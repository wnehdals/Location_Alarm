package com.jdm.alarmlocation.domain.repository

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
                way: Int): Long

}