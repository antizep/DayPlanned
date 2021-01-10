package com.example.dayplanned

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivityMainBinding
import com.example.dayplanned.databinding.SheduleLayoutBinding
import com.example.dayplanned.services.MyReceiver

class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        scheduleController = AddScheduleController(this)

        loadSchedule();

        activityMainBinding.createNewButton.setOnClickListener {
            startActivity(Intent(this, AddScheduleActivity::class.java));
        }
        //todo вынести в отдельную функцию уведомления

    }

    fun addAlarmManager(calendar: Calendar){
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MyReceiver::class.java)
        myIntent.putExtra("one_time", true);
        val pendingIntentpi= PendingIntent.getBroadcast(applicationContext,0, myIntent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntentpi)
    }
    fun loadSchedule(){
        val scheduleLayout = activityMainBinding.scheduleLayout
        scheduleLayout.removeAllViews()
        for (schedule in scheduleController!!.getSchedule()) {
            scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater);
            val slp = scheduleLayoutPane.root
            scheduleLayout.addView(slp);
            scheduleLayoutPane.scheduleBody.setText(schedule.description)
            scheduleLayoutPane.scheduleHeader.setText(schedule.header);
            scheduleLayoutPane.time.setText(schedule.time.toString())
            scheduleLayoutPane.deleteScheduleButton.setOnClickListener {
                scheduleLayout.removeView( it.parent as View)
                scheduleController!!.delSchedule(schedule.id)
            }
            scheduleLayoutPane.editScheduleButton.setOnClickListener {
                val intent = Intent(this,AddScheduleActivity::class.java)
                intent.putExtra("id",schedule.id)
                intent.putExtra("header",schedule.header)
                intent.putExtra("description",schedule.description)
                intent.putExtra("time",schedule.time.toString())
                startActivity(intent)
            }
            Log.d("MainActivity", schedule.toString())
        };
    }

    fun alarmManager(){
    }

    override fun onResume() {
        loadSchedule()
        Log.d("MAIN","resume");
        super.onResume()
    }
}