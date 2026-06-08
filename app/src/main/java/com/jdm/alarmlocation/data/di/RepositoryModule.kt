package com.jdm.alarmlocation.data.di

import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.data.repository.AlarmRepositoryImpl
import com.jdm.alarmlocation.data.repository.SearchRepositoryImpl
import com.jdm.alarmlocation.domain.repository.SearchRepository
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
    abstract fun bindAlarmRepository(alarmRepository: AlarmRepositoryImpl): AlarmRepository

    @Singleton
    @Binds
    abstract fun bindSearchRepository(searchRepository: SearchRepositoryImpl): SearchRepository

}
