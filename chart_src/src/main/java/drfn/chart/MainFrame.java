package drfn.chart;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import drfn.UserProtocol;
import drfn.chart.base.Base;
import drfn.chart.base.Base11;
import drfn.chart.base.Base13;
import drfn.chart.base.BaseChart;
import drfn.chart.base.ChartLastsaveDBHelper;
import drfn.chart.base.DetailJipyoController;
import drfn.chart.base.IndicatorConfigView;
import drfn.chart.base.IndicatorConfigView_Mini;
import drfn.chart.base.IndicatorDefaultConfigView;
import drfn.chart.base.MainBase;
import drfn.chart.block.Block;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.comp.DRBottomDialog;
import drfn.chart.comp.DataField;
import drfn.chart.model.ChartViewModel;
import drfn.chart.net.NetClient;
import drfn.chart.net.RTHandler;
import drfn.chart.util.COMMCallback;
import drfn.chart.util.COMUtil;
import drfn.chart.util.COMUtil.OnPopupEventListener;
import drfn.chart.util.ChartColorSet;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;

public class MainFrame extends View implements OnPopupEventListener{
	public MainBase mainBase;
	int menuWidth = 5;
	int toggleButtonWidth = 10;

	public RelativeLayout layout;

	private Context context = null;
	public UserProtocol userProtocol;

	public String strFileName = null;
	public String strGijunFileName = null;
	public String strLocalFileName = "localTable";
	public String sendTrType="";
	//    private String strMin = "분▼";
//    private String strTic = "틱▼";
	private String strMin = "분";
	private String strTic = "틱";

	//2014.04.14 by lyk - 주기별 차트 설정
	public String ctlDataTypeName = null; //메인에서 설정된 주기값

    public Hashtable<String, Object> loadPeriodSaveItem = new Hashtable<String, Object>(); //2015.04.27 by lyl - 주기별 차트 설정 추가
    public Hashtable<String, Object> loadPeriodSizeInfo = new Hashtable<String, Object>(); //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
    //2014.04.14 by lyk - 주기별 차트 설정 end

	public boolean bIsLineChart = false;

	public boolean bIsLine2Chart = false;
	
	public boolean bIsNewsChart = false;

	public boolean bIsLineFillChart = false;	//2015. 10. 19 현재가차트 투명 처리

	public boolean bIsHighLowSign = false;

	public boolean bIsHideXscale = false;

    //차트설정
    public boolean bIsPeriodConfigSave = false; //default:false  //주기별 저장
    public boolean bIsAdjustedStock = true;  //수정주가
    public boolean bIsMinMaxShow = true;
    public boolean bIsyJonggaShow = true;
    public boolean bIsyScaleShow = true;
    public boolean bIsyJonggaCurrentPrice = true;	//2014.05.28 by LYH >> 눈금위 현재가 표시 방법 선택 추가
    public boolean bIsChuseLineValueTextShow = false;		//2015. 1. 16 추세선 수치조회 기본값을 감추기로 변경
    public boolean bIsDayDivisionLineShow = false;   //2013. 9. 3 분틱 날짜구분선 보이기 여부 처리
    public boolean bIsUpperLowerLimit = false;	//2015. 2. 24 차트 상하한가 표시
    public boolean bIsSetScreenViewPanel = true; //2013. 1. 15  지표설정창-일반설정-화면설정 플래그 (true : 뷰패널(수치조회창), false : 보조지표리스트(지표설정창))
    public boolean bIsBongCntShow = true; //현재 조회중인 봉수 표기
    public boolean bIsUsePaddingRight = true;
    public int g_nPaddingRight = 8;  //2016.09.08 by LYH >> 오른쪽 여백 설정 기능 , 2021.07.23 by lyk - kakaopay - 여백 수정
    public int g_nSkinType = COMUtil.SKIN_WHITE;
	public boolean bIsZoomCenterView = false; //2017.05.23 by PJM 사진줌방식
	public boolean bIsKoreanTime = true;  //2017.09.13 by pjm 한국시간 추가

	public boolean bIsUseHubong = false;
	public boolean bIsDetailScroll = false;

	public boolean bIsHighLowShow = true; //2020.03.26 차트 최대/최소 표시 설정 추가 - hjw
	public boolean bIsShowToolBarOneq = false;

	//2021.01.05 by HJW - X,YScale 격자선 옵션화 >>
	public boolean bIsShowYScaleLine = true;
	public boolean bIsShowXScaleLine = true;
	//2021.01.05 by HJW - X,YScale 격자선 옵션화 <<

	public boolean bIsCrossLineJongga = false; //2021.01.07 by HJW - 십자선 종가 따라가기 옵션화

	//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 >>
	public String strModYn = "N";      //만기보정
	public String strSkipTp = "0";     //제외기준 (0:없음 / 1:가격 / 2:거래량 / 3:둘다)
	public String strSkipTick = "50";  //제외기준 틱
	public String strSkipVol = "10";   //제외기준 거래량
	//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 <<

	public boolean bIsTechnical = false; //2019. 03. 14 by hyh - 테크니컬차트 개발

	public String strChartTitle = "";
	public String strChartTitleColor = "";

	public int nFxMarginType = -1; //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리

	public boolean bIsAnalToolbar = true;

    public String symbol="001200";

    public boolean m_bSimpleChart = false;

	public String m_storageSymbol = null;	//2017.04.19 종목코드에 따라 저장된 분석툴 로드

	public boolean m_bFXChart = false;

	public boolean isSimpleSet = false;
	public boolean bUseOneqToolBar = false;

	public boolean isOneTouchSet = false;
	public boolean bShowOneTouch = true; //2019.04.15 원터치 차트설정불러오기 추가 - lyj

	public boolean bIsTradeChart = false; //2019. 05. 30 by hyh - 매매연습차트 개발

	public boolean bShowBtnOpenAllScreen = false; //2019. 05. 08 by hyh - 전체보기버튼 보기/숨기기 옵션 추가
	public boolean bShowBtnOpenAlarmScreen = false; //2019. 05. 08 by hyh - 시세알람버튼 보기/숨기기 옵션 추가

	public boolean bCompConnectFut = false;	// true : on , false : off
	public String strCompFX   =	"B";	// B : 매수, S : 매도

	private String strInitGraphList = ""; //2019. 10. 14 by hyh - 초기화 상태일 때에는 저장하지 않도록 수정

	public boolean bIsOneQStockChart = false;

	public boolean bIsInitState = false; //2020.01.23 상단 초기화 저장안되는 오류 수정 - hjw

	public boolean bIsHideXScale = false;

	public int nMarketType = 0;

	public MainFrame(Context context , RelativeLayout layout) {
		super(context);
		this.layout = layout;
		this.context = context;
		COMUtil._mainFrame = this;

		if(COMUtil.apiMode) {
			RTHandler rt = new RTHandler();
			COMUtil.rt = rt;
		}

        g_nSkinType = COMUtil.skinType_Main;
	}

	String userID = "";
	String passWD = "";
	String serverIP = "";
	String serverPort = "";

	//2013. 1. 29 비교차트
//    private Vector<String> cmpArr = null;

	public void init() {

	}

	String menuIndex = "0-0";
	String defaultBase = "base11";
	public void initCode(String sCode, String fCode, String oCode, String uCode, String soCode) {
//        COMUtil.initHistoryCode(sCode, fCode, oCode, uCode, soCode);       
	}

	public void reSetCode(String sCode, String fCode, String oCode, String uCode, String soCode) {
		if (!sCode.equals("")) {
			mainBase.reSetBase("base11", sCode);
		}
	}

	public void setMain() {
	}


	//ImageView backImg;
	public void setUI() {
//        backImg = new ImageView(this.getContext());
//        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.background_main);
////        Bitmap resized = Bitmap.createScaledBitmap(image, COMUtil.chartWidth, COMUtil.chartHeight, true);  
//        backImg.setImageBitmap(image);  
//        backImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        backImg.layout(0, 0, COMUtil.chartWidth, COMUtil.chartHeight);
//        layout.addView(backImg);
		//2013.09.17 by LYH >> 패턴 그리기 추가.
		if(!COMUtil.base.equals("base11")) {
			defaultBase = COMUtil.base;
            this.bIsBongCntShow=false;
		}
		//2013.09.17 by LYH <<
		mainBase = new MainBase(context, this.layout);
		mainBase.setMainFrame(this);
		mainBase.setDefaultBase(defaultBase);
		mainBase.init();

		//2019. 10. 14 by hyh - 초기화 상태일 때에는 저장하지 않도록 수정 >>
		if (strInitGraphList.equals("")) {
			try {
				strInitGraphList = COMUtil.getGraphListStr();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		//2019. 10. 14 by hyh - 초기화 상태일 때에는 저장하지 않도록 수정 <<
	}

	public void startChart() {

	}

	public NetClient netClient;

	public boolean createSocket(Context context, String serverIP, String serverPort) {
		try {
			if(netClient != null) netClient = null;
			netClient = new NetClient(serverIP, null);
			if(connect(serverIP, serverPort)) {
				netClient.start();
				return true;
			} else {
				return false;
			}
		}
		catch(Exception exception) {
//        	System.out.println(exception.getMessage());
			return false;
		}

	}

	public boolean connect(String ip, String port) {
		int serverPort = 0;
		try {
			serverPort = Integer.parseInt(port);
		} catch(Exception e) {}

		return netClient.connect(ip, serverPort) == 0;
	}

	boolean start = true;
	String serverName = "";
	DataField[] f;
	int[][] dtt = {
			{8,8,2,8,8},
			{1,11,8}
	};
	int[][] fieldType = {
			{4,4,0,4,4},
			{4, 4,4}
	};
	int lStat = 0;
	public int getLoginStatus() {
		return lStat;
	}
	/* API 호출 함수 : 실시간 데이터 처리 */
	public void setRealData(byte[] data) {
		mainBase.setRealData(data);
	}
	public void setData(byte[] data) {
//	   // this.setEnabled(true);
//	    if(COMUtil.getSendTrType().equals("storageType") || COMUtil.divideStorageType==true) {
//	   		//Base11의 분할차트 함수 호출.
//    		Base11 base11 = (Base11)mainBase.baseP;
//    		base11.setStorageState();
//	    }
//	    
//	    if(data !=null && mainBase!=null) {
//	    	mainBase.setData(data);
//	    } else {
//	    	COMUtil.isProcessData=false;
//	        mainBase.stopProcessing();
//	    }
//	    
//	    COMUtil.setSendTrType(""); //초기화.
	}

	public void setData_Gijun(Hashtable<String, String> datas) {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setData_Gijun(datas);
	}

	public void setData_data(byte[] data, String[] strDates, double[] strOpens, double[] strHighs, double[] strLows, double[] strCloses, double[] strVolumes, double[] strValues, double[] strRights, double[] strRightRates, String strCandleType) {
		//2012.11.30 by LYH >> neochart select에서 세팅함.
	   	if(COMUtil._mainFrame != this)
    	{
            if(this.mainBase.baseP._chart != null)
            {
                if(!this.mainBase.baseP._chart._cvm.bIsLineChart && !this.mainBase.baseP._chart._cvm.bIsLine2Chart && !this.mainBase.baseP._chart._cvm.bIsLineFillChart  && !this.mainBase.baseP._chart._cvm.bIsMiniBongChart && !this.mainBase.baseP._chart._cvm.bInvestorChart)
                {
                    COMUtil._mainFrame = this;
                    COMUtil._neoChart = this.mainBase.baseP._chart;
                }
            }
            else
            {
                Base11 base11 = (Base11)mainBase.baseP;
                if(base11.compareChart != null)
                {
                    COMUtil._mainFrame = this;
                    COMUtil._neoChart = base11.compareChart;
                }
            }

    	}

//    	if(this.mainBase.baseP._chart != COMUtil._neoChart)
//    	{
//    		COMUtil._neoChart = this.mainBase.baseP._chart;
//    	}
		//2012.11.30 by LYH <<
		// this.setEnabled(true);
		Base11 base11 = (Base11)mainBase.baseP;
		if(this.sendTrType.equals("storageType") || base11.divideStorageType==true) {
			//Base11의 분할차트 함수 호출.
			base11.setStorageState();
		}

		//2015.05.26 by lyk - 주기별 차트 설정 (주기가 변경될때 마다 처리가 되야하기 때문에 이곳에서 처리하도록 함)
		//04.24 - "requestAddData" 추가
		if(bIsPeriodConfigSave && !COMUtil.isSyncJugi && !COMUtil.isSyncJongmok && !COMUtil.getSendTrType().equals("requestGapRevision") && !COMUtil.getSendTrType().equals("requestAddData") && COMUtil.chartMode!=COMUtil.COMPARE_CHART && (this.strFileName!=null && !this.strFileName.equals(""))) {
			//Base11의 분할차트 함수 호출.
			base11.setPeriodSaveStorageState();
		}
		//2015.05.26 by lyk - 주기별 차트 설정 end


		if(data !=null && mainBase!=null) {
			mainBase.setData_data(data, strDates, strOpens, strHighs, strLows,strCloses,strVolumes,strValues,strRights,strRightRates,strCandleType);
		} else {
			COMUtil.isProcessData=false;
			mainBase.stopProcessing();
		}

		this.sendTrType = "";
		//if(COMUtil._mainFrame == this)
		//	COMUtil.setSendTrType(""); //초기화.
	}

	public String getServerName() {
		return serverName;
	}

	public void sendTR(String tr, String param, int dataType) {
		if(netClient == null) return;
		netClient.sendTR((byte)0x40, tr, param, dataType);
//        System.out.println("tr1:"+tr);
	}

	public void sendTR(String tr, String param) {        //ProgressBar (O), Applet Enable(false)   
		if(netClient == null) return;
		netClient.sendTR((byte)0x40, tr, param);
		//if (mainBase != null) mainBase.startProcessing();
		//this.setEnabled(false);
	}


	/**
	 // void sendTREx(COMMCallback target, String tr, byte[] param, int nDataLen)
	 @author alzioyes.
	 @date   2010/12/15
	 @brief  NetClient.java의 sendTREx 호출하므로 NetClient참조.
	 @param target : 데이터를 콜백할 대상.
	 trCode : tr번호
	 trData : byte[] 형태의 데이터
	 nDataLen : 데이터 길이
	 @return 없음.
	 */
	public void sendTREx(COMMCallback target, String tr, byte[] param, int nDataLen) {
		if(netClient == null) return;
		netClient.sendTREx(target, tr, param, nDataLen);
		//if (mainBase != null) mainBase.startProcessing();
		//this.setEnabled(false);
	}

	public String getUserID() {
		return userID;
	}

	public void showWarning() {

		return;
	}

	public void stopAll() {
//    	this.layout.removeView(backImg);

		//2015. 1. 13 by lyk 동일지표 리스트 초기화
//    	COMUtil.setAddJipyoList(null);
		//2015. 1. 13 by lyk 동일지표 리스트 초기화 end

		//2019. 08. 21 by hyh - 자동추세선 복원 >>
//		COMUtil.autoTrendWaveType=0;
//		COMUtil.autoTrendHighType=0;
//		COMUtil.autoTrendLowType=0;
//		COMUtil.autoTrendWType=0;
		//2019. 08. 21 by hyh - 자동추세선 복원 <<

		closePopup();

		if (netClient != null) netClient.disconnect();
		if (mainBase !=  null) mainBase.removeBaseV();
		closeApiPopup(); //2016.11.10 by pjm << 가로모드 팝업창 on상태에서 세로모드로 전환시 팝업창 안닫히는 부분 조치
//        COMUtil.destroy();
//        PctrManager.notifyMainFrameStopAll();
	}

	//API 차트크기변경
	public void resizeChart(View view) {
		//this.closeApiPopup();
//		this.layout = (RelativeLayout)view;
		//chart frame
		int width = 0;
		int height = 0;
		RelativeLayout.LayoutParams chartParam = null;
		//2024.05.16 by SJW - 상단 티커 영역 사이즈에 따라 차트 resize 하도록 수정 >>
		if(this.mainBase.baseP instanceof Base11)
		{
			this.mainBase.baseP.resizeChart(view);
		}
		//2024.05.16 by SJW - 상단 티커 영역 사이즈에 따라 차트 resize 하도록 수정 <<

		try {
			chartParam = (RelativeLayout.LayoutParams)layout.getLayoutParams();
			if (chartParam == null) return;
			this.layout = (RelativeLayout)view;
			width = chartParam.width;
			height = chartParam.height;
		} catch (Exception e) {
			return;
//			width = COMUtil.chartWidth;
//			height = COMUtil.chartHeight;
		}
		if(width!=0 && height!=0) {
			COMUtil.chartWidth=width;
			COMUtil.chartHeight=height;
		}

//		//설정화면 회전시 크기 변경
////		LinearLayout viewGroup = (LinearLayout)COMUtil.apiView.findViewWithTag("indicator");
////		if(viewGroup!=null) viewGroup.setLayoutParams(chartParam);
//		LinearLayout indicatorLayout = new LinearLayout(context);
//		//2012. 7. 17 레이아웃 width, height 조절
//		indicatorLayout.setTag(COMUtil.INDICATOR_LAYOUT);
//		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
//		indicatorLayout.setLayoutParams(params);
//
//		LinearLayout layout = (LinearLayout)indicatorLayout.findViewWithTag(COMUtil.INDICATOR_LAYOUT);
//		layout.setLayoutParams(chartParam);
//
//		if(COMUtil.apiMode && indicatorLayout!=null) {
////    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);
//			saveStatus(null);
//
//			if (indicatorConfigView != null) {
//				indicatorConfigView.closePopupViews();    //2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
//			}
//
//			indicatorLayout.removeAllViews();
//			COMUtil.apiLayout.removeView(indicatorLayout);
//			indicatorLayout = null;
//
//		}
//
//		if(indicatorPopup!=null)
//		{
////			indicatorConfigView.resizeChart();
//			//차트저장
//			COMUtil.recycleDrawable(indicatorPopup.getBackground());
//			indicatorPopup.setBackgroundDrawable(null);
//			saveStatus(null);
//			indicatorPopup.dismiss();
//			indicatorPopup = null;
//		}
//
//		if(indicatorMiniPopup != null)
//		{
//			COMUtil.recycleDrawable(indicatorMiniPopup.getBackground());
//			indicatorMiniPopup.setBackgroundDrawable(null);
//			indicatorMiniPopup.dismiss();
//			indicatorMiniPopup = null;
//		}
//
//		//2014. 4. 7 차트 리사이즈 될 때 차트저장하기설정창 닫지 않게 수정 : 태블릿일때만 닫게 수정
//		if(COMUtil.savechartPopup != null && COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			COMUtil.savechartPopup.dismiss();
//			COMUtil.savechartPopup = null;
//		}

		//2024.05.16 by SJW - 상단 티커 영역 사이즈에 따라 차트 resize 하도록 수정 >>
//		if(this.mainBase.baseP instanceof Base11)
//		{
//			this.mainBase.baseP.resizeChart(view);
//		}
		//2024.05.16 by SJW - 상단 티커 영역 사이즈에 따라 차트 resize 하도록 수정 <<
	}

	// API 설정 화면 처리
	public PopupWindow indicatorPopup=null;
	public PopupWindow indicatorMiniPopup = null;
	PopupWindow syncPopup=null;
	PopupWindow dividePopup=null;
	PopupWindow saveloadPopup=null;
	PopupWindow divideAndSyncPopup=null;
	PopupWindow dividePopupBack=null;
	public void selectChartMenuFromParent(Hashtable<String, Object> dic) {
		if(COMUtil._mainFrame != this)
		{
			COMUtil._mainFrame = this;
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}
		if(this.mainBase.baseP._chart != COMUtil._neoChart)
		{
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}
		//frame info
//    	Hashtable<String, Integer> frame = (Hashtable<String, Integer>)dic.get("frame");
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)dic.get("frame");
//		ViewGroup apiView = (ViewGroup)dic.get("apiView");
		ViewGroup apiView = COMUtil.apiView;
//    	apiView.setLayoutParams(params);

		//팝업창 상단의 삼각형 이미지 x position.
		int triXpos = 0;
		try {
			triXpos = Integer.parseInt((String)dic.get("triXpos"));
		} catch(Exception e) {

		}

		//
		if(dic.get("chartSaveLoadFileName") !=null)
			this.strLocalFileName = (String)dic.get("chartSaveLoadFileName");


		//tag info
		Integer tag = (Integer)dic.get("tag");
		//tag = COMUtil._TAG_COMPARISON_EDIT_CONFIG;

//		if (tag == COMUtil._TAG_TOOL_CONFIG)
//			tag = COMUtil._TAG_INDICATOR_CONFIG_ONEQ;

		if(tag.intValue() == COMUtil._TAG_INDICATOR_CONFIG || tag.intValue() == COMUtil._TAG_CHARTSET_CONFIG || tag.intValue() == COMUtil._TAG_CHARTSET_NO_PERIOD) {    //2017.10.24 by pjm 차트설정-주기설정제외 >>
			final int requestedOrientation = COMUtil._chartMain.getRequestedOrientation();

			COMUtil.saveLastState(COMUtil._mainFrame.strFileName+"_original");//2020.05.14 설정 취소
			indicatorParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			indicatorLayout = new RelativeLayout(COMUtil._mainFrame.getContext());
			indicatorLayout.setLayoutParams(indicatorParams);
			indicatorLayout.setTag("indicator");
			indicatorLayout.setFocusable(true);
			indicatorLayout.setBackgroundColor(Color.argb(70, 0, 0, 0));
			indicatorLayout.setGravity(Gravity.TOP | Gravity.LEFT);
			indicatorLayout.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (indicatorMiniPopup != null) {
						indicatorMiniPopup.dismiss();
						indicatorMiniPopup = null;
					}

					((Base11) mainBase.baseP).resetSimpleSettingButton();
				}
			});

			indicatorConfigView = new IndicatorConfigView(getContext(), indicatorLayout, triXpos, 0);
			indicatorConfigView.setOnEventListener((Base11)mainBase.baseP);	//2015. 2. 24 차트 상하한가 표시
			((Base11)mainBase.baseP).onLoadPeriodValue();

			//팝업 띄우기
			indicatorPopup = new PopupWindow(indicatorLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			indicatorPopup.setOutsideTouchable(true);
			indicatorPopup.setBackgroundDrawable(new BitmapDrawable());
			indicatorPopup.showAtLocation(apiView, Gravity.TOP | Gravity.LEFT, 0, 0);

			//차트설정 닫기 추가
			int layoutResId = context.getResources().getIdentifier("btnchartsettingclose", "id", context.getPackageName());
			final Button btnClose = (Button)indicatorLayout.findViewById(layoutResId);
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//차트저장
					if(indicatorConfigView.getInitBtnType() != 0)
						indicatorConfigView.savePeriodValues();

					//2013. 8. 19 지표설정창 분틱설정에서 소프트키보드 올라와있는 상태에서 닫으면 키보드 남아있음>>
					indicatorConfigView.closePeriodViewKeyboard();
					//2013. 8. 19 지표설정창 분틱설정에서 소프트키보드 올라와있는 상태에서 닫으면 키보드 남아있음>>

					bIsInitState = true;
					saveStatus(null);
					COMUtil.sendTR(""+COMUtil._TAG_SET_PERIOD_UNITS);

					if(COMUtil.apiMode && indicatorLayout!=null) {
						indicatorLayout.removeAllViews();
						addRemovePopupView("2", indicatorLayout);
						indicatorLayout = null;

					}

					if(indicatorPopup!=null) {
						indicatorPopup.dismiss();
						indicatorPopup = null;
					}
				}
			});

			//차트초기화 이벤트 처리
			layoutResId = context.getResources().getIdentifier("btnchartsettingreset", "id", context.getPackageName());
			Button btnReset = (Button)indicatorLayout.findViewById(layoutResId);
			btnReset.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//2013. 9. 12 초기화 대화창 UI 디자인>>

					int nInitType = indicatorConfigView.getInitBtnType();
					DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
					alert.show();alert.setNoButton("아니오", null);

					if(nInitType == 0)
					{
						alert.setMessage("지표설정 전체를 초기화 하시겠습니까?\n상세 지표설정도 초기화 됩니다.");
						alert.setYesButton("초기화",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									//initChart(requestedOrientation);
                                    initIndicator(requestedOrientation);

									//2019. 08. 21 by hyh - 자동추세선 복원 >>
									//COMUtil.saveGijunState("gijunChartSetting");	//2013. 10. 7 분석툴바 기준선 추가 후 초기화 한 다음에 화면 나갔다 들어오면 기준선이 다시 그려져있음
									//2019. 08. 21 by hyh - 자동추세선 복원 <<

									dialog.dismiss();	//팝업창 닫기
									Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
								}
							});
					}
					else if(nInitType == 1)
					{
						alert.setMessage("차트 일반설정 전체를 초기화 하시겠습니까?");
						alert.setYesButton("초기화",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										//initChart(requestedOrientation);
                                        COMUtil.resetConvenientList();
                                        ((Base11)mainBase.baseP).setSkinType(COMUtil.SKIN_WHITE);
                                        indicatorConfigView.initGeneral();
                                        ((Base11)mainBase.baseP).setFuncAllChart("setPaddingRight");
										dialog.dismiss();	//팝업창 닫기
                                        COMUtil.saveLastState("combChartSetting");
										Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
									}
								});
					}
					else if(nInitType == 2)
					{
						alert.setMessage("차트 분/틱설정 전체를 초기화 하시겠습니까?");
						alert.setYesButton("초기화",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										indicatorConfigView.initPeriod();
										dialog.dismiss();	//팝업창 닫기
										Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
									}
								});
					}
					else if(nInitType == 3)
					{
						alert.setMessage("차트 도구모음설정 전체를 초기화 하시겠습니까?");
						alert.setYesButton("초기화",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										indicatorConfigView.initAnalTool();
										indicatorConfigView.initToolbarList();
										dialog.dismiss();	//팝업창 닫기
										Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
									}
								});
					}


					COMUtil.g_chartDialog = alert;

					//2013. 9. 12 초기화 대화창 UI 디자인>>
				}
			});

			layoutResId = context.getResources().getIdentifier("btn_config_cancel", "id", context.getPackageName());
			Button btnBottomCancel = (Button)indicatorLayout.findViewById(layoutResId);
			btnBottomCancel.setTypeface(COMUtil.typefaceMid);

			btnBottomCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loadLastSaveState(COMUtil._mainFrame.strFileName+"_original");
					indicatorConfigView.closePeriodViewKeyboard();
					//2013. 8. 19 지표설정창 분틱설정에서 소프트키보드 올라와있는 상태에서 닫으면 키보드 남아있음>>

					bIsInitState = true;

					if(COMUtil.apiMode && indicatorLayout!=null) {
						indicatorLayout.removeAllViews();
						addRemovePopupView("2", indicatorLayout);
						indicatorLayout = null;

					}

					if(indicatorPopup!=null) {
						indicatorPopup.dismiss();
						indicatorPopup = null;
					}
					//btnClose.performClick();
				}
			});

			layoutResId = context.getResources().getIdentifier("btn_config_confirm", "id", context.getPackageName());
			Button btnBottomConfirm = (Button)indicatorLayout.findViewById(layoutResId);
			btnBottomConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnClose.performClick();
				}
			});
		}

		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 start
		if(tag.intValue() == COMUtil._TAG_INDICATOR_CONFIG_ONEQ ) {

			DRBottomDialog drBottomDialog = new DRBottomDialog(COMUtil._mainFrame.getContext(),7);
			drBottomDialog.showChartSettingListPopup(COMUtil._mainFrame.getContext());
			drBottomDialog.show();

		}
		// 2020.04.29 by JJH >> 차트 세팅 UI 이벤트 적용 end

		//2015. 11. 2 미니 설정창 추가>>
		if(tag.intValue() == COMUtil._TAG_INDICATORMINI_CONFIG) {
			indicatorLayout_mini = new RelativeLayout(COMUtil._mainFrame.getContext());
			indicatorLayout_mini.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			indicatorLayout_mini.setTag("indicator_mini");
			indicatorLayout_mini.setFocusable(true);
			//indicatorLayout_mini.setBackgroundColor(Color.argb(70, 0, 0, 0));
			indicatorLayout_mini.setBackgroundColor(Color.argb(0, 0, 0, 0));
			indicatorLayout_mini.setGravity(Gravity.TOP | Gravity.LEFT);
			indicatorLayout_mini.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if (indicatorMiniPopup != null) {
						indicatorMiniPopup.dismiss();
						indicatorMiniPopup = null;
					}

					((Base11) mainBase.baseP).resetSimpleSettingButton();
				}
			});

			//int nCloseImageViewMarginTop = (int)dic.get("closeImageViewMarginTop");
			int nCloseImageViewMarginTop = 0;

			indicatorConfigView_mini = new IndicatorConfigView_Mini(COMUtil._mainFrame.getContext(), indicatorLayout_mini, params, 0);
			indicatorConfigView_mini.setOnMiniSettingViewEventListener(this);
