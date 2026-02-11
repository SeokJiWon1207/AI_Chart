/**
 *
 */
package drfn.chart.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import Engine.KakaoLink;
import Engine.facebook.android.AsyncFacebookRunner;
import Engine.facebook.android.AsyncFacebookRunner.RequestListener;
import Engine.facebook.android.DialogError;
import Engine.facebook.android.Facebook;
import Engine.facebook.android.Facebook.DialogListener;
import Engine.facebook.android.FacebookError;
import drfn.chart.NeoChart2;
import drfn.chart.comp.DRAlertDialog;
import drfn.chart.util.COMUtil;

/**
 * @author user
 *
 */
//2012. 8. 3   키보드 감추기 기능을 위해 OnEditorActionListener 를 포함시킴 
public class SaveChartController extends View
		implements View.OnClickListener, TextView.OnEditorActionListener {

	//	private static final int LAUNCHED_ACTIVITY_JipyoSetup = 1;
	private FileInputStream mFileInputStream = null;
	public URL connectUrl = null;

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";

	boolean allowProMode = false;
	boolean allowOthers = false;
	String imgFilePath="";

	EditText memoBox=null;
	public RelativeLayout layout=null;
	//	View xmlUI=null;
	LinearLayout xmlUI = null;
	//View xmlTwitterLoginUI=null;
	Handler mHandler=null;
	Context context;
	Facebook mFacebook = null;
	private AsyncFacebookRunner mAsyncRunner;

//    ConfigurationBuilder builder=null;
//    AccessToken accessToken = null;
//    OAuthAuthorization oauth = null;
//    Configuration config = null;
//    TwitterFactory factory = null;

	//public CheckBox fbBox = null;
	private SharedPreferences prefs = null;
	Bitmap mBack = null;
	Paint paint = new Paint();

	//2012. 8. 14  저장하기창 가로화면 레이아웃 설정 : SL18
	ImageView saveImage;
	LinearLayout memoLayout;

	public SaveChartController(final Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		prefs = COMUtil._chartMain.getSharedPreferences(COMUtil.appPackageId, Activity.MODE_PRIVATE);

		//2012. 8. 13  가로모드에서 저장하기 창을 실행시키면 세로모드로 나오게 수정 
//		COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); 
		Configuration config = getResources().getConfiguration();

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

		//Facebook init.
//		mFacebook = new Facebook(C.FACEBOOK_APP_ID);
//		COMUtil.mFacebook = mFacebook; //ChartPro 메인(Activity)서 사용하기 위해 Static 변수로 설정한다.
//		mFacebookAccessToken = prefs.getString("FACEBOOK_ACCESS_TOKEN", "");
//	    mFacebook.setAccessToken(mFacebookAccessToken);
//	    
//	    mAsyncRunner = new AsyncFacebookRunner(mFacebook);

		//Twitter init.
//        builder = new ConfigurationBuilder();
//        builder.setOAuthConsumerKey(COMUtil.consumer_key);
//        builder.setOAuthConsumerSecret(COMUtil.consumer_secret); 
//        config =  builder.build();
//        oauth = new OAuthAuthorization(config);
//        oauth.setOAuthConsumer(COMUtil.consumer_key, COMUtil.consumer_secret);
//        factory = new TwitterFactory(config);

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
		//2020.05.08 by JJH >> 가로모드 작업 (차트 저장 팝업)
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams((int) COMUtil.getPixel(324),ViewGroup.LayoutParams.WRAP_CONTENT);	//2014. 10. 14 popupwindow 배경 반투명색
		xmlUI.setLayoutParams(params);
		//2012. 7. 24 자동추세설정창 에디트텍스트 레이아웃 설정
		//setUI();
		xmlUI.requestFocus();
		//2012. 7. 23  최초 차트저장하기 화면 오픈시  orientation 설정 
		//2012. 8. 2 차트저장하기 orientation layout 변경으로 해당 코드 주석처리하고 savemain을 불러온 saveMain  변수쪽으로 코드 이동 
//		Configuration config = getResources().getConfiguration();
//		layoutResId = context.getResources().getIdentifier("LinearLayout02", "id", context.getPackageName());
//		LinearLayout linearXmlUI = (LinearLayout)xmlUI.findViewById(layoutResId);
//        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//        {
//        	linearXmlUI.setOrientation(LinearLayout.HORIZONTAL);
//        }
//        else
//        {
//        	linearXmlUI.setOrientation(LinearLayout.VERTICAL);
//        }

//		layoutResId = context.getResources().getIdentifier("savetop", "id", context.getPackageName());
//		RelativeLayout savetop = (RelativeLayout)xmlUI.findViewById(layoutResId);
		//이미지 줄이기.(OutOfMemory 해결)
//		layoutResId = context.getResources().getIdentifier("bg_top", "drawable", context.getPackageName());
//		Drawable drawable = COMUtil.getSmallBitmap(layoutResId);
		//2012. 7. 24 차트저장화면   배경색 변경 
//		savetop.setBackgroundDrawable(drawable);

		/** Pad용 화면 구성 **/
//		if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB)) { //Pad
//
//			savetop.setVisibility(View.GONE);
//		}
		//2012. 8. 2 차트저장하기 화면 오픈 orientation 설정 -> layout xml 변경으로 위쪽 코드 이동시킴 
//		layoutResId = context.getResources().getIdentifier("savemain", "id", context.getPackageName());
//		LinearLayout saveMain = (LinearLayout)xmlUI.findViewById(layoutResId);

		//2012. 8. 14 저장하기창 가로화면 레이아웃 설정을 위해 메모창과 이미지창 불러옴 
//		layoutResId = context.getResources().getIdentifier("savechart_image", "id", context.getPackageName());
//		saveImage = (ImageView) xmlUI.findViewById(layoutResId);

//		layoutResId = context.getResources().getIdentifier("memoLayout", "id", context.getPackageName());
//		memoLayout = (LinearLayout) xmlUI.findViewById(layoutResId);

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

		//2012. 8. 14   저장하기창 가로화면 설정을 위해 주석 해제  :   SL18
//		if(bIsLandscape)
//        {
//			if(bHighResolution)
//			{
//				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//				{
//					
//				}
//				else
//				{
//					saveMain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, COMUtil.chartHeight+(int)COMUtil.getPixel(20)));
//				}
//				
//			}
//			else
//			{
//				if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//				{
//					
//				}
//				else
//				{
//					saveMain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, COMUtil.chartHeight+(int)COMUtil.getPixel(5)));
//				}
//				
//			}
//			 saveMain.setOrientation(LinearLayout.HORIZONTAL);
//        }
//        else
//        {
////        	memoLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0));
//        	//2012. 8. 22   태블릿에서 세로모드로 돌리고 저장하기 창 열었을 때 아무것도 안나오던 현상 수정 : SL_tab13
//        	if(COMUtil.deviceMode.equals(COMUtil.HONEYCOMB))
//			{
//        		saveMain.setOrientation(LinearLayout.HORIZONTAL);
//			}
//        	else
//        	{
//        		saveMain.setOrientation(LinearLayout.VERTICAL);
//        	}
//        	
//        }
		//2014. 10. 14 popupwindow 배경 반투명색>>
//		xmlUI.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		//2020.05.08 by JJH >> 가로모드 작업 (차트 저장 팝업) start
//		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
//		//2013. 8. 8 가로모드 인식방법 변경
//		{
//			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(320), ViewGroup.LayoutParams.WRAP_CONTENT));
//		}
//		else
//		{
//			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(320), ViewGroup.LayoutParams.WRAP_CONTENT));
//		}
		//2020.05.08 by JJH >> 가로모드 작업 (차트 저장 팝업) end
		//2014. 10. 14 popupwindow 배경 반투명색<<

		//2012. 8. 1  저장화면 배경 흰색으로 설정위해 주석처리 
		//이미지 줄이기.(OutOfMemory 해결)
//		layoutResId = context.getResources().getIdentifier("wholebg", "drawable", context.getPackageName());
//	    drawable = COMUtil.getSmallBitmap(layoutResId);
//		saveMain.setBackgroundDrawable(drawable);

		//Button btnBack = (Button) findViewById(R.id.frameaBtnBack);
		layoutResId = context.getResources().getIdentifier("frameabtnfunction", "id", context.getPackageName());
		Button btnClose = (Button) xmlUI.findViewById(layoutResId);
		btnClose.setTypeface(COMUtil.typefaceMid);

		//2013. 1. 25 새로고침 버튼 
		layoutResId = context.getResources().getIdentifier("refreshbtn", "id", context.getPackageName());
		Button btnRefresh = (Button) xmlUI.findViewById(layoutResId);


//		int resId = COMUtil.apiView.getContext().getResources().getIdentifier("frameabtnfunction", "id", COMUtil.apiView.getContext().getPackageName());
//		Button btnClose = (Button) xmlUI.findViewById(resId);
//
//		resId = COMUtil.apiView.getContext().getResources().getIdentifier("cancelbtn", "id", COMUtil.apiView.getContext().getPackageName());
//		Button btnCancel = (Button) xmlUI.findViewById(resId);

		int resId = COMUtil.apiView.getContext().getResources().getIdentifier("acceptbtn", "id", COMUtil.apiView.getContext().getPackageName());
		Button btnAccept = (Button) xmlUI.findViewById(resId);
		if(btnAccept!=null) {
			btnAccept.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
						saveChartToFile();
				}
			});
		}

		for(int i = 0; i < 2; i++){
			layoutResId = context.getResources().getIdentifier("savechart_txt_"+String.valueOf(i+1), "id", context.getPackageName());
			TextView tvSavechart = (TextView)xmlUI.findViewById(layoutResId);
			tvSavechart.setTypeface(COMUtil.typefaceMid);
		}

