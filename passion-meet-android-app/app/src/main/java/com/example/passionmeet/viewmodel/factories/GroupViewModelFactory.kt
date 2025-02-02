package com.example.passionmeet.viewmodel.factories;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.MainActivity
import com.example.passionmeet.SelectPassionActivity
import com.example.passionmeet.UserHomeActivity
import com.example.passionmeet.repositories.GroupRepository
import com.example.passionmeet.viewmodel.GroupViewModel

class GroupViewModelFactory(
    private val repository: GroupRepository,
    private val activity: UserHomeActivity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(repository, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
