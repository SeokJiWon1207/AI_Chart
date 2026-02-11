package com.kfitchart

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kakaopay.feature.stock.common.presentation.R
import com.kfitchart.entity.KfitBaseChartData
import com.kfitchart.entity.KfitBaseChartResponseEntity
import com.kfitchart.entity.KfitFluctuationFlagType
import com.kfitchart.entity.KfitPresentPriceStreamEntity
import com.kfitchart.entity.KfitStockPresentEntity
import drfn.UserProtocol
import drfn.chart.MainFrame
import drfn.chart.util.COMUtil
import drfn.chart.util.CoSys
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Hashtable
import java.util.Locale
import java.util.Vector

// import java.util.logging.Logger

class KfitDRChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr), UserProtocol {

    companion object {
//        val log = Logger.getLogger(KfitDRChartView::class.java.name)

        private const val INDICATOR_PAVERAGE = 40099
        private const val INDICATOR_PAVERAGE1 = 40010
        private const val INDICATOR_PAVERAGE2 = 40011
        private const val INDICATOR_PAVERAGE3 = 40012

        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
        private const val INDICATOR_PAVERAGE4 = 40013
        private const val INDICATOR_PAVERAGE5 = 40014
        private const val INDICATOR_PAVERAGE6 = 40015

        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
        private const val INDICATOR_PAVERAGE7 = 40016 // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청
        private const val AUTOTRENDWAVE_TYPE = 65001
        private const val SUPPORT_RESISTANCE_LINE = 65000
        private const val AVGBUY_PRICE = 65002
        private const val BUYSELL_PRICE = 65003
        private const val CORPORATE_CALENDAR = 65004
        private const val INDICATOR_CONFIG_VIEW = 65005
        private const val INDICATOR_VOLUME = 30000 // 2023.06.14 by SJW - "거래량" 이평 안그려지는 현상 수정
        private const val INDICATOR_VAVERAGE = 40007 // 2023.06.14 by SJW - "거래량" 이평 안그려지는 현상 수정
    }

    var chartLayout: RelativeLayout? = null
    var chartView: MainFrame? = null

    private var m_enPacketPeriod: ENPacketPeriodType = ENPacketPeriodType.GE_PACKET_PERIOD_DAILY
    private var m_isConti = false // 연속데이타 여부..

    private val m_sAssetName = ""
    public var period = "2"
    public var unit = "1"
    private var m_nDataCount = 200
    private var nKey = ""
    public var m_nTotCount = 0
    private val m_nMarketIndicator = 0
    var siseResult: StringBuffer? = // 시세 데이터 버퍼
        null
    var m_bIsMultiChart = false
    private val m_sPriceControls = ""
    private val m_sPriceData = ""
    private var m_sLoadFileName = ""
    private var m_enMarketCategory: ENMarketCategoryType =
        ENMarketCategoryType.GE_MARKET_CATE_STOCK
    private val m_bIsMigyul = false
    private val m_bIsOneBlock = false
    private var m_bIsLoadChart = false
    lateinit var strDates: Array<String>
    lateinit var dOpens: DoubleArray
    lateinit var dHighs: DoubleArray
    lateinit var dLows: DoubleArray
    lateinit var dCloses: DoubleArray
    lateinit var dVolumes: DoubleArray
    lateinit var dValues: DoubleArray
    lateinit var dRights: DoubleArray
    lateinit var dRightRates: DoubleArray
    lateinit var dSessionIds: DoubleArray // 2023.03.15 by SJW - 애프터마켓 추가

    private val m_nDeviceWidth = 0
    private var m_nDeviceHeight: Int = 0
    private val m_bUseOneQToolBar = false

    // 2017.07.05 by lyk - 전일,당일 차트 처리
    private val m_bIsFirstLine2Data = true // 1:전일 2:당일

    private val m_nPrevStartTime = -1
    private val m_nFixDataCount = -1
    // 2017.07.05 by lyk - 전일,당일 차트 처리 end

    // 2017.07.05 by lyk - 전일,당일 차트 처리 end
    private val m_bHideXScale = false
    private val m_sChartBgTransparent = "" // 2018. 12. 11 by hyh - 차트 배경 투명 기능 추가

    private val m_sHideChartTitle = ""

    private val m_strHighest = ""
    private val m_strLowest = ""
    private val m_strStandardValue = ""

    var avgBuyPriceFunc = "0" // 2022.04.11 by lyk - kakaopay - 구매평균값 설정 해제 후에도 차트 실행시 값이 보이는 현상 수정
    var m_avgBuyPrice = ""

    // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) >>
//    var buySellPriceFunc = ""
    var buySellPriceFunc = "1"

    //    private var corporateCalendarFunc = ""
    private var corporateCalendarFunc = "1"

    private var movingAverageLine = "1"

    // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) <<
    var sTimeZone = ""
    var timeZone = null
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val timeFormat = SimpleDateFormat("HHmmss")

    // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
    // 2019. 06. 07 by hyh - 멀티틱 조회 시점 몇 틱인지 알려줌 >>
    private val m_nTickCount = 0
    var bOpenData = false
    private var kfitBaseChartData: KfitBaseChartData? = null

    enum class ChartType {
        BASE_CHART,
        MINI_CHART,
        COMPARE_CHART, // 2012. 11. 21 마운틴차트 타입 추가
        LINEFILL_CHART,
        MINI_BONG_CHART,
        RATE_COMPARE_CHART, // 2013.07.31 >> 기준선 라인 차트 타입 추가 <<
        STANDARDLINE_CHART, // 2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가  <<
        SUB_CHART, // 2013.09.17 by LYH >> 패턴 그리기 추가. <<
        PATTERN,
        LINE_CHART,
        PIE_CHART,
        BAR_CHART,
        HORIZONTAL_STACK_CHART, // 2020.04.13 가로 Stack형 차트 수정 - hjw
        TODAY_LINE_CHART, // 2020.04.14 당일 라인차트 추가 - hjw
        LINE2_CHART,
        SPIDER_CHART,
        // 2021.02.15 재무 차트 추가
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Chart Market Infos
    enum class ENMarketCategoryType {
        GE_MARKET_CATE_STOCK,
        GE_MARKET_CATE_INDUSTRY,
        GE_MARKET_CATE_F_O_DOMESTIC,
        GE_MARKET_CATE_FUTURE_DOMESTIC,
        GE_MARKET_CATE_OPTION_DOMESTIC,
        GE_MARKET_CATE_FX,

        GE_MARKET_CATE_ACCRUE, // 투자자 누적차트
        GE_MARKET_CATE_FUND, // 펀드
        GE_MARKET_CATE_ELW,
        GE_MARKET_CATE_FUTURE_STOCK, // 개별주식선물
        GE_MARKET_CATE_FUTURE_COMMODITY, // 상품선물
        GE_MARKET_CATE_FOREIGN,
        // 주식 - 코스닥 (마켓타입 11)  (11)
    }

    enum class ENPacketPeriodType {
        // GE_PACKET_PERIOD_NONE			,
        GE_PACKET_PERIOD_TICK, // 		GE_PACKET_PERIOD_SECOND			,
        GE_PACKET_PERIOD_MINUTE,
        GE_PACKET_PERIOD_HOUR,
        GE_PACKET_PERIOD_DAILY,
        GE_PACKET_PERIOD_WEEKLY,
        GE_PACKET_PERIOD_MONTHLY, // 		GE_PACKET_PERIOD_QUARTER		,
        GE_PACKET_PERIOD_YEARLY,
    }

    // ChartType.BASE_CHART, ChartType.COMPARE_CHART
    private var m_chartType = ChartType.BASE_CHART
    private val m_bDayTick = false

    private val m_sCode = ""
    private val m_bHaveMA = false
    private val m_bHaveToolbar = true
    private val m_bResetPrice = false

    private var m_sCompCode = ""
    private var m_sCompName = ""
    private var m_sCompMarket = ""

    private var cmpArr: Vector<String>? = null

    private val m_bDataReverse = true // 서버에서 날짜가 거꾸로 올경우 사용

    // 2013. 1. 29  분틱주기 버튼 이름
    var periodName: String? = null

    private val m_dDivideValue = 0.0

    private val m_sLineColor = ""
    private val m_sTextColor = ""
    private val m_sHighLowSign = ""
    private var m_nDecimal = -1

    // 2015.01.08 by LYH >> 3일차트 추가
    private var m_b3DayChart = false

    // 2015. 1. 21 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기
    private val m_bUseDrawPriceLine = false

    // 2015. 2. 25 차트 상하한가 표시>>
    var m_strUpperLowerLimitPacketData = ""

    private var m_strUpperLowerCodes = ""
    private var m_strUpperLowerCounts = ""
    private var m_nUpperLowerIndex = -1
    private var m_nUpperLowerCountIndex = -1

    // 2015. 2. 25 차트 상하한가 표시<<
    private val m_nStandardAmount = 0

    // 2019.04.15 원터치 차트설정불러오기 추가 - lyj
    private var m_sOneTouchMode = ""
    private var m_sShowOneTouchMode = ""

    // 전체보기버튼, 알림버튼
    private val m_sOpenAllScreen = ""
    private val m_sOpenAlarmScreen = ""
    private val m_sAlarmValue = ""

    // 2019. 07. 30 by hyh - 봉개수가 데이터 최대치 넘지 못하도록 처리
    private var m_nRequestAddByNumber = 0

    // 2019. 08. 20 by hyh - 기준선 데이터 적용
    private val m_sBaselineData = ""

    // 2019. 05. 24 by hyh - 시세알람차트 외부에서 수치조회창 위치설정 기능 추가 <<
    // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
    private var m_sConn = "0" // 연속시세 유무

    private var m_sDay = "0" // 주간포함 유무

    private var m_sFloor = "0" // 본장 유무

    // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
    private var manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    private var info = manager!!.getRunningTasks(1)
    private var componentName = info.getOrNull(0)?.topActivity

    // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 >>
//    var topActivityName = componentName!!.shortClassName.substring(1)
//    var currentActivityName = topActivityName.split(".").last()
    var topActivityName = ""
    // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 <<
    // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<

    // 2024.05.09 by PETER - Fragment, Activity 참조 제거 & 인터페이스 추가 >>
    private var mainLayout: RelativeLayout? = null
    private var onChartListener: OnChartListener? = null

    interface OnChartListener {

        /**
         * 차트 스냅샷 호출
         * @param isNextPage 페이징 혹은 초깃값
         */
        fun requestChartSnapshot(isNextPage: Boolean = false)

        /**
         * 표시정보 : 구매/판매 표시
         * @param isEndDate 호출 기준 날짜
         */
        fun requestTradeData(isEndDate: Boolean)

        /**
         * 표시정보 : 기업캘린더 뱃지 표시
         */
        fun requestCorporateCalendar()

        /**
         * 보조지표 : 순매수, 추세, 비율
         */
        fun requestMarketData(title: String, count: Int)
    }
    // 2024.05.09 by PETER - Fragment, Activity 참조 제거 & 인터페이스 추가 <<

    var CHART_COLORS = arrayOf(
        intArrayOf(255, 60, 60),
        intArrayOf(0, 141, 255),
        intArrayOf(135, 146, 156),
        intArrayOf(207, 214, 220),
        intArrayOf(255, 113, 67),
        intArrayOf(138, 118, 255),
        intArrayOf(28, 213, 255),
        intArrayOf(68, 75, 82),
        intArrayOf(178, 186, 194),
        intArrayOf(252, 135, 5),
        intArrayOf(152, 116, 232),
        intArrayOf(3, 225, 193),
        intArrayOf(10, 121, 235),
        intArrayOf(19, 72, 186),
        intArrayOf(108, 72, 255),
        intArrayOf(175, 82, 222),
        intArrayOf(96, 106, 116),
        intArrayOf(178, 186, 194),
        intArrayOf(78, 196, 32),
        intArrayOf(58, 170, 224),
        intArrayOf(255, 124, 199),
        intArrayOf(254, 189, 0),
        intArrayOf(255, 113, 67),
    )

    // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    // 2019. 04. 01 by hyh - 차트 타이틀 적용 <<
    //    private void send_gdhtsSvc006() {
    //        //==================================================
    //        // #gdhtsSvc006 (MTS 차트환경설정 서버저장)
    //        //==================================================
    //        // SVR : 4
    //        //--------------------------------------------------
    //        // IN(S)
    //        // char userid [8];         /* 사용자ID */
    //        // char totstep [2];        /* 전체단계 (01) */
    //        // char curstep [2];        /* 현재단계 (01) */
    //        // char reclen [8];         /* contexts 크기 */
    //        // char contexts [3000000]; /* 내용 */
    //        //--------------------------------------------------
    //        int ret = 0;
    //
    //        String strUserId = SessionInfo.getUserID();
    //        String strContexts = COMUtil.getCloudData(getContext());
    //
    //        System.out.println("strContexts : " + strContexts);
    //
    //        try {
    //            strContexts = URLEncoder.encode(strContexts,"utf-8");
    //        }
    //        catch (Exception e) {
    //            LOG.e("Exception", e.toString());
    //            return;
    //        }
    //
    //        String strLength = String.valueOf(strContexts.length());
    //
    //        // 1. 보낼 데이터 생성
    //        DataBuilder buildReqData = new DataBuilder(3000020);
    //        buildReqData.setAttrUse(false);
    //
    //        buildReqData.setString(strUserId, 8); // userid [8]
    //        buildReqData.setString("01", 2); // totstep [2]
    //        buildReqData.setString("01", 2); // curstep [2]
    //        buildReqData.setString(strLength, 8); // reclen [8]
    //        buildReqData.setString(strContexts, 3000000); // contexts [3000000]
    //
    //        byte[] szReqData = buildReqData.getBuffer();
    //        LOG.e(LOG_TAG, "send_gdhtsSvc006 [" + new String(szReqData) + "]");
    //
    //        // 2. 요청 정보 생성
    //        RequestTranInfo infoReq = new RequestTranInfo();
    //        infoReq.setTranDataLink(this);
    //        infoReq.setTrCode("gdhtsSvc006");
    //        infoReq.setReqData(szReqData);
    //
    // //        ret = NetSession.requestData(infoReq);
    //
    //        if (ret < 0) {
    //            LOG.e(LOG_TAG, "===  Request Error  ===");
    //        }
    //
    //        m_nGdhtsSvc006 = ret;
    //        LOG.e(LOG_TAG, "========  send_m_nGdhtsSvc006  ==========:" + ret);
    //    }
    //
    //    private void send_gdhtsSvc007() {
    //        //==================================================
    //        // #gdhtsSvc007 (MTS 차트환경설정 불러오기)
    //        //==================================================
    //        // SVR : 4
    //        //--------------------------------------------------
    //        // IN(S)
    //        // char userid [8];         /* 사용자ID */
    //        // char totstep [2];        /* 전체단계 (00) */
    //        // char curstep [2];        /* 현재단계 (00) */
    //        //--------------------------------------------------
    //        // OUT(S)
    //        // char userid [8];         /* 사용자ID */
    //        // char totstep [2];        /* 전체단계 (01) */
    //        // char curstep [2];        /* 현재단계 (01) */
    //        // char reclen [8];         /* contexts 크기 */
    //        // char contexts [3000000]; /* 내용 */
    //        //--------------------------------------------------
    //        int ret = 0;
    //
    //        String strUserId = SessionInfo.getUserID();
    //
    //        // 1. 보낼 데이터 생성
    //        DataBuilder buildReqData = new DataBuilder(12);
    //        buildReqData.setAttrUse(false);
    //
    //        buildReqData.setString(strUserId, 8); // userid [8]
    //        buildReqData.setString("00", 2); // totstep [2]
    //        buildReqData.setString("00", 2); // curstep [2]
    //
    //        byte[] szReqData = buildReqData.getBuffer();
    //        LOG.e(LOG_TAG, "send_gdhtsSvc007 [" + new String(szReqData) + "]");
    //
    //        // 2. 요청 정보 생성
    //        RequestTranInfo infoReq = new RequestTranInfo();
    //        infoReq.setTranDataLink(this);
    //        infoReq.setTrCode("gdhtsSvc007");
    //        infoReq.setReqData(szReqData);
    //
    // //        ret = NetSession.requestData(infoReq);
    //
    //        if (ret < 0) {
    //            LOG.e(LOG_TAG, "===  Request Error  ===");
    //        }
    //
    //        m_nGdhtsSvc007 = ret;
    //        LOG.e(LOG_TAG, "========  send_m_nGdhtsSvc007  ==========:" + ret);
    //    }
    // 2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
    var m_sFxMarginType = "-1"

