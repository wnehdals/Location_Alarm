package com.jdm.alarmlocation.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** 알람 방식: 원형 범위 진입(enter) / 이탈(exit). DEFAULT = ENTER. */
enum class AlarmDirection { ENTER, EXIT }

/** 알림 수단: 앱 푸시(NotificationManager) / 알람(AlarmManager). DEFAULT = PUSH. */
enum class AlarmMethod { PUSH, ALARM }

/**
 * 위치 알람 루틴 — 목록의 1 아이템 단위 (REQUIREMENTS_SPEC §3 확정사항).
 *
 * 반경: 50~500m, 50m 단위 10단계 (기본 50).
 * 요일: [days] = 0(일)~6(토) 다중 선택, 필수.
 * 시간: [startMinuteOfDay]/[endMinuteOfDay] = 자정 기준 분(0~1439). null이면 시간 제약 없음(상시).
 */
@Parcelize
data class LocationRoutine(
    val id: Long = 0,
    val title: String = "",
    val address: String = "",
    val latitude: Double = 37.5666102,
    val longitude: Double = 126.9783881,
    val radiusMeters: Int = DEFAULT_RADIUS,
    val direction: AlarmDirection = AlarmDirection.ENTER,
    val days: Set<Int> = emptySet(),
    val startMinuteOfDay: Int? = null,
    val endMinuteOfDay: Int? = null,
    val method: AlarmMethod = AlarmMethod.PUSH,
    val isOn: Boolean = false,
    val createdAt: Long = 0L,
) : Parcelable {

    val hasTimeRange: Boolean get() = startMinuteOfDay != null && endMinuteOfDay != null

    companion object {
        const val MIN_RADIUS = 50
        const val MAX_RADIUS = 500
        const val RADIUS_STEP = 50
        const val DEFAULT_RADIUS = 50
    }
}
