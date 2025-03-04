package com.example.passionmeet.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.repositories.GroupRepository

class GroupViewModel(
    val groupRepository: GroupRepository,
    val context: LifecycleOwner
) : ViewModel() {

    // Observables pour notifier la view
    private val _groupData = MutableLiveData<List<GroupModel>>()
    val groupData: LiveData<List<GroupModel>> get() = _groupData

    private val _joinGroupResultData = MutableLiveData<Boolean>()
    val joinGroupResultData: LiveData<Boolean> get() = _joinGroupResultData

    private val _leaveGroupResultData = MutableLiveData<Boolean>()
    val leaveGroupResultData: LiveData<Boolean> get() = _leaveGroupResultData

    private val _createGroupData = MutableLiveData<GroupModel>()
    val createGroupData: LiveData<GroupModel> get() = _createGroupData

    fun getSelfGroups() {
        this.groupRepository.groupData.observe(context) { data ->
            this@GroupViewModel._groupData.value = data
        }
        this.groupRepository.getSelfGroups()
    }

    fun joinGroup(groupId: String) {
        this.groupRepository.joinGroupResultData.observe(context) { result ->
            if(result) {
                this@GroupViewModel._joinGroupResultData.value = result
            }
        }
        this.groupRepository.joinGroup(groupId)
    }

    fun leaveGroup(groupId: String) {
        this.groupRepository.leaveGroupResultData.observe(context) { result ->
            if(result) {
                this@GroupViewModel._leaveGroupResultData.value = result
            }
        }
        this.groupRepository.leaveGroup(groupId)
    }

    fun createGroup(name: String, image: String, description: String, userId: String, passionId: String) {
        this.groupRepository.createGroupData.observe(context) { data ->
            this@GroupViewModel._createGroupData.value = data
        }
        this.groupRepository.createGroup(name, image, description, userId, passionId)
    }
}