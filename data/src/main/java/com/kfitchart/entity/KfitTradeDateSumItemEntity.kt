package com.kfitchart.entity

/**
 * 표시 정보 : 매수,매도 표시 아이템 엔티티
 */
data class KfitTradeDateSumItemEntity(
    var sellQuantity: Double,
    var buyQuantity: Double,
    var sellAveragePrice: Double,
    var buyAveragePrice: Double,
)
