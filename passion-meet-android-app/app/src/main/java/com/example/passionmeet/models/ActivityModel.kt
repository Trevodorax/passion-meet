package com.example.passionmeet.models

data class ActivityModel(
        val id: String,
        val name: String,
        val createdBy: String,
        val maxParticipants: Int,
        val location: String,
        val startDate: String,
        val description: String,
        val participants: List<String> = emptyList(),
    )
