/**
 *
 */
package drfn.chart.base;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

import Engine.facebook.android.Facebook;
import drfn.chart.NeoChart2;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.util.COMUtil;

/**
 * @author user
 *
 */
//2012. 8. 3   키보드 감추기 기능을 위해 OnEditorActionListener 를 포함시킴 
public class ModifySavedChartController extends View
		implements OnEditorActionListener {

	//	private static final int LAUNCHED_ACTIVITY_JipyoSetup = 1;
//	private FileInputStream mFileInputStream = null;
	public URL connectUrl = null;

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";

	boolean allowProMode = false;
	boolean allowOthers = false;
	String imgFilePath="";

	EditText memoBox=null;
	EditText saveTitle = null;
	public RelativeLayout layout=null;
	//	View xmlUI=null;
	LinearLayout xmlUI = null;
	//View xmlTwitterLoginUI=null;
	Handler mHandler=null;
	Context context;
	Facebook mFacebook = null;
//	private AsyncFacebookRunner mAsyncRunner;

	//public CheckBox fbBox = null;
//	private SharedPreferences prefs = null;
	Bitmap mBack = null;
	Paint paint = new Paint();

	//2012. 8. 14  저장하기창 가로화면 레이아웃 설정 : SL18
	ImageView saveImage;
	LinearLayout memoLayout;

	//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>	
//	public ModifySavedChartController(final Context context, RelativeLayout layout, String strSaveTitle, String strMemo) {
	public ModifySavedChartController(final Context context, RelativeLayout layout, String strSaveTitle, String strMemo, String strSaveDate, String strCodeName, String strPeriod, String strJipyoList) {
		//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>
		super(context);
		this.context = context;
//		prefs = COMUtil._chartMain.getSharedPreferences(COMUtil.appPackageId, Activity.MODE_PRIVATE);

		//2012. 8. 13  가로모드에서 저장하기 창을 실행시키면 세로모드로 나오게 수정 
//		COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); 
//		Configuration config = getResources().getConfiguration();

		//2012. 8. 14  저장하기창 가로화면 레이아웃 설정을 위해  화면방향 저장하는 플래그 -> false = 세로, true = 가로   : SL18
//		boolean bIsLandscape;
//		
//		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//			//--- 가로 화면 고정
//			//2012. 8. 14  저장하기창이 현재 화면방향에서 다른방향으로 회전되면 창을 닫게 수정  :  SL36 
////			 COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
//			 bIsLandscape = true;
//        }
//        else
//        {
//        	//--- 세로 화면 고정
//        	//2012. 8. 14  저장하기창이 현재 화면방향에서 다른방향으로 회전되면 창을 닫게 수정  :  SL36 
////        	COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
//        	bIsLandscape = false;
//        }

		this.layout = layout;

		mHandler = new Handler();

		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 20 태블릿  저장하기창 팝업화 
		int layoutResId;
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
		{
			layoutResId = context.getResources().getIdentifier("chart_save_tab", "layout", context.getPackageName());
		}
		else
		{
			layoutResId = context.getResources().getIdentifier("chart_save", "layout", context.getPackageName());
		}

		xmlUI = (LinearLayout)factory.inflate(layoutResId, null);
		xmlUI.setOnTouchListener(
				new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						//2012. 8. 3 키패드 감추기
						hideKeyPad();
						return true;
					}
				}
		);

		//2013. 2. 1  상단 바에 '상세설정' 타이틀 세팅 
		layoutResId = context.getResources().getIdentifier("frameaTitle", "id", context.getPackageName());
		TextView tvHeaderTitle = (TextView)xmlUI.findViewById(layoutResId);
		tvHeaderTitle.setText("상세 설정");	//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정

		layoutResId = context.getResources().getIdentifier("frameabtnfunction", "id", context.getPackageName());
		Button btnClose = (Button) xmlUI.findViewById(layoutResId);
		btnClose.setTypeface(COMUtil.typefaceMid);

		for(int i = 0; i < 2; i++){
			layoutResId = context.getResources().getIdentifier("savechart_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvModifyChart = (TextView)xmlUI.findViewById(layoutResId);
			tvModifyChart.setTypeface(COMUtil.typefaceMid);
		}

		//2012. 8. 2 차트저장하기 화면 오픈 orientation 설정 -> layout xml 변경으로 위쪽 코드 이동시킴 
//		layoutResId = context.getResources().getIdentifier("savemain", "id", context.getPackageName());
//		LinearLayout saveMain = (LinearLayout)xmlUI.findViewById(layoutResId);

		//2012. 8. 14 일부기기에서 저장하기 창 가로모드에서 길이가 짧던  문제 해결  : SL39
//		Display dis = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
//		int mDisWidth = dis.getWidth();            // 가로 사이즈 
//		int mDisHeight = dis.getHeight();          // 세로 사이즈

//		boolean bHighResolution = false;
//		
//		//2012. 8. 14 일부기기에서 저장하기 창 가로모드에서 길이가 짧던  문제 해결  : SL39
//		if(mDisWidth >= 1280 && mDisHeight >= 720)
//		{
//			bHighResolution = true;
//		}
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 start
		xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int) COMUtil.getPixel(320),ViewGroup.LayoutParams.WRAP_CONTENT));
