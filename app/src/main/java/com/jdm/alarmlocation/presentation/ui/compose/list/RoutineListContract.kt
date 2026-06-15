package com.jdm.alarmlocation.presentation.ui.compose.list

import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.domain.model.TicketState
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

data class RoutineListState(
    val isLoading: Boolean = true,
    val routines: List<LocationRoutine> = emptyList(),
    val ticket: TicketState = TicketState(),
    /** D1 티켓 차감 안내 다이얼로그 대상 루틴 id (null이면 숨김). */
    val pendingTurnOnId: Long? = null,
    /** D2 티켓 부족 바텀시트 표시 여부 (E-4). */
    val showInsufficientTicket: Boolean = false,
) : UiState

sealed interface RoutineListIntent : UiIntent {
    data class Toggle(val id: Long, val turnOn: Boolean) : RoutineListIntent
    /** D1에서 "켜기" 확인. */
    data object ConfirmTurnOn : RoutineListIntent
    data object DismissDialog : RoutineListIntent
    data object CreateRoutine : RoutineListIntent
    data class OpenDetail(val id: Long) : RoutineListIntent
    data object OpenCharge : RoutineListIntent
}

sealed interface RoutineListEffect : UiEffect {
    data object NavigateToCreate : RoutineListEffect
    data class NavigateToDetail(val id: Long) : RoutineListEffect
    data object NavigateToCharge : RoutineListEffect
    data class ShowMessage(val message: String) : RoutineListEffect
}
