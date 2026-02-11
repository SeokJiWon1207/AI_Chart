package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 권리차트 request
 */
data class KfitForeignRightChartRequestEntity(
    var nextKey: List<String>? = null, // 연속키
    val count: Int = 100,
)
