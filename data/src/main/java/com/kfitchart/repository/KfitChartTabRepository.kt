package com.kfitchart.repository

import androidx.lifecycle.LifecycleObserver
import com.kfitchart.entity.KfitAverageBuyPriceStreamEntity
import com.kfitchart.entity.KfitBaseChartRequestEntity
import com.kfitchart.entity.KfitBaseChartResponseEntity
import com.kfitchart.entity.KfitDomesticFinancialChartRequestEntity
import com.kfitchart.entity.KfitDomesticFinancialChartResponseEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartRequestEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartResponseEntity
import com.kfitchart.entity.KfitForeignRightChartRequestEntity
import com.kfitchart.entity.KfitForeignRightChartResponseEntity
import com.kfitchart.entity.KfitPresentPriceStreamEntity
import com.kfitchart.entity.KfitStockPresentEntity
import com.kfitchart.entity.KfitTradeDataRequestEntity
import com.kfitchart.entity.KfitTradeDataResponseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

interface KfitChartTabRepository : LifecycleObserver, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    val state: Flow<State>

    fun changeState(key: KfitChartCoreInfo)

    // 차트 스냅샷
    suspend fun getChartSnapshot(
        request: KfitBaseChartRequestEntity,
        stockId: String,
        exchangeId: String,
        isIndex: Boolean,
        isDomestic: Boolean,
    ): KfitBaseChartResponseEntity

    /**
     * 표시정보 : 기업캘린더
     * - 국내 : 재무 정보
     */
    suspend fun getDomesticCalendar(
        request: KfitDomesticFinancialChartRequestEntity,
        stockId: String,
        exchangeId: String,
    ): KfitDomesticFinancialChartResponseEntity

    /**
     * 표시정보 : 기업캘린더
     * - 해외 : 권리 정보
     */
    suspend fun getForeignCalendar(
        request: KfitForeignRightChartRequestEntity,
        stockId: String,
        exchangeId: String,
    ): KfitForeignRightChartResponseEntity

    /**
     * 표시정보 : 매수/매도 표시
     */
    suspend fun getTradedBuyAndSell(
        request: KfitTradeDataRequestEntity,
        stockId: String,
        exchangeId: String,
    ): KfitTradeDataResponseEntity

    /**
     * 보조지표 : 순매수, 추세, 외국인비율
     * - 국내만 해당
     */
    suspend fun getMarketIndicator(
        request: KfitDomesticMrkIndcChartRequestEntity,
        stockId: String,
        exchangeId: String,
    ): KfitDomesticMrkIndcChartResponseEntity

    /**
     * @param coreInfo 차트, 시장지표, 재무, 매매건수 등 스냅샷 호출용
     * @param priceSnapshot 현재가 스냅샷
     * @param currentPriceFlow 실시간 현재가
     * @param holdingAverageFlow 보유 구매평균
     */
    data class State(
        val coreInfo: KfitChartCoreInfo,
        val priceSnapshot: KfitStockPresentEntity,
        val currentPriceFlow: StateFlow<KfitPresentPriceStreamEntity?>,
        val holdingAverageFlow: StateFlow<KfitAverageBuyPriceStreamEntity>,
    )
}