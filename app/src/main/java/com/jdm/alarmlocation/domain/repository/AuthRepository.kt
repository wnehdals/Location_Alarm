package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.AuthProvider
import com.jdm.alarmlocation.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * 인증 저장소 (REQUIREMENTS_SPEC §1).
 * 정본은 서버이나 본 인터페이스는 클라이언트 관점. 구현은 추후 Kakao SDK / Firebase Auth + 서버 검증으로 교체.
 */
interface AuthRepository {
    val user: Flow<AuthUser?>
    suspend fun signIn(provider: AuthProvider): Result<AuthUser>
    suspend fun signOut()
}
