package com.example.googletaskproject.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.ui.screens.home.TaskAlarmActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.google.gson.Gson


class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let { it ->
            it.getStringExtra(Const.EVENT_DATA)?.let {
                val event = Gson().fromJson(it, TaskItem::class.java)
//                context?.sendNotification(event.title, event.description)
                Log.d(TAG, "onReceive: starting alarm activity ")
                Intent(context, TaskAlarmActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Const.EVENT_DATA, Gson().toJson(event))
                    context.startActivity(this)
                }
            }
        }
    }

}