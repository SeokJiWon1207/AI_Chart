package drfn.chart.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


import drfn.chart.draw.LineDialog;
import drfn.chart.draw.paletteDialog;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;

import static drfn.chart.util.COMUtil.mHandler;

public class BaseLineView extends View {
	CheckBox[] btnFuncs = new CheckBox[20];
	LinearLayout[] lys = new LinearLayout[20];

	TextView[] colors = new TextView[20];
	TextView[] lineImgs = new TextView[20];
	TextView[] lineTexts = new TextView[20];

	RelativeLayout layout = null;
	View baselineView = null;

	LinearLayout baselineLayout;

	private SharedPreferences m_prefConfig = null;

	public static paletteDialog palette = null;

	int[] Lines = {
			this.getContext().getResources().getIdentifier("line_width01", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width06", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width07", "drawable", this.getContext().getPackageName()),};

    Context context = null;

	//2012. 7. 24  기준선창   전체화면을 위해 addview 하는 layout 을  화면전체 layout 받아옴.  그래서 relative 변경 
	ArrayList<Integer> baselineTypes;

	int g_nBaseLineColor[][]= {
			{228, 0, 56},
			{203, 101, 210},
			{106, 158, 208},
			{61, 180, 162},
			{159, 255, 0},
			{252, 135, 5},
			{159, 255, 0},
			{252, 135, 5},
			{152, 116, 232},
			{158, 40, 255},
			{230, 100, 39},
			{125, 125, 125},
			{228, 0, 56},
			{203, 101, 210},
			{106, 158, 208},
			{61, 180, 162},
			{159, 255, 0},
			{252, 135, 5},
			{152, 116, 232},
			{158, 40, 255}
	};
	int g_nBaseLineThick[] = {  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

	public BaseLineView(Context context, final RelativeLayout layout) {
		super(context);
		this.context= context;
		m_prefConfig = context.getSharedPreferences("baseLine", Context.MODE_PRIVATE);
		if(mHandler == null){
			mHandler = new Handler(){
				@Override public void handleMessage(Message msg) {
					close();
				}
			};
		}
//		//2012. 8. 7  해상도 크기로 리스트뷰 크기를 결정하기 위함 
//		Display dis = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
//		int mDisWidth = dis.getWidth();            // 가로 사이즈 
//		int mDisHeight = dis.getHeight();          // 세로 사이즈
//		
//		//2012. 8. 13  일부기기에서 리스트뷰 하단 짤리는 현상 : I95
//		if((mDisWidth >= 720 && mDisHeight >= 1280) || (mDisWidth >= 1280 && mDisHeight >= 720))
//		{
//			bHighResolution = true;
//		}


		//2012. 10. 2  체크박스 이미지 변경  : I106
//		stateDrawable = new StateListDrawable();

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

//		int layoutResId = context.getResources().getIdentifier("checkbox_off_background2", "drawable", context.getPackageName());
//		Bitmap image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		Bitmap resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	normal = (Drawable)(new BitmapDrawable(resizeImage));

//		layoutResId = context.getResources().getIdentifier("checkbox_on_background2", "drawable", context.getPackageName());
//		image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//    	press = (Drawable)(new BitmapDrawable(resizeImage));

		final ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;

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
				context.getResources().getIdentifier("public_checkbox12", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox01", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox02", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox03", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox04", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox05", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox06", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox07", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_pdcheckbox08", "id", context.getPackageName())


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
				context.getResources().getIdentifier("baselinear12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear01", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear02", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear03", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear04", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear05", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear06", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear07", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdlinear08", "id", context.getPackageName())

		};

		int[] ids_pt = { context.getResources().getIdentifier("baseline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv8", "id", context.getPackageName())
		};

		int[] ids_linethi = { context.getResources().getIdentifier("baseline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview8", "id", context.getPackageName())
		};

		baselineLayout = new LinearLayout(context);
		//2012. 7. 17 레이아웃 width, height 조절 
		baselineLayout.setTag(_cvm.baseLineType);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		baselineLayout.setLayoutParams(params);

		//2014. 10. 14 popupwindow 배경 반투명색>>
		baselineLayout.setBackgroundColor(Color.argb(150, 0, 0, 0));
		baselineLayout.setGravity(Gravity.CENTER);
		//2014. 10. 14 popupwindow 배경 반투명색<<

		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 16 자동추세창 레이아웃 크기 및 위치 조절 : T_tab01
		int resId;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			resId = context.getResources().getIdentifier("chart_baseline_tab", "layout", context.getPackageName());
		}
		else
		{
			resId = context.getResources().getIdentifier("chart_baseline", "layout", context.getPackageName());
		}
		baselineView = (LinearLayout) factory.inflate(
				resId, null);
		//2012. 7. 25 뷰를 add 하기전에 setlayoutparam 안해주면 원하는 전체 레이아웃을 얻을 수 없음. 
//		Display hDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//    	int m_nWidth = hDisplay.getWidth();
//    	int m_nHeight= hDisplay.getHeight();
//		
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int)COMUtil.getPixel(m_nWidth), (int)COMUtil.getPixel(m_nHeight));
		//2014. 10. 14 popupwindow 배경 반투명색>>
		baselineView.setLayoutParams(params);
