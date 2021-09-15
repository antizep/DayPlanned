package ru.ccoders.clay.model

import ProfileModel
import android.util.Log
import org.json.JSONObject

data class SearchModel constructor(
    val next:String,
    val previous:String,
    val scheduleAndProfile: List<ScheduleAndProfile>
    ){

    companion object{
        private val tag = SearchModel::class.java.canonicalName
        fun parseJSON(searchModel:JSONObject):SearchModel{
            val result = mutableListOf<ScheduleAndProfile>()
            val resp = searchModel.getJSONArray("results")
            val next = searchModel.getString("next")
            val previous = searchModel.getString("previous")
            for (i in 0 until resp.length()) {
                val schedule = resp.getJSONObject(i)
                val profileJ = schedule.getJSONObject("profile")
                result.add(
                    ScheduleAndProfile(
                        ScheduleModel.parseJson(schedule),
                        ProfileModel.parseJSON(profileJ)
                    )
                )
            }
            return SearchModel(next, previous, result)
        }
    }
}