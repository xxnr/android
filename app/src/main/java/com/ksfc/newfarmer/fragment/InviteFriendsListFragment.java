package com.ksfc.newfarmer.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ksfc.newfarmer.BaseFragment;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ConsumerOrderActivity;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.XUtilsDb.XUtilsDbHelper;
import com.ksfc.newfarmer.http.ApiType;
import com.ksfc.newfarmer.http.Request;
import com.ksfc.newfarmer.http.RequestParams;
import com.ksfc.newfarmer.http.beans.InviteeResult;
import com.ksfc.newfarmer.http.beans.LoginResult;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.utils.Utils;
import com.ksfc.newfarmer.widget.QuickAlphabeticBar;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.App;


/**
 * 我的客户
 */

public class InviteFriendsListFragment extends BaseFragment {


    private TextView count_tv;
    private ListView listView;
    private QuickAlphabeticBar alphabeticBar;
    private TextView fast_position;
    private InviteAdapter adapter;
    private LinearLayout none_invitee_customer_ll;


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View view = inflater
                .inflate(R.layout.fragment_potential_customer, null);
        View headerLayout = inflater.inflate(R.layout.item_invite_list_head, null);

        none_invitee_customer_ll = (LinearLayout) view.findViewById(R.id.none_invitee_customer_ll);

        listView = (ListView) view.findViewById(R.id.potential_customer_listView);
        alphabeticBar = (QuickAlphabeticBar) view.findViewById(R.id.fast_scroller);
        fast_position = (TextView) view.findViewById(R.id.fast_position);
        count_tv = (TextView) headerLayout.findViewById(R.id.item_invite_list_head_count);
        listView.addHeaderView(headerLayout);

        //从数据库中取数据
        DbUtils dbUtils = XUtilsDbHelper.getInstance(activity, App.getApp().getUid());
        try {
            List<InviteeResult.InviteeEntity> inviteeEntityList = dbUtils.findAll(Selector.from(InviteeResult.InviteeEntity.class));
            if (inviteeEntityList != null && !inviteeEntityList.isEmpty()) {
                adapter = new InviteAdapter(activity, inviteeEntityList);
                listView.setAdapter(adapter);
                initAlphabeticBar(inviteeEntityList);

                none_invitee_customer_ll.setVisibility(View.GONE);

                //好友数量
                if (StringUtil.checkStr(inviteeEntityList.size() + "")) {
                    count_tv.setText(inviteeEntityList.size() + "");
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        getData();
        return view;
    }

    public void getData() {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo != null) {
            String uid = userInfo.userid;
            params.put("userId", uid);
            execApi(ApiType.GET_INVITEE_ORDER_BY_NAME.setMethod(ApiType.RequestMethod.GET), params);
        }
    }

    @Override
    public void onResponsed(Request req) {

        if (req.getApi() == ApiType.GET_INVITEE_ORDER_BY_NAME) {

            if (req.getData().getStatus().equals("1000")) {

                InviteeResult data = (InviteeResult) req.getData();
                //好友数量
                if (StringUtil.checkStr(data.total + "")) {
                    count_tv.setText(data.total + "");
                }
                if (data.invitee != null && !data.invitee.isEmpty()) {

                    none_invitee_customer_ll.setVisibility(View.GONE);

                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(data.invitee);
                    } else {
                        adapter = new InviteAdapter(activity, data.invitee);
                        listView.setAdapter(adapter);
                    }
                    initAlphabeticBar(data.invitee);
                    //删除并重新 存入数据库
                    DbUtils dbUtils = XUtilsDbHelper.getInstance(activity, App.getApp().getUid());
                    XUtilsDbHelper.deleteAll(dbUtils, InviteeResult.InviteeEntity.class);
                    XUtilsDbHelper.saveOrUpdateAll(dbUtils, data.invitee);

                } else {
                    none_invitee_customer_ll.setVisibility(View.VISIBLE);
                }

            }

        }


    }

    //初始化滚动条
    public void initAlphabeticBar(List<InviteeResult.InviteeEntity> data) {

        HashMap<String, Integer> alphaIndexer = new HashMap<>();
        ArrayList<String> alphas = new ArrayList<>();
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
        alphabeticBar.setListViewAndCustomerList(data, listView);
        alphabeticBar.setVisibility(View.VISIBLE);
    }

    //适配器
    class InviteAdapter extends CommonAdapter<InviteeResult.InviteeEntity> {
        private List<InviteeResult.InviteeEntity> list;

        public InviteAdapter(Context context, List<InviteeResult.InviteeEntity> data) {
            super(context, data, R.layout.item_invite_list);
            this.list = data;
        }

        @Override
        public void convert(final CommonViewHolder holder, final InviteeResult.InviteeEntity invitee) {

            if (invitee != null) {
                //分组 性别 姓名 是否更新订单 手机号
                TextView alpha = (TextView) holder.getView(R.id.alpha);

                // 当前字母
                String currentStr = invitee.nameInitial;
                // 前面的字母
                String previewStr = (holder.getPosition() - 1) >= 0 ? list.get(holder.getPosition() - 1).nameInitial : " ";

                if (!previewStr.equals(currentStr)) {
                    alpha.setVisibility(View.VISIBLE);
                    alpha.setText(currentStr);
                } else {
                    alpha.setVisibility(View.GONE);
                }

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
                final TextView dotView = (TextView) holder.getView(R.id.my_inviter_nickname_remind_dot);

                if (invitee.newOrdersNumber > 0) {
                    dotView.setVisibility(View.VISIBLE);
                } else {
                    dotView.setVisibility(View.INVISIBLE);
                }

                if (StringUtil.checkStr(invitee.account)) {
                    holder.setText(R.id.my_inviter_phone, invitee.account);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dotView.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(activity, ConsumerOrderActivity.class);
                        intent.putExtra("consumer", invitee);
                        startActivity(intent);
                    }
                });


            }
        }
    }


}
