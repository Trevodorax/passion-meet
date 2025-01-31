package com.example.passionmeet.encounters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

class EncounterVH(view: View): RecyclerView.ViewHolder(view) {
    val encounterNameTV: TextView = view.findViewById(R.id.encouter_card_username);
    val encounterDatetimeTV: TextView = view.findViewById(R.id.encounter_card_datetime);
    val encounterStatusIV: ImageView = view.findViewById(R.id.encounter_card_stars);


    fun bind(encounter: ShortenedEncounter) {
        encounterNameTV.text = encounter.userEncounteredName1
        encounterDatetimeTV.text = encounter.happenedAt;

        if(encounter.status === EncounterStatus.UNSEEN) {
            encounterStatusIV.visibility = View.VISIBLE
        } else {
            encounterStatusIV.visibility = View.GONE
        }
    }
}