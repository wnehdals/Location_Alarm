package com.jdm.alarmlocation.presentation.ui.compose.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.SecondaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfileEffect.LoggedOut -> onLoggedOut()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Text(
            "마이",
            style = AppTheme.typography.headline,
            color = AppTheme.colors.text,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp),
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileCard(name = state.userName, provider = state.providerLabel)
            Spacer(Modifier.height(24.dp))
            SectionLabel("계정 정보")
            Spacer(Modifier.height(12.dp))
            InfoCard(userName = state.userName, appVersion = state.appVersion)
            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = "로그아웃",
                onClick = { viewModel.onIntent(ProfileIntent.Logout) },
                contentColor = AppTheme.colors.danger,
                borderColor = AppTheme.colors.danger.copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun ProfileCard(name: String, provider: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(AppTheme.colors.primaryTint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                name.take(1).ifBlank { "?" },
                style = AppTheme.typography.title,
                color = AppTheme.colors.primary,
            )
        }
        Spacer(Modifier.size(14.dp))
        Column {
            Text(name.ifBlank { "게스트" }, style = AppTheme.typography.title, color = AppTheme.colors.text)
            if (provider.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(provider, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textSub)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = AppTheme.typography.title, color = AppTheme.colors.text)
}

@Composable
private fun InfoCard(userName: String, appVersion: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(horizontal = 16.dp),
    ) {
        InfoRow("회원 이름", userName.ifBlank { "게스트" })
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.colors.border))
        InfoRow("앱 버전", appVersion)
    }
}

@Composable
private fun InfoRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(key, style = AppTheme.typography.body, color = AppTheme.colors.textSub, modifier = Modifier.weight(1f))
        Text(value, style = AppTheme.typography.body, color = AppTheme.colors.text)
    }
}
