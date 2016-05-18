package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaums.mpos.service.IUmsMposResultListener;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.beans.EposPayResult;
import com.ksfc.newfarmer.utils.thrid.EposServiceManager;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;

import net.yangentao.util.app.App;


/**
 * Created by HePeng on 2016/3/21.
 */
public class EposActivity extends BaseActivity {
    private TextView pay_sure_tv;
    private LinearLayout install_prompt;

    private final static String billsMID = "898410159987746";
    private final static String billsTID = "80083007";
    private final static String merOrderDesc = "河南新农人网络科技有限公司";

    private MyOrderDetailResult.Datas orderInfo;
    private String payPrice;

    private String consumerPhone;

    private String memo;

    private Handler handler = new Handler();

    @Override
    public int getLayout() {
        return R.layout.epos_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        setTitle("全民付EPOS");
        RndApplication.tempDestroyActivityList.add(EposActivity.this);
        initView();
        unBindEPOS();//解决某些情况中途卸载EPOS插件绑定不了
        bindEPOS();
    }


    private void initView() {

        TextView pay_price = (TextView) findViewById(R.id.pay_price);
        TextView RSC_companyName = (TextView) findViewById(R.id.RSC_companyName);
        TextView RSC_Address = (TextView) findViewById(R.id.RSC_Address);
        TextView RSC_phone = (TextView) findViewById(R.id.RSC_phone);
        pay_sure_tv = (TextView) findViewById(R.id.pay_sure_tv);
        LinearLayout state_info_ll = (LinearLayout) findViewById(R.id.state_info_ll);
        install_prompt = (LinearLayout) findViewById(R.id.install_prompt);

        RelativeLayout none_state_info_rel = (RelativeLayout) findViewById(R.id.none_state_info_rel);
        state_info_ll.setVisibility(View.GONE);
        none_state_info_rel.setVisibility(View.GONE);
        setViewClick(R.id.pay_sure_tv);
        setViewClick(R.id.view_other_state_ll);

        //是否安装插件
        if (!Utils.isPkgInstalled(this, "com.chinaums.mposplugin")) {
            pay_sure_tv.setText("立即支付");
            install_prompt.setVisibility(View.INVISIBLE);
        } else {
            pay_sure_tv.setText("安装插件");
            install_prompt.setVisibility(View.VISIBLE);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderInfo = (MyOrderDetailResult.Datas) bundle.getSerializable("orderInfo");
            payPrice = bundle.getString("payPrice");
        }

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
        if (StringUtil.checkStr(payPrice)) {
            pay_price.setText("¥" + payPrice);
        } else {
            pay_price.setText("");
        }

    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.pay_sure_tv:
                //是否安装插件
                if (Utils.isPkgInstalled(this, "com.chinaums.mposplugin")) {
                    eposPay();
                } else {
                    Utils.addApk(this, "mpospluginphone.apk");
                }
                break;
            case R.id.view_other_state_ll:

                startActivity(EposSlotCardStateActivity.class);
                break;

        }

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.EPOS_PAY) {
            if (req.getData().getStatus().equals("1000")) {
                EposPayResult payResult = (EposPayResult) req.getData();
                if (orderInfo != null && orderInfo.rows != null) {
                    if (StringUtil.checkStr(orderInfo.rows.id)) {
                        if (payResult != null) {
                            //支付
                            memo = "商户订单号=" + orderInfo.rows.id +
                                    "&商户支付号=" + payResult.paymentId +
                                    "&支付金额=" + payResult.price + "元";
                            consumerPhone = orderInfo.rows.recipientPhone;
                            bookOrderAndPay(payResult.paymentId, payResult.price);
                        }
                    }
                }
            }
        }
    }

    //通知后台Epos支付金额
    public void eposPay() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        if (orderInfo != null && orderInfo.rows != null) {
            params.put("orderId", orderInfo.rows.id);
        }

        if (StringUtil.checkStr(payPrice)) {
            params.put("price", payPrice);
        }
        execApi(ApiType.EPOS_PAY.setMethod(ApiType.RequestMethod.GET), params);
    }


    //支付和消费
    public void bookOrderAndPay(String merOrderId, String payPrice) {
        final Bundle args = new Bundle();
        args.putString("billsMID", billsMID);
        args.putString("billsTID", billsTID);
        args.putString("merOrderDesc", merOrderDesc);
        args.putString("amount", StringUtil.unitToCent(payPrice));
        args.putString("merOrderId", StringUtil.addZeroForNum(merOrderId, 11));
        args.putString("salesSlipType", "1");
        args.putBoolean("isShowOrderInfo", true);
        args.putString("consumerPhone", consumerPhone);
        args.putString("memo", memo);
        try {
            if (EposServiceManager.getInstance().mUmsMposService == null) {
                bindEPOS();   //此次等待一秒后 确保成功后执行
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EposServiceManager.getInstance().mUmsMposService.pay(args, new PayOrderResultListener());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
                return;
            }

            EposServiceManager.getInstance().mUmsMposService.pay(args, new PayOrderResultListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 定义订单支付回调
    class PayOrderResultListener extends IUmsMposResultListener.Stub {
        @Override
        public void umsServiceResult(final Bundle result) throws RemoteException {
            runOnUiThread(new Runnable() {
                public void run() {
                    String payStatus = result.getString("payStatus");
                    String signatureStatus = result.getString("signatureStatus");
                    String orderId = result.getString("orderId");

                    RndLog.d("PayOrderResultListener", "支付回调" + Utils.printBundle(result));

                    if ("success".equals(payStatus)) {
                        if ("success".equals(payStatus)
                                && !"success".equals(signatureStatus)) {
                            showToast("支付成功，但请重新签名");
                            if (StringUtil.checkStr(orderId)) {
                                SignOrder(orderId);
                            }
                        } else if ("success".equals(payStatus)) {
                            showToast("支付成功");
                            orderSuccess();
                        }
                    } else {
                        String resultInfo = result.getString("resultInfo");
                        if (resultInfo != null && resultInfo.contains("银行卡密码输入错误")) {
                            showToast(resultInfo);
                        } else {
                            showToast("支付失败");
                        }

                    }
                }
            });
        }
    }

    //支付成功，跳转到支付成功页
    public void orderSuccess() {
        if (orderInfo != null && orderInfo.rows != null) {
            Intent intent = new Intent(EposActivity.this,
                    OrderSuccessActivity.class);
            intent.putExtra("orderId", orderInfo.rows.id);
            intent.putExtra("price", payPrice);
            startActivity(intent);
        }
    }


    //补签签购单

    public void SignOrder(String orderId) {
        Bundle args = new Bundle();
        args.putString("billsMID", billsMID);
        args.putString("billsTID", billsTID);
        args.putString("orderId", orderId);
        args.putString("salesSlipType", "1");
        try {
            EposServiceManager.getInstance().mUmsMposService.showTransactionInfoAndSign(args, new SignOrderResultListener());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    // 定义补签签购单回调
    class SignOrderResultListener extends IUmsMposResultListener.Stub {
        @Override
        public void umsServiceResult(final Bundle result) throws RemoteException {
            runOnUiThread(new Runnable() {
                public void run() {
                    RndLog.d("PayOrderResultListener", "补签签购单回调" + Utils.printBundle(result));
                    String signatureStatus = result.getString("signatureStatus");
                    if ("success".equals(signatureStatus)) {
                        showToast("签名成功");
                    } else {
                        showToast("签名失败");
                    }
                    App.getApp().partQuit();
                    //不管签名失败与否 都去订单支付成功页面
                    orderSuccess();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //是否安装插件
        boolean installed = Utils.isPkgInstalled(this, "com.chinaums.mposplugin");
        if (installed) {
            pay_sure_tv.setText("立即支付");
            install_prompt.setVisibility(View.INVISIBLE);
        } else {
            pay_sure_tv.setText("安装插件");
            install_prompt.setVisibility(View.VISIBLE);
        }
    }


    private void bindEPOS() {
        try {
            EposServiceManager.getInstance().bindMpospService(EposActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unBindEPOS() {
        try {
            EposServiceManager.getInstance().unbindMposService(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindEPOS();
    }
}
