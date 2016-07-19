package com.ksfc.newfarmer.beans;

import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

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
