package com.example.passionmeet.data.remote.dto

import com.example.passionmeet.network.dto.PassionDto

data class GroupDto(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val participants: List<UserDto>
)

data class CreateGroupDto(
    val name: String,
    val description: String,
    val imageUrl: String,
    val createdBy: UserRequestDto,
    val passion: PassionRequestDto
)

data class PassionRequestDto(
    val id: String
)

