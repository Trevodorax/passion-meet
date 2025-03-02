package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.local.dao.GroupDao
import com.example.passionmeet.data.remote.dto.CreateGroupDto
import com.example.passionmeet.data.remote.dto.PassionRequestDto
import com.example.passionmeet.data.remote.dto.UserRequestDto
import com.example.passionmeet.mapper.mapGroupDtoToGroupEntity
import com.example.passionmeet.mapper.mapGroupEntityToGroupModel
import com.example.passionmeet.mapper.mapGroupToGroupModel
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.JoinGroupRequestDTO
import com.example.passionmeet.network.services.GroupService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.passionmeet.utils.NetworkUtils

class GroupRepository(
    private val context: Context,
    private val groupService: GroupService,
    private val groupDao: GroupDao? = null
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _groupData = MutableLiveData<List<GroupModel>>()
    val groupData get() = _groupData

    private val joinGroupResult = MutableLiveData<Boolean>()
    val joinGroupResultData get() = joinGroupResult

    private val leaveGroupResult = MutableLiveData<Boolean>()
    val leaveGroupResultData get() = leaveGroupResult

    private val _createGroupData = MutableLiveData<GroupModel>()
    val createGroupData get() = _createGroupData

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getSelfGroups() {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // No network available, load from local database only
                val localGroups = groupDao?.getAllGroups() ?: emptyList()
                withContext(Dispatchers.Main) {
                    _groupData.value = mapGroupEntityToGroupModel(localGroups)
                }
                return@launch
            }

            try {
                val call = groupService.getSelfGroups(
                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
                )

                call.enqueue(object : retrofit2.Callback<List<GroupResponseDTO>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<GroupResponseDTO>>,
                        response: retrofit2.Response<List<GroupResponseDTO>>
                    ) {
                        if (!response.isSuccessful || response.body() == null) {
                            Log.e("GroupRepository", "Error: ${response.code()} - ${response.message()}")
                            // Load from local database on error response
                            coroutineScope.launch {
                                val localGroups = groupDao?.getAllGroups()
                                withContext(Dispatchers.Main) {
                                    _groupData.value = localGroups?.let {
                                        mapGroupEntityToGroupModel(
                                            it
                                        )
                                    }
                                }
                            }
                            return
                        }

                        val body = response.body()
                        body?.let {
                            val groupModels = mapGroupToGroupModel(it)
                            _groupData.value = groupModels

                            // Save to local database
                            coroutineScope.launch {
                                val entities = it.map { dto -> mapGroupDtoToGroupEntity(dto) }
                                groupDao?.deleteAll() // Clear old data
                                groupDao?.insertAll(entities)
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<List<GroupResponseDTO>>, t: Throwable) {
                        Log.e("GroupRepository", "Error fetching groups: ${t.message}")
                        // Load from local database on failure
                        coroutineScope.launch {
                            val localGroups = groupDao?.getAllGroups()
                            withContext(Dispatchers.Main) {
                                _groupData.value = localGroups?.let { mapGroupEntityToGroupModel(it) }
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("GroupRepository", "Error fetching groups", e)
                // Load from local database on error
                val localGroups = groupDao?.getAllGroups()
                withContext(Dispatchers.Main) {
                    _groupData.value = localGroups?.let { mapGroupEntityToGroupModel(it) }
                }
            }
        }
    }

    fun joinGroup(groupId: String){
        Log.d("GroupRepository", "Joining group with id: $groupId")
        coroutineScope.launch {
            try {
                val request = JoinGroupRequestDTO(
                    groupId = groupId
                )
                val call = groupService.joinGroup(
                    "Bearer ${sharedPreferences.getString("auth_token", "")}",
                    request
                )

                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(
                        call: retrofit2.Call<Void>,
                        response: retrofit2.Response<Void>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e("GroupRepository", "Error: ${response.code()} - ${response.message()}")
                            joinGroupResult.postValue(false)
                            return
                        }
                        joinGroupResult.postValue(true)
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Log.e("GroupRepository", "Error joining group: ${t.message}")
                        joinGroupResult.postValue(false)
                    }
                })
            } catch (e: Exception) {
                Log.e("GroupRepository", "Error joining group", e)
                joinGroupResult.postValue(false)
            }
        }
    }

    fun leaveGroup(groupId: String){
        Log.d("GroupRepository", "Leaving group with id: $groupId")
        coroutineScope.launch {
            try {
                val request = JoinGroupRequestDTO(
                    groupId = groupId
                )
                val call = groupService.leaveGroup(
                    "application/json",
                    "Bearer ${sharedPreferences.getString("auth_token", "")}",
                    request
                )

                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(
                        call: retrofit2.Call<Void>,
                        response: retrofit2.Response<Void>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e("GroupRepository", "Error: ${response.code()} - ${response.message()}")
                            leaveGroupResult.postValue(false)
                            return
                        }
                        leaveGroupResult.postValue(true)
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Log.e("GroupRepository", "Error leaving group: ${t.message}")
                        leaveGroupResult.postValue(false)
                    }
                })
            } catch (e: Exception) {
                Log.e("GroupRepository", "Error leaving group", e)
                leaveGroupResult.postValue(false)
            }
        }
    }

    fun createGroup(name: String, image: String, description: String, userId: String, passionId: String) {
        Log.d("GroupRepository", "Creating group with name: $name")
        coroutineScope.launch {
            val request = CreateGroupDto(
                name = name,
                imageUrl = image,
                description = description,
                createdBy = UserRequestDto(userId),
                passion = PassionRequestDto(passionId)
            )
            try {
                val call = groupService.createGroup(
                    "Bearer ${sharedPreferences.getString("auth_token", "")}",
                    request
                )

                call.enqueue(object : retrofit2.Callback<GroupResponseDTO> {
                    override fun onResponse(
                        call: retrofit2.Call<GroupResponseDTO>,
                        response: retrofit2.Response<GroupResponseDTO>
                    ) {
                        if (!response.isSuccessful) {
                            Log.e(
                                "GroupRepository",
                                "Error: ${response.code()} - ${response.message()}"
                            )
                            return
                        }
                        val body = response.body()
                        if (body != null) {
                            _createGroupData.postValue(
                                GroupModel(
                                    id =  body.id,
                                    name = body.name,
                                    description = body.description,
                                    image = body.imageUrl,
                                    members = body.participants.size
                                )
                            )
                        }

                    }

                    override fun onFailure(call: retrofit2.Call<GroupResponseDTO>, t: Throwable) {
                        Log.e("GroupRepository", "Error creating group: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                Log.e("GroupRepository", "Error creating group", e)
            }
        }
    }
}
