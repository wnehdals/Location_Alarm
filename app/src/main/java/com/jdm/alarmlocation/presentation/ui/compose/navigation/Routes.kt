package com.jdm.alarmlocation.presentation.ui.compose.navigation

/** Compose 네비게이션 경로. */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"
    /** 목록/충전/마이 공용 하단 네비 탭 호스트. */
    const val MAIN = "main"
    const val CREATE_TIME = "create_time"

    const val DETAIL = "detail"
    const val DETAIL_ARG_ID = "id"
    const val DETAIL_PATTERN = "$DETAIL/{$DETAIL_ARG_ID}"
    fun detail(id: Long) = "$DETAIL/$id"
}
