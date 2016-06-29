package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.XUtilsDb.XUtilsDbHelper;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.beans.InviteeResult;
import com.ksfc.newfarmer.http.beans.PotentialListResult;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.ClearEditText;
import com.ksfc.newfarmer.widget.UnSwipeListView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import com.ksfc.newfarmer.App;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HePeng on 2016/3/28.
 */
public class InviterSearchActivity extends BaseActivity {


    private ClearEditText inviter_search_edit;
    private UnSwipeListView invitee_search_customer_listView, invitee_search_potential_listView;
    private RelativeLayout null_customer_layout;
    private List<PotentialListResult.PotentialCustomersEntity> potentialCustomersEntities;
    private List<InviteeResult.InviteeEntity> inviteeEntityList;

    @Override
    public int getLayout() {
        return R.layout.activity_inviter_seach;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        inviter_search_edit = (ClearEditText) findViewById(R.id.inviter_search_edit);
        TextView inviter_search_text = (TextView) findViewById(R.id.inviter_search_text);
        invitee_search_customer_listView = (UnSwipeListView) findViewById(R.id.invitee_search_customer_listView);
        invitee_search_potential_listView = (UnSwipeListView) findViewById(R.id.invitee_search_potential_listView);
        final LinearLayout invitee_search_customer_separatrix = (LinearLayout) findViewById(R.id.invitee_search_customer_separatrix);
        null_customer_layout = (RelativeLayout) findViewById(R.id.null_customer_layout);
        null_customer_layout.setVisibility(View.GONE);


        inviter_search_text.setText("取消");
        inviter_search_text.setOnClickListener(this);
        inviter_search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    String searchText = inviter_search_edit.getText().toString().trim();
                    if (StringUtil.checkStr(searchText)) {
                        //请求数据库数据
                        invitee_search_customer_separatrix.setVisibility(View.GONE);

                        DbUtils dbUtils = XUtilsDbHelper.getInstance(InviterSearchActivity.this, App.getApp().getUid());
                        if (isMobileNum(searchText)) {
                            try {
                                potentialCustomersEntities = dbUtils.findAll(Selector.from(PotentialListResult.PotentialCustomersEntity.class).where("phone", "=", searchText));
                                inviteeEntityList = dbUtils.findAll(Selector.from(InviteeResult.InviteeEntity.class).where("account", "=", searchText));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                potentialCustomersEntities = dbUtils.findAll(Selector.from(PotentialListResult.PotentialCustomersEntity.class).where("name", "like", "%" + searchText + "%"));
                                inviteeEntityList = dbUtils.findAll(Selector.from(InviteeResult.InviteeEntity.class).where("name", "like", "%" + searchText + "%"));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        if (potentialCustomersEntities != null && !potentialCustomersEntities.isEmpty()) {
                            invitee_search_potential_listView.setVisibility(View.VISIBLE);
                            View headView = getLayoutInflater().inflate(R.layout.item_tab_bar, null);
                            TextView tab_text = (TextView) headView.findViewById(R.id.common_tab_bar_text);
                            tab_text.setText("客户登记");
                            if (invitee_search_potential_listView.getHeaderViewsCount() == 0) {
                                invitee_search_potential_listView.addHeaderView(headView);
                            }
                            invitee_search_potential_listView.setAdapter(new PotentialCustomerAdapter(InviterSearchActivity.this, potentialCustomersEntities));
                        } else {
                            invitee_search_potential_listView.setVisibility(View.GONE);
                        }

                        if (inviteeEntityList != null && !inviteeEntityList.isEmpty()) {
                            invitee_search_customer_listView.setVisibility(View.VISIBLE);
                            View headView = getLayoutInflater().inflate(R.layout.item_tab_bar, null);
                            TextView tab_text = (TextView) headView.findViewById(R.id.common_tab_bar_text);
                            tab_text.setText("我的客户");
                            if (invitee_search_customer_listView.getHeaderViewsCount() == 0) {
                                invitee_search_customer_listView.addHeaderView(headView);
                            }
                            invitee_search_customer_listView.setAdapter(new InviteAdapter(InviterSearchActivity.this, inviteeEntityList));

                            if (invitee_search_potential_listView.getVisibility() == View.VISIBLE) {
                                invitee_search_customer_separatrix.setVisibility(View.VISIBLE);
                            }

                        } else {
                            invitee_search_customer_listView.setVisibility(View.GONE);
                        }
                        //没有结果
                        if (invitee_search_customer_listView.getVisibility() == View.GONE && invitee_search_potential_listView.getVisibility() == View.GONE) {
                            null_customer_layout.setVisibility(View.VISIBLE);
                        } else {
                            null_customer_layout.setVisibility(View.GONE);
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        //软件盘自动弹出
        inviter_search_edit.requestFocus(); //edittext是一个EditText控件
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inviter_search_edit, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300); //设置300毫秒的时
    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.inviter_search_text:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                finish();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }


    //我的客户列表适配器
    class InviteAdapter extends CommonAdapter<InviteeResult.InviteeEntity> {

        public InviteAdapter(Context context, List<InviteeResult.InviteeEntity> data) {
            super(context, data, R.layout.item_invite_list);
        }

        @Override
        public void convert(final CommonViewHolder holder, final InviteeResult.InviteeEntity invitee) {

            if (invitee != null) {
                //分组 性别 姓名 是否更新订单 手机号
                holder.getView(R.id.alpha).setVisibility(View.GONE);
                ImageView sex_iv = (ImageView) holder.getView(R.id.item_invite_sex);

                if (invitee.sex) {
                    sex_iv.setBackgroundResource(R.drawable.girl_icon);
                } else {
                    sex_iv.setBackgroundResource(R.drawable.boy_icon);
                }

                TextView nickname_tv = (TextView) holder.getView(R.id.my_inviter_nickname);
                if (!TextUtils.isEmpty(invitee.name)) {
                    nickname_tv.setText(invitee.name);
                } else {
                    nickname_tv.setText("好友未填姓名");
                }

                holder.getView(R.id.my_inviter_nickname_remind_dot).setVisibility(View.INVISIBLE);

                if (StringUtil.checkStr(invitee.account)) {
                    holder.setText(R.id.my_inviter_phone, invitee.account);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InviterSearchActivity.this, ConsumerOrderActivity.class);
                        intent.putExtra("consumer", invitee);
                        startActivity(intent);
                    }
                });


            }
        }
    }


