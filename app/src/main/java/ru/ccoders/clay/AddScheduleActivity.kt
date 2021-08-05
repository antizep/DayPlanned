package ru.ccoders.clay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.yalantis.ucrop.UCrop
import org.json.JSONArray
import ru.ccoders.clay.controller.AddScheduleController
import ru.ccoders.clay.databinding.ActivityAddScheduleBinding
import ru.ccoders.clay.model.ScheduleModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class AddScheduleActivity : AppCompatActivity() {
    private lateinit var addScheduleBinding: ActivityAddScheduleBinding
    var scheduleController: AddScheduleController? = null
    var scheduleImages: MutableList<Bitmap> = mutableListOf()
    val REQUEST_CODE = 200
    val PIC_CROP = 69
    val cont = this;
    var mode: Int? = null;
    var schedule: String? = null;
    var imageUris = mutableListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {

        scheduleController = AddScheduleController(this)
        Log.d("AHTUNG", "RUNNER")
        super.onCreate(savedInstanceState)

        addScheduleBinding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(addScheduleBinding.root)




        val id = intent.getIntExtra("id", 0);
        val header = intent.getStringExtra("header")
        val descr = intent.getStringExtra("description")
        val t = intent.getStringExtra("time")
        mode = intent.getIntExtra(AddScheduleController.MODE, AddScheduleController.VEEKLY_MODE)
        schedule = intent.getStringExtra(AddScheduleController.SCHEDULE)

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
        addScheduleBinding.navigationBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mainMenuMenu -> {
                    startActivity(
                        Intent(
                            this,
                            AddScheduleActivity::class.java
                        )
                    )
                }
                R.id.addScheduleMenu -> it.isChecked = true
            }
            false
        }
        addSchedule(id, header, descr, t)
    }


    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {

                scheduleImages.clear()
                var imageUri: Uri? = null
                imageUris.forEach {
                    imageUri = Uri.fromFile(File(cacheDir, it.hashCode().toString()))
                    var bitmap: Bitmap? = null
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            imageUri
                        )
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                    }
                    if(bitmap!=null) {
                        scheduleImages.add(bitmap)
                    }
                }
                addScheduleBinding.scheduleImage.setImageBitmap(scheduleImages[0]);
            }

            if (requestCode == REQUEST_CODE) {
                // if multiple images are selected
                Log.d("AddScheduleActivity", "getImage")
                imageUris.clear()
                if (data?.getClipData() != null) {
                    var count = data.clipData?.itemCount

                    for (i in 0..count!! - 1) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        imageUris.add(uri)
                        //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                        Log.d("AddScheduleActivity", uri.toString())

                    }
                    openEditor()
                } else if (data?.getData() != null) {
                    val uri = data.data!!
                    imageUris.add(uri)

                    openEditor()
                    //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
                    Log.d("AddScheduleActivity", uri.toString())
                }
            }

            addScheduleBinding.scheduleImage.setOnClickListener {
                openGalleryForImages()
            }
            addScheduleBinding.addImagesButton.hide()
        }
    }


    private fun saveImageToStorage(image: Bitmap, indexUri: Int, indexSchedule: Int) {
        val appGallery = getExternalFilesDir(DIRECTORY_PICTURES);
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

    private fun openGalleryForImages() {

        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE);

    }

    private fun openEditor() {
        imageUris.forEach {

            UCrop.of(it, Uri.fromFile(File(cacheDir, it.hashCode().toString())))
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(640, 360)
                .start(this);

            Log.d("AddScheduleActivity", it.toString())
        }
    }

    fun addSchedule(id: Int, head: String?, discr: String?, t: String?) {
        val header = addScheduleBinding.textChedule.text;
        val descript = addScheduleBinding.descriptionSchedule.text
        if (id != 0) {
            header.insert(0, head)
            descript.insert(0, discr)
        }

        val publicSwich = addScheduleBinding.publicSwich
        var isPublic = false
        publicSwich.setOnCheckedChangeListener { buttonView, isChecked ->
            isPublic = isChecked
        }
        addScheduleBinding.addScheduleButton.setOnClickListener {
            Log.d("AddScheduleActivity", "H:" + header + " D:" + descript);
            val schedule = ScheduleModel(
                id,
                header.toString(),
                descript.toString(),
                0,
                0,
                0,
                JSONArray()
            );
            var index: Int = 0;
            if (id == 0) {
                if (!header.isBlank()) {
                    index = scheduleController!!.addSchedule(schedule)

                }
            } else {
                scheduleController!!.updateSchedule(schedule)
                index = schedule.id
            }
            if (index > 0) {
                for (ind in scheduleImages.indices) {
                    saveImageToStorage(scheduleImages[ind], ind, index)
                }
                finish()
                val intent = Intent(this, SetPeriodActivity::class.java)
                intent.putExtra("id", index)
                intent.putExtra("time", t)
                intent.putExtra("isPublic", isPublic)
                intent.putExtra(AddScheduleController.MODE, this.mode)
                intent.putExtra(AddScheduleController.SCHEDULE, this.schedule)
                startActivity(intent)
            }
        }
    }
}