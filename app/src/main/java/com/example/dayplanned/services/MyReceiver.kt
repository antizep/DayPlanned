package com.example.dayplanned.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService


class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = getSystemService(context, NotificationManager::class.java)
        val CHANNEL_ID = "my_channel_01"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "my_channel"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(1500,50,1500)
            mChannel.setShowBadge(false)
            notificationManager!!.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).setAutoCancel(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(".!.")
            .setContentText("ЯБААААААААААААТЬ!!!!")

        val notification: Notification = builder.build()

        if (notificationManager != null) {
            notificationManager.notify(1, notification)
        }
        Log.d("MyReceiver", "AHTUNG")
    }
}