package com.example.googletaskproject.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.googletaskproject.data.CalendarEventItem
import com.example.googletaskproject.utils.Const.TABLE_NAME
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Dao
interface EventDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<CalendarEventItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(historyItem: CalendarEventItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CalendarEventItem>)

    @Delete
    suspend fun deleteEvent(historyItem: CalendarEventItem)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAllEvent()

    @Query("DELETE FROM $TABLE_NAME WHERE id IN (SELECT id FROM $TABLE_NAME ORDER BY id ASC LIMIT :count)")
    suspend fun deleteOldestEntries(count: Int)

    @Query("SELECT COUNT(*) FROM $TABLE_NAME")
    suspend fun getEventCount(): Int

    @Transaction
    suspend fun replaceAllEventsConcurrently(newEvents: List<CalendarEventItem>) {
        coroutineScope {
            launch { deleteAllEvent() }
            launch { insertEvents(newEvents) }
        }
    }


}