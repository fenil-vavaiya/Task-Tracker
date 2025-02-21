package com.example.googletaskproject.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.googletaskproject.data.CalendarEventItem
import com.example.googletaskproject.data.local.dao.EventDao

@Database(entities = [CalendarEventItem::class], version = 1)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}