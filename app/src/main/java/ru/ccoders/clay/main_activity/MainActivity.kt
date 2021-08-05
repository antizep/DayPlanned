package ru.ccoders.clay.main_activity

import PagerAdapterSchedule
import ProfileModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import ru.ccoders.clay.AddScheduleActivity
import ru.ccoders.clay.R
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.model.ScheduleModel


class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    lateinit var ctx: Context

    lateinit var  scheduleListLiveData:ScheduleLiveData
    companion object {
        private var calAlert: String? = null;
        public val ID_PROFILE = 1;
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(activityMainBinding.root)

        val provider: MainActivityViewModel by lazy {
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        }
        scheduleListLiveData = provider.scheduleListLiveData
        provider.loadProfile()
        provider.loadSchedules()

        profileLiveData.observe(this, Observer {
            activityMainBinding.friendCount.text = it.followed.toString()
            activityMainBinding.likedCount.text = it.followers.toString()
            activityMainBinding.nameField.text = it.username
            activityMainBinding.altNameField.text = "@${it.altName}"
            activityMainBinding.bioField.text = it.bio

        })



        printSchedule()
        createDayBtn()

        activityMainBinding.navigationBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.addScheduleMenu -> startActivity(
                    Intent(
                        this,
                        AddScheduleActivity::class.java
                    )
                );
            }
            false
        }

    }

    val profileLiveData = MutableLiveData<ProfileModel>()


    fun printSchedule() {

        scheduleListLiveData.observe(this, Observer {
            var isPublic = false


            val words = arrayListOf("Личные", "Доступные Всем")

            val adapter = PagerAdapterSchedule(this, it, focusCalendar, isPublic)
            val pager = activityMainBinding.pager
            val tab = activityMainBinding.scseduleListSwiper
            pager.adapter = adapter
            TabLayoutMediator(tab, pager) { tab, position ->
                tab.text = words[position]
                isPublic = words[0].equals(words[position])
            }.attach()

        })
    }

    var focusCalendar = Calendar.getInstance();

    @SuppressLint("ResourceType")
    fun createDayBtn() {
        val today = Calendar.getInstance();
        var todayBatton: Button? = null
        val mondayBtn = activityMainBinding.mondayBtn
        val tuesdayBtn = activityMainBinding.tuesdayBtn
        val wednesdayBth = activityMainBinding.wednesdayBth
        val thursdayBtn = activityMainBinding.thursdayBtn
        val fridayBtn = activityMainBinding.fridayBtn
        val saturdayBtn = activityMainBinding.saturdayBtn
        val sundayBtn = activityMainBinding.sundayBtn

        when (today.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> todayBatton = mondayBtn
            Calendar.TUESDAY -> todayBatton = tuesdayBtn
            Calendar.WEDNESDAY -> todayBatton = wednesdayBth
            Calendar.THURSDAY -> todayBatton = thursdayBtn
            Calendar.FRIDAY -> todayBatton = fridayBtn
            Calendar.SATURDAY -> todayBatton = saturdayBtn
            Calendar.SUNDAY -> todayBatton = sundayBtn
        }
        //todo сделать с обводкой
        todayBatton!!.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_yellow_button
        )
        val onClickListener = View.OnClickListener {
            vineButton(todayBatton)
            if (today != it) {
                it.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.calendar_shady_button
                )
                if (it == mondayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                } else if (it == tuesdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                } else if (it == wednesdayBth) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                } else if (it == thursdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                } else if (it == fridayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                } else if (it == saturdayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                } else if (it == sundayBtn) {
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
            } else {
                focusCalendar = Calendar.getInstance()
            }
            printSchedule()
        }
        mondayBtn.setOnClickListener(onClickListener)
        tuesdayBtn.setOnClickListener(onClickListener)
        wednesdayBth.setOnClickListener(onClickListener)
        thursdayBtn.setOnClickListener(onClickListener)
        fridayBtn.setOnClickListener(onClickListener)
        saturdayBtn.setOnClickListener(onClickListener)
        sundayBtn.setOnClickListener(onClickListener)

    }

    @SuppressLint("ResourceType")
    fun vineButton(today: Button) {
        activityMainBinding.mondayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.tuesdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.wednesdayBth.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.thursdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.fridayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.saturdayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.sundayBtn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_inactive_button
        )
        today.background = AppCompatResources.getDrawable(this, R.drawable.calendar_yellow_button)
    }

    override fun onResume() {

        Log.d("MAIN", "resume");
        super.onResume()
    }
}