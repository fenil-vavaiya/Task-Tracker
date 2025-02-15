package com.example.googletaskproject.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.googletaskproject.R
import com.example.googletaskproject.ui.screens.alwaysondisplay.AlwaysOnDisplayActivity

class OverlayService : Service() {

    val CHANNEL_ID = "alwaysOnDisplay"


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        registerOverlayReceiver()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID, "Always On Display", NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("" + R.string.app_name)
                .setContentText("").build()
        startForeground(1, notification)
    }

    private fun registerOverlayReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(overlayReceiver, filter)
    }

    private val overlayReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_SCREEN_OFF) {
                showOverlayActivity(context)
            } else if (action == Intent.ACTION_USER_PRESENT) {
                showOverlayActivity(context)
            }
        }
    }

    private fun showOverlayActivity(context: Context) {
        Intent(context, AlwaysOnDisplayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }


    }

    override fun onDestroy() {
        unregisterOverlayReceiver()
        super.onDestroy()
    }

    private fun unregisterOverlayReceiver() {
        unregisterReceiver(overlayReceiver)
    }

}