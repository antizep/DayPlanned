package ru.ccoders.clay.services

import NotificationUtils.Companion.CHANNEL_ID
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import ru.ccoders.clay.R
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.main_activity.MainActivity
import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.utills.ScheduleUtils


class MyReceiver : BroadcastReceiver() {
    var scheduleController: SQLScheduleController? = null

    companion object {
        var HEADER = "header"
        var DESCRIPTION = "description"
        var TIME = "time"
        var ID = "ID"

        private val CANCELL_BUTTON_CODE = 100;
        private val COMPLETE_BUTTON_CODE = 101;
    }

    override fun onReceive(context: Context, intent: Intent) {
        scheduleController = SQLScheduleController(context)
        val schedules = scheduleController!!.getSchedule();



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
            val id = intent.getIntExtra(MyReceiver.ID, 0)
            setSmallIcon(R.drawable.ic_stat_name)

            val remoteViews = RemoteViews(context.packageName, R.layout.shedule_natification)
            remoteViews.setTextViewText(R.id.headerNatification, intent.getStringExtra(MyReceiver.HEADER))

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
            setContentTitle(intent.getStringExtra(MyReceiver.HEADER+":"+intent.getStringExtra(MyReceiver.TIME)))

        }.build()




        val notificationManager = createNotificationManager(context)
        notificationManager.notify(100, mNotification)


        Log.d("MyReceiver","this notification:"+intent.getStringExtra(HEADER))

        addAlarmManager(ScheduleUtils.nextTask(schedules)!!,context)
    }


    fun createNotificationManager(context: Context): NotificationManager{

        val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

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

    fun addAlarmManager(schedule: ScheduleModel,context: Context) {
        if(schedule.time == null){
            return
        }
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(context, MyReceiver::class.java)
        myIntent.putExtra(HEADER,schedule.header)
        myIntent.putExtra(TIME,schedule.getTxtTime())
        myIntent.putExtra(DESCRIPTION,schedule.description)
        myIntent.putExtra(ID,schedule.id)
        Log.d("MyReceiver","next schedule:"+schedule.header+" date:"+schedule.time!!.time.toString())
        val pendingIntentpi = PendingIntent.getBroadcast(context, 2, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }
}

