package com.vacuno_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.vacuno_app.data.remote.model.UserFromFirebase
import com.vacuno_app.data.remote.model.UserToFarm
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.UserRepository
import com.vacuno_app.utils.Constants
import javax.inject.Named

class UserRepositoryImpl(
    @Named("userReference")
    private val userReference: DatabaseReference,
    @Named("userRegisteredReference")
    private val userRegisteredReference: DatabaseReference,
): UserRepository {

    override suspend fun saveUser(user: User) {
        val newUser = user.toUserFromFirebase()
        userRegisteredReference.child(user.uid!!).setValue(newUser).addOnFailureListener{
            Log.e("UsersRepository/register", it.message.toString())
        }
    }

    override fun getUsersFromFarm(liveData: MutableLiveData<List<User>>)  {
        userReference
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userItems: List<UserToFarm> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(UserToFarm::class.java)!!
                    }

                    val users: MutableList<User> = mutableListOf()
                    for(uI in userItems){
                        getUserById(uI.userId!!, users, liveData)
                    }

                    liveData.postValue(users)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Nothing to do
                }
            })
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

    fun getUserById(uid: String, users: MutableList<User>, liveData: MutableLiveData<List<User>>) {
        var user: User? = null
        userRegisteredReference.child(uid).get().addOnSuccessListener {
            user = it.getValue(User::class.java)
            users.add(user!!)
            liveData.postValue(users)
        }.addOnFailureListener {
            Log.e("getUserById", "no user found")
        }
    }


    override fun getUserByEmail(email: String, liveData: MutableLiveData<User>) {

        userRegisteredReference.orderByChild("email").equalTo(email)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userItems: List<User> = snapshot.children.map { dataSnapshot ->
                            dataSnapshot.getValue(User::class.java)!!.copy(uid = dataSnapshot.key!!)
                        }
                        liveData.postValue(userItems[0])
                    }else{
                        liveData.postValue(null)
                    }

                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun addUserToFarm(uid: String, rol: String) {
        val userToFarm: UserToFarm = UserToFarm(userId = uid, status = "A", role = rol)
        userReference.child(Constants.APP_FARM_ID).push().setValue(userToFarm)
    }
}