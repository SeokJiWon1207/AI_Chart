package drfn.chart.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.appcompat.widget.AppCompatCheckBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import Engine.C;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.comp.DRBottomDialog;
import drfn.chart.comp.ExEditText;
import drfn.chart.comp.ExEditText.OnBackButtonListener;
import drfn.chart.comp.UnderLineEditText;
import drfn.chart.draw.BarDraw;
import drfn.chart.draw.DrawTool;
import drfn.chart.draw.LineColorDialog;
import drfn.chart.draw.LineDialog;
import drfn.chart.draw.LineDraw;
import drfn.chart.draw.SignalDraw;
import drfn.chart.draw.paletteDialog;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;

public class JipyoControlSetUI extends View implements OnBackButtonListener, View.OnTouchListener {
	AbstractGraph _graph;
	private int cont_cnt=4;
	//2012. 6. 28  line_cnt 추가
	private int line_cnt;
	private SeekBar _seekBar[] = null;

	private Boolean isFocus = false;
	private EditText isFocusEdit = null;
	private UnderLineEditText isUnderLineEditText = null;
	private UnderLineEditText isPreUnderLineEditText = null;
	private SeekBar isFocusSeekBar = null;
	private LinearLayout select_ll = null;

			RelativeLayout layout = null;
	Handler mHandler=null;
	//2012. 8. 30  JipyoControlSetUI 가 부모 등급으로 올라가서 접근자 변경   : I97, I98
	Context context = null;

	//2012. 9. 14  태블릿의 분틱차트 팝업윈도우 
	public PopupWindow periodPopup = null;

	//2012. 10. 2  체크박스 크기 세팅할 해상도 관련 문제로 사용하기 위해 클레스멤버로 위치이동  : I106
	//2013.04.05 이미지 리사이징 코드 비적용   >>
//  boolean bHighResolution = false;
	//2012. 10. 2  체크박스 이미지들을 가지고 있을 listdrawable  : I106
//  StateListDrawable stateDrawable;
//	Drawable normal, press;
	//2013.04.05 이미지 리사이징 코드 비적용   <<
	public static paletteDialog palette = null;
	public static LineColorDialog lineColorPalette = null;

	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)
	private WebView mWebView = null;

	//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>> : 콤보박스와 콤보박스의텍스트뷰 저장 버퍼
	private ArrayList<LinearLayout> m_arComboBox = new ArrayList<LinearLayout>();
	private ArrayList<TextView> m_arComboBoxText = new ArrayList<TextView>();

	private ArrayList<Button> m_arComboBoxDataType = new ArrayList<Button>();
	private ArrayList<Button> m_arComboBoxAverageType = new ArrayList<Button>();

	//콤보박스 리스트 팝업
	PopupWindow comboPopup;
	LinearLayout comboPopupLayout;
	//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

	private boolean bDestroy = false;
	private boolean m_bInitCompleted = false;	//2017.06.02 by LYH >> 화면 뜰 때 이벤트 막음

	//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
	Button btnComboBasePrice; 	//이평선타입
	Button btnComboAverageType; //종가기준값 콤보
	//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<
	//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//	Button btnComboBasePrice_average; 	//이평선타입
//	Button btnComboAverageType_average; //종가기준값 콤보
	//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
	LinearLayout m_selLinearLayout;
	Button m_selBtnCombo = null;

	Button btnReset, btnConfirm; //2023.11.07 by CYJ - kakaopay 지표상세설정 확인버튼
	public Button btnEditSoftKeypadConfirm; //2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼
	LinearLayout ll_bottom_view, ll_linearlayout1, llFrameTitle; //2023.11.07 by CYJ - kakaopay 지표상세설정 바텀뷰 (초기화, 확인)
	public DRBottomDialog drBottom;
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end
	boolean bIsKakao = true; //2023.11.03 by CYJ - 카카오페이 디자인
	boolean bIsErrorCase = false; //2023.11.10 by CYJ - 카카오페이 디자인
	//2023.11.10 by CYJ - kakaopay 인터렉션 추가>>
	public OnTouchListener onTouchListener;
	Animation reduce = AnimationUtils.loadAnimation(this.getContext(), R.anim.animation_reduce);
	Animation enlarge = AnimationUtils.loadAnimation(this.getContext(), R.anim.animation_enlarge);
	Animation enlargeReset = AnimationUtils.loadAnimation(this.getContext(), R.anim.animation_enlarge); //2024.01.03 by CYJ - 초기화 버튼 입력 이후 확인버튼 입력 시 초기화 버튼에도 같이 인터랙션 적용되는 현상 수정
	//2023.11.10 by CYJ - kakaopay 인터렉션 추가 <<
	ScrollView m_sv;
	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >>
	DRAlertDialog alert;
	int[] firstValue;
	boolean bEditing = false; //2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교
	public boolean bBack = false; //2023.12.27 by CYJ - 변경사항에 대한 적용 여부 팝업창 뒤로가기에만 보이도록 설정
	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 <<
	private boolean isCloseWebView = false; //2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정
	InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

	public JipyoControlSetUI(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;
		mHandler = new Handler();

		setUI();
//		COMUtil.setGlobalFont(this.layout);

		this.layout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}
	LinearLayout jipyoui=null;
	SeekBar tmpBar;
	//2012. 6. 27 선굵기  관련 id  추가  (l_ 가 선굵기관련  id)

	////2016.12.08 by PJM >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리
	private final int ids[] = {this.getContext().getResources().getIdentifier("gsetItem1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem2", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem3", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem4", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem5", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem6", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem7", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem8", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem9", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("gsetItem10", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem2", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem3", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem4", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem5", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem6", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem7", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem8", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem9", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_gsetItem10", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem11", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem12", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem13", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem14", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem15", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem16", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem17", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem18", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem19", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem20", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem21", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem22", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem23", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem24", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem25", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem26", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem27", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem28", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem29", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_gsetItem30", "id", this.getContext().getPackageName())
			};

	private final String ids_name[] = {"gsetItem1",
			"gsetItem2",
			"gsetItem3",
			"gsetItem4",
			"gsetItem5",
			"gsetItem6",
			"gsetItem7",
			"gsetItem8",
			"gsetItem9",
			"gsetItem10"};

	private final int seekids[] = {this.getContext().getResources().getIdentifier("seekBar1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar2", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar3", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar4", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar5", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar6", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar7", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar8", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar9", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("seekBar10", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar2", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar3", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar4", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar5", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar6", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar7", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar8", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar9", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("l_seekBar10", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar11", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar12", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar13", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar14", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar15", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar16", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar17", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar18", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar19", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar20", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar21", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar22", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar23", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar24", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar25", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar26", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar27", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar28", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar29", "id", this.getContext().getPackageName()),
//			this.getContext().getResources().getIdentifier("l_seekBar30", "id", this.getContext().getPackageName())
			};
	////2016.12.08 by PJM >> 레인보우 차트 30개까지 라인 색상 굵기 설정 가능하도록 처리 end

	private final String seekids_name[] = {"seekBar1",
			"seekBar2",
			"seekBar3",
			"seekBar4",
			"seekBar5",
			"seekBar6",
			"seekBar7",
			"seekBar8",
			"seekBar9",
			"seekBar10"};

	int[] Lines = {
			this.getContext().getResources().getIdentifier("line_width01", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05", "drawable", this.getContext().getPackageName()),
/*			drfn.chart.R.drawable.line_width06*/ };

	int[] DisabledLines = { this.getContext().getResources().getIdentifier("line_width01_d", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02_d", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03_d", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04_d", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05_d", "drawable", this.getContext().getPackageName())};


	//2013. 9. 3 지표마다 기준선 설정 추가>>
	private final int baseSettingIds[] = {	this.getContext().getResources().getIdentifier("baselinesetting_gsetItem1", "id", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("baselinesetting_gsetItem2", "id", this.getContext().getPackageName())};
	//2013. 9. 3 지표마다 기준선 설정 추가>>

	public void setUI() {
		//2012.6.27 선굵기 SeekBar 갯수 6개 추가 
		//2012. 8. 30  seekbar 메모리할당 위치 이동   : I97, I98
		_seekBar = new SeekBar[40];
		//6.26 jipyoui 를 inflate 로 받아오지 않고 DetailJipyoController에서 addView 되어있
		//View 를 받아온다. 

//		LayoutInflater factory = LayoutInflater.from(context);

//		jipyoui = factory.inflate(R.layout.jipyocontrol, null);
		jipyoui = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);

		int layoutResId1 = this.getContext().getResources().getIdentifier("scrollview", "id", context.getPackageName());
		m_sv = layout.findViewById(layoutResId1);

		layoutResId1 = this.context.getResources().getIdentifier("LinearLayout01", "id", this.context.getPackageName());
		ll_linearlayout1 = (LinearLayout)jipyoui.findViewById(layoutResId1);//종가기준값
//		int width = (int)COMUtil.getPixel(434);
//		int height = (int)COMUtil.getPixel(183);
//		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
//        		width, height);
//		params.leftMargin=COMUtil.chartWidth/2-width/2+10;
//		params.topMargin=COMUtil.chartHeight/2-height/2+30;
//		jipyoui.setLayoutParams(params);

//		this.layout.addView(jipyoui);



		//초기화.
//		for(int i=0; i<ids.length; i++) {
        for(int i=0; i<10; i++) {   //2016.12.08 by LYH >> 설정 뜨는 속도 개선
			//2012. 8. 7  레이아웃 id 받아오는 법 변경 
			int layoutResId = this.getContext().getResources().getIdentifier(String.valueOf(ids[i]), "id", context.getPackageName());
			LinearLayout ll = (LinearLayout)jipyoui.findViewById(layoutResId);
			//6.26  slider 의 초기상태는 GONE
			ll.setVisibility(View.GONE);

			tmpBar = (SeekBar)ll.findViewById(seekids[i]);

			//2012. 6. 27  변수냐 선굵기냐에 따라서 seekbar의 max 값 설정 
			if(i >= 10)
			{
				tmpBar.setMax(5);
				tmpBar.setProgress(1);
			}
//			else if()
//			{
//				//2012. 8. 8  주가이동평균선의 변수설정 seekbar에 한해서만 max값 300 설정 : I88
//				tmpBar.setMax(COMUtil.jipyoMaxForMoveAvgLine);
//			}
			else
			{
				tmpBar.setMax(COMUtil.jipyoMax);
			}

			_seekBar[i]=tmpBar;

			final int nIdx = i;
			//2012. 6. 27  감소,증가 버튼 
			layoutResId = this.getContext().getResources().getIdentifier("btnminus", "id", this.getContext().getPackageName());
			Button btnMinus = (Button)ll.findViewById(layoutResId);
			btnMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					decreaseCount(nIdx);
				}
			});

			layoutResId = this.getContext().getResources().getIdentifier("btnplus", "id", this.getContext().getPackageName());
			Button btnPlus = (Button)ll.findViewById(layoutResId);
			btnPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					increaseCount(nIdx);
				}
			});
			//2023.11.03 by CYJ - 카카오페이 디자인 >>
			if(bIsKakao) {
				btnPlus.setVisibility(GONE);
				btnMinus.setVisibility(GONE);
			}
			//2023.11.03 by CYJ - 카카오페이 디자인 <<
		}
	}

	public void init(AbstractGraph graph) {
		_graph = graph;
		resetUI();
	}

	public void resetUI() {
		if(_graph==null) return;

		COMUtil.closeToastDialog(); //2023.11.22 by CYJ - 다이어로그 떠있는 상태에서 지표설정창 재 진입 시 다이어로그 종료
		//2012. 8. 7  해상도 크기로 리스트뷰 크기를 결정하기 위함 
		//2013.04.05 이미지 리사이징 코드 비적용   >>
//		Display dis = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
//		int mDisWidth = dis.getWidth();            // 가로 사이즈 
//		int mDisHeight = dis.getHeight();          // 세로 사이즈
//		
//		//2012. 8. 13  일부기기에서 리스트뷰 하단 짤리는 현상 : I95
//		if((mDisWidth >= 720 && mDisHeight >= 1280) || (mDisWidth >= 1280 && mDisHeight >= 720))
//		{
//			bHighResolution = true;
//		}
//
//		//2012. 10. 2  체크박스 이미지 변경  : I106
//		stateDrawable = new StateListDrawable();
//		
//		int nChkBoxSize;
//		
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			nChkBoxSize = (int)(COMUtil.getPixel(45) / 2);
//		}
//		else
//		{
//			if(bHighResolution)
//			{
//				nChkBoxSize = (int)COMUtil.getPixel(45);
//			} 
//			else
//			{
//				nChkBoxSize = (int)COMUtil.getPixel(30);
//			}
//		}
//		
//		int layoutResId = context.getResources().getIdentifier("checkbox_off_background2", "drawable", context.getPackageName());
//		Bitmap image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		Bitmap resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	normal = (Drawable)(new BitmapDrawable(resizeImage));
//    	
//		layoutResId = context.getResources().getIdentifier("checkbox_on_background2", "drawable", context.getPackageName());
//		image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	press = (Drawable)(new BitmapDrawable(resizeImage));
		//2013.04.05 이미지 리사이징 코드 비적용   <<

		String strAdapterDataName = "jipyo_baseprice";
		int layoutResId = 0;

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>

		layoutResId = this.context.getResources().getIdentifier("LinearLayout02", "id", this.context.getPackageName());
		LinearLayout comboBackLayout = (LinearLayout)jipyoui.findViewById(layoutResId);//종가기준값
		comboBackLayout.setVisibility(View.GONE);

		//2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트 >>
		//2023.11.07 by CYJ - kakaopay 지표 상세설정 디자인 >>
		layoutResId = this.context.getResources().getIdentifier("bottom_view", "id", this.context.getPackageName());
		ll_bottom_view = (LinearLayout) jipyoui.findViewById(layoutResId);//종가기준값

		layoutResId = this.context.getResources().getIdentifier("btn_reset", "id", this.context.getPackageName());
		btnReset = (Button) jipyoui.findViewById(layoutResId);//종가기준값
		btnReset.setOnTouchListener(this);

		layoutResId = this.context.getResources().getIdentifier("btn_confirm", "id", this.context.getPackageName());
		btnConfirm = (Button) jipyoui.findViewById(layoutResId);//종가기준값
		btnConfirm.setOnTouchListener(this);

		layoutResId = this.context.getResources().getIdentifier("btn_edit_softkeypad_confirm", "id", this.context.getPackageName());
		btnEditSoftKeypadConfirm = (Button) jipyoui.findViewById(layoutResId);//종가기준값
		btnEditSoftKeypadConfirm.setVisibility(GONE);
		//2023.11.07 by CYJ - kakaopay 지표 상세설정 디자인 <<
		//2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트 <<

		if(isBollingerBand()) {
			//2023.11.03 by CYJ - 카카오 디자인 >>
			if(bIsKakao)
				comboBackLayout.setVisibility(View.GONE);
			//2023.11.03 by CYJ - 카카오 디자인 <<

			//기준가 콤보박스
			strAdapterDataName = "jipyo_baseprice_bollingerband";
			layoutResId = this.context.getResources().getIdentifier("jipyo_button_price", "id", this.context.getPackageName());
			btnComboBasePrice = (Button)jipyoui.findViewById(layoutResId);//종가기준값
			setComboBox(btnComboBasePrice, strAdapterDataName);

			//기준가 콤보박스 초기값 지정
			layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, this.context.getResources().getIdentifier("dropdown_bg", "drawable", this.context.getPackageName()));
			btnComboBasePrice.setText(String.valueOf(comboAdapter.getItem(_graph.dataTypeBollingerband)));

			//이평선타입 콤보박스
			strAdapterDataName = "jipyo_calctype_bollingerband";
			layoutResId = this.context.getResources().getIdentifier("jipyo_button_type", "id", this.context.getPackageName());
			btnComboAverageType = (Button)jipyoui.findViewById(layoutResId);//이평선타입
			setComboBox(btnComboAverageType, strAdapterDataName);

			//이평선타입 콤보박스 초기값 지정
			layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
			comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId,
					this.context.getResources().getIdentifier("dropdown_bg", "drawable", this.context.getPackageName()));
			btnComboAverageType.setText(String.valueOf(comboAdapter.getItem(_graph.calcTypeBollingerband)));
		}
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

		//<<<<<< 2014.3.5 가격,이평선타입콤보 리스트만들기
		if(_graph.getGraphTitle().equals("주가이동평균")) {

			DrawTool baseDrawTool = _graph.getDrawTool().get(0);
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//			layoutResId = this.context.getResources().getIdentifier("ll_average", "id", this.context.getPackageName());
//			LinearLayout ll_average = (LinearLayout)jipyoui.findViewById(layoutResId);
//			ll_average.setVisibility(View.VISIBLE);
//
//			//기준가 콤보박스
//			strAdapterDataName = "jipyo_baseprice";
//			layoutResId = this.context.getResources().getIdentifier("jipyo_button_price_average", "id", this.context.getPackageName());
//			btnComboBasePrice_average = (Button)jipyoui.findViewById(layoutResId);//종가기준값
//			setComboBox(btnComboBasePrice_average, strAdapterDataName);
//
//			//기준가 콤보박스 초기값 지정
//			layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
//			ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, this.context.getResources().getIdentifier("dropdown_bg", "drawable", this.context.getPackageName()));
//			btnComboBasePrice_average.setText(String.valueOf(comboAdapter.getItem(baseDrawTool.getDataType())));
//
//			//이평선타입 콤보박스
//			strAdapterDataName = "jipyo_averagetype";
//			layoutResId = this.context.getResources().getIdentifier("jipyo_button_type_average", "id", this.context.getPackageName());
//			btnComboAverageType_average = (Button)jipyoui.findViewById(layoutResId);//이평선타입
//			setComboBox(btnComboAverageType_average, strAdapterDataName);
//
//			//이평선타입 콤보박스 초기값 지정
//			layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
//			comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId,
//					this.context.getResources().getIdentifier("dropdown_bg", "drawable", this.context.getPackageName()));
//			btnComboAverageType_average.setText(String.valueOf(comboAdapter.getItem(baseDrawTool.getAverageCalcType())));

			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			LinearLayout paramlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			paramlinear.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("colorlinear", "id", this.context.getPackageName());
			LinearLayout colorlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			colorlinear.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			LinearLayout averageLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
			averageLayout.setVisibility(View.VISIBLE);
			layoutResId = this.context.getResources().getIdentifier("paramtv", "id", this.context.getPackageName());
			TextView paramtv = (TextView)jipyoui.findViewById(layoutResId);
			paramtv.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("colortv", "id", this.context.getPackageName());
			TextView colortv = (TextView)jipyoui.findViewById(layoutResId);
			colortv.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("jipyo_baseprice", "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapterData = ArrayAdapter.createFromResource(this.context, layoutResId, this.getContext().getResources().getIdentifier("pop_back", "drawable", this.getContext().getPackageName()));

			layoutResId = this.context.getResources().getIdentifier("jipyo_averagetype", "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapterAverage = ArrayAdapter.createFromResource(this.context, layoutResId, this.getContext().getResources().getIdentifier("pop_back", "drawable", this.getContext().getPackageName()));

			for(int i=0; i<ids_name.length; i++) {

				//2012. 8. 7  레이아웃 id 받아오는 법 변경
				layoutResId = averageLayout.getResources().getIdentifier(ids_name[i], "id", context.getPackageName());
				LinearLayout ll = (LinearLayout)averageLayout.findViewById(layoutResId);

				if(i < 7) {
					ll.setVisibility(View.VISIBLE);
				} else {
					ll.setVisibility(View.GONE);
				}

				layoutResId = ll.getResources().getIdentifier(seekids_name[i], "id", context.getPackageName());
				final SeekBar tmpBar = (SeekBar)ll.findViewById(layoutResId);
				tmpBar.setMax(COMUtil.jipyoMaxForMoveAvgLine);
				_seekBar[i]=tmpBar;
				final int nIdx = i;
				//2012. 6. 27  감소,증가 버튼
				layoutResId = ll.getResources().getIdentifier("btnminus", "id", this.getContext().getPackageName());
				Button btnMinus = (Button)ll.findViewById(layoutResId);
				btnMinus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						decreaseCount(nIdx);
					}
				});

				layoutResId = ll.getResources().getIdentifier("btnplus", "id", this.getContext().getPackageName());
				Button btnPlus = (Button)ll.findViewById(layoutResId);
				btnPlus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						increaseCount(nIdx);
					}
				});

				//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 >>
				Vector<DrawTool> drawTools = _graph.getDrawTool();
				if (drawTools.isEmpty()) {
					break;
				}

				DrawTool dt = drawTools.get(i);
				//DrawTool dt = _graph.getDrawTool().get(i);
				//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 >>

				layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
				final TextView tvColorOpen = (TextView)ll.findViewById(layoutResId);
				int[] getUpColor = dt.getUpColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						showColorPalette(tvColorOpen);
//					}
//				});

