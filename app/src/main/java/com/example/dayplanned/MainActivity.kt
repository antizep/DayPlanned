package com.example.dayplanned

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService.enqueueWork
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivityMainBinding
import com.example.dayplanned.databinding.SheduleLayoutBinding
import com.example.dayplanned.model.Schedule
import com.example.dayplanned.services.MyReceiver
import com.example.dayplanned.services.MyReceiver.Companion.DESCRIPTION
import com.example.dayplanned.services.MyReceiver.Companion.HEADER
import com.example.dayplanned.services.MyReceiver.Companion.ID
import com.example.dayplanned.services.NotificationService
import java.io.File

class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    companion object {
        private var calAlert: String? = null;
    }

    override fun onDestroy() {
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, MyReceiver::class.java)
//        this.sendBroadcast(broadcastIntent)
//        Log.d("MyRec","closed")
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        scheduleController = AddScheduleController(this)

        loadSchedule();

//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, NotificationService::class.java)
//        startForegroundService(broadcastIntent)

        activityMainBinding.createNewButton.setOnClickListener {
            startActivity(Intent(this, AddScheduleActivity::class.java));
        }

    }


    fun addAlarmManager(schedule: Schedule) {
        if(schedule.time == null){
            return
        }
        if(calAlert != null && schedule.getTxtTime().equals(calAlert)){
            return
        }
        calAlert = schedule.getTxtTime()
        Log.d("MyReceiver","old:"+ calAlert+",new:"+ schedule.time!!.time.toString())
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MyReceiver::class.java)
        //myIntent.action = "restartservice"
        myIntent.putExtra(HEADER,schedule.header)
        myIntent.putExtra(DESCRIPTION,schedule.description)
        myIntent.putExtra(ID,schedule.id)
        val pendingIntentpi = PendingIntent.getBroadcast(applicationContext, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Toast.makeText(this, "notification added:"+schedule.getTxtTime(), Toast.LENGTH_LONG).show()

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
            if(min.time !=null) {
                min.time!!.add(Calendar.DAY_OF_YEAR, 1);
            }
            unsorted.remove(min)
        }
        if(!sorted.isEmpty()) {
            addAlarmManager(sorted.get(0))
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

    @SuppressLint("SetTextI18n")
    fun loadSchedule() {
        val scheduleLayout = activityMainBinding.scheduleLayout
        scheduleLayout.removeAllViews()
        val sorted = sortByThisTime(scheduleController!!.getSchedule())
        for (schedule in sorted) {

            scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater);
            val slp = scheduleLayoutPane.root
            scheduleLayout.addView(slp);
            scheduleLayoutPane.scheduleBody.setText(schedule.description)
            scheduleLayoutPane.scheduleHeader.setText(schedule.header);
            val t = schedule.getTxtTime();
            scheduleLayoutPane.time.setText(t)
            val id= schedule.id
            val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            var file = File(appGallery!!.absolutePath + "/$id/")
            if (file.exists()){
                val images = file.listFiles()
                if(images !=null &&images.size>0) {
                    Glide.with(this).load(images[0]).apply(RequestOptions().signature(ObjectKey(images[0].length()))).into(scheduleLayoutPane.ImageSchedule)
                }
            }
            scheduleLayoutPane.deleteScheduleButton.setOnClickListener {
                scheduleLayout.removeView(it.parent as View)
                scheduleController!!.delSchedule(schedule.id)
            }
            scheduleLayoutPane.editScheduleButton.setOnClickListener {
                val intent = Intent(this, AddScheduleActivity::class.java)
                intent.putExtra("id", schedule.id)
                intent.putExtra("header", schedule.header)
                intent.putExtra("description", schedule.description)
                intent.putExtra("time", schedule.getTxtTime())
                startActivity(intent)
            }
            Log.d("MainActivity", schedule.toString())
        };
    }

    override fun onResume() {
        loadSchedule()
        Log.d("MAIN", "resume");
        super.onResume()
    }
}