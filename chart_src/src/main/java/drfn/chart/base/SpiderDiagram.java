package drfn.chart.base;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import drfn.chart.MainFrame;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.piechart.extra.UiUtils;


public class SpiderDiagram extends View {

	public final String TAG = this.getClass().getSimpleName();

	/** 더블버퍼링용 bitmap */
	private Bitmap backbit = null;

	/** 그리기가 수행될 canvas 객체 */
	private Canvas m_Canvas = null;

	public Rect chart_bounds = new Rect();

	private MainFrame mainFrame = null;

	private double _dCenterX, _dCenterY, _dX, _dY;

	public static int  POINT_COUNT	= 8;

//	public static int[]  POINT_LINE_COLOR =	{195,59,60};
//	public static int[]  POINT_ROUND_COLOR = {180,100,100};
	public static int[]  POINT_LINE_COLOR =	{224, 45, 35};
	public static int[]  POINT_ROUND_COLOR = {180, 100, 100};

	public static int[][]  POINT_SCOPE_COLORS = {{61,205,224}, {55,184,201}, {96,72,202}, {96,72,202}, {123,103,207}, {137,115,231}, {100,215,215}, {100,215,215},};

//	public static int[][]  POINT_CIRCLE_COLORS =	{{100,215,215}, {100,215,215}, {137,115,231}, {137,115,231}, {137,115,231}, {137,115,231}, {137,115,231}, {100,215,215},};
	//public static int[]  LEVEL_LINE_COLOR = {234,234,234};
	//public static int[]  LEVEL_LINE_COLOR = {53,133,221};
	public static int[]  LEVEL_LINE_COLOR = {221,221,221};

	int m_nScale = 1;
	private int m_nGapLeft = 40*m_nScale;
	private int m_nGapTop = 25*m_nScale;



	public static final int CHART_HIDDEN = 0;
	public static final int CHART_SHOWING = 1;
	public static final int CHART_INVALID = 2;

	/** X-coordinate of the down event */
	private int mTouchStartX;

	/** Y-coordinate of the down event */
	private int mTouchStartY;


	/** The diameter of the chart */
	private int mChartDiameter;

	/** The center point of the chart */
	private PointF mCenter = new PointF();

	/** The current degrees of rotation of the chart */
	private float mRotationDegree = 156; //최초 시작할 각도(디폴트:topright)

	private boolean mChartHidden = false;

	private boolean mLoaded = false;

	//private List<SpiderSliceDrawable> mDrawables;

//	private LinkedList<SpiderSliceDrawable> mRecycledDrawables;

	private ArrayList<InvestData> m_arrInvestData;
	private ArrayList<InvestData> m_arrPoint;

	InvestData m_pSelItem;
	InvestData m_pMoveItem, m_moveItem;

	//	//2015. 8. 5 파이차트 수정사항 : 정보 표시 영역 >>
//	private CenterCircleInfoDrawable mCenterCircleInfoDrawable;
//	private InfoDrawable mInfoDrawable;
//	private ArrowDrawable mArrowDrawable;
//	//2015. 8. 5 파이차트 수정사항 : 정보 표시 영역 <<
	private Bitmap mDrawingCache;

	private Paint mPaint;
	private Path mPath;
	private Paint mInfoPaint;

	private Paint mTextPaint;

	private boolean m_bIsMoving = false;	//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해
	private String m_strInvest="3:3:3:3:3:3:3:3";


	public Bitmap getDrawingCache() {
		return mDrawingCache;
	}


	public SpiderDiagram(Context context) {
		super(context);

		init();
	}

