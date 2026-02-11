package drfn.chart.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import drfn.chart.model.ChartViewModel;
import drfn.chart.util.COMUtil;
import drfn.chart.util.CoSys;
import drfn.chart_src.R;
//2012. 7. 9  mPaint 를 이용해 한글 글자 표시
//import drfn.chart.util.CoSys;

public class ViewPanel extends View implements View.OnTouchListener{

	protected RectF bounds;

	//2012. 7. 9  mPaint 를 이용해 한글 글자 표시
	Context context;
	Vector<Hashtable<String, String>> datas;
	public Paint mPaint;
	public boolean bCompareChart = false;
	private boolean isArrowDisabled = false;

	//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 Start
	private Bitmap bmpClose = null;	// 닫기 버튼 이미지

	// 닫기 버튼 위치관련
	int btnCloseSize = (int)COMUtil.getPixel(16);
	int topMargin = (int)COMUtil.getPixel(4);
	int rightMargin = (int)COMUtil.getPixel(4);

	// 닫기버튼 보이거나 숨기는 Flag
	boolean bShowCloseButton = false;  //2021.05.27 by hanjun.Kim - kakaopay - 닫기버튼 안보이게 수정 >>
	public boolean bTradeData = false;	//2019.07.17 by JJH >> 매매내역 표시 기능 추가
	//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End
	public boolean bEventBadgeData = false;

	//2021.05.28 by hanjun.Kim - kakaopay - 뷰패널 사이드 화살표 >>
	boolean isShowRightSide = true; // 뷰패널이 오른쪽에 그려지면 true
	private Bitmap bmpArrow = null;    // 측면 화살표 이미지

	boolean m_bIsRightDirection = true;
	boolean isFirstLine = false; //2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가

	public ViewPanel(Context context, RelativeLayout layout) {
		super(context);

		this.context = context;

		mPaint = new Paint();
		if(COMUtil.numericTypefaceMid != null)
			mPaint.setTypeface(COMUtil.numericTypefaceMid);
		mPaint.setTextSize(COMUtil.getPixel(11));

		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 Start
		int layoutResId = context.getResources().getIdentifier("btn_close_viewp_n", "drawable", context.getPackageName());
		bmpClose = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);

		int layoutResId2 = context.getResources().getIdentifier("kfit_core_ic_tooltip_arrow_right_round", "drawable", context.getPackageName());
		bmpArrow = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId2);

		setOnTouchListener(this);
		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End

//		int layoutResId = context.getResources().getIdentifier("land_info_bg", "drawable", context.getPackageName());
//		panelBackImg = BitmapFactory.decodeResource(context.getResources(), layoutResId);

//		RectF rectClose = new RectF(bounds.width() - btnCloseSize - rightMargin, topMargin, bounds.width() - rightMargin, topMargin + btnCloseSize);
//		canvas.drawBitmap(bmpClose, null, rectClose, null);	// 닫기 버튼 그리기

//		this.setBackgroundResource(R.drawable.btn_close_viewp_n);


	}


	public void setBounds(RectF rect) {
		this.bounds = rect;
		//2012. 7. 9  ViewPanel 이동. 아이폰의 y축 고정된 것을 보고 y축은 고정값을 줌
		COMUtil._chartMain.runOnUiThread(new Runnable() {
			public void run() {
				//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 >>
//				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int)bounds.width(), (int)bounds.height());
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams((int)bounds.width(), (int)bounds.height() - (int)COMUtil.getPixel(40));
				//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 <<
				rl.setMargins((int)bounds.left, (int)bounds.top, 0, 0);
				setLayoutParams(rl);
			}
		});
	}


	//2012. 7. 9  mPaint 를 이용해 한글 글자 표시
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (bTradeData)
			drawTradeData(canvas, datas);
		else if (bEventBadgeData)
			drawEventBadgeData(canvas, datas);
		else
			drawData(canvas, datas);
	}


	public void setProcessPresentData(GL10 gl, Vector<Hashtable<String, String>> datas, boolean bInvester) {
		//2012. 7. 9  mPaint 를 이용해 한글 글자 표시
//		COMUtil.drawFillRect(gl, bounds.left, bounds.top, bounds.width(),bounds.height(), CoSys.VIEWPANEL_COLOR, 0.7f);
//		COMUtil.drawRect(gl, bounds.left, bounds.top, bounds.width(),bounds.height(), CoSys.WHITE);
		this.datas = datas;

		//drawData(gl, datas);
	}

	public void setProcessPresentData(Vector<Hashtable<String, String>> datas, boolean isArrowDisabled) {
		this.datas = datas;
		this.isArrowDisabled = isArrowDisabled;
	}

	protected void drawData(Canvas canvas, Vector<Hashtable<String, String>> datas) {
		if( bounds == null) {
			return;
		}
		if (COMUtil._mainFrame == null) return; //2024.07.02 by SJW - crashlytics 오류 수정
		//blurred view test >>
//		RectF blurBounds = new RectF(5, 5, bounds.width() - 10, bounds.height() - 10);
//		canvas.drawBitmap(bmpClose, null, blurBounds, null);	// 닫기 버튼 그리기
		//blurred view test <<

		//2012. 7. 9  mPaint 를 이용해 한글 글자 표시
		//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09
		//int ih = (int)COMUtil.getPixel(12);
//		int ih = (int)(getFontSpacing() + + COMUtil.getPixel(2));
//		int igab = (int)COMUtil.getPixel(13);
		int ih = (int)COMUtil.getPixel(17);     //15
		int igab = (int)COMUtil.getPixel(17);
		int subGab = (int)COMUtil.getPixel(6);
//		int fx = 0;
//		int fy = 0;
		int fx = (int)COMUtil.getPixel(15);
		//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 >>
//		int fy = (int)COMUtil.getPixel(12);
		int fy = (int)COMUtil.getPixel(0);
		//2023.02.03 by SJW - 스크롤뷰 안되는 현상 수정 <<


		int[] arrUpColor = {255, 124, 158};
		int[] arrDownColor = {143, 212, 255};

		float fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(1);
		if(COMUtil.getVPFontSizeBtn() == 0)
			fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
		else if(COMUtil.getVPFontSizeBtn() == 2)
			fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);


