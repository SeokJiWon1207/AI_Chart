/**
 *
 */
package drfn.chart.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import drfn.chart.comp.DRAlertDialog;
import drfn.chart.graph.AbstractGraph;
import drfn.chart.util.COMUtil;

/**
 * @author user
 *
 */
public class DetailJipyoController extends View
		implements View.OnClickListener {

//	private static final int LAUNCHED_ACTIVITY_JipyoSetup = 1;

	ArrayList<JipyoChoiceItem> m_itemsArr;
	Handler mHandler=null;
	Handler initHandler=null;
	//2012. 8. 21 상세설정창 x버튼 누르면 상세설정창과 지표설정창 오픈된 것 다 닫히게 구현 : I_tab20
	Handler closeHandler=null;
	RelativeLayout layout = null;
	String title = null;
	private Context context = null;
	public boolean isPopupState = false;
	View jipyoui = null;
	ScrollView sv = null;
	TextView tvDivider = null;

	public JipyoListViewByLongTouch jipyoListViewByLongTouch; //2017. 3. 9 by hyh - 지표 상세설정 여러개 팝업되지 않도록 처리

	public DetailJipyoController(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUI() {
		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = 0;
//		View jipyoui = null;

		//2012. 8. 28 캔들 / 거래량 / 그외의 경우를 각각 다르게 레이아웃 파일 로드 : I97, I98
		if(this.title.equals("캔들"))
		{
			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				layoutResId = this.getContext().getResources().getIdentifier("chart_candle_b_tab", "layout", this.getContext().getPackageName());
				layoutResId = this.getContext().getResources().getIdentifier("chart_candle_b_tab", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				jipyoui.setTag("candle_tab");
			} else {
				layoutResId = this.getContext().getResources().getIdentifier("chart_candle_b", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				//jipyoui.setBackgroundColor(Color.WHITE);
				jipyoui.setTag("candle");
			}
			// 2016.04.22 black 테마
//			if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//				layoutResId = this.getContext().getResources().getIdentifier("chart_candle_b_black", "layout", this.getContext().getPackageName());
//				jipyoui = factory.inflate(layoutResId, null);
//				//jipyoui.setBackgroundColor(Color.WHITE);
//				jipyoui.setTag("candle");
//			}
			// 2016.04.22 black 테마
		}
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
		else if(this.title.equals("거래량") || this.title.indexOf("거래량"+COMUtil.JIPYO_ADD_REMARK)!=-1)
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end
		{
			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				layoutResId = this.getContext().getResources().getIdentifier("chart_volume_b_tab", "layout", this.getContext().getPackageName());
				layoutResId = this.getContext().getResources().getIdentifier("chart_volume_b_tab", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				jipyoui.setTag("volume_tab");
			} else {
				layoutResId = this.getContext().getResources().getIdentifier("chart_volume_b", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				//jipyoui.setBackgroundColor(Color.WHITE);
				jipyoui.setTag("volume");
//				if(COMUtil.skinType == COMUtil.SKIN_BLACK){
//					layoutResId = this.getContext().getResources().getIdentifier("chart_volume_b_black", "layout", this.getContext().getPackageName());
//					jipyoui = factory.inflate(layoutResId, null);
//					//jipyoui.setBackgroundColor(Color.WHITE);
//					jipyoui.setTag("volume");
//				}
			}
		}
		else if(this.title.equals("분틱차트 주기"))
		{
			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				layoutResId = this.getContext().getResources().getIdentifier("chart_volume_b_tab", "layout", this.getContext().getPackageName());
				layoutResId = this.getContext().getResources().getIdentifier("periodsetting_tab", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				jipyoui.setTag("period_tab");
			} else {
				layoutResId = this.getContext().getResources().getIdentifier("periodsetting", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				//jipyoui.setBackgroundColor(Color.WHITE);
				jipyoui.setTag("period");
			}
		}
		else
		{
			if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
				layoutResId = this.getContext().getResources().getIdentifier("chart_jipyo_b_tab", "layout", this.getContext().getPackageName());
				jipyoui = factory.inflate(layoutResId, null);
				jipyoui.setTag("jipyo_tab");
			} else {
				if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
					layoutResId = this.getContext().getResources().getIdentifier("chart_jipyo_b", "layout", this.getContext().getPackageName());
				} else{
					layoutResId = this.getContext().getResources().getIdentifier("chart_jipyo_b_black", "layout", this.getContext().getPackageName());
				}
				jipyoui = factory.inflate(layoutResId, null);
				//jipyoui.setBackgroundColor(Color.WHITE);
				jipyoui.setTag("jipyo");
				
				// 2016.04.22 black 테마
//				if(COMUtil.skinType == COMUtil.SKIN_BLACK)
//				{
//					layoutResId = this.getContext().getResources().getIdentifier("chart_jipyo_b_black", "layout", this.getContext().getPackageName());
//					jipyoui = factory.inflate(layoutResId, null);
//					//jipyoui.setBackgroundColor(Color.WHITE);
//					jipyoui.setTag("jipyo");
//				}
				// 2016.04.22 black 테마
			}
		}

		//2023.11.24 by CYJ - kakaopay 스크롤시 디바이더 추가 >>
		layoutResId = this.getContext().getResources().getIdentifier("scrollview", "id", this.getContext().getPackageName());
		sv = jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("tvDivider", "id", this.getContext().getPackageName());
		tvDivider = jipyoui.findViewById(layoutResId);
		sv.setOnScrollChangeListener(new OnScrollChangeListener() {
			@Override
			public void onScrollChange(View view, int i, int i1, int i2, int i3) {
				if(view.getScrollY() > 0) {
					tvDivider.setVisibility(VISIBLE);
				} else {
					tvDivider.setVisibility(GONE);
				}
			}
		});
		//2023.11.24 by CYJ - kakaopay 스크롤시 디바이더 추가 <<

		//이미지 줄이기.(OutOfMemory 해결)
//		layoutResId = this.getContext().getResources().getIdentifier("iphone_title", "drawable", this.getContext().getPackageName());
//		Drawable drawable = COMUtil.getSmallBitmap(layoutResId);
//		jipyoui.setBackgroundDrawable(drawable);
		//2012. 7. 17 상세설정창 배경 흰색으로변경 

		//6.26  view1~3 미사용으로 주석처리  
//		View view1 = jipyoui.findViewById(R.id.view1);
//		view1.setBackgroundDrawable(COMUtil.getSmallBitmap(R.drawable.wholebg));
//		View view2 = jipyoui.findViewById(R.id.view2);
//		view2.setBackgroundDrawable(COMUtil.getSmallBitmap(R.drawable.bg_box));
//		View view3 = jipyoui.findViewById(R.id.view3);
//		view3.setBackgroundDrawable(COMUtil.getSmallBitmap(R.drawable.bg_bottom));
		//@drawable/wholebg
		//@drawable/bg_box
		//bg_bottom

		final View jipyouiFinal = jipyoui;

		jipyoui.setOnTouchListener(
				new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {

						return true;
					}
				}
		);

		//2012. 8. 21 상세설정창 x버튼 누르면 상세설정창과 지표설정창 오픈된 것 다 닫히게 구현 : I_tab20
//		if(closeHandler==null) {
//			closeHandler = new Handler() { 
//	    		@Override public void handleMessage(Message msg) {
//	    			layout.removeAllViews();
//	    			gset.destroy();
//	    		}
//	    	};
//		}

		if(mHandler==null) {
			mHandler = new Handler() {
				@Override public void handleMessage(Message msg) {
					//2013. 4. 1   상세설정창 소프트키보드 열린상태로  '닫기 ' 버튼 눌렀을 때 키보드 남아있던 현상
					InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(jipyouiFinal.getWindowToken(), 0);
					layout.removeView(jipyouiFinal);
					gset.destroy();

					//2017. 3. 9 by hyh - 지표 상세설정 여러개 팝업되지 않도록 처리 >>
					if(jipyoListViewByLongTouch != null) {
						jipyoListViewByLongTouch.closePopupViews();
					}
					//2017. 3. 9 by hyh - 지표 상세설정 여러개 팝업되지 않도록 처리 <<
				}
			};
		}

		if(initHandler==null) {
			initHandler = new Handler() {
				@Override public void handleMessage(Message msg) {
					setDefault();
				}
			};
		}

		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnBack", "id", this.getContext().getPackageName());
		Button btnBack = (Button)jipyoui.findViewById(layoutResId);
//		Button btnFunc = (Button) jipyoui.findViewById(R.id.frameaBtnFunction);  btnFunc 사용안함으로 주석처리. 6. 26 
		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnInit", "id", this.getContext().getPackageName());
		Button btnInit = (Button) jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("frameaTitle", "id", this.getContext().getPackageName());
		TextView textTitle = (TextView)jipyoui.findViewById(layoutResId);
		layoutResId = this.getContext().getResources().getIdentifier("navi_frameaTitle", "id", this.getContext().getPackageName());
		TextView naviTextTitle = (TextView)jipyoui.findViewById(layoutResId);

		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
//        textTitle.setText(COMUtil.getAddJipyoTitle(this.title)+" 설정");
		//2023.11.15 by CYJ - kakaopay 지표명을 한글로 표현 >>
		if(this.title.equals("주가이동평균")) {
			textTitle.setText("이동평균선");
		} else if(this.title.equals("Bollinger Band")) {
			textTitle.setText("볼린저밴드");
		} else if(this.title.equals("Envelope")) {
			textTitle.setText("엔벨로프");
		} else {
			textTitle.setText(COMUtil.getAddJipyoTitle(this.title));
		}
		naviTextTitle.setText(textTitle.getText());
		//2023.11.15 by CYJ - kakaopay 지표명을 한글로 표현 <<

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				gset.contUI.bBack = true; //2023.12.27 by CYJ - 변경사항에 대한 적용 여부 팝업창 뒤로가기에만 보이도록 설정
				gset.contUI.reSetJipyo(); 	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창
			}
		});

		sv.setOnScrollChangeListener(
			new OnScrollChangeListener() {
			 @Override
			 public void onScrollChange(View view, int i, int i1, int i2, int i3) {
				 if(sv.getScrollY() == 0) {
					 naviTextTitle.setAlpha(0);
					 tvDivider.setAlpha(0);
				 } else if((float)sv.getScrollY() / (float) naviTextTitle.getBottom() < 1) {
					 if((float)sv.getScrollY() / (float) naviTextTitle.getBottom() > 0.5) {
						 naviTextTitle.setAlpha((float) sv.getScrollY() / (float) naviTextTitle.getBottom());
						 tvDivider.setAlpha((float) sv.getScrollY() / (float) naviTextTitle.getBottom());
					 }
				 } else {
					 naviTextTitle.setAlpha(1.0f);
					 tvDivider.setAlpha(1.0f);
				 }
			 }
		 });
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end

		//2012. 8. 21 태블릿의 상세설정창 x버튼 누르면 상세설정창과 지표설정창 오픈된 것 다 닫히게 구현 : I_tab20
		Button btnClose=null;
		//2012. 9. 14  분틱차트 설정창 팝업윈도우로 따로 빼게되어  태블릿 상세설정창에서 전체 다 닫는 x 버튼 로딩하는 것을 분틱차트설정창은 제외
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) && !jipyoui.getTag().equals("period_tab"))
		{
			layoutResId = this.getContext().getResources().getIdentifier("frameaBtnClose", "id", this.getContext().getPackageName());
			btnClose = (Button)jipyoui.findViewById(layoutResId);
		}

		AbstractGraph abGraph = COMUtil._mainFrame.mainBase.baseP._chart.getGraph(this.title);

		//2012. 8. 8  외국인보유비중등 변수설정 삭제 : I77 I78 I79 I80  I81  I82
		int[] interval = null;
		String[] s_interval = null;
		if(abGraph != null)
		{
			interval = abGraph.interval;
			s_interval = abGraph.s_interval;
		}

		if(s_interval != null && interval[0] == 0 && s_interval[0].equals(""))
		{
			layoutResId = this.getContext().getResources().getIdentifier("paramtv", "id", this.getContext().getPackageName());
			TextView paramtv = (TextView)jipyoui.findViewById(layoutResId);
			paramtv.setVisibility(View.GONE);

			layoutResId = this.getContext().getResources().getIdentifier("paramlinear", "id", this.getContext().getPackageName());
			LinearLayout paramlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			paramlinear.setVisibility(View.GONE);

//			//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)>>
//			layoutResId = this.getContext().getResources().getIdentifier("detail_tab", "id", this.getContext().getPackageName());
//			LinearLayout detailTabLinear = (LinearLayout)jipyoui.findViewById(layoutResId);
//			detailTabLinear.setVisibility(View.GONE);
//			//2015. 1. 13 각 보조지표 설명/활용법 추가(상세설정창)<<
		}
		//2012. 11. 5  매물대분석의 "대기매물" 색굵기설정을 표시 안되게 수정 : I109
		if(this.title.equals("매물대"))
		{
//			layoutResId = this.getContext().getResources().getIdentifier("colortv", "id", this.getContext().getPackageName());
//			TextView colortv = (TextView)jipyoui.findViewById(layoutResId);
//			colortv.setVisibility(View.GONE);

			layoutResId = this.getContext().getResources().getIdentifier("colorlinear", "id", this.getContext().getPackageName());
			LinearLayout colorlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			if(colorlinear != null)
			{
				colorlinear.setVisibility(View.GONE);
			}

			layoutResId = this.getContext().getResources().getIdentifier("standscalelinear", "id", this.getContext().getPackageName());
			LinearLayout standscalelinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			if(standscalelinear != null)
			{
				standscalelinear.setVisibility(View.GONE); //2023.11.08 by CYJ - kakaopay 이평선 외의 지표에 생략 (매물대 Layout)
			}
		}
		else
		{
			layoutResId = this.getContext().getResources().getIdentifier("colorlinear", "id", this.getContext().getPackageName());
			LinearLayout colorlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			if(colorlinear != null)
			{
				colorlinear.setVisibility(View.VISIBLE);
			}

			layoutResId = this.getContext().getResources().getIdentifier("standscalelinear", "id", this.getContext().getPackageName());
			LinearLayout standscalelinear = (LinearLayout)jipyoui.findViewById(layoutResId);
			if(standscalelinear != null)
			{
				standscalelinear.setVisibility(View.GONE);
			}
		}
