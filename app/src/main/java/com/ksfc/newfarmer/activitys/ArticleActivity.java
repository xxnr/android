package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

@SuppressLint("SetJavaScriptEnabled")
public class ArticleActivity extends BaseActivity {
    private UMImage image;
    private String urlImage;
    private String urlTitle;
    private String url;
    private String newsAbstract;
    private PopupWindow popupWindow;
    private RelativeLayout share_dialog_bg;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ArticleActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ArticleActivity.this, platform + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ArticleActivity.this, platform + " 取消了分享", Toast.LENGTH_SHORT).show();
        }
    };



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
        newsAbstract = getIntent().getStringExtra("newsAbstract");

        if (!StringUtil.checkStr(urlTitle)) {
            urlTitle = "新新农人";
        }
        if (!StringUtil.checkStr(url)) {
            url = "http://www.xinxinnongren.com";
        }

        if (StringUtil.checkStr(urlImage)) {
            image = new UMImage(ArticleActivity.this, urlImage);
        } else {
            image = new UMImage(ArticleActivity.this, R.drawable.ic_launcher);
        }

        if (!StringUtil.checkStr(newsAbstract)) {
            newsAbstract = "新新农人";
        }

        share_dialog_bg=(RelativeLayout)findViewById(R.id.share_dialog_bg);

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
                setRightImage(R.drawable.share_icon);
                showRightImage();
                setRightViewListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUp(v);
                    }
                });
            }
        });
        if (StringUtil.checkStr(url)) {
            RndLog.d(TAG, url);
            web.loadUrl(url);
        }

    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_friends:
                share(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.qq_share:
                share(SHARE_MEDIA.QQ);
                break;
            case R.id.qq_zone_share:
                share(SHARE_MEDIA.QZONE);
                break;

        }

    }

    @Override
    public void onResponsed(Request req) {

    }


    public void initPopWindow() {
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_share_dialog, null);

        TextView wechat_friends = (TextView) popupWindow_view.findViewById(R.id.wechat_friends);
        TextView wechat_circle = (TextView) popupWindow_view.findViewById(R.id.wechat_circle);
        TextView qq = (TextView) popupWindow_view.findViewById(R.id.qq_share);
        TextView qq_zone = (TextView) popupWindow_view.findViewById(R.id.qq_zone_share);

        wechat_friends.setOnClickListener(this);
        wechat_circle.setOnClickListener(this);
        qq.setOnClickListener(this);
        qq_zone.setOnClickListener(this);


        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowUtils.setBackgroundBlack(share_dialog_bg,1);
            }
        });

        Config.dialog = CustomProgressDialog.createLoadingDialog(this, "");
    }

    //显示popWindow
    private void showPopUp(View parent) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopWindow();
        }
        PopWindowUtils.setBackgroundBlack(share_dialog_bg,0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    public void share(SHARE_MEDIA share_media) {
        try {
            new ShareAction(this)
                    .setPlatform(share_media)
                    .setCallback(umShareListener)
                    .withText(newsAbstract)
                    .withTitle(urlTitle)
                    .withTargetUrl(url)
                    .withMedia(image)
                    .share();
        } catch (Exception e) {
            showToast("分享失败");
        }

    }


}
