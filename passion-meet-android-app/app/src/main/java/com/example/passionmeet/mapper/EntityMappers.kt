package com.example.passionmeet.mapper

import com.example.passionmeet.data.local.entity.GroupEntity
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.PassionDto

fun mapPassionDtoToPassionEntity(dto: PassionDto): PassionEntity {
    return PassionEntity(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        picture = dto.picture,
        type = dto.type
    )
}

fun mapPassionEntityToPassionCategoryModel(entities: List<PassionEntity>): List<PassionCategoryModel> {
    val categoryMap = entities.groupBy { it.type }
    return categoryMap.map { (category, passions) ->
        PassionCategoryModel(
            name = category,
            items = passions.map { 
                PassionSelectorModel(
                    id = it.id,
                    name = it.name,
                    image = it.picture,
                    isSelected = it.isSelfPassion
                )
            }
        )
    }
}

fun mapGroupDtoToGroupEntity(dto: GroupResponseDTO): GroupEntity {
    return GroupEntity(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        imageUrl = dto.imageUrl
    )
}

fun mapGroupEntityToGroupModel(entities: List<GroupEntity>): List<GroupModel> {
    return entities.map { 
        GroupModel(
            id = it.id,
            name = it.name,
            description = it.description,
            image = it.imageUrl,
            members = it.memberCount
        )
    }
} 