//		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
//		{
//			mPaint.setColor(Color.WHITE);
//		}
//		else
//		{
//			mPaint.setColor(Color.BLACK);
//		}
//		mPaint.setStrokeWidth(2);
//		canvas.drawLine(fx, fy, fx, bounds.height(), mPaint); //좌
//		canvas.drawLine(fx, fy, bounds.width(), fy, mPaint);//상
//		mPaint.setStrokeWidth(2);
//		canvas.drawLine(bounds.width(), fy, bounds.width(), bounds.height(), mPaint); //우
//		canvas.drawLine(fx, bounds.height(), bounds.width(), bounds.height(), mPaint); //하

		//2015. 3. 4 차트 테마 메인따라가기 추가>>
//		if(COMUtil.bIsAutoTheme) {
//			if(COMUtil.currentTheme == COMUtil.SKIN_BLACK) {
//				mPaint.setColor(Color.TRANSPARENT);
//			} else {
//				mPaint.setColor(Color.rgb(142, 145, 150));
//				mPaint.setAlpha(220);
//			}
//		} else {


//		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
//		{
////			mPaint.setColor(Color.TRANSPARENT);
//		}
//		else
//		{
////			mPaint.setColor(Color.rgb(0, 0, 0));
////			//mPaint.setAlpha(230);
////			mPaint.setAlpha(192);
////			//mPaint.setColor(Color.rgb(142, 145, 150));
////			//mPaint.setAlpha(220);
//			int resId = context.getResources().getIdentifier("grey0", "color", context.getPackageName());
//			mPaint.setColor(context.getResources().getColor(resId));
//			mPaint.setColor(Color.rgb(255, 255, 255));
//			mPaint.setAlpha(165);
//		}
//		int resId = context.getResources().getIdentifier("grey0", "color", context.getPackageName());
//		mPaint.setColor(context.getResources().getColor(resId));
//		mPaint.setAlpha(230);

//		}

		//2015. 3. 4 차트 테마 메인따라가기 추가<<
//		mPaint.setStyle(Paint.Style.FILL);

//		canvas.drawRoundRect(new RectF(fx, fy, bounds.width(), bounds.height()), COMUtil.getPixel(4), COMUtil.getPixel(4), mPaint);

//		canvas.drawRoundRect(new RectF(fx, fy, bounds.width(), bounds.height()), COMUtil.getPixel(4), COMUtil.getPixel(4), mPaint);
		//		canvas.drawRoundRect(new RectF(fx, fy, bounds.width(), bounds.height()), COMUtil.getPixel(6), COMUtil.getPixel(6), mPaint);
//		canvas.drawRoundRect(new RectF(fx, fy, bounds.width(), bounds.height()), COMUtil.getPixel(4), COMUtil.getPixel(4), mPaint);


//		Bitmap panelBackImg_resize = Bitmap.createScaledBitmap(panelBackImg, bounds.width(), bounds.height(),true);
//		canvas.drawBitmap(panelBackImg_resize, fx, fy, mPaint);

//        Rect rec = new Rect();
//        rec.set(fx, fy, bounds.width(), bounds.height());
//
//        canvas.drawBitmap(panelBackImg, null, rec, mPaint);

		//2021. 06. 17 by hanjun.Kim - kakaopay 뷰패널 적용 >>
//		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
//		{
////			if (isShowRightSide) {
////				setBackgroundResource(context.getResources().getIdentifier("kp_img_buysell_bubble_right_dark", "drawable", context.getPackageName()));
////			} else {
////				setBackgroundResource(context.getResources().getIdentifier("kp_img_buysell_bubble_left_dark", "drawable", context.getPackageName()));
////			}
//		}
//		else
//		{
//			if (isShowRightSide) {
////				setBackgroundResource(context.getResources().getIdentifier("kp_img_buysell_bubble_right", "drawable", context.getPackageName()));
//			} else {
////				int layoutResId = context.getResources().getIdentifier("kp_img_buysell_bubble_left", "drawable", context.getPackageName());
////				Bitmap backgroundBitmap = BitmapFactory.decodeResource(COMUtil.apiView.getContext().getResources(), layoutResId);
////				Bitmap panelBackImg_resize = Bitmap.createScaledBitmap(backgroundBitmap, (int)(bounds.width() + COMUtil.getPixel(4)), (int)(bounds.height() + COMUtil.getPixel(4)),true);
////				canvas.drawBitmap(panelBackImg_resize, fx, fy, mPaint);
////				setBackgroundResource(context.getResources().getIdentifier("kp_img_buysell_bubble_left", "drawable", context.getPackageName()));
//			}
//		}
//		scrollviewpanel안에서 구현
		//2021. 06. 17 by hanjun.Kim - kakaopay 뷰패널 적용 <<

		if(datas == null)
		{
			return;
		}
		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 Start
		if(bShowCloseButton) {
			RectF rectClose = new RectF(bounds.width() - btnCloseSize - rightMargin, topMargin, bounds.width() - rightMargin, topMargin + btnCloseSize);
			canvas.drawBitmap(bmpClose, null, rectClose, null);	// 닫기 버튼 그리기
			//2019.07.17 by JJH >> 매매내역 표시 기능 추가 start
//			if(bTradeData){
//				bShowCloseButton = false;
//			}
			//2019.07.17 by JJH >> 매매내역 표시 기능 추가 end
		}
		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End

		//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 >>
		float maxLen = 0;
		int nRateWidth = 0;
//		int leftMargin = fx+(int)COMUtil.getPixel(8);
		int leftMargin = fx+(int)COMUtil.getPixel(12);
		int rightMargin = (int)COMUtil.getPixel(4);
//		for(int i=0; i<datas.size(); i++) {
//
//			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
//			Enumeration<String> enumStr = item.keys();
//			String key="";
//			key = enumStr.nextElement().toString();
//
//			String name=key;
//
//			String value=(String)item.get(key);
//
//			int nIndex=value.indexOf("(");
//
//			if (nIndex > 0 && name != "거래량") {
//				String strRate = value.substring(nIndex);
//				float nLen = mPaint.measureText(strRate) + COMUtil.getPixel(8);
//				if(nLen>maxLen)
//				{
//					maxLen = nLen;
//				}
//			}
//
////			if(i==0) { // 날짜
////				mPaint.setColor(Color.rgb(153, 153, 153));
////				mPaint.setTextSize(fontSize);
////				mPaint.setTypeface(COMUtil.numericTypeface);
////			} else {
////				mPaint.setColor(Color.WHITE);
////				mPaint.setTextSize(fontSize);
////				mPaint.setTypeface(COMUtil.typeface);
////			}
////
////			int nTextLength = 0;
////			try {
//////				nTextLength = COMUtil._mainFrame.mainBase.baseP._chart._cvm.GetTextLength(strRate, mPaint);
////				nTextLength = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(strRate, mPaint);
////			} catch (Exception e) {
////				e.printStackTrace();
////			}
////
////			if(nTextLength > nRateWidth)
////			{
////				nRateWidth = nTextLength;
////			}
//		}
		//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 <<
