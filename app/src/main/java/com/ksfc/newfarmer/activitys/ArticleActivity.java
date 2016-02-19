package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.ShareBoardlistener;

@SuppressLint("SetJavaScriptEnabled")
public class ArticleActivity extends BaseActivity {
    private LinearLayout social_share_ll;
    private UMImage image ;
    private String urlImage;
    private String urlTitle;
    private String url;

    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.article_layout;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("资讯详情");
        url = getIntent().getStringExtra("articleUrl");
        urlImage = getIntent().getStringExtra("urlImage");
        urlTitle = getIntent().getStringExtra("urlTitle");

        if (StringUtil.checkStr(urlImage)){
            image = new UMImage(ArticleActivity.this, urlImage);
        }else {
            image = new UMImage(ArticleActivity.this, R.drawable.ic_launcher);
        }

        social_share_ll = ((LinearLayout) findViewById(R.id.social_share_ll));

        WebView web = (WebView) findViewById(R.id.webView);
        // 允许运行js脚本
        web.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                social_share_ll.setVisibility(View.VISIBLE);
                setViewClick(R.id.social_share);
            }
        });
        if (StringUtil.checkStr(url)) {
            RndLog.d(TAG, url);
            web.loadUrl(url);

        }

    }

    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.social_share) {

            final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                    {
                            SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
                            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                    };
            Config.dialog = CustomProgressDialog.createLoadingDialog(this, "分享中");
            new ShareAction(this).setDisplayList(displaylist)
                    .withText("新新农人")
                    .withTitle(urlTitle)
                    .withTargetUrl(url)
                    .withMedia(image)
                    .setListenerList(umShareListener, umShareListener)
                    .open();
        }
    }

    @Override
    public void onResponsed(Request req) {

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ArticleActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ArticleActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ArticleActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


}
