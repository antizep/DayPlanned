package com.example.dayplanned

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
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
import com.example.dayplanned.utills.ScheduleUtils
import java.io.File
import java.util.*

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
        if (schedule.time == null) {
            return
        }
        if (calAlert != null && schedule.getTxtTime().equals(calAlert)) {
            return
        }
        calAlert = schedule.getTxtTime()
        Log.d("MyReceiver", "old:" + calAlert + ",new:" + schedule.time!!.time.toString())
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MyReceiver::class.java)
        //myIntent.action = "restartservice"
        myIntent.putExtra(HEADER, schedule.header)
        myIntent.putExtra(DESCRIPTION, schedule.description)
        myIntent.putExtra(ID, schedule.id)
        val pendingIntentpi = PendingIntent.getBroadcast(
            applicationContext,
            0,
            myIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );
        Toast.makeText(this, "notification added:" + schedule.getTxtTime(), Toast.LENGTH_LONG)
            .show()

        alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.time!!.timeInMillis, pendingIntentpi)
    }

    @SuppressLint("SetTextI18n")
    fun loadSchedule() {
        val scheduleLayout = activityMainBinding.scheduleLayout
        scheduleLayout.removeAllViews()
        val scheduleAll = scheduleController!!.getSchedule();
        if(scheduleAll.size == 0){
            return
        }
        val sorted = ScheduleUtils.sortByTimeToday(scheduleAll)
        if(sorted.size ==0){
            return
        }
        val nextTask = ScheduleUtils.nextTask(sorted)
        if (nextTask!=null) {
            addAlarmManager(nextTask)
        }
        val indexTask:Int
        if(nextTask!!.time!!.get(Calendar.DAY_OF_YEAR) <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR) ) {
             indexTask = sorted.indexOf(nextTask);
        }else{
            indexTask = sorted.size-1;
        }
        for (schedule in sorted) {

            scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater);
            val slp = scheduleLayoutPane.root
            scheduleLayout.addView(slp);
            scheduleLayoutPane.scheduleBody.setText(schedule.description)
            scheduleLayoutPane.scheduleHeader.setText(schedule.header);
            scheduleLayoutPane.completeCounter.setText(schedule.complete.toString())
            val t = schedule.getTxtTime();
            scheduleLayoutPane.time.setText(t)
            val id = schedule.id
            val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            var file = File(appGallery!!.absolutePath + "/$id/")
            if (file.exists()) {
                val images = file.listFiles()
                if (images != null && images.size > 0) {
                    Glide.with(this).load(images[0]).apply(
                        RequestOptions().signature(
                            ObjectKey(
                                images[0].length()
                            )
                        )
                    ).into(scheduleLayoutPane.ImageSchedule)
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

                intent.putExtra(AddScheduleController.MODE, schedule.mode)
                intent.putExtra(AddScheduleController.SCHEDULE, schedule.schedule.toString())

                intent.putExtra("description", schedule.description)
                intent.putExtra("time", schedule.getTxtTime())

                startActivity(intent)
            }

            Log.d("MainActivity", schedule.toString())
        }
        //todo сейчас листаем до 6-й записи необходимо листать до следующей по времени + сделать разделение по времени и дням.
        //определить ближайщее по времни событие.
        //запомнить индекс элемента и сфокусироваться на нем.

            scheduleLayout.post {
                activityMainBinding.SV.scrollTo(0, scheduleLayout.get(indexTask).top)
                activityMainBinding.SV.computeScroll()
            }



    }

    override fun onResume() {
        loadSchedule()
        Log.d("MAIN", "resume");
        super.onResume()
    }
}