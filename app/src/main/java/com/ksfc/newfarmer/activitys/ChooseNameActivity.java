package com.ksfc.newfarmer.activitys;

import net.yangentao.util.msg.MsgCenter;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.LoginResult.UserInfo;
import com.ksfc.newfarmer.utils.MaxLengthWatcher;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChooseNameActivity extends BaseActivity {
    private EditText et_modify;
    private String str;
    private TextView name_submit_tv;

    @Override
    public int getLayout() {
        return R.layout.activity_set_name;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("我的昵称");
        setViewClick(R.id.name_submit_tv);

        name_submit_tv = (TextView) findViewById(R.id.name_submit_tv);
        name_submit_tv.setEnabled(false);

        et_modify = (EditText) findViewById(R.id.et_modify);
        UserInfo me = Store.User.queryMe();
        if (me != null) {
            et_modify.setText(me.nickname);
        }
        et_modify.addTextChangedListener(new MaxLengthWatcher(12, et_modify, ChooseNameActivity.this));
        // 光标移到最后
        Editable eText = et_modify.getText();
        Selection.setSelection(eText, eText.length());
        //设置字数限制
        et_modify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name_submit_tv.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.name_submit_tv:
                str = et_modify.getText().toString();
                if (StringUtil.empty(str)) {
                    showToast("请您先完善信息");
                } else {
                    showProgressDialog("正在保存中...");
                    RequestParams params = new RequestParams();
                    if (isLogin()){
                        params.put("userId", Store.User.queryMe().userid);
                    }
                    params.put("nickName", str);
                    execApi(ApiType.SAVE_MYUSER, params);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (ApiType.SAVE_MYUSER == req.getApi()) {
            UserInfo queryMe = Store.User.queryMe();
            if (queryMe != null) {
                queryMe.nickname = str;
                Store.User.saveMe(queryMe);
            }
            showToast("保存成功");
            //保存用户
            Intent intent = new Intent();
            intent.putExtra("str", str);
            setResult(0x11, intent);
            MsgCenter.fireNull(MsgID.UPDATE_USER_TYPE, "update");
            finish();
        }

    }

}