//		Configuration config = getResources().getConfiguration();
//		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//		//2013. 8. 8 가로모드 인식방법 변경
//		{
//			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(280), (int)COMUtil.getPixel(295)));
//		}
//		else
//		{
//			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(320), ViewGroup.LayoutParams.WRAP_CONTENT));
//		}
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 end

		//2013. 1. 25 새로고침 버튼 
		layoutResId = context.getResources().getIdentifier("refreshbtn", "id", context.getPackageName());
		Button btnRefresh = (Button) xmlUI.findViewById(layoutResId);

//		layoutResId = context.getResources().getIdentifier("savedatetitle", "id", context.getPackageName());
//		TextView textTitle = (TextView) xmlUI.findViewById(layoutResId);
////		2012. 8. 1  글자색 xml에서 처리 
//		textTitle.setTextColor(Color.GRAY);
//		textTitle.setText(COMUtil.getSaveDate());

		//2013. 1. 25 저장하기창 종목정보 세팅 
//		layoutResId = context.getResources().getIdentifier("chartsave_tv_symbolname", "id", context.getPackageName());
//		TextView tvSymbolName = (TextView)xmlUI.findViewById(layoutResId);
//		tvSymbolName.setText(" : " + getStateTitle());
//
//		layoutResId = context.getResources().getIdentifier("chartsave_tv_price", "id", context.getPackageName());
//		TextView tvPrice = (TextView)xmlUI.findViewById(layoutResId);
//		
//		double _price = 0;
//		if(Integer.parseInt(COMUtil._neoChart._cdm.codeItem.strPrice) == 0)
//		{
//    		int startPos = COMUtil._neoChart._cvm.getIndex();
//    		int dataLen = startPos + COMUtil._neoChart._cvm.getViewNum() + COMUtil._neoChart._cvm.futureMargin;
//    		int nTotCnt = COMUtil._neoChart._cdm.getCount();
//    		if(dataLen>nTotCnt)
//    		{
//    			dataLen = nTotCnt;
//    		}
//    		if(dataLen>0)
//    		{
//    			double[] NSClose = COMUtil._neoChart._cdm.getSubPacketData("종가");
//    			if(NSClose == null)
//    			{
//    				return;
//    			}
//    			double[] fClose = NSClose;
//    			_price = fClose[dataLen-1];
//    			double prePrice = _price;
//    			if(dataLen > 1)
//    				prePrice = fClose[dataLen - 2];
//    		}
//		}
//		String strPrice = String.format("%.0f", _price);
//		tvPrice.setText(" : " + String.valueOf(strPrice));
//		
//		layoutResId = context.getResources().getIdentifier("chartsave_tv_chgrade", "id", context.getPackageName());
//		TextView tvChGrade = (TextView)xmlUI.findViewById(layoutResId);
//		
//		String strChange = ChartUtil.getFormatedData(COMUtil._neoChart._cdm.codeItem.strChange,COMUtil._neoChart._cdm.getPriceFormat());
//		int nChange = Integer.parseInt(COMUtil._neoChart._cdm.codeItem.strChange);
//		if(nChange < 0)
//		{
//			strChange = "▼"+strChange;
//			tvChGrade.setTextColor(Color.BLUE);
//		}
//		else
//		{
//			strChange = "▲"+strChange;
//			tvChGrade.setTextColor(Color.RED);
//		}
//		tvChGrade.setText(" : " + strChange + "(" + COMUtil._neoChart._cdm.codeItem.strChgrate + ")");
//		
//		
//		layoutResId = context.getResources().getIdentifier("chartsave_tv_volume", "id", context.getPackageName());
//		TextView tvVolume = (TextView)xmlUI.findViewById(layoutResId);
//		tvVolume.setText(" : " + COMUtil._neoChart._cdm.codeItem.strVolume);

		//2013. 1. 25 새로고침 버튼 리스너
		btnRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
