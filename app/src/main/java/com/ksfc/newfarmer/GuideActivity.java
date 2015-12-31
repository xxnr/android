package com.ksfc.newfarmer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * 三张启动页
 * 
 * @author Bruce.wang
 * 
 */
public class GuideActivity extends FragmentActivity {

	private ViewPager viewPager_guide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initView();
	}

	public void initView() {
		viewPager_guide = (ViewPager) findViewById(R.id.viewPager_guide);
		List<Fragment> fragmentsList = new ArrayList<Fragment>();
		for (int i = 0; i < 2; i++) {
			Fragment fragment = new GuideFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("index", i);
			fragment.setArguments(bundle);
			fragmentsList.add(fragment);
		}
		viewPager_guide.setAdapter(new GuideAdapter(
				getSupportFragmentManager(), fragmentsList));
		viewPager_guide.setCurrentItem(0);
	}

	class GuideAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragmentsList = null;

		public GuideAdapter(FragmentManager fm) {
			super(fm);
		}

		public GuideAdapter(FragmentManager fm, List<Fragment> fragmentsList) {
			super(fm);
			this.fragmentsList = fragmentsList;
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentsList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

	}
}
