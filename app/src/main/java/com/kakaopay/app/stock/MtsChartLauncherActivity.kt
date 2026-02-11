package com.kakaopay.app.stock

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kakaopay.app.stock.trading_v2.MtsTradingActivity
import com.kakaopay.feature.stock.common.presentation.R

class MtsChartLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mts_chart_launcher)
    }

    fun onTradingV2Button(view: View) {
        startActivity(
            MtsTradingActivity.newIntent(this)
        )
        overridePendingTransition(0, 0)
    }
}
