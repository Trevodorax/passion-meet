package com.example.passionmeet.ui.activities

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.passionmeet.R
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.views.ActivityVH
// Interface for the click listener
interface OnClickListener {
    fun onClick(position: Int, model: ActivityModel)
}

class ActivityRecyclerViewAdapter(
    private val context: Context,
    private val activities: List<ActivityModel>,
    private var onClickListener: OnClickListener? = null
) : RecyclerView.Adapter<ActivityVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityVH {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_activity, parent, false)
        return ActivityVH(view)
    }

    override fun onBindViewHolder(holder: ActivityVH, position: Int) {
        val activity = activities[position]
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, activity)
        }
        holder.bind(activity)
    }

    override fun getItemCount(): Int = activities.size

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}