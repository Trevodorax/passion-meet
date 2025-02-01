package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.LoginResponseDTO
import com.example.passionmeet.network.dto.SignupResponseDTO
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignupService {

    // Fonction représentant le contrat d'interface avec l'API
    @FormUrlEncoded
    @POST("users") // FONCTION + PATH
    fun signup(
        // Paramètres de la fonction
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("username") username: String
    ): Call<SignupResponseDTO> // Type de retour de la fonction
}