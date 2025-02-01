package com.example.passionmeet.mapper

import com.example.passionmeet.models.SignupModel
import com.example.passionmeet.network.dto.SignupResponseDTO

fun mapSignupDtoToSignupModel(
    dto: SignupResponseDTO,
    email: String,
    username: String
): SignupModel {
    return SignupModel(
        isSignupSuccess = assertSignupValidity(dto, email, username)
    )
}

fun assertSignupValidity(dto: SignupResponseDTO, email: String, username: String): Boolean {
    return dto.email == email && dto.username == username && dto.id.isNotEmpty()
}
