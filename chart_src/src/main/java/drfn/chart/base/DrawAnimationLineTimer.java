package drfn.chart.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;

import java.util.Timer;
import java.util.TimerTask;

import drfn.chart.draw.DrawTool;
import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;

/**
 * 애니메이션 라인차트의 Timer 에 쓰일 class
 * @author drfnkimsh
 * @since 2015. 7. 8 
 * @version 1.0   최초생성(15. 7. 8)
 * */
public class DrawAnimationLineTimer extends TimerTask
{
	/** 작업을 모두 실행한 후 종료시킬 타이머(이 TimerTask를 실행시키는 주체) */
	Timer m_Timer;
	/** 차트의 ChartViewModel */
	ChartViewModel m_cvm;
	/** 차트의 DrawTool */
	DrawTool m_dt;
	/** 차트가 그려질 NeoChart의 Canvas객체 */
	Canvas m_Canvas;
	/** 차트를 그릴 좌표 */
	float[] m_Positions;
	/** 차트의 색상 */
	int[] m_Color;
	/** 차트 좌표의 시작위치 (4개씩 변한다) */
	int m_nStartIdx = 0;
	
	int m_nId;
	
	private PathEffect[] mEffects; //여러가지 효과객체를 저장하는 배열변수
	
	private Path mPath;
	
	private int ROUTE_LINE_WIDTH = 6;
	private int nShowType = 0;
	private String baseLabel = "";
	
	/**
	 * @param _timer TimerTask를 실행시키는 주체
	 * @param _cvm ChartViewModel
	 * @param _gl  NeoChart의 Canvas객체
	 * @param _positions 차트를 그릴 좌표
	 * @param _cColor 차트의 색상
	 * */
	public DrawAnimationLineTimer(Timer _timer, ChartViewModel _cvm, DrawTool _dt, Canvas _gl, float[] _positions, int[] _cColor)
	{
		this.m_Timer = _timer;
		this.m_cvm = _cvm;
		this.m_dt = _dt;
		this.m_Canvas = _gl;
		this.m_Positions = _positions;
//		this.m_Color = _cColor;
		this.m_Color = CoSys.ACCRUE_CHART_COLORS[9];
		
		//여섯개의 효과객체를 설정하고 있습니다.
        mEffects = new PathEffect[6];
        
        mPath = new Path();
	}
	
