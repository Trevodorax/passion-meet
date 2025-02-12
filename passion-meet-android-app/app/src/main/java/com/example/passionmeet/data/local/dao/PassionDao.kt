package com.example.passionmeet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.passionmeet.data.local.entity.PassionEntity

@Dao
interface PassionDao {
    @Query("SELECT * FROM passions")
    suspend fun getAllPassions(): List<PassionEntity>

    @Query("SELECT * FROM passions WHERE isSelfPassion = 1")
    suspend fun getSelfPassions(): List<PassionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(passions: List<PassionEntity>)

    @Query("DELETE FROM passions")
    suspend fun deleteAll()

    @Query("DELETE FROM passions WHERE isSelfPassion = 1")
    suspend fun deleteAllSelfPassions()
} 