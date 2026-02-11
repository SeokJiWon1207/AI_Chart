package drfn.chart.comp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//import kr.co.kfits.conds.db.CondItem;
//import kr.co.kfits.conds.db.CondMng;
//import kr.co.kfits.conds.db.StrategyItem;
//import kr.co.kfits.conds.util.CondCommonUtil;
//import kr.co.kfits.conds.view.CondCompoundListView;
//import kr.co.kfits.conds.view.CondTabBarController;
import drfn.chart.base.Base11;
import drfn.chart.base.JipyoTabViewController;
import drfn.chart.net.CommonUtil;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;

@SuppressLint("NewApi")
public class DRBottomDialog extends Dialog implements View.OnClickListener {

	//Header
	final private int HEADER_TEXT_COLOR = Color.parseColor("#000000");
	final private int HEADER_TEXT_SIZE = 17;

	//Button - Yes
	final private int YES_BUTTON_TEXT_COLOR = Color.parseColor("#ffffff");

	final public static int BOTTOMVIEW_TYPE_LIST = 0;
	final public static int BOTTOMVIEW_TYPE_LIST_BUTTON = 1;
	final public static int BOTTOMVIEW_TYPE_MESSAGE = 2;
	final public static int BOTTOMVIEW_TYPE_VIEW = 3;
	final public static int BOTTOMVIEW_TYPE_CHECK_LIST = 4;
	final public static int BOTTOMVIEW_TYPE_LIST_DEL = 5;
	final public static int BOTTOMVIEW_TYPE_COMPOUND = 6;
	final public static int BOTTOMVIEW_TYPE_CHARTSETTING = 7;	//2020.04.24 by JJH >> 차트 설정 팝업 UI 수정

	//============================
	// Variable
	//============================
	private Context m_context;
	
	private LinearLayout m_confirm_dialog;
	private TextView m_tvConfirmTitle;
	private TextView m_tvMessage;
	public LinearLayout rlContent;
	
	private Button m_btnYes, m_btnNo, btnAddGroupStrategy;
	private int m_nBtnYesId, m_nBtnNoId, m_nBtnOkId, m_nBtnPrev;

	private DialogInterface.OnClickListener m_listenerYes = null;
	private DialogInterface.OnClickListener m_listenerNo = null;
	private DialogInterface.OnClickListener m_listenerAdd = null;
	private DialogInterface.OnClickListener m_listenerPrev = null;

	ListView lvContentsCond = null;
	BottomDialogCondListAdapter bottomDialogCondListAdapter;

	LinearLayout llBack;
	Button btnOpenPrevView;

	public static String[] arrCondDetail = {"변수 설정", "미리보기", "복사", "삭제"};

	int m_nViewType= 0;
	String  strSelValue= "";

	private String[] mArrKeys; // 키 리스트
	private String[] mArrValues; // 값 리스트

	public ImageView ivBottomView;

	LinearLayout[] lys = new LinearLayout[12];	// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용

	//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 start
    ArrayList<Button> m_arButtons;
    ArrayList<CheckBox> m_arChkButtons;
	//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 end

	public DRBottomDialog(Context context, int ViewType) {
		super(context, context.getResources().getIdentifier("alert_layout_bottom", "style", context.getPackageName()));

		m_context = context;
		m_nViewType = ViewType;

		initDialog(context);
	}

	@SuppressWarnings("deprecation")
	private void initDialog(Context context) {

		//Container Layout Setting
		LayoutInflater factory = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_confirm_dialog = (LinearLayout) factory.inflate(m_context.getResources().getIdentifier("dr_bottom_dialog", "layout", m_context.getPackageName()), null);
//		m_confirm_dialog.setBackgroundColor(Color.parseColor("#f3f3f3"));
		setContentView(m_confirm_dialog);

//		Display display = CondCommonUtil.getActivity().getWindowManager().getDefaultDisplay();
		Display display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) start
		// Dialog 사이즈 조절 하기
		LayoutParams params = getWindow().getAttributes();
//		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
//		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//		{
//			//가로
//			params.width = (int)COMUtil.getPixel(320);
//		}
//		else
//		{
			//세로
			params.width = LayoutParams.MATCH_PARENT;
//		}
		//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) end

