package ru.ccoders.clay.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = MainViewModel::class.java.name


    val scheduleLiveData = MutableLiveData<List<ScheduleModel>>()

    private var scheduleController = SQLiteScheduleController(application)

    @SuppressLint("StaticFieldLeak")
    private var context: Application = application

    fun loadSchedule() {

        val scheduleAll = scheduleController.getSchedule();

        scheduleAll.forEach {
            if (it.time == null || it.mode == null) {
                scheduleController.delSchedule(it.id)
                val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                val file = File(appGallery!!.absolutePath + "/${it.id}/")
                if (file.exists()) {
                    file.deleteRecursively()
                }
            }

        }
        scheduleLiveData.postValue(scheduleAll)
        MyReceiver().addAlarmManager(ScheduleUtils.nextTask(scheduleAll)!!, context)

    }

}

