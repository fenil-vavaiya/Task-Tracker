package com.example.googletaskproject.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googletaskproject.data.model.GroupItem
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.data.repository.TaskRepository
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.helper.UserInfoHelper.getUserInfo
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewmodel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> = _tasksLiveData

    private val _groupMembers = MutableLiveData<List<GroupMember>>()
    val groupMembers: LiveData<List<GroupMember>> get() = _groupMembers

    fun fetchTasks() {
        viewModelScope.launch {
            taskRepository.getTasks(getUserInfo().groupId).collect { taskList ->
                _tasksLiveData.postValue(taskList)
            }
        }
    }

    fun addTask(task: TaskItem) {
        viewModelScope.launch { taskRepository.insertTask(getUserInfo().groupId, task) }
    }

    fun addTaskList(tasks: List<TaskItem>) {
        viewModelScope.launch { taskRepository.insertTasks(getUserInfo().groupId, tasks) }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch { taskRepository.updateTask(getUserInfo().groupId, task) }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch { taskRepository.deleteTask(getUserInfo().groupId, taskId) }
    }

    fun createNewGroup(groupItem: GroupItem) {
        viewModelScope.launch {
            taskRepository.createNewGroup(groupItem).addOnSuccessListener {
                Log.d(TAG, "Group created successfully")
            }.addOnFailureListener { error ->
                Log.e(TAG, "Error creating group: ${error.message}")
            }
        }
    }

    fun addMember(newMember: GroupMember) {
        viewModelScope.launch {
            taskRepository.addMemberToTeam(getUserInfo().groupId, newMember).addOnSuccessListener {
                Log.d(TAG, "Member added successfully")
            }.addOnFailureListener { error ->
                Log.e(TAG, "Error adding member: ${error.message}")
            }
        }
    }

    fun fetchGroupMembers() {
        viewModelScope.launch {
            taskRepository.getGroupMembers(getUserInfo().groupId).collect { members ->
                _groupMembers.value = members
            }
        }
    }

    fun findTeam(userId: String): Task<GroupItem> {
        return taskRepository.getTeamById(userId)
    }

    fun isTeamExists(userId: String, callback: (Boolean) -> Unit) {
        taskRepository.getTeamById(userId).addOnSuccessListener { callback(true) }
            .addOnFailureListener {
                Log.d(TAG, "isTeamExists: message = ${it.message}"); callback(
                false
            )
            }
    }


    fun createNewUser(userItem: UserModel) {
        viewModelScope.launch {
            taskRepository.createNewUser(userItem).addOnSuccessListener {
                Log.d(TAG, "User created successfully")
            }.addOnFailureListener { error ->
                Log.e(TAG, "Error creating User: ${error.message}")
            }
        }
    }

    fun getUser(email: String): Task<UserModel> {
        return taskRepository.getUserById(email)
    }
}