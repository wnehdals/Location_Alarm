package com.jdm.alarmlocation.presentation.ui.compose.util

import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.domain.model.LocationRoutine

/** 0(일)~6(토) 한글 요일 라벨. */
val DAY_LABELS = listOf("일", "월", "화", "수", "목", "금", "토")

/** 분(0~1439) → "08:00". */
fun Int.toHourMinuteLabel(): String {
    val h = (this / 60).coerceIn(0, 23)
    val m = (this % 60).coerceIn(0, 59)
    return "%02d:%02d".format(h, m)
}

/** 선택 요일 집합 → "월·화·수·목·금" 또는 "매일" (Figma 시안 기준, "매주" 접두 없음). */
fun Set<Int>.toDaysLabel(): String {
    if (isEmpty()) return ""
    if (size == 7) return "매일"
    return sorted().joinToString("·") { DAY_LABELS.getOrElse(it) { "" } }
}

fun AlarmDirection.label(): String = when (this) {
    AlarmDirection.ENTER -> "진입"
    AlarmDirection.EXIT -> "이탈"
}

/** 세그먼트/상세용 전체 라벨. */
fun AlarmMethod.label(): String = when (this) {
    AlarmMethod.PUSH -> "앱 푸시"
    AlarmMethod.ALARM -> "알람"
}

/** 목록 카드 칩용 축약 라벨 (Figma: "푸시"/"알람"). */
fun AlarmMethod.cardLabel(): String = when (this) {
    AlarmMethod.PUSH -> "푸시"
    AlarmMethod.ALARM -> "알람"
}

/** 카드 시간 요약: 시간 미설정이면 "시간 미설정 (상시)" (Figma 시안 기준). */
fun LocationRoutine.timeSummary(): String {
    return if (hasTimeRange) {
        "${startMinuteOfDay!!.toHourMinuteLabel()}–${endMinuteOfDay!!.toHourMinuteLabel()}"
    } else {
        "시간 미설정 (상시)"
    }
}
