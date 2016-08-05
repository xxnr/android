package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.App;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.event.IsLoginEvent;
import com.ksfc.newfarmer.event.UserInfoChangeEvent;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.beans.PersonalData;
import com.ksfc.newfarmer.beans.PersonalData.Data;
import com.ksfc.newfarmer.utils.FastBlur;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.HeadImageView;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MineActivity extends BaseActivity {

    private TextView title_tv;
    private ImageView head_View_bg_iv;
    private HeadImageView myself_userImg;
    private TextView nickName_tv, mine_type_tv;// 昵称,用户类型
    private ImageView isVerified_iv;


    private String nickname = "";

    private LinearLayout my_order_open_ll;//我的订单入口
    private LinearLayout my_state_ll;//我的网点布局


    private ImageView arrow_right_tv;
    private TextView unLogin_msg_tv;
    private LinearLayout login_content_ll;

    private View pop_bg;

    private PopupWindow popupWindow;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        myself_userImg.setImageBitmap(bitmap);
                        //虚化处理
                        Observable
                                .create(new Observable.OnSubscribe<Bitmap>() {
                                    @Override
                                    public void call(Subscriber<? super Bitmap> subscriber) {
                                        Bitmap aeroBitmap = FastBlur.doBlur(bitmap, 50, false, 0);
                                        subscriber.onNext(aeroBitmap);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(MineActivity.this.<Bitmap>bindToLifecycle())
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        if (bitmap != null) {
                                            head_View_bg_iv.setImageBitmap(bitmap);
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                });
                    }
                    break;
                case 1:
                    head_View_bg_iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mine_bg));
                    myself_userImg.setImageResource(R.drawable.mine_account_head_default_head);
                    break;
            }
        }
    };


    @Override
    public int getLayout() {
        return R.layout.activity_mine;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusHeight = ScreenUtil.getStatusHeight(this);
            ScreenUtil.setMargins(title_tv, 0, Utils.dip2px(MineActivity.this, 10) + statusHeight, 0, 0);
        } else {
            ScreenUtil.setMargins(title_tv, 0, Utils.dip2px(MineActivity.this, 10), 0, 0);
        }
        setData();
        if (isLogin()) {
            setLayout(true);
            getData();
        } else {
            setLayout(false);
        }
    }

    //先展示数据库的信息
    private void setData() {
        if (isLogin()) {
            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {
                final String imgUrl = userInfo.photo;
                if (!StringUtil.empty(imgUrl)) {
                    Observable.create(new Observable.OnSubscribe<Bitmap>() {
                        @Override
                        public void call(Subscriber<? super Bitmap> subscriber) {
                            try {
                                Bitmap bitmap = Picasso.with(MineActivity.this)
                                        .load(MsgID.IP + imgUrl)
                                        .resize(210, 210)
                                        .noFade().get();
                                subscriber.onNext(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                            .compose(this.<Bitmap>bindToLifecycle())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Action1<Bitmap>() {
                                @Override
                                public void call(Bitmap bitmap) {
                                    Message msg = Message.obtain();
                                    msg.obj = bitmap;
                                    msg.what = 0;
                                    handler.sendMessage(msg);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });

                } else {
                    handler.sendEmptyMessage(1);
                }

                if (StringUtil.checkStr(userInfo.nickname)) {
                    nickname = userInfo.nickname;
                    nickName_tv.setText(nickname);
                } else {
                    nickName_tv.setText("新新农人");
                }
                if (StringUtil.checkStr(userInfo.userTypeInName)) {
                    mine_type_tv.setText(userInfo.userTypeInName);
                } else {
                    mine_type_tv.setText("还没填写呦~");
                }
                if (userInfo.isVerified) {
                    isVerified_iv.setVisibility(View.VISIBLE);
                } else {
                    isVerified_iv.setVisibility(View.INVISIBLE);
                }
                if (userInfo.isRSC) {
                    my_order_open_ll.setVisibility(View.GONE);
                    my_state_ll.setVisibility(View.VISIBLE);
                } else {
                    my_order_open_ll.setVisibility(View.VISIBLE);
                    my_state_ll.setVisibility(View.GONE);
                }
            }
        }
    }

    //显示popWindow
    private void showPopUp(View parent) {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopWindow();
        }
        PopWindowUtils.setBackgroundBlack(pop_bg, 0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }


    //初始化popWindow
    public void initPopWindow() {
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_layout_contact_service, null);
        TextView call_phone_tv = (TextView) popupWindow_view.findViewById(R.id.call_phone);
        TextView qq_service_tv = (TextView) popupWindow_view.findViewById(R.id.qq_service);
        LinearLayout cancel_ll = (LinearLayout) popupWindow_view.findViewById(R.id.cancel_ll);

        //增加按纽点击样式
        call_phone_tv.setOnClickListener(this);
        qq_service_tv.setOnClickListener(this);
        cancel_ll.setOnClickListener(this);

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
                PopWindowUtils.setBackgroundBlack(pop_bg, 1);
            }
        });


    }


    /**
     * 获取个人信息
     */
    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("flags", "address");
        execApi(ApiType.PERSONAL_CENTER, params);
    }

    private void initView() {


        title_tv = (TextView) findViewById(R.id.title_tv);
        head_View_bg_iv = (ImageView) findViewById(R.id.head_View_bg); //登录时的背景
        myself_userImg = (HeadImageView) findViewById(R.id.myself_userImg);//用户头像
        nickName_tv = (TextView) findViewById(R.id.nickName_mine);//真实姓名
        mine_type_tv = (TextView) findViewById(R.id.mine_type);//用户类型
        isVerified_iv = (ImageView) findViewById(R.id.mine_type_isVerified);//是否是认证用户


        arrow_right_tv = (ImageView) findViewById(R.id.arrow_right_tv);
        unLogin_msg_tv = (TextView) findViewById(R.id.unLogin_msg);
        login_content_ll = (LinearLayout) findViewById(R.id.login_content);


        my_state_ll = (LinearLayout) findViewById(R.id.my_state_ll);
        my_order_open_ll = (LinearLayout) findViewById(R.id.my_order_open_ll);
        my_order_open_ll.setVisibility(View.GONE);

        pop_bg = findViewById(R.id.pop_bg);

        setViewClick(R.id.head_View);

        setViewClick(R.id.my_net_state_ll);
        setViewClick(R.id.my_order_ll_1);
        setViewClick(R.id.my_order_ll);


        setViewClick(R.id.my_yaoqing_ll);
        setViewClick(R.id.my_jifen_ll);
        setViewClick(R.id.my_set);
        setViewClick(R.id.my_kefudianhua);

        setViewClick(R.id.mine_button1);
        setViewClick(R.id.mine_button2);
        setViewClick(R.id.mine_button3);
        setViewClick(R.id.mine_button4);

    }

    /**
     * 监听登录事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isLoginEvent(IsLoginEvent event) {
        if (isLogin()) {
            setLayout(true);
            getData();
        } else {
            setLayout(false);
        }
    }

    /**
     * 修改用户信息通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserInfoChangeEvent(UserInfoChangeEvent event) {
        if (isLogin()) {
            setLayout(true);
            getData();
        } else {
            setLayout(false);
        }
    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.head_View:
                if (!isLogin()) {
                    goLogin();
                } else {
                    startActivity(MyaccountActivity.class);
                }
                break;
            case R.id.my_net_state_ll:
                if (!isLogin()) {
                    goLogin();
                } else {
                    startActivity(RSCOrderListActivity.class);
                }
                break;
            case R.id.my_order_ll:
            case R.id.my_order_ll_1:
                if (!isLogin()) {
                    goLogin();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 0);
                    startActivity(intent);
                }
                break;
            case R.id.my_yaoqing_ll:
                if (!isLogin()) {
                    goLogin();
                } else {
                    startActivity(NewFramerInviteActivity.class);
                }
                break;
            case R.id.my_jifen_ll:
                IntentUtil.activityForward(MineActivity.this,
                        RewardShopActivity.class, null, false);
                break;
            case R.id.my_kefudianhua:
                showPopUp(v);
                break;
            case R.id.my_set:
                startActivity(SettingActivity.class);
                break;
            case R.id.mine_button1:
                if (!isLogin()) {
                    goLogin();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 1);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button2:
                if (!isLogin()) {
                    goLogin();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 2);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button3:
                if (!isLogin()) {
                    goLogin();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 3);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button4:
                if (!isLogin()) {
                    goLogin();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 4);
                    startActivity(intent);
                }
                break;
            case R.id.call_phone:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Utils.dial(this, "400-056-0371");
                break;
            case R.id.qq_service:
                if(Utils.isQQClientAvailable(MineActivity.this)){
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    String url = "http://wpa.qq.com/msgrd?v=3&uin=2487401812&site=qq&menu=yes";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else {
                    showToast("未检测到QQ，请选择其他方式");
                }
                break;
            case R.id.cancel_ll:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 去登陆
     */
    private void goLogin() {
        startActivity(LoginActivity.class);
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.PERSONAL_CENTER) {
            PersonalData data = (PersonalData) req.getData();
            Data user = data.datas;
            //下载并存储头像
            if (user != null) {
                final String imgUrl = user.getImageUrl();
                if (!StringUtil.empty(imgUrl)) {
                    Observable
                            .create(new Observable.OnSubscribe<Bitmap>() {
                                @Override
                                public void call(Subscriber<? super Bitmap> subscriber) {
                                    try {
                                        Bitmap bitmap = Picasso.with(MineActivity.this)
                                                .load(MsgID.IP + imgUrl)
                                                .resize(210, 210)
                                                .noFade().get();
                                        subscriber.onNext(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .compose(this.<Bitmap>bindToLifecycle())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Bitmap>() {
                                @Override
                                public void call(Bitmap bitmap) {
                                    Message msg = Message.obtain();
                                    msg.obj = bitmap;
                                    msg.what = 0;
                                    handler.sendMessage(msg);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                }
            } else {
                handler.sendEmptyMessage(1);
            }

            if (StringUtil.checkStr(user.nickname)) {
                nickname = user.nickname;
                nickName_tv.setText(nickname);
            } else {
                nickName_tv.setText("新新农人");
            }
            if (StringUtil.checkStr(user.userTypeInName)) {
                mine_type_tv.setText(user.userTypeInName);
            } else {
                mine_type_tv.setText("还没填写呦~");
            }

            if (user.isVerified) {
                isVerified_iv.setVisibility(View.VISIBLE);
            } else {
                isVerified_iv.setVisibility(View.INVISIBLE);
            }
            if (user.isRSC) {
                my_order_open_ll.setVisibility(View.GONE);
                my_state_ll.setVisibility(View.VISIBLE);
            } else {
                my_order_open_ll.setVisibility(View.VISIBLE);
                my_state_ll.setVisibility(View.GONE);
            }
            saveMe(user);
        }
    }

    //存储个人信息到本地
    public void saveMe(Data user) {
        LoginResult.UserInfo me = Store.User.queryMe();
        if (me != null) {
            me.photo = user.getImageUrl();
            if (user.defaultAddress != null) {
                me.defaultAddress = user.defaultAddress.country.replace("undefined", "") + user.defaultAddress.address;
            } else {
                me.defaultAddress = "";
            }
            if (user.address != null) {
                String province = "";
                String city = "";
                String county = "";
                String town = "";
                String address = "";
                if (user.address.province != null) {
                    province = user.address.province.name;
                    me.provinceid = user.address.province.id;
                }
                if (user.address.city != null) {
                    city = user.address.city.name;
                    me.cityid = user.address.city.id;
                }
                if (user.address.county != null) {
                    county = user.address.county.name;
                    me.countyid = user.address.county.id;
                }
                if (user.address.town != null) {
                    town = user.address.town.name;
                    me.townid = user.address.town.id;
                }
                address = StringUtil.checkBufferStr
                        (province, city, county, "");
                me.addressCity = address;
                me.addressTown = town;
            }
            me.sex = user.sex;
            me.nickname = user.nickname;
            me.name = user.name;
            me.phone = user.phone;
            me.userType = user.userType;
            me.userTypeInName = user.userTypeInName;
            me.isXXNRAgent = user.isXXNRAgent;
            me.isRSC = user.isRSC;
            me.RSCInfoVerifing = user.RSCInfoVerifing;
            Store.User.saveMe(me);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogin()) {
            App.loginOut();
            setLayout(false);
        } else {
            setLayout(true);
        }
        LoginResult.UserInfo me = Store.User.queryMe();
        if (me != null) {
            if (me.isRSC) {
                my_order_open_ll.setVisibility(View.GONE);
                my_state_ll.setVisibility(View.VISIBLE);
            } else {
                my_order_open_ll.setVisibility(View.VISIBLE);
                my_state_ll.setVisibility(View.GONE);
            }
        } else {
            my_order_open_ll.setVisibility(View.VISIBLE);
            my_state_ll.setVisibility(View.GONE);
        }
    }


    public void setLayout(boolean isLogin) {
        if (isLogin) {
            login_content_ll.setVisibility(View.VISIBLE);
            arrow_right_tv.setVisibility(View.VISIBLE);
            unLogin_msg_tv.setVisibility(View.GONE);
        } else {
            login_content_ll.setVisibility(View.GONE);
            unLogin_msg_tv.setVisibility(View.VISIBLE);
            arrow_right_tv.setVisibility(View.GONE);
            handler.sendEmptyMessage(1);
            nickName_tv.setText("");
            mine_type_tv.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
