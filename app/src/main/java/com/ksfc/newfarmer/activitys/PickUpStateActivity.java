package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.DeliveryCodeResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.UnSwipeListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HePeng on 2016/3/22.
 */
public class PickUpStateActivity extends BaseActivity {
    private TextView pick_up_code; //自提码
    private TextView RSC_companyName; //网点名称
    private TextView RSC_Address; //网店地址
    private TextView RSC_phone; //手机号
    private UnSwipeListView pick_up_listView;//可自提商品列表

    @Override
    public int getLayout() {
        return R.layout.pick_up_state;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("网点自提");
        initView();

        String orderId = getIntent().getStringExtra("orderId");
        if (StringUtil.checkStr(orderId)) {
            showProgressDialog();
            //获取自提码
            RequestParams params = new RequestParams();
            params.put("orderId", orderId);
            if (isLogin()) {
                params.put("userId", Store.User.queryMe().userid);
            }
            execApi(ApiType.GET_DELIVERY_CODE.setMethod(ApiType.RequestMethod.GET), params);

            //获取商品详情 从而获得自提商品
            RequestParams params1 = new RequestParams();
            if (isLogin()) {
                params1.put("userId", Store.User.queryMe().userid);
            }
            params1.put("orderId", orderId);
            execApi(ApiType.GET_ORDER_DETAILS, params1);
        }
    }

    private void initView() {
        pick_up_code = (TextView) findViewById(R.id.pick_up_code);
        RSC_companyName = (TextView) findViewById(R.id.RSC_companyName);
        RSC_Address = (TextView) findViewById(R.id.RSC_Address);
        RSC_phone = (TextView) findViewById(R.id.RSC_phone);
        pick_up_listView = (UnSwipeListView) findViewById(R.id.pick_up_listView);
    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_DELIVERY_CODE) {
            if (req.getData().getStatus().equals("1000")) {
                DeliveryCodeResult data = (DeliveryCodeResult) req.getData();
                if (StringUtil.checkStr(data.deliveryCode)) {
                    pick_up_code.setText(data.deliveryCode);
                }

            }

        } else if (req.getApi() == ApiType.GET_ORDER_DETAILS) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {

                MyOrderDetailResult.Datas orderInfo = data.datas;
                //设置RSCInfo
                if (orderInfo != null && orderInfo.rows != null && orderInfo.rows.RSCInfo != null) {
                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.companyName)) {
                        RSC_companyName.setText(orderInfo.rows.RSCInfo.companyName);
                    }

                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCAddress)) {
                        RSC_Address.setText(orderInfo.rows.RSCInfo.RSCAddress);
                    }

                    if (StringUtil.checkStr(orderInfo.rows.RSCInfo.RSCPhone)) {
                        RSC_phone.setText(orderInfo.rows.RSCInfo.RSCPhone);
                    }
                }

                //过滤 带自提商品列表  展示
                if (orderInfo != null && orderInfo.rows != null && orderInfo.rows.SKUList != null) {
                    List<MyOrderDetailResult.Rows.SKUS> skuList = new ArrayList<>();
                    List<MyOrderDetailResult.Rows.SKUS> skuList1 = orderInfo.rows.SKUList;

                    for (int i = 0; i < skuList1.size(); i++) {
                        MyOrderDetailResult.Rows.SKUS skus = skuList1.get(i);
                        if (skus != null) {
                            if (skus.deliverStatus.equals("4")) {
                                skuList.add(skus);
                            }
                        }
                    }

                    if (!skuList.isEmpty()) {
                        pick_up_listView.setAdapter(new PickUpGoodsAdapter(this, skuList));
                    }


                }


            }


        }

    }

    class PickUpGoodsAdapter extends CommonAdapter<MyOrderDetailResult.Rows.SKUS> {


        public PickUpGoodsAdapter(Context context, List<MyOrderDetailResult.Rows.SKUS> data) {
            super(context, data, R.layout.item_pickupstate_goods_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, MyOrderDetailResult.Rows.SKUS skus) {

            if (skus != null) {

                //商品个数
                holder.setText(R.id.sku_count, "X " + skus.count + "");
                //商品名
                if (StringUtil.checkStr(skus.productName)) {
                    holder.setText(R.id.sku_name, skus.productName);
                } else {
                    holder.setText(R.id.sku_name, "");
                }
                //Sku属性
                StringBuilder stringBuilder = new StringBuilder();
                if (skus.attributes != null && !skus.attributes.isEmpty()) {
                    for (int k = 0; k < skus.attributes.size(); k++) {
                        if (StringUtil.checkStr(skus.attributes.get(k).name)
                                && StringUtil.checkStr(skus.attributes.get(k).value)) {
                            stringBuilder.append(skus.attributes.get(k).name + ":")
                                    .append(skus.attributes.get(k).value + ";");
                        }
                    }
                    String car_attr = stringBuilder.substring(0, stringBuilder.length() - 1);
                    if (StringUtil.checkStr(car_attr)) {
                        holder.setText(R.id.sku_attr, car_attr);
                    } else {
                        holder.setText(R.id.sku_attr, "");
                    }
                } else {
                    holder.setText(R.id.sku_attr, "");
                }

                //附加选项
                TextView sku_addiction = (TextView) holder.getView(R.id.sku_addiction);

                StringBuilder stringAdditions = new StringBuilder();
                if (skus.additions != null && !skus.additions.isEmpty()) {
                    stringAdditions.append("附加项目:");
                    for (int k = 0; k < skus.additions.size(); k++) {
                        if (StringUtil.checkStr(skus.additions.get(k).name)) {
                            stringAdditions.append(skus.additions.get(k).name + ";");
                        }
                    }
                    String car_additions = stringAdditions.substring(0, stringAdditions.length() - 1);
                    if (StringUtil.checkStr(car_additions)) {
                        sku_addiction.setVisibility(View.VISIBLE);
                        sku_addiction.setText(car_additions);
                    } else {
                        sku_addiction.setText("");
                    }
                } else {
                    sku_addiction.setVisibility(View.GONE);
                }


            }

        }
    }


}