//
//		else if(abGraph != null && abGraph.graphTitle.equals("매물대분석"))
//		{
//			layoutResId = this.getContext().getResources().getIdentifier("colortv", "id", this.getContext().getPackageName());
//			TextView colortv = (TextView)jipyoui.findViewById(layoutResId);
//			colortv.setVisibility(View.GONE);
//			
//			layoutResId = this.getContext().getResources().getIdentifier("colorlinear", "id", this.getContext().getPackageName());
//			LinearLayout colorlinear = (LinearLayout)jipyoui.findViewById(layoutResId);
//			colorlinear.setVisibility(View.GONE);
//		}

		//2012. 8. 21 상세설정창 x버튼 누르면 상세설정창과 지표설정창 오픈된 것 다 닫히게 구현 : I_tab20
		//2012. 9. 14  분틱차트 설정창 팝업윈도우로 따로 빼게되어  태블릿 상세설정창에서 전체 다 닫는 x 버튼 로딩하는 것을 분틱차트설정창은 제외
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB) && !jipyoui.getTag().equals("period_tab"))
//		{
//			btnClose.setOnClickListener(new Button.OnClickListener() {
//		      	public void onClick(View v) {
////			        	try {
////			        		//이전화면으로 이동.
////			        		Message msg = new Message();
////			        		closeHandler.sendMessage(msg);
////			        	}
////			        	catch (Exception e) {        	     
////			        	}
//		      			COMUtil._mainFrame.closePopup();
//		      			
		//2012. 9. 13 태블릿에서 지표설정창 및 상세설정장 모두 닫는 X 버튼 누를때 분틱차트설정의 값이 저장되지 않는 현상 수정