//		baselineView.setLayoutParams(new LayoutParams((int) COMUtil.getPixel(280),(int) COMUtil.getPixel(339)));
		//2014. 10. 14 popupwindow 배경 반투명색<<
		baselineView.setTag("baseline");
		baselineLayout.addView(baselineView);
		layout.addView(baselineLayout);

//		resId = context.getResources().getIdentifier("frameabtnfunction", "id", context.getPackageName());
//		Button btnclose = (Button) baselineView
//				.findViewById(resId);

		//2012. 7. 24 기준선 초기화 버튼 추가 
		//2012. 8. 16  기준선창 기본값 버튼 삭제. 허니콤모드 아닐때만 버튼 리스너를 등록한다.  : T_tab07
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			//2013. 11. 13 기준선설정창 초기화 버튼 동작안함>>
			resId = context.getResources().getIdentifier("btn_baseline_refresh", "id", context.getPackageName());
			Button btnRefresh = (Button) baselineView.findViewById(resId);
			btnRefresh.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setDefault();
				}
			});
			//2013. 11. 13 기준선설정창 초기화 버튼 동작안함<<
		}
		else
		{
			resId = context.getResources().getIdentifier("btn_baseline_refresh", "id", context.getPackageName());
			Button btnRefresh = (Button) baselineView.findViewById(resId);
			btnRefresh.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setDefault();
				}
			});
		}

		resId = context.getResources().getIdentifier("baselinescroll", "id", context.getPackageName());
		final ScrollView baselinView = baselineView.findViewById(resId);
		baselinView.setVisibility(View.VISIBLE);

		resId = context.getResources().getIdentifier("pivotdemarkscroll", "id", context.getPackageName());
		final ScrollView pdView = baselineView.findViewById(resId);
		pdView.setVisibility(View.GONE);

		resId = context.getResources().getIdentifier("baslineSetBtn", "id", context.getPackageName());
		final Button btnBasline = (Button) baselineView.findViewById(resId);
		//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start
		btnBasline.setSelected(true);
		btnBasline.setTypeface(COMUtil.typefaceBold);
		//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start

		resId = context.getResources().getIdentifier("PDlineSetBtn", "id", context.getPackageName());
		final Button btnPDline = (Button) baselineView.findViewById(resId);

		btnBasline.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				btnBasline.setSelected(true);
				btnPDline.setSelected(false);
				//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start
				baselinView.setVisibility(View.VISIBLE);
				pdView.setVisibility(View.GONE);
				btnBasline.setTextColor(Color.rgb(17, 17, 17));
				btnPDline.setTextColor(Color.rgb(102, 102, 102));
				btnBasline.setTypeface(COMUtil.typefaceBold);
				btnPDline.setTypeface(COMUtil.typeface);
				//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start
			}
		});

		btnPDline.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				btnBasline.setSelected(false);
				btnPDline.setSelected(true);
				//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start
				baselinView.setVisibility(View.GONE);
				pdView.setVisibility(View.VISIBLE);
				btnPDline.setTextColor(Color.rgb(17, 17, 17));
				btnBasline.setTextColor(Color.rgb(102, 102, 102));
				btnPDline.setTypeface(COMUtil.typefaceBold);
				btnBasline.setTypeface(COMUtil.typeface);
				//2020.05.14 by JJH >> 기준선 설정 화면 UI 작업 start
			}
		});


//
//		btnclose.setOnClickListener(new Button.OnClickListener() {
//			public void onClick(View v) {
//				try {
//				//2012.7.24 기준선창 삭제할 대상 view->layout  으로 변경 
//					layout.removeView(baselineLayout);
//				} catch (Exception e) {
//				}
//			}
//		});

		//2012. 7. 24 기준선설정 '기본값' 버튼 리스너 등록 
