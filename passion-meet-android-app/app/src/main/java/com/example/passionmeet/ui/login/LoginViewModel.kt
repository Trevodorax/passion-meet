package com.example.passionmeet.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.passionmeet.data.LoginRepository
import com.example.passionmeet.data.Result

import com.example.passionmeet.R
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    /**
     * Calls the login function in the repository, which will use Cronet in the data source
     * Updates loginResult LiveData based on the Result type
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Call the login function in the repository, which will use Cronet in the data source
            val result = loginRepository.login(username, password)

            // Update loginResult LiveData based on the Result type
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.token))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    /**
     * Called when the user changes the username or password
     * Updates the loginFormState LiveData based on the validity of the username and password
     */
    fun loginDataChanged(username: String, password: String) {
        if (!isEmailNameValid(username)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * A placeholder username validation check
     */
    private fun isEmailNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    /**
     * A placeholder password validation check
     */
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}