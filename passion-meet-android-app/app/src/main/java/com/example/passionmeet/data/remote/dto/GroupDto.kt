package com.example.passionmeet.data.remote.dto

data class GroupDto(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val participants: List<UserDto>
)