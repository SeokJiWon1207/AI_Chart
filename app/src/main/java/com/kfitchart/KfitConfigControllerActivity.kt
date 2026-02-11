package com.kfitchart

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakaopay.feature.stock.common.presentation.R
import com.kfitchart.entity.KfitIndexType
import com.kfitchart.entity.KfitStockPresentEntity
import com.kfitchart.entity.indexType
import com.kfitchart.entity.isDomestic
import com.kfitchart.entity.isIndexChart
import drfn.chart.comp.DRAlertDialog
import drfn.chart.util.COMUtil
import java.util.Timer
import java.util.TimerTask
import java.util.Vector
import kotlin.concurrent.schedule

/**
 * @author hanjun.Kim
 */
// val clickedList: (ArrayList<String>) -> Unit
var stateval: Boolean = false
var toastchk: Boolean = false
private var mToastToShow: Toast? = null
private var timer: TimerTask? = null

class KfitConfigControllerActivity : KfitBaseActivity() {
    companion object {

        const val TAG = "ChartSettingBottomDialogFragment"

        private var kfitDRChartView: KfitDRChartView? = null
        private var KfitDRChartData: KfitStockPresentEntity? = null

        // 2024.05.10 by PETER - Activity(가로모드)에서 설정이 제거되어 Fragment(차트탭)에서만 접근 가능 >>
        fun intent(
            context: Context,
            kfitChartView: KfitDRChartView,
            kfitChartData: KfitStockPresentEntity,
        ): Intent {
            return Intent(context, KfitConfigControllerActivity::class.java).apply {
                kfitDRChartView = kfitChartView
                KfitDRChartData = kfitChartData
            }
        }
        // 2024.05.10 by PETER - Activity(가로모드)에서 설정이 제거되어 Fragment(차트탭)에서만 접근 가능 <<
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kfit_fragment_chartsettingbottomsheet)
        initView()
    }

    // 2023.11.27 by hhk - 화면 갱신시 viewpager 상태저장 막음 >>
    // 단순히 나갔다 들어왔을때 복원 안되는 문제가 있어 보류
