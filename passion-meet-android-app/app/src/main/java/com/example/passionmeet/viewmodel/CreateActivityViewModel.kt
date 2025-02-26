package com.example.passionmeet.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.repositories.ActivityRepository

class ActivityViewModel(
    val activityRepository: ActivityRepository,
    val context: LifecycleOwner
) : ViewModel() {

    val groupId = MutableLiveData<String>()
    private val _activityResult = MutableLiveData<Boolean>()
    val createActivityResult: MutableLiveData<Boolean> = _activityResult
    private val activities = MutableLiveData<List<ActivityModel>>()
    val activitiesData: LiveData<List<ActivityModel>> get() = activities

    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {

        this.activityRepository.createActivityResponse.observe(context) { data ->
            this@ActivityViewModel._activityResult.value = data
        }

        this.activityRepository.createActivity(groupId, userId, startDate, name, description, maxParticipants, location)
    }

    fun getActivities(groupId: String) {
        this.activityRepository.getActivities(groupId)
    }
}