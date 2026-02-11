package drfn.chart.base;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import drfn.chart.NeoChart2;
import drfn.chart.comp.ChartsaveCursorAdapter;
import drfn.chart.util.COMUtil;

public class OneTouchItemView extends View {

	NeoChart2 baseChart;
	public RelativeLayout layout;
	View xmlUI=null;
	LinearLayout ll = null;
	public ArrayList<LoadCellItem> mitems;

	private Context context = null;
//	private ViewWrapper wrapper = null;
	private Cursor cursor = null;
	private ChartsaveDBHelper mHelper=null;
	private SQLiteDatabase db=null;

	String strLocalTable = null;

	ArrayList<LoadCellItem> m_itemsArr;
	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	ChartsaveCursorAdapter Adapter = null;

	RelativeLayout.LayoutParams params = null;

//	int pos = 0;

	public OneTouchItemView(Context context, RelativeLayout layout) {
		super(context);
		this.context = context;
		this.layout = layout;

		LayoutInflater factory = LayoutInflater.from(context);
		int layoutResId = context.getResources().getIdentifier("onetouchview", "layout", context.getPackageName());
		xmlUI = factory.inflate(layoutResId, null);
		this.layout.addView(xmlUI);
	}
	public void setBaseChart(NeoChart2 chart) {
		baseChart = chart;
	}

	public void setBasicUI() {
		initUI();
	}

	public void initUI(){

		int layoutResId = context.getResources().getIdentifier("btn_popup_save", "id", context.getPackageName());
		Button btnPopupSave = (Button)xmlUI.findViewById(layoutResId);
		btnPopupSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				COMUtil.saveChart(v);
			}
		});

		layoutResId = context.getResources().getIdentifier("ll_onetouchlist", "id", context.getPackageName());
		ll = (LinearLayout)xmlUI.findViewById(layoutResId);

		params =new RelativeLayout.LayoutParams(
				(int)COMUtil.getPixel(60), (int)COMUtil.getPixel(26));
		params.leftMargin = (int)COMUtil.getPixel(2);
		params.rightMargin = (int)COMUtil.getPixel(2);

		m_itemsArr = LoadChartDataManager.getUserItems();
		createLocalChartList();

		makeButton();

//		layoutResId = context.getResources().getIdentifier("btn_left_chart", "drawable", context.getPackageName());
//
//		while (cursor.moveToNext()){
//			Button btn = new Button(context);
//			btn.setLayoutParams(params);
//			String btnText = cursor.getString(25);
//			btn.setText(btnText);
//			btn.setTextSize(12);
//			btn.setBackgroundResource(layoutResId);
//			btn.setTag(""+cursor.getPosition());
//			btn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					COMUtil._mainFrame.setLocalStorageState(cursor,Integer.parseInt((String)v.getTag()));
//				}
//			});
//			ll.addView(btn);
//		}

	}
	public void resetUI() {
		ll.removeAllViewsInLayout();

//		strLocalTable = COMUtil._mainFrame.strLocalFileName;
//
//		cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id desc", null);

		createLocalChartList();
		makeButton();

	}
	private void makeButton(){
		int layoutResId = context.getResources().getIdentifier("editbox_enable", "drawable", context.getPackageName());

		//2017.10.19 LYH - 차트 불러오기 순서변경 >>
//		String strSortData = COMUtil.getEnvString("savechart_sortlist", "");
//		if (strSortData.length() > 0) {
//			String[] arrData = strSortData.split(";");
//
//			if (arrData.length >= cursor.getCount()) {
//				for (int i = 0; i < arrData.length; i++) {
//					for (int j = 0; j < cursor.getCount(); j++) {
//						cursor.moveToPosition(j);
//						if (arrData[i].equals(cursor.getString(0))) {
//							Button btn = new Button(context);
//							btn.setLayoutParams(params);
//							String btnText = cursor.getString(25);
//							btn.setText(btnText);
//							btn.setTextSize(12);
//							btn.setBackgroundResource(layoutResId);
//							btn.setTag(""+cursor.getPosition());
//							btn.setOnClickListener(new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									COMUtil._mainFrame.setLocalStorageState(cursor,Integer.parseInt((String)v.getTag()));
//								}
//							});
//							ll.addView(btn);
//						}
//					}
//				}
//			}
//		}else{
			while (cursor.moveToNext()){
				Button btn = new Button(context);
				btn.setLayoutParams(params);
				String btnText = cursor.getString(25);
				btn.setText(btnText);
				btn.setTextSize(12);
				btn.setSingleLine();
				btn.setBackgroundResource(layoutResId);
				btn.setTag(""+cursor.getPosition());
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						COMUtil._mainFrame.setLocalStorageState(cursor,Integer.parseInt((String)v.getTag()));
					}
				});
				ll.addView(btn);
			}
			//2017.10.19 LYH - 차트 불러오기 순서변경 <<
