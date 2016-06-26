package cn.com.jian.smarttransxa.adapter;

import cn.com.jian.smarttransxa.activity.OutLineActivity;
import cn.com.jian.smarttransxa.fragment.MoreFragment;
import cn.com.jian.smarttransxa.fragment.RouteFragment;
import cn.com.jian.smarttransxa.fragment.StationFragment;
import cn.com.jian.smarttransxa.fragment.TransferFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * viewpager   ≈‰∆˜¿‡
 * 
 * @author Jian
 *
 */
public class OutlineFragmentAdapter extends FragmentPagerAdapter {

	public final static int TAB_COUNT = 4;

	public OutlineFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int id) {
		switch (id) {
		case OutLineActivity.TAB_STATION:
			StationFragment stationFragment = new StationFragment();
			return stationFragment;
		case OutLineActivity.TAB_ROUTE:
			RouteFragment routeFragment = new RouteFragment();
			return routeFragment;
		case OutLineActivity.TAB_TRANSFER:
			TransferFragment transferFragment = new TransferFragment();
			return transferFragment;
		case OutLineActivity.TAB_MORE:
			MoreFragment moreFragment = new MoreFragment();
			return moreFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}
}