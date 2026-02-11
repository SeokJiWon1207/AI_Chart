package drfn.chart.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import drfn.chart.NeoChart2;
import drfn.chart.base.DrawAnimationLineTimer;
import drfn.chart.event.ViewChangable;
import drfn.chart.event.ViewChangedListener;
import drfn.chart.event.ViewEvent;
import drfn.chart.scale.AREA;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

import static android.os.Build.VERSION_CODES.R;

/**
 * 차트 전체 뷰에 대한 속성을 정의하는 클래스
 */
public class ChartViewModel implements ViewChangable{
    RectF bounds;    //차트의 폭과 시작점을 저장
    private int index;   //차트의 인덱스 

    //  public final int VIEW_NUM_ORG=100; //초기화면에 보여줄 데이터수
    public final int VIEW_NUM_ORG=40; //초기화면에 보여줄 데이터수
    public final int INQUIRY_NUM_ORG=200; //조회할 데이터수.
    public final int MIN_VIEW_NUM=5; //최소 보여줄 데이터수
    public final int MAX_VIEW_NUM=1000; //최대 보여줄 데이터수

    public int SCROLL_B = 0;     //스크롤바 톺이
    //2013.04.05  >> 고해상도 처리
//    public int XSCALE_H = (int)COMUtil.getPixel(12);
    //public int XSCALE_H = (int)COMUtil.getPixel(22);
    public int XSCALE_H = (int)COMUtil.getPixel_H(18); // 날짜 영역 높이
    public int EVENT_BADGE_H = (int)COMUtil.getPixel_H(18); //2021.04.28 by lyk - kakaopay - 종목 캘린더 이벤트 뱃지 표시 영역 높이
    //2013.04.05  <<
    public int chartType = 0;

    private int num=0;     //차트 한화면에 그릴 데이터의 수(확대/축소에 사용)
    public int inquiryNum=200;
    public int TOOLBAR_B=0;//하단 툴바의 높이
    private int defIndex = 11;
    private int nIndicatorColorIndex = 0;

    private boolean usePopup=true;     //팝업 사용여부
    private boolean useUnderToolbar=true;
    private boolean useToolbar=false;
    private boolean useTooltip =false;
    private boolean isOnePage=false;
    private boolean useStatusBar = false;//차트 하단 데이터 보기
    private boolean isVerticalMode = false; //세로보기
    private boolean changable_ScaleMode=true;//가변모드
    private boolean useSimpleMenu=false;//간략형 메뉴스타일
    boolean change=false;
    boolean resize=false;

    private Vector<ViewChangedListener> listeners;
    private boolean setStandGraph=false;
    public String sStandGraphName = "";//2015.06.25 by lyk - standGraph 이름 설정
    private boolean showRightsState=false;//락구분

    private float xfactor;
    private int toolbar_state=9999;//선택된 툴바
    private int tooltip_idx;//툴팁의 종류

    public Vector<String> chartTitle;

    public int BMargin_T=0;//블럭의 상단마진
    public int BMargin_B=0;//블럭의 하단마진
    public int Margin_T=0;
    public int Margin_B=0;
    //각 BlockViewModel에서 셋팅한다.
    public int Margin_L=0;
    public int Margin_R=0;
    //일목균형도 퓨쳐마진.
    public int futureMargin = 0;

    public boolean isLog=false;
    //2012. 11. 2  캔들설정창의 최대/최소값 체크버튼 값. : I107
    public boolean isCandleMinMax=false;
    //private Color backcolor = Color.white;
    private int[] backcolor = CoSys.CHART_BACK_MAINCOLORS;
    public int[] CB=CoSys.BLACK;//차트 배경색?
    public int[] CBB=CoSys.GRAY;//블럭 테두리 색
    public int[] CAT={39, 147, 13};//분석도구 색
    public int[] CSL={225, 225, 225};//스케일 라인칼라
    public int[] CST={255, 255, 255};//스케일 텍스트 칼라
    private int scale_line_type=1;//스케일 라인타입 0:실선, 1:점선,2:없슴
    private int block_col_cnt;
    public ImageView[] img = new ImageView[2];//수치조회
    private int[][] CGB= {CoSys.RED,CoSys.RED,CoSys.RED,CoSys.RED};
    public int YSCALE_POS=0;
    //2012. 8. 6 주가이.평  5, 6번째 값 추가 : I65
    //    public int[] average_title={5,20,60,120, 200, 300};//이동평균선의 종류
    //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
    //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//    public int[] average_title={5,20,60,120, 200, 300, 400, 500, 600, 700};//이동평균선의 종류
//    public int[] average_title={5, 10, 20, 60, 120, 200, 300, 400, 500, 600};//이동평균선의 종류
    public int[] average_title={5, 10, 20, 60, 120, 200, 240, 300, 400, 500};//이동평균선의 종류
    //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
    //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<
    //    public boolean[] average_state={true,true,true,true, false, false};
    //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
    //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//    public boolean[] average_state={true,true,true,false, false, false, false, false, false, false};
//    public boolean[] average_state={true, true, true, true, true, true, false, false, false, false};
    public boolean[] average_state={true, true, true, true, true, false, false, false, false, false}; //5개
    //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
    //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<

    //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
    public static final int AVERAGE_GENERAL=0; //단순이평
    public static final int AVERAGE_WEIGHT=1;//가중이평
    public static final int AVERAGE_EXPONENTIAL=2;  //지수이평
    public static final int AVERAGE_GEOMETIC=3;  //기하이평

    public static final int AVERAGE_DATA_CLOSE=0;//종가
    public static final int AVERAGE_DATA_OPEN=1; //시가
    public static final int AVERAGE_DATA_HIGH=2;  //고가
    public static final int AVERAGE_DATA_LOW=3;  //저가
    public static final int AVERAGE_DATA_HL2=4;  //(고+저)/2
    public static final int AVERAGE_DATA_HLO3=5;  //(고+저+종)/3
    //2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산

    public static final int CHART_LINE_ROUNDED_BAR=5;
    public static final int CHART_THREE_ROUNDED_BAR=6;
    public static final int CHART_HORIZONTAL_ROUNDED_BAR=7;

    //2012. 8. 6 주가이.평  5, 6번째 값 추가 : I65
    //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 >>
//    public int[] vol_average_title={5,20,60,120};//이동평균선의 종류
//    public boolean[] vol_average_state={true,true,true,false};
    public int[] vol_average_title={5,10,20,60};//이동평균선의 종류
    public boolean[] vol_average_state={true,true,true,true};
    //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 <<

    public static final int VOL_NO_OPTION=0; //거래량 조건없슴
    public static final int VOL_WITH_PRICE=1;//전일대비 종가 상승/하락
    public static final int VOL_WITH_VOL=2;  //전일대비 거래량 상승/하락
    public static final int VOL_WITH_SPRICE=3;//당일대비 시가 상승/하락
    //2013.09.27 by LYH >> 캔들색과 같이 디폴트로 변경 <<
    private int vol_drawType=3;              //2016.01.05 by LYH >> 디폴트 설정값 변경 거래량 전봉기준 상승/하락
    //2012. 9. 14 캔들상세설정창 상태변수 추가
    private int candle_basePrice=1;              //상승/하락기준:전봉  콤보박스  (0:시가, 1:종가)
    private int candle_sameColorType=1;              //2016.01.05 by LYH >> 디폴트 설정값 변경 시가=종가일경우색상표시 콤보박스 (0:전봉종가대비, 1:상승색상, 2:하락색상)

    public int preViewNum=-1;
    public int curIndex = -1;
    public boolean isCrosslineMode = false;
    public boolean useJipyoSign = false; //지표 현재값 표시 유무.
    public boolean isCredigJipyo = false; //신용잔고, 신용잔고율 지표 유무.
    //2011.08.05 by LYH >> 차트별 높이 별도로 가지고 있도록 처리(drawString 위함) <<
    public int nChartHeight=0;
    //public TextFont tf;

    private ByteBuffer lineByteBuffer = null;
    private FloatBuffer lineBuffer = null;
    private float[] lineVertices=new float[4];

    private ByteBuffer rectByteBuffer = null;
    private FloatBuffer rectBuffer = null;
    private float[] rectVertices=new float[8];

    private ByteBuffer triByteBuffer = null;
    private FloatBuffer triBuffer = null;
    private float[] triVertices=new float[6];

    public Vector analItem = null;
    public String preSelectLabel;
    public Vector preMenuItem;
    public Vector indicatorItem;
    public boolean isCaptureMode = false;
    public Bitmap captureImg = null;

    private int nSkinType = COMUtil.SKIN_WHITE;
    Paint mPaint;
    Path mPath;
    public Paint mPaint_Text;

    String strRecvDragPrice = "";	//2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기

    private boolean bIsQuery=false;
    private int nSetViewNum=-1;

    //2012. 11. 21 해외선물 분차트 플래그 :  C31
    public boolean bIsLineFillChart = false;
    public boolean bIsLineChart = false;
    public boolean bIsLine2Chart = false;
    public int[] cLineColor={153, 153, 153};
    public boolean bIsHighLowSign = false;//가로선
    public String m_sLineThick = "";

