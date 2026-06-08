package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.data.dao.AlarmDao
import com.jdm.alarmlocation.data.dao.RoutineDao
import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.RoutineEntity
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Routine
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.domain.toAlarmEntity
import com.jdm.alarmlocation.domain.toData
import com.jdm.alarmlocation.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val routineDao: RoutineDao
) : AlarmRepository {
    override suspend fun insert(alarm: Alarm) {
        return alarmDao.insert(alarm.toAlarmEntity())
    }

    override suspend fun getAllAlarm(): List<Alarm> {
        return alarmDao.selectAll().map { it.toDomain() }
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.update(alarm.toAlarmEntity(alarm.id))
    }

    override suspend fun deleteAlarm(id: Long) {
        alarmDao.delete(id)
    }

    override fun getAlarmById(id: Long): Flow<Alarm> {
        return alarmDao.selectById(id).map { it.toDomain() }
    }

    override suspend fun getAllRoutine(): List<Routine> {
        val routines = routineDao.selectAll()
        if (routines.isEmpty())
            return listOf()
        val alarms = alarmDao.selectAll()
        return routines.map { routineEntity ->
            val alarm = alarms.filter { it.routineId == routineEntity.id }
            routineEntity.toDomain(alarm)
        }
    }

    override suspend fun insertRoutine(): Long {
        val id: Long = routineDao.insert(RoutineEntity(isOn = true))
        return id
    }

    override suspend fun updateRoutine(routine: Routine) {
        return routineDao.update(routine.toData())
    }

    override suspend fun getAlarmByRoutineId(id: Long): List<Alarm> {
        return alarmDao.selectAllByRoutineId(id).map { it.toDomain() }
    }

    override suspend fun deleteRoutine(id: Long) {
        routineDao.delete(id)
    }
}