/**
 *
 */
package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonFragmentPagerAdapter;
import com.ksfc.newfarmer.fragment.MyOrderListFragment;
import com.ksfc.newfarmer.fragment.RscGiftOrderListFragment;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.widget.UnSwipeViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import net.yangentao.util.msg.MsgCenter;

/**
 * 项目名称：QianXihe518 类名称：MyOrderListActivity 类描述： 创建人：王蕾 创建时间：2015-5-29 下午5:31:11
 * 修改备注：
 */
public class MyOrderListActivity extends BaseActivity implements MyOrderListFragment.BgSwitch, ViewPager.OnPageChangeListener {
    private UnSwipeViewPager viewPager;
    private TabLayout mTabLayout;
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
                if (getIntent().getBooleanExtra("callByDetailActivity", false)) {
                    Intent intent = new Intent(MyOrderListActivity.this, MainActivity.class);
                    startActivity(intent);
                    MsgCenter.fireNull(MsgID.MainActivity_select_tab,  MainActivity.Tab.MINE);
                }
                finish();
            }
        });
    }

    private void initView() {
        viewPager = (UnSwipeViewPager) findViewById(R.id.waitingpay_ViewPager);
        viewPager.setScanScroll(false);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);
        if (fragments.isEmpty()){
            initTabsAndFragments();
        }
        viewPager.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), titleList,fragments));
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
        MsgCenter.fireNull(MsgID.swipe_reFlash, position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
