package com.jdm.alarmlocation.presentation.ui.compose.profile

import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

/** 마이 화면 (Figma 07_마이). 회원 이름 + 앱 버전 + 로그아웃. */
data class ProfileState(
    val userName: String = "",
    val providerLabel: String = "",
    val appVersion: String = "",
) : UiState

sealed interface ProfileIntent : UiIntent {
    data object Logout : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object LoggedOut : ProfileEffect
}
