package ru.ccoders.clay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color.green
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivitySetPeriodBinding
import ru.ccoders.clay.model.Schedule
import com.google.android.material.chip.Chip
import org.json.JSONArray
import java.sql.Time

class SetPeriodActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    companion object{
        var shedle = JSONArray("[true,true,true,true,true,false,false]")
    }
    private lateinit var setPeriodBinding: ActivitySetPeriodBinding
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleController = AddScheduleController(this)
        val id = intent.getIntExtra("id", 0);
        val t = intent.getStringExtra("time")
        var mode = intent.getIntExtra(AddScheduleController.MODE,AddScheduleController.VEEKLY_MODE)
        val scheduleS = intent.getStringExtra(AddScheduleController.SCHEDULE)

        if(scheduleS != null) {
            val s = JSONArray(scheduleS)
            if(s.length() != 0) {
                shedle = s
            }
        }
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

        setChip(chipMon, shedle.getBoolean(0))
        setChip(chipTue, shedle.getBoolean(1))
        setChip(chipWed, shedle.getBoolean(2))
        setChip(chipTh, shedle.getBoolean(3))
        setChip(chipFr, shedle.getBoolean(4))
        setChip(chipSat, shedle.getBoolean(5))
        setChip(chipSun, shedle.getBoolean(6))

        setPeriodBinding.comleteSetPeriod.setOnClickListener {
            Log.d(
                SetPeriodActivity::class.java.name,
                "h" + setPeriodBinding.setTimePicker.hour + " m:" + setPeriodBinding.setTimePicker.minute
            )
            var calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, setPeriodBinding.setTimePicker.hour)
            calendar.set(Calendar.MINUTE, setPeriodBinding.setTimePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            val schedule = Schedule(id, null, null, 0, 0,mode, shedle)
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

    fun  setChip(chip: Chip,check: Boolean){
        if(check){
            chip.background = AppCompatResources.getDrawable(this,R.drawable.calendar_yellow_button)
            chip.isChecked = true
        }else{
            chip.background =AppCompatResources.getDrawable(this,R.drawable.calendar_inactive_button)
            chip.isChecked = false
        }
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("SetPeriodActivity", "checked:" + buttonView.id +"|"+isChecked)
            if(isChecked) {
                chip.background = AppCompatResources.getDrawable(this,R.drawable.calendar_yellow_button)
            }else{
                chip.background =AppCompatResources.getDrawable(this,R.drawable.calendar_inactive_button)
            }
            when(buttonView.id){
                setPeriodBinding.chipMon.id -> {
                    shedle.put(0,isChecked)
                }
                setPeriodBinding.chipTue.id ->{
                    shedle.put(1,isChecked)
                }
                setPeriodBinding.chipWed.id ->{
                    shedle.put(2,isChecked)
                }
                setPeriodBinding.chipTh.id ->{
                    shedle.put(3,isChecked)
                }
                setPeriodBinding.chipFr.id ->{
                    shedle.put(4,isChecked)
                }
                setPeriodBinding.chipSat.id ->{
                    shedle.put(5,isChecked)
                }
                setPeriodBinding.chipSun.id ->{
                    shedle.put(6,isChecked)
                }
            }
            Log.d("SetPeriodActivity", shedle.toString())
        }
    }

}