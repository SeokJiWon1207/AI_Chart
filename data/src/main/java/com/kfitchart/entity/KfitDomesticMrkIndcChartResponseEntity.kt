package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 시장지표차트 response
 */
data class KfitDomesticMrkIndcChartResponseEntity(
    val page: KfitPageEntity, // 연속키
    val dataList: List<KfitDomesticMrkIndcChart>,
)

data class KfitDomesticMrkIndcChart(
    val dateTime: Long, // 일시 //Timestamp
    val foreignRate: String, // 외국인보유율
    val foreignNet: String, // 외국인순매수 (데이터 없을 경우 "0")
    val institutionNet: String, // 기관순매수 (데이터 없을 경우 "0")
    val individualNet: String, // 개인순매수 (데이터 없을 경우 "0")
)
