package ru.ccoders.clay.main_activity

import PagerAdapterSchedule
import ProfileModel
import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ru.ccoders.clay.R
import ru.ccoders.clay.databinding.FragmentMainBinding
import ru.ccoders.clay.model.SearchModel


class MainFragment : Fragment() {

    private lateinit var activityMainBinding: FragmentMainBinding
    lateinit var ctx: Context

    lateinit var  scheduleListLiveData:ScheduleLiveData
    lateinit var  profileLiveData:MutableLiveData<ProfileModel>

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
        ctx = requireContext()
        activityMainBinding = FragmentMainBinding.inflate(layoutInflater)




        val provider: MainActivityViewModel by lazy {
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        }
        profileLiveData = provider.profileLiveData
        scheduleListLiveData = provider.scheduleListLiveData
        provider.loadProfile()
        provider.loadSchedules()

        profileLiveData.observe(this, Observer {
            activityMainBinding.followerCount.text = it.followed.toString()
            activityMainBinding.subscriberCount.text = it.followers.toString()
            activityMainBinding.nameField.text = it.username
            activityMainBinding.altNameField.text = "@${it.altName}"
            activityMainBinding.bioField.text = it.bio

        })

        val followerListener:View.OnClickListener= View.OnClickListener {
            it.findNavController().navigate(R.id.myFollowerFragment)
        }
        activityMainBinding.followerIco.setOnClickListener(followerListener)
        activityMainBinding.followerCount.setOnClickListener(followerListener)
        activityMainBinding.subscriberCount.setOnClickListener(followerListener)
        activityMainBinding.subscriberIco.setOnClickListener(followerListener)

        activityMainBinding.messagerIco.setOnClickListener {
            it.findNavController().navigate(R.id.messageFragment)
        }

        printSchedule()
        createDayBtn()



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return activityMainBinding.root
    }


    fun printSchedule() {

        scheduleListLiveData.observe(this, Observer {
            var isPublic = false


            val words = arrayListOf("Личные", "Доступные Всем")
            Log.d(tag,"refresh schedules")
            val adapter = PagerAdapterSchedule(ctx, it, focusCalendar, isPublic)
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
            ctx,
            R.drawable.calendar_yellow_button
        )
        val onClickListener = View.OnClickListener {
            vineButton(todayBatton)
            if (today != it) {
                it.background = AppCompatResources.getDrawable(
                    ctx,
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
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.tuesdayBtn.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.wednesdayBth.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.thursdayBtn.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.fridayBtn.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.saturdayBtn.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        activityMainBinding.sundayBtn.background = AppCompatResources.getDrawable(
            ctx,
            R.drawable.calendar_inactive_button
        )
        today.background = AppCompatResources.getDrawable(ctx, R.drawable.calendar_yellow_button)
    }

    override fun onResume() {

        Log.d("MAIN", "resume");
        super.onResume()
    }
}