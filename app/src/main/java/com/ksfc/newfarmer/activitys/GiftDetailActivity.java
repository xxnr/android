package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.PicassoHelper;
import com.ksfc.newfarmer.event.IsLoginEvent;
import com.ksfc.newfarmer.event.RewardConsumeEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.remoteapi.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.GiftDetailResult;
import com.ksfc.newfarmer.beans.IntegralGetResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.ObservableScrollView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/15.
 */
public class GiftDetailActivity extends BaseActivity {

    @BindView(R.id.gift_detail_img)
    ImageView giftDetailImg;
    @BindView(R.id.gift_detail_title)
    TextView giftDetailTitle;
    @BindView(R.id.gift_detail_integral_tv)
    TextView giftDetailIntegralTv;
    @BindView(R.id.gift_detail_able_integral_tv)
    TextView giftDetailAbleIntegralTv;
    @BindView(R.id.gift_detail_able_integral_text_tv)
    TextView gift_detail_able_integral_text_tv;
    @BindView(R.id.gift_detail_market_price_tv)
    TextView giftDetailMarketPriceTv;
    @BindView(R.id.gift_detail_market_price_ll)
    LinearLayout giftDetailMarketPriceLl;
    @BindView(R.id.gift_detail_webView)
    WebView giftDetailWebView;
    @BindView(R.id.gift_detail_sure_tv)
    TextView giftDetailSureTv;
    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;
    @BindView(R.id.title_bg_up)
    View title_bg_up;
    @BindView(R.id.title_bg_down)
    View title_bg_down;
    @BindView(R.id.title_div)
    View title_div;
    @BindView(R.id.titleview)
    View titleview;
    @BindView(R.id.title_name_text)
    View title_name_text;

    private int score;
    private String id;
    private GiftDetailResult.GiftBean gift;

    @Override
    public int getLayout() {
        return R.layout.activity_gift_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTitle("礼品详情");

        titleview.setBackgroundResource(R.color.transparent);

        title_bg_up.setVisibility(View.INVISIBLE);
        title_name_text.setVisibility(View.INVISIBLE);
        //透明状态栏和设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusHeight = ScreenUtil.getStatusHeight(this);
            title_div.setVisibility(View.VISIBLE);
            title_div.getLayoutParams().height = statusHeight;
            title_bg_down.getLayoutParams().height = Utils.dip2px(GiftDetailActivity.this, 45) + statusHeight;
            title_bg_up.getLayoutParams().height = Utils.dip2px(GiftDetailActivity.this, 45) + statusHeight;
        } else {
            title_div.setVisibility(View.GONE);
            title_bg_down.getLayoutParams().height = Utils.dip2px(GiftDetailActivity.this, 45);
            title_bg_up.getLayoutParams().height = Utils.dip2px(GiftDetailActivity.this, 45);
        }
        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                title_name_text.setVisibility(View.VISIBLE);
                title_bg_up.setVisibility(View.VISIBLE);
                float offset = Utils.px2dip(GiftDetailActivity.this, y) * 0.005f;
                if (0 <= offset && offset <= 1.0f) {
                    if (offset < 0.5f) {
                        title_name_text.setAlpha(0);
                        title_bg_up.setAlpha(0);
                        title_bg_down.setAlpha((1 - offset * 2));
                    } else {
                        title_bg_down.setAlpha(0);
                        title_bg_up.setAlpha((offset - 0.5f) * 2);
                        title_name_text.setAlpha((offset - 0.5f) * 2);
                    }
                } else if (offset > 1.0f) {
                    title_bg_up.setAlpha(1);
                    title_name_text.setAlpha(1);
                    title_bg_down.setAlpha(0);
                }
            }
        });

        ScreenUtil.setHeight(this, giftDetailImg, 360);
        setViewClick(R.id.gift_detail_sure_tv);
        giftDetailSureTv.setEnabled(false);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        id = extras.getString("id");
        score = extras.getInt("score", 0);
        showProgressDialog();
        if (isLogin()){
            RemoteApi.getIntegral(GiftDetailActivity.this);
        }else {
            RemoteApi.getGiftDetail(this, id);
        }
    }

    /**
     * 监听登录事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isLoginEvent(IsLoginEvent event){
        RemoteApi.getIntegral(GiftDetailActivity.this);
    }

    /**
     * 消费通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rewardConsume(RewardConsumeEvent event) {
        RemoteApi.getIntegral(GiftDetailActivity.this);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.gift_detail_sure_tv:
                if (isLogin()) {
                    if (gift != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("gift", gift);
                        IntentUtil.activityForward(this, RewardGiftSubmitActivity.class, bundle, false);
                    }
                } else {
                    startActivity(LoginActivity.class);
                }
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_INTEGRAL) {
            IntegralGetResult data = (IntegralGetResult) req.getData();
            if (data.datas != null) {
                score = data.datas.score;
                RemoteApi.getGiftDetail(this, id);
            }
        } else if (ApiType.GET_GIFT_DETAIL == req.getApi()) {
            GiftDetailResult reqData = (GiftDetailResult) req.getData();
            gift = reqData.gift;
            if (gift != null) {
                PicassoHelper.setImageRes(GiftDetailActivity.this, gift.originalUrl, giftDetailImg);
                giftDetailTitle.setText(StringUtil.checkStr(gift.name) ? gift.name : "");
                giftDetailIntegralTv.setText(String.valueOf(gift.points));
                giftDetailAbleIntegralTv.setText(String.valueOf(score));
                try {
                    if (Double.parseDouble(gift.marketPrice) != 0) {
                        giftDetailMarketPriceTv.setText(gift.marketPrice);
                        giftDetailMarketPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                    } else {
                        giftDetailMarketPriceLl.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    giftDetailMarketPriceLl.setVisibility(View.GONE);
                }


                //礼品介绍
                giftDetailWebView.getSettings().setJavaScriptEnabled(true);
                giftDetailWebView.loadUrl(gift.appbody_url);
                giftDetailWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                if (!gift.online) {
                    giftDetailSureTv.setEnabled(false);
                    giftDetailSureTv.setText("已下架");
                    gift_detail_able_integral_text_tv.setVisibility(View.INVISIBLE);
                    giftDetailAbleIntegralTv.setVisibility(View.INVISIBLE);
                    return;
                }

                if (gift.soldout) {
                    giftDetailSureTv.setEnabled(false);
                    giftDetailSureTv.setText("已抢光");
                    gift_detail_able_integral_text_tv.setVisibility(View.INVISIBLE);
                    giftDetailAbleIntegralTv.setVisibility(View.INVISIBLE);
                    return;
                }

                if (isLogin()) {
                    if (gift.points > score) {
                        giftDetailSureTv.setEnabled(false);
                        giftDetailSureTv.setText("积分不足");
                    } else {
                        giftDetailSureTv.setEnabled(true);
                        giftDetailSureTv.setText("立即兑换");
                    }
                    gift_detail_able_integral_text_tv.setVisibility(View.VISIBLE);
                    giftDetailAbleIntegralTv.setVisibility(View.VISIBLE);
                } else {
                    giftDetailSureTv.setEnabled(true);
                    giftDetailSureTv.setText("立即登录兑换");
                    gift_detail_able_integral_text_tv.setVisibility(View.INVISIBLE);
                    giftDetailAbleIntegralTv.setVisibility(View.INVISIBLE);
                }
                giftDetailSureTv.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