	@Override
	public void run() {
		if(m_nStartIdx >= m_Positions.length)
		{
			cancel();
			this.m_Timer.cancel();
			
		    if(COMUtil.dataTypeName.equals("4") /*&& m_nStartIdx==0*/) {
			    //그라데이션 효과
		//	    float[] onePosition = new float[2];
		//	    onePosition[0] = m_Positions[m_nStartIdx];
		//	    onePosition[1] = m_Positions[m_nStartIdx+1];
			    
//			    m_cvm.drawLineWithFillGradient(m_Canvas, m_Positions, m_dt.max_view + (int)COMUtil.getPixel(10), m_Color, 127, m_Positions.length, m_dt.min_view);
//			    //_cvm.drawLineWithFillGradient(gl, positions, this.max_view + (int)COMUtil.getPixel(8), color0, 127, nIndex, this.min_view);
//			    
//			    m_cvm.getAnimationLineChartListener().postInvalidateToChart();
		    }
		    
			return;
		}
		Paint paint = new Paint();
		paint.setColor(Color.rgb(m_Color[0],m_Color[1],m_Color[2]));
		paint.setAntiAlias(true);
//		paint.setShadowLayer(2, 2, 2, 0x30000000);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(ROUTE_LINE_WIDTH);
//		paint.setShadowLayer(5.5f, 6.0f, 6.0f, 0x80000000);
		
		makeEffects(mEffects, 2);
		
		for (int i = 0; i < mEffects.length; i++) {
//			paint.setPathEffect(mEffects[i]);
			//Shadow Effect
			paint.setStrokeWidth(ROUTE_LINE_WIDTH);
			paint.setColor(Color.rgb(m_Color[0],m_Color[1],m_Color[2]));
//			paint.setXfermode(null);
			paint.setAlpha(100);
			
			m_Canvas.drawLine(m_Positions[m_nStartIdx], m_Positions[m_nStartIdx+1], m_Positions[m_nStartIdx+2], m_Positions[m_nStartIdx+3], paint);
//			m_Canvas.drawPath(makeFollowPath(m_Positions[m_nStartIdx], m_Positions[m_nStartIdx+1], m_Positions[m_nStartIdx+2], m_Positions[m_nStartIdx+3]), paint);
			
			//Shadow Effect
			paint.setStrokeWidth(ROUTE_LINE_WIDTH/1.7f);
			paint.setColor(Color.BLACK);
			paint.setAlpha(50);
//			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		    m_Canvas.drawLine(m_Positions[m_nStartIdx], m_Positions[m_nStartIdx+1]+ROUTE_LINE_WIDTH/1.7f, m_Positions[m_nStartIdx+2], m_Positions[m_nStartIdx+3]+ROUTE_LINE_WIDTH/1.7f, paint);
//			m_Canvas.drawPath(makeFollowPath(m_Positions[m_nStartIdx], m_Positions[m_nStartIdx+1]+ROUTE_LINE_WIDTH/1.7f, m_Positions[m_nStartIdx+2], m_Positions[m_nStartIdx+3]+ROUTE_LINE_WIDTH/1.7f), paint);
		    
//		    this.drawLineAnimation(m_Canvas,m_Positions[m_nStartIdx], m_Positions[m_nStartIdx+1], m_Positions[m_nStartIdx+2], m_Positions[m_nStartIdx+3],(int)(m_Positions[m_nStartIdx+2]-m_Positions[m_nStartIdx]), paint);
		}
		
		paint.setPathEffect(null);
		
		if(COMUtil.dataTypeName.equals("3")) {
			nShowType = 1;
		} else {
			nShowType = 0;
		}		
    	
	    if(nShowType==0) {
	    	//해당 데이터 위치의 사각형 점 표시
	    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx]-COMUtil.getPixel(6/3), m_Positions[m_nStartIdx+1]-COMUtil.getPixel(2), COMUtil.getPixel(6), COMUtil.getPixel(6), CoSys.CHART_BACK_COLOR[0], 0.8f, paint);
	    	//해당 데이터 상단 데이터1 표시
	    	baseLabel = m_nStartIdx*100+"만원";
	    	float sWidth = m_cvm.GetTextLength(baseLabel);
	    	float infoY = m_Positions[m_nStartIdx+1]-COMUtil.getPixel(20)-COMUtil.getPixel(10);
	    	this.drawFillRect(m_Canvas, (int)(m_Positions[m_nStartIdx]-(sWidth+40)/2), (int)infoY, (int)(sWidth+40), COMUtil.getPixel(20), CoSys.ACCRUE_CHART_COLORS[0], 1.0f, paint);
	    	this.drawFillTriangle(m_Canvas, (m_Positions[m_nStartIdx]-(sWidth+40)/2-(int)COMUtil.getPixel(6)/2) + (sWidth+40)/2,infoY+COMUtil.getPixel(20),COMUtil.getPixel(6),(int)COMUtil.getPixel(6), CoSys.ACCRUE_CHART_COLORS[0], paint);
	    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[1], (int)(m_Positions[m_nStartIdx]-sWidth/2), (int)(infoY+COMUtil.getPixel(9)), baseLabel);
	    	
	    	//해당 데이터 하단 데이터2 표시
	    	baseLabel = m_nStartIdx+"만원";
	    	sWidth = m_cvm.GetTextLength(baseLabel);
	    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[0], (int)(m_Positions[m_nStartIdx]-sWidth/2), (int)(m_Positions[m_nStartIdx+1]+COMUtil.getPixel(8)), baseLabel);
	    	
	    	//마지막 데이터 처리
	    	if((m_nStartIdx+4) >= m_Positions.length) {
		    	//해당 데이터 위치의 사각형 점 표시
		    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx+2]-COMUtil.getPixel(6/3), m_Positions[m_nStartIdx+3]-COMUtil.getPixel(2), COMUtil.getPixel(6), COMUtil.getPixel(6), CoSys.CHART_BACK_COLOR[0], 0.8f, paint);
		    	//해당 데이터 상단 데이터1 표시
		    	baseLabel = m_nStartIdx*100+"만원";
		    	sWidth = m_cvm.GetTextLength(baseLabel);
		    	infoY = m_Positions[m_nStartIdx+3]-COMUtil.getPixel(20)-COMUtil.getPixel(10);
		    	this.drawFillRect(m_Canvas, (int)(m_Positions[m_nStartIdx+2]-(sWidth+40)/2), (int)infoY, (int)(sWidth+40), COMUtil.getPixel(20), CoSys.ACCRUE_CHART_COLORS[0], 1.0f, paint);
		    	this.drawFillTriangle(m_Canvas, (m_Positions[m_nStartIdx+2]-(sWidth+40)/2-(int)COMUtil.getPixel(6)/2) + (sWidth+40)/2,infoY+COMUtil.getPixel(20),COMUtil.getPixel(6),(int)COMUtil.getPixel(6), CoSys.ACCRUE_CHART_COLORS[0], paint);
		    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[1], (int)(m_Positions[m_nStartIdx+2]-sWidth/2), (int)(infoY+COMUtil.getPixel(9)), baseLabel);
		    	
		    	//해당 데이터 하단 데이터2 표시
		    	baseLabel = m_nStartIdx+"만원";
		    	sWidth = m_cvm.GetTextLength(baseLabel);
		    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[0], (int)(m_Positions[m_nStartIdx+2]-sWidth/2), (int)(m_Positions[m_nStartIdx+3]+COMUtil.getPixel(8)), baseLabel);
	    	}
	    	