//		      			Message msg = new Message();
//		        		mHandler.sendMessage(msg);
//			        }
//		      });
//		}

		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					boolean bWebview = gset.contUI.closeWebview();
					if(bWebview==true)
					{
						//이전화면으로 이동.
//						Message msg = new Message();
//						mHandler.sendMessage(msg);
						gset.contUI.hideKeyPad();
						gset.contUI.bBack = true; //2023.12.27 by CYJ - 변경사항에 대한 적용 여부 팝업창 뒤로가기에만 보이도록 설정
						gset.contUI.reSetJipyo(); 	//2023.12.18 by CYJ - 변경사항에 대한 적용 여부 팝업창
					}
				}
				catch (Exception e) {
				}
			}
		});

//		btnFunc.setOnClickListener(new Button.OnClickListener() {
//      	public void onClick(View v) {
//	        	try {
//	        		Message msg = new Message();
//	        		mHandler.sendMessage(msg);
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });

		btnInit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Message msg = new Message();
//		        		String textTochange = "";
//		            	msg.obj = textTochange;
					initHandler.sendMessage(msg);
					Toast.makeText(context, "설정값이 초기화되었습니다.", Toast.LENGTH_LONG).show();
				}
				catch (Exception e) {
				}
			}
		});

		//06.25 지표상세설정 레이아웃 수정 
		//2012. 7. 17 지표상세설정 width, height 를 꽉채움으로 변경 
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.leftMargin=0;
		params.topMargin=0;
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

