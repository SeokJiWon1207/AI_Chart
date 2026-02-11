/**
 *
 */
package drfn.chart.base;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView.OnEditorActionListener;
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
import drfn.chart.util.COMUtil;

/**
 * @author user
 *
 */
//2012. 8. 3   키보드 감추기 기능을 위해 OnEditorActionListener 를 포함시킴 
public class CompareChartType extends View
		implements View.OnClickListener {

	public LinearLayout layout=null;
	
	public  CheckBox chkCTRTOVER, chkCTRT, chkTrend;
	public TextView tvColorOpen, tvColorOpen2, tvColorOpen3, tvColorOpen4, tvColorOpen5;
	
	
	LinearLayout xmlUI = null;

	Handler mHandler=null;
	Context context;



	public CompareChartType(final Context context, LinearLayout layout) {
		super(context);
		this.context = context;

		//2012. 8. 13  가로모드에서 저장하기 창을 실행시키면 세로모드로 나오게 수정 
//		COMUtil._chartMain.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); 
		Configuration config = getResources().getConfiguration();

	

		this.layout = layout;

		mHandler = new Handler();


		LayoutInflater factory = LayoutInflater.from(context);

		//2012. 8. 20 태블릿  저장하기창 팝업화 
		int layoutResId;

		layoutResId = context.getResources().getIdentifier("chart_compare_type", "layout", context.getPackageName());



		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams((int) COMUtil.getPixel(358),(int) COMUtil.getPixel(362));	//2014. 10. 14 popupwindow 배경 반투명색
		xmlUI.setLayoutParams(params);
		//2012. 7. 24 자동추세설정창 에디트텍스트 레이아웃 설정
		//setUI();
		xmlUI.requestFocus();
	
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		//2013. 8. 8 가로모드 인식방법 변경 
		{
			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(300), (int)COMUtil.getPixel(365)));
		}
		else
		{
			xmlUI.setLayoutParams(new LinearLayout.LayoutParams((int)COMUtil.getPixel(300), (int)COMUtil.getPixel(392)));
		}
	}
	//2012. 7. 23 차트저장화면 가로세로 전환을 위해 xmlUI getter 설정 
	public View getXmlUI()
	{
		return xmlUI;
	}
	
	private void close() {
//	
	
	}

	//OnClick에대한 처리루틴.
	public void onClick(View view) {
//		int btnID = view.getId();
//		switch(btnID) {
//			case R.id.frameaBtnFunction:
//
//		}
	}


	
}