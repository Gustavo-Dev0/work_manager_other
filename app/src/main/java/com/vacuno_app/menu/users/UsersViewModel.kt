package com.vacuno_app.menu.users;

import android.util.Log
import androidx.lifecycle.*
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.FarmRepository
import com.vacuno_app.domain.repository.UserRepository
import com.vacuno_app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository,
    private val farmRepository: FarmRepository
): ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun getUsers(){
        //repository.getUsers(_users)
        repository.getUsersFromFarm(_users)
    }

    fun getUserByEmail(email: String): MutableLiveData<User> {
        val userEmailLiveData: MutableLiveData<User> = MutableLiveData(User(uid = "0"))
        repository.getUserByEmail(email, userEmailLiveData)

        return userEmailLiveData
    }

    fun addUserToFarm(uid: String, rol: String) {
        repository.addUserToFarm(uid, rol)
    }

    fun addFarmToUser(uid: String) {
        viewModelScope.launch {
            farmRepository.addUserFarm(Constants.APP_FARM_ID, uid)
        }
    }

}
