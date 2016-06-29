/**
 *
 */
package com.ksfc.newfarmer.http.beans;

import java.util.List;

import com.ksfc.newfarmer.http.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：GetGoodsData 类描述： 创建人：尚前琛 创建时间：2015-6-29 下午7:07:10 修改备注：
 */
public class GetGoodsData extends ResponseResult {
	public GoodsDatas datas;

	public static class GoodsDatas {
		public int total;
		public List<SingleGood> rows;
	}

	public static class SingleGood {
		public String thumbnail;
		public String imgUrl;
		public String goodsId;
		public String allowScore;
		public String unitPrice;
		public String originalPrice;
		public String goodsSellCount;
		public String awardPoint;
		public String goodsSort;
		public String goodsName;
		public String goodsGreatCount;
		public String brandName;
		public String brandId;
		public int orderNum;// 添加购物车时的数量
		public String model;
		public boolean presale;
	}
}
