package com.kfitchart

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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

class ChartSettingIndicatorAdapter(val items: List<CheckableIndicator>) :
    RecyclerView.Adapter<ChartSettingIndicatorAdapter.ChartSettingIndicatorHolder>() {
    var onClick: ((displayInfo: CheckableIndicator) -> Unit)? = null
    val itemSelectedTag: MutableList<String> = mutableListOf() // 바텀시트 띄울때 차트에 적용된 tag값 사용
    var isPreviosViewHeader: Boolean = false
    var ChkboxSelected: String = ""
    val MAX_SELECT = 4
    var activity: FragmentActivity? = null

    companion object {
        private const val HEADER = "header"
        private const val CONTENTS = "contents"
        private const val EMPTYVIEW = "empty"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChartSettingIndicatorHolder, position: Int) {
        val adapter = this
        // 아이템 UI 와 관련된 clicklistener (data에 관련된 이벤트들은 itemClickListener)
        holder.apply {
            bind(
                items[position],
                View.OnClickListener {
                    if (!items[position].isClickable) {
                    } else {
                        if (!items[position].isHeader) {
                            // 최대 선택 갯수 제한
                            if (getCheckedCount() >= MAX_SELECT && !items[position].isCheck) {
                                itemView.context?.let {
                                    val dialog = Dialog(it)
                                    dialog.setCanceledOnTouchOutside(false)
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                    // 2021.11.18 by JHY - z-fold 싱글화면 예외처리 >
//                                if(checkFoldedDisplay(itemView.context) == 2) { //폴드 접힌 상태
//                                    if (it.resources.displayMetrics.widthPixels <= 900) { //900 , 884(폴드접힌상태)
//                                        dialog.setContentView(R.layout.kfit_alert_select_limit2)
//                                        dialog.findViewById<TextView>(R.id.kfit_alert_content2)
//                                            .setText(it.getString(R.string.chart_setting_indicator_select_limit_message2))
//                                    } else {
//                                        dialog.setContentView(R.layout.kfit_alert_select_limit)
//                                        dialog.findViewById<TextView>(R.id.kfit_alert_content)
//                                            .setText(it.getString(R.string.chart_setting_indicator_select_limit_message))
//                                    }
//                                } else {
                                    // 2024.01.09 by CYJ - 보조지표 최대개수 팝업 (기획변경) >>
//                                    if (it.resources.displayMetrics.widthPixels > 720 && it.resources.displayMetrics.widthPixels < 905) { // 900 , 884(폴드접힌상태)
                                    dialog.setContentView(R.layout.kfit_alert_select_limit2)
                                    dialog.findViewById<TextView>(R.id.kfit_alert_content2)
                                        .setText(it.getString(R.string.chart_setting_indicator_select_limit_message2))
//                                    } else {
//                                        dialog.setContentView(R.layout.kfit_alert_select_limit)
//                                        dialog.findViewById<TextView>(R.id.kfit_alert_content)
//                                            .setText(it.getString(R.string.chart_setting_indicator_select_limit_message))
//                                    }
                                    // 2024.01.09 by CYJ - 보조지표 최대개수 팝업 (기획변경) <<
//                                }
                                    // 2021.11.18 by JHY - z-fold 싱글화면 예외처리 <<

//                                dialog.setContentView(R.layout.kfit_alert_select_limit)
//                                dialog.findViewById<TextView>(R.id.kfit_alert_content)
//                                    .setText(it.getString(R.string.chart_setting_indicator_select_limit_message))

                                    // 2021.10.06 by.JHY - 확인 버튼 이벤트 >>
                                    dialog.findViewById<Button>(R.id.kfit_ok_btn)
                                        .setOnTouchListener { v, event ->
                                            // 2021.10.06 by JHY - Scale 확대 축소 애니메이션 >>
                                            val reduce = AnimationUtils.loadAnimation(
                                                itemView.context,
                                                R.anim.anim_reduce,
                                            )
                                            val enlarge = AnimationUtils.loadAnimation(
                                                itemView.context,
                                                R.anim.anim_enlarge,
                                            )
                                            // 2021.10.06 by JHY - Scale 확대 축소 애니메이션 <<
                                            when (event.action) {
                                                MotionEvent.ACTION_DOWN -> {
                                                    v.startAnimation(reduce)
                                                    v.background.setAlpha(150)
                                                    v.invalidate()
                                                }

                                                MotionEvent.ACTION_UP -> {
                                                    v.startAnimation(enlarge)
                                                    v.background.setAlpha(255)
                                                    v.invalidate()
                                                }
                                            }
                                            false
                                        }
                                    // 2021.10.06 by.JHY - 확인 버튼 이벤트 <<

                                    // 2021.10.07 by JHY - Dialog animation >>
                                    dialog.window?.attributes?.windowAnimations =
                                        drfn.chart_src.R.style.CustomDialogAnimation
                                    // 2021.10.07 by JHY - Dialog animation <<

                                    dialog.findViewById<Button>(R.id.kfit_ok_btn).setOnClickListener {
                                        dialog.dismiss()
                                    }
                                    dialog.show()

                                    dialog.window?.setGravity(Gravity.CENTER)
                                    dialog.window?.setLayout(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    )
                                }
                            } else {
                                val preChekcked = items[position].isCheck
                                if (preChekcked) {
                                    itemSelectedTag.remove(items[position].indicatorItem.tag)
                                } else {
                                    itemSelectedTag.add(items[position].indicatorItem.tag)
                                }
                                items[position].isCheck = !items[position].isCheck
                                adapter.notifyItemChanged(position)
                                // 2021.10.13 by JHY - 체크박스 animation 선택값 변수지정 >>
                                ChkboxSelected = items[position].indicatorItem.tag
                                // 2021.10.13 by JHY - 체크박스 animation 선택값 변수지정 <<
                                itemClickListener.onClick(it, position, items[position])
                            }
                        }
                    }
                },
            )
        }
    }

    fun checkFoldedDisplay(context: Context): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val dm = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            dm?.displays?.forEach {
                return it.mode.hashCode()
                // mode: 1 펼친 상태
                // mode: 2 접힌 상태
            }
        }

        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartSettingIndicatorHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.kfit_sub2_cell_item, parent, false)
        return ChartSettingIndicatorHolder(view)
    }

    inner class ChartSettingIndicatorHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(item: CheckableIndicator, onClickListener: View.OnClickListener) {
            with(itemView) {
                changeLayout(itemView, CONTENTS)
                val tvContent: TextView = itemView.findViewById(R.id.fctb_TextViewA01)
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

                tvContent.text = item.indicatorItem.name
                item.indicatorItem.name.run {
                    if (contains("기업 일정 표시 여부") || contains("외국인 비율") || contains("외국인/기관/개인 추세") ||
                        contains("기관 순매수") || contains("외국인 순매수") || contains("개인 순매수")
                    ) {
                        tvContentHint.visibility = View.VISIBLE
                        tvContentHint.setText(context.getString(R.string.indicator_item_hint_message))

                        // 2023.06.16 by SJW - 차트설정 서브 텍스트 영역 수정 >>
                        val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 8
                        tvContent.layoutParams = layoutParams
                    } else {
                        val layoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.topMargin = 41
                        tvContent.layoutParams = layoutParams
                        // 2023.06.16 by SJW - 차트설정 서브 텍스트 영역 수정 <<
                    }

                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 레이아웃 설정 >>
                    val hintLayoutParams = tvContent.layoutParams as ViewGroup.MarginLayoutParams
                    if (tvContentHint.isVisible) {
                        hintLayoutParams.topMargin = COMUtil.getPixel(5).toInt()
                        tvContent.layoutParams = hintLayoutParams
                        tvContent.layoutParams.height = -2
                    } else { // 힌트가 있을때와 없을때의
                        hintLayoutParams.topMargin = 0
                        tvContent.layoutParams = hintLayoutParams
                        tvContent.layoutParams.height = -1
                        tvContent.gravity = Gravity.CENTER_VERTICAL
                    }
                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 레이아웃 설정 <<
                }

                if (isPreviosViewHeader) {
                    itemView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
                } else if (layoutPosition == 0) {
                    itemView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
                    itemView.findViewById<View>(R.id.sub2_cell_headerMarginView).visibility =
                        View.VISIBLE
                }
                isPreviosViewHeader = false

                val imageButton: ImageButton = itemView.findViewById(R.id.usf_ImageButton)
                imageButton.isClickable = false
                imageButton.isSelected = false
                imageButton.isEnabled = false

                // 2021.04.22 by hanjun.Kim - kakaopay - 차트에서 불러온 체크값들 적용 >>
                if (!itemSelectedTag.isNullOrEmpty() && itemSelectedTag.contains(item.indicatorItem.tag) && item.isClickable) {
                    item.isCheck = true
                    imageButton.isEnabled = true
                    imageButton.isSelected = true
                }
                // 2021.04.22 by hanjun.Kim - kakaopay - 차트에서 불러온 체크값들 적용 >>

                // 지표 상세 설정 사용시 추가 >>
                if (item.isCheck && isAvailableConfigIndicator(item.indicatorItem.tag)) {
                    tvConfigContent.text = "설정"
                    tvConfigContent.visibility = View.VISIBLE
                    // 2023.11.15 by CYJ - kakaopay 설정 디테일 이미지 설정 (다크모드) >>
                    ivConfigContent.visibility = View.VISIBLE
                    val currentNightMode =
                        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
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
    RSI : 30008
    MACD : 30002
    OBV : 30009
    Stochastic Fast : 30004
    Stochastic Slow : 30003
    MFI : 30022
    DMI : 30005
     */
    private fun isAvailableConfigIndicator(tag: String): Boolean {
        var rtnVal = false
        /*
                 RSI : 30008
                 MACD : 30002
                 OBV : 30009 : 2차
                 Stochastic Fast : 30004 : 2차
                 Stochastic Slow : 30003 : 2차
                 MFI : 30022 : 2차
                 DMI : 30005 : 2차
                 ADX :30006 : 2차
         */
        if (tag == "30008" || tag == "30002" || tag == "30009" || tag == "30004" || tag == "30003" || tag == "30022" || tag == "30005" || tag == "30006") {
            rtnVal = true
        }

//        if(tag == "30008" || tag == "30002") {
//            rtnVal = true
//        }

        return rtnVal
    }

    fun setCustomActivity(activityVal: FragmentActivity) {
        activity = activityVal
    }

    private fun changeLayout(parentView: View, type: String) {
        if (type == HEADER) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_headerMarginView).visibility = View.GONE
        } else if (type == CONTENTS) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.sub2_cell_headerMarginView).visibility = View.GONE
        } else if (type == EMPTYVIEW) {
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout01).visibility = View.GONE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout02).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.fctb_AbsoluteLayout03).visibility = View.VISIBLE
            parentView.findViewById<View>(R.id.sub2_cell_divider).visibility = View.GONE
            parentView.findViewById<View>(R.id.sub2_cell_headerMarginView).visibility = View.GONE
        }
    }

    fun setCheckedItem(itemsList: Vector<String>?) {
        if (!itemsList.isNullOrEmpty()) {
            checkClear()
            itemSelectedTag.clear()
            itemSelectedTag.addAll(itemsList)
        }
    }

    fun setClickableAll(value: Boolean) {
        items.forEach {
            it.isClickable = value
//            itemSelected[it.indicatorItem.tag.toInt()] = it
        }
    }

    fun checkAll() {
        items.forEach {
            it.isCheck = true
//            itemSelected[it.indicatorItem.tag.toInt()] = it
        }
    }

    fun checkClear() {
        items.forEach {
            it.isCheck = false
//            itemSelected[it.indicatorCode] = it
        }
    }

    fun getCheckedCount(): Int {
        return items.count { itemSelectedTag.contains(it.indicatorItem.tag) }
    }

    // 2021.04.22 by hanjun.Kim - kakaopay - View(Fragment)에서 Click Event 정의하기 위한 interface >>
    interface OnItemClickListener {
        fun onClick(v: View, position: Int, item: CheckableIndicator)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
    // 2021.04.22 by hanjun.Kim - kakaopay - View(Fragment)에서 Click Event 정의하기 위한 interface <<
}

open class CheckableIndicator(
    var indicatorItem: KfitIndicatorListItem,
    var isHeader: Boolean = false,
    var isClickable: Boolean = true,
) {
    var isCheck = false
}
