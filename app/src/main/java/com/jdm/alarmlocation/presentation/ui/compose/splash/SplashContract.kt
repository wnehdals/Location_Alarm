package com.jdm.alarmlocation.presentation.ui.compose.splash

import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

enum class SplashDialog { FORCE_UPDATE, OPTIONAL_UPDATE }

data class SplashState(
    val dialog: SplashDialog? = null,
) : UiState

sealed interface SplashIntent : UiIntent {
    /** 선택 업데이트 안내에서 "다음에 하기". */
    data object ConfirmOptional : SplashIntent
    /** 강제 업데이트에서 "앱 종료". */
    data object ConfirmForceExit : SplashIntent
}

sealed interface SplashEffect : UiEffect {
    data object NavigateToLogin : SplashEffect
    data object NavigateToList : SplashEffect
    data object ExitApp : SplashEffect
}
