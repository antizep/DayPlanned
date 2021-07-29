package ru.ccoders.clay

import PagerAdapterSchedule
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import ru.antizep.russua_victory.dataprovider.rest.ProfileRest
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityMainBinding
import ru.ccoders.clay.databinding.SheduleLayoutBinding
import ru.ccoders.clay.model.TaskModel
import ru.ccoders.clay.services.MyReceiver
import ru.ccoders.clay.services.MyReceiver.Companion.DESCRIPTION
import ru.ccoders.clay.services.MyReceiver.Companion.HEADER
import ru.ccoders.clay.services.MyReceiver.Companion.ID
import ru.ccoders.clay.services.MyReceiver.Companion.TIME
import ru.ccoders.clay.services.MyWorker
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File
import java.time.Duration
import java.util.*


class MainActivity : AppCompatActivity() {
    var scheduleController: AddScheduleController? = null
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var scheduleLayoutPane: SheduleLayoutBinding;
    lateinit var ctx:Context
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
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        loadProfile()
        setContentView(activityMainBinding.root)
        scheduleController = AddScheduleController(this)
        createDayBtn()
        loadSchedule()

        val scheduleAll = scheduleController!!.getSchedule();
        val nextTask = ScheduleUtils.nextTask(scheduleAll)
        if(nextTask != null) {
            addAlarmManager(nextTask)
        }
        activityMainBinding.navigationBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.addSchedule -> startActivity(Intent(this, AddScheduleActivity::class.java));
            }
            false
        }

    }

    fun loadProfile(){
        val scope = CoroutineScope(Dispatchers.IO)
        scope.async {
            val  profile = ProfileRest().loadProfile(ID_PROFILE)
            withContext(Dispatchers.Main) {

                if (profile!=null) {
                    activityMainBinding.friendCount.text = profile.followed.toString()
                    activityMainBinding.likedCount.text = profile.followers.toString()
                    activityMainBinding.nameField.text = profile.username
                    activityMainBinding.altNameField.text = "@${profile.altName}"
                    activityMainBinding.bioField.text = profile.bio
                }else{
                    Log.d("MainActivity_CoroutineScope", "profile is null")
                }
            }
        }
    }

    fun addAlarmManager(taskModel: TaskModel) {
        WorkManager.getInstance().cancelAllWorkByTag("natWorker");
        val data  = Data.Builder().putInt(MyReceiver.ID,taskModel.id)
            .putString(MyReceiver.HEADER,taskModel.header)
            .build()
        val onTimeWorkRequest =    OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInitialDelay(Duration.ofMillis(taskModel.time!!.timeInMillis - Date().time))
            .addTag("natWorker")
            .setInputData(data)
            .build()
        val workManager = WorkManager.getInstance()
        workManager.enqueue(onTimeWorkRequest)
    }

    @SuppressLint("SetTextI18n")
    fun loadSchedule() {

        var isPublic = false
        val scheduleAll = scheduleController!!.getSchedule();
        if(scheduleAll.size == 0){
            return
        }

        scheduleAll.forEach {
            if(it.time == null||it.mode == null){
                scheduleController!!.delSchedule(it.id)
                val appGallery = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                var file = File(appGallery!!.absolutePath + "/${it.id}/")
                if(file.exists()) {
                    file.deleteRecursively()
                }
            }
        }
        val words = arrayListOf("Личные", "Доступные Всем")

        val adapter = PagerAdapterSchedule(this,scheduleAll,focusCalendar,isPublic)
        val pager = activityMainBinding.pager
        val tab = activityMainBinding.scseduleListSwiper
        pager.adapter = adapter
        TabLayoutMediator(tab,pager){tab, position ->
            tab.text = words[position]
            isPublic = words[0].equals(words[position])
        }.attach()
    }

    var focusCalendar = Calendar.getInstance();
    @SuppressLint("ResourceType")
    fun createDayBtn(){
        val today = Calendar.getInstance();
        var todayBatton: Button? = null
        val mondayBtn = activityMainBinding.mondayBtn
        val tuesdayBtn = activityMainBinding.tuesdayBtn
        val wednesdayBth = activityMainBinding.wednesdayBth
        val thursdayBtn = activityMainBinding.thursdayBtn
        val fridayBtn = activityMainBinding.fridayBtn
        val saturdayBtn= activityMainBinding.saturdayBtn
        val sundayBtn = activityMainBinding.sundayBtn

        when (today.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> todayBatton = mondayBtn
            Calendar.TUESDAY -> todayBatton = tuesdayBtn
            Calendar.WEDNESDAY -> todayBatton = wednesdayBth
            Calendar.THURSDAY -> todayBatton = thursdayBtn
            Calendar.FRIDAY -> todayBatton = fridayBtn
            Calendar.SATURDAY -> todayBatton = saturdayBtn
            Calendar.SUNDAY -> todayBatton = sundayBtn
        }
        //todo сделать с обводкой
        todayBatton!!.background= AppCompatResources.getDrawable(
            this,
            R.drawable.calendar_yellow_button
        )
        val onClickListener =View.OnClickListener{
            vineButton(todayBatton)
            if (today!=it) {
                it.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.calendar_shady_button
                )
                if(it==mondayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }else if(it==tuesdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                }else if(it==wednesdayBth){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                }else if(it==thursdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                }else if(it==fridayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                }else if(it==saturdayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                }else if(it==sundayBtn){
                    focusCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }
            }else{
                focusCalendar = Calendar.getInstance()
            }
            loadSchedule()
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
    fun vineButton(today: Button){
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
        loadSchedule()
        val scheduleAll = scheduleController!!.getSchedule();
        val nextTask = ScheduleUtils.nextTask(scheduleAll)
        if(nextTask != null) {
            addAlarmManager(nextTask)
        }
        Log.d("MAIN", "resume");
        super.onResume()
    }
}