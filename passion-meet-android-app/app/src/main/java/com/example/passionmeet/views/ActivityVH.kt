package com.example.passionmeet.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.mapper.formatDate
import com.example.passionmeet.models.ActivityModel

class ActivityVH(view: View): RecyclerView.ViewHolder(view) {

    private val activitySentenceTv: TextView = view.findViewById(R.id.texts_activity_small)

    fun bind(activity: ActivityModel) {
        val activitySentence = "${activity.createdBy} wants to ${activity.name} at ${activity.location} on the ${formatDate(activity.startDate)}"
        activitySentenceTv.text = activitySentence
    }
}