package ru.ccoders.clay

import SimpleItemTouchHelperCallback
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ccoders.clay.adapter.ScheduleCaustomAdapter
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.model.Schedule
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.services.MyReceiver.Companion.DESCRIPTION
import ru.ccoders.clay.services.MyReceiver.Companion.HEADER
import ru.ccoders.clay.services.MyReceiver.Companion.ID
import ru.ccoders.clay.services.MyReceiver.Companion.TIME
import ru.ccoders.clay.utills.ScheduleUtils


class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    lateinit var ctx:Context
    companion object {
        private var calAlert: String? = null;
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
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
        activityMainBinding.navigationBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.addSchedule -> startActivity(Intent(this, AddScheduleActivity::class.java));
            }
            false
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

        val scheduleLayout = activityMainBinding.SV
        if(scheduleLayout.layoutManager == null) {
            scheduleLayout.layoutManager = LinearLayoutManager(ctx)
        }
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
        val adapter = ScheduleCaustomAdapter(sorted,this)
        scheduleLayout.adapter  = adapter
//        val itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
//        itemTouchHelper.attachToRecyclerView(scheduleLayout)
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