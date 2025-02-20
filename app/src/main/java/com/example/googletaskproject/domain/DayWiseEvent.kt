package com.example.googletaskproject.domain

import com.example.googletaskproject.data.CalendarEventItem

sealed class DayWiseEvent {
    data class Header(val date: String) : DayWiseEvent()
    data class EventItem(val event: CalendarEventItem) : DayWiseEvent()
}