package com.kamran.xurveykshan.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {

    @Insert
    suspend fun insertContact(contact: DataEntity)

    @Query("SELECT * FROM contacts_table")
    fun getAll(): LiveData<List<DataEntity>>

}