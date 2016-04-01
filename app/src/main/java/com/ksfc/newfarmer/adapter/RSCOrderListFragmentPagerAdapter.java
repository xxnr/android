/**
 *
 */
package com.ksfc.newfarmer.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ksfc.newfarmer.fragment.RscOrderDetailFragment;

import java.util.ArrayList;

/**
 * 项目名称：newFarmer 类名称：FragmentPagerAdapter1 类描述： 创建人：王蕾 创建时间：2015-5-30 下午9:52:59
 * 修改备注：
 */
public class RSCOrderListFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> titles;

    public RSCOrderListFragmentPagerAdapter(FragmentManager fm, ArrayList<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int arg0) {
        RscOrderDetailFragment fragment = new RscOrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TYPE", arg0);//创建fragment的时候传值
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
