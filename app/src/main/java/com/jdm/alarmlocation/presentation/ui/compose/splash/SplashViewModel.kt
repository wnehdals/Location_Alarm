package com.jdm.alarmlocation.presentation.ui.compose.splash

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.domain.repository.AppConfigRepository
import com.jdm.alarmlocation.domain.repository.AuthRepository
import com.jdm.alarmlocation.domain.repository.VersionStatus
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val authRepository: AuthRepository,
) : MviViewModel<SplashState, SplashIntent, SplashEffect>(SplashState()) {

    init {
        viewModelScope.launch {
            delay(SPLASH_MIN_DURATION_MS)
            when (appConfigRepository.checkVersion(BuildConfig.VERSION_NAME)) {
                VersionStatus.FORCE_UPDATE -> setState { copy(dialog = SplashDialog.FORCE_UPDATE) }
                VersionStatus.OPTIONAL_UPDATE -> setState { copy(dialog = SplashDialog.OPTIONAL_UPDATE) }
                VersionStatus.UP_TO_DATE -> proceed()
            }
        }
    }

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.ConfirmOptional -> {
                setState { copy(dialog = null) }
                viewModelScope.launch { proceed() }
            }

            SplashIntent.ConfirmForceExit -> sendEffect(SplashEffect.ExitApp)
        }
    }

    /** 버전 통과 후 로그인 여부에 따라 진입 지점 결정. */
    private suspend fun proceed() {
        val loggedIn = authRepository.user.first() != null
        sendEffect(if (loggedIn) SplashEffect.NavigateToList else SplashEffect.NavigateToLogin)
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 800L
    }
}
