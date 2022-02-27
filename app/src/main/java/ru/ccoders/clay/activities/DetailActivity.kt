package ru.ccoders.clay.activities

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.R
import ru.ccoders.clay.controller.RestController
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.databinding.ActivityDetailBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.utills.ImageUtil
import ru.ccoders.clay.viewModel.DetailViewModel
import java.io.File


class DetailActivity : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var detailObserver: Observer<ScheduleModel>
    private var idSchedule:Int = 0

    override fun onStart() {
        super.onStart()
        detailViewModel.scheduleDeatailLiveData.observe(this,detailObserver)
    }

    override fun onStop() {
        super.onStop()
        detailViewModel.scheduleDeatailLiveData.removeObserver(detailObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding.root)

        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        detailObserver = Observer {
            printActivity(it)
        }
        idSchedule =  intent.getIntExtra("id",0)

        detailViewModel.loadSchedule(idSchedule)

    }

    private fun printActivity(schedule:ScheduleModel){
        activityDetailBinding.ImageLayout.removeAllViews()
        scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater)
        val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var file = File(appGallery!!.absolutePath + "/${schedule.id}/")

        activityDetailBinding.deleteScheduleButton.setOnClickListener {
            detailViewModel.delete(schedule.id)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        scheduleLayoutPane.timeScheduleLayout.setText(schedule.getTxtTimeNotSecond())
        scheduleLayoutPane.scheduleHeader.setText(schedule.header)
        scheduleLayoutPane.canceledCounter.setText(schedule.skipped.toString())
        scheduleLayoutPane.completeCounter.setText(schedule.complete.toString())

        activityDetailBinding.ImageLayout.addView(scheduleLayoutPane.root)

        activityDetailBinding.scheduleBody.setText(schedule.description)
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
        }else{
            scheduleLayoutPane.ImageSchedule.setImageBitmap(BitmapFactory.decodeResource(resources,
                R.mipmap.pic_dafault
            ))
        }

        ImageUtil().resizeImage(scheduleLayoutPane.info,scheduleLayoutPane.ImageSchedule,getResources().getDisplayMetrics().widthPixels)
        if (schedule.getRemoteId()>0){
            activityDetailBinding.uploadScheduleButton.visibility = View.GONE
        }else {
            activityDetailBinding.uploadScheduleButton.setOnClickListener {
                val preferences = getSharedPreferences("authentication", Context.MODE_PRIVATE);
                val username = preferences.getString("login", null)
                if (username == null && !preferences.contains("password")) {
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent);
                }else{
                    CoroutineScope(Dispatchers.IO).async {
                        detailViewModel.uploadToServer(schedule)
                    }
                }
            }
        }
        activityDetailBinding.editScheduleButton.setOnClickListener {
            Log.d("Detail","click edit:"+schedule.header)
            val intent = Intent(this, AddScheduleActivity::class.java)
            intent.putExtra("id", schedule.id)
            intent.putExtra("header", schedule.header)

            intent.putExtra(SQLiteScheduleController.MODE, schedule.mode)
            intent.putExtra(SQLiteScheduleController.SCHEDULE, schedule.schedule.toString())

            intent.putExtra("description", schedule.description)
            intent.putExtra("time", schedule.getTxtTime())

            startActivity(intent)
        }
    }
}