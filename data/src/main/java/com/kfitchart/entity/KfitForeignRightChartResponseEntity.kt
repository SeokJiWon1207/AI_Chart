package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 권리차트 response
 */
data class KfitForeignRightChartResponseEntity(
    val dataList: List<KfitRightDateTimeEntity>,
    val page: KfitPageEntity,
)

data class KfitRightDateTimeEntity(
    val rightDateTime: Long, // 발생일시
    val right: String, // 권리
    val dividendAmount: String, // 배당금액
    val dividendCurrency: String, // 배당통화
)
