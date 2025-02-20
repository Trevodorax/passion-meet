package com.example.passionmeet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.ui.groups.GroupsListAdapter
import com.example.passionmeet.viewmodel.GroupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var grouplistAdapter: GroupsListAdapter
    private lateinit var groups: List<GroupModel>
    private lateinit var selectPassionBtn: Button

    private val groupViewModel: GroupViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val headerFragment = HeaderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, headerFragment)
            .commit()

        selectPassionBtn = findViewById(R.id.update_passion_button)
        selectPassionBtn.setOnClickListener {
            // switch to select passion activity
            val intent = Intent(this, SelectPassionActivity::class.java)
            startActivity(intent)
        }

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