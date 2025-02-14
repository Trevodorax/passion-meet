package com.example.passionmeet.repositories

import android.content.Context
import com.example.passionmeet.data.local.dao.MessageDao
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.data.remote.api.MessageApi
import com.example.passionmeet.data.remote.dto.MessageDto
import com.example.passionmeet.util.NetworkResult
import com.example.passionmeet.util.SharedPreferencesUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date

@Singleton
class MessageRepository @Inject constructor(
    private val context: Context,
    private val messageDao: MessageDao,
    private val messageApi: MessageApi
) {
    private val sharedPreferencesUtil = SharedPreferencesUtil(context)

    fun getMessagesByGroup(groupId: Long): Flow<List<MessageEntity>> {
        return messageDao.getMessagesByGroup(groupId)
    }

    suspend fun fetchMessagesFromApi(groupId: Long): NetworkResult<List<MessageDto>> {
        return try {
            val token = "Bearer ${sharedPreferencesUtil.getToken()}"
            val response = messageApi.getMessages(groupId, token)
            
            if (response.isSuccessful) {
                val messages = response.body()
                if (messages != null) {
                    // Convert and save to local database
                    val entities = messages.map { it.toEntity(groupId) }
                    messageDao.insertMessages(entities)
                    NetworkResult.Success(messages)
                } else {
                    NetworkResult.Error("Empty response body")
                }
            } else {
                NetworkResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun sendMessage(groupId: Long, content: String, userId: String, userName: String): NetworkResult<MessageDto> {
        return try {
            val token = "Bearer ${sharedPreferencesUtil.getToken()}"
            val message = mapOf(
                "content" to content,
                "sendedAt" to Date(),
                "createdBy" to mapOf(
                    "id" to userId,
                    "username" to userName
                ),
                "group" to mapOf(
                    "id" to groupId
                )
            )
            
            val response = messageApi.createMessage(groupId, message, token)
            
            if (response.isSuccessful) {
                val createdMessage = response.body()
                if (createdMessage != null) {
                    // Save to local database
                    messageDao.insertMessage(createdMessage.toEntity(groupId))
                    NetworkResult.Success(createdMessage)
                } else {
                    NetworkResult.Error("Empty response body")
                }
            } else {
                NetworkResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }

    suspend fun insertMessages(messages: List<MessageEntity>) {
        messageDao.insertMessages(messages)
    }

    suspend fun deleteMessagesByGroup(groupId: Long) {
        messageDao.deleteMessagesByGroup(groupId)
    }

    suspend fun markMessagesAsRead(groupId: Long) {
        messageDao.markMessagesAsRead(groupId)
    }

    fun getUnreadMessageCount(groupId: Long): Flow<Int> {
        return messageDao.getUnreadMessageCount(groupId)
    }

    private fun MessageDto.toEntity(groupId: Long): MessageEntity {
        return MessageEntity(
            id = 0, // Room will auto-generate
            groupId = groupId,
            senderId = this.sender.id,
            senderName = this.sender.username,
            content = this.content,
            timestamp = this.sentAt,
            isNew = true
        )
    }
} 