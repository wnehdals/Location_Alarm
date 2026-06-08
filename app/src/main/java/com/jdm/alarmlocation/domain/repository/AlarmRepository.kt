package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.RoutineEntity
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insert(alarm: Alarm)
    suspend fun getAllAlarm(): List<Alarm>
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(id: Long)
    suspend fun getAlarmByRoutineId(id: Long): List<Alarm>
    fun getAlarmById(id: Long): Flow<Alarm>
    suspend fun getAllRoutine(): List<Routine>
    suspend fun insertRoutine(): Long
    suspend fun updateRoutine(routine: Routine)
    suspend fun deleteRoutine(id: Long)

}