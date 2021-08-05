package ru.ccoders.clay.services

import NotificationUtils.Companion.CHANNEL_ID
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.work.*
import ru.ccoders.clay.main_activity.MainActivity
import ru.ccoders.clay.R
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.utills.ScheduleUtils
import java.time.Duration
import java.util.*


class MyWorker(private val context:Context,private val workerParameters: WorkerParameters): Worker(context,workerParameters) {
    var scheduleController: AddScheduleController = AddScheduleController(context)

    private val CANCELL_BUTTON_CODE = 100;
    private val COMPLETE_BUTTON_CODE = 101;
    private lateinit var notificationManager:NotificationManager

    override fun doWork(): Result {
        Log.d("MyWorker","doWork()")
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
            setContentTitle(inputData.getString(MyReceiver.HEADER+":"+inputData.getString(MyReceiver.TIME)))

        }.build()




        val notificationManager = createNotificationManager()
        notificationManager.notify(100, mNotification)
        nextWork()
        return Result.success()
    }

    fun createNotificationManager(): NotificationManager{

        notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Clay tasks channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Кнанал для уведомлений о задаче"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }

    fun nextWork(){
        val schedules = scheduleController.getSchedule();
        addAlarmManager(ScheduleUtils.nextTask(schedules)!!,context)
    }
    companion object {
        fun addAlarmManager(scheduleModel: ScheduleModel, context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("natWorker");
            val constraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .build()

            val data = Data.Builder().putInt(MyReceiver.ID, scheduleModel.id)
                .putString(MyReceiver.HEADER, scheduleModel.header)
                .putString(MyReceiver.TIME,scheduleModel.getTxtTime())
                .build()
            val time = scheduleModel.time!!.timeInMillis - Date().time
            val t = Duration.ofMillis(scheduleModel.time!!.timeInMillis).minusMillis(System.currentTimeMillis())

            Log.d("MyWorker", "next task timer calculate:${t.toMinutes()}")
            Log.d("MyWorker", "task time in schedule:${scheduleModel.getTxtTime()}")
            val onTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setInitialDelay(t)
                .addTag("natWorker")
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            val workManager = WorkManager.getInstance()
            workManager.enqueue(onTimeWorkRequest)
        }
    }

}