package drfn.chart.comp;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import drfn.chart.NeoChart2;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.CoSys;
import drfn.chart.util.CodeItemObj;

public class ChartItemView extends View {
	TextView labelName;
	TextView presentVal;
	TextView changeVal;
	TextView volumeVal;
	TextView labelUnit;
	Button btnExtend;

	NeoChart2 baseChart;
	ImageView backView;
	public RelativeLayout layout;
	LinearLayout extendButtonLayout = null;

	//2011.08.05 by LYH >> 시세바 데이터 처리 이벤트 <<
	Handler mHandler_codeItem;
	private Context context = null;

	int extendXmargine=3;
	int labelUnitXmargine=26;

	public ChartItemView(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			extendXmargine=25;
			labelUnitXmargine=25;
		}
	}

	public void setBaseChart(NeoChart2 chart) {
		baseChart = chart;
	}

	public void setBasicUI() {
		initUI();
	}

	public void initUI() {
		Button signalBtn = new Button(context);
		signalBtn.setId(4999);
		int layoutResId = context.getResources().getIdentifier("alarm_shin", "drawable", context.getPackageName());
		signalBtn.setBackgroundResource(layoutResId);
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)COMUtil.getPixel(23), (int)COMUtil.getPixel(23));
		//2012. 8. 10  상수들 getPixel 화 : C12
		params.leftMargin=(int)COMUtil.getPixel(1);
		params.topMargin=0;
		signalBtn.setLayoutParams(params);

		signalBtn.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
//	    		selectMenu(v);	
			}
		});

		signalBtn.setOnTouchListener(
				new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						switch(event.getAction()) {
							case MotionEvent.ACTION_DOWN:
//								 v.setBackgroundResource(menuImage[Integer.parseInt((String)v.getTag())]);

								break;
							case MotionEvent.ACTION_UP:
//								 v.setBackgroundResource(menuImage_dn[Integer.parseInt((String)v.getTag())]);
								break;

						}
						return false;
					}
				}
		);

//		layout.addView(signalBtn);

		//int tWidth = this.labelName.getWidth();
		//2012. 8. 10  상수들 getPixel 화 : C12
		//int nY = (int)COMUtil.getPixel(2);
		int nY = (int)COMUtil.getPixel(0);
		int nHeight = (int)COMUtil.getPixel(18); // 차트위쪽 종목정보 높이 .   기존 25
		int nX = (int)COMUtil.getPixel(5);
		int nWidth;
		int nFontSize = 10;

		nWidth = (int)COMUtil.getPixel(100);
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		labelName = new TextView(context);
		labelName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		labelName.setBackgroundColor(Color.TRANSPARENT);
		labelName.setTextColor(Color.WHITE);
		labelName.setTextSize(nFontSize);
		labelName.setSingleLine(false);
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		labelName.setLayoutParams(params);
		layout.addView(labelName);

		nX += nWidth;
		nWidth = (int)COMUtil.getPixel(80);
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		presentVal = new TextView(context);
		presentVal.setBackgroundColor(Color.TRANSPARENT);
		presentVal.setTextSize(nFontSize);
		presentVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		presentVal.setLayoutParams(params);
		layout.addView(presentVal);
		if(nX > layout.getLayoutParams().width-nWidth)
			presentVal.setVisibility(View.GONE);

		nX += nWidth;
		nWidth = (int)COMUtil.getPixel(90);
		//2013. 6. 14 태블릿에서 시세바 거래량 위치 오른쪽으로 조금 이동 >>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			nWidth += (int)COMUtil.getPixel(3);
		}
		//2013. 6. 14 태블릿에서 시세바 거래량 위치 오른쪽으로 조금 이동 <<
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		changeVal = new TextView(context);
		changeVal.setBackgroundColor(Color.TRANSPARENT);
		changeVal.setTextSize(nFontSize);
		changeVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		changeVal.setLayoutParams(params);
		layout.addView(changeVal);
		if(nX > layout.getLayoutParams().width-nWidth)
			changeVal.setVisibility(View.GONE);

		nX += nWidth;
		//2013. 6. 14 태블릿에서 시세바 거래량 위치 오른쪽으로 조금 이동 >>
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			nX += (int)COMUtil.getPixel(5);
		}
		//2013. 6. 14 태블릿에서 시세바 거래량 위치 오른쪽으로 조금 이동 <<
		nWidth = (int)COMUtil.getPixel(70);
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		volumeVal = new TextView(context);
		volumeVal.setBackgroundColor(Color.TRANSPARENT);
		volumeVal.setTextSize(nFontSize);
		volumeVal.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		volumeVal.setTextColor(Color.WHITE);
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		volumeVal.setLayoutParams(params);
		layout.addView(volumeVal);
		if(nX > layout.getLayoutParams().width-nWidth-(int)COMUtil.getPixel(10))
			volumeVal.setVisibility(View.GONE);

		RelativeLayout.LayoutParams bounds = (RelativeLayout.LayoutParams)this.getLayoutParams();

		nWidth = (int)COMUtil.getPixel(17);
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
			nWidth = (int)COMUtil.getPixel(25);
			nHeight = (int)COMUtil.getPixel(20);
		}
		//2012. 11. 27  확대/축소 버튼 크기 조절 : C37
		//2012. 11. 27  확대/축소 버튼 크기 조절 : C37
		else
		{
			nWidth = (int)COMUtil.getPixel(19);
			nHeight = (int)COMUtil.getPixel(19);
		}

		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);

		nX = bounds.width -(int)COMUtil.getPixel(17) - (int)COMUtil.getPixel(extendXmargine);
