package com.example.passionmeet.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.SignupModel
import com.example.passionmeet.repositories.SignupRepository

/**
 * ViewModel for the login screen.
 */
class SignupViewModel(
    val signupRepository: SignupRepository,
    val context: LifecycleOwner
) : ViewModel() {

    private val _signupResult = MutableLiveData<SignupModel>()
    val signupResult: LiveData<SignupModel> = _signupResult

    /**
     */
    fun signup(email: String, password: String, username: String) {
        this.signupRepository.signupResponse.observe(context) { data ->

            if (data.isSignupSuccess) {
                Log.e("SignupViewModel", "Signup successful")
                this@SignupViewModel._signupResult.value = data
            } else {
                Log.e("SignupViewModel", "Signup failed")
                this@SignupViewModel._signupResult.value = SignupModel(isSignupSuccess = false)
            }
        }

        this.signupRepository.signup(email, password, username)

    }
}