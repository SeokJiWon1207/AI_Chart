package com.kfitchart.screen.tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.testapi.KfitChartRepositoryImpl
import com.example.testapi.KfitPayRemoteDataSource
import com.example.testapi.KfitTiaraTrackerImpl
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kakaopay.feature.stock.common.presentation.R
import com.kakaopay.feature.stock.common.presentation.databinding.KfitFragmentMainBinding
import com.kfitchart.CHART_FILE_NAME_INDEX_DOMESTIC
import com.kfitchart.CHART_FILE_NAME_INDEX_FOREIGN
import com.kfitchart.CHART_FILE_NAME_INDEX_FOREIGN_FUTURE
import com.kfitchart.CHART_FILE_NAME_STOCK_DOMESTIC
import com.kfitchart.CHART_FILE_NAME_STOCK_FOREIGN
import com.kfitchart.GE_PACKET_PERIOD_DAILY
import com.kfitchart.GE_PACKET_PERIOD_MINUTE
import com.kfitchart.GE_PACKET_PERIOD_MONTHLY
import com.kfitchart.GE_PACKET_PERIOD_TICK
import com.kfitchart.GE_PACKET_PERIOD_WEEKLY
import com.kfitchart.KFIT_KEY_EXCHANGE_ID
import com.kfitchart.KFIT_KEY_ISIN_CODE
import com.kfitchart.KFIT_KEY_IS_INDEX
import com.kfitchart.KFIT_KEY_STOCK_ID
import com.kfitchart.KfitDRChartView
import com.kfitchart.entity.KfitBaseChartRequestEntity
import com.kfitchart.entity.KfitBaseChartResponseEntity
import com.kfitchart.entity.KfitConnectionStateEntity
import com.kfitchart.entity.KfitDomesticFinancialChart
import com.kfitchart.entity.KfitDomesticFinancialChartRequestEntity
import com.kfitchart.entity.KfitDomesticFinancialChartResponseEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChart
import com.kfitchart.entity.KfitDomesticMrkIndcChartRequestEntity
import com.kfitchart.entity.KfitDomesticMrkIndcChartResponseEntity
import com.kfitchart.entity.KfitFluctuationFlagType
import com.kfitchart.entity.KfitForeignRightChartRequestEntity
import com.kfitchart.entity.KfitForeignRightChartResponseEntity
import com.kfitchart.entity.KfitIndexType
import com.kfitchart.entity.KfitMarketStatusEntity
import com.kfitchart.entity.KfitPageEntity
import com.kfitchart.entity.KfitPresentPriceStreamEntity
import com.kfitchart.entity.KfitRightDateTimeEntity
import com.kfitchart.entity.KfitStockPresentEntity
import com.kfitchart.entity.KfitTradeDataRequestEntity
import com.kfitchart.entity.KfitTradeDataResponseEntity
import com.kfitchart.entity.KfitTradingListItemEntity
import com.kfitchart.repository.KfitChartRepository
import com.kfitchart.screen.landscape.KfitChartLandscapeActivity
import com.kfitchart.tiara.KfitTiaraTracker
import drfn.chart.util.COMUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Hashtable
import java.util.Locale
import java.util.Vector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

@Deprecated("KfitChartTabFragment 사용, 히스토리 관리를 위해 남겨둡니다")
class KfitChartViewFragment : Fragment() {

    private var _binding: KfitFragmentMainBinding? = null
    private val binding: KfitFragmentMainBinding get() = _binding!!

    private val viewModel: KfitChartRepository by lazy {
        KfitChartRepositoryImpl(
            KfitPayRemoteDataSource()
        )
    }

    private val tracker: KfitTiaraTracker by lazy {
        KfitTiaraTrackerImpl()
    }

    /**
     *  여기 아래 부터 코드 머지
     */
    private var chartViewKfit: KfitDRChartView? = null

    //    private var bottomDialogFragment: ConfigViewController? = null
    var presentInfoKfit = KfitStockPresentEntity(
        code = "",
        name = "",
        type = "",
        open = "",
        high = "",
        low = "",
        close = "",
        sign = KfitFluctuationFlagType.INIT, // 2023.03.17 by SJW - 차트 상단 대비기호 변경
        change = "",
        chgRate = "",
        volume = "",
        market = "",
        decimal = "",
        timezone = "",
        capitalization = null,
        realCode = null,
        nextKey = listOf(),
        isMarketLive = false,
    )
    private var chartDataListKfit: KfitBaseChartResponseEntity = KfitBaseChartResponseEntity(
        page = KfitPageEntity(
            hasMore = false,
            nextKey = listOf(),
        ),
        list = listOf(),
        data = null,
    )

    private var tradeDataListKfit: KfitTradeDataResponseEntity = KfitTradeDataResponseEntity(
        tradingList = listOf(),
    )

    private var domesticMrkIndcDataListKfit: KfitDomesticMrkIndcChartResponseEntity =
        KfitDomesticMrkIndcChartResponseEntity(
            page = KfitPageEntity(
                hasMore = false,
                nextKey = listOf(),
            ),
            dataList = listOf(),
        )

    private lateinit var mGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    private val stockId by lazy { arguments?.getString(KFIT_KEY_STOCK_ID) ?: "" }
    private val exchangeId by lazy { arguments?.getString(KFIT_KEY_EXCHANGE_ID) ?: "" }
    private val isinCode by lazy { arguments?.getString(KFIT_KEY_ISIN_CODE) ?: "" }
    private val isIndex by lazy { arguments?.getBoolean(KFIT_KEY_IS_INDEX, false) ?: false }

    var chartFileName = CHART_FILE_NAME_STOCK_DOMESTIC
    private var tradeChartPeriodWeekFindIndex = 0

    // 2023.07.24 by SJW - 차트 디폴트 조회 개수 변경 200 -> 300 >>
    var m_nDefReqCnt = 300 // 차트 디폴트 조회 갯수 설정

    // 2023.07.24 by SJW - 차트 디폴트 조회 개수 변경 200 -> 300 <<
    var marketNextKeyList = Hashtable<String, Any>()

    // 2021.09.28 by lyk - kakaopay - 네비게이션 바 높이 체크 >>
    var navH = 0
    var preNavH = -1
    var isFirstLoad = true
    // 2021.09.28 by lyk - kakaopay - 네비게이션 바 높이 체크 <<

    var stateOfChartConfigLoad = false

    var m_bMarketLive = false // 장마감 상태 전역변수

    var isSettingMode = false
    var m_isCross = false
    private var viewCreated = false // 2023.07.25 by SJW - Analytics Log Crash 수정
    private var tooltipLayout: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = KfitFragmentMainBinding.inflate(
        inflater,
        container,
        false,
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        viewModel.init()

        viewCreated = true // 2023.07.25 by SJW - Analytics Log Crash 수정

        initChartView()

        // 구독 리스닝
        subscribeMessageFlow()
        subscribeConnectionStateFlow()
        subscribeAveragePriceFlow()
    }

    // /// data 호출 영역 start ///////////
    private fun requestPresentInfo() {
        view?.let { // 2023.10.31 by SJw - crashlytics 로그 오류 수정(fragmentview null check)
            viewLifecycleOwner.lifecycleScope.launch {
                kotlin.runCatching {
                    viewModel.stockPresentInfo().also {
                        presentInfoKfit = it
                    }
                }.onFailure {
//                log.info("stockPresentInfo responseError:" + it.toString())
                }.onSuccess {
//                log.info("stockPresentInfo" + presentInfoKfit.toString())

                    // presentInfoKfit를 받아온 후 파일 저장소 이름을 적용한다. >>
                    // 해외지수
                    val isIndexType: KfitIndexType? = isIndexType()
                    if (isIndexChart() && isIndexType != null && isIndexType.ordinal == KfitIndexType.INDEX.ordinal && !isDomestic()) {
                        chartFileName = CHART_FILE_NAME_INDEX_FOREIGN
                    }
                    // 해외선물
                    else if (isIndexChart() && isIndexType != null && isIndexType.ordinal == KfitIndexType.FUTURE.ordinal && !isDomestic()) {
                        chartFileName = CHART_FILE_NAME_INDEX_FOREIGN_FUTURE
                    }
                    // 국내지수
                    else if (isIndexChart()) { // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가
                        chartFileName = CHART_FILE_NAME_INDEX_DOMESTIC
                    }
                    // 미국종목
                    else if (it.market.toLowerCase(Locale.ENGLISH) == "usd") {
                        chartFileName = CHART_FILE_NAME_STOCK_FOREIGN
                    }

                    // 2023. 04. 17 by hyh - [임시] 에러 수정
//                mGlobalLayoutListener.onGlobalLayout() //2023.06.14 by SJW - lifecycle 생명주기 수정

                    chartViewKfit?.setLoadChart(chartFileName)
                    // presentInfoKfit를 받아온 후 파일 저장소 이름을 적용한다. <<
                    m_bMarketLive = it.isMarketLive
                    // 2024.01.16 by SJW - 설정 관련 코드 처리 >>
//                    requestBaseChartData(false)
                    loadPeriodPrev()
                    // 2024.01.16 by SJW - 설정 관련 코드 처리 <<
                }
            }
        }
    }

    private fun requestBaseChartData(isReturnNextkey: Boolean) {
        view?.let { // 2023.10.31 by SJw - crashlytics 로그 오류 수정(fragmentview null check)
            viewLifecycleOwner.lifecycleScope.launch {
                kotlin.runCatching {
                    // API request-response
                    var periodtype = chartViewKfit?.period ?: "2"
                    var divisionUnit = "1"
                    when (periodtype) {
                        GE_PACKET_PERIOD_TICK -> {
                            periodtype = "TICK"
                            divisionUnit = chartViewKfit?.unit ?: "1"
                        }

                        GE_PACKET_PERIOD_MINUTE -> {
                            periodtype = "MIN"
                            divisionUnit = chartViewKfit?.unit ?: "1"
                        }

                        GE_PACKET_PERIOD_DAILY -> {
                            periodtype = "DAY"
                            divisionUnit = "1"
                        }

                        GE_PACKET_PERIOD_WEEKLY -> {
                            periodtype = "WEEK"
                            divisionUnit = "1"
                        }

                        GE_PACKET_PERIOD_MONTHLY -> {
                            periodtype = "MONTH"
                            divisionUnit = "1"
                        }
                    }

                    val nextKey: List<String>?
                    if (isReturnNextkey) {
                        nextKey = getNextKey()
                    } else {
                        nextKey = null
                    }

                    val inputData = KfitBaseChartRequestEntity(
                        isAdjustedData = true,
                        divisionUnit = divisionUnit, // period 정보(앱단) //  봉단위 (1~60)
                        chartDivision = periodtype, // period 유닛 // 봉단위 숫자  (TICK, MIN, DAY, WEEK, MONTH)
//                chartDivision = "5MINUTE", // "5MINUTE", "30TICK" //2023.03.15 by SJW - 애프터마켓 testapi 추가
                        count = m_nDefReqCnt,
                        nextKey = nextKey, // 최초요청때는 null
                        // 두번째 요청떄부터 presentData.nextKey?
                    )

                    viewModel.baseChart(inputData)
                }.onFailure {
                }.onSuccess {
                    updateOutputData(presentInfoKfit, it)
                }
            }
        }
    }

