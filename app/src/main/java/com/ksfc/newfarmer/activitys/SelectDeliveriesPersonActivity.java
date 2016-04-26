package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.ConsigneeResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.utils.StringUtil;


import java.util.List;


/**
 * Created by HePeng on 2016/3/9.
 */
public class SelectDeliveriesPersonActivity extends BaseActivity {
    private EditText receipt_phone;
    private EditText receipt_name;

    private String consigneeName;
    private String consigneePhone;

    private final static int ResultPerson = 0x11;
    private TextView name_submit_tv;
    private ListView history_person_listView;
    private LinearLayout history_receive_person_ll;
    private View headView1;
    private View headView2;

    @Override
    public int getLayout() {
        return R.layout.select_deliveries_person_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("选择收货人");
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            consigneeName = bundle.getString("consigneeName");
            consigneePhone = bundle.getString("consigneePhone");
        }
        showProgressDialog();
        getData();


    }

    private void getData() {
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("userId", Store.User.queryMe().userid);
        }
        execApi(ApiType.GET_CONSIGNEE_INFO.setMethod(ApiType.RequestMethod.GET), params);
    }

    private void initView() {

        headView1 = getLayoutInflater().inflate(R.layout.select_deliveries_person_layout_head1, null);
        headView2 = getLayoutInflater().inflate(R.layout.select_deliveries_person_layout_head2, null);

        name_submit_tv = (TextView) headView1.findViewById(R.id.name_submit_tv);
        receipt_phone = (EditText) headView1.findViewById(R.id.shouhuo_tel);
        receipt_name = (EditText) headView1.findViewById(R.id.shouhuo_name);
        history_receive_person_ll = (LinearLayout) findViewById(R.id.history_receive_person_ll);
        history_person_listView = (ListView) findViewById(R.id.history_person_listView);
        history_person_listView.addHeaderView(headView1);
        history_person_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem>=1){
                        history_receive_person_ll.setVisibility(View.VISIBLE);
                    }else {
                        history_receive_person_ll.setVisibility(View.GONE);
                    }
            }
        });


        name_submit_tv.setEnabled(false);

        receipt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0 && receipt_phone.getText().toString().trim().length() > 0) {
                    name_submit_tv.setEnabled(true);
                } else {
                    name_submit_tv.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        receipt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0 && receipt_name.getText().toString().trim().length() > 0) {
                    name_submit_tv.setEnabled(true);
                } else {
                    name_submit_tv.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setViewClick(R.id.name_submit_tv);
    }

    @Override
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.name_submit_tv:
                if (StringUtil.empty(receipt_name.getEditableText().toString()
                        .trim())) {
                    showToast("请输入收货人姓名");
                    return;
                }
                if (TextUtils.isEmpty(receipt_phone.getText().toString().trim())) {
                    showToast("请输入手机号码");
                    return;
                }
                if (!isMobileNum(receipt_phone.getText().toString().trim())) {
                    showToast("请输入正确的手机号码");
                    return;
                }
                //保存用户
                Intent intent = new Intent();
                intent.putExtra("receipt_name", receipt_name.getText().toString().trim());
                intent.putExtra("receipt_phone", receipt_phone.getText().toString().trim());
                setResult(ResultPerson, intent);
                //只需执行，不管返回是否成功
                RequestParams params = new RequestParams();
                if (isLogin()) {
                    params.put("userId", Store.User.queryMe().userid);
                }
                params.put("consigneeName", receipt_name.getText().toString().trim());
                params.put("consigneePhone", receipt_phone.getText().toString().trim());
                execApi(ApiType.SAVE_CONSIGNEE_INFO, params);

                finish();
                break;
        }
    }


    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_CONSIGNEE_INFO) {

            if (req.getData().getStatus().equals("1000")) {
                ConsigneeResult data = (ConsigneeResult) req.getData();
                List<ConsigneeResult.DatasEntity.RowsEntity> rows = data.datas.rows;
                if (rows != null && !rows.isEmpty()) {
                    ConsigneesAdapter consigneesAdapter = new ConsigneesAdapter(this, rows);
                    history_person_listView.addHeaderView(headView2);
                    history_person_listView.setAdapter(consigneesAdapter);
                } else {
                    history_person_listView.setAdapter(null);
                    //没有历史联系人时的默认设置
                    LoginResult.UserInfo userInfo = Store.User.queryMe();
                    if (userInfo != null) {
                        receipt_name.setText(userInfo.name);
                        receipt_phone.setText(userInfo.phone);
                    }
                    history_receive_person_ll.setVisibility(View.GONE);
                    // 光标移到最后
                    Editable eText = receipt_name.getText();
                    Selection.setSelection(eText, eText.length());
                }
            }
        }
    }


    class ConsigneesAdapter extends CommonAdapter<ConsigneeResult.DatasEntity.RowsEntity> {


        public ConsigneesAdapter(Context context, List<ConsigneeResult.DatasEntity.RowsEntity> data) {
            super(context, data, R.layout.item_consignees_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final ConsigneeResult.DatasEntity.RowsEntity rowsEntity) {

            if (rowsEntity != null) {

                holder.setText(R.id.name_consignees_item, rowsEntity.consigneeName);
                holder.setText(R.id.phone_consignees_item, rowsEntity.consigneePhone);

                CheckBox checkBox = (CheckBox) holder.getView(R.id.btn_consignees_item);
                if (consigneeName != null && consigneeName.equals(rowsEntity.consigneeName) &&
                        consigneePhone != null && consigneePhone.equals(rowsEntity.consigneePhone)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //保存用户
                        Intent intent = new Intent();
                        intent.putExtra("receipt_name", rowsEntity.consigneeName);
                        intent.putExtra("receipt_phone", rowsEntity.consigneePhone);
                        setResult(ResultPerson, intent);
                        finish();
                    }
                });

            }

        }
    }
}
