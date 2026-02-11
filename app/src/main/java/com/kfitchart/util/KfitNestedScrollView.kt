package com.kfitchart.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.fragment.app.findFragment
import androidx.viewpager2.widget.ViewPager2
import com.kakaopay.app.stock.trading_v2.MtsTradingContainerFragment
import com.kakaopay.app.stock.trading_v2.MtsTradingTabFragment
import com.kakaopay.feature.stock.common.presentation.R
import com.kfitchart.KfitChartViewFragment

/**
 * 증권 레포에서는 구조가 달라서 각각 사용합니다
 * XML 싱크를 위해 동일한 네이밍으로 사용합니다
 */
class KfitNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val tabFragment = (findFragment<KfitChartViewFragment>().parentFragment) as MtsTradingTabFragment
        val containerFragment = (tabFragment.parentFragment) as MtsTradingContainerFragment

        val parentViewPager = containerFragment.view?.rootView?.findViewById<ViewPager2>(R.id.container)
        parentViewPager?.isUserInputEnabled = false

        when (e.action) {
            MotionEvent.ACTION_UP -> {
                parentViewPager?.isUserInputEnabled = true
            }
            else -> {
                parentViewPager?.isUserInputEnabled = false
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}