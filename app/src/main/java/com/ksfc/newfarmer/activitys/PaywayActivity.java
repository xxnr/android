package com.ksfc.newfarmer.activitys;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.pay.demo.AlipayClass;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.ServerInterface;
import com.ksfc.newfarmer.protocol.beans.AlipayResult;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.protocol.beans.ProFileResult;
import com.ksfc.newfarmer.protocol.beans.ProFileResult.Datas;
import com.ksfc.newfarmer.protocol.beans.UnionPayResponse;
import com.ksfc.newfarmer.protocol.beans.UnipayResult;
import com.ksfc.newfarmer.protocol.beans.WaitingPay.Orders;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yangentao.util.app.App;

public class PaywayActivity extends BaseActivity implements Runnable {
    private ImageView alipay_img, bank_img, dianhui_img, pos_img;
    private int tag;
    private int order_type;//支付类型
    private String orderId;
    private TextView payway_sumPrice, payWay_order_id, payWay_totalPrice, payWay_order_type;//应付金额 订单号 订单总额 订单状态
    private TextView payWay_pay_total_text, payWay_pay_times_text, payWay_pay_total_bar, payWay_pay_times_bar;//全额支付 分次支付
    private TextView payWay_pay_total_price; //全额支付 应付金额
    private RelativeLayout payWay_pay_total_view, payWay_pay_times_view;//全额支付 分次支付
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


        payway_sumPrice = (TextView) findViewById(R.id.payway_sumPrice);
        payWay_totalPrice = (TextView) findViewById(R.id.payWay_totalPrice);
        payWay_order_id = (TextView) findViewById(R.id.payWay_order_id);
        payWay_order_type = (TextView) findViewById(R.id.payWay_order_type);

        payWay_pay_total_text = (TextView) findViewById(R.id.payWay_pay_total_text);
        payWay_pay_times_text = (TextView) findViewById(R.id.payWay_pay_times_text);
        payWay_pay_total_bar = (TextView) findViewById(R.id.payWay_pay_total_bar);
        payWay_pay_times_bar = (TextView) findViewById(R.id.payWay_pay_times_bar);
        payWay_pay_total_view = (RelativeLayout) findViewById(R.id.payWay_pay_total_view);
        payWay_pay_times_view = (RelativeLayout) findViewById(R.id.payWay_pay_times_view);

        payWay_pay_total_price = (TextView) findViewById(R.id.payWay_pay_total_price);


        orderId = getIntent().getStringExtra("orderId");
        order_type = getIntent().getIntExtra("order_type", 1);

        if (StringUtil.checkStr(orderId)) {
            showProgressDialog();
            RequestParams params = new RequestParams();
            params.put("userId", Store.User.queryMe().userid);
            params.put("orderId", orderId);
            execApi(ApiType.GET_ORDER_DETAILS, params);
        }
        payWay_order_id.setText(orderId);

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
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                break;
            case 2:
                // 银联支付
                bank_img.setBackgroundResource(R.drawable.circle_green);
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
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                break;

        }
    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.payWay_pay_total_rel://全额支付
                payWay_pay_total_view.setVisibility(View.VISIBLE);
                payWay_pay_times_view.setVisibility(View.GONE);
                payWay_pay_total_bar.setVisibility(View.VISIBLE);
                payWay_pay_times_bar.setVisibility(View.INVISIBLE);
                payWay_pay_times_text.setTextColor(getResources().getColor(R.color.main_index_gary));
                payWay_pay_total_text.setTextColor(getResources().getColor(R.color.orange));


                break;
            case R.id.payWay_pay_times_rel://分次支付
                payWay_pay_total_view.setVisibility(View.GONE);
                payWay_pay_times_view.setVisibility(View.VISIBLE);
                payWay_pay_total_bar.setVisibility(View.INVISIBLE);
                payWay_pay_times_bar.setVisibility(View.VISIBLE);
                payWay_pay_times_text.setTextColor(getResources().getColor(R.color.orange));
                payWay_pay_total_text.setTextColor(getResources().getColor(R.color.main_index_gary));

                break;

            case R.id.alipay_ll://支付宝
                init();
                alipay_img.setBackgroundResource(R.drawable.circle_green);
                tag = 1;
                RequestParams params = new RequestParams();
                params.put("userId", Store.User.queryMe().userid);
                if (orderId != null) {
                    params.put("orderId", orderId);
                }
                params.put("payType", tag);
                execApi(ApiType.GET_UPDATPAYWAY, params);

                break;
            case R.id.bank_ll://银联
                init();
                bank_img.setBackgroundResource(R.drawable.circle_green);
                tag = 2;
                RequestParams params1 = new RequestParams();
                params1.put("userId", Store.User.queryMe().userid);
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

                } else {
                    if (tag == 1) {
                        RequestParams params3 = new RequestParams();
                        params3.put("userId", Store.User.queryMe().userid);
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
                            payWay_order_id.setText(datas.rows.id);
                        }
                        //待付金额
                        if (StringUtil.checkStr(datas.rows.duePrice)) {
                            payway_sumPrice.setText(datas.rows.duePrice);
                            payWay_pay_total_price.setText(datas.rows.duePrice);
                        }
                        //订单状态
                        if (StringUtil.checkStr(datas.rows.paySubOrderType)) {

                            if (datas.rows.paySubOrderType.equals("deposit")) {
                                payWay_order_type.setText("阶段一：订金");
                            } else if (datas.rows.paySubOrderType.equals("balance")) {
                                payWay_order_type.setText("阶段二：尾款");
                            } else if (datas.rows.paySubOrderType.equals("full")) {
                                payWay_order_type.setText("全款");
                            }
                        }

                        //订单总额
                        if (datas.rows.order != null) {
                            if (StringUtil.checkStr(datas.rows.order.totalPrice)) {
                                payWay_totalPrice.setText(datas.rows.order.totalPrice);
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
            byte[] b = ("token="+Store.User.queryMe().token+"&consumer=app&responseStyle=v1.0&orderId=" + orderId)
                    .getBytes();
            ucon.getOutputStream().write(b, 0, b.length);
            ucon.getOutputStream().flush();
            ucon.getOutputStream().close();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            final String json = ServerInterface.toString(is, "UTF-8");
            is.close();
            response = JSON.parseObject(json,UnionPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = response;
        mHandler.sendMessage(msg);
    }

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
                msg = "支付成功！";
                Intent intent = new Intent(PaywayActivity.this,
                        OrderSuccessActivity.class);
                intent.putExtra("orderId", orderId);
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