		if(m_nViewType == BOTTOMVIEW_TYPE_COMPOUND)
		{
			m_nViewType = BOTTOMVIEW_TYPE_LIST;
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 start
//			LayoutParams params = getWindow().getAttributes();
//			params.width = LayoutParams.MATCH_PARENT;

//			params.height = (int) CondCommonUtil.getPixel_H(84)+(int) CondCommonUtil.getPixel_H(126)+ (int) CondCommonUtil.getPixel_H(78)*3;
			params.height = (int) COMUtil.getPixel(92)+(int) COMUtil.getPixel(10)+ (int) COMUtil.getPixel(56)*3;
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 end
			getWindow().setAttributes((WindowManager.LayoutParams) params);
		}
		//2020.04.24 by JJH >> 차트 설정 팝업 UI 수정 start
		else if(m_nViewType == BOTTOMVIEW_TYPE_CHARTSETTING){
//			m_nViewType = BOTTOMVIEW_TYPE_LIST;
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 start
//			LayoutParams params = getWindow().getAttributes();
//			params.width = LayoutParams.MATCH_PARENT;
			params.height = (int)(COMUtil.g_nDisHeight * 0.86);
//			params.height = (int)(COMUtil.g_nDisHeight - COMUtil.getPixel(100));
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 end
			getWindow().setAttributes((WindowManager.LayoutParams) params);
		}
		//2020.04.24 by JJH >> 차트 설정 팝업 UI 수정 end
		else {
			// Dialog 사이즈 조절 하기
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 start
//			LayoutParams params = getWindow().getAttributes();
//			params.width = LayoutParams.MATCH_PARENT;
			//2020.05.08 by JJH >> 드롭박스 리스트 UI 수정 end
			params.height = LayoutParams.WRAP_CONTENT;
			if(params.height >= size.y/3 * 2)
				params.height = size.y/3 * 2;

			getWindow().setAttributes((WindowManager.LayoutParams) params);
		}
		getWindow().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);	//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트)

		int resId = m_context.getResources().getIdentifier("popup_header_bg", "id", m_context.getPackageName());
		ViewGroup llTitle = (ViewGroup) m_confirm_dialog.findViewById(resId);
		ViewGroup.LayoutParams layoutParam = llTitle.getLayoutParams();
		layoutParam.height = (int)COMUtil.getPixel(92);
		llTitle.setLayoutParams(layoutParam);

		// 팝업 헤더 글씨 색상 지정
		resId = m_context.getResources().getIdentifier("confirm_title", "id", m_context.getPackageName());
		m_tvConfirmTitle = (TextView) m_confirm_dialog.findViewById(resId);
		m_tvConfirmTitle.setTextColor(HEADER_TEXT_COLOR);
//		m_tvConfirmTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, HEADER_TEXT_SIZE);
//		CondCommonUtil.setTextSize(m_tvConfirmTitle, HEADER_TEXT_SIZE);
		// 컨텐츠 영역
		resId = m_context.getResources().getIdentifier("ll_content", "id", m_context.getPackageName());
		rlContent = (LinearLayout) m_confirm_dialog.findViewById(resId);

		// 메시지 박스 영역
