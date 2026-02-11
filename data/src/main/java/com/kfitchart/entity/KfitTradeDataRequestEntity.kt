package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 매수/매도 request
 */
data class KfitTradeDataRequestEntity(
    val firstTradedDate: String, // 시작 YYYYMMDD
    val lastTradedDate: String, // 끝 YYYYMMDD
)
