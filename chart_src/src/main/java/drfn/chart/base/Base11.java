package drfn.chart.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import drfn.UserProtocol;
import drfn.chart.NeoChart2;
import drfn.chart.base.IndicatorConfigView.OnSettingViewEventListener;
import drfn.chart.block.Block;
import drfn.chart.comp.ChartItemView;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.comp.DataField;
import drfn.chart.event.ChartChangedListener;
import drfn.chart.event.ChartEvent;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

public class Base11 extends Base implements ChartChangedListener, OnSettingViewEventListener {	//2015. 2. 24 차트 상하한가 표시

	protected static int _TAG_STORAGE_TYPE = 200001; // 저장된 차트 타입
	protected static int _PAD_TICKER_HEIGHT = 0; // 패드 하단 티커의 높이
	protected static int _PHONE_TICKER_HEIGHT = 0; // 폰 하단 티커의 높이
	public static String _TAG_BACK_IMG = "backImg";

	String[] disablegraph={"일본식봉차트","로그차트","미국식봉차트"};

	BaseChart chart2;
//	public InputPanel_Foreign inputPanel;
	ViewPanel viewP;// 상위 시고저종 패널
	RelativeLayout layout;
	String[][] data_info_DWM = null;

	//2015. 3. 18 보이는 개수 및 조회 갯수 설정
	private int m_nRequestDataLength = 200;

	public Vector<BaseChart> chartList = null;
	public BaseChart_Multi compareChart = null;
	public int nDivideType = 11;
	int m_nExtendChart = -1;
	public boolean m_bSyncJongMok = false;
	public boolean m_bSyncJugi = false;
	private boolean isRotation = false;
	int resultCount = 0;
	boolean countStart = false;
	public int m_nRotateIndex = -1;
	int m_nSelIndex = -1;
	int m_nCompareRotateIndex = -1;
	int m_nQueryChartIndex = 0;
	public int m_nTotNum = 1;
	int m_nRowNum = 1;
	int m_nColumnNum = 1;
	boolean bSendCompareData = false;
	int m_nCompareRoteIndex = -1;
	boolean m_bIsShowToolBar = true;
	boolean m_bIsAnalToolBar = true;
	// Handler mHandler = new Handler();
	Context context = null;
	//    boolean isSyncJongmok = false;
//    boolean isSyncJugi = false;
	int storageDivideIndex = -1;
	boolean m_bDWMMClick = false;
	public boolean m_bMultiChart = false;
	boolean m_bMultiData = false;
	public boolean m_bCompareChart = false;
	boolean m_bConnectSocket = false;

	//2012. 9. 6  분틱차트주기 초기값 
	public int[] astrMinDefaultData = {1, 3, 5, 10, 15, 30, 45, 60, 120};
	public int[] astrTikDefaultData = {1, 3, 5, 10, 15, 30, 60, 120};
    public int[] astrSecDefaultData = {5, 10, 30};  //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
	public int[] astrPeriodDefaultData = {0, 1, 3, 5, 10, 15, 30, 1, 3, 5, 10, 30, 0, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가
	//2013. 1. 21  분틱차트주기 체크박스 초기값 
	public int[] astrMinCheckDefaultData = {1, 1, 1, 1, 1, 1, 1, 1, 1};
	public int[] astrTikCheckDefaultData = {1, 1, 1, 1, 1, 1, 1, 1};
    public int[] astrSecCheckDefaultData = {1, 1, 1};   //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가

	public int[] astrPeriodDayBtnDefaultData = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가 일버튼
	public int[] astrPeriodWeekBtnDefaultData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가 주버튼
	public int[] astrPeriodMonBtnDefaultData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}; //2017.04.14 by PJM >> 해외선물 주기설정 추가 월버튼
	public int[] astrPeriodMinBtnDefaultData = {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가 분버튼
	public int[] astrPeriodTikBtnDefaultData = {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가 틱버튼

	//2012. 9. 6 분틱차트주기 저장버퍼 
	public int[] astrMinData = {1, 3, 5, 10, 15, 30, 45, 60, 120};
	public int[] astrTikData = {1, 3, 5, 10, 15, 30, 60, 120};
    public int[] astrSecData = {5, 10, 30}; //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
	public int[] astrPeriodData = {0, 1, 3, 5, 10, 15, 30, 1, 3, 5, 10, 30, 0, 0}; //2017.04.14 by PJM >> 해외선물 주기설정 추가
	//2013. 1. 21 분틱차트주기 저장버퍼 
	public int[] astrMinCheckData = {1, 1, 1, 1, 1, 1, 1, 1, 1};
	public int[] astrTikCheckData = {1, 1, 1, 1, 1, 1, 1, 1};
    public int[] astrSecCheckData = {1, 1, 1};  //2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가

	public int[] astrPeriodDayBtnData = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public int[] astrPeriodWeekBtnData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
	public int[] astrPeriodMonBtnData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
	public int[] astrPeriodMinBtnData = {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
	public int[] astrPeriodTikBtnData = {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0};

	public boolean divideStorageType = false;
	public boolean m_bSyncIndicator = false;
	public boolean isInit = true;
	public boolean isInitChart = false; //차트 초기화시 처리 
	public boolean isSendMarket = false;
	private Hashtable<String, String> gijun_datas = null;

	//2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리 >>
	private boolean bIsAnotherColorWhiteSkin = false;
	//2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리<<
	private int m_nToolbarWidth = 48;


	public boolean isJisuType = false; //주요지수 차트 타입 
	ArrayList<Button> m_arrButtons;

	//2013.10.04 by LYH >> 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음 <<
	private boolean m_bGetData = false;

	//2015. 3. 18 보이는 개수 및 조회 갯수 설정
	private boolean m_bUseChangeRequestDataLength = true;
	
	private boolean m_bRotateEnd = false;

	public boolean m_bFXChart = false; //2017.05.15 fx멀티차트

	int oneTouchTopMargin = 0; //2019.04.15 원터치 차트설정불러오기 추가 - lyj

	boolean m_bIsOpenedToolbar = false;

//	//2013. 1. 25  분석툴바 버튼 눌렸을 시 토스트메시지 띄우기 
//	private String[] astrToolbarNames = {"추세선", "십자선", "수직선", "수평선", "삼등분선", "사등분선", "피보나치아크", "피보나치팬", "피보나치시간대", "사각형", "원형", "피보나치조정대",
//										"갠팬", "갠그리드", "스피드라인", "앤드류스피치포크", "문자", "삭제", "전체삭제", "갠선", "가속저항호", "가속저항팬", "사이클구간","직선회귀선",  
//										"직선회귀채널", "엘리어트파동선","삼각형", "하향갠팬", "일주월분"};

	/**
	 * 이전/다음 스크롤 버튼 토글 핸들러
	 *
	 * 버튼 표시/숨기기
	 *
	 * @since 1.0.0
	 */
	private Handler mScrollButtonHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
//			System.out.println("#####mScrollButtonHandler: "+msg.what);
		}
	};

	/**
	 * 탑 스크롤 버튼
	 *
	 * @since 1.0.0
	 */
	private FrameLayout mUpScrollButton;

	/**
	 * 다운 스크롤 버튼
	 *
	 * @since 1.0.0
	 */
	private FrameLayout mDownScrollButton;

	/**
	 * 도구 분석툴 스크롤 뷰
	 *
	 */
	private ScrollView analToolScrollView;

	//2019.04.15 원터치 차트설정불러오기 추가 - lyj
	public RelativeLayout oneTouchLayout = null;
	public OneTouchItemView oneTouchItem = null;
	//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

	public Base11(Context context, RelativeLayout layout) {
		super(context);
		this.layout = layout;
		this.context = context;
		frame.right = COMUtil.chartWidth;
		frame.bottom = COMUtil.chartHeight;

		if(COMUtil._mainFrame.bIsLineChart)
			this.setBackgroundColor(Color.TRANSPARENT);
		else
			this.setBackgroundColor(Color.WHITE);

//      ImageView backImg = new ImageView(this.context);
//      Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.main_bg);
//      backImg.layout(0, 0, frame.width(), frame.height());
//      backImg.setImageBitmap(image);  
//      backImg.setScaleType(ImageView.ScaleType.FIT_XY);
//      
//      layout.addView(backImg);
//      backImg.destroyDrawingCache();

		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = context.getResources().getIdentifier("base11", "layout", context.getPackageName());
		final View backImg = factory.inflate(layoutResId, null);
		backImg.setTag(_TAG_BACK_IMG);
		layout.addView(backImg);

		dateType = "일";

		if (COMUtil.apCode.equals("10101")) {
			data_info_DWM = COMUtil.data_info_stock;
		} else if (COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) {
			data_info_DWM = COMUtil.data_info_stock;
		} else if (COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE)) {
			data_info_DWM = COMUtil.data_info_future;
		}

		isInitChart = false;

		gijun_datas = new Hashtable<String, String>();
	}

	public void init() {
		setPanel();
	}
	String[][] data_info_TIC={
			{"자료일자","6","HHMMSS","유"},
			{"Attr","1","× 1","무"},
			{"가격 ","9","× 1","무"},
			{"Attr","1","× 1","무"},
			{"기본거래량","9","× 1","무"},
			{"Attr","1","× 1","무"},
	};

	String[][] data_info_M={
			{"자료일자","12","HHMMSS","유"},
			{"시가","7","× 1","무"},
			{"고가","7","× 1","무"},
			{"저가","7","× 1","무"},
			{"종가","7","× 1","무"},
			{"기본거래량","8","× 1","무"},
			{"피봇2차저항","7","× 1","무"},
			{"피봇1차저항","7","× 1","무"},
			{"피봇가","7","× 1","무"},
			{"피봇1차지지","7","× 1","무"},
			{"피봇2차지지","7","× 1","무"},
	};

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	private void setPanel() {
		// int left = (int)COMUtil.getPixel(COMUtil.mainLeftMargin);
		int top = (int) COMUtil.getPixel(COMUtil.mainTopMargin);

		// Orientation에 따라 계산된 width와 height
		int width = frame.width();
		// int height = frame.height();

		// 툴바 사용여부 처리
		// COMUtil.isAnalToolbar 사용.
		m_bIsAnalToolBar = COMUtil.isAnalToolbar;

		// 툴바 보임 처리
		this.m_bIsShowToolBar = COMUtil.showAnalToolbar == true;

		if (COMUtil.bIsCompare == true) {
			bSendCompareData = true;
			this.m_bCompareChart = true;
		} else {
			this.m_bCompareChart = false;
		}

		//this.m_bMultiChart = COMUtil.bIsMulti == true;
		
		if(!COMUtil._mainFrame.bIsLineChart)
		{
			if (this.mainFrame!=null && COMUtil.bIsMulti == true) {
				this.m_bMultiChart = true;
				COMUtil.chartMode = COMUtil.DIVIDE_CHART;
			} else {
				this.m_bMultiChart = false;
				COMUtil.chartMode = COMUtil.BASIC_CHART;
			}
		}
		
		if (!COMUtil.apiMode) {
			COMUtil.mainTopMargin = 45;
//			inputPanel = new InputPanel_Foreign(Base11.this, this.context,
//					this.layout);
//			inputPanel.setFocusable(true);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//					width, top);
//			inputPanel.setLayoutParams(lp);
//			inputPanel.setBase(this);
//			this.layout.addView(inputPanel);
//			inputPanel.init();

			_PAD_TICKER_HEIGHT = 53; // 패드 하단 티커의 높이
			_PHONE_TICKER_HEIGHT = 18; // 폰 하단 티커의 높이
		} else {
			//2019.04.15 원터치 차트설정불러오기 추가 - lyj
			if(COMUtil._mainFrame.isOneTouchSet) {
				if (COMUtil._mainFrame.bShowOneTouch) {
                    showOneTouch(true);
				} else {
                    showOneTouch(false);
                }
			}
			//2019.04.15 원터치 차트설정불러오기 추가 - lyj end
			COMUtil.mainTopMargin = 0;
		}

		if (!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			this.m_bIsShowToolBar = false;
		}
		// 2012.09.05 by LYH >> 비교차트 추가

		if(this.mainFrame!=null && mainFrame.m_bFXChart)
		{
			this.m_bFXChart = true;
		}

		if (m_bCompareChart) {
			// BaseChart chart = addBaseChart(0, 0);
			if (chartList == null) {
				chartList = new Vector<BaseChart>();
				COMUtil.divideChartList = chartList;
			}
			BaseChart_Multi chart1 = showCompareChart(true);
			chart1.setSkinType(COMUtil.getSkinType());
			COMUtil._neoChart = chart1;
			COMUtil.preChartMode = COMUtil.chartMode;
			COMUtil.chartMode = COMUtil.COMPARE_CHART;
			// _chart=chart;

			m_nCompareRotateIndex = -1;
		} else {
			BaseChart chart = addBaseChart(0, 0);
			// 2012.11.29 by LYH >> neochart에 ctlchartex 연결 <<
			// chart.userProtocol = COMUtil._mainFrame.userProtocol;
			// if(m_bIsShowToolBar == false)
			// chart.removeDelButton();

			COMUtil._neoChart = chart;
			_chart = chart;
		}

		if (!COMUtil.apiMode) {
			addTickerBar();
		}

		//2019. 03. 11 by hyh - 간편설정 버튼 추가 >>
		if(COMUtil._mainFrame.isSimpleSet) {
			addSimpleSet();
		}
		//2019. 03. 11 by hyh - 간편설정 버튼 추가 <<
		if(COMUtil._mainFrame.bUseOneqToolBar) {
			addOneqToolbar();
			setShowToolBarOneq(false);
		}

//		if (COMUtil.isAnalToolbar) {
//			addAnaltool();
//		}

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		if(COMUtil._mainFrame.isOneTouchSet)
			addOneTouch();
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end
	}
//	TickerPanel tickerPanel = null;
	public void addTickerBar() {
//		if (tickerPanel == null) {
//			RelativeLayout.LayoutParams params;
//			if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				params = new RelativeLayout.LayoutParams(frame.width(),
//						_PAD_TICKER_HEIGHT);
//				params.leftMargin = 0;
//				params.topMargin = frame.height() - _PAD_TICKER_HEIGHT;
//			} else {
//				params = new RelativeLayout.LayoutParams(frame.width(),
//						(int) COMUtil.getPixel(_PHONE_TICKER_HEIGHT));
//				params.leftMargin = 0;
//				params.topMargin = frame.height()
//						- (int) COMUtil.getPixel(_PHONE_TICKER_HEIGHT);
//			}
//			RelativeLayout tickerLayout = new RelativeLayout(this.context);
//			tickerLayout.setLayoutParams(params);
//			tickerPanel = new TickerPanel(this.context, tickerLayout);
//			tickerPanel.setLayoutParams(params);
//			tickerPanel.setBasicUI();
//			tickerLayout.addView(tickerPanel);
//			layout.addView(tickerLayout);
//		}
	}

	RelativeLayout analToolbarLayout = null;
	View analtoolmenuViewPhone=null, analtoolmenuViewPad, toolbarBack=null;
//    RelativeLayout analToolbarCloseLayout = null;
//    Button toolbarBackBtn; 

	public void addAnaltool() {
		int width = frame.width();
		int height = frame.height();

		analToolbarLayout = new RelativeLayout(this.context);
		analToolbarLayout.setTag("analToolbarLayout");
		int analHeight = height - _PHONE_TICKER_HEIGHT;

		// 2012. 8. 20 분석툴바 HONEYCOMB 레이아웃 적용 : T_tab23
		RelativeLayout.LayoutParams params;
		if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			params = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(59),
					(int) COMUtil.getPixel(analHeight));
		} else {
			params = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(m_nToolbarWidth),
					(int) COMUtil.getPixel(analHeight));
		}

		//2012. 8. 17 태블릿의  분석툴바 숨김 위치는 조금 드러나는게 아니라 완전히 숨겨진다 : T_tab22 
		if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			if (m_bIsShowToolBar) {
				params.leftMargin = width - (int) COMUtil.getPixel(58);
			} else {
				params.leftMargin = width - (int) COMUtil.getPixel(0);
			}

		} else {
			if (m_bIsShowToolBar) {
				params.leftMargin = width - (int) COMUtil.getPixel(m_nToolbarWidth);
			} else {
				params.leftMargin = width - (int) COMUtil.getPixel(0);
			}
		}

		params.topMargin = (int) COMUtil.getPixel(COMUtil.mainTopMargin);
		analToolbarLayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(this.context);

		//2012. 8. 17  분석툴바 태블릿일경우 layout로드 다르게 적용 : T_tab19
		int layoutResId;
		if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			layoutResId = context.getResources().getIdentifier(
					"analtoolmenupad", "layout", context.getPackageName());
		} else {
			layoutResId = context.getResources().getIdentifier("analtoolmenu",
					"layout", context.getPackageName());
		}

		analtoolmenuViewPhone = factory.inflate(layoutResId, null);

		int upScroll = context.getResources().getIdentifier("CommonUpScrollButton", "id", context.getPackageName());
		int downScroll = context.getResources().getIdentifier("CommonDownScrollButton", "id", context.getPackageName());

		mUpScrollButton = (FrameLayout) analtoolmenuViewPhone.findViewById(upScroll);
		mDownScrollButton = (FrameLayout) analtoolmenuViewPhone.findViewById(downScroll);

		int contentsViewScroll = context.getResources().getIdentifier("analtoolmenuScrollView", "id", context.getPackageName());
		analToolScrollView = (ScrollView) analtoolmenuViewPhone.findViewById(contentsViewScroll);

		analToolScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Message msg = new Message();
				mScrollButtonHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						COMUtil._chartMain.runOnUiThread(new Runnable() {
							public void run() {
								int relativeLayout1 = context.getResources().getIdentifier("relativeLayout1", "id", context.getPackageName());
								LinearLayout layout = (LinearLayout) analToolScrollView.findViewById(relativeLayout1);
								if(layout!=null)
								{
									//System.out.println("Y: "+analToolScrollView.getScrollY()+"height: "+analToolScrollView.getHeight()+"layoutheight:"+layout.getHeight());
									if (analToolScrollView.getScrollY() == 0) {
										mUpScrollButton.setVisibility(View.INVISIBLE);
										if(analToolScrollView.getScrollY()+ analToolScrollView.getHeight() < layout.getHeight())
											mDownScrollButton.setVisibility(View.VISIBLE);
										else
											mDownScrollButton.setVisibility(View.INVISIBLE);
									}
									else if (analToolScrollView.getScrollY()+ analToolScrollView.getHeight() < layout.getHeight()) {
										mUpScrollButton.setVisibility(View.VISIBLE);
										mDownScrollButton.setVisibility(View.VISIBLE);
									}
									else
									{
										mUpScrollButton.setVisibility(View.VISIBLE);
										mDownScrollButton.setVisibility(View.INVISIBLE);
									}
								}
							}
						});
					}
				}, 1000);
				return false;
			}
		});
		//postDelayed(mToggleButtonRunnable, 1000);
		//post(mToggleButtonRunnable);
		// 2013. 1. 14 툴바에 비교차트 버튼 표출 여부
		if (analtoolmenuViewPhone != null) {
			layoutResId = context.getResources().getIdentifier("button32",
					"id", context.getPackageName());
			Button btnCompare = (Button) analtoolmenuViewPhone
					.findViewById(layoutResId);
//				if (COMUtil.addCompareButton) {
//					btnCompare.setVisibility(View.VISIBLE);
//				} else {
//					btnCompare.setVisibility(View.GONE);
//				}
		}

		// 분석툴바 XML 메뉴 항목에 이벤트 연결.
		// 2012. 12. 13 비교차트 추가로 분석툴바 갯수 증가 : T65
//			int[] btnResId = new int[32];
//			String index = "";
		View view = null;
		m_arrButtons = new ArrayList<Button>();

		int relativeLayout1 = context.getResources().getIdentifier("relativeLayout1", "id", context.getPackageName());
		LinearLayout toolLayout = (LinearLayout) analToolScrollView.findViewById(relativeLayout1);

		for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
			view = toolLayout.findViewWithTag(""+COMUtil.arrToolbarIndex[i]);
			if(m_bMultiChart || ( !m_bMultiChart && COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_DIVIDE))
//					&& COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_DWMM
//					&& COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_SAVE && COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_LOAD) )	//2015. 2. 9 저장 불러오기 버튼 멀티차트 아닐땐 제외
			{
				//2019. 05. 28 by hyh - 시세알람차트 저장/불러오기/일주월분 도구모음 제거 >>
				if ((_chart != null && _chart._cvm != null) && _chart._cvm.bIsAlarmChart) {
					if (COMUtil.TOOLBAR_CONFIG_SAVE == COMUtil.arrToolbarIndex[i]
					|| COMUtil.TOOLBAR_CONFIG_LOAD == COMUtil.arrToolbarIndex[i]
					|| COMUtil.TOOLBAR_CONFIG_DWMM == COMUtil.arrToolbarIndex[i]) {
						view.setVisibility(View.GONE);
						continue;
					}
				}
				//2019. 05. 28 by hyh - 시세알람차트 저장/불러오기/일주월분 도구모음 제거 <<
				//2023.06.09 by SJW - 미사용 변수 주석 처리 >>
				//2019. 05. 30 by hyh - 매매연습차트 개발. 저장/불러오기/일주월분 도구모음 제거 >>
//				if (COMUtil._mainFrame.bIsTradeChart) {
//					if (COMUtil.TOOLBAR_CONFIG_SAVE == COMUtil.arrToolbarIndex[i]
//							|| COMUtil.TOOLBAR_CONFIG_LOAD == COMUtil.arrToolbarIndex[i]
//							|| COMUtil.TOOLBAR_CONFIG_DWMM == COMUtil.arrToolbarIndex[i]) {
//						view.setVisibility(View.GONE);
//						continue;
//					}
//				}
				//2019. 05. 30 by hyh - 매매연습차트 개발. 저장/불러오기/일주월분 도구모음 제거 <<
				//2023.06.09 by SJW - 미사용 변수 주석 처리 <<
				//2014.05.16 by LYH >> 선택한 툴 버튼만 추가.
				if(COMUtil.arrToolbarSelected[i].equals("1"))
				{
					view.setVisibility(View.VISIBLE);
				}
				else
				{
					view.setVisibility(View.GONE);
				}
				//2014.05.16 by LYH <<

				m_arrButtons.add((Button)view);
				if (view != null) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							setAnalTool(v);
						}
					});
				}
			}
		}
//			for (int i = toolLayout.getChildCount()-2; i >= 0; i=i-2) {
//				toolLayout.removeViewAt(i);
//			}
		toolLayout.removeAllViews();

//		Button btn_chartset = new Button(context);
//		params.leftMargin = 0;
//		params.topMargin = 0;
//		params.width = (int)COMUtil.getPixel(76);
//		params.height = (int)COMUtil.getPixel(60);
//		btn_chartset.setLayoutParams(params);
//		btn_chartset.setBackgroundResource(context.getResources().getIdentifier("c_option_35_change", "drawable", context.getPackageName()));
//		toolLayout.addView(btn_chartset);
//		btn_chartset.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				openIndicatorPopup();
//				addToolBarHanWha();
//			}
//		});

		for (int i = 0; i < m_arrButtons.size(); i++) {
			View button =  m_arrButtons.get(i);
			toolLayout.addView(button);

//			View lineView = new View(context);
//			lineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(1)));
//			lineView.setBackgroundColor(Color.TRANSPARENT);
//			lineView.setVisibility(button.getVisibility());
//
//			toolLayout.addView(lineView);
		}

//			for (int i = toolLayout.getChildCount()-1; i > COMUtil.arrToolbarIndex.length*2-2; i--) {
//				toolLayout.removeViewAt(i);
//			}
		//2013. 6. 10  (미래에셋-태블릿) 종합차트에서 분석툴바 최초 나와있는 속성일 때 버튼 터치  동작 설정 <<

		//2012. 7. 31  도구모음툴바를 Visibility 가 아닌 좌표로  컨트롤 하게되어서 주석처리 
//			analtoolmenuViewPhone.setVisibility(View.GONE);

		analToolbarLayout.addView(analtoolmenuViewPhone);

//			//2012. 8. 2 도구모음 여닫이 버튼 터치영역 넓게 잡아주는 영역 추가.  
//			analToolbarCloseLayout = new RelativeLayout(this.context);
//			analToolbarCloseLayout.setTag("analToolbarCloseLayout");
//			//여닫이 버튼보다 20픽셀 크게 
//			//2012. 8. 17  패드용 분석툴바의 여닫이버튼 여백공간레이아웃 : T_tab20
//			//2013. 6. 7 (미래에셋) 태블릿 차트화면중 분석툴바 표출 옵션일때 레이아웃 조절 >>
//			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//			{
//				//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
////				params =new RelativeLayout.LayoutParams(
////						(int)COMUtil.getPixel(38), (int)COMUtil.getPixel(60));
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(39), (int)COMUtil.getPixel(130));
//				//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
//				if(this.m_bIsShowToolBar)
//				{
//					params.leftMargin=width-(int)COMUtil.getPixel(88);
//				}
//				else
//				{
////					params.leftMargin=width-(int)COMUtil.getPixel(35);
//					params.leftMargin=width-(int)COMUtil.getPixel(33);
//				}
////				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65/2);
//				//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
//				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65);
//				//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
//			}
//			else
//			{
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(38), (int)COMUtil.getPixel(60));
//				params.leftMargin=width-(int)COMUtil.getPixel(35);
//				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65/2);
//			}
//			//2013. 6. 7 (미래에셋) 태블릿 차트화면중 분석툴바 표출 옵션일때 레이아웃 조절 <<
//			
//			analToolbarCloseLayout.setLayoutParams(params);
//			factory = LayoutInflater.from(this.context);
//			layoutResId = context.getResources().getIdentifier("analtoolmenuclose", "layout", context.getPackageName());
//			toolbarBack = factory.inflate(layoutResId, null);
//			analToolbarCloseLayout.setOnClickListener(new View.OnClickListener(){
//		    	public void onClick(View v) {
//		    		addToolBarPhone();
//		    		
//		    	}
//			});
//			analToolbarCloseLayout.setBackgroundColor(Color.TRANSPARENT);
//			analToolbarCloseLayout.addView(toolbarBack);
//			layout.addView(analToolbarCloseLayout);
//			
////	        params = (RelativeLayout.LayoutParams)toolbarBack.getLayoutParams();
////	        params.height = (int)COMUtil.getPixel(height);
//	        
//	        //2012. 7. 31 도구모음 여닫이버튼 크기 조절 
//			//2012. 8. 17  분석툴바 여닫이 버튼 태블릿용 적용 : T_tab21
//			//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
//			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//			{
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(18), (int)COMUtil.getPixel(130));
//				toolbarBackBtn = new Button(context);
//				toolbarBackBtn.setTag("imgButton");
//				//btnExtend.setBackgroundResource(R.drawable.view_full);
//				Drawable draw =  toolbarBackBtn.getBackground();
//				draw.setAlpha(0);
//				draw.invalidateSelf();
//				//2012. 7. 25 툴바 열고닫는 버튼의 크기 조절. 
////				params.leftMargin=width-(int)COMUtil.getPixel(40);
////				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(30);
//				
//				//2012. 8. 20 분석툴바 패드용 여닫이버튼 적용 : T_tab21
//				if(this.m_bIsShowToolBar)
//				{
//					params.leftMargin=width-(int)COMUtil.getPixel(70);
//				}
//				else
//				{
//					params.leftMargin=width-(int)COMUtil.getPixel(15);
//				}
//				
//				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65);
//				toolbarBackBtn.setLayoutParams(params);
//				//2012. 7. 25 툴바 열고닫는 버튼 배경그림 설정 
////				layoutResId = context.getResources().getIdentifier("toolbar_open_pad", "drawable", context.getPackageName());
//				//2013. 6. 17 분석툴바 여닫이버튼 태블릿 이미지 변경 >>
////				layoutResId = context.getResources().getIdentifier("toolbg_close", "drawable", context.getPackageName());
//				layoutResId = context.getResources().getIdentifier("toolbar_open_pad", "drawable", context.getPackageName());
//				//2013. 6. 17 분석툴바 여닫이버튼 태블릿 이미지 변경 <<
//			}
//			//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
//			else
//			{
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(18), (int)COMUtil.getPixel(60));
//				toolbarBackBtn = new Button(context);
//				toolbarBackBtn.setTag("imgButton");
//				//btnExtend.setBackgroundResource(R.drawable.view_full);
//				Drawable draw =  toolbarBackBtn.getBackground();
//				draw.setAlpha(0);
//				draw.invalidateSelf();
//				//2012. 7. 25 툴바 열고닫는 버튼의 크기 조절. 
////				params.leftMargin=width-(int)COMUtil.getPixel(40);
////				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(30);
//				
//				//2012. 8. 20  분석툴바 패드용 / 폰용 여닫이버튼  적용 : T_tab21 
//				if(this.m_bIsShowToolBar)
//				{
//					params.leftMargin=width-(int)COMUtil.getPixel(63);
//				}
//				else
//				{
//					params.leftMargin=width-(int)COMUtil.getPixel(15);
//				}
//				params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65/2);
//				toolbarBackBtn.setLayoutParams(params);
//				//2012. 7. 25 툴바 열고닫는 버튼 배경그림 설정 
//				layoutResId = context.getResources().getIdentifier("toolbg_close", "drawable", context.getPackageName());
//			}
//			
//			
//			toolbarBackBtn.setBackgroundResource(layoutResId);
//			toolbarBackBtn.setOnClickListener(new View.OnClickListener(){
//		    	public void onClick(View v) {
//		    		addToolBarPhone();
//		    		
//		    	}
//			});
//			layout.addView(toolbarBackBtn);
		layout.addView(analToolbarLayout);//분석툴바는 뒤에 추가한다.
	}

	//   }

	public void resetAnaltool()
	{
		if(analToolbarLayout != null)
		{
			int relativeLayout1 = context.getResources().getIdentifier("relativeLayout1", "id", context.getPackageName());
			LinearLayout toolLayout = (LinearLayout) analToolScrollView.findViewById(relativeLayout1);
			View view;
			m_arrButtons.removeAll(m_arrButtons);

			for (int i = 0; i < COMUtil.arrToolbarIndex.length; i++) {
				if(m_bMultiChart || ( !m_bMultiChart && COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_DIVIDE))
//						&& COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_DWMM
//						&& COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_SAVE && COMUtil.arrToolbarIndex[i] != COMUtil.TOOLBAR_CONFIG_LOAD) )	//2015. 2. 9 저장 불러오기 버튼 멀티차트 아닐땐 제외
				{
					view = toolLayout.findViewWithTag(""+COMUtil.arrToolbarIndex[i]);

					if (view != null) {
						m_arrButtons.add((Button) view);

						//2014.05.16 by LYH >> 선택한 툴 버튼만 추가.
						if (COMUtil.arrToolbarSelected[i].equals("1")) {
							view.setVisibility(View.VISIBLE);
						}
						else {
							view.setVisibility(View.GONE);
						}
						//2014.05.16 by LYH <<
					}
				}
			}

			toolLayout.removeAllViews();

//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//					(int) COMUtil.getPixel(76),
//					(int) COMUtil.getPixel(60));;
//			Button btn_chartset = new Button(context);
//			params.leftMargin = 0;
//			params.topMargin = 0;
//			btn_chartset.setLayoutParams(params);
//			btn_chartset.setBackgroundResource(context.getResources().getIdentifier("c_option_35_change", "drawable", context.getPackageName()));
//			toolLayout.addView(btn_chartset);
//			btn_chartset.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					openIndicatorPopup();
//				}
//			});

			for (int i = 0; i < m_arrButtons.size(); i++) {
				View button = m_arrButtons.get(i);
				toolLayout.addView(button);
//
//				View lineView = new View(context);
//				lineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) COMUtil.getPixel(1)));
//				lineView.setBackgroundColor(Color.TRANSPARENT);
//				lineView.setVisibility(button.getVisibility());
//
//				toolLayout.addView(lineView);
			}
		}
	}
	/** 패드에서 우측 분석툴 메뉴 보임 처리 **/
	public void showToolBar() {
		this.m_bIsShowToolBar = !this.m_bIsShowToolBar;
		if (this.m_bIsShowToolBar) {
			this.analtoolmenuViewPad.setVisibility(View.VISIBLE);
		} else {
			COMUtil.isContinueAnalDrawMode = false;// 연속 그리기 모드 해제.
			this.analtoolmenuViewPad.setVisibility(View.GONE);
			onClick(null); // 분석툴 버튼 선택 초기화.
			this.setToolbarState(9999);
		}

		this.resizeChart();
	}
