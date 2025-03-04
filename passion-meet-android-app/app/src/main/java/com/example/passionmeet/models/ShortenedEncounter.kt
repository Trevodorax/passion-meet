package com.example.passionmeet.models

enum class EncounterStatus {
    SEEN,
    UNSEEN
}
data class ShortenedEncounter(
    val userEncounteredName1: String,
    val status: EncounterStatus,
    val profilePic: String,
    val happenedAt: String,
    val groups: List<GroupModel>
)
