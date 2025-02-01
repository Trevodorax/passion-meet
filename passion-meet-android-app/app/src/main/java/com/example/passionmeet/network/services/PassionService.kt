package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.PassionDto
import retrofit2.Call
import retrofit2.http.GET

interface PassionService {

    @GET("passions")
    fun getAllPassions(): Call<List<PassionDto>>
}