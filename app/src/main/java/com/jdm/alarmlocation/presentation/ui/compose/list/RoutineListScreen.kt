package com.jdm.alarmlocation.presentation.ui.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.PrimaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCharge: () -> Unit,
    viewModel: RoutineListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            RoutineListEffect.NavigateToCreate -> onNavigateToCreate()
            is RoutineListEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
            RoutineListEffect.NavigateToCharge -> onNavigateToCharge()
            is RoutineListEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
        }
    }

    val context = LocalContext.current
    val backgroundGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ListHeader(
                ticketCount = state.ticket.balance,
                onTicketClick = { viewModel.onIntent(RoutineListIntent.OpenCharge) },
            )
            Box(modifier = Modifier.weight(1f)) {
                if (state.routines.isEmpty() && !state.isLoading) {
                    EmptyState(onCreate = { viewModel.onIntent(RoutineListIntent.CreateRoutine) })
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.routines, key = { it.id }) { routine ->
                            RoutineCard(
                                routine = routine,
                                onToggle = { on -> viewModel.onIntent(RoutineListIntent.Toggle(routine.id, on)) },
                                onClick = { viewModel.onIntent(RoutineListIntent.OpenDetail(routine.id)) },
                                showPermissionWarning = routine.isOn && !backgroundGranted,
                            )
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { viewModel.onIntent(RoutineListIntent.CreateRoutine) },
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "위치 알람 추가")
                }
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // D1 — 티켓 차감 안내 (SPEC §6: 안내만, 차감 없음)
    if (state.pendingTurnOnId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(RoutineListIntent.DismissDialog) },
            title = { Text("알람을 켤까요?", style = AppTheme.typography.title) },
            text = {
                Text(
                    "알람이 1회 발생할 때마다 티켓 1장이 차감돼요. " +
                        "지금 켜는 시점에는 차감되지 않아요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(RoutineListIntent.ConfirmTurnOn) }) {
                    Text("켜기", color = AppTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(RoutineListIntent.DismissDialog) }) {
                    Text("취소", color = AppTheme.colors.textSub)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }

    // D2 — 티켓 부족 (E-4)
    if (state.showInsufficientTicket) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(RoutineListIntent.DismissDialog) },
            containerColor = AppTheme.colors.surface,
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text("티켓이 부족해 켤 수 없어요", style = AppTheme.typography.titleLarge, color = AppTheme.colors.text)
                Spacer(Modifier.height(8.dp))
                Text(
                    "알람을 켜려면 티켓이 1장 이상 필요해요. 충전 후 다시 시도해주세요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
                Spacer(Modifier.height(20.dp))
                PrimaryButton(
                    text = "충전하러 가기",
                    onClick = {
                        viewModel.onIntent(RoutineListIntent.DismissDialog)
                        viewModel.onIntent(RoutineListIntent.OpenCharge)
                    },
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ListHeader(ticketCount: Int, onTicketClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("내 위치알람", style = AppTheme.typography.headline, color = AppTheme.colors.text, modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .background(AppTheme.colors.primaryTint, AppTheme.shapes.chip)
                .clickable(onClick = onTicketClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Filled.ConfirmationNumber, contentDescription = "티켓 충전", tint = AppTheme.colors.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(4.dp))
            Text("${ticketCount}장", style = AppTheme.typography.caption, color = AppTheme.colors.primary)
        }
    }
}

@Composable
private fun EmptyState(onCreate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("아직 등록한 위치 알람이 없어요", style = AppTheme.typography.title, color = AppTheme.colors.text)
        Spacer(Modifier.height(8.dp))
        Text(
            "+ 버튼을 눌러 첫 위치 알람을 만들어보세요.",
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSub,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = "위치 알람 만들기", onClick = onCreate, modifier = Modifier.fillMaxWidth(0.7f))
    }
}