//		resId = m_context.getResources().getIdentifier("alert_message", "id", m_context.getPackageName());
//		m_tvMessage = (TextView) m_confirm_dialog.findViewById(resId);

		resId = context.getResources().getIdentifier("iv_bottomLine", "id", context.getPackageName());
		ivBottomView = (ImageView) m_confirm_dialog.findViewById(resId);
		ivBottomView.setVisibility(View.GONE);

		//listview
		int layoutResId = m_context.getResources().getIdentifier("lv_sub_cond", "id", m_context.getPackageName());
		lvContentsCond = (ListView) m_confirm_dialog.findViewById(layoutResId);
		lvContentsCond.setVisibility(View.VISIBLE);

		if(m_nViewType == BOTTOMVIEW_TYPE_LIST_BUTTON)
			lvContentsCond.setVisibility(View.VISIBLE);

		if(m_nViewType == BOTTOMVIEW_TYPE_LIST ||m_nViewType == BOTTOMVIEW_TYPE_LIST_BUTTON || m_nViewType == BOTTOMVIEW_TYPE_CHECK_LIST || m_nViewType == BOTTOMVIEW_TYPE_LIST_DEL) {
			if(m_nViewType != BOTTOMVIEW_TYPE_LIST && m_nViewType != BOTTOMVIEW_TYPE_LIST_DEL)
//				bottom_bg.setVisibility(View.VISIBLE);
			lvContentsCond.setVisibility(View.VISIBLE);
//			m_tvMessage.setVisibility(View.GONE);
		}

		COMUtil.setGlobalFont((ViewGroup) m_confirm_dialog);	//2018.04.16 by LYH >> 메인 폰트 적용
	}

	public void setTitle(String strTitle) {
		m_tvConfirmTitle.setText(strTitle);
	}

	@Override
	public void onClick(View v) {
		int nId = v.getId();

		if (nId == m_nBtnYesId) {
			if (m_listenerYes != null)
				m_listenerYes.onClick(this, BUTTON_POSITIVE);
			else
				dismiss();
		} else if (nId == m_nBtnNoId) {
			if (m_listenerNo != null)
				m_listenerNo.onClick(this, BUTTON_NEGATIVE);
			else
				dismiss();
		} else if (nId == m_nBtnOkId) {
			if (m_listenerAdd != null)
				m_listenerAdd.onClick(this, BUTTON_NEUTRAL);
			else
				dismiss();
		}else if(nId == m_nBtnPrev)
		{
			if (m_listenerPrev != null)
				m_listenerPrev.onClick(this, BUTTON_NEUTRAL);
			else
				dismiss();
		}
	}
	
	public View getLayout() {
		return m_confirm_dialog; 
	}

	public void setCondListView(ArrayList<String> itemList) {
		bottomDialogCondListAdapter = new BottomDialogCondListAdapter(this.getContext(), itemList);
		lvContentsCond.setAdapter(bottomDialogCondListAdapter);

		//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) start
//		float height = (COMUtil.getPixel(56)+ itemList.size() * COMUtil.getPixel(52));
		float height = (COMUtil.getPixel(92) + COMUtil.getPixel(10) + itemList.size() * COMUtil.getPixel(56));
		//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) end

//		Display display = CondCommonUtil.getActivity().getWindowManager().getDefaultDisplay();
		Display display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) start
