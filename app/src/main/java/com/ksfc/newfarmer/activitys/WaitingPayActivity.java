/**
 *
 */
package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.FragmentPagerAdapter1;
import com.ksfc.newfarmer.protocol.Request;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 项目名称：QianXihe518 类名称：WaitingPayActivity 类描述： 创建人：王蕾 创建时间：2015-5-29 下午5:31:11
 * 修改备注：
 */
public class WaitingPayActivity extends BaseActivity {
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private ArrayList<String> titleList = new ArrayList<>();
    private TabLayout mTabLayout;
    private int select; //我的新农人中选中的入口

    @Override
    public int getLayout() {
        return R.layout.waitingpay_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我的订单");
        select = getIntent().getIntExtra("orderSelect", 0);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.waitingpay_ViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        initTabs();
        fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentPagerAdapter1(fragmentManager, titleList));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setCurrentItem(select);//设置当前viewpager选中的item
        viewPager.setOffscreenPageLimit(1);//每次加载的item数量
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
        titleList.add("全部");
        titleList.add("待付款");
        titleList.add("待发货");
        titleList.add("已发货");
        titleList.add("已完成");
    }


}
