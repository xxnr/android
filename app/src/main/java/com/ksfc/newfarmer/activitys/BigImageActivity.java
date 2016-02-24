package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.BigImageFragment;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.ksfc.newfarmer.widget.HackyViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by HePeng on 2015/12/7.
 */
public class BigImageActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.big_image_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        HackyViewPager viewPager = ((HackyViewPager) findViewById(R.id.viewPager_big_image));
        CirclePageIndicator indicator=(CirclePageIndicator)findViewById(R.id.circlePageIndicator);
        GetGoodsDetail.GoodsDetail detail = (GetGoodsDetail.GoodsDetail) getIntent().getSerializableExtra("detail");
        int position = getIntent().getIntExtra("position", 0);
        if (detail != null && detail.pictures != null) {
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), detail.pictures);
            viewPager.setAdapter(myPagerAdapter);
            viewPager.setOffscreenPageLimit(1);
            if (detail.pictures.size()>1){
                indicator.setViewPager(viewPager);
                viewPager.setCurrentItem(position);
            }else {
                indicator.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private List<GetGoodsDetail.GoodsDetail.Pictures> pictures;

        public MyPagerAdapter(FragmentManager fm, List<GetGoodsDetail.GoodsDetail.Pictures> pictures) {
            super(fm);
            this.pictures = pictures;
        }

        @Override
        public Fragment getItem(int position) {
            BigImageFragment fragment = new BigImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("picture", pictures.get(position).originalUrl);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return pictures.size();
        }
    }
}