//		if(size.y/3 * 2 < height) {
		if(size.y - (int)COMUtil.getPixel(51) < height) {
			LayoutParams params = getWindow().getAttributes();
			Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
//			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//			{
//				//가로
//				params.width = (int)COMUtil.getPixel(320);
//			}
//			else
//			{
				//세로
				params.width = LayoutParams.MATCH_PARENT;
//			}

//			params.width = LayoutParams.MATCH_PARENT;
//			params.height = size.y / 3 * 2;
			params.height = size.y - (int)COMUtil.getPixel(51);
			//2020.05.08 by JJH >> 가로모드 작업 (드롭박스 리스트) end
			getWindow().setAttributes((WindowManager.LayoutParams) params);
		}
	}

	//2020.04.24 by JJH >> 차트 설정 팝업 UI 수정 start
	public void showChartSettingListPopup(final Context context){
		LayoutInflater factory = LayoutInflater.from(context);

		//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 start
        m_arButtons = new ArrayList<Button>();
        m_arChkButtons = new ArrayList<CheckBox>();
		//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 end

		int resId = context.getResources().getIdentifier("chart_setting_list", "layout", context.getPackageName());
		m_confirm_dialog = (LinearLayout) factory.inflate(
				resId, null);

		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)(COMUtil.g_nDisHeight-COMUtil.getPixel(100)));	//2014. 10. 14 popupwindow 배경 반투명색
		m_confirm_dialog.setLayoutParams(params);
		m_confirm_dialog.requestFocus();

		resId = m_context.getResources().getIdentifier("popup_header_bg", "id", m_context.getPackageName());
		ViewGroup llTitle = (ViewGroup) m_confirm_dialog.findViewById(resId);

		//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 start
		resId = context.getResources().getIdentifier("frameaSub", "id", context.getPackageName());
		final TextView tvResetChartSetting = (TextView) m_confirm_dialog.findViewById(resId);

		resId = context.getResources().getIdentifier("btn_candle", "id", context.getPackageName());
		final Button btnCandle = (Button) m_confirm_dialog.findViewById(resId);
        m_arButtons.add(btnCandle);
        btnCandle.setTag("20001");
		btnCandle.setSelected(true);
		resId = context.getResources().getIdentifier("btn_line", "id", context.getPackageName());
		final Button btnLine = (Button) m_confirm_dialog.findViewById(resId);
        m_arButtons.add(btnLine);
        btnLine.setTag("20003");
		resId = context.getResources().getIdentifier("btn_enable_tool", "id", context.getPackageName());
		final Button btnEnableTool = (Button) m_confirm_dialog.findViewById(resId);
        m_arButtons.add(btnEnableTool);
        btnEnableTool.setTag("60001");
		resId = context.getResources().getIdentifier("btn_disenable_tool", "id", context.getPackageName());
		final Button btnDisEnableTool = (Button) m_confirm_dialog.findViewById(resId);
        m_arButtons.add(btnDisEnableTool);
        btnDisEnableTool.setTag("60002");

		tvResetChartSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
				reSetOriginal();
			}
		});
		//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 end


		btnCandle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnCandle.setSelected(true);
				btnLine.setSelected(false);
				// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 start
//				if (btnCandle.isSelected()){
//					//차트 적용
//					COMUtil.setChartFromXML(v);
//				}
				// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 end
			}
		});

		btnLine.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnLine.setSelected(true);
				btnCandle.setSelected(false);
				// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 start
