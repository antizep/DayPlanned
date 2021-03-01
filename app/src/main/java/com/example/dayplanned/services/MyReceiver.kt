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

    companion object {
        var HEADER = "header"
        var DESCRIPTION = "description"
        var ID = "ID"
    }

    override fun onReceive(context: Context, intent: Intent) {
        scheduleController = AddScheduleController(context)
        val notificationService = Intent(context, NotificationService::class.java)
        var schedules = scheduleController!!.getSchedule();
        schedules = sortByThisTime(schedules);
        notificationService.putExtras(intent)
        context.startForegroundService(notificationService)
        Log.d("MyReceiver","this notification:"+intent.getStringExtra(HEADER))

        addAlarmManager(schedules[0],context)
    }



    fun addAlarmManager(schedule: Schedule,context: Context) {
        if(schedule.time == null){
            return
        }
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(context, MyReceiver::class.java)
        myIntent.putExtra(HEADER,schedule.header)
        myIntent.putExtra(DESCRIPTION,schedule.description)
        myIntent.putExtra(ID,schedule.id)
        Log.d("MyReceiver","next schedule:"+schedule.header+" date:"+schedule.time!!.time.toString())
        val pendingIntentpi = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }

    fun sortByThisTime(unsorted: MutableList<Schedule>): MutableList<Schedule> {

        val positive: MutableList<Schedule> = excludeNegative(unsorted);
        unsorted.removeAll(positive);
        val sorted = mutableListOf<Schedule>()
        while (positive.size>0){
            val min:Schedule? = minSchedule(positive);

            sorted.add(min!!)
            positive.remove(min)
        }
        while (unsorted.size>0){
            val min:Schedule? = minSchedule(unsorted);

            sorted.add(min!!)
            min.time!!.add(Calendar.DAY_OF_YEAR,1);
            unsorted.remove(min)
        }
        return sorted
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
                }else if(it.getHour() == ph && it.getMinute()< pm){
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

