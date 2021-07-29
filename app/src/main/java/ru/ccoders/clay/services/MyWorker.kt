package ru.ccoders.clay.services

import NotificationUtils.Companion.CHANNEL_ID
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import ru.ccoders.clay.MainActivity
import ru.ccoders.clay.R
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.model.TaskModel
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File
import java.time.Duration
import java.util.*


class MyWorker(private val context:Context,private val workerParameters: WorkerParameters): Worker(context,workerParameters) {
    var scheduleController: AddScheduleController = AddScheduleController(context)

    private val CANCELL_BUTTON_CODE = 100;
    private val COMPLETE_BUTTON_CODE = 101;

    override fun doWork(): Result {

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val mNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }.apply {
            setContentIntent(pendingIntent)
            val id = inputData.getInt(MyReceiver.ID, 0)
            setSmallIcon(R.drawable.ic_stat_name)

            val remoteViews = RemoteViews(context.packageName, R.layout.shedule_natification)
            remoteViews.setTextViewText(R.id.headerNatification, inputData.getString(MyReceiver.HEADER))

            val cancelIntent = Intent(context, NotificationService::class.java)

            cancelIntent.putExtra("final_id",CANCELL_BUTTON_CODE)
            cancelIntent.putExtra(MyReceiver.ID, id)
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
            completelIntent.putExtra(MyReceiver.ID, id)
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
            setContentTitle(inputData.getString(MyReceiver.HEADER))

        }.build()


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(100, mNotification)
        nextWork()
        return Result.success()
    }
    fun nextWork(){
        val schedules = scheduleController!!.getSchedule();
        addAlarmManager(ScheduleUtils.nextTask(schedules)!!)
    }

    fun addAlarmManager(taskModel: TaskModel) {
        WorkManager.getInstance().cancelAllWorkByTag("natWorker");
        val data  = Data.Builder().putInt(MyReceiver.ID,taskModel.id)
            .putString(MyReceiver.HEADER,taskModel.header)
            .build()
        val onTimeWorkRequest =    OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInitialDelay(Duration.ofMillis(taskModel.time!!.timeInMillis - Date().time))
            .addTag("natWorker")
            .setInputData(data)
            .build()
        val workManager = WorkManager.getInstance()
        workManager.enqueue(onTimeWorkRequest)
    }

}