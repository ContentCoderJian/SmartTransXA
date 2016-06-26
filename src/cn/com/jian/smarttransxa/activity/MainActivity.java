package cn.com.jian.smarttransxa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * 主界面,对三个按钮点击监听切换不同布局
 * @author Jian
 *
 */
public class MainActivity extends Activity {

	private ImageButton ibBusLineOnline;
	private ImageButton ibTransferOnline;
	private ImageButton ibSearchOutline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 显示界面
		setContentView(R.layout.activity_main);

		// 获取控件
		initViews();

		// 设置监听器
		setListeners();
	}

	private void initViews() {
		ibBusLineOnline = (ImageButton) findViewById(R.id.ib_busline_online);
		ibTransferOnline = (ImageButton) findViewById(R.id.ib_transfer_online);
		ibSearchOutline = (ImageButton) findViewById(R.id.ib_search_outline);
	}

	private void setListeners() {
		
		ibBusLineOnline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, BusLineOnlineActivity.class));
			}
		});


		ibTransferOnline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, TransferOnlineActivity.class));
			}
		});

		ibSearchOutline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, OutLineActivity.class));
			}
		});
	}

}