//			indicatorConfigView_mini.setCloseImageViewMarginTop(nCloseImageViewMarginTop);

			//팝업 띄우기
			indicatorMiniPopup = new PopupWindow(indicatorLayout_mini, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			indicatorMiniPopup.setOutsideTouchable(true);
			indicatorMiniPopup.setBackgroundDrawable(new BitmapDrawable());
			indicatorMiniPopup.showAtLocation(apiView, Gravity.TOP | Gravity.LEFT, 0, 0);
			indicatorMiniPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
				@Override
				public void onDismiss() {
					indicatorMiniPopup = null;
					indicatorLayout_mini.performClick();
				}
			});
		}
		//2015. 11. 2 미니 설정창 추가<<
		else if(tag.intValue() == COMUtil._TAG_DIVIDECHART_CONFIG) {
			//2013. 7. 25 분할및동기화메뉴 통합 선택창 추가 >>
//    		Base11 base11 = (Base11)mainBase.baseP;
//    		//멀티차트면 멀티/동기화3개 선택창을 호출 
//    		if(base11.isMultiChart())
//    		{
//    			//크기와 위치 재설정
//				params.width =(int)COMUtil.getPixel(125);
//				params.height = (int)COMUtil.getPixel(211);   
//				params.leftMargin+=(int)COMUtil.getPixel(98);
//    			
//    			RelativeLayout divideAndSyncLayout = setDivideAndSyncMenu(null, params, -1, apiView);
//    	    	divideAndSyncPopup = new PopupWindow(divideAndSyncLayout, params.width, params.height, true);
//    	    	divideAndSyncPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
//    			divideAndSyncPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
//    			
//    			divideAndSyncPopup.showAtLocation(apiView, Gravity.TOP|Gravity.LEFT, params.leftMargin, params.topMargin);
//    		}
//    		//2013. 7. 25 분할및동기화메뉴 통합 선택창 추가 >>
//    		else
//    		{
			//원분할이면 기존대로 멀티차트설정창 호출

			setDivideConfig(null, params, triXpos);

//			//popupView로 변경
//			//    		if(dividePopup==null) {
////	    			dividePopup = new PopupWindow(divideLayout, params.width, params.height, true);
////			dividePopup = new PopupWindow(divideLayout, COMUtil.g_nDisWidth, COMUtil.g_nDisHeight, true);	//2014. 10. 14 popupwindow 배경 반투명색
//            dividePopup = new PopupWindow(divideLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
//			dividePopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
//			dividePopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
//			//    		} else {
//			//    			dividePopup.dismiss();
//			//    			dividePopup = null;
//			//    			return;
//			//    		}
//			//팝업 띄우기
//			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >> : 화면가운데로 띄우기
////	    		dividePopup.showAtLocation(apiView, Gravity.TOP|Gravity.LEFT, params.leftMargin, params.topMargin);
//			dividePopup.showAtLocation(apiView, Gravity.CENTER, 0, 0);
			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>

			//설정 닫기 추가
			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>  : setDivideConfig 에서 처리하게 변경. (모든 버튼기능은 이 함수에서 처리중)
//	    		int layoutResId = context.getResources().getIdentifier("btnchartsettingclose", "id", context.getPackageName());
//	    		Button btnClose = (Button)divideLayout.findViewById(layoutResId);
//	    		if(btnClose != null)
//	    		{
//		    		btnClose.setOnClickListener(new Button.OnClickListener() {
//		    			@Override
//		    			public void onClick(View v) {
//		    				// TODO Auto-generated method stub
//			    			COMUtil.recycleDrawable(dividePopup.getBackground());
//			    			dividePopup.setBackgroundDrawable(null);
//		    				dividePopup.dismiss();
//		    				dividePopup = null;
//		    			}
//		    		});
//	    		}
			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>
//    		}

		} else if(tag.intValue() == COMUtil._TAG_SAVELOADMENU_CONFIG) {
			setSaveload(null, params, triXpos);
			//popupView로 변경
//    		if(saveloadPopup==null) {
			saveloadPopup = new PopupWindow(this.saveloadLayout, COMUtil.g_nDisWidth, COMUtil.g_nDisHeight, true);
			saveloadPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			saveloadPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
//    		} else {
//    			saveloadPopup.dismiss();
//    			saveloadPopup = null;
//    			return;
//    		}
			//팝업 띄우기
			saveloadPopup.showAtLocation(apiView, Gravity.LEFT|Gravity.TOP, COMUtil.g_nDisWidth, COMUtil.g_nDisHeight);

			//설정 닫기 추가
			int layoutResId = context.getResources().getIdentifier("btnchartsettingclose", "id", context.getPackageName());
			Button btnClose = (Button)saveloadLayout.findViewById(layoutResId);
			if(btnClose != null)
			{
				btnClose.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						COMUtil.recycleDrawable(saveloadPopup.getBackground());
						saveloadPopup.setBackgroundDrawable(null);
						saveloadPopup.dismiss();
						saveloadPopup = null;
					}
				});
			}

		} else if(tag.intValue() == COMUtil._TAG_SYNC_CONFIG) {
//	    	apiView.addView(getSyncMenu(this, params), params);
//	    	setSyncMenu(params);
			//popupView로 변경
//    		if(syncPopup==null) {
			View syncMenu = getSyncMenu(this, params, triXpos);
			syncPopup = new PopupWindow(syncMenu, params.width, params.height, true);
			syncPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			syncPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
//    		} else {
//    			syncPopup.dismiss();
//    			syncPopup = null;
//    			return;
//    		}
			//팝업 띄우기
			syncPopup.showAtLocation(apiView, Gravity.TOP|Gravity.LEFT, params.leftMargin, params.topMargin);

			//설정 닫기 추가
			int layoutResId = context.getResources().getIdentifier("btnchartsettingclose", "id", context.getPackageName());
			Button btnClose = (Button)syncMenu.findViewById(layoutResId);
			if(btnClose != null)
			{
				btnClose.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						COMUtil.recycleDrawable(syncPopup.getBackground());
						syncPopup.setBackgroundDrawable(null);
						syncPopup.dismiss();
						syncPopup = null;
					}
				});
			}
		}
		//2013. 1. 25 분틱 리스트뷰 오픈 추가
		else if(tag.intValue() == COMUtil._TAG_LOADCONTROL_CONFIG) {
			COMUtil.loadChart(null);
		}
		else if(tag.intValue() == COMUtil._TAG_SET_COMBOMIN) {
			ListView list = new ListView(context);
			list = setComboBoxList("period_minute");
			setComboBox(list, params, true);
		}
		else if(tag.intValue() == COMUtil._TAG_SET_COMBOTIC) {
			ListView list = new ListView(context);
			list = setComboBoxList("period_tic");
			setComboBox(list, params, true);
		}
		else if(tag.intValue() == COMUtil._TAG_SET_COMBODWM) {
			ListView list = new ListView(context);
			list = setComboBoxList("periodDWM");
			setComboBox(list, params, true);
		}
		else if(tag.intValue() == COMUtil._TAG_SET_COMBOALL) {
			ListView list = new ListView(context);
			list = setComboBoxList("period_all");
			setComboBox(list, params, true);
		}
		//2013. 1. 29 비교차트 팝업
		else if(tag.intValue() == COMUtil._TAG_COMPARISON_EDIT_CONFIG) {
//	    	openEditView();
			COMUtil.showCompareSetting();
		}
		else if(tag.intValue() == COMUtil._TAG_TOOL_CONFIG) {
			setAnalTool();
		}

	}

	public void initChart(int requestedOrientation) {
		// TODO Auto-generated method stub
		mainBase.initChart("pressInitBtn");


		//2013. 1. 17 분틱설정 초기화 루틴 
		if(indicatorConfigView != null)
		{
			//COMUtil.resetBunTicPeriodList(false);
//			if(!COMUtil.bIsForeignFuture)
				indicatorConfigView.initPeriodValues();
			//2013. 1. 22 초기화시 일반설정 플래그 초기화
			indicatorConfigView.initNormarSetValues();
			COMUtil.sendTR(""+COMUtil._TAG_SET_PERIOD_UNITS);
		}
		//차트저장


		if(COMUtil.apiMode && indicatorLayout!=null) {
//    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);
			indicatorLayout.removeAllViews();
			COMUtil.apiLayout.removeView(indicatorLayout);
			indicatorLayout = null;

		}

		if(indicatorPopup!=null) {
			COMUtil.recycleDrawable(indicatorPopup.getBackground());
			indicatorPopup.setBackgroundDrawable(null);
			indicatorPopup.dismiss();
			indicatorPopup = null;
		}

		saveStatus(null);
		//자동회전 방지기능을 화면이 닫힐때 원래 requestedOrientation 정보로 설정한다.
		COMUtil._chartMain.setRequestedOrientation(requestedOrientation);
	}

    private void initIndicator(int requestedOrientation) {
        // TODO Auto-generated method stub
        mainBase.initChart("pressIndicatorInitBtn");
//        //2013. 1. 17 분틱설정 초기화 루틴
//        if(indicatorConfigView != null)
//        {
//            //COMUtil.resetBunTicPeriodList(false);
//            indicatorConfigView.initPeriodValues();
//            //2013. 1. 22 초기화시 일반설정 플래그 초기화
//            indicatorConfigView.initNormarSetValues();
//            COMUtil.sendTR(""+COMUtil._TAG_SET_PERIOD_UNITS);
//        }
//        //차트저장


        if(COMUtil.apiMode && indicatorLayout!=null) {
//    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);
            indicatorLayout.removeAllViews();
            COMUtil.apiLayout.removeView(indicatorLayout);
            indicatorLayout = null;

        }

        if(indicatorPopup!=null) {
            COMUtil.recycleDrawable(indicatorPopup.getBackground());
            indicatorPopup.setBackgroundDrawable(null);
            indicatorPopup.dismiss();
            indicatorPopup = null;
        }

        saveStatus(null);
        //자동회전 방지기능을 화면이 닫힐때 원래 requestedOrientation 정보로 설정한다.
        COMUtil._chartMain.setRequestedOrientation(requestedOrientation);
    }

	//API 에서 호출되는 함수
	public void setIndicatorDefaultConfig(View v) {
//		if(!COMUtil.isJipyoBtnState) return;

		IndicatorDefaultConfigView indicatorDefaultConfigView = new IndicatorDefaultConfigView(this.getContext(), this.layout);
		layout.addView(indicatorDefaultConfigView);
	}
	public void setIndicatorConfig(View v) {
//		if(!COMUtil.isJipyoBtnState) return;
		if(COMUtil.apiMode) {
//			IndicatorConfigView indicatorConfigView = new IndicatorConfigView(this.getContext(), this.layout);
////			layout.addView(indicatorConfigView);
//			RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//					COMUtil.chartWidth, COMUtil.chartHeight);
//			params.leftMargin=0;
//			params.topMargin=0;
//			COMUtil.apiView.addView(indicatorConfigView, params);


		} else {
			IndicatorConfigView indicatorConfigView = new IndicatorConfigView(this.getContext(), this.layout, 0, 0);
			layout.addView(indicatorConfigView);
		}
	}

	public RelativeLayout indicatorLayout = null;
	public RelativeLayout indicatorLayout_mini = null;
	public IndicatorConfigView indicatorConfigView = null;
	public IndicatorConfigView_Mini indicatorConfigView_mini = null;
	//2012. 9. 12 캔들설정창 콤보박스의 리스트뷰 배치할 좌표설정을 위해서 지표설정창의 layoutparams 를 저장
	public RelativeLayout.LayoutParams indicatorParams = null;
	private String m_strPeriodValue;	//2015. 12. 10 맵-차트간 분틱값 동기화
	public View getIndicatorConfig(View v, RelativeLayout.LayoutParams params, int triXpos) {
		indicatorLayout = new RelativeLayout(this.getContext());
		indicatorLayout.setTag("indicator");
		indicatorLayout.setLayoutParams(params);
		indicatorParams = params;

		indicatorConfigView = new IndicatorConfigView(v.getContext(), indicatorLayout, triXpos, 0);
		indicatorConfigView.setOnEventListener((Base11)mainBase.baseP);	//2015. 2. 24 차트 상하한가 표시

		return indicatorLayout;
	}

	public View getChartSettingConfig(View v, RelativeLayout.LayoutParams params, int triXpos) {
		indicatorLayout = new RelativeLayout(this.getContext());
		indicatorLayout.setTag("indicator");
		indicatorLayout.setLayoutParams(params);
		indicatorParams = params;

		indicatorConfigView = new IndicatorConfigView(v.getContext(), indicatorLayout, triXpos, 1);
		indicatorConfigView.setOnEventListener((Base11)mainBase.baseP);	//2015. 2. 24 차트 상하한가 표시

		return indicatorLayout;
	}
	//2017.10.24 by pjm 차트설정-주기설정제외 >>
	public View getChartSettingNoPeiord(View v, RelativeLayout.LayoutParams params, int triXpos) {
		indicatorLayout = new RelativeLayout(this.getContext());
		indicatorLayout.setTag("indicator");
		indicatorLayout.setLayoutParams(params);
		indicatorParams = params;

		indicatorConfigView = new IndicatorConfigView(v.getContext(), indicatorLayout, triXpos, 2);
		indicatorConfigView.setOnEventListener((Base11)mainBase.baseP);	//2015. 2. 24 차트 상하한가 표시

		return indicatorLayout;
	}
	//2017.10.24 by pjm 차트설정-주기설정제외 <<

	//API 분할설정 메뉴
	RelativeLayout divideLayout=null;
	ArrayList<Button> arDivideIcons = new ArrayList<Button>();
	public void setDivideConfig(View v, RelativeLayout.LayoutParams params, int triXpos) {
		WindowManager wm = COMUtil._chartMain.getWindowManager();
		Display display = wm.getDefaultDisplay();

		int width = 0;	//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업)
		int height = display.getHeight();

		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) start
		Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
//		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//		{
//			//가로
//			width = (int) COMUtil.getPixel(360);
//		}else{
//			//세로
			width = (int) COMUtil.getPixel(324);
//		}
		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) end
		divideLayout = new RelativeLayout(COMUtil._mainFrame.getContext());
		divideLayout.setLayoutParams(new LayoutParams(width, height));
		divideLayout.setTag(COMUtil.DIVIDE_LAYOUT);
		divideLayout.setFocusable(true);
		divideLayout.setBackgroundColor(Color.argb(70, 0, 0, 0));
		divideLayout.setGravity(Gravity.BOTTOM);
		divideLayout.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (dividePopup != null) {
					dividePopup.dismiss();
					dividePopup = null;
				}
				if (dividePopupBack != null) {
					dividePopupBack.dismiss();
					dividePopupBack = null;
				}
			}
		});

		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = context.getResources().getIdentifier("devideviewpopup", "layout", context.getPackageName());
		View divideView = factory.inflate(layoutResId, null);

		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) start
//		LayoutParams lp = new LayoutParams(COMUtil.g_nDisWidth, LayoutParams.WRAP_CONTENT);
		LayoutParams lp = new LayoutParams(COMUtil.g_nDisWidth, height);
		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) end
		divideView.setLayoutParams(lp);

//		divideLayout.addView(divideView);

		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) start
//		dividePopupBack = new PopupWindow(divideLayout, width, LayoutParams.MATCH_PARENT);
		dividePopupBack = new PopupWindow(divideLayout, display.getWidth(), LayoutParams.MATCH_PARENT);
		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) end
		dividePopupBack.showAtLocation(COMUtil.apiView, Gravity.NO_GRAVITY, 0, 0);

		//팝업 띄우기
		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) start
		dividePopup = new PopupWindow(divideView, width, LayoutParams.WRAP_CONTENT);
		//2020.05.08 by JJH >> 가로모드 작업 (차트 분할설정 팝업) end
//		dividePopup.setOutsideTouchable(true);
//		dividePopup.setBackgroundDrawable(new BitmapDrawable());
//		dividePopup.setAnimationStyle(context.getResources().getIdentifier("AnimationPopupStyle", "style", context.getPackageName()));
//		dividePopup.showAtLocation(COMUtil.apiView, Gravity.BOTTOM, 0, 0);
		dividePopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.CENTER, 0, 0);

		layoutResId = context.getResources().getIdentifier("ll_divide", "id", context.getPackageName());
		LinearLayout ll_divide = (LinearLayout) divideView.findViewById(layoutResId);

		COMUtil.setGlobalFont(ll_divide);

//    	if(divideLayout==null) {
//		divideLayout = new RelativeLayout(context);
//		divideLayout.setTag(COMUtil.DIVIDE_LAYOUT);
//		if(params==null) {
//			params =new RelativeLayout.LayoutParams(
//					(int)COMUtil.getPixel(200), (int)COMUtil.getPixel(200));
//			if(v!=null) {
//				params.leftMargin=v.getLeft()-params.width/2+v.getWidth()/2;
//				params.topMargin=v.getBottom();
//			}
//		}
//
//		//2014. 10. 14 popupwindow 배경 반투명색>>
////			divideLayout.setLayoutParams(params);
//		divideLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		divideLayout.setBackgroundColor(Color.argb(70, 0, 0, 0));
//		divideLayout.setGravity(Gravity.CENTER);
//		divideLayout.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if(dividePopup!=null) {
//					dividePopup.dismiss();
//					dividePopup = null;
//				}
//			}
//		});
//		//2014. 10. 14 popupwindow 배경 반투명색<<
//
//		LayoutInflater factory = LayoutInflater.from(context);
//		View divideView = null;
//
//		int layoutResId;
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			//삼각형 이미지 추가
//			ImageView viewArrow = new ImageView(context);
//			layoutResId = context.getResources().getIdentifier("round_desc_tri", "drawable", context.getPackageName());
//			viewArrow.setImageResource(layoutResId);
//			RelativeLayout.LayoutParams xparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			xparams.leftMargin = triXpos;
//			xparams.topMargin = - xparams.height;
//			viewArrow.setLayoutParams(xparams);
//			divideLayout.addView(viewArrow);
//
//			layoutResId = context.getResources().getIdentifier("devideview_tab", "layout", context.getPackageName());
//			divideView = factory.inflate(layoutResId, null);
////				divideView.setLayoutParams(params);
//
//			//2013. 6. 11 팝업창 phone용으로 변경 >>
//			layoutResId = context.getResources().getIdentifier("pop_back_divide", "drawable", context.getPackageName());
//			Bitmap bmp=BitmapFactory.decodeResource(getResources(), layoutResId); // 비트맵 이미지를 만든다.
//			int width= params.width; // 가로 사이즈 지정
//			int height= params.height; // 세로 사이즈 지정
//			Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, (int)COMUtil.getPixel(width), (int)COMUtil.getPixel(height), true); // 이미지 사이즈 조정
//			Drawable drawable = new BitmapDrawable(resizedbitmap);
//			divideView.setBackgroundDrawable(drawable); // 이미지뷰에 조정한 이미지 넣기
//			//2013. 6. 11 팝업창 phone용으로 변경 <<
//
//		} else {
//			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>
////				layoutResId = context.getResources().getIdentifier("devideview", "layout", context.getPackageName());
//			layoutResId = context.getResources().getIdentifier("devideviewpopup", "layout", context.getPackageName());
//			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>
//
//			divideView = factory.inflate(layoutResId, null);
//			divideView.setLayoutParams(params);
//
//			Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
////			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
////			{
////				layoutResId = context.getResources().getIdentifier("savetop", "id", context.getPackageName());
////				RelativeLayout rl_Top = (RelativeLayout) divideView.findViewById(layoutResId);
////				rl_Top.setVisibility(View.GONE);
////			}
////				else
////				{
//			//세로
//			//2013. 7. 24 멀티차트설정창 새 디자인 적용>>
////					layoutResId = context.getResources().getIdentifier("pop_back_divide", "drawable", context.getPackageName());
//			layoutResId = context.getResources().getIdentifier("pop_back", "drawable", context.getPackageName());
//			//2013. 7. 24 멀티차트설정창 새 디자인 적용>>
////				}
////				layoutResId = context.getResources().getIdentifier("pop_back", "drawable", context.getPackageName());
////				Bitmap bmp=BitmapFactory.decodeResource(getResources(), layoutResId); // 비트맵 이미지를 만든다.
////				int width=(int)(params.width); // 가로 사이즈 지정
////				int height=(int)(params.height); // 세로 사이즈 지정
////				Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, (int)COMUtil.getPixel(width), (int)COMUtil.getPixel(height), true); // 이미지 사이즈 조정
////				Drawable drawable = new BitmapDrawable(resizedbitmap);
////				divideView.setBackgroundDrawable(drawable); // 이미지뷰에 조정한 이미지 넣기
//			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >> : 뒷배경 xml 에서 처리
////				divideView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
////				divideView.setBackgroundResource(layoutResId);
//			//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>
//		}
////			divideView.setLayoutParams(params);
//		//이미지 줄이기.(OutOfMemory 해결)
//		//		layoutResId = context.getResources().getIdentifier("popupbg1", "drawable", context.getPackageName());
//		//		Drawable drawable = COMUtil.getSmallBitmap(layoutResId);
//		//		divideView.setBackgroundDrawable(drawable);

		//분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
		int[] btnResId = new int[19];
		String index="";
		View view = null;
		int layoutId;

		//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경>>
		if(arDivideIcons.size() > 0)
		{
			arDivideIcons.clear();
		}
		//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경<<

		for(int i = 0; i < 2; i++){
			layoutResId = context.getResources().getIdentifier("devideview_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvDevideView = (TextView)divideView.findViewById(layoutResId);
			tvDevideView.setTypeface(COMUtil.typefaceMid);
		}

		final View _divideView = divideView;
		for(int i=0; i<btnResId.length; i++) {
			if((i+1)<10) index = "0"+(i+1);
			else index = ""+(i+1);
			btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
			view = divideView.findViewById(btnResId[i]);
			if (view != null)
			{
				switch(i)
				{
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
						//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경>>
						//분할아이콘 배열에 추가
						arDivideIcons.add((Button)view);

						//현재 분할상태의 아이콘이면 선택상태로 
						Base11 base11 = (Base11)mainBase.baseP;
						if(Integer.parseInt((String)view.getTag()) == base11.getDivideType())
						{
							view.setSelected(true);
						}
						//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경<<

						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
//				        		COMUtil.setDivideChart(v);

								//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경>>
								Button btn = (Button)v;
								//터치한 아이콘을 선택상태로
								for(int i = 0; i < arDivideIcons.size(); i++)
								{
									if(arDivideIcons.get(i) == btn)
									{
										arDivideIcons.get(i).setSelected(true);
									}
									else
									{
										arDivideIcons.get(i).setSelected(false);
									}
								}
								//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경<<
							}
						});
						break;
					case 14:
						CheckBox chkJongMok = (CheckBox)view;
						chkJongMok.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								CheckBox chk = (CheckBox)v;
//				        		mainBase.baseP.syncJongMok(v);

								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 단일차트일 때는 닫지 않게 하는 것 처리할려고 하는데 중복코드 많아서 모듈
//				    			COMUtil.recycleDrawable(dividePopup.getBackground());
//				    			dividePopup.setBackgroundDrawable(null);
//				    			dividePopup.dismiss();
//				    			dividePopup = null;
//				        		removeDividePopup(((Base11)mainBase.baseP).m_bSyncJongMok);	//2013. 9. 17 분할동기화창 체크풀때는 창 안닫히게 하기>>
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

								//텍스트뷰 색상변경
								TextView tv = (TextView)_divideView.findViewById(context.getResources().getIdentifier("button15", "id", context.getPackageName()));
								if(chk.isChecked())
								{
//									tv.setTextColor(Color.rgb(239, 115, 28));
								}
								else
								{
									tv.setTextColor(Color.rgb(102, 102, 102));
								}
							}
						});
						break;
					case 15:
						//종목동기화 텍스트뷰 
						//textview 가 눌렸을 때이므로 자신의 왼쪽에 있는 checkbox 를 가져온다. 
						chkJongMok = (CheckBox)divideView.findViewById(btnResId[i-1]);
						chkJongMok.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);

						//체크상태에 따라 색상적용
						TextView tv = (TextView) view;
						if(chkJongMok.isChecked())
						{
//							tv.setTextColor(Color.rgb(239, 115, 28));
						}
						else
						{
							tv.setTextColor(Color.rgb(102, 102, 102));
						}

						final CheckBox _chkJongmok = chkJongMok;

						view.setTag(_chkJongmok);
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								//종목동기화 체크박스를 불러온다.
								CheckBox chk = (CheckBox)v.getTag();
								chk.setChecked(!chk.isChecked());

								//종목동기화 수행. 여기서  종목동기화플래그가 반대로 바뀐다. 
//				        		mainBase.baseP.syncJongMok(chk);

								//종목동기화 체크박스에 변경사항 반영 
//								chk.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);

								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 단일차트일 때는 닫지 않게 하는 것 처리할려고 하는데 중복코드 많아서 모듈
//				    			COMUtil.recycleDrawable(dividePopup.getBackground());
//				    			dividePopup.setBackgroundDrawable(null);
//				    			dividePopup.dismiss();
//				    			dividePopup = null;
//				        		removeDividePopup(((Base11)mainBase.baseP).m_bSyncJongMok);	//2013. 9. 17 분할동기화창 체크풀때는 창 안닫히게 하기>>
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

								//텍스트뷰 색상변경
								if(chk.isChecked())
								{
//									((TextView)v).setTextColor(Color.rgb(239, 115, 28));
								}
								else
								{
									((TextView)v).setTextColor(Color.rgb(102, 102, 102));
								}
							}
						});
						break;
					case 16:
						CheckBox chkJugi = (CheckBox)view;
						chkJugi.setChecked(((Base11)mainBase.baseP).m_bSyncJugi);
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								CheckBox chk = (CheckBox)v;
								//주기동기화 수행. 여기서  주기동기화플래그가 반대로 바뀐다. 
//								mainBase.baseP.syncJugi(v);

								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 단일차트일 때는 닫지 않게 하는 것 처리할려고 하는데 중복코드 많아서 모듈
//				    			COMUtil.recycleDrawable(dividePopup.getBackground());
//				    			dividePopup.setBackgroundDrawable(null);
//				    			dividePopup.dismiss();
//				    			dividePopup = null;
//								removeDividePopup(((Base11)mainBase.baseP).m_bSyncJugi);	//2013. 9. 17 분할동기화창 체크풀때는 창 안닫히게 하기>>
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

								//텍스트뷰 색상변경
								TextView tv = (TextView)_divideView.findViewById(context.getResources().getIdentifier("button17", "id", context.getPackageName()));
								if(chk.isChecked())
								{
//									tv.setTextColor(Color.rgb(239, 115, 28));
								}
								else
								{
									tv.setTextColor(Color.rgb(102, 102, 102));
								}
							}
						});
						break;
					case 17:
						//주기동기화 텍스트뷰 
						//textview 가 눌렸을 때이므로 자신의 왼쪽에 있는 checkbox 를 가져showPeriodView온다.
						chkJugi = (CheckBox)divideView.findViewById(btnResId[i-1]);
						chkJugi.setChecked(((Base11)mainBase.baseP).m_bSyncJugi);

						//체크상태에 따라 색상적용
						tv = (TextView) view;
						if(chkJugi.isChecked())
						{
//							tv.setTextColor(Color.rgb(239, 115, 28));
						}
						else
						{
							tv.setTextColor(Color.rgb(102, 102, 102));
						}

						final CheckBox _chkJugi = chkJugi;

						view.setTag(_chkJugi);
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								//주기동기화 체크박스를 불러온다.
								CheckBox chk = (CheckBox)v.getTag();
				        		chk.setChecked(!chk.isChecked());

								//주기동기화 수행. 여기서  주기동기화플래그가 반대로 바뀐다. 
//								mainBase.baseP.syncJugi(chk);

								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 단일차트일 때는 닫지 않게 하는 것 처리할려고 하는데 중복코드 많아서 모듈