//	    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx]-COMUtil.getPixel(2), m_Positions[m_nStartIdx+1]+COMUtil.getPixel(5), COMUtil.getPixel(2), COMUtil.getPixel(3), CoSys.CHART_COLORS[0], 1.0f, paint);
	    } else if(nShowType==1) {
	    	//해당 데이터 위치의 사각형 점 표시
//	    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx]-COMUtil.getPixel(6/3), m_Positions[m_nStartIdx+1]-COMUtil.getPixel(2), COMUtil.getPixel(6), COMUtil.getPixel(6), CoSys.CHART_BACK_COLOR[0], 0.8f, paint);
	    	int x = (int)(m_Positions[m_nStartIdx]-COMUtil.getPixel(6/3));
	    	int y = (int)(m_Positions[m_nStartIdx+1]-COMUtil.getPixel(2));
	    	m_cvm.drawCircle(m_Canvas, x, y, x+COMUtil.getPixel(6), y+COMUtil.getPixel(6), true, CoSys.CHART_BACK_COLOR[0]);
	    	//해당 데이터 상단 데이터1 표시
	    	baseLabel = m_nStartIdx*100+"만원";
	    	float sWidth = m_cvm.GetTextLength(baseLabel);
	    	float infoY = m_Positions[m_nStartIdx+1]-COMUtil.getPixel(20)-COMUtil.getPixel(25);
	    	this.drawRoundedFillRect(m_Canvas, (int)(m_Positions[m_nStartIdx]-(sWidth+40)/2), (int)infoY, (int)(sWidth+40), COMUtil.getPixel(20), CoSys.ACCRUE_CHART_COLORS[1], 1.0f, paint);
//	    	this.drawFillTriangle(m_Canvas, (m_Positions[m_nStartIdx]-(sWidth+40)/2-(int)COMUtil.getPixel(6)/2) + (sWidth+40)/2,infoY+COMUtil.getPixel(20),COMUtil.getPixel(6),(int)COMUtil.getPixel(6), CoSys.ACCRUE_CHART_COLORS[1], paint);
	    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[1], (int)(m_Positions[m_nStartIdx]-sWidth/2), (int)(infoY+COMUtil.getPixel(9)), baseLabel);
	    	
	    	//해당 데이터 하단 데이터2 표시
	    	baseLabel = m_nStartIdx+"만원";
	    	sWidth = m_cvm.GetTextLength(baseLabel);
	    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[0], (int)(m_Positions[m_nStartIdx]-sWidth/2), (int)(infoY+COMUtil.getPixel(20)+COMUtil.getPixel(5)), baseLabel);
	    	
