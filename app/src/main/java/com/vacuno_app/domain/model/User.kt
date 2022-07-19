package com.vacuno_app.domain.model

import com.vacuno_app.data.remote.model.UserFromFirebase

data class User (
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var lastName: String? = null,
    var status: String? = null,
    var farmId: String? = null,
    var password: String? = null
) {
    fun toUserFromFirebase(): UserFromFirebase {
        return UserFromFirebase(
            email = email!!,
            name = name,
            lastName = lastName,
            status = status,
            farmId = farmId
        )
    }
}