    private val m_bIsOneQStockChart = false

    // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 <<
    // 2019. 05. 15 by hyh - 멀티차트, 비교차트 진법소수점 처리 >>
    //    private String getScaledPriceWithDot(String strData, String strCode) {
    //        if (strData.contains(".")) {
    //            return strData;
    //        }
    //
    //        String strResult = strData;
    //
    //        if (strData == null || strData.equals("")) {
    //            return strResult;
    //        }
    //
    //        if (m_mapLog.size() > 0) {
    //            String strLogInfo = m_mapLog.get(strCode);
    //
    //            if (strLogInfo == null) {
    //                return strResult;
    //            }
    //
    //            String[] values = strLogInfo.split(":");
    //
    //            if (values.length > 3) {
    //                try {
    //                    int nLog = Integer.parseInt(values[1]);
    //                    int nPrecision = 0;
    //
    //                    if (nLog == 10) {
    //                        nPrecision = Integer.parseInt(values[2]);
    //                    }
    //                    else {
    //                        nPrecision = Integer.parseInt(values[3]);
    //                    }
    //
    //                    double dData = __String.todouble(strData);
    //                    dData = dData / Math.pow(10, nPrecision);
    //
    //                    strResult = String.format("%f", dData);
    //                } catch (Exception e) {
    //                    LOG.e("Exception", e.toString());
    //                }
    //            }
    //        }
    //
    //        return strResult;
    //    }
    //    //2019. 05. 15 by hyh - 멀티차트, 비교차트 진법소수점 처리 <<
    private val m_sChartTitle = ""
    private val m_sChartTitleColor = ""

    var m_bDisableInterceptScroll = false

//    fun createChart(
//        relativeLayout: RelativeLayout,
//        params: FrameLayout.LayoutParams,
//        Iwidth: Int,
//        Iheight: Int
//    ): RelativeLayout {
//        COMUtil.apiMode = true
//
//        COMUtil.setChartMain(this) //Activity 인터페이스 연결
//        var viewGroup: ViewGroup =
//            (this.findViewById(R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
//
//        COMUtil.apiView = viewGroup
//        COMUtil.apiLayout = relativeLayout    //기준선, 자동추세선을 추가할 ViewGroup설정.
//        COMUtil.base = "base11"
//
//        var chartLayout: RelativeLayout = RelativeLayout(this.context)
//        //chartLayout.setBackgroundColor(Color.WHITE);
//        chartLayout.setTag("chartLayout")
//        chartLayout.setLayoutParams(
//            RelativeLayout.LayoutParams(Iwidth, Iheight)
//        )
//
//        COMUtil.chartWidth = Iwidth
//        COMUtil.chartHeight = Iheight
//
//        COMUtil.g_nDisWidth = Iwidth
//        COMUtil.g_nDisHeight = Iheight
//
//        val rtnVal = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_SP, COMUtil.nFontSize,
//            this.getResources().getDisplayMetrics()
//        )
//        COMUtil.nFontSize_paint = rtnVal
//        COMUtil.typeface = Typeface.createFromAsset(
//            this.getResources().getAssets(),
//            "Fonts/NotoSans/NotoSansKR-Regular.otf"
//        )
//        COMUtil.typefaceBold = Typeface.createFromAsset(
//            this.getResources().getAssets(),
//            "Fonts/NotoSans/NotoSansKR-Medium.otf"
//        )
//
//        chartView = MainFrame(this, chartLayout)
//        chartView!!.userProtocol = this
//        chartView!!.setMethod("isAnalToolbar", "YES")
//        chartView!!.setUI()
//        chartView!!.setSkinType("white")
//        chartlayout.addView(chartLayout)
//
//        return relativeLayout
//
//    }

    // updateOutputData start
    fun updateOutputData(
        codeData: KfitStockPresentEntity,
        basechartData: KfitBaseChartResponseEntity,
    ): Boolean {
        // sample data apply
        var data: ByteArray? = null
        val chartDataListKfit = basechartData.list
        // 2023.02.14 by SJW - chartview 방어 코드 추가 >>
        if (chartView == null) {
            chartView = getMainFrame()
        }
        // 2023.02.14 by SJW - chartview 방어 코드 추가 <<
        if (m_bIsLoadChart && m_sLoadFileName.length > 0) chartView?.loadLastSaveState(m_sLoadFileName) // 2024.01.16 by SJW - 설정 관련 코드 처리
//        var chartData = "XBTUSD
//        15414.51     105.0  0.69                                              00001XBTUS                 256002000252UM=E@UD=1@UT=1@RDATET=YYYYMMDD@PREVPRICE=15309.5000000@@MARKET=0@
//        "

//        data = chartData.toByteArray(Charsets.ISO_8859_1)

        // ChartData setting #####

        m_nDataCount = chartDataListKfit.size

        // test
//        m_nDataCount = 1

//        if (!codeData.nextKey.isNullOrEmpty())
//            nKey = codeData.nextKey!!.get(0)
        kotlin.runCatching {
            if (basechartData.page.hasMore) {
                nKey = basechartData.page.nextKey.get(0)
            } else {
                nKey = ""
            }
        }.onFailure {
            it.printStackTrace()
        }

        var strCode = codeData.code
        var nMarketGb = 0
        if (isFloatType()) nMarketGb = 1

        val result = StringBuffer(146 + 252)

        val sPriceData: Array<String> = m_sPriceData.split(";").toTypedArray()
        var strName = "" // codeData.name (사용안함)
        var strClose = codeData.close
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 >>
//        var strSign = codeData.sign
        var strSign = codeData.sign.value
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 <<
        var strChange = codeData.change
        var strChgRate = codeData.chgRate
        var strVolume = codeData.volume
        var strOpen = codeData.open
        var strHigh = codeData.high
        var strLow = codeData.low
        var strRealCode = codeData.realCode
        var strValue = ""
        // 2021.06.09 by hanjun.Kim - kakaopay - 소수점 자리수 처리 >>
        when {
            codeData.isIndex -> { // 지수 및 선물 차트 소수점 2자리
                m_nDecimal = 2
                // 2022.05.04 by lyk - 지수차트 처리 >>
                m_enMarketCategory = ENMarketCategoryType.GE_MARKET_CATE_INDUSTRY
                chartView?.mainBase!!.baseP.nMarketType = m_enMarketCategory.ordinal
                // 2022.05.04 by lyk - 지수차트 처리 <<
            }

            codeData.market.toLowerCase(Locale.ENGLISH) == "krw" -> {
                m_nDecimal = 0
            }

            codeData.market.toLowerCase(Locale.ENGLISH) == "usd" -> {
                // 가격이 1달러 미만이면 4자리, 이상이면 2자리 -> 가격상관없이 4자리 고정으로 변경, 기획요청사항 (2021.12.15)
                // 2023.04.05 by SJW - 종가 데이터 빈 값 방어코드 처리 >>
//                if(strClose.toDouble() >= 1.0) {
                if (!strClose.isNullOrEmpty() && strClose.toDouble() >= 1.0) {
                    // 2023.04.05 by SJW - 종가 데이터 빈 값 방어코드 처리 <<
                    m_nDecimal = 4
                } else {
                    m_nDecimal = 4
                }
            }

            else -> {
                kotlin.runCatching {
                    m_nDecimal = codeData.decimal.toInt()
                }.onFailure {
//                    log.info("decimal cast error")
                    m_nDecimal = 0
                }
            }
        }

        // 타임존 설정 >>
        sTimeZone = codeData.timezone
        // 테스트를 위해 임시 막음
//        if(sTimeZone != "") {
//            dateFormat.timeZone = TimeZone.getTimeZone(sTimeZone)
//            timeFormat.timeZone = TimeZone.getTimeZone(sTimeZone)
//        }
        // 타임존 설정 <<
        // 2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 >>
        if (sTimeZone.toLowerCase().startsWith("america")) {
            chartView?.setMethod("setTimeZoneText", sTimeZone)
        }
        // 2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 <<

        kfitBaseChartData = basechartData.data
        // 2021.07.30 by lyk - 프리애프터 적용 >>
        if (codeData.market.toLowerCase(Locale.ENGLISH) == "usd") {
            if (kfitBaseChartData != null) {
                if (kfitBaseChartData!!.marketStartTime != "" && kfitBaseChartData!!.marketEndTime != "") {
                    val sMarketTime =
                        "1;" + kfitBaseChartData!!.marketStartTime + ";" + kfitBaseChartData!!.marketEndTime
                    // 2023.02.09 by SJW - 가로/세로 전환 시 차트 크래시 나는 오류 수정 >>
//                    chartView!!.setMethod("setPreAfterAreaVisible", sMarketTime)
                    chartView?.setMethod("setPreAfterAreaVisible", sMarketTime)
                    // 2023.02.09 by SJW - 가로/세로 전환 시 차트 크래시 나는 오류 수정 <<
                }
            }
        }
        // 2021.07.30 by lyk - 프리애프터 적용 <<

        // 2021.06.09 by hanjun.Kim - kakaopay - 소수점 자리수 처리 <<
        // 2023.03.17 by SJW - 차트 상단 대비기호 변경 >>
//        kotlin.runCatching {
//            if (strChange.toDouble() > 0) {
//                strSign = "2"
//            } else if (strChange.toDouble() < 0) {
//                strSign = "4"
//            } else {
//                strSign = "3"
//            }
//        }.onFailure {
// //            log.info("strChange cast error")
//            strSign = "3"
//        }
//        kotlin.runCatching {
//            if (strChange.toDouble() > 0) {
//                strSign = KfitFluctuationFlagType.UP
//            } else if (strChange.toDouble() < 0) {
//                strSign = KfitFluctuationFlagType.LOWER
//            } else {
//                strSign = KfitFluctuationFlagType.FLAT
//            }
//        }.onFailure {
// //            log.info("strChange cast error")
//            strSign = KfitFluctuationFlagType.FLAT
//        }
        // 2023.03.17 by SJW - 차트 상단 대비기호 변경 <<
        result.append(this.rpad(strName, 40, ' ')) // NAME
        result.append(this.rpad("", 10, ' ')) // JANG
        result.append(this.lpad(strClose, 20, ' ')) // PRICE
        // 2023.03.17 by SJW - 차트 상단 대비기호 변경 >>
        if (strSign == null || strSign.length < 1 || strSign == KfitFluctuationFlagType.INIT.value) {
            /* 대비부호     */
            result.append("3")
        } else {
            result.append(strSign)
        }
        // 2023.03.17 by SJW - 차트 상단 대비기호 변경 <<
        result.append(this.lpad(strChange, 20, ' ')) // CHANGE
        // 2023.04.04 by SJW - 등락률 null or empty string 방어코드 >>
//        result.append(
//            this.lpad(
//                java.lang.String.format(
//                    "%.2f",
//                    strChgRate!!.toDouble()
//                ), 6, ' '
//            )
//        )
        if (strChgRate != null && strChgRate.isNotEmpty()) {
            result.append(
                this.lpad(
                    java.lang.String.format(
                        "%.2f",
                        strChgRate.toDouble(),
                    ),
                    6,
                    ' ',
                ),
            )
        } else {
            // Handle the case where strChgRate is null or empty
            strChgRate = "0.00"
        }
        // 2023.04.04 by SJW - 등락률 null or empty string 방어코드 <<
        result.append(this.lpad(strVolume, 20, ' ')) // VOLUME
        result.append(this.lpad(strOpen, 20, ' ')) // OPEN
        result.append(this.lpad(strHigh, 20, ' ')) // HIGH
        result.append(this.lpad(strLow, 20, ' ')) // LOW
        result.append(
            this.lpad(
                strRealCode,
                10,
                ' ',
            ),
        ) // PREVOL
        result.append(this.rpad(nKey, 15, ' ')) // NKEY (15)
        result.append(this.lpad("256", 6, ' ')) // CCHTSIZE
        result.append(
            this.lpad(
                m_nDataCount.toString(),
                5,
                '0',
            ),
        ) // TMP
        result.append("0252") // BOJOLEN

        // UM=E, A 타입지정.
        var um = "E"
        if (m_isConti == true && m_nDataCount > 0) {
            um = "A"
        } else {
            COMUtil.initSendTrType()
        }

        // Period을 UD값으로 변경.
        val ud: String = this.convertToUD(period)

        // Unit값 설정.
        var ut = "1"
        if (unit != null && unit != "") {
            ut = unit
        }

        // UD에 대응하는 RDATE 추출.
        val rdate: String? = this.convertToRdate(ud)
        var strBojo = "UM=$um@UD=$ud@UT=$ut@RDATET=$rdate@"

        // 2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 >>
        var sBoundMark: String? = null

        if (m_chartType == ChartType.BASE_CHART && m_strHighest.length > 0 && m_strLowest.length > 0 && m_strStandardValue.length > 0) {
            sBoundMark = "가격차트:"
            sBoundMark += String.format("%9s,", m_strHighest)
            sBoundMark += String.format("%9s:", m_strLowest)
            sBoundMark += strCode + ":1:"
            sBoundMark += String.format("%9s:1", m_strStandardValue)
        }

        // 2015. 12. 16 일반설정 상하한가 바 표시<<
        if (sBoundMark != null) {
            strBojo += "@BOUNDMARK=$sBoundMark@"
        }
        // 2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 <<

        // 2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 <<
        try {
            strBojo += "@MARKET=" + m_enMarketCategory.ordinal.toString() + "@"

            // 2019. 06. 07 by hyh - 멀티틱 조회 시점 몇 틱인지 알려줌 >>
            if (m_nTickCount > 0) {
                strBojo += "@UTEC=" + (m_nTickCount - 1) + "@"
            }
            // 2019. 06. 07 by hyh - 멀티틱 조회 시점 몇 틱인지 알려줌 <<
//            strBojo = String(this.rpad(strBojo, 252, ' ').toByteArray(), "EUC-KR") //Encoding을 맞추지 않으면, byte size가 달라진다.
            strBojo = String(
                this.rpad(strBojo, 252, ' ')!!.toByteArray(Charsets.ISO_8859_1),
                Charsets.UTF_8,
            )
        } catch (exUE: UnsupportedEncodingException) {
            strBojo = ""
        }

        result.append(strBojo)

        val chartData = result.toString()
//        try {
        data = chartData.toByteArray(Charset.forName("EUC-KR"))
//        } catch ()
//        {
//            m_isConti = false;
//            return false;
//        }

        if (null == strCode || 0 == strCode.trim { it <= ' ' }.length) {
            strCode = "000000"
        }
        if (chartView != null) {
            chartView!!.symbol = strCode
        }

        COMUtil.symbol = strCode
        if (m_enMarketCategory === ENMarketCategoryType.GE_MARKET_CATE_ACCRUE) {
            COMUtil.apCode = COMUtil.TR_CHART_INVESTOR
            COMUtil.lcode = "S31"
        } else {
            if (m_chartType == ChartType.COMPARE_CHART) {
                if (nMarketGb == 1) {
                    COMUtil.apCode = COMUtil.TR_COMPARE_FUTURE
                    COMUtil.lcode = "SC0"
                } else {
                    COMUtil.apCode = COMUtil.TR_COMPARE_STOCK
                    COMUtil.lcode = "S31"
                }
            } else {
                if (nMarketGb == 1) {
                    COMUtil.apCode = COMUtil.TR_CHART_FUTURE
                    COMUtil.lcode = "SC0"
                } else {
                    COMUtil.apCode = COMUtil.TR_CHART_STOCK
                    COMUtil.lcode = "S31"
                }
            }
        }

        bOpenData = true
//        var strCandleType = "캔들"
//        if (!bOpenData || m_chartType == ChartType.LINE_CHART || m_chartType == ChartType.LINE2_CHART || m_chartType == ChartType.LINEFILL_CHART || m_chartType == ChartType.TODAY_LINE_CHART) {
//            strCandleType = "라인"
//        }
        // 2017.02.15 by LYH >> 기타지표 항목 추가 후 투자자 탭 갔다가 복귀하면 지표 데이터가 표시되지 않음
        if (m_sLoadFileName.length > 0 && COMUtil._mainFrame !== chartView) {
            COMUtil._mainFrame = chartView
            COMUtil._neoChart = chartView?.mainBase!!.baseP._chart
        }
        // chart setting end ###

        strDates = Array(m_nDataCount, { "" })
        dOpens = DoubleArray(m_nDataCount)
        dHighs = DoubleArray(m_nDataCount)
        dLows = DoubleArray(m_nDataCount)
        dCloses = DoubleArray(m_nDataCount)
        dVolumes = DoubleArray(m_nDataCount)
        dValues = DoubleArray(m_nDataCount)
        dRights = DoubleArray(m_nDataCount)
        dRightRates = DoubleArray(m_nDataCount)
        dSessionIds = DoubleArray(m_nDataCount) // 2023.03.15 by SJW - 애프터마켓 추가

        var nIndex = m_nDataCount - 1
        for (i in 0 until m_nDataCount) {
            if (m_bDataReverse) {
                nIndex = m_nDataCount - 1 - i
            } else {
                nIndex = i
            }
            val dateTime = chartDataListKfit[nIndex].dateTime

            strDates[i] =
                getChartDate(convertTimestampToDate(dateTime), convertTimestampToTime(dateTime))
            dOpens[i] = chartDataListKfit[nIndex].startPrice.toDouble()
            dHighs[i] = chartDataListKfit[nIndex].highPrice.toDouble()
            dLows[i] = chartDataListKfit[nIndex].lowPrice.toDouble()
            dCloses[i] = chartDataListKfit[nIndex].closePrice.toDouble()
            dVolumes[i] = chartDataListKfit[nIndex].volume.toDouble()
            if (chartDataListKfit[nIndex].amount.isNotEmpty()) {
                dValues[i] = chartDataListKfit[nIndex].amount.toDouble()
            }
            // 2023.03.16 by SJW - 국내시장에서 sessionId이 null일 경우 크래시나는 오류 수정 >>
//            dSessionIds[i] = chartDataListKfit[nIndex].sessionId?.toDouble()
            dSessionIds[i] = chartDataListKfit[nIndex].sessionId?.toDoubleOrNull()
                ?: 0.0 // 2023.03.15 by SJW - 애프터마켓 추가
            // 2023.03.16 by SJW - 국내시장에서 sessionId이 null일 경우 크래시나는 오류 수정 <<
        }

        chartView?.setData_data(
            data,
            strDates,
            dOpens,
            dHighs,
            dLows,
            dCloses,
            dVolumes,
            dValues,
            dRights,
            dRightRates,
            "캔들",
        )
        // 2023.03.15 by SJW - 애프터마켓 추가 >>
        if (dSessionIds != null) {
            // 2023.03.28 by SJW -  애프터마켓(연속데이터 처리) >>
//            chartView?.setSignalData("sessionIds", dSessionIds)
            chartView?.setSignalData("sessionIds", dSessionIds, m_isConti)
            // 2023.03.28 by SJW -  애프터마켓(연속데이터 처리) <<
        }
        // 2023.03.15 by SJW - 애프터마켓 추가 <<
        if (m_nFixDataCount > 0) {
            if (chartView != null) chartView?.setMethod("setRealUpdate", "1")
        }
        // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 >>
        if (m_nDecimal >= 0 && chartView != null) {
            val nScale = 10
            val nDecimal = m_nDecimal
            val nLogDisp: Int = m_nLogDisp
            chartView?.setPriceFormat(strCode, nScale, nDecimal, nDecimal)

            // 2019. 05. 15 by hyh - 멀티차트, 비교차트 진법소수점 처리
//            val strPriceFormat = "$strCode:$nScale:$nDecimal:$nLogDisp"
            // m_mapLog.put(strCode, strPriceFormat);
        }
        // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 <<

        // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 <<

        m_bIsLoadChart = false

        strDates = emptyArray<String>()
        dOpens = DoubleArray(0)
        dHighs = DoubleArray(0)
        dLows = DoubleArray(0)
        dCloses = DoubleArray(0)
        dVolumes = DoubleArray(0)
        dValues = DoubleArray(0)
        dSessionIds = DoubleArray(0) // 2023.03.15 by SJW - 애프터마켓 추가
        // 차트 데이터 처리 후 기타 데이터 호출

//        //평단가 기능 사용중인 경우 데이터 요청
//        if(avgBuyPriceFunc == "1") {
//            reqAvgBuyPrice()
//        }
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 >>
        if ((componentName?.shortClassName?.length ?: 0) > 1) {
            topActivityName = componentName!!.shortClassName.substring(1)
        }
        val currentActivityName = topActivityName.split(".").last()
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 <<
        // 매수,매도 표시 기능 사용중인 경우 데이터 요청
        if (buySellPriceFunc == "1") {
//            (mainActivity as KfitChartViewActivity).bTradeData_Conti = false
//            (mainActivity as KfitChartViewActivity).tradeTotalDataList.clear()
            // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//            (mainActivity as KfitChartViewActivity).reqTradeData(false) //false:조회, true:당일건 조회시 설정값
            if (currentActivityName == "KfitChartViewActivity") {
                onChartListener?.requestTradeData(false)
            } else {
                onChartListener?.requestTradeData(false)
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            }
        }

        // 기업 캘린더 기능 사용중인 경우 데이터 요청
        if (corporateCalendarFunc == "1") {
            if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY) {
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                (mainActivity as KfitChartViewActivity).reqCorporateCalendar()
                if (currentActivityName == "KfitChartViewActivity") {
                    onChartListener?.requestCorporateCalendar()
                } else {
                    onChartListener?.requestCorporateCalendar()
                }
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            } else {
                setCorporateCalendarData("")
            }
        }
        m_isConti = false
        return true
    }
    // updateOutputData end

