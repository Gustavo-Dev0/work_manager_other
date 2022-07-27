package com.vacuno_app.menu.production

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vacuno_app.domain.model.Production
import com.vacuno_app.domain.repository.ProductionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductionViewModel @Inject constructor(
    private val repository: ProductionRepository
): ViewModel() {

    private val _productions = MutableLiveData<List<Production>>(emptyList())
    val productions: LiveData<List<Production>> = _productions


    fun getProductions() {
        repository.getProductionsFromFarm(_productions)
    }

    fun addProduction(p: Production): MutableLiveData<Boolean> {
        return repository.addProduction(p)
    }

}