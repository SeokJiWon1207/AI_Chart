package com.kfitchart.repository

import androidx.lifecycle.LifecycleObserver
import com.kfitchart.entity.KfitAverageBuyPriceStreamEntity
import com.kfitchart.entity.KfitBaseChartRequestEntity
import com.kfitchart.entity.KfitBaseChartResponseEntity
import com.kfitchart.entity.KfitConnectionStateEntity
import com.kfitchart.entity.KfitDomesticFinancialChartRequestEntity
import com.kfitchart.entity.KfitDomesticFinancialChartResponseEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartRequestEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartResponseEntity
import com.kfitchart.entity.KfitForeignRightChartRequestEntity
import com.kfitchart.entity.KfitForeignRightChartResponseEntity
import com.kfitchart.entity.KfitStockPresentEntity
import com.kfitchart.entity.KfitTradeDataRequestEntity
import com.kfitchart.entity.KfitTradeDataResponseEntity
import kotlinx.coroutines.flow.Flow

interface KfitChartRepository : LifecycleObserver {

    /**
     * IO(실시간, 스냅샷 등) 초기화
     */
    fun init()

    fun onResume()

    /**
     * REST API 처럼 Request-Response 구조는 suspend fun 으로 처리
     */
    suspend fun stockPresentInfo(): KfitStockPresentEntity

    suspend fun baseChart(request: KfitBaseChartRequestEntity): KfitBaseChartResponseEntity

    suspend fun domesticMrkIndcChart(request: KfitDomesticMrkIndcChartRequestEntity): KfitDomesticMrkIndcChartResponseEntity

    suspend fun domesticFinancialChartChart(request: KfitDomesticFinancialChartRequestEntity): KfitDomesticFinancialChartResponseEntity

    suspend fun foreignRightChart(request: KfitForeignRightChartRequestEntity): KfitForeignRightChartResponseEntity

    suspend fun tradeData(request: KfitTradeDataRequestEntity): KfitTradeDataResponseEntity

    /**
     * 실시간가격 Subscibe 요청
     * 틱변동시 subscribeFlow 를 통해 KfitPresentPriceStreamEntity 값 전달
     */
    suspend fun subscribePresentPrice()


    /**
     * 실시간 메시지(가격, 장상태) 플로우
     */
    fun subscribeMessageFlow(): Flow<Any>

    /**
     * 실시간 연결상태 플로우
     */
    fun subscribeConnectionStateFlow(): Flow<KfitConnectionStateEntity>

    /**
     * 구매평균 플로우
     */
    fun subscribeAveragePriceFlow(): Flow<KfitAverageBuyPriceStreamEntity>
}
