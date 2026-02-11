package com.kakaopay.app.stock.trading_v2.data

import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import kotlinx.coroutines.flow.StateFlow

/**
 * 탭(종목) 스와이프 시 이전 탭의 하위탭을 캐싱 & 호출
 * (차트와 무관한 UI 상태 저장 코드라서 해당 패키지에서 구현)
 */
interface MtsTradingRepository {

    /**
     * 선택된 하위탭 캐싱
     */
    suspend fun cacheSelectedTab(position: Int)

    /**
     * 선택된 하위탭 호출
     */
    fun getSelectedTab(): StateFlow<Int>

    /**
     * 저장된 종목 리스트
     */
    fun getStockList(): List<MtsTradingStockKey>
}