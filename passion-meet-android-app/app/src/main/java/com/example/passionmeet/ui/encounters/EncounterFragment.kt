package com.example.passionmeet.ui.encounters

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.mapper.mapEncounterModelTOShortenedEncounterModel
import com.example.passionmeet.models.ShortenedEncounter
import com.example.passionmeet.viewmodel.EncounterViewModel
import com.example.passionmeet.viewmodel.GroupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * A fragment representing a list of notification of recent encounters.
 */
class EncounterFragment : Fragment(), EncounterRecyclerViewAdapter.OnItemClickListener  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var encounterRecyclerViewAdapter: EncounterRecyclerViewAdapter
    private var encounters: List<ShortenedEncounter> = emptyList()

    private val encounterViewModel: EncounterViewModel by viewModel { parametersOf(requireContext()) }
    private val groupsViewModel: GroupViewModel by viewModel { parametersOf(requireContext()) }

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
                Log.d("EncounterFragment", "Mapping encounter: $encounter")
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
        }
    }

    private fun setupRecyclerView() {
        encounterRecyclerViewAdapter = EncounterRecyclerViewAdapter(requireContext(), encounters, this)
        recyclerView.adapter = encounterRecyclerViewAdapter
    }

    // Implement the OnItemClickListener interface
    override fun onItemClick(encounter: ShortenedEncounter) {
        Log.d("EncounterFragment", "Encounter clicked: ${encounter.groups}")
        showDialog(encounter)
    }

    // Method to show the dialog
    private fun showDialog(encounter: ShortenedEncounter) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Join ${encounter.userEncounteredName1}'s group ?")
        dialogBuilder.setMessage("You and ${encounter.userEncounteredName1} crossed path. Would you like to join their group: Milky way club?")
        dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // Call the view model to join the group
            groupsViewModel.joinGroup("fecce777-7d1a-4dd9-b443-f186327d07a7")
            observeJoinGroupResult(dialog)
        }
        dialogBuilder.setNegativeButton("No") { dialog, _ ->
           dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun observeJoinGroupResult(dialog: DialogInterface) {
        groupsViewModel.joinGroupResultData.observe(viewLifecycleOwner) { result ->
            if(result) {
                Toast.makeText(requireContext(), "You have joined the group", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to join the group", Toast.LENGTH_LONG).show()
            }
        }
        dialog.dismiss()
    }
}