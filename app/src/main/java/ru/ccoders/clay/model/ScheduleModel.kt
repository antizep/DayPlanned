package ru.ccoders.clay.model

import android.icu.util.Calendar
import org.json.JSONArray
import org.json.JSONObject
import ru.ccoders.clay.main_activity.MainFragment
import ru.ccoders.clay.controller.SQLScheduleController
import java.sql.Time


data class ScheduleModel constructor(
    var id: Int,
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
        val EDIT_TIME = "edit_time"
        val ID= "id"
        fun parseJson(jsonObject: JSONObject):ScheduleModel{

            val mode = if (jsonObject.getBoolean(DAILY)) SQLScheduleController.DAILY_MODE else SQLScheduleController.VEEKLY_MODE
            val schedule = ScheduleModel(0,
                jsonObject.getString(HEADER),
                jsonObject.optString(DESCRIPTION),
                jsonObject.getInt(DONE),
                jsonObject.getInt(REJECTED),
                mode,
                JSONArray(jsonObject.getString(DAY_OF_WEEK)),
                jsonObject.getInt(ID),
                jsonObject.getBoolean(EDIT_TIME)
            )

            val time = Time.valueOf(jsonObject.optString(TIME))
            schedule.time = Calendar.getInstance();
            schedule.time!!.set(Calendar.HOUR_OF_DAY,time.hours)
            schedule.time!!.set(Calendar.MINUTE,time.minutes)
            schedule.time!!.set(Calendar.SECOND,0)
            return schedule
        }
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
            .put(DAILY, mode == SQLScheduleController.DAILY_MODE)
            .put(DAY_OF_WEEK, schedule.toString())
            .put(TIME, getTxtTime())
            .put(DONE, complete)
            .put(REJECTED, skipped)
            .put(REMOTE_ID,remoteId)
            .put(PROFILE, MainFragment.ID_PROFILE)
    }
}
