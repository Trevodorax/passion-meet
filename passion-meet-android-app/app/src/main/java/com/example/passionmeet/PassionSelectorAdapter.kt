package com.example.passionmeet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.views.PassionSelectorVH

class PassionSelectorAdapter(
    private val passions: List<PassionSelectorModel>,
    private val onSelectionChanged: (selectedItems: List<PassionSelectorModel>) -> Unit
) : RecyclerView.Adapter<PassionSelectorVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassionSelectorVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.passion_selector, parent, false)
        return PassionSelectorVH(view)
    }

    override fun getItemCount(): Int = passions.size

    override fun onBindViewHolder(holder: PassionSelectorVH, position: Int) {
        val passion = passions[position]

        holder.nameTv.text = passion.name

        // Update the icon based on isSelected property
        if (passion.isSelected) {
            holder.addIcon.setImageResource(R.drawable.ic_confirm)
            holder.addIcon.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.mainAccentuate
                )
            )
        } else {
            holder.addIcon.setImageResource(R.drawable.ic_add)
            holder.addIcon.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_circle)
        }

        holder.itemView.setOnClickListener {
            passion.isSelected = !passion.isSelected  // Toggle selection state
            notifyItemChanged(position)

            // Notify parent of updated selected items
            onSelectionChanged(getSelectedItems())
        }
    }

    fun getSelectedItems(): List<PassionSelectorModel> {
        return passions.filter { it.isSelected }
    }
}
