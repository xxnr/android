package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.beans.ArticleDetailResult;
import com.ksfc.newfarmer.beans.InformationResult;
import com.ksfc.newfarmer.beans.ShareAddPointsResult;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.RxApi.RxService;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import net.yangentao.util.NetUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


@SuppressLint("SetJavaScriptEnabled")
public class ArticleActivity extends BaseActivity {
    private InformationResult.DatasEntity.ItemsEntity itemsEntity;

    private UMImage image;      //分享内容的图片
    private String urlImage;    //分享内容的图片
    private String urlTitle;    //分享内容的标题
    private String url;         //webView加载的url
    private String shareUrl;        //分享内容的url
    private String newsAbstract;    //分享内容的摘要
    private PopupWindow popupWindow;

    private String newsId;

    public static final String ARG_PARAM1 = "param1";


    @BindView(R.id.webView)
    WebView web;
    @BindView(R.id.share_dialog_bg)
    View share_dialog_bg;
    @BindView(R.id.page_error_layout)
    LinearLayout page_error_layout;

    public static Intent getCallingIntent(Context context, InformationResult.DatasEntity.ItemsEntity itemsEntity) {
        Intent callingIntent = new Intent(context, ArticleActivity.class);
        callingIntent.putExtra(ARG_PARAM1, itemsEntity);
        return callingIntent;
    }


    //友盟分享的回调
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (isLogin()&&StringUtil.checkStr(newsId)) {
                RequestParams params = new RequestParams();
                params.put("type", "news");
                params.put("id", newsId);
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


    @Override
    public int getLayout() {
        // TODO Auto-generated method stub
        return R.layout.activity_web_view;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("资讯详情");
        initView();
        itemsEntity = ((InformationResult.DatasEntity.ItemsEntity) getIntent().getSerializableExtra(ARG_PARAM1));
        if (itemsEntity != null) {
            url = itemsEntity.url;
            shareUrl = itemsEntity.shareurl;
            urlImage = itemsEntity.image;
            urlTitle = itemsEntity.title;
            newsAbstract = itemsEntity.newsabstract;

            newsId = itemsEntity.id;
            //加载url内容
            if (StringUtil.checkStr(url) && NetUtil.isConnected(this)) {
                RndLog.d(TAG, url);
                showProgressDialog();
                web.loadUrl(url);
            } else {
                showToast("网络未连接");
            }

            shareContentCheck();
        } else {
            String id = getIntent().getStringExtra("id");
            newsId=id;
            if (StringUtil.checkStr(id)) {
                RxService.createApi()
                        .GET_NEWS_DETAIL(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(this.<ArticleDetailResult>bindToLifecycle())
                        .subscribe(new Action1<ArticleDetailResult>() {
                            @Override
                            public void call(ArticleDetailResult articleDetailResult) {
                                if (articleDetailResult != null && articleDetailResult.datas != null) {
                                    ArticleDetailResult.DatasBean datas = articleDetailResult.datas;

                                    if (StringUtil.checkStr(datas.url)) {
                                        web.loadUrl(datas.url);
                                    }
                                    shareUrl = datas.shareurl;
                                    urlImage = datas.image;
                                    urlTitle = datas.title;
                                    newsAbstract = datas.abstractX;
                                    shareContentCheck();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
            }
        }

    }

    private void initView() {
        // 允许运行js脚本
        web.getSettings().setJavaScriptEnabled(true);
        // 如果web内出现链接 依旧由当前webVIew加载
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //加载完成之后 展示分享按钮
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                disMissDialog();
                setRightImage(R.drawable.share_icon);
                showRightImage();
                setRightViewListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUp(v);
                    }
                });
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                page_error_layout.setVisibility(View.VISIBLE);
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
         if (req.getApi() == ApiType.SHARE_ADD_POINTS) {
            if (req.getData().getStatus().equals("1000")) {
                ShareAddPointsResult reqData = (ShareAddPointsResult) req.getData();
                if (reqData.points != 0) {
                    showToast("分享成功，奖励您" + reqData.points + "积分");
                }else {
                    showToast("分享成功");
                }
            }else{
                showToast("分享成功");
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
    private void showPopUp(View parent) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void shareContentCheck() {
        //分享content
        if (!StringUtil.checkStr(urlTitle)) {
            urlTitle = "新农资讯";
        }
        if (StringUtil.checkStr(urlImage)) {
            //验证urlImage是否有效
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
                                image = new UMImage(ArticleActivity.this, urlImage);
                            } else {
                                image = new UMImage(ArticleActivity.this, R.drawable.share_app_icon);
                            }
                        }
                    });
        } else {
            image = new UMImage(ArticleActivity.this, R.drawable.share_app_icon);
        }
        if (!StringUtil.checkStr(newsAbstract)) {
            newsAbstract = "分享自@新新农人";
        }
    }
}
