package com.example.passionmeet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.passionmeet.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var createAccountButton: Button
    private lateinit var signInButton: Button

    companion object {
        public const val TOKEN_KEY = "auth_token"
        public const val STAY_CONNECTED_KEY = "stay_connected"
        public const val TOKEN_EXPIRY_KEY = "token_expiry"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        this.createAccountButton = findViewById(R.id.create_account_button)
        this.signInButton = findViewById(R.id.sign_in_account_button)

        // Check if user is already logged in with valid token
        if (isTokenValid()) {
            // Navigate to UserHomeActivity directly
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
            return
        }

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

        signInButton.setOnClickListener {
            // Switch to the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            updateAuthenticationUI()
        }
        // Update UI based on authentication state
        updateAuthenticationUI()
    }

    private fun updateAuthenticationUI() {
        val isAuthenticated = isTokenValid()

        // Show/hide buttons based on authentication state
        createAccountButton.visibility = if (isAuthenticated) android.view.View.GONE else android.view.View.VISIBLE
        signInButton.visibility = if (isAuthenticated) android.view.View.GONE else android.view.View.VISIBLE
    }

    /**
     * Check if the stored token is valid and not expired
     */
    private fun isTokenValid(): Boolean {
        val token = sharedPreferences.getString(TOKEN_KEY, null) ?: return false
        val expiryTime = sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0)
        val stayConnected = sharedPreferences.getBoolean(STAY_CONNECTED_KEY, false)

        return if (stayConnected && System.currentTimeMillis() < expiryTime) {
            true
        } else {
            // Clear invalid token and preferences
            sharedPreferences.edit()
                .remove(TOKEN_KEY)
                .remove(TOKEN_EXPIRY_KEY)
                .remove(STAY_CONNECTED_KEY)
                .apply()
            false
        }
    }
}