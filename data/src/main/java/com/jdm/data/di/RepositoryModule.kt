package com.jdm.data.di

import com.jdm.data.repository.AlarmRepository
import com.jdm.data.repository.AlarmRepositoryImpl
import com.jdm.data.repository.LocationRepository
import com.jdm.data.repository.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Singleton
    @Binds
    abstract fun bindLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository
    @Singleton
    @Binds
    abstract fun bindAlarmRepository(alarmRepository: AlarmRepositoryImpl): AlarmRepository

}