//				//LineDraw 가 아니면 바타입으로 바꾸는 UI를 감춤
//				if(dt instanceof LineDraw && !_graph.graphTitle.equals("주가이동평균") && !isOverlay(_graph.graphTitle))	//2015. 3. 2 채널지표에는 라인바 콤보 감추기
//				{
//					m_arComboBox.get(i-ids.length/2).setVisibility(View.VISIBLE);
//				}
//				else
//				{
//					m_arComboBox.get(i-ids.length/2).setVisibility(View.GONE);
//					ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)tvColorOpen.getLayoutParams();
//					param.width += (int)COMUtil.getPixel(80);
//					tvColorOpen.setLayoutParams(param);
//				}
//				//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

				layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
				final TextView tvLineOpen = (TextView)ll.findViewById(layoutResId);
				layoutResId = this.getContext().getResources().getIdentifier("ll_color_line", "id", this.getContext().getPackageName());
				LinearLayout llColorLine = (LinearLayout) ll.findViewById(layoutResId);
				llColorLine.setTag(i + 5000);
				llColorLine.setOnClickListener(new OnClickListener() {
					   @Override
					   public void onClick(View view) {
						   showLineColorPalette(tvColorOpen, tvLineOpen);
					   }
				   }
				);
				tvColorOpen.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showLineColorPalette(tvColorOpen, tvLineOpen);
					}
				});

				if(!(dt instanceof BarDraw))
				{
					int getUpLine = dt.getLineT();
					int lineImg = 0;
//					if(getUpLine>0)
//						lineImg=Lines[getUpLine-1];
//					tvLineOpen.setBackgroundResource(lineImg);
					tvLineOpen.setText(getUpLine + "px");
					tvLineOpen.setTag(String.valueOf(getUpLine));
					tvLineOpen.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//showLineSelect(tvLineOpen);
							showLineColorPalette(tvColorOpen, tvLineOpen);
						}
					});

					if(7 == dt.getDrawType2())
					{
						enableApplyLineButton(false, i-10);
					}
				}
				else
				{
					tvLineOpen.setVisibility(View.GONE);
				}
				layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
				CheckBox chkBox = (CheckBox)ll.findViewById(layoutResId);
				//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
				//2013.04.05 이미지 리사이징 코드 비적용   >>
				//			stateDrawable = imageChange(normal, press);
				//			chkBox.setButtonDrawable(stateDrawable);
				//2013.04.05 이미지 리사이징 코드 비적용   <<

				chkBox.setChecked(dt.isVisible());
				chkBox.setTag(i + 4000);
				if(chkBox.isChecked()) {
					tvLineOpen.setEnabled(true);
					tvLineOpen.setAlpha(1);
					tvColorOpen.setEnabled(true);
					tvColorOpen.setAlpha(1);
				} else {
					tvLineOpen.setEnabled(false);
					tvLineOpen.setAlpha(0.28f);
					tvColorOpen.setEnabled(false);
					tvColorOpen.setAlpha(0.28f);
				}

				chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						if(m_bInitCompleted==false)
							return;
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
						if(!isChecked)
						{
							keepCheckboxStateAtLeastOne(buttonView);
						}
						patchUI_index(Integer.parseInt(buttonView.getTag().toString()) - 4000); 	//2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>

						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
//						reSetJipyo();
//						COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//						COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<
					}
				});
				if(!bIsKakao) {
					//데이터타입콤보
					strAdapterDataName = "jipyo_baseprice";
					layoutResId = this.context.getResources().getIdentifier("jipyo_button_price", "id", this.context.getPackageName());
					Button btnComboBasePriceType = (Button) ll.findViewById(layoutResId);//종가기준값
					setComboBox(btnComboBasePriceType, strAdapterDataName);

					if (dt.getDataType() != 99)
						btnComboBasePriceType.setText(String.valueOf(comboAdapterData.getItem(dt.getDataType())));
					btnComboBasePriceType.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

					m_arComboBoxDataType.add(btnComboBasePriceType);
					btnComboBasePriceType.setTag(String.valueOf(i));
					//이평선타입콤보
					strAdapterDataName = "jipyo_averagetype";
					layoutResId = this.context.getResources().getIdentifier("jipyo_button_type", "id", this.context.getPackageName());
					Button btnComboAverageType = (Button) ll.findViewById(layoutResId);//이평선타입
					setComboBox(btnComboAverageType, strAdapterDataName);

					if (dt.getAverageCalcType() != 99)
						btnComboAverageType.setText(String.valueOf(comboAdapterAverage.getItem(dt.getAverageCalcType())));
					btnComboAverageType.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

					m_arComboBoxAverageType.add(btnComboAverageType);
					btnComboAverageType.setTag(String.valueOf(i));
				}

				//2023.11.03 by CYJ - 카카오 디자인 >>
				if(bIsKakao) {
					btnPlus.setVisibility(GONE);
					btnMinus.setVisibility(GONE);
				}
				//2023.11.03 by CYJ - 카카오 디자인 <<
			}
		}else{
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			LinearLayout paramlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			paramlinear.setVisibility(View.VISIBLE);
			layoutResId = this.context.getResources().getIdentifier("colorlinear", "id", this.context.getPackageName());
			LinearLayout colorlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			colorlinear.setVisibility(View.VISIBLE);
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			LinearLayout averageLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
			averageLayout.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("paramtv", "id", this.context.getPackageName());
			TextView paramtv = (TextView)jipyoui.findViewById(layoutResId);
			paramtv.setVisibility(View.GONE);
			layoutResId = this.context.getResources().getIdentifier("colortv", "id", this.context.getPackageName());
			TextView colortv = (TextView)jipyoui.findViewById(layoutResId);
			colortv.setVisibility(View.GONE);

			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//			layoutResId = this.context.getResources().getIdentifier("ll_average", "id", this.context.getPackageName());
//			LinearLayout ll_average = (LinearLayout)jipyoui.findViewById(layoutResId);
//			ll_average.setVisibility(View.GONE);
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

			if(COMUtil.isMarketIndicator(_graph.getGraphTitle())|| _graph.getGraphTitle().equals("미결제약정") || _graph.getGraphTitle().equals("Pivot") || _graph.getGraphTitle().equals("Demark") || _graph.getGraphTitle().equals("Pivot전봉기준")){
				paramlinear.setVisibility(View.GONE);
				paramtv.setVisibility(View.GONE);
			}
		}


		int i=0;
		String[] cont = _graph.s_interval;
		int[] i_cnt = _graph.interval;

		//gsetItem1~6가지 안보임처리.
		for(i=0; i<cont_cnt; i++) {
			LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[i]);
//			ll.setVisibility(View.INVISIBLE);
			ll.setVisibility(View.GONE);
		}
		if(cont==null) {
			return;
		}
//		int layoutResId;
		//레이아웃 지정
		LinearLayout baseLayout = null;
		if(_graph.getGraphTitle().equals("주가이동평균")) {
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		} else {
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		}

		boolean bDetectFirst = false;
		for(i=0; i<cont.length; i++)
		{
			final LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[i]);
			if(i < 7) {
				ll.setVisibility(View.VISIBLE);
			} else {
				ll.setVisibility(View.GONE);
			}

			//첫번째 활성화 텍스트 필드에 키보드 띄우기 >>
			DrawTool dt = null;
			if(_graph.getGraphTitle().equals("주가이동평균")) {
				dt = _graph.getDrawTool().get(i);
			} else {
				dt = _graph.getDrawTool().get(0); //첫번째 항목만 체크
			}
			//첫번째 활성화 텍스트 필드에 키보드 띄우기 <<

//			layoutResId = this.getContext().getResources().getIdentifier("textView1", "id", this.getContext().getPackageName());
//			TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 이름.
//
//			//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
//			tmp.setText(COMUtil.getAddJipyoTitle(cont[i]));
			//tmp.setText(cont[i]);
			//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

			//2012. 12. 20 상세설정 선굵기설정의 굵기값 입력가능하게 수정. EditText에서 SeekBar 조절이 필요해서 SeekBar의 세팅을 앞으로 옮겼음. 
			final SeekBar tmpBar = _seekBar[i];
			tmpBar.setId(i);
			tmpBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
												  public void onStopTrackingTouch(SeekBar seekBar) {
//					System.out.println("onStopTrackingTouch");
//													  updateValue(seekBar);
												  }
												  public void onStartTrackingTouch(SeekBar seekBar) {
//					System.out.println("onStartTrackingTouch");
												  }
												  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//					System.out.println("onProgressChanged");
													  dragUpdateValue(seekBar);
												  }
											  }
			);

			layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//			tmp = (TextView)ll.findViewById(layoutResId); //지표 설정 값.
//			EditText tmp2 = (EditText)ll.findViewById(layoutResId); //지표 설정 값.
			UnderLineEditText tmp2 = (UnderLineEditText)ll.findViewById(layoutResId); //지표 설정 값.   //2013. 3. 26   EditText 에서 Back버튼 처리
			tmp2.edInput.setOnBackButtonListener(this);
			String strEditHint = "까지 입력";
//			tmp2.setHintTextColor(Color.GRAY);
//			tmp2.setOnBackButtonListener(this);  //2013. 3. 26   EditText 에서 Back버튼 처리
//			tmp2.setTypeface(COMUtil.numericTypefaceMid);
			tmp2.setTag(String.valueOf(i));		 //2013. 3. 26   EditText 에서 Back버튼 처리
			tmp2.edInput.setTag(String.valueOf(i));		 //2013. 3. 26   EditText 에서 Back버튼 처리
			tmp2.tvDescription.setText(COMUtil.getAddJipyoTitle(cont[i]));

			//2023.11.16 by lyk - 카카오페이 캔들차트 개선 - 키보드 닫은 후 같은 에디트필드 선택시 "확인" 버튼이 보이는 현상 수정 >>
			tmp2.edInput.setMaxLines(1);
			tmp2.edInput.setInputType(InputType.TYPE_CLASS_TEXT);
			//2023.11.16 by lyk - 카카오페이 캔들차트 개선 - 키보드 닫은 후 같은 에디트필드 선택시 "확인" 버튼이 보이는 현상 수정 <<

			//2023.11.07 by CYJ - kakaopay 소프트키보드 외부 클릭시 숨김 처리 >>
			layoutResId = this.getContext().getResources().getIdentifier("ll_frame_title", "id", this.getContext().getPackageName());
			llFrameTitle = jipyoui.findViewById(layoutResId);
			llFrameTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {hideKeyPad(isFocusEdit);}
			});
			final int finalLayoutResId = layoutResId;
			ll.setOnTouchListener(
					new OnTouchListener() {
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
								case MotionEvent.ACTION_UP:
									hideKeyPad(isFocusEdit);
									break;
							}
							return true;
						}
					}
			);
			//2023.11.07 by CYJ - kakaopay 소프트키보드 외부 클릭시 숨김 처리 <<

			//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 >>
			btnEditSoftKeypadConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
//					if(COMUtil.dialog != null && COMUtil.dialog.isShowing()) {
//						COMUtil.closeToastDialog();
//					}
					hideKeyPad(isFocusEdit);
				}
			});
			//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 <<

			//이평선에서만 라인 체크, 색상/굵기 선택 기능으로 UI 구분
			CheckBox chkBox = null;
			if(_graph.graphTitle.equals("주가이동평균")) {
				chkBox = (CheckBox)ll.findViewWithTag(i + 4000);
			}

			if(chkBox != null) {
				patchUI_index(i); //2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트
			}

			//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
//			tmp2.setText(""+i_cnt[i]);
			setEditTextValue(tmp2.edInput, i_cnt[i], true);
			//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<

			final UnderLineEditText _tmp2 = tmp2;
			tmp2.edInput.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 >>
					ll_bottom_view.setVisibility(GONE);
					btnEditSoftKeypadConfirm.setVisibility(VISIBLE);
					//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 <<
					if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{


						hideKeyPad(_tmp2.edInput);
//	    				inputUpdateValue(tmpBar, _tmp2);
//	    				updateValue(tmpBar);

					}
					return true;
				}
			});