//		layoutResId = context.getResources().getIdentifier("savedatetitle", "id", context.getPackageName());
//		TextView textTitle = (TextView) xmlUI.findViewById(layoutResId);
////		2012. 8. 1  글자색 xml에서 처리 
//		textTitle.setTextColor(Color.WHITE);
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

//		layoutResId = context.getResources().getIdentifier("savetitle", "id", context.getPackageName());
//		TextView saveTitle = (TextView) xmlUI.findViewById(layoutResId);
//		saveTitle.setText(getStateTitle());

//		btnFunc.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		//2013. 1. 25 저장하기 창 닫힐 때 저장하기 위해 함수 호출. 
//	        		saveChartToFile();
//	        		close();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });

		//2013. 1. 25 새로고침 버튼 리스너
//		btnRefresh.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		Toast.makeText(context, "새로고침", Toast.LENGTH_SHORT).show();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });

//		layoutResId = context.getResources().getIdentifier("savebutton", "id", context.getPackageName());
//		//2012. 7.26 차트저장화면 버튼 변경 (이미지 삭제하고 기본버튼으로)
//		Button saveBtn = (Button)xmlUI.findViewById(layoutResId);
//		saveBtn.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//        	//2012. 7. 23 차트저장  try-catch 주석처리 
////	        	try {
//	        		saveChartToFile();
////	        	}
////	        	catch (Exception e) {        	     
////	        		System.out.println("SaveChart Error msg:"+e.getMessage());
////	        	}
//	        }
//        });
		//2012. 7. 23 SNS 공유기능 영역 주석처리 - drfnkimsh
		//공유처리.
//		layoutResId = context.getResources().getIdentifier("public_checkbox", "id", context.getPackageName());
//		CheckBox allowBox = (CheckBox) xmlUI.findViewById(layoutResId);
//		allowBox.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		CheckBox allowBox = (CheckBox) v;
//	    			if(allowBox.isChecked()) {
//	    				COMUtil.showMessage(context, "SNS기능이 활성화 됩니다.");
//	    				setSNSEnable(true);
//	    			} else {
//	    				COMUtil.showMessage(context, "SNS기능이 비활성화 됩니다.");
//	    				setSNSEnable(false);	    			}
//	        	}
//	        	catch (Exception e) {        	     
//	        		System.out.println("facebook Error msg:"+e.getMessage());
//	        	}
//	        }
//        });
//		
//		//Facebook Checkbox 이벤트 설정.
//		layoutResId = context.getResources().getIdentifier("facebook_checkbox", "id", context.getPackageName());
//		fbBox = (CheckBox)xmlUI.findViewById(layoutResId);
//		  String text = prefs.getString("facebook_state", "");
//		if(!mFacebookAccessToken.equals("")) {
//			fbBox.setChecked(true);
//		}
//		fbBox.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		CheckBox allowBox = (CheckBox) v;
////	        		allowBox.setChecked(!allowBox.isChecked());
//	    			if(allowBox.isChecked()) {
//	    				COMUtil.showMessage(context, "페이스북 저장기능을 활성화합니다.");
////	    				allowBox.setChecked(false);
//	    				FacebookLogin();
//	    			} else {
//	    				COMUtil.showMessage(context, "페이스북 저장기능을 종료합니다.");
//	    				FacebookLogout();
//	    			}
//	        	}
//	        	catch (Exception e) {        	     
//	        		System.out.println("facebook Error msg:"+e.getMessage());
//	        	}
//	        }
//        });
		//2012. 7. 23 주석처리 끝 

		//Twitter Checkbox 이벤트 설정.
//		layoutResId = context.getResources().getIdentifier("twitter_checkbox", "id", context.getPackageName());
//		CheckBox twitterBox = (CheckBox) xmlUI.findViewById(layoutResId);
//		
//		text = prefs.getString("twitter_state", "");
//		if(text.equals("true")) {
//			twitterBox.setChecked(true);
//	    	String twitterID =prefs.getString("twitterID", "");
//	    	String twitterPW =prefs.getString("twitterPW", "");
//	    	if(!twitterID.equals("") && !twitterPW.equals("")) {
//		    	try {
//		    		accessToken = oauth.getOAuthAccessToken(twitterID, twitterPW);
//		    	} catch(Exception e) {
//		    		System.out.println(e.getMessage());
//		    	}
//	    	} else {
//	    		twitterBox.setChecked(false);
//	    	}
//	
//
//		}

//		twitterBox.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		CheckBox allowBox = (CheckBox) v;
//	    			if(allowBox.isChecked()) {
////	    				COMUtil.showMessage(context, "트위터 저장기능을 활성화합니다.");
//	    				showTwitterLoginView();
//	    			} else {
////	    				COMUtil.showMessage(context, "트위터 저장기능을 종료합니다.");
//	    				TwitterLogout();
//	    				
//	    			}
//	        	}
//	        	catch (Exception e) {        	     
//	        		System.out.println("트위터 Error msg:"+e.getMessage());
//	        	}
//	        }
//        });

		layoutResId = context.getResources().getIdentifier("memo_box", "id", context.getPackageName());
		memoBox = (EditText)xmlUI.findViewById(layoutResId);
		//2012. 8. 3 키보드 내리기 기능 추가
//		memoBox.setOnKeyListener(new OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                  // Perform action on key press
////                	Editable str = memoBox.getText();
//
//                  return true;
//                }
//                return false;
//            }
//          });

		//memoBox.setOnEditorActionListener(this);

		layoutResId = context.getResources().getIdentifier("chartsave_editname", "id", context.getPackageName());
		EditText edSaveTitle = (EditText)xmlUI.findViewById(layoutResId);

		edSaveTitle.setOnEditorActionListener(this);

		final EditText _memoBox = memoBox;
		final EditText _edSaveTitle = edSaveTitle;
		btnRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
//		    		showToastMessage("기존 입력된 내용이 초기화 됩니다");
					Toast.makeText(context, "기존 입력된 내용이 초기화 됩니다", Toast.LENGTH_LONG).show();
					_memoBox.setText("");
					_edSaveTitle.setText("");
				}
				catch (Exception e) {
				}
			}
		});

//		COMUtil._mainFrame.mainBase.baseP._chart.getRootView().setDrawingCacheEnabled(true); // 캡쳐 할 수 있도록 권한 설정하는 부분이고

		// 화면 캡쳐해서 비트맵으로 가져오는 부분.
//		View v1 = COMUtil._mainFrame.mainBase.baseP._chart.getRootView();
//        v1.setDrawingCacheEnabled(true);
//        Bitmap bm = v1.getDrawingCache();
//        if(bm!=null) {
//        	ImageView saveImage = (ImageView) xmlUI.findViewById(R.id.savechart_image);
//			saveImage.setImageBitmap(bm);
//        }

		//chart frame
