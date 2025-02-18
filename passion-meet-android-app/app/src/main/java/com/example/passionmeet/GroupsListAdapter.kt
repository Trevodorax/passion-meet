package com.example.passionmeet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.views.GroupsVH

class GroupsListAdapter(private val context: Context, private val groups: List<GroupModel>) :
    RecyclerView.Adapter<GroupsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsVH {
        val view = LayoutInflater.from(context).inflate(R.layout.group_card, parent, false)
        return GroupsVH(view)
    }

    override fun onBindViewHolder(holder: GroupsVH, position: Int) {
        val group = groups[position]
        holder.bind(group)

        holder.groupCard.setOnClickListener {
            val intent = GroupChatActivity.createIntent(
                context,
                group.id,
                group.name
            )
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun getItem(position: Int): GroupModel {
        return groups[position]
    }
}