package com.jdm.alarmlocation.presentation.ui.compose.onboarding

import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() :
    MviViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(OnboardingState()) {

    override fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SetForegroundLocation ->
                setState { copy(foregroundLocationGranted = intent.granted) }

            is OnboardingIntent.SetBackgroundLocation ->
                setState { copy(backgroundLocationGranted = intent.granted) }

            is OnboardingIntent.SetNotification ->
                setState { copy(notificationGranted = intent.granted) }

            // 권한 거부 상태로도 진행 가능(목록에서 권한 부족 배지로 안내) — SPEC §2 / E-1.
            OnboardingIntent.Continue,
            OnboardingIntent.SkipForNow -> sendEffect(OnboardingEffect.NavigateToList)
        }
    }
}
