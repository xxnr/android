package com.ksfc.newfarmer.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.fragment.ActivityListFragment;
import com.ksfc.newfarmer.fragment.IntegralTallGuideFragment;
import com.ksfc.newfarmer.fragment.SignSuccessFragment;
import com.ksfc.newfarmer.http.Request;
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
    @BindView(R.id.unLogin_bar)
    View unLogin_bar;
    private FragmentManager fragmentManager;

    public static final String KEY = "PAGE_FRAGMENT"; //传值的KEY
    public static final String REWARD_SHOP_GUIDE = "REWARD_SHOP_GUIDE"; //积分商城引导页
    public static final String SIGN_SUCCESS = "SIGN_SUCCESS";  //签到成功
    public static final String ACTIVITY_LIST = "ACTIVITY_LIST"; //活动列表


    @Override
    public int getLayout() {
        return R.layout.activity_floating_layer;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (fragmentManager == null) {
                fragmentManager = getSupportFragmentManager();
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (extras.getString(KEY, REWARD_SHOP_GUIDE)) {
                case REWARD_SHOP_GUIDE:  //加载积分商城引导页
                    //空出积分商城未登录时候的提示未登录的布局
                    if (isLogin()) {
                        unLogin_bar.setVisibility(View.GONE);
                    } else {
                        unLogin_bar.setVisibility(View.VISIBLE);
                    }
                    changFragment(1, fragmentTransaction);
                    //浮层引导页切换通知
                    MsgCenter.addListener(new MsgListener() {
                        @Override
                        public void onMsg(Object sender, String msg, Object... args) {
                            if (args != null) {
                                if (fragmentManager == null) {
                                    fragmentManager = getSupportFragmentManager();
                                }
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                changFragment((int) args[0], transaction);
                                transaction.commitAllowingStateLoss();
                            } else {
                                finish();
                            }
                        }
                    }, MsgID.Integral_Guide_Change);
                    break;
                case SIGN_SUCCESS://加载签到成功动画
                    SignSuccessFragment fragment = new SignSuccessFragment();
                    fragment.setArguments(extras);
                    fragmentTransaction.replace(R.id.layer_content_view, fragment);
                    break;
                case ACTIVITY_LIST:
                    fragmentTransaction.replace(R.id.layer_content_view, ActivityListFragment.newInstance());
                    break;
            }
            fragmentTransaction.commitAllowingStateLoss();
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
    public void changFragment(int page, FragmentTransaction fragmentTransaction) {
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
    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimationUtils.setActivityAnimation(this, R.anim.animation_none, R.anim.animation_none);
    }
}