//    private void addToolBarPhone() {
//    	this.m_bIsShowToolBar = !this.m_bIsShowToolBar;
//    	if(this.m_bIsShowToolBar) {
//    		
////    		toolbarBack.setClickable(true);
////			toolbarBack.setOnClickListener(new View.OnClickListener(){
////		    	public void onClick(View v) {
////		    		addToolBarPhone();
////		    		
////		    	}
////			});
//			RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//					(int)COMUtil.getPixel(18), (int)COMUtil.getPixel(256));
//			params.leftMargin=frame.width()-(int)COMUtil.getPixel(55);
//			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin)+(int)COMUtil.getPixel(90);
////			toolbarBack.setLayoutParams(params);
////			toolbarBack.setBackgroundColor(Color.YELLOW);
////			View view = (View)this.layout.findViewWithTag("analToolbarCloseLayout");
////			analToolbarCloseLayout.setBackgroundColor(Color.YELLOW);
//			analToolbarCloseLayout.setLayoutParams(params);
//    		analtoolmenuViewPhone.setVisibility(View.VISIBLE);
////    		toolbarBack.setVisibility(View.INVISIBLE);
//    	} else {
//    		toolbarBack.setVisibility(View.VISIBLE);
//    		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//					(int)COMUtil.getPixel(18), (int)COMUtil.getPixel(256));
//			params.leftMargin=frame.width()-(int)COMUtil.getPixel(18);
//			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin)+(int)COMUtil.getPixel(90);
////			toolbarBack.setLayoutParams(params);
////			View view = (View)this.layout.findViewWithTag("analToolbarCloseLayout");
//			analToolbarCloseLayout.setLayoutParams(params);
//    		analtoolmenuViewPhone.setVisibility(View.GONE);
//    	}
//    	this.resizeBaseChart(chart, 1, 0, 0);
//    }

	//    //2012. 7. 25  apiMode 에 따라 다르게 처리  및 open/close 버튼 설정 
//    private void addToolBarPhone() {
//    	if(m_bIsAnalToolBar)
//    	{
//    		
//    		if(analtoolmenuViewPhone == null)
//    		{
//    			LayoutInflater factory = LayoutInflater.from(context);
////    			int layoutResId = context.getResources().getIdentifier("analtoolmenu", "layout", context.getPackageName());
//    			int layoutResId;
//    			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//    			{
//    				layoutResId = context.getResources().getIdentifier("analtoolmenupad", "layout", context.getPackageName());
//    			}
//    			else
//    			{
//    				layoutResId = context.getResources().getIdentifier("analtoolmenu", "layout", context.getPackageName());
//    			}
//    			analtoolmenuViewPhone = factory.inflate(layoutResId, null);
//    			
//    			//분석툴바 XML 메뉴 항목에 이벤트 연결.
//    			int[] btnResId = new int[31];
//    			String index="";
//    			View view = null;
//    			int nBtnLen = btnResId.length;
//    			for(int i=0; i<nBtnLen; i++) {
//    				if((i+1)<10) index = "0"+(i+1);
//    				else index = ""+(i+1);
//    				btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
//    				view = analtoolmenuViewPhone.findViewById(btnResId[i]);
//    				if( !COMUtil.bIsMulti && view !=null && Integer.parseInt((String)view.getTag()) == COMUtil.TOOLBAR_CONFIG_DWMM)
//    				{
//    					if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//    						int resid = context.getResources().getIdentifier("line12", "id", context.getPackageName());
//    						View lineView = analtoolmenuViewPhone.findViewById(resid);
//    						lineView.setVisibility(View.GONE);
//    					}
//    					view.setVisibility(View.GONE);
//    				}
//    				else
//    				{
//    					if (view != null)
//    					{
//    						view.setOnClickListener(new Button.OnClickListener() {
//    				        	public void onClick(View v) {
//    				        		setAnalTool(v);
//    					        }
//    				        });
//    					}
//    				}
//    			}
//    			
//    			layout.removeView(analToolbarLayout);
//    			analToolbarLayout = null;
//    			
//    	    	int width = frame.width();
//    	    	int height = frame.height();
//    			
//    			analToolbarLayout = new RelativeLayout(this.context);
//    	    	analToolbarLayout.setTag("analToolbarLayout");	    		
//    	    	int analHeight = height-_PHONE_TICKER_HEIGHT;
//    	    	
//    	    	//2012. 8. 20 분석툴바 HONEYCOMB 레이아웃 적용 : T_tab23
//    	    	RelativeLayout.LayoutParams params;
//    	    	if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//    			{
//    	    		params =new RelativeLayout.LayoutParams(
//    						(int)COMUtil.getPixel(59), (int)COMUtil.getPixel(analHeight));
//    			}
//    	    	else
//    	    	{
//    	    		params =new RelativeLayout.LayoutParams(
//    						(int)COMUtil.getPixel(55), (int)COMUtil.getPixel(analHeight));
//    	    	}
//
//    			//2012. 8. 17 태블릿의  분석툴바 숨김 위치는 조금 드러나는게 아니라 완전히 숨겨진다 : T_tab22 
//    			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//    			{
//    				if(m_bIsShowToolBar)
//    				{
//    					params.leftMargin=width-(int)COMUtil.getPixel(58);
//    				}
//    				else
//    				{
//    					params.leftMargin=width-(int)COMUtil.getPixel(0);
//    				}
//    				
//    			}
//    			else
//    			{
//    				if(m_bIsShowToolBar)
//    				{
//    					params.leftMargin=width-(int)COMUtil.getPixel(53);
//    				}
//    				else
//    				{
//    					params.leftMargin=width-(int)COMUtil.getPixel(2);
//    				}
//    				
//    			}
//    			
//    			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin);
//    			analToolbarLayout.setLayoutParams(params);
//    			
//    			analToolbarLayout.addView(analtoolmenuViewPhone);
//    			
//    			layout.addView(analToolbarLayout);
//    		}
//    		
//    		this.m_bIsShowToolBar = !this.m_bIsShowToolBar;
////    		if(COMUtil.apiMode)
////    		{
//    		//2012. 7. 31 툴바 보이고 사라지는 형태  visible 이 아닌 좌표로 
//    		int height = frame.height();
//			int analHeight = height-_PHONE_TICKER_HEIGHT;
//			int width = frame.width();
//			//2012. 8. 20  테블릿에서의 분석툴바 열렸을의 x 좌표 지정 :  T_tab22
//			RelativeLayout.LayoutParams params;
//			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//    		{
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(59), (int)COMUtil.getPixel(analHeight));
//    		}
//			else
//			{
//				params =new RelativeLayout.LayoutParams(
//						(int)COMUtil.getPixel(55), (int)COMUtil.getPixel(analHeight));
//			}
//    			if(this.m_bIsShowToolBar) {
//    				//2012. 7. 31 툴바 보이고 사라지는 형태  visible 이 아닌 좌표로 
////            		analtoolmenuViewPhone.setVisibility(View.VISIBLE);
//    				
//    				//2012. 8. 20  테블릿에서의 분석툴바 열렸을의 x 좌표 지정 :  T_tab22
//    				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//            		{
//    					
//            			params.leftMargin=width-(int)COMUtil.getPixel(58);
//            		}
//            		else
//            		{
//            			params.leftMargin=width-(int)COMUtil.getPixel(53);
//            		}
//        			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin);
//        			analToolbarLayout.setLayoutParams(params);
//            		
////        			analToolbarCloseLayout.setVisibility(View.GONE);
//            		//2012. 7. 25  툴바 열고 닫는 버튼의 배경그림 토글 
////            		int layoutResId = context.getResources().getIdentifier("toolbg_close", "drawable", context.getPackageName());
////            		toolbarBackBtn.setBackgroundResource(layoutResId);
//        			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
//        			//2012. 7. 25 툴바 열기/닫기 버튼  높이 변하지 않게 설정, 분석툴바에 화살표 x 축 정확히 위치하도록 설정. 아이폰도 크기변경은 없어서 아래 height 관련 주석처리함 
//        			//2012. 8. 3 여닫이버튼이 분석툴바에 약간 들어간 모양으로 하기 위해 
//        			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//        			{
//        				param.leftMargin = frame.width()-(int)COMUtil.getPixel(70);
//        			}
//        			else
//        			{
//        				param.leftMargin = frame.width()-(int)COMUtil.getPixel(65);
//        			}
//        	        
////        	        param.height = (int)COMUtil.getPixel(frame.height());
//        	        
//        	        //2012. 8. 2 여닫이버튼 왼쪽 터치영역확장 레이아웃의 위치 조절 
//        	        RelativeLayout.LayoutParams area_param = (RelativeLayout.LayoutParams)analToolbarCloseLayout.getLayoutParams();
//        	        area_param.leftMargin = frame.width()-(int)COMUtil.getPixel(88);
//        	        //2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
////        	        area_param.topMargin = (int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65/2);
//        	        area_param.topMargin = (int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65);
//        	        //2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
//            	} else {
//            		
////            		analToolbarCloseLayout.setVisibility(View.VISIBLE);
//            		//2012. 7. 31 툴바 보이고 사라지는 형태  visible 이 아닌 좌표로 
////            		analtoolmenuViewPhone.setVisibility(View.GONE);
//            		
//            		//2012. 8. 17 태블릿의  분석툴바 숨김 위치는 조금 드러나는게 아니라 완전히 숨겨진다 : T_tab21
//            		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//            		{
//            			params.leftMargin=width-(int)COMUtil.getPixel(0);
//            		}
//            		else
//            		{
//            			params.leftMargin=width-(int)COMUtil.getPixel(2);
//            		}
//            		
//        			params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin);
//        			analToolbarLayout.setLayoutParams(params);
//            		
//            		//2012. 7. 25  툴바 열고 닫는 버튼의 배경그림 토글 
//            		//2012. 8. 20 툴바 여닫이버튼의 그림은 스왑되지 않아서 주석처리 : T_tab24
////            		int layoutResId = context.getResources().getIdentifier("toolbg_close", "drawable", context.getPackageName());
////            		toolbarBackBtn.setBackgroundResource(layoutResId);
//            		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
//            		//2012. 7. 25 툴바 열기/닫기 버튼  높이 변하지 않게 설정 , 윗쪽에서 x 축 위치 빼주는 값을 20으로 줘서 여기서도 맞춰줌 . 아이폰도 크기변경은 없어서 주석처리함 
//            		//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
//            		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//        			{
//        				param.leftMargin = frame.width()-(int)COMUtil.getPixel(15);
//        				
//        			}
//        			else
//        			{
//        				param.leftMargin = frame.width()-(int)COMUtil.getPixel(15);
//        			}
//            		//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
////        	        param.height = (int)COMUtil.getPixel(frame.height());
//        	        
//        	        //2012. 8. 2 여닫이버튼 왼쪽 터치영역확장 레이아웃의 위치 조절 
//        	        RelativeLayout.LayoutParams area_param = (RelativeLayout.LayoutParams)analToolbarCloseLayout.getLayoutParams();
//        	        area_param.leftMargin = frame.width()-(int)COMUtil.getPixel(35);
//        	      	//2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 >>
////        	        area_param.topMargin = (int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65/2);
//        	        area_param.topMargin = (int)COMUtil.getPixel(COMUtil.mainTopMargin) + height/2-(int)COMUtil.getPixel(65);
//        	        //2013. 6. 12 (미래에셋) 태블릿 분석툴바 여닫이버튼 레이아웃 조절 <<
//        	        
////        	        param = (RelativeLayout.LayoutParams)toolbarBack.getLayoutParams();
////        	        param.height = (int)COMUtil.getPixel(frame.height());
//        	        
//        	        onClick(null); //분석툴 버튼 선택 초기화.
//        	        this.setToolbarState(9999);
//            	}
////    		}
////			else
////			{
////				if(this.m_bIsShowToolBar) {
////            		analtoolmenuViewPhone.setVisibility(View.VISIBLE);
////        			analToolbarCloseLayout.setVisibility(View.GONE);
////        			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
////        	        param.leftMargin = frame.width()-(int)COMUtil.getPixel(80);
////        	        param.height = (int)COMUtil.getPixel(frame.height());
////            	} else {
////            		
////            		analToolbarCloseLayout.setVisibility(View.VISIBLE);
////            		analtoolmenuViewPhone.setVisibility(View.GONE);
////            		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
////        	        param.leftMargin = frame.width()-(int)COMUtil.getPixel(40);
////        	        param.height = (int)COMUtil.getPixel(frame.height());
////        	        
////        	        param = (RelativeLayout.LayoutParams)toolbarBack.getLayoutParams();
////        	        param.height = (int)COMUtil.getPixel(frame.height());
////        	        
////        	        COMUtil.onClick(null); //분석툴 버튼 선택 초기화.
////        	        this.setToolbarState(9999);
////            	}
////			}
//    	}
//    	isChangeBlock = true;
//    	this.resizeChart();
//    	isChangeBlock =	false;
//    }
	public void addToolBarHanWha() {
		if (m_bIsAnalToolBar) {
			if (analToolbarLayout == null) {
				addAnaltool();
			}
			this.m_bIsShowToolBar = !this.m_bIsShowToolBar;
			// if(COMUtil.apiMode)
			// {
			// 2012. 7. 31 툴바 보이고 사라지는 형태 visible 이 아닌 좌표로
			int height = frame.height();
			int analHeight = height - _PHONE_TICKER_HEIGHT;
			int width = frame.width();
			// 2012. 8. 20 테블릿에서의 분석툴바 열렸을의 x 좌표 지정 : T_tab22
			RelativeLayout.LayoutParams params;
			if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
				params = new RelativeLayout.LayoutParams(
						(int) COMUtil.getPixel(59),
						(int) COMUtil.getPixel(analHeight));
			} else {
				params = new RelativeLayout.LayoutParams(
						(int) COMUtil.getPixel(m_nToolbarWidth),
						(int) COMUtil.getPixel(analHeight));
			}
			if (this.m_bIsShowToolBar) {
				// 2012. 7. 31 툴바 보이고 사라지는 형태 visible 이 아닌 좌표로
				// analtoolmenuViewPhone.setVisibility(View.VISIBLE);

				// 2012. 8. 20 테블릿에서의 분석툴바 열렸을의 x 좌표 지정 : T_tab22
				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {

					params.leftMargin = width - (int) COMUtil.getPixel(58);
				} else {
					params.leftMargin = width - (int) COMUtil.getPixel(m_nToolbarWidth);
				}
				params.topMargin = (int) COMUtil
						.getPixel(COMUtil.mainTopMargin);
				analToolbarLayout.setLayoutParams(params);

				// analToolbarCloseLayout.setVisibility(View.GONE);
				// 2012. 7. 25 툴바 열고 닫는 버튼의 배경그림 토글
				// int layoutResId =
				// context.getResources().getIdentifier("toolbg_close",
				// "drawable", context.getPackageName());
				// toolbarBackBtn.setBackgroundResource(layoutResId);
				// RelativeLayout.LayoutParams param =
				// (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
				// //2012. 7. 25 툴바 열기/닫기 버튼 높이 변하지 않게 설정, 분석툴바에 화살표 x 축 정확히
				// 위치하도록 설정. 아이폰도 크기변경은 없어서 아래 height 관련 주석처리함
				// //2012. 8. 3 여닫이버튼이 분석툴바에 약간 들어간 모양으로 하기 위해
				// if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				// {
				// param.leftMargin = frame.width()-(int)COMUtil.getPixel(70);
				// }
				// else
				// {
				// param.leftMargin = frame.width()-(int)COMUtil.getPixel(63);
				// }

				// param.height = (int)COMUtil.getPixel(frame.height());

				// 2012. 8. 2 여닫이버튼 왼쪽 터치영역확장 레이아웃의 위치 조절
				// RelativeLayout.LayoutParams area_param =
				// (RelativeLayout.LayoutParams)analToolbarCloseLayout.getLayoutParams();
				// area_param.leftMargin =
				// frame.width()-(int)COMUtil.getPixel(88);
				// area_param.topMargin =
				// (int)COMUtil.getPixel(COMUtil.mainTopMargin) +
				// height/2-(int)COMUtil.getPixel(65/2);
			} else {

				// analToolbarCloseLayout.setVisibility(View.VISIBLE);
				// 2012. 7. 31 툴바 보이고 사라지는 형태 visible 이 아닌 좌표로
				// analtoolmenuViewPhone.setVisibility(View.GONE);

				// 2012. 8. 17 태블릿의 분석툴바 숨김 위치는 조금 드러나는게 아니라 완전히 숨겨진다 : T_tab21
				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
					params.leftMargin = width - (int) COMUtil.getPixel(0);
				} else {
					params.leftMargin = width - (int) COMUtil.getPixel(0);
				}

				params.topMargin = (int) COMUtil
						.getPixel(COMUtil.mainTopMargin);
				analToolbarLayout.setLayoutParams(params);

				// 2012. 7. 25 툴바 열고 닫는 버튼의 배경그림 토글
				// 2012. 8. 20 툴바 여닫이버튼의 그림은 스왑되지 않아서 주석처리 : T_tab24
				// int layoutResId =
				// context.getResources().getIdentifier("toolbg_close",
				// "drawable", context.getPackageName());
				// toolbarBackBtn.setBackgroundResource(layoutResId);
				// RelativeLayout.LayoutParams param =
				// (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
				// //2012. 7. 25 툴바 열기/닫기 버튼 높이 변하지 않게 설정 , 윗쪽에서 x 축 위치 빼주는 값을
				// 20으로 줘서 여기서도 맞춰줌 . 아이폰도 크기변경은 없어서 주석처리함
				// param.leftMargin = frame.width()-(int)COMUtil.getPixel(15);
				// param.height = (int)COMUtil.getPixel(frame.height());

				// 2012. 8. 2 여닫이버튼 왼쪽 터치영역확장 레이아웃의 위치 조절
				// RelativeLayout.LayoutParams area_param =
				// (RelativeLayout.LayoutParams)analToolbarCloseLayout.getLayoutParams();
				// area_param.leftMargin =
				// frame.width()-(int)COMUtil.getPixel(35);
				// area_param.topMargin =
				// (int)COMUtil.getPixel(COMUtil.mainTopMargin) +
				// height/2-(int)COMUtil.getPixel(65/2);

				onClick(null); // 분석툴 버튼 선택 초기화.
				this.setToolbarState(9999);

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("toolbarState", "0");
				if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SET_TOOLBAR_CLOSE, dic);
			}

		}
		isChangeBlock = true;
		isChangeBlock = false;
	}
	private boolean isChangeBlock = false;

	public void inputPanel_destroy(boolean isSign) {
//        inputPanel.print_destroy(isSign);
	}
	public void destroy() {
//		this.layout.removeView(inputPanel);
//		COMUtil.unbindDrawables(inputPanel);
		//2014.06.12 by LYH >> 멀티 차트 메모리 해제.
//    	if(_chart != null)
//    		_chart.destroy();
		for(int i=0; i<chartList.size(); i++)
		{
			BaseChart pChart = chartList.get(i);
			pChart.destroy();
		}
		//2014.06.12 by LYH << 멀티 차트 메모리 해제.
		if(compareChart != null)
			compareChart.destroy();
//		COMUtil._mainFrame.closeApiPopup(); //2016.11.10 by pjm << 가로모드 팝업창 on상태에서 세로모드로 전환시 팝업창 안닫히는 부분 조치
	}

	public void setPacketData(byte[] data) {
		if(data==null || data.length<20){
			COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
			COMUtil.isProcessData=false;
			COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
			COMUtil.nkey="";
			return;
		}
		if(m_nRotateIndex>=0) {
			BaseChart pChart = chartList.get(m_nRotateIndex);
			//chart = pChart;
			_chart = pChart;
			for(int i=0; i<chartList.size(); i++)
			{
				pChart = chartList.get(i);
				pChart.setSelected(m_nTotNum != 1 && i == m_nRotateIndex);
			}
			if(!baseChart._cvm.bIsLineChart)
				COMUtil._neoChart = _chart;
		}

		BaseChart baseChart;
		if(data==null || data.length<20) {
			COMUtil.symbol = COMUtil.preSymbol;
//			if(this.inputPanel!=null) inputPanel.setCodeName(COMUtil.codeName);
		} else {
			String sendTrType = COMUtil.getSendTrType();
			if(m_nQueryChartIndex>0) {
				for(int i=m_nQueryChartIndex; i<chartList.size(); i++) {
					baseChart = chartList.get(i);
					baseChart.initDataInfo(sendTrType);
				}
			} else {
				if(m_bCompareChart) {
					compareChart.initDataInfo(sendTrType);
				} else {
					_chart.initDataInfo(sendTrType);
					if(m_bMultiData)
					{
						if(nMarketType != 0) //주식멀티차트(일/주/월/10분으로 초기에 띄우기)
						{
							for(int i = m_nQueryChartIndex; i<chartList.size(); i++)
							{
								baseChart = chartList.get(i);
								baseChart.initDataInfo(sendTrType);
							}
						}
					}
				}
			}
		}

		if(m_nQueryChartIndex>0) {
			for(int i=m_nQueryChartIndex; i<chartList.size(); i++) {
				baseChart = chartList.get(i);
				baseChart.setData(data);
				//2011.08.05 by LYH >> 멀티 차트 띄우기  <<
				//baseChart.onResume();
			}
			m_nQueryChartIndex=0;
		} else {
			if(m_bCompareChart) {
				compareChart.setData(data);
			} else {
				_chart.setData(data);
				if(m_bMultiData)
				{
					if(nMarketType == 0) //주식멀티차트(일/주/월/10분으로 초기에 띄우기)
					{
						m_bDWMMClick = true;
						m_bConnectSocket = true;
					}
					else
					{
						for(int i = m_nQueryChartIndex; i<chartList.size(); i++)
						{
							baseChart = chartList.get(i);
							baseChart.setData(data);
						}
					}
					m_bMultiData = false;
				}
				//2011.08.05 by LYH >> 멀티 차트 띄우기  <<
				//chart.onResume();
			}
		}

		//2011.08.05 by LYH >> 멀티 차트 띄우기  (막음)
//        for(int i=0; i<chartList.size(); i++) {
////        	RelativeLayout ll = (RelativeLayout)chartList.get(i);
////        	BaseChart bChart = (BaseChart)ll.findViewWithTag("baseChart");
//        	BaseChart bChart = (BaseChart)chartList.get(i);
//        	bChart.onResume();
//        }
////		this.requestSignalTR();

//	    //분할차트 로드시 함수 추가. 2011.09.06 by lyk
//	    boolean hasMarketType = _chart.hasMarketType();
//	    if (hasMarketType) {
//	        isSendMarket = true;
//	    } else {
//	        isSendMarket = false;
//	    }
//	    
//		//2011.09.15 by lyk 
//		if(divideStorageType==true && !isSendMarket) {
//			
//			sendRotateTR_storage();
//            
//		} else {
//			//2011.08.05 by LYH <<
//			//분할차트관련 추가.
//		    if(!bSendCompareData && m_nTotNum>1 && m_nQueryChartIndex <=0 && !COMUtil.getSendTrType().equals("requestAddData")
//		    		&& ((m_bSyncJongMok || m_bSyncJugi || m_bConnectSocket) && !isSendMarket && !m_bSyncIndicator) && !isRotation)
//		    	
////		    	System.out.println("base11:"+"isSendMarket:"+isSendMarket+" syncIndicator:"+m_bSyncIndicator);
//		        sendRotateTR();
//		}
//	    
//	    sendTrCodes();
	}

	public void setData_Gijun(Hashtable<String, String> datas) {
//    	for(int i=0; i<chartList.size(); i++)
//        {
//    		BaseChart pChart = (BaseChart)chartList.get(i);
//    		pChart.setData_Gijun(datas);
//        }
		this.gijun_datas = datas;
//    	if(_chart!=null)
//    		_chart.setData_Gijun(datas);
	}

	public void setPacketData_data(byte[] data, String[] strDates, double[] strOpens, double[] strHighs, double[] strLows, double[] strCloses, double[] strVolumes, double[] strValues, double[] strRights, double[] strRightRates, String strCandleType) {
		//2013.10.04 by LYH >> 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음 <<
		if(_chart != null && _chart._cvm.bInvestorChart)
			COMUtil.apCode = COMUtil.TR_CHART_INVESTOR;
		m_bGetData = true;
		
		m_bRotateEnd = false;

		if(data==null || data.length<20){
			COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
			COMUtil.isProcessData=false;
			COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
			COMUtil.nkey="";
			return;
		}
		if(m_nRotateIndex>=0) {
			BaseChart pChart = chartList.get(m_nRotateIndex);
			//chart = pChart;
			_chart = pChart;
//			for(int i=0; i<chartList.size(); i++)
//			{
//				pChart = chartList.get(i);
//				if(m_nTotNum != 1 && i == m_nRotateIndex)
//				{
//					pChart.setSelected(true);
//				}
//				else {
//					if(pChart.isSelected()) {
//						pChart.setSelected(false);
//						pChart.repaintAll();
//					}
//				}
//			}
			COMUtil._neoChart = _chart;

		}

		BaseChart baseChart;
		if(data==null || data.length<20) {
			COMUtil.symbol = COMUtil.preSymbol;
//			if(this.inputPanel!=null) inputPanel.setCodeName(COMUtil.codeName);
		} else {
			String sendTrType = COMUtil.getSendTrType();
			if(m_nQueryChartIndex>0) {
				for(int i=m_nQueryChartIndex; i<chartList.size(); i++) {
					baseChart = chartList.get(i);
					baseChart.initDataInfo(sendTrType);
				}
			} else {
				if(m_bCompareChart) {
					compareChart.initDataInfo(sendTrType);
				} else {
					_chart.initDataInfo(sendTrType);
					if(m_bMultiData)
					{
						if(nMarketType != 0) //주식멀티차트(일/주/월/10분으로 초기에 띄우기)
						{
							for(int i = m_nQueryChartIndex; i<chartList.size(); i++)
							{
								baseChart = chartList.get(i);
								baseChart.initDataInfo(sendTrType);
							}
						}
					}
				}
			}
		}

		if(m_nQueryChartIndex>0) {
			for(int i=m_nQueryChartIndex; i<chartList.size(); i++) {
				baseChart = chartList.get(i);
				baseChart.setData_header(data, strCandleType);
				baseChart.setData_data(strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates);
				//2011.08.05 by LYH >> 멀티 차트 띄우기  <<
				//baseChart.onResume();
			}
			m_nQueryChartIndex=0;
		} else {
			if(m_bCompareChart) {
				compareChart.setData_header(data, strCandleType);
				compareChart.setData_data(strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates);
			} else {
//				System.out.println("DEBUG_gijun_datas"+gijun_datas);
				_chart.setData_Gijun(gijun_datas);
				_chart.setData_header(data, strCandleType);
				_chart.setData_data(strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates);

				//2019. 07. 02 by hyh - 화면 내렸다가 올릴 때 분할설정 저장되도록 처리 >>
				if (m_nRotateIndex < 0 && COMUtil._mainFrame.strFileName != null) {
					COMUtil.saveLastState(COMUtil._mainFrame.strFileName);
				}
				//2019. 07. 02 by hyh - 화면 내렸다가 올릴 때 분할설정 저장되도록 처리 <<

				if(m_bMultiData)
				{
					if(nMarketType == 0) //주식멀티차트(일/주/월/10분으로 초기에 띄우기)
					{
						m_nSelIndex = 0;
						m_bDWMMClick = true;
						m_bConnectSocket = true;
					}
					else
					{
						for(int i = m_nQueryChartIndex; i<chartList.size(); i++)
						{
							baseChart = chartList.get(i);
							baseChart.setData_header(data, strCandleType);
							baseChart.setData_data(strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates);
						}
					}
					m_bMultiData = false;
				}
				//2011.08.05 by LYH >> 멀티 차트 띄우기  <<
				//chart.onResume();
			}
		}

		//2011.08.05 by LYH >> 멀티 차트 띄우기  (막음)
//        for(int i=0; i<chartList.size(); i++) {
////        	RelativeLayout ll = (RelativeLayout)chartList.get(i);
////        	BaseChart bChart = (BaseChart)ll.findViewWithTag("baseChart");
//        	BaseChart bChart = (BaseChart)chartList.get(i);
//        	bChart.onResume();
//        }
////		this.requestSignalTR();
		//2011.09.15 by lyk 
//		if(divideStorageType==true) {
////			mHandler.post(new Runnable() {
////				public void run() {
////					sendRotateTR_storage();
////			    }
////			});   
//			
//			sendRotateTR_storage();
//            
//		} else {
//			//2011.08.05 by LYH <<
//			//분할차트관련 추가.
//		    if(!bSendCompareData && m_nTotNum>1 && m_nQueryChartIndex <=0 && !COMUtil.getSendTrType().equals("requestAddData") && (m_bSyncJongMok || m_bSyncJugi || m_bConnectSocket))
//		        sendRotateTR();
//		}
		//분할차트 로드시 함수 추가. 2011.09.06 by lyk
		boolean hasMarketType = false;
		if(_chart!=null)
			hasMarketType = _chart.hasMarketType();
		isSendMarket = hasMarketType;

		if(m_bRotateEnd == true)
			return;
		
		//2011.09.15 by lyk 
		if(divideStorageType==true && !isSendMarket) {

			sendRotateTR_storage();
			//2015. 2. 24 차트 상하한가 표시>>
			if(m_nRotateIndex<0)
			{
				requestUpperLowerLimitData(true);
			}
			//2015. 2. 24 차트 상하한가 표시<<

		} else {
			//2011.08.05 by LYH <<
			//분할차트관련 추가.
			if(!bSendCompareData && m_nTotNum>1 && m_nQueryChartIndex <=0 && !COMUtil.getSendTrType().equals("requestAddData")
					&& ((m_bSyncJongMok || m_bSyncJugi || m_bConnectSocket) && !isSendMarket && !m_bSyncIndicator) && !isRotation)
			{
//		    	System.out.println("base11:"+"isSendMarket:"+isSendMarket+" syncIndicator:"+m_bSyncIndicator);
				sendRotateTR();
				//2015. 2. 24 차트 상하한가 표시>>
				if(m_nRotateIndex<0)
				{
					requestUpperLowerLimitData(true);
				}
				//2015. 2. 24 차트 상하한가 표시<<
			}
			else
			{
				//2015. 2. 24 차트 상하한가 표시>>
				if(m_nRotateIndex<0)
				{
					requestUpperLowerLimitData(false);
				}
				//2015. 2. 24 차트 상하한가 표시<<
			}
		}

		sendTrCodes();
	}
	public void selectChart(NeoChart2 pChart) {
		if(pChart != _chart && baseChart!=null && !baseChart._cvm.bIsLineChart) {
			if(pChart.getClass().equals(BaseChart.class)) {
				_chart = (BaseChart)pChart;
				COMUtil._neoChart = _chart;
			} else if(pChart.getClass().equals(BaseChart_Multi.class)) {
				compareChart = (BaseChart_Multi)pChart;
				COMUtil._neoChart = compareChart;
//        		_chart = chart_multi;
			}


			for(int i=0; i<chartList.size(); i++) {
				NeoChart2 pChart2 = chartList.get(i);
				if(m_nTotNum != 1 && pChart2.equals(_chart)) {
//                  m_nRotateIndex = i;	//2015. 2. 24 차트 상하한가 표시
					m_nSelIndex = i;
					pChart2.setSelected(true);
				} else {
					pChart2.setSelected(false);
				}
				pChart2.repaintAll();
			}
		} else {
			if(isMultiChart()) {
				NeoChart2 tmpChart = chartList.get(0);
				tmpChart.setSelected(false);
				tmpChart.repaintAll();
				pChart.setSelected(true);
				pChart.repaintAll();

				_chart = (BaseChart)pChart;
				COMUtil._neoChart = _chart;
			}
		}
	}
	/* API 호출 함수 : 실시간 데이터 처리 */
	public void setRealData(byte[] data) {
		if(chartList != null)
		{
			for(int i=0; i<chartList.size(); i++)
			{
				BaseChart pBaseChart = chartList.get(i);
				//pBaseChart.setRealData(data);
				pBaseChart.setRealData(data, m_bMultiChart);
			}
		}
		if(compareChart != null)
			compareChart.setRealData(data, m_bMultiChart);
		//_chart.setRealData(data);
	}
	public void setCode(String code) {
		//tr을 보내야한다.
//		if(inputPanel != null) inputPanel.setCode(code);
	}
	public void setCodeName(String name) {
//		if(inputPanel != null) inputPanel.setCodeName(name);
	}
	public void setPeriodName(String name) {
//		if(inputPanel != null) inputPanel.setPeriodName(name);
	}
	public void setCountText(String name) {
//		if(inputPanel != null) inputPanel.setCountText();
	}

	//차트관련 이벤트
	public void addGraph(ChartEvent e){
//        setGraphListState(e.getPrameterString(),true);
	}
	public void removeGraph(ChartEvent e){
		_chart.setTitleBounds();
//        setGraphListState(e.getPrameterString(),false);
	}
	public void initChart(String type){
		//2023.06.09 by SJW - 미사용 변수 주석 처리 >>
		//2019. 05. 30 by hyh - 매매연습차트 개발. 지표만 초기화 되도록 처리 >>
//		if(COMUtil._mainFrame.bIsTradeChart) {
//			if (type.equals("pressInitBtn")) {
//				type = "pressIndicatorInitBtn";
//			}
//		}
		//2019. 05. 30 by hyh - 매매연습차트 개발. 지표만 초기화 되도록 처리 <<
		//2023.06.09 by SJW - 미사용 변수 주석 처리 <<
		if(type.equals("pressInitBtn")) {
			int tag = 11;
			if (nDivideType != tag) {
				nDivideType = tag;
				COMUtil.divideType = nDivideType;
				if (tag == 0 || tag == 11) {
//					if (inputPanel != null)
//						this.inputPanel.showSyncButton(false);
				} else {
//					if (inputPanel != null)
//						this.inputPanel.showSyncButton(true);
				}

				if (tag > 10) {
					tag = tag / 10 - 1;
				}
				setDivisionChart(tag + 1);
				m_nRotateIndex = -1;

				this.sendTR(String.valueOf(COMUtil._TAG_RESET_MULTICODES));
			}
			// 주기 / 동기화 초기화
			this.m_bSyncJongMok = false;
			COMUtil.isSyncJongmok = m_bSyncJongMok;
			this.m_bSyncJugi = false;
			COMUtil.isSyncJugi = m_bSyncJugi;

			// 화면 색상 관련 초기화.

			//2015. 3. 4 차트 테마 메인따라가기 추가>>
			setSkinType(COMUtil.SKIN_WHITE);
//			skin type 초기화 (auto theme)
//			COMUtil.bIsAutoTheme = true;
			COMUtil.bIsAutoTheme = false;
			if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
				this.setSkinType(COMUtil.SKIN_BLACK);
			} else {
				this.setSkinType(COMUtil.SKIN_WHITE);
			}
			//2015. 3. 4 차트 테마 메인따라가기 추가<<

			// 편의설정 관련 추가
			/** 여기다 추가하세요 */
			COMUtil.resetBunTicPeriodList(false);
			COMUtil.resetConvenientList();


			//2015. 3. 18 보이는 개수 및 조회 갯수 설정>>
			//조회 갯수 초기화
//			setRequestDataLength("200");
//			Hashtable<String, Object> dic = new Hashtable<String, Object>();
//			dic.put("RequestDataLength", "200");
//			if(_chart.userProtocol!=null) _chart.userProtocol.requestInfo(COMUtil._TAG_CHANGE_REQUESTDATALENGTH, dic);
			//2015. 3. 18 보이는 개수 및 조회 갯수 설정<<

			//2013. 9. 3 도구설정 초기화 >>
			for(int i = 0; i < COMUtil.arrToolbarIndex.length; i++)
			{
				COMUtil.arrToolbarIndex[i] = COMUtil.arrDefaultToolbarIndex[i];
				COMUtil.arrToolbarSelected[i] = COMUtil.arrToolbarDefaultSelected[i];	//2014.05.16 by LYH >> 선택한 툴 버튼만 추가.
			}
			resetAnaltool();
			//2013. 9. 3 도구설정 초기화 >>

			//2015. 1. 13 by lyk 동일지표 리스트 초기화
			COMUtil.setAddJipyoList(null);
			//2015. 1. 13 by lyk 동일지표 리스트 초기화 end

			//2015.04.27 by lyk - 주기별 차트 설정
            COMUtil._mainFrame.loadPeriodSaveItem.clear();
			COMUtil.loadItem.put("graphList", new Vector());
			COMUtil.loadItem.put("graphLists", new Vector());

            COMUtil._mainFrame.loadPeriodSizeInfo.clear();  //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
			//2015.04.27 by lyk - 주기별 차트 설정 end

			//2021.10.19 by lyk - kakaopay - 매매내역 데이터 초기화 >>
			_chart.resetTradeData();
			//2021.10.19 by lyk - kakaopay - 매매내역 데이터 초기화 <<

			//2016.11.10 기준선 초기화
			if (_chart._cvm.baseLineType != null && _chart._cvm.baseLineType.size() > 0) {
				_chart._cvm.baseLineType.clear();
			}
			//2016.11.10 기준선 초기화  end

			//2021.04.28 by lyk - kakaopay - 외부 설정값 초기화 >>
			_chart._cvm.isAvgBuyPriceFunc = true; //2022.04.11 by lyk - kakaopay - 구매평균가격 표시 디폴트 true로 변경 (기획요청)
			_chart._cdm.strAvgBuyPrice = "";
			//2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) >>
//			_chart._cvm.isBuySellPriceFunc = false;
			_chart._cvm.isBuySellPriceFunc = true;
//			_chart._cvm.isCorporateCalendarFunc = false;
			_chart._cvm.isCorporateCalendarFunc = true;
			//2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) <<
			_chart._cvm.isSupportResistanceLine = false;
			_chart._cvm.isAutoTrendWaveLine = false;
			_chart._cvm.isMovingAverageLine = true;
			//2021.04.28 by lyk - kakaopay - 외부 설정값 초기화 <<

			//2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
			_chart.initBlock(true);
			if(m_bFXChart){
				_chart.setBasicUI_noVolume();
			}else{
				_chart.setBasicUI();
			}
			_chart.reSetUI(true);

			//2019. 06. 20 by hyh - 일주월분 초기화 >>
			//일주월분 flag 해제
			m_bDWMMClick = false;

			//일주월분 버튼 해제
			if (analToolScrollView != null) {
				int relativeLayout1 = context.getResources().getIdentifier("relativeLayout1", "id", context.getPackageName());
				LinearLayout toolLayout = (LinearLayout) analToolScrollView.findViewById(relativeLayout1);
				Button btnDWMM = (Button) toolLayout.findViewWithTag(String.valueOf(COMUtil.TOOLBAR_CONFIG_DWMM));

				if (btnDWMM != null) {
					btnDWMM.setSelected(false);
				}
			}
			//2019. 06. 20 by hyh - 일주월분 초기화 <<

			//2019. 08. 20 by hyh - 전체 추세선 초기화
        	this.resetAllAnalToolBySymbol();

			if (COMUtil.apiMode) {
				COMUtil.isInitChart = true;
				COMUtil.symbol = _chart._cdm.codeItem.strCode;
				this.sendTR("storageType");
				return;
			}
			if (_chart._cdm.codeItem.strRealKey.equals("SC0")) {
				sendTR(COMUtil.TR_CHART_FUTURE);
//			} else if (_chart._cdm.codeItem.strRealKey.equals("JS0")) {
//				sendTR(COMUtil.TR_CHART_UPJONG);
			} else {
				sendTR(COMUtil.TR_CHART_STOCK);
			}
//    		isInitChart = true;
//    		_chart.initBlock();
//    		_chart.setBasicUI();
//    		_chart.reSetUI(true);
//    		if(COMUtil.apiMode) {
//    			this.sendTR("storageType");
//    			return;
//    		}
//    		if(_chart._cdm.codeItem.strRealKey.equals("SC0")) {
//    			sendTR(COMUtil.TR_CHART_FUTURE);
//    		} else if(_chart._cdm.codeItem.strRealKey.equals("JS0")) {
//    			sendTR(COMUtil.TR_CHART_UPJONG);
//    		} else {
//    			sendTR(COMUtil.TR_CHART_STOCK);
//    		}
		}
        else if(type.equals("pressIndicatorInitBtn")) {
            // 화면 색상 관련 초기화.
            //2015. 1. 13 by lyk 동일지표 리스트 초기화
            COMUtil.setAddJipyoList(null);
            //2015. 1. 13 by lyk 동일지표 리스트 초기화 end

            //2015.04.27 by lyk - 주기별 차트 설정
            COMUtil._mainFrame.loadPeriodSaveItem.clear();
            COMUtil.loadItem.put("graphList", new Vector());
            COMUtil.loadItem.put("graphLists", new Vector());
            COMUtil._mainFrame.loadPeriodSizeInfo.clear();  //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
            //2015.04.27 by lyk - 주기별 차트 설정 end

            //2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
            for(int i=0; i<chartList.size(); i++) {
                BaseChart pChart = chartList.get(i);
				//2016.11.10 기준선 초기화
				if (pChart._cvm.baseLineType != null && pChart._cvm.baseLineType.size() > 0) {
					pChart._cvm.baseLineType.clear();
				}
				//2016.11.10 기준선 초기화  end
                //2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
                pChart.initBlock(false);
//                pChart.setBasicUI();
				//2017.06.13 by PJM 지표설정 초기화 버튼 fx차트 거래량 안나오게 처리
				if(m_bFXChart){
					pChart.setBasicUI_noVolume();
				}else{
					pChart.setBasicUI();
				}
                pChart.reSetUI(true);
                pChart.reset();
            }

            if(null != COMUtil.indicatorPrefEditor)
            {
                //모든 저장정보를 초기화한다.
                COMUtil.indicatorPrefEditor.clear();

                //수정완료.
                COMUtil.indicatorPrefEditor.commit();
            }
        }
        else if(type.equals("storageType")) {
			//2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
			_chart.initBlock(false);
			_chart.setBasicUI();
			_chart.reSetUI(true);
			this.sendTR(COMUtil.apCode);
		}else if(type.equals("divideInit")) {
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pChart = chartList.get(i);
				//2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
				pChart.initBlock(false);
				pChart.setBasicUI();
				pChart.reSetUI(true);
			}
		}else if(type.equals("pressCompareInitBtn")) {
			compareChart._cvm.setViewNum(200);
			compareChart._cvm.setInquiryNum(200);
		}else {
			//2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
			_chart.initBlock(false);
			_chart.setBasicUI();
			_chart.reSetUI(true);
		}

	}

	BaseChart baseChart = null;
	public BaseChart addBaseChart(int row, int col) {
		//Orientation에 따라 계산된 width와 height
		int width = frame.width();
		int height = frame.height();
		int ih = (int)COMUtil.getPixel(COMUtil.mainTopMargin);
		int nGap = 0;
		int nTotWidth = width-nGap;
		int nTotHeight = height-ih-nGap;

		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			nGap = 8;
			//2012.10.24 by LYH >> 차트 여백 조정.
			//nTotWidth = width-nGap;
			if (m_bIsAnalToolBar)
				nTotWidth = width-nGap;
			else
				nTotWidth = width;

			if(COMUtil.apiMode) {
				if(row==0 && col==0)
				{
					nGap = 0;
				}
			}
			//2012.10.24 by LYH <<


			if(m_bIsShowToolBar) {
				nTotWidth -= (int)COMUtil.getPixel(51);
			}
			nTotHeight = height-ih-nGap-_PAD_TICKER_HEIGHT;

		} else {
//    		if(COMUtil.apiMode) {
//    			if(row==0 && col==0)
//    				nGap = 0;
//				if(nDivideType > 1)
//				{
//					nGap = 2;
//				}
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(45);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(0);
//		    		}
//	    		}
//    		} else {
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(48);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(6);
//		    		}
//	    		}
//    		}
			nTotHeight = height-ih-(int)COMUtil.getPixel(_PHONE_TICKER_HEIGHT);
		}
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				width, height);
		params.leftMargin=col * (nTotWidth/m_nColumnNum)+nGap;
		params.topMargin=ih+row*(nTotHeight/m_nRowNum)+nGap;

		if((nDivideType == 31 && row == 1) || (nDivideType == 35 && row == 0)
				|| (nDivideType == 43 && row == 0) || (nDivideType == 45 && row == 1)) {
			params.width = nTotWidth - nGap;
		} else {
			params.width = (nTotWidth/m_nColumnNum)-nGap;
		}

		if((nDivideType == 34 && col == 0)||(nDivideType == 36 && col == 1)) {
			params.height = nTotHeight - nGap;
		} else {
			params.height = (nTotHeight/m_nRowNum) - nGap;
		}

		baseChart = new BaseChart(this.context, this.layout);
		baseChart.setBase(this);
