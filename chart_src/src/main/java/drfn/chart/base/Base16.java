package drfn.chart.base;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import drfn.chart.MainFrame;
import drfn.chart.draw.paletteDialog;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

/**
 * 포트폴리오 자산관리 비교차트 용 Base
 * @author 김승환 of kfits
 * @since 2015. 6. 5
 * @version 1.0
 * */
@SuppressLint("NewApi")
public class Base16 extends Base{
	/** bar차트가 추가될 메인쪽 layout */
	private RelativeLayout m_Layout;
	private Context m_Context = null;
	/** 그리기가 수행될 canvas 객체 */
	private Canvas m_Canvas = null;
	/** 그릴때 사용할 Paint  */
	private Paint m_Paint = null;
	/**사용할 Rect */
	private RectF m_RectforRect = null;
	/** 더블버퍼링용 bitmap */
	private Bitmap backbit = null;
	/** rect 객체 배열(Observer) */
	private ArrayList<Rect16> m_fir_Rect = null;
	private ArrayList<Rect16> m_sec_Rect = null;

	/** dragRect 객체 배열(Observer) */
	private RectF dragRect = new RectF();

	/** 차트 색상 */
	ArrayList<int[]> m_rectColors = null;
	/** 차트 데이터 */

	Point ptEventOccur = new Point();
	String funcName = "";

	int nGraphStart, nGraphEnd;

	List<String> m_strTitles = new ArrayList<String>();
	List<String> m_strFirstDatas = new ArrayList<String>();
	List<String> m_strSecondDatas = new ArrayList<String>();
	List<String> m_colorIndexes = new ArrayList<String>();

	private MainFrame mainFrame = null;

	private Paint mTitlePaint, mSubTitlePaint;

