package com.example.googletaskproject.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.googletaskproject.domain.CalendarEvent
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.extensions.sendNotification
import com.google.gson.Gson

class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { it ->
            it.getStringExtra(Const.EVENT_DATA)?.let {
                val event = Gson().fromJson(it, CalendarEvent::class.java)
                context?.sendNotification(event.title, event.description)
            }
        }
    }

}