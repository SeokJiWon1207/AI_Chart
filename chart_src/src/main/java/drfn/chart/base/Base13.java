package drfn.chart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import drfn.UserProtocol;
import drfn.chart.util.COMUtil;


@SuppressLint("ViewConstructor")
public class Base13 extends Base {

	RelativeLayout layout;
	Context context = null;
	ArrayList<Vertex> arVertex;
	private DrawView vw;
	public UserProtocol userProtocol;
	//boolean bDrawText = true;
	TextView[] m_tv = new TextView[5];
	public Base13(Context context , RelativeLayout layout) {
		super(context);
		this.layout = layout;
		this.context = context;
		frame.right = COMUtil.chartWidth;
		frame.bottom = COMUtil.chartHeight;

		this.setBackgroundColor(Color.YELLOW);

		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				frame.right, frame.bottom);
//		params.leftMargin=col * (nTotWidth/m_nColumnNum)+nGap;
//		params.topMargin=ih+row*(nTotHeight/m_nRowNum)+nGap;
		vw = new DrawView(context);
		vw.setLayoutParams(params);
		layout.addView(vw);
		layout.setBackgroundColor(Color.rgb(255, 255, 255));


		for(int i=0; i<5; i++)
		{
			m_tv[i] = new TextView(context);
			if(COMUtil.typeface != null)
				m_tv[i].setTypeface(COMUtil.typeface);
			m_tv[i].setTextSize(12);
			m_tv[i].setText(""+vw.GRID_COUNT/4*i);

			params =new RelativeLayout.LayoutParams(
					(int)COMUtil.getPixel(30), (int)COMUtil.getPixel(20));
			params.topMargin = frame.bottom - (int)COMUtil.getPixel(15);
			params.leftMargin = (frame.right-(int)COMUtil.getPixel(15))/4 * i;
			layout.addView(m_tv[i], params);
		}
		arVertex = new ArrayList<Vertex>();
	}

	public void init() {
		setPanel();
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	private void setPanel() {
		userProtocol = COMUtil._mainFrame.userProtocol;
	}

	public class Vertex {
		float x;
		float y;
		boolean draw;

		// 그리기 여부

		public Vertex(float x, float y, boolean draw) {
			this.x = x;
			this.y = y;
			this.draw = draw;
		}
	}

	public String getDrawCoordinate(boolean bSave) {
		if(bSave)
		{
			int nOrgCount = vw.GRID_COUNT;
			vw.GRID_COUNT = 20;
			String ret = vw.getDrawCoordinate();
			vw.GRID_COUNT = nOrgCount;
			return ret;
		}
		else
			return vw.getDrawCoordinate();
	}

	//2013.09.17 by LYH>> 사용자 패턴 저장
	public void drawImage(String imgFilePath)
	{
		vw.setDrawingCacheEnabled(true);
		Bitmap mainImg = vw.getDrawingCache(); //전체 화면이미지.
		Bitmap mBack = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(mBack);
		Paint paint = new Paint();
		c.drawBitmap(mainImg, 0, 0, paint);

		vw.setDrawingCacheEnabled(false);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(imgFilePath);
			mBack.compress(Bitmap.CompressFormat.PNG, 50, out);
		} catch (FileNotFoundException e) {
		}
		finally
		{
			try
			{
				if(out!=null)
					out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		mBack.recycle();
		mBack = null;
		//bDrawText = true;
		invalidate();
	}
	//2013.09.17 by LYH<<

	//2013.09.24 by LYH>> 패턴 기간 설정
	public void setGridCount(int nCount)
	{
		vw.GRID_COUNT = nCount;
		for(int i=0; i<5; i++)
		{
			m_tv[i].setText(""+vw.GRID_COUNT/4*i);
		}
	}
	//2013.09.24 by LYH<<

	//2013.10.02 by LYH>> 패턴 지우기
	public void clearPattern()
	{
		vw.clearPattern();
	}
	//2013.10.02 by LYH<<
	protected class DrawView extends View {
		Paint mPaint;
		Paint mPaint_Text;
		RelativeLayout layout;
		private int GRID_COUNT = 20;
		// 페인트 객체 선언
		public DrawView(Context context) {
			super(context);
			// 페인트 객체 생성 후 설정
			mPaint = new Paint();
			//mPaint.setColor(Color.rgb(243, 97, 0));
			mPaint.setColor(Color.rgb(199, 56, 17));
			mPaint.setStrokeWidth(4);
			mPaint.setAntiAlias(true);
			// 안티얼라이싱

			mPaint_Text = new Paint();
			if(COMUtil.typeface != null)
				mPaint_Text.setTypeface(COMUtil.typeface);
			mPaint_Text.setAntiAlias(true);
			mPaint_Text.setTextAlign(Align.LEFT);
		}
		//2013.10.02 by LYH>> 패턴 지우기
		public void clearPattern()
		{
			View v1 = (View)this;
			v1.setDrawingCacheEnabled(false);

			arVertex.clear();
			invalidate();
		}
		//2013.10.02 by LYH<<
		public String getDrawCoordinate() {
			//2013.10.02 by LYH>> 패턴 지우기
			if(arVertex.size()<1)
				return "0";
			//2013.10.02 by LYH<<
			//2013.09.24 by LYH>> 패턴 기간 설정 <<
			String[] coordinate = new String[GRID_COUNT];
			float[] drawPoint;
//			int nPos = 0;
			int nilCnt = 0;
			int tranX;
			int tranY;
//			char[] tmpVal;
			double xFactor = frame.right/GRID_COUNT;
			double xFactorMid = xFactor/2;
			for(int i=0; i<GRID_COUNT; i++) {
//				nPos = i * 4;

				//2013.09.24 by LYH>> 패턴 기간 설정
				//tranX = (int)(((95 - (i * 5))/100.0f) * frame.right);
//				tranX = (int)(((95 - (i * (100/GRID_COUNT)))/100.0f) * frame.right);
//				//2013.09.24 by LYH<<
//				tranX += COMUtil.getPixel(3);

				tranX = (int)((GRID_COUNT-i-1) * xFactor + xFactorMid);
				//2013.09.24 by LYH<<
				//tranX += xFactorMid;

				drawPoint = linePointInPosX(tranX);

				if (drawPoint[1] == -999) {
					nilCnt++;
					tranY = (int)drawPoint[1];
				}else{
					//2013.09.24 by LYH>> 패턴 기간 설정
					tranY = (int)((drawPoint[1] / frame.bottom) * 100.0f);
					//2013.09.24 by LYH<<
					tranY = 100 - tranY;
				}

				String tmpValStr = String.format("%04d", tranY);
				coordinate[i]=tmpValStr;
			}

			String value="";
			//2013.09.24 by LYH>> 패턴 기간 설정 <<
			if(nilCnt == GRID_COUNT) {
				value = "0";
			} else {
//				value = coordinate.toString();
				for(int i=0; i<coordinate.length; i++) {
					value+=coordinate[i];
				}
			}

			return value;
		}
		private float[] linePointInPosX(int posX) {
			float[] rtnVal = new float[2];

			View v1 = (View)this;
			v1.setDrawingCacheEnabled(true);
			Bitmap bitmap = v1.getDrawingCache(); //전체 화면이미지.

			//2013.09.24 by LYH>> 패턴 기간 설정 <<
			for (int i = 0; i < frame.bottom; i++) {
				int pixel = bitmap.getPixel(posX,i);
				int nRed = Color.red(pixel);
				int nGreen = Color.green(pixel);
				int nBlue = Color.blue(pixel);

				//2015. 2. 12 핑거서치 팔레트 블랫스킨일 때 처리>>
				int nColorVal;
				if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
				{
					nColorVal = Color.rgb(33,33,33);
				}
				else
				{
					nColorVal = Color.rgb(255,255,255);
				}

//				if(pixel!=-1 && pixel!=Color.rgb(250,250,250)) {
				if(pixel!=-1 && pixel!=nColorVal) {
					//2015. 2. 12 핑거서치 팔레트 블랫스킨일 때 처리<<
//					System.out.println(posX+": bbb "+pixel + ":" + nRed+ ":" + nGreen+ ":" + nBlue);
					rtnVal[0]=posX;
					rtnVal[1]=i;
					return rtnVal;
				}

			}

			rtnVal[0] = -999;
			rtnVal[1] = -999;
			return rtnVal;
		}
		/** 터치이벤트를 받는 함수 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					View v1 = (View)this;
					v1.setDrawingCacheEnabled(false);

					arVertex.clear();
					arVertex.add(new Vertex(event.getX(), event.getY(), false));
					if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_SELECT_VIEW, null);
					break;
				case MotionEvent.ACTION_MOVE:
					arVertex.add(new Vertex(event.getX(), event.getY(), true));
					break;
				case MotionEvent.ACTION_UP:
					if(userProtocol!=null) userProtocol.requestInfo(COMUtil._TAG_DRAW_PATTERN, null);
					break;
			}
			invalidate();
			// onDraw() 호출
			return true;
		}

		/** 화면을 계속 그려주는 함수 */
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			//2015. 2. 12 핑거서치 팔레트 블랫스킨일 때 처리>>
			if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
			{
				canvas.drawColor(Color.rgb(33,33,33));
			}
			else
			{
				canvas.drawColor(Color.rgb(255,255,255));
			}
			//2015. 2. 12 핑거서치 팔레트 블랫스킨일 때 처리<<

			// 캔버스 배경색깔 설정
			// 그리기
			if(arVertex.size()>0)
			{
				for (int i = 0; i < arVertex.size(); i++) {
					if (arVertex.get(i).draw) {
						// 이어서 그리고 있는 중이라면
						canvas.drawLine(arVertex.get(i - 1).x,
								arVertex.get(i - 1).y, arVertex.get(i).x,
								arVertex.get(i).y, mPaint);
						// 이전 좌표에서 다음좌표까지 그린다.
					} else {
						canvas.drawPoint(arVertex.get(i).x, arVertex.get(i).y,
								mPaint);
						// 점만 찍는다.
					}
				}
//				if(bDrawText)
//				{
//					for(int i=0; i<5;i++)
//					{
//						mPaint_Text.setColor(Color.rgb(66,66,66));
//				        mPaint_Text.setTextSize(20);
//				        mPaint_Text.setTypeface(Typeface.DEFAULT);
//				    	String strTitle = ""+(i*GRID_COUNT/4);
//				    	float nTextSize = mPaint_Text.measureText(strTitle);
//				    	if(i==4)
//				    		canvas.drawText(strTitle, frame.right/4*i-nTextSize, frame.bottom-COMUtil.getPixel(5), mPaint_Text);
//				    	else
//				    		canvas.drawText(strTitle, frame.right/4*i, frame.bottom-COMUtil.getPixel(5), mPaint_Text);
//					}
//				}
			}
			else
			{
				//2013.09.24 by LYH>> 패턴 초기에 텍스트 표시
				mPaint_Text.setColor(Color.rgb(30,80,200));
				mPaint_Text.setTextSize(COMUtil.getPixel(15));
				if(COMUtil.typeface != null)
					mPaint_Text.setTypeface(COMUtil.typefaceBold);
				String strTitle = "화면에 패턴을 그려주세요";
				float nTextSize = mPaint_Text.measureText(strTitle);
				canvas.drawText(strTitle, (frame.right-nTextSize)/2, frame.bottom/2+COMUtil.getPixel(7), mPaint_Text);
				//2013.09.24 by LYH<<
			}
		}
	}
}