//			tmp2.setOnEditorActionListener(new OnEditorActionListener() {
//				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//					if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//						hideKeyPad(_tmp2.edInput);
//					}
//					return true;
//				}
//			});
			tmp2.setOnFocusChangeListener(new OnFocusChangeListener()
			{

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					//2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 >>
					if(isCloseWebView) {
						return;
					}
					//2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 <<

					btnEditSoftKeypadConfirm.setVisibility(VISIBLE);
					ll_bottom_view.setVisibility(GONE);

					Log.d("chart_onFocusChange", ""+hasFocus);

					bEditing = false; //2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교

					//2013. 8. 27 상세설정창 EditText 터치시 전체선택 >>
//					if(!isFocus) {
					if(!hasFocus) {
						//2023.11.03 by CYJ - 텍스트 필드 에러 케이스 적용 >>
						inputUpdateValue(tmpBar, _tmp2.edInput);

						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
//						updateValue(tmpBar);
						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<

//						isFocus = false;
//						isFocusView = v;

						isFocusEdit =_tmp2.edInput;
						isFocusSeekBar = tmpBar;
						isPreUnderLineEditText = tmp2;

						//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
						//EditText가 포커스를 잃을 때 정수->소숫점으로 변환.
						setEditTextValue(_tmp2.edInput, tmpBar.getProgress(), true);
						//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<
						//2019. 07. 11 by hyh - 설정창 닫을 때 저장 되도록 수정

						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
//                        reSetJipyo();
						//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<
					} else {
						isFocus = true;
						ExEditText edit = (ExEditText)v;
						//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
						//EditText를 선택했을 때 소숫점이 아닌  seekBar 의 값을 보여준다. 
//						setEditTextValue(_tmp2, Float.parseFloat(_tmp2.getText().toString()), false);	//2015. 1. 20 볼린저밴드 값 입력시에도 소숫점으로
						//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<
						//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 >>
//						if(edit.getText().length() > 0) {
							//btnEditSoftKeypadConfirm.setEnabled(true);
//							btnEditSoftKeypadConfirm.setTextColor(CoSys.HIGH_EMPHASIS);
//						} else {
							//btnEditSoftKeypadConfirm.setEnabled(false);
//							btnEditSoftKeypadConfirm.setTextColor(CoSys.DISABLE_TEXT_COLOR);
//						}
						//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 <<
						isFocusSeekBar = tmpBar;
						isFocusEdit =_tmp2.edInput;
						isUnderLineEditText = tmp2;

						//2023.11.16 by CYJ - kakaopay 주가이동평균에서 EditText 클리어버튼 비활성화 >>
						if(_graph.getGraphTitle().equals("주가이동평균")) {
							tmp2.setClearIconVisible(false);
						} else {
							tmp2.setClearIconVisible(true);
						}
						//2023.11.16 by CYJ - kakaopay 주가이동평균에서 EditText 클리어버튼 비활성화 <<

						edit.selectAll();

						int selectIndex = Integer.parseInt(tmp2.getTag().toString()) + 1;
						int selectEditFocus = llFrameTitle.getHeight() -  m_sv.getScrollY() + _tmp2.getHeight() * selectIndex + (int) COMUtil.getPixel_W(34);
						int nYScroll = 0;

						if(selectEditFocus > btnEditSoftKeypadConfirm.getY()) {
							nYScroll = selectEditFocus - (int) btnEditSoftKeypadConfirm.getY() + m_sv.getScrollY() + (int) COMUtil.getPixel_W(34);
							m_sv.smoothScrollTo(0, nYScroll);
						}
					}
					//2013. 8. 27 상세설정창 EditText 터치시 전체선택 >>
				}

			});

			tmp2.edInput.addTextChangedListener(new TextWatcher() {
				//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 >>
				String currentText = tmp2.edInput.toString();
				String updatedText = "";
				//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 <<

				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 >>
					updatedText = charSequence.toString();
					if (currentText.equals(updatedText)) {
						bEditing = false;
					} else {
						bEditing = true;
					}
					//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 <<
					//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 >>
//					if(tmp2.tvErrorTooltip.getVisibility() == VISIBLE) {
//						btnConfirm.setEnabled(false);
//						btnEditSoftKeypadConfirm.setEnabled(false);
//						btnEditSoftKeypadConfirm.setTextColor(CoSys.DISABLE_TEXT_COLOR);
//					} else {
//						if (charSequence.length() > 0) {
//							btnConfirm.setEnabled(true);
//							btnEditSoftKeypadConfirm.setEnabled(true);
//							btnEditSoftKeypadConfirm.setTextColor(CoSys.HIGH_EMPHASIS);
//						} else {
//							btnConfirm.setEnabled(false);
//							btnEditSoftKeypadConfirm.setEnabled(false);
//							btnEditSoftKeypadConfirm.setTextColor(CoSys.DISABLE_TEXT_COLOR);
//						}
//					}
					//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 <<
				}

				@Override
				public void afterTextChanged(Editable editable) {
				}
			});

			//2015. 1. 20 볼린저밴드 값 입력시에도 소숫점으로 >>
			if( ( _graph.getGraphTitle().contains("Parabolic SAR") && (((String)tmp2.getTag()).equals("0") || ((String)tmp2.getTag()).equals("1")) )  ||
					//2017.05.11 by LYH << 전략(신호, 강약) 추가
					( _graph.getGraphTitle().contains("Parabolic SAR 신호") && (((String)tmp2.getTag()).equals("0") || ((String)tmp2.getTag()).equals("1")) )  ||
					( _graph.getGraphTitle().contains("Parabolic SAR 강세약세") && (((String)tmp2.getTag()).equals("0") || ((String)tmp2.getTag()).equals("1")) )  ||
					//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
					( isBollingerBand() && ((String)tmp2.getTag()).equals("1")) ||
					( _graph.getGraphTitle().contains("Envelope") && (((String)tmp2.getTag()).equals("1") || ((String)tmp2.getTag()).equals("2")) ) ) //2020.11.27 by HJW - Envelope 지표 소수점 처리
			{
				tmp2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			} else {
				tmp2.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			//2015. 1. 20 볼린저밴드 값 입력시에도 소숫점으로 <<

			final int ival = i_cnt[i];
			if(tmpBar!=null) {
				//2012. 8. 8 주가이동평균선의 5, 6번째 max 값을 300 으로 세팅 : I88
//				if(ival >= 200)
//				{
				//2015. 1. 13 이동평균 최대변수기간 확대:999일>>
				if(_graph.getGraphTitle().equals("주가이동평균")) {
//					tmp2.setFilters(new InputFilterMinMax[]{ new InputFilterMinMax("1", "999")});
					tmpBar.setMax(999);
				}
				//2020. 05. 27 by hyh - 일목균형표 선행스팬1이 미래영역에 적용 안되는 에러 수정. 미래영역 제한 해제 >>
//				else if(cont[i].endsWith("선행1기간"))	//일목균형표 미래영역 26 제한.
//				{
////					tmp2.setFilters(new InputFilterMinMax[]{ new InputFilterMinMax("1", "26")});
//					tmpBar.setMax(26);
//				}
				//2020. 05. 27 by hyh - 일목균형표 선행스팬1이 미래영역에 적용 안되는 에러 수정. 미래영역 제한 해제 <<
				else if(_graph.getGraphTitle().equals("그물차트") && cont[i].endsWith("개수"))	//그물차트 개수 30 제한
				{
//					tmp2.setFilters(new InputFilterMinMax[]{ new InputFilterMinMax("1", "30")});
					tmpBar.setMax(30);
				}
				//2019. 06. 28 by hyh - 볼린저밴드 최대값 변경 3.00 -> 100.00 >>
				else if (_graph.getGraphTitle().equals("Bollinger Band") && i == 1) {
					tmpBar.setMax(10000);
				}
				//2019. 06. 28 by hyh - 볼린저밴드 최대값 변경 3.00 -> 100.00 <<
				//2020.11.27 by HJW - Envelope 지표 소수점 처리 >>
				else if(_graph.getGraphTitle().equals("Envelope") && cont[i].endsWith("율(%)"))	//그물차트 개수 30 제한
				{
//					tmp2.setFilters(new InputFilterMinMax[]{ new InputFilterMinMax("1", "30")});
					tmpBar.setMax(10000);
				}
				//2023.11.08 by CYJ - kakaopay 매물대 최대개수 수정 >>
				else if(_graph.getGraphTitle().equals("매물대"))
				{
					tmpBar.setMax(30);
				}
				//2023.11.08 by CYJ - kakaopay 매물대 최대개수 수정 <<
				//2020.11.27 by HJW - Envelope 지표 소수점 처리 <<
				else {
					tmpBar.setMax(300);
				}
				//2015. 1. 13 이동평균 최대변수기간 확대:999일<<
//				}
				tmpBar.setProgress(ival);
			}
			//2023.11.07 by CYJ - kakaopay 텍스트필드 에러케이스 >>
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				tmpBar.setMin(tmpBar.getMin()+1);
			}
			float minValue = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				minValue = (float)tmpBar.getMin()/100;
			}
			float maxValue = tmpBar.getMax()/100;

			if((_graph.getGraphTitle().equals("Bollinger Band") && i == 1) || (_graph.getGraphTitle().equals("Envelope") && cont[i].endsWith("율(%)"))) {
				tmp2.m_fMinValue = minValue; //2023.11.15 by CYJ - 최솟값보다 작은 경우에 에러케이스 적용
				tmp2.m_fMaxValue = maxValue;
				tmp2.tvErrorTooltip.setText(String.format("%.2f~%.2f%s할 수 있어요.", minValue, maxValue, strEditHint));
				tmp2.edInput.setHint(String.format("%.2f~%.2f%s가능", minValue, maxValue, strEditHint));
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					tmp2.m_fMinValue = tmpBar.getMin(); //minvalue 값 처리
				}
				tmp2.m_fMaxValue = tmpBar.getMax();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					tmp2.tvErrorTooltip.setText(String.format("%d~%d%s할 수 있어요.", tmpBar.getMin(), tmpBar.getMax(), strEditHint));
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					tmp2.edInput.setHint(String.format("%d~%d%s가능", tmpBar.getMin(), tmpBar.getMax(), strEditHint));
				}
			}
			//2023.11.07 by CYJ - kakaopay 텍스트필드 에러케이스 <<

			//첫번째 활성화 텍스트 필드에 키보드 띄우기 >>
			if(!bDetectFirst && dt.isVisible()) {
				bDetectFirst = true;
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); //2023.12.04 by HHK - 낮은 버전에서 테마 전환시 키패드 노출상태 유지되던 현상 수정 //2023.12.14 by CYJ - 키보드활성화 되있는 상태에서 뒤로가기 버튼 입력시 설정창 종료되는 현상 수정
				tmp2.requestFocus();
				//2023.11.17 by lyk - 캔들차트개선 - 커서를 끝에 위치 >>
				tmp2.edInput.post(new Runnable() {
					@Override
					public void run() {
						tmp2.edInput.setSelection(tmp2.edInput.length());
						isFocusEdit = tmp2.edInput; //2023.11.16 by CYJ - kakaopay 처음 활성화된 키보드 비활성화

					}
				});
				//2023.11.17 by lyk - 캔들차트개선 - 커서를 끝에 위치 <<
				isFocusEdit = tmp2.edInput; //2023.11.16 by CYJ - kakaopay 처음 활성화된 키보드 비활성화
				isFocusEdit.requestFocus();
			}
			//첫번째 활성화 텍스트 필드에 키보드 띄우기 <<
		}

		//2012. 6. 27  선굵기 seekbar 세팅  
		int dCnt = _graph.getDrawTool().size();
		line_cnt = dCnt;
		if(_graph.graphTitle.equals("매물대"))
		{
			line_cnt = 3;
			DrawTool dt = _graph.getDrawTool().get(0);
			int[] getUpColor = dt.getUpColor();
			for(i=1; i<=3; i++)
			{
				String strId = "standscale_row_" + String.valueOf(i) + "_colortextview";
				layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
				final TextView tvColorOpen = (TextView)jipyoui.findViewById(layoutResId);
				if(i==2)
					getUpColor = dt.getSameColor();
				else if(i==3)
					getUpColor = dt.getDownColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				tvColorOpen.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showColorPalette(tvColorOpen);
					}
				});
			}

			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
			layoutResId = this.getContext().getResources().getIdentifier("chk_standscaleshow", "id", this.getContext().getPackageName());
			final CheckBox chk_standscaleshow = (AppCompatCheckBox)jipyoui.findViewById(layoutResId);
			chk_standscaleshow.setChecked(_graph.getDrawTool().get(0).isStandScaleLabelShow());
			chk_standscaleshow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reSetJipyo();
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
				}
			});
			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>



			layoutResId = this.getContext().getResources().getIdentifier("tv_showvalue", "id", this.getContext().getPackageName());
			TextView tv_standscaleshow = (TextView)jipyoui.findViewById(layoutResId);

			tv_standscaleshow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					chk_standscaleshow.setChecked(!chk_standscaleshow.isChecked());
					reSetJipyo();
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
				}
			});




		}
		else
		{

//			for(i = ids.length/2; i < line_cnt+ids.length/2; i++)
			for(i = 10; i < line_cnt+10; i++)
			{
				if(bIsKakao) break; //2023.11.22 by CYJ - kakaopay 성능개선
				//dt 의 인덱스는 0 부터인데, 선굵기 seekbar 는 배열한개에서 관리되기때문에 6부터 시작함 
				DrawTool dt = _graph.getDrawTool().get(i-10);

				LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[i]);
				ll.setVisibility(View.GONE);

				//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
				//라디오그룹 및 버튼 
//				RadioGroup rg_DrawType = (RadioGroup)ll.findViewById(this.getContext().getResources().getIdentifier("detail_drawtype_radiogroup", "id", this.getContext().getPackageName()));
//				RadioButton rb_Bar = (RadioButton)rg_DrawType.findViewById(this.getContext().getResources().getIdentifier("detail_radiobtn_bar", "id", this.getContext().getPackageName()));
//				RadioButton rb_Line = (RadioButton)rg_DrawType.findViewById(this.getContext().getResources().getIdentifier("detail_radiobtn_line", "id", this.getContext().getPackageName()));
//
//				//상세설정 열었을 때 라디오버튼 상태 설정
//				if(7 == dt.getDrawType2())
//				{
//					//바
//					rb_Bar.setChecked(true);
//				}
//				else
//				{
//					//라인
//					rb_Line.setChecked(true);
//				}
//				
//				//LineDraw 가 아니면 바타입으로 바꾸는 UI를 감춤
//				if(_graph.graphTitle.equals("주가이동평균"))		//2015. 1. 16 주가이평에는 라인바 UI 감추기
//				{
//					rg_DrawType.setVisibility(View.INVISIBLE);
//				}
//				else if(dt instanceof LineDraw)
//				{
//					rg_DrawType.setVisibility(View.VISIBLE);
//				}
//				else
//				{
//					rg_DrawType.setVisibility(View.INVISIBLE);
//				}
//				
//				final DrawTool _dt = dt;
//				rb_Bar.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						//바타입은 drawType2 가 7
//						_dt.setDrawType2(7);
//
//						reSetJipyo();
//			    	      COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//			    	      COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
//					}
//				});
//				
//				rb_Line.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						_dt.setDrawType2(0);
//
//						reSetJipyo();
//			    	      COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//			    	      COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
//					}
//				});

				//콤보박스 및 콤보박스의 리스트뷰 설정
				comboPopupLayout  = new LinearLayout(this.context);
				comboPopupLayout.setOrientation(LinearLayout.VERTICAL);

				layoutResId = context.getResources().getIdentifier("detail_drawtype_combo", "id", context.getPackageName());
				m_arComboBox.add((LinearLayout)ll.findViewById(layoutResId));
				m_arComboBox.get(i-10).setTag(String.valueOf(i-10));

				layoutResId = context.getResources().getIdentifier("detail_drawtype_combo_text", "id", context.getPackageName());
				m_arComboBoxText.add((TextView)m_arComboBox.get(i-10).findViewById(layoutResId));

				setComboBox(m_arComboBox.get(i-10));

				//상세설정 열었을 때 콤보박스 상태 설정
				if(7 == dt.getDrawType2())
				{
					//바
					m_arComboBoxText.get(i-10).setText("바");
				}
				else
				{
					//라인
					m_arComboBoxText.get(i-10).setText("라인");
				}


				layoutResId = this.getContext().getResources().getIdentifier("textView1", "id", this.getContext().getPackageName());
				TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 이름.

				//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
				tmp.setText(COMUtil.getAddJipyoTitle(dt.getTitle()));
//				tmp.setText(dt.getTitle());
				//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

				//2016.12.08 by LYH >> 설정 뜨는 속도 개선
//				final SeekBar tmpBar = _seekBar[i];
//				tmpBar.setId(i);
//				tmpBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//													  public void onStopTrackingTouch(SeekBar seekBar) {
//														  //					System.out.println("onStopTrackingTouch");
//														  updateValue(seekBar);
//													  }
//													  public void onStartTrackingTouch(SeekBar seekBar) {
//														  //					System.out.println("onStartTrackingTouch");
//													  }
//													  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//														  //					System.out.println("onProgressChanged");
//														  dragUpdateValue(seekBar);
//													  }
//												  }
//				);
                //2016.12.08 by LYH >> 설정 뜨는 속도 개선 end

				layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
				//			tmp = (TextView)ll.findViewById(layoutResId); //지표 설정 값.
				UnderLineEditText tmp2 = (UnderLineEditText)ll.findViewById(layoutResId); //지표 설정 값.
				tmp2.edInput.setOnBackButtonListener(this);  //2013. 3. 26   EditText 에서 Back버튼 처리
				tmp2.edInput.setTag(String.valueOf(i));
				tmp2.edInput.setText("1");
				tmp2.edInput.setTypeface(COMUtil.numericTypefaceMid);

				final UnderLineEditText _tmp2 = tmp2;
				tmp2.edInput.setOnEditorActionListener(new OnEditorActionListener()
				{
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
					{

						if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
						{
							hideKeyPad(_tmp2.edInput);
//		    				inputUpdateValue(tmpBar, _tmp2);
//		    				updateValue(tmpBar);
						}
						return true;
					}
				});
				tmp2.edInput.setOnFocusChangeListener(new OnFocusChangeListener()
				{

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						bEditing = false; //2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교
						//2013. 8. 27 상세설정창 EditText 터치시 전체선택 >>
						if(!hasFocus) {
							inputUpdateValue(tmpBar, _tmp2.edInput);
//							updateValue(tmpBar);
//							isFocus = false;
//							isFocusView = v;

							isFocusEdit =_tmp2.edInput;
							isFocusSeekBar = tmpBar;

						} else {
							isFocus = true;
							UnderLineEditText edit = (UnderLineEditText)v;
							edit.edInput.selectAll();
							//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 >>
							//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 >>
//							if(edit.getText().length() > 0) {
//								btnEditSoftKeypadConfirm.setEnabled(true);
//								btnEditSoftKeypadConfirm.setTextColor(CoSys.HIGH_EMPHASIS);
//							} else {
//								btnEditSoftKeypadConfirm.setEnabled(false);
//								btnEditSoftKeypadConfirm.setTextColor(CoSys.DISABLE_TEXT_COLOR);
//							}
							//2023.11.27 by CYJ - kakaopay (기획 변경). 에러케이스 키보드 비활성화 기능 제거 <<
							//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 <<
						}
						//2013. 8. 27 상세설정창 EditText 터치시 전체선택 >>
					}

				});

				//			final SeekBar tmpBar = _seekBar[i];
				//			tmpBar.setId(i);
				//			tmpBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				//				public void onStopTrackingTouch(SeekBar seekBar) {
				//					System.out.println("onStopTrackingTouch");
				//					updateValue(seekBar);
				//				}
				//				public void onStartTrackingTouch(SeekBar seekBar) {
				//					System.out.println("onStartTrackingTouch");
				//			    }
				//				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				////					System.out.println("onProgressChanged");
				//					dragUpdateValue(seekBar);
				//				}
				//			}
				//			);

				layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
				final TextView tvColorOpen = (TextView)ll.findViewById(layoutResId);
				int[] getUpColor = dt.getUpColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				tvColorOpen.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						hideKeyPad();
						showColorPalette(tvColorOpen);
					}
				});

				//LineDraw 가 아니면 바타입으로 바꾸는 UI를 감춤
				if(dt instanceof LineDraw && !_graph.graphTitle.equals("주가이동평균") && !isOverlay(_graph.graphTitle))	//2015. 3. 2 채널지표에는 라인바 콤보 감추기
				{
					m_arComboBox.get(i-10).setVisibility(View.VISIBLE);
				}
				else
				{
					m_arComboBox.get(i-10).setVisibility(View.GONE);
//					ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)tvColorOpen.getLayoutParams();
//					param.width += (int)COMUtil.getPixel(80);
//					tvColorOpen.setLayoutParams(param);
				}
				//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<


				layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
				final TextView tvLineOpen = (TextView)ll.findViewById(layoutResId);
//				if(!(dt instanceof BarDraw) && !(dt instanceof SignalDraw))	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				if(!(dt instanceof BarDraw))	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				{
					int getUpLine = dt.getLineT();
					int lineImg = 0;
//					if(getUpLine>0)
//						lineImg=Lines[getUpLine-1];
//					tvLineOpen.setBackgroundResource(lineImg);
					tvLineOpen.setText(getUpLine + "px");
					tvLineOpen.setTag(String.valueOf(getUpLine));
					tvLineOpen.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							hideKeyPad();
							showLineSelect(tvLineOpen);
						}
					});
					if(7 == dt.getDrawType2())
					{
						enableApplyLineButton(false, i-10);
					}
				}
				//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				else if(dt instanceof SignalDraw){
//					tvLineOpen.setVisibility(View.INVISIBLE);
					tvLineOpen.setVisibility(View.VISIBLE);
				}
				//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
				else
				{
					tvLineOpen.setVisibility(View.GONE);
				}


//				//2012. 7. 2  라인굵기 가져오기
//				if(tmpBar!=null) {
//					tmpBar.setProgress(dt.getLineT());
//				}
				layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
				final CheckBox chkBox = (CheckBox)ll.findViewById(layoutResId);
				//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
				//2013.04.05 이미지 리사이징 코드 비적용   >>
				//			stateDrawable = imageChange(normal, press);
				//			chkBox.setButtonDrawable(stateDrawable);
				//2013.04.05 이미지 리사이징 코드 비적용   <<

				chkBox.setChecked(dt.isVisible());

				chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						if(m_bInitCompleted==false)
							return;
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
						if(!isChecked)
						{
							keepCheckboxStateAtLeastOne(buttonView);
						}
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
						reSetJipyo();
						COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
						COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
					}
				});

				//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				if(dt instanceof SignalDraw)
				{
					tmp.setText("매수");
					chkBox.setChecked(dt.isUpVisible());
				}
				//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

				layoutResId = this.getContext().getResources().getIdentifier("textView1", "id", this.getContext().getPackageName());
				TextView tv = (TextView)ll.findViewById(layoutResId);
				//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
				//2013.04.05 이미지 리사이징 코드 비적용   >>
				//			stateDrawable = imageChange(normal, press);
				//			chkBox.setButtonDrawable(stateDrawable);
				//2013.04.05 이미지 리사이징 코드 비적용   <<
				tv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						chkBox.setChecked(!chkBox.isChecked());

			
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
						if(!chkBox.isChecked())
						{
							keepCheckboxStateAtLeastOne(chkBox);
						}
						//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
						reSetJipyo();
						COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
						COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
			
					}
				});


				//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				if(dt instanceof SignalDraw && i == 10){
					ll = (LinearLayout)jipyoui.findViewById(ids[i+1]);
					ll.setVisibility(View.VISIBLE);

					layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
					CheckBox chkBox2 = (AppCompatCheckBox)ll.findViewById(layoutResId);

					chkBox2.setChecked(dt.isDownVisible());

					chkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener()
					{
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
						{
							if(m_bInitCompleted==false)
								return;
							//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
							if(!isChecked)
							{
								keepCheckboxStateAtLeastOne(buttonView);
							}
							//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
							reSetJipyo();
							COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
							COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
						}
					});

					layoutResId = this.getContext().getResources().getIdentifier("textView1", "id", this.getContext().getPackageName());
					tv = (TextView)ll.findViewById(layoutResId);
					tv.setText("매도");
					tv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							chkBox.setChecked(!chkBox.isChecked());


							//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
							if(!chkBox.isChecked())
							{
								keepCheckboxStateAtLeastOne(chkBox);
							}
							//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
							reSetJipyo();
							COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
							COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();

						}
					});

					layoutResId = context.getResources().getIdentifier("detail_drawtype_combo", "id", context.getPackageName());
					m_arComboBox.add((LinearLayout)ll.findViewById(layoutResId));
					m_arComboBox.get(i-9).setTag(String.valueOf(i-9));

					m_arComboBox.get(i-9).setVisibility(View.GONE);

					layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
					final TextView tvColorOpen2 = (TextView)ll.findViewById(layoutResId);
					int[] getDownColor = dt.getDownColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
					setButtonColorWithRound(tvColorOpen2, Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
					tvColorOpen2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showColorPalette(tvColorOpen2);
						}
					});

					//2020.05.21 by JJH >> 전략 탭에서 필요없는 레이아웃 파라미터 주석처리 start
//					ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)tvColorOpen2.getLayoutParams();
//					param.width += (int)COMUtil.getPixel(80);
//					tvColorOpen2.setLayoutParams(param);
					//2020.05.21 by JJH >> 전략 탭에서 필요없는 레이아웃 파라미터 주석처리 end


					layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
					final TextView tvLineOpen2 = (TextView)ll.findViewById(layoutResId);
//					tvLineOpen2.setVisibility(View.INVISIBLE);
					//2017.07.10 by pjm 수정중 >>
					if(!(dt instanceof BarDraw))
					{
						int getUpLine = dt.getDownLineT();
						int lineImg = 0;
						if(getUpLine>0)
							tvLineOpen2.setText(getUpLine + "px");
//							lineImg=Lines[getUpLine-1];
//						tvLineOpen2.setBackgroundResource(lineImg);
						tvLineOpen2.setTag(String.valueOf(getUpLine));
						tvLineOpen2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								showLineSelect(tvLineOpen2);
							}
						});

						if(7 == dt.getDrawType2())
						{
							enableApplyLineButton(false, i-10);
						}
					}
					else
					{
						tvLineOpen2.setVisibility(View.GONE);
					}
				}
				//2017.07.10 by pjm 수정중 <<
				//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
			}
		}

		//2013. 9. 3 지표마다 기준선 설정 추가>>
		//initBaseSetting(_graph); //2023.11.22 by CYJ - kakaopay 성능개선
		//2013. 9. 3 지표마다 기준선 설정 추가>>

		//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)>>
		initWebView();
		initTabButton();
		//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)<<

		m_bInitCompleted = true;	//2017.05.11 by LYH >> 전략(신호, 강약) 추가
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >>
		if(firstValue == null)
        	reSetJipyo();
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 <<

	}

	//2014. 11. 20 각 보조지표 설명/활용법 추가(상세설정창)>>
	/**
	 * 상세설정창의 설정/설명 탭 버튼 초기화
	 * */
	private void initTabButton()
	{
		/**
		 * "설정" 버튼을 누르면 설정화면이 나타나고 "설명" 버튼을 누르면 설명화면이 나타난다 
		 * */
		int layoutResId = this.getContext().getResources().getIdentifier("btn_valuesetting", "id", this.getContext().getPackageName());
		final Button btnValueSetting = (Button)jipyoui.findViewById(layoutResId);
		btnValueSetting.setSelected(true);
		layoutResId = this.getContext().getResources().getIdentifier("btn_description", "id", this.getContext().getPackageName());
		final Button btnDescription = (Button)jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("txt_description", "id", this.getContext().getPackageName());
		final TextView txtDescription = (TextView)jipyoui.findViewById(layoutResId);

		layoutResId = this.getContext().getResources().getIdentifier("detail_settingview", "id", this.getContext().getPackageName());
		final RelativeLayout rl_SettingView = (RelativeLayout)jipyoui.findViewById(layoutResId);

		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnInit", "id", this.getContext().getPackageName());
		final Button btnInit = (Button)jipyoui.findViewById(layoutResId);
//		layoutResId = this.getContext().getResources().getIdentifier("btn_description", "id", this.getContext().getPackageName());
//		final Button btnInfo = (Button)jipyoui.findViewById(layoutResId);


		if((_graph.getGraphTitle().contains("신호")) || (_graph.getGraphTitle().contains("약세")) || COMUtil.isMarketIndicator(_graph.getGraphTitle()) || bIsKakao) { //2023.11.07 by CYJ - kakaopay 지표설명 버튼 숨김처리 (1차)
			btnDescription.setVisibility(View.GONE);
			txtDescription.setVisibility(View.GONE);
		}
		else
			btnDescription.setVisibility(View.VISIBLE);

		btnValueSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_SettingView.setVisibility(View.VISIBLE);
				btnValueSetting.setSelected(true);

				mWebView.setVisibility(View.GONE);
				btnDescription.setSelected(false);
			}
		});

		btnDescription.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_SettingView.setVisibility(View.GONE);
				btnValueSetting.setSelected(false);

				mWebView.setVisibility(View.VISIBLE);
				btnInit.setVisibility(View.GONE);
				btnDescription.setVisibility(View.GONE);
				btnDescription.setSelected(true);
			}
		});

		txtDescription.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_SettingView.setVisibility(View.GONE);
				btnValueSetting.setSelected(false);

				mWebView.setVisibility(View.VISIBLE);
				btnInit.setVisibility(INVISIBLE);
				btnDescription.setSelected(true);
			}
		});
	}

	/**
	 * 지표설명 웹뷰 초기화
	 * */
	private void initWebView()
	{
		int layoutResId = this.getContext().getResources().getIdentifier("webview_description", "id", this.getContext().getPackageName());
		mWebView = (WebView)jipyoui.findViewById(layoutResId);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);

		//웹뷰 기본크기 확대
		mWebView.setInitialScale(320);
		//2015. 2. 26 갤노트4등 고해상도 단말기에서 지표설명 확대 더 크게 함>>
		float scale = COMUtil._chartMain.getResources().getDisplayMetrics().density;
		if (3 == scale)
		{
			mWebView.setInitialScale(420);
		}
		else if(4 == scale)
		{
			mWebView.setInitialScale(560);
		}
		else if(scale > 4)
		{
			mWebView.setInitialScale(680);
		}
		//2015. 2. 26 갤노트4등 고해상도 단말기에서 지표설명 확대 더 크게 함<<

		mWebView.setWebViewClient(new WebBrowserClient());
//		mWebView.addJavascriptInterface(new MethodForWebPage(), "MethodForWebPage");

		//URL에 설명이 들어가있는 HTML  파일을 추가하여 경로를 완성하고 웹뷰를 연다
		//mWebView.loadUrl("http://file.truefriend.com/updata/namo/" + _graph.getDefinitionHtmlString().toLowerCase());

        //mWebView.loadUrl("file:///" + COMUtil.strAppPath + "html/chart/" + _graph.getDefinitionHtmlString().toLowerCase());

		//2020.02.02 by HJW - 파일경로에서 URL로 변경 >>
