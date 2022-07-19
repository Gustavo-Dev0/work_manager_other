package com.vacuno_app.menu.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vacuno_app.domain.repository.LoginRepository
import com.vacuno_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private var repository: LoginRepository
) : ViewModel() {




    suspend fun logout(){
        repository.logout()
    }

}