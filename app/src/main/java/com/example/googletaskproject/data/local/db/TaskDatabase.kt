package com.example.googletaskproject.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.googletaskproject.data.local.dao.TaskDao
import com.example.googletaskproject.data.model.TaskItem

@Database(entities = [TaskItem::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}