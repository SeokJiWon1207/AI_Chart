package drfn.chart.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import drfn.chart.util.COMUtil;

public class ChartLastsaveDBHelper extends SQLiteOpenHelper {
	private String tablename = "Chartprolastsavedata";
	public ChartLastsaveDBHelper(Context context, String tableName) {
		super(context, tableName+".db", null, COMUtil.lastSaveDbVersion);
		if(tableName!=null) this.tablename = tableName;
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+this.tablename+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				" divideinfo TEXT," +
				" verinfo TEXT," +
				" savedate TEXT," +
				" userid TEXT," +
				" userip TEXT," +
				" codename TEXT," +
				" title TEXT," +
				" detail TEXT," +
				" fileName TEXT," +
				" symbol TEXT," +
				" lcode TEXT," +
				" apCode TEXT," +
				" dataTypeName TEXT," +
				" count TEXT," +
				" viewCount TEXT," +
				" valueOfMin TEXT," +	//(16)
				" jipyodata TEXT," +
				" periodConfig TEXT," +
				" analInfo TEXT,"+
				" chartMode int,"+
				" market int,"+
				" bunPeriodList TEXT,"+ //2013. 1. 17 분틱주기 설정 
				" ticPeriodList TEXT,"+ //2013. 1. 17 분틱주기 설정
				" marketName TEXT,"+
				" saveTitleName TEXT,"+ //2013. 1. 28 차트저장 타이틀 추가
                " listNo TEXT,"+ 							//listNo	 //26
                " baseline TEXT,"+ //기준선 설정					 		 //27
                " commonInfo TEXT,"+ //일반설정							 //28
				" fxMarginTypes TEXT,"+ //FxMarginType					 //29
				" connTypes TEXT,"+ 	//connType						 //30
				" dayTypes TEXT,"+ 		//dayType						 //31
				" floorTypes TEXT,"+ 	//floorType						 //32
				" isSavedJongmok TEXT"+ 	////2021.02.03 by HJW - 차트저장 종목저장 여부 추가  //33
				");");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//2015. 6. 3 내부DB에 저장항목 가변적으로 추가할 수 있도록 하기 >>
//		if(newVersion <= 20)
//		{
			COMUtil.showMessage(COMUtil._chartMain, "onUpgrade");
			db.execSQL("DROP TABLE IF EXISTS "+this.tablename);
			onCreate(db);
//		}
//		else if(newVersion > 20)	//db version 올라간 후 새롭게 추가되는 COLUMN 은 이쪽으로
//		{
//			db.beginTransaction();
//
//			/**
//			 * 	db버전 올린 후, 특정 이름을 가진 COLUMN을 추가한다. 예외상황 : 이미 테이블에 동일명 COLUMN 이 존재할 경우. 이때는 catch를 타면서 수행하지 않음.
//			 사용상황 : 새로 개발한 기능이 내부DB의 COLUMN 을 신규로 추가해서 이용해야 하는 경우, 기존 저장데이터와의 호환(기존엔 테이블을 한번 날려야했음)
//			 중요 : 마지막상태 저장 관련 DB 이므로, COMUtil의 saveLastState 함수에서 해당 COLUMN 영역에 데이터 채워주지 않으면 db가 에러가 나면서 초기화된다
//			 * */
//
//			//예제
////            try{db.execSQL("ALTER TABLE " + this.tablename + " ADD COLUMN barTypeStr TEXT");}catch(Exception e){}
//
//			db.setTransactionSuccessful();
//			db.endTransaction();
//		}
		//2015. 6. 3 내부DB에 저장항목 가변적으로 추가할 수 있도록 하기 <<
	}
}