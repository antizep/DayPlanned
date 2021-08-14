package ru.ccoders.clay.add_schedule

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.model.ScheduleModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class AddScheduleViewModel(application: Application) :AndroidViewModel(application) {

    val context = application
    var scheduleController: SQLScheduleController? = SQLScheduleController(application)


    fun saveImageToStorage(image: Bitmap, indexUri: Int, indexSchedule: Int) {
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var file = File(appGallery!!.absolutePath + "/$indexSchedule/")
        if (!file.exists()) {
            file.mkdir()
        }
        file = File(file.absolutePath + "/$indexUri.JPG")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the output stream
            stream.flush()

            // Close the output stream
            stream.close()

        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

    }

    fun saveSchedule(scheduleModel: ScheduleModel){
        if(scheduleModel.id==0){
            scheduleController!!.addSchedule(scheduleModel)
        }else{
            scheduleController!!.updateSchedule(scheduleModel)
        }
    }

}