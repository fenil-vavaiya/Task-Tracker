package com.example.googletaskproject.data.repository

import com.example.googletaskproject.data.CalendarEventItem
import com.example.googletaskproject.data.local.dao.EventDao
import javax.inject.Inject

class EventRepository @Inject constructor(private val eventDao: EventDao) {

    fun getAllEvent() = eventDao.getAllEvent()
    suspend fun deleteEvent(historyItem: CalendarEventItem) = eventDao.deleteEvent(historyItem)
    suspend fun deleteAllEvent() = eventDao.deleteAllEvent()

    suspend fun insertEvent(eventItem: CalendarEventItem, maxEvent: Int) {
        val count = eventDao.getEventCount()
        if (count >= maxEvent) {
            val removeCount = count - maxEvent + 1
            eventDao.deleteOldestEntries(removeCount)
        }
        eventDao.insertEvent(
            eventItem
        )
    }

}