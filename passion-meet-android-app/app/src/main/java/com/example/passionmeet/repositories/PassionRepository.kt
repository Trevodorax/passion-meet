package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.local.dao.PassionDao
import com.example.passionmeet.mapper.mapPassionCategoryDtoToPassionCategoryModel
import com.example.passionmeet.mapper.mapPassionDtoToPassionEntity
import com.example.passionmeet.mapper.mapPassionEntityToPassionCategoryModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.network.services.PassionService
import com.example.passionmeet.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PassionRepository(
    private val context: Context,
    private val passionService: PassionService,
    private val passionDao: PassionDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _passionData = MutableLiveData<List<PassionCategoryModel>>()
    val passionData get() = _passionData

    // self data
    private val _selfPassionData = MutableLiveData<List<PassionCategoryModel>>()
    val selfPassionData get() = _selfPassionData

    // Result of update operation
    private val _updatePassionsResult = MutableLiveData<Boolean>()
    val updatePassionsResult get() = _updatePassionsResult

    /**
     * Resets the update passions result value
     */
    fun resetUpdatePassionsResult() {
        _updatePassionsResult.value = null
    }

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getAllPassions() {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // No network available, load from local database only
                val localPassions = passionDao.getAllPassions()
                withContext(Dispatchers.Main) {
                    _passionData.value = mapPassionEntityToPassionCategoryModel(localPassions)
                }
                return@launch
            }

            // Network is available, try to fetch from API
            try {
                val call = passionService.getAllPassions()
                call.enqueue(object : retrofit2.Callback<List<PassionDto>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<PassionDto>>,
                        response: retrofit2.Response<List<PassionDto>>
                    ) {
                        val body = response.body()
                        body?.let {
                            val passionModels = mapPassionCategoryDtoToPassionCategoryModel(it)
                            _passionData.value = passionModels
                            
                            // Save to local database
                            coroutineScope.launch {
                                val entities = it.map { dto -> mapPassionDtoToPassionEntity(dto) }
                                passionDao.deleteAll() // Clear old data
                                passionDao.insertAll(entities)
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<List<PassionDto>>, t: Throwable) {
                        Log.e("Error getAllPassions()", "Error: ${t.message}")
                        // Load from local database on failure
                        coroutineScope.launch {
                            val localPassions = passionDao.getAllPassions()
                            withContext(Dispatchers.Main) {
                                _passionData.value = mapPassionEntityToPassionCategoryModel(localPassions)
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("PassionRepository", "Error fetching passions", e)
                // Load from local database on error
                val localPassions = passionDao.getAllPassions()
                withContext(Dispatchers.Main) {
                    _passionData.value = mapPassionEntityToPassionCategoryModel(localPassions)
                }
            }
        }
    }

    fun getSelfPassions() {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                // No network available, load from local database only
                val localSelfPassions = passionDao.getSelfPassions()
                withContext(Dispatchers.Main) {
                    _selfPassionData.value = mapPassionEntityToPassionCategoryModel(localSelfPassions)
                }
                return@launch
            }

            try {
                val call = passionService.getSelfPassions(
                    "Bearer ${sharedPreferences.getString("auth_token", "")}"
                )

                call.enqueue(object : retrofit2.Callback<List<PassionDto>> {
                    override fun onResponse(
                        call: retrofit2.Call<List<PassionDto>>,
                        response: retrofit2.Response<List<PassionDto>>
                    ) {
                        val body = response.body()
                        body?.let {
                            val passionModels = mapPassionCategoryDtoToPassionCategoryModel(it)
                            _selfPassionData.value = passionModels

                            // Save to local database with isSelfPassion flag
                            coroutineScope.launch {
                                val entities = it.map { dto -> 
                                    mapPassionDtoToPassionEntity(dto).copy(isSelfPassion = true)
                                }
                                // Update only self passions
                                passionDao.deleteAllSelfPassions()
                                passionDao.insertAll(entities)
                            }
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<List<PassionDto>>, t: Throwable) {
                        Log.e("Error getSelfPassions()", "Error: ${t.message}")
                        // Load from local database on failure
                        coroutineScope.launch {
                            val localSelfPassions = passionDao.getSelfPassions()
                            withContext(Dispatchers.Main) {
                                _selfPassionData.value = mapPassionEntityToPassionCategoryModel(localSelfPassions)
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("PassionRepository", "Error fetching self passions", e)
                // Load from local database on error
                val localSelfPassions = passionDao.getSelfPassions()
                withContext(Dispatchers.Main) {
                    _selfPassionData.value = mapPassionEntityToPassionCategoryModel(localSelfPassions)
                }
            }
        }
    }

    fun updateMultiplePassions(passionIds: List<String>) {
        coroutineScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context) || !NetworkUtils.hasInternetConnection()) {
                withContext(Dispatchers.Main) {
                    _updatePassionsResult.value = false
                }
                return@launch
            }

            try {
                val passionsToUpdate = passionIds.map { 
                    com.example.passionmeet.network.dto.AddPassionDto(it) 
                }
                
                val call = passionService.updateMultiplePassions(
                    "Bearer ${sharedPreferences.getString("auth_token", "")}",
                    passionsToUpdate
                )

                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(
                        call: retrofit2.Call<Void>,
                        response: retrofit2.Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            _updatePassionsResult.value = true
                            
                            // Update local database to mark these passions as selected
                            coroutineScope.launch {
                                // First, reset all self passions
                                passionDao.deleteAllSelfPassions()
                                
                                // Then get the current passions to update their isSelfPassion flag
                                val allPassions = passionDao.getAllPassions()
                                val passionsToUpdate = allPassions.filter { 
                                    passionIds.contains(it.id) 
                                }.map { 
                                    it.copy(isSelfPassion = true) 
                                }
                                
                                if (passionsToUpdate.isNotEmpty()) {
                                    passionDao.insertAll(passionsToUpdate)
                                }
                                
                                // Refresh self passions data
                                getSelfPassions()
                            }
                        } else {
                            _updatePassionsResult.value = false
                            Log.e("PassionRepository", "Error updating passions: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        _updatePassionsResult.value = false
                        Log.e("PassionRepository", "Error updating passions", t)
                    }
                })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _updatePassionsResult.value = false
                }
                Log.e("PassionRepository", "Error updating passions", e)
            }
        }
    }
}
