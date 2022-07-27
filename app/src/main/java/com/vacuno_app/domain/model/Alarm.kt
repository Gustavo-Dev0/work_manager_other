package com.vacuno_app.domain.model

data class Alarm(
    var id: String? = null,
    var date: String? = null,
    var type: String? = null,
    var userId: String? = null,
    var sheetId: String? = null,
    var sheetName: String? = null
)