//		RelativeLayout.LayoutParams chartParam = (RelativeLayout.LayoutParams)layout.getLayoutParams();

		/** 분할차트 저장 캡쳐 이미지 생성 (2011.09.23 by lyk) **/
		//2013. 1. 31  주석처리 
		Base baseP = COMUtil._mainFrame.mainBase.baseP;
		RelativeLayout v1 = baseP._chart.layout;
		v1.setDrawingCacheEnabled(true);
        Bitmap mainImg = v1.getDrawingCache(); //전체 화면이미지.
        mBack = Bitmap.createBitmap(baseP.frame.width()-10, baseP.frame.height(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(mBack);
		c.drawBitmap(mainImg, 0, 0, paint);

		//2012. 8. 13  차트저장할때 한번 캡쳐된 이미지가 고정됨, 저장화면이미지에 검은 여백 생기는 현상  :  SL33, SL34
//		v1.setDrawingCacheEnabled(false);
//		View v1 = COMUtil._neoChart.getRootView();

//        v1.setDrawingCacheEnabled(true);
//        Bitmap mainImg = v1.getDrawingCache(); //전체 화면이미지.
//        
//        ImageView mainView = new ImageView(this.getContext());
////        mainView.setImageBitmap(mainImg);
//        RelativeLayout.LayoutParams params;
//        params =new RelativeLayout.LayoutParams(
//        		layout.getWidth(), layout.getHeight());
//        
////        drawable = new BitmapDrawable(mainImg);
//        
//		LinearLayout captureLayout = new LinearLayout(this.getContext());
//		captureLayout.setLayoutParams(params);
////		captureLayout.setBackgroundDrawable(drawable);
//		captureLayout.setDrawingCacheEnabled(true);
////		captureLayout.addView(mainView);
//		Base baseP = COMUtil._mainFrame.mainBase.baseP;
//		mBack = Bitmap.createBitmap(baseP.frame.width()-10, baseP.frame.height(), Bitmap.Config.RGB_565);
//		Canvas c = new Canvas(mBack);
//		c.drawBitmap(mainImg, 0, 0, paint);
//		
//		synchronized(this) {
//
//			if(COMUtil.chartMode==COMUtil.BASIC_CHART) {
////				Vector chartList = COMUtil.divideChartList;
//				NeoChart2 pBaseChart = baseP._chart;
//				//pBaseChart.onResume();
//				pBaseChart._cvm.isCaptureMode = true;
////				pBaseChart._cvm.captureImg = null;
//				pBaseChart.repaintAll(); // captureImg 생성.
////				Bitmap srcimg=null;
////			    while(true){
////				    srcimg = pBaseChart._cvm.captureImg;
////				  	if(srcimg!=null) {
//				  		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)pBaseChart.getLayoutParams();
////				  		c.drawBitmap(srcimg, param.leftMargin, param.topMargin, paint);
//				  		
//				  		//ChartItemView 캡쳐이미지 추가.
//				  		RelativeLayout chartItemLayout = COMUtil._baseChart.chartItemLayout;
//				  		chartItemLayout.setDrawingCacheEnabled(true);
//				        Bitmap chartImg = chartItemLayout.getDrawingCache(); //chartItem image;
//				        c.drawBitmap(chartImg, param.leftMargin, param.topMargin, paint);
//				  		
////						break;
////				  	}  
////			    }
//			} else if(COMUtil.chartMode==COMUtil.DIVIDE_CHART) {
//				//확대모드이면 축소모드로 변경 후 차트 이미지를 저장한다.(저장 속도 개선필요)
//				COMUtil._mainFrame.mainBase.baseP.reduceChart(COMUtil._mainFrame.mainBase.baseP._chart);
//				Vector<BaseChart> chartList = COMUtil.divideChartList;
//				
//				for(int i=0; i<chartList.size(); i++) {
//					BaseChart pBaseChart = (BaseChart)chartList.get(i);
//					pBaseChart._cvm.isCaptureMode = true;
////					pBaseChart._cvm.captureImg = null;
//					pBaseChart.repaintAll(); // captureImg 생성.
////					Bitmap srcimg=null;
////				    while(true){
////					    srcimg = pBaseChart._cvm.captureImg;
////					  	if(srcimg!=null) {
//					  		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)pBaseChart.getLayoutParams();
////					  		c.drawBitmap(srcimg, param.leftMargin, param.topMargin, paint);
//					  		
//					  		//ChartItemView 캡쳐이미지 추가.
//					  		RelativeLayout chartItemLayout = pBaseChart.chartItemLayout;
//					  		chartItemLayout.setDrawingCacheEnabled(true);
//					        Bitmap chartImg = chartItemLayout.getDrawingCache(); //chartItem image;
//					        c.drawBitmap(chartImg, param.leftMargin, param.topMargin, paint);
//					        
//							break;
////					  	}  
////				    }
//				}
//			}
//		}

		//2013. 1. 31  주석처리 
		try {
			imgFilePath = COMUtil._chartMain.getFilesDir() + "//" + "saveimg.png";
			 FileOutputStream out = new FileOutputStream(imgFilePath);
			 mBack.compress(CompressFormat.PNG, 50, out);
		} catch (FileNotFoundException e) {
			Log.d("FileNotFoundException:", e.getMessage());
		}

		//2013. 1. 25  이미지 캡쳐 기능 주석처리 (사유 : 기획에서 제외 ) 
//		layoutResId = context.getResources().getIdentifier("savechart_image", "id", context.getPackageName());
		//2012. 8. 14  saveImage 변수  화면회전 관련 layoutparam 조절을 설정해야 해서(위쪽 savemain부분) 주석처리  : SL18
//		ImageView saveImage = (ImageView) xmlUI.findViewById(layoutResId);
//		saveImage.setImageBitmap(mBack);

		//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>
		//지표리스트 구하기 
		String strJipyoList = "";
		Vector<String> list = COMUtil._mainFrame.mainBase.baseP._chart.getGraphList();

		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용)
		//Vector<Hashtable<String, String>> v = COMUtil.getJipyoMenu();
		Vector<Hashtable<String, String>>  v = (Vector<Hashtable<String, String>>)COMUtil.getJipyoMenu().clone();
		Vector<Hashtable<String, String>> addItems = COMUtil.getAddJipyoList();
		for(int i=0; i<addItems.size(); i++) {
			v.add(addItems.get(i));
		}
		//2015. 1. 13 by lyk - 동일지표 추가 (추가리스트 적용) end

		int jCnt = v.size();
		int inx = 0;
		for(int i=inx; i<jCnt; i++) {
			Hashtable<String, String> item = (Hashtable<String, String>)v.get(i);
			int listLen = list.size();
			for(int k=0; k<listLen; k++) {
				String cmp = (String)item.get("name");
				if(cmp.equals((String)list.get(k))) {
					strJipyoList += cmp;
					strJipyoList += ", ";
					break;
				}
			}
		}
		//2014. 3. 21 차트 저장할 때 지표가 아무것도 없으면 죽는현상>>
		if(strJipyoList.length()>0)
		{
			strJipyoList = strJipyoList.substring(0, strJipyoList.length()-2);
		}
		//2014. 3. 21 차트 저장할 때 지표가 아무것도 없으면 죽는현상<<

		//주기값 구하기
		BaseChart pBaseChart = (BaseChart)COMUtil._neoChart;
		String ptype = pBaseChart._cdm.codeItem.strDataType;
		String unitType = pBaseChart._cdm.codeItem.strUnit;
		String period = COMUtil.getPeriod(ptype, unitType);

		//일자
		layoutResId = context.getResources().getIdentifier("detailinfo_date", "id", context.getPackageName());
		TextView detailinfo_date = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_date.setText(COMUtil.getSaveDate());
		//종목
		layoutResId = context.getResources().getIdentifier("detailinfo_jongmokname", "id", context.getPackageName());
		TextView detailinfo_jongmokname = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_jongmokname.setText(COMUtil.codeName + "(" + COMUtil.symbol + ")");
		//주기
		layoutResId = context.getResources().getIdentifier("detailinfo_period", "id", context.getPackageName());
		TextView detailinfo_period = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_period.setText(period);
		//지표
		layoutResId = context.getResources().getIdentifier("detailinfo_jipyo", "id", context.getPackageName());
		TextView detailinfo_jipyo = (TextView)xmlUI.findViewById(layoutResId);
		detailinfo_jipyo.setText(strJipyoList);
		//2013. 7. 31 저장하기창에 종목 상세내용(일시/종목/주기) 추가>>

		//2021.02.03 by HJW - 차트저장 종목저장 여부 추가 >>
		layoutResId = context.getResources().getIdentifier("chk_save_jongmok", "id", context.getPackageName());
		final CheckBox chkSaveJongmok = (CheckBox)xmlUI.findViewById(layoutResId);

		layoutResId = context.getResources().getIdentifier("tv_save_jongmok", "id", context.getPackageName());
		TextView tvSaveJongmok = (TextView)xmlUI.findViewById(layoutResId);
		tvSaveJongmok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chkSaveJongmok.performClick();
			}
		});
		//2021.02.03 by HJW - 차트저장 종목저장 여부 추가 <<

