package com.example.passionmeet.models

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoginModel(
    val token: String?
)