	public Base16(Context context , RelativeLayout layout) {
		super(context);

		//this.setBackgroundColor(Color.TRANSPARENT);
		m_Layout = layout;
		m_Context = context;
		m_Paint = new Paint();
		m_Paint.setAntiAlias(true);

		m_fir_Rect = new ArrayList<Rect16>();
		m_sec_Rect = new ArrayList<Rect16>();


		m_RectforRect = new RectF();

		//width = 720, height = 682
		m_RectforRect.left = 0;
		m_RectforRect.top = 0;

		RelativeLayout.LayoutParams lpMain = (RelativeLayout.LayoutParams)m_Layout.getLayoutParams();

		m_RectforRect.right = lpMain.width;
		m_RectforRect.bottom = lpMain.height;

		nGraphStart = 0;
		nGraphStart = (int)COMUtil.getPixel(100);
		nGraphEnd = (int)m_RectforRect.right;

		this.m_Layout.addView(this);

		initrectColor();

		//double buffering
		if(backbit!=null)
		{
			backbit.recycle();
			backbit = null;
		}

		if(m_Canvas!=null)
			m_Canvas = null;

		if(backbit==null) {
			backbit = Bitmap.createBitmap(	(int)(m_RectforRect.left+(m_RectforRect.right-m_RectforRect.left)),
					(int)(m_RectforRect.top+(m_RectforRect.bottom-m_RectforRect.top)), Bitmap.Config.RGB_565);
			m_Canvas = new Canvas(backbit);
		}

		mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTitlePaint.setTypeface(COMUtil.typeface);
		int resId = context.getResources().getIdentifier("text_grey2", "color", context.getPackageName());
		mTitlePaint.setColor(context.getResources().getColor(resId));

		mSubTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSubTitlePaint.setTypeface(COMUtil.typeface);
		resId = context.getResources().getIdentifier("text_grey4", "color", context.getPackageName());
		mSubTitlePaint.setColor(context.getResources().getColor(resId));
		mSubTitlePaint.setTextAlign(Align.RIGHT);

		//타이틀
		ImageView line = new ImageView (m_Context);
		int nId = this.getContext().getResources().getIdentifier("img_line_dot", "drawable", this.getContext().getPackageName());
		line.setBackgroundResource(nId);

		RelativeLayout.LayoutParams btnTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)COMUtil.getPixel(1));
		btnTitleParams.leftMargin = 0;
		btnTitleParams.topMargin = (int)(m_RectforRect.height()/2);
		line.setLayoutParams(btnTitleParams);
		m_Layout.addView(line);
	}
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(backbit==null) {
			backbit = Bitmap.createBitmap(	this.getWidth(),
					this.getHeight(), Bitmap.Config.RGB_565);
			m_Canvas = new Canvas(backbit);
		}
		drawChart(canvas);
	}

	@Override
	public void init() {
		super.init();

//		setPanel();	//추후 외부객체에서 접근해야하는 인터페이스 필요한 경우 구조변경할 예정
	}

	/**
	 * 차트 그래프들을 그림. 
	 * @param c Base14 의 Canvas
	 * */
	private void drawChart(Canvas c)
	{

		m_Canvas.drawColor(Color.rgb(250,250,250));
		m_Paint.setStyle(Style.FILL);
		m_Paint.setColor(Color.rgb(0,0,0));

		int containIndex = -1;
		int containIndex2 = -1;

		//int totWidth = (int)m_RectforRect.width();
		int nTop = (int)COMUtil.getPixel(0);
		int nHeight = 0;

		if(m_strTitles.size() == 0)
		{
			return;
		}

		if(m_fir_Rect.size()>0) {
			nHeight = (int)((m_RectforRect.height()-nTop*2)/6);
			mTitlePaint.setTextSize((int)COMUtil.getPixel(13));
			mSubTitlePaint.setTextSize((int)COMUtil.getPixel(10));

			String mTitle = String.format("%s", m_strTitles.get(0));
			m_Canvas.drawText(mTitle, 0, (int)(nTop+COMUtil.getPixel(5)+1*nHeight), mTitlePaint);

			if(m_strTitles.size() > 1)
			{
				mTitle = String.format("%s", m_strTitles.get(1));
				m_Canvas.drawText(mTitle, 0, (int) (nTop + COMUtil.getPixel(5) + 5 * nHeight), mTitlePaint);
			}

			for(int i = 0; i < m_fir_Rect.size(); i++)
			{
				Rect16 rect = m_fir_Rect.get(i);

				drawRect((int)rect.getStart(), (int)(nTop+i*nHeight), (int)(rect.getEnd()-rect.getStart()), (int)nHeight, Color.parseColor(rect.getRectColor()), 1);

				//데이터 추출
				if(ptEventOccur.x>=rect.getStart() && ptEventOccur.x<=rect.getEnd()) {
					containIndex = i;
				}
				String mSubTitle = COMUtil.format(String.format("%.2f", rect.getPercent() * 100), 2, 3);//String.format("%.2f", rect.getPercent()*100);
				if(Float.parseFloat(mSubTitle.replace(",", "")) >= 0)
				{
					mSubTitlePaint.setColor(Color.rgb(92, 96, 102));
				}
				else
				{
					mSubTitlePaint.setColor(Color.rgb(228, 63, 66));
				}
				m_Canvas.drawText(mSubTitle, m_RectforRect.right - COMUtil.getPixel(2) , (int)(nTop+(i+1)*nHeight), mSubTitlePaint);
			}
		}

		if(m_sec_Rect.size()>0) {
			for(int i = 0; i < m_sec_Rect.size(); i++)
			{
				Rect16 rect = m_sec_Rect.get(i);

				drawRect((int)rect.getStart(), (int)(nTop+(i+4)*nHeight), (int)(rect.getEnd()-rect.getStart()), (int)nHeight, Color.parseColor(rect.getRectColor()), 1);

				//데이터 추출
				if(ptEventOccur.x>=rect.getStart() && ptEventOccur.x<=rect.getEnd()) {
					containIndex2 = i;
				}
				String mSubTitle = COMUtil.format(String.format("%.2f", rect.getPercent() * 100), 2, 3);//String.format("%.2f", rect.getPercent()*100);
				if(Float.parseFloat(mSubTitle.replace(",", "")) >= 0)
				{
					mSubTitlePaint.setColor(Color.rgb(92, 96, 102));
				}
				else
				{
					mSubTitlePaint.setColor(Color.rgb(228, 63, 66));
				}
				m_Canvas.drawText(mSubTitle, m_RectforRect.right - COMUtil.getPixel(2), (int)(nTop+(i+5)*nHeight), mSubTitlePaint);
			}
		}
		c.drawBitmap(backbit, 0,0, null);
	}

	public void drawLine(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha) {
		m_Paint.setColor(Color.rgb(color[0],color[1],color[2]));
		m_Paint.setAlpha((int)(alpha*255));
		m_Paint.setAntiAlias(true);
		m_Paint.setStyle(Style.STROKE);
		gl.drawLine(x1, y1, x2, y2, m_Paint);
		m_Paint.setAlpha(255);
	}
	public void drawRect(int nX, int nY, int nWidth, int nHeight, int color, float alpha)
	{
		m_Paint.setColor(color);
		if(Math.abs(nWidth)>COMUtil.getPixel(3))
		{
			if(nWidth<0)
			{
				m_Canvas.drawRect(nX+nWidth+COMUtil.getPixel(2), nY, nX, nY+nHeight, m_Paint);
				m_Canvas.drawRoundRect(new RectF(nX+nWidth, nY, nX, nY+nHeight), COMUtil.getPixel(2), COMUtil.getPixel(2), m_Paint);
			}
			else {
				m_Canvas.drawRect(nX, nY, nX + nWidth - COMUtil.getPixel(2), nY + nHeight, m_Paint);
				m_Canvas.drawRoundRect(new RectF(nX, nY, nX + nWidth, nY + nHeight), COMUtil.getPixel(2), COMUtil.getPixel(2), m_Paint);
			}
		}
		else
		{
			m_Canvas.drawRect(nX, nY, nX+nWidth, nY+nHeight, m_Paint);
		}
	}
