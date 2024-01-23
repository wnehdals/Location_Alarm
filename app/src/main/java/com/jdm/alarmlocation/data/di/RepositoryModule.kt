package com.jdm.data.di

import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.data.repository.AlarmRepositoryImpl
import com.jdm.alarmlocation.domain.repository.LocationRepository
import com.jdm.alarmlocation.data.repository.LocationRepositoryImpl
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
