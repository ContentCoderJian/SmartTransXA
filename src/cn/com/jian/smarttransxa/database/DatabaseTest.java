package cn.com.jian.smarttransxa.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * 数据库单元测试类
 * 
 * @author Jian
 * 
 */
public class DatabaseTest extends AndroidTestCase {
	private MyDatabaseHelper mdbp;
	private SQLiteDatabase db;

	public void test() {
		// getContext():获取一个虚拟的上下文
		mdbp = new MyDatabaseHelper(getContext(), "xian.db", null, 1);
		// 如果数据库不存在，先创建数据库，再获取可读可写的数据库对象，如果数据库存在，就直接打开
		// SQLiteDatabase db = mdbp.getWritableDatabase();
		// 如果存储空间满了，那么返回只读数据库对象
		// SQLiteDatabase db = mdbp.getReadableDatabase();

	}

	// 测试框架初始化完毕之后，在测试方法执行之前，此方法调用
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mdbp = new MyDatabaseHelper(getContext(), "xian.db", null, 1);
		db = mdbp.getWritableDatabase();
	}

	// 测试方法执行完毕之后，此方法调用
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		db.close();
	}

	public void insertApi() {
		// 把要插入的数据全部封装至ContentValues对象
		ContentValues values = new ContentValues();
		values.put("line", "教育专线");
		values.put("time", " 劳动南路 6：30-20：00 幸福中路 6：30-20：00");
		values.put(
				"station",
				"劳动南路 - 西工大 - 边家村 - 太白路立交 - 西斜六路 - 公交五公司 - 西斜七路 - 沙井村 - 电子商城 - 紫薇花园 - 电子二路 - 省博爱医院 - 东仪路 - 二府庄 - 省肿瘤医院 - 医学院 - 纬二街 - 小寨 - 长安立交 - 省体育场 - 草场坡 - 南稍门 - 文艺路 - 瓦窑小区 - 李家村 - 空军医院 - 太乙路 - 建东街东段 - 东南城角 - 南郭门 - 兴庆公园 - 交大电脑城 - 交大商场 - 金花南路 - 长兴路 - 互助路立交 - 公园南路北口 - 韩森寨 - 幸福中路(福邸铭门小区) - 幸福中路");
		values.put(
				"opposite",
				"幸福中路 - 幸福中路(福邸铭门小区) - 韩森寨 - 公园南路北口 - 互助路立交 - 长兴路 - 金花南路 - 交大商场 - 交大电脑城 - 兴庆公园 - 南郭门 - 东南城角 - 建东街东段 - 太乙路 - 空军医院 - 瓦窑小区 - 文艺路 - 南稍门 - 草场坡 - 省体育场 - 长安立交 - 小寨 - 纬二街 - 医学院 - 省肿瘤医院 - 二府庄 - 东仪路 - 省博爱医院 - 电子二路 - 紫薇花园 - 电子商城 - 沙井村 - 西斜七路 - 公交五公司 - 西斜六路 - 太白路立交 - 边家村 - 西工大 - 劳动南路");
		// 插入第一条数据
		db.insert("bus_line", null, values);
		Log.i("Database", "数据添加成功");
	}

	public void deleteApi() {
		// 删除刚刚添加的教育专线数据
		int i = db.delete("bus_line", "line = ? and _id = ?", new String[] {
				"教育专线", "1" });
		Log.i("Database", "删除数据 i = " + i);
	}

	public void updateApi() {
		// 把教育专线的time 改成 劳动南路 6：00-23：00 幸福中路 6：00-23：00
		ContentValues values = new ContentValues();
		values.put("time", " 劳动南路 6：00-23：00 幸福中路 6：00-23：00");
		int i = db.update("bus_line", values, "line = ?",
				new String[] { "教育专线" });
		Log.i("Database", "数据修改 i = " + i);
	}

	public void selectApi() {
		// 查询刚刚添加的 教育专线 数据
		Cursor cursor = db.query("bus_line", null, null, null, null, null,
				null, null);
		while (cursor.moveToNext()) {
			String line = cursor.getString(cursor.getColumnIndex("line"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String station = cursor.getString(cursor.getColumnIndex("station"));
			String opposite = cursor.getString(cursor
					.getColumnIndex("opposite"));
			Log.i("Database", "数据查询:  " + line + ";/n" + time + ";/n" + station
					+ ";/n" + opposite);
		}
	}
}
