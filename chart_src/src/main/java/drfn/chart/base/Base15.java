package drfn.chart.base;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import drfn.chart.MainFrame;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.piechart.adapter.PieChartAdapter;
import drfn.piechart.extra.Dynamics;
import drfn.piechart.extra.FrictionDynamics;
import drfn.piechart.views.PieChartView;
import drfn.piechart.views.PieChartView.PieChartAnchor;

/**
 * Pie Chart 용 Base
 * @author lyk of kfits
 * @since 2015. 8. 17
 * @version 1.0
 * */
@SuppressLint("NewApi")
public class Base15 extends Base{
	/** 파이차트가 추가될 메인쪽 layout */
	private RelativeLayout m_Layout;
	private Context m_Context = null;
	public RelativeLayout pieChartLayout;
	private PieChartView mChart = null;
	private List<String> m_sliceDatas = new ArrayList<String>();
	private List<Float> m_slicePercents = new ArrayList<Float>();
	private List<String> m_sliceNames = new ArrayList<String>();
	private List<Integer> m_sliceColors = new ArrayList<Integer>();
	private List<Integer> m_sliceColorIndexes = new ArrayList<Integer>();

	private List<String> m_OrgSliceNames = new ArrayList<String>();


	/** 더블버퍼링용 bitmap */
	private Bitmap backbit = null;
	/** 그리기가 수행될 canvas 객체 */
	private Canvas m_Canvas = null;
	/** 그릴때 사용할 Paint  */
	private Paint m_Paint = null;

	private MainFrame mainFrame = null;