//		layoutResId = context.getResources().getIdentifier("savechart_image", "id", context.getPackageName());
//		saveImage = (ImageView) xmlUI.findViewById(layoutResId);

		this.layout.addView(xmlUI);

//		COMUtil.setGlobalFont_Except_EditText(xmlUI);
		COMUtil.setGlobalFont(xmlUI);
	}

	private void showToastMessage(String str) {
		Hashtable<String, Object> dic = new Hashtable<String, Object>();
		dic.put("message", str);

		if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_MESSAGE, dic);
	}
	//2012. 7. 23 차트저장화면 가로세로 전환을 위해 xmlUI getter 설정 
	public View getXmlUI()
	{
		return xmlUI;
	}
	private void setSNSEnable(boolean state) {
		int layoutResId = context.getResources().getIdentifier("facebook_checkbox", "id", context.getPackageName());
		CheckBox allowBox1 = (CheckBox) xmlUI.findViewById(layoutResId);
		allowBox1.setEnabled(state);
		layoutResId = context.getResources().getIdentifier("twitter_checkbox", "id", context.getPackageName());
		CheckBox allowBox2 = (CheckBox) xmlUI.findViewById(layoutResId);
		allowBox2.setEnabled(state);
		layoutResId = context.getResources().getIdentifier("kakao_checkbox", "id", context.getPackageName());
		CheckBox allowBox3 = (CheckBox) xmlUI.findViewById(layoutResId);
		allowBox3.setEnabled(state);
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
//			((ViewGroup) view).removeAllViews();
			((ViewGroup) view).removeAllViewsInLayout();
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

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
//		int btnID = view.getId();
//		switch(btnID) {
//			case R.id.frameaBtnFunction:
//
//		}
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		//implement here for click item.

//		Toast.makeText(SaveChartController.this, "지표상세 설정화면이동",
//                Toast.LENGTH_LONG).show();
//        
//        Intent InIntent = new Intent(this, DetailJipyoController.class);
//        InIntent.putExtra("listIndex", position);
//
//        startActivityForResult(InIntent, LAUNCHED_ACTIVITY_JipyoSetup);
	}

	private String PATH = null;
	private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath)
	{
		File fileCacheItem = new File(strFilePath);
        if(!fileCacheItem.exists()) {
            String strDstFolder = fileCacheItem.getParent();
            if(strDstFolder != null)
            {
                File f = new File(strDstFolder);
                f.mkdirs();
            }
        }
		OutputStream out = null;

		try
		{
			fileCacheItem.createNewFile();
			out = new FileOutputStream(fileCacheItem);

			bitmap.compress(CompressFormat.JPEG, 20, out);
		}
		catch (Exception e)
		{
			COMUtil.showMessage(context, e.getMessage());
			e.printStackTrace();
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
	}
//	private void onDelete(String mes) {
//		 
//		File file = new File(PATH);
//		File[] fList = file.listFiles();
//		int count = 0;
// 
//		for (int i = 0; i < fList.length; i++) {
// 
//			String fileName = fList[i].getName();
// 
//			if (fileName.equals(mes)) {
// 
//				count++;
//				fList[i].delete();
//				String okmes = "지우기 성공";
//				COMUtil.showMessage(context, okmes);
//			}
//		}
//		if (count < 1) {
//			String errormes = "잘못된 파일 이름 입니다.";
//			COMUtil.showMessage(context, errormes);
//		}
//	}// Delete

	SQLiteDatabase db=null;
	Cursor cursor = null;
	ChartsaveDBHelper mHelper=null;
	private void saveLocal() {
//		ByteArrayOutputStream stream=null;
//		byte[] imgdata = null;
//		if(COMUtil.captureImg!=null) {
//			Bitmap image = COMUtil.captureImg;
//			stream = new ByteArrayOutputStream();
//	        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//	        imgdata = stream.toByteArray();
//	        
//		}

		Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;
		int chartMode=0;
		if(base.chartList.size()>1) {
			chartMode = COMUtil.DIVIDE_CHART;
		} else {
			chartMode = COMUtil.BASIC_CHART;
		}

		if(base.chartList.size()>0) {
			NeoChart2 chart = base.chartList.get(0);
			String strCode = chart._cdm.codeItem.strCode;
//			strCode = "003300";
			if(strCode==null || strCode.trim().equals("")) {
				//이 종목은 저장할 수 없습니다.
				COMUtil.showMessage(COMUtil._chartMain, "해당 차트는 저장할 수 없습니다.");
				return;
			}
		}

		//로컬저장.
		String strLocalTable = COMUtil._mainFrame.strLocalFileName;

//		System.out.println("DEBUG_saveLocal_tableName:"+strLocalTable);

		if(mHelper==null) {
			mHelper = new ChartsaveDBHelper(context, strLocalTable);
		}
		try {
			db = mHelper.getReadableDatabase();
		} catch(Exception e) {
			mHelper.close();
			db.close();
		}
//		int maxCnt = 10; //최대 maxCnt만큼 처리.

//		cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id asc", null); //디바이스 저장 공간. 최대 maxCnt.
//		if(cursor.getCount()>maxCnt) {
//			if(cursor.moveToFirst()) {
//				String _id = cursor.getString(0);//_id
//				db.execSQL("DELETE FROM chartsavedata where _id="+"'"+_id+"'");				
//			}
//		}

//		db = mHelper.getReadableDatabase();
//		int maxCnt = 10; //최대 maxCnt만큼 처리.
//		cursor = db.rawQuery("SELECT * FROM chartsavedata", null); //디바이스 저장 공간. 최대 maxCnt.
//		if(cursor.getCount()>=maxCnt) {
////			if(cursor.moveToFirst()) {
////				String _id = cursor.getString(0);//_id
////				db.execSQL("DELETE FROM chartsavedata where _id="+"'"+_id+"'");
////			}
//
//			//2016. 4. 4 차트 저장 10개까지만 되기>>
//			DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
//
//			alert.setTitle("");
//			alert.setMessage("차트 저장은 최대 10개까지 가능합니다.\n차트 불러오기에서 저장된 차트를 삭제 후 저장해 주시기 바랍니다.");
//			alert.setYesButton("확인",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog,int which) {
//							dialog.dismiss();
//						}
//					});
//			alert.show();
//
//			return;
//			//2016. 4. 4 차트 저장 10개까지만 되기<<
//		}

		//추세선 정보 저장 : 사용자 정의 저장은 분,틱 타입에선 저장하지 않는다.
		COMUtil.isSaveLocalMode = true;
		String strAnalInfo = "";
//		if(!COMUtil.dataTypeName.equals("0") && !COMUtil.dataTypeName.equals("1")) {  //2014. 4. 7 분/틱으로 차트 저장했을 때 추세선 불러오지 않는 현상
		strAnalInfo = COMUtil.getAnalInfoStr();
//		}
		COMUtil.isSaveLocalMode = false;

		//저장 타이틀 중복 체크
		cursor = db.rawQuery("SELECT saveTitleName FROM "+strLocalTable+" where saveTitleName='"+COMUtil.saveTitle+"'", null);
		if(cursor.getCount()>0) {
			//
//			COMUtil.showMessage(COMUtil._chartMain, "저장명이 중복되었습니다.");

//			AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this.getContext());
//			alert_confirm.setMessage("저장명이 중복되었습니다.").setCancelable(false).setPositiveButton("확인",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							db.close();
//							cursor.close();
//							mHelper.close();
//							// 'YES'
//							return;
//						}
//					});
//			alert_confirm.create();
//			alert_confirm.show();

            DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());

            alert.setTitle("");
            alert.setMessage("저장명이 중복되었습니다.\n덮어씌우시겠습니까?");
            alert.setMessageGravity("center");
            alert.setNoButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            db.close();
                            cursor.close();
                            mHelper.close();
                            dialog.dismiss();
							return;
                        }
                    });
			alert.setYesButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int which) {
//							db.close();인
//							cursor.close();
//							mHelper.close();
							dialog.dismiss();
							setCoverCopy();
						}
					});
            alert.show();
			COMUtil.g_chartDialog = alert;
			return;
		}

        int maxCnt = 10; //최대 maxCnt만큼 처리.
        cursor = db.rawQuery("SELECT * FROM "+strLocalTable, null); //디바이스 저장 공간. 최대 maxCnt.
        if(cursor.getCount()>=maxCnt) {
//			if(cursor.moveToFirst()) {
//				String _id = cursor.getString(0);//_id
//				db.execSQL("DELETE FROM chartsavedata where _id="+"'"+_id+"'");
//			}

            //2016. 4. 4 차트 저장 10개까지만 되기>>
            DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());

            alert.setTitle("");
            alert.setMessage("차트 저장은 최대 10개까지 가능합니다. 기존 저장된 차트 삭제 후 추가 저장 가능합니다.");
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

            return;
            //2016. 4. 4 차트 저장 10개까지만 되기<<
        }
        if(COMUtil.strAppPath.length()>0)
        {
            PATH = COMUtil.strAppPath+"user/chart/";
        }
        else {
            //파일로 저장한다.
            ActivityManager am = (ActivityManager) COMUtil._chartMain.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> info = am.getRunningTasks(1);
            ComponentName topActivity = info.get(0).topActivity;
            String name = topActivity.getPackageName();
//	   System.out.println("==================packetName:"+name);
            PATH = "/data/data/" + name + "/files/";
        }

        //2013. 1. 28 저장하기창에서
        String fileName = PATH+COMUtil.saveTitle+".jpg";
        SaveBitmapToFileCache(mBack, fileName);