	public SpiderDiagram(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public SpiderDiagram(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private void init() {
		initPaints();
		//double buffering
		if(backbit!=null)
		{
			backbit.recycle();
			backbit = null;
		}

		if(m_Canvas!=null)
			m_Canvas = null;

		//double buffering end

		m_nGapLeft = (int)COMUtil.getPixel(48)*m_nScale;
		m_nGapTop = (int)COMUtil.getPixel(25)*m_nScale;

		m_arrPoint = new ArrayList<InvestData>();
		m_arrInvestData = new ArrayList<InvestData>();
	}

	public void setBounds(int left, int top, int right, int bottom){
		chart_bounds = new Rect(left, top, right, bottom); //left, top, right, bottom

		int nRadiusX = right-left;
		int nRadiusY = bottom-top;
		if(nRadiusX<nRadiusY)
		{
			mCenter.x = (float) nRadiusX / 2;
			mCenter.y = (float) nRadiusX / 2;

			mChartDiameter = nRadiusX;
		}
		else
		{
			mCenter.x = (float) nRadiusY / 2;
			mCenter.y = (float) nRadiusY / 2;

			mChartDiameter = nRadiusY;
		}

		if(backbit!=null) {
			backbit.recycle();
			backbit = null;
		}

		if(m_Canvas!=null)
			m_Canvas = null;

		backbit = Bitmap.createBitmap(right,bottom, Bitmap.Config.ARGB_8888);
		m_Canvas = new Canvas(backbit);

		SetDataInit();
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		System.out.println("============Debug_onDraw");
		super.onDraw(canvas);
		if(backbit==null) {
			backbit = Bitmap.createBitmap(chart_bounds.right,chart_bounds.bottom, Bitmap.Config.ARGB_8888);
			m_Canvas = new Canvas(backbit);
		}

//		m_Canvas.drawColor(Color.GREEN);

//		mPaint.setStyle(Style.FILL);
//		mPaint.setColor(Color.rgb(0,0,0));
//		m_Canvas.drawLine(Color.CYAN, 10, 100, 50, mPaint);

		// Update our animator objects

//		// If the drawing cache is null build it
//		if (mDrawingCache == null) {
//			buildDrawingCache1();
//			//double buffering
//			canvas.drawBitmap(mDrawingCache, 0,0, null);
//		}

		synchronized (this) {
			//updateAnimators();
			//m_Canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
//			m_Canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
//			this.doDraw(m_Canvas);
			DrawItem(m_Canvas, chart_bounds);
		}

		//double buffering
		canvas.drawBitmap(backbit, 0,0, null);

	}

	private void initPaints() {

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
//		mPaint.setAlpha(70);
		mPaint.setAlpha(255);

		mPath = new Path();

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.rgb(60, 60, 60));
		mTextPaint.setTypeface(COMUtil.typeface);
		mTextPaint.setTextSize(COMUtil.getPixel(12));

		//2015. 8. 5 파이차트 수정사항>> : 부채꼴 정보 표시  
		mInfoPaint = new Paint();
		mInfoPaint.setColor(Color.WHITE);
		//2015. 8. 5 파이차트 수정사항<<
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	//	/**
//	 * 파이영역에서 메인 이벤트를 사용하지 않도록 설정하기 위함
//	 */
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev)
//	{
//		super.dispatchTouchEvent(ev);
//
//		if(ev.getAction() == MotionEvent.ACTION_MOVE)
//		{
//			if(mainFrame.userProtocol != null) {
//				Hashtable<String, Object> dic = new Hashtable<String, Object>();
//				dic.put("levelDatas", GetResultData());
//				mainFrame.userProtocol.requestInfo(COMUtil._TAG_DRAW_SPIDER, dic);
//	        }
//		}
//
//		return true;
//	}
	boolean m_bDragMode = false;
	Point m_ptClick = new Point();
	@Override
	public boolean onTouchEvent(final MotionEvent event) {

//        if ((!inCircle((int) event.getX(), (int) event.getY()) && 
//				mTouchState == TOUCH_STATE_RESTING)) {
//        	return false;
//        }
//        
		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				//startTouch(event);
				m_bIsMoving = false;
				InvestData pConditionItem = FindPoint(new Point((int)event.getX(), (int)event.getY()));
				if(pConditionItem != null)
				{
					m_pMoveItem = pConditionItem;

					if(m_moveItem == null)
						m_moveItem = new InvestData();

					if(m_pMoveItem != null)
					{
						m_moveItem.m_strTitle = pConditionItem.m_strTitle;
						m_moveItem.m_nType = pConditionItem.m_nType;
						m_moveItem.m_nValue = pConditionItem.m_nValue;
						m_moveItem.m_nRadius = pConditionItem.m_nRadius;
						m_moveItem.m_rectItemArea = new Rect();
						m_moveItem.m_rectItemArea.set(pConditionItem.m_rectItemArea.left, pConditionItem.m_rectItemArea.top,
								pConditionItem.m_rectItemArea.right,
								pConditionItem.m_rectItemArea.bottom);
					}
					else
						m_moveItem.m_nValue = -1;
				}
				else
					m_pMoveItem = null;

				m_bDragMode = true;
				m_ptClick.set((int)event.getX(), (int)event.getY());
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				if(Math.abs(mTouchStartX - (int)event.getX()) > UiUtils.getDynamicPixels(getContext(), 5) || Math.abs(mTouchStartY - (int)event.getY()) > UiUtils.getDynamicPixels(getContext(), 5))
				{
					m_bIsMoving = true;
				}

				if(m_moveItem!=null && m_bDragMode && m_bIsMoving)
				{
					double dDiffX = event.getX() - m_ptClick.x;
					double dDiffY = event.getY() - m_ptClick.y;
					Rect rcClient = chart_bounds;
					//int nWidth = (rcClient.width()/2 - m_nGapLeft)/5;
					double dDistance = Math.sqrt(Math.pow((double)(event.getX() - m_ptClick.x), 2) + Math.pow((double)(event.getY() - m_ptClick.y), 2));
					int nGapLeft, nGapTop;

					int nRadius;

					boolean bPlus = true;
					switch(m_moveItem.m_nType)
					{
						case 0:
							if(dDiffY>0)
								bPlus = false;
							break;
						case 1:
							if(dDiffX<0 && dDiffY>0)
								bPlus = false;
							break;
						case 2:
							if(dDiffX<0)
								bPlus = false;
							break;
						case 3:
							if(dDiffX<0 && dDiffY<0)
								bPlus = false;
							break;
						case 4:
							if(dDiffY<0)
								bPlus = false;
							break;
						case 5:
							if(dDiffX>0 && dDiffY<0)
								bPlus = false;
							break;
						case 6:
							if(dDiffX>0)
								bPlus = false;
							break;
						case 7:
							if(dDiffX>0 && dDiffY>0)
								bPlus = false;
							break;
					}
					if(bPlus)
						nRadius = (int)(m_moveItem.m_nRadius+dDistance*2);
					else
						nRadius = (int)(m_moveItem.m_nRadius-dDistance*2);


					int nRadiusMax = rcClient.width()-m_nGapLeft/m_nScale*2;
					int nRadiusMin = nRadiusMax/5;
					if(nRadius>nRadiusMax) nRadius = nRadiusMax;
					if(nRadius<nRadiusMin) nRadius = nRadiusMin;
					m_moveItem.m_nValue = (5-(nRadius-nRadiusMin/2)/nRadiusMin);

//            		nGapTop = (rcClient.height()-nRadius)/2;
//            		nGapLeft = nGapTop+(m_nGapLeft-m_nGapTop)/m_nScale;
					nGapLeft = (rcClient.width()-nRadius)/2;
					nGapTop = nGapLeft+(m_nGapTop-m_nGapLeft)/m_nScale;

					GetPosition(m_moveItem.m_nType, nRadius);
					m_moveItem.m_rectItemArea.left = (int)_dX+nGapLeft;
					m_moveItem.m_rectItemArea.top = (int)_dY+nGapTop;

					invalidate();
				}

				break;

			case MotionEvent.ACTION_UP:
				m_bIsMoving = true;
				Rect rcClient = chart_bounds;
				Rect rcTitle;
				int nWidth = (rcClient.width()/2 - m_nGapLeft/m_nScale)/5;

				if(m_moveItem!=null && m_moveItem.m_nType>=0)
				{
					for(int i=0; i<m_arrInvestData.size(); i++)
					{
						InvestData pData = m_arrInvestData.get(i);
						if(pData.m_nType == m_moveItem.m_nType)
						{
							int nValue = m_moveItem.m_nValue;
							pData.m_nValue = nValue;

							int nGapLeft = m_nGapLeft/m_nScale+(nValue-1)*nWidth;
							int nGapTop =  m_nGapTop/m_nScale+(nValue -1)*nWidth;
							double nRadius = rcClient.width()-nGapLeft*2;

							GetPosition(pData.m_nType, (int)nRadius);
							_dX += nGapLeft;
							_dY += nGapTop;

							rcTitle = new Rect();
							rcTitle.left = (int)_dX;
							rcTitle.top = (int)_dY;
							rcTitle.right = rcTitle.left;
							rcTitle.bottom = rcTitle.top;
							pData.m_rectItemArea = rcTitle;
							pData.m_nRadius = (int)nRadius;
							break;
						}
					}
					invalidate();
				}
				if(m_moveItem!=null)
					m_moveItem.m_nType = -1;
				m_bDragMode = false;

				if(mainFrame.userProtocol != null) {
					Hashtable<String, Object> dic = new Hashtable<String, Object>();
					dic.put("levelDatas", GetResultData());
					mainFrame.userProtocol.requestInfo(COMUtil._TAG_DRAW_SPIDER, dic);
				}
				break;

			default:
				//endTouch(event.getX(), event.getY(), 0);
				break;
		}

		return true;
	}


