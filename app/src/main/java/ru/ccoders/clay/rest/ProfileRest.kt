package ru.antizep.russua_victory.dataprovider.rest

import ProfileModel
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.ccoders.clay.rest.ProfileRestInterface

class ProfileRest : AbstractRest(), ProfileRestInterface {
    val tag = "ProfileRest"
    override fun loadProfile(id: Int): ProfileModel? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$domain/api/profile/get/$id")
            .build()
        Log.d(tag,"rest:$domain/api/profile/get/$id")
        try {

            val response = client.newCall(request).execute()
            val  respStr = response.body?.string()
            val resp = JSONObject(respStr)
            Log.d(tag,"resp:$respStr")
            return ProfileModel.parseJSON(resp)
        }catch (e:Exception){
            e.printStackTrace()
            return null;
        }

    }
}