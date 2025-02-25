package com.example.googletaskproject.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.repository.TaskRepository
import com.example.googletaskproject.utils.Const.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewmodel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> = _tasksLiveData

    private val _groupMembers = MutableLiveData<List<GroupMember>>()
    val groupMembers: LiveData<List<GroupMember>> get() = _groupMembers

    fun fetchTasks(groupId: String) {
        viewModelScope.launch {
            taskRepository.getTasks(groupId).collect { taskList ->
                _tasksLiveData.postValue(taskList)
            }
        }
    }

    fun addTask(groupId: String, task: TaskItem) {
        viewModelScope.launch { taskRepository.insertTask(groupId, task) }
    }

    fun addTaskList(groupId: String, tasks: List<TaskItem>) {
        viewModelScope.launch { taskRepository.insertTasks(groupId, tasks) }
    }

    fun updateTask(groupId: String, task: TaskItem) {
        viewModelScope.launch { taskRepository.updateTask(groupId, task) }
    }

    fun deleteTask(groupId: String, taskId: Int) {
        viewModelScope.launch { taskRepository.deleteTask(groupId, taskId) }
    }

    fun createNewGroup(groupId: String) {
        viewModelScope.launch {
            taskRepository.createGroup(groupId)
                .addOnSuccessListener {
                    Log.d(TAG, "Group created successfully")
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Error creating group: ${error.message}")
                }
        }
    }

    fun addMember(groupId: String, newMember: GroupMember) {
        viewModelScope.launch {
            taskRepository.addMemberToGroup(groupId, newMember)
                .addOnSuccessListener {
                    Log.d(TAG, "Member added successfully")
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Error adding member: ${error.message}")
                }
        }
    }

    fun fetchGroupMembers(groupId: String) {
        viewModelScope.launch {
            taskRepository.getGroupMembers(groupId).collect { members ->
                _groupMembers.value = members
            }
        }
    }

}