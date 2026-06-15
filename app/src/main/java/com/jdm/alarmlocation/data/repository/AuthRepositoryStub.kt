package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.domain.model.AuthProvider
import com.jdm.alarmlocation.domain.model.AuthUser
import com.jdm.alarmlocation.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 로컬 스텁 인증 (백엔드/SDK 미연동).
 * Kakao SDK / Firebase Auth + 서버 토큰 검증으로 교체 예정.
 * 현재는 항상 성공하며 인메모리로 로그인 상태를 유지한다.
 */
@Singleton
class AuthRepositoryStub @Inject constructor() : AuthRepository {

    private val _user = MutableStateFlow<AuthUser?>(null)
    override val user = _user.asStateFlow()

    override suspend fun signIn(provider: AuthProvider): Result<AuthUser> {
        delay(SIMULATED_NETWORK_MS)
        val name = when (provider) {
            AuthProvider.KAKAO -> "카카오 사용자"
            AuthProvider.GOOGLE -> "구글 사용자"
        }
        val user = AuthUser(
            id = "${provider.name.lowercase()}-stub-user",
            provider = provider,
            displayName = name,
        )
        _user.value = user
        return Result.success(user)
    }

    override suspend fun signOut() {
        _user.value = null
    }

    private companion object {
        const val SIMULATED_NETWORK_MS = 600L
    }
}
