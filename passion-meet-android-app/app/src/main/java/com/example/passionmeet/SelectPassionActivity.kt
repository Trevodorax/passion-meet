package com.example.passionmeet

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.ui.passions.CategoryAdapter
import com.example.passionmeet.viewmodel.PassionViewModel
import com.google.android.flexbox.FlexboxLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SelectPassionActivity : AppCompatActivity() {

    private var passions: List<PassionCategoryModel>? = null
    private lateinit var selectedPassionsContainer: FlexboxLayout
    private lateinit var submitButton: Button

    private val passionViewModel: PassionViewModel by viewModel { parametersOf(this) }

    private lateinit var categoryAdapter: CategoryAdapter

    private var initialObservation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_passion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedPassionsContainer = findViewById(R.id.selected_passions_container)
        submitButton = findViewById(R.id.submit_button)
        
        // Reset the update result state
        passionViewModel.resetUpdateResult()
        
        // Set up the submit button click listener
        submitButton.setOnClickListener {
            handleSubmitPassions()
        }

        fetchData()
        observeUpdateResult()
    }

    private fun fetchData() {
        this.passionViewModel.passionData.observe(this) { passions ->
            // Update the UI with the fetched data
            setupRecyclerView(passions)
            this.passions = passions
            Log.e("PassionData", "passions-assign to rv $passions")

            // now get the self passions
            initSelfPassions()
        }

        this.passionViewModel.fetchAllData()
    }
    
    private fun observeUpdateResult() {
        passionViewModel.updatePassionsResult.observe(this) { success ->
            // Reset button state
            submitButton.isEnabled = true
            submitButton.text = getString(R.string.submit_button_label)
            
            // Skip the first observation when activity is created
            if (initialObservation) {
                initialObservation = false
                return@observe
            }
            
            if (success) {
                // navigate back to the previous screen by destroying this activity

                // destroy the observer
                setResult(Activity.RESULT_OK)
                Log.e("SelectPassionActivity", "Passions updated successfully - finishing activity")

                finish()
//                super.onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(this, "Failed to update passions. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Observes the self passions (e.g., passions already chosen by the user) and updates the UI.
     */
    private fun initSelfPassions() {
        this.passionViewModel.selfPassionData.observe(this) { selfPassions ->
            // Iterate over all passions and mark as selected if they appear in selfPassions.
            passions?.forEach { category ->
                category.items.forEach { passion ->
                    // Assume each passion has a unique id and selfPassions is a list of similar models
                    passion.isSelected = selfPassions
                        .filter { it.name == category.name }
                        .flatMap { it.items }
                        .any { it.id == passion.id }
                }
            }
            // Update the selected passions chips and refresh the adapter
            updateSelectedPassions(passions!!.flatMap { it.items.filter { it.isSelected } })
            updateCategoryAdapter()
        }
        // Trigger fetching of the user's own passions
        this.passionViewModel.fetchSelfPassions()
    }


    private fun setupRecyclerView(passionByCategory: List<PassionCategoryModel>) {
        // Setup categories rv
        val categoryRecyclerView = findViewById<RecyclerView>(R.id.category_recycler_view)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(passionByCategory) { selectedPassions ->
            updateSelectedPassions(selectedPassions)
        }
        categoryRecyclerView.adapter = categoryAdapter
    }

    /**
     * Handles the submit button click to update the user's passions.
     */
    private fun handleSubmitPassions() {
        val selectedPassions = passions?.flatMap { it.items.filter { it.isSelected } } ?: emptyList()
        
        if (selectedPassions.isEmpty()) {
            Toast.makeText(this, "Please select at least one passion", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading state (you might want to add a progress indicator)
        submitButton.isEnabled = false
        submitButton.text = "Updating..."
        
        // Call the ViewModel to update passions
        passionViewModel.updatePassions(selectedPassions)
    }

    private fun updateSelectedPassions(selectedPassions: List<PassionSelectorModel>) {
        selectedPassionsContainer.removeAllViews() // Clear existing chips

        for (passion in selectedPassions) {
            val chipView = LayoutInflater.from(this)
                .inflate(R.layout.chip_selected_passion, selectedPassionsContainer, false)

            // Set passion name
            chipView.findViewById<TextView>(R.id.chip_text).text = passion.name

            // Handle remove action
            chipView.findViewById<ImageView>(R.id.chip_close_icon).setOnClickListener {
                // Deselect the passion and update UI
                passion.isSelected = false
                updateCategoryAdapter() // Update the main adapter to reflect deselection
                updateSelectedPassions(passions!!.flatMap { it.items.filter { it.isSelected } })
            }

            selectedPassionsContainer.addView(chipView)
        }
    }

    private fun updateCategoryAdapter() {
        // Notify the adapter that data has changed to refresh UI
        categoryAdapter.notifyDataSetChanged()
    }
}