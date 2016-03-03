package com.ksfc.newfarmer.activitys;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.pay.demo.AlipayClass;
import com.google.gson.Gson;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ResponseResult;
import com.ksfc.newfarmer.protocol.ServerInterface;
import com.ksfc.newfarmer.protocol.beans.AlipayResult;
import com.ksfc.newfarmer.protocol.beans.MinPayPriceResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.UnionPayResponse;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.app.App;


public class PaywayActivity extends BaseActivity implements Runnable {
    private ImageView alipay_img, bank_img, dianhui_img, pos_img;
    private int tag;
    private int order_type;//支付类型
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


    private double duePrice;  //待支付金额
    private static final String mMode = "00";// 设置测试模式:01为测试 00为正式环境
    private static final String TN_URL_01 = ApiType.url + "unionpay";// 自己后台需要实现的给予我们app的tn号接口
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            /*
             * 接入指南：1:拷贝sdkStd目录下的UPPayAssistEx.jar到libs目录下
			 * 2:根据需要拷贝sdkStd/jar/data.bin（或sdkPro/jar/data.bin）至工程的assets目录下
			 * 3:根据需要拷贝sdkStd/jar/XXX/XXX.so（或sdkPro/jar/XXX/XXX.so）libs目录下
			 * 4:根据需要拷贝sdkStd
			 * /jar/UPPayPluginExStd.jar（或sdkPro/jar/UPPayPluginExPro
			 * .jar）到工程的libs目录下 5:获取tn后通过UPPayAssistEx.startPayByJar(...)方法调用控件
			 */
            if (msg.obj instanceof UnionPayResponse) {
                UnionPayResponse response = (UnionPayResponse) msg.obj;

                String tn = response.tn;
                if (TextUtils.isEmpty(tn)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            PaywayActivity.this);
                    builder.setTitle("错误提示");
                    builder.setMessage("网络连接失败,请重试!");
                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    doStartUnionPayPlugin(PaywayActivity.this, tn, mMode);
                }
            }
        }
    };


    /**
     * 启动支付界面
     */
    public void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null,
                tn, mode);
    }

    @Override
    public int getLayout() {
        return R.layout.payway_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        App.getApp().exit();
        RndApplication.tempDestroyActivityList.add(PaywayActivity.this);
        setTitle("支付方式");
        initView();
    }


    private void initView() {
        alipay_img = (ImageView) findViewById(R.id.alipay_img);
        bank_img = (ImageView) findViewById(R.id.bank_img);
        dianhui_img = (ImageView) findViewById(R.id.dianhui_img);
        pos_img = (ImageView) findViewById(R.id.pos_img);

        //应付金额 订单号 订单总额 订单状态
        payway_sumPrice_tv = (TextView) findViewById(R.id.payway_sumPrice);
        payWay_totalPrice_tv = (TextView) findViewById(R.id.payWay_totalPrice);
        payWay_order_id_tv = (TextView) findViewById(R.id.payWay_order_id);
        payWay_order_type_tv = (TextView) findViewById(R.id.payWay_order_type);

        //全额支付 分次支付 title bar View
        payWay_pay_total_text_tv = (TextView) findViewById(R.id.payWay_pay_total_text);
        payWay_pay_times_text_tv = (TextView) findViewById(R.id.payWay_pay_times_text);
        payWay_pay_total_bar_tv = (TextView) findViewById(R.id.payWay_pay_total_bar);
        payWay_pay_times_bar_tv = (TextView) findViewById(R.id.payWay_pay_times_bar);
        payWay_pay_total_view = (RelativeLayout) findViewById(R.id.payWay_pay_total_view);
        payWay_pay_times_view = (RelativeLayout) findViewById(R.id.payWay_pay_times_view);
        //全额支付里的订单价钱
        payWay_pay_total_price_tv = (TextView) findViewById(R.id.payWay_pay_total_price);

        //加减按纽 和分次支付编辑框
        payWay_discount_jian = (ImageView) findViewById(R.id.payWay_discount_jian);
        payWay_times_price_et = (EditText) findViewById(R.id.payWay_discount_price);
        payWay_times_price_et.setEnabled(false);
        payWay_discount_jia = (ImageView) findViewById(R.id.payWay_discount_jia);
        describe_min_pay_price_tv = (TextView) findViewById(R.id.describe_min_pay_price);

        orderId = getIntent().getStringExtra("orderId");
        order_type = getIntent().getIntExtra("payType", 1);

        if (StringUtil.checkStr(orderId)) {
            //获取最小支付金额
            getMinPayPrice();
            //获取订单详情
            showProgressDialog();
            getOrderDetail();
            //获取白名单
            getWriteList();
        }


        payWay_order_id_tv.setText(orderId);
        setViewClick(R.id.alipay_ll);
        setViewClick(R.id.bank_ll);
        setViewClick(R.id.pay_sure_tv);
        setViewClick(R.id.payWay_pay_total_rel);
        setViewClick(R.id.payWay_pay_times_rel);
//        setViewClick(R.id.bank_dianhui_ll);
//        setViewClick(R.id.pos_ll);

        init();
        switch (order_type) {
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
//            case 3:
//                // 银行电汇
//                dianhui_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 3;
//                break;
//            case 4:
//                // pos支付
//                pos_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 4;
//                break;
            default:
                // 默认选中支付宝支付
                alipay_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 1;
                break;

        }
    }

    private void getMinPayPrice() {
        //获取最小支付金额
        RequestParams params1 = new RequestParams();
        if (isLogin()) {
            params1.put("userId", Store.User.queryMe().userid);
        }
        params1.put("orderId", orderId);
        execApi(ApiType.GET_MIN_PAY_PRICE, params1);
    }


    private void getOrderDetail() {
        showProgressDialog();
        //获取订单详情
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        params.put("orderId", orderId);
        execApi(ApiType.GET_ORDER_DETAILS, params);
    }

    //获取白名单   单独处理massage用xutils
    private void getWriteList() {
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        if (isLogin()) {
            params.addBodyParameter("token", Store.User.queryMe().token);
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, ApiType.GET_WRITE_LIST.getOpt(), params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {

                        Gson gson = new Gson();
                        ResponseResult responseResult = gson.fromJson(arg0.result, ResponseResult.class);
                        if (responseResult.getStatus().equals("1000")) {
                            //白名单下 可以输入金额 ，并控制 不可以加减。
                            payWay_discount_jian.setEnabled(false);
                            payWay_discount_jia.setEnabled(false);
                            payWay_times_price_et.setEnabled(true);
                            payWay_times_price_et.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {


                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (StringUtil.checkStr(s.toString())) {
                                        double price = Double.parseDouble(s.toString());
                                        if (price > duePrice) {
                                            payWay_times_price_et.setText(StringUtil.toTwoString(duePrice + ""));
                                        } else if (price == 0) {
                                            payWay_times_price_et.setText(StringUtil.toTwoString("0.01"));
                                        }
                                    } else {
                                        payWay_times_price_et.setText("0.01");
                                    }
                                }
                            });

                        }

                    }

                });
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
                break;
            case R.id.payWay_pay_times_rel://分次支付
                payWay_pay_total_view.setVisibility(View.GONE);
                payWay_pay_times_view.setVisibility(View.VISIBLE);
                payWay_pay_total_bar_tv.setVisibility(View.INVISIBLE);
                payWay_pay_times_bar_tv.setVisibility(View.VISIBLE);
                payWay_pay_times_text_tv.setTextColor(getResources().getColor(R.color.orange));
                payWay_pay_total_text_tv.setTextColor(getResources().getColor(R.color.main_index_gary));
                break;
            case R.id.payWay_discount_jia:
                changePayPrice(true);
                break;
            case R.id.payWay_discount_jian:
                changePayPrice(false);
                break;
            case R.id.alipay_ll://支付宝
                init();
                alipay_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 1;
                RequestParams params = new RequestParams();
                if (isLogin()) {
                    params.put("userId", Store.User.queryMe().userid);
                }
                if (orderId != null) {
                    params.put("orderId", orderId);
                }
                params.put("payType", tag);
                execApi(ApiType.GET_UPDATPAYWAY, params);

                break;
            case R.id.bank_ll://银联
                init();
                bank_img.setBackgroundResource(R.drawable.circle_orange);
                tag = 2;
                RequestParams params1 = new RequestParams();
                if (isLogin()) {
                    params1.put("userId", Store.User.queryMe().userid);
                }
                if (orderId != null) {
                    params1.put("orderId", orderId);
                }
                params1.put("payType", tag);
                execApi(ApiType.GET_UPDATPAYWAY, params1);
                break;
