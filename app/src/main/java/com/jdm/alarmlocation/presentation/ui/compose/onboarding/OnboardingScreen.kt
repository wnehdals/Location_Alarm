package com.jdm.alarmlocation.presentation.ui.compose.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.PrimaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@Composable
fun OnboardingScreen(
    onNavigateToList: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            OnboardingEffect.NavigateToList -> onNavigateToList()
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> viewModel.onIntent(OnboardingIntent.SetNotification(granted)) }

    fun requestNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.onIntent(OnboardingIntent.SetNotification(true))
        }
    }

    val backgroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onIntent(OnboardingIntent.SetBackgroundLocation(granted))
        requestNotification()
    }

    val foregroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onIntent(OnboardingIntent.SetForegroundLocation(granted))
        if (granted) {
            backgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            requestNotification()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(OnboardingIntent.SetForegroundLocation(context.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)))
        viewModel.onIntent(OnboardingIntent.SetBackgroundLocation(context.isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)))
        viewModel.onIntent(OnboardingIntent.SetNotification(context.isNotificationGranted()))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 64.dp)) {
            Text(
                "딱 두 가지 권한이면\n준비 끝이에요",
                style = AppTheme.typography.headline,
                color = AppTheme.colors.text,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "위치와 알림 권한이 있어야 잠든 사이에도\n정확히 깨워드릴 수 있어요.",
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSub,
            )
            Spacer(Modifier.height(32.dp))

            PermissionCard(
                icon = Icons.Filled.LocationOn,
                title = "위치 · 항상 허용",
                description = "버스/지하철에서 자는 동안에도 백그라운드에서 위치를 확인해 정류장 진입을 감지해요.",
                granted = state.foregroundLocationGranted && state.backgroundLocationGranted,
            )
            Spacer(Modifier.height(12.dp))
            PermissionCard(
                icon = Icons.Filled.Notifications,
                title = "알림",
                description = "조건을 충족하면 앱 푸시 또는 알람으로 알려드려요.",
                granted = state.notificationGranted,
            )
            Spacer(Modifier.height(16.dp))
            InfoBox()
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
            PrimaryButton(
                text = "권한 허용하고 시작하기",
                onClick = {
                    foregroundLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "나중에 할게요",
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSub,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onIntent(OnboardingIntent.SkipForNow) }
                    .padding(8.dp),
            )
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(AppTheme.colors.primaryTint, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = AppTheme.colors.primary)
        }
        Spacer(Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = AppTheme.typography.title, color = AppTheme.colors.text)
                Spacer(Modifier.size(8.dp))
                RequiredBadge(granted)
            }
            Spacer(Modifier.height(4.dp))
            Text(description, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textSub)
        }
    }
}

@Composable
private fun RequiredBadge(granted: Boolean) {
    val (label, bg, fg) = if (granted) {
        Triple("허용됨", AppTheme.colors.success.copy(alpha = 0.15f), AppTheme.colors.successText)
    } else {
        Triple("필수", AppTheme.colors.primaryTint, AppTheme.colors.primary)
    }
    Box(
        modifier = Modifier.background(bg, AppTheme.shapes.chip).padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(label, style = AppTheme.typography.captionSmall, color = fg)
    }
}

@Composable
private fun InfoBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.primaryTint, AppTheme.shapes.input)
            .padding(14.dp),
    ) {
        Text(
            "‘앱 사용 중 허용’ → ‘항상 허용’ 2단계로 동의하면 가장 정확하게 동작해요.",
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.primary,
        )
    }
}

private fun Context.isGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Context.isNotificationGranted(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        isGranted(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        true
    }