    public boolean bIsNewsChart = false;
    public boolean bIsTransparent = false; 	//2015. 10. 19 현재가차트 투명 처리
    public boolean bIsMiniBongChart = false;
    public boolean bRateCompare = false;
    public boolean bInvestorChart = false;
    public boolean bRatePeriod = false;
    //2013.07.31 >> 기준선 라인 차트 타입 추가
    public boolean bStandardLine = false;
    public ArrayList<Integer> baseLineType = new ArrayList<Integer>();   //2013. 3. 6  기준선 다중선택

    // 2016.05.31 기준선 대비, 색상 굵기 >>
    public ArrayList<String> baseLineColors = new ArrayList<String>();
    public ArrayList<Integer> baseLineThicks = new ArrayList<Integer>();
    // 2016.05.31 기준선 대비, 색상 굵기 <<

    public boolean m_bCurrentChart = false;
    //2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
    public boolean bSubChart = false;

    public boolean bIsInnerText = false;	//상하식 바차트내 값 표시 유무
    public boolean bIsInnerTextVertical = false;
    public boolean bNetBuyChart = false;

    boolean m_bUsePrice;	//2015. 1. 13 분석툴 수정기능 및 자석기능 추가

    //public String bIsOHLCType = "0";//2015.04.30 by lyk - 바(시고저종) 유형 추가

    public boolean bUseCurrentColor = false;
    
    public int nTouchEventType = 1;
    
    public int nCompareType = 0;	//0:등락률비교(오버레이), 1:등락률비교, 2:추세비교

    public int PADDING_RIGHT = 0;

    public boolean isInverse = false;   //2016.09.29 by LYH >> 거꾸로차트 기능 추가.
    public boolean isGapRevision = false;   //2016.12.14 by LYH >>갭보정 추가

    public boolean bIsNoScale = false;  //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)

    public boolean m_bFXChart = false;

    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
    public boolean bIsShowTitle = true;
    public boolean bIsUpdownChart = false;
    //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

    //2019. 03. 14 by hyh - 테크니컬차트 개발
    public boolean bIsTechnical = false;

    //2019. 04. 01 by hyh - 차트 타이틀 적용 >>
    public String strChartTitle = "";
    public String strChartTitleColor = "";
    //2019. 04. 01 by hyh - 차트 타이틀 적용 <<

    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
    public int nFxMarginType = -1;

    public final static int FX_BUY = 0;
    public final static int FX_SELL = 1;
    public final static int FX_BUYSELL = 2;
    public final static int FX_AVERAGE = 3;
    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

    //2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 >>
    public boolean bIsConn = false;
    public boolean bIsDay = false;
    public boolean bIsFloor = false;
    //2019. 06. 05 by hyh - 연속, 주간, 본장 유무 설정 <<

    //2021.04.26 by lyk - kakaopay - 추가 기능 설정 >>
    public boolean isAvgBuyPriceFunc = true; //2022.04.11 by lyk - kakaopay - 구매평균가격 표시 디폴트 true로 변경 (기획요청)
    //2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) >>
//    public boolean isBuySellPriceFunc = false;
    public boolean isBuySellPriceFunc = true;
//    public boolean isCorporateCalendarFunc = false;
    public boolean isCorporateCalendarFunc = true;
    //2023.06.27 by SJW - 구매평균가격 표시 및 기업 캘린더 뱃지 표시 디폴트 true로 변경 (기획요청) <<
    public boolean isSupportResistanceLine = false;
    public boolean isAutoTrendWaveLine = false;
    public boolean isMovingAverageLine = true;
    //2021.04.26 by lyk - kakaopay - 추가 기능 설정 <<

    public boolean bIsAlarmChart = false; //2019. 04. 17 by hyh - 시세알람차트 개발
    public boolean bIsHideXYscale = false;  //2019.04.30 by lyj 라인차트 xscale 삭제
//    public boolean bIsShowChartItemView = true; //2019. 06. 12 by hyh - 차트 상단 아이템뷰 보기 유무
    public boolean bIsOrderChart = false; //2019. 06. 21 by hyh - 주문차트 개발

    //2019. 08. 21 by hyh - 자동추세선 복원 >>
    public int autoTrendLowType = 0;
    public int autoTrendWaveType = 0;
    public int autoTrendHighType = 0;
    public int autoTrendWType = 0;
    public int endName = 3;
    public int preName = 3;
    //2019. 08. 21 by hyh - 자동추세선 복원 <<

    public ArrayList<AREA> m_arrArea = new ArrayList<AREA>();   //2020.07.06 by LYH >> 캔들볼륨

    public boolean bIsTodayLineChart = false;

    public int nAssetType = 0;
    public final static int ASSET_LINE_MOUNTAIN = 1;
    public final static int ASSET_LINE = 2;
    public final static int ASSET_LINE_FILL = 3;
    public final static int ASSET_UPDOWN_BAR = 4;

    public int m_nChartType = 0;
    public boolean bIsOneQStockChart = false;

    //public boolean isExRightShow = true;
    public boolean isExRightShow = false;

    public boolean bIsUpdownGridChart = false;

    //2017.11 by PJM >> 자산 차트 적용
//    private String m_strAssetType = "";
    public boolean bIsBothScale = false;
    public String priceUnitStr = "";
    public boolean m_bUseAnimationLine = false;	//2015. 7. 8 애니메이션 라인차트
    public boolean m_bWorkingAnimationTimer = false; //2015.09.24 by lyk - 애니메이션 구현시 그래프의 draw만 호출되도록 하기 위함
    public boolean isLineCircleVisible = true;
    public NeoChart2 parent;
    private DrawAnimationLineTimer.drawAnimationLineChartListener aniLineListener;	//2015. 7. 7 애니메이션 라인차트
    public int accrueDataViewCnt = 0;
    public String[] accrueDataViewDatas = null;
    public Paint mPaint_Bar_Text;
    public int m_nAnimationDirection = 0;
    public static int UPDOWN_DIRECTION = 0;
    public static int RIGHT_DIRECTION = 1;
    //2017.11 by PJM >> 자산 차트 적용 end

    public boolean bGreenType = false;
    public int m_titlePos = Gravity.LEFT;

    //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
    public int g_nFontSizeBtn = 1;
    public int g_nVPFontSizeBtn = 1;
    //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 <<

    public Bitmap bitmapCircle; //2021.02.18 by HJW - 매입평균선 추가
    public Bitmap imgTradeSell;
    public Bitmap imgTradeBuy;

    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 >>
    public Bitmap imgSignalSell;
    public Bitmap imgSignalBuy;
    //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 <<

    //2021.05.21 by HJW - 프리애프터 적용 >>
    public boolean bIsPreAfterAreaVisible = false;
    public String strRegularOpenTime = "";
    public String strRegularCloseTime = "";
    //2021.05.21 by HJW - 프리애프터 적용 <<
    public boolean bIsShowPreAfter = false;
    public String strTimeZoneText = ""; //2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가