//		if(maxLen>0)
//		{
//			//maxLen = leftMargin + COMUtil.getPixel_W(140) - maxLen;
//			maxLen = bounds.right - maxLen;
//		}
		SimpleDateFormat server_format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat display_format = new SimpleDateFormat("yyyy. M. d.");

		int topMargin = 0;
		int subCnt = 0;
		for(int i=0; i<datas.size(); i++) {
			if (i==0) isFirstLine = true; //2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가
			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
			Enumeration<String> enumStr = item.keys();
			String key="";
			key = enumStr.nextElement().toString();

			String name=key;

			//2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상>>
			if(name.contains("88:88:88"))
			{
				name = name.replace("88:88:88", "장마감");
			} else if(name.contains("88:88"))
			{
				name = name.replace("88:88", "장마감");
			} else if(name.contains("99:99:99"))
			{
				name = name.replace("99:99:99", "시간외종료");
			} else if(name.contains("99:99"))
			{
				name = name.replace("99:99", "시간외종료");
			}
			//2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상<<

			String value=(String)item.get(key);

			value = value.replace("--", "-");

			//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09
			topMargin = fy+(ih*i)+igab+(subCnt*subGab);

			mPaint.setAntiAlias(true);
//			if(i==0) { // 날짜
//				mPaint.setColor(Color.rgb(153, 153, 153));
//				mPaint.setTextSize(fontSize);
//				mPaint.setTypeface(COMUtil.numericTypeface);
//			} else {
//				mPaint.setColor(Color.WHITE);
//				mPaint.setTextSize(fontSize-1);
//				mPaint.setTypeface(COMUtil.typeface);
			//			}
			//2021. 05. 18 by hanjun.Kim - kakaopay 뷰패널 글자색상 적용 >>
//			if(i!=0 && !value.equals("")) {
//				mPaint.setColor(Color.rgb(6, 11, 17));
//				mPaint.setAlpha(122);
//				mPaint.setTextSize(fontSize);
//				mPaint.setTypeface(COMUtil.typeface);
//			} else {
//				int resColorId = context.getResources().getIdentifier("grey990", "color", context.getPackageName());
//				mPaint.setColor(context.getResources().getColor(resColorId));
//				mPaint.setAlpha(0xFF);
//				mPaint.setTextSize(fontSize);
//				mPaint.setTypeface(COMUtil.typeface);
//			}

			if (name.equals("날짜") && !value.isEmpty()) {
				try {
					value = display_format.format(Objects.requireNonNull(server_format.parse(value)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			//2021. 05. 28 by hanjun.Kim - kakaopay 뷰패널 글자색상 적용 >>
			if (i == 0 || (value.equals("") && !name.contains("이평"))) {
				mPaint.setColor(CoSys.VIEWPANEL_TEXT_COLOR);
				mPaint.setAlpha(0xFF);
				mPaint.setTextSize(fontSize);
				mPaint.setTypeface(COMUtil.typeface);
			//2023.06.01 by SJW - 이평 데이터 없을 경우 해당 이평 영역 미노출 >>
//			} else if (value.equals("") && (name.contains("이평 5") || name.contains("이평 20") || name.contains("이평 60"))) {
			} else if (value.equals("") && (name.contains("이평"))) {
				continue;
			//2023.06.01 by SJW - 이평 데이터 없을 경우 해당 이평 영역 미노출 <<
			} else {
//				mPaint.setColor(Color.rgb(6, 11, 17));
				//2023.06.01 by SJW - 인포윈도우 폰트 컬러 변경 요청(다크테마 적용) >>
				if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
					mPaint.setColor(CoSys.VIEWPANEL_JIPYO_COLOR);
				} else {
					mPaint.setColor(CoSys.VIEWPANEL_JIPYO_COLOR_NIGHT);
				}
//				mPaint.setColor(CoSys.VIEWPANEL_TEXT_COLOR);
//				mPaint.setColor(CoSys.VIEWPANEL_JIPYO_COLOR);
//				mPaint.setAlpha(122);
//				mPaint.setAlpha(142);
				//2023.06.01 by SJW - 인포윈도우 폰트 컬러 변경 요청(다크테마 적용) <<
				mPaint.setTextSize(fontSize);
				mPaint.setTypeface(COMUtil.typeface);
			}
			//2021. 05. 28 by hanjun.Kim - kakaopay 뷰패널 글자색상 적용 <<


//			mPaint.setAlpha(0xFF);
			//2012. 8. 10  뷰패널에 새로 바뀐 폰트로 적용  : VP08
//			mPaint.setTypeface(Typeface.DEFAULT_BOLD);

			mPaint.setTextAlign(Align.LEFT);

			if (value.equals("") && i!=0 && !name.contains("이평")) {
				//2021.07.22 by hanjun.Kim - kakaopay - 인포뷰 구분선 라인추가
				Paint linePaint = new Paint();
				linePaint.setColor(CoSys.VIEWPANEL_LINE_COLOR);
				float lineTopMargin = topMargin - ih + (int) COMUtil.getPixel(12);

				// 2021.11.11 by JHY - 인포뷰 구분선 stopX 좌표 수정 >>
				//canvas.drawLine(leftMargin, lineTopMargin, bounds.width() - leftMargin, lineTopMargin+COMUtil.getPixel(0.5f), linePaint);
				canvas.drawLine(leftMargin, lineTopMargin, bounds.width() - leftMargin+15, lineTopMargin+COMUtil.getPixel(0f), linePaint);
				// 2021.11.11 by JHY - 인포뷰 구분선 stopX 좌표 수정 <<

				topMargin += (int) COMUtil.getPixel(12);
				subCnt +=2;
			} else if (i==0) {
				topMargin += (int) COMUtil.getPixel(8);
				subCnt +=1;
			}
			canvas.drawText(name, leftMargin, topMargin, mPaint);
			//2024.02.02 by SJW - "미국시간 기준" 텍스트 삭제 >>
			//2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 >>
//			String strTimeZoneText = COMUtil._mainFrame.mainBase.baseP._chart._cvm.strTimeZoneText;
//			if (isFirstLine && strTimeZoneText.toLowerCase().startsWith("america")){
//				//2023.06.02 by SJW - "미국시간 기준" 텍스트 다크 테마에서 표기 >>
////				int usdTextColor = Color.rgb(6, 11, 17);
//				int usdTextColor;
//				if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
//					usdTextColor = CoSys.VIEWPANEL_JIPYO_COLOR;
//				} else {
//					usdTextColor = CoSys.VIEWPANEL_JIPYO_COLOR_NIGHT;
//				}
//				//2023.06.02 by SJW - "미국시간 기준" 텍스트 다크 테마에서 표기 <<
//				int nameLen = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(name, mPaint);
//				mPaint.setColor(usdTextColor);
//				//2023.06.05 by SJW - "미국시간 기준" 투명도 변경 >>
////				mPaint.setAlpha(100);
//				mPaint.setAlpha(142);
//				//2023.06.05 by SJW - "미국시간 기준" 투명도 변경 <<
//				canvas.drawText("미국시간 기준", leftMargin + nameLen + COMUtil.getPixel(4), topMargin, mPaint);
//				isFirstLine = false;
//			}
			//2023.05.18 by SJW - 미국종목 인포윈도우 - "미국시간 기준" 텍스트 추가 <<
			//2024.02.02 by SJW - "미국시간 기준" 텍스트 삭제 <<
//			mPaint.setTextAlign(Align.RIGHT); //2016.10.12 by lyk - 수치조회창 내 가격표시 개선
			//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09

//			int resColorId = context.getResources().getIdentifier("grey990", "color", context.getPackageName());
			mPaint.setColor(CoSys.VIEWPANEL_TEXT_COLOR);
			mPaint.setAlpha(0xFF);
//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기. 기존소스 >>
			if(bCompareChart) {
//				mPaint.setTextAlign(Align.RIGHT); //2016.10.12 by lyk - 수치조회창 내 가격표시 개선
				mPaint.setTextSize(fontSize);
				mPaint.setTypeface(COMUtil.numericTypefaceMid);
				canvas.drawText(value, bounds.width()-leftMargin, topMargin, mPaint);
			} else {
				//				//canvas.drawText(value, leftMargin+COMUtil.getPixel(90), topMargin, mPaint);
				//
				//				//2016. 08. 01 by hyh - 수치조회 창 글씨 위치가 뷰의 크기에 따라 가변적으로 이동
				//				canvas.drawText(value, bounds.width()-leftMargin, topMargin, mPaint);
				//			}
				//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기. 기존소스 <<

				//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 >>
//				String strPrice="";
//				String strRate="";
//
//				int indexOfRate=value.indexOf("(");
//
//				if(indexOfRate >0){
//					strPrice=value.substring(0, indexOfRate);
//					if(!strPrice.contains(".")) {
//						if (strPrice.length() >= 13) {
//							strPrice = strPrice.substring(0, strPrice.length() - 8) + "M";
//						} else if (strPrice.length() >= 10) {
//							strPrice = strPrice.substring(0, strPrice.length() - 4) + "K";
//						}
//					}
//					strRate=value.substring(indexOfRate);
//				}

				//2021.05.24 by hanjun.Kim - kakaopay
//				strRate = strRate.replace("(", "");
//				strRate = strRate.replace(")", "");
//				strRate = strRate.trim();

//				if(value.contains("-")) {
//					mPaint.setColor(Color.rgb(arrDownColor[0], arrDownColor[1], arrDownColor[2]));
//				}
//				else {
//					mPaint.setColor(Color.rgb(arrUpColor[0], arrUpColor[1], arrUpColor[2]));
//					try {
//						if (Float.parseFloat(strRate.replace("%", "")) == 0)
//							mPaint.setColor(Color.rgb(221, 221, 221));
//					} catch (Exception e) {
//
//					}
//
//				}

//				if(strRate != null && !strRate.equals("")){
//					//2016.10.12 by lyk - 수치조회창 내 가격표시 개선
//					if(COMUtil._mainFrame.mainBase.baseP._chart._cvm.bIsMiniBongChart) {
//						int nameLen = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(name, mPaint);
////						int nameLen = (int)mesureText(name);
//						int priceLen = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(strPrice, mPaint);
//						canvas.drawText(strPrice, leftMargin+nameLen+COMUtil.getPixel(2), topMargin, mPaint);
//						canvas.drawText(strRate, leftMargin+nameLen+priceLen+COMUtil.getPixel(2), topMargin, mPaint);
//					}//2016.10.12 by lyk - 수치조회창 내 가격표시 개선 end
//					else {
//						mPaint.setTextSize(fontSize);
//						mPaint.setTypeface(COMUtil.numericTypefaceMid);
////						mPaint.setTextAlign(Align.RIGHT); //2016.10.12 by lyk - 수치조회창 내 가격표시 개선
//						canvas.drawText(strPrice, maxLen, topMargin, mPaint);
//						canvas.drawText(strRate, bounds.width()-rightMargin, topMargin, mPaint);
//					}
//				}
//				else {
//					if(COMUtil._mainFrame.mainBase.baseP._chart._cvm.bIsMiniBongChart) {
				int nameLen = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(name, mPaint);
//						canvas.drawText(value, leftMargin+nameLen+COMUtil.getPixel(2), topMargin, mPaint);
				//2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 >>
//					canvas.drawText(value, leftMargin + nameLen + COMUtil.getPixel(4), topMargin, mPaint);
				if (!value.equals("")) {
					int nIndex = value.indexOf(' ');
					if (nIndex > 0) {
						String strTitle = value.substring(0, nIndex);
						// strTitle 그리기
						canvas.drawText(strTitle, leftMargin + nameLen + COMUtil.getPixel(4), topMargin, mPaint);

						// strRate 그리기
						float titleWidth = mPaint.measureText(strTitle);
						//2024.03.12 by SJW - 2.27.5 crash 오류 수정 >>
						int newWidth = (int) COMUtil.getPixel(8);
						int newHeight = (int) COMUtil.getPixel(8);
						//2024.03.12 by SJW - 2.27.5 crash 오류 수정 <<
						//2023.05.30 by SJW - 인포윈도우 영역 겹치는 현상 수정 >>
//						if (value.contains("+0.00")) {
						if (value.contains("0.00")) {
						//2023.05.30 by SJW - 인포윈도우 영역 겹치는 현상 수정 <<
							mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[2][0], CoSys.CHART_COLORS[2][1], CoSys.CHART_COLORS[2][2]));
						} else if (value.contains("-")) {
							mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
							Bitmap bmpMinus = BitmapFactory.decodeResource(getResources(), R.drawable.kfit_service_ic_stockarrow_down_solid_kfits);
//							//2024.03.12 by SJW - 2.27.5 crash 오류 수정 >>
//							try {
//								if (bmpMinus != null && bmpMinus.getWidth() != 0 && bmpMinus.getHeight() != 0) {
//									newWidth = (int) COMUtil.getPixel(8);
//									newHeight = (int) ((float) newWidth / bmpMinus.getWidth() * bmpMinus.getHeight()); // 이미지 비율 유지
//								}
//							} catch (Exception e) {
//								newWidth = (int) COMUtil.getPixel(8);
//								newHeight = (int) COMUtil.getPixel(8);
//							}
//							//2024.04.19 by SJW - 2.27.5 crash 오류 수정(기호 사이즈 '0'일 경우 에러 처리) >>
//							if (newWidth == 0 || newHeight == 0) {
//								newWidth = (int) COMUtil.getPixel(8);
//								newHeight = (int) COMUtil.getPixel(8);
//							}
//							//2024.04.19 by SJW - 2.27.5 crash 오류 수정(기호 사이즈 '0'일 경우 에러 처리) <<
//							//2024.03.12 by SJW - 2.27.5 crash 오류 수정 <<
							bmpMinus = Bitmap.createScaledBitmap(bmpMinus, newWidth, newHeight, false);
							float x = leftMargin + nameLen + COMUtil.getPixel(4) + titleWidth + COMUtil.getPixel(4);
							float y = topMargin - COMUtil.getPixel(8);
							canvas.drawBitmap(bmpMinus, x, y, mPaint);
						} else if (value.contains("+")) {
							mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
							Bitmap bmpPlus = BitmapFactory.decodeResource(getResources(), R.drawable.kfit_service_ic_stockarrow_up_solid_kfits);
							//2024.03.12 by SJW - 2.27.5 crash 오류 수정 >>
//							try {
//								if (bmpPlus != null && bmpPlus.getWidth() != 0 && bmpPlus.getHeight() != 0) {
//									newWidth = (int) COMUtil.getPixel(8);
//									newHeight = (int) ((float) newWidth / bmpPlus.getWidth() * bmpPlus.getHeight()); // 이미지 비율 유지
//								}
//							} catch (Exception e) {
//								newWidth = (int) COMUtil.getPixel(8);
//								newHeight = (int) COMUtil.getPixel(8);
//							}
//							//2024.04.19 by SJW - 2.27.5 crash 오류 수정(기호 사이즈 '0'일 경우 에러 처리) >>
//							if (newWidth == 0 || newHeight == 0) {
//								newWidth = (int) COMUtil.getPixel(8);
//								newHeight = (int) COMUtil.getPixel(8);
//							}
							//2024.04.19 by SJW - 2.27.5 crash 오류 수정(기호 사이즈 '0'일 경우 에러 처리) <<
							//2024.03.12 by SJW - 2.27.5 crash 오류 수정 <<
							bmpPlus = Bitmap.createScaledBitmap(bmpPlus, newWidth, newHeight, false);
							float x = leftMargin + nameLen + COMUtil.getPixel(4) + titleWidth + COMUtil.getPixel(4);
							float y = topMargin - COMUtil.getPixel(8);
							canvas.drawBitmap(bmpPlus, x, y, mPaint);
						}

						String strRate = "  " + value.substring(nIndex).replaceAll("[\\-\\+]", "").trim();
						// strRate 그리기
						if (strRate.contains("0.00")) {
//						canvas.drawText(strRate, leftMargin + nameLen + titleWidth + COMUtil.getPixel(8), topMargin, mPaint);
							canvas.drawText(strRate.replaceAll("^\\s+", ""), leftMargin + nameLen + titleWidth + COMUtil.getPixel(8), topMargin, mPaint);
						} else {
							canvas.drawText(strRate, leftMargin + nameLen + COMUtil.getPixel(4) + titleWidth + COMUtil.getPixel(8), topMargin, mPaint);
						}
					} else {
						canvas.drawText(value, leftMargin + nameLen + COMUtil.getPixel(4), topMargin, mPaint);
					}
				}
				//2023.03.20 by SJW - 인포윈도우 등락 표기 방법 변경 <<


//					} else {
//						mPaint.setTextSize(fontSize);
//						mPaint.setTypeface(COMUtil.numericTypefaceMid);
//						mPaint.setTextAlign(Align.RIGHT); //2016.10.12 by lyk - 수치조회창 내 가격표시 개선
//						int priceLen = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getTextWidth(value, mPaint);
//						canvas.drawText(value, bounds.width()-rightMargin, topMargin, mPaint);
//					}
//				}
				//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 <<
			}
//			if(i==0)
//				fy += (int)COMUtil.getPixel(6);

//			if (value.equals("")) {
//				fy += (int) COMUtil.getPixel(2);
//			}
			//2021.05.21 by hanjun.Kim - kakaopay
		}
	}

	protected void drawEventBadgeData(Canvas canvas, Vector<Hashtable<String, String>> datas) {
		if( bounds == null) {
			return;
		}
		if (COMUtil._mainFrame == null) return; //2024.07.02 by SJW - crashlytics 오류 수정
		int ih = (int)COMUtil.getPixel(17);     //15
		int igab = (int)COMUtil.getPixel(17);
		int subGab = (int)COMUtil.getPixel(6);
		int fx = 0;
		int fy = 0;

		float fontSize = COMUtil.nFontSize_paint;
		if(COMUtil.getVPFontSizeBtn() == 0)
			fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
		else if(COMUtil.getVPFontSizeBtn() == 2)
			fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);

		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
		{
//			mPaint.setColor(Color.TRANSPARENT);
			if (isArrowDisabled)
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_company_bubble_box_dark", "drawable", context.getPackageName()));
			else
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_company_bubble_dark", "drawable", context.getPackageName()));
		}
		else
		{
			if (isArrowDisabled)
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_company_bubble_box", "drawable", context.getPackageName()));
			else
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_company_bubble", "drawable", context.getPackageName()));
		}

		if(datas == null)
		{
			return;
		}

		if(bShowCloseButton) {
			RectF rectClose = new RectF(bounds.width() - btnCloseSize - rightMargin, topMargin, bounds.width() - rightMargin, topMargin + btnCloseSize);
			canvas.drawBitmap(bmpClose, null, rectClose, null);	// 닫기 버튼 그리기
		}

		float maxLen = 0;
		int nRateWidth = 0;
