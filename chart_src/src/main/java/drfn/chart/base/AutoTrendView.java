package drfn.chart.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import drfn.chart.comp.ExEditText;
import drfn.chart.comp.ExEditText.OnBackButtonListener;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;

public class AutoTrendView extends View implements OnBackButtonListener {

	CheckBox[] btnFuncs = new CheckBox[4];
	LinearLayout[] lys = new LinearLayout[4];



	RelativeLayout layout=null;
	ViewGroup autotrendView=null;

	//2012. 7. 24  자동추세선 설정창의 EditText 저장할 변수 추가 
//	 EditText preNameField, endNameField;
	//2013. 3. 26  back 버튼 처리를 위해 확장EditText 생성. 기존과 사용은 동일함.
	ExEditText preNameField, endNameField;

	Context context;

	//2012. 10. 2  체크박스 크기 세팅할 해상도 관련 문제로 사용하기 위해 클레스멤버로 위치이동 : I106
	//boolean bHighResolution = false;
	//2012. 10. 2  체크박스 이미지들을 가지고 있을 listdrawable  : I106
//    StateListDrawable stateDrawable;
//	Drawable normal, press;

	//2012. 7. 24  자동추세창  전체화면을 위해 addview 하는 layout 을  화면전체 layout 받아옴.  그래서 relative 변경 
	public AutoTrendView(Context context, final RelativeLayout layout) {
		super(context);

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
//
//
//		//2012. 10. 2  체크박스 이미지 변경  : I106
////		stateDrawable = new StateListDrawable();
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

//		int layoutResId = context.getResources().getIdentifier("checkbox_off_background2", "drawable", context.getPackageName());
//		Bitmap image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		Bitmap resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//		normal = (Drawable)(new BitmapDrawable(resizeImage));

//		layoutResId = context.getResources().getIdentifier("checkbox_on_background2", "drawable", context.getPackageName());
//		image = BitmapFactory.decodeResource(context.getResources(), layoutResId);	
//		resizeImage = Bitmap.createScaledBitmap(image, nChkBoxSize, nChkBoxSize,true);
//		press = (Drawable)(new BitmapDrawable(resizeImage));


		//키보드감추기 할 때 Context 가 인자로 들어가서 저장 
		this.context = context;

		int[] ids = { context.getResources().getIdentifier("public_checkbox01", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox02", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox03", "id", context.getPackageName()),
				context.getResources().getIdentifier("public_checkbox06", "id", context.getPackageName())};

		int[] ids1 = { context.getResources().getIdentifier("autotrendlinear01", "id", context.getPackageName()),
				context.getResources().getIdentifier("autotrendlinear02", "id", context.getPackageName()),
				context.getResources().getIdentifier("autotrendlinear03", "id", context.getPackageName()),
				context.getResources().getIdentifier("autotrendlinear06", "id", context.getPackageName())};
		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 16 자동추세창 레이아웃 크기 및 위치 조절 : T_tab10
		int layoutResId;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			layoutResId = context.getResources().getIdentifier("chart_autotrend_tab", "layout", context.getPackageName());
		}
		else
		{
			layoutResId = context.getResources().getIdentifier("chart_autotrend", "layout", context.getPackageName());
		}
		autotrendView = (LinearLayout)factory.inflate(layoutResId, null);
		//2012. 7. 25 뷰를 add 하기전에 setlayoutparam 안해주면 원하는 전체 레이아웃을 얻을 수 없음. 
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		//2020.04.21 by JJH >> 자동추세 설정 팝업 UI 수정 start
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(280), ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) COMUtil.getPixel(320), ViewGroup.LayoutParams.WRAP_CONTENT);
		//2020.04.21 by JJH >> 자동추세 설정 팝업 UI 수정 end
		autotrendView.setLayoutParams(params);
		//2012. 7. 24 자동추세설정창 에디트텍스트 레이아웃 설정 
		setUI();
		autotrendView.requestFocus();
		layout.addView(autotrendView);
		int sint;

		int resId = context.getResources().getIdentifier("initbtn", "id", context.getPackageName());
		Button btnRefresh = (Button) autotrendView.findViewById(resId);
		if (btnRefresh != null) {
			btnRefresh.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setDefault();
				}
			});
		}

		resId = context.getResources().getIdentifier("initTv", "id", context.getPackageName());
		TextView tvRefresh = (TextView) autotrendView.findViewById(resId);
		if (tvRefresh != null) {
			btnRefresh.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setDefault();
				}
			});
		}

