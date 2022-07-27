package com.vacuno_app.domain.repository

import androidx.lifecycle.MutableLiveData
import com.vacuno_app.domain.model.Sheet

interface SheetRepository {

    fun getSheetsFromFarm(liveData: MutableLiveData<List<Sheet>>)
    fun addSheet(s: Sheet): MutableLiveData<Boolean>
    suspend fun updateSheet(s: Sheet): Boolean
    suspend fun deleteSheet(s: Sheet): Boolean
}