//		if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//	    {
		if(row==0 && col ==0)
			baseChart.setMarginT(false);
//	    }

		if(this.mainFrame!=null && this.mainFrame.bIsHighLowSign) {
			baseChart._cvm.bIsHighLowSign = true;
		} else {
			baseChart._cvm.bIsHighLowSign = false;
		}

		if(this.mainFrame!=null && this.mainFrame.bIsHideXscale) {
			baseChart._cvm.bIsHideXYscale = true;
		} else {
			baseChart._cvm.bIsHideXYscale = false;
		}

		//2019. 03. 14 by hyh - 테크니컬차트 추가 >>
		if (this.mainFrame != null && this.mainFrame.bIsTechnical) {
			baseChart._cvm.bIsTechnical = true;
		}
		else {
			baseChart._cvm.bIsTechnical = false;
		}
		//2019. 03. 14 by hyh - 테크니컬차트 추가 <<

		//2019. 04. 01 by hyh - 차트 타이틀 적용 >>
		if (this.mainFrame != null && !this.mainFrame.strChartTitle.equals("")) {
			baseChart._cvm.strChartTitle = this.mainFrame.strChartTitle;
		}

		if (this.mainFrame != null && !this.mainFrame.strChartTitleColor.equals("")) {
			baseChart._cvm.strChartTitleColor = this.mainFrame.strChartTitleColor;
		}
		//2019. 04. 01 by hyh - 차트 타이틀 적용 <<

		//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
		if (this.mainFrame != null && this.mainFrame.nFxMarginType >= 0) {
			baseChart._cvm.nFxMarginType = this.mainFrame.nFxMarginType;
		}
		//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

		if (this.mainFrame != null && this.mainFrame.nMarketType == 6) {
			baseChart._cvm.bInvestorChart = true;
		}

		if(this.mainFrame!=null && this.mainFrame.bIsOneQStockChart)
			baseChart._cvm.bIsOneQStockChart = true;
		else
			baseChart._cvm.bIsOneQStockChart = false;

		if(this.mainFrame!=null && this.mainFrame.bIsHideXScale)
			baseChart._cvm.bIsHideXYscale = true;
		else
			baseChart._cvm.bIsHideXYscale = false;

		if(m_bFXChart)
		{
			baseChart._cvm.m_bFXChart = true;
			baseChart.setChartItemViewFXHidden(true);
		}

		if(COMUtil.bIsMiniBong)
		{
			baseChart._cvm.bIsMiniBongChart = true;
			baseChart._cvm.XSCALE_H=(int)COMUtil.getPixel(15);
            COMUtil.bIsMiniBong = false;
		}
		baseChart.setLayoutParams(params);
		baseChart.init();
		if(row==0 && col == 0) {
			baseChart.setAllProperties();
		}
		if(chartList == null) {
			chartList = new Vector<BaseChart>();
//			COMUtil.divideChartList = chartList;
		}
		chartList.add(baseChart);
		baseChart.setChartIndex(chartList.size()); //chart index 를 키로 설정한다.(블럭삭제버튼에서 사용)set by lyk

		baseChart.addChartChangedListener(this);
		//2011.08.05 by LYH >> setBounds 이전에 추가 되도록 이동 - chartItemView 차트 위에 추가 위함
		//baseChart.onResume();
		//2012. 7. 31   basechart xml 에서 inflate 한 layout 변수를 addview 함 
		layout.addView(baseChart);
//		baseChart.setZOrderOnTop(false);
		//2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
		if(COMUtil.bSubChart) {
			baseChart._cvm.bSubChart = true;
			COMUtil.bSubChart = false;
		}
		//2013.09.13 <<
		//2020.04.14 당일 라인차트 추가 - hjw >>
		if(COMUtil.bIsTodayLineChart) {
			baseChart._cvm.bIsTodayLineChart = true;
			COMUtil.bIsTodayLineChart = false;
		}
		//2020.04.14 당일 라인차트 추가 - hjw <<

		baseChart.m_bHaveMA = COMUtil.bHaveMA;
		//2012. 7. 9  차트 초기 로딩시 타이틀 가려지는 현상 해결위해 위쪽 코드 위치이동 
		if(COMUtil.bIsMigyul)
		{
			COMUtil.bIsMigyul = false;
			baseChart.setBasicUI_Fut();
		}
		//2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
		//2013. 9. 24 관심-세로분할 일 때 차트  형식 변경
		else if(COMUtil._mainFrame.bIsLineFillChart)
		{
			baseChart.setLineFillUI();
		}
		else if(COMUtil._mainFrame.bIsLineChart)
		{
			//baseChart._cdm.m_bRealUpdate = true;
			if(COMUtil._mainFrame.bIsNewsChart)
			{
				baseChart._cvm.bIsNewsChart = true;
//				baseChart._cvm.XSCALE_H=(int)COMUtil.getPixel(25);
				baseChart._cvm.XSCALE_H=(int)COMUtil.getPixel(25);
			}
			baseChart.setLineUI(COMUtil.m_sLineColor, COMUtil.m_sTextColor);
		}
        //2016.08.25 by LYH >> 2일 라인차트 타입 추가
		else if(COMUtil._mainFrame.bIsLine2Chart)
		{
			baseChart._cdm.m_bRealUpdate = true;
			baseChart.setBasicUI_2Day();
		}
        //2016.08.25 by LYH << 2일 라인차트 타입 추가
		else if(COMUtil.bRateCompare)
		{
			baseChart.setBasicUI_RateCompare();
			baseChart._cvm.setOnePage(1);
			baseChart._cvm.bRateCompare = true;
			COMUtil.bRateCompare = false;
		}
		//2013.01.04 by LYH <<
		//2013.07.31 >> 기준선 라인 차트 타입 추가 
		else if(COMUtil.bStandardLine)
		{
			//2015.01.08 by LYH >> 3일차트 추가
			if(COMUtil.b3DayChart)
			{
				baseChart._cdm.m_bRealUpdate = true;
				baseChart.setBasicUI_Standard_3Day();
			}
			else
				//2015.01.08 by LYH << 3일차트 추가	    		
				baseChart.setBasicUI_Standard();
			//baseChart._cvm.setOnePage(1);
			baseChart._cvm.setViewNum(80);
			COMUtil.bStandardLine = false;
		}
		//2013.07.31 <<
		else if(COMUtil.bNetBuyChart)
		{
			baseChart._cvm.bNetBuyChart = true;			
			baseChart.setBasicUI_NetBuy();
			COMUtil.bNetBuyChart = false;
		}
		//2013.01.04 by LYH <<
		else {
			if (m_bFXChart) {
				baseChart.setBasicUI_noVolume();
			} else {
				baseChart.setBasicUI();
			}
		}
		//2011.08.05 by LYH <<
		baseChart.setBounds(0, 0, params.width, params.height);

		if(COMUtil._neoChart !=null)
		{
			if(row==0 && col ==0)
				baseChart.setSkinType(COMUtil.getSkinType());
			else
				baseChart.setSkinType(COMUtil._neoChart._cvm.getSkinType());
		}
		else
			baseChart.setSkinType(COMUtil.getSkinType());

		//2013.09.12 by LYH >> 가로차트 일 경우 현재가 정보 무조건 보이도록 수정.
//	    if(row==0 && col ==0)
//	    	baseChart.showChartItem(false);
//		Configuration config = getResources().getConfiguration();
		if(row==0 && col ==0)
			baseChart.showChartItem(false);
		//2013.09.12 by LYH <<

		//2012.11.29 by LYH >> neochart에 ctlchartex 연결 <<
		baseChart.userProtocol = COMUtil._mainFrame.userProtocol;

		//2019. 06. 12 by hyh - 차트 아이템 뷰 넓이 조정 >>
//		boolean bIsWideChartItem = false;
//
//		if((nDivideType == 31 && m_nTotNum == 3 && row == 1)
//		|| (nDivideType == 35 && m_nTotNum == 3 && row == 0)
//		|| (nDivideType == 43 && row == 0)
//		|| (nDivideType == 45 && row == 1)) {
//			bIsWideChartItem = true;
//		} else {
//			if (m_nColumnNum == 1) {
//				bIsWideChartItem = true;
//			}
//		}
//
//		if (baseChart.chartItem != null) {
//			baseChart.chartItem.setWide(bIsWideChartItem);
//		}
		//2019. 06. 12 by hyh - 차트 아이템 뷰 넓이 조정 <<

		return baseChart;

	}

	//    public void setCompareCodes() {
//    	m_nCompareRotateIndex = -1;
//    	Vector codes = null;
//    	Vector names = null;
//    	if(codes != null && codes.size()>0) {
//    		compareChart.removeAllCodes();
//    		for(int i=0; i<codes.size(); i++) {
//    			String strCode = (String)codes.get(i);
//    			if(!strCode.equals("")) {
//    				compareChart.addCode((String)codes.get(i), (String)names.get(i));
//    			}
//    		}
//    	}
//    	bSendCompareData= true;
//    	sendTrCodes();
//    }
	public void showCompareChartUI(boolean selected) {
		if(selected) {
			BaseChart_Multi chart = showCompareChart(true);
			COMUtil._neoChart = chart;
			COMUtil.preChartMode = COMUtil.chartMode;
			COMUtil.chartMode = COMUtil.COMPARE_CHART;
			//_chart = chart;

			m_nCompareRotateIndex = -1;
			if(compareChart.getCodeCount()<1 || m_nTotNum > 1) {
				compareChart.removeAllCodes();
				for(int i=0; i<chartList.size(); i++) {
					BaseChart pChart = chartList.get(i);
					compareChart.addCode(pChart._cdm.codeItem.strCode, pChart._cdm.codeItem.strName, pChart._cdm.codeItem.strMarket);
				}
				bSendCompareData = true;
				sendTrCodes();
			} else {
				if(COMUtil.dataTypeName.equals("0")) {
					COMUtil.dataTypeName = "1";
					COMUtil.unit = "1";
				}
				bSendCompareData = true;
				sendTrCodes();
			}
			if(isMultiChart()) {
//				if(inputPanel != null) inputPanel.showSyncButton(false);
			}
		} else {
			COMUtil.chartMode = COMUtil.preChartMode;
			showCompareChart(false);
			if(isMultiChart()) {
//				if(inputPanel != null) inputPanel.showSyncButton(true);
			}
		}


	}
	public void setDivision(int tag) {
		if(nDivideType == tag) {
			return;
		}
		nDivideType = tag;
		COMUtil.divideType = nDivideType;
		if(tag==0 || tag==11) {
//			if(inputPanel != null) this.inputPanel.showSyncButton(false);
			m_nRotateIndex=-1;
		} else {
//			if(inputPanel != null) this.inputPanel.showSyncButton(true);
		}

		if(tag>10) {
			tag = tag/10-1;
		}

		//2015. 1. 19 일주월분 버튼 토글로 변경 >> : 분할상태 변경될 때에는 일주월분 상태 해제
		if(m_bDWMMClick)
		{
			//일주월분 flag 해제
			m_bDWMMClick = false;

			//일주월분 버튼 해제
			int relativeLayout1 = context.getResources().getIdentifier("relativeLayout1", "id", context.getPackageName());
			LinearLayout toolLayout = (LinearLayout) analToolScrollView.findViewById(relativeLayout1);
			Button btnDWMM = (Button)toolLayout.findViewWithTag(String.valueOf(COMUtil.TOOLBAR_CONFIG_DWMM));
			btnDWMM.setSelected(false);
		}
		//2015. 1. 19 일주월분 버튼 토글로 변경 <<

		setDivisionChart(tag+1);

		this.sendTR(String.valueOf(COMUtil._TAG_RESET_MULTICODES));

		_chart.selectChart();	//2014. 2. 25 분할 갯수 줄였을 때 차트 주기 버튼 선택효과 정상적으로 표시되게 하기
	}
	/** 저장된 차트 로드 **/
	private Vector<String> symbols=null;
	private Vector<String> marketNames=null;
	private Vector<String> lcodes=null;
	private Vector<String> dataTypeNames=null;
	private Vector<String> counts=null;
	private Vector<String> viewnums=null;
	private Vector<String> units=null;
	private Vector<String> apCodes=null;
	private Vector<String> codeNames=null;
	private Vector<String> barTypes=null;//2015.04.30 by lyk - 바(시고저종) 유형 추가

	//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
	private Vector<String> fxMarginTypes=null;
	private Vector<String> connTypes=null;
	private Vector<String> dayTypes=null;
	private Vector<String> floorTypes=null;
	//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

	public void setStorageDivision(int tag, boolean isSyncJongmokVal, boolean isSyncJugiVal, Hashtable<String, Vector<String>> items, boolean isDivision ) {
		nDivideType = tag;
		if(items!=null) {
			this.symbols = items.get("symbols");
			this.marketNames = items.get("marketNames");
			this.lcodes = items.get("lcodes");
			this.dataTypeNames = items.get("dataTypeNames");
			this.counts = items.get("counts");
			this.viewnums = items.get("viewnums");
			this.units = items.get("units");
			this.apCodes = items.get("apCodes");
			this.codeNames = items.get("codeNames");
			this.barTypes = items.get("barTypes");//2015.04.30 by lyk - 바(시고저종) 유형 추가

			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
			this.fxMarginTypes = items.get("fxMarginTypes");
			this.connTypes = items.get("connTypes");
			this.dayTypes = items.get("dayTypes");
			this.floorTypes = items.get("floorTypes");
			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

			if(isDivision) {
				divideStorageType = true;
			}
		}

		COMUtil.divideType = nDivideType;

		if(tag==0 || tag==11) {
//			if(inputPanel != null) this.inputPanel.showSyncButton(false);
		} else {
//			if(inputPanel != null) this.inputPanel.showSyncButton(true);
		}

		if(tag>10) {
			tag = tag/10-1;
		}
		setDivisionChart(tag+1);

		//동기화처리.
//    	this.isSyncJongmok = isSyncJongmokVal;
//    	this.isSyncJugi = isSyncJugiVal;

//		for(int i=0; i<chartList.size(); i++) {
//			BaseChart baseChart = chartList.get(i);
//			baseChart.setSkinType(COMUtil.skinType);
//
//			//2015.04.30 by lyk - 바(시고저종) 유형 추가
//			if(barTypes!=null)
//				baseChart._cvm.bIsOHLCType = barTypes.get(i);
//			//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
//		}
	}
	public void setDivisionChart(int nDivCount) {
		if(nDivCount>1) {
			COMUtil.chartMode = COMUtil.DIVIDE_CHART;
		} else {
			COMUtil.chartMode = COMUtil.BASIC_CHART;
		}
		if(m_nExtendChart >= 0) {
			m_nExtendChart = -1;
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pChart = chartList.get(i);
				if(pChart.equals(_chart)) {
					resizeBaseChart(pChart, m_nTotNum, getRowIndex(i), getColIndex(i));
					pChart.setExtendButton();
				} else {
					pChart.setVisibility(View.VISIBLE);
					//pChart.onResume();

				}

			}
		}
		boolean bSendTR = true;
		if(nDivCount <= m_nTotNum || m_bDWMMClick || COMUtil.apiMode) {
			bSendTR = false;
		} else {
			m_nQueryChartIndex = chartList.size();
		}

		switch(nDivideType)
		{
			case 11:
			{
				m_nRowNum = 1;
				m_nColumnNum = 1;

				//2013. 9. 17 원분할때는 동기화 초기화>>
				//원분할일때는 종목/주기/지표 동기화 초기화시킨다. 
				this.m_bSyncJongMok = false;
				COMUtil.isSyncJongmok = false;

				this.m_bSyncJugi = false;
				COMUtil.isSyncJugi = false;

				this.m_bSyncIndicator = false;
				//2013. 9. 17 원분할때는 동기화 초기화>>

				break;
			}
			case 21:
			{
				m_nRowNum = 2;
				m_nColumnNum = 1;
				break;
			}
			case 22:
			{
				m_nRowNum = 1;
				m_nColumnNum = 2;
				break;
			}
			case 31:
			{
				m_nRowNum =2;
				m_nColumnNum = 2;
				break;
			}
			case 32:
			{
				m_nRowNum =3;
				m_nColumnNum = 1;
				break;
			}
			case 33:
			{
				m_nRowNum =1;
				m_nColumnNum = 3;
				break;
			}
			case 34:
			{
				m_nRowNum =2;
				m_nColumnNum = 2;
				break;
			}
			case 35:
			{
				m_nRowNum =2;
				m_nColumnNum = 2;
				break;
			}
			case 36:
			{
				m_nRowNum =2;
				m_nColumnNum = 2;
				break;
			}
			case 41:
			{
				m_nRowNum = 2;
				m_nColumnNum = 2;
				break;
			}
			case 42:
			{
				m_nRowNum = 4;
				m_nColumnNum = 1;
				break;
			}
			case 43:
			{
				m_nRowNum = 2;
				m_nColumnNum = 3;
				break;
			}
			case 44:
			{
				m_nRowNum = 1;
				m_nColumnNum = 4;
				break;
			}
			case 45:
			{
				m_nRowNum = 2;
				m_nColumnNum = 3;
				break;
			}
		}
		resetChartDivision(nDivCount);
		m_nTotNum = nDivCount;

		boolean bShowTooltip = _chart._cvm.isCrosslineMode;

		//2019. 06. 19 by hyh - 일주월분 초기화 개선 >>
		int nFxMarginType = _chart._cvm.nFxMarginType;
		boolean bIsConn = _chart._cvm.bIsConn;
		boolean bIsDay = _chart._cvm.bIsDay;
		boolean bIsFloor = _chart._cvm.bIsFloor;
		//2019. 06. 19 by hyh - 일주월분 초기화 개선 <<

		for(int i=chartList.size(); i<nDivCount; i++) {
			BaseChart pChart = addBaseChart(getRowIndex(i), getColIndex(i));
			//pChart.setSkinType(COMUtil.skinType);


			//if(i==nDivCount-1) {
			if(i==0){
				pChart.setSelected(true);
				//chart = pChart;
				_chart = pChart;
				COMUtil._neoChart = _chart;

			}else{
				pChart._cvm.isCrosslineMode = bShowTooltip;

				//2019. 06. 19 by hyh - 일주월분 초기화 개선 >>
				pChart._cvm.nFxMarginType = nFxMarginType;
				pChart._cvm.bIsConn = bIsConn;
				pChart._cvm.bIsDay = bIsDay;
				pChart._cvm.bIsFloor = bIsFloor;
				//2019. 06. 19 by hyh - 일주월분 초기화 개선 <<
			}
		}

		//분할차트관련 추가(2011.09.15 by lyk)

		if(divideStorageType) {
			if(m_bGetData)
				this.initChart("divideInit");//차트초기화.
			m_nRotateIndex=-1;
			m_nQueryChartIndex=0;
			this.sendRotateTR_storage();
		} else {
			if(bSendTR) {
				String sendTrType = COMUtil.getSendTrType();
				sendTR(sendTrType);
			}
		}

		//if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
		bringToFrontCtrl();
		//}
	}
	public void bringToFrontCtrl() {
//    	if(analToolbarCloseLayout!=null) {
//	    	analToolbarCloseLayout.bringToFront();
//	    	toolbarBackBtn.bringToFront();
//	    	analToolbarLayout.bringToFront();
//    	}
		if (analToolbarLayout != null) {
			analToolbarLayout.bringToFront();
		}

		if (btnSimpleOpen != null) {
			btnSimpleOpen.bringToFront();
		}
		if (rv_toolbar_oneq != null) {
			rv_toolbar_oneq.bringToFront();
		}
	}
	public void sendTR(String data) {
		int dataInt=0;
		try {
			dataInt = Integer.parseInt(data);
		} catch(Exception e) {

		}

		if(data.equals("requestAddData")) {
			COMUtil.sendTR(data, _chart.userProtocol);
			isInitChart = false;

			//돌려보기시 동기화상태이고 마켓지표가 있는 경우 조회하지 않음.
			if((m_bSyncJongMok || m_bSyncJugi) && isRotation && isSendMarket)
				return;

			if(data.equals(COMUtil.TR_COMPARE_STOCK) || data.equals(COMUtil.TR_COMPARE_FUTURE) || data.equals(COMUtil.TR_COMPARE_UPJONG)) {
				this.bSendCompareData = true;
			}

			return;
		}

		//System.out.println("DEBUG_base11:=========sendTr1:m_nRotateIndex:"+m_nRotateIndex+" isSendMarket:"+isSendMarket);
		boolean hasMarketType =false;
        if(_chart!=null)
			hasMarketType =	_chart.hasMarketType();

        if(!hasMarketType)
            m_bSyncIndicator = false;
	
		if(dataInt!=COMUtil._TAG_SELECTED_CHART && (m_nRotateIndex>=0 && !m_bSyncIndicator && !hasMarketType) && divideStorageType==false && !isInitChart) {
			//System.out.println("base11:=========sendTr1:isSendMarket:"+isSendMarket+" isInitChart:"+isInitChart);
			isInitChart = false;
			return;
		}

//    	//돌려보기시 동기화상태이고 마켓지표가 있는 경우 조회하지 않음.
//    	if((m_bSyncJongMok || m_bSyncJugi) && isRotation && hasMarketType)
//    		return;

		if(data.equals(COMUtil.TR_COMPARE_STOCK) || data.equals(COMUtil.TR_COMPARE_FUTURE) || data.equals(COMUtil.TR_COMPARE_UPJONG)) {
			this.bSendCompareData = true;
		}
//    	System.out.println("base11:sendTr2:isSendMarket:"+isSendMarket+" isInitChart:"+isInitChart);
	    UserProtocol userProtocol = null;
	    if(_chart!=null) {
	    	userProtocol = _chart.userProtocol;
	    }
	    COMUtil.sendTR(data, userProtocol);
	}
	public void sendRotateTR() {
		if(m_nRotateIndex == -1 && !m_bDWMMClick) {
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				if(pBaseChart.equals(_chart)) {
					m_nSelIndex = i;
					break;
				}
			}
		}
		m_nRotateIndex++;
		if(m_nSelIndex == m_nRotateIndex) {
			m_nRotateIndex++;
		}
