package com.vacuno_app.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.vacuno_app.login.LoginState
import com.vacuno_app.register.RegisterState
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(email: String, password: String): Flow<LoginState>
    fun getCurrentUser(): FirebaseUser?
    suspend fun logout()
    suspend fun register(email: String, password: String): Flow<RegisterState>
    suspend fun updateProfileOfUser(userProfileChangeRequest: UserProfileChangeRequest)
}