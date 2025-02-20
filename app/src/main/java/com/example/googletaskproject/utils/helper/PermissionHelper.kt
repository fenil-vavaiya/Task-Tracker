package com.example.googletaskproject.utils.helper

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {

    fun areAllPermissionsGranted(context: Context): Boolean {
        val permission = when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            Build.VERSION_CODES.TIRAMISU -> arrayOf(
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.POST_NOTIFICATIONS
            )

            else -> arrayOf(
                android.Manifest.permission.READ_CALENDAR
            )
        }

        return permission.all {
            if (it == android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
                canScheduleExactAlarms(context)
            } else {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            return alarmManager.canScheduleExactAlarms()
        }
        return true
    }
}