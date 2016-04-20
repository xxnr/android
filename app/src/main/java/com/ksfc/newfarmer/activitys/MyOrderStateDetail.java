package com.ksfc.newfarmer.activitys;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.beans.MyOrderDetailResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;

/**
 * Created by CAI on 2016/4/19.
 */
public class MyOrderStateDetail extends BaseActivity {
    private LinearLayout view_container;
    private MyOrderDetailResult.Datas datas;


    @Override
    public int getLayout() {
        return R.layout.order_state_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        setTitle("订单状态");
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            datas = ((MyOrderDetailResult.Datas) bundle.getSerializable("order_state"));
        }
        setData();
    }

    private void initView() {

        view_container = ((LinearLayout) findViewById(R.id.view_container));

    }


    //适配数据
    private void setData() {

        if (datas != null && datas.rows != null && datas.rows.order != null && datas.rows.order.orderStatus != null) {

            //当前订单状态 和需要展示的状态
            //0:交易关闭 1:待付款 2:部分付款 3:待发货 4:配送中 5:待自提 6:已完成 7:付款待审核
            MyOrderDetailResult.Rows.Order order = datas.rows.order;

            switch (datas.rows.order.orderStatus.type) {

                case 0:
                    ViewGroup rootView0_0 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder0_0 = new ViewHolder(rootView0_0);
                    setCommonAttribute(holder0_0, true);
                    holder0_0.order_state_time.setVisibility(View.GONE);
                    holder0_0.order_state_icon.setImageResource(R.drawable.order_state_close);
                    holder0_0.order_state_description.setText("尊敬的客户，您的订单已关闭，如有问题可拨打客服电话联系我们");
                    holder0_0.order_state_title.setText("订单已关闭");
                    view_container.addView(rootView0_0);


                    ViewGroup rootView0_1 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder0_1 = new ViewHolder(rootView0_1);
                    setCommonAttribute(holder0_1, false);
                    holder0_1.dividing_line.setVisibility(View.GONE);
                    holder0_1.order_state_bottom_divide.setVisibility(View.GONE);
                    holder0_1.order_state_icon.setImageResource(R.drawable.order_state_submit);
                    holder0_1.order_state_title.setText("订单已提交");
                    holder0_1.order_state_time.setText(DateFormatUtils.convertTime(order.dateCreated));
                    view_container.addView(rootView0_1);
                    break;
                case 1:
                case 2:
                case 7:
                    ViewGroup rootView2_0 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder2_0 = new ViewHolder(rootView2_0);
                    setCommonAttribute(holder2_0, true);
                    holder2_0.dividing_line.setVisibility(View.INVISIBLE);
                    holder2_0.order_state_bottom_divide.setVisibility(View.INVISIBLE);
                    holder2_0.order_state_top_divide.setVisibility(View.INVISIBLE);
                    holder2_0.order_state_icon.setImageResource(R.drawable.order_state_submit);
                    holder2_0.order_state_title.setText("订单已提交");
                    holder2_0.order_state_description.setText("尊敬的客户，您的订单还未支付完成，请尽快完成付款");
                    holder2_0.order_state_time.setText(DateFormatUtils.convertTime(order.dateCreated));
                    view_container.addView(rootView2_0);
                    break;
                case 3:
                    ViewGroup rootView3_0 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder3_0 = new ViewHolder(rootView3_0);
                    setCommonAttribute(holder3_0, true);
                    holder3_0.order_state_icon.setImageResource(R.drawable.order_state_payment);
                    holder3_0.order_state_title.setText("支付成功");
                    holder3_0.order_state_description.setText("尊敬的客户，请耐心等待卖家发货，如有问题可拨打客服电话联系我们");
                    holder3_0.order_state_time.setText(DateFormatUtils.convertTime(order.datePaid));
                    view_container.addView(rootView3_0);

                    ViewGroup rootView3_1 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder3_1 = new ViewHolder(rootView3_1);
                    setCommonAttribute(holder3_1, false);
                    holder3_1.dividing_line.setVisibility(View.INVISIBLE);
                    holder3_1.order_state_bottom_divide.setVisibility(View.INVISIBLE);
                    holder3_1.order_state_icon.setImageResource(R.drawable.order_state_submit);
                    holder3_1.order_state_title.setText("订单已提交");
                    holder3_1.order_state_time.setText(DateFormatUtils.convertTime(order.dateCreated));
                    view_container.addView(rootView3_1);

                    break;
                case 4:
                case 5:
                    ViewGroup rootView4_0 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder4_0 = new ViewHolder(rootView4_0);
                    setCommonAttribute(holder4_0, true);
                    holder4_0.order_state_icon.setImageResource(R.drawable.order_state_delivery);
                    holder4_0.order_state_title.setText("卖家已发货");
                    holder4_0.order_state_description.setText("尊敬的客户，您的订单商品已发货，请前往网点自提或确认收货");
                    holder4_0.order_state_time.setText(DateFormatUtils.convertTime(order.datePendingDeliver));
                    view_container.addView(rootView4_0);

                    ViewGroup rootView4_1 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder4_1 = new ViewHolder(rootView4_1);
                    setCommonAttribute(holder4_1, false);
                    holder4_1.order_state_icon.setImageResource(R.drawable.order_state_payment);
                    holder4_1.order_state_title.setText("支付成功");
                    holder4_1.order_state_time.setText(DateFormatUtils.convertTime(order.datePaid));
                    view_container.addView(rootView4_1);

                    ViewGroup rootView4_2 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder4_2 = new ViewHolder(rootView4_2);
                    setCommonAttribute(holder4_2, false);
                    holder4_2.dividing_line.setVisibility(View.INVISIBLE);
                    holder4_2.order_state_bottom_divide.setVisibility(View.INVISIBLE);
                    holder4_2.order_state_icon.setImageResource(R.drawable.order_state_submit);
                    holder4_2.order_state_title.setText("订单已提交");
                    holder4_2.order_state_time.setText(DateFormatUtils.convertTime(order.dateCreated));
                    view_container.addView(rootView4_2);


                    break;
                case 6:
                    ViewGroup rootView5_0 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder5_0 = new ViewHolder(rootView5_0);
                    setCommonAttribute(holder5_0, true);
                    holder5_0.order_state_icon.setImageResource(R.drawable.order_state_finished);
                    holder5_0.order_state_title.setText("订单完成");
                    holder5_0.order_state_description.setText("尊敬的客户，您的订单商品已完成收货，如有问题可拨打客服电话联系我们");
                    holder5_0.order_state_time.setText(DateFormatUtils.convertTime(order.dateCompleted));
                    view_container.addView(rootView5_0);

                    ViewGroup rootView5_1 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder5_1 = new ViewHolder(rootView5_1);
                    setCommonAttribute(holder5_1, false);
                    holder5_1.order_state_icon.setImageResource(R.drawable.order_state_delivery);
                    holder5_1.order_state_title.setText("卖家已发货");
                    holder5_1.order_state_time.setText(DateFormatUtils.convertTime(order.datePendingDeliver));
                    view_container.addView(rootView5_1);

                    ViewGroup rootView5_2 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder5_2 = new ViewHolder(rootView5_2);
                    setCommonAttribute(holder5_2, false);
                    holder5_2.order_state_icon.setImageResource(R.drawable.order_state_payment);
                    holder5_2.order_state_title.setText("支付成功");
                    holder5_2.order_state_time.setText(DateFormatUtils.convertTime(order.datePaid));
                    view_container.addView(rootView5_2);

                    ViewGroup rootView5_3 = (ViewGroup) getLayoutInflater().inflate(R.layout.item_order_detail_layout, null);
                    ViewHolder holder5_3 = new ViewHolder(rootView5_3);
                    setCommonAttribute(holder5_3, false);
                    holder5_3.order_state_bottom_divide.setVisibility(View.INVISIBLE);
                    holder5_3.dividing_line.setVisibility(View.INVISIBLE);
                    holder5_3.order_state_icon.setImageResource(R.drawable.order_state_submit);
                    holder5_3.order_state_title.setText("订单已提交");
                    holder5_3.order_state_time.setText(DateFormatUtils.convertTime(order.dateCreated));
                    view_container.addView(rootView5_3);


                    break;


            }


        }


    }


    public void setCommonAttribute(ViewHolder holder, boolean isGreen) {

        if (isGreen) {
            holder.order_state_time.setTextColor(getResources().getColor(R.color.green));
            holder.order_state_title.setTextColor(getResources().getColor(R.color.green));
            holder.order_state_description.setTextColor(getResources().getColor(R.color.green));
            holder.order_state_top_divide.setVisibility(View.INVISIBLE);
            holder.order_state_bottom_divide.setVisibility(View.VISIBLE);
            holder.order_state_icon.setBackgroundResource(R.drawable.circle_green_bg);
            holder.order_state_description.setVisibility(View.VISIBLE);
        } else {
            holder.order_state_time.setTextColor(getResources().getColor(R.color.deep_gray));
            holder.order_state_title.setTextColor(getResources().getColor(R.color.deep_gray));
            holder.order_state_description.setTextColor(getResources().getColor(R.color.deep_gray));
            holder.order_state_top_divide.setVisibility(View.VISIBLE);
            holder.order_state_bottom_divide.setVisibility(View.VISIBLE);
            holder.order_state_icon.setBackgroundResource(R.drawable.circle_deep_gray_bg);
            holder.order_state_description.setVisibility(View.GONE);
        }

    }


    class ViewHolder {
        private ImageView order_state_icon;
        private TextView dividing_line, order_state_top_divide, order_state_bottom_divide, order_state_title, order_state_time, order_state_description;


        ViewHolder(View rootView) {
            dividing_line = (TextView) rootView.findViewById(R.id.dividing_line);
            order_state_icon = (ImageView) rootView.findViewById(R.id.order_state_icon);
            order_state_top_divide = (TextView) rootView.findViewById(R.id.order_state_top_divide);
            order_state_bottom_divide = (TextView) rootView.findViewById(R.id.order_state_bottom_divide);
            order_state_title = (TextView) rootView.findViewById(R.id.order_state_title);
            order_state_time = (TextView) rootView.findViewById(R.id.order_state_time);
            order_state_description = (TextView) rootView.findViewById(R.id.order_state_description);
        }
    }


    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {

    }

}