    //客户登记列表适配器
    class PotentialCustomerAdapter extends CommonAdapter<PotentialListResult.PotentialCustomersEntity> {
        private List<PotentialListResult.PotentialCustomersEntity> list;

        public PotentialCustomerAdapter(Context context, List<PotentialListResult.PotentialCustomersEntity> data) {
            super(context, data, R.layout.item_already_customer);
        }

        @Override
        public void convert(CommonViewHolder holder, final PotentialListResult.PotentialCustomersEntity potentialCustomers) {

            if (potentialCustomers != null) {

                //姓名 性别 是否注册 点击进入详情
                holder.getView(R.id.alpha).setVisibility(View.GONE);

                holder.setText(R.id.item_already_customer_name, potentialCustomers.name);

                ImageView sex_icon = (ImageView) holder.getView(R.id.item_already_customer_sex);
                if (potentialCustomers.sex) {
                    sex_icon.setBackgroundResource(R.drawable.girl_icon);
                } else {
                    sex_icon.setBackgroundResource(R.drawable.boy_icon);
                }


                if (potentialCustomers.isRegistered) {
                    holder.getView(R.id.item_already_customer_register).setVisibility(View.VISIBLE);
                    holder.getView(R.id.item_already_customer_register_icon).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.item_already_customer_register).setVisibility(View.GONE);
                    holder.getView(R.id.item_already_customer_register_icon).setVisibility(View.GONE);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InviterSearchActivity.this, CustomerDetailActivity.class);
                        if (StringUtil.checkStr(potentialCustomers._id)) {
                            intent.putExtra("_id", potentialCustomers._id);
                            startActivity(intent);
                        }
                    }
                });

            }


        }
    }


}
