package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PersonalData;
import com.ksfc.newfarmer.protocol.beans.PersonalData.Data;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.HeadImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

public class MineActivity extends BaseActivity {
    private HeadImageView myself_userImg;
    private TextView userAaddress_mine, nickName_mine, mine_type;// --，送货地址,所在地,昵称
    private String nickname = "";
    private RelativeLayout titleView, titleview_login;
    private ImageView isVerified;
    private String integral = ""; //用户的积分用于向，我的积分页传递
    private LinearLayout my_order_open_ll;
    private LinearLayout my_state_ll;


    @Override
    public int getLayout() {
        return R.layout.mine_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我的新农人");
        hideLeft();
        initView();
        setData();
        titleview_login = (RelativeLayout) findViewById(R.id.titleview_login);
        titleView = (RelativeLayout) findViewById(R.id.titleview);
        if (isLogin()) {
            titleview_login.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
            getData();
        } else {
            titleView.setVisibility(View.VISIBLE);
            titleview_login.setVisibility(View.GONE);
        }

    }

    //先展示数据库的信息
    private void setData() {
        if (isLogin()) {

            LoginResult.UserInfo userInfo = Store.User.queryMe();
            if (userInfo != null) {

                //下载并存储头像
                final String imgUrl = userInfo.photo;
                if (!StringUtil.empty(imgUrl)) {
                    ImageLoader.getInstance().displayImage(MsgID.IP + imgUrl,
                            myself_userImg);
                } else {
                    myself_userImg.setImageResource(R.drawable.mine_account_head_default_head);
                }

                if (!StringUtil.empty(userInfo.nickname)) {
                    nickname = userInfo.nickname;
                    nickName_mine.setText("昵称：" + nickname);
                } else {
                    nickName_mine.setText("昵称：" + "新新农人");
                }
                if (!StringUtil.empty(userInfo.userTypeInName)) {
                    mine_type.setText("类型：" + userInfo.userTypeInName);
                } else {
                    mine_type.setText("类型：还没填写呦~");
                }

                if (userInfo.isVerified) {
                    isVerified.setVisibility(View.VISIBLE);
                } else {
                    isVerified.setVisibility(View.GONE);
                }

                if (userInfo.isRSC) {

                    my_order_open_ll.setVisibility(View.GONE);
                    my_state_ll.setVisibility(View.VISIBLE);

                }else {
                    my_order_open_ll.setVisibility(View.VISIBLE);
                    my_state_ll.setVisibility(View.GONE);
                }


                if (userInfo.userAddress != null) {

                    String province = "";
                    String city = "";
                    String county = "";
                    String town = "";
                    if (userInfo.userAddress.province != null) {
                        province = userInfo.userAddress.province.name;
                    }
                    if (userInfo.userAddress.city != null) {
                        city = userInfo.userAddress.city.name;
                    }
                    if (userInfo.userAddress.county != null) {
                        county = userInfo.userAddress.county.name;
                    }
                    if (userInfo.userAddress.town != null) {
                        town = userInfo.userAddress.town.name;
                    }

                    String address = StringUtil.checkBufferStr
                            (province, city, county, town);
                    if (address.equals("")) {
                        userAaddress_mine.setText("所在地区：还没填写呦~");
                    } else {
                        userAaddress_mine.setText("所在地区：" + address);
                    }
                } else {
                    userAaddress_mine.setText("所在地区：还没填写呦~");
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
        myself_userImg = (HeadImageView) findViewById(R.id.myself_userImg);//用户头像
        userAaddress_mine = (TextView) findViewById(R.id.userAaddress_mine);//用户地址
        nickName_mine = (TextView) findViewById(R.id.nickName_mine);//真实姓名
        mine_type = (TextView) findViewById(R.id.mine_type);//用户类型
        isVerified = (ImageView) findViewById(R.id.mine_type_isVerified);//是否是认证用户

        my_order_open_ll = (LinearLayout) findViewById(R.id.my_order_open_ll);
        my_order_open_ll.setVisibility(View.GONE);
        my_state_ll = (LinearLayout) findViewById(R.id.my_state_ll);

        setViewClick(R.id.my_state_ll);
        setViewClick(R.id.my_order_ll);
        setViewClick(R.id.my_yaoqing_ll);
        setViewClick(R.id.my_jifen_ll);
        setViewClick(R.id.my_set);
        setViewClick(R.id.my_kefudianhua);
        setViewClick(R.id.my_login_cancel);
        setViewClick(R.id.my_login_sure);
        setViewClick(R.id.titleview_toMyCount);


        setViewClick(R.id.mine_button2);
        setViewClick(R.id.mine_button3);
        setViewClick(R.id.mine_button4);
        setViewClick(R.id.mine_button5);

        //修改用信息通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (isLogin()) {
                    getData();
                    titleview_login.setVisibility(View.VISIBLE);
                    titleView.setVisibility(View.GONE);
                } else {
                    titleView.setVisibility(View.VISIBLE);
                    titleview_login.setVisibility(View.GONE);
                }
            }
        }, MsgID.UPDATE_USER);
        //登陆通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (isLogin()) {
                    titleview_login.setVisibility(View.VISIBLE);
                    titleView.setVisibility(View.GONE);
                    getData();
                } else {
                    titleView.setVisibility(View.VISIBLE);
                    titleview_login.setVisibility(View.GONE);
                }
            }
        }, MsgID.ISLOGIN);
        //退出登录通知
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                myself_userImg.setBackgroundResource(0);
                myself_userImg
                        .setBackgroundResource(R.drawable.person_head_img);
                nickName_mine.setText("游客");
                userAaddress_mine.setText("");
            }
        }, MsgID.CLEAR_USER);

    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.titleview_toMyCount:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(this, MyaccountActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.my_state_ll:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            RSCOrderListActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.my_order_ll:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 0);
                    startActivity(intent);
                }
                break;
            case R.id.my_yaoqing_ll:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(this, NewFramerInvite.class);
                    startActivity(intent);
                }
                break;
            case R.id.my_jifen_ll:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("integral", integral);
                    IntentUtil.activityForward(MineActivity.this,
                            MyIntegralActivity.class, bundle, false);
                }
                break;

            case R.id.my_kefudianhua:
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        MineActivity.this);
                builder.setMessage("400-056-0371")
                        .setPositiveButton("拨打",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:"
                                                + "4000560371"));
                                        dialog.dismiss();
                                        // 开启系统拨号器
                                        startActivity(intent);
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
            case R.id.my_set:
                IntentUtil.activityForward(MineActivity.this,
                        SettingActivity.class, null, false);
                break;
            case R.id.my_login_cancel:
                if (!isLogin())
                    IntentUtil.activityForward(MineActivity.this,
                            RegisterActivity.class, null, false);
                break;
            case R.id.my_login_sure:
                if (!isLogin()) {
                    Intent intent = new Intent(MineActivity.this,
                            LoginActivity.class);
                    intent.putExtra("id", 4);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button2:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 1);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button3:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 2);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button4:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra("orderSelect", 3);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button5:
                if (!isLogin()) {
                    DialogShow();
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
     * 去登陆的dialog
     */
    private void DialogShow() {
        CustomDialog.Builder builder = new CustomDialog.Builder(
                MineActivity.this);
        builder.setMessage("您还没有登录,是否登录？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MineActivity.this,
                                LoginActivity.class);
                        intent.putExtra("id", 4);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        CustomDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.PERSONAL_CENTER) {
            PersonalData data = (PersonalData) req.getData();
            Data user = data.datas;
            //下载并存储头像
            final String imgUrl = user.getImageUrl();
            if (!StringUtil.empty(imgUrl)) {
                ImageLoader.getInstance().displayImage(MsgID.IP + imgUrl,
                        myself_userImg);
                SPUtils.put(getApplicationContext(), "head", MsgID.IP + imgUrl);
            } else {
                myself_userImg.setImageResource(R.drawable.mine_account_head_default_head);
                SPUtils.put(getApplicationContext(), "head", "");
            }

            if (!StringUtil.empty(user.nickname)) {
                nickname = user.nickname;
                nickName_mine.setText("昵称：" + nickname);
            } else {
                nickName_mine.setText("昵称：" + "新新农人");
            }
            if (!StringUtil.empty(user.userTypeInName)) {
                mine_type.setText("类型：" + user.userTypeInName);
            } else {
                mine_type.setText("类型：还没填写呦~");
            }

            if (user.isVerified) {
                isVerified.setVisibility(View.VISIBLE);
            } else {
                isVerified.setVisibility(View.GONE);
            }

            if (user.isRSC) {
                my_order_open_ll.setVisibility(View.GONE);
                my_state_ll.setVisibility(View.VISIBLE);
            }else {
                my_order_open_ll.setVisibility(View.VISIBLE);
                my_state_ll.setVisibility(View.GONE);
            }


            if (user.address != null) {

                String province = "";
                String city = "";
                String county = "";
                String town = "";
                if (user.address.province != null) {
                    province = user.address.province.name;
                }
                if (user.address.city != null) {
                    city = user.address.city.name;
                }
                if (user.address.county != null) {
                    county = user.address.county.name;
                }
                if (user.address.town != null) {
                    town = user.address.town.name;
                }

                String address = StringUtil.checkBufferStr
                        (province, city, county, town);
                if (address.equals("")) {
                    userAaddress_mine.setText("所在地区：还没填写呦~");
                } else {
                    userAaddress_mine.setText("所在地区：" + address);
                }
            } else {
                userAaddress_mine.setText("所在地区：还没填写呦~");
            }
            if (StringUtil.checkStr(user.pointLaterTrade)) {
                integral = user.pointLaterTrade;
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
            exitLogin();
            titleView.setVisibility(View.VISIBLE);
            titleview_login.setVisibility(View.GONE);
        } else {
            titleview_login.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
        }
        LoginResult.UserInfo me = Store.User.queryMe();
        if (me!=null){
            if (me.isRSC) {
                my_order_open_ll.setVisibility(View.GONE);
                my_state_ll.setVisibility(View.VISIBLE);
            }
        }

    }
}