//            case R.id.bank_dianhui_ll://银行电汇
//                init();
//                dianhui_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 3;
//                break;
//            case R.id.pos_ll://POS机
//                init();
//                pos_img.setBackgroundResource(R.drawable.circle_green);
//                tag = 4;
//                break;
            case R.id.pay_sure_tv:
                if (payWay_pay_times_view.getVisibility() == View.VISIBLE) {
                    if (tag == 1) {
                        RequestParams params3 = new RequestParams();
                        if (isLogin()) {
                            params3.put("userId", Store.User.queryMe().userid);
                        }
                        params3.put("consumer", "app");
                        if (StringUtil.checkStr(payWay_times_price_et.getText().toString().trim())) {
                            params3.put("price", payWay_times_price_et.getText().toString().trim());
                        }
                        if (orderId != null) {
                            params3.put("orderId", orderId);
                        }
                        execApi(ApiType.GET_ALI, params3);
                    } else if (tag == 2) {
                        new Thread(this).start();
                    } else {
                        showToast("请选择支付方式");
                    }
                } else {
                    if (tag == 1) {
                        RequestParams params3 = new RequestParams();
                        if (isLogin()) {
                            params3.put("userId", Store.User.queryMe().userid);
                        }
                        params3.put("consumer", "app");
                        if (orderId != null) {
                            params3.put("orderId", orderId);
                        }
                        execApi(ApiType.GET_ALI, params3);
                    } else if (tag == 2) {
                        new Thread(this).start();
                    } else {
                        showToast("请选择支付方式");
                    }
                }

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


    /**
     * 调用支付宝支付
     */
    @SuppressWarnings("unused")
    private void getAliPay(HashMap<String, String> map) {
        new AlipayClass(map, this);
    }

    private void init() {
        alipay_img.setBackgroundResource(R.drawable.circle_gray);
        bank_img.setBackgroundResource(R.drawable.circle_gray);
        dianhui_img.setBackgroundResource(R.drawable.circle_gray);
        pos_img.setBackgroundResource(R.drawable.circle_gray);
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (ApiType.GET_UPDATPAYWAY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                RndLog.i(TAG, "更改支付方式成功");
            }
        } else if (ApiType.GET_ORDER_DETAILS == req.getApi()) {
            MyOrderDetailResult data = (MyOrderDetailResult) req.getData();
            if (data.getStatus().equals("1000")) {
                MyOrderDetailResult.Datas datas = data.datas;
                if (datas != null) {
                    if (datas.rows != null) {
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
                                    setViewClick(R.id.payWay_discount_jian);
                                    setViewClick(R.id.payWay_discount_jia);
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

                            if (datas.rows.paySubOrderType.equals("deposit")) {
                                payWay_order_type_tv.setText("阶段一：订金");
                            } else if (datas.rows.paySubOrderType.equals("balance")) {
                                payWay_order_type_tv.setText("阶段二：尾款");
                            } else if (datas.rows.paySubOrderType.equals("full")) {
                                payWay_order_type_tv.setText("订单总额");
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
        } else if (req.getApi() == ApiType.GET_ALI) {
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
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Object response = null;
        InputStream is;
        try {
            String url = TN_URL_01;
            URL myURL = new URL(url);
            URLConnection ucon = myURL.openConnection();
            ((HttpURLConnection) ucon).setRequestMethod("POST");
            RndLog.i(TAG, "URL:" + url + "consumer=app&orderId=" + orderId);
            //****************************************////////////////
            //根据分次支付的布局是否显示来判断此次支付是分次还是全额 如果是分次 参数中传入price
            if (payWay_pay_times_view.getVisibility() == View.VISIBLE) {
                //分次支付金额
                if (StringUtil.checkStr(payWay_times_price_et.getText().toString().trim())) {
                    price = payWay_times_price_et.getText().toString().trim();
                    if (isLogin()) {
                        byte[] b = ("token=" + Store.User.queryMe().token + "&consumer=app&orderId=" + orderId + "&price=" + payWay_times_price_et.getText().toString().trim())
                                .getBytes();
                        ucon.getOutputStream().write(b, 0, b.length);
                    }
                }

            } else {
                //全额金额
                price = payWay_pay_total_price_tv.getText().toString().trim().replace("¥", "");
                if (isLogin()) {
                    byte[] b = ("token=" + Store.User.queryMe().token + "&consumer=app&orderId=" + orderId)
                            .getBytes();
                    ucon.getOutputStream().write(b, 0, b.length);
                }
            }
            //*********************************************////

            ucon.getOutputStream().flush();
            ucon.getOutputStream().close();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            final String json = ServerInterface.toString(is, "UTF-8");
            is.close();
            response = JSON.parseObject(json, UnionPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = response;
        mHandler.sendMessage(msg);
    }

    //银联
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        RndLog.v(TAG, str);
        if (str != null) {
            if (str.equalsIgnoreCase("success")) {

                String result = data.getExtras().getString("result_data");
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
