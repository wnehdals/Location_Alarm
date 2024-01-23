package com.jdm.alarmlocation.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jdm.alarmlocation.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarmEntity: AlarmEntity): Long

    @Delete
    suspend fun delete(alarmEntity: AlarmEntity)


    @Query("SELECT * FROM ALARM")
    fun selectAll(): Flow<List<AlarmEntity>>
}