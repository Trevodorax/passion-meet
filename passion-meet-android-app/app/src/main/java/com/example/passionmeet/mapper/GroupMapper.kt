package com.example.passionmeet.mapper

import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.network.dto.GroupResponseDTO

fun mapGroupToGroupModel(dtos: List<GroupResponseDTO>): List<GroupModel> {
    return dtos.map {
        GroupModel(
            it.id,
            it.name,
            it.imageUrl,
            0,
            it.description
        )
    }
}


