package ru.ccoders.clay

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.add_schedule.AddScheduleFragment
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.databinding.ActivityDetailBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.main_activity.MainFragment
import ru.ccoders.clay.rest.TaskRest
import ru.ccoders.clay.utills.ImageUtil
import java.io.File

class Detail : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    var scheduleController: SQLScheduleController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        scheduleLayoutPane = SheduleLayoutBinding.inflate(layoutInflater);
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding.root)
        scheduleController = SQLScheduleController(this)
        val id =  intent.getIntExtra("id",0)
        if(id==0){
            return;
        }

        val schedule = scheduleController!!.getScheduleById(id)
        val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var file = File(appGallery!!.absolutePath + "/$id/")

        activityDetailBinding.deleteScheduleButton.setOnClickListener {
            scheduleController!!.delSchedule(schedule.id)

            if(schedule.getRemoteId()>0){
                val scheduleRest = TaskRest()
                val scope = CoroutineScope(Dispatchers.IO)
                scope.async {
                    scheduleRest.taskDelete(schedule.getRemoteId())
                }
            }

            if(file.exists()) {
                file.deleteRecursively()
            }
            val intent = Intent(this, RunActivity::class.java)

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
            scheduleLayoutPane.ImageSchedule.setImageBitmap(BitmapFactory.decodeResource(resources,R.mipmap.pic_dafault))
        }

        ImageUtil().resizeImage(scheduleLayoutPane.info,scheduleLayoutPane.ImageSchedule,getResources().getDisplayMetrics().widthPixels)


        activityDetailBinding.editScheduleButton.setOnClickListener {
            Log.d("Detail","click edit:"+schedule.header)
            val intent = Intent(this, RunActivity::class.java)
            intent.putExtra("idEdit", schedule.id)
            intent.putExtra("header", schedule.header)

            intent.putExtra(SQLScheduleController.MODE, schedule.mode)
            intent.putExtra(SQLScheduleController.SCHEDULE, schedule.schedule.toString())

            intent.putExtra("description", schedule.description)
            intent.putExtra("time", schedule.getTxtTime())

            startActivity(intent)
        }
    }
}