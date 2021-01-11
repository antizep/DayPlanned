package com.example.dayplanned.services
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var context = this;
    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                while(true){
                    val notificationManager =
                        ContextCompat.getSystemService(context, NotificationManager::class.java)
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
                        mChannel.vibrationPattern = longArrayOf(100,50,100)
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
                    Log.d("AHD","LOOP")
                    Thread.sleep(10000)

                }

            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}
