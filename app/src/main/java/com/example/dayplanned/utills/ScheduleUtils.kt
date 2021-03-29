package com.example.dayplanned.utills

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import com.example.dayplanned.controller.AddScheduleController
import com.example.dayplanned.model.Schedule
import org.json.JSONArray

class ScheduleUtils {
    companion object {
        fun excludeNotToday(unsorted: MutableList<Schedule>): MutableList<Schedule> {
            return excludeNotDay(unsorted, Calendar.getInstance())
        }

        fun excludeNotDay(
            unsorted: MutableList<Schedule>,
            calendar: Calendar
        ): MutableList<Schedule> {
            val mutableList: MutableList<Schedule> = mutableListOf();

            unsorted.forEach {

                var i = it.getHour();
                if (it.mode == AddScheduleController.VEEKLY_MODE) {
                    val sch = it.schedule!!
                    val schUSA = JSONArray()
                    schUSA.put(sch[6])
                    schUSA.put(sch[0])
                    schUSA.put(sch[1])
                    schUSA.put(sch[2])
                    schUSA.put(sch[3])
                    schUSA.put(sch[4])
                    schUSA.put(sch[5])
                    val dw = calendar.get(Calendar.DAY_OF_WEEK)
                    if (!schUSA.getBoolean(dw - 1)) {
                        i = -1
                    }
                }
                if (i > 0) {
                    mutableList.add(it)
                }
            }
            return mutableList
        }

        fun minSchedule(unsorted: MutableList<Schedule>): Schedule? {
            return minScheduleByTime(unsorted, -1, -1)
        }

        fun minScheduleByTime(unsorted: MutableList<Schedule>, hour: Int, minute: Int): Schedule? {

            var schedule: Schedule?
            var ph = hour
            var pm = minute
            schedule = null;
            unsorted.forEach {
                Log.d("ScheduleUtils","minScheduleByTime"+it.getTxtTime())
                if (it.getHour() > ph || (it.getHour() == ph && it.getMinute() > pm)){
                    if (schedule == null) {
                        schedule = it;

                    } else if (it.getHour() < schedule!!.getHour()) {
                        schedule = it;
                    } else if (it.getHour() == schedule!!.getHour() && it.getMinute() < schedule!!.getMinute()) {
                        schedule = it;
                    }
                }
                Log.d("ScheduleUtils","minScheduleByTime"+schedule)

            }
            return schedule;
        }

        fun sortByDay(unsorted: MutableList<Schedule>,day:Calendar): MutableList<Schedule> {

            val positive: MutableList<Schedule> = excludeNotDay(unsorted,day);
            unsorted.removeAll(positive);
            val sorted = mutableListOf<Schedule>()
            while (positive.size > 0) {
                val min: Schedule? = minSchedule(positive);
                sorted.add(min!!)
                positive.remove(min)
            }
            return sorted
        }

        fun nextTask(unsorted: MutableList<Schedule>): Schedule? {
            return nextTaskByDay(unsorted, Calendar.getInstance())
        }

        fun nextTaskByDay(unsorted: MutableList<Schedule>, calendar: Calendar): Schedule? {
            if (unsorted.size == 0) {
                return null;
            }
            var todaySchedule = excludeNotDay(unsorted, calendar)
            var addDay = 0;
            while (todaySchedule.size == 0) {
                calendar.add(Calendar.HOUR, 24)
                todaySchedule = excludeNotDay(unsorted, calendar)
                addDay++
            }
            val h = calendar.get(Calendar.HOUR_OF_DAY)
            val m = calendar.get(Calendar.MINUTE)
            var scheduleMinTime = minScheduleByTime(todaySchedule, h, m)
            if (scheduleMinTime == null) {
                calendar.add(Calendar.HOUR, 24)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                scheduleMinTime = nextTaskByDay(unsorted, calendar)
            } else {
                scheduleMinTime.time!!.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
                val dt = formatter.format(scheduleMinTime.time)
                Log.d("ScheduleUtils", dt)
                Log.d("ScheduleUtils", "next task:" + scheduleMinTime.header)
            }
            return scheduleMinTime;
        }
    }
}