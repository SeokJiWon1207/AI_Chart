package drfn.chart.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.RelativeLayout;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

public class ExViewPanel extends ViewPanel {
	int layoutResId;

	boolean m_bIsRightDirection = true;

	public ExViewPanel(Context context, RelativeLayout layout) {
		super(context, layout);

		mPaint.setTextSize(COMUtil.nFontSize_paint - 4);
		setBackgroundResource(context.getResources().getIdentifier("img_handler_bg", "drawable", context.getPackageName()));
	}

	@Override
	public void setBounds(RectF rect) {
		this.bounds = rect;
		//2012. 7. 9  ViewPanel 이동. 아이폰의 y축 고정된 것을 보고 y축은 고정값을 줌 
		COMUtil._chartMain.runOnUiThread(new Runnable() {
			public void run() {
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int)bounds.width(), (int)bounds.height());
				rl.leftMargin =(int) bounds.left;
				rl.topMargin = (int)bounds.top;
				setLayoutParams(rl);

			}
		});
	}

	@Override
	public void drawData(Canvas canvas,
							Vector<Hashtable<String, String>> datas) {
		int ih = (int)COMUtil.getPixel(12);
		int igab = (int)COMUtil.getPixel(13);
		int fx = 0;
		int fy = 0;

		int leftMargin = fx+(int)COMUtil.getPixel(5);

		if(datas == null)
		{
			return;
		}

		for(int i=0; i<datas.size(); i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
			Enumeration<String> enumStr = item.keys();
			String key="";
			key = enumStr.nextElement().toString();

			String name=key;
			String value=(String)item.get(key);

			int topMargin = (int)COMUtil.getPixel(13);

			mPaint.setAntiAlias(true);
			mPaint.setAlpha(0xFF);
			mPaint.setTextAlign(Align.LEFT);
			mPaint.setColor(Color.rgb(11, 11, 11));	//text_grey0


			if(1 == i)	//종가 및 등락률일때는 색상 상승하락보합에 따라 바꾸기
			{
				String[] arToken = value.split("\\(");
				String strChgrate = arToken[1];

				if(strChgrate.length() >= 2)
				{
					//맨 끝   )   제거
					strChgrate = strChgrate.substring(0, strChgrate.length()-2);

					//색상정하기
					int nTextColor;
					if( Float.parseFloat(strChgrate) < 0)
					{
						nTextColor = CoSys.STEXT_DOWN;
					}
					else if(Float.parseFloat(strChgrate) > 0)
					{
						nTextColor = CoSys.STEXT_UP;
					}
					else
					{
						nTextColor = CoSys.STEXT_GREY0;
					}
					mPaint.setColor(nTextColor);
				}

			}

			canvas.drawText(value, leftMargin + COMUtil.getPixel(5), topMargin, mPaint);

			leftMargin += (int)COMUtil.getPixel(70);
		}
	}
}
