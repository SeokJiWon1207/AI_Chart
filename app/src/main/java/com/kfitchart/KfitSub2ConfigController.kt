package com.kfitchart

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakaopay.feature.stock.common.presentation.R
import java.util.Vector
import java.util.logging.Logger

/**
 * @author hanjun.Kim
 */

class KfitSub2ConfigController : KfitBaseFragment() {
    lateinit var fragmentView: View

    companion object {
        val log = Logger.getLogger(KfitSub2ConfigController::class.java.name)

        private const val INDICATOR_RAINBOW = 40004
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
        private const val INDICATOR_GLANCE_BALANCE = 40003
        private const val INDICATOR_BOLLINGER = 40001
        private const val INDICATOR_VAVERAGE = 40007
        private const val INDICATOR_STANDSCALE = 11000
        private const val AUTOTRENDWAVE_TYPE = 65001
        private const val SUPPORT_RESISTANCE_LINE = 65000
        private const val AVGBUY_PRICE = 65002
        private const val BUYSELL_PRICE = 65003
        private const val CORPORATE_CALENDAR = 65004
        private const val INDICATOR_ENVELOPE = 40002 // 2023.06.27 by SJW - 엔벨로프 지표 추가
    }

    lateinit var adapter: ChartSettingDisplayInfoAdapter
    var checkChangeList = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentView = inflater.inflate(R.layout.kfit_fragment_setting_indicator, container, false)

