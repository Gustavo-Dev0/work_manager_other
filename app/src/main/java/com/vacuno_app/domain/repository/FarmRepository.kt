package com.vacuno_app.domain.repository

import androidx.lifecycle.MutableLiveData
import com.vacuno_app.domain.model.Farm

interface FarmRepository {

    fun getFarmsFromUser(id: String, liveData: MutableLiveData<List<String>>)
    fun getFarmById(id: String, farmLiveData: MutableLiveData<Farm>)
    suspend fun addFarm(nF: Farm, userID: String): Boolean

    suspend fun addUserFarm(farmKey: String, userID: String): Boolean
}