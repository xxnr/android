/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：ChangeNum 类描述： 创建人：尚前琛 创建时间：2015-7-4 上午2:46:28 修改备注：
 */
public class ChangeNum extends ResponseResult {
	public ChangeData datas;

	public static class ChangeData {
		public String total;
		public String locationUserId;
		public backData rows;
	}

	public static class backData {
		public int total;
		public String shopCartId;
		public int DiscountPrice;
		public int totalPrice;
		public List<BackGood> rows;
	}

	public static class BackGood {
		public String brandName;
		public List<Good> rows;
	}

	public static class Good {
		public String goodsId;
		public String goodsName;
		public String goodsCount;
		public String imgUrl;
		public String originalPrice;
		public String unitPrice;
		public String point;
	}
}
