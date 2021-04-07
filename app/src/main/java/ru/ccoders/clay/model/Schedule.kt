package ru.ccoders.clay.model

import android.icu.util.Calendar
import org.json.JSONArray


data class Schedule(val id:Int, val header: String?, val description: String?, val complete :Int?, val skipped:Int?, val mode: Int?, val schedule: JSONArray?){
    var time:Calendar ? = null;
    companion object{
        var TIEME_NOT:String = "время не задано"
    }
    fun getTxtTime():String{
        if(time == null){
            return TIEME_NOT
        }
       return String.format("%02d:%02d:00", this.time!!.get(Calendar.HOUR_OF_DAY),this.time!!.get(Calendar.MINUTE))
    }
    fun getHour() :Int{
        if(time != null){
            return time!!.get(Calendar.HOUR_OF_DAY)
        }else{
            return -1;
        }
    }

    fun getMinute():Int{
        if(time != null){
            return time!!.get(Calendar.MINUTE)
        }else{
            return -1;
        }
    }
}
