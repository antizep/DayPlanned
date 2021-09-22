package ru.ccoders.clay.adapter

import ProfileModel
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.Detail
import ru.ccoders.clay.R
import ru.ccoders.clay.main_activity.MainFragment
import ru.ccoders.clay.model.ScheduleAndProfile
import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.utills.ImageUtil
import ru.ccoders.clay.utills.ScheduleUtils
import java.io.File
import kotlin.system.exitProcess

class ScheduleCastomAdapter constructor(
    private var dataSet: List<ScheduleAndProfile>,
    private val context: Context,
    private val day: Calendar?,
    private val isPublic: Boolean
) :
    RecyclerView.Adapter<ScheduleCastomAdapter.ViewHolder>() {

    constructor(dataSet: List<ScheduleAndProfile>, context: Context, isPublic: Boolean) : this(dataSet, context, null, isPublic)

    var profileByScheduleMap: HashMap<ScheduleModel, ProfileModel>
    var sortedSet:List<ScheduleModel>
    init {

        profileByScheduleMap = hashMapOf()
        sortedSet = mutableListOf<ScheduleModel>()
        dataSet.forEach {
            if (it.profileModel != null) {
                profileByScheduleMap.put(it.scheduleModel, it.profileModel)
            }
            (sortedSet as MutableList<ScheduleModel>).add(it.scheduleModel)
        }
        if (day !=null) {
            sortedSet = ScheduleUtils.sort(sortedSet, day, isPublic);
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val panel = view
        val scheduleHeader: TextView
        val completeCounter: TextView
        val canceledCounter: TextView
        val timeScheduleLayout: TextView
        val ImageSchedule: ImageView
        val profileAuthorName: TextView
        val profileAuthorIcon: ImageView
        val info: View

        init {
            // Define click listener for the ViewHolder's View.

            scheduleHeader = view.findViewById(R.id.scheduleHeader)
            completeCounter = view.findViewById(R.id.completeCounter)
            canceledCounter = view.findViewById(R.id.canceledCounter)
            timeScheduleLayout = view.findViewById(R.id.timeScheduleLayout)
            ImageSchedule = view.findViewById(R.id.ImageSchedule)
            profileAuthorName = view.findViewById(R.id.profileAuthorName)
            profileAuthorIcon = view.findViewById(R.id.profileAuthorIco)
            info = view.findViewById(R.id.info)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shedule_layout, viewGroup, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        if (sortedSet.isEmpty()) return
        val schedule = sortedSet[position]
        viewHolder.panel.setOnClickListener {
            Log.d("MainActivity", "click task name:" + schedule.header)
            val intent = Intent(context, Detail::class.java)
            intent.putExtra("id", schedule.id)
            context.startActivity(intent)
        }
        val profileModel = profileByScheduleMap[schedule]
        if (day != null && (schedule.getRemoteId() == 0 || (profileModel != null && profileModel.id == MainFragment.ID_PROFILE))) {
            viewHolder.profileAuthorIcon.visibility = View.GONE
            viewHolder.profileAuthorName.visibility = View.GONE
        }else{
            if (profileModel!=null) {
                viewHolder.profileAuthorName.text = profileModel.username
                //todo урла на картинку профиля
            }
        }

        viewHolder.scheduleHeader.setText(schedule.header);
        viewHolder.completeCounter.setText(schedule.complete.toString())
        viewHolder.canceledCounter.setText(schedule.skipped.toString())
        viewHolder.timeScheduleLayout.setText(schedule.getTxtTimeNotSecond())

        val id = schedule.id
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        var file = File(appGallery!!.absolutePath + "/$id/")
        if (file.exists()) {
            val images = file.listFiles()
            if (images != null && images.size > 0) {
                Glide.with(context).load(images[0]).apply(
                    RequestOptions().signature(
                        ObjectKey(
                            images[0].length()
                        )
                    )
                ).into(viewHolder.ImageSchedule)
            }
        }

        ImageUtil().resizeImage(
            viewHolder.info,
            viewHolder.ImageSchedule,
            context.getResources().getDisplayMetrics().widthPixels
        )

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = sortedSet.size

    fun onItemDismiss(position: Int) {
        //mItems.remove(position)
        notifyItemRemoved(position)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
//            for (i in fromPosition until toPosition) {
//                Collections.swap(mItems, i, i + 1)
//            }
        } else {
//            for (i in fromPosition downTo toPosition + 1) {
//                Collections.swap(mItems, i, i - 1)
//            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

}