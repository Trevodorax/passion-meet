package com.example.passionmeet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class GroupPageActivity : AppCompatActivity() {

    //TODO: implement data from intent so far it's just the layout data
    private lateinit var groupChatBtn: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        enableEdgeToEdge()
        val bundle = intent.extras
        if (bundle != null) {
            val groupId = bundle.getString("group_id")
            val groupName = bundle.getString("group_name")

            this.groupChatBtn = findViewById(R.id.card_group_chat)
            groupChatBtn.setOnClickListener {
                val intent = Intent(this, GroupChatActivity::class.java)

                intent.putExtra("extra_group_id", groupId)
                intent.putExtra("extra_group_name", groupName)

                startActivity(intent)
            }
        }

        }
}
