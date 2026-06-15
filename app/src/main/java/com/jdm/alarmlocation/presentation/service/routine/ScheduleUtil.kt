package com.jdm.alarmlocation.presentation.service.routine

import com.jdm.alarmlocation.domain.model.LocationRoutine
import java.util.Calendar

/** 지오펜스 스케줄 판정 유틸 (요일/시간/날짜). 요일 인덱스: 0(일)~6(토). */
object ScheduleUtil {

    /** 오늘 요일 인덱스 0(일)~6(토). */
    fun todayDayIndex(now: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        return cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar: 1=Sun..7=Sat
    }

    fun nowMinuteOfDay(now: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    /** 오늘이 루틴 동작 요일인지. */
    fun isScheduledToday(routine: LocationRoutine, now: Long = System.currentTimeMillis()): Boolean =
        routine.days.contains(todayDayIndex(now))

    /**
     * 시간 조건 충족 여부.
     * 시간 미설정이면 상시(true). 설정 시 [start, end) 범위 내에서만 true.
     * 시작=종료(범위 0)는 미동작(E-10).
     */
    fun isWithinTime(routine: LocationRoutine, now: Long = System.currentTimeMillis()): Boolean {
        val start = routine.startMinuteOfDay ?: return true
        val end = routine.endMinuteOfDay ?: return true
        if (start == end) return false
        val minute = nowMinuteOfDay(now)
        return minute in start until end
    }

    fun isSameDay(a: Long, b: Long): Boolean {
        if (a == 0L || b == 0L) return false
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
