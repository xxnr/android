/**
 *
 */
package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonFragmentPagerAdapter;
import com.ksfc.newfarmer.event.MainTabSelectEvent;
import com.ksfc.newfarmer.event.OrderListSwipeEvent;
import com.ksfc.newfarmer.fragment.MyOrderListFragment;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.widget.UnSwipeViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;


import org.greenrobot.eventbus.EventBus;

/**
 * 项目名称：QianXihe518 类名称：MyOrderListActivity 类描述： 创建人：王蕾 创建时间：2015-5-29 下午5:31:11
 * 修改备注：
 */
public class MyOrderListActivity extends BaseActivity implements MyOrderListFragment.BgSwitch, ViewPager.OnPageChangeListener {
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int select; //我的新农人中选中的入口
    private RelativeLayout pop_bg;

    @Override
    public int getLayout() {
        return R.layout.activity_order_list;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我的订单");
        select = getIntent().getIntExtra("orderSelect", 0);
        initView();
        setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderListActivity.this, MainActivity.class);
                intent.putExtra("id", MainActivity.Tab.MINE);
                startActivity(intent);
                EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.MINE));
                finish();
            }
        });
    }
    //设置返回监听的，回传数据
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(MyOrderListActivity.this, MainActivity.class);
            intent.putExtra("id", MainActivity.Tab.MINE);
            startActivity(intent);
            EventBus.getDefault().post(new MainTabSelectEvent(MainActivity.Tab.MINE));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        UnSwipeViewPager viewPager = (UnSwipeViewPager) findViewById(R.id.waitingpay_ViewPager);
        viewPager.setScanScroll(false);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);
        if (fragments.isEmpty()) {
            initTabsAndFragments();
        }
        viewPager.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), titleList, fragments));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setCurrentItem(select);//设置当前viewpager选中的item
        viewPager.setOffscreenPageLimit(1);//每次加载的item数量
        viewPager.addOnPageChangeListener(this);

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
    private void initTabsAndFragments() {
        titleList.add("全部");
        titleList.add("待付款");
        titleList.add("待发货");
        titleList.add("待收货");
        titleList.add("已完成");

        for (int i = 0; i < titleList.size(); i++) {
            fragments.add(MyOrderListFragment.newInstance(i));
        }
    }


    @Override
    public void backgroundSwitch(int bg) {
        PopWindowUtils.setBackgroundBlack(pop_bg, bg);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //通知 订单列表刷新
        EventBus.getDefault().post(new OrderListSwipeEvent(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
