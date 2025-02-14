package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.passionmeet.data.local.dao.EncounterDao
import com.example.passionmeet.mapper.toEntity
import com.example.passionmeet.mapper.toModel
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.network.services.EncounterService
import com.example.passionmeet.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EncounterRepository(
    private val context: Context,
    private val encounterService: EncounterService,
    private val encounterDao: EncounterDao
) {
    private val TAG = "EncounterRepository"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getEncounters(): LiveData<List<EncounterModel>> {
        Log.d(TAG, "Getting encounters from database")
        refreshEncounters()
        return encounterDao.getAllEncounters().map { entities ->
            Log.d(TAG, "Mapping ${entities.size} entities to models")
            entities.map { it.toModel() }
        }
    }

    fun getUnseenCount(): LiveData<Int> {
        Log.d(TAG, "Getting unseen count")
        return encounterDao.getUnseenCount()
    }

    fun refreshEncounters() {
        Log.d(TAG, "Refreshing encounters")
        coroutineScope.launch {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val token = sharedPreferences.getString("auth_token", "") ?: ""
                    if (token.isEmpty()) {
                        Log.e(TAG, "No auth token found")
                        _error.postValue("No authentication token found")
                        return@launch
                    }

                    Log.d(TAG, "Making network request for encounters")
                    val response = encounterService.getSelfEncounters("Bearer $token")
                    Log.d(TAG, "Response received: ${response.code()}")
                    
                    if (response.isSuccessful) {
                        response.body()?.let { encounters ->
                            Log.d(TAG, "Received ${encounters.size} encounters")
                            encounterDao.deleteAllEncounters()
                            encounterDao.insertEncounters(
                                encounters.map { it.toEntity() }
                            )
                            Log.d(TAG, "Encounters saved to database")
                        } ?: run {
                            Log.e(TAG, "Empty response body")
                            _error.postValue("Empty response from server")
                        }
                    } else {
                        Log.e(TAG, "Error response: ${response.code()} - ${response.message()}")
                        _error.postValue("Error: ${response.code()} - ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "No network connection")
                    _error.postValue("No network connection available")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing encounters", e)
                _error.postValue("Error: ${e.message}")
            }
        }
    }

    fun markAsSeen(encounterId: String) {
        Log.d(TAG, "Marking encounter as seen: $encounterId")
        coroutineScope.launch {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val token = sharedPreferences.getString("auth_token", "") ?: ""
                    if (token.isEmpty()) {
                        Log.e(TAG, "No auth token found")
                        _error.postValue("No authentication token found")
                        return@launch
                    }

                    // Update local database immediately for better UX
                    Log.d(TAG, "Updating local database")
                    encounterDao.updateSeenStatus(encounterId, true)

                    // Make API call to update server
                    Log.d(TAG, "Making network request to mark as seen")
                    val response = encounterService.markAsSeen("Bearer $token", encounterId)
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Error response: ${response.code()} - ${response.message()}")
                        _error.postValue("Error: ${response.code()} - ${response.message()}")
                        // Revert local change if API call fails
                        encounterDao.updateSeenStatus(encounterId, false)
                    }
                } else {
                    Log.e(TAG, "No network connection")
                    _error.postValue("No network connection available")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking encounter as seen", e)
                _error.postValue("Error marking encounter as seen: ${e.message}")
                // Revert local change if operation fails
                encounterDao.updateSeenStatus(encounterId, false)
            }
        }
    }
}