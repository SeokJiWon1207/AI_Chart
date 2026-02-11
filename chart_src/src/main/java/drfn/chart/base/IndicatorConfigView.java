package drfn.chart.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.block.Block;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.comp.ExEditText;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

public class IndicatorConfigView extends View implements ExEditText.OnBackButtonListener {
	protected ArrayList<JipyoChoiceItem> m_itemsArr, m_itemsArr2, m_itemsArr3, m_itemsArr4, m_itemsArr5;	//2014. 9. 3 차트유형 추가:m_itemsArr5 추가
	//일반설정 - 화면설정(길게누름)
	protected ArrayList<JipyoChoiceItem> wrapperScreenSetList = new ArrayList<JipyoChoiceItem>();

	//2016. 1. 11 종가 등락률 옵션처리
	//일반설정 - 종가 등락률 대비
	//protected ArrayList<JipyoChoiceItem> wrapperChgRateList = new ArrayList<JipyoChoiceItem>();
	ExEditText edRightPadding=null;
	ExEditText edSkipTick=null;
	ExEditText edSkipVolume=null;

	//2012. 7. 18 지표설정, 오버레이 의 어댑터 분리  2가 오버레이
	MyArrayAdapter m_scvAdapter;
	MyArrayAdapter m_scvAdapter2;
	MyArrayAdapter m_scvAdapter3;
	MyArrayAdapter m_scvAdapter4;
	MyArrayAdapter m_scvAdapter5;	//2015. 1. 13 차트유형 추가

	chartArrayAdapter m_scvAdapter_selectlist;

	//2012. 7. 17 차트 어댑터
	protected ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	protected ArrayList<Boolean> itemChecked2 = new ArrayList<Boolean>();
	protected ArrayList<Boolean> itemChecked3 = new ArrayList<Boolean>();
	protected ArrayList<Boolean> itemChecked4 = new ArrayList<Boolean>();
	protected ArrayList<Boolean> itemChecked5 = new ArrayList<Boolean>();	//2015. 1. 13 차트유형 추가

	//2012. 8. 6 체크유무에 따라서 상세설정버튼 상태를 변경하기 위해 wrapper 클래스의 리스트 만들어서 참조함
	protected ArrayList<ViewWrapper> wrapperList = new ArrayList<ViewWrapper>();
	protected ArrayList<ViewWrapper> wrapperList2 = new ArrayList<ViewWrapper>();
	protected ArrayList<ViewWrapper> wrapperList3 = new ArrayList<ViewWrapper>();
	protected ArrayList<ViewWrapper> wrapperList4 = new ArrayList<ViewWrapper>();
	protected ArrayList<ViewWrapper> wrapperList5 = new ArrayList<ViewWrapper>();	//2015. 1. 13 차트유형 추가

//    protected ArrayList<ViewWrapper_charttype> wrapperList_mini_selectlist = new ArrayList<ViewWrapper_charttype>();

	protected ListView jipyolist = null;
	protected ListView overlaylist = null;
	protected ListView totallist = null;
	protected ListView otherlist = null;
	//2012. 7. 17 차트유형 리스트뷰 추가
	protected ListView chartlist = null;
	RelativeLayout layout=null;
	String currentType = "";
	LinearLayout ll=null;
	//2012. 7. 27 지표들 제목부분 레이아웃
	RelativeLayout rl_chartType, rl_overLay, rl_Jipyo, rl_period, rl_other;


	//2012. 7. 17 차트유형 ArrayList 추가
	protected ArrayList<String> typeList = new ArrayList<String>();
	protected ArrayList<String> typeTags = new ArrayList<String>();
	ArrayList<CheckBox> arChkBox;
	EditText editPeriod;
	int[] arBtn = 	new int[14];


	//2012. 7. 17 차트유형  현재 선택된 차트 index
	int nChartSelected;
	int nSelbtncount = 0;

	Context context;

	//2013. 2. 20  체크박스 이미지 변경  : I106
	Drawable pressChk, normalChk;
	Drawable pressRadio, normalRadio;
	int nDrawableSize;

	//2013. 1. 16  분틱설정창 컨트롤 리스트
	ArrayList<TextView> arPeriodTextView;
	ArrayList<EditText> arPeriodBunEditText;
	ArrayList<EditText> arPeriodTicEditText;

	ToolBarSetListView toolbarlist = null;

	final int nIndicatorRowHeightValue = 40;   //2013. 8. 23 지표설정창의 한 행 크기
	//2015. 3. 3 지표설정 거래금액의 위치가 기술적지표로 이동되어있음>>
	final int OVERLAY_JIPYO_COUNT = 12;
	final int OTHER_JIPYO_COUNT = 17;
	//2015. 3. 3 지표설정 거래금액의 위치가 기술적지표로 이동되어있음<<

	//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
	Vector<Hashtable<String, String>> jipyoItems = null;
	//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

	//2015. 1. 13 by lyk 중복지표 추가/삭제 처리
	protected ArrayList<Vector<Hashtable<String, String>>> items2 = null;
	//2015. 1. 13 by lyk 중복지표 추가/삭제 처리 end

	//2015. 2. 24 차트 상하한가 표시
	OnSettingViewEventListener eventListener;

	/** 미니설정창인지 */
	protected boolean m_bIsMiniPopup = false;

	//2016.04.22 black 테마 텍스트 색상
	int textColor, selectTextColor, tabTextColor;
	//2016.04.22 black 테마 텍스트 색상

	int m_nType = 0; //0:지표설정, 1:차트설정

	Boolean m_bChartTypeFlag = false;
	TextView m_tv_charttype;
	
	//상속용 기본생성자
	public IndicatorConfigView(Context context, final RelativeLayout layout, RelativeLayout.LayoutParams params)
	{
		super(context);
		this.context = context;
		this.layout = layout;
	}

	LinearLayout periodui=null;

	private final int ids[] = {this.getContext().getResources().getIdentifier("periodsetItem1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem2", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem3", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem4", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem5", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem6", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem7", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem8", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem9", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem10", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem11", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem12", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem13", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("periodsetItem14", "id", this.getContext().getPackageName())};

	private final String ids_name[] = {"periodsetItem1",
			"periodsetItem2",
			"periodsetItem3",
			"periodsetItem4",
			"periodsetItem5",
			"periodsetItem6",
			"periodsetItem7",
			"periodsetItem8",
			"periodsetItem9",
			"periodsetItem10",
			"periodsetItem11",
			"periodsetItem12",
			"periodsetItem13",
			"periodsetItem14"};

	public IndicatorConfigView(final Context context, final RelativeLayout layout, int triXpos, int nType) {
		super(context);
		this.context = context;
		m_nType = nType;

		//2012. 7. 16  지표설정창 레이아웃 변경.
		this.layout = layout;
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId;

		//2012. 8. 16  HONEYCOMB 인지 아닌지에 따라서 구분 : I_tab16
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			layoutResId = context.getResources().getIdentifier("indicatorview_tab", "layout", context.getPackageName());
		}
		else
		{
			layoutResId = context.getResources().getIdentifier("indicatorview", "layout", context.getPackageName());
		}
//		if(COMUtil.skinType == COMUtil.SKIN_BLACK)
//		{
//
//			layoutResId = context.getResources().getIdentifier("indicatorview_black", "layout", context.getPackageName());
//		}
			
		ll = (LinearLayout)factory.inflate(layoutResId, null);



		//2016.04.22 테마 블랙 추가
		
//		if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//			textColor = Color.rgb(255, 255, 255);
//			selectTextColor = Color.rgb(255, 255, 255);
//			tabTextColor = Color.rgb(255, 255, 255);
//
//		}
//		else{
			textColor =Color.rgb(0,0,0);
			selectTextColor = Color.rgb(0,0,0);
			tabTextColor = Color.rgb(199,199,199);
//		}
		//2016.04.22 테마 블랙 추가
		//2012. 7. 17 레이아웃 배경색 흰색 변경 
//		ll.setBackgroundColor(Color.WHITE);
		ll.setTag("indicatorView");
		//2012. 7. 18 지표설정창 하단 여백 해결 
		ll.setLayoutParams(params);



		//삼각형 이미지 추가
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			if(triXpos>0) {
				ImageView viewArrow = new ImageView(context);
				layoutResId = context.getResources().getIdentifier("round_desc_tri", "drawable", context.getPackageName());
				viewArrow.setImageResource(layoutResId);
				RelativeLayout.LayoutParams xparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				xparams.leftMargin = triXpos;
				xparams.topMargin = params.topMargin-xparams.height;
				viewArrow.setLayoutParams(xparams);
				layout.addView(viewArrow);
			}
		}
		//2013. 6. 3   지표설정창 태블릿일 때 위치설정 <<
		layout.addView(ll);

		initTabViews();
		setChartTypeList();

		//체크박스들의 상태를 가지고 있는 array 
		arChkBox = new ArrayList<CheckBox>();
		initChartTypeList();
		//initLists();
		//layoutResId = context.getResources().getIdentifier("setTapBtn", "id", context.getPackageName());
		//LinearLayout ll_tabBtn = (LinearLayout)ll.findViewById(layoutResId);

		//2012. 9. 5  분틱차트주기 여는 기능 추가
		layoutResId = context.getResources().getIdentifier("indicator_periodsetting", "id", context.getPackageName());
		rl_period = (RelativeLayout)ll.findViewById(layoutResId);
		rl_period.setVisibility(View.GONE);

		//2012. 7. 18  indicatorview.xml 의 라디오버튼에서 onClick 으로 setChartFromXML 함수 호출할때 could not find a method...  에러발생. 자바코드로 해결안을 발견하여 변경  - drfnkimsh
		layoutResId = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
		final RadioButton radioWhite = (RadioButton) ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName());
		final RadioButton radioBlack = (RadioButton) ll.findViewById(layoutResId);
		//2015. 3. 4 차트 테마 메인따라가기 추가>>
		layoutResId = context.getResources().getIdentifier("bns_gbn_auto_radio_btn", "id", context.getPackageName());
		final RadioButton radioAuto = (RadioButton) ll.findViewById(layoutResId);

		if(COMUtil.bIsAutoTheme) {
			//테마 메인따라가기일 때
			radioAuto.setChecked(true);
			radioAuto.setTextColor(Color.rgb(203, 29, 118));
			radioWhite.setTextColor(Color.rgb(21, 21, 21));
			radioBlack.setTextColor(Color.rgb(21, 21, 21));
		}
		else
		{
			if(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getSkinType() != COMUtil.SKIN_BLACK)
			{
				radioWhite.setChecked(true);
				radioWhite.setTextColor(Color.WHITE);
				radioBlack.setTextColor(Color.rgb(17, 17, 17));
//				radioAuto.setTextColor(Color.rgb(21, 21, 21));
			}
			else
			{
				radioBlack.setChecked(true);
				radioBlack.setTextColor(Color.WHITE);
				radioWhite.setTextColor(Color.rgb(17, 17, 17));
//				radioWhite.setTextColor(Color.rgb(21, 21, 21));
//				radioBlack.setTextColor(Color.rgb(203, 29, 118));
//				radioAuto.setTextColor(Color.rgb(21, 21, 21));
			}
		}

		radioWhite.setOnClickListener(radioListener);
		radioBlack.setOnClickListener(radioListener);
		radioAuto.setOnClickListener(radioListener);
		//2015. 3. 4 차트 테마 메인따라가기 추가<<

		//일반설정 - 테마 터치 개선
//		layoutResId = context.getResources().getIdentifier("rl_bns_gbn_white", "id", context.getPackageName());
//		RelativeLayout rlBnsGbnWhite = (RelativeLayout) ll.findViewById(layoutResId);
//		rlBnsGbnWhite.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				radioWhite.performClick();
//			}
//		});
//
//		layoutResId = context.getResources().getIdentifier("rl_bns_gbn_black", "id", context.getPackageName());
//		RelativeLayout rlBnsGbnBlack = (RelativeLayout) ll.findViewById(layoutResId);
//		rlBnsGbnBlack.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				radioBlack.performClick();
//			}
//		});
//
//		layoutResId = context.getResources().getIdentifier("rl_bns_gbn_auto", "id", context.getPackageName());
//		RelativeLayout rlBnsGbnAuto= (RelativeLayout) ll.findViewById(layoutResId);
//		rlBnsGbnAuto.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				radioAuto.performClick();
//			}
//		});

		//2019. 05. 15 by hyh - EditText 주변 터치 시 에디트박스로 Focus 되도록 처리 >>
		for (int i = 0; i < 8; i++) {
			//분차트
			layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_" + String.valueOf(i + 1), "id", context.getPackageName());
			final EditText editBun = (EditText) ll.findViewById(layoutResId);
			editBun.setTypeface(COMUtil.numericTypefaceMid);

			if (editBun != null) {
				LinearLayout llEditBun = (LinearLayout) editBun.getParent();
				llEditBun.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (editBun != null) {
							editBun.requestFocus();
						}
					}
				});
			}

			//틱차트
			layoutResId = context.getResources().getIdentifier("periodset_row1_edit_tic_" + String.valueOf(i + 1), "id", context.getPackageName());
			final EditText editTic = (EditText) ll.findViewById(layoutResId);
			editTic.setTypeface(COMUtil.numericTypefaceMid);

			if (editTic != null) {
				LinearLayout llEditTic = (LinearLayout) editTic.getParent();
				llEditTic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (editTic != null) {
							editTic.requestFocus();
						}
					}
				});
			}

			//초차트
			if (i < 3) {
				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
				final EditText editSec = (EditText) ll.findViewById(layoutResId);

				if (editSec != null) {
					LinearLayout llEditSec = (LinearLayout) editSec.getParent();
					llEditSec.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (editSec != null) {
								editSec.requestFocus();
							}
						}
					});
				}
			}
		}

		layoutResId = context.getResources().getIdentifier("periodparentlinear", "id", context.getPackageName());
		final LinearLayout periodparentlinear = (LinearLayout) ll.findViewById(layoutResId);
		periodparentlinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_1", "id", context.getPackageName());

				final EditText editBun = (EditText) ll.findViewById(layoutResId);
				editBun.requestFocus();
			}
		});
		//2019. 05. 15 by hyh - EditText 주변 터치 시 에디트박스로 Focus 되도록 처리 <<

		COMUtil.setGlobalFont(ll);
	}

	//2013. 1. 16  분틱설정 텍스트뷰   분차트-틱차트  를 라디오버튼 모드에 따라 바꿔주기
//	private void changePeriodTextViewString()
//	{
//		if(arPeriodTextView == null)
//		{
//			return;
//		}
//		
//		String strPreText;
//		if(COMUtil.isMinValueSet)
//		{
//			strPreText = "분차트";
//		}
//		else
//		{
//			strPreText = "틱차트";			
//		}
//		
//		for(int i = 0; i < arPeriodTextView.size(); i++)
//		{
//			TextView tv = arPeriodTextView.get(i);
//			tv.setText(strPreText+String.valueOf(i+1));
//		}
//	}
	private void changePeriodTextView()
	{
		int layoutResId = context.getResources().getIdentifier("bunview", "id", context.getPackageName());
		LinearLayout bunView = (LinearLayout)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("ticview", "id", context.getPackageName());
		LinearLayout ticView = (LinearLayout)ll.findViewById(layoutResId);

		if(COMUtil.isMinValueSet)
		{
			bunView.setVisibility(View.VISIBLE);
			ticView.setVisibility(View.GONE);
		}
		else
		{
			bunView.setVisibility(View.GONE);
			ticView.setVisibility(View.VISIBLE);
		}
	}

	//2012. 7. 18  indicatorview.xml 의 라디오버튼에서 onClick 으로 setChartFromXML 함수 호출시 에러발생하여 함수호출하는 리스너를 생성 - drfnkimsh
	RadioButton.OnClickListener radioListener = new RadioButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			//2012. 1. 16  라디오버튼 동작 정의  (switch-case 로 v.getId 과 R.id 이용하는 루틴이  Android ADT14 부터 막힘)
//			if(v.getId() == R.id.periodset_radiomin)
//			{
//				COMUtil.isMinValueSet = true;
//				changePeriodTextView();
//			}
//			else if(v.getId() == R.id.periodset_radiotik)
//			{
//				COMUtil.isMinValueSet = false;
//				changePeriodTextView();
//			}
//			else if(v.getId() == R.id.screenset_valuesearch)
//			{
//				COMUtil.isSetScreenViewPanel = true;
//			}
//			else if(v.getId() == R.id.screenset_indicator)
//			{
//				COMUtil.isSetScreenViewPanel = false;
//			}
            if(v.getId() == context.getResources().getIdentifier("lastday_radio_btn", "id", context.getPackageName()))
            {
				int layoutResId = context.getResources().getIdentifier("lastbong_radio_btn", "id", context.getPackageName());
				final RadioButton lastbong_radio_btn = (RadioButton) ll.findViewById(layoutResId);
				lastbong_radio_btn.setChecked(false);

				layoutResId = context.getResources().getIdentifier("lastday_radio_btn", "id", context.getPackageName());
				final RadioButton lastday_radio_btn = (RadioButton) ll.findViewById(layoutResId);

				lastbong_radio_btn.setTextColor(Color.rgb(17, 17, 17));
				lastday_radio_btn.setTextColor(Color.WHITE);

                COMUtil._mainFrame.bIsyJonggaCurrentPrice = true;
                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                base11.repaintAllChart();
            }
            else if(v.getId() == context.getResources().getIdentifier("lastbong_radio_btn", "id", context.getPackageName()))
            {
				int layoutResId = context.getResources().getIdentifier("lastday_radio_btn", "id", context.getPackageName());
				final RadioButton lastday_radio_btn = (RadioButton) ll.findViewById(layoutResId);
				lastday_radio_btn.setChecked(false);

				layoutResId = context.getResources().getIdentifier("lastbong_radio_btn", "id", context.getPackageName());
				final RadioButton lastbong_radio_btn = (RadioButton) ll.findViewById(layoutResId);

				lastbong_radio_btn.setTextColor(Color.WHITE);
				lastday_radio_btn.setTextColor(Color.rgb(17, 17, 17));

                COMUtil._mainFrame.bIsyJonggaCurrentPrice = false;
                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                base11.repaintAllChart();
            }
			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
			if(v.getId() == context.getResources().getIdentifier("zoom_radio_btn_normal", "id", context.getPackageName()))
			{
				int layoutResId = context.getResources().getIdentifier("zoom_radio_btn_detail", "id", context.getPackageName());
				final RadioButton zoom_radio_btn_detail = (RadioButton) ll.findViewById(layoutResId);
				zoom_radio_btn_detail.setChecked(false);

				COMUtil._mainFrame.bIsDetailScroll = false;
//				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//				base11.repaintAllChart();
			}
			else if(v.getId() == context.getResources().getIdentifier("zoom_radio_btn_detail", "id", context.getPackageName()))
			{
				int layoutResId = context.getResources().getIdentifier("zoom_radio_btn_normal", "id", context.getPackageName());
				final RadioButton zoom_radio_btn_normal = (RadioButton) ll.findViewById(layoutResId);
				zoom_radio_btn_normal.setChecked(false);

				COMUtil._mainFrame.bIsDetailScroll = true;
//				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//				base11.repaintAllChart();
			}

			if(v.getId() == context.getResources().getIdentifier("hubong_radio_btn", "id", context.getPackageName()))
			{
				int layoutResId = context.getResources().getIdentifier("silbong_radio_btn", "id", context.getPackageName());
				final RadioButton silbong_radio_btn = (RadioButton) ll.findViewById(layoutResId);
				silbong_radio_btn.setChecked(false);

				COMUtil._mainFrame.bIsUseHubong = true;
//				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//				base11.repaintAllChart();
				COMUtil.sendTR(""+COMUtil._TAG_SET_REALBONG);
			}
			else if(v.getId() == context.getResources().getIdentifier("silbong_radio_btn", "id", context.getPackageName()))
			{
				int layoutResId = context.getResources().getIdentifier("hubong_radio_btn", "id", context.getPackageName());
				final RadioButton hubong_radio_btn = (RadioButton) ll.findViewById(layoutResId);
				hubong_radio_btn.setChecked(false);

				COMUtil._mainFrame.bIsUseHubong = false;
//				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//				base11.repaintAllChart();
				COMUtil.sendTR(""+COMUtil._TAG_SET_REALBONG);
			}
			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<
			if(v.getId() == context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName()) ||
					v.getId() == context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName()))
//					v.getId() == R.id.bns_gbn_auto_radio_btn)	//2015. 3. 4 차트 테마 메인따라가기 추가

			{
				int layoutResId = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
				final RadioButton radioWhite = (RadioButton) ll.findViewById(layoutResId);
				layoutResId = context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName());
				final RadioButton radioBlack = (RadioButton) ll.findViewById(layoutResId);
				layoutResId = context.getResources().getIdentifier("bns_gbn_auto_radio_btn", "id", context.getPackageName());
				final RadioButton radioAuto = (RadioButton) ll.findViewById(layoutResId);

				radioWhite.setChecked(false);
				radioBlack.setChecked(false);
				radioAuto.setChecked(false);

				if (v.getTag().equals("500002")) { //white
					radioWhite.setTextColor(Color.WHITE);
					radioBlack.setTextColor(Color.rgb(17,17,17));
				} else { //black
					radioBlack.setTextColor(Color.WHITE);
					radioWhite.setTextColor(Color.rgb(17,17,17));
				}

				RadioButton rbSender = (RadioButton)v;
				rbSender.setChecked(true);

				COMUtil.setChartFromXML(v);

