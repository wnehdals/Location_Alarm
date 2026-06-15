package com.jdm.alarmlocation.presentation.ui.compose.list

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.domain.repository.TicketRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineListViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val ticketRepository: TicketRepository,
) : MviViewModel<RoutineListState, RoutineListIntent, RoutineListEffect>(RoutineListState()) {

    init {
        viewModelScope.launch {
            combine(
                routineRepository.routines,
                ticketRepository.ticketState,
            ) { routines, ticket -> routines to ticket }
                .collect { (routines, ticket) ->
                    setState { copy(isLoading = false, routines = routines, ticket = ticket) }
                }
        }
    }

    override fun onIntent(intent: RoutineListIntent) {
        when (intent) {
            is RoutineListIntent.Toggle -> onToggle(intent)
            RoutineListIntent.ConfirmTurnOn -> confirmTurnOn()
            RoutineListIntent.DismissDialog ->
                setState { copy(pendingTurnOnId = null, showInsufficientTicket = false) }
            RoutineListIntent.CreateRoutine -> sendEffect(RoutineListEffect.NavigateToCreate)
            is RoutineListIntent.OpenDetail -> sendEffect(RoutineListEffect.NavigateToDetail(intent.id))
            RoutineListIntent.OpenCharge -> sendEffect(RoutineListEffect.NavigateToCharge)
        }
    }

    private fun onToggle(intent: RoutineListIntent.Toggle) {
        if (!intent.turnOn) {
            setOn(intent.id, false)
            return
        }
        // ON 전환: 티켓 1개 이상 필요 (SPEC §6 / E-4).
        if (currentState.ticket.balance < 1) {
            setState { copy(showInsufficientTicket = true) }
            return
        }
        // D1 티켓 차감 안내(안내만, 이 시점 차감 없음).
        setState { copy(pendingTurnOnId = intent.id) }
    }

    private fun confirmTurnOn() {
        val id = currentState.pendingTurnOnId ?: return
        setState { copy(pendingTurnOnId = null) }
        setOn(id, true)
    }

    private fun setOn(id: Long, isOn: Boolean) {
        viewModelScope.launch {
            routineRepository.setOn(id, isOn)
                .onFailure { sendEffect(RoutineListEffect.ShowMessage("상태를 변경하지 못했어요.")) }
        }
    }
}
