package com.example.passionmeet.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.ActivityModel
import com.example.passionmeet.repositories.ActivityRepository

enum class JoinStatus {
    JOIN,
    LEAVE
}
class ActivityViewModel(
    val activityRepository: ActivityRepository,
    val context: LifecycleOwner
) : ViewModel() {


    private val _activityResult = MutableLiveData<Boolean>()
    val createActivityResult: MutableLiveData<Boolean> = _activityResult

    private val activities = MutableLiveData<List<ActivityModel>>()
    val activitiesData: LiveData<List<ActivityModel>> get() = activities

    private val _joinActivityResult = MutableLiveData<Boolean>()
    val joinActivityResult: LiveData<Boolean> get() = _joinActivityResult

    private var _isInitialized = false

    fun initialize(groupId: String) {
        if (_isInitialized) return
        _isInitialized = true

        this.activityRepository.activities.observe(context) { data ->
            Log.d("ActivityViewModel", "Received $data activities")
            this.activities.value = data
        }

        this.activityRepository.getActivities(groupId)
    }


    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {

        this.activityRepository.createActivityResponse.observe(context) { data ->
            this@ActivityViewModel._activityResult.value = data
        }

        this.activityRepository.createActivity(groupId, userId, startDate, name, description, maxParticipants, location)
    }

    fun joinActivity(activityId: String, status: JoinStatus) {
        this.activityRepository.joinActivityResponse.observe(context) { data ->
            this@ActivityViewModel._joinActivityResult.value = data
        }
        if (status == JoinStatus.JOIN) this.activityRepository.joinActivity(activityId)
        else this.activityRepository.leaveActivity(activityId)
    }

}
