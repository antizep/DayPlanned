package com.example.dayplanned.model

import android.icu.util.Calendar


data class Schedule(val id:Int, val header: String?, val description: String?){
    var time:Calendar ? = null;
    fun getTxtTime():String{
        if(time == null){
            return "время не задано"
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
