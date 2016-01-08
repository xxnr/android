/**
 *
 */
package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.widget.CustomDialog;
import com.ksfc.newfarmer.widget.CustomProgressDialogForCache;
import com.ksfc.newfarmer.utils.DataCleanManager;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.CustomCheckBox;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.yangentao.util.sql.Table;

/**
 * 项目名称：newFarmer 类名称：SettingActivity 类描述： 创建人：王蕾 创建时间：2015-5-29 下午2:28:31 修改备注：
 */
public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView versionName;
    private CustomCheckBox checkBox;
    private Dialog progressDialog;
    private boolean toastFlag = false;
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


    @Override
    public int getLayout() {
        return R.layout.setting_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("设置");
        initView();
    }

    private void initView() {
        setViewClick(R.id.versionManage);
        setViewClick(R.id.clear_cache);
        setViewClick(R.id.about_us);
        //是否推送
        checkBox = (CustomCheckBox) findViewById(R.id.push_switch);
        PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
        checkBox.setChecked(mPushAgent.isEnabled());
        checkBox.setOnCheckedChangeListener(this);

        //版本号
        versionName = ((TextView) findViewById(R.id.versionName));
        if (getVersionInfo() != null) {
            versionName.setText("V" + getVersionInfo());
        }

    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.versionManage://检查更新
                UmengUpdateAgent.forceUpdate(getApplicationContext());
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        switch (updateStatus) {
                            case UpdateStatus.Yes: // has update
                                UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
                                break;
                            case UpdateStatus.No: // has no update
                                if (toastFlag) {
                                    showToast("已经是最新版本");
                                }
                                break;
                        }
                    }
                });
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
            case R.id.about_us://关于我们
                Bundle bundle = new Bundle();
                bundle.putString("versionName", getVersionInfo());
                IntentUtil.activityForward(SettingActivity.this,
                        AboutUsActivity.class, bundle, false);
                break;

            default:
                break;
        }

    }


    @Override
    public void onResponsed(Request req) {

    }

    //获取版本号
    public String getVersionInfo() {

        PackageManager manager;
        PackageInfo info = null;
        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = info.versionName;
        if (StringUtil.checkStr(versionName)) {
            return versionName;
        }
        return null;
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
    protected void onResume() {
        super.onResume();
        toastFlag = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        toastFlag = false;
    }
}
