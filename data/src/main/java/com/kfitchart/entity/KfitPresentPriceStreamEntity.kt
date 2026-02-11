package com.kfitchart.entity

/**
 * 실시간 현재가
 */
data class KfitPresentPriceStreamEntity(
    val exchangeId: String, // 거래소코드
    val stockId: String, // 종목코드
    val endPrice: String, // 전일종가
    val startPrice: String, // 시가
    val highestPrice: String, // 고가
    val lowestPrice: String, // 저가
    val currentPrice: String, // 현재가
    val fluctuationFlagType: KfitFluctuationFlagType, // 대비기호
    val fluctuationPrice: String, // 당일 변동가
    val fluctuationRate: String, // 당일 변동률
    val volume: String, // 거래량 // 체결
    val totalVolume: String, // 누적 거래량
    val updatedAt: Long, // 체결시간 // TimeStamp
    val isExpected: Boolean = false, // 예상지수 여부 //지수에서만 사용
    val isIndex: Boolean, // 지수종목 여부
    val sessionId: String? = null, // 해외장구분 : 0 정규장, 1 프리마켓, 2 애프터마켓, 4 주간거래
)
