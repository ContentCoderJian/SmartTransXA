package cn.com.jian.smarttransxa.database;

/**
 * 
 * 创建数据库
 * 
 * @author Jian
 * 
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "xian.db";
	private static final int DB_VERSION = 1;

	// SQL语句创建 bus_line 表
	public static final String CREATE_BUS_LINE = "create table bus_line ("
			+ "_id integer primary key autoincrement, " 
			+ "line varchar(12), "
			+ "time varchar(150), " 
			+ "station varchar(500),"
			+ "opposite varchar(500)) ";

	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BUS_LINE);
		Log.i("DataBase", "表BUS_LINE创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS bus_line");
		onCreate(db);
		Log.i("DataBase", "表BUS_LINE升级成功");
	}
}
