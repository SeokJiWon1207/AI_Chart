package drfn.chart.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.core.content.ContextCompat;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import drfn.chart.util.COMUtil;
//import drfn.chart.util.blur.BlurEngine;
import drfn.chart.util.blur.LiveBlurView;
import drfn.chart.util.blur.StackBlur;
import drfn.chart_src.R;

public class ScrollViewPanel extends ScrollView {

	public ViewPanel viewP;
//	private final BlurEngine blurEngine;
	public RectF bounds;
	RelativeLayout rl;

	Context context;
	boolean isShowRightSide = false; // 뷰패널이 오른쪽에 그려지면 true

	public ScrollViewPanel(Context context, RelativeLayout layout) {
		super(context);

//		blurEngine = new StackBlur();
		//아래 xml을 만든다는 생각으로 코딩(Programmatically)
		//<ScrollView>
		//  <LinearLayout width="MATCH_PARENT" height="WRAP_CONTENT">
		//    <ViewPanel margin="0"/>
		//  </LinearLayout>
		//</ScrollView>
		this.context = context;

		rl = new RelativeLayout(context);
		RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		rl.setLayoutParams(lparam);

//		int layoutResId = 0;
//		if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//			this.setBackgroundResource(R.drawable.infoview_shadow_dark);
//			layoutResId = context.getResources().getIdentifier("infoview_shadow_dark", "drawable", context.getPackageName());
//		} else {
////			this.setBackgroundResource(R.drawable.infoview_shadow);
//			layoutResId = context.getResources().getIdentifier("infoview_shadow", "drawable", context.getPackageName());
//		}

//		int layoutResId = context.getResources().getIdentifier("btn_close_viewp_n", "drawable", context.getPackageName());
//		Bitmap blurImg = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//		_cvm.drawDimImageWithShadow(context, COMUtil.apiView.getContext(), 5, 5, chart_bounds.width(), changeRect.height(), croppedBitmap, 255);

		viewP = new ViewPanel(context, layout);

//		GradientDrawable shape =  new GradientDrawable();
//		shape.setCornerRadius( 5 );
//		shape.setColor(Color.WHITE);
////		shape.setAlpha(255);
//		viewP.setBackground(shape);

		rl.addView(viewP);
//		drawBackground(context);
		this.addView(rl);

		setOnTouchListener(null);

		this.setHorizontalScrollBarEnabled(false);
		this.setVerticalScrollBarEnabled(false);
		this.setY(COMUtil.getPixel(12)); //2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정
	}

	public void drawDimImageWithShadow(Context context, Canvas c, float x, float y, float width, float height, Bitmap bitImg, int alpha)
	{
		RectF rec = new RectF();
		rec.set(x, y, x + width, y + height);

		int layoutResId = 0;
		if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//			this.setBackgroundResource(R.drawable.infoview_shadow_dark);
			layoutResId = context.getResources().getIdentifier("infoview_shadow_dark", "drawable", context.getPackageName());
		} else {
//			this.setBackgroundResource(R.drawable.infoview_shadow);
			layoutResId = context.getResources().getIdentifier("infoview_shadow", "drawable", context.getPackageName());
		}

//		int layoutResId = context.getResources().getIdentifier("shadow_142342", "drawable", context.getPackageName());
		Bitmap bmpShadow = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
		RectF shadowRec = new RectF();
		shadowRec.set(x, y, width, height);

		Paint mPaint = new Paint();
		mPaint.setAlpha(alpha);
		ColorFilter colorFilter = new PorterDuffColorFilter(Color.parseColor("#0a000000"), PorterDuff.Mode.DST_OVER);
		mPaint.setColorFilter(colorFilter);
		c.drawBitmap(bmpShadow, null, shadowRec, null);
		c.drawBitmap(bitImg, null, rec, mPaint);
		mPaint.setAlpha(255);
		mPaint.setColorFilter(null);
	}
	//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 >>
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				// only continue to handle the touch event if scrolling enabled
//				return false; // scrollable is always false at this point
//			default:
//				return super.onTouchEvent(ev);
//		}
//	}
//
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		// Don't do anything with intercepted touch events if
//		// we are not scrollable
//		return false;
//	}

