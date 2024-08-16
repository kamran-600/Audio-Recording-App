package com.kamran.xurveykshan.viewModels

import androidx.lifecycle.ViewModel
import com.kamran.xurveykshan.repository.DataRepository

class DataViewModel(private val dataRepository: DataRepository) : ViewModel() {

    val listLiveData get() = dataRepository.listLiveData

}