//		int leftMargin = fx+(int)COMUtil.getPixel(12);
//		int rightMargin = (int)COMUtil.getPixel(4);

		int topMargin = 0;
		int subCnt = 0;
		for(int i=0; i<datas.size(); i++) {

			int leftMargin = fx+(int)COMUtil.getPixel(12);

			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
			Enumeration<String> enumStr = item.keys();
			String key="";
			key = enumStr.nextElement();

			String name=key;

			String value=(String)item.get(key);

			int nIndex=value.indexOf("(");

			if (nIndex > 0 && !name.equals("거래량")) {
				String strRate = value.substring(nIndex);
				float nLen = mPaint.measureText(strRate) + COMUtil.getPixel(8);
				if(nLen>maxLen)
				{
					maxLen = nLen;
				}
			}

			if(maxLen>0)
			{
				maxLen = bounds.right - maxLen;
			}

			value = value.replace("--", "-");

			//2012. 8. 10  뷰패널 높이 갭 등 dip 단위 적용 : VP09
			topMargin = fy+(ih*i)+igab+(subCnt*subGab);

			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setAntiAlias(true);
//			mPaint.setTextSize(fontSize-1);
			mPaint.setTextSize(fontSize);
			mPaint.setColor(CoSys.VIEWPANEL_TEXT_COLOR);
			mPaint.setTypeface(COMUtil.typeface);
			mPaint.setAlpha(0xFF);
			mPaint.setTextAlign(Align.LEFT);

			if (name.equals("재무발표일")) {
				igab += (int) COMUtil.getPixel(3);
				topMargin += (int) COMUtil.getPixel(6) + (int) COMUtil.getPixel(3);
//				mPaint.setFakeBoldText(true);
//				mPaint.setColor(Color.BLACK);
//				mPaint.setTypeface(COMUtil.numericTypefaceMid);
				canvas.drawText(value, leftMargin, topMargin, mPaint);
				continue;
			} else if (name.equals("재무년월")) {
				topMargin += (int) COMUtil.getPixel(6);
//				subCnt +=1;
//				mPaint.setFakeBoldText(true);
				mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[15][0], CoSys.CHART_COLORS[15][1], CoSys.CHART_COLORS[15][2]));
				canvas.drawText(value, leftMargin, topMargin, mPaint);
				continue;
			} else if ( name.equals("예상치") || name.equals("작년대비") || name.equals("지난분기대비")) {
				topMargin += (int) COMUtil.getPixel(6);
				mPaint.setAlpha(122);
				canvas.drawText(name, leftMargin, topMargin, mPaint);

				leftMargin += (int) mesureTextTitle(name + " ");

				mPaint.setAlpha(0xFF);
				mPaint.setTextSize(fontSize);
				mPaint.setTypeface(COMUtil.numericTypefaceMid);
				if (COMUtil.getSkinType() == COMUtil.SKIN_BLACK) {
					if (value.contains("+")) {
						mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
					} else {
						mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
					}
				}
				canvas.drawText(value, leftMargin, topMargin, mPaint);
				continue;
			}

			if (name.equals("VIEW_LINE")) {
				Paint linePaint = new Paint();
				linePaint.setColor(CoSys.VIEWPANEL_LINE_COLOR);
				canvas.drawLine(leftMargin + (int) COMUtil.getPixel(3), topMargin, bounds.width()-(int)COMUtil.getPixel(15), topMargin+COMUtil.getPixel(0.5f), linePaint);
				subCnt -=1;
//				topMargin = fy+(ih*i)+igab+(subCnt*subGab);
				continue;
			}

