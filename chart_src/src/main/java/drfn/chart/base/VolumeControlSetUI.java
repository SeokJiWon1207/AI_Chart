package drfn.chart.base;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import drfn.chart.draw.DrawTool;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

public class VolumeControlSetUI extends JipyoControlSetUI {
	ArrayList<RadioButton> radioBtns;
	
	int[] radioBtnId;
	//2013.09.27 by LYH >> 캔들색과 같이 디폴트로 변경 <<
	int nVolType = 3;
	
	public VolumeControlSetUI(Context context, RelativeLayout layout) {
		super(context, layout);
	}

	TextView tvColorOpen, tvColorOpen2, tvColorOpen3;
	public void setUI() {
		radioBtns = new ArrayList<RadioButton>();
		
//		jipyoui 를 inflate 로 받아오지 않고 DetailJipyoController에서 addView 되어있
		//View 를 받아온다. 
		jipyoui = (LinearLayout)layout.getChildAt(layout.getChildCount() - 1);
		
		int layoutResId = this.context.getResources().getIdentifier("volumesetting", "id", this.context.getPackageName());
		LinearLayout radioLinear = (LinearLayout)jipyoui.findViewById(layoutResId);

		RadioButton radioTemp;
		View layout;
		for(int i=0; i<5; i++)
		{
			layoutResId = this.context.getResources().getIdentifier("volume_radio_btn_"+(i+1), "id", this.context.getPackageName());
			radioTemp = (RadioButton)radioLinear.findViewById(layoutResId);
			radioTemp.setOnClickListener(new RadioButton.OnClickListener() {
	        	public void onClick(View v) {
	        		onRadioClick(v);
		        }
	        });
			radioBtns.add(radioTemp);
		}

		layoutResId = this.context.getResources().getIdentifier("volumeTxt1", "id", this.context.getPackageName());
		TextView tvVolumeTxt1 = (TextView)jipyoui.findViewById(layoutResId);
		tvVolumeTxt1.setTypeface(COMUtil.typefaceMid);

		layoutResId = this.context.getResources().getIdentifier("volumeTxt2", "id", this.context.getPackageName());
		TextView tvVolumeTxt2 = (TextView)jipyoui.findViewById(layoutResId);
		tvVolumeTxt2.setTypeface(COMUtil.typefaceMid);

		layoutResId = this.context.getResources().getIdentifier("volumecolor_up", "id", this.context.getPackageName());
		tvColorOpen = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen);

			}
		});
		
		layoutResId = this.context.getResources().getIdentifier("volumecolor_down", "id", this.context.getPackageName());
		tvColorOpen2 = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen2);

			}
		});


		layoutResId = this.context.getResources().getIdentifier("volumecolor_equal", "id", this.context.getPackageName());
		tvColorOpen3 = (TextView)jipyoui.findViewById(layoutResId);
		tvColorOpen3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showColorPalette(tvColorOpen3);
			}
		});
		
	}
	
	private void resetRadioBtnsTextColor()
	{
		for(int i = 0; i < 5; i++)
		{
			radioBtns.get(i).setSelected(false);
			radioBtns.get(i).setTextColor(Color.rgb(51, 51, 51));
			//setTitleColor(i, Color.rgb(21, 21, 21));
		}
	}

	public void resetUI() {
		//선택되었던 라디오버튼 선택된상태로 세팅 
		radioBtns.get(_graph._cvm.getVolDrawType()).setChecked(true);
		radioBtns.get(_graph._cvm.getVolDrawType()).setTextColor(Color.rgb(51, 51, 51));
		
		if(_graph.getDrawTool().size()>0)
		{
			DrawTool dt = _graph.getDrawTool().get(0);
			
			if(dt != null)
			{
				int[] getUpColor = dt.getUpColor();
//				tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//				tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
				
				int[] getDownColor = dt.getDownColor();
//				tvColorOpen2.setBackgroundColor(Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
//				((GradientDrawable)tvColorOpen2.getBackground()).setColor(Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
//				tvColorOpen2.setTag(""+Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
				setButtonColorWithRound(tvColorOpen2, Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
				
				int[] getSameColor = dt.getSameColor();
//				tvColorOpen3.setBackgroundColor(Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
//				((GradientDrawable)tvColorOpen3.getBackground()).setColor(Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
//				tvColorOpen3.setTag(""+Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
				setButtonColorWithRound(tvColorOpen3, Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
			}
		}
		else
		{
			int[] getUpColor = CoSys.CHART_COLORS[0];
//			tvColorOpen.setBackgroundColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//			((GradientDrawable)tvColorOpen.getBackground()).setColor(Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
//			tvColorOpen.setTag(""+Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
			setButtonColorWithRound(tvColorOpen, Color.rgb(getUpColor[0], getUpColor[1], getUpColor[2]));
			
			int[] getDownColor = CoSys.CHART_COLORS[1];
//			tvColorOpen2.setBackgroundColor(Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
//			((GradientDrawable)tvColorOpen2.getBackground()).setColor(Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
//			tvColorOpen2.setTag(""+Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
			setButtonColorWithRound(tvColorOpen2, Color.rgb(getDownColor[0], getDownColor[1], getDownColor[2]));
			
			//2013. 10. 10 거래량설정에서 초기화 시 연한갈색 보합색이 아닌 변경전 검은색으로 바뀌는 현상>>
//			int[] getSameColor =CoSys.CHART_COLORS[2];
			int[] getSameColor =CoSys.CHART_COLORS[3];
			//2013. 10. 10 거래량설정에서 초기화 시 연한갈색 보합색이 아닌 변경전 검은색으로 바뀌는 현상<<
//			tvColorOpen3.setBackgroundColor(Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
//			((GradientDrawable)tvColorOpen3.getBackground()).setColor(Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
//			tvColorOpen3.setTag(""+Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
			setButtonColorWithRound(tvColorOpen3, Color.rgb(getSameColor[0], getSameColor[1], getSameColor[2]));
		}
	}
	
	public void reSetOriginal() {
		//기본값 : 전일대비 종가 상승/하락 
		//2013.09.27 by LYH >> 캔들색과 같이 디폴트로 변경 <<
		nVolType = 3;
		_graph._cvm.setVolDrawType(nVolType);

		//라디오버튼 초기화 
		resetRadioBtnsTextColor();
		radioBtns.get(_graph._cvm.getVolDrawType()).setChecked(true);
		radioBtns.get(_graph._cvm.getVolDrawType()).setTextColor(Color.rgb(51, 51, 51));
		
		//상승, 하락, 보합 팔레트 초기화 
//		tvColorOpen.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
//		tvColorOpen.setTag(""+Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		setButtonColorWithRound(tvColorOpen, Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
		
//		tvColorOpen2.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
//		tvColorOpen2.setTag(""+Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
		setButtonColorWithRound(tvColorOpen2, Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));

		//2013. 10. 10 거래량설정에서 초기화 시 연한갈색 보합색이 아닌 변경전 검은색으로 바뀌는 현상>>
//		tvColorOpen3.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[2][0], CoSys.CHART_COLORS[2][1], CoSys.CHART_COLORS[2][2]));
//		tvColorOpen3.setTag(""+Color.rgb(CoSys.CHART_COLORS[2][0], CoSys.CHART_COLORS[2][1], CoSys.CHART_COLORS[2][2]));
		
//		tvColorOpen3.setBackgroundColor(Color.rgb(CoSys.CHART_COLORS[3][0], CoSys.CHART_COLORS[3][1], CoSys.CHART_COLORS[3][2]));
//		tvColorOpen3.setTag(""+Color.rgb(CoSys.CHART_COLORS[3][0], CoSys.CHART_COLORS[3][1], CoSys.CHART_COLORS[3][2]));
		setButtonColorWithRound(tvColorOpen3, Color.rgb(CoSys.CHART_COLORS[3][0], CoSys.CHART_COLORS[3][1], CoSys.CHART_COLORS[3][2]));
		//2013. 10. 10 거래량설정에서 초기화 시 연한갈색 보합색이 아닌 변경전 검은색으로 바뀌는 현상<<
		
	}
	
	public void reSetJipyo() {
		int[] tmp = new int[10];
        int cont=0;
        
        tmp[cont++] = nVolType;
		
		String a = (String)tvColorOpen.getTag();
		int color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		a = (String)tvColorOpen2.getTag();
		color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
		a = (String)tvColorOpen3.getTag();
		color = Integer.parseInt(a);
		tmp[cont++] = Color.red(color);
		tmp[cont++] = Color.green(color);
		tmp[cont++] = Color.blue(color);
		
        int[] cont_val = new int[10];
        System.arraycopy(tmp,0,cont_val,0,cont);
        _graph.changeControlValue(cont_val);
		
        COMUtil._mainFrame.mainBase.baseP._chart.resetTitleBoundsAll();
	}
	
	public void onRadioClick(View v) {
		// TODO Auto-generated method stub
		String tag = (String)v.getTag();
		nVolType = Integer.parseInt(tag)-1;
		String strResId = "volume_radio_btn_"+tag;
		int layoutResId = context.getResources().getIdentifier(strResId, "id", context.getPackageName());
		RadioButton rb = (RadioButton) jipyoui.findViewById(layoutResId);
		resetRadioBtnsTextColor();
		rb.setSelected(true);
		rb.setTextColor(Color.rgb(51, 51, 51));
		//setTitleColor(nVolType, Color.rgb(203, 29, 118));
		//당일시가 대비 종가 상승/하락 작동코드 
		reSetJipyo();
		COMUtil._neoChart.repaintAll();
	}
}


