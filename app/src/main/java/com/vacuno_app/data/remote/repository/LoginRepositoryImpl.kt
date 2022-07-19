package com.vacuno_app.data.remote.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.vacuno_app.domain.repository.LoginRepository
import com.vacuno_app.login.LoginState
import com.vacuno_app.register.RegisterState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
): LoginRepository {
    override suspend fun login(email: String, password: String): Flow<LoginState> {
        var flowResponse: Flow<LoginState>
        delay(3000)
        try {
            var isSuccessful = false
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { isSuccessful = true }
                .addOnFailureListener {
                    Log.e("IN REPOSITORY LOGIN", "ERROR")
                    isSuccessful = false
                }
                .await()
            flowResponse = MutableStateFlow(LoginState(isLogged = isSuccessful, isLoading = false))

        } catch (e: Exception){
            flowResponse = MutableStateFlow(LoginState(isLogged = false, isLoading = false))
        }
        return flowResponse
    }


    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }


    override suspend fun register(email: String, password: String): Flow<RegisterState> {
        var flowResponse: Flow<RegisterState>
        var uidResult: String? = null
        delay(3000)
        try {
            var isSuccessful = false
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    isSuccessful = true
                    uidResult = it.user?.uid
                }
                .await()

            flowResponse = MutableStateFlow(RegisterState(isRegistered = isSuccessful, isLoading = false, uid = uidResult))

        } catch (e: Exception){
            Log.e("ERROR CREANDO", e.message.toString())
            flowResponse = MutableStateFlow(RegisterState(isRegistered = false, isLoading = false))
        }
        return flowResponse
    }

    override suspend fun updateProfileOfUser(userProfileChangeRequest: UserProfileChangeRequest) {

    }
}