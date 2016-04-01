package com.ksfc.newfarmer;

import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;


import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.utils.Constants;
import com.ksfc.newfarmer.utils.RndLog;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * 项目名称：QianXihe 类名称：MainActivity 类描述： 程序的主页面；首页 创建人：Jimes 创建时间：2015-5-14
 * 下午1:56:53 修改备注：
 */
public class MainActivity extends TabActivity {

    private static MainActivity instance;

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

    public TabHost mTabHost;
    private RadioGroup mRadioGroup;
    private long backPressTime;
    public RadioButton homepage;
    public RadioButton chat;
    public RadioButton product;
    public RadioButton setting;
    public TabSpec tabSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        initView();
        setRadioGroupCheckById(getIntent().getIntExtra("id", 1));
        DBManager.getInstance().Init(MainActivity.this);


        //滑动时刷新
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {

                int i = 1;
                try {
                    i=(Integer) args[0];
                }catch (Exception e){
                    e.printStackTrace();
                }
                setRadioGroupCheckById(i);
            }
        }, "MainActivity_select_tab");

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
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        homepage = (RadioButton) findViewById(R.id.rb_homepage);
        chat = (RadioButton) findViewById(R.id.rb_indent);
        product = (RadioButton) findViewById(R.id.rb_product);
        setting = (RadioButton) findViewById(R.id.rb_shopkeeper);
        homepage.setChecked(true);
        mTabHost.setCurrentTab(0);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

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
                        mTabHost.setCurrentTabByTag(Constants.TAB_LIST[3]);
                        break;
                }
            }
        });

        ((RadioButton) mRadioGroup.getChildAt(0)).toggle();
    }

    public void setRadioGroupCheckById(int id) {
        switch (id) {
            case 1:
                mRadioGroup.check(R.id.rb_homepage);
                break;
            case 2:
                mRadioGroup.check(R.id.rb_indent);
                break;
            case 3:
                mRadioGroup.check(R.id.rb_product);
                break;
            case 4:
                mRadioGroup.check(R.id.rb_shopkeeper);
                break;
        }
    }

    public Intent getTabItemIntent(int index) {
        Intent intent = new Intent(this, Constants.mHomeTabClassArray[index]);
        return intent;
    }

    // 再按一次退出程序
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            long current = System.currentTimeMillis();
            if (current - backPressTime > 2000) {
                backPressTime = current;
                App.getApp().showToast("再次按返回键退出程序");
            } else {
                RndLog.i("MainActivity", "Exiting...");
                RndApplication.getInstance().quit();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
