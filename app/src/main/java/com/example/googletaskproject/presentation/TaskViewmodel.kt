package com.example.googletaskproject.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewmodel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {
    private val _tasksLiveData = MutableLiveData<List<TaskItem>>()
    val tasksLiveData: LiveData<List<TaskItem>> = _tasksLiveData

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

}