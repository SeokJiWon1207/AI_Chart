package drfn.chart.base;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import drfn.chart.MainFrame;

/**
 * Pie Chart 용 Base
 * @author lyk of kfits
 * @since 2015. 8. 17
 * @version 1.0
 * */
@SuppressLint("NewApi")
public class Base17 extends Base{
	/** 파이차트가 추가될 메인쪽 layout */
	private RelativeLayout m_Layout;
	private Context m_Context = null;
	LinearLayout pieChartLayout;
	private SpiderDiagram mChart = null;

	private MainFrame mainFrame = null;

	public Base17(Context context , RelativeLayout layout) {
		super(context);

		m_Layout = layout;
		m_Context = context;

		this.setBackgroundColor(Color.YELLOW);
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public void init() {
		super.init();

		createPieChart();
	}
	RelativeLayout.LayoutParams lpMain;
	//2015. 8. 5 파이차트 수정사항>> : 파이차트 공개 API -> 데이터설정
	public void createPieChart()
	{
		LayoutInflater factory = LayoutInflater.from(m_Context.getApplicationContext());

		int resId = m_Context.getResources().getIdentifier("spiderchart_main", "layout", m_Context.getPackageName());
		pieChartLayout = (LinearLayout) factory.inflate(resId, null);

		int resPieChartId = pieChartLayout.getResources().getIdentifier("spiderchart", "id", m_Context.getPackageName());
		mChart = (SpiderDiagram)pieChartLayout.findViewById(resPieChartId);
		mChart.setMainFrame(mainFrame);

		lpMain = (RelativeLayout.LayoutParams)m_Layout.getLayoutParams();

		mChart.setBounds(0, 0, lpMain.width, lpMain.height);

//		SpiderChartAdapter adapter = new SpiderChartAdapter(m_Context, m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors);
//		//mChart.setParent(this);
		mChart.setAdapter(null);

		this.m_Layout.addView(pieChartLayout);
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
//	    super.onSizeChanged(w, h, oldw, oldh);
//		LayoutInflater factory = LayoutInflater.from(m_Context.getApplicationContext());
//		
//		int resId = m_Context.getResources().getIdentifier("piechart_main", "layout", m_Context.getPackageName());
//		pieChartLayout = (LinearLayout) factory.inflate(resId, null);
//		
//        int resGraphPinId = pieChartLayout.getResources().getIdentifier("graphPin", "id", m_Context.getPackageName());
//		FrameLayout graphPin = (FrameLayout)pieChartLayout.findViewById(resGraphPinId);
//		FrameLayout.LayoutParams pinParams =new FrameLayout.LayoutParams(
//				(int)graphPin.getLayoutParams().width), (int)graphPin.getLayoutParams().height));
//		
//		FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)graphPin.getLayoutParams();
//		
//		pinParams.leftMargin=(int)lp.leftMargin);
//		pinParams.topMargin=(int)lp.topMargin);
//		graphPin.setLayoutParams(pinParams);
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev)
//	{
//		super.dispatchTouchEvent(ev);
//
//		if(ev.getAction() == MotionEvent.ACTION_MOVE)
//		{
//			this.m_Layout.requestDisallowInterceptTouchEvent(true);
//		}
//
//		return true;
//	}

	//API 차트크기변경 
	public void resizeChart(View view) {

	}

	public void setSliceSelect(String strParam) {
		//mChart.setSliceSelect(strParam);
	}

	public void setPacketData_hashtable(Hashtable<String, Object> data)
	{
		if(mChart==null) {
			this.createPieChart();
		} else {
			try {
				//SpiderChartAdapter adapter = new SpiderChartAdapter(m_Context, m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors);
				String strData = (String)data.get("levelDatas");
				mChart.SetData(strData, true);
			} catch(Exception e) {

			}

		}
	}
	//2015. 8. 5 파이차트 수정사항<<

	/**
	 * A very simple dynamics implementation with spring-like behavior
	 //     */
//    class SimpleDynamics extends Dynamics {
//
//        /** The friction factor */
//        private float mFrictionFactor;
//
//        /**
//         * Creates a SimpleDynamics object
//         * 
//         * @param frictionFactor The friction factor. Should be between 0 and 1.
//         *            A higher number means a slower dissipating speed.
//         * @param snapToFactor The snap to factor. Should be between 0 and 1. A
//         *            higher number means a stronger snap.
//         */
//        public SimpleDynamics(final float frictionFactor) {
//            mFrictionFactor = frictionFactor;
//        }
//
//        @Override
//        protected void onUpdate(final int dt) {
//
//            // then update the position based on the current velocity
//            mPosition += mVelocity * dt / 1000;
//
//            // and finally, apply some friction to slow it down
//            mVelocity *= mFrictionFactor;
//        }
//    }

	public void destroy() {
		mChart.setVisibility(View.GONE);
	}
}