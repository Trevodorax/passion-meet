package com.example.passionmeet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.repositories.PassionRepository
import com.example.passionmeet.viewmodel.PassionViewModel
import com.example.passionmeet.viewmodel.factories.PassionViewModelFactory
import com.google.android.flexbox.FlexboxLayout

class SelectPassionActivity : AppCompatActivity() {

    private var passions: List<PassionCategoryModel>? = null
    private lateinit var selectedPassionsContainer: FlexboxLayout

    private val passionViewModel: PassionViewModel by viewModels {
        PassionViewModelFactory(PassionRepository(), this)
    }

    private lateinit var categoryAdapter: CategoryAdapter


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


        fetchData()
    }

    private fun fetchData() {
        this.passionViewModel.passionData.observe(this) { passions ->
            // Update the UI with the fetched data
            setupRecyclerView(passions)
            this.passions = passions
            Log.e("PassionData", "passions-assign to rv $passions")
        }

        this.passionViewModel.fetchAllData()
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


//    private fun updateSelectedPassionsText(selectedPassions: List<PassionSelectorModel>) {
//        val selectedNames = selectedPassions.joinToString(", ") { it.name }
//        selectedPassionsTextView.text = "Selected Passions: $selectedNames"
//    }

    private fun updateSelectedPassions(selectedPassions: List<PassionSelectorModel>) {
        selectedPassionsContainer.removeAllViews() // Clear existing chips

        for (passion in selectedPassions) {
            val chipView = LayoutInflater.from(this).inflate(R.layout.chip_selected_passion, selectedPassionsContainer, false)

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