    public ChartViewModel(){
        listeners = new Vector<ViewChangedListener>();
        lineByteBuffer = ByteBuffer.allocateDirect(lineVertices.length * 4);
        rectByteBuffer = ByteBuffer.allocateDirect(rectVertices.length * 4);
        triByteBuffer = ByteBuffer.allocateDirect(triVertices.length * 4);
        indicatorItem = new Vector();

        mPaint = new Paint();
        mPaint.setStrokeWidth(1);

        mPaint_Text = new Paint();
        if(COMUtil.numericTypeface != null)
            mPaint_Text.setTypeface(COMUtil.numericTypeface);
        mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
        mPaint_Text.setAntiAlias(true);
        mPath = new Path();

        //2021.08.24 by lyk - kakaopay - 매매내역 이미지 표시 부하 개선 >>
        int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_ic_common_arrow_sell", "drawable", COMUtil.apiView.getContext().getPackageName());
        imgTradeSell = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
        layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_ic_common_arrow_buy", "drawable", COMUtil.apiView.getContext().getPackageName());
        imgTradeBuy = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
        //2021.08.24 by lyk - kakaopay - 매매내역 이미지 표시 부하 개선 <<

        //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 >>
        layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_ic_common_assist_arrow_red", "drawable", COMUtil.apiView.getContext().getPackageName());
        imgSignalBuy = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
        layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("kfit_mts_ic_common_assist_arrow_blue", "drawable", COMUtil.apiView.getContext().getPackageName());
        imgSignalSell = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
        //2021.09.29 by lyk - kakaopay - 보조지표 시그널 이미지 표시 부하 개선 <<

        //xcale height 설정
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
            //2013.04.05 >> 고해상도 처리
            //XSCALE_H = (int)COMUtil.getPixel(16);
            XSCALE_H = (int)COMUtil.getPixel(25);
            //2013.04.05 <<
        }
    }
    //한화면 보기 설정
    public void setOnePage(int i){
        if(i==0)isOnePage=false;
        else isOnePage = true;
    }
    //거래량의 그리기 타입 설정
    public void setVolDrawType(int type){
        vol_drawType = type;
    }
    public int getVolDrawType(){
        return vol_drawType;
    }
    //2012. 9. 14 캔들상세설정창 상태변수들의 getter/setter 추가
    public int getCandle_basePrice() {
        return candle_basePrice;
    }
    public void setCandle_basePrice(int candle_basePrice) {
        this.candle_basePrice = candle_basePrice;
    }
    public int getCandle_sameColorType() {
        return candle_sameColorType;
    }
    public void setCandle_sameColorType(int candle_sameColorType) {
        this.candle_sameColorType = candle_sameColorType;
    }

    //2012. 11. 2  최대최소, 로그 체크버튼 값 알아오기 : I107
    public boolean getIsLog() {
        return isLog;
    }
    public void setIsLog(boolean isLog) {
        this.isLog = isLog;
    }
    public boolean getIsCandleMinMax() {
        return isCandleMinMax;
    }
    public void setIsCandleMinMax(boolean isCandleMinMax) {
        this.isCandleMinMax = isCandleMinMax;
    }

    public void setStandGraph(boolean b){
        setStandGraph = b;

        //2015.06.25 by lyk - standGraph 이름 설정
        if(!b)
            sStandGraphName = "";
        //2015.06.25 by lyk - standGraph 이름 설정

    }
    public boolean isStandGraph(){
        return setStandGraph;
    }
    //2015.06.25 by lyk - standGraph 이름 설정
    public void setStandGraphName(String name){
        sStandGraphName = name;
    }
    public String getStandGraphName() {
        return sStandGraphName;
    }
    //2015.06.25 by lyk - standGraph 이름 설정 end
    //간략형 메뉴를 사용설정
    public void setUseSimpleMenu(boolean b){
        useSimpleMenu = b;
    }
    public boolean isSimpleMenuStyle(){
        return useSimpleMenu;
    }

    //가변모드, 고정모드 설정 
    public void setScaleMode(boolean b){
        this.changable_ScaleMode = b;
    }
    public boolean getScaleMode(){
        return this.changable_ScaleMode;
    }
    public void setScaleLineColor(int[] col){
        CSL=col;
    }
    public void setScaleTextColor(int[] col){
        CST=col;
    }
    public void setBlockColor(int[] cb, int[] cbb){
        this.CB = cb;
        this.CBB= cbb;
    }
    public void setBlockBoundLineColor(int[] cbb){
        this.CBB = cbb;
        ViewEvent evt = new ViewEvent(this,this,ViewEvent.BACK_COLOR_CHANGE);
        processViewChangeEvent(evt);
    }
    public void setAnalToolColor(int[] cat){
        this.CAT = cat;
    }
    public void setToolbarState(int state){
        toolbar_state = state;
    }
    public void setGraphBaseColor1(int[] c){
        CGB[0] = c;
    }
    public void setGraphBaseColor2(int[] c){
        CGB[1] = c;
    }
    public void setGraphBaseColor3(int[] c){
        CGB[2] = c;
    }
    public void setGraphBaseColor4(int[] c){
        CGB[3] = c;
    }
    public void setGraphBaseColor5(int[] c){
        CGB[4] = c;
    }
    public void setGraphBaseColor6(int[] c){
        CGB[5] = c;
    }
    public int[][] getGraphBaseColor(){
        return CGB;
    }
    public void showRightsState(boolean b){//락구분
        this.showRightsState = b;
    }
    public boolean isShowingRightState(){
        return showRightsState;
    }
    public void addGraph(String title){
        if(chartTitle==null)chartTitle = new Vector<String>();
        chartTitle.addElement(title);
    }
    public void removeGraph(String title){
        if(chartTitle==null)return;
        chartTitle.removeElement(title);
    }
    public int getToolbarState(){
        return toolbar_state;
    }
    public boolean isOnePage(){
        return isOnePage;
    }
    //상단의 마진
    public void setMarginT(int t){
        Margin_T= t;
    }
    //하단의 마진
    public void setMarginB(int b){
        Margin_B = b;
    }
    //블럭의 하단 마진
    public void setBlockMarginB(int b){
        BMargin_B = b;
    }
    //블럭의 상단 마진
    public void setBlockMarginT(int t){
        BMargin_T =t;
    }

    public void setMarginL(int l){
        Margin_L = l;
    }
    public void setMarginR(int r){
        Margin_R = r;
    }

    public void setBlockColumnCount(int c){
        block_col_cnt = c;
    }
    public int getBlockColumnCount(){
        return block_col_cnt;
    }
    public void setUseUnderToolbar(boolean b){
        useUnderToolbar = b;
        if(b)TOOLBAR_B+=17;
    }
    public void setUsePopupMenu(int b){
        usePopup =(b==0)?false:true;
    }
    public void setScaleLineType(int type){
        scale_line_type = type;
        ViewEvent evt = new ViewEvent(this,this,ViewEvent.VIEW_MODE_CHANGE);
        processViewChangeEvent(evt);
    }
    public int getScaleLineType(){
        return scale_line_type;
    }
    public boolean usePopupMenu(){
        return usePopup;
    }
    public boolean useUnderToolbar(){
        return useUnderToolbar;
    }
    public void setUseToolbar(boolean b){
        useToolbar = b;
    }
    public boolean useToolbar(){
        return useToolbar;
    }
    //================================
    // tooltip의 종류 
    // 종가만 보여주는 툴팁, 
    // 차트에 그린 모든 그래프를 보여주는 툴팁 
    //================================
    public void setUseTooltib(boolean b,int index){
        tooltip_idx = index;
        useTooltip = b;
    }
    public boolean useTooltip(){
        return useTooltip;
    }
    public int getTooltipType(){
        return tooltip_idx;
    }
    public void setTooltipType(int type){
        tooltip_idx = type;
    }
    public void setUseStatusBar(boolean b){
        useStatusBar = b;
        if(b)TOOLBAR_B+=17;
    }
    public boolean useStatusBar(){
        return useStatusBar;
    }
    public void setToolBar(String[] to){
        for(int i=0;i<to.length;i++){
            if(to[i].startsWith("스크롤바")){
                setUseUnderToolbar(true);
            }else if(to[i].startsWith("수치조회데이터보기")){
                setUseStatusBar(true);
            }else if(to[i].startsWith("추세선")){
                setUseToolbar(true);
            }
        }
    }
    public void setBackColor(int[] c){
        backcolor = c;
        ViewEvent evt = new ViewEvent(this,this,ViewEvent.BACK_COLOR_CHANGE);
        processViewChangeEvent(evt);
    }
    public int[] getBackColor(){
        return backcolor;
    }
    // public Cursor[] cursor = {new Cursor(Cursor.DEFAULT_CURSOR),new Cursor(Cursor.HAND_CURSOR),new Cursor(Cursor.N_RESIZE_CURSOR)};

    public int[] getLineDrawColor() {
        int[] rtnColor = {3, 225, 193};
        int indIndex = nIndicatorColorIndex % 7;
        if (indIndex == 0) {
            nIndicatorColorIndex = 0;
        } else {
            nIndicatorColorIndex = indIndex;
        }
        // CoSys.CHART_COLORS[11] ~[17]
        rtnColor = CoSys.CHART_COLORS[nIndicatorColorIndex + defIndex];
        nIndicatorColorIndex++;

        return rtnColor;
    }
    public void setIndex(int index){
        this.index = index;
        ViewEvent evt = new ViewEvent(this,this,ViewEvent.VIEW_INDEX_CHANGE);
        processViewChangeEvent(evt);
    }
    public int getIndex(){
        return index;
    }
    //xfm에서 정한 화면당 데이터 수를 설정
    public void setViewNum_org(int num){
        this.num = num;
        //setViewNum(num);
    }
    public void setViewNum(int num){
        if(num>0){
        	if(num < MIN_VIEW_NUM && !bInvestorChart) {
                num = MIN_VIEW_NUM;
            }
        	
            int nPrevNum = this.num;
            //if(!bIsQuery)
            //2023.09.15 by SJW - 차트유형 설정시 이전 봉 개수 조회 못하는 현상 수정 >>
//            if(!bIsQuery && !isOnePage)
            if(!bIsQuery && !isOnePage && !isStandGraph())
            //2023.09.15 by SJW - 차트유형 설정시 이전 봉 개수 조회 못하는 현상 수정 <<
                nSetViewNum = num;
            this.num = num;

//            if(num > 2000)
//            {
//                this.num = 2000;
//                if(!bIsQuery && !isOnePage)
//                    nSetViewNum = this.num;
//            }

            if(nPrevNum != num) {
                ViewEvent evt = new ViewEvent(this, this, ViewEvent.VIEW_NUM_CHANGE);
                processViewChangeEvent(evt);
            }
        }
    }
    public void setInquiryNum(int num) {
        inquiryNum = num;
        if(inquiryNum < 200)
            inquiryNum = 200;
    }
    public void setDataWidth(float xfactor){
        this.xfactor=xfactor;
    }
    public float getDataWidth(){
        return xfactor;
    }
    public int getViewNum(){
        return this.num;
    }
    public void setBounds(float sx, float sy, float w, float h){
        bounds = new RectF(sx,sy,w,h);
    }
    public void setBounds(RectF bounds){
        this.bounds = bounds;
    }
    public void setHBounds(float sy, float h){
        RectF r = new RectF(bounds.left,sy,bounds.width(),h);
        this.bounds = r;
    }
    public RectF getBounds(){
        return bounds;
    }
    public float getWidth(){
        return this.bounds.width();
    }
    public float getHeight(){
        return this.bounds.height();
    }
    public void setChangeState(boolean b){
        change = b;
    }
    public void setResizeState(boolean b){
        resize = b;
    }
    public boolean isChange(){
        return change;
    }
    public boolean isResize(){
        return resize;
    }
    public boolean isVerticalMode(){
        return isVerticalMode;
    }
    public void setVerticalMode(boolean b){
        isVerticalMode = b;
    }

    public void addViewChangedListener(ViewChangedListener l){
        listeners.addElement(l);
    }
    public void removeViewChangedListener(ViewChangedListener l){
        listeners.removeElement(l);
    }
    protected synchronized void processViewChangeEvent(ViewEvent evt){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ViewChangedListener l = (ViewChangedListener)e.nextElement();
            switch(evt.getViewState()){
                case ViewEvent.VIEW_INDEX_CHANGE:
                    l.ViewIndexChanged(evt);
                    break;
                case ViewEvent.VIEW_NUM_CHANGE:
                    l.ViewNumChanged(evt);
                    break;
                case ViewEvent.VIEW_MODE_CHANGE:
                    l.ViewModeChanged(evt);
                    break;
                case ViewEvent.BACK_COLOR_CHANGE:
                    l.ViewBackColorChanged(evt);
                    break;
            }
        }
    }
    public float getXFactor(float width){
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
        //return (width*1.0f)/(num+futureMargin);
        return (width*1.0f)/(num);
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
    }
    public float getXFactorWithCnt(float width, int cnt){
        //2021.11.23 by JJH >> 특수차트에서 일목균형표 활성화시 수치조회창 위치가 스케일 끝까지 출력되지않는 오류 수정 start
//        return (width*1.0f)/(cnt+futureMargin);
        return (width*1.0f)/(cnt);
        //2021.11.23 by JJH >> 특수차트에서 일목균형표 활성화시 수치조회창 위치가 스케일 끝까지 출력되지않는 오류 수정 end
    }
    public void drawLine(GL10 gl, float x1, float y1, float x2, float y2, int[] color, float alpha) {
        gl.glClearColor(0.0f, 0.0f, 0.0f,0.0f);    //Black Background
        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, alpha);
        gl.glPushMatrix();

        lineVertices[0]=x1;
        lineVertices[1]=y1;
        lineVertices[2]=x2;
        lineVertices[3]=y2;

        for(int i=0; i<lineVertices.length; i++ )
            lineByteBuffer.putFloat(i, lineVertices[i]);

        lineByteBuffer.order(ByteOrder.nativeOrder());
        if(lineBuffer == null)
            lineBuffer = lineByteBuffer.asFloatBuffer();

        lineBuffer.put(lineVertices);
        lineBuffer.position(0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lineBuffer );
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        gl.glPopMatrix();
    }

    public void drawLines(GL10 gl, float positions[], int[] color, float alpha) {
        gl.glClearColor(0.0f, 0.0f, 0.0f,0.0f);    //Black Background
        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, alpha);
        gl.glPushMatrix();
