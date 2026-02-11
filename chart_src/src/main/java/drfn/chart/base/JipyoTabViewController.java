package drfn.chart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.block.Block;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.DRKoreanChar;

@SuppressWarnings("unused")
public class JipyoTabViewController extends View implements OnClickListener {
	//====================
	// Constant
	//====================
	final public String FILTER_MARK_COLOR = "#488be2";
	final public String JIPYO_LIST_ITEM_TEXT_COLOR = "#666666";
	final public String JIPYO_LIST_DUPLICATED_ITEM_TEXT_COLOR = "#666666";
	final public String JIPYO_LIST_DIVIDER_COLOR = "#eeeeee";
	final public String JIPYO_LIST_GROUP_DIVIDER_COLOR = "#cccccc";
	final private int MAXIMUM_SELECTED_JIPYO_COUNT = 30;
	
	//====================
	// Member Variable
	//====================
	private Context m_context;
	private RelativeLayout m_layout;

	//====================
	// View
	//====================
	LinearLayout jipyoTabView = null;

	//====================
	// Component
	//====================
	Button btnTotal;
	Button btnChartType;
	Button btnOverlay;
	Button btnIndicator;
	Button btnSignal;
	Button btnFavorite;
	Button btnOther;
	Button btnClear;	//2017.08.07 by PJM 지표검색창 clear 버튼 추가.
	EditText edFilter;
	LinearLayout llListview;
	ListView lvJipyoList;
	ExpandableListView lvExpandableList;
	ExpandableListView lvExpandableListSignal;
	
	//====================
	// Enumeration
	//====================
	public enum TabId {
		TotalTabId, ChartTabId, OverlayTabId, IndicatorTabId, SignalTabId, FavoriteTabId, MarketTabId,
	};

	private enum JipyoType {
		ChartJipyoType, OverlayJipyoType, IndicatorJipyoType, SignalJipyoType, FavoriteJipyoType, marketJipyoType,
	};

	private enum JipyoSubType {
		NoneSubType, VolumeSubType, TrendSubType, VolatilitySubType, MarketStrengthType, MarketSubType, EtcSubType, SignalSubType, CandleSubType, 
		FavoriteSubType, ChartSubType, OverlaySubType, DerivationType,
	};

	//2017.08.08 by LYH >> mini 지표 설정 추가.
	public enum TabType {
		NormalType, MiniType,
	}
	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	//====================
	// Variable
	//====================
	TabId currentTabId;
	boolean bIsFiltered = false;
	
	//2017. 8. 29 by hyh - 지표 추가 개수 제한 처리
	int nSelectedJipyoCount; //선택 된 지표 개수

	//====================
	// Jipyo List
	//====================
	@SuppressWarnings("serial")
	//private class JipyoItem extends HashMap<String, Object> {};
	public class JipyoItem extends HashMap<String, Object> {};

	private ArrayList<JipyoItem> totalJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> chartTypeJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> overlayJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> indicatorJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> signalJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> favoriteJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<JipyoItem> marketJipyoList = new ArrayList<JipyoItem>();

	//JipyoListView에 출력 할 지표 리스트
	//private ArrayList<JipyoItem> currentJipyoList = new ArrayList<JipyoItem>();
	public ArrayList<JipyoItem> currentJipyoList = new ArrayList<JipyoItem>();

	//Filtering에 사용 할 리스트, 지표명 리스트(색상 적용)
	private ArrayList<JipyoItem> filteredJipyoList = new ArrayList<JipyoItem>();
	private ArrayList<String> arrFilteredNames = new ArrayList<String>();
	
	//Expandable List
	private ArrayList<JipyoSubType> indicatorGroupList = new ArrayList<JipyoSubType>();
	private ArrayList<ArrayList<JipyoItem>> indicatorChildList = new ArrayList<ArrayList<JipyoItem>>();
	private ArrayList<JipyoSubType> signalGroupList = new ArrayList<JipyoSubType>();
	private ArrayList<ArrayList<JipyoItem>> signalChildList = new ArrayList<ArrayList<JipyoItem>>();

	//ExpandableListView에 출력 할 리스트
	private ArrayList<JipyoSubType> currentGroupList = new ArrayList<JipyoSubType>();
	private ArrayList<ArrayList<JipyoItem>> currentChildList = new ArrayList<ArrayList<JipyoItem>>();
	
	//2017.08.08 by LYH >> mini 지표 설정 추가.
	private ArrayList<JipyoSubType> totalGroupList = new ArrayList<JipyoSubType>();
	private ArrayList<ArrayList<JipyoItem>> totalChildList = new ArrayList<ArrayList<JipyoItem>>();
	private TabType m_nTabType = TabType.NormalType;
	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	
	//====================
	// Adapter
	//====================
	private JipyoListAdapter jipyoListAdapter;
	private ExpandableListAdapter expandableListAdapter;
	private ExpandableListAdapter expandableListAdapterSiganl;
	
	public JipyoTabViewController(Context context, ViewGroup targetLayout, RelativeLayout parentLayout) {
		super(context);

		this.m_context = context;
		this.m_layout = parentLayout;

		init(targetLayout);
	}
	
