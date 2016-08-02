package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.GetGoodsDetail;
import com.ksfc.newfarmer.common.CommonFragmentPagerAdapter;
import com.ksfc.newfarmer.event.BigImageEvent;
import com.ksfc.newfarmer.fragment.BigImageFragment;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.widget.CirclePageIndicator;
import com.ksfc.newfarmer.widget.HackyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by HePeng on 2015/12/7.
 * 商品详情查看大图
 */
public class BigImageActivity extends BaseActivity {


    @BindView(R.id.viewPager_big_image)
    HackyViewPager viewPager;
    @BindView(R.id.circlePageIndicator)
    CirclePageIndicator indicator;

    public static final String POSITION = "POSITION";
    public static final String DETAIL = "DETAIL";

    @Override
    public int getLayout() {
        return R.layout.activity_big_image;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt(POSITION, 0);
            GetGoodsDetail.GoodsDetail detail = (GetGoodsDetail.GoodsDetail) bundle.getSerializable(DETAIL);

            if (detail != null && detail.pictures != null) {
                List<Fragment> fragments = new ArrayList<>();
                for (int i = 0; i < detail.pictures.size(); i++) {
                    GetGoodsDetail.GoodsDetail.Pictures picture = detail.pictures.get(i);
                    if (picture != null) {
                        fragments.add(BigImageFragment.newInstance(picture.originalUrl, i));
                    }
                }
                viewPager.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments));
                viewPager.setOffscreenPageLimit(detail.pictures.size());
                if (detail.pictures.size() > 1) {
                    indicator.setVisibility(View.VISIBLE);
                    indicator.setViewPager(viewPager);
                    viewPager.setCurrentItem(position);
                } else {
                    indicator.setVisibility(View.GONE);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateItem(BigImageEvent event) {
        indicator.setVisibility(View.GONE);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimationUtils.setActivityAnimation(this, 0, 0);
    }

    @Override
    public void onBackPressed() {
        indicator.setVisibility(View.GONE);
        EventBus.getDefault().post(new BigImageEvent(viewPager.getCurrentItem()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
