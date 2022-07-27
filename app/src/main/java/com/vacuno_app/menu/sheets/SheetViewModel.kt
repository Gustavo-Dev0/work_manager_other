package com.vacuno_app.menu.sheets;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.domain.repository.SheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SheetViewModel @Inject constructor(
    private val repository: SheetRepository
): ViewModel() {

    private val _sheets = MutableLiveData<List<Sheet>>(emptyList())
    val sheets: LiveData<List<Sheet>> = _sheets


    fun getSheets(){
        repository.getSheetsFromFarm(_sheets)
    }

    fun addSheet(s: Sheet): MutableLiveData<Boolean> {
        return repository.addSheet(s)
    }

    suspend fun updateSheet(s: Sheet): Boolean {
        return repository.updateSheet(s)
    }

    suspend fun deleteSheet(s: Sheet): Boolean {
        return repository.deleteSheet(s)
    }
}
