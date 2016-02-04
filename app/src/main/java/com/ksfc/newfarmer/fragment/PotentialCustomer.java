package com.ksfc.newfarmer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.AddPotentialActivity;
import com.ksfc.newfarmer.activitys.PotentialCustomerDetailActivity;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.PotentialListResult;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.StringUtil;

import net.yangentao.util.msg.MsgCenter;
import net.yangentao.util.msg.MsgListener;

import java.util.Collection;
import java.util.List;

/**
 * Created by CAI on 2016/2/1.
 */
public class PotentialCustomer extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {
    private int page = 1;
    private TextView count_left, count_total;
    private PullToRefreshListView listView;
    private MyAdapter adapter;
    private ImageView add_icon;

    private boolean isToast = false;//正常时候不提示没有客户的情况 防止当前fragment隐藏的时候提示

    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.potential_customer_layout, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.potential_customer_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        boolean isXXNRAgent = Store.User.queryMe().isXXNRAgent;//是否是新农经纪人
        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);

        count_left = ((TextView) view.findViewById(R.id.count_left));//剩余邀请人数
        count_total = ((TextView) view.findViewById(R.id.count_total));//共邀请人数
        add_icon = ((ImageView) view.findViewById(R.id.add_potential_customer));
        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPotentialActivity.class);
                startActivity(intent);
            }
        });


        MsgCenter.addListener(new MsgListener() {
            @Override
            public void onMsg(Object sender, String msg, Object... args) {
                page = 1;
                getData();
            }
        }, "add_potential_success");

        if (isXXNRAgent) {
            getData();
        }

        return view;
    }

    private void getData() {
        RequestParams params = new RequestParams();
        execApi(ApiType.GET_POTENTIAL_CUSTOMER_LIST.setMethod(ApiType.RequestMethod.GET)
                .setOpt("/api/v2.1/potentialCustomer/query" + "?token=" + Store.User.queryMe().token + "&page=" + page), params);
    }


    @Override
    public void onResponsed(Request req) {
        listView.onRefreshComplete();
        if (req.getApi() == ApiType.GET_POTENTIAL_CUSTOMER_LIST) {
            PotentialListResult data = (PotentialListResult) req.getData();
            if (data.getStatus().equals("1000")) {
                count_left.setText(data.countLeftToday + "");
                count_total.setText(data.count + "");
                if (data.countLeftToday < 1) {
                    add_icon.setEnabled(false);
                    add_icon.setBackgroundResource(R.drawable.circle_gary_bg);
                } else {
                    add_icon.setEnabled(true);
                    add_icon.setBackgroundResource(R.drawable.circle_orange_bg);
                }

                if (data.potentialCustomers != null && !data.potentialCustomers.isEmpty()) {
                    if (adapter == null) {
                        adapter = new MyAdapter(data.potentialCustomers);
                        listView.setAdapter(adapter);
                    } else {
                        if (page == 1) {
                            adapter.clear();
                        }
                        adapter.addAll(data.potentialCustomers);
                    }

                } else {
                    if (page != 1) {
                        page--;
                        showToast("没有更多客户");
                    } else {
                        if (isToast) {
                            showToast("暂无客户");
                        }

                    }
                }
            }

        }

    }

    //上拉，下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        PullToRefreshUtils.setFreshClose(refreshView);
        page = 1;
        isToast = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        getData();
    }

    class MyAdapter extends BaseAdapter {
        private List<PotentialListResult.PotentialCustomers> list;

        public MyAdapter(List<PotentialListResult.PotentialCustomers> list) {
            this.list = list;
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

        public void addAll(Collection<? extends PotentialListResult.PotentialCustomers> collection) {
            list.addAll(collection);
            notifyDataSetChanged();
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_already_customr_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.name.setText(list.get(position).name);
            if (list.get(position).sex) {
                holder.sex_icon.setBackgroundResource(R.drawable.girl_icon);
            } else {
                holder.sex_icon.setBackgroundResource(R.drawable.boy_icon);
            }

            if (list.get(position).isRegistered) {
                holder.Register.setVisibility(View.VISIBLE);
            } else {
                holder.Register.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PotentialCustomerDetailActivity.class);
                    if (StringUtil.checkStr(list.get(position)._id)) {
                        intent.putExtra("_id", list.get(position)._id);
                        startActivity(intent);
                    }

                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView name;
            private TextView Register;
            private ImageView sex_icon;

            ViewHolder(View convertView) {

                this.name = (TextView) convertView.findViewById(R.id.item_already_customer_name);
                this.Register = (TextView) convertView.findViewById(R.id.item_already_customer_register);
                this.sex_icon = (ImageView) convertView.findViewById(R.id.item_already_customer_sex);
            }
        }
    }


}
