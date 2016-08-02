package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.dbbeans.InviteeEntity;
import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.BaseViewUtils;
import com.ksfc.newfarmer.widget.ClearEditText;
import com.ksfc.newfarmer.widget.UnSwipeListView;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import greendao.DaoSession;
import greendao.InviteeEntityDao;
import greendao.PotentialCustomersEntityDao;

/**
 * Created by HePeng on 2016/3/28.
 */
public class InviterSearchActivity extends BaseActivity {


    private ClearEditText inviter_search_edit;
    private UnSwipeListView invitee_search_customer_listView, invitee_search_potential_listView;
    private RelativeLayout null_customer_layout;
    private List<PotentialCustomersEntity> potentialCustomersEntities;
    private List<InviteeEntity> inviteeEntityList;

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
        final TextView inviter_search_text = (TextView) findViewById(R.id.inviter_search_text);
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
                    BaseViewUtils.hideSoftInput(InviterSearchActivity.this, inviter_search_edit);
                    String searchText = inviter_search_edit.getText().toString().trim();
                    if (StringUtil.checkStr(searchText)) {
                        //请求数据库数据
                        invitee_search_customer_separatrix.setVisibility(View.GONE);


                        DaoSession readableDaoSession = DBManager.getInstance(InviterSearchActivity.this).getReadableDaoSession();
                        if (isMobileNum(searchText)) {
                            PotentialCustomersEntityDao potentialCustomersEntityDao = readableDaoSession.getPotentialCustomersEntityDao();
                            potentialCustomersEntities = potentialCustomersEntityDao
                                    .queryBuilder()
                                    .where(PotentialCustomersEntityDao.Properties.Phone.eq(searchText))
                                    .list();
                            InviteeEntityDao inviteeEntityDao = readableDaoSession.getInviteeEntityDao();
                            inviteeEntityList = inviteeEntityDao
                                    .queryBuilder()
                                    .where(InviteeEntityDao.Properties.Account.eq(searchText))
                                    .list();

                        } else {

                            PotentialCustomersEntityDao potentialCustomersEntityDao = readableDaoSession.getPotentialCustomersEntityDao();
                            potentialCustomersEntities = potentialCustomersEntityDao
                                    .queryBuilder()
                                    .where(PotentialCustomersEntityDao.Properties.Name.like("%" + searchText + "%"))
                                    .list();
                            InviteeEntityDao inviteeEntityDao = readableDaoSession.getInviteeEntityDao();
                            inviteeEntityList = inviteeEntityDao
                                    .queryBuilder()
                                    .where(InviteeEntityDao.Properties.Name.like("%" + searchText + "%"))
                                    .list();

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
                BaseViewUtils.showSoftInput(InviterSearchActivity.this, inviter_search_edit);
            }
        }, 300); //设置300毫秒的时
    }


    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            case R.id.inviter_search_text:
                BaseViewUtils.hideSoftInput(InviterSearchActivity.this, v);
                finish();
                break;
        }
    }

    @Override
    public void onResponsed(Request req) {

    }


    //我的客户列表适配器
    class InviteAdapter extends CommonAdapter<InviteeEntity> {

        public InviteAdapter(Context context, List<InviteeEntity> data) {
            super(context, data, R.layout.item_invite_list);
        }

        @Override
        public void convert(final CommonViewHolder holder, final InviteeEntity invitee) {

            if (invitee != null) {
                //分组 性别 姓名 是否更新订单 手机号
                holder.getView(R.id.alpha).setVisibility(View.GONE);
                ImageView sex_iv = holder.getView(R.id.item_invite_sex);

                if (invitee.sex) {
                    sex_iv.setBackgroundResource(R.drawable.girl_icon);
                } else {
                    sex_iv.setBackgroundResource(R.drawable.boy_icon);
                }

                TextView nickname_tv = holder.getView(R.id.my_inviter_nickname);
                if (StringUtil.checkStr(invitee.name)) {
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
                        startActivity(ConsumerOrderActivity.getCallingIntent(InviterSearchActivity.this,invitee));
                    }
                });


            }
        }
    }


    //客户登记列表适配器
    class PotentialCustomerAdapter extends CommonAdapter<PotentialCustomersEntity> {
        private List<PotentialCustomersEntity> list;

        public PotentialCustomerAdapter(Context context, List<PotentialCustomersEntity> data) {
            super(context, data, R.layout.item_already_customer);
        }

        @Override
        public void convert(CommonViewHolder holder, final PotentialCustomersEntity potentialCustomers) {

            if (potentialCustomers != null) {

                //姓名 性别 是否注册 点击进入详情
                holder.getView(R.id.alpha).setVisibility(View.GONE);

                holder.setText(R.id.item_already_customer_name, potentialCustomers.name);

                ImageView sex_icon = holder.getView(R.id.item_already_customer_sex);
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
                        if (StringUtil.checkStr(potentialCustomers._id)) {
                            Intent intent =   CustomerDetailActivity.getCallingIntent(InviterSearchActivity.this,potentialCustomers._id);
                            startActivity(intent);
                        }
                    }
                });

            }


        }
    }


}
