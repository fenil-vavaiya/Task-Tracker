package com.example.googletaskproject.domain

data class CalendarEvent(
    val eventId: String,  // Google Calendar event ID
    val title: String,
    val description: String,
    val startTime: Long,  // Store as timestamp (milliseconds)
    val endTime: Long,    // Store as timestamp (milliseconds)
    val allDay: Boolean,
    val calendarId: String, // To differentiate calendars
    val location: String = "",
    val eventColor: Int = 0 // Optional color for event
)