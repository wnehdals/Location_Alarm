package com.jdm.alarmlocation.data.dao

import androidx.room.*
import com.jdm.alarmlocation.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationEntity: LocationEntity)

    @Update
    suspend fun updateAll(locationList: List<LocationEntity>)

    @Delete
    suspend fun delete(locationEntity: LocationEntity)

    @Query("SELECT * FROM LOCATION")
    fun selectAll(): Flow<List<LocationEntity>>
}