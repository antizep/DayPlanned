package ru.ccoders.clay.services

import NotificationUtils.Companion.CHANNEL_ID
import NotificationUtils.Companion.CHANNEL_NAME
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import ru.ccoders.clay.R
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.services.MyReceiver.Companion.DESCRIPTION
import ru.ccoders.clay.services.MyReceiver.Companion.HEADER
import ru.ccoders.clay.services.MyReceiver.Companion.ID
import ru.ccoders.clay.services.MyReceiver.Companion.TIME
import java.io.File


class NotificationService : Service() {

    private var serviceLooper: Looper? = null
    private var context = this;
    private val CANCELL_BUTTON_CODE = 100;
    private val COMPLETE_BUTTON_CODE = 101;
    var scheduleController: AddScheduleController? = null
    private lateinit var nManager: NotificationManager

    companion object {
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

        val context = this as Context
        nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(myChannel)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val runId = intent.getIntExtra("final_id", 0);

        if (runId != 0) {
            Log.d("AHTUNG", "final Schedule")
            if (runId == COMPLETE_BUTTON_CODE) {
                nManager.cancelAll()
                if (scheduleController == null) {
                    scheduleController = AddScheduleController(this)
                }
                scheduleController!!.complete(intent.getIntExtra(ID, 0))
            } else if (runId == CANCELL_BUTTON_CODE) {
                nManager.cancelAll()
                if (scheduleController == null) {
                    scheduleController = AddScheduleController(this)
                }
                scheduleController!!.cancel(intent.getIntExtra(ID, 0))
            }

        }
        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {

    }
}