//		}
	}
	public void createLocalChartList() {
		try {
			strLocalTable = COMUtil._mainFrame.strLocalFileName;
//			strLocalTable = COMUtil._mainFrame.strLocalFileName+"_local";
//			strLocalTable = "common"+"_local";
//			if(m_nMode == HTS_CHART)
//			{
//				strLocalTable = COMUtil._mainFrame.strLocalFileName+"_hts";
//			}
			//차트 최초 실행시 두개항목 삽입 >>
//			if(!checkDataBase(strLocalTable))
//				setFirstLocalState(strLocalTable);
			//차트 최초 실행시 두개항목 삽입 <<

			if(mHelper==null) {
				mHelper = new ChartsaveDBHelper(context, strLocalTable);
			}
			db = mHelper.getWritableDatabase();

			//		cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id desc", null);
			cursor = db.rawQuery("SELECT * FROM "+strLocalTable+" order by _id desc", null);
			COMUtil._chartMain.startManagingCursor(cursor);//cursor 자동관리.


			m_itemsArr.clear();//초기화.
			itemChecked.clear();
			m_itemsArr = LoadChartDataManager.getUserItems();

			Adapter = new ChartsaveCursorAdapter(context, cursor);
//			Adapter.setMode(m_nMode);
//
//			COMUtil._chartMain.stopManagingCursor(cursor);
//			//		cursor.close();
//			mHelper.close();
//
//			// 커스텀 ArrayAdapter 선언/초기화.
//			Adapter.notifyDataSetChanged();
//
		} catch(Exception e) {
			System.out.println("Debug:"+e.getMessage());
		}
	}
	//차트 최초 실행시 두개항목 삽입 >>
	public void setFirstLocalState(String filename){
//		String strItem = "divideInfo:/@/chartMode:0/1/@/symbol:030610/@/lcode:S31/@/dataTypeName:2/@/count:300/@/viewCount:40/@/valueOfMin:1/@/apCode:03815/@/market:0/@/marketName:0/@/detail:주가이동평균, 거래량, 일목균형표/@/codeName:교보증권/@/savedate:2017-09-12/@/strOnlyChart:1/@/jipyodata:일본식봉{0=0=252=50=57=252=50=57=0=126=217=0=126=217=1=1=1=1=1=1=1=0=0=0}~주가이동평균{5=20=60=120=200=300=400=500=600=700=1=214=0=0=1=0=1=201=102=211=1=0=1=71=145=216=1=0=1=0=102=102=1=0=1=127=207=22=0=0=1=255=86=38=0=0=1=165=102=216=0=0=1=73=177=228=0=0=1=206=46=136=0=0=1=246=24=17=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=1=}~일목균형표{26=9=26=52=26=1=64=64=64=1=0=1=0=102=102=1=0=1=214=0=0=1=0=1=252=50=57=1=2=1=0=126=217=1=2=1=}~거래량{3=252=50=57=0=126=217=24=65=127=1=}~/@/analInfo:@@@divideInfo:/@/chartMode:0/1/@/symbol:030610/@/lcode:S31/@/dataTypeName:2/@/count:300/@/viewCount:40/@/valueOfMin:1/@/apCode:03815/@/market:0/@/marketName:0/@/detail:주가이동평균, 거래량, MACD/@/codeName:AP위성/@/savedate:2017-09-12/@/strOnlyChart:1/@/jipyodata:일본식봉{0=0=252=50=57=252=50=57=0=126=217=0=126=217=1=1=1=1=1=1=1=0=0=0}~주가이동평균{5=20=60=120=200=300=400=500=600=700=1=214=0=0=1=0=1=201=102=211=1=0=1=71=145=216=1=0=1=0=102=102=1=0=1=127=207=22=0=0=1=255=86=38=0=0=1=165=102=216=0=0=1=73=177=228=0=0=1=206=46=136=0=0=1=246=24=17=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=1=}~거래량{3=252=50=57=0=126=217=24=65=127=1=}~MACD{12=26=9=1=255=86=38=1=0=1=71=145=216=1=0=1=0=0=1=}~/@/analInfo:";
//		COMUtil.cloudToStorage(context,strItem,filename);
	}
	//경로 위치에 db 체크
	private boolean checkDataBase(String dbname) {

//		Context ctx = COMUtil._chartMain; // for Activity, or Service. Otherwise simply get the context.
		dbname = dbname + ".db";
		String dbpath = context.getDatabasePath(dbname).getPath();

		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(dbpath, null,
					SQLiteDatabase.OPEN_READONLY);
			checkDB.close();
		} catch (SQLiteException e) {
			// database doesn't exist yet.
		}
		return checkDB != null;
	}
	//차트 최초 실행시 두개항목 삽입 <<
}
