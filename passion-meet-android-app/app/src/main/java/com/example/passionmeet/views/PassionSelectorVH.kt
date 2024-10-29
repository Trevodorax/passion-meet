package com.example.passionmeet.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.google.android.material.imageview.ShapeableImageView

class PassionSelectorVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTv: TextView = itemView.findViewById(R.id.story_header_name_tv)
    val imageTv: ShapeableImageView = itemView.findViewById(R.id.story_header_image)
    val addIcon: ImageView = itemView.findViewById(R.id.add_icon)
}

