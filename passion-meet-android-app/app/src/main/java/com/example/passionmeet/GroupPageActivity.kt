package com.example.passionmeet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.passionmeet.data.local.dao.GroupDao
import com.example.passionmeet.repositories.ActivityRepository
import com.example.passionmeet.repositories.GroupRepository
import com.example.passionmeet.ui.activities.ActivityFragment
import com.example.passionmeet.ui.activities.CreateActivityActivity
import com.example.passionmeet.viewmodel.ActivityViewModel
import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.factories.ActivityViewModelFactory
import com.example.passionmeet.viewmodel.factories.GroupViewModelFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GroupPageActivity : AppCompatActivity() {

    //TODO: implement data from intent so far it's just the layout data
    private lateinit var groupChatBtn: CardView
    private lateinit var creatActivityBtn: Button
    private lateinit var fullActivitiesMap: Button
    private lateinit var leaveGroupBtn: Button
    private val groupViewModel: GroupViewModel by viewModel { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        enableEdgeToEdge()

        val headerFragment = HeaderFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_container, headerFragment)
            .commit()

        val activitiesRVFragement = ActivityFragment()
        val bundleForFragment = Bundle()

        val bundle = intent.extras
        if (bundle != null) {
            val groupId = bundle.getString("group_id", "unknown")
            val groupName = bundle.getString("group_name")

            bundleForFragment.putString("group_id", groupId)
            activitiesRVFragement.arguments = bundleForFragment

            this.fullActivitiesMap = findViewById(R.id.see_group_activities_map)
            fullActivitiesMap.setOnClickListener {
                val intent = Intent(this, FullMapActivity::class.java)
                intent.putExtra("group_id", groupId)
                intent.putExtra("group_name", groupName)
                startActivity(intent)
            }

            this.groupChatBtn = findViewById(R.id.card_group_chat)
            groupChatBtn.setOnClickListener {
                val intent = Intent(this, GroupChatActivity::class.java)
                intent.putExtra("extra_group_id", groupId)
                intent.putExtra("extra_group_name", groupName)
                startActivity(intent)
            }

            this.creatActivityBtn = findViewById(R.id.add_activity_button)
            creatActivityBtn.setOnClickListener {
                val intent = Intent(this, CreateActivityActivity::class.java)
                intent.putExtra("group_id", groupId)
                startActivity(intent)
            }

            this.leaveGroupBtn = findViewById(R.id.leave_group_button)
            leaveGroupBtn.setOnClickListener {

                groupViewModel.leaveGroup(groupId)
            }

            groupViewModel.leaveGroupResultData.observe(this) {
                if(!it){
                    Toast.makeText(this, "Failed to leave group", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Group left successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        // Add the fragment to the container with the groupID
        supportFragmentManager.beginTransaction()
            .replace(R.id.activities_recycler_container, activitiesRVFragement)
            .commit()
    }
}
