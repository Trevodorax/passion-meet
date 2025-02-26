package com.example.passionmeet.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.repositories.ActivityRepository

class ActivityViewModel(
    val activityRepository: ActivityRepository,
    val context: LifecycleOwner
) : ViewModel() {

    private val _activityResult = MutableLiveData<Boolean>()
    val createActivityResult: MutableLiveData<Boolean> = _activityResult

    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {

        this.activityRepository.createActivityResponse.observe(context) { data ->
            this@ActivityViewModel._activityResult.value = data
        }

        this.activityRepository.createActivity(groupId, userId, startDate, name, description, maxParticipants, location)
    }
}