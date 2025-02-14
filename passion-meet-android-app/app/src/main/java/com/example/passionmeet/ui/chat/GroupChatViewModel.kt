package com.example.passionmeet.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.repositories.MessageRepository
import com.example.passionmeet.util.NetworkResult
import com.example.passionmeet.util.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val sharedPreferencesUtil = SharedPreferencesUtil(context)
    private var groupId: Long = -1
    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setGroupId(id: Long) {
        groupId = id
        viewModelScope.launch {
            // Start observing local database
            messageRepository.getMessagesByGroup(groupId).collectLatest {
                _messages.value = it
            }
        }
        // Fetch latest messages from API
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            when (val result = messageRepository.fetchMessagesFromApi(groupId)) {
                is NetworkResult.Success -> {
                    // Messages are automatically saved to local DB and will be reflected in the UI
                    _error.value = null
                }
                is NetworkResult.Error -> {
                    _error.value = result.message
                }
            }
        }
    }

    fun sendMessage(content: String) {
        if (groupId == -1L) return

        viewModelScope.launch {
            val userId = getCurrentUserId()
            val userName = getCurrentUserName()

            when (val result = messageRepository.sendMessage(groupId, content, userId, userName)) {
                is NetworkResult.Success -> {
                    _error.value = null
                }
                is NetworkResult.Error -> {
                    _error.value = result.message
                    // Optionally save message locally with a pending status
                    val localMessage = MessageEntity(
                        groupId = groupId,
                        senderId = userId,
                        senderName = userName,
                        content = content,
                        timestamp = java.util.Date(),
                        isNew = true
                    )
                    messageRepository.insertMessage(localMessage)
                }
            }
        }
    }

    fun getCurrentUserId(): String {
        return sharedPreferencesUtil.getUserId() ?: "unknown"
    }

    private fun getCurrentUserName(): String {
        return sharedPreferencesUtil.getUsername() ?: "Unknown User"
    }
} 