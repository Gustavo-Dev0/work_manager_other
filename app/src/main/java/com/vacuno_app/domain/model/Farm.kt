package com.vacuno_app.domain.model

import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*


data class Farm(
    var id: String? = null,
    var name: String? = null,
    var date: Long? = null,
    var status: String? = null,
    var userId: String? = null
) {
    fun toMap(): Map<String, Any> {


        val map: MutableMap<String, Any> = HashMap()

        map["name"] = name!!
        map["date"] = ServerValue.TIMESTAMP
        map["status"] = "A"
        map["userId"] = userId!!

        return map
    }
}
