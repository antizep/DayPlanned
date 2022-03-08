package ru.ccoders.clay.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.databinding.ActivitySetPeriodBinding
import ru.ccoders.clay.dto.ScheduleModel
import org.json.JSONArray
import ru.ccoders.clay.R
import ru.ccoders.clay.RunActivity
import java.sql.Time

class SetPeriodActivity : AppCompatActivity() {
    var SQLScheduleController: SQLiteScheduleController? = null

    companion object {
        var shedle = JSONArray("[true,true,true,true,true,false,false]")
    }

    private lateinit var setPeriodBinding: ActivitySetPeriodBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SQLScheduleController = SQLiteScheduleController(this)
        val id = intent.getIntExtra("id", 0);
        val t = intent.getStringExtra("time")

        var mode = intent.getIntExtra(SQLiteScheduleController.MODE, SQLiteScheduleController.VEEKLY_MODE)
        val scheduleS = intent.getStringExtra(SQLiteScheduleController.SCHEDULE)

        if (scheduleS != null) {
            val s = JSONArray(scheduleS)
            if (s.length() != 0) {
                shedle = s
            }
        }
        setPeriodBinding = ActivitySetPeriodBinding.inflate(layoutInflater)
        setContentView(setPeriodBinding.root)
        setPeriodBinding.setTimePicker.setIs24HourView(true)
        if (!t.isNullOrBlank() && !t.equals(ScheduleModel.TIEME_NOT)) {
            val time = Time.valueOf(t);
            setPeriodBinding.setTimePicker.hour = time.hours
            setPeriodBinding.setTimePicker.minute = time.minutes
        }
        val radioButtonDaily = setPeriodBinding.radioGroupMode;
        val chipGroupWeekly = setPeriodBinding.dayOfWeekLayout
        when (mode) {
            1 -> {
                setPeriodBinding.radioButtonDaily.isChecked = true
                chipGroupWeekly.visibility = View.GONE
            }
            2 -> {
                setPeriodBinding.radioButtonWeekly.isChecked = true
                chipGroupWeekly.visibility = View.VISIBLE
            }
        }
        radioButtonDaily.setOnCheckedChangeListener { buttonView, isChecked ->
            val chipGroupWeekly = setPeriodBinding.dayOfWeekLayout
            when (buttonView.checkedRadioButtonId) {
                setPeriodBinding.radioButtonDaily.id -> {
                    chipGroupWeekly.visibility = View.GONE
                    mode = SQLiteScheduleController.DAILY_MODE
                }
                setPeriodBinding.radioButtonWeekly.id -> {
                    chipGroupWeekly.visibility = View.VISIBLE
                    mode = SQLiteScheduleController.VEEKLY_MODE
                }
            }
            Log.d("SetPeriodActivity", "checked mon:" + buttonView.checkedRadioButtonId)
        }

        val chipMon = setPeriodBinding.mondayBtn
        val chipFr = setPeriodBinding.fridayBtn
        val chipWed = setPeriodBinding.wednesdayBth
        val chipSat = setPeriodBinding.saturdayBtn
        val chipTue = setPeriodBinding.tuesdayBtn
        val chipSun = setPeriodBinding.sundayBtn
        val chipTh = setPeriodBinding.thursdayBtn

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
            val schedule = ScheduleModel(id, null, null, 0, 0, mode, shedle)
            schedule.time = calendar;
            SQLScheduleController!!.setTime(schedule)
            Log.d(SetPeriodActivity::class.java.name, "s:" + schedule)
            Log.d(SetPeriodActivity::class.java.name, "t:" + schedule.time)

            val intent = Intent(this, RunActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun setChip(chip: Button, check: Boolean) {
        if (check) {
            chip.background =
                AppCompatResources.getDrawable(this, R.drawable.calendar_yellow_button)

        } else {
            chip.background =
                AppCompatResources.getDrawable(this, R.drawable.calendar_inactive_button)

        }
        chip.setOnClickListener { button ->

            var id = 0;
            when (button.id) {
                setPeriodBinding.mondayBtn.id -> {
                    id = 0;
                }
                setPeriodBinding.tuesdayBtn.id -> {
                    id = 1
                }
                setPeriodBinding.wednesdayBth.id -> {
                    id = 2
                }
                setPeriodBinding.thursdayBtn.id -> {
                    id = 3
                }
                setPeriodBinding.fridayBtn.id -> {
                    id = 4
                }
                setPeriodBinding.saturdayBtn.id -> {
                    id = 5
                }
                setPeriodBinding.sundayBtn.id -> {
                    id = 6
                }


            }
            var isChecked = false;
            if(!shedle.getBoolean(id)) {
                chip.background = AppCompatResources.getDrawable(this,
                    R.drawable.calendar_yellow_button
                )
                isChecked = true
            }else{
                chip.background =AppCompatResources.getDrawable(this,
                    R.drawable.calendar_inactive_button
                )
                isChecked = false;
            }
            shedle.put(id,isChecked)
            Log.d("SetPeriodActivity", "checked:" + button.id +"|"+isChecked)

            Log.d("SetPeriodActivity", shedle.toString())
        }
    }

}