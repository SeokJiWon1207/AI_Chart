package com.kfitchart

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.kakaopay.feature.stock.common.presentation.R
import drfn.chart.util.COMUtil
import java.util.Timer
import java.util.TimerTask
import java.util.logging.Logger
import kotlin.concurrent.schedule

/**
 * @author hanjun.Kim
 */

class KfitSub1ConfigController() : KfitBaseFragment() {
    lateinit var fragmentView: View
    private var mToastToShow: Toast? = null
    private var timer: TimerTask? = null

    companion object {
        val log = Logger.getLogger(KfitSub1ConfigController::class.java.name)

        private const val INDICATOR_PLINE = 10002
        private const val INDICATOR_JBONG = 10000
        private const val INDICATOR_JBONG_TRANSPARENCY = 19999
        private const val INDICATOR_PNF = 20001
        private const val INDICATOR_SWING = 20003
        private const val INDICATOR_RENKO = 20004
        private const val INDICATOR_KAGI = 20005
    }

    var selectedButton: ImageButton? = null
    var initButton: ImageButton? =
        null // 2024.01.10 by CYJ - 차트 설정 변경사항 여부 확인하여 저장하지않고 나간다는 안내창 - 초기 상태와 현재 상태 비교를 위한 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentView = inflater.inflate(R.layout.kfit_fragment_setting_charttype, container, false)
        return fragmentView
    }

    override fun getTitleRes(): Int? {
        return R.string.tab_title_charttype
    }

    override fun refresh() {
        initView()
    }

    override fun launchSelect() {
        if (activity != null) {
            selectedButton?.let {
                selectItemEvent(it.id)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (!::fragmentView.isInitialized) return; // 2024.02.29 by SJW - fragmentView 미 초기화 시 crash 수정
        // 2021.04.21 by lyk - kakaopay - 차트 타입 가져오기
        this.view?.setTag("1001")
        toggleButton(null) // 이전 선택값이 있으면 초기화

        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 >>
//        val selectChartTag : String =
//            (activity as KfitConfigControllerActivity).getMethod("getSelectedChartType", "") as String
        val selectChartTag: String = (activity as? KfitConfigControllerActivity)?.getMethod(
            "getSelectedChartType",
            "",
        ) as? String ?: "10000" // null 들어올 시 차트유형 -> 캔들차트 - "10000"으로 초기화
        // 2023.06.30 by SJW - 차트 탭 관련 크래시 발생 로그 수정 <<

        initCheck(selectChartTag)
        initSelectEvent()

        // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
        setEnableTab(selectChartTag.toInt())
        // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<
    }

    private fun initCheck(checkTag: String) {
        val charttypeRadioBtn1 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_1)
        val charttypeRadioBtn2 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_2)
        val charttypeRadioBtn3 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_3)
        val charttypeRadioBtn4 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_4)
        val charttypeRadioBtn5 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_5)
        val charttypeRadioBtn6 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_6)
        val charttypeRadioBtn7 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_7)

        when (checkTag) {
            INDICATOR_PLINE.toString() -> {
                charttypeRadioBtn1.isSelected = true
                selectedButton = charttypeRadioBtn1
            }

            INDICATOR_JBONG.toString() -> {
                charttypeRadioBtn2.isSelected = true
                selectedButton = charttypeRadioBtn2
            }

            INDICATOR_JBONG_TRANSPARENCY.toString() -> {
                charttypeRadioBtn3.isSelected = true
                selectedButton = charttypeRadioBtn3
            }

            INDICATOR_PNF.toString() -> {
                charttypeRadioBtn4.isSelected = true
                selectedButton = charttypeRadioBtn4
            }

            INDICATOR_SWING.toString() -> {
                charttypeRadioBtn5.isSelected = true
                selectedButton = charttypeRadioBtn5
            }

            INDICATOR_RENKO.toString() -> {
                charttypeRadioBtn6.isSelected = true
                selectedButton = charttypeRadioBtn6
            }

            INDICATOR_KAGI.toString() -> {
                charttypeRadioBtn7.isSelected = true
                selectedButton = charttypeRadioBtn7
            }
        }
        initButton =
            selectedButton // 2024.01.10 by CYJ - 차트 설정 변경사항 여부 확인하여 저장하지않고 나간다는 안내창 - 초기 상태와 현재 상태 비교를 위한 변수 설정
    }

    private fun initSelectEvent() {
        val charttypeRadioLayout1 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_1)
        val charttypeRadioLayout2 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_2)
        val charttypeRadioLayout3 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_3)
        val charttypeRadioLayout4 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_4)
        val charttypeRadioLayout5 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_5)
        val charttypeRadioLayout6 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_6)
        val charttypeRadioLayout7 =
            fragmentView.findViewById<LinearLayout>(R.id.charttype_radio_layout_7)

        val RadioLayoutList = listOf(
            charttypeRadioLayout1,
            charttypeRadioLayout2,
            charttypeRadioLayout3,
            charttypeRadioLayout4,
            charttypeRadioLayout5,
            charttypeRadioLayout6,
            charttypeRadioLayout7,
        )
        // 2021.10.19 by JHY - 버튼 클릭시 배경색 >>
        TouchButton(charttypeRadioLayout1, RadioLayoutList)
        TouchButton(charttypeRadioLayout2, RadioLayoutList)
        TouchButton(charttypeRadioLayout3, RadioLayoutList)
        TouchButton(charttypeRadioLayout4, RadioLayoutList)
        TouchButton(charttypeRadioLayout5, RadioLayoutList)
        TouchButton(charttypeRadioLayout6, RadioLayoutList)
        TouchButton(charttypeRadioLayout7, RadioLayoutList)
        // 2021.10.19 by JHY - 버튼 클릭시 배경색 <<

        val charttypeRadioBtn1 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_1)
        val charttypeRadioBtn2 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_2)
        val charttypeRadioBtn3 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_3)
        val charttypeRadioBtn4 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_4)
        val charttypeRadioBtn5 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_5)
        val charttypeRadioBtn6 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_6)
        val charttypeRadioBtn7 = fragmentView.findViewById<ImageButton>(R.id.charttype_radio_btn_7)

        charttypeRadioLayout1.setOnClickListener {
            toggleButton(charttypeRadioBtn1)
        }
        charttypeRadioLayout2.setOnClickListener {
            toggleButton(charttypeRadioBtn2)
        }
        charttypeRadioLayout3.setOnClickListener {
            toggleButton(charttypeRadioBtn3)
        }
        charttypeRadioLayout4.setOnClickListener {
            toggleButton(charttypeRadioBtn4)
        }
        charttypeRadioLayout5.setOnClickListener {
            toggleButton(charttypeRadioBtn5)
        }
        charttypeRadioLayout6.setOnClickListener {
            toggleButton(charttypeRadioBtn6)
        }
        charttypeRadioLayout7.setOnClickListener {
            toggleButton(charttypeRadioBtn7)
        }
    }

    // 2021.10.19 by JHY - 버튼 클릭시 배경색 >>
    private fun TouchButton(LL: LinearLayout, RadioLayoutList: List<LinearLayout>) {
        LL.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 배경초기화
                    ClearLayout(RadioLayoutList)
                    v.setBackgroundColor(Color.argb(10, 0, 0, 0))
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
//                    v.background.setAlpha(0)
                    v.setBackgroundColor(Color.argb(0, 0, 0, 0))
                    v.invalidate()
                }

                MotionEvent.ACTION_MOVE -> {}
                else -> {
                    v.setBackgroundColor(Color.argb(0, 0, 0, 0))
                }
            }
            false
        }
    }
    // 2021.10.19 by JHY - 버튼 클릭시 배경색 <<

    // 2021.10.19 by JHY - 레이아웃 배경 초기화 >>
    private fun ClearLayout(RadioLayoutList: List<LinearLayout>) {
        for (i in RadioLayoutList.indices) {
            RadioLayoutList[i].setBackgroundColor(Color.argb(0, 0, 0, 0))
        }
    }
    // 2021.10.19 by JHY - 레이아웃 배경 초기화 <<

    private fun toggleButton(button: ImageButton?) {
        selectedButton?.let {
            it.isSelected = !it.isSelected
        }
        button?.let {
            // 2021.10.08 by JHY - 라디오 버튼 애니메이션 >>
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    fragmentView.context,
                    R.anim.anim_fadein_large,
                ),
            )
            // 2021.10.08 by JHY - 라디오 버튼 애니메이션 <<

            it.isSelected = true
            selectedButton = it

            // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
            setEnableTab(getSelectType(it.id))
            // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<
        }
    }

    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
    fun setEnableTab(selectChartTag: Int) {
        if (selectChartTag == INDICATOR_PNF || selectChartTag == INDICATOR_SWING || selectChartTag == INDICATOR_RENKO || selectChartTag == INDICATOR_KAGI) {
            (activity as KfitConfigControllerActivity).setEnableTab(2, false)
//            showToast()
        } else {
            (activity as KfitConfigControllerActivity).setEnableTab(2, true)
//            mToastToShow?.cancel()
//            mToastToShow = null
        }
    }
    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<

    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
    fun isStandGraph(): Boolean {
        selectedButton?.let {
            val selectChartTag = getSelectType(it.id)
            if (selectChartTag == INDICATOR_PNF || selectChartTag == INDICATOR_SWING || selectChartTag == INDICATOR_RENKO || selectChartTag == INDICATOR_KAGI) {
                return true
            }
        }

        return false
    }

    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<
    fun showToast() {
//        val toastDurationInMilliSeconds = 5000
//        mToastToShow = Toast.makeText(this.context, "선택하신 주 차트는 보조 지표 사용이 불가합니다.", Toast.LENGTH_LONG)
//        val toastCountDown: CountDownTimer
//        toastCountDown = object : CountDownTimer(toastDurationInMilliSeconds.toLong(), 5000 /*Tick
//      duration*/) {
//            override fun onTick(millisUntilFinished: Long) {
//                mToastToShow.show()
//            }
//            override fun onFinish() {
//                mToastToShow.cancel()
//            }
//        }
//        mToastToShow.show()
//        toastCountDown.start()
        if (mToastToShow != null) {
            mToastToShow?.cancel()
            mToastToShow = null

            timer?.cancel()
        }
        if (mToastToShow == null) {
            mToastToShow =
                Toast.makeText(this.context, "선택하신 주 차트는 보조 지표 사용이 불가합니다.", Toast.LENGTH_SHORT)
            mToastToShow?.setGravity(Gravity.BOTTOM, 0, COMUtil.getPixel(92).toInt())
            mToastToShow?.show()

            timer = Timer("SettingUp", false).schedule(5000) {
                mToastToShow?.cancel()
                mToastToShow = null
            }
        }
    }

    private fun getSelectType(checkedId: Int): Int {
        when (checkedId) {
            // 라인차트
            R.id.charttype_radio_btn_1 -> {
                return INDICATOR_PLINE
            }
            // 캔들 차트
            R.id.charttype_radio_btn_2 -> {
                return INDICATOR_JBONG
            }
            // 투명 캔들 차트
            R.id.charttype_radio_btn_3 -> {
                return INDICATOR_JBONG_TRANSPARENCY
            }
            // 포인트 앤 피겨 차트
            R.id.charttype_radio_btn_4 -> {
                return INDICATOR_PNF
            }
            // 스윙 차트
            R.id.charttype_radio_btn_5 -> {
                return INDICATOR_SWING
            }
            // 렌코 차트
            R.id.charttype_radio_btn_6 -> {
                return INDICATOR_RENKO
            }
            // 카기 차트
            R.id.charttype_radio_btn_7 -> {
                return INDICATOR_KAGI
            }
        }

        return -1
    }

    private fun selectItemEvent(checkedId: Int) {
        when (checkedId) {
            // 라인차트
            R.id.charttype_radio_btn_1 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_PLINE)
            }
            // 캔들 차트
            R.id.charttype_radio_btn_2 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_JBONG)
            }
            // 투명 캔들 차트
            R.id.charttype_radio_btn_3 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(
                    INDICATOR_JBONG_TRANSPARENCY,
                )
            }
            // 포인트 앤 피겨 차트
            R.id.charttype_radio_btn_4 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_PNF)
            }
            // 스윙 차트
            R.id.charttype_radio_btn_5 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_SWING)
            }
            // 렌코 차트
            R.id.charttype_radio_btn_6 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_RENKO)
            }
            // 카기 차트
            R.id.charttype_radio_btn_7 -> {
                (activity as KfitConfigControllerActivity).setSelectChart(INDICATOR_KAGI)
            }
        }
    }
}
