package ru.ccoders.clay.controller

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.RequestBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.utills.ImageUtil
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.math.log

//todo наличие записи в преференсы здесь сомнительно
class RestController(val sharedPreferences: SharedPreferences) {
    companion object {

        val domain = "http://antizep.ru";
        val port = 8191;
        val LOGIN_FIELD = "login";
        val PASSWORD_FIELD = "password"
    }

    val TAG = RestController::class.java.canonicalName

    fun authentication(login: String, password: String): Boolean {
        //todo добавить синхронизацию задач
        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(login, password))
            .build()

        val url = "$domain:$port/lifequest/profile/"
        val request = Request.Builder()
            .url(url)
            .build()

        try {

            Log.d("RestController", "test:$url")

            val response = client.newCall(request).execute()
            val body = response.body;

            if (response.code == 200 && body != null) {

                val resp = JSONObject(body.string())
                Log.d(this::class.java.name, resp.toString())
                if (resp.getBoolean("enabled")) {
                    sharedPreferences.edit()
                        .putString(LOGIN_FIELD, login)

                        .putString(PASSWORD_FIELD, password)
                        .apply();
                }
            } else {
                Log.d(this::class.java.name, "authentication null")
                return false
            }

        } catch (e: IOException) {
            e.printStackTrace()
            return false

        }
        return true

    }

    fun downloadSchedule(): List<ScheduleModel> {
        val login = sharedPreferences.getString(LOGIN_FIELD, null)
        val password = sharedPreferences.getString(PASSWORD_FIELD, null)
        val schedules = mutableListOf<ScheduleModel>()
        val url = "$domain:$port/lifequest/schedule/findAll"

        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(login.toString(), password.toString()))
            .build()
        try {


            val remoteSchedulesResponse = client.newCall(
                Request.Builder()
                    .url(url)
                    .build()
            ).execute()

//todo проверки на null
            if (remoteSchedulesResponse.body == null) {
                return mutableListOf()
            }
            val json = JSONArray(remoteSchedulesResponse.body?.string())
            for (i in 0 until json.length()) {
                schedules.add(ScheduleModel.parseJson(json.getJSONObject(i)))
            }
            Log.d(TAG, json.toString())
        } catch (ex: Exception) {
            Log.d(TAG, "error", ex)
        }
        return schedules
    }

    fun downloadImage(remoteId: Long, localId: Int, path: String) {
        val login = sharedPreferences.getString(LOGIN_FIELD, null)
        val password = sharedPreferences.getString(PASSWORD_FIELD, null)
        val url =
            "${RestController.domain}:${RestController.port}/lifequest/schedule/$remoteId/0.JPG"
        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(login!!, password!!))
            .build()
        val response = client.newCall(
            Request.Builder().url(url).build()
        ).execute()
        if (response.code == 200) {
            ImageUtil().saveImageToStorage(
                BitmapFactory.decodeStream(response.body!!.byteStream()),
                0,
                localId,
                path
            )
        }
    }

    fun uploadToServer(scheduleModel: ScheduleModel, file: File): Long {
        val login = sharedPreferences.getString(LOGIN_FIELD, null)
        val password = sharedPreferences.getString(PASSWORD_FIELD, null)
        Log.d(TAG, "request: ${scheduleModel.toJSONObject()}")
        if (login == null || password == null) {
            return 0
        } else {
            val client = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor(login, password))
                .build()
            val url = "$domain:$port/lifequest/schedule/save/"

            Log.d(TAG, file.name)

            val reqBody = MultipartBody.Builder()
            if (file.exists()) {
                val fileRequest = file.asRequestBody("image/jpeg".toMediaType())
                reqBody.addFormDataPart("file", file.name, fileRequest)
            }
            parseJSONObjectToFormData(scheduleModel.toJSONObject(), reqBody)

            val request = Request.Builder()
                .url(url)

                .post(reqBody.build())
                .build();
            try {

                val response = client.newCall(request).execute()
                val respStr = response.body?.string()
                Log.d(TAG, "response: $respStr")
                val resp = JSONObject(respStr)

                return resp.getLong("remoteId")
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, "connection $url failed")

                return -1
            }
        }
    }


    fun checkMailAddress(mailAddress: String): Boolean {
        val client = OkHttpClient.Builder()
            .build()
        val url = "$domain:$port/lifequest/profile/registration/sendMail"
        val requestBody = FormBody.Builder()
            .add("mailAddress", mailAddress)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        return try {
            val response = client.newCall(request).execute()
            return response.code == 200
        } catch (ex: Exception) {
            Log.d(TAG, "Не удалось проверить адрес", ex)
            false
        }
    }

    fun checkMailCode(mailAddress: String, mailCode: String): Boolean {

        val client = OkHttpClient.Builder()
            .build()
        val url =
            "$domain:$port/lifequest/profile/registration/verifyMail?code=$mailCode&mailAddress=$mailAddress"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return try {
            val response = client.newCall(request).execute()

            return response.code == 200

        } catch (ex: Exception) {
            Log.d(TAG, "Не удалось проверить mailCode", ex)
            false
        }
    }

    fun register(mailAddress: String, password: String): Boolean {
        val client = OkHttpClient.Builder()
            .build()
        val url =
            "$domain:$port/lifequest/profile/registration"
        val requestBody = FormBody.Builder()
            .add("username", mailAddress)
            .add(PASSWORD_FIELD, password)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        return try {
            val response = client.newCall(request).execute()
            if (response.code == 200) {
                sharedPreferences.edit()
                    .putString(LOGIN_FIELD, mailAddress)
                    .putString(PASSWORD_FIELD, password)
                    .apply()
                true
            } else {
                false
            }
        } catch (ex: Exception) {
            Log.d(TAG, "Не удалось отправить запрос")
            false
        }
    }

    private fun parseJSONObjectToFormData(jsonObject: JSONObject, builder: MultipartBody.Builder) {
        val keys = jsonObject.keys();
        keys.forEachRemaining {
            builder.addFormDataPart(it, jsonObject.getString(it))
        }
    }
}

class BasicAuthInterceptor(user: String, password: String) :
    Interceptor {

    private val credentials = Credentials.basic(user, password);

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

}