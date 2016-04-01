package com.ksfc.newfarmer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.BuildingList;
import com.ksfc.newfarmer.protocol.beans.CityList;
import com.ksfc.newfarmer.protocol.beans.QueueList;
import com.ksfc.newfarmer.protocol.beans.TownList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HePeng on 2015/12/9.
 */
public class SelectAddressActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private TextView textView;
    private LinearLayout title_ll;

    private int TAG = 1;//区分1省2市3县4乡镇

    private AddressAdapter adapter;

    private StringBuffer buffer = new StringBuffer();//省市县的名称
    private String townName;//乡镇的名字

    private String cityId, buildId, queueId;//省市县

    private String buildId1, queueId1, townId1;//市县乡
    private String cityId2, buildId2, queueId2, townId2;//市县乡_id

    private ArrayList<CityList.Data.Rows> cityList;
    private ArrayList<QueueList.Data.Rows> queueList;
    private List<BuildingList.BuildData> bulidList;
    private List<TownList.TownData> townList;

    private int flag = 0;//区分 省市县  ---乡镇


    @Override
    public int getLayout() {
        return R.layout.selector_address_list_layout;

    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        listView = ((ListView) findViewById(R.id.select_address_list));
        textView = ((TextView) findViewById(R.id.select_address_text));
        title_ll = ((LinearLayout) findViewById(R.id.select_address_title_ll));

        Bundle bundle = getIntent().getExtras();
        flag = bundle.getInt("tag");
        queueId1 = bundle.getString("queueid");
        buildId1 = bundle.getString("buildid");

        if (flag == 1) {
            setTitle("选择乡镇地址");
            getTown();
        } else {
            setTitle("选择地址");
            getCity();
        }

        listView.setOnItemClickListener(this);

    }

    //加载省
    private void getCity() {
        RequestParams params = new RequestParams();
        execApi(ApiType.FINDAREALIST, params);
        showProgressDialog();
    }

    //加载市
    private void loadQueue() {
        RequestParams params = new RequestParams();
        params.put("areaId", cityId);
        execApi(ApiType.QUERYBYAREAID, params);
        showProgressDialog();
    }

    //加载县
    private void loadBuild() {
        RequestParams params = new RequestParams();
        execApi(ApiType.QUERYBYBUSINESSID, params);
        params.put("businessId", queueId);
        showProgressDialog();
    }

    //加载乡镇
    private void getTown() {
        RequestParams params = new RequestParams();
        params.put("countyId", buildId1);
        params.put("cityId", queueId1);
        execApi(ApiType.QUERYTOWNID, params);
        showProgressDialog();
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.FINDAREALIST) {
            CityList city = (CityList) req.getData();
            if (city.datas != null) {
                cityList = city.datas.rows;
                adapter = new AddressAdapter(cityList);
                listView.setAdapter(adapter);
            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYBYAREAID) {
            QueueList add = (QueueList) req.getData();
            if (add.datas != null) {
                TAG = 2;
                queueList = add.datas.rows;
                adapter = new AddressAdapter(queueList);
                listView.setAdapter(adapter);
            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYBYBUSINESSID) {

            BuildingList add = (BuildingList) req.getData();
            if (add.datas != null) {
                if (add.datas.rows.size() == 0) {
                    buildId = "";
                    backForResult();
                } else {
                    TAG = 3;
                    bulidList = add.datas.rows;
                    adapter = new AddressAdapter(bulidList);
                    listView.setAdapter(adapter);
                }


            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYTOWNID) {
            TownList add = (TownList) req.getData();
            if (add.datas != null) {
                if (add.datas.rows.size() == 0) {
                    showToast("此地区暂无街道地址");

                } else {
                    TAG = 4;
                    townList = add.datas.rows;
                    adapter = new AddressAdapter(townList);
                    listView.setAdapter(adapter);
                }
            } else {
                showToast("数据加载失败");
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (TAG) {
            case 1:
                CityList.Data.Rows city = (CityList.Data.Rows) adapter.getItem(position);
                cityId = city.id;
                cityId2 = city._id;
                buffer.append(city.name);
                title_ll.setVisibility(View.VISIBLE);
                textView.setText(buffer);
                adapter.clear();
                adapter = null;
                loadQueue();
                break;
            case 2:
                QueueList.Data.Rows Queue = (QueueList.Data.Rows) adapter.getItem(position);
                queueId = Queue.id;
                queueId2 = Queue._id;
                buffer.append(Queue.name);
                textView.setText(buffer);
                adapter.clear();
                adapter = null;
                loadBuild();
                break;
            case 3:
                BuildingList.BuildData build = (BuildingList.BuildData) adapter.getItem(position);
                buildId = build.id;
                buildId2 = build._id;
                buffer.append(build.name);
                backForResult();
                break;
            case 4:
                TownList.TownData town = (TownList.TownData) adapter.getItem(position);
                townId1 = town.id;
                townId2 = town._id;
                townName = town.name;
                backForResult1();
                break;

        }

    }

    //回传值并返回
    public void backForResult() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("city", buffer.toString());
        bundle.putString("cityareaid", cityId);
        bundle.putString("queueid", queueId);
        bundle.putString("buildid", buildId);
        bundle.putString("cityareaid2", cityId2);
        bundle.putString("queueid2", queueId2);
        bundle.putString("buildid2", buildId2);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    //乡镇回传值并返回
    public void backForResult1() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("town", townName);
        bundle.putString("townid", townId1);
        bundle.putString("townid2", townId2);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    //构造特殊 继承BaseAdapter

    class AddressAdapter extends BaseAdapter {
        private List<? extends Object> list;

        public AddressAdapter(List<? extends Object> list) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectAddressActivity.this).inflate(R.layout.city, null);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (TAG == 1) {
                ArrayList<CityList.Data.Rows> list1 = (ArrayList<CityList.Data.Rows>) this.list;
                holder.name.setText(list1.get(position).name);
            } else if (TAG == 2) {
                ArrayList<QueueList.Data.Rows> list1 = (ArrayList<QueueList.Data.Rows>) this.list;
                holder.name.setText(list1.get(position).name);
            } else if (TAG == 3) {
                ArrayList<BuildingList.BuildData> list1 = (ArrayList<BuildingList.BuildData>) this.list;
                holder.name.setText(list1.get(position).name);
            } else if (TAG == 4) {
                ArrayList<TownList.TownData> list1 = (ArrayList<TownList.TownData>) this.list;
                holder.name.setText(list1.get(position).name);
            }

            return convertView;
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }


        class ViewHolder {
            private TextView name;

            ViewHolder(View convertView) {
                name = ((TextView) convertView.findViewById(R.id.cityTextView));
            }

        }

    }


}