//		btnRefresh.setOnClickListener(new Button.OnClickListener() {
//			public void onClick(View v) {
//				setDefault();
//			}
//		});

		//2013. 3. 6 기준선 다중선택 
		baselineTypes = _cvm.baseLineType;

		for(int i = 0; i < 4; i++){
			int layoutResId = context.getResources().getIdentifier("baseline_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvBaseline = (TextView)baselineView.findViewById(layoutResId);
			tvBaseline.setTypeface(COMUtil.typefaceMid);
		}

		for (int i = 0; i < ids.length; i++) {
			CheckBox btnFunc = (CheckBox) baselineView.findViewById(ids[i]);

			//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
//			stateDrawable = imageChange(normal, press);
//			btnFunc.setButtonDrawable(stateDrawable);

//			if (COMUtil.baseLineType == i + 1) {
//				btnFunc.setSelected(true);
//			} else {
//				btnFunc.setSelected(false);
//			}
			btnFunc.setTag(String.valueOf(i+1));

			//2013. 3. 6 기준선 다중선택 
			for(int type : baselineTypes)
			{
				if(type == Integer.parseInt((String)btnFunc.getTag()))
				{
					btnFunc.setChecked(true);
					break;
				}
			}

			btnFuncs[i] = btnFunc;
			btnFunc.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox chk = (CheckBox)v;
//					boolean isOn = chk.isChecked();
//					int tag = Integer.parseInt((String) v.getTag());
//
//					if(isOn)
//					{
//						_cvm.baseLineType.add(tag);
//					}
//					else
//					{
//						for(int type : _cvm.baseLineType)
//						{
//							if(tag == type)
//							{
//								_cvm.baseLineType.remove((Integer)type);
//								break;
//							}
//						}
//					}


					//2012. 7. 24 기준선 선택 해제시 기준선 사라지지 않는 현상 수정 
//					if(COMUtil.baseLineType == tag)
//					{
//						COMUtil.baseLineType = -1;
//					}
//					else
//					{
//						COMUtil.baseLineType = tag;
//					}
//					for (int i = 0; i < 4; i++) {
//						if (COMUtil.baseLineType != i + 1) {
//							btnFuncs[i].setSelected(false);
//						}
//						else
//							btnFuncs[i].setSelected(true);
//						
//					}
					//COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
//					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
				
				
			});
			
		}
		for (int i = 0; i < ids1.length; i++) {
			LinearLayout ly = (LinearLayout) baselineView.findViewById(ids1[i]);
			final CheckBox btnFunc = (CheckBox) baselineView.findViewById(ids[i]);
	
	
			lys[i] = ly;
			ly.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					btnFunc.setChecked(!btnFunc.isChecked());	

//					int tag = Integer.parseInt((String) btnFunc.getTag());
//					boolean isOn = btnFunc.isChecked();
//					if(isOn)
//					{
//						_cvm.baseLineType.add(tag);
//					}
//					else
//					{
//						for(int type : _cvm.baseLineType)
//						{
//							if(tag == type)
//							{
//								_cvm.baseLineType.remove((Integer)type);
//								break;
//							}
//						}
//					}
//					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
				
				
			});
			
		}
		for (int i = 0; i < ids_pt.length; i++) {

			final TextView pt  =(TextView)baselineView.findViewById(ids_pt[i]);
			final TextView lineImg = (TextView)baselineView.findViewById(ids_linethi[i]);

			colors[i] = pt;
			lineImgs[i] = lineImg;

			pt.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showColorPalette(pt);

				}
			});
			lineImg.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showLineSelect(lineImg);

				}
			});

		}
		setEnvData();

		COMUtil.setGlobalFont(baselineLayout);

	}
	public void setCheckItem(View v)
	{
		CheckBox chk = (CheckBox)v;
		boolean isOn = chk.isChecked();
		int tag = Integer.parseInt((String) v.getTag());

		ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;
		if(isOn)
		{
			_cvm.baseLineType.add(tag);
		}
		else
		{
			for(int type : _cvm.baseLineType)
			{
				if(tag == type)
				{
					_cvm.baseLineType.remove((Integer)type);
					break;
				}
			}
		}
	}

	public void setApply()
	{
		ChartViewModel cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;
		cvm.baseLineType.clear();
		for (int i = 0; i < btnFuncs.length; i++) {
			CheckBox btnChk = (CheckBox) btnFuncs[i];
			int tag = Integer.parseInt((String) btnChk.getTag());
			boolean isOn = btnChk.isChecked();
			if(isOn)
			{
				cvm.baseLineType.add(tag);
			}
		}
		COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
		COMUtil.saveLastState(COMUtil._mainFrame.strFileName);
	}

	//2012. 7. 27 API 용 생성자 추가 (apiLayout 사용) 
	public BaseLineView(Context context, final ViewGroup layout) {
		super(context);

		ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;

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
				context.getResources().getIdentifier("public_checkbox11", "id", context.getPackageName()) };
		
		int[] ids1 = { context.getResources().getIdentifier("title01", "id", context.getPackageName()),
				context.getResources().getIdentifier("title02", "id", context.getPackageName()),
				context.getResources().getIdentifier("title03", "id", context.getPackageName()),
				context.getResources().getIdentifier("title04", "id", context.getPackageName()),
				context.getResources().getIdentifier("title05", "id", context.getPackageName()),
				context.getResources().getIdentifier("title06", "id", context.getPackageName()),
				context.getResources().getIdentifier("title07", "id", context.getPackageName()),
				context.getResources().getIdentifier("title08", "id", context.getPackageName()),
				context.getResources().getIdentifier("title09", "id", context.getPackageName()),
				context.getResources().getIdentifier("title10", "id", context.getPackageName()),
				context.getResources().getIdentifier("title11", "id", context.getPackageName()) };

		LinearLayout baselineLayout = new LinearLayout(context);
		//2012. 7. 17 레이아웃 width, height 조절 
		baselineLayout.setTag(_cvm.baseLineType);
