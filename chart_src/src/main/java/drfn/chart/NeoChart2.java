package drfn.chart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL10;

import drfn.UserProtocol;
import drfn.chart.anal.AAndrewsPiTool;
import drfn.chart.anal.ABadgeTool;
import drfn.chart.anal.ABothLineTool;
import drfn.chart.anal.AChuseLineTool;
import drfn.chart.anal.ACrossLineTool;
import drfn.chart.anal.ACycleLinesTool;
import drfn.chart.anal.ADegreeTool;
import drfn.chart.anal.ADiagonalTool;
import drfn.chart.anal.ADivFTool;
import drfn.chart.anal.ADivTTool;
import drfn.chart.anal.AElliotTool;
import drfn.chart.anal.AFiboTargetTool;
import drfn.chart.anal.AGannFanTool;
import drfn.chart.anal.AGannGridTool;
import drfn.chart.anal.AGannLineTool;
import drfn.chart.anal.AHLineTool;
import drfn.chart.anal.ALeftLineTool;
import drfn.chart.anal.AOvalTool;
import drfn.chart.anal.AParallelTool;
import drfn.chart.anal.APencilLineTool;
import drfn.chart.anal.APeriodReturnLineTool;
import drfn.chart.anal.APerpendicularTool;
import drfn.chart.anal.APivoArcTool;
import drfn.chart.anal.APivoFanTool;
import drfn.chart.anal.APivoRetTool;
import drfn.chart.anal.APivoTimeZoneTool;
import drfn.chart.anal.ARectTool;
import drfn.chart.anal.ARegressionLineTool;
import drfn.chart.anal.ARegressionLinesTool;
import drfn.chart.anal.ARightLineTool;
import drfn.chart.anal.ASpeedArcTool;
import drfn.chart.anal.ASpeedFanTool;
import drfn.chart.anal.ASpeedLineTool;
import drfn.chart.anal.ATargetNtTool;
import drfn.chart.anal.ATextTool;
import drfn.chart.anal.ATradeTool;
import drfn.chart.anal.ATriangleTool;
import drfn.chart.anal.AUpperLowerLimitTool;
import drfn.chart.anal.AVLineTool;
import drfn.chart.anal.AnalTool;
import drfn.chart.base.Base;
import drfn.chart.base.Base11;
import drfn.chart.base.DrawAnimationLineTimer;
import drfn.chart.base.ExViewPanel_LeftRightArrow;
import drfn.chart.base.JipyoListViewByLongTouch;
import drfn.chart.base.ScrollViewPanel;
import drfn.chart.base.ViewPanel;
import drfn.chart.block.Block;
import drfn.chart.block.ChartBlockPopup;
import drfn.chart.comp.AnalToolSettingViewController;
import drfn.chart.comp.TrendDelAlertDialog;
import drfn.chart.draw.DrawTool;
import drfn.chart.draw.SignalDraw;
import drfn.chart.event.ChartChangable;
import drfn.chart.event.ChartChangedListener;
import drfn.chart.event.ChartEvent;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.model.ChartDataModel;
import drfn.chart.model.ChartPacketDataModel;
import drfn.chart.model.ChartRealPacketDataModel;
import drfn.chart.model.ChartViewModel;
import drfn.chart.model.DataChangeEvent;
import drfn.chart.model.DataChangedListener;
import drfn.chart.net.RTHandler;
import drfn.chart.net.RealComp;
import drfn.chart.scale.XScale;
import drfn.chart.scale.YScale;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.CodeItemObj;
import drfn.chart.util.DoublePoint;
import drfn.chart.util.OutputPacket;
import drfn.chart.util.TrData;
import drfn.chart.util.blur.LiveBlurView;
import drfn.chart_src.R;

public class NeoChart2 extends View implements ChartChangable, RealComp,DataChangedListener, OnEditorActionListener, DrawAnimationLineTimer.drawAnimationLineChartListener
{
    protected Base base = null;
    public final int NOTONLY_DATACHANGE = 100000;
    public final int ONLY_DATACHANGE = 100001;
    public final int ONLY_SHOWCHANGE = 100002;
    public final int DATA_END=100003;
    public final int ONLY_DATA_END=100004;
    Vector<ChartChangedListener> listeners;
    public ChartDataModel _cdm;
    public ChartViewModel _cvm;
    private int TP_WIDTH = 0;
    public ScrollViewPanel viewP;//상위 시고저종 패널
    private RelativeLayout shadowlayout;
    private RelativeLayout blurViewlayout;
    private View blurView;
    private HorizontalScrollView indicatorView;
    private LinearLayout scrollViewLinearLayout = null;
    private RelativeLayout.LayoutParams indicatorViewlp = null;
    private FrameLayout.LayoutParams scrollViewLinearLayoutlp = null;
    public Dialog gset;//그래프설정창
    String drawMark="";//삽입할 문자
    private int STYLE=0;//차트의 스타일
    private String currDateType="";
    protected XScale xscale;//가로 스케일
    public Vector<Block> blocks;//블럭
    public Vector<AbstractGraph> unChkGraphs  = new Vector<AbstractGraph>();   //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public Vector<AnalTool> analTools;//분석도구
    public Vector<Block> rotate_blocks=null;
    public Vector<String> title_list;
    public boolean isBlockChanged=false;
    //private Block resize_block, basic_block, anal_block;//changable:삭제및 설정에서 사용,
    public Block basic_block;//changable:삭제및 설정에서 사용,
    private Block resize_block;//changable:삭제및 설정에서 사용,
    private Block[] changable_block = new Block[2];//block 이동시 사용

//	private Bitmap bitmapLine = null;
//	private Bitmap bitmapCircle = null;

    Bitmap bitmapLine;
    Bitmap bitmapCircle;

    private DrawTool select_dt;//현재 선택된 DrawTool
    public AnalTool select_at;//2015. 1. 13 분석툴 수정기능 및 자석기능 추가
//    private Block select_block;//현재 선택된 블럭
//    private AbstractGraph select_graph;//현재 선택된 그래프

    //private Bitmap ibuf,nbuf;
    protected PointF pressPoint, curPoint = new PointF();
    protected PointF prePanPoint = new PointF(); //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경
    private int MousePress_count=0;//마우스 프레스 카운트(앤드류스 피치포크에 사용)
    //private int curr_index;//현재 마우스 인덱스
    //private boolean drawToolbar=false;
    public boolean drawtool_select;
    public boolean analtool_select;
    //private boolean isDraw = true;
    private boolean extent = false;
    int lastVol = 0;
    int nCount = 0;

    boolean dragged=false;
    float currY;
    public int dataAddType = 0;
    public boolean useReal = false;// 실시간 사용여부
    private Vector<String> reqRV;//등록한 실시간 file+key
    /** The user interface thread handler. */
    public Handler mHandler;
    public Handler ExpandReduceChartHandler;   //2013. 2. 13  차트 확대/축소
    public RectF chart_bounds = new RectF();

    //onTouch 관련 변수들.
    boolean analDragState = false;
    boolean touchesMoved=false;
    private boolean m_bSelected = false;
    String funcName = "";
    private boolean m_bShowToolTip = false;

    //2012. 7. 17  변수참조를 위해 public  변경
    public String m_strCandleType = null;//차트형태(캔들/라인 등)

    //2012. 9. 27 비교차트 아래의 버튼 추가
    public Bitmap sprite_Compare  = null;

    //2013. 1.    설정지표리스트 팝업
    public PopupWindow popupJipyoList = null;
    public PopupWindow jipyoListDetailPopup = null;

    //2013. 2. 14  차트 확대/축소
    final int CHART_EXPASION = 0;
    final int CHART_REDUCTION = 1;
    int exReType = CHART_EXPASION;

    //2013. 2. 14 차트확대/축소 - 버튼 애니메이션
    AnimationSet set = new AnimationSet(true);

//	final int INDICATOR_PLINE = 20003;
//	final int INDICATOR_JBONG = 20001;
//	final int INDICATOR_JBONG_TRANSPARENCY = 19999; //2021.04.15 by lyk - kakaopay - 투명 캔들 타입
//	final int INDICATOR_PNF = 20011;
//	final int INDICATOR_SWING = 20019;
//	final int INDICATOR_RENKO = 20020;
//	final int INDICATOR_KAGI = 20023;

    Animation showAnimation = new AlphaAnimation( 0.0f, 1.0f ); // 투명도를 조절. 페이드아웃
    Animation hideAnimation = new AlphaAnimation( 1.0f, 0.0f ); // 투명도를 조절. 페이드아웃

    //2013. 2. 14 차트확대축소 - 더블탭
    private GestureDetector gd;

    //================================
    // 생성자
    //================================
    public RelativeLayout layout;

    /** The buffer holding the vertices */
//	private FloatBuffer vertexBuffer;
    /** The buffer holding the texture coordinates */
//	private FloatBuffer textureBuffer;
    /** The buffer holding the indices */
//	private ByteBuffer indexBuffer;

    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
    private int m_nAccelCount = 0;
    private float m_nDistance = 0;
    private long tm_interpolation = 0;
    private Thread m_flickingThread;
    private PointF m_flickingPoint;
    private VelocityTracker m_velocityTracker;
    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<

    //2012.07.09 by LYH>> rolling 기능
    private int m_nMaxIndicatorCount;
    private int m_nRollIndex;
    //2012.07.09 by LYH<<

    public int nSendMarketIndex = -1;

    protected Vector<String> arrCodes = null;
    protected Vector<String> arrNames = null;
    protected Vector<String> arrMarkets = null;

    public int nDirtyFlag = 0;
    public int nChangeFlag = 0;
    private boolean isChangeViewCnt =false;
    public UserProtocol userProtocol;
    private boolean m_bSendNextData = false;

    public SurfaceHolder mSurfaceHolder=null;
    private Context context = null;

    //double buffering
    private Bitmap backbit=null;
    private Canvas offscreen=null;

    //2012.12.17 by LYH >> 매매내역 표시 기능 추가.
    public Vector<AnalTool> analTradeTools;//
    public ViewPanel tradeViewP;//
    //2012.12.17 by LYH <<

    // badge icon ViewPanel
    public Vector<ABadgeTool> aBadgeTools;
    public Vector<String> eventBadgeDataList;
    public ViewPanel eventBadgeViewP;

    String preMarketTitle="";
    boolean isSendRotateMarket=false;
    private boolean isMaxExpansion = false;
    private boolean exReRepeat = false;

    //2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리 >>
    private boolean bIsAnotherColorWhiteSkin = false;
    //2013. 5. 28 차트영역 외부 바탕색 하얀색 이외의 색으로 처리<<

    //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 >>
    private boolean bChartItemViewHidden = false;
    //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 <<

    //2017.05. 15 해외선물옵션 FX 멀티차트 차트 상단 종목정보 (ChartItemView) 미표시 처리  >>
    private boolean bChartItemViewFXHidden = false;
    //2017.05. 15 해외선물옵션 FX 멀티차트 차트 상단 종목정보 (ChartItemView) 미표시 처리  <<

    public Handler ScrollPagePrevChartHandler;   //2013. 2. 13  차트 확대/축소
    public Handler ScrollPageNextChartHandler;   //2013. 2. 13  차트 확대/축소

    //2015. 2. 24 차트 상하한가 표시
    Vector<AnalTool> analUpperLowerTools;

    private int nNewsEventIndex = -1;

    //2017.07.25 by pjm >> Y축 움직여 확대/축소
    int m_nOrgViewNum=-1;
    int m_nOrgIndex= -1;
    //2017.07.25 by pjm << Y축 움직여 확대/축소

    ImageView iv_tooltip_circle;
    RelativeLayout circleRelative;

    //2019. 01. 12 by hyh - 블록병합 처리 >>
    public static final String MERGED_GRAPH_SEPARATOR = "&&";

    ChartBlockPopup chartBlockPopup;
    Block selectedMoveBlock;
    Block targetedMoveBlock;
    AbstractGraph selectedMoveGraph;
    DrawTool selectedMoveDrawTool;
    String[] moveLockedGraphList = {"주가이동평균", "거래량이동평균", "매물대"};
    //2019. 01. 12 by hyh - 블록병합 처리 <<

    //2019. 04. 17 by hyh - 시세알람차트 개발
    private boolean bIsInitAlarmChart = false;
    private String strInitAlarmPrice = "";
    private String strAlarmValue = "";

    private SharedPreferences m_prefConfig = null;

    //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 Start
    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 End

    //2017.09.25 by LYH >> 자산 차트 적용
    public boolean m_bIsBitmapRefresh = false;	//2015. 7. 8 애니메이션 라인차트
    private boolean m_bBaseLine = false;
    //2017.09.25 by LYH >> 자산 차트 적용 end

    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
    private Runnable flickingRunable = new Runnable() {
        @Override
        public void run() {
            try {
                boolean bIsEnd = false;

                while (m_nAccelCount != 0 && !bIsEnd) {
                    m_flickingPoint.x += m_nAccelCount;
                    bIsEnd = !setMouseDrag_sbMove(m_flickingPoint, false);

                    m_nAccelCount = m_nAccelCount - (m_nAccelCount / 5);

                    if (Math.abs(m_nAccelCount) < 5) {
                        m_nAccelCount = 0;
                    }

                    if (bIsEnd || m_nAccelCount == 0) {
                        break;
                    }

                    postInvalidate();
                    Thread.sleep(20);

//					System.out.println("acceleration : onAcceleration() m_nAccelCount = " + m_nAccelCount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<

    //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 >>
//    Button m_btnViewPanelAlarm;
//    Button m_btnViewPanelClose;
    //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 <<

    public NeoChart2(final Context context, RelativeLayout layout){
        super(context, null);
        this.context = context;
        this.layout = layout;
//    	int layoutResId = this.getContext().getResources().getIdentifier("whiteskin", "drawable", this.getContext().getPackageName());
//    	this.setBackgroundResource(layoutResId);

        if(mHandler==null) {
            mHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    handleLongPress();
                }
            };
        }

        //2013. 2. 13  차트 확대/축소
        if(ExpandReduceChartHandler == null)
        {
            ExpandReduceChartHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    hideExReButtonView();
                }
            };
        }

        if(ScrollPagePrevChartHandler == null)
        {
            ScrollPagePrevChartHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    hideScrollPrevImageView();
                }
            };
        }
        if(ScrollPageNextChartHandler == null)
        {
            ScrollPageNextChartHandler = new Handler() {
                @Override public void handleMessage(Message msg) {
                    hideScrollNextImageView();
                }
            };
        }
        //2012. 2. 14 차트 확대/축소
        set.setInterpolator(new AccelerateInterpolator());

//		showAnimation.setDuration(400);
//    	hideAnimation.setDuration(900);
        showAnimation.setDuration(300);
        hideAnimation.setDuration(400);

        set.addAnimation(showAnimation);
        set.addAnimation(hideAnimation);

        gd = new GestureDetector(context, mNullListener);
        gd.setOnDoubleTapListener(mDoubleTapListener);

//        //translucent
////        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
////		setEGLContextClientVersion(2);
//
//    	//setRenderer(this);
//
//    	this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
////    	this.setZOrderOnTop(true) ;
//
//    	//OpenGl use
//    	// 오류 확인과 로깅을 On (속도 느려짐.)
////        setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
////    	setFocusable(true);
// //       setFocusableInTouchMode(true);
//
//    	mSurfaceHolder = this.getHolder();
//        mSurfaceHolder.addCallback(this);
//
//        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);//SURFACE_TYPE_GPU
//        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//RENDERMODE_CONTINUOUSLY, RENDERMODE_WHEN_DIRTY

        //System.gc();
        _cdm = new ChartDataModel();
//        _cdm.setNeoChart(this);
        _cvm = new ChartViewModel();
        listeners = new Vector<ChartChangedListener>();

        //2017.09.25 by LYH >> 자산 차트 적용
        _cvm.parent = this;
        _cvm.setAnimationLineChartListener(this);	//2015. 7. 7 애니메이션 라인차트
        //2017.09.25 by LYH >> 자산 차트 적용 end

//		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
//		byteBuf.order(ByteOrder.nativeOrder());
//		vertexBuffer = byteBuf.asFloatBuffer();
//		vertexBuffer.put(vertices);
//		vertexBuffer.position(0);
//
//		//
//		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
//		byteBuf.order(ByteOrder.nativeOrder());
//		textureBuffer = byteBuf.asFloatBuffer();
//		textureBuffer.put(texture);
//		textureBuffer.position(0);
//
//		//
//		indexBuffer = ByteBuffer.allocateDirect(indices.length);
//		indexBuffer.put(indices);
//		indexBuffer.position(0);

        //2012.07.09 by LYH>> rolling 기능
        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
            m_nMaxIndicatorCount = 10;
        else
        {
            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                //2023.01.19 by SJW - 가로모드 설정 추가 >>
//				m_nMaxIndicatorCount = 2;
                m_nMaxIndicatorCount = 4;
                //2023.01.19 by SJW - 가로모드 설정 추가 <<
            else
                m_nMaxIndicatorCount = 4;
        }

        m_nRollIndex = 0;
        //2012.07.09 by LYH<<
//		fogColor = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//		fogColor.put(new float[]{1.0f, 1.0f, 1.0f, 1.0f });

        arrCodes = new Vector<String>();
        arrNames = new Vector<String>();
        arrMarkets = new Vector<String>();

        int layoutResId;
        layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("img_handler_line", "drawable", COMUtil.apiView.getContext().getPackageName());
        bitmapLine = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

        layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("img_handler_point", "drawable", COMUtil.apiView.getContext().getPackageName());
        bitmapCircle = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

        //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 Start
        mGestureDetector = new GestureDetector(context, mScrollGestureListener);
        mScroller = new Scroller(context);
        //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 End
    }

    /* Rotation values for all axis */
    int cnt=1;
    GL10 mGl;
/*
	@Override
	//GLSurfaceView 에서 호출됨.

	//2011.08.05 by LYH >> 그리기 동기화
	public void onDrawFrame(final GL10 gl) {
        try{
        	//c = mSurfaceHolder.lockCanvas();
        	synchronized(mSurfaceHolder)
        	{
				draw(gl);
        	}
        }
        finally
        {
//        	if(c!=null)
//        	{
//        		mSurfaceHolder.unlockCanvasAndPost(c);
//        	}
        }

	}
	//2011.08.05 by LYH <<
*/

    //2013. 2. 14 차트 확대/축소  --    더블탭 리스너 추가
    //아무것도 안하는 제스쳐 리스너
    private OnGestureListener mNullListener = new OnGestureListener() {
        @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
        @Override public void onShowPress(MotionEvent e) {}
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
        @Override public void onLongPress(MotionEvent e) {}
        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }
        @Override public boolean onDown(MotionEvent e) { return false; }
    };

    //더블탭 리스너
    private OnDoubleTapListener mDoubleTapListener = new OnDoubleTapListener() {
        @Override public boolean onSingleTapConfirmed(MotionEvent e) { return false; }
        @Override public boolean onDoubleTapEvent(MotionEvent e) { return false; }
        @Override public boolean onDoubleTap(MotionEvent e) {

            if(!_cvm.isStandGraph())		//2014. 3. 27 독립차트일때는 +- 버튼과 좌우화살표 안보이게 하기
            {
                handleChartDoubleTap();
            }
            return true;
        }
    };

    public void onDraw(Canvas g){
//    	System.out.println("onDraw !!!");
        super.onDraw(g);

        drawChart(g);
    }
    boolean isFirstDraw=true;
    public void drawChart(Canvas gl) {
        if (COMUtil._mainFrame == null) return; //2023.05.30 by SJW - 인포윈도우 crash 방어 코드 처리
//		//COMUtil.tf = tf;
        COMUtil.currentFrame = chart_bounds;
//
//		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	//Color, Depth 초기화.
//		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//		gl.glMatrixMode(GL10.GL_MODELVIEW);     //매트릭스 모델뷰로 설정.
//		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
//
////		//Drawing
//		gl.glTranslatef(0.0f, 0.0f, -1.0f);		//Move units into the screen
//		gl.glScalef(1.0f, 1.0f, 1.0f); 			//Scale the Cube to 100 percent, otherwise it would be too large for the screen
//
//		//apiMode로 작동시 처리.
////		if(!COMUtil.apiMode) {
//			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//			gl.glEnable(GL10.GL_TEXTURE_2D);
//		    if(_cvm.getSkinType() == COMUtil.SKIN_BLACK)
//		    {
//				if(textures!=null) {
//					if(selected) {
//						gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
//					} else {
//						gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
//					}
//				}
//
//				//Point to our buffers
//
//				//gl.glEnable(GL10.GL_TEXTURE_2D);
//
//				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
//				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
//
//				//Draw the vertices as triangles, based on the Index Buffer information
//				gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
//				//gl.glDisable(GL10.GL_TEXTURE_2D);
//		    }
//		    gl.glDisable(GL10.GL_TEXTURE_2D);
////		    else {
////		        if(selected)
////		        {
////		            //[COMUtil setLineColor:[UIColor greenColor].CGColor];
////		            //[COMUtil drawRect:chart_bounds.origin.x y:chart_bounds.origin.y w:chart_bounds.size.width-1 h:chart_bounds.size.height-1];
////		            //[COMUtil drawRect:chart_bounds.origin.x y:chart_bounds.origin.y w:chart_bounds.size.width-2 h:chart_bounds.size.height-2];
////		        }
////		    }
//
//
////		}
//
//		//2D draw
//		gl.glViewport(0, 0, chart_bounds.width(), chart_bounds.height());
//		gl.glMatrixMode(GL10.GL_PROJECTION); //직교투영. (OpenGL 의 좌표값을 Quartz의 좌표값으로 변환한다.)
//		gl.glLoadIdentity();
//		gl.glOrthof(0.0f, chart_bounds.width(), chart_bounds.height(), 0.0f, -1.0f, 1.0f);
//		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select Modelview Matrix
//		gl.glLoadIdentity(); // Reset The Matrix

        if(backbit==null) {
            backbit = Bitmap.createBitmap((int)chart_bounds.right,(int)chart_bounds.bottom, Bitmap.Config.ARGB_8888);
            offscreen = new Canvas(backbit);
        }
//

        //애니메이션 라인차트의 Timer 에서 postInvalidate 형식으로 호출한 거면 doublebuffering bitmap 만 다시 그려주기위함
        if(basic_block.getGraphs() == null) return; //2023.04.17 by SJW - fragment 생명주기 관련 수정
        AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
        DrawTool basic_dt = null;
        if(basic_graph.getDrawTool().size()>0) {
            basic_dt = (DrawTool) basic_graph.getDrawTool().elementAt(0);
        }
//        if(m_bIsBitmapRefresh && _cvm.isUseAnimation() && basic_dt.getDrawType1() == 1 && basic_dt.getDrawType2() == 0)
        if(m_bIsBitmapRefresh  && _cvm.isUseAnimationLine() && !touchesMoved)
        {
//        	System.out.println("Debug_NeoChart_m_bIsBitmapRefresh:"+m_bIsBitmapRefresh);
            if(_cvm.m_bWorkingAnimationTimer) {
                drawBuffer(offscreen);
            }

            gl.drawBitmap(backbit,0,0,null);
            return;
        }
        //2016.05.16 by LYH << 1분선차트 애니메이션 추가.

        //십자선 모드일 경우 처리.
        if (touchesMoved) {
            if((m_bShowToolTip || _cvm.isCrosslineMode) && _cvm.chartType != COMUtil.COMPARE_CHART) {
                this.drawTitleData();
            }
            //2013.04.04 by LYH >> 분석툴바 선택시 삭제/전체삭제 subbar 사라짐 개선.
            //2012. 8. 9 분석툴바 선택시 삭제/전체삭제 subbar와  네모칸이 빨리 사라짐  : T55
//            if(analToolSubbar!=null && Math.abs(newDistance) < 8) {
////            	analParams.leftMargin = 0;
////            	analParams.topMargin = 0;
//            	this.showAnalToolSubbar(false, analParams);
//            }
            //2013.04.04 by LYH <<
        } else if(m_bShowToolTip || _cvm.isCrosslineMode) {
            if(COMUtil._neoChart!=null && this.getClass().equals(COMUtil._neoChart.getClass())) {
                this.drawTitleData();
            }
        }

//		if(_cvm.chartType==COMUtil.COMPARE_CHART)
//			drawBaseRect(gl);
        drawBuffer(offscreen);
        drawAnalTool(offscreen);

        //십자선 모드일 경우 처리.
        if (touchesMoved) {
            if(funcName.equals("drawAnalTool_MouseDrag")) {
                this.drawAnalTool_MouseDrag(offscreen);

                //2019. 06. 14 by hyh - 시세알람차트 수치조회창 떠 있을 때 Y축 Drag로 알람값 설정 가능하도록 변경 >>
                if (!_cvm.bIsAlarmChart) {
                    hideViewPanel();
                }
                //2019. 06. 14 by hyh - 시세알람차트 수치조회창 떠 있을 때 Y축 Drag로 알람값 설정 가능하도록 변경 <<
            } else if(funcName.equals("drawAnalTool_MouseMove")) {
                if(m_bShowToolTip || _cvm.isCrosslineMode) {
                    drawAnalTool_MouseMove(offscreen);
                }
            }
        } else if(m_bShowToolTip || _cvm.isCrosslineMode) {
            if(_cvm.chartType==COMUtil.COMPARE_CHART || COMUtil._neoChart!=null && this.getClass().equals(COMUtil._neoChart.getClass())) {
                if(curPoint.x==-1 && curPoint.y==-1) {
                    curPoint = new PointF(this.layout.getWidth()/2, this.layout.getHeight()/2);
                }
                this.drawAnalTool_MouseMove(offscreen);
            }
            //2016.11.29 by LYH >> 조회 데이터 없을 경우 수치 조회 막음
            if(_cdm.getCount()<1)
            {
                if(viewP != null) {
                    hideViewPanel();
                }
            }
            //2016.11.29 by LYH << 조회 데이터 없을 경우 수치 조회 막음 end
            //else if(viewP != null && viewP.getVisibility() == View.GONE)	//2020.06.08 by LYH >> 마운틴 차트 툴팁 개선
            else if(viewP != null && viewP.getVisibility() == View.GONE && _cvm.getAssetType() != ChartViewModel.ASSET_LINE_MOUNTAIN)
            {
                if(!_cvm.bIsHideXYscale)
                    showViewPanel();
                else {
                    if (viewP != null)
                        hideViewPanel();
                }
            }
        }

//        //비교차트의 타이틀 표시
////        if(_cvm.chartType==COMUtil.COMPARE_CHART) {
////        	showChartItemView(gl);
////        }
//		gl.glMatrixMode(GL10.GL_PROJECTION); //직교투영. (OpenGL 의 좌표값을 Quartz의 좌표값으로 변환한다.)
//		gl.glLoadIdentity();
//
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//
//		//차트 저장시 OpenGL의 데이터를 받아서 이미지로 저장한다.
//		if(_cvm.isCaptureMode) {
//			//차트 저장시 타이틀 입력.
//			_cvm.captureImg = SavePixels(0, chart_bounds.top, chart_bounds.width(), chart_bounds.height()-chart_bounds.top, gl);
//			_cvm.isCaptureMode = false;
//		}
//
//		//COMUtil._chartMain.mainFrame.mainBase.setCountText("");//count버튼의 값 설정.
////		COMUtil.drawSpriteText(gl, this.cContext, ""+cnt+" 한글입니다.", 50, 50, Color.RED);
        cnt++;

        //2012. 9. 27   비교차트 아래 버튼 추가
        if(_cvm.chartType==COMUtil.COMPARE_CHART)
        {
            int x = (int)xscale.getDateToX(_cdm.baseLineIndex);
            RectF out_graph_bounds = basic_block.getOutBounds();
            if(x<out_graph_bounds.right) {
                //_cvm.setLineWidth(5);
                _cvm.setLineWidth(1);	//2020.05.26 by LYH >> 두께 조정
                //2013.04.05  >> 고해상도 처리
                //_cvm.drawRect(offscreen, x+5, this.chart_bounds.bottom-COMUtil.getPixel(_cvm.XSCALE_H)+7, COMUtil.getPixel(_cvm.XSCALE_H)-10, COMUtil.getPixel(_cvm.XSCALE_H)-12, CoSys.GRAY);
                _cvm.drawRect(offscreen, x + 5, this.chart_bounds.bottom - _cvm.XSCALE_H + 7, _cvm.XSCALE_H - 10, _cvm.XSCALE_H - 12, CoSys.GRAY);
                //2013.04.05 >> 고해상도 처리
                _cvm.setLineWidth(1);
                if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
                    _cvm.drawLine(offscreen, x, chart_bounds.top, x, chart_bounds.bottom, CoSys.crossline_colDarkSkin, 1.0f);
                } else {
                    _cvm.drawLine(offscreen, x, chart_bounds.top, x, chart_bounds.bottom, CoSys.crossline_col, 1.0f);
                }
            }
        }

        //2016. 1. 14 뉴스차트 핸들러>>
        if(_cvm.bIsNewsChart)
        {
//			//십자선라인 무조건 표시
////			Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
////			base11.showCrossLine(true);
//			//2016.08.23 뉴스차트에서 Crossline 계속 보여주지 않음.
//			_cvm.isCrosslineMode = true;
//
//			//showViewPanel();
//			int idx = getXToDate(curPoint.x);
            int x = (int)xscale.getDateToX(_cvm.curIndex);
            showVertLineWithCircle(offscreen, x, chart_bounds.bottom, _cvm.getBounds(), viewDatas);
        }
        //2016. 1. 14 뉴스차트 핸들러<<
        gl.drawBitmap(backbit,0,0,null);
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        int measuredHeight = measureHeight(hMeasureSpec);
        int measuredWidth = measureWidth(hMeasureSpec);

        // setMesasuredDimension을 반드시 호출해야만 한다.
        // 그렇지 않으면 컨트롤이 배치될 때
        // 런타임 예외가 발생할 것이다.

        setMeasuredDimension(measuredHeight, measuredWidth);
    }

    private int measureHeight(int measureSpec) {
//		int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // 뷰의 높이를 계산한다.
        return specSize;
    }

    private int measureWidth(int measureSpec) {
//		int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // 뷰의 폭을 계산한다.

        return specSize;
    }

    //================================
    // 차트를 초기화 한다
    // 1. 차트를 등록
    // 2. 스크롤바 생성
    // 3. 툴바 생성
    // 4. 차트 뷰모델
    //================================
    public void init(){
        _cdm.addDataChangedListener(this);

        xscale = new XScale(_cdm, _cvm);
        xscale.setProperties("자료일자", ". ", ":");

        indicatorView = new HorizontalScrollView(context);
        ViewCompat.setNestedScrollingEnabled(indicatorView, true);
    }

    public void startProcessing() {
//        if(inquiry!=null) {
//            inquiry.setVisible(true);
//        }
    }
    public void stopProcessing() {
//        if(inquiry!=null) {
//            inquiry.setVisible(false);
//        }
    }
    //================================
    // 차트의 속성을 설정
    //================================
    public void setAllProperties(){
//        _cvm.average_title=COMUtil.getChartSetUI().average_title;
//        _cvm.average_state=COMUtil.getChartSetUI().average_state;
//
//        _cvm.setViewNum_org(COMUtil.getChartSetUI().VIEW_NUM_ORG);
//        _cvm.setOnePage(COMUtil.getChartSetUI().isOnePage?1:0);
//        _cvm.setVolDrawType(COMUtil.getChartSetUI().vol_draw_type);
//        setStyle(COMUtil.getChartSetUI().chart_style);
//        if(STYLE == 4){
//            setBackColor(COMUtil.getChartSetUI().userBackColor);
//            _cvm.setScaleLineType(COMUtil.getChartSetUI().scaleLineType);
//        }
        //"S31","PRICE","005930"
        resetUseRealData();
    }

    //실시간 패킷을 변경한다.
    public void resetUseRealData() {
        String lcode = "";

        lcode = _cdm.codeItem.strRealKey;
        if(lcode.equals("")) lcode = COMUtil.lcode; //realKey.

        String[] realInfo = {lcode,"date","",lcode,"open","",lcode,"high","",lcode,"low","",lcode,"close","",lcode,"volume",""};
        setUseRealData(realInfo);
    }
    //================================
    // title의 지표를 포함하는 블럭을 리턴
    //================================
    public Block makeBlock(int c, int l,String title){
        Block block = new Block(this, _cvm,_cdm,c,l,title);
        block.add(title);
        if(title.equals("가격차트")){
            m_strCandleType = null;
            block.setBlockType(Block.BASIC_BLOCK);
            basic_block = block;
        }
        return block;
    }
    //================================
    // c열 l행의 블럭을 리턴
    //================================
    public Block makeBlock(int c, int l){
        Block block = new Block(this, _cvm,_cdm,c,l,"");
        basic_block = block;
        return block;
    }
    //================================
    // 차트의 배경색을 설정
    //================================
    public void setBackColor(int[] back){
        _cvm.setBackColor(back);
//        this.setBackgroundColor(CoSys.CHART_BACK_COLOR[0]);
    }
    public void setBounds(int left, int top, int right, int bottom){
        chart_bounds = new RectF(left, top, right, bottom); //left, top, right, bottom

        //2016. 08. 30 by hyh - 분할 시 분할영역을 재활용 하는 경우. 더블버퍼링 그리는 영역은 갱신되고, 레이아웃은 갱신되지 않아서 그려지는 영역이 올바르지 않은 에러 수정
        LayoutParams params = getLayoutParams();
        params.width=right;
        params.height=bottom;
        this.setLayoutParams(params);

        //double buffering
        if(backbit!=null)
        {
            backbit.recycle();
            backbit = null;
        }

        if(offscreen!=null)
            offscreen = null;

        backbit = Bitmap.createBitmap((int)chart_bounds.right,(int)chart_bounds.bottom, Bitmap.Config.ARGB_8888);
        offscreen = new Canvas(backbit);

        //2011.08.05 by LYH >> 차트별 높이 세팅 <<
        _cvm.nChartHeight = bottom;
//    	_cvm.nChartWidth = right;
//    	mSurfaceHolder.setFixedSize(chart_bounds.width(), chart_bounds.height());
//    	Surface surface = mSurfaceHolder.getSurface();
//    	surface.setSize(chart_bounds.width(), chart_bounds.height());
//    	mSurfaceHolder.setKeepScreenOn(true);
        this.showViewPanel();
        m_bShowToolTip = false;

        if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
        {
            if(chart_bounds.width()<COMUtil.getPixel(170))
            {
                _cvm.setViewNum(20);
            }
        }
        reSetUI(true);
    }
    public void setBounds2(float left, float top, float right, float bottom){
        boolean bResizeWidth = true;
        if(right == 0 || bottom == 0)
            return;
        if(chart_bounds != null && chart_bounds.right == right)
            bResizeWidth = false;

        chart_bounds = new RectF(left, top, right, bottom); //left, top, right, bottom

        //2016. 08. 30 by hyh - 분할 시 분할영역을 재활용 하는 경우. 더블버퍼링 그리는 영역은 갱신되고, 레이아웃은 갱신되지 않아서 그려지는 영역이 올바르지 않은 에러 수정
        LayoutParams params = getLayoutParams();
        params.width=(int)right;
        params.height=(int)bottom;
        this.setLayoutParams(params);

        //double buffering
        if(backbit!=null)
        {
            backbit.recycle();
            backbit = null;
        }

        if(offscreen!=null)
            offscreen = null;

        backbit = Bitmap.createBitmap((int)chart_bounds.right,(int)chart_bounds.bottom, Bitmap.Config.ARGB_8888);
        offscreen = new Canvas(backbit);

        //2011.08.05 by LYH >> 차트별 높이 세팅 <<
        _cvm.nChartHeight = (int)bottom;
//    	_cvm.nChartWidth = right;
//    	mSurfaceHolder.setFixedSize(chart_bounds.width(), chart_bounds.height());
//    	Surface surface = mSurfaceHolder.getSurface();
//    	surface.setSize(chart_bounds.width(), chart_bounds.height());
//    	mSurfaceHolder.setKeepScreenOn(true);
        //this.showViewPanel();

        if(bResizeWidth && !COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
        {
            if(_cdm.getCount()<1)
            {
                if(chart_bounds.width()<COMUtil.getPixel(170))
                {
                    _cvm.setViewNum(20);
                }
                //2013. 11. 26 주식현재가 세로->가로화면 넘어갈 때 가로모드에서 뷰갯수 40개 초기화되는 현상 >>
//	            else if(chart_bounds.width() != 0)
//	            {
//	            	_cvm.setViewNum(40);
//	            }
                //2013. 11. 26 주식현재가 세로->가로화면 넘어갈 때 가로모드에서 뷰갯수 40개 초기화되는 현상 <<
            }
        }
        reSetUI(true);
        if(this.getVisibility() == View.GONE)
        {
            setVisibleBlockDelButton(false);
        }

        initScrollImageView(true);
    }
    public int getDateType(){
        return _cdm.getDateType();
    }
    //=====================================
    //ChartDataModel에 새로 그릴 데이터의 타입과 포맷을 셋
    //=====================================
    public void setDataType(int type, int format){
        _cdm.setDateType(type);
        _cdm.setDateFormat(format);
    }
    //================================
    // 차트의 시간타입을 설정
    // 관련 메쏘드
    // setRealTerm,setDataType_MIN,setDataType_DAY,setDataType_TIC,setDataType_Text
    //================================
    private void setRealTerm(String dateType){
        currDateType=dateType;
        if(dateType.length()<1)return;
        String unit= new String(dateType.substring(dateType.length()-1,dateType.length()));
        int term=0;
        try{
            if(unit.equals("분")){
                term=Integer.parseInt(new String(dateType.substring(0,dateType.length()-1)))*60000;
            }else if(unit.equals("틱")||unit.equals("초")){
                term=Integer.parseInt(new String(dateType.substring(0,dateType.length()-1)));
            }
        }catch(NumberFormatException e){}
        _cdm.setRealAddTerm(term);
    }
    public void setDataType_MIN(String dateType){
        setRealTerm(dateType);
        setDataType(ChartDataModel.DATA_MIN,XScale.HHMMSS);
    }
    public void setDataType_MIN(String dateType,String format){
        setRealTerm(dateType);
        if(format.equals("MMDDHHMM"))setDataType(ChartDataModel.DATA_MIN, XScale.MMDDHHMM);
        else if(format.equals("HHMMSS"))setDataType(ChartDataModel.DATA_MIN, XScale.HHMMSS);
        else if(format.equals("DDHHMMSS"))setDataType(ChartDataModel.DATA_MIN, XScale.DDHHMMSS);
    }
    public void setDataType_DAY(String dateType){
        setRealTerm(dateType);
        if(dateType.indexOf("일")!=-1){
            setDataType(ChartDataModel.DATA_DAY,XScale.YYYYMMDD);
        }else if(dateType.indexOf("주")!=-1){
            setDataType(ChartDataModel.DATA_WEEK,XScale.YYYYMMDD);
        }else if(dateType.indexOf("월")!=-1){
            setDataType(ChartDataModel.DATA_MONTH,XScale.YYYYMMDD);
        }else if(dateType.indexOf("년")!=-1){
            setDataType(ChartDataModel.DATA_YEAR,XScale.YYYYMMDD);
        }
    }
    public void setDataType_DAY(String dateType, String format){
        setRealTerm(dateType);
        if(dateType.indexOf("일")!=-1){
            if(format.equals("YYYYMMDD"))setDataType(ChartDataModel.DATA_DAY,XScale.YYYYMMDD);
            else if(format.equals("YYMMDD"))setDataType(ChartDataModel.DATA_DAY,XScale.YYMMDD);
        }else if(dateType.indexOf("주")!=-1){
            if(format.equals("YYYYMMDD"))setDataType(ChartDataModel.DATA_WEEK,XScale.YYYYMMDD);
            else if(format.equals("YYMMDD"))setDataType(ChartDataModel.DATA_WEEK,XScale.YYMMDD);
        }else if(dateType.indexOf("월")!=-1){
            if(format.equals("YYYYMMDD"))setDataType(ChartDataModel.DATA_MONTH,XScale.YYYYMMDD);
            else if(format.equals("YYMMDD"))setDataType(ChartDataModel.DATA_MONTH,XScale.YYMMDD);
        }else if(dateType.indexOf("년")!=-1){
            if(format.equals("YYYYMMDD"))setDataType(ChartDataModel.DATA_YEAR,XScale.YYYYMMDD);
            else if(format.equals("YYMMDD"))setDataType(ChartDataModel.DATA_YEAR,XScale.YYMMDD);
        }
    }
    public void setDataType_TIC(String dateType){
        setRealTerm(dateType);
        setDataType(ChartDataModel.DATA_TIC,XScale.HHMMSS);
    }
    public void setDataType_Text(){
        setDataType(ChartDataModel.DATA_TEXT,XScale.TEXT);
    }
    //================================
    public void setLastVol(int vol){
        lastVol = vol;
    }
    //=====================================
    // 현재 그리고 있는 차트의 시간타입을 리턴 (예)30초, 1분, 일
    //=====================================
    public String getCurrDateType_fromInput(){
        return currDateType;
    }

    //================================
    // 스크롤바를 셋
    //================================
    public void setScrollBar(int type){
        int bindex= _cvm.getIndex();
        int max = _cdm.getCount();
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
        if(_cvm.futureMargin>0)
            max = _cdm.getCount() + _cvm.futureMargin - 1;
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        int index =(bindex>max||bindex<0)?max:bindex;
        int view;
        switch(type){
            case NOTONLY_DATACHANGE://데이터가 모두 바뀌는 경우(데이터 크기로 set하고 인덱스는 마지막 데이터
            {
                if(!COMUtil.doubleTouchToggle && (_cvm.preViewNum!=-1)) {
                    view = _cvm.preViewNum;
                    _cvm.preViewNum = -1;
                } else {
                    view= _cvm.getViewNum();
                }
                if(view==0) view = _cvm.VIEW_NUM_ORG;
                if(view>max) view = max;
                _cvm.setViewNum(view);
                int nIndex = max-view;
                if(nIndex>=max)
                    nIndex = max -view;
                if(nIndex<0)
                    nIndex = 0;
                _cvm.setIndex(nIndex);
            }
            break;
            case ONLY_DATACHANGE://데이터만 바뀌고 스크롤인덱스는 고정
            {
                view=_cvm.getViewNum();
                if(index < max-view)index++;
                int nIndex = index-_cdm.getMargin();
                if(nIndex<0)
                    nIndex = 0;
                _cvm.setIndex(nIndex);
                //this.curr_index = index;
            }
            break;
            case ONLY_SHOWCHANGE://확대/축소처럼 보이기만 바뀌는 경우
            {
                view=_cvm.getViewNum();
                if(view>max) {
                    view=max;
                }
                int nIndex = index+nCount;
                if(nIndex>=max)
                    nIndex = max -view;
                if(nIndex<0)
                    nIndex = 0;
                if(nIndex+view>max)
                    nIndex = max-view;
                _cvm.setIndex(nIndex);
            }
            break;
            case DATA_END://한화면 데이터처럼 보이도록
                if(_cvm.preViewNum<0)
                    _cvm.preViewNum = _cvm.getViewNum();
                _cvm.setViewNum(max);
                _cvm.setIndex(0);
                break;
        }
        //       System.out.println(_cvm.getIndex() +"$$"+_cvm.getViewNum() + "$$"+_cdm.getCount());
    }
    //=====================================
    // 데이터 셋
    // 1.분석도구 지우기
    // 2.스크롤바 셋
    // 3.각 그래프 계산
    // 4.자료조회 리스트 변환
    // 5.xscale 데이터 변환
    //=====================================
    private TrData trData = new TrData();
    public void setData(byte[] data) {
        synchronized (this) {
//    		String tempStr = new String(data, 0, data.length);
//    		System.out.println("tempStr:"+tempStr);

            //비교차트 데이터 분기점.
            if(_cvm.chartType==COMUtil.COMPARE_CHART) {
                setCompareData(data);
                COMUtil.isProcessData=false;
                return;
            }
            if(data==null || data.length<20){
                COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
                COMUtil.isProcessData=false;
                COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
                COMUtil.nkey="";
                return;
            }

            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
            if (_cvm.nFxMarginType == ChartViewModel.FX_BUYSELL || _cvm.nFxMarginType == ChartViewModel.FX_AVERAGE) {
                m_strCandleType = "라인";
            }
            else {
                m_strCandleType = COMUtil.selectedJipyo;
            }
            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

            //차트형태 설정.
//	        if(m_strCandleType==null) {
//	        	if(COMUtil.selectedJipyo==null) {
            if(m_strCandleType == null || m_strCandleType.length()<1)
                m_strCandleType = "캔들";
//	        	} else {
//	        		m_strCandleType = COMUtil.selectedJipyo;
//	        	}
//	        }

            Hashtable<String, String> items = trData.makeTrData(COMUtil.apCode, data);
            //_cdm.codeItem = items;
            if(COMUtil.lcode==null)
                COMUtil.lcode = "S31";
            _cdm.codeItem.strRealKey =COMUtil.lcode;
            _cdm.codeItem.strCode = COMUtil.symbol;
//	        _cdm.codeItem.strDataType = COMUtil.dataTypeName;
//	        _cdm.codeItem.strUnit = COMUtil.unit;

            int offset = 0;
            String codeName = "";
            String change = "";
            String chgrate = "";
            nCount = 0;
            int nMsgCodeLen = 0;

            if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) {
                try {

                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);

                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);

                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_CHART_UPJONG)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    return;
                }
            }

            if(nMsgCodeLen < 0){
                //if(useReal)
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }


            String str = "", key, value;
            //        offset += 4;

            //      	str = new String(data, offset, nMsgCodeLen);
            str = items.get(OutputPacket.BOJOMSG);
            StringTokenizer st = new StringTokenizer(str, "@");
            //보조메세지 분석
            //        _cdm.setPrevData("0");
            while (st.hasMoreTokens()) {
                str = st.nextToken();
                int nIndex = str.indexOf('=');
                if(nIndex!=-1){
                    key = new String(str.substring(0, nIndex));
                    value = new String(str.substring(nIndex+1));
                    processTheCmdMsg(key, value);
                }
            }

//	        if(_cdm.getDateType() == 5 && _cdm.getTerm() == 1) {
//	        	this.changeBlock_NotRepaint("라인");
//	        } else {
//	        	this.changeBlock_NotRepaint(m_strCandleType);
//	        }
            //UD=0:notype 1:일 2:주 3:월 4:분 5:틱 6:Text 8:년
            //0,1,2,3,4,5(틱,분,일,주,월,년)
            int nDataTypeName[] = {2,3,4,1,0,2,6,5};

            if(_cdm.getDataType()>0 && _cdm.getDataType()<9)
                _cdm.codeItem.strDataType = ""+nDataTypeName[_cdm.getDataType()-1];
            _cdm.codeItem.strUnit = ""+_cdm.getTerm();

            COMUtil.dataTypeName = _cdm.codeItem.strDataType;
            COMUtil.unit = _cdm.codeItem.strUnit;

            //YSCale에서 사용할 가격 등락폭, 등락률 설정.
            _cdm.codeItem.strChange = COMUtil.format(change, 0, 3);
            _cdm.codeItem.strChgrate = COMUtil.format(chgrate, 2, 3)+"%";

            _cdm.codeItem.strGiPreOpen = "";
            _cdm.codeItem.strGiPreHigh = "";
            _cdm.codeItem.strGiPreLow = "";
            _cdm.codeItem.strGiPreClose = "";

            if(dataAddType==1) _cdm.initAppendData(nCount);
            else{
                deregRT();
                DataClear();
                _cdm.initData(nCount);
            }
            if(nCount==0){
                if(useReal)regRT();
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }
            byte[] d = new byte[data.length-(offset+nMsgCodeLen)];
            try{
                System.arraycopy(data,offset+nMsgCodeLen,d, 0, d.length);
            }catch(ArrayIndexOutOfBoundsException e){

            }
            if(useReal)regRT();
	        /*if(_cvm.isOnePage()||nCount>400){
	            _cdm.setMaxData(nCount);
	        }
	        else{
	            _cdm.setMaxData(400);
	        }*/

            _cdm.setData(nCount,d);

            //추가요청 데이터의 경우 분석툴을 초기화하지 않고, 새로 조회할 경우에만 초기화한다.
            if(!COMUtil.getSendTrType().equals("requestAddData")) {
                removeAllAnalTool();
                addStrageAnalTool();
            }
            //k delete
            _cdm.codeItem.strName = COMUtil.removeString(_cdm.codeItem.strName, "ⓚ");
            codeName = _cdm.codeItem.strName;
            COMUtil.codeName = codeName;
            COMUtil.preSymbol = COMUtil.symbol;
            if(!COMUtil.getSendTrType().equals("requestAddData"))
                COMUtil.getMainBase().setCodeName(codeName.trim()); //종목명설정.
            COMUtil.getMainBase().setPeriodName("");
//	        if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) { //주식코드만 저장.
//	        	saveHistoryCode();
//	        }
            //조회 count 저장.
            COMUtil.count = ""+_cdm.getCount();
            if(dataAddType==1){
                _cdm.resetCnt();
                setScrollBar(ONLY_SHOWCHANGE);
            } else {
                if(_cvm.isStandGraph() || _cvm.isOnePage()){
                    setScrollBar(this.DATA_END);
                }else{
                    setScrollBar(NOTONLY_DATACHANGE);
                }
            }

            try{
                makeGraphData();
                //            if(dataList!=null && dataList.isVisible())
                //                dataList.setData(_cdm);
            }catch(ArrayIndexOutOfBoundsException e){
            }
            xscale.dataChanged();
            COMUtil.getMainBase().setCountText("");
            repaintAll();
            COMUtil.isProcessData=false;

            if(_cvm.chartType!=COMUtil.COMPARE_CHART) {
                if(blocks != null) {
                    for (int i=0; i<blocks.size(); i++) {
                        Block block = blocks.get(i);
                        if(COMUtil.isMarketIndicator(block.getTitle())) {
                            nSendMarketIndex = i;
                            Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                            base11.sendTR(block.getTitle());
                            break;
                        }
                    }
                }

                if(rotate_blocks !=null && rotate_blocks.size()>0) {
                    for(int i=0; i<rotate_blocks.size(); i++) {
                        Block block = rotate_blocks.get(i);

                        //2019. 01. 12 by hyh - 블록병합 처리. 시장지표 데이터 조회 >>
                        String strGraphName = "";
                        for (AbstractGraph ag : block.getGraphs()) {
                            strGraphName = ag.getName();
                            if (COMUtil.isMarketIndicator(block.getTitle())) {
                                nSendMarketIndex = i;
                                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                                base11.sendTR(block.getTitle());
                                break;
                            }
                        }

                        if (!strGraphName.equals("")) {
                            break;
                        }

                        //if(COMUtil.isMarketIndicator(block.getTitle())) {
                        //	nSendMarketIndex = i;
                        //	Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                        //	base11.sendTR(block.getTitle());
                        //	break;
                        //}
                        //2019. 01. 12 by hyh - 블록병합 처리. 시장지표 데이터 조회 <<
                    }
                }
            }

        }
    }
    public void setData_Gijun(Hashtable<String, String> datas) {
        if(_cdm != null) {
//    		System.out.println("DEBUG_setData_Gijun_NeoChart");
            _cdm.codeItem.strGiOpen =datas.get("giOpen");
            _cdm.codeItem.strGiHigh =datas.get("giHigh");
            _cdm.codeItem.strGiLow =datas.get("giLow");
            _cdm.codeItem.strPrice =datas.get("giClose");
        }

    }
    public void setData_header(byte[] data, String strCandleType) {
        synchronized (this) {
//    		String tempStr = new String(data, 0, data.length);
//    		System.out.println("DEBUG_tempStr:"+tempStr);

            //비교차트 데이터 분기점.
            if(_cvm.chartType==COMUtil.COMPARE_CHART) {
                setCompareData_header(data);
                COMUtil.isProcessData=false;
                return;
            }
            if(data==null || data.length<20){
                COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
                COMUtil.isProcessData=false;
                COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
                COMUtil.nkey="";
                return;
            }

            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
            if (_cvm.nFxMarginType == ChartViewModel.FX_BUYSELL || _cvm.nFxMarginType == ChartViewModel.FX_AVERAGE) {
                m_strCandleType = "라인";
            }
            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

            //차트형태 설정.
//	        if(m_strCandleType==null) {
//	        	if(COMUtil.selectedJipyo==null) {
            if(m_strCandleType == null || m_strCandleType.length()<1)
                m_strCandleType = "캔들";
//	        	} else {
//	        		m_strCandleType = COMUtil.selectedJipyo;
//	        	}
//	        }

            Hashtable<String, String> items = trData.makeTrData(COMUtil.apCode, data);
            //_cdm.codeItem = items;
            if(COMUtil.lcode==null)
                COMUtil.lcode = "S31";
            _cdm.codeItem.strRealKey =COMUtil.lcode;
            _cdm.codeItem.strCode = COMUtil.symbol;
//	        _cdm.codeItem.strDataType = COMUtil.dataTypeName;
//	        _cdm.codeItem.strUnit = COMUtil.unit;

//	        int offset = 0;
//	        String codeName = "";
            String change = "";
            String chgrate = "";
            nCount = 0;
            int nMsgCodeLen = 0;

            if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK) || COMUtil.apCode.equals(COMUtil.TR_CHART_INVESTOR)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME);
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE);
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME);
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY);
                    _cdm.codeItem.strGiOpen = items.get(OutputPacket.OPEN);
                    _cdm.codeItem.strGiHigh = items.get(OutputPacket.HIGH);
                    _cdm.codeItem.strGiLow = items.get(OutputPacket.LOW);
                    _cdm.codeItem.strRealCode = items.get(OutputPacket.PREVOL).trim();    //2014.04.17 by LYH >> 연결선물 실시간 처리.

//		        	_cdm.codeItem.S1386 = items.get(OutputPacket.S1386 );
//		        	_cdm.codeItem.S1339 = items.get(OutputPacket.S1339 );
//		        	_cdm.codeItem.S1522 = items.get(OutputPacket.S1522 );
//
//		        	//2013. 2. 12 상하한가바 추가
//		        	_cdm.codeItem.strHighest = items.get(OutputPacket.HIGHEST);
//		        	_cdm.codeItem.strLowest = items.get(OutputPacket.LOWEST);
//		        	_cdm.codeItem.strGijun = items.get(OutputPacket.GIJUN);
//
//		        	_cdm.codeItem.strPreVolume = items.get(OutputPacket.PREVOLUME );

                    change = items.get(OutputPacket.CHANGE);
//		        	String sign = items.get(OutputPacket.SIGN);
//		        	if(Integer.parseInt(sign)>3){
//		        		change = "-"+change;
//		        	}
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
//		        	System.out.println("DEBUG_neochart_nkey:"+items.get(OutputPacket.NKEY));
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);

                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);

                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
//		        	System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );
                    _cdm.codeItem.strGiOpen = items.get(OutputPacket.OPEN );
                    _cdm.codeItem.strGiHigh = items.get(OutputPacket.HIGH );
                    _cdm.codeItem.strGiLow = items.get(OutputPacket.LOW );
                    _cdm.codeItem.strRealCode = items.get(OutputPacket.PREVOL ).trim();	//2014.04.17 by LYH >> 연결선물 실시간 처리.

//		        	_cdm.codeItem.S1386 = items.get(OutputPacket.S1386 );
//		        	_cdm.codeItem.S1339 = items.get(OutputPacket.S1339 );
//		        	_cdm.codeItem.S1522 = items.get(OutputPacket.S1522 );
//
//		        	_cdm.codeItem.strPreVolume = items.get(OutputPacket.PREVOLUME );

                    change = items.get(OutputPacket.CHANGE);
//		        	String sign = items.get(OutputPacket.SIGN);
//		        	if(Integer.parseInt(sign)>3){
//		        		change = "-"+change;
//		        	}
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_CHART_UPJONG)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );
                    _cdm.codeItem.strGiOpen = items.get(OutputPacket.OPEN );
                    _cdm.codeItem.strGiHigh = items.get(OutputPacket.HIGH );
                    _cdm.codeItem.strGiLow = items.get(OutputPacket.LOW );

                    change = items.get(OutputPacket.CHANGE);
//		        	String sign = items.get(OutputPacket.SIGN);
//		        	if(Integer.parseInt(sign)>3){
//		        		change = "-"+change;
//		        	}
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    return;
                }
            }
//			if(_cdm.codeItem.strPrice.startsWith("-") || _cdm.codeItem.strPrice.startsWith("+"))
//				_cdm.codeItem.strPrice = _cdm.codeItem.strPrice.substring(1);

            _cdm.codeItem.dLastVol = -1;
            if(nMsgCodeLen < 0){
                //if(useReal)
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }

            _cdm.setOpenTime("");	//2014.05.23 by LYH >> 날짜 구분선 시작 시간 포함.
            String str = "", key, value;
            //        offset += 4;

            //      	str = new String(data, offset, nMsgCodeLen);
            str = items.get(OutputPacket.BOJOMSG);
            StringTokenizer st = new StringTokenizer(str, "@");
            //보조메세지 분석
            //        _cdm.setPrevData("0");
            while (st.hasMoreTokens()) {
                str = st.nextToken();
                int nIndex = str.indexOf('=');
                if(nIndex!=-1){
                    key = new String(str.substring(0, nIndex));
                    value = new String(str.substring(nIndex+1));
                    processTheCmdMsg(key, value);
                }
            }

            if(basic_block != null) {
                for(int j=0; j<basic_block.getGraphs().size(); j++) {
                    AbstractGraph graph = (AbstractGraph)basic_block.getGraphs().get(j);
                    if(graph.graphTitle.equals("일본식봉")) {
                        DrawTool basic_dt=(DrawTool)graph.getDrawTool().get(0);
                        //2020.06.15 수정주가일때도 타이틀 "가격"으로 표기 (하나금융투자 요청) >>
//						if(COMUtil.isAdjustedStock() && (_cdm.codeItem.strMarket.equals("0") || _cdm.codeItem.strMarket.equals("7"))
//								&& (_cdm.getDateType() == ChartDataModel.DATA_DAY || _cdm.getDateType() == ChartDataModel.DATA_WEEK ||_cdm.getDateType() == ChartDataModel.DATA_MONTH))
//							basic_dt.subTitle = "가격(수정)";
//						else
                        //2020.06.15 수정주가일때도 타이틀 "가격"으로 표기 (하나금융투자 요청) <<
                        basic_dt.subTitle = "가격";
                        basic_block.resetTitleBounds();
                        //2017.08.22 by pjm >> 타이틀 영역 수정
                        if(basic_block.isBasicBlock())
                        {
                            basic_block.setBounds_Pivot(false);
                        }
                        //2017.08.22 by pjm << 타이틀 영역 수정
                        break;
                    }
//					if(graph.graphTitle.equals("일본식봉")) {
//						DrawTool basic_dt=(DrawTool)graph.getDrawTool().get(0);
//						//2019. 06. 25 - by hyh - 만기보정 타이틀 제거 >>
//                        //if (COMUtil.isUseMod() && (_cdm.codeItem.strMarket.equals("0") || _cdm.codeItem.strMarket.equals("7"))
//                        //        && (_cdm.getDateType() == ChartDataModel.DATA_DAY || _cdm.getDateType() == ChartDataModel.DATA_WEEK || _cdm.getDateType() == ChartDataModel.DATA_MONTH)) {
//                        //    if (!COMUtil.bIsForeignFuture) {
//                        //        basic_dt.subTitle = "가격(만기보정)";
//                        //    }
//                        //}
//                        //else
//						//2019. 06. 25 - by hyh - 만기보정 타이틀 제거 <<
//						{
//							//2014.04.17 by LYH >> 연결선물 타이틀 처리.
//							if (_cdm.codeItem.strRealCode.trim().length() > 0 && !_cdm.codeItem.strRealCode.trim().equals("0")) {
//								basic_dt.subTitle = _cdm.codeItem.strRealCode + "(연결)";
//							}
//							else {
//								basic_dt.subTitle = "가격";
//							}
//							//2014.04.17 by LYH << 연결선물 타이틀 처리.
//                        }
//
//						//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
//						if(_cvm.nFxMarginType == ChartViewModel.FX_BUYSELL) {
//							basic_dt.subTitle = "매도";
//						}
//						//2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<
//
//						basic_block.resetTitleBounds();
//						//2017.08.22 by pjm >> 타이틀 영역 수정
//						if(basic_block.isBasicBlock())
//						{
//							basic_block.setBounds_Pivot(false);
//						}
//						//2017.08.22 by pjm << 타이틀 영역 수정
//						break;
//					}
                    else if(graph.graphTitle.equals("Heikin-Ashi")) {
                        DrawTool basic_dt=(DrawTool)graph.getDrawTool().get(0);
                        if(COMUtil.isAdjustedStock() && (_cdm.codeItem.strMarket.equals("0") || _cdm.codeItem.strMarket.equals("7"))
                                && (_cdm.getDateType() == ChartDataModel.DATA_DAY || _cdm.getDateType() == ChartDataModel.DATA_WEEK ||_cdm.getDateType() == ChartDataModel.DATA_MONTH))
                            basic_dt.subTitle = "Heikin-Ashi(수정)";
                        else
                            basic_dt.subTitle = "Heikin-Ashi";
                        basic_block.resetTitleBounds();
                        break;
                    }
                }
                //2023.11.28 by SJW - 일목균형표 선행영역 yscale 침범하던 현상 수정 >>
                for (int i=0; i<basic_block.getGraphs().size(); i++) {
                    AbstractGraph graph = (AbstractGraph)basic_block.getGraphs().get(i);
                    if (graph.getGraphTitle().equals("일목균형표")) {
                        _cvm.futureMargin = graph.interval[2];
                    }
                }
                //2023.11.28 by SJW - 일목균형표 선행영역 yscale 침범하던 현상 수정 <<
            }

            if(!_cvm.bRateCompare && !_cvm.bInvestorChart)
            {
                if(_cdm.getDateType() == 5 && _cdm.getTerm() == 1) {
                    this.changeBlock_NotRepaint("라인");
                } else {
                    this.changeBlock_NotRepaint(m_strCandleType);
                }

                if(strCandleType.equals("라인"))
                    this.setCandleType(strCandleType);
            }

            //this.setCandleType(strCandleType);
            //UD=0:notype 1:일 2:주 3:월 4:분 5:틱 6:Text 8:년
            //0,1,2,3,4,5(틱,분,일,주,월,년)
            int nDataTypeName[] = {2,3,4,1,0,2,6,5};

            if(_cdm.getDataType()>0 && _cdm.getDataType()<9)
                _cdm.codeItem.strDataType = ""+nDataTypeName[_cdm.getDataType()-1];
            _cdm.codeItem.strUnit = ""+_cdm.getTerm();

            COMUtil.dataTypeName = _cdm.codeItem.strDataType;
            COMUtil.unit = _cdm.codeItem.strUnit;

            //YSCale에서 사용할 가격 등락폭, 등락률 설정.
//	        _cdm.codeItem.strChange = COMUtil.format(change, 0, 3);
            _cdm.codeItem.strChange = change;
//			if(_cdm.codeItem.strChange.startsWith("-") || _cdm.codeItem.strChange.startsWith("+"))
//			{
//				_cdm.codeItem.strChange = _cdm.codeItem.strChange.substring(1);
//			}
            if(chgrate.startsWith("--"))
                chgrate = chgrate.substring(1);
            _cdm.codeItem.strChgrate = COMUtil.format(chgrate, 2, 3)+"%";

            if(dataAddType==1) _cdm.initAppendData(nCount);
            else{
                deregRT();
                DataClear();
                _cdm.initData(nCount);
            }
            if(nCount==0){
                //2019. 08. 20 by hyh - 데이터 없을 때 현재가 정보 초기화
                _cdm.codeItem = new CodeItemObj();

                if(useReal)regRT();
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }
//	        byte[] d = new byte[data.length-(offset+nMsgCodeLen)];
//	        try{
//	            System.arraycopy(data,offset+nMsgCodeLen,d, 0, d.length);
//	        }catch(ArrayIndexOutOfBoundsException e){
//
//	        }
            if(useReal)regRT();
	        /*if(_cvm.isOnePage()||nCount>400){
	            _cdm.setMaxData(nCount);
	        }
	        else{
	            _cdm.setMaxData(400);
	        }*/
        }
    }

    public void setData_data(String[] strDate, double[] strOpen, double[] strHigh, double[] strLow, double[] strClose, double[] strVolume, double[] strValue, double[] strRights, double[] strRightRates) {
        // _cdm.setData(nCount,d);

        this.hideViewPanel(); //2021.05.27 by lyk - kakaopay - 데이터 조회시 시세 인포뷰 감추기

        if(_cvm.chartType==COMUtil.COMPARE_CHART) {
            _cdm.setData_data("자료일자", strDate);
            _cdm.setData_data(COMUtil.symbol, strClose);
            if(strRights.length >0)
            {
                _cdm.setSubPacketData("락구분", strRights);
                _cdm.setSubPacketData("락비율", strRightRates);
            }
        }
        else
        {
            if(COMUtil.apCode.equals(COMUtil.TR_CHART_INVESTOR))
            {
                //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
                if (_cvm.bIsUpdownChart) {
                    _cdm.accrueNames = strDate;
                    _cdm.accrueOpens = strOpen;
                    _cdm.accrueHighs = strHigh;
                    _cdm.accrueLows = strLow;
                    _cdm.accrueCloses = strClose;
                    _cdm.accrueBases = strVolume;
                }
                //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
                else {
                    _cdm.setData_data("자료일자", strDate);
                    _cdm.setData_data("data1", strOpen);
                    if (strHigh != null)
                        _cdm.setData_data("data2", strHigh);
                    if (strLow != null)
                        _cdm.setData_data("data3", strLow);
                    //2013.07.22 by LYH >> 투자자 차트 그래프 보이기/숨기기 기능
                    if (strClose != null)
                        _cdm.setData_data("data4", strClose);
                    if (strVolume != null)
                        _cdm.setData_data("data5", strVolume);
                    if (strValue != null)
                        _cdm.setData_data("data6", strValue);
                    //2013.07.22 by LYH <<
                }
            }
            //2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
            else if(_cvm.bRateCompare)
            {
                _cdm.setData_data("자료일자", strDate);
                _cdm.setData_data("data1", strLow);
                _cdm.setData_data("data2", strClose);
            }
            //2013.01.04 by LYH <<
            else
            {
                _cdm.setData_data("자료일자", strDate);
                _cdm.setData_data("시가", strOpen);
                _cdm.setData_data("고가", strHigh);
                _cdm.setData_data("저가", strLow);
                _cdm.setData_data("종가", strClose);
                _cdm.setData_data("기본거래량", strVolume);

                if(strValue!=null && strValue.length>0)
                {
                    _cdm.setData_data("거래대금", strValue);

                    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
                    if (_cvm.nFxMarginType == ChartViewModel.FX_BUYSELL) {
                        if (dataAddType == 1) {
                            ChartPacketDataModel cpdm = _cdm.getChartPacket("매수");
                            if (cpdm != null)
                                cpdm.initAppendData(strValue.length);
                            _cdm.setData_data("매수", strValue);

                        }
                        else {
                            setFXData("매수", strValue, false);
                        }
                    }
                    else {
                        removeGraph("매수");
                    }
                    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

                    //2019. 07. 02 by hyh - FX마진 거래량 블록 제거. 통합차트에서는 동작하지 않도록 처리 >>
                    String strLastFileName = COMUtil._mainFrame.strLocalFileName;
                    if (_cvm.nFxMarginType >= 0 && !strLastFileName.contains("total")) {
                        removeBlock("거래량");
                    }
                    //2019. 07. 02 by hyh - FX마진 거래량 블록 제거. 통합차트에서는 동작하지 않도록 처리 <<
                }
                if(strRights!=null && strRights.length >0)
                {
                    _cdm.setData_data("락구분", strRights);
                }

                if(strRightRates!=null && strRightRates.length >0)
                {
                    _cdm.setData_data("락비율", strRightRates);
                }
            }
        }

        //추가요청 데이터의 경우 분석툴을 초기화하지 않고, 새로 조회할 경우에만 초기화한다.
        if(!COMUtil.getSendTrType().equals("requestAddData")) {
            removeAllAnalTool();	//2013. 11. 15 조회시 도구만 지우도록 롤백
            addStrageAnalTool();
        }

        //k delete
        _cdm.codeItem.strName = COMUtil.removeString(_cdm.codeItem.strName, "ⓚ");
        String codeName = _cdm.codeItem.strName;
//	        System.out.println("codeItem.strName_neoChart:"+codeName);
        COMUtil.codeName = codeName;
        COMUtil.preSymbol = COMUtil.symbol;
        if(!COMUtil.getSendTrType().equals("requestAddData"))
            COMUtil.getMainBase().setCodeName(codeName.trim()); //종목명설정.
        COMUtil.getMainBase().setPeriodName("");
//	        if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) { //주식코드만 저장.
//	        	saveHistoryCode();
//	        }
        //조회 count 저장.
        COMUtil.count = ""+_cdm.getCount();
        _cvm.setIsQuery(true);

        //2016.12.14 by LYH >>갭보정 추가
        if(dataAddType==1){
            calcAdjusted(false);
            calcGapRevision(false);
        }
        else
        {
            m_dAdjustedRate = 1;
            if(_cvm.chartType==COMUtil.COMPARE_CHART)
                calcAdjusted_Compare(true);
            else
                calcAdjusted(true);
            calcGapRevision(true);
        }
        //2016.12.14 by LYH >>갭보정 추가 end
        if(dataAddType==1){
            _cdm.resetCnt();
            setScrollBar(ONLY_SHOWCHANGE);
        } else {
            _cvm.setViewNum(_cvm.getSetViewNum()); //추가데이터 처리시 디폴트 뷰카운트와 차트데이터+추가데이터 카운트의 갯수에 따른 오류 문제 수정.
            if(_cvm.isStandGraph() || _cvm.isOnePage()){
                setScrollBar(this.DATA_END);
            }else{
                setScrollBar(NOTONLY_DATACHANGE);
            }
        }

        try{
            //2016.03.29 by LYH >> 비교차트 데이터 개수 줄어드는 현상 수정.
            if(_cvm.chartType==COMUtil.COMPARE_CHART && _cdm.getCount()<=_cdm.baseLineIndex) {
                _cdm.baseLineIndex = 0;
            }
            //2016.03.29 by LYH << 비교차트 데이터 개수 줄어드는 현상 수정.
            makeGraphData();
            //            if(dataList!=null && dataList.isVisible())
            //                dataList.setData(_cdm);
        }catch(ArrayIndexOutOfBoundsException e){
        }
        xscale.dataChanged();
        COMUtil.getMainBase().setCountText("");

        //2016. 1. 14 뉴스차트 핸들러>>
        if(_cvm.bIsNewsChart && _cvm.curIndex < 0)
        {
            _cvm.curIndex = _cdm.getCount()-1;
        }
        //2016. 1. 14 뉴스차트 핸들러<<

        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
        if (_cvm.bIsUpdownChart) {
            setBounds2(chart_bounds.left, chart_bounds.top, chart_bounds.width(), (int) (strDate.length * COMUtil.getPixel(33)));
            ScrollView sView = (ScrollView) this.getParent();
            sView.scrollTo(0, 0);
        }
        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
        else {
            repaintAll();
        }
        COMUtil.isProcessData=false;

        if(_cvm.bNetBuyChart)
        {
            if(basic_block != null) {
                for(int i=0; i<basic_block.getGraphs().size(); i++) {
                    AbstractGraph graph = (AbstractGraph)basic_block.getGraphs().get(i);
                    if(COMUtil.isMarketIndicator(graph.getGraphTitle())) {
                        nSendMarketIndex = i;
                        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                        base11.sendTR(graph.getGraphTitle());
                        break;
                    }
                }
            }
        }

        if(_cvm.chartType!=COMUtil.COMPARE_CHART) {
            if(blocks != null) {
                for (int i=0; i<blocks.size(); i++) {
                    Block block = blocks.get(i);
                    if(COMUtil.isMarketIndicator(block.getTitle())) {
                        nSendMarketIndex = i;
                        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                        base11.sendTR(block.getTitle());
                        break;
                    }
                }
            }
//2016.02.14 by LYH >> 로테이트 블럭 보이지 않는 영역 시장지표 조회 막음
//			if(rotate_blocks !=null && rotate_blocks.size()>0) {
//				for(int i=0; i<rotate_blocks.size(); i++) {
//					Block block = rotate_blocks.get(i);
//					if(COMUtil.isMarketIndicator(block.getTitle())) {
////	        				isSendRotateMarket = true;
//						nSendMarketIndex = i;
//						Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//						base11.sendTR(block.getTitle());
//						break;
//					}
//				}
//			}
//2016.02.14 by LYH << 로테이트 블럭 보이지 않는 영역 시장지표 조회 막음
        }
        _cvm.setIsQuery(false);

        //2017.09.25 by LYH >> 자산 차트 적용
        m_bIsBitmapRefresh = false;
        //2017.09.25 by LYH >> 자산 차트 적용 end
    }
    //}

    public void setCompareData( byte[] data) {
        synchronized (this) {
            if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK) ||
                    COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE) ||
                    COMUtil.apCode.equals(COMUtil.TR_CHART_UPJONG)) {
                COMUtil.isProcessData=false;
                return;
            }

            if(data==null || data.length<20){
                COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
                COMUtil.isProcessData=false;
                COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
                COMUtil.nkey="";
                COMUtil.isProcessData=false;
                return;
            }

            //차트형태 설정.
            //        if(COMUtil.dataTypeName.equals("0")) {
            //        	this.changeBlock_NotRepaint("라인");
            //        } else {
            //        	if(COMUtil.selectedJipyo==null) {
            //        		this.changeBlock_NotRepaint("캔들");
            //        	} else {
            //        		this.changeBlock_NotRepaint(COMUtil.selectedJipyo);
            //        	}
            //        }
//	        String tmpStr = COMUtil.stringFromData(data, data.length);
            Hashtable<String, String> items = trData.makeTrData(COMUtil.apCode, data);
            //_cdm.codeItem = items;
            if(COMUtil.lcode==null)
                COMUtil.lcode = "S31";
            _cdm.codeItem.strRealKey =COMUtil.lcode;
            _cdm.codeItem.strCode = COMUtil.symbol;
            _cdm.codeItem.strDataType = COMUtil.dataTypeName;
            _cdm.codeItem.strUnit = COMUtil.unit;

            int offset = 0;
            String codeName = "";
            String change = "";
            String chgrate = "";
            int nCount = 0;
            int nMsgCodeLen = 0;

            if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_STOCK)) {
                try {

                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);

                    tmp = items.get(OutputPacket.BOJOLEN);
                    if(tmp.equals("")) nMsgCodeLen=0;
                    else nMsgCodeLen = Integer.parseInt(tmp);

                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_UPJONG)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_FUTURE)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }

            if(nMsgCodeLen < 0){
                //if(useReal)
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }


            String str = "", key, value;
            //        offset += 4;

            //      	str = new String(data, offset, nMsgCodeLen);
            str = items.get(OutputPacket.BOJOMSG);
            StringTokenizer st = new StringTokenizer(str, "@");
            //보조메세지 분석
            //        _cdm.setPrevData("0");
            while (st.hasMoreTokens()) {
                str = st.nextToken();
                int nIndex = str.indexOf('=');
                if(nIndex!=-1){
                    key = new String(str.substring(0, nIndex));
                    value = new String(str.substring(nIndex+1));
                    processTheCmdMsg(key, value);
                }
            }

            int nDataTypeName[] = {2,3,4,1,0,2,6,5};

            if(_cdm.getDataType()>0 && _cdm.getDataType()<9)
                _cdm.codeItem.strDataType = ""+nDataTypeName[_cdm.getDataType()-1];
            _cdm.codeItem.strUnit = ""+_cdm.getTerm();

            COMUtil.dataTypeName = _cdm.codeItem.strDataType;
            COMUtil.unit = _cdm.codeItem.strUnit;

            //YSCale에서 사용할 가격 등락폭, 등락률 설정.
            _cdm.codeItem.strChange = COMUtil.format(change, 0, 3);
            _cdm.codeItem.strChgrate = COMUtil.format(chgrate, 2, 3)+"%";

            if(dataAddType==1) _cdm.initAppendData(nCount);
            else{
                //이전종목 실시간 해제
//				deregRT();
//				DataClear();
                _cdm.initCompareData(nCount);
            }
            if(nCount==0){
                if(useReal)regRT();
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }
            byte[] d = new byte[data.length-(offset+nMsgCodeLen)];
            try{
                System.arraycopy(data,offset+nMsgCodeLen,d, 0, d.length);
            }catch(ArrayIndexOutOfBoundsException e){

            }
            if(useReal)regRT();
	        /*if(_cvm.isOnePage()||nCount>400){
	            _cdm.setMaxData(nCount);
	        }
	        else{
	            _cdm.setMaxData(400);
	        }*/

            _cdm.setData(nCount,d);

            //추가요청 데이터의 경우 분석툴을 초기화하지 않고, 새로 조회할 경우에만 초기화한다.
            if(!COMUtil.getSendTrType().equals("requestAddData")) {
                removeAllAnalTool();
            }
            //k delete
            _cdm.codeItem.strName = COMUtil.removeString(_cdm.codeItem.strName, "ⓚ");
            codeName = _cdm.codeItem.strName;
            //COMUtil.codeName = codeName;
            COMUtil.preSymbol = COMUtil.symbol;
            COMUtil.getMainBase().setCodeName(codeName.trim()); //종목명설정.
            COMUtil.getMainBase().setPeriodName("");
//	        if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) { //주식코드만 저장.
//	        	saveHistoryCode();
//	        }
            //조회 count 저장.
            COMUtil.count = ""+_cdm.getCount();
            if(dataAddType==1){
                _cdm.resetCnt();
                setScrollBar(ONLY_SHOWCHANGE);
            } else {
                if(_cvm.isStandGraph() || _cvm.isOnePage()){
                    setScrollBar(this.DATA_END);
                }else{
                    setScrollBar(NOTONLY_DATACHANGE);
                }
            }

            try{
                makeGraphData();
                //            if(dataList!=null && dataList.isVisible())
                //                dataList.setData(_cdm);
            }catch(ArrayIndexOutOfBoundsException e){
            }
            xscale.dataChanged();
            repaintAll();
            COMUtil.isProcessData=false;
        }
    }
    public void setCompareData_header( byte[] data) {
        synchronized (this) {
            if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK) ||
                    COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE) ||
                    COMUtil.apCode.equals(COMUtil.TR_CHART_UPJONG)) {
                COMUtil.isProcessData=false;
                return;
            }

            if(data==null || data.length<20){
                COMUtil.showMessage(context, "조회된 데이터가 없습니다.");
                COMUtil.isProcessData=false;
                COMUtil.getMainBase().setCodeName(COMUtil.codeName); //조회된 종목이 없으면 이전 종목명으로 다시 설정한다.
                COMUtil.nkey="";
                COMUtil.isProcessData=false;
                return;
            }

            //차트형태 설정.
            //        if(COMUtil.dataTypeName.equals("0")) {
            //        	this.changeBlock_NotRepaint("라인");
            //        } else {
            //        	if(COMUtil.selectedJipyo==null) {
            //        		this.changeBlock_NotRepaint("캔들");
            //        	} else {
            //        		this.changeBlock_NotRepaint(COMUtil.selectedJipyo);
            //        	}
            //        }
//	        String tmpStr = COMUtil.stringFromData(data, data.length);
            Hashtable<String, String> items = trData.makeTrData(COMUtil.apCode, data);
            //_cdm.codeItem = items;
            if(COMUtil.lcode==null)
                COMUtil.lcode = "S31";
            _cdm.codeItem.strRealKey =COMUtil.lcode;
            _cdm.codeItem.strCode = COMUtil.symbol;
            _cdm.codeItem.strDataType = COMUtil.dataTypeName;
            _cdm.codeItem.strUnit = COMUtil.unit;

            //int offset = 0;
//	        String codeName = "";
            String change = "";
            String chgrate = "";
            int nCount = 0;
            int nMsgCodeLen = 0;

            if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_STOCK)) {
                try {

                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);

                    tmp = items.get(OutputPacket.BOJOLEN);
                    if(tmp.equals("")) nMsgCodeLen=0;
                    else nMsgCodeLen = Integer.parseInt(tmp);

                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_UPJONG)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }else if(COMUtil.apCode.equals(COMUtil.TR_COMPARE_FUTURE)) {
                try {
                    _cdm.codeItem.strName = items.get(OutputPacket.NAME );
                    _cdm.codeItem.strPrice = items.get(OutputPacket.PRICE );
                    _cdm.codeItem.strSign = items.get(OutputPacket.SIGN);
                    _cdm.codeItem.strVolume = items.get(OutputPacket.VOLUME );
                    _cdm.codeItem.strNextKey = items.get(OutputPacket.NKEY );

                    change = items.get(OutputPacket.CHANGE);
                    String sign = items.get(OutputPacket.SIGN);
                    if(Integer.parseInt(sign)>3){
                        change = "-"+change;
                    }
                    chgrate = items.get(OutputPacket.CHGRATE);
                    COMUtil.nkey = items.get(OutputPacket.NKEY);
                    //Chart count
                    String tmp = items.get(OutputPacket.TMP);
                    nCount = Integer.parseInt(tmp);
                    tmp = items.get(OutputPacket.BOJOLEN);
                    nMsgCodeLen = Integer.parseInt(tmp);
                    //offset = trData.getOffset()-Integer.parseInt(items.get(OutputPacket.BOJOMSG+"_Len"));

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    COMUtil.showMessage(context, e.getMessage());
                    COMUtil.isProcessData=false;
                    return;
                }
            }

            if(nMsgCodeLen < 0){
                //if(useReal)
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }


            String str = "", key, value;
            //        offset += 4;

            //      	str = new String(data, offset, nMsgCodeLen);
            str = items.get(OutputPacket.BOJOMSG);
            StringTokenizer st = new StringTokenizer(str, "@");
            //보조메세지 분석
            //        _cdm.setPrevData("0");
            while (st.hasMoreTokens()) {
                str = st.nextToken();
                int nIndex = str.indexOf('=');
                if(nIndex!=-1){
                    key = new String(str.substring(0, nIndex));
                    value = new String(str.substring(nIndex+1));
                    processTheCmdMsg(key, value);
                }
            }

            //YSCale에서 사용할 가격 등락폭, 등락률 설정.
            _cdm.codeItem.strChange = COMUtil.format(change, 0, 3);
            _cdm.codeItem.strChgrate = COMUtil.format(chgrate, 2, 3)+"%";

            if(dataAddType==1) _cdm.initAppendData(nCount);
            else{
                //2016.03.29 by LYH >> 비교차트 데이터 개수 줄어드는 현상 수정.
                //이전종목 실시간 해제
//				deregRT();
//				DataClear();
                _cdm.initCompareData(nCount);
                //2016.03.29 by LYH << 비교차트 데이터 개수 줄어드는 현상 수정.
            }
            if(nCount==0){
                if(useReal)regRT();
                repaintAll();
                COMUtil.isProcessData=false;
                return;
            }
//	        byte[] d = new byte[data.length-(offset+nMsgCodeLen)];
//	        try{
//	            System.arraycopy(data,offset+nMsgCodeLen,d, 0, d.length);
//	        }catch(ArrayIndexOutOfBoundsException e){
//
//	        }
//	        if(useReal)regRT();
//	        /*if(_cvm.isOnePage()||nCount>400){
//	            _cdm.setMaxData(nCount);
//	        }
//	        else{
//	            _cdm.setMaxData(400);
//	        }*/
//
//	        _cdm.setData(nCount,d);
//
//	        //추가요청 데이터의 경우 분석툴을 초기화하지 않고, 새로 조회할 경우에만 초기화한다.
//	        if(!COMUtil.getSendTrType().equals("requestAddData")) {
//	        	removeAllAnalTool();
//	        }
//	        //k delete
//	        _cdm.codeItem.strName = COMUtil.removeString(_cdm.codeItem.strName, "ⓚ");
//	        codeName = _cdm.codeItem.strName;
//	        //COMUtil.codeName = codeName;
//	        COMUtil.preSymbol = COMUtil.symbol;
//	        COMUtil.getMainBase().setCodeName(codeName.trim()); //종목명설정.
//	        COMUtil.getMainBase().setPeriodName("");
//	        if(COMUtil.apCode.equals(COMUtil.TR_CHART_STOCK)) { //주식코드만 저장.
//	        	saveHistoryCode();
//	        }
//	        //조회 count 저장.
//	        COMUtil.count = ""+_cdm.getCount();
//	        if(dataAddType==1){
//	            _cdm.resetCnt();
//	            setScrollBar(ONLY_SHOWCHANGE);
//	        } else {
//	            if(_cvm.isStandGraph() || _cvm.isOnePage()){
//	                setScrollBar(this.DATA_END);
//	            }else{
//	                setScrollBar(NOTONLY_DATACHANGE);
//	            }
//	        }
//
//	        try{
//	            makeGraphData();
//	//            if(dataList!=null && dataList.isVisible())
//	//                dataList.setData(_cdm);
//	        }catch(ArrayIndexOutOfBoundsException e){
//	        }
//	        xscale.dataChanged();
//	        repaintAll();
//	        COMUtil.isProcessData=false;
        }
    }

    public boolean hasMarketType() {
        //일봉이 아닌 경우 false
//        if(!COMUtil.dataTypeName.equals("2") || COMUtil.apCode.equals(COMUtil.TR_CHART_FUTURE)) {
//            return false;
//        }

        if(blocks != null) {
            for (int i=0; i<blocks.size(); i++) {
                Block block = blocks.get(i);
                if(COMUtil.isMarketIndicator(block.getTitle())) {
//    				nSendMarketIndex = i;	//2015. 3. 9 시장지표 여러개 추가되어 rotateblock 생성시 조회안됨
                    return true;
                }
            }
        }

        if(rotate_blocks !=null && rotate_blocks.size()>0) {
            for(int i=0; i<rotate_blocks.size(); i++) {
                Block block = rotate_blocks.get(i);
                if(COMUtil.isMarketIndicator(block.getTitle())) {
//    				isSendRotateMarket = true;
//    				nSendMarketIndex = i;	//2015. 3. 9 시장지표 여러개 추가되어 rotateblock 생성시 조회안됨
                    return true;
                }
            }
        }

        return false;
    }

//	//히스토리 아이템 저장.
//	Cursor cursor = null;
//	HistoryDBHelper hHelper = COMUtil.getHDBHelper(this.getContext());
//	SQLiteDatabase db = hHelper.getWritableDatabase();

//    public void saveHistoryCode() {
//
//		try {
//			String strLcode = _cdm.codeItem.strRealKey;
//			String strSymbol = _cdm.codeItem.strCode;
//			String strCodeName = _cdm.codeItem.strName;
//			int maxCnt = 20; //최대 maxCnt만큼 처리.
//			db = hHelper.getWritableDatabase();
//			if(!db.isOpen()) return;
//			cursor = db.rawQuery("SELECT * FROM historydata", null);
//			if(cursor.getCount()>maxCnt) {
//				if(cursor.moveToLast()) {
//					String _id = cursor.getString(0);//_id
//					db.execSQL("DELETE FROM historydata where _id="+"'"+_id+"'");
//				}
//			}
//			cursor.close();
//
//			//같은 종목이면, 기존 위치의 데이터를 삭제하고, 첫번째 위치에 넣는다.
//			cursor = db.rawQuery("SELECT * FROM historydata where symbol="+"'"+strSymbol+"'", null);
//			if(cursor.getCount()>0) {
//				db.execSQL("DELETE FROM historydata where symbol="+"'"+strSymbol+"'");
//			}
//			cursor.moveToFirst();
//			db.execSQL("INSERT INTO historydata VALUES (null, " +
//			" '"+strLcode+"'," +
//			" '"+strSymbol+"'," +
//			" '"+strCodeName+"'" +
//					");");
//
//			cursor.close();
//			db.close();
//		} catch(SQLiteException e) {
//			System.out.println("NeoChart Exception : "+e.getMessage());
//		}
//    }

    //=====================================
    //ChartDataModel의 데이터들을 클리어 한다
    //=====================================
    public void ChartDataClear(){
        if(_cdm!=null){
            _cdm.clearData();
        }
        repaintAll();
    }
    //=====================================
    //챠트가 닫힐때 불려진다.
    //=====================================
    public void destroy() {
        //double buffering
        if(backbit != null)
        {
            backbit.recycle();
            backbit = null;
        }
        offscreen = null;

        if(_cdm!=null){
            _cdm.destroy();
            //_cdm=null;
        }
//        if(dataP!=null && dataP.isVisible()) dataP.setVisible(false);
//        dataP = null;
//        if(gset!=null){
//            gset.setVisible(false);
//            gset=null;
//        }
        //ibuf=null;
        //_cvm=null;
        if(this.analTools!=null){
            analTools.removeAllElements();
            analTools=null;
            if(analToolSubbar!=null) {
                COMUtil._chartMain.runOnUiThread(new Runnable() {
                    public void run() {
                        layout.removeView(analToolSubbar);
                        COMUtil.unbindDrawables(analToolSubbar);
                    }
                });

                analToolSubbar = null;
            }
        }
        if(_cvm != null)
            _cvm.destroy();
        listeners.removeAllElements();
        if(this.reqRV != null)
            reqRV.removeAllElements();
        //vertexBuffer.clear();
        //textureBuffer.clear();
//        fogColor.clear();
        trData.destroy();
        changable_block = null;
        //textures = null;

        //2012. 10. 30  멀티차트에서 단일차트로 돌아올때 멀티차트 뷰패널이 열려있으면 닫기 : C20
        if (viewP != null) {
            layout.removeView(viewP);
            layout.removeView(blurView);
            viewP = null;
            blurView = null;
        }
//		if (m_btnViewPanelAlarm != null) {
//			layout.removeView(m_btnViewPanelAlarm);
//			m_btnViewPanelAlarm = null;
//		}
//		if (m_btnViewPanelClose != null) {
//			layout.removeView(m_btnViewPanelClose);
//			m_btnViewPanelClose = null;
//		}

        removeAllBlocks();
    }

    public void addBlock(Block b){
        if(blocks ==null)blocks = new Vector<Block>();

        //if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
        if(rotate_blocks==null) rotate_blocks = new Vector<Block>();
        if(title_list == null) title_list = new Vector<String>();
        int nBlockCnt = blocks.size();
        if(nBlockCnt > m_nMaxIndicatorCount) {
            Block cb = (Block)blocks.get(nBlockCnt-1);
            if(cb.getBlockType()!=2) {
                rotate_blocks.addElement(cb);
                cb.setHideDelButton(true);
                if(!title_list.contains(b.getTitle())) {
                    title_list.addElement(b.getTitle());
                }

                blocks.remove(cb);
            } else {
                cb.setHideDelButton(true);
            }
        } else if(nBlockCnt == m_nMaxIndicatorCount && rotate_blocks.size() < 1) {
            if(!title_list.contains(b.getTitle())) {
                title_list.addElement(b.getTitle());
            }
            b.setTitleNumber(0, 0);
        }
        //}
        if(_cvm.isStandGraph())
            b.setHideDelButton(true);

        blocks.addElement(b);

        if(rotate_blocks!=null && rotate_blocks.size()>0) {
            setTitleNumber();
        }
        isBlockChanged = true;
    }
    public void setTitleNumber() {
        int i = 0;
        if(blocks.size()<m_nMaxIndicatorCount) {
            return;
        }
        Block cb = (Block)blocks.get(m_nMaxIndicatorCount);
        for(; i<title_list.size(); i++) {
            String title = (String)title_list.get(i);
            if(title.equals(cb.getTitle())) {
                cb.setTitleNumber(rotate_blocks.size()+1, i);
                m_nRollIndex = i;
                break;
            }
        }
    }
    public void changeBlock_NotRepaint(String graph){
        if(graph==null) return;

        if(graph.equals("캔들")) graph = "일본식봉차트";
        else if(graph.equals("바")) graph = "미국식봉차트";
        else if(graph.equals("라인")) graph = "종가선차트";
        else if(graph.equals("Log")) graph = "로그차트";
        else if(graph.equals("Linear")) graph = "선형차트";
        else if(graph.equals("Heikin-Ashi")) graph="Heikin-Ashi";

        if(_cvm.isStandGraph())setScrollBar(NOTONLY_DATACHANGE);
//        if(sbExtent!=null)sbExtent.setEnabled(true);
        if(basic_block==null || basic_block.getGraphs().size()<1) return;
        AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
        if(basic_graph.getDrawTool().size()<0)
            return;

        DrawTool basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);

        Vector<AbstractGraph> graphs = basic_block.getGraphs();
        if(graph.equals("일본식봉차트")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY)){ //2021.04.05 by lyk - kakaopay - 투명캔들차트
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
        }else if(graph.equals("미국식봉차트")) {
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(1);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_BAR_OHLC)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(2);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_RANGE_LINE)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(3);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_FLOW)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(4);
        }else if(graph.equals("종가선차트")){
            basic_dt.setDrawType1(1);
            basic_dt.setDrawType2(0);
            basic_block.setLog(false);
            basic_dt.setLog(false);
            //_cvm.isLog=true;
            for(int i=1; i<graphs.size(); i++){
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                Vector<DrawTool> dts = g.getDrawTool();
                for(int j=0; j<dts.size(); j++){
                    DrawTool dt = (DrawTool)g.getDrawTool().elementAt(j);
                    dt.setLog(false);
                }
            }
            return;
        }else if(graph.equals("로그차트")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
            basic_block.setLog(true);
            basic_dt.setLog(true);
            _cvm.isLog=true;
            for(int i=1; i<graphs.size(); i++){
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                Vector<DrawTool> dts = g.getDrawTool();
                for(int j=0; j<dts.size(); j++){
                    DrawTool dt = (DrawTool)g.getDrawTool().elementAt(j);
                    dt.setLog(true);
                }
            }
            return;
        }
        else if(graph.equals("Heikin-Ashi")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(5);
        }
        //2020.07.06 by LYH >> 캔들볼륨 >>
        else if(graph.equals(COMUtil.CANDLE_TYPE_CANDLE_VOLUME)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(6);
        }
        else if(graph.equals(COMUtil.CANDLE_TYPE_EQUI_VOLUME)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(7);
        }
        //2020.07.06 by LYH >> 캔들볼륨 <<
        else if(graph.equals(COMUtil.CANDLE_TYPE_STAIR))
        {
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(8);
        }
//        basic_block.setLog(false);
//        basic_dt.setLog(false);
//        _cvm.isLog=false;
//        for(int i=1; i<graphs.size(); i++){
//            AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
//            Vector<DrawTool> dts = g.getDrawTool();
//            for(int j=0; j<dts.size(); j++){
//                DrawTool dt = (DrawTool)g.getDrawTool().elementAt(j);
//                dt.setLog(false);
//            }
//        }
        //setLogChart();
    }

    //================================
    // 분석도구의 내용을 셋
    //================================
    public void setChartToolBar(int nType){
        this.reSetAnalTool(false);
        if(nType==COMUtil.TOOLBAR_CONFIG_ALL_ERASE){

//            final TrendDelAlertDialog alert = new TrendDelAlertDialog(COMUtil.apiView.getContext());
//            alert.setTitle("추세선 삭제");
//            alert.setNoButton("취소", null);
//            alert.setYesButton("확인",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog,
//                                            int which) {
////									initChart("pressInitBtn");	//초기화 수행
////									COMUtil._mainFrame.saveStatus(null);	//2013. 9. 12 분석툴바에서 초기화 하고 딴화면 갔다 오면 분석툴바 순서가 안바뀌어있음.
////									COMUtil.saveGijunState("gijunChartSetting");	//2013. 10. 7 분석툴바 기준선 추가 후 초기화 한 다음에 화면 나갔다 들어오면 기준선이 다시 그려져있음
//                            //2013. 11. 21 추세선 종목별 저장>> : 분석툴바 전체삭제 버튼
////            removeAllAnalTool();
//                            if(alert.isAllDelete())
//                            {
//                                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//                                base11.resetAllAnalToolBySymbol();
//                            }
//                            else {
//                                resetAnalToolBySymbol();
//                                //2013. 11. 21 추세선 종목별 저장<<
//
//                                repaintAll();
//
//                                if (userProtocol != null)
//                                    userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
//                                nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
//                                saveAnalToolBySymbol();
//                            }
//
//                            dialog.dismiss();
//                        }
//                    });
//            alert.show();

            //2020.05.14 by JJH >> 추세선 설정 팝업 UI 작업 start
//			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//			alert.setTitle("");
//			alert.setMessage("저장된 모든 추세선을 초기화하시겠습니까?");
//			alert.setNoButton("취소", null);
//			alert.setYesButton("초기화",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							//모든 차트의 분석툴을 지운다.
//							Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//							base11.resetAllAnalToolBySymbol();
//							resetAutoTrend();
//
//							//알림창 닫기
//							dialog.dismiss();
//
//							//설정창 닫기
//							COMUtil._mainFrame.closePopup();
//						}
//					});
//			alert.show();
//
//
//			COMUtil.g_chartDialog = alert;

            final TrendDelAlertDialog alert = new TrendDelAlertDialog(COMUtil.apiView.getContext());
            alert.setTitle("추세선 삭제");
//			alert.setMessage("저장된 모든 추세선을 초기화하시겠습니까?");
            alert.setNoButton("취소", null);
            alert.setYesButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            //모든 차트의 분석툴을 지운다.
                            Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;

                            if (alert.isAllDelete()){
                                base11.resetAllAnalToolBySymbol();
                            }else{
                                resetAnalToolBySymbol();
                            }
                            resetAutoTrend();
                            repaintAll();
                            saveAnalToolBySymbol();

                            //알림창 닫기
                            dialog.dismiss();

                            //설정창 닫기
                            COMUtil._mainFrame.closePopup();
                        }
                    });
            alert.show();


            COMUtil.g_chartDialog = alert;
            //2020.05.14 by JJH >> 추세선 설정 팝업 UI 작업 end
        }else if(nType==COMUtil.TOOLBAR_CONFIG_ERASE){
            removeAnalTool();
            repaintAll();

            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
            nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
            saveAnalToolBySymbol();
        }else{
            if(analToolTextField != null && nType!=COMUtil.TOOLBAR_CONFIG_TEXT)
            {
//
                layout.removeView(analToolTextField);

//				analToolTextField = null;

//				removeAnalTool();
                layout.removeView(analToolTextField);
                analToolTextField = null;
                removeAnalTool();
            }

            _cvm.setToolbarState(nType);
//        	if(nType == 9999) {
//        		_cvm.setToolbarState(nType);
//        	} else {
//        		_cvm.setToolbarState(nType - COMUtil.TOOLBAR_CONFIG_LINE);
//        	}
        }
    }
    private boolean pivotState = false;
    private float pivotGab = 0;
    public void setPivotState(boolean state) {
        pivotState = state;
        //피봇 시간대 처리
        if(state){
            for(int i=0; i<blocks.size(); i++) {
                Block block = (Block)blocks.elementAt(i);
                block.setBounds_Pivot(state);
            }
            pivotGab = this.basic_block.getPivotGab();
        } else {
            for(int i=0; i<blocks.size(); i++) {
                Block block = (Block)blocks.elementAt(i);
                block.setBounds_Pivot(state);
            }
            pivotGab = 0;
        }

        reSetUI(true);

    }

    //2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.<<
    public void initBlock(boolean bClearAnalTool){
        _cvm.indicatorItem.removeAllElements();
//    	COMUtil.baseLineType=-1;
//		_cvm.baseLineType =new ArrayList<Integer>();   //2013. 3. 6 기준선 다중선택

        // 2016.05.31 기준선 대비, 색상 굵기 >>
        _cvm.baseLineColors =new ArrayList<String>();
        _cvm.baseLineThicks = new ArrayList<Integer>();
        setEnvData(_cvm);
        // 2016.05.31 기준선 대비, 색상 굵기 <<

        //2019. 08. 21 by hyh - 자동추세선 복원 >>
        _cvm.autoTrendWaveType=0;
        _cvm.autoTrendHighType=0;
        _cvm.autoTrendLowType=0;
        _cvm.autoTrendWType=0;
        _cvm.preName = 3;
        _cvm.endName = 3;
        //2019. 08. 21 by hyh - 자동추세선 복원 <<

        COMUtil.nkey="";
        this.setVerticlaMode(false);

        //2013. 11. 21 추세선 종목별 저장>>
//    	this.removeAllAnalTool();
        //2014.03.21 by LYH >> 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.
        if(bClearAnalTool)
            resetAnalToolBySymbol();
        //2014.03.21 by LYH << 현재가 가로차트에서 현재가 차트 돌아올때 추세선 지워지는 문제 해결.
        //2013. 11. 21 추세선 종목별 저장<<

        _cvm.setOnePage(0);
        _cvm.setViewNum(_cvm.VIEW_NUM_ORG);
        _cvm.preViewNum = -1;
        _cvm.setStandGraph(false);
        _cvm.setInquiryNum(_cvm.INQUIRY_NUM_ORG);
        //2012. 10. 24 차트설정창에서 전체초기화시 이평이 초기화되지 않는 현상 수정 :  : I100
        _cvm.initAverage();
        _cvm.setCandle_basePrice(1);
        _cvm.setCandle_sameColorType(1);    //2016.01.05 by LYH >> 디폴트 설정값 변경
        //2013.09.27 by LYH >> 캔들색과 같이 디폴트로 변경 <<
        _cvm.setVolDrawType(3);	//2016.01.05 by LYH >> 디폴트 설정값 변경 거래량 전봉기준 상승/하락
        _cvm.setIsCandleMinMax(false);
        _cvm.setIsLog(false);
        _cvm.setIsInverse(false);  //2016.09.29 by LYH >> 거꾸로차트 기능 추가.
        _cvm.setIsGapRevision(false); //2016.12.14 by LYH >>갭보정 추가

        _cvm.futureMargin=0;
        if(_cvm.isOnePage()) this.setScrollBar(DATA_END);
        else this.setScrollBar(NOTONLY_DATACHANGE);

        //2012. 10. 30  초기화시 십자선 데이터 패널을 플래그 값에 따라서  감춤  : C21
        if(!_cvm.isCrosslineMode) {
            hideViewPanel();
        }

        //2015. 2. 24 차트 상하한가 표시
        setChartUpperLowerLimitData(null);

        removeAllBlocks();

        m_strSizeInfo = null;   //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
        //캔들로 초기화(Tic차트의 경우 그대로 둔다.) 2011.11.04 by lyk
//    	if(COMUtil.dataTypeName.equals("0")) {
//
//    	} else {
        COMUtil.selectedJipyo = "캔들";
        //COMUtil.dataTypeName = "2";
//    	}
    }
    // 2016.05.31 기준선 대비, 색상 굵기 >>
    @SuppressLint("DefaultLocale")
    public void setEnvData(ChartViewModel cvm){
        String strColor = getEnvString("baseline_color", "");

        int nBaseLineColor[][] = CoSys.CHART_COLORS;
        int g_nBaseLineColor[][] = CoSys.CHART_COLORS;

        if(strColor.length() >0)
        {
            String[] strColors = strColor.split("=");
            if( strColors.length >=15)
            {
                for(int i = 0 ; i < 20 ; i++)	{
                    g_nBaseLineColor[i][0] = nBaseLineColor[i][0] = Integer.parseInt(strColors[(i*3+0)]) ;
                    g_nBaseLineColor[i][1] = nBaseLineColor[i][1] = Integer.parseInt(strColors[(i*3+1)]) ;
                    g_nBaseLineColor[i][2] = nBaseLineColor[i][2] = Integer.parseInt(strColors[(i*3+2)]) ;
                }
            }
        }
        else
        {
            for(int i = 0 ; i < 20 ; i++)	{
                g_nBaseLineColor[i][0] = nBaseLineColor[i][0];
                g_nBaseLineColor[i][1] = nBaseLineColor[i][1];
                g_nBaseLineColor[i][2] = nBaseLineColor[i][2];
            }
        }


        String strThick = getEnvString("baseline_thick" ,"");
        int nBaseLineThick[] = {   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1,1,1,1  };
        int g_nBaseLineThick[] = {  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1,1,1,1  };
        if(strThick.length()>0)
        {
            String[] strThicks = strThick.split("=");
            if(strThicks.length>=11)
            {
                for(int i = 0 ; i < 20 ; i++)	{
                    g_nBaseLineThick[i] = nBaseLineThick[i] = Integer.parseInt(strThicks[i]) ;
                }
            }
        }
        else
        {
            for(int i = 0 ; i < 20 ; i++)	{
                g_nBaseLineThick[i] = nBaseLineThick[i];
            }
        }

        _cvm.baseLineColors.clear();
        _cvm.baseLineThicks.clear();

        String color = "";
        for(int i = 0 ; i < 20 ; i++)	{

            //공유정보
            color = String.format("%d=%d=%d", g_nBaseLineColor[i][0], g_nBaseLineColor[i][1],  g_nBaseLineColor[i][2]);
            cvm.baseLineColors.add(i,color);
            cvm.baseLineThicks.add(i, g_nBaseLineThick[i]);
        }
    }

    public void setEnvString(String strKey, String strValue)
    {


        if(m_prefConfig == null) return;

        SharedPreferences.Editor editConfig = m_prefConfig.edit();
        editConfig.putString(strKey, strValue);
        editConfig.commit();
    }

    public String getEnvString(String strKey, String strDefault){

        if(m_prefConfig == null) return strDefault;
        if(!m_prefConfig.contains(strKey)) return strDefault;

        try
        {

            return m_prefConfig.getString(strKey, strDefault);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return strDefault;
        }

    }
    // 2016.05.31 기준선 대비, 색상 굵기 <<

    //================================
    // 분석도구 전체 삭제
    //================================
    public void removeAllAnalTool(){
        if(this.analTools!=null){
            //문자 (ATextTool 삭제)
            for(int i=0; i<analTools.size(); i++)
            {
                AnalTool delTool = analTools.get(i);
                if(delTool instanceof ATextTool)
                {
                    ((ATextTool)delTool).removeLabel();
                }
            }
            analTools.removeAllElements();
            analTools=null;
            if(analToolSubbar!=null) {
//            	analParams.leftMargin = 0;
//            	analParams.topMargin = 0;
                this.showAnalToolSubbar(false, analParams);
            }

            repaintAll();

            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
            this.nChangeFlag = 1;
        }

    }
    //================================
    // 분석도구 순차적 삭제
    //================================
    private void removeAnalTool(){
        if(analTools!=null&&analTools.size()>0){
//            analTools.removeElement(analTools.lastElement());
            AnalTool delTool = analTools.get(analTools.size()-1);
            if(delTool instanceof ATextTool)
            {
                ((ATextTool)delTool).removeLabel();
            }
            analTools.remove(analTools.size()-1);
            if(analToolSubbar!=null) {
//            	analParams.leftMargin = 0;
//            	analParams.topMargin = 0;
                this.showAnalToolSubbar(false, analParams);
            }
        }
    }
    //================================
    // 선택된 분석도구 삭제
    //================================
    public void removeSelectedAnalTool(){
        if(select_at!=null){
            Block basic=(Block)blocks.elementAt(0);
            if(basic.getTitle().equals("Multi"))return;
            if(select_at instanceof ATextTool)
            {
                ((ATextTool)select_at).removeLabel();
            }
            this.analTools.removeElement(select_at);
            select_at=null;
            if(analToolSubbar!=null) {
//            	analParams.leftMargin = 0;
//            	analParams.topMargin = 0;
                this.showAnalToolSubbar(false, analParams);
            }
            repaintAll();

            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
            this.nChangeFlag = 1;
        }
    }
    //================================
    // 블럭 전체삭제
    //================================
    public void removeAllBlocks(){
        for(int i=0;i<blocks.size();i++){
            Block tmp= (Block)blocks.elementAt(i);
            tmp.destroy();
        }
        blocks.removeAllElements();
        if(rotate_blocks != null) {
            //rotate block의 블럭이동버튼 잔상 현상 수정. 2012.10.02 by lyk.
            for(int i=0;i<rotate_blocks.size();i++){
                Block tmp= (Block)rotate_blocks.elementAt(i);
                tmp.destroy();
            }
            rotate_blocks.removeAllElements();
        }
        if(title_list != null) {
            title_list.removeAllElements();
        }

        if(analTools != null) {
            analTools.removeAllElements();
        }

        //2015. 1. 29 가상매매연습기 매도/매수 표시>>
        if (analTradeTools != null) {
            analTradeTools.removeAllElements();
        }
        //2015. 1. 29 가상매매연습기 매도/매수 표시<<

        //2021. 4. 26  by hanjun.Kim - kakaopay - 배지아이콘 표시 >>
        if (aBadgeTools != null) {
            aBadgeTools.removeAllElements();
        }
        if (eventBadgeDataList != null) {
            eventBadgeDataList.removeAllElements();
        }
        //2021. 4. 26  by hanjun.Kim - kakaopay - 배지아이콘 표시 <<

        _cvm.setIsLog(false);
        _cvm.setIsInverse(false);
        _cvm.setIsGapRevision(false);   //2016.12.14 by LYH >>갭보정 추가
    }
    //================================
    // 해당 타이틀의 그래프 삭제 (추세지표에서만 적용,그외지표는 removeBlock을 이용)
    //0:기본,1: 독립,2:추세,3:일반
    //================================
    public void removeGraph(String graph){
        if(graph.equals("거래량이동평균"))
        {
            Block block = null;
            if(blocks!=null){
                int removeIndex =blocks.size();
                for(int i=0; i<blocks.size(); i++){
                    block= (Block)blocks.elementAt(i);
                    if(block.getTitle().equals("거래량")){
                        block.removeGraph(graph);
                        block.makeGraphData();
                        repaintAll();
                    }
                }
            }
            return;
        }
        if(basic_block!=null){
            if(graph.equals("BaseMarket"))
                preMarketTitle = "";

            basic_block.removeGraph(graph);
            basic_block.makeGraphData();
            repaintAll();
        }
    }

    public void setVisibleUserGraph(String sData, String visible) {
        Block block = null;
        if(blocks!=null){
            for(int i=0; i<blocks.size(); i++){
                block= (Block)blocks.elementAt(i);
                block.setVisibleUserGraph(sData, visible);
            }
        }
        this.reSetUI(true);
    }
    //================================
    // 해당 그래프의 블럭을 삭제
    //================================
    public void removeBlock(String graph){
        if(graph==null)return;
        boolean bRemove = false;
        Block block = null;

        if(blocks!=null){
            int removeIndex =blocks.size();
            for(int i=0; i<blocks.size(); i++){
                block= (Block)blocks.elementAt(i);

                //2019. 01. 12 by hyh - 블록병합 처리. 병합된 그래프 모두 삭제 >>
                if (block.arrMergedGraphTitles != null && block.arrMergedGraphTitles.size() > 1) {
                    if (block.getTitle().equals(graph)) {
                        if (!block.isBasicBlock()) {
                            removeIndex = i;
                            block.destroy();
                            blocks.removeElementAt(removeIndex);
                            bRemove = true;
                            break;
                        }
                    }
                    else {
                        for (String strMergedGraph : block.arrMergedGraphTitles) {
                            if (strMergedGraph.equals(graph)) {
                                block.removeGraph(strMergedGraph);
                                block.arrMergedGraphTitles.remove(strMergedGraph);
                                break;
                            }
                        }
                    }
                }
                else
                //2019. 01. 12 by hyh - 블록병합 처리. 병합된 그래프 모두 삭제 <<
                {
                    if (block.getTitle().equals("역시계곡선")) {
                        if (block.getTitle().contains(graph)) {
                            if (block.isBasicBlock()) {
                                COMUtil.showMessage(context, "가격차트 영역은 삭제될 수 없습니다");
                            }
                            else {
                                removeIndex = i;
                                block.destroy();
                                blocks.removeElementAt(removeIndex);
                                bRemove = true;
                                break;
                            }
                        }
                    }
                    else {
                        if (block.getTitle().equals(graph)) {
                            if (block.isBasicBlock()) {
                                COMUtil.showMessage(context, "가격차트 영역은 삭제될 수 없습니다");
                            }
                            else {
                                removeIndex = i;
                                block.destroy();
                                blocks.removeElementAt(removeIndex);
                                bRemove = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(title_list!=null) {
                for(int i=0; i<title_list.size();i++) {
                    String title = (String)title_list.get(i);
                    if(title.equals(graph)) {
                        title_list.remove(i);
                        break;
                    }
                }
            }
//            for(int i=removeIndex; i<blocks.size(); i++){
//                block = (Block)blocks.elementAt(i);
//                block.setIndex(block.getIndex()-1);
//            }
            if(rotate_blocks!=null && rotate_blocks.size()>0) {
                if(bRemove) {
                    if(rotate_blocks.size()>0) {
                        if(removeIndex<m_nMaxIndicatorCount) {
                            block = (Block)blocks.get(blocks.size()-1);
                            block.setTitleNumber(0, 0);
                            for(int i=0; i<title_list.size(); i++) {
                                String title = (String)title_list.get(i);
                                if(title.equals((String)block.getTitle())) {
                                    title_list.remove(i);
                                    break;
                                }
                            }
                        }

                        Block cb = (Block)rotate_blocks.get(rotate_blocks.size()-1);
                        rotate_blocks.remove(cb);
                        cb.setHideDelButton(false);
                        cb.makeGraphData();
                        if(m_nRollIndex>0)
                            m_nRollIndex--;
                        addBlock(cb);
                        setPivotState(pivotState);

                    }
                } else {
                    for(int i=0; i<rotate_blocks.size(); i++) {
                        Block cb = (Block)rotate_blocks.get(i);

                        if(cb.getTitle().equals(graph)) {
                            rotate_blocks.remove(i);
                        }
                    }
                    block = (Block)blocks.get(blocks.size()-1);
                    if(rotate_blocks.size()>0) {
                        setTitleNumber();
                    } else {
                        block = (Block)blocks.get(blocks.size()-1);
                        block.setTitleNumber(0, 0);
                    }
                }
            }

            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
            if(m_strSizeInfo != null)
            {
                String[] rateInfos = m_strSizeInfo.split("=");
                if(rateInfos.length>2) {
                    String rtnStr = "";
                    if (removeIndex <= m_nMaxIndicatorCount) {
                        double dTotRateHeight = 0;
                        double dHeightRate;
                        for (int i = 0; i < rateInfos.length; i++) {
                            if (i != removeIndex)
                                dTotRateHeight += Double.parseDouble(rateInfos[i]);
                        }
                        for (int i = 0; i < rateInfos.length; i++) {
                            if (i != removeIndex) {
                                dHeightRate = Double.parseDouble(rateInfos[i]) / dTotRateHeight;
                                rtnStr += String.format("%.6f=", dHeightRate);
                            }
                        }
                        if (rtnStr.endsWith("="))
                            rtnStr = rtnStr.substring(0, rtnStr.length() - 1);
                        m_strSizeInfo = rtnStr;
                    }
                }
                else
                    m_strSizeInfo = null;
            }
            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end
        }
        reSetUI(true);

        //2013. 9. 26 회전블록(rotate_block) 에 일반블록을 이동시키면  블록이 이상하게 변함. 초기화에 좀비블록이 남음. 회전블록 동그라미 표시 이상함 >> :
        //solution : 나중에  getChangableBlocks 해줄때 block 의 index변수값이 blocks 갯수보다 커서 exception 발생했었음. 그래서 블록삭제후에 인덱스 다시 세팅
        if(blocks.size()<m_nMaxIndicatorCount+1)
        {
            title_list.removeAllElements();
        }
        for(int i=0;i<blocks.size();i++){
            Block cb = (Block)blocks.elementAt(i);
            cb.setIndex(i);
            if(i==blocks.size()-1 && blocks.size()==m_nMaxIndicatorCount+1 && rotate_blocks.size()==0)
            {
                title_list.removeAllElements();
                title_list.add(cb.getTitle());
                cb.setTitleNumber(0, 0);
                m_nRollIndex = 0;
            }
        }
        //2013. 9. 26 회전블록(rotate_block) 에 일반블록을 이동시키면  블록이 이상하게 변함. 초기화에 좀비블록이 남음. 회전블록 동그라미 표시 이상함 >>
    }
    public boolean checkBlock(String graph) {
        if(graph==null)return false;
        Block block = null;
        if(blocks!=null){
            for(int i=0; i<blocks.size(); i++){
                block= (Block)blocks.elementAt(i);
                if(block.getTitle().equals(graph)){
                    return true;
                }
            }
        }

        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                block= (Block)rotate_blocks.elementAt(i);
                if(block.getTitle().equals(graph)){
                    return true;
                }
            }
        }
        return false;
    }
    //================================
    // 독립차트 블럭을 삭제
    //================================
    public void removeStandBlock(String graph){
        removeBlock(graph);
        for(int i=0; i<blocks.size(); i++) {
            Block block = (Block)blocks.get(i);
            block.setHideDelButton(false);
        }
    }
    //================================
    // 이동평균 그래프를 리턴(ChartSetUI참조)
    //================================
    public AbstractGraph getAverageGraph(){
        if(basic_block!=null){
            Vector<AbstractGraph> graph = basic_block.getGraphs();
            for(int i=0;i<graph.size();i++){
                AbstractGraph aver = (AbstractGraph)graph.elementAt(i);
                if(aver.getGraphTitle().endsWith("이동평균")){
                    return aver;
                }
            }
        }
        return null;
    }
    //================================
    // 해당 그래프를 기본블럭에 추가(추세지표에서만 사용, 그외는 addBlock을 사용
    //================================
    public void addGraph(String graph){
        if(graph.equals("거래량이동평균"))
        {
            Block block = null;
            if(blocks!=null){
                for(int i=0; i<blocks.size(); i++){
                    block= (Block)blocks.elementAt(i);
                    if(block.getTitle().equals("거래량")){
                        Vector<AbstractGraph> graphs = block.getGraphs();
                        for(int j=0; j<graphs.size(); j++) {
                            AbstractGraph g = (AbstractGraph)graphs.elementAt(j);
                            if(graph.equals(g.getGraphTitle())) {
                                return;
                            }
                        }
                        block.add(graph);
                        block.makeGraphData();
                        setPivotState(pivotState);
                    }
                }
            }

            if(rotate_blocks != null) {
                for(int i=0; i<rotate_blocks.size(); i++){
                    block= (Block)rotate_blocks.elementAt(i);
                    if(block.getTitle().equals("거래량")){
                        Vector<AbstractGraph> graphs = block.getGraphs();
                        for(int j=0; j<graphs.size(); j++) {
                            AbstractGraph g = (AbstractGraph)graphs.elementAt(j);
                            if(graph.equals(g.getGraphTitle())) {
                                return;
                            }
                        }
                        block.add(graph);
                        block.makeGraphData();
                        setPivotState(pivotState);
                    }
                }
            }
            return;
        }
        if(basic_block!=null){
            Vector<AbstractGraph> graphs = basic_block.getGraphs();
            for(int i=0; i<graphs.size(); i++) {
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                if(graph.equals(g.getGraphTitle())) {
                    return;
                }
            }

            basic_block.add(graph);
            basic_block.makeGraphData();

//            if(dataList!=null && dataList.isVisible())dataList.setData(_cdm);
            reSetUI(true);
            basic_block.setBounds_Pivot(false);//2015.07.17 by lyk - 가격지표 영역 그래프 타이틀 개행 처리
        }
    }
    private int viewH = 0;
    //================================
    // 해당그래프를 추가한 블럭을 추가(일반적인 indicator는 이 함수를 사용하여 블럭추가)
    //================================
    public void addBlock(String graph){
        if(checkBlock(graph) || _cvm.isStandGraph()) return;

        Block cb = makeBlock(0,blocks.size(),graph);
        cb.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb.setBounds(0,(int)(chart_bounds.height()*0.2),chart_bounds.width()-this.TP_WIDTH,(int)((chart_bounds.height()-this.viewH)*0.2)+(int)(chart_bounds.height()*0.2),true);
        addBlock(cb);
//        if(graph.equals("거래량")){
//            cb.add("거래량이동평균");
//        }
        cb.makeGraphData();

        if(COMUtil.isMarketIndicator(graph) && _cdm.getCount()>0) {
            Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
            base11.sendTR(graph);
        }
        //pivot check

        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
        if(m_strSizeInfo != null)
        {
            String[] rateInfos = m_strSizeInfo.split("=");
            String rtnStr = "";
            if(rateInfos.length>1 && rateInfos.length <= m_nMaxIndicatorCount)
            {
                double dTotRateHeight = 0;
                double dHeightRate;
//                for(int i=0; i<rateInfos.length; i++)
//                {
//                    dTotRateHeight += Double.parseDouble(rateInfos[i]);
//                }
//                dTotRateHeight += 1.0/(blocks.size()+2);
                dTotRateHeight = 1.0- 1.0/(blocks.size()+2);
                for(int i=0; i<rateInfos.length; i++)
                {
                    dHeightRate = Double.parseDouble(rateInfos[i]) * dTotRateHeight;
                    rtnStr += String.format("%.6f=", dHeightRate);
                }
                rtnStr += String.format("%.6f", 1.0/(blocks.size()+2));
                m_strSizeInfo = rtnStr;
            }
        }
        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

        setPivotState(pivotState);

    }
    public void addGraph_Storage(String graph){
        if(graph.equals("거래량이동평균"))
        {
            Block block = null;
            if(blocks!=null){
                for(int i=0; i<blocks.size(); i++){
                    block= (Block)blocks.elementAt(i);
                    if(block.getTitle().equals("거래량")){
                        Vector<AbstractGraph> graphs = block.getGraphs();
                        for(int j=0; j<graphs.size(); j++) {
                            AbstractGraph g = (AbstractGraph)graphs.elementAt(j);
                            if(graph.equals(g.getGraphTitle())) {
                                return;
                            }
                        }
                        block.add(graph);
                        block.makeGraphData();
                        setPivotState(pivotState);
                    }
                }
            }

            if(rotate_blocks != null) {
                for(int i=0; i<rotate_blocks.size(); i++){
                    block= (Block)rotate_blocks.elementAt(i);
                    if(block.getTitle().equals("거래량")){
                        Vector<AbstractGraph> graphs = block.getGraphs();
                        for(int j=0; j<graphs.size(); j++) {
                            AbstractGraph g = (AbstractGraph)graphs.elementAt(j);
                            if(graph.equals(g.getGraphTitle())) {
                                return;
                            }
                        }
                        block.add(graph);
                        block.makeGraphData();
                        setPivotState(pivotState);
                    }
                }
            }
            return;
        }
        if(basic_block!=null){
            Vector<AbstractGraph> graphs = basic_block.getGraphs();
            for(int i=0; i<graphs.size(); i++) {
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                if(graph.equals(g.getGraphTitle())) {
                    return;
                }
            }

            basic_block.add(graph);
            basic_block.makeGraphData();
//            if(dataList!=null && dataList.isVisible())dataList.setData(_cdm);
            reSetUI(false);
        }
    }
    public void addBlock_Storage(String graph){
        if(checkBlock(graph) || _cvm.isStandGraph()) return;

        Block cb = makeBlock(0,blocks.size(),graph);
        cb.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb.setBounds(0,(int)(chart_bounds.height()*0.2),chart_bounds.width()-this.TP_WIDTH,(int)((chart_bounds.height()-this.viewH)*0.2)+(int)(chart_bounds.height()*0.2),true);
        addBlock(cb);
//        if(graph.equals("거래량")){
//            cb.add("거래량이동평균");
//        }
        cb.makeGraphData();
//        if(isMarketIndicator(graph)) {
//        	COMUtil.sendTR(graph);
//        }
//        //pivot check
//        setPivotState(pivotState);
        reSetUI(false);
    }
    //================================
    //기본그래프의 드로우 툴의 속성을 바꾼다
    //================================
    public synchronized void changeBlock(String graph){
        String dataTypeName = COMUtil.dataTypeName;
        this.m_strCandleType = graph;

        //2021.07.20 by lyk - 1틱 상태에서 저장/복원시 캔들로 변경되는 현상 수정 >>
//		if(dataTypeName.equals("0") && _cdm.getTerm() == 1) {
//			graph = "라인";
//		}
        //2021.07.20 by lyk - 1틱 상태에서 저장/복원시 캔들로 변경되는 현상 수정 <<

        this.isBlockChanged=true;
        COMUtil.selectedJipyo=graph;

        if(_cvm.isStandGraph())setScrollBar(NOTONLY_DATACHANGE);
        _cvm.setStandGraph(false);
        if(_cvm.preViewNum >= 0)
        {
            setScrollBar(NOTONLY_DATACHANGE);
        }
        AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
        DrawTool basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);

        if(graph.equals("캔들")) graph="일본식봉차트";
        else if(graph.equals("바")) graph="미국식봉차트";
        else if(graph.equals("라인")) graph="종가선차트";
        else if(graph.equals("Log")) graph="로그차트";
        else if(graph.equals("Linear")) graph="선형차트";
        else if(graph.equals("Heikin-Ashi")) graph="Heikin-Ashi";

        Vector<AbstractGraph> graphs = basic_block.getGraphs();
        //2021.07.20 by lyk - 1틱 상태에서 저장/복원시 캔들로 변경되는 현상 수정 >>
//		if(_cdm.getDateType() == 5 && _cdm.getTerm() == 1) {
//			this.changeBlock_NotRepaint("라인");
//		}
        if(_cdm.getDateType() == 5 && _cdm.getTerm() == 1) {
            //2021.04.05 by lyk - kakaopay - 투명캔들차트 >>
            if(graph.equals(COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY))
            {
                basic_dt.setFillUp(false);
                basic_dt.setFillDown(false);
                basic_dt.setFillUp2(false);
                basic_dt.setFillDown2(false);
            }
            else if(graph.equals("일본식봉차트"))
            {
                basic_dt.setFillUp(true);
                basic_dt.setFillDown(true);
                basic_dt.setFillUp2(true);
                basic_dt.setFillDown2(true);
            }
            //2021.04.05 by lyk - kakaopay - 투명캔들차트 <<
            this.changeBlock_NotRepaint("라인");
        }
        //2021.07.20 by lyk - 1틱 상태에서 저장/복원시 캔들로 변경되는 현상 수정 <<
        else if(graph.equals("일본식봉차트")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
            basic_dt.setFillUp(true);
            basic_dt.setFillDown(true);
            basic_dt.setFillUp2(true);
            basic_dt.setFillDown2(true);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY)){ //2021.04.05 by lyk - kakaopay - 투명캔들차트
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
            basic_dt.setFillUp(false);
            basic_dt.setFillDown(false);
            basic_dt.setFillUp2(false);
            basic_dt.setFillDown2(false);
        }else if(graph.equals("미국식봉차트")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(1);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_BAR_OHLC)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(2);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_RANGE_LINE)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(3);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_FLOW)){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(4);
        }else if(graph.equals("종가선차트")){
            basic_dt.setDrawType1(1);
            basic_dt.setDrawType2(0);
            basic_block.setLog(false);
            basic_dt.setLog(false);
            //_cvm.isLog=true;
            for(int i=1; i<graphs.size(); i++){
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                Vector<DrawTool> dts = g.getDrawTool();
                for(int j=0; j<dts.size(); j++){
                    DrawTool dt = (DrawTool)g.getDrawTool().elementAt(j);
                    dt.setLog(false);
                }
            }
            repaintAll();
            return;
        }else if(graph.equals("로그차트")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(0);
            basic_block.setLog(true);
            basic_dt.setLog(true);
            _cvm.isLog=true;
            for(int i=1; i<graphs.size(); i++){
                AbstractGraph g = (AbstractGraph)graphs.elementAt(i);
                Vector<DrawTool> dts = g.getDrawTool();
                for(int j=0; j<dts.size(); j++){
                    DrawTool dt = (DrawTool)g.getDrawTool().elementAt(j);
                    dt.setLog(true);
                }
            }
            repaintAll();
            return;
        }else if(graph.equals("Heikin-Ashi")){
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(5);
            //2020.07.06 by LYH >> 캔들볼륨 >>
        }else if(graph.equals(COMUtil.CANDLE_TYPE_CANDLE_VOLUME)) {
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(6);
        }else if(graph.equals(COMUtil.CANDLE_TYPE_EQUI_VOLUME)) {
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(7);
            //2020.07.06 by LYH >> 캔들볼륨 <<
        }else if(graph.equals(COMUtil.CANDLE_TYPE_STAIR))
        {
            basic_dt.setDrawType1(0);
            basic_dt.setDrawType2(8);
        }
        //setLogChart();
        repaintAll();
        this.reSetUI(true);
    }
    public void addStandBlock(String graph){
        synchronized (this)    {
            if(this.checkBlock(graph)) return;

            setVerticlaMode(false);

            Block cb = makeBlock(0,blocks.size(),graph);
            cb.setBlockType(2);
            cb.setProperties("지표 Data",1,_cvm.getScaleLineType());
            cb.setBounds(0,(int)(chart_bounds.height()*0.2)+viewH,chart_bounds.width()-this.TP_WIDTH,(int)(chart_bounds.height()*0.2),true);
            addBlock(cb);
            _cvm.setStandGraph(true); //2023.09.15 by SJW - 차트유형 설정시 이전 봉 개수 조회 못하는 현상 수정
            setScrollBar(DATA_END);
//			_cvm.setStandGraph(true); //2023.09.15 by SJW - 차트유형 설정시 이전 봉 개수 조회 못하는 현상 수정
            //2015.06.25 by lyk - standGraph 이름 설정
            _cvm.setStandGraphName(graph);
            //2015.06.25 by lyk - standGraph 이름 설정 end
            cb.makeGraphData();
            if(_cvm.Margin_B>=100)_cvm.setMarginB(_cvm.Margin_B-100);

            reSetUI(true);

            for(int i=0; i<blocks.size(); i++) {
                Block block = (Block)blocks.get(i);
                block.setHideDelButton(true);
            }
        }
    }

    public void addLargeBlock(String graph) {
        setVerticlaMode(false);

        Block cb = makeBlock(0,blocks.size(),graph);
        cb.setProperties("지표 Data",1,_cvm.getScaleLineType());
        cb.setBounds(0,(int)(getHeight()*0.2)+viewH,chart_bounds.width()-this.TP_WIDTH,(int)(chart_bounds.height()*0.2)-viewH,true);
        addBlock(cb);
        cb.makeGraphData();
        if(_cvm.Margin_B>=100)_cvm.setMarginB(_cvm.Margin_B-100);
        reSetUI(true);
    }

    //    private void setMinMaxScroll(){
//        int max = _cdm.getCount();
//        int view= _cvm.getViewNum();
//    }
    //================================
    // 팝업메뉴 셋
    //================================
    public void setPopupMenu(boolean simple){
        _cvm.setUseSimpleMenu(simple);
    }

    //================================
    // 툴팁 셋(말풍선... 데이터, 지표 설명)
    //================================
//    private void setBlockColor(int[] back, int[] line){
//        _cvm.setBlockColor(back,line);
//    }
    public void reset(){
        if(_cvm.isOnePage())setScrollBar(DATA_END);
        else setScrollBar(NOTONLY_DATACHANGE);
        try{
            makeGraphData();
            COMUtil.getMainBase().setCountText("");
        }catch(ArrayIndexOutOfBoundsException e){
        }
        repaintAll();
    }

    private void setViewPanel(int x, int y) {
        if (m_bShowToolTip || _cvm.isCrosslineMode) {
            int listNum = this.viewDatas.size();
            int viewP_width = (int) COMUtil.getPixel(150);
            int viewP_height = listNum * (int) COMUtil.getPixel(15) + (int) COMUtil.getPixel(15);

            if (_cvm.chartType == COMUtil.COMPARE_CHART) {
                viewP_width = (int) COMUtil.getPixel(180);
            }

            int leftRightMargin = (int) COMUtil.getPixel(10);
            int topBottomMargin = (int) COMUtil.getPixel(25);

            int left = x - viewP_width - leftRightMargin;
            int top = topBottomMargin;
            int bottom = topBottomMargin;

            if (x < viewP_width + leftRightMargin) {
                left = x + leftRightMargin;
            }

            if (left + viewP_width + leftRightMargin > chart_bounds.width()) {
                left = x + (int) COMUtil.getPixel(2);
            }

            float fBottomButtonWidth = (viewP_width - COMUtil.getPixel(1))/ 2;
            float fBottomButtonHeight = COMUtil.getPixel(34);

            //2019. 07. 30 by hyh - 시세알람차트에서도 닫기 버튼 보이도록 처리 >>
            //if (_cvm.bIsAlarmChart) {
            //	fBottomButtonHeight = 0;
            //}
            //2019. 07. 30 by hyh - 시세알람차트에서도 닫기 버튼 보이도록 처리 <<

            if (_cvm.chartType == COMUtil.COMPARE_CHART || _cvm.bNetBuyChart) {
                viewP.setCompareChart(true); //2016.11.02 by hyh - 스크롤 수치조회창 개발
                viewP_width = (int) COMUtil.getPixel(180);
            }

            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();

            //2016. 1. 14 뉴스차트 핸들러>>
            RectF bounds;
            //2017.09.25 by LYH >> 자산 차트 적용
            if (_cvm.bIsNewsChart) {
                viewP_width = (int) COMUtil.getPixel(140);
                viewP_height = (int) COMUtil.getPixel(70);

                float fLeft = x - (viewP_width / 2);
                if (x < (viewP_width / 2))    //맨 첫데이터일경우는 옆으로 이동하면 수치조회창이 짤리기때문. 왼쪽화살표 배경이미지로 표시하게 조정
                {
                    fLeft = COMUtil.getPixel(2);
                }
                else if (x > (chart_bounds.width() - (viewP_width / 2))) {
                    fLeft = chart_bounds.width() - viewP_width - (int) COMUtil.getPixel(2);
                }

                float fRight = fLeft + viewP_width;
                float fBottom = viewP_height;

                bounds = new RectF(fLeft, param.topMargin - (int) COMUtil.getPixel(5), fRight, param.topMargin + (int) COMUtil.getPixel(5) + fBottom);

//				setBoundsBlurView(bounds);
                viewP.setBounds(bounds);
            }
            //2016. 1. 14 뉴스차트 핸들러<<
            else {
                //컨텐츠는 내용 모두를 표시할 수 있도록 지정
                bounds = new RectF(0, 0, viewP_width, viewP_height);


                //ScrollViewPanel의 사이즈는 화면 사이즈에 맞춰 지정. 화면을 넘어간 경우 스크롤 표시
                if(top + viewP_height + fBottomButtonHeight + bottom >= chart_bounds.height()) {
                    viewP_height = (int) (chart_bounds.height() - top - fBottomButtonHeight - bottom);
                }

                top += param.topMargin;
                left += param.leftMargin;

                bounds = new RectF(left, top, left + viewP_width, top + viewP_height);
                setBoundsBlurView(bounds);
                setContentBoundsBlurView(bounds);
                viewP.setBounds(bounds);
                viewP.setContentBounds(bounds);
            }

            //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 >>
//			float fBottomButtonTop = top + viewP_height + COMUtil.getPixel(1);
//			float fBottomButtonLeft = left;
//
//			if (_cvm.bIsAlarmChart) {
//				m_btnViewPanelAlarm.setVisibility(GONE);
//
//				//2019. 07. 30 by hyh - 시세알람차트에서도 닫기 버튼 보이도록 처리 >>
//				ViewGroup.MarginLayoutParams btnViewPanelCloseLayoutParams = (ViewGroup.MarginLayoutParams) m_btnViewPanelClose.getLayoutParams();
//				btnViewPanelCloseLayoutParams.topMargin = (int) fBottomButtonTop;
//				btnViewPanelCloseLayoutParams.leftMargin = (int) fBottomButtonLeft;
//				btnViewPanelCloseLayoutParams.width = (int) viewP_width;
//				btnViewPanelCloseLayoutParams.height = (int) fBottomButtonHeight;
//
//				m_btnViewPanelClose.setLayoutParams(btnViewPanelCloseLayoutParams);
//				m_btnViewPanelClose.setVisibility(VISIBLE);
//				//2019. 07. 30 by hyh - 시세알람차트에서도 닫기 버튼 보이도록 처리 <<
//			}
//			else if (!_cvm.bIsAlarmChart && !_cvm.bIsOrderChart && COMUtil._mainFrame.bShowBtnOpenAlarmScreen) {
//				//Alarm Button
//				ViewGroup.MarginLayoutParams btnViewPanelAlarmLayoutParams = (ViewGroup.MarginLayoutParams) m_btnViewPanelAlarm.getLayoutParams();
//				btnViewPanelAlarmLayoutParams.topMargin = (int) fBottomButtonTop;
//				btnViewPanelAlarmLayoutParams.leftMargin = (int) fBottomButtonLeft;
//				btnViewPanelAlarmLayoutParams.width = (int) fBottomButtonWidth;
//				btnViewPanelAlarmLayoutParams.height = (int) fBottomButtonHeight;
//
//				m_btnViewPanelAlarm.setLayoutParams(btnViewPanelAlarmLayoutParams);
//				m_btnViewPanelAlarm.setVisibility(VISIBLE);
//
//				//Close Button
//				ViewGroup.MarginLayoutParams btnViewPanelCloseLayoutParams = (ViewGroup.MarginLayoutParams) m_btnViewPanelClose.getLayoutParams();
//				btnViewPanelCloseLayoutParams.topMargin = (int) fBottomButtonTop;
//				btnViewPanelCloseLayoutParams.leftMargin = (int) (fBottomButtonLeft + fBottomButtonWidth + COMUtil.getPixel(1));
//				btnViewPanelCloseLayoutParams.width = (int) fBottomButtonWidth;
//				btnViewPanelCloseLayoutParams.height = (int) fBottomButtonHeight;
//
//				m_btnViewPanelClose.setLayoutParams(btnViewPanelCloseLayoutParams);
//				m_btnViewPanelClose.setVisibility(VISIBLE);
//			}
//			else {
//				//Alarm Button
//				m_btnViewPanelAlarm.setVisibility(GONE);
//
//				//Close Button
//				ViewGroup.MarginLayoutParams btnViewPanelCloseLayoutParams = (ViewGroup.MarginLayoutParams) m_btnViewPanelClose.getLayoutParams();
//				btnViewPanelCloseLayoutParams.topMargin = (int) fBottomButtonTop;
//				btnViewPanelCloseLayoutParams.leftMargin = (int) fBottomButtonLeft;
//				btnViewPanelCloseLayoutParams.width = (int) viewP_width;
//				btnViewPanelCloseLayoutParams.height = (int) fBottomButtonHeight;
//
//				m_btnViewPanelClose.setLayoutParams(btnViewPanelCloseLayoutParams);
//				m_btnViewPanelClose.setVisibility(VISIBLE);
//			}
            //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 <<
        }
    }

    private void setBoundsBlurView(final RectF bounds) {
        COMUtil._chartMain.runOnUiThread(new Runnable() {
            public void run() {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        (int) bounds.width() + 10,
                        (int) bounds.height() + 10);
//				lp.leftMargin = (int) bounds.left - 5;
//				lp.topMargin = (int) bounds.top - 5;
//
//				if(shadowlayout != null) {
//					shadowlayout.setVisibility(View.VISIBLE);
//					shadowlayout.setLayoutParams(lp);
//				}
//
                blurViewlayout.setVisibility(View.VISIBLE);
                lp = new RelativeLayout.LayoutParams(
                        (int) bounds.width() - 30,
                        (int) bounds.height() - 30);
                lp.leftMargin = (int) bounds.left + 13;
                lp.topMargin = (int) bounds.top + 13;
//
//				lp = new RelativeLayout.LayoutParams(
//						(int) bounds.width() - 25,
//						(int) bounds.height() - 25);
//				lp.leftMargin = (int) bounds.left;
//				lp.topMargin = (int) bounds.top;
                blurViewlayout.setLayoutParams(lp);
            }
        });
    }

    private void setContentBoundsBlurView(final RectF bounds) {
        int margintoArrow = (int)COMUtil.getPixel(4);
        int margin = (int)COMUtil.getPixel(4);
        COMUtil._chartMain.runOnUiThread(new Runnable() {
            public void run() {
//				if(shadowlayout != null) shadowlayout.setVisibility(View.VISIBLE);
                blurViewlayout.setVisibility(View.VISIBLE);
                if (viewP.getIsShowRight()) {
                    RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int) bounds.width()-margintoArrow-margin, (int) bounds.height()-margin);
                    rl.setMargins((int)bounds.left+margintoArrow, (int)bounds.top+margin, 0, 0);
                    blurView.setLayoutParams(rl);
                } else {
                    RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int) bounds.width()-margintoArrow-margin, (int) bounds.height()-margin);
                    rl.setMargins((int)bounds.left+margintoArrow, (int)bounds.top+margin, 0, 0);
                    blurView.setLayoutParams(rl);
                }

//				Bitmap chartImg = loadBitmapFromView(blurView);
//				Bitmap chartImg = ((LiveBlurView)blurView).getBlurBitmap();
//				viewP.setChartImg(chartImg);
            }
        });
    }

    public Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    // 기본 뷰패널 위치 설정
    private void setViewPanel_Location(int x, int y) {
        if(m_bShowToolTip || _cvm.isCrosslineMode) {
            //int listNum = this.viewDatas.size()/2;
            int listNum = this.viewDatas.size();
            //2012. 7. 9  십자선데이터 패널  크기조절
            //2012. 8. 10  뷰패널크기  dip단위 적용 : VP09
//			int viewP_width = (int)COMUtil.getPixel(130);

            //태블릿 글자잘림현상 수정
            //by kibum77
            //최대길이 데이터 찾기

            float max = 0f;
            int viewP_height = (int)COMUtil.getPixel(25);
            for(int i=0;i<viewDatas.size();i++)
            {
                Hashtable<String, String> item = (Hashtable<String, String>)viewDatas.get(i);
                Enumeration<String> enumStr = item.keys();
                String name = enumStr.nextElement().toString();
                String value=(String)item.get(name);
                float marginWidth = COMUtil.getPixel_W(20);

                if(name.equals("날짜") || name.equals("가격"))
                {
                    viewP_height += (int)COMUtil.getPixel(19);
                    marginWidth += (viewP.viewP.mesureTextTitle(".") * 3) + viewP.viewP.mesureTextTitle(" ");
                    //2023.06.27 by SJW - 엔벨로프 지표 추가 >>
//				} else if(value.equals("") && (name.equals("주가이동평균") || name.equals("일목균형표") || name.equals("Bollinger Band"))) {
                } else if(value.equals("") && (name.equals("주가이동평균") || name.equals("일목균형표") || name.equals("Bollinger Band") || name.equals("엔벨로프"))) {
                    //2023.06.27 by SJW - 엔벨로프 지표 추가 <<
                    //2021.07.22 by hanjun.Kim - kakaopay - 인포뷰 구분선 라인추가
                    viewP_height += (int)COMUtil.getPixel(30);
                }
                else if(value.equals("") && !name.contains("이평"))
                {
                    viewP_height += (int)COMUtil.getPixel(25);
                }
                else
                {
                    //2023.06.27 by SJW - 인포윈도우 여백 수정 >>
                    if (value.equals("") && name.contains("이평")) {
//						viewP_height -= (int) COMUtil.getPixel(3);
                        viewP_height -= (int) COMUtil.getPixel(2);
                    } else {
                        viewP_height += (int) COMUtil.getPixel(17);
                    }
                    //2023.06.27 by SJW - 인포윈도우 여백 수정 <<
                }

                //2023.06.27 by SJW - 인포윈도우 여백 수정 >>
//				max = Math.max(max, viewP.viewP.mesureTextTitle(name) + viewP.viewP.mesureTextValue(value) + COMUtil.getPixel_W(25) + marginWidth);
                max = Math.max(max, viewP.viewP.mesureTextTitle(name) + viewP.viewP.mesureTextValue(value) + COMUtil.getPixel_W(30) + marginWidth);
                //2023.06.27 by SJW - 인포윈도우 여백 수정 <<
                //2023.11.02 by SJW - 특수차트에서 "미국시간 기준" 텍스트 잘리던 현상 수정 >>
                String strTimeZoneText = COMUtil._mainFrame.mainBase.baseP._chart._cvm.strTimeZoneText;
                if ((_cvm.getStandGraphName().equals("Kagi") || _cvm.getStandGraphName().equals("스윙") || _cvm.getStandGraphName().equals("PnF")) && strTimeZoneText.toLowerCase().startsWith("america")) {
                    max += COMUtil.getPixel_W(15);
                } else if (_cvm.getStandGraphName().equals("렌코") && strTimeZoneText.toLowerCase().startsWith("america")) {
                    max += COMUtil.getPixel_W(5);
                }
                //2023.11.02 by SJW - 특수차트에서 "미국시간 기준" 텍스트 잘리던 현상 수정 <<
            }
            if (viewDatas.size() < 5) {
                viewP_height += (int)COMUtil.getPixel(10);
                if (viewDatas.size() < 3) viewP_height += (int)COMUtil.getPixel(2);
            }

            int viewP_width = (int) max;
            if(_cvm.bInvestorChart) {
                viewP_width = viewP_width + (int)COMUtil.getPixel(20);
            }
            if(_cvm.chartType==COMUtil.COMPARE_CHART || _cvm.bNetBuyChart)
            {
                viewP.viewP.bCompareChart = true;
                viewP_width = (int)COMUtil.getPixel(180);
            }
//			int viewP_height = listNum*(int)COMUtil.getPixel(12) + (int)COMUtil.getPixel(8);
            //태블릿 글자자림/겹침현상 수정
            //by kibum77
            //int viewP_height = listNum*(int)COMUtil.getPixel(25)+ (int)COMUtil.getPixel(14);
            //int viewP_height = listNum*((int) (viewP.getFontSpacing() + COMUtil.getPixel(2)))+ (int)COMUtil.getPixel(14)+ (int)COMUtil.getPixel(10);

            //2021.05.28 by hanjun.Kim - kakaopay - 뷰패널 화살표 붙이기 위한 margin값 조절 >>
            int left = 0; // 차트 x축 시자기점
//			int margintoCrossline = (int)COMUtil.getPixel(10);
            int margintoCrossline = (int)COMUtil.getPixel(0);
            //int top = 0;

            left = x - viewP_width - margintoCrossline;
            //top = y - viewP_height;

            if(x < viewP_width + margintoCrossline) {
                //2012. 8. 10  뷰패널 x 좌표   dip단위 적용 : VP09
                left = x + margintoCrossline;
            }
            if(left<0 || (left+viewP_width)>chart_bounds.width())
            {
                left = 0;
            }

//	    	if(y < viewP_height) {
//	    		//2012. 8. 10  뷰패널 y 좌표   dip단위 적용  : VP09
//	    		top = y + (int)COMUtil.getPixel(10);
//	    	}

            //2012. 8. 13  분할차트에서 뷰패널 호출 위치가 잘못 나오는 현상 : D6
//            final Block cb = (Block)blocks.elementAt(0);

            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
//            final int x = cb.getOutBounds().right+param.leftMargin; //분할차트에서 블럭삭제버튼 위치문제수정.(2011.09.19 by lyk)
//            final int y = cb.getOutBounds().top+param.topMargin;

//     		Rect bounds = new Rect(left, top, left+viewP_width, top+viewP_height);
            //2016. 1. 14 뉴스차트 핸들러>>
            RectF bounds;
            if(_cvm.bIsNewsChart)
            {
                viewP_width = (int)COMUtil.getPixel(172);
                viewP_height = (int)COMUtil.getPixel(18);

                float fL = x-(viewP_width/2);
                if(x < (viewP_width / 2))	//맨 첫데이터일경우는 옆으로 이동하면 수치조회창이 짤리기때문. 왼쪽화살표 배경이미지로 표시하게 조정
                {
                    fL = COMUtil.getPixel(2);
                }
                else if (x > (chart_bounds.width() - (viewP_width / 2)) )
                {
                    fL = chart_bounds.width() - viewP_width - (int)COMUtil.getPixel(2);
                }

                float nR = fL+viewP_width;
                float nB = viewP_height;
                bounds = new RectF(fL, param.topMargin+(int)COMUtil.getPixel(5), nR, param.topMargin+(int)COMUtil.getPixel(5)+nB);
            }
            else
            {
                if(_cvm.getAssetType()>0) {
//					if( _cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL || _cvm.getAssetType() == ChartViewModel.ASSET_LINE || _cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN)
//					//2018.10.18 by LYH >> 버블차트형 추천종목 차트 End
//					{
                    //viewP_width = (int)COMUtil.getPixel(130);

                    //오른쪽화살표

//	            	viewP_height = (int)COMUtil.getPixel(60);	//for test

                    if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL) {
                        if (viewDatas != null && viewDatas.size() > 1) {
                            Hashtable<String, String> item = (Hashtable<String, String>) viewDatas.get(1);
                            Enumeration<String> enumStr = item.keys();
                            String key = enumStr.nextElement().toString();
                            String strValue = (String) item.get(key) + "원";
                            float strWidth = ((ExViewPanel_LeftRightArrow) viewP).getTextWidth(strValue);
                            viewP_width = (int) (strWidth + COMUtil.getPixel_W(20));
                            if (viewP_width < COMUtil.getPixel_W(73))
                                viewP_width = (int) COMUtil.getPixel_W(73);
                        }
                        int nL = x - viewP_width + (int) COMUtil.getPixel_W(16);
                        viewP_height = (int) COMUtil.getPixel_H(45);
                        if (y < viewP_height) {
                            if (nL < 0)    //맨 첫데이터일경우는 옆으로 이동하면 수치조회창이 짤리기때문. 왼쪽화살표 배경이미지로 표시하게 조정
                            {
                                nL = x - (int) COMUtil.getPixel_W(16);


                                ((ExViewPanel_LeftRightArrow) viewP).setViewPanelRightDirection(false, true);
                            } else {

                                ((ExViewPanel_LeftRightArrow) viewP).setViewPanelRightDirection(true, true);
                            }
                            bounds = new RectF(nL + param.leftMargin, param.topMargin + y + (int) COMUtil.getPixel_H(5), nL + viewP_width + param.leftMargin, param.topMargin + y + viewP_height + (int) COMUtil.getPixel_H(5));
                        } else {
                            if (nL < 0)    //맨 첫데이터일경우는 옆으로 이동하면 수치조회창이 짤리기때문. 왼쪽화살표 배경이미지로 표시하게 조정
                            {
                                nL = x - (int) COMUtil.getPixel(16);


                                ((ExViewPanel_LeftRightArrow) viewP).setViewPanelRightDirection(false, false);
                            } else {

                                ((ExViewPanel_LeftRightArrow) viewP).setViewPanelRightDirection(true, false);
                            }
                            bounds = new RectF(nL + param.leftMargin, param.topMargin + y - viewP_height - (int) COMUtil.getPixel_H(5), nL + viewP_width + param.leftMargin, param.topMargin + y - (int) COMUtil.getPixel_H(5));
                        }
                    }
                    else if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN)
                    {
                        if(!(viewP instanceof ExViewPanel_LeftRightArrow))	//예외처리
                            showViewPanel();
                        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
                        if(_cvm.curIndex<0) {
                            viewP.setVisibility(View.GONE);
                            blurView.setVisibility(View.GONE);
                        } else {
                            viewP.setVisibility(View.VISIBLE);
                            blurView.setVisibility(View.VISIBLE);
                        }
                        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End
                        ((ExViewPanel_LeftRightArrow) viewP).setTooltipType(ChartViewModel.ASSET_LINE_MOUNTAIN);

                        //bounds = new RectF((int)COMUtil.getPixel_W(60), 0, (int)COMUtil.getPixel_W(300), (int)COMUtil.getPixel_H(49));
                        bounds = new RectF((int)COMUtil.getPixel_W(60), 0, (int)COMUtil.getPixel_W(300), (int)COMUtil.getPixel_H(43));

                    }
                    else
                    {
                        bounds = new RectF(left+param.leftMargin, (int)COMUtil.getPixel(39), left+viewP_width+param.leftMargin, (int)COMUtil.getPixel(39)+viewP_height);
                    }
//						int nT = y - (viewP_height / 2);
//						if(nT < 0)
//						{
//							nT = 0;
//						}
//						int nR = nL+viewP_width;
//						int nB = nT+viewP_height;
//						bounds = new Rect(nL, nT, nR, nB);
//					}
                    //2017.09.25 by LYH >> 자산 차트 적용 end


                }
                else {
                    int outMargin = (int) COMUtil.getPixel(0);

                    //2021.05.28 by hanjun.Kim - kakaopay -  뷰패널 측면 화살표 >>
                    int leftGab = 0;
                    if (left == x + margintoCrossline) {  // 뷰패널이 crossline오른쪽에 붙는 경우우
                        viewP.showArrowToCrossline(true, true);
//						outMargin = (int) COMUtil.getPixel(20);
//						leftGab = -10;
                    } else {
                        viewP.showArrowToCrossline(true, false);
//						outMargin = (int) COMUtil.getPixel(20);
//						leftGab = -50;
                    }
                    //2021.05.28 by hanjun.Kim - kakaopay - 뷰패널 측면 화살표 <<


                    bounds = new RectF(0, 0, viewP_width+outMargin, viewP_height+outMargin);
                    setContentBoundsBlurView(bounds);
//					viewP.setContentBounds(bounds);

                    //2023.02.09 by SJW - 윈도인포우 위치 설정 >>
//					bounds = new RectF(left + param.leftMargin + leftGab, (int) COMUtil.getPixel(39), left + viewP_width + param.leftMargin + outMargin, (int) COMUtil.getPixel(39) + viewP_height + outMargin);
                    Configuration config = getResources().getConfiguration();
                    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        bounds = new RectF(left + param.leftMargin + leftGab, (int) COMUtil.getPixel(5), left + viewP_width + param.leftMargin + outMargin, (int) COMUtil.getPixel(39) + viewP_height + outMargin);
                    } else {
                        bounds = new RectF(left + param.leftMargin + leftGab, (int) COMUtil.getPixel(30), left + viewP_width + param.leftMargin + outMargin, (int) COMUtil.getPixel(39) + viewP_height + outMargin);
                    }
                    //2023.02.09 by SJW - 윈도인포우 위치 설정 <<
                }
            }
            //2016. 1. 14 뉴스차트 핸들러<<

            viewP.setBounds(bounds);  // 21.06.18 현재 뷰패널 위치설정
            setBoundsBlurView(bounds);
//			RectF cBounds = new RectF(bounds.left + COMUtil.getPixel_W(10), bounds.top + COMUtil.getPixel_W(10), bounds.right - COMUtil.getPixel_W(10)*2, bounds.bottom - - COMUtil.getPixel_W(10)*2);
            viewP.setContentBounds(bounds);

        }
    }
    public void makeGraphData(){
        if(blocks!=null){
            for(int i=0;i<blocks.size();i++){
                Block cb = (Block)blocks.elementAt(i);
                cb.makeGraphData();
            }
        }
    }
    /*
    public int getRequestDataSize(byte[] data){ //보조메시지의 길이가 2에서 4로 변함. nykim 2001.10.31
        if(data.length < 8) return -1;
        String nCnt = new String(data, 0, 4);
        nCnt = nCnt.trim();
        int nCount = Integer.parseInt(nCnt);
        nCnt = new String(data, 4, 4);
        nCnt = nCnt.trim();
        if(useReal)deregRT();
        int nMsgCodeLen = Integer.parseInt(nCnt);
        if(nMsgCodeLen < 0) return -1;
       	int nLength = _cdm.getPacketAllLength();
       	int nRet = (nLength * nCount) + nMsgCodeLen + 8;

	    if(nRet < 8) return -1;
    	return nRet;
    }
    */
    //=========================
    // 보조메세지 처리
    //=========================
    public void processTheCmdMsg(String key, String val){
        if(val==null || val.length()==0) return;
        switch(ChartUtil.getBojoMsgIndex(key)){
            case 0://이전/이후
                if(val.equals("E")){
                    useReal=true;
                    dataAddType = 0;
                }
                else if(val.equals("A")) dataAddType = 1;
                else dataAddType = 2;
                //2013.10.02 by LYH >> 업종 거래량 단위 표시(TDC). <<
                _cdm.m_nVolumeUnit = 1;
                break;
            case 1://UD=0:notype 1:일 2:주 3:월 4:분 5:틱 6:Text
                _cdm.setDateType(Integer.parseInt(val));
                break;
            case 2://실시간간격
                int tmp=0;
                if(!val.equals("%d")){
                    try {
                        tmp = Integer.parseInt(val);
//						if(tmp>=100 && 4 == _cdm.getDataType())	//분일때 값이 100이상으로 오면 (삼성증권에서만  ex: 100분이면 140으로 값이
//						{
//							tmp = ((int)tmp/100)*60 + tmp%100;
//						}
                    } catch(Exception e) {

                    }
                    _cdm.setRealAddTerm(tmp);
                }
                break;
            case 3://RDORT=0:none 1:date 2=time 3=date+time -> 안쓰기로 함
                break;
            case 4://RDATET=YYMMDD 등 쓰일 데이타의 타입
            {
                //if(_cdm.getDateType()==4 || _cdm.getDateType()==5)
                //    _cdm.setDateFormat(ChartUtil.getPacketFormatIndex("HHMMSS"));
                //else
                int index = val.indexOf(":");
                if(index != -1) {
                    val = new String(val.substring(index+1));
                }
                _cdm.setDateFormat(ChartUtil.getPacketFormatIndex(val));
            }
            break;
            case 5://PREVPRICE=전일종가등을 넣을때 씀 ->  패킷명:전일기준가
            {
                _cdm.codeItem.strGijun = val;
//            	int idx1 = val.indexOf(":");
//            	int idx2 = val.indexOf("|");
//            	String strPriceData = val.substring(idx1+1, idx2);
//            	String[] values = strPriceData.split(",");
//	        	_cdm.codeItem.strHighest = values[1].trim();
//	        	_cdm.codeItem.strLowest = values[2].trim();
//	        	_cdm.codeItem.strGijun = values[0].trim();
            }
            break;
            case 9://BOUNDMARK=패킷명:상한:하한선을 지정한다
            {
                String[] values = val.split(":");
                int idx2 = values[1].indexOf(",");
                _cdm.codeItem.strHighest = values[1].substring(0,idx2).trim();
                _cdm.codeItem.strLowest = values[1].substring(idx2+1).trim();
                //2014. 2. 3 보조메세지에 상하한가 기준선이 없을 경우 처리 >>
                if(values.length >= 5)
                {
                    _cdm.codeItem.strGijun = values[4].trim();
                }
                //2014. 2. 3 보조메세지에 상하한가 기준선이 없을 경우 처리 <<
                //String[] bound=parser.convertArray(val.substring(val.indexOf(":")+1),":",false);
                //_cdm.setBoundData(bound);
            }
            break;
            case 10:
                if(val.equals("0")){//실시간 사용 안함
                    useReal = false;
                }else{//사용함
                    useReal = true;
                }
                break;
            case 6://FUNDSTARTPOINT=펀트차트에서 시장/펀드분산 시 초기 기준점
                break;
            case 7://FUNDENDPOINT=펀트차트에서 시장/펀드분산 시 마지막 기준점
                break;
            case 8://USEPACKET=사용가능한 패킷의 수를 지정한다
                break;
            case 11://PRICEFORMAT=가격의 data type을 정한다. 20030506 ykLee add.
                //String[][] dataInfo = getPacketInfo(val);
                //_cdm.setPacketData(dataInfo);
                if((new String(val.substring(0,1))).equalsIgnoreCase("x")) {
                    val = "× "+new String(val.substring(1));
                }
                _cdm.setPriceFormat(val); //가격포맷 적용안한 상태 (임시)
                break;
            case 12:
                if(_cdm!=null) {
                    _cdm.codeItem.strMarket = val;
//                    [[CommonConst sharedSingleton] setMarketName:val]; //lyk add
                    COMUtil.market = val;
                }
                break;
            //2013.09.17 by LYH>> 해외종목차트 등 데이터 소수점 가변 처리.
            case 13://RESETPACKET=시가:20:x0.01|
            {
                String[] values = val.split("\\|");
                if(values.length>0)
                {
                    String[] values2 = values[0].split(":");
                    if(values2.length>=3)
                    {
                        String strPoint = values2[2];
                        strPoint = strPoint.replace("x", "");
                        try{
                            double a = Double.parseDouble(strPoint);
                            double b = Math.log10(a);
                            setPriceFormat(10, (int)Math.abs(b), 0);
                        }catch(Exception e)
                        {

                        }
                    }
                }
            }
            break;
            //2013.09.17 by LYH<<
            //2013.10.02 by LYH >> 업종 거래량 단위 표시(TDC).
            case 14://TDC=1000:기본거래량,|
            {
                String[] values = val.split("\\|");
                if(values.length>0)
                {
                    String[] values2 = values[0].split(":");
                    if(values2.length>=2)
                    {
                        String strUnit = values2[0];
                        String strPacket = values2[1];

                        if(strPacket.startsWith("기본거래량"))
                        {
                            try
                            {
                                _cdm.m_nVolumeUnit = Integer.parseInt(strUnit);
                            }
                            catch(Exception e)
                            {

                            }
                        }
                    }
                }
            }
            break;
            //2014.04.17 by LYH >> 멀티틱 조회 시점 몇 틱인지 알려줌.
            case 15://UTEC : tic count
                _cdm.setTickCount(Integer.parseInt(val));
                break;
            //2014.04.17 by LYH << 멀티틱 조회 시점 몇 틱인지 알려줌.
            //2013.10.02 by LYH <<
            //2014.05.23 by LYH >> 날짜 구분선 시작 시간 포함.
            case 16://OPENTIME
                _cdm.setOpenTime(val);
                break;
            //2014.05.23 by LYH << 날짜 구분선 시작 시간 포함.
        }
    }
    /*
    private String[][] getPacketInfo(String val) {
        String[][] temp = new String[6][4];
        StringTokenizer st = new StringTokenizer(val, "|");
        int cnt = 0;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if(cnt==0) str += ":유";
            else str += ":무";
            StringTokenizer st2 = new StringTokenizer(str, ":");
            int cnt2 = 0;
            while(st2.hasMoreTokens()) {
                String str2 = st2.nextToken();
                if((str2.substring(0,1)).equalsIgnoreCase("x")) {
                    str2 = "× "+str2.substring(1);
                }
                temp[cnt][cnt2] = str2;
                cnt2++;
            }
            cnt++;
        }
        temp[5][0] = "기본거래량";
        temp[5][1] = "15";
        temp[5][2] = "× 1";
        temp[5][3] = "무";
        return temp;
    }
    */
    public void update(Canvas g){
        this.invalidate();
    }
    //====================================
    // NeoChart그리기
    // 1. 더블버퍼링
    // 2. 클리어
    // 3. block repaint
    //====================================
    public void repaintAll(){
        setLogChart();
        if(COMUtil.apiMode) {
            invalidate();
        }
        else
        {
            mHandler.post(new Runnable() {
                public void run() {
                    invalidate();
                }
            });
        }
        //isDraw = true;
//        mHandler.post(new Runnable() {
//            public void run() {
//              invalidate();
//            }
//          });
        //invalidate();
//    	if(mGl != null)
//    	{
//    		if (mEglSurface == null && mEglContext == null && mEglContext == null)
//    			return;
//    		//bSizeChanged = true;
//    		//if (bSizeChanged) {
//    			createSurface(getHolder());
//
//        	//	bSizeChanged = false;
//
////        		//배경화면 초기화
////        		gl.glClearColor(1.0f, 1.0f, 1.0f, 0f);
////        		gl.glClearDepthf(1.0f);
////        		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
////        		mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
//            //}
//
//    		draw(mGl);
//    		mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
//    		//this.requestRender();
//    	}
//
    }

    //로드한 차트의 분석툴을 그려준다.
    private void drawAnalTool(Canvas gl){
        if(_cvm==null) return;
        if(_cvm.isStandGraph())return;

        if(this.analTools!=null){
            try {
                AnalTool at;
                int len = this.analTools.size();
                for(int i=0;i<len;i++){
                    at = (AnalTool)analTools.elementAt(i);
                    at.draw(gl);
                    _cvm.setLineWidth(1);   //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        //2015. 1. 29 가상매매연습기 매도/매수 표시>>
        if(this.analTradeTools!=null){
            try {
                AnalTool at;
                int len = this.analTradeTools.size();
                for(int i=0;i<len;i++){
                    at = (AnalTool)analTradeTools.elementAt(i);
                    at.draw(gl);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            basic_block.stateSize = false;
        }
        //2015. 1. 29 가상매매연습기 매도/매수 표시<<

        //2021.04.21 by hanjun.Kim - kakaopay - 배지아이콘 표시 >>
        if (aBadgeTools != null) {
            try {
                ABadgeTool at;
                int len = aBadgeTools.size();
                for (int i=0; i<len; i++) {
                    at = aBadgeTools.elementAt(i);
                    at.draw(gl);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //2021.04.21 by hanjun.Kim - kakaopay - 배지아이콘 표시 >>

        //2015. 2. 24 차트 상하한가 표시>>
        if(this.analUpperLowerTools!=null){
            try {
                AnalTool at;
                int len = this.analUpperLowerTools.size();
                for(int i=0;i<len;i++){
                    at = (AnalTool)analUpperLowerTools.elementAt(i);
                    at.draw(gl);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //2015. 2. 24 차트 상하한가 표시<<
    }

    private void addStrageAnalTool() {
        if(_cvm.chartType==COMUtil.COMPARE_CHART)
            return;

        //2013. 11. 21 추세선 종목별 저장>>
//      Vector analD = _cvm.analItem;
        Vector analD = null;
        Vector vTemp = (Vector)loadAnalToolBySymbol();

        //2014. 1. 10 종목별 추세선기능 종합차트 / 전체차트 에서만 동작하게 수정 >>
//    	if(null != vTemp)
        boolean bSave = false;

        //2017.04.19 종목코드에 따라 저장된 분석툴 로드 >>
        if(COMUtil._mainFrame.m_storageSymbol != null)
        {
            if(!COMUtil._mainFrame.m_storageSymbol.equals(COMUtil.symbol)) {
                _cvm.analItem = null;
                if(null != vTemp)
                {
                    _cvm.analItem = (Vector)vTemp.get(0);
                }
                COMUtil._mainFrame.m_storageSymbol = null;
            }
        }
        //2017.04.19 종목코드에 따라 저장된 분석툴 로드 <<

        if(null != vTemp && COMUtil.isAnalToolbar && !COMUtil.getSendTrType().equals("storageType"))	//2014. 4. 7 저장/불러오기 시에는 종목별 추세선 저장이 아닌 현재 추세선상태 저장
        //2014. 1. 10 종목별 추세선기능 종합차트 / 전체차트 에서만 동작하게 수정 <<
        {
            _cvm.analItem = (Vector)vTemp.get(0);
//    		analD = _cvm.analItem;	//2015. 1. 13 저장불러오기 했을 때 분석툴바 불러오는지 확인
        }
        else
        {
            if(_cvm.analItem!=null && _cvm.analItem.size()>0)
                bSave = true;
        }
        analD = _cvm.analItem;		//2015. 1. 13 저장불러오기 했을 때 분석툴바 불러오는지 확인
        //2013. 11. 21 추세선 종목별 저장<<

        int analCnt=0;
        if(analD!=null) {
            analCnt = analD.size();
        }

        if(analD!=null && analCnt>0) {
            Vector<Block> blocks = this.getBlocks();
            if(blocks==null || blocks.size()==0) return;
            Block cb = (Block)blocks.get(0);
            Vector<AnalTool> insData = new Vector<AnalTool>();
            for(int i=0; i<analCnt; i++) {
                Hashtable tmpDic = (Hashtable)analD.get(i);
                String title = (String)tmpDic.get("title");
                Vector tmpItem = (Vector)tmpDic.get("location");
                String strUsePrice = (String)tmpDic.get("useprice");	//2015. 1. 13 분석툴 수정기능 및 자석기능 추가
                AnalTool at = null;
                if(title.equals("추세선")) {
                    at = new AChuseLineTool(cb);
                } else if(title.equals("수평선")) {
                    at = new AHLineTool(cb);
                } else if(title.equals("수직선")) {
                    at = new AVLineTool(cb);
                } else if(title.equals("사각형")) {
                    at = new ARectTool(cb);
                } else if (title.equals("십자선")) {
                    at = new ACrossLineTool(cb);
                } else if (title.equals("원형")) {
                    at = new AOvalTool(cb);
                } else if (title.equals("삼등분선")) {
                    at = new ADivTTool(cb);
                } else if (title.equals("사등분선")) {
                    at = new ADivFTool(cb);
                } else if (title.equals("스피드라인")) {
                    at = new ASpeedLineTool(cb);
                } else if (title.equals("갠팬")) {
                    at = new AGannFanTool(cb);
                } else if (title.equals("하향갠팬")) {
                    at = new AGannFanTool(cb);
                    ((AGannFanTool)at).setIsDown(true);
                } else if (title.equals("갠그리드")) {
                    at = new AGannGridTool(cb);
                } else if (title.equals("피보나치조정대")) {
                    at = new APivoRetTool(cb);
                } else if (title.equals("피보나치시간대")) {
                    at = new APivoTimeZoneTool(cb);
                } else if (title.equals("앤드류스피치포크")) {
                    at = new AAndrewsPiTool(cb);
                } else if (title.equals("피보나치아크")) {
                    at = new APivoArcTool(cb);
                } else if (title.equals("피보나치팬")) {
                    at = new APivoFanTool(cb);
                } else if (title.equals("갠라인")) {
                    at = new AGannLineTool(cb);
                } else if (title.equals("가속저항호")) {
                    at = new ASpeedArcTool(cb);
                } else if (title.equals("가속저항팬")) {
                    at = new ASpeedFanTool(cb);
                } else if (title.equals("사이클구간")) {
                    at = new ACycleLinesTool(cb);
                } else if (title.equals("엘리어트파동선")) {
                    at = new AElliotTool(cb);
                } else if (title.equals("직선회귀선")) {
                    at = new ARegressionLineTool(cb);
                } else if (title.equals("직선회귀채널")) {
                    at = new ARegressionLinesTool(cb);
                } else if (title.equals("삼각형")) {
                    at = new ATriangleTool(cb);
                } else if (title.equals("좌측연장선")) {
                    at = new ALeftLineTool(cb);
                } else if (title.equals("우측연장선")) {
                    at = new ARightLineTool(cb);
                } else if (title.equals("양측연장선")) {
                    at = new ABothLineTool(cb);
                } else if (title.equals("평행선")) {
                    at = new AParallelTool(cb);
                } else if (title.equals("수직구간")) {
                    at = new APerpendicularTool(cb);
                } else if (title.equals("각")) {
                    at = new ADegreeTool(cb);
                } else if (title.equals("목표치NT")) {
                    at = new ATargetNtTool(cb);
                } else if (title.equals("피보나치목표치")) {
                    at = new AFiboTargetTool(cb);
                } else if (title.equals("대각선")) {
                    at = new ADiagonalTool(cb);
                } else if (title.equals("그리기")) {
                    at = new APencilLineTool(cb);
                } else if (title.equals("가격변화선")) {
                    at = new APeriodReturnLineTool(cb);
                }

                if(at==null) break;
                for(int k=0; k<tmpItem.size(); k++) {
                    String[] tmpDic2 = (String[])tmpItem.get(k);

                    try {
                        double px = Double.parseDouble(tmpDic2[0]);
                        double py = Double.parseDouble(tmpDic2[1]);
                        at.addAnalInfo(px, py);
                    }catch(NumberFormatException e) {

                    }

                }

                //2016.12.19 - 분석툴 굵기,색상 추가
                String strLineThick = (String)tmpDic.get("lineThick");
                String strColor = (String)tmpDic.get("color");

                try {
                    if(strLineThick  != null)
                    {
                        at.setLineT(Integer.parseInt(strLineThick));
                    }

                    if(strColor  != null)
                    {
                        String[] colorInfo = strColor.split(":");
                        if(colorInfo.length>=3)
                        {
                            int color[] = {Integer.parseInt(colorInfo[0]), Integer.parseInt(colorInfo[1]), Integer.parseInt(colorInfo[2])};
                            at.setColor(color);
                        }
                    }
                }catch (Exception e){}
                //2016.12.19 - 분석툴 굵기,색상 추가 end
                //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
                if(strUsePrice != null && strUsePrice.equals("1"))
                    at.setUsePrice(true);
                //_cvm.setUsePrice(true);
                //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<

                insData.add(at);
            }

            this.analTools=insData;

            //2014. 4. 7 차트불러오기 후 추세선 종목별 저장 한번더 적용시켜주기>>
            if(bSave)
                saveAnalToolBySymbol();
            //2014. 4. 7 차트불러오기 후 추세선 종목별 저장 한번더 적용시켜주기<<

            if(!COMUtil.getSendTrType().equals("requestAddData")) {
                _cvm.analItem = null; //초기화.
            } else {
                COMUtil.setSendTrType("");
            }

        }
    }

    //    Hashtable viewpDatas=null;
    Vector<Hashtable<String, String>> viewDatas=null;
    RectF tmpRect = new RectF();
    private void drawAnalTool_MouseMove(Canvas gl){
        RectF bounds = _cvm.getBounds();
        tmpRect.set(xscale.getBounds().left, bounds.top, xscale.getBounds().right, bounds.bottom);

        //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 >>
//		if(!tmpRect.contains(curPoint.x, curPoint.y)) {
//			//viewP.setVisibility(View.GONE);
//			//2012. 10. 19  십자선 버튼 최초 눌렀을 보이게 처리 : VP10
//			if(_cvm.isCrosslineMode)
//			{
//				curPoint.x = tmpRect.right;
//				curPoint.y = tmpRect.top;
//			}
//			else
//			{
//				return;
//			}
//		}
        //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 <<

        //2019. 04. 17 by hyh - 시세알람차트 개발. 초기값은 가장 우측 봉으로 설정 >>
        if (_cvm.bIsAlarmChart) {
            //알람 설정값이 존재하는 경우, 해당 좌표로 이동
            if (!strAlarmValue.equals("")) {
                String[] arrAlarmValues = strAlarmValue.split(";");

                if (arrAlarmValues.length == 0) {
                    this.hideViewPanel();
                    return;
                }

                String strPeriod = arrAlarmValues[0];
                String strDate = arrAlarmValues[1];
                String strPrice = arrAlarmValues[2];

                //=====================
                // Set _cvm.index
                //=====================
                int nSelectedIndex = -1;

                try {
                    String[] dateData = _cdm.getStringData("자료일자");

                    if (dateData.length > 0) {
                        strDate = strDate.replaceAll(" ", "");
                        strDate = strDate.replaceAll(":", "");
                        strDate = strDate.replaceAll("-", "");

                        int nTotCount = dateData.length;
                        for (int i = 0; i < nTotCount; i++) {
                            if (Double.parseDouble(strDate) <= Double.parseDouble(dateData[i])) {
                                nSelectedIndex = i;
                                break;
                            }
                        }
                    }

                    if (nSelectedIndex >= 0) {
                        _cvm.curIndex = nSelectedIndex;

                        int viewNum = _cvm.getViewNum();
                        int startIdx = _cvm.getIndex();
                        if (nSelectedIndex - startIdx < 0) {
                            _cvm.setIndex(nSelectedIndex);
                        }
                        else if (nSelectedIndex > startIdx + viewNum) {
                            _cvm.setIndex(nSelectedIndex - viewNum + 1);
                        }
                    }
                } catch (Exception e) {

                }

                //=====================
                // Set curPoint.x
                //=====================
                if (nSelectedIndex > 0) {
                    curPoint.x = (int) xscale.getDateToX(nSelectedIndex);
                }

                if (curPoint.x > tmpRect.right) {
                    curPoint.x = (int) (tmpRect.right - xscale.xfactor / 2);
                }

                if (curPoint.x < tmpRect.left) {
                    curPoint.x = (int) (tmpRect.left + xscale.xfactor / 2);
                }

                //=====================
                // Set curPoint.y
                //=====================
                try {
                    strPrice = strPrice.replaceAll("'", "");
                    strPrice = strPrice.replaceAll(",", "");

                    double price = Double.parseDouble(strPrice);

                    YScale yScale = basic_block.getYScale()[0];
                    if (yScale.format_index == 14 || yScale.format_index == 15 || yScale.format_index == 16) {
                        price = price * 10000;
                    }

                    curPoint.y = (int) yScale.priceToY(price);
                }
                catch (Exception e) {
                }

                bIsInitAlarmChart = true;
                strInitAlarmPrice = strPrice;
            }
            //알람 설정값이 존재하지 않는 경우, 초기값은 가장 우측 봉으로 설정
            else if (curPoint.x <= 0 && curPoint.y <= 0) {
                double[] fClose = _cdm.getSubPacketData("종가");

                if (fClose != null) {
                    int nLastIndex = fClose.length - 1;
                    double price = fClose[nLastIndex];

                    YScale yScale = basic_block.getYScale()[0];
                    if (yScale.format_index == 14 || yScale.format_index == 15 || yScale.format_index == 16)
                        price = price * 10000;

                    curPoint.x = (int) (tmpRect.right - xscale.xfactor / 2);
                    curPoint.y = (int) yScale.priceToY(price);

                    bIsInitAlarmChart = true;
                    strInitAlarmPrice = _cdm.codeItem.strPrice;
                }
            }
        }
        else {
            if (curPoint.x > tmpRect.right) {
                curPoint.x = (int) (tmpRect.right - xscale.xfactor / 2);
            }

            if (curPoint.x < tmpRect.left) {
                curPoint.x = (int) (tmpRect.left + xscale.xfactor / 2);
            }
        }
        //2019. 04. 17 by hyh - 시세알람차트 개발. 초기값은 가장 우측 봉으로 설정 <<

//		if(_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN && _cvm.isCrosslineMode) {
//			double[] fClose = _cdm.getSubPacketData("수익률");
//
//			if (fClose != null) {
//				try {
//					AbstractGraph graph = null;
//					Block block = (Block)blocks.get(0);
//					int blGraphCnt = block.getGraphs().size();
//					for (int j = 0; j < blGraphCnt; j++) {
//						graph = (AbstractGraph) block.getGraphs().get(j);
//						for (int k = 0; k < graph.getDrawTool().size(); k++) {
//							DrawTool dt = (DrawTool) graph.getDrawTool().get(k);
//							if(dt.getTitle().equals("수익률")) {
//								int idx = getXToDate(curPoint.x);
//								double price = fClose[idx];
//								curPoint.y = (int)dt.calcy(price);
//							}
//						}
//					}
//				} catch (Exception e) {
//
//				}
//			}
//			if(circleRelative != null) {
//				layout.removeView(circleRelative);
//				circleRelative = null;
//			}
//
//			iv_tooltip_circle = new ImageView(context);
//			String btnImg = "img_tooltip_circle_oneq";
//
//			int layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
//			iv_tooltip_circle.setBackgroundResource(layoutResId);
//			iv_tooltip_circle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//
//			circleRelative = new RelativeLayout(context);
//			RelativeLayout.LayoutParams circleRelativeParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(10), (int) COMUtil.getPixel(10));
//			circleRelativeParams.leftMargin = curPoint.x;
//			circleRelativeParams.topMargin = curPoint.y;
//			circleRelative.setLayoutParams(circleRelativeParams);
//			circleRelative.addView(iv_tooltip_circle);
//			layout.addView(circleRelative);
//		}

        PointF p = pressPoint;
        int idx = getXToDate(curPoint.x);
        int x = (int)xscale.getDateToX(idx);
        if(idx<0) return;
        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
        //_cvm.curIndex=idx;
        if(_cvm.getAssetType() != ChartViewModel.ASSET_LINE_MOUNTAIN)
            _cvm.curIndex=idx;
        //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End
        String[] datas = _cdm.getFormatDatas(idx);
        if (datas == null || datas.length < 1) return;

        float py=0;
        if(COMUtil.isCrossLineJongga()) {//2021.01.07 by HJW - 십자선 종가 따라가기 옵션화
            if (basic_block.getYScale() != null) {
                YScale yscale = basic_block.getYScale()[0];
                double close = 0;
                if (_cvm.chartType != COMUtil.COMPARE_CHART) {
                    String strPrice = _cdm.getData("종가", idx);
                    if (strPrice.length() > 0) {
                        close = Double.parseDouble(strPrice);
                        if (yscale.format_index == 14 || yscale.format_index == 15 || yscale.format_index == 16)
                            close = close * 10000;
                    }
                }
                try {
                    py = yscale.priceToY(close);
                } catch (Exception e) {
                    //2012. 8. 10   앱이 시작되었을때  '삼선전환도' 일때  차트영역 터치하면 죽는 현상 :  C02
                    //    		System.out.println(e.getMessage());
                }

                if (_cvm.chartType == COMUtil.COMPARE_CHART || _cvm.bIsAlarmChart) {
                    py = curPoint.y;
                }
            }
        } else {
            py = curPoint.y;
        }
        //2021.01.07 by HJW - 십자선 종가 따라가기 옵션화

        // 2017.08.09 by pjm 분석툴 선택시 수치조회창, 십자선 안뜨게 처리 >>
        if(_cdm.getCount()>0) {
            showViewPanel();
        }
//			if(selectedAnalTool(p))	// 2017.07.06 by pjm 분석툴 선택시 수치조회창, 십자선 안뜨게 처리
//				return;
        // 2017.08.09 by pjm 분석툴 선택시 수치조회창, 십자선 안뜨게 처리 <<

        if(m_bShowToolTip || _cvm.isCrosslineMode) {
            if (viewP !=null) {
                viewDatas = new Vector<Hashtable<String, String>>();
                Block block;
                AbstractGraph graph;
                int blCnt = blocks.size();
                for(int i=0; i<blCnt; i++) {
                    block = (Block)blocks.get(i);
                    //2015.06.25 by lyk - stand block 처리
                    if(block.getBlockType()==Block.STAND_BLOCK) {
                        int blGraphCnt = block.getGraphs().size();
                        viewDatas.clear();
                        for(int j=0; j<blGraphCnt; j++) {
                            graph=(AbstractGraph)block.getGraphs().get(j);
                            for(int k=0; k<graph.getDrawTool().size(); k++) {
                                DrawTool dt = (DrawTool)graph.getDrawTool().get(k);
                                //if(dt.getTitle().equals("Kagi") || dt.getTitle().equals("스윙")) {
                                //2021.10.28 by JHY - 렌코 이평 추가 >>
//                                if(xscale!=null && dt.getTitle().equals("렌코")) {
                                if(xscale!=null && dt.getTitle().contains("렌코")) {
                                    double[] closeData = _cdm.getSubPacketData("variable" + "_close");
                                    String[] dateData = _cdm.getStringData("variable" + "_자료일자");
                                    if(closeData==null) return;
                                    int cnt = closeData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if (dIndex >= cnt) dIndex = cnt - 1;
//        							viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    if (dt.getTitle().startsWith("렌코이평")) {
                                        if (k == 1) {
                                            viewDatas.add(getViewPanelItem("렌코이동평균", ""));
                                        }
                                        String rkSTR = dt.subTitle.replaceAll("렌코", "");
                                        viewDatas.add(getViewPanelItem(rkSTR, dt.getFormatData(dIndex)));
                                    } else {

                                        if (dateData != null) {
                                            //2021.10.28 by JHY - "날짜"텍스트 제거 >>
//											viewDatas.add(getViewPanelItem("날짜", dateData[dIndex]));
                                            //2024.05.09 by SJW - 인포윈도우 "렌코차트" 날짜 인덱스 변경 >>
//											String strDate = datas[0].replaceAll("/", "");
//											viewDatas.add(getViewPanelItem(strDate, ""));
                                            viewDatas.add(getViewPanelItem(this.getFormatDateString(dateData[dIndex]), ""));
                                            //2024.05.09 by SJW - 인포윈도우 "렌코차트" 날짜 인덱스 변경 <<
                                            //2021.10.28 by JHY - "날짜"텍스트 제거 <<
                                        }

                                        if (closeData != null)
                                            viewDatas.add(getViewPanelItem("가격", _cdm.getFormatData("variable" + "_close", dIndex)));
                                    }
                                    //2021.10.28 by JHY - 렌코 이평 추가 <<
                                }else if(dt.getTitle().equals("역시계곡선")) {
                                    double[] closeData=_cdm.getSubPacketData(dt.getPacketTitle());
                                    String[] dateData=_cdm.getStringData(dt.getPacketTitle()+"_거래량스트링");

                                    int cnt = closeData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if(dIndex>=cnt)dIndex=cnt-1;

//        							viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    viewDatas.add(getViewPanelItem("날짜", datas[0]));
                                    if(dateData!=null) {
                                        try {
                                            String s = dateData[dIndex];
                                            if(s.indexOf("e")!=-1 || s.indexOf("E")!=-1) { //지수형 데이터의 처리
                                                try{
                                                    Double d = Double.valueOf(s.trim());
                                                    s = ""+d.intValue();
                                                } catch(Exception e) {
//							                        s = "0";
                                                    //System.out.println(e);
                                                }
                                            }
                                            s=COMUtil.format(s,0,3);
                                            viewDatas.add(getViewPanelItem("거래량", s));
                                        } catch (Exception e) {

                                        }
                                    }

                                    if(closeData!=null) {
                                        try {
                                            String price = ChartUtil.getFormatedData(String.valueOf(closeData[dIndex]), _cdm.getPriceFormat(), _cdm);
                                            viewDatas.add(getViewPanelItem("가격", price));
                                        } catch (Exception e) {

                                        }
                                    }

                                }else if(dt.getTitle().equals("삼선전환도")) {
                                    String[] dateData=_cdm.getStringData("variable"+"_자료일자");
                                    double[] closeData=_cdm.getSubPacketData("variable"+"_close");
                                    double[] openData=_cdm.getSubPacketData(dt.getPacketTitle()+"_open");
                                    double[] highData=_cdm.getSubPacketData(dt.getPacketTitle()+"_high");
                                    if(closeData==null) return;

                                    int cnt = dateData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if(dIndex>=cnt)dIndex=cnt-1;

                                    //viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    if(dateData!=null)
                                        viewDatas.add(getViewPanelItem("날짜", dateData[dIndex]));
                                    if(openData!=null)
                                    {
                                        if(openData[dIndex] == closeData[dIndex])
                                            viewDatas.add(getViewPanelItem("시가", ChartUtil.getFormatedData(String.valueOf(highData[dIndex]), _cdm.getPriceFormat(), _cdm)));
                                        else
                                            viewDatas.add(getViewPanelItem("시가", ChartUtil.getFormatedData(String.valueOf(openData[dIndex]), _cdm.getPriceFormat(), _cdm)));
                                    }
                                    if(closeData!=null)
                                        viewDatas.add(getViewPanelItem("종가", ChartUtil.getFormatedData(String.valueOf(closeData[dIndex]), _cdm.getPriceFormat(), _cdm)));
                                    //viewDatas.add(getViewPanelItem("자료일자", datas[0]));
                                }else if(dt.getTitle().equals("PnF")) {
                                    String[] dateData=_cdm.getStringData("variable"+"_자료일자");
                                    double[] closeData=_cdm.getSubPacketData("variable"+"_close");
                                    double[] openData=_cdm.getSubPacketData("variable"+"_open");
                                    if(closeData==null) return;

                                    int cnt = dateData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if(dIndex>=cnt)dIndex=cnt-1;

                                    //viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    if(dateData!=null) {
                                        // 2021.11.10 by JHY - 날짜 타이틀 앞으로 >>
//										viewDatas.add(getViewPanelItem("",this.getFormatDateString(dateData[dIndex]))); //날짜 타이틀 사용안함
                                        viewDatas.add(getViewPanelItem(this.getFormatDateString(dateData[dIndex]),""));
                                        // 2021.11.10 by JHY - 날짜 타이틀 앞으로 <<
                                    }

                                    if(openData!=null)
                                    {
                                        viewDatas.add(getViewPanelItem("시가", ChartUtil.getFormatedData(String.valueOf(openData[dIndex]), _cdm.getPriceFormat(), _cdm)));
                                    }
                                    if(closeData!=null)
                                        viewDatas.add(getViewPanelItem("종가", ChartUtil.getFormatedData(String.valueOf(closeData[dIndex]), _cdm.getPriceFormat(), _cdm)));
                                    //viewDatas.add(getViewPanelItem("자료일자", datas[0]));
                                }else if(dt.getTitle().equals("Kagi") || dt.getTitle().equals("스윙")) {
                                    double[] closeData=_cdm.getSubPacketData("variable"+"_close");
                                    String[] dateData=_cdm.getStringData("variable"+"_자료일자");
                                    if(closeData==null) return;
                                    int cnt = closeData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if(dIndex>=cnt)dIndex=cnt-1;

//        							viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    if(dateData!=null)
                                        // 2021.11.10 by JHY - 날짜 타이틀 앞으로 >>
                                        //viewDatas.add(getViewPanelItem("",this.getFormatDateString(dateData[dIndex]))); //날짜 타이틀 사용안함
                                        viewDatas.add(getViewPanelItem(this.getFormatDateString(dateData[dIndex]), ""));
                                    // 2021.11.10 by JHY - 날짜 타이틀 앞으로 <<

                                    if(closeData!=null)
                                        viewDatas.add(getViewPanelItem("가격", ChartUtil.getFormatedData(String.valueOf(closeData[dIndex]), _cdm.getPriceFormat(), _cdm)));

                                }
                                //2020.06.08 by JJH >> PnF 차트 데이터 창 추가 start
                                else{
                                    double[] closeData=_cdm.getSubPacketData("고가");
                                    String[] dateData=_cdm.getStringData("자료일자");
                                    if(closeData==null) return;
                                    int cnt = closeData.length;
                                    idx = getXToDateWithCount(curPoint.x, cnt);
//        					        System.out.println("Debug_getX_idx:"+idx);
                                    int dIndex = idx;
                                    if(dIndex>=cnt)dIndex=cnt-1;

//        							viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
                                    if(dateData!=null)
                                        viewDatas.add(getViewPanelItem("날짜", dateData[dIndex]));

                                    if(closeData!=null)
                                        viewDatas.add(getViewPanelItem("가격", ChartUtil.getFormatedData(String.valueOf(closeData[dIndex]), _cdm.getPriceFormat(), _cdm)));

                                }
                                //2020.06.08 by JJH >> PnF 차트 데이터 창 추가 end
//        						if(dt.getTitle().equals("삼선전환도")) {
//        							double[] openData=_cdm.getSubPacketData(dt.getPacketTitle()+"_open");
//        							double[] highData=_cdm.getSubPacketData(dt.getPacketTitle()+"_high");
//        							String[] dateData=_cdm.getStringData(dt.getPacketTitle()+"_date");
//
//        					        int cnt = openData.length;
//        					        idx = getXToDateWithCount(curPoint.x, cnt);
////        					        System.out.println("Debug_getX_idx:"+idx);
//        					        int dIndex = idx;
//        					        if(dIndex>=cnt)dIndex=cnt-1;
//
////        							viewDatas.add(getViewPanelItem("종목명", COMUtil.codeName));
//        							if(openData!=null)
//        								viewDatas.add(getViewPanelItem("시가", String.valueOf(openData[dIndex])));
//        							if(highData!=null)
//        								viewDatas.add(getViewPanelItem("종가", String.valueOf(highData[dIndex])));
//        							if(dateData!=null)
//        								viewDatas.add(getViewPanelItem("날짜", dateData[dIndex]));
//        						}
                            }
                        }
                    }
                    //2015.06.25 by lyk - stand block 처리  end
                    else if((block.isBasicBlock() || (_cvm.getAssetType()>0 && i==0)) && !_cvm.isStandGraph()){
                        int blGraphCnt = block.getGraphs().size();
                        for(int j=0; j<blGraphCnt; j++) {
                            graph=(AbstractGraph)block.getGraphs().get(j);
                            if(graph.getGraphTitle().equals("그물차트") || graph.getGraphTitle().equals("매물대"))
                                continue;
//							// 2016. 3. 2 by pjm 수치조회창에 그래프 이름 보여주기
                            if (!_cvm.bInvestorChart) {
                                if (j >= 1) {
                                    if (graph.getGraphTitle().equals("주가이동평균") &&
                                            (
                                                    getVisible("이평 5") ||
                                                            getVisible("이평 20") ||
                                                            getVisible("이평 60") ||
                                                            //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 >>
                                                            getVisible("이평 10") ||
                                                            getVisible("이평 120") ||
                                                            getVisible("이평 200") ||
                                                            getVisible("이평 240") //2023.06.29 by SJW - 주가이동평균 라인 "240"일 추가 요청
                                                    //2023.06.27 by SJW - 주가이동평균 라인("10","20","200") 추가 <<

                                            )
                                    ) {
                                        viewDatas.add(getViewPanelItem(graph.getGraphTitle(), ""));
                                    } else if (graph.getGraphTitle().equals("일목균형표") || graph.getGraphTitle().equals("Bollinger Band")) {
                                        viewDatas.add(getViewPanelItem(graph.getGraphTitle(), ""));
                                        //2023.06.27 by SJW - 엔벨로프 지표 추가 >>
                                    } else if (graph.getGraphTitle().equals("Envelope")) {
                                        viewDatas.add(getViewPanelItem("엔벨로프", ""));
                                    }
                                    //2023.06.27 by SJW - 엔벨로프 지표 추가 <<
                                }
                            }
                            for(int k=0; k<graph.getDrawTool().size(); k++) {
                                DrawTool dt = (DrawTool)graph.getDrawTool().get(k);
                                //2016. 1. 14 뉴스차트 핸들러>>
//								if(_cvm.bIsNewsChart)
//								{
//									String[] curData = _cdm.getDatas(idx);
//									String[] prevData = _cdm.getDatas(idx-1);
//									viewDatas.add(getViewPanelItem("날짜", datas[0]));
//									viewDatas.add(getViewPanelItem("종가", datas[4]+"("+COMUtil.getRate(curData[4], prevData[4])+")"));
//								}
                                //2016. 1. 14 뉴스차트 핸들러<<
                                if(dt.getTitle().equals("종가") && !_cvm.bInvestorChart) {
                                    //2012. 7. 9  십자선데이터 한글 변환 ?및 볼륨 제외
//        							viewDatas.add(getViewPanelItem("날짜", datas[0]));
//        							viewDatas.add(getViewPanelItem("시가", datas[1]));
//        							viewDatas.add(getViewPanelItem("고가", datas[2]));
//        							viewDatas.add(getViewPanelItem("저가", datas[3]));
//        							viewDatas.add(getViewPanelItem("종가", datas[4]));
//        							viewDatas.add(getViewPanelItem("volume", datas[5]));
                                    //if(_cvm.getAssetType()<1)
                                    //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 >>
                                    if (!_cdm.codeItem.strDataType.equals("0") && !_cdm.codeItem.strDataType.equals("1")) {
                                        // viewpanel 날짜
                                        String strDate = datas[0].replaceAll("/", "");
                                        viewDatas.add(getViewPanelItem(strDate, ""));
//										String strDateWithDay = this.getFormatDate(strDate);
//
//										if (!strDateWithDay.equals(strDate)) {
//											viewDatas.add(getViewPanelItem(strDateWithDay, ""));
//										}
//										else {
//											viewDatas.add(getViewPanelItem(datas[0], ""));
//										}
                                    }
                                    else {
                                        //2021.07.09 by hanjun.Kim - kakaopay - 카카오 날짜디자인 적용 >>
                                        String value = datas[0];
                                        SimpleDateFormat server_format = new SimpleDateFormat("MM/dd HH:mm");
                                        SimpleDateFormat display_format = new SimpleDateFormat("MM.dd. HH:mm");
                                        if (value.contains("/")) {
                                            try {
                                                value = display_format.format(Objects.requireNonNull(server_format.parse(value)));
                                            } catch (ParseException e) {
                                                value = datas[0];
                                                e.printStackTrace();
                                            }
                                        }
                                        viewDatas.add(getViewPanelItem(value, ""));
                                        //2021.07.09 by hanjun.Kim - kakaopay - 카카오 날짜디자인 적용 <<
                                    }

                                    //viewDatas.add(getViewPanelItem(datas[0], ""));
                                    //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 <<
                                    double dStartPrice = 0;
                                    try
                                    {
                                        //2013. 10. 31 롱터치 뷰패널 시고저종중 종가만 표시되던 현상>>
                                        String[] curData = _cdm.getDatas(idx);
                                        //String strTmp = datas[1].replace(",", "");
                                        //strTmp = datas[1].replace("'", "");
                                        dStartPrice = Double.parseDouble(curData[1]);
                                        //2013. 10. 31 롱터치 뷰패널 시고저종중 종가만 표시되던 현상<<
                                    }catch(Exception e)
                                    {
                                    }
                                    if(idx>=0)
                                    {
                                        String[] curData = _cdm.getDatas(idx);
                                        String[] prevData;
                                        if(idx == 0) {
                                            prevData= _cdm.getDatas(idx);

                                            //2021.1.27 by lyk - 기준가 처리 >>
                                            String strGijun = _cdm.codeItem.strGijun;
                                            if(strGijun.equals("")) {
                                                prevData[4] = prevData[1]; //2021.1.27 by lyk - 기준가가 없을 경우, 시가로 처리함
                                            } else {
                                                prevData[4] = strGijun;
                                            }
                                            //2021.1.27 by lyk - 기준가 처리 <<
                                        } else {
                                            prevData = _cdm.getDatas(idx - 1);
                                        }

                                        if(!_cvm.bStandardLine && dStartPrice!=0)
                                        {
                                            //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//											viewDatas.add(getViewPanelItem("시가", datas[1]+" "+"("+COMUtil.getRate(curData[1], prevData[4])+")"));
//											viewDatas.add(getViewPanelItem("고가", datas[2]+" "+"("+COMUtil.getRate(curData[2], prevData[4])+")"));
//											viewDatas.add(getViewPanelItem("저가", datas[3]+" "+"("+COMUtil.getRate(curData[3], prevData[4])+")"));
                                            viewDatas.add(getViewPanelItem("시가", datas[1]+" "+COMUtil.getRate(curData[1], prevData[4])));
                                            viewDatas.add(getViewPanelItem("고가", datas[2]+" "+COMUtil.getRate(curData[2], prevData[4])));
                                            viewDatas.add(getViewPanelItem("저가", datas[3]+" "+COMUtil.getRate(curData[3], prevData[4])));
                                            //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<
                                        }
                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//										viewDatas.add(getViewPanelItem("종가", datas[4]+" "+"("+COMUtil.getRate(curData[4], prevData[4])+")"));
                                        viewDatas.add(getViewPanelItem("종가", datas[4]+" "+COMUtil.getRate(curData[4], prevData[4])));
                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<
                                        //2021.05.24 by hanjun.Kim - kakaopay
                                    }
                                    else
                                    {
                                        if(dStartPrice>0)
                                        {
                                            viewDatas.add(getViewPanelItem("시가", datas[1]));
                                            viewDatas.add(getViewPanelItem("고가", datas[2]));
                                            viewDatas.add(getViewPanelItem("저가", datas[3]));
                                        }
                                        viewDatas.add(getViewPanelItem("종가", datas[4]));
                                        //2021.05.24 by hanjun.Kim - kakaopay
                                    }
                                    // add by metalpooh 2013-01-11 거래량 등록 추가
                                    if(!_cvm.bStandardLine && dStartPrice>0) {
//										viewDatas.add(getViewPanelItem("거래량", datas[5]));
                                        //2022.05.04 by lyk - 지수차트 처리 >>
                                        String strUnit = "";
                                        if (COMUtil._mainFrame.mainBase.baseP.nMarketType == 1) {
                                            strUnit = "(천)";
                                        }
                                        //2022.05.04 by lyk - 지수차트 처리 <<
                                        if(idx==0)
                                            viewDatas.add(getViewPanelItem("거래량"+strUnit, datas[5]));
                                        else
                                        {
                                            String[] curData = _cdm.getDatas(idx);
                                            String[] prevData= _cdm.getDatas(idx-1);
                                            //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//											viewDatas.add(getViewPanelItem("거래량"+strUnit, datas[5]+" ("+COMUtil.getRate_Vol(curData[5], prevData[5])+")"));
                                            viewDatas.add(getViewPanelItem("거래량"+strUnit, datas[5]+" "+COMUtil.getRate_Vol(curData[5], prevData[5])));
                                            //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<
                                        }
                                    }
                                } else {
                                    if(_cvm.chartType==COMUtil.COMPARE_CHART) {
                                        viewP.setCompareChart(true); //2016.11.02 by hyh - 스크롤 수치조회창 개발
                                        if(k==0) {
                                            //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 >>
                                            String strDate = datas[0].replaceAll("/", "");
                                            String strDateWithDay = this.getFormatDate(strDate);

                                            if (!strDateWithDay.equals(strDate)) {
                                                viewDatas.add(getViewPanelItem(strDateWithDay, ""));
                                            }
                                            else {
                                                viewDatas.add(getViewPanelItem(datas[0], ""));
                                            }

                                            //viewDatas.add(getViewPanelItem(datas[0], ""));
                                            //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 <<
                                        }
                                        //2012.10.04 by LYH >> 비교차트 툴팁수정.
                                        String strName = datas[0];
                                        if(k<arrNames.size())
                                        {
                                            strName = arrNames.get(k);
                                        }
                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//										viewDatas.add(getViewPanelItem(strName, dt.getFormatData(idx)+" "+"("+dt.getCompareData(idx)+"%)"));
                                        viewDatas.add(getViewPanelItem(strName, dt.getFormatData(idx)+" "+dt.getCompareData(idx)+"%"));
                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<
                                        //2012.10.04 by LYH <<
                                    } else {
                                        //2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
                                        if(_cvm.bRateCompare || _cvm.bInvestorChart)
                                        {
                                            if(_cvm.getAssetType()>0)
                                            {
                                                if(dt.isVisible())	//2013. 11. 18 투자자 추이 차트에서 지수 없앴는데 롱터치뷰패널에선 보이는 현상 : 스크립트 lineproperty로 감췄는데도 뷰패널에선 표시되던 현상
                                                {
                                                    if(j==0) {
                                                        viewDatas.add(getViewPanelItem(datas[0], ""));
                                                    }
                                                    //viewDatas.add(getViewPanelItem(dt.subTitle, dt.getFormatData(idx)));
                                                    viewDatas.add(getViewPanelItem("", dt.getFormatData(idx)));
                                                }
                                            }
                                            else
                                            {
                                                if(j==0) {
                                                    viewDatas.add(getViewPanelItem(datas[0], ""));
                                                }
                                                if(dt.isVisible())	//2013. 11. 18 투자자 추이 차트에서 지수 없앴는데 롱터치뷰패널에선 보이는 현상 : 스크립트 lineproperty로 감췄는데도 뷰패널에선 표시되던 현상
                                                {
                                                    //viewDatas.add(getViewPanelItem(dt.subTitle, dt.getFormatData(idx)));
                                                    viewDatas.add(getViewPanelItem(dt.subTitle, dt.getFormatData(idx)));
                                                }
                                            }

                                        }
                                        //2013.01.04 by LYH <<
                                        else
                                        {
                                            if(dt.isVisible())
                                            {
                                                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선
//												if(dt.getTitle().equals("선행스팬"))
//												{
//													ChartPacketDataModel cpdm = _cdm.getChartPacket("toData4");
//													String strSpan1 = cpdm.getFormatData(idx);
//													viewDatas.add(getViewPanelItem("선행스팬1",strSpan1));
//													viewDatas.add(getViewPanelItem("선행스팬2",dt.getFormatData(idx)));
//												}
//												else {
                                                //2017.01.03 by LYH >> 일목균형 선행스팬1, 2 타이틀 개선 end
                                                if(dt.getTitle().startsWith("이평"))
                                                {
                                                    //2023.06.28 by SJW - 이평 데이터 없을 경우 수치조회창에서 안보이게 처리 >>
                                                    double dValue = Double.parseDouble(_cdm.getData(dt.getTitle() + k, idx));
                                                    if (dValue != 0.0) {
                                                        //2023.06.28 by SJW - 이평 데이터 없을 경우 수치조회창에서 안보이게 처리 <<
                                                        //2019.07.12 by JJH >> 종합차트 십자선 툴팁에 이동평균선 등락률 정보 표시 start
//														viewDatas.add(getViewPanelItem(COMUtil.jipyoNameToEng(dt.getTitle()), dt.getFormatData(idx, dt.getTitle()+k)));
                                                        String[] prevData;
                                                        if (idx == 0)
                                                            prevData = _cdm.getDatas(idx);
                                                        else
                                                            prevData = _cdm.getDatas(idx - 1);
                                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//														String strChgrate = "(" + COMUtil.getRate(_cdm.getData(dt.getTitle() + k, idx), prevData[4]) + ")";
                                                        String strChgrate = COMUtil.getRate(_cdm.getData(dt.getTitle() + k, idx), prevData[4]);
                                                        //2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<

                                                        try {
//																double dValue = Double.parseDouble(_cdm.getData(dt.getTitle() + k, idx)); ㅍ\//2023.06.28 by SJW - 이평 데이터 없을 경우 수치조회창에서 안보이게 처리
                                                            String strValue = "";
                                                            if (dValue > 0) {
                                                                strValue = dt.getFormatData(idx, dt.getTitle() + k) + " " + strChgrate;
                                                            }
                                                            viewDatas.add(getViewPanelItem(COMUtil.jipyoNameToEng(dt.getTitle()), strValue));
                                                        } catch (Exception e) {

                                                        }
                                                        //2019.07.12 by JJH >> 종합차트 십자선 툴팁에 이동평균선 등락률 정보 표시 end
                                                        //2023.06.28 by SJW - 이평 데이터 없을 경우 수치조회창에서 안보이게 처리 >>
                                                    }
                                                    //2023.06.28 by SJW - 이평 데이터 없을 경우 수치조회창에서 안보이게 처리 <<
                                                }else{
                                                    viewDatas.add(getViewPanelItem(COMUtil.jipyoNameToEng(dt.getTitle()), dt.getFormatData(idx)));
                                                }
//												}

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        if(_cvm.chartType==COMUtil.COMPARE_CHART) {
                            int blGraphCnt = block.getGraphs().size();
                            for (int j = 0; j < blGraphCnt; j++) {
                                graph = (AbstractGraph) block.getGraphs().get(j);
                                for (int k = 0; k < graph.getDrawTool().size(); k++) {
                                    DrawTool dt = (DrawTool) graph.getDrawTool().get(k);
                                    //2012.10.04 by LYH >> 비교차트 툴팁수정.
                                    String strName = datas[0];
                                    if(k<arrNames.size())
                                    {
                                        strName = arrNames.get(i);
                                    }
                                    if(strName.length()>8)
                                        strName = strName.substring(0, 8);
                                    viewDatas.add(getViewPanelItem(strName, dt.getFormatData(idx)+"("+dt.getCompareData(idx)+"%)"));
                                    //2012.10.04 by LYH <<
                                }
                            }
                        }
                    }
                }
            }

            //2016. 1. 14 뉴스차트 핸들러>>
            //2017.09.25 by LYH >> 자산 차트 적용
            if(		_cvm.getAssetType()== ChartViewModel.ASSET_LINE_FILL||
                    _cvm.getAssetType()== ChartViewModel.ASSET_LINE ||
                    _cvm.getAssetType()== ChartViewModel.ASSET_LINE_MOUNTAIN)
            {
                if(_cvm.getAssetType()== ChartViewModel.ASSET_LINE_MOUNTAIN)
                {
                    setViewPanel_Location((int)(x+xscale.xfactor/2), 0);
                    viewP.setProcessPresentData(null, viewDatas, _cvm.bInvestorChart);
                    return;
                }
                //showVertLineWithCircle_Asset(gl, x, (int)py, bounds, viewDatas);
                YScale upperBlockY = blocks.get(0).getYScale()[0];

                double dValue = 0;
                try {
                    dValue = Double.parseDouble(_cdm.getData("data1", idx));
                } catch (Exception e) {

                }
                setViewPanel_Location(x, (int)upperBlockY.priceToY(dValue));
                viewP.setProcessPresentData(null, viewDatas, _cvm.bInvestorChart);
                return;
            }
            //2017.09.25 by LYH >> 자산 차트 적용 end
            else if (_cvm.bIsNewsChart) {
                showVertLineWithCircle(gl, x, bounds.bottom, bounds, viewDatas);
                YScale upperBlockY = blocks.get(0).getYScale()[0];
            }
            //2016. 1. 14 뉴스차트 핸들러<<

            //2021.06.30 by lyk - kakaopay - 시세 인포뷰 십자선 수정 >>
//			if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//				//가로선
//				_cvm.drawLine(gl, x + _cvm.Margin_L, bounds.top, x + _cvm.Margin_L, bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H, CoSys.crossline_colDarkSkin, 1.0f);
//				//세로선
//				_cvm.drawLine(gl, bounds.left + _cvm.Margin_L, py, bounds.left + bounds.right - TP_WIDTH - (_cvm.Margin_L + _cvm.Margin_R), py, CoSys.crossline_colDarkSkin, 1.0f);
//			} else {
            _cvm.drawLine(gl, x + _cvm.Margin_L, bounds.top, x + _cvm.Margin_L, bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H + COMUtil.getPixel(1), CoSys.crossline_col, 0.5f);

            //2023.07.24 by SJW - 십자선 레이블 차트 밖으로 나가지 않도록 수정 >>
//			_cvm.drawLine(gl, bounds.left + _cvm.Margin_L, py, bounds.left + bounds.right - TP_WIDTH - (_cvm.Margin_L + _cvm.Margin_R), py, CoSys.crossline_col, 0.5f);
            if (0 < py && py < xscale.getBounds().top) {
                _cvm.drawLine(gl, bounds.left + _cvm.Margin_L, py, bounds.left + bounds.right - TP_WIDTH - (_cvm.Margin_L + _cvm.Margin_R), py, CoSys.crossline_col, 0.5f);
            }
            //2023.07.24 by SJW - 십자선 레이블 차트 밖으로 나가지 않도록 수정 <<

//			}
            //2021.06.30 by lyk - kakaopay - 시세 인포뷰 십자선 수정 <<

            if (_cvm.bIsAlarmChart) {
                float fHeight = COMUtil.getPixel(14);
                float fWidth = _cvm.Margin_R;
                float fX = bounds.left + bounds.right - TP_WIDTH - (_cvm.Margin_L + _cvm.Margin_R);
                float fY = py - (fHeight / 2);
                float fTextTopMargin = COMUtil.getPixel(7);
                float fTextLeftMargin = COMUtil.getPixel(4);

                if (chart_bounds.contains(curPoint.x, curPoint.y)) {
                    YScale yScale = basic_block.getYScale()[0];

                    double dPrice = yScale.getChartPrice(curPoint.y);
                    String strPrice = String.valueOf(dPrice);

                    if (bIsInitAlarmChart) {
                        strPrice = strInitAlarmPrice;
                        bIsInitAlarmChart = false;
                    }

                    strPrice = ChartUtil.getFormatedData(strPrice, _cdm.getPriceFormat(), _cdm);

                    _cvm.drawFillRect(gl, fX, fY, fWidth, fHeight, CoSys.alarm_price_bar_col, 1.0f);
                    _cvm.drawString(gl, CoSys.WHITE, (int) (fX + fTextLeftMargin), (int) (fY + fTextTopMargin), strPrice);
                }
            }
            //2019. 04. 17 by hyh - 시세알람차트 개발 <<
            if(!_cvm.bIsHideXYscale)
//				setViewPanel(x + _cvm.Margin_L, (int) py);
                setViewPanel_Location(x + _cvm.Margin_L, (int) py);
            else
                hideViewPanel();

            //2019.07.17 by JJH >> 차트 십자선 Y축의 가격 표시 start
//			YScale tmpYscale = basic_block.getYScale()[0];

            //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 >>
//			Block block = getChartBlock(curPoint);
            Block block = null;
            if (isSpecialDraw())
                block = getSpecialChartBlock(_cvm.getStandGraphName());
            else
                block = getChartBlock(curPoint);
            //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 <<

            YScale tmpYscale;

            if(block != null && !block.getTitle().equals("가격차트"))
                tmpYscale = block.getYScale()[0];
            else
                tmpYscale = basic_block.getYScale()[0];

            String tmpPrice = "";
            String yPrice = tmpYscale.getChartPriceStr(curPoint.y);
            if(yPrice == "") {

            } else {
                tmpPrice =  ChartUtil.getFormatedData(yPrice,_cdm.getPriceFormat());
            }
//			String tmpPrice =  ChartUtil.getFormatedData(tmpYscale.getChartPriceStr(curPoint.y),_cdm.getPriceFormat());
            //2020.02.05 십자선 종가 따라가기 옵션화 - hjw >>
            if(COMUtil.isCrossLineJongga()) {
                double close = 0;
                if(_cvm.chartType!=COMUtil.COMPARE_CHART)
                {
                    String strPrice=_cdm.getData("종가", idx);
                    if(strPrice.length()>0)
                    {
                        close = Double.parseDouble(strPrice);
                    }
                }
                tmpPrice =  ChartUtil.getFormatedData(""+close,_cdm.getPriceFormat());
            }
            //2020.02.05 십자선 종가 따라가기 옵션화 - hjw <<
            float xPos = _cvm.getBounds().left + _cvm.getBounds().width() - _cvm.Margin_R +(int)COMUtil.getPixel(1);
            int pw = _cvm.Margin_R-(int)COMUtil.getPixel(4);
            int ph = (int)COMUtil.getPixel_H(18);

            //2021.07.14 by hanjun.Kim - kakaopay - 십자선 가격표시 색상 변경
//			int[] backColor = {255, 211, 37};
//			int[] textolor = {17, 17, 17};
//			_cvm.drawFillRect(gl, xPos,py-(int)COMUtil.getPixel(7),pw,(int)COMUtil.getPixel(14), backColor, 1.0f);

            //2023.07.24 by SJW - 십자선 레이블 차트 밖으로 나가지 않도록 수정 >>
            //_cvm.drawCurrentPriceBox(gl, xPos, py-COMUtil.getPixel_H(18)/2, pw, ph, CoSys.crossline_col);

            //int w = _cvm.GetTextLength(tmpPrice);
            //2021.07.12 by hanjun.Kim - kakaopay - 가격표 좌측정렬 >>
            //_cvm.drawScaleString(gl, CoSys.crossline_text_col, (int)xPos+(int)COMUtil.getPixel(3),(int)py, tmpPrice);

            if (0 < py && py < xscale.getBounds().top && 0 < xPos) {
                _cvm.drawCurrentPriceBox(gl, xPos, py - COMUtil.getPixel_H(18) / 2, pw, ph, CoSys.crossline_col);
                _cvm.drawScaleString(gl, CoSys.crossline_text_col, (int) xPos + (int) COMUtil.getPixel(3), (int) py, tmpPrice);
            }
            //2023.07.24 by SJW - 십자선 레이블 차트 밖으로 나가지 않도록 수정 <<

            String strDate = datas[0];
            pw = _cvm.GetTextLength(strDate)+(int)COMUtil.getPixel(6);
            RectF xScaleBounds = xscale.getBounds();
//			_cvm.drawFillRect(gl, x-pw/2/*+COMUtil.getPixel(4)*/,xScaleBounds.top,pw,ph, backColor, 1.0f);
//			_cvm.drawString(gl, textolor, (int)x-pw/2+(int)COMUtil.getPixel(2),xScaleBounds.top+ph/2, strDate);
            //2019.07.17 by JJH >> 차트 십자선 Y축의 가격 표시 end
            //2021.05.27 by hanjun.Kim - kakaopay - 차트 십자선 X축 날짜표시 제거

            viewP.setProcessPresentData(null, viewDatas, _cvm.bInvestorChart);

            return;
        }
    }
    private Hashtable<String, String> getViewPanelItem(String key, String value) {
        Hashtable<String, String> rtnVal = new Hashtable<String, String>();
        try {
            rtnVal.put(key, value);
        } catch (Exception e) {
            if (value == null) {
                rtnVal.put(key, "");
            }
            e.printStackTrace();
        }

        return rtnVal;
    }
    private void drawTitleData() {
        int idx = this.getXToDate(curPoint.x);
        Block block;

        int blCnt = blocks.size();
        for(int i=0; i<blCnt; i++) {
            block=(Block)blocks.get(i);
            if(block.isBasicBlock())
                continue;
            block.setGraphDataTitle(idx);
        }
    }
    public void closeButtonClicked(final String strName) {
        if(_cvm.getToolbarState()!=9999) {
            return;
        }
//    	COMUtil._chartMain.runOnUiThread(new Runnable() {
//            public void run() {
//		    	if(!COMUtil._neoChart.equals(this)) {
//		    		selectChart();
//		    		selectBaseChart();
//		    	}
//		    	removeBlock(strName);
//            }
//    	});
        if(!COMUtil._neoChart.equals(this)) {
            selectBaseChart();
            selectChart();
            return;
        }
        removeBlock(strName);

        //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다)
        COMUtil.setSavePeriodChartSave();
        //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다) end
        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
        nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
    }
    public void resetTitleBounds() {
        for(int i=0; i<blocks.size(); i++) {
            Block block = (Block)blocks.get(i);
            if(block.isBasicBlock())
                continue;
            block.resetTitleBounds();
        }
    }

    public void resetTitleBoundsAll() {
        for(int i=0; i<blocks.size(); i++) {
            Block block = (Block)blocks.get(i);
            block.resetTitleBounds();
            //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정
            if(block.isBasicBlock())
            {
                basic_block.setBounds_Pivot(false);
            }
            //2017.06.21 by LYH >> 타이틀 3줄까지 타이틀 영역 밑으로 그래프 영역 잡도록 수정 end
        }
    }
    Block selectedChangeBlock = null;
    RectF changeRect = null;

    //2021. 06. 05 by hyh - 지표 잔상 효과 개발
    Bitmap croppedBitmap = null;

    private void drawAnalTool_MouseDrag(Canvas gl){
        //2023.02.09 by SJW - 블록병합 기능 제거 >>
//		//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 >>
//		if (selectedMoveDrawTool != null && selectedMoveGraph != null) {
//			String strGraphName = selectedMoveGraph.getName();
//			int[] textColor = selectedMoveGraph.getDrawTool().firstElement().getUpColor();
//
//			_cvm.drawString(gl, textColor, curPoint.x, curPoint.y, strGraphName);
//			return;
//		}
//		//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 <<
        //2023.02.09 by SJW - 블록병합 기능 제거 <<
        //2021.06.22 by lyk - kakaopay - 리사이즈시 가로선 삭제 >>
//		if(_cvm.isResize()) {
////    		_cvm.drawLine(gl, 0,currY,chart_bounds.width()-TP_WIDTH,currY, CoSys.GRAY, 1.0f);
//			//_cvm.drawFillRect(gl, 1.0f, currY, (float)(chart_bounds.width()-TP_WIDTH), 4.0f, CoSys.GRAY, 0.5f);
//            //2016.11.17 by LYH >> 리사이즈 시 색상 라인두께 변경
//            _cvm.drawFillRect(gl, 1.0f, currY, (float)(chart_bounds.width()-TP_WIDTH), COMUtil.getPixel(3), CoSys.at_col, 1.0f);
//            //2016.11.17 by LYH >> 리사이즈 시 색상 라인두께 변경 end
//			return;
//		}
        //2021.06.22 by lyk - kakaopay - 리사이즈시 가로선 삭제 <<

        if(_cvm.isChange()) {
            float yOffset = -1.0f;

            if(selectedChangeBlock==null) {
                selectedChangeBlock = getChartBlock(curPoint);
                if(selectedChangeBlock!=null) changeRect = selectedChangeBlock.getOutBounds();
            } else {
                yOffset = curPoint.y;
            }

            //2019. 09. 30 by hyh - 가격 블럭 이동 막음 >>
            //2022.09.06 by lyk - selectedChangeBlock null object reference 방어코드 추가 >>
            if (selectedChangeBlock != null && selectedChangeBlock.isBasicBlock()) {
                return;
            }
            //2022.09.06 by lyk - selectedChangeBlock null object reference 방어코드 추가 <<
            //2019. 09. 30 by hyh - 가격 블럭 이동 막음 <<

            if(changeRect!=null) {
                //2021. 06. 05 by hyh - 지표 잔상 효과 개발 >>
                if (croppedBitmap != null) {
                    float drawRectYp = changeRect.top;

                    if (yOffset > -1) {
                        drawRectYp = yOffset;
                    }
                    // 블럭 이동때 버튼 사라지게 할때.
//					for(int i=0;i<blocks.size();i++) {
//						Block cb = (Block) blocks.elementAt(i);
//						cb.setHideChangeBlockButton(true);
//					}
                    _cvm.drawDimImageWithShadow(context, gl, changeRect.left, drawRectYp, chart_bounds.width(), changeRect.height(), croppedBitmap, 255);
                }
                //2021. 06. 05 by hyh - 지표 잔상 효과 개발 <<

                //2021. 06. 05 by hyh - 지표 잔상 효과 개발 >>
                //_cvm.drawRect(gl, changeRect.left, changeRect.top, changeRect.right, changeRect.height(), CoSys.GREEN);
                //if(yOffset>-1) _cvm.drawRect(gl, changeRect.left, yOffset, changeRect.right, changeRect.height(), CoSys.GRAY);
                //2021. 06. 05 by hyh - 지표 잔상 효과 개발
            }

            return;
        }
//        if(dragged){
//            _cvm.drawLine(gl, 0,currY,chart_bounds.width()-TP_WIDTH,currY, CoSys.GRAY, 1.0f);
//        }else{
        RectF bounds = _cvm.getBounds();
        //int x = (int)xscale.getDateToX(idx);
        AnalTool at;
        switch(_cvm.getToolbarState()){
            case COMUtil.TOOLBAR_CONFIG_CROSS ://십자선
                _cvm.drawLine(gl, curPoint.x+_cvm.Margin_L,bounds.top,curPoint.x+_cvm.Margin_L,bounds.bottom, CoSys.at_col, 1.0f);
                _cvm.drawLine(gl, bounds.left+_cvm.Margin_L,curPoint.y,bounds.left+bounds.right-TP_WIDTH-(_cvm.Margin_L+_cvm.Margin_R),curPoint.y, CoSys.at_col, 1.0f);
                break;
            case COMUtil.TOOLBAR_CONFIG_VERT://수직선
            case COMUtil.TOOLBAR_CONFIG_FIBOTIME ://피보나치 시간대
                _cvm.drawLine(gl, curPoint.x,bounds.top,curPoint.x,bounds.bottom, CoSys.GRAY, 1.0f);
                break;
            case COMUtil.TOOLBAR_CONFIG_HORZ://수평선
            case COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ:
                if(m_nOrgViewNum<=0)    //2017.07.25 by pjm >> Y축 움직여 확대/축소
                {
                    YScale yscale = basic_block.getYScale()[0];
                    if(curPoint.y<yscale.getBounds().bottom+(int)COMUtil.getPixel(10))
                    {
                        float nRight = bounds.left+bounds.right-TP_WIDTH-(_cvm.Margin_L+_cvm.Margin_R);
                        float nLeft = bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(10);
                        _cvm.drawLine(gl, bounds.left+_cvm.Margin_L,curPoint.y,nRight,curPoint.y, CoSys.BLACK, 1.0f);

                        String price = ChartUtil.getFormatedData("" + yscale.getChartPrice(curPoint.y), _cdm.getPriceFormat(), _cdm);

                        //int w = _cvm.GetTextLength(price)+(int)COMUtil.getPixel(15);
                        int h = (int)COMUtil.getPixel(15);
                        //_cvm.drawString(gl, CoSys.BLACK, nRight-w,curPoint.y+h/2, price);
                        _cvm.drawString(gl, CoSys.BLACK, nLeft,curPoint.y+h/2, price);
                    }
                }
                else
                {
                    float nRight = bounds.left+bounds.right-TP_WIDTH-(_cvm.Margin_L+_cvm.Margin_R);
                    float nLeft = bounds.left+_cvm.Margin_L+(int)COMUtil.getPixel(10);

                    //2015. 2. 12 yscale 드래그 추세선 black스킨일 때 색상>>
                    //	            	 _cvm.drawLine(gl, bounds.left+_cvm.Margin_L,curPoint.y,nRight,curPoint.y, CoSys.BLACK, 1.0f);
                    int[] arLineColor;
                    if(_cvm.getSkinType() == COMUtil.SKIN_BLACK)
                    {
                        arLineColor = CoSys.WHITE;
                    }
                    else
                    {
                        arLineColor = CoSys.BLACK;
                    }
                    _cvm.setLineWidth(1);
                    _cvm.drawLine(gl, bounds.left+_cvm.Margin_L,curPoint.y,nRight,curPoint.y, arLineColor, 1.0f);
                    //2015. 2. 12 yscale 드래그 추세선 black스킨일 때 색상<<

                    YScale yscale = basic_block.getYScale()[0];
                    String price =  ChartUtil.getFormatedData(""+yscale.getChartPrice(curPoint.y),_cdm.getPriceFormat(), _cdm);

                    //int w = _cvm.GetTextLength(price)+(int)COMUtil.getPixel(15);
                    int h = (int)COMUtil.getPixel(15);
                    //_cvm.drawString(gl, CoSys.BLACK, nRight-w,curPoint.y+h/2, price);

                    //2015. 2. 12 yscale 드래그 추세선 black스킨일 때 색상>>
                    //	                 _cvm.drawString(gl, CoSys.BLACK, nLeft,curPoint.y+h/2, price);
                    _cvm.drawString(gl, arLineColor, nLeft,curPoint.y+h/2, price);
                    //2015. 2. 12 yscale 드래그 추세선 black스킨일 때 색상<<
                }
                break;
            case COMUtil.TOOLBAR_CONFIG_LINE://추세선
            case COMUtil.TOOLBAR_CONFIG_FIBOARC ://피보나치아크
            case COMUtil.TOOLBAR_CONFIG_FIBOFAN://피보나치 펜
            case COMUtil.TOOLBAR_CONFIG_FIBORET://피보나치조정대
            case COMUtil.TOOLBAR_CONFIG_SPEEDLINE://스피드라인
                //drawLineWithAngle(gl,pressPoint,curPoint);
                _cvm.drawLine(gl,pressPoint.x,pressPoint.y,curPoint.x,curPoint.y, CoSys.at_col ,1.0f);
                break;
            case COMUtil.TOOLBAR_CONFIG_LINE_TRISECT://삼등분선
            case COMUtil.TOOLBAR_CONFIG_LINE_QUARTER://사등분선
            {
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                _cvm.drawLine(gl, pressPoint.x, bound.top, pressPoint.x, bound.top + bound.height(), CoSys.GRAY, 1.0f);
                _cvm.drawLine(gl, curPoint.x, bound.top, curPoint.x, bound.top + bound.height(), CoSys.GRAY, 1.0f);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_ANDREW://앤드류스
            case COMUtil.TOOLBAR_CONFIG_FIBOTARGET:
            case COMUtil.TOOLBAR_CONFIG_TARGETNT:
                at = (AnalTool)analTools.lastElement();
                at.draw(gl);
                break;
            case COMUtil.TOOLBAR_CONFIG_RECT:
                if(pressPoint.x>curPoint.x){
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawRect(gl, curPoint.x,curPoint.y,pressPoint.x-curPoint.x,pressPoint.y-curPoint.y, CoSys.at_col);
                    }else{
                        _cvm.drawRect(gl, curPoint.x,pressPoint.y,pressPoint.x-curPoint.x,curPoint.y-pressPoint.y, CoSys.at_col);
                    }
                }else{
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawRect(gl, pressPoint.x,curPoint.y,curPoint.x-pressPoint.x,pressPoint.y-curPoint.y, CoSys.at_col);
                    }else{
                        _cvm.drawRect(gl, pressPoint.x,pressPoint.y,curPoint.x-pressPoint.x,curPoint.y-pressPoint.y, CoSys.at_col);
                    }
                }
                break;
            case COMUtil.TOOLBAR_CONFIG_TRIANGLE:
                //2012. 11. 6 삼각형 분석툴 그려지는 도중에 형태  제대로 표시 : T64
                if(pressPoint.x>curPoint.x){
                    _cvm.drawTriangle(gl,curPoint.x,pressPoint.y,pressPoint.x-curPoint.x,curPoint.y-pressPoint.y, CoSys.at_col);
                }else{
                    _cvm.drawTriangle(gl,pressPoint.x,pressPoint.y,curPoint.x-pressPoint.x,curPoint.y-pressPoint.y, CoSys.at_col);
                }
                break;

            //2011.09.29 by LYH >> 원 그리기 네모로 그리지 않고 원형으로 드래그
            case COMUtil.TOOLBAR_CONFIG_ROUND://원
            {
                //_cvm.drawCircle(gl, (pressPoint.x+curPoint.x)/2, (pressPoint.y+curPoint.y)/2, (pressPoint.x-curPoint.x), (pressPoint.y-curPoint.y), false, CoSys.at_col);
                _cvm.drawCircle(gl, pressPoint.x, pressPoint.y, curPoint.x, curPoint.y, false, CoSys.at_col);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_PERIOD:
            {
                _cvm.drawLine(gl,pressPoint.x,pressPoint.y,curPoint.x,pressPoint.y, CoSys.at_col ,1.0f);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_LEFTLINE: {
                double ry = (Math.abs(pressPoint.y - curPoint.y));
                double rx = (Math.abs(pressPoint.x - curPoint.x));
                double ratio = ry / rx;
                double ratio1 = (-1) * ry / rx;

                int[] at_col1 = {0, 0, 0};

                if (pressPoint.x > curPoint.x) {
                    if (pressPoint.y > curPoint.y) {
                        _cvm.drawLine(gl, pressPoint.x, pressPoint.y, 0, pressPoint.y - (int) ((-1 * pressPoint.x) * ratio1), at_col1, 1.0f);
                    } else {
                        _cvm.drawLine(gl, pressPoint.x, pressPoint.y, 0, pressPoint.y + (int) ((-1 * pressPoint.x) * ratio1), at_col1, 1.0f);
                    }
                } else {
                    if (pressPoint.y > curPoint.y) {
                        _cvm.drawLine(gl, curPoint.x, curPoint.y, 0, pressPoint.y - (int) ((-1 * pressPoint.x) * ratio), at_col1, 1.0f);
                    } else {
                        _cvm.drawLine(gl, curPoint.x, curPoint.y, 0, pressPoint.y + (int) ((-1 * pressPoint.x) * ratio), at_col1, 1.0f);
                    }
                }
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_RIGHTLINE: {

                double ry = (Math.abs(pressPoint.y - curPoint.y));
                double rx = (Math.abs(pressPoint.x - curPoint.x));
                double ratio = ry / rx;
                double ratio1 = (-1) * ry / rx;

                int[] at_col1 = {0, 0, 0};
                float x2 = 0;
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                x2 = bound.left+bound.width();

                if (pressPoint.x > curPoint.x) {
                    if (pressPoint.y > curPoint.y) {
                        _cvm.drawLine(gl, curPoint.x, curPoint.y, x2, pressPoint.y - (int) ((x2 - pressPoint.x) * ratio1), at_col1, 1.0f);
                    } else {
                        _cvm.drawLine(gl, curPoint.x, curPoint.y, x2, pressPoint.y + (int) ((x2 - pressPoint.x) * ratio1), at_col1, 1.0f);
                    }
                } else {
                    if (pressPoint.y > curPoint.y) {
                        _cvm.drawLine(gl, pressPoint.x, pressPoint.y, x2, pressPoint.y - (int) ((x2 - pressPoint.x) * ratio), at_col1, 1.0f);
                    } else {
                        _cvm.drawLine(gl, pressPoint.x, pressPoint.y, x2, pressPoint.y + (int) ((x2 - pressPoint.x) * ratio), at_col1, 1.0f);
                    }
                }
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_BSLINE:
            {
                double ry = (Math.abs(pressPoint.y - curPoint.y));
                double rx = (Math.abs(pressPoint.x - curPoint.x));
                double ratio = ry / rx;
                double ratio1 = (-1) * ry / rx;

                int[] at_col1 = {0, 0, 0};
                float x2 = 0;
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                x2 = bound.left+bound.width();
                if(pressPoint.x>curPoint.x){
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawLine(gl, x2,pressPoint.y-(int)((x2-pressPoint.x)*ratio1),0,pressPoint.y-(int)((-1 * pressPoint.x)*ratio1), at_col1, 1.0f);
                    }else{
                        _cvm.drawLine(gl, x2,pressPoint.y+(int)((x2-pressPoint.x)*ratio1),0,pressPoint.y+(int)((-1 * pressPoint.x)*ratio1), at_col1, 1.0f);
                    }
                }else{
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawLine(gl, x2,pressPoint.y-(int)((x2-pressPoint.x)*ratio),0,pressPoint.y-(int)((-1 * pressPoint.x)*ratio), at_col1, 1.0f);
                    }else{
                        _cvm.drawLine(gl, x2,pressPoint.y+(int)((x2-pressPoint.x)*ratio),0,pressPoint.y+(int)((-1 * pressPoint.x)*ratio), at_col1, 1.0f);
                    }
                }
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_DEGREE: {
                drawLineWithAngle(gl, pressPoint, curPoint);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_PARALLEL:{
                float x2 = 0;
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                x2 = bound.left+bound.width();

                _cvm.drawLine(gl,pressPoint.x,pressPoint.y,curPoint.x,curPoint.y, CoSys.at_col ,1.0f);
                _cvm.drawLine(gl,0,pressPoint.y,x2,pressPoint.y, CoSys.at_col ,1.0f);
                _cvm.drawLine(gl,0,curPoint.y,x2,curPoint.y, CoSys.at_col ,1.0f);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_PERPENDICULAR:{
                float y2 = 0;
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                y2 = bound.top+bound.height();

                _cvm.drawLine(gl,pressPoint.x,pressPoint.y,curPoint.x,curPoint.y, CoSys.at_col ,1.0f);
                _cvm.drawLine(gl,pressPoint.x,0,pressPoint.x,y2, CoSys.at_col ,1.0f);
                _cvm.drawLine(gl,curPoint.x,0,curPoint.x,y2, CoSys.at_col ,1.0f);
            }
            break;
            case COMUtil.TOOLBAR_CONFIG_GANNLINE:{
                double ry = (Math.abs(pressPoint.y - curPoint.y));
                double rx = (Math.abs(pressPoint.x - curPoint.x));
                double ratio = ry / rx;
                double ratio1 = (-1) * ry / rx;

                int[] at_col1 = {0, 0, 0};
                float x2 = 0;
                at = (AnalTool) analTools.lastElement();
                RectF bound = at._ac.getGraphBounds();
                x2 = bound.left+bound.width();

                if(pressPoint.x>curPoint.x){
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawLine(gl, pressPoint.x,pressPoint.y,0,pressPoint.y-(int)((pressPoint.x)*ratio), at_col1, 1.0f);
                    }else{
                        _cvm.drawLine(gl, pressPoint.x,pressPoint.y,0,pressPoint.y+(int)((pressPoint.x)*ratio), at_col1, 1.0f);
                    }
                }else{
                    if(pressPoint.y>curPoint.y){
                        _cvm.drawLine(gl, pressPoint.x,pressPoint.y,x2,pressPoint.y-(int)((x2-pressPoint.x)*ratio), at_col1, 1.0f);
                    }else{
                        _cvm.drawLine(gl, pressPoint.x,pressPoint.y,x2,pressPoint.y+(int)((x2-pressPoint.x)*ratio), at_col1, 1.0f);
                    }
                }
            }
            break;
            //2011.09.29 by LYH <<
        }
//        }
    }
    private void drawBuffer(Canvas gl){
        if(_cvm==null) return;

//		//2015. 3. 4 차트 테마 메인따라가기 추가>>
        if(COMUtil.bIsAutoTheme) {
            _cvm.setSkinType(COMUtil.currentTheme); //메인 테마로 설정
        }
//		//2015. 3. 4 차트 테마 메인따라가기 추가<<

        //2015. 10. 19 현재가차트 투명 처리>>
        if(_cvm.bIsTransparent) {
            gl.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        else
        {
//			if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
////        		gl.drawColor(Color.BLACK);
//				//gl.drawColor(Color.rgb(35, 37, 38));
////				gl.drawColor(Color.rgb(18, 28, 40));
//				gl.drawColor(CoSys.CHART_BACK_MAINCOLOR);
//			} else
//			{
//				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//					gl.drawColor(Color.WHITE);
//				else
//				{
//					if(_cvm.m_bCurrentChart)
//						gl.drawColor(Color.rgb(251, 251, 251));
//					else
//						gl.drawColor(Color.WHITE);
//				}
//			}

            gl.drawColor(CoSys.CHART_BACK_MAINCOLOR);
        }
        //2015. 10. 19 현재가차트 투명 처리<<

        synchronized (this) {
            drawBlocks(gl);
        }

        if(touchesMoved) {
            if(funcName=="drawAnalTool_MouseDrag") {
                drawAnalTool_MouseDrag(gl);
            } else if(funcName=="drawAnalTool_MouseMove") {
                if(m_bShowToolTip || _cvm.isCrosslineMode) {
                    drawAnalTool_MouseMove(gl);
                }
            }
        }

        if(_cvm.getSkinType() != COMUtil.SKIN_BLACK)
        {
            if(m_bSelected)
            {
//				_cvm.setLineWidth(2);
                _cvm.setLineWidth(6);	//2016. 1. 8 멀티차트 테두리 색 및 굵기 변경
//				_cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width()-1,chart_bounds.height()-1, CoSys.crossline_col);
                _cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width(),chart_bounds.height(), CoSys.crossline_col);
                _cvm.setLineWidth(1);
            }
            else {
//            	Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//            	if(base11.isMultiChart())
//            		_cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width()-1,chart_bounds.height()-1, CoSys.rectLineColorWhiteSkin);
//
//				if(_cvm.m_bCurrentChart || _cvm.bInvestorChart)
//				{
//					_cvm.setLineWidth_Fix(COMUtil.getPixel(2));
//					int[] rectLineColor = {214,214,214};
//
//					_cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width()-1,chart_bounds.height()-1, rectLineColor);
//					_cvm.setLineWidth(1);
//				}
            }
        } else if(_cvm.getSkinType() == COMUtil.SKIN_BLACK)
        {
            if(m_bSelected)
            {
                _cvm.setLineWidth(2);
                _cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width()-1,chart_bounds.height()-1, CoSys.YELLOW);
                _cvm.setLineWidth(1);
            }
//            else {
//                _cvm.drawRect(gl, chart_bounds.left,chart_bounds.top,chart_bounds.width()-1,chart_bounds.height()-1, CoSys.rectLineColorWhiteSkin);
//            }
        }
    }
    //====================================
    // 차트 블럭 그리기
    //====================================
//    public void drawBlocks(Canvas gl){
//        if(xscale==null||blocks==null) return;
//        if(_cdm.getCount()<1){
//            for(int i=0;i<blocks.size();i++){
//                Block cb = (Block)blocks.elementAt(i);
//                //2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 >>
////                if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) && _cvm.getSkinType() == COMUtil.SKIN_WHITE && !_cvm.bIsMiniBongChart) {
//                if(_cvm.getSkinType() == COMUtil.SKIN_WHITE && !_cvm.bIsMiniBongChart) {
//                //2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 <<
//                	Rect out_graph_bounds = cb.getOutBounds();
//                	_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//                }
//                cb.drawBound(gl);
//            }
//        }else{
//        	//2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 >>
////        	if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) || _cvm.getSkinType() == COMUtil.SKIN_BLACK || _cvm.bIsMiniBongChart) {
//        	if(_cvm.getSkinType() == COMUtil.SKIN_BLACK || _cvm.bIsMiniBongChart) {
//        	//2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 <<
//	            xscale.draw(gl);
//	            Block stand=getChartBlockByType(Block.STAND_BLOCK);
//	            if(stand!=null){
//	                stand.draw(gl);
//	            }else{
//	                for(int i=0;i<blocks.size();i++){
//	                    Block cb = (Block)blocks.elementAt(i);
//	                    cb.draw(gl);
//	                }
//	            }
//        	}
//        	else
//        	{
//	            Block stand=getChartBlockByType(Block.STAND_BLOCK);
//	            if(stand!=null){
//	                Rect out_graph_bounds = stand.getOutBounds();
//	                _cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//	                xscale.draw(gl);
//	                stand.draw(gl);
//	            }else{
//	            	for(int i=0;i<blocks.size();i++){
//	                    Block cb = (Block)blocks.elementAt(i);
//	                    Rect out_graph_bounds = cb.getOutBounds();
//	                    _cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//	            	}
//	                xscale.draw(gl);
//	                for(int i=0;i<blocks.size();i++){
//	                    Block cb = (Block)blocks.elementAt(i);
//	                    cb.draw(gl);
//	                }
//	            }
//        	}
//        }
//    }

    //2015. 10. 19 현재가차트 투명 처리>>
    public void drawBlocks(Canvas gl){
        if(xscale==null||blocks==null) return;

        int[] nRectColor = {255,255,255};
        int[] nRectBottomColor = {255,255,255};
        //int[] nRectLineColor = {240, 240, 240};
        int[] nRectLineColor = {187, 187, 187};
        int[] nRectTopLineColor = {230, 230, 230};

        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 >>
        if(_cvm.g_nFontSizeBtn == 0)
            _cvm.mPaint_Text.setTextSize(COMUtil.nFontSize_paint-COMUtil.getPixel(2));
        else if(_cvm.g_nFontSizeBtn == 2)
            _cvm.mPaint_Text.setTextSize(COMUtil.nFontSize_paint+COMUtil.getPixel(2));
        else
            _cvm.mPaint_Text.setTextSize(COMUtil.nFontSize_paint);
        //2020.12.28 by HJW - 폰트 사이즈 옵션 추가 <<

//		if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
//			nRectColor[0] = 18;
//			nRectColor[1] = 28;
//			nRectColor[2] = 40;
        //2021.06.11 by hanjun.Kim - kakaopay - 차트 배경 수정(Y축 배경) >>
        nRectColor = COMUtil.convertColorToRGBArray(CoSys.CHART_BACK_MAINCOLOR);

//			nRectBottomColor[0] = 18;
//			nRectBottomColor[1] = 28;
//			nRectBottomColor[2] = 40;
        nRectBottomColor = COMUtil.convertColorToRGBArray(CoSys.CHART_BACK_MAINCOLOR);
        //2021.06.11 by hanjun.Kim - kakaopay - 차트 배경 수정(Y축 배경) <<
        nRectTopLineColor = CoSys.rectLineColor;
//			nRectLineColor[0] = 68;
//			nRectLineColor[1] = 68;
//			nRectLineColor[2] = 68;
//		}

        if(_cvm.bIsNewsChart)
        {
//			int[] nBottomColor = {46,58,92};
            int[] nBottomColor = {255,255,255};
            for(int i=0;i<blocks.size();i++){
                Block cb = (Block)blocks.elementAt(i);

                RectF out_graph_bounds = cb.getOutBounds();
                cb.drawBound(gl);
                if(i==0)
                {
                    _cvm.drawFillRect(gl, chart_bounds.left, chart_bounds.bottom-(_cvm.XSCALE_H-COMUtil.getPixel(1)), out_graph_bounds.right, _cvm.XSCALE_H, nBottomColor, 1.0f);
                }
                //_cvm.drawLine(gl, out_graph_bounds.left,chart_bounds.top+1,chart_bounds.right,chart_bounds.top+1, nBottomColor, 1.0f);
                _cvm.drawLine(gl, out_graph_bounds.left,out_graph_bounds.top+1,chart_bounds.right,out_graph_bounds.top+1, CoSys.WHITE, 0.3f);
                //_cvm.drawLine(gl, out_graph_bounds.right,out_graph_bounds.top,out_graph_bounds.right,chart_bounds.bottom, nBottomColor, 1.0f);
            }
        }

        //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가. && !_cvm.bIsUpdownChart
        if(_cdm.getCount()<1 && !_cvm.bIsLineChart && !_cvm.bIsLine2Chart && !_cvm.bIsUpdownChart && !_cvm.bIsUpdownGridChart){
            if (!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                if (_cvm.m_bCurrentChart || _cvm.bIsMiniBongChart || _cvm.bInvestorChart || (_cvm.bIsLineFillChart && !_cvm.bIsHighLowSign)) {
                    for (int i = 0; i < blocks.size(); i++) {
                        Block cb = (Block) blocks.elementAt(i);

                        RectF out_graph_bounds = cb.getOutBounds();
                        if(!_cvm.bIsTransparent) {
                            _cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), nRectColor, 1.0f);
                        }
                        cb.drawBound(gl);
                    }
                }
                else {
                    for (int i = 0; i < blocks.size(); i++) {
                        Block cb = (Block) blocks.elementAt(i);

                        RectF out_graph_bounds = cb.getOutBounds();
                        cb.drawBound(gl);
                        if (i == 0) {
                            _cvm.drawFillRect(gl, out_graph_bounds.right, out_graph_bounds.top, chart_bounds.width() - out_graph_bounds.right, chart_bounds.height(), nRectColor, 1.0f);
                            _cvm.drawFillRect(gl, chart_bounds.left, chart_bounds.bottom - (_cvm.XSCALE_H - COMUtil.getPixel(1)), chart_bounds.right, _cvm.XSCALE_H, nRectBottomColor, 1.0f);
                        }
//						_cvm.drawLine(gl, out_graph_bounds.left, chart_bounds.top, chart_bounds.right, chart_bounds.top, nRectLineColor, 1.0f);
//						_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top, chart_bounds.right, out_graph_bounds.top, nRectLineColor, 1.0f);
//						_cvm.drawLine(gl, out_graph_bounds.right, out_graph_bounds.top, out_graph_bounds.right, chart_bounds.bottom, nRectLineColor, 1.0f);
                    }
                }
            }
            else {
                for (int i = 0; i < blocks.size(); i++) {
                    Block cb = (Block) blocks.elementAt(i);
                    cb.drawBound(gl);
                }
            }
            //2013. 3. 29  스킨에 따라서 x, yscale 색상 변경
        }
        else {
            if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                //xscale.draw(gl);
                Block stand = getChartBlockByType(Block.STAND_BLOCK);
                if (stand != null) {
                    stand.draw(gl);
                    drawXScale(gl, stand);
                }
                else {
                    Block cb = null;
                    for (int i = 0; i < blocks.size(); i++) {
                        cb = (Block) blocks.elementAt(i);
                        cb.draw(gl);
                    }
                    drawXScale(gl, stand);
                }
            }
            else {
                Block stand = getChartBlockByType(Block.STAND_BLOCK);
                if (stand != null) {
//	                Rect out_graph_bounds = stand.getOutBounds();
//	                _cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//	                xscale.draw(gl);
//	                stand.draw(gl);
                    RectF out_graph_bounds = stand.getOutBounds();
                    //2013. 3. 29  스킨에 따라서 x, yscale 색상 변경
                    _cvm.drawFillRect(gl, out_graph_bounds.right, out_graph_bounds.top, chart_bounds.width() - out_graph_bounds.right, chart_bounds.height(), nRectColor, 1.0f);
                    _cvm.drawFillRect(gl, chart_bounds.left, chart_bounds.bottom - (_cvm.XSCALE_H - COMUtil.getPixel(1)), chart_bounds.right, _cvm.XSCALE_H, nRectBottomColor, 1.0f);
                    //_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
                    //xscale.draw(gl);

                    //2021.04.19 by lyk - kakaopay - 초기화 후 stand 타입 차트 추가시 xscale 날짜 표시가 처음에 안되는 오류 수정 (draw 위치 바꿈) >>
                    stand.draw(gl);
                    drawXScale(gl, stand);
                    //2021.04.19 by lyk - kakaopay - 초기화 후 stand 타입 차트 추가시 xscale 날짜 표시가 처음에 안되는 오류 수정 (draw 위치 바꿈) <<

                    //drawXScale(gl, stand);
                    _cvm.setLineWidth_Fix(COMUtil.getPixel(0.5f)); //차트 상단라인
//					_cvm.drawLine(gl, out_graph_bounds.left, chart_bounds.top + 1, chart_bounds.right, chart_bounds.top + 1, nRectTopLineColor, 1.0f);
//					_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top + 1, chart_bounds.right, out_graph_bounds.top + 1, nRectLineColor, 1.0f); //차트 상단 라인
//					_cvm.drawLine(gl, out_graph_bounds.right, out_graph_bounds.top, out_graph_bounds.right, chart_bounds.bottom, nRectLineColor, 1.0f);
//					_cvm.drawLine(gl, chart_bounds.right - 1, chart_bounds.top + 1, chart_bounds.right - 1, chart_bounds.bottom, nRectLineColor, 1.0f);

                    //2021.06.30 by lyk - kakaopay - 차트 최하단 외곽 가로선 (xscale, eventBadge 제외)
                    _cvm.drawLine(gl, out_graph_bounds.left, chart_bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H, chart_bounds.right, chart_bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H, nRectTopLineColor, 0.2f);

                    _cvm.setLineWidth(1);
                }
                else {
                    if (_cvm.bIsMiniBongChart) {
                        //2021.01.19 by HJW - mini봉 차트 배경 삭제 >>
//						for (int i = 0; i < blocks.size(); i++) {
//							Block cb = (Block) blocks.elementAt(i);
//							RectF out_graph_bounds = cb.getOutBounds();
//							_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), nRectColor, 1.0f);
//							_cvm.setLineWidth_Fix(COMUtil.getPixel(1));
//							_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.bottom, chart_bounds.right, out_graph_bounds.bottom, CoSys.WHITE, 1.0f);
//							_cvm.setLineWidth(1);
//						}
                        //2021.01.19 by HJW - mini봉 차트 배경 삭제 <<
                        for (int i = 0; i < blocks.size(); i++) {
                            Block cb = (Block) blocks.elementAt(i);
                            cb.draw(gl);
                        }
                    }
//					else if (_cvm.m_bCurrentChart || _cvm.bIsInnerText) {
//						for (int i = 0; i < blocks.size(); i++) {
//							Block cb = (Block) blocks.elementAt(i);
//							RectF out_graph_bounds = cb.getOutBounds();
//							_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), nRectColor, 1.0f);
//							_cvm.setLineWidth_Fix(COMUtil.getPixel(1));
//							if (cb.getYScalePos() == 0)    //왼쪽
//							{
//								_cvm.drawLine(gl, chart_bounds.left, out_graph_bounds.bottom, chart_bounds.right, out_graph_bounds.bottom, nRectLineColor, 1.0f);
//								_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.left, out_graph_bounds.bottom, nRectLineColor, 1.0f);
//							}
//							else {
//								_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.bottom, chart_bounds.right, out_graph_bounds.bottom, nRectLineColor, 1.0f);
//								_cvm.drawLine(gl, out_graph_bounds.right, out_graph_bounds.top, out_graph_bounds.right, out_graph_bounds.bottom, nRectLineColor, 1.0f);
//							}
//							_cvm.setLineWidth(1);
//						}
////		                xscale.draw(gl);
//						drawXScale(gl, blocks.elementAt(0));    //2014. 1. 7 시세종합 차트 스크롤바 생성
//						for (int i = 0; i < blocks.size(); i++) {
//							Block cb = (Block) blocks.elementAt(i);
//							cb.draw(gl);
//						}
//						//drawXScale(gl, blocks.elementAt(0));    //2014. 1. 7 시세종합 차트 스크롤바 생성
//					}
                    else {
                        Block cb = (Block) blocks.elementAt(0);
                        RectF out_graph_bounds = cb.getOutBounds();

                        //2013.10.07 by LYH >> 스케일 왼쪽
                        if (!_cvm.bIsLineChart && !_cvm.bIsTransparent) {
                            if (cb.getYScalePos() == 0)    //왼쪽
                            {
                                _cvm.drawFillRect(gl, chart_bounds.left, out_graph_bounds.top, out_graph_bounds.left, chart_bounds.height(), nRectColor, 1.0f);
                                _cvm.drawFillRect(gl, out_graph_bounds.left, chart_bounds.bottom - (_cvm.XSCALE_H - COMUtil.getPixel(1)), out_graph_bounds.right, _cvm.XSCALE_H, nRectBottomColor, 1.0f);
                            }
                            else {
                                _cvm.drawFillRect(gl, out_graph_bounds.right, out_graph_bounds.top, chart_bounds.width() - out_graph_bounds.right, chart_bounds.height(), nRectColor, 1.0f);
//								_cvm.drawFillRect(gl, chart_bounds.left, chart_bounds.bottom - (_cvm.XSCALE_H - COMUtil.getPixel(1)), out_graph_bounds.right, _cvm.XSCALE_H, nRectBottomColor, 1.0f);
                            }
                        }
                        //2013.10.07 by LYH <<

                        //xscale.draw(gl);
                        //2020.07.06 by LYH >> 캔들볼륨 >>
                        if(basic_block != null) {
                            _cvm.m_arrArea.clear();
                            for (int j = 0; j < basic_block.getGraphs().size(); j++) {
                                AbstractGraph graph = (AbstractGraph) basic_block.getGraphs().get(j);
                                if (graph.graphTitle.equals("일본식봉") ) {
                                    if(graph.getDrawTool().get(0).getDrawType2() == 6 || graph.getDrawTool().get(0).getDrawType2() == 7)	//Candle Volume, Equi Volume
                                        xscale.calcVolumeScale();
                                    break;
                                }
                            }
                        }
                        //2020.07.06 by LYH >> 캔들볼륨 <<

                        drawXScale(gl, cb);
                        for (int i = 0; i < blocks.size(); i++) {
                            cb = (Block) blocks.elementAt(i);
                            cb.draw(gl);
                            //if(i < blocks.size()-1)
                            cb.drawBoundLine(gl);
                        }
                        //drawXScale(gl, cb);
                        _cvm.setLineWidth_Fix(COMUtil.getPixel_H(0.5f));
                        if (!_cvm.bIsTransparent && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsLine2Chart && !_cvm.bIsTodayLineChart && _cvm.getAssetType()<1) {
                            //2013.10.07 by LYH >> 스케일 왼쪽
                            if (cb.getYScalePos() == 0)    //왼쪽
                            {
//                                if(_cvm.bIsShowTitle)
//                                {
//                                    _cvm.drawLine(gl, chart_bounds.left,chart_bounds.top+1,out_graph_bounds.right,chart_bounds.top+1, nRectLineColor, 1.0f);
//                                    _cvm.drawLine(gl, chart_bounds.left,out_graph_bounds.top+1,out_graph_bounds.left,out_graph_bounds.top+1, nRectLineColor, 1.0f);
//                                    _cvm.drawLine(gl, chart_bounds.right-1,chart_bounds.top+1,chart_bounds.right-1,chart_bounds.bottom, nRectLineColor, 1.0f);
//                                }
                                if(_cvm.m_nChartType != ChartViewModel.CHART_THREE_ROUNDED_BAR)
                                    _cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.left, chart_bounds.bottom, nRectLineColor, 1.0f);
                            }
                            else if (cb.getYScalePos() == 2)    //양쪽
                            {
                                _cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.left, chart_bounds.bottom, nRectLineColor, 1.0f);
                                _cvm.drawLine(gl, out_graph_bounds.right, out_graph_bounds.top, out_graph_bounds.right, chart_bounds.bottom, nRectLineColor, 1.0f);
                            }
                            //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                            else if (cb.getYScalePos() == 3)   //없음
                            {

                            }
                            //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트) end
                            else {
                                if(!_cvm.bInvestorChart) {
                                    //2021.04.29 by lyk - kakaopay - 차트 최상단 외곽 가로선
//									_cvm.drawLine(gl, out_graph_bounds.left, chart_bounds.top + 1, chart_bounds.right, chart_bounds.top + 1, nRectTopLineColor, 1.0f);

                                    //2021.04.29 by lyk - kakaopay - 차트 최하단 외곽 가로선 (xscale, eventBadge 제외)
                                    _cvm.setLineWidth(0.5f);
                                    _cvm.drawLine(gl, out_graph_bounds.left, chart_bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H + COMUtil.getPixel(0.5f), chart_bounds.right, chart_bounds.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H + COMUtil.getPixel(0.5f), nRectTopLineColor, 0.2f);

                                    //_cvm.drawLine(gl, out_graph_bounds.left, out_graph_bounds.top + 1, chart_bounds.right, out_graph_bounds.top + 1, nRectLineColor, 1.0f);

                                    //2021.04.29 by lyk - kakaopay - 차트와 YScale 사이 세로 구분선 주석처리
//									_cvm.drawLine(gl, out_graph_bounds.right, out_graph_bounds.top, out_graph_bounds.right, chart_bounds.bottom, nRectLineColor, 1.0f);
                                }
//								if (!_cvm.bIsLineFillChart && !_cvm.bInvestorChart) {
//									_cvm.drawLine(gl, chart_bounds.right - 1, chart_bounds.top + 1, chart_bounds.right - 1, chart_bounds.bottom, nRectLineColor, 1.0f);
//								}

                                //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
                                if (_cvm.bIsUpdownChart) {
                                    _cvm.drawLine(gl, chart_bounds.left, xscale.getBounds().top, out_graph_bounds.right, xscale.getBounds().top, nRectLineColor, 1.0f);
                                }
                                //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<
                            }
                        }
                        //2013.10.07 by LYH <<
//						drawXScale(gl, cb);

                        _cvm.setLineWidth(1);
                        if (basic_block.getYScale() != null) {   //2016.12.01 by LYH >> 스케일 없슴 옵션 추가(테마분석 차트)
                            YScale yscale = basic_block.getYScale()[0];
                            yscale.drawPriceLimit(gl);
                        }

//	                    _cvm.drawFillRect(gl, out_graph_bounds.right, out_graph_bounds.top, _cvm.Margin_R, +chart_bounds.height(), CoSys.WHITE, 1.0f);
//	                    _cvm.drawFillRect(gl, out_graph_bounds.right, out_graph_bounds.top, _cvm.Margin_R, +chart_bounds.height(), CoSys.WHITE, 1.0f);
                    }

                }
            }
        }
    }
    //2015. 10. 19 현재가차트 투명 처리<<

    public void drawXScale(Canvas gl, Block cb)
    {
//		if(_cvm.bIsLine2Chart || _cvm.getAssetType() == ChartViewModel.ASSET_UPDOWN_BAR)
//			return;
        if(_cvm.getAssetType() == ChartViewModel.ASSET_UPDOWN_BAR)
            return;
//		RectF xScaleBounds = xscale.getBounds();
//		//수정
//		if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart &&!_cvm.bIsLineFillChart && !_cvm.bIsNewsChart && !_cvm.bInvestorChart && basic_block != null) {
//			if (_cvm.getSkinType() == COMUtil.SKIN_WHITE) {
//
//				int[] fillCol = COMUtil.convertColorToRGBArray(CoSys.CHART_BACK_MAINCOLOR);
//				_cvm.drawFillRect(gl, xScaleBounds.left, xScaleBounds.top, chart_bounds.width(), xScaleBounds.height(), fillCol, 1.0f);
//			} else {
//				int[] fillCol = COMUtil.convertColorToRGBArray(CoSys.CHART_BACK_MAINCOLOR);
//				_cvm.drawFillRect(gl, xScaleBounds.left, xScaleBounds.top, chart_bounds.width(), xScaleBounds.height(), fillCol, 1.0f);
//			}
//		}
        xscale.draw(gl);
        if(_cvm.bIsLineChart)
            return;
//		RectF out_graph_bounds = cb.getOutBounds();
//		if(_cvm.getIndex()+_cvm.getViewNum()>_cdm.getCount())
//			return;
//		float x1 = out_graph_bounds.left;
//		//float x2 = out_graph_bounds.left+out_graph_bounds.width();
//		float x2 = chart_bounds.right;
//		int sWidth = (int)(_cvm.getDataWidth() * _cdm.getCount());
//		int vX = (int)(_cvm.getDataWidth() * _cvm.getViewNum());
//		int sX = (int)(_cvm.getDataWidth() * _cvm.getIndex());
//		if(vX==0) return;
//		float ratio = (float)vX/(float)sWidth;
//		float x = cb.getGraphBounds().left+sX * ratio ;
//		float y = xscale.getBounds().top-(int)COMUtil.getPixel(3);
//
//		float w = vX * ratio;
////        float h = (int)COMUtil.getPixel(2);
//		float h = (int)COMUtil.getPixel(2);
        //Rect rectScroll = new Rect((int)x, (int)y, (int)w+(int)x, (int)h+(int)y);
        //g.drawRect(rectScroll, pnt);

        //2014. 2. 6 시세종합 미니봉차트 포팅>>
//        if(!_cvm.bIsLineFillChart)
//		if(!_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsMiniBongChart && !_cvm.bInvestorChart)	//2014. 2. 6 시세종합 미니봉차트 포팅<<
//		{
        //기존
//        	_cvm.drawFillRect(gl, x, y, w, h, CoSys.scrollLineColor, 1.0f);
        //2013.03.26 by LYH >> 디자인 적용.
        //_cvm.drawLine(gl, this.out_graph_bounds.left, y+h+(int)COMUtil.getPixel(1), this.out_graph_bounds.left+this.out_graph_bounds.width(), y+h+(int)COMUtil.getPixel(1), rectLineColor ,1.0f);
//	        _cvm.drawLine(gl, x1, y+h+(int)COMUtil.getPixel(1), x2, y+h+(int)COMUtil.getPixel(1), rectLineColor ,1.0f);
        //2013.03.26 by LYH <<

        //수정
//			if(_cvm.getSkinType() == COMUtil.SKIN_WHITE)
//			{
////				int[] fillCol = {220, 222, 224};
////				_cvm.drawFillRect(gl, out_graph_bounds.left, y, out_graph_bounds.width(), h, fillCol, 1.0f);
//
//				int[] scrollbarColor = {102, 102, 102};
//				_cvm.drawFillRect(gl, x+(int)COMUtil.getPixel(3), y, w-(int)COMUtil.getPixel(6), h, scrollbarColor, 1.0f);
//				_cvm.drawCircle(gl, (int)x, (int)y, x+(int)COMUtil.getPixel(6), y+h, true, scrollbarColor);
//				int nX = (int)(x+w)-(int)COMUtil.getPixel(6);
//				_cvm.drawCircle(gl, nX, (int)(y), nX+(int)COMUtil.getPixel(6), y+h, true, scrollbarColor);
//
//				//2013.03.26 by LYH >> 디자인 적용.
//				//_cvm.drawLine(gl, this.out_graph_bounds.left, y+h+(int)COMUtil.getPixel(1), this.out_graph_bounds.left+this.out_graph_bounds.width(), y+h+(int)COMUtil.getPixel(1), rectLineColor ,1.0f);
//				_cvm.setLineWidth_Fix((int)COMUtil.getPixel(1));
//				int[] lineCol = {209, 209, 209};
//				//_cvm.drawLine(gl, x1, y+h+(int)COMUtil.getPixel(1), x2, y+h+(int)COMUtil.getPixel(1), lineCol ,1.0f);
//				_cvm.drawLine(gl, x1, y+h+(int)COMUtil.getPixel(1), x2, y+h+(int)COMUtil.getPixel(1), lineCol ,1.0f);
//				_cvm.setLineWidth(1);
//			}
//			else
//			{
//				int[] fillCol = {0, 0, 0};
//				_cvm.drawFillRect(gl, out_graph_bounds.left, y, out_graph_bounds.width(), h, fillCol, 1.0f);
//
//				int[] scrollbarColor = {109, 120, 138};
//				_cvm.drawFillRect(gl, x+(int)COMUtil.getPixel(3), y, w-(int)COMUtil.getPixel(6), h, scrollbarColor, 1.0f);
//				_cvm.drawCircle(gl, (int)x, (int)y, x+(int)COMUtil.getPixel(6), y+h, true, scrollbarColor);
//				int nX = (int)(x+w)-(int)COMUtil.getPixel(6);
//				_cvm.drawCircle(gl, nX, (int)(y), nX+(int)COMUtil.getPixel(6), y+h, true, scrollbarColor);
//
//				//2013.03.26 by LYH >> 디자인 적용.
//				//_cvm.drawLine(gl, this.out_graph_bounds.left, y+h+(int)COMUtil.getPixel(1), this.out_graph_bounds.left+this.out_graph_bounds.width(), y+h+(int)COMUtil.getPixel(1), rectLineColor ,1.0f);
//				_cvm.setLineWidth_Fix((int)COMUtil.getPixel(1));
//				int[] lineCol = {51, 51, 51};
//				_cvm.drawLine(gl, x1, y+h+(int)COMUtil.getPixel(1), x2, y+h+(int)COMUtil.getPixel(1), lineCol ,1.0f);
//				_cvm.setLineWidth(1);
//			}
//		}
    }
    Canvas bufCanvas=null;
    int gab = 20;
    boolean off=false;
    String countTxt;
    int count;

    //====================================
    // 1. 해당 구역에서 리사이즈가 될 수 있도록 셋
    // 2. 십자선, 말풍선
    //====================================

    //터치 이벤트 핸들러
    int p_count=0;
    int distance = 0;
    int newDistance = 0;
    int preDistance = 0;
    int sbExtentValue=0;
    PointF location=new PointF(0,0);
    PointF preLocation=new PointF(0,0);
    float preLocationX = -1;
    private int distanceTwoPoint(Point ptA , Point ptB) {
        int start = getXToDate(ptA.x);
        int end = getXToDate(ptB.x);

        return start-end;
    }
    public void selectChart() {
//    	if(COMUtil.symbol != _cdm.codeItem.strCode)
//    	{
        if (COMUtil._mainFrame == null) {
            return;
        }
        COMUtil.symbol = _cdm.codeItem.strCode;
        COMUtil.lcode = _cdm.codeItem.strRealKey;
        COMUtil.dataTypeName = _cdm.codeItem.strDataType;
        COMUtil.unit = _cdm.codeItem.strUnit;
        COMUtil.market = _cdm.codeItem.strMarket;

        if(_cvm.chartType != COMUtil.COMPARE_CHART) {
            if(_cdm.codeItem.strRealKey.equals("SC0")) {
                COMUtil.apCode = COMUtil.TR_CHART_FUTURE;
//				} else if(_cdm.codeItem.strRealKey.equals("JS0")) {
//					COMUtil.apCode = COMUtil.TR_CHART_UPJONG;
            } else {
                COMUtil.apCode = COMUtil.TR_CHART_STOCK;
            }
        }

        COMUtil.codeName = _cdm.codeItem.strName;
        COMUtil.preSymbol = _cdm.codeItem.strCode;
        COMUtil.nkey = _cdm.codeItem.strNextKey;
//    	}

        if(COMUtil.apiMode) {
            if(_cvm.chartType != COMUtil.COMPARE_CHART) {
                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                base11.sendTR(String.valueOf(COMUtil._TAG_SELECTED_CHART));
                //2014. 2. 3 분석툴바 열려있을 때 차트화면 선택하면 닫히는 기능 막기>> : 태블릿만
                if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
                {
                    base11.hideToolBar();
                }
                //2014. 2. 3 분석툴바 열려있을 때 차트화면 선택하면 닫히는 기능 막기<<
            }
        } else {
            //apiMode 분리. (viewCnt 정보 전달)
            COMUtil._mainFrame.mainBase.setCodeName(_cdm.codeItem.strName);
            COMUtil._mainFrame.mainBase.setPeriodName("");
            COMUtil._mainFrame.mainBase.setCountText("");
        }
    }
    public void selectBaseChart() {
        //2012.11.29 by LYH >> 현재 화면 ctlchartex의 메인 프레임으로 변경처리 <<
        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SELECT_VIEW, null);
        //2023.05.02 by SJW - 차트 탭 스와이프 시 크래시 수정 >>
//		COMUtil._mainFrame.mainBase.selectChart(this);
        if (COMUtil._mainFrame != null) {
            COMUtil._mainFrame.mainBase.selectChart(this);
        }
        //2023.05.02 by SJW - 차트 탭 스와이프 시 크래시 수정 <<
    }

    int nTouchesEnd = 0;
    private void handleLongPress() {
        //2014. 2. 3 마운틴차트에서 드래그 이벤트 메인에 전달되게 수정>>
        if(_cvm.bIsLineFillChart && !_cvm.bIsOneQStockChart)
        {
            return;
        }
        //2014. 2. 3 마운틴차트에서 드래그 이벤트 메인에 전달되게 수정<<
        curPoint = pressPoint;

        Block dnBlock = getDnChartBlock(curPoint);

        //2015. 1. 22 yscale 롱터치시 수평선 그리기 >>
//        if(_cvm.getToolbarState()==COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ)
//    	{
//        	//수평선으로 분석툴사용상태 변경
//    		_cvm.setToolbarState(COMUtil.TOOLBAR_CONFIG_HORZ);
//
//    		//수평선 추가
//        	Point p = new Point(0, curPoint.y);
//        	AnalTool at = getAnalTool(_cvm.getToolbarState(), getChartBlock(p));
//    		at.addPoint(p);
//    		analDragState = false;
//    		if(analTools==null)analTools=new Vector<AnalTool>();
//    		analTools.add(at);
//
//    		//다시 yscale 분석툴 상태로 (롱터치시 기능을 완전 끝낼려면  9999로 준다)
//        	_cvm.setToolbarState(COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ);
//
//    		return;
//    	}
        //2015. 1. 22 yscale 롱터치시 수평선 그리기 <<

        //2023.02.09 by SJW - 블록병합 기능 제거 >>
//		//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 >>
//		this.hideChartBlockPopup();
//
//		selectedMoveBlock = null;
//		selectedMoveGraph = null;
//		selectedMoveDrawTool = null;
//
//		for (Block chartBlock : blocks) {
//			for (AbstractGraph graph : chartBlock.getGraphs()) {
//				for (DrawTool drawTool : graph.getDrawTool()) {
//					if (drawTool.isVisible() && drawTool.getTitleBounds().contains(curPoint.x, curPoint.y)) {
//						selectedMoveBlock = chartBlock;
//						selectedMoveGraph = graph;
//						selectedMoveDrawTool = drawTool;
//						break;
//					}
//				}
//				if (selectedMoveGraph != null) {
//					break;
//				}
//			}
//			if (selectedMoveBlock != null) {
//				break;
//			}
//		}
//
//		if (selectedMoveGraph != null) {
//			String strGraphName = selectedMoveGraph.getName();
//
//			//가격차트 이동되지 않도록 처리
//			if (strGraphName.equals("일본식봉") || strGraphName.equals(COMUtil.CANDLE_TYPE_HEIKIN_ASHI) || _cvm.chartType == COMUtil.COMPARE_CHART) {
//				selectedMoveBlock = null;
//				selectedMoveGraph = null;
//				selectedMoveDrawTool = null;
//			}
//
//			//투자자차트 이동되지 않도록 처리
//			if (strGraphName.startsWith("가격차트")){
//				selectedMoveBlock = null;
//				selectedMoveGraph = null;
//				selectedMoveDrawTool = null;
//			}
//
//			//데이터가 없는 경우 이동되지 않도록 처리
//			if (_cdm.getCount() <= 0){
//				selectedMoveBlock = null;
//				selectedMoveGraph = null;
//				selectedMoveDrawTool = null;
//			}
//
//			if (selectedMoveDrawTool != null) { //지표 선택 시
//				curPoint = location;
//				touchesMoved = true;    //터치하고 있을때 drawAnalTool_MouseDrag가 실행되도록 설정
//				funcName = "drawAnalTool_MouseDrag";
//				repaintAll();
//				return;
//			}
//		}
//		//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 <<
        //2023.02.09 by SJW - 블록병합 기능 제거 <<

        //2019. 07. 03 by hyh - 추세선이 선택 돼 있을 때에는 수치조회 되지 않도록 처리 >>
        if (select_at != null) {
            return;
        }
        //2019. 07. 03 by hyh - 추세선이 선택 돼 있을 때에는 수치조회 되지 않도록 처리 <<

//		if(_cdm.getCount()>1&&!_cvm.isStandGraph()) {
//			if(dnBlock!=null && !_cvm.isChange()){	//블럭 resize 상태일때 처리
//				touchesMoved = true;
//				currY = curPoint.y;
//				resize_block = dnBlock;
//				funcName = "drawAnalTool_MouseDrag";
//				_cvm.setResizeState(true);
//				repaintAll();
//
//				mHandler.removeMessages(LONG_PRESS);
//				return;
//			}
//		}

//		m_bShowToolTip = true; //2021.05.14 by lyk - KakaoPay - 롱터치 이벤트 막기(원터치로 변경)

        //2017.07.25 by pjm >> Y축 움직여 확대/축소
        m_nOrgViewNum = -1;
        m_nOrgIndex = -1;
        //2017.07.25 by pjm << Y축 움직여 확대/축소

        //2013. 1.    차트화면 롱클릭시 뷰패널(수치조회창) or 지표설정창(보조지표리스트)  보이게
//		if(_cvm.chartType == COMUtil.COMPARE_CHART || COMUtil.isSetScreenViewPanel())
//		{
//			// 2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 Start
//			if (!_cvm.isCrosslineMode && viewP != null) {
//				if (_cvm.chartType == COMUtil.COMPARE_CHART) {
//					//2012. 7. 16   롱클릭시 십자선데이터패널 표시
//					_cvm.isCrosslineMode = true;
//
//					viewP.showCloseButton(true);    // 닫기 버튼 보이게
//					viewP.setVisibility(View.VISIBLE);
//				} else {
//					// 롱 클릭시 십자선 패널 보이게
//					Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
//					base11.showCrossLineLongClick(true, this);
//				}
//			}
//			//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End
//		}
//		else
//		{
//			//롱클릭시 지표리스트팝업
////        	Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
//			openPopupJipyoList();
//		}
        this.repaintAll();
    }

    public void openPopupJipyoList() {
        if (popupJipyoList == null) {
            WindowManager wm = COMUtil._chartMain.getWindowManager();
            Display display = wm.getDefaultDisplay();

            int width = display.getWidth();
            int height = display.getHeight();

            RelativeLayout simpleLayout = new RelativeLayout(context);
            simpleLayout.setLayoutParams(new LayoutParams(width, height));
            simpleLayout.setFocusable(true);
            simpleLayout.setBackgroundColor(Color.argb(70, 0, 0, 0));
            simpleLayout.setGravity(Gravity.CENTER);
            simpleLayout.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if (popupJipyoList != null) {
                        popupJipyoList.dismiss();
                        popupJipyoList = null;
                    }
                }
            });

            final JipyoListViewByLongTouch jipyoListView = new JipyoListViewByLongTouch(context, simpleLayout);

            popupJipyoList = new PopupWindow(simpleLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            popupJipyoList.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
            popupJipyoList.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
            popupJipyoList.showAtLocation(COMUtil.apiView.getRootView(), Gravity.CENTER, 0, 0);

            int resId = COMUtil.apiView.getContext().getResources().getIdentifier("frameabtnfunction", "id", COMUtil.apiView.getContext().getPackageName());
            Button btnClose = (Button) simpleLayout.findViewById(resId);

            btnClose.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (popupJipyoList != null) {
                        popupJipyoList.dismiss();
                        popupJipyoList = null;

                        popupJipyoListCloseEvent();
                    }

                    //2017. 3. 9 by hyh - 지표 상세설정 여러개 팝업되지 않도록 처리 >>
                    if(jipyoListView != null) {
                        jipyoListView.closePopupViews();
                    }
                    //2017. 3. 9 by hyh - 지표 상세설정 여러개 팝업되지 않도록 처리 <<
                }
            });
        }
    }

    public void popupJipyoListCloseEvent() {

        //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다)
        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
        if(!COMUtil._mainFrame.sendTrType.equals("storageType") && base11.divideStorageType!=true) {
            COMUtil.setSavePeriodChartSave();
        }
        //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다) end

        //설정변경사항 체크하여 클라우드에 저장여부 결정.
        String beforeValue = "";
        beforeValue = COMUtil.getCloudData(this.context, null);

        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);

        String afterValue = "";
        afterValue = COMUtil.getCloudData(this.context, null);

        if(!beforeValue.equals(afterValue)) {
            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SAVE_CLOUD, null);
        }
    }

    //2013. 2. 13  차트 확대/축소
    public int getExReViewNum(){
        int rstNum = 0;
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();
        if(cdmCount <= 20)
        {
            return cdmCount;
        }

        if (exReRepeat) {
            if (viewNum == 20)  return 30;
            else                return 20;
        }

        //  축소, viewNum++
//        if (isReduction) {
        if(exReType == CHART_REDUCTION){
            //  viewNum이 cdmCount보다 크거나 같은 경우 => 확대, viewNum--
            if (viewNum >= cdmCount) {
//                isReduction = false;
                exReType = CHART_EXPASION;
                rstNum = getExReViewNum();
            }
            //  viewNum이 cdmCount보다 작은 경우 => 축소, viewNum++
            else{
                rstNum = (int)(viewNum * 1.5f);
                if (rstNum > cdmCount) {
                    rstNum = cdmCount;
                }
            }
        }
        //  확대, viewNum--
        else{
            //  viewNum이 20보다 작은 경우 => 축소, viewNum++
            if (viewNum < 20) {
                isMaxExpansion = true;
                exReType = CHART_REDUCTION;
                rstNum = getExReViewNum();
            }
            //  viewNum이 20보다 큰 경우 => 확대, viewNum--
            else{
                rstNum = (int)(viewNum / 1.5f);

                //  확대한 값이 20보다 작은 경우
                if (rstNum < 20) {
                    exReRepeat = true;
                    exReType = CHART_REDUCTION;
                    rstNum = 20;
                }
            }
        }

        return rstNum;
    }

    //2013. 2. 13  차트 확대/축소
    public int getExReViewNum(int type){
        //  더블탭은 확대로 초기화
        exReType = CHART_EXPASION;

        //  확대축소 반복 초기화
        exReRepeat = false;

        int rstNum = 0;
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();

        if (cdmCount <= 20) {
            return cdmCount;
        }

        //  축소, viewNum++
        if(type == CHART_REDUCTION){
            rstNum = (int)(viewNum * 1.5f);
            if (rstNum > cdmCount) {
                rstNum = cdmCount;
            }
        }
        //  확대, viewNum--
        else{
            rstNum = (int)(viewNum / 1.5f);

            //  확대한 값이 20보다 작은 경우
            if (rstNum < 20) {
                rstNum = 20;
                //  확대축소 반복 활성화
                exReRepeat = true;
            }
        }

        return rstNum;
    }

    //2013. 2. 13  차트 확대/축소
    public void clickButtonToExpansion(){
//        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(hideExReButtonView) object:nil];
//        [self performSelector:@selector(hideExReButtonView) withObject:nil afterDelay:3.0f];

        int idx = _cvm.getIndex();
        int oldViewNum = _cvm.getViewNum();

//        if (oldViewNum <= 20) {
//            return;
//        }

        int newViewNum = getExReViewNum(CHART_EXPASION);
        int cdmCount = _cdm.getCount();

//        idx = idx + oldViewNum/2;
//        int startIdx = idx - newViewNum/2;
//        if (startIdx + newViewNum > cdmCount)
//            startIdx = cdmCount - newViewNum;
        int startIdx = (idx+oldViewNum)-newViewNum;
        //2013. 2. 20  index 가 음수라서 더블탭으로 축소시 앱이 죽는 현상 해결

        if(COMUtil.isZoomCenterView()) //2017.05.23 by PJM 사진줌방식
        {
            newViewNum = oldViewNum/2;
            startIdx = (idx+oldViewNum)-newViewNum-newViewNum/2;
        }
        if(startIdx < 0)
        {
            startIdx = 0;
        }

        _cvm.setViewNum(newViewNum);
        _cvm.setIndex(startIdx);

        repaintAll();
        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, null);
    }

    //2013. 2. 13  차트 확대/축소
    public void clickButtonToReduction(){
//      [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(hideExReButtonView) object:nil];
//      [self performSelector:@selector(hideExReButtonView) withObject:nil afterDelay:3.0f];

        int idx = _cvm.getIndex();
        int cdmCount = _cdm.getCount();
        int oldViewNum = _cvm.getViewNum();

        if (oldViewNum >= cdmCount) {
            return;
        }

        int newViewNum = getExReViewNum(CHART_REDUCTION);

        idx = idx + oldViewNum/2;
        int startIdx = idx - newViewNum/2;
        if (startIdx + newViewNum > cdmCount)
            startIdx = cdmCount - newViewNum;

        //2013. 2. 20  index 가 음수라서 더블탭으로 축소시 앱이 죽는 현상 해결
        if(startIdx < 0)
        {
            startIdx = 0;
        }

        _cvm.setViewNum(newViewNum);
        _cvm.setIndex(startIdx);
        repaintAll();

        //  더블탭은 확대로 초기화
        exReType = CHART_EXPASION;

        //  확대축소 반복 초기화
        exReRepeat = false;
        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, null);
    }

    //  x좌표를 센터로 차트 이동
    //2013. 2. 13  차트 확대/축소
    public void scrollMoveToCenter(float x){
        int idx = getXToDate(x);
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();

        int startIdx = idx - viewNum/2;
        if (startIdx + viewNum > cdmCount)
            startIdx = cdmCount - viewNum;

        //2013. 2. 20  index 가 음수라서 더블탭으로 축소시 앱이 죽는 현상 해결
        if(startIdx < 0)
        {
            startIdx = 0;
        }

        _cvm.setIndex(startIdx);
        repaintAll();
    }

    //2013. 2. 13  차트 확대/축소
    public void scrollMoveToNext(float x){
        int idx = getXToDate(x);
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();

        int startIdx = idx - viewNum/2;
        if (startIdx + viewNum > cdmCount)
            startIdx = cdmCount - viewNum;

        //2013. 2. 20  index 가 음수라서 더블탭으로 축소시 앱이 죽는 현상 해결
        if(startIdx < 0)
        {
            startIdx = 0;
        }

        _cvm.setIndex(startIdx);
        repaintAll();
    }

    //2013. 2. 13  차트 확대/축소
    public void scrollMoveToPrev(float x){
        int idx = getXToDate(x);
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();

        int startIdx = idx - viewNum/2;
        if (startIdx + viewNum > cdmCount)
            startIdx = cdmCount - viewNum;

        //2013. 2. 20  index 가 음수라서 더블탭으로 축소시 앱이 죽는 현상 해결
        if(startIdx < 0)
        {
            startIdx = 0;
        }

        _cvm.setIndex(startIdx);
        repaintAll();
    }
    //2013. 2. 13  차트 확대/축소
//	public void handleChartDoubleTap(){
//	    int newViewNum = getExReViewNum();
//	    _cvm.setViewNum(newViewNum);
//	    float x = curPoint.x;
//	    scrollMoveToCenter(x);
//
//        //viewCnt 변경되면 차트 저장
//        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, null);
//	}
    ImageView m_btnScrollPrev = null, m_btnScrollNext = null;
    private void hideScrollPrevImageView() {
        ScrollPagePrevChartHandler.removeMessages(TAP);
        if(m_btnScrollPrev != null)
        {
            m_btnScrollPrev.startAnimation(hideAnimation);
            m_btnScrollPrev.setVisibility(View.GONE);
        }
    }
    private void hideScrollNextImageView() {
        ScrollPageNextChartHandler.removeMessages(TAP);
        if(m_btnScrollNext != null)
        {
            m_btnScrollNext.startAnimation(hideAnimation);
            m_btnScrollNext.setVisibility(View.GONE);
        }
    }
    private void initScrollImageView(boolean bReset)
    {
        if(bReset && m_btnScrollPrev == null)
            return;

        int layoutResId;

        //2012. 8. 13  일부기기에서 리스트뷰 하단 짤리는 현상 : I95
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
        float btnX = COMUtil.getPixel(20);
        float btnY = param.topMargin + COMUtil.getPixel(30);;

        float nWidth = COMUtil.getPixel(55);
        float nHeight = COMUtil.getPixel(28);

        //이전
        if(m_btnScrollPrev == null)
        {
            m_btnScrollPrev = new ImageView(context);
            String btnImg = "img_scroll_prev";

            layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
            m_btnScrollPrev.setBackgroundResource(layoutResId);

            m_btnScrollPrev.setAnimation(set);
            m_btnScrollPrev.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            RelativeLayout scrollPrevRelative = new RelativeLayout(context);
            RelativeLayout.LayoutParams scrollPrevRelativeParams = new RelativeLayout.LayoutParams((int)nWidth, (int)nHeight);
            scrollPrevRelativeParams.leftMargin = (int)(param.leftMargin+btnX);
            scrollPrevRelativeParams.topMargin = (int)btnY;
            scrollPrevRelative.setLayoutParams(scrollPrevRelativeParams);
            scrollPrevRelative.addView(m_btnScrollPrev);
            layout.addView(scrollPrevRelative);
        }
        else
        {
            if(bReset)
            {
                RelativeLayout.LayoutParams scrollPrevRelativeParams = new RelativeLayout.LayoutParams((int)nWidth, (int)nHeight);
                scrollPrevRelativeParams.leftMargin = (int)(param.leftMargin+btnX);
                scrollPrevRelativeParams.topMargin = (int)btnY;
                ((View)m_btnScrollPrev.getParent()).setLayoutParams(scrollPrevRelativeParams);
            }
        }

        //이후
        RectF bounds = chart_bounds;
        float fRight = bounds.left+bounds.right-TP_WIDTH-(_cvm.Margin_L+_cvm.Margin_R);
        btnX = fRight - btnX -nWidth;
        if(m_btnScrollNext == null)
        {
            m_btnScrollNext = new ImageView(context);
            String btnImg = "img_scroll_next";

            layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
            m_btnScrollNext.setBackgroundResource(layoutResId);

            m_btnScrollNext.setAnimation(set);
            m_btnScrollNext.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            RelativeLayout scrollNextRelative = new RelativeLayout(context);
            RelativeLayout.LayoutParams scrollNextRelativeParams = new RelativeLayout.LayoutParams((int)nWidth, (int)nHeight);
            scrollNextRelativeParams.leftMargin = (int)(param.leftMargin+btnX);
            scrollNextRelativeParams.topMargin = (int)(chart_bounds.top + btnY);
            scrollNextRelative.setLayoutParams(scrollNextRelativeParams);
            scrollNextRelative.addView(m_btnScrollNext);
            layout.addView(scrollNextRelative);
        }
        else
        {
            if(bReset)
            {
                RelativeLayout.LayoutParams scrollNextRelativeParams = new RelativeLayout.LayoutParams((int)nWidth, (int)nHeight);
                scrollNextRelativeParams.leftMargin = (int)(param.leftMargin+btnX);
                scrollNextRelativeParams.topMargin = (int)(chart_bounds.top + btnY);
                ((View)m_btnScrollNext.getParent()).setLayoutParams(scrollNextRelativeParams);
            }
        }
    }
    public void handleChartDoubleTap(){
        initScrollImageView(false);

        m_btnScrollPrev.setVisibility(View.GONE);
        m_btnScrollNext.setVisibility(View.GONE);
        ScrollPageNextChartHandler.removeMessages(TAP);
        ScrollPagePrevChartHandler.removeMessages(TAP);
        float x = curPoint.x;
        int idx = getXToDate(x);
        int viewNum = _cvm.getViewNum();
        int cdmCount = _cdm.getCount();
        int startIdx = _cvm.getIndex();
        //2020.07.06 by LYH >> 캔들볼륨 >>
        //if(idx-startIdx>viewNum/2)
        if(basic_block == null)
            return;
        if(x>(chart_bounds.width() - basic_block.W_YSCALE)/2)	//equivolume
        //2020.07.06 by LYH >> 캔들볼륨 <<
        {
            startIdx = startIdx + viewNum;
            if(startIdx>=cdmCount)
                return;
            if(startIdx>cdmCount-viewNum)
                startIdx = cdmCount-viewNum;
            _cvm.setIndex(startIdx);
            if(m_btnScrollNext != null)
            {
                m_btnScrollNext.startAnimation(showAnimation);
                m_btnScrollNext.setVisibility(View.VISIBLE);
            }
            ScrollPageNextChartHandler.sendEmptyMessageDelayed(TAP, 1000);
        }
        else
        {
            if(startIdx==0)
                return;
            startIdx = startIdx - viewNum;
            if(startIdx<0)
                startIdx = 0;
            _cvm.setIndex(startIdx);
            if(m_btnScrollPrev != null)
            {
                m_btnScrollPrev.startAnimation(showAnimation);
                m_btnScrollPrev.setVisibility(View.VISIBLE);
            }
            ScrollPagePrevChartHandler.sendEmptyMessageDelayed(TAP, 1000);
        }
        repaintAll();
        return;
    }


    //2013. 2. 13  차트 확대/축소
    Button btnExpand = null, btnReduce = null, btnIndicatorList = null;
    Button btnAll = null;
    private void initExpandReduceButtons() {
        if (_cvm.bIsNewsChart || _cvm.bIsLine2Chart || _cvm.bIsTodayLineChart || (_cvm.bIsLineFillChart && _cvm.bIsOneQStockChart)) {
            return;
        }

        int layoutResId;

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();

        int nLeftMargin = (int) COMUtil.getPixel_W(10);
        //int nTopMargin = (int) COMUtil.getPixel(30);
        int nBtnWidth = (int) COMUtil.getPixel_H(24);
        int nBtnHeight = (int) COMUtil.getPixel_H(24);
        int nTopMargin = (int) (basic_block.getBounds().bottom - nBtnHeight - COMUtil.getPixel_H(13)-_cvm.XSCALE_H);
        int nBtnX = param.leftMargin + nLeftMargin;
        int nBtnY = param.topMargin + nTopMargin;


        //2019. 06. 10 by hyh - ChartItemView가 항상 보이므로, 위치 살짝 아래로 이동 >>
        //Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
        //if(base11.isMultiChart()) {
        //	nTopMargin = (int) COMUtil.getPixel(30);
        //}
        //2019. 06. 10 by hyh - ChartItemView가 항상 보이므로, 위치 살짝 아래로 이동 <<

        RelativeLayout reduceRelative = (RelativeLayout) layout.findViewWithTag("reduceLinear");
        if (reduceRelative != null) {
            layout.removeView(reduceRelative);
            btnReduce = null;
        }

        RelativeLayout expandRelative = (RelativeLayout) layout.findViewWithTag("expandLinear");
        if (expandRelative != null) {
            layout.removeView(expandRelative);
            btnExpand = null;
        }

        RelativeLayout indicatorSetRelative = (RelativeLayout) layout.findViewWithTag("indicatorSetLinear");
        if (indicatorSetRelative != null) {
            layout.removeView(indicatorSetRelative);
            btnIndicatorList = null;
        }

        RelativeLayout allRelative = (RelativeLayout) layout.findViewWithTag("allLinear");
        if (allRelative != null) {
            layout.removeView(allRelative);
            btnAll = null;
        }

        //확대버튼
        RelativeLayout.LayoutParams expandRelativeParams = null;
        if (btnExpand == null) {
            if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_zoomin_change_black", "drawable", this.getContext().getPackageName());
            }
            else {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_zoomin_change", "drawable", this.getContext().getPackageName());
            }

            btnExpand = new Button(context);
            btnExpand.setBackgroundResource(layoutResId);
            btnExpand.setAnimation(set);
            btnExpand.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ExpandReduceChartHandler.removeMessages(TAP);
                    ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);

                    clickButtonToExpansion();
                }
            });

            expandRelative = new RelativeLayout(context);
            expandRelative.setTag("expandLinear");
            expandRelativeParams = new RelativeLayout.LayoutParams(nBtnWidth, nBtnHeight);
            expandRelativeParams.leftMargin = nBtnX;
            expandRelativeParams.topMargin = nBtnY;
            expandRelative.setLayoutParams(expandRelativeParams);
            expandRelative.addView(btnExpand);
            layout.addView(expandRelative);
        }

        nLeftMargin = (int) COMUtil.getPixel_W(6);
        //축소버튼
        RelativeLayout.LayoutParams reduceRelativeParams = null;
        if (btnReduce == null && null != expandRelativeParams) {
            if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_zoomout_change_black", "drawable", this.getContext().getPackageName());
            }
            else {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_zoomout_change", "drawable", this.getContext().getPackageName());
            }

            btnReduce = new Button(context);
            btnReduce.setBackgroundResource(layoutResId);
            btnReduce.setAnimation(set);
            btnReduce.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ExpandReduceChartHandler.removeMessages(TAP);
                    ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);

                    clickButtonToReduction();
                }
            });

            reduceRelative = new RelativeLayout(context);
            reduceRelative.setTag("reduceLinear");
            reduceRelativeParams = new RelativeLayout.LayoutParams(nBtnWidth, nBtnHeight);
            reduceRelativeParams.leftMargin = expandRelativeParams.leftMargin + expandRelativeParams.width + nLeftMargin;
            reduceRelativeParams.topMargin = expandRelativeParams.topMargin;
            reduceRelative.setLayoutParams(reduceRelativeParams);
            reduceRelative.addView(btnReduce);
            layout.addView(reduceRelative);
        }

        //지표버튼
        if (btnIndicatorList == null && null != reduceRelativeParams) {
            if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_set_change_black", "drawable", this.getContext().getPackageName());
            }
            else {
                layoutResId = this.getContext().getResources().getIdentifier("btn_chart_set_change", "drawable", this.getContext().getPackageName());
            }

            btnIndicatorList = new Button(context);
            btnIndicatorList.setBackgroundResource(layoutResId);
            btnIndicatorList.setAnimation(set);
            btnIndicatorList.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openPopupJipyoList();
                }
            });

            indicatorSetRelative = new RelativeLayout(context);
            indicatorSetRelative.setTag("indicatorSetLinear");
            RelativeLayout.LayoutParams indicatorSetRelativeParams = new RelativeLayout.LayoutParams(nBtnWidth, nBtnHeight);
            indicatorSetRelativeParams.leftMargin = reduceRelativeParams.leftMargin + reduceRelativeParams.width + nLeftMargin;
            indicatorSetRelativeParams.topMargin = reduceRelativeParams.topMargin;
            indicatorSetRelative.setLayoutParams(indicatorSetRelativeParams);
            indicatorSetRelative.addView(btnIndicatorList);
            layout.addView(indicatorSetRelative);
        }

        //전체보기 버튼
//		if (btnAll == null && basic_block.getOutBounds().width() > COMUtil.getPixel(240)
//		&& !_cvm.bIsAlarmChart
//		&& COMUtil._mainFrame.bShowBtnOpenAllScreen) {
//			if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
//				layoutResId = this.getContext().getResources().getIdentifier("btn_chart_all_change_black", "drawable", this.getContext().getPackageName());
//			}
//			else {
//				layoutResId = this.getContext().getResources().getIdentifier("btn_chart_all_change", "drawable", this.getContext().getPackageName());
//			}
//
//			btnAll = new Button(context);
//			btnAll.setBackgroundResource(layoutResId);
//			btnAll.setAnimation(set);
//			btnAll.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					userProtocol.requestInfo(COMUtil._TAG_OPEN_ALL_SCREEN, null);
//				}
//			});
//
//			allRelative = new RelativeLayout(context);
//			allRelative.setTag("allLinear");
//			RelativeLayout.LayoutParams allRelativeParams = new RelativeLayout.LayoutParams(nBtnWidth, nBtnHeight);
//			allRelativeParams.leftMargin = basic_block.getOutBounds().width() - nBtnWidth;
//			allRelativeParams.topMargin = nTopMargin - (int) COMUtil.getPixel(7);
//			allRelative.setLayoutParams(allRelativeParams);
//			allRelative.addView(btnAll);
//			layout.addView(allRelative);
//		}
    }

    //2015. 11. 5 차트 우상단 분석툴바 삭제/전체삭제 버튼 >>
    Button btnDeleteAnal = null, btnDeleteAllAnal = null;
    private void hideExReButtonView() {
        try {
            ExpandReduceChartHandler.removeMessages(TAP);

            btnExpand.startAnimation(hideAnimation);
            btnReduce.startAnimation(hideAnimation);
            if (null != btnIndicatorList) {
                btnIndicatorList.startAnimation(hideAnimation);
            }
            if (null != btnAll) {
                btnAll.startAnimation(hideAnimation);
            }
            if (null != btnDeleteAllAnal) {
                btnDeleteAllAnal.startAnimation(hideAnimation);
            }
            if (null != btnDeleteAnal) {
                btnDeleteAnal.startAnimation(hideAnimation);
            }

            btnExpand.setVisibility(View.GONE);
            btnReduce.setVisibility(View.GONE);
            if (null != btnIndicatorList) {
                btnIndicatorList.setVisibility(View.GONE);
            }
            if (null != btnAll) {
                btnAll.setVisibility(View.GONE);
            }
            if (null != btnDeleteAllAnal) {
                btnDeleteAllAnal.setVisibility(View.GONE);
            }
            if (null != btnDeleteAnal) {
                btnDeleteAnal.setVisibility(View.GONE);
            }
        }
        catch(Exception e) {
        }
    }
    /**
     * 분석툴바 삭제/전체삭제 버튼 추가
     * */
    private void initDeleteAnalButtons()
    {
//    	int layoutResId;
//
//    	YScale yscale = basic_block.getYScale()[0];
//		Rect yScaleBounds = new Rect(yscale.getBounds().left, yscale.getBounds().top-(int)COMUtil.getPixel(10),
//				yscale.getBounds().right, yscale.getBounds().bottom+(int)COMUtil.getPixel(10) );
//
//		int btnX = yScaleBounds.left - (int)COMUtil.getPixel(55);
//		int btnY = (int)COMUtil.getPixel(20);
//
//		//전체삭제
//		RelativeLayout.LayoutParams deleteAllAnalRelativeParams = null;
//    	if(btnDeleteAllAnal == null)
//    	{
//    		btnDeleteAllAnal = new Button(context);
////    		btnReduce.setText("-");
////    		btnReduce.setBackgroundColor(Color.GRAY);
//
//    		String btnImg = "btn_chart_deleteanalall";
////    		if(COMUtil.skinType == COMUtil.SKIN_BLACK)
////    			btnImg = "btn_chart_zoomout_change_black";
//
//    		layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
//    		btnDeleteAllAnal.setBackgroundResource(layoutResId);
//
//    		btnDeleteAllAnal.setAnimation(set);
//
//    		btnDeleteAllAnal.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					ExpandReduceChartHandler.removeMessages(TAP);
//                	ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);
//
//                	setChartToolBar(COMUtil.TOOLBAR_CONFIG_ALL_ERASE);
//				}
//			});
//    		RelativeLayout deleteAnalAllRelative = new RelativeLayout(context);
//    		deleteAnalAllRelative.setTag("deleteAnalAllRelative");
//    		deleteAllAnalRelativeParams = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(36), (int)COMUtil.getPixel(36));
//    		deleteAllAnalRelativeParams.leftMargin = btnX;
//    		deleteAllAnalRelativeParams.topMargin = btnY;
//    		deleteAnalAllRelative.setLayoutParams(deleteAllAnalRelativeParams);
//    		deleteAnalAllRelative.addView(btnDeleteAllAnal);
//    		layout.addView(deleteAnalAllRelative);
//    	}
//
//    	//순차삭제
//    	if(btnDeleteAnal == null && null != deleteAllAnalRelativeParams)
//    	{
//    		btnDeleteAnal = new Button(context);
////    		btnExpand.setText("+");
////    		btnExpand.setBackgroundColor(Color.GRAY);
//    		String btnImg = "btn_chart_deleteanal";
////    		if(COMUtil.skinType == COMUtil.SKIN_BLACK)
////    			btnImg = "btn_chart_zoomin_change_black";
//
//    		layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
//    		btnDeleteAnal.setBackgroundResource(layoutResId);
//
//    		btnDeleteAnal.setAnimation(set);
//
//    		btnDeleteAnal.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					ExpandReduceChartHandler.removeMessages(TAP);
//                	ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);
//
//					setChartToolBar(COMUtil.TOOLBAR_CONFIG_ERASE);
//				}
//			});
//    		RelativeLayout deleteAnalRelative = new RelativeLayout(context);
//    		deleteAnalRelative.setTag("deleteAnalRelative");
//    		RelativeLayout.LayoutParams deleteAnalRelativeParams = new RelativeLayout.LayoutParams((int)COMUtil.getPixel(40), (int)COMUtil.getPixel(40));
//    		deleteAnalRelativeParams.leftMargin = btnX;
//    		deleteAnalRelativeParams.topMargin = deleteAllAnalRelativeParams.topMargin + deleteAllAnalRelativeParams.height + (int)COMUtil.getPixel(5);
//    		deleteAnalRelative.setLayoutParams(deleteAnalRelativeParams);
//    		deleteAnalRelative.addView(btnDeleteAnal);
//    		layout.addView(deleteAnalRelative);
//    	}
    }
    //2015. 11. 5 차트 우상단 분석툴바 삭제/전체삭제 버튼 <<

    //    private static final int SHOW_PRESS = 1;
    private static final int LONG_PRESS = 2;
    private static final int TAP = 3;
    // ViewConfiguration 에서 Long Press 를 판단하는 time 을 가져 온다.
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    //2012. 7. 26  터치한 x 좌표 를 알아내서  드래그했을때 보조지표 토글되지 않게 하기위해 사용
    int nDownX;

    public boolean onTouchEvent(final MotionEvent evt) {
        try {
            if (COMUtil._mainFrame == null)
                return false;
            //2019. 09. 11 by hyh - sendRotateTR_storage() 도중 터치 불가능하도록 처리 >>
            if (COMUtil.m_nRotateIndex >= 0) {
                return true;
            }
            //2019. 09. 11 by hyh - sendRotateTR_storage() 도중 터치 불가능하도록 처리 <<

            //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 >>
            if (_cvm.bIsUpdownChart) {
                return true;
            }
            //2019. 03. 12 by hyh - 투자자 업종 가로 바차트 추가 <<

            try {
                if (COMUtil._mainFrame.mainBase.baseP instanceof Base11) {
                    Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
                    if (_cvm.bIsLineFillChart && _cvm.bIsOneQStockChart && base11.nMarketType == 1)
                        return true;
                }
            } catch (Exception e) {

            }

            //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<
            if (m_velocityTracker == null) {
                m_velocityTracker = VelocityTracker.obtain();
            }

            m_velocityTracker.addMovement(evt);
            //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<

            //if(_cvm.bIsLineFillChart)
            //if(_cvm.nTouchEventType == 0 || (basic_block.getYScalePos()==0 && _cvm.nTouchEventType == 0) || _cvm.bIsLineFillChart && _cvm.nTouchEventType == 0) || (_cvm.bIsLineChart && !_cvm.bIsNewsChart) || _cvm.bIsMiniBongChart || _cvm.bIsLine2Chart)
            if (_cvm.nTouchEventType == 0 || (basic_block.getYScalePos() == 0 && _cvm.nTouchEventType == 0) || (_cvm.bIsLineFillChart && !_cvm.bIsOneQStockChart) || (_cvm.bIsLineChart && !_cvm.bIsNewsChart) || _cvm.bIsMiniBongChart || _cvm.m_nChartType == ChartViewModel.CHART_HORIZONTAL_ROUNDED_BAR)
                return super.onTouchEvent(evt);

            //2013. 2. 14 차트 확대/축소
            if (gd != null)
                gd.onTouchEvent(evt);    //제스처는 더블탭만 인식, 사용.

            //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 Start
            if (mGestureDetector != null)
                mGestureDetector.onTouchEvent(evt);
            //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 End

            YScale yscale = null;
            RectF yScaleBounds = new RectF(0, 0, 0, 0);
            if (basic_block.getYScale() != null) {
                yscale = basic_block.getYScale()[0];
                yScaleBounds = new RectF(yscale.getBounds().left, yscale.getBounds().top - (int) COMUtil.getPixel(10),
                        yscale.getBounds().right, yscale.getBounds().bottom + (int) COMUtil.getPixel(10));
            }
            switch (evt.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    //2023.02.09 by SJW - 블록병합 기능 제거 >>
//				//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동
//				this.hideChartBlockPopup();
                    //2023.02.09 by SJW - 블록병합 기능 제거 <<

                    tm_interpolation = System.currentTimeMillis();

                    if (eventBadgeViewP != null) {
                        layout.removeView(eventBadgeViewP);
                        eventBadgeViewP = null;
                        return false;
                    }

                    if (tradeViewP != null) {
                        layout.removeView(tradeViewP);
                        tradeViewP = null;
                        return false;
                    }

                    m_bSendNextData = false;
                    //2013.04.04 by LYH >> 분석툴바 선택시 삭제/전체삭제 subbar 사라짐 개선.
                    if (analToolSubbar != null && analToolSubbar.getVisibility() == View.VISIBLE)
                        this.showAnalToolSubbar(false, analParams);
                    //2013.04.04 by LYH <<
                    touchesMoved = false;
                    m_nAccelCount = 0;
                    preLocationX = -1;
//                m_bShowToolTip = false; //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경
                    if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ)
                        _cvm.setToolbarState(9999);
                    //process
                    //  		queueEvent(new Runnable(){
                    //              public void run() {
                    //화면에 열려있는 화면들(설정화면등)을 닫는다.
                    COMUtil.closeAllView();

//                	//2013. 2. 13  차트 확대/축소
////                	if (chart_bounds.width() > (int)COMUtil.getPixel(200) && chart_bounds.height() > (int)COMUtil.getPixel(202)) {
//                	if(rotate_blocks.size() == 0)
//                	{
//	                	if(_cvm.chartType != COMUtil.DIVIDE_CHART)
//	                	{
//	                		initExpandReduceButtons();
//	                		initDeleteAnalButtons();
//
//	                    	if(btnExpand != null)
//	                    	{
//	                    		btnExpand.startAnimation(showAnimation);
//	                    		btnExpand.setVisibility(View.VISIBLE);
//	                    	}
//	                    	if(btnReduce != null)
//	                    	{
//	                    		btnReduce.startAnimation(showAnimation);
//	                    		btnReduce.setVisibility(View.VISIBLE);
//	                    	}
//	                    	if(btnDeleteAllAnal != null)
//	                    	{
//	                    		btnDeleteAllAnal.startAnimation(showAnimation);
//	                    		btnDeleteAllAnal.setVisibility(View.VISIBLE);
//	                    	}
//	                    	if(btnDeleteAnal != null)
//	                    	{
//	                    		btnDeleteAnal.startAnimation(showAnimation);
//	                    		btnDeleteAnal.setVisibility(View.VISIBLE);
//	                    	}
//	                    	if(btnIndicatorList != null)
//	                    	{
//	                    		btnIndicatorList.startAnimation(showAnimation);
//	                    		btnIndicatorList.setVisibility(View.VISIBLE);
//	                    	}
//
                    ExpandReduceChartHandler.removeMessages(TAP);
                    ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);
//
//	                	}
//    				}

                    p_count = evt.getPointerCount();
                    nTouchesEnd = 0;

                    pressPoint = new PointF((int) evt.getX(), (int) evt.getY());
                    prePanPoint = new PointF((int) evt.getX(), (int) evt.getY()); //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경

                    //2016.11.02 by hyh - 스크롤 수치조회창 개발. 수치조회창 보이게 설정됐다면 스크롤이 가능하도록 NeoChart 터치이벤트 무시 >>
                    if (_cvm.isCrosslineMode && viewP.getBounds() != null && viewP.getBounds().contains(pressPoint.x, pressPoint.y)) {
                        return false;
                    }
                    //2016.11.02 by hyh - 스크롤 수치조회창 개발. 수치조회창 보이게 설정됐다면 스크롤이 가능하도록 NeoChart 터치이벤트 무시 <<

                    //2015. 3. 25 y축 롱터치 추세선의 롱터치시간 조정>>
                    mHandler.removeMessages(LONG_PRESS);
                    if (yScaleBounds.contains(pressPoint.x, pressPoint.y) && _cvm.nTouchEventType != 2) {
                        mHandler.sendEmptyMessageAtTime(LONG_PRESS, evt.getDownTime() + 1500);
                    } else {
                        if (_cvm.getToolbarState() == 9999)
                            mHandler.sendEmptyMessageAtTime(LONG_PRESS, evt.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
                    }
                    //2015. 3. 25 y축 롱터치 추세선의 롱터치시간 조정<<

                    //2012. 7. 26  터치한 x 좌표 를 알아내서  드래그했을때 보조지표 토글되지 않게 하기위해 사용
                    nDownX = (int) evt.getX();
                    if (!m_bSelected) {
                        selectBaseChart();
                        selectChart();
                    }

                    //2012. 7. 16  십자선데이터패널  터치했을때 위치이동하도록 수정
                    curPoint = pressPoint;

                    //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치 드래그 이벤트 시 크기 바로 반영 >>
                    if (_cdm.getCount() > 1 && !_cvm.isStandGraph()) {
                        Block dnBlock = getDnChartBlock(curPoint);
                        if (dnBlock != null && !_cvm.isChange()) {    //블럭 resize 상태일때 처리
                            touchesMoved = true;
                            currY = curPoint.y;
                            resize_block = dnBlock;
                            funcName = "drawAnalTool_MouseDrag";
                            _cvm.setResizeState(true);
                        }
                    }
                    //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치 드래그 이벤트 시 크기 바로 반영 <<

                    if (p_count == 2) {//two touch
                        Point ptA = new Point((int) evt.getX(0), (int) evt.getY(0));
                        Point ptB = new Point((int) evt.getX(1), (int) evt.getY(1));
                        distance = distanceTwoPoint(ptA, ptB);

                        preDistance = 0;
                    }

                    try {
                        if (_cdm.getCount() < 1 || _cvm.isStandGraph()) {
                            return true;
                        }
                    } catch (Exception e) {
                        return true;
                    }

                    PointF p = pressPoint;
//			        int start = getXToDate(pressPoint.x);

                    Block cb = getUpChartBlock(p);
//			        Block dnBlock = getDnChartBlock(p);
                    //2023.05.25 by SJW - 가격 블럭 이동 막음 >>
//				if (cb != null) {
                    if (cb != null && cb != basic_block) {
                        //2023.05.25 by SJW - 가격 블럭 이동 막음 <<
                        //블럭이동 상태일때 처리
                        mHandler.removeMessages(LONG_PRESS);
                        touchesMoved = true;
                        funcName = "drawAnalTool_MouseDrag";
                        _cvm.setChangeState(true);
                        changable_block[0] = cb;

                        //2021. 06. 05 by hyh - 지표 잔상 효과 개발 >>
                        croppedBitmap = cropBitmap(getCapturedBitmap(), new RectF(changeRect));
                        //2021. 06. 05 by hyh - 지표 잔상 효과 개발 <<

                        repaintAll();
                    }
//			        else if(dnBlock!=null && !_cvm.isChange()){	//블럭 resize 상태일때 처리
//			        	touchesMoved = true;
//			        	currY = evt.getY();
//			        	resize_block = dnBlock;
//			        	funcName = "drawAnalTool_MouseDrag";
//			        	_cvm.setResizeState(true);
//			        	repaintAll();
//
//			        	mHandler.removeMessages(LONG_PRESS);
//			        }
                    //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 >>
//				else if(_cvm.chartType!=COMUtil.COMPARE_CHART && yScaleBounds.contains(prePanPoint.x, prePanPoint.y) && !_cvm.isStandGraph()) {
//			//else if(_cvm.chartType!=COMUtil.COMPARE_CHART && yScaleBounds.contains(pressPoint.x, pressPoint.y) && !_cvm.isStandGraph()) {
//				//2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 <<
//					analDragState = false;
//					curPoint = pressPoint;
//					touchesMoved=true;
//					funcName = "drawAnalTool_MouseDrag";
//					//2017.07.25 by pjm >> Y축 움직여 확대/축소
//					m_nOrgViewNum = _cvm.getViewNum();
//					m_nOrgIndex = _cvm.getIndex();
//					//2017.07.25 by pjm << Y축 움직여 확대/축소
//
//					//2019. 06. 14 by hyh - 시세알람차트 수치조회창 떠 있을 때 Y축 Drag로 알람값 설정 가능하도록 변경 >>
//					if (!_cvm.bIsAlarmChart) {
////						_cvm.setToolbarState(COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ); //2021.06.22 by lyk - kakaopay - yscale 가격선 사용안함
//
//						//2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 >>
//						if (viewP != null) {
//							hideViewPanel();
//						}
//						//2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 <<
//					}
//					//2019. 06. 14 by hyh - 시세알람차트 수치조회창 떠 있을 때 Y축 Drag로 알람값 설정 가능하도록 변경 <<
//
//					if(select_at!=null)reSetAnalTool(false);
//					repaintAll();
//				}
                    else if (!_cvm.isResize() && _cvm.getToolbarState() != 9999) {//툴바를 사용하고 툴바가 설정되어 있는 경우
                        //drawToolbar = true;
                        if (analTools == null) analTools = new Vector<AnalTool>();
                        if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_ANDREW || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_FIBOTARGET || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_TARGETNT) {//앤드류스피치포크인 경우 (3포인트)
                            if (MousePress_count == 0) {
                                AnalTool at = getAnalTool(_cvm.getToolbarState(), getChartBlock(p));
                                at.addPoint(p);
                                analTools.addElement(at);
                            } else {
                                AnalTool at = (AnalTool) analTools.lastElement();
                                at.addPoint(p);
                            }
                        } else if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_CROSS ||
                                _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_VERT || //수직선.
                                _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_HORZ) { //수평선.

                            analDragState = false;
                            curPoint = pressPoint;
                            touchesMoved = true;
                            funcName = "drawAnalTool_MouseDrag";
                            repaintAll();

                        } else {
                            AnalTool at = getAnalTool(_cvm.getToolbarState(), getChartBlock(p));
                            if (at != null) {
                                at.addPoint(p);
                                analTools.addElement(at);
                            }
                            analDragState = false;
                        }
                    } else {
                        if (select_at != null) reSetAnalTool(false);
                        if (selectedAnalTool(p)) {
                            if (select_dt != null) reSetDrawTool(false);
                            analtool_select = true;
                            repaintAll();
                        } else {
                            analtool_select = false;
                            if (select_dt != null) reSetDrawTool(false);
                            if (selectedDrawTool(p)) {
                                if (p_count == 2) {

                                }
                                drawtool_select = true;
                                funcName = "";
                                repaintAll();
                            } else {
                                drawtool_select = false;
                            }
                        }
                    }
//			        return;
                    //               }});
                    //2013. 2. 13  차트 확대/축소
//                	if (chart_bounds.width() > (int)COMUtil.getPixel(200) && chart_bounds.height() > (int)COMUtil.getPixel(202)) {
                    return true;
                case MotionEvent.ACTION_MOVE:
                    touchesMoved = true;
                    //process
                    //   		 queueEvent(new Runnable(){
                    //                public void run() {

                    //2015. 2. 12 yscale 롱터치시 수평선 그리기>>  : 차트 yscale 롱터치시 추세선 그려지지않음 수정
//    				if( 	!(_cvm.getToolbarState()==COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ && Math.abs(curPoint.y - (int)evt.getY()) <= COMUtil.getPixel(3)) &&
//    						(_cvm.getToolbarState()!=9999 ||Math.abs(curPoint.x - (int)evt.getX()) > COMUtil.getPixel(3)) )
//    				{
//    					mHandler.removeMessages(LONG_PRESS);
//    		        	//2013.04.04 by LYH >> 분석툴바 선택시 삭제/전체삭제 subbar 사라짐 개선.
//    					if(analToolSubbar != null && analToolSubbar.getVisibility()==View.VISIBLE)
//    						this.showAnalToolSubbar(false, analParams);
//    		        	//2013.04.04 by LYH <<
//    				}
                    //2015. 2. 12 yscale 롱터치시 수평선 그리기<<

                    //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치 드래그 이벤트 시 크기 바로 반영 >>
                    if (_cvm.isResize() && !_cvm.isChange()) {
                        //수직 제스쳐일 경우 처리
//					if ((Math.abs(curPoint.x - (int) evt.getX()) < COMUtil.getPixel(3)) && (Math.abs(curPoint.y - (int) evt.getY()) > COMUtil.getPixel(2))) {
                        //2024.07.17 by SJW - resizeBlock 기능 수정(casting, 로직) >>
                        //resizeBlock(new PointF((int) evt.getX(), (int) evt.getY()));

                        resizeBlock(new PointF(evt.getX(), evt.getY()));
                        //2024.07.17 by SJW - resizeBlock 기능 수정(casting, 로직) <<
                        dragged = false;
                        repaintAll();

                        return false;
//					} else {
//						dragged = true;
//					}
                    }
                    //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치 드래그 이벤트 시 크기 바로 반영 <<

                    //rotate block 스와이프 제스쳐 이벤트 발생시 스크롤 안되도록 처리
                    if ((!m_bShowToolTip || !_cvm.isCrosslineMode) && (rotate_blocks != null && _cvm.isStandGraph() == false)) {
                        int nBlockCnt = blocks.size();
                        if (nBlockCnt > m_nMaxIndicatorCount) {
                            cb = (Block) blocks.get(nBlockCnt - 1);
                            //2012. 7. 26 보조지표가 여러개 겹쳐 있을 때 드래그로는 토글되지 않게 수정
                            int nDistance = Math.abs((int) evt.getX() - nDownX);
                            if (cb.getBounds().contains(pressPoint.x, pressPoint.y)) {
                                if (rotate_blocks.size() > 0) {
                                    return false;
                                }
                            }
                        }
                    }
                    //rotate block 스와이프 제스쳐 이벤트 발생시 스크롤 안되도록 처리  end

                    if (Math.abs(curPoint.x - (int) evt.getX()) > COMUtil.getPixel(3) || Math.abs(curPoint.y - (int) evt.getY()) > COMUtil.getPixel(5))
                        mHandler.removeMessages(LONG_PRESS);

//    				//2013. 2. 13 차트 확대/축소
                    ExpandReduceChartHandler.removeMessages(TAP);

                    if (btnExpand != null) {
                        btnExpand.setVisibility(View.GONE);
                    }
                    if (btnReduce != null) {
                        btnReduce.setVisibility(View.GONE);
                    }
                    if (btnIndicatorList != null) {
                        btnIndicatorList.setVisibility(View.GONE);
                    }
                    if (btnAll != null) {
                        btnAll.setVisibility(View.GONE);
                    }
                    if (btnDeleteAllAnal != null) {
                        btnDeleteAllAnal.setVisibility(View.GONE);
                    }
                    if (btnDeleteAnal != null) {
                        btnDeleteAnal.setVisibility(View.GONE);
                    }

                    funcName = "drawAnalTool_MouseMove";
                    //확대/축소 거리계산.
                    p_count = evt.getPointerCount();
                    if (p_count == 2 && !_cvm.isStandGraph()) {
                        if (_cvm.bIsLineFillChart && _cvm.bIsOneQStockChart)
                            return false;
                        //2019. 05. 20 by hyh - 수치조회창 확대 축소 시 보이지 않게 하기 >>
                        if (!_cvm.bIsAlarmChart && _cvm.getAssetType() != ChartViewModel.ASSET_LINE_MOUNTAIN) {
                            this.hideViewPanel();
                        }
                        //2019. 05. 20 by hyh - 수치조회창 확대 축소 시 보이지 않게 하기 <<

                        Point ptA = new Point((int) evt.getX(0), (int) evt.getY(0));
                        Point ptB = new Point((int) evt.getX(1), (int) evt.getY(1));
//			        	int ptA_index = getXToDate(ptA.x);
//			        	int ptB_index = getXToDate(ptB.x);

                        newDistance = Math.abs(ptB.x - ptA.x);

                        int limitCnt = 5;

                        //2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw >>
                        int nSpeed = 10;
                        int gab = 0;
                        if (COMUtil.isDetailScroll()) {
                            nSpeed = 50;
                            gab = _cvm.getViewNum() / nSpeed;//확대/축소시 갭 보정.
                            if (gab == 0) {
                                gab = 2;
                            }
                        } else {
                            //2023.06.30 by SJW - 차트 확대/축소 시 가속도 변경 >>
//						nSpeed = 20;
                            nSpeed = 10;
                            //2023.06.30 by SJW - 차트 확대/축소 시 가속도 변경 <<
                            gab = _cvm.getViewNum() / nSpeed;//확대/축소시 갭 보정.
                            if (gab == 0) {
                                gab = 1;
                            }
                        }
                        //2020.03.25 확대/축소 정밀도 설정, 실봉/허봉 설정 추가 - hjw <<

//			        	int viewIndex = _cdm.getCount()-_cvm.getViewNum();

                        //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
//					int gab = _cvm.getViewNum() / 30;
                        //int gab = _cvm.getViewNum() / 10; //확대,축소시 갭 보정.
                        //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<

                        if (gab == 0) {
                            gab = 2;
                        }


                        if (distance == 0) {
                            distance = newDistance;
                        } else if (newDistance > preDistance) { //확대

                            if (_cvm.getViewNum() < limitCnt) {
                                return true;
                            }
                            int nIndex = _cvm.getIndex() + gab;
                            if (COMUtil.isZoomCenterView()) //2017.05.23 by PJM 사진줌방식
                                nIndex = _cvm.getIndex() + gab / 2;
                            int nViewNum = _cvm.getViewNum() - gab;
                            if (nViewNum < limitCnt)
                                return true;

                            if (nIndex < 0) {
                                nIndex = 0;
                            }
                            _cvm.setIndex(nIndex);
                            _cvm.setViewNum(nViewNum);
                            _cvm.setOnePage(0);//전체보기 기능을 해제한다.
                            repaintAll();
                            COMUtil.getMainBase().setCountText("");
                            isChangeViewCnt = true;
                        } else if (newDistance < preDistance) { //축소.

                            //_cvm.setIndex(_cvm.getIndex()-gab);
                            //_cvm.setViewNum(_cvm.getViewNum()+gab);
                            int nIndex = _cvm.getIndex() - gab;
                            if (COMUtil.isZoomCenterView()) //2017.05.23 by PJM 사진줌방식
                                nIndex = _cvm.getIndex() - gab / 2;
                            int nViewNum = _cvm.getViewNum() + gab;

                            if (nViewNum > _cdm.getCount()) {
                                //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
                                if (_cvm.futureMargin == 0 || (nViewNum > _cdm.getCount() + _cvm.futureMargin -1)) {
                                    //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
                                    nIndex = 0;
                                    nViewNum = _cdm.getCount();
                                    //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
                                    if(_cvm.futureMargin > 0)
                                        nViewNum = _cdm.getCount() + _cvm.futureMargin - 1;
                                    //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<

                                    //이곳에 데이터를 추가로 호출한다.
                                    if (COMUtil.getSendTrType().equals("requestAddData")) {
                                        //2012. 11. 22 전체차트 모드일때 제대로 나오게 하기위해 바로 true 로 리턴하는 것 수정 : C32
                                        //			        				return true;
                                    } else {
                                        if (_cvm.chartType != COMUtil.COMPARE_CHART) {
                                            nSendMarketIndex = -1;  //2016.12.19 - 다음 조회시 시장지표 인덱스 초기화
                                            Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
                                            base11.sendTR("requestAddData");
                                        }

                                    }
                                }
                            }
                            if (nIndex < 0) {
                                nIndex = 0;
                            }

                            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
//						if ((nIndex + nViewNum) > _cdm.getCount()) {
//							nIndex = _cdm.getCount() - nViewNum;
//						}
                            if(_cvm.futureMargin == 0)
                            {
                                if ((nIndex + nViewNum) > _cdm.getCount()) {
                                    nIndex = _cdm.getCount() - nViewNum;
                                }
                            }
                            else {
                                if ((nIndex + nViewNum) > _cdm.getCount() + _cvm.futureMargin - 1) {
                                    nIndex = _cdm.getCount() - nViewNum + _cvm.futureMargin - 1;
                                }
                            }
                            //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<

//				        	if(_cvm.getViewNum() > _cdm.getCount()) {
//				        		nIndex = 0;
//				        		nViewNum = _cdm.getCount();
//				        	}


                            //2019. 07. 30 by hyh - 최대 개수 이상 확대 불가 처리 >>
//						if (nViewNum > _cvm.MAX_VIEW_NUM) {
//							nViewNum = _cvm.MAX_VIEW_NUM;
//							nIndex = _cvm.getIndex();
//						}
                            //2019. 07. 30 by hyh - 최대 개수 이상 확대 불가 처리 <<

                            _cvm.setIndex(nIndex);
                            _cvm.setViewNum(nViewNum);
                            _cvm.setOnePage(0);
                            repaintAll();
                            COMUtil.getMainBase().setCountText("");
//				        	System.out.println(""+nIndex +":"+ nViewNum +":"+ _cdm.getCount());
                            isChangeViewCnt = true;
                        }

                        preDistance = newDistance;
                        m_nAccelCount = 0;

                        //  더블탭은 확대로 초기화
                        exReType = CHART_EXPASION;

                        //  확대축소 반복 초기화
                        exReRepeat = false;

                        //2019. 08. 02 by hyh - 확대/축소 시 분석툴바 선택 해제
                        this.reSetAnalTool(false);

                        return true;
                    }

                    location = new PointF((int) evt.getX(), (int) evt.getY());
                    preLocation = pressPoint;

                    PointF preCurPoint = curPoint;
                    preLocationX = pressPoint.x;
                    curPoint = location;

                    //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 Start
                    if (_cvm.getAssetType() == ChartViewModel.ASSET_LINE_MOUNTAIN)
                        _cvm.curIndex = getXToDate(curPoint.x);
                    //2020.06.08 by LYH >> 마운틴 차트 툴팁 개선 End
                    if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ) {
                        if (!yScaleBounds.contains(location.x, location.y)) {
                            analDragState = false;
                            funcName = "";
                            _cvm.setToolbarState(9999);
                        }
                        //2017.07.25 by pjm >> Y축 움직여 확대/축소
                        else {
                            if (m_nOrgViewNum > 0) {
                                setExpandReduce();
                            }
                        }
                        //2017.07.25 by pjm << Y축 움직여 확대/축소
                    } else {
                        if (_cvm.chartType != COMUtil.COMPARE_CHART && yScaleBounds.contains(pressPoint.x, pressPoint.y) && yScaleBounds.contains(location.x, location.y) && !_cvm.isStandGraph()
                                && !_cvm.bIsAlarmChart) {
                            analDragState = false;
                            touchesMoved = true;
                            funcName = "drawAnalTool_MouseDrag";
//						_cvm.setToolbarState(COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ); //2021.06.22 by lyk - kakaopay - yscale 가격선 사용안함

                            if (select_at != null) reSetAnalTool(false);
                            repaintAll();
                        }
                    }

                    if (_cvm.chartType == COMUtil.COMPARE_CHART || _cvm.bIsNewsChart) {
                        if (xscale.getBounds().contains(location.x, location.y)) {
                            _cdm.baseLineIndex = getXToDate(location.x);
                            if (blocks.size() > 0) {
                                for (int i = 0; i < blocks.size(); i++) {
                                    Block b = (Block) blocks.get(i);
                                    b.makeGraphData();
                                }
                            }
                            repaintAll();
                            return true;
                        }
                    }


//				if((m_bShowToolTip || _cvm.isCrosslineMode)&&_cvm.getToolbarState()==9999) {
//					int idx = this.getXToDate(preCurPoint.x);
//					int idx1 = this.getXToDate(curPoint.x);
//
//					if(idx != idx1)
//					{
//						int nIndex = -1;
//						int nMoveCount = 5;
//						if(_cvm.bIsNewsChart)
//							nMoveCount = 25;
//						if(idx1<=_cvm.getIndex())
//						{
//							nIndex = _cvm.getIndex()-nMoveCount+1;
//							if(nIndex<0)
//								nIndex=0;
//						}
//						else if(idx1<_cdm.getCount() && idx1>=_cvm.getIndex()+_cvm.getViewNum() && _cvm.futureMargin == 0)	//2017.01.03 by LYH >> 일목균형 수치조회 시 이동하는 문제 해결
//						{
//							nIndex = _cvm.getIndex()+nMoveCount-1;
//							if(nIndex+_cvm.getViewNum()>=_cdm.getCount())
//							{
//								nIndex = _cdm.getCount()-_cvm.getViewNum();
//							}
//						}
//						if(nIndex>=0)
//							_cvm.setIndex(nIndex);
//					}
//
//					repaintAll();
//
//					//2019. 04. 17 by hyh - 시세알람차트 개발 >>
//					if (_cvm.bIsAlarmChart) {
//						sendAlarmPriceToScreen(curPoint);
//					}
//					//2019. 04. 17 by hyh - 시세알람차트 개발 <<
//
//					return true;
//				}

                    int btype = Block.STAND_BLOCK;
                    Block stand = getChartBlockByType(btype);
                    if (stand != null) {
                        //2015.06.23 by lyk - 차트 유형 추가
                        if (stand.getTitle().equals("삼선전환도") || stand.getTitle().equals("스윙") || stand.getTitle().equals("Kagi") || stand.getTitle().equals("PnF") || stand.getTitle().equals("렌코") || stand.getTitle().equals("역시계곡선")) {
                            //2015.06.23 by lyk - 차트 유형 추가 end
                            return true;
                        }
                    }

//		    		if(rotate_blocks != null && rotate_blocks.size()>0 && _cvm.isStandGraph()==false) {
//		    			cb = (Block)blocks.get(blocks.size()-1);
//		    			if(cb.getBounds().contains(location.x, location.y)) {
//		    				return false;
//		    			}
//		    		}

//			        cb = getDnChartBlock(location);

                    //dragged event
                    if (!containsX(pressPoint.x)) {
//			        	System.out.println("containsX."+pressPoint.x);
//			        	System.out.println("containsX end.");
                        return true;
                    }

//			        if(cb!=null && !_cvm.isChange()){
//			            //경계선에 마우스 올라갔을때 리사이즈 설정
//			            _cvm.setResizeState(true);
//			            resize_block = cb;
//			            return true;
//			        }else

                    if (_cvm.getToolbarState() != 9999) {
                        if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_CROSS || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_VERT || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_HORZ || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_FIBOTIME) {
                            //drawAnalTool_MouseMove(gl));
                        }
                    } else {
                    }

                    extent = false;

                    //그리기 >>
                    if (analTools != null && analTools.size() > 0) {
                        AnalTool at = (AnalTool) analTools.lastElement();
                        if (at != null) {

                            if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_PENCIL) {
                                at.addPoint(curPoint);
                                funcName = "";
                                repaintAll();
                                return true;
                            }
                        }
                    }
                    //그리기 <<

                    //2023.02.09 by SJW - 블록병합 기능 제거 >>
                    //2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 >>
//                if (selectedMoveDrawTool != null) {
//                    dragged=true;
//                    funcName = "drawAnalTool_MouseDrag";
//                    repaintAll();
//                    return true;
//                }
                    //2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 <<
                    //2023.02.09 by SJW - 블록병합 기능 제거

                    if (analtool_select) {
                        if (select_at == null) {
                            analtool_select = false;
                            return true;
                        }
                        //2021.04.28 by lyk - kakaopay - 종목 캘린더 뱃지 버튼 이벤트 막음 >>
                        if (!select_at.getClass().equals(ABadgeTool.class)) {
                            select_at.changePoint(pressPoint, curPoint);
                            select_at.isSelect = true;
                        }
                        //2021.04.28 by lyk - kakaopay - 종목 캘린더 뱃지 버튼 이벤트 막음 <<
                        repaintAll();
                        return true;
                    } else if (_cdm.getCount() < 2 || _cvm.isChange()) {
                        dragged = true;
                        funcName = "drawAnalTool_MouseDrag";
                        repaintAll();
                        return true;
                    } else if (_cvm.isResize() && !_cvm.isChange()) {
                        dragged = true;
                        currY = evt.getY();
                        funcName = "drawAnalTool_MouseDrag";
                        repaintAll();
                    } else if (!_cvm.isChange() && _cvm.getToolbarState() != 9999) {//툴바를 사용하고 툴바가 설정되어 있는 경우
                        if (analDragState) {
                            funcName = "";
                        } else {
                            funcName = "drawAnalTool_MouseDrag";
                        }

                        repaintAll();
                    } else {
                        //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
                        float fDistance = 0;
                        if (evt.getHistorySize() > 0) {
                            fDistance = evt.getX() - evt.getHistoricalX(0);
                        }

//					if (Math.abs(fDistance) > 0) {
//						m_velocityTracker.computeCurrentVelocity(1);
//						int nVelocity = (int) (m_velocityTracker.getXVelocity() * 500); //안드로이드velocity * 500 == 아이폰velocity
//						m_nAccelCount = (int) ((fDistance / chart_bounds.width()) * 1000 + nVelocity * 0.05);
//
//						//System.out.println("acceleration : onTouchEvent() velocity = " + nVelocity);
//					}

                        m_nDistance = fDistance;

                        if ((m_nAccelCount < 0 && fDistance > 0) || (m_nAccelCount > 0 && fDistance < 0)) {
                            m_nAccelCount = 0;
                        }

                        //2014.10.13 by lyk - 드래그 가속도 감도 낮춤 (10 -> 5)
                        if (Math.abs(COMUtil.getPixel((int) fDistance)) > chart_bounds.width() / 5)
                        //2014.10.13 by lyk - 드래그 가속도 감도 낮춤 (10 -> 5) end
                        {
                            int nAccelCount = (int) (fDistance / (chart_bounds.width() / 20));
                            //2014.11.25 by lyk - AccelCount 수정 end
                            if (fDistance > 0) {

                                m_nAccelCount += Math.abs(nAccelCount);
                            } else {
                                int viewIndex = _cdm.getCount() - _cvm.getViewNum();
                                if (viewIndex == _cvm.getIndex()) {
                                    m_nAccelCount = 0;
                                } else
                                    m_nAccelCount -= Math.abs(nAccelCount);
                            }
                        }

                        //2021.07.07 by lyk - 확대/축소 이벤트시 스크롤 안되도록 수정 >>
                        if (m_nDistance != 0 && distance == 0 && preDistance == 0) {
                            setMouseDrag_sbMove(location, true);
                        }
                        //2021.07.07 by lyk - 확대/축소 이벤트시 스크롤 안되도록 수정 <<

                        //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<
                    }

                    return true;
                case MotionEvent.ACTION_UP:

                    //  편의기능 추가 : 수치팝업창 활성화인 경우 - 2021.05.11 by lyk - kakaopay - 탭 이벤트 처리로 변경 (롱터치 기능 삭제) >>
//				if((_cvm.chartType == COMUtil.COMPARE_CHART || (COMUtil.isSetScreenViewPanel() && eventBadgeViewP == null && tradeViewP == null))) {
//					m_bShowToolTip = !m_bShowToolTip;
//					if(!m_bShowToolTip)
//					{
//						hideViewPanel();
//						resetTitleBounds();
//						repaintAll();
//					} else {
//						if (!_cvm.isCrosslineMode && viewP != null) {
//							if (_cvm.chartType == COMUtil.COMPARE_CHART) {
//								//2012. 7. 16   롱클릭시 십자선데이터패널 표시
//								_cvm.isCrosslineMode = true;
//
//								viewP.showCloseButton(true);    // 닫기 버튼 보이게
//								viewP.setVisibility(View.VISIBLE);
//							} else {
//								// 롱 클릭시 십자선 패널 보이게
//								Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
//								base11.showCrossLineLongClick(true, this);
//							}
//						}
//					}
//				}
                    //  편의기능 추가 : 지표팝업창 활성화인 경우 - 2021.05.11 by lyk - kakaopay - 탭 이벤트 처리로 변경 (롱터치 기능 삭제) >>

                    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
                    if (m_velocityTracker != null) {
                        m_velocityTracker.recycle();
                        m_velocityTracker = null;
                    }
                    //2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<

                    boolean isRotateBlock = false;

                    if (isChangeViewCnt && !COMUtil.getSendTrType().equals("requestAddData")) {
                        if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_VIEWCNT_CONFIG, null);
                    }
                    isChangeViewCnt = false;
                    analDragState = true;
                    touchesMoved = false;
                    distance = 0;
                    preDistance = 0;
                    // 2011.11.23 <<
                    //preocess
                    //   		queueEvent(new Runnable(){
                    //               public void run() {
                    mHandler.removeMessages(LONG_PRESS);

                    //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 >>
                    //2012. 7. 16   롱클릭후  손을 떼면 십자선데이터툴팁 사라지게 수정
//				if(!_cvm.isCrosslineMode && viewP != null && viewP.getVisibility() == View.VISIBLE)
//				{
//					hideViewPanel();
//				}
//				if(m_bShowToolTip)
//				{
//					m_bShowToolTip = false;
//					resetTitleBounds();
//					repaintAll();
//				}
                    //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 <<

                    //2017.07.25 by pjm >> Y축 움직여 확대/축소
                    m_nOrgViewNum = -1;
                    m_nOrgIndex = -1;
                    //2017.07.25 by pjm << Y축 움직여 확대/축소

                    preLocationX = -1;

//				//2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 >>
//				if (Math.abs(m_nAccelCount) > 0) {
//					m_flickingPoint = new PointF((int) evt.getX(), (int) evt.getY());
//
//					m_flickingThread = new Thread(flickingRunable);
//					m_flickingThread.setName("ChartPro m_flickingThread");
//					m_flickingThread.start();
//				}
//				//2019. 03. 12 by hyh - 차트영역 확대/축소/가속도 수정 <<
                    if (m_nAccelCount != 0) {
                        float velocity = m_nDistance / (System.currentTimeMillis() - tm_interpolation);

                        m_flickingPoint = new PointF((int) evt.getX(), (int) evt.getY());

                    }

                    p_count = evt.getPointerCount();
                    p = new PointF((int) evt.getX(), (int) evt.getY());
                    if (_cdm == null) {
                        return true;
                    }
//				if(_cdm.getCount()<2||_cvm.isStandGraph()) {
//					return true;
//				}
                    nTouchesEnd++;

                    //2023.02.09 by SJW - 블록병합 기능 제거 >>
//				//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 >>
//				if (selectedMoveDrawTool != null) {
//
//					targetedMoveBlock = getChartBlock(p);
//
//					this.showGraphMovePopup(p);
//
//					repaintAll();
//
//					if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
//					nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
//
//					return true;
//				}
//				//2019. 01. 12 by hyh - 블록병합 처리. 지표 이동 <<
                    //2023.02.09 by SJW - 블록병합 기능 제거 <<

                    if (_cvm.isChange()) {
                        //2023.05.25 by SJW - 가격 블럭 이동 막음 >>
//					cb = getChartBlock(p);
                        cb = getChangeChartBlock(p);
                        //2023.05.25 by SJW - 가격 블럭 이동 막음 <<
                        _cvm.setChangeState(false);
                        selectedChangeBlock = null;
                        changeRect = null;
                        changable_block[1] = cb;

                        //2021. 06. 05 by hyh - 지표 잔상 효과 개발 >>
                        croppedBitmap = null;
                        //2021. 06. 05 by hyh - 지표 잔상 효과 개발 <<

                        //2019. 01. 12 by hyh - 블록병합 처리 >>
//					if (_cvm.chartType != COMUtil.COMPARE_CHART ) {
//						this.showChartBlockPopup(curPoint);
//					}
                        changeBlock(); //2021.05.14 by lyk - KakaoPay - 블록병합 팝업 기능 사용안함
                        //2019. 01. 12 by hyh - 블록병합 처리 <<

                        repaintAll();

                        if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
                        nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
//	    	            System.out.println("changeBLock");

                        return true;
                    } else if (_cvm.isResize() && !_cvm.isChange()) {
                        resizeBlock(p);
                        dragged = false;
                        repaintAll();

                        _cvm.setResizeState(false);

                        return true;
                    } else if (_cvm.getToolbarState() != 9999) {//툴바를 사용하고 툴바가 설정되어 있는 경우
                        if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ) {
                            analDragState = false;
                            funcName = "";
                            _cvm.setToolbarState(9999);
                            repaintAll();

                            //2015. 1. 21 목표가 전달 requestinfo 이벤트 및 값넘기기>>
                            if (yscale != null) {
                                String price = ChartUtil.getFormatedData("" + yscale.getChartPrice(curPoint.y), _cdm.getPriceFormat(), _cdm);
                                Hashtable<String, Object> dic = new Hashtable<String, Object>();
                                dic.put("yscaleDragPrice", price);
                                if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_SEND_YSCALEDRAGPRICE, dic);
                                //2015. 1. 21 목표가 전달 requestinfo 이벤트 및 값넘기기<<
                            }
                            return true;
                        }
                        if (analTools == null) {

                            return true;
                        }
                        if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_ANDREW || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_FIBOTARGET || _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_TARGETNT) {//앤드류스 피치포크
                            if (MousePress_count == 2) {
                                MousePress_count = 0;
                                //drawToolbar = false;
                                funcName = "";
//	    	                    _cvm.setToolbarState(9999);
                                //2012. 10. 23 분석툴을 한번만 그리고 해당 분석툴버튼 disable.  : T62
                                ((Base11) COMUtil._mainFrame.mainBase.baseP).initAnalTool();
//	    	                    _cvm.setToolbarState(9999);

                                repaintAll();
                            } else {
                                MousePress_count++;
                                funcName = "drawAnalTool_MouseDrag";
                                repaintAll();
                            }
                        } else {
                            AnalTool at;
                            if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_CROSS ||
                                    _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_VERT || //수직선.
                                    _cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_HORZ) { //수평선.

                                at = getAnalTool(_cvm.getToolbarState(), getChartBlock(p));
                                //2020.10.28 by JJH >> 추세선(수평선)을 차트화면 바깥에 작도하였을때 nullException 오류 처리 start
                                if (at.getAtColor() != null) {
                                    at.addPoint(p);
//								analDragState = false;
                                    //	    	            		if(at==null) return;
                                    analTools.add(at);
                                }
                                analDragState = false;
                                //2020.10.28 by JJH >> 추세선(수평선)을 차트화면 바깥에 작도하였을때 nullException 오류 처리 end

                            }
                            if (analTools.size() < 1) return true;
                            at = (AnalTool) analTools.lastElement();
                            if (at == null) return true;

                            //문자
                            if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_TEXT) {
                                float xPos = p.x;
                                float yPos = p.y;
                                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();

                                RelativeLayout.LayoutParams analParams1 = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(80), (int) COMUtil.getPixel(30));
                                analParams1.leftMargin = (int) (xPos + param.leftMargin);
                                analParams1.topMargin = (int) (yPos + param.topMargin);
                                this.showAnalToolTextField(true, analParams1);

                            }
                            at.addPoint(p);
                            //drawToolbar = false;
                            funcName = "";

                            //2012. 10. 23 분석툴을 한번만 그리고 해당 분석툴버튼 disable. 앤드류스피치포크는 3개 점을 찍으므로 앤드류스피치포크쪽 조건문에서 따로 불러줌.  : T62
                            if (_cvm.getToolbarState() != COMUtil.TOOLBAR_CONFIG_ANDREW) {
                                ((Base11) COMUtil._mainFrame.mainBase.baseP).initAnalTool();
//	    	    				_cvm.setToolbarState(9999);
                            }

                            repaintAll();
                        }

                        saveAnalToolBySymbol();    //2013. 11. 21 추세선 종목별 저장 : 분석툴 그리고 손가락을 뗏을 때

                        if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
                        nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.

                    } else if (extent) {
//	    	            int start = getXToDate(pressPoint.x);
//	    	            int end = getXToDate((int)evt.getX());
//	    	            if(start>end){
//	    	                int tmp = start;
//	    	                start = end;
//	    	                end = tmp;
//	    	            }
//	    	            start=(start<0)?0:start;
//	    	            int num=(end>=_cdm.getCount())?_cdm.getCount()-start:end-start+1;
//	    	            if(num<5){
//	    	                repaintAll();
//	    	            }else{
//	    	                _cvm.setIndex(start);
//	    	                _cvm.setViewNum(num);
//	    	                setScrollBar(ONLY_SHOWCHANGE);
//	    	                repaintAll();
//	    	                extent = false;
////	    	                return true;
//	    	                return;
//	    	            }
                    } else if (analtool_select) {
                        if (select_at == null) return true;

                        //2021.04.28 by lyk - kakaopay - 종목 캘린더 뱃지 버튼 이벤트 막음 >>
                        if (!select_at.getClass().equals(ABadgeTool.class)) {
                            select_at.resetPoint();
                        }
                        //2021.04.28 by lyk - kakaopay - 종목 캘린더 뱃지 버튼 이벤트 막음 <<

                        saveAnalToolBySymbol();    //2013. 11. 21 추세선 종목별 저장 : 그려진 분석툴 위치 및 크기 수정
                    } else {
//	    	        	String deviceType = COMUtil.deviceMode;
                        //if(!deviceType.equals(COMUtil.HONEYCOMB)) {

                        //rotate_block 스와이프 이벤트 변경건으로 주석처리함
//					if(rotate_blocks!=null && _cvm.isStandGraph()==false) {
//						int nBlockCnt = blocks.size();
//						if(nBlockCnt>m_nMaxIndicatorCount) {함
//							cb = (Block)blocks.get(nBlockCnt-1);
//							//2012. 7. 26 보조지표가 여러개 겹쳐 있을 때 드래그로는 토글되지 않게 수정
//							int nDistance = Math.abs((int)evt.getX() - nDownX);
//							if(cb.getBounds().contains(p.x, p.y) && nDistance < COMUtil.getPixel(4)) {
//								if(rotate_blocks.size()>0) {
//									isRotateBlock = true;
//									//2012.07.09 by LYH>> rolling 기능
//									m_nRollIndex++;
//									if(m_nRollIndex > rotate_blocks.size())
//										m_nRollIndex = 0;
//									int nIndex = 0;
//
//									String title = title_list.get(m_nRollIndex);
//									for(int i=0; i<rotate_blocks.size(); i++)
//									{
//										if(((Block)rotate_blocks.get(i)).getTitle().equals(title))
//										{
//											nIndex = i;
//											break;
//										}
//									}
//									Block block = (Block)rotate_blocks.get(nIndex);
//									rotate_blocks.remove(block);
//									block.setHideDelButton(false);
//									block.makeGraphData();
//									addBlock(block);
////	    	        						setPivotState(pivotState);
//									resetTitleBounds();
//									repaintAll();
//
//								}
//							}
//						}
//					}
                        //}
                    }
//	    	        return;
//                }});

                    if (!isRotateBlock && !_cvm.bInvestorChart && _cvm.getToolbarState() == 9999) {
                        //분할 갯수
                        int mCnt = COMUtil._mainFrame.getDivideNum();
                        Base11 base11 = (Base11) base;
//			        	if(mCnt==1 && _cvm.chartType != COMUtil.COMPARE_CHART && !COMUtil.isTrendType && !COMUtil.isAfterType && !base11.isJisuType)
                        int nDistance = Math.abs((int) evt.getX() - nDownX);
//					if(nDistance < COMUtil.getPixel(10) && !isChangeViewCnt && mCnt==1 && _cvm.chartType != COMUtil.COMPARE_CHART && !COMUtil.isTrendType && !COMUtil.isAfterType && !base11.isJisuType && !_cvm.isStandGraph())	//2014. 3. 27 독립차트일때는 +- 버튼과 좌우화살표 안보이게 하기
                        if (nDistance < COMUtil.getPixel(10) && !isChangeViewCnt && _cvm.chartType != COMUtil.COMPARE_CHART && !COMUtil.isTrendType && !COMUtil.isAfterType && !base11.isJisuType /*&& !_cvm.isStandGraph()*/)    //2014. 3. 27 독립차트일때는 +- 버튼과 좌우화살표 안보이게 하기
                        {
//						initExpandReduceButtons();
                            initDeleteAnalButtons();
                            repaintAll();

                            if (btnExpand != null) {
                                btnExpand.startAnimation(showAnimation);
                                btnExpand.setVisibility(View.VISIBLE);
                            }
                            if (btnReduce != null) {
                                btnReduce.startAnimation(showAnimation);
                                btnReduce.setVisibility(View.VISIBLE);
                            }
                            if (btnIndicatorList != null) {
                                btnIndicatorList.startAnimation(showAnimation);
                                btnIndicatorList.setVisibility(View.VISIBLE);
                            }
                            if (btnAll != null) {
                                btnAll.startAnimation(showAnimation);
                                btnAll.setVisibility(View.VISIBLE);
                            }
                            if (btnDeleteAllAnal != null) {
                                btnDeleteAllAnal.startAnimation(showAnimation);
                                btnDeleteAllAnal.setVisibility(View.VISIBLE);
                            }
                            if (btnDeleteAnal != null) {
                                btnDeleteAnal.startAnimation(showAnimation);
                                btnDeleteAnal.setVisibility(View.VISIBLE);
                            }

                            ExpandReduceChartHandler.removeMessages(TAP);
                            ExpandReduceChartHandler.sendEmptyMessageDelayed(TAP, 3000);

                            //2019. 04. 17 by hyh - 시세알람차트 개발 >>
                            if (_cvm.bIsAlarmChart) {
                                if (m_bShowToolTip) {
                                    this.hideViewPanel();
                                } else {
                                    m_bShowToolTip = true;
                                    curPoint = pressPoint;
                                    repaintAll();
                                    sendAlarmPriceToScreen(curPoint);
                                }
                            }
                            //2019. 04. 17 by hyh - 시세알람차트 개발 <<
                        }

                    }

                    //2016. 1. 14 뉴스차트 핸들러>>
                    if (_cvm.bIsNewsChart) {
                        _cvm.curIndex = getXToDate(curPoint.x);
                        if (nNewsEventIndex != _cvm.curIndex) {
                            nNewsEventIndex = _cvm.curIndex;
                            String[] datas = _cdm.getDatas(_cvm.curIndex);

                            Hashtable<String, Object> dic = new Hashtable<String, Object>();
                            dic.put("newsdate", datas[0]);

                            if (userProtocol != null)
                                userProtocol.requestInfo(COMUtil._TAG_NEWS_DATE, dic);
                        }
                    }
                    //2016. 1. 14 뉴스차트 핸들러<<
                    saveLastState(); //2024.07.17 by SJW - 손 뗐을때 봉 개수 저장하게끔 변경
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    mHandler.removeMessages(LONG_PRESS);
                    if (m_bShowToolTip) {
                        m_bShowToolTip = false; //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경
                        hideViewPanel();
                        resetTitleBounds();
                        repaintAll();
                    }
                    if (_cvm.getToolbarState() == COMUtil.TOOLBAR_CONFIG_YSCALE_HORZ) {
                        analDragState = false;
                        funcName = "";
                        _cvm.setToolbarState(9999);
                        repaintAll();
                    }
                    //2017.07.25 by pjm >> Y축 움직여 확대/축소
                    m_nOrgViewNum = -1;
                    m_nOrgIndex = -1;
                    //2017.07.25 by pjm << Y축 움직여 확대/축소
                    break;
            }
        } catch(Exception e){
        }
        return super.onTouchEvent(evt);
    }

    private String convertDecimalFormat(String changeText) {
        DecimalFormat dec = new DecimalFormat("###,###");
        return dec.format(Double.parseDouble(changeText));
    }

    private boolean setMouseDrag_sbMove(PointF location, boolean bPaint) {
        if(_cvm.nTouchEventType == 2)
            return false;

        //2019. 09. 23 by hyh - 수치조회창 켜있을 때에는 Swipe 이벤트 동작하지 않도록 수정 >>
        if (m_bShowToolTip) {
            return false;
        }
        //2019. 09. 23 by hyh - 수치조회창 켜있을 때에는 Swipe 이벤트 동작하지 않도록 수정 <<

        if (_cvm.bIsLineFillChart && _cvm.bIsOneQStockChart)
            return false;

        //rotate block 스와이프 제스쳐 이벤트 발생시 스크롤 안되도록 처리
        if((!m_bShowToolTip || !_cvm.isCrosslineMode) && (rotate_blocks!=null && _cvm.isStandGraph()==false)) {
//					Block cb = null;
//					PointF p = pressPoint;
            int nBlockCnt = blocks.size();
            if (nBlockCnt > m_nMaxIndicatorCount) {
                Block cb = (Block) blocks.get(nBlockCnt - 1);
                //2012. 7. 26 보조지표가 여러개 겹쳐 있을 때 드래그로는 토글되지 않게 수정
                int nDistance = Math.abs((int) location.x - nDownX);
                if (cb.getBounds().contains(pressPoint.x, pressPoint.y)) {
                    if (rotate_blocks.size() > 0) {
                        return false;
                    }
                }
            }
        }
        //rotate block 스와이프 제스쳐 이벤트 발생시 스크롤 안되도록 처리  end

        //synchronized(this) {
        //2020.07.06 by LYH >> 캔들볼륨 >>
//		int start = this.getXToDate(pressPoint.x);
//		int end = this.getXToDate(location.x);
//		if(pressPoint.x<0 || location.x<0)
////			return false;
        int start = xscale.getXToDate_ByViewNum(pressPoint.x);	//CandleVolume
        int end = xscale.getXToDate_ByViewNum(location.x);
        //2020.07.06 by LYH >> 캔들볼륨 <<
        int viewIndex = _cdm.getCount()-_cvm.getViewNum();
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 >>
        if(_cvm.futureMargin > 0)
            viewIndex = _cdm.getCount()-_cvm.getViewNum() + _cvm.futureMargin - 1;
        //2024.01.03 by LYH - 일목균형표 줌인, 줌아웃, 스크롤 개선 <<
        int index = 0;

        int preIndex = _cvm.getIndex();

        if(end > start) {//오른쪽으로 드래그.
            pressPoint.x = location.x;
            if(preIndex <= 0 && bPaint) {
                //데이터를 추가로 호출한다.
                //if(m_bSendNextData) {
                if(COMUtil.getSendTrType().equals("requestAddData")) {	//2020.06.12 by LYH >> 차트 축소시 데이터 과거데이터로 보이는 현상 개선
                    return false;
                } else {
                    if(_cvm.chartType!=COMUtil.COMPARE_CHART) {
                        nSendMarketIndex = -1;  //2016.12.19 - 다음 조회시 시장지표 인덱스 초기화
                        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                        base11.sendTR("requestAddData");
                        m_bSendNextData = true;
                    }
                }
            } else {
                index = preIndex-(end-start);
                if(index<0) {
                    _cvm.setIndex(0);
                    if(bPaint)
                        repaintAll();
                    return false;
                }
            }
            _cvm.setIndex(index);
        } else if(end < start) { //왼쪽으로 드래그.
            pressPoint.x = location.x;
            if(preIndex >= viewIndex) {
                index=viewIndex;
                return false;
            } else {
                index = preIndex+(start-end);
                if(index > viewIndex) {
                    _cvm.setIndex(viewIndex);
                    if(bPaint)
                        repaintAll();
                    return false;
                }
            }
            _cvm.setIndex(index);
        }
        if(bPaint)
            this.repaintAll();
        return true;
        //}
    }

    //    private void removeAction(){
//        if(select_graph.getGraphTitle().equals("Multi") || select_graph.getGraphTitle().equals("주가이동평균")){
//            select_block.removeDrawTool(select_graph.getGraphTitle(), select_dt.getTitle());
//            select_dt.setSelected(false);
//            reSetDrawTool(true);
//            //drawtool_select = false;
//            //select_dt=null;
//            setTitleBounds();
//            repaintAll();
//        }else if(select_block.isBasicBlock()&&select_block.getGraphs().size()>2){
//            if(!(select_graph instanceof JapanBongGraph)) {
//                select_block.removeGraph(select_graph.getGraphTitle());
//                ChartEvent chartEvt = new ChartEvent(this,this,ChartEvent.CHART_REMOVE,select_graph.getGraphTitle());
//                processChartChangeEvent(chartEvt);
//                reSetDrawTool(true);
//                repaintAll();
//            }
//        }else{
//            this.removeBlock(select_graph.getGraphTitle());
//            ChartEvent chartEvt = new ChartEvent(this,this,ChartEvent.CHART_REMOVE,select_graph.getGraphTitle());
//            processChartChangeEvent(chartEvt);
//            repaintAll();
//        }
//    }
    public void refresh(){
        ChartEvent ce = new ChartEvent(this,this,ChartEvent.CHART_INIT,"");
        processChartChangeEvent(ce);
    }
    public Block getChartBlock(PointF p){
        Block cb = null;
        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getBounds().contains(p.x,p.y))
            {
                return cb;
            }
        }
        return null;
    }
    //2023.05.25 by SJW - 가격 블럭 이동 막음 >>
    public Block getChangeChartBlock(PointF p){
        if(blocks.size()<3)
            return null;

        Block cb = null;
        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getBounds().contains(p.x,p.y))
            {
                if(cb.getBlockType()==Block.BASIC_BLOCK && i==0)
                {
                    return blocks.get(i+1);
                }
                return cb;
            }
        }
        return null;
    }
    //2023.05.25 by SJW - 가격 블럭 이동 막음 <<
    public Block getDnChartBlock(PointF p){
        Block cb = null;
        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getDnBounds().contains(p.x, p.y) && cb.getIndex()!=blocks.size()-1)
                return cb;
        }
        return null;
    }
    //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 >>
    public Block getSpecialChartBlock(String sName){
        Block cb = null;

        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getTitle().equals(sName))
            {
                return cb;
            }
        }
        return null;
    }
    //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 <<

    public Block getUpChartBlock(PointF p){
        Block cb = null;
        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getUpBounds().contains(p.x,p.y)==true)return cb;
        }
        return null;
    }

    public boolean isChangeBlock = false;
    /** block이동 처리 함수 by lyk 2012.09.04 **/
    public void changeBlock(){
        if(blocks.size()<2)return;//블럭끼리 교환은 블럭사이즈가 2개 이상일때만
        Block[] cBlocks = getChangableBlocks();
        if(cBlocks ==null)return;
//        int miny=1000;

        isChangeBlock = true;
        reSetUI(true);
        isChangeBlock = false;

//        if(_cvm.isVerticalMode()){
//            reSetUI(true);
//        }else{
//            for(int i=0;i<cBlocks.length;i++)
//                if(cBlocks[i].getY()<miny)miny=cBlocks[i].getY();
//
//            cBlocks[0].setLocation(cBlocks[0].getBounds().left,miny);
//            for(int i=1;i<cBlocks.length;i++)
//                cBlocks[i].setLocation( cBlocks[i].getBounds().left, cBlocks[i-1].getY()+ cBlocks[i-1].getHeight());
//        }
    }
    //독립차트나, 가격차트 블럭을 리턴
    public Block getChartBlockByType(int type){
        Block cb = null;
        for(int i=0;i<blocks.size();i++){
            cb = (Block)blocks.elementAt(i);
            if(cb.getBlockType()==type)return cb;
            else cb=null;
        }
        return cb;
    }
    public Block[] getChangableBlocks(){
        int min, max=0;
        try {
            //rotate blocks은 블럭을 이동하지 않게 함.
            if(rotate_blocks!=null && rotate_blocks.size()>0) {
                if(blocks!=null && blocks.size()>0) {
                    if(blocks.lastElement().equals((Block)changable_block[0]) ||
                            blocks.lastElement().equals((Block)changable_block[1])) {
                        return null;
                    }
                }
            }
            if(changable_block[0].getIndex()<changable_block[1].getIndex()){ //changable_block[0] 아래에 insert
                //change
                min = changable_block[0].getIndex();
                max = changable_block[1].getIndex();

                int index = (max+1);

                blocks.remove(min);
                blocks.insertElementAt(changable_block[0], max);
                Block cb0 = changable_block[0];
                Block cb1 = changable_block[1];

                if(index>=blocks.size()) {
//	            	index = blocks.size()-1; //마지막 index로 지정.
//	            	Block bottomCb = blocks.get(index);
                    cb0.setBounds(cb0.getX(), cb1.getBottom()-cb0.getHeight(), cb0.getRight(), cb1.getBottom()-cb0.getHeight()+cb0.getHeight(), true);

                    for(int i=(max-1); i>=0; i--) {
                        Block cb2 = blocks.get(i);
                        Block afterBlock = blocks.get(i+1);
                        cb2.setBounds(cb2.getX(), afterBlock.getY()-cb2.getHeight(), cb2.getRight(), afterBlock.getY()-cb2.getHeight()+cb2.getHeight(), true);
                    }
                } else {
                    Block bottomCb = blocks.get(index);
                    cb0.setBounds(cb0.getX(), bottomCb.getY()-cb0.getHeight(), cb0.getRight(), bottomCb.getY()-cb0.getHeight()+cb0.getHeight(), true);

                    for(int i=max; i>=0; i--) {
                        Block cb2 = blocks.get(i);
                        Block afterBlock = blocks.get(i+1);
                        cb2.setBounds(cb2.getX(), afterBlock.getY()-cb2.getHeight(), cb2.getRight(), afterBlock.getY()-cb2.getHeight()+cb2.getHeight(), true);
                    }
                }
//	            cb1.setBounds(cb1.getX(), cb0.getY(), cb1.getRight(), cb0.getY()+cb1.getHeight(),true);



//	            Block cb = blocks.get(index);
//	            cb.setBounds(changable_block[0].getX(),changable_block[0].getY(),changable_block[0].getRight(), changable_block[0].getBottom(),true);

            }else{															//changable_block[0] 위에 insert
                min = changable_block[1].getIndex();
                max = changable_block[0].getIndex();

                int index = (min-1);
                if(index<0) {
                    index = 0;
                }

                blocks.remove(max);
                blocks.insertElementAt(changable_block[0], min);
                Block cb0 = changable_block[0];
                Block cb1 = changable_block[1];
                cb0.setBounds(cb0.getX(), cb1.getY(), cb0.getRight(), cb1.getY()+cb0.getHeight(), true);

                for(int i=cb1.getIndex()+1; i<blocks.size(); i++) {
                    Block cb2 = blocks.get(i);
                    Block preBlock = blocks.get(i-1);
                    cb2.setBounds(cb2.getX(), preBlock.getBottom(), cb2.getRight(), preBlock.getBottom()+cb2.getHeight(), true);
                }

//	            Block cb = blocks.get(index);
//	            cb.setBounds(changable_block[0].getX(),changable_block[0].getY(),changable_block[0].getRight(), changable_block[0].getBottom(),true);

            }
        } catch (Exception e) {
            return null;
        }

        for(int i=0;i<blocks.size();i++){
            Block cb = (Block)blocks.elementAt(i);
            cb.setIndex(i);
            //2013. 9. 26 회전블록(rotate_block) 에 일반블록을 이동시키면  블록이 이상하게 변함. 초기화에 좀비블록이 남음. 회전블록 동그라미 표시 이상함 >>
            if(i==blocks.size()-1 && max==m_nMaxIndicatorCount && rotate_blocks.size()==0)
            {
                title_list.removeAllElements();
                title_list.add(cb.getTitle());
                cb.setTitleNumber(0, 0);
                m_nRollIndex = 0;
            }
            //2013. 9. 26 회전블록(rotate_block) 에 일반블록을 이동시키면  블록이 이상하게 변함. 초기화에 좀비블록이 남음. 회전블록 동그라미 표시 이상함 >>
        }

        Block[] changableBlocks = new Block[max-min+1];
//        int count = 0;
//        Block[] tmp = new Block[blocks.size()];
//        for(int i=0;i<blocks.size();i++){
//            Block cb = (Block)blocks.elementAt(i);
//            if(cb.getIndex()>min-1&&cb.getIndex()<max+1){
//                tmp[cb.getIndex()] =cb;
//                count++;
//            }
//        }
//        System.arraycopy(tmp,min, changableBlocks, 0,changableBlocks.length);
//        return changableBlocks;

        return changableBlocks;
    }
    //===========================================
    // 블럭 리사이즈 이벤트 추가 by lyk 2012.09.04
    //===========================================
    public void resizeBlock(PointF p){
//        int changedIndex;

        Block changedBlock = null;
        Block lastBlock = null; //2024.08.12 by SJW - 차트 리사이즈 시 마지막 블록 사이즈 못잡던 현상 수정
        try {
            changedBlock = (Block)blocks.elementAt(resize_block.getIndex()+1);
            lastBlock = (Block)blocks.elementAt(blocks.size()-1); //2024.08.12 by SJW - 차트 리사이즈 시 마지막 블록 사이즈 못잡던 현상 수정
        } catch(Exception e) {
            return;
        }

//        changedBlock = (Block)blocks.elementAt(resize_block.getIndex()+1);
        if(Math.round(p.y)>resize_block.getY() && Math.round(p.y) < changedBlock.getBottom()){
            //2023.10.27 by SJW - 블럭 축소 시 고가와 저가 겹치는 현상 개선 >>
            //2021.09.10 by lyk - kakaopay - 리사이즈 블럭 최소 사이즈 설정 >>
//			if((Math.round(p.y)-resize_block.getY()) < COMUtil.getPixel(40)) {
            if((Math.round(p.y)-resize_block.getY()) < COMUtil.getPixel(60)) {
                //2023.10.27 by SJW - 블럭 축소 시 고가와 저가 겹치는 현상 개선 <<
                return;
            }
            //2023.10.27 by SJW - 블럭 축소 시 고가와 저가 겹치는 현상 개선 >>
//			if((changedBlock.getY() + changedBlock.getHeight() - Math.round(p.y)) < COMUtil.getPixel(40)) {
            if((changedBlock.getY() + changedBlock.getHeight() - Math.round(p.y)) < COMUtil.getPixel(60)) {
                //2023.10.27 by SJW - 블럭 축소 시 고가와 저가 겹치는 현상 개선 <<
                return;
            }
            //2021.09.10 by lyk - kakaopay - 리사이즈 블럭 최소 사이즈 설정 <<

            //2024.07.17 by SJW - resizeBlock 기능 수정(casting, 로직) >>
//            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
//			resize_block.setHBounds(resize_block.getY(),Math.round(p.y)-resize_block.getY());
//			changedBlock.setHBounds(Math.round(p.y), changedBlock.getY() + changedBlock.getHeight() - Math.round(p.y));
//            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
            float middlePoint = p.y; //Math.round(p.y);
            float resizeBlockY = resize_block.getY();
            float resizeBlockHeight = middlePoint - resizeBlockY;
            float changeBlockHeight = changedBlock.getBottom() - middlePoint;

            resize_block.setHBounds(resizeBlockY, resizeBlockHeight);
            changedBlock.setHBounds(middlePoint, changeBlockHeight);
            //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end
            //2024.07.17 by SJW - resizeBlock 기능 수정(casting, 로직) <<

            //2024.08.12 by SJW - 차트 리사이즈 시 마지막 블록 사이즈 못잡던 현상 수정 >>
            if(lastBlock.getHeight() < COMUtil.getPixel(60)) {
                return;
            }
            //2024.08.12 by SJW - 차트 리사이즈 시 마지막 블록 사이즈 못잡던 현상 수정 <<

            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
            //2013. 9. 23 블록이동버튼 x축 위치 조정>>
//            int x = chart_bounds.right+param.leftMargin;
            float x = chart_bounds.right+param.leftMargin+(int)COMUtil.getPixel(2); //블럭삭제 버튼을 우측 가격차트 영역으로 이동.
//			float x = chart_bounds.right+ param.leftMargin + basic_block.W_YSCALE - (int)COMUtil.getPixel(4); //블럭삭제 버튼 전체영역으로 .
            //2013. 9. 23 블록이동버튼 x축 위치 조정>>
            float y=0;

            //rotate block 처리
            if(changedBlock.getIndex()>=(blocks.size()-1)) {
                if(rotate_blocks != null) {
                    for(int i=0; i<rotate_blocks.size(); i++){
                        Block bl = (Block)rotate_blocks.get(i);
                        bl.setHBounds(p.y,changedBlock.getY()+changedBlock.getHeight()-p.y);

                        y = bl.getOutBounds().top+param.topMargin;
                        bl.setChangeBlockBtn(x, y);						//블럭이동버튼 위치 재조정.
                        bl.setBlockBtn(x, y, bl.getTitle());			//블럭삭제버튼 위치 재조정.
                    }
                }
            }

            y = changedBlock.getOutBounds().top+param.topMargin;
            changedBlock.setChangeBlockBtn(x, y);						//블럭이동버튼 위치 재조정.
            changedBlock.setBlockBtn(x, y, changedBlock.getTitle());	//블럭삭제버튼 위치 재조정.

            setBlockRateInfo();    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장

            this.reSetUI(true);
        }
    }
    //===============================
    //전체 데이터 한페이지 보기
    //===============================
    public void setOnePage(){
        _cvm.setOnePage(1); //전체데이터보기 클릭시 고정
        reset();
    }

    public void showViewPanel() {
        m_bShowToolTip = true;

        //2017.09.25 by LYH >> 자산 차트 적용
//		if(viewP != null)
//		{
//			this.layout.removeView(shadowlayout);
//		}
        //2017.09.25 by LYH >> 자산 차트 적용 end

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewP == null) {
            if(_cvm.getAssetType()>0 )
            //2018.10.18 by LYH >> 버블차트형 추천종목 차트 End
            {
                viewP = new ExViewPanel_LeftRightArrow(context, this.layout);
//        		viewP = new ViewPanel(context, this.layout);
            }
            //2017.09.25 by LYH >> 자산 차트 적용 end
            else {
                viewP = new ScrollViewPanel(context, this.layout);
            }

//			Rect bounds = new Rect(0, 0, 0, 0);
//			viewP.setBounds(bounds);
//			viewP.setContentBounds(bounds);
            //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 >>
//			viewP.viewP.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(!_cvm.isCrosslineMode) {
//						viewP.setVisibility(View.GONE);
//						resetTitleBounds();
//						_cvm.isCrosslineMode = false;shadowlayout
//						m_bShowToolTip = false;
//						repaintAll();
//					}
//				}
//			});
            //2019. 04. 16 by hyh - 수치조회창 터치시 사라지게 변경 <<

//			if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//				shadowlayout = (RelativeLayout) inflater.inflate(R.layout.shadowpanel_dark, null);
//			} else {
//				shadowlayout = (RelativeLayout) inflater.inflate(R.layout.shadowpanel, null);
//			}
            blurViewlayout = (RelativeLayout) inflater.inflate(R.layout.blurviewpanel, null);
//			FrameLayout.LayoutParams lparam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//			blurView.setLayoutParams(lparam);
            blurView = blurViewlayout.findViewById(R.id.live_blur);
//			GradientDrawable shape = new GradientDrawable();
//			shape.setCornerRadius(12);
//			shape.setColor(CoSys.CHART_BACK_MAINCOLOR);
//			shape.setAlpha(150);
//			blurViewlayout.setBackground(shape);

//			if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//				blurView.setBackgroundResource(R.drawable.infoview_shadow_dark);
//			} else {
//				blurView.setBackgroundResource(R.drawable.infoview_shadow);
//			}

            blurViewlayout.setVisibility(View.GONE);
//			blurView.addView(viewP);


//			this.layout.addView(blurView);
//			blurView.setPadding((int)viewP.bounds.left, (int)viewP.bounds.top, 0, 0);
//			this.layout.addView(shadowlayout);
//			viewP.setBlurView(blurView);

            this.layout.addView(blurViewlayout);
            this.layout.addView(viewP);

        }
        else {
//			this.layout.removeView(shadowlayout);
//			if(_cvm.getSkinType() == COMUtil.SKIN_BLACK) {
//				shadowlayout = (RelativeLayout) inflater.inflate(R.layout.shadowpanel_dark, null);
//			} else {
//				shadowlayout = (RelativeLayout) inflater.inflate(R.layout.shadowpanel, null);
//			}
//			this.layout.addView(shadowlayout);

            blurView.setVisibility(VISIBLE);
            viewP.setVisibility(VISIBLE);
        }

        //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 >>
//		int btnColor = Color.argb(242, 111, 119, 130);
//
//		if (m_btnViewPanelAlarm == null) {
//			m_btnViewPanelAlarm = new Button(context);
//			m_btnViewPanelAlarm.setText("알람");
//			m_btnViewPanelAlarm.setTextSize(12);
//			m_btnViewPanelAlarm.setTextColor(Color.WHITE);
//			m_btnViewPanelAlarm.setPadding(0, 0, 0, 0);
//			m_btnViewPanelAlarm.setBackgroundColor(btnColor);
//			m_btnViewPanelAlarm.setVisibility(GONE);
//			m_btnViewPanelAlarm.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					//2019. 07. 26 by hyh - 알람화면 이동 전에 동일한 종목 및 데이터가 전달 되도록 차트 재선택 >>
//					if (!m_bSelected) {
//						selectBaseChart();
//						selectChart();
//					}
//					//2019. 07. 26 by hyh - 알람화면 이동 전에 동일한 종목 및 데이터가 전달 되도록 차트 재선택 <<
//
//					double[] fCloses = _cdm.getSubPacketData("종가");
//					String[] strDates = _cdm.getStringData("자료일자");
//
//					if (fCloses != null) {
//						double dPrice = fCloses[_cvm.curIndex];
//						String strPrice = String.valueOf(dPrice);
//						strPrice = ChartUtil.getFormatedData(strPrice, _cdm.getPriceFormat(), _cdm);
//
//						String strDate = strDates[_cvm.curIndex];
//
//						Hashtable<String, Object> dic = new Hashtable<String, Object>();
//						dic.put("price", strPrice);
//						dic.put("date", strDate);
//
//						userProtocol.requestInfo(COMUtil._TAG_OPEN_ALARM_SCREEN, dic);
//					}
//
//					hideViewPanel();
//				}
//			});
//
//			this.layout.addView(m_btnViewPanelAlarm);
//		}
//
//		if (m_btnViewPanelClose == null) {
//			m_btnViewPanelClose = new Button(context);
//			m_btnViewPanelClose.setText("닫기");
//			m_btnViewPanelClose.setTextSize(12);
//			m_btnViewPanelClose.setTextColor(Color.WHITE);
//			m_btnViewPanelClose.setPadding(0, 0, 0, 0);
//			m_btnViewPanelClose.setBackgroundColor(btnColor);
//			m_btnViewPanelClose.setVisibility(GONE);
//			m_btnViewPanelClose.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					hideViewPanel();
//				}
//			});
//
//			this.layout.addView(m_btnViewPanelClose);
//		}
        //2019. 04. 16 by hyh - 수치조회창 알람/닫기 버튼 추가 <<
    }

    //삭제/전체삭제 창 호출
    LinearLayout analToolSubbar = null;
    public void showAnalToolSubbar(boolean visible, final RelativeLayout.LayoutParams params) {
        if (tradeViewP != null) {
            tradeViewP.setVisibility(View.GONE);
        }
        if (select_at != null && select_at.getTitle().equals("Trade")) {
            if (visible) {
                //2021.06.15 by lky - kakaopay - 시세인포뷰가 떠있지 않을 경우에 추가 >>
                if(!m_bShowToolTip) {
                    showTradePanel();
                }
                //2021.06.15 by lky - kakaopay - 시세인포뷰가 떠있지 않을 경우에 추가 <<
                return;
            }
        } else if (select_at != null && select_at.getTitle().equals("Badge")) {
            if (visible) {
                //2021.06.15 by lky - kakaopay - 시세인포뷰가 떠있지 않을 경우에 추가 >>
                if(!m_bShowToolTip) {
                    showEventBadgePanel();
                }
                //2021.06.15 by lky - kakaopay - 시세인포뷰가 떠있지 않을 경우에 추가 <<
                return;
            }
        }
        if (analToolSubbar == null && visible == false)
            return;
        if(analToolSubbar==null) {
            LayoutInflater factory = LayoutInflater.from(this.context);
            int layoutResId = this.getContext().getResources().getIdentifier("analtoolsubbar", "layout", this.getContext().getPackageName());
            analToolSubbar = (LinearLayout)factory.inflate(layoutResId, null);

            COMUtil.setGlobalFont(analToolSubbar);

            //분석툴바 선택시 삭제,전체삭제 메뉴 XML 메뉴 항목에 이벤트 연결.
            int[] btnResId = new int[3];	//2015. 1. 13 분석툴 수정기능 및 자석기능 추가
            String index="";
            View view = null;
            for(int i=0; i<btnResId.length; i++) {
                if((i+1)<10) index = "0"+(i+1);
                else index = ""+(i+1);
                btnResId[i] = context.getResources().getIdentifier("button"+index, "id", context.getPackageName());
                view = analToolSubbar.findViewById(btnResId[i]);
                if (view != null)
                {
                    if(view.getTag().equals("one")) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                //COMUtil._neoChart.removeSelectedAnalTool();
                                removeSelectedAnalTool();

                                saveAnalToolBySymbol();	//2013. 11. 21 추세선 종목별 저장	: 분석툴 단일 삭제
                            }
                        });
                    } else if(view.getTag().equals("all")) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                //COMUtil._neoChart.removeAllAnalTool();
                                //2013. 11. 21 추세선 종목별 저장>> : 분석툴 전체 삭제
//				        		removeAllAnalTool();
                                resetAnalToolBySymbol();
                                //2013. 11. 21 추세선 종목별 저장<<
                            }
                        });
                    }
                    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
                    else if(view.getTag().equals("modify")) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                showAnalToolSetting();
                            }
                        });
                    }
                    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<
                }
            }
            //COMUtil._chartMain.runOnUiThread(new Runnable() {
            //    public void run() {
            layout.addView(analToolSubbar);
            //    }
            //});
        }
        final boolean bVisible = visible;

        COMUtil._chartMain.runOnUiThread(new Runnable() {
            public void run() {
                int fv = -1;
                if(bVisible) {
                    fv = View.VISIBLE;
                    analToolSubbar.setVisibility(View.VISIBLE);
                } else {
                    fv = View.INVISIBLE;
                    analToolSubbar.setVisibility(View.INVISIBLE);
                }

                if(fv == View.VISIBLE)
                    //2012. 8. 13 삭제/전체삭제 대화창 오른쪽 너비 좀더 주기 : T60
//            		params.width = (int)COMUtil.getPixel(110);
                    params.width = (int)COMUtil.getPixel(141);	//2015. 1. 13 분석툴 수정기능 및 자석기능 추가
                params.height = (int)COMUtil.getPixel(30);

                //2016. 1. 22 분석툴 subbar 위치가 화면을 벗어남>>
                int xPos = params.leftMargin;
                int nRightPos = xPos + params.width;
                if(nRightPos > COMUtil.g_nDisWidth )
                {
                    xPos = COMUtil.g_nDisWidth - (params.width + (int)COMUtil.getPixel(10));
                    //2020.05.08 by JJH >> 가로모드 작업 (분석툴 버튼바) start
                    Configuration config = COMUtil.apiView.getContext().getResources().getConfiguration();
                    if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                        xPos = COMUtil.g_nDisWidth - (params.height + (int)COMUtil.getPixel(10));
                    //2020.05.08 by JJH >> 가로모드 작업 (분석툴 버튼바) end
                    params.leftMargin = xPos;

                }
                //2016. 1. 22 분석툴 subbar 위치가 화면을 벗어남<<

                analToolSubbar.setLayoutParams(params);
            }
        });

//    	if(visible) {
//    		COMUtil._chartMain.runOnUiThread(new Runnable() {
//                public void run() {
//                	analToolSubbar.setLayoutParams(params);
//                }
//			});
//
//    	}COMUtil.g_nDisWidth
    }

    LinearLayout analToolTextField = null;
    RelativeLayout.LayoutParams rlparam = null;
    //String strData = null;
    public void showAnalToolTextField(boolean visible, final RelativeLayout.LayoutParams params) {
        if(analToolTextField==null || analToolTextField.getVisibility() == View.INVISIBLE)
        {
            LayoutInflater factory = LayoutInflater.from(this.context);
            int layoutResId = this.getContext().getResources().getIdentifier("analtooltextfield", "layout", this.getContext().getPackageName());
            analToolTextField = (LinearLayout)factory.inflate(layoutResId, null);

            rlparam = params;

            layoutResId = this.getContext().getResources().getIdentifier("analtooltextfield_middle", "id", this.getContext().getPackageName());
            final EditText editText = (EditText)analToolTextField.findViewById(layoutResId);
            //strData = editText.getText().toString();
            editText.setOnEditorActionListener(this);
            layout.addView(analToolTextField);

            COMUtil._chartMain.runOnUiThread(new Runnable() {
                public void run() {
                    analToolTextField.setVisibility(View.VISIBLE);
                    analToolTextField.setLayoutParams(params);
                }
            });
        }
        else
        {
            layout.removeView(analToolTextField);
            analToolTextField = null;
            setChartToolBar(9999);
            removeAnalTool();

        }
//    	int v = -1;
//    	if(visible) {
//    		v = View.VISIBLE;
//    	} else {
//    		v = View.INVISIBLE;
//    	}
//    	final int fv = v;
//		COMUtil._chartMain.runOnUiThread(new Runnable() {
//            public void run()
//            {
//            	analToolTextField.setVisibility(fv);
//            	if(fv == View.VISIBLE)

//
//            }
//		});

    }

    //Device 의 키패드등에서 문자 입력후 확인 버튼 눌렸을 때의 동작
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        int layoutResId = this.getContext().getResources().getIdentifier("analtooltextfield_middle", "id", this.getContext().getPackageName());
        EditText editText = (EditText)analToolTextField.findViewById(layoutResId);
        String strData = editText.getText().toString();

        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
        {
            hideKeyPad();
//			ATextTool at = (ATextTool)analTools.lastElement();
//			RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)this.getLayoutParams();
//			at.addLabel(strData, layout, rlparam, param.topMargin, param.leftMargin);
//
//			analToolTextField.setVisibility(LinearLayout.INVISIBLE);
//
//			//COMUtil.setAnalTool(null);
//			((Base11)COMUtil._mainFrame.mainBase.baseP).setAnalTool(null);
            ATextTool at = (ATextTool)analTools.lastElement();
            if(at instanceof ATextTool) {
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)this.getLayoutParams();
                ((ATextTool)at).addLabel(strData, layout, rlparam, param.topMargin, param.leftMargin);
                saveAnalToolBySymbol();

                //analToolTextField.setVisibility(LinearLayout.INVISIBLE);
                layout.removeView(analToolTextField);
                analToolTextField = null;

                //COMUtil.setAnalTool(null);
                ((Base11)COMUtil._mainFrame.mainBase.baseP).setAnalTool(null);
            }

            setChartToolBar(9999);
            return true;
        }
        else
        {
            hideKeyPad();
            setChartToolBar(9999);
            ATextTool at = (ATextTool)analTools.lastElement();
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)this.getLayoutParams();
            at.addLabel(strData, layout, rlparam, param.topMargin, param.leftMargin);
            analToolTextField.setVisibility(LinearLayout.INVISIBLE);

            return true;
        }

//		return false;
    }

    //문자 분석툴 사용시 키패드 감추기 위함
    public void hideKeyPad()
    {
        int layoutResId = this.getContext().getResources().getIdentifier("analtooltextfield_middle", "id", this.getContext().getPackageName());
        EditText editText = ((EditText) analToolTextField.findViewById(layoutResId));
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    public void reSetDrawTool(boolean b){
        if(b){
            select_dt.reSet();
        }
        if(select_dt!=null) select_dt.setSelected(false);
        drawtool_select=false;
        select_dt=null;
        //select_graph=null;
        repaintAll();
    }
    public void reSetDrawTool(int[] c){//색상변경
        if(select_dt!=null){
            select_dt.setUpColor(c);
            repaintAll();
        }
    }
    public void reSetDrawToolThick(int t){//라인두께 변경
        if(select_dt!=null){
            select_dt.setLineT(t);
            repaintAll();
        }
    }
    public void reSetAnalTool(boolean b){
        if(select_at!=null) select_at.isSelect = false;
        analtool_select = false;
        if(analToolSubbar!=null && tradeViewP != null) {
//        	analParams.leftMargin = 0;
//        	analParams.topMargin = 0;
            this.showAnalToolSubbar(false, analParams);
        }
        repaintAll();
    }
    public void reSetAnalTool(int[] c){
        if(select_at!=null){
            select_at.setColor(c);
            repaintAll();
        }
    }
    public void reSetAnalToolThick(int t){
        if(select_at!=null){
            select_at.setLineT(t);
            repaintAll();
        }
    }
    //==================================
    // 픽셀의 x좌표를 가지고 차트의 index로 리턴
    //==================================
    /*
    public int getXToDate(int x){
        x=x-_cvm.Margin_L;
        int index = _cvm.getIndex();
        float xfactor = _cvm.getDataWidth();
        if(x>xscale.getBounds().width+_cvm.Margin_R) {
            return index+(int)((x-(xscale.getBounds().width+_cvm.Margin_R))/xfactor);
        }
        return index+(int)(x/xfactor);
    }
    */
    public int getXToDate(float x){
        return xscale.getXToDate(x);
    }

    public int getXToDateWithCount(float x, int num) {
        return xscale.getXToDateWithCount(x, num);
    }

    //==================================
    // 픽셀좌표를 해당데이터의 정중앙의 위치의 픽셀좌표로 변환하여 리턴
    // (예)십자선에서 좌표는 항상 봉의 정중앙에 위치
    //==================================
//    private int getChartXPixel(Point p){
//        int idx = getXToDate(p.x);
//        int pos = idx-_cvm.getIndex()+_cdm.getMargin();
//
//        return (int)(pos*xscale.xfactor+xscale.xfactor/2)+_cvm.Margin_L;
//    }
    //==================================
    // 인덱스에 해당하는 분석도구 리턴(case의 int값은 ChartUtil참조)
    //==================================
    private AnalTool getAnalTool(int index,Block block){
        AnalTool at=null;
        switch(index){
            case COMUtil.TOOLBAR_CONFIG_LINE://
                at= new AChuseLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_CROSS://
                at = new ACrossLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_VERT:
                at= new AVLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_HORZ:
                at = new AHLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_LINE_TRISECT:
                at = new ADivTTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_LINE_QUARTER:
                at = new ADivFTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_FIBOARC:
                at = new APivoArcTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_FIBOFAN:
                at = new APivoFanTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_FIBOTIME:
                at = new APivoTimeZoneTool(block); //피보나치 시간대.
                break;
            case COMUtil.TOOLBAR_CONFIG_RECT:
                at = new ARectTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_ROUND:
                at = new AOvalTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_FIBORET:
                at = new APivoRetTool(block); //피보나치 조정대.
                break;
            case COMUtil.TOOLBAR_CONFIG_GANNFAN://갠팬
                at = new AGannFanTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_DOWNGANNFAN://갠팬
                at = new AGannFanTool(block);
                ((AGannFanTool)at).setIsDown(true);
                break;
            case COMUtil.TOOLBAR_CONFIG_GANNGRID://갠그리드
                at = new AGannGridTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_SPEEDLINE:
                at = new ASpeedLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_ANDREW:
                at = new AAndrewsPiTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_GANNLINE:
                at = new AGannLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_SPEEDARC:
                at = new ASpeedArcTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_SPEEDFAN:
                at = new ASpeedFanTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_CYCLELINES:
                at = new ACycleLinesTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_ELLIOT:
                at = new AElliotTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_REGRESSIONLINE:
                at = new ARegressionLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_REGRESSIONLINES:
                at = new ARegressionLinesTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_TRIANGLE:
                at = new ATriangleTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_TEXT:
                at = new ATextTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_LEFTLINE:
                at = new ALeftLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_RIGHTLINE:
                at = new ARightLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_BSLINE:
                at = new ABothLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_PARALLEL:
                at = new AParallelTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_PERPENDICULAR:
                at = new APerpendicularTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_DEGREE:
                at = new ADegreeTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_TARGETNT:
                at = new ATargetNtTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_FIBOTARGET:
                at = new AFiboTargetTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_DIAGONAL:
                at = new ADiagonalTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_PENCIL:
                at = new APencilLineTool(block);
                break;
            case COMUtil.TOOLBAR_CONFIG_PERIOD:
                at = new APeriodReturnLineTool(block);
                break;
//	        case 16:
//	            at = new AArrowTool(block,AArrowTool.TEXT_DRAW,this.drawMark);
//	        break;
        }
        return at;
    }
    //==================================
    //ChartViewManager 추가 (차트의 UI를 셋)
    //==================================
    int maxWidth = 0, maxHeight=0;
    public void reSetUI(boolean bDraw){
//		System.out.println("<<<<<<<<<<<<<<reSetUI");
        final RectF bound = chart_bounds;

        if(_cvm.isStandGraph() || !_cvm.isCorporateCalendarFunc) {
            _cvm.EVENT_BADGE_H = 0; //2021.04.28 by lyk - kakaopay - 종목 캘린더 이벤트 뱃지 표시 영역
        } else {
            _cvm.EVENT_BADGE_H = (int)COMUtil.getPixel_H(18); //2021.04.28 by lyk - kakaopay - 종목 캘린더 이벤트 뱃지 표시 영역 높이
        }

        setUI1();
        if(_cvm.useUnderToolbar()||_cvm.useTooltip()||_cvm.useStatusBar()){
            _cvm.setBounds(0,_cvm.Margin_T+bound.top,bound.right,bound.bottom-_cvm.Margin_T-_cvm.Margin_B-_cvm.TOOLBAR_B-(int)COMUtil.getPixel(1));
            //2013.04.05 by LYH >> 고해상도 처리 <<
//            xscale.setBounds(_cvm.Margin_L+(int)COMUtil.getPixel(2),bound.bottom-(_cvm.SCROLL_B+(int)COMUtil.getPixel(_cvm.XSCALE_H)),bound.right-(_cvm.Margin_R + pivotGab)-(int)COMUtil.getPixel(4) ,bound.bottom-(_cvm.SCROLL_B+(int)COMUtil.getPixel(_cvm.XSCALE_H))+(int)COMUtil.getPixel(_cvm.XSCALE_H));

            //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정>>
//            xscale.setBounds(_cvm.Margin_L+(int)COMUtil.getPixel(2),bound.bottom-(_cvm.SCROLL_B+_cvm.XSCALE_H),bound.right-(_cvm.Margin_R + pivotGab)-(int)COMUtil.getPixel(2) ,bound.bottom-(_cvm.SCROLL_B+_cvm.XSCALE_H)+_cvm.XSCALE_H);
            final int nBlockRightPadding = _cvm.PADDING_RIGHT;
            //xscale.setBounds(_cvm.Margin_L+(int)COMUtil.getPixel(2),bound.bottom-(_cvm.SCROLL_B+_cvm.XSCALE_H),bound.right-(_cvm.Margin_R + pivotGab)-nBlockRightPadding ,bound.bottom-(_cvm.SCROLL_B+_cvm.XSCALE_H)+_cvm.XSCALE_H);
            if(!_cvm.bIsLineChart && !_cvm.bIsLine2Chart &&!_cvm.bIsLineFillChart && !_cvm.bIsNewsChart && !_cvm.bInvestorChart &&!_cvm.bStandardLine && basic_block != null)
            {
                xscale.setBounds(_cvm.Margin_L + (int) COMUtil.getPixel(2),
                        bound.bottom - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H,
                        bound.right - (_cvm.Margin_R + pivotGab) - nBlockRightPadding,
                        bound.bottom  - _cvm.EVENT_BADGE_H);

                //2021.04.06 - 차트 내부 지표선택 스크롤뷰 추가
//				COMUtil._chartMain.runOnUiThread(new Runnable() {
//					@RequiresApi(api = Build.VERSION_CODES.N)
//					public void run() {
//						if ( scrollViewLinearLayoutlp == null ) {
//							// 임시 배지 아이콘 badge icon
//							indicatorViewlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//							indicatorViewlp.setMargins(0,
//									Math.round(basic_block.getBounds().bottom),
//									0,
//									Math.round(basic_block.getBounds().bottom));
//							indicatorView.setLayoutParams(indicatorViewlp);
//							indicatorView.setHorizontalScrollBarEnabled(false);
//							indicatorView.setVerticalScrollBarEnabled(false);
//							scrollViewLinearLayout = new LinearLayout(context);
//							scrollViewLinearLayoutlp = new FrameLayout.LayoutParams(COMUtil.chartWidth+200, (int) COMUtil.getPixel(26));
////							indicatorViewlp.setMargins(_cvm.Margin_L + (int) COMUtil.getPixel(2),
////									Math.round(basic_block.getBounds().bottom),
////									Math.round(bound.right - (_cvm.Margin_R + pivotGab) - nBlockRightPadding),
////									Math.round(basic_block.getBounds().bottom));
//							scrollViewLinearLayout.setLayoutParams(scrollViewLinearLayoutlp);
//							scrollViewLinearLayout.setBackgroundColor(Color.WHITE);
//							scrollViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//							Map<String, Integer> indicatorItems = new LinkedHashMap<>();
//							indicatorItems.put("MACD", ChartUtil.MACD);
//							indicatorItems.put("KDJ", 0);
//							indicatorItems.put("RSI", ChartUtil.RSI);
//							indicatorItems.put("ROC", ChartUtil.ROC);
//							indicatorItems.put("DMA", 0);
//							indicatorItems.put("VOL", ChartUtil.VOLUME);
//							indicatorItems.put("거래량(기본)", ChartUtil.TRADING_VALUE);
//							indicatorItems.put("외국인 보유", ChartUtil.MARKET12);
//							indicatorItems.put("외국인/기관/개인 추세", 0);
//							indicatorItems.put("기관 순매수", ChartUtil.MARKET5);
//							indicatorItems.put("외국인 순매수", ChartUtil.MARKET13);
//
//							final OnClickListener indicatorViewItemClickListener = new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									// btn에 setTag로 tag값을 설정하여 해당 값으로 이벤트 구현.
//									if ((int)v.getTag() == 0) return;
//									CheckBox cb = new CheckBox(context);
//									if (v.isSelected()) {
//										cb.setChecked(false);
//										cb.setTag(v.getTag());
//										COMUtil.setJipyo(cb);
//										v.setSelected(false);
//										v.invalidate();
//									} else {
//										cb.setChecked(true);
//										cb.setTag(v.getTag());
//										COMUtil.setJipyo(cb);
//										v.setSelected(true);
//										v.invalidate();
//									}
//								}
//							};
//
//							indicatorItems.forEach((new BiConsumer<String, Integer>() {
//								@SuppressLint("ResourceType")
//								@Override
//								public void accept(String itemName, Integer code) {
//									LinearLayout subLayout = new LinearLayout(context);
//									RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(60), (int) COMUtil.getPixel(24));
//									lp2.leftMargin=0;
//									lp2.topMargin=0;
//
//									RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(60), (int) COMUtil.getPixel(24));
//									btnParams.leftMargin=2;
//									btnParams.topMargin=0;
//
//									subLayout.setLayoutParams(lp2);
//									subLayout.setOrientation(LinearLayout.HORIZONTAL);
//									Button btn = new Button(context);
//									btn.setLayoutParams(btnParams);
//									btn.setMinHeight(0);
//									btn.setMinWidth(0);
//									btn.setMinimumWidth(0);
//									btn.setMinimumHeight(0);
//									btn.setPadding(2, 2, 2, 2);
//									btn.setTextSize(12);
//									btn.setIncludeFontPadding(false);
//									btn.setSingleLine();
//									btn.setTextColor(context.getResources().getColorStateList(R.drawable.selector_indicatorview, null));
//									btn.setBackgroundColor(Color.TRANSPARENT);
//									btn.setText(itemName);
//									btn.setTag(code);
//									btn.setSelected(false);
//									btn.setOnClickListener(indicatorViewItemClickListener);
//
//									subLayout.addView(btn);
//									scrollViewLinearLayout.addView(subLayout);
//								}
//							}));
//							indicatorView.addView(scrollViewLinearLayout);
//							layout.addView(indicatorView);
//						} else {
////							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////							indicatorViewlp.setMargins(_cvm.Margin_L + (int) COMUtil.getPixel(2),
////									Math.round(basic_block.getBounds().bottom),
////									Math.round(bound.right - (_cvm.Margin_R + pivotGab) - nBlockRightPadding),
////									Math.round(basic_block.getBounds().bottom));
//							scrollViewLinearLayout.setLayoutParams(scrollViewLinearLayoutlp);
//							indicatorView.setLayoutParams(indicatorViewlp);
//						}
//						xscale.setIndicatorView(indicatorView);
//					}
//				});
            }
            else {
                xscale.setBounds(_cvm.Margin_L + (int) COMUtil.getPixel(2),
                        bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H + _cvm.EVENT_BADGE_H),
                        bound.right - (_cvm.Margin_R + pivotGab) - nBlockRightPadding,
                        bound.bottom - (_cvm.SCROLL_B + _cvm.XSCALE_H + _cvm.EVENT_BADGE_H));
            }
            //2013. 10. 29 1분선차트, 마운틴차트 제외한 차트 오른쪽 패딩 2->8 정도로 넓게 수정<<

            //2013.04.05 by LYH >> 고해상도 처리 <<
        }else{
//            _cvm.setBounds(0,(int)COMUtil.getPixel(_cvm.Margin_T)+viewH,bound.right,bound.bottom-(int)COMUtil.getPixel(_cvm.Margin_T)-(int)COMUtil.getPixel(_cvm.Margin_B)-1-viewH);
        }

        if(xscale!=null) xscale.setOuterBounds(bound.left, bound.top, bound.right, bound.bottom);
        if(m_nCurDataWidth > 0 && _cdm.getCount() > 0)
        {
            int nCurViewNum = _cvm.getViewNum();
            int nCurStart = _cvm.getIndex();
            int nViewNum = (int)((float)xscale.getBounds().width()/m_nCurDataWidth);
            if(nViewNum>_cdm.getCount())
                nViewNum =_cdm.getCount();
            if(nViewNum<1)
                nViewNum = 1;
            int nStart = nCurStart-(nViewNum-nCurViewNum);
            if(nStart<0)
                nStart = 0;
            _cvm.setIndex(nStart);
            _cvm.setViewNum(nViewNum);
        }
        m_nCurDataWidth = 0;

        if(bDraw) {
            if(!_cvm.bIsInnerText) //2017.07.10 by pjm 거래원 bar 차트 안나오던 오류 처리.
                resetTitleBoundsAll(); //2017.07.07 by pjm 지표 삭제시 블럭 타이틀 그리기.
            repaintAll();
        }
    }

    int chartIndex = 0;
    public void setChartIndex(int index) {
        chartIndex = index;
    }

    //===============================
    // 차트블럭의 재배치 (블럭 추가/삭제시)
    //===============================
    public void setUI1(){
        if(_cvm.bIsLine2Chart || (_cvm.bIsLineFillChart && !_cvm.bIsOneQStockChart) || _cvm.bIsMiniBongChart || _cvm.bIsInnerText || _cvm.bIsLineChart )
            _cvm.bIsTransparent = true;

        if(blocks == null) return;
//        int each_col_width= chart_bounds.width();
//        int ww=chart_bounds.left+each_col_width;
        float w = chart_bounds.width();
        //2011.08.05 by LYH >> 시세바 추가로 마진 설정 이후로 이동 <<
        //int h = (chart_bounds.height() - (int)COMUtil.getPixel((_cvm.XSCALE_H+_cvm.SCROLL_B)))/(blocks.size()+3);

        int drawBlockCnt = blocks.size();
//        boolean bPhone = false;
//        if(drawBlockCnt>m_nMaxIndicatorCount+1 && !COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//        	bPhone = true;
//        	drawBlockCnt = m_nMaxIndicatorCount+1;
//        }
        if(drawBlockCnt>m_nMaxIndicatorCount+1) {
            //bPhone = true;
            drawBlockCnt = m_nMaxIndicatorCount+1;
        }

        //2021. 4. 28  by hanjun.Kim - kakaopay - 스탠드그래프 indicatorview 처리 >>
        indicatorView.setVisibility(View.GONE);
        //2021. 4. 28  by hanjun.Kim - kakaopay - 스탠드그래프 indicatorview 처리 <<

        int sx = 0;//getBounds().x;
        //2011.08.05 by LYH >> 시세바 추가
        // int sy = 0;//getBounds().y;
        int sy = _cvm.Margin_T;
        //2011.08.05 by LYH <<
        //2013.04.05 by LYH >> 고해상도 처리
//        int h = (chart_bounds.height() -sy - (int)COMUtil.getPixel((_cvm.XSCALE_H+_cvm.SCROLL_B)))/(blocks.size()+2);
//		float totHeight = (chart_bounds.height() -sy - (_cvm.XSCALE_H+_cvm.SCROLL_B));
        float totHeight = chart_bounds.height() - sy - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H;	//XScale
        if(_cvm.bIsNewsChart)
        {
            totHeight = chart_bounds.height() -sy - (int)COMUtil.getPixel(30);
        }
        else if(_cvm.bIsLineChart || _cvm.bIsLine2Chart || _cvm.bIsLineFillChart || _cvm.bInvestorChart || _cvm.bStandardLine)
        {
            totHeight = (chart_bounds.height() -sy - (_cvm.XSCALE_H + _cvm.SCROLL_B + _cvm.EVENT_BADGE_H));
        }
        else if(_cvm.bIsMiniBongChart)
        {
            totHeight = chart_bounds.height() -sy;
        }

//		if(_cvm.bIsLineFillChart)
//			totHeight += (int)COMUtil.getPixel(5);
//		float h = totHeight/(blocks.size()+2);

        //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) >>
        float h = totHeight / 4;
        if(drawBlockCnt > 2) {
            h = totHeight / 7;
        }
        //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) <<

        if(_cvm.bIsMiniBongChart)
            //h = totHeight/blocks.size();
            h = totHeight/(blocks.size()+1);
        //2013.07.22 by LYH >> 투자자 차트 블럭 크기 동일하게 수정.
        if(_cvm.bInvestorChart || _cvm.bStandardLine)	//2015.01.08 by LYH >> 3일차트 추가
            h = totHeight/(blocks.size());
        //2013.07.22 by LYH <<
        //2013.04.05 by LYH <<
        int bcnt=0;
        float blockH = 0;
        for(int i=0;i<drawBlockCnt;i++){
//            try{
            final Block cb = (Block)blocks.elementAt(i);
            //2013.07.22 by LYH >> 투자자 차트 블럭 크기 동일하게 수정. <<
            if(cb.getBlockType()==Block.BASIC_BLOCK  && !_cvm.bInvestorChart && !_cvm.bStandardLine && !_cvm.bIsLineChart){	//2015.01.08 by LYH >> 3일차트 추가
                if(!isChangeBlock) {
                    if(drawBlockCnt == 1)
                        cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,sy+totHeight,true);
                    else
                    {
                        if(_cvm.bIsMiniBongChart)
                            cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,(h*2+(int)COMUtil.getPixel(5))+sy+h*bcnt+chart_bounds.top,true);
                        else {
                            //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) >>
                            if(drawBlockCnt > 2) {
                                blockH = totHeight - (h * (drawBlockCnt - 1));
                            } else {
                                blockH = h * 3;
                            }
                            cb.setBounds(sx, sy + h * bcnt + chart_bounds.top, w + sx, blockH + sy + h * bcnt + chart_bounds.top, true);
                            //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) <<
                        }
                    }
                } else {
                    cb.setBounds(sx,cb.getY(),w+sx,cb.getY()+cb.getHeight(),true);
                }
                if(!_cvm.m_bCurrentChart && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsLine2Chart && !_cvm.bInvestorChart && !_cvm.bIsMiniBongChart && !_cvm.bIsTodayLineChart)
                {
                    //2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
                    //if(drawBlockCnt==1) { //가격차트 블럭이고 블럭갯수가 한개라면 블럭이동버튼을 숨긴다.
                    if(drawBlockCnt==1 || _cvm.bSubChart) { //가격차트 블럭이고 블럭갯수가 한개라면 블럭이동버튼을 숨긴다.
                        //2013.09.13 <<
                        cb.setHideChangeBlockButton(true);
                    } else {
                        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
                        //                  final int x = cb.getOutBounds().right+param.leftMargin; //분할차트에서 블럭삭제버튼 위치문제수정.(2011.09.19 by lyk)
                        //2013. 9. 23 블록이동버튼 x축 위치 조정>>
//		                    int x = chart_bounds.right+param.leftMargin; //블럭삭제 버튼을 우측 가격차트 영역으로 이동.
                        float x = chart_bounds.right+param.leftMargin+(int)COMUtil.getPixel(2);
                        //2013. 9. 23 블록이동버튼 x축 위치 조정>>
                        float y = cb.getOutBounds().top+param.topMargin;
                        cb.setChangeBlockBtn(x, y);
                    }

                    //2014. 3. 20 Y축 봉갯수 누르면 봉입력창 뜨고 봉갯수변경
                    ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
                    float x = chart_bounds.right+param.leftMargin+(int)COMUtil.getPixel(2);
                    float y = cb.getOutBounds().top+param.topMargin;
                    cb.setViewNumTextViewInYScale(x, y);
                }
                if(_cvm.bIsMiniBongChart)
                    //bcnt+=1;
                    bcnt+=2;
                else {
                    //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) >>
                    if(drawBlockCnt > 2) {
                        bcnt += 7 - (drawBlockCnt - 1);
                    }
                    else {
                        bcnt += 3;
                    }
                    //2021.05.11 by lyk - kakaopay - 보조지표 높이 기준값 적용 (보조지표 갯수가 2개이면 1/4 그외엔 1/7로 높이 고정) <<
                }
            }else if(cb.getBlockType()==Block.STAND_BLOCK){
                //cb.setBounds(sx,sy+chart_bounds.top,w+sx,_cvm.getBounds().bottom-(_cvm.XSCALE_H+_cvm.SCROLL_B+viewH),false);
                //2013.04.05 by LYH >> 고해상도 처리
                //cb.setBounds(sx,sy+chart_bounds.top,w+sx,chart_bounds.bottom-(int)COMUtil.getPixel(_cvm.XSCALE_H+_cvm.SCROLL_B+viewH)-3,false);
                cb.setBounds(sx,sy+chart_bounds.top,w+sx,totHeight,false);
                //2013.04.05 by LYH <<
            }else{
                //보조지표 한 화면에서 볼 경우 처리
                if(!isChangeBlock) {
                    if(drawBlockCnt == 1)
                        cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,sy+totHeight,true);
                    else if(_cvm.bIsMiniBongChart)
                    {
                        cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,(h-(int)COMUtil.getPixel(5))+sy+h*bcnt+chart_bounds.top,true);
                    }
                    else
                    {
                        cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,h+sy+h*bcnt+chart_bounds.top,true);
                    }
                }  else {
                    cb.setBounds(sx,cb.getY(),w+sx,cb.getY()+cb.getHeight(),true);
                }

//                    if((bPhone && i<2) || !bPhone) {
//                    	bcnt++;
//                    }
                if(i<m_nMaxIndicatorCount)
                    bcnt++;
                //2013.09.13 >> 종합차트 등에 탭차트 로 들어가는 차트 타입 추가
                //if(!_cvm.m_bCurrentChart && !_cvm.bInvestorChart)
                if(!_cvm.m_bCurrentChart && !_cvm.bInvestorChart && !_cvm.bStandardLine  && !_cvm.bSubChart && !_cvm.bInvestorChart && !_cvm.bIsLineChart)
                //2013.09.13 <<
                {
                    ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
                    //                    final int x = cb.getOutBounds().right+param.leftMargin; //분할차트에서 블럭삭제버튼 위치문제수정.(2011.09.19 by lyk)
                    //2013. 9. 23 블록이동버튼 x축 위치 조정>>
//	                    final int x = chart_bounds.right+param.leftMargin;
                    final float x = chart_bounds.right+param.leftMargin+COMUtil.getPixel(2); //블럭삭제 버튼을 우측 가격차트 영역으로 이동.
                    final float y = cb.getOutBounds().top+param.topMargin;
                    //2013. 9. 23 블록이동버튼 x축 위치 조정>>

                    if (COMUtil._chartMain != null) { //2024.01.04 by sJW - 크래시로그 수정
                        COMUtil._chartMain.runOnUiThread(new Runnable() {
                            public void run() {
                                cb.setBlockBtn(x, y, cb.getTitle());
                                cb.setChangeBlockBtn(x, y);
                            }
                        });
                    }
//	            		COMUtil._chartMain.runOnUiThread(new Runnable() {
//					((Activity)COMUtil.apiView.getContext()).runOnUiThread(new Runnable() {
//						public void run() {
//							cb.setBlockBtn(x, y, cb.getTitle());
//							cb.setChangeBlockBtn(x, y);
//						}
//					});
                }
                if(_cvm.isStandGraph()) {
                    Block stand=getChartBlockByType(Block.STAND_BLOCK);
                    if(stand==null) {
                        _cvm.setStandGraph(false);
                        cb.setHideChangeBlockButton(false);
                    } else {
                        cb.setHideChangeBlockButton(true);
                        //2021. 4. 28  by hanjun.Kim - kakaopay - 스탠드그래프 indicatorview 처리 >>
                        indicatorView.setVisibility(View.GONE);
                        //2021. 4. 28  by hanjun.Kim - kakaopay - 스탠드그래프 indicatorview 처리 <<
                    }
                }
            }
            cb.setXScale(xscale); //20030702 ykLee

            //2013.04.05 by LYH >> 고해상도 처리
            if(i!=0 && i==drawBlockCnt-1)
            {
                RectF cbRect = cb.getBounds();
                cb.setBounds(cbRect.left,cbRect.top,cbRect.right, sy+totHeight,true);
            }
//            }catch(Exception e){
//            }
            // 페이증권 미사용 기능 주석 처리
//			if(!COMUtil._mainFrame.bIsAnalToolbar) { //분석툴바가 없는 화면에서 처리
//				cb.setHideChangeBlockButton(true);
//				cb.setHideViewNumTextView(true);
//				cb.setHideDelButton(true);
//			}
        }

        for(int i=0; i<rotate_blocks.size(); i++) {
            final Block cb = (Block)rotate_blocks.elementAt(i);

            //cb.setBounds(sx,sy+h*bcnt+chart_bounds.top,w+sx,h+sy+h*bcnt+chart_bounds.top,true);
            cb.setBounds(sx,sy+h*bcnt+chart_bounds.top+_cvm.XSCALE_H,w+sx,h+sy+h*bcnt+chart_bounds.top+_cvm.XSCALE_H,true);
            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
//            final int x = cb.getOutBounds().right+param.leftMargin; //분할차트에서 블럭삭제버튼 위치문제수정.(2011.09.19 by lyk)
            //2013. 9. 23 블록이동버튼 x축 위치 조정>>
            final float x = chart_bounds.right+param.leftMargin+(int)COMUtil.getPixel(2); //블럭삭제 버튼을 우측 가격차트 영역으로 이동.
            final float y = cb.getOutBounds().top+param.topMargin;
            if (COMUtil._chartMain != null) {
                COMUtil._chartMain.runOnUiThread(new Runnable() {
                    public void run() {
                        cb.setBlockBtn(x, y, cb.getTitle());
                        cb.setChangeBlockBtn(x, y);
                    }
                });
            }
            cb.setXScale(xscale); //20030702 ykLee
        }

        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
        if(m_strSizeInfo != null)
        {
            String[] rateInfos = m_strSizeInfo.split("=");
            if(rateInfos.length>1 && rateInfos.length == blocks.size())
            {
                double dRate;

                //2021.04.29 by lyk - kakaopay - 스크롤바 위치가 차트 하단일 경우 처리 (리사이즈 블럭 이벤트 발생시 xscale, eventbadge 포함하여 계산)
                float nChartHeight = chart_bounds.height()-_cvm.Margin_T-_cvm.XSCALE_H-_cvm.EVENT_BADGE_H-(int)COMUtil.getPixel(6);

//				float nChartHeight = chart_bounds.height()-_cvm.Margin_T-(int)COMUtil.getPixel(6);	//XScale
                float nHeight=0;
                float nTotHeight=0;
                float y = chart_bounds.top+_cvm.Margin_T;
                float x = chart_bounds.left+chart_bounds.width()+(int)COMUtil.getPixel(2);
                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)this.getLayoutParams();
                //2023.10.09 by SJW - 콤마(,) 포함되어있는 String -> Double 파싱할 때 생기는 에러 수정 >>
//                for(int i=0; i<blocks.size(); i++) {
//                    dRate = Double.parseDouble(rateInfos[i]);
//
//                    if(i == blocks.size()-1)
//                    {
//                        nHeight = nChartHeight-y+_cvm.Margin_T+(int)COMUtil.getPixel(6); //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치(마지막 블럭 크기 보정)
//                    }
//                    else
//                    {
//                        nHeight = Math.round((nChartHeight*dRate));
//                    }
//                    Block cb=blocks.elementAt(i);
//                    if(cb.getBlockType() != Block.STAND_BLOCK)
//                    {
//                        cb.setHBounds(y, nHeight);
//
//                        cb.setChangeBlockBtn(x, cb.getOutBounds().top + +param.topMargin);
//                        cb.setBlockBtn(x, cb.getOutBounds().top + +param.topMargin, cb.getTitle());
//                    }
//                    if(i != blocks.size()-1)
//                        y += nHeight;
//                }
                for(int i=0; i<blocks.size(); i++) {
                    String rateInfo = rateInfos[i].replace(",", ".");
                    try {
                        dRate = Double.parseDouble(rateInfo);

                        if (i == blocks.size() - 1) {
                            nHeight = nChartHeight - y + _cvm.Margin_T + (int) COMUtil.getPixel(6); //2021.06.22 by lyk - kakaopay - 블록 리사이즈 원터치(마지막 블럭 크기 보정)
                        } else {
                            nHeight = Math.round((nChartHeight * dRate));
                        }
                        Block cb = blocks.elementAt(i);
                        if (cb.getBlockType() != Block.STAND_BLOCK) {
                            cb.setHBounds(y, nHeight);

                            cb.setChangeBlockBtn(x, cb.getOutBounds().top + +param.topMargin);
                            cb.setBlockBtn(x, cb.getOutBounds().top + +param.topMargin, cb.getTitle());
                        }
                        if (i != blocks.size() - 1)
                            y += nHeight;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                //2023.10.09 by SJW - 콤마(,) 포함되어있는 String -> Double 파싱할 때 생기는 에러 수정 <<
                if(rotate_blocks != null) {
                    for(int i=0; i<rotate_blocks.size(); i++) {
                        Block bl = (Block)rotate_blocks.get(i);
                        bl.setHBounds(y,nHeight);

                        bl.setChangeBlockBtn(x, bl.getOutBounds().top+param.topMargin);						//블럭이동버튼 위치 재조정.
                        bl.setBlockBtn(x, bl.getOutBounds().top+param.topMargin, bl.getTitle());			//블럭삭제버튼 위치 재조정.
                    }
                }
            }
        }
        //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end
        // 익셉션 발생 안하게 처리 seongsu/itgen 2014/3/14
        // 페이증권 미사용 기능 주석 처리
//		if(COMUtil._mainFrame.mainBase.baseP instanceof Base11)
//			((Base11)COMUtil._mainFrame.mainBase.baseP).bringToFrontCtrl();

        //2015. 11. 5 차트 좌상단 확대/축소 버튼, 차트 우상단 분석툴바 삭제/전체삭제 버튼 >>
//        initExpandReduceButtons();
//        initDeleteAnalButtons();
        //2015. 11. 5 차트 좌상단 확대/축소 버튼, 차트 우상단 분석툴바 삭제/전체삭제 버튼 <<
    }
    //==================================
    // 차트가 세로모드인경우의 UI설정
    //==================================
    public void setUI2(){
        if(blocks == null) return;
//        int each_col_width= chart_bounds.width();
//        int ww=getLeft()+each_col_width;
        float w;
        float h;
        float sx = 0;
        float sy = 0;
        int bcnt=0;
        int leftRow = blocks.size()/2, rightRow = blocks.size()-leftRow;
        Block stand=getChartBlockByType(Block.STAND_BLOCK);
        if(stand!=null){
            w = chart_bounds.width();
            stand.setBounds(sx,sy,w,_cvm.getBounds().bottom-(_cvm.XSCALE_H+_cvm.SCROLL_B),false);
        }else{
            h = (getHeight()-(_cvm.XSCALE_H+_cvm.SCROLL_B))/(leftRow);
            w = chart_bounds.width()/2;
            for(int i=0;i<blocks.size();i++){
                if(i==leftRow){
                    bcnt =0;
                    sx = w;
                    h = (getHeight()-(_cvm.XSCALE_H+_cvm.SCROLL_B))/(rightRow);
                }
                Block cb = (Block)blocks.elementAt(i);
                cb.setBounds(sx,sy+h*bcnt+viewH,w-TP_WIDTH,h-viewH,true);
                bcnt++;
            }
        }
    }

    RelativeLayout.LayoutParams analParams = null;
    private boolean selectedAnalTool(PointF p){
        synchronized(this) {
            if (analTradeTools != null) {
                if (analParams == null)
                    analParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(100), (int) COMUtil.getPixel(38));
                int alen = analTradeTools.size();
                for (int i = 0; i < alen; i++) {
                    AnalTool at = (AnalTool) analTradeTools.elementAt(i);
                    try {
                        if (at.isSelected(p)) {
                            at.isSelect = true;
                            select_at = at;
                            DoublePoint[] analData = at.data;
                            DoublePoint analDic;
                            if (analData.length > 0) {
                                analDic = analData[0];
                                int xPos = (int)at.dateToX(analDic.x);
                                int yPos = (int)at.priceToY(analDic.y);
                                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
                                analParams.leftMargin = xPos + param.leftMargin;
                                analParams.topMargin = yPos + param.topMargin;
                                this.showAnalToolSubbar(true, analParams);
                            }

                            return true;
                        }
                    } catch (Exception e) {

                    }
                }
            }
            if (aBadgeTools != null) {
                if (analParams == null)
                    analParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(100), (int) COMUtil.getPixel(38));
                int alen = aBadgeTools.size();
                for (int i = 0; i < alen; i++) {
                    AnalTool at = (AnalTool) aBadgeTools.elementAt(i);
                    try {
                        if (at.isSelected(p)) {
                            at.isSelect = true;
                            select_at = at;
                            DoublePoint[] analData = at.data;
                            DoublePoint analDic;
                            if (analData.length > 0) {
                                analDic = analData[0];
                                int xPos = (int)at.dateToX(analDic.x);
                                int yPos = (int)at.priceToY(analDic.y);
                                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
                                analParams.leftMargin = xPos + param.leftMargin;
                                analParams.topMargin = yPos + param.topMargin;
                                this.showAnalToolSubbar(true, analParams);
                            }

                            return true;
                        }
                    } catch (Exception e) {

                    }
                }
            }
            try {
                if (analTools != null) {
                    if (analParams == null)
                        analParams = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(100), (int) COMUtil.getPixel(38));
                    int alen = analTools.size();
                    for (int i = 0; i < alen; i++) {
                        AnalTool at = (AnalTool) analTools.elementAt(i);
                        if (at.isSelected(p)) {
                            at.isSelect = true;
                            select_at = at;
                            DoublePoint[] analData = at.data;
                            DoublePoint analDic;
                            if (analData.length > 0) {
                                analDic = analData[0];
                                float xPos = at.dateToX(analDic.x);
                                float yPos = at.priceToY(analDic.y);
                                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
                                analParams.leftMargin = (int) (xPos + param.leftMargin);
                                analParams.topMargin = (int) (yPos + param.topMargin);
                                this.showAnalToolSubbar(true, analParams);
                            }

                            return true;
                        }
                    }
                }
                select_at=null;
                this.showAnalToolSubbar(false, analParams);				        //2015. 1. 29 가상매매연습기 매도/매수 표시
                return false;
            } catch (Exception e){
                return false;
            }
        }
    }
    private boolean selectedDrawTool(PointF p){
        Block block = null;
        AbstractGraph graph;
        DrawTool dt =null;
        for(int i=0;i<blocks.size();i++){
            block = (Block)blocks.elementAt(i);
            if(block.getBounds().contains(p.x, p.y)) break;
        }
        if(block == null) return false;
        Vector<AbstractGraph> v = block.getGraphs();
        for(int i=v.size()-1; i>=0; i--){
            graph = (AbstractGraph)v.elementAt(i);
            Vector<DrawTool> v1 = graph.getDrawTool();
            for(int j=0; j<v1.size(); j++){
                dt = (DrawTool)v1.elementAt(j);
                if(dt.isVisible() && (dt.getTitleBounds().contains(p.x, p.y) || dt.isSelected(p, getXToDate(p.x))) ){
                    dt.setSelected(true);
                    select_dt = dt;
                    //select_graph = graph;
                    //select_block = block;
                    return true;
                }
            }
        }
        select_dt = null;
        //select_graph = null;
        return false;
    }
    //=================================
    // 해당 포인트가 블럭내에 있는지 확인
    //=================================
//    private boolean containsBlock(Point p){
//        Block block = null;
//        for(int i=0;i<blocks.size();i++){
//            block = (Block)blocks.elementAt(i);
//            if(block.getGraphBounds().contains(p.x, p.y)&& select_block==block)
//                return true;
//        }
//        return false;
//    }
    //==================================
    // s점과 e점사이에 라인을 긋고 각도를 표시
    //==================================
//	private void drawLineWithAngle(Canvas gl,Point s, Point e){
//
//		_cvm.drawLine(gl, s.x, s.y, e.x, e.y, CoSys.at_col, 1.0f);
//		int w= Math.abs(s.x-e.x);
//		_cvm.drawLine(gl, s.x, s.y, s.x+w, s.y, CoSys.at_col, 1.0f);
//		int arc= ChartUtil.getAngle(s, e);
//
//		_cvm.drawString(gl, _cvm.CST, s.x,s.y+(int)COMUtil.getPixel(5), arc+"degree");
//	}
    private void drawLineWithAngle(Canvas gl,PointF s, PointF e){


        _cvm.drawLine(gl, s.x, s.y, e.x, e.y, CoSys.at_col, 1.0f);
        double length = Math.sqrt(Math.pow((e.x - s.x),2)  + Math.pow((e.y - s.y),2) );
        _cvm.drawLine(gl, s.x, s.y, s.x+(int)length, s.y, CoSys.at_col, 1.0f);
        double arc= ChartUtil.getAngle(s, e);

        DecimalFormat format = new DecimalFormat(".#");

        _cvm.drawString(gl, _cvm.CST, s.x,s.y+(int)COMUtil.getPixel(5), format.format(Math.abs(arc))+"°");
        _cvm.drawArc1(gl, (int)(length / 3), s, true, CoSys.at_col, 0.0, arc);
    }
    //    private void drawDataTooltip(GL10 gl, Point p){
//        if(!containsBlock(p)) return;
//        int idx = getXToDate(p.x);
////        Rect bounds = _cvm.getBounds();
//        int pos = idx-_cvm.getIndex();
//        DrawTool curDT = select_dt;
//
//        if(pos>=0 && pos<_cvm.getViewNum()){
////            String data = curDT.getFormatData(idx);
//            String date = _cdm.getData("자료일자",idx);
//            float width = COMUtil.tf.GetTextLength(date);
//            //int width = gbuf.getFontMetrics().stringWidth(date);
//            int xPixel;
//            double xfactor = _cvm.getDataWidth();
//            if(p.x>xscale.getBounds().right+_cvm.Margin_R)
//                xPixel = (int)(pos*xfactor+xfactor/2)-1+_cvm.Margin_L+(xscale.getBounds().right+_cvm.Margin_R);
//            else{
//                xPixel = (int)(pos*xfactor+xfactor/2)-1+_cvm.Margin_L;
//            }
//            float yPixel = curDT.getYPos(pos);
//            if(!select_block.getBounds().contains(xPixel, (int)yPixel-1)) return;
//
//            if(xPixel+width+10<xscale.getBounds().left+xscale.getBounds().right){
////                int[] x = {xPixel, xPixel+6, xPixel+6};
////                int[] y = {(int)yPixel, (int)yPixel-5, (int)yPixel-12};
//                xPixel += 4; yPixel-=24;
//
//            }else{
////                int[] x = {xPixel, xPixel-6, xPixel-6};
////                int[] y = {(int)yPixel, (int)yPixel-5, (int)yPixel-12};
//                xPixel -= (width+4+10); yPixel-=24;
//
//            }
//
//            _cvm.drawFillRect(gl, xPixel, yPixel, width+10, 26, CoSys.at_col, 1.0f);
//            _cvm.drawFillRect(gl, xPixel, yPixel, width+9, 25, CoSys.at_col, 1.0f);
//
//            _cvm.drawString(gl, CoSys.WHITE,  xPixel+5, (int)yPixel+12, date);
//            _cvm.drawString(gl, CoSys.WHITE,  xPixel+5, (int)yPixel+24, date);
//
//        }
//    }
    //==================================
    // 차트의 스타일 리턴
    //==================================
    public int getStyle(){
        return this.STYLE;
    }
    //==================================
    // 스타일 설정
    //==================================
    public void setStyle(int style){
        STYLE=style;
        if(style!=4)setBackColor(CoSys.CHART_BACK_COLOR[style]);
        switch(style){
            case 0://흰색
                _cvm.setScaleLineType(1);
                xscale.setScaleLineType(1);
                _cvm.setAnalToolColor(new int[]{128,64,64});

                _cvm.setScaleLineColor(new int[]{225,225,225});
                _cvm.setScaleTextColor(CoSys.SCALE_LINE_COLOR);
                _cvm.setBlockBoundLineColor(CoSys.SCALE_LINE_COLOR);
                break;
            case 1://검정색
                _cvm.setScaleLineType(1);
                xscale.setScaleLineType(1);
                _cvm.setAnalToolColor(CoSys.GRAY);
                _cvm.setScaleLineColor(new int[]{57,57,57});
                _cvm.setScaleTextColor(CoSys.GRAY);
                _cvm.setBlockBoundLineColor(CoSys.GRAY);
                break;
            case 2://회색
                _cvm.setScaleLineType(0);
                xscale.setScaleLineType(0);
                _cvm.setAnalToolColor(new int[]{128,64,64});
                _cvm.setScaleLineColor(CoSys.GRAY);
                _cvm.setScaleTextColor(CoSys.DKGRAY);
                _cvm.setBlockBoundLineColor(CoSys.DKGRAY);
                break;
            case 3://줄무늬
                _cvm.setScaleLineType(3);
                xscale.setScaleLineType(1);
                _cvm.setScaleLineColor(CoSys.GRAY);
                _cvm.setScaleTextColor(CoSys.DKGRAY);
                _cvm.setAnalToolColor(new int[]{128,64,64});
                _cvm.setBlockBoundLineColor(CoSys.DKGRAY);
                break;
            case 4://사용자
                xscale.setScaleLineType(1);
                _cvm.setScaleLineColor(new int[]{225,225,225});
                _cvm.setScaleTextColor(CoSys.DKGRAY);
                _cvm.setBlockBoundLineColor(CoSys.DKGRAY);
                break;
        }
    }
    //==================================
    // 가로세로보기 모드 설정
    //==================================
    public void setVerticlaMode(boolean b){
        if(blocks.size()>1){
            _cvm.setVerticalMode(b);
            if(b) setUI2();
            else setUI1();
            repaintAll();
        }
    }
    //    private long getLongTime(int time){
//        int h = time/10000;
//        int m = time/100-h*100;
//        int s = time-h*10000-m*100;
//        Time t = new Time(h, m, s);
//        return t.getTime();
//    }
    public Vector<Block> getBlocks(){
        return blocks;
    }
    public void addChartChangedListener(ChartChangedListener l){
        listeners.addElement(l);
    }
    public void removeChartChangedListener(ChartChangedListener l){
        listeners.removeElement(l);
    }
    protected synchronized void processChartChangeEvent(ChartEvent evt){
        Enumeration<ChartChangedListener> e = listeners.elements();
        while(e.hasMoreElements()){
            ChartChangedListener l = (ChartChangedListener)e.nextElement();
            switch(evt.getChartState()){
                case ChartEvent.CHART_ADD:
                    l.addGraph(evt);
                    break;
                case ChartEvent.CHART_REMOVE:
                    l.removeGraph(evt);
                    break;
                case ChartEvent.CHART_INIT:
                    l.initChart(evt);
                    break;
                case ChartEvent.CHART_TOOLBAR_DONE:
                    l.notifyChartAnalToolDone(evt);
                    break;
            }
        }
    }
    private boolean containsX(float x){
        if(x>=_cvm.getBounds().left+ _cvm.Margin_L && x <=_cvm.getBounds().width()+1)
            return true;
        return false;
    }

    //    private void saveFile(String fn){
//	    PrintWriter pw=null;
//        try {
//            File savefile=new File(fn);
//            pw=new PrintWriter(new FileWriter(savefile));
//
//            int size = _cdm._dataTitles.size();
//            String content="";
//	        for(int i=0; i<size-1; i++){
//	            content = content+ "\""+_cdm._dataTitles.elementAt(i)+"\",";
//	        }
//	        content = content+ "\""+_cdm._dataTitles.lastElement() +"\"";
//	        pw.println(content);
//	        content="";
//
//	        int count = _cdm.getCount();
//	        String[][] datas = new String[count][];
//            for(int i=count-1, j=0; i>=0; i--, j++){
//                datas[j] = _cdm.getAllDatas(i);
//            }
//
//	        for(int i=0; i<datas.length; i++) {
//                for(int j=0; j<datas[i].length-1; j++){
//	                content = content+ "\""+datas[i][j]+"\",";
//	            }
//	            content = content+ "\""+datas[i][datas[i].length-1]+"\"";
//	            pw.println(content);
//	            content="";
//            }
//        }catch(IOException ioe) {
//
//        }finally {
//            if(pw!=null) pw.close();
//        }
//	}
    public int getBlockUnitHeight(int num){
        return (this.getHeight()-(_cvm.XSCALE_H+_cvm.SCROLL_B))/(num+1);
    }
    //===============================
    // 기본차트에 들어갈 타이틀을 셋
    //===============================
    public void setTitles(String[] titles){
        Block block = (Block)getBlocks().elementAt(0);
        Vector<AbstractGraph> v = block.getGraphs();
        for(int i=0, index=0; i<v.size(); i++){
            AbstractGraph graph = (AbstractGraph)v.elementAt(i);
            Vector<DrawTool> gv = graph.getDrawTool();
            for(int j=0; j<gv.size(); j++){
                if(index>=titles.length) break;
                DrawTool dt = (DrawTool)gv.elementAt(j);
                dt.setViewTitle(titles[index++]);
            }
        }
        setTitleBounds();
    }
    //===============================
    // 타이틀 바운드를 재설정
    //===============================
    public void setTitleBounds(){
        Block block = (Block)getBlocks().elementAt(0);
        block.resetTitleBounds();
    }

    public void DataClear(){
        if(_cdm!=null){
            _cdm.clearData();
        }
    }

    public Vector<String> getSubGraphList(){
        Vector<String> listV = new Vector<String>();
        Block block;
        AbstractGraph graph;
        for(int i=0; i<blocks.size(); i++){
            block = (Block)blocks.elementAt(i);
            if(block.isBasicBlock() || block.getBlockType() == Block.STAND_BLOCK) {
                continue;
            }
            for(int j=0; j<block.getGraphs().size(); j++){
                graph = (AbstractGraph)block.getGraphs().elementAt(j);
                listV.addElement(graph.getGraphTitle());
            }
        }
        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                Block bl = (Block)rotate_blocks.get(i);
                if(bl.isBasicBlock() || bl.getBlockType() == Block.STAND_BLOCK) {
                    continue;
                }
                String strTitle = bl.getTitle();
                listV.addElement(strTitle);
            }
        }

        isBlockChanged=false;
        return listV;
    }

    public Vector<String> getGraphList(){
        Vector<String> listV = new Vector<String>();
        Block block;
        AbstractGraph graph;
        if (blocks != null) { //2024.01.04 by sJW - 크래시로그 수정
            for (int i = 0; i < blocks.size(); i++) {
                block = (Block) blocks.elementAt(i);
                for (int j = 0; j < block.getGraphs().size(); j++) {
                    graph = (AbstractGraph) block.getGraphs().elementAt(j);
                    listV.addElement(graph.getGraphTitle());
                }
            }
        }
        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                Block bl = (Block)rotate_blocks.get(i);
                String strTitle = bl.getTitle();
                listV.addElement(strTitle);
            }
        }

        isBlockChanged=false;
        return listV;
    }

    //2021.04.12 by lyk - kakaopay - 차트에 추가된 그래프 tag값을 추출해서 리턴한다. (기존 그래프 이름으로 비교하는 방식에서 tag값 비교 방식으로 사용하기 위함) >>
    public Vector<String> getGraphTagList(){
        Vector<String> graphTagList = new Vector<String>();
        Vector<String> graphList = this.getGraphList();

        Vector<Hashtable<String, String>>  jipyoItems = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu();
        String sTag = "";
        for(int i = 0; i < graphList.size(); i++) {
            String graphName = graphList.get(i);

            for(int k = 0; k < jipyoItems.size(); k++) {
                String cmp = jipyoItems.get(k).get("name");
                sTag = "";
                if(cmp.equals(graphName)) {
                    sTag = jipyoItems.get(k).get("tag");
                    graphTagList.add(sTag);
                    break;
                }
            }
        }

        return graphTagList;
    }
    //2021.04.12 by lyk - kakaopay - 차트에 추가된 그래프 tag값을 추출해서 리턴한다. (기존 그래프 이름으로 비교하는 방식에서 tag값 비교 방식으로 사용하기 위함) <<

    /* 그래프타이틀+설정값으로 구성한다.차트 저장/복원용 */
    public Vector<String> getGraphConfigList() {
        Vector<String> listV = new Vector<String>();
        Block block;
        AbstractGraph graph;
        int blCnt = blocks.size();  //2013. 2. 8 체크안된 상세설정 오픈 : I114
        for(int i=0; i<blocks.size(); i++){
            block = (Block)blocks.elementAt(i);

            //2019. 01. 12 by hyh - 블록병합 처리. 저장 >>
            //Ex. RSI+++10+++CCI+++20+++(14=9=1=14=9=1...=1=)
            if (block.arrMergedGraphTitles != null && block.arrMergedGraphTitles.size() > 1) {
                //저장 전에 내용 갱신
                block.resetMergedGraphs();

                String strTitles = "";
                String strValues = "";

                for (int nIndex = 0; nIndex < block.arrMergedGraphTitles.size(); nIndex++) {
                    strTitles += block.arrMergedGraphTitles.get(nIndex) + MERGED_GRAPH_SEPARATOR;

                    String strGraphValues = block.arrMergedGraphValues.get(nIndex);
                    String[] arrGraphValues = strGraphValues.split("=");

                    strTitles += arrGraphValues.length + MERGED_GRAPH_SEPARATOR;
                    strValues += strGraphValues;
                }

                listV.addElement(strTitles + "{" + strValues + "}");

                continue;
            }
            //2019. 01. 12 by hyh - 블록병합 처리. 저장 <<

            for(int j=0; j<block.getGraphs().size(); j++){
                graph = (AbstractGraph)block.getGraphs().elementAt(j);
                String str;

                //2013. 2. 8 체크안된 상세설정 오픈 : I114
                str = makeGraphConfigStr(graph, true);

                listV.addElement(str);
            }
        }

        //2013. 2. 8 체크안된 상세설정 오픈 : I114
        blCnt = unChkGraphs.size();
        String strGraph = null;
        for(int i = 0; i < blCnt; i++)
        {
            graph = unChkGraphs.get(i);
            strGraph = makeGraphConfigStr(graph, false);
            listV.addElement(strGraph);
        }

        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                Block bl = (Block)rotate_blocks.get(i);
//                String strTitle = bl.getTitle();
                for(int j=0; j<bl.getGraphs().size(); j++){
                    graph = (AbstractGraph)bl.getGraphs().elementAt(j);
                    String str = graph.graphTitle+"{"+getGraphValue2(graph, true)+"}";
                    listV.addElement(str);
                }
            }
        }

        isBlockChanged=false;
        return listV;
    }

    /* 그래프타이틀+설정값으로 구성한다.차트 저장/복원용 */
    public Vector<String> getGraphConfigListOnlyShown() {
        Vector<String> listV = new Vector<String>();
        Block block;
        AbstractGraph graph;
        int blCnt = blocks.size();  //2013. 2. 8 체크안된 상세설정 오픈 : I114
        for(int i=0; i<blocks.size(); i++){
            block = (Block)blocks.elementAt(i);

            //2019. 01. 12 by hyh - 블록병합 처리. 저장 >>
            //Ex. RSI+++10+++CCI+++20+++(14=9=1=14=9=1...=1=)
            if (block.arrMergedGraphTitles != null && block.arrMergedGraphTitles.size() > 1) {
                //저장 전에 내용 갱신
                block.resetMergedGraphs();

                String strTitles = "";
                String strValues = "";

                for (int nIndex = 0; nIndex < block.arrMergedGraphTitles.size(); nIndex++) {
                    strTitles += block.arrMergedGraphTitles.get(nIndex) + MERGED_GRAPH_SEPARATOR;

                    String strGraphValues = block.arrMergedGraphValues.get(nIndex);
                    String[] arrGraphValues = strGraphValues.split("=");

                    strTitles += arrGraphValues.length + MERGED_GRAPH_SEPARATOR;
                    strValues += strGraphValues;
                }

                listV.addElement(strTitles + "{" + strValues + "}");

                continue;
            }
            //2019. 01. 12 by hyh - 블록병합 처리. 저장 <<

            for(int j=0; j<block.getGraphs().size(); j++){
                graph = (AbstractGraph)block.getGraphs().elementAt(j);
                String str;

                //2013. 2. 8 체크안된 상세설정 오픈 : I114
                str = makeGraphConfigStr(graph, true);
                listV.addElement(str);
            }
        }

        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                Block bl = (Block)rotate_blocks.get(i);
//                String strTitle = bl.getTitle();
                for(int j=0; j<bl.getGraphs().size(); j++){
                    graph = (AbstractGraph)bl.getGraphs().elementAt(j);
                    String str = graph.graphTitle+"{"+getGraphValue2(graph, true)+"}";
                    listV.addElement(str);
                }
            }
        }

        isBlockChanged=false;
        return listV;
    }
    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public String makeGraphConfigStr(AbstractGraph graph, boolean checked)
    {
        String str;
        if(graph.graphTitle.equals("일본식봉") || graph.graphTitle.equals("Heikin-Ashi")) {
            DrawTool basic_dt=(DrawTool)graph.getDrawTool().get(0);

            int drawType1 = 0;
            int drawType2 = 0;
            if(m_strCandleType == null)
                m_strCandleType = "캔들";
            if(m_strCandleType.equals("캔들")) {
                drawType1 = 0;
                drawType2 = 0;
            } else if(m_strCandleType.equals(COMUtil.CANDLE_TYPE_CANDLE_TRANSPARENCY)) { //2021.04.05 by lyk - kakaopay - 투명캔들
                drawType1 = 0;
                drawType2 = 9;
            } else if(m_strCandleType.equals("바")) {
                drawType1 = 0;
                drawType2 = 1;
            } else if(m_strCandleType.equals("바(시고저종)")) { //2015.04.30 by lyk - 바(시고저종) 유형 추가
                drawType1 = 0;
                drawType2 = 2;
            } else if(m_strCandleType.equals("종가영역")) { //영역라인
                drawType1 = 0;
                drawType2 = 3;
            } else if(m_strCandleType.equals("FLOW")) { //플로우형
                drawType1 = 0;
                drawType2 = 4;
            } else if(m_strCandleType.equals("Heikin-Ashi")) {
                drawType1 = 0;
                drawType2 = 5;
                //2020.07.06 by LYH >> 캔들볼륨 >>
            }else if(m_strCandleType.equals(COMUtil.CANDLE_TYPE_CANDLE_VOLUME)) {
                drawType1 = 0;
                drawType2 = 6;
            }else if(m_strCandleType.equals(COMUtil.CANDLE_TYPE_EQUI_VOLUME)) {
                drawType1 = 0;
                drawType2 = 7;
                //2020.07.06 by LYH >> 캔들볼륨 <<
            }else if(m_strCandleType.equals(COMUtil.CANDLE_TYPE_STAIR)) {
                drawType1 = 0;
                drawType2 = 8;
            } else {
                drawType1 = basic_dt.getDrawType1();
                drawType2 = basic_dt.getDrawType2();
            }
            //str = graph.graphTitle+"("+drawType1+"="+drawType2+")";

            int components_up[] = basic_dt.getUpColor();
            int components_up2[] = basic_dt.getUpColor2();
            int components_down2[] = basic_dt.getDownColor2();
            int components_down[] = basic_dt.getDownColor();
            str = graph.graphTitle+"{"+drawType1+"="+drawType2+ "=" + components_up[0] + "=" + components_up[1] + "=" + components_up[2] + "="
                    + components_up2[0] + "=" + components_up2[1] + "=" + components_up2[2] + "="
                    + components_down2[0] + "=" + components_down2[1] + "=" + components_down2[2] + "="
                    + components_down[0] + "=" + components_down[1] + "=" + components_down[2] + "="
                    + (basic_dt.isFillUp()?"1":"0") + "=" + (basic_dt.isFillUp2()?"1":"0") + "=" + (basic_dt.isFillDown2()?"1":"0") + "=" + (basic_dt.isFillDown()?"1":"0") + "="
                    + _cvm.getCandle_basePrice() + "=" + _cvm.getCandle_sameColorType() + "="
                    + (_cvm.getIsCandleMinMax()?"1":"0") + "=" + (_cvm.getIsLog()?"1":"0") + "=" + (_cvm.getIsInverse()?"1":"0") + "=" + (_cvm.getIsGapRevision()?"1":"0") +"}";

        } else {
            //2021.09.13 by lyk - kakaopay - 차트 저장/불러오기시 그래프명의 "/" 값을 "!@#" 로 치환 후 사용 (지표명 복원시 "/"가 구분자이기 때문) >>
            String sGraphTitle = graph.graphTitle.replace("/", "!@#");
            str = sGraphTitle+"{"+this.getGraphValue2(graph, checked)+"}";
            //2021.09.13 by lyk - kakaopay - 차트 저장/불러오기시 그래프명의 "/" 값을 "!@#" 로 치환 후 사용 (지표명 복원시 "/"가 구분자이기 때문) <<
        }

        return str;
    }

    public void applyGraphConfigValue(String graphName, int[] values) {
        Block block;
        AbstractGraph graph;
        int blCnt = blocks.size();

        //2019. 01. 12 by hyh - 블록병합 처리. 불러오기 >>
        if (graphName.contains(MERGED_GRAPH_SEPARATOR)){
            Vector<String> arrMergedGraphsTitles = new Vector<String>();
            Vector<String> arrMergedGraphsValuesCount = new Vector<String>();
            Vector<int[]> arrMergedGraphsValues = new Vector<int[]>();

            try {
                //Ex) RSI+++10+++CCI+++20+++
                String[] arrMergedGraphsTitlesWithValuesCount = graphName.split(Pattern.quote(MERGED_GRAPH_SEPARATOR));
                for (int nIndexForTitle = 0; nIndexForTitle < arrMergedGraphsTitlesWithValuesCount.length; nIndexForTitle += 2) {
                    arrMergedGraphsTitles.add(arrMergedGraphsTitlesWithValuesCount[nIndexForTitle]); //Even
                    arrMergedGraphsValuesCount.add(arrMergedGraphsTitlesWithValuesCount[nIndexForTitle + 1]); //Odd
                }

                //Ex) 14=9...=1=14=9...
                int nLocationFrom = 0;
                int nLocationTo = 0;
                for (int nIndexForValueArray = 0; nIndexForValueArray < arrMergedGraphsValuesCount.size(); nIndexForValueArray++) {
                    nLocationFrom = nLocationTo;

                    try {
                        nLocationTo += Integer.parseInt(arrMergedGraphsValuesCount.get(nIndexForValueArray));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (nLocationFrom < nLocationTo) {
                        int[] graphValues = new int[nLocationTo - nLocationFrom];
                        int nIndexForGraphValues = 0;

                        for (int nIndexForValues = nLocationFrom; nIndexForValues < nLocationTo; nIndexForValues++) {
                            try {
                                graphValues[nIndexForGraphValues++] = values[nIndexForValues];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        arrMergedGraphsValues.add(graphValues);
                    }
                }

                //블록 탐색
                int nBlockIndex = -1;
                for (int nIndex = 0; nIndex < blCnt; nIndex++) {
                    block = (Block) blocks.get(nIndex);
                    graph = block.getGraphs().firstElement();

                    //중복지표 병합처리
                    if (graph.graphTitle != null) {
                        if (graph.graphTitle.equals(arrMergedGraphsTitles.firstElement())) {
                            nBlockIndex = nIndex;
                            break;
                        }
                    }
                    else if (graph.getName().equals(arrMergedGraphsTitles.firstElement())) {
                        nBlockIndex = nIndex;
                        break;
                    }
                }

                if (nBlockIndex < 0) {
                    return;
                }

                block = (Block) blocks.get(nBlockIndex);
                RectF graph_bounds = block.getGraphBounds();

                //그래프 추가
                for (int nIndexForGraphs = 1; nIndexForGraphs < arrMergedGraphsTitles.size(); nIndexForGraphs++) {
                    block.add(arrMergedGraphsTitles.elementAt(nIndexForGraphs));
                }

                //속성 적용
                for (int nIndexForGraphs = 0; nIndexForGraphs < block.getGraphs().size(); nIndexForGraphs++) {
                    graph = (AbstractGraph) block.getGraphs().get(nIndexForGraphs);

                    if (arrMergedGraphsValues.size() > nIndexForGraphs) {
                        graph.changeControlValue(arrMergedGraphsValues.get(nIndexForGraphs));
                    }

                    graph.setBounds(graph_bounds.left, graph_bounds.top, graph_bounds.right, graph_bounds.bottom);
                }

                block.resetMergedGraphs();
                block.makeGraphData();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //2019. 01. 12 by hyh - 블록병합 처리. 불러오기 <<
        else
        {
            for (int i = 0; i < blCnt; i++) {
                block = (Block) blocks.get(i);
                int blGraphCnt = block.getGraphs().size();
                for (int j = 0; j < blGraphCnt; j++) {
                    graph = (AbstractGraph) block.getGraphs().get(j);

                    //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리
                    String strGraphTitle = graph.getName();
                    if (graph.getGraphTitle() != null && graph.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                        strGraphTitle = graph.getGraphTitle();
                    }

                    if (graphName.equals(strGraphTitle))
                    //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리 end
                    {
                        graph.changeControlValue(values);
                    }
                }
            }
        }

        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++) {
                block = (Block)rotate_blocks.get(i);
                int blGraphCnt = block.getGraphs().size();
                for(int j=0; j<blGraphCnt; j++) {
                    graph = (AbstractGraph)block.getGraphs().get(j);

                    //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리
                    String strGraphTitle = graph.getName();
                    if(graph.getGraphTitle()!=null && graph.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                        strGraphTitle = graph.getGraphTitle();
                    }

                    if(graphName.equals(strGraphTitle))
                    //2015. 1. 13 by lyk - 동일지표인 경우 타이틀 처리 end
                    {
                        graph.changeControlValue(values);
                    }
                }
            }
        }
        if(basic_block != null) {
            for(int j=0; j<basic_block.getGraphs().size(); j++) {
                graph = (AbstractGraph)basic_block.getGraphs().get(j);
                if(graphName.equals(graph.graphTitle)) {
                    graph.changeControlValue(values);
                }
            }
        }
    }
    public String getGraphValue(int[] data) {
        String rtnStr = "";
        if(data==null) return rtnStr;

        for(int i=0; i<data.length; i++) {
            rtnStr += ""+data[i]+"=";
        }
        return rtnStr;
    }

//	public String getGraphValue1(AbstractGraph graph) {
//		String rtnStr = "";
//		if(graph.interval == null)
//		{
//			if(graph.graphTitle.equals("거래량"))
//			{
//				DrawTool dt=graph.getDrawTool().get(0);
//				int components_up[] = dt.getUpColor();
//				int components_down[] = dt.getDownColor();
//				int components_same[] = dt.getSameColor();
//				rtnStr += _cvm.getVolDrawType() + "=" + components_up[0] + "=" + components_up[1] + "=" + components_up[2] + "=" + components_down[0] + "=" + components_down[1] + "=" + components_down[2] + "=" + components_same[0] + "=" + components_same[1] + "=" + components_same[2] + "=";
//			}
//			return rtnStr;
//		}
//		for(int i=0; i<  graph.interval.length; i++) {
//			rtnStr += ""+graph.interval[i]+"=";
//		}
//
//		if(graph.graphTitle.equals("대기매물"))
//		{
//			DrawTool dt=graph.getDrawTool().get(0);
//
//			int components[] = dt.getUpColor();
//			rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
//			components = dt.getSameColor();
//			rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
//			components = dt.getDownColor();
//			rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
//		}
//		else
//		{
//			for(int i=0; i<  graph.getDrawTool().size(); i++) {
//				DrawTool dt=graph.getDrawTool().get(i);
//				int components[] = dt.getUpColor();
//				rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
//			}
//		}
////        System.out.println(rtnStr);
//		return rtnStr;
//	}

    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public String getGraphValue2(AbstractGraph graph, boolean checked)
    {
        String rtnStr = "";

        if(graph.interval != null)
        {
            for(int i=0; i<  graph.interval.length; i++)
            {
                rtnStr += String.format("%d=", graph.interval[i]);
            }
        }

        for(int i=0; i< graph.getDrawTool().size(); i++) {
            DrawTool dt=graph.getDrawTool().get(i);
            if(graph.graphTitle.equals("거래량"))
            {
                final int[] components_up = dt.getUpColor();
                final int[] components_down = dt.getDownColor();
                final int[] components_same = dt.getSameColor();

                rtnStr += String.format("%d=%d=%d=%d=%d=%d=%d=%d=%d=%d=",
                        _cvm.getVolDrawType(),
                        components_up[0],components_up[1],components_up[2],
                        components_down[0],components_down[1],components_down[2],
                        components_same[0],components_same[1],components_same[2]);

            }
            else if(graph.graphTitle.equals("매물대"))
            {
                int components[] = dt.getUpColor();
                rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
                components = dt.getSameColor();
                rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";
                components = dt.getDownColor();
                rtnStr += dt.getLineT() + "=" + components[0] + "=" + components[1] + "=" + components[2] + "=" + (dt.isVisible()?"1":"0") + "=";

                //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
                rtnStr += (dt.isStandScaleLabelShow()?"1=":"0=");
                //2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
            }
            //2017.05.11 by LYH >> 전략(신호, 강약) 추가
            else if(dt instanceof SignalDraw)
            {
                final int[] components = dt.getUpColor();
                final int[] components_down = dt.getDownColor();
                int nIsVisible = dt.isUpVisible() ? 1 : 0;
                rtnStr += String.format("%d=%d=%d=%d=%d=%d=",(int)dt.getLineT(),components[0],components[1],components[2],nIsVisible, (int)dt.getDrawType2());

                nIsVisible = dt.isDownVisible() ? 1 : 0;
                rtnStr += String.format("%d=%d=%d=%d=%d=",(int)dt.getLineT(),components_down[0],components_down[1],components_down[2],nIsVisible);
            }
            //2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
            else
            {
                final int[] components = dt.getUpColor();
                int nIsVisible = dt.isVisible() ? 1 : 0;
                //2015. 1. 13 보조지표 bar 타입 유형 변경 기능>> : 각 drawtool 값 저장하는 마지막에 drawtype2 추가		for test
//              rtnStr += String.format("%d=%d=%d=%d=%d=",(int)dt.getLineT(),components[0],components[1],components[2],nIsVisible);
                rtnStr += String.format("%d=%d=%d=%d=%d=%d=",(int)dt.getLineT(),components[0],components[1],components[2],nIsVisible, (int)dt.getDrawType2());
                //2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
            }
        }

        //2013. 9. 3 지표마다 기준선 설정 추가>> : 저장될 문자열에 기준선정보(체크, 기준선값) 추가.
        if( graph.getBaseValue() != null )
        {
            //기준선이 있는 지표면 기준선 정보 추가 저장.
            for(int i = 0; i < graph.getBaseValue().length; i++)
            {
                //체크값
                rtnStr += (graph.isBaseLineVisiility(i) ? "1=" : "0=");

                //기준선값
                rtnStr += String.valueOf(graph.getBaseValue()[i])+"=";
            }

            rtnStr += (graph.isSellingSignalShow() ? "1=" : "0=");	//2014. 9. 11 매매 신호 보기 기능 추가
        }
        //2013. 9. 3 지표마다 기준선 설정 추가>>

        //2016.07.28 by LYH >> 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산
        if(graph.graphTitle.indexOf("주가이동평균")!=-1)
        {
            for(int i=0; i< graph.getDrawTool().size(); i++) {
                DrawTool dt=graph.getDrawTool().get(i);
                rtnStr += dt.getDataType() + "=" + dt.getAverageCalcType() + "=";
            }
        }
        //2016.07.28 by LYH << 데이터기준, 이평선타입별(단순,가중,지수,기하) 계산

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
        if(graph.getGraphTitle().equals("Bollinger Band")
                || graph.getGraphTitle().startsWith("Bollinger Band [보조]")
                || graph.getGraphTitle().startsWith("Band %B")
                || graph.getGraphTitle().equals("Band Width")
                || graph.getGraphTitle().startsWith("%B"))
        {
            rtnStr += graph.dataTypeBollingerband + "=";
            rtnStr += graph.calcTypeBollingerband + "=";
        }
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

        //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//		if(graph.graphTitle.indexOf("주가이동평균")!=-1)
//		{
//			for(int i=0; i< graph.getDrawTool().size(); i++) {
//				rtnStr += graph.dataTypeAverage + "=" + graph.calcTypeAverage + "=";
//			}
//		}
        if(graph.graphTitle.indexOf("주가이동평균")!=-1)
        {
            for(int i=0; i< graph.getDrawTool().size(); i++) {
                DrawTool dt=graph.getDrawTool().get(i);
                rtnStr += dt.getDataType() + "=" + dt.getAverageCalcType() + "=";
            }
        }
        //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

        //2021.05.24 by hhk - 구름대 채우기 >>
        if(graph.graphTitle.equals("일목균형표"))
        {
            //2023.11.28 by SJW - 일목균형표 구름대 채우게 설정 >>
//			rtnStr += (graph.isFillCloud() ? "1=" : "0=");	//2021. 5. 14 구름대 채우기
            rtnStr += "1=";	//2021. 5. 14 구름대 채우기
            //2023.11.28 by SJW - 일목균형표 구름대 채우게 설정 <<
        }
        //2021.05.24 by hhk - 구름대 채우기 <<

        int nChecked = checked?1:0;
        rtnStr += String.format("%d=", nChecked);
        return rtnStr;
    }

    //2013. 2. 8 체크안된 상세설정 오픈 : I114
    public void setGraph(AbstractGraph nGraph, String str)
    {
        Block block;
        AbstractGraph oGraph;
        int blCnt = blocks.size();
        for(int i=0; i<blCnt; i++) {
            block=blocks.get(i);
            int blGraphCnt = block.getGraphs().size();
            for(int j=0; j<blGraphCnt; j++) {
                oGraph=block.getGraphs().get(j);
                if(oGraph.equals(str)) {
                    block.getGraphs().remove(j);
                    block.getGraphs().add(j, nGraph);
                }
            }
        }
    }

    public AbstractGraph getGraph(String str){
        Block block;
        AbstractGraph graph;
        for(int i=0; i<blocks.size(); i++){
            block = (Block)blocks.elementAt(i);
            for(int j=0; j<block.getGraphs().size(); j++){
                graph = (AbstractGraph)block.getGraphs().elementAt(j);
                if(graph.getGraphTitle().equals(str))
                    return graph;
            }
        }

        if(rotate_blocks != null) {
            for(int i=0; i<rotate_blocks.size(); i++){
                block = (Block)rotate_blocks.get(i);
                int blGraphCnt = block.getGraphs().size();
                for(int j=0; j<blGraphCnt; j++) {
                    graph = (AbstractGraph)block.getGraphs().get(j);
                    if(graph.graphTitle.equals(str)) {
                        return graph;
                    }
                }
            }
        }

        return null;
    }

    public int getYPos(){
        return basic_block.getYScalePos();
    }

    public void RemoveStandGraph(){
        if(_cvm.isStandGraph()){
            Block stand=getChartBlockByType(Block.STAND_BLOCK);
            if(stand==null) return;
//	        if(jipyo!=null && jipyo.isVisible()) jipyo.deselectCheckBox(stand.getTitle());
            //if(stand.getTitle().equals("대기매물")){
            //    removeBlock((Block)blocks.lastElement());
            //}
            removeBlock(stand);
            if(_cvm.isStandGraph())setScrollBar(NOTONLY_DATACHANGE);
            _cvm.setStandGraph(false);
        }
    }

    public void removeBlock(Block rmBlock){
        if(rmBlock==null) return;
        int removeIndex = rmBlock.getIndex();
        if(rmBlock.isBasicBlock()){
            return;
        }else{
//            boolean xscale = false;
//            if(rmBlock.getXScale()!=null) xscale = true;

            rmBlock.destroy();
            blocks.removeElementAt(removeIndex);
//            if(xscale){
////                Block last = (Block)blocks.lastElement();
////                last.setXScale(_cvm.getXScale());
//            }
        }

        for(int i=removeIndex; i<blocks.size(); i++){
            Block block = (Block)blocks.elementAt(i);
            block.setIndex(block.getIndex()-1);
        }
        reSetUI(true);
    }

    /* 차트 확대시 delButton의 visible 처리 (2011.11.02 by lyk) */
    public void setVisibleBlockDelButton(boolean state){
        for(int i=0; i<blocks.size(); i++) {
            Block block = (Block)blocks.get(i);
            block.setHideDelButton(!state);
            //2014. 6. 3 분할차트에서 차트확대/축소시 봉갯수설정 텍스트뷰가 남아있음>>
            block.setHideViewNumTextView(!state);
            block.setHideChangeBlockButton(!state);
            //2014. 6. 3 분할차트에서 차트확대/축소시 봉갯수설정 텍스트뷰가 남아있음<<
        }
    }

    //===========================================
    // 실시간 인터페이스 구현
    //===========================================
    public void regRT(){
        //이전종목 실시간 해제
        deregRT();

        resetUseRealData();

        RTHandler rtHandler = COMUtil.getRTHandler();
        Vector<ChartRealPacketDataModel> realInfo = _cdm.getRealPacketInfo();
        if(realInfo==null)return;
        int realInfoLen = realInfo.size();
        for(int i=0;i<realInfoLen;i++){
            ChartRealPacketDataModel ele = (ChartRealPacketDataModel)realInfo.elementAt(i);
//            String[] key = ele.getPacketKey();//패킷데이터 키를 구한다 ex: 코드
            String[] field = ele.getFieldTitle();// 실시간 필드의 title을 구한다  ex: price, high
            String dataFile = ele.getPacketTitle();//실시간 파일을 구한다 ex: S31
            String dataKey = _cdm.codeItem.strCode;
            //field는 날짜,시,고,저,종,거래량의 데이터이다.
            int[] infos1 = new int[6];
            int[] infos2 = new int[6];
            if(dataFile.equals("S31")) {
                //데이터 타입에 따라, 거래량 타입이 설정됨.(일:누적거래량, )
                int startVal = 0;
                int endVal = 0;
                if(_cdm.getDataType()==1 || _cdm.getDataType()==2 || _cdm.getDataType()==3) { //일,주,월
                    startVal = 92;//날짜,시,고,저,종,누적거래량
                    endVal = 12;
                } else {
                    startVal = 104;//날짜,시,고,저,종,변동거래량
                    endVal = 8;
                }
                //날짜,시,고,저,종,거래량
                infos1[0]=17;
                infos1[1]=47;
                infos1[2]=47+9;
                infos1[3]=47+9*2;
                infos1[4]=33;
                infos1[5]=startVal;

                infos2[0]=6;
                infos2[1]=9;
                infos2[2]=9;
                infos2[3]=9;
                infos2[4]=9;
                infos2[5]=endVal;

//	            infos1 = {17,47,47+9,47+9*2,33,startVal};//날짜,시,고,저,종,거래량
//	            int[] infos2 = {6,9,9,9,9,endVal};
            } else if(dataFile.equals("SC0")) {
                //데이터 타입에 따라, 거래량 타입이 설정됨.(일:누적거래량, )
                int startVal = 0;
                int endVal = 0;
                if(_cdm.getDataType()==1 || _cdm.getDataType()==2 || _cdm.getDataType()==3) { //일,주,월
                    startVal = 34+7*4;//날짜,시,고,저,종,누적거래량
                    endVal = 7;
                } else {
                    startVal = 34+7*5+11+1+7+7;//날짜,시,고,저,종,변동거래량
                    endVal = 6;
                }
                //날짜,시,고,저,종,거래량
                infos1[0]=8+13;
                infos1[1]=34;
                infos1[2]=34+7;
                infos1[3]=34+7*2;
                infos1[4]=27;
                infos1[5]=startVal;

                infos2[0]=6;
                infos2[1]=7;
                infos2[2]=7;
                infos2[3]=7;
                infos2[4]=7;
                infos2[5]=endVal;
//            } else if(dataFile.equals("JS0")) {
//
//	            //날짜,시,고,저,종,거래량
//	            infos1[0]=8+7;
//	            infos1[1]=0;
//	            infos1[2]=0;
//	            infos1[3]=0;
//	            infos1[4]=infos1[0]+6+1;
//
//	            //데이터 타입에 따라, 거래량 타입이 설정됨.(일:누적거래량, )
//	            int startVal = 0;
//	            int endVal = 0;
//	            if(_cdm.getDataType()==1 || _cdm.getDataType()==2 || _cdm.getDataType()==3) { //일,주,월
//	            	startVal = infos1[4]+9+1+9;//날짜,시,고,저,종,누적거래량
//	                endVal = 8;
//	            } else {
//	            	startVal = infos1[4]+9+1+9+8+8+8+6;//날짜,시,고,저,종,변동거래량
//	                endVal = 8;
//	            }
//
//	            infos1[5]=startVal;
//
//	            infos2[0]=6;
//	            infos2[1]=0;
//	            infos2[2]=0;
//	            infos2[3]=0;
//	            infos2[4]=9;
//	            infos2[5]=endVal;
            }
            for(int j=0;j<field.length;j++){
                int[] packetInfo = {infos1[j],infos2[j],0,j};
                if((rtHandler!=null)&&(dataKey!=null)){
                    rtHandler.registeRT(dataFile,dataKey,this,packetInfo);
                    //rtHandler.setChartDataModel(_cdm);
                    if(reqRV==null) reqRV = new Vector<String>();
                    if(!reqRV.contains(dataFile+dataKey)) reqRV.addElement(dataFile+dataKey);
                }
            }
        }
    }
    public void deregRT(){
        if(reqRV==null) return;
        if(reqRV.isEmpty()) return;

        RTHandler rtHandler = COMUtil.getRTHandler();

        if(rtHandler!=null) {
            rtHandler.removeRT(reqRV, this);
            reqRV.removeAllElements();
        }
    }
    public void addData(boolean b){
    }

    /* API 호출 함수 : 실시간 데이터 처리 */
    public void setRealData(byte[] _data, boolean bCheckCode) {

        if(_data==null || _data.length<3) return;

        byte[] data = new byte[_data.length];	//size(4)포함
        System.arraycopy(_data, 0, data, 0, _data.length);

        //data : 00010237S310059300909125000012000000828000-1.4300082600000
        //String realStr = new String(data, 0, data.length);
        String cntStr = new String(data, 0, 4);
        int cnt = Integer.parseInt(cntStr);

        int nOffset = 4; //count 자리수(4)
        int nSizeFix = 4;
        int nPacketSize=0;
        String strPacketSize;
        String sSizeFixData = String.format("0001");

        for(int k=0; k<cnt; k++) {
            strPacketSize = new String(data, nOffset, 4);
            nPacketSize = Integer.parseInt(strPacketSize) + 4;	//사이즈정보(4) 포함.

            byte[] packetItem = new byte[nPacketSize+nSizeFix];	//size(4)포함
            System.arraycopy(sSizeFixData.getBytes(), 0, packetItem, 0, nSizeFix);
            System.arraycopy(data, nOffset, packetItem, nSizeFix, nPacketSize);
//  	    	packetItem[nPacketSize+nSizeFix] = 0x00;

            int localnOffset = 4+4;

            String realName = new String(packetItem, localnOffset, 3);
//  	  		nOffset += 3;

//  	  	    int index = threadName.indexOf(realName+ "*");
//  	  	    index = (int)((index+1) / 4);
//  	  	    int dataKeyLen = getDataKeyLen(realName);
//
//  	  	    if (index != -1  && dataKeyLen != -1) {
//  	  	        String datakey = new String(packetItem, nOffset, dataKeyLen);
//  	  	        if(subRT[index]== null) {
//  	  	        	realdataToDRDSManager(realName, datakey, packetItem, nDataSize);
//  	  	        	return;
//  	  	        }
//  	  	        subRT[index].setData(packetItem, realName, datakey);
//  	  	    }
            if(realName.equals("S31")) { //주식
                realName = new String(packetItem, localnOffset, 35);
                realName= realName.replace("@", "#");	//2017.10.17 by pjm 실시간 코드 @ -> # 수정.
            } else if(realName.equals("SC0")) { //선물
                realName = new String(packetItem, localnOffset, 35);
            } else if(realName.equals("SC3")) { //주요지수
                realName = new String(packetItem, localnOffset, 18);
            }
            realName = realName.trim();
            //if(_cvm.chartType==COMUtil.COMPARE_CHART || realName.substring(3).equals(_cdm.codeItem.strCode)) {
            //2014.04.17 by LYH >> 연결선물 실시간 처리. <<
            if(/*!bCheckCode || */_cvm.chartType==COMUtil.COMPARE_CHART || realName.substring(3).equals(_cdm.codeItem.strCode) || realName.substring(3).equals(_cdm.codeItem.strRealCode) || _cdm.codeItem.strCode.equals("000000") ) {
                if(_cdm.codeItem.strCode!=null && _cdm.codeItem.strCode.length()>0) {
                    this.repaintRT(realName.substring(0, 3), packetItem);
                    if (_cvm.chartType == COMUtil.COMPARE_CHART)
                        repaintAll();
                }
            }
            //}

            nOffset += (nPacketSize);
        }
    }

    public void repaintRT(String mstVal, byte[] data){
        synchronized (this) {
            _cdm.setRealAddType(_cdm.getDataType());
            String dateVal="", openVal="", highVal="", lowVal="", priceVal="", volumeResult = "", signVal="", changeVal="", movolumeVal="";
            String strCodeType;  //2016.01.05 by LYH >> 분차트 30초 보정 처리
            String value="", bidVolume="";	//2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
            Hashtable<String, String> itemsReal = trData.makeTrData(mstVal, data);
            String chgrate = itemsReal.get(OutputPacket.CHGRATE);
            itemsReal.put(OutputPacket.CHGRATE, chgrate+"%");

//        	if(mstVal.equals("JS0") || mstVal.equals(COMUtil.TR_COMPARE_UPJONG)) {
//	        	 dateVal = itemsReal.get(OutputPacket.TIME);
//	        	 priceVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.PRICE));
//	        	 volumeResult = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.VOLUME));
//	        	 signVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.SIGN));
//	        	 changeVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.CHANGE));
//	        	 movolumeVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.CVOLUME));
//        	} else {
            dateVal = itemsReal.get(OutputPacket.TIME);
            openVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.OPEN));
            highVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.HIGH));
            lowVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.LOW));
            priceVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.PRICE));
            volumeResult = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.VOLUME));
            signVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.SIGN));
            changeVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.CHANGE));
            movolumeVal = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.CVOLUME));

            strCodeType = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.SEQ));

            //2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
            value = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.VALUE));
            bidVolume = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.BIDVOL));
            //2016.01.20 by LYH << 거래대금, 매수, 매도거래량 실시간
            //}
            String sessionId = COMUtil.removeFrontZero(itemsReal.get(OutputPacket.SEQ)); //2023.03.15 by SJW - 애프터마켓 추가
            priceVal = priceVal.trim();
//			if(priceVal.startsWith("-") || priceVal.startsWith("+"))
//				priceVal = priceVal.substring(1);

            changeVal = changeVal.trim();
//			if(changeVal.startsWith("-") || changeVal.startsWith("+"))
//				changeVal = changeVal.substring(1);

            //2016.01.05 by LYH >> 분차트 30초 보정 처리
            if(strCodeType.equals("99")) //30초 보정
            {
                //2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
                if(value==null || value.length()<1)
                    value = "0";
                String askVolume = "0";
                try{
                    askVolume = String.format("%.0f",Double.parseDouble(volumeResult)-Double.parseDouble(bidVolume));
                }catch(Exception e){}
                String[] datas = {dateVal, openVal, highVal, lowVal, priceVal, volumeResult, value, bidVolume, askVolume};
                //String[] datas = {dateVal, openVal, highVal, lowVal, priceVal, volumeResult};
                //2016.01.20 by LYH << 거래대금, 매수, 매도거래량 실시간
                _cdm.setRealData(datas);
                return;
            }
            //2016.01.05 by LYH << 분차트 30초 보정 처리

            try {
                //if(!_cdm.codeItem.strMarket.equals("6") && Integer.parseInt(movolumeVal) == 0)	//메리츠 해외지수 거래량,체결량 모두 0 처리
                if(Integer.parseInt(movolumeVal) == 0)	//메리츠 해외지수 거래량,체결량 모두 0 처리
                {
                    if( Double.parseDouble(volumeResult)!=0 && Double.parseDouble(_cdm.codeItem.strVolume) == Double.parseDouble(volumeResult))
                        return;

                    movolumeVal = String.format("%.0f", Double.parseDouble(volumeResult) - Double.parseDouble(_cdm.codeItem.strVolume));
                }
            }catch(Exception e){}

            if( _cdm.getDateType() == ChartDataModel.DATA_WEEK|| _cdm.getDateType() == ChartDataModel.DATA_MONTH|| _cdm.getDateType() == ChartDataModel.DATA_YEAR)
            {
                if(_cdm.codeItem.dLastVol < 0)
                {
                    String strLastVol = _cdm.getLastStringData("기본거래량");
                    double dChartVol = 0;
                    if(strLastVol != null)
                    {
                        try {
                            dChartVol = Double.parseDouble(strLastVol);
                            _cdm.codeItem.dLastVol = dChartVol - Double.parseDouble(_cdm.codeItem.strVolume);
                        }catch(Exception e){}

                    }
                }
            }
//			//2014.04.08 by LYH >> 8,32진법처리.
//			if(_cdm.nDispScale != 10)
//			{
//
//				try
//				{
//					double dPrice = Double.parseDouble(priceVal);
//					dPrice = getScalePrice(dPrice, _cdm.nDispScale);
//					priceVal = String.format("%s", dPrice);
//				}catch(Exception e){}
//				try
//				{
//					double dPrice = Double.parseDouble(changeVal);
//					dPrice = getScalePrice(dPrice, _cdm.nDispScale);
//					changeVal = String.format("%s", dPrice);
//				}catch(Exception e){}
//
//				try
//				{
//					double dPrice = Double.parseDouble(openVal);
//					dPrice = getScalePrice(dPrice, _cdm.nDispScale);
//					openVal = String.format("%s", dPrice);
//				}catch(Exception e){}
//				try
//				{
//					double dPrice = Double.parseDouble(highVal);
//					dPrice = getScalePrice(dPrice, _cdm.nDispScale);
//					highVal = String.format("%s", dPrice);
//				}catch(Exception e){}
//				try
//				{
//					double dPrice = Double.parseDouble(lowVal);
//					dPrice = getScalePrice(dPrice, _cdm.nDispScale);
//					lowVal = String.format("%s", dPrice);
//				}catch(Exception e){}
//
//			}
//			//2014.04.08 by LYH << 8,32진법처리.

            _cdm.codeItem.strPrice = priceVal;
            _cdm.codeItem.strSign = signVal;
            //_cdm.codeItem.strChange = COMUtil.format(changeVal, 0, 3);
            _cdm.codeItem.strChange = changeVal;
            //_cdm.codeItem.strChange = ChartUtil.getFormatedData(changeVal, _cdm.getPriceFormat());
            _cdm.codeItem.strChgrate = COMUtil.format(chgrate, 2, 3)+"%";
//        	try
//        	{
//        		 if(Integer.parseInt(volumeResult) == Integer.parseInt(movolumeVal))
//        		 {
//        			 _cdm.codeItem.strMoveVolume = ""+(Integer.parseInt(movolumeVal) - Integer.parseInt(_cdm.codeItem.strVolume));
//        			 _cdm.codeItem.strVolume = volumeResult;
//
//        		 }
//        		 else
//        		 {
            _cdm.codeItem.strVolume = volumeResult;
            _cdm.codeItem.strMoveVolume = movolumeVal;
            //       		 }
//        	}
//        	catch(Exception e)
//        	{
//        		_cdm.codeItem.strVolume = volumeResult;
//        		_cdm.codeItem.strMoveVolume = movolumeVal;
//        	}

            String askVolume = "0";
            String moveValue = "0";
            if(bidVolume!=null && bidVolume.length()>0 &&Integer.parseInt(bidVolume)==0)
                askVolume = movolumeVal;
//	        try{
            if(mstVal.equals("S31") || mstVal.equals("SC0") || mstVal.equals("JS0") || mstVal.equals("SC3")) {
                if(_cdm.getDateType() == ChartDataModel.DATA_MIN || _cdm.getDateType() == ChartDataModel.DATA_TIC||  _cdm.getDateType() == ChartDataModel.DATA_SECOND
                        ||_cdm.getDateType() == ChartDataModel.DATA_WEEK|| _cdm.getDateType() == ChartDataModel.DATA_MONTH|| _cdm.getDateType() == ChartDataModel.DATA_YEAR)

                {
                    //if(_cdm.getDateType()==4 || _cdm.getDateType()==5 || _cdm.getDateType()==2 || _cdm.getDateType()==3) {
                    if(_cdm.getDateType() == ChartDataModel.DATA_WEEK|| _cdm.getDateType() == ChartDataModel.DATA_MONTH|| _cdm.getDateType() == ChartDataModel.DATA_YEAR) {
                        volumeResult = itemsReal.get(OutputPacket.VOLUME);
                        volumeResult = String.format("%.0f", _cdm.codeItem.dLastVol + Double.parseDouble(volumeResult));
                    }
                    else
                        volumeResult = _cdm.codeItem.strMoveVolume;

                    //2019. 12. 26 by hyh - 미결제약정 실시간 적용. 체결량 방식 제거 >>
                    moveValue = value;
                    _cdm.codeItem.strValue = value;

//					//2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
//					if(_cdm.codeItem.strValue == null || _cdm.codeItem.strValue.length() == 0 || Double.parseDouble(_cdm.codeItem.strValue) ==0 )
//						_cdm.codeItem.strValue = value;
//					try {
//						moveValue = String.format("%d", Integer.parseInt(value)-Integer.parseInt(_cdm.codeItem.strValue));
//					}catch(Exception e){}
//					_cdm.codeItem.strValue = value;
//					//2016.01.20 by LYH << 거래대금, 매수, 매도거래량 실시간
                    //2019. 12. 26 by hyh - 미결제약정 실시간 적용. 체결량 방식 제거 <<
                } else {
                    volumeResult = itemsReal.get(OutputPacket.VOLUME);

                    //2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
                    moveValue = value;
                    String strLastMesu = _cdm.getLastStringData("매수거래량");
                    String strLastMedo = _cdm.getLastStringData("매도거래량");

                    try {
                        bidVolume = String.format("%d", Integer.parseInt(strLastMesu) + Integer.parseInt(bidVolume));
                        askVolume = String.format("%d", Integer.parseInt(strLastMedo) + Integer.parseInt(askVolume));
                    }catch(Exception e){}
                    //2016.01.20 by LYH << 거래대금, 매수, 매도거래량 실시간
                }
            }

            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
            double dMarketValue = 0;
            if (_cvm.nFxMarginType >= 0) {
                if (_cvm.nFxMarginType == ChartViewModel.FX_BUY) {
                    priceVal = highVal;
                }
                else if (_cvm.nFxMarginType == ChartViewModel.FX_AVERAGE) {
                    priceVal = lowVal;
                }
                else {
                    priceVal = openVal;
                }

                //2019. 08. 21 by hyh - FX마진차트 기준선 처리 >>
                _cdm.codeItem.strPrice = priceVal;    //현재가

                ////2014.03.28 by LYH >> FX마진차트 시세바 처리(매수매도 이자 추가)
                //_cdm.codeItem.strGiOpen = openVal;    //매도가
                //_cdm.codeItem.strGiHigh = highVal;    //매수가
                //_cdm.codeItem.strPrice = priceVal;    //현재가
                ////2014.03.28 by LYH << FX마진차트 시세바 처리(매수매도 이자 추가)
                //2019. 08. 21 by hyh - FX마진차트 기준선 처리 <<

                try {
                    dMarketValue = Double.parseDouble(highVal);
                } catch (Exception e) {
                }
                highVal = "";
                lowVal = "";
                openVal = "";
                volumeResult = "0";
            }
            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

            //2016.01.20 by LYH >> 거래대금, 매수, 매도거래량 실시간
            //String[] datas = {dateVal, openVal, highVal, lowVal, priceVal, volumeResult};
            //2023.03.15 by SJW - 애프터마켓 추가 >>
//			String[] datas = {dateVal, openVal, highVal, lowVal, priceVal, volumeResult, moveValue, bidVolume, askVolume};
            String[] datas = {dateVal, openVal, highVal, lowVal, priceVal, volumeResult, moveValue, bidVolume, askVolume, sessionId};
            //2023.03.15 by SJW - 애프터마켓 추가 <<
            //2016.01.20 by LYH << 거래대금, 매수, 매도거래량 실시간
            if(_cvm.chartType==COMUtil.COMPARE_CHART) {
                boolean bAdd = _cdm.addCompareData("자료일자", dateVal);
                String strCode = itemsReal.get(OutputPacket.CODE).trim();
                if(bAdd)
                {

                    ChartPacketDataModel cpdm =_cdm.getChartPacket(strCode);
                    if(cpdm != null)
                    {
                        int nCount = _cdm.getCount() - cpdm.getDataCount();
                        if(nCount>0)
                        {
                            for(int i=0; i< nCount; i++)
                            {
                                cpdm.addRealData(priceVal);
                            }
                            //비교차트 분차트 타종목 카운트 하나씩 증가시켜 줌 >>
                            for(int i=0; i<arrCodes.size(); i++)
                            {
                                if(!arrCodes.get(i).equals(strCode)) {
                                    cpdm = _cdm.getChartPacket(arrCodes.get(i));
                                    cpdm.addRealData(cpdm.getLastStringData());
                                }
                            }
                            //비교차트 분차트 타종목 카운트 하나씩 증가시켜 줌 <<
                            setScrollBar(this.ONLY_DATACHANGE);
                        }
                        else
                        {
                            _cdm.addData(strCode, priceVal);
                        }
                    }
                }
                else
                {
                    _cdm.addData("자료일자", dateVal);
                    _cdm.addData(strCode, priceVal);
                }
                makeGraphData();
            } else {
                _cdm.addData(datas,0,4);
                //2023.02.13 by SJW - special chart - 틱 차트에서 x축 시간 0으로 나오는 현상 수정 >>
                if(isSpecialDraw()) {
                    setScrollBar(DATA_END);
                }
                //2023.02.13 by SJW - special chart - 틱 차트에서 x축 시간 0으로 나오는 현상 수정 >>
            }

            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
            if (_cvm.nFxMarginType == ChartViewModel.FX_BUYSELL) {
                double[] dCloses = new double[1];
                dCloses[0] = dMarketValue;
                setFXData("매수", dCloses, true);
            }
            //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

            //2014.03.27 by LYH >> 시고저 실시간 처리.
            try
            {
                if(openVal.length()>0 && Double.parseDouble(openVal) != 0)
                    _cdm.codeItem.strGiOpen = openVal;
                if(highVal.length()>0 && Double.parseDouble(highVal) != 0)
                    _cdm.codeItem.strGiHigh = highVal;
                if(lowVal.length()>0 && Double.parseDouble(lowVal) != 0)
                    _cdm.codeItem.strGiLow = lowVal;
            }catch(Exception e)
            {

            }
            //2014.03.27 by LYH << 시고저 실시간 처리.
//	        	if(COMUtil.getMainBase()!=null) COMUtil.getMainBase().setCountText("");

//	        }catch(Exception e){
//	            System.out.println("repaintRT Error:"+e.getMessage());
//	            e.printStackTrace();
//	        }
        }
    }

    private void setUseRealData(String[] rlData){
        useReal = true;
//        reqRV = new Vector(10);
        _cdm.setRealPacketData(rlData);
    }

    public void DataChanged(DataChangeEvent e){

    }
    public void DataAdded(DataChangeEvent e){
        if(_cvm.isOnePage()){
            setScrollBar(DATA_END);
        }else{
            switch(_cdm.getDateType()){
                case 0://text
                    break;
                case 4://분
                case 7://초
                    if(e.getDataState() == DataChangeEvent.ADDDATA_CHANGE)
                        setScrollBar(this.ONLY_DATACHANGE);
                    break;
                case 5://틱
                    //setScrollBar(this.NOTONLY_DATACHANGE);
                    setScrollBar(this.ONLY_DATACHANGE);
                    break;
            }
        }
        Block b;
        for(int i=0; i<blocks.size(); i++){
            b = (Block)blocks.elementAt(i);
            b.makeGraphDataReal();
        }
        repaintAll();
    }
    public void DataIndexChanged(DataChangeEvent e){

    }
    public void DataInserted(DataChangeEvent e) {
        setScrollBar(this.ONLY_DATACHANGE);
        Block b;
        for(int i=0; i<blocks.size(); i++){
            b = (Block)blocks.elementAt(i);
            b.makeGraphDataReal();
        }
        repaintAll();
    }

//    public void onPause() {
//        super.onPause();
//    }
//
//    public void onResume() {
//    	super.onResume();
//    }

    public void setMarketData(String title, long[] dates, double[] marketData, int nCount, boolean bSendTR) {
        //2024.01.04 by sJW - 크래시로그 수정 >>
        if (title == null || dates == null || marketData == null) {
            return;
        }
        //2024.01.04 by sJW - 크래시로그 수정 <<
        //2017.06.14 by LYH >> FX마진차트 매수매도 next조회 처리
        if(title.equals("팔때_append"))
        {
            ChartPacketDataModel cpdm = _cdm.getChartPacket("팔때");
            if(cpdm != null)
                cpdm.initAppendData(nCount);
            _cdm.setData_data("팔때", marketData);
            return;
        }
        //2017.06.14 by LYH >> FX마진차트 매수매도 next조회 처리 end
        double[] d = new double[_cdm.getCount()];
        if(marketData != null)
        {
            try{
                System.arraycopy(marketData, 0, d, 0, _cdm.getCount());
            }catch(ArrayIndexOutOfBoundsException e){

            }

            if(dates != null)
            {
                String[] dateData=_cdm.getStringData("자료일자");
                if(dateData.length>0)
                {
                    long dDate;
                    int nTotCount = dateData.length;
                    for(int i = 0 ; i < nTotCount ; i++) {
                        d[i]=0;
                    }
                    int nPreMonth = 0;
                    int nMonth = 0;
                    int nPreYear = 0;
                    int nYear = 0;
                    for(int i = 0 ; i < nTotCount ; i++) {
                        dDate = Long.parseLong(dateData[i]);
                        nMonth =  (int)dDate%100;
                        nYear =  (int)dDate/100;
//						for(int j = nCount-1 ; j >=0 ; j--) {
                        for(int j = 0 ; j <dates.length ; j++) {
                            if(_cdm.getDateType() == ChartDataModel.DATA_MONTH)
                            {
                                if((int)dDate==(int)dates[j]/100 && nMonth != nPreMonth)
                                {
                                    d[i]=marketData[j];
                                    nPreMonth = (int)dDate%100;
                                    break;
                                }
                            }
                            else if(_cdm.getDateType() == ChartDataModel.DATA_YEAR)
                            {
                                if((int)dDate==(int)dates[j]/100 && nYear != nPreYear)
                                {
                                    d[i]=marketData[j];
                                    nPreYear = (int)dDate/100;
                                    break;
                                }
                            }
                            else
                            {
                                if(dDate==dates[j])
                                {
                                    d[i]=marketData[j];
                                    break;
                                }
                            }
                        }
                    }
                }
            }


            _cdm.setSubPacketData(title, d);
            if(title.equals("외국인 비율") || title.equals("외국인/기관/개인 추세") || title.equals("외국인 순매수") || title.equals("기관 순매수")  || title.equals("개인 순매수") ||
                    title.equals("신용 잔고율") || title.equals("PBR") || title.equals("PSR") || title.equals("엔-달러") ||
                    title.equals("금") || title.equals("두바이유") || title.equals("서부텍사스유") ||
                    (title.equals("BaseMarket") && _cdm.codeItem.strBaseMarket.endsWith("200")|| title.equals("(선물)베이시스") || title.equals("수익률"))) {
                _cdm.setPacketFormat(title, "× 0.01");
            }

            if(title.equals("팔때"))
                _cdm.setSyncPriceFormat(title);

            if(bSendTR)
                this.repaintAll();
        }

        Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
        if(nSendMarketIndex>=0 && bSendTR)
        {
            if(_cvm.bNetBuyChart)
            {
                if(basic_block != null) {
                    for(int i=nSendMarketIndex+1; i<basic_block.getGraphs().size(); i++) {
                        AbstractGraph graph = (AbstractGraph)basic_block.getGraphs().get(i);
                        if(COMUtil.isMarketIndicator(graph.getGraphTitle())) {
                            nSendMarketIndex = i;
                            String strMarket = graph.getGraphTitle();
                            int cnt = _cdm.getCount();

                            Hashtable<String, Object> dic = new Hashtable<String, Object>();
                            dic.put("nTotCount", String.valueOf(cnt));
                            dic.put("title", strMarket);

                            //delegate 처리
                            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
                            //2013. 12. 20 주식현재가 차트 시장지표추가후 세로->가로 전환시 기존추가한 지표가 전부 초기화>>
                            COMUtil.setSendTrType(strMarket); //차트 축소시 추가 데이터 요청 발생할때 필요.
                            return;
                        }
                    }
                }
            }
            if(!isSendRotateMarket) {
                for ( int i=nSendMarketIndex+1; i<blocks.size(); i++) {
                    Block block = blocks.get(i);
                    if(COMUtil.isMarketIndicator(block.getTitle())) {
                        //base11.sendTR(block.getTitle());
                        String strMarket = block.getTitle();
                        int cnt = _cdm.getCount();

                        Hashtable<String, Object> dic = new Hashtable<String, Object>();
                        dic.put("nTotCount", String.valueOf(cnt));
                        dic.put("title", strMarket);

                        if(marketData == null)
                            nSendMarketIndex++;
                        //delegate 처리
                        if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
                        //2013. 12. 20 주식현재가 차트 시장지표추가후 세로->가로 전환시 기존추가한 지표가 전부 초기화>>
                        COMUtil.setSendTrType(strMarket); //차트 축소시 추가 데이터 요청 발생할때 필요.
                        nSendMarketIndex++;
                        return;
                    }

                }

                //2016.11.28 by LYH >> 로테이트 블럭 시장지표 조회 처리
                if(rotate_blocks!=null && rotate_blocks.size()>0) {
                    if(nSendMarketIndex<blocks.size()-1)
                        nSendMarketIndex = blocks.size()-1;
                    for (int i = nSendMarketIndex + 1; i < blocks.size()+rotate_blocks.size(); i++) {
                        Block block = rotate_blocks.get(i-blocks.size());
                        if (COMUtil.isMarketIndicator(block.getTitle())) {
                            //base11.sendTR(block.getTitle());
                            String strMarket = block.getTitle();
                            int cnt = _cdm.getCount();

                            Hashtable<String, Object> dic = new Hashtable<String, Object>();
                            dic.put("nTotCount", String.valueOf(cnt));
                            dic.put("title", strMarket);

                            if (marketData == null)
                                nSendMarketIndex++;
                            //delegate 처리
                            if (userProtocol != null)
                                userProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
                            //2013. 12. 20 주식현재가 차트 시장지표추가후 세로->가로 전환시 기존추가한 지표가 전부 초기화>>
                            COMUtil.setSendTrType(strMarket); //차트 축소시 추가 데이터 요청 발생할때 필요.
                            nSendMarketIndex++;
                            return;
                        }
                    }
                }
                //2016.11.28 by LYH << 로테이트 블럭 시장지표 조회 처리 end

//2016.02.14 by LYH >> 로테이트 블럭 보이지 않는 영역 시장지표 조회 막음
//				//blocks 마켓 처리한 후 rotateBlock 처리
//				if(rotate_blocks!=null && rotate_blocks.size()>0) {
//					isSendRotateMarket = true;
//					preMarketTitle = "";
//					nSendMarketIndex = -1;
//
//					Block block = rotate_blocks.get(0);
//					if (block==null) {
//						if (base11.m_bSyncIndicator) {
//							base11.m_bSyncIndicator = true;
//							base11.sendIndicatorTrCodes();
////                        } else if(base11.m_bSyncJongMok || base11.m_bSyncJugi) {
////                        	System.out.println("base11:NeoChart"+"sendRotateTR");
////                            base11.sendRotateTR();
//						} else {
//							base11.sendEventOfLastMarketData();
//						}
//
//						return;
//					}
//					if (COMUtil.isMarketIndicator(block.getTitle())) {
//						base11.sendTR(block.getTitle());
//						nSendMarketIndex++;
//						return;
//					}
//				}
//2016.02.14 by LYH << 로테이트 블럭 보이지 않는 영역 시장지표 조회 막음
                isSendRotateMarket = false;
                preMarketTitle = "";
                nSendMarketIndex = -1;

                if (base11.m_bSyncIndicator) {
                    base11.m_bSyncIndicator = true;
                    base11.sendIndicatorTrCodes();
//                } else if(base11.m_bSyncJongMok || base11.m_bSyncJugi) {
//                	System.out.println("base11:NeoChart2"+"sendRotateTR" + " nSendMarketIndex:"+nSendMarketIndex);
//                    base11.sendRotateTR();
                } else {
                    base11.sendEventOfLastMarketData();
                }

                return;

            }else {
                //blocks 마켓 처리한 후 rotateBlock 처리
                if(rotate_blocks!=null && rotate_blocks.size()>0) {
                    for ( int i=nSendMarketIndex+1; i<rotate_blocks.size(); i++) {
                        Block block = rotate_blocks.get(i);
                        if(COMUtil.isMarketIndicator(block.getTitle())) {
                            base11.sendTR(block.getTitle());
                            nSendMarketIndex++;
                            return;
                        }

                    }

                    isSendRotateMarket = false;
                    preMarketTitle = "";
                    nSendMarketIndex = -1;

                    if (base11.m_bSyncIndicator) {
                        base11.m_bSyncIndicator = true;
                        base11.sendIndicatorTrCodes();
//                    } else if(base11.m_bSyncJongMok || base11.m_bSyncJugi) {
//                    	System.out.println("base11:NeoChart3"+"sendRotateTR"+ " nSendMarketIndex:"+nSendMarketIndex);
//                        base11.sendRotateTR();
                    } else {
                        base11.sendEventOfLastMarketData();
                    }

                    return;
                }
            }

        }
//        preMarketTitle = title;
//        nSendMarketIndex++;
    }
    private float m_nCurDataWidth=0;
    public void checkOrientation(int nMode)
    {
        if(_cvm.isStandGraph() || _cvm.bStandardLine || _cvm.bInvestorChart || _cvm.bIsLineFillChart || _cvm.bIsLineChart || _cvm.bIsMiniBongChart || _cvm.bRateCompare)
        {
            return;
        }
        if(nMode == 1)
        {
            m_nCurDataWidth = _cvm.getDataWidth();
            if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                m_nMaxIndicatorCount = 3;
            }
            int nBlockCnt = blocks.size();
            if(nBlockCnt >m_nMaxIndicatorCount+1)
            {
                for(int i=m_nMaxIndicatorCount+1; i<nBlockCnt; i++)
                {
                    Block cb = (Block)blocks.get(m_nMaxIndicatorCount+1);
                    rotate_blocks.addElement(cb);
                    cb.setHideDelButton(true);
                    blocks.removeElement(cb);
                    if(!title_list.contains(cb.getTitle())) {
                        title_list.insertElementAt(cb.getTitle(), 0);
                    }
                }
                Block b = (Block)blocks.get(m_nMaxIndicatorCount);
                if(!title_list.contains(b.getTitle())) {
                    title_list.insertElementAt(b.getTitle(), 0);
                }

                setTitleNumber();
            }
            m_nRollIndex = 0;
        }
        else if(nMode == 2)
        {
            m_nCurDataWidth = _cvm.getDataWidth();
            if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
                m_nMaxIndicatorCount = 5;
            }
            int nBlockCnt = blocks.size();
            if(nBlockCnt <= m_nMaxIndicatorCount)
            {
                Block b = (Block)blocks.get(blocks.size()-1);
                if(title_list.contains(b.getTitle())) {
                    title_list.removeElement(b.getTitle());
                    b.setTitleNumber(0, 0);
                }
                for ( int i=0; i<2; i++)
                {
                    if(blocks.size()>m_nMaxIndicatorCount || title_list.size()<1)
                        break;
                    String title = title_list.get(0);

                    for(int j=rotate_blocks.size()-1; j>=0; j--)
                    {
                        Block cb = (Block)rotate_blocks.get(j);
                        if(cb.getTitle().equals(title))
                        {

                            rotate_blocks.removeElement(cb);
                            cb.setHideDelButton(false);
                            cb.setTitleNumber(0, 0);
                            cb.makeGraphData();
                            blocks.addElement(cb);
                            if(blocks.size()<=m_nMaxIndicatorCount)
                            {
                                title_list.removeElementAt(i);
                            }
                        }
                    }
                }
            }
            //[self setPivotState:pivotState];
            m_nRollIndex = 0;
            if(rotate_blocks != null && rotate_blocks.size() > 0)
                setTitleNumber();

        }

    }

    public void setCandleType(String strType)
    {
        if(basic_block==null || basic_block.getGraphs().size()<1) return;
        AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
        if(basic_graph.getDrawTool().size()<0)
            return;

        DrawTool basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);
        if (strType != null) {
            if(basic_dt.getDrawType1() != 1)
            {
                this.changeBlock_NotRepaint("라인");
                //repaintAll();
            }
        } else {
            if(m_strCandleType!=null /*&& basic_dt.getDrawType1() == 1*/)
            {
                this.changeBlock_NotRepaint(m_strCandleType);
            }
        }
    }

    public void removeDelButton()
    {
        if(blocks != null) {
            for (int i=0; i<blocks.size(); i++) {
                Block block = blocks.get(i);
                block.setHideDelButton(true);
            }
        }
    }

    //2012.11.27 by LYH >> 진법 및 승수 처리.
    public void setPriceFormat(int nScale, int nDecPoint, int nLogDisp)
    {
        _cdm.setPriceFormat("× 0.0001", nScale, nDecPoint, nLogDisp);
        makeGraphData();
        repaintAll();
    }
    //2012.11.27 by LYH <<

    //2016.10.27 by lyk - 비교차트 진법 적용
    public void setPriceFormatCompare(int nScale, int nDecPoint, int nLogDisp, String sCode)
    {
        //    NSLog(@"Debug_neoChart_nDecPoint:%d", nDecPoint);
        _cdm.setPriceFormatCompare("× 0.0001", nScale, nDecPoint, nLogDisp, sCode);
        makeGraphData();
        repaintAll();
    }
    //2016.10.27 by lyk - 비교차트 진법 적용 end

    boolean m_bLog = false;
    private void setLogChart()
    {
        if(_cvm.chartType == COMUtil.COMPARE_CHART)
            return;
//		if(this.m_bLog != _cvm.isLog || m_bInverse != COMUtil.bIsInverseChart)
//		{
        if(basic_block == null || basic_block.getGraphs() == null || basic_block.getGraphs().size() < 1) return; //2022.10.13 by lyk - NullPointerException 방어코드 추가
        AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
        if(basic_graph.getDrawTool().size()<1)
            return;

        DrawTool basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);
        if(basic_dt.isLog() != _cvm.isLog || basic_dt.isInverse() != _cvm.isInverse) {
            basic_block.setLog(_cvm.isLog);
            basic_dt.setLog(_cvm.isLog);
            basic_dt.setInvertScale(_cvm.isInverse);
            Vector<AbstractGraph> graphs = basic_block.getGraphs();
            for (int i = 1; i < graphs.size(); i++) {
                AbstractGraph g = (AbstractGraph) graphs.elementAt(i);
                Vector<DrawTool> dts = g.getDrawTool();
                for (int j = 0; j < dts.size(); j++) {
                    DrawTool dt = (DrawTool) g.getDrawTool().elementAt(j);
                    dt.setLog(_cvm.isLog);
                    dt.setInvertScale(_cvm.isInverse);
                }
            }
            YScale yscale = basic_block.getYScale()[0];
            yscale.setInvertScale(_cvm.isInverse);
        }
        //this.m_bLog = _cvm.isLog;
//	        System.out.println("###setLog: "+m_bLog);
//		}
    }

    //2013.01.04 by LYH >> 등락률 비교차트 추가(섹션별종목차트)
    public void changeTitle(String strNames)
    {
        if(strNames != null)
        {
            String[] values = strNames.split(",");

            if(values.length >=2)
            {
                AbstractGraph basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(0);
                if(basic_graph.getDrawTool().size()<0)
                    return;

                DrawTool basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);
                if(basic_dt != null)
                {
                    basic_dt.subTitle = values[0];
                }

                basic_graph = (AbstractGraph)basic_block.getGraphs().elementAt(1);
                if(basic_graph.getDrawTool().size()<0)
                    return;

                basic_dt = (DrawTool)basic_graph.getDrawTool().elementAt(0);
                if(basic_dt != null)
                {
                    basic_dt.subTitle = values[1];
                }
                resetTitleBoundsAll();//블럭 타이틀 그리기.
            }
        }
    }
    //2013.01.04 by LYH <<

    //2013. 2. 8 지표설정창 상세설정 체크안된 것 보이게 처리
    public AbstractGraph getUnChkGraph(String name)
    {
        AbstractGraph tmpGraph = null;
        int count = unChkGraphs.size();
        boolean isEqual = false;
        for(int i = 0; i < count; i++)
        {
            tmpGraph = unChkGraphs.get(i);
            if(tmpGraph.graphTitle.equals(name))
            {
                isEqual = true;
                break;
            }
        }

        if(isEqual)
        {
            return tmpGraph;
        }
        else
        {
            return null;
        }
    }
    public void addUnChkGraph(AbstractGraph item)
    {
        if(item == null)  return;
//    	if(unChkGraphs == null)
//    	{
//    		unChkGraphs = new Vector<AbstractGraph>();
//    	}

        AbstractGraph tmpGraph = null;
        int count = unChkGraphs.size();
        boolean add = false;
        for(int i = 0; i < count; i++)
        {
            tmpGraph = unChkGraphs.get(i);

            if(tmpGraph.graphTitle.equals(item.graphTitle))
            {
                unChkGraphs.remove(i);
                unChkGraphs.add(i, item);
                add = true;
                break;
            }
        }

        if(!add)
        {
            unChkGraphs.add(item);
        }
    }
    public void removeUnChkGraph(AbstractGraph graph)
    {
        unChkGraphs.remove(graph);
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


    //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 >>
    public boolean isChartItemViewHidden()
    {
        return  bChartItemViewHidden;
    }
    public void setChartItemViewHidden(boolean  bChartItemViewHidden)
    {
        this. bChartItemViewHidden =  bChartItemViewHidden;
    }
    //2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 <<

    //2017.05. 15 해외선물옵션 FX 멀티차트 차트 상단 종목정보 (ChartItemView) 미표시 처리  >>
    public boolean isChartItemViewFXHidden()
    {
        return  bChartItemViewFXHidden;
    }
    public void setChartItemViewFXHidden(boolean  bChartItemViewFXHidden)
    {
        this. bChartItemViewFXHidden =  bChartItemViewFXHidden;
    }
    //2017.05. 15 해외선물옵션 FX 멀티차트 차트 상단 종목정보 (ChartItemView) 미표시 처리  >>

    //2013.07.22 by LYH >> 투자자 차트 그래프 보이기/숨기기 기능
    public void setVisible(String strTitle, boolean bVisible)
    {
        for(int i=0; i<blocks.size(); i++)
        {
            Block block = blocks.get(i);
            block.setVisible(strTitle, bVisible);
        }
        resetTitleBoundsAll();
        repaintAll();
    }
    //2013.07.22 by LYH <<

    //2021.04.26 by lyk - kakaopay - 그래프명으로 차트 그래프의 visible 상태를 반환 >>
    public boolean getVisible(String strTitle)
    {
        boolean rtnVal = false;
        for(int i=0; i<blocks.size(); i++)
        {
            Block block = blocks.get(i);
            rtnVal = block.getVisible(strTitle);
            if(rtnVal) {
                break;
            }
        }
        return rtnVal;
    }
    //2021.04.26 by lyk - kakaopay - 그래프명으로 차트 그래프의 visible 상태를 반환 <<

    //2013.09.02 by LYH >> 라인 속성 스크립트로 변경 가능하도록 함.
    public void changeLineProperty(String strValues)
    {
        if(strValues != null)
        {
            String[] values = strValues.split("\\^");

            if(values.length >=2)
            {
                for(int i=0; i<basic_block.getGraphs().size(); i++)
                {
                    AbstractGraph graph = (AbstractGraph)basic_block.getGraphs().elementAt(i);
                    if(graph.getDrawTool().size()<0)
                        return;
                    for(int j=0; j<graph.getDrawTool().size(); j++)
                    {
                        DrawTool dt = (DrawTool)graph.getDrawTool().elementAt(j);
                        if(dt != null && dt.getTitle().equals(values[0]))
                        {
                            dt.setVisible(values[1].equals("0")?false:true);
                            if(values.length>=3)
                                dt.subTitle = values[2];
                            if(values.length>=6)
                            {
                                try{
                                    int nValue[] = {Integer.parseInt(values[3]), Integer.parseInt(values[4]), Integer.parseInt(values[5])};
                                    dt.setUpColor(nValue);
                                }
                                catch(Exception e)
                                {

                                }
                            }
                            if(values.length>=7)
                            {
                                try{
                                    int nThick = Integer.parseInt(values[6]);
                                    dt.setLineT(nThick);
                                }
                                catch(Exception e)
                                {
                                }
                            }
                            resetTitleBoundsAll();//블럭 타이틀 그리기.
                            repaintAll();
                            break;
                        }
                    }
                }
            }
        }
    }
    //2013.09.02 by LYH <<

    //2013. 11. 21 추세선 종목별 저장>>
    /**
     * 종목과주기를 기준으로 추세선등 분석툴바를 다르게 저장
     * */
    public void saveAnalToolBySymbol()
    {
        if(null != COMUtil.analPrefEditor)
        {
            //저장 Key.  종목코드:주기   ex) 000660:2
            String strKey = _cdm.codeItem.strCode + ":" + _cdm.codeItem.strDataType;

            //현재 종목에 대한 분석툴바 저장정보를 추가한다.
            //String strAnalInfo = COMUtil.getAnalInfoStrBySymbol();
            String strAnalInfo = COMUtil.getAnalInfoStr(this);
            if(strAnalInfo == null || strAnalInfo.equals(""))	//그려진 분석툴바가 없을 경우, 해당 정보를 제거
            {
                COMUtil.analPrefEditor.remove(strKey);
            }
            else
            {
                COMUtil.analPrefEditor.putString(strKey, strAnalInfo);
            }
            //추가완료.
            COMUtil.analPrefEditor.commit();

            //2019. 08. 12 by hyh - 화면 내렸다가 올릴 때 추세선 추가/이동 저장되도록 처리
            saveLastState();
        }
    }

    /**
     * 추세선등 분석툴바를 종목별로 불러옴.
     * @return 현재 종목의 분석툴바 저장 정보 (Vector 객체)
     * */
    public Vector loadAnalToolBySymbol()
    {
        //analPref preference 에 저장된 종목별 분석툴 정보를 가져온다.
        String[] token = null;
        Vector analInfos = null;
        if(null != COMUtil.analPref && !_cvm.bIsLineFillChart && !_cvm.bIsLineChart && !_cvm.bIsMiniBongChart && !_cvm.bIsLine2Chart)
        {
            //현재 종목정보를 기반으로 분석툴정보를 불러오는데 사용할 Key 생성.  종목코드:주기  ex) 000660:2
            String strKey = _cdm.codeItem.strCode + ":" + _cdm.codeItem.strDataType;

            //key 에 해당하는 분석툴바 저장정보를 불러온다.
            token = COMUtil.analPref.getString(strKey, "").split("\\/=/");

            //String 배열로 되어있는 분석툴 정보를 Vector 객체로 만든다. ( 분석툴 불러와서 사용할 때 Vector 객체로 사용함 )
            analInfos = new Vector();
            if(null != token && token[0].length() > 0)	//2015. 1. 13 저장불러오기 했을 때 분석툴바 불러오는지 확인
            {
                for(int i=0; i<token.length; i++)
                {
                    analInfos.add(COMUtil.getAnalInfos(token[i]));
                }
            }
            //2015. 1. 13 저장불러오기 했을 때 분석툴바 불러오는지 확인>>
            else
            {
                analInfos = null;
            }
            //2015. 1. 13 저장불러오기 했을 때 분석툴바 불러오는지 확인<<
        }

        //현재 종목에 대한 분석툴바 저장정보를 Vector 객체로 반환한다.
        return analInfos;
    }

    /**
     * 현재 차트의 종목주기에 해당하는 분석툴만 지운다.
     * */
    public void resetAnalToolBySymbol()
    {
        //현재 차트화면에 그려져있는 분석툴을 모두 지운다.
        removeAllAnalTool();

        //차트화면이 지워진 상태를  preference로 저장.
        saveAnalToolBySymbol();
    }
    //2013. 11. 21 추세선 종목별 저장<<

    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가>>
    private void showAnalToolSetting()
    {
        AnalToolSettingViewController analtoolSettingView = new AnalToolSettingViewController(context, this);
        analtoolSettingView.show();

        if(analToolSubbar!=null) {
            this.showAnalToolSubbar(false, analParams);
        }
    }
    //2015. 1. 13 분석툴 수정기능 및 자석기능 추가<<

    //2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기>>
    public void setYScalePriceLine(String strPrice)
    {
        if(null == strPrice)
        {
            return;
        }

        //수평선 값 저장
        _cvm.setDragPrice(strPrice);

        repaintAll();
    }
    //2015. 1. 22 목표가 전달 메인으로부터 값을 받아 목표가 선 하나 그리기<<

    //2015. 1. 29 가상매매연습기 매도/매수 표시>>
    public void setTradeData(String strTradeData)
    {
        if (analTradeTools == null) {
            analTradeTools = new Vector<AnalTool>();
        } else {
            analTradeTools.removeAllElements();
        }
        if (tradeViewP != null) {
            tradeViewP.setVisibility(View.GONE);
        }
        if (strTradeData != null) {
            String[] values = strTradeData.split(";");
            if (values.length >= 2) {
                Block cb = (Block) blocks.get(0);
                ATradeTool at;
                String strOneData;
                for (int i = 1; i < values.length; i++) {
                    strOneData = values[i];
                    String[] strOneArray = strOneData.split("\\^");
                    if (strOneArray.length >= 4) {
                        at = new ATradeTool(cb);
                        at.addAnalInfo(strOneArray);
                        analTradeTools.addElement(at);
                    }
                }
            }
        }

        repaintAll();
    }

    //2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건(특정일) 처리 >>
    public void setTradeDataSpecificDay(String strTradeData)
    {
        if (analTradeTools == null) {
            analTradeTools = new Vector<AnalTool>();
        }
//		if (tradeViewP != null) {
//			tradeViewP.setVisibility(View.GONE);
//		}
        if (strTradeData != null) {
            String[] values = strTradeData.split(";");
            if (values.length > 2) {
                Block cb = (Block) blocks.get(0);
                ATradeTool at;

                String strOneData;

                //같은 날짜의 tradeTool이 있다면 신규 값으로 교체한다.
                if(this.analTradeTools.size() > 0) {
                    for (int i = 1; i < values.length; i++) {
                        strOneData = values[i];
                        if(strOneData.equals("")) {
                            continue;
                        }

                        String[] strOneArray = strOneData.split("\\^");
                        String strDate = strOneArray[1];
                        String strType = strOneArray[0];

                        if (strOneArray.length >= 4) {
                            for(int k=0; k<this.analTradeTools.size(); k++) {
                                at = (ATradeTool) this.analTradeTools.get(k);
                                String cmpDate = at.getTradeData()[1];
                                String cmpType = at.getTradeData()[0];
                                strOneData = values[i];
                                String[] strNewArray = strOneData.split("\\^");
                                if(strDate.equals(cmpDate) && strType.equals(cmpType)) { //날짜와 매수/매도 타입이 같을 경우 처리
                                    if (strNewArray.length >= 4) {
                                        ATradeTool newItem = new ATradeTool(cb);
                                        newItem.addAnalInfo(strNewArray);
                                        analTradeTools.setElementAt(newItem, k);
                                    }
                                    break;
                                } else if(!strDate.equals(cmpDate) && strType.equals(cmpType)) {
                                    if (strOneArray.length >= 4) {
                                        at = new ATradeTool(cb);
                                        at.addAnalInfo(strOneArray);
                                        analTradeTools.addElement(at);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 1; i < values.length; i++) {
                        strOneData = values[i];
                        String[] strOneArray = strOneData.split("\\^");
                        if (strOneArray.length >= 4) {
                            at = new ATradeTool(cb);
                            at.addAnalInfo(strOneArray);
                            analTradeTools.addElement(at);
                        }
                    }
                }
            }
        }

        repaintAll();
    }
    //2021.08.10 by lyk - kakaopay - 실시간 매매내역 당일건(특정일) 처리 <<

    public void resetTradeData()
    {
        if(null != analTradeTools)
        {
            analTradeTools.removeAllElements();
        }
    }

    public void addTradePacketData(String strTradeDataPacket)
    {
        _cdm.addTradePacketData(strTradeDataPacket);
    }
    //2015. 1. 29 가상매매연습기 매도/매수 표시<<
    //2015. 2. 24 차트 상하한가 표시>>
    /**
     * 차트 상하한가 데이터를 세팅한다.
     * @param strData 차트 상하한가 데이터   (날짜, 상하한가구분)
     * */
    public void setChartUpperLowerLimitData(String strData)
    {
        if(null == strData)
        {
            return;
        }

        if(analUpperLowerTools == null)
        {
            analUpperLowerTools=new Vector<AnalTool>();
        }
        if(analUpperLowerTools.size()>0)
        {
            analUpperLowerTools.clear();
        }

        if(strData != null && !strData.equals(""))
        {
            Block cb = (Block)blocks.get(0);
            String[] arDataToken = strData.split(":");
            for(String sData : arDataToken)
            {
                AUpperLowerLimitTool at;
                at = new AUpperLowerLimitTool(cb);
                at.addAnalInfo(sData);
                analUpperLowerTools.addElement(at);
            }
        }
        repaintAll();
    }
    //2015. 2. 24 차트 상하한가 표시<<

    //2014.04.08 by LYH >> 8,32진법처리.
    private double getScalePrice(double dPrice, int nScale)
    {
        double dFractional, dInt;

        dInt = Math.floor(dPrice);
        dFractional = dPrice - dInt + 0.00001;
        if(nScale == 8)
            return dInt + dFractional*1.25 + 0.000001;
        else if(nScale == 32)
            return dInt + dFractional*3.125 + 0.000001;

        return dPrice;
    }
    //2014.04.08 by LYH << 8,32진법처리.

    //2016. 1. 14 뉴스차트 핸들러>>
    private void showVertLineWithCircle(Canvas gl, float x, float y, RectF bounds, Vector<Hashtable<String, String>> datas)
    {
        try {
            //핸들러 점선
//			_cvm.drawImage(gl, x-(int)COMUtil.getPixel(1), (int)COMUtil.getPixel(23), (int)COMUtil.getPixel(2),bounds.height()-_cvm.XSCALE_H-(int)COMUtil.getPixel(23), bitmapLine, 255);
            int[] arLineColor = {255, 167, 167};
            _cvm.drawLine(gl, x,bounds.top,x,bounds.bottom-_cvm.XSCALE_H+(int)COMUtil.getPixel(12), arLineColor, 0.5f);

            //핸들러 흰색 원
            final int _n_Circle_Width = (int)COMUtil.getPixel(22);
            final int _n_Circle_Height = (int)COMUtil.getPixel(22);
//			_cvm.drawImage(gl, x-(_n_Circle_Width/2), y-(_n_Circle_Height/2), _n_Circle_Width,_n_Circle_Height, bitmapCircle, 255);
            _cvm.drawImage(gl, x-(_n_Circle_Width/2), y-(_n_Circle_Height), _n_Circle_Width,_n_Circle_Height, bitmapCircle, 255);
        } catch (Exception e) {

        }
    }
    //2016. 1. 14 뉴스차트 핸들러<<

    //2015.04.21 by lyk - 차트 블럭크기 및 위치 정보 저장 end
    /* 투자자별 실시간 처리 2013.03.14 by lyk */
    public void setInvestorRealData(String[] titles, String[] marketData, boolean bReal) {

        if(_cdm.m_bRealUpdate)
        {
            ChartPacketDataModel cpdm;
            cpdm = _cdm.getChartPacket("자료일자");
            String strTime = marketData[0];
            String resultTime = strTime;
            switch(_cdm.getDateFormat()){
                case XScale.DDHHMMSS:
                    if(strTime.length()==8) {
                        resultTime = COMUtil.getSaveDate("MMdd") + strTime.substring(4, 4);
                    } else {
                        resultTime = COMUtil.getSaveDate("MMdd") + strTime.substring(0, 4);
                    }
                    break;
                case XScale.HHMMSS: //HHMMSS
                    if(strTime.length() == 6)
                    {
                        resultTime = strTime;
                    }
                    break;
                case XScale.MMDDHHMM:
                    if(strTime.length()>4)
                        strTime = strTime.substring(0,4);

                    if(strTime.length() == 4)
                    {
                        resultTime = cpdm.getDate(cpdm.getLastStringData())+strTime;
                    }
                    break;
                default:
                    break;
            }

            int m_nRealIndex = cpdm.findIndex(resultTime);
            if (m_nRealIndex < 0)
            {
                cpdm.addRealData(resultTime);
                setScrollBar(ONLY_DATACHANGE);
            }
            String title;
            for(int i=0; i<titles.length; i++) {
                title = titles[i];
                if(title.trim().equals(""))
                    continue;

                cpdm = _cdm.getChartPacket(title);
                if(cpdm==null) continue;

                if (m_nRealIndex >= 0)
                    cpdm.setDataAtIndex(marketData[i+1], m_nRealIndex);
                else {

                    cpdm.addRealData(marketData[i+1]);
                }
            }
            this.repaintAll();
            return;
        }
        if(bReal)
        {
            String title="";
            ChartPacketDataModel cpdm;
            if(!titles[0].equals("(선물)베이시스") && !titles[0].equals("팔때")) {
                double price[] = _cdm.getSubPacketData(titles[0]);
                if (price == null) return;
            }
            else
            {
                boolean bContains = false;
                for(int i=0; i<titles.length; i++) {
                    title = titles[i];
                    if (title.trim().equals(""))
                        continue;

                    cpdm = _cdm.getChartPacket(title);
                    if (cpdm == null) continue;
                    bContains = true;
                }
                if(!bContains)
                    return;
                if(titles[0].equals("팔때")){
                    cpdm = _cdm.getChartPacket(titles[0]);
                    if(cpdm.getDataCount() == _cdm.getCount())
                        cpdm.setData(marketData[1]);
                    else
                        cpdm.addRealInvestorData(marketData[1]);

                    repaintAll();
                    return;
                }
            }
            //int m_dataCnt = price.length;

            String strTime = marketData[0];

            boolean bRealTimeCheck = false;
            String resultTime = strTime;
            //날짜 포맷에 따라 변경
            switch(_cdm.getDateFormat()){
                case XScale.DDHHMMSS:
                    if(strTime.length()==8) {
                        resultTime = COMUtil.getSaveDate("MMdd") + strTime.substring(4, 4);
                    } else {
                        resultTime = COMUtil.getSaveDate("MMdd") + strTime.substring(0, 4);
                    }
                    break;
                case XScale.HHMMSS: //HHMMSS
                    if(strTime.length() == 6)
                    {
                        resultTime = strTime;
                    }
                    break;
                case XScale.MMDDHHMM:
                    if(strTime.length() == 4)
                    {
                        resultTime = COMUtil.getSaveDate("MMdd")+strTime;
                    }
                    break;
                default:
                    break;
            }

            if(_cdm.getDateType() == ChartDataModel.DATA_TIC)
                bRealTimeCheck = true;
            if(_cdm.getDateType() == ChartDataModel.DATA_MIN && _cdm.realTimeCheck(strTime))
            {
                bRealTimeCheck = true;
            }

            if(bRealTimeCheck)
            {
                cpdm = _cdm.getChartPacket("자료일자");
                cpdm.addRealInvestorData(resultTime);
            }

            for(int i=0; i<titles.length; i++) {
                title = titles[i];
                if(title.trim().equals(""))
                    continue;

                cpdm = _cdm.getChartPacket(title);
                if(cpdm==null) continue;

                if(!strTime.equals("")) {
                    if(bRealTimeCheck)
                    {
                        cpdm.addRealInvestorData(marketData[i+1]);
                    }
                    else
                    {
                        cpdm.setData(marketData[i+1]);
                    }
                }
            }

            if(!titles[0].equals("(선물)베이시스"))
//        	    setScrollBar(DATA_END);
                setScrollBar(ONLY_DATACHANGE);

            this.repaintAll();
        }
    }

    public void setLineColor(String strColor)
    {
        String[] sepDatas = strColor.split(";");
        if(sepDatas.length>3)
        {
            AbstractGraph graph;
            DrawTool dt;
            for(int i=0; i<blocks.size(); i++) {
                Block block = (Block)blocks.elementAt(i);
                for(int j=0; j<block.getGraphs().size(); j++)
                {
                    graph = block.getGraphs().get(j);
                    for(int k=0; k<graph.getDrawTool().size(); k++)
                    {
                        dt = graph.getDrawTool().get(k);
                        if(dt.getTitle().equals(sepDatas[0]))
                        {
                            int color[] = new int[3];
                            try {
                                color[0] = Integer.parseInt(sepDatas[1]);
                                color[1] = Integer.parseInt(sepDatas[2]);
                                color[2] = Integer.parseInt(sepDatas[3]);
                                dt.setUpColor(color);
                            } catch (Exception e) {

                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    public void setNewsDate(String strDate)
    {
        try {
            int idx = -1;
            String[] dateData=_cdm.getStringData("자료일자");
            if(dateData.length>0)
            {
                double dDate;
                int nTotCount = dateData.length;

                for(int i = 0 ; i < nTotCount ; i++) {
                    if(Double.parseDouble(strDate) <= Double.parseDouble(dateData[i]))
                    {
                        idx = i;
                        break;
                    }
                }
            }
            if(idx >=0){
                _cvm.curIndex = idx;

                int viewNum = _cvm.getViewNum();
                int cdmCount = _cdm.getCount();
                int startIdx = _cvm.getIndex();
                if(idx-startIdx<0)
                {
                    _cvm.setIndex(idx);
                }
                else if(idx>startIdx+viewNum)
                {
                    _cvm.setIndex(idx-viewNum+1);
                }
                repaintAll();
            }
        }
        catch (Exception e){

        }
    }

    public void setPaddingRight(int nPaddingRight)
    {
        _cvm.PADDING_RIGHT = nPaddingRight;
        if(blocks != null) {
            for (int i=0; i<blocks.size(); i++) {
                Block block = blocks.get(i);
                block.setPaddingRight(nPaddingRight);
            }
        }
        if(rotate_blocks != null) {
            for (int i=0; i<rotate_blocks.size(); i++) {
                Block block = rotate_blocks.get(i);
                block.setPaddingRight(nPaddingRight);
            }
        }
    }

    public void setBaselineData(String datas) {

        String [] token = datas.split(",");


        for(int i=0; i<token.length; i++)
        {
            String [] value = token[i].split(":");

            if(value.length >=2)
            {
                if(value[0].equals("code"))
                {
                    if(!_cdm.codeItem.strCode.equals(value[1]))
                        return;
                }
                if(value[0].equals("giOpen"))
                {
                    _cdm.codeItem.strGiOpen = value[1];
                }
                if(value[0].equals("giHigh"))
                {
                    _cdm.codeItem.strGiHigh = value[1];
                }
                if(value[0].equals("giLow"))
                {
                    _cdm.codeItem.strGiLow = value[1];
                }
                if(value[0].equals("giClose"))
                {
                    _cdm.codeItem.strGiClose = value[1];
                }
                if(value[0].equals("giPreOpen"))
                {
                    _cdm.codeItem.strGiPreOpen = value[1];
                }
                if(value[0].equals("giPreHigh"))
                {
                    _cdm.codeItem.strGiPreHigh = value[1];
                }
                if(value[0].equals("giPreLow"))
                {
                    _cdm.codeItem.strGiPreLow = value[1];
                }
                if(value[0].equals("giPreClose"))
                {
                    _cdm.codeItem.strGiPreClose = value[1];
                }
            }
        }
    }

    public void setSelected(boolean bSelected) {
        if (!bSelected) {
            ExpandReduceChartHandler.removeMessages(TAP);

            if (null != btnExpand)
                btnExpand.setVisibility(View.GONE);
            if (null != btnReduce)
                btnReduce.setVisibility(View.GONE);
            if (null != btnIndicatorList)
                btnIndicatorList.setVisibility(View.GONE);
            if (null != btnAll)
                btnAll.setVisibility(View.GONE);
        }
        m_bSelected = bSelected;
    }

    public boolean isSelected() {
        return m_bSelected;
    }

    //2016.12.14 by LYH >>갭보정 추가
    private boolean m_bUseGapRevision = false;
    private double m_dGap = 0;
    int m_nPrevCount = 0;
    double m_dAdjustedRate = 1;
    private void calcGapRevision(boolean bIsReset)
    {
        m_bUseGapRevision = _cvm.isGapRevision;
        if( !m_bUseGapRevision) return;

        // Market BaseTime 얻음
        String szMarketTime;
        szMarketTime = "000000";

        int nBaseTime = 0;

        ChartPacketDataModel cpdm = _cdm.getChartPacket("자료일자");
        if(cpdm == null)
            return;

        // 패킷타입 Check
        int nDateFormat = _cdm.getDateFormat();
        if( nDateFormat != XScale.DDHHMMSS && nDateFormat != XScale.MMDDHHMM)
        {
            return;
        }


        ChartPacketDataModel cpdmOpen = _cdm.getChartPacket("시가");
        ChartPacketDataModel cpdmHigh = _cdm.getChartPacket("고가");
        ChartPacketDataModel cpdmLow = _cdm.getChartPacket("저가");
        ChartPacketDataModel cpdmClose = _cdm.getChartPacket("종가");

        double[] fOpen = _cdm.getSubPacketData("시가");
        double[] fHigh = _cdm.getSubPacketData("고가");
        double[] fLow = _cdm.getSubPacketData("저가");
        double[] fClose = _cdm.getSubPacketData("종가");
        String[] dates=_cdm.getStringData("자료일자");

        if(fOpen == null || fHigh == null|| fLow == null|| fClose == null)
            return;

        //--------------------------------------------------------
        //>> 갭보정 처리
        double dTime, dOpen, dHigh, dLow, dClose;
        double dGap=0, dGapData;
        boolean bNeedCalcuate = false;

        int nPacketCount = fClose.length;
        int nCurIndx = 0;
        int nNextIndx = -1;
        int nCurDay = 0, nNextDay = 0, nCurTime=0, nNextTime=0;
        boolean bInitialized = false;

        if(bIsReset)
        {
            nCurIndx = nPacketCount - 1;
        }
        else
        {
            dGap = m_dGap;
            nCurIndx = nPacketCount - m_nPrevCount - 1;
            nNextIndx = nCurIndx + 1;
            if(m_dGap != 0)
                bNeedCalcuate = true;
            bInitialized = true;

            dTime = Integer.parseInt(dates[nNextIndx]);
            if(nDateFormat == XScale.MMDDHHMM)
            {
                nNextDay = (int)dTime/10000;
                nNextTime = (int)dTime%10000 * 100;
            }
            else if(nDateFormat == XScale.DDHHMMSS)
            {
                nNextDay = (int)dTime/1000000;
                nNextTime = (int)dTime%1000000;
            }
        }

        m_nPrevCount = nPacketCount;

        for(; nCurIndx >= 0; --nCurIndx)
        {
            dTime = Integer.parseInt(dates[nCurIndx]);
            if(nDateFormat == XScale.MMDDHHMM)
            {
                nCurDay = (int)dTime/10000;
                nCurTime = (int)dTime%10000 * 100;
            }
            else if(nDateFormat == XScale.DDHHMMSS)
            {
                nCurDay = (int)dTime/1000000;
                nCurTime = (int)dTime%1000000;
            }

            if(bInitialized == false)
            {
                bInitialized = true;
            }

            if( (nCurDay != nNextDay && nCurTime > nBaseTime && nNextTime >= nBaseTime) ||
                    (nCurDay == nNextDay && nCurTime < nBaseTime && nNextTime >= nBaseTime) ||
                    (nCurDay != nNextDay && nCurTime < nBaseTime && nNextTime >= nBaseTime) )
            //<<
            {
                if(nCurIndx != nPacketCount-1)
                {
                    double dOpenData, dCloseData;
                    dOpenData = fOpen[nNextIndx];
                    dCloseData = fClose[nCurIndx];
                    dGapData = (dOpenData - dGap) - dCloseData;
                    dGap += dGapData;
                    bNeedCalcuate = true;
                    m_dGap = dGap;
                }
            }

            if(bNeedCalcuate)
            {
                dOpen = fOpen[nCurIndx];
                dOpen += dGap;
                dHigh = fHigh[nCurIndx];
                dHigh += dGap;
                dLow = fLow[nCurIndx];
                dLow += dGap;
                dClose = fClose[nCurIndx];
                dClose += dGap;

                //NSLog(@"%d[%d] %.0f -> %.0f %.0f", dates[nCurIndx], nCurIndx, fOpen[nCurIndx], dOpen, dGap);
                fOpen[nCurIndx] = dOpen;
                fHigh[nCurIndx] = dHigh;
                fLow[nCurIndx] = dLow;
                fClose[nCurIndx] = dClose;

            }

            nNextIndx = nCurIndx;
            nNextDay = nCurDay;
            nNextTime = nCurTime;
        }
        //<< 갭보정 처리
    }

    public void changeGapRevision()
    {
        if(m_bUseGapRevision!=_cvm.isGapRevision)
        {
            m_bUseGapRevision = _cvm.isGapRevision;
            if(!m_bUseGapRevision)
            {
                Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
                COMUtil.setSendTrType("requestGapRevision");

                String strCode = COMUtil.symbol;
                Hashtable<String, Object> dic = new Hashtable<String, Object>();
//	            if(COMUtil.lcode.equals("SC0")) {
//	            	strCode = "1"+strCode;
//	            }
                if(strCode!=null && strCode.length()>0) {
                    dic.put("symbol", strCode);
                    dic.put("market", COMUtil.market);
                    dic.put("name", COMUtil.codeName);
                    dic.put("lcode", COMUtil.lcode);
                    dic.put("period", COMUtil.dataTypeName);

                    int viewNum = 100;
                    try {
                        viewNum = _cvm.getViewNum();
                    } catch (Exception e) {

                    }
                    dic.put("viewnum", String.valueOf(viewNum));
                    dic.put("count", COMUtil.count);
                    dic.put("unit", COMUtil.unit);
                    dic.put("apcode", COMUtil.apCode);
//	            int selectedIndex = base.m_nRotateIndex;
                    int selectedIndex = base11.getSelectedChartIndex();
                    dic.put("selIndex", String.valueOf(selectedIndex));

                    //delegate 처리
                    if (userProtocol != null)
                        userProtocol.requestInfo(COMUtil._TAG_STORAGE_TYPE, dic);
                }
            }
            else
            {
                calcGapRevision(true);
                makeGraphData();
            }
        }
    }
    //2016.12.14 by LYH >>갭보정 추가 end

    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
    public String m_strSizeInfo = null;
    private void setBlockRateInfo() {
        String rtnStr = "";
        for (int i = 0; i <blocks.size();i++){
            RectF bound =blocks.elementAt(i).getBounds();

            //2021.04.29 by lyk - kakaopay - 스크롤바 위치가 차트 하단일 경우 처리 (리사이즈 블럭 이벤트 발생시 xscale, eventbadge 포함하여 계산)
            double dHeightRate = (double)bound.height() / (double)(chart_bounds.height() -_cvm.Margin_T - _cvm.XSCALE_H - _cvm.EVENT_BADGE_H - COMUtil.getPixel(6));

            if (i ==blocks.size()-1)
                rtnStr += String.format("%.6f", dHeightRate);
            else
                rtnStr += String.format("%.6f=", dHeightRate);
        }

        m_strSizeInfo =rtnStr;

        if(COMUtil.isPeriodConfigSave()) {
            //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다)
            COMUtil.setSavePeriodChartSave();
            //2015.04.07 by lyk - 주기별 차트 설정 (지표값에 변동사항이 발생하면 주기별 설정 항목에 업데이트를 한다) end
            if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_SET_SAVECHART, null);
            nChangeFlag = 1; //클라우드 저장은 차트가 종료될 때 처리한다.
        }
    }
    //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장 end

    //2017.07.25 by pjm >> Y축 움직여 확대/축소
    private void setExpandReduce()
    {
        //2019. 05. 24 by hyh - Y축 움직여 확대/축소 기능 제거. 가로선은 표시 >>
//		int newViewNum = getUpDownViewNum();
//		int startIdx = m_nOrgIndex+(m_nOrgViewNum-newViewNum);
//
//		_cvm.setViewNum(newViewNum);
//		//2014.06.05 by LYH >> 시작 인덱스 음수값 들어가는 경우 생김 -> 음수인 경우 0으로 수정.
//		if(startIdx<0) {
//			startIdx = 0;
//		}
//		//2014.06.05 by LYH << 시작 인덱스 음수값 들어가는 경우 생김 -> 음수인 경우 0으로 수정.
//		_cvm.setIndex(startIdx);
        //2019. 05. 24 by hyh - Y축 움직여 확대/축소 기능 제거. 가로선은 표시 <<
    }

    private int getUpDownViewNum()
    {
        YScale yscale = basic_block.getYScale()[0];
        double dHeight = chart_bounds.top+chart_bounds.height() - (yscale.getBounds().top-(int)COMUtil.getPixel(10));
        if(dHeight<=0)
            return m_nOrgViewNum;

        double dUnit = (100/dHeight);
        int gab = (int)((pressPoint.y-curPoint.y)*dUnit);
        int rstNum = m_nOrgViewNum - gab;

        if (rstNum < _cvm.MIN_VIEW_NUM) {
            rstNum = _cvm.MIN_VIEW_NUM;
        }
        int cdmCount = _cdm.getCount();     //  데이터 총 갯수
        if (rstNum > cdmCount) {
            rstNum = cdmCount;
        }
        return  rstNum;
    }

    //2019. 01. 12 by hyh - 블록병합 처리 >>
    public void mergeBlock() {
        if(blocks.size() < 2) return;

        if(this.getMergeBlock()) return;

        isChangeBlock = true;
        reSetUI(true);
        isChangeBlock = false;
    }

    public boolean getMergeBlock() {
        Block selectedBlock = changable_block[0];
        Block targetedBlock = changable_block[1];

        //동일 블록 merge 불가
        if (selectedBlock == null || targetedBlock == null || selectedBlock == targetedBlock) {
            return false;
        }

        //기본블록은 항상 메인블록으로 처리
        if (selectedBlock.isBasicBlock()) {
            selectedBlock = changable_block[1];
            targetedBlock = changable_block[0];
        }

        //rotate blocks은 블럭을 이동하지 않게 함.
        if (rotate_blocks != null && rotate_blocks.size() > 0) {
            if (blocks != null && blocks.size() > 0) {
                if (blocks.lastElement().equals(selectedBlock) || blocks.lastElement().equals(targetedBlock)) {
                    return false;
                }
            }
        }

        //Merge graphs
        Vector<AbstractGraph> graphs = selectedBlock.getGraphs();
        Vector<String> arrGraphTitles = new Vector<String>();
        Vector<String[]> arrGraphValues = new Vector<String[]>();

        for (int nGraphIndex = 0; nGraphIndex < graphs.size(); nGraphIndex++) {
            AbstractGraph ag = graphs.get(nGraphIndex);
            String strGraphTitle = ag.getName();

            //중복지표 병합처리
            if (ag.getGraphTitle() != null && ag.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                strGraphTitle = ag.getGraphTitle();
            }

            String strGraphValue = this.getGraphValue2(ag, true);
            String[] strGraphValues = strGraphValue.split("=");

            arrGraphTitles.add(strGraphTitle);
            arrGraphValues.add(strGraphValues);
        }

        //Set BasicBlock before it would be removed.
        if (selectedBlock.isBasicBlock()) {
            basic_block = targetedBlock;
        }

        //Remove the block
        this.removeBlock(arrGraphTitles.firstElement());
        //selectedBlock.destroy();

        //Add and Set graph values
        for (int nGraphIndex=0; nGraphIndex < arrGraphTitles.size(); nGraphIndex++) {
            String strGraphTitle = arrGraphTitles.get(nGraphIndex); //[arrGraphTitles objectAtIndex:nGraphIndex];
            targetedBlock.add(strGraphTitle);

            String[] strGraphValues = arrGraphValues.get(nGraphIndex);
            int[] graphValues = new int[strGraphValues.length];
            for (int nIndex = 0; nIndex < graphValues.length; nIndex++) {
                try {
                    graphValues[nIndex] = Integer.parseInt(strGraphValues[nIndex]);
                } catch (NumberFormatException e) {
                }
            }

            AbstractGraph newAg = targetedBlock.getGraphs().lastElement(); //[[targetedBlock getGraphs] lastObject];
            newAg.changeControlValue(graphValues);
        }

        targetedBlock.makeGraphData();

        //Set arrMergedGraphs
        targetedBlock.resetMergedGraphs();

        isBlockChanged=true;
        if (basic_block != null) {
            select_dt = null;
            basic_block.makeGraphData();
            this.reSetUI(true);
        }

        return true;
    }

    private void makeNewBlockFromSelectedGraph() {
        String strGraphTitle = selectedMoveGraph.getName();

        //중복지표 병합처리
        if (selectedMoveGraph.getGraphTitle() != null && selectedMoveGraph.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
            strGraphTitle = selectedMoveGraph.getGraphTitle();
        }

        String strGraphValue = this.getGraphValue2(selectedMoveGraph, true);
        String[] strGraphValues = strGraphValue.split("=");
        int[] graphValues = new int[strGraphValues.length];
        for (int nIndex = 0; nIndex < graphValues.length; nIndex++) {
            try {
                graphValues[nIndex] = Integer.parseInt(strGraphValues[nIndex]);
            } catch (NumberFormatException e) {
            }
        }

        this.removeSelectedGraph();

        isBlockChanged = true;
        Block newCb = makeBlock(0, blocks.size(), strGraphTitle);
        newCb.setProperties("지표 Data", 1, _cvm.getScaleLineType());
        newCb.setBounds(0,(int)(getHeight()*0.2)+viewH,chart_bounds.width()-this.TP_WIDTH,(int)(chart_bounds.height()*0.2)-viewH,true);

        //Set values
        AbstractGraph newAg = newCb.getGraphs().lastElement();
        newAg.changeControlValue(graphValues);

        //삽입 할 블록 index를 찾는다.
        int nBlockIndex = -1;
        for (int nIndex = 0; nIndex < blocks.size(); nIndex++) {
            Block cb = blocks.get(nIndex);

            RectF rect = cb.getBounds(); //[cb getBoundsBlock];
            if(rect.contains(curPoint.x, curPoint.y)) {
                float fDistance = rect.top - curPoint.y;

                if (rect.bottom - curPoint.y > fDistance){
                    nBlockIndex = nIndex;
                }
                else {
                    nBlockIndex = nIndex - 1;
                }

                break;
            }
        }

        //첫 번째 블록으로는 추가 불가
        if (nBlockIndex <= 0) {
            nBlockIndex = 1;
        }

        addBlock(newCb, nBlockIndex);
        //addBlock(newCb);
        newCb.makeGraphData();

        if(COMUtil.isMarketIndicator(strGraphTitle)) {
            int cnt = _cdm.getCount();

            Hashtable<String, Object> dic = new Hashtable<String, Object>();
            dic.put("nTotCount", String.valueOf(cnt));
            dic.put("title", strGraphTitle);

            //delegate 처리
            if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_REQUEST_MARKET_TYPE, dic);
            COMUtil.setSendTrType(strGraphTitle); //차트 축소시 추가 데이터 요청 발생할때 필요.
        }

        setPivotState(pivotState);
    }

    private void moveSelectedGraph() {
        String strGraphTitle = selectedMoveGraph.getName();

        //중복지표 병합처리
        if (selectedMoveGraph.getGraphTitle() != null && selectedMoveGraph.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
            strGraphTitle = selectedMoveGraph.getGraphTitle();
        }

        String strGraphValue = getGraphValue2(selectedMoveGraph, true);
        String[] strGraphValues = strGraphValue.split("=");
        Block targetedBlock = targetedMoveBlock;

        this.removeSelectedGraph();
        targetedBlock.add(strGraphTitle);

        AbstractGraph newAg = targetedBlock.getGraphs().lastElement();

        int[] graphValues = new int[strGraphValues.length];
        for (int nIndex = 0; nIndex < graphValues.length; nIndex++) {
            try {
                graphValues[nIndex] = Integer.parseInt(strGraphValues[nIndex]);
            } catch (NumberFormatException e) {
            }
        }

        newAg.changeControlValue(graphValues);

        targetedBlock.resetMergedGraphs();
        targetedBlock.makeGraphData();

        isBlockChanged = true;
        if (basic_block != null) {
            select_dt = null;
            basic_block.makeGraphData();
            this.reSetUI(true);
        }
    }

    private void removeSelectedGraph() {
        String strGraphTitle = selectedMoveGraph.getName();

        //중복지표 병합처리
        if (selectedMoveGraph.getGraphTitle() != null && selectedMoveGraph.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
            strGraphTitle = selectedMoveGraph.getGraphTitle();
        }

        if ((rotate_blocks != null && rotate_blocks.size() > 0)
                && (blocks != null && blocks.size() > 0)
                && blocks.lastElement().equals(selectedMoveBlock)) { //Rotate Block
            removeBlock(strGraphTitle);
        }
        else if (selectedMoveBlock.getGraphs().size() == 1) {
            removeBlock(strGraphTitle);
        }
        else {
            selectedMoveBlock.removeGraph(strGraphTitle);
            selectedMoveBlock.resetMergedGraphs();

            if (selectedMoveBlock.getTitle().equals(strGraphTitle)) {
                AbstractGraph ag = selectedMoveBlock.getGraphs().firstElement();
                String strNewTitle = ag.getName();

                //중복지표 병합처리
                if (ag.getGraphTitle() != null && ag.getGraphTitle().contains(COMUtil.JIPYO_ADD_REMARK)) {
                    strNewTitle = ag.getGraphTitle();
                }

                selectedMoveBlock.setTitle(strNewTitle);
            }
        }

        isBlockChanged = true;

        if (basic_block != null) {
            select_dt = null;
            basic_block.makeGraphData();
            this.reSetUI(true);
        }

        selectedMoveBlock = null;
        targetedMoveBlock = null;
        selectedMoveGraph = null;
        selectedMoveDrawTool = null;
    }

    private void showChartBlockPopup(PointF point) {
        Block selectedBlock = changable_block[0];
        Block targetedBlock = changable_block[1];

        if (targetedBlock == null) {
            return;
        }

        if (selectedBlock == targetedBlock) {
            return;
        }

        if (chartBlockPopup == null) {
            chartBlockPopup = new ChartBlockPopup(this.context);
            chartBlockPopup.setChartFrame(this.getLayoutParams());

            layout.addView(chartBlockPopup);
        }

        if (targetedBlock.isBasicBlock()) {
            //버튼 설정
            chartBlockPopup.btn1.setText("블록병합");
            chartBlockPopup.btn1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mergeBlock();
                    saveLastState();
                    chartBlockPopup.setVisibility(GONE);
                }
            });

            chartBlockPopup.setUI(1);
        }
        else {
            //버튼 설정
            chartBlockPopup.btn1.setText("블록교체");
            chartBlockPopup.btn1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeBlock();
                    saveLastState();
                    chartBlockPopup.setVisibility(GONE);
                }
            });

            chartBlockPopup.btn2.setText("블록병합");
            chartBlockPopup.btn2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mergeBlock();
                    saveLastState();
                    chartBlockPopup.setVisibility(GONE);
                }
            });

            chartBlockPopup.setUI(2);
        }

        //팝업 설정
        chartBlockPopup.setVisibility(VISIBLE);
        chartBlockPopup.setPoint(point);
    }

    private void showGraphMovePopup(PointF point) {
        if (targetedMoveBlock == selectedMoveBlock) {
            return;
        }

        if (chartBlockPopup == null) {
            chartBlockPopup = new ChartBlockPopup(this.context);
            chartBlockPopup.setChartFrame(this.getLayoutParams());

            layout.addView(chartBlockPopup);
        }

        //이동불가 지표 확인
        boolean bIsMoveLockedGraph = false;
        for (String strName : moveLockedGraphList) {
            if (strName.equals(selectedMoveGraph.getName())) {
                bIsMoveLockedGraph = true;
            }
        }

        if (bIsMoveLockedGraph) {
            chartBlockPopup.btn1.setText("지표삭제");
            chartBlockPopup.btn1.setOnClickListener(onClickListenerGraphRemove);
            chartBlockPopup.setUI(1);
        }
        else if (targetedMoveBlock == null) {
            if (selectedMoveBlock.getGraphs().size() == 1) {
                chartBlockPopup.btn1.setText("지표삭제");
                chartBlockPopup.btn1.setOnClickListener(onClickListenerGraphRemove);
                chartBlockPopup.setUI(1);
            }
            else {
                chartBlockPopup.btn1.setText("새 블록");
                chartBlockPopup.btn1.setOnClickListener(onClickListenerMakeNewBlock);
                chartBlockPopup.btn2.setText("지표삭제");
                chartBlockPopup.btn2.setOnClickListener(onClickListenerGraphRemove);
                chartBlockPopup.setUI(2);
            }
        }
        else {
            if (selectedMoveBlock.getGraphs().size() == 1) {
                chartBlockPopup.btn1.setText("지표이동");
                chartBlockPopup.btn1.setOnClickListener(onClickListenerGraphMove);
                chartBlockPopup.btn2.setText("지표삭제");
                chartBlockPopup.btn2.setOnClickListener(onClickListenerGraphRemove);
                chartBlockPopup.setUI(2);
            }
            else {
                chartBlockPopup.btn1.setText("새 블록");
                chartBlockPopup.btn1.setOnClickListener(onClickListenerMakeNewBlock);
                chartBlockPopup.btn2.setText("지표이동");
                chartBlockPopup.btn2.setOnClickListener(onClickListenerGraphMove);
                chartBlockPopup.btn3.setText("지표삭제");
                chartBlockPopup.btn3.setOnClickListener(onClickListenerGraphRemove);
                chartBlockPopup.setUI(3);
            }
        }

        chartBlockPopup.setPoint(point);
        chartBlockPopup.setVisibility(VISIBLE);
    }

    private void hideChartBlockPopup() {
        if (chartBlockPopup != null) {
            chartBlockPopup.setVisibility(GONE);
        }

        selectedMoveBlock = null;
        targetedMoveBlock = null;
        selectedMoveGraph = null;
        selectedMoveDrawTool = null;
    }

    OnClickListener onClickListenerMakeNewBlock = new OnClickListener() {
        @Override
        public void onClick(View v) {
            makeNewBlockFromSelectedGraph();
            saveLastState();
            chartBlockPopup.setVisibility(GONE);
        }
    };

    OnClickListener onClickListenerGraphMove = new OnClickListener() {
        @Override
        public void onClick(View v) {
            moveSelectedGraph();
            saveLastState();
            chartBlockPopup.setVisibility(GONE);
        }
    };

    OnClickListener onClickListenerGraphRemove = new OnClickListener() {
        @Override
        public void onClick(View v) {
            removeSelectedGraph();
            saveLastState();
            chartBlockPopup.setVisibility(GONE);
        }
    };

    public void addBlock(Block b, int nAtIndex){
        if (blocks == null) blocks = new Vector<Block>();
        if (rotate_blocks == null) rotate_blocks = new Vector<Block>();
        if (title_list == null) title_list = new Vector<String>();

        int nBlockCnt = blocks.size();

        if (nAtIndex < 0 || nBlockCnt - 1 <= nAtIndex) {
            this.addBlock(b);
            return;
        }

        if (nBlockCnt > m_nMaxIndicatorCount) {
            Block lastBlock = blocks.get(nBlockCnt - 1);
            Block secondLastBlock = blocks.get(nBlockCnt - 2);

            if (lastBlock.getBlockType() != 2) {
                rotate_blocks.addElement(lastBlock);
                lastBlock.setHideDelButton(true);
                if (!title_list.contains(secondLastBlock.getTitle())) {
                    title_list.addElement(secondLastBlock.getTitle());
                }

                blocks.remove(lastBlock);
            } else {
                lastBlock.setHideDelButton(true);
            }
        }
        else if (nBlockCnt == m_nMaxIndicatorCount && rotate_blocks.size() < 1) {
            Block lastBlock = blocks.lastElement();

            if(!title_list.contains(lastBlock.getTitle())) {
                title_list.add(lastBlock.getTitle());
            }
            lastBlock.setTitleNumber(0, 0);
        }

        if (_cvm.isStandGraph())
            b.setHideDelButton(true);

        blocks.insertElementAt(b, nAtIndex);

        if (rotate_blocks != null && rotate_blocks.size() > 0) {
            setTitleNumber();
        }

        isBlockChanged = true;
    }

    private void saveLastState() {
        //2019. 07. 25 by hyh - 화면 내렸다가 올릴 때 지표설정 내용 저장되도록 처리 >>
        if (COMUtil._mainFrame.strFileName != null) {
            COMUtil.saveLastState(COMUtil._mainFrame.strFileName);
        }
        //2019. 07. 25 by hyh - 화면 내렸다가 올릴 때 지표설정 내용 저장되도록 처리 <<
    }
    //2019. 01. 12 by hyh - 블록병합 처리 <<

    //2019. 03. 14 by hyh - 테크니컬차트 개발 >>
    public String getTechnicalValues() {
        int nDataIndex = _cdm.getCount() - 1;
        String strReturn = "";

        for (Block cb : blocks) {
            for (AbstractGraph graph : cb.getGraphs()) {
                for (int nGraphIndex = 0; nGraphIndex < graph.getDrawTool().size(); nGraphIndex++) {
                    DrawTool dt = graph.getDrawTool().get(nGraphIndex);

                    String strTitle = dt.getTitle();
                    String strValue = "0";

                    if (dt.getTitle().contains("이평")) {
                        strValue = dt.getFormatData(nDataIndex, dt.getTitle() + nGraphIndex);

                        if (nGraphIndex < 5) {
                            strTitle = "단순" + strTitle;
                        }
                        else {
                            strTitle = "지수" + strTitle;
                        }
                    }
                    else {
                        strValue = dt.getFormatData(nDataIndex);
                    }

                    strReturn += strTitle + "=" + strValue + ";";
                }
            }
        }

        return strReturn;
    }
    //2019. 03. 14 by hyh - 테크니컬차트 개발 <<

    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 >>
    public void setFXData(String title, double[] marketData, boolean bReal) {
        if (bReal) {
            double price[] = _cdm.getSubPacketData("종가");
            if (price == null) return;
            int m_dataCnt = price.length;
            ChartPacketDataModel cpdm = _cdm.getChartPacket(title);

            if (cpdm == null) return;
            if (cpdm.getDataCount() < m_dataCnt) {

                cpdm.addRealData(String.valueOf(marketData[0]));
            }
            else {
                cpdm.setData(String.valueOf(marketData[0]));
            }
        }
        else {
            addGraph(title);
            double[] d = new double[_cdm.getCount()];
            try {
                System.arraycopy(marketData, 0, d, 0, _cdm.getCount());
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            _cdm.setSubPacketData(title, d);
            _cdm.setPriceFormat_Investor("x0.0001", title, 10, _cdm.nTradeMulti);

            this.repaintAll();
        }
    }
    //2019. 04. 08 by hyh - FX마진 데이터 조회 및 실시간 처리 <<

    private void hideViewPanel() {
        m_bShowToolTip = false;
        _cvm.isCrosslineMode = false;
        resetTitleBounds();
        repaintAll();

        if (viewP != null) {
            viewP.setVisibility(View.GONE);
//			if(shadowlayout != null) shadowlayout.setVisibility(View.GONE);
            blurViewlayout.setVisibility(View.GONE);
            blurView.setVisibility(View.GONE);
//			if((viewP instanceof ExViewPanel_LeftRightArrow)) {

//			}
        }

//		if (m_btnViewPanelAlarm != null) {
//			m_btnViewPanelAlarm.setVisibility(GONE);
//		}
//
//		if (m_btnViewPanelClose != null) {
//			m_btnViewPanelClose.setVisibility(GONE);
//		}
    }

    //2019. 04. 17 by hyh - 시세알람차트 개발 >>
    private void sendAlarmPriceToScreen(PointF point) {
        if (bIsInitAlarmChart && strAlarmValue.equals("")) {
            bIsInitAlarmChart = false;
            return;
        }

        if (!chart_bounds.contains(point.x, point.y) || (point.x == 0 && point.y == 0)) {
            return;
        }

        YScale yScale = basic_block.getYScale()[0];

        double dPrice = yScale.getChartPrice(point.y);
        String strPrice = String.valueOf(dPrice);
        strPrice = ChartUtil.getFormatedData(strPrice, _cdm.getPriceFormat(), _cdm);

        Hashtable<String, Object> dic = new Hashtable<String, Object>();
        dic.put("price", strPrice);

        if (userProtocol != null) userProtocol.requestInfo(COMUtil._TAG_TOUCH_ALARM_CHART, dic);

        strAlarmValue = "";
    }
    //2019. 04. 17 by hyh - 시세알람차트 개발 <<

    //2019. 04. 22 by hyh - 가상매매차트 개발. 지표리스트 >>
    public String getJipyoList() {
        String strReturn = "";

        for (Block cb : blocks) {
            for (AbstractGraph graph : cb.getGraphs()) {
                String strName = graph.getName();
                strReturn += strName + ";";
            }
        }

        return strReturn;
    }
    //2019. 04. 22 by hyh - 가상매매차트 개발. 지표리스트 <<

    //2019. 05. 24 by hyh - 시세알람차트 외부에서 수치조회창 위치설정 기능 추가 >>
    public void setAlarmValue(String strAlarmValue) {
        this.strAlarmValue = strAlarmValue;
    }
    //2019. 05. 24 by hyh - 시세알람차트 외부에서 수치조회창 위치설정 기능 추가 <<

    //2019. 07. 26 by hyh - 추세선 전체 삭제 시 자동추세선도 초기화 >>
    public void resetAutoTrend() {
        _cvm.autoTrendWaveType=0;
        _cvm.autoTrendHighType=0;
        _cvm.autoTrendLowType=0;
        _cvm.autoTrendWType=0;
        _cvm.preName = 3;
        _cvm.endName = 3;
        COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
    }
    //2019. 07. 26 by hyh - 추세선 전체 삭제 시 자동추세선도 초기화 <<

    Bitmap handler_image;
    private void showVertLineWithCircle_Asset(Canvas gl, int x, int y, Rect bounds, Vector<Hashtable<String, String>> datas)
    {
        if(_cdm.getCount()<1)
            return;
        int[] arLineColor = {63, 63, 63};
        _cvm.drawLine(gl, x,bounds.top,x,bounds.bottom-_cvm.XSCALE_H+(int)COMUtil.getPixel(12), arLineColor, 0.5f);
        int layoutResId = COMUtil.apiView.getContext().getResources().getIdentifier("ic_graph_handler", "drawable", COMUtil.apiView.getContext().getPackageName());
        try {
            if(handler_image==null)
                handler_image = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

            _cvm.drawImage(gl, x-(int)COMUtil.getPixel(9), bounds.bottom-_cvm.XSCALE_H+(int)COMUtil.getPixel(6), (int)COMUtil.getPixel(18), (int)COMUtil.getPixel(18), handler_image, 255);
        } catch (Exception e) {

        }
    }
    //2017.09.25 by LYH >> 자산 차트 적용 end
    public boolean getShowTooltip() {
        return m_bShowToolTip;
    }
    public void setShowTooltip(boolean bShow) {
        m_bShowToolTip = bShow;
    }

    private void calcAdjusted(boolean bIsReset)
    {
        if(!COMUtil.isAdjustedStock())
            return;
        //2020.11.23 by JJH >> 권리락/배당락 추가 start
        //double[] exRightTypeData = _cdm.getSubPacketData("락구분");
        double[] exRightRateData = _cdm.getSubPacketData("락비율");
        //2020.11.23 by JJH >> 권리락/배당락 추가 end
        // Market BaseTime 얻음

        double[] fOpen = _cdm.getSubPacketData("시가");
        double[] fHigh = _cdm.getSubPacketData("고가");
        double[] fLow = _cdm.getSubPacketData("저가");
        double[] fClose = _cdm.getSubPacketData("종가");
        double[] fVolume = _cdm.getSubPacketData("기본거래량");

        if(fOpen == null || fHigh == null|| fLow == null|| fClose == null)
            return;

        if(exRightRateData == null
                || exRightRateData.length < 1 || exRightRateData[0] == 0) {
            return;
        }
        //--------------------------------------------------------
        //>> 갭보정 처리
        double dOpen, dHigh, dLow, dClose, dVolume;
        double dRate;
        boolean bNeedCalcuate = false;

        int nPacketCount = fClose.length;
        int nCurIndx = 0;
        boolean bInitialized = false;

        if(bIsReset)
        {
            nCurIndx = nPacketCount - 1;
        }
        else
        {
            nCurIndx = nPacketCount - m_nPrevCount - 1;
            if(m_dAdjustedRate != 1)
                bNeedCalcuate = true;
            bInitialized = true;
        }

        m_nPrevCount = nPacketCount;

        for(; nCurIndx >= 0; --nCurIndx)
        {
            dRate = exRightRateData[nCurIndx];
            if(dRate==0)
                return;

            if(bInitialized == false)
            {
                bInitialized = true;
            }

            if(bNeedCalcuate)
            {
                dOpen = fOpen[nCurIndx];
                dOpen *= m_dAdjustedRate;
                dHigh = fHigh[nCurIndx];
                dHigh *= m_dAdjustedRate;
                dLow = fLow[nCurIndx];
                dLow *= m_dAdjustedRate;
                dClose = fClose[nCurIndx];
                dClose *= m_dAdjustedRate;

                //NSLog(@"%d[%d] %.0f -> %.0f %.0f", dates[nCurIndx], nCurIndx, fOpen[nCurIndx], dOpen, dGap);
                fOpen[nCurIndx] = dOpen;
                fHigh[nCurIndx] = dHigh;
                fLow[nCurIndx] = dLow;
                fClose[nCurIndx] = dClose;

                if(fVolume != null && fVolume.length>nCurIndx) {
                    dVolume = fVolume[nCurIndx];
                    dVolume /= m_dAdjustedRate;
                    fVolume[nCurIndx] = dVolume;
                }
            }

            if( dRate != 1 )
            {
                if(nCurIndx != nPacketCount-1)
                {
                    bNeedCalcuate = true;
                    m_dAdjustedRate *= dRate;
                }
            }
        }
        //<< 갭보정 처리
    }

    private void calcAdjusted_Compare(boolean bIsReset)
    {
        if(!COMUtil.isAdjustedStock())
            return;
        //2020.11.23 by JJH >> 권리락/배당락 추가 start
        //double[] exRightTypeData = _cdm.getSubPacketData("락구분");
        double[] exRightRateData = _cdm.getSubPacketData("락비율");
        //2020.11.23 by JJH >> 권리락/배당락 추가 end
        // Market BaseTime 얻음


        double[] fClose = _cdm.getSubPacketData(COMUtil.symbol);

        if(fClose == null)
            return;

        if(exRightRateData == null
                || exRightRateData.length < 1 || exRightRateData[0] == 0) {
            return;
        }
        //--------------------------------------------------------
        //>> 갭보정 처리
        double dOpen, dHigh, dLow, dClose, dVolume;
        double dRate;
        boolean bNeedCalcuate = false;

        int nPacketCount = fClose.length;
        int nCurIndx = 0;
        boolean bInitialized = false;

        if(bIsReset)
        {
            nCurIndx = nPacketCount - 1;
        }
        else
        {
            nCurIndx = nPacketCount - m_nPrevCount - 1;
            if(m_dAdjustedRate != 1)
                bNeedCalcuate = true;
            bInitialized = true;
        }

        m_nPrevCount = nPacketCount;

        for(; nCurIndx >= 0; --nCurIndx)
        {
            dRate = exRightRateData[nCurIndx];
            if(dRate==0)
                return;

            if(bInitialized == false)
            {
                bInitialized = true;
            }

            if(bNeedCalcuate)
            {

                dClose = fClose[nCurIndx];
                dClose *= m_dAdjustedRate;

                fClose[nCurIndx] = dClose;
            }

            if( dRate != 1 )
            {
                if(nCurIndx != nPacketCount-1)
                {
                    bNeedCalcuate = true;
                    m_dAdjustedRate *= dRate;
                }
            }
        }
        //<< 갭보정 처리
    }
    //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 >>
    private String getFormatDate(String date) {
        String retStr = date;

        retStr = retStr.replace(".", "");
        //자리수 체크
        if (retStr.length() != 8) {
            return retStr;
        }

        //숫자인지 체크
        boolean isNumeric = false;
        try {
            Integer.parseInt(retStr);
            isNumeric = true;
        } catch (Exception e) {
        }

        if (!isNumeric) {
            return retStr;
        }

        //요일 획득
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyyMMdd");
        Date sourceDate = null;
        try {
            sourceDate = inputFormatter.parse(retStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(sourceDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        String[] arrWeekDayString = {"", "일", "월", "화", "수", "목", "금", "토"};

        //형식 변환 (yyyyMMdd -> yyyy/MM/dd)
        SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy/MM/dd");
        retStr = outputFormatter.format(sourceDate) + " (" + arrWeekDayString[dayNum] + ")";

        return retStr;
    }
    //2019. 12. 19 by hyh - 수치조회창 자료일자 요일 추가 <<
    //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 Start
    private GestureDetector.OnGestureListener mScrollGestureListener = new GestureDetector.OnGestureListener() {
        @Override public boolean onSingleTapUp(MotionEvent e) {
            //  편의기능 추가 : 수치팝업창 활성화인 경우 - 2021.05.11 by lyk - kakaopay - 탭 이벤트시 활성화 >>
            if((_cvm.chartType == COMUtil.COMPARE_CHART || (COMUtil.isSetScreenViewPanel() && eventBadgeViewP == null && tradeViewP == null))) {
                pressPoint = new PointF((int) e.getX(), (int) e.getY());
                curPoint = pressPoint;

                m_bShowToolTip = !m_bShowToolTip;
                if(!m_bShowToolTip)
                {
                    hideViewPanel();
                } else {
                    if (!_cvm.isCrosslineMode && viewP != null) {
                        if (_cvm.chartType == COMUtil.COMPARE_CHART) {
                            //2012. 7. 16   롱클릭시 십자선데이터패널 표시
                            _cvm.isCrosslineMode = true;

                            viewP.showCloseButton(false);    // 닫기 버튼 보이게
                            //2021.05.27 by hanjun.Kim - kakaopay - 닫기버튼 안보이게 수정 >>

                            viewP.setVisibility(View.VISIBLE);
                            blurView.setVisibility(VISIBLE);
                        } else {
                            // 롱 클릭시 십자선 패널 보이게
                            Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
                            base11.showCrossLineLongClick(true, this);
                        }
                    }
                }
            }
            //  편의기능 추가 : 지표팝업창 활성화인 경우 - 2021.05.11 by lyk - kakaopay - 탭 이벤트시 활성화 >>
            return false;
        }
        @Override public void onShowPress(MotionEvent e) {}
        @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
        @Override public void onLongPress(MotionEvent e) {}
        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(mScroller != null) {
                mScroller.forceFinished(true);
                if (Math.abs(velocityX) > 1000) {
                    int dx = (int) velocityX / 10;

                    mScroller.startScroll((int) e1.getRawX(), 0, dx, 0, 500);
                    pressPoint = new PointF((int) e1.getX(), (int) e1.getY());

                    //rotate block 스와이프 제스쳐로 기능 변경함
                    if(rotate_blocks!=null && _cvm.isStandGraph()==false) {
                        Block cb = null;
                        PointF p = pressPoint;
                        int nBlockCnt = blocks.size();
                        if(nBlockCnt>m_nMaxIndicatorCount) {
                            cb = (Block)blocks.get(nBlockCnt-1);
                            //2012. 7. 26 보조지표가 여러개 겹쳐 있을 때 드래그로는 토글되지 않게 수정
                            int nDistance = Math.abs((int)e1.getX() - nDownX);
                            if(cb.getBounds().contains(p.x, p.y) && nDistance < COMUtil.getPixel(4)) {
                                if(rotate_blocks.size()>0) {
//									isRotateBlock = true;
                                    //2012.07.09 by LYH>> rolling 기능
                                    m_nRollIndex++;
                                    if(m_nRollIndex > rotate_blocks.size())
                                        m_nRollIndex = 0;
                                    int nIndex = 0;

                                    String title = title_list.get(m_nRollIndex);
                                    for(int i=0; i<rotate_blocks.size(); i++)
                                    {
                                        if(((Block)rotate_blocks.get(i)).getTitle().equals(title))
                                        {
                                            nIndex = i;
                                            break;
                                        }
                                    }
                                    Block block = (Block)rotate_blocks.get(nIndex);
                                    rotate_blocks.remove(block);
                                    block.setHideDelButton(false);
                                    block.makeGraphData();
                                    addBlock(block);
//	    	        						setPivotState(pivotState);
                                    resetTitleBounds();
                                    repaintAll();

                                }
                            }
                        }
                    }
                    //
                }
            }

            return false;
        }
        @Override public boolean onDown(MotionEvent e) {
            if(mScroller != null) {
                mScroller.forceFinished(true);
                mScroller.abortAnimation();
            }
            return false;
        }
    };

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            PointF point = new PointF(mScroller.getCurrX(), mScroller.getCurrY());
            setMouseDrag_sbMove(point, true);
        }
    }
    //2018.06.21 by sdm >> 차트 좌,우 스크롤 성능개선 End

    //2017.09.25 by LYH >> 자산 차트 적용
    //====================================
    // 차트 블럭 그리기
    //====================================
    public void drawBlocks_Asset(Canvas gl) {
//		int[] nRectLineColor = {214,214,214};
//		if (xscale == null || blocks == null) return;
//		if (_cdm.getCount() < 1) {
//			for (int i = 0; i < blocks.size(); i++) {
//				Block cb = (Block) blocks.elementAt(i);
//				//2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 >>
////                if(!COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) && _cvm.getSkinType() == COMUtil.SKIN_WHITE && !_cvm.bIsMiniBongChart) {
//				if (_cvm.getSkinType() == COMUtil.SKIN_WHITE && !_cvm.bIsMiniBongChart) {
//					//2013. 6. 5  (미래에셋태블릿) 시장정보 차트  차트영역 흰색으로 안그리던 현상 수정 <<
//					Rect out_graph_bounds = cb.getOutBounds();
//					_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//				}
//				if(cb.getYScalePos()!=3)	//스케일 없음이 아닌 경우만 그림
//					cb.drawBound(gl);
//			}
//		} else {
//			for (int i = 0; i < blocks.size(); i++) {
//				Block cb = (Block) blocks.elementAt(i);
//				Rect out_graph_bounds = cb.getOutBounds();
//				_cvm.drawFillRect(gl, out_graph_bounds.left, out_graph_bounds.top, out_graph_bounds.width(), out_graph_bounds.height(), CoSys.WHITE, 1.0f);
//			}
//			xscale.draw(gl);
//			for (int i = 0; i < blocks.size(); i++) {
//				Block cb = (Block) blocks.elementAt(i);
//				cb.draw(gl);
//				if(_cvm.getAssetType().equals("PercentBar") && _cdm.accrueNames != null && _cdm.accrueNames.length==2)
//					continue;;
//				cb.drawBound(gl);
//			}
//		}

    }

    //2015. 7. 7 애니메이션 라인차트>>
    @Override
    public void postInvalidateToChart() {
        //postInvalidate후, neochart 의 draw 함수에서 bitmap만 refresh할 수 있게 m_bIsBitmapRefresh를 true 로 변경(이걸 하지 않으면 계속 DrawTool 관련된 로직이 수행된다)
        m_bIsBitmapRefresh = true;
        postInvalidate();
    }
    //2015. 7. 7 애니메이션 라인차트<<

    //2016.05.16 by LYH >> 1분선차트 애니메이션 추가.
    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        m_bIsBitmapRefresh = false;
        setInitMode(false);
        repaintAll();
    }

    public void setInitMode(boolean bInit)
    {
        if(blocks != null) {
            for (int i=0; i<blocks.size(); i++) {
                Block block = blocks.get(i);
                block.setInitMode(bInit);
            }
        }
    }
    //2016.05.16 by LYH << 1분선차트 애니메이션 추가.
    private void showTradePanel() {
        ATradeTool tradeTool = null;
        if (select_at != null && select_at.getTitle().equals("Trade")) {
            tradeTool = (ATradeTool) select_at;
        } else
            return;
        if (tradeViewP != null) {
            tradeViewP.setVisibility(View.VISIBLE);
        } else {
            tradeViewP = new ViewPanel(context, this.layout);
            tradeViewP.bTradeData = true;
//			RectF bounds = new RectF(0, 0, 0, 0);
            this.layout.addView(tradeViewP);
        }

        int x = (int)tradeTool.getXPos();
        int y = (int)tradeTool.getYPos();

        viewDatas = new Vector<Hashtable<String, String>>();

//		String titleArray[] = {"매수매도구분", "자료일자", "평균", "수량"};
        String dataArray[] = tradeTool.getTradeData();
//		for (int i = 0; i < titleArray.length; i++) {
//			viewDatas.add(getViewPanelItem(titleArray[i], dataArray[i]));
//		}
        viewDatas.add(getViewPanelItem("자료일자", dataArray[1]));
//		viewDatas.add(getViewPanelItem("매수매도구분", dataArray[0]));

        String quantityText = "";
        String quantityName = "";
        if (dataArray[0].equals("0")) {
            quantityName = "매수건";
            quantityText = "주 구매";
        } else {
            quantityName = "매도건";
            quantityText = "주 판매";
        }

//		viewDatas.add(getViewPanelItem(quantityName, ChartUtil.getFormatedData(dataArray[3], 12, _cdm) + quantityText));

        try {
            String strQuantity = COMUtil.format(dataArray[3], 4, 3);
            strQuantity = strQuantity.contains(".") ? strQuantity.replaceAll("0*$","").replaceAll("\\.$","") : strQuantity;
            viewDatas.add(getViewPanelItem(quantityName, strQuantity + quantityText));
        } catch (Exception e) {

        }

        String unitHead = "";
        String unitTail = "";
        if(_cdm.nTradeMulti>0) {
            unitHead = "$";
            unitTail = "";
        } else {
            unitHead = "";
            unitTail = "원";
        }
        viewDatas.add(getViewPanelItem("평균", unitHead+ChartUtil.getFormatedData(dataArray[2], _cdm.getPriceFormat(), _cdm)+unitTail));

        int listNum = this.viewDatas.size();    //2014.05.14 by LYH >> 툴팁 UI개선.
        //2012. 7. 9  십자선데이터 패널  크기조절
        //2012. 8. 10  뷰패널크기  dip단위 적용 : VP09
//    	int viewP_width = (int)COMUtil.getPixel(120);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
//		int viewP_width = (int) COMUtil.getPixel(150);
        int viewP_width = (int) COMUtil.getPixel(100);
        //2021.11.05 by JHY - 가격 자릿수 별 box(viewpanel) 사이즈 변경 >>

        //2021.11.05 by lyk - 매매내역 인포뷰 넓이 동적 처리 >>
        float max_width = viewP_width;
        for(int i=0; i<viewDatas.size(); i++) {
            String strValue = viewDatas.get(i).toString();
            if(strValue != null && !strValue.equals("")) {
                float itemWidth = _cvm.getFontWidth(strValue, (int)COMUtil.getPixel(11));
                //소수점 포함하여 크기 보정
                float plusWidth = 0;
                if(i==2 && _cdm.nTradeMulti > 0) {
                    plusWidth = _cvm.getFontWidth(" ", (int)COMUtil.getPixel(11)) * _cdm.nTradeMulti;
                    itemWidth += (plusWidth  + (int)COMUtil.getPixel(25));
                }
                else if(i==2 && _cdm.nTradeMulti == 0) {
                    itemWidth += (int)COMUtil.getPixel(35);
                }
                if(itemWidth > max_width) {
                    max_width = itemWidth;
                }
                viewP_width = (int) max_width + (int)COMUtil.getPixel(12);
            }
        }
        //2021.11.05 by lyk - 매매내역 인포뷰 넓이 동적 처리 <<

//		if(viewDatas.lastElement().get("평균").length()>3){
//			int pw =viewDatas.lastElement().get("평균").length();
//			pw = (pw-3)*5;
//			viewP_width = (int) COMUtil.getPixel(100+pw);
//		}
        //2021.11.05 by JHY - 가격 자릿수 별 box(viewpanel) 사이즈 변경 <<
        //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 >>
//		int viewP_height = (int) COMUtil.getPixel(80);
        int viewP_height = (int) COMUtil.getPixel(110);
        //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 <<
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        int panelMargin = (int) COMUtil.getPixel(5);
        left = x - viewP_width - (int) COMUtil.getPixel(7);
        right = left + viewP_width + param.leftMargin;
        top = y - (viewP_height + panelMargin)/2;
        bottom = y + (viewP_height + panelMargin)/2;

        if(top < (int) COMUtil.getPixel(1)) {
            top = (int) COMUtil.getPixel(2);
            bottom = (viewP_height + panelMargin) + (int) COMUtil.getPixel(2);
        }

        if (x < viewP_width) {
            //2012. 8. 10  뷰패널 x 좌표   dip단위 적용 : VP09
            left = x + (int) COMUtil.getPixel(7);
            right = left + viewP_width;
            tradeViewP.showArrowToCrossline(true, true);
            tradeViewP.setViewPanelRightDirection(true);
        } else {
            tradeViewP.showArrowToCrossline(true, false);
            tradeViewP.setViewPanelRightDirection(false);
        }

//		if (y < viewP_height) {
//			//2012. 8. 10  뷰패널 y 좌표   dip단위 적용  : VP09
//			top = y + (int) COMUtil.getPixel(10);
//		}


        RectF bounds = new RectF(left + param.leftMargin, top, right, bottom);

        tradeViewP.setBounds(bounds);
        tradeViewP.setProcessPresentData(null, viewDatas, false);
    }

    //2021.04.21 by hanjun.Kim - kakaopay - eventbadge 배지버튼 위치및 데이터초기화 >>
    public void resetCorporateCalendarData() {
        if(aBadgeTools != null) {
            aBadgeTools.removeAllElements();
        }
        if(eventBadgeDataList != null) {
            eventBadgeDataList.removeAllElements();
        }
    }

    public void setCorporateCalendarData(String strBadgeData) {
        if (aBadgeTools == null) {
            aBadgeTools = new Vector<ABadgeTool>();
        }
//		else {
//			aBadgeTools.removeAllElements();
//		}

        if (eventBadgeDataList == null) {
            eventBadgeDataList = new Vector<String>();
        }

        if (eventBadgeViewP != null ) {
            eventBadgeViewP.setVisibility(View.GONE);
        }
        if (strBadgeData != null) {
            String[] values = strBadgeData.split(";");
            if (values.length >= 2) {
                Block cb = (Block) blocks.get(0);
                ABadgeTool at;
                String strOneData;
                for (int i = 1; i < values.length; i++) {
                    strOneData = values[i];
                    if(!eventBadgeDataList.contains(strOneData)) { //동일 데이터가 있으면 스킵
                        String[] strOneArray = strOneData.split("\\^");
                        if (strOneArray.length >= 3) {
                            at = new ABadgeTool(cb);
                            at.addBadgeInfo(strOneData);
                            aBadgeTools.addElement(at);
                        }

                        if(eventBadgeDataList != null) {
                            eventBadgeDataList.addElement(strOneData);
                        }
                    }
                }
            }
        }
        repaintAll();
    }
    //2021.04.21 by hanjun.Kim - kakaopay - eventbadge 배지버튼 위치및 데이터초기화 <<

    //2021.04.27 by hanjun.Kim - kakaopay - 배지 뷰패널 >>
    private void showEventBadgePanel() {
        ABadgeTool badgeTool = null;
        if (select_at != null && select_at.getTitle().equals("Badge")) {
            badgeTool = (ABadgeTool) select_at;
        } else
            return;

        if (eventBadgeViewP != null) {
            eventBadgeViewP.setVisibility(View.VISIBLE);
        } else {
            eventBadgeViewP = new ViewPanel(context, this.layout);
            eventBadgeViewP.bEventBadgeData = true;
//			RectF bounds = new RectF(0, 0, 0, 0);
//			viewP.setBounds(bounds);
            this.layout.addView(eventBadgeViewP);
        }

        int nBtnX = (int)badgeTool.getXPos();
        int nBtnY = (int)badgeTool.getYPos();

        viewDatas = new Vector<Hashtable<String, String>>();
        String dataArray[] = badgeTool.getBadgeData();
        String dataType = dataArray[0];

        // 기본 사이즈 값
        int viewP_width = (int) COMUtil.getPixel(120);
        int viewP_height = (int) COMUtil.getPixel(93);

        // 서버에서 받을 데이터 포멧
        SimpleDateFormat server_format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat display_format = new SimpleDateFormat("yyyy. M. d.");
        SimpleDateFormat s_format = new SimpleDateFormat("yyyy년 MM월 실적발표");

        String display_date = dataArray[1];
        try {
            Date server_date = server_format.parse(dataArray[1]);
            display_date = display_format.format(server_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int listNum = viewDatas.size();
        // 데이터 타입에 따른 viewpanel 분기 처리. S 실적발표, B 배당발표, K 공시
        // 2차 수정. D 권리, E 재무 DE 권리재무날짜 겹치는 날
        if (dataType.equals("D") || dataType.equals("DE")) {
            viewDatas.add(getViewPanelItem("D_date", display_date));
            viewDatas.add(getViewPanelItem("D_현금배당", dataArray[2]));
            String eventbadgeString = dataArray[4]+dataArray[3];

			/*
			Stock split
			Stock consolidation
			Rights issue in same stock
			Rights issue in different stock
			*/
//			if ((dataArray[3]+dataArray[4]).length() > 21) {
            if(!dataArray[2].equals("Stock split") && !dataArray[2].equals("Stock consolidation") && !dataArray[2].equals("Rights issue in same stock") && !dataArray[2].equals("Rights issue in different stock")) {
                viewDatas.add(getViewPanelItem("D_multiline", eventbadgeString));
                viewP_width = (int) COMUtil.getPixel(148);
                //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 >>
//					viewP_height = (int) COMUtil.getPixel(70);
                viewP_height = (int) COMUtil.getPixel(110);
                //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 <<
            } else {
                viewP_width = (int) COMUtil.getPixel(148);
                //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 >>
//					viewP_height = (int) COMUtil.getPixel(50);
                viewP_height = (int) COMUtil.getPixel(90);
                //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 <<
            }
//				viewDatas.add(getViewPanelItem("D_multiline", context.getString(R.string.viewpanel_rightdata_hint)));

//			} else {
//				viewDatas.add(getViewPanelItem("",eventbadgeString));
//				viewP_width = (int) COMUtil.getPixel(148);
//				viewP_height = (int) COMUtil.getPixel(70);
//			}
            //2023.02.15 by LYH - D타입 여러개 오는 경우 처리 >>
            for (int i=0; i<aBadgeTools.size(); i++) {
                ABadgeTool at = aBadgeTools.elementAt(i);
                if(at != select_at && at.getBadgeData()[1].equals(dataArray[1]))
                {
                    dataArray = at.getBadgeData();
                    viewDatas.add(getViewPanelItem("D_현금배당", dataArray[2]));
                    eventbadgeString = dataArray[4]+dataArray[3];

                    if(!dataArray[2].equals("Stock split") && !dataArray[2].equals("Stock consolidation") && !dataArray[2].equals("Rights issue in same stock") && !dataArray[2].equals("Rights issue in different stock")) {
                        viewDatas.add(getViewPanelItem("D_multiline", eventbadgeString));
                        viewP_height += (int) COMUtil.getPixel(40);
                        //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 <<
                    } else {
                        viewP_height += (int) COMUtil.getPixel(20);
                    }
                }
            }
            //2023.02.15 by LYH - D타입 여러개 오는 경우 처리 <<
        }
        if (dataType.equals("E") || dataType.equals("DE")) {
            String financialDate = dataArray[2]; //  재무년월(S타입일때만) // 나중에 데이터가 따로 넘어오면 처리
            String businessProfit = dataArray[3];
            String businessProfitEarning = dataArray[4];
            String businessProfitRateYoy = dataArray[5];
            String businessProfitRateQoq = dataArray[6];
            String netProfit = dataArray[7];
            String netProfitEarning = dataArray[8];
            String netProfitRateYoy = dataArray[9];
            String netProfitRateQoq = dataArray[10];
            if (dataType.equals("DE")) {
                viewDatas.add(getViewPanelItem("VIEW_LINE", ""));
                financialDate = dataArray[5]; //  재무년월(S타입일때만) // 나중에 데이터가 따로 넘어오면 처리
                businessProfit = dataArray[6];
                businessProfitEarning = dataArray[7];
                businessProfitRateYoy = dataArray[8];
                businessProfitRateQoq = dataArray[9];
                netProfit = dataArray[10];
                netProfitEarning = dataArray[11];
                netProfitRateYoy = dataArray[12];
                netProfitRateQoq = dataArray[13];
            }

            try { Double.parseDouble(businessProfit); } catch(Exception e) { businessProfit = "0"; }
            try { Double.parseDouble(businessProfitEarning); } catch(Exception e) { businessProfitEarning = "0"; }
            try { Double.parseDouble(businessProfitRateYoy); } catch(Exception e) { businessProfitRateYoy = "0"; }
            try { Double.parseDouble(businessProfitRateQoq); } catch(Exception e) { businessProfitRateQoq = "0"; }
            try { Double.parseDouble(netProfit); } catch(Exception e) { netProfit = "0"; }
            try { Double.parseDouble(netProfitEarning); } catch(Exception e) { netProfitEarning = "0"; }
            try { Double.parseDouble(netProfitRateYoy); } catch(Exception e) { netProfitRateYoy = "0"; }
            try { Double.parseDouble(netProfitRateQoq); } catch(Exception e) { netProfitRateQoq = "0"; }

            // 값 단위 천원

            String sBusinessProfit = String.valueOf(Double.parseDouble(businessProfit));
            businessProfit = convertDecimalFormat(sBusinessProfit) + "천원";

            String sNetProfit = String.valueOf(Double.parseDouble(netProfit));
            netProfit = convertDecimalFormat(sNetProfit) + "천원";


            SimpleDateFormat financialdate_format = new SimpleDateFormat("yyyyMM");

            try {
                financialDate = s_format.format(financialdate_format.parse(financialDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //2022.02.11 by lyk - 소수점 2자리만 비교 >>
            String strBusinessProfitEarning = businessProfitEarning.replace("+", "");
            strBusinessProfitEarning = String.format("%.2f", Double.parseDouble(strBusinessProfitEarning));
            if ( Double.parseDouble(strBusinessProfitEarning) > 0 ) {
                businessProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_over), strBusinessProfitEarning);
            } else if ( Double.parseDouble(strBusinessProfitEarning) < 0 ) {
                strBusinessProfitEarning = strBusinessProfitEarning.replace("-", "");
                businessProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_under), strBusinessProfitEarning);
            } else {
                strBusinessProfitEarning = strBusinessProfitEarning.replace("-", "");
                businessProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strBusinessProfitEarning);
            }

            String strBusinessProfitRateYoy = businessProfitRateYoy.replace("+", "");
            strBusinessProfitRateYoy = String.format("%.2f", Double.parseDouble(strBusinessProfitRateYoy));
            if ( Double.parseDouble(strBusinessProfitRateYoy) > 0 ) {
                businessProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_compared_plus), strBusinessProfitRateYoy);
            } else if ( Double.parseDouble(strBusinessProfitRateYoy) < 0 ) {
                strBusinessProfitRateYoy = strBusinessProfitRateYoy.replace("-", "");
                businessProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_compared_minus), strBusinessProfitRateYoy);
            } else {
                strBusinessProfitRateYoy = strBusinessProfitRateYoy.replace("-", "");
                businessProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strBusinessProfitRateYoy);
            }

            String strBusinessProfitRateQoq = businessProfitRateQoq.replace("+", "");
            strBusinessProfitRateQoq = String.format("%.2f", Double.parseDouble(strBusinessProfitRateQoq));
            if ( Double.parseDouble(strBusinessProfitRateQoq) > 0 ) {
                businessProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_compared_plus), strBusinessProfitRateQoq);
            } else if ( Double.parseDouble(strBusinessProfitRateQoq) < 0 ) {
                strBusinessProfitRateQoq = strBusinessProfitRateQoq.replace("-", "");
                businessProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_compared_minus), strBusinessProfitRateQoq);
            } else {
                strBusinessProfitRateQoq = strBusinessProfitRateQoq.replace("-", "");
                businessProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strBusinessProfitRateQoq);
            }

            //2022.02.11 by lyk - 소수점 2자리만 비교 >>
            String strNetProfitEarning = netProfitEarning.replace("+", "");
            strNetProfitEarning = String.format("%.2f", Double.parseDouble(strNetProfitEarning));
            if ( Double.parseDouble(strNetProfitEarning) > 0 ) {
                netProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_over), strNetProfitEarning);
            } else if ( Double.parseDouble(strNetProfitEarning) < 0 ) {
                strNetProfitEarning = strNetProfitEarning.replace("-", "");
                netProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_under), strNetProfitEarning);
            } else {
                strNetProfitEarning = strNetProfitEarning.replace("-", "");
                netProfitEarning = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strNetProfitEarning);
            }

            String strNetProfitRateYoy = netProfitRateYoy.replace("+", "");
            strNetProfitRateYoy = String.format("%.2f", Double.parseDouble(strNetProfitRateYoy));
            if ( Double.parseDouble(strNetProfitRateYoy) > 0 ) {
                netProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_compared_plus), strNetProfitRateYoy);
            } else if ( Double.parseDouble(strNetProfitRateYoy) < 0 ) {
                strNetProfitRateYoy = strNetProfitRateYoy.replace("-", "");
                netProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_compared_minus), strNetProfitRateYoy);
            } else {
                strNetProfitRateYoy = strNetProfitRateYoy.replace("-", "");
                netProfitRateYoy = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strNetProfitRateYoy);
            }

            String strNetProfitRateQoq = netProfitRateQoq.replace("+", "");
            strNetProfitRateQoq = String.format("%.2f", Double.parseDouble(strNetProfitRateQoq));
            if ( Double.parseDouble(strNetProfitRateQoq) > 0 ) {
                netProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_compared_plus), strNetProfitRateQoq);
            } else if ( Double.parseDouble(strNetProfitRateQoq) < 0 ) {
                strNetProfitRateQoq = strNetProfitRateQoq.replace("-", "");
                netProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_compared_minus), strNetProfitRateQoq);
            } else {
                strNetProfitRateQoq = strNetProfitRateQoq.replace("-", "");
                netProfitRateQoq = String.format(context.getString(R.string.viewpanel_typeE_expect_same), strNetProfitRateQoq);
            }

            if (!dataType.equals("DE"))
                viewDatas.add(getViewPanelItem("재무발표일", display_date));
            viewDatas.add(getViewPanelItem("재무년월", financialDate));

            viewDatas.add(getViewPanelItem("영업이익", businessProfit));
            viewDatas.add(getViewPanelItem("예상치", businessProfitEarning));
            viewDatas.add(getViewPanelItem("작년대비", businessProfitRateYoy));
            viewDatas.add(getViewPanelItem("지난분기대비", businessProfitRateQoq));
            viewDatas.add(getViewPanelItem("VIEW_LINE", ""));
            viewDatas.add(getViewPanelItem("순이익", netProfit));
            viewDatas.add(getViewPanelItem("예상치", netProfitEarning));
            viewDatas.add(getViewPanelItem("작년대비", netProfitRateYoy));
            viewDatas.add(getViewPanelItem("지난분기대비", netProfitRateQoq));

            viewP_width = (int) COMUtil.getPixel(119);
            //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 >>
//			viewP_height = (int) COMUtil.getPixel(200);
            viewP_height = (int) COMUtil.getPixel(240);
            //2023.02.10 by SJW - 매매내역/기업 뱃지 인포뷰 잘려보이는 현상 수정 <<

            //2021.11.03 by JHY- 자릿수 별 box 크기 조정 >>
            int len = Math.max(businessProfit.length(), netProfit.length() - 1);
            if (len > 6) viewP_width = (int) COMUtil.getPixel(119 + ((len - 6) * 3));
            //2021.11.03 by JHY- 자릿수 별 box 크기 조정 <<

            if (dataType.equals("DE")) {
                viewP_width = (int) COMUtil.getPixel(148);
                viewP_height = (int) COMUtil.getPixel(245);
            }
        }


        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

//		left = nBtnX - viewP_width - (int) COMUtil.getPixel(10);
//		left = nBtnX - viewP_width +  (int) COMUtil.getPixel(20);;
        int panelMargin = (int) COMUtil.getPixel(5);
        int arrowHeight = (int) COMUtil.getPixel(12);
        boolean isArrowDisabled = false;

        left = nBtnX - (viewP_width + panelMargin)/2;
        top = nBtnY - viewP_height - panelMargin - (arrowHeight/2);
        right = nBtnX + (viewP_width + panelMargin)/2;
        bottom = top + viewP_height + panelMargin + (arrowHeight/2);
        //2023.02.28 by SJW - 갤럭시 폴드 축소 상태에서 기업 캘린더 인포윈도우가 상단 영역에 일부 가려진 상태로 노출되는 현상 수정 >>
        Rect windowRect = COMUtil.getWindowRect();
        int mDisHeight = windowRect.height();
        if (mDisHeight>800 && mDisHeight<1050) {
            top += (int) COMUtil.getPixel(50);
            bottom += (int) COMUtil.getPixel(50);
        }
        //2023.02.28 by SJW - 갤럭시 폴드 축소 상태에서 기업 캘린더 인포윈도우가 상단 영역에 일부 가려진 상태로 노출되는 현상 수정 <<
        if ( left < 0 ) {
            left = (int) COMUtil.getPixel(2);
            right = (int) COMUtil.getPixel(2) + viewP_width + panelMargin;
            isArrowDisabled = true;
        } else if ( nBtnX > chart_bounds.width() - (viewP_width + panelMargin)/2) {
            left = (int) (chart_bounds.width() - (viewP_width + panelMargin) - (int) COMUtil.getPixel(2));
            right = (int) (chart_bounds.width() - (int) COMUtil.getPixel(2));
            isArrowDisabled = true;
        }

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) getLayoutParams();
        RectF bounds = new RectF(left + param.leftMargin, top + param.topMargin, right + param.leftMargin, param.topMargin + bottom);

        eventBadgeViewP.setBounds(bounds);
        eventBadgeViewP.setProcessPresentData(viewDatas, isArrowDisabled);
    }
    //2021.04.27 by hanjun.Kim - kakaopay - 배지 뷰패널 <<

    //2021. 06. 05 by hyh - 지표 잔상 효과 개발 >>
    Bitmap getCapturedBitmap() {
//		Base baseP = COMUtil._mainFrame.mainBase.baseP;
//		Bitmap bitmap = Bitmap.createBitmap(baseP.frame.width(), baseP.frame.height(), Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bitmap);
//		drawChart(canvas);
//
//		return bitmap;

        Base baseP = COMUtil._mainFrame.mainBase.baseP;
        RelativeLayout v1 = baseP._chart.layout;
        v1.setDrawingCacheEnabled(true);
        Bitmap mainImg = Bitmap.createBitmap(v1.getDrawingCache());
//		Bitmap mBack = Bitmap.createBitmap(baseP.frame.width()-10, baseP.frame.height(), Bitmap.Config.RGB_565);
//		Canvas c = new Canvas(mBack);
        v1.setDrawingCacheEnabled(false);
        return mainImg;
    }

    private String getFormatDateString(String sValue) {
        String rtnValue = sValue;
        String dateFormat = "";
        String dateSFormat = "";
        if (_cdm.codeItem.strDataType.equals("2") || _cdm.codeItem.strDataType.equals("3")) {
            dateFormat = "yyyy. M. d.";
            dateSFormat = "yyyyMMdd";
        } else if (_cdm.codeItem.strDataType.equals("4")) {
            dateFormat = "yyyy. M.";
            dateSFormat = "yyyyMMdd";
        } else if (_cdm.codeItem.strDataType.equals("1")) {
            dateFormat = "MM.dd. HH:mm";
            dateSFormat = "MMddHHmm";
        } else {
            dateFormat = "dd. HH:mm:ss";
            dateSFormat = "ddHHmmss";
        }

        //2021.07.09 by hanjun.Kim - kakaopay - 카카오 날짜디자인 적용 >>
        String value = sValue;
        SimpleDateFormat server_format = new SimpleDateFormat(dateSFormat);
        SimpleDateFormat display_format = new SimpleDateFormat(dateFormat);
        try {
            value = display_format.format(Objects.requireNonNull(server_format.parse(value)));
        } catch (ParseException e) {
            value = sValue;
            e.printStackTrace();
        }
        rtnValue = value;
        //2021.07.09 by hanjun.Kim - kakaopay - 카카오 날짜디자인 적용 <<

        return rtnValue;
    }

    Bitmap cropBitmap(Bitmap original, RectF rect) {
//		Bitmap result = Bitmap.createBitmap(original, (int) rect.left, (int) rect.top, (int) rect.width(), (int) rect.height());
        //2022.07.08 by lyk - kakaopay- 비트맵 이미지 사이즈와 changeRect 사이즈 처리시 방어코드 추가 >>
        if((rect.top + rect.height()) <= original.getHeight()) { //2022.09.13 by lyk - kakaopay - 비트맵 이미지 생성시 비교값을 float값으로 변경함
            Bitmap result = Bitmap.createBitmap(original, (int) rect.left, (int) rect.top, (int) chart_bounds.width(), (int) rect.height());
            if (result != original) {
                original.recycle();
            }

            return result;
        }
        //2022.07.08 by lyk - kakaopay- 비트맵 이미지 사이즈와 changeRect 사이즈 처리시 방어코드 추가 <<

        return null;
    }
    //2021. 06. 05 by hyh - 지표 잔상 효과 개발 <<
    //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 >>
    public boolean isSpecialDraw()
    {
        if(_cvm.getStandGraphName().equals("Kagi") || _cvm.getStandGraphName().equals("스윙") || _cvm.getStandGraphName().equals("렌코") || _cvm.getStandGraphName().equals("삼선전환도") || _cvm.getStandGraphName().equals("PnF"))
            return true;

        return false;
    }
    //2021.10.28 by HJW - specialDraw일 때 Block 잘못 가져오는 오류 수정 <<
}