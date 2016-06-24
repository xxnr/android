package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.GiftDetailResult;
import com.ksfc.newfarmer.protocol.beans.IntegralGetResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/15.
 */
public class IntegralGiftDetailActivity extends BaseActivity {

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

    private int score;
    private String id;
    private GiftDetailResult.GiftBean gift;

    @Override
    public int getLayout() {
        return R.layout.integral_gift_detail_layout;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("礼品详情");

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
        RemoteApi.getGiftDetail(this, id);

        //监听登录事件
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                RemoteApi.getIntegral(IntegralGiftDetailActivity.this);
            }
        }, MsgID.ISLOGIN);
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.gift_detail_sure_tv:
                if (isLogin()) {
                    if (gift != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("gift", gift);
                        IntentUtil.activityForward(this, IntegralGiftSubmitActivity.class, bundle, false);
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
                if (StringUtil.checkStr(gift.originalUrl)) {
                    ImageLoader.getInstance().displayImage(MsgID.IP + gift.originalUrl, giftDetailImg);
                }
                giftDetailTitle.setText(StringUtil.checkStr(gift.name) ? gift.name : "");
                giftDetailIntegralTv.setText(String.valueOf(gift.points));
                giftDetailAbleIntegralTv.setText(String.valueOf(score));
                try {
                    if (Double.parseDouble(gift.marketPrice)!=0){
                        giftDetailMarketPriceTv.setText(gift.marketPrice);
                        giftDetailMarketPriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                    }else {
                        giftDetailMarketPriceLl.setVisibility(View.GONE);
                    }
                }catch (Exception e){
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

}
