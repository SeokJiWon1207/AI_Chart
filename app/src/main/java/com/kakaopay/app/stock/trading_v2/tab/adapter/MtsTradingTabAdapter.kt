package com.kakaopay.app.stock.trading_v2.tab.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingBidAskFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingHoldingFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingInfoFragment
import com.kakaopay.app.stock.trading_v2.tab.MtsTradingOrderFragment
import com.kfitchart.screen.tab.KfitChartTabFragment

class MtsTradingTabAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        MtsTradingInfoFragment(),
        KfitChartTabFragment(),
        MtsTradingBidAskFragment(),
        MtsTradingHoldingFragment(),
        MtsTradingOrderFragment()
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
