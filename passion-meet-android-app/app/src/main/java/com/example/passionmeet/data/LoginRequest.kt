package com.example.passionmeet.data

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoginRequest(
    val email: String,
    val password: String
)
