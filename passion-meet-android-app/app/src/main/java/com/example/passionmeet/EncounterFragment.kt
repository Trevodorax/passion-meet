package com.example.passionmeet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.mapper.mapEncounterModelTOShortenedEncounterModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter
import com.example.passionmeet.viewmodel.EncounterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * A fragment representing a list of notification of recent encounters.
 */
class EncounterFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var encounterRecyclerViewAdapter: EncounterRecyclerViewAdapter
    private var encounters: List<ShortenedEncounter> = emptyList()

    private val encounterViewModel: EncounterViewModel by viewModel { parametersOf(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("EncounterFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_encounter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EncounterFragment", "onViewCreated")

        recyclerView = view.findViewById(R.id.encounters_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        setupRecyclerView()
        
        observeViewModel()
        encounterViewModel.initialize()
    }

    private fun observeViewModel() {
        encounterViewModel.encounterData.observe(viewLifecycleOwner) { encounters ->
            Log.d("EncounterFragment", "Received ${encounters.size} encounters")
            this.encounters = encounters.map { encounter -> 
                mapEncounterModelTOShortenedEncounterModel(encounter)
            }
            setupRecyclerView()
        }

        encounterViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("EncounterFragment", "Error: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        encounterViewModel.unseenCount.observe(viewLifecycleOwner) { count ->
            Log.d("EncounterFragment", "Unseen count: $count")
            // Update UI with unseen count if needed
        }
    }

    private fun setupRecyclerView() {
        encounterRecyclerViewAdapter = EncounterRecyclerViewAdapter(requireContext(), encounters)
        recyclerView.adapter = encounterRecyclerViewAdapter
    }
}