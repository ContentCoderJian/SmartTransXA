package cn.com.jian.smarttransxa.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.jian.smarttransxa.util.OverlayManager;
import cn.com.jian.smarttransxa.util.TransitRouteOverlay;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

/**
 * 在线换乘查询类
 * 
 * @author Jian
 * 
 */
public class TransferOnlineActivity extends Activity implements
		BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {
	// 声明控件
	private EditText etStart;
	private EditText etEnd;

	// 声明百度地图相关组件
	private RoutePlanSearch mSearch;
	private BaiduMap mBaidumap;
	RouteLine<?> route;
	MapView mMapView;

	// 浏览路线节点相关
	Button mBtnPre; // 上一个节点
	Button mBtnNext; // 下一个节点
	int nodeIndex = -1; // 节点索引,供浏览节点时使用
	boolean useDefaultIcon = false;
	private TextView popupText; // 泡泡view
	OverlayManager routeOverlay;

	// 西安经纬度 34.26667, 108.95000
	// 邮电学院南校区 34.162719, 108.907775
	// 兵马俑 34.372371, 109.292528
	// protected LatLng stLatLng = new LatLng(34.162719, 108.907775);
	// protected LatLng enLatLng = new LatLng(34.372371, 109.292528);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_trans_online);

		// 获取控件
		initViews();

		// 设置监听器
		setListeners();
	}

	private void initViews() {
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		etStart = (EditText) findViewById(R.id.et_online_transstart);
		etEnd = (EditText) findViewById(R.id.et_online_transend);

		// 初始化地图
		mMapView = (MapView) findViewById(R.id.map);

		mBaidumap = mMapView.getMap();

		// 设定地图初始中心点坐标（西安邮电大学长安校区）
		LatLng xayd = new LatLng(34.162719, 108.907775);
		// 设定地图状态（设定初始中心点和缩放级数）
		MapStatus mMapStatus = new MapStatus.Builder().target(xayd).zoom(12)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);

		// 设置地图状态
		mBaidumap.setMapStatus(mMapStatusUpdate);

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();

	}

	private void setListeners() {
		mBaidumap.setOnMapClickListener(this);
		mSearch.setOnGetRoutePlanResultListener(this);
	}

	/**
	 * 换乘方案按钮选择
	 * 
	 * @param v
	 */

	public void searchButtonProcess(View v) {
		// 重置浏览节点的路线数据
		route = null;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

		mBaidumap.clear();
		// 设置起、终点信息,设置城市名没用，默认北京
		// PlanNode stNode = PlanNode.withCityNameAndPlaceName("西安", etStart
		// .getText().toString());
		// PlanNode enNode = PlanNode.withCityNameAndPlaceName("西安", etEnd
		// .getText().toString());
		PlanNode stNode = PlanNode.withCityCodeAndPlaceName(233, etStart
				.getText().toString().trim());
		PlanNode enNode = PlanNode.withCityCodeAndPlaceName(233, etEnd
				.getText().toString().trim());

		// 通过坐标设置起、终点信息
		// PlanNode stNode = PlanNode.withLocation(stLatLng);
		// PlanNode enNode = PlanNode.withLocation(enLatLng);

		if (v.getId() == R.id.btn_search_timefirst) {
			mSearch.transitSearch((new TransitRoutePlanOption())
					.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TIME_FIRST)
					.from(stNode).city("西安").to(enNode));
		} else if (v.getId() == R.id.btn_search_transferfirst) {
			mSearch.transitSearch((new TransitRoutePlanOption())
					.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST)
					.from(stNode).city("西安").to(enNode));
		} else if (v.getId() == R.id.btn_search_walkfirst) {
			mSearch.transitSearch((new TransitRoutePlanOption())
					.policy(TransitRoutePlanOption.TransitPolicy.EBUS_WALK_FIRST)
					.from(stNode).city("西安").to(enNode));
		} else if (v.getId() == R.id.btn_search_nosubway) {
			mSearch.transitSearch((new TransitRoutePlanOption())
					.policy(TransitRoutePlanOption.TransitPolicy.EBUS_NO_SUBWAY)
					.from(stNode).city("西安").to(enNode));
		}
	}

	/**
	 * 节点浏览示例
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance()
				.getLocation();
		nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(TransferOnlineActivity.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(TransferOnlineActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
			mBaidumap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 定制RouteOverly
	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		mBaidumap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}

}
