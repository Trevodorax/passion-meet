package com.example.passionmeet.network.services

import com.example.passionmeet.data.remote.dto.CreateGroupDto
import com.example.passionmeet.data.remote.dto.GroupDto
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.JoinGroupRequestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface GroupService {

    @GET("users/me/groups")
    fun getSelfGroups(@Header("Authorization") authHeader: String): Call<List<GroupResponseDTO>>

    @POST("/users/me/groups")
    fun joinGroup(
        @Header("Authorization") authHeader: String,
        @Body group: JoinGroupRequestDTO
    ): Call<Void>

    @DELETE("/users/me/groups")
    fun leaveGroup(
        @Header("Content-Type:") contentType: String ,
        @Header("Authorization") authHeader: String,
        @Body group: JoinGroupRequestDTO
    ): Call<Void>

    @POST("groups")
    fun createGroup(
        @Header("Authorization") authHeader: String,
        @Body group: CreateGroupDto
    ): Call<GroupResponseDTO>
}