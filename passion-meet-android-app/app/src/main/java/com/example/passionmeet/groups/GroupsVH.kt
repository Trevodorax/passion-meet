package com.example.passionmeet.groups

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.R
import com.example.passionmeet.models.GroupModel

class GroupsVH(view: View): RecyclerView.ViewHolder(view) {
    val groupNameTv: TextView = view.findViewById(R.id.group_card_title);
    val groupMembers: TextView = view.findViewById(R.id.group_card_memberCount);
    val groupDescription:TextView = view.findViewById(R.id.group_card_description);
    val groupImage = view.findViewById<ImageView>(R.id.group_card_background)

    fun bind(group: GroupModel) {
        groupNameTv.text = group.name
        groupMembers.text = group.members.toString()
        groupDescription.text = group.description
        //TODO: bind image with URL
    }
}