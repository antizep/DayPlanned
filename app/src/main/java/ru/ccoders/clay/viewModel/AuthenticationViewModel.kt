package ru.ccoders.clay.viewModel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.ccoders.clay.controller.RestController
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    val authLiveData = MutableLiveData<Boolean>()
    val context = application
    val sqLiteScheduleController = SQLiteScheduleController(context)

    fun auth(login: String, password: String) {

        val preferences = context.getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val restController = RestController(preferences)

        val authStatus = restController.authentication(
            login,
            password
        )
        val schedules = restController.downloadSchedule();
        syncSchedules(schedules)
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        schedules.forEach {
            restController.downloadImage(it.getRemoteId(),it.id,appGallery.toString())
        }
        authLiveData.postValue(authStatus)

    }

    fun syncSchedules(schedules: List<ScheduleModel>){
        schedules.forEach {
            sqLiteScheduleController.updateScheduleByRemoteId(it)
        }
    }

}