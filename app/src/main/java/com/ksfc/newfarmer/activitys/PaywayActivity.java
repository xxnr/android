package com.ksfc.newfarmer.activitys;


import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.fragment.OrderPaidFragment;
import com.ksfc.newfarmer.fragment.PayWayFragment;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.RndLog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ksfc.newfarmer.App;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;


public class PaywayActivity extends BaseActivity {

    private FragmentManager fragmentManager;
    private String orderId;
    private int payType;
    private String price;

    @Override
    public int getLayout() {
        return R.layout.activity_payway;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        App.getApp().partQuit();
        RndApplication.tempDestroyActivityList.add(PaywayActivity.this);
        initView();
        showProgressDialog();
        getData();

        //登陆通知 银联支付成功后的金额
        MsgCenter.addListener(new MsgListener() {

            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                if (args != null && args.length > 0) {
                    price = (String) args[0];
                }
            }
        }, MsgID.PAY_PRICE);

    }

    //请求订单详情
    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("orderId", orderId);
        execApi(ApiType.GET_ORDER_DETAILS, params);
    }


    private void initView() {
        setTitle("");
        fragmentManager = getSupportFragmentManager();
        orderId = getIntent().getStringExtra("orderId");
        payType = getIntent().getIntExtra("payType", 1);//支付类型

    }

    @Override
    public void OnViewClick(View v) {


    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {

                MyOrderDetailResult.Datas datas = data.datas;
                if (datas != null
                        && datas.rows != null
                        && datas.rows.order != null
                        && datas.rows.order.orderStatus != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    switch (datas.rows.order.orderStatus.type) {
                        case 1:
                        case 2:
                        case 7:
                            setTitle("支付方式");
                            PayWayFragment payWayFragment = new PayWayFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("orderDetail", datas);
                            bundle.putInt("payType", payType);
                            payWayFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.payWay_frameLayout, payWayFragment);
                            break;
                        default:
                            setTitle("订单已支付");
                            OrderPaidFragment orderPaidFragment = new OrderPaidFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("orderId", orderId);
                            orderPaidFragment.setArguments(bundle1);
                            fragmentTransaction.replace(R.id.payWay_frameLayout, orderPaidFragment);
                            MsgCenter.fireNull(MsgID.order_Change);//订单状态改变需要刷新列表
                            break;
                    }
                    fragmentTransaction.commitAllowingStateLoss();

                }
            }
        }
    }

    //银联回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        Bundle extras = data.getExtras();
        if (extras != null) {
            String str = extras.getString("pay_result");
            RndLog.v(TAG, str);
            if (str != null) {
                if (str.equalsIgnoreCase("success")) {
                    // 验证通过后，显示支付结果
                    msg = "支付成功！";
                    Intent intent = new Intent(PaywayActivity.this,
                            OrderSuccessActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("price", price);
                    startActivity(intent);
                } else if (str.equalsIgnoreCase("fail")) {
                    msg = "支付失败！";
                } else if (str.equalsIgnoreCase("cancel")) {
                    msg = "用户取消了支付";
                }
            }
            // 支付完成,处理自己的业务逻辑!
            RndLog.v(TAG, msg);
        }

    }

}
