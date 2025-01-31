package com.example.passionmeet.encounters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.passionmeet.R

import com.example.passionmeet.databinding.FragmentEncounterBinding
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.models.ShortenedEncounter


class EncounterRecyclerViewAdapter(
    private val context: Context,
    private val lastEncounters: List<ShortenedEncounter>
) : RecyclerView.Adapter<EncounterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncounterVH {
       val view = LayoutInflater.from(context).inflate(R.layout.fragment_encounter, parent, false)
        return EncounterVH(view)
    }

    override fun onBindViewHolder(holder: EncounterVH, position: Int) {
        val lastEncounters = lastEncounters[position]
        holder.bind(lastEncounters)
    }

    override fun getItemCount(): Int = lastEncounters.size

    fun getItem(position: Int): ShortenedEncounter {
        return lastEncounters[position]
    }
}