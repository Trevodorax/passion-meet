package com.example.passionmeet.ui.passions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel

class CategoryAdapter(
    private val categories: List<PassionCategoryModel>,
    private val onPassionsSelected: (selectedPassions: List<PassionSelectorModel>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle = view.findViewById<TextView>(R.id.category_title)
        val toggleButton = view.findViewById<ImageView>(R.id.toggle_button)
        val itemsRecyclerView = view.findViewById<RecyclerView>(R.id.items_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = categories[position]
        val categoryViewHolder = holder as CategoryViewHolder

        categoryViewHolder.categoryTitle.text = category.name
        categoryViewHolder.toggleButton.setImageResource(
            if (category.isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
        )

        // Set up the inner RecyclerView for items
        categoryViewHolder.itemsRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val adapter = PassionSelectorAdapter(category.items) { selectedItems ->
            category.items.forEach { item ->
                item.isSelected = selectedItems.contains(item)
            }
            onPassionsSelected(getAllSelectedPassions())
        }
        categoryViewHolder.itemsRecyclerView.adapter = adapter
        categoryViewHolder.itemsRecyclerView.visibility =
            if (category.isExpanded)
                View.VISIBLE else
                View.GONE

        // Toggle category expand/collapse
        categoryViewHolder.categoryTitle.setOnClickListener {
            expandCategory(category, position)
        }
        categoryViewHolder.toggleButton.setOnClickListener {
            expandCategory(category, position)
        }
    }

    private fun expandCategory(
        category: PassionCategoryModel,
        position: Int
    ) {
        category.isExpanded = !category.isExpanded
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = categories.size

    private fun getAllSelectedPassions(): List<PassionSelectorModel> {
        return categories.flatMap { category ->
            category.items.filter { it.isSelected }
        }
    }
}
