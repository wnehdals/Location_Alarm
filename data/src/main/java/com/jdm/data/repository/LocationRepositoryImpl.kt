package com.jdm.data.repository

import com.jdm.data.dao.LocationDao
import com.jdm.data.entity.LocationEntity
import com.jdm.data.entity.toNameLocation
import com.jdm.model.NameLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
): LocationRepository {
    override fun getAllLocation(): Flow<List<NameLocation>> {
        return locationDao.selectAll().map { entities ->
            entities.map { it.toNameLocation() }
        }
    }

    override suspend fun insertLocation(name: String, latitude: Double, longitude: Double) {
        locationDao.insert(LocationEntity(name, latitude, longitude))
    }
}