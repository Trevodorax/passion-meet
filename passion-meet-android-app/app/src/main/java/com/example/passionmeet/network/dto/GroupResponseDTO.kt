package com.example.passionmeet.network.dto

import com.example.passionmeet.data.remote.dto.UserDto

data class GroupResponseDTO (

    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val participants: List<UserDto>
)