//				int layoutResId = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
//				RadioButton radioWhite = (RadioButton) ll.findViewById(layoutResId);
//				layoutResId = context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName());
//				RadioButton radioBlack = (RadioButton) ll.findViewById(layoutResId);
//
//				//2015. 3. 4 차트 테마 메인따라가기 추가>>
//				layoutResId = context.getResources().getIdentifier("bns_gbn_auto_radio_btn", "id", context.getPackageName());
//				RadioButton radioAuto = (RadioButton) ll.findViewById(layoutResId);
//				//2015. 3. 4 차트 테마 메인따라가기 추가<<
//
//				if(v.getId() == R.id.bns_gbn_white_radio_btn)
//				{
//					radioWhite.setTextColor(Color.rgb(203, 29, 118));
//					radioBlack.setTextColor(Color.rgb(21, 21, 21));
//					radioAuto.setTextColor(Color.rgb(21, 21, 21));	//2015. 3. 4 차트 테마 메인따라가기 추가
//				}
//				else if(v.getId() == R.id.bns_gbn_black_radio_btn)
//				{
//					radioBlack.setTextColor(Color.rgb(203, 29, 118));
//					radioWhite.setTextColor(Color.rgb(21, 21, 21));
//					radioAuto.setTextColor(Color.rgb(21, 21, 21));	//2015. 3. 4 차트 테마 메인따라가기 추가
//				}
//				//2015. 3. 4 차트 테마 메인따라가기 추가>>
//				else if(v.getId() == R.id.bns_gbn_auto_radio_btn)
//				{
//					radioAuto.setTextColor(Color.rgb(203, 29, 118));
//					radioWhite.setTextColor(Color.rgb(21, 21, 21));
//					radioBlack.setTextColor(Color.rgb(21, 21, 21));
//				}
				//2015. 3. 4 차트 테마 메인따라가기 추가<<
			}
		}
	};

	private void initPeriodEditTexts()
	{
		if(arPeriodBunEditText == null)
		{
			arPeriodBunEditText = new ArrayList<EditText>();
		}
		if(arPeriodTicEditText == null)
		{
			arPeriodTicEditText = new ArrayList<EditText>();
		}

		int layoutResId;
		for(int i = 1; i <= 7; i++)
		{
			String strId = "periodset_row1_edit_bun_" + String.valueOf(i);
			layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());
			EditText edit = (EditText)ll.findViewById(layoutResId);

			final EditText _edit = edit;

			edit.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{
						hideKeyPad(_edit);
					}
					return true;
				}
			});

			arPeriodBunEditText.add(edit);
		}

		for(int i = 1; i <= 7; i++)
		{
			String strId = "periodset_row1_edit_tic_" + String.valueOf(i);
			layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());
			EditText edit = (EditText)ll.findViewById(layoutResId);

			final EditText _edit = edit;

			edit.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{
						hideKeyPad(_edit);
					}
					return true;
				}
			});

			arPeriodTicEditText.add(edit);
		}


	}

	//문자 분석툴 사용시 키패드 감추기 위함 
	public void hideKeyPad(EditText edText)
	{
		if(edText != null) {
			InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
			edText.requestFocus();
		}
	}

	public void hideKeyPad_(View v)
	{
		InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		v.requestFocus();
	}

	//2013. 1.    지표설정창 탭 버튼 및 뷰 세팅 
	private void initTabViews()
	{
		//Buttons
		int layoutResId = context.getResources().getIdentifier("indicatorSetBtn", "id", context.getPackageName());
		final Button indicatorSetBtn = (Button)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("periodSetBtn", "id", context.getPackageName());
		final Button periodSetBtn = (Button)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("toolbarSetBtn", "id", context.getPackageName());
		final Button toolbarSetBtn = (Button)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("normalSetBtn", "id", context.getPackageName());
		final Button normalSetBtn = (Button)ll.findViewById(layoutResId);
		normalSetBtn.setSelected(true);
		normalSetBtn.setTypeface(COMUtil.typefaceBold);

		layoutResId = context.getResources().getIdentifier("linear_jipyolistbtn", "id", context.getPackageName());
		final LinearLayout linear_jipyolistbtn = (LinearLayout)ll.findViewById(layoutResId);

		//ScrollViews
		layoutResId = context.getResources().getIdentifier("indicatorscroll", "id", context.getPackageName());
		final ScrollView sv = (ScrollView)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("periodscroll", "id", context.getPackageName());
		final ScrollView svPeriod = (ScrollView)ll.findViewById(layoutResId);
		initPeriodValues();

		layoutResId = context.getResources().getIdentifier("toolbarSetView", "id", context.getPackageName());
		final ViewGroup toolbarView = (ViewGroup)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("normalscroll", "id", context.getPackageName());
		final ScrollView svNormal = (ScrollView)ll.findViewById(layoutResId);
		setNormalSetView();

		//2017. 8. 1 by hyh - 지표설정 UI 변경 >>
		layoutResId = context.getResources().getIdentifier("ll_jipyo_tab_view", "id", context.getPackageName());
		final LinearLayout llJipyoTabView = (LinearLayout)ll.findViewById(layoutResId);
		new JipyoTabViewController(context, llJipyoTabView, layout);
		sv.setVisibility(View.GONE);
		//2017. 8. 1 by hyh - 지표설정 UI 변경 <<
		llJipyoTabView.setVisibility(View.GONE);

		indicatorSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//2017. 8. 1 by hyh - 지표설정 UI 변경 >>
				llJipyoTabView.setVisibility(View.VISIBLE);
				//sv.setVisibility(View.VISIBLE);
				//2017. 8. 1 by hyh - 지표설정 UI 변경 <<

				svPeriod.setVisibility(View.GONE);
				toolbarView.setVisibility(View.GONE);
				svNormal.setVisibility(View.GONE);

				linear_jipyolistbtn.setVisibility(View.VISIBLE);	//2013. 8. 23 지표설정창 지표리스트 버튼 리니어레이아웃 가시성 처리 (지표탭 눌렀을때만 보이게)

				sv.post(new Runnable() {

					@Override
					public void run() {
						sv.scrollTo(0, 0); //맨위로 강제이동
					}
				});

				indicatorSetBtn.setTextColor(Color.rgb(17, 17, 17));
				normalSetBtn.setTextColor(Color.rgb(102, 102, 102));
				periodSetBtn.setTextColor(Color.rgb(102, 102, 102));
				toolbarSetBtn.setTextColor(Color.rgb(102, 102, 102));

				indicatorSetBtn.setSelected(true);
				indicatorSetBtn.setTypeface(COMUtil.typefaceBold);
				normalSetBtn.setSelected(false);
				normalSetBtn.setTypeface(COMUtil.typeface);
				periodSetBtn.setSelected(false);
				periodSetBtn.setTypeface(COMUtil.typeface);
				toolbarSetBtn.setSelected(false);
				toolbarSetBtn.setTypeface(COMUtil.typeface);
				hideKeyPad_(ll);
			}
		});

		normalSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linear_jipyolistbtn.setVisibility(View.GONE);	//2013. 8. 23 지표설정창 지표리스트 버튼 리니어레이아웃 가시성 처리 (지표탭 눌렀을때만 보이게)

				//2017. 8. 1 by hyh - 지표설정 UI 변경 >>
				llJipyoTabView.setVisibility(View.GONE);
				//sv.setVisibility(View.GONE);
				//2017. 8. 1 by hyh - 지표설정 UI 변경 <<

				svPeriod.setVisibility(View.GONE);
				toolbarView.setVisibility(View.GONE);
				svNormal.setVisibility(View.VISIBLE);

				indicatorSetBtn.setTextColor(Color.rgb(102, 102, 102));
				normalSetBtn.setTextColor(Color.rgb(17, 17, 17));
				periodSetBtn.setTextColor(Color.rgb(102, 102, 102));
				toolbarSetBtn.setTextColor(Color.rgb(102, 102, 102));

				indicatorSetBtn.setSelected(false);
				indicatorSetBtn.setTypeface(COMUtil.typeface);
				normalSetBtn.setSelected(true);
				normalSetBtn.setTypeface(COMUtil.typefaceBold);
				periodSetBtn.setSelected(false);
				periodSetBtn.setTypeface(COMUtil.typeface);
				toolbarSetBtn.setSelected(false);
				toolbarSetBtn.setTypeface(COMUtil.typeface);
				hideKeyPad_(v);
			}
		});

		periodSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linear_jipyolistbtn.setVisibility(View.GONE);    //2013. 8. 23 지표설정창 지표리스트 버튼 리니어레이아웃 가시성 처리 (지표탭 눌렀을때만 보이게)

				//2017. 8. 1 by hyh - 지표설정 UI 변경 >>
				llJipyoTabView.setVisibility(View.GONE);
				//sv.setVisibility(View.GONE);
				//2017. 8. 1 by hyh - 지표설정 UI 변경 <<

				svPeriod.setVisibility(View.VISIBLE);
				toolbarView.setVisibility(View.GONE);
				svNormal.setVisibility(View.GONE);

				indicatorSetBtn.setTextColor(Color.rgb(102, 102, 102));
				normalSetBtn.setTextColor(Color.rgb(102, 102, 102));
				periodSetBtn.setTextColor(Color.rgb(17, 17, 17));
				toolbarSetBtn.setTextColor(Color.rgb(102, 102, 102));

				indicatorSetBtn.setSelected(false);
				indicatorSetBtn.setTypeface(COMUtil.typeface);
				normalSetBtn.setSelected(false);
				normalSetBtn.setTypeface(COMUtil.typeface);
				periodSetBtn.setSelected(true);
				periodSetBtn.setTypeface(COMUtil.typefaceBold);
				toolbarSetBtn.setSelected(false);
				toolbarSetBtn.setTypeface(COMUtil.typeface);
				hideKeyPad_(v);

//				eventListener.onLoadPeriodValue();    //2015. 12. 11 map-chart 간 분틱값 동기화>>
			}
		});

		//분틱 초기화 버튼
		layoutResId = context.getResources().getIdentifier("indicator_btn_buntick_reset", "id", context.getPackageName());
		Button btn_Buntick_Reset = (Button)ll.findViewById(layoutResId);
		btn_Buntick_Reset.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 initPeriod();
			 }
		 });

		toolbarSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linear_jipyolistbtn.setVisibility(View.GONE);	//2013. 8. 23 지표설정창 지표리스트 버튼 리니어레이아웃 가시성 처리 (지표탭 눌렀을때만 보이게)

				//2017. 8. 1 by hyh - 지표설정 UI 변경 >>
				llJipyoTabView.setVisibility(View.GONE);
				//sv.setVisibility(View.GONE);
				//2017. 8. 1 by hyh - 지표설정 UI 변경 <<

				svPeriod.setVisibility(View.GONE);
				toolbarView.setVisibility(View.VISIBLE);
				svNormal.setVisibility(View.GONE);

				indicatorSetBtn.setTextColor(Color.rgb(102, 102, 102));
				normalSetBtn.setTextColor(Color.rgb(102, 102, 102));
				periodSetBtn.setTextColor(Color.rgb(102, 102, 102));
				toolbarSetBtn.setTextColor(Color.rgb(17, 17, 17));

				if(toolbarlist == null)
				{
					initToolbarList();
				}

				indicatorSetBtn.setSelected(false);
				indicatorSetBtn.setTypeface(COMUtil.typeface);
				normalSetBtn.setSelected(false);
				normalSetBtn.setTypeface(COMUtil.typeface);
				periodSetBtn.setSelected(false);
				periodSetBtn.setTypeface(COMUtil.typeface);
				toolbarSetBtn.setSelected(true);
				toolbarSetBtn.setTypeface(COMUtil.typefaceBold);
				hideKeyPad_(v);
			}
		});

		//분석툴바 초기화 버튼
		layoutResId = context.getResources().getIdentifier("indicator_btn_tool_reset", "id", context.getPackageName());
		Button btn_Tool_Reset = (Button)ll.findViewById(layoutResId);
		btn_Tool_Reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initAnalTool();
				initToolbarList();
			}
		});
	}

	public void initNormarSetValues()
	{
//    	COMUtil.bIsAdjustedStock = true;
//    	COMUtil.bIsyScaleShow = true;
//    	COMUtil.bIsyJonggaShow = true;
//    	COMUtil.bIsMinMaxShow = true;
//    	COMUtil.isSetScreenViewPanel = true;
//    	COMUtil.sendTR(""+COMUtil._TAG_SET_ADJUSTEDSTOCK);
		int layoutResId;
		//체크박스

		//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
//    	int nNormalChkCount = 6;
		int nNormalChkCount = 7; //2015.04.08 by lyk - 봉 개수 설정 옵션>> (2015.04.16 by lyk - 주기별 차트 설정 추가
//		for(int i = 0; i < 3; i++)
		for(int i = 0; i < nNormalChkCount; i++)
		//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
		{
			layoutResId = context.getResources().getIdentifier("normalset_check_"+String.valueOf(i+1), "id", context.getPackageName());
			CheckBox chkNormal = (CheckBox)ll.findViewById(layoutResId);

			layoutResId = context.getResources().getIdentifier("normalset_tv_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvNormal = (TextView)ll.findViewById(layoutResId);

			boolean bCheck = true;

			
			switch(i)
			{
				case 0:
					bCheck = COMUtil.isAdjustedStock();
					break;
				case 1:
					bCheck = COMUtil.isyScaleShow();
					break;
				case 2:
					bCheck = COMUtil.isyJonggaShow();
					break;
				case 3:
					bCheck = COMUtil.isMinMaxShow();
					break;
				//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
				case 4:
					bCheck = COMUtil.isChuseLineValueTextShow();		//추세선 그릴때 값 같이 표시 여부
					break;
				//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>
				//2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
				case 5:
					bCheck = COMUtil.isDayDivisionLineShow();		//분틱날짜구분선
					break;
                //2015. 12. 15 조회개수 설정 표시  일반설정에 UI 추가>>
                case 6:
                    bCheck = COMUtil.isBongCntShow();
                    break;
                //2015. 12. 15 조회개수 설정 표시  일반설정에 UI 추가<<
				//2015.04.16 by lyk - 주기별 차트 설정 옵션>>
				case 7:
					bCheck = COMUtil.isPeriodConfigSave();		//주기별 차트 설정
					break;
				//2015.04.16 by lyk - 주기별 차트 설정 옵션<<
                case 8:
                    bCheck = COMUtil.isUsePaddingRight();
                    break;
			}
			//2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>

			chkNormal.setChecked(bCheck);
			if(chkNormal.isChecked())
			{
	//			tvNormal.setTextColor(Color.rgb(239, 115, 28));
			}
			else
			{
				
				tvNormal.setTextColor(textColor);
			}
		}



		//2013. 8. 21 일반설정에서 화면설정(길게누름) 제외 >>
		//일반설정-라디오버튼		
//    	layoutResId = context.getResources().getIdentifier("screenset_valuesearch", "id", context.getPackageName());
//		RadioButton radioValueSearch = (RadioButton) ll.findViewById(layoutResId);
//		radioValueSearch.setTag("screenset_valuesearch");
//		radioValueSearch.setOnClickListener(radioListener);
//
//		layoutResId = context.getResources().getIdentifier("screenset_indicator", "id", context.getPackageName());
//		RadioButton radioIndicator = (RadioButton) ll.findViewById(layoutResId);
//		radioIndicator.setTag("screenset_indicator");
//		radioIndicator.setOnClickListener(radioListener);

//		if(COMUtil.isSetScreenViewPanel)
//		{
//			radioValueSearch.setChecked(true);
//			radioIndicator.setChecked(false);
//		}
//		else
//		{
//			radioValueSearch.setChecked(false);
//			radioIndicator.setChecked(true);
//		}
		//2013. 8. 21 일반설정에서 화면설정(길게누름) 제외 >>
	}

	public void initPeriodValues()
	{
		int layoutResId;

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		for(int i = 0; i < 2; i++){
			layoutResId = context.getResources().getIdentifier("period_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvPeriod = (TextView)ll.findViewById(layoutResId);
			tvPeriod.setTypeface(COMUtil.typefaceMid);
		}

		if(COMUtil.bIsForeignFuture) {
			periodui = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);
			for (int i = 0; i < 14; i++) {
				layoutResId = this.getContext().getResources().getIdentifier(String.valueOf(ids[i]), "id", context.getPackageName());
				LinearLayout ll = (LinearLayout)periodui.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("jugiset_row1_edit", "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
				int nValue = base11.astrPeriodData[i];
				editPeriod.setText(String.valueOf(nValue));

				final int nIdx = i;
				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
				Button btnDay = (Button)ll.findViewById(layoutResId);
				//기본값 세팅
				final String periodDay = "jugiset_row_day";
				int nBtn = base11.astrPeriodDayBtnData[i];
				if(nBtn == 1)
				{
					btnDay.setSelected(true);
					editPeriod.setText("일");
					editPeriod.setEnabled(false);
				}
				else
					btnDay.setSelected(false);
				btnDay.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodDay);
					}
				});

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
				Button btnWeek = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodWeekBtnData[i];
				final String periodWeek = "jugiset_row_week";
				if(nBtn == 1)
				{
					btnWeek.setSelected(true);
					editPeriod.setText("주");
					editPeriod.setEnabled(false);
				}
				else
					btnWeek.setSelected(false);
				btnWeek.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodWeek);
					}

				});

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
				Button btnMon = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodMonBtnData[i];
				final String periodMon = "jugiset_row_mon";
				if(nBtn == 1)
				{
					btnMon.setSelected(true);
					editPeriod.setText("월");
					editPeriod.setEnabled(false);
				}
				else
					btnMon.setSelected(false);
				btnMon.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodMon);
					}
				});

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
				Button btnMin = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodMinBtnData[i];
				if(nBtn == 1)
				{
					btnMin.setSelected(true);
				}
				else
					btnMin.setSelected(false);
				btnMin.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectMin(nIdx);
					}
				});

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
				Button btnTic = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodTikBtnData[i];
				if(nBtn == 1)
				{
					btnTic.setSelected(true);
				}
				else
					btnTic.setSelected(false);
				btnTic.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectTic(nIdx);
					}
				});
			}
		}
		else {
			//체크박스
			for(int i = 0; i < 8; i++) {
				layoutResId = context.getResources().getIdentifier("periodset_check_bun_" + String.valueOf(i + 1), "id", context.getPackageName());
				CheckBox chk = (CheckBox) ll.findViewById(layoutResId);

				int nChk = base11.astrMinCheckData[i];
				if (nChk == -1) {
					nChk = base11.astrMinCheckDefaultData[i];
				}
				if (nChk == 1) {
					chk.setChecked(true);
				} else
					chk.setChecked(false);

				layoutResId = context.getResources().getIdentifier("periodset_check_tic_" + String.valueOf(i + 1), "id", context.getPackageName());
				chk = (CheckBox) ll.findViewById(layoutResId);

				nChk = base11.astrTikCheckData[i];
				if (nChk == -1) {
					nChk = base11.astrTikCheckDefaultData[i];
				}
				if (nChk == 1) {
					chk.setChecked(true);
				} else
					chk.setChecked(false);

				if (i < 3) {

					layoutResId = context.getResources().getIdentifier("periodset_check_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
					chk = (CheckBox) ll.findViewById(layoutResId);

					nChk = base11.astrSecCheckData[i];
					if (nChk == -1) {
						nChk = base11.astrSecCheckDefaultData[i];
					}
					if (nChk == 1) {
						chk.setChecked(true);
					} else
						chk.setChecked(false);
				}
			}
			if (base11.astrMinCheckDefaultData.length > 8) {
				layoutResId = context.getResources().getIdentifier("periodset_check_bun_9", "id", context.getPackageName());
				CheckBox chk = (CheckBox) ll.findViewById(layoutResId);

				int nChk = base11.astrMinCheckData[8];
				if (nChk == -1) {
					nChk = base11.astrMinCheckDefaultData[8];
				}
				if (nChk == 1) {
					chk.setChecked(true);
				} else
					chk.setChecked(false);
			}

			//에디트텍스트
			for (int i = 0; i < 8; i++) {
				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_" + String.valueOf(i + 1), "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
//			int nValue = Integer.parseInt(COMUtil.bunPeriodList.get(i).get("title"));
				int nValue = base11.astrMinData[i];
				editPeriod.setText(String.valueOf(nValue));

				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_tic_" + String.valueOf(i + 1), "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
//			nValue = Integer.parseInt(COMUtil.ticPeriodList.get(i).get("title"));
				nValue = base11.astrTikData[i];
				editPeriod.setText(String.valueOf(nValue));

				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
				if (i < 3) {
					layoutResId = context.getResources().getIdentifier("periodset_row1_edit_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
					editPeriod = (EditText) ll.findViewById(layoutResId);
//			nValue = Integer.parseInt(COMUtil.ticPeriodList.get(i).get("title"));
					nValue = base11.astrSecData[i];
					editPeriod.setText(String.valueOf(nValue));
				}
				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
			}
			if (base11.astrMinData.length > 8) {
				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_9", "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
//			int nValue = Integer.parseInt(COMUtil.bunPeriodList.get(i).get("title"));
				int nValue = base11.astrMinData[8];
				editPeriod.setText(String.valueOf(nValue));
			}
		}
		layoutResId = context.getResources().getIdentifier("periodparentlinear", "id", context.getPackageName());
		LinearLayout linearPeriod = (LinearLayout)ll.findViewById(layoutResId);
		linearPeriod.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				hideKeyPad(editPeriod);    //2013. 8. 19 키보드닫기 모듈화해서 함수호출로 변경

			}
		});
	}


	public void selectPeriodBtn(int nIndex, String str)
	{
		boolean bcondition = getSelBtn(str);
		//2017.04.21 선택된 버튼의 상태에 따라 버튼 상태 변경. by PJM
		if(bcondition) {
			if (str.equals("jugiset_row_day"))
				selectDay(nIndex);
			else if(str.equals("jugiset_row_week"))
				selectWeek(nIndex);
			else if(str.equals("jugiset_row_mon"))
				selectMon(nIndex);
		}
		else {
			Toast.makeText(context, "일, 주, 월 주기는 하나만 선택이 가능합니다.", Toast.LENGTH_LONG).show();

		}
	}

	public void savePeriodValues()
	{
		int layoutResId;
		int[] arMin = 		new int[9];
		int[] arMinChk = 	new int[9];
		int[] arTic = 		new int[8];
		int[] arTicChk =	new int[8];
        //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
        int[] arSec = 		new int[3];
        int[] arSecChk =	new int[3];
        //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
		//2017.04.17 by PJM >> 해외선물 주기설정 변경
		int[] arPeriod = 		new int[14];
		int[] arPeriodChk = 		new int[14];
		int[] arDayBtn = 	new int[14];
		int[] arWeekBtn = 	new int[14];
		int[] arMonBtn = 	new int[14];
		int[] arMinBtn = 	new int[14];
		int[] arTikBtn = 	new int[14];

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		//에디트텍스트
		if(COMUtil.bIsForeignFuture) {
			for(int i = 0; i < 14; i++) {

				layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
				LinearLayout periodlinear = (LinearLayout)periodui.findViewById(layoutResId);

				layoutResId = periodlinear.getResources().getIdentifier(ids_name[i], "id", context.getPackageName());
				LinearLayout ll = (LinearLayout)periodlinear.findViewById(layoutResId);

				final int nIdx = i;

				layoutResId = context.getResources().getIdentifier("jugiset_row_day", "id", context.getPackageName());
				final String periodDay = "jugiset_row_day";
				final Button btnDay = (Button) ll.findViewById(layoutResId);
				btnDay.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodDay);
					}
				});

				arDayBtn[i] = (btnDay.isSelected() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("jugiset_row_week", "id", context.getPackageName());
				final String periodWeek = "jugiset_row_week";
				final Button btnWeek = (Button) ll.findViewById(layoutResId);
				btnWeek.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodWeek);
					}
				});
				arWeekBtn[i] = (btnWeek.isSelected() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("jugiset_row_mon", "id", context.getPackageName());
				final String periodMon = "jugiset_row_mon";
				final Button btnMon = (Button) ll.findViewById(layoutResId);
				btnMon.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectPeriodBtn(nIdx, periodMon);
					}
				});
				arMonBtn[i] = (btnMon.isSelected() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("jugiset_row_min", "id", context.getPackageName());
				final Button btnMin = (Button) ll.findViewById(layoutResId);
				btnMin.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectMin(nIdx);
					}
				});
				arMinBtn[i] = (btnMin.isSelected() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("jugiset_row_tic", "id", context.getPackageName());
				final Button btnTic = (Button) ll.findViewById(layoutResId);
				btnTic.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectTic(nIdx);
					}
				});
				arTikBtn[i] = (btnTic.isSelected() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("jugiset_row1_edit", "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
				String strPeriodData = editPeriod.getText().toString();
//				if (strPeriodData.equals("") || Integer.parseInt(strPeriodData) < 1) {
				if (strPeriodData.equals("") || strPeriodData.equals("0")) {
					strPeriodData = String.valueOf(base11.astrPeriodDefaultData[i]);
				}
				if(strPeriodData.equals("일"))
					strPeriodData = "0";
				else if(strPeriodData.equals("주"))
					strPeriodData = "0";
				else if(strPeriodData.equals("월"))
					strPeriodData = "0";
//				else if(strPeriodData.equals("") || Integer.parseInt(strPeriodData) == 0) {
//					strPeriodData = String.valueOf(base11.astrPeriodDefaultData[i]);
//					arPeriod[i] = Integer.parseInt(strPeriodData);
//				}
				arPeriod[i] = Integer.parseInt(strPeriodData);
			}
			COMUtil.setDayWeekMonMinTikPeriodValues(arPeriod, arPeriodChk, arDayBtn, arWeekBtn, arMonBtn, arMinBtn, arTikBtn);   //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
		}
		else {
			//체크박스
			for(int i = 0; i < 8; i++)
			{
				layoutResId = context.getResources().getIdentifier("periodset_check_bun_"+String.valueOf(i+1), "id", context.getPackageName());
				CheckBox chk = (CheckBox)ll.findViewById(layoutResId);
				arMinChk[i] = (chk.isChecked() == true) ? 1 : 0;

				layoutResId = context.getResources().getIdentifier("periodset_check_tic_"+String.valueOf(i+1), "id", context.getPackageName());
				chk = (CheckBox)ll.findViewById(layoutResId);
				arTicChk[i] = (chk.isChecked() == true) ? 1 : 0;

				if(i<3) {
					layoutResId = context.getResources().getIdentifier("periodset_check_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
					chk = (CheckBox) ll.findViewById(layoutResId);
					arSecChk[i] = (chk.isChecked() == true) ? 1 : 0;
				}

			}

			layoutResId = context.getResources().getIdentifier("periodset_check_bun_9", "id", context.getPackageName());
			CheckBox chk = (CheckBox)ll.findViewById(layoutResId);
			arMinChk[8] = (chk.isChecked() == true) ? 1 : 0;

			for (int i = 0; i < 8; i++) {
				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_" + String.valueOf(i + 1), "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
				String strMinData = editPeriod.getText().toString();
				if (strMinData.equals("") || Integer.parseInt(strMinData) < 1) {
					strMinData = String.valueOf(base11.astrMinDefaultData[i]);
				}
				arMin[i] = Integer.parseInt(strMinData);

				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_tic_" + String.valueOf(i + 1), "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
				String strTicData = editPeriod.getText().toString();
				if (strTicData.equals("") || Integer.parseInt(strTicData) < 1) {
					strTicData = String.valueOf(base11.astrTikDefaultData[i]);
				}
				arTic[i] = Integer.parseInt(strTicData);

				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
				if (i < 3) {
					layoutResId = context.getResources().getIdentifier("periodset_row1_edit_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
					editPeriod = (EditText) ll.findViewById(layoutResId);
					String strSecData = editPeriod.getText().toString();
					if (strSecData.equals("") || Integer.parseInt(strSecData) < 1) {
						strSecData = String.valueOf(base11.astrSecDefaultData[i]);
					}
					arSec[i] = Integer.parseInt(strSecData);
				}
				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
			}
			if (base11.astrMinDefaultData.length > 8) {
				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_9", "id", context.getPackageName());
				editPeriod = (EditText) ll.findViewById(layoutResId);
				String strMinData = editPeriod.getText().toString();
				if (strMinData.equals("") || Integer.parseInt(strMinData) < 1) {
					strMinData = String.valueOf(base11.astrMinDefaultData[8]);
				}
				arMin[8] = Integer.parseInt(strMinData);
			}

			COMUtil.setBunTicPeriodValues(arMin, arMinChk, arTic, arTicChk, arSec, arSecChk);   //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
		}


		if(toolbarlist != null)
		{
			//2014. 1. 17 더블탭 전체차트에서 도구설정 위치 바뀐 상태가 종합차트에 돌아왔을 땐 반영되지 않음>>
//			if(toolbarlist != null)
//			{
//				ArrayList<ToolbarItem> userItems = toolbarlist.getData();
//				for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
//					ToolbarItem item1 = userItems.get(i);
//					COMUtil.arrToolbarIndex[i] = item1.getTag();
//				}
//				base11.resetAnaltool();
//			}
			resetAnalTool();
			//2014. 1. 17 더블탭 전체차트에서 도구설정 위치 바뀐 상태가 종합차트에 돌아왔을 땐 반영되지 않음<<
		}

	}

	//2012. 7. 17  차트유형 데이터 추가 함수  - drfnkimsh 
	protected void setChartTypeList()
	{
		if(typeList.size()>0)
		{
			typeList.clear();
		}

		typeList.add(COMUtil.CANDLE_TYPE_CANDLE);
		typeList.add(COMUtil.CANDLE_TYPE_BAR);
		typeList.add(COMUtil.CANDLE_TYPE_BAR_OHLC);
		typeList.add(COMUtil.CANDLE_TYPE_LINE);
		typeList.add(COMUtil.CANDLE_TYPE_FLOW);
		typeList.add(COMUtil.CANDLE_TYPE_STAIR);
		typeList.add(COMUtil.CANDLE_TYPE_PF);
		typeList.add(COMUtil.CANDLE_TYPE_THIRD);
		typeList.add(COMUtil.CANDLE_TYPE_SWING);
		typeList.add(COMUtil.CANDLE_TYPE_RENKO);
		typeList.add(COMUtil.CANDLE_TYPE_KAGI);
		typeList.add(COMUtil.CANDLE_TYPE_RANGE_LINE);
		typeList.add(COMUtil.CANDLE_TYPE_REVERSECLOCK);
		typeList.add(COMUtil.CANDLE_TYPE_VARAINCE);
		typeList.add(COMUtil.CANDLE_TYPE_HEIKIN_ASHI);
		//2020.07.06 by LYH >> 캔들볼륨 >>
		typeList.add(COMUtil.CANDLE_TYPE_CANDLE_VOLUME);
		typeList.add(COMUtil.CANDLE_TYPE_EQUI_VOLUME);
		//2020.07.06 by LYH >> 캔들볼륨 <<

		if(typeTags.size()>0)
		{
			typeTags.clear();
		}
		Vector<Hashtable<String, String>> clTag = COMUtil.getChartListTag();

		typeTags.add(clTag.get(0).get("tag"));
		typeTags.add(clTag.get(1).get("tag"));
		typeTags.add(clTag.get(10).get("tag"));
		typeTags.add(clTag.get(2).get("tag"));
		typeTags.add(clTag.get(14).get("tag"));
		typeTags.add(clTag.get(18).get("tag"));
		typeTags.add(clTag.get(6).get("tag"));
		typeTags.add(clTag.get(5).get("tag"));
		typeTags.add(clTag.get(7).get("tag"));
		typeTags.add(clTag.get(8).get("tag"));
		typeTags.add(clTag.get(11).get("tag"));
		typeTags.add(clTag.get(12).get("tag"));
		typeTags.add(clTag.get(9).get("tag"));
		typeTags.add(clTag.get(13).get("tag"));
		typeTags.add(clTag.get(15).get("tag"));
		//2020.07.06 by LYH >> 캔들볼륨 >>
		typeTags.add(clTag.get(16).get("tag"));
		typeTags.add(clTag.get(17).get("tag"));
		//2020.07.06 by LYH >> 캔들볼륨 <<

	}

	class chartArrayAdapter extends BaseAdapter {
		Context context = null;
		ArrayList<JipyoChoiceItem> items;
		boolean bChecked = false;
		private ViewWrapper_charttype wrapper = null;
		private String m_strName;

		protected ArrayList<ViewWrapper_charttype> wrapperList_mini_selectlist = new ArrayList<ViewWrapper_charttype>();

		chartArrayAdapter(Context context, ArrayList<JipyoChoiceItem> items, String name)
		{
			this.context = context;
			this.items = items;
			this.m_strName = name;

			wrapperList_mini_selectlist.clear();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				int layoutResId = context.getResources().getIdentifier("jipyo_celltype_selectlist", "layout", context.getPackageName());

				// 2016.04.22 black 테마
//				if(COMUtil.skinType == COMUtil.SKIN_BLACK)
//				{
//					layoutResId = context.getResources().getIdentifier("jipyo_celltype_selectlist_black", "layout", context.getPackageName());
//				}
				// 2016.04.22 black 테마
				
				convertView = inflater.inflate(layoutResId, null);

				wrapper = new ViewWrapper_charttype(convertView);
				convertView.setTag(wrapper);

				wrapperList_mini_selectlist.add(wrapper);

				final JipyoChoiceItem oneItem = items.get(position);

				TextView tv = (TextView)wrapper.getCtrlCodeName();
                if(oneItem.getName().equals("PnF"))
				    tv.setText("P&F");
                else
                    tv.setText(oneItem.getName());
				//지표설정창의 캔들일 때는 설정 버튼 보이기
				if(m_strName.equals("charttype") && oneItem.getName().equals("캔들") && !m_bIsMiniPopup)
				{
					layoutResId = context.getResources().getIdentifier("tv_setjipyo", "id", context.getPackageName());
					TextView tv_setjipyo = (TextView)convertView.findViewById(layoutResId);
					tv_setjipyo.setVisibility(View.VISIBLE);
					tv_setjipyo.setId(0);
					tv_setjipyo.setTag(oneItem.getName());
                    tv_setjipyo.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showDetailView(v, "캔들");
                        }
                    });
                } else if(m_strName.equals("charttype") && oneItem.getName().equals("역시계곡선") && !m_bIsMiniPopup)
                {
                    layoutResId = context.getResources().getIdentifier("tv_setjipyo", "id", context.getPackageName());
                    TextView tv_setjipyo = (TextView)convertView.findViewById(layoutResId);
                    tv_setjipyo.setVisibility(View.VISIBLE);
                    tv_setjipyo.setId(0);
                    tv_setjipyo.setTag(oneItem.getName());
                    tv_setjipyo.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showDetailView(v, "역시계곡선");
                        }
                    });
                }

				final int _pos = position;
				CheckBox iv_Check = (CheckBox)wrapper.getRowCheck();
				iv_Check.setTag(typeTags.get(position));
				iv_Check.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
