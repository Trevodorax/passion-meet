package com.example.passionmeet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.passionmeet.repositories.SignupRepository
import com.example.passionmeet.viewmodel.SignupViewModel
import com.example.passionmeet.viewmodel.factories.SignupViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button

    private val signupViewModel: SignupViewModel by viewModels {
        SignupViewModelFactory(SignupRepository(this), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_signup)
        this.emailEditText = findViewById(R.id.email_field)
        this.usernameEditText = findViewById(R.id.username_field)
        this.passwordEditText = findViewById(R.id.password_field)
        this.submitButton = findViewById(R.id.submit_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        signupViewModel.signupResult.observe(this@SignupActivity, Observer {
            val result = it ?: return@Observer

            if (result.isSignupSuccess) {
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                // close and destroy activity
                finish()
            } else {
                Toast.makeText(this, "Signup failed", Toast.LENGTH_LONG).show()
            }
        })

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            signupViewModel.signup(email, password, username)
        }
    }
}