//	public void setBlurView(View v) {
//		if(v.getParent() != null) {
//			((ViewGroup)v.getParent()).removeView(v);
//		}
//
//		LiveBlurView blurView = (LiveBlurView)v;
//		Bitmap bitmap = blurView.blurredBitmap;
//
//		rl.setBackgroundColor(Color.RED);drawBackground
//		rl.addView(v);
//		this.addView(rl);
//	}
	//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 >>
	Bitmap chartImg = null;
	public void setChartImg(Bitmap img) {
		this.chartImg = img;
	}
	private void drawBackground(Context context) {


		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
		{
			// 기존 적용 인포뷰 나인패치 배경이미지
//			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("kp_img_company_bubble_box_dark", "drawable", context.getPackageName()));
			// 디자이너분이 주신 인포뷰 나인패치 배경이미지
			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("kfit_img_chart_infowindow_dark", "drawable", context.getPackageName()));
			// 만든 나인패치 배경이미지
//			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("shadow_10452_dark", "drawable", context.getPackageName()));
			if (drawable != null) {
				drawable.setAlpha(150);
			}
			setBackground(drawable);
		}
		else
		{
//			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("kp_img_company_bubble_box", "drawable", context.getPackageName()));
			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("kfit_img_chart_infowindow", "drawable", context.getPackageName()));
//			Drawable drawable = ContextCompat.getDrawable(context, context.getResources().getIdentifier("shadow_10452", "drawable", context.getPackageName()));
			if (drawable != null) {
				drawable.setAlpha(150);
			}
			setBackground(drawable);

//			this.setBackgroundResource(R.drawable.infoview_shadow);

//			NinePatchDrawable ninepatch = null;
//			Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.kp_img_chart_infowindow);
//			if (image.getNinePatchChunk()!=null && viewP != null){
//				byte[] chunk = image.getNinePatchChunk();
////				Rect paddingRectangle = new Rect((int)viewP.bounds.left, (int)viewP.bounds.top, (int)viewP.bounds.width(), (int)viewP.bounds.height());
//				Rect paddingRectangle = new Rect(-10, -10, (int)viewP.getLayoutParams().width-20, (int)viewP.getLayoutParams().height-20);
//				ninepatch = new NinePatchDrawable(getResources(), image, chunk, paddingRectangle, null);
//			}
//			int sdk = android.os.Build.VERSION.SDK_INT;
//			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//				setBackgroundDrawable(ninepatch);
//			} else {
//				if(ninepatch != null) {
//					ninepatch.setAlpha(190);
//					setBackground(ninepatch);
//				}
//			}
		}
//		setElevation(1 * context.getResources().getDisplayMetrics().density);
	}

	private Drawable displayNinePatch(String path, Context context) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		byte[] chunk = bitmap.getNinePatchChunk();
		if(NinePatch.isNinePatchChunk(chunk)) {
			return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
		} else return new BitmapDrawable(bitmap);
	}

	public RectF getBounds() {
		return bounds;
	}

	public void setBounds(RectF rect) {
		this.bounds = rect;

		COMUtil._chartMain.runOnUiThread(new Runnable() {
			public void run() {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)bounds.width(), (int)bounds.height());
				lp.leftMargin = (int)bounds.left;
				lp.topMargin = (int)bounds.top;
				lp.bottomMargin = (int) COMUtil.getPixel(20); //2023.06.30 by SJW - 인포윈도우 스크롤 시 텍스트가 윈도우 영역을 벗어나는 현상 수정
				setLayoutParams(lp);
			}
		});
	}

	public void setContentBounds(RectF rect) {
		viewP.setBounds(rect);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		viewP.dispatchDraw(canvas);
	}

	public void setProcessPresentData(GL10 gl, Vector<Hashtable<String, String>> datas,boolean bInvester) {
		viewP.setProcessPresentData(gl, datas, bInvester);
	}

	public void setCompareChart(boolean bCompareChart) {
		viewP.bCompareChart = bCompareChart;
	}
	public void showCloseButton(boolean bShow) {
		viewP.showCloseButton(bShow);
	}
	public void showArrowToCrossline(boolean bShow, boolean isArrowRight) {
		viewP.showArrowToCrossline(bShow, isArrowRight);
		isShowRightSide = isArrowRight;
//		drawBackground(context);
	}

	public Boolean getIsShowRight() {
		return isShowRightSide;
	}
}
