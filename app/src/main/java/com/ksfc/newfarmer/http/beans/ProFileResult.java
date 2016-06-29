package com.ksfc.newfarmer.http.beans;

import java.util.List;

import com.ksfc.newfarmer.http.ResponseResult;

public class ProFileResult extends ResponseResult {

	public Datas datas;

	public static class Datas {
		public int total;
		public List<BandInfo> rows;
	}

	public static class BandInfo {
		public String id;
		public String value;
		public String lable;
		public String key;
	}
}
