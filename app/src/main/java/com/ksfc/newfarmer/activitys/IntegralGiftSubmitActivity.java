package com.ksfc.newfarmer.activitys;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CompleteReceiver;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.RemoteApi;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.AddGiftOrderResult;
import com.ksfc.newfarmer.http.beans.ConsigneeResult;
import com.ksfc.newfarmer.http.beans.GiftDetailResult;
import com.ksfc.newfarmer.http.beans.RSCStateInfoResult;
import com.ksfc.newfarmer.utils.IntentUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CAI on 2016/6/16.
 */
public class IntegralGiftSubmitActivity extends BaseActivity {
    @BindView(R.id.select_state_address_info)
    TextView selectStateAddressInfo;
    @BindView(R.id.select_state_address_ll_state)
    LinearLayout selectStateAddressLlState;
    @BindView(R.id.select_state_person_info)
    TextView selectStatePersonInfo;
    @BindView(R.id.select_state_address_ll_person)
    LinearLayout selectStateAddressLlPerson;
    @BindView(R.id.gift_submit_ll)
    LinearLayout giftSubmitLl;
    @BindView(R.id.gift_submit_empty_ll)
    LinearLayout giftSubmitEmptyLl;
    @BindView(R.id.gift_img_iv)
    ImageView giftImgIv;
    @BindView(R.id.gift_name_tv)
    TextView giftNameTv;
    @BindView(R.id.gift_price_tv)
    TextView giftPriceTv;
    @BindView(R.id.gift_submit_sure_tv)
    TextView gift_submit_sure_tv;
    @BindView(R.id.up_grade)
    TextView up_grade;


    //网点和收货人的请求码
    private final static int requestState = 1;
    private final static int requestPerson = 2;
    private String consigneeName;
    private String consigneePhone;
    private RSCStateInfoResult.RSCsEntity rsCsEntity;
    private GiftDetailResult.GiftBean gift;
    private CompleteReceiver completeReceiver;


