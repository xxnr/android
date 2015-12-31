package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

@SuppressWarnings("serial")
public class EvaluateList extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public String locationUserId;
		public String total;
		public List<Rows> rows;
	}

	public static class Rows implements Serializable {
		public String id;
		public String imgUrl;
		public String content;
		public String goodsName;
		public String goodsId;
	}
}
