package com.example.passionmeet.network.services

import com.example.passionmeet.data.remote.dto.CreateActivityRequestDTO
import com.example.passionmeet.network.dto.CreatedActivityResponseDTO
import com.example.passionmeet.network.dto.ListActivityDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ActivityService {
    // Fonctions représentant le contrat d'interface avec l'API

    @POST("activities")
    fun createActivity(
        // Paramètres de la fonction
        @Body request: CreateActivityRequestDTO,
        @Header("Authorization") token: String
    ): Call<CreatedActivityResponseDTO>


    @GET("/groups/{groupId}/activities")
    fun getActivities(
        @Path("groupId") groupId: String,
        @Header("Authorization") token: String
    ): Call<ListActivityDTO>

}