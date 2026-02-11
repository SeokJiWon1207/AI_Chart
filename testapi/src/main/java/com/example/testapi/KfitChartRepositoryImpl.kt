package com.example.testapi

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
import com.kfitchart.repository.KfitChartRepository
import kotlinx.coroutines.flow.Flow

class KfitChartRepositoryImpl(
    private val remoteKfit: KfitPayRemoteDataSource
) : KfitChartRepository {

    private lateinit var stockId: String
    private lateinit var exchangeId: String
    private var isIndex: Boolean = false

    override fun init() {
        remoteKfit.initialize()
    }

    override fun onResume() {
        remoteKfit.onResume()
    }

    override suspend fun stockPresentInfo(): KfitStockPresentEntity {
        return if (isIndex) remoteKfit.getIndexPresent() else remoteKfit.getStockPresent()
    }

    override suspend fun baseChart(requestKfit: KfitBaseChartRequestEntity): KfitBaseChartResponseEntity {
        return remoteKfit.getBaseChart(requestKfit)
    }

    override suspend fun domesticMrkIndcChart(requestKfit: KfitDomesticMrkIndcChartRequestEntity): KfitDomesticMrkIndcChartResponseEntity {
        return remoteKfit.getDomesticMrkIndcChart(requestKfit)
        // COMUtil._TAG_REQUEST_MARKET_TYPE 과 연관되어 있음.
    }

    override suspend fun domesticFinancialChartChart(requestKfit: KfitDomesticFinancialChartRequestEntity): KfitDomesticFinancialChartResponseEntity {
        return remoteKfit.getDomesticFinancialData(requestKfit)
    }

    override suspend fun foreignRightChart(requestKfit: KfitForeignRightChartRequestEntity): KfitForeignRightChartResponseEntity {
        return remoteKfit.getForeignRightData(requestKfit)
    }

    override suspend fun tradeData(requestKfit: KfitTradeDataRequestEntity): KfitTradeDataResponseEntity {
        return remoteKfit.getTradeData(requestKfit)
    }

    override suspend fun subscribePresentPrice() {
        remoteKfit.subscribe(if (isIndex) 3 else 1)
    }

    override fun subscribeMessageFlow(): Flow<Any> {
        remoteKfit.subscribe(if (isIndex) 3 else 1)
        return remoteKfit.subscribeMessageFlow()
    }

    override fun subscribeConnectionStateFlow(): Flow<KfitConnectionStateEntity> {
        return remoteKfit.subscribeConnectionStateFlow()
    }

    override fun subscribeAveragePriceFlow(): Flow<KfitAverageBuyPriceStreamEntity> {
        return remoteKfit.subscribeAveragePriceFlow()
    }
}