//        String sFullPath = "";
//        String tmpPath = "html/chart/" + _graph.getDefinitionHtmlString().toLowerCase();
//        File file = new File(COMUtil.strAppPath+tmpPath);
//        try {
//            InputStream isFile = new FileInputStream(file);
//            if (isFile == null)
//                sFullPath = "file:///android_asset/" + tmpPath;
//            else
//                sFullPath = "file:///" + COMUtil.strAppPath + tmpPath;
//            mWebView.loadUrl(sFullPath);
//        }catch (Exception e){
//            sFullPath = "file:///android_asset/" + tmpPath;
//            mWebView.loadUrl(sFullPath);
//        }
		mWebView.loadUrl("http://mdev1.shinhaninvest.com/mobilealpha/chart/" + _graph.getDefinitionHtmlString().toLowerCase());
		//2020.02.02 by HJW - 파일경로에서 URL로 변경 <<
	}

	final class WebBrowserClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//이 함수를 오버라이드 하지 않으면 외부 브라우저가 불림
			view.loadUrl(url);
			return true;
		}

	}
	//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)<<

	//2013. 9. 3 지표마다 기준선 설정 추가>>
	//상세설정창 열릴때 기준선설정 값 세팅 함수 
	private void initBaseSetting(AbstractGraph agJipyo)
	{
		int layoutResId;

		//만약 기준선값이 없는 것 (이동평균 등) 이면 '기준선설정' 헤드라인과 기준선설정 레이아웃을 감추고,  아래 코드들을 실행하지 않고 나간다. 
		layoutResId = this.getContext().getResources().getIdentifier("baselinesettingtv", "id", this.getContext().getPackageName());
		TextView baselinesettingtv = (TextView)jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("baselinesettinglinear", "id", this.getContext().getPackageName());
		LinearLayout baselinesettinglinear = (LinearLayout)jipyoui.findViewById(layoutResId);

		//2014. 9. 11 매매 신호 보기 기능 추가 >>
		layoutResId = this.getContext().getResources().getIdentifier("sellingsignal_linear", "id", this.getContext().getPackageName());
		LinearLayout sellingSignal_Linear = (LinearLayout)jipyoui.findViewById(layoutResId);
		//2014. 9. 11 매매 신호 보기 기능 추가 <<

		if(agJipyo.base==null)
		{
			baselinesettingtv.setVisibility(View.GONE);		//기준선설정 헤드라인 
			baselinesettinglinear.setVisibility(View.GONE);	//기준선설정 레이아웃을 포함한 부모뷰. 아래 기준선설정1,2 는 가중치레이아웃 문제 때문에 GONE 이 아닌  
			//invisible 상태이다. 그래서 base가 없을 때는 부모뷰 자체를 gone 시켜준다.

			sellingSignal_Linear.setVisibility(View.GONE);	//2014. 9. 11 매매 신호 보기 기능 추가

			return;
		}
		else
		{
			//2023.11.07 by CYJ - kakaopay 디자인 매매신호 숨김처리 >>
//			baselinesettingtv.setVisibility(View.VISIBLE);
//			sellingSignal_Linear.setVisibility(View.VISIBLE);	//2014. 9. 11 매매 신호 보기 기능 추가
			//2023.11.07 by CYJ - kakaopay 디자인 매매신호 숨김처리 <<
		}

		int nBaseSettingStartIdx = 0;	//기준선설정 레이아웃 시작인덱스 
		int nBaseSettingEndIdx = agJipyo.base.length;

		for(int i=nBaseSettingStartIdx; i<nBaseSettingEndIdx; i++)
		{
			//기준선설정 레이아웃은 기본적으로 보이지 않는 상태이다. 해당하는 것을 보이게 설정. 
			LinearLayout linearParentRow = (LinearLayout)jipyoui.findViewById(baseSettingIds[i]);
//			linearParentRow.setVisibility(View.VISIBLE);  //2023.11.07 by CYJ - kakaopay 디자인 설정창 기준선 숨김처리

			//기준선값을 EditText 에 세팅 
			String strId = "ed_basesetting_"+String.valueOf((i+1));
			layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
			UnderLineEditText edBaseSetting = (UnderLineEditText)linearParentRow.findViewById(layoutResId);
			edBaseSetting.setText(String.valueOf(agJipyo.base[i]));
			edBaseSetting.edInput.setTypeface(COMUtil.numericTypefaceMid);

			//2017.06.30 by LYH >> 기준선 -값 처리. <<
			edBaseSetting.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

			//값변경하고 완료/다음 버튼등을 눌렀을때 처리 리스너
			final UnderLineEditText _edBaseSetting = edBaseSetting;
			edBaseSetting.edInput.setOnEditorActionListener(new OnEditorActionListener()
			{
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
				{
					if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
					{
						reSetJipyo();
						hideKeyPad(_edBaseSetting.edInput);
					}
					return true;
				}
			});

			//포커스 사라졌을때의 저장처리 
			edBaseSetting.setOnFocusChangeListener(new OnFocusChangeListener()
			{

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(!hasFocus) {
						reSetJipyo();
                        COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();	//체크상태에 따라 매매신호 그리거나 지우기
					} else {
						UnderLineEditText edit = (UnderLineEditText)v;
						edit.edInput.selectAll();
					}
				}

			});

			//edBaseSetting.setOnBackButtonListener(this);

			//체크박스 
			final AbstractGraph _agJipyo = agJipyo;

			strId = "chk_basesetting_"+String.valueOf((i+1));
			layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
			final CheckBox chkBaseSetting = (AppCompatCheckBox)linearParentRow.findViewById(layoutResId);

			//기준선1의 체크인지 2의 체크인지에 따라 지표그래프(agJipyo) 의 가시성 플레그 값을 체크박스에 세팅해줌. (tag값은 xml 에 각각 1, 2로 세팅되어있음)
			if(chkBaseSetting.getTag().equals("1"))
			{
				chkBaseSetting.setChecked(agJipyo.isBaseLineVisiility(0));
			}
			else
			{
				chkBaseSetting.setChecked(agJipyo.isBaseLineVisiility(1));
			}

			//체크박스 눌렸을 때의 처리. (체크박스 값에 따라서 기준선값을 보이거나 숨김) 
			chkBaseSetting.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reSetJipyo();	//저장하고 
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();  //기준선을 가시여부에 따라 다시 그려줌 
				}
			});
			
			
			strId = "tv_basesetting_"+String.valueOf((i+1));
			layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
			TextView tv = (TextView)linearParentRow.findViewById(layoutResId);
			
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					chkBaseSetting.setChecked(!chkBaseSetting.isChecked());
					reSetJipyo();	//저장하고 
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();  //기준선을 가시여부에 따라 다시 그려줌 
												
				}
			});
		}

		//2014. 9. 11 매매 신호 보기 기능 추가 >>
		final CheckBox chk_SellingSignal = (AppCompatCheckBox)sellingSignal_Linear.findViewById(this.getContext().getResources().getIdentifier("chk_sellingsignal", "id", this.getContext().getPackageName()));
		TextView tv_SellingSignal = (TextView)sellingSignal_Linear.findViewById(this.getContext().getResources().getIdentifier("tv_sellingsignal", "id", this.getContext().getPackageName()));
		LinearLayout ll_SellingSignal = (LinearLayout)sellingSignal_Linear.findViewById(this.getContext().getResources().getIdentifier("sellingsignal_linear", "id", this.getContext().getPackageName()));

		if(		(agJipyo.getGraphTitle().equals("VR") || agJipyo.getGraphTitle().contains("VR*")) ||
				(agJipyo.getGraphTitle().equals("심리도") || agJipyo.getGraphTitle().contains("심리도*")) ||
				(agJipyo.getGraphTitle().equals("Chaikins OSC") || agJipyo.getGraphTitle().contains("Chaikins OSC*")) ||
				(agJipyo.getGraphTitle().equals("Mass Index") || agJipyo.getGraphTitle().contains("Mass Index*")) ||
				(agJipyo.getGraphTitle().equals("DMI") || agJipyo.getGraphTitle().contains("DMI*")) ||
				(agJipyo.getGraphTitle().equals("ADX") || agJipyo.getGraphTitle().contains("ADX*")) ||
				(agJipyo.getGraphTitle().equals("LRS") || agJipyo.getGraphTitle().contains("LRS*"))||
				(agJipyo.getGraphTitle().equals("EOM") || agJipyo.getGraphTitle().contains("EOM*"))||
				(agJipyo.getGraphTitle().equals("Sigma") || agJipyo.getGraphTitle().contains("Sigma*"))||
				(agJipyo.getGraphTitle().equals("Price Oscillator") || agJipyo.getGraphTitle().contains("Price Oscillator*"))||
				(agJipyo.getGraphTitle().equals("Price ROC") || agJipyo.getGraphTitle().contains("Price ROC*"))||
				(agJipyo.getGraphTitle().equals("VHF") || agJipyo.getGraphTitle().contains("VHF*"))||
				(agJipyo.getGraphTitle().equals("RMI") || agJipyo.getGraphTitle().contains("RMI*"))||
				(agJipyo.getGraphTitle().equals("NCO") || agJipyo.getGraphTitle().contains("NCO*"))||
				(agJipyo.getGraphTitle().equals("Chaikins Volatility") || agJipyo.getGraphTitle().contains("Chaikins Volatility*"))||
				(agJipyo.getGraphTitle().equals("VROC") || agJipyo.getGraphTitle().contains("VROC*")))		//2015. 2. 13 LRS 지표 추가
		//2015. 3. 6 중복지표 추가시 무조건 매매신호 표시
		{
			//위 지표는 매매신호가 없으므로 체크를 감춘다 
			ll_SellingSignal.setVisibility(View.GONE);
			chk_SellingSignal.setVisibility(View.GONE);
			tv_SellingSignal.setVisibility(View.GONE);
			chk_SellingSignal.setChecked(false);
			agJipyo.setSellingSignalShow(false);
		}
		else
		{
			ll_SellingSignal.setVisibility(View.VISIBLE);
			chk_SellingSignal.setVisibility(View.VISIBLE);
			tv_SellingSignal.setVisibility(View.VISIBLE);

			chk_SellingSignal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//체크박스 눌렀을 때의 처리
					reSetJipyo();	//체크값 저장 
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();	//체크상태에 따라 매매신호 그리거나 지우기
				}
			});
			
			chk_SellingSignal.setChecked(agJipyo.isSellingSignalShow());
			
			tv_SellingSignal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					chk_SellingSignal.setChecked(!chk_SellingSignal.isChecked());
					
					reSetJipyo();	//체크값 저장 
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();	//체크상태에 따라 매매신호 그리거나 지우기
				}
			});

			
		}
		//2014. 9. 11 매매 신호 보기 기능 추가 <<
	}
	//2013. 9. 3 지표마다 기준선 설정 추가>>

	//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>
	private void keepCheckboxStateAtLeastOne(CompoundButton chkSender)
	{
		/**
		 * 상세설정의 선/굵기 체크박스가 적어도 하나는 체크되어있게 하는 것이 목적. 
		 * 체크 해제시에만 해당 함수가 동작한다. 
		 * 1. 6개의 선굵기 체크박스중 몇개나 체크되어 있는지 확인한다 
		 * 2. 만약 현재 체크해제한 체크박스가 마지막으로 체크되어 있는 것이었다면, 다시 체크상태로 돌리고 메시지를 표시한다.
		 *
		 * **/


		int dCnt = _graph.getDrawTool().size();
		//체크박스가 몇개나 체크되어있는지를 검사.
		//2017.05.11 by LYH >> 전략(신호, 강약) 추가
		if( _graph.getDrawTool().get(0) instanceof SignalDraw)
			dCnt++;
		//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

		int i;
		//체크박스가 몇개나 체크되어있는지를 검사. 
		int nChkCnt=0;
		//2023.11.16 by CYJ - kakaopay 체크 한개 남기는 함수 중 부모 레이아웃을 잘못 바라보고 있는 현상 수정 >>
		LinearLayout ll_parent;
		int layoutResId;
		if(_graph.getGraphTitle().equals("주가이동평균")) {
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			ll_parent = (LinearLayout)jipyoui.findViewById(layoutResId);
		} else {
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			ll_parent = (LinearLayout)jipyoui.findViewById(layoutResId);
		}
		//2023.11.16 by CYJ - kakaopay 체크 한개 남기는 함수 중 부모 레이아웃을 잘못 바라보고 있는 현상 수정 <<
		for(i=0; i<dCnt; i++)
		{
			LinearLayout ll = (LinearLayout)ll_parent.findViewById(ids[i]); //2023.11.16 by CYJ - kakaopay 체크 한개 남기는 함수 중 부모 레이아웃을 잘못 바라보고 있는 현상 수정
			layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
			CheckBox chk_atIndex = (AppCompatCheckBox)ll.findViewById(layoutResId);
			if(chk_atIndex.isChecked())
			{
				nChkCnt++;
			}
		}

		//모든 체크박스가 체크해제 상태면, 현재 눌린 체크박스를 다시 체크상태로 만들고 메시지 표시 
		if(nChkCnt == 0)
		{
			//다시 체크상태
			chkSender.setChecked(true);
			//메시지 표시 
//			Toast.makeText(context, "최소 하나 이상의 구상설정이 존재해야 됩니다.", Toast.LENGTH_SHORT).show();
//			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(COMUtil._chartMain);  
//			alert_confirm.setMessage("최소 하나 이상의 구상설정이 존재해야 됩니다.").setNegativeButton("확인",  
//			new DialogInterface.OnClickListener() {  
//			    @Override  
//			    public void onClick(DialogInterface dialog, int which) {  
//			        // 'No'  
//			    return;  
//			    }  
//			});
//			alert_confirm.create().show();

			//2023.11.16 by CYJ - kakaopay 최소 하나의 설정이 존재하는 안내창 >>
//			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//			alert.setTitle("");
//			alert.setMessage("최소 하나 이상의 구성설정이 존재해야 됩니다.");
//			alert.setOkButton("확인", null);
//			alert.show();
//			COMUtil.g_chartDialog = alert;
			COMUtil.showToast(COMUtil._chartMain.getString(R.string.kfit_dialog_at_last_one_message), 1, context); //2023.11.17 by CYJ - showToast Gravity 지정
			//2023.11.16 by CYJ - kakaopay 최소 하나의 설정이 존재하는 안내창 <<
		}
		else
		{
			//정상적으로 체크박스가 여러개 체크된 상태였다면 그냥 리턴. 
			return;
		}
	}
	//2013. 8. 30 상세설정 선굵기설정  체크박스 모두 해제되지 않도록 하고 마지막체크박스 해제할 때 메시지>>

	protected void showLineColorPalette(TextView tvColorOpen, TextView tvLineOpen)
	{
		hideKeyPad();

		int colorTag = 1;
		int lineTag = 1;
		try{
			String selColor = (String) tvColorOpen.getTag();
			colorTag = Integer.valueOf(selColor);

			String selLine = (String) tvLineOpen.getTag();
			lineTag = Integer.valueOf(selLine);
		}catch(Exception e){
			e.printStackTrace();
		}

		//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
		lineColorPalette = null;
		if(lineColorPalette == null) {
			lineColorPalette = new LineColorDialog(context, 4, 7, tvColorOpen, colorTag, tvLineOpen, lineTag);
		} else {
			lineColorPalette.tvColor = tvColorOpen;
			lineColorPalette.colorTag = colorTag;
			lineColorPalette.tvLine = tvLineOpen;
			lineColorPalette.lineTag = lineTag;
		}
		//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<

		lineColorPalette.setParent(this);
		//2012. 7. 20  팔레트 밖의 영역을 터치하면 닫히도록 설정
		lineColorPalette.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = lineColorPalette.getWindow().getAttributes();
//		params.y = nLineBtnTop;
//		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*2;
		//params.gravity = Gravity.CENTER;
		params.x = 0;
		params.y = - params.height;
		params.gravity = Gravity.BOTTOM;
		lineColorPalette.show();
	}


	//2012. 8. 28 showColorPalette 를 자식클래스에서 이용가능하게 하기위하여 protected 로 변경  : I97, I98
	protected void showColorPalette(TextView tvColorOpen)
	{
		int colorTag = 1;
		try{
			String selColor = (String) tvColorOpen.getTag();
			colorTag = Integer.valueOf(selColor);
		}catch(Exception e){
			e.printStackTrace();
		}
//		palette = new paletteDialog(context, 4, 6, tvColorOpen);
		paletteDialog palette = new paletteDialog(context, 4, 6, tvColorOpen, colorTag);

		palette.setParent(this);
		//2012. 7. 20  팔레트 밖의 영역을 터치하면 닫히도록 설정 
		palette.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = palette.getWindow().getAttributes();
//		params.y = nLineBtnTop;
//		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*2;
		params.gravity = Gravity.CENTER;
		palette.show();
	}
	public void updateValue(final SeekBar seekBar) {
		mHandler.post(new Runnable() {
			public void run() {

				//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
				reSetJipyo();
				COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
				COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
				//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<

				//changeLabelColorThickSetting(seekBar);	//2013. 8. 27 주가이평/거래량이평에서 변수설정 값 변경시 색굵기설정의 숫자도 같이 변경>> //2023.11.22 by CYJ - kakaopay 성능 개선
			}
		});
	}

	protected void showLineSelect(TextView tvLineOpen)
	{
		int lineTag = 1;
		try{
			String selLine = (String) tvLineOpen.getTag();
			lineTag = Integer.valueOf(selLine);
		}catch(Exception e){
			e.printStackTrace();
		}
		LineDialog linepalette = new LineDialog(context, 5, 1, tvLineOpen, lineTag);
		linepalette.setParent(this);
		linepalette.setCanceledOnTouchOutside(false);
		int[] location = new int[2];
		tvLineOpen.getLocationOnScreen(location);
		//드랍다운리스트뷰의 너비
		int nLineBtnWidth = tvLineOpen.getWidth();
//		//드랍다운리스트뷰의 x축좌표
//		int nLineBtnLeft = location[0]+(int)COMUtil.getPixel(3);
//		//드랍다운리스트뷰의 y축좌표
//		int nLineBtnTop = location[1]-(int)COMUtil.getPixel(56*5);
		WindowManager.LayoutParams params = linepalette.getWindow().getAttributes();
//		params.y = nLineBtnTop;
//		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*2;
		params.gravity = Gravity.CENTER;
		linepalette.show();
	}

	public void dragUpdateValue(final SeekBar seekBar) {
		int inx = seekBar.getId();
		LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[inx]);
		ll.setVisibility(View.VISIBLE);

		int layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//		final TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 설정값.
		//final EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		final UnderLineEditText tmp = (UnderLineEditText)ll.findViewById(layoutResId); //지표 설정값.

		mHandler.post(new Runnable() {
			final SeekBar _seekBar = seekBar;
			public void run() {
				int tval = seekBar.getProgress();
				if(tval==0) seekBar.setProgress(1);
				//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
//	        	  tmp.setText(""+seekBar.getProgress());
				setEditTextValue(tmp.edInput, (float)seekBar.getProgress(), true);
				//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<
			}
		});
	}

	//2012. 12. 20  프로그레스바 값 나타내는 textview가 edittext로 바뀌어서  입력값을 프로그레스바에 적용시키기 위한 함수 
	public void inputUpdateValue(final SeekBar seekBar, EditText edValueBox) {
		int inx = seekBar.getId();
		LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[inx]);
		ll.setVisibility(View.VISIBLE);

		//2023.11.30 by CYJ - 소수점 관련 지표 이슈로 원복 후 2차개발에 진행 >>
		String strValue = edValueBox.getText().toString().trim().replaceAll(",", ""); //2023.11.29 by CYJ - 입력값 세자리마다 (,) 추가
//		String strValue = edValueBox.getText().toString().trim();
		//2023.11.30 by CYJ - 소수점 관련 지표 이슈로 원복 후 2차개발에 진행 <<
		if(strValue.equals(""))
		{
			strValue = "0"; //2023.11.22 by CYJ - 입력값 없을떈, 0값으로 넣어서 최소값으로 설정되도록 수정
		}

		int nValue = -1;
		try {
			nValue = Integer.parseInt(strValue);
		} catch(Exception e) {
			try {
				BigInteger bigInt = new BigInteger(strValue);
				nValue = Math.abs(bigInt.intValue());
			} catch(Exception ee){

			}
		}

        if( ( _graph.getGraphTitle().contains("Parabolic SAR") && (((String)edValueBox.getTag()).equals("0") || ((String)edValueBox.getTag()).equals("1")) )  ||
				//2017.05.11 by LYH << 전략(신호, 강약) 추가
				( _graph.getGraphTitle().contains("Parabolic SAR 신호") && (((String)edValueBox.getTag()).equals("0") || ((String)edValueBox.getTag()).equals("1")) )  ||
				( _graph.getGraphTitle().contains("Parabolic SAR 강세약세") && (((String)edValueBox.getTag()).equals("0") || ((String)edValueBox.getTag()).equals("1")) )  ||
				//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
                ( isBollingerBand() && ((String)edValueBox.getTag()).equals("1")) ||
				( _graph.getGraphTitle().contains("Envelope") && (((String)edValueBox.getTag()).equals("1") || ((String)edValueBox.getTag()).equals("2")) )) //2020.11.27 by HJW - Envelope 지표 소수점 처리
        {
            //2016.05.02 by LYH >> 설정값 소수점 4자리 처리.
//            nValue = Integer.parseInt(String.format("%.0f", Float.parseFloat(strValue)*100));
            //2016.05.02 by LYH << 설정값 소수점 4자리 처리.

			//2023.11.23 by lyk - 큰 값 입력시 죽는 오류 수정 >>
			String sFmtStr = String.format("%.0f", Float.parseFloat(strValue)*100);
			try {
				nValue = Integer.parseInt(sFmtStr);
			} catch(Exception e) {
				try {
					BigInteger bigInt = new BigInteger(sFmtStr);
					nValue = Math.abs(bigInt.intValue());
				} catch(Exception ee){

				}
			}
			//2023.11.23 by lyk - 큰 값 입력시 죽는 오류 수정 <<
        }

		if(nValue > seekBar.getMax())
		{
			nValue = seekBar.getMax();
			//2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 >>
			if(this.getVisibility() == View.VISIBLE) {
				COMUtil.showToast(COMUtil._chartMain.getString(R.string.kfit_dialog_value_change_max), 0, context); //2023.11.17 by CYJ - showToast Gravity 지정
			}
			//2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 <<
		} else if(nValue < 1)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				nValue = seekBar.getMin();
			}
			//2023.11.17 by lyk - 캔들차트개선 - 최소입력값 미만 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 >>
			if(this.getVisibility() == View.VISIBLE) {
				COMUtil.showToast(COMUtil._chartMain.getString(R.string.kfit_dialog_value_change_min), 0, context); //2023.11.17 by CYJ - showToast Gravity 지정
			}
			//2023.11.17 by lyk - 캔들차트개선 - 최소입력값 미만 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 <<
		}

		final int _nNewValue = nValue;
		final EditText _edValueBox = edValueBox;

