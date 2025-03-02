package com.example.passionmeet.ui.groups

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.HeaderFragment
import com.example.passionmeet.R
import com.example.passionmeet.UserHomeActivity
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


            val userId = getCurrentUserId(this)

            this.groupPassionDropDown = findViewById(R.id.spinner_passion)
            this.nameEditText = findViewById(R.id.group_name_field)
            this.descriptionEditText = findViewById(R.id.group_description_field)
            this.imageEditText = findViewById(R.id.groupe_image_field)
            this.submitButton = findViewById(R.id.submit_button)
            this.loading = findViewById(R.id.activity_loading)

            // Create a list of options
            val options = listOf("Peinture", "Musique", "Jeux vidéo", "Astronomie")

            // Create an ArrayAdapter using the list of options
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set the adapter to the spinner
            groupPassionDropDown.adapter = adapter


            submitButton.setOnClickListener {
                submitButton.isEnabled = false
                loading.visibility = View.VISIBLE
                val name = nameEditText.text.toString()
                val image = imageEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val passionId = "2435a8d9-15f0-4519-acf3-f81b74eecea3"

                groupsViewModel.createGroup(name, image, description, userId, passionId)

                groupsViewModel.createGroupData.observe(this) { data ->
                    if(data.id.isNotEmpty()) {
                        groupsViewModel.joinGroup(data.id)

                        groupsViewModel.joinGroupResultData.observe(this) { result ->
                            if(result) {
                                Toast.makeText(this, "Successfully created and joined new group", Toast.LENGTH_SHORT).show()
                                loading.visibility = View.GONE
                                val intent = Intent(this, UserHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        loading.visibility = View.GONE
                        submitButton.isEnabled = true
                        Toast.makeText(this, "Erreur lors de la création du groupe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}
