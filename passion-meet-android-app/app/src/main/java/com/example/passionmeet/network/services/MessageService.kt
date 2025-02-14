package com.example.passionmeet.network.services

import com.example.passionmeet.data.remote.dto.MessageDto
import retrofit2.Call
import retrofit2.http.*

interface MessageService {
    @GET("groups/{groupId}/messages")
    fun getMessages(
        @Path("groupId") groupId: Long,
        @Header("Authorization") token: String
    ): Call<List<MessageDto>>

    @POST("groups/{groupId}/messages")
    fun createMessage(
        @Path("groupId") groupId: Long,
        @Body message: Map<String, Any>,
        @Header("Authorization") token: String
    ): Call<MessageDto>
} 