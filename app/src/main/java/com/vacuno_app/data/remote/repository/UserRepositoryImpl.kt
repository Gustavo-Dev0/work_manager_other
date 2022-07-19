package com.vacuno_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.vacuno_app.data.remote.model.UserFromFirebase
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userReference: DatabaseReference
): UserRepository {

    override suspend fun saveUser(user: User) {
        val newUser = user.toUserFromFirebase()
        userReference.child(user.uid!!).setValue(newUser).addOnFailureListener{
            Log.e("UsersRepository", it.message.toString())
        }
    }

    override fun getUsers(liveData: MutableLiveData<List<User>>) {
        //FirebaseDatabase.getInstance().getReference("users")
        userReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userItems: List<User> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(UserFromFirebase::class.java)!!.toUser().copy(uid = dataSnapshot.key!!)
                    }
                    liveData.postValue(userItems)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Nothing to do
                }
            })
    }
}