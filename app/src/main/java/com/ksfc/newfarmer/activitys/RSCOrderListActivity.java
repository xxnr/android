package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.RSCOrderListFragmentPagerAdapter;
import com.ksfc.newfarmer.fragment.RscOrderDetailFragment;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.widget.UnSwipeViewPager;

import net.yangentao.util.msg.MsgCenter;

import java.util.ArrayList;

/**
 * Created by HePeng on 2016/3/23.
 */
public class RSCOrderListActivity extends BaseActivity implements RscOrderDetailFragment.BgSwitch {
    private UnSwipeViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout mTabLayout;
    private ArrayList<String> titleList = new ArrayList<>();
    private RelativeLayout pop_bg;


    @Override
    public int getLayout() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("服务站订单");
        initView();
        showRightImage();
        setRightImage(R.drawable.search_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.activityForward(RSCOrderListActivity.this, RscSearchOrderActivity.class, null, false);
                int version = Integer.valueOf(android.os.Build.VERSION.SDK);
                if (version > 5) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });


    }

    private void initView() {
        viewPager = (UnSwipeViewPager) findViewById(R.id.waitingpay_ViewPager);
        viewPager.setScanScroll(false);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        pop_bg = (RelativeLayout) findViewById(R.id.pop_bg);
        initTabs();
        fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new RSCOrderListFragmentPagerAdapter(fragmentManager, titleList));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(1);//每次加载的item数量
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //通知 订单列表刷新
                MsgCenter.fireNull(MsgID.rsc_swipe_reFlash, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        titleList.add("待审核");
        titleList.add("待配送");
        titleList.add("待自提");
    }

    @Override
    public void backgroundSwitch(int bg) {

        PopWindowUtils.setBackgroundBlack(pop_bg, bg);
    }


}
