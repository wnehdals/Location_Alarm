package com.jdm.alarmlocation.presentation.ui.compose.login

import com.jdm.alarmlocation.domain.model.AuthProvider
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

data class LoginState(
    val isLoading: Boolean = false,
) : UiState

sealed interface LoginIntent : UiIntent {
    data class SignIn(val provider: AuthProvider) : LoginIntent
}

sealed interface LoginEffect : UiEffect {
    data object NavigateToOnboarding : LoginEffect
    /** D7 로그인 실패 (E-9). */
    data class ShowError(val message: String) : LoginEffect
}
