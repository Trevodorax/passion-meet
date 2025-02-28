package com.example.passionmeet.network.dto

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
//    val createdBy: UserRequestDto,
//    val participants: List<UserRequestDto>
)

data class ListActivityDTO (
    val activities: List<ActivityResponseDTO>
)