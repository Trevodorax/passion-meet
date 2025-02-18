package com.example.passionmeet.ui.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.passionmeet.R
import com.example.passionmeet.databinding.ActivityLoginBinding
import com.example.passionmeet.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModel { parametersOf(this) }
    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val STAY_CONNECTED_KEY = "stay_connected"
        private const val TOKEN_EXPIRY_KEY = "token_expiry"
        private const val TOKEN_EXPIRY_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
    }

    /**
     * Shared preferences for storing the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        this.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val stayConnected = binding.stayConnectedCheckbox

        // Restore stay connected preference
        if (stayConnected != null) {
            stayConnected.isChecked = sharedPreferences.getBoolean(STAY_CONNECTED_KEY, false)
        }

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                username.error = getString(loginState.emailError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                System.err.println("Login Failed: ${loginResult.error}")
                showLoginFailed(loginResult.error)
                return@Observer
            }
            if (loginResult.success != null) {
                System.err.println("LoginActivity: loginResult.success: ${loginResult.success}")
                updateUiWithUser(loginResult.success)
                
                // Save stay connected preference and token expiry if checked
                if (stayConnected != null) {
                    if (stayConnected.isChecked) {
                        sharedPreferences.edit()
                            .putBoolean(STAY_CONNECTED_KEY, true)
                            .putLong(TOKEN_EXPIRY_KEY, System.currentTimeMillis() + TOKEN_EXPIRY_DURATION)
                            .apply()
                    } else {
                        // Clear stay connected preference if unchecked
                        sharedPreferences.edit()
                            .remove(STAY_CONNECTED_KEY)
                            .remove(TOKEN_EXPIRY_KEY)
                            .apply()
                    }
                }
            }
            System.err.println("LoginActivity: loginResult: $loginResult")
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // get the user's token from the preferences
        val token = sharedPreferences.getString(TOKEN_KEY, null)
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName $token",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    /**
     * Check if the stored token is valid and not expired
     */
    fun isTokenValid(): Boolean {
        val token = sharedPreferences.getString(TOKEN_KEY, null) ?: return false
        val expiryTime = sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0)
        val stayConnected = sharedPreferences.getBoolean(STAY_CONNECTED_KEY, false)

        return stayConnected && System.currentTimeMillis() < expiryTime
    }

    /**
     * Clear stored token and preferences
     */
    fun clearToken() {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(TOKEN_EXPIRY_KEY)
            .remove(STAY_CONNECTED_KEY)
            .apply()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
