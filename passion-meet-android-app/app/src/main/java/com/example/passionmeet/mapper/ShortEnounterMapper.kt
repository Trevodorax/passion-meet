package com.example.passionmeet.mapper

import android.util.Log
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.ShortenedEncounter

fun mapEncounterModelTOShortenedEncounterModel(encounterModel: EncounterModel): ShortenedEncounter {
    Log.d("shortemapper", encounterModel.userMet.groups.toString())
    return ShortenedEncounter(
        userEncounteredName1 = encounterModel.userMet.username,
        status = if (encounterModel.isSeen) EncounterStatus.SEEN else EncounterStatus.UNSEEN,
        happenedAt = formatDate(encounterModel.createdAt),
        profilePic = "https://robohash.org/${encounterModel.userMet.id}",
        groups = encounterModel.userMet.groups
    )
}