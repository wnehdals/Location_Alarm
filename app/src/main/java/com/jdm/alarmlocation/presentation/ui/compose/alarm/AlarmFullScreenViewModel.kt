package com.jdm.alarmlocation.presentation.ui.compose.alarm

import androidx.lifecycle.SavedStateHandle
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 알람 발생 풀스크린 — 순수 표시용.
 * 티켓 차감/자동 OFF 등 부수효과는 [com.jdm.alarmlocation.presentation.service.routine.RoutineGeofenceReceiver]
 * 가 발생 시점에 단일 처리하고, 본 화면은 그 결과(잔량/자동OFF)를 인텐트 extra로 전달받아 렌더링만 한다.
 */
@HiltViewModel
class AlarmFullScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<AlarmFullScreenState, AlarmFullScreenIntent, AlarmFullScreenEffect>(AlarmFullScreenState()) {

    init {
        val directionOrdinal = savedStateHandle.get<Int>(KEY_DIRECTION) ?: 0
        setState {
            copy(
                title = savedStateHandle.get<String>(KEY_TITLE) ?: "",
                direction = AlarmDirection.entries.getOrElse(directionOrdinal) { AlarmDirection.ENTER },
                radiusMeters = savedStateHandle.get<Int>(KEY_RADIUS) ?: 0,
                consumedTicket = true,
                remainingTickets = savedStateHandle.get<Int>(KEY_REMAINING) ?: 0,
                showAutoOff = savedStateHandle.get<Boolean>(KEY_AUTO_OFF) ?: false,
            )
        }
    }

    override fun onIntent(intent: AlarmFullScreenIntent) {
        when (intent) {
            AlarmFullScreenIntent.Dismiss -> sendEffect(AlarmFullScreenEffect.Close)
            AlarmFullScreenIntent.Snooze -> sendEffect(AlarmFullScreenEffect.Close)
            AlarmFullScreenIntent.OpenCharge -> sendEffect(AlarmFullScreenEffect.OpenCharge)
            AlarmFullScreenIntent.DismissAutoOff -> setState { copy(showAutoOff = false) }
        }
    }

    companion object {
        const val KEY_ROUTINE_ID = "routineId"
        const val KEY_TITLE = "title"
        const val KEY_DIRECTION = "direction"
        const val KEY_RADIUS = "radius"
        const val KEY_REMAINING = "remaining"
        const val KEY_AUTO_OFF = "autoOff"
    }
}
