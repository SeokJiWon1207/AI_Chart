package com.kfitchart.screen.tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.testapi.KfitChartTabRepositoryImpl
import com.example.testapi.KfitTiaraTrackerImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kakaopay.app.stock.util.isNotZero
import com.kakaopay.app.stock.util.toBigDecimalOrZero
import com.kakaopay.feature.stock.common.presentation.R
import com.kakaopay.feature.stock.common.presentation.databinding.KfitChartTabFragmentBinding
import com.kfitchart.CHART_FILE_NAME_INDEX_DOMESTIC
import com.kfitchart.CHART_FILE_NAME_INDEX_FOREIGN
import com.kfitchart.CHART_FILE_NAME_INDEX_FOREIGN_FUTURE
import com.kfitchart.CHART_FILE_NAME_STOCK_DOMESTIC
import com.kfitchart.CHART_FILE_NAME_STOCK_FOREIGN
import com.kfitchart.DEFAULT_CHART_COUNT
import com.kfitchart.GE_PACKET_PERIOD_DAILY
import com.kfitchart.GE_PACKET_PERIOD_MINUTE
import com.kfitchart.GE_PACKET_PERIOD_MONTHLY
import com.kfitchart.GE_PACKET_PERIOD_TICK
import com.kfitchart.GE_PACKET_PERIOD_WEEKLY
import com.kfitchart.KfitConfigControllerActivity
import com.kfitchart.KfitDRChartView
import com.kfitchart.PREF_FLAG_PERIOD_MINUTE
import com.kfitchart.PREF_FLAG_PERIOD_MODE
import com.kfitchart.PREF_FLAG_PERIOD_TICK
import com.kfitchart.PREF_FLAG_TOOLTIP
import com.kfitchart.entity.KfitAverageBuyPriceStreamEntity
import com.kfitchart.entity.KfitBaseChartRequestEntity
import com.kfitchart.entity.KfitDomesticFinancialChartRequestEntity
import com.kfitchart.entity.KfitDomesticFinancialChartResponseEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartRequestEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartResponseEntity
import com.kfitchart.entity.KfitForeignRightChartRequestEntity
import com.kfitchart.entity.KfitForeignRightChartResponseEntity
import com.kfitchart.entity.KfitIndexType
import com.kfitchart.entity.KfitPageEntity
import com.kfitchart.entity.KfitPresentPriceStreamEntity
import com.kfitchart.entity.KfitStockPresentEntity
import com.kfitchart.entity.KfitTradeDataRequestEntity
import com.kfitchart.entity.KfitTradeDataResponseEntity
import com.kfitchart.entity.KfitTradeDateSumItemEntity
import com.kfitchart.entity.indexType
import com.kfitchart.entity.isDomestic
import com.kfitchart.entity.isIndexChart
import com.kfitchart.repository.KfitChartCoreInfo
import com.kfitchart.repository.KfitChartTabRepository
import com.kfitchart.screen.landscape.KfitChartLandscapeActivity
import com.kfitchart.tiara.KfitTiaraTracker
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KfitChartTabFragment : Fragment() {

    private var _binding: KfitChartTabFragmentBinding? = null
    private val binding: KfitChartTabFragmentBinding get() = _binding!!

    private val viewModel: KfitChartTabRepository = KfitChartTabRepositoryImpl.getInstance()

    private val tracker: KfitTiaraTracker by lazy {
        KfitTiaraTrackerImpl()
    }

    // //////////////////////////////
    // //// 해당 코드 아래부터 머지 //////
    // /////////////////////////////

    // 캔들차트 커스텀뷰
    private val kfitChartView by lazy {
        KfitDRChartView(requireContext())
    }

    // 차트 스냅샷 페이징 정보
    private var chartSnapshotPage: KfitPageEntity = KfitPageEntity(
        hasMore = false,
        nextKey = listOf(),
    )

    // 분, 틱 설정 바텀시트
    private var dialog: BottomSheetDialog? = null

    // 공통 앱바 사이즈 변경 처리
    // 캔들차트(KfitDRChartView) 사이즈 변경
    private val layoutChangeListener =
        View.OnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            runCatching {
                val newRect = Rect(left, top, right, bottom)
                val oldRect = Rect(oldLeft, oldTop, oldRight, oldBottom)

                if (newRect != oldRect && binding.chartView.layoutParams != null) {
                    // 변경된 차트 높이 : 컨테이너 높이에서 하단 메뉴 높이를 뺀 값
                    val containerHeight = newRect.height()
                    val menuHeight = binding.chartMenu.chartSetting.height
                    val chartHeight = containerHeight - menuHeight

                    // 변경된 차트 높이 적용
                    kfitChartView.resizeChart(
                        chartLayout = binding.chartView,
                        width = newRect.width(),
                        height = chartHeight,
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = KfitChartTabFragmentBinding.inflate(
        inflater,
        container,
        false,
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        // 차트뷰 초기화
        binding.chartView.doOnLayout {
            kfitChartView.createChart(
                mainActivity = requireActivity() as AppCompatActivity,
                mainLayout = binding.chartView,
                Iwidth = binding.chartView.measuredWidth,
                Iheight = binding.chartView.measuredHeight,
            )
        }

        // 공통 앱바 높이 변경 대응
        addLayoutChangeListener()

        // 데이터 구독
        subscribeCoreInfo()
    }

    /**
     * CoreInfo 변경 구독
     * - 차트 스냅샷 호출
     * - 사용자 설정값에 따른 지표 스냅샷 호출 & 노출
     * - 실시간 데이터 재적용
     */
    private fun subscribeCoreInfo() {
        viewModel.state.flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.RESUMED,
        ).onEach { state ->

            val coreInfo = state.coreInfo
            val data = state.priceSnapshot

            // 차트 초기화
            createChart(coreInfo, data)

            // 설정 초기화
            initChartMenuListener(coreInfo, data)
            initChartMenuData(coreInfo, data)

            // 차트 스냅샷 호출
            loadChartSnapshot(
                isReturnNextkey = false,
                coreInfo = coreInfo,
                data = data,
            )

            // 티아라 페이지뷰 : 종목 변경 시 마다
            runCatching {
                tracker.pageView(
                    "차트탭",
                    currencyId = data.market,
                    stockId = data.code,
                    stockName = data.name,
                    stockType = data.type,
                )
            }

            // 보유 구매평균 스트림 구독
            state.holdingAverageFlow.subscribeHoldingAverageFlow()

            // 실시간 현재가 스트림 구독
            state.currentPriceFlow.subscribeCurrentPriceFlow()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * 구매평균 변경 구독
     * - 사용자 설정값에 따른 노출
     */
    private fun StateFlow<KfitAverageBuyPriceStreamEntity>.subscribeHoldingAverageFlow() {
        this.flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.RESUMED,
        ).onEach {
            // 구매평균 업데이트
            if (it.averagePrice.toBigDecimalOrZero().isNotZero()) {
                kfitChartView.setAvgBuyPriceData(it.averagePrice)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * 현재가 구독
     * - 초깃값으로 스냅샷이 내려옴으로 별도 가격 스냅샷 호출하지 않음
     */
    private fun StateFlow<KfitPresentPriceStreamEntity?>.subscribeCurrentPriceFlow() {
        this.flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.RESUMED,
        ).filterNotNull().onEach {
            // 지수상세가 아니거나 예상지수가 아닐때 업데이트
            if (it.isIndex.not() || it.isExpected.not()) {
                kfitChartView.updateRealData(it)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /**
     * 차트 생성 & 설정
     */
    private fun createChart(
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        // 차트 종류 설정
        // 차트 재생성 시 코어 내부에서 변경된 차트 종류(m_sLoadFileName)를 호출하기 때문에 "차트 종류 설정" 선행
        loadChartView(data)

        // 데이터 호출을 위한 리스너 등록
        kfitChartView.setListener(
            object : KfitDRChartView.OnChartListener {
                // 차트 스냅샷 호출
                override fun requestChartSnapshot(isNextPage: Boolean) {
                    loadChartSnapshot(
                        isReturnNextkey = isNextPage,
                        coreInfo = coreInfo,
                        data = data,
                    )
                }

                // 표시정보 : 구매/판매 표시
                override fun requestTradeData(isEndDate: Boolean) {
                    requestTradedBuyAndSell(isEndDate, coreInfo)
                }

                // 표시정보 : 기업캘린더 뱃지 표시
                override fun requestCorporateCalendar() {
                    requestCorpCalendarBadge(coreInfo, data)
                }

                // 보조지표 : 순매수, 추세, 비율
                override fun requestMarketData(title: String, count: Int) {
                    requestMarketIndicator(title, count, coreInfo)
                }
            },
        )
    }

    /**
     * 차트 종류 설정
     */
    private fun loadChartView(data: KfitStockPresentEntity) {
        // 지수 차트 여부
        val isIndexChart = data.isIndexChart()

        // 지수 차트가 아니면 null
        val isIndexType = data.indexType()

        // 국내 종목 & 지수 여부
        val isDomestic = data.isDomestic()

        val chartFileName =
            if (isIndexChart && isIndexType != null && isIndexType.ordinal == KfitIndexType.INDEX.ordinal && !isDomestic) {
                // 해외지수
                CHART_FILE_NAME_INDEX_FOREIGN
            } else if (isIndexChart && isIndexType != null && isIndexType.ordinal == KfitIndexType.FUTURE.ordinal && !isDomestic) {
                // 해외선물
                CHART_FILE_NAME_INDEX_FOREIGN_FUTURE
            } else if (isIndexChart) {
                // 국내지수
                CHART_FILE_NAME_INDEX_DOMESTIC
            } else {
                when (isDomestic) {
                    // 국내종목
                    true -> CHART_FILE_NAME_STOCK_DOMESTIC
                    // 해외종목
                    false -> CHART_FILE_NAME_STOCK_FOREIGN
                }
            }

        kfitChartView.setLoadChart(chartFileName)
    }

    /**
     * 차트 스냅샷 호출 & 업데이트
     */
    private fun loadChartSnapshot(
        isReturnNextkey: Boolean,
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            kotlin.runCatching {
                /**
                 * period 유닛 : TICK, MIN, DAY, WEEK, MONTH
                 * period 정보 : 봉단위 (1~60)
                 */
                val (periodType, divisionUnit) = when (kfitChartView.period) {
                    GE_PACKET_PERIOD_TICK -> {
                        "TICK" to kfitChartView.unit
                    }

                    GE_PACKET_PERIOD_MINUTE -> {
                        "MIN" to kfitChartView.unit
                    }

                    GE_PACKET_PERIOD_DAILY -> {
                        "DAY" to "1"
                    }

                    GE_PACKET_PERIOD_WEEKLY -> {
                        "WEEK" to "1"
                    }

                    GE_PACKET_PERIOD_MONTHLY -> {
                        "MONTH" to "1"
                    }

                    else -> {
                        kfitChartView.period to "1"
                    }
                }

                viewModel.getChartSnapshot(
                    request = KfitBaseChartRequestEntity(
                        isAdjustedData = true,
                        divisionUnit = divisionUnit,
                        chartDivision = periodType,
                        count = DEFAULT_CHART_COUNT,
                        nextKey = when (isReturnNextkey) {
                            true -> {
                                // 페이징 호출 시 캐싱된 값 전달 혹은 없을 시 null
                                chartSnapshotPage.nextKey.ifEmpty {
                                    null
                                }
                            }

                            false -> {
                                // 페이징 동작하지 않을 시 값 초기화
                                chartSnapshotPage = KfitPageEntity(
                                    hasMore = false,
                                    nextKey = listOf(),
                                )
                                null
                            }
                        },
                    ),
                    stockId = coreInfo.id,
                    exchangeId = coreInfo.exchangeId,
                    isIndex = coreInfo.isIndex,
                    isDomestic = data.isDomestic(),
                ).let {
                    // 캔들차트 뷰 업데이트
                    kfitChartView.updateOutputData(data, it)

                    // 페이징 정보 리턴
                    it.page
                }
            }.onSuccess { pageInfo ->

                // 페이징 정보 저장
                chartSnapshotPage = pageInfo
            }
        }
    }

    /**
     * 설정 메뉴 리스너 초기화
     */
    private fun initChartMenuListener(
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        with(binding) {
            // 분, 틱 선택값 초기화
            // 저장된 값이 없으면 초깃값으로 저장
            if (getPeriodUnit(GE_PACKET_PERIOD_TICK).isNullOrBlank()) {
                requireContext().getSharedPreferences(
                    PREF_FLAG_PERIOD_TICK,
                    Activity.MODE_PRIVATE,
                ).edit().putString(PREF_FLAG_PERIOD_TICK, "60").apply()
            }

            if (getPeriodUnit(GE_PACKET_PERIOD_MINUTE).isNullOrBlank()) {
                requireContext().getSharedPreferences(
                    PREF_FLAG_PERIOD_MINUTE,
                    Activity.MODE_PRIVATE,
                ).edit().putString(PREF_FLAG_PERIOD_MINUTE, "10").apply()
            }

            // 일 메뉴 선택
            chartMenu.menuSelectDay.setOnClickListener {
                clearChartMenuSelected()
                chartMenu.menuSelectDay.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_DAILY, coreInfo, data)

                runCatching {
                    tracker.clickFilterDay()
                }
            }

            // 주 메뉴 선택
            chartMenu.menuSelectWeek.setOnClickListener {
                clearChartMenuSelected()
                chartMenu.menuSelectWeek.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_WEEKLY, coreInfo, data)

                runCatching {
                    tracker.clickFilterWeek()
                }
            }

            // 월 메뉴 선택
            chartMenu.menuSelectMonth.setOnClickListener {
                clearChartMenuSelected()
                chartMenu.menuSelectMonth.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_MONTHLY, coreInfo, data)

                runCatching {
                    tracker.clickFilterMonth()
                }
            }

            // 틱 메뉴 선택
            chartMenu.menuSelectTick.setOnClickListener {
                // 선택된 상태에 따라 선택 바텀시트 노출
                if (!chartMenu.menuSelectTick.isSelected) {
                    clearChartMenuSelected()
                    chartMenu.menuSelectTick.isSelected = true

                    if (getPeriodUnit(GE_PACKET_PERIOD_TICK).isNullOrBlank()) {
                        val units: Array<String> = arrayOf("1", "3", "5", "10", "30", "60")
                        showChartMenuBottomSheet("tick", units, coreInfo, data)
                    } else {
                        setPeriodUnit(
                            chartMenu.tickText.text.toString()
                                .replace(getString(R.string.period_tick), ""),
                            GE_PACKET_PERIOD_TICK,
                            coreInfo,
                            data,
                        )
                    }

                    runCatching {
                        tracker.clickFilterTick(chartMenu.tickText.text.toString())
                    }
                } else {
                    val units: Array<String> = arrayOf("1", "3", "5", "10", "30", "60")
                    showChartMenuBottomSheet("tick", units, coreInfo, data)
                }
            }

            // 분 메뉴 선택
            chartMenu.menuSelectMinute.setOnClickListener {
                // 선택된 상태에 따라 선택 바텀시트 노출
                if (!chartMenu.menuSelectMinute.isSelected) {
                    clearChartMenuSelected()
                    chartMenu.menuSelectMinute.isSelected = true

                    if (getPeriodUnit(GE_PACKET_PERIOD_MINUTE).isNullOrBlank()) {
                        val units: Array<String> = arrayOf("1", "3", "5", "10", "15", "30", "60")
                        showChartMenuBottomSheet("minutes", units, coreInfo, data)
                    } else {
                        setPeriodUnit(
                            chartMenu.minuteText.text.toString()
                                .replace(getString(R.string.period_minute), ""),
                            GE_PACKET_PERIOD_MINUTE,
                            coreInfo,
                            data,
                        )
                    }

                    runCatching {
                        tracker.clickFilterMinute(chartMenu.minuteText.text.toString())
                    }
                } else {
                    val units: Array<String> = arrayOf("1", "3", "5", "10", "15", "30", "60")
                    showChartMenuBottomSheet("minutes", units, coreInfo, data)
                }
            }

            // 가로모드 메뉴 선택
            chartMenu.menuLandscape.setOnClickListener {
                startActivity(
                    KfitChartLandscapeActivity.orientationIntent(
                        context = requireContext(),
                        stockId = coreInfo.id,
                        exchangeId = coreInfo.exchangeId,
                        isinCode = coreInfo.isinCode,
                        isIndex = coreInfo.isIndex,
                    ),
                )

                runCatching {
                    tracker.clickFilterHorizontal()
                }
            }

            // 설정 메뉴 선택
            chartMenu.menuSetting.setOnClickListener {
                // 설정 안내 툴팁 제거
                hideTooltip()

                startActivity(
                    KfitConfigControllerActivity.intent(
                        context = requireContext(),
                        kfitChartView = kfitChartView,
                        kfitChartData = data,
                    ),
                )

                runCatching {
                    tracker.clickFilterSetting()
                }
            }

            // 설정 툴팁 선택
            tooltip.root.setOnClickListener {
                // 설정 안내 툴팁 제거
                hideTooltip()
            }
        }
    }

    /**
     * 설정 메뉴 값 초기화
     */
    private fun initChartMenuData(
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        with(binding) {
            val periodModePreference = requireContext().getSharedPreferences(
                PREF_FLAG_PERIOD_MODE,
                AppCompatActivity.MODE_PRIVATE,
            )
            var tickUnit = getPeriodUnit(GE_PACKET_PERIOD_TICK)
            var minUnit = getPeriodUnit(GE_PACKET_PERIOD_MINUTE)

            if (tickUnit.isNullOrEmpty()) {
                ("60" + getString(R.string.period_tick)).also {
                    chartMenu.tickText.text = it
                    tickUnit = it
                }
            } else {
                (tickUnit + getString(R.string.period_tick)).also { chartMenu.tickText.text = it }
            }

            if (minUnit.isNullOrEmpty()) {
                ("10" + getString(R.string.period_minute)).also {
                    chartMenu.minuteText.text = it
                    minUnit = it
                }
            } else {
                (minUnit + getString(R.string.period_minute)).also {
                    chartMenu.minuteText.text = it
                }
            }

            clearChartMenuSelected()

            when (periodModePreference.getString("kfit_period_mode", GE_PACKET_PERIOD_DAILY)) {
                GE_PACKET_PERIOD_DAILY -> {
                    chartMenu.menuSelectDay.isSelected = true
                    setPeriodType(GE_PACKET_PERIOD_DAILY, coreInfo, data)
                }

                GE_PACKET_PERIOD_WEEKLY -> {
                    chartMenu.menuSelectWeek.isSelected = true
                    setPeriodType(GE_PACKET_PERIOD_WEEKLY, coreInfo, data)
                }

                GE_PACKET_PERIOD_MONTHLY -> {
                    chartMenu.menuSelectMonth.isSelected = true
                    setPeriodType(GE_PACKET_PERIOD_MONTHLY, coreInfo, data)
                }

                GE_PACKET_PERIOD_TICK -> {
                    chartMenu.menuSelectTick.isSelected = true
                    chartMenu.tickText.isSelected = true
                    tickUnit?.let { setPeriodUnit(it, GE_PACKET_PERIOD_TICK, coreInfo, data) }
                }

                GE_PACKET_PERIOD_MINUTE -> {
                    chartMenu.menuSelectMinute.isSelected = true
                    chartMenu.minuteText.isSelected = true
                    minUnit?.let {
                        setPeriodUnit(it, GE_PACKET_PERIOD_MINUTE, coreInfo, data)
                    }
                }

                else -> {
                    chartMenu.menuSelectDay.isSelected = true
                    setPeriodType(GE_PACKET_PERIOD_DAILY, coreInfo, data)
                }
            }
        }
    }

    /**
     * 설정 메뉴 초기화 : 일, 주, 월, 분, 틱
     */
    private fun clearChartMenuSelected() {
        with(binding) {
            chartMenu.menuSelectDay.isSelected = false
            chartMenu.menuSelectWeek.isSelected = false
            chartMenu.menuSelectMonth.isSelected = false

            chartMenu.menuSelectTick.isSelected = false
            chartMenu.tickText.isSelected = false

            chartMenu.menuSelectMinute.isSelected = false
            chartMenu.minuteText.isSelected = false
        }
    }

    /**
     * 설정 메뉴 분, 틱 설정 바텀시트
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showChartMenuBottomSheet(
        type: String,
        units: Array<String>,
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        with(binding) {
            if (dialog == null) {
                dialog = BottomSheetDialog(requireContext(), R.style.kfit_AppBottomSheetDialogTheme)
                dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
            if (dialog?.isShowing == true) {
                return@with
            }

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL
            layout.isMotionEventSplittingEnabled = false

            val inflater = (activity as AppCompatActivity)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val headerLayout = inflater.inflate(
                R.layout.kfit_header_cycleoption_bottomsheet,
                binding.root,
                false,
            ) as LinearLayout

            // 상단 닫기 버튼 클릭
            headerLayout.findViewById<View>(R.id.btn_close).setOnClickListener {
                dialog!!.dismiss()
            }

            val titleTextView = headerLayout.findViewById<TextView>(R.id.tvContent)
            if (type == "tick") {
                titleTextView.text = getString(R.string.chart_bottomsheet_title_tick)
            } else if (type == "minutes") {
                titleTextView.text = getString(R.string.chart_bottomsheet_title_minutes)
            }
            layout.addView(headerLayout)

            for (text in units) {
                val itemLayout = inflater.inflate(
                    R.layout.kfit_cell_cycleoption_item,
                    binding.root,
                    false,
                ) as LinearLayout

                val divider = itemLayout.findViewById<View>(R.id.kfit_cell_divder)
                val circleView = itemLayout.findViewById<View>(R.id.cell_circle_view)
                val itemView = itemLayout.findViewById<View>(R.id.cell_body)
                val itemTextView = itemLayout.findViewById<TextView>(R.id.cell_textView)

                divider.visibility = View.VISIBLE

                if (text == "60") {
                    divider.visibility = View.INVISIBLE
                }

                circleView.visibility = View.INVISIBLE
                if (type == "tick") {
                    (text + getString(R.string.period_tick)).also { itemTextView.text = it }
                } else if (type == "minutes") {
                    (text + getString(R.string.period_minute)).also { itemTextView.text = it }
                }
                if (type == "tick" && chartMenu.tickText.text == itemTextView.text) {
                    itemView.isSelected = true
                    circleView.visibility = View.VISIBLE
                } else if (type == "minutes" && chartMenu.minuteText.text == itemTextView.text) {
                    itemView.isSelected = true
                    circleView.visibility = View.VISIBLE
                }

                itemView.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            itemView.isSelected = true
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            if (circleView.visibility != View.VISIBLE) {
                                itemView.isSelected = !itemView.isSelected
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            runCatching {
                                when (type) {
                                    "tick" -> {
                                        (text + getString(R.string.period_tick)).also {
                                            chartMenu.tickText.text = it
                                            tracker.clickFilterTick(it)
                                        }
                                    }

                                    "minutes" -> {
                                        (text + getString(R.string.period_minute)).also {
                                            chartMenu.minuteText.text = it
                                            tracker.clickFilterMinute(it)
                                        }
                                    }

                                    else -> Unit
                                }
                            }

                            dialog?.dismiss()
                        }
                    }
                    true
                }
                layout.addView(itemLayout)
            }

            dialog?.setOnDismissListener {
                if (type == "tick") {
                    setPeriodUnit(
                        chartMenu.tickText.text.toString()
                            .replace(getString(R.string.period_tick), ""),
                        GE_PACKET_PERIOD_TICK,
                        coreInfo,
                        data,
                    )
                } else {
                    setPeriodUnit(
                        chartMenu.minuteText.text.toString()
                            .replace(getString(R.string.period_minute), ""),
                        GE_PACKET_PERIOD_MINUTE,
                        coreInfo,
                        data,
                    )
                }
            }

            val bottomLayout =
                inflater.inflate(R.layout.kfit_empty_view, binding.root, false) as LinearLayout
            layout.addView(bottomLayout)

            dialog?.setContentView(layout)
            dialog?.setCanceledOnTouchOutside(true)

            dialog?.create()
            dialog?.show()
        }
    }

    /**
     * 기간 타입 변경 & 저장
     */
    private fun setPeriodType(
        periodtype: String,
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        // 기간 타입 저장
        kfitChartView.setPeriodType(periodtype)

        // 변경된 기간에 대한 차트 스냅샷 호출
        loadChartSnapshot(
            isReturnNextkey = false,
            coreInfo = coreInfo,
            data = data,
        )

        // 변경된 기간 타입 프리퍼런스 저장
        requireContext().getSharedPreferences(
            PREF_FLAG_PERIOD_MODE,
            Activity.MODE_PRIVATE,
        ).edit().putString(PREF_FLAG_PERIOD_MODE, periodtype).apply()
    }

    /**
     * 기간 정보 변경 & 저장
     */
    private fun setPeriodUnit(
        unit: String,
        periodtype: String,
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        // 기간 정보 저장
        kfitChartView.apply {
            setPeriodType(periodtype)
            setPeriodTime(unit)
        }

        // 변경된 기간에 대한 차트 스냅샷 호출
        loadChartSnapshot(
            isReturnNextkey = false,
            coreInfo = coreInfo,
            data = data,
        )

        // 변경된 기간 정보 프리퍼런스 저장
        requireContext().getSharedPreferences(
            PREF_FLAG_PERIOD_MODE,
            Activity.MODE_PRIVATE,
        ).edit().putString(PREF_FLAG_PERIOD_MODE, periodtype).apply()

        if (periodtype == GE_PACKET_PERIOD_TICK) {
            requireContext().getSharedPreferences(
                PREF_FLAG_PERIOD_TICK,
                Activity.MODE_PRIVATE,
            ).edit().putString(PREF_FLAG_PERIOD_TICK, unit).apply()
        } else if (periodtype == GE_PACKET_PERIOD_MINUTE) {
            requireContext().getSharedPreferences(
                PREF_FLAG_PERIOD_MINUTE,
                Activity.MODE_PRIVATE,
            ).edit().putString(PREF_FLAG_PERIOD_MINUTE, unit).apply()
        }
    }

    /**
     * 저장된 기간 정보 호출
     */
    private fun getPeriodUnit(periodtype: String): String? {
        return if (periodtype == GE_PACKET_PERIOD_TICK) {
            requireContext().getSharedPreferences(
                PREF_FLAG_PERIOD_TICK,
                Activity.MODE_PRIVATE,
            ).getString(PREF_FLAG_PERIOD_TICK, "")
        } else {
            requireContext().getSharedPreferences(
                PREF_FLAG_PERIOD_MINUTE,
                Activity.MODE_PRIVATE,
            ).getString(PREF_FLAG_PERIOD_MINUTE, "")
        }
    }

    override fun onResume() {
        super.onResume()

        // 차트 내부 저장
        kfitChartView.setMainframe()

        // 툴팁 초기화
        showTooltip()
    }

    override fun onPause() {
        super.onPause()

        // 가로모드 진입 시 현재 차트 설정(확대, 축소)을 유지하기 위해 저장
        kfitChartView.setSaveChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // 공통 앱바 높이 변경 리스너 제거
        removeLayoutChangeListener()

        // 차트 내부 참조 해제
        kfitChartView.onDestroyView()

        _binding = null
    }

    // 공통 앱바 사이즈 변경 처리 : 설정
    private fun addLayoutChangeListener() {
        binding.container.addOnLayoutChangeListener(layoutChangeListener)
    }

    // 공통 앱바 사이즈 변경 처리 : 해제
    private fun removeLayoutChangeListener() {
        binding.container.removeOnLayoutChangeListener(layoutChangeListener)
    }

    // 툴팁 노출
    private fun showTooltip() {
        requireContext().getSharedPreferences(PREF_FLAG_TOOLTIP, Activity.MODE_PRIVATE)
            .getBoolean(PREF_FLAG_TOOLTIP, true).let { isFirstLoad ->
                when (isFirstLoad) {
                    true -> binding.tooltip.root.isVisible = true
                    false -> Unit
                }
            }
    }

    // 툴팁 제거
    private fun hideTooltip() {
        requireContext().getSharedPreferences(PREF_FLAG_TOOLTIP, Activity.MODE_PRIVATE)
            .edit().putBoolean(PREF_FLAG_TOOLTIP, false).apply()

        binding.tooltip.root.isVisible = false
    }

    /**
     * 표시 정보 : 기업 캘린더 뱃지 표시
     */
    private fun requestCorpCalendarBadge(
        coreInfo: KfitChartCoreInfo,
        data: KfitStockPresentEntity,
    ) {
        // 지수일 경우 기업 캘린더 표시 x
        if (coreInfo.isIndex) return

        viewLifecycleOwner.lifecycleScope.launch {
            var financialData = KfitDomesticFinancialChartResponseEntity(
                KfitPageEntity(false, emptyList()),
                emptyList(),
            )

            var rightListData = KfitForeignRightChartResponseEntity(
                emptyList(),
                KfitPageEntity(false, emptyList()),
            )

            if (data.isDomestic()) {
                // 국내종목 : 기업 캘린더
                financialData = kotlin.runCatching {
                    viewModel.getDomesticCalendar(
                        request = KfitDomesticFinancialChartRequestEntity(null),
                        stockId = coreInfo.id,
                        exchangeId = coreInfo.exchangeId,
                    )
                }.getOrDefault(
                    KfitDomesticFinancialChartResponseEntity(
                        page = KfitPageEntity(
                            hasMore = false,
                            nextKey = listOf(),
                        ),
                        dataList = listOf(),
                    ),
                )
            } else {
                // 해외종목 : 권리
                rightListData = kotlin.runCatching {
                    viewModel.getForeignCalendar(
                        request = KfitForeignRightChartRequestEntity(null),
                        stockId = coreInfo.id,
                        exchangeId = coreInfo.exchangeId,
                    )
                }.getOrDefault(
                    KfitForeignRightChartResponseEntity(
                        dataList = listOf(),
                        page = KfitPageEntity(
                            hasMore = false,
                            nextKey = listOf(),
                        ),
                    ),
                )
            }

            var badgeData = ""
            val regexParamSplitString = "^"
            val regexDataSplitString = ";"
            var totalCount = 0

            val typeFinance = "E"
            val typeRight = "D"
            val typeBoth = "DE"

            val equalList = financialData.dataList.filter {
                it.dateTime in rightListData.dataList.map { timeEntity ->
                    timeEntity.rightDateTime
                }
            }.map { it.dateTime }

            // 실적발표
            if (financialData.dataList.isNotEmpty()) {
                for (i in 0 until financialData.dataList.size) {
                    if (financialData.dataList[i].dateTime in equalList) continue

                    val dateTime =
                        kfitChartView.convertTimestampToDate(financialData.dataList[i].dateTime)

                    val financialDate =
                        financialData.dataList[i].financialDate // financialDate YYYYMM

                    badgeData += regexDataSplitString + typeFinance + regexParamSplitString +
                            dateTime + regexParamSplitString +
                            financialDate + regexParamSplitString +
                            financialData.dataList[i].businessProfit + regexParamSplitString +
                            financialData.dataList[i].businessProfitEarning + regexParamSplitString +
                            financialData.dataList[i].businessProfitRateYoy + regexParamSplitString +
                            financialData.dataList[i].businessProfitRateQoq + regexParamSplitString +
                            financialData.dataList[i].netProfit + regexParamSplitString +
                            financialData.dataList[i].netProfitEarning + regexParamSplitString +
                            financialData.dataList[i].netProfitRateYoy + regexParamSplitString +
                            financialData.dataList[i].netProfitRateQoq
                    totalCount++
                }
            }

            // 배당
            if (rightListData.dataList.isNotEmpty()) {
                for (i in 0 until rightListData.dataList.size) {
                    if (rightListData.dataList[i].rightDateTime in equalList) continue

                    val dateTime =
                        kfitChartView.convertTimestampToDate(rightListData.dataList[i].rightDateTime)

                    var sCurrency = rightListData.dataList[i].dividendCurrency
                    if (sCurrency.lowercase(Locale.ENGLISH) == "usd") {
                        sCurrency = "$"
                    }

                    badgeData += regexDataSplitString + typeRight + regexParamSplitString +
                            dateTime + regexParamSplitString +
                            rightListData.dataList[i].right + regexParamSplitString +
                            rightListData.dataList[i].dividendAmount + regexParamSplitString +
                            sCurrency

                    totalCount++
                }
            }

            // 공통 처리
            if (equalList.isNotEmpty()) {
                equalList.map { time ->
                    val dateTime = kfitChartView.convertTimestampToDate(time)

                    val financialDataList =
                        financialData.dataList.filter { it.dateTime == time }

                    for (i in 0 until rightListData.dataList.size) {
                        if (rightListData.dataList[i].rightDateTime in equalList) {
                            var sCurrency = rightListData.dataList[i].dividendCurrency
                            if (sCurrency.lowercase(Locale.ENGLISH) == "usd") {
                                sCurrency = "$"
                            }

                            badgeData += regexDataSplitString + typeBoth + regexParamSplitString +
                                    dateTime + regexParamSplitString +
                                    rightListData.dataList[i].right + regexParamSplitString +
                                    rightListData.dataList[i].dividendAmount + regexParamSplitString +
                                    sCurrency + regexParamSplitString +
                                    financialDataList[0].financialDate + regexParamSplitString +
                                    financialDataList[0].businessProfit + regexParamSplitString +
                                    financialDataList[0].businessProfitEarning + regexParamSplitString +
                                    financialDataList[0].businessProfitRateYoy + regexParamSplitString +
                                    financialDataList[0].businessProfitRateQoq + regexParamSplitString +
                                    financialDataList[0].netProfit + regexParamSplitString +
                                    financialDataList[0].netProfitEarning + regexParamSplitString +
                                    financialDataList[0].netProfitRateYoy + regexParamSplitString +
                                    financialDataList[0].netProfitRateQoq

                            totalCount++
                        }
                    }
                }
            }

            badgeData = totalCount.toString() + badgeData

            kfitChartView.setCorporateCalendarData(badgeData)
        }
    }

    /**
     * 표시정보 : 구매/판매 표시
     */
    private fun requestTradedBuyAndSell(
        isEndDate: Boolean,
        coreInfo: KfitChartCoreInfo,
    ) {
        // 지수일 경우 호출 x
        if (coreInfo.isIndex) return

        val firstTradedDate = kfitChartView.getMethod(
            sValue = when (isEndDate) {
                true -> "getEndDate"
                false -> "getStartDate"
            },
            sParam = "",
        )?.let { method ->
            if (method is String) {
                method
            } else {
                ""
            }
        } ?: ""

        // 구매일자가 없는 경우 호출 x
        if (firstTradedDate == "") return

        viewLifecycleOwner.lifecycleScope.launch {
            val tradedBuyAndSell = kotlin.runCatching {
                if (kfitChartView.chartView == null) {
                    kfitChartView.chartView = kfitChartView.getMainFrame()
                }

                val lastTradedDate = SimpleDateFormat(
                    "yyyyMMdd",
                    Locale("ko", "KR"),
                ).format(Date(System.currentTimeMillis()))

                viewModel.getTradedBuyAndSell(
                    request = KfitTradeDataRequestEntity(
                        firstTradedDate = firstTradedDate,
                        lastTradedDate = lastTradedDate,
                    ),
                    stockId = coreInfo.id,
                    exchangeId = coreInfo.exchangeId,
                )
            }.getOrDefault(KfitTradeDataResponseEntity(tradingList = listOf()))

            val tradeDataList = tradedBuyAndSell.tradingList.sortedWith(
                compareByDescending { it.orderedDate },
            ).toMutableList()

            var buySellPrice = ""
            val regexParamSplitString = "^"
            val regexDataSplitString = ";"
            var totalCount = 0
            val typeBuy = "0"
            val typeSell = "1"

            var preDate = ""
            var preStartWeekDay = ""
            var preEndWeekDay = ""
            var preStartOfMonthStr = ""
            var preEndOfMonthStr = ""

            val count = tradeDataList.count()

            val sumTradeChartList = java.util.HashMap<String, KfitTradeDateSumItemEntity>()

            var sumSellQuantity = 0.0
            var sumBuyQuantity = 0.0
            var sumSellAveragePrice = 0.0
            var sumBuyAveragePrice = 0.0

            var buyCnt = 0
            var sellCnt = 0

            var isSumMode = false // 합산 계산 진행중 여부

            for ((index, chartItem) in tradeDataList.withIndex()) {
                val date = chartItem.orderedDate

                // 주간 데이터 생성시 필요
                var monday = ""
                var friday = ""

                // 월간 데이터 생성시 필요
                var startOfMonthStr = ""
                var endOfMonthStr = ""

                // 월요일, 금요일 찾기
                if (kfitChartView.period == "3") { // 주간 데이터 합산
                    val weekday = getBuyAndSellWeek(date)
                    monday = weekday[0]
                    friday = weekday[1]

                    if (date < preStartWeekDay) {
                        if (buyCnt > 0) {
                            sumBuyAveragePrice /= buyCnt.toDouble()
                        }
                        if (sellCnt > 0) {
                            sumSellAveragePrice /= sellCnt.toDouble()
                        }

                        val fixDay =
                            getPeriodDateForTradeChart(preStartWeekDay, kfitChartView.period)
                        if (fixDay != null) {
                            sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                sellQuantity = sumSellQuantity,
                                buyQuantity = sumBuyQuantity,
                                sellAveragePrice = sumSellAveragePrice,
                                buyAveragePrice = sumBuyAveragePrice,
                            )
                        }

                        // 사용값 초기화
                        sumSellQuantity = 0.0
                        sumBuyQuantity = 0.0
                        sumSellAveragePrice = 0.0
                        sumBuyAveragePrice = 0.0

                        isSumMode = false
                        buyCnt = 0
                        sellCnt = 0

                        preStartWeekDay = ""
                        preEndWeekDay = ""
                    }

                    if (preStartWeekDay == "") {
                        preStartWeekDay = monday
                    }

                    if (preEndWeekDay == "") {
                        preEndWeekDay = friday
                    }
                } else if (kfitChartView.period == "4") { // 월간 데이터 합산
                    // - 월 첫날 계산, - 월 마지막날 계산
                    val monthday = getBuyAndSellMonth(date)
                    startOfMonthStr = monthday[0]
                    endOfMonthStr = monthday[1]

                    if (date < preStartOfMonthStr) {
                        if (buyCnt > 0) {
                            sumBuyAveragePrice /= buyCnt.toDouble()
                        }
                        if (sellCnt > 0) {
                            sumSellAveragePrice /= sellCnt.toDouble()
                        }

                        val fixDay =
                            getPeriodDateForTradeChart(preStartOfMonthStr, kfitChartView.period)

                        if (fixDay != null) {
                            sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                sellQuantity = sumSellQuantity,
                                buyQuantity = sumBuyQuantity,
                                sellAveragePrice = sumSellAveragePrice,
                                buyAveragePrice = sumBuyAveragePrice,
                            )
                        }

                        // 사용값 초기화
                        sumSellQuantity = 0.0
                        sumBuyQuantity = 0.0
                        sumSellAveragePrice = 0.0
                        sumBuyAveragePrice = 0.0

                        isSumMode = false
                        buyCnt = 0
                        sellCnt = 0

                        preStartOfMonthStr = ""
                        preEndOfMonthStr = ""
                    }

                    if (preStartOfMonthStr == "") {
                        preStartOfMonthStr = startOfMonthStr
                    }

                    if (preEndOfMonthStr == "") {
                        preEndOfMonthStr = endOfMonthStr
                    }
                }

                var sellQuantity = 0.0
                var buyQuantity = 0.0
                var sellAveragePrice = 0.0
                var buyAveragePrice = 0.0

                kotlin.runCatching {
                    sellQuantity = chartItem.sellQuantity.toDouble()
                    buyQuantity = chartItem.buyQuantity.toDouble()
                    sellAveragePrice = chartItem.sellAveragePrice.toDouble()
                    buyAveragePrice = chartItem.buyAveragePrice.toDouble()
                }

                if (kfitChartView.period == "2") { // 일주기
                    if (date == preDate) {
                        isSumMode = true

                        // 데이터합산
                        // 매수 주문 추출
                        if (buyQuantity > 0.0 && buyAveragePrice > 0.0) {
                            sumBuyQuantity += buyQuantity
                            sumBuyAveragePrice += buyAveragePrice
                            buyCnt += 1
                        }

                        // 매도 주문 추출
                        if (sellQuantity > 0.0 && sellAveragePrice > 0.0) {
                            sumSellQuantity += sellQuantity
                            sumSellAveragePrice += sellAveragePrice
                            sellCnt += 1
                        }

                        // 마지막 데이터는 합산 후 업데이트
                        if (index == (count - 1)) {
                            if (buyCnt > 0) {
                                sumBuyAveragePrice /= buyCnt.toDouble()
                            }
                            if (sellCnt > 0) {
                                sumSellAveragePrice /= sellCnt.toDouble()
                            }
                            sumTradeChartList[date] = KfitTradeDateSumItemEntity(
                                sellQuantity = sumSellQuantity,
                                buyQuantity = sumBuyQuantity,
                                sellAveragePrice = sumSellAveragePrice,
                                buyAveragePrice = sumBuyAveragePrice,
                            )
                        }
                    } else {
                        if (!isSumMode) {
                            sumSellQuantity = sellQuantity
                            sumBuyQuantity = buyQuantity
                            sumSellAveragePrice = sellAveragePrice
                            sumBuyAveragePrice = buyAveragePrice
                        }
                        if (buyCnt > 0) {
                            sumBuyAveragePrice /= buyCnt.toDouble()
                        }
                        if (sellCnt > 0) {
                            sumSellAveragePrice /= sellCnt.toDouble()
                        }
                        sumTradeChartList[date] = KfitTradeDateSumItemEntity(
                            sellQuantity = sumSellQuantity,
                            buyQuantity = sumBuyQuantity,
                            sellAveragePrice = sumSellAveragePrice,
                            buyAveragePrice = sumBuyAveragePrice,
                        )

                        // 사용값 초기화
                        sumSellQuantity = 0.0
                        sumBuyQuantity = 0.0
                        sumSellAveragePrice = 0.0
                        sumBuyAveragePrice = 0.0

                        isSumMode = false
                        buyCnt = 0
                        sellCnt = 0
                    }
                } else if (kfitChartView.period == "3") { // 주간 데이터 합산
                    if (date in preStartWeekDay..preEndWeekDay) {
                        isSumMode = true

                        // 데이터합산
                        // 매수 주문 추출
                        if (buyQuantity > 0.0 && buyAveragePrice > 0.0) {
                            sumBuyQuantity += buyQuantity
                            sumBuyAveragePrice += buyAveragePrice
                            buyCnt += 1
                        }

                        // 매도 주문 추출
                        if (sellQuantity > 0.0 && sellAveragePrice > 0.0) {
                            sumSellQuantity += sellQuantity
                            sumSellAveragePrice += sellAveragePrice
                            sellCnt += 1
                        }

                        // 마지막 데이터는 합산 후 업데이트
                        if (index == (count - 1)) {
                            if (buyCnt > 0) {
                                sumBuyAveragePrice /= buyCnt.toDouble()
                            }
                            if (sellCnt > 0) {
                                sumSellAveragePrice /= sellCnt.toDouble()
                            }

                            val fixDay = getPeriodDateForTradeChart(
                                preStartWeekDay,
                                kfitChartView.period,
                            )

                            if (fixDay != null) {
                                sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                    sellQuantity = sumSellQuantity,
                                    buyQuantity = sumBuyQuantity,
                                    sellAveragePrice = sumSellAveragePrice,
                                    buyAveragePrice = sumBuyAveragePrice,
                                )
                            }
                        }
                    } else {
                        if (!isSumMode) {
                            sumSellQuantity = sellQuantity
                            sumBuyQuantity = buyQuantity
                            sumSellAveragePrice = sellAveragePrice
                            sumBuyAveragePrice = buyAveragePrice
                        }
                        if (buyCnt > 0) {
                            sumBuyAveragePrice /= buyCnt.toDouble()
                        }
                        if (sellCnt > 0) {
                            sumSellAveragePrice /= sellCnt.toDouble()
                        }

                        val fixDay =
                            getPeriodDateForTradeChart(preStartWeekDay, kfitChartView.period)

                        if (fixDay != null) {
                            sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                sellQuantity = sumSellQuantity,
                                buyQuantity = sumBuyQuantity,
                                sellAveragePrice = sumSellAveragePrice,
                                buyAveragePrice = sumBuyAveragePrice,
                            )
                        }

                        // 사용값 초기화
                        sumSellQuantity = 0.0
                        sumBuyQuantity = 0.0
                        sumSellAveragePrice = 0.0
                        sumBuyAveragePrice = 0.0

                        isSumMode = false
                        buyCnt = 0
                        sellCnt = 0
                    }
                } else if (kfitChartView.period == "4") { // 월간 데이터 합산
                    if ((date in preStartOfMonthStr..preEndOfMonthStr)) {
                        isSumMode = true

                        // 데이터합산
                        // 매수 주문 추출
                        if (buyQuantity > 0.0 && buyAveragePrice > 0.0) {
                            sumBuyQuantity += buyQuantity
                            sumBuyAveragePrice += buyAveragePrice
                            buyCnt += 1
                        }

                        // 매도 주문 추출
                        if (sellQuantity > 0.0 && sellAveragePrice > 0.0) {
                            sumSellQuantity += sellQuantity
                            sumSellAveragePrice += sellAveragePrice
                            sellCnt += 1
                        }

                        // 마지막 데이터는 합산 후 업데이트
                        if (index == (count - 1)) {
                            if (buyCnt > 0) {
                                sumBuyAveragePrice /= buyCnt.toDouble()
                            }
                            if (sellCnt > 0) {
                                sumSellAveragePrice /= sellCnt.toDouble()
                            }

                            val fixDay = getPeriodDateForTradeChart(
                                preStartOfMonthStr,
                                kfitChartView.period,
                            )
                            if (fixDay != null) {
                                sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                    sellQuantity = sumSellQuantity,
                                    buyQuantity = sumBuyQuantity,
                                    sellAveragePrice = sumSellAveragePrice,
                                    buyAveragePrice = sumBuyAveragePrice,
                                )
                            }
                        }
                    } else {
                        if (!isSumMode) {
                            sumSellQuantity = sellQuantity
                            sumBuyQuantity = buyQuantity
                            sumSellAveragePrice = sellAveragePrice
                            sumBuyAveragePrice = buyAveragePrice
                        }
                        if (buyCnt > 0) {
                            sumBuyAveragePrice /= buyCnt.toDouble()
                        }
                        if (sellCnt > 0) {
                            sumSellAveragePrice /= sellCnt.toDouble()
                        }

                        val fixDay =
                            getPeriodDateForTradeChart(preStartOfMonthStr, kfitChartView.period)

                        if (fixDay != null) {
                            sumTradeChartList[fixDay] = KfitTradeDateSumItemEntity(
                                sellQuantity = sumSellQuantity,
                                buyQuantity = sumBuyQuantity,
                                sellAveragePrice = sumSellAveragePrice,
                                buyAveragePrice = sumBuyAveragePrice,
                            )
                        }

                        // 사용값 초기화
                        sumSellQuantity = 0.0
                        sumBuyQuantity = 0.0
                        sumSellAveragePrice = 0.0
                        sumBuyAveragePrice = 0.0

                        isSumMode = false
                        buyCnt = 0
                        sellCnt = 0
                    }
                }

                preDate = date

                preStartWeekDay = monday
                preEndWeekDay = friday

                preStartOfMonthStr = startOfMonthStr
                preEndOfMonthStr = endOfMonthStr
            }
            // Step2 : 매매내역 데이터 생성
            sumTradeChartList.map {
                val tradingItem = it.value
                if (tradingItem.buyQuantity > 0) {
                    buySellPrice += regexDataSplitString + typeBuy + regexParamSplitString +
                            it.key + regexParamSplitString +
                            tradingItem.buyAveragePrice + regexParamSplitString +
                            tradingItem.buyQuantity
                    totalCount++
                }
                if (tradingItem.sellQuantity > 0) {
                    buySellPrice += regexDataSplitString + typeSell + regexParamSplitString +
                            it.key + regexParamSplitString +
                            tradingItem.sellAveragePrice + regexParamSplitString +
                            tradingItem.sellQuantity
                    totalCount++
                }
            }

            if (!isEndDate) { // 조회
                if (totalCount > 0) {
                    buySellPrice = totalCount.toString() + buySellPrice
                    kfitChartView.setBuySellPriceData(buySellPrice)
                } else {
                    kfitChartView.setBuySellPriceData("")
                }
            } else { // 당일건 조회
                if (totalCount > 0) {
                    kfitChartView.setBuySellPriceSpecificDay(buySellPrice)
                }
            }
        }
    }

    /**
     * 보조 지표 : 외국인 비율, 외국인/기관/개인 추세, 기관 순매수
     */
    fun requestMarketIndicator(
        title: String,
        reqCnt: Int,
        coreInfo: KfitChartCoreInfo,
    ) {
        // 지수일 경우 시장지표 호출 x
        if (coreInfo.isIndex) return

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                var indicatorDivision = "" // [FOREIGN, INVESTOR, CREDIT]
                var investorDivision = "" // [ALL, FRN, IND, IST]

                val title0 = "외국인 비율"
                val title1 = "외국인/기관/개인 추세"
                val title2 = "기관 순매수"
                val title3 = "신용 잔고율"
                val title4 = "개인 순매수"
                val title5 = "외국인 순매수"

                var isDMarkets = false
                var isDMarkets1 = false
                var isDMarkets2 = false
//                var isDMarkets3 = false
                var isDMarkets4 = false
                var isDMarkets5 = false

                when (title) {
                    title0 -> {
                        indicatorDivision = "FOREIGN"
                    }

                    title1 -> {
                        indicatorDivision = "INVESTOR"
                        investorDivision = "ALL"
                    }

                    title2 -> {
                        indicatorDivision = "INVESTOR"
                        investorDivision = "IST"
                    }

                    title3 -> {
                        indicatorDivision = "CREDIT"
                    }

                    title4 -> {
                        indicatorDivision = "INVESTOR"
                        investorDivision = "IND"
                    }

                    title5 -> {
                        indicatorDivision = "INVESTOR"
                        investorDivision = "FRN"
                    }
                }

                val marketIndicator = kotlin.runCatching {
                    viewModel.getMarketIndicator(
                        request = KfitDomesticMrkIndcChartRequestEntity(
                            indicatorDivision = indicatorDivision, // [FOREIGN, INVESTOR, CREDIT]
                            investorDivision = investorDivision, // 지표구분이 INVESTOR 인 경우만 [ALL, FRN, IND, IST]
                            count = reqCnt,
                            nextKey = null,
                        ),
                        stockId = coreInfo.id,
                        exchangeId = coreInfo.exchangeId,
                    )
                }.getOrDefault(
                    KfitDomesticMrkIndcChartResponseEntity(
                        page = KfitPageEntity(
                            hasMore = false,
                            nextKey = listOf(),
                        ),
                        dataList = listOf(),
                    ),
                )

                val marketList = marketIndicator.dataList

                val count = marketList.size
                val sTotCnt = kfitChartView.m_nTotCount

                val dDates = LongArray(sTotCnt)
                val dMarkets = DoubleArray(sTotCnt)
                val dMarkets1 = DoubleArray(sTotCnt)
                val dMarkets2 = DoubleArray(sTotCnt)
//                val dMarkets3 = DoubleArray(sTotCnt) // 신용 잔고율은 미사용
                val dMarkets4 = DoubleArray(sTotCnt)
                val dMarkets5 = DoubleArray(sTotCnt)

                if (count > 0) { // 빈데이터 리스트 넘어와서 다른 데이터 덮는걸 방지
                    for (i in 0..<count) {
                        val item = marketList[i]

                        val string = kfitChartView.convertTimestampToDate(item.dateTime)

                        if (sTotCnt - i - 1 >= 0) {
                            dDates[sTotCnt - i - 1] = string.toLong()
                            dMarkets[sTotCnt - i - 1] =
                                item.foreignRate.toDouble() // 외국인 보유율
                            if (dMarkets[sTotCnt - i - 1] != 0.0) isDMarkets = true

                            dMarkets1[sTotCnt - i - 1] =
                                item.foreignNet.toDouble() // 외국인/기관/개인 추세에서 사용
                            if (dMarkets1[sTotCnt - i - 1] != 0.0) isDMarkets1 = true

                            dMarkets2[sTotCnt - i - 1] =
                                item.institutionNet.toDouble() // 기관 순매수
                            if (dMarkets2[sTotCnt - i - 1] != 0.0) isDMarkets2 = true

                            dMarkets4[sTotCnt - i - 1] =
                                item.individualNet.toDouble() // 개인 순매수
                            if (dMarkets4[sTotCnt - i - 1] != 0.0) isDMarkets4 = true

                            dMarkets5[sTotCnt - i - 1] =
                                item.foreignNet.toDouble() // 외국인 순매수
                            if (dMarkets5[sTotCnt - i - 1] != 0.0) isDMarkets5 = true
                        }
                    }

                    if (isDMarkets) {
                        kfitChartView.setMarketData(
                            title0,
                            dDates,
                            dMarkets,
                            sTotCnt,
                            !isDMarkets1,
                        )
                    }
                    if (isDMarkets1) {
                        kfitChartView.setMarketData(
                            title1,
                            dDates,
                            dMarkets1,
                            sTotCnt,
                            !isDMarkets2,
                        )
                    }
                    if (isDMarkets2) {
                        kfitChartView.setMarketData(
                            title2,
                            dDates,
                            dMarkets2,
                            sTotCnt,
                            !isDMarkets4,
                        )
                    }
                    if (isDMarkets4) {
                        kfitChartView.setMarketData(
                            title4,
                            dDates,
                            dMarkets4,
                            sTotCnt,
                            !isDMarkets5,
                        )
                    }
                    if (isDMarkets5) {
                        kfitChartView.setMarketData(
                            title5,
                            dDates,
                            dMarkets5,
                            sTotCnt,
                            true,
                        )
                    }
                }
            }
        }
    }

    /**
     * 매수, 매도 날짜를 가져오기 위한 함수
     * - requestTradedBuyAndSell() 에서만 사용
     */
    private fun getPeriodDateForTradeChart(sValue: String, sParam: String): String? {
        val dates = kfitChartView.getMethod("getStringData", "자료일자")?.let { data ->
            runCatching {
                (data as? Array<*>)?.filterIsInstance<String>() ?: emptyList()
            }.getOrDefault(emptyList())
        } ?: run {
            return null
        }

        if (sParam == "3") { // 차트 시세의 주봉 날짜와 동기화
            val cmpWeekday = getBuyAndSellWeek(sValue)
            val dataCount = dates.size
            for (i in 0 until dataCount) {
                val nIndex = dataCount - i - 1
                val sChartDate = dates[nIndex]
                val chartWeekday = getBuyAndSellWeek(sChartDate)
                // 두 날짜가 같은 week의 범위안에 있는지 비교
                if ((cmpWeekday[0] == chartWeekday[0]) &&
                    (cmpWeekday[1] == chartWeekday[1])
                ) {
                    return sChartDate
                }
            }
        } else if (sParam == "4") { // 차트 시세의 월봉 날짜와 동기화
            val cmpMonthday = getBuyAndSellMonth(sValue)
            for (element in dates) {
                val chartMonthday = getBuyAndSellMonth(element)
                // 두 날짜가 같은 week의 범위안에 있는지 비교
                if ((cmpMonthday[0] == chartMonthday[0]) &&
                    (cmpMonthday[1] == chartMonthday[1])
                ) {
                    return element
                }
            }
        }

        return null
    }

    /**
     * 표시 정보 : 매수,매도 표시
     * 특정 월 범위
     */
    private fun getBuyAndSellMonth(eventDate: String): Array<String> {
        val sYear = eventDate.substring(0, 4)
        val dYear = sYear.toInt()
        val sMon = eventDate.substring(4, 6)
        val dMon = sMon.toInt()
        val dDay = eventDate.substring(6, 8).toInt()

        val cal = Calendar.getInstance()

        cal[dYear, dMon - 1] = dDay
        val endDt = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        println(cal.getActualMaximum(Calendar.DAY_OF_MONTH))

        val monthDay = arrayOf(sYear + sMon + "01", sYear + sMon + endDt.toString())
        return monthDay
    }

    /**
     * 표시 정보 : 매수,매도 표시
     * 특정 날짜의 같은 한 주간의 날짜 범위 * @param eventDate ex) 2020-10-10 *
     */
    private fun getBuyAndSellWeek(eventDate: String): Array<String> {
        val sYear = eventDate.substring(0, 4)
        val sMon = eventDate.substring(4, 6)
        val sDay = eventDate.substring(6, 8)

        val cal = Calendar.getInstance()
        cal[sYear.toInt(), sMon.toInt() - 1] = sDay.toInt()
        // 일주일의 첫날을 일요일로 지정한다
        cal.firstDayOfWeek = Calendar.SUNDAY
        // 시작일과 특정날짜의 차이를 구한다
        val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek
        // 해당 주차의 첫째날을 지정한다
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val sf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        // 해당 주차의 첫째 날짜
        val startDt = sf.format(cal.time)
        // 해당 주차의 마지막 날짜 지정
        cal.add(Calendar.DAY_OF_MONTH, 6)
        // 해당 주차의 마지막 날짜
        val endDt = sf.format(cal.time)

        val weekday = arrayOf(startDt, endDt)
        return weekday
    }
}
