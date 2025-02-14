package com.example.passionmeet.data.remote.dto

import com.example.passionmeet.data.remote.dto.UserDto
import com.example.passionmeet.data.remote.dto.GroupDto
import com.google.gson.annotations.SerializedName
import java.util.Date

data class MessageDto(
    val id: String,
    val content: String,
    @SerializedName("sendedAt")
    val sentAt: Date,
    @SerializedName("createdBy")
    val sender: UserDto,
    val group: GroupDto
) 