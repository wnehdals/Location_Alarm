package com.jdm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jdm.data.dao.AlarmDao
import com.jdm.data.dao.LocationDao
import com.jdm.data.entity.AlarmEntity
import com.jdm.data.entity.LocationEntity

@Database(
    entities = [LocationEntity::class, AlarmEntity::class], version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun alarmDao(): AlarmDao
}