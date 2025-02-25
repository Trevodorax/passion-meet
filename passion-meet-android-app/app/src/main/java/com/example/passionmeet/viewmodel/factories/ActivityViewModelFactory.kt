package com.example.passionmeet.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.SignupActivity
import com.example.passionmeet.repositories.ActivityRepository
import com.example.passionmeet.repositories.SignupRepository
import com.example.passionmeet.ui.activities.CreateActivityActivity
import com.example.passionmeet.viewmodel.ActivityViewModel
import com.example.passionmeet.viewmodel.SignupViewModel

class ActivityViewModelFactory (
        private val repository: ActivityRepository,
        private val activity: CreateActivityActivity
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
                return ActivityViewModel(
                    repository,
                    activity
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }