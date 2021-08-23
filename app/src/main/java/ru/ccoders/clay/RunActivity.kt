package ru.ccoders.clay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.ccoders.clay.add_schedule.AddScheduleFragment


class RunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)

        val idEdit = intent.getIntExtra("idEdit",0)
        val bottomNavigationView = findViewById<BottomNavigationView
                >(R.id.navigationBar)
        val navController = findNavController(R.id.nav_fragment)
        bottomNavigationView.setupWithNavController(navController)
        if(idEdit!=0){
            navController.navigate(R.id.addScheduleActivity)
        }
    }
}