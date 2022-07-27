package com.vacuno_app.domain.repository

import androidx.lifecycle.MutableLiveData
import com.vacuno_app.domain.model.Production

interface ProductionRepository {
    fun addProduction(p: Production): MutableLiveData<Boolean>
    fun getProductionsFromFarm(_productions: MutableLiveData<List<Production>>)
}