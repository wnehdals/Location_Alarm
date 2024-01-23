package com.jdm.alarmlocation.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class LocationEntity(
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}