package drfn.piechart.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import drfn.chart.base.BaseHorizontalStackChart;
import drfn.chart.util.COMUtil;

//2020.04.13 가로 Stack형 차트 수정 - hjw
public class HorizontalStackChartView extends View {
	public final String TAG = this.getClass().getSimpleName();

	private BaseHorizontalStackChart parent = null;

	public final static float STACK_BAR_MARGIN = COMUtil.getPixel(2.5f);				//스택 바 margin
	public final static int STACK_BAR_SELECTED_HEIGHT_GAB = 3;	//타입1에서 클릭된 스택의 크기 차이
	public final static int STACK_BAR_HEIGHT = 4;				//스택 바 높이
	public final static int STACK_BAR_INFO_COLUMN_COUNT = 2;	//타입2에서 범례 열 갯수

	public final static int STACK_BAR_INFO_LIST = 1;	//목록형 범례
	public final static int STACK_BAR_INFO_SELECT = 2;	//선택형 범례 (선택형 범례는 현재 프로젝트에서는 사용안함)

	public final static String STACK_BAR_INFO_ALIGN_TOP = "top";	//차트 위에
	public final static String STACK_BAR_INFO_ALIGN_BOTTOM = "bottom";	//차트 밑에

	public final static String BLANK_VALUE_TITLE = "bvt";	//퍼센트로 들어왔을때 100% 보다 적으면 나머지를 채워주는 값의 타이틀

	private Bitmap mBufferBgBmp = null;		//더블버퍼링용 bitmap
	private Canvas mCanvas = null;			//그리기가 수행될 canvas

	public Rect chart_bounds = new Rect();

	private int mTouchStartX;
	private int mTouchStartY;

	public List<HorizontalStackSliceDrawable> mDrawables;
	public HorizontalStackInfoDrawable mInfoDrawable;

	private int mCurrentIndex;

	private Paint mPaint;
	private Paint mInfoPaint;

	private int m_nSelIndex = 0;
	private int m_nColorType = 0;
	private int m_nInfoType = 0;	//범례 타잎 (0=안보임, 1=하나씩 보여주는 범례, 2=색나열형 범례)
	private String m_strAlignType = "bottom";	//범례 타입 (top, bottom)
	private String m_strValueType = "V";	//Value 타입 (V:Value형 펗센트로 변형해서 사용, P:퍼센트형 들어온 데이터 그대로씀)

	//기본 데이터
	private List<String> m_arDatas;
	private List<Float> m_arPercents;
	private List<String> m_arNames;
	private List<Integer> m_arColors;

	private DecimalFormat df = null;       // 세자리 마다 ','를 추가

	// 생성자
	public HorizontalStackChartView(Context context) {
		super(context);
		init();
	}

	public HorizontalStackChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HorizontalStackChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// 초기화
	private void init() {
		Context context = getContext();

		mDrawables = new ArrayList<HorizontalStackSliceDrawable>();

		initPaints();

		//double buffering
		if(mBufferBgBmp!=null) {
			mBufferBgBmp.recycle();
			mBufferBgBmp = null;
		}

		if(mCanvas!=null)
			mCanvas = null;
		//double buffering end
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mBufferBgBmp==null) {
			mBufferBgBmp = Bitmap.createBitmap(chart_bounds.right,chart_bounds.bottom, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBufferBgBmp);
		}

		synchronized (this) {
			mCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
			this.doDraw(mCanvas);
		}

		//double buffering
		canvas.drawBitmap(mBufferBgBmp, 0,0, null);

