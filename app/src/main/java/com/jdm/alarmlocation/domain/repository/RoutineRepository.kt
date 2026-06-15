package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.LocationRoutine
import kotlinx.coroutines.flow.Flow

/**
 * 위치 알람 루틴 저장소 (REQUIREMENTS_SPEC §3, §4).
 * 목록 정렬: 생성 시점 기준. ON/OFF, 수정, 삭제 지원.
 */
interface RoutineRepository {
    /** 생성 시점 오름차순 정렬된 전체 루틴. */
    val routines: Flow<List<LocationRoutine>>

    suspend fun get(id: Long): LocationRoutine?

    /** id == 0 이면 생성, 아니면 수정. 저장된 id 반환. */
    suspend fun upsert(routine: LocationRoutine): Result<Long>

    suspend fun setOn(id: Long, isOn: Boolean): Result<Unit>

    suspend fun delete(id: Long): Result<Unit>
}
