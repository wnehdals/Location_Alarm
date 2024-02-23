package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.NameLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocation(): Flow<List<NameLocation>>
    suspend fun insertLocation(name: String, latitude: Double, longitude: Double)
}