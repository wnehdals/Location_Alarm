package com.jdm.alarmlocation.presentation.ui.compose.charge

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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Movie
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.domain.model.TicketState
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.AppTopBar
import com.jdm.alarmlocation.presentation.ui.compose.components.PrimaryButton
import com.jdm.alarmlocation.presentation.ui.compose.components.SecondaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

@Composable
fun ChargeScreen(
    onBack: (() -> Unit)? = null,
    viewModel: ChargeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is ChargeEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(title = "티켓 충전", onBack = onBack)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(8.dp))
                HeroTicketCard(balance = state.ticket.balance)

                Spacer(Modifier.height(20.dp))
                ChargeOptionCard(
                    icon = Icons.Filled.Movie,
                    title = "광고 보고 티켓 받기",
                    subtitle = "시청 완료 시 +1장 · 오늘 ${state.ticket.todayAdRewardCount}/${TicketState.DAILY_AD_REWARD_LIMIT}",
                ) {
                    SecondaryButton(
                        text = "받기",
                        onClick = { viewModel.onIntent(ChargeIntent.WatchAd) },
                        enabled = !state.isAdLoading && state.ticket.canWatchAd,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(12.dp))
                ChargeOptionCard(
                    icon = Icons.Filled.ConfirmationNumber,
                    title = "티켓 ${TicketState.PURCHASE_AMOUNT}장",
                    subtitle = "₩${"%,d".format(TicketState.PURCHASE_PRICE_KRW)}",
                ) {
                    PrimaryButton(
                        text = "구매하기",
                        onClick = { viewModel.onIntent(ChargeIntent.Purchase) },
                        loading = state.isPurchaseLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(20.dp))
                PolicyNote()
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // D5 — 광고 한도 (E-6)
    if (state.showAdLimit) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ChargeIntent.DismissDialog) },
            title = { Text("오늘 광고 보상을 모두 받았어요", style = AppTheme.typography.title) },
            text = {
                Text(
                    "광고로 받을 수 있는 티켓은 하루 ${TicketState.DAILY_AD_REWARD_LIMIT}장이에요. 자정에 초기화돼요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ChargeIntent.DismissDialog) }) {
                    Text("확인", color = AppTheme.colors.primary)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }

    // D6 — 결제 확인 중 (E-8)
    if (state.showPurchaseVerifying) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("결제 확인 중", style = AppTheme.typography.title) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AppTheme.colors.primary)
                    Spacer(Modifier.size(12.dp))
                    Text("결제를 확인하고 있어요. 잠시만 기다려주세요.", style = AppTheme.typography.body, color = AppTheme.colors.textSub)
                }
            },
            confirmButton = {},
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }
}

@Composable
private fun HeroTicketCard(balance: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.primary, AppTheme.shapes.card)
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = AppTheme.colors.onPrimary)
            Spacer(Modifier.size(8.dp))
            Text("보유 티켓", style = AppTheme.typography.body, color = AppTheme.colors.onPrimary)
        }
        Spacer(Modifier.height(8.dp))
        Text("${balance}장", style = AppTheme.typography.headline, color = AppTheme.colors.onPrimary)
    }
}

@Composable
private fun ChargeOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(AppTheme.colors.primaryTint, AppTheme.shapes.input),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = AppTheme.colors.primary)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = AppTheme.typography.title, color = AppTheme.colors.text)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textSub)
            }
        }
        Spacer(Modifier.height(12.dp))
        action()
    }
}

@Composable
private fun PolicyNote() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.primaryTint, AppTheme.shapes.input)
            .padding(14.dp),
    ) {
        Text(
            "광고 보상은 하루 최대 ${TicketState.DAILY_AD_REWARD_LIMIT}장(자정 초기화)이에요. " +
                "결제는 서버 검증 후 지급되며, 알람이 1회 발생할 때마다 티켓 1장이 차감돼요.",
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.primary,
        )
    }
}