//    	System.out.println("base11:sendRotateTr m_nRotateIndex:"+m_nRotateIndex);
		if(m_nRotateIndex >= chartList.size()) {
			if(m_nSelIndex != (m_nRotateIndex-1) || m_nSelIndex == (chartList.size()-1)) {
				BaseChart pChart = chartList.get(m_nSelIndex);
				_chart = pChart;
				for(int i=0; i<chartList.size(); i++)
				{
					pChart = chartList.get(i);
					if(m_nTotNum != 1 && i == m_nSelIndex)
					{
						pChart.setSelected(true);
						pChart.repaintAll();
					}
					else {
						if(pChart.isSelected()) {
							pChart.setSelected(false);
							pChart.repaintAll();
						}
					}
				}
				COMUtil._neoChart = _chart;
				//_chart = chart;

				_chart.selectChart();
			}

			m_nRotateIndex = -1;
//    		m_bDWMMClick = false;	//2015. 1. 19 일주월분 버튼 토글로 변경
			m_bConnectSocket = false;
			m_bSyncIndicator = false;
			isInit = false;
//            isSendMarket = false;

			if(baseChart.userProtocol!=null) baseChart.userProtocol.requestInfo(COMUtil._TAG_SET_MARKET_ROTATE_END, null);

//            System.out.println("base11:_TAG_SET_MARKET_ROTATE_END");

			return;
		}

		BaseChart pBaseChart = chartList.get(m_nRotateIndex);
		if(!m_bSyncJongMok) {
			if(pBaseChart._cdm.codeItem.strCode != null && pBaseChart._cdm.codeItem.strCode.length()>0)
			{
				COMUtil.symbol = pBaseChart._cdm.codeItem.strCode;
				COMUtil.market = pBaseChart._cdm.codeItem.strMarket;
				COMUtil.codeName = pBaseChart._cdm.codeItem.strName;
				COMUtil.lcode = pBaseChart._cdm.codeItem.strRealKey;
				if(COMUtil.lcode==null) COMUtil.lcode = "S31";
				String realKey = pBaseChart._cdm.codeItem.strRealKey;
				if(realKey==null) realKey = "S31";
				if(realKey.equals("SC0")) {
					COMUtil.apCode = COMUtil.TR_CHART_FUTURE;
//	    		} else if(realKey.equals("JS0")) {
//	    			COMUtil.apCode = COMUtil.TR_CHART_UPJONG;
				} else {
					COMUtil.apCode = COMUtil.TR_CHART_STOCK;
				}
			}
			else
			{
				if(m_bSyncJugi)
				{
					m_nRotateIndex++;
					if(m_nRotateIndex >= chartList.size()) {
						endRotate();
						return;
					}
					pBaseChart = chartList.get(m_nRotateIndex);
					
					BaseChart pChart;
					for(int i=m_nRotateIndex; i<chartList.size(); i++)
					{
						pChart = chartList.get(i);
						if(pChart._cdm.codeItem.strCode == null || pChart._cdm.codeItem.strCode.length()==0)
						{
							m_nRotateIndex++;
							if(m_nRotateIndex >= chartList.size()) {
								endRotate();
								return;
							}
							pBaseChart = chartList.get(m_nRotateIndex);
						}
					}
					if(pBaseChart._cdm.codeItem.strCode != null && pBaseChart._cdm.codeItem.strCode.length()>0)
					{
						COMUtil.symbol = pBaseChart._cdm.codeItem.strCode;
						COMUtil.market = pBaseChart._cdm.codeItem.strMarket;
						COMUtil.codeName = pBaseChart._cdm.codeItem.strName;
						COMUtil.lcode = pBaseChart._cdm.codeItem.strRealKey;
						if(COMUtil.lcode==null) COMUtil.lcode = "S31";
						String realKey = pBaseChart._cdm.codeItem.strRealKey;
						if(realKey==null) realKey = "S31";
						if(realKey.equals("SC0")) {
							COMUtil.apCode = COMUtil.TR_CHART_FUTURE;
//			    		} else if(realKey.equals("JS0")) {
//			    			COMUtil.apCode = COMUtil.TR_CHART_UPJONG;
						} else {
							COMUtil.apCode = COMUtil.TR_CHART_STOCK;
						}
					}
				}
			}
		}

		if(!m_bSyncJugi) {
			if(pBaseChart._cdm.codeItem.strCode != null && pBaseChart._cdm.codeItem.strCode.length()>0)
			{
				COMUtil.dataTypeName = pBaseChart._cdm.codeItem.strDataType;
				COMUtil.unit = pBaseChart._cdm.codeItem.strUnit;
				if(COMUtil.unit==null) COMUtil.unit = "1";
			}
		}

		if(m_bDWMMClick)
		{
			switch (m_nRotateIndex) {
				case 0:
					COMUtil.dataTypeName = "2";
					break;
				case 1:
					COMUtil.dataTypeName = "3";
					break;
				case 2:
					COMUtil.dataTypeName = "4";
					break;
				case 3:
					COMUtil.dataTypeName = "1";
					break;
			}
			//if(m_bConnectSocket && m_nRotateIndex == 3)
			if(m_nRotateIndex == 3)
				COMUtil.unit = "5";
			else
				COMUtil.unit = "1";
		}

		//증권그룹ID
		// 0 >> 전체
		// 1 >> 주식
		// 2 >> ELW
		// 3 >> 상장지수펀드(ETF)
		// 4 >> 거래원
		// 5 >> 선물
		// 6 >> 옵션
		if( ( 	(COMUtil.market.equals("2")) ||  //elw
				(COMUtil.market.equals("5")) ||  //선물
				(COMUtil.market.equals("6")) )   //옵션
				&&  (COMUtil.dataTypeName.equals("5")) )  //콜     //년 
		{
			if(m_bSyncJongMok)
			{
				COMUtil.dataTypeName = "2";
			}
			else
			{
				return;
			}
		}
		if(COMUtil.apiMode) {
			COMUtil.sendTR("storageType", _chart.userProtocol);
		} else {
			COMUtil.sendTR(COMUtil.apCode, _chart.userProtocol);
		}
	}

	private void endRotate()
	{
			if(m_nSelIndex != (m_nRotateIndex-1) || m_nSelIndex == (chartList.size()-1)) {
				BaseChart pChart = chartList.get(m_nSelIndex);
				_chart = pChart;
				for(int i=0; i<chartList.size(); i++)
				{
					pChart = chartList.get(i);
					if(m_nTotNum != 1 && i == m_nSelIndex)
					{
						pChart.setSelected(true);
						pChart.repaintAll();
					}
					else {
						if(pChart.isSelected()) {
							pChart.setSelected(false);
							pChart.repaintAll();
						}
					}
				}
				COMUtil._neoChart = _chart;
				//_chart = chart;

				_chart.selectChart();
			}
			m_nRotateIndex = -1;
//    		m_bDWMMClick = false;	//2015. 1. 19 일주월분 버튼 토글로 변경
			m_bConnectSocket = false;
			m_bSyncIndicator = false;
			isInit = false;
			
			m_bRotateEnd = true;
//            isSendMarket = false;

			if(baseChart.userProtocol!=null) baseChart.userProtocol.requestInfo(COMUtil._TAG_SET_MARKET_ROTATE_END, null);
	}
	/** 저장된 분할차트를 복원하는 함수 **/
	public void sendRotateTR_storage() {
		synchronized (this)    {
			COMUtil.isProcessData=false;
			if(chartList==null || chartList.size()==0) {
				return;
			}
			if(m_nRotateIndex == -1) {
				for(int i=0; i<chartList.size(); i++) {
					BaseChart pBaseChart = chartList.get(i);
					if(pBaseChart.equals(_chart)) {
						m_nSelIndex = i;
						break;
					}
				}
			}

			if ((m_nRotateIndex+1)<chartList.size()) {
				BaseChart pChart = chartList.get(m_nRotateIndex+1);
				boolean hasMarketType = pChart.hasMarketType();
				if (hasMarketType) {
					isSendMarket = true;

					//2015. 3. 9 시장지표 여러개 추가되어 rotateblock 생성시 조회안됨
//	                if (isInit) {
//	                    m_nRotateIndex++;
//	                }

					//마켓지표가 있으면, 마켓지표 데이터 처리 후 직접 tr요청됨

					return;
				}
			}

			m_nRotateIndex++;

			//marketType 불럭이 존재하는 경우 (market 데이터 처리 후 rotateTr 처리)

			isSendMarket = false;

			if(m_nRotateIndex >= chartList.size()) {
				if(m_nSelIndex != (m_nRotateIndex-1) || m_nSelIndex == (chartList.size()-1)) {
					BaseChart pChart = chartList.get(m_nSelIndex);
					_chart = pChart;
					for(int i=0; i<chartList.size(); i++)
					{
						pChart = chartList.get(i);
						if(m_nTotNum != 1 && i == m_nSelIndex)
						{
							pChart.setSelected(true);
							pChart.repaintAll();
						}
						else {
							if(pChart.isSelected()) {
								pChart.setSelected(false);
								pChart.repaintAll();
							}
						}
					}
					COMUtil._neoChart = _chart;
					//_chart = chart;

					_chart.selectChart();
				}

				m_nRotateIndex = -1;
				storageDivideIndex=-1;
				divideStorageType=false;
				m_bSyncIndicator = false;
				isInit = false;

//	    		//동기화 처리.
//	    		m_bSyncJongMok = isSyncJongmok;
//	    		COMUtil.isSyncJongmok=isSyncJongmok;
//	    		if(inputPanel!=null) inputPanel.syncJongmokButton.setSelected(isSyncJongmok);
//	    		
//	    		m_bSyncJugi = isSyncJugi;
//	    		COMUtil.isSyncJugi = isSyncJugi;
//	    		if(inputPanel!=null) inputPanel.syncJugiButton.setSelected(isSyncJugi);

				this.sendTR(String.valueOf(COMUtil._TAG_RESET_MULTICODES));

                //2019. 09. 11 by hyh - sendRotateTR_storage() 도중 터치 불가능하도록 처리 >>
                COMUtil.m_nRotateIndex = m_nRotateIndex;
                //2019. 09. 11 by hyh - sendRotateTR_storage() 도중 터치 불가능하도록 처리 <<

				return;
			}
//	    	try {
			// 2011.11.23 >> 예외 상황 처리
			if(symbols==null || symbols.size()<=m_nRotateIndex)
			{
				m_nRotateIndex = -1;
				storageDivideIndex=-1;
				divideStorageType = false;
				return;
			}
			String strSymbol = this.symbols.get(m_nRotateIndex);
			if(strSymbol.length() <1)
			{
//				if(this.symbols.size()==(m_nRotateIndex+1))
                if(this.symbols.size() < 1)
				{
					//변수 초기화.
					m_nRotateIndex = -1;
					storageDivideIndex=-1;
					divideStorageType = false;
				}
				//2013.10.04 by LYH >> 분할 상태에서 종목이 없는 경우 끝내지 않고 다음 종목 조회 가능하도록 개선
				else
				{
					sendRotateTR_storage();
				}
				//2013.10.04 by LYH <<
				return;
			}
			if(marketNames!=null && marketNames.size()>0 && m_nRotateIndex<=(marketNames.size()-1)) {
				COMUtil.market = marketNames.get(m_nRotateIndex);
			}
			if(codeNames!=null && codeNames.size()>0 && m_nRotateIndex<=(codeNames.size()-1))
				COMUtil.codeName = codeNames.get(m_nRotateIndex);

			COMUtil.symbol = strSymbol;

			String strRealKey = "";
			if(lcodes!=null && lcodes.size()>0 && m_nRotateIndex<=(lcodes.size()-1)) {
				strRealKey = this.lcodes.get(m_nRotateIndex);
			}
			COMUtil.lcode = strRealKey;
			if(strRealKey.equals("SC0")) {
				COMUtil.apCode = COMUtil.TR_CHART_FUTURE;
//				} else if(strRealKey.equals("JS0")) {
//					COMUtil.apCode = COMUtil.TR_CHART_UPJONG;
			} else {
				COMUtil.apCode = COMUtil.TR_CHART_STOCK;
			}

			if(dataTypeNames!=null && dataTypeNames.size()>0 && m_nRotateIndex<=(dataTypeNames.size()-1))
				COMUtil.dataTypeName = this.dataTypeNames.get(m_nRotateIndex);

			if(units!=null && units.size()>0 && m_nRotateIndex<=(units.size()-1))
				COMUtil.unit = this.units.get(m_nRotateIndex);

			COMUtil.nkey=" ";

			BaseChart pChart = null;
			if(chartList!=null && chartList.size()>0 && m_nRotateIndex<=(chartList.size()-1))
				pChart = chartList.get(m_nRotateIndex);

			int reqCnt = 200;
			try {
				reqCnt = Integer.parseInt(counts.get(m_nRotateIndex));
			}catch (Exception e) {

			}
			COMUtil._neoChart._cvm.setInquiryNum(reqCnt);

			int viewCnt = 100;
			try {
				viewCnt = Integer.parseInt(viewnums.get(m_nRotateIndex));
			} catch(Exception e) {

			}
			pChart._cvm.setViewNum(viewCnt);

			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
			try {
				pChart._cvm.nFxMarginType = Integer.parseInt(fxMarginTypes.get(m_nRotateIndex));
				pChart._cvm.bIsConn = connTypes.get(m_nRotateIndex).equals("1") ? true : false;
				pChart._cvm.bIsDay = dayTypes.get(m_nRotateIndex).equals("1") ? true : false;
				pChart._cvm.bIsFloor = floorTypes.get(m_nRotateIndex).equals("1") ? true : false;
			} catch (Exception e) {
			}
			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

			//apiMode로 작동시 처리.
			if(COMUtil.apiMode) {
				COMUtil.m_nRotateIndex = m_nRotateIndex;
				this.sendTR("storageType");
			} else {
				COMUtil.sendTR(this.apCodes.get(m_nRotateIndex), _chart.userProtocol);
			}
//	    	} catch(Exception e) {
//	    		System.out.println("Base11 exception : "+e.getMessage());
//	    	}
		}
	}
	public void resizeBaseChart(BaseChart pChart, int nDivCount, int row, int col) {
		if(nDivCount==1) COMUtil._neoChart = pChart;

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		if (COMUtil._mainFrame.bShowOneTouch) {
			showOneTouch(true);
		}
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

		//2019. 06. 12 by hyh - 차트 상단 아이템뷰 보기 유무 >>
		if(m_nExtendChart<0 && nDivCount<=1)
			pChart.showChartItem(false);
		else
			pChart.showChartItem(true);
//	    }
		//2019. 06. 12 by hyh - 차트 상단 아이템뷰 보기 유무 <<

		int ih = (int)COMUtil.getPixel(COMUtil.mainTopMargin);

		//2011.08.05 by LYH >> 분할 시 차트 사이즈 개선
//    	int nGap = 8;
//    	int nTotWidth = frame.width()-nGap;
//    	int nTotHeight = frame.height()-ih-nGap - 53;
//    	if(m_bIsShowToolBar) {
//    		nTotWidth -= 51;
//    	}
		int chartWidth = frame.width();
		int chartHeight = frame.height();
//    	System.out.println("resizeBaseChart width:"+chartWidth+" height:"+chartHeight);
		//int nGap = 2;
		int nGap = 0;
		int nTotWidth = chartWidth-nGap;
		int nTotHeight = chartHeight-ih-nGap - _PAD_TICKER_HEIGHT;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//    		nGap = 8;
//    		//2013.6.17 (미래에셋-태블릿) 시장정보차트 우측 보라색 여백 안보이게 수정 >>
////    		nTotWidth = chartWidth-nGap;
//    		//nTotWidth 가 초기화될때 nGap 만큼 빼준 것 다시 더해서 원래크기로 복원 
//    		nTotWidth += 2;
//    		//2013.6.17 (미래에셋-태블릿) 시장정보차트 우측 보라색 여백 안보이게 수정 <<
//    		//2012.10.24 by LYH >> 차트 여백 조정.
//    		if (COMUtil.apiMode) {
//                if(nDivCount<=1)
//                    nGap = 0;
//            }
//    		//2012.10.24 by LYH <<
//    		if(m_bIsShowToolBar) {
//    			nTotWidth -= (int)COMUtil.getPixel(51);
//    		}
			nTotHeight = chartHeight-ih-nGap-_PAD_TICKER_HEIGHT;
		} else {
//    		if(COMUtil.apiMode) {
//    			if(nDivCount == 1) 
//    				nGap = 0;
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(43);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(0);
//		    		}
//	    		}
//    		} else {
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(48);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(6);
//		    		}
//	    		}
//    		}
			nTotHeight = chartHeight-ih-(int)COMUtil.getPixel(_PHONE_TICKER_HEIGHT);
		}
		//2011.08.05 by LYH <<    	

		int nColumnNum = m_nColumnNum;
		int nRowNum = m_nRowNum;
		if((m_nExtendChart >=0 && nDivCount==1)|| pChart.equals(compareChart)) {
			nColumnNum = 1;
			nRowNum = 1;
		}

		int width, height, left, top;
		boolean bIsWideChartItem = false;
		left = col * (nTotWidth/nColumnNum) + nGap;
		top = ih + row * (nTotHeight/nRowNum) + nGap;

		if((nDivideType == 31 && nDivCount == 3 && row == 1) || (nDivideType == 35 && nDivCount == 3 && row == 0)
				|| (nDivideType == 43 && row == 0) || (nDivideType == 45 && row == 1)) {
			width = nTotWidth - nGap;
			bIsWideChartItem = true;
		} else {
			width = (nTotWidth/nColumnNum) - nGap;

			if (nColumnNum == 1) {
				bIsWideChartItem = true;
			}
		}

		if((nDivideType == 34 && nDivCount == 3 && col == 0)||(nDivideType == 36 && nDivCount == 3 && col == 1)) {
			height = nTotHeight - nGap;
		} else {
			height = (nTotHeight/nRowNum) - nGap;
		}

//    	RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//				width, height);
//    	params.leftMargin=left;
//    	params.topMargin=top;

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)pChart.getLayoutParams();
		param.leftMargin = left;
		param.topMargin = top;
//    	params.leftMargin=0;
//    	params.topMargin=0;
//    	pChart.setBounds2(params.leftMargin, params.topMargin, params.width, params.height);
		pChart.isChangeBlock = isChangeBlock;
		pChart.setBounds2(0, 0, width, height);
		pChart.isChangeBlock = false;

//		if (pChart.chartItem != null) {
//			pChart.chartItem.setWide(bIsWideChartItem);
//		}
	}

	public void resizeBaseChart(BaseChart_Multi pChart, int nDivCount, int row, int col) {
		if(nDivCount==1) COMUtil._neoChart = pChart;

//		if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//	    {
		if(m_nExtendChart<0 && nDivCount<=1)
			pChart.showChartItem(true);
		else
			pChart.showChartItem(true);
//	    }

		int ih = (int)COMUtil.getPixel(COMUtil.mainTopMargin);

		//2011.08.05 by LYH >> 분할 시 차트 사이즈 개선
//    	int nGap = 8;
//    	int nTotWidth = frame.width()-nGap;
//    	int nTotHeight = frame.height()-ih-nGap - 53;
//    	if(m_bIsShowToolBar) {
//    		nTotWidth -= 51;
//    	}
		int chartWidth = frame.width();
		int chartHeight = frame.height();
//    	System.out.println("resizeBaseChart width:"+chartWidth+" height:"+chartHeight);
		int nGap = 2;
		int nTotWidth = chartWidth-nGap;
		int nTotHeight = chartHeight-ih-nGap - _PAD_TICKER_HEIGHT;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			nGap = 8;
			nTotWidth = chartWidth-nGap;
			//2012.10.24 by LYH >> 차트 여백 조정.
			if (COMUtil.apiMode) {
				if(nDivCount<=1)
					nGap = 0;
			}
			//2012.10.24 by LYH <<
			if(m_bIsShowToolBar) {
				nTotWidth -= (int)COMUtil.getPixel(51);
			}
			nTotHeight = chartHeight-ih-nGap-_PAD_TICKER_HEIGHT;
		} else {
//    		if(COMUtil.apiMode) {
//    			if(nDivCount == 1) 
//    				nGap = 0;
//    			if(compareChart != null)
//    				nGap = (int)COMUtil.getPixel(3);
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(45);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(0);
//		    		}
//	    		}
//    		} else {
//	    		if(m_bIsAnalToolBar) {
//		    		if(m_bIsShowToolBar) {
//		    			nTotWidth -= (int)COMUtil.getPixel(48);
//		    		} else {
//		    			nTotWidth -= (int)COMUtil.getPixel(6);
//		    		}
//	    		}
//    		}
			nTotHeight = chartHeight-ih-(int)COMUtil.getPixel(_PHONE_TICKER_HEIGHT);
		}
		//2011.08.05 by LYH <<    	

		int nColumnNum = m_nColumnNum;
		int nRowNum = m_nRowNum;
		if((m_nExtendChart >=0 && nDivCount==1)|| pChart.equals(compareChart)) {
			nColumnNum = 1;
			nRowNum = 1;
		}

		int width, height, left, top;
		left = col * (nTotWidth/nColumnNum) + nGap;
		top = ih + row * (nTotHeight/nRowNum) + nGap;

		if((nDivideType == 31 && nDivCount == 3 && row == 1) || (nDivideType == 35 && nDivCount == 3 && row == 0)
				|| (nDivideType == 43 && row == 0) || (nDivideType == 45 && row == 1)) {
			width = nTotWidth - nGap;
		} else {
			width = (nTotWidth/nColumnNum) - nGap;
		}

		if((nDivideType == 34 && nDivCount == 3 && col == 0)||(nDivideType == 36 && nDivCount == 3 && col == 1)) {
			height = nTotHeight - nGap;
		} else {
			height = (nTotHeight/nRowNum) - nGap;
		}

//    	RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//				width, height);
//    	params.leftMargin=left;
//    	params.topMargin=top;

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)pChart.getLayoutParams();
		param.leftMargin = left;
		param.topMargin = top;
//    	params.leftMargin=0;
//    	params.topMargin=0;
//    	pChart.setBounds2(params.leftMargin, params.topMargin, params.width, params.height);
		pChart.isChangeBlock = isChangeBlock;
		pChart.setBounds2(0, 0, width, height);
		pChart.isChangeBlock = false;
	}

	public void resetChartDivision(int nDivCount) {
		if(chartList != null) {
			if(chartList.size() > nDivCount) {
				if(chartList.size() > nDivCount) {
					BaseChart pBaseChart;

					//2011.09.29 by LYH >> 1*1로 복원할 경우 선택된 차트로 복원
					if(nDivCount == 1)
					{
						for(int i=chartList.size()-1; i>=0; i--)
						{
							pBaseChart = chartList.get(i);
							if(_chart != pBaseChart)
							{
								pBaseChart.destroyDrawingCache();
								pBaseChart.deregRT();
								pBaseChart.removeAllBlocks();
								pBaseChart.destroy();
								this.layout.removeView(pBaseChart);
								pBaseChart=null;
								chartList.remove(i);
							}
						}
					}
					else
					//2011.09.29 by LYH <<
					{
						for(int i=chartList.size()-1; i>=nDivCount; i--) {
							pBaseChart = chartList.get(i);
							pBaseChart.destroyDrawingCache();
							pBaseChart.deregRT();
							pBaseChart.removeAllBlocks();
							pBaseChart.destroy();
							this.layout.removeView(pBaseChart);
							pBaseChart=null;
							chartList.remove(i);

						}
					}
				}
			}
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				//if(i==nDivCount-1) {
				if(i==0){
					pBaseChart.setSelected(nDivCount > 1);
					_chart = pBaseChart;
					COMUtil._neoChart = _chart;
					//_chart = chart;
				} else {
					pBaseChart.setSelected(false);
				}
				resizeBaseChart(pBaseChart, nDivCount, getRowIndex(i), getColIndex(i));
			}
		}
	}
	public BaseChart_Multi showCompareChart(boolean bShow) {
		if(bShow) {
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				pBaseChart.setVisibility(View.GONE);
				if(pBaseChart.equals(_chart)) {
					m_nSelIndex = i;
				}
			}
			int col = 0;
			int row = 0;
			int nColumnNum = 1;
			int nRowNum = 1;
			int width = frame.width();
			int height = frame.height();
			int ih = (int)COMUtil.getPixel(COMUtil.mainTopMargin);
			int nGap = 0;
			int nTotWidth = width-nGap;
			int nTotHeight = height-ih-nGap;

			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
				nGap = 8;
				//2012.10.24 by LYH >> 차트 여백 조정.
				//nTotWidth = width-nGap;
				if (m_bIsAnalToolBar)
					nTotWidth = width-nGap;
				else
					nTotWidth = width;

				if(COMUtil.apiMode) {
					if(row==0 && col==0)
					{
						nGap = 0;
					}
				}
				//2012.10.24 by LYH <<

				if(m_bIsShowToolBar) {
					nTotWidth -= (int)COMUtil.getPixel(51);
				}
				nTotHeight = height-ih-nGap-_PAD_TICKER_HEIGHT;

			} else {
//        		if(COMUtil.apiMode) {
////        			if(row==0 && col==0)
////        				nGap = 0;
////    				if(nDivideType > 1)
////    				{
////    					nGap = 2;
////    				}
//        			nGap = (int)COMUtil.getPixel(3);
//    	    		if(m_bIsAnalToolBar) {
//    		    		if(m_bIsShowToolBar) {
//    		    			nTotWidth -= (int)COMUtil.getPixel(45);
//    		    		} else {
//    		    			nTotWidth -= (int)COMUtil.getPixel(0);
//    		    		}
//    	    		}
//        		} else {
//    	    		if(m_bIsAnalToolBar) {
//    		    		if(m_bIsShowToolBar) {
//    		    			nTotWidth -= (int)COMUtil.getPixel(48);
//    		    		} else {
//    		    			nTotWidth -= (int)COMUtil.getPixel(6);
//    		    		}
//    	    		}
//        		}
				nTotHeight = height-ih-(int)COMUtil.getPixel(_PHONE_TICKER_HEIGHT);
			}

			if(compareChart == null) {
				RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
						nTotWidth-nGap, (nTotHeight/nRowNum)-nGap);
				params.leftMargin=col * (nTotWidth/nColumnNum) + nGap;
				params.topMargin=ih + row * (nTotWidth/nRowNum) + nGap;

				compareChart = new BaseChart_Multi(this.context, this.layout);
				compareChart.setLayoutParams(params);
				compareChart.setParent(this);
				compareChart.init(data_info_DWM, 14);
				compareChart._cvm.chartType=COMUtil.COMPARE_CHART;

				//해상도 크기로 리스트뷰 크기를 결정하기 위함 
				Display dis = ((WindowManager) COMUtil.apiView.getContext().getSystemService(COMUtil.apiView.getContext().WINDOW_SERVICE)).getDefaultDisplay();
				int mDisWidth = dis.getWidth();            // 가로 사이즈 
				int mDisHeight = dis.getHeight();          // 세로 사이즈
				int size = 30;
				if((mDisWidth >= 720 && mDisHeight >= 1232) || (mDisWidth >= 1232 && mDisHeight >= 720))
				{
					size = 30;
				}
				//2013.	04.05  >> 고해상도 처리
