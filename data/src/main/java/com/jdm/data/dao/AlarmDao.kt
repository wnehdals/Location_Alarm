package com.jdm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jdm.data.entity.AlarmEntity
import com.jdm.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmEntity: AlarmEntity): Flow<Int>

    @Delete
    suspend fun delete(alarmEntity: AlarmEntity)


    @Query("SELECT * FROM ALARM")
    fun selectAll(): Flow<List<AlarmEntity>>
}