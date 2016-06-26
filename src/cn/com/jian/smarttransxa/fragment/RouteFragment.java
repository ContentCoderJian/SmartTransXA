package cn.com.jian.smarttransxa.fragment;

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
 * route 线路查询
 * 
 * @author Jian
 * 
 */
public class RouteFragment extends Fragment {

	// 声明route 4个控件和数据库对象
	private EditText etRoute;
	private Button btnRouteSearch;
	private Button btnRouteSearchOppo;
	private ListView lvRoute;
	private BusDatabase dbfile;
	private SQLiteDatabase database;

	// 声明 Adapter 和数组
	private ArrayAdapter<String> adapter;
	private String[] stations;

	public RouteFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_trans_route, container,
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
		etRoute = (EditText) v.findViewById(R.id.et_route);
		btnRouteSearch = (Button) v.findViewById(R.id.btn_route_search);
		btnRouteSearchOppo = (Button) v
				.findViewById(R.id.btn_route_search_opposite);
		lvRoute = (ListView) v.findViewById(R.id.lv_route);
	}

	private void setListeners() {
		OnClickListener listener = new InnerOnClickListener();
		btnRouteSearch.setOnClickListener(listener);
		btnRouteSearchOppo.setOnClickListener(listener);
	}

	private class InnerOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_route_search:
				showRoute();
				break;
			case R.id.btn_route_search_opposite:
				showOppoRoute();
				break;
			}
		}
	}

	public void showRoute() {

		// 显示route 的ListView
		lvRoute.setVisibility(View.VISIBLE);
		// // 获取输入框的字符串
		String busRoute = etRoute.getText().toString().trim();

		// 判断输入的线路是否满足条件
		// -- 是　：就接着执行
		// -- 否　：Toast提示 "请重新输入公交线路"
		if (busRoute != null && !"".equals(busRoute)) {
			// sql语句查询满足busRoute的公交数据
			Cursor cursor = database.rawQuery(
					"select * from bus_line where line  = '" + busRoute + "'",
					null);
			// 游标定位第一行
			cursor.moveToFirst();

			// 判断sql是否没找到,即判断游标是否指向最后一位
			// -- 是 : 就关闭游标,Toast提示 "您输入的公交线路不存在!"
			// -- 否 : 就把查到满足条件的 数据库列表中station 所对应的公交线路用split()方法分割成数组
			if (cursor.isAfterLast()) {
				cursor.close();
				// 显示route 的ListView, 但不释放空间
				lvRoute.setVisibility(View.INVISIBLE);
				Toast.makeText(getActivity(), "您输入的公交线路不存在!",
						Toast.LENGTH_SHORT).show();
			} else {
				stations = cursor.getString(cursor.getColumnIndex("station"))
						.split(" - ");
				adapter = new ArrayAdapter<String>(getActivity(),
						R.layout.route_item, stations);
				// 给route 的ListView 设置适配器
				lvRoute.setAdapter(adapter);
			}
			// 关闭游标,释放资源
			cursor.close();

		} else {
			// 显示route 的ListView,但不释放空间
			lvRoute.setVisibility(View.INVISIBLE);
			Toast.makeText(getActivity(), "请重新输入公交线路", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void showOppoRoute() {
		// 显示route 的ListView
		lvRoute.setVisibility(View.VISIBLE);
		// // 获取输入框的字符串
		String busRouteOppo = etRoute.getText().toString().trim();

		// 判断输入的线路是否满足条件
		// -- 是　：就接着执行
		// -- 否　：Toast提示 "请重新输入公交线路"
		if (busRouteOppo != null && !"".equals(busRouteOppo)) {
			// sql语句查询满足busRoute的公交数据
			Cursor cursor = database.rawQuery(
					"select * from bus_line where line  = '" + busRouteOppo
							+ "'", null);
			// 游标定位第一行
			cursor.moveToFirst();

			// 判断sql是否没找到,即判断游标是否指向最后一位
			// -- 是 : 就关闭游标,Toast提示 "您输入的公交线路不存在!"
			// -- 否 : 就把查到满足条件的 数据库列表中opposite 所对应的公交线路用split()方法分割成数组
			if (cursor.isAfterLast()) {
				cursor.close();
				// 显示route 的ListView, 但不释放空间
				lvRoute.setVisibility(View.INVISIBLE);
				Toast.makeText(getActivity(), "您输入的公交线路不存在!",
						Toast.LENGTH_SHORT).show();
			} else {
				stations = cursor.getString(cursor.getColumnIndex("opposite"))
						.split(" - ");
				adapter = new ArrayAdapter<String>(getActivity(),
						R.layout.route_item, stations);

				// 给route 的ListView 设置适配器
				lvRoute.setAdapter(adapter);
			}
			// 关闭游标,释放资源
			cursor.close();

		} else {
			// 显示route 的ListView,但不释放空间
			lvRoute.setVisibility(View.INVISIBLE);
			Toast.makeText(getActivity(), "请重新输入公交线路", Toast.LENGTH_SHORT)
					.show();
		}
	}

}