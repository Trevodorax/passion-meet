package com.example.passionmeet.data.remote.api

import com.example.passionmeet.data.remote.dto.MessageDto
import retrofit2.Response
import retrofit2.http.*

interface MessageApi {
    @GET("groups/{groupId}/messages")
    suspend fun getMessages(
        @Path("groupId") groupId: Long,
        @Header("Authorization") token: String
    ): Response<List<MessageDto>>

    @POST("groups/{groupId}/messages")
    suspend fun createMessage(
        @Path("groupId") groupId: Long,
        @Body message: Map<String, Any>,
        @Header("Authorization") token: String
    ): Response<MessageDto>
} 