package com.example.passionmeet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.FullMapActivity
import com.example.passionmeet.R
import com.example.passionmeet.mapper.formatDate
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.viewmodel.ActivityViewModel
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A fragment representing a list of activities.
 */
class ActivityFragment : Fragment() {

    private val activityViewModel : ActivityViewModel by viewModel { parametersOf(this) }
    private lateinit var recylcerView: RecyclerView
    private lateinit var activitiesRecyclerView: ActivityRecyclerViewAdapter
    private var activities: List<ActivityModel> = emptyList()
    private lateinit var groupId: String
    private lateinit var fullActivitiesMap: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activities_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("ActivityFragment", "View created")
        super.onViewCreated(view, savedInstanceState)


        this.fullActivitiesMap = view.findViewById(R.id.see_group_activities_map)
        fullActivitiesMap.setOnClickListener {
            val intent = Intent(requireContext(), FullMapActivity::class.java)
            intent.putExtra("group_id", groupId)
            intent.putExtra("activities", ArrayList(activities))
            startActivity(intent)
        }

        this.recylcerView = view.findViewById(R.id.activities_list_recycler_view)
        recylcerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        this.groupId= arguments?.getString("group_id") ?: ""
        Log.d("ActivityFragment", "Group ID: $groupId")
        this.activitiesRecyclerView = ActivityRecyclerViewAdapter(requireContext(), activities)
        this.recylcerView.adapter = activitiesRecyclerView

        observeViewModel()
        activityViewModel.initialize(groupId)
    }

    private fun observeViewModel() {
        this.activityViewModel.activitiesData.observe(viewLifecycleOwner) { activities ->
            Log.e("ActivityFragment", "Data received 2: $activities")
            this.activities = activities
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView() {
        this.activitiesRecyclerView = ActivityRecyclerViewAdapter(requireContext(), activities)
        this.recylcerView.adapter = activitiesRecyclerView

        activitiesRecyclerView.setOnClickListener(object : OnClickListener {
            override fun onClick(position: Int, model: ActivityModel) {
                val intent = Intent(requireContext(), DetailledActivityFragment::class.java)
                val participantsNames = model.participants.map { it.username }
                val activitySentence= "${model.createdBy} wants to ${model.name} at ${model.location} on ${formatDate(model.startDate)}"
                intent.putExtra("activity_sentence", activitySentence)
                intent.putExtra("activity_id", model.id)
                intent.putExtra("activity_location", model.location)
                intent.putExtra("activity_description", model.description)
                intent.putExtra("participants", ArrayList(participantsNames))
                intent.putExtra("max_participants", model.maxParticipants.toString() + " max")
                startActivity(intent)
            }
        })
    }


}