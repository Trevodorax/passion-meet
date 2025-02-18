package com.example.passionmeet.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

class ActivityVH(view: View): RecyclerView.ViewHolder(view) {

    val activity_sentence_tv: TextView = view.findViewById(R.id.texts_activity_small)
    //val maxParticipants_tv: TextView = view.findViewById(R.id.encounter_card_datetime)

    fun bind(activity: ActivityModel) {
        val activity_sentence_builed = "${activity.createdBy} wants to ${activity.name} at ${activity.location} on ${activity.date}"
        activity_sentence_tv.text = activity_sentence_builed
    }
}