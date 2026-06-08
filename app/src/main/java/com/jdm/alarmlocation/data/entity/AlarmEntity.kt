package com.jdm.alarmlocation.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarm")
data class AlarmEntity(
    val routineId: Long,
    val placeTitle: String,
    val latitude: Double,
    val longitude: Double,
    val range: Int,
    val isIn: Boolean,
    val day: Int,
    val hour: Int,
    val minute: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}