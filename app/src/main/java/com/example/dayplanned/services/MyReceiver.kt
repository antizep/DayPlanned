package com.example.dayplanned.services

import android.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.dayplanned.MainActivity
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.model.Schedule


class MyReceiver : BroadcastReceiver() {
    var scheduleController: AddScheduleController? = null

    override fun onReceive(context: Context, intent: Intent) {
        scheduleController = AddScheduleController(context)


        val notificationService = Intent(context, NotificationService::class.java)
        context.startForegroundService(notificationService)

    }

    fun addAlarmManager(schedule: Schedule, applicationContext: Context) {
        val alarmManager: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MyReceiver::class.java)
        myIntent.putExtra("header", schedule.header);
        myIntent.putExtra("body", schedule.description);
        val pendingIntentpi = PendingIntent.getBroadcast(applicationContext, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }

    fun minSchedule(unsorted: MutableList<Schedule>): Schedule? {

        var schedule: Schedule?
        var ph = -1
        var pm = -1
        schedule = null;
        unsorted.forEach {
            if (schedule == null) {
                schedule = it;
                ph = it.getHour();
                pm = it.getMinute()
            } else {
                if (it.getHour() < ph) {
                    schedule = it;
                    ph = it.getHour();
                    pm = it.getMinute();
                } else if (it.getHour() == ph && it.getMinute() < pm) {
                    schedule = it;
                    ph = it.getHour();
                    pm = it.getMinute();
                }
            }
        }

        return schedule;
    }

    fun excludeNegative(unsorted: MutableList<Schedule>): MutableList<Schedule> {
        val mutableList: MutableList<Schedule> = mutableListOf();
        val calendar = Calendar.getInstance();

        unsorted.forEach {

            var i = it.getHour();
            if (i > calendar.get(Calendar.HOUR_OF_DAY)) {
                mutableList.add(it);
            } else if (i == calendar.get(Calendar.HOUR_OF_DAY)
                && it.getMinute() > calendar.get(Calendar.MINUTE)
            ) {
                mutableList.add(it);
            }
        }
        return mutableList
    }
}

