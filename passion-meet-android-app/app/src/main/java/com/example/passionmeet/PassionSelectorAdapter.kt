package com.example.passionmeet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.views.PassionSelectorVH

class PassionSelectorAdapter(val passions: List<PassionSelectorModel>) :
    RecyclerView.Adapter<PassionSelectorVH>() {

    private val selectedPositions = mutableSetOf<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassionSelectorVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.passion_selector, parent, false)
        return PassionSelectorVH(view)
    }

    override fun getItemCount(): Int {
        return this.passions.size
    }

    override fun onBindViewHolder(holder: PassionSelectorVH, position: Int) {
        val passion = passions[position]

        holder.nameTv.text = passion.name

//        // Load image using Glide
//        Glide.with(holder.imageTv.context)
//            .load(passion.image)
//            .skipMemoryCache(true)
//            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
//            .into(holder.imageTv)

        // Set icon based on selection state
        if (selectedPositions.contains(position)) {
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
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_circle);
        }

        // Handle click event
        holder.itemView.setOnClickListener {
            if (selectedPositions.contains(position)) {
                // Deselect if already selected
                selectedPositions.remove(position)
            } else {
                // Select if not already selected
                selectedPositions.add(position)
            }
            // Update the icon for the clicked item only
            notifyItemChanged(position)
        }
    }

    fun getSelectedItems(): List<PassionSelectorModel> {
        return selectedPositions.map { passions[it] }
    }
}