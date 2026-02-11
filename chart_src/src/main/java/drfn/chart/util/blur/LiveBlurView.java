package drfn.chart.util.blur;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;

/**
 * source come from https://github.com/mmin18/RealtimeBlurView/blob/master/library/src/com/github/mmin18/widget/RealtimeBlurView.java
 */
public class LiveBlurView extends View {

    private float downsampleFactor; // default 4
    private int overlayColor; // default #00ffffff
    private int radius; //

    private final BlurEngine blurEngine;
    private boolean mDirty;
    public Bitmap bitmapToBlur, blurredBitmap;
    private Canvas blurringCanvas;
    private boolean isRendering;
    private Paint paint;
    private final Rect rectSrc = new Rect(), mRectDst = new Rect();
    // decorView는 액티비티의 root view로 지정한다(dialog처럼 다른 윈도우에 있는 경우에도)
    private View decorView;
    // 만약 view가 다른 root view상에 있다면 (보통은 PopupWindow일 때)
    // 수동으로 onPreDraw()에서 invalidate()를 호출해야한다. 그렇지 않으면 변경되지 않는다.
    private boolean differentRoot;
    private static int renderingCount; //LiveBlurView 위에 또 다른 LiveBlurView가 겹치는지 알기 위해 static 변수 사용

    public LiveBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);

        blurEngine = new StackBlur();
//        blurEngine = new StackBlurRs();
//        blurEngine = new StackBlurNative();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LiveBlurView);
        radius = a.getInt(R.styleable.LiveBlurView_liveBlurRadius,12);
        downsampleFactor = a.getFloat(R.styleable.LiveBlurView_liveBlurDownsampleFactor, 3);

        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//            overlayColor = a.getColor(R.styleable.LiveBlurView_liveBlurOverlayColor, Color.parseColor(String.valueOf(CoSys.GREY0_WHITE)));
            overlayColor = a.getColor(R.styleable.LiveBlurView_liveBlurOverlayColor, Color.argb(150, CoSys.GREY0_WHITE[0], CoSys.GREY0_WHITE[1], CoSys.GREY0_WHITE[2]));
        } else {
//            overlayColor = a.getColor(R.styleable.LiveBlurView_liveBlurOverlayColor, 0x90FFFFFF);
            overlayColor = a.getColor(R.styleable.LiveBlurView_liveBlurOverlayColor, Color.argb(190, CoSys.GREY0_WHITE[0], CoSys.GREY0_WHITE[1], CoSys.GREY0_WHITE[2]));
        }
        a.recycle();

        //2021.09.07 by lyk - blurview 그림자 효과 이미지 백그라운드 설정 추가 >>
        int layoutResId = 0;
        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
            layoutResId = this.getContext().getResources().getIdentifier("infoview_shadow_dark", "drawable", this.getContext().getPackageName());
        } else {
            layoutResId = this.getContext().getResources().getIdentifier("infoview_shadow", "drawable", this.getContext().getPackageName());
        }
        this.setBackgroundResource(layoutResId);

        // prepare
//        int strokeWidth = 5; // 5px not dp
//        int roundRadius = 12; // 15px not dp
//        int strokeColor = Color.parseColor("#2E3135");
//        int fillColor = Color.parseColor("#DFDFE0");
//
//        GradientDrawable gd = new GradientDrawable();
//        gd.setColor(fillColor);
//        gd.setCornerRadius(roundRadius);
//        gd.setStroke(strokeWidth, strokeColor);
//        this.setBackground(gd);

