package com.jdm.alarmlocation.presentation.ui.compose.profile

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.domain.model.AuthProvider
import com.jdm.alarmlocation.domain.repository.AuthRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(
    ProfileState(appVersion = BuildConfig.VERSION_NAME),
) {

    init {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                setState {
                    copy(
                        userName = user?.displayName ?: "게스트",
                        providerLabel = when (user?.provider) {
                            AuthProvider.KAKAO -> "카카오 로그인"
                            AuthProvider.GOOGLE -> "구글 로그인"
                            null -> ""
                        },
                    )
                }
            }
        }
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            sendEffect(ProfileEffect.LoggedOut)
        }
    }
}
