package com.example.passionmeet.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.repositories.LoginRepository
import com.example.passionmeet.ui.login.LoginActivity
import com.example.passionmeet.viewmodel.LoginViewModel

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(
    private val repository: LoginRepository,
    private val activity: LoginActivity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                repository,
                activity
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
