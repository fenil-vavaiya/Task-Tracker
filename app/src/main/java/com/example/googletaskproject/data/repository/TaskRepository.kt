package com.example.googletaskproject.data.repository

import android.util.Log
import com.example.googletaskproject.data.local.dao.TaskDao
import com.example.googletaskproject.data.model.Group
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.utils.Const.TAG
import com.google.android.gms.tasks.Task
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


    fun createGroup(groupId: String): Task<Void> {
        Log.d(TAG, "createGroup: groupId = $groupId")

        val groupData = hashMapOf(
            "members" to listOf<Map<String, Any>>() // Empty list (to be updated later)
        )

        return FirebaseFirestore.getInstance().collection("groups").document(groupId)
            .set(groupData)
    }


    fun addMemberToGroup(groupId: String, newMember: GroupMember): Task<Void> {
        val groupRef = firestore.collection("groups").document(groupId)

        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(groupRef)
            val currentMembers = snapshot.toObject(Group::class.java)?.members ?: emptyList()

            // Convert to mutable list and add the new member
            val updatedMembers = currentMembers.toMutableList()
            if (!updatedMembers.any { it.androidId == newMember.androidId }) { // Prevent duplicates
                updatedMembers.add(newMember)
            }

            // Update Firestore
            transaction.update(groupRef, "members", updatedMembers)
            null
        }
    }

    fun getGroupMembers(groupId: String): Flow<List<GroupMember>> = channelFlow {
        val listener = firestore.collection("groups").document(groupId)
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
                            parentId = memberMap["parentId"] as? String ?: "",
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


    fun getTasks(groupId: String): Flow<List<TaskItem>> = channelFlow {
        // Emit cached tasks first (from Room DB)
        val cachedTasks = taskDao.getTasks().first().map { it.toTaskItem() }
        send(cachedTasks) // Use `send()` inside `channelFlow`

        // Fetch data from Firestore
        val listener = firestore.collection("groups").document(groupId).collection("tasks")
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
        awaitClose { listener.remove() }
    }

    fun getTasksWithCaching(groupId: String): Flow<List<TaskItem>> = channelFlow {
        val listener = firestore.collection("groups").document(groupId).collection("tasks")
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

                    // Send updated tasks
                    trySend(tasks).isSuccess
                }
            }

        // Close the flow when the coroutine is canceled
        awaitClose { listener.remove() }
    }


    // 🔹 Insert a new task (Firestore + Room)
    suspend fun insertTask(groupId: String, task: TaskItem) {
        try {
            // Insert task into Room first
            val insertedTaskId = taskDao.insertTask(task.toTaskEntity()) // Returns new taskId
            val updatedTask = task.copy(taskId = insertedTaskId.toInt()) // Update taskId

            val groupRef = firestore.collection("groups").document(groupId)

            // Ensure the group document exists before adding tasks
            groupRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    groupRef.set(mapOf("createdAt" to System.currentTimeMillis())) // Create the group
                }

                // Now insert the task using the correct (latest) taskId
                groupRef.collection("tasks")
                    .document(updatedTask.taskId.toString()) // Use updated taskId
                    .set(updatedTask, SetOptions.merge()) // Prevent overwriting other fields
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

    suspend fun insertTasks(groupId: String, tasks: List<TaskItem>) {
        try {
            taskDao.clearTasks()
            // Insert all tasks into Room and get their generated IDs
            val insertedTaskIds =
                taskDao.insertTasks(tasks.map { it.toTaskEntity() }) // Returns List<Long>

            // Update taskId for each task using the new Room-generated IDs
            val updatedTasks = tasks.mapIndexed { index, task ->
                task.copy(taskId = insertedTaskIds[index].toInt())
            }

            val groupRef = firestore.collection("groups").document(groupId)

            // Ensure the group document exists before adding tasks
            groupRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    groupRef.set(mapOf("createdAt" to System.currentTimeMillis())) // Create the group
                }

                // Batch write to Firestore for efficiency
                val batch = firestore.batch()
                updatedTasks.forEach { task ->
                    val taskRef = groupRef.collection("tasks").document(task.taskId.toString())
                    batch.set(taskRef, task, SetOptions.merge())
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "All tasks inserted successfully in Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to insert tasks in Firestore: ${e.message}")
                    }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert tasks in Room: ${e.message}") // Handle Room errors
        }
    }


    // 🔹 Update an existing task (Firestore + Room)
    suspend fun updateTask(groupId: String, task: TaskItem) {
        taskDao.updateTask(task.toTaskEntity()) // Update locally in Room

        firestore.collection("groups").document(groupId).collection("tasks")
            .document(task.taskId.toString())
            .set(task, SetOptions.merge()) // Merge updates in Firestore
            .addOnFailureListener { e -> Log.e(TAG, "Failed to update task: ${e.message}") }
    }

    // 🔹 Delete a task (Firestore + Room)
    suspend fun deleteTask(groupId: String, taskId: Int) {
        taskDao.deleteTask(taskId) // Delete locally from Room

        firestore.collection("groups").document(groupId).collection("tasks")
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

