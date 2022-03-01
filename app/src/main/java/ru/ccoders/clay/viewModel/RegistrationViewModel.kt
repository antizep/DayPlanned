package ru.ccoders.clay.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher
import ru.ccoders.clay.controller.RestController

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    val context = application;

    val regLiveData = MutableLiveData<Boolean>()
    val checkMailCodeLiveData = MutableLiveData<Boolean>()
    val registerLiveData = MutableLiveData<Boolean>()

    val restController =
        RestController(context.getSharedPreferences("authentication", Context.MODE_PRIVATE));

    fun checkMailAddress(mailAddress: String) {

        CoroutineScope(Dispatchers.IO).async {
            regLiveData.postValue(restController.checkMailAddress(mailAddress))
        }
    }

    fun enterMailCode(mailAddress: String, mailCode: String) {
        CoroutineScope(Dispatchers.IO).async {
            checkMailCodeLiveData.postValue(restController.checkMailCode(mailAddress, mailCode))
        }
    }

    fun registration(mailAddress: String, password: String) {
        CoroutineScope(Dispatchers.IO).async {
            registerLiveData.postValue(restController.register(mailAddress, password))
        }
    }

}