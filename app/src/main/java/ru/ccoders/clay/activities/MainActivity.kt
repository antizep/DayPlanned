package ru.ccoders.clay.activities

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
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.R
import ru.ccoders.clay.adapters.ScheduleRecyclerViewAdapter
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.services.MyReceiver.Companion.DESCRIPTION
import ru.ccoders.clay.services.MyReceiver.Companion.HEADER
import ru.ccoders.clay.services.MyReceiver.Companion.ID
import ru.ccoders.clay.services.MyReceiver.Companion.TIME
import ru.ccoders.clay.utills.ImageUtil
import ru.ccoders.clay.utills.ScheduleUtils
import ru.ccoders.clay.viewModel.MainViewModel
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    private lateinit var scheduleLiveData: MutableLiveData<List<ScheduleModel>>;

    companion object {
        private var calAlert: String? = null;
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val provider = ViewModelProvider(this).get(MainViewModel::class.java)
        provider.loadSchedule();
        scheduleLiveData = provider.scheduleLiveData;

        createDayBtn()
        scheduleLiveData.observe(this, Observer {
            loadSchedule(it);
        })

        activityMainBinding.createNewButton.setOnClickListener {
            startActivity(Intent(this, AddScheduleActivity::class.java));
        }

    }


    fun addAlarmManager(scheduleModel: ScheduleModel) {
        if (scheduleModel.time == null) {
            return
        }
        if (calAlert != null && scheduleModel.getTxtTime().equals(calAlert)) {
            return
        }
        calAlert = scheduleModel.getTxtTime()
        Log.d("MyReceiver", "old:" + calAlert + ",new:" + scheduleModel.time!!.time.toString())
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(applicationContext, MyReceiver::class.java)
        myIntent.putExtra(HEADER, scheduleModel.header)
        myIntent.putExtra(DESCRIPTION, scheduleModel.description)
        myIntent.putExtra(TIME, scheduleModel.getTxtTime())
        myIntent.putExtra(ID, scheduleModel.id)
        val pendingIntentpi = PendingIntent.getBroadcast(
            applicationContext,
            0,
            myIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            scheduleModel.time!!.timeInMillis,
            pendingIntentpi
        )
    }

    @SuppressLint("SetTextI18n")
    fun loadSchedule(scheduleAll: List<ScheduleModel>) {

        val scheduleLayout = activityMainBinding.scheduleLayout

        scheduleLayout.removeAllViews()
        if (scheduleAll.size == 0) {
            return
        }
        val sorted = ScheduleUtils.sortByDay(scheduleAll, focusCalendar)
        if (sorted.size == 0) {
            return
        }
        val nextTask = ScheduleUtils.nextTask(sorted)

        val indexTask: Int
        if (nextTask!!.time!!.get(Calendar.DAY_OF_YEAR) <= Calendar.getInstance()
                .get(Calendar.DAY_OF_YEAR)
        ) {
            indexTask = sorted.indexOf(nextTask);
        } else {
            indexTask = sorted.size - 1;
        }
        scheduleLayout.layoutManager = LinearLayoutManager(this)
        scheduleLayout.adapter = ScheduleRecyclerViewAdapter(sorted)

        scheduleLayout.post {
            activityMainBinding.SV.scrollTo(0, scheduleLayout[indexTask].top)
            activityMainBinding.SV.computeScroll()
        }


    }

    var focusCalendar = Calendar.getInstance();

    @SuppressLint("ResourceType")
    fun createDayBtn() {
        val today = Calendar.getInstance();
        var todayBatton: Button? = null
        val mondayBtn = activityMainBinding.mondayBtn
        val tuesdayBtn = activityMainBinding.tuesdayBtn
        val wednesdayBth = activityMainBinding.wednesdayBth
        val thursdayBtn = activityMainBinding.thursdayBtn
        val fridayBtn = activityMainBinding.fridayBtn
        val saturdayBtn = activityMainBinding.saturdayBtn
        val sundayBtn = activityMainBinding.sundayBtn

        when (today.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> todayBatton = mondayBtn
            Calendar.TUESDAY -> todayBatton = tuesdayBtn
            Calendar.WEDNESDAY -> todayBatton = wednesdayBth
            Calendar.THURSDAY -> todayBatton = thursdayBtn
            Calendar.FRIDAY -> todayBatton = fridayBtn
            Calendar.SATURDAY -> todayBatton = saturdayBtn
            Calendar.SUNDAY -> todayBatton = sundayBtn
        }

        todayBatton!!.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_yellow_button
        )
        val onClickListener = View.OnClickListener {
            vineButton(todayBatton)
            if (today != it) {
                it.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.calendar_shady_button
                )
                if (it == mondayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                } else if (it == tuesdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                } else if (it == wednesdayBth) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                } else if (it == thursdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                } else if (it == fridayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                } else if (it == saturdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                } else if (it == sundayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
            } else {
                focusCalendar = Calendar.getInstance()
            }
            scheduleLiveData.value?.let { it1 -> loadSchedule(it1) }
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
    fun vineButton(today: Button) {
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
}