//    	gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

        ByteBuffer lineByteBuffers = ByteBuffer.allocateDirect(positions.length * 4);
        lineByteBuffers.order(ByteOrder.nativeOrder());

        FloatBuffer	lineBuffers = lineByteBuffers.asFloatBuffer();

        lineBuffers.put(positions);
        lineBuffers.position(0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lineBuffers );
        gl.glDrawArrays(GL10.GL_LINES, 0, positions.length/2);
//        gl.glDisable(gl.GL_BLEND);
        gl.glPopMatrix();
    }
    public void drawFillRects(GL10 gl, float[] positions, int[] color, float alpha) {
        // Calculate bounding rect and store in vertices
        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, alpha);
        gl.glPushMatrix();

        ByteBuffer byteBuffers = ByteBuffer.allocateDirect(positions.length * 4);
        byteBuffers.order(ByteOrder.nativeOrder());

        FloatBuffer	rectBuffers = byteBuffers.asFloatBuffer();

        rectBuffers.put(positions);
        rectBuffers.position(0);

        gl.glVertexPointer (2, GL10.GL_FLOAT , 0, rectBuffers);
        gl.glDrawArrays (GL10.GL_TRIANGLES , 0, positions.length/2);
        //gl.glDrawElements(GL10.GL_POLYGON_OFFSET_FILL, 4, GL10.GL_FLOAT, rectBuffers);

        gl.glPopMatrix();
    }

    public void drawFillRect(GL10 gl, float x, float y, float w, float h, int[] color, float alpha) {
        // Calculate bounding rect and store in vertices
        gl.glPushMatrix();

        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, alpha);

        rectVertices[0]=x;
        rectVertices[1]=y;
        rectVertices[2]=x;
        rectVertices[3]=y+h;
        rectVertices[4]=x+w;
        rectVertices[5]=y+h;
        rectVertices[6]=x+w;
        rectVertices[7]=y;

        for(int i=0; i<rectVertices.length; i++ )
            rectByteBuffer.putFloat(i, rectVertices[i]);

        rectByteBuffer.order(ByteOrder.nativeOrder());
        if(rectBuffer == null)
            rectBuffer = rectByteBuffer.asFloatBuffer();

        rectBuffer.put(rectVertices);
        rectBuffer.position(0);

        gl.glVertexPointer (2, GL10.GL_FLOAT , 0, rectBuffer);
        gl.glDrawArrays (GL10.GL_TRIANGLE_FAN, 0, 4);
        gl.glPopMatrix();
    }

    public void drawRect(GL10 gl, float x, float y, float w, float h, int[] color) {
        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, 1.0f);

        gl.glPushMatrix();

        rectVertices[0]=x;
        rectVertices[1]=y;
        rectVertices[2]=x;
        rectVertices[3]=y+h;
        rectVertices[4]=x+w;
        rectVertices[5]=y+h;
        rectVertices[6]=x+w;
        rectVertices[7]=y;

        for(int i=0; i<rectVertices.length; i++ )
            rectByteBuffer.putFloat(i, rectVertices[i]);

        rectByteBuffer.order(ByteOrder.nativeOrder());
        if(rectBuffer == null)
            rectBuffer = rectByteBuffer.asFloatBuffer();
        //}
//    	lineRect.clear();
        rectBuffer.put(rectVertices);
        rectBuffer.position(0);

        gl.glVertexPointer (2, GL10.GL_FLOAT , 0, rectBuffer);
        gl.glDrawArrays (GL10.GL_LINE_LOOP, 0, 4);
        gl.glPopMatrix();
        //bbRect = null;
    }

    public void drawFillTri(GL10 gl, float x, float y, float w, float h, int[] color) {

        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, 1.0f);

        gl.glPushMatrix();

