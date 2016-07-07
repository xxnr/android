package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.PersonalData;
import com.ksfc.newfarmer.http.beans.PersonalData.Data;
import com.ksfc.newfarmer.utils.FastBlur;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.HeadImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import rx.Observable;
import rx.Scheduler;
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


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        myself_userImg.setImageBitmap(bitmap);
                        //虚化处理
                        Observable.create(new Observable.OnSubscribe<Bitmap>() {
                            @Override
                            public void call(Subscriber<? super Bitmap> subscriber) {
                                Bitmap aeroBitmap = FastBlur.doBlur(bitmap, 50, false, 0);
                                subscriber.onNext(aeroBitmap);
                            }
                        })
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        if (bitmap != null) {
                                            head_View_bg_iv.setImageBitmap(bitmap);

                                        }
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(MsgID.IP + imgUrl);
                            Message msg = Message.obtain();
                            msg.obj = bitmap;
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }).start();
                } else {
                    handler.sendEmptyMessageDelayed(1, 100);
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

        //修改用信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (isLogin()) {
                    setLayout(true);
                    getData();
                } else {
                    setLayout(false);
                }
            }
        }, MsgID.UPDATE_USER);
        //登陆通知
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (isLogin()) {
                    setLayout(true);
                    getData();
                } else {
                    setLayout(false);
                }
            }
        }, MsgID.ISLOGIN);

        //退出登录通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                setLayout(false);
            }
        }, MsgID.CLEAR_USER);

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
                        IntegralTallActivity.class, null, false);
                break;
            case R.id.my_kefudianhua:
                Utils.dial(this, "400-056-0371");
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(MsgID.IP + imgUrl);
                            Message msg = Message.obtain();
                            msg.obj = bitmap;
                            msg.what = 0;
                            handler.sendMessage(msg);
                        }
                    }).start();
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
            exitLogin();
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
}
