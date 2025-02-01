package com.example.passionmeet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.GroupModel

class UserHomeActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var grouplistAdapter: GroupsListAdapter
    private lateinit var groups: List<GroupModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        //Groups recycler view
        recyclerView = findViewById(R.id.groups_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Fake data
        groups = listOf(
            //GroupModel("Pikachu lovers", "https://unsplash.com/fr/photos/cartouche-de-jeu-nintendo-game-boy-pokemon-7P2QXuPmR9I", 90, "A group for Pikachu lovers"),
            GroupModel("Pikachu lovers", "https://robohash.org/testeuuuh", 90, "A group for Pikachu lovers"),
            GroupModel("Salamache lovers", "https://robohash.org/estuh2", 7, "A group for Salamche lovers"),
            GroupModel("Tortipousse lovers", "https://robohash.org/steuuteuhw", 10, "A group for Tortipousse lovers"),
        )
        grouplistAdapter = GroupsListAdapter(this, groups)
        recyclerView.adapter = grouplistAdapter
    }
}