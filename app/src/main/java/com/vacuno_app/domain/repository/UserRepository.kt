package com.vacuno_app.domain.repository

import androidx.lifecycle.MutableLiveData
import com.vacuno_app.data.remote.model.UserFromFirebase
import com.vacuno_app.data.remote.model.UserToFarm
import com.vacuno_app.domain.model.User
import com.vacuno_app.menu.users.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface UserRepository {
    suspend fun saveUser(user: User)

    fun getUsers(liveData: MutableLiveData<List<User>>)
    fun getUsersFromFarm(liveData: MutableLiveData<List<User>>, liveDataFarm: MutableLiveData<List<UserToFarm>>)

    fun getUserByEmail(email: String, liveData: MutableLiveData<User>)
    fun addUserToFarm(uid: String, rol: String)
    fun setStatusUserFromFarm(uid: String, s: String)
    fun setRoleUserFromFarm(uid: String, newRole: String)


}