package com.example.passionmeet.network.dto

import com.example.passionmeet.data.remote.dto.UserDto

data class JoinGroupRequestDTO (
    var groupId: String
)

data class CreateGroupDTO (
    var name: String,
    var description: String,
    var imageUrl: String,
    var createdBy: UserDto,
    var passion: PassionDto
)