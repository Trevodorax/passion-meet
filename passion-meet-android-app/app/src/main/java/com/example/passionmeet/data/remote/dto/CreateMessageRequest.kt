package com.example.passionmeet.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateMessageRequest(
    val content: String,
    @SerializedName("sendedAt")
    val sentAt: Date,
    @SerializedName("createdBy")
    val sender: UserRequestDto,
    val group: GroupRequestDto
)

data class UserRequestDto(
    val id: String,
)

data class GroupRequestDto(
    val id: String
) 