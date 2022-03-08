package ru.ccoders.clay

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.ccoders.clay.activities.AuthenticationFragment.Companion.AUTHENTICATION_PREFERENCES_NAME
import ru.ccoders.clay.databinding.MyProfileFragmentBinding
import ru.ccoders.clay.viewModel.DetailViewModel
import ru.ccoders.clay.viewModel.MyProfileViewModel


class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var myProfileFragmentBinding:MyProfileFragmentBinding
    private lateinit var ctx:Context
    private lateinit var dropObserver: Observer<Boolean>
    private lateinit var myProfileViewModel: MyProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myProfileFragmentBinding = MyProfileFragmentBinding.inflate(layoutInflater)
        ctx = requireContext()
        val preferences = ctx.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,Context.MODE_PRIVATE)
        val navigator = findNavController()

        myProfileViewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        dropObserver = Observer {
            if(it){
                ctx.deleteSharedPreferences(AUTHENTICATION_PREFERENCES_NAME)
                navigator.navigate(R.id.authenticationActivity)
            }
        }
        if (preferences.contains("login") && preferences.contains("password")) {

            myProfileFragmentBinding.myMailProfile.text = preferences.getString("login", "")
            myProfileFragmentBinding.exitButton.setOnClickListener {
                myProfileViewModel.dropScheduleInExit()
            }

        }else{
            navigator.navigate(R.id.authenticationActivity)
        }
    }

    override fun onStart() {
        super.onStart()
        myProfileViewModel.dropScheduleStateLiveData.observe(this,dropObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return myProfileFragmentBinding.root
    }
}