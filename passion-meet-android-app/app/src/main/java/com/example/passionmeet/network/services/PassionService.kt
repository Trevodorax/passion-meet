package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.AddPassionDto
import com.example.passionmeet.network.dto.PassionDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface PassionService {

    @GET("passions")
    fun getAllPassions(): Call<List<PassionDto>>

    @GET("users/me/passions")
    fun getSelfPassions(@Header("Authorization") authHeader: String): Call<List<PassionDto>>
    
    @PUT("users/me/passions/multiple")
    fun updateMultiplePassions(
        @Header("Authorization") authHeader: String,
        @Body passions: List<AddPassionDto>
    ): Call<Void>
}