//    	    	compareChart._cvm.XSCALE_H=size;
				compareChart._cvm.XSCALE_H=(int)COMUtil.getPixel(size);
				//2013.04.05 <<
				compareChart.setBasicUI();
				compareChart.addChartChangedListener(this);
				//compareChart.onResume();
				this.layout.addView(compareChart);
				compareChart.setBounds(0, 0, params.width, params.height);
			} else {
				compareChart.setVisibility(View.VISIBLE);
				//compareChart.onResume();
			}
			compareChart._cvm.preViewNum=compareChart._cvm.getViewNum();
			compareChart._cvm.setOnePage(1);
			//showChartItemView(true, compareChart);

			return compareChart;
		} else {
			compareChart.setVisibility(View.GONE);
			//showChartItemView(false, compareChart);
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				//pBaseChart.onResume();
				if(m_nSelIndex == i) {
					_chart = pBaseChart;
					COMUtil._neoChart = _chart;
					//_chart = chart;
					_chart.selectChart();
				}

				if(m_nExtendChart < 0 || m_nExtendChart == i) {
					pBaseChart.setVisibility(View.VISIBLE);

				}
			}
			return null;
		}
	}
	public void sendTrCodes() {
		if(compareChart!=null && (bSendCompareData || COMUtil.bSendCompareData)) {
			m_nCompareRotateIndex++;
			String strCode = compareChart.getCode(m_nCompareRotateIndex);
			String strName = compareChart.getName(m_nCompareRotateIndex);
			String strMarket = compareChart.getMarket(m_nCompareRotateIndex);
			if(strCode == null || strName == null || strMarket == null || strCode.length() == 0 ) {
				m_nCompareRotateIndex = -1;
//				bSendCompareData = false;
//				COMUtil.bSendCompareData = bSendCompareData;
				return;
			}

			this.mainFrame.symbol = strCode;
			COMUtil.symbol = strCode;
			COMUtil.codeName = strName;
			COMUtil.market = strMarket;
			if(strCode.length()==8) {
				sendTR(COMUtil.TR_COMPARE_FUTURE);
			} else if(strCode.length()==4) {
				sendTR(COMUtil.TR_COMPARE_UPJONG);
			} else {
				sendTR(COMUtil.TR_COMPARE_STOCK);
			}
		}
	}

	/*
     * TR조회성 마켓데이터 처리가 완료된 후 다음 분할차트의 TR을 호출하기 위해서 호출됨
     */
	public void sendIndicatorTrCodes() {
		if((m_bSyncIndicator || m_bSyncJongMok || m_bSyncJugi) && !isRotation) {
			if(m_nRotateIndex == -1 && !m_bDWMMClick) {
				for(int i=0; i<chartList.size(); i++) {
					BaseChart pBaseChart = chartList.get(i);
					if(pBaseChart.equals(_chart)) {
						m_nSelIndex = i;
						break;
					}
				}
			}
			m_nRotateIndex++;
			if(m_nSelIndex == m_nRotateIndex) {
				m_nRotateIndex++;
			}
			if(m_nRotateIndex >= chartList.size()) {
				if(m_nSelIndex != (m_nRotateIndex-1) || m_nSelIndex == (chartList.size()-1)) {
					BaseChart pChart = chartList.get(m_nSelIndex);
					_chart = pChart;
//	    	        for(int i=0; i<chartList.size(); i++)
//	    	        {
//	    	        	pChart = (BaseChart)chartList.get(i);
//	    	            if(m_nTotNum != 1 && i == m_nSelIndex)
//	    	            {
//	    	            	pChart.selected=true;
//	    	            	pChart.repaintAll();
//	    	            }
//	    	            else {
//	    	            	if(pChart.selected==true) {
//	    	            		pChart.selected=false;
//	    	            		pChart.repaintAll();
//	    	            	}
//	    	            }
//	    	        }
					COMUtil._neoChart = _chart;
					//_chart = chart;

					_chart.selectChart();
				}

				m_nRotateIndex = -1;
				m_bDWMMClick = false;
				m_bConnectSocket = false;
				m_bSyncIndicator = false;

				isInit = false;

				return;
			}

			BaseChart pBaseChart = chartList.get(m_nRotateIndex);
			if(!m_bSyncJongMok) {
				if(pBaseChart._cdm.codeItem.strCode != null && pBaseChart._cdm.codeItem.strCode.length()>0)
				{
					COMUtil.symbol = pBaseChart._cdm.codeItem.strCode;
					COMUtil.market = pBaseChart._cdm.codeItem.strMarket;
					COMUtil.codeName = pBaseChart._cdm.codeItem.strName;
					COMUtil.lcode = pBaseChart._cdm.codeItem.strRealKey;
					if(COMUtil.lcode==null) COMUtil.lcode = "S31";
					String realKey = pBaseChart._cdm.codeItem.strRealKey;
					if(realKey==null) realKey = "S31";
					if(realKey.equals("SC0")) {
						COMUtil.apCode = COMUtil.TR_CHART_FUTURE;
//		    		} else if(realKey.equals("JS0")) {
//		    			COMUtil.apCode = COMUtil.TR_CHART_UPJONG;
					} else {
						COMUtil.apCode = COMUtil.TR_CHART_STOCK;
					}
				}
			}

			if(!m_bSyncJugi) {
				if(pBaseChart._cdm.codeItem.strCode != null && pBaseChart._cdm.codeItem.strCode.length()>0)
				{
					COMUtil.dataTypeName = pBaseChart._cdm.codeItem.strDataType;
					COMUtil.unit = pBaseChart._cdm.codeItem.strUnit;
					if(COMUtil.unit==null) COMUtil.unit = "1";
				}
			}
//	    	
//	    	if(m_bDWMMClick)
//	    	{
//	    	    switch (m_nRotateIndex) {
//	    	        case 0:
//	    	        	COMUtil.dataTypeName = "2";
//	    	            break;
//	    	        case 1:
//	    	        	COMUtil.dataTypeName = "3";
//		                break;
//	 	            case 2:
//	 	            	COMUtil.dataTypeName = "4";
//	   	               break;
//	                case 3:
//	                	COMUtil.dataTypeName = "1";
//	   	                break;
//	            }
//	    	    if(m_bConnectSocket && m_nRotateIndex == 3)
//	    	    	COMUtil.unit = "10";
//	    	    else
//	    	    	COMUtil.unit = "1";
//	   	    }
//	    	
//	    	//증권그룹ID
//	    	// 0 >> 전체
//	    	// 1 >> 주식
//	    	// 2 >> ELW
//	    	// 3 >> 상장지수펀드(ETF)
//	    	// 4 >> 거래원
//	    	// 5 >> 선물
//	    	// 6 >> 옵션
//	    	if( ( 	(COMUtil.market.equals("2")) ||  //elw
//	    			(COMUtil.market.equals("5")) ||  //선물
//	    			(COMUtil.market.equals("6")) )   //옵션
//	    			&&  (COMUtil.dataTypeName.equals("5")) )  //콜     //년 
//			{
//				if(m_bSyncJongMok)
//				{
//					COMUtil.dataTypeName = "2";
//				}
//				else
//				{
//					return;
//				}
//			}
			if(COMUtil.apiMode) {
				this.sendTR("storageType");
			} else {
				COMUtil.sendTR(COMUtil.apCode, _chart.userProtocol);
			}
		}
	}

	/*
     * 최종상태가 복원되는 경우 분할1개의 TR조회성 마켓지표가 처리된 후 호출되는 함수(다음 분할차트의 TR을 요청한다)
     */
	public void sendEventOfLastMarketData()
	{
		//storage rotate tr 처리
//        [self.delegate sendTR:[NSString stringWithFormat:@"%d", _TAG_STORAGE_TYPE]];

		if (m_bSyncJongMok || m_bSyncJugi) {
			sendRotateTR();
		} else {
			if(m_nRotateIndex>=0)
				sendRotateTR_storage();
			isSendMarket = false;
		}

	}

	public int getRowIndex(int nIndex) {
		int nRow = nIndex/m_nColumnNum;
		int nCol = nIndex%m_nColumnNum;
		if(nDivideType == 43 || nDivideType == 35) {
			if(nRow == 0 && nCol > 0) {
				return 1;
			}
		}
		return nRow;
	}
	public int getColIndex(int nIndex)
	{
		int nRow = nIndex/m_nColumnNum;
		int nCol = nIndex%m_nColumnNum;
		if(nDivideType == 34)
		{
			if(nRow == 1 && nCol == 0)
				return 1;
		}
		else if(nDivideType == 43)
		{
			if(nRow == 0 && nCol > 0)
				return (nCol - 1);
			else if(nRow == 1)
			{
				return 2;
			}
		}
		else if(nDivideType == 35)
		{
			if(nIndex > 0)
				nCol = nIndex-1;
		}
		return nCol;
	}

	// API 차트크기변경
	public void resizeChart(View view) {
		frame.right = COMUtil.chartWidth;
		frame.bottom = COMUtil.chartHeight;
		if (_chart != null) {
			Configuration config = getResources().getConfiguration();
			int nMode = 0;
			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
				nMode = 1;
			else
				nMode = 2;
			for (int i = 0; i < chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				//2016. 10. 20 가로/세로 변경시 봉개수 고정 시킴. by pjm
//				pBaseChart.checkOrientation(nMode);
			}
		}

		this.resizeChart();

		// 분석툴바 위치 조정
		if (m_bIsAnalToolBar) {
			// chart frame
			int width = 0;
			int height = 0;
			int topMargin = 0;

			width = frame.width();
			height = frame.height();

			// analToolbarLayout frame
			if (analToolbarLayout == null)
				return;

			analToolbarLayout.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams analToolbarLayoutParam = (RelativeLayout.LayoutParams) analToolbarLayout
					.getLayoutParams();

			// analToolbarCloseLayout frame
			// RelativeLayout.LayoutParams analToolbarCloseLayoutParam =
			// (RelativeLayout.LayoutParams)analToolbarCloseLayout.getLayoutParams();

			// RelativeLayout.LayoutParams toolbarBackParam =
			// (RelativeLayout.LayoutParams)toolbarBack.getLayoutParams();

			// 2012. 7. 27 여닫이버튼 크기 이상하게 조절되는 문제 때문에 주석처리
			// RelativeLayout.LayoutParams toolbarBackBtnParam =
			// (RelativeLayout.LayoutParams)toolbarBackBtn.getLayoutParams();
			// 2012. 8. 17 분석툴바 여닫이 버튼 태블릿용 적용 : T_tab21
			// if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
			// {
			// toolbarBackBtnParam.width = (int)COMUtil.getPixel(18);
			// toolbarBackBtnParam.height = (int)COMUtil.getPixel(130);
			// }
			// else
			// {
			// toolbarBackBtnParam.width = (int)COMUtil.getPixel(18);
			// toolbarBackBtnParam.height = (int)COMUtil.getPixel(60);
			// }

			if (m_bIsAnalToolBar) {
				if (m_bIsShowToolBar) { // 툴바메뉴 ON 모드.
					// 2012. 8. 20 분석툴바 HONEYCOMB 레이아웃 적용 : T_tab23
					if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
						analToolbarLayoutParam.leftMargin = width
								- (int) COMUtil.getPixel(58);
					} else {
						analToolbarLayoutParam.leftMargin = width
								- (int) COMUtil.getPixel(m_nToolbarWidth);
					}
					analToolbarLayoutParam.topMargin = topMargin;
					// 2012. 8. 20 분석툴바 HONEYCOMB 좌표 지정 : T_tab22
//					if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//						analToolbarLayoutParam.width = (int) COMUtil
//								.getPixel(59);
//					} else {
//						analToolbarLayoutParam.width = (int) COMUtil
//								.getPixel(55);
//					}

					analToolbarLayoutParam.height = (int) COMUtil
							.getPixel(height);

					// 2012. 8. 17 분석툴바 여닫이 버튼 태블릿용 적용 : T_tab21
					// if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
					// {
					// toolbarBackBtnParam.leftMargin=analToolbarLayoutParam.leftMargin-(int)COMUtil.getPixel(12);
					// toolbarBackBtnParam.topMargin=topMargin +
					// height/2-(int)COMUtil.getPixel(65);
					// }
					// else
					// {
					// toolbarBackBtnParam.leftMargin=analToolbarLayoutParam.leftMargin-(int)COMUtil.getPixel(8);
					// toolbarBackBtnParam.topMargin=topMargin +
					// height/2-(int)COMUtil.getPixel(65/2);
					// }
					//
					// toolbarBackParam.leftMargin =
					// toolbarBackBtnParam.leftMargin-(int)COMUtil.getPixel(20);
					// toolbarBackParam.topMargin =
					// toolbarBackBtnParam.topMargin;
					// toolbarBackParam.width = toolbarBackBtnParam.width;
					// toolbarBackParam.height = toolbarBackBtnParam.height;
					//
					// analToolbarCloseLayoutParam.leftMargin =
					// width-(int)COMUtil.getPixel(88);
					// analToolbarCloseLayoutParam.topMargin =
					// (int)COMUtil.getPixel(COMUtil.mainTopMargin) +
					// height/2-(int)COMUtil.getPixel(65/2);
				} else {
					// 2012. 8. 20 분석툴바 HONEYCOMB 좌표 지정 : T_tab22
					if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
						analToolbarLayoutParam.leftMargin = width
								- (int) COMUtil.getPixel(0);
					} else {
						analToolbarLayoutParam.leftMargin = width
								- (int) COMUtil.getPixel(0);
					}
					analToolbarLayoutParam.topMargin = topMargin;
					//analToolbarLayoutParam.width = (int) COMUtil.getPixel(48);
					analToolbarLayoutParam.height = (int) COMUtil
							.getPixel(height);

					// 2012. 8. 17 분석툴바 여닫이 버튼 태블릿용 적용 : T_tab21
					// if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
					// {
					// toolbarBackBtnParam.leftMargin=analToolbarLayoutParam.leftMargin-(int)COMUtil.getPixel(18);
					// toolbarBackBtnParam.topMargin=topMargin +
					// height/2-(int)COMUtil.getPixel(65);
					// }
					// else
					// {
					// toolbarBackBtnParam.leftMargin=analToolbarLayoutParam.leftMargin-(int)COMUtil.getPixel(8);
					// toolbarBackBtnParam.topMargin=topMargin +
					// height/2-(int)COMUtil.getPixel(65/2);
					// }
					//
					// toolbarBackParam.leftMargin =
					// toolbarBackBtnParam.leftMargin-(int)COMUtil.getPixel(20);
					// toolbarBackParam.topMargin =
					// toolbarBackBtnParam.topMargin;
					// toolbarBackParam.width = toolbarBackBtnParam.width;
					// toolbarBackParam.height = toolbarBackBtnParam.height;
					//
					// analToolbarCloseLayoutParam.leftMargin =
					// width-(int)COMUtil.getPixel(35);
					// analToolbarCloseLayoutParam.topMargin =
					// (int)COMUtil.getPixel(COMUtil.mainTopMargin) +
					// height/2-(int)COMUtil.getPixel(65/2);

				}
			}
		}

		if (!COMUtil.bIsCompare) {
			this.resetSimpleSettingButton();
		}
	}

	public void resizeChart() {
		int nDivCount = nDivideType/10;
		if(chartList != null) {
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pBaseChart = chartList.get(i);
				if(m_nExtendChart<0 || m_nExtendChart != i) {
					resizeBaseChart(pBaseChart, nDivCount, getRowIndex(i), getColIndex(i));
					pBaseChart.resetTitleBoundsAll();	//2017.07.06 by pjm 가로/세로모드 전환 시 지표타이틀 개행.
				} else {
					resizeBaseChart(pBaseChart, 1, 0, 0);
					pBaseChart.resetTitleBoundsAll();	//2017.07.06 by pjm 가로/세로모드 전환 시 지표타이틀 개행.
				}
			}
		}

		if(compareChart != null) {
			resizeBaseChart(compareChart, 1, 0, 0);
			compareChart.applySetting(true);
		}

		if (!COMUtil.bIsCompare) {
			this.resetSimpleSettingButton();
			this.setOneTouchModeParams();
		}
	}

	public boolean addCompareCode(String strCode, String strName, String strMarket) {
		if(compareChart != null) {
			return compareChart.addCode(strCode, strName, strMarket);
		}

		return false;
	}
	public boolean isMultiChart() {
		return !(m_nRowNum == 1 && m_nColumnNum == 1);
	}
	public void syncJongMok(View v) {
		CheckBox check = (CheckBox)v;

		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>
		if(11 == nDivideType && check.isChecked())		//멀티차트가 아니라 원분할차트면
		{
			check.setChecked(!check.isChecked());
			showSyncAlert();	//경고창을 띄우고 
			return;				//동기화를 수행하지 않고 함수종료 
		}
		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

		if(m_nRotateIndex >= 0) {
			m_nRotateIndex = -1;
			BaseChart pChart = chartList.get(m_nSelIndex);
			_chart = pChart;
			COMUtil._neoChart = _chart;
		}

//    	System.out.println("base11:syncJongMok:m_nRotateIndex"+m_nRotateIndex);

		this.m_bSyncJongMok = check.isChecked();
		COMUtil.isSyncJongmok = m_bSyncJongMok;

		m_bSyncIndicator = false;

		if(this.m_bSyncJongMok) {
			if(COMUtil.apiMode) {
				this.sendTR("storageType");
			} else {
				this.sendTR(COMUtil.getSendTrType());
			}
		}
	}
	public void syncJugi(View v) {
		CheckBox check = (CheckBox)v;

		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>
		if(11 == nDivideType  && check.isChecked())		//멀티차트가 아니라 원분할차트면
		{
			check.setChecked(!check.isChecked());
			showSyncAlert();	//경고창을 띄우고 
			return;				//동기화를 수행하지 않고 함수종료 
		}
		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

		if(m_nRotateIndex >= 0) {
			m_nRotateIndex = -1;
			BaseChart pChart = chartList.get(m_nSelIndex);
			_chart = pChart;
			COMUtil._neoChart = _chart;
		}

//    	System.out.println("base11:syncJugi:m_nRotateIndex"+m_nRotateIndex);

		this.m_bSyncJugi = check.isChecked();
		COMUtil.isSyncJugi = m_bSyncJugi;

		m_bSyncIndicator = false;

		if(this.m_bSyncJugi) {
			if(COMUtil.apiMode) {
				this.sendTR("storageType");
			} else {
				this.sendTR(COMUtil.getSendTrType());
			}
		}
	}
	public void syncIndicator(View view) {
		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>
		if(!isMultiChart())		//멀티차트가 아니라 원분할차트면 
		{
			showSyncAlert();	//경고창을 띄우고 
			return;				//동기화를 수행하지 않고 함수종료 
		}
		//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

		//지표별 차트 처리.
		Vector<String> list = _chart.getGraphConfigListOnlyShown();
		if(list!=null && list.size()>0) {
			COMUtil.setSendTrType("storageType");

			//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
			//Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();

			Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
			Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
			for(int i=0; i<addItems.size(); i++) {
				v.add(addItems.get(i));
			}
			//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

			Vector<Hashtable<String, String>> v2 = COMUtil.getChartListTag();
			int vLen = v.size();
			int listLen = list.size();

			for(int nIdx=0; nIdx<chartList.size(); nIdx++)
			{
				BaseChart pChart = chartList.get(nIdx);

				if(!pChart.equals(_chart)) {

					pChart.removeAllBlocks();
					pChart.setEmptyUI();
					pChart.reSetUI(false);
					pChart._cvm.setOnePage(0);
					pChart._cvm.setStandGraph(false);

					for(int k=0; k<listLen; k++) {
						//지표설정항목에서 이름과 값을 분리한다.
						String loadJipyo = list.get(k);
						String graphName = "";
						int[] graphValues = null;
						int index = loadJipyo.indexOf("{");
						if(index<1) {
							graphName = loadJipyo;
						} else {
							graphName = loadJipyo.substring(0, index);
							String graphValue = loadJipyo.substring(index);
							graphValue = COMUtil.removeString(graphValue, "{");
							graphValue = COMUtil.removeString(graphValue, "}");
							if(!graphValue.equals("")) {
								String[] strValues = graphValue.split("=");
								graphValues = new int[strValues.length];
								for(int m=0; m<strValues.length; m++) {
									try
									{
										graphValues[m] = Integer.parseInt(strValues[m]);
									}catch(NumberFormatException e){}
								}
							}
						}

						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
						boolean bIsMergedBlock = false;

						if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
							bIsMergedBlock = true;
						}
						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

						//라인/바/봉 형식 저장 불러오기.
						//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
						if (graphName.equals("일본식봉") || graphName.equals("Heikin-Ashi")
						|| (bIsMergedBlock && (graphName.startsWith("일본식봉") || graphName.startsWith("Heikin-Ashi") || graphName.startsWith("투명캔들")))
						) {
							if(graphValues.length >= 2) {
								if(graphValues[0]==1 && graphValues[1]==0) {
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_LINE;
								} else if(graphValues[0]==0 && graphValues[1]==1) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR;
                                //2015.04.30 by lyk - 바(시고저종) 유형 추가
                                } else if(graphValues[0]==0 && graphValues[1]==2) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR_OHLC;
								//2015.04.30 by lyk - 바(시고저종) 유형 추가 end
                                } else if(graphValues[0]==0 && graphValues[1]==3) {//영역라인
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_RANGE_LINE;
								} else if(graphValues[0]==0 && graphValues[1]==4) {//플로우
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_FLOW;
								} else if(graphValues[0]==0 && graphValues[1]==5) { //Heikin
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_HEIKIN_ASHI;
								//2020.07.06 by LYH >> 캔들볼륨 >>
								} else if(graphValues[0]==0 && graphValues[1]==6) { //Candle Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_VOLUME;
								} else if(graphValues[0]==0 && graphValues[1]==7) { //Equi Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_EQUI_VOLUME;
								//2020.07.06 by LYH >> 캔들볼륨 <<
								} else if(graphValues[0]==0 && graphValues[1]==8) { //계단
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_STAIR;
								} else if(graphValues[0]==0 && graphValues[1]==9) { //투명캔들
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY;
								} else {
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE;
								}
								pChart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk
								pChart.changeBlock(pChart.m_strCandleType);

								//2019. 01. 12 by hyh - 블록병합 처리. bIsMergedBlock 추가
								if(graphValues.length >= 20 || bIsMergedBlock) pChart.applyGraphConfigValue(graphName, graphValues);
							}
							break;
						}
					}
					vLen = v.size();
					for(int i=0; i<vLen; i++) {
						Hashtable<String, String> item = v.get(i);
						String cmp = item.get("name");
						String type = item.get("type");
						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = list.get(k);
							String graphName = "";
							int[] graphValues = null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m=0; m<strValues.length; m++) {
										try
										{
											graphValues[m] = Integer.parseInt(strValues[m]);
										}catch(NumberFormatException e){}
									}
								}
							}

							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
							boolean bIsMergedBlock = false;
							String strMergeBlockName = "";

							if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
								bIsMergedBlock = true;

								String[] arrGraphName = graphName.split(NeoChart2.MERGED_GRAPH_SEPARATOR);
								strMergeBlockName = arrGraphName[0];
							}
							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

							//2015. 1. 13 by lyk -null체크
							if (cmp == null || graphName == null) {
								continue;
							}

							//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
							if (cmp.equals(graphName)
							|| (bIsMergedBlock && strMergeBlockName.equals(cmp))) {
								if(type.equals("Indicator")) {
									pChart.addBlock_Storage(cmp);
									if(graphValues!=null) pChart.applyGraphConfigValue(graphName, graphValues);
								} else if(type.equals("Trend") || type.equals("strategy")) {	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
									pChart.addGraph_Storage(cmp);
									if(graphValues!=null) pChart.applyGraphConfigValue(graphName, graphValues);
								}
							}
						}
					}
					//Independence차트 체크.
					vLen = v2.size();
					for(int m=0; m<vLen; m++) {
						Hashtable<String, String> item = v2.get(m);
						String cmp = item.get("name");
						String type = item.get("type");

						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = list.get(k);
							if(loadJipyo.equals("")) {
//			    				System.out.println("aa");
								continue;
							}
							String graphName = "";
							int[] graphValues=null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m2=0; m2<strValues.length; m2++) {
										try
										{
											graphValues[m2] = Integer.parseInt(strValues[m2]);
										}catch(NumberFormatException e){}
									}
								}
							}
							if(cmp.equals(graphName)) {
								Vector<String> indiItem = new Vector<String>();
								indiItem.add(cmp);
								indiItem.add(type);
								pChart._cvm.preSelectLabel = cmp;
								pChart._cvm.preMenuItem = indiItem;
								pChart._cvm.indicatorItem = indiItem;
								if(type.equals("Basic")) {
									pChart.changeBlock(cmp);
								} else if(type.equals("Independence")) {
									pChart.addStandBlock(cmp);
								}
							}
						}
					}
				}
				pChart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.

				pChart.makeGraphData();
				pChart.repaintAll();

				//마켓지표 체크하여 처리
//                Vector<Block> cBlocks = pChart.getBlocks();
//                System.out.println("indicatorSync_cBlocks:"+cBlocks.size());
//	        	if(cBlocks != null) {
//	        		for (int i=0; i<cBlocks.size(); i++) {
//	        			Block block = cBlocks.get(i);
//	        			if(pChart.isMarketIndicator(block.getTitle())) {
//	        				pChart.nSendMarketIndex = i;
//	        				System.out.println("indicatorSync_getTitle:"+block.getTitle());
//	        				COMUtil.sendTR(block.getTitle());
//	        				break;
//	        			}
//	        		}
//	        	}
			}
			COMUtil.setSendTrType("");
		}

		m_nRotateIndex = -1;
//		BaseChart pChart = chartList.get(m_nSelIndex);
//		_chart = pChart;

		//2013. 7. 26 지표동기화시 분할차트 번호가 -1 로 오는경우 예외처리 >>

//		COMUtil._neoChart = _chart;

		m_bSyncIndicator = true;

		this.sendTR("storageType");

		_chart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.

	}
	public void extendChart(NeoChart2 chart) {
		if(m_nTotNum == 1)
		{
			((BaseChart)chart).setExtendButton();
			return;
		}
		COMUtil._neoChart = chart;
		_chart = (BaseChart)chart;
		selectChart(chart);

		chart.selectBaseChart();
		chart.selectChart();

		for(int i=0; i<chartList.size(); i++) {
			BaseChart pChart = chartList.get(i);

			if(pChart.equals(chart)) {
				this.m_nExtendChart = i;
				pChart.setSelected(true); //2019. 10. 09 by hyh - 일주월분 > 차트확대 > 새종목조회 > 일주월분해제 시 에러 수정. FALSE -> TRUE
				this.resizeBaseChart(pChart, 1, 0, 0);
			} else {
				pChart.setSelected(false);
				pChart.setVisible(false);
				pChart.setVisibleBlockDelButton(false);//차트 확대시 delButton 숨김 (2011.11.02 by lyk)
			}
			//삭제/전체삭제 바가 전체Layout에 추가되어있기 때문에, 각각의 NeoChart의 분석툴 선택 상태를 해제함.(2011.11.10 by lyk)
			pChart.reSetAnalTool(false);
//    		pChart.repaintAll();
		}
	}
	public void reduceChart(NeoChart2 chart) {
		synchronized(this) {
			this.m_nExtendChart = -1;
			for(int i=0; i<chartList.size(); i++) {
				BaseChart pChart = chartList.get(i);
				if(pChart.equals(chart)) {
					pChart.setSelected(true);
					this.resizeBaseChart(pChart, m_nTotNum, this.getRowIndex(i), this.getColIndex(i));
				} else {
					pChart.setVisible(true);
					pChart.setVisibleBlockDelButton(true);//차트 확대시 delButton 보임 (2011.11.02 by lyk)
				}
			}
		}
	}
	public ChartItemView chartItem = null;
	public void showChartItemView(boolean b, NeoChart2 neochart) {
		RelativeLayout rl=null;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)neochart.chart_bounds.width(),(int)COMUtil.getPixel(200));
		params.leftMargin=(int)COMUtil.getPixel(10);
		params.topMargin=(int)COMUtil.getPixel(COMUtil.mainTopMargin);
		if(b) {
			rl = new RelativeLayout(this.context);
			rl.setLayoutParams(params);
			rl.setTag("chartItemLayout");
			chartItem = new ChartItemView(this.context, rl);
			chartItem.initCompareUI();
			chartItem.setBaseChart(neochart);
			rl.addView(chartItem);
			layout.addView(rl);
		} else {
			RelativeLayout cll = (RelativeLayout)this.layout.findViewWithTag("chartItemLayout");
			layout.removeView(cll);
		}
	}
	public void setToolbarState(int state) {
		for (int i = 0; i < chartList.size(); i++) {
			BaseChart pBaseChart = chartList.get(i);
			pBaseChart._cvm.setToolbarState(state);
		}
	}
	public void setChartToolBar(int id) {
		// 2011.10.07 by LYH >> 지우개는 현재 선택된 차트만 적용.
		if (id == COMUtil.TOOLBAR_CONFIG_ERASE
				|| id == COMUtil.TOOLBAR_CONFIG_ALL_ERASE
				|| id == COMUtil.TOOLBAR_CONFIG_TEXT) {
			_chart.setChartToolBar(id);
			return;
		}
		if (id == COMUtil.MENU_STYLE_BLACK) {
			setSkinType(COMUtil.SKIN_BLACK);
			return;
		}
		if (id == COMUtil.MENU_STYLE_WHITE) {
			setSkinType(COMUtil.SKIN_WHITE);
			return;
		}
		if (id == COMUtil.TOOLBAR_CONFIG_DWMM) {
			nDivideType = 41;
			COMUtil.divideType = nDivideType;
//			if (inputPanel != null)
//				inputPanel.showSyncButton(true);

			//2015. 1. 19 일주월분 버튼 토글로 변경 >>
//			m_bDWMMClick = true;
			m_bDWMMClick = !m_bDWMMClick;

			if(m_bDWMMClick)
			{
				//일주월분 작동

				setDivisionChart(4);
				m_nSelIndex = 0;
				m_nRotateIndex = -1;

				BaseChart pChart = chartList.get(chartList.size() - 1);
				pChart.setSelected(false);

				pChart = chartList.get(0);
				pChart.setSelected(true);

				_chart = pChart;
				COMUtil._neoChart = _chart;

				// 동기화 처리.
				m_bSyncJongMok = true;
				COMUtil.isSyncJongmok = true;

				// 동기화 처리.
				m_bSyncJugi = false;
				COMUtil.isSyncJugi = false;
//				if (inputPanel != null)
//					inputPanel.setSyncButton(m_bSyncJongMok, m_bSyncJugi);
			}
			else
			{
				//일주월분이었을 때는 원분할로 돌아온다 
				nDivideType = 11;		//분할상태 변수 원분할로 설정 
				setDivisionChart(1);	//분할상태 설정
				hideToolBar();			//툴바숨기기 (일주월분될때는 4분할차트 회전되면서 selectChart 탔을 때 닫힌다.)
			}

			COMUtil.dataTypeName = "2";
			COMUtil.unit = "1";

			if (COMUtil.apiMode) {
				this.sendTR("storageType");
			} else {
				this.sendTR(COMUtil.getSendTrType());
			}
			return;
			//2015. 1. 19 일주월분 버튼 토글로 변경 <<
		}

		for (int i = 0; i < chartList.size(); i++) {
			BaseChart pBaseChart = chartList.get(i);
			pBaseChart.setChartToolBar(id);
		}
	}

	//저장된 차트 항목을 로딩하여, 차트를 구성한다.
	public void setStorageState() {
		synchronized (this)    {
			//신호 실시간 해제.
			if(divideStorageType==true) {
				//2015.04.07 by lyk - 주기별 차트 설정
				if(!COMUtil.isPeriodConfigSave()) {
					//2015.04.07 by lyk - 주기별 차트 설정 end
					storageDivideIndex++;
				}
			}

			if(COMUtil.chartMode==COMUtil.DIVIDE_CHART && divideStorageType==true) {
				//Log.d("storage", ""+storageDivideIndex);
				if(storageDivideIndex==-1) return;
				//지표데이터 

				Vector graphLists = (Vector)COMUtil.loadItem.get("graphLists");
				Vector analInfos = (Vector)COMUtil.loadItem.get("analInfos");
//	    		Vector dataTypeNames = (Vector)COMUtil.loadItem.get("dataTypeNames");
				//Vector<BaseChart> chartList = COMUtil.divideChartList;
				if(chartList==null || (chartList!=null && chartList.size()<=storageDivideIndex)) return;
				BaseChart pBaseChart = chartList.get(storageDivideIndex);

				//2015.04.07 by lyk - 주기별 차트 설정
				if(!COMUtil.isPeriodConfigSave())
				{
					//2015.04.07 by lyk - 주기별 차트 설정 end
					if(graphLists!=null && pBaseChart!=null) {
						if(storageDivideIndex>=graphLists.size())
							return;

						Vector<String> graphList = (Vector<String>)graphLists.get(storageDivideIndex);
						//지표별 차트처리.
						//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
//		        			Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
						Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
						Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
						for(int i=0; i<addItems.size(); i++) {
							v.add(addItems.get(i));
						}
						//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

						Vector<Hashtable<String, String>> v2 = COMUtil.getChartListTag();
						int vLen = v.size();
						if(vLen>0)
						{
							pBaseChart.removeAllBlocks();
							pBaseChart.setEmptyUI();
							pBaseChart.reSetUI(false);
						}
                        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                        Vector sizeInfo = (Vector)COMUtil.loadItem.get("chartSizeInfo");
                        if(sizeInfo != null && sizeInfo.size()>storageDivideIndex)
                        {
                            String strSizeInfo = (String)sizeInfo.get(storageDivideIndex);
                            pBaseChart.m_strSizeInfo = strSizeInfo;
                        }
                        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

						int listLen = graphList.size();
						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = graphList.get(k);
							if(loadJipyo.equals("")) continue;
							String graphName = "";
							int[] graphValues=null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m=0; m<strValues.length; m++) {
                                        //2016.12.02 by LYH >> HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                        if(strValues[m].contains("."))
                                        {
                                            graphValues[m] = (int)Double.parseDouble(strValues[m]);
                                        }
                                        //2016.12.02 by LYH << HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                        else
										    graphValues[m] = Integer.parseInt(strValues[m]);
									}
								}
							}

							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
							boolean bIsMergedBlock = false;
							String strMergeBlockName = "";

							if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
								bIsMergedBlock = true;

								String[] arrGraphName = graphName.split(NeoChart2.MERGED_GRAPH_SEPARATOR);
								strMergeBlockName = arrGraphName[0];
							}
							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

							//라인/바/봉 형식 저장 불러오기.
							//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
							if (graphName.equals("일본식봉") || graphName.equals("Heikin-Ashi")
							|| (bIsMergedBlock && (graphName.startsWith("일본식봉") || graphName.startsWith("Heikin-Ashi") || graphName.startsWith("투명캔들")))
							) {
								if(graphValues.length >= 2) {
                                    if(graphValues[0]==1 && graphValues[1]==0) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_LINE;
                                    } else if(graphValues[0]==0 && graphValues[1]==1) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR;
                                        //2015.04.30 by lyk - 바(시고저종) 유형 추가
                                    } else if(graphValues[0]==0 && graphValues[1]==2) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR_OHLC;
                                        //2015.04.30 by lyk - 바(시고저종) 유형 추가 end
                                    } else if(graphValues[0]==0 && graphValues[1]==3) {//영역라인
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_RANGE_LINE;
                                    } else if(graphValues[0]==0 && graphValues[1]==4) {//플로우
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_FLOW;
									} else if(graphValues[0]==0 && graphValues[1]==5) { //Heikin
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_HEIKIN_ASHI;
									//2020.07.06 by LYH >> 캔들볼륨 >>
									} else if(graphValues[0]==0 && graphValues[1]==6) { //Candle Volume
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_VOLUME;
									} else if(graphValues[0]==0 && graphValues[1]==7) { //Equi Volume
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_EQUI_VOLUME;
									//2020.07.06 by LYH >> 캔들볼륨 <<
									} else if(graphValues[0]==0 && graphValues[1]==8) { //계단
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_STAIR;
									} else if(graphValues[0]==0 && graphValues[1]==9) { //투명캔들
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY;
									} else {
                                        COMUtil.selectedJipyo=null;
                                    }
									pBaseChart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk

									//2019. 01. 12 by hyh - 블록병합 처리. bIsMergedBlock 추가
									if(graphValues.length >= 20 || bIsMergedBlock) pBaseChart.applyGraphConfigValue(graphName, graphValues);
								}
							}

							//2013. 2. 8 체크안된 상세설정 오픈 : I114
							for(int i=0; i<vLen; i++) {
								Hashtable<String, String> item = v.get(i);
								String cmp = item.get("name");
								String type = item.get("type");

								//2015. 1. 13 by lyk -null체크
								if (cmp == null || graphName == null) {
									continue;
								}

								//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
								if (cmp.equals(graphName)
								|| (bIsMergedBlock && bIsMergedBlock && strMergeBlockName.equals(cmp))) {
									String checked = String.valueOf(graphValues[graphValues.length-1]);

									//활성화되지 않은 지표
									if(checked.equals("0"))
									{
										Block block = new Block(_chart, pBaseChart._cvm, pBaseChart._cdm, 0, 0, graphName);
										AbstractGraph nGraph = block.createGraph(graphName, pBaseChart._cvm, pBaseChart._cdm, Block.STAND_BLOCK);
										nGraph.changeControlValue(graphValues);
										pBaseChart.addUnChkGraph(nGraph);
										if(graphName.equals("일목균형표"))
											_chart._cvm.futureMargin = 0;
									}
									else
									{
										if(type.equals("Indicator"))
										{
											pBaseChart.addBlock_Storage(cmp);
											if(graphValues != null)
											{
												pBaseChart.applyGraphConfigValue(graphName, graphValues);
											}
										}
										else if(type.equals("Trend") || type.equals("strategy"))	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
										{
											pBaseChart.addGraph_Storage(cmp);
											{
												if(graphValues != null)
												{
													pBaseChart.applyGraphConfigValue(graphName, graphValues);
												}
											}
										}
									}
									//	        	    				if(type.equals("Indicator")) {
									//	        	    					pBaseChart.addBlock_Storage(cmp);
									//	        	    					if(graphValues!=null) pBaseChart.applyGraphConfigValue(graphName, graphValues);
									//	        	    				} else if(type.equals("Trend")) {
									//	        	    					pBaseChart.addGraph_Storage(cmp);
									//	        	    					if(graphValues!=null) pBaseChart.applyGraphConfigValue(graphName, graphValues);
									//	        	    				}
								}
							}
						}
