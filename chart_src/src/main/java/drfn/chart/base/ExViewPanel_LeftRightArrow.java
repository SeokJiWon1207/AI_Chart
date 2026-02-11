package drfn.chart.base;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.widget.RelativeLayout;

import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

public class ExViewPanel_LeftRightArrow extends ScrollViewPanel {
	//2015. 9. 21 자산관리 라인오실레이터차트 롱터치 수치조회창 디자인>>
	int layoutResId_L;
	int layoutResId_R;
	int layoutResId_Lower_L;
	int layoutResId_Lower_R;
	//2015. 9. 21 자산관리 라인오실레이터차트 롱터치 수치조회창 디자인<<
	
	boolean m_bIsRightDirection = true;
	boolean m_bIsLowerDirection = true;
	int m_nTooltipType = -1;
	int nColorLine = Color.rgb(147,81,246);
	int nColorRect = Color.rgb(255,130,229);
	
	public ExViewPanel_LeftRightArrow(Context context, RelativeLayout layout) {
		super(context, layout);

        //2015. 9. 21 자산관리 라인오실레이터차트 롱터치 수치조회창 디자인>>
		layoutResId_L = context.getResources().getIdentifier("graph_popup_left_bg", "drawable", context.getPackageName());
		layoutResId_R = context.getResources().getIdentifier("graph_popup_right_bg", "drawable", context.getPackageName());
		layoutResId_Lower_L = context.getResources().getIdentifier("graph_popup_lower_left_bg", "drawable", context.getPackageName());
		layoutResId_Lower_R = context.getResources().getIdentifier("graph_popup_lower_right_bg", "drawable", context.getPackageName());
		//2015. 9. 21 자산관리 라인오실레이터차트 롱터치 수치조회창 디자인<<
		
		setViewPanelRightDirection(m_bIsRightDirection, m_bIsLowerDirection);
		this.context = context;
	}

