package ru.ccoders.clay

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.ccoders.clay.databinding.ActivityRunBinding

class RunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRunBinding

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