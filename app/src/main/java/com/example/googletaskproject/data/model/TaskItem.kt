package com.example.googletaskproject.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.googletaskproject.utils.Const
import kotlinx.parcelize.Parcelize

@Entity(Const.TABLE_NAME)
@Parcelize
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    var title: String = "",
    val description: String = "",
    var startTime: Long = 0,  // Store as timestamp (milliseconds)
    var taskDuration: Int = 0,    // Store as timestamp (milliseconds)
    var reminderBefore: Int = 0,
    val calendarId: String = "", // To differentiate calendars
    var location: String = "",
    val assignedTo: String = "",
    var isCompleted: Boolean = false,
) : Parcelable {
    fun toTaskItem() = TaskItem(
        taskId,
        title,
        description,
        startTime,
        taskDuration,
        reminderBefore,
        calendarId,
        location,
        assignedTo,
        isCompleted
    )
}