//	    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx]-COMUtil.getPixel(2), m_Positions[m_nStartIdx+1]+COMUtil.getPixel(5), COMUtil.getPixel(2), COMUtil.getPixel(3), CoSys.CHART_COLORS[0], 1.0f, paint);
	    	
	    	//마지막 데이터 처리
	    	if((m_nStartIdx+4) >= m_Positions.length) {
		    	//해당 데이터 위치의 사각형 점 표시
//		    	this.drawFillRect(m_Canvas,  m_Positions[m_nStartIdx]-COMUtil.getPixel(6/3), m_Positions[m_nStartIdx+1]-COMUtil.getPixel(2), COMUtil.getPixel(6), COMUtil.getPixel(6), CoSys.CHART_BACK_COLOR[0], 0.8f, paint);
		    	x = (int)(m_Positions[m_nStartIdx+2]-COMUtil.getPixel(6/3));
		    	y = (int)(m_Positions[m_nStartIdx+3]-COMUtil.getPixel(2));
		    	m_cvm.drawCircle(m_Canvas, x, y, x+COMUtil.getPixel(6), y+COMUtil.getPixel(6), true, CoSys.CHART_BACK_COLOR[0]);
		    	//해당 데이터 상단 데이터1 표시
		    	baseLabel = m_nStartIdx*100+"만원";
		    	sWidth = m_cvm.GetTextLength(baseLabel);
		    	infoY = m_Positions[m_nStartIdx+3]-COMUtil.getPixel(20)-COMUtil.getPixel(25);
		    	this.drawRoundedFillRect(m_Canvas, (int)(m_Positions[m_nStartIdx+2]-(sWidth+40)/2), (int)infoY, (int)(sWidth+40), COMUtil.getPixel(20), CoSys.ACCRUE_CHART_COLORS[1], 1.0f, paint);
//		    	this.drawFillTriangle(m_Canvas, (m_Positions[m_nStartIdx]-(sWidth+40)/2-(int)COMUtil.getPixel(6)/2) + (sWidth+40)/2,infoY+COMUtil.getPixel(20),COMUtil.getPixel(6),(int)COMUtil.getPixel(6), CoSys.ACCRUE_CHART_COLORS[1], paint);
		    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[1], (int)(m_Positions[m_nStartIdx+2]-sWidth/2), (int)(infoY+COMUtil.getPixel(9)), baseLabel);
		    	
		    	//해당 데이터 하단 데이터2 표시
		    	baseLabel = m_nStartIdx+"만원";
		    	sWidth = m_cvm.GetTextLength(baseLabel);
		    	m_cvm.drawString(m_Canvas, CoSys.CHART_BACK_COLOR[0], (int)(m_Positions[m_nStartIdx+2]-sWidth/2), (int)(infoY+COMUtil.getPixel(20)+COMUtil.getPixel(5)), baseLabel);
	    	}
	    }
	    
	    if(COMUtil.dataTypeName.equals("4") && m_nStartIdx==0) {
		    //그라데이션 효과
	//	    float[] onePosition = new float[2];
	//	    onePosition[0] = m_Positions[m_nStartIdx];
	//	    onePosition[1] = m_Positions[m_nStartIdx+1];
		    
		    m_cvm.drawLineWithFillGradient(m_Canvas, m_Positions, m_dt.max_view + (int)COMUtil.getPixel(10), m_Color, 80, m_Positions.length, m_dt.min_view);
		    //_cvm.drawLineWithFillGradient(gl, positions, this.max_view + (int)COMUtil.getPixel(8), color0, 127, nIndex, this.min_view);
	    }
	    
		m_cvm.getAnimationLineChartListener().postInvalidateToChart();

		m_nStartIdx+=4;
		

	}
	
	//그리기할 선의 점들을 정의하여, 각 점들을 라인으로 그리고 있습니다.
    private static Path makeFollowPath(float x, float y, float x1, float y1) {
        Path p = new Path();
        p.moveTo(x, y);
        int len = (int)(x1 - x);
        for (int i = 1; i <= len; i++) {
//            p.lineTo(x, y);
            p.moveTo(x+1, y+1);
            p.lineTo(x+1, y+1);

            
        }
        p.lineTo(x1, y1);
        return p;
    }

	public void drawLineAnimation(Canvas gl, float x, float y, float x1, float y1, int len, Paint mPaint) {	
//    	mPaint.setStyle(Paint.Style.FILL);
//    	mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		
    	mPath.reset();
    	for(int i=0; i<len; i++) {
	    	mPath.moveTo(x, y);
	    	mPath.lineTo(x+i, y+i);
    	}
    	mPath.lineTo(x1, y1);
    	mPath.close();
    	gl.drawPath(mPath, mPaint);
    }

	public void drawFillTriangle(Canvas gl, float x, float y, float w, float h, int[] color, Paint mPaint) {	
    	mPaint.setStyle(Paint.Style.FILL);
    	mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
    	mPath.reset();
    	
    	mPath.moveTo(x, y);
    	mPath.lineTo(x+w, y);
    	mPath.lineTo(x+w/2, y+h);
    	mPath.lineTo(x, y);
    	
    	mPath.close();
    	gl.drawPath(mPath, mPaint);
    }
	
	public void drawFillRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha, Paint mPaint) {
    	mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
    	mPaint.setAlpha((int)(alpha*255));
    	mPaint.setStyle(Paint.Style.FILL);
    	gl.drawRect(x, y, x+w, y+h, mPaint);
    	mPaint.setAlpha(255);
    }
	public void drawRoundedFillRect(Canvas gl, float x, float y, float w, float h, int[] color, float alpha, Paint mPaint) {
//    	mPaint.setColor(Color.rgb(color[0],color[1],color[2]));
		mPaint.setColor(0x665599EE);
		
    	mPaint.setAlpha((int)(alpha*255));
    	mPaint.setStyle(Paint.Style.FILL);
    	RectF roundR = new RectF(x, y, x + w, y + h);
    	gl.drawRoundRect(roundR, 14, 14, mPaint);
    	mPaint.setAlpha(255);
    }
	private static void makeEffects(PathEffect[] e, float phase) {
        //Paint.setPathEffect 에 널값을 넘기면, 현재 설정된 효과를 제거합니다.
		e[0] = null;     // no effect
        //끝이 둥근 선을 그리고, 인자는 반지름을 의미합니다.
        e[1] = new CornerPathEffect(10); 
        e[2] = new DashPathEffect(new float[] {10, 5, 5, 5}, phase);
        e[3] = new PathDashPathEffect(makePathDash(), 12, phase,PathDashPathEffect.Style.ROTATE);
        e[4] = new ComposePathEffect(e[2], e[1]);
        e[5] = new ComposePathEffect(e[3], e[1]);
    }
	// 점선을 그리는 Path 객체를 반환합니다.
    private static Path makePathDash() {
        Path p = new Path();
        p.moveTo(4, 0);
        p.lineTo(0, -4);
        p.lineTo(8, -4);
        p.lineTo(12, 0);
        p.lineTo(8, 4);
        p.lineTo(0, 4);
        return p;
    }
    
	public interface drawAnimationLineChartListener
	{
		public void postInvalidateToChart();
		public void onAnimationEnd();
	}
}


