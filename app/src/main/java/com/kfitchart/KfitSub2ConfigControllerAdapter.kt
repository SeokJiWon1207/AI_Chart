package com.kfitchart

import android.content.res.Configuration
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kakaopay.feature.stock.common.presentation.R
import drfn.chart.util.COMUtil
import java.util.Vector

/**
 * @author hanjun.Kim
 */

class ChartSettingDisplayInfoAdapter(val items: MutableList<CheckableDisplayInfo>) :
    RecyclerView.Adapter<ChartSettingDisplayInfoAdapter.ChartSettingIndicatorHolder>() {
    var onClick: ((displayInfo: CheckableDisplayInfo) -> Unit)? = null
    val itemSelectedTag: MutableList<String> = mutableListOf() // 바텀시트 띄울때 차트에 적용된 tag값 사용
    var isPreviosViewHeader: Boolean = false
    var ChkboxSelected: String = ""
    var activity: FragmentActivity? = null

    companion object {
        private const val HEADER = "header"
        private const val CONTENTS = "contents"
        private const val EMPTYVIEW = "empty"
        private const val SETTING = "setting"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChartSettingIndicatorHolder, position: Int) {
        val adapter = this
        holder.apply {
            bind(
                items[position],
                View.OnClickListener {
                    if (!items[position].isHeader) {
                        items[position].isCheck = !items[position].isCheck
                        adapter.notifyItemChanged(position)
                        // 2021.10.13 by JHY - 체크박스 animation 선택값 변수지정 >>
                        ChkboxSelected = items[position].indicatorItem.tag
                        // 2021.10.13 by JHY - 체크박스 animation 선택값 변수지정 <<
                        itemClickListener.onClick(it, position, items[position])
                    }
                },
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartSettingIndicatorHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.kfit_sub2_cell_item, parent, false)
        return ChartSettingIndicatorHolder(view)
    }

    inner class ChartSettingIndicatorHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(item: CheckableDisplayInfo, onClickListener: View.OnClickListener) {
            with(itemView) {
                val tvContentHint: TextView = itemView.findViewById(R.id.sub2_cell_item_textlint)
                tvContentHint.visibility = View.GONE

                val tvConfigContent: TextView = itemView.findViewById(R.id.fctb_TextView_config)
                tvConfigContent.visibility = View.GONE

                // 2023.11.15 by CYJ - kakaopay 설정 디테일 이미지 설정 (다크모드) >>
                val ivConfigContent: ImageView = itemView.findViewById(R.id.iv_config)
                ivConfigContent.visibility = View.GONE
                // 2023.11.15 by CYJ - kakaopay 설정 디테일 이미지 설정 (다크모드) <<
                val configLayout: LinearLayout = itemView.findViewById(R.id.indicatorConfigButtonLayout)
                configLayout.visibility = View.GONE

                // 2021.07.23 by hanjun.Kim - kakaopay - 하단 여유공간 추가 >>
                if (adapterPosition == (itemCount - 1)) {
                    itemView.findViewById<View>(R.id.kfit_cell_emptyview).visibility = View.VISIBLE
                } else {
                    itemView.findViewById<View>(R.id.kfit_cell_emptyview).visibility = View.GONE
                }
                // 2021.07.23 by hanjun.Kim - kakaopay - 하단 여유공간 추가 <<

                if (item.isHeader && layoutPosition != 0) {
                    changeLayout(itemView, EMPTYVIEW)
                    val tvContent: TextView = itemView.findViewById(R.id.sub2_cell_headerTextview)
                    tvContent.text = item.indicatorItem?.name
                    isPreviosViewHeader = true
                } else if (item.isHeader) {
                    changeLayout(itemView, HEADER)
                    val tvContent: TextView = itemView.findViewById(R.id.sub2_cell_headerTextview)
                    tvContent.text = item.indicatorItem?.name
                    isPreviosViewHeader = true
                } else {
                    changeLayout(itemView, CONTENTS)
                    val tvContent: TextView = itemView.findViewById(R.id.fctb_TextViewA01)
                    tvContent.text = item.indicatorItem?.name
                    if (item.indicatorItem?.tag == "65002" || item.indicatorItem?.tag == "40004") { // 구매평균가격 표시, 그물망 위 라인
                        itemView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
                    }
                    isPreviosViewHeader = false

                    if (item.indicatorItem.tag == "65004") {
                        tvContentHint.visibility = View.VISIBLE
                        tvContentHint.setText(context.getString(R.string.indicator_item_hint_message))

                        // 2023.06.16 by SJW - 차트설정 서브 텍스트 영역 수정 >>
                        val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 8
                        tvContent.layoutParams = layoutParams
                    } else if (item.indicatorItem.tag == "40099") { // 이동평균선
                        val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 41
                        tvContent.layoutParams = layoutParams
                    } else {
                        val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 41
                        tvContent.layoutParams = layoutParams
                        // 2023.06.16 by SJW - 차트설정 서브 텍스트 영역 수정 <<
                    }

                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 레이아웃 설정 >>
                    val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                    if (tvContentHint.isVisible) {
                        layoutParams.topMargin = COMUtil.getPixel(5).toInt()
                        tvContent.layoutParams = layoutParams
                        tvContent.layoutParams.height = -2
                    } else { // 힌트가 있을때와 없을때의
                        layoutParams?.topMargin = 0
                        tvContent.layoutParams = layoutParams
                        tvContent.layoutParams.height = -1
                        tvContent.gravity = Gravity.CENTER_VERTICAL
                    }
                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 레이아웃 설정 <<

                    if (item.isCheck && item.indicatorItem.tag == "40018") {
                        changeLayout(itemView, SETTING)
                    }
                }

                val imageButton: ImageButton = itemView.findViewById(R.id.usf_ImageButton)
                imageButton.isClickable = false

                // 2021.04.22 by hanjun.Kim - kakaopay - 차트에서 불러온 체크값들 적용 >>
                if (!itemSelectedTag.isNullOrEmpty() && itemSelectedTag.contains(item.indicatorItem.tag)) {
                    item.isCheck = true
                    itemSelectedTag.remove(item.indicatorItem.tag) // 처음로딩때만 사용
                }
                // 2021.04.22 by hanjun.Kim - kakaopay - 차트에서 불러온 체크값들 적용 >>

                // 지표 상세 설정 사용시 추가 >>
                if (item.isCheck && isAvailableConfigIndicator(item.indicatorItem.tag)) {
                    tvConfigContent.text = "설정"
                    tvConfigContent.visibility = View.VISIBLE
                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 이미지 설정 (다크모드) >>
                    ivConfigContent.visibility = View.VISIBLE
                    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                        ivConfigContent.setBackgroundResource(R.drawable.kfit_core_ic_common_detail)
                    } else {
                        ivConfigContent.setBackgroundResource(R.drawable.kfit_core_ic_common_detail_dark)
                    }
                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 이미지 설정 (다크모드) <<

                    configLayout.visibility = View.VISIBLE
                    configLayout?.setTag(65005) // INDICATOR_CONFIG_VIEW
                    configLayout.setOnClickListener {
                        // 지표 상세설정 팝업의 루트뷰를 재지정한다. (설정 버튼 누를 떄 지정)
                        COMUtil.apiView = rootView as ViewGroup?
                        // 지표 상세 설정창 처리
                        val conTag = "65005" // INDICATOR_CONFIG_VIEW
                        (activity as KfitConfigControllerActivity).setChartFunc(conTag + ";" + item.indicatorItem.tag)
                    }
                }
                // 지표 상세 설정 사용시 추가 <<

                // 2021.10.08 by JHY - 라디오 버튼 애니메이션 >>
//                if(item.isCheck == true) {
//                    imageButton.startAnimation(AnimationUtils.loadAnimation(this.context,R.anim.anim_fadein_large))
//                }
                // 2021.10.08 by JHY - 라디오 버튼 애니메이션 <<

                imageButton.isSelected = item.isCheck
                // 2021.10.13 by JHY - 체크박스 선택시 fadein >>
                if (item.isCheck && (ChkboxSelected == item.indicatorItem.tag)) {
                    imageButton.startAnimation(
                        AnimationUtils.loadAnimation(
                            itemView.context,
                            R.anim.anim_fadein_large,
                        ),
                    )
                }
                // 선택해제시 fadeout 미완성
//                else if (!item.isCheck &&(ChkboxSelected==item.indicatorItem.tag)) {
//                    imageButton.startAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.anim_fadeout_small))
//                }
                // 2021.10.13 by JHY - 체크박스 선택시 fadein <<
                setOnClickListener(onClickListener)
            }
        }
    }

    /*
    이동평균선 : 40099
    일목균형표 : 40003
    볼린저밴드 : 40001
    매물대    : 11000
    엔벨로프   : 40002
     */
    private fun isAvailableConfigIndicator(tag: String): Boolean {
        var rtnVal = false
        if (tag == "40099" || tag == "40003" || tag == "40001" || tag == "11000" || tag == "40002") {
            rtnVal = true
        }

        return rtnVal
    }

    private fun changeLayout(parentView: View, type: String) {
        if (type == HEADER) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
        } else if (type == CONTENTS) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.VISIBLE
        } else if (type == EMPTYVIEW) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
        } else if (type == SETTING) {
            parentView.findViewById<View>(R.id.indicatorConfigButtonLayout).visibility = View.VISIBLE
        }
    }

    fun setCustomActivity(activityVal: FragmentActivity) {
        activity = activityVal
    }

    fun setCheckedItem(itemsList: Vector<String>?) {
        if (!itemsList.isNullOrEmpty()) {
            checkClear()
            itemSelectedTag.clear()
            itemSelectedTag.addAll(itemsList)
        }
    }

    fun setAdditionCheckedItem(itemsList: ArrayList<String>?) {
        if (!itemsList.isNullOrEmpty()) {
            itemSelectedTag.addAll(itemsList)
        }
    }

    fun checkAll() {
//        itemSelected.clear()
        items.forEach {
            it.isCheck = true
        }
    }

    fun checkClear() {
//        itemSelected.clear()
        items.forEach {
            it.isCheck = false
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int, item: CheckableDisplayInfo)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}

open class CheckableDisplayInfo(
    var indicatorItem: KfitIndicatorListItem,
    var isHeader: Boolean = false,
) {
    var isCheck = false

    constructor(title: String, isHeader: Boolean) : this(KfitIndicatorListItem(title), isHeader)
    constructor(code: Int, message: String) : this(KfitIndicatorListItem(code, message))
}
