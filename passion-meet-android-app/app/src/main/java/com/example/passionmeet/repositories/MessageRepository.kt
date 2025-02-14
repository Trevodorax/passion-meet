package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.local.dao.MessageDao
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.data.remote.dto.MessageDto
import com.example.passionmeet.network.services.MessageService
import com.example.passionmeet.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MessageRepository(
    private val context: Context,
    private val messageService: MessageService,
    private val messageDao: MessageDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _messages = MutableLiveData<List<MessageEntity>>()
    val messages get() = _messages

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getMessages(groupId: Long) {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // No network available, load from local database only
                val localMessages = messageDao.getMessagesByGroup(groupId)
                withContext(Dispatchers.Main) {
                    _messages.value = localMessages
                }
                return@launch
            }

            try {
                val call = messageService.getMessages(
                    groupId,
                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
                )

                call.enqueue(object : retrofit2.Callback<List<MessageDto>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<MessageDto>>,
                        response: retrofit2.Response<List<MessageDto>>
                    ) {
                        val body = response.body()
                        body?.let {
                            val messageEntities = it.map { dto -> dto.toEntity(groupId) }
                            _messages.value = messageEntities
                            
                            // Save to local database
                            coroutineScope.launch {
                                messageDao.deleteMessagesByGroup(groupId) // Clear old messages for this group
                                messageDao.insertMessages(messageEntities)
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<List<MessageDto>>, t: Throwable) {
                        Log.e("Error getMessages()", "Error: ${t.message}")
                        // Load from local database on failure
                        coroutineScope.launch {
                            val localMessages = messageDao.getMessagesByGroup(groupId)
                            withContext(Dispatchers.Main) {
                                _messages.value = localMessages
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("MessageRepository", "Error fetching messages", e)
                // Load from local database on error
                val localMessages = messageDao.getMessagesByGroup(groupId)
                withContext(Dispatchers.Main) {
                    _messages.value = localMessages
                }
            }
        }
    }

    fun sendMessage(groupId: Long, content: String, userId: String, userName: String) {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // Save message locally when offline
                val localMessage = MessageEntity(
                    groupId = groupId,
                    senderId = userId,
                    senderName = userName,
                    content = content,
                    timestamp = Date(),
                    isNew = true
                )
                messageDao.insertMessage(localMessage)
                return@launch
            }

            try {
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

                val call = messageService.createMessage(
                    groupId,
                    message,
                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
                )

                call.enqueue(object : retrofit2.Callback<MessageDto> {
                    override fun onResponse(
                        call: retrofit2.Call<MessageDto>,
                        response: retrofit2.Response<MessageDto>
                    ) {
                        val body = response.body()
                        body?.let {
                            val messageEntity = it.toEntity(groupId)
                            // Save to local database
                            coroutineScope.launch {
                                messageDao.insertMessage(messageEntity)
                            }
                            // Refresh messages
                            getMessages(groupId)
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<MessageDto>, t: Throwable) {
                        Log.e("Error sendMessage()", "Error: ${t.message}")
                        // Save message locally on failure
                        coroutineScope.launch {
                            val localMessage = MessageEntity(
                                groupId = groupId,
                                senderId = userId,
                                senderName = userName,
                                content = content,
                                timestamp = Date(),
                                isNew = true
                            )
                            messageDao.insertMessage(localMessage)
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("MessageRepository", "Error sending message", e)
                // Save message locally on error
                val localMessage = MessageEntity(
                    groupId = groupId,
                    senderId = userId,
                    senderName = userName,
                    content = content,
                    timestamp = Date(),
                    isNew = true
                )
                messageDao.insertMessage(localMessage)
            }
        }
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