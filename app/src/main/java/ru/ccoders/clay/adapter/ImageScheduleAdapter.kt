package ru.ccoders.clay.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ru.ccoders.clay.R
import kotlin.math.log

class ImageScheduleAdapter(
    private val context:Context,
    private val files: List<Bitmap>,
    private val clickListener: View.OnClickListener
): RecyclerView.Adapter<ImageScheduleAdapter.ViewHolder>() {

    val TAG = "ImageScheduleAdapter"
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val v = view
        val image:ImageView = view.findViewById(R.id.imageScheduleByGalerry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG,"onCreateViewHolder(parent: ViewGroup, viewType: Int)")
       return ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_schedule_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder(holder: ViewHolder, position: Int)")
        if (files.isEmpty()) return
        val bitmap = files[position]
        holder.image.setImageBitmap(bitmap)
        holder.image.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"fun getItemCount(): Int ->"+files.size)
        return files.size
    }
}