/**
 *
 */
package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer63 类名称：WaitingPay 类描述： 创建人：王蕾 创建时间：2015-7-3 下午11:39:47 修改备注：
 */
@SuppressWarnings("serial")
public class WaitingPay extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public String total;
		public List<Orders> rows;
	}

	public static class Orders implements Serializable {

		public String orderNo;// 订单号
		public String orderId;// 订单ID
		public String totalPrice;// 订单金额
		public String deposit;
		public int typeValue;// 状态值
		public String typeLable;// 订单状态说明
		public int payType;// 支付方式
		public List<Product> products;//商品列表
	}
	public static class Product implements Serializable{

		public String category;// 类别 化肥 汽车
		public String count;// 数量
		public String thumbnail;// 商品图片
		public String name;//商品名称
		public float deposit;// 商品定金
		public float price;// 商品价格
		public String id;// 商品id

	}

}
