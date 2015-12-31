/**
 * 
 */
package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：GetGoodsDetail 类描述： 创建人：尚前琛 创建时间：2015-7-1 上午10:25:39 修改备注：
 */
public class GetGoodsDetail extends ResponseResult {
	public GoodsDetail datas;

	public static class GoodsDetail {
		public String imgUrl;
		public String thumbnail;
		public String originalUrl;
		public String __v;
		public String id;
		public String reference;
		public String category;
		public String name;
		public String price;
		public String istop;
		public String goodsGreatCount;
		public String linker;
		public String linker_category;
		public String datecreated;
		public String positiveRating;
		public String discount;
		public String stars;
		public String brandName;
		public String deposit;
		public String description;
		public String model;
		public String engine;
		public String gearbox;
		public String level;
		public boolean presale;
		public String _id;
		public String app_body_url;
		public String app_standard_url;
		public String app_support_url;
	}


}
