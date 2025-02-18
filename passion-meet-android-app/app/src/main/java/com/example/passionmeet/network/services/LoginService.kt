package com.example.passionmeet.network.services

import com.example.passionmeet.network.dto.LoginResponseDTO
import com.example.passionmeet.network.dto.UserResponseDTO
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {

    // Fonction représentant le contrat d'interface avec l'API
    @FormUrlEncoded
    @POST("users/login") // FONCTION + PATH
    fun login(
        // Paramètres de la fonction
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponseDTO> // Type de retour de la fonction

    @GET("users/me")
    fun getSelfInfo(
        @Header("Authorization") token: String
    ): Call<UserResponseDTO>
}