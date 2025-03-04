package com.example.passionmeet.mapper

import android.util.Log
import com.example.passionmeet.data.local.entity.GroupEntity
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.data.local.entity.EncounterEntity
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.models.PassionSelectorModel
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.UserMetModel
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.models.ShortenedEncounter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

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

fun EncounterModel.toEntity(): EncounterEntity {
    //add fields groups
    val groupsJson = Gson().toJson(this.userMet.groups)
    return EncounterEntity(
        id = id,
        createdAt = createdAt,
        isSeen = isSeen,
        userMetId = userMet.id,
        userMetEmail = userMet.email,
        userMetUsername = userMet.username,
        userMetGroups = groupsJson
    )
}

fun EncounterEntity.toModel(): EncounterModel {
    val groups: List<GroupModel> = Gson().fromJson(this.userMetGroups, object : TypeToken<List<GroupModel>>() {}.type)

    return EncounterModel(
        id = id,
        createdAt = createdAt,
        isSeen = isSeen,
        userMet = UserMetModel(
            id = userMetId,
            email = userMetEmail,
            username = userMetUsername,
            groups = groups
        )
    )
}

fun EncounterModel.toShortenedEncounter(): ShortenedEncounter {
    return ShortenedEncounter(
        userEncounteredName1 = userMet.username,
        status = if (isSeen) EncounterStatus.SEEN else EncounterStatus.UNSEEN,
        happenedAt = formatDate(createdAt),
        profilePic = "https://robohash.org/${userMet.id}/",
        groups = userMet.groups
    )
}

fun formatDate(dateString: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return dateString
        
        val now = Calendar.getInstance()
        val encounterDate = Calendar.getInstance().apply { time = date }
        
        return when {
            isSameDay(now, encounterDate) -> "Today"
            isYesterday(encounterDate) -> "Yesterday"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        return dateString
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(date: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isSameDay(yesterday, date)
} 