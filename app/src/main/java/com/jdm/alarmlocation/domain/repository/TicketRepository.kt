package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.TicketState
import kotlinx.coroutines.flow.Flow

/**
 * 티켓 저장소 (REQUIREMENTS_SPEC §6, §7).
 * 차감은 알람 1회 발생당 1개(ON 전환 시 차감 없음). 광고 보상 하루 3개 한도.
 * 구현은 추후 서버 검증(영수증/보상) 기반으로 교체.
 */
interface TicketRepository {
    val ticketState: Flow<TicketState>

    /** 알람 발생당 1개 차감. 잔량 부족 시 실패. 성공 시 남은 잔량 반환. */
    suspend fun consume(count: Int = 1): Result<Int>

    /** 리워드 광고 시청 완료 보상(+1). 일일 한도 초과 시 실패. */
    suspend fun grantAdReward(): Result<Int>

    /** 인앱 결제 완료(10개). 서버 검증 성공 가정. */
    suspend fun grantPurchase(): Result<Int>
}
