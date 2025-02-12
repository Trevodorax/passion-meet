package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.data.local.PassionMeetDatabase
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.mapper.mapPassionCategoryDtoToPassionCategoryModel
import com.example.passionmeet.mapper.mapPassionDtoToPassionEntity
import com.example.passionmeet.mapper.mapPassionEntityToPassionCategoryModel
import com.example.passionmeet.models.PassionCategoryModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.network.services.PassionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.passionmeet.utils.NetworkUtils

class PassionRepository(private val context: Context) {
    private val passionService = RetrofitClient.instance.create(PassionService::class.java)
    private val passionDao = PassionMeetDatabase.getDatabase(context).passionDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _passionData = MutableLiveData<List<PassionCategoryModel>>()
    val passionData get() = _passionData

    // self data
    private val _selfPassionData = MutableLiveData<List<PassionCategoryModel>>()
    val selfPassionData get() = _selfPassionData

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
}
