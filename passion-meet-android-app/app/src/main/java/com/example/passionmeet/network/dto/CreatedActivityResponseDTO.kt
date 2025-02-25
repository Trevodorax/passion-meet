package com.example.passionmeet.network.dto

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
//    val createdBy: User;
//    group: Group;
//    participants: User[];
)