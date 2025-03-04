package com.example.passionmeet.network.dto

import com.example.passionmeet.data.remote.dto.UserRequestDto

data class CreatedActivityResponseDTO (
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val maxParticipants: Int,
    val imageUrl: String,
    val createdBy: UserRequestDto,
)