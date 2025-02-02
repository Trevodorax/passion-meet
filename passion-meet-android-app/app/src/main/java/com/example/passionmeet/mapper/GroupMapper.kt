package com.example.passionmeet.mapper

import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.PassionDto

fun mapGroupToGroupModel(dtos: List<GroupResponseDTO>): List<GroupModel> {
    return dtos.map {
        GroupModel(
            it.id,
            it.name,
            it.description,
            0,
            it.imageUrl
        )
    }
}


