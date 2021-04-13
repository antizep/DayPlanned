package ru.ccoders.clay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityDetailBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.utills.ImageUtil
import java.io.File

class Detail : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    var scheduleController: AddScheduleController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater);
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
        activityDetailBinding.scheduleBody.setText(schedule.description)
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
                ).into(scheduleLayoutPane.ImageSchedule)
            }
        }
        scheduleLayoutPane.time.setText(schedule.getTxtTime())
        scheduleLayoutPane.scheduleHeader.setText(schedule.header)
        scheduleLayoutPane.canceledCounter.setText(schedule.skipped.toString())
        scheduleLayoutPane.completeCounter.setText(schedule.complete.toString())

        activityDetailBinding.ImageLayout.addView(scheduleLayoutPane.root)
        ImageUtil().resizeImage(scheduleLayoutPane,getResources().getDisplayMetrics().widthPixels)
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