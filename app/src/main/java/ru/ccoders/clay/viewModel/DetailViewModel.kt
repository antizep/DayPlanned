package ru.ccoders.clay.viewModel

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.ccoders.clay.controller.RestController
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel
import java.io.File

class DetailViewModel(application: Application) : AndroidViewModel(application) {


    val deleteLiveData = MutableLiveData<Boolean>();
    var sqlScheduleController = SQLiteScheduleController(application)
    val scheduleDeatailLiveData = MutableLiveData<ScheduleModel>()

    private var context = application

    fun delete(scheduleId: Int) {
        sqlScheduleController.delSchedule(scheduleId);
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val file = File(appGallery!!.absolutePath + "/$scheduleId/")

        if (file.exists()) {
            file.deleteRecursively()
        }

        deleteLiveData.postValue(true)
        deleteLiveData.value = true

    }

    fun loadSchedule(scheduleId: Int){
        val  schedule = sqlScheduleController.getScheduleById(scheduleId)
        scheduleDeatailLiveData.postValue(schedule)
    }

    fun uploadToServer(schedule: ScheduleModel){
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        val file = File(appGallery!!.absolutePath + "/${schedule.id}/0.JPG")
        val remoteId = RestController(
            context.getSharedPreferences(
                "authentication",
                Context.MODE_PRIVATE
            )
        ).uploadToServer(schedule,file)
        if(remoteId > 0L){
            schedule.setRemoteId(remoteId)
            sqlScheduleController.updateSchedule(schedule)
            Log.d(
                this.javaClass.name,
                "Saved schedule:" + schedule.toJSONObject().toString()
            )
            scheduleDeatailLiveData.postValue(schedule)
        }
    }


}