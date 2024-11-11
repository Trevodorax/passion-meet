package com.example.passionmeet

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.CategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.google.android.flexbox.FlexboxLayout

class SelectPassionActivity : AppCompatActivity() {

    private lateinit var selectedPassionsContainer: FlexboxLayout

    private lateinit var categoryAdapter: CategoryAdapter

    val categories = listOf(
        CategoryModel(
            "Music", listOf(
                PassionSelectorModel("Rap", "https://picsum.photos/1200"),
                PassionSelectorModel("Pop", "https://picsum.photos/1200"),
                PassionSelectorModel("K-POP", "https://picsum.photos/1200")
            )
        ),
        CategoryModel(
            "Sports", listOf(
                PassionSelectorModel("Soccer", "https://picsum.photos/1200"),
                PassionSelectorModel("Basketball", "https://picsum.photos/1200")
            )
        )
        // More categories...
    )


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


        // Setup categories rv
        val categoryRecyclerView = findViewById<RecyclerView>(R.id.category_recycler_view)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categories) { selectedPassions ->
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
                updateSelectedPassions(categories.flatMap { it.items.filter { it.isSelected } })
            }

            selectedPassionsContainer.addView(chipView)
        }
    }

    private fun updateCategoryAdapter() {
        // Notify the adapter that data has changed to refresh UI
        categoryAdapter.notifyDataSetChanged()
    }
}