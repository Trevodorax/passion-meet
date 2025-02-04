package com.example.passionmeet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.passionmeet.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var createAccountButton: Button

    private lateinit var signInButton: Button

    private lateinit var selectPassionButton: Button

    private lateinit var passionSelector: RecyclerView

    private lateinit var openGroupList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        this.createAccountButton = findViewById(R.id.create_account_button)

        this.signInButton = findViewById(R.id.sign_in_account_button)

        this.selectPassionButton = findViewById(R.id.navigation_select_passion)

        this.openGroupList = findViewById(R.id.open_groups_list_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createAccountButton.setOnClickListener {
            // Switch to the SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        openGroupList.setOnClickListener {
            // Switch to the SignUpActivity
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            // Switch to the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectPassionButton.setOnClickListener {
            // switch to select passion activity
            val intent = Intent(this, SelectPassionActivity::class.java)
            startActivity(intent)
        }

        // Setup passion rv
//        this.passionSelector = findViewById(R.id.passion_selector_rv)
//        this.passionSelector.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        this.passionSelector.adapter = PassionSelectorAdapter(passionList)


//        // Setup categories rv
//        val categoryRecyclerView = findViewById<RecyclerView>(R.id.category_recycler_view)
//        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
//        categoryRecyclerView.adapter = CategoryAdapter(categories)
    }
}