//						_convertView.onTouchEvent(event);
						for (int i = 0; i < wrapperList_mini_selectlist.size(); i++) {
							ViewWrapper_charttype wrapper_charttype = wrapperList_mini_selectlist.get(i);
//							wrapper_charttype.getRowCheck().setVisibility(View.GONE);
							wrapper_charttype.getRowCheck().setChecked(false);
//							wrapper_charttype.getCtrlCodeName().setTextColor(Color.rgb(46, 48, 51));
						}

						//wrapperList_mini_selectlist.get(_pos).getRowCheck().setVisibility(View.VISIBLE);
					//	wrapperList_mini_selectlist.get(_pos).getCtrlCodeName().setTextColor(textColor);
						wrapperList_mini_selectlist.get(_pos).getRowCheck().setChecked(true);
						if (m_strName.equals("charttype")) {
							COMUtil.setChartFromXML(wrapperList_mini_selectlist.get(_pos).getRowCheck());
						} else if (m_strName.equals("convenient_screenset")) {
							if (0 == _pos) {
								COMUtil._mainFrame.bIsSetScreenViewPanel = true;
							} else if (1 == _pos) {
								COMUtil._mainFrame.bIsSetScreenViewPanel = false;
							}
						}
						//2016. 1. 11 종가 등락률 옵션처리>>
						else if (m_strName.equals("convenient_chgrate")) {
							if (0 == _pos) {
								COMUtil._mainFrame.bIsyJonggaCurrentPrice = true;
							} else if (1 == _pos) {
								COMUtil._mainFrame.bIsyJonggaCurrentPrice = false;
							}
						}
						//2016. 1. 11 종가 등락률 옵션처리<<
					}
				});
				
				tv.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
//						_convertView.onTouchEvent(event);
						for (int i = 0; i < wrapperList_mini_selectlist.size(); i++) {
							ViewWrapper_charttype wrapper_charttype = wrapperList_mini_selectlist.get(i);
//							wrapper_charttype.getRowCheck().setVisibility(View.GONE);
							wrapper_charttype.getRowCheck().setChecked(false);
//							wrapper_charttype.getCtrlCodeName().setTextColor(Color.rgb(46, 48, 51));
						}

						//wrapperList_mini_selectlist.get(_pos).getRowCheck().setVisibility(View.VISIBLE);
						//wrapperList_mini_selectlist.get(_pos).getCtrlCodeName().setTextColor(textColor);
						wrapperList_mini_selectlist.get(_pos).getRowCheck().setChecked(true);
						if (m_strName.equals("charttype")) {
							COMUtil.setChartFromXML(wrapperList_mini_selectlist.get(_pos).getRowCheck());
						} else if (m_strName.equals("convenient_screenset")) {
							if (0 == _pos) {
								COMUtil._mainFrame.bIsSetScreenViewPanel = true;
							} else if (1 == _pos) {
								COMUtil._mainFrame.bIsSetScreenViewPanel = false;
							}
						}
						//2016. 1. 11 종가 등락률 옵션처리>>
						else if (m_strName.equals("convenient_chgrate")) {
							if (0 == _pos) {
								COMUtil._mainFrame.bIsyJonggaCurrentPrice = true;
							} else if (1 == _pos) {
								COMUtil._mainFrame.bIsyJonggaCurrentPrice = false;
							}
						}
						//2016. 1. 11 종가 등락률 옵션처리<<

						try {
							m_tv_charttype.setText(items.get(_pos).getName());
						} catch (Exception e) {

						}
					}
				});
				
				COMUtil.setGlobalFont((ViewGroup) convertView);

//				TextView tv = (TextView)wrapper.getCtrlCodeName();
//				CheckBox iv_Check = (CheckBox)wrapper.getRowCheck();
				//체크상태 확인해서 글씨색상 변경 및 오른쪽 체크 보이기여부 설정
//				if(position == nChartSelected)
				
				if(items.get(position).getCheck())
				{
					iv_Check.setChecked(true);
					//iv_Check.setVisibility(View.VISIBLE);
					//tv.setTextColor(Color.rgb(239, 115, 28));
				}
				else
				{
					iv_Check.setChecked(false);
					//iv_Check.setVisibility(View.GONE);
					//tv.setTextColor(Color.rgb(46, 48, 51));
				}
				convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(nIndicatorRowHeightValue)));
			}
//			else
//			{
//				wrapper = (ViewWrapper_charttype)convertView.getTag();
//			}

			

			return convertView;
		}
	}

	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의. 보조지표용 어댑터
	class MyArrayAdapter extends ArrayAdapter<JipyoChoiceItem> {

		// 생성자 내부에서 초기화
		private Context context;
		private ViewWrapper wrapper = null;
		private ArrayList<JipyoChoiceItem> mitems;
		private int m_nType = 0;
		private ArrayList<Boolean> m_itemChecked;

		private ArrayList<CheckBox> arChkBox = new ArrayList<CheckBox>();	//2015. 1. 13 차트유형 추가

		//2012. 8. 6 체크유무에 따라서 상세설정버튼 상태를 변경하기 위해 wrapper 클래스의 리스트 만들어서 참조함
		private ArrayList<ViewWrapper> m_wrapperList;
//	    private static final int gnSigChoiceViewCellTypeID = R.layout.jipyo_celltype_b;	///< 화면의 layout ID. 

		// 생성자
		MyArrayAdapter(Context context, ArrayList<JipyoChoiceItem> items, int nType) {
			super(context, context.getResources().getIdentifier("jipyo_celltype_b", "layout", context.getPackageName()), items);

			// instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
			this.context = context;
			this.mitems = items;
			if(nType == 1)
			{
				m_itemChecked = itemChecked2;
				m_wrapperList = wrapperList2;
			}
			else if(nType == 2)
			{
				m_itemChecked = itemChecked3;
				m_wrapperList = wrapperList3;
			}
			else if(nType == 3)
			{
				m_itemChecked = itemChecked4;
				m_wrapperList = wrapperList4;
			}
			//2015. 1. 13 차트유형 추가>>
			else if(nType == 4)
			{
				m_itemChecked = itemChecked5;
				m_wrapperList = wrapperList5;
			}
			//2015. 1. 13 차트유형 추가<<
			else
			{
				m_itemChecked = itemChecked;
				m_wrapperList = wrapperList;
			}
			this.m_nType = nType;
			m_wrapperList.removeAll(m_wrapperList);
//	        for (int i = 0; i < this.getCount(); i++) {
//	            itemChecked.add(i, false); // initializes all items value with false
//	        }
		}
		// ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.

		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;

			if(row == null) {
				// LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				//2012. 8. 17 리스트뷰 레이아웃 허니컴모드에 따라 다르게 읽게 수정  : I_tab18
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
					row = (View)inflater.inflate(context.getResources().getIdentifier("jipyo_celltype_b_tab", "layout", context.getPackageName()), null);
				}
				else
				{
					// inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
					row = (View)inflater.inflate(context.getResources().getIdentifier("jipyo_celltype_b", "layout", context.getPackageName()), null);
				}
				// test 2016.04.22
//				if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//					row = (View)inflater.inflate(context.getResources().getIdentifier("jipyo_celltype_b_black", "layout", context.getPackageName()), null);
//				}
				
				COMUtil.setGlobalFont((ViewGroup)row);

				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);

				//2012. 8. 6  wrapper 클래스의 리스트를 만들어서  체크버튼상태변경등을 할때 참조
				m_wrapperList.add(wrapper);

				//2012. 8. 7 참조를 위해  final 로 변경
				final JipyoChoiceItem oneItem = mitems.get(position);
				//2012. 12. 3  stateDrawable 주석처리후, 버튼xml 로 이용하는 방식으로 되돌리는 작업중, 아래 코드 주석처리 누락되어  체크박스 안보이는 현상 발생. 수정함.
//	        wrapper.getCtrlVisible().setButtonDrawable(stateDrawable);
				wrapper.getCtrlVisible().setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						boolean isChecked = cb.isChecked();
						int pos = v.getId();

						//2015. 1. 13 차트유형 추가>>
						if (4 == m_nType) {
							//체크박스를 모두 false 로 만들고
							m_itemChecked.clear();
							for (int i = 0; i < arChkBox.size(); i++) {
								arChkBox.get(i).setChecked(false);
								m_itemChecked.add(false);
							}

							//현재 차트유형에 해당하는 행의 체크박스만 체크표시한다.
							COMUtil.setChartFromXML(cb);
							arChkBox.get(pos).setChecked(true);
							m_itemChecked.set(pos, true);
		//					wrapper.getTv_SetJipyo().setTextColor(Color.rgb(239, 115, 28));

							//상세설정버튼 표출 여부. 캔들일때만 표시한다.
							//2015. 1. 13 차트유형 추가:  차트유형일 때는 변수설정 을 감춤>>
//    					if(0 == pos)
//    					{
//    						m_wrapperList.get(0).getTv_SetJipyo().setText("변수설정");
//    					}
//    					else
//    					{
//    						m_wrapperList.get(0).getTv_SetJipyo().setText("");
//    					}
							//2015. 1. 13 차트유형 추가:  차트유형일 때는 변수설정 을 감춤<<

							return;
						}
						//2015. 1. 13 차트유형 추가<<
						if(COMUtil.isMarketIndicator(oneItem.getName())) 
						{	
						//	if (!COMUtil._mainFrame.mainBase.baseP._chart._cdm.codeItem.strMarket.equals("0"))
							if (COMUtil._mainFrame.mainBase.baseP.nMarketType !=0) {
								DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
								alert.setTitle("");
								alert.setMessage("주식 지표는 주식에서만 조회가 가능합니다.");
								alert.setOkButton("확인", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
								alert.show();
								COMUtil.g_chartDialog = alert;
								cb.setChecked(false);
								return;
							}
						}
						
						if (IsNotAddIndicator(pos)) {
							cb.setChecked(false);
							return;
						}
						if (isChecked) {
							//2012. 7. 19  오버레이 펼친후 보조지표 펼치면 죽는 현상 해결 .
							m_itemChecked.set(pos, true);

							//2012. 8. 7 setJipyo 가 먼저 선행되야 해서 위치이동
							COMUtil.setJipyo(cb);

							//2012. 8. 6  상세버튼 숨기는 조건 추가  (interval, s_interval이 둘다 없거나 check 해제일때 )
							AbstractGraph abGraph = COMUtil._mainFrame.mainBase.baseP._chart.getGraph(oneItem.getName());

							String[] s_int = null;
							int[] inter = null;
							if (abGraph != null) {
								s_int = abGraph.s_interval;
								inter = abGraph.interval;

								if ((s_int != null && inter != null) &&
										m_itemChecked.get(position)) {
									//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//	        		    		wrapperList.get(pos).getCtrlDetail().setVisibility(View.VISIBLE);
//	        		    		m_wrapperList.get(pos).getTv_SetJipyo().setText("변수설정");
									m_wrapperList.get(pos).getTv_SetJipyo().setVisibility(View.VISIBLE);
									//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
								}
								//2012. 11. 15  보조지표의 거래량일 경우는 상세지표버튼 보이게 세팅 : I98
								//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
								else if (COMUtil.getAddJipyoTitle(abGraph.graphTitle).equals("거래량"))
								//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end
								{
									//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//	        		    		wrapperList.get(pos).getCtrlDetail().setVisibility(View.VISIBLE);
//	        		    		m_wrapperList.get(pos).getTv_SetJipyo().setText("변수설정");
									m_wrapperList.get(pos).getTv_SetJipyo().setVisibility(View.VISIBLE);
									//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
								}
							}

//							m_wrapperList.get(pos).getCtrlCodeName().setTextColor(Color.rgb(239, 115, 28));

						} else if (!isChecked) {
							//2012. 7. 19  오버레이 펼친후 보조지표 펼치면 죽는 현상 해결 .
							m_itemChecked.set(pos, false);
							//2012. 8. 7 setJipyo 가 먼저 선행되야 해서 위치이동
							COMUtil.setJipyo(cb);

							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>> : 공백으로 변경이유 -> 변수설정 글자의 가시성 유무와 관계없이 좌측 구분선 표시되야함
//    		    		wrapperList.get(pos).getCtrlDetail().setVisibility(View.GONE);
//	                	m_wrapperList.get(pos).getTv_SetJipyo().setText("");
							m_wrapperList.get(pos).getTv_SetJipyo().setVisibility(View.INVISIBLE);
							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
							// do some operations here

						
						}
//		        	try {

						//미니지표설정창이면 상세설정을 무조건 숨김
						if (m_bIsMiniPopup) {
							m_wrapperList.get(pos).getTv_SetJipyo().setVisibility(View.GONE);
							m_wrapperList.get(pos).getTv_AddOrDelJipyo().setVisibility(View.GONE);
							
							}

					}
				});

				//2015. 5. 12 지표설정창 체크버튼 터치영역 개선>>
				wrapper.getCtrlVisibleBackView().setTag(String.format("%d", 1000+position));
				wrapper.getCtrlVisibleBackView().setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						int nPos = Integer.parseInt((String)v.getTag()) - 1000;
						m_wrapperList.get(nPos).getCtrlVisible().performClick();
					}
				});
				//2015. 5. 12 지표설정창 체크버튼 터치영역 개선<<
				//2012. 8. 7 참조를 위해  final 로 변경
				//final JipyoChoiceItem oneItem = mitems.get(position);

				//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
