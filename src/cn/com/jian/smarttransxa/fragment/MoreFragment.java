package cn.com.jian.smarttransxa.fragment;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.jian.smarttransxa.activity.R;

/**
 * more 更多
 * 
 * @author Jian
 * 
 */
public class MoreFragment extends Fragment {

	// 声明more 的 ListView 对象,adapter,集合
	private ListView lvMore;
	private ArrayAdapter<String> adapter;
	private List<String> list;

	public MoreFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Log.d("smartTransXA", "MoreFragment");
		View v = inflater.inflate(R.layout.fragment_trans_more, container,
				false);

		// 获取控件
		initViews(v);

		// 配置监听器
		setListeners();

		// 添加集合
		addList();

		// 设置adapter适配器
		setAdapter();

		return v;
	}

	private void initViews(View v) {
		lvMore = (ListView) v.findViewById(R.id.lv_more);
	}

	private void addList() {
		list = new LinkedList<String>();
		list.add("软件说明 ");
		list.add("离线模块可以离线查询公交的站点、线路和换乘信息");
		list.add("此离线公交线路数据基于爱帮公交的数据");
		list.add("在使用中如若发现bug，欢迎您给我发邮件!");
		list.add("获取更新");
		list.add("联系作者");
		list.add("mr.lv.arrow@gmail.com");

	}

	private void setListeners() {
		OnItemClickListener listener = new InnerOnItemClickListener();
		lvMore.setOnItemClickListener(listener);

	}

	private void setAdapter() {
		adapter = new ArrayAdapter<>(getActivity(), R.layout.more_item, list);
		lvMore.setAdapter(adapter);
	}

	private class InnerOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Toast.makeText(getActivity(), "已是最新版本", Toast.LENGTH_SHORT).show();

		}
	}

}
