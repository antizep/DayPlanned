package com.example.dayplanned.model

import android.text.Editable
import java.sql.Time

data class Schedule(val id:Int, val header: String?, val description: String?){
    var time:Time ? = null;
}
