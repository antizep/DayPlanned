package ru.ccoders.clay.utills

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import ru.ccoders.clay.databinding.SheduleLayoutBinding

class ImageUtil {
    fun resizeImage(scheduleLayoutPane: SheduleLayoutBinding, displayWidthPixel: Int) {
        val x = displayWidthPixel
        val height = (x / 1.78).toInt();
        scheduleLayoutPane.info.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = height - scheduleLayoutPane.info.layoutParams.height
        }

        printImage(scheduleLayoutPane.imageCard,scheduleLayoutPane.ImageSchedule,x)

    }

    fun printImage(cardView: View, imageView: View, width: Int) {
        val height = (width / 1.78).toInt();
        cardView.layoutParams.height = height
        imageView.layoutParams.width = width
        imageView.layoutParams.height = height
    }
}