package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 매수매도 response
 */
data class KfitTradeDataResponseEntity(
    val tradingList: List<KfitTradingListItemEntity>,
)

data class KfitTradingListItemEntity(
    val orderedDate: String, // 주문일자 YYYYMMDD
    val sellQuantity: String, // 소수점 매도 수량
    val buyQuantity: String, // 소수점 매수 수량
    val sellAveragePrice: String, // 소수점 매도 평균가격
    val buyAveragePrice: String, // 소수점 매수 평균가격
)
