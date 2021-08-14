package ru.ccoders.clay.model

import android.icu.util.Calendar
import org.json.JSONArray
import org.json.JSONObject
import ru.ccoders.clay.main_activity.MainActivity
import ru.ccoders.clay.controller.SQLScheduleController


data class ScheduleModel constructor(
    val id: Int,
    val header: String?,
    val description: String?,
    val complete: Int?,
    val skipped: Int?,
    var mode: Int?,
    var schedule: JSONArray?,

    ) {
    var time: Calendar? = null;
    private var remoteId = 0
    private var editableTime = true

    constructor(
        id: Int,
        header: String?,
        description: String?,
        complete: Int?,
        skipped: Int?,
        mode: Int?,
        schedule: JSONArray?,
        remoteId: Int, editableTime: Boolean
    ) : this(id, header, description, complete, skipped, mode, schedule) {
        this.remoteId = remoteId
        this.editableTime = editableTime
    }

    companion object {
        var TIEME_NOT: String = "время не задано"
        val HEADER = "header"
        val DESCRIPTION = "description"
        val DAILY = "daily"
        val DAY_OF_WEEK = "day_of_week"
        val TIME = "time"
        val DONE = "done"
        val REJECTED = "rejected"
        val PROFILE = "profile"
        val REMOTE_ID= "remote_id"
    }
    fun getRemoteId():Int{
        return remoteId
    }
    fun setRemoteId(remoteId: Int){
        this.remoteId = remoteId
    }

    fun getTxtTime(): String {
        if (time == null) {
            return TIEME_NOT
        }
        return String.format(
            "%02d:%02d:00",
            this.time!!.get(Calendar.HOUR_OF_DAY),
            this.time!!.get(Calendar.MINUTE)
        )
    }

    fun getTxtTimeNotSecond(): String {
        if (time == null) {
            return TIEME_NOT
        }
        return String.format(
            "%02d:%02d",
            this.time!!.get(Calendar.HOUR_OF_DAY),
            this.time!!.get(Calendar.MINUTE)
        )
    }

    fun getHour(): Int {
        if (time != null) {
            return time!!.get(Calendar.HOUR_OF_DAY)
        } else {
            return -1;
        }
    }

    fun getMinute(): Int {
        if (time != null) {
            return time!!.get(Calendar.MINUTE)
        } else {
            return -1;
        }
    }

    fun toJSONObject(): JSONObject {
        return JSONObject().put(HEADER, header)
            .put(DESCRIPTION, description)
            .put(DAILY, mode == SQLScheduleController.VEEKLY_MODE)
            .put(DAY_OF_WEEK, schedule.toString())
            .put(TIME, getTxtTime())
            .put(DONE, complete)
            .put(REJECTED, skipped)
            .put(PROFILE, MainActivity.ID_PROFILE)
    }
}
