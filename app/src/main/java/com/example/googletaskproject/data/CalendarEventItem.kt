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
    val eventId: Int = 0,  // Google Calendar event ID
    val title: String = "",
    val description: String = "",
    val startTime: Long = 0,  // Store as timestamp (milliseconds)
    val endTime: Long = 0,    // Store as timestamp (milliseconds)
    val allDay: Boolean = false,
    val calendarId: String = "", // To differentiate calendars
    val location: String = "",
    val eventColor: Int = 0,
) : Parcelable