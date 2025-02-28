package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.mapper.mapSignupDtoToSignupModel
import com.example.passionmeet.models.SignupModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.SignupResponseDTO
import com.example.passionmeet.network.services.SignupService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepository(
    context: Context,
) {

    private val signupService = RetrofitClient.instance.create(SignupService::class.java)

    private val _signupResponse = MutableLiveData<SignupModel>()
    val signupResponse: MutableLiveData<SignupModel> get() = _signupResponse

    val scope = CoroutineScope(SupervisorJob())

    /**
     * Shared preferences for storing the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun signup(email: String, password: String, username: String) {
        Log.d("SignupRepository", "Attempting signup with username: $email")

        val call = signupService.signup(email, password, username)

        call.enqueue(object: Callback<SignupResponseDTO> {
            override fun onResponse(
                call: Call<SignupResponseDTO>,
                response: Response<SignupResponseDTO>
            ) {
                Log.d("SignupRepository", "Response received: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    Log.d("SignupRepository", "Signup successful: $body")

                    _signupResponse.value = body?.let {
                        val mappedData = mapSignupDtoToSignupModel(it, email, username)
                        scope.launch {
                            response.body()?.let {
                                saveUserInfo(
                                    username = body.username,
                                    email = body.email,
                                    id = body.id

                                )
                            }
                        }

                        mappedData
                    }
                } else {
                    Log.e("SignupRepository", "Signup failed: ${response.code()} - ${response.message()}")
                    try {
                        Log.e("SignupRepository", "Error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("SignupRepository", "Failed to read error body", e)
                    }
                    _signupResponse.value = SignupModel(false)
                }
            }

            override fun onFailure(call: Call<SignupResponseDTO>, t: Throwable) {
                Log.e("LoginRepository", "Login request failed", t)
            }
        })
    }


    /**
     * Saves the auth token to shared preferences
     */
    private fun saveUserInfo(username: String, email: String, id: String) {
        sharedPreferences.edit().putString("username", username).apply()
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("userId", id).apply()
    }
}