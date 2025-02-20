package com.example.passionmeet

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.repositories.GroupRepository
import com.example.passionmeet.viewmodel.EncounterViewModel
import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.factories.GroupViewModelFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var grouplistAdapter: GroupsListAdapter
    private lateinit var groups: List<GroupModel>

    private val groupViewModel: GroupViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val headerFragment = HeaderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, headerFragment)
            .commit()


        //Groups recycler view
        recyclerView = findViewById(R.id.groups_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fetchData()
    }

    private fun fetchData() {
        this.groupViewModel.groupData.observe(this) { groups ->
            // Update the UI with the fetched data
            this.groups = groups
            Log.e("UserHomeActivity", "Data received: $groups")
            setupRecyclerView()
        }

        this.groupViewModel.getSelfGroups()
    }

    private fun setupRecyclerView() {
        // Setup groups rv
        grouplistAdapter = GroupsListAdapter(this, groups)
        recyclerView.adapter = grouplistAdapter
    }
}