//		        			}

						//Independence차트 체크.
						vLen = v2.size();
						for(int i=0; i<vLen; i++) {
							Hashtable<String, String> item = v2.get(i);
							String cmp = item.get("name");
							String type = item.get("type");
							listLen = graphList.size();

							for(int k=0; k<listLen; k++) {
								//지표설정항목에서 이름과 값을 분리한다.
								String loadJipyo = graphList.get(k);
								if(loadJipyo.equals("")) continue;
								String graphName = "";
								int[] graphValues=null;
								int index = loadJipyo.indexOf("{");
								if(index<1) {
									graphName = loadJipyo;
								} else {
									graphName = loadJipyo.substring(0, index);
									String graphValue = loadJipyo.substring(index);
									graphValue = COMUtil.removeString(graphValue, "{");
									graphValue = COMUtil.removeString(graphValue, "}");
									if(!graphValue.equals("")) {
										String[] strValues = graphValue.split("=");
										graphValues = new int[strValues.length];
										for(int m=0; m<strValues.length; m++) {
                                            //2016.12.02 by LYH >> HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                            if(strValues[m].contains("."))
                                            {
                                                graphValues[m] = (int)Double.parseDouble(strValues[m]);
                                            }
                                            //2016.12.02 by LYH << HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                            else
											    graphValues[m] = Integer.parseInt(strValues[m]);
										}
									}
								}
								if(cmp.equals(graphName)) {
									Vector<String> indiItem = new Vector<String>();
									indiItem.add(cmp);
									indiItem.add(type);
									pBaseChart._cvm.preSelectLabel = cmp;
									pBaseChart._cvm.preMenuItem = indiItem;
									pBaseChart._cvm.indicatorItem = indiItem;
									if(type.equals("Basic")) {
										pBaseChart.changeBlock(cmp);
									} else if(type.equals("Independence")) {
										pBaseChart.addStandBlock(cmp);
									}
								}
							}
						}
					}
				}

				pBaseChart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.
				//분석툴 데이터 처리.
				if(analInfos!=null && analInfos.size()>0) {
					try {
						Vector item = (Vector)analInfos.get(storageDivideIndex);
						pBaseChart._cvm.analItem=item;
					}catch(Exception e) {
						//                				System.out.println(e.getMessage());
					}
				}
//	        		//틱 데이터 타입처리.
//	        		try {
//	        		if(dataTypeNames.get(storageDivideIndex).equals("0")) { //틱 데이터 타입이면. 
//	        			pBaseChart.changeBlock_NotRepaint("라인");
//	        		}
//	        		} catch(Exception e) {
//	        			
//	        		}

			} else if(COMUtil.chartMode==COMUtil.BASIC_CHART) {
				//2015.04.07 by lyk - 주기별 차트 설정
				if(!COMUtil.isPeriodConfigSave()) {
					//2015.04.07 by lyk - 주기별 차트 설정 end
					//지표별 차트 처리.
					Vector list = (Vector)COMUtil.loadItem.get("graphList");
					Vector graphLists = (Vector)COMUtil.loadItem.get("graphLists");
					if(list!=null && list.size()>0) {
						Vector graphList = (Vector)graphLists.get(0);

						//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
//		    			Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
						Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
						Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
						for(int i=0; i<addItems.size(); i++) {
							v.add(addItems.get(i));
						}
						//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

						Vector<Hashtable<String, String>> v2 = COMUtil.getChartListTag();
						int vLen = v.size();
						_chart.removeAllBlocks();
						_chart.setEmptyUI();
						_chart.reSetUI(false);

                        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                        Vector sizeInfo = (Vector)COMUtil.loadItem.get("chartSizeInfo");
                        if(sizeInfo != null && sizeInfo.size()>0)
                        {
                            String strSizeInfo = (String)sizeInfo.get(0);
                            _chart.m_strSizeInfo = strSizeInfo;
                        }
                        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

						int listLen = list.size();
						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = (String)list.get(k);
							String graphName = "";
							int[] graphValues = null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m=0; m<strValues.length; m++) {
										try
										{
                                            //2016.12.02 by LYH >> HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                            if(strValues[m].contains("."))
                                            {
                                                graphValues[m] = (int)Double.parseDouble(strValues[m]);
                                            }
                                            //2016.12.02 by LYH << HTS저장 지표 기준선 못 불러오던 오류 수정(소수점 처리)
                                            else
											    graphValues[m] = Integer.parseInt(strValues[m]);
										}catch(NumberFormatException e){}
									}
								}
							}

							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
							boolean bIsMergedBlock = false;
							String strMergeBlockName = "";

							if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
								bIsMergedBlock = true;

								String[] arrGraphName = graphName.split(NeoChart2.MERGED_GRAPH_SEPARATOR);
								strMergeBlockName = arrGraphName[0];
							}
							//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

							//라인/바/봉 형식 저장 불러오기.
							//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
							if(graphName.equals("일본식봉") || graphName.equals("Heikin-Ashi")
							|| (bIsMergedBlock && (graphName.startsWith("일본식봉") || graphName.startsWith("Heikin-Ashi") || graphName.startsWith("투명캔들")))
							) {
								if(graphValues.length >= 2) {
                                    if(graphValues[0]==1 && graphValues[1]==0) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_LINE;
                                    } else if(graphValues[0]==0 && graphValues[1]==1) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR;
                                        //2015.04.30 by lyk - 바(시고저종) 유형 추가
                                    } else if(graphValues[0]==0 && graphValues[1]==2) {
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR_OHLC;
                                        //2015.04.30 by lyk - 바(시고저종) 유형 추가 end
                                    } else if(graphValues[0]==0 && graphValues[1]==3) {//영역라인
                                        COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_RANGE_LINE;
                                    } else if(graphValues[0]==0 && graphValues[1]==4) {//플로우
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_FLOW;
									} else if(graphValues[0]==0 && graphValues[1]==5) { //Heikin
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_HEIKIN_ASHI;
									//2020.07.06 by LYH >> 캔들볼륨 >>
									} else if(graphValues[0]==0 && graphValues[1]==6) { //Candle Volume
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_VOLUME;
									} else if(graphValues[0]==0 && graphValues[1]==7) { //Equi Volume
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_EQUI_VOLUME;
									//2020.07.06 by LYH >> 캔들볼륨 <<
									} else if(graphValues[0]==0 && graphValues[1]==8) { //계단
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_STAIR;
									} else if(graphValues[0]==0 && graphValues[1]==9) { //투명캔들
										COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY;
									} else {
                                        COMUtil.selectedJipyo=null;
                                    }
									_chart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk

									//2019. 01. 12 by hyh - 블록병합 처리. bIsMergedBlock 추가
									if(graphValues.length >= 20 || bIsMergedBlock) _chart.applyGraphConfigValue(graphName, graphValues);
								}
							}

							for(int i=0; i<vLen; i++) {
								Hashtable<String, String> item = v.get(i);
								//2015. 1. 13 by lyk -배열체크
								if(item==null || (item!=null && item.size()==0)) {
									continue;
								}
								//2015. 1. 13 by lyk -배열체크 end
								String cmp = item.get("name");
								String type = item.get("type");

								//2015. 1. 13 by lyk -null체크
								if (cmp == null || graphName == null) {
									continue;
								}

								//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
								if (cmp.equals(graphName)
								|| (bIsMergedBlock && strMergeBlockName.equals(cmp))) {
									//2015. 1. 13 by lyk -null체크 end
									//2013. 2. 8 체크안된 상세설정 오픈 : I114
									if(graphValues==null)
										continue;

									String checked = String.valueOf(graphValues[graphValues.length-1]);

									//활성화되지 않은 지표
									if(checked.equals("0"))
									{
										Block block = new Block(_chart, _chart._cvm, _chart._cdm, 0, 0, graphName);
										AbstractGraph nGraph = block.createGraph(graphName, _chart._cvm, _chart._cdm, Block.STAND_BLOCK);
										nGraph.changeControlValue(graphValues);
										_chart.addUnChkGraph(nGraph);
										if(graphName.equals("일목균형표"))
											_chart._cvm.futureMargin = 0;
									}
									//        	    				//활성화된 지표
									else
									{
										if(type.equals("Indicator")) {
											_chart.addBlock_Storage(cmp);
											if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
										} else if(type.equals("Trend") || type.equals("strategy")) {	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
											_chart.addGraph_Storage(cmp);
											if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
										}
									}
								}
							}
						}


//		    			for(int i=0; i<vLen; i++) {
//		    				Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
//		    	    		String cmp = (String)item.get("name");
//		    	    		String type = (String)item.get("type");
//		    	    		int listLen = list.size();
//		    	    		for(int k=0; k<listLen; k++) {
//	        	    			//지표설정항목에서 이름과 값을 분리한다.
//		    	    			String loadJipyo = (String)list.get(k);
//	        	    			String graphName = "";
//	        	    			int[] graphValues = null;
//	        	    			int index = loadJipyo.indexOf("(");
//	        	    			if(index<1) {
//	        	    				graphName = loadJipyo;
//	        	    			} else {
//	        	    				graphName = loadJipyo.substring(0, index);
//	        	    				String graphValue = loadJipyo.substring(index);
//	        	    				graphValue = COMUtil.removeString(graphValue, "(");
//	        	    				graphValue = COMUtil.removeString(graphValue, ")");
//	        	    				if(!graphValue.equals("")) {
//		        	    				String[] strValues = graphValue.split("=");
//		        	    				graphValues = new int[strValues.length];
//		        	    				for(int m=0; m<strValues.length; m++) {
//		        	    					try
//		        	    					{
//		        	    						graphValues[m] = Integer.parseInt(strValues[m]);
//		        	    					}catch(NumberFormatException e){}
//		        	    				}
//	        	    				}
//	        	    			}
//	        	    			
//	        	    			//라인/바/봉 형식 저장 불러오기.
//	        	    			if(graphName.equals("일본식봉")) {
//	        	    				if(graphValues.length >= 2) {
//	        	    					if(graphValues[0]==1 && graphValues[1]==0) {
//	        	    						COMUtil.selectedJipyo="라인";
//	        	    					} else if(graphValues[0]==0 && graphValues[1]==1) {
//	        	    						COMUtil.selectedJipyo="바";
//	        	    					} else {
//	        	    						COMUtil.selectedJipyo=null;
//	        	    					}
//	        	    					_chart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk
//	        	    					if(graphValues.length >= 20) _chart.applyGraphConfigValue(graphName, graphValues);
//	        	    				}
//	        	    			}
//	        	    			
//		    	    			if(cmp.equals(graphName)) {
//			    	    			//2013. 2. 8 체크안된 상세설정 오픈 : I114
//		    	    				if(graphValues==null) 
//		    	    					continue;
//		    	    				
//		    	    				String checked = String.valueOf(graphValues[graphValues.length-1]);
//	        	    				
//	        	    				//활성화되지 않은 지표
//	        	    				if(checked.equals("0"))
//	        	    				{
//	        	    					Block block = new Block(_chart, _chart._cvm, _chart._cdm, 0, 0, graphName);
//	        	    					AbstractGraph nGraph = block.createGraph(graphName, _chart._cvm, _chart._cdm, Block.STAND_BLOCK);
//	        	    					nGraph.changeControlValue(graphValues);
//	        	    					_chart.addUnChkGraph(nGraph);
//	        	    					if(graphName.equals("일목균형표"))
//	        	    						_chart._cvm.futureMargin = 0;
//	        	    				}
////	        	    				//활성화된 지표
//	        	    				else
//	        	    				{
//	        	    					if(type.equals("Indicator")) {
//			    	    					_chart.addBlock_Storage(cmp);
//			    	    					if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
//			    	    				} else if(type.equals("Trend")) {
//			    	    					_chart.addGraph_Storage(cmp);
//			    	    					if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
//			    	    				}
//	        	    				}
//		    	    			}
//		    	    		}
//		    			}
//		    			
						//Independence차트 체크.
						vLen = v2.size();
						for(int m=0; m<vLen; m++) {
							Hashtable<String, String> item = v2.get(m);
							String cmp = item.get("name");
							String type = item.get("type");
							listLen = graphList.size();

							for(int k=0; k<listLen; k++) {
								//지표설정항목에서 이름과 값을 분리한다.
								String loadJipyo = (String)graphList.get(k);
								if(loadJipyo.equals("")) {
//									System.out.println("aa");
									continue;
								}
								String graphName = "";
								int[] graphValues=null;
								int index = loadJipyo.indexOf("{");
								if(index<1) {
									graphName = loadJipyo;
								} else {
									graphName = loadJipyo.substring(0, index);
									String graphValue = loadJipyo.substring(index);
									graphValue = COMUtil.removeString(graphValue, "{");
									graphValue = COMUtil.removeString(graphValue, "}");
									if(!graphValue.equals("")) {
										String[] strValues = graphValue.split("=");
										graphValues = new int[strValues.length];
										for(int m2=0; m2<strValues.length; m2++) {
											try
											{
												graphValues[m2] = Integer.parseInt(strValues[m2]);
											}catch(NumberFormatException e){}
										}
									}
								}

//	        	    			System.out.println("DEBUG_setStorageState:"+"_cmp:"+cmp+"_graphName:"+graphName+"_type:"+type);

								if(cmp.equals(graphName)) {
									Vector<String> indiItem = new Vector<String>();
									indiItem.add(cmp);
									indiItem.add(type);
									_chart._cvm.preSelectLabel = cmp;
									_chart._cvm.preMenuItem = indiItem;
									_chart._cvm.indicatorItem = indiItem;
									if(type.equals("Basic")) {
										_chart.changeBlock(cmp);
									} else if(type.equals("Independence")) {
										_chart.addStandBlock(cmp);
									}
								}
							}
						}
					}
				}

				_chart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.

				try {
					//2019. 11. 05 by hyh - 세로/가로모드 전환 시 추세선 사라지는 에러 수정 >>
					_chart._cvm.analItem=(Vector)COMUtil.loadItem.get("analInfo");
					//COMUtil._neoChart._cvm.analItem=(Vector)COMUtil.loadItem.get("analInfo");
					//2019. 11. 05 by hyh - 세로/가로모드 전환 시 추세선 사라지는 에러 수정 <<
				} catch(Exception e) {
					e.printStackTrace();
				}

				//틱 데이터 타입처리.
				//if(COMUtil.dataTypeName.equals("0")) { //틱 데이터 타입이면. 
				//	COMUtil._mainFrame.mainBase.changeBlock_NotRepaint("라인");
				//}

			}
		}
	}

	//2015.04.14 by lyk - 주기별 차트 설정
	public void setPeriodSaveStorageState() {
		synchronized (this)    {
			_chart._cvm.setStandGraph(false);
			if(COMUtil.chartMode==COMUtil.DIVIDE_CHART) { //주기별 설정은 멀티차트일땐 처리하지 않는다. (오류발생)

			} else {
				storageDivideIndex=-1;
			}

			if(COMUtil._mainFrame.strFileName!=null &&  COMUtil._mainFrame.strFileName.contains("multi")) {
				COMUtil.chartMode=COMUtil.DIVIDE_CHART;
			} else {
				COMUtil.chartMode=COMUtil.BASIC_CHART;
			}
			if(COMUtil.chartMode==COMUtil.DIVIDE_CHART) {
				int selChartIndex = -1;

				//저장/불러오기 또는 마지막 상태 복원시 구분
				if((COMUtil._mainFrame.sendTrType.equals("storageType") || divideStorageType==true)) {
					selChartIndex = ++storageDivideIndex;
				}
                else {
                    for (int i = 0; i < chartList.size(); i++) {
                        BaseChart pBaseChart = chartList.get(i);
                        if (pBaseChart.equals(_chart)) {
                            selChartIndex = i;
                            break;
                        }
                    }
                }
				//지표데이터
				Vector graphLists = new Vector();
				Vector<BaseChart> chartList = COMUtil.divideChartList;

				if(chartList==null || (chartList!=null && chartList.size()<=selChartIndex)) return;
				BaseChart pBaseChart = chartList.get(selChartIndex);

////    			//2015.05.26 by lyk - 멀티차트의 기간타입 정보 처리 (외부의 기간타입을 차트 내부의 타입으로 변경)
				String strDataType = COMUtil._mainFrame.ctlDataTypeName;
				if(strDataType==null)
				{
					strDataType = pBaseChart._cdm.codeItem.strDataType;
				}
    			else {
    				int nDataTypeName[] = {2,3,4,1,0,2,6,5};
    				try {
    					strDataType = ""+nDataTypeName[Integer.parseInt(strDataType)-1];
    				} catch (Exception e) {

    				}
    			}
////    			//2015.05.26 by lyk - 멀티차트의 기간타입 정보 처리 (외부의 기간타입을 차트 내부의 타입으로 변경) end

				String strPeriodGraphList = "";
                String strPeriodSizeInfo = "";  //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                if((COMUtil._mainFrame.sendTrType.equals("storageType") || divideStorageType==true) && isInit==false) {
                    strPeriodGraphList = (String)COMUtil._mainFrame.loadPeriodSaveItem.get(COMUtil.getPeriodSaveKeyName()+pBaseChart._cdm.codeItem.strDataType+"_"+selChartIndex);
                    strPeriodSizeInfo = (String)COMUtil._mainFrame.loadPeriodSizeInfo.get(COMUtil.getPeriodSaveKeyName()+pBaseChart._cdm.codeItem.strDataType+"_"+selChartIndex);   //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                } else {
                    strPeriodGraphList = (String)COMUtil._mainFrame.loadPeriodSaveItem.get(COMUtil.getPeriodSaveKeyName()+strDataType+"_"+selChartIndex);
                    strPeriodSizeInfo = (String)COMUtil._mainFrame.loadPeriodSizeInfo.get(COMUtil.getPeriodSaveKeyName()+strDataType+"_"+selChartIndex);    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                }

				//System.out.println("Debug_setPeriodSave_storageDivideIndex:"+selChartIndex+" strDataType"+strDataType+" strPeriodGraphList:"+strPeriodGraphList);

				//4개 분할에 대한 정보를 얻어온다.
				if(strPeriodGraphList!=null && !strPeriodGraphList.equals("")) {
					String[] token = strPeriodGraphList.split("\\/");
					graphLists = new Vector<Vector<String>>();
					String str="";
					for(int i=0; i<token.length; i++) {
//		    		   StringTokenizer st = new StringTokenizer(token[i], "#");
						StringTokenizer st = new StringTokenizer(token[i], "~");		//2015. 9. 2 주기별차트 멀티차트시 구분자 변경 (음수값이 들어갈 경우 배열이 잘못 구성됨)
						Vector<String> graphs=new Vector<String>();
						while (st.hasMoreTokens()) {
							str = COMUtil.getDecodeData(st.nextToken());
							graphs.add(str);
						}
						graphLists.add(graphs);
					}
				} else {
					if(_chart!=null) {
						_chart.removeAllBlocks();
//						_chart.setBasicUI();
						if(m_bFXChart){
							_chart.setBasicUI_noVolume();
						}else{
							_chart.setBasicUI();
						}
						_chart.reSetUI(true);

						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제)
//	    		    		COMUtil._mainFrame.isInitBlock = true;
						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제) end
					}
				}

				if(graphLists!=null && pBaseChart!=null && graphLists.size()>0) {
					Vector<String> graphList = (Vector<String>)graphLists.get(0);
					//지표별 차트처리.
					//2015. 1. 13 by lyk - 동일지표 추가 :  (추가리스트 적용)
					Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
					Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
					for(int i=0; i<addItems.size(); i++) {
						v.add(addItems.get(i));
					}
					//2015. 1. 13 by lyk - 동일지표 추가 :  (추가리스트 적용) end
					Vector<Hashtable<String, String>> v2 = COMUtil.getChartListTag();
					int vLen = v.size();
					if(vLen>0)
					{
						pBaseChart.removeAllBlocks();
						pBaseChart.setEmptyUI();
						pBaseChart.reSetUI(false);

						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제)
//		        	            COMUtil._mainFrame.isInitBlock = true;
						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제) end
					}

                    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                    if(strPeriodSizeInfo!=null)
                    {
                        pBaseChart.m_strSizeInfo = strPeriodSizeInfo;
                    }
                    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end
					int listLen = graphList.size();
					for(int k=0; k<listLen; k++) {
						//지표설정항목에서 이름과 값을 분리한다.
						String loadJipyo = graphList.get(k);
						if(loadJipyo.equals("")) continue;
						String graphName = "";
						int[] graphValues=null;
						int index = loadJipyo.indexOf("{");
						if(index<1) {
							graphName = loadJipyo;
						} else {
							graphName = loadJipyo.substring(0, index);
							String graphValue = loadJipyo.substring(index);
							graphValue = COMUtil.removeString(graphValue, "{");
							graphValue = COMUtil.removeString(graphValue, "}");
							if(!graphValue.equals("")) {
								String[] strValues = graphValue.split("=");
								graphValues = new int[strValues.length];
								for(int m=0; m<strValues.length; m++) {
									try
									{
										graphValues[m] = Integer.parseInt(strValues[m]);
									}catch(NumberFormatException e){
										System.out.println("Debug_error_"+loadJipyo);
									}
								}
							}
						}

						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
						boolean bIsMergedBlock = false;
						String strMergeBlockName = "";

						if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
							bIsMergedBlock = true;

							String[] arrGraphName = graphName.split(NeoChart2.MERGED_GRAPH_SEPARATOR);
							strMergeBlockName = arrGraphName[0];
						}
						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

						//라인/바/봉 형식 저장 불러오기.
						//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
						if (graphName.equals("일본식봉") || graphName.equals("Heikin-Ashi")
						|| (bIsMergedBlock && (graphName.startsWith("일본식봉") || graphName.startsWith("Heikin-Ashi") || graphName.startsWith("투명캔들")))
						) {
							if(graphValues.length >= 2) {
                                if(graphValues[0]==1 && graphValues[1]==0) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_LINE;
                                } else if(graphValues[0]==0 && graphValues[1]==1) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR;
                                    //2015.04.30 by lyk - 바(시고저종) 유형 추가
                                } else if(graphValues[0]==0 && graphValues[1]==2) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR_OHLC;
                                    //2015.04.30 by lyk - 바(시고저종) 유형 추가 end
                                } else if(graphValues[0]==0 && graphValues[1]==3) {//영역라인
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_RANGE_LINE;
                                } else if(graphValues[0]==0 && graphValues[1]==4) {//플로우
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_FLOW;
								} else if(graphValues[0]==0 && graphValues[1]==5) { //Heikin
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_HEIKIN_ASHI;
								//2020.07.06 by LYH >> 캔들볼륨 >>
								} else if(graphValues[0]==0 && graphValues[1]==6) { //Candle Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_VOLUME;
								} else if(graphValues[0]==0 && graphValues[1]==7) { //Equi Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_EQUI_VOLUME;
								//2020.07.06 by LYH >> 캔들볼륨 <<
								} else if(graphValues[0]==0 && graphValues[1]==8) { //계단
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_STAIR;
								} else if(graphValues[0]==0 && graphValues[1]==9) { //투명캔들
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY;
								} else {
                                    COMUtil.selectedJipyo=null;
                                }
								pBaseChart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk

								//2019. 01. 12 by hyh - 블록병합 처리. bIsMergedBlock 추가
								if(graphValues.length >= 20 || bIsMergedBlock) pBaseChart.applyGraphConfigValue(graphName, graphValues);
							}
						}

						//2013. 2. 8 체크안된 상세설정 오픈 : I114
						for(int i=0; i<vLen; i++) {
							Hashtable<String, String> item = v.get(i);
							String cmp = item.get("name");
							String type = item.get("type");

							//2015. 1. 13 by lyk -null체크
							if (cmp == null || graphName == null) {
								continue;
							}

							//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
							if (cmp.equals(graphName)
							|| (bIsMergedBlock && strMergeBlockName.equals(cmp))) {
								if(type.equals("Indicator"))
								{
									pBaseChart.addBlock_Storage(cmp);
									if(graphValues != null)
									{
										pBaseChart.applyGraphConfigValue(graphName, graphValues);
									}
								}
								else if(type.equals("Trend") || type.equals("strategy"))	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
								{
									pBaseChart.addGraph_Storage(cmp);
									{
										if(graphValues != null)
										{
											pBaseChart.applyGraphConfigValue(graphName, graphValues);
										}
									}
								}
							}
						}
					}

					//Independence차트 체크.
					vLen = v2.size();
					for(int i=0; i<vLen; i++) {
						Hashtable<String, String> item = v2.get(i);
						String cmp = item.get("name");
						String type = item.get("type");
						listLen = graphList.size();

						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = graphList.get(k);
							if(loadJipyo.equals("")) continue;
							String graphName = "";
							int[] graphValues=null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m=0; m<strValues.length; m++) {
										//graphValues[m] = Integer.parseInt(strValues[m]);
										try
										{
											graphValues[m] = Integer.parseInt(strValues[m]);
										}catch(NumberFormatException e){
											System.out.println("Debug_error_"+loadJipyo);
										}
									}
								}
							}
							if(cmp.equals(graphName)) {
								Vector<String> indiItem = new Vector<String>();
								indiItem.add(cmp);
								indiItem.add(type);
								pBaseChart._cvm.preSelectLabel = cmp;
								pBaseChart._cvm.preMenuItem = indiItem;
								pBaseChart._cvm.indicatorItem = indiItem;
								if(type.equals("Basic")) {
									pBaseChart.changeBlock(cmp);
								} else if(type.equals("Independence")) {
									pBaseChart.addStandBlock(cmp);
								}
							}
						}
					}
				}

				pBaseChart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.

			} else if(COMUtil.chartMode==COMUtil.BASIC_CHART) {
				//지표별 차트 처리.
				Vector<String> list = new Vector<String>();
				Vector<Vector<String>> graphLists = new Vector<Vector<String>>();
				if(COMUtil._mainFrame.ctlDataTypeName==null) {
					COMUtil._mainFrame.ctlDataTypeName = COMUtil.dataTypeName;
				}

                String strDataType = COMUtil._mainFrame.ctlDataTypeName;
                if(strDataType==null)
                {
                    strDataType = _chart._cdm.codeItem.strDataType;
                }
                else {
                    int nDataTypeName[] = {2,3,4,1,0,2,6,5};
                    try {
                        strDataType = ""+nDataTypeName[Integer.parseInt(strDataType)-1];
                    } catch (Exception e) {

                    }
                }
                String strPeriodGraphList = (String)COMUtil._mainFrame.loadPeriodSaveItem.get(COMUtil.getPeriodSaveKeyName()+strDataType+"_0");
//		    			System.out.println("Debug_getKey:"+COMUtil.getPeriodSaveKeyName()+COMUtil._mainFrame.ctlDataTypeName + " result:"+strPeriodGraphList);

				if(strPeriodGraphList!=null && !strPeriodGraphList.equals("")) {
					String[] token = strPeriodGraphList.split("\\/");
					graphLists = new Vector<Vector<String>>();
					String str="";
					for(int i=0; i<token.length; i++) {
//		    		   StringTokenizer st = new StringTokenizer(token[i], "#");
						StringTokenizer st = new StringTokenizer(token[i], "~");	//2015. 9. 2 주기별차트 멀티차트시 구분자 변경 (음수값이 들어갈 경우 배열이 잘못 구성됨)
						Vector<String> graphs=new Vector<String>();
						while (st.hasMoreTokens()) {
							str = COMUtil.getDecodeData(st.nextToken());
							graphs.add(str);
						}
						graphLists.add(graphs);
						list = graphs;
					}
				} else {
					if(_chart!=null) {
						_chart.removeAllBlocks();
//						_chart.setBasicUI();
						if(m_bFXChart){
							_chart.setBasicUI_noVolume();
						}else{
							_chart.setBasicUI();
						}
						_chart.reSetUI(true);

						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제)
//		    		    		COMUtil._mainFrame.isInitBlock = true;
						//2015.04.23 by lyk - 차트 블럭크기 및 위치 정보 저장(블럭크기에 변경이 발생하면 처리 - 추가,삭제) end
					}
				}

				if(list!=null && list.size()>0) {
					Vector graphList = (Vector)graphLists.get(0);
					//2015. 1. 13 by lyk - 동일지표 추가 : (추가리스트 적용)
					Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
					Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
					for(int i=0; i<addItems.size(); i++) {
						v.add(addItems.get(i));
					}
					//2015. 1. 13 by lyk - 동일지표 추가 : (추가리스트 적용) end
					Vector<Hashtable<String, String>> v2 = COMUtil.getChartListTag();
					int vLen = v.size();
					_chart.removeAllBlocks();
					_chart.setEmptyUI();
					_chart.reSetUI(false);

                    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                    String strPeriodSizeInfo = (String)COMUtil._mainFrame.loadPeriodSizeInfo.get(COMUtil.getPeriodSaveKeyName()+strDataType+"_0");
                    if(strPeriodSizeInfo!=null)
                    {
                        _chart.m_strSizeInfo = strPeriodSizeInfo;
                    }
                    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

					int listLen = list.size();
					for(int k=0; k<listLen; k++) {
						//지표설정항목에서 이름과 값을 분리한다.
						String loadJipyo = list.get(k);
						String graphName = "";
						int[] graphValues = null;
						int index = loadJipyo.indexOf("{");
						if(index<1) {
							graphName = loadJipyo;
						} else {
							graphName = loadJipyo.substring(0, index);
							String graphValue = loadJipyo.substring(index);
							graphValue = COMUtil.removeString(graphValue, "{");
							graphValue = COMUtil.removeString(graphValue, "}");
							if(!graphValue.equals("")) {
								String[] strValues = graphValue.split("=");
								graphValues = new int[strValues.length];
								for(int m=0; m<strValues.length; m++) {
									try
									{
										graphValues[m] = Integer.parseInt(strValues[m]);
									}catch(NumberFormatException e){
										System.out.println("Debug_error_"+loadJipyo);
									}
								}
							}
						}

						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
						boolean bIsMergedBlock = false;
						String strMergeBlockName = "";

						if (graphName.contains(NeoChart2.MERGED_GRAPH_SEPARATOR)) {
							bIsMergedBlock = true;

							String[] arrGraphName = graphName.split(NeoChart2.MERGED_GRAPH_SEPARATOR);
							strMergeBlockName = arrGraphName[0];
						}
						//2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<

						//라인/바/봉 형식 저장 불러오기.
						//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
						if (graphName.equals("일본식봉") || graphName.equals("Heikin-Ashi")
						|| (bIsMergedBlock && (graphName.startsWith("일본식봉") || graphName.startsWith("Heikin-Ashi") || graphName.startsWith("투명캔들")))
						) {
							if(graphValues.length >= 2) {
                                if(graphValues[0]==1 && graphValues[1]==0) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_LINE;
                                } else if(graphValues[0]==0 && graphValues[1]==1) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR;
                                    //2015.04.30 by lyk - 바(시고저종) 유형 추가
                                } else if(graphValues[0]==0 && graphValues[1]==2) {
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_BAR_OHLC;
                                    //2015.04.30 by lyk - 바(시고저종) 유형 추가 end
                                } else if(graphValues[0]==0 && graphValues[1]==3) {//영역라인
                                    COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_RANGE_LINE;
                                } else if(graphValues[0]==0 && graphValues[1]==4) {//플로우
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_FLOW;
								} else if(graphValues[0]==0 && graphValues[1]==5) { //Heikin
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_HEIKIN_ASHI;
								//2020.07.06 by LYH >> 캔들볼륨 >>
								} else if(graphValues[0]==0 && graphValues[1]==6) { //Candle Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_VOLUME;
								} else if(graphValues[0]==0 && graphValues[1]==7) { //Equi Volume
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_EQUI_VOLUME;
								//2020.07.06 by LYH >> 캔들볼륨 <<
								} else if(graphValues[0]==0 && graphValues[1]==8) { //계단
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_STAIR;
								} else if(graphValues[0]==0 && graphValues[1]==9) { //투명캔들
									COMUtil.selectedJipyo=COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY;
								} else {
                                    COMUtil.selectedJipyo=null;
                                }
								_chart.m_strCandleType = COMUtil.selectedJipyo; //차트 형태를 알려줌. by lyk

								//2019. 01. 12 by hyh - 블록병합 처리. bIsMergedBlock 추가
								if(graphValues.length >= 20 || bIsMergedBlock) _chart.applyGraphConfigValue(graphName, graphValues);
							}
						}

						for(int i=0; i<vLen; i++) {
							Hashtable<String, String> item = v.get(i);
							//2015. 1. 13 by lyk - 동일지표 추가 -배열체크
							if(item==null || (item!=null && item.size()==0)) {
								continue;
							}
							//2015. 1. 13 by lyk - 동일지표 추가 -배열체크 end
							String cmp = item.get("name");
							String type = item.get("type");

							//2015. 1. 13 by lyk - 동일지표 추가 -null체크
							//2019. 01. 12 by hyh - 블록병합 처리. || bIsMergedBlock 추가
							if((cmp != null && graphName != null) && cmp.equals(graphName)
							|| (bIsMergedBlock && strMergeBlockName.equals(cmp))) {
								if(type.equals("Indicator")) {
									_chart.addBlock_Storage(cmp);
									if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
								} else if(type.equals("Trend") || type.equals("strategy")) {	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
									_chart.addGraph_Storage(cmp);
									if(graphValues!=null) _chart.applyGraphConfigValue(graphName, graphValues);
								}
							}
						}
					}

					//Independence차트 체크.
					vLen = v2.size();
					for(int m=0; m<vLen; m++) {
						Hashtable<String, String> item = v2.get(m);
						String cmp = item.get("name");
						String type = item.get("type");
						listLen = graphList.size();

						for(int k=0; k<listLen; k++) {
							//지표설정항목에서 이름과 값을 분리한다.
							String loadJipyo = (String)graphList.get(k);
							if(loadJipyo.equals("")) {
								continue;
							}
							String graphName = "";
							int[] graphValues=null;
							int index = loadJipyo.indexOf("{");
							if(index<1) {
								graphName = loadJipyo;
							} else {
								graphName = loadJipyo.substring(0, index);
								String graphValue = loadJipyo.substring(index);
								graphValue = COMUtil.removeString(graphValue, "{");
								graphValue = COMUtil.removeString(graphValue, "}");
								if(!graphValue.equals("")) {
									String[] strValues = graphValue.split("=");
									graphValues = new int[strValues.length];
									for(int m2=0; m2<strValues.length; m2++) {
										try
										{
											graphValues[m2] = Integer.parseInt(strValues[m2]);
										}catch(NumberFormatException e){}
									}
								}
							}
							if(cmp.equals(graphName)) {
								Vector<String> indiItem = new Vector<String>();
								indiItem.add(cmp);
								indiItem.add(type);
								_chart._cvm.preSelectLabel = cmp;
								_chart._cvm.preMenuItem = indiItem;
								_chart._cvm.indicatorItem = indiItem;
								if(type.equals("Basic")) {
									_chart.changeBlock(cmp);
								} else if(type.equals("Independence")) {
									_chart.addStandBlock(cmp);
								}
							}
						}
					}
				}

				_chart.resetTitleBoundsAll();//지표 블럭 타이틀 갱신.
			}
		}
	}
	//2015.04.14 by lyk - 주기별 차트 설정

	public void initChart(ChartEvent e){

	}
	public void notifyChartAnalToolDone(ChartEvent e){
//        if(this.toolbar!=null)toolbar.notifyDone();
	}
	public void showCrossLine(boolean bShow)
	{
        if(m_bCompareChart)
        {
            compareChart._cvm.isCrosslineMode = bShow;
            if (bShow) {
                compareChart.viewP.setVisibility(View.VISIBLE);
            } else {
                compareChart.viewP.setVisibility(View.GONE);
            }
			if (!bShow)
				compareChart.setShowTooltip(bShow);
            compareChart.repaintAll();
        }
        else {
            for (int i = 0; i < chartList.size(); i++) {
                BaseChart baseChart = chartList.get(i);
                baseChart._cvm.isCrosslineMode = bShow;
                //2012. 10. 18  십자선보이기 버튼 눌렀을 때  데이터패널 보이게 하기 : VP10
                if (bShow) {
                    baseChart.viewP.setVisibility(View.VISIBLE);
                } else {
                    baseChart.viewP.setVisibility(View.GONE);
                }
                if (!bShow)
                    baseChart.resetTitleBounds();
                baseChart.repaintAll();
            }
        }
	}
	public void repaintAllChart() {
		for(int i=0; i<chartList.size(); i++)
		{
			BaseChart baseChart = chartList.get(i);
			baseChart.repaintAll();
		}
	}
	//날짜타입에 따라 초기화 해야하는지 결정
