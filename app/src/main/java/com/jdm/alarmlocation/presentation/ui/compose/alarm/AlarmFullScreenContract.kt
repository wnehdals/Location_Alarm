package com.jdm.alarmlocation.presentation.ui.compose.alarm

import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

/** 알람 발생 풀스크린 (SPEC §5, §6). 발생당 티켓 1 차감, 0 도달 시 자동 OFF(D4). */
data class AlarmFullScreenState(
    val title: String = "",
    val direction: AlarmDirection = AlarmDirection.ENTER,
    val radiusMeters: Int = 0,
    val consumedTicket: Boolean = false,
    val remainingTickets: Int = 0,
    /** D4 자동 OFF (E-5). */
    val showAutoOff: Boolean = false,
) : UiState

sealed interface AlarmFullScreenIntent : UiIntent {
    data object Dismiss : AlarmFullScreenIntent
    data object Snooze : AlarmFullScreenIntent
    data object OpenCharge : AlarmFullScreenIntent
    data object DismissAutoOff : AlarmFullScreenIntent
}

sealed interface AlarmFullScreenEffect : UiEffect {
    data object Close : AlarmFullScreenEffect
    data object OpenCharge : AlarmFullScreenEffect
}