//	      mHandler.post(new Runnable() {
//	          public void run() {
		seekBar.setProgress(_nNewValue);
		_edValueBox.setText(String.valueOf(seekBar.getProgress()));
//	          }
//	        });

	}

//	public void inputUpdateValueNotTexhtChange(final SeekBar seekBar, EditText edValueBox) {
//		int inx = seekBar.getId();
//		LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[inx]);
//		ll.setVisibility(View.VISIBLE);
//		
//		String strValue = edValueBox.getText().toString().trim();
//		if(strValue.equals(""))
//		{
//			strValue = "1";
//		}
//		
//		int nValue = -1;
//		try {
//			nValue = Integer.parseInt(strValue);
//		} catch(Exception e) {
//			
//		}
//		
//		if(nValue < 1)
//		{
//			nValue = 1;
//		}
//		else if(nValue > seekBar.getMax())
//		{
//			nValue = seekBar.getMax();
//		}
//
//		final int _nNewValue = nValue;
//		
//	      mHandler.post(new Runnable() {
//	          public void run() {
//	        	  seekBar.setProgress(_nNewValue);
//	          }
//	        });
//		
//	}

	//2012. 12. 20 문자 분석툴 사용시 키패드 감추기 위함 
	public void hideKeyPad(EditText edText)
	{
		if(edText != null) {
			imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
			edText.clearFocus();

			if(isFocusEdit != null) {
				isFocusEdit.clearFocus();
			}

		}
		//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 >>
		try {
//			inputUpdateValue(isFocusSeekBar, edText);
			btnEditSoftKeypadConfirm.setVisibility(GONE);

			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable()  {
				public void run() {
					if(btnEditSoftKeypadConfirm.getVisibility() == GONE) {
						ll_bottom_view.setVisibility(VISIBLE);
					}
				}
			}, 200); // 0.2초후

		} catch (Exception e) {

		}
		//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 <<
	}
	public void hideKeyPad()
	{
		if(isFocusEdit != null) {
			imm.hideSoftInputFromWindow(isFocusEdit.getWindowToken(), 0);
			isFocusEdit.clearFocus();
		}
		//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 >>
		try {
			btnEditSoftKeypadConfirm.setVisibility(GONE);
			ll_bottom_view.setVisibility(VISIBLE);
		} catch (Exception e) {

		}
		//2023.11.07 by CYJ - kakaopay 소프트키보드 확인 버튼 <<
	}
	public void reSetOriginal() {
		if(_graph==null) return;
		int i_cnt[] = _graph.org_interval;
		for(int i=0; i<i_cnt.length; i++) {

			LinearLayout baseLayout = null;
			int layoutResId = 0;
			if(_graph.getGraphTitle().equals("주가이동평균")) {
				layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
				baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
			} else {
				layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
				baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
			}

			LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[i]);
			if(ll==null)
				return;
//			LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[i]);화

			if(_graph.getGraphTitle().equals("주가이동평균")) {
				if (i < 7) {
					ll.setVisibility(View.VISIBLE);
				} else {
					ll.setVisibility(View.GONE);
				}
			} else {
				ll.setVisibility(View.VISIBLE);
			}

			layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//			TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 설정 값.
			//EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정 값.
			UnderLineEditText tmp = (UnderLineEditText) ll.findViewById(layoutResId); //2023.11.15 by CYJ - kakaopay 설정창 초기화 수정

			//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
//			tmp.setText(""+i_cnt[i]);
			setEditTextValue(tmp.edInput, i_cnt[i], true); //2023.11.15 by CYJ - kakaopay 설정창 초기화 수정
			//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<

			tmpBar = _seekBar[i];
			tmpBar.setProgress(i_cnt[i]);
			//changeLabelColorThickSetting(tmpBar);	//2013. 8. 27 주가이평/거래량이평에서 변수설정 값 변경시 색굵기설정의 숫자도 같이 변경>> //2023.11.22 by CYJ - kakaopay 성능 개선
		}

		boolean isMoveLine = false;
		if(_graph.graphTitle.equals("주가이동평균")) {
			isMoveLine = true;

			int layoutResId = this.context.getResources().getIdentifier("jipyo_baseprice", "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapterData = ArrayAdapter.createFromResource(this.context, layoutResId, this.getContext().getResources().getIdentifier("pop_back", "drawable", this.getContext().getPackageName()));

			layoutResId = this.context.getResources().getIdentifier("jipyo_averagetype", "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapterAverage = ArrayAdapter.createFromResource(this.context, layoutResId, this.getContext().getResources().getIdentifier("pop_back", "drawable", this.getContext().getPackageName()));

			for(int i = 1; i <= line_cnt; i++){

				layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
				LinearLayout averageLayout = (LinearLayout)jipyoui.findViewById(layoutResId);

				String strId = "gsetItem" + String.valueOf(i);
				layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
				LinearLayout gsetItemLayout = (LinearLayout)averageLayout.findViewById(layoutResId);

				int thickInt = 2;  //2023.11.08 by CYJ - kakaopay 주가이동평균 기본 굵기 변경

				int resId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
				CheckBox chkBox = (AppCompatCheckBox)gsetItemLayout.findViewById(resId);

				//이동평균선 checkbox 처리
				if(isMoveLine) {
					if(i<6) {
						chkBox.setChecked(true);
					} else {
						chkBox.setChecked(false);
					}
				} else {
					chkBox.setChecked(true);
				}

				//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 >>
				Vector<DrawTool> drawTools = _graph.getDrawTool();
				if (drawTools.isEmpty()) {
					break;
				}

				DrawTool dt = drawTools.get(i-1);
				//DrawTool dt = _graph.getDrawTool().get(i-1);
				//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 <<

				layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
				TextView tvColorOpen = (TextView)gsetItemLayout.findViewById(layoutResId);
				int[] getDefColor = dt.getDefUpColor();

//				int[] getDefColor = dt.getDefUpColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));

//				((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));

				layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
				final TextView tvLineOpen = (TextView)gsetItemLayout.findViewById(layoutResId); //지표 설정 값.
				int getUpLine = dt.getLineT();
//				int lineImg = Lines[0];
//				tvLineOpen.setBackgroundResource(lineImg);
				//2023.11.08 by CYJ - kakaopay 주가이동평균 기본 굵기 변경 >>
				tvLineOpen.setText(thickInt+"px");
				tvLineOpen.setTag(""+thickInt);
				//2023.11.08 by CYJ - kakaopay 주가이동평균 기본 굵기 변경 <<

				layoutResId = this.getContext().getResources().getIdentifier("jipyo_button_price", "id", this.getContext().getPackageName());

				Button btn = (Button)gsetItemLayout.findViewById(layoutResId); //지표 설정 값.

				btn.setText(String.valueOf(comboAdapterData.getItem(0)));
				layoutResId = this.getContext().getResources().getIdentifier("jipyo_button_type", "id", this.getContext().getPackageName());

				btn = (Button)gsetItemLayout.findViewById(layoutResId); //지표 설정 값.
				btn.setText(String.valueOf(comboAdapterAverage.getItem(0)));
			}
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//			layoutResId = this.context.getResources().getIdentifier("jipyo_baseprice", "array", this.context.getPackageName());
//			ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
//			btnComboBasePrice_average.setText(String.valueOf(comboAdapter.getItem(0)));
//
//			layoutResId = this.context.getResources().getIdentifier("jipyo_averagetype", "array", this.context.getPackageName());
//			comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
//			btnComboAverageType_average.setText(String.valueOf(comboAdapter.getItem(0)));
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<
		}

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
		if(isBollingerBand())
		{
			int layoutResId = this.context.getResources().getIdentifier("jipyo_baseprice_bollingerband", "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
			btnComboBasePrice.setText(String.valueOf(comboAdapter.getItem(0)));

			layoutResId = this.context.getResources().getIdentifier("jipyo_calctype_bollingerband", "array", this.context.getPackageName());
			comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
			btnComboAverageType.setText(String.valueOf(comboAdapter.getItem(0)));
		}
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

		//2012. 6. 28 색굵기 초기화
		if(_graph.graphTitle.equals("매물대"))
		{
			line_cnt = 3;
//			DrawTool dt = _graph.getDrawTool().get(0);
			//int[] getUpColor = CoSys.CHART_COLORS[0];
			int[] getUpColor = CoSys.VOLUMESCALE_BASE;
			int layoutResId;
			for(int i=1; i<=3; i++)
			{
				String strId = "standscale_row_" + String.valueOf(i) + "_colortextview";
				layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
				final TextView tvColorOpen = (TextView)jipyoui.findViewById(layoutResId);
				if(i==2)
					//getUpColor = CoSys.CHART_COLORS[2];
					getUpColor = CoSys.VOLUMESCALE_CUR;
				else if(i==3)
					//getUpColor = CoSys.CHART_COLORS[1];
					getUpColor = CoSys.VOLUMESCALE_MAX;
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
			}

			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
			layoutResId = this.getContext().getResources().getIdentifier("chk_standscaleshow", "id", this.getContext().getPackageName());
			CheckBox chk_standscaleshow = (AppCompatCheckBox)jipyoui.findViewById(layoutResId);
			chk_standscaleshow.setChecked(true);
			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
		}
		else
		{
			if(!_graph.graphTitle.equals("주가이동평균") && !bIsKakao) {
				for (int i = 10; i < line_cnt + 10; i++) {
					LinearLayout ll = (LinearLayout) jipyoui.findViewById(ids[i]);
					ll.setVisibility(View.VISIBLE);

					int resId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
					CheckBox chkBox = (AppCompatCheckBox) ll.findViewById(resId);

					//이동평균선 checkbox 처리
					if (isMoveLine) {
						if ((i - ids.length / 2) < 4) {
							chkBox.setChecked(true);
						} else {
							chkBox.setChecked(false);
						}
					} else {
						chkBox.setChecked(true);
					}

					int layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
					//			TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 설정 값.
//					EditText tmp = (EditText) ll.findViewById(layoutResId); //지표 설정 값.
					UnderLineEditText tmp = (UnderLineEditText) ll.findViewById(layoutResId); //지표 설정 값.

					if (IsOverayJipyo(_graph.graphTitle))
						tmp.edInput.setText("3");
					else
						tmp.edInput.setText("1");

					//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 >>
					Vector<DrawTool> drawTools = _graph.getDrawTool();
					if (drawTools.isEmpty()) {
						break;
					}

					DrawTool dt = drawTools.get(i - 10);
					//DrawTool dt = _graph.getDrawTool().get(i - 10);
					//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 <<

					layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
					TextView tvColorOpen = (TextView) ll.findViewById(layoutResId);
					int[] getDefColor = dt.getDefUpColor();
//					tvColorOpen.setBackgroundColor(Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
//					tvColorOpen.setTag("" + Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
					setButtonColorWithRound(tvColorOpen, Color.rgb(getDefColor[0], getDefColor[1], getDefColor[2]));
                    //2016.12.08 by LYH >> 설정 뜨는 속도 개선
//					tmpBar = _seekBar[i];
//					if (IsOverayJipyo(_graph.graphTitle))
//						tmpBar.setProgress(3);
//					else
//						tmpBar.setProgress(1);
                    //2016.12.08 by LYH >> 설정 뜨는 속도 개선 end

					layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
					final TextView tvLineOpen = (TextView) ll.findViewById(layoutResId); //지표 설정 값.
//				int getUpLine = dt.getLineT();
//					int lineImg = Lines[0];
//					tvLineOpen.setBackgroundResource(lineImg);
					tvLineOpen.setText(dt.getLineT() + "px");
					tvLineOpen.setTag("" + 1);
					if (IsOverayJipyo(_graph.graphTitle))
					{
//						lineImg = Lines[1];
//						tvLineOpen.setBackgroundResource(lineImg);
						tvLineOpen.setText(dt.getLineT()+1 + "px");
						tvLineOpen.setTag("" + 2);
					}




					//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
//				RadioGroup rg_DrawType = (RadioGroup)ll.findViewById(this.getContext().getResources().getIdentifier("detail_drawtype_radiogroup", "id", this.getContext().getPackageName()));
//				RadioButton rb_Line = (RadioButton)rg_DrawType.findViewById(this.getContext().getResources().getIdentifier("detail_radiobtn_line", "id", this.getContext().getPackageName()));
//				rb_Line.setChecked(true);
					TextView tv_comboText = (TextView) ll.findViewById(context.getResources().getIdentifier("detail_drawtype_combo_text", "id", context.getPackageName()));
					tv_comboText.setText("라인");
					//2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임>> : LineDraw면서 선 또는 바타입 일때만 변경
					if (1 == dt.getDrawType1() && (0 == dt.getDrawType2() || 7 == dt.getDrawType2())) {
						dt.setDrawType2(0);
					}
					//2015. 3. 2 라인바 적용 후 지표설정 저장값 꼬임<<
					if(!(dt instanceof BarDraw))
					{
						enableApplyLineButton(true, i-10);
//						tvLineOpen.setVisibility(INVISIBLE);
					}

					//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

					//2017.05.11 by LYH >> 전략(신호, 강약) 추가
					if((dt instanceof SignalDraw))
					{
						ll = (LinearLayout)jipyoui.findViewById(ids[i+1]);

						resId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
						CheckBox chkBoxDown = (AppCompatCheckBox) ll.findViewById(resId);
						chkBoxDown.setChecked(true);

						layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
						TextView tvColorDown = (TextView) ll.findViewById(layoutResId);
						int[] getDefDownColor = dt.getDefDownColor();
						setButtonColorWithRound(tvColorDown, Color.rgb(getDefDownColor[0], getDefDownColor[1], getDefDownColor[2]));

						layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
						final TextView tvLineOpen2 = (TextView) ll.findViewById(layoutResId); //지표 설정 값.
//				int getUpLine = dt.getLineT();
//						int lineImg2 = Lines[0];
//						tvLineOpen2.setBackgroundResource(lineImg2);
						tvLineOpen2.setText(dt.getLineT() + "px");
						tvLineOpen2.setTag("" + 1);
					}
					//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
				}

				//2013. 9. 3 지표마다 기준선 설정 추가>>  : 기준선 기본값
				int i_base[] = _graph.org_base;
				if (null != i_base)        //2013. 10. 2  주가이동평균에서 초기화 누르면 죽는 현상
				{
					for (int i = 0; i < i_base.length; i++) {
						//기준선설정 레이아웃은 기본적으로 보이지 않는 상태이다. 해당하는 것을 보이게 설정.
						LinearLayout linearParentRow = (LinearLayout) jipyoui.findViewById(baseSettingIds[i]);
						linearParentRow.setVisibility(View.VISIBLE);

						//기준선값을 EditText 에 세팅
						String strId = "ed_basesetting_" + String.valueOf((i + 1));
						int layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
						UnderLineEditText edBaseSetting = (UnderLineEditText) linearParentRow.findViewById(layoutResId);
						edBaseSetting.setText(String.valueOf(i_base[i]));

						strId = "chk_basesetting_" + String.valueOf((i + 1));
						layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
						final CheckBox chkBaseSetting = (AppCompatCheckBox) linearParentRow.findViewById(layoutResId);
						chkBaseSetting.setChecked(true);


					}

					//2014. 9. 11 매매 신호 보기 기능 추가 >>
					LinearLayout sellingSignal_Linear = (LinearLayout) jipyoui.findViewById(this.getContext().getResources().getIdentifier("sellingsignal_linear", "id", this.getContext().getPackageName()));
					final CheckBox chk_SellingSignal = (AppCompatCheckBox) sellingSignal_Linear.findViewById(this.getContext().getResources().getIdentifier("chk_sellingsignal", "id", this.getContext().getPackageName()));
					chk_SellingSignal.setChecked(true);
					_graph.setSellingSignalShow(true);


					//2014. 9. 11 매매 신호 보기 기능 추가 <<
				}
				//2013. 9. 3 지표마다 기준선 설정 추가>>
			}
		}
	}

	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
	// 2014.3.5 setComboBox 함수 호출하여 콤보박스의 리스트 보여질때 좌표값을 직접 입력하지 않고 컨트롤좌표
//	private void setComboBox(final Button btnCombo, String strAdapterDataName) {
//		// 콤보박스 아래에 펼처지는 리스트뷰
//		ListView comboList = new ListView(this.context);
//		final String adapterName = strAdapterDataName;
//		// 인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서
//		// arrayadapter 를 만든다
//		int layoutResId = this.context.getResources().getIdentifier(
//				strAdapterDataName, "array", this.context.getPackageName());
//		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter
//				.createFromResource(this.context, layoutResId,
//						this.getContext().getResources().getIdentifier("dropdown_bg", "drawable", this.getContext().getPackageName()));
//
//		// 읽어온 배열값을 arraylist 에 저장.
//		final ArrayList<String> arStrComboData = new ArrayList<String>();
//		for (int i = 0; i < comboAdapter.getCount(); i++) {
//			arStrComboData.add(String.valueOf(comboAdapter.getItem(i)));
//		}
//
//		btnCombo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				m_selBtnCombo = btnCombo;
//				m_selBtnCombo.setSelected(true);
//				//2014. 5. 14 지표 상세설정창 UI >> : 눌린 뷰의 위치기반으로 left/top 마진 구하기
//				int[] location = new int[2];
//				v.getLocationOnScreen(location);
//				drBottom = new DRBottomDialog(context,0);
//
//				if (adapterName.equals("jipyo_baseprice")) {//종가기준콤보박스일 경우
//					drBottom.setTitle("기준");	//2020.05.08 by JJH >> 차트 설정 화면 - 오버레이 - bollinger band 콤보박스 텍스트 수정
//					drBottom.setCondListView(arStrComboData);
//				}
//				//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
//				else if (adapterName.equals("jipyo_baseprice_bollingerband")) {
//					drBottom.setTitle("기준");	//2020.05.08 by JJH >> 차트 설정 화면 - 오버레이 - bollinger band 콤보박스 텍스트 수정
//					drBottom.setCondListView(arStrComboData);
//				}
//				else if (adapterName.equals("jipyo_calctype_bollingerband")) {
//					drBottom.setTitle("계산");
//					drBottom.setCondListView(arStrComboData);
//				}
//				//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<
//				else {//이평선타입 콤보박스일경우
//					drBottom.setTitle("계산");
//					drBottom.setCondListView(arStrComboData);
//				}
//				drBottom.resetStrSelValue(btnCombo.getText().toString());
//				drBottom.show();
//
//				drBottom.setOnClickListItemListener(new DRBottomDialog.OnClickBottomViewListItemListener() {
//					@Override
//					public void onClick(View view, int index, String value) {
//
//						if (value.equals("  라인") || value.equals("  바")) {
//							int nComboboxTextIndex = Integer.parseInt((String)m_selBtnCombo.getTag());
//							m_arComboBoxText.get(nComboboxTextIndex).setText(value);
//
//							if(value.equals("  라인"))
//							{
//								enableApplyLineButton(false, nComboboxTextIndex);
//							}
//							else
//							{
//								enableApplyLineButton(true, nComboboxTextIndex);
//							}
//
//						}else{
//							m_selBtnCombo.setText(value);
//						}
//						reSetJipyo();
//						COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//						COMUtil._neoChart.repaintAll();
//						drBottom.dismiss();
//						//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업 start
//						m_selBtnCombo.setSelected(false);
//						drBottom =null;
//					}
//				});
//
//				drBottom.setOnDismissListener(new DialogInterface.OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface dialogInterface) {
//						m_selBtnCombo.setSelected(false);
//					}
//				});
//				//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업 end
//			}
//		});
//	}
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end
	private void setComboBox(Button btnCombo, String strAdapterDataName) {
		// 콤보박스 아래에 펼처지는 리스트뷰
		ListView comboList = new ListView(this.context);
		final String adapterName = strAdapterDataName;
		// 인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서
		// arrayadapter 를 만든다
		int layoutResId = this.context.getResources().getIdentifier(
				strAdapterDataName, "array", this.context.getPackageName());
		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter
				.createFromResource(this.context, layoutResId,
						this.getContext().getResources().getIdentifier("dropdown_bg", "drawable", this.getContext().getPackageName()));

		// 읽어온 배열값을 arraylist 에 저장.
		ArrayList<String> arStrComboData = new ArrayList<String>();
		for (int i = 0; i < comboAdapter.getCount(); i++) {
			arStrComboData.add(String.valueOf(comboAdapter.getItem(i)));
		}

		// 콤보버튼 태그 세팅
		btnCombo.setTag(strAdapterDataName);
//			 if(strAdapterDataName.equals("candle_samecolor"))
//			 {
//			 btnCombo.setText(String.valueOf(comboAdapter.getItem(0)));
//			 }
//			 else
//			 {
//			 btnCombo.setText(String.valueOf(comboAdapter.getItem(0)));
//			 }

		//>>>>>> 2014.3.5 종가기준콤보, 이평선타입 콤보박스
		if (adapterName.equals("jipyo_baseprice")) {//종가기준콤보박스일 경우
			comboList.setAdapter(new DropListAdapter2(arStrComboData, btnCombo));
		}
		//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
		else if (adapterName.equals("jipyo_baseprice_bollingerband")) {
			comboList.setAdapter(new DropListAdapter4(arStrComboData, btnCombo));
		}
		else if (adapterName.equals("jipyo_calctype_bollingerband")) {
			comboList.setAdapter(new DropListAdapter5(arStrComboData, btnCombo));
		}
		//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<
		else {//이평선타입 콤보박스일경우
			comboList.setAdapter(new DropListAdapter3(arStrComboData, btnCombo));
		}
		//<<<<<< 2014.3.5

		// 리스트뷰 속성 설정
		//리스트뷰 속성 설정
		comboList.setCacheColorHint(Color.WHITE);
		comboList.setScrollingCacheEnabled(false);
		comboList.setVerticalFadingEdgeEnabled(false);

		layoutResId = this.getResources().getIdentifier("pop_back", "drawable", this.context.getPackageName());
		comboList.setBackgroundResource(layoutResId);
//	          comboList.setDivider(new ColorDrawable(Color.rgb(209, 209, 209)));
		comboList.setDividerHeight((int)COMUtil.getPixel(0));
//        comboList.setPadding(
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1));

//		ImageView imageBottom = new ImageView(context);
//		layoutResId = this.getContext().getResources().getIdentifier("pop_back_bottom", "drawable", this.getContext().getPackageName());
//		imageBottom.setBackgroundResource(layoutResId);
//		imageBottom.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(5)));


		final ListView _comboList2 = comboList;
//		final ImageView imgBottom2 = imageBottom;
		final Button btnCombo2 = btnCombo;

		// btnCombo는 각 콤보박스 버튼임. 콤보박스 버튼 클릭하면 리스트뷰가 아래에 나오게 함
		btnCombo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//2014. 5. 14 지표 상세설정창 UI >> : 눌린 뷰의 위치기반으로 left/top 마진 구하기
				int[] location = new int[2];
				v.getLocationOnScreen(location);
//				int nDropdownViewLeft = location[0];
//				int nDropdownViewTop = location[1] + v.getHeight() + (int)COMUtil.getPixel(2);
				int nDropdownViewWidth = v.getWidth();
				int nDropdownViewLeft = location[0];
				int nDropdownViewTop = location[1] + v.getHeight();


				//2014. 5. 14 지표 상세설정창 UI <<

				if(comboPopup != null && !comboPopup.isShowing())
				{
					comboPopup = null;
					btnCombo2.setSelected(false);
					comboPopupLayout.removeAllViews();
				}

				//콤보박스가 열려있는상태 (not null)  면 닫고  아니면 생성해서 오픈
				if(comboPopup != null)
				{
					comboPopup.dismiss();
					btnCombo2.setSelected(false);
					comboPopup = null;
					comboPopupLayout.removeAllViews();
				}
				else
				{
					comboPopupLayout.addView(_comboList2);
//					comboPopupLayout.addView(imgBottom2);

					comboPopup = new PopupWindow(comboPopupLayout, v.getWidth(), LayoutParams.WRAP_CONTENT, true);
					comboPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
					comboPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.

					comboPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							btnCombo2.setSelected(false);
						}
					});

					//팝업 띄우기