    @Override
    public int getLayout() {
        return R.layout.activity_gift_submit;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setTitle("提交兑换");
        giftSubmitEmptyLl.setVisibility(View.GONE);
        giftSubmitLl.setVisibility(View.VISIBLE);
        InitData();
        //获取联系人
        RemoteApi.getConsignees(this);

        //在主页判断版本是否需要升级 并注册监听下载完成之后的广播
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 设置数据
     */
    private void InitData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gift = (GiftDetailResult.GiftBean) extras.getSerializable("gift");
            if (gift != null) {
                //判断是否包含自提的配送方式
                boolean isSupport = false;
                if (gift.category != null
                        && gift.category.deliveries != null
                        && !gift.category.deliveries.isEmpty()) {
                    for (int i = 0; i < gift.category.deliveries.size(); i++) {
                        GiftDetailResult.GiftBean.CategoryBean.DeliveriesBean deliveriesBean = gift.category.deliveries.get(i);
                        if (deliveriesBean != null) {
                            switch (deliveriesBean.deliveryType) {
                                case 1:
                                    isSupport = true;
                                    break;
                            }
                        }
                    }
                }
                if (!isSupport) {
                    setViewClick(R.id.up_grade);
                    giftSubmitEmptyLl.setVisibility(View.VISIBLE);
                    giftSubmitLl.setVisibility(View.GONE);
                } else {
                    giftSubmitEmptyLl.setVisibility(View.GONE);
                    giftSubmitLl.setVisibility(View.VISIBLE);
                    if (StringUtil.checkStr(gift.thumbnail)) {
                        ImageLoader.getInstance().displayImage(MsgID.IP + gift.thumbnail, giftImgIv);
                    }
                    giftNameTv.setText(StringUtil.checkStr(gift.name) ? gift.name : "");
                    giftPriceTv.setText(String.valueOf(gift.points));

                    //确定兑换
                    gift_submit_sure_tv.setOnClickListener(this);
                    setViewClick(R.id.select_state_address_ll_state);
                    setViewClick(R.id.select_state_address_ll_person);
                }
            }
        }
    }


    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.gift_submit_sure_tv:
                if (rsCsEntity == null) {
                    showToast("请选择自提网点");
                    return;
                }
                if (!StringUtil.checkStr(selectStatePersonInfo.getText().toString().trim())) {
                    showToast("请选择联系人");
                    return;
                }
                showProgressDialog();
                RemoteApi.addGiftOrder(this, 1, rsCsEntity._id, consigneeName, consigneePhone, gift.id);

                break;
            case R.id.select_state_address_ll_state://选择自提网点
                if (gift != null && StringUtil.checkStr(gift._id)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("flags", 1);
                    bundle.putString("gift_Id", gift._id);
                    bundle.putString("requestTag", "gift");
                    IntentUtil.startActivityForResult(this, SelectDeliveriesStateActivity.class,
                            requestState, bundle);
                }
                break;
            case R.id.select_state_address_ll_person://选择收货人
                Bundle bundle1 = new Bundle();
                bundle1.putString("consigneeName", consigneeName);
                bundle1.putString("consigneePhone", consigneePhone);
                IntentUtil.startActivityForResult(this, SelectDeliveriesPersonActivity.class,
                        requestPerson, bundle1);
                break;
            case R.id.up_grade://选择收货人
                //app 是否需要升级
                RemoteApi.appIsNeedUpdate(this);
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_CONSIGNEE_INFO) {
            ConsigneeResult data = (ConsigneeResult) req.getData();
            List<ConsigneeResult.DatasEntity.RowsEntity> rows = data.datas.rows;
            if (rows != null && !rows.isEmpty()) {
                ConsigneeResult.DatasEntity.RowsEntity rowsEntity = rows.get(0);
                if (rowsEntity != null) {
                    consigneeName = rowsEntity.consigneeName;
                    consigneePhone = rowsEntity.consigneePhone;
                    StringBuilder person_info = new StringBuilder().append(consigneeName).append(" ").append(consigneePhone);
                    selectStatePersonInfo.setText(person_info);
                }
            }
        } else if (ApiType.ADD_GIFT_ORDER == req.getApi()) {
            AddGiftOrderResult reqData = (AddGiftOrderResult) req.getData();
            Bundle bundle = new Bundle();
            bundle.putSerializable("giftOrder", reqData.giftOrder);
            IntentUtil.activityForward(IntegralGiftSubmitActivity.this, ExchangeSuccessActivity.class, bundle, true);
        }
    }

    //接收选择后的网点和收货人
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestPerson:
                if (resultCode == 0x11)//返回码
                {
                    consigneeName = data.getStringExtra("receipt_name");
                    consigneePhone = data.getStringExtra("receipt_phone");
                    StringBuilder person_info = new StringBuilder().append(consigneeName).append(" ").append(consigneePhone);
                    selectStatePersonInfo.setText(person_info);
                }
                break;
            case requestState:

                if (resultCode == 0x12)//返回码
                {
                    //网点所在地址
                    rsCsEntity = (RSCStateInfoResult.RSCsEntity) data.getSerializableExtra("rsCsEntity");
                    if (rsCsEntity != null) {
                        String province = "";
                        String city = "";
                        String county = "";
                        String town = "";
                        RSCStateInfoResult.RSCsEntity.RSCInfoEntity.CompanyAddressEntity companyAddress = rsCsEntity.RSCInfo.companyAddress;
                        if (companyAddress != null && companyAddress.province != null) {
                            province = companyAddress.province.name;
                        }
                        if (companyAddress != null && companyAddress.city != null) {
                            city = companyAddress.city.name;
                        }
                        if (companyAddress != null && companyAddress.county != null) {
                            county = companyAddress.county.name;
                        }
                        if (companyAddress != null && companyAddress.town != null) {
                            town = companyAddress.town.name;
                        }
                        String address = StringUtil.checkBufferStr
                                (province, city, county, town);
                        StringBuilder builder = new StringBuilder();
                        builder.append(address);
                        if (companyAddress != null && StringUtil.checkStr(companyAddress.details)) {
                            builder.append(companyAddress.details);
                        }
                        selectStateAddressInfo.setText(builder.toString());
                    }
                }


                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除对下载完成事件的监听
        unregisterReceiver(completeReceiver);
    }


}