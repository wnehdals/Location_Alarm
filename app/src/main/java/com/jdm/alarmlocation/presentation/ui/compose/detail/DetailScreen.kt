package com.jdm.alarmlocation.presentation.ui.compose.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.AppTopBar
import com.jdm.alarmlocation.presentation.ui.compose.components.PrimaryButton
import com.jdm.alarmlocation.presentation.ui.compose.components.SecondaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme
import com.jdm.alarmlocation.presentation.ui.compose.util.label
import com.jdm.alarmlocation.presentation.ui.compose.util.timeSummary
import com.jdm.alarmlocation.presentation.ui.compose.util.toDaysLabel

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is DetailEffect.NavigateToEdit -> onEdit(effect.id)
            DetailEffect.Deleted -> onDeleted()
            is DetailEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(title = "알람 상세", onBack = onBack)
            val routine = state.routine
            when {
                state.isLoading -> LoadingBox()
                routine == null -> NotFoundBox()
                else -> DetailContent(
                    routine = routine,
                    onEdit = { viewModel.onIntent(DetailIntent.Edit) },
                    onDelete = { viewModel.onIntent(DetailIntent.RequestDelete) },
                )
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // 04-5 삭제 확인
    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(DetailIntent.DismissDelete) },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = AppTheme.colors.danger) },
            title = { Text("이 알람을 삭제할까요?", style = AppTheme.typography.title) },
            text = {
                Text(
                    "삭제하면 되돌릴 수 없어요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(DetailIntent.ConfirmDelete) }) {
                    Text("삭제", color = AppTheme.colors.danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(DetailIntent.DismissDelete) }) {
                    Text("취소", color = AppTheme.colors.textSub)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }
}

@Composable
private fun DetailContent(routine: LocationRoutine, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        LocationBanner(routine)
        Spacer(Modifier.height(16.dp))
        InfoCard(routine)
        Spacer(Modifier.weight(1f))
        PrimaryButton(text = "수정하기", onClick = onEdit)
        Spacer(Modifier.height(12.dp))
        SecondaryButton(
            text = "삭제하기",
            onClick = onDelete,
            contentColor = AppTheme.colors.danger,
            borderColor = AppTheme.colors.danger.copy(alpha = 0.5f),
        )
    }
}

/**
 * 위치 배너. 명세상 지도 프리뷰 자리지만 NaverMap은 Compose API가 없어
 * 본 화면(Compose)에서는 위치 요약 배너로 대체한다(라이브 지도 프리뷰는 후속 단계).
 */
@Composable
private fun LocationBanner(routine: LocationRoutine) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(AppTheme.colors.primaryTint, AppTheme.shapes.card),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AppTheme.colors.primary, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            "반경 ${routine.radiusMeters}m · ${routine.direction.label()}",
            style = AppTheme.typography.caption,
            color = AppTheme.colors.primary,
        )
    }
}

@Composable
private fun InfoCard(routine: LocationRoutine) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(routine.title.ifBlank { "선택한 위치" }, style = AppTheme.typography.title, color = AppTheme.colors.text)
            Spacer(Modifier.size(8.dp))
            OnOffPill(isOn = routine.isOn)
        }
        if (routine.address.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(routine.address, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textSub)
        }
        Spacer(Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.colors.border))
        Spacer(Modifier.height(6.dp))
        InfoRow("알람 방식", routine.direction.label())
        InfoRow("반경", "${routine.radiusMeters}m")
        InfoRow("요일", routine.days.toDaysLabel().ifBlank { "-" })
        InfoRow("시간", routine.timeSummary())
        InfoRow("알림 방식", routine.method.label(), last = true)
    }
}

@Composable
private fun OnOffPill(isOn: Boolean) {
    val (label, bg, fg) = if (isOn) {
        Triple("ON", AppTheme.colors.success.copy(alpha = 0.15f), AppTheme.colors.successText)
    } else {
        Triple("OFF", AppTheme.colors.border, AppTheme.colors.textSub)
    }
    Box(
        modifier = Modifier.background(bg, AppTheme.shapes.chip).padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(label, style = AppTheme.typography.captionSmall, color = fg)
    }
}

@Composable
private fun InfoRow(key: String, value: String, last: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(key, style = AppTheme.typography.body, color = AppTheme.colors.textSub, modifier = Modifier.weight(1f))
        Text(value, style = AppTheme.typography.body, color = AppTheme.colors.text)
    }
    if (!last) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.colors.border))
    }
}

@Composable
private fun LoadingBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AppTheme.colors.primary)
    }
}

@Composable
private fun NotFoundBox() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("알람을 찾을 수 없어요.", style = AppTheme.typography.body, color = AppTheme.colors.textSub)
    }
}
