package com.example.passionmeet.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.repositories.EncounterRepository

class EncounterViewModel(
    private val encounterRepository: EncounterRepository,
    private val context: Context
) : ViewModel() {

    private val _encounterData = MutableLiveData<List<EncounterModel>>()
    val encounterData: LiveData<List<EncounterModel>> get() = _encounterData

    private val _unseenCount = MutableLiveData<Int>()
    val unseenCount: LiveData<Int> get() = _unseenCount

    val error: LiveData<String> = encounterRepository.error

    private var _isInitialized = false

    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true
        refreshEncounters()
    }

    fun refreshEncounters() {
        Log.d("EncounterViewModel", "Refreshing encounters")
        encounterRepository.getEncounters().observeForever { encounters ->
            Log.d("EncounterViewModel", "Received ${encounters.size} encounters")
            _encounterData.postValue(encounters)
        }
        encounterRepository.getUnseenCount().observeForever { count ->
            Log.d("EncounterViewModel", "Received unseen count: $count")
            _unseenCount.postValue(count)
        }
    }

    fun markAsSeen(encounterId: String) {
        encounterRepository.markAsSeen(encounterId)
    }

    override fun onCleared() {
        super.onCleared()
        encounterRepository.getEncounters().removeObserver { }
        encounterRepository.getUnseenCount().removeObserver { }
    }
} 