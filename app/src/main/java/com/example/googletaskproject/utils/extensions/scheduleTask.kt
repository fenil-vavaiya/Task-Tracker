package com.example.googletaskproject.utils.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.receiver.EventAlarmReceiver
import com.google.gson.Gson

fun Context.scheduleTask(taskItem: TaskItem) {
    val intent = Intent(this, EventAlarmReceiver::class.java).apply {
        putExtra(Const.TASK_DATA, Gson().toJson(taskItem))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        taskItem.taskId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    Log.d(TAG, "scheduleTask: taskId = ${taskItem.taskId}")

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent) // Cancel existing alarm (if any)

    scheduleAlarm(taskItem.startTime, taskItem.reminderBefore, alarmManager, pendingIntent)

}

fun Context.scheduleTaskWithoutReminder(taskItem: TaskItem) {
    val intent = Intent(this, EventAlarmReceiver::class.java).apply {
        putExtra(Const.TASK_DATA, Gson().toJson(taskItem))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        taskItem.taskId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    Log.d(TAG, "scheduleTask: taskId = ${taskItem.taskId}")

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent) // Cancel existing alarm (if any)

    scheduleAlarm(taskItem.startTime, 0, alarmManager, pendingIntent)

}

private fun scheduleAlarm(
    startTime: Long,
    reminderBeforeMeeting: Int,
    alarmManager: AlarmManager,
    pendingIntent: PendingIntent
) {
    val reminderTimeInMillis = startTime - (reminderBeforeMeeting.toLong() * 60 * 1000)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent)
        }
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent)
    }
}



fun Context.cancelScheduledAlarm(taskId: Int) {
    val intent = Intent(this, EventAlarmReceiver::class.java)
    Log.d(TAG, "cancelScheduledAlarm: taskId = ${taskId}")
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        taskId, // Must match the taskId used in scheduling
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

private fun scheduleAlarm(
    startTime: Long, alarmManager: AlarmManager, pendingIntent: PendingIntent
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent)
        }
    } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent)
    }
}
