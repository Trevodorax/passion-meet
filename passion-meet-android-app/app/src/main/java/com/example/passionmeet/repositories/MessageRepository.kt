package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.local.dao.MessageDao
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.data.remote.dto.MessageDto
import com.example.passionmeet.data.remote.dto.CreateMessageRequest
import com.example.passionmeet.data.remote.dto.UserDto
import com.example.passionmeet.data.remote.dto.GroupDto
import com.example.passionmeet.data.remote.dto.GroupRequestDto
import com.example.passionmeet.data.remote.dto.UserRequestDto
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

    fun getMessages(groupId: String): LiveData<List<MessageEntity>> {
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
                        Log.e("MessageRepository", "Messages received: $body")
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
        return messages
    }

    fun sendMessage(groupId: String, content: String, userId: String) {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // Save message locally when offline
                val userName = sharedPreferences.getString("user_name", "") ?: "You"
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
                val request = CreateMessageRequest(
                    content = content,
                    sentAt = Date(),
                    sender = UserRequestDto(
                        id = userId,
                    ),
                    group = GroupRequestDto(
                        id = groupId
                    )
                )

                Log.e("MessageRepository", "Sending message: $request")

                val call = messageService.createMessage(
                    groupId,
                    request,
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
                                senderName = "You",
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
                    senderName = "You",
                    content = content,
                    timestamp = Date(),
                    isNew = true
                )
                messageDao.insertMessage(localMessage)
            }
        }
    }

    private fun MessageDto.toEntity(groupId: String): MessageEntity {
        return MessageEntity(
            id = 0, // Room will auto-generate
            groupId = groupId,
            senderId = this.createdBy?.id ?: "",
            senderName =  this.createdBy?.username ?: "",
            content = this.content,
            timestamp = this.sendedAt,
            isNew = true
        )
    }
} 