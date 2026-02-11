package drfn.chart.base;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import drfn.chart.comp.DRBottomDialog;
import drfn.chart.draw.DrawTool;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

public class CandleControlSetUI extends JipyoControlSetUI{
//	AbstractGraph _graph;

//	RelativeLayout layout = null;
//	Handler mHandler=null;
//	private Context context = null;
	
	public TextView tvColorOpen, tvColorOpen2, tvColorOpen3, tvColorOpen4;
	
	//2012. 11. 2 최대최소, 로그 체크박스 추가  : I107
	public CheckBox chkYangsang, chkYangha, chkEumsang, chkEumha, chkMinMax, chkLog, chkInverse, chkGap;
	
	//2012. 9. 11 콤보박스용 컨트롤 추가 
	public PopupWindow comboCandlePopup;
	LinearLayout comboCandleLayout;
	LinearLayout btnComboBasePrice, btnComboSameColor;
	TextView tvComboBasePrice, tvComboSameColor;

	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
	LinearLayout m_selLinearLayout;
	public DRBottomDialog drBottom;
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end
	
	public CandleControlSetUI(Context context, RelativeLayout layout) {
		super(context, layout);
	}
	//View jipyoui=null;
	
	public void setUI() {
		
//		jipyoui 를 inflate 로 받아오지 않고 DetailJipyoController에서 addView 되어있
		//View 를 받아온다. 
		jipyoui = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);

		int layoutResId = context.getResources().getIdentifier("paramtv", "id", context.getPackageName());
		TextView paramtv = (TextView)jipyoui.findViewById(layoutResId);
		paramtv.setTypeface(COMUtil.typefaceMid);
		