    fun updateRealData(codeData: KfitPresentPriceStreamEntity) {
        var isRet: Boolean
        val isData = false
        var strRealCode: String? = codeData.stockId
        var strRealTime: String? = convertTimestampToTime(codeData.updatedAt)
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 >>
//        var strRealSign: String? = ""
        var strRealSign: String? = codeData.fluctuationFlagType.value
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 <<
        var strRealChange: String? = codeData.fluctuationPrice
        var strRealChgRate: String? = codeData.fluctuationRate
        var strRealPrice: String? = codeData.currentPrice
        var strRealOpen: String? = codeData.startPrice
        var strRealHigh: String? = codeData.highestPrice
        var strRealLow: String? = codeData.lowestPrice
        var strRealVolume: String? = codeData.totalVolume
        var strRealCheVol: String? = codeData.volume
        var strRealValue = "" // 2014.04.29 by LYH >> 거래대금 실
        var strRealMigyul = ""
        var bRealFloatType = false
        var strRealSessionId: String? = codeData.sessionId // 2023.03.15 by SJW - 애프터마켓 추가
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 >>
//        if (strRealChange!!.toDouble() > 0) {
//            strRealSign = "2"
//        } else if (strRealChange.toDouble() < 0) {
//            strRealSign = "4"
//        } else {
//            strRealSign = "3"
//        }
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 <<
//        KfitChartViewActivity.log.info("DRChart_updateRealData: $strRealTime")
        // 2023.04.07 by SJW - 애프터마켓 실시간 수정 >>
        if (!strRealSessionId.isNullOrEmpty() && (strRealSessionId == "1" || strRealSessionId == "2") &&
            (
                m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY ||
                    m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_WEEKLY ||
                    m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY ||
                    m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY
                )
        ) {
            return
        }
        // 2023.04.07 by SJW - 애프터마켓 실시간 수정 <<

        // 2017.04.21 by LYH >> 장전 선물 실시간이 현재가 0으로 들어와 봉이 아래로 그려짐
        if (strRealPrice!!.toDouble() === 0.0 && strRealVolume!!.toDouble() === 0.0) return
        // 2017.04.21 by LYH << 장전 선물 실시간이 현재가 0으로 들어와 봉이 아래로 그려짐 end
        val size = 166 + 17 // 2019.02.20 by sdm >> 데이터 자리수 변경 (+12)
        val result = StringBuffer(size)
        result.append("0001")
        val packetSize = size - 4 - 4
        result.append(COMUtil.makeZero(packetSize.toString(), 4))
        var strRealHead = "SC0"
        if (isFloatType()) {
        } else {
            strRealHead = "S31"
        }
        result.append(strRealHead)
        result.append(this.rpad(strRealCode, 32, ' '))
        // 2023.03.15 by SJW - 애프터마켓 추가 >>
//        result.append(
//            this.lpad(
//                COMUtil.makeZero(" ", 2),
//                2,
//                ' '
//            )
//        ) //codeseq
        result.append(this.rpad(strRealSessionId, 2, ' '))
        // 2023.03.15 by SJW - 애프터마켓 추가 <<
        var time = strRealTime.toString()
        if (time.length == 8) {
            time = time.substring(0, 6)
        } else if (time.length == 7) {
            time = time.substring(0, 6)
        } else if (time.length == 5) {
            time = "0$time"
        }

        // test time >>
//        time = "160000"
        // test time <<
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 >>
//        if (strRealSign == null) strRealSign = "3"
        if (strRealSign == null || strRealSign == KfitFluctuationFlagType.INIT.value) {
            strRealSign =
                "3"
        }
        // 2023.03.31 by SJW - Y축 현재가 색상, 등락률과 안맞는 현상 수정 <<
        if (strRealChange == null) strRealChange = "0"
        if (strRealChgRate == null) strRealChgRate = "0"
        result.append(this.lpad(time, 6, ' '))
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealPrice).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        ) // 2019.02.20 by sdm >> 데이터 자리수 변경 (12->15)
        if (strRealOpen == null) strRealOpen = "0"
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealOpen).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        ) // 2019.02.20 by sdm >> 데이터 자리수 변경 (12->15)
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealHigh).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        ) // 2019.02.20 by sdm >> 데이터 자리수 변경 (12->15)
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealLow).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        ) // 2019.02.20 by sdm >> 데이터 자리수 변경 (12->15)
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealVolume).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        )
        // result.append(lpad(COMUtil.makeZero(String.valueOf(" "), 10), 10, ' '));//bidvol
        // result.append(lpad(COMUtil.makeZero(String.valueOf(strRealBuyVolume), 10), 10, ' ')); //2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
        result.append(
            this.lpad(
                COMUtil.makeZero("0", 10),
                10,
                ' ',
            ),
        ) // 2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealValue).toString(),
                    15,
                ),
                15,
                ' ',
            ),
        ) // 2014.04.29 by LYH >> 미결제약정 실시간
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    strRealSign.toString(),
                    1,
                ),
                1,
                ' ',
            ),
        )
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    strRealChange.toString(),
                    12,
                ),
                12,
                ' ',
            ),
        )
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    strRealChgRate,
                    10,
                ),
                10,
                ' ',
            ),
        )
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealCheVol).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        )
        result.append(
            this.lpad(
                COMUtil.makeZero(
                    getRemovePlusMinus(strRealMigyul).toString(),
                    12,
                ),
                12,
                ' ',
            ),
        )

//        LOG.e("CtlChart", "updateRealData  bRealFloatType:"+bRealFloatType+"  시:"+strRealOpen+"  고:"+strRealHigh+"  저:"+strRealLow+"  종:"+strRealPrice+"  거:"+strRealVolume);
        val chartData = result.toString()
        var data: ByteArray? = null
        data = try {
            chartData.toByteArray(charset("ms949")) // DRFN 인코딩으로 설정.
        } catch (e: UnsupportedEncodingException) {
            return
        }

