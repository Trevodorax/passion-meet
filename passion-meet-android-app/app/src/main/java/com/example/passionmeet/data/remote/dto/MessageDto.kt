package com.example.passionmeet.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MessageDto(
    val id: String,
    val content: String,
    @SerializedName("sendedAt")
    val sendedAt: Date,
    @SerializedName("createdBy")
    val createdBy: UserDto? = null,
    val group: GroupDto? = null
)