package com.ksfc.newfarmer.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.CampaignDetailActivity;
import com.ksfc.newfarmer.beans.CampaignListResult;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.transformer.ScaleInTransformer;
import com.squareup.picasso.Picasso;

import net.yangentao.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by CAI on 2016/6/14.
 */
public class ActivityListFragment extends BaseFragment {

    @BindView(R.id.activity_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.activity_list_tips)
    ImageView activity_list_tips;


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
        //设置缓存的页面数量
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ScaleInTransformer());
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

    @OnClick(R.id.close_float)
    void close_float() {
        activity.finish();
    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_CAMPAIGNS) {
            if (req.getData().getStatus().equals("1000")) {
                CampaignListResult reqData = (CampaignListResult) req.getData();
                List<CampaignListResult.CampaignsBean> campaigns = reqData.campaigns;
                if (campaigns != null && !campaigns.isEmpty()) {
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
        public Object instantiateItem(ViewGroup container, int position) {
            View inflate = inflater.inflate(R.layout.item_activity_list, null);
            ImageView iv = (ImageView) inflate.findViewById(R.id.imageView);

            final CampaignListResult.CampaignsBean campaignsBean = campaigns.get(position);
            if (campaignsBean != null) {
                Picasso.with(activity)
                        .load(campaignsBean.image)
                        .placeholder(R.drawable.zhanweitu)
                        .config(Bitmap.Config.RGB_565)
                        .into(iv);
                container.addView(inflate);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //加入看过列表
                        PreferenceUtil pu = new PreferenceUtil(activity, "config");
                        pu.putBool(campaignsBean._id, true);

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
            container.removeView((View) object);
        }

    }

    @Override
    public void OnViewClick(View v) {

    }


    /**
     * 假数据
     */
    public void setData() {
        List<CampaignListResult.CampaignsBean> campaigns = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CampaignListResult.CampaignsBean campaign = new CampaignListResult.CampaignsBean();
            campaign.image = "http://imgsrc.baidu.com/forum/w%3D580/sign=14b323b3f8dcd100cd9cf829428a47be/ac386d600c338744c48ca6c2510fd9f9d62aa0db.jpg";
            switch (i) {
                case 0:
                    campaign.title = "积分商城上线了";
                    campaign.url = "http://192.168.1.21:8070/campaigns/events/rewardShopLaunch";
                    campaign.share_url = "http://192.168.1.21:8070/campaigns/events/rewardShopLaunch";
                    campaign.share_title = "积分商城上线了";
                    campaign.share_abstract = "积分商城上线了";
                    campaign.share_button = false;
                    break;
                case 1:
                    campaign.title = "快来抢积分";
                    campaign.url = "http://192.168.1.21:8070/campaigns/events/shareAndGetPoints";
                    campaign.share_url = "http://192.168.1.21:8070/campaigns/events/shareAndGetPoints";
                    campaign.share_title = "快来抢积分";
                    campaign.share_abstract = "快来抢积分";
                    campaign.share_button = true;
                    break;
                case 2:
                    campaign.title = "答题拿积分";
                    campaign.url = "http://192.168.1.21:8070/campaigns/quizs/jacCarsQuiz";
                    campaign.share_url = "http://192.168.1.21:8070/campaigns/quizs/jacCarsQuiz";
                    campaign.share_title = "答题拿积分";
                    campaign.share_abstract = "答题拿积分";
                    campaign.share_button = true;
                    break;
            }
            campaigns.add(campaign);
        }
        mViewPager.setAdapter(new ActivityListAdapter(campaigns));

    }

}

