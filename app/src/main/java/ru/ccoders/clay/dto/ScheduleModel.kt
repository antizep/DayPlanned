package ru.ccoders.clay.dto

import android.icu.util.Calendar
import org.json.JSONArray
import org.json.JSONObject
import ru.ccoders.clay.controller.SQLiteScheduleController
import java.sql.Date
import java.sql.Time
import java.time.LocalDate


data class ScheduleModel constructor(
    var id: Int,
    var header: String?,
    var description: String?,
    var complete: Int = 0,
    var skipped: Int = 0,
    var mode: Int?,
    var completeDate: LocalDate?,
    var schedule: JSONArray?,
    ) {
    var time: Calendar? = null;
    private var remoteId = 0L
    private var editableTime = true

    constructor(
        id: Int,
        header: String?,
        description: String?,
        complete: Int = 0,
        skipped: Int = 0,
        mode: Int?,
        schedule: JSONArray?,
        remoteId: Long, editableTime: Boolean
    ) : this(id, header, description, complete, skipped, mode,null, schedule) {
        this.completeDate= null
        this.remoteId = remoteId
        this.editableTime = editableTime
    }

    companion object {
        var TIEME_NOT: String = "время не задано"
        val HEADER = "header"
        val DESCRIPTION = "description"
        val DAILY = "daily"
        val DAY_OF_WEEK = "dayOfWeek"
        val TIME = "time"
        val DONE = "done"
        val REJECTED = "rejected"
        val PROFILE = "profile"
        val REMOTE_ID = "remoteId"
        val EDIT_TIME = "edit_time"
        val ID = "id"
        fun parseJson(jsonObject: JSONObject): ScheduleModel {

            val mode =
                if (jsonObject.getBoolean(DAILY)) SQLiteScheduleController.DAILY_MODE else SQLiteScheduleController.VEEKLY_MODE
            val schedule = ScheduleModel(
                0,
                jsonObject.getString(HEADER),
                jsonObject.optString(DESCRIPTION),
                jsonObject.getInt(DONE),
                jsonObject.getInt(REJECTED),
                mode,
                JSONArray(jsonObject.getString(DAY_OF_WEEK)),
                jsonObject.optLong(ID, 0),
                jsonObject.optBoolean(EDIT_TIME, false)
            )

            val time = Time.valueOf(jsonObject.optString(TIME))
            schedule.time = Calendar.getInstance();
            schedule.remoteId = jsonObject.optLong(REMOTE_ID)
            schedule.time!!.set(Calendar.HOUR_OF_DAY, time.hours)
            schedule.time!!.set(Calendar.MINUTE, time.minutes)
            schedule.time!!.set(Calendar.SECOND, 0)
            return schedule
        }
    }

    fun getRemoteId(): Long {
        return remoteId
    }

    fun setRemoteId(remoteId: Long) {
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
            .put(DAILY, mode == SQLiteScheduleController.DAILY_MODE)
            .put(DAY_OF_WEEK, schedule.toString())
            .put(TIME, getTxtTime())
            .put(DONE, complete)
            .put(REJECTED, skipped)
            .put(REMOTE_ID, remoteId)
    }
}