//		        wrapper.getCtrlCodeName().setText(COMUtil.getAddJipyoTitle(oneItem.getName()));

				int index = oneItem.getName().indexOf(COMUtil.JIPYO_ADD_REMARK);
				if(index>0) {
					//중복지표는 왼쪽 패딩 추가
					wrapper.getCtrlCodeName().setText(COMUtil.getAddJipyoTitle(oneItem.getName()));
					wrapper.base.setPadding(wrapper.base.getPaddingLeft() + (int)COMUtil.getPixel(18),
											wrapper.base.getPaddingTop(),
											wrapper.base.getPaddingRight(),
											wrapper.base.getPaddingBottom());

				} else {
//		        	wrapper.getCtrlCodeName().setTextColor(Color.rgb(0,0,0));
                    if(oneItem.getName().equals("미결제약정")||oneItem.getName().equals("매수매도 거래량"))
                        wrapper.getCtrlCodeName().setText(oneItem.getName()+"(주식)");
                    else
					    wrapper.getCtrlCodeName().setText(COMUtil.getAddJipyoTitle(oneItem.getName()));
				}

				//wrapper.getCtrlCodeName().setText(oneItem.getName());
				//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

				//2015. 1. 13 중복지표 추가 UI 수정>>
				if(currentType.equals("jipyo")	 && !oneItem.getName().equals("주가이동평균")
						&& !oneItem.getName().equals("거래량")
						&& !oneItem.getName().equals("거래량이동평균")
						&& !oneItem.getName().equals("미결제약정")
						&& !oneItem.getName().equals("매수매도 거래량"))		//보조지표만 중복지표 추가/삭제 버튼을 보인다
				{
					//중복지표면 - 로 이미지 변경 및 tag 추가
					if(index>0)
					{
//		        		wrapper.getTv_AddOrDelJipyo().setText("삭제");
						int layoutResId = context.getResources().getIdentifier("btn_duplicatedindicator_minus_change", "drawable", context.getPackageName());
						wrapper.getTv_AddOrDelJipyo().setBackgroundResource(layoutResId);
						wrapper.getTv_AddOrDelJipyo().setTag("삭제");
					}
					else
					{
						//중복지표 아니면  + 로 이미지 변경 및 tag 추가
						int layoutResId = context.getResources().getIdentifier("btn_duplicatedindicator_plus_change", "drawable", context.getPackageName());
						wrapper.getTv_AddOrDelJipyo().setBackgroundResource(layoutResId);
						wrapper.getTv_AddOrDelJipyo().setTag("중복\n추가");
					}

					//보조지표(기술적지표) 에만 추가가 보인다
					//2015. 1. 13 by lyk 중복지표 추가/삭제 처리
					wrapper.getTv_AddOrDelJipyo().setId(position);
					//2015. 1. 13 by lyk 중복지표 추가/삭제 처리 end
					wrapper.getTv_AddOrDelJipyo().setVisibility(View.VISIBLE);
					wrapper.getTv_AddOrDelJipyo().setOnClickListener(new Button.OnClickListener() {
						public void onClick(View v) {
							//중복지표 추가 이벤트 넣을 곳
							TextView tv = (TextView)v;

							//2015. 1. 13 by lyk 중복지표 추가/삭제 처리
//			        		if(tv.getText().toString().equals("중복\n추가"))
							if(tv.getTag().equals("중복\n추가"))
							{
								//중복지표 추가
								addAddJipyoItem(tv);
							}
							else
							{
								//중복지표 삭제
								delAddJipyoItem(tv);
							}
							//2015. 1. 13 by lyk 중복지표 추가/삭제 처리 end
						}
					});
				}
				else
				{
					wrapper.getTv_AddOrDelJipyo().setVisibility(View.GONE);
				}
				//2015. 1. 13 중복지표 추가 UI 수정<<

				wrapper.getCtrlVisible().setTag(oneItem.getTag());
				wrapper.getCtrlVisible().setId(position);
//		        wrapper.getCtrlVisible().setChecked(oneItem.getCheck());
				//2012. 7. 19  오버레이 펼친후 보조지표 펼치면 죽는 현상 해결 .
				wrapper.getCtrlVisible().setChecked(m_itemChecked.get(position));

				arChkBox.add(wrapper.getCtrlVisible());		//2015. 1. 13 차트유형 추가



				//2012.7.20 상세지표설정 값이 없는 '거래량' 의 상세지표버튼 감춤 -> 2012. 8. 6 interval 로 판별하고 감추는 것으로 수정하여 주석처리
//		        if(wrapper.getCtrlCodeName().getText().toString().equals("거래량"))
//		        {
//		        	wrapper.getCtrlDetail().setVisibility(View.GONE);
//		        }

				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//		        wrapper.getCtrlDetail().setId(position);
//		        wrapper.getCtrlDetail().setTag(oneItem.getName());
//		        wrapper.getCtrlDetail().setOnClickListener(new Button.OnClickListener() {
//		        	//2012.7.17 체크안된 보조지표의 상세지표설정창 오픈 안되게 변경   7.18  오버레이지표쪽 기능 검사하면서 보조지표와 충돌나는 현상 방지. 
//		        	public void onClick(View v) {
//		        		//2012. 8. 7  상세지표 이동 조건 추가 : 체크되어있고  상세지표버튼이 VISIBLE 일때만 
//		        		if(	itemChecked.get(v.getId()) && wrapperList.get(position).getCtrlDetail().getVisibility() == View.VISIBLE)
//		        		{
//		        			showDetailView(v, "jipyo");
//		        		}
//			        }
//		        });

				//2015. 1. 13 차트유형 추가:  차트유형일 때는 변수설정 을 감춤>>
				if(4 == m_nType)
				{
					wrapper.getTv_SetJipyo().setVisibility(View.INVISIBLE);
				}
				//2015. 1. 13 차트유형 추가:  차트유형일 때는 변수설정 을 감춤<<
				wrapper.getTv_SetJipyo().setId(position);
				wrapper.getTv_SetJipyo().setTag(oneItem.getName());
			
				//체크상태 확인해서 글씨색상 변경
				if(wrapper.getCtrlVisible().isChecked())
				{
			//		wrapper.getCtrlCodeName().setTextColor(Color.rgb(239, 115, 28));
				}
				else
				{
					wrapper.getCtrlCodeName().setTextColor(textColor);
				}

				wrapper.getTv_SetJipyo().setOnClickListener(new Button.OnClickListener() {
					//2012.7.17 체크안된 보조지표의 상세지표설정창 오픈 안되게 변경   7.18  오버레이지표쪽 기능 검사하면서 보조지표와 충돌나는 현상 방지.
					public void onClick(View v) {
						//2012. 8. 7  상세지표 이동 조건 추가 : 체크되어있고  상세지표버튼이 VISIBLE 일때만
						//2013. 8. 30 지표설정창 체크된 지표만 상세설정 이동 (기존사항으로)>>
						if(	m_itemChecked.get(v.getId()))
						{
							if(m_nType==1)
								showDetailView(v, "overlay");
							else if(m_nType==2)
								showDetailView(v, "other");
							else if(m_nType==3)
								showDetailView(v, "total");
								//2015. 1. 13 차트유형 추가>>
							else if(m_nType==4)
								showDetailView(v, "캔들");
								//2015. 1. 13 차트유형 추가<<
							else
								showDetailView(v, "jipyo");
						}
						//2013. 8. 30 지표설정창 체크된 지표만 상세설정 이동 (기존사항으로)>>
					}
				});
				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>

				//2012. 8. 6  상세버튼 숨기는 조건 추가  (interval, s_interval이 둘다 없거나 check 해제일때 )
				final AbstractGraph abGraph = COMUtil._mainFrame.mainBase.baseP._chart.getGraph(oneItem.getName());

				String[] s_int = null;
				int[] inter = null;
				// abGraph가 있다면 현재 차트영역에 표시되고 있음으로 표시한다.
				if(abGraph != null)
				{
					s_int = abGraph.s_interval;
					inter = abGraph.interval;

					//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//	    	        wrapper.getCtrlDetail().setVisibility(View.VISIBLE);
					wrapper.getTv_SetJipyo().setVisibility(View.VISIBLE);
					//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>

					//만약, abGraph 가 있는데  상세지표값 변수나 굵기값등이 없는 경우라면  이때도 표시하지 않는다 (ex: 거래량)
					if(s_int == null || inter == null)
					{
						//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
						//2012. 8. 27  보조지표의 거래량일 경우는 상세지표버튼 보이게 세팅 : I98
						if(COMUtil.getAddJipyoTitle(abGraph.graphTitle).equals("거래량"))
						//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end
//	    	        	if(abGraph.graphTitle.equals("거래량"))
						{
							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//	    	    	        wrapper.getCtrlDetail().setVisibility(View.VISIBLE);
//	    	    	        wrapper.getTv_SetJipyo().setText("변수설정");
							wrapper.getTv_SetJipyo().setVisibility(View.VISIBLE);
							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
						}
						else
						{
							//거래량 이외에 interval, s_interval 이  null 일수도 있으므로 else 로 따로 영역 구분해둠
							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>> : invisible 로 변경이유 -> 변수설정 글자의 가시성 유무와 관계없이 좌측 구분선 표시되야함
//	    	    	        wrapper.getCtrlDetail().setVisibility(View.GONE);
//	    	        		wrapper.getTv_SetJipyo().setText("");
							wrapper.getTv_SetJipyo().setVisibility(View.INVISIBLE);
							//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
						}
					}
				}
				//abGraph 가 없다면 차트영역에 존재하지 않으므로 표시하지 않는다
				//2015. 1. 13 차트유형 추가 : 차트유형일 때는 변수설정 을 감춤>>
//		    	else if(oneItem.getName().equals("캔들") && wrapper.getCtrlVisible().isChecked())
//		    	{
//		    		wrapper.getTv_SetJipyo().setText("변수설정");
//		    	}
				//2015. 1. 13 차트유형 추가: 차트유형일 때는 변수설정 을 감춤<<
				else
				{
					//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>> : invisible 로 변경이유 -> 변수설정 글자의 가시성 유무와 관계없이 좌측 구분선 표시되야함
//	    	        wrapper.getCtrlDetail().setVisibility(View.GONE);
//		    		wrapper.getTv_SetJipyo().setText("        ");
					wrapper.getTv_SetJipyo().setVisibility(View.INVISIBLE);
					//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
				}

				//2012. 7. 30  리스트뷰의 각 지표행을 클릭햇을때  체크가 되어있으면 상세설정창으로 이동
				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//		        final View layoutView = wrapper.getCtrlDetail();
				final View layoutView = wrapper.getTv_SetJipyo();
				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
//		        final TextView layoutText = wrapper.getCtrlCodeName();
				row.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						//2012. 8. 7  상세지표 이동 조건 추가 : 체크되어있고  상세지표버튼이 VISIBLE 일때만
//		        		if(	itemChecked.get( position ) && wrapperList.get(position).getCtrlDetail().getVisibility() == View.VISIBLE)
//		        		{
						//2012. 8. 27  거래량일때의 상세지표설정 이동 : I98
//			        		showDetailView(layoutView, "jipyo");   //2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : 행 터치시 상세설정 안 들어가도록>>
//		        		}
					}
				});

				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>
				final View _row = row;
				final CheckBox _getCtrlVisible = (CheckBox)wrapper.getCtrlVisible();
				wrapper.getCtrlCodeName().setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						//행 터치 이벤트 강제전달. 여기서 체크버튼 이벤트까지 처리하게 되면 드래그할 때 체크버튼이 눌려진다. 
						_row.onTouchEvent(event);
						return false;
					}
				});
				wrapper.getCtrlCodeName().setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//'누름' 이벤트 발생시에만 체크버튼 강제눌림을 실행
						_getCtrlVisible.performClick();
					}
				});
				//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경>>

				// 커스터마이징 된 View 리턴.
				//2012. 7. 23 지표설정창의  각행 높이  조절

				//2013. 8. 23 지표리스트 행 높이 조절
//		        row.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, (int)COMUtil.getPixel(40)));
				row.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, (int)COMUtil.getPixel(nIndicatorRowHeightValue)));
				//2013. 8. 23 지표리스트 행 높이 조절

				//미니지표설정창이면 중복지표버튼, 상세설정버튼을 감춘다
				if(m_bIsMiniPopup)
				{
					wrapper.getTv_AddOrDelJipyo().setVisibility(View.GONE);
					wrapper.getTv_SetJipyo().setVisibility(View.GONE);
				}
			}
