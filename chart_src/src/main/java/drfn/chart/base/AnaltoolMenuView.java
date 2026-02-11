package drfn.chart.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import drfn.chart.util.COMUtil;

public class AnaltoolMenuView extends ScrollView {

	RelativeLayout layout;
	ScrollView contentsView;
	Button button;
	 RelativeLayout.LayoutParams params;
//	 private Handler mHandler;
	String[] menuItem = {"추세", "가로", "세로", "사각", "삭제"};

//     int[] menuImage = {R.drawable.c_option_01, R.drawable.c_option_02, R.drawable.c_option_03, R.drawable.c_option_05, R.drawable.c_option_15,
//     		R.drawable.c_option_04, R.drawable.c_option_06, R.drawable.c_option_07, R.drawable.c_option_08, R.drawable.c_option_09,
//     		R.drawable.c_option_10, R.drawable.c_option_11, R.drawable.c_option_12, R.drawable.c_option_13};
//     int[] menuImage_dn = {R.drawable.c_option_01_dn, R.drawable.c_option_02_dn, R.drawable.c_option_03_dn, R.drawable.c_option_05_dn, R.drawable.c_option_15_dn,
//     		R.drawable.c_option_04_dn, R.drawable.c_option_06_dn, R.drawable.c_option_07_dn, R.drawable.c_option_08_dn, R.drawable.c_option_09_dn,
//     		R.drawable.c_option_10_dn, R.drawable.c_option_11_dn, R.drawable.c_option_12_dn, R.drawable.c_option_13_dn};
	int[] menuImage = new int[14];
	int[] menuImage_dn = new int[14];

     int[] menuItemIndex = new int[]{COMUtil.TOOLBAR_CONFIG_LINE, COMUtil.TOOLBAR_CONFIG_HORZ, COMUtil.TOOLBAR_CONFIG_VERT, 
    		 						COMUtil.TOOLBAR_CONFIG_SPEEDLINE, COMUtil.TOOLBAR_CONFIG_ERASE, COMUtil.TOOLBAR_CONFIG_CROSS, COMUtil.TOOLBAR_CONFIG_ANDREW, 
    		 						COMUtil.TOOLBAR_CONFIG_ROUND, COMUtil.TOOLBAR_CONFIG_FIBORET, COMUtil.TOOLBAR_CONFIG_GANNFAN, COMUtil.TOOLBAR_CONFIG_FIBOTIME, 
    		 						COMUtil.TOOLBAR_CONFIG_FIBOFAN, COMUtil.TOOLBAR_CONFIG_FIBOARC, COMUtil.TOOLBAR_CONFIG_GANNGRID 
     };
     
     View xmlUI=null;
//     LinearLayout analLayout = null;
     
	public AnaltoolMenuView(Context context, RelativeLayout layout) {
		super(context);
		this.layout = layout;


		int[] menuImage_buf = {	context.getResources().getIdentifier("c_option_01", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_02", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_03", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_05", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_15", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_04", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_06", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_07", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_08", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_09", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_10", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_11", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_12", "drawable", context.getPackageName()),
								context.getResources().getIdentifier("c_option_13", "drawable", context.getPackageName())};
		menuImage = menuImage_buf;

		int[] menuImage_dn_buf = {	context.getResources().getIdentifier("c_option_01_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_02_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_03_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_05_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_15_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_04_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_06_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_07_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_08_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_09_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_10_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_11_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_12_dn", "drawable", context.getPackageName()),
									context.getResources().getIdentifier("c_option_13_dn", "drawable", context.getPackageName())};
		menuImage_dn = menuImage_dn_buf;



//		mHandler = new Handler();
		
		LayoutInflater factory = LayoutInflater.from(context);

		final int _RES_ID = context.getResources().getIdentifier("analtoolmenu", "layout", context.getPackageName());
		xmlUI = (View)factory.inflate(_RES_ID, null);
//		analLayout = (LinearLayout)xmlUI.findViewById(R.id.analtoolLayout);
		
        RelativeLayout analtoolLayout = new RelativeLayout(context);
        analtoolLayout.setTag("analtoolLayout");
		params =new RelativeLayout.LayoutParams(
        		(int)COMUtil.getPixel(60), (int)COMUtil.getPixel(187));
		params.leftMargin=5;
		params.topMargin=5;
		params.bottomMargin=5;
		analtoolLayout.setLayoutParams(params);
		analtoolLayout.addView(xmlUI);
		
		this.layout.addView(analtoolLayout);
	}
	
