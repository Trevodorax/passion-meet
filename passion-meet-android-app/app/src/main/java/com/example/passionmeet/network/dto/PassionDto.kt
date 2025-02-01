package com.example.passionmeet.network.dto

import com.google.gson.annotations.SerializedName

data class PassionDto(
    @SerializedName("id")
    val id: String,
    val name: String,
    val description: String,
    val picture: String,
    val type: String
)
