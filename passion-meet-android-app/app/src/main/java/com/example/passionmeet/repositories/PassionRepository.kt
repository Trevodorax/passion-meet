package com.example.passionmeet.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.mapper.mapPassionCategoryDtoToPassionCategoryModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.network.services.PassionService

class PassionRepository {

    private val passionService = RetrofitClient.instance.create(PassionService::class.java)

    private val _passionData = MutableLiveData<List<PassionCategoryModel>>()
    val passionData get() = _passionData

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

}