//		System.out.println("strAnalInfo:"+strAnalInfo);

		//2015. 3. 4 차트 테마 메인따라가기 추가>>
		int nSaveSkinType = COMUtil.SKIN_AUTO_THEME;
		if(!COMUtil.bIsAutoTheme) {
			nSaveSkinType = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getSkinType();
		}
		//2015. 3. 4 차트 테마 메인따라가기 추가<<

		//2015.05.26 by lyk - 주기별 차트 설정 (주기별 설정이 아닌 경우 처리)
		String sGraphList = COMUtil.getGraphListStr();
		if(cursor.getCount()>0) {
			cursor.moveToFirst();
			if(COMUtil.isPeriodConfigSave()) {
				sGraphList = cursor.getString(17);
			}
		}
		int data9 = COMUtil.isPeriodConfigSave()?1:0;	//2015.04.16 by lyk - 주기별 차트 설정
		//2015.05.26 by lyk - 주기별 차트 설정 (주기별 설정이 아닌 경우 처리) end

			db.execSQL("INSERT INTO " + strLocalTable + " VALUES (null, " +
					" '" + COMUtil.getDivideInfo() + "'," +
					" '" + data9 + COMUtil.strPeriodConfigTableGubun + COMUtil.getPeriodSaveGraphListStr() + "'," + //(2)//2015.04.08 by lyk - 주기별 차트 설정
					" '" + COMUtil.getSaveDate() + "'," +
					" '" + COMUtil.getChartSizeInfo() + "'," +    //" '"+COMUtil.userId+"'," +  //2016.12.22 by LYH >> 차트 블럭 리사이즈 저장
					" '" + COMUtil.getAddJipyoListStr() + "'," + //2015. 1. 13 by lyk 중복지표 추가/삭제 처리 (//				" '"+COMUtil.userIp+"'," +)
					" '" + COMUtil.codeName + "'," +
					" '" + COMUtil.title + "'," +
					" '" + COMUtil.detail + "'," +
					" '" + fileName + "'," +
					" '" + COMUtil.getSymbolStr() + "'," +
					" '" + COMUtil.getLCodeStr() + "'," +
					" '" + COMUtil.getApCodeStr() + "'," +
					" '" + COMUtil.getDataTypeName() + "'," +
					" '" + COMUtil.getCountStr() + "'," +//count
					" '" + COMUtil.getViewCountStr() + "'," +//viewcount
					" '" + COMUtil.getUnitStr() + "'," +  //valufOfMin
					" '" + COMUtil.getGraphListStr() + "'," + //graphlist
					" '" + "" + "'," +
					" '" + strAnalInfo + "'," + //analinfo
//				" '"+chartMode+"/"+COMUtil._mainFrame.mainBase.baseP._chart._cvm.nSkinType+"'," + //chartMode/skinType
					" '" + chartMode + "/" + nSaveSkinType + "'," + //chartMode/skinType	 //2015. 3. 4 차트 테마 메인따라가기 추가
					" '" + COMUtil._mainFrame.mainBase.baseP.nMarketType + "'," + //marketType
					" '" + COMUtil.getBunPeriodListStr() + "'," + //분 주기 데이터
					" '" + COMUtil.getTicPeriodListStr() + "'," + //틱 주기 데이터
					" '" + COMUtil.getMarketNameStr() + "'," + //marketName(stock,elw. etc)
					" '" + COMUtil.saveTitle + "'," +  //2013. 1. 28 사용자입력 저장하기 타이틀
					//" '"+COMUtil.getBarTypeStr()+"'" +	//2015.04.30 by lyk - 바(시고저종) 유형 추가
					" '" + "" + "'," + //listNo(hts)설정안함 																 26
					" '" + COMUtil.getBaseLineStr() + "#" + COMUtil.getAutoWaveLineStr() + "'," + //baseline    		 27 //2019. 08. 21 by hyh - 자동추세선 복원
					" '" + COMUtil.getCommonInfoStr() + "'," +    //commonInfo.										     28
					" '" + COMUtil.getFxMarginTypes() + "'," +    //fxMarginTypes.										 29
					" '" + COMUtil.getConnTypes() + "'," +        //connTypes.											 30
					" '" + COMUtil.getDayTypes() + "'," +         //dayTypes.											 31
					" '" + COMUtil.getFloorTypes() + "'," +        //floorTypes.
					" '" + COMUtil.isSaveJongmok + "'" +        //isSavedJongmok.// 	//2021.02.03 by HJW - 차트저장 종목저장 여부 추가									 32
					");");

		cursor.close();
		mHelper.close();

		DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());

		alert.setTitle("");
		String saveTitle = COMUtil.saveTitle;
		alert.setMessage("["+saveTitle+"]"+" 차트 저장이 완료되었습니다");
		alert.setOkButton("확인",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {

						dialog.dismiss();
					}
				});
		alert.show();

		//if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);

		//2015. 1. 13 by lyk - 동일지표 목록별 저장 
		COMUtil.saveAddJipyoList(COMUtil._mainFrame.strFileName+COMUtil.saveTitle);
		//2015. 1. 13 by lyk - 동일지표 목록별 저장 end

		this.close();
		COMUtil.closeSaveChartPopup();
	}

	public void setCoverCopy()
	{
		String strLocalTable = COMUtil._mainFrame.strLocalFileName;

		if(mHelper==null) {
			mHelper = new ChartsaveDBHelper(context, strLocalTable);
		}
		db = mHelper.getReadableDatabase();

		cursor = db.rawQuery("SELECT _id FROM "+strLocalTable+" where saveTitleName='"+COMUtil.saveTitle+"'", null);

		if(cursor.getCount()>0)
		{

			cursor.moveToFirst();
			String strID = cursor.getString(0);
			int nSaveSkinType = COMUtil.SKIN_AUTO_THEME;
			if(!COMUtil.bIsAutoTheme) {
				nSaveSkinType = COMUtil._mainFrame.mainBase.baseP._chart._cvm.getSkinType();
			}

			if(COMUtil.strAppPath.length()>0)
			{
				PATH = COMUtil.strAppPath+"user/chart/";
			}
			else {
				//파일로 저장한다.
				ActivityManager am = (ActivityManager) COMUtil._chartMain.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> info = am.getRunningTasks(1);
				ComponentName topActivity = info.get(0).topActivity;
				String name = topActivity.getPackageName();
//	   System.out.println("==================packetName:"+name);
				PATH = "/data/data/" + name + "/files/";
			}

			String fileName = PATH+COMUtil.saveTitle+".jpg";

			int data9 = COMUtil.isPeriodConfigSave()?1:0;	//2015.04.16 by lyk - 주기별 차트 설정

			Base11 base = (Base11)COMUtil._mainFrame.mainBase.baseP;
			int chartMode=0;
			if(base.chartList.size()>1) {
				chartMode = COMUtil.DIVIDE_CHART;
			} else {
				chartMode = COMUtil.BASIC_CHART;
			}


			db.execSQL("UPDATE "+strLocalTable+" SET " +
					"divideinfo=" + " '"+COMUtil.getDivideInfo()+"' " +
					", verinfo=" + " '"+data9 + COMUtil.strPeriodConfigTableGubun + COMUtil.getPeriodSaveGraphListStr()+"' " +
					", savedate=" + " '"+COMUtil.getSaveDate()+"' " +
					", userid=" + " '"+COMUtil.getChartSizeInfo()+"' " +
					", userip=" + " '"+COMUtil.getAddJipyoListStr()+"' " +
					", codename=" + " '"+COMUtil.codeName+"' " +
					", title =" + " '"+COMUtil.title+"' " +
					", detail=" + " '"+COMUtil.detail+"' " +
					", fileName=" + " '"+fileName+"' " +
					", symbol=" + " '"+COMUtil.getSymbolStr()+"' " +
					", lcode=" + " '"+COMUtil.getLCodeStr()+"' " +
					", apCode=" + " '"+COMUtil.getApCodeStr()+"' " +
					", dataTypeName=" + " '"+COMUtil.getDataTypeName()+"' " +
					", count=" + " '"+COMUtil.getCountStr()+"' " +
					", viewCount=" + " '"+COMUtil.getViewCountStr()+"' " +
					", valueOfMin=" + " '"+COMUtil.getUnitStr()+"' " +
					", jipyodata=" + " '"+COMUtil.getGraphListStr()+"' " +
					", chartItem=" + " '"+"' " +
					", analInfo=" + " '"+COMUtil.getAnalInfoStr()+"' " +
					", chartMode=" + " '"+chartMode + "/" + nSaveSkinType +"' " +
					", market=" + " '"+COMUtil._mainFrame.mainBase.baseP.nMarketType+"' " +
					", bunPeriodList=" + " '"+COMUtil.getBunPeriodListStr()+"' " +
					", ticPeriodList=" + " '"+COMUtil.getTicPeriodListStr()+"' " +
					", marketName=" + " '"+COMUtil.getMarketNameStr()+"' " +
					", saveTitleName=" + " '"+COMUtil.saveTitle+"' " +
					", listNo=" + " '"+"' " +
					", baseline=" + " '"+COMUtil.getBaseLineStr() + "#" + COMUtil.getAutoWaveLineStr()+"' " +
					", commonInfo=" + " '"+COMUtil.getCommonInfoStr()+"' " +
					", fxMarginTypes=" + " '"+COMUtil.getFxMarginTypes()+"' " +
					", connTypes=" + " '"+COMUtil.getConnTypes()+"' " +
					", dayTypes=" + " '"+COMUtil.getDayTypes()+"' " +
					", floorTypes=" + " '"+COMUtil.getFloorTypes()+"' " +
					", isSavedJongmok=" + " '"+COMUtil.isSaveJongmok+"' " +
					"where _id=" + " '" + strID + "'");
		}

		cursor.close();
		mHelper.close();

		DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());

		alert.setTitle("");
		String saveTitle = COMUtil.saveTitle;
		alert.setMessage("["+saveTitle+"]"+" 차트 저장이 완료되었습니다");
		alert.setOkButton("확인",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {

						dialog.dismiss();
					}
				});
		alert.show();

		//if(COMUtil._mainFrame.userProtocol!=null) COMUtil._mainFrame.userProtocol.requestInfo(COMUtil._TAG_SAVE_LOCAL_CLOUD, null);

		//2015. 1. 13 by lyk - 동일지표 목록별 저장
		COMUtil.saveAddJipyoList(COMUtil._mainFrame.strFileName+COMUtil.saveTitle);
		//2015. 1. 13 by lyk - 동일지표 목록별 저장 end

		this.close();
		COMUtil.closeSaveChartPopup();
	}


	private void savePublic() {
		uploadImage();
	}

	/* 원격 저장 */
	private void uploadImage() {
		String urlString="";
		int layoutResId = context.getResources().getIdentifier("public_checkbox", "id", context.getPackageName());
		CheckBox allowBox = (CheckBox) xmlUI.findViewById(layoutResId);
		if(allowBox.isChecked())	allowProMode = true;
		else allowProMode = false;
		allowProMode=false; //test mode
		if (allowProMode) {
			urlString = "http://218.38.18.171/smartPhone/uploadPro.php";
		} else {
			urlString = "http://218.38.18.171/smartPhone/upload.php";
		}
		COMUtil.showMessage(context, "공유차트 저장을 시작합니다.");
		HttpFileUpload(urlString, "", imgFilePath);
	}

	private void HttpFileUpload(String urlString, String params, String fileName) {
		try {
			mFileInputStream = new FileInputStream(fileName);
			connectUrl = new URL(urlString);

			//open connection
			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			//write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"userfile\"; filename=\"" +
					"ipodfile.jpg" + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			//read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);

			//param roof
			Vector<String> paramNames=new Vector<String>();
			Vector<String> paramValues=new Vector<String>();

			paramNames.add("saveDate");
			paramValues.add(COMUtil.getSaveDate());

			paramNames.add("userId");
			paramValues.add(COMUtil.userId);

			paramNames.add("userIp");
			paramValues.add(COMUtil.userIp);

			paramNames.add("verInfo");
			paramValues.add(COMUtil.verInfo);

			paramNames.add("title");
			paramValues.add(COMUtil.title);

			paramNames.add("detail");
			paramValues.add(COMUtil.detail);

			//추가정보입력(chartMode) : 2011.09.08 by lyk
			String strChartMode = ""+COMUtil.chartMode+"/"+COMUtil.getSkinType();
			paramNames.add("chartMode");
			paramValues.add(strChartMode);

			//분할차트인 경우 추가정보를 구성한다. : 2011.09.05 by lyk
			// cnt / divideType / 종목동기화 / 기간동기화
			paramNames.add("divideInfo");
			paramValues.add(COMUtil.getDivideInfo());
			//apCode(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("apCode");
			paramValues.add(COMUtil.getApCodeStr());

			//graphList(item1-item2-item3 ...) (분할차트추가) 2011.09.09 by lyk
			paramNames.add("graphList");
			paramValues.add(COMUtil.getGraphListStr());

			//deviceID추가. 2011.09.30 by lyk
			paramNames.add("deviceID");
			paramValues.add(COMUtil.deviceID);

			//analInfoList(item1-item2-item3 ...) (분할차트추가) 2011.09.09 by lyk
			paramNames.add("analInfo");
			paramValues.add(COMUtil.getAnalInfoStr());

			paramNames.add("codeName");
			paramValues.add(COMUtil.codeName);

			//symbol(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("symbol");
			paramValues.add(COMUtil.getSymbolStr());

			//lcode(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("lcode");
			paramValues.add(COMUtil.getLCodeStr());

			//DataTypeName(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("dataTypeName");
			paramValues.add(COMUtil.getDataTypeName());

			//Count(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("count");
			paramValues.add(COMUtil.getCountStr());

			//viewCount(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("viewCount");
			paramValues.add(COMUtil.getViewCountStr());

			//Unit(ValueOfMin)(분할차트추가) : 2011.09.09 by lyk
			paramNames.add("valueOfMin");
			paramValues.add(COMUtil.getUnitStr());//COMUtil.unit

			int paramLen = paramNames.size();
			for(int i =0; i<paramLen;i++){
				dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
				dos.writeBytes("Content-Disposition: form-data; name=\""+paramNames.get(i)+"\""+ lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes(""+paramValues.get(i));
				dos.writeBytes(lineEnd);
			}

			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			//close streams
			mFileInputStream.close();
			dos.flush();

			//get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			while((ch=is.read()) != -1) {
				b.append((char)ch);
			}
			String s=b.toString();
			String[] ss = s.split(":");
			String filename="";
			if(ss.length>1) {
				filename = ss[1];
			}
			this.imgUrl = COMUtil.imgPath+filename+".jpg";
			//Log.e("Test", "result = "+s);
			dos.close();

			//Facebook으로 저장.
			int layoutResId = context.getResources().getIdentifier("facebook_checkbox", "id", context.getPackageName());
			CheckBox allowBox = (CheckBox) xmlUI.findViewById(layoutResId);
			if(allowBox.isEnabled() && allowBox.isChecked()) {
				COMUtil.showMessage(context, "페이스북에 차트를 저장하고 있습니다.");
				FacebookMsgAndPic();
			}
			//Kakao으로 저장.
			layoutResId = context.getResources().getIdentifier("kakao_checkbox", "id", context.getPackageName());
			CheckBox kakaoBox = (CheckBox) xmlUI.findViewById(layoutResId);
			if(kakaoBox.isEnabled() && kakaoBox.isChecked()) {
				COMUtil.showMessage(context, "카카오톡을 실행하고 있습니다.");
				sendKakao(imgUrl);
			}

		} catch (Exception e) {
			//Log.d("Test", "exception " +e.getMessage());
		}
	}
	String imgUrl="";

	private void sendKakao(String url) {
		String message = COMUtil.detail+COMUtil.title;
		String referenceURLString = url;

		KakaoLink kakaoLink = null;

		try {
			kakaoLink = new KakaoLink(this.context, referenceURLString, COMUtil.appPackageId, COMUtil.appVersion, message, "UTF-8");
		} catch (Exception e) {

		}

		if (kakaoLink!=null && kakaoLink.isAvailable()) {
			COMUtil._chartMain.startActivity(kakaoLink.getIntent());
		} else {
			// 카카오톡이 설치되어 있지 않은 경우에 대한 처리
			COMUtil.showMessage(this.context, "카카오톡이 설치되어있지 않습니다.");
		}
	}

	/* 로컬 저장 */
	public void saveChartToFile() {
		//2012. 7. 23  차트저장을 apiMode 인지에 따라 구분처리. 
//		if(!COMUtil.apiMode)
//		{
//			if(COMUtil.chartMode == COMUtil.COMPARE_CHART) {
//				COMUtil.showMessage(context, "비교차트는 저장할 수 없습니다.");
//				return;
//			}
//			COMUtil.showMessage(context, "차트 저장을 시작합니다.");
//			COMUtil.title = getStateTitle();
//			COMUtil.detail = memoBox.getText().toString();
//			COMUtil.saveDate = COMUtil.getSaveDate();
//			
//			int layoutResId = context.getResources().getIdentifier("public_checkbox", "id", context.getPackageName());
//			CheckBox allowBox = (CheckBox) xmlUI.findViewById(layoutResId);
//			if(allowBox.isChecked())	allowOthers = true;
//			else allowOthers = false;
//			//allowProMode는 실전차트용인데 현재는 사용하지 않음.
//			if(!allowProMode) {//실전차트 모드는 로컬에 저장하지 않는다.
//				// 로컬엔 기본적으로 저장한다.
//				saveLocal();
//				if(allowOthers) {
//					savePublic();
//				}
//			} else {
//				savePublic(); //원격저장.
//			}
//			
//			//트위터 사용체크하여 메시지 보냄.
//			layoutResId = context.getResources().getIdentifier("twitter_checkbox", "id", context.getPackageName());
//			CheckBox twitterBox = (CheckBox) xmlUI.findViewById(layoutResId);
//			if(twitterBox.isEnabled() && twitterBox.isChecked()) {
//				COMUtil.showMessage(context, "트위터에 메시지 전송을 시작합니다.");
//				this.sendTwitterAndPic(memoBox.getText().toString());
//			}
//	    	
//			String textTochange = "차트가 저장되었습니다.";
//	    	COMUtil.showMessage(context, textTochange); //Context, String msg
//	    	
//	    	
//		}
//		else
//		{

		//2013. 1. 28 차트 저장 타이틀 추가 
		int layoutResId = context.getResources().getIdentifier("chartsave_editname", "id", context.getPackageName());
		EditText edSaveTitle = (EditText)xmlUI.findViewById(layoutResId);

		//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 start
		DRAlertDialog alert = new DRAlertDialog(COMUtil.apiView.getContext());
		String saveTitle = (String)edSaveTitle.getText().toString();
		if(saveTitle==null || saveTitle.equals("")) {
			//2014. 3. 25 차트 저장하기창 제목입력한게 없을 때는 저장버튼 눌러도 창을 닫지 않고 메시지 표시>>
//			this.close();
//			COMUtil.closeSaveChartPopup();

//			Toast.makeText(context, "저장명을 입력하셔야 합니다.", Toast.LENGTH_SHORT).show();
			alert.setMessage("저장명을 입력하셔야 합니다.");
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
			//2020.05.14 by JJH >> 알림 설정 팝업 UI 작업 end

			//2014. 3. 25 차트 저장하기창 제목입력한게 없을 때는 저장버튼 눌러도 창을 닫지 않고 메시지 표시<<
			return;
		}

		COMUtil.saveTitle = saveTitle;


//			COMUtil.showMessage(context, "차트가 저장되었습니다.");
		COMUtil.title = getStateTitle();
		//COMUtil.detail = memoBox.getText().toString();

        layoutResId = context.getResources().getIdentifier("detailinfo_jipyo", "id", context.getPackageName());
        TextView detailinfo_jipyo = (TextView)xmlUI.findViewById(layoutResId);
        COMUtil.detail = detailinfo_jipyo.getText().toString();

		COMUtil.saveDate = COMUtil.getSaveDate();

		//2021.02.03 by HJW - 차트저장 종목저장 여부 추가 >>
		layoutResId = context.getResources().getIdentifier("chk_save_jongmok", "id", context.getPackageName());
		CheckBox chkSaveJongmok = (CheckBox)xmlUI.findViewById(layoutResId);

		COMUtil.isSaveJongmok = chkSaveJongmok.isChecked();
		//2021.02.03 by HJW - 차트저장 종목저장 여부 추가 <<

		saveLocal();

		//2019.04.15 원터치 차트설정불러오기 추가 - lyj
		Base11 base11 = (Base11)COMUtil._mainFrame.mainBase.baseP;
		base11.resetOneTouchList();
		//2019.04.15 원터치 차트설정불러오기 추가 - lyj end
//		}

	}


	//Facebook 관련함수처리.
	private String mFacebookAccessToken;
	private void FacebookLogin(){
		if (!"".equals(mFacebookAccessToken) && mFacebookAccessToken != null) {
			COMUtil.mFacebook.setAccessToken(mFacebookAccessToken);
		} else {
			COMUtil.mFacebook.authorize2(COMUtil._chartMain, new String[] {"publish_stream, user_photos, read_stream, offline_access"}, new AuthorizeListener());
		}
	}
	//	private void FacebookFeed()
