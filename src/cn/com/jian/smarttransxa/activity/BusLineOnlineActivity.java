package cn.com.jian.smarttransxa.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.jian.smarttransxa.util.BusLineOverlay;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

/**
 * 在线公交信息查询类
 * 
 * @author Jian
 *
 */
public class BusLineOnlineActivity extends FragmentActivity implements
		OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener,
		BaiduMap.OnMapClickListener {

	// 声明控件
	private Button mBtnPre;
	private Button mBtnNext;
	private EditText etRouteOnline;

	private int nodeIndex = -2;// 节点索引,供浏览节点时使用
	private BusLineResult route;// 保存驾车/步行路线数据的变量，供浏览节点时使用
	private List<String> busLineIDList;
	private int busLineIndex = 0;
	// 搜索相关
	private PoiSearch mSearch; // 搜索模块，也可去掉地图模块独立使用
	private BusLineSearch mBusLineSearch;
	private BaiduMap mBaiduMap;
	BusLineOverlay overlay;// 公交路线绘制对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_busline_online);

		// 获取控件
		initViews();

		// 设置监听器
		setListeners();
	}

	private void initViews() {
		mBtnPre = (Button) findViewById(R.id.btn_busline_pre);
		mBtnNext = (Button) findViewById(R.id.btn_busline_next);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		etRouteOnline = (EditText) findViewById(R.id.et_route_online);
		mBaiduMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.bmapView)).getBaiduMap();
		// 设定地图初始中心点坐标（西安邮电大学长安校区）
		LatLng xayd = new LatLng(34.162719, 108.907775);
		// 设定地图状态（设定初始中心点和缩放级数）
		MapStatus mMapStatus = new MapStatus.Builder().target(xayd).zoom(12)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);

		// 设置地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		
	}

	private void setListeners() {
		mBaiduMap.setOnMapClickListener(this);
		mSearch = PoiSearch.newInstance();
		mSearch.setOnGetPoiSearchResultListener(this);
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
		busLineIDList = new ArrayList<String>();
		overlay = new BusLineOverlay(mBaiduMap);
		mBaiduMap.setOnMarkerClickListener(overlay);

	}

	/**
	 * 发起检索
	 * 
	 * @param v
	 */
	public void searchRouteProcess(View v) {
		busLineIDList.clear();
		busLineIndex = 0;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
		mSearch.searchInCity((new PoiCitySearchOption()).city("西安").keyword(
				etRouteOnline.getText().toString()));
	}

	public void SearchOppositeBusline(View v) {
		if (busLineIndex >= busLineIDList.size()) {
			busLineIndex = 0;
		}
		if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
				&& busLineIDList.size() > 0) {
			mBusLineSearch.searchBusLine((new BusLineSearchOption().city("西安")
					.uid(busLineIDList.get(busLineIndex))));

			busLineIndex++;
		}
	}

	/**
	 * 节点浏览示例
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {

		if (nodeIndex < -1 || route == null
				|| nodeIndex >= route.getStations().size())
			return;
		TextView popupText = new TextView(this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xff000000);
		// 上一个节点
		if (mBtnPre.equals(v) && nodeIndex > 0) {
			// 索引减
			nodeIndex--;
		}
		// 下一个节点
		if (mBtnNext.equals(v) && nodeIndex < (route.getStations().size() - 1)) {
			// 索引加
			nodeIndex++;
		}
		if (nodeIndex >= 0) {
			// 移动到指定索引的坐标
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
					.getStations().get(nodeIndex).getLocation()));
			// 弹出泡泡
			popupText.setText(route.getStations().get(nodeIndex).getTitle());
			mBaiduMap.showInfoWindow(new InfoWindow(popupText, route
					.getStations().get(nodeIndex).getLocation(), 0));
		}
	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(BusLineOnlineActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		mBaiduMap.clear();
		route = result;
		nodeIndex = -1;
		overlay.removeFromMap();
		overlay.setData(result);
		overlay.addToMap();
		overlay.zoomToSpan();
		mBtnPre.setVisibility(View.VISIBLE);
		mBtnNext.setVisibility(View.VISIBLE);
		Toast.makeText(BusLineOnlineActivity.this, result.getBusLineName(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(BusLineOnlineActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// 遍历所有poi，找到类型为公交线路的poi
		busLineIDList.clear();
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_LINE
					|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
				busLineIDList.add(poi.uid);
			}
		}
		SearchOppositeBusline(null);
		route = null;

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	@Override
	public void onMapClick(LatLng result) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi result) {
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mBusLineSearch.destroy();
		super.onDestroy();
	}
}
