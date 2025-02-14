package com.example.passionmeet.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.passionmeet.data.local.entity.EncounterEntity

@Dao
interface EncounterDao {
    @Query("SELECT * FROM encounters ORDER BY createdAt DESC")
    fun getAllEncounters(): LiveData<List<EncounterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncounters(encounters: List<EncounterEntity>)

    @Query("DELETE FROM encounters")
    suspend fun deleteAllEncounters()

    @Query("UPDATE encounters SET isSeen = :seen WHERE id = :encounterId")
    suspend fun updateSeenStatus(encounterId: String, seen: Boolean)

    @Query("SELECT COUNT(*) FROM encounters WHERE isSeen = 0")
    fun getUnseenCount(): LiveData<Int>
} 