//				    			COMUtil.recycleDrawable(dividePopup.getBackground());
//				    			dividePopup.setBackgroundDrawable(null);
//				    			dividePopup.dismiss();
//				    			dividePopup = null;
//								removeDividePopup(((Base11)mainBase.baseP).m_bSyncJugi);	//2013. 9. 17 분할동기화창 체크풀때는 창 안닫히게 하기>>
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

								//텍스트뷰 색상변경
								if(chk.isChecked())
								{
//									((TextView)v).setTextColor(Color.rgb(239, 115, 28));
								}
								else
								{
									((TextView)v).setTextColor(Color.rgb(102, 102, 102));
								}
							}
						});
						break;
					case 18:
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								mainBase.baseP.syncIndicator(v);
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 단일차트일 때는 닫지 않게 하는 것 처리할려고 하는데 중복코드 많아서 모듈
//				    			COMUtil.recycleDrawable(dividePopup.getBackground());
//				    			dividePopup.setBackgroundDrawable(null);
//				    			dividePopup.dismiss();
//				    			dividePopup = null;
								removeDividePopup(((Base11)mainBase.baseP).m_bSyncIndicator);
								closeApiPopup();
								//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>
							}
						});

						break;
				}
			}
		}

		//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>  : selectChartMenuFromParent 에 있던 것 이동.
		//닫기
		layoutResId = context.getResources().getIdentifier("btnchartsettingclose", "id", context.getPackageName());
		Button btnClose = (Button) divideView.findViewById(layoutResId);
		btnClose.setTypeface(COMUtil.typefaceMid);
		if (btnClose != null) {
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDividePopup(true);
				}
			});
		}

		layoutResId = context.getResources().getIdentifier("btn_divide_close", "id", context.getPackageName());
		btnClose = (Button) divideView.findViewById(layoutResId);
		if (btnClose != null) {
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDividePopup(true);
				}
			});
		}

		//초기화
		layoutResId = context.getResources().getIdentifier("btn_devideview_refresh", "id", context.getPackageName());
		final Button btnRefresh = (Button)divideView.findViewById(layoutResId);
		if(btnRefresh != null)
		{
			btnRefresh.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
					alert.setMessage("분할 설정을 초기화 합니다.");
					alert.setNoButton("아니오", null);
					alert.setYesButton("초기화",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//동기화 체크를 해제한다.
									//체크값 False 인 임시 체크박스를 만들고, 이를 종목/주기동기화 함수에 넣어줌.
									//원분할일때는 초기화 된 상태로 돌아오므로 초기화는 멀티차트일 때만 수행해주면 된다.
									if (((Base11) mainBase.baseP).isMultiChart()) {
										CheckBox chkForRefresh = new CheckBox(context);
										chkForRefresh.setChecked(false);
										mainBase.baseP.syncJongMok(chkForRefresh);
										mainBase.baseP.syncJugi(chkForRefresh);

										//주기동기화 초기화
										((Base11) mainBase.baseP).m_bSyncIndicator = false;

										//원분할로 바꾸고
										View view = new View(v.getContext());    //단순히 원분할 태그를 넘기기 위한 껍데기 뷰
										view.setTag("11");
										COMUtil.setDivideChart(view);   //분할차트 동작시키면서 팝업창도 닫기 때문에  닫기버튼처럼 팝업창 해제를 따로 안해줌.
									}
									else {
										//원분할에서 초기화 버튼이 눌리면 그냥 닫히게 한다.
										closeApiPopup();
									}

									dialog.dismiss(); //팝업창 닫기
								}
							});

					alert.show();
					COMUtil.g_chartDialog = alert;
				}
			});

			layoutResId = context.getResources().getIdentifier("ll_refresh_area", "id", context.getPackageName());
			LinearLayout llRefreshArea = (LinearLayout)divideView.findViewById(layoutResId);
			llRefreshArea.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnRefresh.performClick();
				}
			});
		}
		//2013. 8. 27 멀티차트설정창을 멀티차트버튼과동기화 통합된 UI 변경 >>


		//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경>>
		//확인 버튼
		layoutResId = context.getResources().getIdentifier("btn_devideview_confirm", "id", context.getPackageName());
		Button btnConfirm = (Button)divideView.findViewById(layoutResId);
		if(btnConfirm != null)
		{
			btnConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//선택되어있는 분할아이콘으로  분할수행
					for(Button btn : arDivideIcons)
					{
						if(btn.isSelected())
						{
							COMUtil.setDivideChart(btn);
							break;
						}
					}

					//종목동기화,주기동기화 체크상태에 따라 수행하기
					int resId = context.getResources().getIdentifier("button15", "id", context.getPackageName());
					CheckBox chkJongmok = (CheckBox)_divideView.findViewById(resId);
					mainBase.baseP.syncJongMok(chkJongmok);
					resId = context.getResources().getIdentifier("button17", "id", context.getPackageName());
					CheckBox chkJugi = (CheckBox)_divideView.findViewById(resId);
					mainBase.baseP.syncJugi(chkJugi);

					//2019. 07. 02 by hyh - 화면 내렸다가 올릴 때 분할설정 저장되도록 처리
					COMUtil.saveLastState(strFileName);

					closeApiPopup();
				}
			});
		}
		//2015. 12. 9 멀티차트설정창 형태 선택후 반영으로 변경<<

		COMUtil.setGlobalFont(divideLayout);

		if(!COMUtil.apiMode) {
			this.layout.addView(divideLayout);
		}
//    	} else {
//    		this.layout.removeView(divideLayout);
//    		divideLayout = null;
//    	}
	}
	//API 저장/불러오기 설정 메뉴
	RelativeLayout saveloadLayout=null;
	public void setSaveload(View v, RelativeLayout.LayoutParams params, int triXpos) {
//    	if(saveloadLayout==null) {
		saveloadLayout = new RelativeLayout(context);
		saveloadLayout.setTag(COMUtil.SAVELOAD_LAYOUT);
//		if(params==null) {
//			params =new RelativeLayout.LayoutParams(
//					COMUtil.g_nDisWidth, COMUtil.g_nDisHeight);
			params.leftMargin=0;
			params.topMargin=0;
		params.width = (int)COMUtil.getPixel(170);
		params.height = (int)COMUtil.getPixel(157);
//		int nTop = params.topMargin;
//		int nLeft = params.leftMargin;
		
		int nTop = 0;
		int nLeft = 0;
//		}
		saveloadLayout.setBackgroundColor(Color.argb(150, 0, 0, 0));
		saveloadLayout.setLayoutParams(params);
		saveloadLayout.setGravity(Gravity.CENTER);
		saveloadLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveloadPopup.dismiss();
				saveloadPopup = null;
			}
		});

		LayoutInflater factory = LayoutInflater.from(this.context);

		//2012. 8. 16  HONEYCOMB 인지 아닌지에 따라서 구분 : J_tab07
//			int layoutResId = context.getResources().getIdentifier("saveloadmenu", "layout", context.getPackageName());
//			LinearLayout ll = (LinearLayout)factory.inflate(layoutResId, null);
		int layoutResId;
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//			LinearLayout ll;
		LinearLayout popupView;
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{

			//삼각형 이미지 추가
			ImageView viewArrow = new ImageView(context);
			layoutResId = context.getResources().getIdentifier("round_desc_tri", "drawable", context.getPackageName());
			viewArrow.setImageResource(layoutResId);
			RelativeLayout.LayoutParams xparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			xparams.leftMargin = triXpos;
			xparams.topMargin = - xparams.height;
			viewArrow.setLayoutParams(xparams);
			saveloadLayout.addView(viewArrow);

			layoutResId = context.getResources().getIdentifier("saveloadmenu_tab", "layout", context.getPackageName());
		}
		else
		{
			layoutResId = context.getResources().getIdentifier("saveloadmenu", "layout", context.getPackageName());
		}
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//			ll = (LinearLayout)factory.inflate(layoutResId, null);
		popupView = (LinearLayout)factory.inflate(layoutResId, null); 
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>

		//커스텀 폰트 적용
		COMUtil.setGlobalFont(popupView);

		//2012. 12. 6 폰에서 팝업창 배경 가로세로에 따라 변경 : SL40
//			if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//			{
		final int requestedOrientation = COMUtil._chartMain.getRequestedOrientation();
//				if(requestedOrientation == 0)
//				{
//					//가로 
//					layoutResId = context.getResources().getIdentifier("pop_back_save", "drawable", context.getPackageName());
//				}
//				else
//				{
		//세로
//					layoutResId = context.getResources().getIdentifier("pop_back_save", "drawable", context.getPackageName());
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//		layoutResId = context.getResources().getIdentifier("bg_popup", "drawable", context.getPackageName());
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//				}
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//				Bitmap bmp=BitmapFactory.decodeResource(getResources(), layoutResId); // 비트맵 이미지를 만든다.
//				int width=(int)(params.width); // 가로 사이즈 지정
//				int height=(int)(params.height); // 세로 사이즈 지정
//				Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, (int)COMUtil.getPixel(width), (int)COMUtil.getPixel(height), true); // 이미지 사이즈 조정
//				Drawable drawable = new BitmapDrawable(resizedbitmap);
//				ll.setBackgroundDrawable(drawable); // 이미지뷰에 조정한 이미지 넣기
//		popupView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(106), (int)COMUtil.getPixel(86));
//		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(170), (int)COMUtil.getPixel(134));
		//rp.topMargin = nTop;
		//rp.leftMargin = nLeft;
		popupView.setLayoutParams(params);
		//popupView.setGravity(Gravity.CENTER);
//		popupView.setBackgroundResource(layoutResId);

		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//			}

		//	ll.setTag("saveloadView");
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//			saveloadLayout.addView(ll);
		saveloadLayout.addView(popupView);
		//2013. 7. 25 저장불러오기창 새 디자인 적용>>

		//분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
		int[] btnResId = new int[2];
		String index="";
		View view = null;
		for(int i=0; i<btnResId.length; i++) {
			if((i+1)<10) index = "0"+(i+1);
			else index = ""+(i+1);
			btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
			//2013. 7. 25 저장불러오기창 새 디자인 적용>>
//				view = ll.findViewById(btnResId[i]);
			view = popupView.findViewById(btnResId[i]);
			//2013. 7. 25 저장불러오기창 새 디자인 적용>>
			if (view != null)
			{
				if(i==0) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							COMUtil.saveChart(v);
							closePopup();
						}
					});
				} else if(i==1) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							COMUtil.loadChart(v);
							closePopup();
						}
					});
				} else if(i==2) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							mainBase.initChart("pressInitBtn");
							closePopup();
						}
					});
				}

			}
		}
		if(!COMUtil.apiMode) {
			this.layout.addView(saveloadLayout);
		}
//    	} else {
//    		this.layout.removeView(saveloadLayout);
//    		saveloadLayout = null;
//    	}
	}

	//2013. 7. 25 분할및동기화메뉴 통합창 추가 >>
//    RelativeLayout divideAndSyncLayout=null;
	public RelativeLayout setDivideAndSyncMenu(View v, RelativeLayout.LayoutParams params, int triXpos, final View apiView) {
		//현재 상단 삼각형 x 좌표를 나타내는데 쓰였던 기존 triXpos Parameter 는 이 함수에서 사용하지 않는다.

		RelativeLayout divideAndSyncLayout = new RelativeLayout(context);
		divideAndSyncLayout.setTag(COMUtil.DIVIDESYNC_LAYOUT);
		if(params==null) {
			params =new RelativeLayout.LayoutParams(
					(int)COMUtil.getPixel(138), (int)COMUtil.getPixel(171));
			params.leftMargin=0;
			params.topMargin=0;
		}
		divideAndSyncLayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(this.context);

		//xml 로드
		int layoutResId = context.getResources().getIdentifier("divideandsyncview", "layout", context.getPackageName());
		LinearLayout popupView = (LinearLayout)factory.inflate(layoutResId, null);

		//배경이미지 세팅
		layoutResId = context.getResources().getIdentifier("pop_back", "drawable", context.getPackageName());
		popupView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		popupView.setBackgroundResource(layoutResId);

		divideAndSyncLayout.addView(popupView);

		//분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
		int[] btnResId = new int[8];
		String index="";
		View view = null;

		//기존 멀티및동기화 창의 위치(x, y) 정보를 이용하기 위해서 final로 설정. 
		final RelativeLayout.LayoutParams _params = params;

		for(int i=0; i<btnResId.length; i++) {
			if((i+1)<10) index = "0"+(i+1);
			else index = ""+(i+1);
			btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
			view = popupView.findViewById(btnResId[i]);
			if (view != null)
			{
				if(i==0)
				{
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//원분할 기능

							//단순히 원분할 태그를 넘기기 위한 껍데기 뷰
							View view = new View(v.getContext());
							view.setTag("11");
							COMUtil.setDivideChart(view);
						}
					});
				}
				else if(i==1) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//멀티차트설정창
							//위치정보는 ctlchartex(메인) 에서 보내주므로, params 를 가지고 처리해야한다. click listener 안이라서 위에서 final 로 한 후 가져와야함.
							//너비, 높이 정보는 수정되야하기 때문에 여기서 다시 변수로 생성후 적용
							RelativeLayout.LayoutParams params = _params;

							params.width = (int)COMUtil.getPixel(223);
							params.height = (int)COMUtil.getPixel(249);
							params.leftMargin -= (int)COMUtil.getPixel(85);    //멀티차트설정창 x 좌표와 멀티및동기화 창의 x 좌표 차이.

							setDivideConfig(null, params, -1);

//							dividePopup = new PopupWindow(divideLayout, params.width, params.height, true);
//							dividePopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
//							dividePopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
//
//							//팝업 띄우기
//							dividePopup.showAtLocation(apiView, Gravity.TOP|Gravity.LEFT, params.leftMargin, params.topMargin);

							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				} else if(i==2) {
					CheckBox chkJongMok = (CheckBox)view;
					chkJongMok.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);

					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//종목동기화
							mainBase.baseP.syncJongMok(v);
							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				}
				else if(i==3) {
					//textview 가 눌렸을 때이므로 자신의 왼쪽에 있는 checkbox 를 가져온다. 
					CheckBox chkJongMok = (CheckBox)popupView.findViewById(btnResId[i-1]);
					chkJongMok.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);
					final CheckBox _chk = chkJongMok;

					view.setTag(_chk);
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//종목동기화
							CheckBox chk = (CheckBox)v.getTag();
							chk.setChecked(!chk.isChecked());

							mainBase.baseP.syncJongMok(_chk);
							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				}
				else if(i==4) {
					CheckBox chkJugi = (CheckBox)view;
					chkJugi.setChecked(((Base11)mainBase.baseP).m_bSyncJugi);

					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//주기동기화
							mainBase.baseP.syncJugi(v);
							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				}
				else if(i==5) {
					//textview 가 눌렸을 때이므로 자신의 왼쪽에 있는 checkbox 를 가져온다. 
					CheckBox chkJugi = (CheckBox)popupView.findViewById(btnResId[i-1]);
					chkJugi.setChecked(((Base11)mainBase.baseP).m_bSyncJugi);
					final CheckBox _chk = chkJugi;

					view.setTag(_chk);

					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							CheckBox chk = (CheckBox)v.getTag();
							chk.setChecked(!chk.isChecked());

							mainBase.baseP.syncJugi(chk);
							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				}
				else if(i==6 || i==7) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//지표동기화
							mainBase.baseP.syncIndicator(v);
							divideAndSyncPopup.dismiss();
							divideAndSyncPopup = null;
						}
					});
				}

			}
		}
		if(!COMUtil.apiMode) {
			this.layout.addView(saveloadLayout);
		}

		return divideAndSyncLayout;
	}
	//2013. 7. 25 분할및동기화메뉴 통합창 추가 >>

	//회전시에만 닫히도록 하는 함수
	public void closeApiPopup() {

//    	if(COMUtil.apiMode && indicatorLayout!=null) { 
//    		indicatorLayout.removeAllViews();
//    		addRemovePopupView("2", indicatorLayout);
//    		indicatorLayout = null;
//    	}

		//경고창 및 봉갯수설정창
		if(null != COMUtil.g_chartDialog)
		{
			COMUtil.g_chartDialog.dismiss();
			COMUtil.g_chartDialog = null;
		}

		if(null != indicatorPopup)
		{
			COMUtil.recycleDrawable(indicatorPopup.getBackground());
			indicatorPopup.setBackgroundDrawable(null);
			indicatorPopup.dismiss();
			indicatorPopup = null;
		}

		if(null != indicatorMiniPopup)
		{
			COMUtil.recycleDrawable(indicatorMiniPopup.getBackground());
			indicatorMiniPopup.setBackgroundDrawable(null);
			indicatorMiniPopup.dismiss();
			indicatorMiniPopup = null;

			((Base11)mainBase.baseP).resetSimpleSettingButton();
		}

		if(syncPopup!=null) {
			COMUtil.recycleDrawable(syncPopup.getBackground());
			syncPopup.setBackgroundDrawable(null);
			syncPopup.dismiss();
			syncPopup=null;
		}
		if(dividePopup!=null) {
			removeDividePopup(true);
		}
		if(saveloadPopup!=null) {
			COMUtil.recycleDrawable(saveloadPopup.getBackground());
			saveloadPopup.setBackgroundDrawable(null);
			saveloadPopup.dismiss();
			saveloadPopup = null;
		}
		//2013. 7. 26 회전시 멀티및동기화 선택창 닫히게 처리 >>
		if(divideAndSyncPopup!=null) {
			COMUtil.recycleDrawable(divideAndSyncPopup.getBackground());
			divideAndSyncPopup.setBackgroundDrawable(null);
			divideAndSyncPopup.dismiss();
			divideAndSyncPopup = null;
		}
		//2013. 7. 26 회전시 멀티및동기화 선택창 닫히게 처리 >>

		//2013. 8. 30 분틱주기 리스트 팝업, 기준선 팝업 회전했을 때 없어지지 않음>>
		if(comboPopup!=null) {
			COMUtil.recycleDrawable(comboPopup.getBackground());
			comboPopup.setBackgroundDrawable(null);
			comboPopup.dismiss();
			comboPopup = null;
		}
		if(comboSecondPopup!=null) {
			COMUtil.recycleDrawable(comboSecondPopup.getBackground());
			comboSecondPopup.setBackgroundDrawable(null);
			comboSecondPopup.dismiss();
			comboSecondPopup = null;
		}

		if(COMUtil.savechartPopup != null)
		{
			COMUtil.savechartPopup.dismiss();
			COMUtil.savechartPopup = null;
		}

		if(COMUtil.loadchartPopup != null)
		{
			COMUtil.loadchartPopup.dismiss();
			COMUtil.loadchartPopup = null;
		}
		if(COMUtil.modifySavedPopup != null)
		{
			COMUtil.modifySavedPopup.dismiss();
			COMUtil.modifySavedPopup = null;
		}

		if(mainBase.baseP instanceof Base11)
		{
			Base11 base11 = (Base11)mainBase.baseP;
			if(null != base11)
			{
				if(base11.baselinePopup!=null)
				{
					COMUtil.recycleDrawable(((Base11)mainBase.baseP).baselinePopup.getBackground());
					base11.baselinePopup.setBackgroundDrawable(null);
					base11.baselinePopup.dismiss();
					base11.baselinePopup = null;
				}
				if(base11.autotrendPopup!=null)
				{
					COMUtil.recycleDrawable(base11.autotrendPopup.getBackground());
					base11.autotrendPopup.setBackgroundDrawable(null);
					base11.autotrendPopup.dismiss();
					base11.autotrendPopup = null;
				}
				if(base11._chart != null)
				{
					if(base11._chart.popupJipyoList != null)
					{
						base11._chart.popupJipyoList.dismiss();
						base11._chart.popupJipyoList = null;
					}
				}
			}
		}
	}
	public void closeIndicatorPopupForBack() {
		if(COMUtil.apiMode && indicatorLayout!=null) {
//    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);

			indicatorLayout.removeAllViews();
			COMUtil.apiLayout.removeView(indicatorLayout);
			indicatorLayout = null;
			//2014. 1. 22 지표설정창 백버튼 누르면 설정창만 닫히게 처리<<

//			//차트저장
//			COMUtil.recycleDrawable(indicatorPopup.getBackground());
//			indicatorPopup.setBackgroundDrawable(null);
//			saveStatus(null);
//			indicatorPopup.dismiss();
//			indicatorPopup = null;
		}
		if(indicatorPopup!=null) {
			//차트저장
			COMUtil.recycleDrawable(indicatorPopup.getBackground());
			indicatorPopup.setBackgroundDrawable(null);
			saveStatus(null);

			indicatorPopup.dismiss();
			indicatorPopup = null;
		}
	}
	public void closePopup() {
		if(COMUtil.apiMode && indicatorLayout!=null) {
//    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);

			//2013. 11. 4 좌측 탭 화면 이동시 지표설정창 닫기 처리>>
			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
			{
				closeIndicatorPopupForBack();
			}
			else
			{
				indicatorLayout.removeAllViews();
				COMUtil.apiLayout.removeView(indicatorLayout);
				indicatorLayout = null;
			}
			//2013. 11. 4 좌측 탭 화면 이동시 지표설정창 닫기 처리<<
		}
		if(indicatorPopup!=null) {
			//차트저장
			COMUtil.recycleDrawable(indicatorPopup.getBackground());
			indicatorPopup.setBackgroundDrawable(null);
			saveStatus(null);
			indicatorPopup.dismiss();
			indicatorPopup = null;
		}

		if(null != indicatorMiniPopup)
		{
			COMUtil.recycleDrawable(indicatorMiniPopup.getBackground());
			indicatorMiniPopup.setBackgroundDrawable(null);
			indicatorMiniPopup.dismiss();
			indicatorMiniPopup = null;
		}

		if(COMUtil.apiMode && syncLayout!=null) {
			COMUtil.apiLayout.removeView(syncLayout);
			syncLayout = null;

		}
		if(syncPopup!=null) {
			COMUtil.recycleDrawable(syncPopup.getBackground());
			syncPopup.setBackgroundDrawable(null);
			syncPopup.dismiss();
			syncPopup=null;
		}
		if(COMUtil.apiMode && divideLayout!=null) {
			COMUtil.apiLayout.removeView(divideLayout);
			divideLayout = null;

		}
		if(dividePopup!=null) {
			removeDividePopup(true);
		}
		if(COMUtil.apiMode && saveloadLayout!=null) {
			COMUtil.apiLayout.removeView(saveloadLayout);
			saveloadLayout = null;

		}
		if(saveloadPopup!=null) {
			COMUtil.recycleDrawable(saveloadPopup.getBackground());
			saveloadPopup.setBackgroundDrawable(null);

			saveloadPopup.dismiss();
			saveloadPopup = null;
		}

		//2013. 1.   지표설정창(보조지표리스트)  있으면 초기화
		if(mainBase == null)
			return;

		NeoChart2 neoChart = mainBase.baseP._chart;
		if(neoChart!=null) {
			if(neoChart.popupJipyoList != null)
			{
				neoChart.popupJipyoList.dismiss();
				neoChart.popupJipyoList = null;
			}

			//2013. 1. 21  보조지표리스트의 상세설정창   있으면 초기화
			if(neoChart.jipyoListDetailPopup != null)
			{
				neoChart.jipyoListDetailPopup.dismiss();
				neoChart.jipyoListDetailPopup = null;
			}
		}

		//2014. 4. 7 화면회전시 주기 콤보박스 리스트뷰가 사라지지 않는 현상>>
		if(comboPopup != null)
		{
			comboPopup.dismiss();
			comboPopup = null;
		}
		//2014. 4. 7 화면회전시 주기 콤보박스 리스트뷰가 사라지지 않는 현상<<
	}

	//2012. 12. 5 차트 저장될때 (회전하여 닫힐때 )   열려있는 팝업창 다 닫기
	public void closePopup(RelativeLayout apiLayout) {
		if(COMUtil.apiMode && indicatorLayout!=null) {
//    		System.out.println("controlPopup_mainFrame_indicatorLayout:"+indicatorLayout);
			indicatorLayout.removeAllViews();
			COMUtil.apiLayout.removeView(indicatorLayout);
			indicatorLayout = null;

		}
		if(indicatorPopup!=null) {
			//차트저장
			COMUtil.recycleDrawable(indicatorPopup.getBackground());
			indicatorPopup.setBackgroundDrawable(null);
			saveStatus(null);
			indicatorPopup.dismiss();
			indicatorPopup = null;
		}
		if(null != indicatorMiniPopup)
		{
			COMUtil.recycleDrawable(indicatorMiniPopup.getBackground());
			indicatorMiniPopup.setBackgroundDrawable(null);
			indicatorMiniPopup.dismiss();
			indicatorMiniPopup = null;
		}
		if(COMUtil.apiMode && syncLayout!=null) {
			apiLayout.removeView(syncLayout);
			syncLayout = null;

		}
		if(syncPopup!=null) {
			COMUtil.recycleDrawable(syncPopup.getBackground());
			syncPopup.setBackgroundDrawable(null);
			syncPopup.dismiss();
			syncPopup=null;
		}
		if(COMUtil.apiMode && divideLayout!=null) {
			apiLayout.removeView(divideLayout);
			divideLayout = null;

		}
		if(dividePopup!=null) {
			removeDividePopup(true);
		}
		if(COMUtil.apiMode && saveloadLayout!=null) {
			apiLayout.removeView(saveloadLayout);
			saveloadLayout = null;

		}
		if(saveloadPopup!=null) {
			COMUtil.recycleDrawable(saveloadPopup.getBackground());
			saveloadPopup.setBackgroundDrawable(null);
			saveloadPopup.dismiss();
			saveloadPopup = null;
		}

		//2012. 12. 5 차트 회전될 때 열린 팝업창 닫기 
		if(COMUtil.savechartPopup != null)
		{
			COMUtil.savechartPopup.dismiss();
			COMUtil.savechartPopup = null;
		}

		if(COMUtil.loadchartPopup != null)
		{
			COMUtil.loadchartPopup.dismiss();
			COMUtil.loadchartPopup = null;
		}

		Base11 base11 = (Base11)mainBase.baseP;
		if(base11.baselinePopup != null)
		{
			base11.baselinePopup.dismiss();
			base11.baselinePopup = null;
		}

		if(base11.autotrendPopup != null)
		{
			base11.autotrendPopup.dismiss();
			base11.autotrendPopup = null;
		}

		//2013. 1.   지표설정창(보조지표리스트)  있으면 초기화
		if(base11._chart != null)
		{
			if(base11._chart.popupJipyoList != null)
			{
				base11._chart.popupJipyoList.dismiss();
				base11._chart.popupJipyoList = null;
			}
		}
	}
	//2012. 8. 8 체크박스 이전상태 기억 : J04
