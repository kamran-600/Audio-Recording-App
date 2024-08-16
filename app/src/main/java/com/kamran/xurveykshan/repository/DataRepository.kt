package com.kamran.xurveykshan.repository

import com.kamran.xurveykshan.data.DataDao
import com.kamran.xurveykshan.data.DataEntity

class DataRepository(private val dataDao: DataDao) {

    val listLiveData  = dataDao.getAll()


    suspend fun insertContact(data: DataEntity) {
        dataDao.insertContact(data)
    }


}