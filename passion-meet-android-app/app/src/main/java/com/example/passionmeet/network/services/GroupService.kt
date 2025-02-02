package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.GroupResponseDTO
import retrofit2.Call
import retrofit2.http.GET

interface GroupService {

    @GET("users/me/groups")
    fun getSelfGroups(): Call<List<GroupResponseDTO>>
}