/**
 *
 */
package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.JifenData;
import com.ksfc.newfarmer.utils.StringUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 项目名称：newFarmer 类名称：myintegralActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 下午4:16:57
 * 修改备注：
 */
public class MyIntegralActivity extends BaseActivity {

    private TextView myIntegral_tv;

    @Override
    public int getLayout() {
        return R.layout.myintegral_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("我的积分");
        Bundle bundle = getIntent().getExtras();
        String integral = bundle.getString("integral");
        myIntegral_tv = (TextView) findViewById(R.id.myintegral_count);
        if (StringUtil.checkStr(integral)) {
            myIntegral_tv.setText(integral);
        }
        getData();
    }

    /**
     *
     */
    private void getData() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()){
            params.put("userId", Store.User.queryMe().userid);
        }
        execApi(ApiType.MY_JIFEN, params);



    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.MY_JIFEN) {
            if (req.getData().getStatus().equals("1000")){
                JifenData data = (JifenData) req.getData();
                    if (data.datas!=null){
                        if (StringUtil.checkStr(data.datas.pointLaterTrade)){
                            myIntegral_tv.setText(data.datas.pointLaterTrade);
                        }
                    }
            }
        }
    }


}
