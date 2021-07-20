package ru.ccoders.clay.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.model.TaskModel
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



    fun addAlarmManager(taskModel: TaskModel, context: Context) {
        if(taskModel.time == null){
            return
        }
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(context, MyReceiver::class.java)
        myIntent.putExtra(HEADER,taskModel.header)
        myIntent.putExtra(TIME,taskModel.getTxtTime())
        myIntent.putExtra(DESCRIPTION,taskModel.description)
        myIntent.putExtra(ID,taskModel.id)
        Log.d("MyReceiver","next schedule:"+taskModel.header+" date:"+taskModel.time!!.time.toString())
        val pendingIntentpi = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, taskModel.time!!.timeInMillis, pendingIntentpi)
    }
}