//    public void initChart(String dateType){
//        int curr_type=chart.getDateType();
//        chart.setReverse(false);
//        switch(curr_type){            
//            case 1:
//            case 2:
//            case 3:
//                if(dateType.indexOf("틱")!=-1){
//                    chart.resetDataInfo(data_info_TIC,6);
//                    chart.setDataType_TIC(dateType);
//                    chart.setReverse(true);
//                    chart.initBlock();
//                    chart.setBasicTICUI();
//                }else if((dateType.indexOf("분")!=-1)||(dateType.indexOf("초")!=-1)){                    
//                    chart.resetDataInfo(data_info_M,5);
//                    chart.setDataType_MIN(dateType,"DDHHMMSS");
//                }else{
//                    if(dateType.equals("일")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1");
//                    }else if(dateType.equals("주")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    }else if(dateType.equals("월")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    }else if(dateType.equals("년")){ //20030514 ykLee
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    }
//                    chart.setDataType_DAY(dateType);
//                }
//            break;
//            //case chart._cdm.DATA_MIN:
//            case 4:
//                if(dateType.equals("일")){
//                    chart.resetDataInfo(data_info_DWM,14);
//                    chart._cdm.setPacketFormat("기본거래량","× 1");
//                    chart.setDataType_DAY(dateType);
//                }else if(dateType.equals("주")){
//                    chart.resetDataInfo(data_info_DWM,14);
//                    chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    chart.setDataType_DAY(dateType);
//                }else if(dateType.equals("월")){
//                    chart.resetDataInfo(data_info_DWM,14);
//                    chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    chart.setDataType_DAY(dateType);
//                }else if(dateType.equals("년")){
//                    chart.resetDataInfo(data_info_DWM,14);
//                    chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    chart.setDataType_DAY(dateType);
//                }else if(dateType.indexOf("틱")!=-1){
//                    chart.setReverse(true);
//                    chart.resetDataInfo(data_info_TIC,6);
//                    chart.setDataType_TIC(dateType);
//                    chart.initBlock();
//                    chart.setBasicTICUI();
//                }else{
//                    chart.setDataType_MIN(dateType,"DDHHMMSS");
//                }
//            break;
//            case 5:
//                if((dateType.indexOf("일")!=-1)||(dateType.indexOf("주")!=-1)||(dateType.indexOf("월")!=-1)){
//                    chart.resetDataInfo(data_info_DWM,14);
//                    if(dateType.equals("일")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1");
//                    }else if(dateType.equals("주")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");
//                    }else if(dateType.equals("월")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");                        
//                    }else if(dateType.equals("년")){
//                        chart._cdm.setPacketFormat("기본거래량","× 1000");                        
//                    }
//                    chart.setDataType_DAY(dateType);
//                    chart.initBlock();
//                    chart.setBasicUI();
//                }else if((dateType.indexOf("분")!=-1)||(dateType.indexOf("초")!=-1)){
//                    chart.resetDataInfo(data_info_M,5);
//                    chart.setDataType_MIN(dateType,"DDHHMMSS");
//                    chart.initBlock();
//                    chart.setBasicUI();
//                }else{
//                    chart.setDataType_TIC(dateType);
//                    chart.setReverse(true);
//                }
//            break;
//        }
//
//    }

	public void setSkinType(int nSkinType)
	{
		COMUtil.setSkinType(nSkinType);
		if(m_bCompareChart)
		{
			compareChart.setSkinType(nSkinType);
			compareChart.repaintAll();
		}
		if(chartList != null)
		{
			for(int i=0; i<chartList.size(); i++) {
				BaseChart baseChart = chartList.get(i);
				baseChart.setSkinType(nSkinType);
				baseChart.repaintAll();
			}
		}

		if(nSkinType == COMUtil.SKIN_BLACK) {
			View view = this.layout.findViewWithTag(_TAG_BACK_IMG);
			view.setVisibility(View.VISIBLE);
		} else {
			View view = this.layout.findViewWithTag(_TAG_BACK_IMG);
			view.setVisibility(View.GONE);
		}
	}

	public int getChartCount() {
		return chartList.size();
	}

	public int getSelectedChartIndex()
	{
		for(int i=0; i<chartList.size(); i++)
		{
			NeoChart2 pChart = chartList.get(i);
			if(pChart == _chart)
				return i;
		}

		return 0;
	}

	public void setMarketData(String title, long[] dates, double[] marketData, int nCount, boolean bSendTR) {
		if(_chart!=null) {
			_chart.setMarketData(title, dates, marketData, nCount, bSendTR);
		}
	}

	public void checkMultiInit()
	{
		m_bMultiChart = COMUtil.bIsMulti;
//        if (m_bMultiChart)
//        {
//            nDivideType = 41;
//            COMUtil.divideType = nDivideType;
//            setDivisionChart(4);
////            m_bMultiData = true;
//        }
	}
	boolean start = false;
	byte[] buf;

	int[] dtt = { 5, 1, 9, 1, 1, 1, 9, 1, 6, 1, 9, 1, 9, 1, 9, 1 };
	int[] fl = { 4, 1, 0, 1, 2, 1, 0, 1, 3, 1, 0, 1, 0, 1, 0, 1 };

	String code = "";
	int id = 11;
	String old_code = "";

	DataField[] datafield;

	private View preSelBtn = null;
	public PopupWindow baselinePopup = null;
	public PopupWindow autotrendPopup = null;
	BaseLineView baseLine;

	public void onClick(View v) {
		if (v == null) {
			if (preSelBtn != null)
				preSelBtn.setSelected(false);
			return;
		}
		if (preSelBtn != null && !preSelBtn.equals(v)) {
			preSelBtn.setSelected(false);
		}
		if (v.isSelected()) {
			v.setSelected(false);
			// ...Handle toggle off
		} else {
			v.setSelected(true);
			// ...Handled toggle on
		}

		preSelBtn = v;
	}

	public void setAnalTool(View v) {
		if (v == null) {
			if (preSelBtn != null)
				preSelBtn.setSelected(false);
			return;
		}

		if (preSelBtn != null && !preSelBtn.equals(v)) {
			preSelBtn.setSelected(false);
		}
		if (v.isSelected()) {
			v.setSelected(false);
			// ...Handle toggle off
		} else {
			v.setSelected(true);
			// ...Handled toggle on
		}

		//2015. 1. 19 일주월분 버튼 토글로 변경 >>
		if(Integer.parseInt((String)v.getTag()) == COMUtil.TOOLBAR_CONFIG_DWMM)
		{
			setChartToolBar(COMUtil.TOOLBAR_CONFIG_DWMM);
			v.setSelected(m_bDWMMClick);

			return;
		}
		//2015. 1. 19 일주월분 버튼 토글로 변경 <<

		//2014. 3. 18 분석툴바에 십자선 툴팁 추가>> : 십자선툴팁 버튼은 독립적으로. 
		if(v.getTag().equals("50040"))
		{
			showCrossLine(v.isSelected());
			return;
		}
		//2014. 3. 18 분석툴바에 십자선 툴팁 추가<<

		preSelBtn = v;

		int tag = Integer.parseInt((String) v.getTag());
		setButtonMode(tag);

        if (tag == COMUtil.TOOLBAR_CONFIG_BASELINE) {
            if (COMUtil.apiMode) {
                WindowManager wm = COMUtil._chartMain.getWindowManager();
                Display display = wm.getDefaultDisplay();

                int width = display.getWidth();
                int height = display.getHeight();

                RelativeLayout simpleLayout = new RelativeLayout(COMUtil._mainFrame.getContext());
                simpleLayout.setLayoutParams(new LayoutParams(width, height));
                simpleLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (baselinePopup != null) {
                            baselinePopup.dismiss();
                            baselinePopup = null;
                        }
                    }
                });

                baseLine = new BaseLineView(COMUtil.apiView.getContext(), simpleLayout);
                COMUtil.setGlobalFont(simpleLayout);

                baselinePopup = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
                baselinePopup.setOutsideTouchable(true);
                baselinePopup.setBackgroundDrawable(new BitmapDrawable());
                baselinePopup.showAtLocation(COMUtil.apiView, Gravity.TOP, 0, 0);

                int resId = COMUtil.apiView.getContext().getResources().getIdentifier("frameabtnfunction", "id", COMUtil.apiView.getContext().getPackageName());
                Button btnConfirm = (Button) simpleLayout.findViewById(resId);
                btnConfirm.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (baselinePopup != null) {
                            baseLine.close();
                            baseLine.setApply();

							//2019. 08. 21 by hyh - 자동추세선 복원 >>
//                            //2013. 8. 30 기준선 저장 안되고 있음 >>
//                            COMUtil.saveGijunState("gijunChartSetting");
//                            //2013. 8. 30 기준선 저장 안되고 있음 >>
							//2019. 08. 21 by hyh - 자동추세선 복원 <<

                            baselinePopup.dismiss();
                            baselinePopup = null;
                        }
                    }
                });
            }
            else {
                BaseLineView baseLine = new BaseLineView(COMUtil._chartMain,
                        COMUtil._neoChart.layout);
            }

            this.setToolbarState(9999);
        }
        else if (tag == COMUtil.TOOLBAR_CONFIG_AUTOLINE) {
            if (COMUtil.apiMode) {
                WindowManager wm = COMUtil._chartMain.getWindowManager();
                Display display = wm.getDefaultDisplay();

                int width = display.getWidth();
                int height = display.getHeight();

                RelativeLayout simpleLayout = new RelativeLayout(COMUtil._mainFrame.getContext());
                simpleLayout.setLayoutParams(new LayoutParams(width, height));
                simpleLayout.setBackgroundColor(Color.argb(70, 0, 0, 0));
                simpleLayout.setGravity(Gravity.CENTER);
                simpleLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (autotrendPopup != null) {
                            autotrendPopup.dismiss();
                            autotrendPopup = null;
                        }
                    }
                });

                AutoTrendView autotrend = new AutoTrendView(COMUtil._mainFrame.getContext(), simpleLayout);
                COMUtil.setGlobalFont(simpleLayout);

                autotrendPopup = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
                autotrendPopup.setOutsideTouchable(true);
                autotrendPopup.setBackgroundDrawable(new BitmapDrawable());
                autotrendPopup.showAtLocation(COMUtil.apiView, Gravity.CENTER, 0, 0);

                int resId = COMUtil.apiView.getContext().getResources().getIdentifier("btn_autotrend_close", "id", COMUtil.apiView.getContext().getPackageName());
                Button btnClose = (Button) simpleLayout.findViewById(resId);
                btnClose.setTypeface(COMUtil.typefaceMid);

                btnClose.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (autotrendPopup != null) {
                            autotrendPopup.dismiss();
                            autotrendPopup = null;
                        }
                    }
                });
            }
            else {
                AutoTrendView autotrend = new AutoTrendView(COMUtil._chartMain, COMUtil._neoChart.layout);
            }
            this.setToolbarState(9999);
		} else if (tag == COMUtil.TOOLBAR_CONFIG_INIT) {
//			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(COMUtil._chartMain);  
//			//2013. 9. 5 초기화 대화창에서  예, 아니오 위치 변경 >>
////			alert_confirm.setMessage("차트 설정을 초기화 하시겠습니까?").setCancelable(false).setPositiveButton("예",  
////			new DialogInterface.OnClickListener() {  
////			    @Override  
////			    public void onClick(DialogInterface dialog, int which) {  
////			        // 'YES'  
////			    	initChart("pressInitBtn");
////			    }  
////			}).setNegativeButton("아니오",  
////			new DialogInterface.OnClickListener() {  
////			    @Override  
////			    public void onClick(DialogInterface dialog, int which) {  
////			        // 'No'  
////			    return;  
////			    }  
////			});
//			
//			alert_confirm.setMessage("차트 설정을 초기화 하시겠습니까?").setCancelable(false).setNegativeButton("아니오",  
//					new DialogInterface.OnClickListener() {  
//			    @Override  
//			    public void onClick(DialogInterface dialog, int which) {  
//			        // 'No'  
//			    return;  
//			    }  
//			}).setPositiveButton("예",  
//					new DialogInterface.OnClickListener() {  
//					    @Override  
//					    public void onClick(DialogInterface dialog, int which) {  
//					        // 'YES'  
//					    	initChart("pressInitBtn");
//					    }  
//					});
//			
//			alert_confirm.create().show();

			//2013. 9. 12 초기화 대화창 UI 디자인>>
			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
			//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 start
			alert.setMessage("차트 설정을 초기화합니다.");
			alert.setNoButton("취소", null);
			alert.setYesButton("초기화",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {
							initChart("pressInitBtn");	//초기화 수행
							COMUtil._mainFrame.bIsInitState = true; //2020.01.23 상단 초기화 저장안되는 오류 수정 - hjw
							COMUtil._mainFrame.saveStatus(null);	//2013. 9. 12 분석툴바에서 초기화 하고 딴화면 갔다 오면 분석툴바 순서가 안바뀌어있음.
							COMUtil.saveGijunState("gijunChartSetting");	//2013. 10. 7 분석툴바 기준선 추가 후 초기화 한 다음에 화면 나갔다 들어오면 기준선이 다시 그려져있음
							dialog.dismiss();
						}
					});
			//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 end
			alert.show();
			COMUtil.g_chartDialog = alert;

//			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(COMUtil._chartMain);  
//			alert_confirm.setView(alertLayout);
//			final DialogInterface dlg = alert_confirm.show();

//			int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_no", "id", COMUtil.apiView.getContext().getPackageName());
//			Button alert_btn_no = (Button)alertLayout.findViewById(layoutResId);
//			alert_btn_no.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dlg.dismiss();
//					return;
//				}
//			});
//			
//			layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("alert_btn_yes", "id", COMUtil.apiView.getContext().getPackageName());
//			Button alert_btn_yes = (Button)alertLayout.findViewById(layoutResId);
//			alert_btn_yes.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					initChart("pressInitBtn");	//초기화 수행 
//			    	COMUtil._mainFrame.saveStatus(null);	//2013. 9. 12 분석툴바에서 초기화 하고 딴화면 갔다 오면 분석툴바 순서가 안바뀌어있음.
//			    	dlg.dismiss(); //팝업창 닫기 
//			    	
//				}
//			});
			//2013. 9. 12 초기화 대화창 UI 디자인>>

			this.setToolbarState(9999);

		} else if (tag == COMUtil.TOOLBAR_CONFIG_SETTING) {
//			Hashtable<String, Object> dic = new Hashtable<String, Object>();
//			RelativeLayout.LayoutParams params=null;
//			params =new RelativeLayout.LayoutParams(
//					COMUtil.apiView.getWidth(), COMUtil.apiView.getHeight());
//			params.leftMargin=0;
//			params.topMargin=COMUtil.apiView.getTop();
//
//			dic.put("frame", params);
//			dic.put("tag", COMUtil._TAG_INDICATOR_CONFIG);
//			dic.put("apiView",  COMUtil.apiView);
//			dic.put("triXpos", "-1");
//
//			COMUtil._mainFrame.selectChartMenuFromParent(dic);

			openIndicatorPopup();

		} else if (tag == COMUtil.TOOLBAR_CONFIG_DIVIDE) {
			Hashtable<String, Object> dic = new Hashtable<String, Object>();
			RelativeLayout.LayoutParams params=null;
			params =new RelativeLayout.LayoutParams(
					(int)COMUtil.getPixel(310), (int)COMUtil.getPixel(335));
//			params.leftMargin=(COMUtil.apiView.getWidth()-(int)COMUtil.getPixel(223))/2;
//			params.topMargin=(COMUtil.apiView.getHeight()-(int)COMUtil.getPixel(249))/2;

			dic.put("frame", params);
			dic.put("tag", COMUtil._TAG_DIVIDECHART_CONFIG);
			dic.put("apiView",  COMUtil.apiView);
			dic.put("triXpos", "-1");

			COMUtil._mainFrame.selectChartMenuFromParent(dic);

			this.setToolbarState(9999);
		} else if (tag == COMUtil.TOOLBAR_CONFIG_INDICATOR) {
			_chart.openPopupJipyoList();
			this.setToolbarState(9999);
		}
		//2015. 1. 13 저장/불러오기 버튼 분석툴바에 추가>>
		else if (tag == COMUtil.TOOLBAR_CONFIG_SAVE) {
            final View view = v;
            try
            {
				Handler mHandler = new Handler(Looper.getMainLooper());//안드로이드 7.0버전 저장버튼 에러 처리
				mHandler.postDelayed( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        COMUtil.saveChart(view);
                    }
                }, 1 );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

		}
		else if (tag == COMUtil.TOOLBAR_CONFIG_LOAD) {
			COMUtil.loadChart(v);
		}