//        KfitChartViewActivity.log.info("DRChart_chartView_setRealData: $strRealTime")
        chartView?.setRealData(data)
    }

    private fun getRemovePlusMinus(sVal: String?): String? {
        var rtnVal = sVal
        if (sVal == null) return null
        val sStep = sVal.replace("+", "")
        rtnVal = sStep.replace("-", "").trim { it <= ' ' }
        return rtnVal
    }

    // /////////// api function start //////////////////
    /**
     * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 왼쪽에 붙혀서 보내준다.
     * ex) lpad("abc", 5, '^') ==> "^^abc"
     * lpad("abcdefghi", 5, '^') ==> "abcde"
     * lpad(null, 5, '^') ==> "^^^^^"
     *
     * @param strSource
     * @param iLength
     * @param cPadder
     */
    open fun lpad(strSource: String?, iLength: Int, cPadder: Char): String? {
        var sbBuffer: StringBuffer? = null
        if (strSource != null) {
            val iByteSize = getByteSize(strSource)
            return if (iByteSize > iLength) {
                strSource.substring(0, iLength)
            } else if (iByteSize == iLength) {
                strSource
            } else {
                val iPadLength = iLength - iByteSize
                sbBuffer = StringBuffer()
                for (j in 0 until iPadLength) {
                    sbBuffer.append(cPadder)
                }
                sbBuffer.append(strSource)
                sbBuffer.toString()
            }
        }
        // int iPadLength = iLength;
        sbBuffer = StringBuffer()
        for (j in 0 until iLength) {
            sbBuffer.append(cPadder)
        }
        return sbBuffer.toString()
    }

    /**
     * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 오른쪽에 붙혀서 보내준다.
     * ex) lpad("abc", 5, '^') ==> "abc^^"
     * lpad("abcdefghi", 5, '^') ==> "abcde"
     * lpad(null, 5, '^') ==> "^^^^^"
     *
     * @param strSource
     * @param iLength
     * @param cPadder
     */
    open fun rpad(strSource: String?, iLength: Int, cPadder: Char): String? {
        var sbBuffer: StringBuffer? = null
        if (strSource != null) {
            val iByteSize = getByteSize(strSource)
            return if (iByteSize > iLength) {
                // 2014. 2. 28  20바이트 이상 종목명 중 조회안되는 현상 >>
                var strRtnData = ""
                strRtnData = try {
                    strSource.substring(0, iLength)
                } catch (e: StringIndexOutOfBoundsException) {
                    strSource.substring(0, strSource.length - 1)
                }
                strRtnData
                // 2014. 2. 28  20바이트 이상 종목명 중 조회안되는 현상 <<
            } else if (iByteSize == iLength) {
                strSource
            } else {
                val iPadLength = iLength - iByteSize
                sbBuffer = StringBuffer(strSource)
                for (j in 0 until iPadLength) {
                    sbBuffer.append(cPadder)
                }
                sbBuffer.toString()
            }
        }
        sbBuffer = StringBuffer()
        for (j in 0 until iLength) {
            sbBuffer.append(cPadder)
        }
        return sbBuffer.toString()
    }

    /**
     * byte size를 가져온다.
     *
     * @param str String target
     * @return int bytelength
     */
    open fun getByteSize(str: String?): Int {
        if (str == null || str.length == 0) return 0
        var byteArray: ByteArray? = null
        try {
            byteArray = str.toByteArray(charset("EUC-KR"))
        } catch (ex: UnsupportedEncodingException) {
//            LOG.e("Exception", ex.toString())
        }
        return byteArray?.size ?: 0
    }

    private fun isFloatType(): Boolean {
        return m_enMarketCategory === ENMarketCategoryType.GE_MARKET_CATE_FUTURE_DOMESTIC ||
            m_enMarketCategory === ENMarketCategoryType.GE_MARKET_CATE_INDUSTRY
    }

    //    //2015. 8. 25 ETF-ETN 일부종목 길이가 긴 경우 처리 >>
    //    public String subString(String strData, int iStartPos, int iByteLength) {
    //        byte[] bytTemp = null;
    //        int iRealStart = 0;
    //        int iRealEnd = 0;
    //        int iLength = 0;
    //        int iChar = 0;
    //
    //        try {
    //            //UTF-8로 변환하는 경우 한글 2Byte, 기타 1Byte로 떨어짐
    //            bytTemp = strData.getBytes("EUC-KR");
    //            iLength = bytTemp.length;
    //
    //            for (int iIndex = 0; iIndex < iLength; iIndex++) {
    //                if (iStartPos <= iIndex) {
    //                    break;
    //                }
    //                iChar = (int) bytTemp[iIndex];
    //                if ((iChar > 127) || (iChar < 0)) {
    //                    //한글의 경우 (2byte 통과처리)
    //                    //한글은 2byte이기 때문에 다음 글자는 볼 것도 없이 스킵한다
    //                    iRealStart++;
    //                    iIndex++;
    //                }
    //                else {
    //                    //기타 글씨 (1Byte 통과처리)
    //                    iRealStart++;
    //                }
    //            }
    //
    //            iRealEnd = iRealStart;
    //            int iEndLength = iRealStart + iByteLength;
    //            for (int iIndex = iRealStart; iIndex < iEndLength; iIndex++) {
    //                iChar = (int) bytTemp[iIndex];
    //                //한글의 경우 (2byte 통과처리)
    //                //한글은 2byte이기 때문에 다음 글자는 볼 것도 없이 스킵한다
    //                if ((iChar > 127) || (iChar < 0)) {
    //                    iRealEnd++;
    //                    iIndex++;
    //                }
    //                else {
    //                    iRealEnd++;
    //                }
    //            }
    //        } catch (Exception e) {
    //        }
    //
    //        return strData.substring(iRealStart, iRealEnd);
    //    }
    //    //2015. 8. 25 ETF-ETN 일부종목 길이가 긴 경우 처리 <<
    /* UD Converter (2011.11.28 by DRFN lyk) */
    private fun convertToUD(period: String): String {
        var rtnUd = ""
        // if (period == null) period = "2";
        // INPUT :   2,3,4,1,0 (일,주,월,분,틱)
        // DRFN_UD : 1,2,3,4,5 (일,주,월,분,틱)
        if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY) {
            rtnUd = "1"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_WEEKLY) {
            rtnUd = "2"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY) {
            rtnUd = "3"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MINUTE) {
            rtnUd = "4"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_TICK) {
            rtnUd = "5"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
            rtnUd = "8"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_HOUR) { // 2014.07.18 by lyk -"시" 타입 추가
            rtnUd = "9"
        }

        // 2017.08.31 by lyk - 주기별 차트 설정 관련 설정
        chartView?.ctlDataTypeName = rtnUd
        // 2017.08.31 by lyk - 주기별 차트 설정 관련 설정 end
        return rtnUd
    }

    /* RDATE Convert (2011.11.28 by DRFN lyk) */
    private fun convertToRdate(period: String): String? {
        var rtnRdate = ""
        // if (period == null) period = "1";
        // DRFN :   2,3,4,1,0 (일,주,월,분,틱)
        // HAHWHA : 1,2,3,4,5 (일,주,월,분,틱)
        if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY) {
            rtnRdate = "YYYYMMDD"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_WEEKLY) {
            rtnRdate = "YYYYMMDD"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY || m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
            rtnRdate = "YYYYMM"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MINUTE) {
            rtnRdate = "MMDDHHMM"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_TICK) {
            rtnRdate =
                if (m_enMarketCategory === ENMarketCategoryType.GE_MARKET_CATE_ELW || m_bDayTick) "HHMMSS" else "DDHHMMSS"
        }
        return rtnRdate
    }

    fun getChartDate(strGetDate: String?, strTime: String?): String {
        var date = strGetDate
        var time = strTime
        if (strGetDate == null || strGetDate.equals("")) return ""

        if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_TICK) {
            if (time!!.length < 6) time = "000000"
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MINUTE) {
            if (time!!.length < 4) time = "0000"
        }

        if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_TICK) {
            if (time != null && !time.equals("")) {
                date = if (strGetDate.length < 8) {
                    val formate: SimpleDateFormat = SimpleDateFormat("dd", Locale.KOREA)
                    formate.format(Date()) + time
                } else {
                    strGetDate.substring(6, 8) + time
                }
            }
        } else if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_MINUTE) {
            date = if (strGetDate.length < 8) {
                val formate: SimpleDateFormat = SimpleDateFormat("MMdd", Locale.KOREA)
                formate.format(Date()) + time
            } else {
                strGetDate.substring(4, 8) + time
            }
        }
//        else if(m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY || m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
//            date = strGetDate.substring(0, 6);
//        }
//        else if(m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
//            date = strGetDate.substring(0, 4);
//        }

        //        else if(m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY || m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
