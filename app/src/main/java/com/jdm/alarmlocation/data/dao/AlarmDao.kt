package com.jdm.alarmlocation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jdm.alarmlocation.data.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmEntity: AlarmEntity)

    @Query("DELETE FROM ALARM WHERE id = :id")
    suspend fun delete(id: Long)


    @Query("SELECT * FROM ALARM")
    suspend fun selectAll(): List<AlarmEntity>

    @Query("SELECT * FROM ALARM WHERE routineId = :routineId")
    suspend fun selectAllByRoutineId(routineId: Long): List<AlarmEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM ALARM WHERE id = :id")
    fun selectById(id: Long): Flow<AlarmEntity>
}