	public Base15(Context context , RelativeLayout layout) {
		super(context);

		m_Layout = layout;
		m_Context = context;

		//this.setBackgroundColor(Color.YELLOW);
		this.setBackgroundColor(Color.TRANSPARENT);

		//색상설정 API 예제
		List<Integer> sliceColors = new ArrayList<Integer>();
		sliceColors.add(Color.rgb(255, 255, 0));
		sliceColors.add(Color.rgb(252, 0, 42));
		sliceColors.add(Color.rgb(21, 162, 202));
		sliceColors.add(Color.rgb(252, 125, 9));
		sliceColors.add(Color.rgb(211, 44, 97));
		sliceColors.add(Color.rgb(27, 208, 40));
		sliceColors.add(Color.rgb(119, 49, 166));
		setSliceColors(sliceColors);
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

		int resId = m_Context.getResources().getIdentifier("piechart_main", "layout", m_Context.getPackageName());
		pieChartLayout = (RelativeLayout) factory.inflate(resId, null);

		int resPieChartId = pieChartLayout.getResources().getIdentifier("piechart", "id", m_Context.getPackageName());
		mChart = (PieChartView)pieChartLayout.findViewById(resPieChartId);
		mChart.setMainFrame(mainFrame);

		lpMain = (RelativeLayout.LayoutParams)m_Layout.getLayoutParams();
		mChart.setBounds(0, 0, lpMain.width, lpMain.height);

		PieChartAdapter adapter = new PieChartAdapter(m_Context, m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors, m_sliceColorIndexes);
		mChart.setParent(this);
		mChart.setAdapter(adapter);
		mChart.showInfo();

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

	public void setSliceInfo(int index) {
		String strValue = String.valueOf(index);
		try {
			if(mainFrame!=null && mainFrame.userProtocol != null)
			{
				//현재 차트의 종목코드를 같이 넘겨준다.
				Hashtable<String, Object> dic = new Hashtable<String, Object>();
				dic.put("index", strValue);
				dic.put("name", this.m_OrgSliceNames.get(index));

				//증팝태블릿일 경우는 더블탭 이벤트 발생을 메인에 알려준다
				if(mainFrame.userProtocol!=null) mainFrame.userProtocol.requestInfo(COMUtil._TAG_REQUEST_ASSET_PIE_INFO, dic);
			}
		} catch (Exception e) {

		}
	}

	public void setSliceColors(List<Integer> colors)
	{
		m_sliceColors = colors;
		if(null == m_sliceColors || m_sliceColors.size() == 0)
		{
			m_sliceColors.add(Color.rgb(255, 0, 0));
			m_sliceColors.add(Color.rgb(255, 127, 0));
			m_sliceColors.add(Color.rgb(255, 255, 0));
			m_sliceColors.add(Color.rgb(0, 255, 0));
			m_sliceColors.add(Color.rgb(0, 0, 255));
			m_sliceColors.add(Color.rgb(0, 0, 128));
			m_sliceColors.add(Color.rgb(127, 0, 127));
		}
	}

	//API 차트크기변경 
	public void resizeChart(View view) {

	}

	public void setSliceSelect(String strParam) {
		mChart.setSliceSelect(strParam);
	}

	public void setPacketData_hashtable(Hashtable<String, Object> data)
	{
		boolean bIsEmpty = false;
		m_sliceDatas = (List<String>)data.get("sliceDatas");
		if(null != m_sliceDatas && m_sliceDatas.size() == 0)
		{
			m_sliceDatas.add("1000");
			bIsEmpty = true;
		}

		float fTotal = 0;
		for(int i = 0; i < m_sliceDatas.size(); i++)
		{
			fTotal += Float.parseFloat(m_sliceDatas.get(i).trim());
		}

		m_slicePercents = new ArrayList<Float>();
		for(int i = 0; i < m_sliceDatas.size(); i++)
		{
			float fPercent = 0;
			if(fTotal != 0)
			{
				fPercent = Float.parseFloat(m_sliceDatas.get(i).trim()) / fTotal;
			}
			m_slicePercents.add(fPercent);
		}

		m_sliceNames = (List<String>)data.get("sliceNames");
		m_OrgSliceNames = (List<String>)data.get("sliceNames");
		if((null != m_sliceNames && m_sliceNames.size() == 0) || bIsEmpty)
		{
			m_sliceNames.clear();
			m_sliceNames.add("데이터 입력 안됨");
		}

		m_sliceColorIndexes = (List<Integer>)data.get("sliceColorIndexes");


		String colorType = (String)data.get("colorType");
		String strEmpty = "";
		if(m_sliceNames != null && m_sliceNames.size() >0) {
			strEmpty = m_sliceNames.get(0);
		}
		int nColorType = 0;
		if(colorType!=null) {
			if(strEmpty.equals("데이터 입력 안됨")) {
				List<Integer> sliceColors = new ArrayList<Integer>();
				sliceColors.add(Color.rgb(240, 240, 240));
				setSliceColors(sliceColors);
			} else {
				if (colorType.equals("1")) { //펀드유형
					List<Integer> sliceColors = new ArrayList<Integer>();
					sliceColors.add(Color.rgb(163, 241, 228));
					sliceColors.add(Color.rgb(113, 232, 236));
					sliceColors.add(Color.rgb(100, 218, 255));
					sliceColors.add(Color.rgb(98, 203, 249));
					sliceColors.add(Color.rgb(91, 166, 251));
					sliceColors.add(Color.rgb(91, 144, 251));
					sliceColors.add(Color.rgb(104, 115, 255));
					sliceColors.add(Color.rgb(114, 91, 232));

					//임시
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));

					setSliceColors(sliceColors);
				} else if (colorType.equals("2")) { //투자지역 유형 (6가지)
					List<Integer> sliceColors = new ArrayList<Integer>();
					sliceColors.add(Color.rgb(72, 139, 226));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));

					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));

					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));
					sliceColors.add(Color.rgb(233, 234, 239));

					setSliceColors(sliceColors);
				} else if (colorType.equals("3")) { //mini type(점수 표시)
					List<Integer> sliceColors = new ArrayList<Integer>();
					sliceColors.add(Color.rgb(213, 213, 213));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));
					sliceColors.add(Color.argb(0,255, 255, 255));

					setSliceColors(sliceColors);
				} else if (colorType.equals("4")) { //원큐 펀드 유형 (4가지)
					List<Integer> sliceColors = new ArrayList<Integer>();
					sliceColors.add(Color.rgb(72, 201, 207));
					sliceColors.add(Color.rgb(89, 110, 146));
					sliceColors.add(Color.rgb(219, 219, 248));
					sliceColors.add(Color.rgb(192, 192, 192));

					//임시
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					sliceColors.add(Color.rgb(199, 199, 199));
					setSliceColors(sliceColors);
				}
			}
		}

		try {
			nColorType = Integer.parseInt(colorType);
			mChart.setColorType(nColorType);
//			if (m_sliceColorIndexes == null) {
//				m_sliceColorIndexes = new ArrayList<Integer>();
//				for (int i = 0; i < m_sliceNames.size(); i++)
//					m_sliceColorIndexes.add(getColorIndex(nColorType, m_sliceNames.get(i), i));
//			}
		} catch (Exception e) {
		}

		try {
			String toolTipType = "";
			if (data.containsKey("toolTipType"))
				toolTipType = (String) data.get("toolTipType");
			if (!toolTipType.equals(""))
				mChart.setToolTipType(toolTipType);
		} catch (NullPointerException e) {

		}

		//그룹코드에 따라 그룹명과 색상을 재정의한다
		/* COMUtil.getAssetGroupData
		item.put("groupid", "AG01");
    	item.put("name", "국내주식");
    	item.put("color", Color.rgb(251, 138, 46));
		 */
		//잠시 막
