package com.example.passionmeet.utils

import androidx.room.TypeConverter
import com.example.passionmeet.data.local.entity.GroupEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromGroupList(groups: List<GroupEntity>?): String? {
        if (groups == null) return null
        return Gson().toJson(groups) // Convert List<GroupEntity> to a JSON string
    }

    @TypeConverter
    fun toGroupList(groupsString: String?): List<GroupEntity>? {
        if (groupsString == null) return null
        val type = object : TypeToken<List<GroupEntity>>() {}.type
        return Gson().fromJson(groupsString, type) // Convert JSON string back to List<GroupEntity>
    }
} 