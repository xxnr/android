package com.ksfc.newfarmer.fragment;

import java.util.Collection;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.ConsumerOrderActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.OnApiDataReceivedCallback;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.InviteeResult;
import com.ksfc.newfarmer.protocol.beans.LoginResult;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class InviteFriendsList extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private LinearLayout none_layout;
    private PullToRefreshListView listView;
    private View head_view;
    private TextView count_tv;
    private InviteAdapter adapter;
    private int page = 1;


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
                            adapter = new InviteAdapter(list);
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


    class InviteAdapter extends BaseAdapter {

        private List<InviteeResult.Invitee> list;

        public InviteAdapter(List<InviteeResult.Invitee> list) {
            this.list = list;
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public void addAll(Collection<? extends InviteeResult.Invitee> collection) {
            list.addAll(collection);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_invite_list, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            if (!TextUtils.isEmpty(list.get(position).name)) {
                holder.nickname_tv.setText(list.get(position).name);
                holder.nickname_tv.setTextColor(Color.WHITE);
                holder.nickname_tv.setBackgroundResource(R.drawable.login_roateup);
            } else {
                holder.nickname_tv.setText("该好友未填姓名");
                holder.nickname_tv.setTextColor(getResources().getColor(R.color.main_index_gary));
                holder.nickname_tv.setBackgroundResource(R.drawable.gethaoyouweishezhinicheng);
            }
            if (list.get(position).newOrdersNumber != 0) {

                if (list.get(position).newOrdersNumber > 0) {
                    holder.remind_dot.setVisibility(View.VISIBLE);
                } else {
                    holder.remind_dot.setVisibility(View.GONE);
                }

            } else {
                holder.remind_dot.setVisibility(View.GONE);
            }
            if (StringUtil.checkStr(list.get(position).account)) {
                holder.phone_tv.setText(list.get(position).account);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.remind_dot.setVisibility(View.GONE);
                    InviteeResult.Invitee consumer = (InviteeResult.Invitee) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), ConsumerOrderActivity.class);
                    intent.putExtra("consumer", consumer);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView phone_tv;
            private TextView nickname_tv;
            private TextView remind_dot;

            ViewHolder(View view) {
                phone_tv = (TextView) view
                        .findViewById(R.id.my_inviter_phone);
                nickname_tv = (TextView) view
                        .findViewById(R.id.my_inviter_nickname);
                remind_dot = (TextView) view
                        .findViewById(R.id.my_inviter_nickname_remind_dot);
            }
        }

    }

}
