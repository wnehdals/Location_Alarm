package com.jdm.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.jdm.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun insert( leftTimeHour: Int,
                leftTImeMinute: Int,
                rightTimeHour: Int,
                rightTimeMinute: Int,
                address: String,
                latitude: Double,
                longitude: Double,
                range: Int,
                way: Int): Flow<Int>

}