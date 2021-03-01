package com.example.dayplanned.services

import NotificationUtils.Companion.CHANNEL_ID
import NotificationUtils.Companion.CHANNEL_NAME
import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.services.MyReceiver.Companion.DESCRIPTION
import com.example.dayplanned.services.MyReceiver.Companion.HEADER
import com.example.dayplanned.services.MyReceiver.Companion.ID
import java.io.File


class NotificationService : Service() {

    private var serviceLooper: Looper? = null
    private var context = this;
    var scheduleController: AddScheduleController? = null
    private lateinit var nManager :NotificationManager


    companion object{
        val myChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            lightColor = Color.GREEN
            lockscreenVisibility = Notification.VISIBILITY_SECRET
        }

    }
    override fun onCreate() {
        /*// Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }*/
        val context = this as Context
        nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(myChannel)
    }
        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

            val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this)


            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val mNotificationId: Int = 1000

            val  runId= intent.getIntExtra("final_id",0);

            if(runId != 0){
                Log.d("AHTUNG", "final Schedule")
                nManager.cancelAll()
                if(scheduleController == null) {
                    scheduleController = AddScheduleController(this)
                }
                scheduleController!!.complete(intent.getIntExtra(ID,0))
                return Service.START_STICKY
            }

            intent.putExtra("final_id",1000);
            val replyPendingIntent = PendingIntent.getService(
                applicationContext,
                100, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val action = Notification.Action.Builder(
                R.drawable.ic_menu_send,
                "выполнить",replyPendingIntent).build()

            val mNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
            } else {
                Notification.Builder(context)
            }.apply {
                setContentIntent(pendingIntent)
                val id = intent.getIntExtra(ID,0)
                val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                var file = File(appGallery!!.absolutePath + "/$id/0.JPG")
                val icon: Bitmap
                if(file.exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        icon = ImageDecoder.decodeBitmap(ImageDecoder.createSource(file))
                    } else {
                        icon = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                    }
                }else {
                    icon = BitmapFactory.decodeResource(
                        context.resources,
                        R.mipmap.sym_def_app_icon
                    )
                }
                setSmallIcon(R.mipmap.sym_def_app_icon)
                setLargeIcon(
                    icon
                )
                addAction(action)
                setChannelId(CHANNEL_ID)
                setContentTitle(intent.getStringExtra(HEADER))
                setStyle(Notification.BigTextStyle().bigText(intent.getStringExtra(DESCRIPTION)))
                setContentText(intent.getStringExtra(DESCRIPTION))
            }.build()
            Notification.DEFAULT_VIBRATE
            nManager.notify(mNotificationId, mNotification)
            return Service.START_STICKY
        }


    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}
