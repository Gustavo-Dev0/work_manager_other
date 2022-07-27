package com.vacuno_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.domain.model.Production
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.domain.repository.ProductionRepository
import com.vacuno_app.utils.Constants
import javax.inject.Named

class ProductionRepositoryImpl(
    @Named("productionReference")
    private var productionReference: DatabaseReference): ProductionRepository {


    override fun addProduction(p: Production): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>(null)

        try {
            productionReference
                .child(Constants.APP_FARM_ID)
                .push()
                .setValue(p).addOnSuccessListener {
                    liveData.postValue(true)
                }.addOnFailureListener {
                    liveData.postValue(false)
                }
        } catch (e: Exception) {
            Log.e("Error añadiendo", e.message.toString())
        }
        return liveData
    }

    override fun getProductionsFromFarm(liveData: MutableLiveData<List<Production>>) {
        productionReference
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val productionItems: List<Production> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Production::class.java)!!
                    }

                    liveData.postValue(productionItems)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}