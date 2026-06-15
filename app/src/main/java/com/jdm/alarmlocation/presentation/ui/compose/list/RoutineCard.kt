package com.jdm.alarmlocation.presentation.ui.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme
import com.jdm.alarmlocation.presentation.ui.compose.util.cardLabel
import com.jdm.alarmlocation.presentation.ui.compose.util.label
import com.jdm.alarmlocation.presentation.ui.compose.util.timeSummary
import com.jdm.alarmlocation.presentation.ui.compose.util.toDaysLabel

@Composable
fun RoutineCard(
    routine: LocationRoutine,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPermissionWarning: Boolean = false,
) {
    val contentAlpha = if (routine.isOn) 1f else 0.5f
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    routine.title.ifBlank { "이름 없는 알람" },
                    style = AppTheme.typography.title,
                    color = AppTheme.colors.text.copy(alpha = contentAlpha),
                )
                if (routine.address.isNotBlank()) {
                    Spacer(Modifier.size(2.dp))
                    Text(
                        routine.address,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSub.copy(alpha = contentAlpha),
                    )
                }
            }
            Switch(
                checked = routine.isOn,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppTheme.colors.success,
                    uncheckedTrackColor = AppTheme.colors.border,
                ),
            )
        }

        Spacer(Modifier.size(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DirectionChip(routine.direction, contentAlpha)
            InfoChip("${routine.radiusMeters}m", contentAlpha)
            MethodChip(routine.method, contentAlpha)
        }

        Spacer(Modifier.size(10.dp))
        Text(
            buildString {
                append(routine.days.toDaysLabel())
                append(" · ")
                append(routine.timeSummary())
            },
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSub.copy(alpha = contentAlpha),
        )

        if (showPermissionWarning) {
            Spacer(Modifier.size(12.dp))
            PermissionWarnStrip()
        }
    }
}

/** E-1: 백그라운드 위치 미허용 경고 스트립. */
@Composable
private fun PermissionWarnStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.warning.copy(alpha = 0.12f), AppTheme.shapes.chip)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = AppTheme.colors.warningText,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.size(6.dp))
        Text(
            "위치 ‘항상 허용’ 필요 · 잠든 사이 안 울릴 수 있어요",
            style = AppTheme.typography.captionSmall,
            color = AppTheme.colors.warningText,
        )
    }
}

@Composable
private fun DirectionChip(direction: AlarmDirection, alpha: Float) {
    val color = when (direction) {
        AlarmDirection.ENTER -> AppTheme.colors.successText
        AlarmDirection.EXIT -> AppTheme.colors.warningText
    }
    val bg = when (direction) {
        AlarmDirection.ENTER -> AppTheme.colors.success.copy(alpha = 0.14f)
        AlarmDirection.EXIT -> AppTheme.colors.warning.copy(alpha = 0.16f)
    }
    Box(
        modifier = Modifier.background(bg.copy(alpha = bg.alpha * alpha), AppTheme.shapes.chip)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(direction.label(), style = AppTheme.typography.caption, color = color.copy(alpha = alpha))
    }
}

@Composable
private fun MethodChip(method: AlarmMethod, alpha: Float) {
    val icon = when (method) {
        AlarmMethod.ALARM -> Icons.Filled.Alarm
        AlarmMethod.PUSH -> Icons.Filled.Notifications
    }
    Row(
        modifier = Modifier
            .background(AppTheme.colors.background, AppTheme.shapes.chip)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AppTheme.colors.textSub.copy(alpha = alpha),
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.size(4.dp))
        Text(method.cardLabel(), style = AppTheme.typography.caption, color = AppTheme.colors.textSub.copy(alpha = alpha))
    }
}

@Composable
private fun InfoChip(text: String, alpha: Float) {
    Box(
        modifier = Modifier.background(AppTheme.colors.background, AppTheme.shapes.chip)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text, style = AppTheme.typography.caption, color = AppTheme.colors.textSub.copy(alpha = alpha))
    }
}