//				if (btnLine.isSelected()){
//					COMUtil.setChartFromXML(v);
//				}
				// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 end
			}
		});

		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 start
		String strGraphType = COMUtil._mainFrame.mainBase.baseP._chart.m_strCandleType;
		//선택 확인
		if (strGraphType != null && strGraphType.equals("캔들")) {
			btnCandle.setSelected(true);
			btnLine.setSelected(false);
		}else if (strGraphType != null && strGraphType.equals("라인")) {
			btnLine.setSelected(true);
			btnCandle.setSelected(false);
		}
		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 end

		boolean bIsShowToolBarOneq = COMUtil.isShowToolBarOneq();
		if(bIsShowToolBarOneq) {
			btnEnableTool.setSelected(true);
			btnDisEnableTool.setSelected(false);
		} else {
			btnEnableTool.setSelected(false);
			btnDisEnableTool.setSelected(true);
		}

		btnEnableTool.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnEnableTool.setSelected(true);
				btnDisEnableTool.setSelected(false);
			}
		});

		btnDisEnableTool.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				btnDisEnableTool.setSelected(true);
				btnEnableTool.setSelected(false);
			}
		});

		resId = context.getResources().getIdentifier("frameabtnfunction", "id", context.getPackageName());
		Button btnclose = (Button) m_confirm_dialog
				.findViewById(resId);

		btnclose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setApply();
				dismiss();
			}
		});

		int[] ids = { context.getResources().getIdentifier("public_checkbox01", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox02", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox03", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox04", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox05", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox06", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox07", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox08", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox09", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox10", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox11", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox12", "id", context.getPackageName())
		};

		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 start
		int[] idsText = { context.getResources().getIdentifier("title01", "id", context.getPackageName()),
				context.getResources().getIdentifier("title02", "id", context.getPackageName()),
				context.getResources().getIdentifier("title03", "id", context.getPackageName()),
				context.getResources().getIdentifier("title04", "id", context.getPackageName()),
				context.getResources().getIdentifier("title05", "id", context.getPackageName()),
				context.getResources().getIdentifier("title06", "id", context.getPackageName()),
				context.getResources().getIdentifier("title07", "id", context.getPackageName()),
				context.getResources().getIdentifier("title08", "id", context.getPackageName()),
				context.getResources().getIdentifier("title09", "id", context.getPackageName()),
				context.getResources().getIdentifier("title10", "id", context.getPackageName()),
				context.getResources().getIdentifier("title11", "id", context.getPackageName()),
				context.getResources().getIdentifier("title12", "id", context.getPackageName())
		};

		int[] ids1 = { context.getResources().getIdentifier("baselinear01", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear02", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear03", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear04", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear05", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear06", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear07", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear08", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear09", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baselinear12", "id", context.getPackageName())
		};

		int[] idsTag = {
				ChartUtil.STANDSCALE,
				ChartUtil.BOLLINGER,
				ChartUtil.PARABOLIC,
				ChartUtil.PAVERAGE,
				ChartUtil.VOLUME,
				ChartUtil.VAVERAGE,
				ChartUtil.MACD_OSC,
				ChartUtil.STOCH_SLW,
				30102,
				30100,
				30104,
				30105
		};

		//현재 추가되어있는 그래프 리스트를 확인하여 체크한다.
		Vector<String> graphList = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();
		for (int i = 0; i < ids.length; i++) {
			CheckBox btnFunc = (CheckBox) m_confirm_dialog.findViewById(ids[i]);
			TextView txt = (TextView) m_confirm_dialog.findViewById(idsText[i]);

			btnFunc.setTag(idsTag[i]);
            m_arChkButtons.add(btnFunc);

			btnFunc.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox chk = (CheckBox)v;
//					COMUtil.setJipyo(chk);
				}
			});

			//지표가 추가 된 상태인지 확인
			for (int nListIndex=0; nListIndex<graphList.size(); nListIndex++) {
				if (txt.getText().toString().equals(graphList.get(nListIndex))) {
					btnFunc.setChecked(true);
					break;
				}
			}
		}

		for (int i = 0; i < ids1.length; i++) {
			LinearLayout ly = (LinearLayout) m_confirm_dialog.findViewById(ids1[i]);
			final CheckBox btnFunc = (CheckBox) m_confirm_dialog.findViewById(ids[i]);

			btnFunc.setTag(idsTag[i]);
			lys[i] = ly;
			ly.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					btnFunc.setChecked(!btnFunc.isChecked());
//					COMUtil.setJipyo(btnFunc);
				}


			});
		}
		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 end

		setContentView(m_confirm_dialog);
		//2020.05.08 by JJH >> 차트 설정 팝업 UI 글로벌 폰트 적용
		COMUtil.setGlobalFont(m_confirm_dialog);
	}
	//2020.04.24 by JJH >> 차트 설정 팝업 UI 수정 end

	public void setCondListView(Map<String, Object> stringObjectMap, String strMapKey) {

		ArrayList<String> arrStrategyList = new ArrayList<String>();
		ArrayList<Object> arrStrategyGroup = (ArrayList<Object>) stringObjectMap.get(strMapKey);

		for (int nIndexForKey = 0; nIndexForKey < arrStrategyGroup.size(); nIndexForKey++) {
//			StrategyItem strategyItem = (StrategyItem)arrStrategyGroup.get(nIndexForKey);
//			if(!strategyItem.strTitle.equals(""))
				arrStrategyList.add(arrStrategyList.get(nIndexForKey));
		}

		if(arrStrategyList != null) {
			bottomDialogCondListAdapter = new BottomDialogCondListAdapter(this.getContext(), arrStrategyList);
			lvContentsCond.setAdapter(bottomDialogCondListAdapter);


			float height = (COMUtil.getPixel(56)+ arrStrategyList.size() * COMUtil.getPixel(52));

//			Display display = CondCommonUtil.getActivity().getWindowManager().getDefaultDisplay();
			Display display = COMUtil._chartMain.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);


			if(size.y/3 * 2 < height) {
				LayoutParams params = getWindow().getAttributes();
				params.width = LayoutParams.MATCH_PARENT;
				params.height = size.y / 3 * 2;
				getWindow().setAttributes((WindowManager.LayoutParams) params);
			}

		}
	}

	public void setLlContextView(Map<Object, Object> ctlMap) {

		if(ctlMap != null) {
			ArrayList<String> arrCond = new ArrayList<String>();
			for (int i = 0; i < arrCondDetail.length; i++)
				arrCond.add(arrCondDetail[i]);

//			CondItem item = (CondItem) ctlMap.get("item");
//			String strElement = ctlMap.get("LOGIC_ELEMENT").toString();
//			setTitle(strElement + ". " + item.title);

			bottomDialogCondListAdapter = new BottomDialogCondListAdapter(this.getContext(), arrCond, ctlMap);
			lvContentsCond.setAdapter(bottomDialogCondListAdapter);
		}
	}

	public void setKeys(String[] arrKeys, String[] arrValues) {

		if (arrKeys.length != arrValues.length) {
			throw new NegativeArraySizeException();
		}

		this.mArrKeys = arrKeys;
		this.mArrValues = arrValues;

		ArrayList<String> itemList = new ArrayList<String>();

		for (int i = 0; i < mArrValues.length; i++) {
			itemList.add(mArrValues[i]);
		}

		setCondListView(itemList);
	}

	public void resetList()
	{
		bottomDialogCondListAdapter.notifyDataSetChanged();
	}

	//listener 추가
	private OnClickBottomViewListItemListener m_listener;

	public interface OnClickBottomViewListItemListener {

		void onClick(View view, int index, String value);
	}

	public void setOnClickListItemListener(OnClickBottomViewListItemListener listener) {
		this.m_listener = listener;
	}

	static class ViewHolder {
		private LinearLayout llCondListView;
		private TextView tvCondTitle;
		private TextView tvSubExplain;
		private LinearLayout llCondDesc;
		private ImageView ivCondDesc;
		private LinearLayout llCheckItem;
		private CheckBox btnCheck;
		private LinearLayout llCondDelete;
		private ImageView ivCondDelete;
	}

	class BottomDialogCondListAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<String> mItemList = new ArrayList<String>();
        private Map<Object, Object> mHashMap = null;

		BottomDialogCondListAdapter(Context context, ArrayList<String> itemList, Map<Object, Object> ctlMap) {
			this.context = context;
			this.mItemList = itemList;
			this.mHashMap = ctlMap;
		}

		BottomDialogCondListAdapter(Context context, ArrayList<String> itemList) {
			this.context = context;
			this.mItemList = itemList;

			if(m_nViewType == BOTTOMVIEW_TYPE_CHECK_LIST)
				strSelValue = mItemList.get(0);
		}

		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;

			if (convertView == null) {

				LayoutInflater factory = LayoutInflater.from(context);
				int layoutResId = context.getResources().getIdentifier("dr_bottom_listview_item", "layout", context.getPackageName());
				convertView = (View) factory.inflate(layoutResId, null);

				holder = new ViewHolder();

				layoutResId = context.getResources().getIdentifier("ll_bottomlistview", "id", context.getPackageName());
				holder.llCondListView = (LinearLayout) convertView.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("tv_cond_title", "id", context.getPackageName());
				holder.tvCondTitle = (TextView) convertView.findViewById(layoutResId);
//				CondCommonUtil.setTextSize(holder.tvCondTitle, 16);

				layoutResId = context.getResources().getIdentifier("tv_cond_explain", "id", context.getPackageName());
				holder.tvSubExplain = (TextView) convertView.findViewById(layoutResId);
				holder.tvSubExplain.setVisibility(View.GONE);
//				CondCommonUtil.setTextSize(holder.tvSubExplain, 12);

				layoutResId = context.getResources().getIdentifier("ll_imge_item", "id", context.getPackageName());
				holder.llCondDesc = (LinearLayout) convertView.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("iv_cond_desc", "id", context.getPackageName());
				holder.ivCondDesc = (ImageView) convertView.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("ll_cond_delete", "id", context.getPackageName());
				holder.llCondDelete = (LinearLayout) convertView.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("iv_cond_delete", "id", context.getPackageName());
				holder.ivCondDelete = (ImageView) convertView.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("ll_check_item", "id", context.getPackageName());
				holder.llCheckItem = (LinearLayout) convertView.findViewById(layoutResId);
				holder.llCheckItem.setVisibility(View.GONE);

				layoutResId = context.getResources().getIdentifier("chk_cond_item", "id", context.getPackageName());
				holder.btnCheck = (CheckBox) convertView.findViewById(layoutResId);

				convertView.setTag(holder);

				COMUtil.setGlobalFont((ViewGroup) convertView);	//2018.04.16 by LYH >> 메인 폰트 적용

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final String stritem = mItemList.get(position);

			if(m_nViewType == BOTTOMVIEW_TYPE_LIST_DEL)
			{
				holder.llCondDesc.setVisibility(View.GONE);
				holder.llCondDelete.setVisibility(View.VISIBLE);
				holder.llCondDelete.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						m_listener.onClick(v, position, "삭제");
					}
				});

				holder.tvCondTitle.setText(stritem);

				int resId = m_context.getResources().getIdentifier("cond_btn_list_del_n", "drawable", m_context.getPackageName());
