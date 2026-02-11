package drfn.chart.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import drfn.chart.util.COMUtil;

public class CompareChartLastsaveDBHelper extends SQLiteOpenHelper {
	private String tablename = "Comparelastsavedata";
	public CompareChartLastsaveDBHelper(Context context, String tableName) {
		super(context, tableName+".db", null, COMUtil.dbVersion);
		if(tableName!=null) this.tablename = tableName;
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+this.tablename+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				" data TEXT," +
				" dataTypeName TEXT," +
				" valueOfMin TEXT" +
				");");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		COMUtil.showMessage(COMUtil._chartMain, "onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS "+this.tablename);
		onCreate(db);
	}
}