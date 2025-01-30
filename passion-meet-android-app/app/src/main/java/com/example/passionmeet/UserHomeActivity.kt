package com.example.passionmeet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.groups.GroupsListAdapter
import com.example.passionmeet.models.GroupModel

class UserHomeActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var grouplistAdapter: GroupsListAdapter
    private lateinit var groups: List<GroupModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups_list)

        //Groups recycler view
        recyclerView = findViewById(R.id.groups_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Fake data
        groups = listOf(
            GroupModel("Pikachu lovers", "https://pikachu.fr", 10, "A group for Pikachu lovers"),
            GroupModel("Salamache lovers", "https://salameche.fr", 7, "A group for Salamche lovers"),
            GroupModel("Tortipousse lovers", "https://tortipousse.fr", 10, "A group for Tortipousse lovers"),
        )
        grouplistAdapter = GroupsListAdapter(this, groups)
        recyclerView.adapter = grouplistAdapter
    }
}