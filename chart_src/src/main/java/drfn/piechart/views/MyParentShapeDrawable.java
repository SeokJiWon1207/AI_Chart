package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * //2015. 8. 5 파이차트 수정사항 : 부채꼴의 drawable들의 부모클래스
 * @author drfnkimsh
 *
 */
public class MyParentShapeDrawable extends Drawable {

	public final String TAG = this.getClass().getSimpleName();

	protected PieChartView mChart;

	protected int m_nX = 0, m_nY = 0;

	public MyParentShapeDrawable(PieChartView chart, Context context, Rect bounds, float radius) {

		mChart = chart;
		setBounds(bounds);
	}

	public void setFrame(Rect bounds)
	{
		setBounds(bounds);

		if( (getBounds().left == m_nX) && (getBounds().top == m_nY))
		{
			return;
		}

		m_nX = bounds.left;
		m_nY = bounds.top;
	}

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
