package drfn;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Hashtable;

import drfn.chart.util.COMUtil;

public class ChartPro extends Activity implements UserProtocol {

	/** Called when the activity is first created. */
	public RelativeLayout layout;
	LinearLayout layoutMain;
	LinearLayout layoutChart;
	LinearLayout layoutFinder;
	LinearLayout layoutAnal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		COMUtil.setChartMain(this);
		//COMUtil.userProtocol = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//2012. 7. 23 차트가로세로 회전 막은 것 해제
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//기기정보얻기.
		TelephonyManager telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        String phoneNumber = telephony.getLine1Number();
		String deviceID = telephony.getDeviceId();
		COMUtil.deviceID = deviceID;
//        String userId2 = android.os.Build.FINGERPRINT;
//        String userId3 = android.os.Build.BOARD;
		int version = android.os.Build.VERSION.SDK_INT;

		version = 10; //phone으로 지정
		//version 15 = icecream
		if(version>=11) { //Android 3.0 이상.(허니콤)
			COMUtil.deviceMode=COMUtil.HONEYCOMB;
			COMUtil.mainTopMargin = 69;
		} else {
			COMUtil.deviceMode="";
		}
		StringBuffer SYSinfoBuffer = new StringBuffer();

		getProperty("java.vendor.url", "java.vendor.url", SYSinfoBuffer);
//        getProperty("java.version", "java.version", SYSinfoBuffer);
//        getProperty("java.class.path", "java.class.path", SYSinfoBuffer);
//        getProperty("java.class.version", "java.class.version", SYSinfoBuffer);
//        getProperty("java.vendor", "java.vendor", SYSinfoBuffer);
//        getProperty("java.home", "java.home", SYSinfoBuffer);        
//        getProperty("user.name", "user.name", SYSinfoBuffer);
//        getProperty("user.home", "user.home", SYSinfoBuffer);
//        getProperty("user.dir", "user.dir", SYSinfoBuffer);
//      getProperty("os.name", "os.name", SYSinfoBuffer);
//      getProperty("os.version", "os.version", SYSinfoBuffer);
		String userId = SYSinfoBuffer.toString();
		int index = userId.indexOf("android");
		if(index!=-1) {
			userId = "Android";
		}

		COMUtil.userId=userId;
		COMUtil.userIp=COMUtil.getLocalIpAddress();

		// 최상위 위젯을 생성한다.
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		if(COMUtil.deviceMode==COMUtil.HONEYCOMB) {
			height = height - (int)COMUtil.getPixel(25);
		}

		COMUtil.chartWidth=width;
		COMUtil.chartHeight=height;

		layout = new RelativeLayout(this);
		layout.setLayoutParams(
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		layout.setFocusable(true);

//        COMUtil._layout = layout;

		//초기화면 생성
		COMUtil.init(layout);

		this.setContentView(layout);

	}
	private void getProperty(String desc, String property, StringBuffer tBuffer)
	{
		tBuffer.append(desc);
		tBuffer.append(" : ");
		tBuffer.append(System.getProperty(property));
		tBuffer.append("\n");
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//2012. 7. 23 가로세로모드에 따른 차트저장화면 전환을 위해서 해당UI의 컨트롤들 얻어옴
		View xmlUI = null;
		if(COMUtil.chartSaveView != null)
		{
			xmlUI = COMUtil.chartSaveView.getXmlUI();
		}
		LinearLayout linearXmlUI = null;
		if(xmlUI != null)
		{
			//2012. 8. 2 저장 레이아웃 xml 변경으로 이미지/메모를 담고있는 layout아이디가 변경되어 수정
			int layoutResId = this.getResources().getIdentifier("savemain", "id", this.getPackageName());
			linearXmlUI = (LinearLayout)xmlUI.findViewById(layoutResId);
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
//			   System.out.println("가로모드");
//		      getWindow().setFlag (WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		                                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		      setContentView(); 

			//2012. 7. 23 차트저장하기 화면의 UI 가로세로 전환 적용
			if(linearXmlUI != null)
			{
				linearXmlUI.setOrientation(LinearLayout.HORIZONTAL);
			}

		}
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
//		    	 System.out.println("세로모드");
//		     getWindow().setFlag(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//		                                              WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		      setContentView();

			//2012. 7. 23 차트저장하기 화면의 UI 가로세로 전환 적용
			if(linearXmlUI != null)
			{
				linearXmlUI.setOrientation(LinearLayout.VERTICAL);
			}
		}
	}

	/** DivideView(XML)에서 호출됨. **/
	public void setDivideChart(View v) {
		COMUtil.setDivideChart(v);
	}

	/** 지표설정창(XML)에서 호출됨. **/
//    public void setIndicator(View v) {
//    	int tag = Integer.parseInt((String)v.getTag());
//    }

	/** 기간설정창(XML)에서 호출됨. **/
	public void setPeriodConfigFromXML(View v) {
		COMUtil.setPeriodConfigFromXML(v);
	}
	/** 차트설정창(XML)에서 호출됨. **/
//    public void setChartFromXML(View v) {
//    	COMUtil.setChartFromXML(v);
//    }

	/** analtoolsubbar(XML)에서 호출됨. **/
//    public void selectRemove(View v) {
//    	COMUtil._neoChart.removeSelectedAnalTool();
//    }
//    public void selectRemoveAll(View v) {
//    	COMUtil._neoChart.removeAllAnalTool();
//    }

	/** saveloadmenu(XML)에서 호출됨. **/
	public void saveChart(View v) {
		COMUtil.saveChart(v);
	}
	public void setAnalTool(View v) {
		//COMUtil.setAnalTool(v);
	}
	public void loadChart(View v) {
		COMUtil.loadChart(v);
	}
	public void setOverlayView(View v) {
//		COMUtil.setOverlayView(v, this.layout);
	}
	public void setJipyoView(View v) {
//		COMUtil.setJipyoView(v, this.layout);
	}
	//2012. 7. 17  차트유형  + 버튼 핸들러 등록 
	public void setChartView(View v) {
//		COMUtil.setChartView(v, this.layout);	
	}
	public void initChart(View v) {

		COMUtil._mainFrame.mainBase.initChart("pressInitBtn");
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		COMUtil.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// if(COMUtil._neoChart!=null) COMUtil._neoChart.onResume();
	}

	@Override
	protected void onPause() {
		if(COMUtil._neoChart!=null) {
			//COMUtil._neoChart.onPause();
			COMUtil.saveLastState("Chartprolastsavedata");
		}

		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		layout=null;
		layoutMain=null;
		layoutChart=null;
		layoutFinder=null;

		COMUtil.destroy();
	}

	/* UserProtocol Interface */
	public void requestInfo(int tag, Hashtable<String, Object> data) {

	}
}