//	  					comboCandlePopup.showAtLocation(comboCandleLayout, Gravity.TOP|Gravity.LEFT, (int)COMUtil.getPixel(nPopupLeftMargin), (int)COMUtil.getPixel(nPopupTopMargin));
					comboPopup.showAtLocation(comboPopupLayout, Gravity.NO_GRAVITY, nDropdownViewLeft, nDropdownViewTop);
					btnCombo2.setSelected(true);
				}
			}
		});
	}

	// 2014. 3. 4 종가기준선택콤보박스용 어댑터
	private class DropListAdapter2 extends BaseAdapter {
		private ArrayList<String> m_arrData;
		Button _btnCombo;

		public DropListAdapter2(ArrayList<String> arrayList, Button btnCombo) {
			super();
			m_arrData = arrayList;
			_btnCombo = btnCombo;
		}

		@Override
		public int getCount() {
			return m_arrData == null ? 0 : m_arrData.size();
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
			// 콤보박스 리스트뷰의 한 행의 속성은 여기서 정해짐.
			final int pos = position;
			TextView tv = (TextView) v;
//			COMUtil.setGlobalFont(parent);
			if (tv == null) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
//				tv.setBackgroundColor(Color.TRANSPARENT);
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//					tv.setSingleLine();
//					tv.setTextSize(13);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int) COMUtil.getPixel_W(120));
				// 2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int) COMUtil.getPixel_H(36));
				// 2012. 9. 12 콤보박스의 글씨 왼쪽 여백 추가
//				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//					tv.setPadding((int) COMUtil.getPixel(8), 0, 0, 0);
//				} else {
					tv.setPadding((int) COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if (m_arrData != null)
				tv.setText(m_arrData.get(position));

			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
			String strTitle = m_arrData.get(position);
			int nComboboxTextIndex = Integer.parseInt((String)_btnCombo.getTag());
			String strBaseComboTitle = m_arComboBoxDataType.get(nComboboxTextIndex).getText().toString();
			if(strBaseComboTitle.equals(strTitle))
			{
//			String strComboTitle = _btnCombo.getText().toString();
//			if (strComboTitle.equals(tv.getText())) {
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<

//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(245, 245, 245));
				tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else
			{
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(255, 255, 255));
				tv.setTextColor(Color.rgb(17, 17, 17));
			}

			// 값을 선택하면 리스트뷰를 닫음
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					_btnCombo.setText(String.valueOf(m_arrData.get(pos)));
					comboPopup.dismiss();
					comboPopup = null;

					comboPopupLayout.removeAllViews();
					reSetJipyo();
					COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
					COMUtil._neoChart.repaintAll();
				}
			});
			return tv;
		}
	}

	// 2014. 3. 4 이평선선택콤보박스용 어댑터
	private class DropListAdapter3 extends BaseAdapter {
		private ArrayList<String> m_arrData;
		Button _btnCombo;

		public DropListAdapter3(ArrayList<String> arrayList, Button btnCombo) {
			super();
			m_arrData = arrayList;
			_btnCombo = btnCombo;
		}

		@Override
		public int getCount() {
			return m_arrData == null ? 0 : m_arrData.size();
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
			// 콤보박스 리스트뷰의 한 행의 속성은 여기서 정해짐.
			final int pos = position;
			TextView tv = (TextView) v;
//			COMUtil.setGlobalFont(parent);
			if (tv == null) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
				tv.setBackgroundColor(Color.TRANSPARENT);
//					tv.setSingleLine();
//					tv.setTextSize(13);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int) COMUtil.getPixel_W(120));
				// 2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int) COMUtil.getPixel_H(36));
				// 2012. 9. 12 콤보박스의 글씨 왼쪽 여백 추가
//				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//					tv.setPadding((int) COMUtil.getPixel(8), 0, 0, 0);
//				} else {
					tv.setPadding((int) COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if (m_arrData != null)
				tv.setText(m_arrData.get(position));

			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
			String strTitle = m_arrData.get(position);
			int nComboboxTextIndex = Integer.parseInt((String)_btnCombo.getTag());
			String strAverageComboTitle = m_arComboBoxAverageType.get(nComboboxTextIndex).getText().toString();
			if(strAverageComboTitle.equals(strTitle))
			{
//			String strComboTitle = _btnCombo.getText().toString();
//			if (strComboTitle.equals(tv.getText())) {
			//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<
//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(245, 245, 245));
				tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else
			{
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(255, 255, 255));
				tv.setTextColor(Color.rgb(17, 17, 17));
			}


			// 값을 선택하면 리스트뷰를 닫음
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					_btnCombo.setText(String.valueOf(m_arrData.get(pos)));
					comboPopup.dismiss();
					comboPopup = null;
					comboPopupLayout.removeAllViews();

					reSetJipyo();
					COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
					COMUtil._neoChart.repaintAll();
				}
			});
			return tv;
		}
	}

    //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
	private class DropListAdapter4 extends BaseAdapter {
		private ArrayList<String> m_arrData;
		Button _btnCombo;

		public DropListAdapter4(ArrayList<String> arrayList, Button btnCombo) {
			super();
			m_arrData = arrayList;
			_btnCombo = btnCombo;
		}

		@Override
		public int getCount() {
			return m_arrData == null ? 0 : m_arrData.size();
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
			// 콤보박스 리스트뷰의 한 행의 속성은 여기서 정해짐.
			final int pos = position;
			TextView tv = (TextView) v;
//			COMUtil.setGlobalFont(parent);
			if (tv == null) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
				tv.setBackgroundColor(Color.TRANSPARENT);
//					tv.setSingleLine();
//					tv.setTextSize(13);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int) COMUtil.getPixel(120));
				// 2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int) COMUtil.getPixel(36));
				// 2012. 9. 12 콤보박스의 글씨 왼쪽 여백 추가
//				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//					tv.setPadding((int) COMUtil.getPixel(8), 0, 0, 0);
//				} else {
					tv.setPadding((int) COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if (m_arrData != null)
				tv.setText(m_arrData.get(position));

			String strComboTitle = _btnCombo.getText().toString();
			if (strComboTitle.equals(tv.getText())) {
//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else {
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(17, 17, 17));
			}

			// 값을 선택하면 리스트뷰를 닫음
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					_btnCombo.setText(String.valueOf(m_arrData.get(pos)));
					//<<<<<<<<< 2014.3.5 선택값에 따라 종가기준값 변경
					if (pos==0){//시가
						_graph.dataTypeBollingerband = 0;
					}
					else if(pos==1){//고가
						_graph.dataTypeBollingerband = 1;
					}
					else if(pos==2){//저가
						_graph.dataTypeBollingerband = 2;
					}
					else if(pos==3){//종가
						_graph.dataTypeBollingerband = 3;
					}
					else if(pos==4){//(고+저)/2
						_graph.dataTypeBollingerband = 4;
					}
					else if(pos==5){//(고+저+종)/3
						_graph.dataTypeBollingerband = 5;
					}
					else {//종가
						_graph.dataTypeBollingerband = 3;
					}
					//>>>>>> 2014.3.5
					comboPopup.dismiss();
					comboPopup = null;

					comboPopupLayout.removeAllViews();
					reSetJipyo();
					COMUtil._neoChart.repaintAll();

					//<<<<<< 2014.3.5 이평선 다시 그리기
					COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
					COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
					//>>>>>> 2014.3.5
				}
			});
			return tv;
		}
	}

	private class DropListAdapter5 extends BaseAdapter {
		private ArrayList<String> m_arrData;
		Button _btnCombo;

		public DropListAdapter5(ArrayList<String> arrayList, Button btnCombo) {
			super();
			m_arrData = arrayList;
			_btnCombo = btnCombo;
		}

		@Override
		public int getCount() {
			return m_arrData == null ? 0 : m_arrData.size();
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
			// 콤보박스 리스트뷰의 한 행의 속성은 여기서 정해짐.
			final int pos = position;
			TextView tv = (TextView) v;
//			COMUtil.setGlobalFont(parent);
			if (tv == null) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
				tv.setBackgroundColor(Color.TRANSPARENT);
//					tv.setSingleLine();
//					tv.setTextSize(13);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int) COMUtil.getPixel(120));
				// 2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int) COMUtil.getPixel(36));
				// 2012. 9. 12 콤보박스의 글씨 왼쪽 여백 추가
//				if (COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//					tv.setPadding((int) COMUtil.getPixel(8), 0, 0, 0);
//				} else {
					tv.setPadding((int) COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if (m_arrData != null)
				tv.setText(m_arrData.get(position));

			String strComboTitle = _btnCombo.getText().toString();
			if (strComboTitle.equals(tv.getText())) {
//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else {
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(17, 17, 17));
			}

			// 값을 선택하면 리스트뷰를 닫음
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					_btnCombo.setText(String.valueOf(m_arrData.get(pos)));

					if (pos==0){//단순이평선
						_graph.calcTypeBollingerband = 0;
					}
					else if (pos==1){//가중이평선
						_graph.calcTypeBollingerband = 1;
					}
					else {//지수이평선
						_graph.calcTypeBollingerband = 2;
					}

					comboPopup.dismiss();
					comboPopup = null;

					comboPopupLayout.removeAllViews();
					reSetJipyo();
					COMUtil._neoChart.repaintAll();
				}
			});
			return tv;
		}
	}
	//2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

	private int getAverageComboIndex(String strAdapterDataName, String cmpStr) {
		int rtnVal = 0;
		final String adapterName = strAdapterDataName;
		// 인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서
		// arrayadapter 를 만든다
		try{
			int layoutResId = this.context.getResources().getIdentifier(
					strAdapterDataName, "array", this.context.getPackageName());
			ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter
					.createFromResource(this.context, layoutResId,
							this.getContext().getResources().getIdentifier("simple_dropdown_item_1line", "drawable", this.getContext().getPackageName()));

			// 읽어온 배열값을 arraylist 에 저장.
			ArrayList<String> arStrComboData = new ArrayList<String>();
			for (int i = 0; i < comboAdapter.getCount(); i++) {
				if(cmpStr.equals(String.valueOf(comboAdapter.getItem(i)))) {
					rtnVal = i;
					break;
				}
				//			arStrComboData.add(String.valueOf(comboAdapter.getItem(i)));
			}
		}
		catch(Exception e)
		{

		}

		return rtnVal;
	}


	private boolean IsOverayJipyo(String strGraph)
	{
		if(strGraph.equals("MAC") || strGraph.equals("Envelope") || strGraph.equals("Pivot") || strGraph.equals("Bollinger Band") || strGraph.equals("Price Channel")|| strGraph.equals("Zig Zag")
				|| strGraph.equals("Demark") || strGraph.equals("Pivot전봉기준") )	//2014.01.11 by LYH >> Price Channel 지표 추가
			return true;

		return false;
	}
	public void reSetJipyo() {
		if(bDestroy)
			return;
		int[] i_cnt = _graph.interval;
		int nContCnt = i_cnt.length;
		//2013. 9. 3 지표마다 기준선 설정 추가>>
		int nBaseLineCount = 0;		//지표 기준선 갯수

		int nSellingSignalCount = 0;	//2014. 9. 11 매매 신호 보기 기능 추가

		//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 >>
		if(bEditing)
			inputUpdateValue(isFocusSeekBar, isFocusEdit);
		//2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 <<
		//null  이 아닐경우만 기준선이 있다는 의미.
		if(_graph.getBaseValue() != null)
		{
			// *2 해준 이유 : 가시성 체크박스 값 때문에.    (체크박스  기준선)
			nBaseLineCount = _graph.getBaseValue().length * 2;

			nSellingSignalCount = 1;	//2014. 9. 11 매매 신호 보기 기능 추가
		}
//        int[] tmp = new int[nContCnt+line_cnt*5];

		//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
		int nStandScale = 0;
		//매물대설정이라면 체크값을 저장하기 위해 버퍼크기를 1 증가
		if(_graph.graphTitle.equals("매물대"))
		{
			nStandScale = 1;
		}

		//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 >>
		Vector<DrawTool> drawTools = _graph.getDrawTool();
		if (drawTools.isEmpty()) {
			return;
		}
		//2019. 07. 09 by hyh - 설정창 보이는 상태에서 화면을 내렸다가 올렸을 때 죽는 에러 수정 <<

		//2017.05.11 by LYH >> 전략(신호, 강약) 추가
		if(_graph.getDrawTool().get(0) instanceof SignalDraw)
		{
			nStandScale = 5;
		}
		//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end

		//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
		int addNum = 0;
		if(_graph.getGraphTitle().equals("주가이동평균")){
			addNum = 2*10;
//			addNum = 2*11; //2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw
		}

        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
		if(isBollingerBand()) {
			addNum = 2;
		}
        //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<

		//기존 갯수에 기준선 갯수까지 포함하여 배열생성
//        int[] tmp = new int[nContCnt+line_cnt*5+nBaseLineCount + nStandScale];  //기준선, 9. 5 매물대 체크박스 갯수 추가 
		int[] tmp = new int[nContCnt+line_cnt*6+nBaseLineCount + nStandScale+nSellingSignalCount+addNum];  //2015. 1. 13 매매 신호 보기 기능 추가, 보조지표 bar 타입 유형 변경 기능
		//2013. 9. 3 지표마다 기준선 설정 추가>>
		int cont=0;

		for(int i=0;i<nContCnt;i++){
			LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[i]);
			tmpBar = _seekBar[i];
			if(ll.getVisibility()==View.VISIBLE){
				int tval = tmpBar.getProgress();
				if(tval==0) tmpBar.setProgress(1);
				if(_graph.interval.length>0 && _graph.interval[0]==0)
				{
					tmp[cont]=0;
				}
				else
					tmp[cont]=tmpBar.getProgress();
				cont++;
			}
		}

		if(_graph.graphTitle.equals("매물대"))
		{
			// 매물대 색상 세팅 할 곳
			int color;

			for(int i=1; i<=3; i++)
			{
				String strId = "standscale_row_" + String.valueOf(i) + "_colortextview";
				int layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
				TextView tvColorOpen = (TextView)jipyoui.findViewById(layoutResId);

				//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 색상 미적용 >>
//				String a = (String)tvColorOpen.getTag();
//				color = Integer.parseInt(a);
//				tmp[cont++] = 1;
//				tmp[cont++] = Color.red(color);
//				tmp[cont++] = Color.green(color);
//				tmp[cont++] = Color.blue(color);
//				tmp[cont++] = 1;
				DrawTool dt = _graph.getDrawTool().get(0);
				int[] getUpColor = dt.getUpColor();
				tmp[cont++] = 1;
				tmp[cont++] = getUpColor[0];
				tmp[cont++] = getUpColor[1];
				tmp[cont++] = getUpColor[2];
				tmp[cont++] = 1;
				//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 색상 미적용 <<
			}

			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
			//매물대값 보이는상태(체크) 면  1,   아니면 0을 세팅
			int layoutResId = this.getContext().getResources().getIdentifier("chk_standscaleshow", "id", this.getContext().getPackageName());
			CheckBox chk = (AppCompatCheckBox)jipyoui.findViewById(layoutResId);
			tmp[cont++] = (chk.isChecked() ? 1 : 0);
			//2013. 9. 5 매물대설정 값 문자열 보이기 여부 처리>>
		}
		else
		{
			if(_graph.getGraphTitle().equals("주가이동평균")){
				int layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
				LinearLayout averageLayout = (LinearLayout)jipyoui.findViewById(layoutResId);

				Boolean bError = false; //2023.11.07 by CYJ - 에러케이스 있는 경우 확인버튼 비활성화
				for(int i = 1; i < 11; i++){
					String strId = "gsetItem" + String.valueOf(i);
					layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
					LinearLayout gsetItemLayout = (LinearLayout)averageLayout.findViewById(layoutResId);

					layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
					TextView lineView = (TextView)gsetItemLayout.findViewById(layoutResId); //지표 설정 값.
					int thickInt = 1;
					try {
						thickInt = Integer.parseInt(lineView.getTag().toString());
					} catch (Exception e) {

					}
					tmp[cont++] = thickInt;

					layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
					TextView tvColorOpen = (TextView)gsetItemLayout.findViewById(layoutResId);

					String a = (String)tvColorOpen.getTag();

					int color = Integer.parseInt(a);
					tmp[cont++] = Color.red(color);
					tmp[cont++] = Color.green(color);
					tmp[cont++] = Color.blue(color);

					//그래프 보기 체크값 넣기.
					layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
					CheckBox chkbox = (AppCompatCheckBox)gsetItemLayout.findViewById(layoutResId);
					if(chkbox.isChecked())
						tmp[cont++] = 1;
					else
						tmp[cont++] = 0;

					tmp[cont++] = 0;//라인,바 타입
				}

				for(int i = 1; i < 11; i++){
					String strId = "gsetItem" + String.valueOf(i);
					layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
					LinearLayout gsetItemLayout = (LinearLayout)averageLayout.findViewById(layoutResId);

					layoutResId = this.getContext().getResources().getIdentifier("jipyo_button_price", "id", this.getContext().getPackageName());
					Button pricebtn = (Button)gsetItemLayout.findViewById(layoutResId);

					String strBaseprice = pricebtn.getText().toString();
					tmp[cont++] = this.getAverageComboIndex("jipyo_baseprice", strBaseprice);

					layoutResId = this.getContext().getResources().getIdentifier("jipyo_button_type", "id", this.getContext().getPackageName());
					Button calcbtn = (Button)gsetItemLayout.findViewById(layoutResId);

					String strAverageType = calcbtn.getText().toString();
					tmp[cont++] = this.getAverageComboIndex("jipyo_averagetype", strAverageType);
				}
//				//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw >>
//				String strBaseprice = btnComboBasePrice_average.getText().toString();
//				tmp[cont++] = this.getAverageComboIndex("jipyo_baseprice", strBaseprice);
//
//				String strAverageType = btnComboAverageType_average.getText().toString();
//				tmp[cont++] = this.getAverageComboIndex("jipyo_averagetype", strAverageType);
				//2020.03.20 주가이동평균 가격, 이평선 타입 수정 - hjw <<
			}else {

				for (int i = 10; i < line_cnt + 10; i++) {
					LinearLayout ll = (LinearLayout) jipyoui.findViewById(ids[i]);//
					if (ll == null) {
						return;
					}

					ll.setVisibility(View.GONE);

//				int layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//				//			TextView lineTextView = (TextView)ll.findViewById(layoutResId); //지표 설정 값.
//				EditText lineTextView = (EditText)ll.findViewById(layoutResId); //지표 설정 값.
//				tmp[cont++] = Integer.parseInt(lineTextView.getText().toString());
					int layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
					TextView lineView = (TextView) ll.findViewById(layoutResId); //지표 설정 값.
					int thickInt = 1;
					try {
						thickInt = Integer.parseInt(lineView.getTag().toString());
					} catch (Exception e) {

					}
					tmp[cont++] = thickInt;

					layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
					TextView tvColorOpen = (TextView) ll.findViewById(layoutResId);
					String a = (String) tvColorOpen.getTag();
					//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 색상 미적용 >>
//					int color = Integer.parseInt(a);
//					tmp[cont++] = Color.red(color);
//					tmp[cont++] = Color.green(color);
//					tmp[cont++] = Color.blue(color);
					DrawTool dt = _graph.getDrawTool().get(i - 10);
					int[] getUpColor = dt.getUpColor();
					tmp[cont++] = getUpColor[0];
					tmp[cont++] = getUpColor[1];
					tmp[cont++] = getUpColor[2];
					//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 색상 미적용 <<

					//그래프 보기 체크값 넣기.
					layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
					CheckBox chkbox = (AppCompatCheckBox) ll.findViewById(layoutResId);
					//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 체크상태 미적용 >>
//					if (chkbox.isChecked())
//						tmp[cont++] = 1;
//					else
//						tmp[cont++] = 0;
					tmp[cont++] = 1;
					//2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 체크상태 미적용 <<

					//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
//				RadioGroup rg_DrawType = (RadioGroup)ll.findViewById(this.getContext().getResources().getIdentifier("detail_drawtype_radiogroup", "id", this.getContext().getPackageName()));
//				RadioButton rb_Bar = (RadioButton)rg_DrawType.findViewById(this.getContext().getResources().getIdentifier("detail_radiobtn_bar", "id", this.getContext().getPackageName()));
//				if(rb_Bar.isChecked())
//				{
//					tmp[cont++] = 7;
//				}
//				else
//				{
//					tmp[cont++] = 0;
//				}
					TextView tv_comboText = (TextView) ll.findViewById(context.getResources().getIdentifier("detail_drawtype_combo_text", "id", context.getPackageName()));
					if (tv_comboText.getText().toString().equals("  바")) {
						tmp[cont++] = 7;
					} else {
						tmp[cont++] = 0;
					}
					//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<
				}

				//2013. 9. 3 지표마다 기준선 설정 추가>>
				//기준선 갯수만큼 세팅. 기준선이 있는 경우(null이 아닐경우) 만 처리한다.
				if (_graph.getBaseValue() != null) {
					int layoutResId;

					//ex:  체크값 기준선값 체크값 기준선값
					for (int i = 0; i < nBaseLineCount / 2; i++)    //체크박스  기준선값   쌍으로 되어있어서 위에서 nBaseLineCount 갯수를 *2 해줬는데,
					//루프는 ~쌍  으로 돌아야함.  (1쌍, 2쌍)  그래서 /2 해줌
					{
						//기준선설정 레이아웃
						LinearLayout linearParentRow = (LinearLayout) jipyoui.findViewById(baseSettingIds[i]);

						//체크박스 값 전달
						String strId = "chk_basesetting_" + String.valueOf((i + 1));
						layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
						CheckBox chkbox = (AppCompatCheckBox) linearParentRow.findViewById(layoutResId);
						if (chkbox.isChecked())
							tmp[cont++] = 1;
						else
							tmp[cont++] = 0;

						//EditText 에 있는 값을 가져온다.
						strId = "ed_basesetting_" + String.valueOf((i + 1));
						layoutResId = this.getContext().getResources().getIdentifier(strId, "id", this.getContext().getPackageName());
						UnderLineEditText edBaseSetting = (UnderLineEditText) linearParentRow.findViewById(layoutResId);

//					anBase[i] = Integer.parseInt(edBaseSetting.getText().toString());
						//넘길 배열에 전달.
                        try {
                            tmp[cont++] = Integer.parseInt(edBaseSetting.getText().toString());
                        }catch (Exception e)
                        {
                            tmp[cont] = 0;
                        }

					}

					//2015. 1. 13 매매 신호 보기 기능 추가 >>
					LinearLayout sellingSignal_Linear = (LinearLayout) jipyoui.findViewById(this.getContext().getResources().getIdentifier("sellingsignal_linear", "id", this.getContext().getPackageName()));
					CheckBox chk_SellingSignal = (AppCompatCheckBox) sellingSignal_Linear.findViewById(this.getContext().getResources().getIdentifier("chk_sellingsignal", "id", this.getContext().getPackageName()));
					tmp[cont++] = chk_SellingSignal.isChecked() ? 1 : 0;
					//2014. 9. 11 매매 신호 보기 기능 추가 <<
				}

				//2017.05.11 by LYH >> 전략(신호, 강약) 추가
				if(_graph.getDrawTool().get(0) instanceof SignalDraw)
				{
					LinearLayout ll = (LinearLayout) jipyoui.findViewById(ids[10+1]);
					if (ll == null) {
						return;
					}

					ll.setVisibility(View.VISIBLE);

					int layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
					TextView lineView = (TextView) ll.findViewById(layoutResId); //지표 설정 값.
					int thickInt = 1;
					try {
						thickInt = Integer.parseInt(lineView.getTag().toString());
					} catch (Exception e) {

					}
					tmp[cont++] = thickInt;

					layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
					TextView tvColorOpen = (TextView) ll.findViewById(layoutResId);
					String a = (String) tvColorOpen.getTag();
					int color = Integer.parseInt(a);
					tmp[cont++] = Color.red(color);
					tmp[cont++] = Color.green(color);
					tmp[cont++] = Color.blue(color);
					//그래프 보기 체크값 넣기.
					layoutResId = this.getContext().getResources().getIdentifier("chk_isSelect", "id", this.getContext().getPackageName());
					CheckBox chkbox = (AppCompatCheckBox) ll.findViewById(layoutResId);
					if (chkbox.isChecked())
						tmp[cont++] = 1;
					else
						tmp[cont++] = 0;
				}
				//2017.05.11 by LYH >> 전략(신호, 강약) 추가 end
			}

            //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
			if(isBollingerBand()) {
				String strBaseprice = btnComboBasePrice.getText().toString();
				tmp[cont++] = this.getAverageComboIndex("jipyo_baseprice", strBaseprice);

				String strAverageType = btnComboAverageType.getText().toString();
				tmp[cont++] = this.getAverageComboIndex("jipyo_averagetype", strAverageType);
			}
            //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<
			//2013. 9. 3 지표마다 기준선 설정 추가>>
		}

		//2013. 9. 3 지표마다 기준선 설정 추가>>
