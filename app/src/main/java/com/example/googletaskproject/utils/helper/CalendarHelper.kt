package com.example.googletaskproject.utils.helper

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.database.getIntOrNull
import com.example.googletaskproject.data.model.DayWiseEvent
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.model.UserModel
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CalendarHelper {

    fun getDayWiseEvents(events: List<TaskItem>): Map<String, List<TaskItem>> {
        val groupedEvents = events.groupBy { event ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            sdf.format(Date(event.startTime))
        }
        return groupedEvents
    }

    fun prepareSectionedList(dayWiseMap: Map<String, List<TaskItem>>): List<DayWiseEvent> {
        val sectionedList = mutableListOf<DayWiseEvent>()

        dayWiseMap.forEach { (date, events) ->
            sectionedList.add(DayWiseEvent.Header(date))  // Add header
            events.forEach { event ->
                sectionedList.add(DayWiseEvent.EventItem(event))  // Add each event
            }
        }
        return sectionedList
    }

    fun fetchGoogleCalendarEvents(context: Context, userModel: UserModel): List<TaskItem> {
        val eventsList = mutableListOf<TaskItem>()

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

        // Get the Calendar ID for the specified email
        val calendarId = getCalendarIdForEmail(context, userModel.email)
            ?: return emptyList() // Return empty if no calendar found for the email

        val selection = "${CalendarContract.Events.CALENDAR_ID} = ?"
        val selectionArgs = arrayOf(calendarId.toString())

        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Events.DTSTART} ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getIntOrNull(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                    ?: "No Title"
                val description =
                    it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION))
                val startTime =
                    it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                val allDay =
                    it.getInt(it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1
                val location =
                    it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION))
                val eventColor =
                    it.getIntOrNull(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_COLOR))

                eventsList.add(
                    TaskItem(
                        taskId = eventId ?: 0,
                        title = title,
                        description = description ?: "",
                        startTime = startTime,
                        calendarId = calendarId.toString(),
                        location = location ?: userModel.location,
                        assignedTo = userModel.userId
                    )
                )
            }
        }
        return eventsList
    }

    private fun getCalendarIdForEmail(context: Context, email: String): Long? {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val selection = "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?"
        val selectionArgs = arrayOf(email)

        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, projection, selection, selectionArgs, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
            }
        }
        return null
    }

    fun filterEventsForTimePeriod(
        events: List<TaskItem>, startTime: Long, endTime: Long
    ): List<TaskItem> {
        return events.filter { event ->
            (event.startTime in startTime..endTime) || (event.taskDuration in startTime..endTime) || (event.startTime < startTime && event.taskDuration > endTime) // Full overlap case
        }.sortedBy { it.startTime } // Sort by start time
    }

    fun sortFutureEvents(eventsList: List<TaskItem>): List<TaskItem> {
        val currentTime = System.currentTimeMillis()

        return eventsList
            .filter { it.startTime >= currentTime } // Filter only future events including today
            .sortedBy { it.startTime } // Sort by start time in ascending order
    }

    /* fun sortFutureEvents(eventsList: List<CalendarEvent>): List<CalendarEvent> { // FOR TESTING PURPOSE
         val calendar = Calendar.getInstance()
         calendar.set(Calendar.HOUR_OF_DAY, 0)
         calendar.set(Calendar.MINUTE, 0)
         calendar.set(Calendar.SECOND, 0)
         calendar.set(Calendar.MILLISECOND, 0)

         val todayStartTime = calendar.timeInMillis // Midnight of today

         return eventsList
             .filter { it.startTime >= todayStartTime } // Include all events from today onwards
             .sortedBy { it.startTime } // Sort by start time in ascending order
     }*/
    fun filterEventsByExactDate(
        events: List<TaskItem>,
        targetDate: LocalDate
    ): List<TaskItem> {
        return events.filter { event ->
            val eventDate =
                LocalDate(event.startTime, DateTimeZone.getDefault()) // Extract only date
            eventDate == targetDate // Only include events for the exact date
        }.sortedBy { it.startTime } // Sort events by start time
    }

    fun filterEventsByLocalDate(
        events: List<TaskItem>,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: DateTimeZone = DateTimeZone.getDefault() // Use system default timezone
    ): List<TaskItem> {
        val startTime = startDate.toDateTimeAtStartOfDay(timeZone).millis
        val endTime =
            endDate.plusDays(1).toDateTimeAtStartOfDay(timeZone).millis // Include full last day

        return events.filter { event ->
            (event.startTime in startTime until endTime) ||
                    (event.taskDuration in startTime until endTime) ||
                    (event.startTime < startTime && event.taskDuration > endTime) // Full overlap case
        }.sortedBy { it.startTime } // Sort by start time
    }


}