//			value.equals(context.getString(R.string.viewpanel_rightdata_hint))
			if (name.equals("D_multiline")){

//				if (name.equals("D_multiline")) {
//					if (value.equals(context.getString(R.string.viewpanel_rightdata_hint)))
//						mPaint.setAlpha(122);
					topMargin += (int) COMUtil.getPixel(6);

					if(value.contains("(Quarterly Dividend)")) {
						value = value.replace("(Quarterly Dividend)", "");
						canvas.drawText(value, leftMargin, topMargin, mPaint);

						ChartViewModel _cvm = COMUtil._mainFrame.mainBase.baseP._chart._cvm;
						float stringWidth = _cvm.getFontWidth(value, (int)COMUtil.getPixel(11));
						if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
							mPaint.setColor(Color.argb(122, 6, 11, 17));
						} else {
							mPaint.setColor(Color.argb(122, 252, 252, 252));
						}
						canvas.drawText("(Quarterly Dividend)", leftMargin + stringWidth, topMargin, mPaint);
//						mPaint.setColor(CoSys.VIEWPANEL_TEXT_COLOR);
					} else {
						//2023.06.16 by SJW - $포함 현금배당 정보 자리수 포맷팅 >>
						//2023.06.08 by SJW - 현금배당 정보 소수점 아래 4자리까지 표기 >>
//						canvas.drawText(value, leftMargin, topMargin, mPaint);
//						String truncatedValue = value.substring(0, Math.min(value.length(), 6));
						String truncatedValue = value.substring(0, Math.min(value.length(), 7)); // $정보 포함
						//2023.06.16 by SJW - $포함 현금배당 정보 자리수 포맷팅 <<

						canvas.drawText(truncatedValue, leftMargin, topMargin, mPaint);
						//2023.06.08 by SJW - 현금배당 정보 소수점 아래 4자리까지 표기 <<
					}
					continue;