//        int[] cont_val = new int[nContCnt+line_cnt*5];
//        int[] cont_val = new int[nContCnt+line_cnt*5+nBaseLineCount+nStandScale];	//변수설정갯수, 색굵기설정갯수(굵기값, 색상, 보임여부), 기준선값, 매물대체크박스
		//2015. 1. 13 보조지표 bar 타입 유형 변경 기능, 매매 신호 보기 기능 추가
		int[] cont_val = new int[nContCnt+line_cnt*6+nBaseLineCount+nStandScale+nSellingSignalCount+addNum];	//변수설정갯수, 색굵기설정갯수(굵기값, 색상, 보임여부), 기준선값, 매물대체크박스, 매매신호
		//2013. 9. 3 지표마다 기준선 설정 추가>>
		System.arraycopy(tmp,0,cont_val,0,cont);

		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >>
		if(firstValue == null) {
			firstValue = cont_val;
			return;
		} else if (alert == null) {
			hideKeyPad(isFocusEdit); //2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교
			if (Arrays.equals(firstValue, cont_val)) {
				if(COMUtil._neoChart.jipyoListDetailPopup != null) { //2023.12.27 by CYJ - 방어코드 추가
					COMUtil._neoChart.jipyoListDetailPopup.dismiss();
					COMUtil._neoChart.jipyoListDetailPopup = null;
				}
				return;
			} else if (bBack) { //2023.12.27 by CYJ - 변경사항에 대한 적용 여부 팝업창 뒤로가기에만 보이도록 설정
				showDialogChangeValueSave();
				return;
			}
		}
		//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 <<
		//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
		_graph.changeControlValue(cont_val);
		//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<

        //2016.11.04 by LHY >> 지표설정시 기본값으로 저장 처리
        setSaveBaseIndicator();
        //2016.11.04 by LHY << 지표설정시 기본값으로 저장 처리 end
		COMUtil._mainFrame.mainBase.baseP._chart.resetTitleBoundsAll();
	}

	//2012. 6. 28 decrease/increase 함수 추가  
	public void decreaseCount(int nIdx)
	{
		SeekBar tmpSlider = _seekBar[nIdx];
		int sVal = tmpSlider.getProgress();
		int rVal = sVal - 1;
		if(rVal < 0)
		{
			return;
		}

		tmpSlider.setProgress(rVal);

		LinearLayout baseLayout = null;
		int layoutResId = 0;
		if(_graph.getGraphTitle().equals("주가이동평균")) {
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		} else {
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		}

		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;

		layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
		final EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		final SeekBar seekBar = tmpSlider;
		mHandler.post(new Runnable() {
			public void run() {
				int tval = seekBar.getProgress();
				if(tval==0) seekBar.setProgress(1);
//	        	  tmp.setText(""+seekBar.getProgress());
				if( ( _graph.getGraphTitle().contains("Parabolic SAR") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) )  ||
						//2017.05.11 by LYH << 전략(신호, 강약) 추가
						( _graph.getGraphTitle().contains("Parabolic SAR 신호") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) ) ||
						( _graph.getGraphTitle().contains("Parabolic SAR 강세약세") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) ) ||
						//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
						( isBollingerBand() && ((String)tmp.getTag()).equals("1")) ||
						( _graph.getGraphTitle().contains("Envelope") && (((String)tmp.getTag()).equals("1") || ((String)tmp.getTag()).equals("2")) )) //2020.11.27 by HJW - Envelope 지표 소수점 처리
				{
					tmp.setText(String.format("%.2f", seekBar.getProgress()/100.0f));
				}
				else
				{
					tmp.setText(""+seekBar.getProgress());
				}
			}
		});
//		updateValue(tmpSlider);
	}
	public void increaseCount(int nIdx)
	{
		SeekBar tmpSlider = _seekBar[nIdx];
		int sVal = tmpSlider.getProgress();
		int rVal = sVal + 1;
		if(rVal > tmpSlider.getMax())
		{
			return;
		}
		tmpSlider.setProgress(rVal);

		LinearLayout baseLayout = null;
		int layoutResId = 0;
		if(_graph.getGraphTitle().equals("주가이동평균")) {
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		} else {
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		}

		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[nIdx]);
		if(ll==null)
			return;

		layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//		final TextView tmp = (TextView)ll.findViewById(layoutResId); //지표 설정값.
		final EditText tmp = (EditText)ll.findViewById(layoutResId); //지표 설정값.
		final SeekBar seekBar = tmpSlider;
		mHandler.post(new Runnable() {
			public void run() {
				int tval = seekBar.getProgress();
				if(tval==0) seekBar.setProgress(1);

				if( ( _graph.getGraphTitle().contains("Parabolic SAR") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) )  ||
						//2017.05.11 by LYH << 전략(신호, 강약) 추가
						( _graph.getGraphTitle().contains("Parabolic SAR 신호") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) )  ||
						( _graph.getGraphTitle().contains("Parabolic SAR 강세약세") && (((String)tmp.getTag()).equals("0") || ((String)tmp.getTag()).equals("1")) )  ||
						//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
						( isBollingerBand() && ((String)tmp.getTag()).equals("1")) ||
						( _graph.getGraphTitle().contains("Envelope") && (((String)tmp.getTag()).equals("1") || ((String)tmp.getTag()).equals("2")) )) //2020.11.27 by HJW - Envelope 지표 소수점 처리
				{
					tmp.setText(String.format("%.2f", seekBar.getProgress()/100.0f));
				}
				else
				{
					tmp.setText(""+seekBar.getProgress());
				}
			}
		});
//		updateValue(tmpSlider);
	}

	//2012. 10. 2  체크박스 이미지 변경  : I106
	//2013.04.05 이미지 리사이징 코드 비적용   >>
//	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
//	    StateListDrawable imageDraw = new StateListDrawable();
//	    imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
//	    imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
//	    return imageDraw; 
//	}
	//2013.04.05 이미지 리사이징 코드 비적용   <<

	//	public void changeTextValue() {
//		int i=0;
//		String[] cont = _graph.s_interval;
//		int[] i_cnt = _graph.interval;
//
//		if(cont==null) {
//			return;
//		}
//		int layoutResId;
//		for(i=0; i<cont.length; i++) 
//		{
//			LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[i]);
//			
//			layoutResId = this.getContext().getResources().getIdentifier("textView1", "id", this.getContext().getPackageName());
//			//2012. 12. 20 상세설정 선굵기설정의 굵기값 입력가능하게 수정. EditText에서 SeekBar 조절이 필요해서 SeekBar의 세팅을 앞으로 옮겼음. 
//			final SeekBar tmpBar = _seekBar[i];
//			
//			layoutResId = this.getContext().getResources().getIdentifier("textView2", "id", this.getContext().getPackageName());
//			EditText tmp2 = (EditText)ll.findViewById(layoutResId); //지표 설정 값.
////			tmp2.setText(""+i_cnt[i]);
////			System.out.println("changeText:"+tmp2.getText().toString());
////			final EditText _tmp2 = tmp2;
//			inputUpdateValue(tmpBar, tmp2);
//			updateValue(tmpBar);
//			hideKeyPad(tmp2);
//		}
//	}
	public void destroy() {
//		changeTextValue();

		//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
//		closePopupViews();

		//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
		//열려있는 콤보박스 리스트뷰를 닫아준다. 
		if(null != comboPopup)
		{
			comboPopup.dismiss();
			comboPopup = null;
		}
		//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

		if(null != this.palette)	this.palette.cancel();

		if(!isFocus && isFocusSeekBar != null && isFocusEdit !=null) {
//			inputUpdateValue(isFocusSeekBar, isFocusEdit);
//			updateValue(isFocusSeekBar);
		}
		this.layout.removeView(jipyoui);
		bDestroy = true;

		//2019. 07. 25 by hyh - 화면 내렸다가 올릴 때 지표설정 내용 저장되도록 처리 >>
		if (COMUtil._mainFrame.strFileName != null) {
			COMUtil.saveLastState(COMUtil._mainFrame.strFileName);
		}
		//2019. 07. 25 by hyh - 화면 내렸다가 올릴 때 지표설정 내용 저장되도록 처리 <<

		this.layout.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
	}

	//2013. 3. 26   EditText 에서 Back버튼 처리 
	public void onBackButtonClick(EditText ed)
	{
		try {
			if (isFocusEdit != null) {
				isFocusEdit.clearFocus();
			}

			try {
				btnEditSoftKeypadConfirm.setVisibility(GONE);

				Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {
					public void run() {
						if(btnEditSoftKeypadConfirm.getVisibility() == GONE) { //
							ll_bottom_view.setVisibility(VISIBLE);
						}
					}
				}, 200); // 0.2초후

			} catch (Exception e) {

			}

			//2023.11.24 by lyk - 키패드 내림 버튼 작동시 키패드가 다시 뜨거나 확인/취소 버튼이 보여지는 현상 수정 >>
			if(isFocusEdit != null) {
				imm.hideSoftInputFromWindow(isFocusEdit.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
			//2023.11.24 by lyk - 키패드 내림 버튼 작동시 키패드가 다시 뜨거나 확인/취소 버튼이 보여지는 현상 수정 <<

		} catch(Exception e) {

		}
	}

	//2013. 8. 27 주가이평/거래량이평에서 변수설정 값 변경시 색굵기설정의 숫자도 같이 변경>>
	private void changeLabelColorThickSetting(SeekBar _seekBar)
	{
		if(null == _seekBar)
		{
			return;
		}

		String strGraphName = _graph.getGraphTitle();
		int inx = _seekBar.getId();
		if(inx < 10 && (strGraphName.equals("주가이동평균") || strGraphName.equals("거래량이동평균")) )
		{
			LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[inx+6]);
			int layoutResId = ll.getContext().getResources().getIdentifier("textView1", "id", ll.getContext().getPackageName());
			TextView tv_lineticksetting = (TextView)ll.findViewById(layoutResId); //지표 설정값.

			/*if(strGraphName.equals("주가이동평균"))
			{
				tv_lineticksetting.setText("이평 " + _seekBar.getProgress());
			}
			else if(strGraphName.equals("거래량이동평균"))
			{
				tv_lineticksetting.setText("거래량이평" + _seekBar.getProgress());
			}*/
		}
	}
	//2013. 8. 27 주가이평/거래량이평에서 변수설정 값 변경시 색굵기설정의 숫자도 같이 변경>>

	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
//	public void closePopupViews()
//	{
//		//팔레트 닫기 
//		if(null != this.palette)	this.palette.cancel();
//	}
	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 <<

	//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 >>
	private void setEditTextValue(EditText _ed, float fChangeVal, boolean bChangeFloat)
	{
		//Parabolic SAR 의 변수설정 1, 2 랑  Bollinger Band 의 2번째 변수설정 에 대에서만 수행한다. 
		//bChangeFloat 가 true 이면  정수->float      
		if( ( _graph.getGraphTitle().contains("Parabolic SAR") && (((String)_ed.getTag()).equals("0") || ((String)_ed.getTag()).equals("1")) )  ||
				//2017.05.11 by LYH << 전략(신호, 강약) 추가
				( _graph.getGraphTitle().contains("Parabolic SAR 신호") && (((String)_ed.getTag()).equals("0") || ((String)_ed.getTag()).equals("1")) )  ||
				( _graph.getGraphTitle().contains("Parabolic SAR 강세약세") && (((String)_ed.getTag()).equals("0") || ((String)_ed.getTag()).equals("1")) )  ||
				//2017.05.11 by LYH << 전략(신호, 강약) 추가 end
				( isBollingerBand() && ((String)_ed.getTag()).equals("1")) ||
				( _graph.getGraphTitle().contains("Envelope") && (((String)_ed.getTag()).equals("1") || ((String)_ed.getTag()).equals("2")) )) //2020.11.27 by HJW - Envelope 지표 소수점 처리
		{
			if(bChangeFloat)
			{
				//값을 소숫점2째자리까지로 변환하여 세팅 
				_ed.setText(String.format("%.2f", fChangeVal/100.0));
			}
			else
			{
				//소수를  정수로 변환.  
				float fTVValue = Float.parseFloat(_ed.getText().toString());
				fTVValue *= 100;
				_ed.setText(String.valueOf((int)fTVValue));
			}
		}
		else
		{
			_ed.setText(""+(int)fChangeVal);
		}

		//2023.11.16 by lyk - 카카오페이 캔들차트 개선 - 키보드 닫은 후 같은 에디트필드 선택시 "확인" 버튼이 보이는 현상 수정 >>
//		_ed.setMaxLines(1);
//		_ed.setInputType(InputType.TYPE_CLASS_TEXT);
		//2023.11.16 by lyk - 카카오페이 캔들차트 개선 - 키보드 닫은 후 같은 에디트필드 선택시 "확인" 버튼이 보이는 현상 수정 <<
	}
	//2013. 10. 16 parabolic sar 변수설정2개, bolingerband 표준편차승수   소숫점처리 <<

	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
	//2015. 1. 13 보조지표 bar 타입 유형 변경 기능>>
