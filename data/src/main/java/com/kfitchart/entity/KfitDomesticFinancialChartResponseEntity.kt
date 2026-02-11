package com.kfitchart.entity

/**
 * @author hanjun.Kim
 * 재무차트 response
 */
data class KfitDomesticFinancialChartResponseEntity(
    val page: KfitPageEntity, // 연속키
    val dataList: List<KfitDomesticFinancialChart>,
)

data class KfitDomesticFinancialChart(
    val financialDate: String, // 재무년월 YYYYMM
    val businessProfit: String, // 영업이익
    val businessProfitEarning: String, // 영업이익 어닝서프라이즈
    val businessProfitRateYoy: String, // 영업이익증가율 YoY
    val businessProfitRateQoq: String, // 영업이익증가율 QoQ
    val netProfit: String, // 순이익
    val netProfitEarning: String, // 순이익 어닝서프라이즈
    val netProfitRateYoy: String, // 순이익 증가율 YoY
    val netProfitRateQoq: String, // 순이익 증가율 QoQ
    val investOpinion: String, // 투자의견
    val goalStockPrice: String, // 목표주가
    val dateTime: Long, // 일시 // timestamp
)
