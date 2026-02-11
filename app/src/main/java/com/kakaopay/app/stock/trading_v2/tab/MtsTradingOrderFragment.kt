package com.kakaopay.app.stock.trading_v2.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kakaopay.app.stock.trading_v2.tab.adapter.MtsTradingSampleAdapter
import com.kakaopay.feature.stock.common.presentation.databinding.FragmentMtsTradingTabScreenBinding

/**
 * 탭 테스트를 위한 빈 화면
 */
class MtsTradingOrderFragment : Fragment() {

    private var _binding: FragmentMtsTradingTabScreenBinding? = null
    private val binding: FragmentMtsTradingTabScreenBinding get() = _binding!!

    private val adapter by lazy {
        MtsTradingSampleAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMtsTradingTabScreenBinding.inflate(
        inflater,
        container,
        false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.recycler) {
            itemAnimator = null
            adapter = this@MtsTradingOrderFragment.adapter
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}