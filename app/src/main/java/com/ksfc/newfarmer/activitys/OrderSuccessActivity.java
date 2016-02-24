package com.ksfc.newfarmer.activitys;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.utils.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.yangentao.util.msg.MsgCenter;

public class OrderSuccessActivity extends BaseActivity {
    private String orderId;
    private String price;

    @Override
    public int getLayout() {
        return R.layout.order_success_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        quit();
        RndApplication.tempDestroyActivityList.add(OrderSuccessActivity.this);
        setTitle("支付成功");
        orderId = (String) getIntent().getSerializableExtra("orderId");
        price = (String) getIntent().getSerializableExtra("price");
        TextView price_tv = (TextView) findViewById(R.id.pay_price);
        if (StringUtil.checkStr(price)) {
            price_tv.setText("支付金额：¥" + price + "元");
        }
        //通知 订单列表刷新
        MsgCenter.fireNull("Pay_success", "price");
        setViewClick(R.id.contact_tv);
        setViewClick(R.id.check_order_tv);

    }

    public void quit() {
        for (Activity activity : RndApplication.tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        RndApplication.tempDestroyActivityList.clear();
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.contact_tv:
                Intent intent = new Intent(OrderSuccessActivity.this, WaitingPayActivity.class);
                startActivity(intent);
                quit();
                break;
            case R.id.check_order_tv:
                intent = new Intent(OrderSuccessActivity.this,
                        MyOrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                quit();
                break;

            default:
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }

}