		if(m_nInfoType != 0) {
			if (mInfoDrawable != null) {
				mInfoDrawable.draw(canvas);
			}
		}
	}

	public void doDraw(Canvas canvas) {
		if (canvas == null || m_arDatas == null || mDrawables == null) return;

		canvas.save();

		//스택 형택의 바 그리기
		synchronized (mDrawables) {
			int nIndex = 0;
			for (HorizontalStackSliceDrawable stackSlice : mDrawables) {
				if(stackSlice.getName().equals("데이터 입력 안됨"))
					stackSlice.setNullData(true);
				stackSlice.setSliceIndex(nIndex);
				stackSlice.setCount(mDrawables.size());
				stackSlice.draw(canvas);
				nIndex++;
//				if(m_nColorType != 3) {
//					if (stackSlice.getName().equals("data1")) {
//						stackSlice.drawBackgroundBar(canvas);
//						stackSlice.draw(canvas);
//					}
//				} else {
//					if (stackSlice.getName().equals("data1"))
//						stackSlice.drawBackgroundBar_quant(canvas, "data1");
//					else
//						stackSlice.drawBackgroundBar_quant(canvas, "data2");
//					stackSlice.draw(canvas);
//				}
			}
		}
		canvas.restore();
	}

	//paint 초기화
	private void initPaints() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(70);

		mInfoPaint = new Paint();
		mInfoPaint.setColor(Color.WHITE);
	}

	public void setDatas(List<String> datas, List<Float> percents, List<String> names, List<Integer> colors) {
		m_arDatas = datas;
		m_arPercents = percents;
		m_arNames = names;
		m_arColors = colors;

		float total = 0;

		if(m_arPercents != null) {
			for (int i = 0; i < m_arPercents.size(); i++) {
				total += m_arPercents.get(i);
			}
		}

		if ((1f - total) > 0.0001f) {
			//데이터를 퍼센트형으로 받을때 처리
			if(m_strValueType.equals("P")) {	//퍼센트값의 총합이 100%가 안될경우 값을 만들어줌
				m_arNames.add(BLANK_VALUE_TITLE);
				m_arDatas.add("" + ((1f - total)*100.0));
				m_arPercents.add(1f - total);
			} else {
				return;
			}
		}

		clearDrawables();

		if (m_arDatas != null) {
			addStackSlices();
			createInfo();

			if(mDrawables!=null && mDrawables.size()>0) {
				final HorizontalStackSliceDrawable sliceView = mDrawables.get(0);
				if(sliceView!=null)
				{
					m_nSelIndex = 0;
					rotateChart(sliceView, m_nSelIndex);
				}
			}
		}
		m_arPercents.clear();
	}

	//Stack Cell 받아오기
	public HorizontalStackSliceDrawable getStackSlice(int index) {
		synchronized(mDrawables) {
			if (mDrawables.size() > index) {
				return mDrawables.get(index);
			}
		}

		return null;
	}

	//Stack Cell 데이터 매핑
	public HorizontalStackSliceDrawable getStackSlice(int position, float percentAccrue) {

		HorizontalStackSliceDrawable stackSliceView = new HorizontalStackSliceDrawable(this, getContext());
		//데이터를 퍼센트형으로 받을때 처리
		if(m_strValueType.equals("P") && m_arNames.get(position).equals(HorizontalStackChartView.BLANK_VALUE_TITLE)) {
			stackSliceView.setSliceColor(Color.rgb(240, 240, 240));
		} else {
			stackSliceView.setSliceColor(m_arColors.get(position));
		}

		stackSliceView.setPercent(m_arPercents.get(position));
		stackSliceView.setPercentAccru(percentAccrue);
		stackSliceView.setName(m_arNames.get(position));

		try {
			stackSliceView.setPrice(this.format(Double.parseDouble(m_arDatas.get(position)), 2, 3));
		} catch (Exception e) {
		}

		return stackSliceView;
	}

	//메인 이벤트 후킹
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		if(m_nColorType!=0)
			return true;

		if(ev.getAction() == MotionEvent.ACTION_MOVE) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		return true;
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if(m_nInfoType == 1) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					mTouchStartX = (int) event.getX();
					mTouchStartY = (int) event.getY();

					setSliceSelect(mTouchStartX, mTouchStartY);
					break;

				default:
					break;
			}
			return false;
		}

		return true;
	}

	//화면에서 포트폴리오 카드를 이동 후 선택된 인덱스의 색상정보를 다시 화면에 알려준다.
	public void setSliceSelect(int x, int y) {
		try {
			int selIndex = -1;

			if(mDrawables != null) {
				for (int index = 0; index < mDrawables.size(); index++) {
					HorizontalStackSliceDrawable stackSlice = mDrawables.get(index);
					if (stackSlice.containsPoint(x, y)) {
						stackSlice.setSelectedSlice(true);
						selIndex = index;
					} else {
						stackSlice.setSelectedSlice(false);
					}
				}

				if (selIndex >= 0) {
					m_nSelIndex = selIndex;

					final HorizontalStackSliceDrawable sliceView = mDrawables.get(selIndex);
					rotateChart(sliceView, selIndex);
					return;
				}
			}
		} catch (Exception e) {

		}
	}

	private void addStackSlices() {
		synchronized (mDrawables) {

			float percentAccrue = 0;
			for (int index = 0; index < m_arDatas.size(); index++) {
				final HorizontalStackSliceDrawable childSlice = getStackSlice(index, percentAccrue);

				childSlice.setBounds(getBounds());
				mDrawables.add(childSlice);

				percentAccrue += childSlice.getPercent();
			}
		}
	}

	private void createInfo() {
		if(mInfoDrawable == null) {
			mInfoDrawable = new HorizontalStackInfoDrawable(this, getContext(), getBottomBounds());
		}
		mInfoDrawable.setType(m_nInfoType);	//범례 타잎 (0=안보임, 1=하나씩 보여주는 범례, 2=색나열형 범례)

		mInfoDrawable.setStackInfo(mDrawables);
	}

	private void rotateChart(HorizontalStackSliceDrawable slice, int index) {
		synchronized (mDrawables) {
			if (mDrawables.size() == 0 || mDrawables.size() <= index || !isEnabled()) return;

			if (slice == null) {
				slice = mDrawables.get(index);
			}

			//선택한 스택 지정
			setCurrentIndex(index);
			setSliceSelectByIndex(index);

			if(mInfoDrawable!=null) {
				mInfoDrawable.setFontColor(slice.getSliceColor());
				mInfoDrawable.setTitle(slice.getName());
				mInfoDrawable.setPercentTitle(String.format("%.2f", slice.getPercent()*100) + "%");
				mInfoDrawable.setPriceTitle(slice.getPrice());
			}
		}

		invalidate();
	}

	private void setSliceSelectByIndex(int nIdx) {
		for(int i = 0; i < mDrawables.size(); i++) {
			if(nIdx == i) {
				mDrawables.get(i).setSelectedSlice(true);
			} else {
				mDrawables.get(i).setSelectedSlice(false);
			}
		}
	}

	public void clearDrawables() {
		synchronized(mDrawables) {
			if(mDrawables != null) {
				mDrawables.clear();
			}
		}
	}

	public void setColorType(int nColorType) {
		m_nColorType = nColorType;
	}

	public void setInfoType(int nInfoType) {
		m_nInfoType = nInfoType;
	}

	public int getInfoType() {
		return m_nInfoType;
	}

	public void setAlignType(String alignType) {
		m_strAlignType = alignType;
	}

	public String getAlignType() {
		return m_strAlignType;
	}

	public void setValueType(String valueType) {
		m_strValueType = valueType;
	}

	public String getValueType() {
		return m_strValueType;
	}

	public int getAllDataCount() {
		//데이터를 퍼센트형으로 받을때 처리
		if(m_strValueType.equals("P") && m_arNames != null) {
			for(int i = 0; i < m_arNames.size(); i++) {
				if(m_arNames.get(i).equals(HorizontalStackChartView.BLANK_VALUE_TITLE)) {
					return m_arDatas.size() - 1;
				}
			}
		}

		return m_arDatas.size();
	}

	private void setCurrentIndex(final int index) {
		mCurrentIndex = index;
	}

	public int getCurrentIndex() {
		return mCurrentIndex;
	}

	public void setParent(BaseHorizontalStackChart parentChart) {
		this.parent = parentChart;
	}

	//사이즈
	private Rect getBounds() {
		return chart_bounds;
	}

	private Rect getBottomBounds() {
		int left = (int) chart_bounds.left;
		int top = (int)(chart_bounds.top + chart_bounds.height() - COMUtil.getPixel(30) );	//2015. 8. 5 파이차트 수정사항 : 사각 정보창 타이틀 시작y위치 수정

		return new Rect(left, top, chart_bounds.width(), chart_bounds.bottom);
	}

	public void setBounds(int left, int top, int right, int bottom){
		chart_bounds = new Rect(left, top, right, bottom);

		if(mBufferBgBmp!=null) {
			mBufferBgBmp.recycle();
			mBufferBgBmp = null;
		}

		if(mCanvas!=null)
			mCanvas = null;

		mBufferBgBmp = Bitmap.createBitmap(right,bottom, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBufferBgBmp);
	}

	//세자리마다 콤마를 찍어주고 fl길이만큼 소수 표시
	public String format(double value, int fl, int comma) {
		value += 0.000000001;
		if(df == null)
		{
			df = new DecimalFormat();       // 세자리 마다 ','를 추가
			df.setGroupingSize(comma);
			df.setGroupingUsed(true);
			DecimalFormatSymbols symbol = new DecimalFormatSymbols();
			symbol.setGroupingSeparator(',');
			df.setDecimalFormatSymbols(symbol);
		}
		df.setMinimumFractionDigits(fl);
		df.setMaximumFractionDigits(fl);

		return df.format(value);
	}
}
