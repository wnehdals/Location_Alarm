package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.data.dao.AlarmDao
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override fun insert(
        leftTimeHour: Int,
        leftTImeMinute: Int,
        rightTimeHour: Int,
        rightTimeMinute: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        range: Int,
        way: Int
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
                way = way
            )
        )
    }
}