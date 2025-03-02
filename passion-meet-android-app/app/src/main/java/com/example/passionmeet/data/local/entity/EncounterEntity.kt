package com.example.passionmeet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "encounters")
data class EncounterEntity(
    @PrimaryKey
    val id: String,
    val createdAt: String,
    val isSeen: Boolean,
    val userMetId: String,
    val userMetEmail: String,
    val userMetUsername: String,
    @ColumnInfo(name = "userMetGroups")
    val userMetGroups: String
) 