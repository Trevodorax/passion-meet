package com.example.passionmeet.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.GroupModel

class GroupsListAdapter(private val context: Context, private val groups: List<GroupModel>): RecyclerView.Adapter<GroupsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsVH {
        val view = LayoutInflater.from(context).inflate(R.layout.group_card, parent, false)
        return GroupsVH(view)
    }

    override fun onBindViewHolder(holder: GroupsVH, position: Int) {
        val group = groups[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun getItem(position: Int): GroupModel {
        return groups[position]
    }
}