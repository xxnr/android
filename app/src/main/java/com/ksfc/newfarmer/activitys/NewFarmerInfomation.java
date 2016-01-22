package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.InformationResult;
import com.ksfc.newfarmer.protocol.beans.InformationResult.DatasEntity.ItemsEntity;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PullToRefreshUtils;
import com.ksfc.newfarmer.utils.ScreenUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewFarmerInfomation extends BaseActivity implements PullToRefreshBase.OnRefreshListener2, AbsListView.OnScrollListener {

    private PullToRefreshListView listView;
    private List<ItemsEntity> items;
    private InformationAdapter adapter;
    private int page = 1;
    private ImageView return_top;

    @Override
    public int getLayout() {
        return R.layout.information_newfarmer_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {

        listView = (PullToRefreshListView) findViewById(R.id.information_listView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);

        //设置刷新的文字
        PullToRefreshUtils.setFreshText(listView);
        return_top = (ImageView) findViewById(R.id.return_top);
        setViewClick(R.id.return_top);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ItemsEntity entity = (ItemsEntity) adapter.getItem(position);
                Intent intent = new Intent(NewFarmerInfomation.this,
                        ArticleActivity.class);
                if (!TextUtils.isEmpty(entity.getUrl())) {
                    intent.putExtra("articleUrl", entity.getUrl());
                    startActivity(intent);
                }

            }
        });
        setTitle("新农资讯");
        hideLeft();
        items = new ArrayList<InformationResult.DatasEntity.ItemsEntity>();
        getData();
    }

    private void getData() {

        HttpUtils http = new HttpUtils();
        final RequestParams params = new RequestParams();
        params.addQueryStringParameter("max", "20");
        params.addQueryStringParameter("page", String.valueOf(page));
        http.send(HttpMethod.GET, ApiType.GET_INFORMATION.getOpt(), params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        listView.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        listView.onRefreshComplete();
                        InformationResult info = null;
                        info = JSON.parseObject(arg0.result,
                                InformationResult.class);
                        if (info.getCode().equals("1000")) {
                            if (info.getDatas().getItems().size() > 0) {
                                List<ItemsEntity> list = info.getDatas()
                                        .getItems();
                                items.addAll(list);
                                if (adapter == null) {
                                    adapter = new InformationAdapter();
                                    listView.setAdapter(adapter);
                                }
                                adapter.notifyDataSetChanged();

                            } else {
                                showToast("没有更多资讯");
                                if (page!=1){
                                    page--;
                                }


                            }
                        }
                    }

                });
    }

    @Override
    public void OnViewClick(View v) {
        if (v.getId() == R.id.return_top) {
            listView.getRefreshableView().setSelection(0);
        }
    }

    @Override
    public void onResponsed(Request req) {

    }

    //上拉，下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        items.clear();
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        getData();
    }

    //监听listView滚动是否出现return_top
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                // 判断滚动到底部
                if (listView.getRefreshableView().getLastVisiblePosition() ==
                        (listView.getRefreshableView().getCount() - 1)) {
                    return_top.setVisibility(View.VISIBLE);
                }
                // 判断滚动到顶部
                if (listView.getRefreshableView().getFirstVisiblePosition() == 0) {
                    return_top.setVisibility(View.GONE);
                }
                break;
        }


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
        if (getScrollY() >= ScreenUtil
                .getScreenHeight(NewFarmerInfomation.this)) {
            return_top.setVisibility(View.VISIBLE);
        }
    }

    //获得lisView的滚动高度
    public int getScrollY() {
        View c = listView.getRefreshableView().getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getRefreshableView().getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }


    class InformationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NewFarmerInfomation.this)
                        .inflate(R.layout.item_information_layout, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (!TextUtils.isEmpty(items.get(position).getImage())) {
                ImageLoader.getInstance().displayImage(items.get(position).getImage(),
                        holder.image_iv);
            }
            holder.title_tv.setText(items.get(position).getTitle());
            //格式化时间
            String time = DateFormatUtils.convertTime(items.get(position).getDatecreated());
            holder.time_tv.setText(time);
            return convertView;
        }

        class ViewHolder {
            private ImageView image_iv;
            private TextView title_tv, time_tv;

            public ViewHolder(View view) {
                image_iv = (ImageView) view
                        .findViewById(R.id.information_image);
                title_tv = (TextView) view.findViewById(R.id.information_title);
                time_tv = (TextView) view.findViewById(R.id.information_time);

            }

        }

    }

}
