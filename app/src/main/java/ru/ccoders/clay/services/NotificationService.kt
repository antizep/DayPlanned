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

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val mNotificationId: Int = 1000

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
            return START_STICKY
        }

        val mNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }.apply {
            setContentIntent(pendingIntent)
            val id = intent.getIntExtra(ID, 0)
            if(id == 0){
                return START_STICKY
            }
            val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            var file = File(appGallery!!.absolutePath + "/$id/0.JPG")
            val icon: Bitmap
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    icon = ImageDecoder.decodeBitmap(ImageDecoder.createSource(file))
                } else {
                    icon = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        Uri.fromFile(file)
                    )
                }
            } else {
                icon = BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.pic_dafault
                )
            }
            setSmallIcon(R.mipmap.icon_apolo_round)

            val remoteViews = RemoteViews(packageName, R.layout.shedule_natification)
            //remoteViews.setImageViewBitmap(R.id.ImageScheduleNatification, icon)
            remoteViews.setTextViewText(R.id.headerNatification, intent.getStringExtra(HEADER))
            remoteViews.setImageViewBitmap(R.id.iconNatification,BitmapFactory.decodeResource(context.resources,R.mipmap.icon_apolo))
            val cancelIntent = Intent(context, NotificationService::class.java)
            cancelIntent.putExtra("final_id",CANCELL_BUTTON_CODE)
            cancelIntent.putExtra(ID, id)
            remoteViews.setOnClickPendingIntent(
                R.id.buttonNatiCancel,
                PendingIntent.getService(
                    context,
                    CANCELL_BUTTON_CODE,
                    cancelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            val completelIntent = Intent(context, NotificationService::class.java)
            completelIntent.putExtra("final_id",COMPLETE_BUTTON_CODE)
            completelIntent.putExtra(ID, id)
            remoteViews.setOnClickPendingIntent(
                R.id.buttonNatiComplete,
                PendingIntent.getService(
                    context,
                    COMPLETE_BUTTON_CODE,
                    completelIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )


            setCustomContentView(remoteViews)
            setCustomBigContentView(remoteViews)
            setChannelId(CHANNEL_ID)
            setContentTitle(intent.getStringExtra(HEADER))

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
