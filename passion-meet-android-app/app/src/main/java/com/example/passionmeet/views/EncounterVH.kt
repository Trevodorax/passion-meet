package com.example.passionmeet.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passionmeet.R
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

class EncounterVH(view: View): RecyclerView.ViewHolder(view) {
    private val encounterNameTV: TextView = view.findViewById(R.id.encouter_card_username)
    private val encounterDatetimeTV: TextView = view.findViewById(R.id.encounter_card_datetime)
    private val encounterStatusIV: ImageView = view.findViewById(R.id.encounter_card_stars)
    private val encounterProfilePicIV: ImageView = view.findViewById(R.id.encounter_user_avatar)


    fun bind(encounter: ShortenedEncounter) {
        encounterNameTV.text = encounter.userEncounteredName1
        encounterDatetimeTV.text = encounter.happenedAt;
        Glide.with(itemView)
            .load(encounter.profilePic)
            .placeholder(R.drawable.default_avatar_icon_of_social_media_user_vector)
            .into(encounterProfilePicIV)
        if(encounter.status === EncounterStatus.UNSEEN) {
            encounterStatusIV.visibility = View.VISIBLE
        } else {
            encounterStatusIV.visibility = View.GONE
        }
    }
}