//		nX = bounds.width -nWidth - (int)COMUtil.getPixel(extendXmargine);
//		btnExtend = new Button(context);
//		btnExtend.setTag("btnExtend");
//		layoutResId = context.getResources().getIdentifier("chart_full_reduce_change", "drawable", context.getPackageName());
//		btnExtend.setBackgroundResource(layoutResId);

//		btnExtend.setWidth((int)COMUtil.getPixel(24));
//		btnExtend.setHeight((int)COMUtil.getPixel(24));
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		extendButtonLayout = new LinearLayout(this.getContext());
		extendButtonLayout.setLayoutParams(params);
		extendButtonLayout.setGravity(Gravity.CENTER);

		extendButtonLayout.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonClicked(v);
			}
		});

		//btnExtend.setLayoutParams(params);
		btnExtend = new Button(context);
		btnExtend.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				buttonClicked(v);

			}
		});

		btnExtend.setTag("btnExtend");
		layoutResId = context.getResources().getIdentifier("chart_full_reduce_change", "drawable", context.getPackageName());
		btnExtend.setBackgroundResource(layoutResId);

//    }
		nWidth = (int)COMUtil.getPixel(15);
		nHeight = (int)COMUtil.getPixel(15);
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		btnExtend.setLayoutParams(params);

		extendButtonLayout.setGravity(Gravity.CENTER);
		extendButtonLayout.addView(btnExtend);

		layout.addView(extendButtonLayout);

		nWidth = (int)COMUtil.getPixel(25);
		nHeight = (int)COMUtil.getPixel(19);
		params =new RelativeLayout.LayoutParams(
				nWidth, nHeight);
		nX = nX - (int)COMUtil.getPixel(labelUnitXmargine) ;
		labelUnit = new TextView(context);
		labelUnit.setBackgroundColor(Color.TRANSPARENT);
		labelUnit.setTextSize(nFontSize);
		labelUnit.setTextColor(Color.WHITE);
		labelUnit.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		params.width=nWidth;
		params.height=nHeight;
		params.leftMargin=nX;
		params.topMargin=nY;
		labelUnit.setLayoutParams(params);
		labelUnit.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				buttonClicked(btnExtend);

			}
		});
		layout.addView(labelUnit);

		//2011.08.05 by LYH >> 시세바 데이터 처리 이벤트 
		mHandler_codeItem = new Handler() {
			@Override public void handleMessage(Message msg) {
				CodeItemObj codeItem = (CodeItemObj)msg.obj;
				int nDataType = msg.arg1;

				String signStr = codeItem.strSign;

				int sign = 0;
				try {
					sign = Integer.parseInt(signStr);
				} catch(Exception e) {

				}

				int color;
				if(sign<3) {
					color = CoSys.UP_LINE_COLOR;
					if(sign==1)
						signStr = "↑";
					else
						signStr = "▲";
				} else if(sign>3) {
					color = CoSys.DOWN_LINE_COLOR;
					if(sign==4)
                        signStr = "↓";
					else
                        signStr = "▼";
				} else {
					//2013.10.08 by LYH >> 보합색상 검정색으로 변경.
					//color = CoSys.CHART_COLOR[2];
					if(baseChart._cvm.getSkinType()==COMUtil.SKIN_BLACK)
					{
						color = Color.WHITE;
					}
					else
					{
						color = Color.BLACK;
					}
					//2013.10.08 by LYH <<
					signStr = "";
				}


				presentVal.setTextColor(color);
				//presentVal.setText(COMUtil.format(codeItem.strPrice,0,3));
				//2012. 11. 28 현재가 - 붙어서 올때 처리 : C38
//    			presentVal.setText(ChartUtil.getFormatedData(codeItem.strPrice,baseChart._cdm.getPriceFormat()));
				String strPrice = ChartUtil.getFormatedData(codeItem.strPrice,baseChart._cdm.getPriceFormat());
				if(strPrice.startsWith("-"))
				{
					strPrice = strPrice.substring(1);
				}
				presentVal.setText(strPrice);

				//2012. 11. 28 대비, 등락률  - 붙어서 올때 처리 : C38
				//String strChange = codeItem.strChange, strChgrate = codeItem.strChgrate;
				//2012.11.30 by LYH >> 전일대비 소수점 처리 개선.
				String strChange = ChartUtil.getFormatedData(codeItem.strChange,baseChart._cdm.getPriceFormat());
				String strChgrate = codeItem.strChgrate;
				//2012.11.30 by LYH <<
				if(strChange.startsWith("-"))
				{
					strChange = strChange.substring(1);
				}
//				if(strChgrate.startsWith("-"))
//				{
//					strChgrate = strChgrate.substring(1);
//				}
//    			String changeStr = signStr + codeItem.strChange + "(" + codeItem.strChgrate + ")";
				String changeStr = signStr + strChange + "(" + strChgrate + ")";
				changeVal.setTextColor(color);
				changeVal.setText(changeStr);

				volumeVal.setText(COMUtil.format(codeItem.strVolume,0,3));

//    			//2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 >>
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					if(baseChart != null && baseChart.isChartItemViewHidden())
					{
						presentVal.setVisibility(View.GONE);
						changeVal.setVisibility(View.GONE);
						volumeVal.setVisibility(View.GONE);
					}
				}
//    	    	//2013. 6. 13 ELW-ETF타입일 때 차트상단 종목정보 (ChartItemView) 미표시 처리 <<

				if(nDataType == 0)
				{
//    				System.out.println("codeItem.strName:"+codeItem.strName);
//					if(codeItem.strName.length() > 15) {
//						labelName.setTextSize(8);
//					}
//					else if(codeItem.strName.length() > 10) {
//						labelName.setTextSize(9);
//					} else {
//						labelName.setTextSize(12);
//					}
					labelName.setTextSize(10);
					labelName.setText(codeItem.strName);
					//String ptype = COMUtil.getPeriod();
					String ptype = COMUtil.getPeriod(codeItem.strDataType, codeItem.strUnit);
					if(ptype!=null) {
						labelUnit.setText(ptype);
						//nWidth = 40;
//        				float textW =12;
//        				textW = COMUtil.getMeasureText(ptype);
//        				fparams.leftMargin=fnX - (int)textW;
					}
				}
			}
		};
		//2011.08.05 by LYH <<

		COMUtil.setGlobalFont(layout);
	}
	Handler[] mHandlers = new Handler[5];
	Handler[] mHandlersColor = new Handler[5];
	Handler mHandlerColor;
	TextView labels[] = new TextView[5];
	public void initCompareUI() {
		int nY = (int)COMUtil.getPixel(2);
		int nHeight = (int)COMUtil.getPixel(17); // 차트위쪽 종목정보 높이 .   기존 25 
		int nX = (int)COMUtil.getPixel(10);
		int nWidth = (int)COMUtil.getPixel(300);
		//int nFontSize = 12;
//		int nFontSize = 13;
		for(int i=0;i<5; i++) {
			final TextView label = new TextView(this.getContext());
//			label.setId(2000+i);
			label.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
					nWidth, nHeight);
			params.leftMargin=nX;
			params.topMargin=nY;
			label.setLayoutParams(params);
			this.layout.addView(label);
//			labels[i] = label;

			Handler mHandler = new Handler() {
				@Override public void handleMessage(Message msg) {
					String text = (String)msg.obj;
					label.setText(text);
				}
			};
			mHandlers[i] = mHandler;

			mHandlerColor = new Handler() {
				@Override public void handleMessage(Message msg) {
					String text = (String)msg.obj;
					label.setTextColor(Integer.parseInt(text));
				}
			};
			mHandlersColor[i] = mHandlerColor;

			nY += nHeight;
		}


	}

	public void setData(String strName, final int nIndex) {
		if(nIndex == -1) {
			for(int i=0; i<5; i++) {
//				final TextView label = (TextView)this.layout.findViewById(2000+i);
//				final TextView label = (TextView)labels[i];
				Message msg = new Message();
				String textTochange = "";
				msg.obj = textTochange;
				mHandlers[i].sendMessage(msg);

			}
			return;
		}
		Message msg = new Message();
		//String textTochange = ""+CoSys.CHART_COLOR[nIndex+4];
		//String textTochange = ""+Color.rgb(CoSys.CHART_COLORS[nIndex+4][0], CoSys.CHART_COLORS[nIndex+4][1], CoSys.CHART_COLORS[nIndex+4][2]);
		//2016. 1. 29 by hyh - 비교차트 컬러 수정
	    String textTochange = ""+Color.rgb(CoSys.COMPARE_CHART_COLORS[nIndex+1][0], CoSys.COMPARE_CHART_COLORS[nIndex+1][1], CoSys.COMPARE_CHART_COLORS[nIndex+1][2]);
		msg.obj = textTochange;
		mHandlersColor[nIndex].sendMessage(msg);

		final String data = "■ "+strName;
		msg = new Message();
		textTochange = data;
		msg.obj = textTochange;
		mHandlers[nIndex].sendMessage(msg);
	}

	public void buttonClicked(View v) {
		if(baseChart != null) {
			btnExtend.setSelected(!btnExtend.isSelected());

			if(btnExtend.isSelected()) {
				//set extendChart
				COMUtil._mainFrame.mainBase.extendChart(baseChart);
			} else {
				//set reduceChart
				COMUtil._mainFrame.mainBase.reduceChart(baseChart);
			}
		}
	}

	//	public void setFrame(RelativeLayout.LayoutParams params) {
