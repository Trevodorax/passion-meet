package com.example.passionmeet.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.R
import com.example.passionmeet.utils.getCurrentUserId
import com.example.passionmeet.viewmodel.ActivityViewModel
import com.example.passionmeet.viewmodel.factories.ActivityViewModelFactory
import com.example.passionmeet.repositories.ActivityRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateActivityActivity : AppCompatActivity(){
    private val calendar = Calendar.getInstance()

    private val createActivityViewModel: ActivityViewModel by viewModels {
        ActivityViewModelFactory(ActivityRepository(this), this)
    }
    private lateinit var nameEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var maxParticipantsEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_activity_form)
        val bundle = intent.extras
        if (bundle != null) {
            val groupId = bundle.getString("group_id", "unknown")
            val userId = getCurrentUserId(this)

            this.nameEditText = findViewById(R.id.activity_name_field)
            this.locationEditText = findViewById(R.id.activity_location_field)
            this.descriptionEditText = findViewById(R.id.activity_description_field)
            this.maxParticipantsEditText = findViewById(R.id.activity_max_participant_field)
            this.startDateEditText = findViewById(R.id.activity_start_date_field)
            this.submitButton = findViewById(R.id.submit_button)
            this.loading = findViewById(R.id.activity_loading)

            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            startDateEditText.setText(dateFormat.format(calendar.time))

            // Create the DatePickerDialog
            startDateEditText.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        startDateEditText.setText(dateFormat.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            submitButton.setOnClickListener {
                loading.visibility = View.VISIBLE
                submitButton.isEnabled = false
                val name = nameEditText.text.toString()
                val location = locationEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val maxParticipants = maxParticipantsEditText.text.toString().toInt()
                val startDate = startDateEditText.text.toString()

                createActivityViewModel.createActivity(groupId, userId, startDate, name, description, maxParticipants, location);
            }

            createActivityViewModel.createActivityResult.observe(this) {
                loading.visibility = View.GONE
                submitButton.isEnabled = true
                if(!it){
                    Toast.makeText(this, "Failed activity creation", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Activity created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        return;
    }
}