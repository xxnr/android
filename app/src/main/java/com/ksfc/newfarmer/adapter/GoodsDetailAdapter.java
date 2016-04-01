package com.ksfc.newfarmer.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ksfc.newfarmer.fragment.GoodsDetailFragment;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;

/**
 * Created by HePeng on 2015/11/23. 商品详情的适配器
 */
public class GoodsDetailAdapter extends FragmentPagerAdapter {
    private int count;
    private GetGoodsDetail.GoodsDetail detail;

    public GoodsDetailAdapter(FragmentManager fm, int count, GetGoodsDetail.GoodsDetail detail) {
        super(fm);
        this.count = count;
        this.detail = detail;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Fragment getItem(int position) {

        GoodsDetailFragment fragment = new GoodsDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("count", count);
        bundle.putSerializable("detail", detail);//创建fragment的时候传值
        fragment.setArguments(bundle);
        return fragment;
    }

    //不销毁fragment
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

}
