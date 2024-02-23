package com.jdm.alarmlocation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jdm.alarmlocation.data.dao.AlarmDao
import com.jdm.alarmlocation.data.dao.LocationDao
import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.LocationEntity

@Database(
    entities = [LocationEntity::class, AlarmEntity::class], version = 2
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun alarmDao(): AlarmDao
}