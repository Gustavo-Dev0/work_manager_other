package com.vacuno_app.domain.model

data class Production(
    var id: String? = null,
    var dateCreated: String? = null,
    var turn: String? = null,
    var userId: String? = null,
    var total: Double? = null,
    var sheetId: String? = null,
    var sheetName: String? = null
)