//	public void drawStringWithSize(Canvas gl, int[] color, int x, int y, float size, String str) {
//		m_Paint.setStyle(Paint.Style.FILL);
//		m_Paint.setColor(Color.rgb(color[0],color[1],color[2]));
//		m_Paint.setTextAlign(Align.LEFT);
//		m_Paint.setTextSize(size);
//		gl.drawText(str, x, y+COMUtil.getPixel(4), m_Paint);
//		m_Paint.setTextSize(size);
//    }

	//API 차트크기변경 
	public void resizeChart(View view) {

	}
	public void setPacketData_hashtable(Hashtable<String, Object> data)
	{
		//groupid에 따라서 Color Setting 할 것 
		m_strTitles = (List<String>)data.get("datas1");
		m_strFirstDatas = (List<String>)data.get("datas2");
		m_strSecondDatas = (List<String>)data.get("datas3");
		m_colorIndexes = (List<String>)data.get("datas4");


		//배열 초기화
		if(m_fir_Rect.size() > 0)
		{
			m_fir_Rect.clear();
		}

		if(m_sec_Rect.size() > 0)
		{
			m_sec_Rect.clear();
		}

		setRectInfo(m_strFirstDatas, m_fir_Rect, 0, false);
		setRectInfo(m_strSecondDatas, m_sec_Rect, 0, false);

		invalidate();
	}

	public void initrectColor()
	{
		if(null == m_rectColors)
		{
			m_rectColors = new ArrayList<int[]>();
		}

		if(null != m_rectColors)
		{
			m_rectColors.clear();
		}
		for(int i=0; i<CoSys.MULTIBAR_COLOR_FOR_ASSET.length; i++)
		{
			m_rectColors.add(CoSys.MULTIBAR_COLOR_FOR_ASSET[i]);
		}

	}


	private void setRectInfo(List<String> strDatas, ArrayList<Rect16> arrRect, float fStart, boolean bIsInfoReset)
	{
		if(strDatas.size()<1)
			return;

		//데이터의 전체 합 구하기
		float fTotal = 100.0f;
		float fMax = 0.0f;
		float fNextStart = nGraphStart;
		int nMinusCount = 0;

		for(int j = 0; j < strDatas.size(); j++)
		{
			float fData = Float.parseFloat(strDatas.get(j));
			if(fMax == 0 || fMax<Math.abs(fData))
				fMax = Math.abs(fData);

			if(fData<0)
				nMinusCount++;
		}

		nGraphEnd = (int)m_RectforRect.right-(int)COMUtil.getPixel(60);
		if(nMinusCount==1)
			nGraphEnd = (nGraphEnd+nGraphStart)/2;

		int nColorIdx = 0;
		for(int j = 0; j < strDatas.size(); j++)
		{
			float fPercent = 0;
			try {
				fPercent =Float.parseFloat(strDatas.get(j))/fTotal;
			} catch (Exception e) {

			}

			try {
				nColorIdx = Integer.parseInt(m_colorIndexes.get(j)); //외부 그룹 인덱스
			} catch (Exception e) {

			}

			String rectColor = String.format("#%08X", 	0xFFFFFFFF &
					Color.argb(255, m_rectColors.get(nColorIdx)[0], m_rectColors.get(nColorIdx)[1], m_rectColors.get(nColorIdx)[2]));
			if(++nColorIdx >= m_rectColors.size())
			{
				nColorIdx = 0;
			}

			Rect16 rect;

			//int fEnd = (int)(fNextStart + (nGraphEnd-nGraphStart) * fPercent);
			if(nMinusCount>0)
			{
				fNextStart = nGraphEnd;
			}

			int fEnd;
			if(fMax != 0)
			{
				fEnd = (int)(fNextStart + (nGraphEnd-nGraphStart) * Float.parseFloat(strDatas.get(j))/fMax);
				if(fEnd==fNextStart)
				{
					if(fPercent>0)
						fEnd++;
					else if(fPercent<0)
						fEnd--;
				}
			}
			else
				fEnd = (int)fNextStart;

			if(!bIsInfoReset)
			{
				//정보를 최초 설정시엔 객체를 생성한다. 
				rect = new Rect16(m_Context, fNextStart, fEnd, fPercent, rectColor);
			}
			else
			{
				//정보 재설정시엔 이미 생성된 객체에 값만 바꿔준다
				rect = arrRect.get(j).resetRectData(fNextStart, fEnd, fPercent, rectColor);
			}
//
			if(!bIsInfoReset)
			{
				//최초 설정시엔 TextView 가 추가가 되어있지 않기 때문에 추가해준다
				arrRect.add(rect);
				//this.m_Layout.addView(rect.getRectTextView());
			}
			//fNextStart = fEnd;
			//nGraphStart = fEnd;
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		if(null != backbit)
		{
			backbit.recycle();
			backbit = null;
		}

		if(null != m_Canvas)
		{
			m_Canvas = null;
		}

		m_fir_Rect.clear();
		m_fir_Rect = null;
		m_sec_Rect.clear();
		m_sec_Rect = null;
	}
}

