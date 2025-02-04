package com.example.passionmeet.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.passionmeet.mapper.mapGroupToGroupModel
import com.example.passionmeet.models.GroupModel
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.services.GroupService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupRepository(context: Context) {

    private val groupService = RetrofitClient.instance.create(GroupService::class.java)

    private val _groupData = MutableLiveData<List<GroupModel>>()
    val groupData get() = _groupData

    /**
     * Shared preferences for getting the auth token
     */
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    fun getSelfGroups() {
        val call = this.groupService.getSelfGroups(
            "Bearer ${
                sharedPreferences.getString(
                    "auth_token",
                    ""
                )
            }"
        )

        call.enqueue(object : Callback<List<GroupResponseDTO>> {
            override fun onResponse(
                call: Call<List<GroupResponseDTO>>,
                response: Response<List<GroupResponseDTO>>
            ) {
                val bodyString = response.errorBody()?.string() ?: response.body().toString()
                Log.e("GroupRepository", "Response body: $bodyString")

                if (!response.isSuccessful || response.body() == null) {
                    Log.e("GroupRepository", "Error: ${response.code()} - ${response.message()}")
                    return
                }

                val body = response.body()

                this@GroupRepository._groupData.value = body?.let {
                    Log.e("GroupRepository", "Response received: ${response.body()}")
                    mapGroupToGroupModel(
                        it
                    )
                        //TODO: Implementer une logique de sauvegarde des données avant de renvoyer la donnée à la vue
                }
            }

            override fun onFailure(call: Call<List<GroupResponseDTO>>, t: Throwable) {
                Log.e("GroupRepository", "Error fetching groups: ${t.message}")
                Log.e("GroupRepository", "Error fetching groups: ${t.stackTrace}")

                // log respons if any
                Log.e("GroupRepository", "Error fetching groups: ${t.cause}")
            }

        })
    }

}