//			else {
//				wrapper = (ViewWrapper)row.getTag();
//			}

			return row;

		}
	}

	//
	// Holder Pattern을 구현하는 ViewWrapper 클래스
	//

	class ViewWrapper_charttype{
		private View base;
		private TextView  ctlCodeName;
		//private ImageView m_iv_rowCheck;
		private CheckBox m_iv_rowCheck;

		ViewWrapper_charttype(View base) {
			this.base = base;
		}

		View getBaseView()
		{
			return base;
		}

		TextView getCtrlCodeName() {
			if(ctlCodeName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_TextViewA01", "id", this.base.getContext().getPackageName());
				ctlCodeName = (TextView)base.findViewById(layoutResId);
			}
			return ctlCodeName;
		}

//		ImageView getRowCheck()
//		{
//			if(m_iv_rowCheck == null)
//			{
//				int layoutResId = this.base.getContext().getResources().getIdentifier("indicatormini_row_check", "id", this.base.getContext().getPackageName());
//				m_iv_rowCheck = (ImageView)base.findViewById(layoutResId);
//			}
//			return m_iv_rowCheck;
//		}
		CheckBox getRowCheck() {
			if(m_iv_rowCheck == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("indicatormini_row_check", "id", this.base.getContext().getPackageName());
				m_iv_rowCheck = (CheckBox)base.findViewById(layoutResId);
			}
			return m_iv_rowCheck;
		}
	}

	class ViewWrapper {
		private View base;
		private TextView  ctlCodeName;
		//		 private ImageView ctlDetail;		//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : wrapper클래스에 ImageView 대신 '변수설정' TextView 대체 >>
		private CheckBox ctlVisible;
		//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : wrapper클래스에 ImageView 대신 '지표설정' TextView 대체 >>
		private TextView  tv_SetJipyo;
		//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : wrapper클래스에 ImageView 대신 '지표설정' TextView 대체 >>

		private FrameLayout ctlVisibleBackView;	//2015. 5. 12 지표설정창 체크버튼 터치영역 개선

		//2015. 1. 13 중복지표 추가 UI 수정>>
		private TextView tv_AddOrDelJipyo;
		//2015. 1. 13 중복지표 추가 UI 수정<<

		//2012. 10. 2  체크박스 이미지 변경  : I106
		Drawable press, normal;

		StateListDrawable stateDrawable;

		ViewWrapper(View base) {
			this.base = base;
		}

		TextView getCtrlCodeName() {
			if(ctlCodeName == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_TextViewA01", "id", this.base.getContext().getPackageName());
				ctlCodeName = (TextView)base.findViewById(layoutResId);
			}
			return ctlCodeName;
		}

		CheckBox getCtrlVisible() {
			if(ctlVisible == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("usf_CheckBoxB01", "id", this.base.getContext().getPackageName());
				ctlVisible = (CheckBox)base.findViewById(layoutResId);
			}
			return ctlVisible;
		}

		//2015. 5. 12 지표설정창 체크버튼 터치영역 개선>>
		FrameLayout getCtrlVisibleBackView() {
			if(ctlVisibleBackView == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_TextViewA01_back", "id", this.base.getContext().getPackageName());
				ctlVisibleBackView = (FrameLayout)base.findViewById(layoutResId);
			}
			return ctlVisibleBackView;
		}
		//2015. 5. 12 지표설정창 체크버튼 터치영역 개선<<

		//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : wrapper클래스에 ImageView 대신 '변수설정' TextView 대체 >>
//		 ImageView getCtrlDetail() {
//		     if(ctlDetail == null) {
//		    	 int layoutResId = this.base.getContext().getResources().getIdentifier("fctb_ImageViewA01", "id", this.base.getContext().getPackageName());
//		    	 ctlDetail = (ImageView)base.findViewById(layoutResId);
//		     }          
//		     return ctlDetail;          
//		 }	

		TextView getTv_SetJipyo() {
			if(tv_SetJipyo == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("tv_setjipyo", "id", this.base.getContext().getPackageName());
				tv_SetJipyo = (TextView)base.findViewById(layoutResId);
			}
			return tv_SetJipyo;
		}
		//2013. 8. 19 지표설정창 지표설정 리스트 터치 변경 : wrapper클래스에 ImageView 대신 '변수설정' TextView 대체 >>

		//2015. 1. 13 중복지표 추가 UI 수정>>
		TextView getTv_AddOrDelJipyo() {
			if(tv_AddOrDelJipyo == null) {
				int layoutResId = this.base.getContext().getResources().getIdentifier("tv_add_or_del_jipyo", "id", this.base.getContext().getPackageName());
				tv_AddOrDelJipyo = (TextView)base.findViewById(layoutResId);
			}
			return tv_AddOrDelJipyo;
		}
		//2015. 1. 13 중복지표 추가 UI 수정<<
	}
	//2012. 8. 6   오버레이 / 보조지표에 따라서  datas 의 값이 잘못 들어갈 수도 있어서 구분자 파라미터인 String strCase 추가 
	public DetailJipyoController detailJipyoView = null;
	public void showDetailView(View v, String strCase) {
		int position = v.getId();
		ArrayList<JipyoChoiceItem> datas = null;
		//2012. 8. 6 오버레이 / 보조지표가 isShown 되는 것에 따라서 구별하면  datas 의 값이 잘못 들어갈 수도 있어서 구분자 파라미터인 String strCase 로 구분처리 
//		if(jipyolist.isShown()) {
//			datas = m_itemsArr;
//		} else if(overlaylist.isShown()){
//			datas = m_itemsArr2;
//		} else {
//			return;
//		}

		if(strCase.equals("jipyo"))
		{
			datas = m_itemsArr;
		}
		else if(strCase.equals("overlay"))
		{
			datas = m_itemsArr2;
		}
		else if(strCase.equals("other"))
		{
			datas = m_itemsArr3;
		}
		else
		{
			datas = m_itemsArr4;
		}

		JipyoChoiceItem oneItem;
		if(datas != null)
			oneItem = datas.get(position);
		else
			oneItem = new JipyoChoiceItem("20001", COMUtil.CANDLE_TYPE_CANDLE, false);

//		String textTochange = "지표상세 설정화면이동";
//    	COMUtil.showMessage(this.getContext(), textTochange); //Context, String msg

		detailJipyoView = new DetailJipyoController(this.getContext(), this.layout);
		detailJipyoView.setTag("detailJipyo");
		String tag = (String)v.getTag();

		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
//        int index = tag.indexOf(COMUtil.JIPYO_ADD_REMARK);
//        String strJipyoName = tag;
//        if(index!=-1) {
//        	strJipyoName = tag.substring(0, index);
//        }
		detailJipyoView.setTitle(tag);
		//detailJipyoView.setTitle(tag);
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

		detailJipyoView.setUI();

		if(strCase.equals("캔들"))
		{
			detailJipyoView.setInitGraph("일본식봉");
        } else if(strCase.equals("역시계곡선"))
        {
            detailJipyoView.setInitGraph("역시계곡선");
        }
		else
		{
			detailJipyoView.setInitGraph(oneItem.getName());
		}

		COMUtil.setGlobalFont(this.layout);
	}

	//2015. 1. 13 by lyk - 동일지표 추가 (리스트갱신)
	public void refreshList() {
		if(currentType.equals("") || currentType.equals("charttype")) {
			currentType = "jipyo";
		}
//		System.out.println("Debug_currentType:"+currentType);
//		makeItems(currentType);
		int layoutResId;
		if(currentType.equals("total")) {
			layoutResId = context.getResources().getIdentifier("totalList", "id", context.getPackageName());
			totallist = (ListView)ll.findViewById(layoutResId);
			m_itemsArr4 = FindDataManager.getUserJipyoItems4();

			makeItems("total");

			//2013. 8. 23 지표리스트 행 높이 조절 
			totallist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
					(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));

			layoutResId = context.getResources().getIdentifier("totalLayout", "id", context.getPackageName());
			RelativeLayout totalLayout = (RelativeLayout)ll.findViewById(layoutResId);

			totalLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));

			m_scvAdapter4 = new MyArrayAdapter(context, m_itemsArr4, 3);
			totallist.setAdapter(m_scvAdapter4);

		} else if(currentType.equals("other")) {
			layoutResId = context.getResources().getIdentifier("otherList", "id", context.getPackageName());
			otherlist = (ListView)ll.findViewById(layoutResId);
			m_itemsArr3 = FindDataManager.getUserJipyoItems3();

			makeItems("other");

			//2013. 8. 23 지표리스트 행 높이 조절 
			otherlist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
					(m_itemsArr3.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));

			layoutResId = context.getResources().getIdentifier("otherLayout", "id", context.getPackageName());
			RelativeLayout otherLayout = (RelativeLayout)ll.findViewById(layoutResId);

			otherLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					(m_itemsArr3.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));

			m_scvAdapter3 = new MyArrayAdapter(context, m_itemsArr3, 2);
			otherlist.setAdapter(m_scvAdapter3);
		} else if(currentType.equals("jipyo")) {

			layoutResId = context.getResources().getIdentifier("jipyoList", "id", context.getPackageName());
			jipyolist = (ListView)ll.findViewById(layoutResId);
			m_itemsArr = FindDataManager.getUserJipyoItems();

			makeItems("jipyo");

			//2013. 8. 23 지표리스트 행 높이 조절
			jipyolist.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr.size()*(int)COMUtil.getPixel(1)))));
			//2013. 8. 23 지표리스트 행 높이 조절 

			layoutResId = context.getResources().getIdentifier("jipyoLayout", "id", context.getPackageName());
			RelativeLayout jipyoLayout = (RelativeLayout)ll.findViewById(layoutResId);
			//2013. 8. 23 지표리스트 행 높이 조절
			jipyoLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr.size()*(int)COMUtil.getPixel(1)))));
			//2013. 8. 23 지표리스트 행 높이 조절

			m_scvAdapter = new MyArrayAdapter(context, m_itemsArr, 0);
			jipyolist.setAdapter(m_scvAdapter);
		}

		//2015. 1. 13 by lyk - 동일지표 추가리스트에 없는 지표 삭제 
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		Vector<String> list = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();

		Vector<String> graphItems = new Vector<String>();
		for(int i=0; i<list.size(); i++) {
			int index = list.get(i).indexOf(COMUtil.JIPYO_ADD_REMARK);
			if(index>0) {
				graphItems.add(list.get(i));
			}
		}

		//추가지표 리스트에 없는 지표를 찾아 삭제한다.
		if(graphItems.size()>0) {
			for(int k=0; k<graphItems.size(); k++) {
				String graphName = graphItems.get(k);
				boolean hasGraph = false;
				if(addItems.size()>0) {
					for(int i=0; i<addItems.size(); i++) {
						if(graphName.equals(addItems.get(i).get("name"))) {
							hasGraph = true;
							break;
						}
					}
				}

				if(hasGraph==false) {
					COMUtil._mainFrame.mainBase.removeIndicatorConfig(graphName);
				}
			}
		}
		//2015. 1. 13 by lyk - 동일지표 추가리스트에 없는 지표 삭제 end
	}
	//2015. 1. 13 by lyk - 동일지표 추가 (리스트갱신) end

	//2015. 1. 13 by lyk - 동일지표 추가목록에서 상세설정 바로가기 처리
	String strTagForAddJipyo = "";
	String strNameForAddJipyo = "";
	public void showDetailForAddJipyo(Hashtable<String, String> addItem) {
		//index 찾기 
		if(indicatorManager!=null) {
			indicatorManager.close();
		}

		strTagForAddJipyo = (String)addItem.get("tag");
		strNameForAddJipyo = (String)addItem.get("name");
		int addTag = Integer.parseInt((String)addItem.get("tag"));

		int pos = 0;
		for(int k=0; k<jipyoItems.size(); k++) {
			Hashtable<String, String> item = (Hashtable<String, String>)jipyoItems.get(k);
			int itemTag = Integer.parseInt((String)item.get("tag"));
			if(addTag == itemTag) {
				pos = k;
				break;
			}
		}

		jipyolist.performItemClick(jipyolist, pos, jipyolist.getItemIdAtPosition(pos));
	}
	//2015. 1. 13 by lyk - 동일지표 추가 (리스트갱신) end

	protected void makeItems(String type) {
		currentType = type;
		Vector<String> list = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();

		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
		jipyoItems = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		//2015. 1. 13 by lyk - 동일지표(타입끼리 묶이도록 처리 (tag 비교))
		for(int i=0; i<addItems.size(); i++) {
			Hashtable<String, String> addItem = (Hashtable<String, String>)addItems.get(i);
			if(addItem==null || (addItem!=null && addItem.size()==0)) {
				continue;
			}
			int addTag = Integer.parseInt((String)addItem.get("tag"));

			int index = 0;
			for(int k=0; k<jipyoItems.size(); k++) {
				Hashtable<String, String> item = (Hashtable<String, String>)jipyoItems.get(k);
				int itemTag = Integer.parseInt((String)item.get("tag"));
				if(addTag/100 == itemTag || addTag/100 == itemTag/100) {
					index = k;
				}
			}
			jipyoItems.add(index+1, addItem);
		}

		Vector<Hashtable<String, String>> v = jipyoItems;
		//Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

		int jCnt = v.size();
		//2014.02.11 by LYH >> Price Channel 지표 추가
		//int nIndiCnt = jCnt - 8;		
		int nIndiCnt = jCnt - OVERLAY_JIPYO_COUNT;
		//2014.02.11 by LYH << Price Channel 지표 추가
		int inx = 0;
		int cnt = 0;
		if(type.equals("overlay")) {
			if(m_itemsArr2!=null) m_itemsArr2.clear();
			if(itemChecked2!=null) itemChecked2.clear();
			inx = nIndiCnt;
			cnt = jCnt;
		} else if(type.equals("jipyo")) {
			if(m_itemsArr!=null) m_itemsArr.clear();
			if(itemChecked!=null) itemChecked.clear();
			inx = 0;
			//2014.02.11 by LYH >> Price Channel 지표 추가
//			cnt = jCnt-8-15;   // 기타(other) 갯수 = 15 개, 채널(overlay) 지표 = 8개 
			cnt = jCnt-OVERLAY_JIPYO_COUNT-OTHER_JIPYO_COUNT;   // 기타(other) 갯수 = 15 개, 채널(overlay) 지표 = 9개 
			//2014.02.11 by LYH << Price Channel 지표 추가
		} else if(type.equals("other")) {
			if(m_itemsArr3!=null) m_itemsArr3.clear();
			if(itemChecked3!=null) itemChecked3.clear();
			//2014.02.11 by LYH >> Price Channel 지표 추가			
//			inx = jCnt-8-15;
//			cnt = jCnt-8;
			inx = jCnt-OVERLAY_JIPYO_COUNT-OTHER_JIPYO_COUNT;
			cnt = jCnt-OVERLAY_JIPYO_COUNT;
			//2014.02.11 by LYH << Price Channel 지표 추가			

		} else {
			if(m_itemsArr4!=null) m_itemsArr4.clear();
			if(itemChecked4!=null) itemChecked4.clear();
			inx = 0;
			cnt = jCnt;
		}
		if(type.equals("charttype"))
		{
			//2015. 1. 13 지표설정창에 차트유형 추가>>
			if(m_itemsArr5!=null) m_itemsArr5.clear();
			if(itemChecked5!=null) itemChecked5.clear();

			for(int i = 0; i < typeList.size(); i++)
			{
				boolean chkState=false;
				if(getChartTypeName().equals(typeList.get(i)))
				{
					chkState = true;
				}
				JipyoChoiceItem item1 = new JipyoChoiceItem(typeTags.get(i), typeList.get(i), chkState);
				m_itemsArr5.add(item1);
				itemChecked5.add(chkState);
			}
			//2015. 1. 13 지표설정창에 차트유형 추가<<
		}
		else
		{

			//2015. 1. 13 by lyk - 동일지표 추가 (지표 타입 체크)
			if(addItems!=null && addItems.size()>0) {
				for(int i=0; i<jCnt; i++) {
					Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
					boolean chkState=false;
					int listLen = list.size();
					for(int k=0; k<listLen; k++) {
						String cmp = (String)item.get("name");
						chkState=false;
						if(cmp.equals((String)list.get(k))) {
							chkState = true;
							break;
						}
					}

					JipyoChoiceItem item1 = new JipyoChoiceItem((String)item.get("tag"), (String)item.get("name"), chkState);

					String detailType = (String)item.get("detailType");
					if(type.equals("jipyo") && detailType.equals(type)) {
						itemChecked.add(chkState);
						m_itemsArr.add(item1);
					} else if(type.equals("overlay") && detailType.equals(type)) {
						itemChecked2.add(chkState);
						m_itemsArr2.add(item1);
					} else if(type.equals("other") && detailType.equals(type)) {
						itemChecked3.add(chkState);
						m_itemsArr3.add(item1);
					} else {
						if(m_itemsArr4!=null) {
							itemChecked4.add(chkState);
							m_itemsArr4.add(item1);
						}
					}

				}
			}
			else {
				//2015. 1. 13 by lyk - 동일지표 추가 (지표 타입 체크) end
				for(int i=inx; i<cnt; i++) {
					Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
					boolean chkState=false;
					int listLen = list.size();
					for(int k=0; k<listLen; k++) {
						String cmp = (String)item.get("name");
						chkState=false;
						if(cmp.equals((String)list.get(k))) {
							chkState = true;
							break;
						}
					}


					JipyoChoiceItem item1 = new JipyoChoiceItem((String)item.get("tag"), (String)item.get("name"), chkState);
					if(type.equals("jipyo")) {
						itemChecked.add(chkState);
						m_itemsArr.add(item1);
					} else if(type.equals("overlay")) {
						itemChecked2.add(chkState);
						m_itemsArr2.add(item1);
					} else if(type.equals("other")) {
						itemChecked3.add(chkState);
						m_itemsArr3.add(item1);
					} else {
						itemChecked4.add(chkState);
						m_itemsArr4.add(item1);
					}
				}
				//2015. 1. 13 by lyk - 동일지표 추가 (지표 타입 체크)
			}
			//2015. 1. 13 by lyk - 동일지표 추가 (지표 타입 체크) end
		}
	}

	//2015. 1. 13 by lyk - 동일지표 (forceclick 이벤트 처리)
	public void onListItemClick(AdapterView<?> lv, View v, int position, long id) {

		if(strTagForAddJipyo.equals("") || strNameForAddJipyo.equals(""))
			return;

		this.itemChecked.set(position, true);

		CheckBox cBox = new CheckBox(this.context);
		cBox.setChecked(true);
		cBox.setTag(strTagForAddJipyo);

		COMUtil.setJipyo(cBox);

		View view = new View(this.context);
		view.setId(position);
		view.setTag(strNameForAddJipyo);

		showDetailView(view,"jipyo");

		//변수초기화
		strTagForAddJipyo = "";
		strNameForAddJipyo = "";
	}
	//2015. 1. 13 by lyk - 동일지표 (forceclick 이벤트 처리) end 

	public void resizeChart() {
		LayoutInflater factory = LayoutInflater.from(this.getContext());
		int layoutResId = this.getContext().getResources().getIdentifier("indicatorview", "layout", this.getContext().getPackageName());
		ll = (LinearLayout)factory.inflate(layoutResId, null);

//		Configuration config = getResources().getConfiguration();
//		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//			//--- 가로 화면 고정
//			 COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
//        }
//        else
//        {
//        	//--- 세로 화면 고정
//        	COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
//        }


	}
	public boolean IsNotAddIndicator(int nTag)
	{
		int tag = nTag +30000;
//	    if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 3) {   //선물이면...
//	        if(tag == ChartUtil.MARKET1 || tag == ChartUtil.MARKET2||tag == ChartUtil.MARKET3||tag == ChartUtil.MARKET4
//	           ||tag == ChartUtil.MARKET5||tag == ChartUtil.MARKET6)
//	            return true;
//	    }

//		//2012. 10. 25  업종에서는 거래대금 체크 안됨. : I102
//		if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1) {   //업종이면...
//			if(tag == ChartUtil.MARKET6)
//				return true;
//		}
		


		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// You can call view.setVisiblility(View.GONE) if you want to remove it from the layout, or view.setVisibility(View.INVISIBLE) if you just want to hide it.
//	        this.layout.setVisiblility(View.GONE); // or view.setVisibility(View.INVISIBLE);
//			System.out.println("onBackPressed_IndicatorConfigView_onKeyDown");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//2013. 9. 3 일반설정 체크 수정 >>
//	final int nChkCount = 6;
//	final int nChkCount = 7;	//2015. 2. 24 차트 상하한가 표시
//	final int nChkCount = 9;	//2015.04.16 by lyk - 주기별 차트 설정 추가
//	final int nChkCount = 12;	//2019. 03. 07 by hyh - 만기보정, 제외기준 적용
//	final int nChkCount = 13;	//2020.03.26 차트 최대/최소 표시 설정 추가 - hjw
//	final int nChkCount = 15;	//2021.01.05 by HJW - X,YScale 격자선 옵션화
//	final int nChkCount = 18;	//2021.01.06 by HJW - 캔들설정 내부 설정 일반설정 추가
	final int nChkCount = 19;	//2021.01.07 by HJW - 십자선 종가 따라가기 옵션화
	private CheckBox[] normalChkbox =  new CheckBox[nChkCount];
	//2013. 9. 3 일반설정 체크 수정 >>
	public void setNormalSetView()
	{
		//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
		int layoutResId;
//	   	int nNormalChkCount = 6;
//	   	int nNormalChkCount = 7;	//2015. 2. 24 차트 상하한가 표시
		//2015.04.08 by lyk - 봉 개수 설정 옵션>> (2015.04.16 by lyk - 주기별 차트 설정 추가)
//		for(int i = 0; i < 5; i++){
//			layoutResId = context.getResources().getIdentifier("normalset_txt_"+String.valueOf(i+1), "id", context.getPackageName());
//			TextView tvNormal = (TextView)ll.findViewById(layoutResId);
//			tvNormal.setTypeface(COMUtil.typefaceMid);
//		}
		for(int i = 0; i < nChkCount; i++)
		//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
		{
			String strId = "normalset_check_" + String.valueOf(i+1);
			layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());

			normalChkbox[i] = (CheckBox)ll.findViewById(layoutResId);

			layoutResId = context.getResources().getIdentifier("normalset_tv_"+String.valueOf(i+1), "id", context.getPackageName());
			final TextView tvNormal = (TextView)ll.findViewById(layoutResId);

			strId = "convenient_rowlinear_" + String.valueOf(i+1);
			layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());

			switch(i)
			{
				case 0:
					normalChkbox[i].setChecked(COMUtil.isAdjustedStock());
					break;
				case 1:
					normalChkbox[i].setChecked(COMUtil.isyScaleShow());
					break;
				case 2:
					normalChkbox[i].setChecked(COMUtil.isyJonggaShow());
					break;
				case 3:
					normalChkbox[i].setChecked(COMUtil.isMinMaxShow());
					break;
				case 4:
					normalChkbox[i].setChecked(COMUtil.isChuseLineValueTextShow());
					break;
				case 5:
					normalChkbox[i].setChecked(COMUtil.isDayDivisionLineShow());
					break;
                case 6:
                    normalChkbox[i].setChecked(COMUtil.isBongCntShow());
                    break;
				case 7:
					normalChkbox[i].setChecked(COMUtil.isPeriodConfigSave());
					break;
                case 8:
                    normalChkbox[i].setChecked(COMUtil.isUsePaddingRight());
                    break;
				case 9:
					normalChkbox[i].setChecked(COMUtil.isKoreanTime());
					break;
				case 10:
					normalChkbox[i].setChecked(COMUtil.isUseSkipTick());
					break;
				case 11:
					normalChkbox[i].setChecked(COMUtil.isUseSkipVolume());
					break;
				case 12:
					normalChkbox[i].setChecked(COMUtil.isHighLowShow());
					break;
				case 13:
					normalChkbox[i].setChecked(COMUtil.isShowYScaleLine());
					break;
				case 14:
					normalChkbox[i].setChecked(COMUtil.isShowXScaleLine());
					break;
				case 15:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsCandleMinMax());
					break;
				case 16:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsGapRevision());
					break;
				case 17:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsLog());
					break;
				case 18:
					normalChkbox[i].setChecked(COMUtil.isCrossLineJongga());
					break;
			}
			//2015. 2. 24 차트 상하한가 표시<<
			final LinearLayout linear = (LinearLayout)ll.findViewById(layoutResId);

			final int index = i;
			
			normalChkbox[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onCheckNormal(index);
				}
			});

            if (linear != null) {
//			2017.04.05 by PJM - 해외선물옵션 타입에선 수정주가 메뉴 막음
                if (i == 0 && COMUtil.bIsForeignFuture) {
                    linear.setVisibility(View.GONE);
                }
//			2017.04.05 by PJM - 해외선물옵션 타입에선 수정주가 메뉴 막음 end

                //2013. 3. 25  편의설정 행 터치시 체크 및 적용
                linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index != 8) {
                            normalChkbox[index].setChecked(!normalChkbox[index].isChecked());
                            onCheckNormal(index);
                        }
                    }
                });
            }
		}

		//2013. 8. 22 일반설정에 '캔들설정' 항목 추가>>
		layoutResId = context.getResources().getIdentifier("convenient_rowlinear_candle", "id", context.getPackageName());
		LinearLayout linear_candleSet = (LinearLayout)ll.findViewById(layoutResId);
		linear_candleSet.setId(0); //차트지표에서 하던 것과 같이 값을 맞춰주기위함
		linear_candleSet.setTag("캔들");  ////차트지표에서 하던 것과 같이 값을 맞춰주기위함
		linear_candleSet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//차트지표에서 하던 것과 같이  눌린 컨트롤의 태그로 "캔들" 을 주어서 넘겼다.
				showDetailView(v, "캔들");
				hideKeyPad();
			}
		});
		//2013. 8. 22 일반설정에 '캔들설정' 항목 추가>>

		//2016. 1. 11 종가 등락률 옵션처리>>
		//일반설정 - 종가 등락률 대비
        layoutResId = context.getResources().getIdentifier("lastday_radio_btn", "id", context.getPackageName());
        final RadioButton radioLastDay = (RadioButton) ll.findViewById(layoutResId);
        layoutResId = context.getResources().getIdentifier("lastbong_radio_btn", "id", context.getPackageName());
        final RadioButton radioLastBong = (RadioButton) ll.findViewById(layoutResId);

        if(COMUtil.isyJonggaCurrentPrice())
        {
            radioLastDay.setChecked(true);
			radioLastDay.setTextColor(Color.WHITE);
			radioLastBong.setTextColor(Color.rgb(17, 17, 17));
        }
        else
        {
            radioLastBong.setChecked(true);
			radioLastBong.setTextColor(Color.WHITE);
			radioLastDay.setTextColor(Color.rgb(17, 17, 17));
        }

        radioLastDay.setOnClickListener(radioListener);
        radioLastBong.setOnClickListener(radioListener);

        //일반설정 - 종가 등락률 대비 터치 개선
