package com.example.passionmeet.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.R
import com.mapbox.maps.plugin.Plugin

/**
 * A fragment representing the activity focused on.
 */
class DetailledActivityFragment : AppCompatActivity() {

    private lateinit var activitySentenceTV: TextView;
    private lateinit var activityDescriptionTV: TextView;
    private lateinit var activityParticipantsTV: TextView;
    private lateinit var activityMaxParticipantsTV: TextView;
    private lateinit var activityMap: Plugin.Mapbox;
    private lateinit var activitySignUpBtn: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_activity_focussed)

        val bundle = intent.extras
        if (bundle != null) {
            val activitySentence = bundle.getString("activity_sentence")
            val activityId = bundle.getString("activity_id")
            val activityDescription = bundle.getString("activity_description")
            val activityLocation = bundle.getString("activity_location")
            val maxParticipants = bundle.getString("max_participants")
            val participants = bundle.getStringArrayList("participants")

            activitySentenceTV = findViewById(R.id.texts_activity)
            activityDescriptionTV = findViewById(R.id.activity_description_content_tv)
            activityParticipantsTV = findViewById(R.id.activity_list_participants)
            activityMaxParticipantsTV= findViewById(R.id.activity_participants_max_tv)

            //TODO: Add map adress pointer c.f other map branch
            activitySentenceTV.text = activitySentence
            activityDescriptionTV.text = activityDescription
            activityParticipantsTV.text = participants?.joinToString(", ")
            activityMaxParticipantsTV.text = maxParticipants
        }

    }
}