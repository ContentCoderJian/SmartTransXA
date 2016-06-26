package cn.com.jian.smarttransxa.fragment;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.jian.smarttransxa.activity.R;
import cn.com.jian.smarttransxa.database.BusDatabase;

/**
 * station 站点查询
 * 
 * @author Jian
 * 
 */
public class StationFragment extends Fragment {

	// 声明station3个控件和数据库对象
	private EditText etStation;
	private Button btnStationSearch;
	private ListView lvStation;
	private BusDatabase dbfile;
	private SQLiteDatabase database;

	// 声明 Adapter 和集合
	private ArrayAdapter<String> adapter;
	private List<String> route;

	public StationFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Log.d("smartTransXA", "StationFragment");
		View v = inflater.inflate(R.layout.fragment_trans_station, container,
				false);
		// 获取数据库
		initDatabase();

		// 获取控件
		initViews(v);

		// 设置监听器
		setListeners();
		return v;
	}

	private void initDatabase() {
		dbfile = new BusDatabase(getActivity());
		dbfile.openDatabase();
		dbfile.closeDatabase();
		database = SQLiteDatabase.openOrCreateDatabase(BusDatabase.getDbPath()
				+ "/" + BusDatabase.getDbName(), null);
	}

	private void initViews(View v) {
		etStation = (EditText) v.findViewById(R.id.et_station);
		btnStationSearch = (Button) v.findViewById(R.id.btn_station_search);
		lvStation = (ListView) v.findViewById(R.id.lv_station);
	}

	private void setListeners() {
		OnClickListener listener = new InnerOnClickListener();
		btnStationSearch.setOnClickListener(listener);
	}

	private class InnerOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// // 获取输入框的字符串
			String busStation = etStation.getText().toString().trim();

			// 显示station 的ListView,但不释放空间
			lvStation.setVisibility(View.VISIBLE);

			// 判断输入的站点是否满足条件
			// -- 是　：就接着执行
			// -- 否　：Toast提示 "请重新输入公交站点"
			if (busStation != null && !"".equals(busStation)
					&& busStation.length() > 1 && !" - ".equals(busStation)) {
				// sql语句模糊查询在station中存在busStation 的公交数据
				Cursor cursor = database.rawQuery(
						"select * from bus_line where station like '%"
								+ busStation + "%'", null);
				// 游标定位第一行
				cursor.moveToFirst();

				// 判断sql是否没找到,即判断游标是否指向最后一位
				// -- 是 : 就关闭游标,Toast提示 "不好意思,您输入的公交站点暂未开通公交线路!"
				// -- 否 : 就把查到满足条件的 数据库列表中line 所对应的公交线路添加到集合中
				if (cursor.isAfterLast()) {
					cursor.close();
					// 显示station 的ListView,但不释放空间
					lvStation.setVisibility(View.INVISIBLE);
					Toast.makeText(getActivity(),
							"不好意思,您输入的 " + busStation + " 暂未开通公交线路!",
							Toast.LENGTH_SHORT).show();
				} else {
					route = new ArrayList<String>();

					route.add("   ----- 经过 " + busStation
							+ " 的公交线路有 ： -----   ");
					while (!cursor.isAfterLast()) {
						route.add(" <"
								+ cursor.getString(cursor
										.getColumnIndex("line"))
								+ "路> \n运行时间 :"
								+ cursor.getString(cursor
										.getColumnIndex("time")));
						// 游标移到下一行
						cursor.moveToNext();
					}
					adapter = new ArrayAdapter<String>(getActivity(),
							R.layout.station_item, route);
					// 给Station 的ListView 设置适配器
					lvStation.setAdapter(adapter);
				}
				// 关闭游标,释放资源
				cursor.close();
			} else {
				// 显示route 的ListView,但不释放空间
				lvStation.setVisibility(View.INVISIBLE);
				Toast.makeText(getActivity(), "请重新输入公交站点", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
