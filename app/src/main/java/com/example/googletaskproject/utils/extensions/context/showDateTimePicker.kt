package com.example.googletaskproject.utils.extensions.context

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

fun FragmentActivity.showDateTimePicker(calendar: Calendar,onDateTimeSelected: (Calendar) -> Unit) {
    val constraintsBuilder = CalendarConstraints.Builder()
        .setStart(calendar.timeInMillis) // Set minimum date to today

    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Date")
        .setSelection(calendar.timeInMillis)
        .setCalendarConstraints(constraintsBuilder.build())
        .build()

    datePicker.addOnPositiveButtonClickListener { selection ->
        val selectedDate = Calendar.getInstance().apply { timeInMillis = selection }
        showTimePicker(selectedDate, onDateTimeSelected)
    }


    datePicker.show(this.supportFragmentManager, "MATERIAL_DATE_PICKER")
}

private fun FragmentActivity.showTimePicker(
    date: Calendar,
    onDateTimeSelected: (Calendar) -> Unit,

) {
    val calendar = Calendar.getInstance()
    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
        .setMinute(calendar.get(Calendar.MINUTE))
        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
        .setTitleText("Pick Time")
        .build()
    
    timePicker.addOnPositiveButtonClickListener {
        date.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        date.set(Calendar.MINUTE, timePicker.minute)

        if (date.before(Calendar.getInstance())) {
            Toast.makeText(this, "Selected date and time is in the past. Please select a future date and time.", Toast.LENGTH_SHORT).show()
        } else {
            onDateTimeSelected(date)
        }
    }

    timePicker.show(this.supportFragmentManager, "MATERIAL_TIME_PICKER")
}
