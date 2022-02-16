package ru.ccoders.clay.utills

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import ru.ccoders.clay.databinding.SheduleLayoutBinding

class ImageUtil {
    fun resizeImage(infoPanel: View, image:ImageView, displayWidthPixel: Int) {
        val x = displayWidthPixel
        val height = (x / 1.78).toInt();
        infoPanel.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = height - infoPanel.layoutParams.height
        }

        printImage(image,displayWidthPixel)

    }

    fun printImage(imageView: View, width: Int) {
        val height = (width / 1.78).toInt();
        imageView.layoutParams.width = width
        imageView.layoutParams.height = height
    }
}