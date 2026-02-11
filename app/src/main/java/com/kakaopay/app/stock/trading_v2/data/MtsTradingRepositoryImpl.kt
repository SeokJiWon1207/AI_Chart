package com.kakaopay.app.stock.trading_v2.data

import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MtsTradingRepositoryImpl : MtsTradingRepository {

    private val selectedTab = MutableStateFlow(0)

    override suspend fun cacheSelectedTab(position: Int) {
        selectedTab.emit(position)
    }

    override fun getSelectedTab(): StateFlow<Int> = selectedTab
    override fun getStockList(): List<MtsTradingStockKey> {
        return listOf(
            MtsTradingStockKey("A000660/001/KR7000660001", false),
            MtsTradingStockKey("A003490/001/KR7003490000", false),
            MtsTradingStockKey("V/200/US92826C8394", false),
            MtsTradingStockKey("COST/201/US22160K1051", false),
        )
    }

    companion object {
        private var instance: MtsTradingRepository? = null

        @Synchronized
        fun getInstance(): MtsTradingRepository {
            if (instance == null) {
                synchronized(MtsTradingRepositoryImpl::class) {
                    instance = MtsTradingRepositoryImpl()
                }
            }
            return instance!!
        }
    }
}