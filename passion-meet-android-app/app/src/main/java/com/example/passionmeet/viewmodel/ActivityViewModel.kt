package com.example.passionmeet.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.passionmeet.repositories.ActivityRepository

class ActivityViewModel(
    val activityRepository: ActivityRepository,
    val context: LifecycleOwner
) : ViewModel() {
    fun createActivity(groupId: String, userId: String, startDate: String, name: String, description: String, maxParticipants: Int, location: String) {
        //ADD FORM ERROR HANDLING AND RESPONSE?

        this.activityRepository.createActivity(groupId, userId, startDate, name, description, maxParticipants, location)
    }
}