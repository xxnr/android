/**
 * 
 */
package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：SureOrder 类描述： 创建人：尚前琛 创建时间：2015-7-4 上午5:32:28 修改备注：
 */
public class SureOrder extends ResponseResult {
	public Datas datas;

	public static class Datas {
		public String total;
		public String locationUserId;
		public Order rows;
	}

	public static class Order {
		public String id;
		public String orderNo;
		public String userId;
		public String buildingUserId;
		public String recipientName;
		public String recipientPhone;
		public String shopCartId;
		public String deliveryTime;
		public long dataSubmit;
		public double totalPrice;
		public String invoiceId;
		public String payType;
		public String privilegeId;
		public String redPacketMemberId;
		public String remarks;
		public List<OrderSubList> orderSubList;
	}

	public static class OrderSubList {
		public String goodsId;
		public String unitPrice;
		public String goodsName;
		public String point;
		public String remarks;
		public String originalPrice;
		public String habitatId;
		public String imgs;
		public String orderSubType;
		public int goodsCount;
	}
}
