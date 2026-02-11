package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * //2015. 8. 5 파이차트 수정사항 : 부채꼴 %정보 표시에 사용되는 Drawable class
 * @author drfnkimsh
 *
 */
public class ArrowDrawable extends MyParentShapeDrawable {

	private Paint mArrowPaint;
	/** 화살표 위치 (기본 6시방향) B : 6시방향 R : 3시방향 */
	private String m_strArrowPositionType = "B";

	public ArrowDrawable(PieChartView chart, Context context, Rect bounds) {
		super(chart, context, bounds, 0);

		mArrowPaint = new Paint();
		mArrowPaint.setColor(Color.WHITE);
		mArrowPaint.setAlpha(50);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		RectF rect = new RectF(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom);

		if(m_strArrowPositionType.equals("B"))
		{
			canvas.drawArc(rect, 65.0f, 50.0f, true, mArrowPaint);
		}
		else if(m_strArrowPositionType.equals("R"))
		{
			canvas.drawArc(rect, 335.0f, 50.0f, true, mArrowPaint);
		}
		else
		{
			canvas.drawArc(rect, 65.0f, 50.0f, true, mArrowPaint);
		}
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		super.setAlpha(alpha);

		mArrowPaint.setAlpha(alpha);
		invalidateSelf();
	}

	/**
	 * 파이차트의 기준각도를 표시하는 화살표의 위치 지정
	 * @param strPositionType 파이차트 기준각도 화살표 위치 (B : 6시방향, R : 오른쪽 3시방향)
	 * */
	public void setArrowPositionType(String strPositionType)
	{
		m_strArrowPositionType = strPositionType;
	}

	/**
	 * 파이차트의 기준각도를 표시하는 화살표의 위치
	 * @return 화살표의 위치 알파벳 (B : 6시방향, R : 오른쪽 3시방향)
	 * */
	public String getArrowPositionType()
	{
		return m_strArrowPositionType;
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
