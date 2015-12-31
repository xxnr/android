package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class MyOrderDetailResult extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public String total;
		public String locationUserId;

		public Rows rows;
	}

	public static class Rows {
		public String id;// 订单ID
		public String orderNo;// 订单编码
		public String orderSubNo;// 子订单编码
		public String totalPrice;// 总金额
		public String totalCount;// 总数
		public String totalConsumePoint;
		public String dataSubmit;// 订单提交时间
		public int orderType;
		public String recipientName;// 收货人名称
		public String recipientPhone;// 收货人手机号
		public String address;// 地址
		public String remarks;
		public String deliveryTime;// 期望收货日期
		public String deposit;
		public int payType;//支付类型

		public List<OrderGood> orderGoodsList;
	}

	public static class OrderGood implements Serializable {
		public String imgs;
		public String goodsId;
		public String orderSubType;
		public String originalPrice;
		public float unitPrice;
		public String goodsCount;
		public String goodsName;
		public String orderSubNo;
		public float deposit;

	}

}
