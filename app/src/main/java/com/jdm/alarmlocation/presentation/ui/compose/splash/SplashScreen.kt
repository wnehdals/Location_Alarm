package com.jdm.alarmlocation.presentation.ui.compose.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToList: () -> Unit,
    onExit: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            SplashEffect.NavigateToLogin -> onNavigateToLogin()
            SplashEffect.NavigateToList -> onNavigateToList()
            SplashEffect.ExitApp -> onExit()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AppTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(AppTheme.colors.onPrimary, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(52.dp),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text("위치알람", style = AppTheme.typography.titleLarge, color = AppTheme.colors.onPrimary)
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = AppTheme.colors.onPrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        }
    }

    when (state.dialog) {
        SplashDialog.FORCE_UPDATE -> AlertDialog(
            onDismissRequest = {},
            title = { Text("업데이트 안내", style = AppTheme.typography.title) },
            text = {
                Text(
                    "원활한 사용을 위해 최신 버전으로 업데이트가 필요해요. 스토어에서 업데이트 후 다시 실행해주세요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(SplashIntent.ConfirmForceExit) }) {
                    Text("앱 종료", color = AppTheme.colors.danger)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )

        SplashDialog.OPTIONAL_UPDATE -> AlertDialog(
            onDismissRequest = {},
            title = { Text("업데이트 안내", style = AppTheme.typography.title) },
            text = {
                Text(
                    "새로운 버전이 있어요. 더 나은 사용을 위해 업데이트를 권장해요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(SplashIntent.ConfirmOptional) }) {
                    Text("다음에 하기", color = AppTheme.colors.primary)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )

        null -> Unit
    }
}
