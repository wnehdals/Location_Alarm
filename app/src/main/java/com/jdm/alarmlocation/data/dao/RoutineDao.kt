package com.jdm.alarmlocation.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jdm.alarmlocation.data.entity.RoutineEntity

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RoutineEntity): Long

    @Query("DELETE FROM ALARM WHERE id = :id")
    suspend fun delete(id: Long)


    @Query("SELECT * FROM ROUTINE")
    suspend fun selectAll(): List<RoutineEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: RoutineEntity)

    @Query("SELECT * FROM ROUTINE WHERE id = :id")
    suspend fun selectById(id: Long): RoutineEntity
}