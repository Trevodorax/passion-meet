package com.example.passionmeet.models

import com.google.gson.annotations.SerializedName

data class EncounterModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("isSeen")
    val isSeen: Boolean,
    
    @SerializedName("userMet")
    val userMet: UserMetModel
)

data class UserMetModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String,

    @SerializedName("participatedGroups")
    val groups : List<GroupModel>
) 