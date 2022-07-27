package com.vacuno_app.data.remote.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.domain.model.Farm
import com.vacuno_app.domain.repository.FarmRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Named

class FarmRepositoryImpl(
    @Named("farmReference")
    private val farmReference: DatabaseReference,
    @Named("userFarmReference")
    private val userFarmReference: DatabaseReference
): FarmRepository {

    override fun getFarmsFromUser(id: String, liveData: MutableLiveData<List<String>>) {
        userFarmReference
            .child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val farmsItems: List<String> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.value.toString()
                    }
                    liveData.postValue(farmsItems)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Nothing to do
                }
            })

    }

    override fun getFarmById(id: String, farmLiveData: MutableLiveData<Farm>) {
        farmReference
            .child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val farm: Farm? = snapshot.getValue(Farm::class.java)?.copy(id= snapshot.key)
                    farmLiveData.postValue(farm)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    override suspend fun addFarm(nF: Farm, userID: String): Boolean {
        return try {
            val key = farmReference.push().key
            nF.userId = userID
            farmReference.child(key!!).setValue(nF.toMap()).await()

            return addUserFarm(key, userID)
        } catch (e: Exception){
            false
        }
    }

    override suspend fun addUserFarm(farmKey: String, userID: String): Boolean {
        return try {
            userFarmReference.child(userID).push().setValue(farmKey).await()
            true
        } catch (e: Exception){
            false
        }
    }

}