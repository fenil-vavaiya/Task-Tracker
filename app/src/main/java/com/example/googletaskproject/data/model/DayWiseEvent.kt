package com.example.googletaskproject.data.model

sealed class DayWiseEvent {
    data class Header(val date: String) : DayWiseEvent()
    data class EventItem(val event: TaskItem) : DayWiseEvent()
}