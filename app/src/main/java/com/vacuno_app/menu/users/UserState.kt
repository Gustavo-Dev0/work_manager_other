package com.vacuno_app.menu.users

import com.vacuno_app.domain.model.User

data class UserState(
    var userList: List<User> = mutableListOf()
)
