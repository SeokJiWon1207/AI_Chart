package com.kakaopay.app.stock.trading_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepository
import com.kakaopay.app.stock.trading_v2.data.MtsTradingRepositoryImpl
import com.kakaopay.app.stock.trading_v2.model.MtsTradingStockKey
import com.kakaopay.feature.stock.common.presentation.databinding.FragmentMtsTradingContainerBinding
import kotlinx.coroutines.launch

class MtsTradingContainerFragment : Fragment() {

    private var _binding: FragmentMtsTradingContainerBinding? = null
    private val binding: FragmentMtsTradingContainerBinding get() = _binding!!

    private val repository: MtsTradingRepository = MtsTradingRepositoryImpl.getInstance()

    /**
     * 탭 스와이핑 확인용 샘플키
     */
    private val coreKeyList = listOf(
        MtsTradingStockKey("A000660/001/KR7000660001", false),
        MtsTradingStockKey("A003490/001/KR7003490000", false),
        MtsTradingStockKey("V/200/US92826C8394", false),
        MtsTradingStockKey("COST/201/US22160K1051", false),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMtsTradingContainerBinding.inflate(
        inflater,
        container,
        false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            container.adapter = MtsTradingContainerAdapter(
                fragment = this@MtsTradingContainerFragment,
                coreKeyList = coreKeyList
            )
            close.setOnClickListener {
                /**
                 * 인스턴스 스코프 지정없이 여기서 선택된 탭 초기화
                 */
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.cacheSelectedTab(0)
                }
                requireActivity().finish()
            }
        }
    }

    companion object {
        fun newInstance() = MtsTradingContainerFragment()
    }
}