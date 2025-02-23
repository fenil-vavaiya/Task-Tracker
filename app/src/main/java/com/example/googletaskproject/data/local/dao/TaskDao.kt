package com.example.googletaskproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.utils.Const.TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    @Query("SELECT * FROM $TABLE_NAME")
    fun getTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskItem>): List<Long>// Returns list of inserted IDs



    @Update
    suspend fun updateTask(task: TaskItem)

    @Query("DELETE FROM $TABLE_NAME WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun clearTasks()

}