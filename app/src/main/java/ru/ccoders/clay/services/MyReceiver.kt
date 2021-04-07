package ru.ccoders.clay.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.model.Schedule
import ru.ccoders.clay.utills.ScheduleUtils


class MyReceiver : BroadcastReceiver() {
    var scheduleController: AddScheduleController? = null

    companion object {
        var HEADER = "header"
        var DESCRIPTION = "description"
        var TIME = "time"
        var ID = "ID"
    }

    override fun onReceive(context: Context, intent: Intent) {
        scheduleController = AddScheduleController(context)
        val notificationService = Intent(context, NotificationService::class.java)
        val schedules = scheduleController!!.getSchedule();

        notificationService.putExtras(intent)
        context.startForegroundService(notificationService)
        Log.d("MyReceiver","this notification:"+intent.getStringExtra(HEADER))

        addAlarmManager(ScheduleUtils.nextTask(schedules)!!,context)
    }



    fun addAlarmManager(schedule: Schedule,context: Context) {
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
        val pendingIntentpi = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }
}

