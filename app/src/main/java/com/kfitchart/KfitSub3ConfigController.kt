package com.kfitchart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kakaopay.feature.stock.common.presentation.R
import com.kfitchart.entity.KfitIndexType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Vector
import java.util.logging.Logger

/**
 * @author hanjun.Kim
 */
class KfitSub3ConfigController : KfitBaseFragment() {
    lateinit var fragmentView: View

    companion object {
        val log = Logger.getLogger(KfitSub3ConfigController::class.java.name)
    }

    lateinit var adapter: ChartSettingIndicatorAdapter

//    private val adapter: ChartSettingIndicatorAdapter by lazy {
//        ChartSettingIndicatorAdapter(List<CheckableIndicator()>)
//    }

    var checkChangeList = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentView = inflater.inflate(R.layout.kfit_fragment_setting_indicator, container, false)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()

        if (this::adapter.isInitialized) {
            val isStandGraph =
                (activity as KfitConfigControllerActivity).isStandGraph()

            if (isStandGraph) {
                adapter?.let {
                    adapter.setClickableAll(false)
                }
            } else {
                adapter?.let {
                    adapter.setClickableAll(true)
                }
            }
            adapter?.let {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun getTitleRes(): Int? {
        return R.string.tab_title_indicator
    }

    override fun refresh() {
        if (activity != null) {
            val graphLists: Vector<String>? =
                (activity as KfitConfigControllerActivity).getGraphTagList()
            adapter.setCheckedItem(graphLists)
            adapter.setCustomActivity(activity as KfitConfigControllerActivity)
            adapter.notifyDataSetChanged()
            checkChangeList.clear()
        }
    }

    override fun launchSelect() {
        if (activity != null) {
            if (checkChangeList.isNotEmpty()) {
                checkChangeList.forEach { (itemTag, isAdd) ->
                    (activity as KfitConfigControllerActivity).setChartFunc(itemTag + ";" + isAdd)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        this.view?.setTag("1003")
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_view)
// 2021.10.19 by JHY - 체크박스 선택시 backgroundcolor >>
        recyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

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
                        view?.findViewById<View>(R.id.fctb_AbsoluteLayout01)?.setBackgroundColor(
                            Color.argb(10, 0, 0, 0),
                        )
                        // 2023.11.20 by CYJ - kakaopay 터치영역 디자인 >>
                        view?.findViewById<View>(R.id.indicatorNameLayout)?.setBackgroundColor(
                            Color.argb(0, 0, 0, 0),
                        )
                        view?.findViewById<View>(R.id.indicatorConfigButtonLayout)?.setBackgroundColor(
                            Color.argb(0, 0, 0, 0),
                        )
                        // 2023.11.20 by CYJ - kakaopay 터치영역 디자인 <<
                    }
                }
                // 손가락 뗏을때
                else {
                    var child = rv.findChildViewUnder(e.getX(), e.getY())
                    if (child != null) {
                        var position = rv.getChildAdapterPosition(child)
                        var view = rv.layoutManager?.findViewByPosition(position)
                        // 터치 한 부분을 제외하고 for문 돌리면서 모든 position 을 비활성화(투명) 시킨다
                        for (i in 0..rv.adapter!!.itemCount) {
                            var otherView = rv.layoutManager?.findViewByPosition(i)
                            if (otherView != view) {
                                otherView?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
                                    ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                            }
                            // 버그 예외처리 이유아직모름
                            else {
                                otherView?.findViewById<View>(R.id.fctb_AbsoluteLayout01)
                                    ?.setBackgroundColor(Color.argb(0, 0, 0, 0))
                            }
                        }
                    }
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }
        })
        // 2021.10.19 by JHY - 체크박스 선택시 backgroundcolor <<
        recyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false,
            ),
        )

        // 2021.09.09 by lyk - kakaopay - 독립차트 타입 (StandGraph Type)인 경우 보조 지표 선택이 불가하도록 dimmed 처리 >>
