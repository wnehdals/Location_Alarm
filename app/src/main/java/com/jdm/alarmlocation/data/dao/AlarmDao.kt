package com.jdm.alarmlocation.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jdm.alarmlocation.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmEntity: AlarmEntity): Long

    @Delete
    suspend fun delete(alarmEntity: AlarmEntity)


    @Query("SELECT * FROM ALARM")
    suspend fun selectAll(): List<AlarmEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(alarmEntity: AlarmEntity): Int
}