package ru.ccoders.clay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import ru.ccoders.clay.main_activity.MainFragment
import java.io.File

class ProfileIconActivity : AppCompatActivity() {
    val REQUEST_CODE = 200

    var imageUris = mutableListOf<Uri>()
    private lateinit var mCropView:CropImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_icon)
        openGalleryForImages()
        mCropView = findViewById<CropImageView>(R.id.cropImageView)
        val btn = findViewById<Button>(R.id.saveIconBtn)
        val ctx= this

        val callback = object : CropCallback {

            override fun onError(e: Throwable?) {
                Log.e("ProfileIconActivity", "failed", e)
            }


            override fun onSuccess(cropped: Bitmap?) {
                val appGallery = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                val file = File(appGallery!!.absolutePath + "/${MainFragment.ID_PROFILE}")
                mCropView.save(cropped).execute(Uri.fromFile(file),null)
                Log.d("ProfileIconActivity", "succer")
            }
        }
        btn.setOnClickListener {

            mCropView.crop(imageUris[0]).execute(callback)
        }

    }

    private fun openGalleryForImages() {

        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE);

    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE) {
                // if multiple images are selected
                Log.d("AddScheduleActivity", "getImage")
                if (data?.getClipData() != null) {
                    var count = data.clipData?.itemCount

                    for (i in 0..count!! - 1) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        imageUris.add(uri)
                        //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                        Log.d("AddScheduleActivity", uri.toString())
                        openEditor(uri)
                    }

                } else if (data?.getData() != null) {
                    val uri = data.data!!
                    imageUris.add(uri)
                    openEditor(uri)
                    //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
                    Log.d("AddScheduleActivity", uri.toString())
                }
            }
        }
    }

    fun openEditor(image:Uri) {


        val callback1 = object : LoadCallback {
            override fun onError(e: Throwable?) {
                Log.e("ProfileIconActivity", "failed", e)
            }

            override fun onSuccess() {
                Log.d("ProfileIconActivity", "succer")
            }


        }

        mCropView.setOutputHeight(100)
        mCropView.setOutputWidth(100)
        mCropView.load(image).execute(callback1)
    }

}