	//2017.08.08 by LYH >> mini 지표 설정 추가.
	public JipyoTabViewController(Context context, ViewGroup targetLayout, RelativeLayout parentLayout, TabType nType) {
		super(context);

		this.m_context = context;
		this.m_layout = parentLayout;
		
		m_nTabType = nType;	//2017.08.08 by LYH >> mini 지표 설정 추가.
		
		init(targetLayout);
	}
	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	
	@SuppressLint("NewApi")
	private void init(ViewGroup targetLayout)
	{
		//====================
		// View 추가
		//====================
		int resId = m_context.getResources().getIdentifier("jipyo_tab_view", "layout", m_context.getPackageName());

		LayoutInflater factory = LayoutInflater.from(m_context);
		jipyoTabView = (LinearLayout) factory.inflate(resId, null);

		targetLayout.addView(jipyoTabView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		//====================
		// 컴포넌트 초기화
		//====================
		resId = m_context.getResources().getIdentifier("btn_total", "id", m_context.getPackageName());
		btnTotal = (Button) jipyoTabView.findViewById(resId);
		btnTotal.setTag(TabId.TotalTabId);
		btnTotal.setOnClickListener(this);

		resId = m_context.getResources().getIdentifier("btn_charttype", "id", m_context.getPackageName());
		btnChartType = (Button) jipyoTabView.findViewById(resId);
		btnChartType.setTag(TabId.ChartTabId);
		btnChartType.setOnClickListener(this);
		
		resId = m_context.getResources().getIdentifier("btn_overlay", "id", m_context.getPackageName());
		btnOverlay = (Button) jipyoTabView.findViewById(resId);
		btnOverlay.setTag(TabId.OverlayTabId);
		btnOverlay.setOnClickListener(this);
		
		resId = m_context.getResources().getIdentifier("btn_indicator", "id", m_context.getPackageName());
		btnIndicator = (Button) jipyoTabView.findViewById(resId);
		btnIndicator.setTag(TabId.IndicatorTabId);
		btnIndicator.setOnClickListener(this);
		
		resId = m_context.getResources().getIdentifier("btn_signal", "id", m_context.getPackageName());
		btnSignal = (Button) jipyoTabView.findViewById(resId);
		btnSignal.setTag(TabId.SignalTabId);
		btnSignal.setOnClickListener(this);
		
		resId = m_context.getResources().getIdentifier("btn_favorite", "id", m_context.getPackageName());
		btnFavorite = (Button) jipyoTabView.findViewById(resId);
		btnFavorite.setTag(TabId.FavoriteTabId);
		btnFavorite.setOnClickListener(this);

		resId = m_context.getResources().getIdentifier("btn_other", "id", m_context.getPackageName());
		btnOther = (Button) jipyoTabView.findViewById(resId);
		btnOther.setTag(TabId.MarketTabId);
		btnOther.setOnClickListener(this);

		//2017.08.07 by PJM 지표검색창 clear 버튼 추가. >>
		resId = m_context.getResources().getIdentifier("btn_clear", "id", m_context.getPackageName());
		btnClear = (Button) jipyoTabView.findViewById(resId);
		btnClear.setVisibility(INVISIBLE);
		//2017.08.07 by PJM 지표검색창 clear 버튼 추가. <<
		resId = m_context.getResources().getIdentifier("ed_filter", "id", m_context.getPackageName());
		edFilter = (EditText) jipyoTabView.findViewById(resId);
		edFilter.setSingleLine(true);
		//2017.08.07 by PJM Done 버튼 터치 시 키보드 제거 >>
		edFilter.setImeOptions(EditorInfo.IME_ACTION_DONE);
		edFilter.setOnTouchListener(
    			new View.OnTouchListener() {
    				public boolean onTouch(View v, MotionEvent event) {
    					if (currentTabId != TabId.TotalTabId)
    						changeTab(TabId.TotalTabId);
    					return false;
    				}
    			}
            );
		//2017.08.07 by PJM Done 버튼 터치 시 키보드 제거 <<
		edFilter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//Log.d("Choseong", "onTextChanged");

				if (s.length() > 0) {
					if (currentTabId != TabId.TotalTabId) {
						changeTab(TabId.TotalTabId);
					}

					jipyoListAdapter.getFilter().filter(s);
					//2017.08.07 by PJM 지표검색창 clear 버튼 추가. >>
					btnClear.setVisibility(VISIBLE);
					btnClear.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							edFilter.setText("");
							btnClear.setVisibility(INVISIBLE);
						}
					});
					//2017.08.07 by PJM 지표검색창 clear 버튼 추가. <<
				}
				else {
					btnClear.setVisibility(INVISIBLE);
					bIsFiltered = false;
					currentJipyoList = totalJipyoList;
					jipyoListAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				//Log.d("Choseong", "beforeTextChanged");
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				//Log.d("Choseong", "afterTextChanged");
			}
		});

		resId = m_context.getResources().getIdentifier("ll_listview", "id", m_context.getPackageName());
		llListview = (LinearLayout) jipyoTabView.findViewById(resId);

		resId = m_context.getResources().getIdentifier("lv_jipyolist", "id", m_context.getPackageName());
		lvJipyoList = (ListView) jipyoTabView.findViewById(resId);
		
		resId = m_context.getResources().getIdentifier("lv_expandable", "id", m_context.getPackageName());
		lvExpandableList = (ExpandableListView) jipyoTabView.findViewById(resId);

		resId = m_context.getResources().getIdentifier("lv_expandable_signal", "id", m_context.getPackageName());
		lvExpandableListSignal = (ExpandableListView) jipyoTabView.findViewById(resId);

	    //============================
	    // JipyoItem Init
	    //============================
		initJipyoList();
		initJipyoListView();
		
	    //============================
	    // Tab Buttons Init
	    //============================
		if(m_nTabType==TabType.MiniType)	//2017.08.08 by LYH >> mini 지표 설정 추가.
		{
			//resId = m_context.getResources().getIdentifier("ll_tab_area", "id", m_context.getPackageName());
			resId = m_context.getResources().getIdentifier("ll_tab_scroll", "id", m_context.getPackageName());
			ViewGroup llTab = (ViewGroup) jipyoTabView.findViewById(resId);
			llTab.setVisibility(View.GONE);

			resId = m_context.getResources().getIdentifier("ll_listview_line", "id", m_context.getPackageName());
			ImageView llLine = (ImageView) jipyoTabView.findViewById(resId);
			llLine.setVisibility(View.GONE);


			resId = m_context.getResources().getIdentifier("ll_filter", "id", m_context.getPackageName());
			ViewGroup llFilter = (ViewGroup) jipyoTabView.findViewById(resId);
			llFilter.setVisibility(View.GONE);

			lvJipyoList.setVisibility(View.GONE);
			lvExpandableList.setVisibility(View.VISIBLE);

			currentJipyoList = totalJipyoList;
			currentGroupList = totalGroupList;
			currentChildList = totalChildList;
			//expandableListAdapter.notifyDataSetChanged();
			lvExpandableList.expandGroup(0, false);
		}
		else{
			changeTab(TabId.TotalTabId);
		}
	}

	@Override
	public void onClick(View v) {
		TabId tabId = (TabId)v.getTag();
		hideKeyPad_(v);		//2017.08.07 by PJM 키패드 제거.
		changeTab(tabId);
	}

	public void changeTab(TabId tabId) {
	    //============================
	    // Toggle Tab Button
	    //============================
		btnTotal.setSelected(false);
		btnTotal.setTextColor(Color.rgb(153, 153, 153));
		btnChartType.setSelected(false);
		btnChartType.setTextColor(Color.rgb(153, 153, 153));
		btnOverlay.setSelected(false);
		btnOverlay.setTextColor(Color.rgb(153, 153, 153));
		btnIndicator.setSelected(false);
		btnIndicator.setTextColor(Color.rgb(153, 153, 153));
		btnSignal.setSelected(false);
		btnSignal.setTextColor(Color.rgb(153, 153, 153));
		btnFavorite.setSelected(false);
		btnFavorite.setTextColor(Color.rgb(153, 153, 153));
		btnOther.setSelected(false);
		btnOther.setTextColor(Color.rgb(153, 153, 153));

		btnTotal.setTypeface(COMUtil.typefaceMid);
		btnChartType.setTypeface(COMUtil.typefaceMid);
		btnOverlay.setTypeface(COMUtil.typefaceMid);
		btnIndicator.setTypeface(COMUtil.typefaceMid);
		btnSignal.setTypeface(COMUtil.typefaceMid);
		btnFavorite.setTypeface(COMUtil.typefaceMid);
		btnOther.setTypeface(COMUtil.typefaceMid);
		
		Button btnSelected = (Button)jipyoTabView.findViewWithTag(tabId);
		btnSelected.setSelected(true);
		btnSelected.setTextColor(Color.rgb(17, 17, 17));
		btnSelected.setTypeface(COMUtil.typefaceBold);
		
	    //============================
	    // ListView 갱신
	    //============================
	    //필터링 초기화
	    if (tabId != TabId.TotalTabId) {
	        hideKeyboard();
	        edFilter.setText("");
	        bIsFiltered = false;
	    }		
		
		switch (tabId) {
			case TotalTabId:
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = totalJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
			case ChartTabId:
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = chartTypeJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
			case OverlayTabId:
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = overlayJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
			case IndicatorTabId:
				//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw >>
				lvJipyoList.setVisibility(View.GONE);
				lvExpandableList.setVisibility(View.VISIBLE);
				lvExpandableListSignal.setVisibility(View.GONE);
//				lvJipyoList.setVisibility(View.VISIBLE);
//				lvExpandableList.setVisibility(View.GONE);
				//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw <<

				currentJipyoList = indicatorJipyoList;
				currentGroupList = indicatorGroupList;
				currentChildList = indicatorChildList;
				expandableListAdapter.notifyDataSetChanged();
				break;
			case SignalTabId:
				//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw >>
				lvJipyoList.setVisibility(View.GONE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.VISIBLE);
//				lvJipyoList.setVisibility(View.VISIBLE);
//				lvExpandableList.setVisibility(View.GONE);
				//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw <<

				currentJipyoList = signalJipyoList;
				currentGroupList = signalGroupList;
				currentChildList = signalChildList;
				expandableListAdapterSiganl.notifyDataSetChanged();
				break;
			case FavoriteTabId:
				addFavoriteList();	//2017.08.08 by LYH >> 관심 지표 기능 처리.
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = favoriteJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
			case MarketTabId:
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = marketJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
			default:
				lvJipyoList.setVisibility(View.VISIBLE);
				lvExpandableList.setVisibility(View.GONE);
				lvExpandableListSignal.setVisibility(View.GONE);

				currentJipyoList = totalJipyoList;
				jipyoListAdapter.notifyDataSetChanged();
				break;
		}
		
	    //============================
	    // 현재 TabId 갱신
	    //============================
		currentTabId = tabId;
	}
	
	@SuppressWarnings("unchecked")
	private void initJipyoList() {
	    //===========================
	    // 차트유형
	    //===========================
		String[] arrChartTypeNames = {
			COMUtil.CANDLE_TYPE_CANDLE,
			COMUtil.CANDLE_TYPE_BAR,
			COMUtil.CANDLE_TYPE_BAR_OHLC,
			COMUtil.CANDLE_TYPE_LINE,
			COMUtil.CANDLE_TYPE_FLOW,
			COMUtil.CANDLE_TYPE_STAIR,
			COMUtil.CANDLE_TYPE_PF,
			COMUtil.CANDLE_TYPE_THIRD,
			COMUtil.CANDLE_TYPE_SWING,
			COMUtil.CANDLE_TYPE_RENKO,
			COMUtil.CANDLE_TYPE_KAGI,
			COMUtil.CANDLE_TYPE_RANGE_LINE,
			COMUtil.CANDLE_TYPE_REVERSECLOCK,
			COMUtil.CANDLE_TYPE_VARAINCE,
			COMUtil.CANDLE_TYPE_HEIKIN_ASHI,
			//2020.07.06 by LYH >> 캔들볼륨 >>
			COMUtil.CANDLE_TYPE_CANDLE_VOLUME,
			COMUtil.CANDLE_TYPE_EQUI_VOLUME,
			//2020.07.06 by LYH >> 캔들볼륨 <<
		};
		
		Vector<Hashtable<String, String>> clTag = COMUtil.getChartListTag();
		String[] arrChartTypeTags = {
			clTag.get(0).get("tag"),
			clTag.get(1).get("tag"),
			clTag.get(10).get("tag"),
			clTag.get(2).get("tag"),
			clTag.get(14).get("tag"),
			clTag.get(18).get("tag"),	//계단
			clTag.get(6).get("tag"),
			clTag.get(5).get("tag"),
			clTag.get(7).get("tag"),
			clTag.get(8).get("tag"),
			clTag.get(11).get("tag"),
			clTag.get(12).get("tag"),
			clTag.get(9).get("tag"),
			clTag.get(13).get("tag"),
			clTag.get(15).get("tag"),
			//2020.07.06 by LYH >> 캔들볼륨 >>
			clTag.get(16).get("tag"),
			clTag.get(17).get("tag"),
			//2020.07.06 by LYH >> 캔들볼륨 <<
		};

		//현재 선택 된 차트유형 획득  
		String strGraphType = null;
		boolean bIsStand = COMUtil._mainFrame.mainBase.baseP._chart._cvm.isStandGraph();
		if(bIsStand)
		{
			int btype = Block.STAND_BLOCK;
			Block stand = COMUtil._mainFrame.mainBase.baseP._chart.getChartBlockByType(btype);
			if(stand != null)
			{
				strGraphType = stand.getTitle();
			}
		}
		else
		{
			strGraphType = COMUtil._mainFrame.mainBase.baseP._chart.m_strCandleType;
		}
				
		//지표 리스트 추가
		if(m_nTabType== TabType.MiniType) {
			for (int nIndex = 0; nIndex < arrChartTypeNames.length; nIndex++) {
				String name = arrChartTypeNames[nIndex];
				String tag = arrChartTypeTags[nIndex];
				boolean isSelected = false;
				boolean isFavorite = false;
				boolean useConfig = false;
				//2017.08.08 by LYH >> mini 지표 설정 추가.
				boolean canBeFavorite = false;
				if (m_nTabType == TabType.MiniType)
					canBeFavorite = false;
				//2017.08.08 by LYH >> mini 지표 설정 추가. end
				//선택 확인
				if (strGraphType != null && strGraphType.equals(name)) {
					isSelected = true;
				}

				if (name.equals("캔들") || name.equals("역시계곡선")) {
					useConfig = true;
				}
				//아이템 추가
				JipyoItem jipyoItem = getJipyoItem(name, tag,
						JipyoType.ChartJipyoType, JipyoSubType.NoneSubType,
						isSelected, false, isFavorite, false, canBeFavorite, useConfig);

				chartTypeJipyoList.add(jipyoItem);
				totalJipyoList.add(jipyoItem);
			}
		}
		
	    //===========================
	    // 오버레이, 보조지표, 신호, 관심
	    //===========================
	    //중복지표 처리
		Vector<Hashtable<String, String>> jipyoItems = (Vector<Hashtable<String, String>>) COMUtil.getJipyoMenu().clone();
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();

		for (int nIndex = 0; nIndex < addItems.size(); nIndex++) {
			Hashtable<String, String> addItem = (Hashtable<String, String>) addItems.get(nIndex);
			if (addItem == null || (addItem != null && addItem.size() == 0)) {
				continue;
			}
			int addTag = Integer.parseInt((String) addItem.get("tag"));

			int index = 0;
			for (int k = 0; k < jipyoItems.size(); k++) {
				Hashtable<String, String> item = (Hashtable<String, String>) jipyoItems.get(k);
				int itemTag = Integer.parseInt((String) item.get("tag"));
				if (addTag / 100 == itemTag || addTag / 100 == itemTag / 100) {
					index = k;
				}
			}
			jipyoItems.add(index + 1, addItem);
		}
		
		//현재 추가되어있는 그래프 리스트를 확인하여 체크한다.
		Vector<String> graphList = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();

		//지표 리스트 추가
	    for (int nIndex=0; nIndex<jipyoItems.size(); nIndex++) {
	    	Hashtable<String, String> item = jipyoItems.get(nIndex);
	    	
	        String name = item.get("name");
	        String tag = item.get("tag");
	        JipyoType jipyoType = JipyoType.IndicatorJipyoType;
	        JipyoSubType jipyoSubType = JipyoSubType.EtcSubType;
	        boolean isSelected = false;
	        boolean isDuplicated = false;
	        boolean isFavorite = false;
	        boolean canBeDuplicated = true;
	        boolean canBeFavorite = true;
	        boolean useConfig = true;
	        
	      	//2017.08.08 by LYH >> mini 지표 설정 추가.
	        if(m_nTabType == TabType.MiniType)
	        {
		        canBeFavorite = false;
		        canBeDuplicated = false;
	        }
	      	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	        //오버레이, 신호/캔들 확인
	        String detailType = item.get("detailType");
	        
	        if (detailType.equals("overlay")) {
	            jipyoType = JipyoType.OverlayJipyoType;
	            jipyoSubType = JipyoSubType.NoneSubType;
	            
	            //오버레이는 중복추가 불가
	            canBeDuplicated = false;
	        }
	        else if (detailType.equals("strategy")) {
	            jipyoType = JipyoType.SignalJipyoType;
	            
	            //시그널은 중복추가 불가
	            canBeDuplicated = false;
	        }
	        
	        //이름이 거래량 or 미결제약정으로 시작하는 지표는 중복추가 불가
	        if (name.startsWith("거래량") || name.startsWith("미결제약정") || name.equals("주가이동평균") || name.startsWith("거래대금")) {
	            canBeDuplicated = false;
	        }
	        
	        //보조지표, 신호/캔들 subType 확인
	        String strSubType = item.get("subType");
	        
	        if (strSubType != null) {
		        for (JipyoSubType subType : JipyoSubType.values()) {
		        	if (strSubType.equals(getSubTypeText(subType))) {
		        		jipyoSubType = subType;
		        		break;
		        	}
		        }
	        }
	        
	        //시장지표는 중복추가 불가
	        if (jipyoSubType == JipyoSubType.MarketSubType) {
	            canBeDuplicated = false;
	        }
	        
	        //현재 지표가 중복인지 확인
	        int index = name.indexOf(COMUtil.JIPYO_ADD_REMARK);
	        if(index>0) {
	        	isDuplicated = true;
	        	name = COMUtil.getAddJipyoTitle(name);

	        	//중복지표는 관심지표로 추가 불가
	            canBeFavorite = false;
	        }
	        	        
	        //지표가 추가 된 상태인지 확인
	        for (int nListIndex=0; nListIndex<graphList.size(); nListIndex++) {
	            if (item.get("name").equals(graphList.get(nListIndex))) {
	            	isSelected = true;
	                break;
	            }
	        }
	        
	        if (isSelected) {
				nSelectedJipyoCount++;
	        }

			//아이템 추가
			JipyoItem jipyoItem = getJipyoItem(name, tag, 
					jipyoType, jipyoSubType, 
					isSelected, isDuplicated, isFavorite, canBeDuplicated, canBeFavorite, useConfig);
			
	        if (jipyoType == JipyoType.IndicatorJipyoType) {
	        	indicatorJipyoList.add(jipyoItem);
	        }
	        else if (jipyoType == JipyoType.OverlayJipyoType) {
	        	overlayJipyoList.add(jipyoItem);
	        }
	        else if (jipyoType == JipyoType.SignalJipyoType) {
	        	signalJipyoList.add(jipyoItem);
	        }
	        
	        totalJipyoList.add(jipyoItem);
	    }
	    
	  	//2017.08.08 by LYH >> 관심 지표 기능 처리.
	    //=====================================
	    // 관심 List 생성
	    //=====================================
	    COMUtil.loadFavoriteIndicator();
	    addFavoriteList();
	  	//2017.08.08 by LYH >> 관심 지표 기능 처리. end
		
	    //=====================================
	    // 보조지표 Expandable List 생성
	    //=====================================
	    indicatorGroupList.add(JipyoSubType.VolumeSubType);
		indicatorGroupList.add(JipyoSubType.MarketSubType);
	    indicatorGroupList.add(JipyoSubType.TrendSubType);
	    indicatorGroupList.add(JipyoSubType.VolatilitySubType);
		indicatorGroupList.add(JipyoSubType.DerivationType);
	    indicatorGroupList.add(JipyoSubType.MarketStrengthType);
//		indicatorGroupList.add(JipyoSubType.EtcSubType);

	  	//2017.08.08 by LYH >> mini 지표 설정 추가.
	    int nTotalIndex = 0;
	    totalGroupList.add(JipyoSubType.FavoriteSubType);
	    totalGroupList.add(JipyoSubType.ChartSubType);
	    totalGroupList.add(JipyoSubType.OverlaySubType);
	    //2020.02.09 by LYH >> 미니차트
//	    totalGroupList.add(JipyoSubType.VolumeSubType);
//		totalGroupList.add(JipyoSubType.MarketSubType);
//	    totalGroupList.add(JipyoSubType.TrendSubType);
//	    totalGroupList.add(JipyoSubType.VolatilitySubType);
//		totalGroupList.add(JipyoSubType.DerivationType);
//	    totalGroupList.add(JipyoSubType.MarketStrengthType);
////	    totalGroupList.add(JipyoSubType.EtcSubType);
//	    totalGroupList.add(JipyoSubType.SignalSubType);
//		totalGroupList.add(JipyoSubType.CandleSubType);
	    
	    for (int nIndex=0; nIndex<totalGroupList.size(); nIndex++) {
	    	ArrayList<JipyoItem> totalChildArrayList =  new ArrayList<JipyoItem>();
	    	totalChildList.add(totalChildArrayList);
	    }
	    
	    //아이템 추가
	    ArrayList<JipyoItem> totalChildArrayList =  new ArrayList<JipyoItem>();
    	totalChildList.add(totalChildArrayList);
	    for (JipyoItem jipyoItem : favoriteJipyoList) {
	    	totalChildArrayList = totalChildList.get(nTotalIndex);
		    totalChildArrayList.add(jipyoItem);
	    }
	    nTotalIndex++;
	    
	    totalChildArrayList =  new ArrayList<JipyoItem>();
    	totalChildList.add(totalChildArrayList);
	    for (JipyoItem jipyoItem : chartTypeJipyoList) {
	    	totalChildArrayList = totalChildList.get(nTotalIndex);
		    totalChildArrayList.add(jipyoItem);
	    }
	    nTotalIndex++;
	    
	    totalChildArrayList =  new ArrayList<JipyoItem>();
    	totalChildList.add(totalChildArrayList);
	    for (JipyoItem jipyoItem : overlayJipyoList) {
	    	totalChildArrayList = totalChildList.get(2);
		    totalChildArrayList.add(jipyoItem);
	    }
	    nTotalIndex++;
	  	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	    //초기화
	    for (int nIndex=0; nIndex<indicatorGroupList.size(); nIndex++) {
	    	ArrayList<JipyoItem> indicatorChildArrayList =  new ArrayList<JipyoItem>();
	    	indicatorChildList.add(indicatorChildArrayList);
	    }
	    
	    //아이템 추가
	    for (JipyoItem jipyoItem : indicatorJipyoList) {
	    	JipyoSubType jipyoSubType = (JipyoSubType)jipyoItem.get("jipyoSubType");
	    	
	    	if (jipyoSubType == null) {
	    		jipyoSubType = JipyoSubType.EtcSubType;
	    	}
	    	
	    	int nChildListIndex = indicatorGroupList.indexOf(jipyoSubType);
	    	
	    	if (nChildListIndex >= 0) {
		    	ArrayList<JipyoItem> indicatorChildArrayList = indicatorChildList.get(nChildListIndex);
		    	indicatorChildArrayList.add(jipyoItem);
		    	//2017.08.08 by LYH >> mini 지표 설정 추가.
				if(nChildListIndex+nTotalIndex < totalChildList.size()) {
					totalChildArrayList = totalChildList.get(nChildListIndex + nTotalIndex);
					totalChildArrayList.add(jipyoItem);
				}
		    	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	    	}
	    }
	    nTotalIndex+=indicatorGroupList.size();	//2017.08.08 by LYH >> mini 지표 설정 추가.

	    //=====================================
	    // 신호/지표 Expandable List 생성
	    //=====================================
	    signalGroupList.add(JipyoSubType.SignalSubType);
	    signalGroupList.add(JipyoSubType.CandleSubType);
	    
	    //초기화
	    for (int nIndex=0; nIndex<signalGroupList.size(); nIndex++) {
	    	ArrayList<JipyoItem> signalChildArrayList =  new ArrayList<JipyoItem>();
	    	signalChildList.add(signalChildArrayList);
	    }
	    
	    //아이템 추가
	    for (JipyoItem jipyoItem : signalJipyoList) {
	    	JipyoSubType jipyoSubType = (JipyoSubType)jipyoItem.get("jipyoSubType");
	    	
	    	if (jipyoSubType == null) {
	    		jipyoSubType = JipyoSubType.CandleSubType;
	    	}
	    	
	    	int nChildListIndex = signalGroupList.indexOf(jipyoSubType);
	    	
	    	if (nChildListIndex >= 0) {
		    	ArrayList<JipyoItem> signalChildArrayList = signalChildList.get(nChildListIndex);
		    	signalChildArrayList.add(jipyoItem);
		    	//2017.08.08 by LYH >> mini 지표 설정 추가.
				if(nChildListIndex+nTotalIndex < totalChildList.size()) {
					totalChildArrayList = totalChildList.get(nChildListIndex + nTotalIndex);
					totalChildArrayList.add(jipyoItem);
				}
		    	//2017.08.08 by LYH >> mini 지표 설정 추가. end
	    	}
	    }
	}

	//2017.08.08 by LYH >> 관심 지표 기능 처리.
	private void addFavoriteList()
	{
		favoriteJipyoList.clear();
	    ArrayList<String> arrFavoriteTypeNames = COMUtil.getFavoriteIndicatorList();
		for (JipyoItem jipyoItem : totalJipyoList) {
	    	String name = jipyoItem.get("name").toString();

			for (int nIndex=0; nIndex<arrFavoriteTypeNames.size(); nIndex++) {
				String favoriteName = arrFavoriteTypeNames.get(nIndex);
	    	
		    	if (favoriteName.equals(name)) {
		    		jipyoItem.put("isFavorite", true);
			    	favoriteJipyoList.add(jipyoItem);
			    	break;
		    	}		    	
			}
	    }
	}
	//2017.08.08 by LYH >> 관심 지표 기능 처리. end

	private void initJipyoListView() {
		jipyoListAdapter = new JipyoListAdapter(m_context);
		lvJipyoList.setAdapter(jipyoListAdapter);
		lvJipyoList.setDivider(new ColorDrawable(Color.parseColor(JIPYO_LIST_DIVIDER_COLOR)));
		lvJipyoList.setDividerHeight(1);
		
		expandableListAdapter = new ExpandableListAdapter(m_context);
		lvExpandableList.setAdapter(expandableListAdapter);
		lvExpandableList.setDivider(new ColorDrawable(Color.parseColor(JIPYO_LIST_GROUP_DIVIDER_COLOR)));
		lvExpandableList.setChildDivider(new ColorDrawable(Color.parseColor(JIPYO_LIST_DIVIDER_COLOR)));
		lvExpandableList.setDividerHeight(1);

		expandableListAdapterSiganl = new ExpandableListAdapter(m_context);
		lvExpandableListSignal.setAdapter(expandableListAdapterSiganl);
		lvExpandableListSignal.setDivider(new ColorDrawable(Color.parseColor(JIPYO_LIST_GROUP_DIVIDER_COLOR)));
		lvExpandableListSignal.setChildDivider(new ColorDrawable(Color.parseColor(JIPYO_LIST_DIVIDER_COLOR)));
		lvExpandableListSignal.setDividerHeight(1);

		if(m_nTabType == TabType.MiniType)
		{
			lvJipyoList.setDividerHeight(0);
			lvExpandableList.setDividerHeight(0);
		}
		else {
			try {
				currentJipyoList = indicatorJipyoList;
				currentGroupList = indicatorGroupList;
				currentChildList = indicatorChildList;
				expandableListAdapter.notifyDataSetChanged();

				for (int i = 0; i < expandableListAdapter.getGroupCount(); i++)
					lvExpandableList.expandGroup(i);

				currentJipyoList = signalJipyoList;
				currentGroupList = signalGroupList;
				currentChildList = signalChildList;
				expandableListAdapterSiganl.notifyDataSetChanged();

				for (int i = 0; i < expandableListAdapterSiganl.getGroupCount(); i++)
					lvExpandableListSignal.expandGroup(i);
			} catch (Exception e) {

			}
		}
	}
	
	private JipyoItem getJipyoItem(String name, String tag,
                                   JipyoType jipyoType, JipyoSubType jipyoSubType,
                                   boolean isSelected, boolean isDuplicated, boolean isFavorite,
                                   boolean canBeDuplicated, boolean canBeFavorite, boolean useConfig) {
		
		JipyoItem jipyoItem = new JipyoItem();
		jipyoItem.put("name", name);
		jipyoItem.put("tag", tag);
		jipyoItem.put("jipyoType", jipyoType);
		jipyoItem.put("jipyoSubType", jipyoSubType);
		jipyoItem.put("isSelected", isSelected);
		jipyoItem.put("isDuplicated", isDuplicated);
		jipyoItem.put("isFavorite", isFavorite);
		jipyoItem.put("canBeDuplicated", canBeDuplicated);
		jipyoItem.put("canBeFavorite", canBeFavorite);
		jipyoItem.put("useConfig", useConfig);
		
		return jipyoItem;
	}
	
	private String getSubTypeText(JipyoSubType jipyoSubType) {
	    String rtnStr = "";
	    
	    switch (jipyoSubType) {
	        case VolumeSubType:
	            rtnStr = "volume";
	            break;
	            
	        case TrendSubType:
	            rtnStr = "trend";
	            break;
	            
	        case VolatilitySubType:
	            rtnStr = "volatility";
	            break;
	            
	        case MarketStrengthType:
	            rtnStr = "marketStrength";
	            break;
	            
	        case MarketSubType:
	            rtnStr = "market";
	            break;
	            
	        case EtcSubType:
	            rtnStr = "etc";
	            break;
	            
	        case SignalSubType:
	            rtnStr = "signal";
	            break;
	            
	        case CandleSubType:
	            rtnStr = "candle";
	            break;

			case DerivationType:
				rtnStr = "derivation";
				break;
	            
	        default:
	            rtnStr = "etc";
	            break;
	    }
	    
	    return rtnStr;
	}
	
	private String getSubTypeSectionTitle(JipyoSubType jipyoSubType) {
	    String rtnStr = "SubTitle";
	    
	    switch (jipyoSubType) {
	        case VolumeSubType:
	            rtnStr = "거래량지표";
	            break;
	            
	        case TrendSubType:
	            rtnStr = "추세지표";
	            break;
	            
	        case VolatilitySubType:
	            rtnStr = "변동성지표";
	            break;
	            
	        case MarketStrengthType:
	            rtnStr = "시장강도지표";
	            break;
	            
	        case MarketSubType:
	            rtnStr = "시장지표";
	            break;
	            
	        case EtcSubType:
	            rtnStr = "기타지표";
	            break;
	            
	        case SignalSubType:
	            rtnStr = "신호검색";
	            break;
	            
	        case CandleSubType:
	            rtnStr = "캔들 강세/약세";
	            break;

	        case FavoriteSubType:
	            rtnStr = "관심지표";
	            break;

	        case ChartSubType:
	            rtnStr = "차트유형";
	            break;

	        case OverlaySubType:
	            rtnStr = "오버레이";
	            break;

			case DerivationType:
				rtnStr = "2차파생지표";
				break;
	        default:
	            break;
	    }
	    
	    return rtnStr;
	}
	
	class JipyoListAdapter extends BaseAdapter implements Filterable {
		private Context context;

		JipyoListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return currentJipyoList.size();
		}

		@Override
		public Object getItem(int position) {
			return currentJipyoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final JipyoItem jipyoItem = currentJipyoList.get(position);		
			return getJipyoListRowView(position, convertView, parent, jipyoItem);
		}

		@SuppressLint("DefaultLocale")
		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {

					String strConstraint = edFilter.getText().toString();

					//Log.d("Choseong", "publishResults - strConstraint : " + strConstraint + " / constraint : " + constraint);

					//현재 입력된 내용과 같을 때에만 리스트에 적용한다.
					if (!strConstraint.equals(constraint)) {
						return;
					}

					Log.d("Choseong", "publishResults counts : " + results.count);

					ArrayList<Object> arrValues = (ArrayList<Object>) results.values;
					filteredJipyoList = (ArrayList<JipyoItem>) arrValues.get(0);
					arrFilteredNames = (ArrayList<String>) arrValues.get(1);

					currentJipyoList = filteredJipyoList;
					bIsFiltered = true;
					
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {

					FilterResults results = new FilterResults();

					ArrayList<Object> filteredJipyoList = new ArrayList<Object>();
					ArrayList<String> arrFilteredNames = new ArrayList<String>();
					
					constraint = constraint.toString().toLowerCase();

					Log.d("Choseong", "constraint : " + constraint);
					
					for (int nIndex=0; nIndex<totalJipyoList.size(); nIndex++) {
						JipyoItem jipyoItem = totalJipyoList.get(nIndex);
						String name = jipyoItem.get("name").toString();
						String strFilterText = constraint.toString().toLowerCase();
						
						KoreanString s = new KoreanString(name);

						//boolean bChoseongSearch = s.startsWith((String) constraint); //첫 글자부터 초성검색 매치되는 경우에만 반환 
						boolean bChoseongSearch = s.contains((String) constraint); //중간에 초성검색 매치되는 경우 검색 허용
						boolean bContainConstraint = name.toLowerCase().contains(constraint.toString());
						
						if (bChoseongSearch && !bContainConstraint) {
							String strMatchedText = s.getMatchedText();
	
							String strHtmlConstraint = "<font color='" + FILTER_MARK_COLOR + "'>" + strMatchedText + "</font>";
							name = name.replace(strMatchedText, strHtmlConstraint);
	
							arrFilteredNames.add(name);
							filteredJipyoList.add(jipyoItem);
						} else if (bContainConstraint) {
							name = insertHtmlFontTag(name, constraint.toString());
	
							arrFilteredNames.add(name);
							filteredJipyoList.add(jipyoItem);
						}
					}

					//리턴 할 객체 배열 생성
					ArrayList<Object> arrValues = new ArrayList<Object>();
					arrValues.add(filteredJipyoList);
					arrValues.add(arrFilteredNames);

					results.count = arrFilteredNames.size();
					results.values = arrValues;

					//Log.e("VALUES", results.values.toString());

					return results;
				}
			};

			return filter;
		}
	}
	
	class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context context;

		ExpandableListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return currentGroupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return currentChildList.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return currentGroupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return currentChildList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final GroupViewHolder holder;

			if (convertView == null) {

				LayoutInflater factory = LayoutInflater.from(context);
				int layoutResId;
				if (m_nTabType == TabType.MiniType) {
					layoutResId = context.getResources().getIdentifier("jipyo_mini_listview_group_item", "layout", context.getPackageName());
				}
				else
					layoutResId = context.getResources().getIdentifier("jipyo_tab_listview_group_item", "layout", context.getPackageName());
				convertView = (View) factory.inflate(layoutResId, parent, false);

				holder = new GroupViewHolder();

				layoutResId = context.getResources().getIdentifier("tv_title", "id", context.getPackageName());
				holder.tvTitle = (TextView) convertView.findViewById(layoutResId);
				
				layoutResId = context.getResources().getIdentifier("iv_group_indicator", "id", context.getPackageName());
				holder.ivGroupIndicator = (ImageView) convertView.findViewById(layoutResId);
				
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			
            //================================
            // Group Title TextView Setting
            //================================
			JipyoSubType currentJipyoSubType = (JipyoSubType)currentGroupList.get(groupPosition);
			String groupName = getSubTypeSectionTitle(currentJipyoSubType);
			holder.tvTitle.setText(groupName);

			//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw >>
//			2017. 8. 16 by hyh - 지표추가화면에서 선택된 항목이 있는 대메뉴에서는 볼드 처리 >>
			ArrayList<JipyoItem> jipyoList;
			boolean bIsContainSelectedJipyo = false;

            if(currentJipyoSubType == JipyoSubType.FavoriteSubType) {
                jipyoList = favoriteJipyoList;
            }
            else if(currentJipyoSubType == JipyoSubType.ChartSubType) {
                jipyoList = chartTypeJipyoList;
            }
            else if(currentJipyoSubType == JipyoSubType.OverlaySubType) {
                jipyoList = overlayJipyoList;
            }
            else if(currentJipyoSubType == JipyoSubType.VolumeSubType
            	 || currentJipyoSubType == JipyoSubType.TrendSubType
                 || currentJipyoSubType == JipyoSubType.VolatilitySubType
                 || currentJipyoSubType == JipyoSubType.MarketStrengthType
                 || currentJipyoSubType == JipyoSubType.MarketSubType
                 || currentJipyoSubType == JipyoSubType.EtcSubType
				 || currentJipyoSubType == JipyoSubType.DerivationType ) {
                jipyoList = indicatorJipyoList;
            }
            else if(currentJipyoSubType == JipyoSubType.SignalSubType || currentJipyoSubType == JipyoSubType.CandleSubType) {
                jipyoList = signalJipyoList;
            }
            else {
                jipyoList = totalJipyoList;
            }

            for (JipyoItem jipyoItem : jipyoList) {
            	JipyoSubType jipyoSubType = (JipyoSubType)jipyoItem.get("jipyoSubType");

                if (currentJipyoSubType == JipyoSubType.FavoriteSubType
                 || currentJipyoSubType == JipyoSubType.ChartSubType
                 || currentJipyoSubType == JipyoSubType.OverlaySubType
        		 || jipyoSubType == currentJipyoSubType) {
                	boolean isSelected = Boolean.valueOf(jipyoItem.get("isSelected").toString());

                    if (isSelected) {
                        bIsContainSelectedJipyo = true;
                        break;
                    }
                }
            }

//			if (bIsContainSelectedJipyo) {
//				holder.tvTitle.setTypeface(null, Typeface.BOLD);
//				holder.tvTitle.setTextColor(Color.parseColor("#3277C2"));
//			}
//			else {
//				holder.tvTitle.setTypeface(null, Typeface.NORMAL);
//				holder.tvTitle.setTextColor(Color.parseColor("#000000"));
//			}
//			2017. 8. 16 by hyh - 지표추가화면에서 선택된 항목이 있는 대메뉴에서는 볼드 처리 <<
//
//			================================
//             Expand/Collapse Button Setting
//            ================================
//			if (bIsContainSelectedJipyo) {
//				if (isExpanded) {
//					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_up_n", "drawable", context.getPackageName());
//					holder.ivGroupIndicator.setBackgroundResource(resId);
//				}
//				else {
//					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_down_n", "drawable", context.getPackageName());
//					holder.ivGroupIndicator.setBackgroundResource(resId);
//				}
//			}
//			else {
				if (isExpanded) {
					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_up_n", "drawable", context.getPackageName());
					holder.ivGroupIndicator.setBackgroundResource(resId);
				}
				else {
					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_down_n", "drawable", context.getPackageName());
					holder.ivGroupIndicator.setBackgroundResource(resId);
				}
//			}
			//2020.03.17 기술적지표 UI 수정 트리 -> 리스트 - hjw <<

			if (m_nTabType == TabType.MiniType) {
				//List Background
				LayoutParams lpConvertView = convertView.getLayoutParams();
				lpConvertView.height = (int) COMUtil.getPixel(34);
				convertView.setBackgroundColor(Color.TRANSPARENT);

				//Title
//				holder.tvTitle.setTextColor(Color.WHITE);
				holder.tvTitle.setTextColor(CoSys.TITLE_COLOR1);
				holder.tvTitle.setTypeface(COMUtil.typefaceBold);
				holder.tvTitle.setTextSize(15);

				//Button
//				LayoutParams lpGroupIndicator = holder.ivGroupIndicator.getLayoutParams();
//				lpGroupIndicator.width = (int) COMUtil.getPixel(14);
//				lpGroupIndicator.height = (int) COMUtil.getPixel(14);
//				holder.ivGroupIndicator.setLayoutParams(lpGroupIndicator);

				if (isExpanded) {
					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_up_n", "drawable", context.getPackageName());
					holder.ivGroupIndicator.setBackgroundResource(resId);
				}
				else {
					int resId = context.getResources().getIdentifier("btn_jipyo_arrow_down_n", "drawable", context.getPackageName());
					holder.ivGroupIndicator.setBackgroundResource(resId);
				}
			}

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ArrayList<JipyoItem> indicatorChildArrayList = currentChildList.get(groupPosition);
			final JipyoItem jipyoItem = indicatorChildArrayList.get(childPosition);			
			return getJipyoListRowView(childPosition, convertView, parent, jipyoItem);
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	class GroupViewHolder {
		private TextView tvTitle;
		private ImageView ivGroupIndicator;
	}

	class JipyoListViewHolder {
		private RelativeLayout rlChkJipyo;
		private CheckBox chkJipyo;
		private TextView tvTitle;
		private Button btnDuplicate;
		private RelativeLayout rlFavorite;
		private CheckBox chkFavorite;
		private RelativeLayout rlConfig;
		private ImageView ivConfig;
	}
	
	private View getJipyoListRowView(int position, View convertView, ViewGroup parent, final JipyoItem jipyoItem) {
		
		final JipyoListViewHolder holder;

		if (convertView == null) {

			LayoutInflater factory = LayoutInflater.from(m_context);
			int resId;
			if (m_nTabType == TabType.MiniType) {
				resId = m_context.getResources().getIdentifier("jipyo_mini_listview_item", "layout", m_context.getPackageName());
			}
			else
				resId = m_context.getResources().getIdentifier("jipyo_tab_listview_item", "layout", m_context.getPackageName());
			convertView = (View) factory.inflate(resId, parent, false);

			holder = new JipyoListViewHolder();

			resId = m_context.getResources().getIdentifier("rl_chk_jipyo", "id", m_context.getPackageName());
			holder.rlChkJipyo = (RelativeLayout) convertView.findViewById(resId);

			resId = m_context.getResources().getIdentifier("chk_jipyo", "id", m_context.getPackageName());
			holder.chkJipyo = (CheckBox) convertView.findViewById(resId);
			
			resId = m_context.getResources().getIdentifier("tv_title", "id", m_context.getPackageName());
			holder.tvTitle = (TextView) convertView.findViewById(resId);
			holder.tvTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.chkJipyo.performClick();
				}
			});

			resId = m_context.getResources().getIdentifier("btn_duplicate", "id", m_context.getPackageName());
			holder.btnDuplicate = (Button) convertView.findViewById(resId);
			
			resId = m_context.getResources().getIdentifier("rl_favorite", "id", m_context.getPackageName());
			holder.rlFavorite = (RelativeLayout) convertView.findViewById(resId);
			holder.rlFavorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.chkFavorite.performClick();
				}
			});
			
			resId = m_context.getResources().getIdentifier("chk_favorite", "id", m_context.getPackageName());
			holder.chkFavorite = (CheckBox) convertView.findViewById(resId);
			
			resId = m_context.getResources().getIdentifier("rl_config", "id", m_context.getPackageName());
			holder.rlConfig = (RelativeLayout) convertView.findViewById(resId);

			resId = m_context.getResources().getIdentifier("iv_config", "id", m_context.getPackageName());
			holder.ivConfig = (ImageView) convertView.findViewById(resId);

			convertView.setTag(holder);
		} else {
			holder = (JipyoListViewHolder) convertView.getTag();
		}

	    //=========================
	    // 지표 데이터 획득
	    //=========================
		final String name = jipyoItem.get("name").toString();
		final String tag = jipyoItem.get("tag").toString();
		final JipyoType jipyoType = (JipyoType)jipyoItem.get("jipyoType");
		final boolean isSelected = Boolean.valueOf(jipyoItem.get("isSelected").toString());
		boolean isDuplicated = Boolean.valueOf(jipyoItem.get("isDuplicated").toString());
		boolean isFavorite = Boolean.valueOf(jipyoItem.get("isFavorite").toString());
		boolean canBeDuplicated = Boolean.valueOf(jipyoItem.get("canBeDuplicated").toString());
		boolean canBeFavorite = Boolean.valueOf(jipyoItem.get("canBeFavorite").toString());
		boolean useConfig = Boolean.valueOf(jipyoItem.get("useConfig").toString());
		
	    //=========================
	    // Check or Radio Setting
	    //=========================
		int resId = m_context.getResources().getIdentifier("checkbox_change", "drawable", m_context.getPackageName());
		
		if (jipyoType == JipyoType.ChartJipyoType) {
			resId = m_context.getResources().getIdentifier("radiobtn_change", "drawable", m_context.getPackageName());
		}

		holder.chkJipyo.setTag(tag);
		holder.chkJipyo.setBackgroundResource(resId);
		holder.chkJipyo.setChecked(isSelected);
		holder.chkJipyo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox chk = (CheckBox)v;

				if (jipyoType == JipyoType.ChartJipyoType) {
					//차트유형 Radio Button 갱신
					if(chk.isChecked())
					{
						for(JipyoItem chartTypeJipyoItem : chartTypeJipyoList) {
							if(chartTypeJipyoItem.get("name").equals("캔들") || chartTypeJipyoItem.get("name").equals("역시계곡선")) {
								chartTypeJipyoItem.put("isSelected", false);
								chartTypeJipyoItem.put("useConfig", true);
							} else {
								chartTypeJipyoItem.put("isSelected", false);
								chartTypeJipyoItem.put("useConfig", false);
							}
						}

						jipyoItem.put("isSelected", chk.isChecked());

						String name = jipyoItem.get("name").toString();

				        //차트 적용
				        COMUtil.setChartFromXML(chk);
					}
				}
				else {
					//지표 추가 검증
					if(!validateIndicatorAdd(tag)) {
	        	    	//2014.12.03 by lyk - 추가 불가능한 지표 메시지 처리
	    	        	DRAlertDialog alert = new DRAlertDialog(COMUtil._chartMain);

	    				alert.setTitle("");
						alert.setMessage("해당 지표는 추가할 수 없습니다");
						alert.setOkButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});

	    				alert.show();
	    				//2014.12.03 by lyk - 추가 불가능한 지표 메시지 처리 end

	    				chk.setChecked(false);
	        	        return;
	        	    }

					//2017. 8. 29 by hyh - 지표 추가 개수 제한 처리 >>
			        if (chk.isChecked()) {
			            //최대 개수까지 추가
			            if (nSelectedJipyoCount >= MAXIMUM_SELECTED_JIPYO_COUNT) {
			                String strMessage = "지표는 최대 " + MAXIMUM_SELECTED_JIPYO_COUNT + "개까지 설정 가능합니다.";

							DRAlertDialog alert = new DRAlertDialog(COMUtil._chartMain);

		    				alert.setTitle("");
							alert.setMessage(strMessage);
							alert.setOkButton("확인", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});

		    				alert.show();

		    				chk.setChecked(false);
			                return;
			            }

			            nSelectedJipyoCount++;
			        }
			        else {
			            nSelectedJipyoCount--;
			        }
			        //2017. 8. 29 by hyh - 지표 추가 개수 제한 처리 <<

					jipyoItem.put("isSelected", chk.isChecked());
					jipyoItem.put("useConfig", true);

					//지표 적용
					COMUtil.setJipyo(chk);
				}

				//리스트 갱신
				jipyoListAdapter.notifyDataSetChanged();
				expandableListAdapter.notifyDataSetChanged();
				expandableListAdapterSiganl.notifyDataSetChanged();

				//2019. 06. 26 by hyh - 주기별 차트 설정
				COMUtil._mainFrame.saveStatus(null);
			}
		});

		holder.rlChkJipyo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.chkJipyo.performClick();
			}
		});

		//=========================
	    // Title TextView Setting
	    //=========================
		if (bIsFiltered && currentTabId != TabId.IndicatorTabId && currentTabId != TabId.SignalTabId) {
			String strHtmlJipyoName = arrFilteredNames.get(position);
			holder.tvTitle.setText(Html.fromHtml(strHtmlJipyoName));
		}
		else {
			holder.tvTitle.setText(name);
		}

	    //=========================
	    // 중복지표 버튼 세팅
	    //=========================
		if (isDuplicated) {
			holder.tvTitle.setTextColor(Color.parseColor(JIPYO_LIST_DUPLICATED_ITEM_TEXT_COLOR));
			convertView.setPadding((int)COMUtil.getPixel(20),0,0,0);
			convertView.setBackgroundColor(Color.rgb(249,249,250));
		}
		else {
			holder.tvTitle.setTextColor(Color.parseColor(JIPYO_LIST_ITEM_TEXT_COLOR));
			convertView.setPadding(0,0,0,0);
			convertView.setBackgroundColor(Color.WHITE);
		}
		
		if (canBeDuplicated && currentTabId != TabId.FavoriteTabId) {
			holder.btnDuplicate.setVisibility(View.VISIBLE);
			
			if (isDuplicated) {
				int layoutResId = m_context.getResources().getIdentifier("btn_duplicatedindicator_minus_change", "drawable", m_context.getPackageName());
				holder.btnDuplicate.setBackgroundResource(layoutResId);
				holder.btnDuplicate.setTag("중복삭제");
			}
			else {
				int layoutResId = m_context.getResources().getIdentifier("btn_duplicatedindicator_plus_change", "drawable", m_context.getPackageName());
				holder.btnDuplicate.setBackgroundResource(layoutResId);
				holder.btnDuplicate.setTag("중복추가");
			}
		}
		else {
			holder.btnDuplicate.setVisibility(View.GONE);
		}
		
		holder.btnDuplicate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (holder.btnDuplicate.getTag().equals("중복추가")) {
					addAddJipyoItem(jipyoItem);
				}
				else {
					//중복지표 삭제 시 체크 해제 후 진행
					if (isSelected) {
						holder.chkJipyo.performClick();
					}
					
					delAddJipyoItem(jipyoItem);
				}
			}
		});
		
	    //=========================
	    // 즐겨찾기 버튼 세팅
	    //=========================
		holder.chkFavorite.setChecked(isFavorite);

		if (m_nTabType == TabType.MiniType)    //2017.08.08 by LYH >> mini 지표 설정 추가.
		{
			holder.rlFavorite.setVisibility(View.GONE);
		}
		else {
			if (canBeFavorite) {
				holder.rlFavorite.setVisibility(View.VISIBLE);
			}
			else {
				holder.rlFavorite.setVisibility(View.GONE);
			}
		}
		//2017.08.08 by LYH >> 관심 지표 기능 처리.
		holder.chkFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jipyoItem.put("isFavorite", holder.chkFavorite.isChecked());
				if (holder.chkFavorite.isChecked()) {
					COMUtil.addFavoriteIndicator(name);
				}
				else {
					COMUtil.deleteFavoriteIndicator(name);
				}
			}
		});
		//2017.08.08 by LYH >> 관심 지표 기능 처리. end
	    
	    //=========================
	    // 설정 버튼 세팅
	    //=========================
		if (useConfig) {
			holder.rlConfig.setVisibility(View.VISIBLE);
		}
		else {
			holder.rlConfig.setVisibility(View.INVISIBLE);
		}

		if (isSelected || jipyoItem.get("name").toString().equals("캔들")) {
			holder.ivConfig.setVisibility(View.VISIBLE);
		}
		else {
			holder.ivConfig.setVisibility(View.INVISIBLE);
		}

		holder.rlConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyPad_(v);		//2017.08.07 by PJM 키패드 제거.
				showDetailView(jipyoItem);
			}
		});

		if (m_nTabType == TabType.MiniType) {
			//List Background
			LayoutParams lpConvertView = convertView.getLayoutParams();
			lpConvertView.height = (int) COMUtil.getPixel(34);
			convertView.setBackgroundColor(Color.TRANSPARENT);

			//Checkbox
//			int layoutResId = m_context.getResources().getIdentifier("checkbox_change_mini", "drawable", m_context.getPackageName());
//			holder.chkJipyo.setBackgroundResource(layoutResId);

			//Title
			//holder.tvTitle.setTextColor(Color.WHITE);
			holder.tvTitle.setTextColor(CoSys.TITLE_COLOR1);
			holder.tvTitle.setTextSize(14);

			//Config Button
//			LayoutParams lpConfig = holder.ivConfig.getLayoutParams();
//			lpConfig.width = (int) COMUtil.getPixel(14);
//			lpConfig.height = (int) COMUtil.getPixel(14);
//			holder.ivConfig.setLayoutParams(lpConfig);
//
//			LayoutParams lpRlConfig = holder.rlConfig.getLayoutParams();


			if (useConfig && isSelected) {
				int layoutResId = m_context.getResources().getIdentifier("chart_menu_show", "drawable", m_context.getPackageName());
				holder.ivConfig.setBackgroundResource(layoutResId);
				holder.rlConfig.setVisibility(VISIBLE);
			}
			else {
				holder.rlConfig.setVisibility(GONE);
			}
		}
		COMUtil.setGlobalFont(this.m_layout);

		return convertView;
	}
	
	public class KoreanString {
		private final String _strTargetText;
		private String strMatchedText;

		public KoreanString(String s) {
			_strTargetText = s;
		}

		public boolean equals(String other) {
			if (other == null)
				return false;
			if (_strTargetText.length() != other.length())
				return false;

			for (int i = 0; i < _strTargetText.length(); i++) {
				final char c;
				if (DRKoreanChar.isHangulChoseong(other.charAt(i)) && DRKoreanChar.isHangulSyllable(_strTargetText.charAt(i)))
					c = new DRKoreanChar(_strTargetText.charAt(i), false).getChoseong();
				else if (DRKoreanChar.isHangulCompatibilityChoseong(other.charAt(i)) && DRKoreanChar.isHangulSyllable(_strTargetText.charAt(i)))
					c = new DRKoreanChar(_strTargetText.charAt(i), true).getChoseong();
				else
					c = _strTargetText.charAt(i);

				if (c != other.charAt(i))
					return false;
			}
			return true;
		}

		public boolean startsWith(String prefix) {

			if (prefix == null)
				return false;
			if (_strTargetText.length() < prefix.length())
				return false;

			for (int i = 0; i < prefix.length(); i++) {

				final char c;
				if (DRKoreanChar.isHangulChoseong(prefix.charAt(i)) && DRKoreanChar.isHangulSyllable(_strTargetText.charAt(i)))
					c = new DRKoreanChar(_strTargetText.charAt(i), false).getChoseong();
				else if (DRKoreanChar.isHangulCompatibilityChoseong(prefix.charAt(i)) && DRKoreanChar.isHangulSyllable(_strTargetText.charAt(i)))
					c = new DRKoreanChar(_strTargetText.charAt(i), true).getChoseong();
				else
					c = _strTargetText.charAt(i);

				if (c != prefix.charAt(i))
					return false;
			}

			strMatchedText = _strTargetText.substring(0, prefix.length());
			return true;
		}

		public boolean contains(String strInput) {

			String strValue = _strTargetText;

			if (strInput == null)
				return false;

			//한글자씩 줄여가며 비교한다.
			for (int nIndexForValue = 0; nIndexForValue < strValue.length(); nIndexForValue++) {

				if (strValue.length() < strInput.length())
					return false;

				for (int nIndexForInput = 0; nIndexForInput < strInput.length(); nIndexForInput++) {

					final char c;

					if (DRKoreanChar.isHangulChoseong(strInput.charAt(nIndexForInput)) && DRKoreanChar.isHangulSyllable(strValue.charAt(nIndexForInput)))
						c = new DRKoreanChar(strValue.charAt(nIndexForInput), false).getChoseong();
					else if (DRKoreanChar.isHangulCompatibilityChoseong(strInput.charAt(nIndexForInput)) && DRKoreanChar.isHangulSyllable(strValue.charAt(nIndexForInput)))
						c = new DRKoreanChar(strValue.charAt(nIndexForInput), true).getChoseong();
					else
						c = strValue.charAt(nIndexForInput);

					//초성을 가져와 비교한다.
					if (c != strInput.charAt(nIndexForInput))
						break;
					else {
						//입력한 문자와 초성이 모두 같은 경우 true 반환
						if (nIndexForInput == strInput.length() - 1) {
							strMatchedText = strValue.substring(0, strInput.length());
							return true;
						}
					}
				}

				strValue = strValue.substring(1);
			}

			return false;
		}

		//최종 매치된 문자열을 반환한다.
		public String getMatchedText() {
			return strMatchedText;
		}
	}

	@SuppressLint("DefaultLocale")
	private String insertHtmlFontTag(String source, String target) {

		String strAppliedHtmlFontTag = source;

		String strLowerCasedSource = source.toLowerCase();
		String strLowerCasedTarget = target.toLowerCase();

		int nMatchedStartIndex = strLowerCasedSource.indexOf(strLowerCasedTarget);
		int nMatchedLastIndex = strLowerCasedSource.lastIndexOf(strLowerCasedTarget);
		int nMatchedEndIndex = nMatchedStartIndex + strLowerCasedTarget.length();

		if (nMatchedStartIndex >= 0) {
			String strPrefix = source.substring(0, nMatchedStartIndex);
			String strMatchedText = source.substring(nMatchedStartIndex, nMatchedEndIndex);
			String strSuffix = source.substring(nMatchedEndIndex);

			//매치되는 텍스트가 뒤에 더 있는 경우 재귀호출 
			if (nMatchedLastIndex != nMatchedStartIndex) {
				strSuffix = insertHtmlFontTag(strLowerCasedSource.substring(nMatchedEndIndex), target);
			}

			strAppliedHtmlFontTag = strPrefix + "<font color='" + FILTER_MARK_COLOR + "'>" + strMatchedText + "</font>" + strSuffix;
		}

		return strAppliedHtmlFontTag;
	}

	// 키보드 제거
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
	}
	//2017.08.07 by PJM 키패드 제거. >>
	public void hideKeyPad_(View v)
	{
		InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		v.requestFocus();
	}
	//2017.08.07 by PJM 키패드 제거. <<
	
	public void showDetailView(JipyoItem jipyoItem) {
		//===================================
	    // 선택되지 않은 경우에는 상세설정창 보이지 않음
	    //===================================
	    boolean isSelected = Boolean.valueOf(jipyoItem.get("isSelected").toString());
	    
	    if (!isSelected && !jipyoItem.get("name").toString().equals("캔들")) {
	        return;
	    }
		
		//=========================
	    // 지표의 원래 이름으로 획득
	    //=========================
		String name = jipyoItem.get("name").toString();
		String tag = jipyoItem.get("tag").toString();
		
		@SuppressWarnings("unchecked")
        Vector<Hashtable<String, String>> jipyoItems = (Vector<Hashtable<String, String>>) COMUtil.getJipyoMenu().clone();
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();

		for (int nIndex = 0; nIndex < addItems.size(); nIndex++) {
			Hashtable<String, String> addItem = (Hashtable<String, String>) addItems.get(nIndex);
			if (addItem == null || (addItem != null && addItem.size() == 0)) {
				continue;
			}
			int addTag = Integer.parseInt((String) addItem.get("tag"));

			int index = 0;
			for (int k = 0; k < jipyoItems.size(); k++) {
				Hashtable<String, String> item = (Hashtable<String, String>) jipyoItems.get(k);
				int itemTag = Integer.parseInt((String) item.get("tag"));
				if (addTag / 100 == itemTag || addTag / 100 == itemTag / 100) {
					index = k;
				}
			}
			jipyoItems.add(index + 1, addItem);
		}
		
		for (int nIndex=0; nIndex<jipyoItems.size(); nIndex++) {
			
			if (jipyoItems.get(nIndex).get("tag").equals(tag)) {
				name = jipyoItems.get(nIndex).get("name");
				break;
			}
		}

	    //=========================
	    // 팝업 세팅
	    //=========================
		RelativeLayout detailLayout = new RelativeLayout(COMUtil._mainFrame.getContext());
		
		//DetailJipyoController detailJipyoView = new DetailJipyoController(m_context, m_layout);
		DetailJipyoController detailJipyoView;
		if(m_nTabType == TabType.MiniType)	//2017.08.08 by LYH >> mini 지표 설정 추가.
		{
			detailJipyoView = new DetailJipyoController(m_context, detailLayout);
		}
		else
		{
			detailJipyoView = new DetailJipyoController(m_context, m_layout);
		}
		detailJipyoView.setTitle(name);
		detailJipyoView.setUI();

		if (name.equals("캔들")) {
			detailJipyoView.setInitGraph("일본식봉");
		} else {
			detailJipyoView.setInitGraph(name);
		}
		
		//2017.08.08 by LYH >> mini 지표 설정 추가.
		if(m_nTabType == TabType.MiniType)
		{
			if(null == COMUtil._neoChart.jipyoListDetailPopup)	//2014. 6. 16 차트화면 설정상세창에서 각항목 여러번 누르면 창이 안닫히는 현상 수정 : popupwindow null체크
			{
		    	COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(detailLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		    	COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
		    	COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());
		    	COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.NO_GRAVITY, 0, 0);
				COMUtil._neoChart.jipyoListDetailPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
					@Override
					public void onDismiss() {
						COMUtil._neoChart.jipyoListDetailPopup = null;
					}
				});
			}
		}
		//2017.08.08 by LYH >> mini 지표 설정 추가. end
	}

	public boolean validateIndicatorAdd(String sTag) {
		int tag = -1;
		try {
			tag = Integer.parseInt(sTag);
		} catch(Exception e) {
			
		}

//		//2019. 04. 11 by hyh - 미결제약정 국내선물 외에는 추가하지 못하도록 처리 >>
//		if (COMUtil._mainFrame.mainBase.baseP.nMarketType != 1) {
//			if (tag == ChartUtil.MARKET1) {
//				return false;
//			}
//		}
//		//2019. 04. 11 by hyh - 미결제약정 국내선물 외에는 추가하지 못하도록 처리 <<

//		//2019. 06. 07 by hyh - FX에서 거래량 추가 불가 처리 >>
//		if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 4) {
//			if (tag == ChartUtil.VOLUME) {
//				return false;
//			}
//
//			if (tag == ChartUtil.VAVERAGE) {
//				return false;
//			}
//		}
//		//2019. 06. 07 by hyh - FX에서 거래량 추가 불가 처리 <<

	    return true;
	}
	
	//중복지표 추가/삭제 처리
	public void addAddJipyoItem(JipyoItem jipyoItem) {
		String name = jipyoItem.get("name").toString();
		String tag = jipyoItem.get("tag").toString();
		JipyoSubType jipyoSubType = (JipyoSubType)jipyoItem.get("jipyoSubType"); 
		
		//추가된 리스트의 name에서 COMUtil.JIPYO_ADD_REMARK를 체크하여 마지막 인덱스를 찾아낸다.
		String cmpName = COMUtil.getAddJipyoTitle(name);
		
		//같은 이름의 지표항목만 추린다
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		Vector<String> sameNames = new Vector<String>();
		Vector<String> sameIndexs = new Vector<String>();
		for(int nIndex=0; nIndex<addItems.size(); nIndex++) {
			Hashtable<String, String> subItem = addItems.get(nIndex);
			String subName = subItem.get("name").toString();
			
			int subIndex = subName.indexOf(COMUtil.JIPYO_ADD_REMARK);
			String sameName = "";
			String sameIndex = "";
			if(subIndex>0) {
				sameIndex = subName.substring(subIndex+1);
				sameName = subName.substring(0, subIndex);
			}
			if(cmpName.equals(sameName)) {
				sameNames.add(sameName);
				sameIndexs.add(sameIndex);
			}			
		}
		
		//2015.04.08 by lyk - 동일한 중복지표의 최대 추가 가능 갯수 처리(최대 5개 까지)
	    if (sameIndexs.size() > 4) {	        
	    	DRAlertDialog alert = new DRAlertDialog(COMUtil._chartMain);
			alert.setMessage("최대 5개까지 추가할 수 있습니다.");
			alert.setOkButton("확인", null);
			alert.show();
	    	
	        return;
	    }
	    //2015.04.08 by lyk - 동일한 중복지표의 최대 추가 가능 갯수 처리(최대 5개 까지) end

	    //=================================
	    // COMUtil.getAddJipyoList() 갱신 
	    //=================================
		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 	
		int lastIndex = 0;
		if(sameIndexs.size()<1) {
			lastIndex = 0;
		} else {
			lastIndex = Integer.parseInt(sameIndexs.lastElement());
		}
		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 

		String addName = name + COMUtil.JIPYO_ADD_REMARK + (lastIndex+1);
		
		Vector<Hashtable<String, String>> addItem = COMUtil.getAddJipyoList();
		Hashtable<String, String> addHashItem = new Hashtable<String, String>();
		//type, name, tag
		addHashItem.put("type", "Indicator");
		addHashItem.put("name", String.valueOf(addName));

		//COMUtil.addJipyo() 에서 tag 비교 값으로 사용한다. 
		int addTag = 0;
		if (lastIndex + 1 > 0) {
			addTag = Integer.parseInt(tag) * 100 + (lastIndex + 1);
		} else {
			addTag = Integer.parseInt(tag);
		}
		addHashItem.put("tag", String.valueOf(addTag));

		addHashItem.put("detailType", "jipyo");
		addHashItem.put("subType", getSubTypeText(jipyoSubType));

		addItem.add(addHashItem);
		
	    //=========================
	    // 리스트 갱신
	    //=========================
		String addJipyoName = COMUtil.getAddJipyoTitle(name);
		JipyoItem addJipyoItem = getJipyoItem(addJipyoName, String.valueOf(addTag),
				JipyoType.IndicatorJipyoType, jipyoSubType, 
				false, true, false, true, false, false);

	    //totalJipyoList
	    for(int nIndex=0; nIndex<totalJipyoList.size(); nIndex++) {
	    	JipyoItem totalJipyoItem = totalJipyoList.get(nIndex);
	    	int itemTag = Integer.parseInt(totalJipyoItem.get("tag").toString());
	    	
	        if(addTag/100 == itemTag || addTag/100 == itemTag/100) {
	        	totalJipyoList.add(nIndex+1, addJipyoItem);
	            break;
	        }
	    }
	    
	    //indicatorJipyoList
	    for(int nIndex=0; nIndex<indicatorJipyoList.size(); nIndex++) {
	    	JipyoItem totalJipyoItem = indicatorJipyoList.get(nIndex);
	    	int itemTag = Integer.parseInt(totalJipyoItem.get("tag").toString());
	    	
	        if(addTag/100 == itemTag || addTag/100 == itemTag/100) {
	        	indicatorJipyoList.add(nIndex+1, addJipyoItem);
	            break;
	        }
	    }
	    
		//indicatorChildList
		for (ArrayList<JipyoItem> indicatorChildArrayList : indicatorChildList) {
			for (int nIndex=0; nIndex<indicatorChildArrayList.size(); nIndex++) {
				JipyoItem indicatorlJipyoItem = indicatorChildArrayList.get(nIndex);
				int itemTag = Integer.parseInt(indicatorlJipyoItem.get("tag").toString());
				
				if(addTag/100 == itemTag || addTag/100 == itemTag/100) {
					indicatorChildArrayList.add(nIndex+1, addJipyoItem);
					break;
				}
			}
		}
	    
		jipyoListAdapter.notifyDataSetChanged();
		expandableListAdapter.notifyDataSetChanged();
		expandableListAdapterSiganl.notifyDataSetChanged();
		
		//=========================
	    // 필터링 된 상태일 때 내용 갱신
	    //=========================
		if (bIsFiltered) {
			String strFilterText = edFilter.getText().toString();
			
			if (strFilterText.length() > 0) {
				jipyoListAdapter.getFilter().filter(strFilterText);
			}
		}
	}
	
	public void delAddJipyoItem(JipyoItem jipyoItem) {
		String name = jipyoItem.get("name").toString();
		String tag = jipyoItem.get("tag").toString();
		JipyoSubType jipyoSubType = (JipyoSubType)jipyoItem.get("jipyoSubType"); 
		
		//=================================
	    // COMUtil.getAddJipyoList() 갱신 
	    //=================================
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		for (int nIndex=0; nIndex<addItems.size(); nIndex++) {
			Hashtable<String, String> addJipyoItem = addItems.get(nIndex);
			
			if (addJipyoItem.get("tag").equals(tag)) {
				addItems.remove(nIndex);
				break;
			}
		}
		
		//=========================
	    // 리스트 갱신
	    //=========================
		//totalJipyoList 갱신
		for (int nIndex=0; nIndex<totalJipyoList.size(); nIndex++) {
			JipyoItem totalJipyoItem = totalJipyoList.get(nIndex);
			
			if (totalJipyoItem.get("tag").toString().equals(tag)) {
				totalJipyoList.remove(nIndex);
				break;
			}
		}
		
		//indicatorJipyoList 갱신
		for (int nIndex=0; nIndex<indicatorJipyoList.size(); nIndex++) {
			JipyoItem indicatorlJipyoItem = indicatorJipyoList.get(nIndex);
			
			if (indicatorlJipyoItem.get("tag").toString().equals(tag)) {
				indicatorJipyoList.remove(nIndex);
				break;
			}
		}
		
		//indicatorChildList 갱신
		for (ArrayList<JipyoItem> indicatorChildArrayList : indicatorChildList) {
			for (int nIndex=0; nIndex<indicatorChildArrayList.size(); nIndex++) {
				JipyoItem indicatorlJipyoItem = indicatorChildArrayList.get(nIndex);
				
				if (indicatorlJipyoItem.get("tag").toString().equals(tag)) {
					indicatorChildArrayList.remove(nIndex);
					break;
				}
			}
		}

		jipyoListAdapter.notifyDataSetChanged();
		expandableListAdapter.notifyDataSetChanged();
		expandableListAdapterSiganl.notifyDataSetChanged();
		
		//=========================
	    // 필터링 된 상태일 때 내용 갱신
	    //=========================
		if (bIsFiltered) {
			String strFilterText = edFilter.getText().toString();
			
			if (strFilterText.length() > 0) {
				jipyoListAdapter.getFilter().filter(strFilterText);
			}
		}
	}
}
