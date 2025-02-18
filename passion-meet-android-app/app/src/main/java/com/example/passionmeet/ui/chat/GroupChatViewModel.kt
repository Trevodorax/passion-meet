package com.example.passionmeet.ui.chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.repositories.MessageRepository

class GroupChatViewModel (
    private val context: Context,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private var groupId: String = ""
    private val _messages = MediatorLiveData<List<MessageEntity>>()
    val messages: LiveData<List<MessageEntity>> get() = _messages

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun setGroupId(id: String) {
        groupId = id
        _messages.addSource(messageRepository.getMessages(groupId)) { messages ->
            _messages.value = messages
        }
    }

    fun sendMessage(content: String) {
        if (groupId.isEmpty()) return

        messageRepository.sendMessage(groupId, content, getCurrentUserId())
    }

    fun getCurrentUserId(): String {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getString("user_id", "") ?: throw IllegalStateException("User ID not found")
    }
} 