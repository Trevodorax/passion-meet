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

    fun fetchAllData() {
        this.groupRepository.groupData.observe(context) { data ->
            this@GroupViewModel._groupData.value = data
        }

        this.groupRepository.getSelfGroups()
    }
}