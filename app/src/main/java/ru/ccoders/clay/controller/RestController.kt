package ru.ccoders.clay.controller

import android.content.SharedPreferences
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class RestController(val sharedPreferences: SharedPreferences) {

    private val domain = "http://192.168.0.11";
    private val port = 8181;

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
            val body = response.body();

            if(response.code()==200 && body != null) {

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