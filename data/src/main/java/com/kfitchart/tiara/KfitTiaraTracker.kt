package com.kfitchart.tiara

interface KfitTiaraTracker {

    fun pageView(
        actionName: String,
        currencyId: String,
        stockId: String,
        stockName: String,
        stockType: String,
    )

    fun clickFilterDay()

    fun clickFilterWeek()

    fun clickFilterMonth()

    fun clickFilterMinute(currentMinute: String)

    fun clickFilterTick(currentTick: String)

    fun clickFilterHorizontal()

    fun clickFilterSetting()
}
