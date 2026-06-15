package com.jdm.alarmlocation.data.di

import com.jdm.alarmlocation.data.repository.AlarmRepositoryImpl
import com.jdm.alarmlocation.data.repository.AppConfigRepositoryImpl
import com.jdm.alarmlocation.data.repository.AuthRepositoryStub
import com.jdm.alarmlocation.data.repository.RoutineRepositoryStub
import com.jdm.alarmlocation.data.repository.SearchRepositoryImpl
import com.jdm.alarmlocation.data.repository.TicketRepositoryStub
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.domain.repository.AppConfigRepository
import com.jdm.alarmlocation.domain.repository.AuthRepository
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.domain.repository.SearchRepository
import com.jdm.alarmlocation.domain.repository.TicketRepository
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

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryStub): AuthRepository

    @Singleton
    @Binds
    abstract fun bindTicketRepository(impl: TicketRepositoryStub): TicketRepository

    @Singleton
    @Binds
    abstract fun bindRoutineRepository(impl: RoutineRepositoryStub): RoutineRepository

    @Singleton
    @Binds
    abstract fun bindAppConfigRepository(impl: AppConfigRepositoryImpl): AppConfigRepository
}
