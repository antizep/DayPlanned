package ru.ccoders.clay.activities

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.ccoders.clay.R
import ru.ccoders.clay.databinding.ActivityRegistrationBinding
import ru.ccoders.clay.viewModel.RegistrationViewModel
import java.util.regex.Pattern


class RegistrationActivity : AppCompatActivity() {

    private lateinit var registrationBinding: ActivityRegistrationBinding
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private val EMAIL_ADDRESS = 0
    private val ENTER_CODE = 1
    private val PASSWORD = 2
    private var mode = 0
    private lateinit var registrationViewModel: RegistrationViewModel

    private lateinit var checkMailObserver: Observer<Boolean>
    private lateinit var checkMailCodeObserver:Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {


        registrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(registrationBinding.root)
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)

        checkMailObserver = Observer { it ->
            if (it){
                registrationBinding.textEmailRegistration.isEnabled= false
                mode = ENTER_CODE
                onResume()
            }else{
                Toast.makeText(this,"Не далось проверить ваш адресс", Toast.LENGTH_LONG).show()
            }
        }

        checkMailCodeObserver = Observer {
            if (it){
                mode = PASSWORD
                onResume()
            }else{
                Toast.makeText(this,"Не удалось проверить проверочный код",Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        registrationViewModel.regLiveData.observe(this,checkMailObserver)
        registrationViewModel.checkMailCodeLiveData.observe(this,checkMailCodeObserver)
    }

    override fun onStop() {
        super.onStop()
        registrationViewModel.regLiveData.removeObserver(checkMailObserver)
        registrationViewModel.checkMailCodeLiveData.removeObserver(checkMailCodeObserver)
    }

    override fun onResume() {
        super.onResume()
        when (mode) {
            EMAIL_ADDRESS -> {
                registrationBinding.messageSendCode.visibility = View.GONE
                registrationBinding.enterCode.visibility = View.GONE
                registrationBinding.textEmailRegistration.visibility = View.VISIBLE
                registrationBinding.textPasswordRegistration.visibility = View.GONE
                registrationBinding.repeatPassword.visibility = View.GONE
                registrationBinding.iAgree.visibility = View.GONE
                registrationBinding.policy.visibility = View.GONE
                registrationBinding.resendCodeTimer.visibility = View.GONE
                registrationBinding.resendCodeBtn.visibility = View.GONE
                registrationBinding.addScheduleButtonRegistration.setOnClickListener {
                    val mailAddress = registrationBinding.textEmailRegistration.text.toString()
                    if(validateEmail(mailAddress)) {
                        registrationViewModel.checkMailAddress(mailAddress)
                    }else{
                        Toast.makeText(this,"Не корректный email",Toast.LENGTH_LONG).show()
                    }
                }
            }

            ENTER_CODE -> {
                registrationBinding.messageSendCode.visibility = View.VISIBLE
                registrationBinding.enterCode.visibility = View.VISIBLE
                registrationBinding.textEmailRegistration.visibility = View.GONE
                registrationBinding.textPasswordRegistration.visibility = View.GONE
                registrationBinding.repeatPassword.visibility = View.GONE
                registrationBinding.iAgree.visibility = View.GONE
                registrationBinding.policy.visibility = View.GONE
                registrationBinding.resendCodeTimer.visibility = View.VISIBLE
                registrationBinding.resendCodeBtn.visibility = View.VISIBLE
                registrationBinding.addScheduleButtonRegistration.setOnClickListener {

                    val mailAddress = registrationBinding.textEmailRegistration.text.toString()
                    val code = registrationBinding.enterCode.text.toString()
                    registrationViewModel.enterMailCode(mailAddress,code)

                }
            }

            PASSWORD -> {
                val string = getString(R.string.sign_app)
                registrationBinding.messageSendCode.visibility = View.GONE
                registrationBinding.enterCode.visibility = View.GONE
                registrationBinding.textEmailRegistration.visibility = View.VISIBLE
                registrationBinding.textPasswordRegistration.visibility = View.VISIBLE
                registrationBinding.repeatPassword.visibility = View.VISIBLE
                registrationBinding.iAgree.visibility = View.VISIBLE
                registrationBinding.policy.visibility = View.VISIBLE
                registrationBinding.resendCodeTimer.visibility = View.GONE
                registrationBinding.resendCodeBtn.visibility = View.GONE
                registrationBinding.addScheduleButtonRegistration.text = string
                registrationBinding.addScheduleButtonRegistration.setOnClickListener {
                    mode = EMAIL_ADDRESS
                    onResume()
                }
            }
        }
    }

    fun validateEmail(mail:String):Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(mail).matches()
    }
}