//            date = strGetDate.substring(0, 6);
//        }
//        else if(m_enPacketPeriod == ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY) {
//            date = strGetDate.substring(0, 4);
//        }
        return date!!
    }

    fun getGraphTagList(): Vector<String>? {
        return chartView?.graphTagList
    }

    fun getGraphList(): Vector<String>? {
        return chartView?.graphList
    }

    fun getSubGraphList(): Vector<String>? {
        return chartView?.subGraphList
    }

    fun getMethod(sValue: String, sParam: String): Any? {
        return chartView?.getMethod(sValue, sParam)
    }

    fun setPeriodType(nPeriod: String?) {
        if (nPeriod == null || nPeriod == "") {
            return
        }
        period = nPeriod
        try {
            when (nPeriod.toInt()) {
                0 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_TICK
                1 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_MINUTE
                2 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_DAILY
                3 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_WEEKLY
                4 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_MONTHLY
                5 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_YEARLY
                7 -> m_enPacketPeriod = ENPacketPeriodType.GE_PACKET_PERIOD_HOUR
            }
            //            if (m_chartType == ChartType.COMPARE_CHART) {
//                //if(Integer.parseInt(nPeriod) == 3 || Integer.parseInt(nPeriod) == 4)
//                chartView.clearCompareData();
//            }

            // 2019. 06. 26 by hyh - 주기별 차트 설정 >>
            if (chartView != null) {
                chartView?.ctlDataTypeName = convertToUD(period)
            }
            // 2019. 06. 26 by hyh - 주기별 차트 설정 <<
        } catch (e: NumberFormatException) {
        }
    }

    fun setPeriodTime(nTime: String?) {
        unit = nTime!!
// 		 System.out.println("=========SetPeriodTime:"+nTime);
    }

    fun setLoadChart(sValue: String) {
        m_sLoadFileName = sValue // 2024.01.16 by SJW - 설정 관련 코드 처리
        // 2023. 04. 17 by hyh - [임시] 에러 수정
        val chartView = this.chartView ?: getMainFrame()

        if (sValue == "reload") {
            if (chartView != null) {
                // 2023.08.07 by SJW - crashlytics 방어 코드 처리 >>
//                var viewGroup: ViewGroup =
//                    (COMUtil._chartMain.findViewById(android.R.id.content) as ViewGroup).getChildAt(
//                        0
//                    ) as ViewGroup
                val contentView = COMUtil._chartMain?.findViewById<ViewGroup>(android.R.id.content)
                val viewGroup: ViewGroup? = contentView?.getChildAt(0) as? ViewGroup
                // 2023.08.07 by SJW - crashlytics 방어 코드 처리 <<

                COMUtil.apiView = viewGroup
                COMUtil._mainFrame = chartView
                COMUtil._neoChart = chartView.mainBase.baseP._chart

                // 2019. 07. 09 by hyh - 백그라운드로 전환 후 복귀 시 차트 팝업 닫기 >>
                COMUtil._mainFrame.closePopup()
                // 2019. 07. 09 by hyh - 백그라운드로 전환 후 복귀 시 차트 팝업 닫기 <<

                // chartView.mainBase.baseP._chart.selectChart();
                // chartView.mainBase.baseP._chart.selectBaseChart();

                // 2019. 09. 09 by hyh - 백그라운드로 전환 후 복귀 시 전체 종목 요청하도록 수정 >>
                // m_bIsLoadChart = true;
                // 2019. 09. 09 by hyh - 백그라운드로 전환 후 복귀 시 전체 종목 요청하도록 수정 <<
                if (m_bIsMultiChart) COMUtil.bIsMulti = true
                chartView.loadLastSaveState(m_sLoadFileName)
                // chartView.mainBase.baseP.removeWhiteView();
// 				chartView.mainBase.baseP._chart.setVisibility(View.VISIBLE);
// 				chartView.mainBase.baseP._chart.selectChart();
// 				chartView.mainBase.baseP._chart.selectBaseChart();
                // chartView.mainBase.baseP._chart.onResume();
            }
        } else {
            if (COMUtil._mainFrame == null && chartView != null) {
                COMUtil._mainFrame = chartView
                COMUtil._neoChart = chartView.mainBase.baseP._chart
            }
            m_sLoadFileName = sValue
            m_chartType = ChartType.BASE_CHART
            m_bDisableInterceptScroll = true

            // presentInfoKfit를 받아온 후 파일 저장소 이름을 적용한다. >>
            if (m_sLoadFileName != null && m_sLoadFileName.contains("multi")) {
                m_bIsMultiChart = true
                COMUtil.bIsMulti = true
            } else {
                COMUtil.bIsMulti = false
            }
            chartView?.setMethod("LoadFileName", m_sLoadFileName)
            if (m_sLoadFileName.length > 0) {
                m_bIsLoadChart = true
            }
//            if (m_sLoadFileName.length > 0) chartView.loadLastSaveState(m_sLoadFileName) //2024.01.16 by SJW - 설정 관련 코드 처리
            // presentInfoKfit를 받아온 후 파일 저장소 이름을 적용한다. <<
        }
    }

    fun setSaveChart() {
        if (chartView != null && m_sLoadFileName != null && m_sLoadFileName.length > 0) {
            chartView?.saveStatus(m_sLoadFileName)
            // 2012. 12. 5 차트 저장될때 (회전하여 닫힐때 )   열려있는 팝업창 다 닫기 : J10, SL41, D8, I111
            chartView?.closePopup(this)
            // chartView.mainBase.baseP.addWhiteView();
        }
//
// 		if(chartView!=null) {
// 			chartView.mainBase.baseP._chart.setVisibility(View.GONE);
// 			//chartView.mainBase.baseP._chart.onPause();
// 		}
    }

    fun setIndicator(sValue: String?) {
        chartView?.setMethod("setIndicator", sValue)
    }

    fun setChartFunc(sValue: String?) {
        if (chartView == null) {
            chartView = getMainFrame()
        }
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 >>
        if ((componentName?.shortClassName?.length ?: 0) > 1) {
            topActivityName = componentName!!.shortClassName.substring(1)
        }
        val currentActivityName = topActivityName.split(".").last()
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 <<
        val values = sValue?.split(";")?.toTypedArray()
        if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE1) {
            var sItem = "이평 5^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        } else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE2) {
            var sItem = "이평 20^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        } else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE3) {
            var sItem = "이평 60^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        } else if (Integer.parseInt(
                values?.get(0).toString(),
            ) == SUPPORT_RESISTANCE_LINE
        ) { // 지지,저항선
            chartView?.setMethod("setSupportResistanceLine", sValue)
        } else if (Integer.parseInt(values?.get(0).toString()) == AUTOTRENDWAVE_TYPE) { // 추세선
            chartView?.setMethod("setAutoTrendWaveType", sValue)
        } else if (Integer.parseInt(values?.get(0).toString()) == AVGBUY_PRICE) { // 평균매입가
//            var avgBuyPrice = "3200" //test data

            var sndValue = values?.get(1).toString()
            avgBuyPriceFunc = sndValue
            chartView?.setMethod("setAvgBuyPrice", sndValue)

            // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 >>
            if (sndValue == "0") {
                setAvgBuyPriceData("")
            }
            // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 <<
//            reqAvgBuyPrice()
        } else if (Integer.parseInt(values?.get(0).toString()) == BUYSELL_PRICE) { // 매수매도 가격
            var sndValue = values?.get(1).toString()
            buySellPriceFunc = sndValue
            chartView?.setMethod("setBuySellPrice", sndValue)

//            (mainActivity as KfitChartViewActivity).bTradeData_Conti = false
//            (mainActivity as KfitChartViewActivity).tradeTotalDataList.clear()

            if (sndValue == "1") {
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                (mainActivity as KfitChartViewActivity).reqTradeData(false) //false:조회, true:당일건 조회시 설정값
                if (currentActivityName == "KfitChartViewActivity") {
                    onChartListener?.requestTradeData(false)
                } else {
                    onChartListener?.requestTradeData(false)
                }
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            } else {
                setBuySellPriceData("")
            }
        } else if (Integer.parseInt(values?.get(0).toString()) == CORPORATE_CALENDAR) { // 기업 캘린더 표시
            var sndValue = values?.get(1).toString()
            corporateCalendarFunc = sndValue
            chartView?.setMethod("setCorporateCalendar", sndValue)

            if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY) {
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                (mainActivity as KfitChartViewActivity).reqCorporateCalendar()
                if (currentActivityName == "KfitChartViewActivity") {
                    onChartListener?.requestCorporateCalendar()
                } else {
                    onChartListener?.requestCorporateCalendar()
                }
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            } else {
                setCorporateCalendarData("")
            }
        }
        // 2023.06.14 by SJW - "거래량" 이평 안그려지는 현상 수정 >>
        else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_VOLUME) {
            val sndValue = values?.get(1).toString()
            val vmaParam = "$INDICATOR_VAVERAGE;$sndValue"

            chartView?.setMethod("setIndicator", sValue)
            chartView?.setMethod("setIndicator", vmaParam)
        }
        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
        else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE4) {
            var sItem = "이평 10^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        } else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE5) {
            var sItem = "이평 120^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        } else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE6) {
            var sItem = "이평 200^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        }
        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
        else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE7) {
            var sItem = "이평 240^;" + values?.get(1).toString() + "^"
            chartView?.setMethod("setVisibleGraph", sItem)
        }
        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<
        else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_PAVERAGE) { // 이동평균선 표시
            var sndValue = values?.get(1).toString()
            movingAverageLine = sndValue
            chartView?.setMethod("setMovingAverageLineState", sndValue)
        } else if (Integer.parseInt(values?.get(0).toString()) == INDICATOR_CONFIG_VIEW) { // 지표 상세 설정창 연동
            var sndValue = values?.get(1).toString()
            chartView?.setMethod("openIndicatorConfigView", sndValue) // 2023.12.22 by CYJ - 방어코드 추가

//            if (sndValue == "40099"") { //이동평균선
//                chartView?.setMethod("openIndicatorConfigView", sndValue)
//            } else if (sndValue == "indicator_macd") {
//                chartView?.setMethod("openIndicatorConfigView", sndValue)
//            }
        } else {
            // 2023.06.14 by SJW - "거래량" 이평 안그려지는 현상 수정 <<
            chartView?.setMethod("setIndicator", sValue)
        }
    }

    fun reqMarketData(
        title: String?,
        title1: String?,
        title2: String?,
        dDates: LongArray,
        dDatas: DoubleArray,
        dDatas1: DoubleArray,
        dDatas2: DoubleArray,
        dDatas3: DoubleArray,
        count: Int,
    ) {
        chartView?.setMarketData(title, dDates, dDatas, count, false)
//        chartView?.setMarketData(title, dDates, dDatas1, count, false)
        chartView?.setMarketData(title1, dDates, dDatas1, count, false)
        chartView?.setMarketData(title2, dDates, dDatas2, count, true)
    }

    fun setMarketData(
        title: String,
        dates: LongArray,
        marketData: DoubleArray,
        nCount: Int,
        bSendTR: Boolean,
    ) {
        chartView?.setMarketData(title, dates, marketData, nCount, bSendTR)
    } // bSendTR값은 마지막값 보낼때만 true로.

    // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 >>
    fun setAvgBuyPriceData(avgBuyPrice: String) {
//        var avgBuyPrice = "3200" //test data
        if (chartView == null) {
            m_avgBuyPrice = avgBuyPrice
        }
        // chartView?.setMethod("setAvgBuyPriceData", avgBuyPrice)
        if (chartView != null) {
            chartView?.setMethod("setAvgBuyPriceData", avgBuyPrice)
        }
    }
    // 2021.09.29 by lyk - kakaopay - 평단가 설정 변수 오류 수정 <<

    fun setBuySellPriceData(buySellPrice: String) {
//        var buySellPrice = "2;0^20100208^11-111-1111^2,000원^1,000주;1^20100201^11-111-1111^
        //        2,345원^123주"
        if (chartView != null) {
            chartView?.setMethod("setBuySellPriceData", buySellPrice)
        }
    }

    // 2021.08.10 by lyk - kakaopay - 실시간 매매내역 테스트
    fun setBuySellPriceSpecificDay(buySellPrice: String) {
//        var buySellPrice = "2;0^20100208^11-111-1111^2,000원^1,000주;1^20100201^11-111-1111^
        //        2,345원^123주"
        chartView?.setMethod("setBuySellPriceSpecificDay", buySellPrice)
    }

    // 2021.04.16 by lky - kakaopay - 종목 이벤트 뱃지(실적발표/공시/배당 등) >>
    // "2;C^20100215^실적발표;C^20100210^배당발표";
    // 개수;타입^시간^내용^ (추후 링크주소 추가)
    fun setCorporateCalendarData(sValue: String?) {
        if (chartView == null) {
            return
        }
        if (sValue == null || sValue == "") {
            chartView?.setMethod("resetCorporateCalendarData", sValue)
        } else {
            chartView?.setMethod("setCorporateCalendarData", sValue)
        }
    }
    // 2021.04.16 by lky - kakaopay - 종목 이벤트 뱃지(실적발표/공시/배당 등) <<

    fun setSelectChart(sValue: Int?) {
        if (chartView == null) {
            chartView =
               getMainFrame() // 2023.07.25 by SJW - Analytics Log Crash 수정 / Activity 설정 창 -> Fragment 설정 창에서 crash
        }
        // 외부에서 호출된 tag 값을 차트 내부 값으로 컨버팅한다. (안드로이드만 다름, 향후 수정해야 함)
        val INDICATOR_PLINE = 10002
        val INDICATOR_JBONG = 10000
        val INDICATOR_JBONG_TRANSPARENCY = 19999
        val INDICATOR_PNF = 20001
        val INDICATOR_SWING = 20003
        val INDICATOR_RENKO = 20004
        val INDICATOR_KAGI = 20005

        // 외부에서 호출된 tag 값을 차트 내부 값으로 컨버팅한다. (안드로이드만 다름, 향후 수정해야 함)
        val INDICATOR_PLINE_INTERNAL = 20003
        val INDICATOR_JBONG_INTERNAL = 20001
        val INDICATOR_JBONG_TRANSPARENCY_INTERNAL = 19999
        val INDICATOR_PNF_INTERNAL = 20011
        val INDICATOR_SWING_INTERNAL = 20019
        val INDICATOR_RENKO_INTERNAL = 20020
        val INDICATOR_KAGI_INTERNAL = 20023

        var rtnValue = sValue
        if (sValue == INDICATOR_PLINE) {
            rtnValue = INDICATOR_PLINE_INTERNAL
        } else if (sValue == INDICATOR_JBONG) {
            rtnValue = INDICATOR_JBONG_INTERNAL
        } else if (sValue == INDICATOR_JBONG_TRANSPARENCY) {
            rtnValue = INDICATOR_JBONG_TRANSPARENCY_INTERNAL
        } else if (sValue == INDICATOR_PNF) {
            rtnValue = INDICATOR_PNF_INTERNAL
        } else if (sValue == INDICATOR_SWING) {
            rtnValue = INDICATOR_SWING_INTERNAL
        } else if (sValue == INDICATOR_RENKO) {
            rtnValue = INDICATOR_RENKO_INTERNAL
        } else if (sValue == INDICATOR_KAGI) {
            rtnValue = INDICATOR_KAGI_INTERNAL
        }

        chartView?.setMethod("setSelectChart", rtnValue.toString())
    }

    fun setInitChart() {
        // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) >>
//        buySellPriceFunc = ""
        buySellPriceFunc = "1"
        // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) <<
        setBuySellPriceData("")

        avgBuyPriceFunc = "1" // 2022.04.11 by lyk - kakaopay - 구매평균가격 표시 디폴트 true로 변경 (기획요청)
        setAvgBuyPriceData("1")

        // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) >>
//        corporateCalendarFunc = ""
        corporateCalendarFunc = "1"
        // 2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) <<
        setCorporateCalendarData("")

        if (chartView != null) {
            chartView?.setMethod("setInitChart", "")
        }
    }

    fun setSelectChart(sValue: String?) {
        chartView?.setMethod("setSelectChart", sValue)
    }

    fun setOpenDlg(sValue: String, sender: View) {
        val values = sValue.split(";").toTypedArray()
        var strOpenDlg = sValue
        if (values.size == 2) {
            strOpenDlg = values[0]
        }
        val target = Button(this.context)
        val dic = Hashtable<String, Any>()

        /* 테스트 설정창의 주석을 풀고 사용하세요 */
        var tag = COMUtil._TAG_TOOL_CONFIG
        // int tag = COMUtil._TAG_SAVELOADMENU_CONFIG; //저장,로드
        // int tag = COMUtil._TAG_PERIOD_CONFIG; //기간설정
        // int tag = COMUtil._TAG_DIVIDECHART_CONFIG; //분할차트 설정
        // int tag = COMUtil._TAG_BASELINE_CONFIG; //기준선 설정
        // int tag = COMUtil._TAG_AUTOLINE_CONFIG; //자동추세선 설정
        // int tag = COMUtil._TAG_SYNC_CONFIG;//동기화 설정
        if (strOpenDlg == "indicator") {
            tag = COMUtil._TAG_INDICATOR_CONFIG
//            tag = COMUtil._TAG_INDICATORMINI_CONFIG
        } else if (strOpenDlg == "indicator_oneq") {
            tag = COMUtil._TAG_INDICATOR_CONFIG_ONEQ
        } else if (strOpenDlg == "divide") {
            tag = COMUtil._TAG_DIVIDECHART_CONFIG
        } else if (strOpenDlg == "save") {
            tag = COMUtil._TAG_SAVELOADMENU_CONFIG
        } else if (strOpenDlg == "sync") {
            tag = COMUtil._TAG_SYNC_CONFIG
        } else if (strOpenDlg == "toolbar" || strOpenDlg == "tool") {
            tag = COMUtil._TAG_TOOL_CONFIG
        } else if (strOpenDlg == "combo_min") {
            tag = COMUtil._TAG_SET_COMBOMIN
            periodName = "ccPeriodMin"
        } else if (strOpenDlg == "combo_tick") {
            tag = COMUtil._TAG_SET_COMBOTIC
            periodName = "ccPeriodTick"
        } else if (strOpenDlg == "comparePopup") {
            tag = COMUtil._TAG_COMPARISON_EDIT_CONFIG
        } else if (strOpenDlg == "load") {
            tag = COMUtil._TAG_LOADCONTROL_CONFIG
        }
        var params: LayoutParams? = null
        params = LayoutParams(this.width, this.height)
        params!!.leftMargin = 0
        params.topMargin = 0
        var triXpos = -1 // 팝업창의 삼각형 이미지 위치.

// 		MarginLayoutParams senderParams = sender.getParams();
// 		RectF rect = m_oFormMgr.getFormRect();
        val senderView = sender as View?
        val location = IntArray(2)
        senderView?.getLocationOnScreen(location)
        if (tag == COMUtil._TAG_INDICATOR_CONFIG || tag == COMUtil._TAG_INDICATOR_CONFIG_ONEQ) {
            // 2012. 8. 16  레이아웃 크기 및 위치 조절 : I_tab01
//            if (COMUtil.deviceMode == COMUtil.HONEYCOMB) {
//                if (m_chartType == ChartType.MINI_CHART) {
//                    val tWidth: Int = this.rootView.getWidth()
//                    params = LayoutParams(
//                        COMUtil.getPixel(306).toInt(), COMUtil.getPixel(650).toInt()
//                    )
//                    params.leftMargin = (tWidth / 2) - COMUtil.getPixel(306).toInt() / 2
//                    params.topMargin = 85
//                } else {
//                    params = LayoutParams(
//                        COMUtil.getPixel(306).toInt(), COMUtil.getPixel(650).toInt()
//                    )
//                    params.leftMargin =
//                        location[0] - COMUtil.getPixel(245).toInt() + sender.getWidthVal()
//                    triXpos = 225
//                }
//            } else {
            params = LayoutParams(
                COMUtil.apiView.width,
                COMUtil.apiView.height,
            )
            params.leftMargin = 0
            params.topMargin = COMUtil.apiView.top
//            }
        } else if (tag == COMUtil._TAG_INDICATORMINI_CONFIG) {
            params = LayoutParams(
                COMUtil.getPixel_W(180f).toInt(),
                this.height,
            )
            params.leftMargin =
                this.rootView.width - COMUtil.getPixel_W(180f)
                    .toInt()
            getLocationOnScreen(location)
            params.topMargin = location[1] - 38
            triXpos = 235
        } else if (tag == COMUtil._TAG_SAVELOADMENU_CONFIG) {
            // 2012. 8. 16  레이아웃 크기 조절 : SL_tab09
            if (COMUtil.deviceMode == COMUtil.HONEYCOMB) {
                params = LayoutParams(
                    COMUtil.getPixel(258).toInt(),
                    COMUtil.getPixel(140).toInt(),
                )
                params.leftMargin =
                    location[0] - sender!!.width - COMUtil.getPixel(200).toInt()
                params.topMargin = location[1] + sender!!.width + COMUtil.getPixel(3).toInt()
                triXpos = 235
            } else {
// 	    		params =new RelativeLayout.LayoutParams(
// 						(int)COMUtil.getPixel(106), (int)COMUtil.getPixel(70));
// 				params.leftMargin=location[0]-(int)COMUtil.getPixel(106)+sender.getWidthVal();
                // 2013. 7. 25 저장불러오기창 새 디자인 적용>>
                params = LayoutParams(
                    COMUtil.getPixel(88).toInt(),
                    COMUtil.getPixel(68).toInt(),
                )
                params.leftMargin =
                    location[0] - COMUtil.getPixel(88).toInt() + sender!!.width
                params.topMargin = location[1] + sender!!.height + COMUtil.getPixel(3).toInt()
                // 2013. 7. 25 저장불러오기창 새 디자인 적용>>
            }
        } else if (tag == COMUtil._TAG_DIVIDECHART_CONFIG) {
            if (COMUtil.deviceMode == COMUtil.HONEYCOMB) {
                params = LayoutParams(
                    COMUtil.getPixel(200).toInt(),
                    COMUtil.getPixel(260).toInt(),
                )
                params.leftMargin = location[0] - COMUtil.getPixel(100).toInt()
                params.topMargin = location[1] + sender!!.height + COMUtil.getPixel(3).toInt()
                triXpos = 105
            } else {
                params = LayoutParams(
                    COMUtil.getPixel(223).toInt(),
                    COMUtil.getPixel(249).toInt(),
                )
                params.leftMargin = location[0]
                params.topMargin = location[1]
            }
        } else if (tag == COMUtil._TAG_SYNC_CONFIG) {
            // 2012. 8. 16  레이아웃 크기 조절 : J_tab05
            if (COMUtil.deviceMode == COMUtil.HONEYCOMB) {
                params = LayoutParams(
                    COMUtil.getPixel(186).toInt(),
                    COMUtil.getPixel(170).toInt(),
                )
                params.leftMargin =
                    location[0] - sender!!.width - COMUtil.getPixel(50).toInt()
                params.topMargin = location[1] + sender!!.height + COMUtil.getPixel(3).toInt()
                triXpos = 85
            } else {
                params = LayoutParams(
                    COMUtil.getPixel(122).toInt(),
                    COMUtil.getPixel(130).toInt(),
                )
                if (sender != null) {
                    params.leftMargin =
                        location[0] - COMUtil.getPixel(122).toInt() + sender.height
                }
                params.topMargin = location[1] + sender!!.height + COMUtil.getPixel(3).toInt()
            }
        } else if (tag == COMUtil._TAG_COMPARISON_EDIT_CONFIG) {
            // openEditView();

//            params =new RelativeLayout.LayoutParams(
//                    (int)COMUtil.getPixel(338), (int)COMUtil.getPixel(650));
//            params.leftMargin=location[0]-(int)COMUtil.getPixel(245)+sender.getWidthVal();
//            params.topMargin=location[1]+sender.getHeightVal();
//
//            triXpos = 225;
            params = LayoutParams(
                COMUtil.apiView.width,
                COMUtil.apiView.height,
            )
            params.leftMargin = 0
            params.topMargin = COMUtil.apiView.top
            triXpos = 0
        }
        dic["tag"] = Integer.valueOf(tag)
        dic["apiView"] = this.rootView
        if (params != null) dic["frame"] = params
        dic["triXpos"] = triXpos.toString()
        if (cmpArr != null) dic["comparepopup"] = cmpArr
//        if (m_oFormMgr.getScreenType() !== FormManager.LTYPE_HORIZON) COMUtil.apiView =
//            Util.getIMainView()
        var viewGroup: ViewGroup =
            (COMUtil._chartMain.findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        COMUtil.apiView = viewGroup
        chartView?.selectChartMenuFromParent(dic)
    }
    // 2013.09.24 by LYH<<
    // 2015. 1. 21 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기>>
    /**
     * 메인 Script에서 가격(ex: 목표가 등) 을 전달 받아서 차트에서 해당 위치에 선을 그림
     *
     * @param sValue 차트에 선을 그리고자 하는 가격
     */
    fun drawYScalePriceLine(sValue: String?) {
        if (sValue != null && chartView != null) {
            chartView?.setMethod("drawYScalePriceLine", sValue)
        }
    }

    // 2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 <<
    // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 >>
    private var m_nLog = -1 // 표시진법

    private var m_nLogDisp = -1 // 진법자리수 ("'" 다음 데이터 사이즈)

    private var m_nPrecision = -1 // 소수점

    fun setScaleInfo(strScaleInfo: String?) {
        if (strScaleInfo == null || strScaleInfo.length <= 0) {
            return
        }
        val values = strScaleInfo.split(";").toTypedArray()
        var nLog = 0
        var nLogDisp = 0
        var nPrecision = 0
        if (values.size == 2) {
            nLog = values[0].toInt()
            nPrecision = values[1].toInt()
        }
        if (values.size == 3) {
            nLog = values[0].toInt()
            nLogDisp = values[1].toInt()
            nPrecision = values[2].toInt()
        }
        if (nLog == 1) {
            nLog = 10
        }
        if (nLog == 0 && nLogDisp == 0 && nPrecision == 0) {
            return
        } else {
            m_nLog = nLog
            m_nLogDisp = nLogDisp
            m_nPrecision = nPrecision
        }
    }

    // 2019.04.15 원터치 차트설정불러오기 추가 - lyj
    fun setOneTouchMode(sValue: String?) {
        m_sOneTouchMode = sValue!!
    }

    fun setShowOneTouchMode(sValue: String?) {
        m_sShowOneTouchMode = sValue!!
    }
    // 2019.04.15 원터치 차트설정불러오기 추가 - lyj

    // 2019.04.15 원터치 차트설정불러오기 추가 - lyj
    private fun getScaledPriceWithDot(dData: Double): Double {
        var dResult = dData
        dResult = dData / Math.pow(10.0, m_nPrecision.toDouble())
        return dResult
    }

    private fun getScaledPriceWithDot(strData: String): String? {
        if (strData.contains(".")) {
            return strData
        }
        var strResult = strData
        var dData: Double = strData.toDouble()
        if (m_nLog == 10) {
            dData = dData / Math.pow(10.0, m_nPrecision.toDouble())
        } else if (m_nLog == 8) {
            dData = dData / Math.pow(10.0, m_nLogDisp.toDouble())
            val dFractional: Double
            val dInt: Double
            dInt = Math.floor(dData)
            dFractional = dData - dInt + 0.00001
            dData = dInt + dFractional * 1.25 + 0.000001
        } else if (m_nLog == 32) {
            dData = dData / Math.pow(10.0, m_nLogDisp.toDouble())
            val dFractional: Double
            val dInt: Double
            dInt = Math.floor(dData)
            dFractional = dData - dInt + 0.00001
            dData = dInt + dFractional * 3.125 + 0.000001
        }
        strResult = String.format("%f", dData)
        return strResult
    }
    // 2019. 03. 22 by hyh - 표시할 데이타의 진법 정보 설정 <<
    // /////////// api function end //////////////////

    // 2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<
    fun setCompareData(sValue: String?) {
        if (sValue != null && chartView != null) {
            if (sValue.length > 0) {
                val values = sValue.split("^").toTypedArray()
                if (values.size > 3) {
                    // this.m_sCompMarket = values[0];
                    m_sCompMarket = values[3]
                    m_sCompCode = values[1]
                    m_sCompName = values[2]
                }
                setCompareChartSetting(sValue)
            }
        }
    }

    fun setCompareChartSetting(sValue: String) {
        // System.out.println("Debug_setCompareChartSetting:" + sValue);
        COMUtil.bSendCompareData = true
        if (m_chartType != ChartType.COMPARE_CHART) {
            return
        }
        if (cmpArr == null) {
            cmpArr = Vector(5)
        }
        if (m_sCompCode == "") {
            return
        }

        // String compareItem = "^" + this.m_sCompCode + "^" + this.m_sCompName
        // + "^" + market;

        // 동일종목 추가방지
        val cnt = cmpArr!!.size
        if (cnt < 1) {
            cmpArr!!.add(sValue)
        } else {
            if (!isExistCode(cmpArr, m_sCompCode)) {
                cmpArr!!.add(sValue)
            }
        }
        if (cmpArr!!.size > 5) {
            cmpArr!!.removeAt(0)
        }
        compareChartSetting()
    }

    fun isExistCode(src: Vector<String>?, cmpData: String): Boolean {
        if (src != null) {
            val cnt = src.size
            for (i in 0 until cnt) {
                val item = src.elementAt(i)
                val items = item.split("^").toTypedArray()
                if (items.size > 1) {
                    val code = items[1]
                    if (code == cmpData) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun compareChartSetting() {
        if (chartView == null) {
            return
        }
        var compareValue = ""
        var itemsValue = ""
        var cnt = 0
        for (i in 0..4) {
            if (cmpArr!!.size > i) {
                val item = cmpArr!!.elementAt(i)
                if (item != null) {
                    itemsValue = "$itemsValue$i$item^1;"
                    cnt++
                }
            } else {
                itemsValue = "$itemsValue$i^^^^0;"
            }
        }
        compareValue = "$cnt;$itemsValue"
        chartView?.setCompareData(compareValue, cmpArr)
    }

    fun setRequestCompareData(sValue: String?) {
        if (chartView != null) chartView?.setMethod("setRequestCompareData", sValue)
    }

    fun Activity.displayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    /**
     * 차트 타입
     *
     * @param type(현재가타입 = "mini" )
     */
    fun setChartType(type: String) {
        if (type == "mini") {
            m_chartType = ChartType.MINI_CHART
        } else if (type == "linefill") {
            m_chartType = ChartType.LINEFILL_CHART
        } else if (type == "minibong") {
            m_chartType = ChartType.MINI_BONG_CHART
        } else if (type == "compare") {
            m_chartType = ChartType.COMPARE_CHART
        } else if (type == "ratecompare") {
            m_chartType = ChartType.RATE_COMPARE_CHART
            // m_isDelPlusSign = false;
        } else if (type == "standardline") {
            m_chartType = ChartType.STANDARDLINE_CHART
        } else if (type == "subchart") {
            m_chartType = ChartType.SUB_CHART
        } else if (type == "pattern") {
            m_chartType = ChartType.PATTERN
        } else if (type == "3daychart") {
            m_chartType = ChartType.STANDARDLINE_CHART
            m_b3DayChart = true
        } else if (type == "line") {
            m_chartType = ChartType.LINE_CHART
        } else if (type == "line2") {
            m_chartType = ChartType.LINE2_CHART
        } else if (type == "pie") {
            m_chartType = ChartType.PIE_CHART
        } else if (type == "bar") {
            m_chartType = ChartType.BAR_CHART
        } else if (type == "horizontalstack") {
            m_chartType = ChartType.HORIZONTAL_STACK_CHART
        } else if (type == "today") {
            m_chartType = ChartType.TODAY_LINE_CHART
        } else if (type == "spider") {
            m_chartType = ChartType.SPIDER_CHART
        } else {
            m_chartType = ChartType.BASE_CHART
        }
    }

    fun createChart(
        mainActivity: AppCompatActivity,
        mainLayout: RelativeLayout,
        Iwidth: Int,
        Iheight: Int,
    ) {
        if (this.mainLayout != null) {
            this.mainLayout = null
        }

        this.mainLayout = mainLayout

        COMUtil.apiMode = true

        // COMUtil.userProtocol = this;//차트 인터페이스 연결
        // 2023.12.05 by lyk - 가로모드 변경시 차트의 참조 메인 액티비티가 변경되어 Preferences에 저장된 설정값 공유가 안되는 현상 수정 >>
        if (COMUtil._chartMain == null) {
            COMUtil.setChartMain(mainActivity) // Activity 인터페이스 연결
        }
        // 2023.12.05 by lyk - 가로모드 변경시 차트의 참조 메인 액티비티가 변경되어 Preferences에 저장된 설정값 공유가 안되는 현상 수정 <<

        COMUtil.apiView = mainLayout
        COMUtil.apiLayout = mainLayout // 기준선, 자동추세선을 추가할 ViewGroup설정.
        COMUtil.bIsMigyul = m_bIsMigyul
        COMUtil.bIsOneBlock = m_bIsOneBlock // 하나의 블럭에 봉그래프를 그리는 형태.

        matchingColorSet()

        if (m_sLoadFileName != null && m_sLoadFileName.contains("multi")) {
            m_bIsMultiChart = true
            COMUtil.bIsMulti = true
        } else {
            COMUtil.bIsMulti = false
        }
        if (!m_bHaveToolbar) {
            COMUtil.isAnalToolbar = false // 분석툴바 사용여부 설정
        } else {
            COMUtil.isAnalToolbar = true // 분석툴바 사용여부 설정
        }
        if (m_chartType == ChartType.MINI_CHART) { // 현재가 속성의 화면에선 분석툴바를 보여주지 않음.
            COMUtil.showAnalToolbar = false // YES, NO
        } else {
            COMUtil.showAnalToolbar = true // YES, NO
        }
        if (m_chartType == ChartType.COMPARE_CHART) {
            COMUtil.bIsCompare = true // YES, NO

            // init
            for (i in COMUtil.compareChecks.indices) {
                COMUtil.compareChecks[i] = true
            }
        } else {
            COMUtil.bIsCompare = false // YES, NO
        }

        // 2012. 11. 21 마운틴차트 플래그 세팅 : C31
        if (m_chartType == ChartType.MINI_BONG_CHART) {
            COMUtil.bIsMiniBong = true
        } else {
            COMUtil.bIsMiniBong = false
        }
        // 2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
        if (m_chartType == ChartType.RATE_COMPARE_CHART) {
            COMUtil.bRateCompare = true
        } else {
            COMUtil.bRateCompare = false
        }
        // 2013.01.04 by LYH <<
        // 2013.07.31 >> 기준선 라인 차트 타입 추가
        if (m_chartType == ChartType.STANDARDLINE_CHART) {
            COMUtil.bStandardLine = true
            // 2015.01.08 by LYH >> 3일차트 추가
            if (m_b3DayChart) {
                COMUtil.b3DayChart = true
            } else {
                COMUtil.b3DayChart = false
            }
            // 2015.01.08 by LYH << 3일차트 추가
        } else {
            COMUtil.bStandardLine = false
        }
        // 2013.07.31 <<
        // 2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
        if (m_chartType == ChartType.SUB_CHART) {
            COMUtil.bSubChart = true
        } else {
            COMUtil.bSubChart = false
        }
        // 2013.09.13 <<
        // 2020.04.14 당일 라인차트 추가 - hjw >>
        if (m_chartType == ChartType.TODAY_LINE_CHART) {
            COMUtil.bIsTodayLineChart = true
        } else {
            COMUtil.bIsTodayLineChart = false
        }
        // 2020.04.14 당일 라인차트 추가 - hjw <<

        // 2013.11.27 lineColor 설정
        if (m_sLineColor != "") {
            COMUtil.m_sLineColor = m_sLineColor
        } else {
            COMUtil.m_sLineColor = ""
        }

        // 2013.11.27 textColor 설정
        if (m_sTextColor != "") {
            COMUtil.m_sTextColor = m_sTextColor
        } else {
            COMUtil.m_sTextColor = ""
        }
        if (m_chartType == ChartType.PIE_CHART) {
            COMUtil.base =
                "base15"
        } else if (m_chartType == ChartType.PATTERN) {
            COMUtil.base =
                "base13"
        } else if (m_chartType == ChartType.BAR_CHART) {
            COMUtil.base =
                "base16"
        } else if (m_chartType == ChartType.HORIZONTAL_STACK_CHART) {
            COMUtil.base =
                "base23"
        } else if (m_chartType == ChartType.SPIDER_CHART) {
            COMUtil.base =
                "base17"
        } else {
            COMUtil.base = "base11"
        }
//        chartLayout = RelativeLayout(relativeLayout.context)
        // chartLayout.setBackgroundColor(Color.WHITE);
//        chartLayout!!.setTag("chartLayout")
//        chartLayout!!.setLayoutParams(RelativeLayout.LayoutParams(Iwidth, Iheight))
        COMUtil.chartWidth = Iwidth
        COMUtil.chartHeight = Iheight
        // 			if(!SystemUtil.isTx())
        // COMUtil.setSkinType(COMUtil.SKIN_WHITE);
//        if (SystemUtil.isTablet()) {
//            COMUtil.deviceMode = COMUtil.HONEYCOMB
//        }
        // COMUtil.roundType = "ceil2";

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // 일반 모드일때
            COMUtil.currentTheme = COMUtil.SKIN_WHITE; // 2015. 3. 4 차트 테마 메인따라가기 추가
        } else {
            // 다크 모드일때
            COMUtil.currentTheme = COMUtil.SKIN_BLACK; // 2015. 3. 4 차트 테마 메인따라가기 추가
        }

        COMUtil.roundType = "ceil"
        COMUtil.bHaveMA = m_bHaveMA

// 			NeoChart2 chart = COMUtil._neoChart;
// 			if(chart != null)
// 			{
// 				chartLayout.setVisibility(View.GONE);
// 				this.removeView(chartLayout);
// 			}
// 			if(SystemUtil.isTablet())
// 	 		{
// 				COMUtil.nFontSize = ResourceManager.getFontSize(-2);
// 	 		} else{
// 	 			COMUtil.nFontSize = ResourceManager.getFontSize(-5);
// 	 		}
//    		float rtnVal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, COMUtil.nFontSize,
//    				getContext().getResources().getDisplayMetrics());
//
//        if (m_chartType == ChartType.LINE_CHART || m_chartType == ChartType.LINEFILL_CHART ) {
//            COMUtil.nFontSize = 10;
//        } else if (m_chartType == ChartType.STANDARDLINE_CHART || m_chartType == ChartType.MINI_BONG_CHART) {
//            COMUtil.nFontSize = 10;
//        }
//        else if (m_chartType == ChartType.TODAY_LINE_CHART || m_chartType == ChartType.COMPARE_CHART ) {
//            COMUtil.nFontSize = 10;
//        } else {
        COMUtil.nFontSize = 11f
        //        }
        val rtnVal = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COMUtil.nFontSize,
            mainLayout.context.getResources().getDisplayMetrics(),
        )
        COMUtil.nFontSize_paint = rtnVal
//        COMUtil.typeface = ResourceManager.getFontRegular()
//        COMUtil.typefaceMid = ResourceManager.getFont()
//        COMUtil.typefaceBold = ResourceManager.getFontBold()
//        COMUtil.numericTypeface = ResourceManager.getNumericFontRegular()
//        COMUtil.numericTypefaceMid = ResourceManager.getNumericFont()
//        COMUtil.numericTypefaceBold = ResourceManager.getNumericFontBold()

// 			System.out.println("######### CtlChartEx create");
        if (chartView != null) return // 2024.02.07 by SJW - 생성된 차트가 있으면 생성x
        chartView = MainFrame(mainActivity, mainLayout)
        COMUtil._mainFrame = chartView
        chartView?.userProtocol = this

        // set ColorSet
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        chartView?.setChartColors((mainActivity as KfitChartViewActivity).CHART_COLORS)
        chartView?.setChartColors(CHART_COLORS)
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
        if (m_chartType == ChartType.LINE_CHART) {
            chartView?.setMethod("line", "YES")
            chartView?.setMethod("isAnalToolbar", "NO")
            chartView?.setMethod("isSimpleSet", "NO")
        } else if (m_chartType == ChartType.LINE2_CHART) {
            chartView?.setMethod("line2", "YES")
            chartView?.setMethod("isAnalToolbar", "NO")
            chartView?.setMethod("isSimpleSet", "NO")
        } else {
            chartView?.setMethod("line", "NO")
        }
        if (m_chartType == ChartType.LINEFILL_CHART) {
            chartView?.setMethod("linefill", "YES")
            chartView?.setMethod("isAnalToolbar", "NO")
        } else {
            chartView?.setMethod("linefill", "NO")
        }
        if (m_chartType == ChartType.BASE_CHART || m_chartType == ChartType.MINI_BONG_CHART) {
//        if (m_chartType == ChartType.BASE_CHART) {
//            chartView.setMethod("isSimpleSet", "YES");
            COMUtil.bHaveMA = true
        }
        if (m_sHighLowSign != "") {
            chartView?.setMethod("highlowsign", "YES")
        } else {
            chartView?.setMethod("highlowsign", "NO")
        }

        // 2019. 03. 14 by hyh - 테크니컬차트 개발 >>
//        if (m_sTechnical != "") {
//            chartView?.setMethod("technical", "YES")
//        } else {
//            chartView?.setMethod("technical", "NO")
//        }
        // 2019. 03. 14 by hyh - 테크니컬차트 개발 <<

        // 2019. 04. 16 by hyh - 시세알람차트 개발 >>
//        if (m_sAlarmChart != "" && m_sAlarmChart == "1") {
//            chartView?.setMethod("setAlarmChart", "YES")
//        } else {
//            chartView?.setMethod("setAlarmChart", "NO")
//        }
        // 2019. 04. 16 by hyh - 시세알람차트 개발 <<

        // 2019. 05. 30 by hyh - 매매연습차트 개발 >>
//        if (m_sTradeChart != "" && m_sTradeChart == "1") {
//            chartView?.setMethod("setTradeChart", "YES")
//        } else {
//            chartView?.setMethod("setTradeChart", "NO")
//        }
        // 2019. 05. 30 by hyh - 매매연습차트 개발 <<

        // 2019. 04. 01 by hyh - 차트 타이틀 적용 >>
        if (m_sChartTitle != "") {
            chartView?.setMethod("charttitle", m_sChartTitle)
        }
        if (m_sChartTitleColor != "") {
            chartView?.setMethod("charttitlecolor", m_sChartTitleColor)
        }
        // 2019. 04. 01 by hyh - 차트 타이틀 적용 <<

        // 2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<
        if (m_sFxMarginType != "") {
            chartView?.setMethod("setFxMarginType", m_sFxMarginType)
        }
        if (m_enMarketCategory === ENMarketCategoryType.GE_MARKET_CATE_FX) {
            COMUtil.bHaveMA = false
        }
        // 2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

        // 2019.04.15 원터치 차트설정불러오기 추가 - lyj
        if (m_sOneTouchMode != "") {
            chartView?.setMethod("oneTouchMode", "YES")
        } else {
            chartView?.setMethod("oneTouchMode", "NO")
        }
        if (m_sShowOneTouchMode == "1") {
            chartView?.setMethod("ShowOneTouchMode", "YES")
        } else {
            chartView?.setMethod("ShowOneTouchMode", "NO")
        }
        chartView?.setMethod("LoadFileName", m_sLoadFileName)
        // 2019.04.15 원터치 차트설정불러오기 추가 - lyj end
        if (m_sOpenAllScreen == "1") {
            chartView?.setMethod("ShowBtnOpenAllScreen", "YES")
        } else {
            chartView?.setMethod("ShowBtnOpenAllScreen", "NO")
        }
        if (m_sOpenAlarmScreen == "1") {
            chartView?.setMethod("ShowBtnOpenAlarmScreen", "YES")
        } else {
            chartView?.setMethod("ShowBtnOpenAlarmScreen", "NO")
        }
        if (m_bIsOneQStockChart) {
            chartView?.setMethod(
                "setOneQStockChart",
                "YES",
            )
        } else {
            chartView?.setMethod("setOneQStockChart", "NO")
        }
        if (m_bUseOneQToolBar) {
            chartView?.setMethod(
                "useOneQToolBar",
                "YES",
            )
        } else {
            chartView?.setMethod("useOneQToolBar", "NO")
        }
        if (m_bHideXScale) {
            chartView?.setMethod("setHideXscale", "YES")
        } else {
            chartView?.setMethod(
                "setHideXscale",
                "NO",
            )
        }
        chartView?.setUI()
        chartView?.mainBase!!.baseP.nMarketType = m_enMarketCategory.ordinal

//    		if(chartView!=null) {
//    			chartView.mainBase.baseP._chart.setVisibility(View.INVISIBLE);
//    			//chartView.mainBase.baseP._chart.onPause();
//    		}

        // 2015. 2. 4 최초 차트 로딩했을 때 메인쪽 테마 따라가도록 설정. 그 이후엔 차트의 skintype따라감 >>
//        if (ConfigUtil.getCurrentTheme().equals("black")) {
//            chartView.setSkinType("black");
//            COMUtil.currentTheme = COMUtil.SKIN_BLACK;    //2015. 3. 4 차트 테마 메인따라가기 추가
//        }
//        else {
//            chartView.setSkinType("white");
//            COMUtil.currentTheme = COMUtil.SKIN_WHITE;    //2015. 3. 4 차트 테마 메인따라가기 추가
//        }

        // 다크모드인지 아닌지 알수 있는 분기문(다크모드 변경될때 무조건 앱UI가 다시 그려진다)
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // 일반 모드일때
//            COMUtil.setSkinType(COMUtil.SKIN_WHITE);
            chartView?.setSkinType("white")
//            COMUtil.currentTheme = COMUtil.SKIN_WHITE;    //2015. 3. 4 차트 테마 메인따라가기 추가
        } else {
            // 다크 모드일때
//            COMUtil.setSkinType(COMUtil.SKIN_BLACK);
            chartView?.setSkinType("black")
//            COMUtil.currentTheme = COMUtil.SKIN_BLACK;    //2015. 3. 4 차트 테마 메인따라가기 추가
        }
        // 2015. 2. 4 최초 차트 로딩했을 때 메인쪽 테마 따라가도록 설정. 그 이후엔 차트의 skintype따라감 <<
        if (m_bIsMultiChart) {
//        		chartView.loadLastSaveState("chart");
        } else {
//        		chartView.loadLastSaveState("");
        }
        if (m_sLoadFileName.length > 0) {
            m_bIsLoadChart = true
        }
        if (m_sLoadFileName.length > 0) chartView?.loadLastSaveState(m_sLoadFileName)

//        if (m_chartType != ChartType.BASE_CHART) {
//            chartView.setMethod("setChartItemViewHidden", "1");
//        }
        if (m_sChartBgTransparent != "") {
            chartView?.setMethod("setChartBgTransparent", "1")
        }
        if (m_sHideChartTitle != "") {
            chartView?.setMethod("setHideChartTitle", "1")
        }

//        mainLayout.addView(chartLayout)

        // 2013. 1. 22 차트 최초 오픈시에 분틱값이 없으면 초기값 세팅
        // 최초 데이터 없을때만 초기화
        if (m_chartType == ChartType.BASE_CHART) {
            COMUtil.resetBunTicPeriodList(
                true,
            )
        }

        // 2013. 1. 21  분틱 콤보 값 설정
//        setComboListItems()
        val wm = COMUtil._chartMain.windowManager
        val display = wm.defaultDisplay
        COMUtil.g_nDisWidth = display.width
        COMUtil.g_nDisHeight = display.height

//        // 2022.06.09 by lyk - 차트 실행시 초기 구매평균 설정값 불러오기 수정 (차트에서 저장값 불러오는 시점보다 우선이기 때문에 생성시 한번 호출 해줌) >>
//        try {
//            var pref = mainActivity.getPreferences(Activity.MODE_PRIVATE)
//
//            //2023.11.27 by lyk - kakaopay - 외부 설정값 저장소 별도 구성(추가수정) >>
//            if (m_sLoadFileName == "stock") {
//                pref = COMUtil._chartMain.getPreferences(Context.MODE_PRIVATE);
//            } else if (m_sLoadFileName != "") {
//                pref = COMUtil._chartMain.getSharedPreferences(m_sLoadFileName, Context.MODE_PRIVATE);
//            } else {
//                pref = COMUtil._chartMain.getPreferences(Context.MODE_PRIVATE);
//            }
//            //2023.11.27 by lyk - kakaopay - 외부 설정값 저장소 별도 구성(추가수정) <<
//
//            var isValue = pref.contains("avgBuyPriceFunc")
//
//            if (isValue == true) {
//                if (pref.getBoolean("avgBuyPriceFunc", false) == false) {
//                    avgBuyPriceFunc = "0"
//                } else {
//                    avgBuyPriceFunc = "1"
//                }
//            } else {
//                avgBuyPriceFunc = "1"
//            }
//        } catch (exUE: UnsupportedEncodingException) {
//            avgBuyPriceFunc = "1"
//        }
//        // 2022.06.09 by lyk - 차트 실행시 초기 구매평균 설정값 불러오기 수정 (차트에서 저장값 불러오는 시점보다 우선이기 때문에 생성시 한번 호출 해줌) <<
        if (m_avgBuyPrice != "") {
            setAvgBuyPriceData(m_avgBuyPrice)
        }
        // OpenDlg("toolbar");
    }

    /**
     * 차트 데이터 호출 리스너
     * createChart 와 Listener 등록 분리
     */
    fun setListener(listener: OnChartListener) {
        if (this.onChartListener != null) {
            this.onChartListener = null
        }

        this.onChartListener = listener
    }

    /* UserProtocol Interface */
    override fun requestInfo(tag: Int, data: Hashtable<String?, Any?>?) {
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 >>
        if ((componentName?.shortClassName?.length ?: 0) > 1) {
            topActivityName = componentName!!.shortClassName.substring(1)
        }
        val currentActivityName = topActivityName.split(".").last()
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 <<
        // System.out.println("=================>tag        "+tag);
        if (tag == COMUtil.PERIOD_CONFIG_DAY) { // 기간설정(일)
            this.period = "2"
            this.unit = "1"
        } else if (tag == COMUtil.PERIOD_CONFIG_WEEK) { // 기간설정(주)
            this.period = "3"
            this.unit = "1"
        } else if (tag == COMUtil.PERIOD_CONFIG_MONTH) { // 기간설정(월)
            this.period = "4"
            this.unit = "1"
        } else if (tag == COMUtil.PERIOD_CONFIG_MIN) { // 기간설정(분)
            this.period = "1"
            this.unit = "1"
        } else if (tag >= COMUtil.PERIOD_CONFIG_MIN1 && tag <= COMUtil.PERIOD_CONFIG_MIN6) { // 기간설정(분)
            this.period = "1"
            val unitVal = data!!["unit"].toString()
            this.unit = unitVal
        } else if (tag == COMUtil.PERIOD_CONFIG_TIC) { // 기간설정(tic)
            this.period = "0"
            this.unit = "1"
        } else if (tag == COMUtil._TAG_STORAGE_TYPE) { // 저장된 차트 정보
//        	System.out.println("========================>    _TAG_STORAGE_TYPE nKey : " + nKey);
            val symbol = data!!["symbol"].toString()
            var strUnit = data["unit"].toString()
            val strPeriod = data["period"].toString()

            // 2019. 06. 26 by hyh - 초 주기는 무조건 30초로 고정 >>
            if (strPeriod == "6") {
                strUnit = "30"
            }
            // 2019. 06. 26 by hyh - 초 주기는 무조건 30초로 고정 <<
            var selIndex = data["selIndex"].toString()
            if (selIndex == null || selIndex == "-1") { // 2016. 11. 03 by hyh. 멀티차트 분할 선택 인덱스 개선
                selIndex = "0"
            }
            var strInit = data["initChart"] as String?
            if (strInit == null) {
                strInit = "0"
            }
            val market = data["market"].toString()

            // 2022.04.11 by lyk - kakaopay - 구매평균가격 표시 디폴트 true로 변경 대응 (기획요청) >>
            if (data["avgBuyPriceFunc"] != null) {
                avgBuyPriceFunc = data["avgBuyPriceFunc"].toString()
            } else {
                avgBuyPriceFunc =
                    "1"; // 2022.04.11 by lyk - kakaopay - 최초설치 후 실행시 디폴트값 "1"로 설정 (구매평균 데이터가 들어오는 시점이 이곳보다 먼저이기 때문에 디폴트값을 여기서 설정함)
            }

            if (data["buySellPriceFunc"] != null) {
                buySellPriceFunc = data["buySellPriceFunc"].toString()
            }
            if (data["corporateCalendarFunc"] != null) {
                corporateCalendarFunc = data["corporateCalendarFunc"].toString()
            }
            // 2022.04.11 by lyk - kakaopay - 구매평균가격 표시 디폴트 true로 변경 대응 (기획요청) <<

            // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
            val objFxMarginType = data["fxmargintype"]
            if (objFxMarginType != null) {
                m_sFxMarginType = objFxMarginType.toString()
            }
            if (market == "4" && m_sFxMarginType == "-1") {
                m_sFxMarginType = "0"
            }
            val objConn = data["conn"]
            if (objConn != null) {
                m_sConn = objConn.toString()
                m_sConn = if (m_sConn == "true" || m_sConn == "1") "1" else "0"
            }
            val objDay = data["day"]
            if (objDay != null) {
                m_sDay = objDay.toString()
                m_sDay = if (m_sDay == "true" || m_sDay == "1") "1" else "0"
            }
            val objFloor = data["floor"]
            if (objFloor != null) {
                m_sFloor = objFloor.toString()
                m_sFloor = if (m_sFloor == "true" || m_sFloor == "1") "1" else "0"
            }
            // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
            val strData =
                "$symbol;$strPeriod;$strUnit;$strInit;$market;$m_sFxMarginType;$m_sConn;$m_sDay;$m_sFloor"
            // 2013.10.02 by LYH<<
            // System.out.println("==========CtlChartEx:"+strData);
            var nIndex = 1
            if (!m_bIsLoadChart || m_bIsLoadChart && selIndex != null && selIndex.toInt() > 0) {
                this.period = strPeriod
                this.unit = strUnit
                nIndex = selIndex.toInt() + 1

//              // 초기화 버튼시 작업
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                (mainActivity as KfitChartViewActivity).refreshUpdateOutputData()
                if (currentActivityName == "KfitChartViewActivity") {
                    onChartListener?.requestChartSnapshot(false)
                } else {
                    onChartListener?.requestChartSnapshot(false)
                }
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            }
        } else if (tag == COMUtil._TAG_DATACNT_CONFIG) { // 데이터 조회 변경 요청.
            //        self.viewNum = [[dic objectForKey("viewNum"] intValue];
            //        self.inquiryNum = [[dic objectForKey("inquiryNum"] intValue];
            //
            //        setCountText:self.inquiryNum]; // view/dataCnt 갱신.
        } else if (tag == COMUtil._TAG_VIEWCNT_CONFIG) { // 데이터 뷰 갯수 변경 요청.
            // viewCnt 업데이트.
            //        self.viewNum = [[dic objectForKey("viewNum"] intValue];
            //        int dataCnt = [[dic objectForKey("dataCnt"] intValue];
            //
            //        setCountText:dataCnt]; // view/dataCnt 갱신.
            return
        } else if (tag == COMUtil._TAG_REQUESTADD_TYPE) {
            if (data == null) return
            val nKey: String? = data["nKey"].toString()
            if (nKey.isNullOrBlank()) { // null 이거나 공백문자만 있거나 빈문자열일경우
                m_isConti = false
                return
            } else {
                m_isConti = true
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                (mainActivity as KfitChartViewActivity).requestBaseChartNextData()
                if (currentActivityName == "KfitChartViewActivity") {
                    onChartListener?.requestChartSnapshot(true)
                } else {
                    onChartListener?.requestChartSnapshot(true)
                }
                // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            }
            // nkey설정 // nextkey 설정
        } else if (tag == COMUtil._TAG_REQUEST_MARKET_TYPE) {
            if (data!!["nTotCount"] != null) {
                m_nTotCount = data["nTotCount"].toString().toInt()
//                (mainActivity as KfitChartViewActivity).mrkIndicTotalDataList.clear()
                if (m_enPacketPeriod === ENPacketPeriodType.GE_PACKET_PERIOD_DAILY) {
                    val title = data["title"] as String?
                    // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//                    (mainActivity as KfitChartViewActivity).reqMarketData(title, m_nTotCount)
                    if (currentActivityName == "KfitChartViewActivity") {
                        onChartListener?.requestMarketData(title.orEmpty(), m_nTotCount)
                    } else {
                        onChartListener?.requestMarketData(title.orEmpty(), m_nTotCount)
                    }
                    // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
                }
            }
        } else if (tag == COMUtil._TAG_SELECTED_CHART) {
            val nSelIndex = data!!["selIndex"].toString().toInt()
            val strInfo = data["priceInfo"].toString()

            // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
            val objFxMarginType = data["fxmargintype"]
            if (objFxMarginType != null) {
                m_sFxMarginType = objFxMarginType.toString()
            }
            val objConn = data["conn"]
            if (objConn != null) {
                m_sConn = objConn.toString()
                m_sConn = if (m_sConn == "true" || m_sConn == "1") "1" else "0"
            }
            val objDay = data["day"]
            if (objDay != null) {
                m_sDay = objDay.toString()
                m_sDay = if (m_sDay == "true" || m_sDay == "1") "1" else "0"
            }
            val objFloor = data["floor"]
            if (objFloor != null) {
                m_sFloor = objFloor.toString()
                m_sFloor = if (m_sFloor == "true" || m_sFloor == "1") "1" else "0"
            }
            // 2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
            val strTypes: String = m_sFxMarginType + ";" + m_sConn + ";" + m_sDay + ";" + m_sFloor
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnSelectedChart",
//                "ISS",
//                "<<" + (nSelIndex + 1) + ">><<" + strInfo + ">><<" + strTypes + ">>"
//            )

            // 2019. 12. 03 by hyh - 차트영역 선택 시 주기 변경 >>
            val arrInfo: Array<String?> = strInfo.split(";").toTypedArray()
            if (arrInfo.size > 3) {
                val strPeriod = arrInfo[2]
                val strUnit = arrInfo[3]
                if (strPeriod != null && strPeriod != "") {
                    this.setPeriodType(strPeriod)
                }
                if (strUnit != null && strUnit != "") {
                    this.setPeriodTime(strUnit)
                }
            }
            // 2019. 12. 03 by hyh - 차트영역 선택 시 주기 변경 <<
        } else if (tag == COMUtil._TAG_RESET_MULTICODES) {
            val nTotNum = data!!["totNum"].toString().toInt()
            val sCodeData = data["sCodeData"].toString()
            val sRealData = data["sRealData"].toString()

//            System.out.println("_TAG_RESET_MULTICODES value:"+sCodeData);
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnDivideChart", "ISS",
//                "<<$nTotNum>><<$sCodeData>><<$sRealData>>"
//            )
        } else if (tag == COMUtil._TAG_SET_PERIOD_UNITS) {
//            String strInfo = data.get("periodList");
//
//            System.out.println("_TAG_SET_PERIOD_UNITS value:"+strInfo);
//
//            m_oFormMgr.fireEvent( m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnChangePeriod", "S", "<<"+strInfo+">>" );
        } else if (tag == COMUtil._TAG_SELECT_VIEW) {
            COMUtil._mainFrame = chartView
            COMUtil._neoChart = chartView?.mainBase?.baseP?._chart
        } else if (tag == COMUtil._TAG_SELECT_PERIOD) {
            var nIndex = 0
            this.unit = data!!["value"].toString()
            if (periodName == "ccPeriodMin") {
                setPeriodType("1")
//                setChartTickButton(this.unit)
                nIndex = 1
                // }else if(periodName.equals("ccPeriodTick")){
            } else if (periodName == "ccPeriodTick") {
                setPeriodType("0")
//                setChartTickButton(this.unit)
                nIndex = 0
            }
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangePeriod",
//                "IS",
//                "<<" + nIndex + ">><<" + this.unit + ">>"
//            )
        } else if (tag == COMUtil._TAG_SYNC_PERIODVALUE) {
            val strPeriodValue = data!!["PeriodValue"] as String?
            val strMethodType = data["MethodType"] as String?
            if (strMethodType == "get") {
//                m_oFormMgr.fireEvent(
//                    m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                    "OnGetPeriodInfo",
//                    "",
//                    ""
//                )
            } else if (strMethodType == "set") {
//                m_oFormMgr.fireEvent(
//                    m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnChangePeriod", "S",
//                    "<<$strPeriodValue>>"
//                )
            }
        } else if (tag == COMUtil._TAG_SET_ADJUSTEDSTOCK) { // 수정주가 관련
            val adjustedStock = data!!["adjustedStock"].toString()
            val value = "<<$adjustedStock>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangeAdjustedStock",
//                "S",
//                value
//            )
            // 2013.09.17 by LYH >> 패턴 그리기 추가.
        } else if (tag == COMUtil._TAG_SET_REALBONG) { // 실봉+허봉 처리
            val realbong = data!!["realbong"] as String?
            val value = "<<$realbong>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnRealBong", "S", value
//            )
            // 2013.09.17 by LYH >> 패턴 그리기 추가.
        } else if (tag == COMUtil._TAG_DRAW_PATTERN) { // 패턴 그리기 완료
//            send_Pattern()
            // 2013.09.17 by LYH <<
        } else if (tag == COMUtil._TAG_SEND_YSCALEDRAGPRICE) {
            val strYsaleDragValue = data!!["yscaleDragPrice"].toString().replace(",", "")
            val value = "<<$strYsaleDragValue>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangeYScaleDragPrice",
//                "S",
//                value
//            )
            if (m_bUseDrawPriceLine) {
                drawYScalePriceLine(strYsaleDragValue)
            }
        } else if (tag == COMUtil._TAG_REQUEST_UPPERLOWERLIMIT) {
            m_strUpperLowerCodes = data!!["upperLowerCodes"].toString()
            m_strUpperLowerCounts = data["upperLowerCounts"].toString()
            m_nUpperLowerIndex = 0
            m_nUpperLowerCountIndex = 0
//            requestChartUpperLowerLimitData(false)
        } else if (tag == COMUtil._TAG_CHANGE_REQUESTDATALENGTH) {
            val strRequestDataLength = data!!["RequestDataLength"].toString().replace(",", "")
            val value = "<<$strRequestDataLength>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangeRequestDataLength",
//                "S",
//                value
//            )
        } else if (tag == COMUtil._TAG_SET_MOD_SKIP) { // 만기보정, 제외기준
            val strModYn = data!!["modYn"].toString()
            val strSkipTp = data["skipTp"].toString()
            val strSkipTick = data["skipTick"].toString()
            val strSkipVol = data["skipVol"].toString()
            val value = "<<$strModYn>><<$strSkipTp>><<$strSkipTick>><<$strSkipVol>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangeModSkip",
//                "SSSS",
//                value
//            )
        } else if (tag == COMUtil._TAG_OPEN_ALL_SCREEN) {
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnOpenAllScreen",
//                "",
//                ""
//            )
        } else if (tag == COMUtil._TAG_OPEN_ALARM_SCREEN) {
            // 2019. 06. 14 by hyh - 시세알람차트 개선. 알람켜기 전에 차트 저장 >>
            this.setSaveChart()
            // 2019. 06. 14 by hyh - 시세알람차트 개선. 알람켜기 전에 차트 저장 <<
            val objDate = data!!["date"]
            var strDate = ""
            val objPrice = data["price"]
            var strPrice = ""
            if (objPrice != null && strDate != null) {
                strDate = objDate.toString()
                strDate = strDate.replace("/".toRegex(), "") // "/" 제거
                strPrice = objPrice.toString()
                strPrice = strPrice.replace(",".toRegex(), "") // 1000단위 쉼표 제거
                val strValue = "<<" + this.period + ";" + strDate + ";" + strPrice + ">>"
//                m_oFormMgr.fireEvent(
//                    m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                    "OnOpenAlarmScreen",
//                    "S",
//                    strValue
//                )
            }
        } else if (tag == COMUtil._TAG_TOUCH_ALARM_CHART) {
            val objPrice = data!!["price"]
            var strPrice = ""
            if (objPrice != null) {
                strPrice = objPrice.toString()
                strPrice = strPrice.replace(",".toRegex(), "") // 1000단위 쉼표 제거
                val strValue = "<<$strPrice>>"
//                m_oFormMgr.fireEvent(
//                    m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                    "OnTouchAlarmChart",
//                    "S",
//                    strValue
//                )
            }
        } else if (tag == COMUtil._TAG_SET_ONETOUCHMODE) {
            val strData = data!!["isOneTouchMode"] as String?
            val value = "<<$strData>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnOneTouchMode", "S", value
//            )
        } else if (tag == COMUtil._TAG_SET_COMPARECHART) { // 비교차트
            val code = data!!["code"] as String?
            val name = data["name"] as String?
            val market = data["market"] as String?
            val period = data["period"] as String?
            val unit = data["unit"] as String?
            if (code != null) {
                this.m_sCompCode = code
            }
            if (name != null) {
                this.m_sCompName = name
            }
            if (market != null) {
                this.m_sCompMarket = market
            }
            val strData =
                code + ";" + java.lang.String.valueOf(m_enPacketPeriod.ordinal) + ";" + unit + ";" + market + ";" + name
            val nIndex = 1

            // test data
//            this.setCompareData("^000660^하이닉스^S31")
//            this.setRequestCompareData("1")
//            (mainActivity as ChartViewActivity).addCompareData()

//            m_oFormMgr.clearWaitCursor()
//            //this.sendRequestChartData(m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnRequestData", "IS", "<<" + nIndex + ">><<" + strData + ">>");
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnRequestData", "IS",
//                "<<$nIndex>><<$strData>>"
//            )
        } else if (tag == COMUtil._TAG_INDICATOR_CONFIG) // 비교차트 설정창, 멀티설정창 띠울떄 사용됨
            {
                val configView = data!!["view"] as View?
                val nAddRemove: Int = data["addremove"].toString().toInt()
                if (nAddRemove == 2) {
                    (COMUtil.apiView.parent as ViewGroup).removeView(configView)
                } else {
                    (COMUtil.apiView.parent as ViewGroup).addView(
                        configView,
                    )
                }
            } else if (tag == COMUtil._TAG_SET_COMPARECONFIG_CLOSE) // 비교차트 설정창 닫을떄 호출됨
            {
                val strCC = data!!["compConnect"] as String?
                val strFBS = data["compFxbuysell"] as String?
                val strData = "$strCC;$strFBS"
                val value = "<<$strData>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnChangeCompareConfig",
//                "S",
//                value
//            )
//
//            //2019. 06. 17 by hyh - 비교차트 FX 실시간 처리
//            m_strCompFxBS = strFBS
            } else if (tag == COMUtil._TAG_REQUESTADD_BYNUMBER) { // 봉갯수 변경
            val viewnum = data!!["viewnum"].toString()
            val value = "<<$viewnum>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnByNumber",
//                "S",
//                value
//            )

            // 2019. 07. 30 by hyh - 봉개수가 데이터 최대치 넘지 못하도록 처리 >>
            m_nRequestAddByNumber = viewnum.toInt()
            // 2019. 07. 30 by hyh - 봉개수가 데이터 최대치 넘지 못하도록 처리 <<
        } else if (tag == COMUtil._TAG_SET_TOOLBAR_CLOSE) // 비교차트 설정창 닫을떄 호출됨
            {
                val strState = data!!["toolbarState"] as String?
                val value = "<<$strState>>"
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName),
//                "OnCloseChartToolBar",
//                "S",
//                value
//            )
            } else if (tag == COMUtil._TAG_OPEN_SCREEN) {
            val sScreen = data!!["screen"].toString()
//            m_oFormMgr.fireEvent(
//                m_oCtrlMgr.getFullEventCtlName(m_sCtlName), "OnOpenScreen", "S",
//                "<<$sScreen>>"
//            )
        }
        if (tag == COMUtil._TAG_SAVE_LOCAL_CLOUD) {
            // this.send_gdhtsSvc006();
        } else if (tag == COMUtil._TAG_LOAD_LOCAL_CLOUD) {
            // this.send_gdhtsSvc007();
        }
        if (tag != COMUtil._TAG_REQUESTADD_TYPE) { // 추가데이터 요청이 아니면, nextKey 초기화.
            //        self.nextKey = nil;
        }
    }

    fun destroy() {
        if (chartView != null) {
            chartView?.stopAll() // remove chart
            // 2024.02.07 by SJW - 차트 메모리 관리 시점 변경 >>
//            if (chartView == COMUtil._mainFrame) {
//                COMUtil.apiView = null
//                COMUtil._mainFrame = null
//                COMUtil._chartMain = null
//                COMUtil._neoChart = null
//                COMUtil.apiLayout = null
//            }
            // 2024.02.07 by SJW - 차트 메모리 관리 시점 변경 <<
            chartView = null
        }

        this.mainLayout?.removeView(chartLayout)
    }

    fun clear() {
        COMUtil.apiView = null
        COMUtil._mainFrame = null
        COMUtil._chartMain = null
        COMUtil._neoChart = null
        COMUtil.apiLayout = null
    }

    //2024.07.18 by SJW - KfitChartViewFragment와 구조 통일 >>
    fun onDestroyView() {
        destroy()
        if (chartView == getMainFrame()) {
            clear()
        }
    }
    //2024.07.18 by SJW - KfitChartViewFragment와 구조 통일 <<

    fun convertDateToTimestamp(date: String): Long {
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.parse(date).time
    }

    fun convertTimestampToDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        if (timestamp.toString().length < 11) {
            calendar.timeInMillis = timestamp * 1000L
        } else {
            calendar.timeInMillis = timestamp
        }

//        val format = SimpleDateFormat("yyyyMMdd")
//        if(timeZone != null) {
//            dateFormat.timeZone = timeZone
//        }
//        val date = DateFormat.format("yyyyMMdd",calendar).toString()
        val date = dateFormat.format(calendar.time).toString()

        return date
    }

    fun convertTimestampToTime(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        if (timestamp.toString().length < 11) {
            calendar.timeInMillis = timestamp * 1000L
        } else {
            calendar.timeInMillis = timestamp
        }

//        val format = SimpleDateFormat("HHmmss")
//        if(timeZone != null) {
//            timeFormat.timeZone = timeZone
//        }

        val date = timeFormat.format(calendar.time).toString()
//        val date = DateFormat.format("HHmmss",calendar).toString()
        return date
    }

    fun resizeChart(chartLayout: View, width: Int, height: Int) {
        COMUtil.chartWidth = width
        COMUtil.chartHeight = height

        chartView?.resizeChart(chartLayout)
    }

    //2024.07.18 by SJW - KfitChartViewFragment와 구조 통일 >>
    fun setMainframe() {
        if (chartView != null) {
            COMUtil._mainFrame = chartView
            COMUtil._neoChart = chartView!!.mainBase.baseP._chart
        }
    }
    //2024.07.18 by SJW - KfitChartViewFragment와 구조 통일 <<

    fun getMainFrame(): MainFrame? {
        return COMUtil._mainFrame
    }

    fun getCurrentChartFileName(): String {
        return m_sLoadFileName
    }

    fun matchingColorSet() {
        CHART_COLORS[0] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_red500_base))
        CHART_COLORS[1] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_blue600_base))
        CHART_COLORS[2] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey500))
        // 2023.07.19 by SJW - 보조지표 색상 변경 요청 >>
        CHART_COLORS[4] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.orange))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage1)) // 보조지표1
        CHART_COLORS[5] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.periwinkle))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage2)) // 보조지표2
        CHART_COLORS[6] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.sky_blue))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage3)) // 보조지표3
        CHART_COLORS[7] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.grey700))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage4)) // 보조지표4
        CHART_COLORS[8] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.grey400))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage5)) // 보조지표5
        CHART_COLORS[9] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.orange))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage6)) // 보조지표6
        CHART_COLORS[10] =
