package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.data.dao.AlarmDao
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.toAlarm
import com.jdm.alarmlocation.data.entity.toAlarmEntity
import com.jdm.alarmlocation.data.entity.toNameLocation
import com.jdm.alarmlocation.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override suspend fun insert(
        leftTimeHour: Int,
        leftTImeMinute: Int,
        rightTimeHour: Int,
        rightTimeMinute: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        range: Int,
        way: Int,
        isOn: Boolean
    ): Long {
        return alarmDao.insert(
            AlarmEntity(
                leftTimeHour = leftTimeHour,
                leftTImeMinute = leftTImeMinute,
                rightTimeHour = rightTimeHour,
                rightTimeMinute = rightTimeMinute,
                address = address,
                latitude = latitude,
                longitude = longitude,
                range = range,
                way = way,
                isOn = isOn
            )
        )
    }

    override suspend fun getAllAlarm(): List<Alarm> {
        /*
        return alarmDao.selectAll().map { entities ->
            entities.map { it.toAlarm() }
        }

         */
        return  alarmDao.selectAll().map { entities -> entities.toAlarm() }
    }

    override suspend fun updateAlarm(alarm: Alarm): Int {
        val id = alarmDao.update(alarm.toAlarmEntity())
        return id
    }

    override suspend fun deleteAlarm(alarm: Alarm): Int {
        return alarmDao.delete(alarm.toAlarmEntity())
    }
}