//	private void setComboBox(final LinearLayout btnCombo)
//	{
//		// 배열값을 arraylist 에 저장.
//		final ArrayList<String> arStrComboData = new ArrayList<String>();
//		arStrComboData.add("라인");
//		arStrComboData.add("바");
//
//		btnCombo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//2014. 5. 14 지표 상세설정창 UI >> : 눌린 뷰의 위치기반으로 left/top 마진 구하기
//				m_selLinearLayout = btnCombo;
//				//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업
//				m_selLinearLayout.setSelected(true);
//				int[] location = new int[2];
//				v.getLocationOnScreen(location);
//				drBottom = new DRBottomDialog(context,0);
//
//				drBottom.setTitle("라인 유형");
//				drBottom.setCondListView(arStrComboData);
//
//				int nComboboxTextIndex = Integer.parseInt((String)m_selLinearLayout.getTag());
//				TextView selTextView = m_arComboBoxText.get(nComboboxTextIndex);
//				drBottom.resetStrSelValue(selTextView.getText().toString());
//				drBottom.show();
//
//				drBottom.setOnClickListItemListener(new DRBottomDialog.OnClickBottomViewListItemListener() {
//					@Override
//					public void onClick(View view, int index, String value) {
//
////						if (value.equals("라인") || value.equals("바")) {
//							int nComboboxTextIndex = Integer.parseInt((String)m_selLinearLayout.getTag());
//							m_arComboBoxText.get(nComboboxTextIndex).setText(value);
//
//							if(value.equals("  바"))
//							{
//								enableApplyLineButton(false, nComboboxTextIndex);
//							}
//							else
//							{
//								enableApplyLineButton(true, nComboboxTextIndex);
//							}
//
////						}else{
////							m_selBtnCombo.setText(value);
////						}
//						reSetJipyo();
//						COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//						COMUtil._neoChart.repaintAll();
//						drBottom.dismiss();
//						//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업 start
//						m_selLinearLayout.setSelected(false);
//						drBottom =null;
//					}
//				});
//
//				drBottom.setOnDismissListener(new DialogInterface.OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface dialogInterface) {
//						m_selLinearLayout.setSelected(false);
//					}
//				});
//				//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업 end
//			}
//		});
//	}
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end
	private void setComboBox(LinearLayout btnCombo)
	{
		//콤보박스 아래에 펼처지는 리스트뷰
		ListView comboList = new ListView(this.context);

		// 배열값을 arraylist 에 저장.
		ArrayList<String> arStrComboData = new ArrayList<String>();
		arStrComboData.add("라인");
		arStrComboData.add("바");

		//리스트뷰에 값을 세팅한다.
		comboList.setAdapter(new DropListAdapter(arStrComboData, btnCombo));

		//리스트뷰 속성 설정
		comboList.setCacheColorHint(Color.WHITE);
		comboList.setScrollingCacheEnabled(false);
		comboList.setVerticalFadingEdgeEnabled(false);
		int layoutResId = this.getResources().getIdentifier("pop_back", "drawable", this.context.getPackageName());
		comboList.setBackgroundResource(layoutResId);
//		comboList.setDivider(new ColorDrawable(Color.rgb(209, 209, 209)));
		comboList.setDividerHeight((int)COMUtil.getPixel(0));
//        comboList.setPadding(
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1),
//                (int)COMUtil.getPixel(1));
//		comboList.setDividerHeight((int)COMUtil.getPixel(1));

//		ImageView imageBottom = new ImageView(context);
//		layoutResId = this.getContext().getResources().getIdentifier("pop_back_bottom", "drawable", this.getContext().getPackageName());
//		imageBottom.setBackgroundResource(layoutResId);
//		imageBottom.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(5)));


		final ListView _comboList2 = comboList;
//		final ImageView imgBottom2 = imageBottom;
		final LinearLayout btnCombo2 = btnCombo;

		//btnCombo는 각 콤보박스 버튼임.   콤보박스 버튼 클릭하면 리스트뷰가 아래에 나오게 함
		btnCombo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int[] location = new int[2];
				v.getLocationOnScreen(location);
				int nDropdownViewWidth = v.getWidth();
				int nDropdownViewLeft = location[0];
				int nDropdownViewTop = location[1] + v.getHeight();

				//버튼 이외의 영역 터치했을경우 정상적으로 닫히게 처리
				if(comboPopup != null && !comboPopup.isShowing())
				{
					comboPopup = null;
					btnCombo2.setSelected(false);
					comboPopupLayout.removeAllViews();
				}

				//콤보박스가 열려있는상태 (not null)  면 닫고  아니면 생성해서 오픈
				if(comboPopup != null)
				{
					comboPopup.dismiss();
					btnCombo2.setSelected(false);
					comboPopup = null;
					comboPopupLayout.removeAllViews();
				}
				else
				{
					comboPopupLayout.addView(_comboList2);
//					comboPopupLayout.addView(imgBottom2);

					comboPopup = new PopupWindow(comboPopupLayout, nDropdownViewWidth, LayoutParams.WRAP_CONTENT, true);
					comboPopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
					comboPopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.
					comboPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							btnCombo2.setSelected(false);
						}
					});

					//팝업 띄우기
//  					comboCandlePopup.showAtLocation(comboCandleLayout, Gravity.TOP|Gravity.LEFT, (int)COMUtil.getPixel(nPopupLeftMargin), (int)COMUtil.getPixel(nPopupTopMargin));
					comboPopup.showAtLocation(comboPopupLayout, Gravity.NO_GRAVITY, nDropdownViewLeft, nDropdownViewTop);
					btnCombo2.setSelected(true);
				}
			}
		});
	}

	//2012. 9. 11 콤보박스용 어댑터
	private class DropListAdapter extends BaseAdapter{
		private ArrayList<String> m_arrData;
		LinearLayout _btnCombo;

		public DropListAdapter(ArrayList<String> arrayList, LinearLayout btnCombo)
		{
			super();
			m_arrData = arrayList;
			_btnCombo = btnCombo;
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
			final int pos = position;
			TextView tv = (TextView)v;
//			COMUtil.setGlobalFont(parent);
			if( tv == null ) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
//				tv.setBackgroundColor(Color.TRANSPARENT);
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setSingleLine();
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int)COMUtil.getPixel_W(58));
				//2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int)COMUtil.getPixel_H(36));
				//2012. 9. 12  콤보박스의 글씨 왼쪽 여백 추가 
//				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//				{
					tv.setPadding((int)COMUtil.getPixel_W(10), 0, 0, 0);
//				}
//				else
//				{
//					tv.setPadding((int)COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if(m_arrData!=null) tv.setText(m_arrData.get(position));

			String strTitle = m_arrData.get(position);
			int nComboboxTextIndex = Integer.parseInt((String)_btnCombo.getTag());

			String strComboTitle = m_arComboBoxText.get(nComboboxTextIndex).getText().toString();
			if(strComboTitle.equals(strTitle))
			{
//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(245, 245, 245));
                tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else
			{
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
//				tv.setBackgroundColor(Color.rgb(255, 255, 255));
                tv.setTextColor(Color.rgb(17, 17, 17));
			}

			//값을 선택하면 리스트뷰를 닫음 
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int nComboboxTextIndex = Integer.parseInt((String)_btnCombo.getTag());

					m_arComboBoxText.get(nComboboxTextIndex).setText(m_arrData.get(pos));

					if(pos==1)
					{
						enableApplyLineButton(false, nComboboxTextIndex);
					}
					else
					{
						enableApplyLineButton(true, nComboboxTextIndex);
					}

					comboPopup.dismiss();
					comboPopup = null;

					comboPopupLayout.removeAllViews();
					reSetJipyo();
					COMUtil._neoChart.repaintAll();
				}
			});
			return tv;
		}
	}
	//2015. 1. 13 보조지표 bar 타입 유형 변경 기능<<

	//2015. 3. 2 채널지표에는 라인바 콤보 감추기>>
	public boolean isOverlay(String strName)
	{
		if(	strName.equals("일목균형표") ||
				strName.equals("그물차트") ||
				strName.equals("매물대") ||
				strName.equals("Bollinger Band") ||
				strName.equals("Envelope") ||
				strName.equals("MAC") ||
				strName.equals("Parabolic SAR") ||
				strName.equals("Pivot") ||
                strName.equals("Pivot전봉기준") ||
				strName.equals("Price Channel")||
				strName.equals("Zig Zag") ||
				strName.equals("Demark") ||
				strName.equals("TEMA") ||
				strName.equals("DEMA") ||
				strName.equals("가격 & Box")
				)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//2015. 3. 2 채널지표에는 라인바 콤보 감추기<<

	//2016. 6. 16 흰색 버튼 배경 테두리 설정 >>
	public void setButtonColorWithRound(View view, int nColor)
	{
		((GradientDrawable)view.getBackground()).setColor(nColor);
		view.setTag(""+nColor);
		if(nColor == Color.rgb(255, 255, 255))
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), Color.rgb(224, 224, 224));
		else
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), nColor);
	}
	//2016. 6. 16 흰색 버튼 배경 테두리 설정 <<

	// '바'선택 시 라인 버튼 enable 설정 >>
	public void enableApplyLineButton(boolean bEnable, int nIndex)
	{
		int dCnt = _graph.getDrawTool().size();
		line_cnt = dCnt;

		LinearLayout ll = (LinearLayout)jipyoui.findViewById(ids[nIndex+10]);

		int layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
		final TextView tvLineOpen = (TextView)ll.findViewById(layoutResId);
		String lineThick = (String) tvLineOpen.getTag();
		int nThick = (Integer.parseInt(lineThick));
		tvLineOpen.setEnabled(bEnable);
		if(nThick>0)
		{
//			if(bEnable)
//			{
//				tvLineOpen.setBackgroundResource(Lines[nThick-1]);
				tvLineOpen.setText(nThick + "px");
//			}
//			else
//			{
//				tvLineOpen.setBackgroundResource(DisabledLines[nThick-1]);
//			}
		}

	}
	// '바'선택 시 라인 버튼 enable 설정 <<

	// 지표설명 화면에서 back 버튼 눌렀을때 상세설정창 나오도록 설정 >>
	public boolean closeWebview()
	{
		int layoutResId = this.getContext().getResources().getIdentifier("detail_settingview", "id", this.getContext().getPackageName());
		final RelativeLayout rl_SettingView = (RelativeLayout)jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnInit", "id", this.getContext().getPackageName());
		final Button BtnInit = (Button)jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("btn_description", "id", this.getContext().getPackageName());
		final Button BtnInfo = (Button)jipyoui.findViewById(layoutResId);

		if(mWebView!=null && mWebView.getVisibility() == View.VISIBLE)
		{
			rl_SettingView.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
			BtnInit.setVisibility(VISIBLE);
			BtnInfo.setVisibility(VISIBLE);
			BtnInfo.setSelected(false);
			return false;
		}else {
			BtnInit.setVisibility(GONE);
		}
		//isCloseWebView = true; //2023.11.17 by lyk - 캔들차트개선 - 최대입력값 초과 입력 후 설정창 뒤로가기 이동시 메시지 뜨는 현상 수정 //2023.12.20 by CYJ - 텍스트 수정중 뒤로가기하면 적용 후 비교 (변경사항이 생기면 저장여부에 대한 팝업창이 나오면서 이전 기능 미사용)
		return true;
	}
	// 지표설명 화면에서 back 버튼 눌렀을때 상세설정창 나오도록 설정 <<

    //2016.11.04 by LHY << 지표설정시 기본값으로 저장 처리 end
    public void setSaveBaseIndicator()
    {
        if(null != COMUtil.indicatorPref)
        {
            String strKey = _graph.getGraphTitle();

            //현재 종목에 대한 분석툴바 저장정보를 추가한다.
            String strGraphProperty = COMUtil._neoChart.getGraphValue2(_graph, true);
            if(strGraphProperty == null || strGraphProperty.equals(""))	//그려진 분석툴바가 없을 경우, 해당 정보를 제거
            {
                COMUtil.indicatorPrefEditor.remove(strKey);
            }
            else
            {
                COMUtil.indicatorPrefEditor.putString(strKey, strGraphProperty);
            }
            //추가완료.
            COMUtil.indicatorPrefEditor.commit();
        }
    }
    //2016.11.04 by LHY << 지표설정시 기본값으로 저장 처리 end

    //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 >>
	private boolean isBollingerBand() {
		if (_graph.getGraphTitle().equals("Bollinger Band")
		 || _graph.getGraphTitle().startsWith("Bollinger Band [보조]")
		 || _graph.getGraphTitle().startsWith("Band %B")
		 || _graph.getGraphTitle().equals("Band Width")
		 || _graph.getGraphTitle().startsWith("%B")) {
			return true;
		}

		return false;
	}
    //2019. 07. 04 by hyh - BollingerBand 기준가, 계산방법 추가 <<
	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 >>
	private void showDialogChangeValueSave() {
		alert = new DRAlertDialog(COMUtil.apiView.getContext());
		//2024.01.08 by CYJ - 변경사항 팝업 ZFold 대응 >>
		if(COMUtil.checkFolded()) {
			alert.setTitle("변경사항을\n저장할까요?");
			alert.setMessage("나가기를 누르면\n" + "변경사항이 저장되지\n않아요.");
		} else {
			alert.setTitle("변경사항을 저장할까요?");
			alert.setMessage("나가기를 누르면\n" + "변경사항이 저장되지 않아요.");
		}
		//2024.01.08 by CYJ - 변경사항 팝업 ZFold 대응 <<
		alert.setNoButton("나가기");
		alert.setYesButton("저장");
		alert.setCanceledOnTouchOutside(false);
		ll_bottom_view.setVisibility(VISIBLE);
		alert.alert_btn_no.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View view) {
				  //2013. 1. 21  보조지표리스트의 상세설정창   있으면 초기화
				  if(COMUtil._neoChart.jipyoListDetailPopup != null)
				  {
					  alert.dismiss();
					  alert = null;
					  COMUtil._neoChart.jipyoListDetailPopup.dismiss();
					  COMUtil._neoChart.jipyoListDetailPopup = null;
				  }
			  }
		});
		alert.alert_btn_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(COMUtil._neoChart.jipyoListDetailPopup != null) { //2024.01.30 by SJW - 크래시 로그(2.26.0) 수정
					reSetJipyo();
					alert.dismiss();
					alert = null;
					COMUtil._neoChart.jipyoListDetailPopup.dismiss();
					COMUtil._neoChart.jipyoListDetailPopup = null;
				}
			}
		});
		alert.btn_cancle.setVisibility(VISIBLE);
		alert.btn_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				alert.dismiss();
				alert = null;
			}
		});
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				alert.dismiss();
				alert = null;
			}
		});
		alert.show();
	}
	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창 <<
	//2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트 >>
	private void patchUI_index(int index) {
		LinearLayout baseLayout = null;
		int layoutResId = 0;
		if(_graph.getGraphTitle().equals("주가이동평균")) {
			layoutResId = this.context.getResources().getIdentifier("averageLayout", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		} else {
			layoutResId = this.context.getResources().getIdentifier("paramlinear", "id", this.context.getPackageName());
			baseLayout = (LinearLayout)jipyoui.findViewById(layoutResId);
		}
		LinearLayout ll = (LinearLayout)baseLayout.findViewById(ids[index]);
		UnderLineEditText tmp2 = (UnderLineEditText) ll.findViewWithTag(String.valueOf(index));
		CheckBox checkBox = (AppCompatCheckBox) ll.findViewWithTag(index + 4000);
		LinearLayout llColorLine = (LinearLayout) ll.findViewWithTag(index + 5000);

		layoutResId = this.getContext().getResources().getIdentifier("colortextview", "id", this.getContext().getPackageName());
		TextView tvColorOpen = (TextView) ll.findViewById(layoutResId);

		layoutResId = this.getContext().getResources().getIdentifier("thicktextview", "id", this.getContext().getPackageName());
		final TextView tvLineOpen = (TextView) ll.findViewById(layoutResId); //지표 설정 값.

		if(checkBox.isChecked()) {
			tmp2.setEnabled(true);
			llColorLine.setAlpha(1);
			llColorLine.setEnabled(true);
			tvColorOpen.setAlpha(1);
			tvColorOpen.setEnabled(true);
			tvLineOpen.setAlpha(1);
			tvLineOpen.setEnabled(true);
		} else {
			tmp2.setEnabled(false);
			llColorLine.setEnabled(false);
			tvColorOpen.setAlpha(0.32f);
			tvColorOpen.setEnabled(false);
			tvLineOpen.setAlpha(0.28f);
			tvLineOpen.setEnabled(false);
		}
	}
	//2023.11.07 by CYJ - kakaopay 주가이동평균 체크 이벤트 <<
	//2023.11.10 by CYJ - kakaopay 인터렉션 >>
	public void setOnTouchListener(OnTouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}
	public boolean onTouch(final View v, final MotionEvent e) {
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.startAnimation(reduce);
				if(v == btnConfirm) {
					v.getBackground().setColorFilter(Color.parseColor("#80FFEB00"), PorterDuff.Mode.SRC_IN);
				} else if(v == btnReset) {
					v.getBackground().setColorFilter(Color.parseColor("#0A000000"), PorterDuff.Mode.SRC_IN);
				}
				v.invalidate();
				break;

			case MotionEvent.ACTION_UP:
				Animation.AnimationListener animationListener = new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						// Animation start
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// Animation end
						if(v == btnConfirm) {
							//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
							String[] cont = _graph.s_interval;
							for(int i=0; i<cont.length; i++) {
								SeekBar tmpBar = _seekBar[i];
								updateValue(tmpBar);
							}

							//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<

							reSetJipyo();

							//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 >>
							COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
							COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
							//2023.11.15 by lyk - 카카오페이 캔들차트 개선 - "확인" 누를 경우에만 변경된 값 저장 <<
							COMUtil.showToast(COMUtil._chartMain.getString(R.string.kfit_dialog_save_value), 1, context); //2023.11.17 by CYJ - kakaopay "확인" 누를경우에만 변경된 값 저장 토스트 메세지  //2023.11.17 by CYJ - showToast Gravity 지정

							if (COMUtil._neoChart.jipyoListDetailPopup != null) {
								COMUtil._neoChart.jipyoListDetailPopup.dismiss();
								COMUtil._neoChart.jipyoListDetailPopup = null;
							}
//							COMUtil.closeToastDialog(); //2023.11.21 by lyk - kakaopay 토스트 다이어로그 종료되도록 수정
						} else if(v == btnReset) {
							DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
							//2023.11.16 by CYJ - kakaopay 폴더블 해상도 대응 >>
							//2023.11.23 by CYJ - kakaopay 초기화 다이어로그 현 지표명으로 수정 >>
							TextView tvTitle = jipyoui.findViewById(R.id.frameaTitle);
							String graphName = tvTitle.getText().toString();
							if(COMUtil.checkFolded()) {
								//alert.setTitle(_graph.getGraphTitle() + "\n설정 초기화");
								alert.setTitle(graphName + "\n설정 초기화");
							} else {
								//alert.setTitle(_graph.getGraphTitle() + " 설정 초기화");
								alert.setTitle(graphName + " 설정 초기화");
							}
							//2023.11.23 by CYJ - kakaopay 초기화 다이어로그 현 지표명으로 수정 <<
							//2023.11.16 by CYJ - kakaopay 폴더블 해상도 대응 <<
							alert.setMessage("지표 설정을 초기화 할까요?");
							alert.setYesButton("초기화", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									reSetOriginal();
									alert.dismiss();
									return;
								}
							});
							alert.setNoButton("취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									return;
								}
							});
							alert.show();
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// Animation repeat
					}
				};

				//2024.01.03 by CYJ - 초기화 버튼 입력 이후 확인버튼 입력 시 초기화 버튼에도 같이 인터랙션 적용되는 현상 수정 >>
				if(v == btnConfirm) {
					v.startAnimation(enlarge);
					v.getBackground().clearColorFilter();
					v.invalidate();
					enlarge.setAnimationListener(animationListener);
				} else if(v == btnReset) {
					v.startAnimation(enlargeReset);
					v.getBackground().clearColorFilter();
					v.invalidate();
					enlargeReset.setAnimationListener(animationListener);
				}
				//2024.01.03 by CYJ - 초기화 버튼 입력 이후 확인버튼 입력 시 초기화 버튼에도 같이 인터랙션 적용되는 현상 수정 <<
		}
		return false;
	}
	//2023.11.10 by CYJ - kakaopay 인터렉션 <<

	int lastHeightDiff = 0;
	boolean isOpenKeyboard = false;
	private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		synchronized public void onGlobalLayout() {
			View activityRootView = ((Activity)layout.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
			int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
			if (lastHeightDiff == 0) {
				lastHeightDiff = heightDiff;
			}

			if(isFocusEdit != null) {
				int selectIndex = Integer.parseInt(isUnderLineEditText.getTag().toString()) + 1;
				int selectEditFocus = ll_linearlayout1.getHeight() + llFrameTitle.getHeight() - m_sv.getScrollY() + isUnderLineEditText.getHeight() * selectIndex + (int) COMUtil.getPixel_W(34);
				int nYScroll = 0;

				if (selectEditFocus > btnEditSoftKeypadConfirm.getY()) {
					nYScroll = selectEditFocus - (int) btnEditSoftKeypadConfirm.getY() + m_sv.getScrollY();
					m_sv.smoothScrollTo(0, nYScroll);
				}
			}

			//2023.12.28 by CYJ - 앱바 스크롤 >>
//			Rect r = new Rect();
//			coordinatorLayout.getWindowVisibleDisplayFrame(r);
//			int screenHeight = coordinatorLayout.getHeight();
//			int keypadHeight = screenHeight - r.bottom;
//
//			FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) rl_content.getLayoutParams();
//			if (keypadHeight > screenHeight * 0.15) {
//				if(!bKeyboardOpen) {
//					bKeyboardOpen = true;
//
//					rlp.bottomMargin = keypadHeight;
//					rl_content.setLayoutParams(rlp);
//
//
//					int nSelectTag = Integer.parseInt(isFocusEdit.getTag().toString()) + 1;
//					int nSelectPos = 0;
//					if(isPreUnderLineEditText != null)
//						nSelectPos = nSelectTag * isPreUnderLineEditText.getHeight();
//					int nHScroll = nSelectPos + appBarLayout.getBottom() + (int) COMUtil.getPixel_W(34);
//					if (nHScroll > btnEditSoftKeypadConfirm.getY()) {
//						nestedScrollView.setScrollY(nHScroll - (int) btnEditSoftKeypadConfirm.getY());
//					}
//				}
//			} else {
//				// 소프트키보드가 사라질 때의 높이 조절
//				if(bKeyboardOpen) {
//					bKeyboardOpen = false;
//
//					rlp.bottomMargin = 0;
//					rl_content.setLayoutParams(rlp);
//				}
//			}
			//2023.12.28 by CYJ - 앱바 스크롤 <<
		}
	};

}