    // 매수매도 표시
    private suspend fun requestTradeData(state: Boolean): KfitTradeDataResponseEntity {
//        log.info("requestTradeData()" + state)

//        val startDate = chartDataListKfit.list.minByOrNull { it.dateTime }?.dateTime ?: 0
//        val lastDate = chartDataListKfit.list.maxByOrNull { it.dateTime }?.dateTime ?: 0

        return kotlin.runCatching {
            if (chartViewKfit?.chartView == null) {
                chartViewKfit?.chartView = COMUtil._mainFrame
            }

            var reqFirstDate = "getStartDate"
            if (state) {
                reqFirstDate = "getEndDate"
            }
//            val firstTradedDate = this.getMethod(reqFirstDate, "") // 2021.07.09 by lky
            val firstTradedDate = this.getMethod(reqFirstDate, "")?.let { method ->
                if (method is String) {
                    method
                } else {
                    ""
                }
            } ?: ""
//            val lastTradedDate = this.getMethod("getEndDate", "")

            // 2021.11.19 by lyk - kakaopay - 마지막 조회 날짜를 오늘 날짜로 처리 -->
            val now: Long = System.currentTimeMillis()
            val date = Date(now)
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
            val lastTradedDate = dateFormat.format(date)
            // 2021.11.19 by lyk - kakaopay - 마지막 조회 날짜를 오늘 날짜로 처리 <--

            val request = KfitTradeDataRequestEntity(
                firstTradedDate = firstTradedDate,
                lastTradedDate = lastTradedDate,
            )

            val returnData = viewModel.tradeData(request)
//            log.info("tradeData outputdata:$returnData")

            this.tradeDataListKfit = returnData

            returnData
        }.onFailure {
//            log.info("tradeData responseError:$it")
        }.getOrDefault(KfitTradeDataResponseEntity(tradingList = listOf()))
    }

    // 기업캘린더 데이터 1 합산은 reqCorporateCalendar
    suspend fun requestDomesticFinancialData(): KfitDomesticFinancialChartResponseEntity {
        val request = KfitDomesticFinancialChartRequestEntity(
            nextKey = null,
        )

        var returnData = KfitDomesticFinancialChartResponseEntity(
            page = KfitPageEntity(
                hasMore = false,
                nextKey = listOf(),
            ),
            dataList = listOf(),
        )

        kotlin.runCatching {
            returnData = viewModel.domesticFinancialChartChart(request)
        }.onFailure {
        }

        return returnData
    }

    // 기업캘린더 데이터 2
    suspend fun requestForeignRightData(): KfitForeignRightChartResponseEntity {
//        log.info("requestForeignRightData()")
        val request = KfitForeignRightChartRequestEntity(
            nextKey = null,
        )

        var returnData = KfitForeignRightChartResponseEntity(
            dataList = listOf(),
            page = KfitPageEntity(
                hasMore = false,
                nextKey = listOf(),
            ),
        )

        kotlin.runCatching {
            returnData = viewModel.foreignRightChart(request)
//            log.info("foreignRightChart outputdata:$returnData")
        }.onFailure {
//            log.info("foreignRightChart responseError:$it")
        }

        return returnData
    }

    // 보조지표(시장지표)  (외국인 비율, 외국인/기관/개인 추세, 기관 순매수)
    suspend fun requestDomesticMrkIndcChart(
        indicatorDivision: String,
        investorDivision: String,
        reqCnt: Int,
    ): KfitDomesticMrkIndcChartResponseEntity {
//        log.info("requestDomesticMrkIndcChart()")
        val nextKey: List<String>? = null
//        if (isReturnNextkey)
//        nextKey = getDomesticMrkIndcChartNextKey()
//        else
//            nextKey = null

        val request = KfitDomesticMrkIndcChartRequestEntity(
            indicatorDivision = indicatorDivision, // [FOREIGN, INVESTOR, CREDIT]
            investorDivision = investorDivision, // 지표구분이 INVESTOR 인 경우만 [ALL, FRN, IND, IST]
            count = reqCnt, // TODO 카운트 세팅 부탁드립니다.
            nextKey = nextKey,
        )

        var returnData = KfitDomesticMrkIndcChartResponseEntity(
            page = KfitPageEntity(
                hasMore = false,
                nextKey = listOf(),
            ),
            dataList = listOf(),
        )

        kotlin.runCatching {
            returnData = viewModel.domesticMrkIndcChart(request)
        }.onFailure {
        }

        this.domesticMrkIndcDataListKfit = returnData

        return returnData
    }

    // // 차트뷰에서 호출하는 부분 ////
    fun month(eventDate: String): Array<String> {
        val sYear = eventDate.substring(0, 4)
        val dYear = sYear.toInt()
        var sMon = eventDate.substring(4, 6)
        val dMon = sMon.toInt()
        val dDay = eventDate.substring(6, 8).toInt()

        val cal = Calendar.getInstance()

        cal[dYear, dMon - 1] = dDay
        val endDt = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        System.out.println(cal.getActualMaximum(Calendar.DAY_OF_MONTH))

        val monthDay = arrayOf(sYear + sMon + "01", sYear + sMon + endDt.toString())
        return monthDay
    }

    /** * 특정 날짜의 같은 한 주간의 날짜 범위 * @param eventDate ex) 2020-10-10 * */
    fun week(eventDate: String): Array<String> {
//        val dateArray = eventDate.split("-").toTypedArray()
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
        val sf = SimpleDateFormat("yyyyMMdd")
        // 해당 주차의 첫째 날짜
        val startDt = sf.format(cal.time)
        // 해당 주차의 마지막 날짜 지정
        cal.add(Calendar.DAY_OF_MONTH, 6)
        // 해당 주차의 마지막 날짜
        val endDt = sf.format(cal.time)

//        log.info("kfits 특정 날짜 = [$eventDate] >> 시작 날짜 = [$startDt], 종료 날짜 = [$endDt]")
        val weekday = arrayOf(startDt, endDt)
        return weekday
    }

    var tradeDataList: MutableList<KfitTradingListItemEntity> =
        listOf<KfitTradingListItemEntity>().toMutableList() // 매매내역 차트 사용 데이터

    var mrkIndicDataList: MutableList<KfitDomesticMrkIndcChart> =
        listOf<KfitDomesticMrkIndcChart>().toMutableList() // 시장지표 차트 사용 데이터
    var mrkIndicTotalDataList: MutableList<KfitDomesticMrkIndcChart> =
        listOf<KfitDomesticMrkIndcChart>().toMutableList() // 시장지표 다음키 처리를 위한 전체 데이터

    //    var bTradeData_Conti = false
    var bMrkIndicData_Conti = false

