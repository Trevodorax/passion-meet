package com.example.passionmeet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

/**
 * A fragment representing a list of notification of recent encounters.
 */
class EncounterFragment : Fragment() {

    private lateinit var recylcerView: RecyclerView
    private lateinit var encounterRecyclerViewAdapter: EncounterRecyclerViewAdapter
    private lateinit var encounters: List<ShortenedEncounter>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_encounter_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.recylcerView = view.findViewById(R.id.encounters_recycler_view)
        recylcerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        this.encounters = listOf(
            ShortenedEncounter("Moutoo", "Milowo", EncounterStatus.UNSEEN, "https://robohash.org/milowo", "10/03/2025"),
            ShortenedEncounter("Claronce92", "Milowo", EncounterStatus.UNSEEN, "https://robohash.org/claronce92", "07/03/2025"),
            ShortenedEncounter("Pouleisgone", "Milowo", EncounterStatus.SEEN, "https://robohash.org/pouleisgone", "01/03/2025"),
        )
        this.encounterRecyclerViewAdapter = EncounterRecyclerViewAdapter(requireContext(), encounters)
        this.recylcerView.adapter = encounterRecyclerViewAdapter
    }

}