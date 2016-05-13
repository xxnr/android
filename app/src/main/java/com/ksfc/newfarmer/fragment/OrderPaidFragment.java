package com.ksfc.newfarmer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.MainActivity;
import com.ksfc.newfarmer.activitys.MyOrderDetailActivity;
import com.ksfc.newfarmer.protocol.Request;

import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;

/**
 * Created by CAI on 2016/5/13. 订单已支付
 */
public class OrderPaidFragment extends BaseFragment {
    private String orderId;

    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.order_paid_layout, null);
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getString("orderId");
        }
        TextView contact_tv = (TextView) view.findViewById(R.id.contact_tv);
        TextView check_order_tv = (TextView) view.findViewById(R.id.check_order_tv);
        contact_tv.setOnClickListener(this);
        check_order_tv.setOnClickListener(this);

        return view;
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.contact_tv://查看订单
                Intent intent = new Intent(activity,
                        MyOrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                App.getApp().partQuit();
                break;
            case R.id.check_order_tv://去首页
                Intent intent1 = new Intent(activity, MainActivity.class);
                intent1.putExtra("id", 1);
                startActivity(intent1);
                //通知 首页选中的位置
                MsgCenter.fireNull(MsgID.MainActivity_select_tab, 1);
                App.getApp().partQuit();
                break;
        }
    }


    @Override
    public void onResponsed(Request req) {

    }


}
