package com.example.passionmeet.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.SignupActivity
import com.example.passionmeet.repositories.SignupRepository
import com.example.passionmeet.viewmodel.SignupViewModel

/**
 */
class SignupViewModelFactory(
    private val repository: SignupRepository,
    private val activity: SignupActivity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(
                repository,
                activity
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
