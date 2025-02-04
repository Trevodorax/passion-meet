package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.mapper.mapPassionCategoryDtoToPassionCategoryModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.network.services.PassionService

class PassionRepository(context: Context) {

    private val passionService = RetrofitClient.instance.create(PassionService::class.java)

    private val _passionData = MutableLiveData<List<PassionCategoryModel>>()
    val passionData get() = _passionData

    // self data
    private val _selfPassionData = MutableLiveData<List<PassionCategoryModel>>()
    val selfPassionData get() = _selfPassionData

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getAllPassions() {
        val call = this.passionService.getAllPassions()

        call.enqueue(object: retrofit2.Callback<List<PassionDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<PassionDto>>,
                response: retrofit2.Response<List<PassionDto>>
            ) {
                val body = response.body()

                this@PassionRepository._passionData.value = body?.let {
                    mapPassionCategoryDtoToPassionCategoryModel(
                        it
                        //TODO: Implementer une logique de sauvegarde des données avant de renvoyer la donnée à la vue
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PassionDto>>, t: Throwable) {
                Log.e("Error getAllPassions()", "Error: ${t.message}")
            }

        })
    }

    fun getSelfPassions() {
        val call = this.passionService.getSelfPassions(
            "Bearer ${
                sharedPreferences.getString(
                    "auth_token",
                    ""
                )
            }"
        )

        call.enqueue(object : retrofit2.Callback<List<PassionDto>> {
            override fun onResponse(
                call: retrofit2.Call<List<PassionDto>>,
                response: retrofit2.Response<List<PassionDto>>
            ) {
                val body = response.body()

                this@PassionRepository._selfPassionData.value = body?.let {
                    mapPassionCategoryDtoToPassionCategoryModel(
                        it
                    )
                    //TODO: Implementer une logique de sauvegarde des données avant de renvoyer la donnée à la vue
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PassionDto>>, t: Throwable) {
                Log.e("Error getSelfPassions()", "Error: ${t.message}")
            }
        })
    }

}
