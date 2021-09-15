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

    override fun loadTask(profileId: Int): SearchModel {
        val client = OkHttpClient()
        val result = mutableListOf<ScheduleModel>()
        val url = urlLoadByProfile + profileId
        Log.d(tag, "url:$url")
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        val respStr = response.body?.string()

        val resp = JSONObject(respStr)

        Log.d(tag, "resp:$respStr")


        return SearchModel.parseJSON(resp)
    }

    override fun loadPage(next: String?): SearchModel {
        val client = OkHttpClient()

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
        Log.d(tag, "resp: $respStr")
        val responseJ = JSONObject(respStr);

        return SearchModel.parseJSON(responseJ)
    }

}