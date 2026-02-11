package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import drfn.chart.util.COMUtil;


//2020.04.13 가로 Stack형 차트 수정 - hjw
public class HorizontalStackSliceDrawable extends Drawable {
	public final String TAG = this.getClass().getSimpleName();

	private Context mContext;
	private HorizontalStackChartView mChart;

	private float mPercent;
	private float mPercentAccrue;

	private RectF mBounds = new RectF();
	private RectF mBoundsClickArea = new RectF();	//그려지는 자신의 영역

	private Paint mPaint;
	private Paint mPaint2;

	private String m_strSliceName;	//이름
	private String m_strSlicePrice;	//가격

	private boolean m_bIsSelected = false;
	private boolean m_bNullValue = false;

	private int m_nIndex = 0;
	private int m_nCount = 0;

	public HorizontalStackSliceDrawable(HorizontalStackChartView chart, Context context) {
		mChart = chart;

		setCallback(chart);
		mContext = context;

		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	public void draw(Canvas canvas) {
		RectF drawRect = new RectF();
		RectF bounds = null;
//
//		if(mChart.getAlignType().equals(HorizontalStackChartView.STACK_BAR_INFO_ALIGN_TOP) &&
//				(mChart.getInfoType() == HorizontalStackChartView.STACK_BAR_INFO_SELECT || mChart.getInfoType() == HorizontalStackChartView.STACK_BAR_INFO_LIST)) {    //범례를 위로 정렬 또는 범례가 선텩 또는 목록형일때
//			if(mChart.getInfoType() == HorizontalStackChartView.STACK_BAR_INFO_SELECT) {    //선택형 범례일때
//				bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mChart.mInfoDrawable.mAmountPoint.y + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN) + COMUtil.getPixel(12),    //범례와 차트 간격에 12 간격을 둠
//						mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mChart.mInfoDrawable.mAmountPoint.y + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT) + COMUtil.getPixel(12));    //범례와 차트 간격에 12 간격을 둠
//			} else if(mChart.getInfoType() == HorizontalStackChartView.STACK_BAR_INFO_LIST) {    //목록형 범례
//				int fontHeight;
//				if(mChart.getAllDataCount()>10) {
//					fontHeight = 13;
//				} else {
//					fontHeight = 18;
//				}
//
//				int rowCount = ((mChart.getAllDataCount()-1)/HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT) + 1;
//				int infoHeight = (int) COMUtil.getPixel(fontHeight) * rowCount;
//
//				bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN)*2 + infoHeight,	//범례, 차트 각각의 Top Margin을 위해 * 2 해줌
//						mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN)*2 + infoHeight + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT));
//			}
//		} else if(mChart.getInfoType() == 0) {
//			bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.bottom);
//		} else {	//범례를 아래로 정렬
//			bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//					mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT));	//범례와 차트 간격에 12 간격을 둠
//		}

		bounds = mBounds;

		if (getPercent() <= 0)
			return;

		float drawLeft = bounds.width() * getPercentAccru();
		float drawWidth = bounds.width() * getPercent() - HorizontalStackChartView.STACK_BAR_MARGIN;

		if(m_bNullValue) {
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);

//			if(mChart.getInfoType() != 0) {
//				bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
//						mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT));
//			}
		}
//		if (mChart.getInfoType() == HorizontalStackChartView.STACK_BAR_INFO_SELECT && m_bIsSelected) {
//			drawRect.set(bounds.left + drawLeft, bounds.top - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_SELECTED_HEIGHT_GAB), bounds.left + drawLeft + drawWidth, bounds.bottom + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_SELECTED_HEIGHT_GAB));
//		} else {
			drawRect.set(bounds.left + drawLeft + HorizontalStackChartView.STACK_BAR_MARGIN, bounds.top, bounds.left + drawLeft + drawWidth - HorizontalStackChartView.STACK_BAR_MARGIN, bounds.bottom);

//		}

		mBoundsClickArea = drawRect;

		if (m_nIndex == 0 || m_nIndex == m_nCount-1) {
			canvas.drawRoundRect(drawRect, bounds.bottom / 2, bounds.bottom / 2, mPaint);
		}



		RectF bgBounds = null;
		if (m_nIndex == 0) {
			bgBounds = new RectF((drawRect.right-drawRect.left)/2+5, drawRect.top , drawRect.right, mBounds.bottom);
		} else if (m_nIndex == m_nCount-1) {
			bgBounds = new RectF(drawRect.left, drawRect.top , drawRect.left + (drawRect.right-drawRect.left)/2, mBounds.bottom);
		}
		else {
			bgBounds = drawRect;
		}

		canvas.drawRect(bgBounds, mPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.UNKNOWN;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		mPaint.setAlpha(alpha / 255 * 50);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}

	public void setName(String strName) {
		m_strSliceName = strName;
	}

	public String getName() {
		return m_strSliceName;
	}

	public void setPrice(String strPrice) {
		m_strSlicePrice = strPrice;
	}

	public String getPrice() {
		return m_strSlicePrice;
	}

	public float getPercentAccru() {
		return mPercentAccrue;
	}

	public void setPercentAccru(float percentAccrue) {
		this.mPercentAccrue = percentAccrue;
	}

	public float getPercent() {
		return mPercent;
	}

	public void setPercent(float percent) {
		mPercent = percent;
		invalidateSelf();
	}

	public int getSliceColor() {
		return mPaint.getColor();
	}

	public void setSliceColor(int color) {
		mPaint.setColor(color);
		invalidateSelf();
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);

		updateBounds();
	}

	private void updateBounds() {
		mBounds.left = getBounds().left;
		mBounds.top = getBounds().top;
		mBounds.right = getBounds().right;
		mBounds.bottom = getBounds().bottom;

		invalidateSelf();
	}

	//선택 여부
	public void setSelectedSlice(boolean bFlag) {
		m_bIsSelected = bFlag;
	}

	//스택 영역 내부인지 확인
	public boolean containsPoint(int x, int y) {
		return mBoundsClickArea.contains(x, y);
	}

	public void setNullData(boolean bool) {
		m_bNullValue = bool;
	}

	public void drawBackgroundBar(Canvas canvas) {
		RectF bounds = null;

		mPaint2.setColor(Color.rgb(236,236,236));

		bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.bottom);

		canvas.drawRoundRect(bounds,COMUtil.getPixel(4), COMUtil.getPixel(4), mPaint2);
	}
	public void drawBackgroundBar_quant(Canvas canvas, String sType) {
		RectF bounds = null;
		RectF drawRect = new RectF();

		bounds = new RectF(mBounds.left + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.top + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.right - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN),
				mBounds.bottom);

		float drawLeft = bounds.width() * getPercentAccru();
		float drawWidth = bounds.width() * getPercent();

		if(sType.equals("data1")) {
//			mPaint2.setColor(Color.rgb(0, 120, 197));
			mPaint2.setColor(Color.rgb(51, 118, 191));
			drawRect.set(bounds.left + drawLeft + (int) COMUtil.getPixel(10), bounds.top, bounds.left + drawLeft + drawWidth, bounds.bottom);
		} else {
//			mPaint2.setColor(Color.rgb(224, 45, 35));
			mPaint2.setColor(Color.rgb(237, 79, 52));
			drawRect.set(bounds.left + drawLeft, bounds.top, bounds.left + drawLeft + drawWidth - (int) COMUtil.getPixel(10), bounds.bottom);
		}

		canvas.drawRect(drawRect, mPaint2);
	}
	public void setSliceIndex(int nIndex) {
		m_nIndex = nIndex;
	}
	public void setCount(int nCount) {
		m_nCount = nCount;
	}
}
