package com.example.passionmeet.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateActivityRequestDTO(
    val name: String,
    val type: String,
    val description: String,
    val maxParticipants: Int,
    val location: String,
    val imageUrl: String,
    val startDate: Date,
    val endDate: Date,
    @SerializedName("createdBy")
    val user: UserRequestDto,
    val group: GroupRequestDto
)