	/**
	 * Sets the adapter that will provide the data for this
	 * Pie Chart.
	 *
	 * @param adapter The PieChart adapter
	 */
	public void setAdapter(String strData) {

		//SetData("3:2:4:5:1:1:3:2:", true);
//        setLoaded(true);
	}

	boolean DrawItem(Canvas canvas, Rect rcClient)
	{
		rcClient.right = rcClient.width()*m_nScale;
		rcClient.bottom = rcClient.height()*m_nScale;

		drawFillRect(canvas, rcClient.left, rcClient.top, rcClient.width(), rcClient.height(), CoSys.WHITE, 1);

		Rect rcTitle = new Rect();

		for(int i=0; i<POINT_COUNT; i++)
		{
			DrawLine(canvas, rcClient, i);
		}

		int nWidth = (rcClient.width()/2 - m_nGapLeft)/5;
		String strTitle;

		rcTitle.top = m_nGapTop + nWidth*5;
		//rcTitle.left = m_nGapLeft + nWidth*5;

		//pDC->SetTextColor(RGB(100,100,100));
		mTextPaint.setColor(Color.rgb(102,102,102));
		mTextPaint.setTextSize(COMUtil.getPixel(10));
		for(int i=0; i<5; i++)
		{
			if(i==0)
				DrawLevelLine(canvas, rcClient, m_nGapLeft+i*nWidth, m_nGapTop+i*nWidth, (float)1.0);
			else
				DrawLevelLine(canvas, rcClient, m_nGapLeft+i*nWidth, m_nGapTop+i*nWidth, (float)1.0);
			strTitle = String.format("%d", (5-i));
			rcTitle.left = (m_nGapLeft + rcClient.width())/2 + i*nWidth;
			canvas.drawText(strTitle, rcTitle.left - COMUtil.getPixel(2)*m_nScale, rcTitle.top + COMUtil.getPixel(10)*m_nScale, mTextPaint);
		}
		mTextPaint.setTextSize(COMUtil.getPixel(12));

		DrawOval(canvas, rcClient);
		DrawPointLine(canvas, rcClient);


		return true;
	}

