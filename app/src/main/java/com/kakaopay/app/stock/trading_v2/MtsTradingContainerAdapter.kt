package com.kakaopay.app.stock.trading_v2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kakaopay.app.stock.trading_v2.model.KEY_CORE_KEY
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey

class MtsTradingContainerAdapter(
    fragment: MtsTradingContainerFragment,
    private val coreKeyList: List<MtsTradingStockKey>
) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return coreKeyList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = MtsTradingTabFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(KEY_CORE_KEY, coreKeyList[position])
        }
        return fragment
    }
}
