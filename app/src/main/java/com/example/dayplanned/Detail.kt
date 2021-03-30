package com.example.dayplanned

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.databinding.ActivityDetailBinding
import com.example.dayplanned.databinding.ActivityMainBinding
import com.example.dayplanned.model.Schedule
import java.io.File

class Detail : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding
    var scheduleController: AddScheduleController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding.root)
        scheduleController = AddScheduleController(this)
        val id =  intent.getIntExtra("id",0)
        val schedule = scheduleController!!.getScheduleById(id)
        activityDetailBinding.deleteScheduleButton.setOnClickListener {
            scheduleController!!.delSchedule(schedule.id)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val t = schedule.getTxtTime();
        activityDetailBinding.time.setText(t)
        activityDetailBinding.scheduleBody.setText(schedule.description)
        activityDetailBinding.scheduleHeader.setText(schedule.header);

        val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var file = File(appGallery!!.absolutePath + "/$id/")
        if (file.exists()) {
            val images = file.listFiles()
            if (images != null && images.size > 0) {
                Glide.with(this).load(images[0]).apply(
                    RequestOptions().signature(
                        ObjectKey(
                            images[0].length()
                        )
                    )
                ).into(activityDetailBinding.ImageSchedule)
            }
        }

        activityDetailBinding.editScheduleButton.setOnClickListener {
            Log.d("Detail","click edit:"+schedule.header)
            val intent = Intent(this, AddScheduleActivity::class.java)
            intent.putExtra("id", schedule.id)
            intent.putExtra("header", schedule.header)

            intent.putExtra(AddScheduleController.MODE, schedule.mode)
            intent.putExtra(AddScheduleController.SCHEDULE, schedule.schedule.toString())

            intent.putExtra("description", schedule.description)
            intent.putExtra("time", schedule.getTxtTime())

            startActivity(intent)
        }
    }
}