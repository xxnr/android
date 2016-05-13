package com.ksfc.newfarmer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.pay.demo.AlipayClass;
import com.google.gson.Gson;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.EposActivity;
import com.ksfc.newfarmer.activitys.OfflinePayActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.beans.AlipayResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.MinPayPriceResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.UnionPayResponse;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import net.yangentao.util.msg.MsgCenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by CAI on 2016/5/13.
 */
public class PayWayFragment extends BaseFragment {
    private ImageView alipay_img, bank_img, offline_img, pos_img;

    private int tag;
    private String orderId;
    private String price;
    private TextView payway_sumPrice_tv, payWay_order_id_tv, payWay_totalPrice_tv, payWay_order_type_tv;//应付金额 订单号 订单总额 订单状态
    private TextView payWay_pay_total_text_tv, payWay_pay_times_text_tv, payWay_pay_total_bar_tv, payWay_pay_times_bar_tv;//全额支付 分次支付
    private TextView payWay_pay_total_price_tv; //全额支付 应付金额
    private TextView describe_min_pay_price_tv;//展示给用户的最小支付金额
    private RelativeLayout payWay_pay_total_view, payWay_pay_times_view;//全额支付 分次支付
    private ImageView payWay_discount_jia, payWay_discount_jian;//分次支付里的加减
    private EditText payWay_times_price_et;//分次支付里的金额tv
    private double use_calculate = 3000.00;//分次支付里的金额 用于计算 默认3000
    private double MIN_PAY_PRICE = 3000.00;

    private TextView pay_sure_tv;

    private LinearLayout bank_dianhui_ll;//线下支付
    private TextView separatrix_line;//分界线

    private MyOrderDetailResult.Datas datas;//订单详情
    private double duePrice;  //待支付金额
    private static final String mMode = "00";// 设置测试模式:01为测试 00为正式环境