//				} else {
//					topMargin += (int) COMUtil.getPixel(4);
////					mPaint.setFakeBoldText(false);
////					mPaint.setColor(Color.BLACK);
//					canvas.drawText(name + " ", leftMargin, topMargin, mPaint);
//
//					leftMargin += (int) mesureTextTitle(name + " ");
//
////					mPaint.setColor(Color.BLACK);
//					mPaint.setAlpha(122);
//					mPaint.setTextSize(fontSize);
//					mPaint.setTypeface(COMUtil.numericTypefaceMid);
//					canvas.drawText(value, leftMargin, topMargin, mPaint);
//					continue;
//				}
			}

			if (value.equals("") && i!=0) {
				topMargin += (int) COMUtil.getPixel(6);
				subCnt +=1;
			}


			if ( name.equals("D_date") || name.equals("D_현금배당") ) {
//				topMargin = (int) COMUtil.getPixel(1);
				if (name.equals("D_현금배당")) {
//					mPaint.setFakeBoldText(true);
					mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[22][0], CoSys.CHART_COLORS[22][1], CoSys.CHART_COLORS[22][2]));
					topMargin += (int) COMUtil.getPixel(6);
					canvas.drawText(value, leftMargin, topMargin, mPaint);
				} else {
					topMargin += (int) COMUtil.getPixel(6);
					canvas.drawText(value, leftMargin, topMargin, mPaint);

					//미국일자 기준 표시
					ChartViewModel _cvm = COMUtil._mainFrame.mainBase.baseP._chart._cvm;
					float stringWidth = _cvm.getFontWidth(value, (int)COMUtil.getPixel(11));
					if (COMUtil.getSkinType()!= COMUtil.SKIN_BLACK) {
						mPaint.setColor(Color.argb(122, 6, 11, 17));
					} else {
						mPaint.setColor(Color.argb(122, 252, 252, 252));
					}
					mPaint.setTextSize(COMUtil.getPixel(10));
					canvas.drawText(" 미국일자 기준", leftMargin + stringWidth, topMargin - COMUtil.getPixel(0.5f), mPaint);
				}
			} else if (!name.equals("VIEW_LINE")){
				topMargin += (int) COMUtil.getPixel(6);
//				mPaint.setFakeBoldText(false);
//				mPaint.setColor(Color.BLACK);
				mPaint.setAlpha(122);
				canvas.drawText(name, leftMargin, topMargin, mPaint);

				if (name.length() > 0)
					leftMargin += (int) mesureTextTitle(name + " ");

//				mPaint.setColor(Color.BLACK);
//				mPaint.setTextAlign(Align.RIGHT);
//				mPaint.setFakeBoldText(true);
				mPaint.setAlpha(0xFF);
				mPaint.setTextSize(fontSize);
				mPaint.setTypeface(COMUtil.numericTypefaceMid);
//				canvas.drawText(value, bounds.width()-leftMargin, topMargin, mPaint);
//				canvas.drawText(value, leftMargin, topMargin, mPaint);

				ChartViewModel _cvm = COMUtil._mainFrame.mainBase.baseP._chart._cvm;
				_cvm.drawScaleStringWidth(canvas, (int)bounds.width() - (int) mesureTextTitle(name) - (int) COMUtil.getPixel(25), CoSys.VIEWPANEL_TEXT_COLOR, leftMargin, topMargin - COMUtil.getPixel(1.0f), value, 1.0f);
			}
		}
	}

	protected void drawTradeData(Canvas canvas, Vector<Hashtable<String, String>> datas) {
		if( bounds == null) {
			return;
		}
		if (COMUtil._mainFrame == null) return; //2024.07.02 by SJW - crashlytics 오류 수정
		int ih = (int)COMUtil.getPixel(17);     //15
		int igab = (int)COMUtil.getPixel(2);
		int fx = (int)COMUtil.getPixel(15);
		if(m_bIsRightDirection == true) {
			fx = (int)COMUtil.getPixel(22);
		}
		int fy = (int)COMUtil.getPixel(17);

		float fontSize = COMUtil.nFontSize_paint;
		if(COMUtil.getVPFontSizeBtn() == 0)
			fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
		else if(COMUtil.getVPFontSizeBtn() == 2)
			fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);

