package com.example.passionmeet.ui.chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.data.local.entity.MessageEntity
import com.example.passionmeet.repositories.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        messageRepository.getMessages(groupId)
    }

    fun sendMessage(content: String) {
        if (groupId == -1L) return
        
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        val userName = sharedPreferences.getString("username", "") ?: ""

        messageRepository.sendMessage(groupId, content, userId, userName)
    }

    fun getCurrentUserId(): String {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getString("user_id", "") ?: ""
    }
} 