package com.ksfc.newfarmer.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonFragmentPagerAdapter;
import com.ksfc.newfarmer.event.RscSwipeEvent;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.widget.UnSwipeViewPager;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/7/6.
 */
public class RscOrderFragment extends BaseFragment implements ViewPager.OnPageChangeListener {


    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.waitingpay_ViewPager)
    UnSwipeViewPager viewPager;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();


    @Override
    public View InItView() {
        View inflate = inflater.inflate(R.layout.fragment_rsc_order, null);
        ButterKnife.bind(this, inflate);
        if (fragments.isEmpty()) {
            initTabsAndFragments();
        }
        viewPager.setScanScroll(false);
        viewPager.setAdapter(new CommonFragmentPagerAdapter(getChildFragmentManager(), titleList, fragments));
        mTabLayout.setupWithViewPager(viewPager);//设置联动
        viewPager.setOffscreenPageLimit(1);//每次加载的item数量
        viewPager.addOnPageChangeListener(this);
        return inflate;
    }

    @Override
    public void onResponsed(Request req) {
    }

    @Override
    public void OnViewClick(View v) {
    }


    /**
     * 添加title
     */
    private void initTabsAndFragments() {
        titleList.add("全部");
        titleList.add("待付款");
        titleList.add("待审核");
        titleList.add("待配送");
        titleList.add("待自提");

        for (int i = 0; i < titleList.size(); i++) {
            fragments.add(RscOrderListFragment.newInstance(i));
        }
    }


    @Override
    public void onPageSelected(int position) {
        //通知 订单列表刷新
        EventBus.getDefault().post(new RscSwipeEvent(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
