package com.example.passionmeet.viewmodel.factories;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.GroupPageActivity
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
class GroupPageViewModelFactory(
    private val repository: GroupRepository,
    private val activity: GroupPageActivity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(repository, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
