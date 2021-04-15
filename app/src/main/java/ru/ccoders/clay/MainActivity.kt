package ru.ccoders.clay

import android.R.attr.*
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.model.Schedule
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.services.MyReceiver.Companion.DESCRIPTION
import ru.ccoders.clay.services.MyReceiver.Companion.HEADER
import ru.ccoders.clay.services.MyReceiver.Companion.ID
import ru.ccoders.clay.services.MyReceiver.Companion.TIME
import ru.ccoders.clay.utills.ImageUtil
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File


class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;

    companion object {
        private var calAlert: String? = null;
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        scheduleController = AddScheduleController(this)
        createDayBtn()
        loadSchedule();
        val scheduleAll = scheduleController!!.getSchedule();
        val nextTask = ScheduleUtils.nextTask(scheduleAll)
        if(nextTask != null) {
            addAlarmManager(nextTask)
        }
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
        myIntent.putExtra(TIME, schedule.getTxtTime())
        myIntent.putExtra(ID, schedule.id)
        val pendingIntentpi = PendingIntent.getBroadcast(
            applicationContext,
            0,
            myIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );

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
        val sorted = ScheduleUtils.sortByDay(scheduleAll, focusCalendar)
        if(sorted.size ==0){
            return
        }
        val nextTask = ScheduleUtils.nextTask(sorted)
        if (nextTask!=null) {
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
            slp.setOnClickListener {
                Log.d("MainActivity", "click task name:" + schedule.header)
                val intent = Intent(this, Detail::class.java)
                intent.putExtra("id", schedule.id)
                startActivity(intent)
            }
            scheduleLayout.addView(slp);
//            scheduleLayoutPane.scheduleBody.setText(schedule.description)

            scheduleLayoutPane.scheduleHeader.setText(schedule.header);
            scheduleLayoutPane.completeCounter.setText(schedule.complete.toString())
            scheduleLayoutPane.canceledCounter.setText(schedule.skipped.toString())
            val t = schedule.getTxtTime();
            scheduleLayoutPane.timeScheduleLayout.setText(schedule.getTxtTimeNotSecond())
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

            ImageUtil().resizeImage(scheduleLayoutPane,getResources().getDisplayMetrics().widthPixels)

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
    var focusCalendar = Calendar.getInstance();
    @SuppressLint("ResourceType")
    fun createDayBtn(){
        val today = Calendar.getInstance();
        var todayBatton: Button? = null
        val mondayBtn = activityMainBinding.mondayBtn
        val tuesdayBtn = activityMainBinding.tuesdayBtn
        val wednesdayBth = activityMainBinding.wednesdayBth
        val thursdayBtn = activityMainBinding.thursdayBtn
        val fridayBtn = activityMainBinding.fridayBtn
        val saturdayBtn= activityMainBinding.saturdayBtn
        val sundayBtn = activityMainBinding.sundayBtn

        when (today.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> todayBatton = mondayBtn
            Calendar.TUESDAY -> todayBatton = tuesdayBtn
            Calendar.WEDNESDAY -> todayBatton = wednesdayBth
            Calendar.THURSDAY -> todayBatton = thursdayBtn
            Calendar.FRIDAY -> todayBatton = fridayBtn
            Calendar.SATURDAY -> todayBatton = saturdayBtn
            Calendar.SUNDAY -> todayBatton = sundayBtn
        }
        //todo сделать с обводкой
        todayBatton!!.background= AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_yellow_button
        )
        val onClickListener =View.OnClickListener{
            vineButton(todayBatton)
            if (today!=it) {
                it.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.calendar_shady_button
                )
                if(it==mondayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }else if(it==tuesdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                }else if(it==wednesdayBth){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                }else if(it==thursdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                }else if(it==fridayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                }else if(it==saturdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                }else if(it==sundayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
            }else{
                focusCalendar = Calendar.getInstance()
            }
            loadSchedule()
        }
        mondayBtn.setOnClickListener(onClickListener)
        tuesdayBtn.setOnClickListener(onClickListener)
        wednesdayBth.setOnClickListener(onClickListener)
        thursdayBtn.setOnClickListener(onClickListener)
        fridayBtn.setOnClickListener(onClickListener)
        saturdayBtn.setOnClickListener(onClickListener)
        sundayBtn.setOnClickListener(onClickListener)

    }
    @SuppressLint("ResourceType")
    fun vineButton(today: Button){
        activityMainBinding.mondayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.tuesdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.wednesdayBth.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.thursdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.fridayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.saturdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.sundayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        today.background = AppCompatResources.getDrawable(this, R.drawable.calendar_yellow_button)
    }

    override fun onResume() {
        loadSchedule()
        val scheduleAll = scheduleController!!.getSchedule();
        val nextTask = ScheduleUtils.nextTask(scheduleAll)
        if(nextTask != null) {
            addAlarmManager(nextTask)
        }
        Log.d("MAIN", "resume");
        super.onResume()
    }
}