package com.example.testapi

import com.kfitchart.tiara.KfitTiaraTracker

class KfitTiaraTrackerImpl : KfitTiaraTracker {

    override fun pageView(
        actionName: String,
        currencyId: String,
        stockId: String,
        stockName: String,
        stockType: String,
    ) {}

    override fun clickFilterDay() {}

    override fun clickFilterWeek() {}

    override fun clickFilterMonth() {}

    override fun clickFilterMinute(currentMinute: String) {}

    override fun clickFilterTick(currentTick: String) {}

    override fun clickFilterHorizontal() {}

    override fun clickFilterSetting() {}
}