package com.jdm.data.repository

import com.jdm.model.NameLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocation(): Flow<List<NameLocation>>
    suspend fun insertLocation(name: String, latitude: Double, longitude: Double)
}