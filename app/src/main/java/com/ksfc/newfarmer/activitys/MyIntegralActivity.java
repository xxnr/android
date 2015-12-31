/**
 * 
 */
package com.ksfc.newfarmer.activitys;

import java.util.ArrayList;
import java.util.List;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.JifenData;
import com.ksfc.newfarmer.protocol.beans.JifenData.Jifen;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.PullToRefreshView;
import com.ksfc.newfarmer.widget.PullToRefreshView.OnFooterRefreshListener;
import com.ksfc.newfarmer.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 项目名称：newFarmer 类名称：myintegralActivity 类描述： 创建人：王蕾 创建时间：2015-5-28 下午4:16:57
 * 修改备注：
 */
public class MyIntegralActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private ListView myintegral_lv;
	private TextView myintegral_count;
	private PullToRefreshView myintergral_pull;
	int page = 1;
	private List<Jifen> jifenList;
	List<Jifen> list;
	private MyIntegralAdapter1 jifenAdapter;
	private List<Jifen> rows;

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.myintegral_layout;
	}

	@Override
	public void OnActCreate(Bundle savedInstanceState) {
		setTitle("我的积分");
		setLeftClickListener(this);
		initView();
		getData();
	}

	/**
	 * 
	 */
	private void getData() {
		// app/point/findPointList
		// locationUserId:客户ID
		// page:每页记录的开始位置
		// rows:每页记录的数目长度
		// userId:用户ID
		showProgressDialog();
		RequestParams params = new RequestParams();
		params.put("locationUserId", Store.User.queryMe().userid);
		params.put("page", page);
		params.put("rows", 10);
		params.put("userId", Store.User.queryMe().userid);
		execApi(ApiType.MY_JIFEN, params);
	}

	private void initView() {
		list = new ArrayList<Jifen>();
		myintergral_pull = (PullToRefreshView) findViewById(R.id.myintergral_pull);
		myintegral_lv = (ListView) findViewById(R.id.myintegral_lv);
		myintegral_count = (TextView) findViewById(R.id.myintegral_count);
	}

	@Override
	public void OnViewClick(View v) {
		finish();
	}

	@Override
	public void onResponsed(Request req) {
		disMissDialog();
		if (req.getApi() == ApiType.MY_JIFEN) {
			JifenData res = (JifenData) req.getData();
			rows = res.datas.rows;
			int sumJifen = res.datas.pointLaterTrade;
			myintegral_count.setText(sumJifen + "");

			if (rows.size() > 0) {
				if (page == 1) {
					list.clear();
					list.addAll(rows);
					jifenAdapter = new MyIntegralAdapter1();
					// 确保onrespond里拿到数据并经过相应的判断后 再进行数据处理
					myintegral_lv.setAdapter(jifenAdapter);
					myintergral_pull.setOnFooterRefreshListener(this);
					myintergral_pull.setOnHeaderRefreshListener(this);
				} else {
					list.addAll(rows);
					jifenAdapter.notifyDataSetChanged();
				}
			} else {
				// showToast("您没有积分了");
			}
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// 这里进行加载更多的控制
		page++;
		getData();
		view.onFooterRefreshComplete();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		page = 1;
		getData();
		view.onHeaderRefreshComplete();
	}

	public class MyIntegralAdapter1 extends BaseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		// 添加数据
		public void setData(ArrayList<Object> Objectlist) {

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder vh = null;
			if (convertView == null) {
				vh = new viewHolder();
				convertView = LayoutInflater.from(MyIntegralActivity.this)
						.inflate(R.layout.myintegral_item, null);
				vh.text1 = (TextView) convertView
						.findViewById(R.id.mybalence_item1);
				vh.text4 = (TextView) convertView
						.findViewById(R.id.mybalence_item4);
				vh.way_get_tv = (TextView) convertView
						.findViewById(R.id.way_get_tv);
				convertView.setTag(vh);
			} else {
				vh = (viewHolder) convertView.getTag();
			}
			vh.text1.setText(StringUtil.getDateToString(list.get(position).createTime));
			if (list.get(position).pointAction.equals("1")) {
				vh.text4.setText(list.get(position).pointNum + "");
			} else {
				vh.way_get_tv.setText("消费了");
				vh.text4.setText(list.get(position).pointNum + "");

			}
			return convertView;
		}

		class viewHolder {
			TextView text1;
			TextView text2;
			TextView text3;
			TextView text4;
			TextView way_get_tv;
		}

	}

}