//		resId = context.getResources().getIdentifier("public_checkbox01", "id", context.getPackageName());
//		final CheckBox autoChk_wave = (CheckBox) autotrendView.findViewById(resId);
//
//		resId = context.getResources().getIdentifier("public_checkbox02", "id", context.getPackageName());
//		final CheckBox autoChk_high = (CheckBox) autotrendView.findViewById(resId);
//
//		resId = context.getResources().getIdentifier("public_checkbox03", "id", context.getPackageName());
//		final CheckBox autoChk_low = (CheckBox) autotrendView.findViewById(resId);
//
//		resId = context.getResources().getIdentifier("public_checkbox06", "id", context.getPackageName());
//		CheckBox autoChk_w = (CheckBox) autotrendView.findViewById(resId);

		for(int i = 0 ; i <  ids.length  ; i++){
			CheckBox btnCheckBox = (CheckBox)autotrendView.findViewById(ids[i]);

			btnFuncs[i] = btnCheckBox;

			btnCheckBox.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					CheckBox chk = (CheckBox) v;
					for (int i = 0 ; i < 4 ; i++)
					{
						if(btnFuncs[i] != chk)
						{

							CheckBox tmp = btnFuncs[i];

							tmp.setChecked(false);
						}
					}
				}
			});

		};



		//2012. 8. 22 태블릿에서 자동추세창 기본값버튼 위로 올리고 아랫쪽에 적용버튼 추가 : T_tab26
		//2012. 8. 22  폰에서도 태블릿처럼 적용버튼 추가 : T61
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//		{
		resId = context.getResources().getIdentifier("acceptbtn", "id", context.getPackageName());
		Button btnAccept = (Button) autotrendView.findViewById(resId);

		final int[] ids_final = ids;

		//2019. 08. 21 by hyh - 자동추세선 복원
		final ChartViewModel _cvm = (ChartViewModel) COMUtil._mainFrame.mainBase.baseP._chart._cvm;

		for(int i = 0; i < 4; i++) {
			CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids[i]);

			//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
//				stateDrawable = imageChange(normal, press);
//				btnFunc.setButtonDrawable(stateDrawable);

			if(i==0)
				sint = _cvm.autoTrendWaveType;
			else if(i==1)
				sint = _cvm.autoTrendHighType;
			else if(i==2)
				sint = _cvm.autoTrendLowType;
			else
				sint = _cvm.autoTrendWType;

			if(sint == 1){
				btnFunc.setChecked(true);
			} else {
				btnFunc.setChecked(false);
			}

			btnFuncs[i] = btnFunc;
			btnFunc.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					_cvm.preName = Integer.parseInt(preNameField.getText().toString());
					_cvm.endName = Integer.parseInt(endNameField.getText().toString());


					for(int i = 0; i < 4; i++) {
						CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids_final[i]);

						if(i==0)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendWaveType = 1;
							}
							else
							{
//								COMUtil.autoTrendWaveType = -1;
							}

						}

						else if(i==1)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendHighType = 1;
							}
							else
							{
//								COMUtil.autoTrendHighType = -1;
							}
						}
						else if(i==2)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendLowType = 1;
							}
							else
							{
//								COMUtil.autoTrendLowType = -1;
							}
						}
						else if(i==3)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendLowType = 1;
							}
							else
							{
//								COMUtil.autoTrendLowType = -1;
							}
						}
					}

//					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
				
				
			});
		}
		
		for(int i = 0; i < 4; i++) {
			LinearLayout ly = (LinearLayout) autotrendView.findViewById(ids1[i]);
			final CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids[i]);
			//2012. 10. 2  체크버튼 이미지 32*32 사용하여 세팅  : I106
