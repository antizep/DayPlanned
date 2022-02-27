package ru.ccoders.clay.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.databinding.ActivityAuthenticationBinding
import ru.ccoders.clay.viewModel.AuthenticationViewModel

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var authenticationActivityBinding: ActivityAuthenticationBinding
    private lateinit var viewModel:AuthenticationViewModel
    private lateinit var authObserver: Observer<Boolean>
    override fun onStart() {
        super.onStart()
        viewModel.authLiveData.observe(this,authObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.authLiveData.removeObserver(authObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        authenticationActivityBinding = ActivityAuthenticationBinding.inflate(layoutInflater);

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        setContentView(authenticationActivityBinding.root)

        authObserver = Observer {
            if (it){
                onBackPressed()
            }else{
                Toast.makeText(this,"не удалось авторизоваться на сервере",Toast.LENGTH_LONG).show()
            }
        }

        authenticationActivityBinding.addScheduleButton.setOnClickListener {

            CoroutineScope(Dispatchers.IO).async {

                viewModel.auth(authenticationActivityBinding.textUsername.text.toString(),
                    authenticationActivityBinding.textPassword.text.toString())

            }

        }
        authenticationActivityBinding.signUp.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }
}