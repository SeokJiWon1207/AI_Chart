/**
 *
 */
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.draw.paletteDialog;
import drfn.chart.util.COMUtil;


/**
 * @author user
 *
 */
//2012. 8. 3   키보드 감추기 기능을 위해 OnEditorActionListener 를 포함시킴
public class CompareSettingController extends View implements View.OnClickListener {
	LinearLayout xmlUI = null;
	Handler mHandler=null;
    Context mContext;
	RelativeLayout layout = null;

    CheckBox[] btnChk = new CheckBox[3];
	TextView[] tvTitle = new TextView[3];
	CheckBox[] rbtnCompConnet = new CheckBox[3];
	TextView[] tvCCTitle = new TextView[3];
	RadioButton[] rbtnCompFx = new RadioButton[3];
	TextView[] tvCFTitle = new TextView[2];
	Vector<String> CompArr;

	private SharedPreferences m_prefConfig = null;

	public CompareSettingController(final Context context, RelativeLayout layout) {
		super(context);
        mContext = context;
		m_prefConfig = context.getSharedPreferences("compareSetting", Context.MODE_PRIVATE);

		this.layout = layout;


		CompArr = COMUtil.compareArr;
		if(mHandler==null) {
			mHandler = new Handler() {
				@Override public void handleMessage(Message msg) {
					close();
					COMUtil.closeCompareSettingPopup();

					String nVal ="0";
					if(COMUtil._mainFrame.bCompConnectFut)
						nVal = "1";
					Hashtable<String, Object> dic = new Hashtable<String, Object>();
					dic.put("compConnect", nVal);
					dic.put("compFxbuysell", COMUtil._mainFrame.strCompFX);
					if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SET_COMPARECONFIG_CLOSE, dic);

				}
			};
		}

		int[] ids = {
				context.getResources().getIdentifier("chk_comptype1", "id", context.getPackageName()),
				context.getResources().getIdentifier("chk_comptype2", "id", context.getPackageName()),
				context.getResources().getIdentifier("chk_comptype3", "id", context.getPackageName())
		};
		int[] ids1 = {
				context.getResources().getIdentifier("textType1", "id", context.getPackageName()),
				context.getResources().getIdentifier("textType2", "id", context.getPackageName()),
				context.getResources().getIdentifier("textType3", "id", context.getPackageName())
		};

		int[] idsLine = {
				context.getResources().getIdentifier("line1", "id", context.getPackageName()),
				context.getResources().getIdentifier("line2", "id", context.getPackageName()),
				context.getResources().getIdentifier("line3", "id", context.getPackageName()),
				context.getResources().getIdentifier("line4", "id", context.getPackageName()),
				context.getResources().getIdentifier("line5", "id", context.getPackageName()),
		};

		int[] idsLine1 = {
				context.getResources().getIdentifier("thicktextview1", "id", context.getPackageName()),
				context.getResources().getIdentifier("thicktextview2", "id", context.getPackageName()),
				context.getResources().getIdentifier("thicktextview3", "id", context.getPackageName()),
				context.getResources().getIdentifier("thicktextview4", "id", context.getPackageName()),
				context.getResources().getIdentifier("thicktextview5", "id", context.getPackageName()),
		};

//		int[] idsCC = {
//				context.getResources().getIdentifier("chk_compOn", "id", context.getPackageName()),
//				context.getResources().getIdentifier("chk_compOff", "id", context.getPackageName()),
//				context.getResources().getIdentifier("textOn", "id", context.getPackageName()),
//				context.getResources().getIdentifier("textOff", "id", context.getPackageName())
//
//		};
//		int[] idsCFx = {
//				context.getResources().getIdentifier("chk_compfxbuy", "id", context.getPackageName()),
//				context.getResources().getIdentifier("chk_compfxsell", "id", context.getPackageName()),
//				context.getResources().getIdentifier("textFxBuy", "id", context.getPackageName()),
//				context.getResources().getIdentifier("textFxSell", "id", context.getPackageName())
//		};