//    override fun onSaveInstanceState(outState: Bundle) {
//
//        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
//        viewPager.adapter = null
//
//        super.onSaveInstanceState(outState)
//    }
    // 2023.11.27 by hhk - 화면 갱신시 viewpager 상태저장 막음 <<

    override fun onPause() {
        super.onPause()

        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        COMUtil.closeToastDialog()
//        mtsTradingChartFragment = null
    }

    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
    var tablayout: TabLayout? = null
    var gViewPager: ViewPager2? = null

    var mCurrentFragmentPosition: Int = 0
    var mCurrentSelectedScreen: Int = 0
    var mNextSelectedScreen: Int = 0
    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<

    var strConfigKey: String = ""

    private fun initView() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabs = findViewById<TabLayout>(R.id.tabs)

        // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
        tablayout = tabs
        gViewPager = viewPager
        // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<

        // 2021.10.06 by JHY - Scale 확대 축소 애니메이션 >>
        val reduce = AnimationUtils.loadAnimation(this, R.anim.anim_reduce)
        val enlarge = AnimationUtils.loadAnimation(this, R.anim.anim_enlarge)
        // 2021.10.06 by JHY - Scale 확대 축소 애니메이션 <<

        val viewTrading = findViewById<LinearLayout>(R.id.view_Trading)

        val pagerAdapter = KfitPagerFragmentStateAdapter(this)
        pagerAdapter.addFragment(KfitSub1ConfigController())
        pagerAdapter.addFragment(KfitSub2ConfigController())
        pagerAdapter.addFragment(KfitSub3ConfigController())
        viewPager.adapter = pagerAdapter
        //        BottomSheetUtils.setupViewPager(viewPager)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                // onPageScrolled 이후 실행돼 setUserInputEnabled(false) 초기화  -JHY
                gViewPager?.setUserInputEnabled(true)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                @Px positionOffsetPixels: Int,
            ) {
                // 2021.10.15 by JHY - 독립차트 선택 swipe disable >>
                val isGoingToRightPage = position === mCurrentFragmentPosition
                gViewPager?.setUserInputEnabled(true)
                if (isGoingToRightPage && position == 1 && !stateval) {
                    // user is going to the right page
                    gViewPager?.setUserInputEnabled(false)
                    // 이부분에서 보조지표 탭으로 살짝 밀려나는 버그 fix
                    viewPager.setCurrentItem(viewPager.currentItem, false)
                    // 2021.10.19 by JHY -토스트 바로뜨는거 방지 >>
//                    if (toastchk){
//                        showToast()
//                    }
//                    toastchk =true
                    // 2021.10.19 by JHY -토스트 바로뜨는거 방지 <<
                } else {
                    // user is going to the left page
                    // 2021.10.19 by JHY -토스트 바로뜨는거 방지 >>
//                    toastchk=false
                    // 2021.10.19 by JHY -토스트 바로뜨는거 방지 <<
                    gViewPager?.setUserInputEnabled(true)
                }
                // 2021.10.15 by JHY - 독립차트 선택 swipe disable <<

                if (isStandGraph() && position == 2) {
                    viewPager.setCurrentItem(viewPager.currentItem - 1, false)

                    return
                }

//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                mCurrentFragmentPosition = position
                mCurrentSelectedScreen = position
                mNextSelectedScreen = position

                if (isStandGraph() && position == 2) {
                    return
                }

//                super.onPageSelected(position)

//                viewPager.isUserInputEnabled = !(isStandGraph() && position == 2)

                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                    }

                    position <= 0 -> { // [-1,0]
                        // Use the default slide transition when moving to the left page
                    }

                    position <= 1 -> { // (0,1]
                        // Fade the page out.
                    }

                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                    }
                }
            }
        })
        viewPager.children.find { it is RecyclerView }?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.isNestedScrollingEnabled = false
            }
        }

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            pagerAdapter.fragments.get(position).getTitle()?.let { tab.text = getString(it) }
        }.attach()
        // 2021.10.29 by JHY - 독립차트 이후 보조지표 탭 터치이벤트 >>
        val tabStripp = tablayout?.getChildAt(0)

        if (tabStripp is ViewGroup) {
            val childCount = tabStripp.childCount
            for (i in 0 until childCount) {
                val reduce1 = AnimationUtils.loadAnimation(this, R.anim.anim_reduce)
                val enlarge1 = AnimationUtils.loadAnimation(this, R.anim.anim_enlarge)
                val tabView = tabStripp.getChildAt(i)

                tabView.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(Color.argb(10, 0, 0, 0))
                        v.startAnimation(reduce1)
                        v.invalidate()
                        // 2021.11.17 by JHY - 7 버전 예외처리 추가 >>
                        if (!stateval && !toastchk && i == 2) {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                                v.isEnabled = false
                                v.startAnimation(enlarge1)
                                v.setBackgroundColor(Color.TRANSPARENT)
                            }
                            disableChartDialog()
                        }
                        // version check
                        else if (!stateval && toastchk && i == 2) {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                                v.isEnabled = false
                                v.startAnimation(enlarge1)
                                v.setBackgroundColor(Color.TRANSPARENT)
                            }
                        }
                        // 2021.11.17 by JHY - 7 버전 예외처리 추가 <<
                    } else if (event.action == MotionEvent.ACTION_MOVE) {
                    } else {
                        v.startAnimation(enlarge1)
                        v.setBackgroundColor(Color.TRANSPARENT)
                    }
//                    return@setOnTouchListener true
                    false
                }
            }
        }
        // 2021.10.29 by JHY - 독립차트 이후 보조지표 탭 터치이벤트 <<

//        val tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//        pagerAdapter.fragments.get(0).getTitle()?.let { tabOne.setText(it) }
//        tabs.getTabAt(0)?.setCustomView(tabOne)

