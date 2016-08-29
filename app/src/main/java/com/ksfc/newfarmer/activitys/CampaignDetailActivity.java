package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.CampaignDetailResult;
import com.ksfc.newfarmer.beans.CampaignListResult;
import com.ksfc.newfarmer.beans.ShareAddPointsResult;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.event.IsLoginEvent;
import com.ksfc.newfarmer.event.WebShareUrlEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.jsinterface.JavaScriptObject;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import net.yangentao.util.NetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by CAI on 2016/6/24.
 */
public class CampaignDetailActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.share_dialog_bg)
    View share_dialog_bg;//分享dialog的背景
    @BindView(R.id.page_error_layout)
    LinearLayout page_error_layout;

    private PopupWindow popupWindow;

    private String url; //webView加载的url
    private String urlImage;    //分享内容的图片

    private UMImage image;      //分享内容的图片
    private String urlTitle;    //分享内容的标题
    public String shareUrl;        //分享内容的url
    private String newsAbstract;    //分享内容的摘要

    private String campaignId;  //活动Id

    private boolean isNeedRerefreh;


    @Override
    public int getLayout() {
        return R.layout.activity_web_view;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setTitle("");
        init();
        String urlH5 = getIntent().getStringExtra("url");
        if (StringUtil.checkStr(urlH5)) { //是否来自h5页面跳转
            url = MsgID.IP + urlH5;
            fromAppLink(urlH5);
        } else {
            fromList();
        }
        refreshWeb();
    }

    /**
     * 监听h5点击分享
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void urlShareEvent(WebShareUrlEvent event) {
        if (StringUtil.checkStr(event.shareUrl)) {
            shareUrl = event.shareUrl;
            isNeedRerefreh = event.isRefresh;
            titleRightView.performClick();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isLoginEvent(IsLoginEvent event) {
        refreshWeb();
    }


    private void refreshWeb() {
        if (NetUtil.isConnected(this)) {
            showProgressDialog();
            if (url.contains("http")) {
                webView.loadUrl(url);
            } else {
                webView.loadUrl(MsgID.IP + url);
            }
        } else {
            showToast("网络未连接");
        }

    }


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {

        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }

        // 允许运行js脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        webView.addJavascriptInterface(new JavaScriptObject(this), "jsObj");
        webView.getSettings().setSupportZoom(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                disMissDialog();
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                page_error_layout.setVisibility(View.VISIBLE);
            }
        });
        //加载当前页标题
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(StringUtil.checkStr(title) ? title : "");
            }
        });

        titleRightView.setVisibility(View.GONE);
        showRightImage();
        setRightImage(R.drawable.share_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

    }

    @Override
    public void OnViewClick(View v) {
        if (StringUtil.checkStr(shareUrl)) {
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
        } else {
            switch (v.getId()) {
                case R.id.wechat_friends:
                case R.id.wechat_circle:
                case R.id.qq_share:
                case R.id.qq_zone_share:
                    showToast("抱歉，分享失败");
                    break;
            }
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_APP_SHARE_INFO) {
            if (req.getData().getStatus().equals("1000")) {
                CampaignDetailResult campaign = (CampaignDetailResult) req.getData();
                if (campaign != null) {
                    // 是否右上角又分享按钮
                    if (campaign.share_button) {
                        titleRightView.setVisibility(View.VISIBLE);
                    } else {
                        titleRightView.setVisibility(View.GONE);
                    }
                    //share content
                    shareUrl = campaign.share_url;
                    newsAbstract = campaign.share_abstract;
                    urlTitle = campaign.share_title;
                    urlImage = campaign.share_image;
                    campaignId = campaign.campaign_id;
                    checkedShareContent();
                }
            }

        } else if (req.getApi() == ApiType.SHARE_ADD_POINTS) {
            if (req.getData().getStatus().equals("1000")) {
                ShareAddPointsResult reqData = (ShareAddPointsResult) req.getData();
                if (reqData.points > 0) {
                    showToast("分享成功，奖励您" + reqData.points + "积分");
                } else {
                    showToast("分享成功");
                }
            } else {
                showToast("分享成功");
            }
            if (isNeedRerefreh){
                refreshWeb();
            }
        }
    }

    /**
     * 请求活动详情
     *
     * @param urlH5
     */
    private void fromAppLink(String urlH5) {
        RequestParams params = new RequestParams();
        params.put("url", urlH5);
        execApi(ApiType.GET_APP_SHARE_INFO.setMethod(ApiType.RequestMethod.GET), params);
    }

    private void fromList() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            CampaignListResult.CampaignsBean campaign = (CampaignListResult.CampaignsBean) extras.getSerializable("campaign");
            if (campaign != null) {
                url = campaign.url;
                // 是否右上角又分享按钮
                if (campaign.share_button) {
                    titleRightView.setVisibility(View.VISIBLE);
                } else {
                    titleRightView.setVisibility(View.GONE);
                }
                //share content
                campaignId = campaign._id;
                shareUrl = campaign.share_url;
                newsAbstract = campaign.share_abstract;
                urlTitle = campaign.share_title;
                urlImage = campaign.share_image;
                checkedShareContent();
            }
        }

    }


    //初始化popWindow
    public void initPopWindow() {
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_share_dialog, null);
        TextView wechat_friends = (TextView) popupWindow_view.findViewById(R.id.wechat_friends);
        TextView wechat_circle = (TextView) popupWindow_view.findViewById(R.id.wechat_circle);
        TextView qq = (TextView) popupWindow_view.findViewById(R.id.qq_share);
        TextView qq_zone = (TextView) popupWindow_view.findViewById(R.id.qq_zone_share);
        //增加按纽点击样式
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
                PopWindowUtils.setBackgroundBlack(share_dialog_bg, 1);
            }
        });


    }

    //显示popWindow
    public void showPopUp(View parent) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopWindow();
        }
        PopWindowUtils.setBackgroundBlack(share_dialog_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    /**
     * 分享到不同的第三方平台
     *
     * @param share_media 第三方平台
     */
    public synchronized void share(SHARE_MEDIA share_media) {
        //修改分享默认的dialog
        Config.dialog = CustomProgressDialog.createLoadingDialog(this, "分享中...", Color.parseColor("#FFFFFF"));
        boolean install;
        if (share_media == SHARE_MEDIA.QZONE) {
            install = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
        } else {
            install = UMShareAPI.get(this).isInstall(this, share_media);
        }
        if (install) {
            try {
                new ShareAction(this)
                        .setPlatform(share_media)
                        .setCallback(umShareListener)
                        .withText(newsAbstract)
                        .withTitle(urlTitle)
                        .withTargetUrl(shareUrl)
                        .withMedia(image)
                        .share();
            } catch (Exception e) {
                e.printStackTrace();
                showToast("抱歉，分享失败");
            }
        } else {
            if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                showToast("尚未安装微信客户端");
            } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                showToast("尚未安装QQ客户端");
            }
        }
    }


    /**
     * 检查分享内容的完整性
     */
    public void checkedShareContent() {

        //默认分享的标题 和图片 摘要
        if (!StringUtil.checkStr(urlTitle)) {
            urlTitle = "新农资讯";
        }
        if (StringUtil.checkStr(urlImage)) {
            Observable
                    .create(new Observable.OnSubscribe<Boolean>() {
                        @Override
                        public void call(Subscriber<? super Boolean> subscriber) {
                            subscriber.onNext(NetUtil.isValid(urlImage));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(this.<Boolean>bindToLifecycle())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                image = new UMImage(CampaignDetailActivity.this, urlImage);
                            } else {
                                image = new UMImage(CampaignDetailActivity.this, R.drawable.share_app_icon);
                            }
                        }
                    });
        } else {
            image = new UMImage(CampaignDetailActivity.this, R.drawable.share_app_icon);
        }
        if (!StringUtil.checkStr(newsAbstract)) {
            newsAbstract = "分享自@新新农人";
        }
    }


    /**
     * 友盟分享的回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (isLogin() && StringUtil.checkStr(campaignId)) {
                RequestParams params = new RequestParams();
                params.put("type", "campaign");
                params.put("id", campaignId);
                params.put("userId", Store.User.queryMe().userid);
                execApi(ApiType.SHARE_ADD_POINTS, params);
            } else {
                showToast("分享成功");
            }

            //分享成功后关闭对话框
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("抱歉，分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("分享取消");
        }

    };


    /**
     * 分享结果的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        finish();//结束退出程序
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
