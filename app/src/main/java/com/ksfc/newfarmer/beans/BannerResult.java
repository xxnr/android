package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 广告列表
 * 
 * @author Bruce.Wang
 * 
 */
public class BannerResult extends ResponseResult {

	public List<Banner> data;

	public static class Banner {

		public int id;
		public int shopid;
		public int shop_type;
		public String shop_title;
		public String shop_thumb;
		public String shop_url;
		public double shop_express;
		public double shop_price_max;
		public double shop_price_discount;
		public String shop_open_time;
		public int imgUrl;
	}
}
