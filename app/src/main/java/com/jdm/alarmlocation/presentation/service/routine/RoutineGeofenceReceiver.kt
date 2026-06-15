package com.jdm.alarmlocation.presentation.service.routine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.domain.repository.TicketRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 신규 [LocationRoutine] 지오펜스 이벤트 수신 → 조건 검사 → 발생 처리 (SPEC §5, §6).
 *
 * 검사 순서: 방향(진입/이탈) → 요일 → 시간 범위 → 쿨다운. 모두 충족 시 티켓 1개 차감 후
 * 방식(알람/푸시)으로 발생. 차감 결과 0이면 해당 루틴 자동 OFF(D4) → 매니저가 지오펜스 해제.
 */
@AndroidEntryPoint
class RoutineGeofenceReceiver : BroadcastReceiver() {

    @Inject lateinit var routineRepository: RoutineRepository
    @Inject lateinit var ticketRepository: TicketRepository
    @Inject lateinit var cooldownStore: AlarmCooldownStore
    @Inject lateinit var notifier: AlarmNotifier

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e(TAG, "geofence error: ${event.errorCode}")
            return
        }
        val transition = event.geofenceTransition
        val ids = event.triggeringGeofences
            ?.mapNotNull { RoutineGeofenceManager.parseId(it.requestId) }
            ?.distinct()
            ?: return
        if (ids.isEmpty()) return

        val pending = goAsync()
        scope.launch {
            try {
                ids.forEach { id -> handleTrigger(id, transition) }
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun handleTrigger(id: Long, transition: Int) {
        val routine = routineRepository.get(id) ?: return
        if (!routine.isOn) return
        if (!directionMatches(routine, transition)) return
        if (!ScheduleUtil.isScheduledToday(routine)) return
        if (!ScheduleUtil.isWithinTime(routine)) return
        if (!cooldownStore.shouldFire(routine)) return

        // 발생당 티켓 1개 차감 (SPEC §6). 잔량 부족 시 발생하지 않음.
        val remaining = ticketRepository.consume(1).getOrElse {
            Log.d(TAG, "no ticket for routine $id, skip")
            return
        }

        cooldownStore.markFired(id)
        notifier.fire(routine, remaining)

        // 티켓 0 도달 → 자동 OFF (E-5, D4). 매니저가 flow 구독으로 지오펜스 해제.
        if (remaining <= 0) {
            routineRepository.setOn(id, false)
        }
    }

    private fun directionMatches(routine: LocationRoutine, transition: Int): Boolean = when (transition) {
        Geofence.GEOFENCE_TRANSITION_ENTER -> routine.direction == AlarmDirection.ENTER
        Geofence.GEOFENCE_TRANSITION_EXIT -> routine.direction == AlarmDirection.EXIT
        else -> false
    }

    private companion object {
        const val TAG = "RoutineGeofence"
    }
}
