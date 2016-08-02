package com.ksfc.newfarmer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.beans.BuildingList;
import com.ksfc.newfarmer.beans.CityList;
import com.ksfc.newfarmer.beans.QueueList;
import com.ksfc.newfarmer.beans.TownList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HePeng on 2015/12/9.
 */
public class SelectAddressActivity extends BaseActivity {
    private ListView listView;
    private TextView textView;
    private LinearLayout title_ll;

    private int TAG = 1;//区分1省2市3县4乡镇


    private StringBuffer buffer = new StringBuffer();//省市县的名称
    private String townName;//乡镇的名字

    private String cityId, buildId, queueId;//省市县

    private String buildId1, queueId1, townId1;//市县乡
    private String cityId2, buildId2, queueId2, townId2;//市县乡_id

    private List<AddressEntity> entities;

    private int flag = 0;//区分 省市县  ---乡镇
    private AddressAdapter adapter;


    @Override
    public int getLayout() {
        return R.layout.activity_selector_address_list;
    }


    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        listView = ((ListView) findViewById(R.id.select_address_list));
        textView = ((TextView) findViewById(R.id.select_address_text));
        title_ll = ((LinearLayout) findViewById(R.id.select_address_title_ll));

        entities = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            flag = bundle.getInt("tag");
            queueId1 = bundle.getString("queueid");
            buildId1 = bundle.getString("buildid");
        }

        if (flag == 1) {
            setTitle("选择乡镇地址");
            getTown(buildId1,queueId1);
        } else {
            setTitle("选择地址");
            getCity();
        }

    }

    //加载省
    private void getCity() {
        execApi(ApiType.FINDAREALIST, null);
    }

    //加载市
    private void loadQueue(String cityId) {
        RequestParams params = new RequestParams();
        params.put("areaId", cityId);
        execApi(ApiType.QUERYBYAREAID, params);
    }

    //加载县
    private void loadBuild(String queueId) {
        RequestParams params = new RequestParams();
        params.put("businessId", queueId);
        execApi(ApiType.QUERYBYBUSINESSID, params);
    }

    //加载乡镇
    private void getTown(String buildId1, String queueId1) {
        RequestParams params = new RequestParams();
        params.put("countyId", buildId1);
        params.put("cityId", queueId1);
        execApi(ApiType.QUERYTOWNID, params);
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
                TAG = 1;
                List<CityList.Data.Rows> cityList = city.datas.rows;
                if (cityList != null) {
                    for (int i = 0; i < cityList.size(); i++) {
                        AddressEntity entity = new AddressEntity();
                        CityList.Data.Rows row = cityList.get(i);
                        if (row != null) {
                            entity._id = row._id;
                            entity.name = row.name;
                            entity.id = row.id;
                        }
                        entities.add(entity);
                    }
                    adapter = new AddressAdapter(SelectAddressActivity.this, entities);
                    listView.setAdapter(adapter);
                }
            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYBYAREAID) {
            QueueList add = (QueueList) req.getData();
            if (add.datas != null) {
                TAG = 2;
                List<QueueList.Data.Rows> queueList = add.datas.rows;
                if (queueList != null) {
                    for (int i = 0; i < queueList.size(); i++) {
                        AddressEntity entity = new AddressEntity();
                        QueueList.Data.Rows row = queueList.get(i);
                        if (row != null) {
                            entity._id = row._id;
                            entity.name = row.name;
                            entity.id = row.id;
                        }
                        entities.add(entity);
                    }
                    adapter = new AddressAdapter(SelectAddressActivity.this, entities);
                    listView.setAdapter(adapter);
                }
            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYBYBUSINESSID) {
            BuildingList add = (BuildingList) req.getData();
            if (add.datas != null && add.datas.rows != null) {
                if (add.datas.rows.size() == 0) {
                    buildId = "";
                    backResult();
                } else {
                    TAG = 3;
                    List<BuildingList.BuildData> bulidList = add.datas.rows;
                    if (bulidList != null) {
                        for (int i = 0; i < bulidList.size(); i++) {
                            AddressEntity entity = new AddressEntity();
                            BuildingList.BuildData row = bulidList.get(i);
                            if (row != null) {
                                entity._id = row._id;
                                entity.name = row.name;
                                entity.id = row.id;
                            }
                            entities.add(entity);
                        }
                        adapter = new AddressAdapter(SelectAddressActivity.this, entities);
                        listView.setAdapter(adapter);
                    }
                }
            } else {
                showToast("数据加载失败");
            }
        } else if (req.getApi() == ApiType.QUERYTOWNID) {
            TownList add = (TownList) req.getData();
            if (add.datas != null && add.datas.rows != null) {
                if (add.datas.rows.size() == 0) {
                    showToast("此地区暂无街道地址");
                } else {
                    TAG = 4;
                    List<TownList.TownData> townList = add.datas.rows;
                    if (townList != null) {
                        for (int i = 0; i < townList.size(); i++) {
                            AddressEntity entity = new AddressEntity();
                            TownList.TownData row = townList.get(i);
                            if (row != null) {
                                entity._id = row._id;
                                entity.name = row.name;
                                entity.id = row.id;
                            }
                            entities.add(entity);
                        }
                        adapter = new AddressAdapter(SelectAddressActivity.this, entities);
                        listView.setAdapter(adapter);
                    }
                }
            } else {
                showToast("数据加载失败");
            }
        }
    }


    public void onItemClick(AddressEntity entity) {
        if (adapter != null) {
            adapter.clear();
            showProgressDialog();
        }
        switch (TAG) {
            case 1:
                cityId = entity.id;
                cityId2 = entity._id;
                buffer.append(entity.name);
                title_ll.setVisibility(View.VISIBLE);
                textView.setText(buffer);
                loadQueue(cityId);
                break;
            case 2:

                queueId = entity.id;
                queueId2 = entity._id;
                buffer.append(entity.name);
                textView.setText(buffer);

                loadBuild(queueId);

                break;
            case 3:
                buildId = entity.id;
                buildId2 = entity._id;
                buffer.append(entity.name);
                backResult();
                break;
            case 4:
                townId1 = entity.id;
                townId2 = entity._id;
                townName = entity.name;
                backResultTown();
                break;
        }

    }


    //回传值并返回
    public void backResult() {

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
    public void backResultTown() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("town", townName);
        bundle.putString("townid", townId1);
        bundle.putString("townid2", townId2);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    class AddressAdapter extends CommonAdapter<AddressEntity> {

        public AddressAdapter(Context context, List<AddressEntity> data) {
            super(context, data, R.layout.item_only_text);
        }

        @Override
        public void convert(CommonViewHolder holder, final AddressEntity addressEntity) {
            if (addressEntity != null) {
                holder.setText(R.id.cityTextView, addressEntity.name);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(addressEntity);
                    }
                });
            }
        }
    }

    class AddressEntity {

        public String name;
        public String id;
        public String _id;
    }


}
