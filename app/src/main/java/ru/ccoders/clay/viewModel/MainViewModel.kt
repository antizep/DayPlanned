package ru.ccoders.clay.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.controller.RestController
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = MainViewModel::class.java.name


    val scheduleLiveData = MutableLiveData<List<ScheduleModel>>()

    private var scheduleController = SQLiteScheduleController(application)
    private lateinit var restController: RestController

    @SuppressLint("StaticFieldLeak")
    private var context: Application = application

    fun loadSchedule() {

        val scheduleAll = scheduleController.getSchedule();
        val properties = context.getSharedPreferences("authentication", Context.MODE_PRIVATE)
        restController =
            RestController(properties)
        if (properties.contains(RestController.LOGIN_FIELD) && properties.contains(RestController.PASSWORD_FIELD)) {
            CoroutineScope(Dispatchers.IO).async {
                synch()
            }
        } else {
            scheduleAll.stream().filter { it.getRemoteId() > 0 }.forEach {
                scheduleController.delSchedule(it.id)
            }
            removeBad(scheduleAll)

            refreshLiveDataAndNotification(scheduleAll)
        }


    }

    private fun removeBad(scheduleAll: List<ScheduleModel>) {
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
    }

    private fun refreshLiveDataAndNotification(scheduleAll: MutableList<ScheduleModel>) {
        scheduleLiveData.postValue(scheduleAll)
        if (scheduleAll.isNotEmpty()) {
            MyReceiver().addAlarmManager(ScheduleUtils.nextTask(scheduleAll)!!, context)
        }
    }

    private fun synch() {
        val remote = restController.downloadSchedule()
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        remote.forEach {
            val scheduleLocal = scheduleController.getScheduleById(it.getRemoteId(), true)
            if (scheduleLocal != null) {
                var isUnSynch = false;
                if (scheduleLocal.complete > it.complete) {
                    it.complete = scheduleLocal.complete
                    isUnSynch = true
                }
                if (scheduleLocal.skipped > it.skipped) {
                    isUnSynch = true
                    it.skipped = scheduleLocal.skipped
                }
                if (isUnSynch) {
                    restController.uploadToServer(it, File(""))
                }
            }
            scheduleController.updateScheduleByRemoteId(it)

            restController.downloadImage(it.getRemoteId(), it.id, appGallery.toString())
        }

        val scheduleAll = scheduleController.getSchedule()
        removeBad(scheduleAll)
        refreshLiveDataAndNotification(scheduleAll)
    }

}