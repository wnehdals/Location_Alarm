package com.jdm.alarmlocation.domain.model

/**
 * 티켓(이용권) 상태 (REQUIREMENTS_SPEC §6, §7).
 * [balance] 보유 티켓 수, [todayAdRewardCount] 오늘 광고 보상으로 받은 수(자정 초기화).
 */
data class TicketState(
    val balance: Int = 0,
    val todayAdRewardCount: Int = 0,
) {
    val canWatchAd: Boolean get() = todayAdRewardCount < DAILY_AD_REWARD_LIMIT

    companion object {
        const val DAILY_AD_REWARD_LIMIT = 3
        const val AD_REWARD_AMOUNT = 1
        const val PURCHASE_AMOUNT = 10
        const val PURCHASE_PRICE_KRW = 2000
    }
}