//		layoutResId = context.getResources().getIdentifier("rl_lastday", "id", context.getPackageName());
//		RelativeLayout rlLastDay = (RelativeLayout) ll.findViewById(layoutResId);
//		rlLastDay.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				radioLastDay.performClick();
//			}
//		});
//
//		layoutResId = context.getResources().getIdentifier("rl_lastbong", "id", context.getPackageName());
//		RelativeLayout rlLastBong = (RelativeLayout) ll.findViewById(layoutResId);
//		rlLastBong.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				radioLastBong.performClick();
//			}
//		});

		//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
		layoutResId = context.getResources().getIdentifier("zoom_radio_btn_normal", "id", context.getPackageName());
		final RadioButton zoom_radio_btn_normal = (RadioButton) ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("zoom_radio_btn_detail", "id", context.getPackageName());
		final RadioButton zoom_radio_btn_detail = (RadioButton) ll.findViewById(layoutResId);

		if(COMUtil.isDetailScroll())
		{
			zoom_radio_btn_detail.setChecked(true);
		}
		else
		{
			zoom_radio_btn_normal.setChecked(true);
		}
		zoom_radio_btn_detail.setOnClickListener(radioListener);
		zoom_radio_btn_normal.setOnClickListener(radioListener);

		layoutResId = context.getResources().getIdentifier("rl_zoom_normal", "id", context.getPackageName());
		RelativeLayout rl_zoom_normal = (RelativeLayout) ll.findViewById(layoutResId);
		rl_zoom_normal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom_radio_btn_normal.performClick();
			}
		});

		layoutResId = context.getResources().getIdentifier("rl_zoom_detail", "id", context.getPackageName());
		RelativeLayout rl_zoom_detail = (RelativeLayout) ll.findViewById(layoutResId);
		rl_zoom_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom_radio_btn_detail.performClick();
			}
		});



		layoutResId = context.getResources().getIdentifier("hubong_radio_btn", "id", context.getPackageName());
		final RadioButton hubong_radio_btn = (RadioButton) ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("silbong_radio_btn", "id", context.getPackageName());
		final RadioButton silbong_radio_btn = (RadioButton) ll.findViewById(layoutResId);

		if(COMUtil.isUseHubong())
		{
			hubong_radio_btn.setChecked(true);
		}
		else
		{
			silbong_radio_btn.setChecked(true);
		}
		hubong_radio_btn.setOnClickListener(radioListener);
		silbong_radio_btn.setOnClickListener(radioListener);

		layoutResId = context.getResources().getIdentifier("rl_hubong", "id", context.getPackageName());
		RelativeLayout rl_hubong = (RelativeLayout) ll.findViewById(layoutResId);
		rl_hubong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hubong_radio_btn.performClick();
			}
		});

		layoutResId = context.getResources().getIdentifier("rl_silbong", "id", context.getPackageName());
		RelativeLayout rl_silbong = (RelativeLayout) ll.findViewById(layoutResId);
		rl_silbong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				silbong_radio_btn.performClick();
			}
		});

		//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<



		//2016.10.06 by LYH >> 서버저장 불러오기
        layoutResId = context.getResources().getIdentifier("convenient_save", "id", context.getPackageName());
        Button btnSave = (Button)ll.findViewById(layoutResId);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
				alert.setTitle("차트 설정 서버저장");
				alert.setMessage("단말기에 저장되어 있는 전체 차트설정 (사용자 설정 차트 포함)을 서버에 저장합니다. 기존 서버에 저장된 설정이 있는 경우 현재 데이터로 변경됩니다. 서버저장을 진행하시겠습니까?");
				alert.setNoButton("취소", null);
				alert.setYesButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								COMUtil._mainFrame.saveStatus(null);
                                COMUtil.saveLastState("combChartSetting");
								if(COMUtil._mainFrame.userProtocol!=null)
									COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);
								dialog.dismiss();
							}
						});
				alert.show();
				COMUtil.g_chartDialog = alert;

            }
        });

        layoutResId = context.getResources().getIdentifier("convenient_load", "id", context.getPackageName());
        Button btnLoad = (Button)ll.findViewById(layoutResId);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
				alert.setTitle("차트 설정 불러오기");
				alert.setMessage("서버에 저장된 차트 설정(사용자 설정 차트 포함)을 불러오며, 현재 단말기에 설정된 내용이 서버에서 가져온 데이터로 변경됩니다. 설정 불러오기를 진행하시겠습니까?");
				alert.setNoButton("취소", null);
				alert.setYesButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								COMUtil._mainFrame.closePopup();

								if(COMUtil._mainFrame.userProtocol!=null)
									COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_LOAD_LOCAL_CLOUD, null);
								dialog.dismiss();
							}
						});
				alert.show();
				COMUtil.g_chartDialog = alert;

            }
        });
        //2013. 8. 22 일반설정에 '캔들설정' 항목 추가>>

		//2013. 11. 21 추세선 종목별 저장>>
		layoutResId = context.getResources().getIdentifier("convenient_rowlinear_analtool", "id", context.getPackageName());
		LinearLayout linear_analtool = (LinearLayout)ll.findViewById(layoutResId);
		linear_analtool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
				alert.setTitle("추세선 초기화");
				alert.setMessage("저장된 모든 추세선을 삭제하시겠습니까?");
				alert.setNoButton("아니오", null);
				alert.setYesButton("초기화",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								//모든 차트의 분석툴을 지운다. 
								Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
								base11.resetAllAnalToolBySymbol();

								//알림창 닫기
								dialog.dismiss();

								//설정창 닫기 
								COMUtil._mainFrame.closePopup();
							}
						});
				alert.show();
				COMUtil.g_chartDialog = alert;
			}
		});
		//2013. 11. 21 추세선 종목별 저장<<

		//조회데이터 갯수 EditText에 설정
		edRightPadding = (ExEditText)ll.findViewById(context.getResources().getIdentifier("ed_rightpaddingnumset", "id", context.getPackageName()));
		edRightPadding.setOnBackButtonListener(this);
		edRightPadding.setText(COMUtil.getPaddingRight()+"px");
		edRightPadding.setTypeface(COMUtil.numericTypefaceMid);


        if (COMUtil.isUsePaddingRight()) {
            edRightPadding.setEnabled(true);
        } else
            edRightPadding.setEnabled(false);

		final EditText _edRightPadding = edRightPadding;
		edRightPadding.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
				{
					int max = 200;
					int min = 0;
					String strRightPadding = _edRightPadding.getText().toString();
                    strRightPadding = strRightPadding.replace("p", "");
                    strRightPadding = strRightPadding.replace("x", "");
					try {
						if (strRightPadding.equals("")) {
							_edRightPadding.setText(COMUtil.getPaddingRight() + "px");
						} else if (Integer.parseInt(strRightPadding) > max) {
							_edRightPadding.setText(max+"px");
							Toast.makeText(context, "차트 우측여백 입력 값은 0~200px 입니다", Toast.LENGTH_LONG).show();
						} else if (Integer.parseInt(strRightPadding) < min) {
							_edRightPadding.setText(min+"px");
							Toast.makeText(context, "차트 우측여백 입력 값은 0~200px 입니다", Toast.LENGTH_LONG).show();
						} else {
							_edRightPadding.setText(strRightPadding + "px");
						}

                        strRightPadding = _edRightPadding.getText().toString();
                        strRightPadding = strRightPadding.replace("px", "");
                        COMUtil._mainFrame.g_nPaddingRight = Integer.parseInt(strRightPadding);
                        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                        base11.setFuncAllChart("setPaddingRight");
					}catch (Exception e){}
					hideKeyPad(_edRightPadding);

				}
				return true;
			}
		});

		//필터링 가격 EditText에 설정
		edSkipTick = (ExEditText)ll.findViewById(context.getResources().getIdentifier("ed_skip_tick", "id", context.getPackageName()));
		edSkipTick.setOnBackButtonListener(this);
		edSkipTick.setText(COMUtil._mainFrame.strSkipTick);

		if (COMUtil.isUseSkipTick()) {
			edSkipTick.setEnabled(true);
		}
		else {
			edSkipTick.setEnabled(false);
		}

		edSkipTick.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
					String strValue;

					int nMax = 10000;
					int nMin = 0;
					try {
						strValue = edSkipTick.getText().toString();
						Integer.parseInt(strValue);

						if (strValue.equals("")) {
							edSkipTick.setText(COMUtil._mainFrame.strSkipTick);
						}
						else if (Integer.parseInt(strValue) > nMax) {
							edSkipTick.setText(nMax);
						}
						else if (Integer.parseInt(strValue) < nMin) {
							edSkipTick.setText(nMin);
						}
						else {
							edSkipTick.setText(strValue);
						}
					} catch (Exception e) {
						strValue = "0";
					}

					COMUtil._mainFrame.strSkipTick = strValue;
					hideKeyPad(edSkipTick);
				}
				return true;
			}
		});

		//필터링 거래량 EditText에 설정
		edSkipVolume = (ExEditText)ll.findViewById(context.getResources().getIdentifier("ed_skip_volume", "id", context.getPackageName()));
		edSkipVolume.setOnBackButtonListener(this);
		edSkipVolume.setText(COMUtil._mainFrame.strSkipVol);

		if (COMUtil.isUseSkipVolume()) {
			edSkipVolume.setEnabled(true);
		}
		else {
			edSkipVolume.setEnabled(false);
		}

		edSkipVolume.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
					String strValue;

					int nMax = 10000;
					int nMin = 0;
					try {
						strValue = edSkipVolume.getText().toString();
						Integer.parseInt(strValue);

						if (strValue.equals("")) {
							edSkipVolume.setText(COMUtil._mainFrame.strSkipVol);
						}
						else if (Integer.parseInt(strValue) > nMax) {
							edSkipVolume.setText(nMax);
						}
						else if (Integer.parseInt(strValue) < nMin) {
							edSkipVolume.setText(nMin);
						}
						else {
							edSkipVolume.setText(strValue);
						}
					} catch (Exception e) {
						strValue = "0";
					}

					COMUtil._mainFrame.strSkipVol = strValue;
					hideKeyPad(edSkipVolume);
				}
				return true;
			}
		});

		//2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
//		layoutResId = context.getResources().getIdentifier("convenient_rowlinear_21", "id", context.getPackageName());
//		LinearLayout llFont = (LinearLayout)ll.findViewById(layoutResId);
//
//		layoutResId = context.getResources().getIdentifier("convenient_rowlinear_22", "id", context.getPackageName());
//		LinearLayout llVPFont = (LinearLayout)ll.findViewById(layoutResId);

//		if(!COMUtil.bIsForeignFuture && !COMUtil.bIsGlobalStock) {  //2017.09.13 by pjm 해외주식 추가 >>
//			llFont.setVisibility(GONE);
//			llVPFont.setVisibility(GONE);
//		}

		layoutResId = context.getResources().getIdentifier("font_small", "id", context.getPackageName());
		final Button btnSfont = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("font_middle", "id", context.getPackageName());
		final Button btnMfont = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("font_large", "id", context.getPackageName());
		final Button btnLfont = (Button)ll.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("viewp_font_small", "id", context.getPackageName());
		final Button btnVPSfont = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("viewp_font_middle", "id", context.getPackageName());
		final Button btnVPMfont = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("viewp_font_large", "id", context.getPackageName());
		final Button btnVPLfont = (Button)ll.findViewById(layoutResId);

		if(COMUtil.getFontSizeBtn() == 0)
		{
			btnSfont.setSelected(true);
			btnMfont.setSelected(false);
			btnLfont.setSelected(false);

			btnSfont.setTextColor(Color.WHITE);
			btnMfont.setTextColor(Color.rgb(17, 17, 17));
			btnLfont.setTextColor(Color.rgb(17, 17, 17));
		}

		else if(COMUtil.getFontSizeBtn() == 1){
			btnSfont.setSelected(false);
			btnMfont.setSelected(true);
			btnLfont.setSelected(false);

			btnMfont.setTextColor(Color.WHITE);
			btnSfont.setTextColor(Color.rgb(17, 17, 17));
			btnLfont.setTextColor(Color.rgb(17, 17, 17));
		}

		else if(COMUtil.getFontSizeBtn() == 2){
			btnSfont.setSelected(false);
			btnMfont.setSelected(false);
			btnLfont.setSelected(true);

			btnLfont.setTextColor(Color.WHITE);
			btnSfont.setTextColor(Color.rgb(17, 17, 17));
			btnMfont.setTextColor(Color.rgb(17, 17, 17));
		}

		if(COMUtil.getVPFontSizeBtn() == 0) {
			btnVPSfont.setSelected(true);
			btnVPMfont.setSelected(false);
			btnVPLfont.setSelected(false);

			btnVPSfont.setTextColor(Color.WHITE);
			btnVPMfont.setTextColor(Color.rgb(17, 17, 17));
			btnVPLfont.setTextColor(Color.rgb(17, 17, 17));
		}
		else if(COMUtil.getVPFontSizeBtn() == 1){
			btnVPSfont.setSelected(false);
			btnVPMfont.setSelected(true);
			btnVPLfont.setSelected(false);

			btnVPMfont.setTextColor(Color.WHITE);
			btnVPSfont.setTextColor(Color.rgb(17, 17, 17));
			btnVPLfont.setTextColor(Color.rgb(17, 17, 17));
		}
		else if(COMUtil.getVPFontSizeBtn() == 2){
			btnVPSfont.setSelected(false);
			btnVPMfont.setSelected(false);
			btnVPLfont.setSelected(true);

			btnVPLfont.setTextColor(Color.WHITE);
			btnVPSfont.setTextColor(Color.rgb(17, 17, 17));
			btnVPMfont.setTextColor(Color.rgb(17, 17, 17));
		}
		btnSfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnSfont.setSelected(true);
				btnMfont.setSelected(false);
				btnLfont.setSelected(false);

				btnSfont.setTextColor(Color.WHITE);
				btnMfont.setTextColor(Color.rgb(17, 17, 17));
				btnLfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setFontSizeBtn(0);
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.repaintAllChart();
			}
		});

		btnMfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnSfont.setSelected(false);
				btnMfont.setSelected(true);
				btnLfont.setSelected(false);

				btnMfont.setTextColor(Color.WHITE);
				btnSfont.setTextColor(Color.rgb(17, 17, 17));
				btnLfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setFontSizeBtn(1);
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.repaintAllChart();
			}
		});

		btnLfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnSfont.setSelected(false);
				btnMfont.setSelected(false);
				btnLfont.setSelected(true);

				btnLfont.setTextColor(Color.WHITE);
				btnSfont.setTextColor(Color.rgb(17, 17, 17));
				btnMfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setFontSizeBtn(2);
				Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
				base11.repaintAllChart();
			}
		});

		btnVPSfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnVPSfont.setSelected(true);
				btnVPMfont.setSelected(false);
				btnVPLfont.setSelected(false);

				btnVPSfont.setTextColor(Color.WHITE);
				btnVPMfont.setTextColor(Color.rgb(17, 17, 17));
				btnVPLfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setVPFontSizeBtn(0);
			}
		});

		btnVPMfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnVPSfont.setSelected(false);
				btnVPMfont.setSelected(true);
				btnVPLfont.setSelected(false);

				btnVPMfont.setTextColor(Color.WHITE);
				btnVPSfont.setTextColor(Color.rgb(17, 17, 17));
				btnVPLfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setVPFontSizeBtn(1);
			}
		});

		btnVPLfont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnVPSfont.setSelected(false);
				btnVPMfont.setSelected(false);
				btnVPLfont.setSelected(true);

				btnVPLfont.setTextColor(Color.WHITE);
				btnVPSfont.setTextColor(Color.rgb(17, 17, 17));
				btnVPMfont.setTextColor(Color.rgb(17, 17, 17));

				COMUtil.setVPFontSizeBtn(2);
			}
		});


		layoutResId = context.getResources().getIdentifier("normalLinear", "id", context.getPackageName());
		LinearLayout llNormal = (LinearLayout)ll.findViewById(layoutResId);

		llNormal.setOnTouchListener(
				new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						//2012. 8. 3 키패드 감추기
						hideKeyPad();
						return true;
					}
				}
		);


	}

	public void onBackButtonClick(EditText ed)
	{
		// 빈칸이면 초기값으로
		if(ed.getText().toString().equals(""))
		{
			ed.setText("8");
		}
		int max = 200;
		int min = 0;

		String strRightPadding = edRightPadding.getText().toString();
		strRightPadding = strRightPadding.replace("p", "");
		strRightPadding = strRightPadding.replace("x", "");
		try {
			if (strRightPadding.equals("")) {
				edRightPadding.setText(COMUtil.getPaddingRight() + "px");
			} else if (Integer.parseInt(strRightPadding) > max) {
				edRightPadding.setText(max+"px");
				Toast.makeText(context, "차트 우측여백 입력 값은 0~200px 입니다", Toast.LENGTH_LONG).show();
			} else if (Integer.parseInt(strRightPadding) < min) {
				edRightPadding.setText(min+"px");
				Toast.makeText(context, "차트 우측여백 입력 값은 0~200px 입니다", Toast.LENGTH_LONG).show();
			} else {
				edRightPadding.setText(strRightPadding + "px");
			}

			strRightPadding = edRightPadding.getText().toString();
			strRightPadding = strRightPadding.replace("px", "");
			COMUtil._mainFrame.g_nPaddingRight = Integer.parseInt(strRightPadding);
			Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
			base11.setFuncAllChart("setPaddingRight");
		}catch (Exception e){}
		hideKeyPad(ed);
	}

	private void onCheckNormal(int nIndex)
	{
        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		switch(nIndex) {
//            case 0:    //만기보정
//				if (normalChkbox[nIndex].isChecked()) {
//					COMUtil._mainFrame.strModYn = "Y";
//				}
//				else {
//					COMUtil._mainFrame.strModYn = "N";
//				}
//
//				COMUtil._mainFrame.modSkipRequestInfo();
//                break;
			case 0:    //수정주가
				COMUtil._mainFrame.bIsAdjustedStock = normalChkbox[nIndex].isChecked();
				COMUtil.sendTR("" + COMUtil._TAG_SET_ADJUSTEDSTOCK);
				break;
            case 1:    //y축 스케일 보이기/숨기기
                COMUtil._mainFrame.bIsyScaleShow = normalChkbox[nIndex].isChecked();
                base11.resetUIAllChart();
                break;
            case 2:    //y축 현재가
                COMUtil._mainFrame.bIsyJonggaShow = normalChkbox[nIndex].isChecked();
                base11.resetUIAllChart();
                break;
            case 3:    //상하한가 바
                COMUtil._mainFrame.bIsMinMaxShow = normalChkbox[nIndex].isChecked();
                base11.resetUIAllChart();
                break;
            //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
            case 4:    //추세선 그릴때 수치값 문자열 표시 여부
                COMUtil._mainFrame.bIsChuseLineValueTextShow = normalChkbox[nIndex].isChecked();
                base11.resetUIAllChart();
                break;
            //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
            //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
            case 5:    //분틱 날짜구분선
                COMUtil._mainFrame.bIsDayDivisionLineShow = normalChkbox[nIndex].isChecked();
                base11.resetUIAllChart();
                break;
            //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
            //2015. 2. 24 차트 상하한가 표시>>
            case 6:
                COMUtil._mainFrame.bIsBongCntShow = normalChkbox[nIndex].isChecked();
                eventListener.onChangeNormalSetValues(nIndex);
                break;
            //2015.04.16 by lyk - 주기별 차트 설정 옵션<<
            case 7:    //주기별 차트 설정
                COMUtil._mainFrame.bIsPeriodConfigSave = normalChkbox[nIndex].isChecked();
                break;
            case 8:    //우측여백 사용여부
            {
                COMUtil._mainFrame.bIsUsePaddingRight = normalChkbox[nIndex].isChecked();
                base11.setFuncAllChart("setPaddingRight");
                EditText edRightPadding = (EditText) ll.findViewById(context.getResources().getIdentifier("ed_rightpaddingnumset", "id", context.getPackageName()));
                if (COMUtil.isUsePaddingRight()) {
                    edRightPadding.setEnabled(true);
                } else
                    edRightPadding.setEnabled(false);
            }
                break;

			case 9:
				break;

			case 10:    //필터링 가격 적용
			case 11: {
				CheckBox chkSkipTick = normalChkbox[10];
				CheckBox chkSkipVolume = normalChkbox[11];

				boolean bIsSkipTick = chkSkipTick.isChecked();
				boolean bIsSkipVol = chkSkipVolume.isChecked();

				if (!bIsSkipTick && !bIsSkipVol) {
					COMUtil._mainFrame.strSkipTp = "0";
				}
				else if (bIsSkipTick && !bIsSkipVol) {
					COMUtil._mainFrame.strSkipTp = "1";
				}
				else if (!bIsSkipTick && bIsSkipVol) {
					COMUtil._mainFrame.strSkipTp = "2";
				}
				else {
					COMUtil._mainFrame.strSkipTp = "3";
				}

				edSkipTick.setEnabled(bIsSkipTick);
				edSkipVolume.setEnabled(bIsSkipVol);

				COMUtil._mainFrame.modSkipRequestInfo();
			}
				break;
			case 12:    //y축 현재가
				COMUtil._mainFrame.bIsHighLowShow = normalChkbox[nIndex].isChecked();
				base11.resetUIAllChart();
				break;
			case 13:
				COMUtil._mainFrame.bIsShowYScaleLine = normalChkbox[nIndex].isChecked();
				base11.resetUIAllChart();
				break;
			case 14:
				COMUtil._mainFrame.bIsShowXScaleLine = normalChkbox[nIndex].isChecked();
				base11.resetUIAllChart();
				break;
			case 15:
				COMUtil._mainFrame.mainBase.baseP._chart._cvm.setIsCandleMinMax(normalChkbox[nIndex].isChecked());
				base11.resetUIAllChart();
				break;
			case 16:
				COMUtil._mainFrame.mainBase.baseP._chart._cvm.setIsGapRevision(normalChkbox[nIndex].isChecked());
				base11.resetUIAllChart();
				break;
			case 17:
				COMUtil._mainFrame.mainBase.baseP._chart._cvm.setIsLog(normalChkbox[nIndex].isChecked());
				base11.resetUIAllChart();
				break;
			case 18:
				COMUtil._mainFrame.bIsCrossLineJongga = normalChkbox[nIndex].isChecked();
				break;
		}
		hideKeyPad();
	}
	//ToolBarSetListAdapter m_tAdapter = null;
	public void initToolbarList()
	{
		int layoutResId = context.getResources().getIdentifier("toolbarlist", "id", context.getPackageName());
		//toolbarlist = (ToolBarSetListView)ll.findViewById(layoutResId);

//		layoutResId = context.getResources().getIdentifier("toolbarSetView", "id", context.getPackageName());
		layoutResId = context.getResources().getIdentifier("toolbar_tablearea", "id", context.getPackageName());
		final ViewGroup toolbarView = (ViewGroup)ll.findViewById(layoutResId);
		if(null != toolbarlist)
		{
			toolbarView.removeView(toolbarlist);
		}
		toolbarlist = new ToolBarSetListView(context);
		toolbarView.addView(toolbarlist);

		String drawableStr = "";
		ArrayList<ToolbarItem> userItems = new ArrayList<ToolbarItem>();
		int nIndex;
		for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
			nIndex = COMUtil.arrToolbarIndex[i]-COMUtil.TOOLBAR_CONFIG_START;
			if(COMUtil.arrToolbarIndex[i] == COMUtil.TOOLBAR_CONFIG_BASELINE)
				drawableStr = "gijunsun";
			else if(COMUtil.arrToolbarIndex[i] == COMUtil.TOOLBAR_CONFIG_AUTOLINE)
				drawableStr = "jadongchuse";
			else if(COMUtil.arrToolbarIndex[i] == COMUtil.TOOLBAR_CONFIG_DIVIDE)
				drawableStr = "bunhal";
			else
			{
				if(nIndex<9)
					drawableStr = "0" + (nIndex+1);
				else
					drawableStr = "" + (nIndex+1);
			}
			ToolbarItem item1 = new ToolbarItem(drawableStr, COMUtil.astrToolbarNames[nIndex], COMUtil.arrToolbarIndex[i], COMUtil.arrToolbarSelected[i]);
			userItems.add(item1);
		}

		toolbarlist.setData(userItems);