	void DrawLine(Canvas canvas, Rect rcClient, int nType)
	{
		int nRadius = rcClient.width()-m_nGapLeft*2;

		GetPosition(nType, nRadius);

		drawLine(canvas, (float)(_dCenterX+m_nGapLeft), (float)(_dCenterY+m_nGapTop), (float)(_dX+m_nGapLeft), (float)(_dY+m_nGapTop), LEVEL_LINE_COLOR, (float)1.0, 0);

		String strTitle = GetTitle(nType);
		Rect rcTitle = new Rect();
		GetPosition(nType, nRadius+m_nGapLeft);
		rcTitle.left = (int)_dX;
		GetPosition(nType, nRadius+m_nGapTop);
		rcTitle.top = (int)_dY;
		mTextPaint.setColor(Color.rgb(102,102,102));
		if(nType == 0)
			canvas.drawText(strTitle, rcTitle.left + COMUtil.getPixel_W(2)*m_nScale, rcTitle.top + COMUtil.getPixel_H(17)*m_nScale, mTextPaint);
		else
			canvas.drawText(strTitle, rcTitle.left + COMUtil.getPixel_W(5)*m_nScale, rcTitle.top + COMUtil.getPixel_H(17)*m_nScale, mTextPaint);
	}

	void DrawLevelLine(Canvas canvas, Rect rcClient, int nGapLeft, int nGapTop, float alpha)
	{
		int nRadius = rcClient.width()-nGapLeft*2;

		int nType=0;
		float dStartX=0, dStartY=0;
		for(int i= 0; i<=POINT_COUNT; i++)
		{
			nType = i;
			GetPosition(nType, nRadius);
			if(i>0)
			{
				drawLine(canvas, dStartX, dStartY, (float)(_dX+nGapLeft), (float)(_dY+nGapTop), LEVEL_LINE_COLOR, alpha, 0);
			}
			dStartX = (float)(_dX+nGapLeft);
			dStartY = (float)(_dY+nGapTop);
		}

	}

