package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.OfflinePayWayResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.UnSwipeListView;

import java.util.List;

/**
 * Created by HePeng on 2016/3/21.
 */
public class OfflinePayActivity extends BaseActivity {
    private TextView pay_price;
    private TextView RSC_companyName;
    private TextView RSC_Address;
    private TextView RSC_phone;
    private LinearLayout state_info_ll;
    private RelativeLayout none_state_info_rel;
    private String orderId;
    private UnSwipeListView offline_pay_way;

    @Override
    public int getLayout() {

        return R.layout.offline_pay_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("线下支付");
        initView();

        showRightTextView();
        setRightTextView("查看订单");
        setRightTextViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看订单
                Intent intent = new Intent(OfflinePayActivity.this,
                        MyOrderDetailActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                quit();
            }
        });

        quit();
        RndApplication.tempDestroyActivityList.add(OfflinePayActivity.this);

        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString("orderId");
        String payPrice = bundle.getString("payPrice");

        if (StringUtil.checkStr(payPrice)) {
            pay_price.setText("¥" + payPrice);
        } else {
            pay_price.setText("");
        }

        if (StringUtil.checkStr(orderId)) {
            //获取订单详情
            getData();
        }

        getPayWay();

    }

    public void quit() {
        for (Activity activity : RndApplication.tempDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        RndApplication.tempDestroyActivityList.clear();
    }

    private void getPayWay() {

        RequestParams params = new RequestParams();
        execApi(ApiType.GET_OFFLINE_PAY_WAY.setMethod(ApiType.RequestMethod.GET), params);
    }

    //获取订单详情
    private void getData() {

        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("orderId", orderId);
        execApi(ApiType.GET_ORDER_DETAILS, params);

    }

    private void initView() {
        pay_price = (TextView) findViewById(R.id.pay_price);
        RSC_companyName = (TextView) findViewById(R.id.RSC_companyName);
        RSC_Address = (TextView) findViewById(R.id.RSC_Address);
        RSC_phone = (TextView) findViewById(R.id.RSC_phone);
        state_info_ll = (LinearLayout) findViewById(R.id.state_info_ll);
        none_state_info_rel = (RelativeLayout) findViewById(R.id.none_state_info_rel);
        offline_pay_way = (UnSwipeListView) findViewById(R.id.offline_pay_way);

        state_info_ll.setVisibility(View.GONE);
        none_state_info_rel.setVisibility(View.GONE);


    }

    @Override
    public void OnViewClick(View v) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {

                MyOrderDetailResult.Datas orderInfo = data.datas;
                if (orderInfo != null && orderInfo.rows != null && orderInfo.rows.RSCInfo != null) {
                    state_info_ll.setVisibility(View.VISIBLE);
                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.companyName)) {
                        RSC_companyName.setText(orderInfo.rows.RSCInfo.companyName);
                    }

                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCAddress)) {
                        RSC_Address.setText(orderInfo.rows.RSCInfo.RSCAddress);
                    }

                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCPhone)) {
                        RSC_phone.setText(orderInfo.rows.RSCInfo.RSCPhone);
                    }
                } else {
                    none_state_info_rel.setVisibility(View.VISIBLE);
                }

                if (orderInfo != null && orderInfo.rows != null && orderInfo.rows.payment != null) {

                    if (StringUtil.checkStr(orderInfo.rows.payment.price)) {
                        pay_price.setText("¥" + orderInfo.rows.payment.price);
                    } else {
                        pay_price.setText("");
                    }
                }

            }
        } else if (ApiType.GET_OFFLINE_PAY_WAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                OfflinePayWayResult data = (OfflinePayWayResult) req.getData();
                List<OfflinePayWayResult.OfflinePayTypeEntity> offlinePayType = data.offlinePayType;
                if (offlinePayType != null && !offlinePayType.isEmpty()) {
                    offline_pay_way.setAdapter(new PayWayAdapter(this, offlinePayType));
                }
            }

        }


    }


    class PayWayAdapter extends CommonAdapter<OfflinePayWayResult.OfflinePayTypeEntity> {


        public PayWayAdapter(Context context, List<OfflinePayWayResult.OfflinePayTypeEntity> data) {
            super(context, data, R.layout.city);
        }

        @Override
        public void convert(CommonViewHolder holder, OfflinePayWayResult.OfflinePayTypeEntity offlinePayTypeEntity) {
            if (offlinePayTypeEntity != null) {

                holder.getView(R.id.city_ll).setBackgroundColor(getResources().getColor(R.color.white));
                TextView textView = (TextView) holder.getView(R.id.cityTextView);
                textView.setTextSize(16);
                textView.setText(offlinePayTypeEntity.name);


            }

        }
    }


}
