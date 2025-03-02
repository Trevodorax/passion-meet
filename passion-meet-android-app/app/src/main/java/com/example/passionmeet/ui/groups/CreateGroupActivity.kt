package com.example.passionmeet.ui.groups

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.HeaderFragment
import com.example.passionmeet.R
import com.example.passionmeet.repositories.GroupRepository
import com.example.passionmeet.utils.getCurrentUserId
import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.factories.GroupViewModelFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CreateGroupActivity: AppCompatActivity() {

    private val groupsViewModel: GroupViewModel by viewModel { parametersOf(this) }

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var imageEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var loading: ProgressBar
    private lateinit var groupPassionDropDown: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group_form)
        enableEdgeToEdge()

        val headerFragment = HeaderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, headerFragment)
            .commit()


        val bundle = intent.extras
        if (bundle != null) {
            val userId = getCurrentUserId(this)

            this.groupPassionDropDown = findViewById(R.id.spinner_passion)
            this.nameEditText = findViewById(R.id.group_name_field)
            this.descriptionEditText = findViewById(R.id.group_description_field)
            this.imageEditText = findViewById(R.id.groupe_image_field)
            this.submitButton = findViewById(R.id.submit_button)
            this.loading = findViewById(R.id.loading)

            // Create a list of options
            val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

            // Create an ArrayAdapter using the list of options
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set the adapter to the spinner
            groupPassionDropDown.adapter = adapter


            submitButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val image = imageEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val passionId = "idunnoyet"

                groupsViewModel.createGroup(name, image, description, userId, passionId)
            }
        }
    }
}
