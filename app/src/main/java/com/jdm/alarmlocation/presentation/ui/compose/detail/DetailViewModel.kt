package com.jdm.alarmlocation.presentation.ui.compose.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import com.jdm.alarmlocation.presentation.ui.compose.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routineRepository: RoutineRepository,
) : MviViewModel<DetailState, DetailIntent, DetailEffect>(DetailState()) {

    private val routineId: Long = savedStateHandle.get<Long>(Routes.DETAIL_ARG_ID) ?: -1L

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val routine = routineRepository.get(routineId)
            setState { copy(routine = routine, isLoading = false) }
        }
    }

    override fun onIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Edit -> sendEffect(DetailEffect.NavigateToEdit(routineId))
            DetailIntent.RequestDelete -> setState { copy(showDeleteConfirm = true) }
            DetailIntent.DismissDelete -> setState { copy(showDeleteConfirm = false) }
            DetailIntent.ConfirmDelete -> delete()
        }
    }

    private fun delete() {
        setState { copy(isDeleting = true, showDeleteConfirm = false) }
        viewModelScope.launch {
            routineRepository.delete(routineId)
                .onSuccess { sendEffect(DetailEffect.Deleted) }
                .onFailure {
                    setState { copy(isDeleting = false) }
                    sendEffect(DetailEffect.ShowMessage("삭제하지 못했어요."))
                }
        }
    }
}
