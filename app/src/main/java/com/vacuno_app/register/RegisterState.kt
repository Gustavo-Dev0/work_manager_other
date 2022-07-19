package com.vacuno_app.register

data class RegisterState(
    var isRegistered: Boolean = true,
    var isLoading: Boolean? = null,
    var uid: String? = null
)