		int layoutResId = context.getResources().getIdentifier("chart_compare_type", "layout", context.getPackageName());
		LayoutInflater factory = LayoutInflater.from(context);
		xmlUI = (LinearLayout)factory.inflate(layoutResId, null);
		xmlUI.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnBack", "id", this.getContext().getPackageName());
		Button btnBack = (Button) xmlUI.findViewById(layoutResId);
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Message msg = new Message();
					mHandler.sendMessage(msg);
				}
				catch (Exception e) {
				}
			}
		});

		layoutResId = this.getContext().getResources().getIdentifier("frameaBtnInit", "id", this.getContext().getPackageName());
		Button btnInit = (Button) xmlUI.findViewById(layoutResId);
		btnInit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					resetOriginal();
				}catch (Exception e) {
				}
			}
		});

		for(int i = 0; i < 2; i++){
			layoutResId = context.getResources().getIdentifier("comparechart_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvCompareChart = (TextView)xmlUI.findViewById(layoutResId);
			tvCompareChart.setTypeface(COMUtil.typefaceMid);
		}

		this.layout.addView(xmlUI);

		COMUtil.setGlobalFont(xmlUI);

		for(int i = 0 ; i < 3  ; i++){
			CheckBox btnCheckBox = (CheckBox)layout.findViewById(ids[i]);
			TextView tv = (TextView)layout.findViewById(ids1[i]);

			btnChk[i] = btnCheckBox;
			tvTitle[i] = tv;

			btnCheckBox.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

                    CheckBox chk = (CheckBox) v;
					for (int i = 0 ; i < btnChk.length ; i++)
					{
						if(btnChk[i] != chk)
						{

							CheckBox tmp = btnChk[i];

							tmp.setChecked(false);
						}

					}
				}
			});

			tv.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					for (int i = 0 ; i < btnChk.length  ; i++)
					{
						if(tvTitle[i] != v)
						{
							CheckBox tmp = btnChk[i];
							tmp.setChecked(false);
						}
						else
						{
							CheckBox tmp = btnChk[i];
							tmp.setChecked(true);
						}
					}
				}
			});

//			final CheckBox rbtnCC = (CheckBox)layout.findViewById(idsCC[i]);
//			rbtnCompConnet[i] = rbtnCC;
//			rbtnCompConnet[i].setOnClickListener(radioListener);
//
//			TextView tvCC = (TextView)layout.findViewById(idsCC[i+2]);
//			tvCC.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					rbtnCC.performClick();
//				}
//			});
//
//			final RadioButton rbtnCFx = (RadioButton)layout.findViewById(idsCFx[i]);
//			rbtnCompFx[i] = rbtnCFx;
//			rbtnCompFx[i].setOnClickListener(radioListener);
//
//			TextView tvCFx = (TextView)layout.findViewById(idsCFx[i+2]);
//			tvCFx.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					rbtnCFx.performClick();
//				}
//			});

		}