//	  {
//	    try
//	    {
////	      Log.v(C.LOG_TAG, "access token : " + mFacebook.getAccessToken());
//	      
//	      Bundle params = new Bundle();
//	      params.putString("message", memoBox.getText().toString());
//	      params.putString("name", "사용자명");
//	      params.putString("link", this.imgUrl);
//	      params.putString("description", "FacebookCon을통해 포스트됨.");
//	      params.putString("picture", this.imgUrl);
//
//	      COMUtil.mFacebook.request("me/feed", params, "POST");
//	    }
//	    catch (Exception e)
//	    {
//	      e.printStackTrace();
//	    }
//	}
	public void FacebookMsgAndPic() {

		Bundle params = new Bundle();
		params.putString("message", COMUtil.detail);
		params.putString("name", COMUtil.title);
		params.putString("caption", "차트를 클릭하면 큰 이미지를 볼 수 있습니다.");
		params.putString("description", "차트데이터 제공 : (주)DRFN");
		params.putString("link", this.imgUrl);
		params.putString("picture", this.imgUrl);

//		this.mFacebook.request("me/feed", params, "POST");
		mAsyncRunner.request("me/feed", params, "POST", new RequestListener() {
			public void onMalformedURLException(MalformedURLException e,
												Object state) {}
			public void onIOException(IOException e, Object state) {}
			public void onFileNotFoundException(FileNotFoundException e,
												Object state) {}
			public void onFacebookError(FacebookError e, Object state) {}
			public void onComplete(String response, Object state) {

			}
		}, null);
	}
	private void FacebookLogout()
	{
		try
		{
			COMUtil.mFacebook.logout(COMUtil._chartMain);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("FACEBOOK_ACCESS_TOKEN", "");
			editor.commit();

			mFacebookAccessToken="";//토큰 초기화 (2011.09.27 by lyk)
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendTwitterAndPic(String msg) {
//	    try {
//
//
//	       //TwitPic에 이미지를 올리고, 이미지 URL을 받아온다.
//		    String twitpicImgUrl = sendTwitpic();
//		   
//	        Twitter twitter = factory.getInstance(accessToken);
//	        twitter.setOAuthAccessToken(accessToken);
//	        String uploadMsg = msg + "-" + COMUtil.getStateTitle() + twitpicImgUrl;
//	        twitter.updateStatus(uploadMsg); // twitter 인증 테스트       
//
//	    }
//	    catch (TwitterException e) {
//	            COMUtil.showMessage(context, "트위터 저장시 문제가 발생하였습니다."+e.getMessage());
//	    }  
	}

	public String sendTwitpic() {
//		File image = new File(imgFilePath);
//		  ConfigurationBuilder cb = new ConfigurationBuilder();
//		  cb.setOAuthConsumerKey(COMUtil.consumer_key);              // twitter에 어플 등록 후 받은 consumer key
//		  cb.setOAuthConsumerSecret(COMUtil.consumer_secret);      // twitter에 어플 등록 후 받은 consumer secret
//	        
//		  cb.setOAuthAccessToken(accessToken.getToken());                              // tok 은 AccessToken 에서 getToken() 을 이용해 빼낸 String 입니다;
//		  cb.setOAuthAccessTokenSecret(accessToken.getTokenSecret());          // secTok 는 AccessToken 에서 getTokenSecret() 을 이용해 빼낸 String 입니다.;
//		  cb.setMediaProvider(MediaProvider.TWITPIC.toString());
//		  cb.setMediaProviderAPIKey(COMUtil.TWITPIC_API_KEY);                                      // 트윗픽에서 자신의 앱을 등록하고 받은 api key 입니다.
//		  
//		  Configuration config2 = cb.build();
//		  ImageUpload iu = new ImageUploadFactory(config2).getInstance();

		String twitpicImgUrl="";
//		  try {
//		     twitpicImgUrl = iu.upload(image);
//		  } catch (TwitterException e) {
//			  System.out.println("sendTwitpic:"+e.getMessage());
//		  }

		return twitpicImgUrl;
	}

//	private boolean isTwitterLogin() {
//    	String twitterID =prefs.getString("twitterID", "");
//    	String twitterPW =prefs.getString("twitterPW", "");
//    	
//    	if(!twitterID.equals("") && !twitterPW.equals("")) {
//    		return true;
//    	}
//    	
//    	return false;
//	}

	private void showTwitterLoginView() {
//		//로그인 창을 띄운다.
//		LayoutInflater factory = LayoutInflater.from(context);
//		int layoutResId = context.getResources().getIdentifier("twitterloginview", "layout", context.getPackageName());
//		xmlTwitterLoginUI = factory.inflate(layoutResId, null);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(COMUtil.chartWidth, COMUtil.chartHeight);
//		xmlTwitterLoginUI.setLayoutParams(params);
//
//		layoutResId = context.getResources().getIdentifier("button1", "id", context.getPackageName());
//		Button loginBtn = (Button) xmlTwitterLoginUI.findViewById(layoutResId);
//		loginBtn.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		twitterLogin();
//	        	}
//	        	catch (Exception e) {        	     
//	        	}
//	        }
//        });
//		
//		layoutResId = context.getResources().getIdentifier("button2", "id", context.getPackageName());
//		Button closeBtn = (Button) xmlTwitterLoginUI.findViewById(layoutResId);
//		closeBtn.setOnClickListener(new Button.OnClickListener() {
//        	public void onClick(View v) {
//	        	try {
//	        		closeTwitterLoginView();
//	        	}
//	        	catch (Exception e) {        	     
//	        		System.out.println("SaveChart Error msg:"+e.getMessage());
//	        	}
//	        }
//        });
//		
//		this.layout.addView(xmlTwitterLoginUI);
	}
	private void closeTwitterLoginView() {
		//this.layout.removeView(xmlTwitterLoginUI);
	}
	private void setTwitterSession(boolean b) {
		String state="";
		if(b) {
			state = "true";
		} else {
			state = "false";
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("twitter_state", state);
		editor.commit();
	}
	private void twitterLogin() {
//		try {
//			int layoutResId = context.getResources().getIdentifier("editText1", "id", context.getPackageName());
//			EditText idText = (EditText) xmlTwitterLoginUI.findViewById(layoutResId);
//			layoutResId = context.getResources().getIdentifier("editText2", "id", context.getPackageName());
//			EditText pwText = (EditText) xmlTwitterLoginUI.findViewById(layoutResId);
//			final String id = idText.getText().toString();
//			final String pw = pwText.getText().toString();
//			
//	    	accessToken = oauth.getOAuthAccessToken(id.trim(), pw.trim());
//	    	
//	    	if(accessToken!=null) {
//	    		  setTwitterSession(true);
//	    		  COMUtil.showMessage(context, "트위터 로그인 성공!");
//	    		  this.layout.removeView(xmlTwitterLoginUI);
//	    	    	//키보드 내리기.
//	    	  		InputMethodManager imm = (InputMethodManager)COMUtil._chartMain.getSystemService(Context.INPUT_METHOD_SERVICE);
//	    	        imm.hideSoftInputFromWindow(idText.getWindowToken(), 0);
//	    	        imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);
//	    	} else {
//	    		setTwitterSession(false);
//	    		COMUtil.showMessage(context, "트위터 계정정보가 없습니다. 다시 입력해주세요.");
//	    	}
//	
//	        SharedPreferences.Editor edit = prefs.edit();
//	        edit.putString("twitterID", id); //트위터의 accessToken값을 저장한다.
//	        edit.putString("twitterPW", pw); //트위터의 accessToken값을 저장한다.
//	        edit.commit();
//		}catch (TwitterException e) {
//	            COMUtil.showMessage(context, "트위터 계정정보가 없습니다. 다시 입력해주세요.");
//	    }  
	}
	private void TwitterLogout() {
		//계정초기화(Preference포함)
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("twitterID", ""); //트위터의 accessToken값을 저장한다.
		edit.putString("twitterPW", ""); //트위터의 accessToken값을 저장한다.
		edit.commit();
	}

	//2012. 8. 3 키보드 숨기기 동작 가//
	//Device 의 키패드등에서 문자 입력후 확인 버튼 눌렸을 때의 작//
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if( (event != null && actionId == event.getAction()) || actionId == KeyEvent.KEYCODE_ENTER)
		{
			hideKeyPad();
			return true;
		}
		else
		//{
			return false;
		//}
	}
	//문자 분석툴 사용시 키패드 감추기 위함
	public void hideKeyPad()
	{
		InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(memoBox.getWindowToken(), 0);
		memoBox.clearFocus();
	}
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == LAUNCHED_ACTIVITY_JipyoSetup) {
//			if(resultCode == RESULT_OK)
//			{
//
//			}
//		}
//		else {
//			super.onActivityResult(requestCode, resultCode, data);
//		}
//	}

}
//Facebook 인증후 처리를 위한 callback class
class AuthorizeListener implements DialogListener
{
	@Override
	public void onCancel()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete(Bundle values)
	{
		// TODO Auto-generated method stub
//	  COMUtil.mFacebook.authorizeCallback(requestCode, resultCode, data);
		setChecked(true);

		SharedPreferences prefs = COMUtil._chartMain.getSharedPreferences("com.drfn.mchart", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("FACEBOOK_ACCESS_TOKEN", COMUtil.mFacebook.getAccessToken());
		editor.commit();

		COMUtil.showMessage(COMUtil._chartMain, "페이스북에 로그인하였습니다.");
		//if (C.D) Log.v(C.LOG_TAG, "::: onComplete :::");
	}

	@Override
	public void onError(DialogError e)
	{
		// TODO Auto-generated method stub
		try {
			COMUtil.showMessage(COMUtil._chartMain, "페이스북에 권한이 없습니다. 재로그인해주세요.");
			COMUtil.mFacebook.logout(COMUtil._chartMain);
			SharedPreferences prefs = COMUtil._chartMain.getSharedPreferences(COMUtil.appPackageId, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("FACEBOOK_ACCESS_TOKEN", "");
			editor.commit();

		}catch(Exception ee) {

		}

		setChecked(false);
	}

	@Override
	public void onFacebookError(FacebookError e)
	{
		// TODO Auto-generated method stub
		try {
			COMUtil.showMessage(COMUtil._chartMain, "페이스북에 권한이 없습니다. 재로그인해주세요.");
			COMUtil.mFacebook.logout(COMUtil._chartMain);
			SharedPreferences prefs = COMUtil._chartMain.getSharedPreferences(COMUtil.appPackageId, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("FACEBOOK_ACCESS_TOKEN", "");
			editor.commit();

		}catch(Exception ee) {

		}
		setChecked(false);
	}

	public void setChecked(boolean b) {
		// 페이스북 세션 상태 저장.
		String state = "";
		if(b) state = "true";
		else state = "false";
		SharedPreferences prefs = COMUtil._chartMain.getSharedPreferences("com.drfn.mchart", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("facebook_state", state);
		editor.commit();

		RelativeLayout layout = (RelativeLayout)COMUtil._mainFrame.layout.findViewWithTag("chartSaveLayout");
		SaveChartController saveChartView = (SaveChartController)layout.findViewWithTag("chartSaveView");
		//saveChartView.fbBox.setChecked(b);

	}
}
