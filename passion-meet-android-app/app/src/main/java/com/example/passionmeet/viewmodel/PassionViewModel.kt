package com.example.passionmeet.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.repositories.PassionRepository

class PassionViewModel (
    val passionRepository: PassionRepository,
    val context: LifecycleOwner
): ViewModel() {

    // Observables pour notifier la view
    private val _passionData = MutableLiveData<List<PassionCategoryModel>>()
    val passionData: LiveData<List<PassionCategoryModel>> get() = _passionData

    fun fetchAllData() {
        this.passionRepository.passionData.observe(context) { data ->
            this@PassionViewModel._passionData.value = data
        }

        this.passionRepository.getAllPassions()
    }
}