package com.ksfc.newfarmer.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.activitys.AddPotentialActivity;
import com.ksfc.newfarmer.activitys.PotentialCustomerDetailActivity;
import com.ksfc.newfarmer.adapter.CommonAdapter;
import com.ksfc.newfarmer.adapter.CommonViewHolder;
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
 * Created by HePeng on 2016/2/1.
 */
public class PotentialCustomer extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {
    private int page = 1;
    private TextView count_left, count_total;
    private PullToRefreshListView listView;
    private PotentialCustomerAdapter adapter;
    private ImageView add_icon;

    private boolean isToast = false;//正常时候不提示没有客户的情况 防止当前fragment隐藏的时候提示


    @Override
    public View InItView() {

        View view = inflater.inflate(R.layout.potential_customer_layout, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.potential_customer_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        boolean isXXNRAgent = false;
        if (Store.User.queryMe() != null) {
            isXXNRAgent = Store.User.queryMe().isXXNRAgent;//是否是新农经纪人
        }

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
        if (Store.User.queryMe() != null) {
            params.put("userId", Store.User.queryMe().userid);
            params.put("page", page);
            execApi(ApiType.GET_POTENTIAL_CUSTOMER_LIST.setMethod(ApiType.RequestMethod.GET)
                    , params);
        }
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
                        adapter = new PotentialCustomerAdapter(getActivity(),data.potentialCustomers);
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

    @Override
    public void OnViewClick(View v) {

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


    class PotentialCustomerAdapter extends CommonAdapter<PotentialListResult.PotentialCustomers>{


        public PotentialCustomerAdapter(Context context, List<PotentialListResult.PotentialCustomers> data) {
            super(context, data, R.layout.item_already_customr_layout);
        }

        @Override
        public void convert(CommonViewHolder holder, final PotentialListResult.PotentialCustomers potentialCustomers) {

            if (potentialCustomers!=null){
                holder.setText(R.id.item_already_customer_name, potentialCustomers.name);

                ImageView sex_icon = (ImageView) holder.getView(R.id.item_already_customer_sex);
                if (potentialCustomers.sex) {
                    sex_icon.setBackgroundResource(R.drawable.girl_icon);
                } else {
                    sex_icon.setBackgroundResource(R.drawable.boy_icon);
                }


                if (potentialCustomers.isRegistered) {
                    holder.getView(R.id.item_already_customer_register).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.item_already_customer_register).setVisibility(View.GONE);
                }
            }

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PotentialCustomerDetailActivity.class);
                    if (StringUtil.checkStr(potentialCustomers._id)) {
                        intent.putExtra("_id", potentialCustomers._id);
                        startActivity(intent);
                    }

                }
            });
        }
    }




}