//				holder.ivCondDesc.setBackgroundResource(resId);

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(72));
				holder.llCondListView.setLayoutParams(layoutParams);

				if (mItemList != null) {
					holder.tvCondTitle.setTag(stritem);
				}
				convertView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						m_listener.onClick(v, position, stritem);
					}
				});
			}
			else if(m_nViewType == BOTTOMVIEW_TYPE_CHECK_LIST)
			{
				holder.llCondDesc.setVisibility(View.GONE);
				holder.llCheckItem.setVisibility(View.VISIBLE);

				holder.btnCheck.setTag(stritem);
//				holder.tvCondTitle.setText(CondCommonUtil.getPeriodTitle(stritem));
//
//				int crTitleColor =Color.parseColor("#ffaa1a");
//				if (CondCommonUtil.isTX)
//				{
//					crTitleColor =Color.parseColor("#00a383");
//				}

				if(holder.btnCheck != null)
				{
					if(strSelValue.equals(stritem))
					{
						holder.btnCheck.setVisibility(View.VISIBLE);
						holder.btnCheck.setChecked(true);
//						holder.tvCondTitle.setTextColor(crTitleColor);
					}
					else
					{
						holder.btnCheck.setVisibility(View.GONE);
						holder.btnCheck.setChecked(false);
						holder.tvCondTitle.setTextColor(Color.parseColor("#666666"));
					}

					holder.btnCheck.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							CheckBox chk = (CheckBox)view;
							addCheckedList(chk.isChecked(), chk);
							m_listener.onClick(view, position, stritem);
						}
					});

					convertView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							holder.btnCheck.performClick();
						}
					});

				}
			}
			else {

				holder.tvCondTitle.setText(stritem);

				if(strSelValue.equals(stritem))
				{
					holder.llCondListView.setBackgroundColor(Color.rgb(249, 249, 250));
				}
				else
				{
					holder.llCondListView.setBackgroundColor(Color.rgb(255, 255, 255));
				}

				if (mHashMap != null && stritem.equals("변수 설정") && convertView.getLayoutParams() != null) {
					holder.tvSubExplain.setText((String) mHashMap.get("condTitle"));
					holder.tvSubExplain.setVisibility(View.VISIBLE);

					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(126));
					holder.llCondListView.setLayoutParams(layoutParams);
				} else {
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(56));
					holder.llCondListView.setLayoutParams(layoutParams);
				}


				if (mItemList != null) {
					holder.tvCondTitle.setTag(stritem);
				}
				convertView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						m_listener.onClick(v, position, stritem);
					}
				});
			}

			return convertView;
		}
	}

	public void addCheckedList(boolean isChecked, CompoundButton buttonView)
	{
		if (isChecked) {
			strSelValue =  (String) buttonView.getTag();
		}
		bottomDialogCondListAdapter.notifyDataSetChanged();
	}

	public void resetStrSelValue(String strValue)
	{
		strSelValue = strValue;

		bottomDialogCondListAdapter.notifyDataSetChanged();
	}

	private OnClickBottomViewAddNewGroupListener m_BottomViewlistener;

	public interface OnClickBottomViewAddNewGroupListener {

		void onClick(String value);
	}

	public void setOnClickAddNewGroup(OnClickBottomViewAddNewGroupListener listener) {
		this.m_BottomViewlistener = listener;
	}

	//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 start
	public void reSetOriginal() {

	    for (int i=0; i<m_arButtons.size(); i++){
            Button btnChartBtn = (Button) m_arButtons.get(i);

            if (btnChartBtn.getTag().equals("20001")){
				btnChartBtn.setSelected(true);
				COMUtil.setChartFromXML(btnChartBtn);
			} else if (btnChartBtn.getTag().equals("60002")){
				btnChartBtn.setSelected(true);
			} else{
				btnChartBtn.setSelected(false);
			}
        }

        for (int i=0; i<m_arChkButtons.size(); i++){
            CheckBox btnChkBtn = (CheckBox) m_arChkButtons.get(i);

            if (btnChkBtn.getTag().equals(ChartUtil.PAVERAGE) || btnChkBtn.getTag().equals(ChartUtil.VOLUME) || btnChkBtn.getTag().equals(ChartUtil.VAVERAGE)) {
                btnChkBtn.setChecked(true);
            }else{
                btnChkBtn.setChecked(false);
            }

            COMUtil.setJipyo(btnChkBtn);
        }
	}
	//2020.05.26 by JJH >> 원큐 주식 차트 설정 UI 이벤트 수정 end
	public void setApply() {
		for(int i=0; i<m_arButtons.size(); i++) {
			Button btn = m_arButtons.get(i);

			if(btn.getTag().equals("20001") || btn.getTag().equals("20003")) {
				if (btn.isSelected())
					COMUtil.setChartFromXML(btn);
			} else if(btn.getTag().equals("60001")) {
				if (btn.isSelected())
					COMUtil._mainFrame.setShowToolBarOneq(true);
			} else if(btn.getTag().equals("60002")) {
				if (btn.isSelected())
					COMUtil._mainFrame.setShowToolBarOneq(false);
			} else {

			}

		}
		for(int i=0; i<m_arChkButtons.size(); i++) {
			CheckBox chk = m_arChkButtons.get(i);

			COMUtil.setJipyo(chk);
		}
		COMUtil.saveLastState("combChartSetting");
	}
}