//    	if(h>0)
//    	{
        triVertices[0]=x;
        triVertices[1]=y;
        triVertices[2]=x+w;
        triVertices[3]=y+h/2;
        triVertices[4]=x+w;
        triVertices[5]=y-h/2;


        for(int i=0; i<triVertices.length; i++ )
            triByteBuffer.putFloat(i, triVertices[i]);

        triByteBuffer.order(ByteOrder.nativeOrder());
        if(triBuffer == null)
            triBuffer = triByteBuffer.asFloatBuffer();

        triBuffer.put(triVertices);
        triBuffer.position(0);

        gl.glVertexPointer (2, GL10.GL_FLOAT , 0, triBuffer);
        gl.glDrawArrays (GL10.GL_TRIANGLES, 0, 3);
        gl.glPopMatrix();

    }
    public void drawTriangle(GL10 gl, float x, float y, float w, float h, int[] color) {
        gl.glColor4f(color[0]/255.f, color[1]/255.f, color[2]/255.f, 1.0f);

        gl.glPushMatrix();

//    	if(h>0)
//    	{
        triVertices[0]=x+w/2;
        triVertices[1]=y;
        triVertices[2]=x;
        triVertices[3]=y+h;
        triVertices[4]=x+w;
        triVertices[5]=y+h;
//    	}
//    	else
//    	{
//	    	triVertices[0]=x+w/2;
//	    	triVertices[1]=y;
//	    	triVertices[2]=x+w;
//	    	triVertices[3]=y;
//	    	triVertices[4]=x+w/2;
//	    	triVertices[5]=y+h;
//    	}

        for(int i=0; i<triVertices.length; i++ )
            triByteBuffer.putFloat(i, triVertices[i]);

        triByteBuffer.order(ByteOrder.nativeOrder());
        if(triBuffer == null)
            triBuffer = triByteBuffer.asFloatBuffer();
        //}
//    	linetri.clear();
        triBuffer.put(triVertices);
        triBuffer.position(0);

        gl.glVertexPointer (2, GL10.GL_FLOAT , 0, triBuffer);
        gl.glDrawArrays (GL10.GL_LINE_LOOP, 0, 3);
        gl.glPopMatrix();
        //bbtri = null;
    }

    //2014. 9. 15 매매 신호 보기 기능 추가>>
    public void drawFillTriangle(Canvas gl, float x, float y, float w, float h, int[] color) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPath.reset();
        if(h>0)
        {
            mPath.moveTo(x+w/2, y);
            mPath.lineTo(x, y+h);
            mPath.lineTo(x+w, y+h);
            mPath.lineTo(x+w/2, y);
        }
        else
        {
            mPath.moveTo(x, y);
            mPath.lineTo(x+w/2, y-h);
            mPath.lineTo(x+w, y);
            mPath.lineTo(x, y);
        }
        mPath.close();
        gl.drawPath(mPath, mPaint);
    }
    //2014. 9. 15 매매 신호 보기 기능 추가<<

    public void drawString(final GL10 gl, int[] color, int x, int y, String str) {
//       	//gl.glPushMatrix();
//    	if(tf == null)
//    		return;
//    	tf.SetPolyColor(color[0]/255.f, color[1]/255.f, color[2]/255.f);
//    	
//    	//현재 Text draw방식이 image font를 사용하기 때문에, 한글이 지원되지 않는다.
//    	str = COMUtil.jipyoNameToEng(str);		
//    	//str는 영문 대문자, 숫자 특수문자(일부)만 가능.
//    	tf.PrintAt(gl, str, x, nChartHeight-y-26); //default:12 
//    	//gl.glPopMatrix();
    }


    public void setSkinType(int skinType)
    {
        nSkinType = skinType;
        if(bIsNewsChart)
//        	CST=CoSys.WHITE;
            CST=CoSys.BLACK;
        else if(nSkinType == COMUtil.SKIN_BLACK)
            CST=CoSys.WHITE_TEXT;
        else
            CST=CoSys.BLACK_TEXT;
    }
    public int getSkinType()
    {
        return nSkinType;
    }
    //2012.08.08 by LYH>>

    public void drawRect(Canvas gl, float x, float y, float w, float h, int[] color) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setStyle(Paint.Style.STROKE);
        gl.drawRect(x, y, x+w, y+h, mPaint);
    }
    public void drawFillRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setStyle(Paint.Style.FILL);
        gl.drawRect(x, y, x+w, y+h, mPaint);
        mPaint.setAlpha(255);
    }
    public void drawFillCornerRoundedRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setStyle(Paint.Style.FILL);

        float radius = COMUtil.getPixel(3.5f);
        CornerPathEffect corEffect = new CornerPathEffect(radius);
        mPaint.setPathEffect(corEffect);

        gl.drawRect(x, y, x+w, y+h, mPaint);
        mPaint.setAlpha(255);
        mPaint.setPathEffect(null);
    }
    public void drawLine(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        gl.drawLine(x1, y1, x2, y2, mPaint);
        mPaint.setAlpha(255);
    }

    public void drawLines(Canvas gl, float positions[], int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        gl.drawLines(positions, mPaint);
        mPaint.setAlpha(255);
    }

    public void drawDashLine(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setPathEffect(new DashPathEffect(new float[]{COMUtil.getPixel(1),COMUtil.getPixel(2)},0));
        gl.drawLine(x1, y1, x2, y2, mPaint);
        mPaint.setAlpha(255);
        mPaint.setPathEffect(null);
    }
    public void drawDashLine_interval(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha, float interval, float phase) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setPathEffect(new DashPathEffect(new float[]{interval,phase},0));
        gl.drawLine(x1, y1, x2, y2, mPaint);
        mPaint.setAlpha(255);
        mPaint.setPathEffect(null);
    }

    public void drawImage(Canvas c, float x, float y, float width, float height, Bitmap bitImg, int alpha)
    {
        RectF rec = new RectF();
        rec.set(x, y, x + width, y + height);

        mPaint.setAlpha(alpha);
        c.drawBitmap(bitImg, null, rec, mPaint);
        mPaint.setAlpha(255);
    }

    public void drawDimImageWithShadow(Context context, Canvas c, float x, float y, float width, float height, Bitmap bitImg, int alpha)
    {
        RectF rec = new RectF();
        rec.set(x, y, x + width, y + height);

        int layoutResId = context.getResources().getIdentifier("shadow_142342", "drawable", context.getPackageName());
        Bitmap bmpShadow = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
        RectF shadowRec = new RectF();
        shadowRec.set(x, y+height-50, x+width, y+height+15);

        mPaint.setAlpha(alpha);
        ColorFilter colorFilter = new PorterDuffColorFilter(Color.parseColor("#0a000000"),PorterDuff.Mode.DST_OVER);
        mPaint.setColorFilter(colorFilter);
        c.drawBitmap(bmpShadow, null, shadowRec, null);
        c.drawBitmap(bitImg, null, rec, mPaint);
        mPaint.setAlpha(255);
        mPaint.setColorFilter(null);
    }

    public void drawImage(Canvas c, float x, float y, float width, float height, Bitmap bitImg, int alpha, ColorFilter colorFilter)
    {
        RectF rec = new RectF();
        rec.set(x, y, x + width, y + height);

        mPaint.setAlpha(alpha);
        mPaint.setColorFilter(colorFilter);
        c.drawBitmap(bitImg, null, rec, mPaint);
        mPaint.setAlpha(255);
        mPaint.setColorFilter(null);
    }

    //2012. 11. 9  선차트 gradient 영역 그리기 함수 : C28
    public void drawLineWithFillGradient(Canvas gl, float positions[], float fHeight, int[] color0, int alpha)
    {
        //canvas객체, line의좌표(i, i+1)  이 각각 X,Y에 해당함,  높이, 색상, 알파값

        //데이터갯수가 2보다 작은건 x, y 좌표가 한개도 안왓던 의미이므로 함수를 나감
        if(positions.length< 2)
        {
            return;
        }

        //line 의 좌표들이 담겨있는 positions 를 Path 객체에 할당.
        Path mPath = new Path();
        for(int i=0; i < positions.length; i += 2)
        {
            mPath.lineTo(positions[i], positions[i+1]);
        }
        //라인좌표를 제외한 나머지 아랫쪽 영역 좌표를 줘서 다각형으로 세팅
        mPath.lineTo(positions[positions.length-2], fHeight);
        mPath.lineTo(positions[0], fHeight);
        mPath.lineTo(positions[0], positions[1]);

        //실제 paint 로 그리는 부분.
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //첫번째컬러 : 시작색상,  두번째컬러 : 끌색상
//        paint.setShader(new LinearGradient(0,0,0, fHeight/2, Color.rgb(color0[0], color0[1], color0[2]), Color.rgb(color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
        paint.setColor(Color.rgb(color0[0], color0[1], color0[2]));
        paint.setAlpha(alpha);
        gl.drawPath(mPath, paint);

        //다른쪽 차트화면의 알파값이나 gradient 를 변경하지 않도록 초기화
        paint.setAlpha(255);
    }

    //2012. 11. 9  선차트 gradient 영역 그리기 함수 : C28
    public void drawLineWithFillGradient(Canvas gl, float positions[], float fHeight, int[] color0, int alpha, int nLastIndex, float fGradiantY)	//2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
    {
        //canvas객체, line의좌표(i, i+1)  이 각각 X,Y에 해당함,  높이, 색상, 알파값

        //데이터갯수가 2보다 작은건 x, y 좌표가 한개도 안왓던 의미이므로 함수를 나감
        if(nLastIndex< 2)
        {
            return;
        }

        //line 의 좌표들이 담겨있는 positions 를 Path 객체에 할당.
        Path mPath = new Path();
        for(int i=0; i < nLastIndex; i += 2)
        {
            mPath.lineTo(positions[i], positions[i+1]);
        }
        //라인좌표를 제외한 나머지 아랫쪽 영역 좌표를 줘서 다각형으로 세팅
        mPath.lineTo(positions[nLastIndex-2], fHeight);
        mPath.lineTo(positions[0], fHeight);
        mPath.lineTo(positions[0], positions[1]);

        //실제 paint 로 그리는 부분.
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //첫번째컬러 : 시작색상,  두번째컬러 : 끌색상 
//        paint.setShader(new LinearGradient(0,0,0, fHeight/2, Color.rgb(color0[0], color0[1], color0[2]), Color.rgb(color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
        if(color0.length > 3)
            paint.setColor(Color.argb(color0[3], color0[0], color0[1], color0[2]));
        else
            paint.setColor(Color.rgb(color0[0], color0[1], color0[2]));
        //2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
        //paint.setAlpha(alpha);
        int nGradiantAlpha = 0;//= 40
        if(alpha==255)
            nGradiantAlpha = 255;

        if(fGradiantY>0)
            paint.setShader(new LinearGradient(0,fGradiantY,0, fHeight, Color.argb(alpha, color0[0], color0[1], color0[2]), Color.argb(nGradiantAlpha, color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
        else if(fGradiantY<0)
            paint.setShader(new LinearGradient(0,fHeight,0, -1*fGradiantY, Color.argb(nGradiantAlpha, color0[0], color0[1], color0[2]), Color.argb(alpha, color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
        else
            paint.setAlpha(alpha);
        //2014.03.31 by LYH << 마운틴차트 그리기 개선(그라데이션).
        gl.drawPath(mPath, paint);

        //다른쪽 차트화면의 알파값이나 gradient 를 변경하지 않도록 초기화 
        paint.setAlpha(255);
    }

    //    public void setLineWidth(int nThick)
//    {
//    	mPaint.setStrokeWidth(nThick);
//    }
    public void setLineWidth(float nThick)
    {
        //if(COMUtil.getPixel(1)>=3)
//        if(COMUtil.getPixel(1)>2)
//        {
//            nThick *= COMUtil.getPixel(1)-1;
//        }
//        nThick *= COMUtil.getPixel(1);
//        mPaint.setStrokeWidth(nThick);
        if(nThick>=1 && COMUtil.getPixel(1)>2)
        {
            nThick *= COMUtil.getPixel(1)-1;
        }
        mPaint.setStrokeWidth(nThick);
    }
    public void setLineWidth_Fix(float nThick)
    {
        mPaint.setStrokeWidth(nThick);
    }
    public void drawString(Canvas gl, int[] color, float x, float y, String str) {
        if(str==null)
            return;

        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
//        mPaint_Text.setTextSize(COMUtil.getFontSize());
        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);
    }
    //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 >>
    public void drawStringRenko(Canvas gl, int[] color, float x, float y, String str) {
        if(str==null)
            return;

        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        gl.drawText(str, x, y, mPaint_Text);
    }
    //2021.11.01 by JHY - 렌코차트 박스 겹치는 부분 수정 <<
    public void drawScaleString(Canvas gl, int[] color, float x, float y, String str) {
        if(str==null)
            return;

//        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//            color = CoSys.GREY990;
//        } else {
//            color = CoSys.GREY0_WHITE;
//        }
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        float limitWidth = Margin_R - COMUtil.getPixel(8);
        float originSize = mPaint_Text.getTextSize();
//
        if ( mPaint_Text.measureText(str, 0, str.length()) > limitWidth ) {
//            mPaint_Text.setTextSize(originSize - 1);
            /////
            final float testTextSize = originSize;

            // Get the bounds of the text, using our testTextSize.
            mPaint_Text.setTextSize(testTextSize);
            Rect bounds = new Rect();
            mPaint_Text.getTextBounds(str, 0, str.length(), bounds);

            // Calculate the desired size as a proportion of our testTextSize.
            float desiredTextSize = testTextSize * limitWidth / bounds.width();

            // Set the paint for that size.
            mPaint_Text.setTextSize(desiredTextSize);
            //////
        } else {

        }

        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);

        mPaint_Text.setTextSize(originSize);
    }

    public void drawScaleString(Canvas gl, int[] color, float x, float y, String str, float alpha) {
        if(str==null)
            return;

//        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//            color = CoSys.GREY990;
//        } else {
//            color = CoSys.GREY0_WHITE;
//        }
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        mPaint_Text.setAlpha((int) (alpha * 255));
        float limitWidth = Margin_R - COMUtil.getPixel(8);
        float originSize = mPaint_Text.getTextSize();
//
        if ( mPaint_Text.measureText(str, 0, str.length()) > limitWidth ) {
//            mPaint_Text.setTextSize(originSize - 1);
            /////
            final float testTextSize = originSize;

            // Get the bounds of the text, using our testTextSize.
            mPaint_Text.setTextSize(testTextSize);
            Rect bounds = new Rect();
            mPaint_Text.getTextBounds(str, 0, str.length(), bounds);

            // Calculate the desired size as a proportion of our testTextSize.
            float desiredTextSize = testTextSize * limitWidth / bounds.width();

            // Set the paint for that size.
            mPaint_Text.setTextSize(desiredTextSize);
            //////
        } else {

        }

        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);

        mPaint_Text.setTextSize(originSize);

        mPaint_Text.setAlpha(255);
    }

    public void drawScaleStringWidth(Canvas gl, int width, int color, float x, float y, String str, float alpha) {
        if(str==null)
            return;

//        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//            color = CoSys.GREY990;
//        } else {
//            color = CoSys.GREY0_WHITE;
//        }
        mPaint_Text.setColor(color);
        mPaint_Text.setTextAlign(Align.LEFT);
        mPaint_Text.setAlpha((int) (alpha * 255));
        float limitWidth = width;
        float originSize = mPaint_Text.getTextSize();
//
        if ( mPaint_Text.measureText(str, 0, str.length()) > limitWidth ) {
//            mPaint_Text.setTextSize(originSize - 1);
            /////
            final float testTextSize = originSize;

            // Get the bounds of the text, using our testTextSize.
            mPaint_Text.setTextSize(testTextSize);
            Rect bounds = new Rect();
            mPaint_Text.getTextBounds(str, 0, str.length(), bounds);

            // Calculate the desired size as a proportion of our testTextSize.
            float desiredTextSize = testTextSize * limitWidth / bounds.width();

            // Set the paint for that size.
            mPaint_Text.setTextSize(desiredTextSize);
            //////
        } else {

        }

        gl.drawText(str, x, y, mPaint_Text);

        mPaint_Text.setTextSize(originSize);

        mPaint_Text.setAlpha(255);
    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param _paint
     *            the Paint to set the text size for
     * @param _desiredWidth
     *            the desired width
     * @param _text
     *            the text that should be that width
     */
//    private void setTextSizeForWidth(Paint paint, float desiredWidth,
//                                            String text) {
//
//        // Pick a reasonably large value for the test. Larger values produce
//        // more accurate results, but may cause problems with hardware
//        // acceleration. But there are workarounds for that, too; refer to
//        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
//        final float testTextSize = 48f;
//
//        // Get the bounds of the text, using our testTextSize.
//        paint.setTextSize(testTextSize);
//        Rect bounds = new Rect();
//        paint.getTextBounds(text, 0, text.length(), bounds);
//
//        // Calculate the desired size as a proportion of our testTextSize.
//        float desiredTextSize = testTextSize * desiredWidth / bounds.width();
//
//        // Set the paint for that size.
//        paint.setTextSize(desiredTextSize);
//    }

    public void drawString(Canvas gl, int[] color, float x, float y, String str, float alpha) {
        if(str==null)
            return;

        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        mPaint_Text.setAlpha((int) (alpha * 255));
//        mPaint_Text.setTextSize(COMUtil.getFontSize());
        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);
        mPaint_Text.setAlpha(255);
    }

    public void drawFillTri(Canvas gl, float x, float y, float w, float h, int[] color) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPath.reset();
        mPath.moveTo(x, y);
        mPath.lineTo(x+w, y-h/2);
        mPath.lineTo(x+w, y+h/2);
        mPath.lineTo(x, y);
        mPath.close();
        gl.drawPath(mPath, mPaint);
    }

    public void drawFillRects(Canvas gl, float[] positions, int[] color, float alpha) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));

        for(int i=0; i<positions.length/4; i++)
        {
            gl.drawRect(positions[i*4], positions[i*4+1], positions[i*4+2], positions[i*4+3], mPaint);
        }
    }
    public void drawFillRoundedRect(Canvas gl, float[] positions, int[] color, float alpha, float[] corners) {
        mPaint.setColor(Color.rgb(color[0], color[1], color[2]));
        mPaint.setAlpha((int) (alpha * 255));
        mPaint.setStyle(Paint.Style.FILL);

        for(int i=0; i<positions.length/4; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RectF rect = new RectF();
                rect.left = positions[i*4];
                rect.top = positions[i*4+1];
                rect.right = positions[i*4+2];
                rect.bottom = positions[i*4+3];

                final Path path = new Path();
                path.addRoundRect(rect, corners, Path.Direction.CW);
                gl.drawPath(path, mPaint);
            } else {
                gl.drawRect(positions[i*4], positions[i*4+1], positions[i*4+2], positions[i*4+3], mPaint);
            }
        }

        mPaint.setAlpha(255);
    }

    public void drawCircle(Canvas gl, float x, float y, float width, float height, boolean filled, int[] color) {
        if(filled)
            mPaint.setStyle(Paint.Style.FILL);
        else
            mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        RectF r = new RectF(x, y, width, height);
        gl.drawOval(r, mPaint);
    }

    public void drawTriangle(Canvas gl, float x, float y, float w, float h, int[] color) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        gl.drawLine(x+w/2, y, x, y+h, mPaint);  //삼각형
        gl.drawLine(x+w/2, y, x+w, y+h, mPaint);
        gl.drawLine(x , y+h, x+w, y+h, mPaint);
    }

    public void drawArc(Canvas gl, int radius, PointF center, boolean filled, int[] color) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        RectF bound = null;
        if(radius>0)
        {
            bound = new RectF(center.x-radius, center.y-radius, center.x+radius, center.y+radius);
            gl.drawArc(bound, 0, 180, false, mPaint);
        }
        else
        {
            bound = new RectF(center.x+radius, center.y+radius, center.x-radius, center.y-radius);
            gl.drawArc(bound, 180, 180, false, mPaint);
        }
    }

    public void drawArc1(Canvas gl, int radius, PointF center, boolean filled, int[] color, double start, double end) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        RectF bound = null;
        if(radius>0)
        {
            bound = new RectF(center.x-radius, center.y-radius, center.x+radius, center.y+radius);
            gl.drawArc(bound, (int)start, (int)end, false, mPaint);
        }
        else
        {
            bound = new RectF(center.x+radius, center.y+radius, center.x-radius, center.y-radius);
            gl.drawArc(bound, (int)start, (int)end, false, mPaint);
        }
    }

    public int GetTextLength(String text)
    {
//		int len = text.length();
//		float width = 0;
//		float[] widths = new float[len];
//		mPaint_Text.getTextWidths(text, widths);
//    	for(int i=0; i<len; i++){
//    		width += widths[i];
//    	}
//    	widths = null;
//		return (int)width;
        return (int)mPaint_Text.measureText(text);
        //return mPaint_Text.measureText(text);
    }

    //2012. 10. 24 차트설정창에서 전체초기화시 이평이 초기화되지 않는 현상 수정 : I100
    public void initAverage()
    {
//    	int[] _average_title={5,20,60,120, 200, 300};
        //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 >>
        //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
//        int[] _average_title={5,20,60,120, 200, 300, 400, 500, 600, 700};
//        int[] _average_title={5, 10, 20, 60, 120, 200, 300, 400, 500, 600};
        int[] _average_title={5, 10, 20, 60, 120, 200, 240, 300, 400, 500};
        //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<
        //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청 <<
        average_title = _average_title;
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 >>
//        int[] _vol_average_title={5,20,60,120};
        int[] _vol_average_title={5,10,20,60};
        //2023.06.27 by SJW - 거래량 이동평균선 "10일" 추가 <<
        vol_average_title = _vol_average_title;
    }

    public void setIsQuery(boolean bQuery)
    {
        bIsQuery = bQuery;
    }

    public int getSetViewNum()
    {
        return nSetViewNum;
    }
    //2012.08.08 by LYH<< 

    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
    public boolean getUsePrice()
    {
        return m_bUsePrice;
    }

    public void setUsePrice(boolean bUsePrice)
    {
        m_bUsePrice = bUsePrice;
    }
    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<
    //2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기>>
    /**
     * 메인에서 받은 가격을 설정 (ex: 목표가) 
     * @param _strRecvDragPrice 메인에서 받은 가격
     * */
    public void setDragPrice(String _strRecvDragPrice)
    {
        this.strRecvDragPrice = _strRecvDragPrice;
    }
    /**
     * 메인에서 받은 가격(ex: 목표가) 
     * @return strRecvDragPrice 메인에서 받은 가격
     * */
    public String getDragPrice()
    {
        return strRecvDragPrice;
    }
    //2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기<<

    public float getFontWidth(String strText, int size) {
        Paint measurePaint_Text = new Paint();
        measurePaint_Text.setTextSize(size);
        float width = measurePaint_Text.measureText(strText);
        return width;
    }

    public float getFontWidth_Mid(String strText, int size) {
        Paint measurePaint_Text = new Paint();
        measurePaint_Text.setTextSize(size);
        measurePaint_Text.setTypeface(COMUtil.numericTypefaceMid);
        float width = measurePaint_Text.measureText(strText);
        return width;
    }

    public float getFontHeight(String strText, int size) {
        Paint measurePaint_Text = new Paint();
        measurePaint_Text.setTextSize(size);
        Rect bounds = new Rect();
        measurePaint_Text.getTextBounds(strText, 0, strText.length(), bounds);
        float height = bounds.height();
        return height;
    }

    public void setFontSize(float size)
    {
        mPaint_Text.setTextSize(size);
    }
    public void drawStringWithSize(Canvas gl, int[] color, float x, float y, float size, String str) {
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        float textSize = mPaint_Text.getTextSize();
        mPaint_Text.setTextSize(size);
        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);
        //mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
        mPaint_Text.setTextSize(textSize);
    }
    public void drawStringWithSize(Canvas gl, int[] color, float x, float y, float size, String str, float alpha) {
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        float textSize = mPaint_Text.getTextSize();
        mPaint_Text.setTextSize(size);
        mPaint_Text.setAlpha((int)(alpha*255));
        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);
        //mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
        mPaint_Text.setTextSize(textSize);
        mPaint_Text.setAlpha(255);
    }
    public void drawStringWithSizeFont(Canvas gl, int[] color, float x, float y, float size, String str, Typeface typeface) {
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        float textSize = mPaint_Text.getTextSize();
        mPaint_Text.setTextSize(size);
        mPaint_Text.setTypeface(typeface);
        gl.drawText(str, x, y+COMUtil.getPixel_H(4), mPaint_Text);
//        mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
        mPaint_Text.setTextSize(textSize);
        mPaint_Text.setTypeface(COMUtil.numericTypeface);
    }

    //2016.09.29 by LYH >> 거꾸로차트 기능 추가.
    public boolean getIsInverse() {
        return isInverse;
    }
    public void setIsInverse(boolean isInverse) {
        this.isInverse = isInverse;
    }
    //2016.09.29 by LYH << 거꾸로차트 기능 추가.

    //2016.12.14 by LYH >>갭보정 추가
    public boolean getIsGapRevision() {
        return isGapRevision;
    }
    public void setIsGapRevision(boolean isGapRevision) {
        this.isGapRevision = isGapRevision;
    }
    //2016.12.14 by LYH >>갭보정 추가 end

    public void drawDashDotDotLine(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha) {
        mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint.setAlpha((int)(alpha*255));
        mPaint.setPathEffect(new DashPathEffect(new float[]{COMUtil.getPixel(2),COMUtil.getPixel(2)},0));
        gl.drawLine(x1, y1, x2, y2, mPaint);
        mPaint.setAlpha(255);
        mPaint.setPathEffect(null);
    }

    public void drawLineWithFillGradient_Flow(Canvas gl, float positions[], float positions2[], int[] color0, int alpha, int nLastIndex)	//2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
    {
        //canvas객체, line의좌표(i, i+1)  이 각각 X,Y에 해당함,  높이, 색상, 알파값

        //데이터갯수가 2보다 작은건 x, y 좌표가 한개도 안왓던 의미이므로 함수를 나감
        if(nLastIndex< 2)
        {
            return;
        }

        //line 의 좌표들이 담겨있는 positions 를 Path 객체에 할당.
        Path mPath = new Path();
        for(int i=0; i < nLastIndex; i += 2)
        {
            mPath.lineTo(positions[i], positions[i+1]);
        }
        //라인좌표를 제외한 나머지 아랫쪽 영역 좌표를 줘서 다각형으로 세팅
//        mPath.lineTo(positions2[nLastIndex-2], positions2[nLastIndex-1]);
        //mPath.lineTo(positions[nLastIndex-2], positions2[nLastIndex-1]);
        for(int i=2; i <= nLastIndex; i += 2)
        {
            mPath.lineTo(positions2[nLastIndex-i], positions2[nLastIndex-i+1]);
        }
        mPath.lineTo(positions[0], positions[1]);
//        mPath.lineTo(positions[0], fHeight);
//        mPath.lineTo(positions[0], positions[1]);

        //실제 paint 로 그리는 부분.
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //첫번째컬러 : 시작색상,  두번째컬러 : 끌색상
//        paint.setShader(new LinearGradient(0,0,0, fHeight/2, Color.rgb(color0[0], color0[1], color0[2]), Color.rgb(color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
        paint.setColor(Color.rgb(color0[0], color0[1], color0[2]));
        //2014.03.31 by LYH >> 마운틴차트 그리기 개선(그라데이션).
        //paint.setAlpha(alpha);
        int nGradiantAlpha = 0;//= 40
        if(alpha==255)
            nGradiantAlpha = 255;
//        if(fGradiantY>0)
//            paint.setShader(new LinearGradient(0,fGradiantY,0, fHeight, Color.argb(alpha, color0[0], color0[1], color0[2]), Color.argb(nGradiantAlpha, color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
//        else if(fGradiantY<0)
//            paint.setShader(new LinearGradient(0,fHeight,0, -1*fGradiantY, Color.argb(nGradiantAlpha, color0[0], color0[1], color0[2]), Color.argb(alpha, color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
//        else
        paint.setAlpha(alpha);
        //2014.03.31 by LYH << 마운틴차트 그리기 개선(그라데이션).
        gl.drawPath(mPath, paint);

        //다른쪽 차트화면의 알파값이나 gradient 를 변경하지 않도록 초기화
        paint.setAlpha(255);
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }
    public int getTextHeight(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.top + bounds.height();
        return width;
    }
    public void drawFillRoundedRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha, float[] corners) {
        mPaint.setColor(Color.rgb(color[0], color[1], color[2]));
        mPaint.setAlpha((int) (alpha * 255));
        mPaint.setStyle(Paint.Style.FILL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RectF rect = new RectF();
            rect.left = x;
            rect.top = y;
            rect.right = x + w;
            rect.bottom = y + h;

            final Path path = new Path();
            path.addRoundRect(rect, corners, Path.Direction.CW);
            gl.drawPath(path, mPaint);
        } else {
            gl.drawRect(x, y, x + w, y + h, mPaint);
        }

        mPaint.setAlpha(255);
    }

    public int getAssetType()
    {
        return nAssetType;
    }
    public void drawCurrentPriceBox(Canvas gl, float x, float y, float w, float h, int[] color) {
        //round 처리
//        float triangleWidth = COMUtil.getPixel(8.5f);
//
        float radius = COMUtil.getPixel(3.5f);

        CornerPathEffect corEffect = new CornerPathEffect(radius);

        mPaint.setPathEffect(corEffect);
        //round 처리

        //2021.05.13 by hanjun.Kim - kakaopay - 차트가격표삼각형 모양 제거 >>
//        float triangleWidth = COMUtil.getPixel_W(5.0f);
        float triangleWidth = 0f;
        //2021.05.13 by hanjun.Kim - kakaopay - 차트가격표삼각형 모양 제거 <<

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.rgb(color[0], color[1], color[2]));



        mPath.reset();

        mPath.moveTo(x + triangleWidth, y);

        mPath.lineTo(x + w, y);

        mPath.lineTo(x + w, y + h);

        mPath.lineTo(x + triangleWidth, y + h);

        mPath.lineTo(x, y + h / 2);

        mPath.close();



        gl.drawPath(mPath, mPaint);



        mPaint.setAlpha(255);

        mPaint.setPathEffect(null);

    }
    //2020.07.06 by LYH >> 캔들볼륨 >>
    public AREA getArea(int nIndex)
    {
        if(m_arrArea.size()>nIndex && nIndex>=0)
            return m_arrArea.get(nIndex);

        return null;
    }
    //2020.07.06 by LYH >> 캔들볼륨 <<

    //2017.11 by PJM >> 자산 차트 적용
    //2015. 7. 7 애니메이션 라인차트>>
    /**
     * 애니메이션 라인차트의 사용여부 설정
     * @param bFlag  애니메이션라인차트 사용(true), 미사용 (false)
     * */
    public void setUseAnimationLine(boolean bFlag)
    {
        m_bUseAnimationLine = bFlag;
    }

    /**
     * 애니메이션 라인차트의 사용여부를 확인
     * @return 애니메이션 라인차트 사용여부
     * */
    public boolean isUseAnimationLine()
    {
        return m_bUseAnimationLine;
    }

    public void setAnimationLineChartListener(DrawAnimationLineTimer.drawAnimationLineChartListener aniLineListener)
    {
        this.aniLineListener = aniLineListener;
    }

    public DrawAnimationLineTimer.drawAnimationLineChartListener getAnimationLineChartListener()
    {
        return this.aniLineListener;
    }
    //2015. 7. 7 애니메이션 라인차트<<
    //2015. 8. 9 자산관리 타입 추가>>
