package com.vacuno_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.data.remote.model.UserToFarm
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.SheetRepository
import com.vacuno_app.utils.Constants
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Named

class SheetRepositoryImpl(
    @Named("sheetReference")
    private val sheetReference: DatabaseReference
): SheetRepository {
    override fun getSheetsFromFarm(liveData: MutableLiveData<List<Sheet>>) {
        sheetReference
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val sheetItems: List<Sheet> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Sheet::class.java)!!
                    }

                    liveData.postValue(sheetItems)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun addSheet(s: Sheet): MutableLiveData<Boolean> {

        var liveData = MutableLiveData<Boolean>(null)

        try {
            sheetReference
                .child(Constants.APP_FARM_ID)
                .push()
                .setValue(s.toMap()).addOnSuccessListener {
                    liveData.postValue(true)
                }.addOnFailureListener {
                    liveData.postValue(false)
                }
        } catch (e: Exception) {
            Log.e("Error añadiendo", e.message.toString())
        }
        return liveData
    }

    override suspend fun updateSheet(s: Sheet): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSheet(s: Sheet): Boolean {
        TODO("Not yet implemented")
    }
}