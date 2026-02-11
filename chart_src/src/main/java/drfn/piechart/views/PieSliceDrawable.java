package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.ArcShape;

import drfn.chart.util.ChartUtil;
import drfn.piechart.extra.UiUtils;
import drfn.piechart.views.PieChartView.PieChartAnchor;
import drfn.chart.util.COMUtil;

/**
 * The PieSliceDrawable provides drawing a specific slice of a Pie Chart
 * given the starting degree offset from 0 degrees and the percent of
 * the Pie Chart the slice covers, 0 - 1.
 *
 * @author saulhoward
 *
 */
public class PieSliceDrawable extends Drawable {

	public final String TAG = this.getClass().getSimpleName();

	private final int DEFAULT_STROKE_WIDTH = 2;
	private final int DEFAULT_SEL_SLICE_MARGIN = 3;
	private int nSliceMargin;
	private int nSelSliceMargin;

	private float m_StartAngle = 0;
	private float mDegreeOffset;
	private float mPercent;
	private RectF mBounds = new RectF();
	private RectF mBounds_Sel = new RectF();

	private Paint mPaint, mStrokePaint, mTextPaint;

	private Context mContext;

	private float mStrokeWidth;

	private Path mPathRight, mPathLeft;

	//2015. 8. 5 파이차트 수정사항>>
	private String m_strSliceName;	//부채꼴의 이름
	private String m_strSlicePrice;
	// 부채꼴의 % 정보 표시할 drawable
	private SlicePercentDrawable m_slicePercent = null;

	private String m_ArrowPositionType;	//2015. 8. 5 파이차트 수정사항

	private boolean m_bIsSelected = false;

	private int m_nColorType = 0;
	//2015. 8. 5 파이차트 수정사항<<

//	private boolean m_bIsHideText = false;

	//2015. 8. 5 파이차트 수정사항 : 부채꼴 %정보 표시 관련 메서드>>
	public SlicePercentDrawable getSlicePercentDrawable()
	{
		return m_slicePercent;
	}

	public void setStartAngle(float fAngle)
	{
		if(fAngle == m_StartAngle)
		{
			return;
		}
		m_StartAngle =  fAngle >= 360 ? fAngle % 360 : fAngle;
	}
	public float getStartAngle()
	{
		return m_StartAngle;
	}