//    static boolean bIsJongmok = false;
//    static boolean bIsJugi = false;
	CheckBox chkJongmok, chkJugi;
	public View getSyncMenu(View v, RelativeLayout.LayoutParams params, int triXpos) {
		RelativeLayout sublayout = new RelativeLayout(context);
		sublayout.setTag(COMUtil.SYNC_LAYOUT);
		sublayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(this.context);

		//2012. 8. 16  HONEYCOMB 인지 아닌지에 따라서 구분 : J_tab07
		int layoutResId;
		//2013. 7. 31 동기화설정창 새로운 디자인 적용>>
//		LinearLayout ll;
		LinearLayout popupView=null;
		//2013. 7. 31 동기화설정창 새로운 디자인 적용>>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			//2013. 6. 11 팝업창 phone용으로 변경 >>
			//삼각형 이미지 추가 
//			ImageView viewArrow = new ImageView(context);
//			layoutResId = context.getResources().getIdentifier("round_desc_tri", "drawable", context.getPackageName());
//			viewArrow.setImageResource(layoutResId);
//			RelativeLayout.LayoutParams xparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			xparams.leftMargin = triXpos;
//			xparams.topMargin = - xparams.height;
//			viewArrow.setLayoutParams(xparams);
//			sublayout.addView(viewArrow);

			//설정화면 추가 
//			layoutResId = context.getResources().getIdentifier("dividesync_tab", "layout", context.getPackageName());
//			ll = (LinearLayout)factory.inflate(layoutResId, null);
////			ll.setLayoutParams(params);
//			
//			layoutResId = context.getResources().getIdentifier("pop_back_sync", "drawable", context.getPackageName());
//			Bitmap bmp=BitmapFactory.decodeResource(getResources(), layoutResId); // 비트맵 이미지를 만든다.
//			int width=(int)(params.width); // 가로 사이즈 지정
//			int height=(int)(params.height); // 세로 사이즈 지정
//			Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, (int)COMUtil.getPixel(width), (int)COMUtil.getPixel(height), true); // 이미지 사이즈 조정
//			Drawable drawable = new BitmapDrawable(resizedbitmap);
//			ll.setBackgroundDrawable(drawable); // 이미지뷰에 조정한 이미지 넣기
//			//2013. 6. 11 팝업창 phone용으로 변경 <<
//			
//			sublayout.addView(ll);

		}
		else
		{
			layoutResId = context.getResources().getIdentifier("dividesync", "layout", context.getPackageName());
			//2013. 7. 31 동기화설정창 새로운 디자인 적용>>
//			ll = (LinearLayout)factory.inflate(layoutResId, null);
			popupView = (LinearLayout)factory.inflate(layoutResId, null);
			//2013. 7. 31 동기화설정창 새로운 디자인 적용>>
			//2012. 12. 6 폰에서 팝업창 배경 가로세로에 따라 변경 : J9

			//2013. 7. 31 동기화설정창 새 디자인 적용>>
//			final int requestedOrientation = COMUtil._chartMain.getRequestedOrientation();
//			if(requestedOrientation == 0)
//			{
//				//가로 
//				layoutResId = context.getResources().getIdentifier("pop_back_sync", "drawable", context.getPackageName());
//			}
//			else
//			{
//				//세로 
//				layoutResId = context.getResources().getIdentifier("pop_back_sync", "drawable", context.getPackageName());
//			}
//			
//			Bitmap bmp=BitmapFactory.decodeResource(getResources(), layoutResId); // 비트맵 이미지를 만든다.
//			int width=(int)(params.width); // 가로 사이즈 지정
//			int height=(int)(params.height); // 세로 사이즈 지정
//			Bitmap resizedbitmap=Bitmap.createScaledBitmap(bmp, (int)COMUtil.getPixel(width), (int)COMUtil.getPixel(height), true); // 이미지 사이즈 조정
//			Drawable drawable = new BitmapDrawable(resizedbitmap);
//			ll.setBackgroundDrawable(drawable); // 이미지뷰에 조정한 이미지 넣기

			layoutResId = context.getResources().getIdentifier("pop_back", "drawable", context.getPackageName());
			popupView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			popupView.setBackgroundResource(layoutResId);

//			sublayout.addView(ll);
			sublayout.addView(popupView);
			//2013. 7. 31 동기화설정창 새 디자인 적용>>
		}
		//분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
		//2012. 9. 25  지표동기화버튼 추가로 인해 배열크기 증가
		int[] btnResId = new int[4];
		String index="";
		View view = null;
		for(int i=0; i<btnResId.length; i++) {
			if((i+1)<10) index = "0"+(i+1);
			else index = ""+(i+1);
			btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
			//2013. 7. 31 동기화설정창 새 디자인 적용>>
//			view = ll.findViewById(btnResId[i]);
			view = popupView.findViewById(btnResId[i]);
			//2013. 7. 31 동기화설정창 새 디자인 적용>>
			if (view != null)
			{
				if(i==0) {
					//2012. 8. 8 체크박스 이전상태 기억 : J04
					CheckBox chk = (CheckBox)view;
					chk.setChecked(((Base11)mainBase.baseP).m_bSyncJongMok);
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							mainBase.baseP.syncJongMok(v);

							//2012. 8. 8 체크박스 이전상태 기억 : J04
//			        		CheckBox chk = (CheckBox)v;
//			        		bIsJongmok = chk.isChecked();
							COMUtil.recycleDrawable(syncPopup.getBackground());
							syncPopup.setBackgroundDrawable(null);

							syncPopup.dismiss();
							syncPopup = null;
						}
					});
				} else if(i==1) {
					//2012. 8. 8 체크박스 이전상태 기억 : J04
					CheckBox chk = (CheckBox)view;
					chk.setChecked(((Base11)mainBase.baseP).m_bSyncJugi);
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							mainBase.baseP.syncJugi(v);

							//2012. 8. 8 체크박스 이전상태 기억 : J04
//			        		CheckBox chk = (CheckBox)v;
//			        		bIsJugi = chk.isChecked();
							COMUtil.recycleDrawable(syncPopup.getBackground());
							syncPopup.setBackgroundDrawable(null);

							syncPopup.dismiss();
							syncPopup = null;
						}
					});
					//2012. 9. 25  주기동기화 버튼 추가하고 리스너 연결을 위해서 i ==3 조건 추가. 
					//button04 라는 아이디를 가지는 버튼이 하나 추가됨 
				} else if(i==2 || i == 3) {
					//2012. 8. 8 체크박스 이전상태 기억 : J04
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							mainBase.baseP.syncIndicator(v);
							COMUtil.recycleDrawable(syncPopup.getBackground());
							syncPopup.setBackgroundDrawable(null);

							syncPopup.dismiss();
							syncPopup = null;
						}
					});
				}

			}
		}

		return sublayout;
	}

	//API 동기화 설정 메뉴
	LinearLayout syncLayout = null;
	public void setSyncMenu(RelativeLayout.LayoutParams params) {
		if(syncLayout==null) {
			syncLayout = new LinearLayout(COMUtil.apiView.getContext());
			syncLayout.setTag(COMUtil.SYNC_LAYOUT);
			if(params==null) {
				params =new RelativeLayout.LayoutParams(
						(int)COMUtil.getPixel(127), (int)COMUtil.getPixel(193));
				params.leftMargin=0;
				params.topMargin=0;
			}
			syncLayout.setLayoutParams(params);

			LayoutInflater factory = LayoutInflater.from(this.context);

			int layoutResId = context.getResources().getIdentifier("dividesync", "layout", context.getPackageName());
			LinearLayout ll = (LinearLayout)factory.inflate(layoutResId, null);
			//	ll.setTag("saveloadView");

			syncLayout.addView(ll);

			//분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
			int[] btnResId = new int[2];
			String index="";
			View view = null;
			for(int i=0; i<btnResId.length; i++) {
				if((i+1)<10) index = "0"+(i+1);
				else index = ""+(i+1);
				btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
				view = ll.findViewById(btnResId[i]);
				if (view != null)
				{
					if(i==0) {
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								COMUtil.saveChart(v);
							}
						});
					} else if(i==1) {
						view.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								COMUtil.loadChart(v);
							}
						});
					}

				}
			}

			COMUtil.apiView.addView(syncLayout);
		} else {
			COMUtil.apiView.removeView(syncLayout);
			syncLayout = null;
		}
	}

	/* API Function - 시장지표 데이터 세팅. */
	public void setMarketData(String title, long[] dates, double[] marketData, int nCount, boolean bSendTR) {
		if(mainBase!=null) {
			mainBase.setMarketData(title, dates, marketData, nCount, bSendTR);
		}
	}

	/* API Function : 스킨타입 처리. */
	public void setSkinType(String skinType) {
		if(skinType.equals("white")) {
			g_nSkinType = COMUtil.SKIN_WHITE;
			mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_WHITE);
		} else {
            g_nSkinType = COMUtil.SKIN_BLACK;
			mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_BLACK);
		}
	}

	public void setLoadLastFileName(String strFileName)
	{
		this.strFileName = strFileName;

		if(strFileName.equals("stock") || strFileName.equals("elw") ||strFileName.equals("etf") ||strFileName.equals("total")) {
			this.strLocalFileName = "stock"+"_local";
		} else if(strFileName.equals("future") || strFileName.equals("total_future")) {
			this.strLocalFileName = "future"+"_local";
		} else if(!strFileName.equals("jisu") && !strFileName.contains("_local"))  { //지수타입은 jisu0 ~ 3으로 구분됨. 
			this.strLocalFileName = strFileName+"_local";
		}
	}

	/* API Function : API Mode 차트 불러오기. */
	public void loadLastSaveState(String strFileName)
	{
		//기준선 설정 파일명 처리
		//int index = strFileName.indexOf("gijunChartSetting");
		//if(index>=0) {
		this.strGijunFileName = strFileName+"_"+"gijunChartSetting";
		//} else {
		this.strFileName = strFileName;
		//}

//		if(strFileName.equals("stock") || strFileName.equals("elw") ||strFileName.equals("etf") ||strFileName.equals("total")) {
//			this.strLocalFileName = "stock"+"_local";
//		} else if(strFileName.equals("future") || strFileName.equals("total_future")) {
//			this.strLocalFileName = "future"+"_local";
//		} else {
//			//기준설정 저장이라면 로컬네임을 설정하지 않는다.
//
////			if(index>=0) {
////
////			} else {
			this.strLocalFileName = strFileName+"_local";
////			}
//		}
//        this.strLocalFileName = "common"+"_local";

		if(COMUtil._mainFrame != this)
		{

			COMUtil._mainFrame = this;
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}

		//2019. 10. 03 by hyh - 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음 >>
		Base11 base = (Base11) this.mainBase.baseP;
		base.setCanSaveChart(false);
		//2019. 10. 03 by hyh - 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음 <<

		if(strFileName == null || strFileName.length()==0)
		{
			if(!mainBase.baseP._chart._cvm.bIsLineChart && !mainBase.baseP._chart._cvm.bIsLine2Chart)
				COMUtil.setSendTrType("");
		}
		else
		{
//	    	String sLoadFile = strFileName + ".dat";
            COMUtil.setLastSaveState("combChartSetting");
			COMUtil.setLastSaveState(strFileName);
			//2013. 8. 30 기준선 저장 안되고 있음 >>
			COMUtil.setLastSaveState(strFileName+"_"+"gijunChartSetting");
			//2013. 8. 30 기준선 저장 안되고 있음 >>setLastSaveState_file

			//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가, 분틱 날짜구분선 보이기 여부 처리  >> : 불러오기
			//COMUtil.setLastSaveState("combChartSetting");
			//2013. 9. 3 추세선 그렸을 때 수치 가시성 여부 설정하는 것 기본환경에 추가, 분틱 날짜구분선 보이기 여부 처리  >> : 불러오기

			//  [[[CommonConst sharedSingleton] ichart] setLastSaveState_file:sLoadFile]; //마지막 설정을 적용한다.
		}
	}

	//private boolean isStateCloud=false;
	public void saveStatus(String strFileName)
	{
		boolean isConfigClose = false;
		if(strFileName == null) {
//    		this.mainBase.baseP._chart.nDirtyFlag = 1;
			isConfigClose = true;
			//isStateCloud = true;
			strFileName = this.strFileName; //설정창을 닫을때 저장하기 위한 부분(로드 파일명으로 저장한다.)
		} else {
//    		COMUtil.bIsMulti = false; //화면이 닫힐 때만 멀티 상태를 초기화 함. by lyk
		}
		if(COMUtil._mainFrame != this)
		{

			COMUtil._mainFrame = this;
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}
		if(strFileName == null || strFileName.length()==0)
			return;

		COMUtil.setSendTrType("");
		//2012.07.13 by LYH >> API Mode 차트 저장

		//2023.11.23 by lyk - 조회되지 않고 reload(카카오 차트 설정 표시정보 수정 후 닫을 때 MTSCandleChartViewController viewappear에서 호출됨) 되는 경우 저장이 안되는 현상 수정 >>
		//2013.10.04 by LYH >> 첫 조회가 완료되지 않은 상태에서 차트 화면 닫힐 때 저장 막음
//		Base11 base = (Base11)this.mainBase.baseP;
//		if(!base.isCanSaveChart())
//			return;
		//2013.10.04 by LYH <<
		//2023.11.23 by lyk - 조회되지 않고 reload(카카오 차트 설정 표시정보 수정 후 닫을 때 MTSCandleChartViewController viewappear에서 호출됨) 되는 경우 저장이 안되는 현상 수정 <<

		//2019. 10. 14 by hyh - 초기화 상태일 때에는 저장하지 않도록 수정 >>
//        try {
//			//2019. 11. 26 by hyh - 분할상태에서 선택 된 영역이 초기화 상태일 때 저장되지 않는 에러 수정. (getDivideNum() == 1) 조건 추가
//			//2020.11.13 by HJW - 봉갯수만 변경하고 지표 디폴트 상태일때 봉갯수 저장안되는 에러 수정 base._chart._cvm.getViewNum() == 40 추가
//			//2021.06.04 by hanjun.Kim - 초기화 및 특정케이스에서 저장이 안되는 이슈 관련 수정. 
//            if (strInitGraphList.equals(COMUtil.getGraphListStr()) && getDivideNum() == 1 && !bIsInitState && base._chart._cvm.getViewNum() == 40) {
//                return;
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        //2019. 10. 14 by hyh - 초기화 상태일 때에는 저장하지 않도록 수정 <<

		//설정변경사항 체크하여 클라우드에 저장여부 결정.
		String beforeValue = "";
//    	if(isConfigClose)
//    		beforeValue = COMUtil.getCloudData(this.context, null);

		//2014. 1. 28 차트 저장/불러오기 처리 >>
		if(null != indicatorConfigView && indicatorPopup != null)
		{
			indicatorConfigView.savePeriodValues();
		}
		//2014. 1. 28 차트 저장/불러오기 처리 <<

		//마지막상태저장.
		String sLoadFile = strFileName + ".dat";
		try {
			COMUtil.saveLastState(strFileName);
		} catch (Exception e) {
//    		System.out.println("savelastState Exception !!!"+e.getMessage());
//    		return;
		}

		String afterValue = "";
//    	if(isConfigClose)
//    		afterValue = COMUtil.getCloudData(this.context, null);

		if(isConfigClose && !beforeValue.equals(afterValue)) {
			this.mainBase.baseP._chart.nDirtyFlag = 1;
			isConfigClose = false;
		}

		//[[CommonConst sharedSingleton] saveStatus_file:sLoadFile];
		//2012.07.13 by LYH << 차트 저장

		//편의설정 저장
		//2013. 9. 9 편의기능 저장을 설정창 닫힐때만 수행 >>
		if(isConfigClose)
		{
			COMUtil.saveLastState("combChartSetting");
		}
		//2013. 9. 9 편의기능 저장을 설정창 닫힐때만 수행 >>

		//기준선 설정 저장
//    	COMUtil.saveLastState("gijunChartSetting");

		if(COMUtil.chartMode==COMUtil.COMPARE_CHART)
			return;

		//클라우드 저장 요청
//    	try {
		if(this.mainBase.baseP._chart.nDirtyFlag > 0)
		{
//	        	if(sLoadFile.equals("stock.dat")||sLoadFile.equals("elw.dat")||sLoadFile.equals("etf.dat")||sLoadFile.equals("future.dat")||sLoadFile.equals("total.dat")||sLoadFile.equals("total_future.dat")||sLoadFile.equals("multi.dat"))
//	        	{
//	        		if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SAVE_CLOUD, null);
//	        	}
			this.mainBase.baseP._chart.nDirtyFlag = 0;

			//2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다)
			Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
			if(!COMUtil._mainFrame.sendTrType.equals("storageType") && base11.divideStorageType!=true) {
				COMUtil.setSavePeriodChartSave();
			}
			//2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다) end
		}
//    	} catch (Exception e) {
//    		System.out.println("DEBUG_saveStatus:"+e.getMessage());
//    	}
	}
	//2012.07.13 by LYH <<
	public int getTotCnt() {
		NeoChart2 neoChart = mainBase.baseP._chart;
		int cnt = neoChart._cdm.getCount();

		return cnt;
	}
	/* API 호출시 처리사항 */
	public boolean apiModeCheck(String data, UserProtocol curUserProtocol) {
		if(COMUtil.apiMode) {
			if(curUserProtocol==null) {
				curUserProtocol = userProtocol;
			}

			NeoChart2 neoChart = mainBase.baseP._chart;

			if(COMUtil.isMarketIndicator(data)) {
				if(neoChart==null)
					return true;

				int cnt = neoChart._cdm.getCount();

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("nTotCount", String.valueOf(cnt));
				dic.put("title", data);

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
				//2013. 12. 20 주식현재가 차트 시장지표추가후 세로->가로 전환시 기존추가한 지표가 전부 초기화>>
				String strData = COMUtil.getSendTrType();
				COMUtil.setSendTrType(data); //차트 축소시 추가 데이터 요청 발생할때 필요.
				if(strData.equals("storageType"))
				{
					COMUtil.setSendTrType("storageType");
				}
				//2013. 12. 20 주식현재가 차트 시장지표추가후 세로->가로 전환시 기존추가한 지표가 전부 초기화<<
			} else if(data.equals("requestAddData")) {
				//최대 조회 갯수를 999개로 제한한다.
				int cnt = COMUtil._neoChart._cdm.getCount();
				int addCnt = 0;
				if(COMUtil.dataTypeName.equals("2")) { //일.
					addCnt = 100;
				} else if(COMUtil.dataTypeName.equals("3") ||
						COMUtil.dataTypeName.equals("4")) { //주,월.
					addCnt = 50;
				} else {
					addCnt = 100;
				}
//	    		int cmpCnt = cnt + addCnt;
//	    		if(cmpCnt > 1000) {
//	    			return true;
//	    		}

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("nKey", COMUtil.nkey);

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_REQUESTADD_TYPE, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_REQUESTADD_TYPE, dic);
				COMUtil.setSendTrType(data); //차트 축소시 추가 데이터 요청 발생할때 필요.
			}
			else if(data.equals("initChart_apiMode")) {
				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil.PERIOD_CONFIG_DAY, null);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil.PERIOD_CONFIG_DAY, null);
			} else if(data.equals(""+COMUtil._TAG_DATACNT_CONFIG)) { //데이터 값 변경 후 TR조회.
				if(neoChart==null)
					return true;

				int viewNum = neoChart._cvm.getViewNum();
				int inquiryNum = neoChart._cvm.inquiryNum;
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("viewNum", String.valueOf(viewNum));
				dic.put("inquiryNum", String.valueOf(inquiryNum));

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_DATACNT_CONFIG, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_DATACNT_CONFIG, dic);
			} else if(data.equals(""+COMUtil._TAG_VIEWCNT_CONFIG)) { //데이터 값 변경 후 TR비조회.
				if(neoChart==null)
					return true;

				int viewNum = neoChart._cvm.getViewNum();
				int dataCnt = neoChart._cdm.getCount();

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("viewNum", String.valueOf(viewNum));
				dic.put("dataCnt", String.valueOf(dataCnt));

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, dic);
			} else if(data.equals(""+COMUtil._TAG_SELECTED_CHART)) { //분할된 차트 선택
				Base11 base = (Base11)mainBase.baseP;
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("totNum", String.valueOf(String.valueOf(base.m_nTotNum)));
				dic.put("selIndex", String.valueOf(base.getSelectedChartIndex()));

				String strInfo = "";
				if(base._chart!= null && base._chart._cdm.codeItem.strCode !=null && base._chart._cdm.codeItem.strCode.length()>0)
				{
					String strCode = base._chart._cdm.codeItem.strCode;
					String strRealKey = base._chart._cdm.codeItem.strRealCode; //2014.04.17 by LYH>> 연결선물 실시간 처리.
					if(strRealKey.length()<1)
					{
						strRealKey = strCode;
					}
//	                if(base._chart._cdm.codeItem.strRealKey.equals("SC0"))
//	                {
//	                    if(strCode.startsWith("1") || strCode.startsWith("4"))
//	                        strRealKey = strRealKey.substring(1, 5);
//	                    strCode = "1"+strCode;
//
//	                }
					String strChange = base._chart._cdm.codeItem.strChange;
					String strRate = base._chart._cdm.codeItem.strChgrate;
					strRate = strRate.replace("%", "");

//                    String str1386 = base._chart._cdm.codeItem.S1386;
//                    String str1339 = base._chart._cdm.codeItem.S1339;
//                    String str1522 = base._chart._cdm.codeItem.S1522;
//                    
//                    if(str1386==null || str1386.equals(""))
//                    	str1386 = " ";
//	                
//                    if(str1339==null || str1339.equals(""))
//                    	str1339 = " ";
//                    
//                    if(str1522==null || str1522.equals(""))
//                    	str1522 = " ";

//                  base._chart._cdm.codeItem.S1386+";"+base._chart._cdm.codeItem.S1339+";"+base._chart._cdm.codeItem.S1522

					String strPrice = ChartUtil.getFormatedData(base._chart._cdm.codeItem.strPrice, base._chart._cdm.getPriceFormat(), base._chart._cdm);
					strChange = ChartUtil.getFormatedData(base._chart._cdm.codeItem.strChange, base._chart._cdm.getPriceFormat(), base._chart._cdm);

					strPrice = strPrice.replace(",", "");
					strChange = strChange.replace(",", "");

					strInfo = strCode+";"+base._chart._cdm.codeItem.strName+";"+base._chart._cdm.codeItem.strDataType+";"+base._chart._cdm.codeItem.strUnit+";"+
							strPrice+";"+base._chart._cdm.codeItem.strSign+";"+
							strChange+";"+ strRate+";"+base._chart._cdm.codeItem.strVolume+";"+strRealKey+";"+base._chart._cdm.codeItem.strMarket+";"+base._chart._cdm.codeItem.strGiOpen+";"+base._chart._cdm.codeItem.strGiHigh+";"+base._chart._cdm.codeItem.strGiLow+";"+base._chart._cdm.codeItem.strGijun;

					dic.put("lcode", base._chart._cdm.codeItem.strRealKey);
				}
				dic.put("priceInfo", strInfo);


				if(base._chart != null && base._chart._cvm != null) {
					//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
					dic.put("fxmargintype", base._chart._cvm.nFxMarginType);
					//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

					//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
					dic.put("conn", base._chart._cvm.bIsConn);
					dic.put("day", base._chart._cvm.bIsDay);
					dic.put("floor", base._chart._cvm.bIsFloor);
					//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
				}

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SELECTED_CHART, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SELECTED_CHART, dic);

			} else if(data.equals("storageType")) {//분할차트 타입 storage 처리.
				Base11 base = (Base11)mainBase.baseP;
				String apCode = COMUtil.apCode;
				COMUtil.setSendTrType(apCode);

				String strCode = COMUtil.symbol;
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
//	            if(COMUtil.lcode.equals("SC0")) {
//	            	strCode = "1"+strCode;
//	            }
				if(strCode==null || strCode.length()==0)
					return true;

				dic.put("symbol", strCode);
				dic.put("market", COMUtil.market);
				dic.put("name", COMUtil.codeName);
				dic.put("lcode", COMUtil.lcode);
				dic.put("period", COMUtil.dataTypeName);

				int viewNum = 100;
				try {
					viewNum = mainBase.baseP._chart._cvm.getViewNum();
				} catch(Exception e) {

				}
				dic.put("viewnum", String.valueOf(viewNum));
				dic.put("count", COMUtil.count);
				dic.put("unit", COMUtil.unit);
				dic.put("apcode", COMUtil.apCode);
//	            int selectedIndex = base.m_nRotateIndex;
				int selectedIndex = COMUtil.m_nRotateIndex;
				dic.put("selIndex", String.valueOf(selectedIndex));

	    		if(COMUtil.isInitChart)
	    			dic.put("initChart", "1");
	    		COMUtil.isInitChart = false;

				//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
				if (base.m_nRotateIndex >= 0 && base.chartList.size() > base.m_nRotateIndex) {
					BaseChart pChart = base.chartList.get(base.m_nRotateIndex);
					dic.put("fxmargintype", pChart._cvm.nFxMarginType);
					dic.put("conn", pChart._cvm.bIsConn);
					dic.put("day", pChart._cvm.bIsDay);
					dic.put("floor", pChart._cvm.bIsFloor);
				}
				else {
					dic.put("fxmargintype", mainBase.baseP._chart._cvm.nFxMarginType);
					dic.put("conn", mainBase.baseP._chart._cvm.bIsConn);
					dic.put("day", mainBase.baseP._chart._cvm.bIsDay);
					dic.put("floor", mainBase.baseP._chart._cvm.bIsFloor);
				}
				//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_STORAGE_TYPE, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_STORAGE_TYPE, dic);
			} else if(data.equals(""+COMUtil._TAG_RESET_MULTICODES)) { //분할된 차트 선택
				Base11 base = (Base11)mainBase.baseP;
				int nTotalCount = base.m_nTotNum;
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("totNum", String.valueOf(String.valueOf(base.m_nTotNum)));
				dic.put("selIndex", String.valueOf(base.getSelectedChartIndex()));

				String strCodes = "";
				String strRealCodes = "";
				String strMarkets = "";
				for(int i=0; i<base.chartList.size(); i++)
				{
					BaseChart pBaseChart = base.chartList.get(i);
					String strCode = pBaseChart._cdm.codeItem.strCode;
					if(strCode == null)
					{
						nTotalCount = i;
						break;
					}
					String strRealKey = pBaseChart._cdm.codeItem.strRealCode;  //2014.04.17 by LYH >> 연결선물 실시간 처리.
					if(strRealKey.length()<1)
					{
						strRealKey = strCode;
					}
					String strMarket = pBaseChart._cdm.codeItem.strMarket;
//	                if(pBaseChart._cdm.codeItem.strRealKey.equals("SC0"))
//	                {
//	                    if(strCode.startsWith("1") || strCode.startsWith("4"))
//	                        strRealKey = strRealKey.substring(1, 5);
//	                    strCode = "1"+strCode;
//	                }
					strCodes = strCodes+strCode;
					strCodes = strCodes+";";
					strRealCodes = strRealCodes+strRealKey;
					strRealCodes = strRealCodes+";";
					strMarkets = strMarkets+strMarket;
					strMarkets = strMarkets+";";
				}
				dic.put("totNum", String.valueOf(nTotalCount));
				dic.put("sCodeData", strCodes);
				dic.put("sRealData", strRealCodes);
				dic.put("sMarket", strMarkets);

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_RESET_MULTICODES, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_RESET_MULTICODES, dic);

				return true;
			} else if(data.equals(""+COMUtil._TAG_SET_PERIOD_UNITS)) { //데이터 값 변경 후 TR비조회.

				Base11 base = (Base11)mainBase.baseP;
				String strData = "";

				//분데이터:분체크데이터#틱데이터:틱체크데이터
				for(int i = 0; i < 7; i++)
				{
					if(i<6)
					{
						strData += String.valueOf(base.astrMinData[i]) + ":";
						strData += String.valueOf(base.astrMinCheckData[i]) + ":";
					}
					else
					{
						strData += String.valueOf(base.astrMinData[i]) + ":";
						strData += String.valueOf(base.astrMinCheckData[i]) + "#";
					}
				}
				//2013. 1. 21 분 체크박스 상태 설정
				for(int i = 0; i < 7; i++)
				{
					if(i<6)
					{
						strData += String.valueOf(base.astrTikData[i]) + ":";
						strData += String.valueOf(base.astrTikCheckData[i]) + ":";
					}
					else
					{
						strData += String.valueOf(base.astrTikData[i]) + ":";
						strData += String.valueOf(base.astrTikCheckData[i]);
					}
				}

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("periodList", strData);
//	    		System.out.println("periodList "+strData);

				//delegate 처리
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_PERIOD_UNITS, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_PERIOD_UNITS, dic);
			}
			//2013. 1. 22 지표설정창 - 일반설정   수정주가 추가 
			else if(data.equals(""+COMUtil._TAG_SET_ADJUSTEDSTOCK))
			{
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("adjustedStock", COMUtil.getAdjustedChart());

				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_ADJUSTEDSTOCK, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_ADJUSTEDSTOCK, dic);

			} else if(data.equals(""+COMUtil._TAG_SET_MARKET_ROTATE_END))
			{
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_MARKET_ROTATE_END, null);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_MARKET_ROTATE_END, null);
//            //2013.09.17 by LYH >> 패턴 그리기 추가.
//	        } else if(data.equals(""+COMUtil._TAG_DRAW_PATTERN))
//	        {
//				if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_DRAW_PATTERN, null);
//            //2013.09.17 by LYH >> 패턴 그리기 추가.
			}
			//2017.09.13 by pjm 한국시간 추가 >>
			else if(data.equals(""+COMUtil._TAG_SET_KOREANTIME))
			{
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("koreanTime", COMUtil.getKoreanTime());

				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_KOREANTIME, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_KOREANTIME, dic);

			}//2017.09.13 by pjm 한국시간 추가 <<
			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
			else if(data.equals(""+COMUtil._TAG_SET_REALBONG))
			{
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("realbong", COMUtil.isUseHubong()?"0":"1");

				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_REALBONG, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_REALBONG, dic);

			}
			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<
			else { //비교차트 데이터 요청
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("type", data);
				dic.put("code", COMUtil.symbol);
				dic.put("name", COMUtil.codeName);
				dic.put("market", COMUtil.market);
				dic.put("period", COMUtil.compareDataTypeName);
				dic.put("unit", COMUtil.compareUnit);
				if(curUserProtocol!=null) curUserProtocol.requestInfo(COMUtil._TAG_SET_COMPARECHART, dic);
				else if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_COMPARECHART, dic);
			}

			return true;
		}

		return false;
	}

	public boolean setLocalStorageState(Cursor cursor, int position) {
//		try {
//			System.out.println("==================setLocalStorageState Cursor cnt : "+cursor.getCount());
		if(!cursor.moveToPosition(position)) {
			COMUtil.bIsAutoTheme = false;	//2015. 3. 4 차트 테마 메인따라가기 추가 : COMUtil이라서 딴화면에서 변경한 값이 영향을 줄 수도 있다.
			return false;
		}

		//		String textTochange = "차트를 불러옵니다.";
		//    	COMUtil.showMessage(COMUtil._chartMain, textTochange); //Context, String msg
			/*
			 * 		" divideinfo TEXT," +1
					" verinfo TEXT," +2
			 		" savedate TEXT," +3
			 		" userid TEXT," +4
			 		" userip TEXT," +5
			 		" codename TEXT," +6
			 		" title TEXT," +7
			 		" detail TEXT," +8
					" imgdata BLOB," +9
					" symbol TEXT," +10
					" lcode TEXT," +11
					" apCode TEXT," +12
					" dataTypeName TEXT," +13
					" count TEXT," +14
					" viewCount TEXT," +15
					" valueOfMin TEXT," +16
					" jipyodata TEXT," +17
					" periodConfig TEXT," +18
					" analInfo TEXT"+19
					" chartMode int"+20
					" market int"+21"
					" bunPeriodList TEXT"+22"
					" ticPeriodList TEXT"+23"
					" marketName TEXT"+24"
			 */


		//일반 설정 적용
		String strConv = cursor.getString(9);
		String[] arrConv = strConv.split("/");

//			int nViewPanelAlpha = 234;	//2015. 4. 7 수치조회창(뷰패널) 투명도 조정 기능
		//2014. 5. 20 차트설정 - 일반설정 플래그 세팅
//		if(arrConv!=null && arrConv.length>=11) {
//		if(arrConv!=null && arrConv.length>=12) { //2017.05.23 by PJM 사진줌방식추가
//		if(arrConv!=null && arrConv.length>=16) { //2019. 03. 07 by hyh - 만기보정, 제외기준 적용
		if(arrConv!=null && arrConv.length>=17) {

			//수정주가적용,상하한가표시,Y축 눈금표시,y축 현재가표시, 추세선 수치 표시,분틱 날짜 구분선,수치조회창 등락,조회중인봉수, 사진줌방식

			bIsAdjustedStock = arrConv[0].equals("1");

			bIsyScaleShow = arrConv[1].equals("1");

			bIsyJonggaShow = arrConv[2].equals("1");

			bIsMinMaxShow = arrConv[3].equals("1");

            bIsSetScreenViewPanel = arrConv[4].equals("1");

			bIsChuseLineValueTextShow = arrConv[5].equals("1");

			bIsDayDivisionLineShow = arrConv[6].equals("1");

			bIsBongCntShow = arrConv[7].equals("1");

            bIsyJonggaCurrentPrice = arrConv[8].equals("1");

            bIsUsePaddingRight = arrConv[9].equals("1");

            try {
                g_nPaddingRight = Integer.parseInt(arrConv[10]);
            }catch (Exception e){}

			bIsZoomCenterView = arrConv[11].equals("1");

			bIsKoreanTime = arrConv[12].equals("1");    //2017.09.13 by pjm 한국시간 추가 >>

			bIsHighLowShow = arrConv[13].equals("1");   //2020.03.26 차트 최대/최소 표시 설정 추가 - hjw
			//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 >>
//			strModYn = arrConv[13];
//			strSkipTp = arrConv[14];
//			strSkipTick = arrConv[15];
//			strSkipVol = arrConv[16];
			//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 <<

			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
			bIsDetailScroll = arrConv[14].equals("1");
			bIsUseHubong = arrConv[15].equals("1");
			//2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<
			bIsShowToolBarOneq = arrConv[16].equals("1");

//				if(arrConv[6].equals("1"))
//					COMUtil.bIsViewPanelChgShow = true;
//				else 
//					COMUtil.bIsViewPanelChgShow = false;
//	
//				//2015.04.08 by lyk - 봉 개수 설정 옵션>>
//				try
//				{
//					if(arrConv[7].equals("1"))
//						COMUtil.bIsBongCntShow = true;
//					else 
//						COMUtil.bIsBongCntShow = false;
//				}
//				catch(Exception e){}
			//2015.04.08 by lyk - 봉 개수 설정 옵션<<

			//2015.04.16 by lyk - 주기별 차트 설정 옵션>>
//				try
//				{
//					if(arrConv[8].equals("1"))
//						this.isPeriodConfigSave = true;
//					else 
//						this.isPeriodConfigSave = false;
//				}
//				catch(Exception e){}
			//2015.04.16 by lyk - 주기별 차트 설정 옵션<<

			mainBase.initChart("");

			//2015.04.21 by lyk - 차트 블럭크기 및 위치 정보 저장
//				try
//				{
//					
//					this.strBlockInfo = arrConv[9];
//				}
//				catch(Exception e){}

			//2015.04.21 by lyk - 차트 블럭크기 및 위치 정보 저장 end

			//2015. 4. 7 수치조회창(뷰패널) 투명도 조정 기능>>
//				try
//				{
//					nViewPanelAlpha = Integer.parseInt(arrConv[9]);
//				}
//				catch(Exception e)
//				{
//					nViewPanelAlpha = 234;
//				}
			//2015. 4. 7 수치조회창(뷰패널) 투명도 조정 기능<<
		}


		//선택된 차트정보 처리.

		COMUtil.verInfo=cursor.getString(2);
		COMUtil.saveDate=cursor.getString(3);
		//		COMUtil.userId=cursor.getString(4);
		//		COMUtil.userIp=cursor.getString(5);
		COMUtil.detail=cursor.getString(8);

		//2013. 1. 28 차트저장타이틀 불러오기
		COMUtil.saveTitle = cursor.getString(25);

		COMUtil.title=cursor.getString(7);
		String market = cursor.getString(21);

		//2015. 1. 13 by lyk 중복지표 추가/삭제 처리
		String strAddJipyoList = cursor.getString(5);

		COMUtil.setAddJipyoList(null); //초기화
		COMUtil.setAddJipyoList(COMUtil.getAddJipyoList(strAddJipyoList));

//			COMUtil.loadAddJipyoList(COMUtil._mainFrame.strFileName+COMUtil.saveTitle);
		//2015. 1. 13 by lyk 중복지표 추가/삭제 처리 end

		//2013. 1. 17  분틱설정
//			COMUtil.bunPeriodList = COMUtil.setBunPeriodListStr(cursor.getString(22));
//			COMUtil.ticPeriodList = COMUtil.setTicPeriodListStr(cursor.getString(23));

		Base11 base11 = (Base11)mainBase.baseP;
//			if(cursor.getString(22)!=null && !cursor.getString(22).equals("")) {
//				base11.astrMinData = COMUtil.setBunPeriodListStr(cursor.getString(22));
//				base11.astrMinCheckData = COMUtil.setBunCheckPeriodListStr(cursor.getString(22));
//			}
//			
//			if(cursor.getString(23)!=null && !cursor.getString(23).equals("")) {
//				base11.astrTikData = COMUtil.setTicPeriodListStr(cursor.getString(23));
//				base11.astrTikCheckData = COMUtil.setTicCheckPeriodListStr(cursor.getString(23));
//			}

		String sChartMode = cursor.getString(20);
		String[] stInfo = sChartMode.split("/");
		int chartMode=0;
//			System.out.println("mainFrame_chartMode:"+sChartMode);
		if(stInfo.length>0 && !stInfo[0].equals("")) {
			if(stInfo[0]!=null && !stInfo[0].equals("null"))
				chartMode = Integer.parseInt(stInfo[0]);
			//2013. 9. 24 블랙테마 저장 안됨>> : 주석처리되어있엇음>>
//			if(stInfo.length>1) {
//				int skinType = Integer.parseInt(stInfo[1]);
////				//2015. 3. 4 차트 테마 메인따라가기 추가>>
////					COMUtil.skinType = skinType;
//				if(skinType == COMUtil.SKIN_AUTO_THEME) {
//					COMUtil.bIsAutoTheme = true;
//                    g_nSkinType = COMUtil.currentTheme;
//				} else {
//					COMUtil.bIsAutoTheme = false;
//                    g_nSkinType = skinType;
//				}
////				//2015. 3. 4 차트 테마 메인따라가기 추가<<
//			}
			//2013. 9. 24 블랙테마 저장 안됨>> : 주석처리되어있엇음>>
		} else {
            //g_nSkinType = COMUtil.SKIN_BLACK;	//2013. 9. 24 블랙테마 저장 안됨>> : 주석처리되어있엇음>>
			chartMode = COMUtil.BASIC_CHART;
		}

		//		String strChartMode = cursor.getString(20);
		//		int chartMode=Integer.parseInt(strChartMode.substring(0,1));

		if(!COMUtil.bIsMulti) {
			chartMode = COMUtil.BASIC_CHART;
		}

//			if(market!=null && !market.equals("")) {
//				Base11 base = (Base11)mainBase.baseP;
//				if(Integer.parseInt(market) != base.nMarketType) {
//					COMUtil.showMessage(COMUtil._chartMain, "이 종목은 로드할수 없습니다.");
//					return false;
//				}
//			}

		COMUtil.chartMode = chartMode;

		//    	if(strChartMode.length()==3)
		//		{
		//			COMUtil.skinType = Integer.parseInt(strChartMode.substring(2,3));
		//		}
		//COMUtil.chartMode=cursor.getInt(20);
		//    	if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
		//    		COMUtil.chartMode = COMUtil.BASIC_CHART;
		//    	}
		//    	int divideCnt = 0;
		Hashtable<String, Object> loadItem = new Hashtable<String, Object>();

		//2015.04.15 by lyk - 주기별 차트 설정시 팝업차트 이동 후 설정이 사라지는 오류 수정
        Hashtable<String, Object> localLoadPeriodSaveItem = new Hashtable<String, Object>();
        if(this.bIsPeriodConfigSave && this.loadPeriodSaveItem!=null) {
            localLoadPeriodSaveItem = this.loadPeriodSaveItem;
        }
		//2015.04.15 by lyk - 주기별 차트 설정시 팝업차트 이동 후 설정이 사라지는 오류 수정 end

        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
        Hashtable<String, Object> localLoadPeriodSizeInfo = new Hashtable<String, Object>();
        if(this.bIsPeriodConfigSave && this.loadPeriodSizeInfo!=null) {
            localLoadPeriodSizeInfo = this.loadPeriodSizeInfo;
        }
        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

		//COMUtil.setSendTrType("storageType");
		this.sendTrType = "storageType";
//    		Base11 base11 = (Base11)mainBase.baseP;
		base11 = (Base11)mainBase.baseP;

		if(chartMode == COMUtil.DIVIDE_CHART) {
			//"/" 구분자로 데이터를 분리하여, Base11에 전달한다.
			StringTokenizer st = new StringTokenizer(cursor.getString(1), "/");//divideinfo
			Vector<String> divideInfos = new Vector<String>();
			while (st.hasMoreTokens()) {
				divideInfos.add(st.nextToken());
			}
			if(divideInfos.size()<1)
				return false;
			//            divideCnt = Integer.parseInt(divideInfos.get(0));
			int nSel = Integer.parseInt(divideInfos.get(1));
			String isSyncJongmokStr = divideInfos.get(2);
			String isSyncJugiStr = divideInfos.get(3);
			boolean isSyncJongmok=false;
			boolean isSyncJugi=false;
			if(isSyncJongmokStr.equals("1")) isSyncJongmok = true;
			if(isSyncJugiStr.equals("1")) isSyncJugi = true;

			Hashtable<String, Vector<String>> items = new Hashtable<String, Vector<String>>();

			//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기>>
//	            st = new StringTokenizer(cursor.getString(10), "/");//symbol
			st = new StringTokenizer(cursor.getString(10), "~");//symbol
			//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기<<
			Vector<String> symbols = new Vector<String>();
			while (st.hasMoreTokens()) {
				//2013.10.04 by LYH >> 분할 상태에서 종목이 없는 경우 끝내지 않고 다음 종목 조회 가능하도록 개선
				//symbols.add(st.nextToken());
				String strCode = st.nextToken();
				if(strCode.equals("-"))
					symbols.add("");
				else
					symbols.add(strCode);
				//2013.10.04 by LYH <<
			}
			items.put("symbols", symbols);

			Vector<String> marketNames = null;
			try {
				st = new StringTokenizer(cursor.getString(24), "/");//marketName
				marketNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					//2013.10.04 by LYH >> 분할 상태에서 종목이 없는 경우 끝내지 않고 다음 종목 조회 가능하도록 개선
					//marketNames.add(st.nextToken());
					String strMarket = st.nextToken();
					if(strMarket.equals("-"))
						marketNames.add("");
					else
						marketNames.add(strMarket);
					//2013.10.04 by LYH <<
				}
				items.put("marketNames", marketNames);
			} catch(Exception e) {

			}

			Vector<String> codeNames = null;
			try {
				//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기>>
//		            st = new StringTokenizer(cursor.getString(6), "/");//codeNames
				st = new StringTokenizer(cursor.getString(6), "~");//codeNames
				//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기<<
				codeNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					codeNames.add(st.nextToken());
				}
				items.put("codeNames", codeNames);
			} catch(Exception e) {

			}

			st = new StringTokenizer(cursor.getString(11), "/");//lcodes
			Vector<String> lcodes = new Vector<String>();
			while (st.hasMoreTokens()) {
				//2013.10.04 by LYH >> 분할 상태에서 종목이 없는 경우 끝내지 않고 다음 종목 조회 가능하도록 개선
				//lcodes.add(st.nextToken());
				String strLCode = st.nextToken();
				if(strLCode.equals("-"))
					lcodes.add("");
				else
					lcodes.add(strLCode);
				//2013.10.04 by LYH <<
			}
			items.put("lcodes", lcodes);

			st = new StringTokenizer(cursor.getString(12), "/");//apCodes
			Vector<String> apCodes = new Vector<String>();
			while (st.hasMoreTokens()) {
				apCodes.add(st.nextToken());
			}
			items.put("apCodes", apCodes);

			st = new StringTokenizer(cursor.getString(13), "/");//dataTypeNames
			Vector<String> dataTypeNames = new Vector<String>();
			while (st.hasMoreTokens()) {
				dataTypeNames.add(st.nextToken());
			}
			items.put("dataTypeNames", dataTypeNames);
			loadItem.put("dataTypeNames", dataTypeNames);

			st = new StringTokenizer(cursor.getString(14), "/");//counts
			Vector<String> counts = new Vector<String>();
			while (st.hasMoreTokens()) {
				counts.add(st.nextToken());
			}
			items.put("counts", counts);

			st = new StringTokenizer(cursor.getString(15), "/");//viewNums
			Vector<String> viewNums = new Vector<String>();
			while (st.hasMoreTokens()) {
				viewNums.add(st.nextToken());
			}
			items.put("viewnums", viewNums);

			st = new StringTokenizer(cursor.getString(16), "/");//units
			Vector<String> units = new Vector<String>();
			while (st.hasMoreTokens()) {
				units.add(st.nextToken());
			}
			items.put("units", units);

			//2015.04.30 by lyk - 바(시고저종) 유형 추가
			st = new StringTokenizer(cursor.getString(26), "/");//etc01
			Vector<String> barTypes = new Vector<String>();
			while (st.hasMoreTokens()) {
				barTypes.add(st.nextToken());
			}
			items.put("barTypes", barTypes);
			//2015.04.30 by lyk - 바(시고저종) 유형 추가 end

			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
			try {
				if (cursor.getColumnCount() > 29) {
					st = new StringTokenizer(cursor.getString(29), "/");
					Vector<String> fxMarginTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						fxMarginTypes.add(st.nextToken());
					}
					items.put("fxMarginTypes", fxMarginTypes);

					st = new StringTokenizer(cursor.getString(30), "/");
					Vector<String> connTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						connTypes.add(st.nextToken());
					}
					items.put("connTypes", connTypes);

					st = new StringTokenizer(cursor.getString(31), "/");
					Vector<String> dayTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						dayTypes.add(st.nextToken());
					}
					items.put("dayTypes", dayTypes);

					st = new StringTokenizer(cursor.getString(32), "/");
					Vector<String> floorTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						floorTypes.add(st.nextToken());
					}
					items.put("floorTypes", floorTypes);

					st = new StringTokenizer(cursor.getString(33), "/");
					Vector<String> isSavedJongmok = new Vector<String>();
					while (st.hasMoreTokens()) {
						isSavedJongmok.add(st.nextToken());
					}
					items.put("isSavedJongmok", isSavedJongmok);

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

			//Base11의 분할차트 함수 호출.
			base11.setStorageDivision(nSel, isSyncJongmok, isSyncJugi, items, true);

			//2019. 08. 21 by hyh - 자동추세선 복원
			base11.setBaseSetting(cursor.getString(27));
		}else {
			//1분할로 초기화 처리 (Base11에 이벤트 호출할 것)
			//Base11 base11 = (Base11)mainBase.baseP;
			base11.setStorageDivision(11, false, false, null, false);

			mainBase.initChart("");

			//"/" 구분자로 데이터를 분리
			//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기>>
//	    		StringTokenizer st = new StringTokenizer(cursor.getString(10), "/");//symbol
			StringTokenizer st = new StringTokenizer(cursor.getString(10), "~");//symbol
			//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기<<
			Vector<String> symbols = new Vector<String>();
			while (st.hasMoreTokens()) {
				symbols.add(st.nextToken());
			}

			//2017.04.19 종목코드에 따라 저장된 분석툴 로드 >>
			if(symbols.size()>0)
			{
				m_storageSymbol = symbols.get(0);
			}
			//2017.04.19 종목코드에 따라 저장된 분석툴 로드 <<

			Vector<String> marketNames = null;
			try {
				st = new StringTokenizer(cursor.getString(24), "/");//marketName
				marketNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					marketNames.add(st.nextToken());
				}
			} catch(Exception e) {

			}

			Vector<String> codeNames = null;
			try {
				//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기>>
//		            st = new StringTokenizer(cursor.getString(6), "/");//codeNames
				st = new StringTokenizer(cursor.getString(6), "~");//codeNames
				//2014. 3. 21 차트 종목명 저장하는 구분자 변경하기<<
				codeNames = new Vector<String>();
				while (st.hasMoreTokens()) {
					codeNames.add(st.nextToken());
				}
			} catch(Exception e) {

			}

			st = new StringTokenizer(cursor.getString(11), "/");//lcodes
			Vector<String> lcodes = new Vector<String>();
			while (st.hasMoreTokens()) {
				lcodes.add(st.nextToken());
			}

			st = new StringTokenizer(cursor.getString(12), "/");//apCodes
			Vector<String> apCodes = new Vector<String>();
			while (st.hasMoreTokens()) {
				apCodes.add(st.nextToken());
			}

			st = new StringTokenizer(cursor.getString(13), "/");//dataTypeNames
			Vector<String> dataTypeNames = new Vector<String>();
			while (st.hasMoreTokens()) {
				dataTypeNames.add(st.nextToken());
			}

			st = new StringTokenizer(cursor.getString(14), "/");//counts
			Vector<String> counts = new Vector<String>();
			while (st.hasMoreTokens()) {
				counts.add(st.nextToken());
			}

			st = new StringTokenizer(cursor.getString(15), "/");//viewNums
			Vector<String> viewNums = new Vector<String>();
			while (st.hasMoreTokens()) {
				viewNums.add(st.nextToken());
			}

			st = new StringTokenizer(cursor.getString(16), "/");//units
			Vector<String> units = new Vector<String>();
			while (st.hasMoreTokens()) {
				units.add(st.nextToken());
			}

