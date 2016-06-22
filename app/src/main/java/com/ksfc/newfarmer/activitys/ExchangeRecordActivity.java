package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.GiftOrderListFragment;
import com.ksfc.newfarmer.protocol.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/21.
 */
public class ExchangeRecordActivity extends BaseActivity {
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private List<String> titleList;

    @Override
    public int getLayout() {
        return R.layout.exchange_record_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("兑换记录");
        titleList = new ArrayList<>();
        initTabs();

        GiftOrderListAdapter adapter = new GiftOrderListAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    /**
     * 添加title
     */
    private void initTabs() {
        titleList.add("未完成");
        titleList.add("已完成");
    }

    class GiftOrderListAdapter extends FragmentPagerAdapter {

        public GiftOrderListAdapter(FragmentManager fm) {
            super(fm);
        }

        // 根据下标返回碎片并对碎片进行传值
        @Override
        public Fragment getItem(int position) {
            GiftOrderListFragment fragment = new GiftOrderListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }


}
