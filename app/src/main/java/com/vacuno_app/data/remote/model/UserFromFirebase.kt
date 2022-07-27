package com.vacuno_app.data.remote.model

import com.vacuno_app.domain.model.User

data class UserFromFirebase(
    var email: String? = "",
    var name: String? = "",
    var lastName: String? = "",
    var status: String? = ""
) {
    fun toUser(): User {
        return User(
            email = email,
            name = name,
            lastName = lastName,
            status = status
        )
    }
}
