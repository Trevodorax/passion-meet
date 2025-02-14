package com.example.passionmeet.network.services

import com.example.passionmeet.models.EncounterModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface EncounterService {
    @GET("users/me/relations")
    suspend fun getSelfEncounters(
        @Header("Authorization") token: String
    ): Response<List<EncounterModel>>

    @PATCH("users/me/relations/{encounterId}/seen")
    suspend fun markAsSeen(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String
    ): Response<Unit>
} 