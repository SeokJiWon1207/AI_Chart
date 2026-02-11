package com.example.testapi
//api 통신할때 사용
data class TestKfitBaseChartResponseEntity(
    val page: TestNextKeyEntity,
    val data_list: List<TestBaseChart>
)

data class TestNextKeyEntity(
    val next_key: List<String>,
    val has_more: String // true,false
)

data class TestBaseChart(
    val created_at: String, // 일시 // timestamp
    val start_price: String, // 시가
    val high_price: String, // 고가
    val low_price: String, // 저가
    val close_price: String, // 종가
    val volume: String, // 거래량
    val amount: String // 거래대금
)