//		if(COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
//		{
//			mPaint.setColor(Color.TRANSPARENT);
//		}
//		else
//		{
//			mPaint.setColor(Color.rgb(0, 0, 0));
//			mPaint.setAlpha(192);
//		}
//
//		mPaint.setStyle(Paint.Style.FILL);
//		canvas.drawRoundRect(new RectF(0, 0, bounds.width(), bounds.height()), COMUtil.getPixel(4), COMUtil.getPixel(4), mPaint);

		if (COMUtil.getSkinType() == COMUtil.SKIN_BLACK)
		{
			if (isShowRightSide)
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_buysell_bubble_right_dark", "drawable", context.getPackageName()));
			else
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_buysell_bubble_left_dark", "drawable", context.getPackageName()));
		}
		else
		{
			if (isShowRightSide)
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_buysell_bubble_right", "drawable", context.getPackageName()));
			else
				setBackgroundResource(context.getResources().getIdentifier("kfit_img_buysell_bubble_left", "drawable", context.getPackageName()));
		}

		if(datas == null)
		{
			return;
		}
		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 Start
		if(bShowCloseButton) {
			RectF rectClose = new RectF(bounds.width() - btnCloseSize - rightMargin, topMargin, bounds.width() - rightMargin, topMargin + btnCloseSize);
			canvas.drawBitmap(bmpClose, null, rectClose, null);	// 닫기 버튼 그리기
		}
		//2018.05.29 by sdm >> 롱클릭시 십자선 유지되게 수정 End

		//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 >>
		float maxLen = 0;
		int leftMargin = fx;
		int topMarginFirst = (int)COMUtil.getPixel(10);

		//2016. 08. 12 by hyh - 수치조회창 가격, 등락률 줄맞추기 <<
		if(maxLen>0)
		{
			//maxLen = leftMargin + COMUtil.getPixel_W(140) - maxLen;
			maxLen = bounds.right - maxLen;
		}
		int topMargin = 0;
		String buySellText = "";
		for(int i=0; i<datas.size(); i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)datas.get(i);
			Enumeration<String> enumStr = item.keys();
			String key="";
			key = enumStr.nextElement().toString();

			String name=key;

			//2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상>>
			if(name.contains("88:88:88"))
			{
				name = name.replace("88:88:88", "장마감");
			} else if(name.contains("88:88"))
			{
				name = name.replace("88:88", "장마감");
			} else if(name.contains("99:99:99"))
			{
				name = name.replace("99:99:99", "시간외종료");
			} else if(name.contains("99:99"))
			{
				name = name.replace("99:99", "시간외종료");
			}
			//2014. 1. 29 틱차트 장마감 봉에서 시간이 88:88:88 로 표시되는 현상<<

			String value=(String)item.get(key);
			String strText = "";
			leftMargin = fx;

			mPaint.setAntiAlias(true);
			mPaint.setTextSize(fontSize);
			mPaint.setTypeface(COMUtil.typeface);
			mPaint.setAlpha(0xFF);
			mPaint.setTextAlign(Align.LEFT);

			int resColorId = context.getResources().getIdentifier("kfit_grey990", "color", context.getPackageName());
			mPaint.setColor(context.getResources().getColor(resColorId, null));

			SimpleDateFormat server_format = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat display_format = new SimpleDateFormat("yyyy. M. d.");
			SimpleDateFormat s_format = new SimpleDateFormat("yyyy년 MM월 실적발표");

			//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 >>
			SimpleDateFormat display_format_year = new SimpleDateFormat("yyyy. M.");
			String strDataTypeName = COMUtil._mainFrame.ctlDataTypeName;
			//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 <<
			if (name.equals("매수매도구분")) {
				topMargin = fy;
				if (value.equals("0"))
					strText = "구매 ";
				else
					strText = "판매 ";

				buySellText = strText;
			} else if (name.equals("자료일자")) {
				fy = fy + topMarginFirst;
				topMargin = fy;
//				leftMargin += (int)mesureTextTitle("매수 ");
				try {
					//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 >>
//					strText = display_format.format(server_format.parse(value));
					if (strDataTypeName != null) { //2024.04.19 by SJW - 2.27.5 crash 오류 수정(strDataTypeName null일 경우 에러 처리)
						if (strDataTypeName.equals("3")) {
							strText = display_format_year.format(server_format.parse(value));
						} else {
							strText = display_format.format(server_format.parse(value));
						}
					//2024.04.19 by SJW - 2.27.5 crash 오류 수정(strDataTypeName null일 경우 에러 처리) >>
					} else {
						strText = display_format_year.format(server_format.parse(value));
					}
					//2024.04.19 by SJW - 2.27.5 crash 오류 수정(strDataTypeName null일 경우 에러 처리) <<
					//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 <<
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (name.equals("계좌번호")) {
				topMargin = fy+ih*(i-1)+igab;
				strText = name;
				canvas.drawText(strText, leftMargin, topMargin, mPaint);
				topMargin = fy+ih*i+igab;
				strText = value;
			} else if (name.equals("평균")) {
				mPaint.setAlpha(122);
				topMargin = fy+ih*i+igab;
//				strText = name;
				//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 >>
//				strText = buySellText + "평균";
				strText = "1주 평균";
				//2023.12.06 by SJW - 구매/판매 인포윈도우 기획 변경 <<
				canvas.drawText(strText+" ", leftMargin, topMargin, mPaint);
				leftMargin += (int)mesureTextTitle(strText+" ");
				//2023.06.29 by SJW - 구매/판매 가격 표시 소수점 "2자리 -> 4자리" 까지 표기로 변경 >>
//				strText = value;
				if (value != null && value.startsWith("$")) {
					String numericString = value.substring(1).replace(",", "");
					try {
						double numericValue = Double.parseDouble(numericString);

						String formattedValue = String.format("%.4f", numericValue);

						formattedValue = "$" + formattedValue;

						strText = formattedValue;

					} catch (NumberFormatException e) {

					}
				} else {
					strText = value;
				}
				//2023.06.29 by SJW - 구매/판매 가격 표시 소수점 "2자리 -> 4자리" 까지 표기로 변경 <<
				mPaint.setAlpha(0xFF);
			} else if (name.equals("수량")) {
				topMargin = fy+ih*i+igab;
				strText = name;
				canvas.drawText(strText+" ", leftMargin, topMargin, mPaint);
				leftMargin += (int)mesureTextTitle(name+" ");
				strText = value;
			} else if (name.equals("매수건") || name.equals("매도건")){
				mPaint.setAlpha(122);
				topMargin = fy+ih*i+igab;
//				strText = name;
//				canvas.drawText(strText+" ", leftMargin, topMargin, mPaint);
//				leftMargin += (int)mesureTextTitle(name+" ");
				strText = value;
				mPaint.setAlpha(0xFF);
				if (name.equals("매수건")) {
					mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[0][0], CoSys.CHART_COLORS[0][1], CoSys.CHART_COLORS[0][2]));
					buySellText = "구매";
				} else {
					mPaint.setColor(Color.rgb(CoSys.CHART_COLORS[1][0], CoSys.CHART_COLORS[1][1], CoSys.CHART_COLORS[1][2]));
					buySellText = "판매";
				}
			}
			canvas.drawText(strText, leftMargin, topMargin, mPaint);
		}
	}

	public float getFontSpacing()
	{
		return mPaint.getFontSpacing();
	}

	public float mesureTextTitle(String text)
	{
		float fontSize = COMUtil.nFontSize_paint;
		if(COMUtil.getVPFontSizeBtn() == 0)
			fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
		else if(COMUtil.getVPFontSizeBtn() == 2)
			fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);

		mPaint.setTextSize(fontSize);
		mPaint.setTypeface(COMUtil.typeface);
		return mPaint.measureText(text, 0, text.length());
	}
	public float mesureTextValue(String text)
	{
		float fontSize = COMUtil.nFontSize_paint;
		if(COMUtil.getVPFontSizeBtn() == 0)
			fontSize = COMUtil.nFontSize_paint-COMUtil.getPixel(2);
		else if(COMUtil.getVPFontSizeBtn() == 2)
			fontSize = COMUtil.nFontSize_paint+COMUtil.getPixel(2);
		mPaint.setTextSize(fontSize);
		mPaint.setTypeface(COMUtil.numericTypefaceMid);
		return mPaint.measureText(text, 0, text.length());
	}
	public void showCloseButton(boolean bShow) {
		this.bShowCloseButton = bShow;
	}

	public void showArrowToCrossline(boolean bShow, boolean isRight) {
		this.isShowRightSide = isRight;
	}

	public void setViewPanelRightDirection(boolean bRightFlag) {
		m_bIsRightDirection = bRightFlag;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				// 닫기버튼 클릭시 이벤트
				if(bShowCloseButton) {
//					RectF rect = new RectF(bounds.width() - (btnCloseSize*2) - rightMargin, 0, bounds.width(),topMargin + (btnCloseSize*2));
					if (bTradeData) {
//						if (bounds.contains((int) event.getX(), (int) event.getY())) {
						Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
						base11.hideTradeViewPanel(this);
//						}
					} else if (bEventBadgeData) {
//						if (bounds.contains((int) event.getX(), (int) event.getY())) {
//
//							Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
//							if (base11.m_bCompareChart) {
//								base11.showCrossLine(false);
//							} else {
//								base11.showCrossLineLongClick(false, this);
//							}
//						}
						Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
						base11.hideEventBadgeViewPanel(false, this);
					} else {
						if (bounds.contains((int) event.getX(), (int) event.getY())) {

							Base11 base11 = (Base11) COMUtil._mainFrame.mainBase.baseP;
							if (base11.m_bCompareChart) {
								base11.showCrossLine(false);
							} else {
								base11.showCrossLineLongClick(false, this);
							}
						}
					}
				}
		}
		return true;
	}
}
