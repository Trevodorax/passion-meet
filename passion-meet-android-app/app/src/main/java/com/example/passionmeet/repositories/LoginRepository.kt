package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.model.LoginModel
import com.example.passionmeet.mapper.mapLoginDtoToLoginModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.LoginResponseDTO
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

    fun login(username: String, password: String) {

        val call = loginService.login(username, password)

        call.enqueue(object: Callback<LoginResponseDTO> {
            override fun onResponse(
                call: Call<LoginResponseDTO>,
                response: Response<LoginResponseDTO>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    _loginResponse.value = body?.let {
                        val mappedData = mapLoginDtoToLoginModel(it)
                        scope.launch {
                            saveToken(mappedData.token)
                        }
                        mappedData
                    }
                } else {
                    Log.e("LoginRepository", "Login failed: ${response.code()} - ${response.message()}")
                    // error state
                    _loginResponse.value = LoginModel("")
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                Log.e("LoginRepository", "Error: ${t.message}")
            }
        })

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