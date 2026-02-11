package com.example.testapi

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
import com.kfitchart.entity.KfitTradeDataRequestEntity
import com.kfitchart.entity.KfitTradeDataResponseEntity
import com.kfitchart.repository.KfitChartCoreInfo
import com.kfitchart.repository.KfitChartTabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 샘플앱 전용 레파지토리
 * 실제 구조와는 다를 수 있고, 함수를 호출하는 KfitChartTabFragment 구조만 동일하면 됩니다
 */
class KfitChartTabRepositoryImpl(
    private val remoteKfit: KfitPayRemoteDataSource
) : KfitChartTabRepository {

    private val _state = MutableStateFlow<KfitChartTabRepository.State?>(null)

    override val state: Flow<KfitChartTabRepository.State>
        get() = _state.asStateFlow().filterNotNull()

    override fun changeState(key: KfitChartCoreInfo) {
        launch {
            remoteKfit.subscribe(if (key.isIndex) 3 else 1)

            _state.emit(
                KfitChartTabRepository.State(
                    coreInfo = key,
                    priceSnapshot = if (key.isIndex) {
                        remoteKfit.getIndexPresent()
                    } else {
                        remoteKfit.getStockPresent()
                    },
                    currentPriceFlow = remoteKfit.subscribeMessageFlow().map {
                        when (it) {
                            is KfitPresentPriceStreamEntity -> it
                            else -> null
                        }
                    }.stateIn(
                        scope = this,
                        started = SharingStarted.Eagerly,
                        initialValue = null
                    ),
                    holdingAverageFlow = remoteKfit.subscribeAveragePriceFlow().stateIn(
                        scope = this,
                        started = SharingStarted.Eagerly,
                        initialValue = KfitAverageBuyPriceStreamEntity(
                            holdingQuantity = "33",
                            averagePrice = "124000"
                        )
                    )
                )
            )
        }
    }

    override suspend fun getChartSnapshot(
        request: KfitBaseChartRequestEntity,
        stockId: String,
        exchangeId: String,
        isIndex: Boolean,
        isDomestic: Boolean
    ): KfitBaseChartResponseEntity {
        return remoteKfit.getBaseChart(request)
    }

    override suspend fun getDomesticCalendar(
        request: KfitDomesticFinancialChartRequestEntity,
        stockId: String,
        exchangeId: String
    ): KfitDomesticFinancialChartResponseEntity {
        return remoteKfit.getDomesticFinancialData(request)
    }

    override suspend fun getForeignCalendar(
        request: KfitForeignRightChartRequestEntity,
        stockId: String,
        exchangeId: String
    ): KfitForeignRightChartResponseEntity {
        return remoteKfit.getForeignRightData(request)
    }

    override suspend fun getTradedBuyAndSell(
        request: KfitTradeDataRequestEntity,
        stockId: String,
        exchangeId: String
    ): KfitTradeDataResponseEntity {
        return remoteKfit.getTradeData(request)
    }

    override suspend fun getMarketIndicator(
        request: KfitDomesticMrkIndcChartRequestEntity,
        stockId: String,
        exchangeId: String
    ): KfitDomesticMrkIndcChartResponseEntity {
        return remoteKfit.getDomesticMrkIndcChart(request)
    }

    companion object {
        private var instance: KfitChartTabRepository? = null

        @Synchronized
        fun getInstance(): KfitChartTabRepository {
            if (instance == null) {
                synchronized(KfitChartTabRepositoryImpl::class) {
                    instance = KfitChartTabRepositoryImpl(
                        KfitPayRemoteDataSource()
                    )
                }
            }
            return instance!!
        }
    }
}