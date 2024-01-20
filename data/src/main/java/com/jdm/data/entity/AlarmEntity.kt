package com.jdm.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarm")
data class AlarmEntity(
    val leftTimeHour: Int,
    val leftTImeMinute: Int,
    val rightTimeHour: Int,
    val rightTimeMinute: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val range: Int,
    val way: Int        //0 - 진입하면, 1 - 벗어나면
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}