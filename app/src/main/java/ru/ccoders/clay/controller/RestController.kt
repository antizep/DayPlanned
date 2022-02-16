package ru.ccoders.clay.controller

import android.content.SharedPreferences
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ru.ccoders.clay.dto.ScheduleModel
import java.io.IOException


class RestController(val sharedPreferences: SharedPreferences) {

    private val domain = "http://192.168.0.11";
    private val port = 8181;
    val TAG = RestController::class.java.canonicalName

    fun authentication(login:String,password: String){

        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(login,password))
            .build()

        val url = "$domain:$port/lifequest/profile/"
        val request = Request.Builder()
            .url(url)
            .build()

        try {

            Log.d("RestController","test:$url")

            val response = client.newCall(request).execute()
            val body = response.body;

            if(response.code ==200 && body != null) {

                val resp = JSONObject(body.string ())
                Log.d(this::class.java.name, resp.toString())
                if(resp.getBoolean("enabled")) {
                    sharedPreferences.edit()
                        .putString("login", login)
                        .putString("password", password)
                        .apply();
                }
            }else{

                Log.d(this::class.java.name,"authentication null")

            }

        }catch (e: IOException){

            e.printStackTrace()

        }

    }

    fun uploadToServer(scheduleModel: ScheduleModel) : Long{
        val login = sharedPreferences.getString("login",null)
        val password = sharedPreferences.getString("password",null)
        Log.d(TAG,"request: ${scheduleModel.toJSONObject()}")
        if(login == null || password ==null){
            return 0
        }else {
            val client = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor(login, password))
                .build()
            val url = "$domain:$port/lifequest/schedule/save/"

            val JSON = "application/json; charset=utf-8".toMediaType()
            val reqBody = scheduleModel.toJSONObject().toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url(url)
                .post(reqBody)
                .build();
            Log.d(TAG,"requestBody:"+reqBody.toString())
            val response = client.newCall(request).execute()
            val respStr = response.body?.string()
            val resp = JSONObject(respStr)
            Log.d(TAG, "response: $resp")
            return resp.getLong("remoteId")
        }
    }
}

class BasicAuthInterceptor(user: String, password: String) :
    Interceptor {

    private val credentials = Credentials.basic(user,password);

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

}