//        this.setBackgroundColor(Color.YELLOW);
        //2021.09.07 by lyk - blurview 그림자 효과 이미지 백그라운드 설정 추가 <<

        paint = new Paint();
    }

    public void setBlurRadius(int radius) {
        if (this.radius != radius) {
            this.radius = radius;
            mDirty = true;
            invalidate();
        }
    }

    public void setDownsampleFactor(float factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (downsampleFactor != factor) {
            downsampleFactor = factor;
            mDirty = true; // may also change blur radius
            releaseBitmap();
            invalidate();
        }
    }

    public void setOverlayColor(int color) {
        if (overlayColor != color) {
            overlayColor = color;
            invalidate();
        }
    }

    private void releaseBitmap() {
        if (bitmapToBlur != null) {
            bitmapToBlur.recycle();
            bitmapToBlur = null;
        }
        if (blurredBitmap != null) {
            blurredBitmap.recycle();
            blurredBitmap = null;
        }
    }

    protected void release() {
        releaseBitmap();
//        blurEngine.release();
    }

    protected boolean prepare() {
        if (radius == 0) {
            release();
            return false;
        }

        float downsampleFactor = this.downsampleFactor;
        float radius = this.radius / downsampleFactor;
        if (radius > 25) {
            downsampleFactor = downsampleFactor * radius / 25;
            radius = 25;
        }

        final int width = getBlurWidth();
        final int height = getBlurHeight();

        int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
        int scaledHeight = Math.max(1, (int) (height / downsampleFactor));

        boolean dirty = mDirty;

        if (blurringCanvas == null || blurredBitmap == null
                || blurredBitmap.getWidth() != scaledWidth
                || blurredBitmap.getHeight() != scaledHeight) {
            dirty = true;
            releaseBitmap();

            boolean r = false;
            try {
                bitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (bitmapToBlur == null) {
                    return false;
                }
                blurringCanvas = new Canvas(bitmapToBlur);

                blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (blurredBitmap == null) {
                    return false;
                }

                r = true;
            } catch (OutOfMemoryError e) {
                // Bitmap.createBitmap()를 호출하다보면 OOM 에러가 발생할 수 있는데, 그냥 무시하자
            } finally {
                if (!r) {
                    release();
                    return false;
                }
            }
        }

        if (dirty) {
//            if (blurEngine.prepare(getContext(), bitmapToBlur, radius)) {
            mDirty = false;
//            } else {
//                return false;
//            }
        }

        return true;
    }

    protected void blur(Bitmap bitmapToBlur) {
//        long processingTime = System.currentTimeMillis();
//        this.blurredBitmap = blurEngine.blur(getRoundedCornerBitmap(bitmapToBlur,12), (int) radius); //12
        this.blurredBitmap = blurEngine.blur(bitmapToBlur, (int) radius); //12

//        processingTime = System.currentTimeMillis() - processingTime;
//        Log.e("LiveBlurView","processingTime = "+processingTime+" width = "+bitmapToBlur.getWidth()+" height = "+bitmapToBlur.getHeight());
    }

    public Bitmap getBlurBitmap() {
        return this.blurredBitmap;
    }

    private int getBlurWidth() {
        return this.getWidth() - (int)COMUtil.getPixel(5);
    }

    private int getBlurHeight() {
        return this.getHeight() - (int)COMUtil.getPixel(5);
    }

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            final int[] locations = new int[2];
//            Bitmap oldBmp = blurredBitmap;
            View decor = decorView;
            if (decor != null && isShown() && prepare()) {
//                boolean redrawBitmap = blurredBitmap != oldBmp;
//                oldBmp = null;
                decor.getLocationOnScreen(locations);
                int x = -locations[0];
                int y = -locations[1];

//                x = -(int)COMUtil.getPixel(5);
//                y = -(int)COMUtil.getPixel(5);

                getLocationOnScreen(locations);
                x += locations[0];
                y += locations[1];

                // just erase transparent
                bitmapToBlur.eraseColor(overlayColor & 0xffffff);

                int rc = blurringCanvas.save();
                isRendering = true;
                renderingCount++;
                try {
                    blurringCanvas.scale(1.f * bitmapToBlur.getWidth() / getBlurWidth(), 1.f * bitmapToBlur.getHeight() / getBlurHeight());
                    blurringCanvas.translate(-x, -y);
                    if (decor.getBackground() != null) {
                        decor.getBackground().draw(blurringCanvas);
                    }
                    decor.draw(blurringCanvas);
                } catch (StopException e) {
                } finally {
                    isRendering = false;
                    renderingCount--;
                    blurringCanvas.restoreToCount(rc);
                }

                blur(bitmapToBlur);

//                if (redrawBitmap || differentRoot) {

                invalidate();
//                }
            }

            return true;
        }
    };

    protected View getActivityDecorView() {
        Context ctx = getContext();
        for (int i = 0; i < 4 && ctx != null && !(ctx instanceof Activity) && ctx instanceof ContextWrapper; i++) {
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        if (ctx instanceof Activity) {
            return ((Activity) ctx).getWindow().getDecorView();
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        decorView = getActivityDecorView();
        if (decorView != null) {
            decorView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            differentRoot = decorView.getRootView() != getRootView();
            if (differentRoot) {
                decorView.postInvalidate();
            }
        } else {
            differentRoot = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (decorView != null) {
            decorView.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        }
        release();
        super.onDetachedFromWindow();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRendering) {
            // Quit here, don't draw views above me
            throw STOP_EXCEPTION;
        } else if (renderingCount > 0) {
            // LiveBlurView 위에 또 다른 LiveBlurView 가 겹치는 것은 지원하지 않음.
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBlurredBitmap(canvas, blurredBitmap, overlayColor);
    }

    /**
     * Custom draw the blurred bitmap and color to define your own shape
     *
     * @param canvas
     * @param blurredBitmap
     * @param overlayColor
     */
    protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap, int overlayColor) {
        if (blurredBitmap != null) {
            rectSrc.right = blurredBitmap.getWidth();
            rectSrc.bottom = blurredBitmap.getHeight();
            mRectDst.left = (int)COMUtil.getPixel(5);
            mRectDst.top = (int)COMUtil.getPixel(5);
            mRectDst.right = getBlurWidth();
            mRectDst.bottom = getBlurHeight();

//            Bitmap shadowImg = doHighlightImage(blurredBitmap);
            //add shadow >>
//            int layoutResId = 0;
//            if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//                layoutResId = this.getContext().getResources().getIdentifier("infoview_shadow_dark", "drawable", this.getContext().getPackageName());
//            } else {
//                layoutResId = this.getContext().getResources().getIdentifier("infoview_shadow", "drawable", this.getContext().getPackageName());
//            }
//
//            Drawable drawbleShadow = null;
//            if (layoutResId != 0) {
//                drawbleShadow = this.getContext().getDrawable(layoutResId);
//            }
//
////		int layoutResId = context.getResources().getIdentifier("shadow_142342", "drawable", context.getPackageName());
////            Drawable drawbleShadow = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//            Bitmap bmpShadow = drawableToBitmap(drawbleShadow);
//            RectF shadowRec = new RectF();
//            shadowRec.set(0, 0, getWidth(), getHeight());
//
//            Paint mPaint = new Paint();
//            mPaint.setAlpha(255);
//            ColorFilter colorFilter = new PorterDuffColorFilter(Color.parseColor("#0affffff"), PorterDuff.Mode.DST_OVER);
//            mPaint.setColorFilter(colorFilter);
//            if(bmpShadow != null) {
//                canvas.drawBitmap(bmpShadow, null, shadowRec, null);
//            }
            //add shadow <<

            canvas.drawBitmap(getRoundedCornerBitmap(blurredBitmap, 9), rectSrc, mRectDst, null);
        }
        paint.setColor(overlayColor);

        RectF roundRect = new RectF((float)mRectDst.left, (float)mRectDst.top, (float)mRectDst.right, (float)mRectDst.bottom);
//        canvas.drawRect(mRectDst, paint);
        canvas.drawRoundRect(roundRect, 24, 24, paint);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

//    public void drawDimImageWithShadow(Context context, Canvas c, float x, float y, float width, float height, Bitmap bitImg, int alpha)
//    {
//        RectF rec = new RectF();
//        rec.set(x, y, x + width, y + height);
//
//        int layoutResId = 0;
//        if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
////			this.setBackgroundResource(R.drawable.infoview_shadow_dark);
//            layoutResId = context.getResources().getIdentifier("infoview_shadow_dark", "drawable", context.getPackageName());
//        } else {
////			this.setBackgroundResource(R.drawable.infoview_shadow);
//            layoutResId = context.getResources().getIdentifier("infoview_shadow", "drawable", context.getPackageName());
//        }
//
////		int layoutResId = context.getResources().getIdentifier("shadow_142342", "drawable", context.getPackageName());
//        Bitmap bmpShadow = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
//        RectF shadowRec = new RectF();
//        shadowRec.set(x, y, width, height);
//
//        Paint mPaint = new Paint();
//        mPaint.setAlpha(alpha);
//        ColorFilter colorFilter = new PorterDuffColorFilter(Color.parseColor("#0a000000"), PorterDuff.Mode.DST_OVER);
//        mPaint.setColorFilter(colorFilter);
//        c.drawBitmap(bmpShadow, null, shadowRec, null);
//        c.drawBitmap(bitImg, null, rec, mPaint);
//        mPaint.setAlpha(255);
//        mPaint.setColorFilter(null);
//    }
//
//    public Bitmap doHighlightImage(Bitmap src) {
//        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bmOut);
//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        Paint ptBlur = new Paint();
//        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
//        int[] offsetXY = new int[2];
//        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
//        Paint ptAlphaColor = new Paint();
//        ptAlphaColor.setColor(Color.BLACK);
//        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
//        bmAlpha.recycle();
//        canvas.drawBitmap(src, 0, 0, null);
//        return bmOut;
//    }

    private static class StopException extends RuntimeException {
    }

    private static StopException STOP_EXCEPTION = new StopException();

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int px) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect((int)COMUtil.getPixel_H(0), (int)COMUtil.getPixel_H(0), bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = px;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
