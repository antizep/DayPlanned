package com.example.dayplanned

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
        Log.d("AHTUNG","RUNNER")
        super.onCreate(savedInstanceState)
        addScheduleBinding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(addScheduleBinding.root)

        val header = addScheduleBinding.textChedule.text;
        val descript = addScheduleBinding.descriptionSchedule.text

        addScheduleBinding.addScheduleButton.setOnClickListener{
            Log.d("AddScheduleActivity","H:"+header+" D:"+descript);
            val schedule =  Schedule(0,header.toString(),descript.toString());
            var success = false
            success  = scheduleController!!.addSchedule(schedule)
            if (success){
                val toast = Toast.makeText(this,"Saved Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }
}