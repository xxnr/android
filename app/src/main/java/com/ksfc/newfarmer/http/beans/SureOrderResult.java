package com.ksfc.newfarmer.http.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.http.ResponseResult;

public class SureOrderResult extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public String total;

		public Rows rows;
	}

	public static class Rows implements Serializable {
		public String id;
		public String orderNo;
		public String userId;
		public String buildingUserId;
		public String recipientName;
		public String recipientPhone;
		public String shopCartId;
		public String deliveryTime;
		public String dataSubmit;
		public String totalPrice;
		public String deposit;
		public String invoiceId;
		public String payType;
		public String privilegeId;
		public String redPacketMemberId;
		public String remarks;

		public List<OrderSub> orderSubList;
	}

	public static class OrderSub implements Serializable {
		public String goodsId;
		public String goodsName;
		public String unitPrice;
		public String point;
		public String originalPrice;
		public String habitatId;
		public String imgs;
		public String orderSubType;
		public String proType;
		public String goodsCount;
	}

}
