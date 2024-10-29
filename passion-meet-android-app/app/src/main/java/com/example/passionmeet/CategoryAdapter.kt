package com.example.passionmeet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.CategoryModel

class CategoryAdapter(
    private val categories: List<CategoryModel>
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
        categoryViewHolder.itemsRecyclerView.adapter = PassionSelectorAdapter(category.items)
        categoryViewHolder.itemsRecyclerView.visibility =
            if (category.isExpanded)
                View.VISIBLE else
                View.GONE

        // Toggle category expand/collapse
        categoryViewHolder.categoryTitle.setOnClickListener {
            category.isExpanded = !category.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = categories.size
}
