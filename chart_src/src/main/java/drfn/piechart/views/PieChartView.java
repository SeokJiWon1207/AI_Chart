package drfn.piechart.views;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import drfn.chart.MainFrame;
import drfn.chart.base.Base15;
import drfn.chart.util.COMUtil;
import drfn.piechart.adapter.BasePieChartAdapter;
import drfn.piechart.extra.Dynamics;
import drfn.piechart.extra.UiUtils;
import drfn.piechart.views.ThreadAnimator.AnimationListener;

//import android.view.SurfaceHolder;
//import android.view.SurfaceView;

/**
 * A view that creates a Pie Chart which is backed by an adapter providing the
 * data for each slice of the pie chart.  The chart can be rotated by touch and
 * automatically snaps to one of the compass points, defaults to {@link PieChartAnchor#RIGHT}.  
 * To turn off the rotation feature simply call {@link #setEnabled(boolean)} and
 * set it to false.
 *
 * @author Saul Howard
 * @version 1.0
 */
public class PieChartView extends View /*implements OnGestureListener*/ {

	public final String TAG = this.getClass().getSimpleName();

	private String m_strArrowPositionType = "B";	//2015. 8. 5 파이차트 수정사항

	private Base15 parent = null;

	/** 더블버퍼링용 bitmap */
	private Bitmap backbit = null;

	/** 그리기가 수행될 canvas 객체 */
	private Canvas m_Canvas = null;

	public Rect chart_bounds = new Rect();

	private boolean m_init = true;

	private MainFrame mainFrame = null;

	public enum PieChartAnchor {

		TOP (270),
		RIGHT (1),
		RIGHTTOP (336),
		BOTTOM (90),
		LEFT (180);

		private float degrees;

		PieChartAnchor(float degrees) {
			this.degrees = degrees;
		}
	};

	//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		// NOOP
//		return false;
//	}
	private static final int SUB_STROKE_WIDTH = 1;
	private static final int INFO_STROKE_WIDTH = 3;

	/** Unit used for the velocity tracker */
	private static final int PIXELS_PER_SECOND = 1000;

	/** Tolerance for the velocity */
	private static final float VELOCITY_TOLERANCE = 40f;

	/** Represents an invalid child index */
	private static final int INVALID_INDEX = -1;

	/** Represents a touch to the info circle */
	private static final int INFO_INDEX = -2;

	/** User is not touching the chart */
	public static final int TOUCH_STATE_RESTING = 0;

	/** User is touching the list and right now it's still a "click" */
	private static final int TOUCH_STATE_CLICK = 1;

	/** User is rotating the chart */
	public static final int TOUCH_STATE_ROTATE = 2;

	public static final int CHART_HIDDEN = 0;
	public static final int CHART_SHOWING = 1;
	public static final int CHART_INVALID = 2;

	/** Default degree to snap to */
	private static final float DEFAULT_SNAP_DEGREE = 0f;

//	private DrawThread mDrawThread;

	/** Current touch state */
	private int mTouchState = TOUCH_STATE_RESTING;

	/** Distance to drag before we intercept touch events */
	private int mScrollThreshold;

	/** Velocity tracker used to get fling velocities */
	private VelocityTracker mVelocityTracker;

	/** Dynamics object used to handle fling and snap */
	private Dynamics mDynamics;

	/** Runnable used to animate fling and snap */
	private Runnable mDynamicsRunnable;

	/** Used to check for long press actions */
	private Runnable mLongPressRunnable;

	/** The adapter with all the data */
	private BasePieChartAdapter mAdapter;

	/** X-coordinate of the down event */
	private int mTouchStartX;

	/** Y-coordinate of the down event */
	private int mTouchStartY;

	/** The degree to snap the chart to when rotating */
	private float mSnapToDegree = 315.0f;

	/** Our starting rotation degree */
	private float mRotationStart = 0;

	/** The last rotation degree after touch */
	private float mLastRotation = 0;

	/** The rotating direction of the chart */
	private boolean mRotatingClockwise;

	/** The diameter of the chart */
	private int mChartDiameter;

	private float mInfoRadius;

	/** The pixel density of the current device */
	private float mPixelDensity;

	/** The center point of the chart */
	private PointF mCenter = new PointF();

	private float mStrokeWidth;

	/** The current degrees of rotation of the chart */
	private float mRotationDegree = 156; //최초 시작할 각도(디폴트:topright)

	private float mChartScale = 1.0f;

	private boolean mChartHidden = false;

	private boolean mNeedsToggle = false;

	private boolean mNeedsUpdate = false;

	private boolean mShowInfo = false;

	private boolean mLoaded = false;

	private List<PieSliceDrawable> mDrawables;

	private LinkedList<PieSliceDrawable> mRecycledDrawables;

	//2015. 8. 5 파이차트 수정사항 : 정보 표시 영역 >>
	private CenterCircleInfoDrawable mCenterCircleInfoDrawable;
	private InfoDrawable mInfoDrawable;
	private ArrowDrawable mArrowDrawable;
	//2015. 8. 5 파이차트 수정사항 : 정보 표시 영역 <<

	private CaretDrawable mCaret;

	private int mInfoAlpha = 255;

	/** The current snapped-to index */
	private int mCurrentIndex;

	private Bitmap mDrawingCache;

	private OnPieChartChangeListener mOnPieChartChangeListener;

	private OnItemLongClickListener mOnItemLongClickListener;

	private OnInfoClickListener mOnInfoClickListener;

	private OnPieChartExpandListener mOnPieChartExpandListener;

	private OnPieChartReadyListener mOnPieChartReadyListener;
	/**
	 * The listener that receives notifications when an item is clicked.
	 */
	private OnItemClickListener mOnItemClickListener;

	private OnRotationStateChangeListener mOnRotationStateChangeListener;

	private AdapterDataSetObserver mDataSetObserver;

	private Handler mHandler;

	private Paint mPaint;
	private Paint mInfoPaint;

	private Paint mStrokePaint;

	private boolean m_bIsMoving = false;	//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해

	private Bitmap backgroundBitmap = null;

	private Canvas c1 = null;

	private String m_strTitle="";

	private int m_nColorType = 0;

	private String m_strToolTipType = "P";

	private void setTouchState(int touchState) {

		mTouchState = touchState;

		if (mOnRotationStateChangeListener != null) {
			mOnRotationStateChangeListener.onRotationStateChange(mTouchState);
		}
	}

	public Bitmap getDrawingCache() {
		return mDrawingCache;
	}

	/**
	 * Set the dynamics object used for fling and snap behavior.
	 *
	 * @param dynamics The dynamics object
	 */
	public void setDynamics(final Dynamics dynamics) {

		if (mDynamics != null) {
			dynamics.setState((float) getRotationDegree(), mDynamics.getVelocity(), AnimationUtils
					.currentAnimationTimeMillis());
		}

		mDynamics = dynamics;
	}

	public void setParent(Base15 base15) {
		this.parent = base15;
	}

	/**
	 * Is the current Pie Chart hidden.
	 *
	 * @return True if the chart is hidden, false otherwise.
	 */
	public boolean isChartHidden() {
		return mChartHidden;
	}

	/**
	 * Set the rotation degree of the chart.
	 *
	 * @param rotationDegree the degree to rotate the chart to
	 */
	void setRotationDegree(float rotationDegree) {

		// Keep rotation degree positive
		if (rotationDegree < 0) rotationDegree += 360;

		// Keep rotation degree between 0 - 360
		mRotationDegree = rotationDegree % 360;

//		System.out.println("Debug_setRotationDegree:"+rotationDegree);
	}

	public float getRotationDegree() {
		return mRotationDegree;
	}

	public int getCurrentIndex() {

		if (!isLoaded()) return 0;

		return mCurrentIndex;
	}

	/**
	 * <b>Internal Use Only</b> Sets the currently selected index
	 * and fires of the selection change listener if one is
	 * attached.
	 *
	 * @param index The current index
	 */
	private void setCurrentIndex(final int index) {

		mCurrentIndex = index;

		if (mNeedsToggle) {
			mNeedsToggle = false;
			toggleChart();
		}

		if (mOnPieChartChangeListener != null && !mChartHidden && isLoaded()) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {

					mOnPieChartChangeListener.onSelectionChanged(index);
				}
			});
		}
	}

	public void setSnapToAnchor(PieChartAnchor anchor) {
		//2015. 8. 5 파이차트 수정사항>>
		if(PieChartAnchor.values()[3] == anchor)	//90도 (6시방향)
		{
//			m_strArrowPositionType = "B";
			mRotationDegree = 270; //시작위치 
		} else if(PieChartAnchor.values()[2] == anchor)	//90도 (6시방향)
		{
//			m_strArrowPositionType = "RT";
			mRotationDegree = 156; //시작위치 
		}
		else if(PieChartAnchor.values()[1] == anchor)	//0도 (3시방향)
		{
//			m_strArrowPositionType = "R";
		}
		//2015. 8. 5 파이차트 수정사항<<

		mSnapToDegree = anchor.degrees;
//		snapTo();

		//초기 실행시
		try {
			final PieSliceDrawable sliceView = mDrawables.get(0);
			float degree = sliceView.getSliceCenter();
			animateTo(mRotationDegree-degree, mSnapToDegree-degree);
		} catch(Exception e) {

		}
	}

	public float getChartDiameter() {
		return mChartDiameter;
	}

	public float getChartRadius() {
		return mChartDiameter / 2f;
	}

	public synchronized boolean isLoaded() {
		return mLoaded;
	}

	public synchronized void setLoaded(boolean mLoaded) {
		this.mLoaded = mLoaded;
	}

	/**
	 * Gets the {@link CenterCircleInfoDrawable} used to draw the info panel.
	 *
	 * @return The {@link CenterCircleInfoDrawable}
	 */
	public CenterCircleInfoDrawable getCenterCircleInfoDrawable() {

		createCenterCircleInfo();

		return mCenterCircleInfoDrawable;
	}

	//2015. 8. 5 파이차트 수정사항>>
	public InfoDrawable getInfoDrawable() {

		createInfo();

		return mInfoDrawable;
	}

	public ArrowDrawable getArrowDrawable() {

		createArrow();

		return mArrowDrawable;
	}
	//2015. 8. 5 파이차트 수정사항<<

	/**
	 * Sets the Pie Chart's slice selection.
	 *
	 * @param index The index to select
	 */
	public void setSelection(int index) {
		animateTo(index);
	}

	/**
	 * Gets the drawing thread
	 *
	 * @return {@link DrawingThread}
	 */