//    /**
//     * 자산관리 타입인지 설정 (새 타입 추가시 이 함수 내부에 조건문 추가해야함)
//     * @param strTypeName 자산관리 타입명.
//     * */
//    public void setAssetType(String strTypeName)
//    {
//        m_strAssetType = strTypeName;
//    }

    /**
     * 자산관리 타입여부 반환
     * @return true(자산관리)  false(다른타입)
     * */
//    public String getAssetType()
//    {
//        return m_strAssetType;
//    }
//    //2015. 8. 9 자산관리 타입 추가<<
//    //2012. 11. 9  선차트 gradient 영역 그리기 함수 : C28
//    public void drawLineWithFillGradient(Canvas gl, float positions[], float fHeight, int[] color0, int alpha)
//    {
//        //canvas객체, line의좌표(i, i+1)  이 각각 X,Y에 해당함,  높이, 색상, 알파값
//
//        //데이터갯수가 2보다 작은건 x, y 좌표가 한개도 안왓던 의미이므로 함수를 나감
//        if(positions.length< 2)
//        {
//            return;
//        }
//
//        //line 의 좌표들이 담겨있는 positions 를 Path 객체에 할당.
//        Path mPath = new Path();
//        for(int i=0; i < positions.length; i += 2)
//        {
//            mPath.lineTo(positions[i], positions[i+1]);
//        }
//        //라인좌표를 제외한 나머지 아랫쪽 영역 좌표를 줘서 다각형으로 세팅
//        mPath.lineTo(positions[positions.length-2], fHeight);
//        mPath.lineTo(positions[0], fHeight);
//        mPath.lineTo(positions[0], positions[1]);
//
//        //실제 paint 로 그리는 부분.
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        //첫번째컬러 : 시작색상,  두번째컬러 : 끌색상
////        paint.setShader(new LinearGradient(0,0,0, fHeight/2, Color.rgb(color0[0], color0[1], color0[2]), Color.rgb(color0[0], color0[1], color0[2]), Shader.TileMode.MIRROR));
//        paint.setColor(Color.rgb(color0[0], color0[1], color0[2]));
//        paint.setAlpha(alpha);
//        gl.drawPath(mPath, paint);
//
//        //다른쪽 차트화면의 알파값이나 gradient 를 변경하지 않도록 초기화
//        paint.setAlpha(255);
//    }
//
//    //2014.04.16 by LYH >> 마운틴차트 추가.

    public void drawStringWithSize(Canvas gl, int[] color, int x, int y, float size, String str) {
        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Text.setTextAlign(Align.LEFT);
        mPaint_Text.setTextSize(size);
        gl.drawText(str, x, y+COMUtil.getPixel(4), mPaint_Text);
        mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
    }

