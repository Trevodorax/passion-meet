package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.remote.dto.CreateActivityRequestDTO
import com.example.passionmeet.data.remote.dto.GroupRequestDto
import com.example.passionmeet.data.remote.dto.UserRequestDto
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.models.SignupModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.CreatedActivityResponseDTO
import com.example.passionmeet.network.services.ActivityService
import com.example.passionmeet.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date

class ActivityRepository(
    private val context: Context,
) {

    private val _createActivityResponse = MutableLiveData<Boolean>()
    val createActivityResponse: MutableLiveData<Boolean> get() = _createActivityResponse
    private val activityService=  RetrofitClient.instance.create(ActivityService::class.java)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

//    private val _messages = MutableLiveData<List<MessageEntity>>()
//    val messages get() = _messages

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

//    fun getMessages(groupId: String): LiveData<List<MessageEntity>> {
//        coroutineScope.launch {
//            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
//                // No network available, load from local database only
//                val localMessages = messageDao.getMessagesByGroup(groupId)
//                withContext(Dispatchers.Main) {
//                    _messages.value = localMessages
//                }
//                return@launch
//            }
//
//            try {
//                val call = messageService.getMessages(
//                    groupId,
//                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
//                )
//
//                call.enqueue(object : retrofit2.Callback<List<MessageDto>> {
//                    override fun onResponse(
//                        call: retrofit2.Call<List<MessageDto>>,
//                        response: retrofit2.Response<List<MessageDto>>
//                    ) {
//                        val body = response.body()
//                        Log.e("MessageRepository", "Messages received: $body")
//                        body?.let {
//                            val messageEntities = it.map { dto -> dto.toEntity(groupId) }
//                            _messages.value = messageEntities
//
//                            // Save to local database
//                            coroutineScope.launch {
//                                messageDao.deleteMessagesByGroup(groupId) // Clear old messages for this group
//                                messageDao.insertMessages(messageEntities)
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: retrofit2.Call<List<MessageDto>>, t: Throwable) {
//                        Log.e("Error getMessages()", "Error: ${t.message}")
//                        // Load from local database on failure
//                        coroutineScope.launch {
//                            val localMessages = messageDao.getMessagesByGroup(groupId)
//                            withContext(Dispatchers.Main) {
//                                _messages.value = localMessages
//                            }
//                        }
//                    }
//                })
//            } catch (e: Exception) {
//                Log.e("MessageRepository", "Error fetching messages", e)
//                // Load from local database on error
//                val localMessages = messageDao.getMessagesByGroup(groupId)
//                withContext(Dispatchers.Main) {
//                    _messages.value = localMessages
//                }
//            }
//        }
//        return messages
//    }

    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {
        //Data preparation
        val type= "unknown"
        val imageUrl = "unknown"
        val endDate = Calendar.getInstance()
        endDate.time = Date(startDate)
        endDate.add(Calendar.HOUR, 1)

        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // Save message locally when offline
//                val userName = sharedPreferences.getString("user_name", "") ?: "You"
//                val localMessage = MessageEntity(
//                    groupId = groupId,
//                    senderId = userId,
//                    senderName = userName,
//                    content = content,
//                    timestamp = Date(),
//                    isNew = true
//                )
//                messageDao.insertMessage(localMessage)
//                return@launch
            }

            try {
                val request = CreateActivityRequestDTO(
                 user = UserRequestDto(
                     id = userId
                 ),
                group = GroupRequestDto(
                    id = groupId
                ),
                startDate = Date(startDate),
                endDate = endDate.time,
                name = name,
                description = description,
                maxParticipants = maxParticipants,
                location = location,
                imageUrl = imageUrl,
                type = type
                )

                val call = activityService.createActivity(
                    request,
                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
                )

                call.enqueue(object : retrofit2.Callback<CreatedActivityResponseDTO> {
                    override fun onResponse(
                        call: retrofit2.Call<CreatedActivityResponseDTO>,
                        response: retrofit2.Response<CreatedActivityResponseDTO>
                    ) {

                        if(response.isSuccessful && response.body() != null){
                            val body = response.body()
                            Log.e("ActivityRepository", "Activity created: $body")
                            _createActivityResponse.value = true
                            }


                       else {
                            Log.e("ActivityRepository", "Error response: ${response.errorBody()?.string()}")
                            _createActivityResponse.value = false
                            //val messageEntity = it.toEntity(groupId)
                            // Save to local database
                            // coroutineScope.launch {
                            //    messageDao.insertMessage(messageEntity)
                            // }
                            // Refresh messages
                            //getMessages(groupId)
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<CreatedActivityResponseDTO>, t: Throwable) {
                        Log.e("Error createActivity()", "Error: ${t.message}")
                        _createActivityResponse.value = false
                    // Save message locally on failure
//                        coroutineScope.launch {
//                            val localMessage = MessageEntity(
//                                groupId = groupId,
//                                senderId = userId,
//                                senderName = "You",
//                                content = content,
//                                timestamp = Date(),
//                                isNew = true
//                            )
//                            messageDao.insertMessage(localMessage)
//                        }
                    }
                })
            } catch (e: Exception) {
                _createActivityResponse.value = false
                Log.e("ActivityRepository", "Error creating activity", e)
                // Save message locally on error
//                val localMessage = MessageEntity(
//                    groupId = groupId,
//                    senderId = userId,
//                    senderName = "You",
//                    content = content,
//                    timestamp = Date(),
//                    isNew = true
//                )
//                messageDao.insertMessage(localMessage)
            }
        }
    }

//    private fun MessageDto.toEntity(groupId: String): MessageEntity {
//        return MessageEntity(
//            id = 0, // Room will auto-generate
//            groupId = groupId,
//            senderId = this.createdBy?.id ?: "",
//            senderName =  this.createdBy?.username ?: "",
//            content = this.content,
//            timestamp = this.sendedAt,
//            isNew = true
//        )
//    }
} 