package drfn.piechart.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.List;

import drfn.chart.util.COMUtil;

//2020.04.13 가로 Stack형 차트 수정 - hjw
public class HorizontalStackInfoDrawable extends Drawable {
	public final String TAG = this.getClass().getSimpleName();

	private Context mContext = null;
	private HorizontalStackChartView mChart;

	private Paint mTitlePaint, mAmountPaint, mSubTitlePaint, mPercentPaint;

	private Rect mTitleBounds = new Rect();
	private Rect mAmountBounds = new Rect();
	private Rect mPercentBounds = new Rect();

	public PointF mTitlePoint = new PointF();
	public PointF mAmountPoint = new PointF();
	public PointF mSubTitlePoint = new PointF();
	public PointF mPercentPoint = new PointF();

	private String mTitle = "";
	private String mPercentTitle = "";
	private String mPriceTitle = "";

	private int m_nFontColor = Color.BLACK;

	private List<HorizontalStackSliceDrawable> mDrawables = null;

	private int fontHeight = 18;
	private int m_nType = 0;

	public HorizontalStackInfoDrawable(HorizontalStackChartView chart, Context context, Rect bounds) {
		mContext = context;
		mChart = chart;
		setBounds(bounds);

		mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTitlePaint.setAntiAlias(true);
		mTitlePaint.setColor(m_nFontColor);
		mTitlePaint.setTextSize(COMUtil.getPixel(14));

		mPercentPaint = new Paint(mTitlePaint);
		mPercentPaint.setAntiAlias(true);
		mPercentPaint.setTextSize(COMUtil.getPixel(32));
		mPercentPaint.setColor(Color.rgb(51, 51, 51));

		mSubTitlePaint = new Paint(mTitlePaint);
		mSubTitlePaint.setAntiAlias(true);
		mSubTitlePaint.setTextSize(COMUtil.getPixel(24));
		mSubTitlePaint.setColor(Color.rgb(51, 51, 51));

		mAmountPaint = new Paint(mTitlePaint);
		mAmountPaint.setAntiAlias(true);
		mAmountPaint.setTextSize(COMUtil.getPixel(15));
		mAmountPaint.setColor(Color.rgb(51, 51, 51));
	}

	@Override
	public void draw(Canvas canvas) {
		if(m_nType == HorizontalStackChartView.STACK_BAR_INFO_SELECT) {    //선택형 범례일때
			canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mTitlePaint);
			canvas.drawText(mPercentTitle, mPercentPoint.x - (int) COMUtil.getPixel(1), mPercentPoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mPercentPaint);
			canvas.drawText("%", mSubTitlePoint.x, mSubTitlePoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mSubTitlePaint);

			canvas.drawText(mPriceTitle, mAmountPoint.x, mAmountPoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mAmountPaint);
		} else if(m_nType == HorizontalStackChartView.STACK_BAR_INFO_LIST) {	//나열형 범례일때
			//10개 이상은 사이즈 줄임
			if(mDrawables.size()>10) {
				mSubTitlePaint.setTextSize(COMUtil.getPixel(9));
				fontHeight = 13;
			} else {
				mSubTitlePaint.setTextSize(COMUtil.getPixel(13));
				fontHeight = 18;
			}

			//시작 좌표 설정
			int xStartPos = (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);
			int yStartPos = 0;

			if(mChart.getAlignType().equals(HorizontalStackChartView.STACK_BAR_INFO_ALIGN_TOP)) {    //범례를 위로 정렬
				yStartPos = (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);
			} else {
				yStartPos = (int)COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN)*2;
			}

			//셀 중에 가장 넓은 것을 기준으로 x 좌표 간격을 정함
			float columnInterval = 0;
			for(int index = 0; index < mDrawables.size(); index++) {
				HorizontalStackSliceDrawable slice = mDrawables.get(index);

				//데이터를 퍼센트형으로 받을때 처리
				if(slice.getName().equals(HorizontalStackChartView.BLANK_VALUE_TITLE)) {
					continue;
				}

				String strTitle = slice.getName()+" 0.00%";
				try
				{
					strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
				}catch (Exception e){}

				int size = (int)mSubTitlePaint.measureText(strTitle) + (int) COMUtil.getPixel(9) + (int) COMUtil.getPixel(6) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);	//글자+네모+글자네모 간격+범례간 기본간격