//		List<Integer> assetColors = new ArrayList<Integer>();
//		List<String> assetNames = new ArrayList<String>();
//		for(int i=0; i<m_sliceNames.size(); i++) {
//			for(int k=0; k<COMUtil.getAssetGroupData().size(); k++) {
//				Hashtable<String, Object> item = (Hashtable<String, Object>)(COMUtil.getAssetGroupData().get(k));
//				if(m_sliceNames.get(i).equals((String)item.get("groupid"))) {
//					assetNames.add((String)item.get("name"));
//					assetColors.add((Integer)item.get("color"));
//					break;
//				}
//			}
//		}
//		if(assetNames.size()>0) {
//			this.m_sliceNames = assetNames;
//			setSliceColors(assetColors);
//		}
		//그룹코드에 따라 그룹명과 색상을 재정의한다 end

		if(mChart==null) {
			this.createPieChart();
		} else {
			try {
				PieChartAdapter adapter = new PieChartAdapter(m_Context, m_sliceDatas, m_slicePercents, m_sliceNames, m_sliceColors, m_sliceColorIndexes);
				String title = (String)data.get("title");
				if(title != null)
				{
					mChart.setTitle(title);
				}

				mChart.setAdapter(adapter);
			} catch(Exception e) {

			}

//			mChart.onResume();
		}

		String alignType = (String)data.get("alignType");
		if(alignType!=null) {
			if(alignType.equals("bottom")) {
//				if(graphPin!=null)
//					graphPin.setVisibility(View.GONE);
//				if(textViewLayout!=null)
//					textViewLayout.setVisibility(View.GONE);
//
//				if(graphDot!=null)
//					graphDot.setVisibility(View.VISIBLE);
				mChart.setSnapToAnchor(PieChartAnchor.BOTTOM);
			} else {
//				if(alignType.equals("")) { //rightTop
////					if(graphPin!=null)
////						graphPin.setVisibility(View.VISIBLE);
//					if(textViewLayout!=null)
//						textViewLayout.setVisibility(View.VISIBLE);
////					if(graphDot!=null)
////						graphDot.setVisibility(View.GONE);
//					//mChart.setSnapToAnchor(PieChartAnchor.RIGHTTOP);
//				}
			}
		} else {
//			if(graphPin!=null)
//				graphPin.setVisibility(View.VISIBLE);
//			if(textViewLayout!=null)
//				textViewLayout.setVisibility(View.VISIBLE);
//			if(graphDot!=null)
//				graphDot.setVisibility(View.GONE);
//			mChart.setSnapToAnchor(PieChartAnchor.RIGHTTOP);
		}

		//SurfaceView 차트 이미지 캐쉬
//		updateCache();

//		invalidate();
	}
	//2015. 8. 5 파이차트 수정사항<<

	/**
	 * A very simple dynamics implementation with spring-like behavior
	 */
	class SimpleDynamics extends Dynamics {

		/** The friction factor */
		private float mFrictionFactor;

		/**
		 * Creates a SimpleDynamics object
		 *
		 * @param frictionFactor The friction factor. Should be between 0 and 1.
		 *            A higher number means a slower dissipating speed.
		 * @param snapToFactor The snap to factor. Should be between 0 and 1. A
		 *            higher number means a stronger snap.
		 */
		public SimpleDynamics(final float frictionFactor) {
			mFrictionFactor = frictionFactor;
		}

		@Override
		protected void onUpdate(final int dt) {

			// then update the position based on the current velocity
			mPosition += mVelocity * dt / 1000;

			// and finally, apply some friction to slow it down
			mVelocity *= mFrictionFactor;
		}
	}

//    protected void onWindowVisibilityChanged(int visibility) {
//    	super.onWindowVisibilityChanged(visibility);
//    	if(visibility == View.INVISIBLE || visibility == View.GONE) {
//    		this.setVisibility(visibility);
//    	} else {
//    		if(visibility == View.VISIBLE ) {
//        		this.setVisibility(visibility);
//        	}
//    	}
//    	this.updateCache();
//    }
	/**
	 * Update the drawing cache with the latest changes
	 * from the Pie Chart
	 */
//	public void updateCache() {
//		
//		// Wait for the info panel transition
//		postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				createCache();
//			}
//		}, 400);
//	}

	/**
	 * Create the background drawable for when
	 * the chart is moved.
	 */
	private Bitmap mDrawingCache;

//	BitmapDrawable mDrawableCache;
//	private void createCache() {
//		
//		new AsyncTask<Void, Void, Boolean>() {
//    		
//			@Override
//			protected Boolean doInBackground(Void... params) {
//
//				if (mDrawingCache == null) {
//					mDrawingCache = Bitmap.createBitmap(lpMain.height, lpMain.height, Bitmap.Config.ARGB_8888);
//				}
//
//				Canvas cache = new Canvas(mDrawingCache);
//				cache.drawColor(Color.BLUE);
////				mChart.getDrawThread().drawCache(cache);
//				
//				mDrawableCache = new BitmapDrawable(getResources(), mDrawingCache);
//
//				return true;
//			}
//    		
//    		@Override
//    		protected void onPostExecute(Boolean result) {
//
//				setCachedBackground(mDrawableCache);
//				
////				if (mOnPieChartReadyListener != null) {
////					mOnPieChartReadyListener.onPieChartReady();
////				}
//    		}
//			
//		}.execute();
//	}

	//	@SuppressWarnings("deprecation")
//	@SuppressLint("NewApi")
//	private void setCachedBackground(Drawable drawable) {
//		
//		int sdk = android.os.Build.VERSION.SDK_INT;
//		
//		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//		    setBackgroundDrawable(drawable);
//		} else {
//		    setBackground(drawable);
//		}
//	}
	public void destroy() {
		mChart.setVisibility(View.GONE);
	}
}