package com.example.passionmeet.data.local.dao

import androidx.room.*
import com.example.passionmeet.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY timestamp ASC")
    fun getMessagesByGroup(groupId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE groupId = :groupId")
    suspend fun deleteMessagesByGroup(groupId: String)

    @Query("UPDATE messages SET isNew = 0 WHERE groupId = :groupId")
    suspend fun markMessagesAsRead(groupId: String)

    @Query("SELECT COUNT(*) FROM messages WHERE groupId = :groupId AND isNew = 1")
    fun getUnreadMessageCount(groupId: String): Flow<Int>
} 