	public void setTooltipType(int nType)
	{
		m_nTooltipType = nType;
		if(m_nTooltipType == ChartViewModel.ASSET_LINE_MOUNTAIN)
		{
			layoutResId_L = context.getResources().getIdentifier("img_oneq_tooltip", "drawable", context.getPackageName());
			setBackgroundResource(layoutResId_L);
		}
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {

		if(m_nTooltipType == ChartViewModel.ASSET_LINE_MOUNTAIN)
			drawDataMountain(canvas, viewP.datas);
		else
			drawData(canvas, viewP.datas);
	}

	public void drawData(Canvas canvas,
						 Vector<Hashtable<String, String>> datas) {
		int ih = (int)COMUtil.getPixel_H(15);
		int nDateHeight = (int)COMUtil.getPixel_H(11);
		int igab = (int)COMUtil.getPixel_H(3);
		int fy = (int)COMUtil.getPixel_H(5);
		int xgab = (int)COMUtil.getPixel(0);
		//int rectsize = (int)COMUtil.getPixel(9);
//		if(!m_bIsRightDirection)
//			xgab = (int)COMUtil.getPixel(15);

		if(m_bIsLowerDirection)
		{
			fy = (int)COMUtil.getPixel_H(9);
		}
		if(datas == null)
		{
			return;
		}

		int topMargin = fy + nDateHeight;
		for(int i=0; i<datas.size(); i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
			Enumeration<String> enumStr = item.keys();
			String key="";
			key = enumStr.nextElement().toString();

			String name=key;
			String value=(String)item.get(key);

			//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09	
			int leftMargin = (int)COMUtil.getPixel_W(10);
			viewP.mPaint.setAntiAlias(true);
			viewP.mPaint.setAlpha(0xFF);
			viewP.mPaint.setTextAlign(Align.LEFT);
			if(i==0) {
				//rectsize = 0;
				//viewP.mPaint.setTextSize(COMUtil.getPixel(10));
				viewP.mPaint.setTextSize(COMUtil.getFontSize(-4));
				viewP.mPaint.setTypeface(COMUtil.numericTypeface);
				viewP.mPaint.setColor(Color.rgb(153, 153, 153));
			}
			else
			{
				//rectsize = (int)COMUtil.getPixel(13);
				//viewP.mPaint.setTextSize(COMUtil.getPixel(13));
				viewP.mPaint.setTextSize(COMUtil.getFontSize(-1));
				viewP.mPaint.setTypeface(COMUtil.numericTypefaceMid);
			}
			if(i==0) {
				name = name.replace("/", ".");
				canvas.drawText(name, leftMargin + xgab, topMargin, viewP.mPaint);
				continue;
			}

			if(i==1)
				topMargin += nDateHeight+igab+(int)COMUtil.getPixel_H(3);
			else
				topMargin += ih+igab;

			viewP.mPaint.setTextAlign(Align.RIGHT);
			int selCol = Color.rgb(255, 255, 255);
			viewP.mPaint.setColor(selCol);
			if(i!=0 && COMUtil._neoChart._cvm.getAssetType() == ChartViewModel.ASSET_LINE_FILL) {
				value = value + "원";
				viewP.mPaint.setTextAlign(Align.LEFT);
				canvas.drawText(value, leftMargin + xgab, topMargin, viewP.mPaint);
			}
		}
	}

	public void drawDataMountain(Canvas canvas,
						 Vector<Hashtable<String, String>> datas) {

		String strDate="";
		String strValue1="";
		String strValue2="";
		for(int i=0; i<datas.size(); i++) {
			Hashtable<String, String> item = (Hashtable<String, String>) datas.get(i);

			Enumeration<String> enumStr = item.keys();
			String key = "";
			key = enumStr.nextElement().toString();

			if(i==0)
				strDate = key;
			else if(i==1)
				strValue1 = item.get(key);
			else if(i==2)
				strValue2 = item.get(key);
		}

		int ih = (int)COMUtil.getPixel_H(15);
		int nDateHeight = (int)COMUtil.getPixel_H(11);
		int igab = (int)COMUtil.getPixel_H(3);
		int fy = (int)COMUtil.getPixel_H(14);
		int xgab = (int)COMUtil.getPixel(0);

		if(datas == null)
		{
			return;
		}

		int topMargin = fy + nDateHeight;


			//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09
		int leftMargin = (int)COMUtil.getPixel_W(24);
		viewP.mPaint.setAntiAlias(true);
		viewP.mPaint.setAlpha(0xFF);
		viewP.mPaint.setTextAlign(Align.LEFT);
		viewP.mPaint.setTextSize(COMUtil.getFontSize(-2));
		//viewP.mPaint.setTextSize(COMUtil.getPixel(13));
		viewP.mPaint.setTypeface(COMUtil.numericTypeface);
		viewP.mPaint.setColor(Color.rgb(255, 255, 255));

		float textWidth = viewP.mPaint.measureText(strDate, 0, strDate.length());
		canvas.drawText(strDate, leftMargin, topMargin, viewP.mPaint);

		leftMargin = leftMargin+(int)textWidth+(int)COMUtil.getPixel_W(23);
		viewP.mPaint.setStyle(Paint.Style.FILL);
//		viewP.mPaint.setColor(Color.rgb(224, 45, 35));
		viewP.mPaint.setColor(nColorLine);
		canvas.drawRect(new RectF(leftMargin-(int)COMUtil.getPixel_W(13), COMUtil.getPixel_H(19), leftMargin-(int)COMUtil.getPixel_W(13)+COMUtil.getPixel_W(8), COMUtil.getPixel_H(20)+COMUtil.getPixel_H(2)), viewP.mPaint);
		viewP.mPaint.setColor(Color.rgb(255, 255, 255));
		canvas.drawText(strValue1+"%", leftMargin , topMargin, viewP.mPaint);

		viewP.mPaint.setTextSize(COMUtil.getFontSize(-2));
//		textWidth = viewP.mPaint.measureText(strValue1+"%", 0, strValue1.length());
		textWidth = viewP.mPaint.measureText(strValue1+"%");

		leftMargin = leftMargin+(int)textWidth+(int)COMUtil.getPixel_W(21);
		viewP.mPaint.setStyle(Paint.Style.FILL);
		viewP.mPaint.setColor(nColorRect);
		canvas.drawRect(new RectF(leftMargin-(int)COMUtil.getPixel_W(11), COMUtil.getPixel_H(17), leftMargin-(int)COMUtil.getPixel_W(11)+COMUtil.getPixel_W(6), COMUtil.getPixel_H(18)+COMUtil.getPixel_H(6)), viewP.mPaint);
		viewP.mPaint.setColor(Color.rgb(255, 255, 255));
		canvas.drawText(strValue2+"(억원)", leftMargin , topMargin, viewP.mPaint);

		textWidth = viewP.mPaint.measureText(strValue2+"(억원)");

		int nWidth = (int)leftMargin + (int)textWidth + (int)COMUtil.getPixel_W(24);

				//rectsize = (int)COMUtil.getPixel(13);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(nWidth,  (int)COMUtil.getPixel_H(43));
		try {
			params.leftMargin = (int)((COMUtil._neoChart.chart_bounds.right-nWidth)/2);
		} catch (NullPointerException e) {
			params.leftMargin = 0;
		}
		this.setLayoutParams(params);
		layoutResId_L = context.getResources().getIdentifier("img_oneq_tooltip", "drawable", context.getPackageName());
		setBackgroundResource(layoutResId_L);
	}
	
	public void setViewPanelRightDirection(boolean bRightFlag, boolean bLowerFlag)
	{
		m_bIsRightDirection = bRightFlag;
		m_bIsLowerDirection = bLowerFlag;
		if(m_bIsLowerDirection)
		{
			if(m_bIsRightDirection)
			{
				setBackgroundResource(layoutResId_Lower_R);
			}
			else
			{
				setBackgroundResource(layoutResId_Lower_L);
			}
		}
		else {
			if (m_bIsRightDirection) {
				setBackgroundResource(layoutResId_R);
			} else {
				setBackgroundResource(layoutResId_L);
			}
		}
	}

	public float getTextWidth(String strValue)
	{
		viewP.mPaint.setTextSize(COMUtil.getFontSize(-1));
		viewP.mPaint.setTypeface(COMUtil.numericTypefaceMid);
		return viewP.mPaint.measureText(strValue, 0, strValue.length());
	}
}