//        val tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//        tabs.getTabAt(1)?.setCustomView(tabTwo)
//
//        val tabThree = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//        tabs.getTabAt(2)?.setCustomView(tabThree)

        //        // 상단 초기화 버튼 이벤트 클릭
        //        btn_reset.setOnClickListener {
        //            clickInitChart()
        //        }

        // 차트설정 X버튼
        findViewById<View>(R.id.ll_bottom_title).setOnClickListener {
            // 2024.01.10 by CYJ - 차트 설정 변경사항 여부 확인하여 저장하지않고 나간다는 안내창 >>
            val viewPager = findViewById<ViewPager2>(R.id.viewPager)
            var sub1ConfigController =
                (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(0) as KfitSub1ConfigController
            var sub2ConfigController =
                (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(1) as KfitSub2ConfigController
            var sub3ConfigController =
                (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(2) as KfitSub3ConfigController
            if (sub1ConfigController.initButton != sub1ConfigController.selectedButton || sub2ConfigController.checkChangeList.size > 0 || sub3ConfigController.checkChangeList.size > 0) {
                val saveAlertDialog = DRAlertDialog(it.context)
                saveAlertDialog.setTitle("변경사항을 저장할까요?")
                saveAlertDialog.setMessage("나가기를 누르면\n 변경사항이 저장되지 않아요.")
                saveAlertDialog.setNoButton("나가기")
                saveAlertDialog.setYesButton("저장")
                saveAlertDialog.setCanceledOnTouchOutside(false)
                saveAlertDialog.alert_btn_no.setOnClickListener {
                    saveAlertDialog.dismiss()
                    onBackPressed()
                }
                saveAlertDialog.alert_btn_yes.setOnClickListener {
                    saveAlertDialog.dismiss()
                    launchChartSettingSelect()
                    finish()
                }
                saveAlertDialog.btn_cancle.visibility = View.VISIBLE
                saveAlertDialog.show()
            }
            // 2024.01.10 by CYJ - 차트 설정 변경사항 여부 확인하여 저장하지않고 나간다는 안내창 <<
            else {
                // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix >>
                toastchk = false
                timer?.cancel()
                // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix <<

                // 설정 튤팁 가이드 처리 >>
                val pref = getSharedPreferences(strConfigKey, Activity.MODE_PRIVATE)
                pref.edit().putBoolean(strConfigKey, true).apply()
                initTooltip("", visible = View.GONE)
                // 설정 튤팁 가이드 처리 <<

                onBackPressed()
            }
        }

        // 하단뷰 테스트 입니다(확인버튼)
        viewTrading.findViewById<Button>(R.id.btnTradingInitChart).setText("초기화")
        viewTrading.findViewById<Button>(R.id.btnTradingConfirm).setText("확인")
        // 최초 사용자에게 도움말 표시 >>
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        val strFileName = (mainActivity as KfitChartViewActivity).chartFileName
        val strFileName = kfitDRChartView?.getCurrentChartFileName()
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
        strConfigKey = "kfit_isFirst_" + strFileName
//            const val CHART_FILE_NAME = "stock"
//            const val CHART_FILE_NAME_OVERSEAS = "overseas"
//            const val CHART_FILE_NAME_INDEX = "indexchart"
//            const val CHART_FILE_NAME_OVERSEASINDEX = "overseasindex"
//            const val CHART_FILE_NAME_OVERSEASFUTURE = "overseasfuture"
        val pref = getSharedPreferences(strConfigKey, Activity.MODE_PRIVATE)
        if (pref.getBoolean(strConfigKey, false) == false) {
            // 최초실행
            var strTitle: String = ""
            if (strFileName == CHART_FILE_NAME_STOCK_DOMESTIC) {
                strTitle = "한국주식 캔들차트에만 적용돼요"
            } else if (strFileName == CHART_FILE_NAME_STOCK_FOREIGN) {
                strTitle = "미국주식 캔들차트에만 적용돼요"
            } else if (strFileName == CHART_FILE_NAME_INDEX_DOMESTIC) {
                strTitle = "국내지수 캔들차트에만 적용돼요"
            } else if (strFileName == CHART_FILE_NAME_INDEX_FOREIGN) {
                strTitle = "해외지수 캔들차트에만 적용돼요"
            } else if (strFileName == CHART_FILE_NAME_INDEX_FOREIGN_FUTURE) {
                strTitle = "해외선물 캔들차트에만 적용돼요"
            }
            initTooltip(strTitle, visible = View.VISIBLE)

//            //설정창 "확인" 및 종료 시점으로 이동
//            pref.edit().putBoolean(strConfigKey, true).apply()
        }
        // 최초 사용자에게 도움말 표시 <<

        // 2021.10.05 by.JHY - 초기화 버튼 이벤트 >>
        viewTrading.findViewById<Button>(R.id.btnTradingInitChart).setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(reduce)
//                    v.background.setAlpha(150)
                    v.background.setColorFilter(
                        Color.parseColor("#0A000000"),
                        PorterDuff.Mode.SRC_IN,
                    )
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    v.startAnimation(enlarge)
//                    v.background.setAlpha(255)
                    v.background.clearColorFilter()
                    v.invalidate()
                    COMUtil.closeToastDialog() // 2023.11.23 by lyk - 지표설정 토스트 다이얼로그와 충돌 오류 수정
                }
            }
            false
        }
        // 2021.10.05 by.JHY - 초기화 버튼 이벤트 <<

        viewTrading.findViewById<Button>(R.id.btnTradingInitChart).setOnClickListener {
            clickClearChart()
        }

        // 2021.10.05 by.JHY - 확인 버튼 이벤트 >>
        viewTrading.findViewById<Button>(R.id.btnTradingConfirm).setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(reduce)