//		    		Hashtable<String, String> dic = new Hashtable<String, String>();
//		    		dic.put("message", "기존 입력된 내용이 초기화 됩니다");
//		    		
//					if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_MESSAGE, dic);
					Toast.makeText(context, "기존 입력된 내용이 초기화 됩니다", Toast.LENGTH_LONG).show();
				}
				catch (Exception e) {
				}
			}
		});

		layoutResId = context.getResources().getIdentifier("chartsave_editname", "id", context.getPackageName());
		saveTitle = (EditText)xmlUI.findViewById(layoutResId);
		saveTitle.setText(strSaveTitle);
		saveTitle.setOnEditorActionListener(this);


		layoutResId = context.getResources().getIdentifier("memo_box", "id", context.getPackageName());
		memoBox = (EditText)xmlUI.findViewById(layoutResId);
		memoBox.setText(strMemo);

		memoBox.setOnEditorActionListener(this);

		//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>
		//일자 
		layoutResId = context.getResources().getIdentifier("detailinfo_date", "id", context.getPackageName());
		TextView detailinfo_date = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_date.setText("일시 : " + strSaveDate);
		//종목
		layoutResId = context.getResources().getIdentifier("detailinfo_jongmokname", "id", context.getPackageName());
		TextView detailinfo_jongmokname = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_jongmokname.setText("종목 : " + strCodeName);
		//주기
		layoutResId = context.getResources().getIdentifier("detailinfo_period", "id", context.getPackageName());
		TextView detailinfo_period = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_period.setText(strPeriod);
		//지표		
		layoutResId = context.getResources().getIdentifier("detailinfo_jipyo", "id", context.getPackageName());
		TextView detailinfo_jipyo = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_jipyo.setText(strJipyoList);
		//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>

		this.layout.addView(xmlUI);

		COMUtil.setGlobalFont(xmlUI);
	}
	//2012. 7. 23 차트저장화면 가로세로 전환을 위해 xmlUI getter 설정 
	public View getXmlUI()
	{
		return xmlUI;
	}

	private void close() {
//		COMUtil.captureImg=null;//capture image 초기화.
		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) { //Pad
			COMUtil.closeSaveChartPopover();
		} else {
			layout.removeView(xmlUI);
			unbindDrawables(xmlUI);
			System.gc();
		}
	}
	public void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}


	private String getStateTitle() {
		String rtnStr = "";
		rtnStr = COMUtil.codeName+"("+COMUtil.symbol+")"+" "+getPeriod();
//		rtnStr = COMUtil.getStateTitle();

		return rtnStr;
	}
	private String getPeriod() {
		String rtnStr="";
		String ptype = COMUtil.dataTypeName;
		if(ptype.equals("0")) {
			rtnStr = "틱";
		}else if(ptype.equals("1")) {
			rtnStr = "분";
		}else if(ptype.equals("2")) {
			rtnStr = "일간";
		}else if(ptype.equals("3")) {
			rtnStr = "주간";
		}else if(ptype.equals("4")) {
			rtnStr = "월간";
		}

		return rtnStr;
	}

//	private String PATH = null;

	/* 로컬 저장 */
	public boolean modifyToFile(String _nID, final TextView _tvTitle) {
		//2013. 1. 28 차트 저장 타이틀 추가 
		int layoutResId = context.getResources().getIdentifier("chartsave_editname", "id", context.getPackageName());
		EditText edSaveTitle = (EditText)xmlUI.findViewById(layoutResId);
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 start
//		String saveTitle = (String)edSaveTitle.getText().toString();
//		if(saveTitle==null || saveTitle.equals(""))
//			return;

		//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 start
		DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
		String saveTitle = (String)edSaveTitle.getText().toString();
		if(saveTitle==null || saveTitle.equals("")) {
			//2014. 3. 25 차트 저장하기창 제목입력한게 없을 때는 저장버튼 눌러도 창을 닫지 않고 메시지 표시>>
			alert.setMessage("저장명을 입력하셔야 합니다.");
			alert.setOkButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alert.show();
			COMUtil.g_chartDialog = alert;
			//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 end
			return false;
		}

		//로컬저장.
		String strLocalTable = COMUtil._mainFrame.strLocalFileName;


		if(mHelper==null) {
			mHelper = new ChartsaveDBHelper(context, strLocalTable);
		}
		db = mHelper.getReadableDatabase();

		if (!_tvTitle.getText().toString().equals(saveTitle)){
			cursor = db.rawQuery("SELECT saveTitleName FROM "+strLocalTable+" where saveTitleName='"+saveTitle+"'", null);


			if(cursor.getCount() > 0) {

				alert = new DRAlertDialog(COMUtil.apiView.getContext());

				alert.setTitle("");
				alert.setMessage("저장명이 중복되었습니다.");
				alert.setOkButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								db.close();
								cursor.close();
								mHelper.close();
								dialog.dismiss();
							}
						});
				alert.show();
				COMUtil.g_chartDialog = alert;
				return false;
			}
		}
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 end
		COMUtil.saveTitle = saveTitle;

