package com.example.googletaskproject.ui.screens.home

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import androidx.core.database.getIntOrNull
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.databinding.ActivityMainBinding
import com.example.googletaskproject.domain.CalendarEvent

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        val permission = listOf(Manifest.permission.READ_CALENDAR)
        requestPermissionIfNeeded(
            permission
        ) { allGranted, grantedList, deniedList ->
            if (allGranted) {

                val events = fetchGoogleCalendarEvents(this)
                binding.rv.adapter = EventsListAdapter(events)


            }
        }
    }

    override fun initListeners(view: View) {
    }

    fun fetchGoogleCalendarEvents(context: Context): List<CalendarEvent> {
        val eventsList = mutableListOf<CalendarEvent>()

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.EVENT_COLOR
        )

        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            null,  // Selection (optional filtering)
            null,  // Selection args
            "${CalendarContract.Events.DTSTART} ASC"  // Order by event start time
        )

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                    ?: "No Title"
                val description =
                    it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION))
                val startTime =
                    it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                val allDay =
                    it.getInt(it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1
                val calendarId =
                    it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID))
                val location =
                    it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION))
                val eventColor =
                    it.getIntOrNull(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_COLOR))

                eventsList.add(
                    CalendarEvent(
                        eventId = eventId ?: "",
                        title = title ?: "",
                        description = description ?: "",
                        startTime = startTime ?: 0,
                        endTime = endTime ?: 0,
                        allDay = allDay ?: false,
                        calendarId = calendarId ?: "",
                        location = location ?: "",
                        eventColor = eventColor ?: 0
                    )
                )
            }
        }
        return eventsList
    }


}