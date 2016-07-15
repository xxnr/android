package com.ksfc.newfarmer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.TextView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.GetGoodsDetail;


/**
 * Created by CAI on 2016/7/15.
 */
public class GoodsDetailButtomFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private GetGoodsDetail.GoodsDetail detail;
    private WebView web;
    private TextView guild_1;
    private TextView guild_2;
    private TextView guild_3;
    private TextView bar_guild_1;
    private TextView bar_guild_2;
    private TextView bar_guild_3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detail = (GetGoodsDetail.GoodsDetail) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    public static GoodsDetailButtomFragment newInstance(GetGoodsDetail.GoodsDetail detail) {
        GoodsDetailButtomFragment fragment = new GoodsDetailButtomFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, detail);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guid1:
                guild_1.setTextColor(activity.getResources().getColor(R.color.green));
                guild_2.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_1.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_body_url);
                break;
            case R.id.tv_guid2:
                guild_1.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(activity.getResources().getColor(R.color.green));
                guild_3.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                initBar();
                bar_guild_2.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_standard_url);
                break;
            case R.id.tv_guid3:
                guild_1.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                guild_2.setTextColor(activity.getResources().getColor(R.color.main_index_gary));
                guild_3.setTextColor(activity.getResources().getColor(R.color.green));
                initBar();
                bar_guild_3.setVisibility(View.VISIBLE);
                web.loadUrl(detail.app_support_url);
                break;
        }
    }

    private void initBar() {
        bar_guild_1.setVisibility(View.INVISIBLE);
        bar_guild_2.setVisibility(View.INVISIBLE);
        bar_guild_3.setVisibility(View.INVISIBLE);
    }

    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.goods_detail_buttom, null);
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
        guild_1.setTextColor(activity.getResources().getColor(R.color.green));
        bar_guild_1.setVisibility(View.VISIBLE);
        web.loadUrl(detail.app_body_url);
        return view;
    }

    @Override
    public void onResponsed(Request req) {

    }


}
