package com.kamran.xurveykshan.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
data class DataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("Q1") val age : Int,
    @ColumnInfo("Q2") val imageUri : String,
    @ColumnInfo("recording") val recording : String,
    @ColumnInfo("submit_time") val submitTime : String
)