//    public void drawScaleStringWithSize(Canvas gl, int[] color, int x, int y, float size, String str) {
//        mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
//        mPaint_Text.setTextAlign(Align.LEFT);
//        mPaint_Text.setTextSize(size);
//
//        float limitWidth = Margin_R - COMUtil.getPixel(2);
//        if ( mPaint_Text.measureText(str, 0, str.length()) > limitWidth ) {
//            mPaint_Text.setTextSize(size - 1);
//        }
//        gl.drawText(str, x, y+COMUtil.getPixel(4), mPaint_Text);
//        mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
//    }

    public void drawBarStringWithSize(Canvas gl, int[] color, int x, int y, float size, String str) {
        mPaint_Bar_Text.setColor(Color.rgb(color[0],color[1],color[2]));
        mPaint_Bar_Text.setTextAlign(Align.LEFT);
        mPaint_Bar_Text.setTextSize(size);
        gl.drawText(str, x, y+COMUtil.getPixel(4), mPaint_Bar_Text);
        mPaint_Bar_Text.setTextSize(COMUtil.nFontSize_paint);
    }
    public void clearBarText() {
        //
        mPaint_Bar_Text.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //
    }
    //2017.11 by PJM >> 자산 차트 적용 end
    //2021.02.18 by HJW - 매입평균선 추가 >>
    public void showLastDataLine(Canvas gl, int xStart, int y, int xEnd, int[] color, boolean bCircle)
    {
        try {
            drawDashDotDotLine(gl, xStart,y,xEnd ,y, color ,1.0f);
            if(bCircle) {
                int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("ic_chart_point", "drawable", COMUtil.apiView.getContext().getPackageName());
                bitmapCircle = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
                //핸들러 흰색 원
                final int _n_Circle_Width = (int) COMUtil.getPixel(21);
                final int _n_Circle_Height = (int) COMUtil.getPixel(21);
                drawImage(gl, xEnd - (_n_Circle_Width / 2), y + +(int)COMUtil.getPixel(10) - (_n_Circle_Height), _n_Circle_Width, _n_Circle_Height, bitmapCircle, 255);

                drawCircle(gl, (int)xEnd - (int)COMUtil.getPixel(2), y - (int)COMUtil.getPixel(2),xEnd +(int)COMUtil.getPixel(2),y+ (int)COMUtil.getPixel(2),true, color);
            }
        } catch (Exception e) {

        }
    }
    //2021.02.18 by HJW - 매입평균선 추가 <<
    public void destroy()
    {
        listeners.removeAllElements();
        if(chartTitle!=null)
            chartTitle.removeAllElements();
        lineByteBuffer.clear();
        rectByteBuffer.clear();
        triByteBuffer.clear();
    }
}

