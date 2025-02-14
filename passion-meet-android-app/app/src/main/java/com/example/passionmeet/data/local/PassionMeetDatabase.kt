package com.example.passionmeet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.passionmeet.data.local.dao.PassionDao
import com.example.passionmeet.data.local.dao.GroupDao
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.data.local.entity.GroupEntity
import com.example.passionmeet.data.local.entity.EncounterEntity
import com.example.passionmeet.data.local.dao.EncounterDao

@Database(
    entities = [PassionEntity::class, GroupEntity::class, EncounterEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PassionMeetDatabase : RoomDatabase() {
    abstract fun passionDao(): PassionDao
    abstract fun groupDao(): GroupDao
    abstract fun encounterDao(): EncounterDao

    companion object {
        @Volatile
        private var INSTANCE: PassionMeetDatabase? = null

        fun getDatabase(context: Context): PassionMeetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PassionMeetDatabase::class.java,
                    "passion_meet_db"
                )
                .fallbackToDestructiveMigration() // This will recreate tables if no Migration found
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}