package com.vacuno_app.select_farm

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vacuno_app.domain.model.Farm
import com.vacuno_app.domain.repository.FarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectFarmViewModel @Inject constructor(
    private val repository: FarmRepository
): ViewModel() {

    private val _farmsFromUser = MutableLiveData(emptyList<String>())
    val farmsFromUser: LiveData<List<String>> = _farmsFromUser




    fun getAllFarmsFromCurrentUser(id: String){
        repository.getFarmsFromUser(id, _farmsFromUser)
    }

    fun getFarmById(id: String): MutableLiveData<Farm> {
        val farmLiveData: MutableLiveData<Farm> = MutableLiveData(null)

        repository.getFarmById(id, farmLiveData)
        return farmLiveData
    }

    fun addFarm(nF: Farm, USER_ID: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>(null)
        viewModelScope.launch {
            val r = repository.addFarm(nF, USER_ID)
            result.postValue(r)
        }
        return result
    }
}