package com.kakaopay.app.stock.trading_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepository
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepositoryImpl
import com.kakaopay.app.stock.trading_v2.model.KEY_CORE_KEY
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import com.kakaopay.feature.stock.common.presentation.databinding.FragmentMtsTradingTabContainerBinding
import kotlinx.coroutines.flow.collect

class MtsTradingTabFragment : Fragment() {

    private var _binding: FragmentMtsTradingTabContainerBinding? = null
    private val binding: FragmentMtsTradingTabContainerBinding get() = _binding!!

    private val repository: MtsTradingRepository = MtsTradingRepositoryImpl.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMtsTradingTabContainerBinding.inflate(
        inflater,
        container,
        false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coreKey = arguments?.getParcelable<MtsTradingStockKey>(KEY_CORE_KEY) ?: return

        with(binding) {
            pager.isUserInputEnabled = false
            pager.adapter = MtsTradingTabAdapter(
                fragment = this@MtsTradingTabFragment,
                coreKey = coreKey
            )
            stockName.text = coreKey.key

            initRepository()
            initTabSelectedListener()
        }
    }

    /**
     * 선택된 탭 호출
     * - 초깃값(화면진입) 첫번째 탭
     * - 탭 스와이프 시 캐싱된 값
     */
    private fun initRepository() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repository.getSelectedTab().collect {
                setTabSelected(it)
            }
        }
    }

    /**
     * 탭 버튼 리스너 초기화
     */
    private fun initTabSelectedListener() {
        with(binding) {
            tabInfo.setOnClickListener {
                setTabSelected(0)
                goBackToExpanded()
            }
            tabChart.setOnClickListener {
                setTabSelected(1)
                goBackToExpanded()
            }
            tabBidAsk.setOnClickListener {
                setTabSelected(2)
                goBackToExpanded()
            }
            tabHolding.setOnClickListener {
                setTabSelected(3)
                goBackToExpanded()
            }
            tabOrder.setOnClickListener {
                setTabSelected(4)
                goBackToExpanded()
            }

            priceText.setOnClickListener {
                fakeView.isVisible = fakeView.isVisible.not()
            }
        }
    }

    /**
     * 탭 선택 시 앱바 스크롤 초기화
     */
    private fun goBackToExpanded() {
        if (!binding.pager.isScrollContainer) {
            binding.bottomAppbar.setExpanded(true)
        }
    }

    private fun setTabSelected(position: Int) {
        /**
         * 현재 선택된 탭 캐싱
         */
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repository.cacheSelectedTab(position)
        }

        /**
         * 탭 선택 처리
         */
        binding.pager.setCurrentItem(position, false)
        binding.tabContainer.children.forEachIndexed { index, view ->
            view.isSelected = index == position
        }
    }
}