package com.example.passionmeet.models

import com.example.passionmeet.data.remote.dto.UserDto
import java.io.Serializable

data class ActivityModel(
        val id: String,
        val name: String,
        val createdBy: String = "A user",
        val maxParticipants: Int,
        val location: String,
        val startDate: String,
        val description: String,
        val participants: List<UserDto> = emptyList(),
    ): Serializable