//		if(!isPopupState) {
//			Rect rect = new Rect();
//			Window window= COMUtil._chartMain.getWindow();
//			window.getDecorView().getWindowVisibleDisplayFrame(rect);
//	//		int parentLinearTop = rect.top;
//			params.topMargin = rect.top;
//		} else {
		params.topMargin = 0;
//		}
//		System.out.println("indicatorTop:"+params.topMargin);

//		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		LinearLayout detaillinear = new LinearLayout(COMUtil.apiView.getContext());

		jipyoui.setLayoutParams(params);
		detaillinear.addView(jipyoui);
		detaillinear.setLayoutParams(params);
		detaillinear.setTag("detailLayout");
		this.layout.addView(detaillinear);
		gset = new GraphSetUI(COMUtil.apiView.getContext(), this.layout);
		//2012. 8. 28 거래량 추가설정, 캔들 설정에 따라 다른 UI 로딩을 위해  타입값을 setUI 에서 받아오게 변경 : I97, I98 
//		gset.setUI(strType);
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
//			gset.setUI("jipyo");
//		}
//		else
//		{
		if(this.title.equals("캔들"))
		{
			gset.setUI("candle");
		}
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다.
		else if(this.title.equals("거래량") || this.title.indexOf("거래량"+COMUtil.JIPYO_ADD_REMARK)!=-1)
		//2015. 1. 13 by lyk - 동일지표 이름을 화면에 보여줄때는 COMUtil.JIPYO_ADD_REMARK 이전까지 이름을 사용한다. end
		{
			gset.setUI("volume");
		}
		else if(this.title.equals("분틱차트 주기"))
		{
			gset.setUI("period");
		}
		else
		{
			gset.setUI("jipyo");
		}
