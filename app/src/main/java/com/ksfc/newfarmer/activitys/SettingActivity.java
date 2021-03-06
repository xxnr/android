/**
 *
 */
package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CompleteReceiver;
import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.remoteapi.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.AppUpgrade;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.widget.dialog.CustomDialogUpdate;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialog;
import com.ksfc.newfarmer.widget.dialog.CustomProgressDialogForCache;
import com.ksfc.newfarmer.utils.DataCleanManager;
import com.ksfc.newfarmer.utils.StringUtil;

import com.umeng.message.PushAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import greendao.DaoSession;

/**
 * 项目名称：newFarmer 类名称：SettingActivity 类描述： 创建人：王蕾 创建时间：2015-5-29 下午2:28:31 修改备注：
 */
public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView versionName;
    private CheckBox checkBox;
    private Dialog progressDialog;

    private String msg = "下载新新农人APP，买车实惠又方便！";
    private String title = "新新农人-互联网汽车综合服务平台";
    private String url = "http://www.xinxinnongren.com/shareApp.html";
    private UMImage image ;

    //去清除缓存需要三秒
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        showToast("缓存已经清理干净了！");
                    }
                    break;
            }
        }
    };

    private PopupWindow popupWindow;
    private RelativeLayout share_dialog_bg;//分享dialog的背景
    //友盟分享的回调
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToast("分享成功");
            //分享成功后关闭对话框
            if (popupWindow.isShowing()) {
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
    private CompleteReceiver completeReceiver;


    @Override
    public int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("设置");
        initView();
        image=new UMImage(SettingActivity.this, R.drawable.share_app_icon);
        //在主页判断版本是否需要升级 并注册监听下载完成之后的广播
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initView() {
        setViewClick(R.id.versionManage);
        setViewClick(R.id.clear_cache);
        setViewClick(R.id.about_us);
        setViewClick(R.id.share_app_ll);
        //是否推送
        checkBox = (CheckBox) findViewById(R.id.push_switch);
        PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
        checkBox.setChecked(mPushAgent.isEnabled());
        checkBox.setOnCheckedChangeListener(this);

        //版本号
        versionName = ((TextView) findViewById(R.id.versionName));
        String versionInfo = Utils.getVersionInfo(this);
        if (StringUtil.checkStr(versionInfo)) {
            versionName.setText("V" + versionInfo);
        }

        share_dialog_bg = (RelativeLayout) findViewById(R.id.share_dialog_bg);
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
                        .withText(msg)
                        .withTitle(title)
                        .withTargetUrl(url)
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


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.versionManage://检查更新
                //app 是否需要升级
                RemoteApi.appIsNeedUpdate(this);

                break;
            case R.id.clear_cache://清除缓存
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        SettingActivity.this);
                builder.setMessage("确定清除缓存的图片和数据吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        //清除缓存
                                        DataCleanManager.cleanInternalCache(getApplicationContext());
                                        DataCleanManager.cleanExternalCache(getApplicationContext());
                                        //清除当前用户的数据库 部分数据库
                                        DaoSession writableDaoSession = DBManager.getInstance(SettingActivity.this).getWritableDaoSession();
                                        writableDaoSession.getInviteeEntityDao().deleteAll();
                                        writableDaoSession.getPotentialCustomersEntityDao().deleteAll();


                                        //进度条
                                        progressDialog = CustomProgressDialogForCache.createLoadingDialog(SettingActivity.this, "正在清除缓存请稍后...");
                                        progressDialog.show();
                                        handler.sendEmptyMessageDelayed(0, 1500);
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                builder.create().show();

                break;
            case R.id.share_app_ll:
                showPopUp(v);
                break;
            case R.id.about_us://关于我们
                Intent callingIntent = AboutUsActivity.getCallingIntent(SettingActivity.this, Utils.getVersionInfo(this));
                startActivity(callingIntent);
                break;
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
            default:
                break;
        }

    }


    @Override
    public void onResponsed(Request req) {
        if (ApiType.APP_UP_GRADE == req.getApi()) {
            final AppUpgrade reqData = (AppUpgrade) req.getData();
            if (StringUtil.checkStr(reqData.android_update_url)) {
                CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(
                        SettingActivity.this);
                builder.setMessage(reqData.getMessage())
                        .setTitle("V" + reqData.version)
                        .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.loadApk(SettingActivity.this, reqData.android_update_url, "Download");
                                showToast("正在下载请稍后...");
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                CustomDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }


    //推送开关
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
            mPushAgent.enable();
        } else {
            CustomDialog.Builder builder = new CustomDialog.Builder(
                    SettingActivity.this);
            builder.setMessage("推送关闭后收不到最新消息了哦，确定关闭吗？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
                                    mPushAgent.disable();
                                    dialog.dismiss();
                                    checkBox.setChecked(false);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    checkBox.setChecked(true);
                                    dialog.dismiss();
                                }
                            });
            CustomDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除对下载完成事件的监听
        unregisterReceiver(completeReceiver);
    }
}
