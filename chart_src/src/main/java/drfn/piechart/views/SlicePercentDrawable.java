package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

import drfn.piechart.views.ThreadAnimator.AnimationListener;

/**
 * //2015. 8. 5 파이차트 수정사항 : 부채꼴 %정보 표시에 사용되는 Drawable class
 * @author drfnkimsh
 *
 */
public class SlicePercentDrawable extends MyParentShapeDrawable {

	private Paint mTitlePaint;

	private Rect mTitleBounds = new Rect();

	private String mTitle = "";

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
		mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleBounds);
	}

	public SlicePercentDrawable(PieChartView chart, Context context, Rect bounds, float radius) {
		super(chart, context, bounds, radius);

		mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTitlePaint.setColor(Color.WHITE);
		mTitlePaint.setTextSize(getBounds().width()*1.5f);
	}

	public void animateTransition(final String amount, final int amountColor, final String title) {

//		if (mChart.getDrawThread().isPaused()) {
//			setTitle(title);
//			
//			return;
//		}

		final ThreadAnimator inAlpha = ThreadAnimator.ofInt(0, 255);
		inAlpha.setDuration(200);

		ThreadAnimator outAlpha = ThreadAnimator.ofInt(255, 0);
		outAlpha.setDuration(200);
		outAlpha.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnded() {
				setTitle(title);
//				mChart.getDrawThread().setInfoAnimator(inAlpha);
			}
		});

//		mChart.getDrawThread().setInfoAnimator(outAlpha);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawText(mTitle, m_nX, m_nY, mTitlePaint);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		super.setAlpha(alpha);

		mTitlePaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
