package com.kfitchart.repository

import kotlinx.coroutines.flow.Flow

/**
 * @property id 종목일 경우 stockId, 지수일 경우 indexId
 * @property exchangeId
 * @property isinCode
 * @property isIndex
 * @property stockKey
 *
 * @property detailMaster 종목 마스터
 * @property exchangeMaster 거래소 마스터
 * @property currencyMaster 통화 마스터
 * @property priceFlow 실시간 현재가
 */
data class KfitChartCoreInfo(
    val id: String,
    val exchangeId: String,
    val isinCode: String,
    val isIndex: Boolean,
    val stockKey: String,
    val detailMaster: String,
    val exchangeMaster: String,
    val currencyMaster: String,
    val priceFlow: Flow<String>,
)
