package com.jdm.alarmlocation.domain.model

/** 소셜 로그인 제공자 (REQUIREMENTS_SPEC §1). */
enum class AuthProvider { KAKAO, GOOGLE }

/** 인증된 사용자. 정본은 서버이지만 클라이언트 식별/표시용. */
data class AuthUser(
    val id: String,
    val provider: AuthProvider,
    val displayName: String = "",
)
