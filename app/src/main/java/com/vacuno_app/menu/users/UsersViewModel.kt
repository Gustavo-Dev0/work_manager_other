package com.vacuno_app.menu.users;

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun getUsers(){
        repository.getUsers(_users)
    }

    fun getUserByEmail(email: String): User? {
        val user: User? = null
        return try {

            /*val userRecord: UserRecord = FirebaseAuth.getInstance().getUserByEmail(email)
            User(uid = userRecord.uid, email = userRecord.email)*/

            //Revisar si el usuario ya está registrado(en sevidor backend)
            null
        } catch (e: Exception) {
            Log.e("", e.message.toString())
            null
        }

    }

}
