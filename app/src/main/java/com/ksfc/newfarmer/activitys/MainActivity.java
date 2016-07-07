package com.ksfc.newfarmer.activitys;

import net.yangentao.util.PreferenceUtil;

import com.ksfc.newfarmer.App;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;


import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.utils.Constants;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.Utils;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * 类名称：MainActivity 类描述：程序的主页面
 */
public class MainActivity extends TabActivity implements View.OnClickListener {

    private static MainActivity instance;
    private View mStatus_bar;
    private RelativeLayout integral_mall_guide_rel;
    private LinearLayout integral_mall_content_rel;

    private RadioGroup mRadioGroup;
    public TabHost mTabHost;
    public TabSpec tabSpec;

    private long backPressTime;

    public enum Tab {
        INDEX, INFORMATION, SHOPPING_CART, MINE
    }

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        // 屏幕竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        initView();
        //透明状态栏和设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mStatus_bar.setVisibility(View.VISIBLE);
            mStatus_bar.getLayoutParams().height = ScreenUtil.getStatusHeight(this);
            mStatus_bar.setLayoutParams(mStatus_bar.getLayoutParams());
        } else {
            mStatus_bar.setVisibility(View.GONE);
        }
        //通知MainActivity切换
        Tab tab = (Tab) getIntent().getSerializableExtra("id");
        if (tab != null) {
            setRadioGroupCheckById(tab);
        }else {
            setRadioGroupCheckById(Tab.INDEX);
        }
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                Tab tab = Tab.INDEX;
                try {
                    tab = (Tab) args[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setRadioGroupCheckById(tab);
            }
        }, MsgID.MainActivity_select_tab);
    }

    public void initView() {
        mTabHost = getTabHost();
        int count = Constants.TAB_LIST.length;
        for (int i = 0; i < count; i++) {
            tabSpec = mTabHost.newTabSpec(Constants.TAB_LIST[i])
                    .setIndicator(Constants.TAB_LIST[i])
                    .setContent(getTabItemIntent(i));
            mTabHost.addTab(tabSpec);
        }
        mStatus_bar = findViewById(R.id.status_bar);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        integral_mall_guide_rel = (RelativeLayout) findViewById(R.id.integral_mall_guide_rel);
        integral_mall_content_rel = (LinearLayout) findViewById(R.id.integral_ll);
        integral_mall_guide_rel.setOnClickListener(this);

        mRadioGroup.check(R.id.rb_homepage);
        mTabHost.setCurrentTab(0);
        //设置状态栏颜色状态栏
        setBarTint(true);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setBarTint(true);
                switch (checkedId) {
                    case R.id.rb_homepage:
                        mTabHost.setCurrentTabByTag(Constants.TAB_LIST[0]);
                        break;
                    case R.id.rb_indent:
                        mTabHost.setCurrentTabByTag(Constants.TAB_LIST[1]);
                        break;
                    case R.id.rb_product:
                        mTabHost.setCurrentTabByTag(Constants.TAB_LIST[2]);
                        break;
                    case R.id.rb_shopkeeper:
                        //第一次进入我的展示积分引导页
                        PreferenceUtil pu = new PreferenceUtil(MainActivity.this, "config");
                        boolean firstInMine = pu.getBool("firstInMine", true);
                        if (firstInMine) {
                            LoginResult.UserInfo userInfo = Store.User.queryMe();
                            //是县级经销商需要调整高度
                            if (userInfo != null && userInfo.isRSC) {
                                ScreenUtil.setMargins(integral_mall_content_rel, 0, Utils.dip2px(MainActivity.this, 298f), 0, 0);
                            } else {
                                ScreenUtil.setMargins(integral_mall_content_rel, 0, Utils.dip2px(MainActivity.this, 318f), 0, 0);
                            }
                            integral_mall_guide_rel.setVisibility(View.VISIBLE);
                        }
                        pu.putBool("firstInMine", false);
                        mTabHost.setCurrentTabByTag(Constants.TAB_LIST[3]);
                        setBarTint(false);
                        break;
                }
            }
        });
        ((RadioButton) mRadioGroup.getChildAt(0)).toggle();
    }

    public void setRadioGroupCheckById(Tab tab) {
        switch (tab) {
            case INDEX:
                mRadioGroup.check(R.id.rb_homepage);
                break;
            case INFORMATION:
                mRadioGroup.check(R.id.rb_indent);
                break;
            case SHOPPING_CART:
                mRadioGroup.check(R.id.rb_product);
                break;
            case MINE:
                mRadioGroup.check(R.id.rb_shopkeeper);
                break;
        }
    }

    public Intent getTabItemIntent(int index) {
        return new Intent(this, Constants.mHomeTabClassArray[index]);
    }


    @Override
    public void onClick(View v) {
        //点击隐藏我的新农人页  积分商城tab
        if (v.getId() == R.id.integral_mall_guide_rel) {
            integral_mall_guide_rel.setVisibility(View.GONE);
        }
    }

    /**
     * 设置状态栏颜色
     */
    public void setBarTint(boolean enabled) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (enabled) {
                mStatus_bar.setVisibility(View.VISIBLE);
                mStatus_bar.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                mStatus_bar.setVisibility(View.GONE);
            }
        } else {
            mStatus_bar.setVisibility(View.GONE);
        }
    }

    // 再按一次退出程序
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if (integral_mall_guide_rel.getVisibility() == View.VISIBLE) {
                integral_mall_guide_rel.setVisibility(View.GONE);
            }
            long current = System.currentTimeMillis();
            if (current - backPressTime > 2000) {
                backPressTime = current;
                App.getApp().showToast("再次按返回键退出程序");
            } else {
                RndLog.i("MainActivity", "Exiting...");
                App.getApp().quit();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