//				stateDrawable = imageChange(normal, press);
//				btnFunc.setButtonDrawable(stateDrawable);

			if(i==0)
				sint = _cvm.autoTrendWaveType;
			else if(i==1)
				sint = _cvm.autoTrendHighType;
			else if(i==2)
				sint = _cvm.autoTrendLowType;
			else
				sint = _cvm.autoTrendWType;

			if(sint == 1){
				btnFunc.setChecked(true);
			} else {
				btnFunc.setChecked(false);
			}

			 lys[i] =  ly;
			 ly.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					_cvm.preName = Integer.parseInt(preNameField.getText().toString());
					_cvm.endName = Integer.parseInt(endNameField.getText().toString());
					btnFunc.setChecked(!btnFunc.isChecked());

					for(int i = 0; i < 4; i++) {
						CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids_final[i]);

						if(i==0)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendWaveType = 1;
							}
							else
							{
//								COMUtil.autoTrendWaveType = -1;
							}

						}

						else if(i==1)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendHighType = 1;
							}
							else
							{
//								COMUtil.autoTrendHighType = -1;
							}
						}
						else if(i==2)
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendLowType = 1;
							}
							else
							{
//								COMUtil.autoTrendLowType = -1;
							}
						}
						else
						{
							if(btnFunc.isChecked())
							{
//								COMUtil.autoTrendWType = 1;

							}
							else
							{
//								COMUtil.autoTrendWType = -1;
							}
							isWtypeCheck();
						}
					}

//					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
				
				
			});
		}


		resId = context.getResources().getIdentifier("public_checkbox06", "id", context.getPackageName());
		final CheckBox autoChk_w = (CheckBox) autotrendView.findViewById(resId);
		autoChk_w.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isWtypeCheck();
				}
		});





		btnAccept.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_cvm.preName = Integer.parseInt(preNameField.getText().toString());
				_cvm.endName = Integer.parseInt(endNameField.getText().toString());


				for(int i = 0; i < 4; i++) {
					CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids_final[i]);

					if(i==0)
					{
						if(btnFunc.isChecked())
						{
							_cvm.autoTrendWaveType = 1;
						}
						else
						{
							_cvm.autoTrendWaveType = -1;
						}

					}

					else if(i==1)
					{
						if(btnFunc.isChecked())
						{
							_cvm.autoTrendHighType = 1;
						}
						else
						{
							_cvm.autoTrendHighType = -1;
						}
					}
					else if(i==2)
					{
						if(btnFunc.isChecked())
						{
							_cvm.autoTrendLowType = 1;
						}
						else
						{
							_cvm.autoTrendLowType = -1;
						}
					}
					else
					{
						if(btnFunc.isChecked())
						{
							_cvm.autoTrendWType = 1;

						}
						else
						{
							_cvm.autoTrendWType = -1;
						}
					}
				}

				COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				((Base11)COMUtil._mainFrame.mainBase.baseP).autotrendPopup.dismiss();
				((Base11)COMUtil._mainFrame.mainBase.baseP).autotrendPopup = null;

				//2019. 08. 21 by hyh - 자동추세선 복원. 적용 시 바로 저장
				COMUtil.saveLastState(COMUtil._mainFrame.strFileName);
			}
		});
//		}

		//2012. 8. 22 태블릿에서 자동추세창 기본값버튼 위로 올리고 아랫쪽에 적용버튼 추가로 아래의 체크버튼 눌릴때마다 동작하는 리스너코드 수정 : T_tab26