//		layout.setLayoutParams(params);
//		
//		int nWidth = 30;
//		int nX = layout.getLayoutParams().width-nWidth-10;
//		int nY = 2;
//		int nHeight = 25;
//		
//		params.width=nWidth;
//		params.height=nHeight;
//		params.leftMargin=nX;
//		params.topMargin=nY;
//		btnExtend.setLayoutParams(params);
//		
//		nWidth = 40;
//		nX = nX - nWidth - 10;
//		params.width = nWidth;
//		params.leftMargin=nX;
//		labelUnit.setLayoutParams(params);
//	}
	Vector<String> names = null;
	public void setCompareTitleList(Vector<String> names) {
		this.names = names;
	}
	//2011.08.05 by LYH >> 사용안함
//	public void setCompareTitle(GL10 gl) {
//		if(names==null) return;
//		int nX = 10;
//		int nY = 12;
//		int nHeight = 25;
//		for(int i=0; i<names.size(); i++) {
//			String nameStr = (String)this.names.get(i);
//			COMUtil.drawString(gl, CoSys.WHITE, nX, nY, nameStr);
//			nY += nHeight;
//		}
//	}
	//2011.08.05 by LYH <<
	public void setText(CodeItemObj codeItem, int nDataType, int type) {

		//2011.08.05 by LYH >> 시세바 데이터 처리 이벤트 <<
		Message msg = new Message();
		msg.obj = codeItem;
		msg.arg1 = nDataType;
		mHandler_codeItem.sendMessage(msg);
	}

	//2011.08.05 by LYH >> 사이즈 변경 시 확대 및 주기타이틀 위치 이동
	public void setFrame(int width) {
		int nWidth = (int)COMUtil.getPixel(17);
		int nX = width-nWidth-(int)COMUtil.getPixel(extendXmargine);
//		int nY = 2;
//		int nHeight = 25;
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)extendButtonLayout.getLayoutParams();
		params.leftMargin=nX;
		//params.topMargin=nY;
