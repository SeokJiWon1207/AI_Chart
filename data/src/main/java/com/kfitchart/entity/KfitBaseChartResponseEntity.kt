package com.kfitchart.entity

data class KfitBaseChartResponseEntity(
    val page: KfitPageEntity,
    val list: List<KfitBaseChart>,
    val data: KfitBaseChartData?, // 장시간 정보 (해외만)
)

data class KfitBaseChart(
    val dateTime: Long, // 일시 // timestamp
    val startPrice: String, // 시가
    val highPrice: String, // 고가
    val lowPrice: String, // 저가
    val closePrice: String, // 종가
    val volume: String, // 거래량
    val amount: String, // 거래대금
    val sessionId: String?, // 해외장구분 : 0 정규장, 1 프리마켓, 2 애프터마켓, 3 주간거래
    val createdTime: String?, // 허봉처리 (해외만)
)

data class KfitBaseChartData(
    val preMarketStartTime: String, // 프리마켓시작시간
    val preMarketEndTime: String, // 프리마켓종료시간
    val marketStartTime: String, // 정규장시작
    val marketEndTime: String, // 정규장종료시간
    val afterMarketStartTime: String, // 애프터마켓시작시간
    val afterMarketEndTime: String, // 애프터마켓종료시간
    val dayMarketStartTime: String, // 데이마켓시작시간
    val dayMarketEndTime: String, // 데이마켓종료시간
)
