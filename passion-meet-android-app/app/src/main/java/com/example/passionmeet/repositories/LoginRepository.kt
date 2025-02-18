package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.mapper.mapLoginDtoToLoginModel
import com.example.passionmeet.models.LoginModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.LoginResponseDTO
import com.example.passionmeet.network.dto.UserResponseDTO
import com.example.passionmeet.network.services.LoginService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(
    context: Context,
) {

    private val loginService = RetrofitClient.instance.create(LoginService::class.java)

    private val _loginResponse = MutableLiveData<LoginModel>()
    val loginResponse: MutableLiveData<LoginModel> get() = _loginResponse

    val scope = CoroutineScope(SupervisorJob())

    /**
     * Shared preferences for storing the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun login(email: String, password: String) {
        Log.d("LoginRepository", "Attempting login with username: $email")

        val call = loginService.login(email, password)

        call.enqueue(object: Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                Log.d("LoginRepository", "Response received: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    Log.d("LoginRepository", "Login successful: $body")

                    _loginResponse.value = body?.let {
                        val mappedData = mapLoginDtoToLoginModel(it)
                        scope.launch {
                            mappedData.token?.let { token ->
                                Log.d("LoginRepository", "Saving token: $token")
                                saveToken(token)
                            }
                        }

                        getSelfInfo()

                        mappedData
                    }
                } else {
                    Log.e("LoginRepository", "Login failed: ${response.code()} - ${response.message()}")
                    try {
                        Log.e("LoginRepository", "Error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("LoginRepository", "Failed to read error body", e)
                    }
                    _loginResponse.value = LoginModel(null)
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                Log.e("LoginRepository", "Login request failed", t)
            }
        })
    }

    private fun getSelfInfo() {
        val call = loginService.getSelfInfo("Bearer ${sharedPreferences.getString("auth_token", "")}")

        call.enqueue(object: Callback<UserResponseDTO> {
            override fun onResponse(
                call: Call<UserResponseDTO>,
                response: Response<UserResponseDTO>
            ) {
                Log.d("LoginRepository", "Self info response received: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    Log.d("LoginRepository", "Self info received: $body")

                    saveUserInfo(body)
                } else {
                    Log.e("LoginRepository", "Self info request failed: ${response.code()} - ${response.message()}")
                    try {
                        Log.e("LoginRepository", "Self info error body: ${response.errorBody()?.string()}")
                    } catch (e: Exception) {
                        Log.e("LoginRepository", "Failed to read self info error body", e)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponseDTO>, t: Throwable) {
                Log.e("LoginRepository", "Self info request failed", t)
            }
        })
    }

    private fun saveUserInfo(body: UserResponseDTO?): UserResponseDTO {
        body?.let {
            val editor = sharedPreferences.edit()
            editor.putString("user_id", it.id)
            editor.putString("user_name", it.name)
            editor.putString("user_email", it.email)
            editor.apply()
        }

        return UserResponseDTO(
            id = sharedPreferences.getString("user_id", "") ?: throw IllegalStateException("User ID not found"),
            name = sharedPreferences.getString("user_name", "") ?: throw IllegalStateException("User name not found"),
            email = sharedPreferences.getString("user_email", "") ?: throw IllegalStateException("User email not found")
        )
    }


    /**
     * Saves the auth token to shared preferences
     */
    private fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("auth_token", token)
            .apply()
    }
}