//            COMUtil.convertColorToRGBArray(context.getColor(R.color.blue900))
            COMUtil.convertColorToRGBArray(context.getColor(R.color.movingaverage7)) // 보조지표7
        // 2023.07.19 by SJW - 보조지표 색상 변경 요청 <<
        CHART_COLORS[11] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_light_purple))
        CHART_COLORS[12] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_sky_blue))
        CHART_COLORS[13] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_purple))
        CHART_COLORS[14] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_green))
        CHART_COLORS[15] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_periwinkle))
        CHART_COLORS[16] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_yellow700))
        CHART_COLORS[17] =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_orange))

//        CoSys.GREY0_WHITE = COMUtil.convertColorToRGBArray(context.getColor(R.color.grey0))
        CoSys.GREY0_WHITE =
            COMUtil.convertColorToRGBArray(ContextCompat.getColor(context, R.color.kfits_grey0))
        CoSys.vertLineColor =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey100))
        CoSys.vertLineColor_xscale =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey300))
        CoSys.rectLineColor =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey500))
        CoSys.dotLineColor =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey200))
        CoSys.scrollLineColor =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey200))
        CoSys.CHART_BACK_MAINCOLOR = context.getColor(R.color.kfits_grey0)
        CoSys.VIEWPANEL_TEXT_COLOR = context.getColor(R.color.kfits_grey990)
        CoSys.GREY600 =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey600))
        CoSys.GREY700 =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey700))
        CoSys.GREY990 =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey990))
        CoSys.indicatorBaseLineColor =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey200)) // 보조지표 기준선 색상
        CoSys.crossline_col =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey700)) // 시세 인포뷰 십자선 색상
        CoSys.crossline_text_col =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_grey0)) // 시세 인포뷰 십자선 색상
        CoSys.UP_LINE_COLORS =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_red500_base))
        CoSys.DOWN_LINE_COLORS =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_blue600_base))

        CoSys.hint =
            COMUtil.convertColorToRGBArray(context.getColor(R.color.kfits_hint))
    }
}
