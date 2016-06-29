package com.ksfc.newfarmer.fragment;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.ResponseResult;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.http.beans.MyInviterResult;
import com.ksfc.newfarmer.http.beans.NominatedInviterResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.dialog.CustomDialog;
import com.ksfc.newfarmer.widget.dialog.CustomDialogForInviter;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;




public class MyInviterFragment extends BaseFragment {

    private EditText edit;//输入代表人的手机号
    private String phoneNumber;//手机号
    private LinearLayout noneInviterView;//没有代表的布局
    private LinearLayout inviterView;//有代表的布局
    private String phoneNum;//手机号
    private TextView nickname_tv;
    private TextView phoneNum_tv;
    private String user_phone;
    private ImageView my_inviter_sex;
    private TextView my_inviter_userType;
    private ImageView my_inviter_userType_icon;
    private TextView my_inviter_address;


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.fragment_my_inviter, null);
        //未设置新农代表的View
        noneInviterView = (LinearLayout) view.findViewById(R.id.my_inviter_fragment);
        edit = (EditText) view.findViewById(R.id.my_inviter_fragment_edittext);
        Button addInvite = (Button) view.findViewById(R.id.add_inviter);
        addInvite.setOnClickListener(this);
        noneInviterView.setVisibility(View.GONE);

        //设置完成新农代表的View
        inviterView = (LinearLayout) view
                .findViewById(R.id.my_inviter_fragment_lin);
        nickname_tv = (TextView) view.findViewById(R.id.my_inviter_nickname);
        phoneNum_tv = (TextView) view.findViewById(R.id.my_inviter_phone);

        LinearLayout my_inviter_phone_ll = (LinearLayout) view.findViewById(R.id.my_inviter_phone_ll);
        my_inviter_phone_ll.setOnClickListener(this);

        my_inviter_sex = (ImageView) view.findViewById(R.id.my_inviter_sex);

        my_inviter_userType = (TextView) view.findViewById(R.id.my_inviter_userType);
        my_inviter_userType_icon = (ImageView) view.findViewById(R.id.my_inviter_userType_icon);
        my_inviter_address = (TextView) view.findViewById(R.id.my_inviter_address);

        inviterView.setVisibility(View.GONE);
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            user_phone = userInfo.phone;
        }

        //请求我的新农代表
        showProgressDialog();
        getMyinviter();
        return view;
    }

    //请求我的新农代表
    public void getMyinviter() {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            String userId = userInfo.userid;
            params.put("userId", userId);
        }
        execApi(ApiType.GET_MY_INVITER.setMethod(ApiType.RequestMethod.GET), params);
    }


    @Override
    public void OnViewClick(View v) {
        phoneNumber = edit.getText().toString();
        switch (v.getId()) {

            case R.id.add_inviter:
                if (TextUtils.isEmpty(phoneNumber)) {
                    showToast("请输入一个手机号码");
                    return;
                }

                if (!Utils.isMobileNum(phoneNumber)) {
                    showToast("您输入的手机号码格式不正确");
                    return;
                }

                if (phoneNumber.equals(user_phone)) {
                    showToast("不能设置自己为新农代表哦");
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("account", phoneNumber);
                execApi(ApiType.FIND_USER, params);
                break;
            case R.id.my_inviter_phone_ll:

                if (StringUtil.checkStr(phoneNum)) {
                    Utils.dial(activity, phoneNum);
                }

                break;
        }
    }


    @Override
    public void onResponsed(Request req) {
        disMissDialog();

        // TODO Auto-generated method stub
        if (req.getApi() == ApiType.GET_BINDINVITER) {
            if (req.getData().getStatus().equals("1000")) {
                showToast("绑定成功");
                //绑定成功后 获取一次个人信息用于展示
                noneInviterView.setVisibility(View.GONE);
                //请求我的新农代表
                getMyinviter();
            }
        } else if (req.getApi() == ApiType.GET_MY_INVITER) {
            if (req.getData().getStatus().equals("1000")) {

                MyInviterResult data = (MyInviterResult) req.getData();
                if (data.datas != null) {
                    phoneNum = data.datas.inviterPhone;
                    String nickname = data.datas.inviterName;
                    if (StringUtil.checkStr(phoneNum)) {
                        inviterView.setVisibility(View.VISIBLE);
                        noneInviterView.setVisibility(View.GONE);
                        //名字和号码
                        if (!TextUtils.isEmpty(nickname)) {//如果有昵称 设置一些属性
                            nickname_tv.setText(nickname);
                        }
                        phoneNum_tv.setText("电话号码：" + phoneNum);
                        //用户类型
                        my_inviter_userType.setText("用户类型：" + data.datas.inviterUserTypeInName);
                        if (data.datas.inviterIsVerified) {
                            my_inviter_userType_icon.setVisibility(View.VISIBLE);
                        } else {
                            my_inviter_userType_icon.setVisibility(View.GONE);
                        }

                        //性别
                        if (data.datas.inviterSex) {
                            my_inviter_sex.setImageResource(R.drawable.girl_icon);
                        } else {
                            my_inviter_sex.setImageResource(R.drawable.boy_icon);
                        }
                        //所在地区
                        if (data.datas.inviterAddress != null) {

                            String province = "";
                            String city = "";
                            String county = "";
                            String town = "";
                            if (data.datas.inviterAddress.province != null) {
                                province = data.datas.inviterAddress.province.name;
                            }
                            if (data.datas.inviterAddress.city != null) {
                                city = data.datas.inviterAddress.city.name;
                            }
                            if (data.datas.inviterAddress.county != null) {
                                county = data.datas.inviterAddress.county.name;
                            }
                            if (data.datas.inviterAddress.town != null) {
                                town = data.datas.inviterAddress.town.name;
                            }

                            String address = StringUtil.checkBufferStr
                                    (province, city, county, town);
                            if (address.equals("")) {
                                my_inviter_address.setText("所在地区：");
                            } else {
                                my_inviter_address.setText("所在地区：" + address);
                            }
                        }

                    } else {
                        inviterView.setVisibility(View.GONE);
                        noneInviterView.setVisibility(View.VISIBLE);
                        //没有添加新农代表 获取推荐代表
                        if (Store.User.queryMe() != null) {
                            if (noneInviterView != null && noneInviterView.getVisibility() == View.VISIBLE) {//当前用户未设置新农代表
                                getRecommend();
                            }
                        }
                    }
                }


            }

        } else if (req.getApi() == ApiType.FIND_USER) {
            ResponseResult data = req.getData();
            if (data.getStatus().equals("1000")) {
                CustomDialog.Builder builder = new CustomDialog.Builder(
                        activity);
                builder.setMessage("代表人添加后不可修改,确定设置该用户为您的代表吗?")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        RequestParams params = new RequestParams();
                                        LoginResult.UserInfo userInfo = Store.User.queryMe();
                                        if (userInfo != null) {
                                            String uid = userInfo.userid;
                                            params.put("userId", uid);
                                        }
                                        params.put("inviter", phoneNumber);
                                        execApi(ApiType.GET_BINDINVITER, params);
                                        dialog.dismiss();
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
                CustomDialog dialog = builder.create();
                dialog.show();
            } else {
                showToast("该手机号未注册，请确认后重新输入");
            }
        } else if (req.getApi() == ApiType.GET_RECOMMEND_INVITER) {
            NominatedInviterResult reqData = (NominatedInviterResult) req.getData();
            final NominatedInviterResult.Datas datas = reqData.nominated_inviter;
            if (datas != null) {
                if (StringUtil.checkStr(datas.phone) && StringUtil.checkStr(datas.name)) {
                    CustomDialogForInviter.Builder builder = new CustomDialogForInviter.Builder(activity);  //先得到构造器
                    builder.setTitle("是否要添加该用户为您的代表？"); //设置标题
                    builder.setMessage(datas.name); //设置内容
                    builder.setMessage2(datas.phone);
                    builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = new RequestParams();
                            LoginResult.UserInfo userInfo = Store.User.queryMe();
                            if (userInfo != null) {
                                String uid = userInfo.userid;
                                params.put("userId", uid);
                            }
                            params.put("inviter", datas.phone);
                            execApi(ApiType.GET_BINDINVITER, params);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    /**
     * 获取推荐代表
     */
    public void getRecommend() {
        RequestParams params1 = new RequestParams();
        params1.put("token", Store.User.queryMe().token);
        execApi(ApiType.GET_RECOMMEND_INVITER.setMethod(ApiType.RequestMethod.GET), params1);
    }

}
