package drfn.piechart.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

//import drfn.piechart.R;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.piechart.views.ThreadAnimator.AnimationListener;

/**
 *
 * @author Saul Howard
 *
 */
public class CenterCircleInfoDrawable extends Drawable {

	public final String TAG = this.getClass().getSimpleName();

	private PieChartView mChart;

	//	private Paint mTitlePaint, mAmountPaint, mSubTitlePaint;
	private Paint mTitlePaint;
	private Paint mSubTitlePaint;
	private Paint mAmountPaint;
	private float mOffsetX;
	private float mRadius;

	private Rect mTitleBounds = new Rect();
	private Rect mAmountBounds = new Rect();
	//private Rect mSubTitleBounds = new Rect();

	private PointF mTitlePoint = new PointF();
	private PointF mAmountPoint = new PointF();
	//private PointF mSubTitlePoint = new PointF();

	private float mTitleOffset;//, mSubTitleOffset;

	//	private String mTitle = "", mAmount = "", mSubTitle = "";
	private String mTitle = "";
	private String mAmount = "";

	public float getOffsetX() {
		return mOffsetX;
	}

	public void setOffsetX(float mOffsetX) {
		this.mOffsetX = mOffsetX;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
//		if(mChart.getColorType() == 3) {
//			mTitle = COMUtil.format(mTitle,0,3);
//		}
		this.mTitle = mTitle;
		mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleBounds);
		mTitlePoint.x = getBounds().exactCenterX() - mTitleBounds.width() / 2-COMUtil.getPixel_W(1);
//		if(mChart.getColorType() == 3) {
//			mTitlePoint.x = getBounds().exactCenterX() - COMUtil.getPixel_W(150 + 16) / 2 ;
//		}
		mTitlePoint.y = getBounds().exactCenterY() + mTitleBounds.height() / 2;
	}

	public String getAmount() {
		return mAmount;
	}

	public void setAmount(String mAmount) {
		this.mAmount = mAmount;
		mAmountPaint.getTextBounds(mAmount, 0, mAmount.length(), mAmountBounds);
		mAmountPoint.x = getBounds().exactCenterX() - mAmountBounds.width() / 2;
		mAmountPoint.y = getBounds().exactCenterY() + mAmountBounds.height() / 2;
	}

//	public String getSubTitle() {
//		return mSubTitle;
//	}
//
//	public void setSubTitle(String mSubTitle) {
//		this.mSubTitle = mSubTitle;
//		mSubTitlePaint.getTextBounds(mSubTitle, 0, mSubTitle.length(), mSubTitleBounds);
//		mSubTitlePoint.x = getBounds().exactCenterX() - mSubTitleBounds.width() / 2;
//		mSubTitlePoint.y = getBounds().exactCenterY() + mSubTitleBounds.height() + mSubTitleOffset;
//	}

	public void setAmountColor(int color) {
		mAmountPaint.setColor(color);
		invalidateSelf();
	}

	public CenterCircleInfoDrawable(PieChartView chart, Context context, Rect bounds, float radius) {

		mChart = chart;
		Resources resources = context.getResources();
		setBounds(bounds);
		mRadius = radius;

		mTitleOffset = radius / 3;
		//mSubTitleOffset = radius / 3;

		mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mTitlePaint.setColor(Color.BLACK);
		//mTitlePaint.setColor(Color.rgb(100,100,100));
		if(mChart.getColorType() == 3) {
			mTitlePaint.setTypeface(COMUtil.numericTypefaceMid);
			mTitlePaint.setTextSize(COMUtil.getPixel(11));
			mTitlePaint.setColor(Color.WHITE);
		}
		else {
			mTitlePaint.setTypeface(COMUtil.typefaceBold);
			mTitlePaint.setTextSize(COMUtil.getPixel(20));
		}


		mSubTitlePaint = new Paint();
		mSubTitlePaint.setTextSize(COMUtil.getPixel(14));
		mSubTitlePaint.setTypeface(COMUtil.typefaceMid);
		mSubTitlePaint.setColor(Color.BLACK);

	}

	private static void setTextSizeForWidth(Paint paint, float desiredWidth,
											String text) {

		// Pick a reasonably large value for the test. Larger values produce
		// more accurate results, but may cause problems with hardware
		// acceleration. But there are workarounds for that, too; refer to
		// http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
		final float testTextSize = COMUtil.getPixel(30);

		// Get the bounds of the text, using our testTextSize.
		paint.setTextSize(testTextSize);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		// Calculate the desired size as a proportion of our testTextSize.
		float desiredTextSize = testTextSize * desiredWidth / bounds.width();

		// Set the paint for that size.
		paint.setTextSize(desiredTextSize);
	}
//	public void animateTransition(final String amount, final int amountColor, final String title) {
//		
//		if (mChart.getDrawThread().isPaused()) {
//			
//			setAmount(amount);
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
//		if(mChart.getColorType() == 3) {
//			float textWidth = mTitlePaint.measureText(mTitle);
//			float subTextWidth = mSubTitlePaint.measureText("원");
//			setTextSizeForWidth(mTitlePaint,COMUtil.getPixel_W(150), mTitle );
//			canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y, mTitlePaint);
//
//			canvas.drawText("원", mTitlePoint.x + COMUtil.getPixel_W(150)+COMUtil.getPixel_W(5), mTitlePoint.y, mSubTitlePaint);
//		}
//		else
			canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y, mTitlePaint);

		//아래 Text를 변수로 받아서 처리하도록 할 것 
//		canvas.drawText("합계:", mAmountPoint.x+5, mAmountPoint.y-50, mAmountPaint);
//		canvas.drawText("$17.1M", mAmountPoint.x-20, mAmountPoint.y, mAmountPaint);

//		canvas.drawText(mSubTitle, mSubTitlePoint.x, mSubTitlePoint.y, mSubTitlePaint);

//		animateTransition(mAmount, mAmountPaint.getColor(), mTitle);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {

		mAmountPaint.setAlpha(alpha);
//		mTitlePaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
