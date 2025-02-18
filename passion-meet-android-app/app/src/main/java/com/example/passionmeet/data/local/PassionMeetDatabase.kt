package com.example.passionmeet.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.passionmeet.data.local.dao.PassionDao
import com.example.passionmeet.data.local.dao.GroupDao
import com.example.passionmeet.data.local.dao.MessageDao
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.data.local.entity.GroupEntity
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.data.local.entity.EncounterEntity
import com.example.passionmeet.data.local.dao.EncounterDao
import com.example.passionmeet.util.Converters

@Database(
    entities = [
        PassionEntity::class,
        GroupEntity::class,
        EncounterEntity::class,
        MessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PassionMeetDatabase : RoomDatabase() {
    abstract fun passionDao(): PassionDao
    abstract fun groupDao(): GroupDao
    abstract fun encounterDao(): EncounterDao
    abstract fun messageDao(): MessageDao

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