	int menuImageID;
	int menuImageDnID;
	public void setUI() {
		int nMenuLen = menuItemIndex.length;
        for(int i=0; i<nMenuLen; i++) {
    		menuImageID = menuImage[i];
    		menuImageDnID = menuImage_dn[i];
    		
            button = new Button(this.getContext());
            button.setId(menuItemIndex[i]);
            button.setBackgroundResource(menuImageID);
            button.setTextSize(12);
            if(i == 0) {
            	button.setTag(8001);
            } else if(i == 1) {
            	button.setTag(8000);
            } else {
            	button.setTag(""+i);
            }
            params =new RelativeLayout.LayoutParams((int)COMUtil.getPixel(60), (int)COMUtil.getPixel(38));
    		params.leftMargin=5;
    		params.topMargin=(int)COMUtil.getPixel(43*i+7);
    		button.setLayoutParams(params);
    		
    		button.setOnClickListener(new OnClickListener(){
    	    	public void onClick(View v) {
    	    		selectMenu(v);	
    	    	}
    		});
  
    		button.setOnTouchListener(
    				new OnTouchListener() {
    					public boolean onTouch(View v, MotionEvent event) {
    						switch(event.getAction()) {
    							case MotionEvent.ACTION_DOWN:
    								 v.setBackgroundResource(menuImage[Integer.parseInt((String)v.getTag())]);
    								 
    								 break;
    							case MotionEvent.ACTION_UP:
    								 v.setBackgroundResource(menuImage_dn[Integer.parseInt((String)v.getTag())]);
    								 break;
    						
    						}
    						return false;
    					}
    				}
    			);
    		
//    		analLayout.addView(button);
        }

	}
	boolean isSelectSameAnalToolButton=false;
	int preSelectedAnalToolTag = -1; 
	private void selectMenu(View v) {
		int tag = v.getId();
		setButtonMode(tag);
		if(tag!=COMUtil.TOOLBAR_CONFIG_ERASE && tag!=COMUtil.TOOLBAR_CONFIG_ALL_ERASE && tag!=COMUtil.TOOLBAR_CONFIG_DWMM && tag!=COMUtil.TOOLBAR_CONFIG_TEXT) {//지우기모드엔 토글생략..
			if(tag==preSelectedAnalToolTag) {
				isSelectSameAnalToolButton=!isSelectSameAnalToolButton;
			} else {
				isSelectSameAnalToolButton=false;
			}
			if(isSelectSameAnalToolButton) { //같은 분석툴 기능이면 해제한다.
				COMUtil._neoChart._cvm.setToolbarState(9999);
				return;
			}
		}
		
		COMUtil.selectChart(tag);

		if(tag!=COMUtil.TOOLBAR_CONFIG_ERASE && tag!=COMUtil.TOOLBAR_CONFIG_ALL_ERASE && tag!=COMUtil.TOOLBAR_CONFIG_DWMM && tag!=COMUtil.TOOLBAR_CONFIG_TEXT) {//삭제
			preSelectedAnalToolTag = tag;
		}
	}
	int preTag = -1;
	boolean btnToggle = false;
	private void setButtonMode(int tag) {
		if (tag==COMUtil.TOOLBAR_CONFIG_ERASE || tag==COMUtil.TOOLBAR_CONFIG_DWMM|| tag == COMUtil.TOOLBAR_CONFIG_ALL_ERASE|| tag == COMUtil.TOOLBAR_CONFIG_TEXT) {
			return;
		}
		COMUtil.isContinueAnalDrawMode=true;
		COMUtil.isCrossBtnSelect=false;
		if(tag==preTag) {
			btnToggle = !btnToggle;
		} else {
			btnToggle = true;
		}
		
		int cnt = this.menuItemIndex.length;
		
		for(int i=0; i<cnt; i++) {
			Button btn = (Button)layout.findViewById(menuItemIndex[i]);
			btn.setBackgroundResource(menuImage[i]);
			if(tag==menuItemIndex[i]) {
				if(btnToggle) {
					btn.setBackgroundResource(menuImage_dn[i]);
				} else {
					btn.setBackgroundResource(menuImage[i]);
				}
			}
		}
		
		preTag = tag;
	}
}
