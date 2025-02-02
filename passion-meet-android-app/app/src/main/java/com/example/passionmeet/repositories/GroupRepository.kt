package com.example.passionmeet.repositories

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

class GroupRepository {

    private val groupService = RetrofitClient.instance.create(GroupService::class.java)

    private val _groupData = MutableLiveData<List<GroupModel>>()
    val groupData get() = _groupData

    fun getSelfGroups() {
        val call = this.groupService.getSelfGroups()

        call.enqueue(object : Callback<List<GroupResponseDTO>> {
            override fun onResponse(
                call: Call<List<GroupResponseDTO>>,
                response: Response<List<GroupResponseDTO>>
            ) {
                val body = response.body()

                this@GroupRepository._groupData.value = body?.let {
                    mapGroupToGroupModel(
                        it
                        //TODO: Implementer une logique de sauvegarde des données avant de renvoyer la donnée à la vue
                    )
                }
            }

            override fun onFailure(call: Call<List<GroupResponseDTO>>, t: Throwable) {
                Log.e("Error getAllPassions()", "Error: ${t.message}")
            }

        })
    }

}
