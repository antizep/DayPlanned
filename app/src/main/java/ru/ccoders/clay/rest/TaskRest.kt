package ru.ccoders.clay.rest

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ru.antizep.russua_victory.dataprovider.rest.AbstractRest
import ru.ccoders.clay.model.ScheduleModel

class TaskRest: AbstractRest(),TaskRestInterface {
    val urlCreate = "$DOMAIN/api/task/create/"
    val TAG = "TaskRest"
    override fun uploadTask(scheduleModel: ScheduleModel): Int {
        val client = OkHttpClient()
        val JSON =  "application/json; charset=utf-8".toMediaType()
        val reqBody =  scheduleModel.toJSONObject().toString().toRequestBody(JSON)
        Log.d(TAG,"response: ${scheduleModel.toJSONObject()}")
        val request = Request.Builder()
            .url(urlCreate)
            .post(reqBody)
            .build()
        try {

            val response = client.newCall(request).execute()
            val  respStr = response.body?.string()
            val resp = JSONObject(respStr)
            Log.d(TAG,"response: $resp")
            return resp.getInt("id")
        }catch (e:Exception){
            e.printStackTrace()
            return 0
        }
    }
}