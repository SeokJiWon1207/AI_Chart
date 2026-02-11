package com.kakaopay.app.stock.trading_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testapi.KfitChartTabRepositoryImpl
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepository
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepositoryImpl
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import com.kakaopay.app.stock.trading_v2.tab.adapter.MtsTradingStockAdapter
import com.kakaopay.app.stock.trading_v2.tab.adapter.MtsTradingTabAdapter
import com.kakaopay.feature.stock.common.presentation.databinding.FragmentMtsTradingBinding
import com.kfitchart.repository.KfitChartCoreInfo
import com.kfitchart.repository.KfitChartTabRepository
import kotlinx.coroutines.flow.flowOf

/**
 * 종목상세 차트탭 테스트를 위한 샘플 화면
 * - 증권 코드와 다르게 고정된 데이터를 셋팅합니다
 * - 고정된 데이터에서 첫번째 StockKey 를 로드하지만 데이터는 목데이터이기 때문에 모두 동일합니다
 * - 헤더 영역에서 종목 변경으로 StockKey 변경 플로우가 타지만 차트탭이 리로드만 되고 데이터는 동일합니다
 */
class MtsTradingFragment : Fragment() {

    private var _binding: FragmentMtsTradingBinding? = null
    private val binding: FragmentMtsTradingBinding get() = _binding!!

    private val tradingRepository: MtsTradingRepository = MtsTradingRepositoryImpl.getInstance()
    private val chartRepository: KfitChartTabRepository = KfitChartTabRepositoryImpl.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMtsTradingBinding.inflate(
        inflater,
        container,
        false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            pager.isUserInputEnabled = false
            pager.adapter = MtsTradingTabAdapter(
                fragment = this@MtsTradingFragment,
            )

            /**
             * 샘플앱 구조상 저장된 종목 리스트의 첫번째를 선택된 종목으로 지정
             * 별도 종목 선택 시 UI 변경 없이 Flow 만 전달
             */
            tradingRepository.getStockList().let { keys ->
                stockName.text = keys.first().getId()
                stockListView.adapter = MtsTradingStockAdapter(
                    items = keys,
                    onItemClick = ::changeCurrentStock
                )

                changeCurrentStock(keys.first())
            }

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
            tradingRepository.getSelectedTab().collect {
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
                stockListContainer.isVisible = !stockListContainer.isVisible
            }

            close.setOnClickListener {
                requireActivity().finish()
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
            tradingRepository.cacheSelectedTab(position)
        }

        /**
         * 탭 선택 처리
         */
        binding.pager.setCurrentItem(position, false)
        binding.tabContainer.children.forEachIndexed { index, view ->
            view.isSelected = index == position
        }
    }

    /**
     * 종목 변경에 따른 화면 갱신 테스트
     */
    private fun changeCurrentStock(stockKey: MtsTradingStockKey) {
        chartRepository.changeState(
            KfitChartCoreInfo(
                id = stockKey.key,
                exchangeId = "",
                isinCode = "",
                isIndex = stockKey.isIndex,
                stockKey = "",
                detailMaster = "",
                exchangeMaster = "",
                currencyMaster = "",
                priceFlow = flowOf("")

            )
        )
    }

    companion object {
        fun newInstance() = MtsTradingFragment()
    }
}