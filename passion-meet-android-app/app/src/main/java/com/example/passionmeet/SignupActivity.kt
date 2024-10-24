package com.example.passionmeet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.HttpURLConnection
import java.net.URL

class SignupActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button

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

        submitButton.setOnClickListener {
            Thread {
                try {
                    val url = URL("http://10.0.2.2:3000/user")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    // Create JSON body
                    val jsonBody = """
                        {
                            "email": "${emailEditText.text}",
                            "username": "${usernameEditText.text}",
                            "password": "${passwordEditText.text}"
                        }
                    """.trimIndent()

                    // Send request
                    connection.outputStream.use { os ->
                        os.write(jsonBody.toByteArray())
                    }

                    // Check response
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_CREATED) {
                        runOnUiThread {
                            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Failed: $responseCode", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }
}
