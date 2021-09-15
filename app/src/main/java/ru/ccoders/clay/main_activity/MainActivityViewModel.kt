package ru.ccoders.clay.main_activity

import ProfileModel
import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.ccoders.clay.services.MyReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.antizep.russua_victory.dataprovider.rest.ProfileRest
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.model.ScheduleAndProfile
import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.rest.TaskRest
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MainActivityViewModel"
    val profileLiveData = MutableLiveData<ProfileModel>()
    val scheduleListLiveData = ScheduleLiveData()
    private val scheduleRest:TaskRest = TaskRest()

    private var scheduleController: SQLScheduleController = SQLScheduleController(application)

    @SuppressLint("StaticFieldLeak")
    private var context:Application = application

    fun loadProfile() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.async {
            val profile = ProfileRest().loadProfile(MainFragment.ID_PROFILE)

            if (profile != null) {

                profileLiveData.postValue(profile)
                profileLiveData.value = profile

            } else {
                Log.d("MainActivity_CoroutineScope", "profile is null")
            }

        }
    }

    fun loadSchedules(){
        val scheduleAll = scheduleController.getSchedule();
        val scheduleAndProfileList = mutableListOf<ScheduleAndProfile>()

        scheduleAll.forEach {
            if (it.time == null || it.mode == null) {
                scheduleController.delSchedule(it.id)
                val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                var file = File(appGallery!!.absolutePath + "/${it.id}/")
                if (file.exists()) {
                    file.deleteRecursively()
                }
            }
            if (it.getRemoteId() == 0){
                scheduleAndProfileList.add(ScheduleAndProfile(it,null))
            }
        }
        if (scheduleAll.isEmpty()) return
        //context.startForegroundService(Intent(context,MyReceiver::class.java))
        MyReceiver().addAlarmManager(ScheduleUtils.nextTask(scheduleAll)!!,context)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.async {
            val scheduleRemote = scheduleRest.loadTask(MainFragment.ID_PROFILE)
            val scheduleAndProfiles = scheduleRemote.scheduleAndProfile

            if (scheduleAndProfiles.isNotEmpty()) {
                val s = mutableListOf<ScheduleAndProfile>()
//                val sched = scheduleController.upgradeInRemoteSchedule(scheduleAndProfiles)
                s.addAll(scheduleAndProfileList)
                s.addAll(scheduleAndProfiles)
                scheduleListLiveData.postValue(s)
                scheduleListLiveData.value=s

            } else {
                Log.d("MainActivity_CoroutineScope", "profile is null")
            }

        }
        //MyWorker.addAlarmManager(ScheduleUtils.nextTask(scheduleAll)!!,context)
        scheduleListLiveData.value = scheduleAndProfileList

    }

}