//        val selectChartTag =
//            (activity as KfitConfigControllerActivity).getMethod("getSelectedChartType", "")
//        val isStandGraph =
//            (activity as KfitConfigControllerActivity).isStandGraph()
//
//        if(isStandGraph) {
//            lifecycleScope.launch {
//                val list = withContext(Dispatchers.IO) {
//                    loadNormalIndicatorList()
//                }
//
//                adapter = ChartSettingIndicatorAdapter(list)
//                adapter.setClickableAll(false)
//                adapter.setItemClickListener(object :
//                    ChartSettingIndicatorAdapter.OnItemClickListener {
//                    override fun onClick(v: View, position: Int, item: CheckableIndicator) {
//                        adapter.notifyDataSetChanged()
//                    }
//                })
//                recyclerView.adapter = adapter
//            }
//        }
//        //2021.09.09 by lyk - kakaopay - 독립차트 타입 (StandGraph Type)인 경우 보조 지표 선택이 불가하도록 dimmed 처리 <<
//        else {

        // 차트에 추가되어있는 지표 리스트(tag 리스트) 를 가져온다
        val graphLists: Vector<String>? =
            (activity as KfitConfigControllerActivity).getGraphTagList()

        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) {
                loadNormalIndicatorList()
            }

            adapter = ChartSettingIndicatorAdapter(list)
            adapter.setClickableAll(true)
            adapter.setCustomActivity(activity as KfitConfigControllerActivity)
            adapter.setCheckedItem(graphLists)
            adapter.setItemClickListener(object :
                ChartSettingIndicatorAdapter.OnItemClickListener {
                override fun onClick(v: View, position: Int, item: CheckableIndicator) {
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
            recyclerView.adapter = adapter
        }
//        }
    }

    // 2021.04.21 by hanjun.Kim - kakaopay - json파일 데이터 추출 >>
    private fun loadNormalIndicatorList(): List<CheckableIndicator> {
        val returnData = arrayListOf<KfitIndicatorListItem>()

        try {
            val inputStream =
                requireContext().applicationContext.assets.open("normal_indicator_list.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()

            val listdata =
                Gson().fromJson(jsonString, Array<KfitIndicatorListItem>::class.java)
                    .asList()

            // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 >>
            val isIndexChartState: Boolean =
                (activity as KfitConfigControllerActivity).isIndexChart()
            val isIndexType: KfitIndexType? =
                (activity as KfitConfigControllerActivity).isIndexType()
            val isDomestic: Boolean? = (activity as KfitConfigControllerActivity).isDomestic()
            if (!isIndexChartState) {
                listdata.filter {
                    it.use == "1" &&
                        (it.detailType == "jipyo" || it.detailType == "other") &&
                        it.type == "Indicator"
                }.forEach {
                    returnData.add(it)
                }
            } else {
                if (!isDomestic!! || (isIndexType != null && isIndexType.ordinal == KfitIndexType.FUTURE.ordinal)) { // 2022.04.11 by lyk - kakaopay - 미국지수, 선물 거래대금 삭제
                    listdata.filter {
                        it.use == "1" &&
                            (it.detailType == "jipyo" && it.tag != "30043") &&
                            it.type == "Indicator"
                    }.forEach {
                        returnData.add(it)
                    }
                } else {
                    listdata.filter {
                        it.use == "1" &&
                            (it.detailType == "jipyo") &&
                            it.type == "Indicator"
                    }.forEach {
                        returnData.add(it)
                    }
                }
            }
            // 2022.04.11 by lyk - kakaopay - 지수상세 차트추가 <<
        } catch (e: Exception) {
            log.info("KfitSub3ConfigController read NormalIndicatorList error > $e")
        }

        return returnData.map {
            CheckableIndicator(it, false)
        }
    }

    // 2021.04.21 by hanjun.Kim - kakaopay - json파일에서 데이터 추출 <<
}
