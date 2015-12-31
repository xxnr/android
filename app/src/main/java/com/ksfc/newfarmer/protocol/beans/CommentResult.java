package com.ksfc.newfarmer.protocol.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class CommentResult extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public String total;
		public String locationUserId;

		public List<PjRows> rows;
	}

	public static class PjRows {

		public String imgUrl;
		public String content;
		public String goodsUnitPrice;
		public String starValue;
		public String goodsName;
		public String goodsOriginalPrice;
	}

}
