package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.GroupResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GroupService {

    @GET("users/me/groups")
    fun getSelfGroups(@Header("Authorization") authHeader: String): Call<List<GroupResponseDTO>>
}