//			COMUtil.showMessage(context, "상세정보가 수정되었습니다.");
		COMUtil.title = getStateTitle();
		COMUtil.detail = memoBox.getText().toString();
		COMUtil.saveDate = COMUtil.getSaveDate();

		_tvTitle.setText(COMUtil.saveTitle);
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정
//		modifyLocal(_nID);

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		base11.resetOneTouchList();
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end

		COMUtil.isModifyDetail = true;

		//if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);
//
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정
		return true;
	}

	ChartsaveDBHelper mHelper=null;
	SQLiteDatabase db=null;
	Cursor cursor = null;

	public void modifyLocal(String _strID) {
		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;
//    	int chartMode=0;
//    	if(base.chartList.size()>1) {
//    		chartMode = COMUtil.DIVIDE_CHART;
//    	} else { 
//    		chartMode = COMUtil.BASIC_CHART;
//    	}

		if(base.chartList.size()>0) {
			NeoChart2 chart = base.chartList.get(0);
			String strCode = chart._cdm.codeItem.strCode;
			if(strCode==null || strCode.trim().equals("")) {
				//이 종목은 저장할 수 없습니다.
				COMUtil.showMessage(COMUtil._chartMain, "해당 차트는 수정될 수 없습니다.");
				return;
			}
		}

		//파일로 저장한다.
		ActivityManager am = (ActivityManager)COMUtil._chartMain.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> info = am.getRunningTasks(1);
		ComponentName topActivity = info.get(0).topActivity;
		String name = topActivity.getPackageName();
//	   System.out.println("==================packetName:"+name);
//		PATH = "/data/data/"+name+"/files/";

		//2013. 1. 28 저장하기창에서 
//		String fileName = PATH+COMUtil.getRandomString(8)+".jpg";
//		SaveBitmapToFileCache(mBack, fileName);

		//로컬저장.
		String strLocalTable = COMUtil._mainFrame.strLocalFileName;


		if(mHelper==null) {
			mHelper = new ChartsaveDBHelper(context, strLocalTable);
		}
		db = mHelper.getReadableDatabase();
//		int maxCnt = 10; //최대 maxCnt만큼 처리.

		cursor = db.rawQuery("SELECT * FROM "+strLocalTable, null); //디바이스 저장 공간. 최대 maxCnt.

		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 start
		//저장 타이틀 중복 체크
//		cursor = db.rawQuery("SELECT saveTitleName FROM "+strLocalTable+" where saveTitleName='"+COMUtil.saveTitle+"'", null);
//		if(cursor.getCount()>0) {
//
//			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//
//			alert.setTitle("");
//			alert.setMessage("저장명이 중복되었습니다.");
//			alert.setOkButton("확인",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							db.close();
//							cursor.close();
//							mHelper.close();
//							dialog.dismiss();
//						}
//					});
//			alert.show();
//			COMUtil.g_chartDialog = alert;
//			return;
//		}
//		else
//		{
			db.execSQL("UPDATE "+strLocalTable+" SET detail=" + " '"+COMUtil.detail+"', saveTitleName=" + " '"+COMUtil.saveTitle+"' " + "where _id=" + " '" + _strID + "'");

//		}
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 end
		cursor.close();
		mHelper.close();
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 start
		DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());

		alert.setTitle("");
		String saveTitle = COMUtil.saveTitle;
		alert.setMessage("["+saveTitle+"]"+" 차트 수정이 완료되었습니다");
		alert.setOkButton("확인",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						dialog.dismiss();
					}
				});
		alert.show();

		this.close();
		COMUtil.closeModifyChartPopup();
		COMUtil.loadChart(null);
		//2020.05.19 by JJH >> 차트 불러오기 - 상세 설정 팝업 UI 수정 end
	}


	//2012. 8. 3 키보드 숨기기 동작 추가 
	//Device 의 키패드등에서 문자 입력후 확인 버튼 눌렸을 때의 동작 
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		//2012. 8. 14
		if( (event != null && actionId == event.getAction()) || actionId == KeyEvent.KEYCODE_ENTER)
		{
			hideKeyPad();
			return true;
		}
		else
		{
			return false;
		}
	}
	//문자 분석툴 사용시 키패드 감추기 위함
	public void hideKeyPad()
	{
		InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(memoBox.getWindowToken(), 0);
		memoBox.clearFocus();
	}
}
