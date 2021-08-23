package ru.ccoders.clay.add_schedule

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yalantis.ucrop.UCrop
import org.json.JSONArray
import ru.ccoders.clay.SetPeriodActivity
import ru.ccoders.clay.adapter.ImageScheduleAdapter
import ru.ccoders.clay.controller.SQLScheduleController
import ru.ccoders.clay.databinding.FragmentAddScheduleBinding
import ru.ccoders.clay.model.ScheduleModel
import java.io.File


class AddScheduleFragment : Fragment() {

    private val TAG = AddScheduleFragment::class.java.canonicalName
    private lateinit var addScheduleBinding: FragmentAddScheduleBinding
    private lateinit var addScheduleViewModel: AddScheduleViewModel
    var scheduleImages: MutableList<Bitmap> = mutableListOf()
    val REQUEST_CODE = 200
    val PIC_CROP = 69
    val cont = this;
    var mode: Int? = null;
    var schedule: String? = null;
    var imageUris = mutableListOf<Uri>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return addScheduleBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("AHTUNG", "RUNNER")
        super.onCreate(savedInstanceState)

        addScheduleBinding = FragmentAddScheduleBinding.inflate(layoutInflater)
//        setContentView(addScheduleBinding.root)

        addScheduleViewModel = ViewModelProvider(this).get(AddScheduleViewModel::class.java)

        val id = requireActivity().intent.getIntExtra("idEdit", 0);
        val header = requireActivity().intent.getStringExtra("header")
        val descr = requireActivity().intent.getStringExtra("description")
        val t = requireActivity().intent.getStringExtra("time")
        mode = requireActivity().intent.getIntExtra(SQLScheduleController.MODE, SQLScheduleController.VEEKLY_MODE)
        schedule = requireActivity().intent.getStringExtra(SQLScheduleController.SCHEDULE)




        val appGallery = requireContext().getExternalFilesDir(DIRECTORY_PICTURES);
        val file = File(appGallery!!.absolutePath + "/$id/")
        if (file.exists()) {

            val files = file.listFiles()
            if (files.isNotEmpty()){
                addScheduleBinding.addImagesButton.hide()
                files.forEach {
                    uriToBitmap(Uri.fromFile(it))
                }
            }
        }
        updateImages()

        addSchedule(id, header, descr, t)
    }
    fun updateImages(){
        val imageViewPager2 = addScheduleBinding.imageViewPager2
        val imageScheduleAdapter = ImageScheduleAdapter(requireContext(),scheduleImages, View.OnClickListener {openGalleryForImages()})
        imageViewPager2.adapter = imageScheduleAdapter
        addScheduleBinding.addImagesButton.setOnClickListener {
            openGalleryForImages()
        }
    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {

                scheduleImages.clear()
                var imageUri: Uri? = null
                imageUris.forEach {
                    imageUri = Uri.fromFile(File(requireContext().cacheDir, it.hashCode().toString()))
                    uriToBitmap(imageUri!!)
                }
                updateImages()
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

            addScheduleBinding.imageViewPager2.setOnClickListener {
                openGalleryForImages()
            }
            addScheduleBinding.addImagesButton.hide()
        }
    }

    private fun uriToBitmap(uri: Uri){
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT < 28) {
            bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            bitmap = ImageDecoder.decodeBitmap(source)
        }
        if (bitmap != null) {
            scheduleImages.add(bitmap)
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

            UCrop.of(it, Uri.fromFile(File(requireContext().cacheDir, it.hashCode().toString())))
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(640, 360)
                .start(requireContext(),this);

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
            Log.d(TAG, "H:" + header + " D:" + descript);
            val schedule = ScheduleModel(
                id,
                header.toString(),
                descript.toString(),
                0,
                0,
                0,
                JSONArray()
            );
            addScheduleViewModel.saveSchedule(schedule)
            val index = schedule.id
            Log.d(TAG,"id is not 0:"+(index>0))
            if (index > 0) {
                for (ind in scheduleImages.indices) {
                    addScheduleViewModel.saveImageToStorage(scheduleImages[ind], ind, index)
                }
                val intent = Intent(requireContext(), SetPeriodActivity::class.java)
                intent.putExtra("id", index)
                intent.putExtra("time", t)
                intent.putExtra("isPublic", isPublic)
                intent.putExtra(SQLScheduleController.MODE, this.mode)
                intent.putExtra(SQLScheduleController.SCHEDULE, this.schedule)
                startActivity(intent)
            }
        }
    }

}