class Rect16 {
	/** 시작점 */
	private float m_fStart;
	/** 끝점 */
	private float m_fEnd;
	/** 비율 */
	private float m_fPercent;
	/** 색상  */
	private String m_strColor;
	Context m_Context;
	/** 글자를 쓰기 위한 TextView  */
//	private RectTextView m_tv_RectText;

	/**
	 *  객체
	 * @param context 부모의 Context
	 * @param strPrice  가격정보
	 * @param fStart 시작점
	 * @param fSweep 범위
	 * @param dPercent 비율
	 * @param strColor 색상 (#aarrggbb)
	 * */
	public Rect16(Context context, float fStart, float fEnd, float fPercent, String strColor) {
		m_fStart = fStart;
		m_fEnd = fEnd;
		m_strColor = strColor;
		m_Context = context;
		m_fPercent = fPercent;
		//m_tv_RectText = new RectTextView(m_Context);
	}

	public Rect16 resetRectData(float fStart, float fEnd, float fPercent, String strColor)
	{
		m_fStart = fStart;
		m_fEnd = fEnd;
		m_fPercent = fPercent;
		m_strColor = strColor;
		return this;
	}

	public float getPercent() {
		return m_fPercent;
	}
	public float getStart() {
		return m_fStart;
	}
	public float getEnd()
	{
		return m_fEnd;
	}
	public String getRectColor()
	{
		return m_strColor;
	}
	public void setRectColor(String strColor)
	{
		m_strColor = strColor;
	}
}
