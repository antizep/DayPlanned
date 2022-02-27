package ru.ccoders.clay.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.ccoders.clay.controller.RestController

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    val authLiveData = MutableLiveData<Boolean>()
    val context = application
    fun auth(login: String, password: String) {

        val preferences = context.getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val restController = RestController(preferences)

        val authStatus = restController.authentication(
            login,
            password
        )
        authLiveData.postValue(authStatus)

    }

}