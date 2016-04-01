package com.ksfc.newfarmer.fragment;

import java.util.Collection;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ConsumerOrderActivity;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.InviteeResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InviteFriendsList extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private LinearLayout none_layout;
    private PullToRefreshListView listView;
    private View head_view;
    private TextView count_tv;
    private InviteAdapter adapter;
    private int page = 1;


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public View InItView() {
        View view = inflater
                .inflate(R.layout.fragment_invitefriends_list, null);
        none_layout = (LinearLayout) view.findViewById(R.id.invitefriends_text);
        listView = (PullToRefreshListView) view.findViewById(R.id.invitefriends);
        listView.setOnRefreshListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        head_view = LayoutInflater.from(getActivity()).inflate(
                R.layout.item_invite_list_head, null);
        count_tv = (TextView) head_view
                .findViewById(R.id.item_invite_list_head_count);
        showProgressDialog();
        getData();
        return view;
    }

    public void getData() {
        RequestParams params = new RequestParams();
        LoginResult.UserInfo userInfo = Store.User.queryMe();
        if (userInfo!=null){
            String uid = userInfo.userid;
            params.put("userId", uid);
        }
        params.put("max", 20);
        params.put("page", page);
        execApi(ApiType.GET_INVITEE, params);
    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        listView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_INVITEE) {
            InviteeResult data = (InviteeResult) req.getData();
            if (data.getStatus().equals("1000")) {
                none_layout.setVisibility(View.GONE);
                if (listView.getRefreshableView().getHeaderViewsCount() == 0) {
                    listView.getRefreshableView().addHeaderView(head_view);
                }
                if (data.invitee.size() > 0) {
                    List<InviteeResult.Invitee> list = data.invitee;
                    if (page == 1) {
                        if (adapter == null) {
                            adapter = new InviteAdapter(getActivity(),list);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.clear();
                            adapter.addAll(list);
                        }
                    } else {
                        adapter.addAll(list);
                    }
                    listView.setAdapter(adapter);
                    count_tv.setText(data.invitee.size() + "");

                } else {
                    if (page > 1) {
                        page--;
                        if (adapter != null) {
                            if (adapter.getCount() == 0) {
                                none_layout.setVisibility(View.VISIBLE);
                            }

                        } else {
                            none_layout.setVisibility(View.VISIBLE);

                        }
                        showToast("没有更多客户");
                    } else {
                        none_layout.setVisibility(View.VISIBLE);
                    }

                }
            }

        }
    }

    class InviteAdapter extends CommonAdapter<InviteeResult.Invitee>{



        public InviteAdapter(Context context, List<InviteeResult.Invitee> data) {
            super(context, data, R.layout.item_invite_list);
        }

        @Override
        public void convert(final CommonViewHolder holder, final InviteeResult.Invitee invitee) {

            if (invitee!=null){
                TextView nickname_tv = (TextView) holder.getView(R.id.my_inviter_nickname);
                if (!TextUtils.isEmpty(invitee.name)) {
                    nickname_tv.setText(invitee.name);
                    nickname_tv.setTextColor(Color.WHITE);
                    nickname_tv.setBackgroundResource(R.drawable.login_roateup);
                } else {
                    nickname_tv.setText("该好友未填姓名");
                    nickname_tv.setTextColor(getResources().getColor(R.color.main_index_gary));
                    nickname_tv.setBackgroundResource(R.drawable.gethaoyouweishezhinicheng);
                }

                final TextView dotView = (TextView) holder.getView(R.id.my_inviter_nickname_remind_dot);
                if (invitee.newOrdersNumber != 0) {
                    if (invitee.newOrdersNumber > 0) {
                        dotView.setVisibility(View.VISIBLE);
                    } else {
                        dotView.setVisibility(View.GONE);
                    }
                } else {
                    dotView.setVisibility(View.GONE);
                }
                if (StringUtil.checkStr(invitee.account)) {
                    holder.setText(R.id.my_inviter_phone, invitee.account);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dotView.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity(), ConsumerOrderActivity.class);
                        intent.putExtra("consumer", invitee);
                        startActivity(intent);
                    }
                });


            }
        }
    }




    //加载更多
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page++;
        getData();
    }


}