        // 2023.12.13 by lyk - 키패드 올리고 내릴때 이전 설정화면 잔상이 보이는 현상 (수정중) >>
//        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        // 2023.12.13 by lyk - 키패드 올리고 내릴때 이전 설정화면 잔상이 보이는 현상 (수정중) <<
        return fragmentView
    }

    override fun getTitleRes(): Int? {
        return R.string.tab_title_displayInfo
    }

    override fun refresh() {
        if (activity != null) {
            initAapterData()
            adapter.notifyDataSetChanged()
            checkChangeList.clear()
        }
    }

    override fun launchSelect() {
        if (checkChangeList.isNotEmpty()) {
            checkChangeList.forEach { (itemTag, isAdd) ->
                (activity as KfitConfigControllerActivity).setChartFunc(itemTag + ";" + isAdd)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @SuppressLint("ResourceAsColor")
    private fun initView() {
        this.view?.setTag("1002")
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_view)

        adapter = createAdapter()
        initAapterData()
        // 2021.10.19 by JHY - 체크박스 선택시 backgroundcolor >>
        recyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                // move 이벤트는 시도때도없이 발생하니 예외처리
                if (e.action == MotionEvent.ACTION_MOVE) {
                }
                // 터치했을경우
                else if (e.action == MotionEvent.ACTION_DOWN) {
                    var child = rv.findChildViewUnder(e.getX(), e.getY())
                    // 제대로된곳 터치했는지
                    if (child != null) {
                        // 몇번째 인지 체크
                        var position = rv.getChildAdapterPosition(child)
                        // 해당 레이아웃의 포지션을 특정
                        var view = rv.layoutManager?.findViewByPosition(position)
                        // 특정 레이아웃에서 ID값을 이용해 원하는 동작을 지정
                        Log.i("debug----->", "3->")
                        // 2023.11.16 by LYH - 지표상세설정 디자인 >>
//                        view?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
//                            ?.setBackgroundColor(Color.argb(10, 0, 0, 0))
                        var configView = view?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                        val x = configView?.x?.toFloat()
                        if ((configView != null && !configView.isVisible) || x == null || x > e.getX()) {
                            view?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
                                ?.setBackgroundColor(Color.argb(10, 0, 0, 0))
                            view?.findViewById<View>(R.id.indicatorNameLayout)
                                ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                            view?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                        } else {
                            view?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                ?.setBackgroundColor(Color.argb(10, 0, 0, 0))
                            // 2023.11.20 by CYJ - kakaopay 터치영역 디자인 >>
                            view?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                ?.setBackgroundColor(Color.argb(10, 0, 0, 0))
                            view?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                ?.setBackgroundColor(Color.argb(10, 0, 0, 0))
                            // 2023.11.20 by CYJ - kakaopay 터치영역 디자인 <<
                        }
                        // 2023.11.16 by LYH - 지표상세설정 디자인 <<
                    }
                }
                // 손가락 뗏을때
                else {
                    var child = rv.findChildViewUnder(e.getX(), e.getY())
                    if (child != null) {
                        var position = rv.getChildAdapterPosition(child)
                        var view = rv.layoutManager?.findViewByPosition(position)
                        Log.i("debug----->", "1->" + rv.adapter!!.itemCount)
                        // 터치 한 부분을 제외하고 for문 돌리면서 모든 position 을 비활성화(투명) 시킨다
                        for (i in 0..rv.adapter!!.itemCount) {
                            var otherView = rv.layoutManager?.findViewByPosition(i)
                            if (otherView != view) {
                                otherView?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
                                    ?.setBackgroundColor(
                                        Color.argb(0, 0, 0, 0),
                                    )
                                // 2023.11.16 by LYH - 지표상세설정 디자인 >>
                                otherView?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                    ?.setBackgroundColor(
                                        Color.argb(0, 0, 0, 0),
                                    )
                                // 2023.11.16 by LYH - 지표상세설정 디자인 <<
                            }
                            // 버그 예외처리 이유아직모름
                            else {
                                otherView?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
                                    ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                                // 2023.11.16 by LYH - 지표상세설정 디자인 >>
                                otherView?.findViewById<View>(R.id.indicatorConfigButtonLayout)
                                    ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                                // 2023.11.16 by LYH - 지표상세설정 디자인 <<
                            }
                        }
                        Log.i("debug----->", "2->" + rv.getChildAdapterPosition(child))
                    }
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
        // 2021.10.19 by JHY - 체크박스 선택시 backgroundcolor <<
        adapter.setItemClickListener(object : ChartSettingDisplayInfoAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int, item: CheckableDisplayInfo) {
                var isAdd = "0"
                if (item.isCheck) {
                    isAdd = "1"
                }
                // for direct launch
//                        (activity as MainActivity).setChartFunc(item.indicatorItem.tag+";"+isAdd)
                if (checkChangeList.contains(item.indicatorItem.tag)) {
                    // 초기값에서 수정되었던 것을 다시 되돌리면 리스트 삭제
                    checkChangeList.remove(item.indicatorItem.tag)
                } else {
                    checkChangeList.put(item.indicatorItem.tag, isAdd)
                }
                adapter.notifyDataSetChanged()
            }
        })
        recyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false,
            ),
        )
        recyclerView.adapter = adapter
    }

    // 2021. 4. 28  by hanjun.Kim - kakaopay - 차트 초기 체크값들설정 >>
    fun initAapterData() {
        // 추가기능 체크여부 ( 코드, 체크상태값(Boolean) )
        val additionalItemCheck = getAddtionalItemCheck()

        // 차트에 추가되어있는 지표 리스트(tag 리스트) 를 가져온다
        val graphLists: Vector<String>? =
            (activity as KfitConfigControllerActivity).getGraphTagList()
        adapter.setCustomActivity(activity as KfitConfigControllerActivity)
        adapter.setCheckedItem(graphLists)
        adapter.setAdditionCheckedItem(additionalItemCheck)
    }
    // 2021. 4. 28  by hanjun.Kim - kakaopay - 차트 초기 체크값들설정 <<

    // 2021. 4. 28  by hanjun.Kim - kakaopay - 추가 기능 설정값 >>
    fun getAddtionalItemCheck(): ArrayList<String> {
        val checkAdditionItem = arrayListOf<String>()

        val isIndexChartState: Boolean =
            (activity as KfitConfigControllerActivity).isIndexChart() // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가

        // 추가 기능 설정값 가져오기 >>

        // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 >>
        if (!isIndexChartState) {
            // 평단가 설정 유무
            val strAvgBuyState =
                (activity as KfitConfigControllerActivity).getMethod("getAvgBuyState", "")
            if (strAvgBuyState != null && strAvgBuyState as Boolean) {
                checkAdditionItem.add(
                    AVGBUY_PRICE.toString(),
                )
            }

            // 매수,매도 설정 유무 가져오기
            val strBuySellState =
                (activity as KfitConfigControllerActivity).getMethod("getBuySellState", "")
            if (strBuySellState != null && strBuySellState as Boolean) {
                checkAdditionItem.add(
                    BUYSELL_PRICE.toString(),
                )
            }

            // 기업 캘린더 기능 설정값 가져오기
            val strCorporateCalendarState = (activity as KfitConfigControllerActivity).getMethod(
                "getCorporateCalendarState",
                "",
            )
            if (strCorporateCalendarState != null && strCorporateCalendarState as Boolean) {
                checkAdditionItem.add(
                    CORPORATE_CALENDAR.toString(),
                )
            }
        }
        // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 <<

        // 추세선 설정 유무 가져오기
        val strAutoTrendWaveLineState =
            (activity as KfitConfigControllerActivity).getMethod("getAutoTrendWaveLine", "")
        if (strAutoTrendWaveLineState != null && strAutoTrendWaveLineState as Boolean) {
            checkAdditionItem.add(
                AUTOTRENDWAVE_TYPE.toString(),
            )
        }

        // 지지선/저항선 설정 유무 가져오기
        val strSupportResistanceLineState =
            (activity as KfitConfigControllerActivity).getMethod("getSupportResistanceLine", "")
        if (strSupportResistanceLineState != null && strSupportResistanceLineState as Boolean) {
            checkAdditionItem.add(
                SUPPORT_RESISTANCE_LINE.toString(),
            )
        }

        // 이동평균선 기능 설정값 가져오기
        val strMovingAverageLineState = (activity as KfitConfigControllerActivity).getMethod("getMovingAverageLineState", "")
        if (strMovingAverageLineState != null && strMovingAverageLineState as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE.toString())

//        // 5일 이평
//        val strMA5 = (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 5")
//        if (strMA5 != null && strMA5 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE1.toString())
//
//        // 20일 이평
//        val strMA20 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 20")
//        if (strMA20 != null && strMA20 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE2.toString())
//
//        // 60일 이평
//        val strMA60 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 60")
//        if (strMA60 != null && strMA60 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE3.toString())
//
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//        // 10일 이평
//        val strMA10 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 10")
//        if (strMA10 != null && strMA10 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE4.toString())
//
//        // 120일 이평
//        val strMA120 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 120")
//        if (strMA120 != null && strMA120 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE5.toString())
//
//        // 200일 이평
//        val strMA200 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 200")
//        if (strMA200 != null && strMA200 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE6.toString())
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
//
//        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
//        // 240일 이평
//        val strMA240 =
//            (activity as KfitConfigControllerActivity).getMethod("getVisibleGraph", "이평 240")
//        if (strMA240 != null && strMA240 as Boolean) checkAdditionItem.add(INDICATOR_PAVERAGE7.toString())
//        // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<
        // 추가 기능 설정값 가져오기 <<

        return checkAdditionItem
    }
    // 2021. 4. 28  by hanjun.Kim - kakaopay - 추가 기능 설정값 <<

    fun createAdapter(): ChartSettingDisplayInfoAdapter {
        var items = mutableListOf<CheckableDisplayInfo>()

        val isIndexChartState: Boolean? =
            (activity as? KfitConfigControllerActivity)?.isIndexChart() // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가
        // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 >>
        if (!isIndexChartState!!) {
            // 내 정보
            items.add(CheckableDisplayInfo(getString(R.string.displayinfo_header_myinfo), true))
            items.add(
                CheckableDisplayInfo(
                    AVGBUY_PRICE,
                    getString(R.string.indicator_myinfo_item1),
                ),
            )
            items.add(
                CheckableDisplayInfo(
                    BUYSELL_PRICE,
                    getString(R.string.indicator_myinfo_item2),
                ),
            )
            items.add(
                CheckableDisplayInfo(
                    CORPORATE_CALENDAR,
                    getString(R.string.indicator_myinfo_item3),
                ),
            )
        }
        // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 <<

        // 오버레이(차트에 함께 표시)
        items.add(CheckableDisplayInfo(getString(R.string.displayinfo_header_overlay), true))
        items.add(
            CheckableDisplayInfo(
                INDICATOR_RAINBOW,
                getString(R.string.indicator_overlay_item1),
            ),
        )
        items.add(
            CheckableDisplayInfo(
                INDICATOR_PAVERAGE,
                getString(R.string.indicator_overlay_item15),
            ),
        )
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE1,
//                getString(R.string.indicator_overlay_item2),
//            ),
//        )
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE4,
//                getString(R.string.indicator_overlay_item11),
//            ),
//        ) // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE2,
//                getString(R.string.indicator_overlay_item3),
//            ),
//        )
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE3,
//                getString(R.string.indicator_overlay_item4),
//            ),
//        )
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE5,
//                getString(R.string.indicator_overlay_item12),
//            ),
//        )
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE6,
//                getString(R.string.indicator_overlay_item13),
//            ),
//        )
//        // 2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
//        items.add(
//            CheckableDisplayInfo(
//                INDICATOR_PAVERAGE7,
//                getString(R.string.indicator_overlay_item14),
//            ),
//        ) // 2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청
        items.add(
            CheckableDisplayInfo(
                INDICATOR_ENVELOPE,
                getString(R.string.indicator_overlay_item10),
            ),
        ) // 2023.06.27 by SJW - 엔벨로프 지표 추가
        items.add(
            CheckableDisplayInfo(
                INDICATOR_GLANCE_BALANCE,
                getString(R.string.indicator_overlay_item5),
            ),
        )
        items.add(
            CheckableDisplayInfo(
                INDICATOR_BOLLINGER,
                getString(R.string.indicator_overlay_item6),
            ),
        )
        items.add(
            CheckableDisplayInfo(
                INDICATOR_STANDSCALE,
                getString(R.string.indicator_overlay_item7),
            ),
        )
        items.add(
            CheckableDisplayInfo(
                AUTOTRENDWAVE_TYPE,
                getString(R.string.indicator_overlay_item8),
            ),
        )
        items.add(
            CheckableDisplayInfo(
                SUPPORT_RESISTANCE_LINE,
                getString(R.string.indicator_overlay_item9),
            ),
        )

        return ChartSettingDisplayInfoAdapter(items)
    }
}
