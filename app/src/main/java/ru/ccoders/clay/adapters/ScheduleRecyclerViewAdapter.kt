package ru.ccoders.clay.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import ru.ccoders.clay.R
import ru.ccoders.clay.RunActivity
import ru.ccoders.clay.activities.DetailActivity
import ru.ccoders.clay.activities.MainFragment
import ru.ccoders.clay.controller.SQLiteScheduleController
import ru.ccoders.clay.dto.ScheduleModel
import ru.ccoders.clay.utills.ImageUtil
import java.io.File
import java.time.LocalDate

class ScheduleRecyclerViewAdapter(val schedules: List<ScheduleModel>) :
    RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ScheduleRecyclerViewHolder>() {
    private val TAG = ScheduleRecyclerViewAdapter::class.java.name

    private lateinit var context: Context

    class ScheduleRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleHeader = itemView.findViewById<TextView>(R.id.scheduleHeader)
        val completeCounter = itemView.findViewById<TextView>(R.id.completeCounter)
        val canceledCounter = itemView.findViewById<TextView>(R.id.canceledCounter)
        val timeScheduleLayout = itemView.findViewById<TextView>(R.id.timeScheduleLayout)
        val imageSchedule = itemView.findViewById<ImageView>(R.id.ImageSchedule)
        val info = itemView.findViewById<View>(R.id.info)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleRecyclerViewHolder {
        context = parent.context
        val itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.shedule_layout, parent, false)
        return ScheduleRecyclerViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ScheduleRecyclerViewHolder, position: Int) {

        val slp = holder.itemView
        val schedule = schedules[position]
        slp.setOnLongClickListener {
            Log.d(TAG, "LongClick" + it.id)
            createDialog(it.context, schedule)
            true
        }
        slp.setOnClickListener {
            Log.d("MainActivity", "click task name:" + schedule.header)
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("id", schedule.id)
            context.startActivity(intent)
        }
//            scheduleLayoutPane.scheduleBody.setText(schedule.description)

        holder.scheduleHeader.setText(schedule.header);
        holder.completeCounter.setText(schedule.complete.toString())
        holder.canceledCounter.setText(schedule.skipped.toString())
        holder.timeScheduleLayout.setText(schedule.getTxtTimeNotSecond())
        val id = schedule.id
        val appGallery = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val file = File(appGallery!!.absolutePath + "/$id/")

        if (file.exists()) {
            val images = file.listFiles()
            if (images != null && images.size > 0) {
                Glide.with(context).load(images[0]).apply(
                    RequestOptions().signature(
                        ObjectKey(
                            images[0].length()
                        )
                    )
                ).into(holder.imageSchedule)
            }

        }

        ImageUtil().resizeImage(
            holder.info,
            holder.imageSchedule,
            context.getResources().getDisplayMetrics().widthPixels
        )
    }

    override fun getItemCount(): Int {
        return schedules.size
    }


    private fun createDialog(context: Context, schedeModel: ScheduleModel) {
        var dalog = AlertDialog.Builder(context);
        dalog.setTitle(schedeModel.header)
        dalog.setMessage(schedeModel.description)

        if (schedeModel.completeDate == null || !isToday(schedeModel.completeDate!!)) {
            dalog.setPositiveButton(R.string.make_natification, posListener(schedeModel))
            dalog.setNegativeButton(R.string.cancel_natification, negativeListener(schedeModel))
        } else {
            dalog.setNeutralButton(R.string.exit, neitralListener())
        }
        dalog.show()
    }

    private fun isToday(date: LocalDate): Boolean {
        val nd = LocalDate.now()
        Log.d(TAG, "nd:" + nd.toString())
        Log.d(TAG, "date:" + date.toString())

        val res = date.dayOfYear == nd.dayOfYear && nd.year == date.year
        Log.d(TAG, "res:" + res)
        return res
    }

    private fun posListener(scheduleModel: ScheduleModel): DialogInterface.OnClickListener {

        return DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
            run {
                val data = SQLiteScheduleController(this.context)
                data.complete(scheduleModel.id);
                restartActivity()
                dialogInterface.cancel();
            }
        }
    }

    private fun neitralListener(): DialogInterface.OnClickListener? {
        return null
    }

    private fun restartActivity() {
        val intent = Intent(context, RunActivity::class.java)
        context.startActivity(intent)
    }

    private fun negativeListener(scheduleModel: ScheduleModel): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
            run {
                val data = SQLiteScheduleController(this.context)
                restartActivity()
                data.cancel(scheduleModel.id);
                dialogInterface.cancel();
            }

        }
    }
}