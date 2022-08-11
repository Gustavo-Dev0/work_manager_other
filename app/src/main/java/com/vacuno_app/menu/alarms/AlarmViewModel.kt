package com.vacuno_app.menu.alarms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vacuno_app.domain.model.Alarm
import com.vacuno_app.domain.model.Production
import com.vacuno_app.domain.repository.AlarmRepository
import com.vacuno_app.domain.repository.ProductionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository
): ViewModel() {

    private val _alarms = MutableLiveData<List<Alarm>>(emptyList())
    val alarms: LiveData<List<Alarm>> = _alarms

    fun getAlarms() {
        repository.getAlarmsFromFarm(_alarms)
    }

    fun addAlarm(a: Alarm): MutableLiveData<Boolean> {
        return repository.addAlarm(a)
    }

    fun editAlarm(a: Alarm) {
        repository.editAlarm(a)
    }

    fun alarmDeleteInFarm(id: String) {
        repository.deleteAlarm(id)
    }
}