package com.jdm.alarmlocation.presentation.service.routine

import android.content.Context
import com.jdm.alarmlocation.domain.model.LocationRoutine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 중복 발생 방지 쿨다운 (REQUIREMENTS_SPEC §5).
 * - 시간 범위 미설정: 같은 알람 하루 1회(24시간).
 * - 시간 범위 설정: 해당 범위 내 1회만(같은 날 1회).
 */
@Singleton
class AlarmCooldownStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun shouldFire(routine: LocationRoutine, now: Long = System.currentTimeMillis()): Boolean {
        val last = prefs.getLong(key(routine.id), 0L)
        return if (routine.hasTimeRange) {
            // 같은 날 같은 범위에서는 재발생 안 함.
            !ScheduleUtil.isSameDay(last, now)
        } else {
            now - last >= DAY_MILLIS
        }
    }

    fun markFired(routineId: Long, now: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(key(routineId), now).apply()
    }

    private fun key(id: Long) = "fire_$id"

    private companion object {
        const val PREFS = "routine_alarm_cooldown"
        const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}
