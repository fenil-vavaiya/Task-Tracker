package com.example.googletaskproject.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.googletaskproject.utils.Const
import kotlinx.parcelize.Parcelize

@Entity(Const.TABLE_NAME)
@Parcelize
data class CalendarEventItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,  // Google Calendar event ID
    val title: String,
    val description: String,
    val startTime: Long,  // Store as timestamp (milliseconds)
    val endTime: Long,    // Store as timestamp (milliseconds)
    val allDay: Boolean,
    val calendarId: String, // To differentiate calendars
    val location: String = "",
    val eventColor: Int,
): Parcelable