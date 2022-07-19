package com.vacuno_app.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vacuno_app.domain.model.User
import com.vacuno_app.domain.repository.LoginRepository
import com.vacuno_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state


    fun register(user: User) {
        _state.value = state.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val r = repository.register(user.email!!, user.password!!)
            r.onEach { result ->

                if(result.isRegistered){
                    val uid = result.uid
                    user.uid = uid
                    userRepository.saveUser(user)
                }

                _state.value = state.value.copy(
                    isRegistered = result.isRegistered,
                    isLoading = result.isLoading,
                    uid = result.uid
                )
            }.launchIn(viewModelScope)
        }
    }

}