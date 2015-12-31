/**
 *
 */
package com.alipay.sdk.pay.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


import com.alipay.sdk.app.PayTask;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.OrderSuccessActivity;
import com.ksfc.newfarmer.protocol.ApiType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 项目名称：DriveStudent_student 类名称：AlipayClass 类描述： 创建人：王蕾 创建时间：2015-6-9
 * 上午10:58:27 修改备注：
 */
public class AlipayClass extends FragmentActivity {
    // // 商户PID
    public static final String PARTNER = "2088911973097354";
    // 商户收款账号
    public static final String SELLER = "it@xinxinnongren.com";
    // 商户私钥，pkcs8格式已修改
    public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAP2cuJXfkcLYzf8ldCcppdOelA1P7U3tAxF+avSorHXBWpvmfcgAZtdTM9olsC/bief2/BsPXpbaWaIw3epiFyUsi76OMXhFl6tPUOivZ2NKM+x0PPi0YaJe1K+O9KmXTqr2UIgQ4kmwGrqN/vCmRrXZWg7+BSkFQ/cBCbgHfkZZAgMBAAECgYEAoT258bex0aLL3ZMvdRK6ln/0+z28z1WIJOAuGhz/gOKMvB/gCn+O4wnIJsLdcJ/w3uUdxgqQhfKPGFpfTPxOX5Ug/jDpp3BATpvqpzMVwsuOrP7B9X+KGB2tksksb0HpdbcOMfDWl096BylF4bE3Lq3vsuzrjk9RqgSokcBsV4ECQQD/iU0DWv/N4v6znpqMTmSQWms101vZJFkgoblJ3HU4iPa+6b2joF1Shj6ef0uOr3akQHqatte9eKB0/YTIskKJAkEA/hKGw51vIrGHhy+2/11adZf1MifYpwSwrYLMvpb+oH7ws1AzDt1e2Gn4X47QhFoPWGaWuZaviRrKIZgNasMxUQJBAPlAPrt4Jq33rUMdAFi9GoBngc2l1SBPwRQAS5CNFlXH2w5LRmv1PzIAudG2DsglxE7gifahRHyOzcxvgPaWUikCQQCShdaoS0vDY0R4pwDPJmQ7uuXSBf7A20iU2AEBzQyNPIfNsWuwn+PJxNtTKIaCPXnqDkfQQeF7nTKCyzC5qFXxAkAVh/2rl+kk+dvZYJWK6phERjAu9RB+r9Of+I8cMtpY6LzrrUxs4SpWyZNl8LbY0TBeTeG6PF+GFRmbRcdBczWx";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD9nLiV35HC2M3/JXQnKaXTnpQNT+1N7QMRfmr0qKx1wVqb5n3IAGbXUzPaJbAv24nn9vwbD16W2lmiMN3qYhclLIu+jjF4RZerT1Dor2djSjPsdDz4tGGiXtSvjvSpl06q9lCIEOJJsBq6jf7wpka12VoO/gUpBUP3AQm4B35GWQIDAQAB";
    private String notifyUrl = ApiType.url + "dynamic/alipay/nofity.asp";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    private String orderId;
    private String orderNo;
    private Activity activcty;
    public static String TAG = "AlipayClass";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(activcty, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activcty,
                                OrderSuccessActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("orderNo", orderNo);
                        activcty.startActivity(intent);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(activcty, "支付结果确认中", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            Toast.makeText(activcty, "支付失败", Toast.LENGTH_SHORT)
//                                    .show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(activcty, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 构造方法
     */
    public AlipayClass(HashMap<String, String> map, Activity activcty) {
        super();
        this.activcty = activcty;
        String title = map.get("title");
        String price = map.get("price");
        orderNo = map.get("orderNo");
        orderId = map.get("orderId");
        pay(price, title, orderNo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_main);
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(String price, String title, String orderNo) {

        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }

        // 订单
        String orderInfo = getOrderInfo(title, price, orderNo);
        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activcty);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(activcty);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();
                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String body, String price, String orderNo) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + body + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"3d\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
