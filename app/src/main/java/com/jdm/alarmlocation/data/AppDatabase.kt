package com.jdm.alarmlocation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jdm.alarmlocation.data.dao.AlarmDao
import com.jdm.alarmlocation.data.dao.LocationDao
import com.jdm.alarmlocation.data.dao.RoutineDao
import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.LocationEntity
import com.jdm.alarmlocation.data.entity.RoutineEntity
import com.jdm.alarmlocation.data.resp.LongListConverter

@Database(
    entities = [LocationEntity::class, AlarmEntity::class, RoutineEntity::class], version = 2
)
@TypeConverters(value = [IntListConverter::class, LongListConverter::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun alarmDao(): AlarmDao
    abstract fun routineDao(): RoutineDao
}
