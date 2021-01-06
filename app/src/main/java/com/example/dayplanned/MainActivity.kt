package com.example.dayplanned

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.get
import com.example.dayplanned.databinding.ActivityAddScheduleBinding

class MainActivity : AppCompatActivity() {
    private lateinit var addScheduleBinding: ActivityAddScheduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("AHTUNG","RUNNER")
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_schedule)
        addScheduleBinding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(addScheduleBinding.root)

        var msg = "";
        addScheduleBinding.dateChedule.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            msg += "На:" + dayOfMonth + "/" + (month + 1) + "/" + year

        }

        addScheduleBinding.addScheduleButton.setOnClickListener{
            val schedule = addScheduleBinding.textChedule.text.toString();
            addScheduleBinding.textChedule.text.clear()
            Log.d("AHT",msg)
            msg+=" Запланировано:"+schedule;
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            msg= "";
        }
    }
}