//			//2015.04.30 by lyk - 바(시고저종) 유형 추가
//			st = new StringTokenizer(cursor.getString(26), "/");//etc01
//			Vector<String> barTypes = new Vector<String>();
//			while (st.hasMoreTokens()) {
//				barTypes.add(st.nextToken());
//			}
//			if(barTypes.size()>0) COMUtil._neoChart._cvm.bIsOHLCType = barTypes.get(0);
//			//2015.04.30 by lyk - 바(시고저종) 유형 추가 end

			if(symbols.isEmpty() )
			{
				cursor.close();
				return false;
			}

			if(cursor.getCount() >32) {
				if (cursor.getString(33).equals("true"))
					COMUtil.symbol = symbols.get(0);
			}
			//2013. 3. 15  codename 이 빈칸일경우(null은아닌데) 예외떨어져서 마지막상태복원 안되는현상 해결
			if(codeNames!=null && codeNames.size() > 0) COMUtil.codeName=codeNames.get(0);
			if(lcodes.size()>0) COMUtil.lcode=lcodes.get(0);
			if(dataTypeNames.size()>0) COMUtil.dataTypeName=dataTypeNames.get(0);

			int nCnt = 200;
			try {
				nCnt = Integer.parseInt(counts.get(0));
			} catch(Exception e) {

			}

			if(counts.size()>0) COMUtil._neoChart._cvm.setInquiryNum(nCnt);

			int nViewCnt = 100;
			try {
				nViewCnt = Integer.parseInt(viewNums.get(0));
			} catch(Exception e) {

			}

			if(viewNums.size()>0) COMUtil._neoChart._cvm.setViewNum(nViewCnt);
			if(units.size()>0) COMUtil.unit=units.get(0);
			if(apCodes.size()>0) COMUtil.apCode=apCodes.get(0);
			if(marketNames.size()>0) COMUtil.market=marketNames.get(0);

			//2019. 08. 21 by hyh - 자동추세선 복원
			base11.setBaseSetting(cursor.getString(27));

			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
			try {
				if (cursor.getColumnCount() > 29) {
					st = new StringTokenizer(cursor.getString(29), "/");
					Vector<String> fxMarginTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						fxMarginTypes.add(st.nextToken());
					}

					if (fxMarginTypes.size() > 0) {
						mainBase.baseP._chart._cvm.nFxMarginType = Integer.parseInt(fxMarginTypes.get(0));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (cursor.getColumnCount() > 30) {
					st = new StringTokenizer(cursor.getString(30), "/");
					Vector<String> connTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						connTypes.add(st.nextToken());
					}

					if (connTypes.size() > 0) {
						mainBase.baseP._chart._cvm.bIsConn = connTypes.get(0).equals("1") ? true : false;
					}

					st = new StringTokenizer(cursor.getString(31), "/");
					Vector<String> dayTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						dayTypes.add(st.nextToken());
					}

					if (dayTypes.size() > 0) {
						mainBase.baseP._chart._cvm.bIsDay = dayTypes.get(0).equals("1") ? true : false;
					}

					st = new StringTokenizer(cursor.getString(32), "/");
					Vector<String> floorTypes = new Vector<String>();
					while (st.hasMoreTokens()) {
						floorTypes.add(st.nextToken());
					}

					if (floorTypes.size() > 0) {
						mainBase.baseP._chart._cvm.bIsFloor = floorTypes.get(0).equals("1") ? true : false;
					}

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
		}

		//2015.04.07 by lyk - 주기별 차트 설정 데이터 복원
//			System.out.println("Debug_복원0:"+cursor.getString(2));
		//2015.05.26 by lyk - 주기별 차트 설정 옵션 (로컬 저장/로드시 이미지 파일명 필드와 겹침 문제로 주기별 설정 옵션 복원 관련하여 이곳에서 한번더 처리한다)
		String[] tokenPeriodSaveTableGubun = cursor.getString(2).split(COMUtil.strPeriodConfigTableGubun);
		try
		{
			this.bIsPeriodConfigSave = tokenPeriodSaveTableGubun[0].equals("1");

			String[] tokenPeriodSave = tokenPeriodSaveTableGubun[1].split(COMUtil.strPeriodConfigSaveGubun2);
			//2015.05.26 by lyk - 주기별 차트 설정 옵션 (로컬 저장/로드시 이미지 파일명 필드와 겹침 문제로 주기별 설정 옵션 복원 관련하여 이곳에서 한번더 처리한다) end

			String strPeriodSave="";
			for(int i=0; i<tokenPeriodSave.length; i++) {
				Vector<Vector<String>> periodSaveGraphLists = new Vector<Vector<String>>();
				//dateType 분리
				StringTokenizer stDateType = new StringTokenizer(tokenPeriodSave[i], COMUtil.strPeriodConfigSaveGubun);
				String strPeriodType = "";
				String strContents = "";
				//데이터구조:"periodKey@@@contents###"
				while (stDateType.hasMoreTokens()) {
					strPeriodType = COMUtil.getDecodeData(stDateType.nextToken());
					strContents = COMUtil.getDecodeData(stDateType.nextToken());
				}

//				System.out.println("Debug_복원:"+strPeriodType+" content:"+strContents);
                localLoadPeriodSaveItem.put(strPeriodType, strContents);
			}
		}
		catch(Exception e){}
		//2015.04.07 by lyk - 주기별 차트 설정 데이터 복원 end

		//지표리스트.
		String[] token = cursor.getString(17).split("\\/");
		Vector<Vector<String>> graphLists = new Vector<Vector<String>>();
		String str="";
		for(int i=0; i<token.length; i++) {
//	    		   StringTokenizer st = new StringTokenizer(token[i], "#");
			StringTokenizer st = new StringTokenizer(token[i], "~");	//2015. 9. 2 주기별차트 멀티차트시 구분자 변경 (음수값이 들어갈 경우 배열이 잘못 구성됨)
			Vector<String> graphs=new Vector<String>();
			while (st.hasMoreTokens()) {
				str = COMUtil.getDecodeData(st.nextToken());
				if(!str.equals("")) {
					//2021.09.13 by lyk - kakaopay - 차트 저장/불러오기시 그래프명의 "/" 값을 "!@#" 로 치환 후 사용 (지표명 복원시 "/"가 구분자이기 때문) >>
					String sGraphTitle = str.replace("!@#", "/");
					graphs.add(sGraphTitle);
				}
			}
			graphLists.add(graphs);
		}
		if(graphLists.size()>0) {
			loadItem.put("graphList", graphLists.get(0));
			loadItem.put("graphLists", graphLists);
		}

		//저장된 분 설정 값 적용.
		StringTokenizer st = new StringTokenizer(cursor.getString(18), "#");
		String[] periodList = new String[st.countTokens()];
		int pIndex = -1;
		while (st.hasMoreTokens()) {
			pIndex++;
			str = st.nextToken();
			periodList[pIndex] = str;

		}
		if(pIndex!=-1) COMUtil.periodMinConfigList = periodList;

		//분석툴 정보 파싱.
//	          System.out.println("DEBUG_"+this.getClass().getName()+"_setLocalStorageState():"+cursor.getString(19));

		token = cursor.getString(19).split("\\/=/");
		Vector analInfoLists = new Vector();
		for(int i=0; i<token.length; i++) {
			analInfoLists.add(COMUtil.getAnalInfos(token[i]));
		}
		if(analInfoLists.size()>0) {
			loadItem.put("analInfo", analInfoLists.get(0));
			loadItem.put("analInfos", analInfoLists);
		}

        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
        st = new StringTokenizer(cursor.getString(4), "/");
        Vector<String> chartSizeInfos = new Vector<String>();
        while (st.hasMoreTokens()) {
            chartSizeInfos.add(st.nextToken());
        }
        loadItem.put("chartSizeInfo", chartSizeInfos);

        try {
            if (bIsPeriodConfigSave && chartSizeInfos!= null && chartSizeInfos.size()>0 ) {
                String strChartSizeInfo = chartSizeInfos.get(0);
                if (strChartSizeInfo.contains(COMUtil.strPeriodConfigSaveGubun2)) {
                    String[] tokenPeriodSave = strChartSizeInfo.split(COMUtil.strPeriodConfigSaveGubun2);
                    //2015.05.26 by lyk - 주기별 차트 설정 옵션 (로컬 저장/로드시 이미지 파일명 필드와 겹침 문제로 주기별 설정 옵션 복원 관련하여 이곳에서 한번더 처리한다) end

                    String strPeriodSave = "";
                    for (int i = 0; i < tokenPeriodSave.length; i++) {
                        String[] sizeInfo = tokenPeriodSave[i].split(COMUtil.strPeriodConfigSaveGubun1);
                        if(sizeInfo.length >= 2)
                            localLoadPeriodSizeInfo.put(sizeInfo[0], sizeInfo[1]);
                    }
                }
                this.loadPeriodSizeInfo = localLoadPeriodSizeInfo; //2015.04.27 by lyk - 주기별 차트 설정 추가
            }
        }catch(Exception e){
        }
        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

		COMUtil.loadItem=loadItem;
        this.loadPeriodSaveItem = localLoadPeriodSaveItem; //2015.04.27 by lyk - 주기별 차트 설정 추가

		if(COMUtil.apiMode) {
			//COMUtil.setSendTrType("storageType");
			if(chartMode != COMUtil.DIVIDE_CHART) {
				this.sendTrType = "storageType";
				Base11 base = (Base11)mainBase.baseP;

				String strCode = COMUtil.symbol;
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				//		            if(COMUtil.lcode.equals("SC0")) {
				//		            	strCode = "1"+strCode;
				//		            }
				dic.put("name", COMUtil.codeName);
				dic.put("market", COMUtil.market);
				dic.put("symbol", strCode);
				dic.put("lcode", COMUtil.lcode);

				Vector<String> dataTypeNames = (Vector<String>)loadItem.get("dataTypeNames");
				if(dataTypeNames!=null && dataTypeNames.size()>1) {
					dic.put("period", dataTypeNames.get(0)); //분할정보의 첫번째 항목으로 설정.
				} else {
					dic.put("period", COMUtil.dataTypeName);
				}
				//	    		if(dataTypeNames==null || dataTypeNames.size()<1) {
				//	    			dic.put("period", "2");
				//	    		} else {
				//	    			dic.put("period", COMUtil.dataTypeName);
				//	    		}

				int viewNum = mainBase.baseP._chart._cvm.getViewNum();
				dic.put("viewnum", String.valueOf(viewNum));
				dic.put("count", COMUtil.count);

				Vector<String> units = (Vector<String>)loadItem.get("units");
				if(units!=null && units.size()>1) {
					dic.put("unit", units.get(0)); //분할정보의 첫번째 항목으로 설정.
//			    			System.out.println("=========unit1:"+units.get(0));
				} else {
					dic.put("unit", COMUtil.unit);
//			    			System.out.println("==========unit2:"+COMUtil.unit);
				}
				//	            if(units==null || units.size()<1) {
				//	            	dic.put("unit", "1");
				//	            } else {
				//	            	dic.put("unit", COMUtil.unit);
				//	            }

				dic.put("apcode", COMUtil.apCode);
				dic.put("selIndex", String.valueOf(base.m_nRotateIndex));

				//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<
				if (base.m_nRotateIndex >= 0 && base.chartList.size() > base.m_nRotateIndex) {
					BaseChart pChart = base.chartList.get(base.m_nRotateIndex);
					dic.put("fxmargintype", pChart._cvm.nFxMarginType);
					dic.put("conn", pChart._cvm.bIsConn);
					dic.put("day", pChart._cvm.bIsDay);
					dic.put("floor", pChart._cvm.bIsFloor);
				}
				else {
					dic.put("fxmargintype", mainBase.baseP._chart._cvm.nFxMarginType);
					dic.put("conn", mainBase.baseP._chart._cvm.bIsConn);
					dic.put("day", mainBase.baseP._chart._cvm.bIsDay);
					dic.put("floor", mainBase.baseP._chart._cvm.bIsFloor);
				}
				//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

				//2023.11.27 by lyk - kakaopay - 외부 설정값 저장소 별도 구성(추가수정) >>
				SharedPreferences sharedPref = null;
				try {
					String tableName = COMUtil._mainFrame.strFileName;
					if (tableName.equals("stock")) {
						sharedPref = COMUtil._chartMain.getPreferences(Context.MODE_PRIVATE);
					} else if (!tableName.equals("")) {
						sharedPref = COMUtil._chartMain.getSharedPreferences(tableName, Context.MODE_PRIVATE);
					} else {
						sharedPref = COMUtil._chartMain.getPreferences(Context.MODE_PRIVATE);
					}
				} catch(Exception e) {
					return false;
				}
				//2023.11.27 by lyk - kakaopay - 외부 설정값 저장소 별도 구성(추가수정) <<

				boolean isAvgBuyPriceFunc = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.avgBuyPriceFunc), false);
				boolean isBuySellPriceFunc = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.buySellPriceFunc), false);
				boolean isCorporateCalendarFunc = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.corporateCalendarFunc), false);
				boolean isSupportResistanceLine = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.supportResistanceLine), false);
				boolean isAutoTrendWaveLine = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.autoTrendWaveLine), false);
				boolean isMovingAverageLine = sharedPref.getBoolean(COMUtil._chartMain.getString(R.string.movingAverageLine), false);

				mainBase.baseP._chart._cvm.isAvgBuyPriceFunc = isAvgBuyPriceFunc;
				mainBase.baseP._chart._cvm.isBuySellPriceFunc = isBuySellPriceFunc;
				mainBase.baseP._chart._cvm.isCorporateCalendarFunc = isCorporateCalendarFunc;
				mainBase.baseP._chart._cvm.isSupportResistanceLine = isSupportResistanceLine;
				mainBase.baseP._chart._cvm.isAutoTrendWaveLine = isAutoTrendWaveLine;
				mainBase.baseP._chart._cvm.isMovingAverageLine = isMovingAverageLine;

				//2021.05.03 by hanjun.Kim - kakaopay - DrChartView에서 참조될수 있도록 수정 >>
				if (isAvgBuyPriceFunc) dic.put("avgBuyPriceFunc", "1"); //평단가 데이터 요청
				else dic.put("avgBuyPriceFunc", "0");
				if (isBuySellPriceFunc) dic.put("buySellPriceFunc", "1"); //매수,매도 데이터 요청
				else dic.put("buySellPriceFunc", "0");
				if (isCorporateCalendarFunc) dic.put("corporateCalendarFunc", "1"); //기업 캘린더
				else dic.put("corporateCalendarFunc", "0");
				if (isCorporateCalendarFunc) dic.put("corporateCalendarFunc", "1"); //기업 캘린더
				else dic.put("corporateCalendarFunc", "0");
				//2021.05.03 by hanjun.Kim - kakaopay - DrChartView에서 참조될수 있도록 수정 <<
				//2021.04.27 by lyk - kakaopay - 외부 설정값 저장소 별도 구성 <<
				Log.d("kfits","setLocalStorageState's isMovingAverageLine: "+isMovingAverageLine);
				//delegate 처리
				if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_STORAGE_TYPE, dic);
			}

