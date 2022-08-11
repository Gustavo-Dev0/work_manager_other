package com.vacuno_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.domain.model.Alarm
import com.vacuno_app.domain.model.Production
import com.vacuno_app.domain.repository.AlarmRepository
import com.vacuno_app.utils.Constants
import javax.inject.Named

class AlarmRepositoryImpl(
    @Named("alarmReference")
    private val alarmReference: DatabaseReference
): AlarmRepository {
    override fun addAlarm(a: Alarm): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>(null)

        try {
            alarmReference
                .child(Constants.APP_FARM_ID)
                .push()
                .setValue(a).addOnSuccessListener {
                    liveData.postValue(true)
                }.addOnFailureListener {
                    liveData.postValue(false)
                }
        } catch (e: Exception) {
            Log.e("Error añadiendo", e.message.toString())
        }
        return liveData
    }

    override fun getAlarmsFromFarm(liveData: MutableLiveData<List<Alarm>>) {
        alarmReference
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val productionItems: List<Alarm> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Alarm::class.java)!!.copy(id = dataSnapshot.key)
                    }

                    liveData.postValue(productionItems)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun editAlarm(a: Alarm) {
        val idA: String = ""+a.id!!
        a.id = null
        alarmReference
            .child(Constants.APP_FARM_ID)
            .child(idA)
            .setValue(a)
    }

    override fun deleteAlarm(id: String) {
        alarmReference
            .child(Constants.APP_FARM_ID)
            .child(id)
            .setValue(null)
    }
}