//		}



//		this.layout.addView(jipyoui);
//		
//		gset = new GraphSetUI(context, this.layout);
//		gset.setUI("jipyo");
//		this.layout.addView(gset);
	}
	JipyoListViewByLongTouch parent = null;
	public void setParent(JipyoListViewByLongTouch parent) {
		this.parent = parent;
	}
	private void setReload() {
		if(parent!=null)
			parent.reload();

	}
	public GraphSetUI gset=null;
	public void setInitGraph(String name) {
		gset.setInitGraph(name);
	}
	public void setDefault() {
		gset.setDefault();
	}

//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        
//		setContentView(R.layout.chart_jipyo_b);
//		
//		Button btnBack = (Button) findViewById(R.id.frameaBtnBack);
//		Button btnFunc = (Button) findViewById(R.id.frameaBtnFunction);
//		TextView textTitle = (TextView) findViewById(R.id.frameaTitle);
////		textTitle.layout(0, 0, COMUtil.chartWidth-(int)COMUtil.getPixel(80), (int)COMUtil.getPixel(20));
//		
//		btnBack.setText("지표설정");
//		btnBack.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		setResult(RESULT_CANCELED);
//	        		finish();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });
//		
//		textTitle.setText("설정");
//		btnFunc.setText("닫기");
//		
//		btnFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		setResult(RESULT_CANCELED);
//	        		finish();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });
//		
//		//m_itemsArr = new ArrayList<SigChoiceItem>();
//		m_itemsArr = FindDataManager.getUserJipyoItems();
//
//		JipyoChoiceItem item1 = new JipyoChoiceItem("", "", false);
//
//		m_itemsArr.add(item1);
//
//		// 커스텀 ArrayAdapter 선언/초기화.
//		m_scvAdapter = new MyArrayAdapter(this, m_itemsArr);
//		
//		// 본 Activity의 아답터로 m_scvAdapter 지정.
//		setListAdapter(m_scvAdapter);
//	}

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
		int btnID = view.getId();
		switch(btnID) {
//			case R.id.frameaBtnFunction:
//			break;
		}
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		//implement here for click item.
		//Log.v("sigchoiceview", "onListItemClick="+position);

		String textTochange = "Item Click!";
		COMUtil.showMessage(context, textTochange); //Context, String msg
	}

	public boolean onTouchEvent(final MotionEvent evt) {
		return false;
	}

	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
	public void closePopupViews()
	{
		if(null != gset)
		{
			//각 상세설정의 키보드를 닫는다. 
			InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(jipyoui.getWindowToken(), 0);

			gset.destroy();
		}
	}
	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == LAUNCHED_ACTIVITY_JipyoSetup) {
//			if(resultCode == RESULT_OK)
//			{
//				String strSiseTitle, strSignalTitle;
//				strSiseTitle = data.getExtras().getString("SiseTitle");
//				strSignalTitle = data.getExtras().getString("SignalTitle");
//				
//				int nPosition = data.getExtras().getInt("listIndex");;
//				JipyoChoiceItem oneItem = m_itemsArr.get(nPosition);
////				oneItem.setTitleSise(strSiseTitle);
////				oneItem.setTitleSignal(strSignalTitle);
//				
//				m_scvAdapter.notifyDataSetChanged();
//			}
//		}
//		else {
//			super.onActivityResult(requestCode, resultCode, data);
//		}
//	}
//	
//	//ArrayAdapter에서 상속받는 커스텀 ArrayAdapter 정의.
//	class MyArrayAdapter extends ArrayAdapter<JipyoChoiceItem> {
//
//	    // 생성자 내부에서 초기화
//		private Context context;
//	    private ViewWrapper wrapper = null;
//	    private ArrayList<JipyoChoiceItem> mitems;
//	    private static final int gnSigChoiceViewCellTypeID = R.layout.jipyo_celltype_c;	///< 화면의 layout ID. 
//
//	    // 생성자
//	    MyArrayAdapter(Context context, ArrayList<JipyoChoiceItem> items) {
//	        super(context, gnSigChoiceViewCellTypeID, items);
//	
//	        // instance 변수(this.context)를 생성자 호출시 전달받은 지역 변수(context)로 초기화.
//	        this.context = context;
//	        this.mitems = items;
//	    }
//	 // ListView에서 각 행(row)을 화면에 표시하기 전 호출됨.
//	
//	    public View getView(int position, View convertView, ViewGroup parent){
//	        View row = convertView;
//	        
//	        if(row == null) {
//	            // LayoutInflater의 객체 inflater를 현재 context와 연결된 inflater로 초기화.
//	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//	
//	            // inflator객체를 이용하여 \res\laout\cellsigview.xml 파싱
//	            row = (View)inflater.inflate(gnSigChoiceViewCellTypeID, null);
//	            
//	            wrapper = new ViewWrapper(row);
//	            row.setTag(wrapper);
//	        }
//	        else {
//	        	wrapper = (ViewWrapper)row.getTag();
//	        }
//	
//	        JipyoChoiceItem oneItem = mitems.get(position);
////	        wrapper.getCtrlCodeName().setText(oneItem.getCodeName());
////
////	        wrapper.getCtrlCode().setText(oneItem.getCode());
////	        wrapper.getCtrlCodeName().setText(oneItem.getCodeName());
////	        wrapper.getCtrlSise().setText(oneItem.getTitleSise());
////	        wrapper.getCtrlSig().setText(oneItem.getTitleSignal());
//
//	        // 커스터마이징 된 View 리턴.
//	        return row;
//	
//	    }
//	}
//	
//	//
//	// Holder Pattern을 구현하는 ViewWrapper 클래스
//	//
//	class ViewWrapper {
//		 private View base;
//		 private TextView  ctlCode, ctlCodeName, ctlSise, ctlSig;
//	
//		 ViewWrapper(View base) {
//		     this.base = base;
//		 }
//	
//		 // 멤버 변수가 null일때만 findViewById를 호출
//		 // null이 아니면 저장된 instance 리턴 -> Overhaed 줄임
//		 TextView getCtrlCode() {
//		     if(ctlCode == null) {
//		    	 ctlCode = (TextView)base.findViewById(R.id.fctb_TextViewB01);
//		     }          
//		     return ctlCode;
//		 }
//		 
//		 TextView getCtrlCodeName() {
//		     if(ctlCodeName == null) {
//		    	 ctlCodeName = (TextView)base.findViewById(R.id.fctb_TextViewA01);
//		     }          
//		     return ctlCodeName;
//		 }
//		 
//		 TextView getCtrlSise() {
//		     if(ctlSise == null) {
//		    	 ctlSise = (TextView)base.findViewById(R.id.fctb_TextViewA02);
//		     }          
//		     return ctlSise;          
//		 }
//		 
//		 TextView getCtrlSig() {
//		     if(ctlSig == null) {
//		    	 ctlSig = (TextView)base.findViewById(R.id.fctb_TextViewB02);
//		     }          
//		     return ctlSig;          
//		 }	
//	}
}