//		m_tAdapter = new ToolBarSetListAdapter(toolbarlist);
//		m_tAdapter.setData(userItems);
//		toolbarlist.setAdapter(m_tAdapter);
//		toolbarlist.setOnItemClickListener(new OnItemClickListener() {
//			   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//			     long arg3) {
//				   onListItemClick(arg0,arg1,arg2,arg3);
//			   }
//			         
//		});
	}

	//2013. 8. 19 지표설정창 분틱설정에서 소프트키보드 올라와있는 상태에서 닫으면 키보드 남아있음>>
	public void closePeriodViewKeyboard()
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		// 분틱설정이 초기화 되어 있지 않으면 리턴 
//		if( null == arPeriodBunEditText || 0 == arPeriodBunEditText.size() ||
//				null == arPeriodTicEditText || 0 == arPeriodTicEditText.size())
//		{
//			return;
//		}

		//키보드 내림처리.   어느키보드에서 열린 것인지 알 수 없으므로 전체에 대해서 hide 동작 수행 
		for(int i = 0; i <base11.astrMinData.length ; i++)
		{
			hideKeyPad(editPeriod);
		}
	}
	//2013. 8. 19 지표설정창 분틱설정에서 소프트키보드 올라와있는 상태에서 닫으면 키보드 남아있음>>

	//2013. 8. 21 COMUtil 에 있던 지표설정 리스트 3개 가시성을 제어하는 함수 .
	public void setIndicatorListViewVisibility(View btn)
	{
		LinearLayout ll = (LinearLayout)layout.findViewWithTag("indicatorView");

		int layoutResId = context.getResources().getIdentifier("jipyoLayout", "id", context.getPackageName());
		RelativeLayout jipyoLayout = (RelativeLayout)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("overlayLayout", "id", context.getPackageName());
		RelativeLayout overlayLayout = (RelativeLayout)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("chartLayout", "id", context.getPackageName());
		LinearLayout chartLayout = (LinearLayout)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("otherLayout", "id", context.getPackageName());
		RelativeLayout otherLayout = (RelativeLayout)ll.findViewById(layoutResId);
//    	layoutResId = context.getResources().getIdentifier("totalLayout", "id", context.getPackageName());
//    	RelativeLayout totalLayout = (RelativeLayout)ll.findViewById(layoutResId);

		//먼저 스크롤뷰들을 모두 감추고 아래에서 조건에 맞는 뷰만 보여준다.
		jipyoLayout.setVisibility(View.GONE);
		overlayLayout.setVisibility(View.GONE);
		chartLayout.setVisibility(View.GONE);
		otherLayout.setVisibility(View.GONE);
//    	totalLayout.setVisibility(View.GONE);

		
		//지표리스트 토글버튼들을 모두 일단 선택되지 않은 상태로 하고 아래 조건에서 해당하는 것만 선택상태로 변경
//    	Button btn_totalindicator = (Button)ll.findViewWithTag("1150");
//    	btn_totalindicator.setSelected(false);
//    	btn_totalindicator.setTextColor(Color.rgb(46, 48, 51));
		
		Button btn_channelindicator  = (Button)ll.findViewWithTag("1160");
		btn_channelindicator.setSelected(false);
		btn_channelindicator.setTextColor(tabTextColor);
		btn_channelindicator.setTypeface(COMUtil.typeface);
		Button btn_technicalindicator  = (Button)ll.findViewWithTag("1170");
		btn_technicalindicator.setSelected(false);
		btn_technicalindicator.setTextColor(tabTextColor);
		btn_technicalindicator.setTypeface(COMUtil.typeface);
		Button btn_etcindicator  = (Button)ll.findViewWithTag("1180");
		btn_etcindicator.setSelected(false);
		btn_etcindicator.setTextColor(tabTextColor);
		btn_etcindicator.setTypeface(COMUtil.typeface);


		//2015. 1. 13 차트유형 추가>>
		Button btn_charttypeindicator  = (Button)ll.findViewWithTag("1190");
		btn_charttypeindicator.setSelected(false);
		btn_charttypeindicator.setTextColor(tabTextColor);
		btn_charttypeindicator.setTypeface(COMUtil.typeface);
		//2015. 1. 13 차트유형 추가<<

	
		//1150 : 전체지표
//    	if(  ((String)btn.getTag()).equals("1150") )
//		{
//    		makeItems("total");
//    		
//    		//2015. 1. 13 by lyk - 동일지표 추가 (지표리스트 행 높이 조절) 
//			totallist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 
//					(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));
//	    	totalLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 
//	    			(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));
//	    	//2015. 1. 13 by lyk - 동일지표 추가 (지표리스트 행 높이 조절) end
//    		
//    		m_scvAdapter4 = new MyArrayAdapter(context, m_itemsArr4, 3);
//    		totallist.setAdapter(m_scvAdapter4);
//    		btn_totalindicator.setSelected(true);
//    		btn_totalindicator.setTextColor(Color.WHITE);
//    		totalLayout.setVisibility(View.VISIBLE);
//		}
		//1160 : 채널지표
		if(  ((String)btn.getTag()).equals("1160") )
		{
			makeItems("overlay");
			m_scvAdapter2 = new MyArrayAdapter(context, m_itemsArr2, 1);
			overlaylist.setAdapter(m_scvAdapter2);
			btn_channelindicator.setSelected(true);
			btn_channelindicator.setTextColor(selectTextColor);
			btn_channelindicator.setTypeface(COMUtil.typefaceBold);
			overlayLayout.setVisibility(View.VISIBLE);
		}
		else if( ((String)btn.getTag()).equals("1170")  )
		{
			makeItems("jipyo");

			//2015. 1. 13 by lyk - 동일지표 추가 (지표리스트 행 높이 조절)
			jipyolist.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr.size()*(int)COMUtil.getPixel(1)))));
			jipyoLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr.size()*(int)COMUtil.getPixel(1)))));
			//2015. 1. 13 by lyk - 동일지표 추가 (지표리스트 행 높이 조절) end

			m_scvAdapter = new MyArrayAdapter(context, m_itemsArr, 0);
			jipyolist.setAdapter(m_scvAdapter);
			//1170 : 기술적지표
			btn_technicalindicator.setSelected(true);
			btn_technicalindicator.setTextColor(selectTextColor);
			btn_technicalindicator.setTypeface(COMUtil.typefaceBold);

			layoutResId = context.getResources().getIdentifier("indicatorscroll", "id", context.getPackageName());
			final ScrollView sv = (ScrollView)ll.findViewById(layoutResId);
			sv.post(new Runnable() {

				@Override
				public void run() {
//    	        	sv.fullScroll(ScrollView.FOCUS_UP);
					sv.scrollTo(0, 0); //맨위로 강제이동
				}
			});
			jipyoLayout.setVisibility(View.VISIBLE);
		}
		else if( ((String)btn.getTag()).equals("1180") )
		{
			makeItems("other");
			m_scvAdapter3 = new MyArrayAdapter(context, m_itemsArr3, 2);
			otherlist.setAdapter(m_scvAdapter3);
			//기타지표
			btn_etcindicator.setSelected(true);
			btn_etcindicator.setTextColor(selectTextColor);
			btn_etcindicator.setTypeface(COMUtil.typefaceBold);

			//chartLayout.setVisibility(View.VISIBLE);
			otherLayout.setVisibility(View.VISIBLE);
		}
		//2015. 1. 13 차트유형 추가>>
		else if( ((String)btn.getTag()).equals("1190") )
		{
			//2014. 3. 17 지표설정창에 차트유형 추가>>
			makeItems("charttype");
//    		m_scvAdapter5 = new MyArrayAdapter(context, m_itemsArr5, 4);
			m_scvAdapter_selectlist = new chartArrayAdapter(context, m_itemsArr5, "charttype");
			chartlist.setAdapter(m_scvAdapter_selectlist);
			//2014. 3. 17 지표설정창에 차트유형 추가<<

			//기타지표
			btn_charttypeindicator.setSelected(true);
			btn_charttypeindicator.setTextColor(selectTextColor);
//			btn_charttypeindicator.setTypeface(COMUtil.typefaceBold);

			layoutResId = context.getResources().getIdentifier("iv_charttype_arrow", "id", context.getPackageName());
			ImageView iv_charttype_arrow = ll.findViewById(layoutResId);

			if (m_bChartTypeFlag) {
				chartLayout.setVisibility(View.GONE);
				m_bChartTypeFlag = false;
				iv_charttype_arrow.setBackgroundResource(context.getResources().getIdentifier("btn_jipyo_arrow_down_n", "drawable", context.getPackageName()));
			}
			else {
				chartLayout.setVisibility(View.VISIBLE);
				m_bChartTypeFlag = true;
				iv_charttype_arrow.setBackgroundResource(context.getResources().getIdentifier("btn_jipyo_arrow_up_n", "drawable", context.getPackageName()));
			}
		}
		//2015. 1. 13 차트유형 추가<<
	}
	//2013. 8. 21 COMUtil 에 있던 지표설정 리스트 3개 가시성을 제어하는 함수 .

	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
	public void closePopupViews()
	{
		if(null != detailJipyoView)		//상세설정창이 열려있는 경우에는 팝업창 닫는 함수로 
		{
			detailJipyoView.closePopupViews();
		}
		else
		{
			closePeriodViewKeyboard();  //2013. 10. 10 분틱설정창에서 키보드 떠 있을 때 회전시 안닫히는 현상 수정 
		}
	}
	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>

	//2014. 1. 17 더블탭 전체차트에서 도구설정 위치 바뀐 상태가 종합차트에 돌아왔을 땐 반영되지 않음>>
	/**
	 * 도구모음에서 설정한 분석툴바 위치정보를 실제 분석툴바에 적용한다. 
	 * */
	public void resetAnalTool()
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		if(toolbarlist != null)
		{
			ArrayList<ToolbarItem> userItems = toolbarlist.getData();
			for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
				ToolbarItem item1 = userItems.get(i);
				COMUtil.arrToolbarIndex[i] = item1.getTag();

				COMUtil.arrToolbarSelected[i] = item1.getChk()?"1":"0";
			}
			base11.resetAnaltool();
		}
	}
	//2014. 1. 17 더블탭 전체차트에서 도구설정 위치 바뀐 상태가 종합차트에 돌아왔을 땐 반영되지 않음<<

	/**
	 * 분석툴바 순서 및 체크상태를 초기화한다
	 * */
	public void initAnalTool()
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		if(toolbarlist != null)
		{
			for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
				COMUtil.arrToolbarIndex[i] = COMUtil.arrDefaultToolbarIndex[i];
				COMUtil.arrToolbarSelected[i] = "1";
			}
			base11.resetAnaltool();
		}
	}

	//2014. 3. 17 지표설정창에 차트유형 추가>>

	//2015. 1. 13 차트유형 추가>>
	/**
	 * 현재 차트가 어느 타입인지 문자열로 반환한다. 
	 * @return  현재 차트타입  (ex: 캔들, 바, 봉, P&F...)
	 * */
	protected String getChartTypeName()
	{
		String strChart = null;
		boolean bIsStand = COMUtil._mainFrame.mainBase.baseP._chart._cvm.isStandGraph();
		if(bIsStand)
		{
			int btype = Block.STAND_BLOCK;
			Block stand = COMUtil._mainFrame.mainBase.baseP._chart.getChartBlockByType(btype);
			if(stand != null)
			{
				strChart = stand.getTitle();
			}
		}
		else
		{
			strChart = COMUtil._mainFrame.mainBase.baseP._chart.m_strCandleType;
		}

		//2014. 3. 27 차트 아무것도 조회 안되어 있을 때 설정 누르면 죽는 현상>>
		if(null == strChart)	return "캔들";
		else					return strChart;
		//2014. 3. 27 차트 아무것도 조회 안되어 있을 때 설정 누르면 죽는 현상<<
	}
	//2015. 1. 13 차트유형 추가<<

	//2015. 1. 13 by lyk - 동일지표 추가
	private IndicatorManager indicatorManager = null;
	private void showIndicatorManager(RelativeLayout layout) {
		indicatorManager = new IndicatorManager(this.getContext(), layout);
		indicatorManager.setParent(this);
		this.layout.addView(indicatorManager);
	}
	//2015. 1. 13 by lyk - 동일지표 추가 end

	//2015. 1. 13 by lyk 중복지표 추가/삭제 처리
	public void addAddJipyoItem(View v) {
		int position = v.getId();
		ArrayList<JipyoChoiceItem> datas = null;
		datas = m_itemsArr;
		JipyoChoiceItem oneItem = datas.get(position);

//		Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items.get(position);
//		System.out.println("Debug_selected_item:"+item.get(0).get("name"));

		//to right list add

		//추가된 리스트의 name에서 COMUtil.JIPYO_ADD_REMARK를 체크하여 마지막 인덱스를 찾아낸다.
		String defaultName = oneItem.getName();

		String cmpName = COMUtil.getAddJipyoTitle(defaultName);

		//같은 이름의 지표항목만 추린다
		Vector<String> sameNames = new Vector<String>();
		Vector<String> sameIndexs = new Vector<String>();
		for(int i=0; i<items2.size(); i++) {
			Vector<Hashtable<String, String>> subItem = (Vector<Hashtable<String, String>>)items2.get(i);
			if(subItem.get(0)==null || (subItem.get(0)!=null && subItem.get(0).size()==0)) {
				continue;
			}
			String subName = subItem.get(0).get("name");
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
			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
			alert.setTitle("");
			alert.setMessage("최대 5개까지 추가할 수 있습니다.");
			alert.setOkButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//알림창 닫기
							dialog.dismiss();
						}
					});
			alert.show();
			COMUtil.g_chartDialog = alert;

			return;
		}
		//2015.04.08 by lyk - 동일한 중복지표의 최대 추가 가능 갯수 처리(최대 5개 까지) end

		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 	
		int lastIndex = 0;
		if(sameIndexs.size()<1) {
			lastIndex = 0;
		} else {
			lastIndex = Integer.parseInt(sameIndexs.lastElement());
		}
		//같은 이름의 지표 리스트 중에서 lastIndex를 찾는다 

		String addName = defaultName + COMUtil.JIPYO_ADD_REMARK + (lastIndex+1);	// jipyoname_1;

//		item.get(0).put("name", String.valueOf(addName));

		Vector<Hashtable<String, String>> addItem = new Vector<Hashtable<String, String>>();
		Hashtable<String, String> addHashItem = new Hashtable<String, String>();
		//type, name, tag
		addHashItem.put("type", "Indicator");
		addHashItem.put("name", String.valueOf(addName));

		//COMUtil.addJipyo() 에서 tag 비교 값으로 사용한다. 
		if((lastIndex+1)>0) {
			int addTag = Integer.parseInt(oneItem.getTag()) * 100 + (lastIndex+1);
			addHashItem.put("tag", String.valueOf(addTag));
		} else {
			addHashItem.put("tag", oneItem.getTag());
		}
		addHashItem.put("detailType", "jipyo");

		addItem.add(addHashItem);

//		addItem.get(0).put("name", String.valueOf(addName));
		//추가된 리스트의 name에서 "_"를 체크하여 마지막 인덱스를 찾아낸다.

		items2.add(addItem);
