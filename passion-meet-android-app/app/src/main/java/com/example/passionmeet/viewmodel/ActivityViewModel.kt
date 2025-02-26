package com.example.passionmeet.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.ActivityModel
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

    private var _isInitialized = false

    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        this.activityRepository.activities.observe(context) { data ->
            Log.d("ActivityViewModel", "Received $data activities")
            this.activities.value = data
        }
        //TODO: deal with group id later
        this.activityRepository.getActivities("49540ff4-db56-4cbe-9e01-2506714ede73")
    }


    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {

        this.activityRepository.createActivityResponse.observe(context) { data ->
            this@ActivityViewModel._activityResult.value = data
        }

        this.activityRepository.createActivity(groupId, userId, startDate, name, description, maxParticipants, location)
    }
}