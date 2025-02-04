package com.example.passionmeet.mapper

import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.network.dto.PassionDto

fun mapPassionCategoryDtoToPassionCategoryModel(dtos: List<PassionDto>): List<PassionCategoryModel> {
    // convert passion model dto to passion category model
    // a passion category model contains a list of passion selector model

    // map of String(category/type) to List<PassionDto>
    val passionCategoryMap = mutableMapOf<String, MutableList<PassionDto>>()
    dtos.forEach { dto ->
        if (passionCategoryMap.containsKey(dto.type)) {
            passionCategoryMap[dto.type]?.add(dto)
        } else {
            passionCategoryMap[dto.type] = mutableListOf(dto)
        }
    }

    // map of String(category/type) to List<PassionSelectorModel>
    val passionCategoryModelMap = mutableMapOf<String, MutableList<PassionSelectorModel>>()
    passionCategoryMap.forEach { (category, dtos) ->
        val passionSelectorModels = dtos.map { dto ->
            PassionSelectorModel(
                id = dto.id,
                name = dto.name,
                image = dto.picture
            )
        }
        passionCategoryModelMap[category] = passionSelectorModels.toMutableList()
    }

    return passionCategoryModelMap.map { (category, passionSelectorModels) ->
        PassionCategoryModel(
            name = category,
            items = passionSelectorModels
        )
    }
}


