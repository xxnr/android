package com.ksfc.newfarmer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ActivityDetailActivity;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.transformer.ScaleInTransformer;

import net.yangentao.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by CAI on 2016/6/14.
 */
public class ActivityListFragment extends BaseFragment {

    @BindView(R.id.activity_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.close_float)
    ImageView close_float;
    @BindView(R.id.activity_list_tips)
    ImageView activity_list_tips;

    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.close_float) {
            activity.finish();
        }
    }

    public static ActivityListFragment newInstance() {
        return new ActivityListFragment();
    }

    @Override
    public View InItView() {
        View inflate = inflater.inflate(R.layout.fragment_activity_list, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        close_float.setOnClickListener(this);
        int res[] = {R.drawable.yindaoye1, R.drawable.yindaoye2, R.drawable.yindaoye3};
        //设置Page间间距
        mViewPager.setPageMargin(Utils.dip2px(activity, 24));
        //设置缓存的页面数量
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ScaleInTransformer());
        mViewPager.setAdapter(new ActivityListAdapter(res));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                activity_list_tips.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //第一次展示tips
        PreferenceUtil pu = new PreferenceUtil(activity, "config");
        boolean firstInMine = pu.getBool("firstActivityList", true);
        if (firstInMine) {
            activity_list_tips.setVisibility(View.VISIBLE);
        } else {
            activity_list_tips.setVisibility(View.INVISIBLE);
        }
        pu.putBool("firstActivityList", false);

    }


    @Override
    public void onResponsed(Request req) {

    }

    class ActivityListAdapter extends PagerAdapter {
        private int res[];

        public ActivityListAdapter(int[] res) {
            this.res = res;
        }

        @Override
        public int getCount() {
            return res.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = inflater.inflate(R.layout.item_activity_list, null);
            ImageView iv = (ImageView) inflate.findViewById(R.id.imageView);
            iv.setImageResource(res[position]);
            container.addView(inflate);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.activityForward(activity, ActivityDetailActivity.class,null,true);
                }
            });
            return inflate;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}

