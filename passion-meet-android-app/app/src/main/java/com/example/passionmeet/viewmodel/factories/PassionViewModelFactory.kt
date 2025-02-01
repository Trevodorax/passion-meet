package com.example.passionmeet.viewmodel.factories;

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passionmeet.SelectPassionActivity
import com.example.passionmeet.repositories.PassionRepository
import com.example.passionmeet.viewmodel.PassionViewModel

class PassionViewModelFactory (
        private val repository: PassionRepository,
        private val activity: SelectPassionActivity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PassionViewModel::class.java)) {
            return PassionViewModel(repository, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
