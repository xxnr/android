package com.ksfc.newfarmer.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.AddPotentialActivity;
import com.ksfc.newfarmer.activitys.CustomerDetailActivity;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.XUtilsDb.XUtilsDbHelper;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.CustomerIsLatestResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.protocol.beans.PotentialListResult;
import com.ksfc.newfarmer.widget.QuickAlphabeticBar;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import net.yangentao.util.PreferenceUtil;
import net.yangentao.util.app.App;
import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        View view = inflater.inflate(R.layout.potential_customer_layout, null);
        View headerLayout = inflater.inflate(R.layout.potential_customer_header_layout, null);

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
                Intent intent = new Intent(getActivity(), AddPotentialActivity.class);
                startActivity(intent);
            }
        });

        listView.addHeaderView(headerLayout);
        boolean isXXNRAgent = false;
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            isXXNRAgent = userInfo.isXXNRAgent;//是否是新农经纪人
        }


        if (isXXNRAgent) {
            getOfflineList();
            //如果请求是否需要请求列表
            getIsLatest(count);

            //是否大于24小时 如果是就更新
            PreferenceUtil pu = new PreferenceUtil(App.getApp().getApplicationContext(), "config");
            long last_up_date = pu.getLong("customer_up_date", 0l);
            long currentTimeMillis = System.currentTimeMillis();
            if ((currentTimeMillis - last_up_date) > (24 * 60 * 60 * 1000)) {
                //24小时未请求户，请求一次全部
                getIsLatest(0);
            }
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
        DbUtils dbUtils = XUtilsDbHelper.getInstance(App.getApp().getApplicationContext(), App.getApp().getUid());
        try {
            List<PotentialListResult.PotentialCustomersEntity> potentialCustomersEntities =
                    dbUtils.findAll(Selector.from(PotentialListResult.PotentialCustomersEntity.class).orderBy("nameInitialType , namePinyin"));
            if (potentialCustomersEntities != null && !potentialCustomersEntities.isEmpty()) {
                count = potentialCustomersEntities.size();
                adapter = new PotentialCustomerAdapter(App.getApp().getApplicationContext(), potentialCustomersEntities);
                listView.setAdapter(adapter);
                initAlphabeticBar(potentialCustomersEntities);
            }
        } catch (DbException e) {
            e.printStackTrace();
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
                List<PotentialListResult.PotentialCustomersEntity> potentialCustomersEntities = data.potentialCustomers;
                if (potentialCustomersEntities != null && !potentialCustomersEntities.isEmpty()) {
                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(potentialCustomersEntities);
                    } else {
                        adapter = new PotentialCustomerAdapter(getActivity(), potentialCustomersEntities);
                        listView.setAdapter(adapter);
                    }
                    initAlphabeticBar(potentialCustomersEntities);
                    //删除并重新 存入数据库
                    DbUtils dbUtils = XUtilsDbHelper.getInstance(App.getApp().getApplicationContext(), App.getApp().getUid());
                    XUtilsDbHelper.deleteAll(dbUtils, PotentialListResult.PotentialCustomersEntity.class);
                    XUtilsDbHelper.saveOrUpdateAll(dbUtils, potentialCustomersEntities);

                    //记下更新时间
                    PreferenceUtil pu = new PreferenceUtil(App.getApp().getApplicationContext(), "config");
                    pu.putLong("customer_up_date", System.currentTimeMillis());

                }
            }
        }
    }

    @Override
    public void OnViewClick(View v) {

    }

    //初始化滚动条
    public void initAlphabeticBar(List<PotentialListResult.PotentialCustomersEntity> data) {

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
        float mHeight = (ScreenUtil.getScreenHeight(App.getApp().getApplicationContext()) - (Utils.dip2px(App.getApp().getApplicationContext(), 125))) * (float) (alphaIndexer.size() / 27.00);
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


    class PotentialCustomerAdapter extends CommonAdapter<PotentialListResult.PotentialCustomersEntity> {
        private List<PotentialListResult.PotentialCustomersEntity> list;

        public PotentialCustomerAdapter(Context context, List<PotentialListResult.PotentialCustomersEntity> data) {
            super(context, data, R.layout.item_already_customr_layout);
            this.list = data;


        }

        @Override
        public void convert(CommonViewHolder holder, final PotentialListResult.PotentialCustomersEntity potentialCustomers) {

            if (potentialCustomers != null) {

                //姓名 性别 是否注册 点击进入详情
                holder.setText(R.id.item_already_customer_name, potentialCustomers.name);
                TextView alpha = (TextView) holder.getView(R.id.alpha);

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
                        Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
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
