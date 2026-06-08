package com.jdm.alarmlocation.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Routine")
data class RoutineEntity(
    val isOn: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}