	void DrawPointLine(Canvas canvas, Rect rcClient)
	{

		if(m_arrInvestData == null || m_arrInvestData.size()<8)
			return;

		int nWidth = (rcClient.width()/2 - m_nGapLeft)/5;
		int nGapLeft, nGapTop=0;

		double dX, dY;

		int nRadius = 0;

		int nType = 0;
		InvestData pData = null;

		float dStartX=0, dStartY=0;

		float dCenter;
		dCenter = (rcClient.width()-m_nGapLeft*2)/2;

		for(int i= 0; i<=POINT_COUNT; i++)
		{
			if(i==POINT_COUNT)
			{
				pData = m_arrInvestData.get(0);
				nType = 0;
			}
			else
			{
				pData = m_arrInvestData.get(i);
				nType = i;
			}

			if(m_moveItem!=null && m_moveItem.m_nType == nType)
			{
				dX = m_moveItem.m_rectItemArea.left*m_nScale;
				dY = m_moveItem.m_rectItemArea.top*m_nScale;
				if(i>0)
				{
					//drawLine(canvas, dStartX, dStartY, (float)(dX), (float)(dY), POINT_SCOPE_COLORS[i], 1);
					//drawTriangle(canvas, dStartX, dStartY, (float)(dX), (float)(dY), (float)(m_nGapLeft+dCenter), (float)(m_nGapTop+dCenter), POINT_SCOPE_COLORS[i-1]);
					//drawLine(canvas, (float)(dX), (float)(dY), (float)(m_nGapLeft+dCenter), (float)(m_nGapTop+dCenter), CoSys.WHITE, (float)0.4);
					drawLine(canvas, (float)(dX), (float)(dY), (float)dStartX, (float)dStartY, POINT_LINE_COLOR, (float)1.0, COMUtil.getPixel(2));
				}
				dStartX = (float)(dX);
				dStartY = (float)(dY);
			}
			else
			{
				nGapLeft = m_nGapLeft+(pData.m_nValue-1)*nWidth;
				nGapTop = m_nGapTop+(pData.m_nValue-1)*nWidth;

				nRadius = rcClient.width()-nGapLeft*2;

				GetPosition(nType, nRadius);

				if(i>0)
				{
					//drawLine(canvas, dStartX, dStartY, (float)(_dX+nGapLeft), (float)(_dY+nGapTop), POINT_SCOPE_COLORS[i-1], 1);
					//drawTriangle(canvas, dStartX, dStartY, (float)(_dX+nGapLeft), (float)(_dY+nGapTop), (float)(m_nGapLeft+dCenter), (float)(m_nGapTop+dCenter), POINT_SCOPE_COLORS[i-1]);
					//drawLine(canvas, (float)(_dX+nGapLeft), (float)(_dY+nGapTop), (float)(m_nGapLeft+dCenter), (float)(m_nGapTop+dCenter), CoSys.WHITE, (float)0.4);
					drawLine(canvas, (float)(_dX+nGapLeft), (float)(_dY+nGapTop), (float)dStartX, (float)dStartY, POINT_LINE_COLOR, (float)1.0, COMUtil.getPixel(2));
				}
				dStartX = (float)(_dX+nGapLeft);
				dStartY = (float)(_dY+nGapTop);
			}
		}
//
		for(int i= 0; i<POINT_COUNT; i++)
		{
			pData = m_arrInvestData.get(i);
			nGapLeft = m_nGapLeft+(pData.m_nValue-1)*nWidth;
			nGapTop =  m_nGapTop+(pData.m_nValue -1)*nWidth;
			nRadius = rcClient.width()-nGapLeft*2;

			nType = i;
			GetPosition(nType, nRadius);
			int nCircle = (int)COMUtil.getPixel(4)*m_nScale;
			//drawCircle(canvas, (int)_dX+nGapLeft-nCircle, (int)_dY+nGapTop-nCircle, (int)_dX+nGapLeft+nCircle, (int)_dY+nGapTop+nCircle, true, POINT_CIRCLE_COLORS[i]);
			drawCircle(canvas, (int)_dX+nGapLeft-nCircle, (int)_dY+nGapTop-nCircle, (int)_dX+nGapLeft+nCircle, (int)_dY+nGapTop+nCircle, true, POINT_LINE_COLOR);

			//nCircle = (int)COMUtil.getPixel(2)*m_nScale;
			//drawCircle(canvas, (int)_dX+nGapLeft-nCircle, (int)_dY+nGapTop-nCircle, (int)_dX+nGapLeft+nCircle, (int)_dY+nGapTop+nCircle, true, CoSys.WHITE);

		}

		if(m_moveItem != null && m_moveItem.m_nType>=0)
		{
			dX = m_moveItem.m_rectItemArea.left*m_nScale;
			dY = m_moveItem.m_rectItemArea.top*m_nScale;

			int nCircle = (int)COMUtil.getPixel(10)*m_nScale;
			//drawCircle(canvas, (int)dX-nCircle, (int)dY-nCircle, (int)dX+nCircle, (int)dY+nCircle, true, POINT_CIRCLE_COLORS[m_moveItem.m_nType]);
			drawCircle(canvas, (int)dX-nCircle, (int)dY-nCircle, (int)dX+nCircle, (int)dY+nCircle, true, POINT_LINE_COLOR);

			String strTitle;
			strTitle = String.format("%d", m_moveItem.m_nValue);

			mTextPaint.setColor(Color.WHITE);
			canvas.drawText(strTitle, (int)dX-nCircle + COMUtil.getPixel(6)*m_nScale, (int)dY-nCircle + COMUtil.getPixel(15)*m_nScale, mTextPaint);
//			pDC->SetBkColor(POINT_LINE_COLOR);
//			pDC->SetTextColor(RGB(255,255,255));
//			::TextOut(pDC->GetSafeHdc(), (int)dX-nCircle + 7*m_nScale, (int)dY-nCircle + 5*m_nScale, strTitle, strTitle.GetLength());

		}

	}

