package com.vacuno_app.login;

import android.app.ProgressDialog
import android.util.Log
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.vacuno_app.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {

        _state.value = state.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val r = repository.login(email, password)
            r.onEach { result ->
                //Log.e("viewModel", "logged "+result.isLogged)
                //Log.e("viewModel", "loading "+result.isLoading)
                _state.value = state.value.copy(
                    isLogged = result.isLogged,
                    isLoading = result.isLoading
                )
            }.launchIn(viewModelScope)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

}
