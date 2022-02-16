package ru.ccoders.clay.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.controller.RestController
import ru.ccoders.clay.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var authenticationActivityBinding: ActivityAuthenticationBinding
    private lateinit var preferences:SharedPreferences
    private lateinit var  restController:RestController

    override fun onCreate(savedInstanceState: Bundle?) {
        authenticationActivityBinding = ActivityAuthenticationBinding.inflate(layoutInflater);

        super.onCreate(savedInstanceState)

        setContentView(authenticationActivityBinding.root)
        preferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
        restController =  RestController(preferences)
        authenticationActivityBinding.addScheduleButton.setOnClickListener {


            CoroutineScope(Dispatchers.IO).async {

                restController.authentication(authenticationActivityBinding.textUsername.text.toString(),
                    authenticationActivityBinding.textPassword.text.toString())

            }


        }

    }



}