//		itemChecked2.add(false);

		COMUtil.setAddJipyoList(null); //초기화 
		if(items2!=null && items2.size()>0) {
			Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
			for(int i=0; i<items2.size(); i++) {
				addItems.add(items2.get(i).get(0));
			}
		}

		this.refreshList();

	}

	public void delAddJipyoItem(View v) {
		int position = v.getId();
		ArrayList<JipyoChoiceItem> datas = null;
		datas = m_itemsArr;
		JipyoChoiceItem oneItem = datas.get(position);

		//지표명 비교하여 삭제할 것 
		if(items2!=null && items2.size()>0) {
			for(int i=0; i<items2.size(); i++) {
				if(items2.get(i).get(0).get("name").equals(oneItem.getName())) {
					Vector<Hashtable<String, String>> item = (Vector<Hashtable<String, String>>)items2.get(i);
					items2.remove(item);
					break;
				}
			}
		}

		COMUtil.setAddJipyoList(null); //초기화 
		if(items2!=null && items2.size()>0) {
			Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
			for(int i=0; i<items2.size(); i++) {
				addItems.add(items2.get(i).get(0));
			}
		}

		this.refreshList();

	}
	//2015. 1. 13 by lyk 중복지표 추가/삭제 처리 end

	//2015. 2. 24 차트 상하한가 표시>>
	public interface OnSettingViewEventListener
	{
		public void onUpperLowerLimitCheck();

		//2015. 3. 18 보이는 개수 및 조회 갯수 설정>>
		public void onChangeRequestDataLength(String strData);
		public boolean isUseChangeRequestDataLength();
		public int getRequestDataLength();
		//2015. 3. 18 보이는 개수 및 조회 갯수 설정<<
		public void onChangeNormalSetValues(int nIndex);

		public void onLoadPeriodValue();	//2015. 12. 11 map-chart 간 분틱값 동기화
	}

	public void setOnEventListener(OnSettingViewEventListener l)
	{
		this.eventListener = l;
	}
	//2015. 2. 24 차트 상하한가 표시<<

	//2015. 3. 25 보이는 개수 및 조회 갯수 설정>>
	public void changeRequestDataLength()
	{
		//Base11 쪽으로 조회갯수 변경값 데이터를 넘겨서 처리하게함 (Delegate)
		EditText edRequestDataLength = (EditText)ll.findViewById(context.getResources().getIdentifier("normalset_edit_requestdatalength", "id", context.getPackageName()));
		String strDataLength = edRequestDataLength.getText().toString();

		if(strDataLength.equals(""))
		{
			strDataLength = String.valueOf(eventListener.getRequestDataLength());
		}
		else if(Integer.parseInt(strDataLength) > 999)
		{
			strDataLength = "999";
		}
		else if(Integer.parseInt(strDataLength) <= 0)
		{
			strDataLength = "1";
		}
		edRequestDataLength.setText(strDataLength);

		//값의 변경이 없으면 리턴
		if(eventListener.getRequestDataLength() == Integer.parseInt(strDataLength))
		{
			return;
		}

		eventListener.onChangeRequestDataLength(edRequestDataLength.getText().toString());
		hideKeyPad(edRequestDataLength);
	}
	//2015. 3. 25 보이는 개수 및 조회 갯수 설정<<

	/**
	 * 지표설정창의 각 지표리스트 크기, 항목 등 초기화
	 * */
	protected void initLists()
	{


		//현재 선택된 차트유형 알아와서 차트유형 체크박스의 index 설정  
		String strChart = getChartTypeName();

		if(strChart==null) {
			nChartSelected = 0;
		} else {
			if(strChart.equals("캔들"))
			{
				nChartSelected = 0;
			}

			//2015.04.30 by lyk - 바(시고저종) 유형 추가
			else if(strChart.equals("바"))
			{
                nChartSelected = 1;
//				String bIsOHLCType = COMUtil._mainFrame.mainBase.baseP._chart._cvm.bIsOHLCType;
//				if(bIsOHLCType.equals("0")) {
//					nChartSelected = 1;
//				} else {
//					nChartSelected = 2;
//				}
			}
            else if(strChart.equals("바(시고저종"))
            {
                nChartSelected = 2;
            }
            else if(strChart.equals("종가영역"))
            {
                nChartSelected = 3;
            }
			//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
			else if(strChart.equals("라인"))
			{
				nChartSelected = 3;
			}
			else if(strChart.equals("PnF"))
			{
				nChartSelected = 4;
			}
			else if(strChart.equals("삼선전환도"))
			{
				nChartSelected = 5;
			}
			else if(strChart.equals("스윙"))
			{
				nChartSelected = 6;
			}
			else if(strChart.equals("렌코"))
			{
				nChartSelected = 7;
			}
			//2015.06.23 by lyk - 차트 유형 추가
			else if(strChart.equals("Kagi"))
			{
				nChartSelected = 8;
			}

			else if(strChart.equals("역시계곡선"))
			{
				nChartSelected = 9;
			}
		}


		//체크박스들의 상태를 가지고 있는 array 
		arChkBox = new ArrayList<CheckBox>();

		//보조지표(기술적지표)
		int layoutResId = context.getResources().getIdentifier("jipyoList", "id", context.getPackageName());
		jipyolist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr = FindDataManager.getUserJipyoItems();

		items2 = COMUtil.getJipyoMenuArrayList2();

		makeItems("jipyo");

		jipyolist.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue)  + (m_itemsArr.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("jipyoLayout", "id", context.getPackageName());
		RelativeLayout jipyoLayout = (RelativeLayout)ll.findViewById(layoutResId);
		jipyoLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr.size()*(int)COMUtil.getPixel(1)) )));

		m_scvAdapter = new MyArrayAdapter(context, m_itemsArr, 0);
		jipyolist.setAdapter(m_scvAdapter);

		jipyolist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				onListItemClick(arg0,arg1,arg2,arg3);
			}
		});

		layoutResId = context.getResources().getIdentifier("indicatorscroll", "id", context.getPackageName());
		final ScrollView sv = (ScrollView)ll.findViewById(layoutResId);
		sv.post(new Runnable() {

			@Override
			public void run() {
				sv.scrollTo(0, 0);
			}
		});

		layoutResId = context.getResources().getIdentifier("btn_technicalindicator", "id", context.getPackageName());
		View view = ll.findViewById(layoutResId);
		((Button)view).setSelected(true);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);
				}
			});
		}


		//오버레이(채널지표)
		layoutResId = context.getResources().getIdentifier("overlayList", "id", context.getPackageName());
		overlaylist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr2 = FindDataManager.getUserJipyoItems2();

		makeItems("overlay");

		overlaylist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				(m_itemsArr2.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr2.size()*(int)COMUtil.getPixel(1))  )));
		layoutResId = context.getResources().getIdentifier("overlayLayout", "id", context.getPackageName());
		RelativeLayout overlayLayout = (RelativeLayout)ll.findViewById(layoutResId);
		overlayLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr2.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr2.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("btn_channelindicator", "id", context.getPackageName());
		view = ll.findViewById(layoutResId);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);
				}
			});
		}

		//시장지표(기타지표)
		layoutResId = context.getResources().getIdentifier("otherList", "id", context.getPackageName());
		otherlist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr3 = FindDataManager.getUserJipyoItems3();

		makeItems("other");

		otherlist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				(m_itemsArr3.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr3.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("otherLayout", "id", context.getPackageName());
		RelativeLayout otherLayout = (RelativeLayout)ll.findViewById(layoutResId);

		otherLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr3.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue)  + (m_itemsArr3.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("btn_etcindicator", "id", context.getPackageName());
		view = ll.findViewById(layoutResId);
		if(COMUtil.bIsForeignFuture)
			view.setVisibility(GONE);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);  //2013. 8. 21 지표설정 리스트뷰 제어하는 함수 추가
				}
			});
		}


		//전체지표
		layoutResId = context.getResources().getIdentifier("btn_totalindicator", "id", context.getPackageName());
		view = ll.findViewById(layoutResId);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);
				}
			});
		}
		layoutResId = context.getResources().getIdentifier("totalList", "id", context.getPackageName());
		totallist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr4 = FindDataManager.getUserJipyoItems4();

		makeItems("total");

		totallist.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));

		layoutResId = context.getResources().getIdentifier("totalLayout", "id", context.getPackageName());
		RelativeLayout totalLayout = (RelativeLayout)ll.findViewById(layoutResId);

		totalLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr4.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue))));


		//차트유형
		layoutResId = context.getResources().getIdentifier("chartList", "id", context.getPackageName());
		chartlist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr5 = FindDataManager.getUserJipyoItems4();

		makeItems("charttype");

		chartlist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				(m_itemsArr5.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr5.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("chartLayout", "id", context.getPackageName());
		LinearLayout charttypeLayout = (LinearLayout)ll.findViewById(layoutResId);

		charttypeLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr5.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr5.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("btn_charttypeindicator", "id", context.getPackageName());
		view = ll.findViewById(layoutResId);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);
				}
			});
		}

		currentType = "jipyo";	//2015. 1. 13 중복지표 추가 UI 수정
	}

	protected void initChartTypeList()
	{


		//현재 선택된 차트유형 알아와서 차트유형 체크박스의 index 설정
		String strChart = getChartTypeName();

		if(strChart==null) {
			nChartSelected = 0;
		} else {
			if(strChart.equals("캔들"))
			{
				nChartSelected = 0;
			}

			//2015.04.30 by lyk - 바(시고저종) 유형 추가
			else if(strChart.equals("바"))
			{
				nChartSelected = 1;
//				String bIsOHLCType = COMUtil._mainFrame.mainBase.baseP._chart._cvm.bIsOHLCType;
//				if(bIsOHLCType.equals("0")) {
//					nChartSelected = 1;
//				} else {
//					nChartSelected = 2;
//				}
			}
			else if(strChart.equals("바(시고저종"))
			{
				nChartSelected = 2;
			}
			//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
			else if(strChart.equals("라인"))
			{
				nChartSelected = 3;
			}
			else if(strChart.equals("FLOW"))
			{
				nChartSelected = 4;
			}
			else if(strChart.equals("계단"))
			{
				nChartSelected = 5;
			}
			else if(strChart.equals("PnF"))
			{
				nChartSelected = 6;
			}
			else if(strChart.equals("삼선전환도"))
			{
				nChartSelected = 7;
			}
			else if(strChart.equals("스윙"))
			{
				nChartSelected = 8;
			}
			else if(strChart.equals("렌코"))
			{
				nChartSelected = 9;
			}
			//2015.06.23 by lyk - 차트 유형 추가
			else if(strChart.equals("Kagi"))
			{
				nChartSelected = 10;
			}
			else if(strChart.equals("영역라인"))
			{
				nChartSelected = 11;
			}
			else if(strChart.equals("역시계곡선"))
			{
				nChartSelected = 12;
			}
			else if(strChart.equals("분산형"))
			{
				nChartSelected = 13;
			}
			else if(strChart.equals("Heikin-Ashi"))
			{
				nChartSelected = 14;
			}
			else if(strChart.equals("캔들볼륨"))
			{
				nChartSelected = 15;
			}
			else if(strChart.equals("이큐볼륨"))
			{
				nChartSelected = 16;
			}
		}


		//체크박스들의 상태를 가지고 있는 array
		arChkBox = new ArrayList<CheckBox>();


		//차트유형
		int layoutResId = context.getResources().getIdentifier("chartList", "id", context.getPackageName());
		chartlist = (ListView)ll.findViewById(layoutResId);
		m_itemsArr5 = FindDataManager.getUserJipyoItems4();

		makeItems("charttype");

		chartlist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				(m_itemsArr5.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr5.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("chartLayout", "id", context.getPackageName());
		LinearLayout charttypeLayout = (LinearLayout)ll.findViewById(layoutResId);

		charttypeLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(m_itemsArr5.size() * (int)COMUtil.getPixel(nIndicatorRowHeightValue) + (m_itemsArr5.size()*(int)COMUtil.getPixel(1)) )));

		layoutResId = context.getResources().getIdentifier("btn_charttypeindicator", "id", context.getPackageName());
		View view = ll.findViewById(layoutResId);
		if (view != null)
		{
			view.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					setIndicatorListViewVisibility(v);
				}
			});
		}
		layoutResId = context.getResources().getIdentifier("tv_charttype", "id", context.getPackageName());
		m_tv_charttype = ll.findViewById(layoutResId);
		m_tv_charttype.setText(strChart);

		layoutResId = context.getResources().getIdentifier("ll_charttype", "id", context.getPackageName());
		LinearLayout ll_charttype = (LinearLayout)ll.findViewById(layoutResId);
		ll_charttype.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Button btn_charttypeindicator  = (Button)ll.findViewWithTag("1190");
				btn_charttypeindicator.performClick();
			}
		});

		currentType = "charttype";	//2015. 1. 13 중복지표 추가 UI 수정
	}
	public void hideKeyPad()
	{
		InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edRightPadding.getWindowToken(), 0);
		edRightPadding.clearFocus();
	}
	public int getInitBtnType()
	{
//		if(m_nType==0)
//			return 0;

		int layoutResId = context.getResources().getIdentifier("periodSetBtn", "id", context.getPackageName());
		Button periodSetBtn = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("normalSetBtn", "id", context.getPackageName());
		Button normalSetBtn = (Button)ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("toolbarSetBtn", "id", context.getPackageName());
		Button toolbarSetBtn = (Button)ll.findViewById(layoutResId);

		if(periodSetBtn.isSelected())
		{
			return 2;

		}else if(normalSetBtn.isSelected())
		{
			return 1;
		}else if(toolbarSetBtn.isSelected())
		{
			return 3;
		}
		return 0;
	}
	public void initPeriod()
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		if(COMUtil.bIsForeignFuture) {

			for (int i = 0; i < 14; i++) {

				int layoutResId = this.getContext().getResources().getIdentifier(String.valueOf(ids[i]), "id", context.getPackageName());
				LinearLayout ll = (LinearLayout)periodui.findViewById(layoutResId);

				layoutResId = context.getResources().getIdentifier("jugiset_row1_edit", "id", context.getPackageName());
				EditText edit = (EditText) ll.findViewById(layoutResId);
				int nValue = base11.astrPeriodDefaultData[i];
				edit.setText(String.valueOf(nValue));

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
				Button btnDay = (Button)ll.findViewById(layoutResId);
				int nBtn = base11.astrPeriodDayBtnDefaultData[i];
				if(nBtn == 1)
				{
					btnDay.setSelected(true);
					edit.setEnabled(false);
					edit.setText("일");
				}
				else
					btnDay.setSelected(false);

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
				Button btnWeek = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodWeekBtnDefaultData[i];
				if(nBtn == 1)
				{
					btnWeek.setSelected(true);
					edit.setEnabled(false);
					edit.setText("주");
				}
				else
					btnWeek.setSelected(false);

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
				Button btnMon = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodMonBtnDefaultData[i];
				if(nBtn == 1)
				{
					btnMon.setSelected(true);
					edit.setEnabled(false);
					edit.setText("월");
				}
				else
					btnMon.setSelected(false);

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
				Button btnMin = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodMinBtnDefaultData[i];
				if(nBtn == 1)
				{
					btnMin.setSelected(true);
					edit.setEnabled(true);
				}
				else
					btnMin.setSelected(false);

				layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
				Button btnTic = (Button)ll.findViewById(layoutResId);
				nBtn = base11.astrPeriodTikBtnDefaultData[i];
				if(nBtn == 1)
				{
					btnTic.setSelected(true);
					edit.setEnabled(true);
				}
				else
					btnTic.setSelected(false);
			}
		}
		else {


			COMUtil.resetBunTicPeriodList(false);
			this.initPeriodValues();
//			for (int i = 0; i < 8; i++) {
//				int layoutResId = context.getResources().getIdentifier("periodset_row1_edit_bun_" + String.valueOf(i + 1), "id", context.getPackageName());
//				EditText edit = (EditText) ll.findViewById(layoutResId);
//				int nValue = base11.astrMinDefaultData[i];
//				edit.setText(String.valueOf(nValue));
//
//				layoutResId = context.getResources().getIdentifier("periodset_row1_edit_tic_" + String.valueOf(i + 1), "id", context.getPackageName());
//				edit = (EditText) ll.findViewById(layoutResId);
//				nValue = base11.astrTikDefaultData[i];
//				edit.setText(String.valueOf(nValue));
//
//				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
//				if (i < 3) {
//					layoutResId = context.getResources().getIdentifier("periodset_row1_edit_sec_" + String.valueOf(i + 1), "id", context.getPackageName());
//					edit = (EditText) ll.findViewById(layoutResId);
//					nValue = base11.astrSecDefaultData[i];
//					edit.setText(String.valueOf(nValue));
//				}
//				//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
//			}
		}

		savePeriodValues();
	}

    public void initGeneral()
    {
        int layoutResId;

        for(int i = 0; i < nChkCount; i++)
        //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
        {
            String strId = "normalset_check_" + String.valueOf(i+1);
            layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());

            normalChkbox[i] = (CheckBox)ll.findViewById(layoutResId);

            layoutResId = context.getResources().getIdentifier("normalset_tv_"+String.valueOf(i+1), "id", context.getPackageName());
            final TextView tvNormal = (TextView)ll.findViewById(layoutResId);

            strId = "convenient_rowlinear_" + String.valueOf(i+1);
            layoutResId = context.getResources().getIdentifier(strId, "id", context.getPackageName());

            switch(i)
            {
                case 0:
                    normalChkbox[i].setChecked(COMUtil.isAdjustedStock());
                    break;
                case 1:
                    normalChkbox[i].setChecked(COMUtil.isyScaleShow());
                    break;
                case 2:
                    normalChkbox[i].setChecked(COMUtil.isyJonggaShow());
                    break;
                case 3:
                    normalChkbox[i].setChecked(COMUtil.isMinMaxShow());
                    break;
                //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
                case 4:
                    normalChkbox[i].setChecked(COMUtil.isChuseLineValueTextShow());
                    break;
                //2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가>>
                //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
                case 5:
                    normalChkbox[i].setChecked(COMUtil.isDayDivisionLineShow());
                    break;
                //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리 >>
                //2015. 2. 24 차트 상하한가 표시>>
                case 6:
                    normalChkbox[i].setChecked(COMUtil.isBongCntShow());
                    break;
//			//2015.04.16 by lyk - 주기별 차트 설정 옵션<<
                case 7:
                    normalChkbox[i].setChecked(COMUtil.isPeriodConfigSave());
                    break;
                case 8:
                    normalChkbox[i].setChecked(COMUtil.isUsePaddingRight());
                    break;
				case 9:
					normalChkbox[i].setChecked(COMUtil.isKoreanTime());
					break;
				case 10:
					normalChkbox[i].setChecked(COMUtil.isUseSkipTick());
					break;
				case 11:
					normalChkbox[i].setChecked(COMUtil.isUseSkipVolume());
					break;
				case 12:
					normalChkbox[i].setChecked(COMUtil.isHighLowShow());
					break;
				case 13:
					normalChkbox[i].setChecked(COMUtil.isShowYScaleLine());
					break;
				case 14:
					normalChkbox[i].setChecked(COMUtil.isShowXScaleLine());
					break;
				case 15:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsCandleMinMax());
					break;
				case 16:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsGapRevision());
					break;
				case 17:
					normalChkbox[i].setChecked(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getIsLog());
					break;
				case 18:
					normalChkbox[i].setChecked(COMUtil.isCrossLineJongga());
					break;
            }
            //2015. 2. 24 차트 상하한가 표시<<
            final LinearLayout linear = (LinearLayout)ll.findViewById(layoutResId);
            final int index = i;

            normalChkbox[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckNormal(index);

                    if( ((CheckBox)v).isChecked() )
                    {
//						tvNormal.setTextColor(Color.rgb(239, 115, 28));
                    }
                    else
                    {
                        tvNormal.setTextColor(textColor);
                    }
                }
            });

            //2013. 3. 25  편의설정 행 터치시 체크 및 적용
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index != 8) {
                        normalChkbox[index].setChecked(!normalChkbox[index].isChecked());
                        onCheckNormal(index);

                        if (normalChkbox[index].isChecked()) {
                            //				tvNormal.setTextColor(Color.rgb(239, 115, 28));
                        } else {
                            tvNormal.setTextColor(textColor);
                        }
                    }
                }
            });

            if(normalChkbox[i].isChecked())
            {
                //			tvNormal.setTextColor(Color.rgb(239, 115, 28));
            }
            else
            {
                tvNormal.setTextColor(textColor);
            }
        }

        //2013. 8. 22 일반설정에 '캔들설정' 항목 추가>>
        layoutResId = context.getResources().getIdentifier("convenient_rowlinear_candle", "id", context.getPackageName());
        LinearLayout linear_candleSet = (LinearLayout)ll.findViewById(layoutResId);

        //2016. 1. 11 종가 등락률 옵션처리>>
        //일반설정 - 종가 등락률 대비
        layoutResId = context.getResources().getIdentifier("lastday_radio_btn", "id", context.getPackageName());
        RadioButton radioLastDay = (RadioButton) ll.findViewById(layoutResId);
        layoutResId = context.getResources().getIdentifier("lastbong_radio_btn", "id", context.getPackageName());
        RadioButton radioLastBong = (RadioButton) ll.findViewById(layoutResId);

        if(COMUtil.isyJonggaCurrentPrice())
        {
            radioLastDay.setChecked(true);
			radioLastBong.setChecked(false);
			radioLastDay.setTextColor(Color.WHITE);
			radioLastBong.setTextColor(Color.rgb(17, 17, 17));
        }
        else
        {
			radioLastDay.setChecked(false);
            radioLastBong.setChecked(true);
			radioLastBong.setTextColor(Color.WHITE);
			radioLastDay.setTextColor(Color.rgb(17, 17, 17));
        }

        layoutResId = context.getResources().getIdentifier("bns_gbn_white_radio_btn", "id", context.getPackageName());
        RadioButton radioWhite = (RadioButton) ll.findViewById(layoutResId);
        layoutResId = context.getResources().getIdentifier("bns_gbn_black_radio_btn", "id", context.getPackageName());
        RadioButton radioBlack = (RadioButton) ll.findViewById(layoutResId);
        //2015. 3. 4 차트 테마 메인따라가기 추가>>
        layoutResId = context.getResources().getIdentifier("bns_gbn_auto_radio_btn", "id", context.getPackageName());
        RadioButton radioAuto = (RadioButton) ll.findViewById(layoutResId);

		//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
		layoutResId = context.getResources().getIdentifier("zoom_radio_btn_normal", "id", context.getPackageName());
		final RadioButton zoom_radio_btn_normal = (RadioButton) ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("zoom_radio_btn_detail", "id", context.getPackageName());
		final RadioButton zoom_radio_btn_detail = (RadioButton) ll.findViewById(layoutResId);

		if(COMUtil.isDetailScroll())
		{
			zoom_radio_btn_detail.setChecked(true);
			zoom_radio_btn_normal.setChecked(false);
		}
		else
		{
			zoom_radio_btn_normal.setChecked(true);
			zoom_radio_btn_detail.setChecked(false);
		}
		zoom_radio_btn_detail.setOnClickListener(radioListener);
		zoom_radio_btn_normal.setOnClickListener(radioListener);

		layoutResId = context.getResources().getIdentifier("rl_zoom_normal", "id", context.getPackageName());
		RelativeLayout rl_zoom_normal = (RelativeLayout) ll.findViewById(layoutResId);
		rl_zoom_normal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom_radio_btn_normal.performClick();
			}
		});

		layoutResId = context.getResources().getIdentifier("rl_zoom_detail", "id", context.getPackageName());
		RelativeLayout rl_zoom_detail = (RelativeLayout) ll.findViewById(layoutResId);
		rl_zoom_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoom_radio_btn_detail.performClick();
			}
		});



		layoutResId = context.getResources().getIdentifier("hubong_radio_btn", "id", context.getPackageName());
		final RadioButton hubong_radio_btn = (RadioButton) ll.findViewById(layoutResId);
		layoutResId = context.getResources().getIdentifier("silbong_radio_btn", "id", context.getPackageName());
		final RadioButton silbong_radio_btn = (RadioButton) ll.findViewById(layoutResId);

		if(COMUtil.isUseHubong())
		{
			hubong_radio_btn.setChecked(true);
			silbong_radio_btn.setChecked(false);
		}
		else
		{
			hubong_radio_btn.setChecked(false);
			silbong_radio_btn.setChecked(true);
		}
		hubong_radio_btn.setOnClickListener(radioListener);
		silbong_radio_btn.setOnClickListener(radioListener);

		layoutResId = context.getResources().getIdentifier("rl_hubong", "id", context.getPackageName());
		RelativeLayout rl_hubong = (RelativeLayout) ll.findViewById(layoutResId);
		rl_hubong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hubong_radio_btn.performClick();
			}
		});

		layoutResId = context.getResources().getIdentifier("rl_silbong", "id", context.getPackageName());
		RelativeLayout rl_silbong = (RelativeLayout) ll.findViewById(layoutResId);
		rl_silbong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				silbong_radio_btn.performClick();
			}
		});
		//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<

        if(COMUtil.bIsAutoTheme) {
            //테마 메인따라가기일 때
            radioAuto.setChecked(true);
            radioAuto.setTextColor(Color.rgb(203, 29, 118));
            radioWhite.setTextColor(Color.rgb(21, 21, 21));
            radioBlack.setTextColor(Color.rgb(21, 21, 21));
        }
        else
        {
            if(COMUtil._mainFrame.mainBase.baseP._chart._cvm.getSkinType() != COMUtil.SKIN_BLACK)
            {
                radioWhite.setChecked(true);
				radioBlack.setChecked(false);
				radioWhite.setTextColor(Color.WHITE);
				radioBlack.setTextColor(Color.rgb(17, 17, 17));
            }
            else
            {
				radioWhite.setChecked(false);
                radioBlack.setChecked(true);
				radioBlack.setTextColor(Color.WHITE);
				radioWhite.setTextColor(Color.rgb(17, 17, 17));
            }
        }

        edRightPadding.setText(COMUtil.getPaddingRight()+"px");

        if (COMUtil.isUsePaddingRight()) {Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
            edRightPadding.setEnabled(true);
        } else {
			edRightPadding.setEnabled(false);
		}

		edSkipTick.setText(COMUtil._mainFrame.strSkipTick);
		edSkipTick.setEnabled(COMUtil.isUseSkipTick());

		edSkipVolume.setText(COMUtil._mainFrame.strSkipVol);
		edSkipVolume.setEnabled(COMUtil.isUseSkipTick());
    }

	public void selectDay(int nIdx)
	{

		int countselBtn = 0;
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
		baseLayout = (LinearLayout)periodui.findViewById(layoutResId);
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
		final Button btnDay = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
		final Button btnWeek = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
		final Button btnMon = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
		final Button btnMin = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
		final Button btnTic = (Button)ll.findViewById(layoutResId);

		btnDay.setSelected(true);
		btnWeek.setSelected(false);
		btnMon.setSelected(false);
		btnMin.setSelected(false);
		btnTic.setSelected(false);

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row1_edit", "id", this.getContext().getPackageName());
		EditText tmp = (EditText) ll.findViewById(layoutResId); //지표 설정값.
		tmp.setText("일");
		tmp.setEnabled(false);
	}

	public void selectWeek(int nIdx)
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		int countselBtn = 0;
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
		baseLayout = (LinearLayout)periodui.findViewById(layoutResId);
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
		final Button btnDay = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
		final Button btnWeek = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
		final Button btnMon = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
		final Button btnMin = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
		final Button btnTic = (Button)ll.findViewById(layoutResId);

		btnDay.setSelected(false);
		btnWeek.setSelected(true);
		btnMon.setSelected(false);
		btnMin.setSelected(false);
		btnTic.setSelected(false);

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row1_edit", "id", this.getContext().getPackageName());
		EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		tmp.setText("주");
		tmp.setEnabled(false);
	}

	public void selectMon(int nIdx)
	{
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		int countselBtn = 0;
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
		baseLayout = (LinearLayout)periodui.findViewById(layoutResId);
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
		final Button btnDay = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
		final Button btnWeek = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
		final Button btnMon = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
		final Button btnMin = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
		final Button btnTic = (Button)ll.findViewById(layoutResId);

		btnDay.setSelected(false);
		btnWeek.setSelected(false);
		btnMon.setSelected(true);
		btnMin.setSelected(false);
		btnTic.setSelected(false);

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row1_edit", "id", this.getContext().getPackageName());
		EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		tmp.setText("월");
		tmp.setEnabled(false);
	}

	public void selectMin(int nIdx)
	{
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
		baseLayout = (LinearLayout)periodui.findViewById(layoutResId);
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
		final Button btnDay = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
		final Button btnWeek = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
		final Button btnMon = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
		final Button btnMin = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
		final Button btnTic = (Button)ll.findViewById(layoutResId);

		btnDay.setSelected(false);
		btnWeek.setSelected(false);
		btnMon.setSelected(false);
		btnMin.setSelected(true);
		btnTic.setSelected(false);

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row1_edit", "id", this.getContext().getPackageName());
		EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		tmp.setText("5");
		tmp.setEnabled(true);
	}

	public void selectTic(int nIdx)
	{
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		layoutResId = this.context.getResources().getIdentifier("minview", "id", this.context.getPackageName());
		baseLayout = (LinearLayout)periodui.findViewById(layoutResId);
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
		final Button btnDay = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
		final Button btnWeek = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
		final Button btnMon = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
		final Button btnMin = (Button)ll.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
		final Button btnTic = (Button)ll.findViewById(layoutResId);

		btnDay.setSelected(false);
		btnWeek.setSelected(false);
		btnMon.setSelected(false);
		btnMin.setSelected(false);
		btnTic.setSelected(true);

		layoutResId = this.getContext().getResources().getIdentifier("jugiset_row1_edit", "id", this.getContext().getPackageName());
		EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		tmp.setText("10");
		tmp.setEnabled(true);
	}

	//2017.04.21 선택된 주기의 선택 상태를 알려주는 함수. by PJM
	public Boolean getSelBtn(String str)
	{
		boolean bselected;
		boolean bresult= true;
		for(int i=0; i<14; i++)
		{
			int layoutResId = this.getContext().getResources().getIdentifier(String.valueOf(ids[i]), "id", context.getPackageName());
			LinearLayout ll = (LinearLayout)periodui.findViewById(layoutResId);
			layoutResId = this.getContext().getResources().getIdentifier(str, "id", this.getContext().getPackageName());
			Button btn = (Button)ll.findViewById(layoutResId);
			bselected = btn.isSelected();
			arBtn[i] = (btn.isSelected() == true) ? 1 : 0;
			if(bselected==true)
				bresult=false;
		}
		return bresult;
	}

	public void initPeriodValueList()
	{

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		for(int i = 0; i < 14; i++) {
			int layoutResId = this.getContext().getResources().getIdentifier(String.valueOf(ids[i]), "id", context.getPackageName());
			LinearLayout ll = (LinearLayout) periodui.findViewById(layoutResId);

			layoutResId = context.getResources().getIdentifier("jugiset_row1_edit", "id", context.getPackageName());
			editPeriod = (EditText) ll.findViewById(layoutResId);
			int nValue = base11.astrPeriodData[i];
			editPeriod.setText(String.valueOf(nValue));

			layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_day", "id", this.getContext().getPackageName());
			Button btnDay = (Button) ll.findViewById(layoutResId);
			//기본값 세팅
			final String periodDay = "jugiset_row_day";
			int nBtn = base11.astrPeriodDayBtnData[i];
			if (nBtn == 1) {
				btnDay.setSelected(true);
				editPeriod.setText("일");
				editPeriod.setEnabled(false);
			} else
				btnDay.setSelected(false);

			layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_week", "id", this.getContext().getPackageName());
			Button btnWeek = (Button) ll.findViewById(layoutResId);
			//기본값 세팅
			final String periodWeek = "jugiset_row_day";
			nBtn = base11.astrPeriodWeekBtnData[i];
			if (nBtn == 1) {
				btnWeek.setSelected(true);
				editPeriod.setText("주");
				editPeriod.setEnabled(false);
			} else
				btnWeek.setSelected(false);

			layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_mon", "id", this.getContext().getPackageName());
			Button btnMon = (Button) ll.findViewById(layoutResId);
			//기본값 세팅
			final String periodMon = "jugiset_row_mon";
			nBtn = base11.astrPeriodMonBtnData[i];
			if (nBtn == 1) {
				btnMon.setSelected(true);
				editPeriod.setText("월");
				editPeriod.setEnabled(false);
			} else
				btnMon.setSelected(false);

			layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_min", "id", this.getContext().getPackageName());
			Button btnMin = (Button) ll.findViewById(layoutResId);
			//기본값 세팅
			nBtn = base11.astrPeriodMinBtnData[i];
			if (nBtn == 1) {
				btnMin.setSelected(true);
				editPeriod.setEnabled(true);
			} else
				btnMin.setSelected(false);

			layoutResId = this.getContext().getResources().getIdentifier("jugiset_row_tic", "id", this.getContext().getPackageName());
			Button btnTic = (Button) ll.findViewById(layoutResId);
			//기본값 세팅
			nBtn = base11.astrPeriodTikBtnData[i];
			if (nBtn == 1) {
				btnTic.setSelected(true);
				editPeriod.setEnabled(true);
			} else
				btnTic.setSelected(false);
		}
	}
}
