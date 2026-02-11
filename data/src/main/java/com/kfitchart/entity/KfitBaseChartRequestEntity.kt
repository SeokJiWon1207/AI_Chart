package com.kfitchart.entity

data class KfitBaseChartRequestEntity(
    var chartDivision: String, // 차트구분 (TICK, MIN, DAY, WEEK, MONTH)
    var divisionUnit: String, // 봉단위 (1~60)
    var isAdjustedData: Boolean, // 수정주가여부 // true, fasle
    val count: Int? = null, // 갯수
    var nextKey: List<String>? = null, // 연속키
)
