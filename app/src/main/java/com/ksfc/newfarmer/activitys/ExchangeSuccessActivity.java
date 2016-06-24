package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.AddGiftOrderResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/21.
 */
public class ExchangeSuccessActivity extends BaseActivity {
    @BindView(R.id.exchange_code)
    TextView exchangeCode;


    @Override
    public int getLayout() {
        return R.layout.exchange_success_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("兑换成功");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            AddGiftOrderResult.GiftOrderBean giftOrder = (AddGiftOrderResult.GiftOrderBean) extras.getSerializable("giftOrder");
            if (giftOrder != null) {
                exchangeCode.setText(StringUtil.checkStr(giftOrder.deliveryCode) ? giftOrder.deliveryCode : "");
            }
        }

        setViewClick(R.id.check_order_tv);
        setViewClick(R.id.back_tall_tv);

    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.check_order_tv:
                //查看兑换记录
                IntentUtil.activityForward(ExchangeSuccessActivity.this, ExchangeRecordActivity.class, null, true);
                break;
            case R.id.back_tall_tv:
                IntentUtil.activityForward(ExchangeSuccessActivity.this, IntegralTallActivity.class, null, true);
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }


}
