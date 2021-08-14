package ru.ccoders.clay.utills

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.model.ScheduleModel
import org.json.JSONArray

class ScheduleUtils {
    companion object {
        fun excludeNotToday(unsorted: MutableList<ScheduleModel>): MutableList<ScheduleModel> {
            return excludeNotDay(unsorted, Calendar.getInstance())
        }

        fun excludeNotDay(
            unsorted: List<ScheduleModel>,
            calendar: Calendar
        ): MutableList<ScheduleModel> {
            val mutableList: MutableList<ScheduleModel> = mutableListOf();

            unsorted.forEach {

                var i = it.getHour();
                if (it.mode == SQLScheduleController.VEEKLY_MODE) {
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

        fun minSchedule(unsorted: MutableList<ScheduleModel>): ScheduleModel? {
            return minScheduleByTime(unsorted, -1, -1)
        }

        fun minScheduleByTime(unsorted: MutableList<ScheduleModel>, hour: Int, minute: Int): ScheduleModel? {

            var scheduleModel: ScheduleModel?
            var ph = hour
            var pm = minute
            scheduleModel = null;
            unsorted.forEach {
                Log.d("ScheduleUtils","minScheduleByTime"+it.getTxtTime())
                if (it.getHour() > ph || (it.getHour() == ph && it.getMinute() > pm)){
                    if (scheduleModel == null) {
                        scheduleModel = it;

                    } else if (it.getHour() < scheduleModel!!.getHour()) {
                        scheduleModel = it;
                    } else if (it.getHour() == scheduleModel!!.getHour() && it.getMinute() < scheduleModel!!.getMinute()) {
                        scheduleModel = it;
                    }
                }
                Log.d("ScheduleUtils","minScheduleByTime"+scheduleModel)

            }
            return scheduleModel;
        }

        fun sort(unsorted: List<ScheduleModel>, day:Calendar, isPublic:Boolean):MutableList<ScheduleModel>{
            val ret = mutableListOf<ScheduleModel>()
            unsorted.forEach {
                if (isPublic) {
                    if (it.getRemoteId() > 0) {
                        ret.add(it)
                    }
                }else{
                    if (it.getRemoteId() == 0) {
                        ret.add(it)
                    }
                }
            }
            return sortByDay(ret,day)
        }
        fun sortByDay(unsorted: MutableList<ScheduleModel>, day:Calendar): MutableList<ScheduleModel> {
            val unsortedC = ArrayList(unsorted.toList())
            val positive: MutableList<ScheduleModel> = excludeNotDay(unsortedC,day);
            unsortedC.removeAll(positive);
            val sorted = mutableListOf<ScheduleModel>()
            while (positive.size > 0) {
                val min: ScheduleModel? = minSchedule(positive);
                sorted.add(min!!)
                positive.remove(min)
            }
            return sorted
        }

        fun nextTask(unsorted: List<ScheduleModel>): ScheduleModel? {
            return nextTaskByDay(unsorted, Calendar.getInstance())
        }

        fun nextTaskByDay(unsorted: List<ScheduleModel>, calendar: Calendar): ScheduleModel? {
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