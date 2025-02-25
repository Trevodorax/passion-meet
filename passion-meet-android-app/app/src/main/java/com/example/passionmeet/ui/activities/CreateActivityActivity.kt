package com.example.passionmeet.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.R
import com.example.passionmeet.databinding.ActivityLoginBinding
import com.example.passionmeet.repositories.SignupRepository
import com.example.passionmeet.utils.getCurrentUserId
import com.example.passionmeet.viewmodel.ActivityViewModel
import com.example.passionmeet.viewmodel.SignupViewModel
import com.example.passionmeet.viewmodel.factories.SignupViewModelFactory

class CreateActivityActivity : AppCompatActivity(){
    private val createActivityViewModel: ActivityViewModel by viewModels()
    private lateinit var nameEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var maxParticipantsEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_activity_form)
        val groupId = intent.getStringExtra("group_id")
        val userId = getCurrentUserId(this)

        this.nameEditText = findViewById(R.id.activity_name_field)
        this.locationEditText = findViewById(R.id.activity_location_field)
        this.descriptionEditText = findViewById(R.id.activity_description_field)
        this.maxParticipantsEditText = findViewById(R.id.activity_max_participant_field)
        this.startDateEditText = findViewById(R.id.activity_start_date_field)
        this.submitButton = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            //loading.visibility = View.VISIBLE
            val name = nameEditText.text.toString()
            val location = locationEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val maxParticipants = maxParticipantsEditText.text.toString()
            val startDate = startDateEditText.text.toString()

            //fake data to match api call
            val type = "activity"
            val endDate = startDateEditText.text.toString()
            val imageUrl = "none"
            //TODO: API connection
            createActivityViewModel.createActivity();
        }

    }
}