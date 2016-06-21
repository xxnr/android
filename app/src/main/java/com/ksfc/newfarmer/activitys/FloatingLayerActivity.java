package com.ksfc.newfarmer.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.IntegralTallGuideFragment;
import com.ksfc.newfarmer.fragment.SignSuccessFragment;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.ActivityAnimationUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 浮层 第一次进入某个页面时展示
 */
public class FloatingLayerActivity extends BaseActivity {
    @BindView(R.id.status_bar)
    View mStatus_bar;
    private FragmentManager fragmentManager;

    @Override
    public int getLayout() {
        return R.layout.floating_layer_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            switch (extras.getString("activity", "IntegralTallActivity")) {
                case "IntegralTallActivity":  //加载积分商城引导页
                case "HomepageActivity":  //加载积分商城引导页
                    changFragment(1);
                    //浮层引导页切换通知
                    MsgCenter.addListener(new MsgListener() {

                        @Override
                        public void onMsg(Object sender, String msg, Object... args) {
                            if (args != null) {
                                changFragment((int) args[0]);
                            } else {
                                finish();
                            }
                        }
                    }, MsgID.Integral_Guide_Change);
                    break;
                case "MyIntegralActivity"://加载签到成功动画
                    if (fragmentManager == null) {
                        fragmentManager = getSupportFragmentManager();
                    }
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SignSuccessFragment fragment = new SignSuccessFragment();
                    fragment.setArguments(extras);
                    fragmentTransaction.replace(R.id.layer_content_view, fragment);
                    fragmentTransaction.commit();
                    break;
            }
        }
    }

    private void initView() {
        //透明状态栏和设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mStatus_bar.setVisibility(View.VISIBLE);
            mStatus_bar.getLayoutParams().height = ScreenUtil.getStatusHeight(this);
            mStatus_bar.setLayoutParams(mStatus_bar.getLayoutParams());
        } else {
            mStatus_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnViewClick(View v) {
    }

    @Override
    public void onResponsed(Request req) {

    }

    //积分商城引导页浮层引导页切换
    public void changFragment(int page) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        IntegralTallGuideFragment guideFragment = new IntegralTallGuideFragment();
        Bundle bundle = new Bundle();
        if (page == 1) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                bundle.putString("integral", extras.getString("integral"));
            }
        } else if (page == 2) {
            //传数据
            Bundle extras = getIntent().getExtras();
            bundle.putSerializable("gift", extras.getSerializable("gift"));
        }
        bundle.putInt("page", page);
        guideFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.layer_content_view, guideFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimationUtils.setActivityAnimation(this, R.anim.animation_none, R.anim.animation_none);
    }
}
