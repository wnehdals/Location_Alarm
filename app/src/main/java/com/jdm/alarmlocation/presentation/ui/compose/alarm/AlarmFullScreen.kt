package com.jdm.alarmlocation.presentation.ui.compose.alarm

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppPalette
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

private val AlarmBackground = Color(0xFF111726)
private val AlarmSurface = Color(0xFF1F2637)

@Composable
fun AlarmFullScreen(
    onClose: () -> Unit,
    onOpenCharge: () -> Unit,
    viewModel: AlarmFullScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            AlarmFullScreenEffect.Close -> onClose()
            AlarmFullScreenEffect.OpenCharge -> onOpenCharge()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AlarmBackground).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        PulseRing()
        Spacer(Modifier.height(28.dp))

        val chipText = if (state.direction == AlarmDirection.ENTER) "진입 알람" else "이탈 알람"
        Box(
            modifier = Modifier
                .background(AppPalette.Primary.copy(alpha = 0.25f), AppTheme.shapes.chip)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(chipText, style = AppTheme.typography.caption, color = Color.White)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "${state.title.ifBlank { "도착지" }} 도착!",
            style = AppTheme.typography.headline,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        val directionText = if (state.direction == AlarmDirection.ENTER) "진입했어요" else "벗어났어요"
        Text(
            "설정한 ${state.radiusMeters}m 반경에 $directionText",
            style = AppTheme.typography.body,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
        if (state.consumedTicket) {
            Row(
                modifier = Modifier
                    .background(AlarmSurface, AppTheme.shapes.chip)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
                Text(
                    "티켓 1장 차감 · 남은 ${state.remainingTickets}장",
                    style = AppTheme.typography.caption,
                    color = Color.White,
                )
            }
        }

        Spacer(Modifier.weight(1f))
        DismissButton(text = "알람 끄기", onClick = { viewModel.onIntent(AlarmFullScreenIntent.Dismiss) })
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = { viewModel.onIntent(AlarmFullScreenIntent.Snooze) }) {
            Text("5분 뒤 다시 알림", style = AppTheme.typography.body, color = Color.White.copy(alpha = 0.7f))
        }
        Spacer(Modifier.height(8.dp))
    }

    // D4 자동 OFF (E-5)
    if (state.showAutoOff) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(AlarmFullScreenIntent.DismissAutoOff) },
            title = { Text("티켓이 모두 소진되어 알람이 꺼졌어요", style = AppTheme.typography.title) },
            text = {
                Text(
                    "다음에 다시 사용하려면 티켓을 충전한 뒤 알람을 켜주세요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(AlarmFullScreenIntent.OpenCharge) }) {
                    Text("충전하기", color = AppTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(AlarmFullScreenIntent.DismissAutoOff) }) {
                    Text("닫기", color = AppTheme.colors.textSub)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }
}

@Composable
private fun PulseRing() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "scale",
    )
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(scale)
                .background(AppPalette.Primary.copy(alpha = 0.15f), CircleShape),
        )
        Box(
            modifier = Modifier.size(96.dp).background(AppPalette.Primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun DismissButton(text: String, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = AppTheme.shapes.button,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = AlarmBackground,
        ),
    ) {
        Text(text, style = AppTheme.typography.button)
    }
}