//		// 2012. 12. 13 비교차트 : T65
//		else if (tag == 50030) {
////			 Toast.makeText(COMUtil._mainFrame.getContext(), "비교차트", Toast.LENGTH_SHORT).show();
//			_chart.userProtocol.requestInfo(COMUtil._TAG_COMPARECHART_TYPE,
//					null);
//
//			return;
//		}

		if( 	   tag == COMUtil.TOOLBAR_CONFIG_INIT
				|| tag == COMUtil.TOOLBAR_CONFIG_DIVIDE
				|| tag == COMUtil.TOOLBAR_CONFIG_INDICATOR
				|| tag == COMUtil.TOOLBAR_CONFIG_SETTING
				|| tag == COMUtil.TOOLBAR_CONFIG_SAVE
				|| tag == COMUtil.TOOLBAR_CONFIG_LOAD
				|| tag == COMUtil.TOOLBAR_CONFIG_AUTOLINE
				|| tag == COMUtil.TOOLBAR_CONFIG_BASELINE)
		{
			/**
			 * 팝업창을 띄울 때는 1. 분석툴바 해제하고 2. 분석툴을 닫고 3. 현재 분석툴버튼을 선택해제한다. 
			 * */

			setChartToolBar(9999);			//1
			addToolBarHanWha();				//2
			v.setSelected(false);	//3
		}
		else if (tag >= COMUtil.TOOLBAR_CONFIG_START && tag < COMUtil.TOOLBAR_CONFIG_END) {
			//분석도구들 
			if (v.isSelected())
			{
				/**
				 * 분석툴 버튼을 눌렀다면 1. 누른 분석툴로 세팅 2. 해당분석툴 이름으로 토스트메시지 띄움  3. (조건에따라) 지우개툴이면 버튼선택해제 4. (조건에따라) 십자선 수치조회면 분석툴세팅을 해제
				 * */

				//1
				setChartToolBar(tag);
				//2
				if(tag != COMUtil.TOOLBAR_CONFIG_ERASE)
					Toast.makeText(context, COMUtil.astrToolbarNames[(tag-COMUtil.TOOLBAR_CONFIG_START)], Toast.LENGTH_SHORT).show();
				//3
				if(tag == COMUtil.TOOLBAR_CONFIG_ERASE || tag == COMUtil.TOOLBAR_CONFIG_ALL_ERASE)
				{
					v.setSelected(false);
				}
			}
			else
			{
				/**
				 * 눌러진 분석툴 버튼을 다시 누르면 분석툴선택을 해제 
				 * */

				setChartToolBar(9999);
			}
		}
		//2015. 1. 13 저장/불러오기 버튼 분석툴바에 추가<<
	}

	//	private int tag = 0;
	private int preTag = -1;
	private boolean btnToggle = false;
	public void setButtonMode(final int tag) {
		if (tag == COMUtil.TOOLBAR_CONFIG_ERASE
				|| tag == COMUtil.TOOLBAR_CONFIG_ALL_ERASE
				|| tag == COMUtil.TOOLBAR_CONFIG_DWMM
				|| tag == COMUtil.TOOLBAR_CONFIG_TEXT) {// 삭제
			return;
		}
		this.tag = tag;
		this.isContinueAnalDrawMode = true;
		this.isCrossBtnSelect = false;
		if (tag == preTag) {
			btnToggle = !btnToggle;
		} else {
			btnToggle = true;
		}
		preTag = tag;
	}

	public void selectChart_index(int nIndex)
	{
		//동기화 초기화
		this.m_nRotateIndex = -1;
		this.m_bSyncJongMok = false;
		COMUtil.isSyncJongmok = m_bSyncJongMok;
		this.m_bSyncJugi = false;
		COMUtil.isSyncJugi = m_bSyncJugi;

		if(isMultiChart()) {
			if(nIndex>=chartList.size()) {
				return;
			}
			for(int i=0; i<chartList.size(); i++)
			{
				BaseChart pChart = chartList.get(i);
				if(m_nTotNum != 1 && i == nIndex)
				{
					pChart.setSelected(true);
					_chart = pChart;
					COMUtil._neoChart = _chart;
					pChart.selectChart();
				}
				else
					pChart.setSelected(false);

				pChart.repaintAll();
			}
		}
	}

	public void selectChart_custom_index(int nIndex)
	{
		//동기화 초기화
		this.m_nRotateIndex = -1;
//    	this.m_bSyncJongMok = false;
//    	COMUtil.isSyncJongmok = m_bSyncJongMok;
//    	this.m_bSyncJugi = false;
//    	COMUtil.isSyncJugi = m_bSyncJugi;

		if(nIndex>=chartList.size()) {
			return;
		}
		for(int i=0; i<chartList.size(); i++)
		{
			BaseChart pChart = chartList.get(i);
			if(m_nTotNum != 1 && i == nIndex)
			{
				pChart.setSelected(true);
				_chart = pChart;
				COMUtil._neoChart = _chart;
				pChart.selectChart();
			}
			else
				pChart.setSelected(false);

			pChart.repaintAll();
		}
	}

	public void setRotateState(boolean state)
	{
		this.isRotation = state;
	}

	public void resetCompareCodes(String strCodes, String strNames, String strMarkets)
	{
		if(strCodes != null)
		{
			compareChart.removeAllCodes();
			String[] arrCodes = strCodes.split(";");
			String[] arrNames = strNames.split(";");
			String[] arrMarkets = strMarkets.split(";");
			if(arrCodes.length > 0)
			{
				for(int i=0; i<arrCodes.length; i++)
				{
					String strCode = arrCodes[i];
					if(!strCode.equals("")) {
                        if(arrNames.length>i && arrMarkets.length>i)
                            compareChart.addCode(arrCodes[i], arrNames[i], arrMarkets[i]);
                    }
				}
			}
		}
	}

	public void resetPriceData(String strName, String strPrice, String strSign, String strChange, String strRate, String strVolume, String strCode)
	{
        for(int i=0; i<chartList.size(); i++)
        {
        	BaseChart pChart = (BaseChart)chartList.get(i);
//	        if(_chart._cdm.codeItem.strName != null && _chart._cdm.codeItem.strName.equals(strName))
//	        {
        	if(pChart._cdm.codeItem.strName != null)
	        {
	            String strCurName = pChart._cdm.codeItem.strName.trim();
	            if(strCurName.equals(strName))
	            {   
			        if(strPrice.startsWith("-") || strPrice.startsWith("+"))
			        	strPrice = strPrice.substring(1);
			        strChange = strChange.trim();
			        if(strChange.startsWith("-") || strChange.startsWith("+"))
			        	strChange = strChange.substring(1);
			        strRate = strRate.trim();
			        if(strRate.startsWith("-") || strRate.startsWith("+"))
			        	strRate = strRate.substring(1);
			        
			        pChart._cdm.codeItem.strName = strName;
			        pChart._cdm.codeItem.strPrice = strPrice;
			        pChart._cdm.codeItem.strSign = strSign;
			        pChart._cdm.codeItem.strChange = strChange;
			        pChart._cdm.codeItem.strChgrate = COMUtil.format(strRate, 2, 3)+"%";
			        pChart._cdm.codeItem.strVolume = strVolume;
			        pChart.repaintAll();
			        pChart.chartItem.setText(pChart._cdm.codeItem, 0, 1);
	            }
	        }
	    }
	}

	public void setPeriodList(String sData)
	{
		if(COMUtil.bIsForeignFuture || COMUtil.bIsGlobalStock)  //2017.09.13 by pjm 해외주식 >>
		{
			String[] strList = sData.split(";");
			if (strList.length >= 14) {
				for (int i = 0; i < 14; i++) {
					String strPeriodInfo = strList[i];
					String[] strPeriod = strPeriodInfo.split("=");

					int nPeriodType = Integer.parseInt(strPeriod[0]);
					if(nPeriodType==1)
						astrPeriodDayBtnData[i] = 1;
					else
						astrPeriodDayBtnData[i] = 0;
					if(nPeriodType==2)
						astrPeriodWeekBtnData[i] = 1;
					else
						astrPeriodWeekBtnData[i] = 0;
					if(nPeriodType==3)
						astrPeriodMonBtnData[i] = 1;
					else
						astrPeriodMonBtnData[i] = 0;
					if(nPeriodType==4)
						astrPeriodMinBtnData[i] = 1;
					else
						astrPeriodMinBtnData[i] = 0;
					if(nPeriodType==5)
						astrPeriodTikBtnData[i] = 1;
					else
						astrPeriodTikBtnData[i] = 0;

					astrPeriodData[i] = Integer.parseInt(strPeriod[1]);
				}
			}
		}
		else {
			String[] strList = sData.split("\\/");
			if (strList.length >= 2) {
				try {
					String strMin = strList[0];
					String[] strMins = strMin.split(";");
					for (int i = 0; i < astrMinData.length; i++) {
						//2019. 09. 09 by hyh - 분틱설정 화면과 동기화 >>
						String[] arrTitleChecked = strMins[i].split(":");
						astrMinData[i] = Integer.parseInt(arrTitleChecked[0]);
						astrMinCheckData[i] = Integer.parseInt(arrTitleChecked[1]);
						//2019. 09. 09 by hyh - 분틱설정 화면과 동기화 <<
					}

					String strTick = strList[1];
					String[] strTicks = strTick.split(";");
					for (int i = 0; i < astrTikData.length; i++) {
						//2019. 09. 09 by hyh - 분틱설정 화면과 동기화 >>
						String[] arrTitleChecked = strTicks[i].split(":");
						astrTikData[i] = Integer.parseInt(arrTitleChecked[0]);
						astrTikCheckData[i] = Integer.parseInt(arrTitleChecked[1]);
						//2019. 09. 09 by hyh - 분틱설정 화면과 동기화 <<
					}

					//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가
					if (strList.length >= 3) {
						String strSec = strList[2];
						String[] strSecs = strSec.split(";");
						for (int i = 0; i < astrSecData.length; i++) {
							astrSecData[i] = Integer.parseInt(strSecs[i]);
						}
					}
					//2016.12.26 by LYH >> 초차트 처리 및 초주기 설정 추가 end
				}
				catch (Exception e) {
					astrMinData = astrMinDefaultData;
					astrMinCheckData = astrMinCheckDefaultData;
					astrTikData = astrTikDefaultData;
					astrTikCheckData = astrMinCheckDefaultData;
					astrSecData = astrSecDefaultData;
					astrSecCheckData = astrSecCheckDefaultData;

					e.printStackTrace();
				}
			}
		}
	}

	public void clearCompareData()
	{
		if(compareChart != null)
		{
			compareChart.clearCompareData();
		}
		else
			_chart.ChartDataClear();
		m_nCompareRotateIndex = -1;
	}

	public void resetBasicUI(String strUI)
	{
		if(_chart==null)
			return;
        if(strUI.equals("미결제약정"))
        {
            _chart.setBasicUI_Migyul();
            // _chart._cvm.setOnePage(1);
            _chart.repaintAll();
        }
        else if(strUI.equals("미결제증감"))
        {
            _chart.setBasicUI_Migyul_UpDown();
            // _chart._cvm.setOnePage(1);
            _chart.repaintAll();
        }
        else if(strUI.equals("Basis"))
        {
            _chart.setBasicUI_Basis();
            // _chart._cvm.setOnePage(1);
            _chart.repaintAll();
        }
		else if(strUI.equals("linefill"))
		{
//    		_chart.setBasicUI_Investor(strUI);
			_chart.setLineFillUI();
			_chart._cvm.setOnePage(1);
			_chart.repaintAll();
		}
		else
		{
			_chart._cvm.bInvestorChart = true;
			if(strUI.endsWith("#"))
			    _chart._cvm.bRatePeriod = true;
			_chart.setBasicUI_Investor(strUI);

			//2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
			if(strUI.endsWith("updown_upjong")) {
				layout.removeView(_chart);
				ScrollView m_VScrollView = new ScrollView(getContext());
				m_VScrollView.addView(_chart, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layout.addView(m_VScrollView);
			}
			//2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

			//2019.08.12 by LYH >> 업종 그리드 바차트 추가 Start
			if(_chart._cvm.bIsUpdownGridChart) {
				//layout.removeView(_chart);
				((ViewGroup)_chart.getParent()).removeView(_chart);
				ScrollView m_VScrollView = new ScrollView(getContext());
				ViewCompat.setNestedScrollingEnabled(m_VScrollView, true);	//2021.02.15 by LYH >> 컨테이너 안에서 스크롤 안 되던 오류 수정
				m_VScrollView.requestDisallowInterceptTouchEvent(false);
				m_VScrollView.addView(_chart, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layout.addView(m_VScrollView);
			}
			//2019.08.12 by LYH >> 업종 그리드 바차트 추가 End
//			if(!strUI.endsWith("#"))
//				_chart._cvm.setOnePage(1);
		}
	}
	//2012. 10. 23 분석툴을 한번만 그리고 해당 분석툴버튼 disable.  : T62
	public void initAnalTool()
	{
		//분석툴바 누르지 않은 상태로 초기화 . 
		setToolbarState(9999);
		onClick(null);
	}

	//2012.11.27 by LYH >> 진법 및 승수 처리.
	public void setPriceFormat(String sCode, int nScale, int nDecPoint, int nLogDisp) {
		if(this.compareChart!=null) {
			compareChart.setPriceFormatCompare(nScale, nDecPoint, nLogDisp, sCode);
		}else {
			for (int i = 0; i < chartList.size(); i++) {
				BaseChart pChart = chartList.get(i);
				if (pChart._cdm.codeItem.strCode != null
						&& pChart._cdm.codeItem.strCode.equals(sCode)) {
					pChart.setPriceFormat(nScale, nDecPoint, nLogDisp);
				}
			}
		}
	}
	//2012.11.27 by LYH <<

	public void totalViewMode() {
		// _chart_cvm.setOnePage(1);
		if (_chart._cvm.isOnePage())
			_chart._cvm.setOnePage(0);
		else
			_chart._cvm.setOnePage(1);

		_chart.reset();
	}

	public void setOnePage(int nOnePage) {
		_chart._cvm.setOnePage(nOnePage);

		_chart.reset();
	}

	public String getCodeInfo(int nIndex) {
		if (isMultiChart()) {
			if (nIndex >= chartList.size()) {
				return "";
			}

			BaseChart pChart = chartList.get(nIndex);
			String strRet=pChart._cdm.codeItem.strCode+"^"+pChart._cdm.codeItem.strName+"^"+pChart._cdm.codeItem.strMarket;
			return strRet;

		}
		return "";
	}

	/* 태블릿 분할기능 처리 */
	public void setDivisionCustom(int divideType, int divideCount) {
		nDivideType = divideType;
		COMUtil.divideType = nDivideType;
//		if(inputPanel != null) inputPanel.showSyncButton(true);

		setDivisionChart(divideCount);
		m_nSelIndex = 0;

		BaseChart pChart = chartList.get(chartList.size()-1);
		pChart.setSelected(false);

		pChart = chartList.get(0);
//        pChart.selected=true;

		_chart = pChart;
		COMUtil._neoChart = _chart;
	}

	//2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리 (getter, setter) >>
	public boolean isAnotherColorWhiteSkin()
	{
		return bIsAnotherColorWhiteSkin;
	}
	public void setIsAnotherColorWhiteSkin(boolean bIsAnotherColorWhiteSkin)
	{
		this.bIsAnotherColorWhiteSkin = bIsAnotherColorWhiteSkin;
	}
	//2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리 <<
	public void hideToolBar()
	{
		if(_chart != null && _chart._cvm != null) {
			if (_chart._cvm.getToolbarState() == 9999 && this.m_bIsShowToolBar == true)
				addToolBarHanWha();
		}
	}

	//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>
	private void showSyncAlert()
	{
		DRAlertDialog alert = new DRAlertDialog(context);
//		alert.setTitle("알림");
		alert.setMessage("단일차트화면일때는 동기화 기능을 사용할 수 없습니다.");
		alert.setOkButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alert.show();
		COMUtil.g_chartDialog = alert;
	}
//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

	//2013.10.04 by LYH >> 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음
	public boolean isCanSaveChart()
	{
		if(divideStorageType == true)
			return false;
		return m_bGetData;
	}

	public void setCanSaveChart(boolean bValue)
	{
		m_bGetData = bValue;
	}
	//2013.10.04 by LYH <<

	//2013. 11. 21 추세선 종목별 저장>>
	/**
	 * 각 차트의 종목주기별로 저장되어 있는 추세선등 분석툴바 정보를 모두 초기화한다.
	 * */
	public void resetAllAnalToolBySymbol()
	{
		if(null != COMUtil.analPrefEditor)
		{
			//모든 저장정보를 초기화한다.
			COMUtil.analPrefEditor.clear();

			//수정완료. 
			COMUtil.analPrefEditor.commit();
		}

		//차트에 그려진 분석툴 모두 제거 
		for(int i = 0; i < chartList.size(); i++)
		{
			chartList.get(i).removeAllAnalTool();
		}
	}
	//2013. 11. 21 추세선 종목별 저장<<

	//2015. 2. 24 차트 상하한가 표시>>
	public void requestUpperLowerLimitData(boolean bIsAll)
	{
		BaseChart pBaseChart;
		if(COMUtil.isUpperLowerLimit() && _chart != null)
		{
			Hashtable<String, Object> dic = new Hashtable<String, Object>();
			String strCodes="";
			String strCount="";

			if(bIsAll)
			{
				for(int i=0; i<chartList.size(); i++)
				{
					pBaseChart = chartList.get(i);
					if(pBaseChart._cdm.codeItem.strRealKey.equals("S31") && pBaseChart._cdm.codeItem.strDataType.equals("2"))
					{
						strCodes = strCodes + pBaseChart._cdm.codeItem.strCode + ";";
						strCount = strCount + pBaseChart._cdm.getCount()/200 + ";";
					}
					else
					{
						pBaseChart.setChartUpperLowerLimitData(null);
						strCodes = strCodes + ";";
						strCount = strCount + ";";
					}
				}
			}
			else
			{
				for(int i=0; i<chartList.size(); i++)
				{
					if(m_nSelIndex==i || m_nSelIndex < 0)
					{
						pBaseChart = chartList.get(i);
						if(pBaseChart._cdm.codeItem.strRealKey.equals("S31") && pBaseChart._cdm.codeItem.strDataType.equals("2"))
						{
							strCodes = strCodes + pBaseChart._cdm.codeItem.strCode + ";";
							strCount = strCount + pBaseChart._cdm.getCount()/200 + ";";
						}
						else
						{
							pBaseChart.setChartUpperLowerLimitData(null);
							strCodes = strCodes + ";";
							strCount = strCount + ";";
						}
					}
					else
					{
						strCodes = strCodes + ";";
						strCount = strCount + ";";
					}
				}
			}

			dic.put("upperLowerCodes", strCodes);
			dic.put("upperLowerCounts", strCount);
			if(_chart.userProtocol!=null) _chart.userProtocol.requestInfo(COMUtil._TAG_REQUEST_UPPERLOWERLIMIT, dic);
		}
		else
		{
			for(int i=0; i<chartList.size(); i++)
			{
				pBaseChart = chartList.get(i);
				pBaseChart.setChartUpperLowerLimitData(null);
			}
		}
	}

	@Override
	public void onUpperLowerLimitCheck()
	{
		requestUpperLowerLimitData(true);
	}

	public void setChartUpperLowerLimitData(String strParam)
	{
		String[] arToken = strParam.split("!");
		if(arToken.length >= 2)
		{
			int nIdx = Integer.parseInt(arToken[0]);
			if(nIdx < chartList.size())
			{
				chartList.get(nIdx).setChartUpperLowerLimitData(arToken[1]);
			}
		}
	}
	//2015. 2. 24 차트 상하한가 표시<<

	//2015. 3. 18 보이는 개수 및 조회 갯수 설정>>
	/**
	 * 차트 조회갯수 변경값을 map  에 전달하고 재조회요청
	 * @param strData   변경된 조회갯수 값
	 * */
	public void onChangeRequestDataLength(String strData)
	{
		//조회갯수 변경값 map 에 넘겨서 재조회
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		dic.put("RequestDataLength", strData);
		if(_chart.userProtocol!=null) _chart.userProtocol.requestInfo(COMUtil._TAG_CHANGE_REQUESTDATALENGTH, dic);

		//조회갯수 변경값 변수 저장
		setRequestDataLength(strData);

		if (COMUtil.apiMode)
		{
			this.sendTR("storageType");
		}
	}

	/**
	 * 차트 조회갯수 변경 사용여부 설정
	 * @param bFlag  true(사용)   false(사용안함)
	 * */
	public void setUseChangeRequestDataLength(boolean bFlag)
	{
		m_bUseChangeRequestDataLength = bFlag;
	}

	/**
	 * 차트 조회갯수 변경 사용여부
	 * @return 조회갯수 변경 기능 사용여부
	 * */
	@Override
	public boolean isUseChangeRequestDataLength() {
		return m_bUseChangeRequestDataLength;
	}

	/**
	 * 차트 조회갯수 변경값 설정
	 * @param strData   조회갯수 값
	 * */
	public void setRequestDataLength(String strData)
	{
		//조회갯수 변경값 변수 저장
		m_nRequestDataLength = Integer.parseInt(strData);
	}

	/**
	 * 차트 조회갯수(delegate함수)
	 * @return 조회갯수
	 * */
	@Override
	public int getRequestDataLength() {
		return m_nRequestDataLength;
	}
	//2015. 3. 18 보이는 개수 및 조회 갯수 설정<<

	//2015. 10. 19 현재가차트 투명 처리>>
	public void setChartBgTransparent(String strParam)
	{
		_chart._cvm.bIsTransparent = strParam.equals("1");
	}
	//2015. 10. 19 현재가차트 투명 처리<<
	public void setHideChartTitle(String strParam)
	{
		_chart._cvm.bIsShowTitle = !strParam.equals("1");
	}

	//2015. 12. 11 map-chart 간 분틱값 동기화>>
	@Override
	public void onLoadPeriodValue() {
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		dic.put("MethodType", "get");
		if(_chart.userProtocol!=null) _chart.userProtocol.requestInfo(COMUtil._TAG_SYNC_PERIODVALUE, dic);

	}
	//2015. 12. 11 map-chart 간 분틱값 동기화<<

	public void openIndicatorPopup()
	{
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		RelativeLayout.LayoutParams params = null;
		params = new RelativeLayout.LayoutParams(COMUtil.g_nDisWidth, COMUtil.g_nDisHeight - (int) COMUtil.getPixel(25));
		params.leftMargin = 0;
		params.topMargin = COMUtil.apiView.getTop();

		dic.put("frame", params);
		dic.put("tag", COMUtil._TAG_INDICATOR_CONFIG);
		dic.put("apiView", COMUtil.apiView);
		dic.put("triXpos", "-1");

		COMUtil._mainFrame.selectChartMenuFromParent(dic);

		this.setToolbarState(9999);
	}

	//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경>>
	/**
	 * 현재 분할타입을 알아온다.  (분할설정창의 분할아이콘 Tag와 동일) 
	 * */
	public int getDivideType()
	{
		return nDivideType;
	}
	//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경<<

	@Override
	public void onChangeNormalSetValues(int nIndex) {
		switch(nIndex)
		{
			case 2:	//y축 스케일 보이기/숨기기
				for(BaseChart _chart : chartList)
				{
					_chart.reSetUI(true);
				}
				break;
			case 6://조회 개수 표시
				for(BaseChart _chart : chartList)
				{
					_chart.reSetUI(false);
				}
				break;
		}
	}

//	@Override
//	public void onMessage(String strMessage) {
//		if(strMessage.equals(COMUtil._POPUP_EVENT_CLOSE))
//		{
//			closeApiPopup();
//		}
//	}

	public void setUseCurrentColor(String strParam)
	{
		_chart._cvm.bUseCurrentColor = strParam.equals("1");
	}

	public void setAccrueData(String arrData)
	{
		if(_chart!=null) {
			//2014.05.22 by LYH >> ChartPacketDataModel로 데이터 값 이동.
			COMUtil.lcode = "S31";
			COMUtil.apCode = COMUtil.TR_CHART_INVESTOR;
			String[] sepDatas = arrData.split("=");
			String sDataField = sepDatas[0];
			if(sDataField.equals("initdata"))
				_chart.initDataInfo("");
//        //2014.05.22 by LYH << ChartPacketDataModel로 데이터 값 이동.
			_chart.setAccrueData(arrData);
		}
	}
    public void setVisibleCompareDataIndex(boolean visible, int index) {
		if(compareChart != null)
    	{
    		compareChart.setVisibleCompareDataIndex(visible, index);
    	}
	}
    public void setCompareSetting() {
		if(compareChart != null)
    	{
    		compareChart.applySetting(false);
    	}
	}
    
    public void setRequestCompareData(boolean bReset)
    {
		if(compareChart != null)
    	{
			if(bReset)
				compareChart.applySetting(true);
    		sendTrCodes();
    	}
    }

    //2013. 8. 5 y축눈금 추가>>
    public void resetUIAllChart()
    {
        for(int i=0; i<chartList.size(); i++)
        {
            BaseChart pBaseChart = chartList.get(i);
            pBaseChart.reSetUI(true);
        }
    }
    //2013. 8. 5 y축눈금 추가>>

    public void setFuncAllChart(String strFunc)
    {
        if(strFunc.equals("setPaddingRight")) {
            for (int i = 0; i < chartList.size(); i++) {
                BaseChart baseChart = chartList.get(i);
                int nPaddingRight = 0;
                if(COMUtil.isUsePaddingRight())
                {
                    nPaddingRight = COMUtil.getPaddingRight();
                }
                baseChart.setPaddingRight(nPaddingRight);
                baseChart.reSetUI(true);
            }
        }
    }

	public void setBaselineData(String datas) {
		BaseChart pChart;
		for(int i=0; i<chartList.size(); i++)
		{
			pChart = (BaseChart)chartList.get(i);
			pChart.setBaselineData(datas);
		}
	}

    public void setRealUpdate(String bRealUpdate) {
        BaseChart pChart;
        for(int i=0; i<chartList.size(); i++)
        {
            pChart = (BaseChart)chartList.get(i);
            pChart._cdm.m_bRealUpdate = true;
        }
    }
	//2017. 7. 24 by pjm 차트 지표명 개행시키기>>
	public int getExtendedChart(){
		return m_nExtendChart;
	}
	//2017. 7. 24 by pjm 차트 상단 지표명 개행시키기<<

	//2019. 03. 11 by hyh - 간편설정 버튼 추가 >>
	public RelativeLayout btnSimpleOpen = null;
	public RelativeLayout rv_toolbar_oneq = null;
	int[] sLocation = null;

	public void addSimpleSet() {
		try {
			//Screen
			final int width = frame.width();
			final int height = frame.height();
			sLocation = new int[2];
			layout.getLocationOnScreen(sLocation); // view 절대좌표

			//Button
			final float btnWidth = COMUtil.getPixel(26);
			final float btnHeight = COMUtil.getPixel(64);

//			if (btnSimpleOpen != null) {
//				COMUtil.apiView.removeView(btnSimpleOpen);
//				btnSimpleOpen = null;
//			}

			//RelativeLayout
			btnSimpleOpen = new RelativeLayout(this.context);

			int resId = context.getResources().getIdentifier("btn_simple_indicator", "drawable", context.getPackageName());
			btnSimpleOpen.setBackgroundResource(resId);
			btnSimpleOpen.setSelected(false); //Selected : SimpleSetting is shown

			final RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams((int) btnWidth, (int) btnHeight);
			btnSimpleOpen.setLayoutParams(btnParams);
			btnParams.leftMargin = 0;
			btnParams.topMargin = sLocation[1] + height / 2 - btnParams.height / 2;

			//ImageView
			resId = context.getResources().getIdentifier("chart_menu_show", "drawable", context.getPackageName());
			ImageView ivChartMenuShow = new ImageView(this.context);
			ivChartMenuShow.setBackgroundResource(resId);
			ivChartMenuShow.setPadding((int) COMUtil.getPixel(1.0f), 0, 0, 0);

			final RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(10), (int) COMUtil.getPixel(10));
			ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
			ivParams.leftMargin = (int) COMUtil.getPixel(3.5f);
			ivChartMenuShow.setLayoutParams(ivParams);

			btnSimpleOpen.addView(ivChartMenuShow);
			layout.addView(btnSimpleOpen);

			btnSimpleOpen.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//Toggle
					btnSimpleOpen.setSelected(!btnSimpleOpen.isSelected());

					if (btnSimpleOpen.isSelected()) {
						btnSimpleOpen.setVisibility(GONE);

						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(206), layout.getHeight() + (int) COMUtil.getPixel(oneTouchTopMargin));
						layout.getLocationOnScreen(sLocation); // view 절대좌표
						params.leftMargin = sLocation[0];
						params.topMargin = sLocation[1] - getStatusBarHeight() - (int) COMUtil.getPixel(oneTouchTopMargin);

						//가로모드 적용
						Configuration config = getResources().getConfiguration();
						if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
							params.leftMargin = 0;
							params.topMargin = sLocation[1] - (int) COMUtil.getPixel(oneTouchTopMargin);
						}

						Hashtable<String, Object> dic = new Hashtable<String, Object>();
						dic.put("tag", Integer.valueOf(COMUtil._TAG_INDICATORMINI_CONFIG));

						if (params != null) {
							dic.put("frame", params);
						}

						if (btnParams != null) {
							int nCloseImageViewMarginTop = btnParams.topMargin + (int) COMUtil.getPixel(oneTouchTopMargin);
							dic.put("closeImageViewMarginTop", nCloseImageViewMarginTop);
						}

						if (COMUtil._mainFrame != null) {
							COMUtil._mainFrame.selectChartMenuFromParent(dic);
						}
					}
					else {
						if (COMUtil._mainFrame != null) {
							COMUtil._mainFrame.closePopup();
						}

						resetSimpleSettingButton();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeSimpleSet() {
		if (btnSimpleOpen != null) {
			layout.removeView(btnSimpleOpen);
			btnSimpleOpen = null;
		}
	}

	public void resetSimpleSettingButton() {
		if (btnSimpleOpen != null) {
			btnSimpleOpen.bringToFront();
			btnSimpleOpen.setVisibility(VISIBLE);
			btnSimpleOpen.setSelected(false);
		}
	}

	public void setShowToolBarOneq(boolean bShow) {
		if (rv_toolbar_oneq != null) {
			if(bShow)
				rv_toolbar_oneq.setVisibility(View.VISIBLE);
			else
				rv_toolbar_oneq.setVisibility(View.GONE);
		}
	}

	public int getStatusBarHeight() {
		int nStatusBarHeight = 0;
		int nResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (nResourceId > 0) {
			nStatusBarHeight = getResources().getDimensionPixelSize(nResourceId);
		}

		return nStatusBarHeight;
	}
	//2019. 03. 11 by hyh - 간편설정 버튼 추가 <<

	//2019.04.15 원터치 차트설정불러오기 추가 - lyj
	public void addOneTouch() {
		if (!COMUtil._mainFrame.isOneTouchSet) {
			COMUtil._mainFrame.bShowOneTouch = false;
		}

		BaseChart pBaseChart = (BaseChart) COMUtil._neoChart;

		ViewGroup.MarginLayoutParams lpBaseChart = (ViewGroup.MarginLayoutParams) pBaseChart.getLayoutParams();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lpBaseChart.width, (int) COMUtil.getPixel(38));
		params.leftMargin = 0;
		params.topMargin = 0;

		oneTouchLayout = new RelativeLayout(context);
		oneTouchLayout.setLayoutParams(params);

		if (COMUtil._mainFrame.bShowOneTouch) {
			oneTouchLayout.setVisibility(View.VISIBLE);
		}
		else {
			oneTouchLayout.setVisibility(View.GONE);
		}

		oneTouchItem = new OneTouchItemView(context, oneTouchLayout);
		oneTouchItem.setLayoutParams(params);
		oneTouchItem.setBasicUI();
		oneTouchItem.setBaseChart(pBaseChart);

		oneTouchLayout.addView(oneTouchItem);
		COMUtil.apiLayout.addView(oneTouchLayout);
	}

	public void removeOneTouch() {
		if (oneTouchLayout != null) {
			COMUtil.apiLayout.removeView(oneTouchLayout);
			oneTouchLayout.removeAllViews();
			oneTouchLayout = null;
			oneTouchItem = null;
		}

		oneTouchTopMargin = 0;
		setOneTouchModeParams();
	}

	public void setOneTouchModeParams() {
		frame.bottom = COMUtil.chartHeight - (int) COMUtil.getPixel(oneTouchTopMargin);

		int width = frame.width();
		int height = frame.height();
		int ih = (int) COMUtil.getPixel(oneTouchTopMargin);
		int nGap = 0;

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		params.leftMargin = 0;
		params.topMargin = ih + nGap;
		params.width = width;
		params.height = height;

		this.layout.setLayoutParams(params);
	}

	public void showOneTouch(boolean bShow) {
		if (oneTouchLayout == null)
			return;

		if (bShow) {
			oneTouchTopMargin = 38;
			oneTouchLayout.setVisibility(View.VISIBLE);
		}
		else {
			oneTouchTopMargin = 0;
			oneTouchLayout.setVisibility(View.GONE);
		}

		setOneTouchModeParams();
	}

	public void resetOneTouchList() {
		if (oneTouchItem != null)
			oneTouchItem.resetUI();
	}
	//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

	//2019. 08. 21 by hyh - 자동추세선 복원 >>
	public void setBaseSetting(String strBaseInfo) {
		try {

			String strItem;
			int cLen = chartList.size();
			String[] strSettingInfos = strBaseInfo.split("#");
			if(strSettingInfos.length<1)
				return;

			String[] strBaseInfos = strSettingInfos[0].split("/");
			if (chartList.size() == 1) {

				if (strBaseInfos.length > 0) {
					strItem = strBaseInfos[0];
					if (strItem != null || !strItem.equals("")) {
						String[] lists = strItem.split("=");
						if (_chart._cvm.baseLineType != null && _chart._cvm.baseLineType.size() > 0) {
							_chart._cvm.baseLineType.clear();
						}
						for (String type : lists) {
							if (!type.equals("")) {
								_chart._cvm.baseLineType.add(Integer.parseInt(type));
							}
						}
					}

				}
			}
			else
			{
				for(int i=0; i<cLen; i++) {
					if(strBaseInfos.length>i) {
						strItem = strBaseInfos[i];
						if (strItem == null || strItem.equals("")) {
							continue;
						}
						String[] arrConv = strItem.split("=");

						BaseChart pBaseChart = chartList.get(i);
						if (pBaseChart._cvm.baseLineType != null && pBaseChart._cvm.baseLineType.size() > 0) {
							pBaseChart._cvm.baseLineType.clear();
						}
						for (String type : arrConv) {
							//								System.out.println("gijunChartSetting load : "+type);
							if (!type.equals("")) {
								pBaseChart._cvm.baseLineType.add(Integer.parseInt(type));
							}
						}
					}
				}
			}

			if(strSettingInfos.length>1)
			{
				String strAutoInfo = strSettingInfos[1];
				String[] strAutoInfos = strAutoInfo.split("---");

				String[] lists;
				if(strAutoInfos.length>=cLen) {
					for(int i=0; i<cLen; i++) {
						strItem = strAutoInfos[i];
						lists = strItem.split("=");
						BaseChart pBaseChart = chartList.get(i);
						if(strItem==null || strItem.equals("")) {
							continue;
						}
						pBaseChart._cvm.autoTrendWaveType = Integer.parseInt(lists[0]);
						pBaseChart._cvm.autoTrendHighType = Integer.parseInt(lists[1]);
						pBaseChart._cvm.autoTrendLowType = Integer.parseInt(lists[2]);
						pBaseChart._cvm.autoTrendWType = Integer.parseInt(lists[3]);
						pBaseChart._cvm.preName = Integer.parseInt(lists[4]);
						pBaseChart._cvm.endName = Integer.parseInt(lists[5]);
					}
				}
			}
		}catch (Exception e){}
	}
	//2019. 08. 21 by hyh - 자동추세선 복원 <<
	public void addOneqToolbar() {
		try {
			//Screen
			final int width = frame.width();
			final int height = frame.height();
			sLocation = new int[2];
			layout.getLocationOnScreen(sLocation); // view 절대좌표

			final float rvWidth = COMUtil.getPixel(118);
			final float rvHeight = height;

			//Button Listener
			OnClickListener toolbarOneqListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					setAnalTool(v);
				}
			};

			//RelativeLayout
			rv_toolbar_oneq = new RelativeLayout(this.context);

			int resId = context.getResources().getIdentifier("toolbar_oneq_open", "drawable", context.getPackageName());
			rv_toolbar_oneq.setBackgroundResource(resId);
			rv_toolbar_oneq.setSelected(false); //Selected : SimpleSetting is shown
			rv_toolbar_oneq.setPadding((int) COMUtil.getPixel(3),(int) COMUtil.getPixel(12),0,0);

			final RelativeLayout.LayoutParams rv_Params = new RelativeLayout.LayoutParams((int) rvWidth, (int) rvHeight);
			rv_toolbar_oneq.setLayoutParams(rv_Params);
			rv_Params.leftMargin = width - (int)COMUtil.getPixel(36);
			rv_Params.topMargin = sLocation[1] + (int)COMUtil.getPixel(1);

			//추세
			RelativeLayout.LayoutParams params_chuse = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(60),
					(int) COMUtil.getPixel(80));

			Button btn_chuse = new Button(context);
			btn_chuse.setTag(COMUtil.TOOLBAR_CONFIG_LINE+"");
			btn_chuse.setOnClickListener(toolbarOneqListener);
			params_chuse.leftMargin = (int) COMUtil.getPixel(46);
			params_chuse.topMargin = (int) COMUtil.getPixel(10);

			btn_chuse.setLayoutParams(params_chuse);
			btn_chuse.setBackgroundResource(context.getResources().getIdentifier("c_option_oneq_01", "drawable", context.getPackageName()));
			rv_toolbar_oneq.addView(btn_chuse);

			//수평선
			RelativeLayout.LayoutParams params_horz = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(60),
					(int) COMUtil.getPixel(80));

			Button btn_horz = new Button(context);
			btn_horz.setTag(COMUtil.TOOLBAR_CONFIG_HORZ+"");
			btn_horz.setOnClickListener(toolbarOneqListener);
			params_horz.leftMargin = (int) COMUtil.getPixel(46);
			params_horz.topMargin = (int) COMUtil.getPixel(20) + ((int) COMUtil.getPixel(80));

			btn_horz.setLayoutParams(params_horz);
			btn_horz.setBackgroundResource(context.getResources().getIdentifier("c_option_oneq_02", "drawable", context.getPackageName()));
			rv_toolbar_oneq.addView(btn_horz);

			//수평선
			RelativeLayout.LayoutParams params_fibo = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(60),
					(int) COMUtil.getPixel(80));

			Button btn_fibo = new Button(context);
			btn_fibo.setTag(COMUtil.TOOLBAR_CONFIG_FIBORET+"");
			btn_fibo.setOnClickListener(toolbarOneqListener);
			params_fibo.leftMargin = (int) COMUtil.getPixel(46);
			params_fibo.topMargin = (int) COMUtil.getPixel(30) + ((int) COMUtil.getPixel(80))*2;

			btn_fibo.setLayoutParams(params_fibo);
			btn_fibo.setBackgroundResource(context.getResources().getIdentifier("c_option_oneq_03", "drawable", context.getPackageName()));
			rv_toolbar_oneq.addView(btn_fibo);

			//전체삭제
			RelativeLayout.LayoutParams params_del = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(60),
					(int) COMUtil.getPixel(40));

			Button btn_del = new Button(context);
			btn_del.setTag(COMUtil.TOOLBAR_CONFIG_ALL_ERASE+"");
			btn_del.setOnClickListener(toolbarOneqListener);
			params_del.leftMargin = (int) COMUtil.getPixel(46);
			params_del.topMargin = (int) COMUtil.getPixel(60) + ((int) COMUtil.getPixel(80))*3;

			btn_del.setLayoutParams(params_del);
			btn_del.setBackgroundResource(context.getResources().getIdentifier("c_option_oneq_04", "drawable", context.getPackageName()));
			rv_toolbar_oneq.addView(btn_del);

			//터치영역
			RelativeLayout.LayoutParams params_touch = new RelativeLayout.LayoutParams(
					(int) COMUtil.getPixel(46),
					(int) COMUtil.getPixel(80));

			Button btn_touch = new Button(context);
			params_touch.leftMargin = 0;
			params_touch.topMargin = (int) COMUtil.getPixel(10);

			btn_touch.setLayoutParams(params_touch);
			btn_touch.setBackgroundColor(Color.TRANSPARENT);
			rv_toolbar_oneq.addView(btn_touch);

			btn_touch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					m_bIsOpenedToolbar = !m_bIsOpenedToolbar;
					if(m_bIsOpenedToolbar)
						rv_Params.leftMargin = width - (int)rvWidth;
					else
						rv_Params.leftMargin = width - (int)COMUtil.getPixel(36);
					rv_toolbar_oneq.setLayoutParams(rv_Params);
				}
			});

			layout.addView(rv_toolbar_oneq);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showCrossLineLongClick(boolean bShow, Object object) {
		for(int i=0; i<chartList.size(); i++) {

			if (object instanceof NeoChart2) {	// NeoChart2 에서 호출 (생성)
				NeoChart2 baseChart = (NeoChart2)chartList.get(i);

				if (object.equals(baseChart)) {
					baseChart._cvm.isCrosslineMode = bShow;
					if (bShow) {
						baseChart.viewP.showCloseButton(false);	// 닫기 버튼 보이게
						baseChart.viewP.setVisibility(View.VISIBLE);
					} else {
						baseChart.viewP.showCloseButton(false);
						baseChart.viewP.setVisibility(View.GONE);
					}
					if (!bShow)
						baseChart.resetTitleBounds();
					baseChart.repaintAll();
				}
			} else if (object instanceof ViewPanel) {	// ViewPanel 에서 호출 (종료)
				NeoChart2 baseChart = (NeoChart2)chartList.get(i);

				if (object.equals(baseChart.viewP.viewP)) {
					baseChart._cvm.isCrosslineMode = bShow;
					if (bShow) {
						baseChart.viewP.showCloseButton(false);
						baseChart.viewP.setVisibility(View.VISIBLE);
					} else {
						baseChart.viewP.showCloseButton(false);
						baseChart.viewP.setVisibility(View.GONE);
					}
					if (!bShow) {
						baseChart.setShowTooltip(bShow);
						baseChart.resetTitleBounds();
					}
					baseChart.repaintAll();
				}
			}
		}
	}
	//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End
	public void hideTradeViewPanel(Object object) {
		for (int i = 0; i < chartList.size(); i++) {
			NeoChart2 baseChart = (NeoChart2) chartList.get(i);

			if (object.equals(baseChart.tradeViewP)) {
				baseChart.tradeViewP.showCloseButton(false);
				//2021.05.27 by hanjun.Kim - kakaopay - 닫기버튼 안보이게 수정 >>
				baseChart.tradeViewP.setVisibility(View.GONE);
				baseChart.resetTitleBounds();
				baseChart.repaintAll();
			}
		}
	}

	//2021.04.21 by hanjun.Kim - kakaopay - 이벤트배지닫힘 클릭이벤트 >>
	public void hideEventBadgeViewPanel(boolean bShow, Object object) {
		// TODO  badge 아이콘 사라지게 하고 이벤트 처리 등등
		baseChart.eventBadgeViewP.showCloseButton(false);
		//2021.05.27 by hanjun.Kim - kakaopay - 닫기버튼 안보이게 수정 >>
		baseChart.eventBadgeViewP.setVisibility(View.GONE);
		baseChart.repaintAll();
	}
	//2021.04.21 by hanjun.Kim - kakaopay - 이벤트배지닫힘 클릭이벤트 <<
}
