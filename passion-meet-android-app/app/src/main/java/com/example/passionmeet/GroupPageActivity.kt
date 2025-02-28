package com.example.passionmeet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.passionmeet.ui.activities.ActivityFragment
import com.example.passionmeet.ui.activities.CreateActivityActivity

class GroupPageActivity : AppCompatActivity() {

    //TODO: implement data from intent so far it's just the layout data
    private lateinit var groupChatBtn: CardView
    private lateinit var creatActivityBtn: Button
    private lateinit var fullActivitiesMap: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        enableEdgeToEdge()

        val activitiesRVFragement = ActivityFragment()
        val bundleForFragment = Bundle()

        val bundle = intent.extras
        if (bundle != null) {
            val groupId = bundle.getString("group_id")
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
        }

        // Add the fragment to the container with the groupID
        supportFragmentManager.beginTransaction()
            .replace(R.id.activities_recycler_container, activitiesRVFragement)
            .commit()
    }
}
