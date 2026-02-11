package drfn.chart.comp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import drfn.chart.NeoChart2;
import drfn.chart.draw.LineDialog;
import drfn.chart.draw.paletteDialog;
import drfn.chart.util.COMUtil;
import drfn.chart.util.ChartUtil;
import drfn.chart.util.DoublePoint;

import static drfn.chart.util.COMUtil.mHandler;

/**
 * 분석툴바 설정창
 * @author dev_kimsh
 * @since  2014. 8. 21 (First develop)
 * */

public class AnalToolSettingViewController extends Dialog implements OnClickListener{
	/** 변수 **/
	/**전달받은 context*/
	Context m_context;
	/**전달받은 부모차트*/
	NeoChart2 m_ParentChart;

	/** 컨트롤 **/
	/**이 창의 xml (analtoolsettingview)*/
	LinearLayout m_thisLayout;
	/** 시작 TextView */
	TextView m_tvStartDate;
	/** 종료 TextView */
	TextView m_tvEndDate;
	/** 시작 가격 EditText */
	EditText m_edStartDatePrice;
	/** 종료 가격 EditText */
	EditText m_edEndDatePrice;
	/** 체크박스 */
	CheckBox m_chkMinMaxPrice;
	/** 확인버튼 */
	Button m_btnOK;
	
	/** 종가따라가기 Textview */
	TextView m_tvMinMaxPrice;

	/** 색상 및 라인 **/
	TextView m_tvColorOpen;
	TextView m_tvLineOpen;

