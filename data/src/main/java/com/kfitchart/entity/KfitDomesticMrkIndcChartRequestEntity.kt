package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 시장지표차트 reqeust
 */
data class KfitDomesticMrkIndcChartRequestEntity(
    val indicatorDivision: String, // 지표구분
    val investorDivision: String, // 투자자구분
    val count: Int,
    val nextKey: List<String>? = null, // 연속키
)
