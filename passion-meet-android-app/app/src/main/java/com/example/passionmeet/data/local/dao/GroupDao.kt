package com.example.passionmeet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.passionmeet.data.local.entity.GroupEntity

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<GroupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<GroupEntity>)

    @Query("DELETE FROM groups")
    suspend fun deleteAll()
} 