		//양봉상승 팔레트 
		layoutResId = context.getResources().getIdentifier("candlecolor_yangsang", "id", context.getPackageName());
		tvColorOpen = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen);
			}
		});
		
		//양봉하락 팔레트
		layoutResId = this.context.getResources().getIdentifier("candlecolor_yangha", "id", this.context.getPackageName());
		tvColorOpen2 = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen2);

			}
		});
		
		//음봉상승 팔레트 
		layoutResId = this.context.getResources().getIdentifier("candlecolor_eumsang", "id", this.context.getPackageName());
		tvColorOpen3 = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen3);

			}
		});
		
		//음봉하락 팔레트 
		layoutResId = this.context.getResources().getIdentifier("candlecolor_eumha", "id", this.context.getPackageName());
		tvColorOpen4 = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen4);
			}
		});
		
		//2012. 9. 11 상승/하락기준 전봉   콤보박스 추가 
		comboCandleLayout = new LinearLayout(this.context);
		comboCandleLayout.setOrientation(LinearLayout.VERTICAL);
		String strAdapterDataName = "candle_baseprice";
		
		layoutResId = context.getResources().getIdentifier("candle_button_baseprice", "id", context.getPackageName());
	    btnComboBasePrice = (LinearLayout)jipyoui.findViewById(layoutResId);
	    layoutResId = context.getResources().getIdentifier("candle_button_baseprice_text", "id", context.getPackageName());
	    tvComboBasePrice = (TextView)btnComboBasePrice.findViewById(layoutResId);
		//2012. 11. 16 콤보박스 리스트뷰 위치등 세팅하는 setComboBox 함수 개선. : I108
		setComboBox(btnComboBasePrice, strAdapterDataName);
		
        //2012. 9. 11 시가=종가일경우 색상표시   콤보박스 추가 
		 strAdapterDataName = "candle_samecolor";
		
		layoutResId = this.context.getResources().getIdentifier("candle_button_samecolor", "id", this.context.getPackageName());
		btnComboSameColor = (LinearLayout)jipyoui.findViewById(layoutResId);
	    layoutResId = context.getResources().getIdentifier("candle_button_samecolor_text", "id", context.getPackageName());
	    tvComboSameColor = (TextView)btnComboSameColor.findViewById(layoutResId);
		//2012. 11. 16 콤보박스 리스트뷰 위치등 세팅하는 setComboBox 함수 개선. : I108
		setComboBox(btnComboSameColor, strAdapterDataName);
		
		//양봉상승 채우기 체크박스 
		layoutResId = this.context.getResources().getIdentifier("candle_yangsang_check", "id", this.context.getPackageName());
		chkYangsang = (CheckBox)jipyoui.findViewById(layoutResId);
		
		chkYangsang.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드 
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_yangsang_label", "id", this.context.getPackageName());
		TextView tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkYangsang.setChecked(!chkYangsang.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		
		//양봉하락 채우기 체크박스 
		layoutResId = this.context.getResources().getIdentifier("candle_yangha_check", "id", this.context.getPackageName());
		chkYangha = (CheckBox)jipyoui.findViewById(layoutResId);
		
		chkYangha.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드 
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_yangha_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkYangha.setChecked(!chkYangha.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		
		//음봉상승 채우기 체크박스 
		layoutResId = this.context.getResources().getIdentifier("candle_eumsang_check", "id", this.context.getPackageName());
		chkEumsang = (CheckBox)jipyoui.findViewById(layoutResId);
		
		chkEumsang.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드 
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_eumsang_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkEumsang.setChecked(!chkEumsang.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		
		//음봉하락 채우기 체크박스 
		layoutResId = this.context.getResources().getIdentifier("candle_eumha_check", "id", this.context.getPackageName());
		chkEumha = (CheckBox)jipyoui.findViewById(layoutResId);
		
		chkEumha.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드 
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_eumha_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkEumha.setChecked(!chkEumha.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		
		//2012. 11. 2 캔들 기준 최대값/최소값 표시  체크박스 : I107
		layoutResId = this.context.getResources().getIdentifier("candle_minmax_check", "id", this.context.getPackageName());
		chkMinMax = (CheckBox)jipyoui.findViewById(layoutResId);
		
		chkMinMax.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_minmax_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkMinMax.setChecked(!chkMinMax.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		
		//2012. 11. 2 로그차트  체크박스 : I107
		layoutResId = this.context.getResources().getIdentifier("candle_log_check", "id", this.context.getPackageName());
		chkLog = (CheckBox)jipyoui.findViewById(layoutResId);

		chkLog.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
        	}
		});
		
		layoutResId = this.context.getResources().getIdentifier("candle_log_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkLog.setChecked(!chkLog.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});

		//2016.09.29 by
		layoutResId = this.context.getResources().getIdentifier("candle_inverse_check", "id", this.context.getPackageName());
		chkInverse = (CheckBox)jipyoui.findViewById(layoutResId);

		chkInverse.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});

		layoutResId = this.context.getResources().getIdentifier("candle_inverse_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkInverse.setChecked(!chkInverse.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});

		//2016.12.14 by LYH >>갭보정 추가
		layoutResId = this.context.getResources().getIdentifier("candle_gap_check", "id", this.context.getPackageName());
		chkGap = (CheckBox)jipyoui.findViewById(layoutResId);

		chkGap.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});

		layoutResId = this.context.getResources().getIdentifier("candle_gap_label", "id", this.context.getPackageName());
		tv = (TextView)jipyoui.findViewById(layoutResId);

		tv.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//전일대비 종가 상승/하락 작동 코드
				chkGap.setChecked(!chkGap.isChecked());
				reSetJipyo();
				COMUtil._neoChart.repaintAll();
			}
		});
		//2016.12.14 by LYH >>갭보정 추가 end
	}
	
	public void resetUI() {
		DrawTool dt = _graph.getDrawTool().get(0);
		
		int[] getUpColor = dt.getUpColor();
//		tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//		((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//		tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
		setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
		
		int[] getUpColor2 = dt.getUpColor2();
//		tvColorOpen2.setBackgroundColor(Color.rgb(getUpColor2[0], getUpColor2[1], getUpColor2[2]));
//		((GradientDrawable)tvColorOpen2.getBackground()).setColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//		tvColorOpen2.setTag(""+Color.rgb(getUpColor2[0], getUpColor2[1], getUpColor2[2]));
		setButtonColorWithRound(tvColorOpen2, Color.rgb(getUpColor2[0], getUpColor2[1], getUpColor2[2]));
		
		int[] getDownColor2 = dt.getDownColor2();
//		tvColorOpen3.setBackgroundColor(Color.rgb(getDownColor2[0], getDownColor2[1], getDownColor2[2]));
//		((GradientDrawable)tvColorOpen3.getBackground()).setColor(Color.rgb(getDownColor2[0], getDownColor2[1], getDownColor2[2]));
//		tvColorOpen3.setTag(""+Color.rgb(getDownColor2[0], getDownColor2[1], getDownColor2[2]));
		setButtonColorWithRound(tvColorOpen3, Color.rgb(getDownColor2[0], getDownColor2[1], getDownColor2[2]));
		
		int[] getDownColor = dt.getDownColor();
//		tvColorOpen4.setBackgroundColor(Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
//		((GradientDrawable)tvColorOpen4.getBackground()).setColor(Color.rgb(getDownColor2[0], getDownColor2[1], getDownColor2[2]));
//		tvColorOpen4.setTag(""+Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
		setButtonColorWithRound(tvColorOpen4, Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
		
		boolean bIsCheck;
		
		bIsCheck = dt.isFillUp();
		chkYangsang.setChecked(bIsCheck);
		
		bIsCheck = dt.isFillUp2();
		chkYangha.setChecked(bIsCheck);
		
		bIsCheck = dt.isFillDown2();
		chkEumsang.setChecked(bIsCheck);
		
		bIsCheck = dt.isFillDown();
		chkEumha.setChecked(bIsCheck);
		
		//2012. 11. 2  최대/최소값 , 로그차트 체크박스 세팅  : I107
		bIsCheck = _graph._cvm.getIsCandleMinMax();
		chkMinMax.setChecked(bIsCheck);
		
		bIsCheck = _graph._cvm.getIsLog();
		chkLog.setChecked(bIsCheck);

		bIsCheck = _graph._cvm.getIsInverse();
		chkInverse.setChecked(bIsCheck);

		//2016.12.14 by LYH >>갭보정 추가
		bIsCheck = _graph._cvm.getIsGapRevision();
		chkGap.setChecked(bIsCheck);
		//2016.12.14 by LYH >>갭보정 추가 end
		
		//인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서 arrayadapter 를 만든다 
      	int layoutResId = this.context.getResources().getIdentifier("candle_baseprice", "array", this.context.getPackageName());
  		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
  		tvComboBasePrice.setText(String.valueOf(comboAdapter.getItem(_graph._cvm.getCandle_basePrice())));

		layoutResId = this.context.getResources().getIdentifier("candle_samecolor", "array", this.context.getPackageName());
		comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
		tvComboSameColor.setText(String.valueOf(comboAdapter.getItem(_graph._cvm.getCandle_sameColorType())));
	}
	
	public void reSetOriginal() {
		//2012. 9. 14  팔레트 초기화
//		tvColorOpen.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
//		((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
//		tvColorOpen.setTag(""+Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		setButtonColorWithRound(tvColorOpen, Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		
//		tvColorOpen2.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
//		((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
//		tvColorOpen2.setTag(""+Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		setButtonColorWithRound(tvColorOpen2, Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		
//		tvColorOpen3.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
//		((GradientDrawable)tvColorOpen3.getBackground()).setColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
//		tvColorOpen3.setTag(""+Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
		setButtonColorWithRound(tvColorOpen3, Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
		
//		tvColorOpen4.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
//		((GradientDrawable)tvColorOpen3.getBackground()).setColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
//		tvColorOpen4.setTag(""+Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
		setButtonColorWithRound(tvColorOpen4, Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
		
		//2012. 9. 12  콤보박스 초기화
		//2012. 9. 14 arrays.xml 의 배열을 참조하여 초기화하도록 변경 
		int layoutResId = this.context.getResources().getIdentifier("candle_baseprice", "array", this.context.getPackageName());
		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
		tvComboBasePrice.setText(String.valueOf(comboAdapter.getItem(1)));
		
		layoutResId = this.context.getResources().getIdentifier("candle_samecolor", "array", this.context.getPackageName());
		comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
		tvComboSameColor.setText(String.valueOf(comboAdapter.getItem(1)));
		//_graph._cvm.setCandle_sameColorType(0);
		
		//2012. 9. 14  체크박스 초기화
		chkYangsang.setChecked(true);
		
		chkYangha.setChecked(true);
		
		chkEumsang.setChecked(true);
		
		chkEumha.setChecked(true);
		
		//2012. 11. 2 최대최소, 로그  초기화  : I107
		chkMinMax.setChecked(false);
	
		chkLog.setChecked(false);

		chkInverse.setChecked(false);

		chkGap.setChecked(false);   //2016.12.14 by LYH >>갭보정 추가
	}
	
	public void reSetJipyo() {
		//2012. 11. 2 체크박스가 최대최소, 로그   2개 늘어나서 전달된 데이터도 2개 늘어남 : I107
		int[] tmp = new int[24];
        int cont=0;
        
        tmp[cont++] = 0;
        tmp[cont++] = 0;
        //양봉상승
		String a = (String)tvColorOpen.getTag();
		int color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		//양봉하락 
		a = (String)tvColorOpen2.getTag();
		color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		//음봉상승 
		a = (String)tvColorOpen3.getTag();
		color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		//음봉하락 
		a = (String)tvColorOpen4.getTag();
		color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		//체크박스 4개 값 
		tmp[cont++] = chkYangsang.isChecked()?1:0;
		tmp[cont++] = chkYangha.isChecked()?1:0;
		tmp[cont++] = chkEumsang.isChecked()?1:0;
		tmp[cont++] = chkEumha.isChecked()?1:0;
		
		
		//콤보박스 2개 값
		int layoutResId = this.context.getResources().getIdentifier("candle_baseprice", "array", this.context.getPackageName());
		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
		for(int i = 0; i < comboAdapter.getCount(); i++)
		{
			if(tvComboBasePrice.getText().toString().equals(comboAdapter.getItem(i)))
			{
				tmp[cont++] = i;
				break;
			}
		}
		
		layoutResId = this.context.getResources().getIdentifier("candle_samecolor", "array", this.context.getPackageName());
		comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
//		layoutResId = context.getResources().getIdentifier("candle_button_samecolor_text", "id", context.getPackageName());
//		tvColorOpen4 = (TextView)jipyoui.findViewById(layoutResId);
		for(int i = 0; i < comboAdapter.getCount(); i++)
		{
			if(tvComboSameColor.getText().toString().equals(comboAdapter.getItem(i)))
			{
				tmp[cont++] = i;
				break;
			}
		}
		
		//2012. 11. 2 최대최소, 로그 체크값 전달  : I107
		tmp[cont++] = chkMinMax.isChecked()?1:0;
		tmp[cont++] = chkLog.isChecked()?1:0;
		tmp[cont++] = chkInverse.isChecked()?1:0;
		tmp[cont++] = chkGap.isChecked()?1:0;   //2016.12.14 by LYH >>갭보정 추가
		
        int[] cont_val = new int[24];
        System.arraycopy(tmp,0,cont_val,0,cont);
        _graph.changeControlValue(cont_val);
		COMUtil._mainFrame.mainBase.baseP._chart.changeGapRevision();   //2016.12.14 by LYH >>갭보정 추가
		COMUtil._mainFrame.mainBase.baseP._chart.resetTitleBoundsAll();
	}

	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
	//2012. 11. 16 setComboBox  함수 호출하여 콤보박스의 리스트 보여질때 좌표값을 직접 입력하지 않고 컨트롤좌표 기반으로 동작 : I108
//	private void setComboBox(Button btnCombo, String strAdapterDataName, 
//			final int nPopupWidth, final int nPopupLeftMargin, final int nPopupTopMargin)
//	private void setComboBox(final LinearLayout btnCombo, String strAdapterDataName)
//	{
//		final String adapterName = strAdapterDataName;
//
//		//인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서 arrayadapter 를 만든다
//      	int layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
//  		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);
//
//  		//읽어온 배열값을 arraylist 에 저장.
//  		final ArrayList<String> arStrComboData = new ArrayList<String>();
//  		for(int i = 0; i < comboAdapter.getCount(); i++)
//  		{
//  			arStrComboData.add(String.valueOf(comboAdapter.getItem(i)));
//  		}
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
//				TextView selTextView = null;
//				if(adapterName.equals("candle_baseprice"))
//				{
//					drBottom.setTitle("전봉 대비");
//					selTextView = tvComboBasePrice;
//				}
//				else
//				{
//					drBottom.setTitle("전일종가 대비");
//					selTextView = tvComboSameColor;
//				}
//
//				drBottom.setCondListView(arStrComboData);
//				drBottom.resetStrSelValue(selTextView.getText().toString());
//				drBottom.show();
//
//				drBottom.setOnClickListItemListener(new DRBottomDialog.OnClickBottomViewListItemListener() {
//					@Override
//					public void onClick(View view, int index, String value) {
//
//						if(value.equals("시가") || value.equals("종가"))
//						{
//							tvComboBasePrice.setText(value);
//						}
//						else
//						{
//							tvComboSameColor.setText(value);
//						}
//
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
//						//2020.05.14 by JJH >> 드롭박스 이벤트 활성화시 이미지 변경 UI 작업 end
//			}
//		});
//	}
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end
	private void setComboBox(LinearLayout btnCombo, String strAdapterDataName)
	{
		//콤보박스 아래에 펼처지는 리스트뷰
		ListView comboList = new ListView(this.context);

		//인자로 들어온 strAdapterDataName 은 arrays.xml 에 있는 배열의 식별자. 이를 참조해서 arrayadapter 를 만든다
		int layoutResId = this.context.getResources().getIdentifier(strAdapterDataName, "array", this.context.getPackageName());
		ArrayAdapter<CharSequence> comboAdapter = ArrayAdapter.createFromResource(this.context, layoutResId, android.R.layout.simple_dropdown_item_1line);

		//읽어온 배열값을 arraylist 에 저장.
		ArrayList<String> arStrComboData = new ArrayList<String>();
		for(int i = 0; i < comboAdapter.getCount(); i++)
		{
			arStrComboData.add(String.valueOf(comboAdapter.getItem(i)));
		}

		//콤보버튼 태그 세팅
		btnCombo.setTag(strAdapterDataName);
//  		if(strAdapterDataName.equals("candle_samecolor"))
//  		{
////  			btnCombo.setText(String.valueOf(comboAdapter.getItem(0)));

//  		}
//  		else
//  		{
////  			btnCombo.setText(String.valueOf(comboAdapter.getItem(0)));
//  		}

		//리스트뷰에 값을 세팅한다.
		comboList.setAdapter(new DropListAdapter(arStrComboData, btnCombo));

		//리스트뷰 속성 설정
		comboList.setCacheColorHint(Color.WHITE);
		comboList.setScrollingCacheEnabled(false);
		comboList.setVerticalFadingEdgeEnabled(false);
		layoutResId = this.getResources().getIdentifier("pop_back", "drawable", this.context.getPackageName());
		comboList.setBackgroundResource(layoutResId);
//          comboList.setDivider(new ColorDrawable(Color.rgb(209, 209, 209)));
		comboList.setDividerHeight((int)COMUtil.getPixel(0));

//		ImageView imageBottom = new ImageView(context);
//		String btnImg = "pop_back_bottom";
//		layoutResId = this.getContext().getResources().getIdentifier(btnImg, "drawable", this.getContext().getPackageName());
//		imageBottom.setBackgroundResource(layoutResId);
//		imageBottom.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(5)));

		final ListView _comboList2 = comboList;
//		final ImageView imgBottom2 = imageBottom;
		final LinearLayout btnCombo2 = btnCombo;

		//btnCombo는 각 콤보박스 버튼임.   콤보박스 버튼 클릭하면 리스트뷰가 아래에 나오게 함
		btnCombo.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//2012. 11. 16  콤보박스 리스트 호출방식 개선.  콤보박스 버튼과 뷰의 마진 및 패딩등을 기반으로 해서 위치계산 후 세팅한다. (onClick 대부분이 수정됨) : I108
				int layoutResId;

				//팝업윈도우의 x,y 좌표 (태블릿은 값이 있고 폰은 0, 0으로 온다 )
				RelativeLayout.LayoutParams tmplayout = COMUtil._mainFrame.indicatorParams;

				int nLeftMargin = 0;
				int nTopMargin = 0;

				if (tmplayout != null) {
					nLeftMargin = tmplayout.leftMargin;
					nTopMargin = tmplayout.topMargin;
				}

				//패딩 등 설정되어 있어서 리스트뷰가 콤보박스 좌우 align 안맞는 것 보정값 구하기
				layoutResId = context.getResources().getIdentifier("candlesetting", "id", context.getPackageName());
				LinearLayout ll = (LinearLayout)jipyoui.findViewById(layoutResId);
				//스크롤뷰의 패딩 : 스크롤뷰는 하나의 자식만을 갖는데 그 유일한 자식인 candlesetting이란  LinearLayout 이 실질적으로 스크롤 되는 뷰이고 그곳에 패딩세팅이 되어있음
				int nScrollviewInnerPaddingLeft = ll.getPaddingLeft();
				//xml 레이아웃 자체패딩
				int nCandleSettingViewPaddingLeft = jipyoui.getChildAt(0).getPaddingLeft();
				int nDropdownViewLeftAdjustValue = nScrollviewInnerPaddingLeft + nCandleSettingViewPaddingLeft;

				//폰의 위쪽 상태바 크기  (폰에선 25dip고정이라 함. 태블릿은 0.  )
				int statusBarHeight;
				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
				{
					statusBarHeight = (int)COMUtil.getPixel(0);
				}
				else
				{
					Configuration config = getResources().getConfiguration();

					if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
					{
						//--- 가로 화면 고정
						statusBarHeight = (int)COMUtil.getPixel(0);
					}
					else
					{
						//--- 세로 화면 고정
						statusBarHeight = (int)COMUtil.getPixel(30);
					}
				}

				//앱의 제목줄 높이
				layoutResId = context.getResources().getIdentifier("candle_relative_top", "id", context.getPackageName());
				ViewGroup relativeHeadline = (ViewGroup)jipyoui.findViewById(layoutResId);
				int relativeHeight = relativeHeadline.getHeight();

				//캔들설정창 xml 에서 콤보박스의 부모 LinearLayout 의 상단 y 좌표
				LinearLayout parentLinear = (LinearLayout)v.getParent();
				int parentLinearTop = parentLinear.getTop();


				//콤보의 드랍다운뷰의 왼쪽위 좌표 (v 는 현재 선택한 콤보박스 버튼)  버튼의왼쪽x값 + 패딩등으로설정되었던왼쪽영역보정값 + 팝업윈도우왼쪽마진
				int nDropdownViewLeft = v.getLeft() + nDropdownViewLeftAdjustValue + nLeftMargin + (int)COMUtil.getPixel_W(18);
				//콤보의 드랍다운뷰의 상단 좌표      (콤보박스부모linear의상단y좌표 + 제목줄높이 + 상태바높이 + 콤보버튼높이 + 팝업윈도우위쪽마진-스크롤된상단위치)
				//2013. 10. 10 캔들설정창 콤보박스 드랍다운리스트뷰가 가로모드 스크롤 했을 때 y 축이 아래로 쳐지는 현상>>
				//가로모드 같이 스크롤 되는 경우 위쪽 스크롤 된 길이를 구한다. 이 값을 빼줘야 가로모드 스크롤 시에 정확한 y 위치에 리스트뷰가 나타난다.
				ScrollView scroll = (ScrollView)ll.getParent();
				int nScrolledYPos = scroll.getScrollY();
//  				int nDropdownViewTop = parentLinearTop + relativeHeight + statusBarHeight + (v.getBottom() - v.getTop())  + nTopMargin + (int)COMUtil.getPixel(3);
				int nDropdownViewTop = parentLinearTop + relativeHeight + statusBarHeight + (v.getBottom() - v.getTop())  + nTopMargin + (int)COMUtil.getPixel(3)-nScrolledYPos;
				//2013. 10. 10 캔들설정창 콤보박스 드랍다운리스트뷰가 가로모드 스크롤 했을 때 y 축이 아래로 쳐지는 현상<<
				//드랍다운뷰의 너비를 버튼위치 기반으로 맞추기
				int nDropdownViewWidth = v.getRight() - v.getLeft();

				//버튼 이외의 영역 터치했을경우 정상적으로 닫히게 처리
				if(comboCandlePopup != null && !comboCandlePopup.isShowing())
				{
					comboCandlePopup = null;
					btnCombo2.setSelected(false);
					comboCandleLayout.removeAllViews();
				}

				//콤보박스가 열려있는상태 (not null)  면 닫고  아니면 생성해서 오픈
				if(comboCandlePopup != null)
				{
					comboCandlePopup.dismiss();
					btnCombo2.setSelected(false);
					comboCandlePopup = null;
					comboCandleLayout.removeAllViews();
				}
				else
				{
					comboCandleLayout.addView(_comboList2);
//					comboCandleLayout.addView(imgBottom2);
//  					comboCandlePopup = new PopupWindow(comboCandleLayout, (int)COMUtil.getPixel(nPopupWidth), LayoutParams.WRAP_CONTENT, true);
					comboCandlePopup = new PopupWindow(comboCandleLayout, nDropdownViewWidth, LayoutParams.WRAP_CONTENT, true);
					comboCandlePopup.setOutsideTouchable(true); // 이부분을 설정해주어야 팝업이 떳을때 다른부분에 이벤트를 줄수있습니다.
					comboCandlePopup.setBackgroundDrawable(new BitmapDrawable());  // 이부분에 이벤트가 들어오게됩니다.

					comboCandlePopup.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss() {
							btnCombo2.setSelected(false);
						}
					});

					//팝업 띄우기
//  					comboCandlePopup.showAtLocation(comboCandleLayout, Gravity.TOP|Gravity.LEFT, (int)COMUtil.getPixel(nPopupLeftMargin), (int)COMUtil.getPixel(nPopupTopMargin));
					comboCandlePopup.showAtLocation(comboCandleLayout, Gravity.NO_GRAVITY, nDropdownViewLeft, nDropdownViewTop);
					btnCombo2.setSelected(true);
				}
			}
		});
	}
	//2020.04.23 by JJH >> 콤보박스 UI 수정 start
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
			COMUtil.setGlobalFont(parent);
			if( tv == null ) {
				tv = new TextView(getContext());
				tv.setTextColor(Color.rgb(17, 17, 17));
//				tv.setBackgroundColor(Color.TRANSPARENT);
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setSingleLine();
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				tv.setWidth((int)COMUtil.getPixel(127));
				//2012. 9. 12 콤보박스의 한 행 크기 조절
				tv.setHeight((int)COMUtil.getPixel(36));
				//2012. 9. 12  콤보박스의 글씨 왼쪽 여백 추가
//				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//				{
//					tv.setPadding((int)COMUtil.getPixel(8), 0, 0, 0);
//				}
//				else
//				{
					tv.setPadding((int)COMUtil.getPixel(10), 0, 0, 0);
//				}
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}

			if(m_arrData!=null) tv.setText(m_arrData.get(position));
			String strTitle = m_arrData.get(position);

