package com.jdm.alarmlocation.presentation.ui.splash

import com.jdm.core.base.ViewEvent
import com.jdm.core.base.ViewSideEffect
import com.jdm.core.base.ViewState

class SplashContract {
    sealed class SplashViewState: ViewState {
        object Undefine: SplashViewState()
        object Loading: SplashViewState()
        data class Success(
            val version: String
        )
        data class Fail(
            val msg: String
        )
    }
    sealed class SplashSideEffect: ViewSideEffect {
        object MoveMain : SplashSideEffect()
        object ForceVersionUpdate : SplashSideEffect()
        object SelectVersionUpdate : SplashSideEffect()
    }
    sealed class SplashEvent: ViewEvent {

    }
}