	int[] Lines = {
			this.getContext().getResources().getIdentifier("line_width01", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width02", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width03", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width04", "drawable", this.getContext().getPackageName()),
			this.getContext().getResources().getIdentifier("line_width05", "drawable", this.getContext().getPackageName()),
	};

	public AnalToolSettingViewController(Context context, NeoChart2 parentChart) {
		// Dialog 배경을 투명 처리 해준다.
//        super(context , android.R.style.Theme_Translucent_NoTitleBar);
		super(context, context.getResources().getIdentifier("alert_layout", "style", context.getPackageName()));

		this.m_context = context;
		this.m_ParentChart = parentChart;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initDialog();
	}

	/**다이얼로그 초기화*/
	private void initDialog()
	{
		//다이얼로그 레이아웃
		m_thisLayout =  (LinearLayout)LayoutInflater.from(m_context).inflate(m_context.getResources().getIdentifier("analtoolsettingview", "layout", m_context.getPackageName()), null);
		//xml 을 컨텐츠뷰로 설정
		if(null != m_thisLayout)
			//2020.04.21 by JJH >> 분석도구 설정 팝업 UI 수정 start
//			this.setContentView(m_thisLayout, new ViewGroup.LayoutParams((int)COMUtil.getPixel(320), (int) COMUtil.getPixel(262)));
			this.setContentView(m_thisLayout, new ViewGroup.LayoutParams((int)COMUtil.getPixel(320), ViewGroup.LayoutParams.WRAP_CONTENT));
			//2020.04.21 by JJH >> 분석도구 설정 팝업 UI 수정 end
		//TextView
		m_tvStartDate = (TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_startdate", "id", m_context.getPackageName()));
		m_tvEndDate = (TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_enddate", "id", m_context.getPackageName()));

		//EditText
		m_edStartDatePrice = (EditText)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_startdate_price", "id", m_context.getPackageName()));
		m_edEndDatePrice = (EditText)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_enddate_price", "id", m_context.getPackageName()));

		m_edStartDatePrice.setTypeface(COMUtil.numericTypefaceMid);
		m_edEndDatePrice.setTypeface(COMUtil.numericTypefaceMid);

		//CheckBox
		m_chkMinMaxPrice = (CheckBox)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice", "id", m_context.getPackageName()));
		m_chkMinMaxPrice.setOnClickListener(this);

		m_tvMinMaxPrice =(TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice_title", "id", m_context.getPackageName()));
		m_tvMinMaxPrice.setOnClickListener(this);
		//Button
		m_btnOK = (Button)m_thisLayout.findViewById(m_context.getResources().getIdentifier("btn_ok", "id", m_context.getPackageName()));
		m_btnOK.setOnClickListener(this);

		DoublePoint analData[] = m_ParentChart.select_at.data;
		DoublePoint analDicStart, analDicEnd;
		if(analData.length>0) {
			analDicStart = analData[0];

			int xIndex=m_ParentChart.select_at.getIndexWithDate(analDicStart.x);
			String date = m_ParentChart._cdm.getFormatData("자료일자", xIndex);
			m_tvStartDate.setText(date);
			String price = ChartUtil.getFormatedData(analDicStart.y, m_ParentChart._cdm.getPriceFormat(), m_ParentChart._cdm);
			//price = price.replace(",", "");
			m_edStartDatePrice.setText(price);

			if(analData.length>1)
			{
				analDicEnd = analData[1];
				xIndex=m_ParentChart.select_at.getIndexWithDate(analDicEnd.x);
				date = m_ParentChart._cdm.getFormatData("자료일자", xIndex);
				m_tvEndDate.setText(date);
				price = ChartUtil.getFormatedData(analDicEnd.y, m_ParentChart._cdm.getPriceFormat(), m_ParentChart._cdm);
				//price = price.replace(",", "");
				m_edEndDatePrice.setText(price);

				TextView tvLabel = (TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice_title", "id", m_context.getPackageName()));

				if(m_ParentChart.select_at.getTitle().equals("피보나치조정대"))
				{
					tvLabel.setText("구간 고저종");
				}
				else if(!m_ParentChart.select_at.getTitle().equals("추세선"))
				{
					tvLabel.setVisibility(View.GONE);
					m_chkMinMaxPrice.setVisibility(View.GONE);
				}
			}
			else
			{
				TextView tvLabel = (TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_enddate_title", "id", m_context.getPackageName()));
				tvLabel.setVisibility(View.GONE);
				tvLabel = (TextView)m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_enddate_price_title", "id", m_context.getPackageName()));
				tvLabel.setVisibility(View.GONE);

				m_tvEndDate.setVisibility(View.GONE);
				m_edEndDatePrice.setVisibility(View.GONE);

				if(!m_ParentChart.select_at.getTitle().equals("수평선")) {
					tvLabel = (TextView) m_thisLayout.findViewById(m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice_title", "id", m_context.getPackageName()));
					tvLabel.setVisibility(View.GONE);
					m_chkMinMaxPrice.setVisibility(View.GONE);
				}
			}
         	m_chkMinMaxPrice.setChecked(m_ParentChart.select_at.getUsePrice());
			//m_chkMinMaxPrice.setChecked(m_ParentChart._cvm.getUsePrice());
		}

		//색상 & 라인 버튼 설정
		NeoChart2 neoChart = this.m_ParentChart;

		int atColor[] = neoChart.select_at.getAtColor();
		int lineT = neoChart.select_at.getLineT();

		//색상 설정
		int layoutResId = this.getContext().getResources().getIdentifier("analcolortextview", "id", this.getContext().getPackageName());
		m_tvColorOpen = (TextView)m_thisLayout.findViewById(layoutResId);

		setButtonColorWithRound(m_tvColorOpen, Color.rgb(atColor[0], atColor[1], atColor[2]));

		m_tvColorOpen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showColorPalette(m_tvColorOpen);
			}
		});

		//라인 설정
		layoutResId = this.getContext().getResources().getIdentifier("analthicktextview", "id", this.getContext().getPackageName());
		m_tvLineOpen = (TextView)m_thisLayout.findViewById(layoutResId); //지표 설정 값.
		int lineImg = Lines[lineT-1];

		m_tvLineOpen.setBackgroundResource(lineImg);
		m_tvLineOpen.setTag(String.valueOf(lineT));

		m_tvLineOpen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLineSelect(m_tvLineOpen);
			}
		});

		COMUtil.setGlobalFont(m_thisLayout);
	}

	/**다이얼로그 크기를 설정
	 * @param nW : 너비
	 * @param nH : 높이
	 * */
	public void setDialogSize(int nW, int nH)
	{
		if(null != m_thisLayout)
		{
			this.setContentView(m_thisLayout, new ViewGroup.LayoutParams(nW, nH));
		}
	}

	/**컨트롤 클릭시*/
	@Override
	public void onClick(View v) {
		if(v.getId() == m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice", "id", m_context.getPackageName()))
		{
			//체크박스 눌렀을 때 


		}
		if(v.getId() == m_context.getResources().getIdentifier("analtoolsettingview_chk_minmaxprice_title", "id", m_context.getPackageName()))
		{
			//체크박스 눌렀을 때 
			m_chkMinMaxPrice.setChecked(!m_chkMinMaxPrice.isChecked());

		}
		else if(v.getId() == m_context.getResources().getIdentifier("btn_ok", "id", m_context.getPackageName()))
		{
			//확인버튼 눌렀을 때
			NeoChart2 neoChart = this.m_ParentChart;
			try
			{
				String strValue = m_edStartDatePrice.getText().toString();
				strValue = strValue.replace(",","");
				double dValue1 = Double.parseDouble(strValue);

				//입력받은 봉갯수를 차트에(정확히는 차트의 ChartViewModel) 설정한다.
				neoChart.select_at.changeValue(0, dValue1);
				if(neoChart.select_at.getPointCount()>1)
				{
					strValue = m_edEndDatePrice.getText().toString();
					strValue = strValue.replace(",","");
					double dValue2 = Double.parseDouble(strValue);
					neoChart.select_at.changeValue(1, dValue2);
				}
			    neoChart.select_at.setUsePrice(m_chkMinMaxPrice.isChecked());
				//neoChart._cvm.setUsePrice(m_chkMinMaxPrice.isChecked());
//			    neoChart.select_at.resetPoint();
				for(int i = 0; i < neoChart.analTools.size(); i++)
				{
					neoChart.analTools.get(i).resetPoint();
				}

				neoChart.saveAnalToolBySymbol();
			}catch(Exception e)
			{

			}
			//변경된 봉갯수와 인덱스 정보를 이용해서 차트를 다시 그린다.
			neoChart.repaintAll();
			cancel();
		}
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

	protected void showColorPalette(TextView tvColorOpen)
	{
		int colorTag = 1;
		try{
			String selColor = (String) tvColorOpen.getTag();
			colorTag = Integer.valueOf(selColor);
		}catch(Exception e){
			e.printStackTrace();
		}
		paletteDialog paletteDialog = new paletteDialog(m_context, 4, 6, tvColorOpen, colorTag);
		paletteDialog.setParent(this, "Anal");
		paletteDialog.setCanceledOnTouchOutside(false);
		//2020.05.08 by JJH >> 가로모드 작업 (분석도구 설정 팝업) start
		WindowManager.LayoutParams params = paletteDialog.getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		//2020.05.08 by JJH >> 가로모드 작업 (분석도구 설정 팝업) end
		paletteDialog.show();
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
		LineDialog lineDialog = new LineDialog(m_context, 5, 1, tvLineOpen, lineTag);
		lineDialog.setParent(this, "Anal");
		lineDialog.setCanceledOnTouchOutside(false);


		int[] location = new int[2];
		tvLineOpen.getLocationOnScreen(location);
		//드랍다운리스트뷰의 너비
		int nLineBtnWidth = tvLineOpen.getWidth();
		//드랍다운리스트뷰의 x축좌표
		int nLineBtnLeft = location[0]+(int)COMUtil.getPixel(3);
		//드랍다운리스트뷰의 y축좌표
		int nLineBtnTop = location[1]-(int)COMUtil.getPixel(32);
		WindowManager.LayoutParams params = lineDialog.getWindow().getAttributes();
		//2020.05.08 by JJH >> 가로모드 작업 (분석도구 설정 팝업) start
//		params.y = nLineBtnTop;
//		params.x = nLineBtnLeft - (tvLineOpen.getWidth()/4)*2;
//		params.gravity = Gravity.TOP|Gravity.START;
		params.gravity = Gravity.CENTER;
		//2020.05.08 by JJH >> 가로모드 작업 (분석도구 설정 팝업) end

		lineDialog.show();
	}


	public void updateValue() {
		mHandler.post(new Runnable() {
			public void run() {

				NeoChart2 neoChart = m_ParentChart;

				//선택된 컬러를 차트에 세팅
				String colorRGB = (String)m_tvColorOpen.getTag();

				int r = Color.red(Integer.parseInt(colorRGB));
				int g = Color.green(Integer.parseInt(colorRGB));
				int b = Color.blue(Integer.parseInt(colorRGB));
				int color[] = {r, g, b};

				neoChart.select_at.setColor(color);

				//선택된 라인을 차트에 세팅
				String lineThick = (String) m_tvLineOpen.getTag();
				int nSelectedIndex = (Integer.parseInt(lineThick));

				neoChart.select_at.setLineT(nSelectedIndex);

				//다시 그리기
				COMUtil._mainFrame.mainBase.baseP._chart.makeGraphData();
				COMUtil._mainFrame.mainBase.baseP._chart.repaintAll();
			}
		});
	}
}
