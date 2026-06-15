package com.jdm.alarmlocation.presentation.ui.compose.charge

import com.jdm.alarmlocation.domain.model.TicketState
import com.jdm.alarmlocation.presentation.ui.compose.base.UiEffect
import com.jdm.alarmlocation.presentation.ui.compose.base.UiIntent
import com.jdm.alarmlocation.presentation.ui.compose.base.UiState

/** 티켓 충전 (SPEC §7). 광고 보상(하루 3개) + 인앱 결제(10개/2,000원). */
data class ChargeState(
    val ticket: TicketState = TicketState(),
    val isAdLoading: Boolean = false,
    val isPurchaseLoading: Boolean = false,
    /** D5 광고 한도 (E-6). */
    val showAdLimit: Boolean = false,
    /** D6 결제 확인 중 (E-8). */
    val showPurchaseVerifying: Boolean = false,
) : UiState

sealed interface ChargeIntent : UiIntent {
    data object WatchAd : ChargeIntent
    data object Purchase : ChargeIntent
    data object DismissDialog : ChargeIntent
}

sealed interface ChargeEffect : UiEffect {
    data class ShowMessage(val message: String) : ChargeEffect
}