//		else
//		{
//			for(int i = 0; i < 3; i++) {
//				CheckBox btnFunc = (CheckBox) autotrendView.findViewById(ids[i]);
//
//		        if(i==0)
//		            sint = COMUtil.autoTrendWaveType;
//		        else if(i==1)
//		            sint = COMUtil.autoTrendHighType;
//		        else
//		            sint = COMUtil.autoTrendLowType;
//		        
//				if(sint == 1){
//					btnFunc.setChecked(true);
//				} else {
//					btnFunc.setChecked(false);
//				}
//
//				btnFuncs[i] = btnFunc;
//				
//				btnFunc.setOnClickListener(new Button.OnClickListener() {
//		        	public void onClick(View v) {
//
//		        		int tag = Integer.parseInt((String) v.getTag());
//		        		CheckBox btn = (CheckBox) v;        		
//		        		if( tag == 2) {
//		        			if(btn.isChecked() == true) {
//		        				COMUtil.autoTrendHighType = 1;	
//
//		        			} else {
//		        				COMUtil.autoTrendHighType = -1;	
//		        			}
//		        		} else if(tag == 3) {
//		        			if(btn.isChecked() == true) {
//		        				COMUtil.autoTrendLowType = 1;
//		        			} else {
//		        				COMUtil.autoTrendLowType = -1;
//		        			}
//		        		} else if(tag  == 1) {
//		        			if(btn.isChecked() == true) {
//		        				COMUtil.autoTrendWaveType = 1;
//		        			} else {
//		        				COMUtil.autoTrendWaveType = -1;
//		        			}
//		        		}
//		        		
//		        		else
//		        			return;
//		        		
////		        		for(int i = 0; i < 4; i++) {
////		        			if(COMUtil.autoTrendHighType != 1 && COMUtil.autoTrendLowType != 1 && COMUtil.autoTrendWaveType != 1) {
////		        				btnFuncs[i].setChecked(false);
////		        			}
////		        		}
//						//2011.10.07 by LYH >> 자동추세선 분할차트 모두 적용 <<
//		        		//COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
//		        		COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
//		        		
//			        }
//		        });
//			}
//		}

		isWtypeCheck();
	}

	//2012. 7. 24  자동추세선 설정창 EditText 설정하는 함수 
	public void setUI()
	{
		//2019. 08. 21 by hyh - 자동추세선 복원
		final ChartViewModel _cvm = (ChartViewModel) COMUtil._mainFrame.mainBase.baseP._chart._cvm;

		//2012. 7. 26  API모드 대비하여 resource 읽어오는 방식 변경
		int layoutResId = context.getResources().getIdentifier("prenamefield", "id", context.getPackageName());
		preNameField = (ExEditText)autotrendView.findViewById(layoutResId);
		//2012. 7. 30  전봉값을 초기값으로 입력
		preNameField.setText(String.valueOf(_cvm.preName));
		preNameField.setOnBackButtonListener(this);
		preNameField.setTypeface(COMUtil.numericTypefaceMid);

		preNameField.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
//				System.out.println("prename actionid = " + actionId);
				if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
				{
					hideKeyPad(preNameField);
					if(Integer.parseInt(preNameField.getText().toString()) >= 0)
					{
						// 빈칸이면 초기값으로
						if(preNameField.getText().toString().equals(""))
						{
							preNameField.setText("3");
						}

						_cvm.preName = Integer.parseInt(preNameField.getText().toString());
						COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
					}

				}
				return true;
			}
		});

		//2012. 8. 22 자동추세창 전봉, 후봉 적용할 때  edittext에서 포커스를 잃어도 작동되게 수정 : T_tab25
		preNameField.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus)
				{
					// 빈칸이면 초기값으로 
					if(preNameField.getText().toString().equals(""))
					{
						preNameField.setText("3");
					}

					_cvm.preName = Integer.parseInt(preNameField.getText().toString());
					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
			}
		});


		//2012. 7. 26  API모드 대비하여 resource 읽어오는 방식 변경
		layoutResId = context.getResources().getIdentifier("endnamefield", "id", context.getPackageName());
		endNameField = (ExEditText)autotrendView.findViewById(layoutResId);
		//2012. 7. 30  후봉값을 초기값으로 입력
		endNameField.setText(String.valueOf(_cvm.endName));
		endNameField.setOnBackButtonListener(this);
		endNameField.setTypeface(COMUtil.numericTypefaceMid);

		endNameField.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
