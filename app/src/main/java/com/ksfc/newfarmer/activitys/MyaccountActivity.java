package com.ksfc.newfarmer.activitys;

import java.io.File;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.ChoicePicActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.AddressList;
import com.ksfc.newfarmer.protocol.beans.CameraResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.widget.CustomDialog;
import com.ksfc.newfarmer.widget.CustomDialogForHead;
import com.ksfc.newfarmer.widget.CustomDialogForSex;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.SPUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.HeadImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

public class MyaccountActivity extends BaseActivity {
    private HeadImageView myself_userImg;
    private String path;
    private TextView nickName, address_songhuo;
    private final int nameCode = 1;
    private final int trueNameCode = 2;
    private final int honeAddress = 3;
    private final int userCode = 4;
    private UserInfo me;
    private TextView name;
    private TextView sex;
    private TextView address_home;
    private TextView type;
    private boolean flag = false;//性别


    @Override
    public int getLayout() {
        return R.layout.myaccount_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我");
        me = Store.User.queryMe();
        initView();
        setLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        nickName = (TextView) findViewById(R.id.nickName);
        address_songhuo = (TextView) findViewById(R.id.address_songhuo);
        myself_userImg = (HeadImageView) findViewById(R.id.myself_userImg);

        name = (TextView) findViewById(R.id.name_tv);
        sex = (TextView) findViewById(R.id.sex_tv);
        address_home = (TextView) findViewById(R.id.home_address_tv);
        type = (TextView) findViewById(R.id.type_tv);


        setViewClick(R.id.header_image_ll);
        setViewClick(R.id.set_name_ll);
        setViewClick(R.id.chane_pass_ll);
        setViewClick(R.id.choose_address_ll);
        setViewClick(R.id.save_userInfo);

        setViewClick(R.id.choose_name_ll);//修改姓名
        setViewClick(R.id.choose_sex_ll);
        setViewClick(R.id.choose_home_address_ll);
        setViewClick(R.id.choose_type_ll);


        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                AddressList.Address address = (AddressList.Address) args[0];
                if (address != null) {
                    address_songhuo.setText(StringUtil.checkBufferStr(address.areaName + address.cityName
                            , address.countyName
                            , address.townName
                            , address.address));
                } else {
                    address_songhuo.setText("");
                }
            }
        }, "MSG.ADDR");


        if (me == null) {
            return;
        }
        if (!TextUtils.isEmpty((String) SPUtils.get(getApplicationContext(),
                "head", ""))) {
            ImageLoader.getInstance().displayImage(
                    (String) SPUtils.get(getApplicationContext(), "head", ""),
                    myself_userImg);
        }
        nickName.setText(TextUtils.isEmpty(me.nickname) ? "" : me.nickname);
        address_songhuo
                .setText(TextUtils.isEmpty(me.defaultAddress) ? "" : me.defaultAddress);
        if (me.sex) {
            sex.setText("女");
        } else {
            sex.setText("男");
        }
        name.setText(TextUtils.isEmpty(me.name) ? "" : me.name);
        address_home.setText(TextUtils.isEmpty(me.addressCity) ? "" : me.addressCity + me.addressTown);

        if (!StringUtil.empty(me.userType)) {
            type.setText(setUserType(me.userType));
        } else {
            type.setText("还没填写呦~");
        }
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.header_image_ll:
                // 点击上传头像
                showCameraDialog();
                break;
            case R.id.set_name_ll:
                IntentUtil.startActivityForResult(this, ChooseNameActivity.class,
                        nameCode, null);
                break;
            case R.id.chane_pass_ll:
                IntentUtil.activityForward(MyaccountActivity.this,
                        ChangePasswordActivity.class, null, false);
                break;
            case R.id.choose_address_ll:
                IntentUtil.activityForward(MyaccountActivity.this,
                        AddressmanageActivity.class, null, false);
                break;
            case R.id.choose_name_ll:
                IntentUtil.startActivityForResult(this, ChoiceTrueNameActivity.class,
                        trueNameCode, null);
                break;
            case R.id.choose_sex_ll:
                CustomDialogForSex.Builder builder3 = new CustomDialogForSex.Builder(
                        MyaccountActivity.this);
                builder3.setMessage("选择性别")
                        .setPositiveButton("男",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        RequestParams params = new RequestParams();
                                        params.put("userId", Store.User.queryMe().userid);
                                        params.put("sex", "0");
                                        execApi(ApiType.SAVE_MYUSER, params);
                                        flag = false;
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setNormalButton("女", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RequestParams params = new RequestParams();
                                params.put("userId", Store.User.queryMe().userid);
                                params.put("sex", "1");
                                execApi(ApiType.SAVE_MYUSER, params);
                                flag = true;

                            }
                        }).setNegativeButtonImage(R.drawable.girl_icon).setPositiveButtonImage(R.drawable.boy_icon)
                ;
                builder3.create().show();
                break;
            case R.id.choose_home_address_ll:
                IntentUtil.startActivityForResult(this, ChoiceHomeAddress.class,
                        honeAddress, null);
                break;
            case R.id.choose_type_ll:
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("flag", true);
                IntentUtil.startActivityForResult(this, SelectUserTypeActivity.class,
                        userCode, bundle2);
                break;
            case R.id.save_userInfo:
                if (isLogin()) {
                    CustomDialog.Builder builder1 = new CustomDialog.Builder(
                            MyaccountActivity.this);
                    builder1.setMessage("确定要退出新新农人吗？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Store.User.removeMe();
                                            SPUtils.clear(getApplicationContext());
                                            showToast("您已退出登录");
                                            startActivity(new Intent(MyaccountActivity.this, MainActivity.class));
                                            finish();
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
                    builder1.create().show();
                }

                break;
            default:
                break;
        }
    }

    public void showCameraDialog() {
        CustomDialogForHead.Builder builder1 = new CustomDialogForHead.Builder(
                MyaccountActivity.this);
        builder1.setMessage("修改头像")
                .setPositiveButton("本地上传",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                //选择
                                choicePhoto(false);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                .setNormalButton("拍照上传", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // 照相
                        choicePhoto(true);
                    }
                })
        ;
        builder1.create().show();

    }

    private void choicePhoto(boolean fromCamra) {
        Intent intent = new Intent(this, ChoicePicActivity.class);
        intent.putExtra(ChoicePicActivity.EXTRA_IS_FROM_CAMRA, fromCamra);
        intent.putExtra(ChoicePicActivity.EXTRA_IS_NEED_ZOOM, true);
        startActivityForResult(intent, 998);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200 && requestCode == 998) {
            path = data.getStringExtra(ChoicePicActivity.EXTRA_PIC_PATH);
            RndLog.v("返回图片的路径:", path);
            if (!TextUtils.isEmpty(path)) {
                UserInfo me = Store.User.queryMe();
                if (me == null) {
                    return;
                }
                // 头像上传
                uploadhead(path, me.userid, "userId");
            }
        } else if (resultCode == 0x11) {//昵称

            nickName.setText(data.getStringExtra("str"));
        } else if (resultCode == 0x12) {//姓名
            name.setText(data.getStringExtra("str"));
        } else if (resultCode == 0x13) {//所在地
            address_home.setText(data.getStringExtra("str"));
        } else if (resultCode == 0x14) {
            type.setText(data.getStringExtra("str"));
        }
    }

    @Override
    public void onResponsed(Request req) {

        if (ApiType.UP_HEAD_IMG == req.getApi()) {
            CameraResult camera = (CameraResult) req.getData();
            String datas = camera.imageUrl;// 图片的URL
            UserInfo queryMe = Store.User.queryMe();
            queryMe.photo = datas;
            Store.User.saveMe(queryMe);
            Bitmap decodeFile = BitmapFactory.decodeFile(path);
            myself_userImg.setImageBitmap(decodeFile);
            MsgCenter.fireNull(MsgID.UPDATE_USER, "update");
        } else if (ApiType.SAVE_MYUSER == req.getApi()) {
            UserInfo queryMe = Store.User.queryMe();
            queryMe.sex = flag;
            Store.User.saveMe(queryMe);
            showToast("保存成功");
            if (flag) {
                sex.setText("女");
            } else {
                sex.setText("男");
            }
        }
    }

    private void uploadhead(final String path, String value, String name) {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addQueryStringParameter(name, value);
        params.addBodyParameter(path.replace("/", ""), new File(path),
                "image/jpeg");
        params.addBodyParameter("token", Store.User.queryMe().token);
        HttpUtils http = new HttpUtils();
        http.send(HttpMethod.POST, ApiType.UP_HEAD_IMG.getOpt(), params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        showToast("头像上传成功！");
                        Bitmap decodeFile = BitmapFactory.decodeFile(path);
                        myself_userImg.setImageBitmap(decodeFile);
                    }

                });
    }


    public String setUserType(String i) {
        String userType = "";
        switch (Integer.parseInt(i)) {
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
