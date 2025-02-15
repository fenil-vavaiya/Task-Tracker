package com.example.googletaskproject.domain

import com.example.googletaskproject.R

data class CalendarEvent(
    val eventId: Int,  // Google Calendar event ID
    val title: String,
    val description: String,
    val startTime: Long,  // Store as timestamp (milliseconds)
    val endTime: Long,    // Store as timestamp (milliseconds)
    val allDay: Boolean,
    val calendarId: String, // To differentiate calendars
    val location: String = "",
    val eventColor: Int ,
)