//		Display hDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//    	int m_nWidth = hDisplay.getWidth();
//    	int m_nHeight= hDisplay.getHeight();

//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int)COMUtil.getPixel(m_nWidth), (int)COMUtil.getPixel(m_nHeight));
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		baselineLayout.setLayoutParams(params);

		LayoutInflater factory = LayoutInflater.from(context);

		int resId = context.getResources().getIdentifier("chart_baseline", "layout", context.getPackageName());
		baselineView = (LinearLayout) factory.inflate(
				resId, null);

		baselineView.setLayoutParams(params);
		baselineView.setTag("baseline");
		baselineLayout.addView(baselineView);
		layout.addView(baselineLayout);

		resId = context.getResources().getIdentifier("frameabtnfunction", "id", context.getPackageName());
		Button btnclose = (Button) baselineView
				.findViewById(resId);

		//2012. 7. 24 기준선 초기화 버튼 추가 
		resId = context.getResources().getIdentifier("btn_baseline_refresh", "id", context.getPackageName());
		Button btnRefresh = (Button) baselineView.findViewById(resId);

		btnclose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					layout.removeView(baselineView);
				} catch (Exception e) {
				}
			}
		});

		//2012. 7. 24 기준선설정 '기본값' 버튼 리스너 등록 
		btnRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setDefault();
			}
		});

		for (int i = 0; i < ids.length; i++) {
			CheckBox btnFunc = (CheckBox) baselineView.findViewById(ids[i]);
			btnFuncs[i] = btnFunc;
			btnFunc.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int tag = Integer.parseInt((String) v.getTag());
					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
			});
		}
		
		for (int i = 0; i < ids1.length; i++) {
			LinearLayout ly = (LinearLayout) baselineView.findViewById(ids1[i]);
			lys[i] = ly;
			ly.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int tag = Integer.parseInt((String) v.getTag());
					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
			});
		}
	}

	//2012. 7. 24 기준선설정 '기본값' 설정 함수 
	private void setDefault() {
		for(int i=0; i<20; i++) {
			btnFuncs[i].setChecked(false);
		}
		ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;
		_cvm.baseLineType = new ArrayList<Integer>();

		// 2016.05.31 기준선 대비, 색상 굵기 >>
		setEnvString("baseline_color", "");
		setEnvString("baseline_thick", "");
		setEnvString("baseline_chgrate", "");

		_cvm.baseLineType.clear();
		_cvm.baseLineColors.clear();
		_cvm.baseLineThicks.clear();

		this.setEnvData();
		// 2016.05.31 기준선 대비, 색상 굵기 <<

		COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
	}