//		if(COMUtil._mainFrame.bCompConnectFut)
//			rbtnCompConnet[0].setChecked(true);
//		else
//			rbtnCompConnet[1].setChecked(true);
//
//		if(COMUtil._mainFrame.strCompFX.equals("B"))
//			rbtnCompFx[0].setChecked(true);
//		else
//			rbtnCompFx[1].setChecked(true);

		for(int i = 0 ; i < 5 ; i++) {
			final TextView tvColor = (TextView) layout.findViewById(idsLine[i]);
			final TextView tvline = (TextView) layout.findViewById(idsLine1[i]);

			tvColor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showColorPalette(tvColor);
				}
			});


			tvline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showLineSelect(tvline);
				}
			});

			setEnvData();
		}
	}


	RadioButton.OnClickListener radioListener = new RadioButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if(v == rbtnCompConnet[0])		//연결선물 ON
			{
				rbtnCompConnet[1].setChecked(false);
				COMUtil._mainFrame.bCompConnectFut = true;
			}
			else if(v == rbtnCompConnet[1])		//연결선물 OFF
			{
				rbtnCompConnet[0].setChecked(false);
				COMUtil._mainFrame.bCompConnectFut = false;
			}

			if(v == rbtnCompFx[0])			//FX마진 매수
			{
				rbtnCompFx[1].setChecked(false);
				COMUtil._mainFrame.strCompFX = "B";
			}
			else if(v == rbtnCompFx[1])		//FX마진 매도
			{
				rbtnCompFx[0].setChecked(false);
				COMUtil._mainFrame.strCompFX = "S";
			}
		}
	};


	public void resetOriginal() {
		//2020.05.21 by JJH >> 비교차트 초기화 버튼 이벤트 오류 수정 start
//		rbtnCompConnet[0].setChecked(false);
//		rbtnCompConnet[1].setChecked(true);
//		rbtnCompConnet[2].setChecked(false);
		//2020.05.21 by JJH >> 비교차트 초기화 버튼 이벤트 오류 수정 end

//		rbtnCompFx[0].setChecked(true);
//		rbtnCompFx[1].setChecked(false);

		COMUtil._mainFrame.bCompConnectFut = false;
		COMUtil._mainFrame.strCompFX = "B";

		setEnvString("compare_type", "0");
		setEnvString("compare_color", "");
		setEnvString("compare_thick", "");
		setEnvData();

	}
	public void setEnvData() {
		String strType = getEnvString("compare_type", "0");
		int nType = Integer.parseInt(strType);
		// 종목명 Setting
		//int nSize= CompArr.size();
		String strColor = getEnvString("compare_color", "");
		//2020.05.25 by JJH >> 비교차트 기본 색상 변경 start
//	    int nCompareColor[][] = {
//			{247, 94, 94}, //red
//			{21, 126, 232}, //blue
//			{0, 166, 81}, //green
//			{113, 110, 194}, //purple
//			{239, 174, 61} //yellow
//	    };
		int nCompareColor[][] = {
				{0, 149, 160}, //청록색
				{81, 193, 241}, //하늘색
				{66, 121, 214}, //파란색
				{163, 114, 231}, //보라색
				{63, 197, 152} //녹색
		};
		//2020.05.25 by JJH >> 비교차트 기본 색상 변경 end
		if(strColor.length()>0)
		{
			String strColors[] = strColor.split("=");
			if(strColors.length>=15)
			{
				for(int i = 0 ; i < 5 ; i++)	{
					nCompareColor[i][0] = Integer.parseInt(strColors[i*3+0]);
					nCompareColor[i][1] = Integer.parseInt(strColors[i*3+1]);
					nCompareColor[i][2] = Integer.parseInt(strColors[i*3+2]);
				}
			}
		}

		String strThick = getEnvString("compare_thick", "");
	    int nCompareThick[] = { 2, 2, 2, 2, 2	};
		if(strThick.length()>0)
		{
			String strThicks[] = strThick.split("=");
			if(strThicks.length>=5)
			{
				for(int i = 0 ; i < 5 ; i++)	{
					nCompareThick[i] = Integer.parseInt(strThicks[i]);
				}
			}
		}
		int[] idsLine = {
				mContext.getResources().getIdentifier("line1", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line2", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line3", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line4", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line5", "id", mContext.getPackageName()),
		};

		int[] idsLine1 = {
				mContext.getResources().getIdentifier("thicktextview1", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview2", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview3", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview4", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview5", "id", mContext.getPackageName()),
		};

        int[] Lines	= {
                mContext.getResources().getIdentifier("line_width01", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width02", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width03", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width04", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width05", "drawable", mContext.getPackageName()),

        };
        int[] Lines_black = {
                mContext.getResources().getIdentifier("line_width01_black", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width02_black", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width03_black", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width04_black", "drawable", mContext.getPackageName()),
                mContext.getResources().getIdentifier("line_width05_black", "drawable", mContext.getPackageName()),
        };
		for (int i = 0 ; i < 3 ; i++)
		{
				if(nType == i)
				{
					btnChk[i].setChecked(true);
				}
				else
				{
					btnChk[i].setChecked(false);
				}
		}

		for(int i = 0 ; i < 5 ; i++)	{
			TextView tvColor = (TextView)layout.findViewById(idsLine[i]);
			TextView tvline = (TextView)layout.findViewById(idsLine1[i]);
//			TextView tvline1 = (TextView)layout.findViewById(idsLine2[i]);

			//((GradientDrawable)tvColor.getBackground()).setColor(Color.rgb(nCompareColor[i][0], nCompareColor[i][1],nCompareColor[i][2]));
//			tvColor.setBackgroundColor(Color.rgb(nCompareColor[i][0], nCompareColor[i][1],nCompareColor[i][2]));
//			tvColor.setTag(""+Color.rgb(nCompareColor[i][0], nCompareColor[i][1],nCompareColor[i][2]));
			setButtonColorWithRound(tvColor, Color.rgb(nCompareColor[i][0], nCompareColor[i][1],nCompareColor[i][2]));

			tvline.setBackgroundResource(Lines[nCompareThick[i]-1]);

			tvline.setTag(String.valueOf(nCompareThick[i]));

		}
	}

	public void setEnvString(String strKey, String strValue)
	{
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

	public void close() {

		int[] idsLine = {
				mContext.getResources().getIdentifier("line1", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line2", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line3", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line4", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("line5", "id", mContext.getPackageName()),
		};

		int[] idsLine1 = {
				mContext.getResources().getIdentifier("thicktextview1", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview2", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview3", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview4", "id", mContext.getPackageName()),
				mContext.getResources().getIdentifier("thicktextview5", "id", mContext.getPackageName()),
		};
		for (int i = 0 ; i < 3 ; i++)
		{

			CheckBox tmp = btnChk[i];
				if(tmp.isChecked())
				{
					setEnvString("compare_type", ""+i);
				}
		}
		int color;
		String strSaveColor="";
		String strSaveThick="";
		for(int i = 0 ; i < 5 ; i++)	{
			//TextView tvItem = (TextView)layout.findViewById(idsItem[i]);
			TextView tvColor = (TextView)layout.findViewById(idsLine[i]);
			TextView tvline1 = (TextView)layout.findViewById(idsLine1[i]);
			String a = (String)tvColor.getTag();
			color = Integer.parseInt(a);
			strSaveColor += Color.red(color)+"=";
			strSaveColor += Color.green(color)+"=";
			strSaveColor += Color.blue(color)+"=";
			strSaveThick += tvline1.getTag().toString().substring(0,1)+"=";
		}
		setEnvString("compare_color", strSaveColor);
		setEnvString("compare_thick", strSaveThick);
	}

	public void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
//		int btnID = view.getId();
//		switch(btnID) {
//			case R.id.frameaBtnFunction:
//
//		}


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
		paletteDialog palette = new paletteDialog(mContext, 4, 6, tvColorOpen, colorTag);
		palette.setParent(this);
		//2012. 7. 20  팔레트 밖의 영역을 터치하면 닫히도록 설정
		palette.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = palette.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
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

	public void updateLineValue() {
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
		drfn.chart.draw.LineDialog linepalette = new drfn.chart.draw.LineDialog(mContext, 5, 1, tvLineOpen, lineTag);
		linepalette.setParent(this);
		linepalette.setCanceledOnTouchOutside(false);
		int[] location = new int[2];
		tvLineOpen.getLocationOnScreen(location);
		//드랍다운리스트뷰의 너비
		int nLineBtnWidth = tvLineOpen.getWidth();
		//드랍다운리스트뷰의 x축좌표
		int nLineBtnLeft = location[0];
		//드랍다운리스트뷰의 y축좌표
		int nLineBtnTop = location[1] - (int)COMUtil.getPixel(4);
		WindowManager.LayoutParams params = linepalette.getWindow().getAttributes();
		params.y = nLineBtnTop;
		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*3;
		params.gravity = Gravity.TOP|Gravity.START;
		linepalette.show();
	}
	public void updateValue(final SeekBar seekBar) {
		mHandler.post(new Runnable() {
			public void run() {

//				reSetJipyo();
//				COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
//				COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
//				changeLabelColorThickSetting(seekBar);	//2013. 8. 27 주가이평/거래량이평에서 변수설정 값 변경시 색굵기설정의 숫자도 같이 변경>>
			}
		});
	}

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

}