    private String token;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {

                case 0:
                    UnionPayResponse unionPayResponse = (UnionPayResponse) msg.obj;
                    if (unionPayResponse != null) {
                        String tn = unionPayResponse.tn;
                        if (StringUtil.checkStr(tn)) {
                            doStartUnionPayPlugin(activity, tn, mMode);
                        }
                    }
                    break;
                case 1://当前是白名单
                    disMissDialog();
                    //白名单下 可以输入金额 ，并控制 不可以加减。
                    payWay_discount_jian.setEnabled(false);
                    payWay_discount_jia.setEnabled(false);
                    payWay_times_price_et.setEnabled(true);

                    break;
                case 404://请求错误
                    disMissDialog();
                    break;
            }

        }
    };


    @Override
    public View InItView() {
        View view = inflater.inflate(R.layout.payway_layout, null);

        alipay_img = (ImageView) view.findViewById(R.id.alipay_img);
        bank_img = (ImageView) view.findViewById(R.id.bank_img);
        offline_img = (ImageView) view.findViewById(R.id.dianhui_img);
        pos_img = (ImageView) view.findViewById(R.id.pos_img);


        //应付金额 订单号 订单总额 订单状态
        payway_sumPrice_tv = (TextView) view.findViewById(R.id.payway_sumPrice);
        payWay_totalPrice_tv = (TextView) view.findViewById(R.id.payWay_totalPrice);
        payWay_order_id_tv = (TextView) view.findViewById(R.id.payWay_order_id);
        payWay_order_type_tv = (TextView) view.findViewById(R.id.payWay_order_type);

        //全额支付 分次支付 title bar View
        payWay_pay_total_text_tv = (TextView) view.findViewById(R.id.payWay_pay_total_text);
        payWay_pay_times_text_tv = (TextView) view.findViewById(R.id.payWay_pay_times_text);
        payWay_pay_total_bar_tv = (TextView) view.findViewById(R.id.payWay_pay_total_bar);
        payWay_pay_times_bar_tv = (TextView) view.findViewById(R.id.payWay_pay_times_bar);
        payWay_pay_total_view = (RelativeLayout) view.findViewById(R.id.payWay_pay_total_view);
        payWay_pay_times_view = (RelativeLayout) view.findViewById(R.id.payWay_pay_times_view);
        //全额支付里的订单价钱
        payWay_pay_total_price_tv = (TextView) view.findViewById(R.id.payWay_pay_total_price);

        //加减按纽 和分次支付编辑框
        payWay_discount_jia = (ImageView) view.findViewById(R.id.payWay_discount_jia);
        payWay_discount_jian = (ImageView) view.findViewById(R.id.payWay_discount_jian);
        payWay_times_price_et = (EditText) view.findViewById(R.id.payWay_discount_price);
        payWay_times_price_et.setEnabled(false);
        describe_min_pay_price_tv = (TextView) view.findViewById(R.id.describe_min_pay_price);


        bank_dianhui_ll = (LinearLayout) view.findViewById(R.id.bank_dianhui_ll);
        separatrix_line = (TextView) view.findViewById(R.id.separatrix_line);


        view.findViewById(R.id.alipay_ll).setOnClickListener(this);
        view.findViewById(R.id.bank_ll).setOnClickListener(this);
        view.findViewById(R.id.pos_ll).setOnClickListener(this);
        view.findViewById(R.id.bank_dianhui_ll).setOnClickListener(this);

        view.findViewById(R.id.payWay_pay_total_rel).setOnClickListener(this);
        view.findViewById(R.id.payWay_pay_times_rel).setOnClickListener(this);

        pay_sure_tv = (TextView) view.findViewById(R.id.pay_sure_tv);
        pay_sure_tv.setOnClickListener(this);
        pay_sure_tv.setEnabled(false);

        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            token = userInfo.token;
        }

        initCircle();
        setData();

        return view;
    }

    //初始化 circle图标
    private void initCircle() {
        alipay_img.setBackgroundResource(R.drawable.circle_gray);
        bank_img.setBackgroundResource(R.drawable.circle_gray);
        offline_img.setBackgroundResource(R.drawable.circle_gray);
        pos_img.setBackgroundResource(R.drawable.circle_gray);
    }

    private void setData() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            int payType = bundle.getInt("payType", 1);
            switch (payType) {
                case 1:
                    // 支付宝支付
                    alipay_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 1;
                    break;
                case 2:
                    // 银联支付
                    bank_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 2;
                    break;
                // 现金支付 3 Pos机支付 4
                case 3:
                case 4:
                    // 银行电汇
                    offline_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 3;
                    break;
                case 5:
                    // Epos支付
                    pos_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 4;
                    break;
                default:
                    // 默认选中支付宝支付
                    alipay_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 1;
                    break;

            }

            datas = (MyOrderDetailResult.Datas) bundle.getSerializable("orderDetail");

            if (datas != null) {
                if (datas.rows != null) {
                    orderId = datas.rows.id;
                    //获取最小支付金额
                    getMinPayPrice();
                    //获取白名单
                    getWriteList();
                    //订单号
                    payWay_order_id_tv.setText(datas.rows.id);
                    pay_sure_tv.setEnabled(true);//支付按钮
                    //订单ID
                    if (StringUtil.checkStr(datas.rows.id)) {
                        payWay_order_id_tv.setText(datas.rows.id);
                    }
                    //待付金额
                    if (datas.rows.payment != null) {
                        payway_sumPrice_tv.setText(datas.rows.payment.price + "元");
                        //大于min_price才能分次 ，如果小于min_price 直接显示
                        try {
                            duePrice = Double.parseDouble(datas.rows.payment.price);
                            if (duePrice >= MIN_PAY_PRICE) {
                                payWay_discount_jia.setOnClickListener(this);
                                payWay_discount_jian.setOnClickListener(this);

                                payWay_discount_jia.setEnabled(true);
                                payWay_discount_jian.setEnabled(true);

                                payWay_times_price_et.setText(StringUtil.toTwoString(MIN_PAY_PRICE + ""));
                            } else {
                                payWay_discount_jian.setEnabled(false);
                                payWay_discount_jia.setEnabled(false);
                                payWay_times_price_et.setText(StringUtil.toTwoString(duePrice + ""));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //全额支付
                        payWay_pay_total_price_tv.setText("¥" + datas.rows.payment.price);
                    }
                    //订单状态
                    if (StringUtil.checkStr(datas.rows.paySubOrderType)) {

                        switch (datas.rows.paySubOrderType) {
                            case "deposit":
                                payWay_order_type_tv.setText("阶段一：订金");
                                break;
                            case "balance":
                                payWay_order_type_tv.setText("阶段二：尾款");
                                break;
                            case "full":
                                payWay_order_type_tv.setText("订单总额");
                                break;
                        }
                    }

                    //订单总额
                    if (datas.rows.order != null) {
                        if (StringUtil.checkStr(datas.rows.order.totalPrice)) {
                            payWay_totalPrice_tv.setText(datas.rows.order.totalPrice + "元");
                        }
                    }
                }
            }
        }


    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.payWay_pay_total_rel://全额支付
                payWay_pay_total_view.setVisibility(View.VISIBLE);
                payWay_pay_times_view.setVisibility(View.GONE);
                payWay_pay_total_bar_tv.setVisibility(View.VISIBLE);
                payWay_pay_times_bar_tv.setVisibility(View.INVISIBLE);
                payWay_pay_times_text_tv.setTextColor(getResources().getColor(R.color.main_index_gary));
                payWay_pay_total_text_tv.setTextColor(getResources().getColor(R.color.orange));

                bank_dianhui_ll.setVisibility(View.VISIBLE);
                separatrix_line.setVisibility(View.VISIBLE);

                break;
            case R.id.payWay_pay_times_rel://分次支付
                payWay_pay_total_view.setVisibility(View.GONE);
                payWay_pay_times_view.setVisibility(View.VISIBLE);
                payWay_pay_total_bar_tv.setVisibility(View.INVISIBLE);
                payWay_pay_times_bar_tv.setVisibility(View.VISIBLE);
                payWay_pay_times_text_tv.setTextColor(getResources().getColor(R.color.orange));
                payWay_pay_total_text_tv.setTextColor(getResources().getColor(R.color.main_index_gary));
                bank_dianhui_ll.setVisibility(View.GONE);
                separatrix_line.setVisibility(View.GONE);
                if (tag == 3) {
                    initCircle();
                    alipay_img.setBackgroundResource(R.drawable.circle_orange);
                    tag = 1;
                }
                break;
            case R.id.payWay_discount_jia:
                changePayPrice(true);
                break;
            case R.id.payWay_discount_jian:
                changePayPrice(false);
                break;
            case R.id.alipay_ll://支付宝
                initCircle();
                alipay_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 1;
                break;
            case R.id.bank_ll://银联
                initCircle();
                bank_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 2;
                break;
            case R.id.bank_dianhui_ll://线下支付
                initCircle();
                offline_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 3;
                break;
            case R.id.pos_ll://POS机
                initCircle();
                pos_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 4;
                break;
            case R.id.pay_sure_tv:
                if (payWay_pay_times_view.getVisibility() == View.VISIBLE) {//当分次付款时
                    switch (tag) {
                        case 1:
                            getAli(false);
                            break;
                        case 2:
                            getUnipay();
                            break;
                        case 4://epos
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("orderInfo", datas);
                            if (StringUtil.checkStr(payWay_times_price_et.getText().toString().trim())) {
                                bundle.putString("payPrice", payWay_times_price_et.getText().toString().trim());
                            }
                            IntentUtil.activityForward(activity, EposActivity.class, bundle, false);
                            break;
                    }
                } else {//当分次付款时，执行
                    switch (tag) {
                        case 1:
                            getAli(true);
                            break;
                        case 2:
                            getUnipay();
                            break;
                        case 3://线下支付
                            showProgressDialog();
                            RequestParams params = new RequestParams();
                            if (StringUtil.checkStr(token)) {
                                params.put("token", token);
                            }
                            if (orderId != null) {
                                params.put("orderId", orderId);
                            }
                            if (datas != null) {
                                if (datas.rows != null && datas.rows.payment != null) {
                                    params.put("price", datas.rows.payment.price);
                                }
                            }
                            execApi(ApiType.OFFLINE_PAY.setMethod(ApiType.RequestMethod.GET), params);
                            break;
                        case 4://Epos
                            if (datas != null && datas.rows != null && datas.rows.payment != null) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("orderInfo", datas);
                                bundle.putString("payPrice", datas.rows.payment.price);
                                IntentUtil.activityForward(activity, EposActivity.class, bundle, false);
                            } else {
                                showToast("订单已支付");
                            }
                            break;
                    }
                }
                MsgCenter.fireNull(MsgID.order_Change, "payType_change");
                break;
            default:
                break;
        }
    }


    /**
     * @param flag true 代表+ false代表-
     */
    public void changePayPrice(boolean flag) {
        if (flag) {
            if (use_calculate + 500 <= duePrice) {
                use_calculate += 500;
            } else {
                use_calculate = duePrice;
            }
        } else {
            if (use_calculate - 500 > MIN_PAY_PRICE) {
                use_calculate -= 500;
            } else {
                use_calculate = MIN_PAY_PRICE;
            }
        }
        payWay_times_price_et.setText(StringUtil.toTwoString("" + use_calculate));
    }


    //获取最小支付金额
    private void getMinPayPrice() {
        RequestParams params1 = new RequestParams();
        if (StringUtil.checkStr(token)) {
            params1.put("token", token);
        }
        params1.put("orderId", orderId);
        execApi(ApiType.GET_MIN_PAY_PRICE, params1);
    }


    //获取白名单
    private void getWriteList() {

        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        if (StringUtil.checkStr(token)) {
            params.addBodyParameter("token", token);
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiType.GET_WRITE_LIST.getOpt(), params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        handler.sendEmptyMessage(404);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {

                        Gson gson = new Gson();
                        ResponseResult responseResult = gson.fromJson(arg0.result, ResponseResult.class);
                        if (responseResult.getStatus().equals("1000")) {
                            handler.sendEmptyMessage(1);
                        }

                    }

                });
    }


    /**
     * 调用支付宝支付
     */
    @SuppressWarnings("unused")
    private void getAliPay(HashMap<String, String> map) {
        new AlipayClass(map, activity);
    }


    /**
     * 启动银联支付界面
     */
    public void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
                tn, mode);
    }

    //确认支付宝支付金额
    public void getAli(boolean isFull) {
        RequestParams params = new RequestParams();
        if (StringUtil.checkStr(token)) {
            params.put("token", token);
        }
        params.put("consumer", "app");
        if (orderId != null) {
            params.put("orderId", orderId);
        }
        if (!isFull) {
            if (StringUtil.checkStr(payWay_times_price_et.getText().toString().trim())) {
                params.put("price", payWay_times_price_et.getText().toString().trim());
            }
        }
        showProgressDialog();
        execApi(ApiType.GET_ALI, params);
    }

    //确认银联支付金额
    public void getUnipay() {

        showProgressDialog();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder builder1 = new FormBody.Builder();

        builder1.add("consumer", "app");
        builder1.add("orderId", orderId);
        if (StringUtil.checkStr("token")) {
            builder1.add("token", token);
        }


        if (payWay_pay_times_view.getVisibility() == View.VISIBLE) { //分次支付金额
            price = payWay_times_price_et.getText().toString().trim();
            if (StringUtil.checkStr(price)) {
                builder1.add("price", price);
                MsgCenter.fireNull(MsgID.PAY_PRICE, price);
            }
        } else {
            if (datas != null && datas.rows != null && datas.rows.payment != null) {
                MsgCenter.fireNull(MsgID.PAY_PRICE, datas.rows.payment.price);
            }
        }

        FormBody formBody = builder1.build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ApiType.GET_UNI.getOpt())
                .post(formBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                if (StringUtil.checkStr(json)) {
                    UnionPayResponse unionPayResponse = gson.fromJson(json, UnionPayResponse.class);
                    if (unionPayResponse != null) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = unionPayResponse;
                        handler.sendMessage(msg);
                    }
                }
            }
        });

    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_ALI) {
            AlipayResult data = (AlipayResult) req.getData();
            if (data != null && data.getStatus().equals("1000")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("price", data.price);
                map.put("title", "新新农人");
                map.put("message", "新新农人");
                map.put("orderNo", data.paymentId);
                map.put("orderId", orderId);
                getAliPay(map);
            }
        } else if (req.getApi() == ApiType.GET_MIN_PAY_PRICE) {
            MinPayPriceResult data = (MinPayPriceResult) req.getData();
            if (data != null && data.getStatus().equals("1000")) {
                MIN_PAY_PRICE = data.payprice;
            } else {
                MIN_PAY_PRICE = 3000;
            }
            use_calculate = MIN_PAY_PRICE;//用于加减计算的金额
            describe_min_pay_price_tv.setText(StringUtil.reduceDouble(MIN_PAY_PRICE));
        } else if (req.getApi() == ApiType.OFFLINE_PAY) {
            if (req.getData().getStatus().equals("1000")) {
                Bundle bundle = new Bundle();
                if (datas != null) {
                    bundle.putString("orderId", orderId);
                    if (datas.rows != null && datas.rows.payment != null) {
                        bundle.putString("payPrice", datas.rows.payment.price);
                        if (datas.rows.RSCInfo != null) {
                            bundle.putString("companyName", datas.rows.RSCInfo.companyName);
                            bundle.putString("RSCPhone", datas.rows.RSCInfo.RSCPhone);
                            bundle.putString("RSCAddress", datas.rows.RSCInfo.RSCAddress);
                        }
                    }
                    IntentUtil.activityForward(activity, OfflinePayActivity.class, bundle, false);
                }
            }
        }
    }
}
