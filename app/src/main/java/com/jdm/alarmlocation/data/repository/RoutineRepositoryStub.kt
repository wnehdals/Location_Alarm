package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 로컬 스텁 루틴 저장소 (인메모리).
 * 생성 시점 기준 정렬을 [LocationRoutine.createdAt] 으로 보장한다.
 * Room 영속화로 교체 예정.
 */
@Singleton
class RoutineRepositoryStub @Inject constructor() : RoutineRepository {

    private val idSeq = AtomicLong(0)
    private val createdSeq = AtomicLong(0)
    private val store = MutableStateFlow<Map<Long, LocationRoutine>>(emptyMap())

    init {
        seedSample()
    }

    override val routines = store.map { map ->
        map.values.sortedBy { it.createdAt }
    }

    override suspend fun get(id: Long): LocationRoutine? = store.value[id]

    override suspend fun upsert(routine: LocationRoutine): Result<Long> {
        val id = if (routine.id == 0L) idSeq.incrementAndGet() else routine.id
        val createdAt = if (routine.id == 0L) createdSeq.incrementAndGet() else routine.createdAt
        val saved = routine.copy(id = id, createdAt = createdAt)
        store.update { it + (id to saved) }
        return Result.success(id)
    }

    override suspend fun setOn(id: Long, isOn: Boolean): Result<Unit> {
        val current = store.value[id] ?: return Result.failure(NoSuchElementException("루틴을 찾을 수 없습니다."))
        store.update { it + (id to current.copy(isOn = isOn)) }
        return Result.success(Unit)
    }

    override suspend fun delete(id: Long): Result<Unit> {
        store.update { it - id }
        return Result.success(Unit)
    }

    /** 디자인 시안(03 알람 목록)의 예시 카드 3종을 초기 데이터로 시드. */
    private fun seedSample() {
        listOf(
            LocationRoutine(
                title = "학교정문",
                address = "서울 서대문구 ○○대학교 정문 앞",
                latitude = 37.5851,
                longitude = 127.0292,
                radiusMeters = 150,
                direction = AlarmDirection.ENTER,
                days = setOf(1, 2, 3, 4, 5),
                startMinuteOfDay = 8 * 60,
                endMinuteOfDay = 9 * 60 + 30,
                method = AlarmMethod.ALARM,
                isOn = true,
            ),
            LocationRoutine(
                title = "시청역",
                address = "서울 중구 시청역 환승 구역",
                latitude = 37.5658,
                longitude = 126.9770,
                radiusMeters = 250,
                direction = AlarmDirection.ENTER,
                days = setOf(1, 2, 3, 4, 5),
                startMinuteOfDay = 7 * 60 + 30,
                endMinuteOfDay = 10 * 60 + 30,
                method = AlarmMethod.ALARM,
                isOn = true,
            ),
            LocationRoutine(
                title = "헬스장",
                address = "서울 강남구 ○○휴먼짐",
                latitude = 37.4979,
                longitude = 127.0276,
                radiusMeters = 100,
                direction = AlarmDirection.EXIT,
                days = setOf(2, 4),
                method = AlarmMethod.PUSH,
                isOn = false,
            ),
        ).forEach { routine ->
            val id = idSeq.incrementAndGet()
            val createdAt = createdSeq.incrementAndGet()
            store.update { it + (id to routine.copy(id = id, createdAt = createdAt)) }
        }
    }
}
