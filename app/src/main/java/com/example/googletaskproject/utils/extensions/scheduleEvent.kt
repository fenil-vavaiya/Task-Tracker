package com.example.googletaskproject.utils.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.receiver.EventAlarmReceiver
import com.google.gson.Gson

fun Context.scheduleEvent(eventInfo: TaskItem) {
    val intent = Intent(this, EventAlarmReceiver::class.java).apply {
        putExtra(Const.EVENT_DATA, Gson().toJson(eventInfo))
    }

    val pendingIntent = PendingIntent.getBroadcast(
        this,
        eventInfo.taskId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent) // Cancel existing alarm (if any)

    scheduleAlarm(eventInfo.startTime, alarmManager, pendingIntent)

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
