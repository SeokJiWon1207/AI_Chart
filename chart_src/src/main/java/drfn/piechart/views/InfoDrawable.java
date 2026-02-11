package drfn.piechart.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.List;

import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

/**
 * 
 * @author Saul Howard
 *
 */
public class InfoDrawable extends Drawable {
    
    public final String TAG = this.getClass().getSimpleName();
	
    private PieChartView mChart;
    
	private Paint mTitlePaint, mAmountPaint, mSubTitlePaint;
	private float mOffsetX;
	private float mRadius;
	
	private Rect mTitleBounds = new Rect();
	private Rect mAmountBounds = new Rect();
	private Rect mSubTitleBounds = new Rect();
	
	private PointF mTitlePoint = new PointF();
	private PointF mAmountPoint = new PointF();
	private PointF mSubTitlePoint = new PointF();
	
	private float mTitleOffset, mSubTitleOffset;
	
	private String mTitle = "", mPercentTitle = "", mPriceTitle = "";
	
	private int m_nFontColor = CoSys.STEXT_GREY4;
	
	private List<PieSliceDrawable> mDrawables = null;

	private int m_nType = 0;

	public void setPieInfo(List<PieSliceDrawable> pDrawables)
	{
		mDrawables = pDrawables;
	}
	public float getOffsetX() {
		return mOffsetX;
	}

	public void setOffsetX(float mOffsetX) {
		this.mOffsetX = mOffsetX;
	}

	public void setType(int nType) {
		this.m_nType = nType;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
		mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleBounds);
		mTitlePoint.x = getBounds().exactCenterX() - mTitleBounds.width() / 2.5f;
		mTitlePoint.y = getBounds().exactCenterY() + mTitleOffset;
	}

	public String getPercentTitle() {
		return mPercentTitle;
	}

	public void setPercentTitle(String strPercent) {
		this.mPercentTitle = strPercent;
		mAmountPaint.getTextBounds(mPercentTitle, 0, mPercentTitle.length(), mAmountBounds);
		mAmountPoint.x = getBounds().exactCenterX() - mAmountBounds.width() / 2;
		mAmountPoint.y = getBounds().exactCenterY() + mAmountBounds.height() / 2;
	}

	public String getPriceTitle() {
		return mPriceTitle;
	}

	public void setPriceTitle(String strPriceTitle) {
		this.mPriceTitle = strPriceTitle;
		mSubTitlePaint.getTextBounds(mPriceTitle, 0, mPriceTitle.length(), mSubTitleBounds);
		mSubTitlePoint.x = getBounds().exactCenterX() - mSubTitleBounds.width() / 2;
		mSubTitlePoint.y = getBounds().exactCenterY() + mSubTitleBounds.height() + mSubTitleOffset;
	}

	public InfoDrawable(PieChartView chart, Context context, Rect bounds, float radius) {
		
		mChart = chart;
		Resources resources = context.getResources();
		setBounds(bounds);
		mRadius = radius;
		
		mTitleOffset = -0.8f * radius / 2;
		mSubTitleOffset = radius / 2.5f;
		
		mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTitlePaint.setAntiAlias(true);
		mTitlePaint.setColor(m_nFontColor);
		mTitlePaint.setTypeface(COMUtil.typeface);
		mTitlePaint.setTextSize(COMUtil.getPixel(12));
//		mTitlePaint.setTextSize(35.0f);
		
		mAmountPaint = new Paint(mTitlePaint);
		mAmountPaint.setTextSize(mRadius / 1.9f);
		mAmountPaint.setAntiAlias(true);
//		mAmountPaint.setTextSize(40.0f);
		mAmountPaint.setColor(m_nFontColor);
		
		mSubTitlePaint = new Paint(mTitlePaint);
		mSubTitlePaint.setAntiAlias(true);
//		mSubTitlePaint.setTextSize(35.0f);
		mSubTitlePaint.setTextSize(mRadius / 5);
		mSubTitlePaint.setColor(m_nFontColor);
		
	}
	
//	public void animateTransition(final String amount, final int amountColor, final String title) {
//		
//		if (mChart.getDrawThread().isPaused()) {
//			
//			setPercentTitle(amount);
//			setAmountColor(amountColor);
//			setTitle(title);
//			
//			return;
//		}
//		
//		final ThreadAnimator inAlpha = ThreadAnimator.ofInt(0, 255);
//		inAlpha.setDuration(200);
//		
//		ThreadAnimator outAlpha = ThreadAnimator.ofInt(255, 0);
//		outAlpha.setDuration(200);
//		outAlpha.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationEnded() {
//				setAmount(amount);
//				setAmountColor(amountColor);
//				setTitle(title);
//				mChart.getDrawThread().setInfoAnimator(inAlpha);
//			}
//		});
//		
//		mChart.getDrawThread().setInfoAnimator(outAlpha);
//	}

	@Override
	public void draw(Canvas canvas) {

		int yPos = 0;
		int yStartPos = (int)COMUtil.getPixel(5);
		if(m_nType==1)
		{
			yStartPos = getBounds().top+(getBounds().height() - (int)COMUtil.getPixel(12)*mDrawables.size())/2;
		}
		if(mDrawables.size()>10) {
			mTitlePaint.setTextSize(COMUtil.getPixel(9));
			yStartPos = (int)COMUtil.getPixel(8);
		}
		else
			mTitlePaint.setTextSize(COMUtil.getPixel(12));
	  	for (int index = 0; index < mDrawables.size(); index++) {
	  		int xPos = getBounds().left;
	        PieSliceDrawable slice = mDrawables.get(index);
//	        yPos = (int)mTitlePoint.y + (int)COMUtil.getPixel(20)*index + yStartPos;    
	        if(m_nType==1) {
				if(mDrawables.size()>10)
					yPos = (int) COMUtil.getPixel(14) * index + yStartPos;
				else
					yPos = (int) COMUtil.getPixel(18) * index + yStartPos;
			}
	        else
	        	yPos = getBounds().top + (int)COMUtil.getPixel(18)*index + yStartPos;
	        mAmountPaint.setColor(slice.getSliceColor());
			canvas.drawRoundRect(new RectF(xPos, yPos-COMUtil.getPixel(9), xPos+COMUtil.getPixel(9),yPos), COMUtil.getPixel(2), COMUtil.getPixel(2), mAmountPaint);
			xPos+= COMUtil.getPixel(15);
			String strTitle = slice.getName()+" 0.00%";
			try
			{
				strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
			}catch (Exception e){}
			canvas.drawText(strTitle.trim(), xPos, yPos, mTitlePaint);
	  	}

		//canvas.drawRect(new RectF(mTitlePoint.x, mTitlePoint.y+10, 20, 20), mTitlePaint);
	

//		canvas.drawText(mPercentTitle, mAmountPoint.x, mAmountPoint.y, mAmountPaint);
//		
//		canvas.drawText(mPriceTitle, mSubTitlePoint.x, mSubTitlePoint.y, mSubTitlePaint);
		
//		animateTransition(mAmount, mAmountPaint.getColor(), mTitle);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		
		mAmountPaint.setAlpha(alpha);
		mTitlePaint.setAlpha(alpha);
		invalidateSelf();
	}
	
	public void setFontColor(int nColor)
	{
		m_nFontColor = nColor;
		//mTitlePaint.setColor(m_nFontColor);
		mAmountPaint.setColor(m_nFontColor);
		mSubTitlePaint.setColor(m_nFontColor);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
