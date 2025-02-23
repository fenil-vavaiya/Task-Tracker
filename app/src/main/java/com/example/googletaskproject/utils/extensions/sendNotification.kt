package com.example.googletaskproject.utils.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.googletaskproject.R
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.utils.Const


fun Context.sendNotification(title: String, messageBody: String) {
    val notificationId = System.currentTimeMillis().toInt()
    val soundUri = getSoundUri()

    // Generate a unique channel ID each time to ensure updated settings
    val channelId = "notification_channel_${soundUri.hashCode()}"

    val intent = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        putExtra("pushnotification", "yes")
    }

    val pendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Check if the channel exists; if not, create a new one
    if (notificationManager.getNotificationChannel(channelId) == null) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(channelId, "Reminder Notifications", importance).apply {
            description = messageBody
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
            setSound(soundUri, audioAttributes)
        }

        notificationManager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(this, channelId)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        .setAutoCancel(true)
        .setSound(soundUri)
        .setVibrate(longArrayOf(0, 250, 250, 250))
        .setColor(Color.parseColor("#FFD600"))
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notificationManager.notify(notificationId, notificationBuilder.build())
}


private fun Context.getSoundUri(): Uri {
    val rawRes =
        SessionManager.getObject(Const.RINGTONE_MUSIC, TaskRingtoneModel::class.java)?.assetName
    return if (rawRes == -1) {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    } else {
        Uri.parse("android.resource://" + applicationContext.packageName + "/" + rawRes)
    }
}



