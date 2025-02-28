package com.example.passionmeet.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.ActivityModel

class ActivityVH(view: View): RecyclerView.ViewHolder(view) {

    val activitySentenceTv: TextView = view.findViewById(R.id.texts_activity_small)
    //val maxParticipants_tv: TextView = view.findViewById(R.id.encounter_card_datetime)

    fun bind(activity: ActivityModel) {
        val activitySentence = "${activity.createdBy} wants to ${activity.name} at ${activity.location} on ${activity.startDate}"
        activitySentenceTv.text = activitySentence
    }
}