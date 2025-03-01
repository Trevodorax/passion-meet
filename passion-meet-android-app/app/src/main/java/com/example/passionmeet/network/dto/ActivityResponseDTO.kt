package com.example.passionmeet.network.dto

import com.example.passionmeet.data.remote.dto.UserDto

data class ActivityResponseDTO (
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val maxParticipants: Int,
    val imageUrl: String,
    val createdBy: UserDto,
    val participants: List<UserDto>
)

data class ListActivityDTO (
    val activities: List<ActivityResponseDTO>
)