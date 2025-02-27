package com.example.googletaskproject.data.repository

import android.util.Log
import com.example.googletaskproject.data.local.dao.TaskDao
import com.example.googletaskproject.data.model.GroupItem
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.MemberList
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.utils.Const.TAG
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val firestore: FirebaseFirestore, private val taskDao: TaskDao
) {
    // group related business logic

    fun createNewGroup(
        groupItem: GroupItem
    ): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val teamRef = db.collection("teams").document(groupItem.id) // Use provided ID

        return db.runTransaction { transaction ->
            transaction.set(teamRef, groupItem)
            null // Firestore transactions require a return value
        }
    }

    fun addMemberToTeam(teamId: String, newMember: GroupMember): Task<Void> {
        val teamRef = firestore.collection("teams").document(teamId)

        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(teamRef)
            val currentMembers = snapshot.toObject(MemberList::class.java)?.members ?: emptyList()

            // Convert to mutable list and add the new member if not already present
            val updatedMembers = currentMembers.toMutableList()
            if (!updatedMembers.any { it.memberId == newMember.memberId }) { // Prevent duplicates
                updatedMembers.add(newMember)
            }

            // Update Firestore
            transaction.update(teamRef, "members", updatedMembers)
            null
        }
    }


    fun getGroupMembers(groupId: String): Flow<List<GroupMember>> = channelFlow {
        val listener = firestore.collection("teams").document(groupId)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching members: ${error.message}")
                    return@addSnapshotListener
                }

                if (document == null || !document.exists()) {
                    Log.e(TAG, "Document does not exist")
                    trySend(emptyList()).isSuccess // Send empty list instead of crashing
                    return@addSnapshotListener
                }

                val membersList = document.get("members") as? List<Map<String, Any>> ?: emptyList()

                val members = membersList.mapNotNull { memberMap ->
                    try {
                        GroupMember(
                            memberId = memberMap["memberId"] as? String ?: "",
                            androidId = memberMap["androidId"] as? String ?: "",
                            groupId = memberMap["groupId"] as? String ?: "",
                            location = memberMap["location"] as? String ?: "",
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing member: ${e.message}")
                        null // Skip faulty entries
                    }
                }

                // Send updated member list
                trySend(members).isSuccess
            }

        awaitClose { listener.remove() }
    }

    fun getTeamById(teamId: String): Task<GroupItem> {
        val db = FirebaseFirestore.getInstance()
        val teamRef = db.collection("teams").document(teamId)

        return teamRef.get().continueWithTask { task ->
            val document = task.result
            Log.d(TAG, "getTeamById: data = ${document.data.toString()}")
            if (task.isSuccessful && document != null && document.exists()) {
                val group = document.toObject(GroupItem::class.java)
                return@continueWithTask group?.let { Tasks.forResult(it) }
                    ?: Tasks.forException(Exception("Group data is null"))
            } else {
                return@continueWithTask Tasks.forException(Exception("Group not found"))
            }
        }
    }


    // User related business logic

    fun createNewUser(userItem: UserModel): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userItem.email) // Use provided ID

        return db.runTransaction { transaction ->
            transaction.set(userRef, userItem)
            null // Firestore transactions require a return value
        }
    }

    fun getUserById(userId: String): Task<UserModel> {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        return userRef.get().continueWithTask { task ->
            val document = task.result
            if (document != null && document.exists()) {
                val user = document.toObject(UserModel::class.java)
                user?.let { Tasks.forResult(it) }
                    ?: Tasks.forException(Exception("User data is null"))
            } else {
                Tasks.forException(Exception("User not found"))
            }
        }
    }

    // task related business logic


    fun getTasks(groupId: String): Flow<List<TaskItem>> = channelFlow {
        // Emit cached tasks first (from Room DB)
        val cachedTasks = taskDao.getTasks().first().map { it.toTaskItem() }
        send(cachedTasks) // Use `send()` inside `channelFlow`

        // Fetch data from Firestore

        val listener = firestore.collection("teams").document(groupId).collection("teamTasks")
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching tasks: ${error.message}")
                    return@addSnapshotListener
                }

                if (documents != null) {
                    val tasks = documents.map { doc ->
                        TaskItem(
                            taskId = doc.getLong("taskId")?.toInt() ?: 0,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            startTime = doc.getLong("startTime") ?: 0L,
                            taskDuration = doc.getLong("taskDuration")?.toInt() ?: 0,
                            reminderBefore = doc.getLong("reminderBefore")?.toInt() ?: 0,
                            calendarId = doc.getString("calendarId") ?: "",
                            location = doc.getString("location") ?: "",
                            assignedTo = doc.getString("assignedTo") ?: "",
                            isCompleted = doc.getBoolean("completed") ?: false,
                        )
                    }

                    // Cache tasks in Room
                    CoroutineScope(Dispatchers.IO).launch {
                        taskDao.clearTasks()
                        taskDao.insertTasks(tasks.map { it.toTaskEntity() })
                    }

                    // Send updated tasks
                    trySend(tasks).isSuccess
                }
            }
        // Close the flow when the coroutine is canceled
        awaitClose {
            Log.d(TAG, "Firestore listener removed")
            listener.remove()
        }

    }

    // 🔹 Insert a new task (Firestore + Room)
    suspend fun insertTask(teamId: String, task: TaskItem) {
        try {
            val taskId = taskDao.insertTask(task.toTaskEntity()) // Insert into Room and get ID
            val updatedTask = task.copy(taskId = taskId.toInt()) // Update taskId from Room

            val teamRef = firestore.collection("teams").document(teamId)
            val taskRef = teamRef.collection("teamTasks").document(updatedTask.taskId.toString())

            // Ensure the team document exists before adding a task
            teamRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    teamRef.set(mapOf("createdAt" to System.currentTimeMillis())) // Create team if missing
                }

                taskRef.set(updatedTask, SetOptions.merge()) // Merge to avoid overwriting
                    .addOnSuccessListener {
                        Log.d(TAG, "Task inserted successfully in Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to insert task in Firestore: ${e.message}")
                    }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert task in Room: ${e.message}") // Handle Room errors
        }
    }

    suspend fun insertTasks(teamId: String, tasks: List<TaskItem>): Task<Void> {
        val taskCompletionSource = TaskCompletionSource<Void>()

        try {
            taskDao.clearTasks()

            // Insert tasks into Room and get their IDs
            val insertedTaskIds = taskDao.insertTasks(tasks.map { it.toTaskEntity() })

            // Update taskId using the new Room-generated IDs
            val updatedTasks = tasks.mapIndexed { index, task ->
                task.copy(taskId = insertedTaskIds[index].toInt())
            }

            val teamRef = firestore.collection("teams").document(teamId)

            // Ensure the team document exists before adding tasks
            teamRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    teamRef.set(mapOf("createdAt" to System.currentTimeMillis()))
                }

                // Reference to teamTasks subcollection
                val batch = firestore.batch()
                updatedTasks.forEach { task ->
                    val taskRef = teamRef.collection("teamTasks").document(task.taskId.toString())
                    batch.set(taskRef, task, SetOptions.merge())
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "All tasks inserted successfully in Firestore")
                        taskCompletionSource.setResult(null) // Success
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to insert tasks in Firestore: ${e.message}")
                        taskCompletionSource.setException(e) // Pass error to callback
                    }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch team document: ${e.message}")
                taskCompletionSource.setException(e) // Pass Firestore fetch failure
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tasks in Room: ${e.message}")
            taskCompletionSource.setException(e) // Handle Room errors
        }

        return taskCompletionSource.task
    }

    // 🔹 Update an existing task (Firestore + Room)
    suspend fun updateTask(groupId: String, task: TaskItem) {
        taskDao.updateTask(task.toTaskEntity()) // Update locally in Room

        firestore.collection("teams").document(groupId).collection("teamTasks")
            .document(task.taskId.toString())
            .set(task, SetOptions.merge()) // Merge updates in Firestore
            .addOnFailureListener { e -> Log.e(TAG, "Failed to update task: ${e.message}") }
    }

    // 🔹 Delete a task (Firestore + Room)
    suspend fun deleteTask(groupId: String, taskId: Int) {
        taskDao.deleteTask(taskId) // Delete locally from Room

        firestore.collection("teams").document(groupId).collection("teamTasks")
            .document(taskId.toString()).delete()
            .addOnFailureListener { e -> Log.e(TAG, "Failed to delete task: ${e.message}") }
    }

    private fun TaskItem.toTaskEntity() = TaskItem(
        taskId,
        title,
        description,
        startTime,
        taskDuration,
        reminderBefore,
        calendarId,
        location,
        assignedTo
    )
}