//			int nComboboxTextIndex = Integer.parseInt((String)_btnCombo.getTag());
			String strPriceComboTitle = tvComboBasePrice.getText().toString();
			String strColorComboTitle = tvComboSameColor.getText().toString();
			if(strColorComboTitle.equals(strTitle) || strPriceComboTitle.equals(strTitle))
			{
//				int layoutResId = context.getResources().getIdentifier("pop_back_sel", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(72, 139, 226));
			}
			else
			{
//				int layoutResId = context.getResources().getIdentifier("combo_change", "drawable", context.getPackageName());
//				tv.setBackgroundResource(layoutResId);
				tv.setTextColor(Color.rgb(17, 17, 17));
			}

			//값을 선택하면 리스트뷰를 닫음
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(_btnCombo == btnComboBasePrice)
						tvComboBasePrice.setText(String.valueOf(m_arrData.get(pos)));
					else
						tvComboSameColor.setText(String.valueOf(m_arrData.get(pos)));

					comboCandlePopup.dismiss();
					comboCandlePopup = null;

					comboCandleLayout.removeAllViews();
					reSetJipyo();
					COMUtil._neoChart.repaintAll();
				}
			});
			return tv;
		}
	}
	//2020.04.23 by JJH >> 콤보박스 UI 수정 end

	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
	@Override
	public void destroy() {
		super.destroy();
		
		//열려있는 콤보박스 리스트뷰를 닫아준다. 
		if(null != comboCandlePopup)
		{
			comboCandlePopup.dismiss();
			comboCandlePopup = null;
		}
		if (COMUtil._mainFrame.indicatorConfigView != null) {
			COMUtil._mainFrame.indicatorConfigView.initGeneral();
		}
	}
	//2013. 10. 10 상세설정창, 거래량설정창, 캔들설정창의 모든 팝업뷰를 회전시 닫는 처리 >>
	
	
	//2012. 10. 2  체크박스 이미지 변경  : I106
	//2013.04.05 체크박스 리사이징 코드 비적용 >>
//	public static StateListDrawable imageChange(Drawable normal, Drawable checked) {
//        StateListDrawable imageDraw = new StateListDrawable();
//        imageDraw.addState(new int[] { android.R.attr.state_checked }, checked);
//        imageDraw.addState(new int[] { -android.R.attr.state_checkable}, normal);
//        return imageDraw; 
//    }
	//2013.04.05 체크박스 리사이징 코드 비적용 <<
}


