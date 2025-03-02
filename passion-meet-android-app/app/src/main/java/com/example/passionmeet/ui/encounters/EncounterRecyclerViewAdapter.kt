package com.example.passionmeet.ui.encounters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.passionmeet.R

import com.example.passionmeet.models.ShortenedEncounter
import com.example.passionmeet.views.EncounterVH


class EncounterRecyclerViewAdapter(
    private val context: Context,
    private val lastEncounters: List<ShortenedEncounter>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<EncounterVH>() {

    interface OnItemClickListener {
        fun onItemClick(encounter: ShortenedEncounter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncounterVH {
       val view = LayoutInflater.from(context).inflate(R.layout.fragment_encounter, parent, false)
        return EncounterVH(view)
    }

    override fun onBindViewHolder(holder: EncounterVH, position: Int) {
        val lastEncounter = lastEncounters[position]
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(lastEncounter)
        }
        holder.bind(lastEncounter)
    }

    override fun getItemCount(): Int = lastEncounters.size

}