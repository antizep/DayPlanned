package com.example.dayplanned

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivitySetPeriodBinding
import com.example.dayplanned.model.Schedule
import com.google.android.material.chip.Chip
import org.json.JSONArray
import java.sql.Time

class SetPeriodActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    companion object{
        var shedle = JSONArray()
    }
    private lateinit var setPeriodBinding: ActivitySetPeriodBinding
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleController = AddScheduleController(this)
        val id = intent.getIntExtra("id", 0);
        val t = intent.getStringExtra("time")
        var mode = intent.getIntExtra(AddScheduleController.MODE,0)

        setPeriodBinding = ActivitySetPeriodBinding.inflate(layoutInflater)
        setContentView(setPeriodBinding.root)
        setPeriodBinding.setTimePicker.setIs24HourView(true)
        if (!t.isNullOrBlank() && !t.equals(Schedule.TIEME_NOT)) {
            val time = Time.valueOf(t);
            setPeriodBinding.setTimePicker.hour = time.hours
            setPeriodBinding.setTimePicker.minute = time.minutes
        }
        val radioButtonDaily = setPeriodBinding.radioGroupMode;
        val chipGroupWeekly = setPeriodBinding.chipGroupWeekly
        when (mode){
            1-> {
                setPeriodBinding.radioButtonDaily.isChecked = true
                chipGroupWeekly.visibility = View.GONE
            }
            2-> {
                setPeriodBinding.radioButtonWeekly.isChecked = true
                chipGroupWeekly.visibility = View.VISIBLE
            }
        }
        radioButtonDaily.setOnCheckedChangeListener { buttonView, isChecked ->
            val chipGroupWeekly = setPeriodBinding.chipGroupWeekly
            when (buttonView.checkedRadioButtonId) {
                setPeriodBinding.radioButtonDaily.id -> {
                    chipGroupWeekly.visibility = View.GONE
                    mode = AddScheduleController.DAILY_MODE
                }
                setPeriodBinding.radioButtonWeekly.id -> {
                    chipGroupWeekly.visibility = View.VISIBLE
                    mode = AddScheduleController.VEEKLY_MODE
                }
            }
            Log.d("SetPeriodActivity", "checked mon:" + buttonView.checkedRadioButtonId)
        }

        val chipMon = setPeriodBinding.chipMon
        val chipFr = setPeriodBinding.chipFr
        val chipWed = setPeriodBinding.chipWed
        val chipSat = setPeriodBinding.chipSat
        val chipTue = setPeriodBinding.chipTue
        val chipSun = setPeriodBinding.chipSun
        val chipTh = setPeriodBinding.chipTh

        setChip(chipMon)
        setChip(chipFr)
        setChip(chipWed)
        setChip(chipSat)
        setChip(chipTue)
        setChip(chipSun)
        setChip(chipTh)

        setPeriodBinding.comleteSetPeriod.setOnClickListener {
            Log.d(
                SetPeriodActivity::class.java.name,
                "h" + setPeriodBinding.setTimePicker.hour + " m:" + setPeriodBinding.setTimePicker.minute
            )
            var calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, setPeriodBinding.setTimePicker.hour)
            calendar.set(Calendar.MINUTE, setPeriodBinding.setTimePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            val schedule = Schedule(id, null, null, 0, 0,mode,JSONArray())
            schedule.time = calendar;
            scheduleController!!.setTime(schedule)
            Log.d(SetPeriodActivity::class.java.name, "s:" + schedule)
            Log.d(SetPeriodActivity::class.java.name, "t:" + schedule.time)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun  setChip(chip: Chip){
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("SetPeriodActivity", "checked:" + buttonView.id +"|"+isChecked)
            if(isChecked) {
                chip.setChipBackgroundColor(getColorStateList(R.color.green))
            }else{
                chip.setChipBackgroundColor(getColorStateList(R.color.red))
            }
        }
    }

}