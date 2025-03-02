package com.example.passionmeet.data.remote.dto

import java.io.Serializable

data class UserDto (
    val id: String,
    val username: String,
    val email: String,
//    val imageUrl: String
) : Serializable