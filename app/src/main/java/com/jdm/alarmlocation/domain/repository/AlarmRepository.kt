package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insert( leftTimeHour: Int,
                leftTImeMinute: Int,
                rightTimeHour: Int,
                rightTimeMinute: Int,
                address: String,
                latitude: Double,
                longitude: Double,
                range: Int,
                way: Int,
                isOn: Boolean): Long
    suspend fun getAllAlarm(): List<Alarm>
    suspend fun updateAlarm(alarm: Alarm): Int

}