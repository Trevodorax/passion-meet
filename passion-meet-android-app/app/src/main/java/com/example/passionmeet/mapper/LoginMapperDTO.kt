package com.example.passionmeet.mapper

import com.example.passionmeet.models.LoginModel
import com.example.passionmeet.network.dto.LoginResponseDTO

fun mapLoginDtoToLoginModel(dto: LoginResponseDTO): LoginModel {
    return LoginModel(
        dto.token
    )
}