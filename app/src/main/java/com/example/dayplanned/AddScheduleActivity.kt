package com.example.dayplanned

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivityAddScheduleBinding
import com.example.dayplanned.model.Schedule

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var addScheduleBinding: ActivityAddScheduleBinding
    var scheduleController: AddScheduleController? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        scheduleController = AddScheduleController(this)
        Log.d("AHTUNG", "RUNNER")
        super.onCreate(savedInstanceState)
        addScheduleBinding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(addScheduleBinding.root)
        val id = intent.getIntExtra("id", 0);
        val header = intent.getStringExtra("header")
        val descr = intent.getStringExtra("description")
        val t = intent.getStringExtra("time")
        addSchedule(id,header,descr,t)
    }

    fun addSchedule(){
        addSchedule(0,null,null,null)
    }
    fun addSchedule(id: Int, head: String?, discr: String?,t:String?) {
        val header = addScheduleBinding.textChedule.text;
        val descript = addScheduleBinding.descriptionSchedule.text
        if (id != 0) {
            header.insert(0,head)
            descript.insert(0,discr)
        }
        addScheduleBinding.addScheduleButton.setOnClickListener {
            Log.d("AddScheduleActivity", "H:" + header + " D:" + descript);
            val schedule = Schedule(id, header.toString(), descript.toString());
            var success:Int = 0;
            if(id == 0) {
                if (!header.isBlank()) {
                    success = scheduleController!!.addSchedule(schedule)

                }
            }else{
                scheduleController!!.updateSchedule(schedule)
                success = schedule.id
            }
            if (success>0) {
                finish()
                val intent = Intent(this,SetPeriodActivity::class.java)
                intent.putExtra("id",success)
                intent.putExtra("time",t)
                startActivity(intent)
            }
        }
    }
}