package ru.ccoders.clay.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.ccoders.clay.R
import ru.ccoders.clay.adapters.ScheduleRecyclerViewAdapter
import ru.ccoders.clay.databinding.FragmentMainBinding
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.utills.ScheduleUtils
import ru.ccoders.clay.viewModel.MainViewModel
import java.util.*


class MainFragment : Fragment() {
    private lateinit var activityMainBinding: FragmentMainBinding

    private lateinit var scheduleLiveData: MutableLiveData<List<ScheduleModel>>;
    private lateinit var scheduleObserver: Observer<List<ScheduleModel>>
    private lateinit var provider:MainViewModel
    private val TAG = MainFragment::class.java.name
    lateinit var ctx: Context

    override fun onStart() {
        super.onStart()
        provider.loadSchedule()
        scheduleObserver = Observer {
            loadSchedule(it);
        }
        scheduleLiveData.observe(this, scheduleObserver)

    }

    override fun onStop() {
        super.onStop()
        scheduleLiveData.removeObserver(scheduleObserver)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = requireContext()

        activityMainBinding = FragmentMainBinding.inflate(layoutInflater)


        provider = ViewModelProvider(this).get(MainViewModel::class.java)
        scheduleLiveData = provider.scheduleLiveData;

        createDayBtn()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return activityMainBinding.root
    }



    @SuppressLint("SetTextI18n")
    fun loadSchedule(scheduleAll: List<ScheduleModel>) {

        val scheduleLayout = activityMainBinding.scheduleLayout

        scheduleLayout.removeAllViews()

        val sorted = ScheduleUtils.sortByDay(scheduleAll, focusCalendar)
        val nextTask = ScheduleUtils.nextTask(sorted)

        var indexTask = 0
        scheduleLayout.layoutManager = LinearLayoutManager(ctx)
        scheduleLayout.adapter = ScheduleRecyclerViewAdapter(sorted)
        if (Objects.nonNull(nextTask)) {
            if (nextTask!!.time!!.get(Calendar.DAY_OF_YEAR) <= Calendar.getInstance()
                    .get(Calendar.DAY_OF_YEAR)
            ) {
                indexTask = sorted.indexOf(nextTask);
            } else {
                indexTask = sorted.size - 1;
            }
            scheduleLayout.post {
                if(scheduleLayout.size > indexTask) {
                    activityMainBinding.SV.scrollTo(0, scheduleLayout[indexTask].top)
                }
                activityMainBinding.SV.computeScroll()
            }
        }
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
            scheduleLiveData.value?.let { it1 -> loadSchedule(it1) }
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
}