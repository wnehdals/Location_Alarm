package com.jdm.alarmlocation.presentation.ui.compose.charge

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.domain.model.TicketState
import com.jdm.alarmlocation.domain.repository.TicketRepository
import com.jdm.alarmlocation.presentation.ui.compose.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargeViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
) : MviViewModel<ChargeState, ChargeIntent, ChargeEffect>(ChargeState()) {

    init {
        viewModelScope.launch {
            ticketRepository.ticketState.collect { ticket ->
                setState { copy(ticket = ticket) }
            }
        }
    }

    override fun onIntent(intent: ChargeIntent) {
        when (intent) {
            ChargeIntent.WatchAd -> watchAd()
            ChargeIntent.Purchase -> purchase()
            ChargeIntent.DismissDialog ->
                setState { copy(showAdLimit = false, showPurchaseVerifying = false) }
        }
    }

    private fun watchAd() {
        // E-6: 광고 보상 일일 한도 초과 → D5.
        if (!currentState.ticket.canWatchAd) {
            setState { copy(showAdLimit = true) }
            return
        }
        if (currentState.isAdLoading) return
        setState { copy(isAdLoading = true) }
        viewModelScope.launch {
            // 스텁: 시청 완료로 간주(E-7 미완료는 실 SDK 연동 시 콜백으로 구분).
            ticketRepository.grantAdReward()
                .onSuccess {
                    setState { copy(isAdLoading = false) }
                    sendEffect(ChargeEffect.ShowMessage("티켓 1장을 받았어요."))
                }
                .onFailure {
                    setState { copy(isAdLoading = false, showAdLimit = true) }
                }
        }
    }

    private fun purchase() {
        if (currentState.isPurchaseLoading) return
        // E-8: 결제 후 서버 검증 대기 → D6.
        setState { copy(isPurchaseLoading = true, showPurchaseVerifying = true) }
        viewModelScope.launch {
            ticketRepository.grantPurchase()
                .onSuccess {
                    setState { copy(isPurchaseLoading = false, showPurchaseVerifying = false) }
                    sendEffect(ChargeEffect.ShowMessage("티켓 ${TicketState.PURCHASE_AMOUNT}장이 충전됐어요."))
                }
                .onFailure {
                    setState { copy(isPurchaseLoading = false, showPurchaseVerifying = false) }
                    sendEffect(ChargeEffect.ShowMessage("결제가 완료되지 않았어요."))
                }
        }
    }
}