    fun reqTradeData(state: Boolean) {
        // 지수일 경우 매수, 매도 표시 x
        if (isIndex) return

        // 2023.08.22 by SJW - 자료일자 빈 값으로 넘어올시 API Request 하지않게 수정 >>
        var reqFirstDate = "getStartDate"
        if (state == true) {
            reqFirstDate = "getEndDate"
        }
        val firstTradedDate = this@KfitChartViewFragment.getMethod(reqFirstDate, "")

        if (firstTradedDate == "") return
        // 2023.08.22 by SJW - 자료일자 빈 값으로 넘어올시 API Request 하지않게 수정 <<

        viewLifecycleOwner.lifecycleScope.launch {
            var tradeDataResponseEntity = async {
                requestTradeData(state)
            }.await()

            tradeDataList = tradeDataResponseEntity.tradingList.toMutableList()
            tradeDataList =
                tradeDataList.sortedWith(compareByDescending({ it.orderedDate })).toMutableList()

            // nextKey 데이터 처리 >>
//            if(state == false && tradeDataResponseEntity.page.hasMore == true && tradeDataResponseEntity.page.nextKey != null && tradeDataResponseEntity.page.nextKey.size
//             > 0) { //next data
//                bTradeData_Conti = true
//
//                //nextKey가 있으면 다음 데이터 조회
//                reqTradeData(false)
//            } else {
//                bTradeData_Conti = false
//            }
//
//            if(bTradeData_Conti == true) {
//                tradeDataList = tradeTotalDataList
//                val recvAddTradeData = tradeDataResponseEntity.tradingList
//                if(recvAddTradeData.size > 0) {
//                    tradeDataList.addAll(recvAddTradeData)
//                    tradeTotalDataList = tradeDataList
//                } else {
//                    bTradeData_Conti = false
//                    return@launch
//                }
//            } else {
//                if(state == true) { //매매내역 실시간 당일건 처리
//                    tradeDataList = tradeDataResponseEntity.tradingList.toMutableList()
//                } else {
//                    tradeTotalDataList = tradeDataResponseEntity.tradingList.toMutableList()
//                    tradeDataList = tradeTotalDataList
//                }
//            }
            // nextKey 데이터 처리 <<

            var buySellPrice: String = ""
            val regexParamSplitString = "^"
            val regexDataSplitString = ";"
            var totalCount: Int = 0
            val typeBuy = "0"
            val typeSell = "1"

            // Step1 : 주기별로 같은 날짜 또는 같은 N주기 시간동안의 데이터를 합산 계산 (매도, 매수 각각 합산건수, 합산금액 계산)
            // 합산 데이터로 재생성
            var sData = ""
            var bsCnt = 0

            var preDate = ""
            var preTime = ""
            var preStartWeekDay = ""
            var preEndWeekDay = ""
            var preStartOfMonthStr = ""
            var preEndOfMonthStr = ""

            val count = tradeDataList.count()

            val sumTradeChartList = java.util.HashMap<String, SumItem>()

            var sumSellQuantity = 0.0
            var sumBuyQuantity = 0.0
            var sumSellAveragePrice = 0.0
            var sumBuyAveragePrice = 0.0

            var buyCnt = 0
            var sellCnt = 0

            tradeChartPeriodWeekFindIndex = 0

            var isSumMode = false // 합산 계산 진행중 여부

            chartViewKfit?.let { chartViewKfit ->
                for ((index, chartItem) in tradeDataList.withIndex()) {
                    var date = chartItem.orderedDate

                    // 주간 데이터 생성시 필요
                    var monday = ""
                    var friday = ""

                    // 월간 데이터 생성시 필요
                    var startOfMonthStr = ""
                    var endOfMonthStr = ""

                    // 월요일, 금요일 찾기
                    if (chartViewKfit.period == "3") { // 주간 데이터 합산
                        val weekday = week(date)
                        monday = weekday.get(0)
                        friday = weekday.get(1)

                        if (date < preStartWeekDay) {
                            if (buyCnt > 0) {
                                sumBuyAveragePrice /= buyCnt.toDouble()
                            }
                            if (sellCnt > 0) {
                                sumSellAveragePrice /= sellCnt.toDouble()
                            }

                            val fixDay =
                                getPeriodDateForTradeChart(preStartWeekDay, chartViewKfit.period)
                            if (fixDay != null) {
                                sumTradeChartList.put(
                                    fixDay,
                                    SumItem(
                                        sellQuantity = sumSellQuantity,
                                        buyQuantity = sumBuyQuantity,
                                        sellAveragePrice = sumSellAveragePrice,
                                        buyAveragePrice = sumBuyAveragePrice,
                                    ),
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
//
                        if (preEndWeekDay == "") {
                            preEndWeekDay = friday
                        }
                    } else if (chartViewKfit.period == "4") { // 월간 데이터 합산
                        // - 월 첫날 계산, - 월 마지막날 계산
                        val monthday = month(date)
                        startOfMonthStr = monthday.get(0)
                        endOfMonthStr = monthday.get(1)

//                        let calendar = Calendar(identifier: .gregorian)
//                        var components = calendar.dateComponents([.year, .month], from: dateTime)
//                        // - 월 마지막날 계산
//                        var componentsLast = DateComponents()
//                        componentsLast.month = 1
//                        componentsLast.second = -1
//
//                        var startOfMonth = calendar.date(from: components)!
//                        var endOfMonth = Calendar(identifier: .gregorian).date(byAdding: componentsLast, to: startOfMonth)!
//
//                        startOfMonthStr = df.string(from: startOfMonth)
//                        endOfMonthStr = df.string(from: endOfMonth)
//
//                        if(date < startOfMonthStr) { //비교날짜가 월첫째 날짜보다 작으면 이전달 값으로 설정
//                            componentsLast.month = -1
//                            startOfMonth = calendar.date(from: components)!
//                            endOfMonth = Calendar(identifier: .gregorian).date(byAdding: componentsLast, to: startOfMonth)!
//
//                            startOfMonthStr = df.string(from: startOfMonth)
//                            endOfMonthStr = df.string(from: endOfMonth)
//                        }
//
//                    if(startOfMonthStr <= date && date > endOfMonthStr) {
//                        friday = df.string(from: Date.curDay(dateTime).next(.saturday))
//                    } else {
//                        friday = df.string(from: Date.curDay(dateTime).previous(.saturday))
//                    }

                        if (date < preStartOfMonthStr) {
                            if (buyCnt > 0) {
                                sumBuyAveragePrice /= buyCnt.toDouble()
                            }
                            if (sellCnt > 0) {
                                sumSellAveragePrice /= sellCnt.toDouble()
                            }

                            val fixDay =
                                getPeriodDateForTradeChart(preStartOfMonthStr, chartViewKfit.period)
                            if (fixDay != null) {
                                // 중복날짜 합산처리 >>
//                                    if(sumTradeChartList.containsKey(fixDay)) {
//                                        val exSum = sumTradeChartList.get(fixDay)
//
//                                        if (exSum != null) {
//                                            sumTradeChartList.put(
//                                                fixDay, SumItem(
//                                                    sellQuantity = exSum.sellQuantity + sumSellQuantity,
//                                                    buyQuantity = exSum.buyQuantity + sumBuyQuantity,
//                                                    sellAveragePrice = sumSellAveragePrice,
//                                                    buyAveragePrice = sumBuyAveragePrice
//                                                )
//                                            )
//                                        }
//                                    }
//                                    //중복날짜 합산처리 <<
//                                    else {
                                sumTradeChartList.put(
                                    fixDay,
                                    SumItem(
                                        sellQuantity = sumSellQuantity,
                                        buyQuantity = sumBuyQuantity,
                                        sellAveragePrice = sumSellAveragePrice,
                                        buyAveragePrice = sumBuyAveragePrice,
                                    ),
                                )
//                                    }
                            }

                            // 사용값 초기화
                            sumSellQuantity = 0.0
                            sumBuyQuantity = 0.0
                            sumSellAveragePrice = 0.0
                            sumBuyAveragePrice = 0.0

                            preStartOfMonthStr = ""
                            preEndOfMonthStr = ""

                            isSumMode = false
                            buyCnt = 0
                            sellCnt = 0

                            preStartOfMonthStr = ""
                            preEndOfMonthStr = ""
                        }

                        if (preStartOfMonthStr == "") {
                            preStartOfMonthStr = startOfMonthStr
                        }
//
                        if (preEndOfMonthStr == "") {
                            preEndOfMonthStr = endOfMonthStr
                        }
                    }
                    // -------

                    var sellQuantity = 0.0
                    var buyQuantity = 0.0
                    var sellAveragePrice = 0.0
                    var buyAveragePrice = 0.0

                    try {
                        sellQuantity = chartItem.sellQuantity.toDouble()
                    } catch (e: Exception) {
                    }

                    try {
                        buyQuantity = chartItem.buyQuantity.toDouble()
                    } catch (e: Exception) {
                    }

                    try {
                        sellAveragePrice = chartItem.sellAveragePrice.toDouble()
                    } catch (e: Exception) {
                    }

                    try {
                        buyAveragePrice = chartItem.buyAveragePrice.toDouble()
                    } catch (e: Exception) {
                    }

                    if (chartViewKfit.period == "2") { // 일주기
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
                                sumTradeChartList.put(
                                    date,
                                    SumItem(
                                        sellQuantity = sumSellQuantity,
                                        buyQuantity = sumBuyQuantity,
                                        sellAveragePrice = sumSellAveragePrice,
                                        buyAveragePrice = sumBuyAveragePrice,
                                    ),
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
                            sumTradeChartList.put(
                                date,
                                SumItem(
                                    sellQuantity = sumSellQuantity,
                                    buyQuantity = sumBuyQuantity,
                                    sellAveragePrice = sumSellAveragePrice,
                                    buyAveragePrice = sumBuyAveragePrice,
                                ),
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
                    } else if (chartViewKfit.period == "3") { // 주간 데이터 합산
                        if (date >= preStartWeekDay && date <= preEndWeekDay) {
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
                                    chartViewKfit.period,
                                )
                                if (fixDay != null) {
                                    sumTradeChartList.put(
                                        fixDay,
                                        SumItem(
                                            sellQuantity = sumSellQuantity,
                                            buyQuantity = sumBuyQuantity,
                                            sellAveragePrice = sumSellAveragePrice,
                                            buyAveragePrice = sumBuyAveragePrice,
                                        ),
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
                                getPeriodDateForTradeChart(preStartWeekDay, chartViewKfit.period)
                            if (fixDay != null) {
                                sumTradeChartList.put(
                                    fixDay,
                                    SumItem(
                                        sellQuantity = sumSellQuantity,
                                        buyQuantity = sumBuyQuantity,
                                        sellAveragePrice = sumSellAveragePrice,
                                        buyAveragePrice = sumBuyAveragePrice,
                                    ),
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
                    } else if (chartViewKfit.period == "4") { // 월간 데이터 합산
                        if ((date >= preStartOfMonthStr && date <= preEndOfMonthStr)) {
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
                                    chartViewKfit.period,
                                )
                                if (fixDay != null) {
                                    // 중복날짜 합산처리 >>
//                                    if(sumTradeChartList.containsKey(fixDay)) {
//                                        val exSum = sumTradeChartList.get(fixDay)
//
//                                        if (exSum != null) {
//                                            sumTradeChartList.put(
//                                                fixDay, SumItem(
//                                                    sellQuantity = exSum.sellQuantity + sumSellQuantity,
//                                                    buyQuantity = exSum.buyQuantity + sumBuyQuantity,
//                                                    sellAveragePrice = sumSellAveragePrice,
//                                                    buyAveragePrice = sumBuyAveragePrice
//                                                )
//                                            )
//                                        }
//                                    }
//                                    //중복날짜 합산처리 <<
//                                    else {
                                    sumTradeChartList.put(
                                        fixDay,
                                        SumItem(
                                            sellQuantity = sumSellQuantity,
                                            buyQuantity = sumBuyQuantity,
                                            sellAveragePrice = sumSellAveragePrice,
                                            buyAveragePrice = sumBuyAveragePrice,
                                        ),
                                    )
//                                    }
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
                                getPeriodDateForTradeChart(preStartOfMonthStr, chartViewKfit.period)
                            if (fixDay != null) {
                                // 중복날짜 합산처리 >>
//                                    if(sumTradeChartList.containsKey(fixDay)) {
//                                        val exSum = sumTradeChartList.get(fixDay)
//
//                                        if (exSum != null) {
//                                            sumTradeChartList.put(
//                                                fixDay, SumItem(
//                                                    sellQuantity = exSum.sellQuantity + sumSellQuantity,
//                                                    buyQuantity = exSum.buyQuantity + sumBuyQuantity,
//                                                    sellAveragePrice = sumSellAveragePrice,
//                                                    buyAveragePrice = sumBuyAveragePrice
//                                                )
//                                            )
//                                        }
//                                    }
//                                    //중복날짜 합산처리 <<
//                                    else {
                                sumTradeChartList.put(
                                    fixDay,
                                    SumItem(
                                        sellQuantity = sumSellQuantity,
                                        buyQuantity = sumBuyQuantity,
                                        sellAveragePrice = sumSellAveragePrice,
                                        buyAveragePrice = sumBuyAveragePrice,
                                    ),
                                )
//                                    }
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
//                    preTime = time

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

                if (state == false) { // 조회
                    if (totalCount > 0) {
                        buySellPrice = totalCount.toString() + buySellPrice
                        chartViewKfit.setBuySellPriceData(buySellPrice)
//                        log.info("kfit tradeData:$buySellPrice")
                    } else {
                        chartViewKfit.setBuySellPriceData("")
                    }
                } else { // 당일건 조회
                    // 2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건 처리 테스트 >>
//                    var newItem = "2;0^20210809^68000^10;1^20210809^78000^2"
                    if (totalCount > 0) {
                        chartViewKfit.setBuySellPriceSpecificDay(buySellPrice)
//                        log.info(" > kfit RealTradeData:$buySellPrice")
                    }
                    // 2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건 처리 테스트 <<
                }
            }
        }
    }

    fun getPeriodDateForTradeChart(sValue: String, sParam: String): String? {
        val arrDates = chartViewKfit?.getMethod("getStringData", "자료일자")
        val dates: Array<String>
        if (arrDates != null) {
            dates = chartViewKfit?.getMethod("getStringData", "자료일자") as Array<String>
        } else {
            return null
        }
//        val dates: Array<String> = chartViewKfit?.getMethod("getStringData", "자료일자") as Array<String>
        if (sParam == "3") { // 차트 시세의 주봉 날짜와 동기화
            val cmpWeekday = week(sValue)
            var nIndex = tradeChartPeriodWeekFindIndex
            val dataCount = dates.size
            for (i in nIndex until dataCount) {
                nIndex = dataCount - i - 1
                val sChartDate = dates[nIndex]
                val chartWeekday = week(sChartDate)
                // 두 날짜가 같은 week의 범위안에 있는지 비교
                if ((cmpWeekday.get(0) == chartWeekday.get(0)) &&
                    (cmpWeekday.get(1) == chartWeekday.get(1))
                ) {
                    tradeChartPeriodWeekFindIndex = i
                    return sChartDate
                }
            }
        } else if (sParam == "4") { // 차트 시세의 월봉 날짜와 동기화
            val cmpMonthday = month(sValue)
            for (i in 0 until dates.size) {
                val sChartDate = dates[i]
                val chartMonthday = month(sChartDate)
                // 두 날짜가 같은 week의 범위안에 있는지 비교
                if ((cmpMonthday.get(0) == chartMonthday.get(0)) &&
                    (cmpMonthday.get(1) == chartMonthday.get(1))
                ) {
                    return sChartDate
                }
            }
        }

        return null
    }

    // 기업 캘린더 표시
    fun reqCorporateCalendar() {
        // 지수일 경우 기업 캘린더 표시 x
        if (isIndex) return

        viewLifecycleOwner.lifecycleScope.launch {
            // 차트탭 관련 코어 초기화 전 다른 탭 진입 시 MainFrame 초기화 에러로 방어코드 추가
            // 초기화 관련 화면 이탈 시 나타나는 이슈로 보여 runCatching 으로 무시하도록 함
            runCatching {
                val financialData: KfitDomesticFinancialChartResponseEntity
                val rightListData: KfitForeignRightChartResponseEntity

                // 국내 혹은 해외 구분 API 호출
                if (isDomestic()) {
                    financialData = async { requestDomesticFinancialData() }.await()
                    val dataList: List<KfitRightDateTimeEntity> = emptyList()
                    rightListData = KfitForeignRightChartResponseEntity(
                        dataList,
                        KfitPageEntity(false, emptyList()),
                    )
                } else {
                    rightListData = async { requestForeignRightData() }.await()
                    val dataList: List<KfitDomesticFinancialChart> = emptyList()
                    financialData = KfitDomesticFinancialChartResponseEntity(
                        KfitPageEntity(false, emptyList()),
                        dataList,
                    )
                }

                var badgeData: String = ""
                val regexParamSplitString = "^"
                val regexDataSplitString = ";"
                var totalCount: Int = 0
                val TYPE_FINANCIAL = "E"
                val TYPE_RIGHT = "D"
                val TYPE_BOTH = "DE"

                val equalList = financialData.dataList.filter {
                    it.dateTime in rightListData.dataList.map {
                        it.rightDateTime
                    }
                }.map { it.dateTime }

                // 실적발표
                if (financialData.dataList.isNotEmpty()) {
                    for (i in 0 until financialData.dataList.size) {
                        var dateTime = ""
                        if (financialData.dataList[i].dateTime in equalList) continue

                        dateTime =
                            chartViewKfit?.convertTimestampToDate(financialData.dataList[i].dateTime)
                                .toString()

                        val financialDate =
                            financialData.dataList[i].financialDate // financialDate YYYYMM

                        badgeData += regexDataSplitString + TYPE_FINANCIAL + regexParamSplitString +
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

                        var dateTime = ""

                        dateTime =
                            chartViewKfit?.convertTimestampToDate(rightListData.dataList[i].rightDateTime)
                                .toString()

                        var sCurrency = rightListData.dataList[i].dividendCurrency
                        if (sCurrency.toLowerCase(Locale.ENGLISH) == "usd") {
                            sCurrency = "$"
                        }

                        badgeData += regexDataSplitString + TYPE_RIGHT + regexParamSplitString +
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
                        var dateTime = ""
                        dateTime = chartViewKfit?.convertTimestampToDate(time)
                            .toString()

                        val financialDataList = financialData.dataList.filter { it.dateTime == time }
                        for (i in 0 until rightListData.dataList.size) {
                            if (rightListData.dataList[i].rightDateTime in equalList) {
                                var sCurrency = rightListData.dataList[i].dividendCurrency
                                if (sCurrency.toLowerCase(Locale.ENGLISH) == "usd") {
                                    sCurrency = "$"
                                }

                                badgeData += regexDataSplitString + TYPE_BOTH + regexParamSplitString +
                                        dateTime + regexParamSplitString +
                                        rightListData.dataList[i].right + regexParamSplitString +
                                        rightListData.dataList[i].dividendAmount + regexParamSplitString +
                                        sCurrency + regexParamSplitString +
                                        financialDataList.get(0).financialDate + regexParamSplitString +
                                        financialDataList.get(0).businessProfit + regexParamSplitString +
                                        financialDataList.get(0).businessProfitEarning + regexParamSplitString +
                                        financialDataList.get(0).businessProfitRateYoy + regexParamSplitString +
                                        financialDataList.get(0).businessProfitRateQoq + regexParamSplitString +
                                        financialDataList.get(0).netProfit + regexParamSplitString +
                                        financialDataList.get(0).netProfitEarning + regexParamSplitString +
                                        financialDataList.get(0).netProfitRateYoy + regexParamSplitString +
                                        financialDataList.get(0).netProfitRateQoq
                                totalCount++
                            }
                        }
                    }
                }

                badgeData = totalCount.toString() + badgeData

                chartViewKfit?.setCorporateCalendarData(badgeData)
            }
        }
    }

    // 시장지표
    fun reqMarketData(title: String?, reqCnt: Int) {
        // 지수일 경우 시장지표를 호출x
        if (isIndex) return

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
                var isDMarkets3 = false
                var isDMarkets4 = false
                var isDMarkets5 = false

                // indicator_division,investor_division 설정
                if (title == title0) {
                    indicatorDivision = "FOREIGN"
                } else if (title == title1) {
                    indicatorDivision = "INVESTOR"
                    investorDivision = "ALL"
                } else if (title == title2) {
                    indicatorDivision = "INVESTOR"
                    investorDivision = "IST"
                } else if (title == title3) {
                    indicatorDivision = "CREDIT"
                } else if (title == title4) {
                    indicatorDivision = "INVESTOR"
                    investorDivision = "IND"
                } else if (title == title5) {
                    indicatorDivision = "INVESTOR"
                    investorDivision = "FRN"
                }

                val domesticMrkIndcData =
                    async {
                        requestDomesticMrkIndcChart(
                            indicatorDivision,
                            investorDivision,
                            reqCnt,
                        )
                    }.await()

                val marketList = domesticMrkIndcData.dataList

                // nextKey 데이터 처리 >>
                //            if(domesticMrkIndcData.page.hasMore == true && domesticMrkIndcData.page.nextKey != null && domesticMrkIndcData.page.nextKey.size
                //                > 0) { //next data
                //                bMrkIndicData_Conti = true
                //
                //                //nextKey가 있으면 다음 데이터 조회
                // //                reqTradeData(false)
                //            } else {
                //                bMrkIndicData_Conti = false
                //            }
                //
                //            if(bMrkIndicData_Conti == true) {
                //                mrkIndicDataList = mrkIndicTotalDataList
                //                val recvAddTradeData = domesticMrkIndcData.dataList
                //                if(recvAddTradeData.size > 0) {
                //                    .addAll(recvAddTradeData)mrkIndicDataList
                //                    mrkIndicTotalDataList = mrkIndicDataList
                //                } else {
                //                    bMrkIndicData_Conti = false
                //                    return@launch
                //                }
                //            } else {
                //                mrkIndicTotalDataList = domesticMrkIndcData.dataList.toMutableList()
                //                mrkIndicDataList = mrkIndicTotalDataList
                //            }
                // nextKey 데이터 처리 <<

                val count = marketList.size
                val sTotCnt = chartViewKfit?.m_nTotCount
                var m_nTotCount = 0
                if (sTotCnt != null) {
                    m_nTotCount = sTotCnt.toInt()
                }

                val dDates = LongArray(m_nTotCount)
                val dMarkets = DoubleArray(m_nTotCount)
                val dMarkets1 = DoubleArray(m_nTotCount)
                val dMarkets2 = DoubleArray(m_nTotCount)
                val dMarkets3 = DoubleArray(m_nTotCount) // 신용 잔고율은 미사용
                val dMarkets4 = DoubleArray(m_nTotCount)
                val dMarkets5 = DoubleArray(m_nTotCount)

                if (count > 0) { // 빈데이터 리스트 넘어와서 다른 데이터 덮는걸 방지
                    chartViewKfit?.let { chartViewKfit ->
                        for (i in 0..(count - 1)) {
                            val item = marketList[i]

                            val string = chartViewKfit.convertTimestampToDate(item.dateTime)

                            if (m_nTotCount - i - 1 >= 0) {
                                dDates[m_nTotCount - i - 1] = string.toLong()
                                dMarkets[m_nTotCount - i - 1] = item.foreignRate.toDouble() // 외국인 보유율
                                if (dMarkets[m_nTotCount - i - 1] != 0.0) isDMarkets = true

                                dMarkets1[m_nTotCount - i - 1] =
                                    item.foreignNet.toDouble() // 외국인/기관/개인 추세에서 사용
                                if (dMarkets1[m_nTotCount - i - 1] != 0.0) isDMarkets1 = true

                                dMarkets2[m_nTotCount - i - 1] =
                                    item.institutionNet.toDouble() // 기관 순매수
                                if (dMarkets2[m_nTotCount - i - 1] != 0.0) isDMarkets2 = true

                                dMarkets4[m_nTotCount - i - 1] =
                                    item.individualNet.toDouble() // 개인 순매수
                                if (dMarkets4[m_nTotCount - i - 1] != 0.0) isDMarkets4 = true

                                dMarkets5[m_nTotCount - i - 1] =
                                    item.foreignNet.toDouble() // 외국인 순매수
                                if (dMarkets5[m_nTotCount - i - 1] != 0.0) isDMarkets5 = true
                            }
                        }

                        if (isDMarkets) {
                            chartViewKfit.setMarketData(
                                title0,
                                dDates,
                                dMarkets,
                                m_nTotCount,
                                !isDMarkets1,
                            )
                        }
                        if (isDMarkets1) {
                            chartViewKfit.setMarketData(
                                title1,
                                dDates,
                                dMarkets1,
                                m_nTotCount,
                                !isDMarkets2,
                            )
                        }
                        if (isDMarkets2) {
                            chartViewKfit.setMarketData(
                                title2,
                                dDates,
                                dMarkets2,
                                m_nTotCount,
                                !isDMarkets4,
                            )
                        }
                        if (isDMarkets4) {
                            chartViewKfit.setMarketData(
                                title4,
                                dDates,
                                dMarkets4,
                                m_nTotCount,
                                !isDMarkets5,
                            )
                        }
                        if (isDMarkets5) {
                            chartViewKfit.setMarketData(title5, dDates, dMarkets5, m_nTotCount, true)
                        }
                    }
                }
            }
        }
    }

    // /// data 호출 영역 end ///////////

    private fun getNextKey(): List<String>? {
        val nextKey: List<String>?
        if (chartDataListKfit.page.nextKey.isNullOrEmpty()) {
            nextKey = null
        } else {
            nextKey = chartDataListKfit.page.nextKey
        }
        return nextKey
    }

    private fun getDomesticMrkIndcChartNextKey(): List<String>? {
        val nextKey: List<String>?
        if (domesticMrkIndcDataListKfit.page.nextKey.isNullOrEmpty()) {
            nextKey = null
        } else {
            nextKey = domesticMrkIndcDataListKfit.page.nextKey
        }
        return nextKey
    }

    private fun initTooltip() = with(binding) {
        val layout = chartviewactivityParentLayout
        val inflater =
            (activity as AppCompatActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        tooltipLayout = inflater.inflate(R.layout.kfit_tooltip_view, null) as RelativeLayout
        tooltipLayout?.findViewById<TextView>(R.id.tooltip_textview)?.text =
            getString(R.string.tool_tip_text)
//        val tooltipTouchLayout = tooltipLayout.findViewById<FrameLayout>(R.id.tooltip_touchlayout)
        val tooltipView = tooltipLayout?.findViewById<FrameLayout>(R.id.tooltip_body)

        layout.addView(tooltipLayout)

        // 기본 사이즈 값
//        val viewP_width = COMUtil.getPixel(117).toInt()
        val viewP_width = resources.getDimension(R.dimen.kfit_tooltip_width)
        val viewP_height = resources.getDimension(R.dimen.kfit_tooltip_height)
        val viewPatchSize = resources.getDimension(R.dimen.kfit_tooltip_icon_right_margin) +
                (resources.getDimension(R.dimen.kfit_sub_item_icon) / 2) +
                (resources.getDimension(R.dimen.kfit_tooltip_arrow_size) / 2)

        val location: IntArray = intArrayOf(0, 0)
        ivChartHeaderSetting.getLocationOnScreen(location)
        var left = location[0] - viewP_width + viewPatchSize

        // 2023.11.07 by SJW - 페이앱과 툴팁 위치 동일하게 수정 >>
//        val top = location[1] - COMUtil.getPixel(205).toInt()
        val containerTopMarginDp = 17 // pixel 계산으로 dp 값이 완전 일치하지 않은 것 같음 (16 > 17)
        val top = chartHeaderlayout.y - COMUtil.getPixel(containerTopMarginDp).roundToInt()
        // 2023.11.07 by SJW - 페이앱과 툴팁 위치 동일하게 수정 <<
        // 2023.06.14 by SJW - "나만의 지표 설정하기" 문구 방향 수정 <<
        if (resources.displayMetrics.widthPixels >= 1768) { // 폴드 펼쳤을 때 화면
            left -= COMUtil.getPixel(4)
        } else if (resources.displayMetrics.widthPixels > 800 && resources.displayMetrics.widthPixels < 905) { // 폴드 접었을 때 화면
            left += COMUtil.getPixel(16)
        } else {
            left += COMUtil.getPixel(10)
        }
        // 2023.02.09 by SJW - "나만의 지표 설정하기" 툴팁 위치 안맞는 현산 수정 <<
        tooltipView?.x = left.toFloat()
        tooltipView?.y = top.toFloat()
        // 2023.12.01 by SJW - 툴팁 제거 액션 영역 수정 >>
//        tooltipTouchLayout.setOnClickListener {
        tooltipView?.setOnClickListener {
            hideTooltip()
        }
        // 2023.12.01 by SJW - 툴팁 제거 액션 영역 수정 <<
    }

    fun initChartView() {
        // chart construct
        this.chartViewKfit = KfitDRChartView(requireContext())
//        this.chartViewKfit?.setFragment(this)
//        if(isIndexChart()) { //2022.04.11 by lyk - kakaopay - 지수상세 차트추가
//            chartFileName = CHART_FILE_NAME_INDEX
//        } else if (presentInfoKfit.market.toLowerCase(Locale.ENGLISH) == "usd") {
//            chartFileName = CHART_FILE_NAME_OVERSEAS
//        }
//
//        chartViewKfit?.setLoadChart(chartFileName)

        // 초기사용자 툴팁을 위한 preference
        val pref = (activity as AppCompatActivity).getSharedPreferences(
            "kfit_isFirst",
            Activity.MODE_PRIVATE,
        )
        // 새로운 차트 영역 생성 시작 지점 (뷰가 그려진 이후 동작)

        // 차트 리사이즈 왜 하는거지?

        val chartlayout = binding.chartlayout
        binding.chartHeaderlayout.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (m_isCross == false && !isFirstLoad && chartViewKfit != null && chartlayout != null && chartlayout.height > 0) {
                if (chartlayout.layoutParams != null) {
                    chartlayout.post {
                        chartViewKfit?.resizeChart(chartlayout, right, top)
                    }
                }
            }
        }
        mGlobalLayoutListener =
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    removeOnGlobalLayoutListener(chartlayout.viewTreeObserver, this)
                    // 2024.01.16 by SJW - 설정 관련 코드 처리 >>
//                    if (chartlayout != null && chartlayout.height > 0 && chartlayout.isAttachedToWindow) {
                    if (chartlayout != null && chartlayout.height > 0 && isFirstLoad) {
                        // 2024.01.16 by SJW - 설정 관련 코드 처리 <<
                        createChart(
                            chartlayout,
                            chartlayout.measuredWidth,
                            chartlayout.measuredHeight,
                            presentInfoKfit,
                        )
                        // 데이터 통신 // 앱통신으로 대체됨.
//                        requestBaseChartData() // 중복되는 요청 제거

                        // 차트상단 (60틱...) 헤더뷰 init
                        runCatching {
                            initChartHeader() // 차트에서 period 값을 가져와서 데이터를 요청하는 구조여서 createChart이후로 동작.
                        }
                        // 최초 사용자에게 도움말 표시
                        if (pref.getBoolean("kfit_isFirst", false) == false) {
                            // 최초실행
                            initTooltip()
//                            pref.edit().putBoolean("kfit_isFirst", true).apply()
                        }
                        // 2021.09.28 by lyk - kakaopay - 내비게이션 바 높이 체크 >>
                        isFirstLoad = false
                        // 2021.09.28 by lyk - kakaopay - 내비게이션 바 높이 체크 <<
                    }
                }
            }

        chartlayout.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
//         강제로 globallayout호출시
//        chartlayout.viewTreeObserver.dispatchOnGlobalLayout()
//        mGlobalLayoutListener.onGlobalLayout()
    }

    private fun setTiaraTracker() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                tracker.pageView(
                    "차트탭",
                    currencyId = presentInfoKfit.market,
                    stockId = presentInfoKfit.code,
                    stockName = presentInfoKfit.name,
                    stockType = presentInfoKfit.type,
                )
            }.onFailure {
//                log.info("setTiaraTracker responseError:$it")
            }
        }
    }

    /**
     * 주 차트 정보
     * private const val INDICATOR_PLINE = 10002
     * private const val INDICATOR_JBONG = 10000
     * private const val INDICATOR_JBONG_TRANSPARENCY = 19999
     * private const val INDICATOR_PNF = 20001
     * private const val INDICATOR_SWING = 20003
     * private const val INDICATOR_RENKO = 20004
     * private const val INDICATOR_KAGI = 20005
     */
    private fun getMainInfo(): Any {
        var rtnStr = ""
        var rtnList = Vector<String>()

        val selectChartTag: String =
            getMethod("getSelectedChartType", "") as String // 주 차트 정보

        if (selectChartTag == "10002") {
            rtnList.add("라인 차트")
        } else if (selectChartTag == "10000") {
            rtnList.add("캔들 차트")
        } else if (selectChartTag == "19999") {
            rtnList.add("투명 캔들 차트")
        } else if (selectChartTag == "20001") {
            rtnList.add("포인트 앤 피겨 차트")
        } else if (selectChartTag == "20003") {
            rtnList.add("스윙 차트")
        } else if (selectChartTag == "20004") {
            rtnList.add("렌코 차트")
        } else if (selectChartTag == "20005") {
            rtnList.add("카기 차트")
        }

        for (str in rtnList) {
            rtnStr += (str + ",")
        }
        rtnStr = rtnStr.removeSuffix(",")

        return rtnStr
    }

    // 표시 정보 리스트
    private fun getDisplayInfo(): String {
        var rtnStr = ""
        var rtnList = Vector<String>()

        // 평단가 설정 유무
        val strAvgBuyState = this.getMethod("getAvgBuyState", "")
        if (strAvgBuyState != null && strAvgBuyState as Boolean) {
            rtnList.add("구매평균가격 표시")
        }
        // 매수,매도 설정 유무 가져오기
        val strBuySellState = this.getMethod("getBuySellState", "")
        if (strBuySellState != null && strBuySellState as Boolean) {
            rtnList.add("구매/판매 표시")
        }

        // 기업 캘린더 기능 설정값 가져오기
        val strCorporateCalendarState = this.getMethod("getCorporateCalendarState", "")
        if (strCorporateCalendarState != null && strCorporateCalendarState as Boolean) {
            rtnList.add("기업 캘린더 뱃지 표시")
        }

        // 이동평균선 기능 설정값 가져오기
        val strMovingAverageLineState = this.getMethod("getMovingAverageLineState", "")
//        if (strMovingAverageLineState != null && strMovingAverageLineState as Boolean) {
//            rtnList.add("기업 캘린더 뱃지 표시")
//        }

        // 추세선 설정 유무 가져오기
        val strAutoTrendWaveLineState = this.getMethod("getAutoTrendWaveLine", "")
        if (strAutoTrendWaveLineState != null && strAutoTrendWaveLineState as Boolean) {
            rtnList.add("추세선")
        }

        // 지지선/저항선 설정 유무 가져오기
        val strSupportResistanceLineState = this.getMethod("getSupportResistanceLine", "")
        if (strSupportResistanceLineState != null && strSupportResistanceLineState as Boolean) {
            rtnList.add("지지선/저항선")
        }

        // 이동평균선
//        val strMA5 = this.getMethod("getVisibleGraph", "이평 5")
        if (strMovingAverageLineState != null && strMovingAverageLineState as Boolean) {
            rtnList.add("이동평균선")
        }

//        // 5일 이평
//        val strMA5 = this.getMethod("getVisibleGraph", "이평 5")
//        if (strMA5 != null && strMA5 as Boolean) {
//            rtnList.add("5일 이동평균선")
//        }
//
//        // 20일 이평
//        val strMA20 = this.getMethod("getVisibleGraph", "이평 20")
//        if (strMA20 != null && strMA20 as Boolean) {
//            rtnList.add("20일 이동평균선")
//        }
//
//        // 60일 이평
//        val strMA60 = this.getMethod("getVisibleGraph", "이평 60")
//        if (strMA60 != null && strMA60 as Boolean) {
//            rtnList.add("60일 이동평균선")
//        }
//
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//        // 10일 이평
//        val strMA10 = this.getMethod("getVisibleGraph", "이평 10")
//        if (strMA10 != null && strMA10 as Boolean) {
//            rtnList.add("10일 이동평균선")
//        }
//
//        // 120일 이평
//        val strMA120 = this.getMethod("getVisibleGraph", "이평 120")
//        if (strMA120 != null && strMA120 as Boolean) {
//            rtnList.add("120일 이동평균선")
//        }
//
//        // 200일 이평
//        val strMA200 = this.getMethod("getVisibleGraph", "이평 200")
//        if (strMA200 != null && strMA200 as Boolean) {
//            rtnList.add("200일 이동평균선")
//        }
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
//
//        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
//        // 240일 이평
//        val strMA240 = this.getMethod("getVisibleGraph", "이평 240")
//        if (strMA240 != null && strMA240 as Boolean) {
//            rtnList.add("240일 이동평균선")
//        }
//        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<

        var tagList: Vector<String>? = this.getGraphTagList()
        if (tagList != null) {
            for (tag in tagList) {
                if (tag == "40004") {
                    rtnList.add("그물망")
                } else if (tag == "40003") {
                    rtnList.add("일목균형표")
                } else if (tag == "40001") {
                    rtnList.add("볼린저밴드")
                } else if (tag == "11000") {
                    rtnList.add("매물대")
                    // 2023.06.27 by SJW - 엔벨로프 지표 추가 >>
                } else if (tag == "40002") {
                    rtnList.add("엔벨로프")
                }
                // 2023.06.27 by SJW - 엔벨로프 지표 추가 <<
            }
        }

        for (str in rtnList) {
            rtnStr += (str + ",")
        }
        rtnStr = rtnStr.removeSuffix(",")

        return rtnStr
    }

    // 보조지표 이름 리스트
    private fun getSubDataInfo(): String {
        var rtnStr = ""
        var rtnList: Vector<String>
        rtnList = this.getSubGraphList()!!

        for (str in rtnList) {
            rtnStr += (str + ",")
        }
        rtnStr = rtnStr.removeSuffix(",")

        return rtnStr
    }

    fun isNavigationBar(): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    private fun subscribeAveragePriceFlow() {
        viewModel.subscribeAveragePriceFlow().flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.STARTED,
        ).onEach {
            // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 >>
            // 2022.04.11 by lyk - kakaopay - 지수상세 차트인 경우 평단가 처리 안함
            if (!isIndexChart()) {
                chartViewKfit?.setAvgBuyPriceData(it.averagePrice)
            } else {
                chartViewKfit?.setAvgBuyPriceData("")
            }
            // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 <<

            // 매수,매도 표시 기능 사용중인 경우 데이터 요청
            if (chartViewKfit?.buySellPriceFunc == "1") {
                reqTradeData(true) // false:조회, true:당일건 조회시 설정값
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun subscribeMessageFlow() {
        viewModel.subscribeMessageFlow().flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.STARTED,
        ).onEach {
            when (it) {
                is KfitPresentPriceStreamEntity -> {
                    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 예상지수일 경우 차트 실시간 처리하지 않음 >>
                    if (it.isIndex.not() || it.isExpected.not()) {
                        chartViewKfit?.updateRealData(it)
                    }
                    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 예상지수일 경우 차트 실시간 처리하지 않음 <<
                }

                is KfitMarketStatusEntity -> {
                    m_bMarketLive = it.isMarketLive
                }

                else -> {
//                            log.info("$it")
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun subscribeConnectionStateFlow() {
        viewModel.subscribeConnectionStateFlow().flowWithLifecycle(
            lifecycle = viewLifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.RESUMED,
        ).onEach {
            when (it) {
                is KfitConnectionStateEntity.OnConnectionOpening -> {
                    // 연결중(재연결시도)
                }

                is KfitConnectionStateEntity.OnConnectionOpened -> {
                    // 연결됨(재연결성공)
                    requestPresentInfo()
                }

                is KfitConnectionStateEntity.OnConnectionFail -> {
                    // 재시도 10번후 연결실패
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun removeOnGlobalLayoutListener(
        observer: ViewTreeObserver,
        listener: ViewTreeObserver.OnGlobalLayoutListener,
    ) {
        observer.removeOnGlobalLayoutListener(listener)
    }

    private fun changeDP(value: Int): Int {
        val displayMetrics = resources.displayMetrics
        val dp = Math.round(value.toFloat() * displayMetrics.density)
        return dp
    }

    private fun dp2px(dp: Float): Int {
        val r = resources
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
        return Math.round(px)
    }

    fun updateOutputData(
        drPresentInfoKfit: KfitStockPresentEntity,
        chartDataListKfit: KfitBaseChartResponseEntity,
    ) {
        this.chartDataListKfit = chartDataListKfit
        this.chartViewKfit?.updateOutputData(drPresentInfoKfit, chartDataListKfit)

        // 2021.12.15 by lyk - kakaopay - 차트 첫 조회 후 차트 설정값을 가져올 수 있어 최초 한번만 처리되도록 함 >>
        if (!this.stateOfChartConfigLoad) {
            // 2021.12.15 by lyk - kakaopay - tiara log >>
            if (this.isResumed) {
                setTiaraTracker()
                this.stateOfChartConfigLoad = true
            }
            // 2021.12.15 by lyk - kakaopay - tiara log <<
        }
        // 2021.12.15 by lyk - kakaopay - 차트 첫 조회 후 차트 설정값을 가져올 수 있어 최초 한번만 처리되도록 함 <<
    }

    fun refreshUpdateOutputData() {
        requestBaseChartData(isReturnNextkey = false)
    }

    fun requestBaseChartNextData() {
        requestBaseChartData(isReturnNextkey = true)
    }

    /**
     * 차트 상단 일주월분틱
     */
    val baseTranX = 20
    fun initChartHeader() = with(binding) {
        // 2023.02.06 by SJW - Fold에서 아이콘 위치가 맞지 않는 현상 수정 >>
        val windowRect = getWindowRect()
        val mDisWidth = windowRect.width()
        val configuration = resources.configuration
        if (mDisWidth >= 1768 && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) { // 폴드 펼쳤을 때 화면
            ivChartCrossSetting.apply {
                layoutParams.width = COMUtil.getPixel_W(72f).toInt()
                scaleType = ImageView.ScaleType.CENTER
            }
            ivChartHeaderSetting.apply {
                layoutParams.width = COMUtil.getPixel_W(72f).toInt()
                scaleType = ImageView.ScaleType.CENTER
            }
        } else if (resources.displayMetrics.widthPixels > 800 && resources.displayMetrics.widthPixels < 905 && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) { // 폴드 접었을 때 화면
            ivChartCrossSetting.apply {
                layoutParams.width = COMUtil.getPixel_W(32f).toInt()
                scaleType = ImageView.ScaleType.CENTER
            }
            ivChartHeaderSetting.apply {
                layoutParams.width = COMUtil.getPixel_W(32f).toInt()
                scaleType = ImageView.ScaleType.CENTER
            }
        }
//        //2023.02.06 by SJW - Fold에서 확장/축소 시 아이콘 위치가 맞지 않는 현상 수정 <<
        llChartHeaderDayParent.setOnClickListener {
            clearChartOptionViewSelectEvent()
            llChartHeaderDay.isSelected = true
            tvChartHeaderDay.isSelected = true
            setPeriodType(GE_PACKET_PERIOD_DAILY)

            runCatching {
                tracker.clickFilterDay()
            }.onFailure {
                Log.d("티아라 : 차트탭 필터 일 선택", it.toString())
            }
        }
        llChartHeaderWeekParent.setOnClickListener {
            clearChartOptionViewSelectEvent()
            llChartHeaderWeek.isSelected = true
            tvChartHeaderWeek.isSelected = true
            setPeriodType(GE_PACKET_PERIOD_WEEKLY)

            runCatching {
                tracker.clickFilterWeek()
            }.onFailure {
                Log.d("티아라 : 차트탭 필터 주 선택", it.toString())
            }
        }
        llChartHeaderMonthParent.setOnClickListener {
            clearChartOptionViewSelectEvent()
            llChartHeaderMonth.isSelected = true
            tvChartHeaderMonth.isSelected = true
            setPeriodType(GE_PACKET_PERIOD_MONTHLY)

            runCatching {
                tracker.clickFilterMonth()
            }.onFailure {
                Log.d("티아라 : 차트탭 필터 월 선택", it.toString())
            }
        }

        // 초기값 설정
        if (getPeriodUnit(GE_PACKET_PERIOD_TICK).isNullOrBlank()) {
            val pref_peroid_unit =
                (activity as AppCompatActivity).getSharedPreferences(
                    "kfit_period_tick_unit",
                    Activity.MODE_PRIVATE,
                )
            pref_peroid_unit.edit().putString("kfit_period_tick_unit", "60").apply()
        }

        if (getPeriodUnit(GE_PACKET_PERIOD_MINUTE).isNullOrBlank()) {
            val pref_peroid_unit =
                (activity as AppCompatActivity).getSharedPreferences(
                    "kfit_period_minute_unit",
                    Activity.MODE_PRIVATE,
                )
            pref_peroid_unit.edit().putString("kfit_period_minute_unit", "10").apply()
        }

        // 분틱바텀시트
        llChartHeaderTicks.setOnClickListener {
            if (!llChartHeaderTicks.isSelected) {
                clearChartOptionViewSelectEvent()
                llChartHeaderTicks.isSelected = true
                tvChartHeaderTicks.isSelected = true

                if (getPeriodUnit(GE_PACKET_PERIOD_TICK).isNullOrBlank()) {
                    val units: Array<String> = arrayOf("1", "3", "5", "10", "30", "60")
                    // setPeriodUnit은 showChartCycleOptionBottomSheet()메서드 안에 정의
                    showChartCycleOptionBottomSheet("tick", units)
                } else {
                    setPeriodUnit(
                        tvChartHeaderTicks.text.toString()
                            .replace(getString(R.string.period_tick), ""),
                        GE_PACKET_PERIOD_TICK,
                    )
                }

                runCatching {
                    tracker.clickFilterTick(tvChartHeaderTicks.text.toString())
                }.onFailure {
                    Log.d("티아라 : 차트탭 필터 틱 선택", it.toString())
                }
            } else {
                // 이미 눌러져있는 상태에서는 무조건 선택팝업
                val units: Array<String> = arrayOf("1", "3", "5", "10", "30", "60")
                showChartCycleOptionBottomSheet("tick", units)
            }
        }
        llChartHeaderMinutes.setOnClickListener {
            if (!llChartHeaderMinutes.isSelected) {
                clearChartOptionViewSelectEvent()
                llChartHeaderMinutes.isSelected = true
                tvChartHeaderMinutes.isSelected = true

                if (getPeriodUnit(GE_PACKET_PERIOD_MINUTE).isNullOrBlank()) {
                    val units: Array<String> = arrayOf("1", "3", "5", "10", "15", "30", "60")
                    // setPeriodUnit은 showChartCycleOptionBottomSheet()메서드 안에 정의
                    showChartCycleOptionBottomSheet("minutes", units)
                } else {
                    setPeriodUnit(
                        tvChartHeaderMinutes.text.toString()
                            .replace(getString(R.string.period_minute), ""),
                        GE_PACKET_PERIOD_MINUTE,
                    )
                }

                runCatching {
                    tracker.clickFilterMinute(tvChartHeaderMinutes.text.toString())
                }.onFailure {
                    Log.d("티아라 : 차트탭 필터 분 선택", it.toString())
                }
            } else {
                // 이미 눌러져있는 상태에서는 무조건 선택팝업
                val units: Array<String> = arrayOf("1", "3", "5", "10", "15", "30", "60")
                showChartCycleOptionBottomSheet("minutes", units)
            }
        }

        ivChartHeaderSetting.setOnClickListener {
            // 2023.12.13 by SJW - 기획 변경 요청(설정 버튼을 누르기 전까지는 툴팁 문구 노출되도록 수정) >>
            val pref = (activity as AppCompatActivity).getSharedPreferences(
                "kfit_isFirst",
                Activity.MODE_PRIVATE,
            )
            if (pref.getBoolean("kfit_isFirst", false) == false) {
                pref.edit().putBoolean("kfit_isFirst", true).apply()
            }
            // 2023.12.13 by SJW - 기획 변경 요청(설정 버튼을 누르기 전까지는 툴팁 문구 노출되도록 수정) <<
            showBottomDialog()

            runCatching {
                tracker.clickFilterSetting()
            }.onFailure {
                Log.d("티아라 : 차트탭 필터 설정 선택", it.toString())
            }
        }

        ivChartCrossSetting.setOnClickListener {
            m_isCross = true
            startActivity(
                KfitChartLandscapeActivity.orientationIntent(
                    context = requireContext(),
                    stockId = stockId,
                    exchangeId = exchangeId,
                    isinCode = isinCode,
                    isIndex = isIndex,
                ),
            )

            runCatching {
                tracker.clickFilterHorizontal()
            }.onFailure {
                Log.d("티아라 : 차트탭 필터 가로보기 선택", it.toString())
            }
        }
        // 2023.01.19 by SJW - 가로모드 설정 추가 <<
//        loadPeriodPrev() //2024.01.16 by SJW - 설정 관련 코드 처리
    }

    private fun loadPeriodPrev() = with(binding) {
        val pref_peroid_mode = (activity as AppCompatActivity).getSharedPreferences(
            "kfit_period_mode",
            AppCompatActivity.MODE_PRIVATE,
        )
        var tickUnit = getPeriodUnit(GE_PACKET_PERIOD_TICK)
        var minUnit = getPeriodUnit(GE_PACKET_PERIOD_MINUTE)

        if (tickUnit.isNullOrEmpty()) {
            ("60" + getString(R.string.period_tick)).also {
                tvChartHeaderTicks.text = it
                tickUnit = it
            }
        } else {
            (tickUnit + getString(R.string.period_tick)).also { tvChartHeaderTicks.text = it }
        }

        if (minUnit.isNullOrEmpty()) {
            ("10" + getString(R.string.period_minute)).also {
                tvChartHeaderMinutes.text = it
                minUnit = it
            }
        } else {
            (minUnit + getString(R.string.period_minute)).also { tvChartHeaderMinutes.text = it }
        }
        clearChartOptionViewSelectEvent() // 2024.01.16 by SJW - 설정 관련 코드 처리
        // 앱실행시 기존에 저장된 period 값 적용
        when (pref_peroid_mode.getString("kfit_period_mode", GE_PACKET_PERIOD_DAILY)) {
            GE_PACKET_PERIOD_DAILY -> {
//                clearChartOptionViewSelectEvent() //2024.01.16 by SJW - 설정 관련 코드 처리
                llChartHeaderDay.isSelected = true
                tvChartHeaderDay.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_DAILY)
            }

            GE_PACKET_PERIOD_WEEKLY -> {
//                clearChartOptionViewSelectEvent() //2024.01.16 by SJW - 설정 관련 코드 처리
                llChartHeaderWeek.isSelected = true
                tvChartHeaderWeek.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_WEEKLY)
            }

            GE_PACKET_PERIOD_MONTHLY -> {
//                clearChartOptionViewSelectEvent() //2024.01.16 by SJW - 설정 관련 코드 처리
                llChartHeaderMonth.isSelected = true
                tvChartHeaderMonth.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_MONTHLY)
            }

            GE_PACKET_PERIOD_TICK -> {
                llChartHeaderTicks.isSelected = true
                tvChartHeaderTicks.isSelected = true
                tickUnit?.let { setPeriodUnit(it, GE_PACKET_PERIOD_TICK) }
            }

            GE_PACKET_PERIOD_MINUTE -> {
                llChartHeaderMinutes.isSelected = true
                tvChartHeaderMinutes.isSelected = true
                minUnit?.let {
                    setPeriodUnit(it, GE_PACKET_PERIOD_MINUTE)
                }
            }

            else -> {
//                clearChartOptionViewSelectEvent() //2024.01.16 by SJW - 설정 관련 코드 처리
                llChartHeaderDay.isSelected = true
                tvChartHeaderDay.isSelected = true
                setPeriodType(GE_PACKET_PERIOD_DAILY)
            }
        }
    }

    /**
     * 분틱 선택 초기화
     */
    private fun clearChartOptionViewSelectEvent() = with(binding) {
        llChartHeaderDay.isSelected = false
        tvChartHeaderDay.isSelected = false

        llChartHeaderWeek.isSelected = false
        tvChartHeaderWeek.isSelected = false

        llChartHeaderMonth.isSelected = false
        tvChartHeaderMonth.isSelected = false

        llChartHeaderTicks.isSelected = false
        tvChartHeaderTicks.isSelected = false

        llChartHeaderMinutes.isSelected = false
        tvChartHeaderMinutes.isSelected = false
    }

    var dialog: BottomSheetDialog? = null
    var dialogHorz: PopupWindow? = null // 2023.01.19 by SJW - 가로모드 설정 추가

    @SuppressLint("ClickableViewAccessibility")
    private fun showChartCycleOptionBottomSheet(
        type: String,
        units: Array<String>,
    ) = with(binding) {
        val configuration = resources.configuration
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (dialog == null) {
                dialog = BottomSheetDialog(requireContext(), R.style.kfit_AppBottomSheetDialogTheme)
                dialog!!.getBehavior().state =
                    BottomSheetBehavior.STATE_EXPANDED // 2022.06.15 by lyk - kakaopay - 전체크기로 보이도록 수정
            }
            if (dialog!!.isShowing) {
                return@with
            }
        } else {
            if (dialogHorz == null) {
                dialogHorz = PopupWindow((activity as AppCompatActivity))
            }
            if (dialogHorz!!.isShowing) {
                return@with
            }
        }

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.isMotionEventSplittingEnabled = false

        // 상단뷰...
        val inflater =
            (activity as AppCompatActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val headerLayout =
                inflater.inflate(R.layout.kfit_header_cycleoption_bottomsheet, null) as LinearLayout
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
        }

        var bScroll = false // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정
        // 아이템뷰...
        for (text in units) {
            // 2023.01.19 by SJW - 가로모드 설정 추가 >>
            val itemLayout = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                inflater.inflate(
                    R.layout.kfit_cell_cycleoption_item_horizontal,
                    null,
                ) as LinearLayout
            } else {
                inflater.inflate(R.layout.kfit_cell_cycleoption_item, null) as LinearLayout
            }
            // 2023.01.19 by SJW - 가로모드 설정 추가 <<

            val divider = itemLayout.findViewById<View>(R.id.kfit_cell_divder)
            val circleView = itemLayout.findViewById<View>(R.id.cell_circle_view)
            val itemView = itemLayout.findViewById<View>(R.id.cell_body)
            val itemTextView = itemLayout.findViewById<TextView>(R.id.cell_textView)

            // 2023.01.19 by SJW - 가로모드 설정 추가 >>
//            divider.visibility = View.VISIBLE
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                divider.visibility = View.INVISIBLE
            } else {
                divider.visibility = View.VISIBLE
            }
            // 2023.01.19 by SJW - 가로모드 설정 추가 <<

            if (text.equals("60")) {
                divider.visibility = View.INVISIBLE
            }

            circleView.visibility = View.INVISIBLE
            if (type == "tick") {
                (text + getString(R.string.period_tick)).also { itemTextView.text = it }
            } else if (type == "minutes") {
                (text + getString(R.string.period_minute)).also { itemTextView.text = it }
            }
            if (type == "tick" && tvChartHeaderTicks.text == itemTextView.text) {
                itemView.isSelected = true
                circleView.visibility = View.VISIBLE
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 >>
                if (itemTextView.text.contains("60") || itemTextView.text.contains("30")) {
                    bScroll = true
                }
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 <<
            } else if (type == "minutes" && tvChartHeaderMinutes.text == itemTextView.text) {
                itemView.isSelected = true
                circleView.visibility = View.VISIBLE
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 >>
                if (itemTextView.text.contains("60") || itemTextView.text.contains("30")) {
                    bScroll = true
                }
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 <<
            }

            itemView.setOnTouchListener { _, event ->
                System.out.println("eventaction" + event.action.toString())
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        itemView.isSelected = true
                        System.out.println("eventaction_DOWN")
                    }

                    MotionEvent.ACTION_MOVE -> {
                        System.out.println("eventaction_MOVE")
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        System.out.println("eventaction_CANCEL:" + circleView.visibility)
                        if (circleView.visibility != View.VISIBLE) {
                            itemView.isSelected = !itemView.isSelected
                        }
                    }

                    MotionEvent.ACTION_OUTSIDE -> {
                        System.out.println("eventaction_OUTSIDE")
                    }

                    MotionEvent.ACTION_UP -> {
                        System.out.println("eventaction_UP")
                        runCatching {
                            when (type) {
                                "tick" -> {
                                    (text + getString(R.string.period_tick)).also {
                                        tvChartHeaderTicks.text = it
                                        tracker.clickFilterTick(it)
                                    }
                                }

                                "minutes" -> {
                                    (text + getString(R.string.period_minute)).also {
                                        tvChartHeaderMinutes.text = it
                                        tracker.clickFilterMinute(it)
                                    }
                                }

                                else -> Unit
                            }
                        }.onFailure {
                            Log.d("티아라 : 차트탭 필터 틱/분 변경", it.toString())
                        }

                        dialog?.dismiss()
                        dialogHorz?.dismiss()
                    }
                }
                true
            }
            layout.addView(itemLayout)
        }

        dialog?.setOnDismissListener {
            if (type == "tick") {
                setPeriodUnit(
                    tvChartHeaderTicks.text.toString().replace(getString(R.string.period_tick), ""),
                    GE_PACKET_PERIOD_TICK,
                )
            } else {
                setPeriodUnit(
                    tvChartHeaderMinutes.text.toString()
                        .replace(getString(R.string.period_minute), ""),
                    GE_PACKET_PERIOD_MINUTE,
                )
            }
        }
        // 2023.01.19 by SJW - 가로모드 설정 추가 >>
        dialogHorz?.setOnDismissListener {
            if (type == "tick") {
                setPeriodUnit(
                    tvChartHeaderTicks.text.toString().replace(getString(R.string.period_tick), ""),
                    GE_PACKET_PERIOD_TICK,
                )
            } else {
                setPeriodUnit(
                    tvChartHeaderMinutes.text.toString()
                        .replace(getString(R.string.period_minute), ""),
                    GE_PACKET_PERIOD_MINUTE,
                )
            }
        }
        // 2023.01.19 by SJW - 가로모드 설정 추가 <<
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val bottomLayout =
                inflater.inflate(R.layout.kfit_empty_view, null) as LinearLayout
            layout.addView(bottomLayout)

            dialog!!.setContentView(layout)
            dialog!!.setCanceledOnTouchOutside(true)

            dialog!!.create()
            dialog!!.show()
        }
        // 2023.01.19 by SJW - 가로모드 설정 추가 >>
        else {
            val scrollView = ScrollView(requireContext())

            scrollView.addView(
                layout,
                COMUtil.getPixel(140).toInt(),
                RelativeLayout.LayoutParams.WRAP_CONTENT,
            )
            scrollView.setBackgroundResource(R.drawable.kfit_img_round_popup)
            scrollView.setPadding(
                COMUtil.getPixel_W(4f).toInt(),
                COMUtil.getPixel_W(13f).toInt(),
                COMUtil.getPixel_W(4f).toInt(),
                COMUtil.getPixel_W(13f).toInt(),
            )

            dialogHorz!!.apply {
                setContentView(scrollView)
                setOutsideTouchable(true)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                showAsDropDown(
                    llChartHeaderMinutes,
                    COMUtil.getPixel_W(-4f).toInt(),
                    COMUtil.getPixel_H(4f).toInt(),
                )
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 >>
                if (bScroll) {
                    scrollView.post {
                        scrollView.fullScroll(View.FOCUS_DOWN)
                    }
                }
                // 2023.02.08 by SJW - 가로모드에서 분/틱 포커스 되지 않는 형상 수정 <<
            }
        }
        // 2023.01.19 by SJW - 가로모드 설정 추가 <<
    }
    // 2021. 4. 29  by hanjun.Kim - kakaopay - 분틱바텀시트 >>

    fun showBottomDialog() {
        isSettingMode = true
//        startActivity(
//            KfitConfigControllerActivity.newIntent2(
//                activity as AppCompatActivity,
//                this,
//            ),
//        )
        (activity as AppCompatActivity).overridePendingTransition(0, 0)
    }

    fun createChart(
        chartlayout: RelativeLayout,
        width: Int,
        height: Int,
        presentInfoKfit: KfitStockPresentEntity,
    ) {
        // 차트 영역 생성 시작 지점
        activity?.let {
            this.chartViewKfit?.createChart(
                activity as AppCompatActivity,
                chartlayout,
                width,
                height,
                // KfitDRChartView 초기화 방식 변경으로 미사용 (KfitChartTabFragment 참고)
//                object : KfitDRChartView.OnChartListener {
//                    override fun requestTradeData(isEndDate: Boolean) {}
//
//                    override fun requestCorporateCalendar() {}
//
//                    override fun requestChartSnapshot(isNextPage: Boolean) {}
//
//                    override fun requestMarketData(title: String, count: Int) {}
//                },
            )
        }
        // 차트 생성 끝 지점
    }

    // 추가된 지표 태그 리스트 반환
    fun getGraphTagList(): Vector<String>? {
        return chartViewKfit?.getGraphTagList()
    }

    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 >>
    fun isIndexChart(): Boolean {
        return presentInfoKfit.isIndex
    }

    fun isIndexType(): KfitIndexType? {
        return presentInfoKfit.indexType
    }
    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 <<

    // 2022.04.11 by lyk - kakaopay - 지수상세 국내해외 구분 >>
    fun isDomestic(): Boolean {
        var isDomestic: Boolean = true
        if (presentInfoKfit.market.toLowerCase(Locale.ENGLISH) == "krw") {
            isDomestic = true
        } else if (presentInfoKfit.market.toLowerCase(Locale.ENGLISH) == "usd") {
            isDomestic = false
        }

        return isDomestic
    }
    // 2022.04.11 by lyk - kakaopay - 지수상세 국내해외 구분 <<

    // 추가된 지표 이름 리스트 반환
    private fun getSubGraphList(): Vector<String>? {
        return this.chartViewKfit?.getSubGraphList()
    }

    fun getMethod(sValue: String, sParam: String): Any? {
        return chartViewKfit?.getMethod(sValue, sParam)
    }

    fun setPeriodType(periodtype: String) {
        chartViewKfit?.setPeriodType(periodtype)
//        requestPresentInfo()
        if (presentInfoKfit.code.isBlank()) {
//            requestPresentInfo() //2024.01.16 by SJW - 설정 관련 코드 처리
        } else {
            // 2023.07.25 by SJW - Analytics Log Crash 수정 >>
//            refreshUpdateOutputData()
            if (viewCreated) {
                refreshUpdateOutputData()
            }
        }
        // 2023.07.25 by SJW - Analytics Log Crash 수정 <<
        val pref_peroid_mode = (activity as AppCompatActivity).getSharedPreferences(
            "kfit_period_mode",
            Activity.MODE_PRIVATE,
        )
        pref_peroid_mode.edit().putString("kfit_period_mode", periodtype).apply()
    }

    fun setPeriodUnit(unit: String, periodtype: String) {
        chartViewKfit?.setPeriodType(periodtype)
        chartViewKfit?.setPeriodTime(unit)
//        requestPresentInfo()
        if (presentInfoKfit.code.isBlank()) {
//            requestPresentInfo() //2024.01.16 by SJW - 설정 관련 코드 처리
        } else {
            refreshUpdateOutputData()
        }
        // 2024.01.04 by sJW - 크래시로그 수정 >>
        val activity = activity as? AppCompatActivity

        activity?.let {
            // 2024.01.04 by sJW - 크래시로그 수정 <<
            val pref_peroid_mode = activity.getSharedPreferences(
                "kfit_period_mode",
                Activity.MODE_PRIVATE,
            )
            pref_peroid_mode.edit().putString("kfit_period_mode", periodtype).apply()

            if (periodtype == GE_PACKET_PERIOD_TICK) {
                val pref_peroid_unit =
                    activity.getSharedPreferences(
                        "kfit_period_tick_unit",
                        Activity.MODE_PRIVATE,
                    )
                pref_peroid_unit.edit().putString("kfit_period_tick_unit", unit).apply()
            } else if (periodtype == GE_PACKET_PERIOD_MINUTE) {
                val pref_peroid_unit =
                    activity.getSharedPreferences(
                        "kfit_period_minute_unit",
                        Activity.MODE_PRIVATE,
                    )
                pref_peroid_unit.edit().putString("kfit_period_minute_unit", unit).apply()
            }
        }
    }

    fun getPeriodUnit(periodtype: String): String? {
        if (periodtype == GE_PACKET_PERIOD_TICK) {
            return (activity as AppCompatActivity).getSharedPreferences(
                "kfit_period_tick_unit",
                Activity.MODE_PRIVATE,
            ).getString("kfit_period_tick_unit", "")
        } else {
            return (activity as AppCompatActivity).getSharedPreferences(
                "kfit_period_minute_unit",
                Activity.MODE_PRIVATE,
            ).getString(
                "kfit_period_minute_unit",
                "",
            )
        }
    }

    fun clearUnitSharedPreference() {
        (activity as AppCompatActivity).apply {
            getSharedPreferences("kfit_period_tick_unit", Activity.MODE_PRIVATE).edit().clear()
                .apply()
            getSharedPreferences("kfit_period_minute_unit", Activity.MODE_PRIVATE).edit().clear()
                .apply()
            getSharedPreferences("kfit_period_mode", AppCompatActivity.MODE_PRIVATE).edit().clear()
                .apply()
        }
    }

    fun setChartFunc(sValue: String) {
        chartViewKfit?.setChartFunc(sValue)
        // 항목 클릭때마다 변경사항이 적용될때 사용
    }

    fun setSelectChart(tag: Int) {
        chartViewKfit?.setSelectChart(tag)
    }

    // 2021. 5. 6  by hanjun.Kim - kakaopay - 차트저장을 한번만 발생되도록수정 >>
    fun saveChart() {
        chartViewKfit?.setSaveChart() // 차트 설정 변경시 저장 이벤트 호출 (추후 부하테스트 필요)
    }
    // 2021. 5. 6  by hanjun.Kim - kakaopay - 차트저장을 한번만 발생되도록수정 <<

    // 차트 초기화
    fun setInitChart() {
        chartViewKfit?.setInitChart()
        saveChart() // 차트 설정 변경시 저장 이벤트 호출 (추후 부하테스트 필요)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
//        loadPeriodPrev() // 2023.06.14 by SJW - lifecycle 생명주기 수정 //2024.01.16 by SJW - 설정 관련 코드 처리
        requestPresentInfo()
        // 2024.03.19 by SJW - 차트 scale 사라지는 현상 수정 >>
        if (chartViewKfit?.chartView != null) {
            COMUtil._mainFrame = chartViewKfit?.chartView
            COMUtil._neoChart = chartViewKfit?.chartView?.mainBase?.baseP?._chart
        }
        // 2024.03.19 by SJW - 차트 scale 사라지는 현상 수정 <<
        if (!isFirstLoad && !isSettingMode) {
            // 2021.09.28 by lyk - kakaopay - 내비게이션 바 높이 체크 >>
            val resources: Resources = (activity as AppCompatActivity).applicationContext.resources
            val resourceId: Int =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            navH = resources.getDimensionPixelSize(resourceId)
            if (preNavH != -1 && navH != preNavH && !isFirstLoad) { // 내비게이션 바 설정이 변경되면 처리

                (activity as AppCompatActivity).finish()
                (activity as AppCompatActivity).overridePendingTransition(0, 0)
                startActivity((activity as AppCompatActivity).getIntent())
                (activity as AppCompatActivity).overridePendingTransition(0, 0)
            }
            preNavH = navH
            // 2021.09.28 by lyk - kakaopay - 네비게이션 바 높이 체크 <<
//            chartViewKfit?.setLoadChart("reload")
        }
        // 2024.01.16 by SJW - 설정 관련 코드 처리 >>
        // 2024.01.05 by SJW - 차트 저장/로드 수정 >>
//        if (!isSettingMode) {
//            chartViewKfit?.setLoadChart("reload")
//        }
        // 2024.01.05 by SJW - 차트 저장/로드 수정 <<
        // 2024.01.16 by SJW - 설정 관련 코드 처리 <<
        // 2023.12.13 by SJW - 기획 변경 요청(설정 버튼을 누르기 전까지는 툴팁 문구 노출되도록 수정) >>
        val pref = (activity as AppCompatActivity).getSharedPreferences(
            "kfit_isFirst",
            Activity.MODE_PRIVATE,
        )
        // 2024.01.09 by SJW - 툴팁 미노출 조건 수정 요청 >>
        if (pref.getBoolean("kfit_isFirst", false) == true) {
            removeTooltip()
        } else {
            showTooltip()
        }
        // 2024.01.09 by SJW - 툴팁 미노출 조건 수정 요청 <<
        // 2023.12.13 by SJW - 기획 변경 요청(설정 버튼을 누르기 전까지는 툴팁 문구 노출되도록 수정) <<
        // 2024.01.05 by SJW - 차트 저장/로드 수정 >>
        if (isSettingMode) {
            saveChart()
        }
        // 2024.01.05 by SJW - 차트 저장/로드 수정 <<
        isSettingMode = false

        viewModel.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        saveChart()
        hideTooltip() // 2024.01.09 by SJW - 툴팁 미노출 조건 수정 요청
    }

    /**
     * 툴팁
     * initTooltip() onGloablLayoutChanged 1회
     * tooltipLayout 별도 inflate
     */

    // 2024.01.09 by SJW - 툴팁 미노출 조건 수정 요청 >>
    private fun showTooltip() {
        tooltipLayout?.let {
            it.visibility = View.VISIBLE
        }
    }

    private fun hideTooltip() {
        tooltipLayout?.let {
            it.visibility = View.GONE
        }
    }

    private fun removeTooltip() {
        tooltipLayout?.let {
            val layout = binding.chartviewactivityParentLayout
            layout.removeView(tooltipLayout)
            tooltipLayout = null
        }
    }
    // 2024.01.09 by SJW - 툴팁 미노출 조건 수정 요청 <<
    /**
     * 숫자 콤마(3자리콤마) 금액표기
     */
    private fun convertDecimalFormat(input: String): String {
        if (input.isEmpty()) {
            return ""
        }

        var headUnit = ""
        var tailUnit = ""
        var rtnString = ""

        var dec = DecimalFormat(headUnit + "###,###" + tailUnit)

        // 2022.04.18 by lyk - kakaopay - 지수상세 차트추가 >>
        if (presentInfoKfit.isIndex) {
            headUnit = ""
            tailUnit = ""

            // 2자리
            if (input.toDouble() > 0.0) {
                dec = DecimalFormat(headUnit + "###,###.00" + tailUnit)
                rtnString = dec.format(input.toDouble())
            } else {
                rtnString = "0"
            }
        }
        // 2022.04.18 by lyk - kakaopay - 지수상세 차트추가 <<
        else {
            if (presentInfoKfit.market.toLowerCase(Locale.ENGLISH) == "krw") {
                headUnit = ""
                tailUnit = "원"
                dec = DecimalFormat(headUnit + "###,###" + tailUnit)
                rtnString = dec.format(input.toDouble())
            } else if (presentInfoKfit.market.toLowerCase(Locale.ENGLISH) == "usd") {
                headUnit = "$"
                tailUnit = ""

                // 가격이 1달러 미만이면 4자리, 이상이면 2자리
                if (input.toDouble() >= 1.0) {
                    dec = DecimalFormat(headUnit + "###,###.00" + tailUnit)
                    rtnString = dec.format(input.toDouble())
                } else {
                    //                dec = DecimalFormat(headUnit+"#.0000"+tailUnit)
                    val value = String.format("%.4f", input.toDouble())
                    rtnString = headUnit + value + tailUnit
                }
            }
        }

        return rtnString
    }

    data class SumItem(
        var sellQuantity: Double,
        var buyQuantity: Double,
        var sellAveragePrice: Double,
        var buyAveragePrice: Double,
    )

    // 2023.02.08 by SJW - Android 31 대응. 윈도우 사이즈 가져오기 >>
    fun getWindowRect(): Rect {
        val wm = (activity as AppCompatActivity).windowManager
        var windowWidth = 0
        var windowHeight = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = wm.currentWindowMetrics
            val windowinsets = windowMetrics.windowInsets
            val insets =
                windowinsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars())
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            val rect = windowMetrics.bounds
            windowWidth = rect.width() - insetsWidth
            windowHeight = rect.height() - insetsHeight
        } else {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            windowWidth = size.x
            windowHeight = size.y
        }
        return Rect(0, 0, windowWidth, windowHeight)
    }
    // 2023.02.08 by SJW - Android 31 대응. 윈도우 사이즈 가져오기 <<

    // 2023.07.25 by SJW - Analytics Log Crash 수정 >>
    private fun dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }
    // 2023.07.25 by SJW - Analytics Log Crash 수정 <<

    override fun onDestroyView() {
        super.onDestroyView()

        // 2023.07.25 by SJW - Analytics Log Crash 수정 >>
        dismissDialog()
        viewCreated = false
        // 2023.07.25 by SJW - Analytics Log Crash 수정 <<

        // 2024.02.07 by SJW - 차트 메모리 관리 시점 변경 >>
//        chartViewKfit?.destroy()

        if (chartViewKfit?.chartView == COMUtil._mainFrame) {
            chartViewKfit?.destroy()
            COMUtil.apiView = null
            COMUtil._mainFrame = null
            COMUtil._neoChart = null
            COMUtil.apiLayout = null
        } else {
            chartViewKfit?.destroy()
        }
        // 2024.02.07 by SJW - 차트 메모리 관리 시점 변경 <<

        // 2021.09.28 by lyk - kakaopay - 내비게이션 바 높이 체크 >>
        isFirstLoad = true
        // 2021.09.28 by lyk - kakaopay - 내비게이션 바 높이 체크 <<

        this.stateOfChartConfigLoad = false

        _binding = null
    }
}
