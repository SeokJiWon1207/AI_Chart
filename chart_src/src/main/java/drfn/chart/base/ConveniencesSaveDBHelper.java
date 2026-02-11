package drfn.chart.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import drfn.chart.util.COMUtil;

public class ConveniencesSaveDBHelper extends SQLiteOpenHelper {
	private String tablename = "ConveniencesSave";
	public ConveniencesSaveDBHelper(Context context, String tableName) {
		super(context, tableName+".db", null, COMUtil.dbVersion);
		if(tableName!=null) this.tablename = tableName;
	}

	public void onCreate(SQLiteDatabase db) {
		//2013. 9. 3 도구설정 저장/불러오기 >> : 저장처리를 위해 column 추가 
//		db.execSQL("CREATE TABLE "+this.tablename+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//				" data TEXT" +
//		");");

		db.execSQL("CREATE TABLE "+this.tablename+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				" data TEXT," +
				" analtooldata TEXT" +
                " indecatorDefault TEXT" +
				");");
		//2013. 9. 3 도구설정 저장/불러오기 >>
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		COMUtil.showMessage(COMUtil._chartMain, "onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS "+this.tablename);
		onCreate(db);
	}
}