package com.kfitchart.entity

/**
 * 평균단가
 */
data class KfitAverageBuyPriceStreamEntity(
    val holdingQuantity: String, // 수량
    val averagePrice: String, // 평균 단가
)
