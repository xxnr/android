package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class AboutUsResult extends ResponseResult {

	public Datas datas;
	public String locationUserId;

	public static class Datas {
		public String total;

		public List<RowsInfos> rows;

	}

	public static class RowsInfos {
		public String id;
		public String value;
		public String lable;
		public String key;

	}

}
