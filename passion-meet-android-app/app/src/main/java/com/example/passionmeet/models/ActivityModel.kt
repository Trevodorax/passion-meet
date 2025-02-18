package com.example.passionmeet.models

data class ActivityModel(
        val id: String,
        val name: String,
        val createdBy: String,
        val maxParticipant: Int,
        val location: String,
        val date: String,
        val description: String,
        val participants: List<String>,
    )
