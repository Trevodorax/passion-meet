package com.example.passionmeet.mapper

import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

fun mapEncounterModelTOShortenedEncounterModel(encounterModel: EncounterModel): ShortenedEncounter {
    return ShortenedEncounter(
        userEncounteredName1 = encounterModel.userMet.username,
        status = if (encounterModel.isSeen) EncounterStatus.SEEN else EncounterStatus.UNSEEN,
        happenedAt = encounterModel.createdAt,
        profilePic = "https://example.com/profiles/${encounterModel.userMet.id}/profile.jpg"
    )
}