//	public DrawThread getDrawThread() {
//		return mDrawThread;
//	}

//	public void onPause() {
//		mDrawThread.onPause();
//	}
//	
//	public void onResume() {
//		mDrawThread.onResume();
//	}

	public void setTitle(String strTitle)
	{
		m_strTitle = strTitle;
	}

	public OnPieChartChangeListener getOnPieChartChangeListener() {
		return mOnPieChartChangeListener;
	}

	public void setOnPieChartChangeListener(
			OnPieChartChangeListener mOnPieChartChangeListener) {
		this.mOnPieChartChangeListener = mOnPieChartChangeListener;
	}

	public void setOnPieChartExpandListener(
			OnPieChartExpandListener mOnPieChartExpandListener) {
		this.mOnPieChartExpandListener = mOnPieChartExpandListener;
	}

	public void setOnPieChartReadyListener(
			OnPieChartReadyListener mOnPieChartReadyListener) {
		this.mOnPieChartReadyListener = mOnPieChartReadyListener;
	}

	public OnRotationStateChangeListener getOnRotationStateChangeListener() {
		return mOnRotationStateChangeListener;
	}

	public void setOnRotationStateChangeListener(
			OnRotationStateChangeListener mOnRotationStateChangeListener) {
		this.mOnRotationStateChangeListener = mOnRotationStateChangeListener;
	}

	public OnItemLongClickListener getOnItemLongClickListener() {
		return mOnItemLongClickListener;
	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener mOnItemLongClickListener) {
		this.mOnItemLongClickListener = mOnItemLongClickListener;
	}

	public OnItemClickListener getOnItemClickListener() {
		return mOnItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	public void setOnInfoClickListener(OnInfoClickListener mOnInfoClickListener) {
		this.mOnInfoClickListener = mOnInfoClickListener;
	}

	/**
	 * Returns the {@link PieSliceDrawable} for the given index
	 *
	 * @param index The position of the slice
	 *
	 * @return A {@link PieSliceDrawable}
	 */
	public PieSliceDrawable getSlice(int index) {

		synchronized(mDrawables) {

			if (mDrawables.size() > index) {
				return mDrawables.get(index);
			}
		}

		return null;
	}

	/**
	 * Show the info panel
	 */
	public void showInfo() {
		mShowInfo = true;
//		mChartHidden = true;
//		mChartScale = 0f;
		mChartHidden = false;
		mChartScale = 1f;
	}

	/**
	 * Hide the info panel
	 */
	public void hideInfo() {
		mShowInfo = false;
		mChartHidden = false;
		mChartScale = 1f;
	}

	public PieChartView(Context context) {
		super(context);

		init();
	}

	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public PieChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	/**
	 * Creates and initializes a new PieChartView
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PieChartView(Context context, OnPieChartReadyListener listener) {
		super(context);

		setOnPieChartReadyListener(listener);
		init();
	}

	private void init() {

		Context context = getContext();

		mHandler = new Handler();

//        getHolder().addCallback(this);
//////		setZOrderOnTop(true);
////		getHolder().setFormat(PixelFormat.TRANSLUCENT);
////		this.setZOrderMediaOverlay(true);
////		this.setBackgroundColor(Color.TRANSPARENT);
//////		this.setDrawingCacheEnabled(true);
//		
//		//
//		this.setZOrderOnTop(true);    // necessary
//		getHolder().setFormat(PixelFormat.TRANSPARENT);
//		//
//		
//		// Background (중요 : SurfaceView에서 bacground를 설정하는 방법!)		
////		backgroundBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
////	    c1 = new Canvas(backgroundBitmap);
////	    c1.drawColor(Color.WHITE);
//
//        mDrawThread = new DrawThread(this, mHandler, this);

		mScrollThreshold = ViewConfiguration.get(context).getScaledTouchSlop();
		mPixelDensity = UiUtils.getDisplayMetrics(context).density;
		mStrokeWidth = UiUtils.getDynamicPixels(context, SUB_STROKE_WIDTH);

		mDrawables = new ArrayList<PieSliceDrawable>();
		mRecycledDrawables = new LinkedList<PieSliceDrawable>();

		initPaints();

		//thread
//		if (mDrawThread.getState() == Thread.State.TERMINATED) {
//		
//			mDrawThread = new DrawThread(this, mHandler, this);
//			mDrawThread.setRunning(true);
//			mDrawThread.start();
//			
//	    } else {
//	    	
//	    	mDrawThread.setRunning(true);
//	    	mDrawThread.start();
//	    }
		//thread end

		//double buffering
		if(backbit!=null)
		{
			backbit.recycle();
			backbit = null;
		}

		if(m_Canvas!=null)
			m_Canvas = null;

		//double buffering end
	}

	public void setBounds(int left, int top, int right, int bottom){
//		top += (int)COMUtil.getPixel(20);
//		bottom -= (int)COMUtil.getPixel(40);
//		chart_bounds = new Rect(left, top, right, bottom); //left, top, right, bottom
		chart_bounds = new Rect(left, top, right, right); //left, top, right, bottom

		int nRadiusX = right-left;
		int nRadiusY = bottom-top;
//		if(nRadiusX<nRadiusY)
//		{
			mCenter.x = (float) nRadiusX / 2;
			mCenter.y = (float) nRadiusX / 2;

			mChartDiameter = nRadiusX;
//		}
//		else
//		{
//			mCenter.x = (float) nRadiusY / 2;
//			mCenter.y = (float) nRadiusY / 2;
//
//			mChartDiameter = nRadiusY;
//		}

//		mInfoRadius = getChartRadius() / 2;
		mInfoRadius = getChartRadius();

		if(backbit!=null) {
			backbit.recycle();
			backbit = null;
		}

		if(m_Canvas!=null)
			m_Canvas = null;

		backbit = Bitmap.createBitmap(right,bottom, Bitmap.Config.ARGB_8888);
		m_Canvas = new Canvas(backbit);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		System.out.println("============Debug_onDraw");
		super.onDraw(canvas);
		if(backbit==null) {
			backbit = Bitmap.createBitmap(chart_bounds.right,chart_bounds.bottom, Bitmap.Config.ARGB_8888);
			m_Canvas = new Canvas(backbit);
		}

//		m_Canvas.drawColor(Color.GREEN);

//		mPaint.setStyle(Style.FILL);
//		mPaint.setColor(Color.rgb(0,0,0));
//		m_Canvas.drawLine(Color.CYAN, 10, 100, 50, mPaint);

		// Update our animator objects

		// If the drawing cache is null build it
		if (mDrawingCache == null) {
			buildDrawingCache1();
			//double buffering
			canvas.drawBitmap(mDrawingCache, 0,0, null);
		}

		synchronized (this) {
			//updateAnimators();
			//m_Canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
			m_Canvas.drawColor(Color.rgb(255,255,255), PorterDuff.Mode.CLEAR);
			this.doDraw(m_Canvas);
		}

		//double buffering
		canvas.drawBitmap(backbit, 0,0, null);

//		drawChart(canvas);
//		mDrawThread.doDraw(m_Canvas);
	}

	private void buildDrawingCache1() {

		if (mDrawingCache == null) {
			mDrawingCache = Bitmap.createBitmap(chart_bounds.right,chart_bounds.bottom, Bitmap.Config.ARGB_8888);
		}

		Canvas cache = new Canvas(mDrawingCache);
		doDraw(cache, mRotationDegree, mChartScale, mShowInfo);
	}

	/**
	 * Update our animators that control animating the
	 * rotation, scale, and info panel alpha
	 */
	private void updateAnimators() {

		if (mRotateAnimator != null && mRotateAnimator.isRunning()) {
			setRotationDegree(mRotateAnimator.floatUpdate());
			this.invalidate();
		}

		if (mScaleAnimator != null && mScaleAnimator.isRunning()) {
			mChartScale = mScaleAnimator.floatUpdate();
		}

		if (mInfoAnimator != null && mInfoAnimator.isRunning()) {
			mInfoAlpha = mInfoAnimator.intUpdate();
		}
	}

	/**
	 * Draw to the supplied canvas using the current state of the
	 * rotation, scale and info panel variables.
	 *
	 * @param canvas the canvas to draw to
	 */
	public void doDraw(Canvas canvas) {
		doDraw(canvas, mRotationDegree, mChartScale, mShowInfo);
	}
	/**
	 * Draw the pie chart
	 *
	 * @param canvas The canvas to draw the chart on
	 * @param rotationDegree The current rotation of the chart
	 * @param scale The scale of the chart
	 * @param showInfo Should the info panel be drawn
	 */
	private void doDraw(Canvas canvas, float rotationDegree, float scale, boolean showInfo) {
		if (canvas == null || mAdapter == null) return;
		if (scale != 0) {
			// Scale and rotate the canvas
			canvas.save();
			canvas.scale(scale, scale, mCenter.x, mCenter.y);
			//canvas.rotate(rotationDegree, mCenter.x, mCenter.y);
//			System.out.println("Debug_canvas_rotate:"+rotationDegree);
			canvas.translate(getPaddingLeft(), getPaddingTop());

			// Draw a background circle
			//2015. 8. 5 파이차트 수정사항>> : 파이차트 배경 원 색상
			//		   	Paint mBgCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//		   	mBgCirclePaint.setColor(Color.rgb(230, 230, 230));
			//		   	//2015. 8. 5 파이차트 수정사항<<
			//		canvas.drawCircle(mCenter.x, mCenter.y, getChartRadius(), mBgCirclePaint);

			// Draw all of the pie slices
			synchronized (mDrawables) {
				for (PieSliceDrawable slice : mDrawables) {
					if (m_nColorType == 2 && slice.getName().equals("상위"))
						slice.setSelectedSlice(true);
					slice.draw(canvas);
				}
			}

			canvas.restore();
		}
		//2015. 8. 5 파이차트 수정사항 : 회전중일때는 부채꼴의 %정보 숨기기
		if(!m_bIsMoving)
		{
			for (PieSliceDrawable slice : mDrawables) {
				if(null == slice.getSlicePercentDrawable())
				{
					slice.setSlicePercentDrawable(this, getContext());
				}
				slice.getSlicePercentDrawable().draw(canvas);
			}
		}
		// Draw the info panel if it has been set to show
		if (showInfo) {

			//2015. 8. 5 파이차트 수정사항 : 가운데 원 정보창 그리기

//		createCaret();
			//
//		       canvas.drawCircle(mCenter.x, mCenter.y, mInfoRadius, mStrokePaint);
//		       canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+mInfoRadius+10, mInfoRadius*3, mCenter.y+mInfoRadius*2+70, mStrokePaint);
//		       mCaret.draw(canvas);
////		       mCaret.setCaretRotation(mSnapToDegree);
////		       canvas.drawCircle(mCenter.x, mCenter.y, mInfoRadius, mPaint);
//		       canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+mInfoRadius+10, mInfoRadius*3, mCenter.y+mInfoRadius*2+70, mPaint);
			if (m_nColorType==3) {
				mPaint.setColor(Color.rgb(8, 172, 145));
				try {
					int nTitle = Integer.parseInt(m_strTitle);
					if (nTitle <= 40)
						mPaint.setColor(Color.rgb(8, 172, 145));
					else if (40 < nTitle && nTitle < 81)
						mPaint.setColor(Color.rgb(73, 80, 166));
					else
						mPaint.setColor(Color.rgb(239, 99, 36));
				} catch (Exception e){

				}
			}
			else
				mPaint.setColor(Color.WHITE);
			if(mCenterCircleInfoDrawable!=null) {
//				if(m_nColorType == 1 || m_nColorType == 2)
//					canvas.drawCircle(getCenterCircleBounds().left+mCenter.x, getCenterCircleBounds().top+mCenter.y, getChartRadius()/5*4, mPaint);

				if(m_nColorType == 3)
					canvas.drawCircle(getCenterCircleBounds().left+mCenter.x, getCenterCircleBounds().top+mCenter.y, getChartRadius() - COMUtil.getPixel(3), mPaint);
				else if(m_nColorType == 4)
					canvas.drawCircle(getCenterCircleBounds().left+mCenter.x, getCenterCircleBounds().top+mCenter.y, (float) (getChartRadius()*0.63), mPaint);
				mCenterCircleInfoDrawable.draw(canvas);
			}

			if(mInfoDrawable!=null) {
				//2015. 8. 5 파이차트 수정사항 : 하단 정보창 그리기
//	        	canvas.drawRect(mCenter.x+mInfoRadius, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 20), 
//	        	mInfoRadius*3, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 150), mStrokePaint);
//		       mCaret.draw(canvas);
//	        	canvas.drawRect(mCenter.x+mInfoRadius, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 20), 
//	        	mInfoRadius*3, mCenter.y+getChartRadius() +  + UiUtils.getDynamicPixels(getContext(), 150), mInfoPaint);
				mInfoDrawable.draw(canvas);
			}

			//2015. 8. 5 파이차트 수정사항 : 하단 화살표
//	        mArrowDrawable.draw(canvas);
		}
	}
	private void initPaints() {

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
//		mPaint.setAlpha(70);
		mPaint.setAlpha(255);

		mStrokePaint = new Paint(mPaint);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(UiUtils.getDynamicPixels(getContext(), INFO_STROKE_WIDTH));
		mStrokePaint.setColor(Color.BLACK);
		mStrokePaint.setAlpha(50);

		//2015. 8. 5 파이차트 수정사항>> : 부채꼴 정보 표시  
		mInfoPaint = new Paint();
		mInfoPaint.setColor(Color.WHITE);
		//2015. 8. 5 파이차트 수정사항<<
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/**
	 * 파이영역에서 메인 이벤트를 사용하지 않도록 설정하기 위함
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		super.dispatchTouchEvent(ev);

		if(ev.getAction() == MotionEvent.ACTION_MOVE)
		{
			if(mainFrame.userProtocol != null) {
				mainFrame.userProtocol.requestInfo(COMUtil._TAG_REQUEST_DISALLOWINTERCEPT_TOUCHEVENT, (Hashtable) null);
			}
		}

		return true;
	}
	@Override
	public boolean onTouchEvent(final MotionEvent event) {

		if ((!inCircle((int) event.getX(), (int) event.getY()) &&
				mTouchState == TOUCH_STATE_RESTING)) {
			return false;
		}

		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				startTouch(event);
				m_bIsMoving = false;
				break;

			case MotionEvent.ACTION_MOVE:
				if(Math.abs(mTouchStartX - (int)event.getX()) > UiUtils.getDynamicPixels(getContext(), 5) || Math.abs(mTouchStartY - (int)event.getY()) > UiUtils.getDynamicPixels(getContext(), 5))
				{
					m_bIsMoving = true;
				}
				if (mTouchState == TOUCH_STATE_CLICK) {
					startScrollIfNeeded(event);
					this.invalidate();
				}

				if (mTouchState == TOUCH_STATE_ROTATE) {
					mVelocityTracker.addMovement(event);
					rotateChart(event.getX(), event.getY());
					this.invalidate();
				}
				break;

			case MotionEvent.ACTION_UP:
				float velocity = 0;
				m_bIsMoving = true;

				if (mTouchState == TOUCH_STATE_CLICK) {

					clickChildAt((int) event.getX(), (int) event.getY());

				} else if (mTouchState == TOUCH_STATE_ROTATE) {

					mVelocityTracker.addMovement(event);
					mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND);

					velocity = calculateVelocity();
				}

				endTouch(event.getX(), event.getY(), velocity);
				break;

			default:
				endTouch(event.getX(), event.getY(), 0);
				break;
		}

		return true;
	}

	/**
	 * Calculates the overall vector velocity given both the x and y
	 * velocities and normalized to be pixel independent.
	 *
	 * @return the overall vector velocity
	 */
	private float calculateVelocity() {

		int direction = mRotatingClockwise ? 1 : -1;

		float velocityX = mVelocityTracker.getXVelocity() / mPixelDensity;
		float velocityY = mVelocityTracker.getYVelocity() / mPixelDensity;
		float velocity = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY) * direction / 2;

		return velocity;
	}

	/**
	 * Sets and initializes all things that need to when we start a touch
	 * gesture.
	 *
	 * @param event The down event
	 */
	private void startTouch(final MotionEvent event) {

		// user is touching the list -> no more fling
		removeCallbacks(mDynamicsRunnable);

		mLastRotation = getRotationDegree();

		// save the start place
		mTouchStartX = (int) event.getX();
		mTouchStartY = (int) event.getY();

		// start checking for a long press
		startLongPressCheck();

		// obtain a velocity tracker and feed it its first event
		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);

		// we don't know if it's a click or a scroll yet, but until we know
		// assume it's a click
		setTouchState(TOUCH_STATE_CLICK);

		//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해
//        setHideSlicesText(true);
//        m_bIsMoving = true;
	}

	/**
	 * Resets and recycles all things that need to when we end a touch gesture
	 */
	private void endTouch(final float x, final float y, final float velocity) {

		// recycle the velocity tracker
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		// remove any existing check for long-press
		removeCallbacks(mLongPressRunnable);

		// create the dynamics runnable if we haven't before
		if (mDynamicsRunnable == null) {

			mDynamicsRunnable = new Runnable() {

				public void run() {

					// if we don't have any dynamics set we do nothing
					if (mDynamics == null) {
						return;
					}

					// we pretend that each frame of the fling/snap animation is
					// one touch gesture and therefore set the start position
					// every time
					mDynamics.update(AnimationUtils.currentAnimationTimeMillis());

					// Keep the rotation amount between 0 - 360
					rotateChart(mDynamics.getPosition() % 360);
					invalidate();
//					System.out.println("Debug_endTouch_threading");

					if (!mDynamics.isAtRest(VELOCITY_TOLERANCE)) {

						// the list is not at rest, so schedule a new frame
						postDelayed(this, 8);
//                        setHideSlicesText(true);
						m_bIsMoving = true;	//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해

					} else {

						snapTo();
						invalidate();

//                    	setHideSlicesText(false);
//                    	m_bIsMoving = false;	//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해 

					}

				}
			};
		}

		if (mDynamics != null && Math.abs(velocity) > ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) {
			// update the dynamics with the correct position and start the runnable
			mDynamics.setState((float) getRotationDegree(), velocity, AnimationUtils.currentAnimationTimeMillis());
			post(mDynamicsRunnable);

		} else if (mTouchState != TOUCH_STATE_CLICK) {

			snapTo();
		}

		// reset touch state
		setTouchState(TOUCH_STATE_RESTING);

		//2015. 8. 5 파이차트 수정사항 : 이동중 글씨 표시 여부를 정하기 위해
//        setHideSlicesText(false);
		m_bIsMoving = false;
	}

	//2015. 8. 5 파이차트 수정사항 : 부채꼴 이동 후 % 정보 글자 위치 재조정
	private void resetSlicePercentDrawablesLocation(float fStartAngle)
	{
		float fAngle = fStartAngle;
		for (PieSliceDrawable slice : mDrawables) {
//        	slice.setDegreeOffset(fAngle);
			slice.setStartAngle(fAngle);
			slice.setSlicePercentDrawable(this, getContext());
			slice.getSlicePercentDrawable().setTitle(String.format("%.0f", slice.getPercent()*100)+"%");
			fAngle += slice.getDegrees();
		}
	}

	/**
	 * Checks if the user has moved far enough for this to be a scroll and if
	 * so, sets the list in scroll mode
	 *
	 * @param event The (move) event
	 * @return true if scroll was started, false otherwise
	 */
	private boolean startScrollIfNeeded(final MotionEvent event) {

		final int xPos = (int) event.getX();
		final int yPos = (int) event.getY();

		if (isEnabled()
				&& (xPos < mTouchStartX - mScrollThreshold
				|| xPos > mTouchStartX + mScrollThreshold
				|| yPos < mTouchStartY - mScrollThreshold
				|| yPos > mTouchStartY + mScrollThreshold)) {

			// we've moved far enough for this to be a scroll
			removeCallbacks(mLongPressRunnable);

			setTouchState(TOUCH_STATE_ROTATE);

			mRotationStart = (float) Math.toDegrees(Math.atan2(mCenter.y - yPos, mCenter.x - xPos));
			return true;
		}

		return false;
	}

	/**
	 * Posts (and creates if necessary) a runnable that will when executed call
	 * the long click listener
	 */
	private void startLongPressCheck() {

		if (!isEnabled()) return;

		// create the runnable if we haven't already
		if (mLongPressRunnable == null) {

			mLongPressRunnable = new Runnable() {

				public void run() {

					if (mTouchState == TOUCH_STATE_CLICK) {

						final int index = getContainingChildIndex(mTouchStartX, mTouchStartY);

						if (index != INVALID_INDEX) longClickChild(index);
					}
				}
			};
		}

		// then post it with a delay
		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	/**
	 * Calls the item click listener for the child with at the specified
	 * coordinates
	 *
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	private void clickChildAt(final int x, final int y) {

		if (!isEnabled()) return;

		final int index = getContainingChildIndex(x, y);

		if (index == INFO_INDEX) {

			if (mOnInfoClickListener != null) {
//                playSoundEffect(SoundEffectConstants.CLICK);
				mOnInfoClickListener.onInfoClicked(mChartHidden ?  -1 : getCurrentIndex());
			}

		} else if (index != INVALID_INDEX) {

			try {
//        		if (getCurrentIndex() != index) {
				final PieSliceDrawable sliceView = mDrawables.get(index);
				if (m_nColorType != 3 && m_nColorType != 1)
					showPieViewPanel(sliceView);
//				float degree = sliceView.getSliceCenter();
//				animateTo(mRotationDegree, mSnapToDegree-degree);

				//선택된 slice 정보를 넘긴다
//				final PieSliceDrawable finalSlice = sliceView;
//				final int selIndex = index;
//				COMUtil._chartMain.runOnUiThread(new Runnable() {
//					public void run() {
//						if(parent!=null) {
////	    	    				parent.textView1.setText(finalSlice.getName());
////	    	    				parent.textView1.setTextColor(finalSlice.getSliceColor());
////	    	    				parent.textView2.setText(finalSlice.getPrice()+"%");
////	    	    				parent.textView2.setTextColor(finalSlice.getSliceColor());
//							parent.setSliceInfo(selIndex);
////	    	    				parent.updateCache(); //
//						}
//					}
//				});
////        		}
			} catch(Exception e) {

			}

//            final PieSliceDrawable sliceView = mDrawables.get(index);
//            final long id = mAdapter.getItemId(index);
//            boolean secondTap = false;
//            
//            if (getCurrentIndex() != index) {
//            	animateTo(sliceView, index);
//            } else {
//            	secondTap = true;
//            }
//
////            playSoundEffect(SoundEffectConstants.CLICK);
//            performItemClick(secondTap, sliceView, index, id);
		}
	}

	public void setSliceSelect(String strParam) {
		try {
			for (int index = 0; index < mDrawables.size(); index++) {
				final PieSliceDrawable slice = mDrawables.get(index);
				if(slice.getName().trim().equals(strParam.trim())) {
					float degree = slice.getSliceCenter();
					animateTo(mRotationDegree, mSnapToDegree-degree);
					break;
				}
			}
		} catch (Exception e) {

		}
	}
	View pieViewPanel = null;
	TextView tv_sub_title = null;
	TextView tv_sub_value = null;

	RelativeLayout m_layout = null;
	public void setLayout(RelativeLayout layout)
	{
		m_layout = layout;
	}
	public void showPieViewPanel(PieSliceDrawable slice) {
		if(slice.getName().equals("데이터 입력 안됨"))
			return;
		if(pieViewPanel == null) {
			LayoutInflater factory = LayoutInflater.from(this.getContext());
			int layoutResId = this.getContext().getResources().getIdentifier("pieviewpanel", "layout", this.getContext().getPackageName());
			pieViewPanel = factory.inflate(layoutResId, null);

			layoutResId = this.getContext().getResources().getIdentifier("tv_sub_title", "id", this.getContext().getPackageName());
			tv_sub_title = (TextView) pieViewPanel.findViewById(layoutResId);
			tv_sub_title.setTypeface(COMUtil.typeface);

			layoutResId = this.getContext().getResources().getIdentifier("tv_sub_value", "id", this.getContext().getPackageName());
			tv_sub_value = (TextView) pieViewPanel.findViewById(layoutResId);
			tv_sub_value.setTypeface(COMUtil.numericTypefaceMid);

			parent.pieChartLayout.addView(pieViewPanel);
//			COMUtil.apiView.addView(pieViewPanel);
			COMUtil.setGlobalFont(parent.pieChartLayout);
		} //else {

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tv_sub_title.setText(slice.getName().replace("비중", ""));
		String strValue = String.format("%.2f%%", slice.getPercent()*100);
		if(m_strToolTipType.equals("V"))
			strValue = slice.getPrice()+"%";
		tv_sub_value.setText(strValue);
		try {
			PointF pointf = slice.getAnagleLinePath(m_nColorType);
			String strTitle = tv_sub_title.getText().toString();

			float strWidth = getFontWidth_Font(tv_sub_title.getText().toString(), (int)COMUtil.getPixel(12), COMUtil.typeface)+getFontWidth_Font(tv_sub_value.getText().toString(), (int)COMUtil.getPixel(13), COMUtil.numericTypefaceMid)+COMUtil.getPixel(26);
			params.leftMargin = (int)(pointf.x - strWidth/2);
			params.topMargin = (int)(pointf.y - COMUtil.getPixel(30));
			int layoutResId = this.getContext().getResources().getIdentifier("back_layout", "id", this.getContext().getPackageName());
			LinearLayout backLayout = (LinearLayout) pieViewPanel.findViewById(layoutResId);
			if(params.topMargin<0)
			{
				params.topMargin = (int)pointf.y;
//				backLayout.setBackgroundResource(this.getContext().getResources().getIdentifier("img_pie_tooltip_down", "drawable", this.getContext().getPackageName()));
				backLayout.setPadding((int)COMUtil.getPixel_W(10), (int)COMUtil.getPixel_H(4), (int)COMUtil.getPixel_W(10), (int)COMUtil.getPixel_H(0));
			}
			else
			{
				backLayout.setPadding((int)COMUtil.getPixel_W(10), (int)COMUtil.getPixel_H(0), (int)COMUtil.getPixel_W(10), (int)COMUtil.getPixel_H(4));
//				backLayout.setBackgroundResource(this.getContext().getResources().getIdentifier("img_pie_tooltip", "drawable", this.getContext().getPackageName()));
			}

		} catch (Exception e) {

		}

		pieViewPanel.setLayoutParams(params);
		//}

	}
	public float getFontWidth(String strText, int size) {
		Paint measurePaint_Text = new Paint();
		measurePaint_Text.setTextSize(size);
		float width = measurePaint_Text.measureText(strText);
		return width;
	}
	public float getFontWidth_Font(String strText, int size, Typeface typeface) {
		Paint measurePaint_Text = new Paint();
		measurePaint_Text.setTextSize(size);
		measurePaint_Text.setTypeface(typeface);
		float width = measurePaint_Text.measureText(strText);
		return width;
	}

	public double dgreesToRadians(double degrees) {
		double pi = Math.PI;
		return degrees*(pi/180);
	}


	/**
	 * Call the OnItemClickListener, if it is defined.
	 *
	 * @param view The drawable within the View that was clicked.
	 * @param position The position of the view in the adapter.
	 * @param id The row id of the item that was clicked.
	 * @return True if there was an assigned OnItemClickListener that was
	 *         called, false otherwise is returned.
	 */
	public boolean performItemClick(boolean secondTap, PieSliceDrawable view, int position, long id) {

		if (mOnItemClickListener != null) {

			mOnItemClickListener.onItemClick(secondTap, this, view, position, id);

			return true;
		}

		return false;
	}

	/**
	 * Calls the item long click listener for the child with the specified index
	 *
	 * @param index Child index
	 */
	private void longClickChild(final int index) {
		try {
			final PieSliceDrawable slice = mDrawables.get(index);
			final long id = mAdapter.getItemId(index);
			final OnItemLongClickListener listener = getOnItemLongClickListener();

			if (listener != null) {
				listener.onItemLongClick(null, slice, index, id);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Returns the index of the child that contains the coordinates given.
	 *
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @return The index of the child that contains the coordinates. If no child
	 *         is found then it returns INVALID_INDEX
	 */
	private int getContainingChildIndex(final int x, final int y) {

		// Check if we touched the info panel
//		if (inInfoCircle(x, y)) return INFO_INDEX;

		// Check if we did not touch within the bounds of the Pie Chart
//		if (!inCircle(x, y)) return INVALID_INDEX;

		// Get the drawing cache to aid in calculating which slice was touched
		final Bitmap viewBitmap = getDrawingCache();

		if (viewBitmap == null) return INVALID_INDEX;

		// Grab the color pixel at the point touched and compare it with the children
		try {
			int pixel = viewBitmap.getPixel(x, y);

			for (int index = 0; index < mDrawables.size(); index++) {

				final PieSliceDrawable slice = mDrawables.get(index);

				boolean isSlice = slice.pieElemInPoint(new PointF(x, y));
				if (slice.getSliceColor() == pixel || isSlice) {
					return index;
				}
			}
		} catch (Exception e) {

		}

		return INVALID_INDEX;
	}

	/**
	 * Does the touch lie within the bounds of the current Pie Chart.
	 *
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 *
	 * @return True if the touch was inside the Pie Chart's circular bounds
	 */
	private boolean inCircle(final int x, final int y) {

		if ((mChartHidden || !isLoaded()) && !inInfoCircle(x, y)) return false;

		double dx = (x - (mCenter.x+getCenterCircleBounds().left)) * (x - (mCenter.x+getCenterCircleBounds().left));
		double dy = (y - (mCenter.y+getCenterCircleBounds().top)) * (y - (mCenter.y+getCenterCircleBounds().top));

		if ((dx + dy) < ((mChartDiameter / 2) * (mChartDiameter / 2))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Does the touch lie within the bounds of the info panel, if
	 * the info panel is currently showing.
	 *
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 *
	 * @return True if the touch was inside the info panel
	 */
	private boolean inInfoCircle(final int x, final int y) {

		if (!mShowInfo) return false;

		double dx = (x - mCenter.x) * (x - mCenter.x);
		double dy = (y - mCenter.y) * (y - mCenter.y);

		if ((dx + dy) < (mInfoRadius * mInfoRadius)) {
			return true;
		} else {
			return false;
		}
	}

//	 protected void onWindowVisibilityChanged(int visibility) {
//		 this.onResume();
//	 }
//	 
//	 protected void onDetachedFromWindow() {
//		 this.onDetachedFromWindow();
//	 }
//	 
//	 protected void dispatchDraw(android.graphics.Canvas canvas) {
//		 this.dispatchDraw(canvas);
//	 }

//	@Override
//	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		
//		int width = MeasureSpec.getSize(widthMeasureSpec);
//		int height = MeasureSpec.getSize(heightMeasureSpec);
//		
////        boolean useHeight = height < width;
//        
////        mChartDiameter = (useHeight ? (height - (getPaddingTop() + getPaddingBottom()))
////        		: (width - (getPaddingLeft() + getPaddingRight()))) - (int) mStrokeWidth;
//        
////        mChartDiameter = (width - (getPaddingLeft() + getPaddingRight())) - (int) mStrokeWidth;
////		mChartDiameter = 360;
//		mChartDiameter = width;
//		
//		mInfoRadius = getChartRadius() / 2;
////        mInfoRadius = height;
//        
//		
////        int size = useHeight ? height : width;
//        
////		setMeasuredDimension(size, size);
//		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//	}

//	@Override
//	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//		
//		System.out.println("Debug_chartView_onLayout:right:"+right+" bottom:"+bottom);
//        
//		// Get the center coordinates of the view
//		mCenter.x = (float) Math.abs(left - right) / 2;
//		mCenter.y = (float) Math.abs(top - bottom) / 2;
////		mCenter.y = top + getChartRadius();//2015. 8. 5 파이차트 수정사항 : 파이차트 중심점 이동
////		mCenter.y = top + UiUtils.getDynamicPixels(getContext(), 100);	//2015. 8. 5 파이차트 수정사항 : 파이차트 중심점 이동
//		
////		if (background != null)
////	         background.setBounds(left, top, right, bottom);
//	}

	/**
	 * Starts at 0 degrees and adds each pie slice as provided
	 * by the adapter
	 */
	private void addPieSlices() {

		synchronized (mDrawables) {

			float offset = -90;
			for (int index = 0; index < mAdapter.getCount(); index++) {

				// Check for any recycled PieSliceDrawables
				PieSliceDrawable recycled = getRecycledSlice();

				// Get the slice from the adapter
				final PieSliceDrawable childSlice = mAdapter.getSlice(this, recycled, index, offset);

				childSlice.setBounds(getBounds());
				childSlice.setStartAngle(offset);
				childSlice.setArrowPositionType(m_strArrowPositionType);	//2015. 8. 5 파이차트 수정사항
				childSlice.setColorType(m_nColorType);
				mDrawables.add(childSlice);

				offset += childSlice.getDegrees();
			}
			setLoaded(true);
		}
	}

	/**
	 * Gets the rectangular bounds of that the current Pie Chart
	 * should be drawn within.
	 *
	 * @return The bounds of the Pie Chart
	 */
	private Rect getBounds() {

		int left = (int) (mCenter.x - getChartRadius() + chart_bounds.right/2 - getChartRadius());
		int top = (int) (mCenter.y - getChartRadius() + chart_bounds.bottom/2 - getChartRadius());

		return new Rect(left, top, left + mChartDiameter, top + mChartDiameter);
	}


	private Rect getArrowBounds() {

//        int left = (int) (mCenter.x -getChartRadius()/3);
//        int top = (int) (mCenter.y + UiUtils.getDynamicPixels(getContext(), 55));
		//2015. 8. 5 파이차트 수정사항>>
		int left = 0, top = 0;

		if(m_strArrowPositionType.equals("B"))
		{
			left = (int) (mCenter.x -getChartRadius()/3);
			top = (int) (mCenter.y + UiUtils.getDynamicPixels(getContext(), 55));
		}
		else if(m_strArrowPositionType.equals("R"))
		{
			left = (int) (mCenter.x + getChartRadius() -(int)UiUtils.getDynamicPixels(getContext(), 100));
			top = (int) (mCenter.y - (int)UiUtils.getDynamicPixels(getContext(), 50));
		}
		else if(m_strArrowPositionType.equals("RT"))
		{
			left = (int) (mCenter.x + getChartRadius() -(int)UiUtils.getDynamicPixels(getContext(), 100));
			top = (int) (mCenter.y - (int)UiUtils.getDynamicPixels(getContext(), 50));
		}
		//2015. 8. 5 파이차트 수정사항<<

		return new Rect(left, top, left + (int)UiUtils.getDynamicPixels(getContext(), 100), top + (int)UiUtils.getDynamicPixels(getContext(), 100));
	}

	private Rect getCenterCircleBounds() {

		//2015. 8. 5 파이차트 수정사항 : mInfoDrawable (현재 부채꼴 정보) 가운데로 옮기기
		int left = (int) (mCenter.x - getChartRadius() + chart_bounds.right/2 - getChartRadius());
		int top = (int) (mCenter.y - getChartRadius() + chart_bounds.bottom/2 - getChartRadius());
//		int left = (int) (mCenter.x - UiUtils.getDynamicPixels(getContext(), 40));
//		int top = (int) (mCenter.y - UiUtils.getDynamicPixels(getContext(), 40));

		return new Rect(left, top, left + (int)mInfoRadius, top + (int)mInfoRadius);
	}

	private Rect getBottomBounds() {

		int left = (int) (mCenter.x - mInfoRadius/2);
//        int top = (int) (mCenter.y + mInfoRadius);
		int top = (int)(chart_bounds.top + chart_bounds.width()+COMUtil.getPixel(10) );	//2015. 8. 5 파이차트 수정사항 : 사각 정보창 타이틀 시작y위치 수정

		return new Rect(left, top, left + (int)mInfoRadius, chart_bounds.bottom);
	}

	private Rect getRightBounds() {

		int left = (int) ((chart_bounds.left + chart_bounds.height()) + UiUtils.getDynamicPixels(getContext(), 20));
//        int top = (int) (mCenter.y + mInfoRadius);
		int top = (int)chart_bounds.top;	//2015. 8. 5 파이차트 수정사항 : 사각 정보창 타이틀 시작y위치 수정

		return new Rect(left, top, left + (int)mInfoRadius, chart_bounds.bottom);
	}

	/**
	 * Returns a recycled {@link PieSliceDrawable} if one is available.
	 *
	 * @return A PieSliceDrawable if one exists, null otherwise
	 */
	private PieSliceDrawable getRecycledSlice() {

		if (mRecycledDrawables.size() != 0) {
			return mRecycledDrawables.removeFirst();
		}

		return null;
	}

	/**
	 * TODO: Rotate depending on our current snap-to position
	 *
	 * Creates the caret pointer drawable
	 */
//	private void createCaret() {
//		
//		if (mCaret == null) {
//			PointF position = new PointF(mCenter.x - mInfoRadius / 2, mCenter.y+mInfoRadius+10);
//	        mCaret = new CaretDrawable(getContext(), position, mInfoRadius, mInfoRadius);
//	        mCaret.setColor(Color.WHITE);
//	        mCaret.setAlpha(50);
//	        
//	        //canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+mInfoRadius+10, mInfoRadius*2, mCenter.y+mInfoRadius+70, mStrokePaint);
//		}
////		mCaret.setCaretRotation(mSnapToDegree);
//	}

	//2015. 8. 5 파이차트 수정사항>>
	private void createCenterCircleInfo() {

		if (mCenterCircleInfoDrawable == null) {
			mCenterCircleInfoDrawable = new CenterCircleInfoDrawable(this, getContext(), chart_bounds, mInfoRadius);
		}
	}

	private void createArrow() {

		if (mArrowDrawable == null) {
			mArrowDrawable = new ArrowDrawable(this, getContext(), getArrowBounds());
			mArrowDrawable.setArrowPositionType(m_strArrowPositionType);	//2015. 8. 5 파이차트 수정사항
		}
	}
	//2015. 8. 5 파이차트 수정사항<<

	private void createInfo() {
		if(mInfoDrawable == null)
		{
			if(chart_bounds.width()>chart_bounds.height()) {
				mInfoDrawable = new InfoDrawable(this, getContext(), getRightBounds(), mInfoRadius);
				mInfoDrawable.setType(1);
			}
			else {
				mInfoDrawable = new InfoDrawable(this, getContext(), getBottomBounds(), mInfoRadius);
			}
			mInfoDrawable.setPieInfo(mDrawables);
		}
	}


	/**
	 * Hide the current Pie Chart if showing.
	 */
	public void hideChart() {

		if (!mChartHidden) {
			toggleChart();
		}
	}

	/**
	 * Show the current Pie Chart if hidden.
	 */
	public void showChart() {

		if (mChartHidden) {
			toggleChart();
		}
	}

	/**
	 * Toggle the Pie Chart to show or hide depending on its current
	 * state.
	 *
	 * @return True if the chart was hidden, false otherwise.
	 */
	public int toggleChart() {

		final float start = mChartScale;
		final float end = start == 1f ? 0f : 1f;

		if (mChartHidden && !isLoaded()) {

			mNeedsToggle = true;

			return CHART_INVALID;
		}

		mNeedsToggle = false;
		mChartHidden = (end == 0);

		ThreadAnimator scale = ThreadAnimator.ofFloat(start, end);
		scale.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnded() {
				mDrawingCache = null;

				if (mNeedsUpdate) {
					mNeedsUpdate = false;
					resetChart();
				}
			}
		});

		scale.setDuration(400);

		if (end == 1) {
			scale.setInterpolator(new OvershootInterpolator());
		}

//    	getDrawThread().setScaleAnimator(scale);

		onChartChanged(mChartHidden);

		return (end == 0) ? CHART_HIDDEN : CHART_SHOWING;
	}

	private void onChartChanged(final boolean didCollapse) {

		if (mOnPieChartExpandListener != null) {

			if (didCollapse) {
				mOnPieChartExpandListener.onPieChartCollapsed();
			} else {
				mOnPieChartExpandListener.onPieChartExpanded();
			}
		}
	}

	/**
	 * Snaps the chart rotation to a given snap degree
	 */
	private void snapTo() {
		snapTo(true);
	}

	/**
	 * Snaps the chart rotation to a given snap degree
	 */
	private void snapTo(boolean animated) {

		for (int index = 0; index < mDrawables.size(); index++) {

			final PieSliceDrawable slice = mDrawables.get(index);

			if (slice.containsDegree(mRotationDegree, mSnapToDegree)) {

//            	rotateChart(slice, index, animated);

				try {
					float degree = slice.getSliceCenter();
					animateTo(mRotationDegree, mSnapToDegree-degree);
					//선택된 slice 정보를 넘긴다
					final PieSliceDrawable finalSlice = slice;
					final int selIndex = index;
					COMUtil._chartMain.runOnUiThread(new Runnable() {
						public void run() {
							if(parent!=null) {
//	    	    				parent.textView1.setText(finalSlice.getName());
//	    	    				parent.textView1.setTextColor(finalSlice.getSliceColor());
//	    	    				parent.textView2.setText(finalSlice.getPrice()+"%");
//	    	    				parent.textView2.setTextColor(finalSlice.getSliceColor());
								parent.setSliceInfo(selIndex);
//	    	    				parent.updateCache(); //
							}
						}
					});
				} catch(Exception e) {

				}

				break;
			}
		}


	}

	/**
	 * Animates the pie chart's rotation to a specific degree
	 *
	 * @param index the index of the PieSliceView
	 */
	private void animateTo(int index) {
		rotateChart(null, index, true);
	}

	private void animateTo(PieSliceDrawable slice, int index) {
		rotateChart(slice, index, true);
	}

	/**
	 * Animates the pie chart's rotation to a specific degree
	 *
	 * @param slice the PieSliceView to rotate to
	 * @param index the index of the PieSliceView
	 */
	private void animateTo(float start, float end) {

//    	// Animate the rotation and update the current index
//    	ThreadAnimator rotate = ThreadAnimator.ofFloat(start, end);
//    	rotate.setDuration(300);
//    	rotate.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationEnded() {
//
//				if (mOnRotationStateChangeListener != null) {
//		        	mOnRotationStateChangeListener.onRotationStateChange(TOUCH_STATE_RESTING);
//		        }
//
//				mDrawingCache = null;
//
//				//초기화면 애니메이션 설정
//				if(m_init) {
//					if(mDrawables!=null && mDrawables.size()>0) {
//						final PieSliceDrawable sliceView = mDrawables.get(0);
//						if(sliceView!=null) animateTo(sliceView, 0);
//					}
//				}
//				m_init = false;
//			}
//		});
//
//    	if (mOnRotationStateChangeListener != null) {
//        	mOnRotationStateChangeListener.onRotationStateChange(TOUCH_STATE_ROTATE);
//        }
//
//    	this.setRotateAnimator(rotate);
//    	this.invalidate();
	}

	private void rotateChart(PieSliceDrawable slice, int index, boolean animated) {

		synchronized (mDrawables) {

			if (mDrawables.size() == 0
					|| mDrawables.size() <= index
					|| !isEnabled()) return;

			if (slice == null) {
				slice = mDrawables.get(index);
			}

			float degree = slice.getSliceCenter();

			// Adjust for our snap degree
			degree = mSnapToDegree - degree;

			// Normalize to a valid 360 degree range
			if (degree < 0) degree += 360;

			float start = getRotationDegree();

			// Make sure we rotate the correct direction to take the
			// shortest distance to the target degree

//			System.out.println("Debug_start:"+start+" degree:"+degree);
			float rawDiff = Math.abs(start - degree);
			float modDiff = rawDiff % 360f;

			if (modDiff > 180.0) {
				start = start > degree ? (360 - start) * -1 : (360 + start);
			}

			if (animated) {
				animateTo(start, degree);

			} else {
				setRotationDegree(degree);

				mDrawingCache = null;
			}

//			System.out.println("########## Debug_slice.getSliceCenter():"+slice.getSliceCenter()+" getRotationDegree():"+getRotationDegree()+" rawDiff:"+rawDiff+" diff:"+(degree-start)+" degree:"+degree);

			//setCurrentIndex(index);

			//2015. 8. 5 파이차트 수정사항 : 회전 끝난 후 각 부채꼴의 % 정보 보이기
			setSliceSelectByIndex(index);

//			resetSlicePercentDrawablesLocation(degree);

			if(mCenterCircleInfoDrawable!=null) {
//            	mInfoDrawable.setTitle("percent");
				mCenterCircleInfoDrawable.setTitle(m_strTitle);
//            	mInfoDrawable.setAmount(""+slice.getPercent()+"%");
//				mCenterCircleInfoDrawable.setAmount(String.format("%.0f", slice.getPercent()*100) + "%");
			}

			if(mInfoDrawable!=null) {
//            	mInfoDrawable.setTitle("percent");
				mInfoDrawable.setFontColor(slice.getSliceColor());
				mInfoDrawable.setTitle(slice.getName());
				mInfoDrawable.setPercentTitle(String.format("%.0f", slice.getPercent()*100) + "%");
				mInfoDrawable.setPriceTitle(slice.getPrice());
			}

			//선택된 slice 정보를 넘긴다
			final PieSliceDrawable finalSlice = slice;
			final int selIndex = index;
			COMUtil._chartMain.runOnUiThread(new Runnable() {
				public void run() {
					if(parent!=null) {
//	    				parent.textView1.setText(finalSlice.getName());
//	    				parent.textView1.setTextColor(finalSlice.getSliceColor());
//	    				parent.textView2.setText(finalSlice.getPrice()+"%");
//	    				parent.textView2.setTextColor(finalSlice.getSliceColor());
						parent.setSliceInfo(selIndex);
//	    				parent.updateCache(); //
					}
				}
			});

		}

		//2015. 8. 5 파이차트 수정사항>> : 중앙정렬시킬때 텍스트 보이지 않게
		if(m_bIsMoving)
		{
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e){}
		}
		m_bIsMoving = false;
		//2015. 8. 5 파이차트 수정사항<<
	}

	private void setSliceSelectByIndex(int nIdx)
	{
		for(int i = 0; i < mDrawables.size(); i++)
		{
			if(nIdx == i)
			{
				mDrawables.get(i).setSelectedSlice(true);
			}
			else
			{
				mDrawables.get(i).setSelectedSlice(false);
			}

		}
	}

	/**
	 * Rotate the chart based on a given (x,y) coordinate
	 *
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	private void rotateChart(final float x, final float y) {

		float degree = (float) (Math.toDegrees(Math.atan2(mCenter.y - y, mCenter.x - x)) - mRotationStart);

		// Rotate from the last rotation position to prevent rotation jumps

//    	System.out.println("Debug_rotateChart_degree:"+degree+" mLastRotation:"+mLastRotation);
		rotateChart(mLastRotation + degree);
	}

	/**
	 * Rotates the chart rotation degree. Takes care of rotation (if enabled) and
	 * snapping
	 *
	 * @param degree The degree to rotate to
	 */
	private void rotateChart(float degree) {

		final float previous = getRotationDegree();

		setRotationDegree(degree);

		setRotatingClockwise(previous);

//		this.invalidate();
//		System.out.println("Debug_rotateChart!!!");
	}

	/**
	 * Checks which way the chart is rotating.
	 *
	 * @param previous The previous degree the chart was rotated to
	 */
	private void setRotatingClockwise(float previous) {

		final float change = (mRotationDegree - previous);
		mRotatingClockwise = (change > 0 && Math.abs(change) < 300) || (Math.abs(change) > 300 && mRotatingClockwise);
	}
	/**
	 * Returns the current PieChartAdapter
	 *
	 * @return The PieChartAdapter
	 */
	public BasePieChartAdapter getPieChartAdapter() {
		return mAdapter;
	}

	//2015. 8. 5 파이차트 수정사항 : 부채꼴 %정보 표시 여부
//	private void setHideSlicesText(boolean bFlag)
//	{
//		for(int i = 0; i < mDrawables.size(); i++)
//		{
//			mDrawables.get(i).setHideText(bFlag);
//		}
//	}

	/**
	 * Sets the adapter that will provide the data for this
	 * Pie Chart.
	 *
	 * @param adapter The PieChart adapter
	 */
	public void setAdapter(BasePieChartAdapter adapter) {

		// Unregister the old data change observer
		if (mAdapter != null && mDataSetObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}

		// Perform validation check
		float total = validAdapter(adapter);
		if ((1f - total) > 0.0001f) {
			return;
		}

		m_init = true;

		resetChart();

		mAdapter = adapter;

		// Register the data change observer
		if (mAdapter != null) {
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);
		}

		if (mDrawables.size() == 0 && mAdapter != null) {

			addPieSlices();
			createCenterCircleInfo();
//			createInfo();
			if(mCenterCircleInfoDrawable!=null) {
				mCenterCircleInfoDrawable.setTitle(m_strTitle);
			}
			buildDrawingCache1();
//			snapTo();
//			if(mDrawables!=null && mDrawables.size()>0) {
//				final PieSliceDrawable sliceView = mDrawables.get(0);
//				if(sliceView!=null) animateTo(sliceView, 0);
//			}
			this.invalidate();
		}
	}

	/**
	 * Get the sum of all the percents from the adapter
	 * to help with validation.  We need an approximate
	 * total of 1.0f so that the chart can be rendered
	 * properly.
	 *
	 * @param adapter The adapter supplying the chart's data
	 * @return The sum of all percentages provided by the adapter
	 */
	private float validAdapter(BasePieChartAdapter adapter) {

		float total = 0;

		for (int i = 0; i < adapter.getCount(); i++) {
			total += adapter.getPercent(i);
		}

		return total;
	}

	/**
	 * Resets the chart and recycles all PieSliceDrawables
	 */
	private void resetChart() {

		synchronized(mDrawables) {

			setLoaded(false);

			mRecycledDrawables.addAll(mDrawables);
			mDrawables.clear();
		}
	}

	public void setColorType(int nColorType)
	{
		m_nColorType = nColorType;
	}

	public int getColorType()
	{
		return m_nColorType;
	}

	public void setToolTipType(String strToolTipType)
	{
		m_strToolTipType = strToolTipType;
	}

//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		
//		if (mDrawThread.getState() == Thread.State.TERMINATED) {
//			
//			mDrawThread = new DrawThread(getHolder(), mHandler, this);
//			mDrawThread.setRunning(true);
//			mDrawThread.start();
//			
//        } else {
//        	
//        	mDrawThread.setRunning(true);
//        	mDrawThread.start();
//        }
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		
//		boolean retry = true;
//
//		mDrawThread.onResume();
//		mDrawThread.setRunning(false);
//		
//		while (retry) {
//			try {
//				mDrawThread.join();
//				retry = false;
//			} catch (InterruptedException e) {}
//		}
//	}

//	Drawable background;

//	   @Override
//	   public void setBackgroundDrawable(Drawable background) {
//	      this.background = background;
//	   }
//
//	   @Override
//	   public void setBackground(Drawable background) {
//	      this.background = background;
//	   }

	/** Animator objects used to animate the rotation, scale, and info panel */
	private ThreadAnimator mRotateAnimator, mScaleAnimator, mInfoAnimator;
	public void setRotateAnimator(ThreadAnimator mRotateAnimator) {
		this.mRotateAnimator = mRotateAnimator;
		mRotateAnimator.start();
	}

	public void setScaleAnimator(ThreadAnimator mScaleAnimator) {
		this.mScaleAnimator = mScaleAnimator;
		mScaleAnimator.start();
	}

	public void setInfoAnimator(ThreadAnimator mInfoAnimator) {
		this.mInfoAnimator = mInfoAnimator;
		mInfoAnimator.start();
	}

	/**
	 * Thread used to draw the Pie Chart
	 *
	 * @author Saul Howard
	 */
//	public class DrawThread extends Thread {
//
//		/** The SurfaceHolder to draw to */
////		private SurfaceHolder mSurfaceHolder;
//		private PieChartView mPieChartView;
//
//		/** Tracks the running state of the thread */
//		private boolean mIsRunning;
//
//		/** Object used to acquire a pause lock on the thread */
//		private Object mPauseLock = new Object();
//
//		/** Tracks the pause state of the drawing thread */
//		private boolean mPaused;
//
//		/** Animator objects used to animate the rotation, scale, and info panel */
//		private ThreadAnimator mRotateAnimator, mScaleAnimator, mInfoAnimator;
//
//		private Handler mHandler;
//
//		PieChartView m_Chart;
//
//		/**
//		 * Creates a new DrawThread that will manage drawing the PieChartView onto
//		 * the SurfaceView
//		 *
//		 * @param surfaceHolder the surfaceHolder to draw to
//		 */
//		public DrawThread(PieChartView chartView, Handler handler, PieChartView chart) {
//			this.mPieChartView = chartView;
//			this.mHandler = handler;
//			mIsRunning = false;
//			mPaused = true;
//
//			m_Chart = chart;
//		}
//
//		public void setRunning(boolean run) {
//			mIsRunning = run;
//		}
//
//		public boolean isRunning() {
//			return mIsRunning;
//		}
//
//		public boolean isPaused() {
//			return mPaused;
//		}
//
//		public void setRotateAnimator(ThreadAnimator mRotateAnimator) {
//			this.mRotateAnimator = mRotateAnimator;
//			mRotateAnimator.start();
//		}
//
//		public void setScaleAnimator(ThreadAnimator mScaleAnimator) {
//			this.mScaleAnimator = mScaleAnimator;
//			mScaleAnimator.start();
//		}
//
//		public void setInfoAnimator(ThreadAnimator mInfoAnimator) {
//			this.mInfoAnimator = mInfoAnimator;
//			mInfoAnimator.start();
//		}
//
////		/**
////		 * Pause the drawing to the SurfaceView
////		 */
////		public void onPause() {
////
////			if (mPaused) return;
////
////		    synchronized (mPauseLock) {
////		    	cleanUp();
////		        mPaused = true;
////		    }
////		}
////
////		/**
////		 * Resume drawing to the SurfaceView
////		 */
////		public void onResume() {
////
////			if (!mPaused) return;
////			cleanUp();
////		    synchronized (mPauseLock) {
////		        mPaused = false;
////		        mPauseLock.notifyAll();
////		    }
////		}
//
////		@Override
////		public void run() {
////			//2015. 8. 5 파이차트 수정사항>>
////			createCenterCircleInfo();
//			createArrow();
////			//2015. 8. 5 파이차트 수정사항<<
////			createInfo();
////
////			// Notify any listener the thread is ready and running
////			mHandler.post(new Runnable() {
////
////				@Override
////				public void run() {
////
////					if (mOnPieChartReadyListener != null) {
////						mOnPieChartReadyListener.onPieChartReady();
////					}
////				}
////			});
////
////			Canvas canvas;
////
////			while (mIsRunning) {
////
////				// Check for a pause lock
////				synchronized (mPauseLock) {
////				    while (mPaused) {
////				        try {
////				            mPauseLock.wait();
////				        } catch (InterruptedException e) {
////				        	Log.e(TAG, "Interrupted", e);
////				        }
////				    }
////				}
////
////				// If there are no PieSliceDrawables and we have an
////				// adapter create the necessary slices, the drawing
////				// cache and snap to the closest position
////				if (mDrawables.size() == 0 && mAdapter != null) {
////
////					addPieSlices();
////					buildDrawingCache();
////					snapTo();
////					if(mDrawables!=null && mDrawables.size()>0) {
////						final PieSliceDrawable sliceView = mDrawables.get(0);
////						if(sliceView!=null) animateTo(sliceView, 0);
////					}
////				}
////
////				// If the drawing cache is null build it
////				if (mDrawingCache == null) {
////					buildDrawingCache();
////				}
////
////				canvas = null;
////
////				//double buffering
////				try {
////
//////					canvas = mSurfaceHolder.lockCanvas(null);
////					canvas = m_Canvas;
////
////					synchronized (mPieChartView) {
////
////						if (canvas != null && !mPaused) {
////
//////							if (background != null)
//////							      background.draw(canvas);
////
////							// Clear the canvas
//////							canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
////							canvas.drawColor(Color.WHITE);
//////							canvas.drawBitmap(backgroundBitmap, 0, 0, null);
////
////							// Update our animator objects
////							updateAnimators();
////
////							// Draw the PieChart
////					    	doDraw(canvas, mRotationDegree, mChartScale, mShowInfo);
////						}
////					}
////
////				} finally {
////
////					// do this in a finally so that if an exception is thrown
////					// during the above, we don't leave the Surface in an
////					// inconsistent state
//////					if (canvas != null) {
//////						mSurfaceHolder.unlockCanvasAndPost(canvas);
//////					}
////				}
////			}
////		}
//
//		/**
//		 * Update our animators that control animating the
//		 * rotation, scale, and info panel alpha
//		 */
//		private void updateAnimators() {
//
//			if (mRotateAnimator != null && mRotateAnimator.isRunning()) {
//				setRotationDegree(mRotateAnimator.floatUpdate());
//			}
//
//			if (mScaleAnimator != null && mScaleAnimator.isRunning()) {
//				mChartScale = mScaleAnimator.floatUpdate();
//			}
//
//			if (mInfoAnimator != null && mInfoAnimator.isRunning()) {
//				mInfoAlpha = mInfoAnimator.intUpdate();
//			}
//		}
//
//		/**
//		 * Clear the canvas upon termination of the thread
//		 */
//		private void cleanUp() {
//
//			Canvas canvas = null;
//
//			try {
//
////				canvas = mSurfaceHolder.lockCanvas(null);
//				canvas = m_Canvas;
//
//				synchronized (mPieChartView) {
//					if (canvas != null) {
////						canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//						canvas.drawColor(Color.WHITE);
//					}
//				}
//
//			} finally {
//				// do this in a finally so that if an exception is thrown
//				// during the above, we don't leave the Surface in an
//				// inconsistent state
////				if (canvas != null) {
////					mSurfaceHolder.unlockCanvasAndPost(canvas);
////				}
//			}
//		}
//
//		/**
//		 * Creates a drawing cache to aid in click selection
//		 */
//		private void buildDrawingCache() {
//
//			if (mDrawingCache == null) {
//				mDrawingCache = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//			}
//
//			Canvas cache = new Canvas(mDrawingCache);
//	    	doDraw(cache, mRotationDegree, mChartScale, mShowInfo);
//		}
//
//		/**
//		 * Draw the PieChart to a canvas, making sure that
//		 * all slices have been added and the chart has snapped
//		 * to its anchor.
//		 *
//		 * @param canvas the canvas to draw to
//		 */
//		public void drawCache(Canvas canvas) {
//
//			if (mDrawables.size() == 0 && mAdapter != null) {
//
//				addPieSlices();
//				snapTo(false);
//			}
//
//			doDraw(canvas);
//		}
//
//		/**
//		 * Draw to the supplied canvas using the current state of the
//		 * rotation, scale and info panel variables.
//		 *
//		 * @param canvas the canvas to draw to
//		 */
//		public void doDraw(Canvas canvas) {
//			doDraw(canvas, mRotationDegree, mChartScale, mShowInfo);
//		}
//
//		/**
//		 * Draw the pie chart
//		 *
//		 * @param canvas The canvas to draw the chart on
//		 * @param rotationDegree The current rotation of the chart
//		 * @param scale The scale of the chart
//		 * @param showInfo Should the info panel be drawn
//		 */
//		private void doDraw(Canvas canvas, float rotationDegree, float scale, boolean showInfo) {
//
//			if (canvas == null || mAdapter == null) return;
//
//			if (scale != 0) {
//
//				// Scale and rotate the canvas
//				canvas.save();
//				canvas.scale(scale, scale, mCenter.x, mCenter.y);
//				canvas.rotate(rotationDegree, mCenter.x, mCenter.y);
////				System.out.println("Debug_canvas_rotate:"+rotationDegree);
//		    	canvas.translate(getPaddingLeft(), getPaddingTop());
//
//		    	// Draw a background circle
//		    	//2015. 8. 5 파이차트 수정사항>> : 파이차트 배경 원 색상
////		    	Paint mBgCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
////		    	mBgCirclePaint.setColor(Color.rgb(230, 230, 230));
////		    	//2015. 8. 5 파이차트 수정사항<<
////				canvas.drawCircle(mCenter.x, mCenter.y, getChartRadius(), mBgCirclePaint);
//
//				// Draw all of the pie slices
//				synchronized (mDrawables) {
//			        for (PieSliceDrawable slice : mDrawables) {
//			        	slice.draw(canvas);
//			        }
//				}
//
//		        canvas.restore();
//			}
//
//			//2015. 8. 5 파이차트 수정사항 : 회전중일때는 부채꼴의 %정보 숨기기
//			if(!m_bIsMoving)
//			{
//				for (PieSliceDrawable slice : mDrawables) {
//					if(null == slice.getSlicePercentDrawable())
//					{
//						slice.setSlicePercentDrawable(m_Chart, getContext());
//					}
//					slice.getSlicePercentDrawable().draw(canvas);
//				}
//			}
//
//			// Draw the info panel if it has been set to show
//	        if (showInfo) {
//
//	        	//2015. 8. 5 파이차트 수정사항 : 가운데 원 정보창 그리기
//
////				createCaret();
////
////		        canvas.drawCircle(mCenter.x, mCenter.y, mInfoRadius, mStrokePaint);
////		        canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+mInfoRadius+10, mInfoRadius*3, mCenter.y+mInfoRadius*2+70, mStrokePaint);
////		        mCaret.draw(canvas);
//////		        mCaret.setCaretRotation(mSnapToDegree);
//////		        canvas.drawCircle(mCenter.x, mCenter.y, mInfoRadius, mPaint);
////		        canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+mInfoRadius+10, mInfoRadius*3, mCenter.y+mInfoRadius*2+70, mPaint);
//	        	if(mCenterCircleInfoDrawable!=null) {
//	        		canvas.drawCircle(mCenter.x, mCenter.y, getChartRadius()/3, mPaint);
//	        		mCenterCircleInfoDrawable.draw(canvas);
//	        	}
//
//	        	//2015. 8. 5 파이차트 수정사항 : 하단 정보창 그리기
////	        	canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 20),
////	        					mInfoRadius*3, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 150), mStrokePaint);
////		        mCaret.draw(canvas);
////	        	canvas.drawRect(mCenter.x-mInfoRadius, mCenter.y+getChartRadius() + UiUtils.getDynamicPixels(getContext(), 20),
////	        					mInfoRadius*3, mCenter.y+getChartRadius() +  + UiUtils.getDynamicPixels(getContext(), 150), mInfoPaint);
////	        	mInfoDrawable.draw(canvas);
//
//	        	//2015. 8. 5 파이차트 수정사항 : 하단 화살표
////	        	mArrowDrawable.draw(canvas);
//	        }
//		}
//	}

	//2015. 8. 5 파이차트 수정사항>> :  차트 크기 설정 API
	/**
	 * 파이차트 크기 설정
	 * @param nCenterX 중심 x좌표
	 * @param nCenterY 중심 y좌표
	 * @param nR 반지름
	 * */
	public void resizeChart(int nCenterX, int nCenterY, int nR)
	{
//		mCenter.x = nCenterX;
//		mCenter.y = nCenterY;
//		
//		mChartDiameter = nR * 2;
//		
//		mInfoRadius = getChartRadius() / 2;

//		if (mDrawingCache == null) {
//			mDrawingCache = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//		}
//		Canvas cache = new Canvas(mDrawingCache);
//		cache.drawColor(Color.WHITE);
//		mDrawThread.doDraw(cache);
//		
//		addPieSlices();
//		snapTo();




	}

	/**
	 * 실행중 기준각도 화살표 위치를 변경하고자 할때
	 * @param anchor 화살표 위치 (ex: PieChartAnchor.BOTTOM)
	 * */
	public void setArrowPositionType(PieChartAnchor anchor)
	{
		setSnapToAnchor(PieChartAnchor.RIGHT);

		//화살표 방향 
		mArrowDrawable.setArrowPositionType(m_strArrowPositionType);
		mArrowDrawable.setBounds(getArrowBounds());

		if(null != mDrawables && mDrawables.size() > 0)
		{
			for(PieSliceDrawable slice : mDrawables)
			{
				slice.setArrowPositionType(m_strArrowPositionType);
			}
		}
	}
	//2015. 8. 5 파이차트 수정사항<<

	/**
	 * Interfaces Used
	 */

	public interface OnPieChartExpandListener {
		public void onPieChartExpanded();
		public void onPieChartCollapsed();
	}

	public interface OnPieChartChangeListener {
		public void onSelectionChanged(int index);
	}

	public interface OnItemClickListener {
		public void onItemClick(boolean secondTap, View parent, Drawable drawable, int position, long id);
	}

	public interface OnInfoClickListener {
		public void onInfoClicked(int index);
	}

	public interface OnItemLongClickListener {
		public void onItemLongClick(View parent, Drawable drawable, int position, long id);
	}

	public interface OnRotationStateChangeListener {
		public void onRotationStateChange(int state);
	}

	public interface OnPieChartReadyListener {
		public void onPieChartReady();
	}

	class AdapterDataSetObserver extends DataSetObserver {

		private Parcelable mInstanceState = null;

		@Override
		public void onChanged() {

			if (mChartScale != 0f) {
				mNeedsUpdate = true;
				return;
			}

			resetChart();

			// Detect the case where a cursor that was previously invalidated
			// has been re-populated with new data.
			if (PieChartView.this.getPieChartAdapter().hasStableIds() && mInstanceState != null) {

				PieChartView.this.onRestoreInstanceState(mInstanceState);
				mInstanceState = null;
			}
		}

		@Override
		public void onInvalidated() {

			if (PieChartView.this.getPieChartAdapter().hasStableIds()) {

				// Remember the current state for the case where our hosting
				// activity is being stopped and later restarted
				mInstanceState = PieChartView.this.onSaveInstanceState();
			}

			if (mChartScale != 0f) {
				mNeedsUpdate = true;
				return;
			}

			resetChart();
		}

		public void clearSavedState() {
			mInstanceState = null;
		}
	}

//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
//			float arg3) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onShowPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
