package ru.ccoders.clay

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivitySetPeriodBinding
import ru.ccoders.clay.model.TaskModel
import org.json.JSONArray
import ru.antizep.russua_victory.dataprovider.rest.ProfileRest
import ru.ccoders.clay.rest.TaskRest
import java.sql.Time

class SetPeriodActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null

    companion object {
        var shedle = JSONArray("[true,true,true,true,true,false,false]")
    }

    private lateinit var setPeriodBinding: ActivitySetPeriodBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleController = AddScheduleController(this)
        val id = intent.getIntExtra("id", 0);
        val t = intent.getStringExtra("time")
        val isPublic = intent.getBooleanExtra("isPublic",false)
        var mode = intent.getIntExtra(AddScheduleController.MODE, AddScheduleController.VEEKLY_MODE)
        val scheduleS = intent.getStringExtra(AddScheduleController.SCHEDULE)

        if (scheduleS != null) {
            val s = JSONArray(scheduleS)
            if (s.length() != 0) {
                shedle = s
            }
        }
        setPeriodBinding = ActivitySetPeriodBinding.inflate(layoutInflater)
        setContentView(setPeriodBinding.root)
        setPeriodBinding.setTimePicker.setIs24HourView(true)
        if (!t.isNullOrBlank() && !t.equals(TaskModel.TIEME_NOT)) {
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
                    mode = AddScheduleController.DAILY_MODE
                }
                setPeriodBinding.radioButtonWeekly.id -> {
                    chipGroupWeekly.visibility = View.VISIBLE
                    mode = AddScheduleController.VEEKLY_MODE
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

            val schedule = scheduleController!!.getScheduleById(id)//TaskModel(id, null, null, 0, 0, mode, shedle)
            schedule.mode = mode
            schedule.schedule = shedle
            schedule.time = calendar;


            Log.d(SetPeriodActivity::class.java.name, "s:" + schedule)
            Log.d(SetPeriodActivity::class.java.name, "t:" + schedule.time)


            val scope = CoroutineScope(Dispatchers.IO)
            val intent = Intent(this, MainActivity::class.java)
            val ctx= this
            if(isPublic) {
                scope.async {
                    val taskSaveResult = TaskRest().uploadTask(schedule)
                    withContext(Dispatchers.Main) {

                        if (taskSaveResult>0) {
                            schedule.setRemoteId(taskSaveResult)
                            scheduleController!!.setTime(schedule)
                            startActivity(intent)
                            Toast.makeText(ctx, "Saved Successfully", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                }
            }else {
                scheduleController!!.setTime(schedule)
                startActivity(intent)
                Toast.makeText(ctx, "Saved Successfully", Toast.LENGTH_LONG).show()
                finish()
            }
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
                chip.background = AppCompatResources.getDrawable(this,R.drawable.calendar_yellow_button)
                isChecked = true
            }else{
                chip.background =AppCompatResources.getDrawable(this,R.drawable.calendar_inactive_button)
                isChecked = false;
            }
            shedle.put(id,isChecked)
            Log.d("SetPeriodActivity", "checked:" + button.id +"|"+isChecked)

            Log.d("SetPeriodActivity", shedle.toString())
        }
    }

}