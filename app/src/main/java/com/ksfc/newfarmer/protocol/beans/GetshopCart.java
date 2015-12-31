/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：GetshopCart 类描述： 创建人：尚前琛 创建时间：2015-7-3 下午9:38:48 修改备注：
 */
public class GetshopCart extends ResponseResult {
	public ShopcartData datas;

	public static class ShopcartData {
		public int total;
		public String shopCartId;
		public int DiscountPrice;
		public String locationUserId;
		public long totalPrice;
		public List<shopCart> rows;
		public int totalCount;
	}

	public static class shopCart {
		public String brandName;
		public List<Goods> goodsList;
	}

	public static class Goods {
		public String goodsId;
		public String goodsName;
		public String goodsCount;
		public String imgUrl;
		public String originalPrice;
		public String unitPrice;
		public String point;
		public String deposit;
	}
}
