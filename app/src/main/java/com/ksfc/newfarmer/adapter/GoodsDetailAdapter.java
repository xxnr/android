package com.ksfc.newfarmer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.BigImageActivity;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.widget.VerticalScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.support.v4.view.PagerAdapter;

/**
 * Created by CAI on 2015/11/23. 商品详情的适配器
 */
public class GoodsDetailAdapter extends PagerAdapter implements View.OnClickListener{
    private int count;
    private GetGoodsDetail.GoodsDetail detail;
    private Context context;
    private WebView web;
    private TextView guild_1, guild_2, guild_3;
    private TextView bar_guild_1, bar_guild_2, bar_guild_3;
    private VerticalScrollView scrollView;

    public GoodsDetailAdapter(int count, GetGoodsDetail.GoodsDetail detail, Context context) {
        this.count = count;
        this.detail = detail;
        this.context = context;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.goods_detail_top_layout, null);
            if (this.count == 2) {//监听scrollView是否滑动到底部
                scrollView = (VerticalScrollView) view.findViewById(R.id.scrollView_goods_detail);
                scrollView.setOnScrollToBottomLintener(new VerticalScrollView.OnScrollToBottomListener() {
                    @Override
                    public void onScrollBottomListener(boolean isBottom) {
                        if (isBottom) {
                            scrollView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View arg0, MotionEvent arg1) {
                                    return arg1.getAction() == MotionEvent.ACTION_UP;
                                }
                            });
                        }
                    }
                });
            }


            TextView good_xianjia = (TextView) view.findViewById(R.id.product_price);//xianjia:价格
            ImageView good_img = (ImageView) view.findViewById(R.id.good_img);//good_img：商品的图片
            TextView good_name = (TextView) view.findViewById(R.id.good_name); //商品名
            TextView product_dingjing = (TextView) view.findViewById(R.id.product_dingjin_price);//定金
            TextView product_description = (TextView) view.findViewById(R.id.product_description);//描述
            TextView product_dingjing_name = (TextView) view.findViewById(R.id.product_dingjin_name);//"定金"
            TextView product_huafei_dun = (TextView) view.findViewById(R.id.product_huafei_dun);//"/吨"
            RelativeLayout detail_detail = (RelativeLayout) view.findViewById(R.id.goods_detail_detail);//继续上滑，可以加载更多
            TextView product_newFarmer_price = (TextView) view.findViewById(R.id.product_newFarmer_price);//新农价
            good_name.setText(detail.name);
            product_description.setText(detail.description);
            //根据化肥还是汽车 选择图片的加载模式
            if (!TextUtils.isEmpty(detail.imgUrl)) {
                if (detail.category.equals("化肥")) {
                    good_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                ImageLoader.getInstance().displayImage(MsgID.IP + detail.imgUrl, good_img);
            }
            good_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BigImageActivity.class);
                    intent.putExtra("image", detail.originalUrl);
                    context.startActivity(intent);
                }
            });
            //根据化肥还是汽车 隐藏吨
            if (detail.category.equals("化肥")) {
                product_huafei_dun.setVisibility(View.VISIBLE);
            } else {
                product_huafei_dun.setVisibility(View.GONE);
            }
            //根据有无商品详情的url,隐藏继续滑动
            if (!TextUtils.isEmpty(detail.app_body_url)) {
                detail_detail.setVisibility(View.VISIBLE);
            } else {
                detail_detail.setVisibility(View.INVISIBLE);
            }
            //是否预售
            if (detail.presale) {
                product_huafei_dun.setVisibility(View.GONE);
                product_newFarmer_price.setVisibility(View.GONE);
                good_xianjia.setTextColor(Color.GRAY);
                good_xianjia.setText("即将上线");
            } else {
                good_xianjia.setTextColor(Color.parseColor("#ff4e00"));
                good_xianjia.setText("¥" + detail.price + "");
            }
            //是否有定金
            if (!detail.deposit.equals("0")) {
                product_dingjing_name.setVisibility(View.VISIBLE);
                product_dingjing.setText("¥" + detail.deposit + "");
            }
            container.addView(view);
            return view;
        }
        if (position == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.goods_detail_buttom_layout, null);
            web = (WebView) view.findViewById(R.id.goods_detail_list);
            guild_1 = (TextView) view.findViewById(R.id.tv_guid1);
            guild_2 = (TextView) view.findViewById(R.id.tv_guid2);
            guild_3 = (TextView) view.findViewById(R.id.tv_guid3);
            bar_guild_1 = (TextView) view.findViewById(R.id.bar_guid1);
            bar_guild_2 = (TextView) view.findViewById(R.id.bar_guid2);
            bar_guild_3 = (TextView) view.findViewById(R.id.bar_guid3);
            WebSettings settings = web.getSettings();
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(true); // 设置可以支持缩放
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false); //影藏缩放控件
            settings.setUseWideViewPort(true);//设定支持viewport
            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            guild_1.setOnClickListener(this);
            guild_2.setOnClickListener(this);
            guild_3.setOnClickListener(this);
            guild_1.setTextColor(Color.parseColor("#00b38a"));
            bar_guild_1.setVisibility(View.VISIBLE);
            web.loadUrl(detail.app_body_url);
            container.addView(view);
            return view;
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guid1:
                guild_1.setTextColor(context.getResources().getColor(R.color.green));
                guild_2.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_1.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_body_url);
                break;
            case R.id.tv_guid2:
                guild_1.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(context.getResources().getColor(R.color.green));
                guild_3.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_2.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_standard_url);
                break;
            case R.id.tv_guid3:
                guild_1.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(context.getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(context.getResources().getColor(R.color.green));
                initBar();
                bar_guild_3.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_support_url);
                break;
        }
    }

    private void initBar() {
        bar_guild_1.setVisibility(View.GONE);
        bar_guild_2.setVisibility(View.GONE);
        bar_guild_3.setVisibility(View.GONE);
    }


}