//	//2012. 10. 2  체크박스 이미지 변경  : I106
//	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
//        StateListDrawable imageDraw = new StateListDrawable();
//        imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
//        imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
//        return imageDraw; 
//    }

	@Override
	public void destroyDrawingCache() {
//		int[] ids = { context.getResources().getIdentifier("public_checkbox01", "id", context.getPackageName()), 
//				  context.getResources().getIdentifier("public_checkbox02", "id", context.getPackageName()),
//				  context.getResources().getIdentifier("public_checkbox03", "id", context.getPackageName()),
//				  context.getResources().getIdentifier("public_checkbox04", "id", context.getPackageName()) };
//		for (int i = 0; i < 4; i++) {
//			CheckBox btnFunc = (CheckBox) baselineView.findViewById(ids[i]);
//			StateListDrawable imageDraw = (StateListDrawable)btnFunc.getBackground();
//			DrawableContainerState containerState = (DrawableContainerState)imageDraw.getConstantState();
//			Drawable[] drawables = containerState.getChildren();
//			for(int j=0; j<drawables.length; i++ )
//			{
//				Bitmap b = ((BitmapDrawable)drawables[j]).getBitmap();
//				b.recycle();
//			}
//		}
	}

	// 2016.05.31 기준선 대비, 색상 굵기 >>
	public void setEnvString(String strKey, String strValue)
	{
		if(strKey == null || strValue == null) return;

		if(m_prefConfig == null) return;

		SharedPreferences.Editor editConfig = m_prefConfig.edit();
		editConfig.putString(strKey, strValue);
		editConfig.commit();
	}

	public String getEnvString(String strKey, String strDefault)
	{
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

	public void setEnvData(){
		ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;
		String strColor = getEnvString("baseline_color", "");

		int nBaseLineColor[][]= {
				{228, 0, 56},
				{203, 101, 210},
				{106, 158, 208},
				{61, 180, 162},
				{159, 255, 0},
				{252, 135, 5},
				{159, 255, 0},
				{252, 135, 5},
				{152, 116, 232},
				{158, 40, 255},
				{230, 100, 39},
				{125, 125, 125},
				{228, 0, 56},
				{203, 101, 210},
				{106, 158, 208},
				{61, 180, 162},
				{159, 255, 0},
				{252, 135, 5},
				{152, 116, 232},
				{158, 40, 255}
		};

		if(strColor.length() > 0){

			String strColors[] = strColor.split("=");
			if(strColors.length>=15)
			{
				for(int i = 0; i < 20; i++)
				{
					g_nBaseLineColor[i][0] = nBaseLineColor[i][0] = Integer.parseInt(strColors[i*3+0]);
					g_nBaseLineColor[i][1] = nBaseLineColor[i][1] = Integer.parseInt(strColors[i*3+1]);
					g_nBaseLineColor[i][2] = nBaseLineColor[i][2] = Integer.parseInt(strColors[i*3+2]);
				}
			}

		}
		else{
			for(int i = 0; i< 20 ; i++)
			{
				g_nBaseLineColor[i][0] = nBaseLineColor[i][0];
				g_nBaseLineColor[i][1] = nBaseLineColor[i][1];
				g_nBaseLineColor[i][2] = nBaseLineColor[i][2];

			}
		}

		String strThick = getEnvString("baseline_thick", "");

		int nBaseLineThick[] = {   1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1,1,1,1,1,1,1,1 };

		if(strThick.length() > 0){
			String strThicks [] = strThick.split("=");
			if(strThick.length() >=20)
			{
				for(int i = 0 ; i < 20 ; i++){
					g_nBaseLineThick[i] = nBaseLineThick[i] = Integer.parseInt(strThicks[i]);

				}
			}
		}
		else{
			for(int i = 0 ; i < 20 ; i++)
			{
				g_nBaseLineThick[i] = nBaseLineThick[i];
			}
		}

		int[] ids_pt = { context.getResources().getIdentifier("baseline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv8", "id", context.getPackageName())
		};

		int[] ids_linethi = { context.getResources().getIdentifier("baseline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview8", "id", context.getPackageName())
		};

		_cvm.baseLineColors.clear();
		_cvm.baseLineThicks.clear();

		String sColor = "";
		for(int i = 0 ; i <20 ; i++){

			TextView color = (TextView)baselineView.findViewById(ids_pt[i]);
			TextView lineImg = (TextView)baselineView.findViewById(ids_linethi[i]);

			setButtonColorWithRound(color, Color.rgb(nBaseLineColor[i][0], nBaseLineColor[i][1], nBaseLineColor[i][2]));
			color.setTag(""+Color.rgb(nBaseLineColor[i][0], nBaseLineColor[i][1],nBaseLineColor[i][2]));

			lineImg.setBackgroundResource(Lines[nBaseLineThick[i]-1]);
			lineImg.setTag(""+g_nBaseLineThick[i]);

			//공유정보
			sColor = String.format("%d=%d=%d", g_nBaseLineColor[i][0], g_nBaseLineColor[i][1],  g_nBaseLineColor[i][2]);
			_cvm.baseLineColors.add(i,sColor);
			_cvm.baseLineThicks.add(i, g_nBaseLineThick[i]);

		}

	}

	protected void showColorPalette(TextView tvColorOpen)
	{
		int colorTag = 1;
		try{
			String selColor = (String) tvColorOpen.getTag();
			colorTag = Integer.valueOf(selColor);
		}catch(Exception e){
			e.printStackTrace();
		}

		paletteDialog palette = new paletteDialog(context, 4, 6, tvColorOpen, colorTag);

		palette.setParent(this);

		palette.setCanceledOnTouchOutside(false);
		//2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) start
		WindowManager.LayoutParams params = palette.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		//2020.05.08 by JJH >> 가로모드 작업 (색상 설정 팝업) end
		palette.show();
	}

	public void updateValue() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

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
		drfn.chart.draw.LineDialog linepalette = new LineDialog(context, 5, 1, tvLineOpen, lineTag);
		//LineDialog linepalette = new LineDialog(context, 1, 6, tvLineOpne);
		linepalette.setParent(this);
		linepalette.setCanceledOnTouchOutside(false);
		int[] location = new int[2];
		tvLineOpen.getLocationOnScreen(location);
		//드랍다운리스트뷰의 너비
		int nLineBtnWidth = tvLineOpen.getWidth();
		//드랍다운리스트뷰의 x축좌표
		//2020.05.08 by JJH >> 가로모드 작업 (굵기 설정 팝업) start
//		int nLineBtnLeft = location[0]+(int)COMUtil.getPixel(3);
		//드랍다운리스트뷰의 y축좌표
//		int nLineBtnTop = location[1]-(int)COMUtil.getPixel(32);
		WindowManager.LayoutParams params = linepalette.getWindow().getAttributes();
//		params.y = nLineBtnTop;
//		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*2;
//		params.gravity = Gravity.TOP|Gravity.START;
		params.gravity = Gravity.CENTER;
		//2020.05.08 by JJH >> 가로모드 작업 (굵기 설정 팝업) end
		linepalette.show();
	}

	public void close(){
		String strSaveColor = "";
		String strSaveThick = "";

		int[] ids_pt = { context.getResources().getIdentifier("baseline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_pttv12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_pttv8", "id", context.getPackageName())
		};

		int[] ids_linethi = { context.getResources().getIdentifier("baseline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview8", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview9", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview10", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview11", "id", context.getPackageName()),
				context.getResources().getIdentifier("baseline_lineview12", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview5", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview6", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview7", "id", context.getPackageName()),
				context.getResources().getIdentifier("pdline_lineview8", "id", context.getPackageName())
		};

		int color;

		for(int i = 0 ; i < 20; i++){
			TextView tvColor = (TextView)baselineView.findViewById(ids_pt[i]);
			TextView tvLine = (TextView)baselineView.findViewById(ids_linethi[i]);
			String a = (String)tvColor.getTag();
			String thick = (String)tvLine.getTag();
			color = Integer.parseInt(a);

			strSaveColor += Color.red(color)+"=";
			strSaveColor += Color.green(color)+"=";
			strSaveColor += Color.blue(color)+"=";

			strSaveThick += thick+"=";

		}
		setEnvString("baseline_color", strSaveColor);
		setEnvString("baseline_thick", strSaveThick);

		setEnvData();

		COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
	}

	public void setButtonColorWithRound(View view, int nColor)
	{
		((GradientDrawable)view.getBackground()).setColor(nColor);
		view.setTag(""+nColor);
		if(nColor == Color.rgb(255, 255, 255))
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), Color.rgb(224, 224, 224));
		else
			((GradientDrawable)view.getBackground()).setStroke((int)COMUtil.getPixel(1), nColor);
	}
	// 2016.05.31 기준선 대비, 색상 굵기 <<
}