package com.example.passionmeet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passions")
data class PassionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val picture: String,
    val type: String,
    val isSelfPassion: Boolean = false
) 