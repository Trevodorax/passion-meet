package com.example.passionmeet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passionmeet.MainActivity.Companion.STAY_CONNECTED_KEY
import com.example.passionmeet.MainActivity.Companion.TOKEN_EXPIRY_KEY
import com.example.passionmeet.MainActivity.Companion.TOKEN_KEY
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.ui.login.LoginActivity

class HeaderFragment: Fragment() {
    private lateinit var logoutButton: ImageButton
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.header, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.logoutButton = view.findViewById(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            logout()
        }
    }


    private fun logout() {
        // Clear all authentication related data
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(STAY_CONNECTED_KEY)
            .remove(TOKEN_EXPIRY_KEY)
            .apply()

        val intent = Intent( requireActivity(), LoginActivity::class.java)
        startActivity(intent)

        requireActivity().finish()
    }

}