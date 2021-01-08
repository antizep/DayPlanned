package com.example.dayplanned

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dayplanned.controller.AddScheduleController
import android.util.Log
import androidx.core.view.children
import com.example.dayplanned.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        scheduleController = AddScheduleController(this)
        for (schedule in scheduleController!!.getSchedule()) {
            Log.d("MainActivity",schedule.toString())
        };
    }
}