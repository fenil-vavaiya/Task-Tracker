package com.example.googletaskproject.domain

sealed class DayWiseEvent {
    data class Header(val date: String) : DayWiseEvent()
    data class EventItem(val event: CalendarEvent) : DayWiseEvent()
}