	void GetPosition(int nType, int nRadius)
	{
//		float x = 0;
//		float y = 0;
//		double dPIValue = (Math.PI/180)*(getDegreeOffset() + (getDegrees()/2));
//		double dX = Math.cos(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerX();
//		double dY = Math.sin(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerY();
		int POINT_COUNT = 8;
		double dAngle = (nType*(360/POINT_COUNT)+(360/POINT_COUNT)*6)%360;

		Rect rect = new Rect(0, 0, nRadius, nRadius);
		_dCenterX = (rect.right-rect.left) / 2;
		_dCenterY = (rect.bottom-rect.top) / 2;

		double dPIValue = (Math.PI/180)*dAngle;
		_dX = Math.cos(dPIValue) * (((rect.right-rect.left) / 2)) + _dCenterX;
		_dY = Math.sin(dPIValue) * (((rect.right-rect.left) / 2)) + _dCenterX;
	}

	public void drawRect(Canvas gl, float x, float y, float w, float h, int[] color) {
		mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		mPaint.setStyle(Paint.Style.STROKE);
		gl.drawRect(x, y, x+w, y+h, mPaint);
	}
	public void drawFillRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha) {
		mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		mPaint.setAlpha((int)(alpha*255));
		mPaint.setStyle(Paint.Style.FILL);
		gl.drawRect(x, y, x+w, y+h, mPaint);
		mPaint.setAlpha(255);
	}
	public void drawLine(Canvas gl, float x1, float y1, float x2, float y2, int[] color, float alpha, float lineWidth) {
		mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		mPaint.setAlpha((int)(alpha*255));
		mPaint.setAntiAlias(true);
		if(lineWidth == 0)
			mPaint.setStrokeWidth(COMUtil.getPixel(1));
		else
			mPaint.setStrokeWidth(lineWidth);
		mPaint.setStyle(Paint.Style.STROKE);
		gl.drawLine(x1, y1, x2, y2, mPaint);
		mPaint.setAlpha(255);
	}
	public void drawCircle(Canvas gl, int x, int y, float width, float height, boolean filled, int[] color) {
		if(filled)
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
		if(color.length>3)
			mPaint.setColor(Color.argb(color[0],color[1],color[2],color[3]));
		else
			mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		RectF r = new RectF(x, y, width, height);
		gl.drawOval(r, mPaint);
	}
	String GetTitle(int nType)
	{
		switch (nType)
		{
			case 0:
				return "시가총액";
			case 1:
				return "투자자";
			case 2:
				return "내재가치";
			case 3:
				return "안정성";
			case 4:
				return "성장성";
			case 5:
				return "수익성";
			case 6:
				return "활동성";
			case 7:
				return "배당";

			default:
				break;
		}
		return "";
	}
	public void SetData(String strData, boolean bDraw)
	{
		m_strInvest = strData;

		m_arrInvestData.removeAll(m_arrInvestData);

		Rect rcClient = chart_bounds;


		int nWidth = (chart_bounds.width()/2 - m_nGapLeft/m_nScale)/5;

		int nGapLeft, nGapTop;
		double dRadius;

		int nType;
		Rect rcTitle;
		String[] strDatas = strData.split(":");
		for(int i=0; i<POINT_COUNT; i++)
		{
			String strValue =strDatas[i];
			nType = i;
			String strTitle = GetTitle(nType);
			int nValue = Integer.parseInt(strValue);

			nGapLeft = m_nGapLeft/m_nScale+(nValue-1)*nWidth;
			nGapTop =  m_nGapTop/m_nScale+(nValue -1)*nWidth;
			dRadius = rcClient.width()-nGapLeft*2;

			GetPosition(nType, (int)dRadius);
			_dX += nGapLeft;
			_dY += nGapTop;

			rcTitle = new Rect();
			rcTitle.left = (int)_dX;
			rcTitle.top = (int)_dY;
			rcTitle.right = rcTitle.left;
			rcTitle.bottom = rcTitle.top;

			m_arrInvestData.add( new InvestData(strTitle, nType, nValue, rcTitle, (int)dRadius)  );
		}
		if(bDraw)
			invalidate();
	}

	void SetDataInit()
	{
		m_arrPoint.removeAll(m_arrPoint);

		Rect rcClient = chart_bounds;

		int nWidth = (rcClient.width()/2 - m_nGapLeft/m_nScale)/5;

		int nGapLeft, nGapTop;
		double dRadius;

		int nType;
		Rect rcTitle;
		for(int i=0; i<POINT_COUNT; i++)
		{
			for(int j=0; j<5; j++)
			{
				nType = i;
				String strTitle = GetTitle(nType);
				int nValue = j+1;

				nGapLeft = m_nGapLeft/m_nScale+(nValue-1)*nWidth;
				nGapTop =  m_nGapTop/m_nScale+(nValue -1)*nWidth;
				dRadius = rcClient.width()-nGapLeft*2;

				GetPosition(nType, (int)dRadius);
				_dX += nGapLeft;
				_dY += nGapTop;

				rcTitle = new Rect();
				rcTitle.left = (int)_dX;
				rcTitle.top = (int)_dY;
				rcTitle.right = rcTitle.left;
				rcTitle.bottom = rcTitle.top;

				m_arrPoint.add( new InvestData(strTitle, nType, nValue, rcTitle, (int)dRadius)  );
			}
		}
		//2015.10.10 by LYH >> 리사이즈시 레벨 값 바뀌던 오류 수정
		if(m_arrInvestData.size()>=8)
		{
			String strInvest="", strItem="";
			InvestData pConditionItem;
			for( int i = 0; i < m_arrInvestData.size(); i++ )
			{
				pConditionItem = m_arrInvestData.get( i );
				if( pConditionItem != null )
				{
					strItem = String.format("%d:", pConditionItem.m_nValue);
				}
				strInvest = strInvest + strItem;
			}
			SetData(strInvest, false);
		}
		//2015.10.10 by LYH << 리사이즈시 레벨 값 바뀌던 오류 수정
	}

	InvestData FindPoint(Point point)
	{

		Rect rcTitle = new Rect();
		InvestData pConditionItem;
		for( int i = 0; i < m_arrPoint.size(); i++ )
		{
			pConditionItem = m_arrPoint.get( i );
			if( pConditionItem != null)
			{
				rcTitle = pConditionItem.m_rectItemArea;
				if(point.x>=rcTitle.left-(int)COMUtil.getPixel(13) && point.x<=rcTitle.left+(int)COMUtil.getPixel(13)
						&& point.y>=rcTitle.top-(int)COMUtil.getPixel(13) && point.y<=rcTitle.top+(int)COMUtil.getPixel(13))
				{
					return pConditionItem;
				}
			}
		}
		return null;
	}

	String GetResultData()
	{
		String strResult = "", strValue;
		InvestData pConditionItem;
		for( int i = 0; i < m_arrInvestData.size(); i++ )
		{
			pConditionItem = m_arrInvestData.get( i );
			if( pConditionItem != null)
			{
				strValue = String.format("%d", pConditionItem.m_nValue);
				strResult += strValue;
				strResult += ":";
			}
		}
		return strResult;
	}

	public void drawTriangle(Canvas gl, float x1, float y1, float x2, float y2, float x3, float y3,int[] color) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		mPath.reset();

		mPath.moveTo(x1, y1);
		mPath.lineTo(x2, y2);
		mPath.lineTo(x3, y3);
		mPath.lineTo(x1, y1);

		mPath.close();
		gl.drawPath(mPath, mPaint);
	}
	void DrawOval(Canvas canvas, Rect rcClient) {
		int nRadius = (rcClient.width() - m_nGapLeft * 2)/2;

		GetPosition(0, nRadius*2);

		int nHeight = (int)(nRadius*0.44f);
		int nMargin = (int)(nRadius*0.22f);
		int nCircle = (int)(nRadius * 1.12f)/2;
		int[] color = {(int)(255 * 0.7f),252,229,226};
		drawCircle(canvas, (int)_dCenterX+m_nGapLeft-nCircle, (int)_dCenterY+m_nGapTop-nHeight-nMargin, (int)_dCenterX+m_nGapLeft+nCircle, (int)_dCenterY+m_nGapTop-nMargin, true, color);

		int[] color1 = {(int)(255 * 0.7f),224,240,251};
		drawCircle(canvas, (int)_dCenterX+m_nGapLeft-nCircle, (int)_dCenterY+m_nGapTop+nMargin, (int)_dCenterX+m_nGapLeft+nCircle, (int)_dCenterY+m_nGapTop+nMargin+nHeight, true, color1);
	}
}
