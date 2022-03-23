package ru.ccoders.clay.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.R
import ru.ccoders.clay.databinding.ActivityAuthenticationBinding
import ru.ccoders.clay.viewModel.AuthenticationViewModel

class AuthenticationFragment : Fragment() {
    private lateinit var authenticationActivityBinding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var authObserver: Observer<Boolean>
    private lateinit var ctx: Context

    companion object {
        val AUTHENTICATION_PREFERENCES_NAME = "authentication"
    }

    override fun onStart() {
        super.onStart()
        viewModel.authLiveData.observe(this, authObserver)
    }

    override fun onStop() {
        super.onStop()
        viewModel.authLiveData.removeObserver(authObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        authenticationActivityBinding = ActivityAuthenticationBinding.inflate(layoutInflater);
        ctx = requireContext()
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        val navigator = findNavController()
        val preferences =
            ctx.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME, Context.MODE_PRIVATE)



        authObserver = Observer {
            if (it) {
                navigator.navigate(R.id.mainActivity)
            } else {
                Toast.makeText(ctx, "не удалось авторизоваться на сервере", Toast.LENGTH_LONG)
                    .show()
            }
        }

        authenticationActivityBinding.addScheduleButton.setOnClickListener {

            CoroutineScope(Dispatchers.IO).async {

                viewModel.auth(
                    authenticationActivityBinding.textUsername.text.toString().trim(),
                    authenticationActivityBinding.textPassword.text.toString().trim()
                )

            }

        }
        authenticationActivityBinding.signUp.setOnClickListener {
            val intent = Intent(ctx, RegistrationActivity::class.java)
            startActivity(intent)
        }

        if (preferences.contains("login") && preferences.contains("password")) {
            navigator.navigate(R.id.myProfileFragment)
            return
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return authenticationActivityBinding.root
    }
}