package com.example.passionmeet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.ActivityModel

/**
 * A fragment representing a list of activities.
 */
class ActivityFragment : Fragment() {

    private lateinit var recylcerView: RecyclerView
    private lateinit var activitiesRecyclerView: ActivityRecyclerViewAdapter
    private lateinit var activities: List<ActivityModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activities_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.recylcerView = view.findViewById(R.id.activities_list_recycler_view)
        recylcerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        this.activities = listOf(
        ActivityModel("1","Hiking", "Moutoo", 12, "242 rue du faubourg Saint Antoine, Paris 12ème", "10-03-2025", "Hiking in the mountain", listOf("Moutoo", "Milowo")),
        ActivityModel("2","Boire un café", "Mil0w0", 4, "242 rue du faubourg Saint Antoine, Paris 12ème", "10-03-2025", "Boire un café pour discuter de la vie", listOf("Moutoo", "Milowo")),
         )
        this.activitiesRecyclerView = ActivityRecyclerViewAdapter(requireContext(), activities)
        activitiesRecyclerView.setOnClickListener(object : OnClickListener {
            override fun onClick(position: Int, model: ActivityModel) {
                val intent = Intent(requireContext(), DetailledActivityFragment::class.java)
                val activitySentence= "${model.createdBy} wants to ${model.name} at ${model.location} on ${model.date}"
                intent.putExtra("activity_sentence", activitySentence)
                intent.putExtra("activity_id", model.id)
                intent.putExtra("activity_location", model.location)
                intent.putExtra("activity_description", model.description)
                intent.putExtra("participants", ArrayList(model.participants))
                intent.putExtra("max_participants", model.maxParticipant)
                startActivity(intent)
            }
        })
        this.recylcerView.adapter = activitiesRecyclerView

    }

}