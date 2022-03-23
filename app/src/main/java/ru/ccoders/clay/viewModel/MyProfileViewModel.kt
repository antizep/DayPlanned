package ru.ccoders.clay.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.ccoders.clay.controller.SQLiteScheduleController

class MyProfileViewModel(context: Application): AndroidViewModel(context) {
    val ctx = context
    private val sqLiteScheduleController =  SQLiteScheduleController(ctx)
    val dropScheduleStateLiveData = MutableLiveData<Boolean>()
    fun dropScheduleInExit(){
        sqLiteScheduleController.dropRemoteIdSchedules()
        dropScheduleStateLiveData.postValue(true)
    }

}