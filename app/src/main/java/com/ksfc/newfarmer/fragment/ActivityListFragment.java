package com.ksfc.newfarmer.fragment;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ksfc.newfarmer.App;
import com.ksfc.newfarmer.EventBaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.CampaignDetailActivity;
import com.ksfc.newfarmer.beans.CampaignListResult;
import com.ksfc.newfarmer.event.CampaignListEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.transformer.ScaleAlphaInTransformer;
import com.squareup.picasso.Picasso;

import net.yangentao.util.PreferenceUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by CAI on 2016/6/14.
 */
public class ActivityListFragment extends EventBaseFragment {

    @BindView(R.id.activity_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.activity_list_tips)
    ImageView activity_list_tips;
    @BindView(R.id.content_frame)
    FrameLayout content_frame;
    private List<CampaignListResult.CampaignsBean> campaigns;


    public static ActivityListFragment newInstance() {
        return new ActivityListFragment();
    }

    @Override
    public View InItView() {
        View inflate = inflater.inflate(R.layout.fragment_activity_list, null);
        ButterKnife.bind(this, inflate);
        init();
        //获取活动列表
        execApi(ApiType.GET_CAMPAIGNS.setMethod(ApiType.RequestMethod.GET), null);
        return inflate;
    }

    private void init() {
        //设置Page间间距
        mViewPager.setPageMargin(Utils.dip2px(activity, 24));
        //设置释放父组件触摸事件给viewpager
        content_frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        mViewPager.setPageTransformer(true, new ScaleAlphaInTransformer());

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

        if (campaigns != null && !campaigns.isEmpty()) {
            //设置缓存的页面数量
            mViewPager.setOffscreenPageLimit(campaigns.size());
            mViewPager.setAdapter(new ActivityListAdapter(campaigns));
        }

        //第一次展示tips
        PreferenceUtil pu = new PreferenceUtil(activity, App.SPNAME);
        boolean firstInMine = pu.getBool("firstActivityList", true);
        if (firstInMine) {
            activity_list_tips.setVisibility(View.VISIBLE);
        } else {
            activity_list_tips.setVisibility(View.INVISIBLE);
        }
        pu.putBool("firstActivityList", false);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(CampaignListEvent event) {
        if (event.campaigns != null && !event.campaigns.isEmpty()) {
            campaigns = event.campaigns;
        }
    }

    @OnClick(R.id.close_float)
    void close_float() {
        activity.finish();
    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_CAMPAIGNS) {
            if (req.getData().getStatus().equals("1000")) {
                CampaignListResult reqData = (CampaignListResult) req.getData();
                campaigns = reqData.campaigns;
                if (campaigns != null && !campaigns.isEmpty()) {
                    //设置缓存的页面数量
                    mViewPager.setOffscreenPageLimit(campaigns.size());
                    mViewPager.setAdapter(new ActivityListAdapter(campaigns));
                } else {
                    mViewPager.setAdapter(null);
                }
            }

        }
    }

    class ActivityListAdapter extends PagerAdapter {
        private List<CampaignListResult.CampaignsBean> campaigns;

        public ActivityListAdapter(List<CampaignListResult.CampaignsBean> campaigns) {
            this.campaigns = campaigns;
        }

        @Override
        public int getCount() {
            return campaigns.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View inflate = inflater.inflate(R.layout.item_activity_list, null);
            ImageView iv = (ImageView) inflate.findViewById(R.id.imageView);


            final CampaignListResult.CampaignsBean campaignsBean = campaigns.get(position);
            if (campaignsBean != null) {
                Picasso.with(activity)
                        .load(campaignsBean.image)
                        .resize(Utils.dip2px(activity, 245), Utils.dip2px(activity, 340))
                        .placeholder(R.drawable.campaign_loading_icon)
                        .error(R.drawable.campaign_loading_icon)
                        .into(iv);
                container.addView(inflate);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //加入看过列表
                        if (position==0){
                            PreferenceUtil pu = new PreferenceUtil(activity, App.CAMPAIGN);
                            pu.putBool(campaignsBean._id, true);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("campaign", campaignsBean);
                        IntentUtil.activityForward(activity, CampaignDetailActivity.class, bundle, true);
                    }
                });
            }
            return inflate;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }


        @Override
        public float getPageWidth(int position) {
            return super.getPageWidth(position);
        }
    }

    @Override
    public void OnViewClick(View v) {

    }



}

