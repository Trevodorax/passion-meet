package com.example.passionmeet.ui.groups

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.GroupPageActivity
import com.example.passionmeet.R
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
            val intent = Intent(context, GroupPageActivity::class.java)
            intent.putExtra("group_id", group.id)
            intent.putExtra("group_name", group.name)
            intent.putExtra("group_description", group.description)
            intent.putExtra("group_image", group.image)
            intent.putExtra("group_members", group.members)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}