//		btnExtend.setLayoutParams(params);

		params = (RelativeLayout.LayoutParams)labelUnit.getLayoutParams();
		nX = nX - (int)COMUtil.getPixel(labelUnitXmargine);
		//params.width = nWidth;
		params.leftMargin=nX;
		//labelUnit.setLayoutParams(params);

		params = (RelativeLayout.LayoutParams)presentVal.getLayoutParams();
		if(params.leftMargin+params.width<width - (int)COMUtil.getPixel(10))
			presentVal.setVisibility(View.VISIBLE);
		else
			presentVal.setVisibility(View.GONE);

		params = (RelativeLayout.LayoutParams)changeVal.getLayoutParams();
		if(params.leftMargin+params.width<width)
			changeVal.setVisibility(View.VISIBLE);
		else
			changeVal.setVisibility(View.GONE);

		params = (RelativeLayout.LayoutParams)volumeVal.getLayoutParams();
		if(params.leftMargin+params.width<width-(int)COMUtil.getPixel(10))
			volumeVal.setVisibility(View.VISIBLE);
		else
			volumeVal.setVisibility(View.GONE);
	}
	//2011.08.05 by LYH <<
	public void setExtendButton()
	{
		btnExtend.setSelected(false);
		//btnExtend.setBackgroundResource(R.drawable.view_full);
	}

	public void setSkinType(int nSkinType)
	{
		if(nSkinType == COMUtil.SKIN_BLACK)
		{
			labelName.setTextColor(Color.WHITE);
			//volumeVal.setTextColor(Color.YELLOW);
			volumeVal.setTextColor(Color.WHITE);
			labelUnit.setTextColor(Color.WHITE);
			int layoutResId = context.getResources().getIdentifier("chart_full_reduce_change", "drawable", context.getPackageName());
			btnExtend.setBackgroundResource(layoutResId);

//	        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnExtend.getLayoutParams();
//				
//				params.width = (int)COMUtil.getPixel(30);
//				params.height = (int)COMUtil.getPixel(25);
//				params.topMargin = 0;
//				
//				btnExtend.setLayoutParams(params);
//	        }
		}
		else {
//	    	labelName.setTextColor(Color.BLACK);
//	    	volumeVal.setTextColor(Color.BLACK);
//	        labelUnit.setTextColor(Color.BLACK);
			labelName.setTextColor(Color.rgb(51,51,51));
			volumeVal.setTextColor(Color.rgb(51,51,51));
			labelUnit.setTextColor(Color.rgb(51,51,51));

			int layoutResId = context.getResources().getIdentifier("chart_full_reduce_white_change", "drawable", context.getPackageName());
			btnExtend.setBackgroundResource(layoutResId);

//	        if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) {
//				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnExtend.getLayoutParams();
//				
//				params.width = (int)COMUtil.getPixel(25);
//				params.height = (int)COMUtil.getPixel(20);
//				params.topMargin = (int)COMUtil.getPixel(1);
//
//				btnExtend.setLayoutParams(params);
//	        }
		}
	}
}
