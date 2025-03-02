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
    private val tag = "EncounterRepository"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getEncounters(): LiveData<List<EncounterModel>> {
        Log.d(tag, "Getting encounters from database")
        refreshEncounters()
        return encounterDao.getAllEncounters().map { entities ->
            Log.d(tag, "Mapping ${entities.size} entities to models")
            entities.map { it.toModel() }
        }
    }

    fun getUnseenCount(): LiveData<Int> {
        Log.d(tag, "Getting unseen count")
        return encounterDao.getUnseenCount()
    }

    fun refreshEncounters() {
        coroutineScope.launch {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val token = sharedPreferences.getString("auth_token", "") ?: ""
                    if (token.isEmpty()) {
                        Log.e(tag, "No auth token found")
                        _error.postValue("No authentication token found")
                        return@launch
                    }
                    val response = encounterService.getSelfEncounters("Bearer $token")

                    if (response.isSuccessful) {
                        response.body()?.let { encounters ->
                            Log.d(tag, "Received ${encounters.size} encounters : $encounters")
                            encounterDao.deleteAllEncounters()
                            encounterDao.insertEncounters(
                                encounters.map { it.toEntity() }
                            )
                            Log.d(tag, "Encounters saved to database")
                        } ?: run {
                            Log.e(tag, "Empty response body")
                            _error.postValue("Empty response from server")
                        }
                    } else {
                        Log.e(tag, "Error response: ${response.code()} - ${response.message()}")
                        _error.postValue("Error: ${response.code()} - ${response.message()}")
                    }
                } else {
                    Log.e(tag, "No network connection")
                    _error.postValue("No network connection available")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error refreshing encounters", e)
                _error.postValue("Error: ${e.message}")
            }
        }
    }

    fun markAsSeen(encounterId: String) {
        Log.d(tag, "Marking encounter as seen: $encounterId")
        coroutineScope.launch {
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val token = sharedPreferences.getString("auth_token", "") ?: ""
                    if (token.isEmpty()) {
                        Log.e(tag, "No auth token found")
                        _error.postValue("No authentication token found")
                        return@launch
                    }

                    // Update local database immediately for better UX
                    Log.d(tag, "Updating local database")
                    encounterDao.updateSeenStatus(encounterId, true)

                    // Make API call to update server
                    Log.d(tag, "Making network request to mark as seen")
                    val response = encounterService.markAsSeen("Bearer $token", encounterId)
                    if (!response.isSuccessful) {
                        Log.e(tag, "Error response: ${response.code()} - ${response.message()}")
                        _error.postValue("Error: ${response.code()} - ${response.message()}")
                        // Revert local change if API call fails
                        encounterDao.updateSeenStatus(encounterId, false)
                    }
                } else {
                    Log.e(tag, "No network connection")
                    _error.postValue("No network connection available")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error marking encounter as seen", e)
                _error.postValue("Error marking encounter as seen: ${e.message}")
                // Revert local change if operation fails
                encounterDao.updateSeenStatus(encounterId, false)
            }
        }
    }
}