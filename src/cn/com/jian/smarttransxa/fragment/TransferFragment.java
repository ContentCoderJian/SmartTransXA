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
import cn.com.jian.smarttransxa.domain.Bus;

/**
 * transfer 换乘查询
 * 
 * @author Jian
 * 
 */
public class TransferFragment extends Fragment {

	// 声明station4个控件和数据库对象
	private EditText etTransferStart;
	private EditText etTransferEnd;
	private Button btnTransferSearch;
	private ListView lvTransfer;
	private BusDatabase dbfile;
	private SQLiteDatabase database;

	// 声明 Adapter 和集合
	public ArrayAdapter<String> adapter;
	private List<String> result = new ArrayList<String>();

	public TransferFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Log.d("smartTransXA", "TransferFragment");
		View v = inflater.inflate(R.layout.fragment_trans_transfer, container,
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
		etTransferStart = (EditText) v.findViewById(R.id.et_transfer_start);
		etTransferEnd = (EditText) v.findViewById(R.id.et_transfer_end);
		btnTransferSearch = (Button) v.findViewById(R.id.btn_transfer_search);
		lvTransfer = (ListView) v.findViewById(R.id.lv_transfer);
	}

	private void setListeners() {
		OnClickListener listener = new InnerOnClickListener();
		btnTransferSearch.setOnClickListener(listener);

	}

	private class InnerOnClickListener implements OnClickListener {

		// 声明一条线的所有站点
		private String[] routeAllStationStart;
		private String[] routeAllStationEnd;

		private String resultRouteStation;
		private String stations;

		public void onClick(View v) {
			// 声明一个标记变量
			boolean state = true;

			// // 获取两个输入框的字符串
			String busStationStart = etTransferStart.getText().toString()
					.trim();
			String busStationEnd = etTransferEnd.getText().toString().trim();

			// 获取站点的所有公交对象
			Bus busRouteStart = getBusMessage(busStationStart);
			Bus busRouteEnd = getBusMessage(busStationEnd);

			// 判断bus对象不为空,即数据库中是否找到该站点
			if ("".equals(busStationStart) || "".equals(busStationEnd)) {
				lvTransfer.setVisibility(View.INVISIBLE);
				Toast.makeText(getActivity(), "起始站  \\ 终点站未输入!",
						Toast.LENGTH_SHORT).show();
			} else {
				lvTransfer.setVisibility(View.VISIBLE);
				// 判断两个输入框是否为空
				// -- 是 ： Toast提示 "起始站 \ 终点站未输入!"
				// -- 否 ： 接着执行下一步
				if (busRouteStart == null || busRouteEnd == null) {
					lvTransfer.setVisibility(View.INVISIBLE);
					Toast.makeText(getActivity(), "抱歉, 您输入的站点不存在!",
							Toast.LENGTH_SHORT).show();
				} else {

					String[] lineStart = busRouteStart.getLine().split(",");
					String[] lineEnd = busRouteEnd.getLine().split(",");

					// 遍历2个线路数组,判断是同一条线路的就连
					result.clear();
					for (int i = 0; i < lineStart.length; i++) {
						for (int j = 0; j < lineEnd.length; j++) {
							if (lineStart[i].equals(lineEnd[j])) {
								// Log.i("--- start ---", lineStart[i]);
								result.add(" <" + lineStart[i] + "路> 直达");
								state = false;
							}
						}
					}

					// 不在同一条线路上
					if (state) {
						lvTransfer.setVisibility(View.VISIBLE);
						result.clear();

						// 将连在一起的站点分成一条线路的所有站点
						String[] stationStartDiff = busRouteStart.getStation()
								.split(",");
						String[] stationEndDiff = busRouteEnd.getStation()
								.split(",");

						// 将连在一起的线路分成一个个的线路数组
						String[] routeStartDiff = busRouteStart.getLine()
								.split(",");
						String[] routeEndDiff = busRouteEnd.getLine()
								.split(",");

						// 遍历所有站点,再拆分成一个个的站点数组
						for (int i = 0; i < stationStartDiff.length; i++) {
							// 获得每个站点
							routeAllStationStart = stationStartDiff[i]
									.split(" - ");
							// Log.d("routeAllStationStart",
							// Arrays.toString(routeAllStationStart));
							for (int j = 0; j < stationEndDiff.length; j++) {
								routeAllStationEnd = stationEndDiff[j]
										.split(" - ");
								// Log.d("--- routeAllStationEnd ---",
								// Arrays.toString(routeAllStationEnd));

								stations = "";
								resultRouteStation = "";
								// 遍历起始站和终点站的所有站点,找到相同的站点
								for (int x = 0; x < routeAllStationStart.length; x++) {
									for (int y = 0; y < routeAllStationEnd.length; y++) {
										// 当站点相同时,再拼接上线路和站点的信息
										if (routeAllStationStart[x]
												.equals(routeAllStationEnd[y])) {
											stations += " "
													+ routeAllStationStart[x]
													+ " ";
											// Log.v("--- starts ---",
											// routeAllStationStart[x]);
											resultRouteStation = "乘坐 <"
													+ routeStartDiff[i]
													+ "路> 在" + stations
													+ "站换乘 <" + routeEndDiff[j]
													+ "> 路可到达";
										}
									}
								}
								// 站点被查询到存在才添加到集合中
								if (!"".equals(stations)) {
									result.add(resultRouteStation);
								}
							}
						}
					}
				}
				adapter = new ArrayAdapter<String>(getActivity(),
						R.layout.transfer_item, result);
				lvTransfer.setAdapter(adapter);

			}
		}

	}

	/**
	 * 
	 * 从数据库中获取公交信息并封装到bus对象中
	 * 
	 * @param busStation
	 *            公交站点
	 * @return 封装好的bus 对象
	 */
	private Bus getBusMessage(String busStation) {
		Bus bus = new Bus();
		StringBuffer lines = new StringBuffer();
		StringBuffer station = new StringBuffer();

		Cursor cursor = database.rawQuery(
				"select * from bus_line where station like '%" + busStation
						+ "%'", null);

		if (cursor.moveToNext()) {
			while (cursor.moveToNext()) {
				bus.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				lines.append(cursor.getString(1));
				lines.append(",");
				station.append(cursor.getString(3));
				station.append(",");
			}
			bus.setLine(lines.toString());
			bus.setStation(station.toString());
		} else {
			bus = null;
		}
		cursor.close();
		return bus;
	}

}
