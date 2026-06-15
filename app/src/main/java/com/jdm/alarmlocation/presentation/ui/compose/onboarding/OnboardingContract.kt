package com.jdm.alarmlocation.presentation.ui.compose.onboarding

import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

/**
 * 권한 온보딩 상태 (REQUIREMENTS_SPEC §2).
 * 위치(앱 사용 중)→ 위치(항상 허용)→ 알림 2~3단계 동의를 추적.
 */
data class OnboardingState(
    val foregroundLocationGranted: Boolean = false,
    val backgroundLocationGranted: Boolean = false,
    val notificationGranted: Boolean = false,
) : UiState

sealed interface OnboardingIntent : UiIntent {
    data class SetForegroundLocation(val granted: Boolean) : OnboardingIntent
    data class SetBackgroundLocation(val granted: Boolean) : OnboardingIntent
    data class SetNotification(val granted: Boolean) : OnboardingIntent
    data object Continue : OnboardingIntent
    data object SkipForNow : OnboardingIntent
}

sealed interface OnboardingEffect : UiEffect {
    data object NavigateToList : OnboardingEffect
}
