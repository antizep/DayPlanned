package ru.ccoders.clay.rest

import ProfileModel
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ru.antizep.russua_victory.dataprovider.rest.AbstractRest
import ru.ccoders.clay.model.ScheduleAndProfile
import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.model.SearchModel

class TaskRest : AbstractRest(), TaskRestInterface {
    val urlCreate = "$DOMAIN/api/task/create/"
    val urlLoadByProfile = "$DOMAIN/api/task/get-with-username/"
    val urlList = "$DOMAIN/api/task/list/"
    val tag = "TaskRest"
    override fun uploadTask(scheduleModel: ScheduleModel): Int {
        val client = OkHttpClient()
        val JSON = "application/json; charset=utf-8".toMediaType()
        val reqBody = scheduleModel.toJSONObject().toString().toRequestBody(JSON)
        Log.d(tag, "response: ${scheduleModel.toJSONObject()}")
        val request = Request.Builder()
            .url(urlCreate)
            .post(reqBody)
            .build()
        try {

            val response = client.newCall(request).execute()
            val respStr = response.body?.string()
            val resp = JSONObject(respStr)
            Log.d(tag, "response: $resp")
            return resp.getInt("id")
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    override fun loadTask(profileId: Int): List<ScheduleModel> {
        val client = OkHttpClient()
        val result = mutableListOf<ScheduleModel>()
        val url = urlLoadByProfile + profileId
        Log.d(tag, "url:$url")
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = client.newCall(request).execute()
            val respStr = response.body?.string()
            val resp = JSONObject(respStr).getJSONArray("results")
            for (i in 0 until resp.length()) {
                result.add(ScheduleModel.parseJson(resp.getJSONObject(i)))
            }
            Log.d(tag, "resp:$respStr")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun loadPage(next: String?): SearchModel {
        val client = OkHttpClient()
        val result = mutableListOf<ScheduleAndProfile>()
        var url: String?
        if (next == null) {
            url = urlList;
        } else {
            url = next
        }

        Log.d(tag, "url:$url")
        val request = Request.Builder()
            .url(url!!)
            .build()
        val response = client.newCall(request).execute()
        val respStr = response.body?.string()
        val responseJ =  JSONObject(respStr);
        val resp = responseJ.getJSONArray("results")
        val next = responseJ.getString("next")
        val previous = responseJ.getString("previous")
        for (i in 0 until resp.length()) {
            val schedule = resp.getJSONObject(i)
            val profileJ = schedule.getJSONObject("profile")
            result.add(ScheduleAndProfile(ScheduleModel.parseJson(schedule),ProfileModel.parseJSON(profileJ)))
        }
        Log.d(tag, "resp: $respStr")
        return SearchModel(next,previous,result)
    }

}