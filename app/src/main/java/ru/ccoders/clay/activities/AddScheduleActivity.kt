package ru.ccoders.clay.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.databinding.ActivityAddScheduleBinding
import ru.ccoders.clay.dto.ScheduleModel
import com.yalantis.ucrop.UCrop
import org.json.JSONArray
import ru.ccoders.clay.utills.ImageUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate


class AddScheduleActivity : AppCompatActivity() {
    private lateinit var addScheduleBinding: ActivityAddScheduleBinding
    var SQLScheduleController: SQLiteScheduleController? = null
    var scheduleImages: MutableList<Bitmap> = mutableListOf()
    val REQUEST_CODE = 200
    val PIC_CROP = 69
    val cont = this;
    var mode: Int? = null;
    var schedule: String? = null;
    var imageUri: Uri? = null;
    override fun onCreate(savedInstanceState: Bundle?) {

        SQLScheduleController = SQLiteScheduleController(this)
        Log.d("AHTUNG", "RUNNER")
        super.onCreate(savedInstanceState)

        addScheduleBinding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(addScheduleBinding.root)
        val id = intent.getIntExtra("id", 0);
        val header = intent.getStringExtra("header")
        val descr = intent.getStringExtra("description")
        val t = intent.getStringExtra("time")
        mode =
            intent.getIntExtra(SQLiteScheduleController.MODE, SQLiteScheduleController.VEEKLY_MODE)
        schedule = intent.getStringExtra(SQLiteScheduleController.SCHEDULE)

        val appGallery = getExternalFilesDir(DIRECTORY_PICTURES);
        val file = File(appGallery!!.absolutePath + "/$id/0.JPG")
        if (file.exists()) {

            Glide.with(cont).load(file).apply(RequestOptions().signature(ObjectKey(file.length())))
                .into(
                    addScheduleBinding.scheduleImage
                )
            addScheduleBinding.scheduleImage.setOnClickListener {
                openGalleryForImages()
            }
            addScheduleBinding.addImagesButton.hide()
        } else {
            addScheduleBinding.addImagesButton.setOnClickListener {
                openGalleryForImages()
            }

        }

        addSchedule(id, header, descr, t)
    }


    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PIC_CROP) {
                imageUri = Uri.fromFile(File(cacheDir, imageUri.hashCode().toString()))

                Glide.with(cont).load(imageUri).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        scheduleImages.clear()
                        scheduleImages.add((resource as BitmapDrawable).bitmap)
                        addScheduleBinding.scheduleImage.setImageBitmap((resource as BitmapDrawable).bitmap);
                        //Log.d("AHTUNG",(resource as BitmapDrawable).bitmap.toString())
                        return true
                    }
                }).into(addScheduleBinding.scheduleImage)
            }
            if (requestCode == REQUEST_CODE) {
                // if multiple images are selected
                Log.d("AddScheduleActivity", "getImage")

                if (data?.getClipData() != null) {
                    var count = data.clipData?.itemCount

                    for (i in 0..count!! - 1) {
                        imageUri = data.clipData!!.getItemAt(i).uri
                        //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                        Log.d("AddScheduleActivity", imageUri.toString())

                    }
                    openEditor()
                } else if (data?.getData() != null) {
                    // if single image is selected

                    imageUri = data.data!!

                    openEditor()
                    //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
                    Log.d("AddScheduleActivity", imageUri.toString())
                }
            }

            addScheduleBinding.scheduleImage.setOnClickListener {
                openGalleryForImages()
            }
            addScheduleBinding.addImagesButton.hide()
        }
    }


    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE
            )
        } else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    private fun openEditor() {
        val u = UCrop.of(imageUri!!, Uri.fromFile(File(cacheDir, imageUri.hashCode().toString())))
            .withAspectRatio(16F, 9F)
            .withMaxResultSize(640, 360)
            .start(this);
        Log.d("AddScheduleActivity", imageUri.toString())
    }

    fun addSchedule(id: Int, head: String?, discr: String?, t: String?) {
        val header = addScheduleBinding.textChedule.text;
        val descript = addScheduleBinding.descriptionSchedule.text
        if (id != 0) {
            header.insert(0, head)
            descript.insert(0, discr)
        }
        addScheduleBinding.addScheduleButton.setOnClickListener {
            Log.d("AddScheduleActivity", "H:" + header + " D:" + descript);
            var schedule: ScheduleModel
            var index: Int = 0;
            if (id == 0) {
                schedule = ScheduleModel(
                    id,
                    header.toString(),
                    descript.toString(),
                    0,
                    0,
                    0,
                    LocalDate.now(),
                    JSONArray()
                );
                if (!header.isBlank()) {
                    index = SQLScheduleController!!.addSchedule(schedule)

                }
            } else {
                schedule = SQLScheduleController!!.getScheduleById(id)
                schedule.header = header.toString()
                schedule.description = descript.toString()

                SQLScheduleController!!.updateSchedule(schedule)
                index = schedule.id
            }
            if (index > 0) {
                for (ind in scheduleImages.indices) {
                    val appGallery = getExternalFilesDir(DIRECTORY_PICTURES);
                    ImageUtil().saveImageToStorage(
                        scheduleImages[ind], ind, index,
                        appGallery.toString()
                    )
                }
                finish()
                val intent = Intent(this, SetPeriodActivity::class.java)
                intent.putExtra("id", index)
                intent.putExtra("time", t)
                intent.putExtra(SQLiteScheduleController.MODE, this.mode)
                intent.putExtra(SQLiteScheduleController.SCHEDULE, this.schedule)
                startActivity(intent)
            }
        }
    }
}