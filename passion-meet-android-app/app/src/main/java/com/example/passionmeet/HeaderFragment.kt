package com.example.passionmeet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.passionmeet.MainActivity.Companion.STAY_CONNECTED_KEY
import com.example.passionmeet.MainActivity.Companion.TOKEN_EXPIRY_KEY
import com.example.passionmeet.MainActivity.Companion.TOKEN_KEY

class HeaderFragment: Fragment() {
    private lateinit var logoutButton: ImageButton
    private lateinit var userHomeButton: ImageButton
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
        this.userHomeButton = view.findViewById(R.id.userAvatar)
        logoutButton.setOnClickListener {
            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            logout()
        }
        userHomeButton.setOnClickListener {
            val intent = Intent( requireActivity(), UserHomeActivity::class.java)
            startActivity(intent)
        }
    }


    private fun logout() {
        // Clear all authentication related data
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(STAY_CONNECTED_KEY)
            .remove(TOKEN_EXPIRY_KEY)
            .apply()

        val intent = Intent( requireActivity(), MainActivity::class.java)
        startActivity(intent)

        requireActivity().finish()
    }

}