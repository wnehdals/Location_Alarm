package com.jdm.alarmlocation.presentation.ui.compose.create

import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

enum class TimeField { START, END }

/**
 * 알람 생성 2/2 — 요일·시간·알림 수단 (SPEC §4-2, §4-3).
 * 위치 정보(title/address/...)는 [com.jdm.alarmlocation.data.draft.RoutineDraftStore] 초안에서 가져온다.
 */
data class CreateTimeState(
    val title: String = "",
    val address: String = "",
    val days: Set<Int> = emptySet(),
    val timeRangeEnabled: Boolean = false,
    val startMinute: Int = 8 * 60,
    val endMinute: Int = 9 * 60 + 30,
    val method: AlarmMethod = AlarmMethod.PUSH,
    val isSaving: Boolean = false,
    val editingTimeField: TimeField? = null,
    /** D8 시간 검증 다이얼로그 (시작=종료, E-10). */
    val showTimeError: Boolean = false,
    val isEdit: Boolean = false,
) : UiState {
    val canSave: Boolean get() = days.isNotEmpty() && !isSaving
}

sealed interface CreateTimeIntent : UiIntent {
    data class ToggleDay(val day: Int) : CreateTimeIntent
    data class SetTimeRangeEnabled(val enabled: Boolean) : CreateTimeIntent
    data class OpenTimePicker(val field: TimeField) : CreateTimeIntent
    data class SetTime(val field: TimeField, val hour: Int, val minute: Int) : CreateTimeIntent
    data object DismissTimePicker : CreateTimeIntent
    data class SetMethod(val method: AlarmMethod) : CreateTimeIntent
    data object DismissTimeError : CreateTimeIntent
    data object Save : CreateTimeIntent
}

sealed interface CreateTimeEffect : UiEffect {
    data object Saved : CreateTimeEffect
    data class ShowMessage(val message: String) : CreateTimeEffect
}
