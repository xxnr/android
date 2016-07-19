package com.ksfc.newfarmer.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.AddPotentialActivity;
import com.ksfc.newfarmer.activitys.CustomerDetailActivity;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.DBManager;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.CustomerIsLatestResult;
import com.ksfc.newfarmer.beans.LoginResult;
import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;
import com.ksfc.newfarmer.beans.dbbeans.PotentialListResult;
import com.ksfc.newfarmer.widget.QuickAlphabeticBar;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import net.yangentao.util.PreferenceUtil;


import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import greendao.PotentialCustomersEntityDao;

/**
 * Created by HePeng on 2016/2/1.
 */
public class PotentialCustomer extends BaseFragment {
    private TextView count_left, count_total;
    private ListView listView;
    private QuickAlphabeticBar alphabeticBar;// 快速索引条
    private TextView fast_position;
    private PotentialCustomerAdapter adapter;
    private int count = 0;//是否需要更新数据


    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.fragment_potential_customer, null);
        View headerLayout = inflater.inflate(R.layout.head_potential_customer, null);

        listView = (ListView) view.findViewById(R.id.potential_customer_listView);
        alphabeticBar = (QuickAlphabeticBar) view.findViewById(R.id.fast_scroller);
        fast_position = (TextView) view.findViewById(R.id.fast_position);

        count_left = ((TextView) headerLayout.findViewById(R.id.count_left));//剩余邀请人数
        count_total = ((TextView) headerLayout.findViewById(R.id.count_total));//共邀请人数
        //添加潜在客户
        ImageView add_icon = ((ImageView) headerLayout.findViewById(R.id.add_potential_customer));
        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddPotentialActivity.class);
                startActivity(intent);
            }
        });

        listView.addHeaderView(headerLayout);
        listView.setAdapter(null);

        getOfflineList();
        //如果请求是否需要请求列表
        getIsLatest(count);

        //是否大于24小时 如果是就更新
        PreferenceUtil pu = new PreferenceUtil(activity, "config");
        long last_up_date = pu.getLong("customer_up_date", 0L);
        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - last_up_date) > (24 * 60 * 60 * 1000)) {
            //24小时未请求户，请求一次全部
            getIsLatest(0);
        }

        //添加了用户数据
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                //每添加用户，请求一次全部
                getIsLatest(0);
            }
        }, MsgID.add_potential_success);

        //更新了用户数据
        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                getOfflineList();
            }
        }, MsgID.change_potential_success);
        return view;
    }

    //获取客户信息
    private void getIsLatest(int count) {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
            params.put("count", count);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_ISLATEST.setMethod(ApiType.RequestMethod.GET)
                    , params);
        }
    }

    //从数据库中适配
    private void getOfflineList() {
        //从数据库中适配
        PotentialCustomersEntityDao potentialCustomersEntityDao = DBManager
                .getInstance(activity).getReadableDaoSession()
                .getPotentialCustomersEntityDao();

            List<PotentialCustomersEntity> potentialCustomersEntities =
                    potentialCustomersEntityDao
                            .queryBuilder()
                            .orderAsc(PotentialCustomersEntityDao.Properties.NameInitialType)
                            .orderAsc(PotentialCustomersEntityDao.Properties.NamePinyin)
                            .list();
            if (potentialCustomersEntities != null && !potentialCustomersEntities.isEmpty()) {
                count = potentialCustomersEntities.size();
                adapter = new PotentialCustomerAdapter(activity, potentialCustomersEntities);
                listView.setAdapter(adapter);
                initAlphabeticBar(potentialCustomersEntities);
            }

    }

    //获取全部的客户列表

    private void getAllInfo() {

        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            params.put("userId", userInfo.userid);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_LIST_NEW.setMethod(ApiType.RequestMethod.GET)
                    , params);
        }

    }


    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_ISLATEST) {
            if (req.getData().getStatus().equals("1000")) {
                CustomerIsLatestResult data = (CustomerIsLatestResult) req.getData();
                count_left.setText(String.valueOf(data.countLeftToday));
                count_total.setText(String.valueOf(data.count));
                if (data.needUpdate == 1) {
                    getAllInfo();
                }
            }
        } else if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_LIST_NEW) {
            if (req.getData().getStatus().equals("1000")) {
                PotentialListResult data = (PotentialListResult) req.getData();
                List<PotentialCustomersEntity> potentialCustomersEntities = data.potentialCustomers;
                if (potentialCustomersEntities != null && !potentialCustomersEntities.isEmpty()) {
                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(potentialCustomersEntities);
                    } else {
                        adapter = new PotentialCustomerAdapter(activity, potentialCustomersEntities);
                        listView.setAdapter(adapter);
                    }
                    initAlphabeticBar(potentialCustomersEntities);
                    //删除并重新 存入数据库
                    PotentialCustomersEntityDao potentialCustomersEntityDao = DBManager.getInstance(activity)
                            .getWritableDaoSession()
                            .getPotentialCustomersEntityDao();
                    potentialCustomersEntityDao.deleteAll();
                    potentialCustomersEntityDao.insertInTx(potentialCustomersEntities);
                    //记下更新时间
                    PreferenceUtil pu = new PreferenceUtil(activity, "config");
                    pu.putLong("customer_up_date", System.currentTimeMillis());

                }
            }
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    //初始化滚动条
    public void initAlphabeticBar(List<PotentialCustomersEntity> data) {

        HashMap<String, Integer> alphaIndexer = new HashMap<>();
        ArrayList<String> alphas = new ArrayList<>();
        //此处做一下字母排序
        for (int i = 0; i < data.size(); i++) {
            // 得到字母
            if (!alphaIndexer.containsKey(data.get(i).nameInitial)) {
                alphaIndexer.put(data.get(i).nameInitial, i);
                alphas.add(data.get(i).nameInitial);
            }
        }
        float mHeight = (ScreenUtil.getScreenHeight(activity) - (Utils.dip2px(activity, 125))) * (float) (alphaIndexer.size() / 27.00);
        ViewGroup.LayoutParams layoutParams = alphabeticBar.getLayoutParams();
        layoutParams.height = (int) mHeight;
        alphabeticBar.setAlphaIndexer(alphaIndexer);
        alphabeticBar.setFastScrollLetter(alphas);
        alphabeticBar.setLayoutParams(layoutParams);
        alphabeticBar.setHight(mHeight);
        alphabeticBar.init(fast_position);
        alphabeticBar.setListViewAndPotentailList(data, listView);
        alphabeticBar.setVisibility(View.VISIBLE);
    }


    class PotentialCustomerAdapter extends CommonAdapter<PotentialCustomersEntity> {
        private List<PotentialCustomersEntity> list;

        public PotentialCustomerAdapter(Context context, List<PotentialCustomersEntity> data) {
            super(context, data, R.layout.item_already_customer);
            this.list = data;


        }

        @Override
        public void convert(CommonViewHolder holder, final PotentialCustomersEntity potentialCustomers) {

            if (potentialCustomers != null) {

                //姓名 性别 是否注册 点击进入详情
                holder.setText(R.id.item_already_customer_name, potentialCustomers.name);
                TextView alpha = holder.getView(R.id.alpha);

                // 当前字母
                String currentStr = potentialCustomers.nameInitial;
                // 前面的字母
                String previewStr = (holder.getPosition() - 1) >= 0 ? list.get(holder.getPosition() - 1).nameInitial : " ";

                if (!previewStr.equals(currentStr)) {
                    alpha.setVisibility(View.VISIBLE);
                    alpha.setText(currentStr);
                } else {
                    alpha.setVisibility(View.GONE);
                }

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
                        Intent intent = new Intent(activity, CustomerDetailActivity.class);
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