				if(columnInterval <= size) {
					columnInterval = size;
				}
			}

			//범례 그림
			for (int index = 0; index < mDrawables.size(); index++) {
				int xPos = 0;
				int yPos = 0;

				int columnIndex = index % HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
				int rowCount = index / HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
				xPos = (int) columnInterval * columnIndex + xStartPos;
				yPos = (int)COMUtil.getPixel(fontHeight)*(rowCount+1) + yStartPos;

				HorizontalStackSliceDrawable slice = mDrawables.get(index);

				//데이터를 퍼센트형으로 받을때 처리
				if(slice.getName().equals(HorizontalStackChartView.BLANK_VALUE_TITLE)) {
					continue;
				}

				if(!slice.getName().equals("데이터 입력 안됨")) {
					mAmountPaint.setColor(slice.getSliceColor());
					canvas.drawRoundRect(new RectF(xPos, yPos - COMUtil.getPixel(9), xPos + COMUtil.getPixel(9), yPos), COMUtil.getPixel(2), COMUtil.getPixel(2), mAmountPaint);
				}

				xPos+= (int) COMUtil.getPixel(9) + (int) COMUtil.getPixel(6);	//네모+글자네모 간격뒤에 텍스트 그림
				String strTitle = slice.getName()+" 0.00%";
				try {
					if(slice.getName().equals("데이터 입력 안됨"))
						strTitle = "";
					else
						strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
				} catch (Exception e){}
				canvas.drawText(strTitle.trim(), xPos, yPos, mSubTitlePaint);
			}
		}


		/*
		//아래 정렬
		if(mChart.getAlignType().equals("bottom")) {
			if (m_nType == 1) {    //선택형 범례일때
				canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y - COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT + 40), mTitlePaint);
				canvas.drawText(mPercentTitle, mPercentPoint.x - (int) COMUtil.getPixel(1), mPercentPoint.y - COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT + 40), mPercentPaint);
				canvas.drawText("%", mSubTitlePoint.x, mSubTitlePoint.y - COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT + 40), mSubTitlePaint);

				canvas.drawText(mPriceTitle, mAmountPoint.x, mAmountPoint.y - COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT + 40), mAmountPaint);
			} else if (m_nType == 2) {	//나열형 범례일때
				//10개 이상은 사이즈 줄임
				if(mDrawables.size()>10) {
					mSubTitlePaint.setTextSize(COMUtil.getPixel(9));
					fontHeight = 13;
				} else {
					mSubTitlePaint.setTextSize(COMUtil.getPixel(13));
					fontHeight = 18;
				}

				//시작 좌표 설정
				int xStartPos = (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);
				int yStartPos = 0;
				yStartPos = getBounds().top + (getBounds().height() - (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT) - ((int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN)*2) - ( (int) COMUtil.getPixel(fontHeight) * (mDrawables.size()/HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT)));

				//셀 중에 가장 넓은 것을 기준으로 x 좌표 간격을 정함
				float columnInterval = 0;
				for(int index = 0; index < mDrawables.size(); index++) {
					HorizontalStackSliceDrawable slice = mDrawables.get(index);
					String strTitle = slice.getName()+" 0.00%";
					try
					{
						strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
					}catch (Exception e){}

					int size = (int)mSubTitlePaint.measureText(strTitle) + (int) COMUtil.getPixel(30);

					if(columnInterval <= size) {
						columnInterval = size;
					}
				}

				//범례 그림
				for (int index = 0; index < mDrawables.size(); index++) {
					int xPos = 0;
					int yPos = 0;

					int columnIndex = index % HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
					int rowCount = index / HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
					xPos = (int) columnInterval * columnIndex + xStartPos;
					yPos = (int) COMUtil.getPixel(fontHeight) * rowCount + yStartPos;
//						yPos = getBounds().top + (int)COMUtil.getPixel(18)*rowCount + yStartPos;	//Top일때

					HorizontalStackSliceDrawable slice = mDrawables.get(index);
					mAmountPaint.setColor(slice.getSliceColor());
					canvas.drawRoundRect(new RectF(xPos, yPos-COMUtil.getPixel(9), xPos+COMUtil.getPixel(9),yPos), COMUtil.getPixel(2), COMUtil.getPixel(2), mAmountPaint);

					xPos+= COMUtil.getPixel(15);
					String strTitle = slice.getName()+" 0.00%";
					try
					{
						strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
					}catch (Exception e){}
					canvas.drawText(strTitle.trim(), xPos, yPos, mSubTitlePaint);
//					fTotSize += (int)mSubTitlePaint.measureText(strTitle)+(int)COMUtil.getPixel(30);
				}
			}
		//상단 정렬
		} else {
			if (m_nType == 1) {    //선택형 범례일때
				canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mTitlePaint);
				canvas.drawText(mPercentTitle, mPercentPoint.x - (int) COMUtil.getPixel(1), mPercentPoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mPercentPaint);
				canvas.drawText("%", mSubTitlePoint.x, mSubTitlePoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mSubTitlePaint);

				canvas.drawText(mPriceTitle, mAmountPoint.x, mAmountPoint.y + COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN), mAmountPaint);
			} else if (m_nType == 2) {	//나열형 범례일때
				//10개 이상은 사이즈 줄임
				if(mDrawables.size()>10) {
					mSubTitlePaint.setTextSize(COMUtil.getPixel(9));
					fontHeight = 13;
				} else {
					mSubTitlePaint.setTextSize(COMUtil.getPixel(13));
					fontHeight = 18;
				}

				//시작 좌표 설정
				int xStartPos = (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);
				int yStartPos = 0;
				yStartPos = (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN);

				//셀 중에 가장 넓은 것을 기준으로 x 좌표 간격을 정함
				float columnInterval = 0;
				for(int index = 0; index < mDrawables.size(); index++) {
					HorizontalStackSliceDrawable slice = mDrawables.get(index);
					String strTitle = slice.getName()+" 0.00%";
					try
					{
						strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
					}catch (Exception e){}

					int size = (int)mSubTitlePaint.measureText(strTitle) + (int) COMUtil.getPixel(30);

					if(columnInterval <= size) {
						columnInterval = size;
					}
				}

				//범례 그림
				for (int index = 0; index < mDrawables.size(); index++) {
					int xPos = 0;
					int yPos = 0;

					int columnIndex = index % HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
					int rowCount = index / HorizontalStackChartView.STACK_BAR_INFO_COLUMN_COUNT;
					xPos = (int) columnInterval * columnIndex + xStartPos;
					yPos = (int)COMUtil.getPixel(18)*(rowCount+1) + yStartPos;

					HorizontalStackSliceDrawable slice = mDrawables.get(index);
					mAmountPaint.setColor(slice.getSliceColor());
					canvas.drawRoundRect(new RectF(xPos, yPos-COMUtil.getPixel(9), xPos+COMUtil.getPixel(9),yPos), COMUtil.getPixel(2), COMUtil.getPixel(2), mAmountPaint);

					xPos+= COMUtil.getPixel(15);
					String strTitle = slice.getName()+" 0.00%";
					try {
						strTitle = String.format("%s %.2f%%",slice.getName(), slice.getPercent()*100);
					} catch (Exception e){}
					canvas.drawText(strTitle.trim(), xPos, yPos, mSubTitlePaint);
				}
			}
		}
		*/
	}

	public String getTitle() {
		return mTitle;
	}

	public void setStackInfo(List<HorizontalStackSliceDrawable> pDrawables) {
		mDrawables = pDrawables;
	}

	public void setType(int nType) {
		this.m_nType = nType;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
		mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleBounds);

		if(mChart.getAlignType().equals(HorizontalStackChartView.STACK_BAR_INFO_ALIGN_TOP)) {
			mTitlePoint.x = getBounds().left;
			mTitlePoint.y = mTitleBounds.height();
		} else {
			mTitlePoint.x = getBounds().left;
			mTitlePoint.y = (int)COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_HEIGHT) + (int) COMUtil.getPixel(HorizontalStackChartView.STACK_BAR_MARGIN)*2 + mTitleBounds.height();
		}
	}

	public String getPercentTitle() {
		return mPercentTitle;
	}

	public void setPercentTitle(String strPercent) {
		this.mPercentTitle = strPercent.replace("%", "");
		mPercentPaint.getTextBounds(mPercentTitle, 0, mPercentTitle.length(), mPercentBounds);

		mPercentPoint.x = getBounds().left;
		mPercentPoint.y = mTitlePoint.y + mPercentBounds.height() + COMUtil.getPixel(12);

		mSubTitlePoint.x = mPercentPoint.x + mPercentBounds.right + COMUtil.getPixel(6);
		mSubTitlePoint.y = mPercentPoint.y;
	}

	public String getPriceTitle() {
		return mPriceTitle;
	}

	public void setPriceTitle(String strPriceTitle) {
		this.mPriceTitle = strPriceTitle+"원";
		mAmountPaint.getTextBounds(mPriceTitle, 0, mPriceTitle.length(), mAmountBounds);

		mAmountPoint.x = getBounds().left;
		mAmountPoint.y = mPercentPoint.y + mPercentBounds.height() + COMUtil.getPixel(0);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.UNKNOWN;
	}

	@Override
	public void setAlpha(int alpha) {
		mAmountPaint.setAlpha(alpha);
		mTitlePaint.setAlpha(alpha);
		invalidateSelf();
	}

	public void setFontColor(int nColor) {
		m_nFontColor = nColor;
		mTitlePaint.setColor(m_nFontColor);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {}
}
