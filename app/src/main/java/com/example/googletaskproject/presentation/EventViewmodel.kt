package com.example.googletaskproject.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.googletaskproject.data.CalendarEventItem
import com.example.googletaskproject.data.repository.EventRepository
import com.example.googletaskproject.utils.helper.CalendarHelper.filterEventsByExactDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewmodel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {

    val allEvents: LiveData<List<CalendarEventItem>> = eventRepository.getAllEvents()

    fun insertHistory(history: CalendarEventItem, maxHistory: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.insertEvent(
                history,
                maxHistory
            )
        }

    suspend fun updateAllEvents(events: List<CalendarEventItem>) {
        eventRepository.updateEvents(events)
    }

    fun deleteHistory(history: CalendarEventItem) =
        viewModelScope.launch(Dispatchers.IO) { eventRepository.deleteEvent(history) }

    fun deleteAllHistory() =
        viewModelScope.launch(Dispatchers.IO) { eventRepository.deleteAllEvent() }

    val todayEvents: LiveData<List<CalendarEventItem>> = allEvents.map { events ->
        filterEventsByExactDate(events, LocalDate())
    }


}