package com.jdm.alarmlocation.presentation.ui.compose.login

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.domain.repository.AuthRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : MviViewModel<LoginState, LoginIntent, LoginEffect>(LoginState()) {

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignIn -> signIn(intent)
        }
    }

    private fun signIn(intent: LoginIntent.SignIn) {
        if (currentState.isLoading) return
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.signIn(intent.provider)
                .onSuccess {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToOnboarding)
                }
                .onFailure {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.ShowError("로그인에 실패했어요. 다시 시도해주세요."))
                }
        }
    }
}
