package com.example.passionmeet.network.services

import com.example.passionmeet.data.remote.dto.CreateActivityRequestDTO
import com.example.passionmeet.network.dto.CreatedActivityResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ActivityService {

    // Fonction représentant le contrat d'interface avec l'API
    @POST("activities")
    fun createActivity(
        // Paramètres de la fonction
        @Body request: CreateActivityRequestDTO,
        @Header("Authorization") token: String
    ): Call<CreatedActivityResponseDTO>
}