//				System.out.println("endname actionid = " + actionId);
				if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
				{
					hideKeyPad(endNameField);
					if(Integer.parseInt(endNameField.getText().toString()) >= 0)
					{
						// 빈칸이면 초기값으로
						if(endNameField.getText().toString().equals(""))
						{
							endNameField.setText("3");
						}
						_cvm.endName = Integer.parseInt(endNameField.getText().toString());
						COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
					}
				}
				return true;
			}
		});

		//2012. 8. 22 자동추세창 전봉, 후봉 적용할 때  edittext에서 포커스를 잃어도 작동되게 수정 : T_tab25
		endNameField.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus)
				{

					// 빈칸이면 초기값으로 
					if(endNameField.getText().toString().equals(""))
					{
						endNameField.setText("3");
					}

					_cvm.endName = Integer.parseInt(endNameField.getText().toString());
					COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
				}
			}
		});
//    	
//    	layoutResId = context.getResources().getIdentifier("autotrend_bglayout", "id", context.getPackageName());
//    	LinearLayout autotrendbg_layout = (LinearLayout)autotrendView.findViewById(layoutResId);
//    	autotrendbg_layout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				System.out.println("autotrendbg clicked");
//			}
//		});
	}

	//문자 분석툴 사용시 키패드 감추기 위함 
	public void hideKeyPad(EditText edText)
	{
		InputMethodManager imm = (InputMethodManager) COMUtil.apiLayout.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edText.getWindowToken(), 0);
		edText.clearFocus();
	}

	/*
	 * - (void)setDefault:(id)sender { 
    for(int i=0; i<[self.menuItemIndex count]; i++) {
		int sinx = [[self.menuItemIndex objectAtIndex:i] intValue];
		UISwitch *uiswitch = (UISwitch*)[self.view viewWithTag:sinx];
        uiswitch.on = NO;
    }
    [[CommonConst sharedSingleton] setAutoTrendWaveType:0];
    [[CommonConst sharedSingleton] setAutoTrendHighType:0];
    [[CommonConst sharedSingleton] setAutoTrendLowType:0];
    [[CommonConst sharedSingleton] setPreName:@"3"];
    [[CommonConst sharedSingleton] setEndName:@"3"];
    preNameField.text = @"3";
    endNameField.text = @"3";
    [delegate setAutoTrendLine:sender];
}

	 */
	private void setDefault() {
		for(int i=0; i<4; i++) {
			btnFuncs[i].setChecked(false);
			btnFuncs[i].setEnabled(true);
		}

		//2019. 08. 21 by hyh - 자동추세선 복원
		final ChartViewModel _cvm = (ChartViewModel)COMUtil._mainFrame.mainBase.baseP._chart._cvm;

		_cvm.autoTrendWaveType=0;
		_cvm.autoTrendHighType=0;
		_cvm.autoTrendLowType=0;
		_cvm.autoTrendWType=0;
		_cvm.preName = 3;
		_cvm.endName = 3;

		preNameField.setText("3");
		endNameField.setText("3");
		COMUtil._mainFrame.mainBase.baseP.repaintAllChart();
	}

	//2012. 10. 2  체크박스 이미지 변경  : I106
	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
		StateListDrawable imageDraw = new StateListDrawable();
		imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
		imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
		return imageDraw;
	}


	public void onBackButtonClick(EditText ed)
	{
		// 빈칸이면 초기값으로 
		if(ed.getText().toString().equals(""))
		{
			ed.setText("3");
		}
		hideKeyPad(ed);
		autotrendView.requestFocus();
	}

	private void isWtypeCheck()
	{
		int resId = context.getResources().getIdentifier("public_checkbox06", "id", context.getPackageName());
		final CheckBox autoChk_w = (CheckBox) autotrendView.findViewById(resId);
		if(autoChk_w.isChecked()){
			for (int i = 0 ; i < 3 ; i++)
			{
//				btnFuncs[i].setChecked(false);
				btnFuncs[i].setEnabled(false);
				lys[i].setEnabled(false);
			}
		}
		else
		{
			for (int i = 0 ; i < 3 ; i++)
			{
				btnFuncs[i].setEnabled(true);
				lys[i].setEnabled(true);
			}
		}
	}
}