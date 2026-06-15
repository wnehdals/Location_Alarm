package com.jdm.alarmlocation.presentation.ui.compose.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.domain.model.AuthProvider
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.BrandButton
import com.jdm.alarmlocation.presentation.ui.compose.components.SecondaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppPalette
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@Composable
fun LoginScreen(
    onNavigateToOnboarding: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            LoginEffect.NavigateToOnboarding -> onNavigateToOnboarding()
            is LoginEffect.ShowError -> snackbar.showSnackbar(effect.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(AppTheme.colors.primary, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = AppTheme.colors.onPrimary,
                    modifier = Modifier.size(52.dp),
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("위치알람", style = AppTheme.typography.headline, color = AppTheme.colors.text)
            Spacer(Modifier.height(8.dp))
            Text(
                "잠들어도 놓치지 않는 위치 알람",
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSub,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
        ) {
            Text(
                "간편하게 1초 만에 시작하기",
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSub,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            BrandButton(
                text = "카카오로 시작하기",
                container = AppPalette.KakaoYellow,
                content = AppPalette.KakaoLabel,
                onClick = { viewModel.onIntent(LoginIntent.SignIn(AuthProvider.KAKAO)) },
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Google로 시작하기",
                onClick = { viewModel.onIntent(LoginIntent.SignIn(AuthProvider.GOOGLE)) },
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "계속하면 이용약관 및 개인정보처리방침에 동의하게 됩니다",
                style = AppTheme.typography.captionSmall,
                color = AppTheme.colors.textSub,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
