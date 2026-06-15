package com.jdm.alarmlocation.presentation.ui.compose.detail

import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

/** 알람 상세 (SPEC §3 — 별도 화면). 수정/삭제 진입. */
data class DetailState(
    val routine: LocationRoutine? = null,
    val isLoading: Boolean = true,
    /** 04-5 삭제 확인 다이얼로그. */
    val showDeleteConfirm: Boolean = false,
    val isDeleting: Boolean = false,
) : UiState

sealed interface DetailIntent : UiIntent {
    data object Edit : DetailIntent
    data object RequestDelete : DetailIntent
    data object ConfirmDelete : DetailIntent
    data object DismissDelete : DetailIntent
}

sealed interface DetailEffect : UiEffect {
    data class NavigateToEdit(val id: Long) : DetailEffect
    data object Deleted : DetailEffect
    data class ShowMessage(val message: String) : DetailEffect
}
