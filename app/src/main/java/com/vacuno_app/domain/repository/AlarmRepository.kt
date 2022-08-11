package com.vacuno_app.domain.repository

import androidx.lifecycle.MutableLiveData
import com.vacuno_app.domain.model.Alarm

interface AlarmRepository {

    fun addAlarm(a: Alarm): MutableLiveData<Boolean>
    fun getAlarmsFromFarm(liveData: MutableLiveData<List<Alarm>>)
    fun editAlarm(a: Alarm)
    fun deleteAlarm(id: String)
}