	public void setSlicePercentDrawable(PieChartView chart, Context context)
	{
		double dPIValue = (Math.PI/180)*(m_StartAngle + (getDegrees()/2));
		double dX = Math.cos(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerX() - UiUtils.getDynamicPixels(mContext, 16);
		double dY = Math.sin(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerY() + UiUtils.getDynamicPixels(mContext, 10);

		//선택된 부채꼴이면 % 표시 문자열을 위쪽으로 조금 위치 이동
		if(m_bIsSelected)
		{
//			dY -= UiUtils.getDynamicPixels(mContext, 40);
			if(m_ArrowPositionType.equals("B"))	//2015. 8. 5 파이차트 수정사항
			{
				dY =  mBounds.centerY() + (mBounds.bottom - mBounds.top) /4 + (int)UiUtils.getDynamicPixels(mContext, 14);
			}
			else if(m_ArrowPositionType.equals("R"))
			{
				dX -= (int)UiUtils.getDynamicPixels(mContext, 30);
			}
			else if(m_ArrowPositionType.equals("RT"))
			{
				dX -= (int)UiUtils.getDynamicPixels(mContext, 30);
			}
		}

		//x좌표가 중심점 x값보다 작거나 같으면 textview의 위치를 textview 너비의 절반 크기만큼 좌측으로 이동시킨다
//		if(dX > (mBounds.right-mBounds.left) / 2)
//		{
//			dX -= UiUtils.getDynamicPixels(mContext, 7);
//		}

		//y좌표가 중심점 y값보다 작거나 같으면 textview의 위치를 textview 너비의 절반 크기만큼 상단으로 이동시킨다
//		if(dY > (mBounds.right-mBounds.left) / 2)
//		{
//			dY += UiUtils.getDynamicPixels(mContext, 7);
//		}

		Rect slicePercentRect = new Rect(	(int)dX,
				(int)dY,
				(int)dX+(int)UiUtils.getDynamicPixels(mContext, 14),
				(int)dY+(int)UiUtils.getDynamicPixels(mContext, 14));

		if(null == m_slicePercent)
		{
			m_slicePercent = new SlicePercentDrawable(chart, context, slicePercentRect, UiUtils.getDynamicPixels(mContext, 50));
		}
		else
		{
			m_slicePercent.setFrame(slicePercentRect);
		}

	}

	public void setName(String strName)
	{
		m_strSliceName = strName;
	}

	public String getName()
	{
		return m_strSliceName;
	}

	public void setPrice(String strPrice)
	{
		m_strSlicePrice = strPrice;
	}

	public String getPrice()
	{
		return m_strSlicePrice;
	}
	//2015. 8. 5 파이차트 수정사항 : 부채꼴 %정보 표시 관련 메서드<<

	public float getDegreeOffset() {
		return mDegreeOffset;
	}

	public void setDegreeOffset(float mDegreeOffset) {
		this.mDegreeOffset = mDegreeOffset;
	}

	public float getPercent() {
		return mPercent;
	}

	public float getDegrees() {
		return mPercent * 360;
	}

	public void setPercent(float percent) {
		mPercent = percent;
		invalidateSelf();
	}

	public float getSliceCenter() {
		//return mDegreeOffset + getDegrees() / 2;
		return mDegreeOffset + getDegrees();
	}

	public void setStokeWidth(float width) {
		mStrokeWidth = width;
		updateBounds();
	}

	public float getStrokeWidth() {
		return mStrokeWidth;
	}

	/**
	 * Returns whether this slice contains the degree supplied.
	 *
	 * @param rotationOffset The overall rotational offset of the chart
	 * @param degree The degree to be checked
	 * @return True if this slice contains the degree
	 */
	public boolean containsDegree(float rotationOffset, float degree) {

		degree = degree - rotationOffset;
		if (degree < 0) degree += 360;
		degree %= 360;

		return mDegreeOffset < degree && degree <= (mDegreeOffset + getDegrees());
	}

	public int getSliceColor() {
		return mPaint.getColor();
	}

	public void setSliceColor(int color) {
		mPaint.setColor(color);
		invalidateSelf();
	}

	/**
	 * Create a new pie slice to be used in a pie chart.
	 *
	 * @param context the context for this view
	 * @param degreeOffset the starting degree offset for the slice
	 * @param percent the percent the slice covers
	 * @param color the color of the slice
	 */
	public PieSliceDrawable(Callback cb, Context context) {

		setCallback(cb);
		mContext = context;

		mStrokeWidth = UiUtils.getDynamicPixels(mContext, DEFAULT_STROKE_WIDTH);
//		nSliceMargin = (int)UiUtils.getDynamicPixels(mContext, 7);
		nSliceMargin = 0;
		nSelSliceMargin = (int)UiUtils.getDynamicPixels(mContext, DEFAULT_SEL_SLICE_MARGIN);

		init();
	}

	/**
	 * Initialize our paints and such
	 */
	private void init() {

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mStrokePaint = new Paint(mPaint);
		//Slice 구분 선 안보이도록 설정
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		mStrokePaint.setColor(Color.WHITE);

		mTextPaint = new Paint(mPaint);
		mTextPaint.setTypeface(Typeface.SANS_SERIF);
		mTextPaint.setStrokeWidth(UiUtils.getDynamicPixels(mContext, 24));
		mTextPaint.setTextSize(COMUtil.getPixel(15));
		mTextPaint.setColor(Color.WHITE);

//    	mPaint_Text.setColor(Color.rgb(color[0],color[1],color[2]));
//    	mPaint_Text.setTextAlign(Align.LEFT);
//    	mPaint_Text.setTextSize(size);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);

		updateBounds();
	}

	private void updateBounds() {

		// Updates the drawing bounds so the slice is sized correctly given the
		// stroke width
//		mBounds.left = getBounds().left + mStrokeWidth;
//		mBounds.top = getBounds().top + mStrokeWidth;
//		mBounds.right = getBounds().right - mStrokeWidth;
//		mBounds.bottom = getBounds().bottom - mStrokeWidth;
		mBounds.left = getBounds().left + nSliceMargin;
		mBounds.top = getBounds().top + nSliceMargin;
		mBounds.right = getBounds().right - nSliceMargin;
		mBounds.bottom = getBounds().bottom - nSliceMargin;

		mBounds_Sel.left = getBounds().left + nSelSliceMargin;
		mBounds_Sel.top = getBounds().top + nSelSliceMargin;
		mBounds_Sel.right = getBounds().right - nSelSliceMargin;
		mBounds_Sel.bottom = getBounds().bottom - nSelSliceMargin;

		double radians = Math.toRadians(mDegreeOffset + getDegrees());
		float radius = mBounds.width() / 2;
		float x = (float) (radius * Math.cos(radians));
		float y = (float) (radius * Math.sin(radians));

		mPathRight = createPath(x, y);

		radians = Math.toRadians(mDegreeOffset);
		x = (float) (radius * Math.cos(radians));
		y = (float) (radius * Math.sin(radians));

		mPathLeft = createPath(x, y);

		invalidateSelf();
	}

	private Path createPath(float x, float y) {

		Path path = new Path();
		path.moveTo(mBounds.centerX(), mBounds.centerY());
		path.lineTo(mBounds.centerX() + x, mBounds.centerY() + y);
		path.close();

		return path;
	}
	Paint mBackPaint = new Paint();
	@Override
	public void draw(Canvas canvas) {
		//background

//    	mBackPaint.setColor(Color.rgb(255, 255, 255));
//    	canvas.drawRect(new RectF(mBounds.left, mBounds.top, mBounds.right*2, mBounds.bottom*2), mBackPaint);

		// Draw and stroke the pie slice
		if (m_nColorType == 2) {
			if (!m_bIsSelected) {
				canvas.drawArc(mBounds_Sel, mDegreeOffset, getDegrees(), true, mPaint);
			} else
				canvas.drawArc(mBounds, mDegreeOffset, getDegrees(), true, mPaint);
		} else {
			canvas.drawArc(mBounds, mDegreeOffset, getDegrees(), true, mPaint);
		}
		if (m_nColorType == 1) {
			canvas.drawPath(mPathRight, mStrokePaint);
			canvas.drawPath(mPathLeft, mStrokePaint);
		}

		//2015. 8. 5 파이차트 수정사항 : drawable안쓰고 그냥 drawText할려면 아래부분 주석해제, 아랫걸로 하면 글자가 회전되어있다. (이 파이차트가 canvas를 회전시키는 루틴이라서)
		//drawText
//		double radians = Math.toRadians(mDegreeOffset + getDegrees());
//		float radius = mBounds.width() / 2;
//		float x = (float) (radius * Math.cos(radians));
//		float y = (float) (radius * Math.sin(radians));

//		//
//		float x = 0;
//		float y = 0;
//		double dPIValue = (Math.PI/180)*(getDegreeOffset() + (getDegrees()/2));
//		double dX = Math.cos(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerX();
//		double dY = Math.sin(dPIValue) * (((mBounds.right-mBounds.left) / 2) - UiUtils.getDynamicPixels(mContext, 35)) + mBounds.centerY();

		//x占쏙옙표占쏙옙 占쌩쏙옙占쏙옙 x占쏙옙占쏙옙占쏙옙 占쌜거놂옙 占쏙옙占쏙옙占쏙옙 textview占쏙옙 占쏙옙치占쏙옙 textview 占십븝옙占쏙옙 占쏙옙占쏙옙 크占썩만큼 占쏙옙占쏙옙占쏙옙占쏙옙 占싱듸옙占쏙옙킨占쏙옙
//		if(dX < mBounds.centerX())
//		{
//			dX -= UiUtils.getDynamicPixels(mContext, 20);
//		}
//		
//		//x占쏙옙표占쏙옙 占쌩쏙옙占쏙옙 x占쏙옙占쏙옙占쏙옙 占쌜거놂옙 占쏙옙占쏙옙占쏙옙 textview占쏙옙 占쏙옙치占쏙옙 textview 占십븝옙占쏙옙 占쏙옙占쏙옙 크占썩만큼 占쏙옙占쏙옙占쏙옙占쏙옙 占싱듸옙占쏙옙킨占쏙옙
//		if(dY < mBounds.centerY())
//		{
//			dY -= UiUtils.getDynamicPixels(mContext, 20);
//		}
//		
//		canvas.drawText(""+mPercent, 30, getDegrees(), mTextPaint);
//		if(!m_bIsHideText)
//		{
//			canvas.save();
//			canvas.rotate(0 - ((getDegreeOffset() + getDegrees()) / 2), (float)dX, (float)dY);
//			canvas.drawText(String.format("%.0f", mPercent*100)+"%", (float)dX, (float)dY, mTextPaint);
//			canvas.restore();
//		}
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		mPaint.setAlpha(alpha / 255 * 50);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

//	public void setHideText(boolean bFlag)
//	{
//		m_bIsHideText = bFlag;
//	}

	public void setSelectedSlice(boolean bFlag)
	{
		m_bIsSelected = bFlag;
	}

	public void setColorType(int type) {
		m_nColorType = type;
	}

	//2015. 8. 5 파이차트 수정사항>>
	public void setArrowPositionType(String strType)
	{
		m_ArrowPositionType = strType;
	}
	//2015. 8. 5 파이차트 수정사항<<

	public PointF getAnagleLinePath(int nColorType)
	{
		float fSizeCircleInside = 0;
		if(nColorType == 1 || nColorType == 2) {
			fSizeCircleInside = (float)(mBounds.width()*0.1);
		} else if(nColorType == 3) {
			fSizeCircleInside = COMUtil.getPixel(3);
		} else if(nColorType == 4) {
			fSizeCircleInside = (float)(mBounds.width()*0.18);
		}

		float strartangle = (float) Math.toRadians(mDegreeOffset);
		float endAngle = (float) Math.toRadians(mDegreeOffset + getDegrees());
		//float midAngle = (strartangle + endAngle)/2;
		float midAngle = (strartangle + endAngle-(endAngle-strartangle)/3)/2;	//2020.05.29 by LYH >> 중간 아닌 1/3 지점에 툴팁 띄우기

		PointF pCenter =  new PointF(mBounds.centerX(), mBounds.centerY());
		PointF p1 = calculatePosition(midAngle, pCenter,mBounds.width()/2 - fSizeCircleInside/2);
		return p1;
	}

	public PointF calculatePosition(float angle ,PointF pCenter, float offset)
	{
		PointF p = new PointF((float)( pCenter.x + offset * Math.cos(angle)) , (float) (pCenter.y +offset* Math.sin(angle)));
		return p;
	}

	public boolean pieElemInPoint(PointF point){
		// Create the path

		PointF center = new PointF(mBounds.centerX(), mBounds.centerY());
		double dAngle = ChartUtil.getAngle(new PointF(center.x, center.y), new PointF(point.x, point.y));

		if(dAngle<-90)
			dAngle += 360;
		Path path = new Path();
		path.moveTo(center.x, center.y);

		if(dAngle>=mDegreeOffset && dAngle < mDegreeOffset + getDegrees())
			return true;

		return false;
	}
}