//			//skin
			if(COMUtil._neoChart != null) {
				//2015. 3. 4 차트 테마 메인따라가기 추가>>
				if(COMUtil.bIsAutoTheme) {
					if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
						mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_BLACK);
					} else {
						mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_WHITE);
					}
				} else {
					if(g_nSkinType == COMUtil.SKIN_BLACK) {
						mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_BLACK);
					} else {
						mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_WHITE);
					}
				}
			}
//			//2015. 3. 4 차트 테마 메인따라가기 추가<<
		} else {

			if(g_nSkinType != COMUtil.SKIN_BLACK)
				mainBase.baseP.setChartToolBar(COMUtil.MENU_STYLE_WHITE);

			COMUtil.sendTR("storageType");

		}

		// 2016.05.31 기준선 대비, 색상 굵기 >>
		try {
			Base11 base = (Base11)mainBase.baseP;
			strConv = cursor.getString(27);
			String[] strSettingInfos = strConv.split("#");
			if(strSettingInfos.length<1) {
				COMUtil.saveTitle = strConv;
			} else {

				// 멀티차트 구분하여 처리
				String[] lists = strSettingInfos[0].split("\\/");

				if(base.chartList.size()==1) { //일반차트
					String strItem = lists[0];
					if(strItem!=null || !strItem.equals("")) {
						arrConv = strItem.split("=");
						ChartViewModel _cvm = (ChartViewModel)mainBase.baseP._chart._cvm;
						if(_cvm.baseLineType!=null && _cvm.baseLineType.size()>0) {
							_cvm.baseLineType.clear();
						}
						_cvm.baseLineColors.clear();
						_cvm.baseLineThicks.clear();
						COMUtil.setEnvData(_cvm);
						for (String type : arrConv) {
							if(!type.equals("")) {
								_cvm.baseLineType.add(Integer.parseInt(type));
							}
						}
					}
				} else { //멀티차트
					int cLen = base.chartList.size();
					if(lists.length>=cLen) {
						for(int i=0; i<cLen; i++) {
							String strItem = lists[i];
							if(strItem==null || strItem.trim().equals("")) {
								continue;
							}
							arrConv = strItem.split("=");

							BaseChart pBaseChart = (BaseChart)base.chartList.get(i);
							ChartViewModel _cvm = (ChartViewModel)pBaseChart._cvm;
							if(_cvm.baseLineType!=null && _cvm.baseLineType.size()>0) {
								_cvm.baseLineType.clear();
							}

							_cvm.baseLineColors.clear();
							_cvm.baseLineThicks.clear();
							COMUtil.setEnvData(_cvm);

							for (String type : arrConv) {
								if(!type.trim().equals("")) {
									_cvm.baseLineType.add(Integer.parseInt(type.trim()));
								}
							}
						}
					}
				}

//				if(strSettingInfos.length>2)
//				{
//					String strItem = "";
//					String strAutoInfo = strSettingInfos[2];
//					String[] strAutoInfos = strAutoInfo.split("---");
//					int cLen = base.chartList.size();
//					String[] lists2;
//					if(strAutoInfos.length>=cLen) {
//						for(int i=0; i<cLen; i++) {
//							strItem = strAutoInfos[i];
//							lists2 = strItem.split("=");
//							BaseChart pBaseChart = (BaseChart)base.chartList.get(i);
//							if(strItem==null || strItem.equals("")) {
//								continue;
//							}
//							pBaseChart._cvm.autoTrendWaveType = Integer.parseInt(lists2[0]);
//							pBaseChart._cvm.autoTrendHighType = Integer.parseInt(lists2[1]);
//							pBaseChart._cvm.autoTrendLowType = Integer.parseInt(lists2[2]);
//							pBaseChart._cvm.preName = Integer.parseInt(lists2[3]);
//							pBaseChart._cvm.endName = Integer.parseInt(lists2[4]);
//						}
//					}
//				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
		// 2016.05.31 기준선 대비, 색상 굵기 <<

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		if(!COMUtil._mainFrame.bShowOneTouch)
			cursor.close();

		return true;
	}

	public int getDivideNum()
	{
		Base11 base11 = (Base11)mainBase.baseP;

		if(base11.m_nTotNum >1)
			return base11.m_nTotNum;
		return 1;
	}

	public ArrayList<String> getDivideCodeList() {
		Base11 base = (Base11)mainBase.baseP;
		int nTotalCount = base.m_nTotNum;

		ArrayList<String> codeList = new ArrayList<String>();

		for(int i=0; i<base.chartList.size(); i++)
		{
			BaseChart pBaseChart = base.chartList.get(i);
			String strCode = pBaseChart._cdm.codeItem.strCode;
			if(strCode == null)
			{
				nTotalCount = i;
				break;
			}

			codeList.add(strCode);
		}

		return codeList;
	}

	public void setSelectIndex(int nIndex)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.selectChart_index(nIndex);
	}

	public void setRotateState(boolean state)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setRotateState(state);
	}

	//2012.08.29 by LYH >> ELW 기초자산 추가.
	public void addIndexGraph(String strTitle)
	{
		if(!mainBase.baseP._chart._cdm.codeItem.strBaseMarket.equals(strTitle))
		{
			mainBase.baseP._chart._cdm.codeItem.strBaseMarket = strTitle;
			if(mainBase.baseP._chart.basic_block != null)
				mainBase.baseP._chart.basic_block.resetTitleBounds();
		}
		mainBase.addTrendConfig("BaseMarket");
	}
	public void removeIndexGraph()
	{
		mainBase.removeTrendConfig("BaseMarket");
	}
	public int getDataCount()
	{
		NeoChart2 chart = mainBase.baseP._chart;
		return chart._cdm.getCount();
	}

	public void removeUserGraph(String graph) {
		mainBase.removeTrendConfig(graph);
	}
	public void setVisibleUserGraph(String sData, String visible) {
		mainBase.setVisibleUserGraph(sData, visible);
	}

	//2012.08.29 by LYH <<
	public void setPeriodList(String sData)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setPeriodList(sData);
	}
	public void setCompareData(String strCompData, Vector<String> cmpArr)
	{
		
		COMUtil.strCompData = COMUtil.getStringFromArray(cmpArr, ";");
    	if(COMUtil._mainFrame != this)
    	{
    		COMUtil._mainFrame = this;
    		COMUtil._neoChart = this.mainBase.baseP._chart;
    	}
	   if(strCompData != null)
	    {
		   String[] values = strCompData.split(";");
	        if(values.length > 2)
	        {
//	            String strCount = values[0];
	            String strCodes = "";
	            String strNames = "";
	            String strMarkets = "";
	            String strOneData;
	            for(int i=1; i<values.length; i++)
	            {
	                strOneData = values[i];
	                String[] strOneArray = strOneData.split("\\^");
	                if(strOneArray.length>=5)
	                {
	                	String strCheck = strOneArray[4];
	                    if(strCheck.equals("1"))
	                    {
	                    	String strCode = strOneArray[1];
	                    	String strType = strOneArray[3];
	                    	
	                        strCodes = strCodes + strCode;
	                        strCodes = strCodes + ";";
	                        strNames = strNames + strOneArray[2];
	                        strNames = strNames + ";";
	                        strMarkets = strMarkets + strType;
	                        strMarkets = strMarkets + ";";
	                    }
	                }
	            }
	            Base11 base11 = (Base11)mainBase.baseP;
	            base11.resetCompareCodes(strCodes, strNames, strMarkets);
	        }
	    }

	}
	
	public void setStorageCompareData(String strCompData)
	{
		String[] cmpArr = strCompData.split(";");
		if(COMUtil._mainFrame != this)
		{
			COMUtil._mainFrame = this;
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}
		String strCodes = "";
		String strNames = "";
		String strMarkets = "";
		String strOneData;
		for(int i=0; i<cmpArr.length; i++)
		{
			strOneData = cmpArr[i];
			String[] strOneArray = strOneData.split("\\^");
			try {
				String strCode = strOneArray[1];
				String strType = strOneArray[3];

				strCodes = strCodes + strCode;
				strCodes = strCodes + ";";
				strNames = strNames + strOneArray[2];
				strNames = strNames + ";";
				strMarkets = strMarkets + strType;
				strMarkets = strMarkets + ";";
			} catch (Exception e) {

			}
		}
		Base11 base11 = (Base11)mainBase.baseP;
		base11.resetCompareCodes(strCodes, strNames, strMarkets);

	}

	public void resetPriceData(String strName, String strPrice, String strSign, String strChange, String strRate, String strVolume, String strCode)
	{
		 Base11 base11 = (Base11)mainBase.baseP;
         base11.resetPriceData(strName, strPrice, strSign, strChange, strRate, strVolume, strCode);
	}	
	
	public void clearCompareData()
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.clearCompareData();
	}

	public void setVisibleCompareDataIndex(boolean visible, int index) {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setVisibleCompareDataIndex(visible, index);
	}

	public void resetBasicUI(String strUI)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.resetBasicUI(strUI);
	}
	public void showCrossLine(boolean bShow)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.showCrossLine(bShow);
	}
	//2012.11.27 by LYH >> 진법 및 승수 처리.
	public void setPriceFormat(String sCode, int nScale, int nDecPoint, int nLogDisp)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setPriceFormat(sCode, nScale, nDecPoint, nLogDisp);
	}
	//2012.11.27 by LYH <<

	//2021.08.11 by lyk - kakaopay - 외부에서 컬러셋 지정시 호출 (차트 라이브러리 외부에서 static 변경 안됨) >>
	public void setChartColors(int[][] values) {
		CoSys.CHART_COLORS = values;
	}
	//2021.08.11 by lyk - kakaopay - 외부에서 컬러셋 지정시 호출 (차트 라이브러리 외부에서 static 변경 안됨) <<

	//2021.04.03 by lyk - kakaopay - setMethod 함수와 비슷한 기능을 하는 get함수 추가(향후 추가되는 클래스 함수는 getMethod를 활용할 것) >>
	public Object getMethod(String strMethodName, String strParam) {
		Object rtnObj = null;

		Base11 base11 = (Base11)mainBase.baseP;
		NeoChart2 neoChart = base11._chart;

		if(strMethodName.equals("getSelectedChartType")) {
			rtnObj = String.valueOf(ChartUtil.JBONG);

			String selectedJipyo = "";
			if(neoChart._cvm.isStandGraph()) {
				selectedJipyo = neoChart._cvm.sStandGraphName;
			} else {
				selectedJipyo = COMUtil.selectedJipyo;
			}

			 if(selectedJipyo == null || selectedJipyo.equals(COMUtil.CANDLE_TYPE_CANDLE)) {
				rtnObj = String.valueOf(ChartUtil.JBONG);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_LINE)) {
				rtnObj = String.valueOf(ChartUtil.PLINE);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY)) {
				rtnObj = String.valueOf(ChartUtil.JBONG_TRANSPARENCY);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_PF)) {
				rtnObj = String.valueOf(ChartUtil.PNF);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_SWING)) {
				rtnObj = String.valueOf(ChartUtil.SWING);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_RENKO)) {
				rtnObj = String.valueOf(ChartUtil.RENKO);
			} else if(selectedJipyo.equals(COMUtil.CANDLE_TYPE_KAGI)) {
				rtnObj = String.valueOf(ChartUtil.KAGI);
			}
		} else if(strMethodName.equals("getAvgBuyState")) {
			rtnObj = neoChart._cvm.isAvgBuyPriceFunc;
		} else if(strMethodName.equals("getBuySellState")) {
			rtnObj = neoChart._cvm.isBuySellPriceFunc;
		} else if(strMethodName.equals("getCorporateCalendarState")) {
			rtnObj = neoChart._cvm.isCorporateCalendarFunc;
		} else if(strMethodName.equals("getSupportResistanceLine")) {
			rtnObj = neoChart._cvm.isSupportResistanceLine;
		} else if(strMethodName.equals("getAutoTrendWaveLine")) {
			rtnObj = neoChart._cvm.isAutoTrendWaveLine;
		} else if(strMethodName.equals("getMovingAverageLineState")) {
			Log.d("kfits","Mainframe getMethod() isMovingAverageLine"+neoChart._cvm.isMovingAverageLine);
			rtnObj = neoChart._cvm.isMovingAverageLine;
		} else if(strMethodName.equals("getVisibleGraph")) {
			return neoChart.getVisible(strParam);
		} else if(strMethodName.equals("getEndDate")) {
			rtnObj = neoChart._cdm.getLastStringData("자료일자");
			if(rtnObj == null) {
				return "";
			}
		} else if(strMethodName.equals("getStartDate")) {
			rtnObj = neoChart._cdm.getFirstStringData("자료일자");
			if(rtnObj == null) {
				return "";
			}
		} else if(strMethodName.equals("getStringData")) {
			if(strParam == "") {
				return null;
			}
			rtnObj = neoChart._cdm.getStringData(strParam);
		}

		return rtnObj;
	}
	//2021.04.03 by lyk - kakaopay - setMethod 함수와 비슷한 기능을 하는 get함수 추가(향후 추가되는 클래스 함수는 getMethod를 활용할 것) <<

	public void setMethod(String strMethodName, String strParam) {
		if(strMethodName.equals("setFullChart")) {
			Base11 base11 = (Base11)mainBase.baseP;
			base11.totalViewMode();
		} else if(strMethodName.equals("selectChart")) {
			int index = 0;
			try {
				index = Integer.parseInt(strParam);
			} catch (Exception e) {
				return;
			}
			Base11 base11 = (Base11)mainBase.baseP;
			base11.selectChart_custom_index(index);

		}
		//2013. 6. 13 시장정보차트 외곽 색상변경,  ELW-ETF 차트 시세바 아이템 감추기 기능 setMethod로 연결 >>
		else if(strMethodName.equals("setAnotherColorWhiteSkin"))
		{
			//시장정보차트 외곽 색상변경 
			boolean bFlag = false;
			if(strParam.equals("1"))
			{
				bFlag = true;
			}
			setAnotherColorWhiteSkin(bFlag);
		}
		else if(strMethodName.equals("setChartItemViewHidden"))
		{
			boolean bFlag = false;
			if(strParam.equals("1"))
			{
				bFlag = true;
			}

			//2013. 12. 16 태블릿에서 차트 시세바 감추기>>
//			setChartItemViewHidden(bFlag);
			try {
				Base11 base11 = (Base11)mainBase.baseP;
				base11.chartList.get(0).showChartItem(bFlag);
				base11.resizeChart();
			} catch (Exception e) {

			}

		}
		//2013. 6. 13 시장정보차트 외곽 색상변경,  ELW-ETF 차트 시세바 아이템 감추기 기능 setMethod로 연결 <<
		//2013. 6. 17 시장정보차트 viewNum 갯수 20개로 조정  >>
		else if(strMethodName.equals("setChartViewNum"))
		{
			int nViewNum = 0;
			//파라미터로 넘어온 viewnum을 세팅한다. 
			try
			{
				nViewNum = Integer.parseInt(strParam);
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				NeoChart2 neoChart = base11._chart;
				if(nViewNum>0)
					neoChart._cvm.setViewNum(nViewNum);
				else {
					//neoChart._cvm.setOnePage(1);
					setOnePage(1);
				}
			}
			catch(Exception e)
			{
			}
		}
		//2013. 6. 17 시장정보차트 viewNum 갯수 20개로 조정  <<

		//2013. 6. 17 시장정보차트 차트화면 좌측 팝업버튼3개 보이지 않게 수정   >>
		else if(strMethodName.equals("setJisuTypeMiniFlag"))
		{
			/**
			 * COMUtil의 isJisuType  으로 사용하면 차트가 2개이상인 화면에서는  기본값 false로 초기화되어 버리는 현상이 있다.
			 * 따라서 독립적인 차트 속성으로 설정하기 위하여 base11에 동일한 플래그를 하나 더 설정한 것이며,
			 * COMUtil 의 값을 그대로 가져온다.  이 조건문으로 들어오기 위해서는 메인 axChartEx 에서 setJisuTypeMiniFlag함수가 true값으로 호출되었을때만 해당된다.
			 * 그러므로 안드로이드 폰 버전에 영향이 없고, 차트화면 독립적으로 적용가능하다.   2013.6.17  by drfnkimsh
			 * */
			Base11 base11 = (Base11)mainBase.baseP;
			base11.isJisuType = COMUtil.isJisuType;
		}
		//2013. 6. 17 시장정보차트 차트화면 좌측 팝업버튼3개 보이지 않게 수정   <<
		//2013.07.22 by LYH >> 투자자 차트 그래프 보이기/숨기기 기능
		else if(strMethodName.equals("setVisibleGraph"))
		{
			try
			{
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				String[] values = strParam.split(";");
				if(values.length >= 2)
				{
					String strTitleData;
					String strVisibleData;
					strTitleData = values[0];
					strVisibleData = values[1];
					String[] strTitleArray = strTitleData.split("\\^");
					String[] strVisibleArray = strVisibleData.split("\\^");
					for(int i=0; i<strTitleArray.length; i++)
					{
						neoChart.setVisible(strTitleArray[i], strVisibleArray[i].equals("1")?true:false);
					}
				}
			}
			catch(Exception e)
			{
			}
		}
		//2013.07.22 by LYH <<
		//2013.07.31 >> 기준선 라인 차트 타입 추가 
		else if(strMethodName.equals("setStandardValue"))
		{
			try
			{
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				neoChart._cdm.codeItem.strStandardPrice = strParam;
				neoChart.repaintAll();
				//neoChart.setVisible(strTitleArray[i], strVisibleArray[i].equals("1")?true:false);
			}catch(Exception e)
			{
			}
		}
		//2013.07.31 <<
		//2013.08.26 by LYH >> 차트확대/축소
		else if(strMethodName.equals("setExpandChart"))	//차트확대
		{
			try
			{
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				neoChart.clickButtonToExpansion();
				//neoChart.setVisible(strTitleArray[i], strVisibleArray[i].equals("1")?true:false);
			}catch(Exception e)
			{
			}
		}
		else if(strMethodName.equals("setReduceChart"))	//차트축소
		{
			try
			{
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				neoChart.clickButtonToReduction();
				//neoChart.setVisible(strTitleArray[i], strVisibleArray[i].equals("1")?true:false);
			}catch(Exception e)
			{
			}
		}
		//2013.09.02 by LYH <<
		else if(strMethodName.equals("setLineProperty"))	//차트축소
		{
			try
			{
				//차트에 viewnum을 세팅.
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				neoChart.changeLineProperty(strParam);
				//neoChart.setVisible(strTitleArray[i], strVisibleArray[i].equals("1")?true:false);
			}catch(Exception e)
			{
			}
		}
		else if(strMethodName.equals("setPriceFormat"))	//차트축소
		{
			try
			{
				//2020.05.11 by LYH >> 진법 처리 Start
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;
				String[] strValues = strParam.split(":");
				if(strValues.length>=3)
				{
					try{
						if(neoChart._cvm.bInvestorChart) {
							if (strValues.length >= 3)
								neoChart._cdm.setPriceFormat_Investor("× 0.0001", strValues[0], Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]));
						}
						else {
							if (strValues.length >= 4)
								base11.setPriceFormat(strValues[0], Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]), Integer.parseInt(strValues[3]));
							else
								base11.setPriceFormat(strValues[0], Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]), Integer.parseInt(strValues[2]));
						}
					}catch(Exception e)
					{

					}
				}
				//2020.05.11 by LYH >> 진법 처리 End
