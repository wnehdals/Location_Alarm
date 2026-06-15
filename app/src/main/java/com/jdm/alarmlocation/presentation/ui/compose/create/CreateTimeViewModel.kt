package com.jdm.alarmlocation.presentation.ui.compose.create

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.data.draft.RoutineDraftStore
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTimeViewModel @Inject constructor(
    private val draftStore: RoutineDraftStore,
    private val routineRepository: RoutineRepository,
) : MviViewModel<CreateTimeState, CreateTimeIntent, CreateTimeEffect>(CreateTimeState()) {

    init {
        val draft = draftStore.draft
        setState {
            copy(
                title = draft.title,
                address = draft.address,
                days = draft.days,
                timeRangeEnabled = draft.hasTimeRange,
                startMinute = draft.startMinuteOfDay ?: (8 * 60),
                endMinute = draft.endMinuteOfDay ?: (9 * 60 + 30),
                method = draft.method,
                isEdit = draft.id != 0L,
            )
        }
    }

    override fun onIntent(intent: CreateTimeIntent) {
        when (intent) {
            is CreateTimeIntent.ToggleDay -> toggleDay(intent.day)
            is CreateTimeIntent.SetTimeRangeEnabled -> setState { copy(timeRangeEnabled = intent.enabled) }
            is CreateTimeIntent.OpenTimePicker -> setState { copy(editingTimeField = intent.field) }
            is CreateTimeIntent.SetTime -> setTime(intent)
            CreateTimeIntent.DismissTimePicker -> setState { copy(editingTimeField = null) }
            is CreateTimeIntent.SetMethod -> setState { copy(method = intent.method) }
            CreateTimeIntent.DismissTimeError -> setState { copy(showTimeError = false) }
            CreateTimeIntent.Save -> save()
        }
    }

    private fun toggleDay(day: Int) {
        setState {
            val next = if (days.contains(day)) days - day else days + day
            copy(days = next)
        }
    }

    private fun setTime(intent: CreateTimeIntent.SetTime) {
        val minutes = intent.hour * 60 + intent.minute
        setState {
            when (intent.field) {
                TimeField.START -> copy(startMinute = minutes, editingTimeField = null)
                TimeField.END -> copy(endMinute = minutes, editingTimeField = null)
            }
        }
    }

    private fun save() {
        val s = currentState
        if (s.days.isEmpty()) {
            sendEffect(CreateTimeEffect.ShowMessage("요일을 한 개 이상 선택해주세요."))
            return
        }
        if (s.timeRangeEnabled) {
            // E-10: 시작=종료(범위 0) → 미동작 → 검증 안내(D8).
            if (s.startMinute == s.endMinute) {
                setState { copy(showTimeError = true) }
                return
            }
            // SPEC §4-2: 시작 ≤ 종료.
            if (s.startMinute > s.endMinute) {
                sendEffect(CreateTimeEffect.ShowMessage("종료 시간은 시작 시간보다 빠를 수 없어요."))
                return
            }
        }

        setState { copy(isSaving = true) }
        val routine = draftStore.draft.copy(
            days = s.days,
            startMinuteOfDay = if (s.timeRangeEnabled) s.startMinute else null,
            endMinuteOfDay = if (s.timeRangeEnabled) s.endMinute else null,
            method = s.method,
        )
        viewModelScope.launch {
            routineRepository.upsert(routine)
                .onSuccess {
                    draftStore.clear()
                    sendEffect(CreateTimeEffect.Saved)
                }
                .onFailure {
                    setState { copy(isSaving = false) }
                    sendEffect(CreateTimeEffect.ShowMessage("저장하지 못했어요. 다시 시도해주세요."))
                }
        }
    }
}
