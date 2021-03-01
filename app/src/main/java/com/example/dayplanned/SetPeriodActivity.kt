package com.example.dayplanned

import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivityAddScheduleBinding
import com.example.dayplanned.databinding.ActivityMainBinding
import com.example.dayplanned.databinding.ActivitySetPeriodBinding
import com.example.dayplanned.model.Schedule
import java.sql.Time

class SetPeriodActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var setPeriodBinding: ActivitySetPeriodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleController = AddScheduleController(this)
        var id = intent.getIntExtra("id",0);
        var t = intent.getStringExtra("time")
        val schedule = Schedule(id,null,null,0,0)
        setPeriodBinding = ActivitySetPeriodBinding.inflate(layoutInflater)
        setContentView(setPeriodBinding.root)
        setPeriodBinding.setTimePicker.setIs24HourView(true)
        if(!t.isNullOrBlank() && !t.equals(Schedule.TIEME_NOT)){
            val time = Time.valueOf(t);
            setPeriodBinding.setTimePicker.hour = time.hours
            setPeriodBinding.setTimePicker.minute = time.minutes
        }

        setPeriodBinding.comleteSetPeriod.setOnClickListener {
            Log.d(SetPeriodActivity::class.java.name,"h"+setPeriodBinding.setTimePicker.hour+" m:"+setPeriodBinding.setTimePicker.minute)
            var calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY,setPeriodBinding.setTimePicker.hour)
            calendar.set(Calendar.MINUTE,setPeriodBinding.setTimePicker.minute)
            calendar.set(Calendar.SECOND,0)
            schedule.time = calendar;
            scheduleController!!.setTime(schedule)
            Log.d(SetPeriodActivity::class.java.name,"s:"+schedule)
            Log.d(SetPeriodActivity::class.java.name,"t:"+schedule.time)

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()
            //parent.finish()
            finish()
        }

    }
}