//                    v.background.setColorFilter(Color.parseColor("#0A000000"), PorterDuff.Mode.SRC_IN)
                    v.background.setAlpha(150)
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    v.startAnimation(enlarge)
//                    v.background.clearColorFilter()
                    v.background.setAlpha(255)
                    v.invalidate()
                }
            }
            false
        }
        // 2021.10.05 by.JHY - 확인 버튼 이벤트 <<

        viewTrading.findViewById<Button>(R.id.btnTradingConfirm).setOnClickListener {
            // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix >>
            toastchk = false
            timer?.cancel()
            // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix <<
            // 각각의 자식 fragment에서 선택된 값들에 대한 차트 이벤트 실행
            launchChartSettingSelect()

            // 설정 튤팁 가이드 처리 >>
            val pref = getSharedPreferences(strConfigKey, Activity.MODE_PRIVATE)
            pref.edit().putBoolean(strConfigKey, true).apply()
            initTooltip("", visible = View.GONE)
            // 설정 튤팁 가이드 처리 <<

            finish()
        }
    }
    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
    // 2021.10.19 by JHY - 독립차트 선택 시 보조지표 탭 Toast 처리 >>
//    fun showToast() {
// //        val toastDurationInMilliSeconds = 5000
// //        mToastToShow = Toast.makeText(this.context, "선택하신 주 차트는 보조 지표 사용이 불가합니다.", Toast.LENGTH_LONG)
// //        val toastCountDown: CountDownTimer
// //        toastCountDown = object : CountDownTimer(toastDurationInMilliSeconds.toLong(), 5000 /*Tick
// //      duration*/) {
// //            override fun onTick(millisUntilFinished: Long) {
// //                mToastToShow.show()
// //            }
// //            override fun onFinish() {
// //                mToastToShow.cancel()
// //            }
// //        }
// //        mToastToShow.show()
// //        toastCountDown.start()
//        if(mToastToShow != null) {
//            mToastToShow?.cancel()
//            mToastToShow = null
//            timer?.cancel()
//        }
//        if(mToastToShow == null) {
//            mToastToShow =
//                Toast.makeText(this, "선택하신 주 차트는 보조 지표 사용이 불가합니다.", Toast.LENGTH_SHORT)
//            mToastToShow?.setGravity(Gravity.BOTTOM,0, COMUtil.getPixel(92).toInt())
//            mToastToShow?.show()
//
//            timer = Timer("SettingUp", false).schedule(5000) {
//                mToastToShow?.cancel()
//                mToastToShow = null
//            }
//        }
//    }
    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<
    // 2021.10.19 by JHY - 독립차트 선택 시 보조지표 탭 Toast 처리 <<

    private fun initTooltip(title: String, visible: Int) {
        val viewToolTip = findViewById<LinearLayout>(R.id.config_tooltip_body)
        viewToolTip.visibility = visible
        val tooltipTextview = findViewById<TextView>(R.id.tooltip_textview)
        tooltipTextview.text = title
    }

    // 2021.10.20 by JHY - color fadein anim <<
    /**
     * v-> View
     * dr -> duration (주기)
     * sc -> StartColor
     * ec -> EndColor
     * 지금은 fade-in 처리를 위해 두가지 컬러만 지정했지만
     * 여러개 추가하여 pulse 처럼 작동하게 할 수 있다.
     */
    fun animateColor(v: View?, dr: Long, sc: Int, ec: Int) {
        val anim = ValueAnimator.ofArgb(sc, ec).apply {
            duration = dr
            // 반복유무
            // repeatCount = ValueAnimator.INFINITE
            // repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                v?.setBackgroundColor(it.animatedValue as Int)
            }
        }
        anim.start()
    }
    // 2021.10.20 by JHY - color fadein anim <<

    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 >>
    fun setEnableTab(index: Int, state: Boolean) {
        val tabStrip = tablayout?.getChildAt(0) as LinearLayout
        // 2021.10.29 by JHY - touch evenvt 위해 비활성 >>
//        tabStrip.getChildAt(index).isEnabled = state
        tabStrip.getChildAt(index).isClickable = state
        tabStrip.getChildAt(index).isEnabled = true
        // 2021.10.29 by JHY - touch evenvt 위해 비활성 <<
        stateval = state
        if (state) {
            tabStrip.getChildAt(index).alpha = 1.0f
//            val cusTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//            cusTab.setText(R.string.tab_title_indicator)
//            cusTab.setTextColor(getColor(R.color.grey990))
//            tablayout?.getTabAt(index)?.setCustomView(cusTab)
        } else {
            tabStrip.getChildAt(index).alpha = 0.3f
//            val cusTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
//            cusTab.setText(R.string.tab_title_indicator)
//            cusTab.setTextColor(getColor(R.color.grey300))
//            tablayout?.getTabAt(index)?.setCustomView(cusTab)
        }

//        gViewPager?.setUserInputEnabled(state) //탭뷰 스와이프 기능 설정 유무
    }
    // 2021.09.29 by lyk - kakaopay - 독립차트 선택 시 보조지표 탭 disable 처리 <<
    /**
     * 독립차트 선택 후 보조지표 클릭 시 dialog
     */
    // 2021.10.20 by JHY - 독립차트 선택 후 보조지표 탭 클릭 dialog >>
    private fun disableChartDialog() {
        toastchk = true
        val dialog = Dialog(this)
        val tabStrip = tablayout?.getChildAt(0) as LinearLayout

        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (COMUtil.getSkinType() != COMUtil.SKIN_BLACK) {
            dialog.setContentView(R.layout.kfit_toast_dialog)
        } else {
            dialog.setContentView(R.layout.kfit_toast_dialog_dark)
        }
        // 배경 검정색아니도록
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // dialog 밖에 터치 가능
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        )

        // 2021.10.20 by JHY - Dialog animation >>
        dialog.window?.attributes?.windowAnimations = drfn.chart_src.R.style.ToastDialogAnimation
        // 2021.10.20 by JHY - Dialog animation <<
        // 2024.01.10 by CYJ - 전체 설정 초기화 플로우 수정 >> 토스트메세지 위치 수정
        val viewTrading = findViewById<LinearLayout>(R.id.view_Trading)
        dialog.window?.attributes?.y = viewTrading.height
        // 2024.01.10 by CYJ - 전체 설정 초기화 플로우 수정 << 토스트메세지 위치 수정
        // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix >>
        dialog.show()
        // 2021.11.09 by JHY - toast delay 5000 -> 3000
        timer = Timer("SettingUp", false).schedule(3000) {
            dialog.dismiss()
            toastchk = false
            // 2021.11.17 by JHY - 7 버전 예외처리 추가 >>
            runOnUiThread {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                    tabStrip.getChildAt(2).isEnabled = true
                }
            }
            // 2021.11.17 by JHY - 7 버전 예외처리 추가 <<
        }
        // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix <<

        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
        )
    }
    // 2021.10.20 by JHY - 독립차트 선택 후 보조지표 탭 클릭 dialog <<

    private fun clickClearChart() {
        //2024.08.08 by SJW - crash 로그 수정 >>
        if (COMUtil._chartMain == null) {
            COMUtil.setChartMain(kfitDRChartView?.context as AppCompatActivity)
        }
        //2024.08.08 by SJW - crash 로그 수정 <<
        val dialog = Dialog(this)

        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 2021.11.24 by JHY - 초기화 z-fold 예외처리 >>
        if (resources.displayMetrics.widthPixels < 905) {
            dialog.setContentView(R.layout.kfit_alert_dialog2)
            dialog.findViewById<TextView>(R.id.kfit_alert_content)
                .setText(getString(R.string.dialog_clear_chart_message2))
        } else {
            dialog.setContentView(R.layout.kfit_alert_dialog)
            dialog.findViewById<TextView>(R.id.kfit_alert_content)
                .setText(getString(R.string.dialog_clear_chart_message))
        }
        // 2021.11.24 by JHY - 초기화 z-fold 예외처리 <<

//        dialog.setContentView(R.layout.kfit_alert_dialog)
//        dialog.findViewById<TextView>(R.id.kfit_alert_title).setText(getString(R.string.dialog_clear_chart_title))
        dialog.findViewById<TextView>(R.id.kfit_alert_content)
            .setText(getString(R.string.dialog_clear_chart_message))

        // 2021.10.06 by.JHY - 확인 버튼 이벤트 >>>
        dialog.findViewById<Button>(R.id.kfit_ok_btn).setOnTouchListener { v, event ->
            val reduce = AnimationUtils.loadAnimation(this, R.anim.anim_reduce)
            val enlarge = AnimationUtils.loadAnimation(this, R.anim.anim_enlarge)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(reduce)
                    v.background.setColorFilter(
                        Color.parseColor("#0A000000"),
                        PorterDuff.Mode.SRC_IN,
                    )
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    v.startAnimation(enlarge)
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
        // 2021.10.06 by.JHY - 확인 버튼 이벤트 <<

        dialog.findViewById<Button>(R.id.kfit_ok_btn).setOnClickListener {
            // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix >>
            toastchk = false
            timer?.cancel()
            // 2021.10.29 by JHY - Dissmiss 관련 프로세스 fix <<
            // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<

            kfitDRChartView?.setInitChart()
            kfitDRChartView?.setSaveChart()

            // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
            // 2021.07.13 by hanjun.Kim - kakaopay - 초기화 버튼 로직 변경 >>
            refresh() // finish 처리 하기 때문에 UI 갱신 필요가 없으므로 주석처리 //2024.01.03 by CYJ - 차트 설정 초기화 시 종료되지 않도로 수정

            // 설정 튤팁 가이드 처리 >>
            val pref = getSharedPreferences(strConfigKey, Activity.MODE_PRIVATE)
            pref.edit().putBoolean(strConfigKey, true).apply()
            initTooltip("", visible = View.GONE)
            // 설정 튤팁 가이드 처리 <<

            dialog.dismiss()
            // finish() //2024.01.03 by CYJ - 차트 설정 초기화 시 종료되지 않도로 수정
            COMUtil.showToast(
                "차트 설정이 초기화 되었어요.",
                1,
                it.context,
            ) // 2024.01.10 by CYJ - 전체 설정 초기화 플로우 수정 - 차트 설정이 초기화 됐다는 토스트 메세지 추가
        }

        // 2021.10.07 by JHY - Dialog animation >>
        dialog.window?.attributes?.windowAnimations = drfn.chart_src.R.style.CustomDialogAnimation
        // 2021.10.07 by JHY - Dialog animation <<

        // 2021.10.06 by.JHY - 취소 버튼 이벤트 >>
        dialog.findViewById<Button>(R.id.kfit_cancel_btn).setOnTouchListener { v, event ->
            val reduce = AnimationUtils.loadAnimation(this, R.anim.anim_reduce)
            val enlarge = AnimationUtils.loadAnimation(this, R.anim.anim_enlarge)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(reduce)
                    v.background.setColorFilter(
                        Color.parseColor("#0A000000"),
                        PorterDuff.Mode.SRC_IN,
                    )
                    v.invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    v.startAnimation(enlarge)
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
        // 2021.10.06 by.JHY - 취소 버튼 이벤트 <<

        dialog.findViewById<Button>(R.id.kfit_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

        dialog.window?.setGravity(Gravity.CENTER)
        // 2023.02.06 by SJW - Fold3 설정 초기화 다이럴로그 사이즈 수정 >>
        val windowRect = COMUtil.getWindowRect()
        val mDisWidth = windowRect.width()
        if (mDisWidth >= 1768) {
            val params = dialog.window?.attributes
            params?.width = COMUtil.getPixel_W(350f).toInt()
            dialog.window?.attributes = params
        } else {
            dialog.window?.setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
            )
        }
        // 2023.02.06 by SJW - Fold3 설정 초기화 다이럴로그 사이즈 수정 <<
    }

    /**
     * 초기화 버튼 클릭시
     */
    fun refresh() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter?.let {
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(0).refresh()
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(1).refresh()
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(2).refresh()
        }
    }

    /**
     * 확인 버튼 클릭시
     */
    fun launchChartSettingSelect() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter?.let {
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(0).launchSelect()
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(1).launchSelect()
            (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(2).launchSelect()
        }

        COMUtil.closeToastDialog() // 2023.11.20 by CYJ - kakaopay 토스트 다이어로그 종료되도록 수정
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
        kfitDRChartView?.setSaveChart()
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    fun isStandGraph(): Boolean {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter?.let {
            var sub1ConfigController =
                (viewPager.adapter as KfitPagerFragmentStateAdapter).getItem(0) as KfitSub1ConfigController
            sub1ConfigController?.let {
                return sub1ConfigController.isStandGraph()
            }
        }

        return false
    }

    fun getMethod(sValue: String, sParam: String): Any? {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        return mainActivity?.getMethod(sValue, sParam)
        return kfitDRChartView?.getMethod(sValue, sParam)
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    fun setSelectChart(tag: Int) {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        mainActivity?.setSelectChart(tag)
        kfitDRChartView?.setSelectChart(tag)
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    fun setChartFunc(sValue: String) {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        mainActivity?.setChartFunc(sValue)
        kfitDRChartView?.setChartFunc(sValue)
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    fun getGraphTagList(): Vector<String>? {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        return mainActivity?.getGraphTagList()
        return kfitDRChartView?.getGraphTagList()
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 >>
    fun isIndexChart(): Boolean {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        return mainActivity?.isIndexChart()
        // 2023.10.31 by SJw - crashlytics 로그 오류 수정 >>
//        return mainActivity?.isIndexChart() ?: mtsTradingChartFragment!!.isIndexChart()
        return KfitDRChartData?.isIndexChart() ?: return false
        // 2023.10.31 by SJw - crashlytics 로그 오류 수정 <<
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }

    fun isIndexType(): KfitIndexType? {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        return mainActivity?.isIndexType()
        return KfitDRChartData?.indexType()
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }
    // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 <<

    // 2022.04.11 by lyk - kakaopay - 지수상세 국내해외 구분 >>
    fun isDomestic(): Boolean? {
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 >>
//        return mainActivity?.isDomestic()
//        return mainActivity?.isDomestic() ?: mtsTradingChartFragment!!.isDomestic()
        return KfitDRChartData?.isDomestic()
        // 2023.03.07 by SJW - 차트 Fragment 변환 작업 <<
    }
    // 2022.04.11 by lyk - kakaopay - 지수상세 국내해외 구분 <<

    // 2023.11.27 by hhk - uiMode 변경시 설정창 종료 >>
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // 글로벌하게 keyboard 강제로 내려줌
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        finish()
    }
    // 2023.11.27 by hhk - uiMode 변경시 설정창 종료 <<
}

class KfitPagerFragmentStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    var fragments: MutableList<KfitBaseFragment> = ArrayList()
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: KfitBaseFragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment() {
        fragments.remove(fragments.last())
        notifyItemRemoved(fragments.size)
    }

    fun getItem(position: Int): KfitBaseFragment {
        return fragments[position]
    }
}

class OntabClickListener(
    private val clickListener: View.OnClickListener,
) :
    View.OnClickListener {

    private var clickable = true
    // clickable 플래그를 이 클래스가 아니라 더 상위 클래스에 두면
    // 여러 뷰에 대한 중복 클릭 방지할 수 있다.

    override fun onClick(v: View?) {
        if (clickable) {
            clickable = false
        } else {
        }
    }
}