//
//				Base11 base11 = (Base11)mainBase.baseP;
//				BaseChart neoChart = base11._chart;
//				String[] strValues = strParam.split("\\^");
//				if(strValues.length>=2)
//				{
//					try{
//						if(neoChart._cvm.bInvestorChart)
//						{
//							if(strValues.length >= 3)
//								neoChart._cdm.setPriceFormat_Investor("× 0.0001", strValues[0], Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]));
//							else
//								neoChart.setPriceFormat(Integer.parseInt(strValues[0]), Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]));
//						}
//						else
//							neoChart.setPriceFormat(Integer.parseInt(strValues[0]), Integer.parseInt(strValues[1]), Integer.parseInt(strValues[2]));
//					}catch(Exception e)
//					{
//
//					}
//				}

			}catch(Exception e)
			{
			}
		}
		//2013.09.02 by LYH <<
		//2013.09.17 by LYH>> 사용자 패턴 저장
		else if(strMethodName.equals("savePatternImage"))
		{
			Base13 base = (Base13)mainBase.baseP;
			base.drawImage(strParam);
		}
		//2013.09.17 by LYH<<
		//2013.09.24 by LYH>> 패턴 기간 설정
		else if(strMethodName.equals("setPatternPeriod"))
		{
			Base13 base = (Base13)mainBase.baseP;
			try{
				base.setGridCount(Integer.parseInt(strParam));
			}catch(Exception e)
			{

			}
		}
		//2013.09.24 by LYH<<
		//2013.10.02 by LYH>> 패턴 지우기
		else if(strMethodName.equals("clearPattern"))
		{
			Base13 base = (Base13)mainBase.baseP;
			try{
				base.clearPattern();
			}catch(Exception e)
			{

			}
		}
		//2013.10.02 by LYH<<
		//2015. 1. 21 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기>>
		else if(strMethodName.equals("drawYScalePriceLine"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			base11._chart.setYScalePriceLine(strParam);
		}
		//2015. 1. 21 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기<<
		//2015. 1. 22 가상매매연습기 데이터 수신 ctlchartex 인터페이스>>
		else if(strMethodName.equals("updateTradeTutorialData"))
		{
			this.mainBase.baseP._chart.addTradePacketData(strParam);
		}
		//2015. 1. 22 가상매매연습기 데이터 수신 ctlchartex 인터페이스<<
		//2015. 1. 22 가상매매연습기 매도/매수 표시하는 ctlchartex 인터페이스>>
		else if(strMethodName.equals("setTradeData"))
		{
			if(strParam != null)
			{
				this.mainBase.baseP._chart.setTradeData(strParam);
			}
		}
		else if(strMethodName.equals("resetTradeData"))
		{
			this.mainBase.baseP._chart.resetTradeData();
		}
		//2015. 1. 22 가상매매연습기 매도/매수 표시하는 ctlchartex 인터페이스<<

		//2015. 2. 24 차트 상하한가 표시>>
		else if(strMethodName.equals("setChartUpperLowerLimitData"))
		{
//					this.mainBase.baseP._chart.setChartUpperLowerLimitData(strParam); 
			((Base11)this.mainBase.baseP).setChartUpperLowerLimitData(strParam);
		}
		//2015. 2. 24 차트 상하한가 표시<<
		//2015. 3. 18 보이는 개수 및 조회 갯수 설정>>
		else if(strMethodName.equals("useChangeRequestDataLength"))
		{
			((Base11)this.mainBase.baseP).setUseChangeRequestDataLength(strParam.equals("0") ? false : true);
		}
		else if(strMethodName.equals("setRequestDataLength"))
		{
			((Base11)this.mainBase.baseP).setRequestDataLength(strParam);
		}
		//2015. 3. 18 보이는 개수 및 조회 갯수 설정<<
		//2015. 3. 18 차트화면 열렸을 때 수정주가 체크가 되어있으면 수정주가조회>>
		else if(strMethodName.equals("setAdjustedStock"))
		{
			this.bIsAdjustedStock = strParam.equals("1") ? true : false;
		}
		//2015. 3. 18 차트화면 열렸을 때 수정주가 체크가 되어있으면 수정주가조회<<
		else if(strMethodName.equals("setTouchEventType"))
		{
			try
			{
				this.mainBase.baseP._chart._cvm.nTouchEventType = Integer.parseInt(strParam);
			}catch(Exception e){}
		}
		else if(strMethodName.equals("line")) {
			this.bIsLineChart = strParam.equals("YES");
		}
        //2016.08.25 by LYH >> 2일 라인차트 타입 추가
		else if(strMethodName.equals("line2")) {
			this.bIsLine2Chart = strParam.equals("YES");
		}
        //2016.08.25 by LYH << 2일 라인차트 타입 추가
		//2015. 10. 19 현재가차트 투명 처리>>
		else if(strMethodName.equals("setChartBgTransparent"))
		{
			((Base11)this.mainBase.baseP).setChartBgTransparent(strParam);
		}
		else if(strMethodName.equals("setHideChartTitle"))
		{
			((Base11)this.mainBase.baseP).setHideChartTitle(strParam);
		}
		else if(strMethodName.equals("linefill")) {
			this.bIsLineFillChart = strParam.equals("YES");
		}
		else if(strMethodName.equals("setPieData"))
		{
			if(strParam != null)
			{
				//PieChart data format
				//value = @"name1;name2;name3=data1;data2;data3=colorType=alignType";
				//value = @"개인;외국인;기관계=10;20;30=2=bottom";
				String[] sepDatas = strParam.split("=");
				String sName = sepDatas[0];
				String sValue = sepDatas[1];

				String[] names = sName.split(";");
				String[] values = sValue.split(";");

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				List<String> lNames = new ArrayList<String>();
				List<String> lValues = new ArrayList<String>();
				double total = 0.0;
				for(int i=0; i<names.length; i++)
					lNames.add(names[i]);
				for(int i=0; i<values.length; i++) {
					try {
						total += Double.parseDouble(values[i]);
					} catch (Exception e) {

					}
				}
				if(total != 0) {
					for (int i = 0; i < values.length; i++)
						lValues.add(values[i]);
				}
				dic.put("sliceNames", lNames);
				dic.put("sliceDatas", lValues);
				if(sepDatas.length>3)
				{
					dic.put("colorType",  sepDatas[2]);
					dic.put("alignType", sepDatas[3]);

					if(sepDatas.length>4) {
						dic.put("toolTipType",  sepDatas[4]);
					}

					if(sepDatas.length>5) {
						dic.put("title", sepDatas[5]);
					}
//					if(sepDatas.length>5) {
//						String sColorIndexes = sepDatas[5];
//
//						String[] colorIndexes = sColorIndexes.split(";");
//						List<Integer> lColorIndexes = new ArrayList<Integer>();
//						for(int i=0; i<names.length; i++)
//							lColorIndexes.add(Integer.parseInt(colorIndexes[i]));
//
//						dic.put("sliceColorIndexes", lColorIndexes);
//					}

				}

				Base base = mainBase.baseP;

				base.setPacketData_hashtable(dic);
			}
		}else if(strMethodName.equals("setBarData"))
		{
			if(strParam != null)
			{
				//PieChart data format
				//value = @"name1;name2;name3=data1;data2;data3==data1;data2;data3=colorIndex";
				//value = @"개인;외국인=10;20=10;20=0,1";
				String[] sepDatas = strParam.split("=");
				String sDatas1 = sepDatas[0];
				String sDatas2 = sepDatas[1];
				String sDatas3 = sepDatas[2];
				String sDatas4 = sepDatas[3];

				String[] datas1 = sDatas1.split(";");
				String[] datas2 = sDatas2.split(";");
				String[] datas3 = sDatas3.split(";");
				String[] datas4 = sDatas4.split(";");

				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				List<String> lDatas1 = new ArrayList<String>();
				List<String> lDatas2 = new ArrayList<String>();
				List<String> lDatas3 = new ArrayList<String>();
				List<String> lDatas4 = new ArrayList<String>();
				for(int i=0; i<datas1.length; i++)
					lDatas1.add(datas1[i]);
				for(int i=0; i<datas2.length; i++)
					lDatas2.add(datas2[i]);
				for(int i=0; i<datas3.length; i++)
					lDatas3.add(datas3[i]);
				for(int i=0; i<datas4.length; i++)
					lDatas4.add(datas4[i]);
				dic.put("datas1", lDatas1);
				dic.put("datas2", lDatas2);
				dic.put("datas3", lDatas3);
				dic.put("datas4", lDatas4);
				Base base = mainBase.baseP;

				base.setPacketData_hashtable(dic);
			}
		}else if(strMethodName.equals("setSpiderData"))
		{
			if(strParam != null)
			{
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("levelDatas", strParam);
				Base base = mainBase.baseP;

				base.setPacketData_hashtable(dic);
			}
		}
		//2015. 10. 19 현재가차트 투명 처리<<
		//2015. 12. 10 맵-차트간 분틱값 동기화>>
		else if(strMethodName.equals("setSyncPeriodValueToChart"))
		{
			m_strPeriodValue = strParam;
			setPeriodList(m_strPeriodValue);
            if(indicatorConfigView != null) {
				if (COMUtil.bIsForeignFuture || COMUtil.bIsGlobalStock)
					indicatorConfigView.initPeriodValueList();
				else
					indicatorConfigView.initPeriodValues();
			}
		}
		//2015. 12. 10 맵-차트간 분틱값 동기화<<
		else if(strMethodName.equals("setUseCurrentColor"))
		{
			((Base11)this.mainBase.baseP).setUseCurrentColor(strParam);
		}
		else if(strMethodName.equals("news")) {
			this.bIsLineChart = strParam.equals("YES");
			this.bIsNewsChart = strParam.equals("YES");
		}
		else if(strMethodName.equals("setLineColor"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;
			neoChart.setLineColor(strParam);
		}
		else if(strMethodName.equals("setRequestCompareData"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			if(strParam.equals("1"))
				base11.setRequestCompareData(true);
			else
				base11.setRequestCompareData(false);
				
		}
		else if(strMethodName.equals("highlowsign")) {
			if(strParam.equals("YES")) {
				this.bIsHighLowSign = true;
			} else {
				this.bIsHighLowSign = false;
			}
		}
		else if(strMethodName.equals("hidexscale")) {
			if(strParam.equals("YES")) {
				this.bIsHideXscale = true;
			} else {
				this.bIsHideXscale = false;
			}
		}
		else if(strMethodName.equals("setNewsDate"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;
			neoChart.setNewsDate(strParam);
		}
		else if(strMethodName.equals("showAlert"))
		{
			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
			alert.setTitle("");
			alert.setMessage(strParam);
			alert.setOkButton("확인", null);

			alert.show();
			COMUtil.g_chartDialog = alert;
		}
        else if(strMethodName.equals("reloadChart"))
        {
            reloadChart();
        }
		else if(strMethodName.equals("setDeleteIndicaBlock"))
		{
			this.bIsBongCntShow=false;
			mainBase.removeIndicatorConfig(strParam);
		}
		else if(strMethodName.equals("setBaselineData"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			base11.setBaselineData(strParam);

			BaseChart neoChart = base11._chart;
			if (neoChart._cvm.baseLineType.size() > 0)
				neoChart.repaintAll();
		}
        else if(strMethodName.equals("setRealUpdate"))
        {
            Base11 base11 = (Base11)mainBase.baseP;
            base11.setRealUpdate(strParam);
        }
		//2017.05. 15 해외선물옵션 FX차트 아이템 감추기 기능 setMethod로 연결 >>
		else if(strMethodName.equals("setFxChart"))
		{
			m_bFXChart = true;
		}
		//2017.05. 15 해외선물옵션 FX차트 아이템 감추기 기능 setMethod로 연결 <<
		//2017.06.07 해외선옵 FX 매수+매도 차트 >>
		else if(strMethodName.equals("addFXGraph"))
		{
			mainBase.addTrendConfig("팔때");
		}
		else if(strMethodName.equals("removeFXGraph"))
		{
			mainBase.removeTrendConfig("팔때");
		}
		//2017.06.07 해외선옵 FX 매수+매도 차트 <<
		//2017.09.13 by pjm 한국시간 추가 >>
		else if(strMethodName.equals("setKoreanTime"))
		{
			this.bIsKoreanTime = strParam.equals("1") ? true : false;
		}
		//2017.09.13 by pjm 한국시간 추가 <<
		else if (strMethodName.equals("isSimpleSet")) {
			if (strParam.equals("YES")) {
				this.isSimpleSet = true;
			}
			else {
				this.isSimpleSet = false;
			}
		}
		//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 >>
		else if(strMethodName.equals("setModYn"))
		{
			this.strModYn = strParam;
		}
		//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 <<
		//2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 >>
    	else if(strMethodName.equals("setBoundMark")) {
			String[] values = strParam.split(";");

			if (values.length > 2) {
				Base11 base11 = (Base11)mainBase.baseP;
				BaseChart neoChart = base11._chart;

				if (neoChart != null) {
					neoChart._cdm.codeItem.strHighest = values[0];
					neoChart._cdm.codeItem.strLowest = values[1];
					neoChart._cdm.codeItem.strGijun = values[2];
					neoChart._cdm.codeItem.strStandardPrice = values[2];

					neoChart.repaintAll();
				}
			}
		}
		//2019. 03. 14 by hyh - 일반설정 상하한가 바 표시 <<
		//2019. 03. 14 by hyh - 테크니컬차트 추가 >>
		else if(strMethodName.equals("technical")) {
			if(strParam.equals("YES")) {
				this.bIsTechnical = true;
			} else {
				this.bIsTechnical = false;
			}
		}
		//2019. 03. 14 by hyh - 테크니컬차트 추가 <<
		//2019. 04. 01 by hyh - 차트 타이틀 적용 >>
		else if(strMethodName.equals("charttitle")) {
			this.strChartTitle = strParam;
		}
		else if(strMethodName.equals("charttitlecolor")) {
			this.strChartTitleColor = strParam;
		}
		//2019. 04. 01 by hyh - 차트 타이틀 적용 <<

		//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
		else if (strMethodName.equals("setFxMarginType")) {
			try {
				Base11 base11 = (Base11) mainBase.baseP;
				BaseChart neoChart = base11._chart;

				if (neoChart != null) {
					neoChart._cvm.nFxMarginType = Integer.parseInt(strParam);
					this.nFxMarginType = Integer.parseInt(strParam);
				}
			} catch (Exception e) {
			}
		}
		//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

		//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
		else if (strMethodName.equals("setConn")) {
			Base11 base11 = (Base11) mainBase.baseP;
			BaseChart neoChart = base11._chart;

			if (strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.bIsConn = true;
			} else {
				neoChart._cvm.bIsConn = false;
			}
		}
		else if (strMethodName.equals("setDay")) {
			Base11 base11 = (Base11) mainBase.baseP;
			BaseChart neoChart = base11._chart;

			if (strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.bIsDay = true;
			} else {
				neoChart._cvm.bIsDay = false;
			}
		}
		else if (strMethodName.equals("setFloor")) {
			Base11 base11 = (Base11) mainBase.baseP;
			BaseChart neoChart = base11._chart;

			if (strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.bIsFloor = true;
			} else {
				neoChart._cvm.bIsFloor = false;
			}
		}
		//2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		else if(strMethodName.equals("oneTouchMode")){
			if(strParam.equals("YES")){
				isOneTouchSet = true;
			}else{
				isOneTouchSet = false;
			}
		}
		else if(strMethodName.equals("ShowOneTouchMode")){
			if(strParam.equals("YES")){
				this.bShowOneTouch = true;
			}else{
				this.bShowOneTouch = false;
			}
		}
		else if(strMethodName.equals("LoadFileName")) {
			COMUtil._mainFrame.strLocalFileName = strParam+"_local";
		}
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end
		else if(strMethodName.equals("ShowBtnOpenAllScreen")){
			COMUtil._mainFrame = this;

			if(strParam.equals("YES") || strParam.equals("1")){
				this.bShowBtnOpenAllScreen = true;
			}else{
				this.bShowBtnOpenAllScreen = false;
			}
		}
		else if(strMethodName.equals("ShowBtnOpenAlarmScreen")){
            COMUtil._mainFrame = this;

			if(strParam.equals("YES") || strParam.equals("1")){
				this.bShowBtnOpenAlarmScreen = true;
			}else{
				this.bShowBtnOpenAlarmScreen = false;
			}
		}
		//2019. 04. 16 by hyh - 시세알람차트 개발 >>
		else if (strMethodName.equals("setAlarmChart")) {
			if(strParam.equals("YES") || strParam.equals("1")) {
				try {
					Base11 base11 = (Base11) mainBase.baseP;

					//2019. 06. 14 by hyh - 시세알람차트 개선. 1분할로 변경 >>
					base11.setStorageDivision(11, false, false, null, false);
					//2019. 06. 14 by hyh - 시세알람차트 개선. 1분할로 변경 <<

					//2019. 06. 19 by hyh - 시세알람차트 원터치, 간편설정 제거 >>
					base11.removeOneTouch();
					base11.removeSimpleSet();
					//2019. 06. 19 by hyh - 시세알람차트 원터치, 간편설정 제거 <<

					BaseChart neoChart = base11._chart;

					if (neoChart != null) {
						neoChart._cvm.bIsAlarmChart = true;
						neoChart.showViewPanel();
					}
				} catch (Exception e) {
				}
			}
		}
		//2019. 04. 16 by hyh - 시세알람차트 개발 <<
		//2023.06.09 by SJW - 미사용 변수 주석 처리 >>
		//2019. 05. 30 by hyh - 매매연습차트 개발 >>
//		else if (strMethodName.equals("setTradeChart")) {
//			if(strParam.equals("YES") || strParam.equals("1")) {
//				COMUtil._mainFrame.bIsTradeChart = true;
//			}
//		}
		//2019. 05. 30 by hyh - 매매연습차트 개발 <<
		//2023.06.09 by SJW - 미사용 변수 주석 처리 <<
		//2019. 06. 21 by hyh - 주문차트 개발 >>
		else if (strMethodName.equals("setOrderChart")) {
			if(strParam.equals("YES") || strParam.equals("1")) {
				try {
					Base11 base11 = (Base11) mainBase.baseP;
					base11.setStorageDivision(11, false, false, null, false);
					base11.removeSimpleSet();

					BaseChart neoChart = base11._chart;

					if (neoChart != null) {
						neoChart._cvm.bIsOrderChart = true;
					}
				} catch (Exception e) {
				}
			}
		}
		//2019. 06. 21 by hyh - 주문차트 개발 <<
		//2019.04.29 비교차트-연결, FX 설정 -lyj
		else if (strMethodName.equals("setCompareSetting"))
		{
			String[] values = strParam.split(";");

			if (values.length > 1) {
				if(values[0].equals("1") || values[0].equals("YES"))
					bCompConnectFut = true;
				else
					bCompConnectFut = false;

				strCompFX = values[1];
			}
		}
		//2019.04.29 비교차트-연결, FX 설정 -lyj end
        //2019.04.30 by lyj 라인차트 xscale 삭제
		else if (strMethodName.equals("setHideXscale"))
		{
            if(strParam.equals("YES") || strParam.equals("1")) {
//                Base11 base11 = (Base11) mainBase.baseP;
//                BaseChart neoChart = base11._chart;
//                if (neoChart != null) {
//                    neoChart._cvm.bIsHideXYscale = true;
//                }
				bIsHideXScale = true;
            }
        }
        //2019.04.30 by lyj 라인차트 xscale 삭제 end
		else if (strMethodName.equals("setAlarmValue"))
		{
			Base11 base11 = (Base11) mainBase.baseP;
			BaseChart neoChart = base11._chart;
			if (neoChart != null) {
				neoChart.setAlarmValue(strParam);
			}
		}
		//2020.04.13 가로 Stack형 차트 수정 - hjw Start
		else if(strMethodName.equals("setHorizontalStackChart")) {
			if(strParam != null) {
				//value = @"name1;name2;name3=data1;data2;data3=infotype=alignType=colorType";
				//infoType(범례 타입, 0=안보임, 1=색나열형 범례, 2=클릭시 파이위에 범례, 3=하나씩 보여주는 범례)
				//alignType(위치) (top, bottom)
//				strParam = "개인;외국인;기관계;데이터1;데이터2=10;20;30;40;50=0=bottom=1";

				String[] sepDatas = strParam.split("=");

				if(strParam.equals("") || sepDatas.length < 2) {
					Hashtable<String, Object> dic = new Hashtable<String, Object>();
					List<String> lNames = new ArrayList<String>();
					List<String> lValues = new ArrayList<String>();

					lNames.add("데이터 입력 안됨");
					dic.put("sliceNames", lNames);
					dic.put("sliceDatas", lValues);

					Base base = mainBase.baseP;
					base.setPacketData_hashtable(dic);
					return;
				}

				String sName = sepDatas[0];
				String sValue = sepDatas[1];

				String[] names = sName.split(";");
				String[] values = sValue.split(";");

				Hashtable<String, Object> dic = new Hashtable<String, Object>();

				List<String> lNames = new ArrayList<String>();
				List<String> lValues = new ArrayList<String>();

				if((names.length < 1 || names[0].equals(""))
						&& (values.length < 1 || values[0].equals(""))) {
					dic.put("sliceNames", lNames);
					dic.put("sliceDatas", lValues);
				} else {
					for (int i = 0; i < names.length; i++)    //타이틀
						lNames.add(names[i]);
					for (int i = 0; i < values.length; i++)    //값
						lValues.add(values[i]);
					dic.put("sliceNames", lNames);
					dic.put("sliceDatas", lValues);
				}

				if(sepDatas.length>2) {
					dic.put("infoType",  sepDatas[2]);
				}

				if(sepDatas.length>3) {
					dic.put("alignType",  sepDatas[3]);
				}

				if(sepDatas.length>4) {
					dic.put("colorType",  sepDatas[4]);
				}

				//데이터를 퍼센트형으로 받을때 처리 (P:퍼센트형, V:Value형 데이터)
				if(sepDatas.length>5) {
					dic.put("valueType",  sepDatas[5]);
				}

				Base base = mainBase.baseP;
				base.setPacketData_hashtable(dic);
			}
		}
		//2020.04.13 가로 Stack형 차트 수정 - hjw End
		else if(strMethodName.equals("setOneQStockChart")) {
			if(strParam.equals("YES"))
				this.bIsOneQStockChart = true;
			else
				this.bIsOneQStockChart = false;
		}
		else if (strMethodName.equals("useOneQToolBar")) {
			if (strParam.equals("YES")) {
				this.bUseOneqToolBar = true;
			}
			else {
				this.bUseOneqToolBar = false;
			}
		}
		else if (strMethodName.equals("setMarketType")) {
			try {
				this.nMarketType = Integer.parseInt(strParam);
			} catch (Exception e) {

			}
		}
		//2021.04.26 by lyk - kakaopay - 매입평균선 추가 >>
		else if(strMethodName.equals("setAvgBuyPrice"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.isAvgBuyPriceFunc = true;
			} else {
				neoChart._cvm.isAvgBuyPriceFunc = false;
			}
		}
		else if(strMethodName.equals("setAvgBuyPriceData"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(!neoChart._cvm.isAvgBuyPriceFunc) {
				neoChart._cdm.strAvgBuyPrice = "";
			} else {
				neoChart._cdm.strAvgBuyPrice = strParam;
			}
			neoChart.repaintAll();
		}
		//2021.04.26 by lyk - kakaopay - 매입평균선 추가 <<

		//2021.04.26 by lyk - kakaopay - 매수,매도 가격 표시 >>
		else if(strMethodName.equals("setBuySellPrice"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.isBuySellPriceFunc = true;
			} else {
				neoChart._cvm.isBuySellPriceFunc = false;
				//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 >>
				neoChart.basic_block.setMarginT((int)COMUtil.getPixel(Block.BLOCK_TOP_MARGIN));
				neoChart.basic_block.setMarginB((int)COMUtil.getPixel(Block.BLOCK_BOTTOM_MARGIN));
				//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 <<

				neoChart.resetTradeData();
			}
			neoChart.reSetUI(true);
		}
		else if(strMethodName.equals("setBuySellPriceData"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(!neoChart._cvm.isBuySellPriceFunc) {
				neoChart.resetTradeData();
			} else {
				if(strParam.equals("")) {
					//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 >>
					neoChart.basic_block.setMarginT((int)COMUtil.getPixel(Block.BLOCK_TOP_MARGIN));
					neoChart.basic_block.setMarginB((int)COMUtil.getPixel(Block.BLOCK_BOTTOM_MARGIN));
					//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 <<
				}
				neoChart.setTradeData(strParam);
			}
			neoChart.reSetUI(true);
		}
		//2021.04.26 by lyk - kakaopay - 매수,매도 가격 표시 <<

		//2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건(특정일) 처리 >>
		else if(strMethodName.equals("setBuySellPriceSpecificDay"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(strParam.equals("")) {
				//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 >>
				neoChart.basic_block.setMarginT((int)COMUtil.getPixel(Block.BLOCK_TOP_MARGIN));
				neoChart.basic_block.setMarginB((int)COMUtil.getPixel(Block.BLOCK_BOTTOM_MARGIN));
				//2021.06.21 by lyk - 매매내역 뱃지 위치가 차트 영역을 벗어날 경우 블럭마진 값이 변경된 경우가 있는데 이곳에서 초기화를 해준다 <<
			}

			neoChart.setTradeDataSpecificDay(strParam);
			neoChart.reSetUI(true);
		}
		//2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건(특정일) 처리 <<

		//2021.04.26 by lyk - kakaopay - 기업 캘린더 표시 >>
		else if(strMethodName.equals("setCorporateCalendar"))
		{
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.isCorporateCalendarFunc = true;
			} else {
				neoChart._cvm.isCorporateCalendarFunc = false;
			}
		}
		else if (strMethodName.equals("setCorporateCalendarData")) {
			Base11 base11 = (Base11)mainBase.baseP;

			if(!base11._chart._cvm.isCorporateCalendarFunc) {
				this.mainBase.baseP._chart.resetCorporateCalendarData();
			} else {
				this.mainBase.baseP._chart.setCorporateCalendarData(strParam);
			}
			base11._chart.reSetUI(true);
		}
		else if (strMethodName.equals("resetCorporateCalendarData")) {
			Base11 base11 = (Base11)mainBase.baseP;
			this.mainBase.baseP._chart.resetCorporateCalendarData();
		}
		//2021.04.26 by lyk - kakaopay - 기업 캘린더 표시 <<
		else if (strMethodName.equals("setMovingAverageLineState")) {
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;

			if(strParam.equals("YES") || strParam.equals("1")) {
				neoChart._cvm.isMovingAverageLine = true;
			} else {
				neoChart._cvm.isMovingAverageLine = false;
			}
			neoChart.reSetUI(true);
			Log.d("kfits","Mainframe's setMethod() isMovingAverageLine: "+neoChart._cvm.isMovingAverageLine);
		}
		else if(strMethodName.equals("setIndicator")) {
			try {
				String[] values = strParam.split(";");
				CheckBox cb = new CheckBox(this.context);
				cb.setTag(values[0]);
				if(values[1].equals("1")) {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
				COMUtil.setJipyo(cb);
			} catch (Exception e) {

			}
		}
		else if(strMethodName.equals("openIndicatorConfigView")) {
			try {
				//지표 팝업창 연동 대상 항목
				/*
				[오버레이]
				이동평균선 : 40099
				일목균형표 : 40003
				볼린저밴드 : 40001
				매물대    : 11000
				엔벨로프   : 40002

				[보조지표]
				RSI : 30008
				MACD : 30002
				OBV : 30009
				Stochastic Fast : 30004
				Stochastic Slow : 30003
				MFI : 30022
				DMI : 30005
				*/
				if(strParam.equals("40099")) {
					this.showDetailView("주가이동평균", "주가이동평균");
				} else {
					String sName = COMUtil.getJipyoNameForTag(strParam);
					if(sName == null || sName.equals(""))
						return;

					this.showDetailView(sName, sName);
				}
			} catch (Exception e) {

			}
		}
		//2021.03.28 by lyk - kakaopay - 외부 차트타입, 화면설정 연동 기능 >>
		else if(strMethodName.equals("setSelectChart")) {
			try {
				int tag = Integer.parseInt(strParam);
				COMUtil.selectChartFromOutside(tag);
			} catch(Exception e) {

			}
		}
		//2021.03.28 by lyk - kakaopay - 외부 차트타입, 화면설정 연동 기능 <<

		//2021.04.01 by lyk - kakaopay - 지지선, 저항선 추가 기능 >>
		else if(strMethodName.equals("setSupportResistanceLine")) {
			String[] values = strParam.split(";");

			Base11 base11 = (Base11)mainBase.baseP;
			BaseChart neoChart = base11._chart;
			ChartViewModel _cvm = neoChart._cvm;

			if(_cvm.baseLineType!=null && _cvm.baseLineType.size()>0) {
				_cvm.baseLineType.clear();
			}

			_cvm.baseLineColors.clear();
			_cvm.baseLineThicks.clear();
			COMUtil.setEnvData(_cvm);

			int isAdd = Integer.parseInt(values[1]);
			if(isAdd == 1) {
				_cvm.isSupportResistanceLine = true; //외부 화면 설정값
				_cvm.baseLineType.add(13);
				_cvm.baseLineType.add(14);
				_cvm.baseLineType.add(15);
				_cvm.baseLineType.add(16);
				_cvm.baseLineType.add(17);
			} else {
				_cvm.isSupportResistanceLine = false; //외부 화면 설정값
				_cvm.baseLineType.remove((Integer)13);
				_cvm.baseLineType.remove((Integer)14);
				_cvm.baseLineType.remove((Integer)15);
				_cvm.baseLineType.remove((Integer)16);
				_cvm.baseLineType.remove((Integer)17);
			}

			neoChart.repaintAll();

		}
		//2021.04.01 by lyk - kakaopay - 지지선, 저항선 추가 기능 <<

		//2021.04.02 by lyk - kakaopay - 추세선 기능 >>
		else if(strMethodName.equals("setAutoTrendWaveType")) {
			String[] values = strParam.split(";");

			Base11 base11 = (Base11)mainBase.baseP;
			BaseChart neoChart = base11._chart;
			ChartViewModel _cvm = neoChart._cvm;

			int isAdd = Integer.parseInt(values[1]);
			if(isAdd == 1) {
				_cvm.isAutoTrendWaveLine = true; //외부 화면 설정값
				_cvm.autoTrendWaveType = 1;
			} else {
				_cvm.isAutoTrendWaveLine = false; //외부 화면 설정값
				_cvm.autoTrendWaveType = 0;
			}

			neoChart.repaintAll();
		}
		//2021.04.02 by lyk - kakaopay - 추세선 기능 <<

		//2021.04.07 by lyk - kakaopay - 차트 초기화 기능 >>
		else if(strMethodName.equals("setInitChart")) {
			Base11 base11 = (Base11)mainBase.baseP;
			base11.initChart("pressInitBtn");
		}
		//2021.04.07 by lyk - kakaopay - 차트 초기화 기능 <<

		//2021.05.21 by HJW - 프리애프터 적용 >>
		else if(strMethodName.equals("setPreAfterAreaVisible")) {
//			strParam = "1;093000;160000";
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;
			String[] values = strParam.split(";");

			if(values.length > 2) {
				try {
					neoChart._cvm.bIsPreAfterAreaVisible = values[0].equals("1");
					neoChart._cvm.strRegularOpenTime = values[1];
					neoChart._cvm.strRegularCloseTime = values[2];
//					neoChart._cvm.strRegularOpenTime = "093000";
//					neoChart._cvm.strRegularCloseTime = "160000";
				} catch (Exception e) {

				}
			} else if(values.length==1) {
				neoChart._cvm.bIsPreAfterAreaVisible = values[0].equals("1");
			}
		}
		else if(strMethodName.equals("setPreAfterValue")) {
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;
			neoChart._cvm.bIsShowPreAfter = strParam.equals("0");
		}
		//2021.05.21 by HJW - 프리애프터 적용 <<

		//2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 >>
		else if(strMethodName.equals("setTimeZoneText")) {
			Base11 base11 = (Base11)mainBase.baseP;
			NeoChart2 neoChart = base11._chart;
			neoChart._cvm.strTimeZoneText = strParam;
		}
		//2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 <<
	}

	public ArrayList<Hashtable<String, String>> getBunPeriodList()
	{
		ArrayList<Hashtable<String, String>> result = new ArrayList<Hashtable<String, String>>();
		String checked;

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		for(int i = 0; i < 7; i++)
		{
			checked = String.valueOf(base11.astrMinCheckData[i]);

			if(checked.equals("1"))
			{
				Hashtable<String, String> item = new Hashtable<String, String>();
				item.put("title", String.valueOf(base11.astrMinData[i])+"분");
				result.add(item);
			}
		}

		return result;
	}

	public ArrayList<Hashtable<String, String>> getTicPeriodList()
	{
		ArrayList<Hashtable<String, String>> result = new ArrayList<Hashtable<String, String>>();
		String checked;

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		for(int i = 0; i < 7; i++)
		{
			checked = String.valueOf(base11.astrTikCheckData[i]);

			if(checked.equals("1"))
			{
				Hashtable<String, String> item = new Hashtable<String, String>();
				item.put("title", String.valueOf(base11.astrTikData[i])+"틱");
				result.add(item);
			}
		}

		return result;
	}

	//지표 상세 설정창 처리 >>
	public void showDetailView(String sTag, String sName) {
		//2023.11.16 by LYH - 상세설정 버튼 여러번 눌려도 한번만 뜨도록 수정 >>
		if(COMUtil._neoChart.jipyoListDetailPopup != null && COMUtil._neoChart.jipyoListDetailPopup.isShowing())
			return;
		//2023.11.16 by LYH - 상세설정 버튼 여러번 눌려도 한번만 뜨도록 수정 <<

		RelativeLayout simpleLayout = new RelativeLayout(COMUtil._mainFrame.getContext());

		DetailJipyoController view = new DetailJipyoController(context,simpleLayout);
//		view.setParent(this.context);

		//최상위 뷰
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

		//2013. 6. 10 (미래에셋) 태블릿 차트화면 설정버튼 눌러서 상세설정 팝업창 띄울 죽던 현상 수정 >>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			simpleLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			//전체길이 - (왼쪽마진 + 오른쪽마진) = Width
			COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(simpleLayout, (int)COMUtil.getPixel(326), (int)COMUtil.getPixel(544), true);
			COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
			COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
			COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.NO_GRAVITY, (int)COMUtil.getPixel(924), (int)COMUtil.getPixel(91));
		}
		else
		{
			try {
				COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true); //2023.12.22 by CYJ - 소프트키보드로 인한 레이아웃 리사이즈
				COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
				COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
				COMUtil._neoChart.jipyoListDetailPopup.setAnimationStyle(android.R.style.Animation_Translucent); //2023.11.21 by CYJ - kakaopay 상세설정창 인터렉션 추가
//				COMUtil._neoChart.jipyoListDetailPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //2023.12.22 by CYJ - 소프트키보드로 인한 레이아웃 리사이즈 //2023.12.28 by CYJ - 앱바 스크롤 - 코드에서 정의하는걸로 변경
				COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.FILL_VERTICAL, 0, 0); //2023.12.28 by CYJ - 앱바 스크롤
			} catch(Exception e) {
				if(COMUtil._neoChart.jipyoListDetailPopup != null && COMUtil._neoChart.jipyoListDetailPopup.isShowing()) {
					COMUtil._neoChart.jipyoListDetailPopup.dismiss();
					COMUtil._neoChart.jipyoListDetailPopup = null;

					Handler mHandler = new Handler();
					mHandler.postDelayed(new Runnable()  {
						public void run() {
							COMUtil._neoChart.jipyoListDetailPopup = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true); //2023.12.22 by CYJ - 소프트키보드로 인한 레이아웃 리사이즈
							COMUtil._neoChart.jipyoListDetailPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
							COMUtil._neoChart.jipyoListDetailPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
							COMUtil._neoChart.jipyoListDetailPopup.setAnimationStyle(android.R.style.Animation_Translucent); //2023.11.21 by CYJ - kakaopay 상세설정창 인터렉션 추가
							COMUtil._neoChart.jipyoListDetailPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //2023.12.22 by CYJ - 소프트키보드로 인한 레이아웃 리사이즈
							COMUtil._neoChart.jipyoListDetailPopup.showAtLocation(COMUtil.apiView.getRootView(), Gravity.NO_GRAVITY, 0, 0);
						}
					}, 200); // 0.2초후
				}
			}
		}
		//2013. 6. 10 (미래에셋) 태블릿 차트화면 설정버튼 눌러서 상세설정 팝업창 띄울 죽던 현상 수정 <<

//    	String tag = (String)v.getTag();
		view.isPopupState = true;
		view.setTitle(sTag);
		view.setUI();
		view.setInitGraph(sName);
	}
	//지표 상세 설정창 처리 <<

	//2013. 1. 25 분틱콤보 리스트 여는 함수 추가 
	public PopupWindow comboPopup;
	LinearLayout comboLayout;
	RelativeLayout.LayoutParams firstViewParams;

	//2013. 2.1 분틱콤보 가로에서 분/틱 선택햇을때 옆에 두번째 리스트 뷰 뜨게 설정 
	public PopupWindow comboSecondPopup;
	LinearLayout comboSecondLayout;
	RelativeLayout.LayoutParams secnondViewParams = new RelativeLayout.LayoutParams(0, 0);

	public void setComboBox(ListView list, RelativeLayout.LayoutParams params, boolean bIsFirstListView) {
		//2013. 6. 7 태블릿 콤보박스 y축위치 조절 >>
//		int nTopSpace = (int)COMUtil.getPixel(3);
//		int nTopSpace;
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			nTopSpace = 0;
//		}
//		else
//		{
//			nTopSpace = (int)COMUtil.getPixel(3);
//		}
		//2013. 6. 7 태블릿 콤보박스 y축위치 조절 <<

		//2013. 1. 7  팝업창 컨트롤위치좌표 얻어오는 루틴 개선 - 함수로 처리 및 저해상도에서 팝업창위치가 달라지던 문제 해결 
//		Point ptArea = area;


		// 콤보의 드랍다운뷰의 상단 좌표 (콤보박스부모linear의상단y좌표 + 콤보버튼높이)
//		int nDropdownViewTop = parentLinearTop + v.getHeight() + nTopSpace;
//		int nDropdownViewTop = params.topMargin + params.height + nTopSpace;
		// 드랍다운뷰의 너비를 버튼위치 기반으로 맞추기
		int nDropdownViewWidth = params.width;
		// 콤보의 드랍다운뷰의 왼쪽위 좌표
//		int nDropdownViewLeft = parentLinearLeft - nDropdownViewWidth + (v.getRight() - v.getLeft());
//		int nDropdownViewLeft = params.leftMargin - nDropdownViewWidth + params.width;

//		String strComboName = (String)list.getTag();
//		int nCount = 0;
//		
//		if (strComboName.equals("periodDWM")) {
//			nCount = 3;
//		} else if (strComboName.equals("period_minute")) {
//			nCount = 7;
//		} else if (strComboName.equals("period_tic")) {
//			nCount = 7;
//		}
//		else if (strComboName.equals("period_all")) {
//			nCount = 5;
//		}

		//int nDropdownViewHeight = nCount * (int)COMUtil.getPixel(30)+(int)COMUtil.getPixel(9);

		if(bIsFirstListView)
		{

			firstViewParams = params;

			// 버튼 이외의 영역 터치했을경우 정상적으로 닫히게 처리
			if (comboPopup != null && !comboPopup.isShowing()) {
				comboPopup = null;
				comboLayout.removeAllViews();
			}

			// 콤보박스가 열려있는상태 (not null) 면 닫고 아니면 생성해서 오픈
			if (comboPopup != null) {
				comboPopup.dismiss();
				comboPopup = null;
				comboLayout.removeAllViews();
			} else {
				comboLayout = new LinearLayout(context);
//				comboLayout.setBackgroundColor(Color.BLACK);
				//comboLayout.setPadding(0, (int)COMUtil.getPixel(2), 0, 0);
//				int layoutResId = context.getResources().getIdentifier("popupbg06",
//						"drawable", context.getPackageName());
//				comboLayout.setBackgroundResource(layoutResId);
				comboLayout.addView(list);
				// comboPopup = new PopupWindow(comboCandleLayout,
				// (int)COMUtil.getPixel(nPopupWidth), LayoutParams.WRAP_CONTENT,
				// true);
				comboPopup = new PopupWindow(comboLayout,
						nDropdownViewWidth,
						LayoutParams.WRAP_CONTENT, true);
				comboPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때
				// 다른부분에 이벤트를 줄수있습니다.
				comboPopup.setBackgroundDrawable(new BitmapDrawable()); // 이부분에
				// 이벤트가
				// 들어오게됩니다.

				// 팝업 띄우기
				// comboPopup.showAtLocation(comboCandleLayout,
				// Gravity.TOP|Gravity.LEFT,
				// (int)COMUtil.getPixel(nPopupLeftMargin),
				// (int)COMUtil.getPixel(nPopupTopMargin));
//				comboPopup.showAtLocation(comboLayout,
//						Gravity.NO_GRAVITY, nDropdownViewLeft,
//											nDropdownViewTop);
				comboPopup.showAtLocation(COMUtil.apiView,
						Gravity.LEFT|Gravity.TOP, params.leftMargin, params.topMargin);
				//saveloadPopup.showAtLocation(apiView, Gravity.LEFT|Gravity.TOP, params.leftMargin, params.topMargin);
			}
		}
		else
		{
			// 버튼 이외의 영역 터치했을경우 정상적으로 닫히게 처리
			if (comboSecondPopup != null && !comboSecondPopup.isShowing()) {
				comboSecondPopup = null;
				comboSecondLayout.removeAllViews();
			}

			// 콤보박스가 열려있는상태 (not null) 면 닫고 아니면 생성해서 오픈
			if (comboSecondPopup != null) {
				comboSecondPopup.dismiss();
				comboSecondPopup = null;
				comboSecondLayout.removeAllViews();
			} else {
				comboSecondLayout = new LinearLayout(context);
//				comboSecondLayout.setBackgroundColor(Color.BLACK);
				//comboSecondLayout.setPadding(2, 2, 2, 2);

//				int layoutResId = context.getResources().getIdentifier("popupbg06",
//						"drawable", context.getPackageName());
//				comboLayout.setBackgroundResource(layoutResId);

				comboSecondLayout.addView(list);
				// comboPopup = new PopupWindow(comboCandleLayout,
				// (int)COMUtil.getPixel(nPopupWidth), LayoutParams.WRAP_CONTENT,
				// true);
				comboSecondPopup = new PopupWindow(comboSecondLayout,
						nDropdownViewWidth,
						LayoutParams.WRAP_CONTENT, true);
				comboSecondPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때
				// 다른부분에 이벤트를 줄수있습니다.
				comboSecondPopup.setBackgroundDrawable(new BitmapDrawable()); // 이부분에
				// 이벤트가
				// 들어오게됩니다.

				// 팝업 띄우기
				// comboPopup.showAtLocation(comboCandleLayout,
				// Gravity.TOP|Gravity.LEFT,
				// (int)COMUtil.getPixel(nPopupLeftMargin),
				// (int)COMUtil.getPixel(nPopupTopMargin));
//				comboSecondPopup.showAtLocation(comboSecondLayout,
//						Gravity.NO_GRAVITY, nDropdownViewLeft,
//											nDropdownViewTop);
				comboSecondPopup.showAtLocation(COMUtil.apiView,
						Gravity.LEFT|Gravity.TOP, params.leftMargin, params.topMargin);
			}
		}


	}

	public ListView setComboBoxList(String strComboName) {
		// 콤보박스 아래에 펼처지는 리스트뷰
		ListView comboList = new ListView(context);

		ArrayList<String> arStrMin;
		ArrayList<String> arStrTik;
		ArrayList<String> arStrPeriod;

		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

		// 읽어온 배열값을 arraylist 에 저장.
		ArrayList<String> arStrComboData = new ArrayList<String>();

		if (strComboName.equals("periodDWM")) {
			arStrPeriod = new ArrayList<String>();
			arStrPeriod.add("일");
			arStrPeriod.add("주");
			arStrPeriod.add("월");
			arStrComboData = arStrPeriod;
		} else if (strComboName.equals("period_minute")) {
			arStrMin = new ArrayList<String>();

			int nCount = base11.astrMinDefaultData.length;
			for (int i = 0; i < nCount; i++) {

				if(base11.astrMinCheckData[i]==1)
					arStrMin.add(String.valueOf(base11.astrMinData[i]));
			}

			arStrComboData = arStrMin;
		} else if (strComboName.equals("period_tic")) {
			arStrTik = new ArrayList<String>();
			int nCount = base11.astrTikDefaultData.length;
			for (int i = 0; i < nCount; i++) {
				if(base11.astrTikCheckData[i]==1)
					arStrTik.add(String.valueOf(base11.astrTikData[i]));
			}

			arStrComboData = arStrTik;
		}
		else if (strComboName.equals("period_all")) {
			arStrPeriod = new ArrayList<String>();
			arStrPeriod.add("일");
			arStrPeriod.add("주");
			arStrPeriod.add("월");
			arStrPeriod.add("분▼");
			arStrPeriod.add("틱▼");
			arStrComboData = arStrPeriod;
		}

		// 리스트뷰에 값을 세팅한다.
		comboList.setAdapter(new DropListAdapter(arStrComboData));

		// 리스트뷰 속성 설정
		comboList.setVerticalScrollBarEnabled(false);
		comboList.setCacheColorHint(Color.WHITE);
		comboList.setScrollingCacheEnabled(false);
		comboList.setVerticalFadingEdgeEnabled(false);
		//int layoutResId = context.getResources().getIdentifier("popupbg06",
		//		"drawable", context.getPackageName());
		int layoutResId = context.getResources().getIdentifier("pop_back", "drawable", this.context.getPackageName());
		comboList.setBackgroundResource(layoutResId);
//		comboList.setBackgroundColor(Color.WHITE);
		comboList.setTag(strComboName);
		comboList.setDivider(new ColorDrawable(Color.rgb(209, 209, 209)));
		comboList.setDividerHeight((int)COMUtil.getPixel(1));
		//comboList.setPadding(0, (int)COMUtil.getPixel(2), 0, 0);

		return comboList;
	}

	//2012. 9. 11 콤보박스용 어댑터
	private class DropListAdapter extends BaseAdapter{
		private ArrayList<String> m_arrData;
//		Button _btnCombo;

		public DropListAdapter(ArrayList<String> arrayList)
		{
			super();
			m_arrData = arrayList;
//			_btnCombo = btnCombo;
		}

		@Override
		public int getCount() {
			return m_arrData==null?0:m_arrData.size();
		}

		@Override
		public Object getItem(int position) {
			return m_arrData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			//콤보박스 리스트뷰의 한 행의 속성은 여기서 정해짐. 
//			final int pos = position;
			TextView tv = (TextView)v;
			if( tv == null ) {
				tv = new TextView(context);
//				tv.setTag(FIRST_LIST_TAG + pos);
				tv.setTextColor(Color.rgb(102, 102, 102));
				tv.setBackgroundColor(Color.TRANSPARENT);
				tv.setSingleLine();
				tv.setTextSize(15);
				tv.setWidth((int)COMUtil.getPixel(127));
				//2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int)COMUtil.getPixel(29));
				//2012. 9. 12  콤보박스의 글씨 왼쪽 여백 추가 
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					tv.setPadding((int)COMUtil.getPixel(8), 0, 0, 0);
				}
				else
				{
					tv.setPadding((int)COMUtil.getPixel(10), 0, 0, 0);
				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if(m_arrData!=null) tv.setText(m_arrData.get(position));

			//값을 선택하면 리스트뷰를 닫음 
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					_btnCombo.setText(String.valueOf(m_arrData.get(pos)));
					TextView tv = (TextView)v;
					String strTouchedItemName =  tv.getText().toString();

					ListView listParentofTextView = (ListView)v.getParent();
					String strListPeriod = null;
					if(listParentofTextView != null)
					{
						strListPeriod = (String)listParentofTextView.getTag();
					}

					//일주월분틱 (periodall) 이면 닫히지 않게.. 
//					System.out.println("strTouchedItemName:"+strTouchedItemName);
					int nIndex = strTouchedItemName.indexOf("분▼");
					int nIndex2 = strTouchedItemName.indexOf("틱▼");
					if(nIndex==-1 && nIndex2==-1)
					{
						comboPopup.dismiss();
						comboPopup = null;
						comboLayout.removeAllViews();

						//periodall 에서 분/틱  두번째 콤보리스트가 열려있을경우 닫아준다 . 
						if(comboSecondPopup != null)
						{
							comboSecondPopup.dismiss();
							comboSecondPopup = null;
							comboSecondLayout.removeAllViews();
						}
					}

//					reSetJipyo();
					if(COMUtil._neoChart!=null)
						mainBase.baseP._chart.repaintAll();

					clickButton(v, strListPeriod);
				}
			});
			return tv;
		}
	}

	private void clickButton(View v, String strListPeriod)
	{
		TextView tv = (TextView)v;

		String strTouchedItemName =  tv.getText().toString();

		Hashtable<String, Object> dic = new Hashtable<String, Object>();

//		dic.put("value",  strTouchedItemName);
//		if(strListPeriod != null)
//		{
//			dic.put("period",  strListPeriod);
//		}

		//2014. 3. 11 일주월, 분, 틱 콤보리스트뷰 이벤트 데이터 개선>>
		String strPeriod = "2";
		String strUnit = "1";

		if(strListPeriod.equals("periodDWM"))
		{
			if(strTouchedItemName.equals("일"))
			{
				strPeriod = "2";
			}
			else if(strTouchedItemName.equals("주"))
			{
				strPeriod = "3";
			}
			else if(strTouchedItemName.equals("월"))
			{
				strPeriod = "4";
			}
		}
		else if(strListPeriod.equals("period_minute"))
		{
			strPeriod = "1";
			strUnit = strTouchedItemName;
		}
		else if(strListPeriod.equals("period_tic"))
		{
			strPeriod = "0";
			strUnit = strTouchedItemName;
		}

		dic.put("period",  strPeriod);
		dic.put("unit",  strUnit);
		//2014. 3. 11 일주월, 분, 틱 콤보리스트뷰 이벤트 데이터 개선<<


		int nIndex = strTouchedItemName.indexOf("분▼");
		int nIndex2 = strTouchedItemName.indexOf("틱▼");
		if(nIndex!=-1)
		{
			ListView list = new ListView(context);
			list = setComboBoxList("period_minute");
			list.setTag(strMin);
			secnondViewParams.leftMargin = firstViewParams.leftMargin+firstViewParams.width;
			secnondViewParams.topMargin =  firstViewParams.topMargin+(v.getHeight()*3);
			secnondViewParams.width = firstViewParams.width;
			secnondViewParams.height = firstViewParams.height;
			setComboBox(list, secnondViewParams, false);
		}
		else if(nIndex2!=-1)
		{
			ListView list = new ListView(context);
			list = setComboBoxList("period_tic");
			list.setTag(strTic);
			secnondViewParams.leftMargin = firstViewParams.leftMargin+v.getWidth();
			secnondViewParams.topMargin =  firstViewParams.topMargin+(v.getHeight()*4);
			secnondViewParams.width = firstViewParams.width;
			secnondViewParams.height = firstViewParams.height;
			setComboBox(list, secnondViewParams, false);
		}
		else
		{
			if(userProtocol != null)
			{
				userProtocol.requestInfo(COMUtil._TAG_SELECT_PERIOD, dic);
			}
		}
	}

	public boolean isStorage(String dbname) {
		if(dbname==null || dbname.equals(""))
			return false;

		boolean rtnVal = true;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		ChartLastsaveDBHelper mHelper = new ChartLastsaveDBHelper(this.context, dbname);

		try {
			db = mHelper.getReadableDatabase();
			if(db==null) {
				mHelper.close();
				db.close();
				return false;
			}
			cursor = db.rawQuery("SELECT * FROM "+dbname, null); //마지막 상태 저장소.(1row)
		} catch(Exception e) {
			mHelper.close();
			db.close();
			return false;
		}

		//해당 DB에 데이터가 없다면 클라우드 데이터를 적용한다.
		if(!cursor.moveToPosition(0)) {
			mHelper.close();
			db.close();
			return false;
		}

		if(db!=null) {
			mHelper.close();
			db.close();
		}

		return rtnVal;
	}

	public int getnChangeFlag() {
		int rtnVal = 0;
		try {
			rtnVal = this.mainBase.baseP._chart.nChangeFlag;
		} catch(Exception e) {

		}
		return rtnVal;
	}

	public void chartDataClear() {
		if(mainBase!=null) {
			mainBase.baseP.chartDataClear();
		}
	}

	public boolean getIsSyncJongmok()
	{
		Base11 base11 = (Base11)mainBase.baseP;

		return base11.m_bSyncJongMok;
	}

	public boolean getIsSyncJugi()
	{
		Base11 base11 = (Base11)mainBase.baseP;

		return base11.m_bSyncJugi;
	}

	/* 태블릿 분할기능 처리 */
	public void setDivisionCustom(int divideType, int divideCount) {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setDivisionCustom(divideType, divideCount);
	}

	//2013. 5. 28 차트영역 외부 White skin 일 때   흰색 말고 다른색으로 처리할 때 사용 플래그 >>
	public void setAnotherColorWhiteSkin(boolean bFlag)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		if(bFlag)
		{
			base11.setIsAnotherColorWhiteSkin(true);
		}
		else
		{
			base11.setIsAnotherColorWhiteSkin(false);
		}
	}
	//2013. 5. 28 차트영역 외부 White skin 일 때   흰색 말고 다른색으로 처리할 때 사용 플래그 <<

	//2013. 6. 3 지표설정창 레이아웃parameter  값 가져오기 >>
	public RelativeLayout.LayoutParams getIndicatorParams()
	{
		return indicatorParams;
	}
	//2013. 6. 3 지표설정창 레이아웃parameter  값 가져오기 <<

	//2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 >>
	public void setChartItemViewHidden(boolean bFlag)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		if(base11 != null)
		{
			for(int i = 0; i < base11.chartList.size(); i++)
			{
				base11.chartList.get(i).setChartItemViewHidden(bFlag);
			}
		}
	}
	//2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 <<

	//2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
	public void changeTitle(String strNames)
	{
		mainBase.baseP._chart.changeTitle(strNames);
	}
	//2013.01.04 by LYH <<

	// 2012.12.11 by metalpooh >> 한화증권 툴바 관련된 추가사항
	public void setAnalTool() {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.addToolBarHanWha();
	}

	// 2012.12.11 by metalpooh >> 한화증권 전체보기 모드 추가
	public void setFullChart() {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.totalViewMode();
	}

	// 2012.12.11 by metalpooh >> 한화증권 전체보기 모드 추가 분당일모드 추가
	public void setOnePage(int nOnePage)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		base11.setOnePage(nOnePage);
	}

	public void setDivisionChart(int nDivCount) {
		Base11 base11 = (Base11)mainBase.baseP;
		base11.nDivideType = 11;
		base11.setDivisionChart(nDivCount);
	}

	public String getCodeInfo(int nIndex)
	{
		Base11 base11 = (Base11)mainBase.baseP;
		return base11.getCodeInfo(nIndex);
	}

	//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>> : 분할팝업창 닫는 함수. 모듈화 및 닫는 플래그 추가. 
	private void removeDividePopup(boolean bShow)
	{
		if(bShow && dividePopup!=null)
		{
			COMUtil.recycleDrawable(dividePopup.getBackground());
			dividePopup.setBackgroundDrawable(null);
			dividePopup.dismiss();
			dividePopup = null;
		}
		if(bShow && dividePopupBack!=null)
		{
			COMUtil.recycleDrawable(dividePopupBack.getBackground());
			dividePopupBack.setBackgroundDrawable(null);
			dividePopupBack.dismiss();
			dividePopupBack = null;
		}
	}
	//2013. 9. 17 원분할일때 분할설정창의 동기화 동작하지 않게 하기>>

	//2013.09.17 by LYH >> 패턴 그리기 추가.
	public String getPatternInput(boolean bSave) {
		Base13 base = (Base13)mainBase.baseP;
		return base.getDrawCoordinate(bSave);
	}
	//2013.09.17 by LYH <<

	//2015. 11. 2 미니 설정창 추가>>
	@Override
	public void onMessage(String strMessage) {
		if(strMessage.equals(COMUtil._POPUP_EVENT_CLOSE))
		{
			closeApiPopup();
		}
		else if(strMessage.equals(COMUtil._POPUP_EVENT_MINI_OPENINDICATOR))
		{
			Base11 base = (Base11)mainBase.baseP;
			base.openIndicatorPopup();
		}
		else if(strMessage.equals("비교") || strMessage.equals("그리기종목"))
		{
			Hashtable<String, Object> dic = new Hashtable<String, Object>();
			dic.put("screen", strMessage);
			this.userProtocol.requestInfo(COMUtil._TAG_OPEN_SCREEN, dic);
		}
	}
	//2015. 11. 2 미니 설정창 추가<<

	public void addRemovePopupView(String strAddRemove, View view)
	{
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		dic.put("view", view);
		dic.put("addremove", strAddRemove);
		this.userProtocol.requestInfo(COMUtil._TAG_INDICATOR_CONFIG, dic);
	}

	public void setAccrueData(String arrData) {
		if(COMUtil._mainFrame != this)
		{
			COMUtil._mainFrame = this;
			COMUtil._neoChart = this.mainBase.baseP._chart;
		}

		Base11 base11 = (Base11)mainBase.baseP;
		//base11.hideAnalTool();
		base11.setAccrueData(arrData);
	}
	
	/* 투자자별 실시간 처리 2013.03.14 by lyk */
    public void setInvestorRealData(String[] titles, String[] marketData, boolean isReal) {
    	if(mainBase!=null) {
    		mainBase.baseP._chart.setInvestorRealData(titles, marketData, isReal);
    	}
    }

    public void reloadChart()
    {
        if(COMUtil.apiMode && indicatorLayout!=null) {
            indicatorConfigView.closePopupViews();	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>

            indicatorLayout.removeAllViews();
            COMUtil.apiLayout.removeView(indicatorLayout);
            indicatorLayout = null;
        }
        Base11 base11 = (Base11)mainBase.baseP;
        base11.setFuncAllChart("setPaddingRight");
        base11.resetUIAllChart();

        if(base11.chartList != null)
        {
            for(int i=0; i<base11.chartList.size(); i++) {
                BaseChart baseChart = base11.chartList.get(i);
                baseChart.setSkinType(g_nSkinType);
                baseChart.repaintAll();
            }
        }
    }

	//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 >>
	public void modSkipRequestInfo() {
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		dic.put("modYn", strModYn);
		dic.put("skipTp", strSkipTp);
		dic.put("skipVol", strSkipVol);
		dic.put("skipTick", strSkipTick);

		if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_SET_MOD_SKIP, dic);
	}

	public String getModYn() {
		String strReturn = "N";

		if (COMUtil._mainFrame != null) {
			strReturn = COMUtil._mainFrame.strModYn;
		}

		return strReturn;
	}
	//2019. 03. 07 by hyh - 만기보정, 제외기준 적용 <<

	//2019. 03. 14 by hyh - 테크니컬차트 개발 >>
	public String getTechnicalValues() {
		String strReturn = "";

		if (COMUtil._neoChart != null) {
			strReturn = COMUtil._neoChart.getTechnicalValues();
		}

		return strReturn;
	}
	//2019. 03. 14 by hyh - 테크니컬차트 개발 <<

	//2019.04.15 원터치 차트설정불러오기 추가 - lyj
	public void popupLoadChart(){
		View v = null;
		COMUtil.loadChart(v);
	}
	//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

	//2019. 04. 22 by hyh - 가상매매차트 개발. 지표리스트 >>
	public String getJipyoList() {
		String strReturn = "";

		if (COMUtil._neoChart != null) {
			strReturn = COMUtil._neoChart.getJipyoList();
		}

		return strReturn;
	}
	//2019. 04. 22 by hyh - 가상매매차트 개발. 지표리스트 <<

	//2021.12.15 by lkk - kakaopay - 보지표리스트 목록(이름) 반환 >>
	public Vector<String> getSubGraphList() {
		Vector<String> rtnObj = null;
		NeoChart2 neoChart = mainBase.baseP._chart;
		if(neoChart != null) {
			rtnObj = neoChart.getSubGraphList();
		}

		return rtnObj;
	}
	//2021.12.15 by lkk - kakaopay - 보지표리스트 목록(이름) 반환 <<

	//2021.04.12 by lkk - kakaopay - 차트 외부에서 지표리스트 참조하기 위해 추가 (지표명 리스트) >>
	public Vector<String> getGraphList() {
    	Vector<String> rtnObj = null;
		NeoChart2 neoChart = mainBase.baseP._chart;
		if(neoChart != null) {
			rtnObj = neoChart.getGraphList();
		}

		return rtnObj;
	}
	//2021.04.12 by lkk - kakaopay - 차트 외부에서 지표리스트 참조하기 위해 추가 (지표명 리스트) <<

	//2021.04.12 by lkk - kakaopay - 차트 외부에서 지표리스트 참조하기 위해 추가 (지표 태그 리스트) >>
	public Vector<String> getGraphTagList(){
		Vector<String> rtnObj = null;
		NeoChart2 neoChart = mainBase.baseP._chart;
		if(neoChart != null) {
			rtnObj = neoChart.getGraphTagList();
		}

		return rtnObj;
	}
	//2021.04.12 by lkk - kakaopay - 차트 외부에서 지표리스트 참조하기 위해 추가 (지표 태그 리스트) <<

	public void setShowToolBarOneq(boolean bShow) {
    	try {
			Base11 base11 = (Base11) mainBase.baseP;
			bIsShowToolBarOneq = bShow;
			base11.setShowToolBarOneq(bIsShowToolBarOneq);
		} catch (Exception e) {

		}
	}

	public String getData(String title, int nIndex) {
		NeoChart2 neoChart = mainBase.baseP._chart;
		return neoChart._cdm.getData(title, nIndex);
	}
	//2023.03.15 by SJW - 애프터마켓 추가 >>
	//2023.03.28 by SJW -  애프터마켓(연속데이터 처리) >>
//	public void setSignalData(String title, double[] marketData) {
	public void setSignalData(String title, double[] marketData, Boolean isConti) {
	//2023.03.28 by SJW -  애프터마켓(연속데이터 처리) <<
		if(mainBase!=null) {
			NeoChart2 neoChart = mainBase.baseP._chart;
			//2023.03.28 by SJW -  애프터마켓(연속데이터 처리) >>
			if (isConti) {
				double[] existData = neoChart._cdm.getSubPacketData(title);

				if (existData != null && existData.length > 0) {
					double[] mergedData = new double[marketData.length + existData.length];
					System.arraycopy(marketData, 0, mergedData, 0, marketData.length);
					System.arraycopy(existData, 0, mergedData, marketData.length, existData.length);
					neoChart._cdm.setSubPacketData(title, mergedData);
					return;
				}
			}
			//2023.03.28 by SJW -  애프터마켓(연속데이터 처리) <<
			neoChart._cdm.setSubPacketData(title, marketData);
		}
	}
	//2023.03.15 by SJW - 애프터마켓 추가 <<
}

