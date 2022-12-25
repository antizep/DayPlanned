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
import ru.ccoders.clay.activities.MainFragment
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.utills.ScheduleUtils
import java.time.LocalDate


class MyReceiver : BroadcastReceiver() {
    val TAG = MyReceiver::class.java.name
    var SQLiteScheduleController: SQLiteScheduleController? = null

    companion object {
        var HEADER = "header"
        var DESCRIPTION = "description"
        var TIME = "time"
        var ID = "ID"
        var notificationManager: NotificationManager? = null
        private val CANCELL_BUTTON_CODE = 100;
        private val COMPLETE_BUTTON_CODE = 101;
        fun createNotificationManager(context: Context): NotificationManager {
            if (notificationManager == null) {
                notificationManager =
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!

                val channel = NotificationChannel(
                    CHANNEL_ID, "Clay tasks channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = "Кнанал для уведомлений о задаче"
                channel.enableLights(true)
                channel.lightColor = Color.RED
                channel.enableVibration(true)
                notificationManager!!.createNotificationChannel(channel)
            }

            return notificationManager as NotificationManager
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        SQLiteScheduleController = SQLiteScheduleController(context)
        val schedules = SQLiteScheduleController!!.getSchedule();
        val id = intent.getIntExtra(MyReceiver.ID, 0)
        if (isSkipSchedule(schedules, id)) {
            notificationManager!!.cancelAll();
            addAlarmManager(ScheduleUtils.nextTask(schedules)!!, context)
            return
        }


        val notificationIntent = Intent(context, MainFragment::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                0, notificationIntent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val mNotification =
            Notification.Builder(context, CHANNEL_ID)
                .apply {
                    setContentIntent(pendingIntent)


                    val remoteViews =
                        RemoteViews(context.packageName, R.layout.shedule_natification)
                    remoteViews.setTextViewText(
                        R.id.headerNatification,
                        intent.getStringExtra(MyReceiver.HEADER)
                    )

                    val cancelIntent = Intent(context, NotificationService::class.java)
                    setSmallIcon(R.drawable.edit_button)
                    cancelIntent.putExtra("final_id", CANCELL_BUTTON_CODE)
                    cancelIntent.putExtra(MyReceiver.ID, id)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        remoteViews.setOnClickPendingIntent(
                            R.id.buttonNatiCancel,
                            PendingIntent.getService(
                                context,
                                CANCELL_BUTTON_CODE,
                                cancelIntent,
                                PendingIntent.FLAG_MUTABLE
                            )
                        )
                    }else{
                        remoteViews.setOnClickPendingIntent(
                            R.id.buttonNatiCancel,
                            PendingIntent.getService(
                                context,
                                CANCELL_BUTTON_CODE,
                                cancelIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                    }

                    val completelIntent = Intent(context, NotificationService::class.java)
                    completelIntent.putExtra("final_id", COMPLETE_BUTTON_CODE)
                    completelIntent.putExtra(MyReceiver.ID, id)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        remoteViews.setOnClickPendingIntent(
                            R.id.buttonNatiComplete,
                            PendingIntent.getService(
                                context,
                                COMPLETE_BUTTON_CODE,
                                completelIntent,
                                PendingIntent.FLAG_MUTABLE
                            )
                        )
                    }else{
                        remoteViews.setOnClickPendingIntent(
                            R.id.buttonNatiComplete,
                            PendingIntent.getService(
                                context,
                                COMPLETE_BUTTON_CODE,
                                completelIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                    }

                    setCustomContentView(remoteViews)
                    setCustomBigContentView(remoteViews)
                    setChannelId(CHANNEL_ID)
                    setContentTitle(
                        intent.getStringExtra(
                            MyReceiver.HEADER + ":" + intent.getStringExtra(
                                MyReceiver.TIME
                            )
                        )
                    )

                }.build()


        val notificationManager = createNotificationManager(context)
        notificationManager.notify(100, mNotification)


        Log.d("MyReceiver", "this notification:" + intent.getStringExtra(HEADER))

        addAlarmManager(ScheduleUtils.nextTask(schedules)!!, context)
    }


    fun isSkipSchedule(schedules: MutableList<ScheduleModel>, id: Int): Boolean {
        for (it in schedules) {
            if (it.id == id) {
                val date = LocalDate.now();
                if (it.completeDate == null) return false;
                if (it.completeDate!!.dayOfYear == date.dayOfYear && it.completeDate!!.year == date.year) {
                    Log.d(TAG,"skip message:"+it.toJSONObject())
                    return true;
                }
            }
        }
        return false;
    }

    fun addAlarmManager(schedule: ScheduleModel, context: Context) {
        if (schedule.time == null) {
            return
        }
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(context, MyReceiver::class.java)
        myIntent.putExtra(HEADER, schedule.header)
        myIntent.putExtra(TIME, schedule.getTxtTime())
        myIntent.putExtra(DESCRIPTION, schedule.description)
        myIntent.putExtra(ID, schedule.id)
        Log.d(
            "MyReceiver",
            "next schedule:" + schedule.header + " date:" + schedule.time!!.time.toString()
        )
        val pendingIntentpi = PendingIntent.getBroadcast(
            context, 2, myIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        );
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }
}