package com.jdm.alarmlocation.data.repository

import com.jdm.alarmlocation.domain.model.TicketState
import com.jdm.alarmlocation.domain.repository.TicketRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 로컬 스텁 티켓 저장소 (서버/결제/광고 SDK 미연동).
 * 차감 규칙: 발생당 1개. 광고 보상: 하루 3개 한도. 결제: 10개.
 * 서버 검증(영수증/보상) 기반 구현으로 교체 예정.
 */
@Singleton
class TicketRepositoryStub @Inject constructor() : TicketRepository {

    private val _state = MutableStateFlow(TicketState(balance = INITIAL_BALANCE))
    override val ticketState = _state.asStateFlow()

    override suspend fun consume(count: Int): Result<Int> {
        val current = _state.value.balance
        if (current < count) {
            return Result.failure(IllegalStateException("티켓이 부족합니다."))
        }
        val remaining = current - count
        _state.update { it.copy(balance = remaining) }
        return Result.success(remaining)
    }

    override suspend fun grantAdReward(): Result<Int> {
        delay(SIMULATED_AD_MS)
        val state = _state.value
        if (!state.canWatchAd) {
            return Result.failure(IllegalStateException("오늘은 광고로 받을 수 있는 티켓을 모두 받았어요."))
        }
        val newBalance = state.balance + TicketState.AD_REWARD_AMOUNT
        _state.update {
            it.copy(
                balance = newBalance,
                todayAdRewardCount = it.todayAdRewardCount + 1,
            )
        }
        return Result.success(newBalance)
    }

    override suspend fun grantPurchase(): Result<Int> {
        delay(SIMULATED_PURCHASE_MS)
        val newBalance = _state.value.balance + TicketState.PURCHASE_AMOUNT
        _state.update { it.copy(balance = newBalance) }
        return Result.success(newBalance)
    }

    private companion object {
        const val INITIAL_BALANCE = 3
        const val SIMULATED_AD_MS = 800L
        const val SIMULATED_PURCHASE_MS = 700L
    }
}
