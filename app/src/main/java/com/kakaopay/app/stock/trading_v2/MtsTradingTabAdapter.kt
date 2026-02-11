package com.kakaopay.app.stock.trading_v2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kakaopay.app.stock.trading_v2.model.KEY_CORE_KEY
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingBidAskFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingHoldingFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingInfoFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingOrderFragment
import com.kfitchart.KfitChartViewFragment

class MtsTradingTabAdapter(
    fragment: MtsTradingTabFragment,
    coreKey: MtsTradingStockKey
) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        MtsTradingInfoFragment(),
        KfitChartViewFragment(),
        MtsTradingBidAskFragment(),
        MtsTradingHoldingFragment(),
        MtsTradingOrderFragment()
    ).onEach {
        it.apply {
            arguments = Bundle().apply {
                putParcelable(KEY_CORE_KEY, coreKey)
            }
        }
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
