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
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.widget.CustomDialog;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.HeadImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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


    @Override
    public int getLayout() {
        return R.layout.mine_layout;

    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我的新农人");
        hideLeft();
        initView();

        titleview_login = (RelativeLayout) findViewById(R.id.titleview_login);
        titleView = (RelativeLayout) findViewById(R.id.titleview);

        if (isLogin()) {
            getData();
            titleview_login.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            titleview_login.setVisibility(View.GONE);
        }

    }

    /**
     *
     */
    private void getData() {
        // app/user/personalCenter
        // locationUserId:
        // userId
        // showProgressDialog("正在加载中..");
        RequestParams params = new RequestParams();
        final String userId = this.isLogin() ? Store.User.queryMe().userid
                : "8089f4e36e";
        params.put("locationUserId", userId);
        params.put("userId", userId);
        params.put("flags", "address");
        execApi(ApiType.PERSONAL_CENTER, params);
        showProgressDialog();
    }

    private void initView() {
        myself_userImg = (HeadImageView) findViewById(R.id.myself_userImg);//用户头像
        userAaddress_mine = (TextView) findViewById(R.id.userAaddress_mine);//用户地址
        nickName_mine = (TextView) findViewById(R.id.nickName_mine);//真实姓名
        mine_type = (TextView) findViewById(R.id.mine_type);//用户类型
        isVerified = (ImageView) findViewById(R.id.mine_type_isVerified);//是否是认证用户

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


        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                getData();
            }
        }, MsgID.UPDATE_USER);
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
            case R.id.my_order_ll:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            WaitingPayActivity.class);
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
                    IntentUtil.activityForward(MineActivity.this,
                            MyIntegralActivity.class, null, false);
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
                    finish();
                }
                break;
            case R.id.mine_button2:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            WaitingPayActivity.class);
                    intent.putExtra("orderSelect", 1);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button3:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            WaitingPayActivity.class);
                    intent.putExtra("orderSelect", 2);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button4:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            WaitingPayActivity.class);
                    intent.putExtra("orderSelect", 3);
                    startActivity(intent);
                }
                break;
            case R.id.mine_button5:
                if (!isLogin()) {
                    DialogShow();
                } else {
                    Intent intent = new Intent(MineActivity.this,
                            WaitingPayActivity.class);
                    intent.putExtra("orderSelect", 4);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    /**
     *
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
                        finish();
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


            if (!StringUtil.empty(user.userType)) {
                mine_type.setText("类型：" + setUserType(user.userType));
            } else {
                mine_type.setText("类型：还没填写呦~");
            }

            if (user.isVerified) {
                isVerified.setVisibility(View.VISIBLE);
            } else {
                isVerified.setVisibility(View.GONE);
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
            saveMe(user);
        }
    }

    //存储个人信息到本地
    public void saveMe(Data user) {
        LoginResult.UserInfo me = Store.User.queryMe();
        me.photo = user.getImageUrl();

        if (user.defaultAddress != null) {
            me.defaultAddress = user.defaultAddress.country.replace("undefined", "") + user.defaultAddress.address;
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
        Store.User.saveMe(me);
    }


    public String setUserType(String i) {
        String userType = "";
        int i1 = 1;
        try {
            i1 = Integer.parseInt(i);
        } catch (Exception e) {
            RndLog.d(TAG, e.getMessage());
            i1 = 1;
        }
        switch (i1) {
            case 1:
                userType = "其他";
                break;
            case 2:
                userType = "种植大户";
                break;
            case 3:
                userType = "村级经销商";
                break;
            case 4:
                userType = "乡镇经销商";
                break;
            case 5:
                userType = "县级经销商";
                break;
            default:
                userType = "其他";
                break;
        }

        return userType;

    }


}
